package com.vsevolodganin.clicktrack

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.vsevolodganin.clicktrack.about.AboutState
import com.vsevolodganin.clicktrack.about.AboutViewModel
import com.vsevolodganin.clicktrack.drawer.DrawerNavigation
import com.vsevolodganin.clicktrack.drawer.DrawerState
import com.vsevolodganin.clicktrack.drawer.DrawerViewModel
import com.vsevolodganin.clicktrack.edit.EditClickTrackState
import com.vsevolodganin.clicktrack.edit.EditClickTrackViewModel
import com.vsevolodganin.clicktrack.edit.toEditState
import com.vsevolodganin.clicktrack.language.AppLanguage
import com.vsevolodganin.clicktrack.list.ClickTrackListState
import com.vsevolodganin.clicktrack.list.ClickTrackListViewModel
import com.vsevolodganin.clicktrack.metronome.MetronomeState
import com.vsevolodganin.clicktrack.metronome.MetronomeViewModel
import com.vsevolodganin.clicktrack.model.BeatsPerMinuteDiff
import com.vsevolodganin.clicktrack.model.BuiltinClickSounds
import com.vsevolodganin.clicktrack.model.ClickSoundType
import com.vsevolodganin.clicktrack.model.ClickSoundsId
import com.vsevolodganin.clicktrack.model.ClickTrackId
import com.vsevolodganin.clicktrack.model.CueDuration
import com.vsevolodganin.clicktrack.model.DefaultBeatsDuration
import com.vsevolodganin.clicktrack.model.DefaultMeasuresDuration
import com.vsevolodganin.clicktrack.model.DefaultTimeDuration
import com.vsevolodganin.clicktrack.model.NotePattern
import com.vsevolodganin.clicktrack.model.PlayProgress
import com.vsevolodganin.clicktrack.model.TimeSignature
import com.vsevolodganin.clicktrack.model.TwoLayerPolyrhythm
import com.vsevolodganin.clicktrack.model.bpm
import com.vsevolodganin.clicktrack.play.PlayClickTrackState
import com.vsevolodganin.clicktrack.play.PlayClickTrackViewModel
import com.vsevolodganin.clicktrack.polyrhythm.PolyrhythmsState
import com.vsevolodganin.clicktrack.polyrhythm.PolyrhythmsViewModel
import com.vsevolodganin.clicktrack.settings.SettingsState
import com.vsevolodganin.clicktrack.settings.SettingsViewModel
import com.vsevolodganin.clicktrack.soundlibrary.SelectableClickSoundsItem
import com.vsevolodganin.clicktrack.soundlibrary.SoundLibraryState
import com.vsevolodganin.clicktrack.soundlibrary.SoundLibraryViewModel
import com.vsevolodganin.clicktrack.theme.Theme
import com.vsevolodganin.clicktrack.training.TrainingEditState
import com.vsevolodganin.clicktrack.training.TrainingEndingKind
import com.vsevolodganin.clicktrack.training.TrainingViewModel
import com.vsevolodganin.clicktrack.ui.ComposableProvider
import com.vsevolodganin.clicktrack.ui.RootView
import com.vsevolodganin.clicktrack.ui.preview.PREVIEW_CLICK_TRACK_1
import com.vsevolodganin.clicktrack.ui.preview.PREVIEW_CLICK_TRACK_2
import com.vsevolodganin.clicktrack.utils.decompose.resetTo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes

// TODO: 🚧 Under heavy construction 🚧
fun MainViewController() = ComposeUIViewController {
    val drawerState = remember {
        MutableStateFlow(
            DrawerState(
                isOpened = false,
                selectedItem = null,
            )
        )
    }

    val drawerNavigation = remember {
        object : DrawerNavigation {
            override fun openDrawer() = drawerState.update { it.copy(isOpened = true) }
            override fun closeDrawer() = drawerState.update { it.copy(isOpened = false) }
        }
    }

    val screenStackNavigation = remember {
        StackNavigation<ScreenConfiguration>()
    }

    val navigation = remember {
        Navigation(screenStackNavigation, drawerNavigation)
    }

    val screens = remember {
        DefaultComponentContext(LifecycleRegistry()).childStack(
            source = screenStackNavigation,
            initialStack = {
                listOf(ScreenConfiguration.ClickTrackList)
            },
            childFactory = { configuration, _ ->
                when (configuration) {
                    ScreenConfiguration.About -> object : AboutViewModel {
                        override val state: StateFlow<AboutState> = MutableStateFlow(
                            AboutState(
                                displayVersion = "6.6.6"
                            )
                        )

                        override fun onBackClick() = navigation.pop()
                        override fun onHomeClick() = Unit
                        override fun onTwitterClick() = Unit
                        override fun onEmailClick() = Unit
                        override fun onArtstationClick() = Unit
                        override fun onProjectLinkClick() = Unit
                    }.let(ScreenViewModel::About)

                    ScreenConfiguration.ClickTrackList -> object : ClickTrackListViewModel {
                        override val state: StateFlow<ClickTrackListState> = MutableStateFlow(
                            ClickTrackListState(
                                listOf(
                                    PREVIEW_CLICK_TRACK_1,
                                    PREVIEW_CLICK_TRACK_2,
                                )
                            )
                        )

                        override fun onAddClick() = Unit
                        override fun onItemClick(id: ClickTrackId.Database) = Unit
                        override fun onItemRemove(id: ClickTrackId.Database) = Unit
                        override fun onMenuClick() = navigation.openDrawer()
                    }.let(ScreenViewModel::ClickTrackList)

                    is ScreenConfiguration.EditClickTrack -> object : EditClickTrackViewModel {
                        override val state: StateFlow<EditClickTrackState?> = MutableStateFlow(
                            PREVIEW_CLICK_TRACK_1.toEditState(showForwardButton = true)
                        )

                        override fun onBackClick() = navigation.pop()
                        override fun onForwardClick() = Unit
                        override fun onNameChange(name: String) = Unit
                        override fun onLoopChange(loop: Boolean) = Unit
                        override fun onTempoDiffIncrementClick() = Unit
                        override fun onTempoDiffDecrementClick() = Unit
                        override fun onAddNewCueClick() = Unit
                        override fun onCueRemove(index: Int) = Unit
                        override fun onCueNameChange(index: Int, name: String) = Unit
                        override fun onCueBpmChange(index: Int, bpm: Int) = Unit
                        override fun onCueTimeSignatureChange(index: Int, timeSignature: TimeSignature) = Unit
                        override fun onCueDurationChange(index: Int, duration: CueDuration) = Unit
                        override fun onCueDurationTypeChange(index: Int, durationType: CueDuration.Type) = Unit
                        override fun onCuePatternChange(index: Int, pattern: NotePattern) = Unit
                    }.let(ScreenViewModel::EditClickTrack)

                    ScreenConfiguration.Metronome -> object : MetronomeViewModel {
                        override val state: StateFlow<MetronomeState?> = MutableStateFlow(
                            MetronomeState(
                                bpm = 90.bpm,
                                pattern = NotePattern.QUINTUPLET_X2,
                                progress = PlayProgress(100.milliseconds),
                                isPlaying = false,
                                areOptionsExpanded = false,
                            )
                        )

                        override fun onBackClick() = navigation.pop()
                        override fun onToggleOptions() = Unit
                        override fun onOptionsExpandedChange(isOpened: Boolean) = Unit
                        override fun onPatternChoose(pattern: NotePattern) = Unit
                        override fun onBpmChange(bpmDiff: BeatsPerMinuteDiff) = Unit
                        override fun onTogglePlay() = Unit
                        override fun onBpmMeterClick() = Unit
                    }.let(ScreenViewModel::Metronome)

                    is ScreenConfiguration.PlayClickTrack -> object : PlayClickTrackViewModel {
                        override val state: StateFlow<PlayClickTrackState?> = MutableStateFlow(
                            PlayClickTrackState(
                                clickTrack = PREVIEW_CLICK_TRACK_1,
                                playProgress = null,
                                playTrackingMode = true,
                            )
                        )

                        override fun onBackClick() = navigation.pop()
                        override fun onTogglePlayStop() = Unit
                        override fun onTogglePlayPause() = Unit
                        override fun onTogglePlayTrackingMode() = Unit
                        override fun onProgressDragStart() = Unit
                        override fun onProgressDrop(progress: Double) = Unit
                        override fun onEditClick() = Unit
                        override fun onRemoveClick() = Unit
                        override fun onExportClick() = Unit
                        override fun onCancelExportClick() = Unit
                    }.let(ScreenViewModel::PlayClickTrack)

                    ScreenConfiguration.Polyrhythms -> object : PolyrhythmsViewModel {
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
                    }.let(ScreenViewModel::Polyrhythms)

                    ScreenConfiguration.Settings -> object : SettingsViewModel {
                        override val state: StateFlow<SettingsState> = MutableStateFlow(
                            SettingsState(
                                theme = Theme.SYSTEM,
                                ignoreAudioFocus = false,
                                language = AppLanguage.SYSTEM,
                            )
                        )

                        override fun onBackClick() = navigation.pop()
                        override fun onThemeChange(theme: Theme) = Unit
                        override fun onLanguageChange(language: AppLanguage) = Unit
                        override fun onIgnoreAudioFocusChange(ignoreAudioFocus: Boolean) = Unit
                    }.let(ScreenViewModel::Settings)

                    ScreenConfiguration.SoundLibrary -> object : SoundLibraryViewModel {
                        override val state: StateFlow<SoundLibraryState?> = MutableStateFlow(
                            SoundLibraryState(
                                items = listOf(
                                    SelectableClickSoundsItem.Builtin(
                                        data = BuiltinClickSounds.BEEP,
                                        selected = true
                                    ),
                                    SelectableClickSoundsItem.UserDefined(
                                        id = ClickSoundsId.Database(0L),
                                        strongBeatValue = "/audio/audio/audio/audio/strong.mp3",
                                        weakBeatValue = "/audio/audio/audio/audio/weak.mp3",
                                        hasError = false,
                                        isPlaying = true,
                                        selected = false
                                    ),
                                    SelectableClickSoundsItem.UserDefined(
                                        id = ClickSoundsId.Database(1L),
                                        strongBeatValue = "/audio/audio/audio/audio/strong.mp3",
                                        weakBeatValue = "no_access.mp3",
                                        hasError = true,
                                        isPlaying = false,
                                        selected = false
                                    )
                                ),
                            )
                        )

                        override fun onBackClick() = navigation.pop()
                        override fun onAddNewClick() = Unit
                        override fun onItemClick(id: ClickSoundsId) = Unit
                        override fun onItemRemove(id: ClickSoundsId.Database) = Unit
                        override fun onItemSoundSelect(id: ClickSoundsId.Database, type: ClickSoundType) = Unit
                        override fun onItemSoundTestToggle(id: ClickSoundsId.Database) = Unit
                    }.let(ScreenViewModel::SoundLibrary)

                    ScreenConfiguration.Training -> object : TrainingViewModel {
                        override val state: StateFlow<TrainingEditState> = MutableStateFlow(
                            TrainingEditState(
                                startingTempo = 120,
                                mode = TrainingEditState.TrainingMode.INCREASE_TEMPO,
                                activeSegmentLengthType = CueDuration.Type.MEASURES,
                                segmentLengthBeats = DefaultBeatsDuration,
                                segmentLengthMeasures = DefaultMeasuresDuration,
                                segmentLengthTime = DefaultTimeDuration,
                                tempoChange = 5,
                                activeEndingKind = TrainingEndingKind.BY_TEMPO,
                                endingByTempo = TrainingEditState.Ending.ByTempo(160),
                                endingByTime = TrainingEditState.Ending.ByTime(5.minutes),
                                errors = emptySet(),
                            )
                        )

                        override fun onBackClick() = navigation.pop()
                        override fun onAcceptClick() = Unit
                        override fun onStartingTempoChange(startingTempo: Int) = Unit
                        override fun onModeSelect(mode: TrainingEditState.TrainingMode) = Unit
                        override fun onSegmentLengthChange(segmentLength: CueDuration) = Unit
                        override fun onSegmentLengthTypeChange(segmentLengthType: CueDuration.Type) = Unit
                        override fun onTempoChangeChange(tempoChange: Int) = Unit
                        override fun onEndingChange(ending: TrainingEditState.Ending) = Unit
                        override fun onEndingKindChange(endingKind: TrainingEndingKind) = Unit
                    }.let(ScreenViewModel::Training)
                }
            }
        )
    }

    RootView(
        viewModel = object : RootViewModel {
            override val drawer: DrawerViewModel = object : DrawerViewModel, DrawerNavigation by drawerNavigation {
                override val state: StateFlow<DrawerState> = drawerState

                override fun navigateToMetronome() = resetTo(ScreenConfiguration.Metronome)
                override fun navigateToTraining() = resetTo(ScreenConfiguration.Training)
                override fun navigateToPolyrhythms() = resetTo(ScreenConfiguration.Polyrhythms)
                override fun navigateToSoundLibrary() = resetTo(ScreenConfiguration.SoundLibrary)
                override fun navigateToSettings() = resetTo(ScreenConfiguration.Settings)
                override fun navigateToAbout() = resetTo(ScreenConfiguration.About)

                private fun resetTo(config: ScreenConfiguration) = navigation.resetTo(config)
            }
            override val screens: ScreenStackState = screens
        },
        composableProvider = object : ComposableProvider {
            override val playClickTrack: @Composable (PlayClickTrackViewModel, Modifier) -> Unit
                get() = @Composable { _, _ -> }
            override val editClickTrack: @Composable (EditClickTrackViewModel, Modifier) -> Unit
                get() = @Composable { _, _ -> }
            override val soundLibrary: @Composable (SoundLibraryViewModel, Modifier) -> Unit
                get() = @Composable { _, _ -> }
            override val training: @Composable (TrainingViewModel, Modifier) -> Unit
                get() = @Composable { _, _ -> }
        })
}
