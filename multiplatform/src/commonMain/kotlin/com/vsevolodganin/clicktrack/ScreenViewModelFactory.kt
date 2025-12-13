package com.vsevolodganin.clicktrack

import com.arkivanov.decompose.ComponentContext
import com.vsevolodganin.clicktrack.about.AboutViewModelImpl
import com.vsevolodganin.clicktrack.edit.EditClickTrackViewModelImpl
import com.vsevolodganin.clicktrack.list.ClickTrackListViewModelImpl
import com.vsevolodganin.clicktrack.metronome.MetronomeViewModelImpl
import com.vsevolodganin.clicktrack.play.PlayClickTrackViewModelImpl
import com.vsevolodganin.clicktrack.polyrhythm.PolyrhythmsViewModelImpl
import com.vsevolodganin.clicktrack.settings.SettingsViewModelImpl
import com.vsevolodganin.clicktrack.soundlibrary.SoundLibraryViewModelImpl
import com.vsevolodganin.clicktrack.training.TrainingViewModelImpl
import dev.zacsweers.metro.Inject

@Inject
class ScreenViewModelFactory(
    private val clickTrackListViewModelFactory: ClickTrackListViewModelImpl.Factory,
    private val playClickTrackViewModelFactory: PlayClickTrackViewModelImpl.Factory,
    private val editClickTrackViewModelFactory: EditClickTrackViewModelImpl.Factory,
    private val metronomeViewModelFactory: MetronomeViewModelImpl.Factory,
    private val settingsViewModelFactory: SettingsViewModelImpl.Factory,
    private val soundLibraryViewModelFactory: SoundLibraryViewModelImpl.Factory,
    private val trainingViewModelFactory: TrainingViewModelImpl.Factory,
    private val aboutViewModelFactory: AboutViewModelImpl.Factory,
    private val polyrhythmsViewModelFactory: PolyrhythmsViewModelImpl.Factory,
) {
    fun create(screenConfiguration: ScreenConfiguration, componentContext: ComponentContext): ScreenViewModel {
        return when (screenConfiguration) {
            ScreenConfiguration.ClickTrackList -> ScreenViewModel.ClickTrackList(
                clickTrackListViewModelFactory.create(componentContext),
            )

            is ScreenConfiguration.PlayClickTrack -> ScreenViewModel.PlayClickTrack(
                playClickTrackViewModelFactory.create(componentContext, screenConfiguration),
            )

            is ScreenConfiguration.EditClickTrack -> ScreenViewModel.EditClickTrack(
                editClickTrackViewModelFactory.create(componentContext, screenConfiguration),
            )

            ScreenConfiguration.Metronome -> ScreenViewModel.Metronome(
                metronomeViewModelFactory.create(componentContext),
            )

            ScreenConfiguration.Training -> ScreenViewModel.Training(
                trainingViewModelFactory.create(componentContext),
            )

            ScreenConfiguration.Settings -> ScreenViewModel.Settings(
                settingsViewModelFactory.create(componentContext),
            )

            ScreenConfiguration.SoundLibrary -> ScreenViewModel.SoundLibrary(
                soundLibraryViewModelFactory.create(componentContext),
            )

            ScreenConfiguration.About -> ScreenViewModel.About(
                aboutViewModelFactory.create(componentContext),
            )

            ScreenConfiguration.Polyrhythms -> ScreenViewModel.Polyrhythms(
                polyrhythmsViewModelFactory.create(componentContext),
            )
        }
    }
}
