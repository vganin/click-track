package com.vsevolodganin.clicktrack.view.widget

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculateCentroidSize
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.positionChangeConsumed
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastAny
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastForEachIndexed
import com.vsevolodganin.clicktrack.lib.ClickTrack
import com.vsevolodganin.clicktrack.lib.interval
import com.vsevolodganin.clicktrack.utils.compose.AnimatableFloat
import com.vsevolodganin.clicktrack.utils.compose.AnimatableViewport
import com.vsevolodganin.clicktrack.utils.compose.awaitLongPressOrCancellation
import com.vsevolodganin.clicktrack.view.preview.PREVIEW_CLICK_TRACK_1
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ClickTrackView(
    clickTrack: ClickTrack,
    modifier: Modifier = Modifier,
    drawAllBeatsMarks: Boolean = false,
    drawTextMarks: Boolean = true,
    progress: Double? = null,
    progressDragAndDropEnabled: Boolean = false,
    onProgressDragStart: () -> Unit = {},
    onProgressDrop: (Double) -> Unit = {},
    viewportPanEnabled: Boolean = false,
    defaultLineWidth: Float = Stroke.HairlineWidth,
) {
    BoxWithConstraints(modifier = modifier) {
        val width = minWidth
        val height = minHeight
        val widthPx = with(LocalDensity.current) { width.toPx() }
        val heightPx = with(LocalDensity.current) { height.toPx() }
        val marks = clickTrack.asMarks(widthPx, drawAllBeatsMarks)

        var isProgressCaptured by remember { mutableStateOf(false) }
        val progressLineWidth = remember { Animatable(defaultLineWidth) }
        LaunchedEffect(isProgressCaptured) {
            val newProgressLineWidth = when (isProgressCaptured) {
                true -> PROGRESS_LINE_WIDTH_CAPTURED
                false -> defaultLineWidth
            }
            val animSpec: AnimationSpec<Float> = when (isProgressCaptured) {
                true -> spring(
                    dampingRatio = Spring.DampingRatioHighBouncy,
                    stiffness = Spring.StiffnessLow
                )
                false -> spring()
            }

            progressLineWidth.animateTo(
                targetValue = newProgressLineWidth,
                animationSpec = animSpec
            )
        }

        val progressPosition = progress?.let {
            animatedProgressPosition(
                clickTrack = clickTrack,
                progress = progress,
                totalWidthPx = widthPx,
            )
        }

        val playbackStampColor = MaterialTheme.colors.secondaryVariant

        val bounds = remember { Rect(0f, 0f, widthPx, heightPx) }
        val viewportState = remember { AnimatableViewport(bounds) }
        val viewportTransformations = viewportState.transformations
        val scaleX = viewportTransformations.scaleX
        val scaleY = viewportTransformations.scaleY
        val translateX = viewportTransformations.translateX
        val translateY = viewportTransformations.translateY

        fun Float.transformXAndPixelAlign(): Float {
            return ((this + translateX) * scaleX).roundToInt().toFloat()
        }

        val transformedAndPixelAlignedMarks = marks.map { mark ->
            mark.copy(x = mark.x.transformXAndPixelAlign())
        }

        Box(
            modifier = Modifier
                .clickTrackGestures(
                    viewportZoomAndPanEnabled = viewportPanEnabled,
                    progressDragAndDropEnabled = progressDragAndDropEnabled,
                    viewportState = viewportState,
                    progressPosition = progressPosition,
                    onProgressDragStart = { progress ->
                        isProgressCaptured = true
                        progress.stop()
                        onProgressDragStart()
                    },
                    onProgressDrop = { progress ->
                        isProgressCaptured = false
                        onProgressDrop(progress.value.toDouble() / widthPx)
                    }
                )
        ) {
            Canvas(modifier = Modifier.size(width, height)) {
                withTransform(
                    transformBlock = {
                        // Transform only Y because X transformation needs post pixel alignment
                        scale(1f, scaleY, Offset(0f, 0f))
                        translate(0f, translateY)
                    },
                    drawBlock = {
                        for (mark in transformedAndPixelAlignedMarks) {
                            val markX = mark.x
                            drawLine(
                                color = mark.color,
                                strokeWidth = defaultLineWidth,
                                start = Offset(markX, 0f),
                                end = Offset(markX, size.height),
                            )
                        }

                        if (progressPosition != null) {
                            val progressX = progressPosition.value.transformXAndPixelAlign()
                            drawLine(
                                color = playbackStampColor,
                                strokeWidth = progressLineWidth.value,
                                start = Offset(progressX, 0f),
                                end = Offset(progressX, size.height)
                            )
                        }
                    }
                )
            }

            if (drawTextMarks) {
                Layout(
                    modifier = Modifier.wrapContentSize(),
                    content = {
                        for (mark in transformedAndPixelAlignedMarks) {
                            val layoutIdModifier = Modifier.layoutId(mark)
                            mark.briefSummary?.invoke(layoutIdModifier)
                            mark.detailedSummary?.invoke(layoutIdModifier)
                        }
                    },
                    measurePolicy = { measurables, constraints ->
                        val placeables = measurables.groupBy({ it.layoutId as Mark }) { measurable ->
                            measurable.measure(constraints)
                        }.toSortedMap { lhs, rhs -> lhs.x.compareTo(rhs.x) }.toList()

                        layout(constraints.maxWidth, constraints.maxHeight) {
                            var currentEndX = Float.NEGATIVE_INFINITY
                            var currentEndY = 0f

                            placeables
                                .fastForEachIndexed { index, (mark, summaries) ->
                                    val markX = mark.x

                                    val firstSummary = summaries.firstOrNull() ?: return@fastForEachIndexed

                                    if (markX < currentEndX) {
                                        currentEndY += firstSummary.height
                                    } else {
                                        currentEndY = 0f
                                    }

                                    val nextMarkX = transformedAndPixelAlignedMarks.getOrNull(index + 1)?.x ?: Float.POSITIVE_INFINITY

                                    val summariesSortedByDescendingWidth = summaries.sortedByDescending(Placeable::width)
                                    val placeable = summariesSortedByDescendingWidth
                                        .firstOrNull { placeableCandidate ->
                                            markX + placeableCandidate.width < nextMarkX
                                        }
                                        ?: summariesSortedByDescendingWidth.last()

                                    placeable.placeRelative(x = markX.toInt(), y = currentEndY.toInt())

                                    currentEndX = markX + placeable.width
                                }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun animatedProgressPosition(
    clickTrack: ClickTrack,
    progress: Double,
    totalWidthPx: Float,
): AnimatableFloat {
    val playbackStampX = remember {
        Animatable(0f).apply {
            updateBounds(0f, totalWidthPx)
        }
    }

    val totalTrackDuration = clickTrack.durationInTime
    val targetPosition = totalTrackDuration * progress

    LaunchedEffect(progress) {
        val targetPositionX = targetPosition.toX(totalTrackDuration, totalWidthPx)
        playbackStampX.snapTo(targetPositionX)
    }

    LaunchedEffect(progress, totalTrackDuration) {
        val animationDuration = totalTrackDuration - targetPosition
        playbackStampX.animateTo(
            targetValue = totalWidthPx,
            animationSpec = tween(
                durationMillis = animationDuration.coerceAtLeast(Duration.ZERO).toLongMilliseconds().toInt(),
                easing = LinearEasing
            )
        )
    }

    return playbackStampX
}

private data class Mark(
    val x: Float,
    val color: Color,
    val briefSummary: (@Composable (modifier: Modifier) -> Unit)?,
    val detailedSummary: (@Composable (modifier: Modifier) -> Unit)?,
)

@Composable
private fun ClickTrack.asMarks(width: Float, drawAllBeatsMarks: Boolean): List<Mark> {
    val result = mutableListOf<Mark>()
    val duration = durationInTime

    var currentTimestamp = Duration.ZERO
    var currentX = 0f
    for (cue in cues) {
        result += Mark(
            x = currentX,
            color = MaterialTheme.colors.onSurface,
            briefSummary = { modifier ->
                CueSummary(cue, modifier)
            },
            detailedSummary = cue.name?.let { name ->
                { modifier ->
                    Column(modifier) {
                        Text(
                            text = "\"$name\"",
                            style = TextStyle(fontWeight = FontWeight.Bold),
                            fontSize = 14.sp,
                        )
                        CueSummary(cue)
                    }
                }
            }
        )
        if (drawAllBeatsMarks) {
            for (i in 1 until cue.timeSignature.noteCount) {
                result += Mark(
                    x = (currentTimestamp + cue.bpm.interval * i).toX(duration, width),
                    color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium),
                    briefSummary = null,
                    detailedSummary = null,
                )
            }
        }
        val nextTimestamp = currentTimestamp + cue.durationAsTime
        currentX = nextTimestamp.toX(duration, width)
        currentTimestamp = nextTimestamp
    }

    return result.distinctBy(Mark::x)
}

private fun Duration.toX(totalDuration: Duration, viewWidth: Float): Float {
    return (this / totalDuration * viewWidth).toFloat()
}

private fun Modifier.clickTrackGestures(
    viewportZoomAndPanEnabled: Boolean,
    progressDragAndDropEnabled: Boolean,
    viewportState: AnimatableViewport,
    progressPosition: AnimatableFloat?,
    onProgressDragStart: suspend (progress: AnimatableFloat) -> Unit,
    onProgressDrop: suspend (progress: AnimatableFloat) -> Unit,
): Modifier = composed {
    val hapticFeedback by rememberUpdatedState(LocalHapticFeedback.current)
    val flingCoroutineScope = rememberCoroutineScope()

    pointerInput(
        viewportZoomAndPanEnabled,
        progressDragAndDropEnabled,
        viewportState,
        progressPosition,
    ) {
        while (true) {
            coroutineScope {
                val currentViewportTransformations = viewportState.transformations

                var dragAndDropGesture: Job? = null
                var zoomAndPanGesture: Job? = null

                if (progressDragAndDropEnabled && progressPosition != null) {
                    dragAndDropGesture = launch {
                        val down = awaitPointerEventScope {
                            awaitFirstDown(requireUnconsumed = false)
                        }

                        val drag = awaitLongPressOrCancellation(down)

                        if (drag != null) {
                            try {
                                zoomAndPanGesture?.cancel()

                                // FIXME(https://issuetracker.google.com/issues/171394805): Not working if global settings disables haptic feedback
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)

                                onProgressDragStart(progressPosition)

                                awaitPointerEventScope {
                                    drag(down.id) {
                                        val snapTo = progressPosition.value +
                                                it.positionChange().x / currentViewportTransformations.scaleX
                                        it.consumePositionChange()
                                        launch { progressPosition.snapTo(snapTo) }
                                    }
                                }
                            } finally {
                                withContext(NonCancellable) {
                                    onProgressDrop(progressPosition)
                                }
                            }
                        }
                    }
                }

                if (viewportZoomAndPanEnabled) {
                    zoomAndPanGesture = launch {
                        awaitPointerEventScope {
                            val zoomChangeEpsilon = 0.01f
                            var zoom = 1f
                            var pan = Offset.Zero
                            var pastTouchSlop = false
                            val touchSlop = viewConfiguration.touchSlop
                            val velocityTracker = VelocityTracker()

                            awaitFirstDown(requireUnconsumed = false)
                            do {
                                val event = awaitPointerEvent()
                                val canceled = event.changes.fastAny { it.positionChangeConsumed() }
                                if (!canceled) {
                                    val zoomChange = event.calculateZoom()
                                    val panChange = event.calculatePan()

                                    if (!pastTouchSlop) {
                                        zoom *= zoomChange
                                        pan += panChange

                                        val centroidSize = event.calculateCentroidSize(useCurrent = false)
                                        val zoomMotion = abs(1 - zoom) * centroidSize
                                        val panMotion = pan.getDistance()

                                        if (zoomMotion > touchSlop ||
                                            panMotion > touchSlop
                                        ) {
                                            pastTouchSlop = true
                                        }
                                    }

                                    if (pastTouchSlop) {
                                        dragAndDropGesture?.cancel()

                                        val centroid = event.calculateCentroid(useCurrent = false)
                                        if (zoomChange != 1f || panChange != Offset.Zero) {
                                            val viewport = viewportState.value
                                            val bounds = viewportState.bounds

                                            val newWidth = viewport.width / zoomChange
                                            val newLeft = viewport.left -
                                                    (newWidth - viewport.width) * (centroid.x - bounds.left) / bounds.width

                                            velocityTracker.addPosition(
                                                timeMillis = event.changes.firstOrNull { it.pressed }?.uptimeMillis ?: 0L,
                                                position = centroid
                                            )

                                            launch {
                                                viewportState.apply {
                                                    snapTo(
                                                        newLeft = newLeft,
                                                        newTop = viewport.top,
                                                        newRight = newLeft + newWidth,
                                                        newBottom = viewport.bottom
                                                    )
                                                    translate(-panChange.copy(y = 0f) / currentViewportTransformations.scaleX)
                                                }
                                            }
                                        }
                                        event.changes.fastForEach {
                                            if (it.positionChanged()) {
                                                it.consumeAllChanges()
                                            }
                                        }
                                    }
                                }
                            } while (!canceled && event.changes.fastAny { it.pressed })

                            flingCoroutineScope.launch {
                                if (abs(zoom - 1f) < zoomChangeEpsilon) {
                                    val velocity = -velocityTracker.calculateVelocity().run { Offset(x, y) } /
                                            currentViewportTransformations.scaleX
                                    viewportState.animateDecay(velocity)
                                }
                            }
                        }
                    }
                }

                // FIXME(https://issuetracker.google.com/issues/180032122): The app crashes without any await
                if (dragAndDropGesture?.isCancelled != false || zoomAndPanGesture?.isCancelled != false) {
                    awaitPointerEventScope {
                        awaitPointerEvent()
                    }
                }
            }
        }
    }
}

private val AnimatableViewport.transformations
    get() = object {
        val scaleX: Float
        val scaleY: Float
        val translateX: Float
        val translateY: Float

        init {
            val rect = value
            scaleX = bounds.width / rect.width
            scaleY = bounds.height / rect.height
            translateX = -rect.left
            translateY = -rect.top
        }
    }

private const val PROGRESS_LINE_WIDTH_CAPTURED = 10f

@Preview
@Composable
fun PreviewClickTrackView() {
    ClickTrackView(
        clickTrack = PREVIEW_CLICK_TRACK_1.value,
        drawTextMarks = true,
        progress = 0.13,
        modifier = Modifier.fillMaxSize()
    )
}
