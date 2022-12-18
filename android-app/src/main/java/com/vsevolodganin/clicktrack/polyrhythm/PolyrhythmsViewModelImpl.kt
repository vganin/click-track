package com.vsevolodganin.clicktrack.polyrhythm

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.pop
import com.vsevolodganin.clicktrack.Navigation
import com.vsevolodganin.clicktrack.model.TwoLayerPolyrhythm
import com.vsevolodganin.clicktrack.model.TwoLayerPolyrhythmId
import com.vsevolodganin.clicktrack.player.PlayerServiceAccess
import com.vsevolodganin.clicktrack.storage.UserPreferencesRepository
import com.vsevolodganin.clicktrack.utils.decompose.consumeSavedState
import com.vsevolodganin.clicktrack.utils.decompose.coroutineScope
import com.vsevolodganin.clicktrack.utils.decompose.registerSaveStateFor
import com.vsevolodganin.clicktrack.utils.grabIf
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class PolyrhythmsViewModelImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    private val navigation: Navigation,
    private val userPreferences: UserPreferencesRepository,
    private val playerServiceAccess: PlayerServiceAccess,
) : PolyrhythmsViewModel, ComponentContext by componentContext {

    private val scope = coroutineScope()

    override val state: StateFlow<PolyrhythmsState?> = combine(
        userPreferences.polyrhythm.flow,
        playerServiceAccess.playbackState()
    ) { twoLayerPolyrhythm, playbackState ->
        val isPlaying = playbackState?.id == TwoLayerPolyrhythmId
        PolyrhythmsState(
            twoLayerPolyrhythm = twoLayerPolyrhythm,
            isPlaying = isPlaying,
            playableProgress = grabIf(isPlaying) { playbackState?.progress }
        )
    }.stateIn(scope, SharingStarted.Eagerly, consumeSavedState())

    init {
        registerSaveStateFor(state)
    }

    override fun onBackClick() = navigation.pop()

    override fun onTogglePlay() {
        val state = state.value ?: return
        if (state.isPlaying) {
            playerServiceAccess.stop()
        } else {
            playerServiceAccess.start(TwoLayerPolyrhythmId)
        }
    }

    override fun onLayer1Change(value: Int) {
        userPreferences.polyrhythm.edit {
            it.copy(layer1 = value).takeIfPlayable() ?: it
        }
    }

    override fun onLayer2Change(value: Int) {
        userPreferences.polyrhythm.edit {
            it.copy(layer2 = value).takeIfPlayable() ?: it
        }
    }

    private fun TwoLayerPolyrhythm.takeIfPlayable() = takeIf { it.isPlayable() }
}