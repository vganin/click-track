package com.vsevolodganin.clicktrack.ui.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material.FabPosition
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.vsevolodganin.clicktrack.R
import com.vsevolodganin.clicktrack.model.CueDuration
import com.vsevolodganin.clicktrack.model.DefaultBeatsDuration
import com.vsevolodganin.clicktrack.model.DefaultMeasuresDuration
import com.vsevolodganin.clicktrack.model.DefaultTimeDuration
import com.vsevolodganin.clicktrack.training.TrainingEditState
import com.vsevolodganin.clicktrack.training.TrainingEditState.TrainingMode
import com.vsevolodganin.clicktrack.training.TrainingEndingKind
import com.vsevolodganin.clicktrack.training.TrainingViewModel
import com.vsevolodganin.clicktrack.ui.ClickTrackTheme
import com.vsevolodganin.clicktrack.ui.piece.BpmInputField
import com.vsevolodganin.clicktrack.ui.piece.CueDurationView
import com.vsevolodganin.clicktrack.ui.piece.DropdownSelector
import com.vsevolodganin.clicktrack.ui.piece.DurationPicker
import com.vsevolodganin.clicktrack.ui.piece.FloatingActionButton
import com.vsevolodganin.clicktrack.ui.piece.TopAppBarWithBack
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.time.Duration.Companion.minutes

@Composable
fun TrainingScreenView(
    viewModel: TrainingViewModel,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            TopAppBarWithBack(
                onBackClick = viewModel::onBackClick,
                title = { Text(stringResource(R.string.training_screen_title)) },
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
    ConstraintLayout(
        modifier = Modifier.padding(16.dp)
    ) {
        val state by viewModel.state.collectAsState()

        val (
            startingTempoLabel,
            startingTempoValue,
            modeValue,
            segmentLengthValue,
            tempoChangeLabel,
            tempoChangeValue,
            endingLabel,
            endingValue
        ) = createRefs()

        Text(
            text = stringResource(R.string.training_starting_tempo),
            modifier = Modifier
                .constrainAs(startingTempoLabel) {
                    start.linkTo(parent.start)
                    end.linkTo(startingTempoValue.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(startingTempoValue.bottom)
                }
        )

        BpmInputField(
            value = state.startingTempo,
            onValueChange = viewModel::onStartingTempoChange,
            modifier = Modifier
                .constrainAs(startingTempoValue) {
                    start.linkTo(startingTempoLabel.end)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    width = Dimension.value(RIGHT_COLUMN_WIDTH)
                },
            isError = TrainingEditState.Error.STARTING_TEMPO in state.errors
        )

        createHorizontalChain(startingTempoLabel, startingTempoValue, chainStyle = ChainStyle.SpreadInside)

        val firstRowBottom = createBottomBarrier(startingTempoLabel, startingTempoValue)
        DropdownSelector(
            items = TrainingMode.values().toList(),
            selectedValue = state.mode,
            onSelect = viewModel::onModeSelect,
            toString = { it.stringResource() },
            modifier = Modifier
                .constrainAs(modeValue) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(firstRowBottom, 16.dp)
                    width = Dimension.fillToConstraints
                }
        )

        CueDurationView(
            value = state.segmentLength,
            onValueChange = viewModel::onSegmentLengthChange,
            onTypeChange = viewModel::onSegmentLengthTypeChange,
            modifier = Modifier
                .constrainAs(segmentLengthValue) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(modeValue.bottom, 8.dp)
                }
        )

        Text(
            text = stringResource(
                when (state.mode) {
                    TrainingMode.INCREASE_TEMPO -> R.string.training_increase_by
                    TrainingMode.DECREASE_TEMPO -> R.string.training_decrease_by
                }
            ),
            modifier = Modifier
                .constrainAs(tempoChangeLabel) {
                    start.linkTo(parent.start)
                    end.linkTo(tempoChangeValue.start)
                    top.linkTo(tempoChangeValue.top)
                    bottom.linkTo(tempoChangeValue.bottom)
                }
        )

        BpmInputField(
            value = state.tempoChange,
            onValueChange = viewModel::onTempoChangeChange,
            modifier = Modifier
                .constrainAs(tempoChangeValue) {
                    start.linkTo(tempoChangeLabel.end)
                    end.linkTo(parent.end)
                    top.linkTo(segmentLengthValue.bottom, 16.dp)
                    width = Dimension.value(RIGHT_COLUMN_WIDTH)
                },
            isError = TrainingEditState.Error.TEMPO_CHANGE in state.errors
        )

        createHorizontalChain(tempoChangeLabel, tempoChangeValue, chainStyle = ChainStyle.SpreadInside)

        DropdownSelector(
            items = TrainingEndingKind.values().toList(),
            selectedValue = state.activeEndingKind,
            onSelect = viewModel::onEndingKindChange,
            toString = { it.stringResource(state.mode) },
            modifier = Modifier
                .constrainAs(endingLabel) {
                    start.linkTo(parent.start)
                    end.linkTo(endingValue.start, 8.dp)
                    top.linkTo(tempoChangeValue.bottom, 16.dp)
                    width = Dimension.fillToConstraints
                }
        )

        val endingValueModifier = Modifier
            .constrainAs(endingValue) {
                start.linkTo(endingLabel.end)
                end.linkTo(parent.end)
                top.linkTo(endingLabel.top)
                bottom.linkTo(endingLabel.bottom)
                width = Dimension.value(RIGHT_COLUMN_WIDTH)
            }
        when (val ending = state.ending) {
            is TrainingEditState.Ending.ByTempo -> {
                BpmInputField(
                    value = ending.endingTempo,
                    onValueChange = { viewModel.onEndingChange(TrainingEditState.Ending.ByTempo(it)) },
                    modifier = endingValueModifier,
                    isError = TrainingEditState.Error.ENDING_TEMPO in state.errors
                )
            }
            is TrainingEditState.Ending.ByTime -> {
                DurationPicker(
                    value = ending.duration,
                    onValueChange = { viewModel.onEndingChange(TrainingEditState.Ending.ByTime(it)) },
                    modifier = endingValueModifier
                )
            }
        }
    }
}

@Composable
private fun TrainingMode.stringResource(): String {
    return stringResource(
        when (this) {
            TrainingMode.INCREASE_TEMPO -> R.string.training_increase_every
            TrainingMode.DECREASE_TEMPO -> R.string.training_decrease_every
        }
    )
}

@Composable
private fun TrainingEndingKind.stringResource(mode: TrainingMode): String {
    return stringResource(
        when (this) {
            TrainingEndingKind.BY_TEMPO -> when (mode) {
                TrainingMode.INCREASE_TEMPO -> R.string.training_max_tempo
                TrainingMode.DECREASE_TEMPO -> R.string.training_min_tempo
            }
            TrainingEndingKind.BY_TIME -> R.string.training_play_for
        }
    )
}

private val RIGHT_COLUMN_WIDTH = 140.dp

@ScreenPreviews
@Composable
private fun Preview() = ClickTrackTheme {
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
                )
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
        }
    )
}
