package com.vsevolodganin.clicktrack.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import clicktrack.multiplatform.generated.resources.Res
import clicktrack.multiplatform.generated.resources.click_track_list_screen_title
import com.vsevolodganin.clicktrack.list.ClickTrackListState
import com.vsevolodganin.clicktrack.list.ClickTrackListViewModel
import com.vsevolodganin.clicktrack.model.ClickTrackId
import com.vsevolodganin.clicktrack.model.ClickTrackWithDatabaseId
import com.vsevolodganin.clicktrack.ui.piece.ClickTrackView
import com.vsevolodganin.clicktrack.ui.piece.DarkTopAppBar
import com.vsevolodganin.clicktrack.ui.piece.DragHandle
import com.vsevolodganin.clicktrack.ui.preview.PREVIEW_CLICK_TRACK_1
import com.vsevolodganin.clicktrack.ui.preview.PREVIEW_CLICK_TRACK_2
import com.vsevolodganin.clicktrack.ui.theme.ClickTrackTheme
import com.vsevolodganin.clicktrack.utils.compose.SwipeToDelete
import com.vsevolodganin.clicktrack.utils.compose.withFabPadding
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun ClickTrackListScreenView(viewModel: ClickTrackListViewModel, modifier: Modifier = Modifier) {
    Scaffold(
        topBar = { TopBar(viewModel) },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onAddClick() },
                shape = CircleShape,
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        },
        modifier = modifier,
    ) { paddingValues ->
        Content(
            viewModel = viewModel,
            paddingValues = paddingValues,
        )
    }
}

@Composable
private fun Content(viewModel: ClickTrackListViewModel, paddingValues: PaddingValues) {
    val state by viewModel.state.collectAsState()

    val numberOfNonDraggableItems = 1
    val lazyListState = rememberLazyListState()
    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        viewModel.onItemMove(from.index - numberOfNonDraggableItems, to.index - numberOfNonDraggableItems)
    }

    LazyColumn(
        state = lazyListState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = paddingValues.withFabPadding(),
    ) {
        item {
            // FIXME: Dummy item to workaround https://github.com/Calvin-LL/Reorderable/issues/4
            Spacer(Modifier.height(1.dp))
        }

        items(items = state.items, key = { Json.encodeToString(it.id) }) { clickTrack ->
            ReorderableItem(state = reorderableLazyListState, key = Json.encodeToString(clickTrack.id)) { isDragging ->
                ClickTrackListItem(
                    viewModel = viewModel,
                    clickTrack = clickTrack,
                    dragHandleModifier = Modifier.draggableHandle(
                        onDragStopped = { viewModel.onItemMoveFinished() },
                    ),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(viewModel: ClickTrackListViewModel) {
    DarkTopAppBar(
        title = {
            Text(text = stringResource(Res.string.click_track_list_screen_title))
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
) {
    val contentPadding = 8.dp

    SwipeToDelete(
        onDeleted = { viewModel.onItemRemove(clickTrack.id) },
        contentPadding = contentPadding,
    ) {
        Card(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxWidth()
                .height(100.dp),
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                ClickTrackView(
                    clickTrack = clickTrack.value,
                    drawTextMarks = false,
                    modifier = Modifier
                        .clickable(onClick = { viewModel.onItemClick(clickTrack.id) }),
                )

                Row(
                    modifier = Modifier.align(Alignment.CenterStart),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    DragHandle(
                        modifier = dragHandleModifier,
                    )

                    Text(
                        text = clickTrack.value.name,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun ClickTrackListScreenPreview() = ClickTrackTheme {
    ClickTrackListScreenView(
        viewModel = object : ClickTrackListViewModel {
            override val state: StateFlow<ClickTrackListState> = MutableStateFlow(
                ClickTrackListState(
                    listOf(
                        PREVIEW_CLICK_TRACK_1,
                        PREVIEW_CLICK_TRACK_2,
                    ),
                ),
            )

            override fun onAddClick() = Unit

            override fun onItemClick(id: ClickTrackId.Database) = Unit

            override fun onItemRemove(id: ClickTrackId.Database) = Unit

            override fun onMenuClick() = Unit

            override fun onItemMove(from: Int, to: Int) = Unit

            override fun onItemMoveFinished() = Unit
        },
    )
}
