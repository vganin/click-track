package com.vsevolodganin.clicktrack

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.vsevolodganin.clicktrack.play.PlayClickTrackState
import com.vsevolodganin.clicktrack.play.PlayClickTrackViewModel
import com.vsevolodganin.clicktrack.ui.preview.PREVIEW_CLICK_TRACK_1
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class DummyPlayClickTrackViewModelImpl(
    @Assisted componentContext: ComponentContext,
    @Assisted private val config: ScreenConfiguration.PlayClickTrack,
    private val navigation: Navigation,
) : PlayClickTrackViewModel, ComponentContext by componentContext {

    override val state: StateFlow<PlayClickTrackState?> = MutableStateFlow(
        PlayClickTrackState(
            clickTrack = PREVIEW_CLICK_TRACK_1,
            playProgress = null,
            playTrackingMode = true,
        )
    )

    override fun onBackClick() = navigation.pop()
    override fun onTogglePlayStop() = Unit
    override fun onTogglePlayPause() = Unit
    override fun onTogglePlayTrackingMode() = Unit
    override fun onProgressDragStart() = Unit
    override fun onProgressDrop(progress: Double) = Unit
    override fun onEditClick() = navigation.push(ScreenConfiguration.EditClickTrack(PREVIEW_CLICK_TRACK_1.id, false))
    override fun onRemoveClick() = Unit
    override fun onExportClick() = Unit
    override fun onCancelExportClick() = Unit
}
