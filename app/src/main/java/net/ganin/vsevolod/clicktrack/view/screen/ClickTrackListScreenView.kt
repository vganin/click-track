package net.ganin.vsevolod.clicktrack.view.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import net.ganin.vsevolod.clicktrack.redux.Dispatch
import net.ganin.vsevolod.clicktrack.state.ClickTrackListScreenState
import net.ganin.vsevolod.clicktrack.state.actions.NavigateToClickTrackScreen
import net.ganin.vsevolod.clicktrack.view.preview.PREVIEW_CLICK_TRACK_1
import net.ganin.vsevolod.clicktrack.view.preview.PREVIEW_CLICK_TRACK_2
import net.ganin.vsevolod.clicktrack.view.widget.ClickTrackView

@Composable
fun ClickTrackListScreenView(
    state: ClickTrackListScreenState,
    dispatch: Dispatch = {},
) {
    LazyColumnFor(state.items, modifier = Modifier.fillMaxSize()) { clickTrack ->
        Card(modifier = Modifier.padding(8.dp)) {
            ClickTrackView(
                clickTrack,
                modifier = Modifier
                    .fillParentMaxWidth()
                    .height(100.dp)
                    .clickable(onClick = {
                        dispatch(NavigateToClickTrackScreen(clickTrack))
                    })
            )
        }
    }
}

@Preview
@Composable
fun PreviewClickTrackListScreenView() {
    ClickTrackListScreenView(
        ClickTrackListScreenState(
            listOf(
                PREVIEW_CLICK_TRACK_1,
                PREVIEW_CLICK_TRACK_2,
            )
        )
    )
}
