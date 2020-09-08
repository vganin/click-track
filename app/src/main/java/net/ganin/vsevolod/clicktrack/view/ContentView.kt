package net.ganin.vsevolod.clicktrack.view

import androidx.compose.runtime.Composable
import androidx.ui.tooling.preview.Preview
import net.ganin.vsevolod.clicktrack.lib.*

@Composable
fun ContentView(clickTrack: ClickTrack) {
    ClickTrackView(clickTrack = clickTrack)
}

@Preview
@Composable
fun PreviewContentView() {
    ClickTrackView(
        clickTrack = ClickTrack(
            cues = listOf(
                CueWithDuration(
                    duration = CueDuration.Beats(4),
                    cue = Cue(
                        bpm = 100,
                        timeSignature = TimeSignature(3, 4)
                    )
                ),
                CueWithDuration(
                    duration = CueDuration.Beats(4),
                    cue = Cue(
                        bpm = 150,
                        timeSignature = TimeSignature(3, 4)
                    )
                ),
                CueWithDuration(
                    duration = CueDuration.Beats(4),
                    cue = Cue(
                        bpm = 200,
                        timeSignature = TimeSignature(4, 4)
                    )
                ),
                CueWithDuration(
                    duration = CueDuration.Beats(4),
                    cue = Cue(
                        bpm = 100,
                        timeSignature = TimeSignature(4, 4)
                    )
                ),
            ),
            loop = false
        )
    )
}