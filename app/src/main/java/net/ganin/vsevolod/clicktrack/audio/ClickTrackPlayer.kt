package net.ganin.vsevolod.clicktrack.audio

import android.content.Context
import android.media.MediaPlayer
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import net.ganin.vsevolod.clicktrack.R
import net.ganin.vsevolod.clicktrack.lib.ClickTrack
import net.ganin.vsevolod.clicktrack.lib.CueWithDuration
import kotlin.coroutines.coroutineContext
import kotlin.time.minutes

class ClickTrackPlayer(private val context: Context) {

    suspend fun play(clickTrack: ClickTrack) {
        val mediaPlayer = MediaPlayer.create(context, R.raw.click)
        try {
            do {
                clickTrack.cues.forEach { mediaPlayer.play(it) }
            } while (clickTrack.loop && coroutineContext.isActive)
        } finally {
            mediaPlayer.release()
        }
    }

    private suspend fun MediaPlayer.play(cueWithDuration: CueWithDuration) {
        val delay = 1.minutes / cueWithDuration.cue.bpm
        var leftToPlay = cueWithDuration.durationInTime
        while (leftToPlay.toLongMilliseconds() > 1) {
            start()
            leftToPlay -= delay
            // FIXME: Fix NoSuchMethod and use override with kotlin.time directly
            delay(delay.toLongMilliseconds())
        }
    }
}