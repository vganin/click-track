package com.vsevolodganin.clicktrack.ui.widget

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import com.vsevolodganin.clicktrack.lib.ClickTrack
import com.vsevolodganin.clicktrack.lib.interval
import com.vsevolodganin.clicktrack.model.PlayableProgress
import com.vsevolodganin.clicktrack.ui.preview.PREVIEW_CLICK_TRACK_1
import com.vsevolodganin.clicktrack.utils.compose.AnimatableFloat
import com.vsevolodganin.clicktrack.utils.compose.AnimatableViewport
import com.vsevolodganin.clicktrack.utils.compose.detectTransformGesturesWithEndCallbacks
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@Composable
fun ClickTrackView(
    clickTrack: ClickTrack,
    modifier: Modifier = Modifier,
    drawAllBeatsMarks: Boolean = false,
    drawTextMarks: Boolean = true,
    progress: PlayableProgress? = null,
    progressDragAndDropEnabled: Boolean = false,
    onProgressDragStart: () -> Unit = {},
    onProgressDrop: (Double) -> Unit = {},
    viewportPanEnabled: Boolean = false,
    defaultLineWidth: Float = Stroke.HairlineWidth,
) {
    BoxWithConstraints(modifier = modifier) {
        // Bounds
        val width = minWidth
        val height = minHeight
        val widthPx = with(LocalDensity.current) { width.toPx() }
        val heightPx = with(LocalDensity.current) { height.toPx() }

        // Progress
        var isProgressCaptured by remember { mutableStateOf(false) }
        val progressLineWidth by progressLineWidth(defaultLineWidth, isProgressCaptured)
        val progressLinePosition = progressLinePosition(progress, widthPx)
        val progressLineColor = MaterialTheme.colors.secondaryVariant

        // Camera
        val bounds = remember { Rect(0f, 0f, widthPx, heightPx) }
        val viewportState = remember { AnimatableViewport(bounds) }
        val viewportTransformations = viewportState.transformations
        val scaleX = viewportTransformations.scaleX
        val scaleY = viewportTransformations.scaleY
        val translateX = viewportTransformations.translateX
        val translateY = viewportTransformations.translateY

        // Marks
        val marks = clickTrack.asMarks(widthPx, drawAllBeatsMarks)
        fun Float.transformXAndPixelAlign(): Float {
            return ((this + translateX) * scaleX).roundToInt().toFloat()
        }

        val transformedAndPixelAlignedMarks = remember(marks, translateX, scaleX) {
            marks.map { mark -> mark.copy(x = mark.x.transformXAndPixelAlign()) }
        }

        Canvas(
            modifier = Modifier
                .clickTrackGestures(
                    viewportZoomAndPanEnabled = viewportPanEnabled,
                    progressDragAndDropEnabled = progressDragAndDropEnabled,
                    viewportState = viewportState,
                    progressPosition = progressLinePosition,
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
                .size(width, height)
        ) {
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

                    if (progressLinePosition != null) {
                        val progressX = progressLinePosition.value.transformXAndPixelAlign()
                        drawLine(
                            color = progressLineColor,
                            strokeWidth = progressLineWidth,
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
                        mark.summary?.let { summary ->
                            Box(modifier = Modifier.layoutId(mark)) {
                                summary()
                            }
                        }
                    }
                },
                measurePolicy = { measurables, constraints ->
                    val placeables = measurables.associate { measurable ->
                        measurable.layoutId as Mark to measurable.measure(Constraints())
                    }.toSortedMap { lhs, rhs -> lhs.x.compareTo(rhs.x) }.toList()

                    layout(constraints.maxWidth, constraints.maxHeight) {
                        class Border(val x: Int, val endY: Int)

                        val borderMap = sortedMapOf<Int, Border>()

                        placeables
                            .forEach { (mark, placeable) ->
                                val x = mark.x.roundToInt()

                                var startY = 0
                                var endY = placeable.height
                                while (true) {
                                    val menacingBorders = borderMap.subMap(startY, endY)
                                    val menacingBorder = menacingBorders.values.maxByOrNull { it.x }
                                    if (menacingBorder != null && menacingBorder.x >= x) {
                                        startY = menacingBorder.endY + 1
                                        endY = startY + placeable.height
                                    } else {
                                        break
                                    }
                                }

                                placeable.placeRelative(x = x, y = startY)

                                borderMap.subMap(startY, endY).clear()
                                borderMap[startY] = Border(x + placeable.width, endY)
                            }
                    }
                }
            )
        }
    }
}

@Composable
private fun progressLinePosition(
    progress: PlayableProgress?,
    totalWidthPx: Float,
): AnimatableFloat? {
    progress ?: return null

    val animatableProgressLinePosition = remember { Animatable(0f) }
        .apply {
            updateBounds(0f, totalWidthPx)
        }

    var cachedProgress by remember { mutableStateOf(progress) }

    LaunchedEffect(progress, totalWidthPx) {
        val progressTimePosition = progress.value + progress.generationTimeMark.elapsedNow()
        val progressXPosition = progressTimePosition.toX(progress.duration, totalWidthPx)
        val animationDuration = progress.duration - progressTimePosition

        if (progress.value <= cachedProgress.value) {
            cachedProgress = progress
            animatableProgressLinePosition.snapTo(progressXPosition)
        }

        animatableProgressLinePosition.animateTo(
            targetValue = totalWidthPx,
            animationSpec = tween(
                durationMillis = animationDuration.coerceAtLeast(Duration.ZERO).inWholeMilliseconds.toInt(),
                easing = LinearEasing
            )
        )
    }

    return animatableProgressLinePosition
}

@Composable
private fun progressLineWidth(
    defaultWidth: Float,
    isProgressCaptured: Boolean,
): State<Float> {
    val animatableWidth = remember { Animatable(defaultWidth) }

    LaunchedEffect(isProgressCaptured) {
        val newProgressLineWidth = when (isProgressCaptured) {
            true -> PROGRESS_LINE_WIDTH_CAPTURED
            false -> defaultWidth
        }
        val animSpec: AnimationSpec<Float> = when (isProgressCaptured) {
            true -> spring(
                dampingRatio = Spring.DampingRatioHighBouncy,
                stiffness = Spring.StiffnessLow
            )
            false -> spring()
        }

        animatableWidth.animateTo(
            targetValue = newProgressLineWidth,
            animationSpec = animSpec
        )
    }

    return animatableWidth.asState()
}

private data class Mark(
    val x: Float,
    val color: Color,
    val summary: (@Composable () -> Unit)?,
)

@Composable
private fun ClickTrack.asMarks(width: Float, drawAllBeatsMarks: Boolean): List<Mark> {
    val primaryMarkColor = MaterialTheme.colors.onSurface
    val secondaryMarkColor = primaryMarkColor.copy(alpha = ContentAlpha.medium)

    return remember(cues, width, drawAllBeatsMarks) {
        val result = mutableListOf<Mark>()
        val duration = durationInTime

        var currentTimestamp = Duration.ZERO
        var currentX = 0f
        for (cue in cues) {
            result += Mark(
                x = currentX,
                color = primaryMarkColor,
                summary = { CueSummaryView(cue) }
            )
            if (drawAllBeatsMarks) {
                for (i in 1 until cue.timeSignature.noteCount) {
                    result += Mark(
                        x = (currentTimestamp + cue.bpm.interval * i).toX(duration, width),
                        color = secondaryMarkColor,
                        summary = null,
                    )
                }
            }
            val nextTimestamp = currentTimestamp + cue.durationAsTime
            currentX = nextTimestamp.toX(duration, width)
            currentTimestamp = nextTimestamp
        }

        result.distinctBy(Mark::x)
    }
}

private fun Duration.toX(totalDuration: Duration, viewWidth: Float): Float {
    return if (totalDuration == Duration.ZERO || viewWidth == 0f) {
        0f
    } else {
        (this / totalDuration * viewWidth).toFloat()
    }
}

private fun Modifier.clickTrackGestures(
    viewportZoomAndPanEnabled: Boolean,
    progressDragAndDropEnabled: Boolean,
    viewportState: AnimatableViewport,
    progressPosition: AnimatableFloat?,
    onProgressDragStart: suspend (progress: AnimatableFloat) -> Unit,
    onProgressDrop: suspend (progress: AnimatableFloat) -> Unit,
): Modifier = composed {
    if ((progressDragAndDropEnabled && progressPosition != null) || viewportZoomAndPanEnabled) {
        val hapticFeedback by rememberUpdatedState(LocalHapticFeedback.current)

        pointerInput(
            viewportZoomAndPanEnabled,
            progressDragAndDropEnabled,
            viewportState,
            progressPosition,
        ) {
            while (currentCoroutineContext().isActive) {
                coroutineScope {
                    if (progressDragAndDropEnabled && progressPosition != null) {
                        launch {
                            detectDragGesturesAfterLongPress(
                                onDragStart = {
                                    // FIXME(https://issuetracker.google.com/issues/171394805): Not working if global settings disables haptic feedback
                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                    launch {
                                        onProgressDragStart(progressPosition)
                                    }
                                },
                                onDragEnd = {
                                    launch {
                                        onProgressDrop(progressPosition)
                                    }
                                },
                                onDragCancel = {
                                    launch {
                                        onProgressDrop(progressPosition)
                                    }
                                },
                                onDrag = { _, dragAmount ->
                                    val currentViewportTransformations = viewportState.transformations
                                    val snapTo = progressPosition.value + dragAmount.x / currentViewportTransformations.scaleX
                                    launch { progressPosition.snapTo(snapTo) }
                                }
                            )
                        }
                    }

                    if (viewportZoomAndPanEnabled) {
                        launch {
                            detectTapGestures(onDoubleTap = { offset ->
                                val currentViewportTransformations = viewportState.transformations
                                val viewport = viewportState.value
                                val bounds = viewportState.bounds
                                val newScale = if (currentViewportTransformations.scaleX < 4) 4 else 1
                                val newWidth = bounds.width / newScale
                                val newLeft = viewport.left - (newWidth - viewport.width) * (offset.x - bounds.left) / bounds.width

                                launch {
                                    viewportState.animateTo(
                                        newLeft = newLeft,
                                        newTop = bounds.top,
                                        newRight = newLeft + newWidth,
                                        newBottom = bounds.bottom
                                    )
                                }
                            })
                        }

                        launch {
                            val velocityTracker = VelocityTracker()
                            awaitPointerEventScope {
                                launch {
                                    detectTransformGesturesWithEndCallbacks(
                                        onGesture = { centroid, pan, zoom, _ ->
                                            val currentViewportTransformations = viewportState.transformations
                                            val viewport = viewportState.value
                                            val bounds = viewportState.bounds
                                            val newWidth = viewport.width / zoom
                                            val newLeft = viewport.left - (newWidth - viewport.width) * (centroid.x - bounds.left) / bounds.width

                                            if (zoom == 1f) {
                                                velocityTracker.addPosition(
                                                    timeMillis = currentEvent.changes.firstOrNull { it.pressed }?.uptimeMillis ?: 0L,
                                                    position = centroid
                                                )
                                            } else {
                                                velocityTracker.resetTracking()
                                            }

                                            launch {
                                                viewportState.apply {
                                                    snapTo(
                                                        newLeft = newLeft,
                                                        newTop = bounds.top,
                                                        newRight = newLeft + newWidth,
                                                        newBottom = bounds.bottom
                                                    )
                                                    snapTranslate(x = -pan.x / currentViewportTransformations.scaleX)
                                                }
                                            }
                                        },
                                        onGestureEnd = {
                                            val currentViewportTransformations = viewportState.transformations
                                            val velocity = -velocityTracker.calculateVelocity().run { Offset(x, y) } /
                                                    currentViewportTransformations.scaleX
                                            velocityTracker.resetTracking()

                                            launch {
                                                viewportState.animateDecay(velocity)
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    } else {
        this
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
private fun Preview() {
    ClickTrackView(
        clickTrack = PREVIEW_CLICK_TRACK_1.value,
        drawTextMarks = true,
        progress = PlayableProgress(Duration.seconds(1), PREVIEW_CLICK_TRACK_1.value.durationInTime),
        modifier = Modifier.fillMaxSize()
    )
}
