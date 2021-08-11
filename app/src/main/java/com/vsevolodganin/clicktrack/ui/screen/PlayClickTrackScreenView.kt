package com.vsevolodganin.clicktrack.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.AlertDialog
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.DialogProperties
import androidx.constraintlayout.compose.ConstraintLayout
import com.vsevolodganin.clicktrack.R
import com.vsevolodganin.clicktrack.state.redux.action.ClickTrackAction
import com.vsevolodganin.clicktrack.state.redux.action.NavigationAction
import com.vsevolodganin.clicktrack.state.redux.action.PlayerAction
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
                        PlayerAction.StartPlay(state.clickTrack.id, progress = 0.0)
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
            progress = state.progress,
            progressDragAndDropEnabled = true,
            onProgressDragStart = { dispatch(PlayerAction.PausePlay) },
            onProgressDrop = { progress -> dispatch(PlayerAction.StartPlay(state.clickTrack.id, progress)) },
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
                    properties = DialogProperties(
                        usePlatformDefaultWidth = true
                    )
                )
            }
        },
    )
}

@Preview
@Composable
private fun Preview() {
    PlayClickTrackScreenView(
        PlayClickTrackUiState(
            clickTrack = PREVIEW_CLICK_TRACK_1,
            progress = null,
            isPlaying = false,
        )
    )
}
