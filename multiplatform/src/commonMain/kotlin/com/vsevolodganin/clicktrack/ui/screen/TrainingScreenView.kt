package com.vsevolodganin.clicktrack.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.FabPosition
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vsevolodganin.clicktrack.generated.resources.MR
import com.vsevolodganin.clicktrack.model.CueDuration
import com.vsevolodganin.clicktrack.model.DefaultBeatsDuration
import com.vsevolodganin.clicktrack.model.DefaultMeasuresDuration
import com.vsevolodganin.clicktrack.model.DefaultTimeDuration
import com.vsevolodganin.clicktrack.training.TrainingEditState
import com.vsevolodganin.clicktrack.training.TrainingEditState.TrainingMode
import com.vsevolodganin.clicktrack.training.TrainingEndingKind
import com.vsevolodganin.clicktrack.training.TrainingViewModel
import com.vsevolodganin.clicktrack.ui.piece.BpmInputField
import com.vsevolodganin.clicktrack.ui.piece.CueDurationView
import com.vsevolodganin.clicktrack.ui.piece.DropdownSelector
import com.vsevolodganin.clicktrack.ui.piece.DurationPicker
import com.vsevolodganin.clicktrack.ui.piece.FloatingActionButton
import com.vsevolodganin.clicktrack.ui.piece.TopAppBarWithBack
import com.vsevolodganin.clicktrack.ui.theme.ClickTrackTheme
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.Duration.Companion.minutes

@Composable
fun TrainingScreenView(viewModel: TrainingViewModel, modifier: Modifier = Modifier) {
    Scaffold(
        topBar = {
            TopAppBarWithBack(
                onBackClick = viewModel::onBackClick,
                title = { Text(stringResource(MR.strings.training_screen_title)) },
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            val state by viewModel.state.collectAsState()
            if (state.errors.isEmpty()) {
                FloatingActionButton(onClick = viewModel::onAcceptClick) {
                    Icon(Icons.Default.Check, contentDescription = null)
                }
            }
        },
        modifier = modifier,
    ) {
        Content(viewModel)
    }
}

@Composable
private fun Content(viewModel: TrainingViewModel) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        val state by viewModel.state.collectAsState()

        @Composable
        fun FormRow(content: @Composable RowScope.() -> Unit) = Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            content = content,
        )

        val rightColumnModifier = Modifier.width(140.dp)

        FormRow {
            Text(
                text = stringResource(MR.strings.training_starting_tempo),
                modifier = Modifier.weight(1f),
            )
            BpmInputField(
                value = state.startingTempo,
                onValueChange = viewModel::onStartingTempoChange,
                modifier = rightColumnModifier,
                isError = TrainingEditState.Error.STARTING_TEMPO in state.errors,
            )
        }

        DropdownSelector(
            items = TrainingMode.entries,
            selectedValue = state.mode,
            onSelect = viewModel::onModeSelect,
            toString = { it.stringResource() },
            modifier = Modifier.fillMaxWidth(),
        )

        CueDurationView(
            value = state.segmentLength,
            onValueChange = viewModel::onSegmentLengthChange,
            onTypeChange = viewModel::onSegmentLengthTypeChange,
            modifier = Modifier.fillMaxWidth(),
        )

        FormRow {
            Text(
                text = stringResource(
                    when (state.mode) {
                        TrainingMode.INCREASE_TEMPO -> MR.strings.training_increase_by
                        TrainingMode.DECREASE_TEMPO -> MR.strings.training_decrease_by
                    },
                ),
                modifier = Modifier.weight(1f),
            )
            BpmInputField(
                value = state.tempoChange,
                onValueChange = viewModel::onTempoChangeChange,
                modifier = rightColumnModifier,
                isError = TrainingEditState.Error.TEMPO_CHANGE in state.errors,
            )
        }

        FormRow {
            DropdownSelector(
                items = TrainingEndingKind.entries,
                selectedValue = state.activeEndingKind,
                onSelect = viewModel::onEndingKindChange,
                toString = { it.stringResource(state.mode) },
                modifier = Modifier.weight(1f),
            )
            when (val ending = state.ending) {
                is TrainingEditState.Ending.ByTempo -> {
                    BpmInputField(
                        value = ending.endingTempo,
                        onValueChange = { viewModel.onEndingChange(TrainingEditState.Ending.ByTempo(it)) },
                        modifier = rightColumnModifier,
                        isError = TrainingEditState.Error.ENDING_TEMPO in state.errors,
                    )
                }

                is TrainingEditState.Ending.ByTime -> {
                    DurationPicker(
                        value = ending.duration,
                        onValueChange = { viewModel.onEndingChange(TrainingEditState.Ending.ByTime(it)) },
                        modifier = rightColumnModifier,
                    )
                }
            }
        }
    }
}

@Composable
private fun TrainingMode.stringResource(): String {
    return stringResource(
        when (this) {
            TrainingMode.INCREASE_TEMPO -> MR.strings.training_increase_every
            TrainingMode.DECREASE_TEMPO -> MR.strings.training_decrease_every
        },
    )
}

@Composable
private fun TrainingEndingKind.stringResource(mode: TrainingMode): String {
    return stringResource(
        when (this) {
            TrainingEndingKind.BY_TEMPO -> when (mode) {
                TrainingMode.INCREASE_TEMPO -> MR.strings.training_max_tempo
                TrainingMode.DECREASE_TEMPO -> MR.strings.training_min_tempo
            }

            TrainingEndingKind.BY_TIME -> MR.strings.training_play_for
        },
    )
}

@Preview
@Composable
internal fun TrainingScreenPreview() = ClickTrackTheme {
    TrainingScreenView(
        viewModel = object : TrainingViewModel {
            override val state: StateFlow<TrainingEditState> = MutableStateFlow(
                TrainingEditState(
                    startingTempo = 120,
                    mode = TrainingMode.INCREASE_TEMPO,
                    activeSegmentLengthType = CueDuration.Type.MEASURES,
                    segmentLengthBeats = DefaultBeatsDuration,
                    segmentLengthMeasures = DefaultMeasuresDuration,
                    segmentLengthTime = DefaultTimeDuration,
                    tempoChange = 5,
                    activeEndingKind = TrainingEndingKind.BY_TEMPO,
                    endingByTempo = TrainingEditState.Ending.ByTempo(160),
                    endingByTime = TrainingEditState.Ending.ByTime(5.minutes),
                    errors = emptySet(),
                ),
            )

            override fun onBackClick() = Unit

            override fun onAcceptClick() = Unit

            override fun onStartingTempoChange(startingTempo: Int) = Unit

            override fun onModeSelect(mode: TrainingMode) = Unit

            override fun onSegmentLengthChange(segmentLength: CueDuration) = Unit

            override fun onSegmentLengthTypeChange(segmentLengthType: CueDuration.Type) = Unit

            override fun onTempoChangeChange(tempoChange: Int) = Unit

            override fun onEndingChange(ending: TrainingEditState.Ending) = Unit

            override fun onEndingKindChange(endingKind: TrainingEndingKind) = Unit
        },
    )
}
