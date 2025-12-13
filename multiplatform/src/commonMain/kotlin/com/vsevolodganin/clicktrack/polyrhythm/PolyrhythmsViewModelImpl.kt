package com.vsevolodganin.clicktrack.polyrhythm

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.pop
import com.vsevolodganin.clicktrack.ScreenStackNavigation
import com.vsevolodganin.clicktrack.model.TwoLayerPolyrhythmId
import com.vsevolodganin.clicktrack.player.PlayerServiceAccess
import com.vsevolodganin.clicktrack.storage.UserPreferencesRepository
import com.vsevolodganin.clicktrack.utils.decompose.consumeSavedState
import com.vsevolodganin.clicktrack.utils.decompose.coroutineScope
import com.vsevolodganin.clicktrack.utils.decompose.registerSaveStateFor
import com.vsevolodganin.clicktrack.utils.grabIf
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import me.tatarka.inject.annotations.Assisted
import dev.zacsweers.metro.Inject

@Inject
class PolyrhythmsViewModelImpl(
    @Assisted componentContext: ComponentContext,
    private val navigation: ScreenStackNavigation,
    private val userPreferences: UserPreferencesRepository,
    private val playerServiceAccess: PlayerServiceAccess,
) : PolyrhythmsViewModel, ComponentContext by componentContext {
    private val scope = coroutineScope()

    override val state: StateFlow<PolyrhythmsState?> = combine(
        userPreferences.polyrhythm.flow,
        playerServiceAccess.playbackState(),
    ) { twoLayerPolyrhythm, playbackState ->
        val isPlaying = playbackState?.id == TwoLayerPolyrhythmId
        PolyrhythmsState(
            twoLayerPolyrhythm = twoLayerPolyrhythm,
            isPlaying = isPlaying,
            playableProgress = grabIf(isPlaying) { playbackState?.progress },
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
            it.copy(layer1 = value.coerceInValidLayerValues())
        }
    }

    override fun onLayer2Change(value: Int) {
        userPreferences.polyrhythm.edit {
            it.copy(layer2 = value.coerceInValidLayerValues())
        }
    }

    private fun Int.coerceInValidLayerValues() = coerceIn(1..32)
}
