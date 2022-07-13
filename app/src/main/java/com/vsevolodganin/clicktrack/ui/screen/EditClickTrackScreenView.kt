package com.vsevolodganin.clicktrack.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.FabPosition
import androidx.compose.material.Icon
import androidx.compose.material.LocalMinimumTouchTargetEnforcement
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vsevolodganin.clicktrack.R
import com.vsevolodganin.clicktrack.model.ClickTrackId
import com.vsevolodganin.clicktrack.model.CueDuration
import com.vsevolodganin.clicktrack.model.NotePattern
import com.vsevolodganin.clicktrack.model.TimeSignature
import com.vsevolodganin.clicktrack.redux.EditClickTrackState
import com.vsevolodganin.clicktrack.redux.EditCueState
import com.vsevolodganin.clicktrack.redux.action.BackstackAction
import com.vsevolodganin.clicktrack.redux.action.EditClickTrackAction
import com.vsevolodganin.clicktrack.redux.core.Dispatch
import com.vsevolodganin.clicktrack.ui.model.EditClickTrackUiState
import com.vsevolodganin.clicktrack.ui.model.EditCueUiState
import com.vsevolodganin.clicktrack.ui.widget.ClickTrackFloatingActionButton
import com.vsevolodganin.clicktrack.ui.widget.CueView
import com.vsevolodganin.clicktrack.ui.widget.GenericTopBarWithBack
import com.vsevolodganin.clicktrack.utils.compose.StatefulTextField
import com.vsevolodganin.clicktrack.utils.compose.SwipeToDelete
import com.vsevolodganin.clicktrack.utils.compose.padWithFabSpace
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.minutes

@Composable
fun EditClickTrackScreenView(
    state: EditClickTrackUiState,
    modifier: Modifier = Modifier,
    dispatch: Dispatch = Dispatch {},
) {
    val coroutineScope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()

    Scaffold(
        topBar = { GenericTopBarWithBack(R.string.edit_click_track, dispatch) },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            Box(modifier = Modifier.fillMaxWidth()) {
                ClickTrackFloatingActionButton(
                    onClick = {
                        dispatch(EditClickTrackAction.AddNewCue)
                        coroutineScope.launch {
                            awaitFrame()
                            lazyListState.scrollToItem(lazyListState.layoutInfo.totalItemsCount - 1)
                        }
                    },
                    modifier = Modifier.align(Center)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                }

                if (state.showForwardButton) {
                    ClickTrackFloatingActionButton(
                        onClick = {
                            dispatch(BackstackAction.ToClickTrackScreen(state.id))
                        },
                        modifier = Modifier
                            .align(CenterEnd)
                            .padding(end = 16.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null)
                    }
                }
            }
        },
        modifier = modifier,
    ) {
        Content(state, lazyListState, dispatch)
    }
}

@Composable
private fun Content(
    state: EditClickTrackUiState,
    listState: LazyListState,
    dispatch: Dispatch,
) {
    val contentPadding = 8.dp
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
    ) {
        stickyHeader {
            StatefulTextField(
                initialValue = state.name,
                onValueChanged = { dispatch(EditClickTrackAction.EditName(it)) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(text = stringResource(R.string.click_track_name_hint)) },
                textStyle = MaterialTheme.typography.h6,
                isError = EditClickTrackState.Error.NAME in state.errors,
                colors = TextFieldDefaults.textFieldColors(backgroundColor = MaterialTheme.colors.surface)
            )
        }

        item {
            Spacer(modifier = Modifier.size(8.dp))
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(contentPadding)
            ) {
                Row(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = stringResource(R.string.repeat),
                        modifier = Modifier.align(CenterVertically)
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    CompositionLocalProvider(LocalMinimumTouchTargetEnforcement provides false) {
                        Checkbox(
                            checked = state.loop,
                            onCheckedChange = { dispatch(EditClickTrackAction.EditLoop(it)) }
                        )
                    }
                }
            }
        }

        itemsIndexed(items = state.cues, key = { _, cue -> cue.id }) { index, cue ->
            CueListItem(
                cue = cue,
                index = index,
                contentPadding = contentPadding,
                elevation = 1.dp,
                dispatch = dispatch,
            )
        }

        padWithFabSpace()
    }
}

@Composable
private fun CueListItem(
    cue: EditCueUiState,
    index: Int,
    contentPadding: Dp,
    elevation: Dp,
    dispatch: Dispatch,
    modifier: Modifier = Modifier,
) {
    SwipeToDelete(
        onDeleted = { dispatch(EditClickTrackAction.RemoveCue(index)) },
        modifier = modifier,
        contentPadding = contentPadding,
    ) {
        Card(
            modifier = Modifier.padding(contentPadding),
            elevation = elevation,
        ) {
            CueView(
                value = cue,
                displayPosition = index + 1,
                onNameChange = { dispatch(EditClickTrackAction.EditCueAction.EditName(index, it)) },
                onBpmChange = { dispatch(EditClickTrackAction.EditCueAction.EditBpm(index, it)) },
                onTimeSignatureChange = { dispatch(EditClickTrackAction.EditCueAction.EditTimeSignature(index, it)) },
                onDurationChange = { dispatch(EditClickTrackAction.EditCueAction.EditDuration(index, it)) },
                onDurationTypeChange = { dispatch(EditClickTrackAction.EditCueAction.EditDurationType(index, it)) },
                onPatternChange = { dispatch(EditClickTrackAction.EditCueAction.EditPattern(index, it)) },
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    EditClickTrackScreenView(
        state = EditClickTrackUiState(
            id = ClickTrackId.Database(value = 0),
            name = "Good click track",
            loop = true,
            cues = listOf(
                EditCueUiState(
                    name = "",
                    bpm = 60,
                    timeSignature = TimeSignature(3, 4),
                    duration = CueDuration.Beats(4),
                    pattern = NotePattern.STRAIGHT_X1,
                    errors = emptySet(),
                ),
                EditCueUiState(
                    name = "",
                    bpm = 120,
                    timeSignature = TimeSignature(5, 4),
                    duration = CueDuration.Time(1.minutes),
                    pattern = NotePattern.QUINTUPLET_X2,
                    errors = setOf(EditCueState.Error.BPM),
                ),
            ),
            errors = setOf(EditClickTrackState.Error.NAME),
            showForwardButton = true,
        ),
        dispatch = {},
    )
}
