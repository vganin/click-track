package net.ganin.vsevolod.clicktrack.view.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.ui.tooling.preview.Preview
import net.ganin.vsevolod.clicktrack.lib.ClickTrack
import net.ganin.vsevolod.clicktrack.lib.Cue
import net.ganin.vsevolod.clicktrack.lib.CueDuration
import net.ganin.vsevolod.clicktrack.lib.CueWithDuration
import net.ganin.vsevolod.clicktrack.lib.SerializableDuration
import net.ganin.vsevolod.clicktrack.lib.TimeSignature
import net.ganin.vsevolod.clicktrack.lib.bpm
import kotlin.time.minutes

@Composable
fun EditClickTrackScreenView(
    state: MutableState<ClickTrack>,
    modifier: Modifier = Modifier
) {

}

@Preview
@Composable
fun PreviewEditClickTrackScreenView() {
    EditClickTrackScreenView(
        state = mutableStateOf(
            ClickTrack(
                cues = listOf(
                    CueWithDuration(
                        cue = Cue(
                            bpm = 60.bpm,
                            timeSignature = TimeSignature(3, 4)
                        ),
                        duration = CueDuration.Beats(4),
                    ),
                    CueWithDuration(
                        cue = Cue(
                            bpm = 120.bpm,
                            timeSignature = TimeSignature(5, 4)
                        ),
                        duration = CueDuration.Time(SerializableDuration(1.minutes)),
                    ),
                ),
                loop = true,
            )
        )
    )
}
