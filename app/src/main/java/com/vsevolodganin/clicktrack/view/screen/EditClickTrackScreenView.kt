package com.vsevolodganin.clicktrack.view.screen

import androidx.compose.animation.animatedFloat
import androidx.compose.animation.animatedValue
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.material.FabPosition
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.gesture.LongPressDragObserver
import androidx.compose.ui.gesture.longPressDragGestureFilter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.AmbientHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.vsevolodganin.clicktrack.R
import com.vsevolodganin.clicktrack.lib.BuiltinClickSounds
import com.vsevolodganin.clicktrack.lib.ClickTrack
import com.vsevolodganin.clicktrack.lib.Cue
import com.vsevolodganin.clicktrack.lib.CueDuration
import com.vsevolodganin.clicktrack.lib.SerializableDuration
import com.vsevolodganin.clicktrack.lib.TimeSignature
import com.vsevolodganin.clicktrack.lib.bpm
import com.vsevolodganin.clicktrack.model.ClickTrackWithId
import com.vsevolodganin.clicktrack.redux.Dispatch
import com.vsevolodganin.clicktrack.state.EditClickTrackScreenState
import com.vsevolodganin.clicktrack.state.actions.NavigateBack
import com.vsevolodganin.clicktrack.state.actions.StoreUpdateClickTrack
import com.vsevolodganin.clicktrack.utils.compose.ObservableMutableState
import com.vsevolodganin.clicktrack.utils.compose.observableMutableStateOf
import com.vsevolodganin.clicktrack.utils.compose.offset
import com.vsevolodganin.clicktrack.utils.compose.swipeToRemove
import com.vsevolodganin.clicktrack.utils.compose.toObservableMutableStateList
import com.vsevolodganin.clicktrack.view.common.Constants.FAB_SIZE_WITH_PADDINGS
import com.vsevolodganin.clicktrack.view.widget.EditCueWithDurationView
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.time.minutes

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
            StoreUpdateClickTrack(
                clickTrack = state.clickTrack.copy(
                    value = state.clickTrack.value.copy(
                        name = nameState.value,
                        loop = loopState.value,
                        cues = cuesState.map(ObservableMutableState<Cue>::value),
                    )
                )
            )
        )
    }

    DisposableEffect(Unit) {
        nameState.observe { update() }
        loopState.observe { update() }
        cuesState.observe { update() }
        cuesState.forEach { it.observe { update() } }
        onDispose { }
    }

    val coroutineScope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()

    Scaffold(
        topBar = { EditClickTrackScreenTopBar(dispatch) },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            FloatingActionButton(onClick = {
                cuesState += observableMutableStateOf(state.defaultCue).observe { update() }
                coroutineScope.launch {
                    // FIXME: Wait for upcoming `scrollTo` API
                    lazyListState.snapToItemIndex(cuesState.lastIndex)
                }
            }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        },
        modifier = modifier,
    ) {
        EditClickTrackScreenContent(nameState, state.isErrorInName, loopState, cuesState, lazyListState)
    }
}

@Composable
private fun EditClickTrackScreenContent(
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
                modifier = Modifier.fillMaxWidth(),
                value = nameState.value,
                onValueChange = { nameState.value = it },
                placeholder = { Text(text = stringResource(R.string.click_track_name_hint)) },
                textStyle = MaterialTheme.typography.h6,
                isErrorValue = isErrorInName,
                backgroundColor = MaterialTheme.colors.surface
            )
        }

        item {
            Box(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier
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

        itemsIndexed(items = cuesState) { index, cueState ->
            key(index, cueState) {
                var zIndex by remember { mutableStateOf(0f) }

                BoxWithConstraints(modifier = Modifier.zIndex(zIndex)) {
                    val offset = animatedFloat(0f)
                    val elevation = animatedValue(0.dp, Dp.VectorConverter)

                    DisposableEffect(moveReorderSourceIndex, moveReorderTargetIndex) {
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
                            offset.snapTo(targetValue = offsetTargetValue)
                            elevation.snapTo(targetValue = elevationTargetValue)
                        } else {
                            offset.animateTo(
                                targetValue = offsetTargetValue,
                                anim = spring()
                            )
                            elevation.animateTo(
                                targetValue = elevationTargetValue,
                                anim = tween(
                                    durationMillis = 300,
                                    easing = LinearEasing
                                )
                            )
                        }

                        zIndex = if (itemIsMoving) 1f else 0f

                        onDispose {}
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
        }

        item {
            Spacer(modifier = Modifier.size(FAB_SIZE_WITH_PADDINGS))
        }
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
    val hapticFeedback = AmbientHapticFeedback.current

    var height by remember { mutableStateOf(0f) }
    var targetPosition by remember { mutableStateOf(position) }
    val positionOffset = animatedFloat(0f)

    val longPressDragObserver = remember {
        object : LongPressDragObserver {
            override fun onLongPress(pxPosition: Offset) {
                // FIXME(https://issuetracker.google.com/issues/171394805): Not working if global settings disables haptic feedback
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)

                onBegin()
            }

            override fun onDrag(dragDistance: Offset): Offset {
                positionOffset.snapTo(positionOffset.value + dragDistance.y)

                val newTargetPosition = (position + (positionOffset.value / height).roundToInt())
                    .coerceIn(0 until elementsCount)
                if (targetPosition != newTargetPosition) {
                    targetPosition = newTargetPosition
                    onMove(newTargetPosition, height)
                }

                return dragDistance
            }

            override fun onStop(velocity: Offset) {
                positionOffset.animateTo(
                    targetValue = (targetPosition - position) * height,
                    anim = spring()
                ) { _, _ ->
                    onDrop(targetPosition)
                }
            }

            override fun onCancel() {
                positionOffset.animateTo(
                    targetValue = 0f,
                    anim = spring()
                ) { _, _ ->
                    onDrop(position)
                }
            }
        }
    }

    this
        .onSizeChanged { height = it.height.toFloat() }
        .padding(padding)
        .clickable(onClick = {})
        .longPressDragGestureFilter(longPressDragObserver)
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

@Composable
private fun EditClickTrackScreenTopBar(dispatch: Dispatch) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = { dispatch(NavigateBack) }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
            }
        },
        title = { Text(text = stringResource(id = R.string.edit_click_track)) }
    )
}

@Preview
@Composable
fun PreviewEditClickTrackScreenView() {
    EditClickTrackScreenView(
        state = EditClickTrackScreenState(
            clickTrack = ClickTrackWithId(
                id = 0,
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
                            duration = CueDuration.Time(SerializableDuration(1.minutes)),
                        ),
                    ),
                    loop = true,
                    sounds = BuiltinClickSounds,
                )
            ),
            isErrorInName = false,
        ),
    )
}
