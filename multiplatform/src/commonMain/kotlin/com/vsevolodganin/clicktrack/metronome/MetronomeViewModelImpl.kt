package com.vsevolodganin.clicktrack.metronome

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.pop
import com.vsevolodganin.clicktrack.ScreenStackNavigation
import com.vsevolodganin.clicktrack.model.BeatsPerMinuteOffset
import com.vsevolodganin.clicktrack.model.ClickTrackId
import com.vsevolodganin.clicktrack.model.NotePattern
import com.vsevolodganin.clicktrack.model.TimeSignature
import com.vsevolodganin.clicktrack.player.PlayerServiceAccess
import com.vsevolodganin.clicktrack.storage.UserPreferencesRepository
import com.vsevolodganin.clicktrack.utils.decompose.consumeSavedState
import com.vsevolodganin.clicktrack.utils.decompose.coroutineScope
import com.vsevolodganin.clicktrack.utils.decompose.registerSaveStateFor
import com.vsevolodganin.clicktrack.utils.grabIf
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

@AssistedInject
class MetronomeViewModelImpl(
    @Assisted componentContext: ComponentContext,
    private val navigation: ScreenStackNavigation,
    private val userPreferences: UserPreferencesRepository,
    private val playerServiceAccess: PlayerServiceAccess,
    private val bpmMeter: BpmMeter,
) : MetronomeViewModel, ComponentContext by componentContext {

    @AssistedFactory
    fun interface Factory {
        fun create(
            componentContext: ComponentContext,
        ): MetronomeViewModelImpl
    }

    private val scope = coroutineScope()

    private val areOptionsExpanded: MutableStateFlow<Boolean>

    override val state: StateFlow<MetronomeState?>

    init {
        val initialState: MetronomeState? = consumeSavedState()

        areOptionsExpanded = MutableStateFlow(initialState?.areOptionsExpanded ?: false)

        state = combine(
            areOptionsExpanded,
            userPreferences.metronomeBpm.flow,
            userPreferences.metronomePattern.flow,
            userPreferences.metronomeTimeSignature.flow,
            playerServiceAccess.playbackState(),
        ) { areOptionsExpanded, bpm, pattern, timeSignature, playbackState ->
            val isPlaying = playbackState?.id is ClickTrackId.Builtin.Metronome
            MetronomeState(
                bpm = bpm,
                pattern = pattern,
                timeSignature = timeSignature,
                isPlaying = isPlaying,
                progress = grabIf(isPlaying) { playbackState?.progress },
                areOptionsExpanded = areOptionsExpanded,
            )
        }.stateIn(scope, SharingStarted.Eagerly, initialState)

        registerSaveStateFor(state)
    }

    override fun onBackClick() = navigation.pop()

    override fun onToggleOptions() {
        areOptionsExpanded.update { !it }
    }

    override fun onOptionsExpandedChange(isOpened: Boolean) {
        areOptionsExpanded.value = isOpened
    }

    override fun onPatternChoose(pattern: NotePattern) {
        userPreferences.metronomePattern.value = pattern
        areOptionsExpanded.value = false
    }

    override fun onTimeSignatureChange(timeSignature: TimeSignature) {
        userPreferences.metronomeTimeSignature.value = timeSignature
    }

    override fun onBpmChange(bpmDiff: BeatsPerMinuteOffset) {
        userPreferences.metronomeBpm.edit { it + bpmDiff }
    }

    override fun onTogglePlay() {
        val state = state.value ?: return
        if (state.isPlaying) {
            playerServiceAccess.stop()
        } else {
            playerServiceAccess.start(ClickTrackId.Builtin.Metronome)
        }
    }

    override fun onBpmMeterClick() {
        bpmMeter.addTap()
        bpmMeter.calculateBpm()?.let { newBpm ->
            userPreferences.metronomeBpm.value = newBpm
        }
    }
}
