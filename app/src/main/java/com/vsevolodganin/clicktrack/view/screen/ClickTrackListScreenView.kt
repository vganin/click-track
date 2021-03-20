package com.vsevolodganin.clicktrack.view.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
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
import com.vsevolodganin.clicktrack.state.actions.ClickTrackAction
import com.vsevolodganin.clicktrack.state.actions.NavigationAction
import com.vsevolodganin.clicktrack.state.actions.OpenDrawer
import com.vsevolodganin.clicktrack.utils.compose.padWithFabSpace
import com.vsevolodganin.clicktrack.utils.compose.swipeToRemove
import com.vsevolodganin.clicktrack.view.preview.PREVIEW_CLICK_TRACK_1
import com.vsevolodganin.clicktrack.view.preview.PREVIEW_CLICK_TRACK_2
import com.vsevolodganin.clicktrack.view.widget.ClickTrackFloatingActionButton
import com.vsevolodganin.clicktrack.view.widget.ClickTrackView

@Composable
fun ClickTrackListScreenView(
    state: ClickTrackListScreenState,
    modifier: Modifier = Modifier,
    dispatch: Dispatch = Dispatch {},
) {
    Scaffold(
        topBar = { TopBar(dispatch) },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            ClickTrackFloatingActionButton(onClick = { dispatch(ClickTrackAction.NewClickTrack) }) {
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
    state: ClickTrackListScreenState,
    dispatch: Dispatch,
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(state.items) { clickTrack ->
            key(clickTrack.id) {
                ClickTrackListItem(clickTrack, dispatch)
            }
        }

        padWithFabSpace()
    }
}

@Composable
private fun TopBar(dispatch: Dispatch) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = { dispatch(OpenDrawer) }) {
                Icon(imageVector = Icons.Default.Menu, contentDescription = null)
            }
        },
        title = {
            Text(text = stringResource(id = R.string.click_track_list))
        },
    )
}

@Composable
private fun LazyItemScope.ClickTrackListItem(clickTrack: ClickTrackWithId, dispatch: Dispatch) {
    BoxWithConstraints {
        Card(
            modifier = Modifier
                .swipeToRemove(constraints = constraints, onDelete = {
                    dispatch(ClickTrackAction.RemoveClickTrack(clickTrack.id, shouldStore = true))
                })
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
                            dispatch(NavigationAction.ToClickTrackScreen(clickTrack))
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
private fun Preview() {
    ClickTrackListScreenView(
        ClickTrackListScreenState(
            listOf(
                PREVIEW_CLICK_TRACK_1,
                PREVIEW_CLICK_TRACK_2,
            )
        )
    )
}
