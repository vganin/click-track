package net.ganin.vsevolod.clicktrack.view

import androidx.compose.runtime.Composable
import androidx.ui.tooling.preview.Preview
import net.ganin.vsevolod.clicktrack.lib.ClickTrack
import net.ganin.vsevolod.clicktrack.lib.Cue
import net.ganin.vsevolod.clicktrack.lib.CueWithTimestamp
import net.ganin.vsevolod.clicktrack.lib.TimeSignature

@Preview
@Composable
fun ContentView() {
    val testClickTrack = ClickTrack(
        duration = 100f,
        initialCue = Cue(
            bpm = 100,
            timeSignature = TimeSignature(4, 4)
        ),
        followingCues = listOf(
            CueWithTimestamp(
                timestamp = 50f,
                cue = Cue(
                    bpm = 125,
                    timeSignature = TimeSignature(3, 4)
                )
            ),
            CueWithTimestamp(
                timestamp = 75f,
                cue = Cue(
                    bpm = 90,
                    timeSignature = TimeSignature(4, 4)
                )
            )
        )
    )
    ClickTrackView(clickTrack = testClickTrack)
}