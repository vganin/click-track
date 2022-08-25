package com.vsevolodganin.clicktrack.edit

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.replaceCurrent
import com.vsevolodganin.clicktrack.Navigation
import com.vsevolodganin.clicktrack.ScreenConfiguration
import com.vsevolodganin.clicktrack.common.ClickTrackValidator
import com.vsevolodganin.clicktrack.model.CueDuration
import com.vsevolodganin.clicktrack.model.DefaultCue
import com.vsevolodganin.clicktrack.model.NotePattern
import com.vsevolodganin.clicktrack.model.TimeSignature
import com.vsevolodganin.clicktrack.storage.ClickTrackRepository
import com.vsevolodganin.clicktrack.utils.collection.immutable.remove
import com.vsevolodganin.clicktrack.utils.collection.immutable.replace
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class EditClickTrackViewModelImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val config: ScreenConfiguration.EditClickTrack,
    private val navigation: Navigation,
    private val clickTrackRepository: ClickTrackRepository,
    private val clickTrackValidator: ClickTrackValidator,
) : EditClickTrackViewModel, ComponentContext by componentContext {

    private val _state: MutableStateFlow<EditClickTrackState?> = MutableStateFlow(null)

    override val state: StateFlow<EditClickTrackState?> = _state

    init {
        GlobalScope.launch(Dispatchers.Unconfined, CoroutineStart.UNDISPATCHED) {
            _state.value = clickTrackRepository.getById(config.id)
                .map { it?.toEditState(showForwardButton = config.isInitialEdit) }
                .firstOrNull()

            _state
                .drop(1)
                .debounce(500.milliseconds)
                .collectLatest(::onEditStateChange)
        }
    }

    override fun onBackClick() = navigation.pop()

    override fun onForwardClick() = navigation.replaceCurrent(ScreenConfiguration.PlayClickTrack(config.id))

    override fun onNameChange(name: String) {
        reduceState { copy(name = name) }
    }

    override fun onLoopChange(loop: Boolean) {
        reduceState { copy(loop = loop) }
    }

    override fun onTempoDiffIncrementClick() {
        reduceState { copy(tempoDiff = this.tempoDiff + 1) }
    }

    override fun onTempoDiffDecrementClick() {
        reduceState { copy(tempoDiff = this.tempoDiff - 1) }
    }

    override fun onAddNewCueClick() {
        reduceState { copy(cues = cues + DefaultCue.toEditState()) }
    }

    override fun onCueRemove(index: Int) {
        reduceState { copy(cues = cues.remove(index)) }
    }

    override fun onCueNameChange(index: Int, name: String) {
        reduceState { copy(cues = cues.replace(index) { it.copy(name = name) }) }
    }

    override fun onCueBpmChange(index: Int, bpm: Int) {
        reduceState { copy(cues = cues.replace(index) { it.copy(bpm = bpm) }) }
    }

    override fun onCueTimeSignatureChange(index: Int, timeSignature: TimeSignature) {
        reduceState { copy(cues = cues.replace(index) { it.copy(timeSignature = timeSignature) }) }
    }

    override fun onCueDurationChange(index: Int, duration: CueDuration) {
        reduceState {
            copy(cues = cues.replace(index) {
                when (duration) {
                    is CueDuration.Beats -> it.copy(beats = duration)
                    is CueDuration.Measures -> it.copy(measures = duration)
                    is CueDuration.Time -> it.copy(time = duration)
                }
            })
        }
    }

    override fun onCueDurationTypeChange(index: Int, durationType: CueDuration.Type) {
        reduceState { copy(cues = cues.replace(index) { it.copy(activeDurationType = durationType) }) }
    }

    override fun onCuePatternChange(index: Int, pattern: NotePattern) {
        reduceState { copy(cues = cues.replace(index) { it.copy(pattern = pattern) }) }
    }

    private fun onEditStateChange(editState: EditClickTrackState?) {
        editState ?: return

        val validationResult = clickTrackValidator.validate(editState)

        clickTrackRepository.update(editState.id, validationResult.validClickTrack)

        reduceState {
            copy(cues = cues.mapIndexed { index, cue ->
                cue.copy(errors = validationResult.cueValidationResults[index].errors)
            })
        }
    }

    private fun reduceState(reduce: EditClickTrackState.() -> EditClickTrackState) {
        _state.update { it?.reduce() }
    }
}
