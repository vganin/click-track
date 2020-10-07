package net.ganin.vsevolod.clicktrack.view.screen

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.ConstraintLayout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.AlertDialog
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import net.ganin.vsevolod.clicktrack.R
import net.ganin.vsevolod.clicktrack.redux.Action
import net.ganin.vsevolod.clicktrack.redux.Dispatch
import net.ganin.vsevolod.clicktrack.state.ClickTrackScreenState
import net.ganin.vsevolod.clicktrack.state.actions.NavigateBack
import net.ganin.vsevolod.clicktrack.state.actions.NavigateToEditClickTrackScreen
import net.ganin.vsevolod.clicktrack.state.actions.RemoveClickTrack
import net.ganin.vsevolod.clicktrack.state.actions.TogglePlay
import net.ganin.vsevolod.clicktrack.view.preview.PREVIEW_CLICK_TRACK_1
import net.ganin.vsevolod.clicktrack.view.widget.ClickTrackView
import net.ganin.vsevolod.clicktrack.view.widget.ClickTrackViewState
import net.ganin.vsevolod.clicktrack.view.widget.PlayStopView

@Composable
fun ClickTrackScreenView(
    state: ClickTrackScreenState,
    dispatch: Dispatch = {},
) {
    Scaffold(
        topBar = { ClickTrackScreenTopBar(state, dispatch) },
        modifier = Modifier.fillMaxSize(),
    ) {
        ClickTrackScreenContent(state, dispatch)
    }
}

@Composable
private fun ClickTrackScreenContent(
    state: ClickTrackScreenState,
    dispatch: (Action) -> Unit = {}
) {
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        ClickTrackView(
            state = ClickTrackViewState(
                clickTrack = state.clickTrack.clickTrack,
                drawTextMarks = true,
                playbackTimestamp = state.playbackStamp,
            ),
            modifier = Modifier.fillMaxSize()
        )
        PlayStopView(
            isPlaying = state.isPlaying,
            onToggle = { dispatch(TogglePlay) },
            modifier = Modifier.constrainAs(createRef()) {
                centerHorizontallyTo(parent)
                bottom.linkTo(parent.bottom, margin = 16.dp)
            }
        )
    }
}

@Composable
private fun ClickTrackScreenTopBar(
    state: ClickTrackScreenState,
    dispatch: (Action) -> Unit = {}
) {
    TopAppBar(
        title = { Text(text = state.clickTrack.name) },
        actions = {
            var editEnabled by remember { mutableStateOf(true) }
            IconButton(
                onClick = {
                    dispatch(NavigateToEditClickTrackScreen(state.clickTrack))
                    editEnabled = false
                },
                enabled = editEnabled
            ) {
                Icon(asset = vectorResource(id = R.drawable.ic_edit_24))
            }

            var showDeleteConfirmation by remember { mutableStateOf(false) }
            IconButton(
                onClick = {
                    showDeleteConfirmation = true
                },
            ) {
                Icon(asset = vectorResource(id = R.drawable.ic_delete_24))
            }

            if (showDeleteConfirmation) {
                val dismiss: () -> Unit = remember {
                    { showDeleteConfirmation = false }
                }
                val confirm: () -> Unit = remember {
                    {
                        dispatch(RemoveClickTrack(state.clickTrack))
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
    ClickTrackScreenView(
        ClickTrackScreenState(
            clickTrack = PREVIEW_CLICK_TRACK_1,
            isPlaying = false,
            playbackStamp = null
        )
    )
}
