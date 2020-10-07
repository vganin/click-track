package net.ganin.vsevolod.clicktrack.view.widget

import androidx.compose.animation.animatedFloat
import androidx.compose.animation.core.ExponentialDecay
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.TargetAnimation
import androidx.compose.foundation.Text
import androidx.compose.foundation.animation.FlingConfig
import androidx.compose.foundation.animation.fling
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.offsetPx
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawOpacity
import androidx.compose.ui.gesture.longPressGestureFilter
import androidx.compose.ui.gesture.scrollorientationlocking.Orientation
import androidx.compose.ui.platform.DensityAmbient
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import kotlin.math.abs

@Composable
fun NumberPicker(
    state: MutableState<Int>,
    modifier: Modifier = Modifier
) {
    val numbersColumnHeight = 36.dp
    val halvedNumbersColumnHeight = numbersColumnHeight / 2
    val halvedNumbersColumnHeightPx = with(DensityAmbient.current) { halvedNumbersColumnHeight.toPx() }

    fun animatedStateValue(offset: Float): Int = state.value - (offset / halvedNumbersColumnHeightPx).toInt()

    val animatedOffset = animatedFloat(initVal = 0f)
    val coercedAnimatedOffset = animatedOffset.value % halvedNumbersColumnHeightPx
    val animatedStateValue = animatedStateValue(animatedOffset.value)

    Column(
        modifier = modifier
            .wrapContentSize()
            .draggable(
                orientation = Orientation.Vertical,
                onDrag = { deltaY ->
                    animatedOffset.snapTo(animatedOffset.value + deltaY)
                },
                onDragStopped = { velocity ->
                    val config = FlingConfig(
                        decayAnimation = ExponentialDecay(
                            frictionMultiplier = 20f
                        ),
                        adjustTarget = { target ->
                            val coercedTarget = target % halvedNumbersColumnHeightPx
                            val coercedAnchors = listOf(-halvedNumbersColumnHeightPx, 0f, halvedNumbersColumnHeightPx)
                            val coercedPoint = coercedAnchors.minByOrNull { abs(it - coercedTarget) }!!
                            val base = halvedNumbersColumnHeightPx * (target / halvedNumbersColumnHeightPx).toInt()
                            val adjusted = coercedPoint + base
                            TargetAnimation(adjusted, SpringSpec())
                        }
                    )
                    animatedOffset.fling(velocity, config) { _, endValue, _ ->
                        state.value = animatedStateValue(endValue)
                        animatedOffset.snapTo(0f)
                    }
                }
            )
    ) {
        val spacing = 4.dp

        Arrow(ArrowDirection.UP)

        Spacer(modifier = Modifier.height(spacing))

        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .offsetPx(y = mutableStateOf(coercedAnimatedOffset))
        ) {
            Label(
                text = (animatedStateValue - 1).toString(),
                modifier = Modifier
                    .offset(y = -halvedNumbersColumnHeight)
                    .drawOpacity(coercedAnimatedOffset / halvedNumbersColumnHeightPx)
            )
            Label(
                text = animatedStateValue.toString(),
                modifier = Modifier
                    .drawOpacity(1 - abs(coercedAnimatedOffset) / halvedNumbersColumnHeightPx)
            )
            Label(
                text = (animatedStateValue + 1).toString(),
                modifier = Modifier
                    .offset(y = halvedNumbersColumnHeight)
                    .drawOpacity(-coercedAnimatedOffset / halvedNumbersColumnHeightPx)
            )
        }

        Spacer(modifier = Modifier.height(spacing))

        Arrow(ArrowDirection.DOWN)
    }
}

@Composable
private fun Label(text: String, modifier: Modifier) {
    Text(
        text = text,
        modifier = modifier.longPressGestureFilter {
            /* Empty to disable text selection */
        }
    )
}

@Preview
@Composable
fun PreviewNumberPicker() {
    Box(modifier = Modifier.fillMaxSize()) {
        NumberPicker(
            state = mutableStateOf(0),
            modifier = Modifier.align(Alignment.Center)
        )
    }
}
