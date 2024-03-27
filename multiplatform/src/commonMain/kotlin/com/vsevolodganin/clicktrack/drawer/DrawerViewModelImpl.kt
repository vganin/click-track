package com.vsevolodganin.clicktrack.drawer

import com.arkivanov.decompose.Cancellation
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.subscribe
import com.vsevolodganin.clicktrack.ScreenConfiguration
import com.vsevolodganin.clicktrack.ScreenStack
import com.vsevolodganin.clicktrack.ScreenStackNavigation
import com.vsevolodganin.clicktrack.ScreenStackState
import com.vsevolodganin.clicktrack.utils.decompose.resetTo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class DrawerViewModelImpl(
    @Assisted componentContext: ComponentContext,
    private val navigation: ScreenStackNavigation,
    screenStackState: Lazy<ScreenStackState>, // Lazy to avoid cyclic dependency initialization
) : DrawerViewModel, ComponentContext by componentContext {

    private val _state: MutableStateFlow<DrawerState> = MutableStateFlow(
        DrawerState(
            isOpened = false,
            selectedItem = null,
        )
    )

    override val state: StateFlow<DrawerState> = _state

    private val onScreenStackChange = { stack: ScreenStack ->
        _state.update { it.copy(selectedItem = stack.active.configuration.toSelectedItem()) }
    }

    init {
        var cancellation: Cancellation? = null
        lifecycle.subscribe(
            onCreate = { cancellation = screenStackState.value.observe(onScreenStackChange) },
            onDestroy = { cancellation?.cancel() }
        )
    }

    override fun navigateToMetronome() = resetTo(ScreenConfiguration.Metronome)

    override fun navigateToTraining() = resetTo(ScreenConfiguration.Training)

    override fun navigateToPolyrhythms() = resetTo(ScreenConfiguration.Polyrhythms)

    override fun navigateToSoundLibrary() = resetTo(ScreenConfiguration.SoundLibrary)

    override fun navigateToSettings() = resetTo(ScreenConfiguration.Settings)

    override fun navigateToAbout() = resetTo(ScreenConfiguration.About)

    private fun resetTo(config: ScreenConfiguration) {
        closeDrawer()
        navigation.resetTo(config)
    }

    override fun openDrawer() = _state.update { it.copy(isOpened = true) }

    override fun closeDrawer() = _state.update { it.copy(isOpened = false) }

    private fun ScreenConfiguration.toSelectedItem(): DrawerState.SelectedItem? {
        return when (this) {
            ScreenConfiguration.Metronome -> DrawerState.SelectedItem.METRONOME
            ScreenConfiguration.Training -> DrawerState.SelectedItem.TRAINING
            ScreenConfiguration.Settings -> DrawerState.SelectedItem.SETTINGS
            ScreenConfiguration.SoundLibrary -> DrawerState.SelectedItem.SOUND_LIBRARY
            ScreenConfiguration.About -> DrawerState.SelectedItem.ABOUT
            ScreenConfiguration.Polyrhythms -> DrawerState.SelectedItem.POLYRHYTHMS
            ScreenConfiguration.ClickTrackList,
            is ScreenConfiguration.EditClickTrack,
            is ScreenConfiguration.PlayClickTrack -> null
        }
    }
}
