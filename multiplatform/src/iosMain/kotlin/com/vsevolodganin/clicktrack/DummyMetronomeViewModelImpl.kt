package com.vsevolodganin.clicktrack

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.pop
import com.vsevolodganin.clicktrack.metronome.MetronomeState
import com.vsevolodganin.clicktrack.metronome.MetronomeViewModel
import com.vsevolodganin.clicktrack.model.BeatsPerMinuteDiff
import com.vsevolodganin.clicktrack.model.NotePattern
import com.vsevolodganin.clicktrack.model.PlayProgress
import com.vsevolodganin.clicktrack.model.bpm
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import kotlin.time.Duration.Companion.milliseconds

@Inject
class DummyMetronomeViewModelImpl(
    @Assisted componentContext: ComponentContext,
    private val navigation: Navigation,
) : MetronomeViewModel, ComponentContext by componentContext {
    override val state: StateFlow<MetronomeState?> = MutableStateFlow(
        MetronomeState(
            bpm = 90.bpm,
            pattern = NotePattern.QUINTUPLET_X2,
            progress = PlayProgress(100.milliseconds),
            isPlaying = false,
            areOptionsExpanded = false,
        )
    )

    override fun onBackClick() = navigation.pop()
    override fun onToggleOptions() = Unit
    override fun onOptionsExpandedChange(isOpened: Boolean) = Unit
    override fun onPatternChoose(pattern: NotePattern) = Unit
    override fun onBpmChange(bpmDiff: BeatsPerMinuteDiff) = Unit
    override fun onTogglePlay() = Unit
    override fun onBpmMeterClick() = Unit
}
