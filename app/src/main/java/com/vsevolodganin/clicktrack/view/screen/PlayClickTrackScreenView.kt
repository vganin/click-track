package com.vsevolodganin.clicktrack.view.screen

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
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import com.vsevolodganin.clicktrack.R
import com.vsevolodganin.clicktrack.model.ClickTrackId
import com.vsevolodganin.clicktrack.redux.Dispatch
import com.vsevolodganin.clicktrack.state.actions.ClickTrackAction
import com.vsevolodganin.clicktrack.state.actions.NavigationAction
import com.vsevolodganin.clicktrack.state.screen.PlayClickTrackScreenState
import com.vsevolodganin.clicktrack.utils.optionalCast
import com.vsevolodganin.clicktrack.view.preview.PREVIEW_CLICK_TRACK_1
import com.vsevolodganin.clicktrack.view.widget.ClickTrackView
import com.vsevolodganin.clicktrack.view.widget.PlayStopButton

@Composable
fun PlayClickTrackScreenView(
    state: PlayClickTrackScreenState,
    modifier: Modifier = Modifier,
    dispatch: Dispatch = Dispatch {},
) {
    DisposableEffect(Unit) {
        val databaseId = state.clickTrack.id.optionalCast<ClickTrackId.Database>() ?: return@DisposableEffect onDispose {}
        dispatch(ClickTrackAction.SubscribeToData(databaseId))
        onDispose {
            dispatch(ClickTrackAction.SubscribeToData.Dispose)
        }
    }

    Scaffold(
        topBar = { TopBar(state, dispatch) },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            PlayStopButton(
                isPlaying = state.isPlaying,
                onToggle = {
                    val action = if (state.isPlaying) {
                        ClickTrackAction.StopPlay
                    } else {
                        ClickTrackAction.StartPlay(state.clickTrack, progress = 0.0)
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
    state: PlayClickTrackScreenState,
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
            onProgressDragStart = { dispatch(ClickTrackAction.PausePlay) },
            onProgressDrop = { progress -> dispatch(ClickTrackAction.StartPlay(state.clickTrack, progress)) },
            viewportPanEnabled = true,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun TopBar(
    state: PlayClickTrackScreenState,
    dispatch: Dispatch,
) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = { dispatch(NavigationAction.Back) }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
            }
        },
        title = { Text(text = state.clickTrack.value.name) },
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
                        val databaseId = state.clickTrack.id.optionalCast<ClickTrackId.Database>() ?: return@lambda
                        dispatch(ClickTrackAction.RemoveClickTrack(databaseId, shouldStore = true))
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
                    }
                )
            }
        }
    )
}

@Preview
@Composable
private fun Preview() {
    PlayClickTrackScreenView(
        PlayClickTrackScreenState(
            clickTrack = PREVIEW_CLICK_TRACK_1,
            progress = null,
            isPlaying = false,
        )
    )
}
