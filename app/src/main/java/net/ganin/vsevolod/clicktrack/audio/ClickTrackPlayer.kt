package net.ganin.vsevolod.clicktrack.audio

import android.content.Context
import android.media.MediaPlayer
import android.media.audiofx.AutomaticGainControl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import net.ganin.vsevolod.clicktrack.R
import net.ganin.vsevolod.clicktrack.audio.ClickTrackPlayer.Const.CLICK_MINIMAL_INTERVAL
import net.ganin.vsevolod.clicktrack.di.component.ViewModelScoped
import net.ganin.vsevolod.clicktrack.di.module.ApplicationContext
import net.ganin.vsevolod.clicktrack.di.module.MainDispatcher
import net.ganin.vsevolod.clicktrack.di.module.PlayerDispatcher
import net.ganin.vsevolod.clicktrack.lib.ClickTrack
import net.ganin.vsevolod.clicktrack.lib.CueWithDuration
import net.ganin.vsevolod.clicktrack.lib.SerializableDuration
import net.ganin.vsevolod.clicktrack.lib.interval
import net.ganin.vsevolod.clicktrack.redux.Action
import net.ganin.vsevolod.clicktrack.redux.Store
import net.ganin.vsevolod.clicktrack.state.AppState
import net.ganin.vsevolod.clicktrack.state.PlaybackStamp
import net.ganin.vsevolod.clicktrack.state.actions.ResetPlaybackStamp
import net.ganin.vsevolod.clicktrack.state.actions.StopPlay
import net.ganin.vsevolod.clicktrack.state.actions.UpdatePlaybackStamp
import net.ganin.vsevolod.clicktrack.utils.coroutine.delay
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.minutes

@ViewModelScoped
class ClickTrackPlayer @Inject constructor(
    @ApplicationContext private val context: Context,
    @MainDispatcher private val mainScope: CoroutineScope,
    @PlayerDispatcher private val playerScope: CoroutineScope,
    private val store: Store<AppState>
) {
    private var playerJob: Job? = null

    fun play(clickTrack: ClickTrack): Job = mainScope.launch {
        playerJob?.cancel()
        playerJob = launch(playerScope.coroutineContext) {
            try {
                suspendPlay(clickTrack)
            } finally {
                dispatch(StopPlay)
            }
        }
    }

    fun stop() = mainScope.launch {
        playerJob?.cancel()
    }

    private suspend fun suspendPlay(clickTrack: ClickTrack) = coroutineScope {
        val mediaPlayer = MediaPlayer.create(context, R.raw.click)
        val agc = mediaPlayer.tryAttachAgc()
        try {
            do {
                dispatch(ResetPlaybackStamp)
                var playedFor = Duration.ZERO
                for (cue in clickTrack.cues) {
                    mediaPlayer.play(cue) {
                        val playbackStamp = PlaybackStamp(
                            timestamp = SerializableDuration(playedFor + it),
                            correspondingCue = cue.cue
                        )
                        dispatch(UpdatePlaybackStamp(playbackStamp))
                    }
                    playedFor += cue.durationInTime
                }
            } while (clickTrack.loop && coroutineContext.isActive)
        } finally {
            agc?.release()
            mediaPlayer.release()
        }
    }

    private suspend fun MediaPlayer.play(
        cueWithDuration: CueWithDuration,
        onBeatPlayed: (playedFor: Duration) -> Unit
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

    private fun dispatch(action: Action) = store.dispatch(action)

    private object Const {
        val CLICK_MINIMAL_INTERVAL = 1.minutes / 1000 // Max. 1000 bpm
    }
}
