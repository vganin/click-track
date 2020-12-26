package net.ganin.vsevolod.clicktrack.player

import android.content.Context
import android.media.MediaPlayer
import android.media.audiofx.AutomaticGainControl
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
import net.ganin.vsevolod.clicktrack.lib.CueWithDuration
import net.ganin.vsevolod.clicktrack.lib.SerializableDuration
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
        }
    }

    override suspend fun stop(): Unit = withContext(mainDispatcher) {
        playerJob?.cancel()
        playerJob = null
        playbackState.setValue(null)
    }

    override fun playbackState(): Flow<PlaybackState?> = playbackState

    private suspend fun playImpl(clickTrack: ClickTrackWithId) = coroutineScope {
        val mediaPlayer = MediaPlayer.create(context, R.raw.click)
        val agc = mediaPlayer.tryAttachAgc()
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
                    mediaPlayer.playImpl(cue, onBeatPlayed = { beatTimestamp ->
                        playbackState.setValue(
                            PlaybackState(
                                clickTrack = clickTrack,
                                playbackStamp = PlaybackStamp(
                                    timestamp = SerializableDuration(playedFor + beatTimestamp),
                                    duration = SerializableDuration(cue.cue.bpm.interval)
                                )
                            )
                        )
                    })
                    playedFor += cue.durationInTime
                }
            } while (clickTrack.value.loop && coroutineContext.isActive)
        } finally {
            agc?.release()
            mediaPlayer.release()
        }
    }

    private suspend fun MediaPlayer.playImpl(
        cueWithDuration: CueWithDuration,
        onBeatPlayed: suspend (beatTimestamp: Duration) -> Unit,
    ) {
        val cue = cueWithDuration.cue
        val delay = cue.bpm.interval
        val duration = cueWithDuration.durationInTime

        var playedFor = Duration.ZERO
        while ((duration - playedFor) > CLICK_MINIMAL_INTERVAL) {
            start()
            onBeatPlayed(playedFor)
            playedFor += delay
            delay(delay)
        }
    }

    private fun MediaPlayer.tryAttachAgc(): AutomaticGainControl? {
        return AutomaticGainControl.create(audioSessionId)
    }

    private object Const {
        val CLICK_MINIMAL_INTERVAL = 1.minutes / 1000 // Max. 1000 bpm
    }
}
