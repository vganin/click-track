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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vsevolodganin.clicktrack.R
import com.vsevolodganin.clicktrack.list.ClickTrackListState
import com.vsevolodganin.clicktrack.list.ClickTrackListViewModel
import com.vsevolodganin.clicktrack.model.ClickTrackId
import com.vsevolodganin.clicktrack.model.ClickTrackWithDatabaseId
import com.vsevolodganin.clicktrack.model.ClickTrackWithId
import com.vsevolodganin.clicktrack.ui.ClickTrackTheme
import com.vsevolodganin.clicktrack.ui.piece.ClickTrackView
import com.vsevolodganin.clicktrack.ui.piece.FloatingActionButton
import com.vsevolodganin.clicktrack.ui.piece.TopAppBar
import com.vsevolodganin.clicktrack.ui.preview.PREVIEW_CLICK_TRACK_1
import com.vsevolodganin.clicktrack.ui.preview.PREVIEW_CLICK_TRACK_2
import com.vsevolodganin.clicktrack.utils.compose.SwipeToDelete
import com.vsevolodganin.clicktrack.utils.compose.padWithFabSpace
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

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

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(items = state.items, key = ClickTrackWithId::id) { clickTrack ->
            ClickTrackListItem(
                viewModel = viewModel,
                clickTrack = clickTrack,
            )
        }

        padWithFabSpace()
    }
}

@Composable
private fun TopBar(viewModel: ClickTrackListViewModel) {
    TopAppBar(
        title = {
            Text(text = stringResource(id = R.string.click_track_list_screen_title))
        },
        navigationIcon = {
            IconButton(onClick = viewModel::onMenuClick) {
                Icon(imageVector = Icons.Default.Menu, contentDescription = null)
            }
        },
    )
}

@Composable
private fun LazyItemScope.ClickTrackListItem(
    viewModel: ClickTrackListViewModel,
    clickTrack: ClickTrackWithDatabaseId,
) {
    val contentPadding = 8.dp

    SwipeToDelete(
        onDeleted = { viewModel.onItemRemove(clickTrack.id) },
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
                        .clickable(onClick = { viewModel.onItemClick(clickTrack.id) }),
                )
                Text(
                    text = clickTrack.value.name,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@ScreenPreviews
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
        }
    )
}
