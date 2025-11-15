package com.vsevolodganin.clicktrack.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import clicktrack.multiplatform.generated.resources.Res
import clicktrack.multiplatform.generated.resources.general_metronome_click_track_title
import clicktrack.multiplatform.generated.resources.metronome_bpm_meter_tap
import clicktrack.multiplatform.generated.resources.metronome_screen_title
import com.vsevolodganin.clicktrack.metronome.MetronomeState
import com.vsevolodganin.clicktrack.metronome.MetronomeViewModel
import com.vsevolodganin.clicktrack.metronome.metronomeClickTrack
import com.vsevolodganin.clicktrack.model.BeatsPerMinuteOffset
import com.vsevolodganin.clicktrack.model.NotePattern
import com.vsevolodganin.clicktrack.model.PlayProgress
import com.vsevolodganin.clicktrack.model.TimeSignature
import com.vsevolodganin.clicktrack.model.bpm
import com.vsevolodganin.clicktrack.ui.piece.BpmWheel
import com.vsevolodganin.clicktrack.ui.piece.ClickTrackView
import com.vsevolodganin.clicktrack.ui.piece.DarkTopAppBarWithBack
import com.vsevolodganin.clicktrack.ui.piece.PlayStopButton
import com.vsevolodganin.clicktrack.ui.piece.SubdivisionsChooser
import com.vsevolodganin.clicktrack.ui.piece.TimeSignatureView
import com.vsevolodganin.clicktrack.ui.theme.ClickTrackTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetronomeScreenView(viewModel: MetronomeViewModel, modifier: Modifier = Modifier) {
    Scaffold(
        topBar = {
            DarkTopAppBarWithBack(
                onBackClick = viewModel::onBackClick,
                title = { Text(text = stringResource(Res.string.metronome_screen_title)) },
                actions = {
                    IconButton(onClick = viewModel::onToggleOptions) {
                        Icon(imageVector = Icons.Default.Tune, contentDescription = null)
                    }
                },
            )
        },
        modifier = modifier,
    ) { paddingValues ->
        Content(
            viewModel = viewModel,
            state = viewModel.state.collectAsState().value ?: return@Scaffold,
            modifier = Modifier.padding(paddingValues),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    viewModel: MetronomeViewModel,
    state: MetronomeState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Card(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .height(200.dp),
        ) {
            val metronomeClickTrackName = stringResource(Res.string.general_metronome_click_track_title)
            val metronomeClickTrack = remember(state.bpm, state.pattern, state.timeSignature) {
                metronomeClickTrack(
                    name = metronomeClickTrackName,
                    bpm = state.bpm,
                    pattern = state.pattern,
                    timeSignature = state.timeSignature,
                )
            }

            ClickTrackView(
                clickTrack = metronomeClickTrack,
                drawAllBeatsMarks = true,
                drawTextMarks = false,
                progress = state.progress,
                defaultLineWidth = with(LocalDensity.current) { 1f.dp.toPx() },
            )
        }

        Text(
            text = state.bpm.value.toString(),
            style = MaterialTheme.typography.displayLarge.copy(
                fontWeight = FontWeight.Medium,
                letterSpacing = 8.sp,
            ),
        )

        TimeSignatureView(
            value = state.timeSignature,
            onValueChange = viewModel::onTimeSignatureChange,
        )

        Layout(
            content = {
                Box(contentAlignment = Alignment.Center) {
                    BpmWheel(
                        value = state.bpm,
                        onValueChange = viewModel::onBpmChange,
                        modifier = Modifier.size(200.dp),
                    )
                    PlayStopButton(
                        isPlaying = state.isPlaying,
                        onToggle = viewModel::onTogglePlay,
                    )
                }

                FloatingActionButton(
                    onClick = viewModel::onBpmMeterClick,
                    modifier = Modifier.size(64.dp),
                    shape = CircleShape,
                ) {
                    Text(
                        text = stringResource(Res.string.metronome_bpm_meter_tap),
                        style = LocalTextStyle.current.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 4.sp,
                        ),
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
        ) { measurables, constraints ->
            val wheel = measurables[0].measure(Constraints())
            val fab = measurables[1].measure(Constraints())

            val width = constraints.maxWidth
            val height = maxOf(wheel.height, fab.height)

            layout(width, height) {
                // Wheel is centered horizontally, nothing special
                wheel.placeRelative(x = (width - wheel.width) / 2, y = 0)

                // FAB is placed in the middle between wheel's and parent's right borders
                fab.placeRelative(
                    x = (width * 3 + wheel.width - fab.width * 2) / 4,
                    y = (height - fab.height) / 2,
                )
            }
        }
    }

    val bottomSheetState = bottomSheetState(viewModel)
    // FIXME: Checking both states to give animation an opportunity to finish
    if (state.areOptionsExpanded || bottomSheetState.isVisible) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.onOptionsExpandedChange(false) },
            sheetState = bottomSheetState,
        ) {
            Options(viewModel)
        }
    }
}

@Composable
private fun Options(viewModel: MetronomeViewModel) {
    val state = viewModel.state.collectAsState().value ?: return
    SubdivisionsChooser(
        pattern = state.pattern,
        timeSignature = state.timeSignature,
        onSubdivisionChoose = viewModel::onPatternChoose,
        modifier = Modifier.padding(8.dp).navigationBarsPadding(),
        alwaysExpanded = true,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun bottomSheetState(viewModel: MetronomeViewModel): SheetState {
    val externalAreOptionsExpanded = viewModel.state.collectAsState().value?.areOptionsExpanded ?: false
    val localBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Sync the external state with the local state
    LaunchedEffect(externalAreOptionsExpanded) {
        when (externalAreOptionsExpanded) {
            true -> localBottomSheetState.expand()
            false -> localBottomSheetState.hide()
        }
    }
    LaunchedEffect(localBottomSheetState.currentValue) {
        when (localBottomSheetState.currentValue) {
            SheetValue.PartiallyExpanded,
            SheetValue.Hidden,
            -> viewModel.onOptionsExpandedChange(false)
            SheetValue.Expanded -> viewModel.onOptionsExpandedChange(true)
        }
    }

    return localBottomSheetState
}

@Preview
@Composable
internal fun MetronomeScreenPreview(expanded: Boolean = false) = ClickTrackTheme {
    MetronomeScreenView(
        viewModel = object : MetronomeViewModel {
            override val state: StateFlow<MetronomeState?> = MutableStateFlow(
                MetronomeState(
                    bpm = 90.bpm,
                    pattern = NotePattern.QUINTUPLET_X2,
                    timeSignature = TimeSignature(4, 4),
                    progress = PlayProgress(100.milliseconds),
                    isPlaying = false,
                    areOptionsExpanded = expanded,
                ),
            )

            override fun onBackClick() = Unit

            override fun onToggleOptions() = Unit

            override fun onOptionsExpandedChange(isOpened: Boolean) = Unit

            override fun onPatternChoose(pattern: NotePattern) = Unit

            override fun onTimeSignatureChange(timeSignature: TimeSignature) = Unit

            override fun onBpmChange(bpmDiff: BeatsPerMinuteOffset) = Unit

            override fun onTogglePlay() = Unit

            override fun onBpmMeterClick() = Unit
        },
    )
}

@Preview
@Composable
private fun ExpandedMetronomeScreenPreview() = MetronomeScreenPreview(expanded = true)
