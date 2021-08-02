package com.vsevolodganin.clicktrack.ui.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.FabPosition
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import com.vsevolodganin.clicktrack.R
import com.vsevolodganin.clicktrack.lib.CueDuration
import com.vsevolodganin.clicktrack.state.redux.TrainingMode
import com.vsevolodganin.clicktrack.state.redux.TrainingState
import com.vsevolodganin.clicktrack.state.redux.action.TrainingAction
import com.vsevolodganin.clicktrack.state.redux.core.Dispatch
import com.vsevolodganin.clicktrack.ui.model.TrainingUiState
import com.vsevolodganin.clicktrack.ui.widget.BpmInputField
import com.vsevolodganin.clicktrack.ui.widget.CueDurationView
import com.vsevolodganin.clicktrack.ui.widget.DropdownSelector
import com.vsevolodganin.clicktrack.ui.widget.DurationPicker
import com.vsevolodganin.clicktrack.ui.widget.GenericTopBarWithBack
import kotlin.time.Duration

@Composable
fun TrainingScreenView(
    state: TrainingUiState,
    modifier: Modifier = Modifier,
    dispatch: Dispatch = Dispatch {},
) {
    Scaffold(
        topBar = { GenericTopBarWithBack(R.string.training, dispatch) },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            if (state.errors.isEmpty()) {
                FloatingActionButton(
                    onClick = {
                        dispatch(TrainingAction.Accept)
                    },
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                }
            }
        },
        modifier = modifier,
    ) {
        Content(state, dispatch)
    }
}

@Composable
private fun Content(state: TrainingUiState, dispatch: Dispatch) {
    ConstraintLayout(
        modifier = Modifier.padding(16.dp)
    ) {
        val (startingTempoLabel, startingTempoValue, modeValue, segmentLengthValue, tempoChangeLabel, tempoChangeValue, endingLabel, endingValue) = createRefs()

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
            onValueChange = {
                dispatch(TrainingAction.EditStartingTempo(it))
            },
            modifier = Modifier
                .constrainAs(startingTempoValue) {
                    start.linkTo(startingTempoLabel.end)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                }
                .width(RIGHT_COLUMN_WIDTH),
            isError = TrainingState.Error.STARTING_TEMPO in state.errors
        )

        createHorizontalChain(startingTempoLabel, startingTempoValue, chainStyle = ChainStyle.SpreadInside)

        val firstRowBottom = createBottomBarrier(startingTempoLabel, startingTempoValue)
        DropdownSelector(
            items = TrainingMode.values().toList(),
            selectedValue = state.mode,
            onSelect = {
                dispatch(TrainingAction.EditMode(it))
            },
            toString = { it.stringResource() },
            modifier = Modifier
                .constrainAs(modeValue) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(firstRowBottom, 16.dp)
                }
        )

        CueDurationView(
            value = state.segmentLength,
            onValueChange = {
                dispatch(TrainingAction.EditSegmentLength(it))
            },
            onTypeChange = {
                dispatch(TrainingAction.EditSegmentLengthType(it))
            },
            modifier = Modifier
                .constrainAs(segmentLengthValue) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(modeValue.bottom, 8.dp)
                }
        )

        Text(
            text = stringResource(when (state.mode) {
                TrainingMode.INCREASE_TEMPO -> R.string.training_increase_by
                TrainingMode.DECREASE_TEMPO -> R.string.training_decrease_by
            }),
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
            onValueChange = {
                dispatch(TrainingAction.EditTempoChange(it))
            },
            modifier = Modifier
                .constrainAs(tempoChangeValue) {
                    start.linkTo(tempoChangeLabel.end)
                    end.linkTo(parent.end)
                    top.linkTo(segmentLengthValue.bottom, 16.dp)
                }
                .width(RIGHT_COLUMN_WIDTH),
            isError = TrainingState.Error.TEMPO_CHANGE in state.errors
        )

        createHorizontalChain(tempoChangeLabel, tempoChangeValue, chainStyle = ChainStyle.SpreadInside)

        DropdownSelector(
            items = TrainingState.EndingKind.values().toList(),
            selectedValue = state.ending.kind,
            onSelect = {
                dispatch(TrainingAction.EditEndingKind(it))
            },
            toString = { it.stringResource(state.mode) },
            modifier = Modifier
                .constrainAs(endingLabel) {
                    start.linkTo(parent.start)
                    end.linkTo(endingValue.start)
                    top.linkTo(tempoChangeValue.bottom, 16.dp)
                }
                .width(LEFT_COLUMN_WIDTH)
        )

        val endingValueModifier = Modifier
            .constrainAs(endingValue) {
                start.linkTo(endingLabel.end)
                end.linkTo(parent.end)
                top.linkTo(endingLabel.top)
                bottom.linkTo(endingLabel.bottom)
            }
            .width(RIGHT_COLUMN_WIDTH)
        when (val ending = state.ending) {
            is TrainingState.Ending.ByTempo -> {
                BpmInputField(
                    value = ending.endingTempo,
                    onValueChange = {
                        dispatch(TrainingAction.EditEnding(TrainingState.Ending.ByTempo(it)))
                    },
                    modifier = endingValueModifier,
                    isError = TrainingState.Error.ENDING_TEMPO in state.errors
                )
            }
            is TrainingState.Ending.ByTime -> {
                DurationPicker(
                    value = ending.duration,
                    onValueChange = {
                        dispatch(TrainingAction.EditEnding(TrainingState.Ending.ByTime(it)))
                    },
                    modifier = endingValueModifier
                )
            }
        }

        createHorizontalChain(endingLabel, endingValue, chainStyle = ChainStyle.SpreadInside)
    }
}

@Composable
private fun TrainingMode.stringResource(): String {
    return stringResource(when (this) {
        TrainingMode.INCREASE_TEMPO -> R.string.training_increase_every
        TrainingMode.DECREASE_TEMPO -> R.string.training_decrease_every
    })
}

@Composable
private fun TrainingState.EndingKind.stringResource(mode: TrainingMode): String {
    return stringResource(when (this) {
        TrainingState.EndingKind.BY_TEMPO -> when (mode) {
            TrainingMode.INCREASE_TEMPO -> R.string.training_max_tempo
            TrainingMode.DECREASE_TEMPO -> R.string.training_min_tempo
        }
        TrainingState.EndingKind.BY_TIME -> R.string.training_play_for
    })
}

@Preview
@Composable
private fun PreviewWithTempoEnding() {
    TrainingScreenView(
        state = TrainingUiState(
            startingTempo = 120,
            mode = TrainingMode.INCREASE_TEMPO,
            segmentLength = CueDuration.Measures(4),
            tempoChange = 5,
            ending = TrainingState.Ending.ByTempo(160),
            errors = setOf(TrainingState.Error.STARTING_TEMPO),
        )
    )
}

private val LEFT_COLUMN_WIDTH = 160.dp
private val RIGHT_COLUMN_WIDTH = 140.dp

@Preview
@Composable
private fun PreviewWithTimeEnding() {
    TrainingScreenView(
        state = TrainingUiState(
            startingTempo = 120,
            mode = TrainingMode.INCREASE_TEMPO,
            segmentLength = CueDuration.Measures(4),
            tempoChange = 5,
            ending = TrainingState.Ending.ByTime(Duration.minutes(5)),
            errors = setOf(TrainingState.Error.STARTING_TEMPO),
        )
    )
}
