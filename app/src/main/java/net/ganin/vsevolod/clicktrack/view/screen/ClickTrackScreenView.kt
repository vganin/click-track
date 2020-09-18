package net.ganin.vsevolod.clicktrack.view.screen

import androidx.compose.foundation.layout.ConstraintLayout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import net.ganin.vsevolod.clicktrack.redux.Action
import net.ganin.vsevolod.clicktrack.state.ClickTrackScreenState
import net.ganin.vsevolod.clicktrack.state.actions.TogglePlay
import net.ganin.vsevolod.clicktrack.view.preview.PREVIEW_CLICK_TRACK_1
import net.ganin.vsevolod.clicktrack.view.widget.ClickTrackView
import net.ganin.vsevolod.clicktrack.view.widget.ClickTrackViewState
import net.ganin.vsevolod.clicktrack.view.widget.PlayStopView

@Composable
fun ClickTrackScreenView(
    state: ClickTrackScreenState,
    dispatch: (Action) -> Unit = {}
) {
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        ClickTrackView(
            state = ClickTrackViewState(
                clickTrack = state.clickTrack,
                drawTextMarks = true,
                playbackTimestamp = state.playbackStamp,
            ),
            modifier = Modifier.fillMaxSize()
        )
        PlayStopView(
            modifier = Modifier.constrainAs(createRef()) {
                centerHorizontallyTo(parent)
                bottom.linkTo(parent.bottom, margin = 16.dp)
            },
            isPlaying = state.isPlaying,
            onToggle = { dispatch(TogglePlay) }
        )
    }
}

@Preview
@Composable
fun PreviewClickTrackScreenView() {
    ClickTrackScreenView(
        ClickTrackScreenState(
            clickTrack = PREVIEW_CLICK_TRACK_1,
            isPlaying = false,
            playbackStamp = null
        )
    )
}
