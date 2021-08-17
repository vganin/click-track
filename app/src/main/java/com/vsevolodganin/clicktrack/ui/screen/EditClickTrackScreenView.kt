package com.vsevolodganin.clicktrack.ui.screen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.vsevolodganin.clicktrack.R
import com.vsevolodganin.clicktrack.lib.CueDuration
import com.vsevolodganin.clicktrack.lib.NotePattern
import com.vsevolodganin.clicktrack.lib.TimeSignature
import com.vsevolodganin.clicktrack.state.redux.EditClickTrackState
import com.vsevolodganin.clicktrack.state.redux.EditCueState
import com.vsevolodganin.clicktrack.state.redux.action.EditClickTrackAction
import com.vsevolodganin.clicktrack.state.redux.core.Dispatch
import com.vsevolodganin.clicktrack.ui.model.EditClickTrackUiState
import com.vsevolodganin.clicktrack.ui.model.EditCueUiState
import com.vsevolodganin.clicktrack.ui.widget.ClickTrackFloatingActionButton
import com.vsevolodganin.clicktrack.ui.widget.CueView
import com.vsevolodganin.clicktrack.ui.widget.GenericTopBarWithBack
import com.vsevolodganin.clicktrack.utils.compose.offset
import com.vsevolodganin.clicktrack.utils.compose.padWithFabSpace
import com.vsevolodganin.clicktrack.utils.compose.swipeToRemove
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

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
            ClickTrackFloatingActionButton(onClick = {
                dispatch(EditClickTrackAction.AddNewCue)
                coroutineScope.launch {
                    awaitFrame()
                    lazyListState.scrollToItem(lazyListState.layoutInfo.totalItemsCount - 1)
                }
            }) {
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
    lazyListState: LazyListState,
    dispatch: Dispatch,
) {
    var moveReorderSourceIndex by remember { mutableStateOf<Int?>(null) }
    var moveReorderTargetIndex by remember { mutableStateOf<Int?>(null) }
    var reorderHeight by remember { mutableStateOf(0f) }

    LazyColumn(
        state = lazyListState,
        modifier = Modifier.fillMaxSize()
    ) {
        stickyHeader {
            TextField(
                value = state.name,
                onValueChange = { dispatch(EditClickTrackAction.EditName(it)) },
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
                    .padding(8.dp)
            ) {
                Row(modifier = Modifier.padding(8.dp)) {
                    Text(text = stringResource(R.string.repeat))
                    Spacer(modifier = Modifier.weight(1f))
                    Checkbox(
                        checked = state.loop,
                        onCheckedChange = { dispatch(EditClickTrackAction.EditLoop(it)) }
                    )
                }
            }
        }

        itemsIndexed(items = state.cues, key = { index, cue -> index to cue.id }) { index, cue ->
            var zIndex by remember { mutableStateOf(0f) }

            BoxWithConstraints(modifier = Modifier.zIndex(zIndex)) {
                val offset = remember { Animatable(0f) }
                val elevation = remember { Animatable(0.dp, Dp.VectorConverter) }

                LaunchedEffect(moveReorderSourceIndex, moveReorderTargetIndex) {
                    val localMoveReorderSourceIndex = moveReorderSourceIndex
                    val localMoveReorderTargetIndex = moveReorderTargetIndex
                    val snap = localMoveReorderSourceIndex == null || localMoveReorderTargetIndex == null
                    val itemIsMoving = localMoveReorderSourceIndex == index

                    val offsetTargetValue = when {
                        localMoveReorderSourceIndex == null || localMoveReorderTargetIndex == null -> 0f
                        index in (localMoveReorderSourceIndex + 1)..localMoveReorderTargetIndex -> -reorderHeight
                        index in localMoveReorderTargetIndex until localMoveReorderSourceIndex -> reorderHeight
                        else -> 0f
                    }
                    val elevationTargetValue = if (itemIsMoving) 12.dp else 2.dp

                    if (snap) {
                        launch { offset.snapTo(targetValue = offsetTargetValue) }
                        launch { elevation.snapTo(targetValue = elevationTargetValue) }
                    } else {
                        launch {
                            offset.animateTo(
                                targetValue = offsetTargetValue,
                                animationSpec = spring()
                            )
                        }
                        launch {
                            elevation.animateTo(
                                targetValue = elevationTargetValue,
                                animationSpec = tween(
                                    durationMillis = 300,
                                    easing = LinearEasing
                                )
                            )
                        }
                    }

                    zIndex = if (itemIsMoving) 1f else 0f
                }

                CueListItem(
                    cue = cue,
                    index = index,
                    elevation = elevation.value,
                    modifier = Modifier
                        .swipeToRemove(
                            constraints = constraints,
                            onDelete = { dispatch(EditClickTrackAction.RemoveCue(index)) }
                        )
                        .moveReorder(
                            position = index,
                            elementsCount = state.cues.size,
                            padding = 8.dp,
                            onBegin = {
                                moveReorderSourceIndex = index
                            },
                            onMove = { targetIndex, height ->
                                moveReorderTargetIndex = targetIndex
                                reorderHeight = height
                            },
                            onDrop = { targetIndex ->
                                dispatch(EditClickTrackAction.MoveCue(index, targetIndex))
                                moveReorderSourceIndex = null
                                moveReorderTargetIndex = null
                            }
                        )
                        .offset(y = { offset.value.roundToInt() }),
                    dispatch = dispatch,
                )
            }
        }

        padWithFabSpace()
    }
}

private fun Modifier.moveReorder(
    position: Int,
    elementsCount: Int,
    padding: Dp, // FIXME: Here because of ordering issues with onSizeChangedPaddingIncluded
    onBegin: () -> Unit,
    onMove: (targetIndex: Int, height: Float) -> Unit,
    onDrop: (targetIndex: Int) -> Unit,
): Modifier = composed {
    val hapticFeedback = LocalHapticFeedback.current

    var height by remember { mutableStateOf(0f) }
    var targetPosition by remember { mutableStateOf(position) }
    val positionOffset = remember { Animatable(0f) }

    this
        .onSizeChanged { height = it.height.toFloat() }
        .padding(padding)
        .clickable(onClick = {})
        .pointerInput(Unit) {
            coroutineScope {
                detectDragGesturesAfterLongPress(
                    onDragStart = {
                        // FIXME(https://issuetracker.google.com/issues/171394805): Not working if global settings disables haptic feedback
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)

                        onBegin()
                    },
                    onDrag = { change, dragDistance ->
                        change.consumeAllChanges()

                        launch {
                            positionOffset.snapTo(positionOffset.value + dragDistance.y)
                        }

                        val newTargetPosition = (position + (positionOffset.value / height).roundToInt())
                            .coerceIn(0 until elementsCount)
                        if (targetPosition != newTargetPosition) {
                            targetPosition = newTargetPosition
                            onMove(newTargetPosition, height)
                        }
                    },
                    onDragEnd = {
                        launch {
                            positionOffset.animateTo(
                                targetValue = (targetPosition - position) * height,
                                animationSpec = spring()
                            )
                            onDrop(targetPosition)
                        }
                    },
                    onDragCancel = {
                        launch {
                            positionOffset.animateTo(
                                targetValue = 0f,
                                animationSpec = spring()
                            )
                            onDrop(position)
                        }
                    },
                )
            }
        }
        .offset(y = { positionOffset.value.roundToInt() })
}

@Composable
private fun CueListItem(
    cue: EditCueUiState,
    index: Int,
    elevation: Dp,
    modifier: Modifier,
    dispatch: Dispatch,
) {
    Card(
        modifier = modifier,
        elevation = elevation
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

@Preview
@Composable
private fun Preview() {
    EditClickTrackScreenView(
        state = EditClickTrackUiState(
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
                    duration = CueDuration.Time(Duration.minutes(1)),
                    pattern = NotePattern.QUINTUPLET_X2,
                    errors = setOf(EditCueState.Error.BPM),
                ),
            ),
            errors = setOf(EditClickTrackState.Error.NAME)
        ),
        dispatch = {},
    )
}
