package clicktrack

import com.arkivanov.decompose.ComponentContext
import com.vsevolodganin.clicktrack.ScreenConfiguration
import com.vsevolodganin.clicktrack.ScreenViewModelFactory
import com.vsevolodganin.clicktrack.about.AboutViewModelImpl
import com.vsevolodganin.clicktrack.edit.EditClickTrackViewModelImpl
import com.vsevolodganin.clicktrack.list.ClickTrackListViewModelImpl
import com.vsevolodganin.clicktrack.metronome.MetronomeViewModelImpl
import com.vsevolodganin.clicktrack.play.PlayClickTrackViewModelImpl
import com.vsevolodganin.clicktrack.polyrhythm.PolyrhythmsViewModelImpl
import com.vsevolodganin.clicktrack.settings.SettingsViewModelImpl
import com.vsevolodganin.clicktrack.soundlibrary.SoundLibraryViewModelImpl
import com.vsevolodganin.clicktrack.training.TrainingViewModelImpl
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.Test

class ScreenViewModelFactoryTest {
    @Test
    fun `When click track list config requested, factory should invoke corresponding delegate`() {
        test<(ComponentContext) -> ClickTrackListViewModelImpl, _, _>(
            config = ScreenConfiguration.ClickTrackList,
            screenViewModelFactoryProvider = { screenViewModelFactory(clickTrackListViewModelFactory = it) },
            viewModelFactoryInvoke = { componentContext, _ -> invoke(componentContext) },
        )
    }

    @Test
    fun `When play click track config requested, factory should invoke corresponding delegate`() {
        test<(ComponentContext, ScreenConfiguration.PlayClickTrack) -> PlayClickTrackViewModelImpl, _, _>(
            config = ScreenConfiguration.PlayClickTrack(mockk()),
            screenViewModelFactoryProvider = { screenViewModelFactory(playClickTrackViewModelFactory = it) },
            viewModelFactoryInvoke = { componentContext, config -> invoke(componentContext, config) },
        )
    }

    @Test
    fun `When edit click track config requested, factory should invoke corresponding delegate`() {
        test<(ComponentContext, ScreenConfiguration.EditClickTrack) -> EditClickTrackViewModelImpl, _, _>(
            config = ScreenConfiguration.EditClickTrack(mockk(), true),
            screenViewModelFactoryProvider = { screenViewModelFactory(editClickTrackViewModelFactory = it) },
            viewModelFactoryInvoke = { componentContext, config -> invoke(componentContext, config) },
        )
    }

    @Test
    fun `When metronome config requested, factory should invoke corresponding delegate`() {
        test<(ComponentContext) -> MetronomeViewModelImpl, _, _>(
            config = ScreenConfiguration.Metronome,
            screenViewModelFactoryProvider = { screenViewModelFactory(metronomeViewModelFactory = it) },
            viewModelFactoryInvoke = { componentContext, _ -> invoke(componentContext) },
        )
    }

    @Test
    fun `When settings config requested, factory should invoke corresponding delegate`() {
        test<(ComponentContext) -> SettingsViewModelImpl, _, _>(
            config = ScreenConfiguration.Settings,
            screenViewModelFactoryProvider = { screenViewModelFactory(settingsViewModelFactory = it) },
            viewModelFactoryInvoke = { componentContext, _ -> invoke(componentContext) },
        )
    }

    @Test
    fun `When sound library config requested, factory should invoke corresponding delegate`() {
        test<(ComponentContext) -> SoundLibraryViewModelImpl, _, _>(
            config = ScreenConfiguration.SoundLibrary,
            screenViewModelFactoryProvider = { screenViewModelFactory(soundLibraryViewModelFactory = it) },
            viewModelFactoryInvoke = { componentContext, _ -> invoke(componentContext) },
        )
    }

    @Test
    fun `When training config requested, factory should invoke corresponding delegate`() {
        test<(ComponentContext) -> TrainingViewModelImpl, _, _>(
            config = ScreenConfiguration.Training,
            screenViewModelFactoryProvider = { screenViewModelFactory(trainingViewModelFactory = it) },
            viewModelFactoryInvoke = { componentContext, _ -> invoke(componentContext) },
        )
    }

    @Test
    fun `When about config requested, factory should invoke corresponding delegate`() {
        test<(ComponentContext) -> AboutViewModelImpl, _, _>(
            config = ScreenConfiguration.About,
            screenViewModelFactoryProvider = { screenViewModelFactory(aboutViewModelFactory = it) },
            viewModelFactoryInvoke = { componentContext, _ -> invoke(componentContext) },
        )
    }

    @Test
    fun `When polyrhythms config requested, factory should invoke corresponding delegate`() {
        test<(ComponentContext) -> PolyrhythmsViewModelImpl, _, _>(
            config = ScreenConfiguration.Polyrhythms,
            screenViewModelFactoryProvider = { screenViewModelFactory(polyrhythmsViewModelFactory = it) },
            viewModelFactoryInvoke = { componentContext, _ -> invoke(componentContext) },
        )
    }

    private inline fun <reified VMF : Any, reified VM : Any, SC : ScreenConfiguration> test(
        config: SC,
        screenViewModelFactoryProvider: (VMF) -> ScreenViewModelFactory,
        noinline viewModelFactoryInvoke: VMF.(ComponentContext, SC) -> VM,
    ) {
        val componentContext = mockk<ComponentContext>()
        val factoryDelegate = mockk<VMF> {
            every { viewModelFactoryInvoke(componentContext, config) } returns mockk()
        }
        val factory = screenViewModelFactoryProvider(factoryDelegate)

        factory.create(config, componentContext)

        verify(exactly = 1) {
            factoryDelegate.viewModelFactoryInvoke(componentContext, config)
        }
    }

    private fun screenViewModelFactory(
        clickTrackListViewModelFactory: (ComponentContext) -> ClickTrackListViewModelImpl = mockk(),
        playClickTrackViewModelFactory: (ComponentContext, ScreenConfiguration.PlayClickTrack) -> PlayClickTrackViewModelImpl = mockk(),
        editClickTrackViewModelFactory: (ComponentContext, ScreenConfiguration.EditClickTrack) -> EditClickTrackViewModelImpl = mockk(),
        metronomeViewModelFactory: (ComponentContext) -> MetronomeViewModelImpl = mockk(),
        settingsViewModelFactory: (ComponentContext) -> SettingsViewModelImpl = mockk(),
        soundLibraryViewModelFactory: (ComponentContext) -> SoundLibraryViewModelImpl = mockk(),
        trainingViewModelFactory: (ComponentContext) -> TrainingViewModelImpl = mockk(),
        aboutViewModelFactory: (ComponentContext) -> AboutViewModelImpl = mockk(),
        polyrhythmsViewModelFactory: (ComponentContext) -> PolyrhythmsViewModelImpl = mockk(),
    ) = ScreenViewModelFactory(
        clickTrackListViewModelFactory = clickTrackListViewModelFactory,
        playClickTrackViewModelFactory = playClickTrackViewModelFactory,
        editClickTrackViewModelFactory = editClickTrackViewModelFactory,
        metronomeViewModelFactory = metronomeViewModelFactory,
        settingsViewModelFactory = settingsViewModelFactory,
        soundLibraryViewModelFactory = soundLibraryViewModelFactory,
        trainingViewModelFactory = trainingViewModelFactory,
        aboutViewModelFactory = aboutViewModelFactory,
        polyrhythmsViewModelFactory = polyrhythmsViewModelFactory,
    )
}
