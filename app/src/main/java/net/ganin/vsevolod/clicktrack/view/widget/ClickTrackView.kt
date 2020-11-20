package net.ganin.vsevolod.clicktrack.view.widget

import androidx.compose.animation.animatedFloat
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Layout
import androidx.compose.ui.Modifier
import androidx.compose.ui.WithConstraints
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.DensityAmbient
import androidx.ui.tooling.preview.Preview
import net.ganin.vsevolod.clicktrack.lib.ClickTrack
import net.ganin.vsevolod.clicktrack.lib.Cue
import net.ganin.vsevolod.clicktrack.lib.SerializableDuration
import net.ganin.vsevolod.clicktrack.state.PlaybackStamp
import net.ganin.vsevolod.clicktrack.view.preview.PREVIEW_CLICK_TRACK_1
import kotlin.time.Duration
import kotlin.time.seconds

data class ClickTrackViewState(
    val clickTrack: ClickTrack,
    val drawTextMarks: Boolean,
    val playbackTimestamp: PlaybackStamp?,
)

@Composable
fun ClickTrackView(
    state: ClickTrackViewState,
    modifier: Modifier = Modifier,
) {
    WithConstraints(modifier = modifier) {
        val width = with(DensityAmbient.current) { minWidth.toPx() }
        val marks = state.clickTrack.asMarks(width)

        val playbackStampX = state.playbackTimestamp?.run {
            val animationData = remember(this) {
                fun Duration.toX() = toX(state.clickTrack.durationInTime, width)
                val timestamp = timestamp.value
                val animationDuration = duration.value
                object {
                    val initial = timestamp.toX()
                    val target = (timestamp + animationDuration).toX()
                    val duration = animationDuration
                }
            }
            animatedFloat(animationData.initial).apply {
                val animationDurationMillis = animationData.duration.toLongMilliseconds().toInt()
                animateTo(
                    targetValue = animationData.target,
                    anim = tween(animationDurationMillis, easing = LinearEasing)
                )
            }.value
        }

        val markColor = MaterialTheme.colors.onSurface

        Canvas(
            modifier = Modifier
                .height(minHeight)
                .width(minWidth)
        ) {
            for (mark in marks) {
                drawLine(
                    color = markColor,
                    start = Offset(mark.x, 0f),
                    end = Offset(mark.x, size.height),
                )
            }

            if (playbackStampX != null) {
                drawLine(
                    color = Color.LightGray,
                    start = Offset(playbackStampX, 0f),
                    end = Offset(playbackStampX, size.height),
                )
            }
        }

        if (state.drawTextMarks) {
            Layout(
                modifier = Modifier.wrapContentSize(),
                children = {
                    for (mark in marks) {
                        Text(text = mark.text, modifier = Modifier.wrapContentSize())
                    }
                },
                measureBlock = { measurables, constraints ->
                    val placeables = measurables.map { measurable ->
                        measurable.measure(constraints)
                    }

                    layout(constraints.maxWidth, constraints.maxHeight) {
                        var currentEndX = 0
                        var currentEndY = 0

                        placeables.forEachIndexed { index, placeable ->
                            val mark = marks[index]
                            val markX = mark.x.toInt()

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

private class Mark(
    val x: Float,
    val text: String
)

private fun ClickTrack.asMarks(width: Float): List<Mark> {
    val result = mutableListOf<Mark>()
    val duration = durationInTime

    var currentTimestamp = Duration.ZERO
    var currentX = 0f
    for (cue in cues) {
        result += Mark(currentX, cue.cue.toText())
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

@Preview
@Composable
fun PreviewClickTrackView() {
    ClickTrackView(
        ClickTrackViewState(
            clickTrack = PREVIEW_CLICK_TRACK_1.value,
            drawTextMarks = true,
            playbackTimestamp = PlaybackStamp(
                timestamp = SerializableDuration(1.seconds),
                duration = SerializableDuration(Duration.ZERO)
            )
        ),
        modifier = Modifier.fillMaxSize()
    )
}
