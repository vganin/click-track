package com.vsevolodganin.clicktrack.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Card
import androidx.compose.material.FabPosition
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vsevolodganin.clicktrack.generated.resources.MR
import com.vsevolodganin.clicktrack.list.ClickTrackListState
import com.vsevolodganin.clicktrack.list.ClickTrackListViewModel
import com.vsevolodganin.clicktrack.model.ClickTrackId
import com.vsevolodganin.clicktrack.model.ClickTrackWithDatabaseId
import com.vsevolodganin.clicktrack.model.ClickTrackWithId
import com.vsevolodganin.clicktrack.ui.piece.ClickTrackView
import com.vsevolodganin.clicktrack.ui.piece.DragHandle
import com.vsevolodganin.clicktrack.ui.piece.FloatingActionButton
import com.vsevolodganin.clicktrack.ui.piece.TopAppBar
import com.vsevolodganin.clicktrack.ui.preview.PREVIEW_CLICK_TRACK_1
import com.vsevolodganin.clicktrack.ui.preview.PREVIEW_CLICK_TRACK_2
import com.vsevolodganin.clicktrack.ui.theme.ClickTrackTheme
import com.vsevolodganin.clicktrack.ui.theme.commonCardElevation
import com.vsevolodganin.clicktrack.utils.compose.SwipeToDelete
import com.vsevolodganin.clicktrack.utils.compose.padWithFabSpace
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyColumnState

@Composable
fun ClickTrackListScreenView(
    viewModel: ClickTrackListViewModel,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = { TopBar(viewModel) },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.onAddClick() }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        },
        modifier = modifier,
    ) {
        Content(viewModel)
    }
}

@Composable
private fun Content(viewModel: ClickTrackListViewModel) {
    val state by viewModel.state.collectAsState()

    val numberOfNonDraggableItems = 1
    val lazyListState = rememberLazyListState()
    val reorderableLazyListState = rememberReorderableLazyColumnState(lazyListState) { from, to ->
        viewModel.onItemMove(from.index - numberOfNonDraggableItems, to.index - numberOfNonDraggableItems)
    }

    LazyColumn(
        state = lazyListState,
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            // FIXME: Dummy item to workaround https://github.com/Calvin-LL/Reorderable/issues/4
            Spacer(Modifier.height(1.dp))
        }

        items(items = state.items, key = ClickTrackWithId::id) { clickTrack ->
            ReorderableItem(reorderableLazyListState = reorderableLazyListState, key = clickTrack.id) { isDragging ->
                ClickTrackListItem(
                    viewModel = viewModel,
                    clickTrack = clickTrack,
                    dragHandleModifier = Modifier.draggableHandle(
                        onDragStopped = { viewModel.onItemMoveFinished() }
                    ),
                    elevation = commonCardElevation(isDragging)
                )
            }
        }

        padWithFabSpace()
    }
}

@Composable
private fun TopBar(viewModel: ClickTrackListViewModel) {
    TopAppBar(
        title = {
            Text(text = stringResource(MR.strings.click_track_list_screen_title))
        },
        navigationIcon = {
            IconButton(onClick = viewModel::onMenuClick) {
                Icon(imageVector = Icons.Default.Menu, contentDescription = null)
            }
        },
    )
}

@Composable
private fun ClickTrackListItem(
    viewModel: ClickTrackListViewModel,
    clickTrack: ClickTrackWithDatabaseId,
    dragHandleModifier: Modifier,
    elevation: Dp,
) {
    val contentPadding = 8.dp

    SwipeToDelete(
        onDeleted = { viewModel.onItemRemove(clickTrack.id) },
        contentPadding = contentPadding
    ) {
        Card(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxWidth()
                .height(100.dp),
            elevation = elevation
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                ClickTrackView(
                    clickTrack = clickTrack.value,
                    drawTextMarks = false,
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(onClick = { viewModel.onItemClick(clickTrack.id) }),
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = MaterialTheme.colors.surface.copy(alpha = 0.25f))
                )

                Row(
                    modifier = Modifier.align(Alignment.CenterStart),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DragHandle(
                        modifier = dragHandleModifier
                    )

                    Text(
                        text = clickTrack.value.name,
                    )
                }
            }
        }
    }
}

@ScreenPreview
@Composable
fun ClickTrackListPreview() = ClickTrackTheme {
    ClickTrackListScreenView(
        viewModel = object : ClickTrackListViewModel {
            override val state: StateFlow<ClickTrackListState> = MutableStateFlow(
                ClickTrackListState(
                    listOf(
                        PREVIEW_CLICK_TRACK_1,
                        PREVIEW_CLICK_TRACK_2,
                    )
                )
            )

            override fun onAddClick() = Unit
            override fun onItemClick(id: ClickTrackId.Database) = Unit
            override fun onItemRemove(id: ClickTrackId.Database) = Unit
            override fun onMenuClick() = Unit
            override fun onItemMove(from: Int, to: Int) = Unit
            override fun onItemMoveFinished() = Unit
        }
    )
}
