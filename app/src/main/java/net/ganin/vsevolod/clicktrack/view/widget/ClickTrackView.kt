package net.ganin.vsevolod.clicktrack.view.widget

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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import net.ganin.vsevolod.clicktrack.lib.ClickTrack
import net.ganin.vsevolod.clicktrack.lib.Cue
import net.ganin.vsevolod.clicktrack.lib.SerializableDuration
import net.ganin.vsevolod.clicktrack.lib.interval
import net.ganin.vsevolod.clicktrack.state.PlaybackStamp
import net.ganin.vsevolod.clicktrack.view.preview.PREVIEW_CLICK_TRACK_1
import kotlin.time.Duration
import kotlin.time.seconds

@Composable
fun ClickTrackView(
    clickTrack: ClickTrack,
    modifier: Modifier = Modifier,
    drawAllBeatsMarks: Boolean = false,
    drawTextMarks: Boolean = true,
    playbackTimestamp: PlaybackStamp? = null,
    viewportPanEnabled: Boolean = false,
) {
    WithConstraints(modifier = modifier) {
        val width = minWidth
        val height = minHeight
        val widthPx = with(AmbientDensity.current) { width.toPx() }
        val heightPx = with(AmbientDensity.current) { height.toPx() }
        val marks = clickTrack.asMarks(widthPx, drawAllBeatsMarks)

        val playbackStampX = playbackTimestamp?.toAnimatedX(
            totalTrackDuration = clickTrack.durationInTime,
            totalWidthPx = widthPx,
        )
        val playbackStampColor = MaterialTheme.colors.primary

        val bounds = remember { Rect(0f, 0f, widthPx, heightPx) }
        val viewportState = remember { mutableStateOf(bounds) }
        val viewport by derivedStateOf { viewportState.value }

        Box(
            modifier = Modifier
                .let {
                    if (viewportPanEnabled) {
                        it.viewportPanGestureFilter(viewportState, bounds)
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

                        if (playbackStampX != null) {
                            drawLine(
                                color = playbackStampColor,
                                start = Offset(playbackStampX, 0f),
                                end = Offset(playbackStampX, size.height),
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
private fun PlaybackStamp.toAnimatedX(totalTrackDuration: Duration, totalWidthPx: Float): Float {
    val playbackStampX = animatedFloat(0f)

    onCommit(this) {
        fun Duration.toX() = toX(totalTrackDuration, totalWidthPx)

        val timestamp = timestamp.value
        val animationDuration = duration.value

        playbackStampX.snapTo(timestamp.toX())
        playbackStampX.animateTo(
            targetValue = (timestamp + animationDuration).toX(),
            anim = tween(
                durationMillis = animationDuration.toLongMilliseconds().toInt(),
                easing = LinearEasing
            )
        )
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
        val nextTimestamp = currentTimestamp + cue.durationInTime
        currentX = nextTimestamp.toX(duration, width)
        currentTimestamp = nextTimestamp
    }

    return result.distinctBy(Mark::x)
}

private fun Duration.toX(totalDuration: Duration, viewWidth: Float): Float {
    return (this / totalDuration * viewWidth).toFloat()
}

private fun Cue.toText() = "${bpm.value} bpm ${timeSignature.noteCount}/${timeSignature.noteDuration}"

private fun Modifier.viewportPanGestureFilter(
    viewportState: MutableState<Rect>,
    bounds: Rect,
) = composed {
    fun Rect.coerceToBounds(): Rect {
        var translateX = 0f
        var translateY = 0f
        if (left < bounds.left) {
            translateX += bounds.left - left
        }
        if (top < bounds.top) {
            translateY += bounds.top - top
        }
        if (right > bounds.right) {
            translateX += bounds.right - right
        }
        if (bottom > bounds.bottom) {
            translateY += bounds.bottom - bottom
        }
        return translate(translateX, translateY).intersect(bounds)
    }

    val context = AmbientContext.current
    val scaleDetector = remember {
        ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                val viewport = viewportState.value

                val newWidth = viewport.width / detector.scaleFactor
                val newLeft = viewport.left - (newWidth - viewport.width) * (detector.focusX - bounds.left) / bounds.width

                viewportState.value = Rect(
                    left = newLeft,
                    top = viewport.top,
                    right = newLeft + newWidth,
                    bottom = viewport.bottom
                ).coerceToBounds()

                return true
            }
        })
    }

    val dragObserver = object : DragObserver {
        override fun onDrag(dragDistance: Offset): Offset {
            viewportState.value = viewportState.value.translate(-dragDistance.copy(y = 0f)).coerceToBounds()
            return dragDistance
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
        playbackTimestamp = PlaybackStamp(
            timestamp = SerializableDuration(1.seconds),
            duration = SerializableDuration(Duration.ZERO)
        ),
        modifier = Modifier.fillMaxSize()
    )
}
