package com.vsevolodganin.clicktrack.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.BackdropScaffold
import androidx.compose.material.BackdropScaffoldDefaults
import androidx.compose.material.BackdropScaffoldState
import androidx.compose.material.BackdropValue
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.rememberBackdropScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.vsevolodganin.clicktrack.R
import com.vsevolodganin.clicktrack.metronome.MetronomeState
import com.vsevolodganin.clicktrack.metronome.MetronomeTimeSignature
import com.vsevolodganin.clicktrack.metronome.MetronomeViewModel
import com.vsevolodganin.clicktrack.metronome.metronomeClickTrack
import com.vsevolodganin.clicktrack.model.BeatsPerMinuteDiff
import com.vsevolodganin.clicktrack.model.NotePattern
import com.vsevolodganin.clicktrack.model.PlayProgress
import com.vsevolodganin.clicktrack.model.bpm
import com.vsevolodganin.clicktrack.ui.ClickTrackTheme
import com.vsevolodganin.clicktrack.ui.piece.BpmWheel
import com.vsevolodganin.clicktrack.ui.piece.ClickTrackView
import com.vsevolodganin.clicktrack.ui.piece.FloatingActionButton
import com.vsevolodganin.clicktrack.ui.piece.PlayStopButton
import com.vsevolodganin.clicktrack.ui.piece.SubdivisionsChooser
import com.vsevolodganin.clicktrack.ui.piece.TopAppBar
import com.vsevolodganin.clicktrack.ui.piece.darkAppBar
import com.vsevolodganin.clicktrack.ui.piece.onDarkAppBarSurface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun MetronomeScreenView(
    viewModel: MetronomeViewModel,
    modifier: Modifier = Modifier,
) {
    viewModel.state.collectAsState().value ?: return // FIXME: Otherwise crash in swipeable state
    BackdropScaffold(
        appBar = { AppBar(viewModel) },
        backLayerContent = { Options(viewModel) },
        frontLayerContent = { Content(viewModel) },
        modifier = modifier,
        scaffoldState = backdropState(viewModel),
        peekHeight = BackdropScaffoldDefaults.PeekHeight + WindowInsets.statusBars.asPaddingValues().calculateTopPadding(),
        backLayerBackgroundColor = MaterialTheme.colors.darkAppBar,
        backLayerContentColor = MaterialTheme.colors.onDarkAppBarSurface,
    )
}

@Composable
private fun AppBar(viewModel: MetronomeViewModel) {
    TopAppBar(
        title = { Text(text = stringResource(R.string.metronome_screen_title)) },
        navigationIcon = {
            IconButton(onClick = viewModel::onBackClick) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
            }
        },
        actions = {
            IconButton(onClick = viewModel::onToggleOptions) {
                Icon(imageVector = Icons.Default.Tune, contentDescription = null)
            }
        }
    )
}

@Composable
private fun Content(viewModel: MetronomeViewModel) {
    val state = viewModel.state.collectAsState().value ?: return

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
    ) {
        val (clickTrackRef, bpmText, bpmWheel, bpmMeter) = createRefs()

        Card(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .height(200.dp)
                .constrainAs(clickTrackRef) {},
            elevation = 8.dp,
        ) {
            val metronomeClickTrackName = stringResource(R.string.general_metronome_click_track_title)
            val metronomeClickTrack = remember(state.bpm, state.pattern) {
                metronomeClickTrack(
                    name = metronomeClickTrackName,
                    bpm = state.bpm,
                    pattern = state.pattern,
                )
            }

            ClickTrackView(
                clickTrack = metronomeClickTrack,
                drawAllBeatsMarks = true,
                drawTextMarks = false,
                progress = state.progress,
                defaultLineWidth = with(LocalDensity.current) { 1f.dp.toPx() }
            )
        }

        Text(
            text = state.bpm.value.toString(),
            style = MaterialTheme.typography.h1.copy(
                fontWeight = FontWeight.Medium,
                letterSpacing = 8.sp,
            ),
            modifier = Modifier
                .constrainAs(bpmText) {
                    centerHorizontallyTo(parent)
                    top.linkTo(clickTrackRef.bottom)
                    bottom.linkTo(bpmWheel.top)
                }
        )

        Box(
            modifier = Modifier
                .constrainAs(bpmWheel) {
                    centerHorizontallyTo(parent)
                    bottom.linkTo(parent.bottom, margin = 32.dp)
                }
        ) {
            BpmWheel(
                value = state.bpm,
                onValueChange = viewModel::onBpmChange,
                modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.Center)
            )
            PlayStopButton(
                isPlaying = state.isPlaying,
                onToggle = viewModel::onTogglePlay,
                modifier = Modifier.align(Alignment.Center),
                enableInsets = false
            )
        }

        FloatingActionButton(
            onClick = viewModel::onBpmMeterClick,
            modifier = Modifier
                .size(64.dp)
                .constrainAs(bpmMeter) {
                    centerVerticallyTo(bpmWheel)
                    start.linkTo(bpmWheel.end)
                    end.linkTo(parent.end)
                },
            enableInsets = false,
        ) {
            Text(
                text = stringResource(id = R.string.metronome_bpm_meter_tap),
                style = LocalTextStyle.current.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 4.sp,
                )
            )
        }
    }
}

@Composable
private fun Options(viewModel: MetronomeViewModel) {
    val pattern = viewModel.state.collectAsState().value?.pattern ?: return
    SubdivisionsChooser(
        pattern = pattern,
        timeSignature = MetronomeTimeSignature,
        onSubdivisionChoose = viewModel::onPatternChoose,
        modifier = Modifier.padding(8.dp),
        alwaysExpanded = true,
    )
}

@Composable
private fun backdropState(
    viewModel: MetronomeViewModel,
): BackdropScaffoldState {
    val areOptionsExpanded = viewModel.state.collectAsState().value?.areOptionsExpanded ?: false
    val backdropValue = if (areOptionsExpanded) BackdropValue.Revealed else BackdropValue.Concealed
    return rememberBackdropScaffoldState(
        initialValue = backdropValue,
        confirmStateChange = remember {
            { newDrawerValue ->
                when (newDrawerValue) {
                    BackdropValue.Concealed -> viewModel.onOptionsExpandedChange(false)
                    BackdropValue.Revealed -> viewModel.onOptionsExpandedChange(true)
                }
                true
            }
        }
    ).apply {
        LaunchedEffect(backdropValue) {
            when (backdropValue) {
                BackdropValue.Concealed -> conceal()
                BackdropValue.Revealed -> reveal()
            }
        }
    }
}

@ScreenPreviews
@Composable
private fun Preview() = ClickTrackTheme {
    MetronomeScreenView(
        viewModel = object : MetronomeViewModel {
            override val state: StateFlow<MetronomeState?> = MutableStateFlow(
                MetronomeState(
                    bpm = 90.bpm,
                    pattern = NotePattern.QUINTUPLET_X2,
                    progress = PlayProgress(100.milliseconds),
                    isPlaying = false,
                    areOptionsExpanded = false,
                )
            )

            override fun onBackClick() = Unit
            override fun onToggleOptions() = Unit
            override fun onOptionsExpandedChange(isOpened: Boolean) = Unit
            override fun onPatternChoose(pattern: NotePattern) = Unit
            override fun onBpmChange(bpmDiff: BeatsPerMinuteDiff) = Unit
            override fun onTogglePlay() = Unit
            override fun onBpmMeterClick() = Unit
        }
    )
}