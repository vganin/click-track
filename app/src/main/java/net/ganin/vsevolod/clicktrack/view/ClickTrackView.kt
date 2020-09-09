package net.ganin.vsevolod.clicktrack.view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.absoluteOffsetPx
import androidx.compose.foundation.layout.fillMaxSize
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
import kotlin.time.Duration

@Composable
fun ClickTrackView(clickTrack: ClickTrack) = WithConstraints {
    val width = with(DensityAmbient.current) { maxWidth.toPx() }
    val marks = clickTrack.asMarks(width)

    Canvas(modifier = Modifier.fillMaxSize()) {
        for (mark in marks) {
            drawLine(
                color = Color.Cyan,
                start = Offset(mark.x, 0f),
                end = Offset(mark.x, size.height),
            )
        }
    }

    for (mark in marks) {
        Text(
            modifier = Modifier
                .absoluteOffsetPx(x = mutableStateOf(mark.x))
                .absoluteOffset(y = 16.dp),
            text = mark.text
        )
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
    for (cue in cues) {
        val nextTimestamp = currentTimestamp + cue.durationInTime
        val nextX: Float = (nextTimestamp / duration * width).toFloat()
        result += Mark(x = nextX, cue.cue.toText())
        currentTimestamp = nextTimestamp
    }

    return result.distinctBy(Mark::x)
}

private fun Cue.toText() = "$bpm bpm ${timeSignature.noteCount}/${timeSignature.noteDuration}"

@Preview
@Composable
fun PreviewClickTrackView() {
    ClickTrackView(PREVIEW_CLICK_TRACK)
}