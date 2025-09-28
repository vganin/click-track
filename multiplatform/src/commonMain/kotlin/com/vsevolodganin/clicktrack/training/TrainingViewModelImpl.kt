package com.vsevolodganin.clicktrack.training

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.replaceCurrent
import com.vsevolodganin.clicktrack.ScreenConfiguration
import com.vsevolodganin.clicktrack.ScreenStackNavigation
import com.vsevolodganin.clicktrack.common.NewClickTrackNameSuggester
import com.vsevolodganin.clicktrack.model.CueDuration
import com.vsevolodganin.clicktrack.model.DefaultBeatsDuration
import com.vsevolodganin.clicktrack.model.DefaultMeasuresDuration
import com.vsevolodganin.clicktrack.model.DefaultTimeDuration
import com.vsevolodganin.clicktrack.storage.ClickTrackRepository
import com.vsevolodganin.clicktrack.storage.UserPreferencesRepository
import com.vsevolodganin.clicktrack.utils.decompose.coroutineScope
import com.vsevolodganin.clicktrack.utils.optionalCast
import com.vsevolodganin.clicktrack.utils.resources.StringResolver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class TrainingViewModelImpl(
    @Assisted componentContext: ComponentContext,
    private val stringResolver: StringResolver,
    private val navigation: ScreenStackNavigation,
    private val clickTrackRepository: ClickTrackRepository,
    private val userPreferences: UserPreferencesRepository,
    private val stateValidator: TrainingStateValidator,
    private val newClickTrackNameSuggester: NewClickTrackNameSuggester,
    private val trainingClickTrackGenerator: TrainingClickTrackGenerator,
) : TrainingViewModel, ComponentContext by componentContext {
    private val scope = coroutineScope()

    private val _state: MutableStateFlow<TrainingEditState> = MutableStateFlow(
        userPreferences.trainingState.value.toEditState(),
    )

    override val state: StateFlow<TrainingEditState> = _state

    init {
        scope.launch {
            _state
                .drop(1)
                .collectLatest(::onTrainingStateChange)
        }
    }

    override fun onBackClick() = navigation.pop()

    override fun onAcceptClick() {
        scope.launch {
            val trainingState = userPreferences.trainingState.flow.first()
            val suggestedName = newClickTrackNameSuggester.suggest(
                // TODO: Update StringResolver to work with compose resources
                "Training", // was: stringResolver.resolve(MR.strings.general_unnamed_training_click_track_template)
            )
            val newClickTrack = trainingClickTrackGenerator.generate(trainingState, suggestedName)
            val newClickTrackId = clickTrackRepository.insert(newClickTrack)

            withContext(Dispatchers.Main) {
                navigation.replaceCurrent(ScreenConfiguration.PlayClickTrack(newClickTrackId))
            }
        }
    }

    override fun onStartingTempoChange(startingTempo: Int) {
        reduceState { copy(startingTempo = startingTempo) }
    }

    override fun onModeSelect(mode: TrainingEditState.TrainingMode) {
        reduceState { copy(mode = mode) }
    }

    override fun onSegmentLengthChange(segmentLength: CueDuration) {
        reduceState {
            when (segmentLength) {
                is CueDuration.Beats -> copy(segmentLengthBeats = segmentLength)
                is CueDuration.Measures -> copy(segmentLengthMeasures = segmentLength)
                is CueDuration.Time -> copy(segmentLengthTime = segmentLength)
            }
        }
    }

    override fun onSegmentLengthTypeChange(segmentLengthType: CueDuration.Type) {
        reduceState { copy(activeSegmentLengthType = segmentLengthType) }
    }

    override fun onTempoChangeChange(tempoChange: Int) {
        reduceState { copy(tempoChange = tempoChange) }
    }

    override fun onEndingChange(ending: TrainingEditState.Ending) {
        reduceState {
            when (ending) {
                is TrainingEditState.Ending.ByTempo -> copy(endingByTempo = ending)
                is TrainingEditState.Ending.ByTime -> copy(endingByTime = ending)
            }
        }
    }

    override fun onEndingKindChange(endingKind: TrainingEndingKind) {
        reduceState { copy(activeEndingKind = endingKind) }
    }

    private fun onTrainingStateChange(trainingState: TrainingEditState?) {
        trainingState ?: return

        val stateValidationResult = stateValidator.validate(trainingState)

        stateValidationResult.persistableState?.let { persistableState ->
            userPreferences.trainingState.value = persistableState
        }

        reduceState {
            copy(errors = stateValidationResult.errors)
        }
    }

    private fun reduceState(reduce: TrainingEditState.() -> TrainingEditState) {
        _state.update { it.reduce() }
    }

    private fun TrainingValidState.toEditState(): TrainingEditState {
        val endingEditState = ending.toEditState()
        return TrainingEditState(
            startingTempo = startingTempo.value,
            mode = mode,
            activeSegmentLengthType = segmentLength.type,
            segmentLengthBeats = segmentLength.optionalCast<CueDuration.Beats>() ?: DefaultBeatsDuration,
            segmentLengthMeasures = segmentLength.optionalCast<CueDuration.Measures>() ?: DefaultMeasuresDuration,
            segmentLengthTime = segmentLength.optionalCast<CueDuration.Time>() ?: DefaultTimeDuration,
            tempoChange = tempoChange.value,
            activeEndingKind = endingEditState.kind,
            endingByTempo = endingEditState.optionalCast<TrainingEditState.Ending.ByTempo>() ?: DefaultEndingByTempo,
            endingByTime = endingEditState.optionalCast<TrainingEditState.Ending.ByTime>() ?: DefaultEndingByTime,
            errors = emptySet(),
        )
    }

    private fun TrainingValidState.Ending.toEditState(): TrainingEditState.Ending = when (this) {
        is TrainingValidState.Ending.ByTempo -> TrainingEditState.Ending.ByTempo(endingTempo.value)
        is TrainingValidState.Ending.ByTime -> TrainingEditState.Ending.ByTime(duration)
    }
}
