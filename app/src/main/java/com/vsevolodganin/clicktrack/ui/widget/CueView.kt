package com.vsevolodganin.clicktrack.ui.widget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vsevolodganin.clicktrack.R
import com.vsevolodganin.clicktrack.lib.Cue
import com.vsevolodganin.clicktrack.lib.CueDuration
import com.vsevolodganin.clicktrack.lib.TimeSignature
import com.vsevolodganin.clicktrack.lib.bpm
import com.vsevolodganin.clicktrack.utils.compose.observableMutableStateOf
import kotlin.time.Duration

@Composable
fun CueView(
    state: MutableState<Cue>,
    position: Int,
    modifier: Modifier = Modifier,
) {
    val nameState = remember { observableMutableStateOf(state.value.name) }
    val bpmState = remember { observableMutableStateOf(state.value.bpm) }
    val timeSignatureState = remember { observableMutableStateOf(state.value.timeSignature) }
    val durationState = remember { observableMutableStateOf(state.value.duration) }

    LaunchedEffect(Unit) {
        fun update() {
            state.value = state.value.copy(
                name = nameState.value,
                bpm = bpmState.value,
                timeSignature = timeSignatureState.value,
                duration = durationState.value
            )
        }
        nameState.observe { update() }
        bpmState.observe { update() }
        timeSignatureState.observe { update() }
        durationState.observe { update() }
    }

    Column(modifier = modifier.padding(8.dp)) {
        Row {
            Text(
                text = stringResource(R.string.cue_position, position),
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Spacer(modifier = Modifier.width(8.dp))
            TextField(
                value = nameState.value.orEmpty(),
                onValueChange = { nameState.value = it },
                placeholder = {
                    Text(stringResource(R.string.cue_name_hint))
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row {
            CueDurationView(
                state = durationState,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .height(IntrinsicSize.Min)
                    .weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row {
            TimeSignatureView(
                state = timeSignatureState,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Spacer(modifier = Modifier.width(16.dp))
            BpmSlider(
                state = bpmState,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .weight(1f)
            )
            Spacer(modifier = Modifier.width(16.dp))

            val bpmSuffix = stringResource(R.string.cue_bpm_suffix)
            NumberInputField(
                value = bpmState.value.value,
                onValueChange = { bpmState.value = it.bpm },
                modifier = Modifier.align(Alignment.CenterVertically),
                maxDigitsCount = 3,
                visualTransformation = { inputText ->
                    TransformedText(
                        text = inputText + AnnotatedString(bpmSuffix),
                        offsetMapping = object : OffsetMapping {
                            override fun originalToTransformed(offset: Int): Int = offset
                            override fun transformedToOriginal(offset: Int): Int = offset.coerceIn(0..inputText.length)
                        }
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        SubdivisionsChooser(
            cue = state.value,
            onSubdivisionChoose = {
                state.value = state.value.copy(pattern = it)
            }
        )
    }
}

@Preview
@Composable
private fun Preview() {
    CueView(
        state = remember {
            mutableStateOf(
                Cue(
                    bpm = 999.bpm,
                    timeSignature = TimeSignature(3, 4),
                    duration = CueDuration.Time(Duration.minutes(1))
                )
            )
        },
        position = 1,
    )
}
