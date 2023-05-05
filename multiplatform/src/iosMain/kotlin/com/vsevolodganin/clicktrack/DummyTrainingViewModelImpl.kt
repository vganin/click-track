package com.vsevolodganin.clicktrack

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.pop
import com.vsevolodganin.clicktrack.model.CueDuration
import com.vsevolodganin.clicktrack.model.DefaultBeatsDuration
import com.vsevolodganin.clicktrack.model.DefaultMeasuresDuration
import com.vsevolodganin.clicktrack.model.DefaultTimeDuration
import com.vsevolodganin.clicktrack.training.TrainingEditState
import com.vsevolodganin.clicktrack.training.TrainingEndingKind
import com.vsevolodganin.clicktrack.training.TrainingViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import kotlin.time.Duration.Companion.minutes

@Inject
class DummyTrainingViewModelImpl(
    @Assisted componentContext: ComponentContext,
    private val navigation: Navigation,
) : TrainingViewModel, ComponentContext by componentContext {

    override val state: StateFlow<TrainingEditState> = MutableStateFlow(
        TrainingEditState(
            startingTempo = 120,
            mode = TrainingEditState.TrainingMode.INCREASE_TEMPO,
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

    override fun onBackClick() = navigation.pop()
    override fun onAcceptClick() = Unit
    override fun onStartingTempoChange(startingTempo: Int) = Unit
    override fun onModeSelect(mode: TrainingEditState.TrainingMode) = Unit
    override fun onSegmentLengthChange(segmentLength: CueDuration) = Unit
    override fun onSegmentLengthTypeChange(segmentLengthType: CueDuration.Type) = Unit
    override fun onTempoChangeChange(tempoChange: Int) = Unit
    override fun onEndingChange(ending: TrainingEditState.Ending) = Unit
    override fun onEndingKindChange(endingKind: TrainingEndingKind) = Unit
}
