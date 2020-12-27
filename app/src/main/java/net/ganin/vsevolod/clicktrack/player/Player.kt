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
import net.ganin.vsevolod.clicktrack.lib.SerializableDuration
import net.ganin.vsevolod.clicktrack.lib.durationAsTime
import net.ganin.vsevolod.clicktrack.lib.interval
import net.ganin.vsevolod.clicktrack.model.ClickTrackWithId
import net.ganin.vsevolod.clicktrack.player.PlayerImpl.Const.CLICK_MINIMAL_INTERVAL
import net.ganin.vsevolod.clicktrack.state.PlaybackStamp
import net.ganin.vsevolod.clicktrack.state.PlaybackState
import net.ganin.vsevolod.clicktrack.utils.coroutine.MutableNonConflatedStateFlow
import net.ganin.vsevolod.clicktrack.utils.coroutine.delay
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.minutes

interface Player {
    suspend fun play(clickTrack: ClickTrackWithId)
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

    override suspend fun play(clickTrack: ClickTrackWithId): Unit = withContext(mainDispatcher) {
        if (playbackState.value?.clickTrack?.id == clickTrack.id) {
            playerJob?.cancel()
        } else {
            stop()
        }

        playerJob = launch(playerDispatcher) {
            playImpl(clickTrack)
            stop()
        }
    }

    override suspend fun stop(): Unit = withContext(mainDispatcher) {
        playerJob?.cancel()
        playerJob = null
        playbackState.setValue(null)
    }

    override fun playbackState(): Flow<PlaybackState?> = playbackState

    private suspend fun playImpl(clickTrack: ClickTrackWithId) = coroutineScope {
        val strongBeatMediaPlayer = clickTrack.value.sounds.strongBeat.createMediaPlayer(context, R.raw.strong)
        val weakBeatMediaPlayer = clickTrack.value.sounds.weakBeat.createMediaPlayer(context, R.raw.weak)
        val agc = AutomaticGainControl.create(audioSessionId)
        try {
            do {
                playbackState.setValue(
                    PlaybackState(
                        clickTrack = clickTrack,
                        playbackStamp = PlaybackStamp(
                            timestamp = SerializableDuration(Duration.ZERO),
                            duration = SerializableDuration(Duration.ZERO)
                        )
                    )
                )
                var playedFor = Duration.ZERO
                for (cue in clickTrack.value.cues) {
                    playImpl(
                        strongBeatMediaPlayer,
                        weakBeatMediaPlayer,
                        cue,
                        onBeatPlayed = { beatTimestamp ->
                            playbackState.setValue(
                                PlaybackState(
                                    clickTrack = clickTrack,
                                    playbackStamp = PlaybackStamp(
                                        timestamp = SerializableDuration(playedFor + beatTimestamp),
                                        duration = SerializableDuration(cue.cue.bpm.interval)
                                    )
                                )
                            )
                        }
                    )
                    playedFor += cue.durationAsTime
                }
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
        onBeatPlayed: suspend (beatTimestamp: Duration) -> Unit,
    ) {
        val cue = cueWithDuration.cue
        val timeSignature = cue.timeSignature
        val delay = cue.bpm.interval
        val duration = cueWithDuration.durationAsTime

        var playedFor = Duration.ZERO
        var beatIndex = 0
        while ((duration - playedFor) > CLICK_MINIMAL_INTERVAL) {
            if (beatIndex == 0) {
                strongBeatMediaPlayer.start()
            } else {
                weakBeatMediaPlayer.start()
            }

            onBeatPlayed(playedFor)

            playedFor += delay
            beatIndex = if (beatIndex + 1 >= timeSignature.noteCount) 0 else beatIndex + 1

            delay(delay)
        }
    }

    private fun ClickSoundSource.createMediaPlayer(context: Context, builtinSoundResId: Int): MediaPlayer {
        return when (this) {
            ClickSoundSource.Builtin -> MediaPlayer.create(context, builtinSoundResId, null, audioSessionId)
            is ClickSoundSource.Uri -> MediaPlayer.create(context, Uri.parse(value), null, null, audioSessionId)
        }
    }

    private object Const {
        val CLICK_MINIMAL_INTERVAL = 1.minutes / 1000 // Max. 1000 bpm
    }
}
