package com.vsevolodganin.clicktrack.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Card
import androidx.compose.material.ContentAlpha
import androidx.compose.material.FabPosition
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.Bottom
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vsevolodganin.clicktrack.R
import com.vsevolodganin.clicktrack.model.BeatsPerMinuteDiff
import com.vsevolodganin.clicktrack.model.ClickTrackId
import com.vsevolodganin.clicktrack.model.CueDuration
import com.vsevolodganin.clicktrack.model.NotePattern
import com.vsevolodganin.clicktrack.model.TimeSignature
import com.vsevolodganin.clicktrack.redux.EditClickTrackState
import com.vsevolodganin.clicktrack.redux.EditCueState
import com.vsevolodganin.clicktrack.redux.action.BackstackAction
import com.vsevolodganin.clicktrack.redux.action.EditClickTrackAction
import com.vsevolodganin.clicktrack.redux.core.Dispatch
import com.vsevolodganin.clicktrack.ui.ClickTrackTheme
import com.vsevolodganin.clicktrack.ui.model.EditClickTrackUiState
import com.vsevolodganin.clicktrack.ui.model.EditCueUiState
import com.vsevolodganin.clicktrack.ui.piece.Checkbox
import com.vsevolodganin.clicktrack.ui.piece.CueView
import com.vsevolodganin.clicktrack.ui.piece.ExpandableChevron
import com.vsevolodganin.clicktrack.ui.piece.FloatingActionButton
import com.vsevolodganin.clicktrack.ui.piece.TopAppBarWithBack
import com.vsevolodganin.clicktrack.utils.compose.StatefulTextField
import com.vsevolodganin.clicktrack.utils.compose.SwipeToDelete
import com.vsevolodganin.clicktrack.utils.compose.padWithFabSpace
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
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
        topBar = {
            TopAppBarWithBack(
                dispatch = dispatch,
                title = { Text(stringResource(R.string.edit_click_track)) },
                actions = {
                    if (state.showForwardButton) {
                        IconButton(onClick = { dispatch(BackstackAction.ToClickTrackScreen(state.id)) }) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null)
                        }
                    }
                }
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    dispatch(EditClickTrackAction.AddNewCue)
                    coroutineScope.launch {
                        awaitFrame()
                        lazyListState.scrollToItem(lazyListState.layoutInfo.totalItemsCount - 1)
                    }
                }
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
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
            OptionsItem(
                loop = state.loop,
                tempoDiff = state.tempoDiff,
                contentPadding = contentPadding,
                dispatch = dispatch,
            )
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
private fun OptionsItem(
    loop: Boolean,
    tempoDiff: BeatsPerMinuteDiff,
    contentPadding: Dp,
    dispatch: Dispatch
) {
    var optionsExpanded by rememberSaveable { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(contentPadding)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = CenterVertically,
            ) {
                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = stringResource(R.string.click_track_options),
                )

                Spacer(modifier = Modifier.weight(1f))

                IconButton(
                    onClick = { optionsExpanded = !optionsExpanded },
                ) {
                    ExpandableChevron(isExpanded = optionsExpanded)
                }
            }

            AnimatedVisibility(
                visible = optionsExpanded,
                enter = fadeIn() + expandVertically(expandFrom = Bottom),
                exit = fadeOut() + shrinkVertically(shrinkTowards = Bottom),
            ) {
                Column {
                    LoopItem(loop, dispatch)
                    TempoDiffItem(tempoDiff, dispatch)
                }
            }
        }
    }
}

@Composable
private fun LoopItem(
    loop: Boolean,
    dispatch: Dispatch
) {
    Row {
        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = stringResource(R.string.repeat),
            modifier = Modifier.align(CenterVertically)
        )

        Spacer(modifier = Modifier.weight(1f))

        Checkbox(
            checked = loop,
            onCheckedChange = { dispatch(EditClickTrackAction.EditLoop(it)) }
        )
    }
}

@Composable
private fun TempoDiffItem(
    tempoDiff: BeatsPerMinuteDiff,
    dispatch: Dispatch,
) {
    Row(
        modifier = Modifier.fillMaxWidth(), horizontalArrangement = SpaceBetween, verticalAlignment = CenterVertically
    ) {
        IconButton(onClick = { dispatch(EditClickTrackAction.DecrementTempoDiff) }) {
            Icon(imageVector = Icons.Default.Remove, contentDescription = null)
        }

        Text(
            text = tempoDiffText(tempoDiff),
            modifier = Modifier.align(CenterVertically),
            color = LocalContentColor.current.copy(alpha = ContentAlpha.medium),
        )

        IconButton(onClick = { dispatch(EditClickTrackAction.IncrementTempoDiff) }) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null)
        }
    }
}

@Composable
private fun tempoDiffText(tempoDiff: BeatsPerMinuteDiff): String {
    val sign = remember(tempoDiff) {
        when {
            tempoDiff.value < 0 -> "-"
            else -> "+"
        }
    }
    val number = remember(tempoDiff) { tempoDiff.value.absoluteValue }
    return stringResource(id = R.string.click_track_tempo_diff, sign, number)
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

@ScreenPreviews
@Composable
private fun Preview() = ClickTrackTheme {
    EditClickTrackScreenView(
        state = EditClickTrackUiState(
            id = ClickTrackId.Database(value = 0),
            name = "Good click track",
            loop = true,
            tempoDiff = BeatsPerMinuteDiff(-3),
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
