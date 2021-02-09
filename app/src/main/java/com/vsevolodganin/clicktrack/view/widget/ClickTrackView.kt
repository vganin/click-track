package com.vsevolodganin.clicktrack.view.widget

import androidx.compose.animation.animatedFloat
import androidx.compose.animation.core.AnimatedFloat
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
import androidx.compose.foundation.gestures.forEachGesture
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.anyPositionChangeConsumed
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.AmbientDensity
import androidx.compose.ui.platform.AmbientHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.vsevolodganin.clicktrack.lib.ClickTrack
import com.vsevolodganin.clicktrack.lib.interval
import com.vsevolodganin.clicktrack.utils.compose.AnimatedRect
import com.vsevolodganin.clicktrack.utils.compose.awaitLongTapOrCancellation
import com.vsevolodganin.clicktrack.view.preview.PREVIEW_CLICK_TRACK_1
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.time.Duration

@Composable
fun ClickTrackView(
    clickTrack: ClickTrack,
    modifier: Modifier = Modifier,
    drawAllBeatsMarks: Boolean = false,
    drawTextMarks: Boolean = true,
    progress: Double? = null,
    onAnimatedProgressUpdate: (Double) -> Unit = {},
    progressDragAndDropEnabled: Boolean = false,
    onProgressDragStart: () -> Unit = {},
    onProgressDrop: (Float) -> Unit = {},
    viewportPanEnabled: Boolean = false,
) {
    BoxWithConstraints(modifier = modifier) {
        val width = minWidth
        val height = minHeight
        val widthPx = with(AmbientDensity.current) { width.toPx() }
        val heightPx = with(AmbientDensity.current) { height.toPx() }
        val marks = clickTrack.asMarks(widthPx, drawAllBeatsMarks)

        var isProgressCaptured by remember { mutableStateOf(false) }
        val progressLineWidth = animatedFloat(0f)
        DisposableEffect(isProgressCaptured) {
            val newProgressLineWidth = when (isProgressCaptured) {
                true -> PROGRESS_LINE_WIDTH_CAPTURED
                false -> PROGRESS_LINE_WIDTH_DEFAULT
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
                anim = animSpec
            )
            onDispose { }
        }

        val progressPosition = progress?.let {
            animatedProgressPosition(
                clickTrack = clickTrack,
                progress = progress,
                totalWidthPx = widthPx,
            ).also { progressPosition ->
                onAnimatedProgressUpdate((progressPosition.value / widthPx).toDouble())
            }
        }

        val playbackStampColor = MaterialTheme.colors.secondaryVariant

        val bounds = remember { Rect(0f, 0f, widthPx, heightPx) }
        val viewportState = AnimatedRect(bounds)
        val viewport by derivedStateOf { viewportState.value }

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
                        onProgressDrop(progress.value / widthPx)
                    }
                )
        ) {
            val scaleX = bounds.width / viewport.width
            val scaleY = bounds.height / viewport.height
            val translateX = -viewport.left
            val translateY = -viewport.top

            Canvas(modifier = Modifier.size(width, height)) {
                withTransform(
                    transformBlock = {
                        scale(scaleX, scaleY, Offset(0f, 0f))
                        translate(translateX, translateY)
                    },
                    drawBlock = {
                        for (mark in marks) {
                            drawLine(
                                color = mark.color,
                                start = Offset(mark.x, 0f),
                                end = Offset(mark.x, size.height),
                            )
                        }

                        if (progressPosition != null) {
                            drawLine(
                                color = playbackStampColor,
                                start = Offset(progressPosition.value, 0f),
                                end = Offset(progressPosition.value, size.height),
                                strokeWidth = progressLineWidth.value / scaleX
                            )
                        }
                    }
                )
            }

            if (drawTextMarks) {
                Layout(
                    modifier = Modifier.wrapContentSize(),
                    content = {
                        for (mark in marks) {
                            val layoutIdModifier = Modifier.layoutId(mark)
                            mark.briefSummary?.invoke(layoutIdModifier)
                            mark.detailedSummary?.invoke(layoutIdModifier)
                        }
                    },
                    measureBlock = { measurables, constraints ->
                        val placeables = measurables.groupBy({ it.layoutId as Mark }) { measurable ->
                            measurable.measure(constraints)
                        }.toSortedMap { lhs, rhs -> lhs.x.compareTo(rhs.x) }.toList()

                        layout(constraints.maxWidth, constraints.maxHeight) {
                            var currentEndX = (translateX * scaleX).toInt()
                            var currentEndY = 0

                            fun Mark.transformedX() = ((x + translateX) * scaleX).toInt()

                            placeables
                                .forEachIndexed { index, (mark, summaries) ->
                                    val markX = mark.transformedX()

                                    val firstSummary = summaries.firstOrNull() ?: return@forEachIndexed

                                    if (markX < currentEndX) {
                                        currentEndY += firstSummary.height
                                    } else {
                                        currentEndY = 0
                                    }

                                    val nextMarkX = marks.getOrNull(index + 1)?.transformedX() ?: Int.MAX_VALUE

                                    val summariesSortedByDescendingWidth = summaries.sortedByDescending(Placeable::width)
                                    val placeable = summariesSortedByDescendingWidth
                                        .firstOrNull { placeableCandidate ->
                                            markX + placeableCandidate.width < nextMarkX
                                        }
                                        ?: summariesSortedByDescendingWidth.last()

                                    placeable.placeRelative(x = markX, y = currentEndY)

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
): AnimatedFloat {
    val playbackStampX = animatedFloat(0f).apply {
        setBounds(0f, totalWidthPx)
    }

    val totalTrackDuration = clickTrack.durationInTime
    val targetPosition = totalTrackDuration * progress

    DisposableEffect(progress) {
        val targetPositionX = targetPosition.toX(totalTrackDuration, totalWidthPx)

        playbackStampX.snapTo(targetPositionX)

        onDispose { }
    }

    DisposableEffect(progress, totalTrackDuration) {
        val animationDuration = totalTrackDuration - targetPosition

        playbackStampX.animateTo(
            targetValue = totalWidthPx,
            anim = tween(
                durationMillis = animationDuration.coerceAtLeast(Duration.ZERO).toLongMilliseconds().toInt(),
                easing = LinearEasing
            )
        )

        onDispose { }
    }

    return playbackStampX
}

private class Mark(
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

// FIXME(https://issuetracker.google.com/issues/177060212): Need to preserve names for easy revert of FIXME below
@Suppress("NAME_SHADOWING", "UnnecessaryVariable")
private fun Modifier.clickTrackGestures(
    viewportZoomAndPanEnabled: Boolean,
    progressDragAndDropEnabled: Boolean,
    viewportState: AnimatedRect,
    progressPosition: AnimatedFloat?,
    onProgressDragStart: (progress: AnimatedFloat) -> Unit,
    onProgressDrop: (progress: AnimatedFloat) -> Unit,
): Modifier = composed {
    val hapticFeedback = AmbientHapticFeedback.current

    // FIXME(https://issuetracker.google.com/issues/177060212): Can't reinstall pointerInput, so need to store and update all argument states locally
    val viewportZoomAndPanEnabledInternal by remember { mutableStateOf(viewportZoomAndPanEnabled) }.apply {
        value = viewportZoomAndPanEnabled
    }
    val progressDragAndDropEnabledInternal by remember { mutableStateOf(progressDragAndDropEnabled) }.apply {
        value = progressDragAndDropEnabled
    }
    val viewportStateInternal by remember { mutableStateOf(viewportState) }.apply { value = viewportState }
    val progressPositionInternal by remember { mutableStateOf(progressPosition) }.apply { value = progressPosition }
    val onProgressDragStartInternal by remember { mutableStateOf(onProgressDragStart) }.apply { value = onProgressDragStart }
    val onProgressDropInternal by remember { mutableStateOf(onProgressDrop) }.apply { value = onProgressDrop }

    pointerInput {
        forEachGesture {
            val viewportZoomAndPanEnabled = viewportZoomAndPanEnabledInternal
            val progressDragAndDropEnabled = progressDragAndDropEnabledInternal
            val viewportState = viewportStateInternal
            val progressPosition = progressPositionInternal
            val onProgressDragStart = onProgressDragStartInternal
            val onProgressDrop = onProgressDropInternal

            coroutineScope {
                // FIXME: The app crashes without any await
                launch {
                    awaitPointerEventScope {
                        awaitPointerEvent()
                    }
                }

                var dragAndDropGesture: Job? = null
                var zoomAndPanGesture: Job? = null

                if (progressDragAndDropEnabled && progressPosition != null) {
                    dragAndDropGesture = launch {
                        val down = awaitLongTapOrCancellation() ?: return@launch

                        zoomAndPanGesture?.cancel()

                        // FIXME(https://issuetracker.google.com/issues/171394805): Not working if global settings disables haptic feedback
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)

                        onProgressDragStart(progressPosition)

                        awaitPointerEventScope {
                            drag(down.id) {
                                progressPosition.snapTo(progressPosition.value + it.positionChange().x)
                            }
                            onProgressDrop(progressPosition)
                        }
                    }
                }

                if (viewportZoomAndPanEnabled) {
                    zoomAndPanGesture = launch {
                        awaitPointerEventScope {
                            var zoom = 1f
                            var pan = Offset.Zero
                            var pastTouchSlop = false
                            val touchSlop = viewConfiguration.touchSlop

                            awaitFirstDown()
                            do {
                                val event = awaitPointerEvent()
                                val canceled = event.changes.any { it.anyPositionChangeConsumed() }
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
                                        if (zoomChange != 1f ||
                                            panChange != Offset.Zero
                                        ) {
                                            val viewport = viewportState.value
                                            val bounds = viewportState.bounds

                                            val newWidth = viewport.width / zoomChange
                                            val newLeft =
                                                viewport.left - (newWidth - viewport.width) * (centroid.x - bounds.left) / bounds.width

                                            viewportState.apply {
                                                snapTo(
                                                    newLeft = newLeft,
                                                    newTop = viewport.top,
                                                    newRight = newLeft + newWidth,
                                                    newBottom = viewport.bottom
                                                )
                                                translate(-panChange.copy(y = 0f))
                                            }
                                        }
                                        event.changes.forEach {
                                            if (it.positionChanged()) {
                                                it.consumeAllChanges()
                                            }
                                        }
                                    }
                                }
                            } while (!canceled && event.changes.any { it.pressed })
                        }
                    }
                }
            }
        }
    }
}

private const val PROGRESS_LINE_WIDTH_DEFAULT = 0f
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
