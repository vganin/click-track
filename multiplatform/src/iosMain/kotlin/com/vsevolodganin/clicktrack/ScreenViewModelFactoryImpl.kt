package com.vsevolodganin.clicktrack

import com.arkivanov.decompose.ComponentContext
import me.tatarka.inject.annotations.Inject

@Inject
class ScreenViewModelFactoryImpl(
    private val clickTrackListViewModelFactory: (componentContext: ComponentContext) -> DummyClickTrackListViewModelImpl,
    private val playClickTrackViewModelFactory: (componentContext: ComponentContext, config: ScreenConfiguration.PlayClickTrack) -> DummyPlayClickTrackViewModelImpl,
    private val editClickTrackViewModelFactory: (componentContext: ComponentContext, config: ScreenConfiguration.EditClickTrack) -> DummyEditClickTrackViewModelImpl,
    private val metronomeViewModelFactory: (componentContext: ComponentContext) -> DummyMetronomeViewModelImpl,
    private val settingsViewModelFactory: (componentContext: ComponentContext) -> DummySettingsViewModelImpl,
    private val soundLibraryViewModelFactory: (componentContext: ComponentContext) -> DummySoundLibraryViewModelImpl,
    private val trainingViewModelFactory: (componentContext: ComponentContext) -> DummyTrainingViewModelImpl,
    private val aboutViewModelFactory: (componentContext: ComponentContext) -> DummyAboutViewModelImpl,
    private val polyrhythmsViewModelFactory: (componentContext: ComponentContext) -> DummyPolyrhythmsViewModelImpl,
) : ScreenViewModelFactory {
    override fun create(screenConfiguration: ScreenConfiguration, componentContext: ComponentContext): ScreenViewModel {
        return when (screenConfiguration) {
            ScreenConfiguration.ClickTrackList -> ScreenViewModel.ClickTrackList(
                clickTrackListViewModelFactory.invoke(componentContext)
            )

            is ScreenConfiguration.PlayClickTrack -> ScreenViewModel.PlayClickTrack(
                playClickTrackViewModelFactory.invoke(componentContext, screenConfiguration)
            )

            is ScreenConfiguration.EditClickTrack -> ScreenViewModel.EditClickTrack(
                editClickTrackViewModelFactory.invoke(componentContext, screenConfiguration)
            )

            ScreenConfiguration.Metronome -> ScreenViewModel.Metronome(
                metronomeViewModelFactory.invoke(componentContext)
            )

            ScreenConfiguration.Training -> ScreenViewModel.Training(
                trainingViewModelFactory.invoke(componentContext)
            )

            ScreenConfiguration.Settings -> ScreenViewModel.Settings(
                settingsViewModelFactory.invoke(componentContext)
            )

            ScreenConfiguration.SoundLibrary -> ScreenViewModel.SoundLibrary(
                soundLibraryViewModelFactory.invoke(componentContext)
            )

            ScreenConfiguration.About -> ScreenViewModel.About(
                aboutViewModelFactory.invoke(componentContext)
            )

            ScreenConfiguration.Polyrhythms -> ScreenViewModel.Polyrhythms(
                polyrhythmsViewModelFactory.invoke(componentContext)
            )
        }
    }
}
