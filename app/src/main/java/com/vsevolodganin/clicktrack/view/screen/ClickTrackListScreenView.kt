package com.vsevolodganin.clicktrack.view.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.FabPosition
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vsevolodganin.clicktrack.R
import com.vsevolodganin.clicktrack.model.ClickTrackWithId
import com.vsevolodganin.clicktrack.redux.Dispatch
import com.vsevolodganin.clicktrack.state.ClickTrackListScreenState
import com.vsevolodganin.clicktrack.state.actions.NavigateToClickTrackScreen
import com.vsevolodganin.clicktrack.state.actions.NavigateToMetronomeScreen
import com.vsevolodganin.clicktrack.state.actions.StoreAddNewClickTrack
import com.vsevolodganin.clicktrack.state.actions.StoreRemoveClickTrack
import com.vsevolodganin.clicktrack.utils.compose.swipeToRemove
import com.vsevolodganin.clicktrack.view.common.Constants
import com.vsevolodganin.clicktrack.view.icon.ClickTrackIcons
import com.vsevolodganin.clicktrack.view.icon.Metronome
import com.vsevolodganin.clicktrack.view.preview.PREVIEW_CLICK_TRACK_1
import com.vsevolodganin.clicktrack.view.preview.PREVIEW_CLICK_TRACK_2
import com.vsevolodganin.clicktrack.view.widget.ClickTrackView

@Composable
fun ClickTrackListScreenView(
    state: ClickTrackListScreenState,
    modifier: Modifier = Modifier,
    dispatch: Dispatch = Dispatch {},
) {
    Scaffold(
        topBar = { ClickTrackListScreenTopBar(dispatch) },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            FloatingActionButton(onClick = { dispatch(StoreAddNewClickTrack) }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        },
        modifier = modifier,
    ) {
        ClickTrackListScreenContent(state, dispatch)
    }
}

@Composable
private fun ClickTrackListScreenContent(
    state: ClickTrackListScreenState,
    dispatch: Dispatch,
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
private fun ClickTrackListScreenTopBar(dispatch: Dispatch) {
    TopAppBar(
        title = {
            Text(text = stringResource(id = R.string.click_track_list))
        },
        actions = {
            IconButton(onClick = { dispatch(NavigateToMetronomeScreen) }) {
                Icon(imageVector = ClickTrackIcons.Metronome, contentDescription = null)
            }
        }
    )
}

@Composable
private fun LazyItemScope.ClickTrackListItem(clickTrack: ClickTrackWithId, dispatch: Dispatch) {
    BoxWithConstraints {
        Card(
            modifier = Modifier
                .swipeToRemove(constraints = constraints, onDelete = { dispatch(StoreRemoveClickTrack(clickTrack.id)) })
                .padding(8.dp),
            elevation = 2.dp
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                ClickTrackView(
                    clickTrack = clickTrack.value,
                    drawTextMarks = false,
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
