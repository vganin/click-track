package com.vsevolodganin.clicktrack

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.decompose.Child
import com.arkivanov.decompose.value.MutableValue
import com.vsevolodganin.clicktrack.about.AboutState
import com.vsevolodganin.clicktrack.about.AboutViewModel
import com.vsevolodganin.clicktrack.drawer.DrawerState
import com.vsevolodganin.clicktrack.drawer.DrawerViewModel
import com.vsevolodganin.clicktrack.edit.EditClickTrackViewModel
import com.vsevolodganin.clicktrack.list.ClickTrackListViewModel
import com.vsevolodganin.clicktrack.metronome.MetronomeViewModel
import com.vsevolodganin.clicktrack.play.PlayClickTrackViewModel
import com.vsevolodganin.clicktrack.polyrhythm.PolyrhythmsViewModel
import com.vsevolodganin.clicktrack.settings.SettingsViewModel
import com.vsevolodganin.clicktrack.soundlibrary.SoundLibraryViewModel
import com.vsevolodganin.clicktrack.training.TrainingViewModel
import com.vsevolodganin.clicktrack.ui.ComposableProvider
import com.vsevolodganin.clicktrack.ui.RootView
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

fun MainViewController() = ComposeUIViewController {
    RootView(
        viewModel = object : RootViewModel {
            override val drawer: DrawerViewModel = object : DrawerViewModel {
                override val state: StateFlow<DrawerState> = MutableStateFlow(
                    DrawerState(
                        isOpened = false,
                        selectedItem = null,
                    )
                )

                override fun openDrawer() = Unit
                override fun closeDrawer() = Unit
                override fun navigateToMetronome() = Unit
                override fun navigateToTraining() = Unit
                override fun navigateToPolyrhythms() = Unit
                override fun navigateToSoundLibrary() = Unit
                override fun navigateToSettings() = Unit
                override fun navigateToAbout() = Unit
            }
            override val screens: ScreenStackState = MutableValue(
                ScreenStack(
                    active = Child.Created(
                        ScreenConfiguration.About,
                        ScreenViewModel.About(object : AboutViewModel {
                            override val state: StateFlow<AboutState> = MutableStateFlow(
                                AboutState(
                                    displayVersion = "6.6.6"
                                )
                            )

                            override fun onBackClick() = Unit
                            override fun onHomeClick() = Unit
                            override fun onTwitterClick() = Unit
                            override fun onEmailClick() = Unit
                            override fun onArtstationClick() = Unit
                            override fun onProjectLinkClick() = Unit
                        })
                    )
                )
            )
        },
        composableProvider = object : ComposableProvider {
            override val theme: @Composable (@Composable () -> Unit) -> Unit
                get() = @Composable { it() }
            override val drawer: @Composable (DrawerViewModel) -> Unit
                get() = @Composable {}
            override val clickTrackList: @Composable (ClickTrackListViewModel, Modifier) -> Unit
                get() = @Composable { _, _ -> }
            override val playClickTrack: @Composable (PlayClickTrackViewModel, Modifier) -> Unit
                get() = @Composable { _, _ -> }
            override val editClickTrack: @Composable (EditClickTrackViewModel, Modifier) -> Unit
                get() = @Composable { _, _ -> }
            override val metronome: @Composable (MetronomeViewModel, Modifier) -> Unit
                get() = @Composable { _, _ -> }
            override val polyrhythms: @Composable (PolyrhythmsViewModel, Modifier) -> Unit
                get() = @Composable { _, _ -> }
            override val settings: @Composable (SettingsViewModel, Modifier) -> Unit
                get() = @Composable { _, _ -> }
            override val soundLibrary: @Composable (SoundLibraryViewModel, Modifier) -> Unit
                get() = @Composable { _, _ -> }
            override val training: @Composable (TrainingViewModel, Modifier) -> Unit
                get() = @Composable { _, _ -> }
        })
}
