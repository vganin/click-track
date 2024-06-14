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
import me.tatarka.inject.annotations.Inject

@Inject
class ScreenViewModelFactory(
    private val clickTrackListViewModelFactory: (componentContext: ComponentContext) -> ClickTrackListViewModelImpl,
    private val playClickTrackViewModelFactory: (
        componentContext: ComponentContext,
        config: ScreenConfiguration.PlayClickTrack,
    ) -> PlayClickTrackViewModelImpl,
    private val editClickTrackViewModelFactory: (
        componentContext: ComponentContext,
        config: ScreenConfiguration.EditClickTrack,
    ) -> EditClickTrackViewModelImpl,
    private val metronomeViewModelFactory: (componentContext: ComponentContext) -> MetronomeViewModelImpl,
    private val settingsViewModelFactory: (componentContext: ComponentContext) -> SettingsViewModelImpl,
    private val soundLibraryViewModelFactory: (componentContext: ComponentContext) -> SoundLibraryViewModelImpl,
    private val trainingViewModelFactory: (componentContext: ComponentContext) -> TrainingViewModelImpl,
    private val aboutViewModelFactory: (componentContext: ComponentContext) -> AboutViewModelImpl,
    private val polyrhythmsViewModelFactory: (componentContext: ComponentContext) -> PolyrhythmsViewModelImpl,
) {
    fun create(screenConfiguration: ScreenConfiguration, componentContext: ComponentContext): ScreenViewModel {
        return when (screenConfiguration) {
            ScreenConfiguration.ClickTrackList -> ScreenViewModel.ClickTrackList(
                clickTrackListViewModelFactory.invoke(componentContext),
            )

            is ScreenConfiguration.PlayClickTrack -> ScreenViewModel.PlayClickTrack(
                playClickTrackViewModelFactory.invoke(componentContext, screenConfiguration),
            )

            is ScreenConfiguration.EditClickTrack -> ScreenViewModel.EditClickTrack(
                editClickTrackViewModelFactory.invoke(componentContext, screenConfiguration),
            )

            ScreenConfiguration.Metronome -> ScreenViewModel.Metronome(
                metronomeViewModelFactory.invoke(componentContext),
            )

            ScreenConfiguration.Training -> ScreenViewModel.Training(
                trainingViewModelFactory.invoke(componentContext),
            )

            ScreenConfiguration.Settings -> ScreenViewModel.Settings(
                settingsViewModelFactory.invoke(componentContext),
            )

            ScreenConfiguration.SoundLibrary -> ScreenViewModel.SoundLibrary(
                soundLibraryViewModelFactory.invoke(componentContext),
            )

            ScreenConfiguration.About -> ScreenViewModel.About(
                aboutViewModelFactory.invoke(componentContext),
            )

            ScreenConfiguration.Polyrhythms -> ScreenViewModel.Polyrhythms(
                polyrhythmsViewModelFactory.invoke(componentContext),
            )
        }
    }
}
