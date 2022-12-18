package com.vsevolodganin.clicktrack.play

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.pop
import com.vsevolodganin.clicktrack.Navigation
import com.vsevolodganin.clicktrack.ScreenConfiguration
import com.vsevolodganin.clicktrack.export.ExportWorkLauncher
import com.vsevolodganin.clicktrack.player.PlayerServiceAccess
import com.vsevolodganin.clicktrack.storage.ClickTrackRepository
import com.vsevolodganin.clicktrack.storage.UserPreferencesRepository
import com.vsevolodganin.clicktrack.utils.decompose.consumeSavedState
import com.vsevolodganin.clicktrack.utils.decompose.coroutineScope
import com.vsevolodganin.clicktrack.utils.decompose.pushIfUnique
import com.vsevolodganin.clicktrack.utils.decompose.registerSaveStateFor
import com.vsevolodganin.clicktrack.utils.grabIf
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlayClickTrackViewModelImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val config: ScreenConfiguration.PlayClickTrack,
    private val navigation: Navigation,
    private val clickTrackRepository: ClickTrackRepository,
    private val playerServiceAccess: PlayerServiceAccess,
    private val userPreferences: UserPreferencesRepository,
    private val exportWorkLauncher: ExportWorkLauncher,
) : PlayClickTrackViewModel, ComponentContext by componentContext {

    private val scope = coroutineScope()

    override val state: StateFlow<PlayClickTrackState?> = combine(
        clickTrackRepository.getById(config.id).filterNotNull(),
        userPreferences.playTrackingMode.flow,
        playerServiceAccess.playbackState(),
    ) { clickTrack, playTrackingMode, playbackState ->
        PlayClickTrackState(
            clickTrack = clickTrack,
            playProgress = grabIf(playbackState?.id == clickTrack.id) { playbackState?.progress },
            playTrackingMode = playTrackingMode,
        )
    }.stateIn(scope, SharingStarted.Eagerly, consumeSavedState())

    init {
        registerSaveStateFor(state)
    }

    override fun onBackClick() = navigation.pop()

    override fun onTogglePlayStop() {
        val state = state.value ?: return
        if (state.isPlaying) {
            playerServiceAccess.stop()
        } else {
            playerServiceAccess.start(config.id)
        }
    }

    override fun onTogglePlayPause() {
        val state = state.value ?: return
        if (state.isPaused) {
            playerServiceAccess.resume()
        } else {
            playerServiceAccess.pause()
        }
    }

    override fun onTogglePlayTrackingMode() = userPreferences.playTrackingMode.edit { !it }

    override fun onProgressDragStart() = playerServiceAccess.pause()

    override fun onProgressDrop(progress: Double) = playerServiceAccess.start(config.id, progress)

    override fun onEditClick() = navigation.pushIfUnique(ScreenConfiguration.EditClickTrack(config.id, isInitialEdit = false))

    override fun onRemoveClick() {
        scope.launch {
            clickTrackRepository.remove(config.id)
            withContext(Dispatchers.Main) {
                navigation.pop()
            }
        }
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
