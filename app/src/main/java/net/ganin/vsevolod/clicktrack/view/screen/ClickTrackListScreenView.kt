package net.ganin.vsevolod.clicktrack.view.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Stack
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.material.Card
import androidx.compose.material.FloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import net.ganin.vsevolod.clicktrack.R
import net.ganin.vsevolod.clicktrack.redux.Dispatch
import net.ganin.vsevolod.clicktrack.state.ClickTrackListScreenState
import net.ganin.vsevolod.clicktrack.state.actions.AddNewClickTrack
import net.ganin.vsevolod.clicktrack.state.actions.NavigateToClickTrackScreen
import net.ganin.vsevolod.clicktrack.view.preview.PREVIEW_CLICK_TRACK_1
import net.ganin.vsevolod.clicktrack.view.preview.PREVIEW_CLICK_TRACK_2
import net.ganin.vsevolod.clicktrack.view.widget.ClickTrackView
import net.ganin.vsevolod.clicktrack.view.widget.ClickTrackViewState

@Composable
fun ClickTrackListScreenView(
    state: ClickTrackListScreenState,
    dispatch: Dispatch = {},
) {
    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumnFor(
            items = state.items,
            modifier = Modifier.weight(1f)
        ) { clickTrack ->
            Card(modifier = Modifier.padding(8.dp)) {
                Stack(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = clickTrack.name,
                        modifier = Modifier.padding(8.dp)
                    )
                    ClickTrackView(
                        state = ClickTrackViewState(
                            clickTrack.clickTrack,
                            drawTextMarks = false,
                            playbackTimestamp = null
                        ),
                        modifier = Modifier
                            .fillParentMaxWidth()
                            .height(100.dp)
                            .clickable(onClick = {
                                dispatch(NavigateToClickTrackScreen(clickTrack.clickTrack))
                            }),
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = { dispatch(AddNewClickTrack) },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(8.dp)
        ) {
            Image(asset = vectorResource(id = R.drawable.ic_add_24))
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
