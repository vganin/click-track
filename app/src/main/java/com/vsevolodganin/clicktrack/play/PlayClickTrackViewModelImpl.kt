package com.vsevolodganin.clicktrack.play

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.vsevolodganin.clicktrack.Navigation
import com.vsevolodganin.clicktrack.ScreenConfiguration
import com.vsevolodganin.clicktrack.export.ExportWorkLauncher
import com.vsevolodganin.clicktrack.model.ClickTrackWithDatabaseId
import com.vsevolodganin.clicktrack.player.PlaybackState
import com.vsevolodganin.clicktrack.player.PlayerServiceAccess
import com.vsevolodganin.clicktrack.storage.ClickTrackRepository
import com.vsevolodganin.clicktrack.storage.UserPreferencesRepository
import com.vsevolodganin.clicktrack.utils.decompose.coroutineScope
import com.vsevolodganin.clicktrack.utils.grabIf
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PlayClickTrackViewModelImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val config: ScreenConfiguration.PlayClickTrack,
    private val navigation: Navigation,
    private val clickTrackRepository: ClickTrackRepository,
    private val playerServiceAccess: PlayerServiceAccess,
    private val userPreferences: UserPreferencesRepository,
    private val exportWorkLauncher: ExportWorkLauncher,
) : PlayClickTrackViewModel, ComponentContext by componentContext {

    private val scope = coroutineScope(Dispatchers.Main)

    override val state: StateFlow<PlayClickTrackState?> = combine(
        clickTrackRepository.getById(config.id),
        userPreferences.playTrackingMode.flow,
        playerServiceAccess.playbackState(),
        ::combineToState,
    ).stateIn(scope, SharingStarted.Eagerly, null)

    private fun combineToState(
        clickTrack: ClickTrackWithDatabaseId?,
        playTrackingMode: Boolean,
        playbackState: PlaybackState?,
    ): PlayClickTrackState? {
        clickTrack ?: return null

        val isPlaying = playbackState?.id == clickTrack.id

        return PlayClickTrackState(
            clickTrack = clickTrack,
            isPlaying = isPlaying,
            playProgress = grabIf(isPlaying) { playbackState?.progress },
            playTrackingMode = playTrackingMode,
        )
    }

    override fun onBackClick() = navigation.pop()

    override fun onTogglePlay() {
        val state = state.value ?: return
        if (state.isPlaying) {
            playerServiceAccess.stop()
        } else {
            playerServiceAccess.start(config.id)
        }
    }

    override fun onTogglePlayTrackingMode() {
        val state = state.value ?: return
        userPreferences.playTrackingMode.edit {
            !state.playTrackingMode
        }
    }

    override fun onProgressDragStart() = playerServiceAccess.pause()

    override fun onProgressDrop(progress: Double) = playerServiceAccess.start(config.id, progress)

    override fun onEditClick() = navigation.push(ScreenConfiguration.EditClickTrack(config.id, isInitialEdit = false))

    override fun onRemoveClick() {
        clickTrackRepository.remove(config.id)
        navigation.pop()
    }

    override fun onExportClick() {
        scope.launch {
            exportWorkLauncher.launchExportToAudioFile(config.id)
        }
    }

    override fun onCancelExportClick() {
        scope.launch {
            exportWorkLauncher.stopExportToAudioFile(config.id)
        }
    }
}
