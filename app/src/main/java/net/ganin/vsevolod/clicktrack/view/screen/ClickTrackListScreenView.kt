package net.ganin.vsevolod.clicktrack.view.screen

import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material.Card
import androidx.compose.material.FabPosition
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.WithConstraints
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import net.ganin.vsevolod.clicktrack.R
import net.ganin.vsevolod.clicktrack.model.ClickTrackWithId
import net.ganin.vsevolod.clicktrack.redux.Dispatch
import net.ganin.vsevolod.clicktrack.state.ClickTrackListScreenState
import net.ganin.vsevolod.clicktrack.state.actions.AddNewClickTrack
import net.ganin.vsevolod.clicktrack.state.actions.NavigateToClickTrackScreen
import net.ganin.vsevolod.clicktrack.state.actions.RemoveClickTrack
import net.ganin.vsevolod.clicktrack.utils.compose.swipeToRemove
import net.ganin.vsevolod.clicktrack.view.common.Constants
import net.ganin.vsevolod.clicktrack.view.preview.PREVIEW_CLICK_TRACK_1
import net.ganin.vsevolod.clicktrack.view.preview.PREVIEW_CLICK_TRACK_2
import net.ganin.vsevolod.clicktrack.view.widget.ClickTrackView
import net.ganin.vsevolod.clicktrack.view.widget.ClickTrackViewState

@Composable
fun ClickTrackListScreenView(
    state: ClickTrackListScreenState,
    dispatch: Dispatch = Dispatch {},
) {
    Scaffold(
        topBar = { ClickTrackListScreenTopBar() },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            FloatingActionButton(onClick = { dispatch(AddNewClickTrack) }) {
                Icon(Icons.Default.Add)
            }
        },
        modifier = Modifier.fillMaxSize(),
    ) {
        ClickTrackListScreenContent(state, dispatch)
    }
}

@Composable
private fun ClickTrackListScreenContent(
    state: ClickTrackListScreenState,
    dispatch: Dispatch = Dispatch {},
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(state.items) { clickTrack ->
            key(clickTrack.id) {
                ClickTrackListItem(clickTrack, dispatch)
            }
        }

        item {
            Spacer(modifier = Modifier.size(Constants.FAB_SIZE_WITH_PADDINGS))
        }
    }
}

@Composable
private fun ClickTrackListScreenTopBar() {
    TopAppBar(title = {
        Text(text = stringResource(id = R.string.click_track_list))
    })
}

@Composable
private fun LazyItemScope.ClickTrackListItem(clickTrack: ClickTrackWithId, dispatch: Dispatch) {
    WithConstraints {
        Card(
            modifier = Modifier
                .padding(8.dp)
                .swipeToRemove(constraints = constraints, onDelete = {
                    dispatch(RemoveClickTrack(clickTrack.id))
                }),
            elevation = 2.dp
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                ClickTrackView(
                    state = ClickTrackViewState(
                        clickTrack.value,
                        drawTextMarks = false,
                        playbackTimestamp = null
                    ),
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .height(100.dp)
                        .clickable(onClick = {
                            dispatch(NavigateToClickTrackScreen(clickTrack))
                        }),
                )
                Text(
                    text = clickTrack.value.name,
                    modifier = Modifier.padding(8.dp)
                )
            }
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
