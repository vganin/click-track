package net.ganin.vsevolod.clicktrack.view.screen

import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.ConstraintLayout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.AlertDialog
import androidx.compose.material.FabPosition
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
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
import androidx.ui.tooling.preview.Preview
import net.ganin.vsevolod.clicktrack.R
import net.ganin.vsevolod.clicktrack.redux.Dispatch
import net.ganin.vsevolod.clicktrack.state.PlayClickTrackScreenState
import net.ganin.vsevolod.clicktrack.state.actions.NavigateBack
import net.ganin.vsevolod.clicktrack.state.actions.NavigateToEditClickTrackScreen
import net.ganin.vsevolod.clicktrack.state.actions.StartPlay
import net.ganin.vsevolod.clicktrack.state.actions.StopPlay
import net.ganin.vsevolod.clicktrack.state.actions.StoreRemoveClickTrack
import net.ganin.vsevolod.clicktrack.view.preview.PREVIEW_CLICK_TRACK_1
import net.ganin.vsevolod.clicktrack.view.widget.ClickTrackView
import net.ganin.vsevolod.clicktrack.view.widget.ClickTrackViewState
import net.ganin.vsevolod.clicktrack.view.widget.PlayStopButton

@Composable
fun PlayClickTrackScreenView(
    state: PlayClickTrackScreenState,
    dispatch: Dispatch = Dispatch {},
) {
    Scaffold(
        topBar = { ClickTrackScreenTopBar(state, dispatch) },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            PlayStopButton(
                isPlaying = state.isPlaying,
                onToggle = {
                    val action = if (state.isPlaying) StopPlay else StartPlay(state.clickTrack)
                    dispatch(action)
                },
            )
        },
        modifier = Modifier.fillMaxSize(),
    ) {
        ClickTrackScreenContent(state)
    }
}

@Composable
private fun ClickTrackScreenContent(
    state: PlayClickTrackScreenState,
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.surface)
    ) {
        ClickTrackView(
            state = ClickTrackViewState(
                clickTrack = state.clickTrack.value,
                drawTextMarks = true,
                playbackTimestamp = state.playbackStamp,
            ),
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun ClickTrackScreenTopBar(
    state: PlayClickTrackScreenState,
    dispatch: Dispatch = Dispatch {}
) {
    TopAppBar(
        title = { Text(text = state.clickTrack.value.name) },
        actions = {
            var editEnabled by remember { mutableStateOf(true) }
            IconButton(
                onClick = {
                    dispatch(NavigateToEditClickTrackScreen(state.clickTrack))
                    editEnabled = false
                },
                enabled = editEnabled
            ) {
                Icon(Icons.Default.Edit)
            }

            var showDeleteConfirmation by remember { mutableStateOf(false) }
            IconButton(
                onClick = {
                    showDeleteConfirmation = true
                },
            ) {
                Icon(Icons.Default.Delete)
            }

            if (showDeleteConfirmation) {
                val dismiss: () -> Unit = remember {
                    { showDeleteConfirmation = false }
                }
                val confirm: () -> Unit = remember {
                    {
                        dispatch(StoreRemoveClickTrack(state.clickTrack.id))
                        dispatch(NavigateBack)
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
                            Text(text = stringResource(id = android.R.string.ok))
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = dismiss,
                            shape = RectangleShape
                        ) {
                            Text(text = stringResource(id = android.R.string.no))
                        }
                    }
                )
            }
        }
    )
}

@Preview
@Composable
fun PreviewClickTrackScreenView() {
    PlayClickTrackScreenView(
        PlayClickTrackScreenState(
            clickTrack = PREVIEW_CLICK_TRACK_1,
            isPlaying = false,
            playbackStamp = null
        )
    )
}
