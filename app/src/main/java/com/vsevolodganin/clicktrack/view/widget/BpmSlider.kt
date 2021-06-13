package com.vsevolodganin.clicktrack.view.widget

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.changedToUpIgnoreConsumed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vsevolodganin.clicktrack.lib.BeatsPerMinute
import com.vsevolodganin.clicktrack.lib.BeatsPerMinuteDiff
import com.vsevolodganin.clicktrack.lib.applyDiff
import com.vsevolodganin.clicktrack.lib.bpm
import kotlin.math.roundToInt
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@Composable
fun BpmSlider(
    state: MutableState<BeatsPerMinute>,
    modifier: Modifier = Modifier,
    sensitivity: Float = DEFAULT_SENSITIVITY,
) {
    BpmSlider(
        value = state.value,
        onValueChange = { state.value = state.value.applyDiff(it) },
        modifier = modifier,
        sensitivity = sensitivity,
    )
}

@Composable
fun BpmSlider(
    value: BeatsPerMinute,
    onValueChange: (BeatsPerMinuteDiff) -> Unit,
    modifier: Modifier = Modifier,
    sensitivity: Float = DEFAULT_SENSITIVITY,
) {
    var floatState by remember { mutableStateOf(value.value.toFloat()) }

    val coroutineScope = rememberCoroutineScope()
    val sliderValue = remember { Animatable(0f) }
    var valueAccretion by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        while (isActive) {
            val floatChange = ACCRETION_EASING.transform(valueAccretion) * sensitivity
            val newFloatState = floatState + floatChange
            val intChange = newFloatState.roundToInt() - floatState.roundToInt()
            floatState = newFloatState
            if (valueAccretion != 0f && intChange != 0) {
                onValueChange(BeatsPerMinuteDiff(intChange))
            }
            delay(32)
        }
    }

    Slider(
        value = sliderValue.value,
        onValueChange = {
            valueAccretion = it
            coroutineScope.launch {
                sliderValue.snapTo(it)
            }
        },
        modifier = modifier
            .widthIn(min = 24.dp)
            .pointerInput(Unit) {
                forEachGesture {
                    try {
                        awaitPointerEventScope {
                            while (true) {
                                val event = awaitPointerEvent()
                                if (event.changes.any { it.changedToUpIgnoreConsumed() }) {
                                    break
                                }
                            }
                        }
                    } finally {
                        valueAccretion = 0f
                        coroutineScope.launch {
                            sliderValue.animateTo(0f, spring())
                        }
                    }
                }
            },
        valueRange = -1f..1f,
        colors = SliderDefaults.colors(
            activeTrackColor = MaterialTheme.colors.primary.copy(alpha = SliderDefaults.InactiveTrackAlpha),
            inactiveTrackColor = MaterialTheme.colors.primary.copy(alpha = SliderDefaults.InactiveTrackAlpha),
        )
    )
}

private const val DEFAULT_SENSITIVITY = 1.5f

private val ACCRETION_EASING = CubicBezierEasing(0.5f, 0f, 0.75f, 0f)

@Preview
@Composable
private fun Preview() {
    val bpmState = remember { mutableStateOf(60.bpm) }
    Column {
        Text(
            text = bpmState.value.value.toString(),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        BpmSlider(bpmState)
    }
}
