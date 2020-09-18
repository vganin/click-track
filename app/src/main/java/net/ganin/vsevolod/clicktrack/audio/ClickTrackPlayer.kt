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
import net.ganin.vsevolod.clicktrack.audio.ClickTrackPlayer.Const.PLAYBACK_UPDATE_RATE
import net.ganin.vsevolod.clicktrack.lib.ClickTrack
import net.ganin.vsevolod.clicktrack.lib.CueWithDuration
import net.ganin.vsevolod.clicktrack.redux.Dispatch
import net.ganin.vsevolod.clicktrack.state.actions.StopPlay
import net.ganin.vsevolod.clicktrack.state.actions.UpdatePlaybackTimestamp
import net.ganin.vsevolod.clicktrack.utils.coroutine.delay
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext
import kotlin.time.Duration
import kotlin.time.milliseconds
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
                val updaterJob = launch { updatePlaybackTimestamps() }
                clickTrack.cues.forEach { mediaPlayer.play(it) }
                updaterJob.cancel()
            } while (clickTrack.loop && playerCoroutineContext.isActive)
        } finally {
            agc?.release()
            mediaPlayer.release()
        }
    }

    private suspend fun MediaPlayer.play(cueWithDuration: CueWithDuration) {
        val delay = 1.minutes / cueWithDuration.cue.bpm
        var leftToPlay = cueWithDuration.durationInTime
        while (leftToPlay > CLICK_MINIMAL_INTERVAL) {
            start()
            leftToPlay -= delay
            delay(delay)
        }
    }

    private fun MediaPlayer.tryAttachAgc(): AutomaticGainControl? {
        return AutomaticGainControl.create(audioSessionId)
    }

    private suspend fun updatePlaybackTimestamps() {
        var playbackTimestamp = Duration.ZERO
        do {
            dispatch(UpdatePlaybackTimestamp(playbackTimestamp))
            delay(PLAYBACK_UPDATE_RATE)
            playbackTimestamp += PLAYBACK_UPDATE_RATE
        } while (coroutineContext.isActive)
    }

    private object Const {
        val CLICK_MINIMAL_INTERVAL = 1.minutes / 1000 // Max. 1000 bpm
        val PLAYBACK_UPDATE_RATE = 16.milliseconds
    }
}
