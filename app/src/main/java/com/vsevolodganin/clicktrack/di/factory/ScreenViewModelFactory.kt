package com.vsevolodganin.clicktrack.di.factory

import com.arkivanov.decompose.ComponentContext
import com.vsevolodganin.clicktrack.ScreenConfiguration
import com.vsevolodganin.clicktrack.ScreenViewModel
import javax.inject.Inject

class ScreenViewModelFactory @Inject constructor(
    private val clickTrackListViewModelFactory: ClickTrackListViewModelFactory,
    private val playClickTrackViewModelFactory: PlayClickTrackViewModelFactory,
    private val editClickTrackViewModelFactory: EditClickTrackViewModelFactory,
    private val metronomeViewModelFactory: MetronomeViewModelFactory,
    private val settingsViewModelFactory: SettingsViewModelFactory,
    private val soundLibraryViewModelFactory: SoundLibraryViewModelFactory,
    private val trainingViewModelFactory: TrainingViewModelFactory,
    private val aboutViewModelFactory: AboutViewModelFactory,
    private val polyrhythmsViewModelFactory: PolyrhythmsViewModelFactory,
) {
    fun create(screenConfiguration: ScreenConfiguration, componentContext: ComponentContext): ScreenViewModel {
        return when (screenConfiguration) {
            ScreenConfiguration.ClickTrackList -> ScreenViewModel.ClickTrackList(
                clickTrackListViewModelFactory.create(componentContext)
            )
            is ScreenConfiguration.PlayClickTrack -> ScreenViewModel.PlayClickTrack(
                playClickTrackViewModelFactory.create(componentContext, screenConfiguration)
            )
            is ScreenConfiguration.EditClickTrack -> ScreenViewModel.EditClickTrack(
                editClickTrackViewModelFactory.create(componentContext, screenConfiguration)
            )
            ScreenConfiguration.Metronome -> ScreenViewModel.Metronome(
                metronomeViewModelFactory.create(componentContext)
            )
            ScreenConfiguration.Training -> ScreenViewModel.Training(
                trainingViewModelFactory.create(componentContext)
            )
            ScreenConfiguration.Settings -> ScreenViewModel.Settings(
                settingsViewModelFactory.create(componentContext)
            )
            ScreenConfiguration.SoundLibrary -> ScreenViewModel.SoundLibrary(
                soundLibraryViewModelFactory.create(componentContext)
            )
            ScreenConfiguration.About -> ScreenViewModel.About(
                aboutViewModelFactory.create(componentContext)
            )
            ScreenConfiguration.Polyrhythms -> ScreenViewModel.Polyrhythms(
                polyrhythmsViewModelFactory.create(componentContext)
            )
        }
    }
}
