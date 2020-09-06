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
import net.ganin.vsevolod.clicktrack.lib.ClickTrack
import net.ganin.vsevolod.clicktrack.lib.Cue

@Composable
fun ClickTrackView(clickTrack: ClickTrack) = WithConstraints {
    val width = with(DensityAmbient.current) { maxWidth.toPx() }

    val marks = clickTrack.asMarks(width)

    Canvas(modifier = Modifier.fillMaxSize(), onDraw = {
        for (mark in marks) {
            drawLine(Color.Cyan, Offset(mark.x, 0f), Offset(mark.x, size.height))
        }
    })

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

    result += Mark(0f, initialCue.toText())
    result += followingCues.asSequence()
        .filter { it.timestamp < duration }
        .map { Mark(x = (it.timestamp * width / duration), it.cue.toText()) }

    return result.distinctBy(Mark::x)
}

private fun Cue.toText() = "$bpm bpm ${timeSignature.noteCount}/${timeSignature.noteDuration}"