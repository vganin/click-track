package com.vsevolodganin.clicktrack.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.AlertDialog
import androidx.compose.material.Chip
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.FabPosition
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalMinimumTouchTargetEnforcement
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.SnackbarResult
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.vsevolodganin.clicktrack.R
import com.vsevolodganin.clicktrack.redux.action.BackAction
import com.vsevolodganin.clicktrack.redux.action.BackstackAction
import com.vsevolodganin.clicktrack.redux.action.ClickTrackAction
import com.vsevolodganin.clicktrack.redux.action.ExportAction
import com.vsevolodganin.clicktrack.redux.action.PlayerAction
import com.vsevolodganin.clicktrack.redux.action.SettingsAction
import com.vsevolodganin.clicktrack.redux.core.Dispatch
import com.vsevolodganin.clicktrack.ui.ClickTrackTheme
import com.vsevolodganin.clicktrack.ui.model.PREVIEW_CLICK_TRACK_1
import com.vsevolodganin.clicktrack.ui.model.PlayClickTrackUiState
import com.vsevolodganin.clicktrack.ui.piece.Checkbox
import com.vsevolodganin.clicktrack.ui.piece.ClickTrackView
import com.vsevolodganin.clicktrack.ui.piece.PlayStopButton
import com.vsevolodganin.clicktrack.ui.piece.TopAppBar
import kotlinx.coroutines.launch

@Composable
fun PlayClickTrackScreenView(
    state: PlayClickTrackUiState,
    modifier: Modifier = Modifier,
    dispatch: Dispatch = Dispatch {},
) {
    val scaffoldState = rememberScaffoldState()
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { TopBar(state, dispatch, scaffoldState.snackbarHostState) },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = { BottomBar(state, dispatch) },
        modifier = modifier,
    ) {
        Content(state, dispatch)
    }
}

@Composable
private fun Content(
    state: PlayClickTrackUiState,
    dispatch: Dispatch,
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.surface)
    ) {
        ClickTrackView(
            clickTrack = state.clickTrack.value,
            playTrackingMode = state.playTrackingMode,
            drawTextMarks = true,
            progress = state.playProgress,
            progressDragAndDropEnabled = true,
            onProgressDragStart = { dispatch(PlayerAction.PausePlay) },
            onProgressDrop = { progress -> dispatch(PlayerAction.StartPlayClickTrack(state.clickTrack.id, progress)) },
            viewportPanEnabled = true,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun TopBar(
    state: PlayClickTrackUiState,
    dispatch: Dispatch,
    snackbarHostState: SnackbarHostState,
) {
    TopAppBar(
        title = { Text(text = state.clickTrack.value.name) },
        navigationIcon = {
            IconButton(onClick = { dispatch(BackAction) }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
            }
        },
        actions = {
            var editEnabled by remember { mutableStateOf(true) }
            IconButton(
                onClick = {
                    dispatch(BackstackAction.ToEditClickTrackScreen(clickTrack = state.clickTrack, isInitialEdit = false))
                    editEnabled = false
                },
                enabled = editEnabled
            ) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = null)
            }

            var showDeleteConfirmation by remember { mutableStateOf(false) }
            IconButton(
                onClick = {
                    showDeleteConfirmation = true
                },
            ) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = null)
            }

            OverflowMenu(state, dispatch, snackbarHostState)

            if (showDeleteConfirmation) {
                val dismiss: () -> Unit = remember {
                    { showDeleteConfirmation = false }
                }
                val confirm: () -> Unit = remember {
                    lambda@{
                        dispatch(ClickTrackAction.Remove(state.clickTrack.id))
                        dispatch(BackAction)
                    }
                }
                AlertDialog(
                    onDismissRequest = dismiss,
                    text = {
                        Text(text = stringResource(id = R.string.delete_click_track_confirmation))
                    },
                    confirmButton = {
                        TextButton(
                            onClick = confirm,
                            shape = RectangleShape
                        ) {
                            Text(text = stringResource(id = android.R.string.ok).uppercase())
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = dismiss,
                            shape = RectangleShape
                        ) {
                            Text(text = stringResource(id = android.R.string.cancel).uppercase())
                        }
                    },
                )
            }
        },
    )
}

@Composable
private fun OverflowMenu(state: PlayClickTrackUiState, dispatch: Dispatch, snackbarHostState: SnackbarHostState) {
    val coroutineScope = rememberCoroutineScope()
    var showDropdown by remember { mutableStateOf(false) }

    IconButton(onClick = { showDropdown = !showDropdown }) {
        Icon(imageVector = Icons.Default.MoreVert, contentDescription = null)
    }

    DropdownMenu(expanded = showDropdown, onDismissRequest = { showDropdown = false }) {
        val startedExportMessage = stringResource(R.string.started_export, state.clickTrack.value.name)
        val cancelActionLabel = stringResource(android.R.string.cancel)

        DropdownMenuItem(onClick = {
            dispatch(ExportAction.Start(state.clickTrack.id))
            showDropdown = false
            coroutineScope.launch {
                val snackbarResult = snackbarHostState.showSnackbar(
                    message = startedExportMessage,
                    duration = SnackbarDuration.Short,
                    actionLabel = cancelActionLabel
                )
                if (snackbarResult == SnackbarResult.ActionPerformed) {
                    dispatch(ExportAction.Stop(state.clickTrack.id))
                }
            }
        }) {
            Text(stringResource(R.string.export_to_audio_file))
        }
    }
}

@Composable
private fun BottomBar(state: PlayClickTrackUiState, dispatch: Dispatch) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
    ) {
        PlayStopButton(
            isPlaying = state.isPlaying,
            onToggle = {
                val action = if (state.isPlaying) {
                    PlayerAction.StopPlay
                } else {
                    PlayerAction.StartPlayClickTrack(state.clickTrack.id, progress = 0.0)
                }
                dispatch(action)
            },
            modifier = Modifier.align(Alignment.Center),
            enableInsets = false,
        )

        CompositionLocalProvider(LocalMinimumTouchTargetEnforcement provides false) {
            Chip(
                onClick = {
                    dispatch(SettingsAction.ChangePlayTrackingMode(!state.playTrackingMode))
                },
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 8.dp)
            ) {
                Checkbox(checked = state.playTrackingMode, onCheckedChange = null)
                Spacer(Modifier.width(8.dp))
                Text(text = stringResource(R.string.play_tracking_mode))
            }
        }
    }
}

@ScreenPreviews
@Composable
private fun Preview() = ClickTrackTheme {
    PlayClickTrackScreenView(
        PlayClickTrackUiState(
            clickTrack = PREVIEW_CLICK_TRACK_1,
            playProgress = null,
            isPlaying = false,
            playTrackingMode = true,
        )
    )
}
