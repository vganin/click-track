package com.vsevolodganin.clicktrack

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.pop
import com.vsevolodganin.clicktrack.edit.EditClickTrackState
import com.vsevolodganin.clicktrack.edit.EditClickTrackViewModel
import com.vsevolodganin.clicktrack.edit.toEditState
import com.vsevolodganin.clicktrack.model.CueDuration
import com.vsevolodganin.clicktrack.model.NotePattern
import com.vsevolodganin.clicktrack.model.TimeSignature
import com.vsevolodganin.clicktrack.ui.preview.PREVIEW_CLICK_TRACK_1
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class DummyEditClickTrackViewModelImpl(
    @Assisted componentContext: ComponentContext,
    @Assisted private val config: ScreenConfiguration.EditClickTrack,
    private val navigation: Navigation,
) : EditClickTrackViewModel, ComponentContext by componentContext {

    override val state: StateFlow<EditClickTrackState?> = MutableStateFlow(
        PREVIEW_CLICK_TRACK_1.toEditState(showForwardButton = true)
    )

    override fun onBackClick() = navigation.pop()
    override fun onForwardClick() = Unit
    override fun onNameChange(name: String) = Unit
    override fun onLoopChange(loop: Boolean) = Unit
    override fun onTempoDiffIncrementClick() = Unit
    override fun onTempoDiffDecrementClick() = Unit
    override fun onAddNewCueClick() = Unit
    override fun onCueRemove(index: Int) = Unit
    override fun onCueNameChange(index: Int, name: String) = Unit
    override fun onCueBpmChange(index: Int, bpm: Int) = Unit
    override fun onCueTimeSignatureChange(index: Int, timeSignature: TimeSignature) = Unit
    override fun onCueDurationChange(index: Int, duration: CueDuration) = Unit
    override fun onCueDurationTypeChange(index: Int, durationType: CueDuration.Type) = Unit
    override fun onCuePatternChange(index: Int, pattern: NotePattern) = Unit
}
