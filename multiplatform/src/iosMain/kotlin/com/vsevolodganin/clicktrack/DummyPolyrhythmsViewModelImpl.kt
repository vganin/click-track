package com.vsevolodganin.clicktrack

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.pop
import com.vsevolodganin.clicktrack.model.PlayProgress
import com.vsevolodganin.clicktrack.model.TwoLayerPolyrhythm
import com.vsevolodganin.clicktrack.model.bpm
import com.vsevolodganin.clicktrack.polyrhythm.PolyrhythmsState
import com.vsevolodganin.clicktrack.polyrhythm.PolyrhythmsViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import kotlin.time.Duration.Companion.milliseconds

@Inject
class DummyPolyrhythmsViewModelImpl(
    @Assisted componentContext: ComponentContext,
    private val navigation: Navigation,
) : PolyrhythmsViewModel, ComponentContext by componentContext {

    override val state: StateFlow<PolyrhythmsState?> = MutableStateFlow(
        PolyrhythmsState(
            twoLayerPolyrhythm = TwoLayerPolyrhythm(
                bpm = 120.bpm,
                layer1 = 3,
                layer2 = 2
            ),
            isPlaying = true,
            playableProgress = PlayProgress(100.milliseconds)
        )
    )

    override fun onBackClick() = navigation.pop()
    override fun onTogglePlay() = Unit
    override fun onLayer1Change(value: Int) = Unit
    override fun onLayer2Change(value: Int) = Unit
}
