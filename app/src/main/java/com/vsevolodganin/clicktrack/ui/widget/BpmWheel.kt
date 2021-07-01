package com.vsevolodganin.clicktrack.ui.widget

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vsevolodganin.clicktrack.lib.BeatsPerMinute
import com.vsevolodganin.clicktrack.lib.BeatsPerMinuteDiff
import com.vsevolodganin.clicktrack.lib.applyDiff
import com.vsevolodganin.clicktrack.lib.bpm
import com.vsevolodganin.clicktrack.utils.compose.detectRadialDragGesture
import com.vsevolodganin.clicktrack.utils.compose.toRadians
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun BpmWheel(
    state: MutableState<BeatsPerMinute>,
    modifier: Modifier = Modifier,
    sensitivity: Float = 0.08f,
) {
    BpmWheel(
        value = state.value,
        onValueChange = { state.value = state.value.applyDiff(it) },
        modifier = modifier,
        sensitivity = sensitivity,
    )
}

@Composable
fun BpmWheel(
    value: BeatsPerMinute,
    onValueChange: (BeatsPerMinuteDiff) -> Unit,
    modifier: Modifier = Modifier,
    sensitivity: Float = 0.08f,
) {
    var floatState by remember { mutableStateOf(value.value.toFloat()) }

    Wheel(
        onAngleChange = {
            val floatChange = it * sensitivity
            val newFloatState = floatState + floatChange
            val intChange = newFloatState.roundToInt() - floatState.roundToInt()
            floatState = newFloatState
            if (intChange != 0) {
                onValueChange(BeatsPerMinuteDiff(intChange))
            }
        },
        modifier = modifier.aspectRatio(1f)
    )
}

@Composable
private fun Wheel(onAngleChange: (diff: Float) -> Unit, modifier: Modifier = Modifier) {
    var buttonAngle by remember { mutableStateOf(90f) }

    BoxWithConstraints(modifier) {
        val density = LocalDensity.current
        val width = minWidth
        val height = minHeight
        val widthPx = with(density) { width.toPx() }
        val heightPx = with(density) { height.toPx() }
        val localOnAngleChange by rememberUpdatedState(onAngleChange)

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(widthPx, heightPx) {
                    val center = Offset(x = widthPx / 2, y = heightPx / 2)
                    detectRadialDragGesture(center) { angleDiff ->
                        buttonAngle -= angleDiff
                        localOnAngleChange(angleDiff)
                    }
                },
            elevation = 4.dp,
            shape = CircleShape,
            color = Color.Transparent
        ) {
            val wheelWidth = width * WHEEL_WIDTH_MULTIPLIER
            val wheelWidthPx = with(density) { wheelWidth.toPx() }

            val wheelColor = MaterialTheme.colors.primary
            val controllerButtonColor = MaterialTheme.colors.onPrimary

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(wheelWidth / 2)
            ) {
                if (size.maxDimension <= 0) return@Canvas

                drawArc(
                    brush = SolidColor(wheelColor),
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
                    color = controllerButtonColor.copy(alpha = 0.8f),
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

private const val WHEEL_WIDTH_MULTIPLIER = 0.125f

@Preview
@Composable
private fun Preview() {
    BpmWheel(
        state = remember { mutableStateOf(60.bpm) },
        modifier = Modifier.size(200.dp)
    )
}
