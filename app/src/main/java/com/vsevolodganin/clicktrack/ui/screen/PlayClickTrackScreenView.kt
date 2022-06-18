package com.vsevolodganin.clicktrack.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.AlertDialog
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.FabPosition
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import com.vsevolodganin.clicktrack.R
import com.vsevolodganin.clicktrack.state.redux.action.ClickTrackAction
import com.vsevolodganin.clicktrack.state.redux.action.ExportAction
import com.vsevolodganin.clicktrack.state.redux.action.NavigationAction
import com.vsevolodganin.clicktrack.state.redux.action.PlayerAction
import com.vsevolodganin.clicktrack.state.redux.core.Action
import com.vsevolodganin.clicktrack.state.redux.core.Dispatch
import com.vsevolodganin.clicktrack.ui.model.PlayClickTrackUiState
import com.vsevolodganin.clicktrack.ui.preview.PREVIEW_CLICK_TRACK_1
import com.vsevolodganin.clicktrack.ui.widget.ClickTrackView
import com.vsevolodganin.clicktrack.ui.widget.InsetsAwareTopAppBar
import com.vsevolodganin.clicktrack.ui.widget.PlayStopButton

@Composable
fun PlayClickTrackScreenView(
    state: PlayClickTrackUiState,
    modifier: Modifier = Modifier,
    dispatch: Dispatch = Dispatch {},
) {
    Scaffold(
        topBar = { TopBar(state, dispatch) },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            PlayStopButton(
                isPlaying = state.isPlaying,
                onToggle = {
                    val action = if (state.isPlaying) {
                        PlayerAction.StopPlay
                    } else {
                        PlayerAction.StartPlayClickTrack(state.clickTrack.id, progress = 0.0)
                    }
                    dispatch(action)
                }
            )
        },
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
) {
    InsetsAwareTopAppBar(
        title = { Text(text = state.clickTrack.value.name) },
        navigationIcon = {
            IconButton(onClick = { dispatch(NavigationAction.Back) }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
            }
        },
        actions = {
            var editEnabled by remember { mutableStateOf(true) }
            IconButton(
                onClick = {
                    dispatch(NavigationAction.ToEditClickTrackScreen(state.clickTrack))
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

            OverflowMenu(state, dispatch)

            if (showDeleteConfirmation) {
                val dismiss: () -> Unit = remember {
                    { showDeleteConfirmation = false }
                }
                val confirm: () -> Unit = remember {
                    lambda@{
                        dispatch(ClickTrackAction.Remove(state.clickTrack.id))
                        dispatch(NavigationAction.Back)
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
private fun OverflowMenu(state: PlayClickTrackUiState, dispatch: Dispatch) {
    var showDropdown by remember { mutableStateOf(false) }
    val dispatchAndHideDropdown = remember {
        { action: Action ->
            showDropdown = false
            dispatch(action)
        }
    }

    IconButton(onClick = { showDropdown = !showDropdown }) {
        Icon(imageVector = Icons.Default.MoreVert, contentDescription = null)
    }

    DropdownMenu(expanded = showDropdown, onDismissRequest = { showDropdown = false }) {
        DropdownMenuItem(onClick = { dispatchAndHideDropdown(ExportAction.Start(state.clickTrack.value)) }) {
            Text(stringResource(R.string.export_to_audio_file))
        }
    }

    if (state.exportProgress != null) {
        val dismiss: () -> Unit = remember {
            {
                dispatch(ExportAction.Stop)
            }
        }
        AlertDialog(
            onDismissRequest = dismiss,
            text = {
                Box(modifier = Modifier.fillMaxWidth()) {
                    CircularProgressIndicator(
                        progress = state.exportProgress,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            },
            confirmButton = {},
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
}

@Preview
@Composable
private fun Preview() {
    PlayClickTrackScreenView(
        PlayClickTrackUiState(
            clickTrack = PREVIEW_CLICK_TRACK_1,
            playProgress = null,
            isPlaying = false,
            exportProgress = null,
        )
    )
}
