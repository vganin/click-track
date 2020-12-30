package com.vsevolodganin.clicktrack.view.widget

import android.view.ScaleGestureDetector
import androidx.compose.animation.animatedFloat
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.onCommit
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.gesture.DragObserver
import androidx.compose.ui.gesture.dragGestureFilter
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.WithConstraints
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.platform.AmbientDensity
import androidx.compose.ui.tooling.preview.Preview
import com.vsevolodganin.clicktrack.lib.ClickTrack
import com.vsevolodganin.clicktrack.lib.Cue
import com.vsevolodganin.clicktrack.lib.durationAsTime
import com.vsevolodganin.clicktrack.lib.interval
import com.vsevolodganin.clicktrack.utils.compose.AnimatedRect
import com.vsevolodganin.clicktrack.view.preview.PREVIEW_CLICK_TRACK_1
import kotlin.time.Duration

@Composable
fun ClickTrackView(
    clickTrack: ClickTrack,
    modifier: Modifier = Modifier,
    drawAllBeatsMarks: Boolean = false,
    drawTextMarks: Boolean = true,
    progress: Float? = null,
    animateProgress: Boolean = progress != null,
    viewportPanEnabled: Boolean = false,
    onProgressChanged: (Float) -> Unit = {},
) {
    WithConstraints(modifier = modifier) {
        val width = minWidth
        val height = minHeight
        val widthPx = with(AmbientDensity.current) { width.toPx() }
        val heightPx = with(AmbientDensity.current) { height.toPx() }
        val marks = clickTrack.asMarks(widthPx, drawAllBeatsMarks)

        val progressX = progress?.let {
            animatedProgressX(
                clickTrack = clickTrack,
                progress = progress,
                isPlaying = animateProgress,
                totalWidthPx = widthPx,
            )
        }

        if (progressX != null) {
            onCommit(progressX) {
                onProgressChanged(progressX / widthPx)
            }
        }

        val playbackStampColor = MaterialTheme.colors.primary

        val bounds = remember { Rect(0f, 0f, widthPx, heightPx) }
        val viewportState = AnimatedRect(bounds)
        val viewport by derivedStateOf { viewportState.value }

        Box(
            modifier = Modifier
                .let {
                    if (viewportPanEnabled) {
                        it.viewportPanGestureFilter(viewportState)
                    } else {
                        it
                    }
                }
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

                        if (progressX != null) {
                            drawLine(
                                color = playbackStampColor,
                                start = Offset(progressX, 0f),
                                end = Offset(progressX, size.height),
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
                            if (mark.text != null) {
                                Text(text = mark.text, modifier = Modifier.wrapContentSize())
                            }
                        }
                    },
                    measureBlock = { measurables, constraints ->
                        val placeables = measurables.map { measurable ->
                            measurable.measure(constraints)
                        }

                        layout(constraints.maxWidth, constraints.maxHeight) {
                            var currentEndX = (translateX * scaleX).toInt()
                            var currentEndY = 0

                            placeables.forEachIndexed { index, placeable ->
                                val mark = marks[index]
                                val markX = ((mark.x + translateX) * scaleX).toInt()

                                if (markX < currentEndX) {
                                    currentEndY += placeable.height
                                } else {
                                    currentEndY = 0
                                }

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
private fun animatedProgressX(clickTrack: ClickTrack, progress: Float, isPlaying: Boolean, totalWidthPx: Float): Float {
    val playbackStampX = animatedFloat(0f)

    onCommit(clickTrack.durationInTime, progress, totalWidthPx) {
        val totalTrackDuration = clickTrack.durationInTime
        val startingPosition = totalTrackDuration * progress.toDouble()

        fun Duration.toX() = toX(totalTrackDuration, totalWidthPx)

        playbackStampX.snapTo(startingPosition.toX())

        if (isPlaying) {
            val animationDuration = totalTrackDuration - startingPosition

            playbackStampX.animateTo(
                targetValue = totalTrackDuration.toX(),
                anim = tween(
                    durationMillis = animationDuration.toLongMilliseconds().toInt(),
                    easing = LinearEasing
                )
            )
        }
    }

    return playbackStampX.value
}

private class Mark(
    val x: Float,
    val text: String?,
    val color: Color,
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
            text = cue.cue.toText(),
            color = MaterialTheme.colors.onSurface
        )
        if (drawAllBeatsMarks) {
            for (i in 1 until cue.cue.timeSignature.noteCount) {
                result += Mark(
                    x = (currentTimestamp + cue.cue.bpm.interval * i).toX(duration, width),
                    text = null,
                    color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium),
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

private fun Cue.toText() = "${bpm.value} bpm ${timeSignature.noteCount}/${timeSignature.noteDuration}"

private fun Modifier.viewportPanGestureFilter(viewportState: AnimatedRect) = composed {
    val context = AmbientContext.current
    val scaleDetector = remember {
        ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                val viewport = viewportState.value
                val bounds = viewportState.bounds

                val newWidth = viewport.width / detector.scaleFactor
                val newLeft = viewport.left - (newWidth - viewport.width) * (detector.focusX - bounds.left) / bounds.width

                viewportState.snapTo(
                    newLeft = newLeft,
                    newTop = viewport.top,
                    newRight = newLeft + newWidth,
                    newBottom = viewport.bottom
                )

                return true
            }
        })
    }

    val dragObserver = remember {
        object : DragObserver {
            override fun onDrag(dragDistance: Offset): Offset {
                viewportState.translate(-dragDistance.copy(y = 0f))
                return dragDistance
            }

            override fun onStop(velocity: Offset) {
                viewportState.fling(-velocity.copy(y = 0f))
            }
        }
    }

    pointerInteropFilter(onTouchEvent = scaleDetector::onTouchEvent)
        .dragGestureFilter(dragObserver)
}

@Preview
@Composable
fun PreviewClickTrackView() {
    ClickTrackView(
        clickTrack = PREVIEW_CLICK_TRACK_1.value,
        drawTextMarks = true,
        progress = 0.13f,
        modifier = Modifier.fillMaxSize()
    )
}
