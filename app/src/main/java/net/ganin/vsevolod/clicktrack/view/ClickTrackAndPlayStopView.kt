package net.ganin.vsevolod.clicktrack.view

import androidx.compose.foundation.layout.ConstraintLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import net.ganin.vsevolod.clicktrack.lib.ClickTrack

@Composable
fun ClickTrackAndPlayStopView(
    clickTrack: ClickTrack,
    modifier: Modifier = Modifier,
    isPlayingState: MutableState<Boolean> = remember { mutableStateOf(false) },
    onPlayToggle: (Boolean) -> Unit = { isPlayingState.value = !isPlayingState.value }
) {
    ConstraintLayout(modifier) {
        ClickTrackView(clickTrack)
        PlayStopView(
            modifier = Modifier.constrainAs(createRef()) {
                centerHorizontallyTo(parent)
                bottom.linkTo(parent.bottom, margin = 16.dp)
            },
            isPlayingState = isPlayingState,
            onPlayToggle = onPlayToggle
        )
    }
}

@Preview
@Composable
fun PreviewClickTrackAndPlayView() {
    ClickTrackAndPlayStopView(PREVIEW_CLICK_TRACK_1)
}
