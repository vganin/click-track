package net.ganin.vsevolod.clicktrack.audio

import android.content.Context
import android.media.MediaPlayer
import android.media.audiofx.AutomaticGainControl
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import net.ganin.vsevolod.clicktrack.R
import net.ganin.vsevolod.clicktrack.audio.ClickTrackPlayer.Const.CLICK_MINIMAL_INTERVAL
import net.ganin.vsevolod.clicktrack.lib.ClickTrack
import net.ganin.vsevolod.clicktrack.lib.CueWithDuration
import kotlin.coroutines.coroutineContext
import kotlin.time.minutes

class ClickTrackPlayer(private val context: Context) {

    suspend fun play(clickTrack: ClickTrack) {
        val mediaPlayer = MediaPlayer.create(context, R.raw.click)
        val agc = mediaPlayer.tryAttachAgc()
        try {
            do {
                clickTrack.cues.forEach { mediaPlayer.play(it) }
            } while (clickTrack.loop && coroutineContext.isActive)
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
            // FIXME: Fix NoSuchMethod and use override with kotlin.time directly
            delay(delay.toLongMilliseconds())
        }
    }

    private fun MediaPlayer.tryAttachAgc(): AutomaticGainControl? {
        return AutomaticGainControl.create(audioSessionId)
    }

    private object Const {
        val CLICK_MINIMAL_INTERVAL = 1.minutes / 1000 // Max. 1000 bpm
    }
}
