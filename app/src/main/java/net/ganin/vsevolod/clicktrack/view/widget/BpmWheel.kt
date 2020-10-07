package net.ganin.vsevolod.clicktrack.view.widget

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.WithConstraints
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.gesture.dragGestureFilter
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RadialGradient
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.DensityAmbient
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.toRadians
import androidx.ui.tooling.preview.Preview
import net.ganin.vsevolod.clicktrack.lib.BeatsPerMinute
import net.ganin.vsevolod.clicktrack.lib.bpm
import net.ganin.vsevolod.clicktrack.utils.compose.RadialDragObserver
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun BpmWheel(
    state: MutableState<BeatsPerMinute> = mutableStateOf(60.bpm),
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Text(
            text = "${state.value.value} bpm",
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Wheel(
            onAngleChange = {
                state.value += it.toInt()
            },
            modifier = Modifier
                .aspectRatio(1f)
                .align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
private fun Wheel(onAngleChange: (diff: Float) -> Unit, modifier: Modifier = Modifier) {
    var buttonAngle: Float by remember { mutableStateOf(90f) }

    WithConstraints(modifier) {
        val density = DensityAmbient.current
        val width = minWidth
        val height = minHeight
        val widthPx = with(density) { width.toPx() }
        val heightPx = with(density) { height.toPx() }

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .dragGestureFilter(RadialDragObserver(Offset(x = widthPx / 2, y = heightPx / 2)) { angleDiff ->
                    buttonAngle -= angleDiff
                    onAngleChange(angleDiff)
                }),
            elevation = 4.dp,
            shape = CircleShape,
            color = Color.Transparent
        ) {
            val wheelWidth = 25.dp
            val wheelWidthPx = with(density) { wheelWidth.toPx() }

            Canvas(
                modifier = Modifier.fillMaxSize().padding(wheelWidth / 2)
            ) {
                if (size.maxDimension <= 0) return@Canvas

                drawArc(
                    brush = RadialGradient(
                        0.0f to Color.Magenta,
                        0.9f to Color.Cyan,
                        centerX = center.x,
                        centerY = center.y,
                        radius = size.width
                    ),
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(
                        width = wheelWidthPx
                    )
                )

                val buttonSize = Size(wheelWidthPx, wheelWidthPx) * 0.3f
                val buttonOffset = Offset(
                    x = (cos(buttonAngle.toRadians()) + 1) * center.x - buttonSize.width / 2,
                    y = (-sin(buttonAngle.toRadians()) + 1) * center.y - buttonSize.height / 2
                )

                drawArc(
                    color = Color.White.copy(alpha = 0.5f),
                    topLeft = buttonOffset,
                    size = buttonSize,
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewBpmWheel() {
    BpmWheel(modifier = Modifier.size(200.dp))
}
