package com.vsevolodganin.clicktrack.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.FabPosition
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vsevolodganin.clicktrack.R
import com.vsevolodganin.clicktrack.model.ClickTrackWithDatabaseId
import com.vsevolodganin.clicktrack.model.ClickTrackWithId
import com.vsevolodganin.clicktrack.redux.action.BackstackAction
import com.vsevolodganin.clicktrack.redux.action.ClickTrackAction
import com.vsevolodganin.clicktrack.redux.action.DrawerAction
import com.vsevolodganin.clicktrack.redux.core.Dispatch
import com.vsevolodganin.clicktrack.ui.model.ClickTrackListUiState
import com.vsevolodganin.clicktrack.ui.model.PREVIEW_CLICK_TRACK_1
import com.vsevolodganin.clicktrack.ui.model.PREVIEW_CLICK_TRACK_2
import com.vsevolodganin.clicktrack.ui.piece.ClickTrackView
import com.vsevolodganin.clicktrack.ui.piece.FloatingActionButton
import com.vsevolodganin.clicktrack.ui.piece.TopAppBar
import com.vsevolodganin.clicktrack.utils.compose.SwipeToDelete
import com.vsevolodganin.clicktrack.utils.compose.padWithFabSpace

@Composable
fun ClickTrackListScreenView(
    state: ClickTrackListUiState,
    modifier: Modifier = Modifier,
    dispatch: Dispatch = Dispatch {},
) {
    Scaffold(
        topBar = { TopBar(dispatch) },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            FloatingActionButton(onClick = { dispatch(ClickTrackAction.AddNew) }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        },
        modifier = modifier,
    ) {
        Content(state, dispatch)
    }
}

@Composable
private fun Content(
    state: ClickTrackListUiState,
    dispatch: Dispatch,
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(items = state.items, key = ClickTrackWithId::id) { clickTrack ->
            ClickTrackListItem(clickTrack, dispatch)
        }

        padWithFabSpace()
    }
}

@Composable
private fun TopBar(dispatch: Dispatch) {
    TopAppBar(
        title = {
            Text(text = stringResource(id = R.string.click_track_list))
        },
        navigationIcon = {
            IconButton(onClick = { dispatch(DrawerAction.Open) }) {
                Icon(imageVector = Icons.Default.Menu, contentDescription = null)
            }
        },
    )
}

@Composable
private fun LazyItemScope.ClickTrackListItem(clickTrack: ClickTrackWithDatabaseId, dispatch: Dispatch) {
    val contentPadding = 8.dp

    SwipeToDelete(
        onDeleted = { dispatch(ClickTrackAction.Remove(clickTrack.id)) },
        contentPadding = contentPadding
    ) {
        Card(
            modifier = Modifier.padding(contentPadding),
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
                            dispatch(BackstackAction.ToClickTrackScreen(clickTrack.id))
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
fun ClickTrackListPreview() {
    ClickTrackListScreenView(
        ClickTrackListUiState(
            listOf(
                PREVIEW_CLICK_TRACK_1,
                PREVIEW_CLICK_TRACK_2,
            )
        )
    )
}
