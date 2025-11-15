package com.vsevolodganin.clicktrack.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import clicktrack.multiplatform.generated.resources.Res
import clicktrack.multiplatform.generated.resources.general_cancel
import clicktrack.multiplatform.generated.resources.general_ok
import clicktrack.multiplatform.generated.resources.play_click_track_delete_confirmation
import clicktrack.multiplatform.generated.resources.play_click_track_export_to_audio_file
import clicktrack.multiplatform.generated.resources.play_click_track_play_tracking_mode
import clicktrack.multiplatform.generated.resources.play_click_track_started_export
import com.vsevolodganin.clicktrack.play.PlayClickTrackState
import com.vsevolodganin.clicktrack.play.PlayClickTrackViewModel
import com.vsevolodganin.clicktrack.play.isPaused
import com.vsevolodganin.clicktrack.play.isPlaying
import com.vsevolodganin.clicktrack.ui.piece.Checkbox
import com.vsevolodganin.clicktrack.ui.piece.ClickTrackView
import com.vsevolodganin.clicktrack.ui.piece.PlayButtons
import com.vsevolodganin.clicktrack.ui.piece.TopAppBarWithBack
import com.vsevolodganin.clicktrack.ui.preview.PREVIEW_CLICK_TRACK_1
import com.vsevolodganin.clicktrack.ui.theme.ClickTrackTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun PlayClickTrackScreenView(viewModel: PlayClickTrackViewModel, modifier: Modifier = Modifier) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = { TopBar(viewModel, state ?: return@Scaffold, snackbarHostState) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = { BottomBar(viewModel, state ?: return@Scaffold) },
        modifier = modifier,
    ) { paddingValues ->
        Content(
            viewModel = viewModel,
            state = state ?: return@Scaffold,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(
                    start = paddingValues.calculateStartPadding(LocalLayoutDirection.current),
                    end = paddingValues.calculateEndPadding(LocalLayoutDirection.current),
                    top = paddingValues.calculateTopPadding(),
                    bottom = 0.dp,
                ),
        )
    }
}

@Composable
private fun Content(
    viewModel: PlayClickTrackViewModel,
    state: PlayClickTrackState,
    modifier: Modifier,
) {
    ClickTrackView(
        clickTrack = state.clickTrack.value,
        playTrackingMode = state.playTrackingMode,
        drawTextMarks = true,
        progress = state.playProgress,
        progressDragAndDropEnabled = true,
        onProgressDragStart = viewModel::onProgressDragStart,
        onProgressDrop = viewModel::onProgressDrop,
        viewportPanEnabled = true,
        modifier = modifier,
    )
}

@Composable
private fun TopBar(viewModel: PlayClickTrackViewModel, state: PlayClickTrackState, snackbarHostState: SnackbarHostState) {
    TopAppBarWithBack(
        onBackClick = viewModel::onBackClick,
        title = { Text(text = state.clickTrack.value.name) },
        actions = {
            var editEnabled by remember { mutableStateOf(true) }
            IconButton(
                onClick = {
                    viewModel.onEditClick()
                    editEnabled = false
                },
                enabled = editEnabled,
            ) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = null)
            }

            var showDeleteConfirmation by remember { mutableStateOf(false) }

            IconButton(onClick = { showDeleteConfirmation = true }) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = null)
            }

            OverflowMenu(viewModel, state, snackbarHostState)

            if (showDeleteConfirmation) {
                val dismiss: () -> Unit = remember {
                    { showDeleteConfirmation = false }
                }
                AlertDialog(
                    onDismissRequest = dismiss,
                    text = {
                        Text(text = stringResource(Res.string.play_click_track_delete_confirmation))
                    },
                    confirmButton = {
                        TextButton(
                            onClick = viewModel::onRemoveClick,
                            shape = RectangleShape,
                        ) {
                            Text(text = stringResource(Res.string.general_ok).uppercase())
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = dismiss,
                            shape = RectangleShape,
                        ) {
                            Text(text = stringResource(Res.string.general_cancel).uppercase())
                        }
                    },
                )
            }
        },
    )
}

@Composable
private fun OverflowMenu(viewModel: PlayClickTrackViewModel, state: PlayClickTrackState, snackbarHostState: SnackbarHostState) {
    val coroutineScope = rememberCoroutineScope()
    var showDropdown by remember { mutableStateOf(false) }

    IconButton(onClick = { showDropdown = !showDropdown }) {
        Icon(imageVector = Icons.Default.MoreVert, contentDescription = null)
    }

    DropdownMenu(expanded = showDropdown, onDismissRequest = { showDropdown = false }) {
        val startedExportMessage = stringResource(Res.string.play_click_track_started_export, state.clickTrack.value.name)
        val cancelActionLabel = stringResource(Res.string.general_cancel)

        DropdownMenuItem(
            onClick = {
                viewModel.onExportClick()
                showDropdown = false
                coroutineScope.launch {
                    val snackbarResult = snackbarHostState.showSnackbar(
                        message = startedExportMessage,
                        duration = SnackbarDuration.Short,
                        actionLabel = cancelActionLabel,
                    )
                    if (snackbarResult == SnackbarResult.ActionPerformed) {
                        viewModel.onCancelExportClick()
                    }
                }
            },
            text = {
                Text(stringResource(Res.string.play_click_track_export_to_audio_file))
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BottomBar(viewModel: PlayClickTrackViewModel, state: PlayClickTrackState) {
    Box(
        modifier = Modifier.fillMaxWidth(),
    ) {
        PlayButtons(
            isPlaying = state.isPlaying,
            isPaused = state.isPaused,
            onTogglePlayStop = viewModel::onTogglePlayStop,
            onTogglePlayPause = viewModel::onTogglePlayPause,
            modifier = Modifier.align(Alignment.Center),
        )

        CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
            AssistChip(
                onClick = viewModel::onTogglePlayTrackingMode,
                label = {
                    Row {
                        Checkbox(checked = state.playTrackingMode, onCheckedChange = null)
                        Spacer(Modifier.width(8.dp))
                        Text(text = stringResource(Res.string.play_click_track_play_tracking_mode))
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 8.dp),
            )
        }
    }
}

@Preview
@Composable
internal fun PlayClickTrackScreenPreview() = ClickTrackTheme {
    PlayClickTrackScreenView(
        viewModel = object : PlayClickTrackViewModel {
            override val state: StateFlow<PlayClickTrackState?> = MutableStateFlow(
                PlayClickTrackState(
                    clickTrack = PREVIEW_CLICK_TRACK_1,
                    playProgress = null,
                    playTrackingMode = true,
                ),
            )

            override fun onBackClick() = Unit

            override fun onTogglePlayStop() = Unit

            override fun onTogglePlayPause() = Unit

            override fun onTogglePlayTrackingMode() = Unit

            override fun onProgressDragStart() = Unit

            override fun onProgressDrop(progress: Double) = Unit

            override fun onEditClick() = Unit

            override fun onRemoveClick() = Unit

            override fun onExportClick() = Unit

            override fun onCancelExportClick() = Unit
        },
    )
}
