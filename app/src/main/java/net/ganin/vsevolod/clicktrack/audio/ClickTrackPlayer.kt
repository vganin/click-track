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
import net.ganin.vsevolod.clicktrack.lib.ClickTrack
import net.ganin.vsevolod.clicktrack.lib.CueWithDuration
import net.ganin.vsevolod.clicktrack.lib.SerializableDuration
import net.ganin.vsevolod.clicktrack.lib.interval
import net.ganin.vsevolod.clicktrack.redux.Dispatch
import net.ganin.vsevolod.clicktrack.state.PlaybackStamp
import net.ganin.vsevolod.clicktrack.state.actions.ResetPlaybackStamp
import net.ganin.vsevolod.clicktrack.state.actions.StopPlay
import net.ganin.vsevolod.clicktrack.state.actions.UpdatePlaybackStamp
import net.ganin.vsevolod.clicktrack.utils.coroutine.delay
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration
import kotlin.time.minutes

class ClickTrackPlayer(
    private val context: Context,
    private val mainCoroutineScope: CoroutineScope,
    private val playerCoroutineContext: CoroutineContext,
    private val dispatch: Dispatch
) {
    private var playerJob: Job? = null

    fun play(clickTrack: ClickTrack) = mainCoroutineScope.launch {
        playerJob?.cancel()
        playerJob = launch(playerCoroutineContext) {
            suspendPlay(clickTrack)
            dispatch(StopPlay)
        }
    }

    fun stop() = mainCoroutineScope.launch {
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

    private object Const {
        val CLICK_MINIMAL_INTERVAL = 1.minutes / 1000 // Max. 1000 bpm
    }
}
