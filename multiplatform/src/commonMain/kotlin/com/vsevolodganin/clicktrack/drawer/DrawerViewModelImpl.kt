package com.vsevolodganin.clicktrack.drawer

import com.arkivanov.decompose.Cancellation
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.Lifecycle
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
    private val stackNavigation: ScreenStackNavigation,
    private val drawerNavigationSource: DrawerNavigationSource,
    screenStackState: ScreenStackState,
) : DrawerViewModel, ComponentContext by componentContext {
    private val _state: MutableStateFlow<DrawerState> = MutableStateFlow(
        DrawerState(
            isOpened = false,
            selectedItem = null,
        ),
    )

    override val state: StateFlow<DrawerState> = _state

    private val onScreenStackChange = { stack: ScreenStack ->
        _state.update { it.copy(selectedItem = stack.active.configuration.toSelectedItem()) }
    }

    init {
        lifecycle.subscribe(object : Lifecycle.Callbacks {
            var cancellation: Cancellation? = null

            override fun onCreate() {
                cancellation = drawerNavigationSource.subscribe(::updateOpenedState)
            }

            override fun onDestroy() {
                cancellation?.cancel()
                cancellation = null
            }
        })

        lifecycle.subscribe(object : Lifecycle.Callbacks {
            var cancellation: Cancellation? = null

            override fun onCreate() {
                cancellation = screenStackState.subscribe(onScreenStackChange)
            }

            override fun onDestroy() {
                cancellation?.cancel()
                cancellation = null
            }
        })
    }

    override fun navigateToMetronome() = resetTo(ScreenConfiguration.Metronome)

    override fun navigateToTraining() = resetTo(ScreenConfiguration.Training)

    override fun navigateToPolyrhythms() = resetTo(ScreenConfiguration.Polyrhythms)

    override fun navigateToSoundLibrary() = resetTo(ScreenConfiguration.SoundLibrary)

    override fun navigateToSettings() = resetTo(ScreenConfiguration.Settings)

    override fun navigateToAbout() = resetTo(ScreenConfiguration.About)

    private fun resetTo(config: ScreenConfiguration) {
        closeDrawer()
        stackNavigation.resetTo(config)
    }

    override fun openDrawer() = updateOpenedState(isOpened = true)

    override fun closeDrawer() = updateOpenedState(isOpened = false)

    private fun updateOpenedState(isOpened: Boolean) = _state.update { it.copy(isOpened = isOpened) }

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
            is ScreenConfiguration.PlayClickTrack,
            -> null
        }
    }
}
