package net.ganin.vsevolod.clicktrack.player

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.audiofx.AutomaticGainControl
import android.net.Uri
import androidx.core.content.getSystemService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.ganin.vsevolod.clicktrack.R
import net.ganin.vsevolod.clicktrack.di.component.PlayerServiceScoped
import net.ganin.vsevolod.clicktrack.di.module.ApplicationContext
import net.ganin.vsevolod.clicktrack.di.module.MainDispatcher
import net.ganin.vsevolod.clicktrack.di.module.PlayerDispatcher
import net.ganin.vsevolod.clicktrack.lib.ClickSoundSource
import net.ganin.vsevolod.clicktrack.lib.CueWithDuration
import net.ganin.vsevolod.clicktrack.lib.durationAsTime
import net.ganin.vsevolod.clicktrack.lib.interval
import net.ganin.vsevolod.clicktrack.model.ClickTrackWithId
import net.ganin.vsevolod.clicktrack.player.PlayerImpl.Const.CLICK_TIME_EPSILON
import net.ganin.vsevolod.clicktrack.state.PlaybackState
import net.ganin.vsevolod.clicktrack.utils.coroutine.MutableNonConflatedStateFlow
import net.ganin.vsevolod.clicktrack.utils.coroutine.delay
import net.ganin.vsevolod.clicktrack.utils.time.rem
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.minutes

interface Player {
    suspend fun play(clickTrack: ClickTrackWithId, startAtProgress: Float = 0f)
    suspend fun stop()

    fun playbackState(): Flow<PlaybackState?>
}

@PlayerServiceScoped
class PlayerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
    @PlayerDispatcher private val playerDispatcher: CoroutineDispatcher,
) : Player {

    private val audioManager = context.getSystemService<AudioManager>()
        ?: throw RuntimeException("Failed to obtain audio service")
    private val audioSessionId = audioManager.generateAudioSessionId()

    private var playerJob: Job? = null
    private var playbackState = MutableNonConflatedStateFlow<PlaybackState?>(null)

    override suspend fun play(clickTrack: ClickTrackWithId, startAtProgress: Float) = withContext(mainDispatcher) {
        val playback = playbackState.value

        val startAtTime = if (playback?.clickTrack?.id == clickTrack.id) {
            playerJob?.cancel()
            clickTrack.value.durationInTime * startAtProgress.toDouble()
        } else {
            stop()
            Duration.ZERO
        }

        playerJob = launch(playerDispatcher) {
            playImpl(clickTrack, startAtTime)
            stop()
        }
    }

    override suspend fun stop(): Unit = withContext(mainDispatcher) {
        playerJob?.cancel()
        playerJob = null
        playbackState.setValue(null)
    }

    override fun playbackState(): Flow<PlaybackState?> = playbackState

    private suspend fun playImpl(clickTrack: ClickTrackWithId, startAt: Duration) = coroutineScope {
        val strongBeatMediaPlayer = clickTrack.value.sounds.strongBeat.createMediaPlayer(context, R.raw.strong)
        val weakBeatMediaPlayer = clickTrack.value.sounds.weakBeat.createMediaPlayer(context, R.raw.weak)
        val agc = AutomaticGainControl.create(audioSessionId)

        try {
            var iterationStartsWith = if (startAt < clickTrack.value.durationInTime) startAt else Duration.ZERO

            do {
                fun Duration.toProgress(): Float = (this / clickTrack.value.durationInTime).toFloat()

                var cueGlobalStart = Duration.ZERO

                for (cue in clickTrack.value.cues) {
                    val cueLocalStart = (iterationStartsWith - cueGlobalStart).coerceAtLeast(Duration.ZERO)

                    playImpl(
                        strongBeatMediaPlayer = strongBeatMediaPlayer,
                        weakBeatMediaPlayer = weakBeatMediaPlayer,
                        cueWithDuration = cue,
                        startAt = cueLocalStart,
                        onBeatPlayed = { beatTimestamp ->
                            playbackState.setValue(
                                PlaybackState(
                                    clickTrack = clickTrack,
                                    progress = (cueGlobalStart + beatTimestamp).toProgress(),
                                )
                            )
                        }
                    )

                    cueGlobalStart += cue.durationAsTime
                }

                iterationStartsWith = Duration.ZERO
            } while (clickTrack.value.loop && coroutineContext.isActive)
        } finally {
            agc?.release()
            strongBeatMediaPlayer.release()
            weakBeatMediaPlayer.release()
        }
    }

    private suspend fun playImpl(
        strongBeatMediaPlayer: MediaPlayer,
        weakBeatMediaPlayer: MediaPlayer,
        cueWithDuration: CueWithDuration,
        startAt: Duration,
        onBeatPlayed: suspend (beatTimestamp: Duration) -> Unit,
    ) {
        val totalDuration = cueWithDuration.durationAsTime

        if (startAt >= totalDuration) {
            return
        }

        val cue = cueWithDuration.cue
        val timeSignature = cue.timeSignature
        val beatInterval = cue.bpm.interval

        var beatIndex: Int
        var playedFor: Duration

        // Handle case when we start somewhere between beats
        if (startAt % beatInterval > Duration.ZERO) {
            val nextBeatIndex = (startAt / beatInterval).toInt() + 1
            val nextBeatTimestamp = (beatInterval * nextBeatIndex).coerceAtMost(totalDuration)
            val beatRemainingDuration = nextBeatTimestamp - startAt

            onBeatPlayed(startAt)
            delay(beatRemainingDuration)

            beatIndex = nextBeatIndex
            playedFor = nextBeatTimestamp
        } else {
            beatIndex = (startAt / beatInterval).toInt()
            playedFor = startAt
        }

        // Handle all full beats
        while ((totalDuration - playedFor) > CLICK_TIME_EPSILON) {
            if (beatIndex % timeSignature.noteCount == 0) {
                strongBeatMediaPlayer.start()
            } else {
                weakBeatMediaPlayer.start()
            }

            val beatDuration = beatInterval.coerceAtMost(totalDuration - playedFor)

            onBeatPlayed(playedFor)
            delay(beatDuration)

            ++beatIndex
            playedFor += beatDuration
        }
    }

    private fun ClickSoundSource.createMediaPlayer(context: Context, builtinSoundResId: Int): MediaPlayer {
        return when (this) {
            ClickSoundSource.Builtin -> MediaPlayer.create(context, builtinSoundResId, null, audioSessionId)
            is ClickSoundSource.Uri -> MediaPlayer.create(context, Uri.parse(value), null, null, audioSessionId)
        }
    }

    private object Const {
        val CLICK_TIME_EPSILON = 1.minutes / 1000 // Max. 1000 bpm
    }
}
