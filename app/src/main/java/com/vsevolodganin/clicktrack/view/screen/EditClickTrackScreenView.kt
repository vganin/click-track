package com.vsevolodganin.clicktrack.view.screen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Card
import androidx.compose.material.FabPosition
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
import com.vsevolodganin.clicktrack.lib.ClickTrack
import com.vsevolodganin.clicktrack.lib.Cue
import com.vsevolodganin.clicktrack.lib.CueDuration
import com.vsevolodganin.clicktrack.lib.TimeSignature
import com.vsevolodganin.clicktrack.lib.bpm
import com.vsevolodganin.clicktrack.model.ClickTrackId
import com.vsevolodganin.clicktrack.model.ClickTrackWithId
import com.vsevolodganin.clicktrack.redux.Dispatch
import com.vsevolodganin.clicktrack.state.EditClickTrackScreenState
import com.vsevolodganin.clicktrack.state.actions.ClickTrackAction
import com.vsevolodganin.clicktrack.utils.compose.ObservableMutableState
import com.vsevolodganin.clicktrack.utils.compose.observableMutableStateOf
import com.vsevolodganin.clicktrack.utils.compose.offset
import com.vsevolodganin.clicktrack.utils.compose.padWithFabSpace
import com.vsevolodganin.clicktrack.utils.compose.swipeToRemove
import com.vsevolodganin.clicktrack.utils.compose.toObservableMutableStateList
import com.vsevolodganin.clicktrack.view.widget.ClickTrackFloatingActionButton
import com.vsevolodganin.clicktrack.view.widget.EditCueWithDurationView
import com.vsevolodganin.clicktrack.view.widget.GenericTopBarWithBack
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun EditClickTrackScreenView(
    state: EditClickTrackScreenState,
    modifier: Modifier = Modifier,
    dispatch: Dispatch = Dispatch {},
) {
    val nameState = remember { observableMutableStateOf(state.clickTrack.value.name) }
    val loopState = remember { observableMutableStateOf(state.clickTrack.value.loop) }
    val cuesState = remember { state.clickTrack.value.cues.map(::observableMutableStateOf).toObservableMutableStateList() }

    fun update() {
        dispatch(
            ClickTrackAction.UpdateClickTrack(
                data = state.clickTrack.copy(
                    value = state.clickTrack.value.copy(
                        name = nameState.value,
                        loop = loopState.value,
                        cues = cuesState.map(ObservableMutableState<Cue>::value),
                    )
                ),
                shouldStore = true
            )
        )
    }

    LaunchedEffect(Unit) {
        nameState.observe { update() }
        loopState.observe { update() }
        cuesState.observe { update() }
        cuesState.forEach { it.observe { update() } }
    }

    val coroutineScope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()

    Scaffold(
        topBar = { GenericTopBarWithBack(R.string.edit_click_track, dispatch) },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            ClickTrackFloatingActionButton(onClick = {
                cuesState += observableMutableStateOf(state.defaultCue).observe { update() }
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
        Content(nameState, state.isErrorInName, loopState, cuesState, lazyListState)
    }
}

@Composable
private fun Content(
    nameState: MutableState<String>,
    isErrorInName: Boolean,
    loopState: MutableState<Boolean>,
    cuesState: MutableList<ObservableMutableState<Cue>>,
    lazyListState: LazyListState,
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
                value = nameState.value,
                onValueChange = { nameState.value = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(text = stringResource(R.string.click_track_name_hint)) },
                textStyle = MaterialTheme.typography.h6,
                isError = isErrorInName,
                colors = TextFieldDefaults.textFieldColors(backgroundColor = MaterialTheme.colors.surface)
            )
        }

        item {
            Box(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.Center)
                ) {
                    Text(text = stringResource(R.string.repeat))
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(checked = loopState.value, onCheckedChange = {
                        loopState.value = !loopState.value
                    })
                }
            }
        }

        itemsIndexed(items = cuesState, key = { index, item -> index to item }) { index, cueState ->
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
                    state = cueState,
                    elevation = elevation.value,
                    modifier = Modifier
                        .swipeToRemove(
                            constraints = constraints,
                            onDelete = {
                                cuesState.removeAt(index)
                            }
                        )
                        .moveReorder(
                            position = index,
                            elementsCount = cuesState.size,
                            padding = 8.dp,
                            onBegin = {
                                moveReorderSourceIndex = index
                            },
                            onMove = { targetPosition, height ->
                                moveReorderTargetIndex = targetPosition
                                reorderHeight = height
                            },
                            onDrop = { targetPosition ->
                                cuesState.add(targetPosition, cuesState[index].also { cuesState.removeAt(index) })
                                moveReorderSourceIndex = null
                                moveReorderTargetIndex = null
                            }
                        )
                        .offset(y = { offset.value.roundToInt() })
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
    onMove: (targetPosition: Int, height: Float) -> Unit,
    onDrop: (targetPosition: Int) -> Unit,
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
    state: MutableState<Cue>,
    elevation: Dp,
    modifier: Modifier,
) {
    Card(
        modifier = modifier,
        elevation = elevation
    ) {
        EditCueWithDurationView(state)
    }
}

@Preview
@Composable
private fun Preview() {
    EditClickTrackScreenView(
        state = EditClickTrackScreenState(
            clickTrack = ClickTrackWithId(
                id = ClickTrackId.Database(0),
                value = ClickTrack(
                    name = "Good click track",
                    cues = listOf(
                        Cue(
                            bpm = 60.bpm,
                            timeSignature = TimeSignature(3, 4),
                            duration = CueDuration.Beats(4),
                        ),
                        Cue(
                            bpm = 120.bpm,
                            timeSignature = TimeSignature(5, 4),
                            duration = CueDuration.Time(Duration.minutes(1)),
                        ),
                    ),
                    loop = true,
                )
            ),
            isErrorInName = false,
        ),
    )
}
