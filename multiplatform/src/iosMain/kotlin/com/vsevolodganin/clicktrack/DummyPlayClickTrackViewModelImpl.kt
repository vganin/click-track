package com.vsevolodganin.clicktrack

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.vsevolodganin.clicktrack.play.PlayClickTrackState
import com.vsevolodganin.clicktrack.play.PlayClickTrackViewModel
import com.vsevolodganin.clicktrack.storage.ClickTrackRepository
import com.vsevolodganin.clicktrack.storage.UserPreferencesRepository
import com.vsevolodganin.clicktrack.utils.decompose.consumeSavedState
import com.vsevolodganin.clicktrack.utils.decompose.coroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class DummyPlayClickTrackViewModelImpl(
    @Assisted componentContext: ComponentContext,
    @Assisted private val config: ScreenConfiguration.PlayClickTrack,
    private val navigation: Navigation,
    clickTrackRepository: ClickTrackRepository,
    userPreferences: UserPreferencesRepository,
) : PlayClickTrackViewModel, ComponentContext by componentContext {

    private val scope = coroutineScope()

    override val state: StateFlow<PlayClickTrackState?> = combine(
        clickTrackRepository.getById(config.id).filterNotNull(),
        userPreferences.playTrackingMode.flow,
    ) { clickTrack, playTrackingMode ->
        PlayClickTrackState(
            clickTrack = clickTrack,
            playProgress = null,
            playTrackingMode = playTrackingMode,
        )
    }.stateIn(scope, SharingStarted.Eagerly, consumeSavedState())

    override fun onBackClick() = navigation.pop()
    override fun onTogglePlayStop() = Unit
    override fun onTogglePlayPause() = Unit
    override fun onTogglePlayTrackingMode() = Unit
    override fun onProgressDragStart() = Unit
    override fun onProgressDrop(progress: Double) = Unit
    override fun onEditClick() = navigation.push(ScreenConfiguration.EditClickTrack(config.id, false))
    override fun onRemoveClick() = Unit
    override fun onExportClick() = Unit
    override fun onCancelExportClick() = Unit
}
