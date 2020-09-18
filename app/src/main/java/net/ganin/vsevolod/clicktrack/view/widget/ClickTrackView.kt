package net.ganin.vsevolod.clicktrack.view.widget

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.absoluteOffsetPx
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.WithConstraints
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.DensityAmbient
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import net.ganin.vsevolod.clicktrack.lib.ClickTrack
import net.ganin.vsevolod.clicktrack.lib.Cue
import net.ganin.vsevolod.clicktrack.view.preview.PREVIEW_CLICK_TRACK_1
import kotlin.time.Duration
import kotlin.time.seconds

class ClickTrackViewState(
    val clickTrack: ClickTrack,
    val drawTextMarks: Boolean,
    val playbackTimestamp: Duration?,
)

@Composable
fun ClickTrackView(
    state: ClickTrackViewState,
    modifier: Modifier = Modifier,
) {
    WithConstraints(modifier = modifier) {
        val width = with(DensityAmbient.current) { maxWidth.toPx() }
        val marks = state.clickTrack.asMarks(width)
        val playbackX = state.playbackTimestamp?.toX(state.clickTrack.durationInTime, width)

        Canvas(
            modifier = Modifier
                .heightIn(minHeight, maxHeight)
                .widthIn(minWidth, maxWidth)
        ) {
            for (mark in marks) {
                drawLine(
                    color = Color.Cyan,
                    start = Offset(mark.x, 0f),
                    end = Offset(mark.x, size.height),
                )
            }

            if (playbackX != null) {
                drawLine(
                    color = Color.LightGray,
                    start = Offset(playbackX, 0f),
                    end = Offset(playbackX, size.height),
                )
            }
        }

        if (state.drawTextMarks) {
            for (mark in marks) {
                Text(
                    modifier = Modifier
                        .absoluteOffsetPx(x = mutableStateOf(mark.x))
                        .absoluteOffset(y = 16.dp),
                    text = mark.text
                )
            }
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

private fun Cue.toText() = "$bpm bpm ${timeSignature.noteCount}/${timeSignature.noteDuration}"

@Preview
@Composable
fun PreviewClickTrackView() {
    ClickTrackView(
        ClickTrackViewState(
            clickTrack = PREVIEW_CLICK_TRACK_1,
            drawTextMarks = true,
            playbackTimestamp = 1.seconds
        ),
        modifier = Modifier.fillMaxSize()
    )
}
