package com.vsevolodganin.clicktrack.player

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.audiofx.AutomaticGainControl
import android.net.Uri
import androidx.core.content.getSystemService
import com.vsevolodganin.clicktrack.R
import com.vsevolodganin.clicktrack.di.component.PlayerServiceScoped
import com.vsevolodganin.clicktrack.di.module.ApplicationContext
import com.vsevolodganin.clicktrack.di.module.MainDispatcher
import com.vsevolodganin.clicktrack.di.module.PlayerDispatcher
import com.vsevolodganin.clicktrack.lib.ClickSoundSource
import com.vsevolodganin.clicktrack.lib.Cue
import com.vsevolodganin.clicktrack.lib.interval
import com.vsevolodganin.clicktrack.model.ClickTrackWithId
import com.vsevolodganin.clicktrack.player.PlayerImpl.Const.CLICK_TIME_EPSILON
import com.vsevolodganin.clicktrack.state.PlaybackState
import com.vsevolodganin.clicktrack.utils.coroutine.MutableNonConflatedStateFlow
import com.vsevolodganin.clicktrack.utils.coroutine.delay
import com.vsevolodganin.clicktrack.utils.time.rem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.minutes

interface Player {
    suspend fun start(clickTrack: ClickTrackWithId, atProgress: Float = 0f)
    suspend fun pause()
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
    private var pausedState = MutableNonConflatedStateFlow(false)

    override suspend fun start(clickTrack: ClickTrackWithId, atProgress: Float): Unit = withContext(mainDispatcher) {
        val playback = playbackState.value
        val startAtTime = if (playback?.clickTrack?.id == clickTrack.id) {
            clickTrack.value.durationInTime * atProgress.toDouble()
        } else {
            Duration.ZERO
        }

        playerJob?.cancel()
        playerJob = launch(playerDispatcher) {
            pausedState.setValue(false)
            startImpl(clickTrack, startAtTime)
            stop()
        }
    }

    override suspend fun pause() {
        pausedState.setValue(true)
    }

    override suspend fun stop(): Unit = withContext(mainDispatcher) {
        playerJob?.cancel()
        playerJob = null
        playbackState.setValue(null)
    }

    override fun playbackState(): Flow<PlaybackState?> = playbackState.distinctUntilChanged()

    private suspend fun startImpl(clickTrack: ClickTrackWithId, startAt: Duration) = coroutineScope {
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

                    startImpl(
                        strongBeatMediaPlayer = strongBeatMediaPlayer,
                        weakBeatMediaPlayer = weakBeatMediaPlayer,
                        cue = cue,
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

    private suspend fun startImpl(
        strongBeatMediaPlayer: MediaPlayer,
        weakBeatMediaPlayer: MediaPlayer,
        cue: Cue,
        startAt: Duration,
        onBeatPlayed: suspend (beatTimestamp: Duration) -> Unit,
    ) {
        val totalDuration = cue.durationAsTime

        if (startAt >= totalDuration) {
            return
        }

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
            if (pausedState.value) {
                pausedState.filter { false }.take(1).collect()
            }

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
