package com.vsevolodganin.clicktrack.state.presenter

import com.vsevolodganin.clicktrack.state.redux.AppState
import com.vsevolodganin.clicktrack.state.redux.Screen
import com.vsevolodganin.clicktrack.state.redux.ScreenBackstack
import com.vsevolodganin.clicktrack.state.redux.core.Store
import com.vsevolodganin.clicktrack.state.redux.frontScreen
import com.vsevolodganin.clicktrack.state.redux.frontScreenPosition
import com.vsevolodganin.clicktrack.ui.model.AppUiState
import com.vsevolodganin.clicktrack.ui.model.UiScreen
import dagger.Reusable
import javax.inject.Inject
import javax.inject.Provider
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Reusable
class PresenterOrchestrator @Inject constructor(
    private val store: Store<AppState>,
    private val drawerPresenter: DrawerPresenter,
    private val clickTrackListPresenter: Provider<ClickTrackListPresenter>,
    private val playClickTrackPresenter: Provider<PlayClickTrackPresenter>,
    private val editClickTrackPresenter: Provider<EditClickTrackPresenter>,
    private val metronomePresenter: Provider<MetronomePresenter>,
    private val settingsPresenter: Provider<SettingsPresenter>,
    private val soundLibraryPresenter: Provider<SoundLibraryPresenter>,
    private val trainingPresenter: Provider<TrainingPresenter>,
) {
    fun states(): Flow<AppUiState> {
        return combine(
            store.state.map { it.backstack }
                .distinctUntilChanged()
                .frontUiScreens(),
            store.state.map { it.backstack.drawerState.let(drawerPresenter::uiState) }
                .distinctUntilChanged(),
        ) { (screen, screenPosition), drawerState ->
            AppUiState(screen, screenPosition, drawerState)
        }
    }

    private fun Flow<ScreenBackstack>.frontUiScreens(): Flow<ScreenToPosition> {
        return channelFlow {
            val screenFlow = MutableSharedFlow<Screen?>()
            var activeScreenType: Class<Screen>? = null
            var uiScreensCollection: Job? = null

            collect { backstack ->
                val screen = backstack.screens.frontScreen()
                val screenPosition = backstack.screens.frontScreenPosition()

                if (activeScreenType != screen?.javaClass) {
                    activeScreenType = screen?.javaClass
                    uiScreensCollection?.cancel()

                    if (screen != null) {
                        uiScreensCollection = launch(context = Dispatchers.Unconfined, start = CoroutineStart.UNDISPATCHED) {
                            val uiScreens = when (screen) {
                                is Screen.ClickTrackList -> clickTrackListPresenter.get().uiScreens()
                                is Screen.PlayClickTrack -> playClickTrackPresenter.get().uiScreens(screenFlow.reinterpret())
                                is Screen.EditClickTrack -> editClickTrackPresenter.get().uiScreens(screenFlow.reinterpret())
                                is Screen.Metronome -> metronomePresenter.get().uiScreens(screenFlow.reinterpret())
                                is Screen.Settings -> settingsPresenter.get().uiScreens()
                                is Screen.SoundLibrary -> soundLibraryPresenter.get().uiScreens()
                                is Screen.Training -> trainingPresenter.get().uiScreens(screenFlow.reinterpret())
                            }

                            uiScreens.collect { uiScreen ->
                                channel.send(ScreenToPosition(
                                    screen = uiScreen,
                                    position = screenPosition,
                                ))
                            }
                        }
                    }
                }

                screenFlow.emit(screen)
            }
        }
    }

    private inline fun <reified T> Flow<*>.reinterpret(): Flow<T> = map { it as T }

    private data class ScreenToPosition(
        val screen: UiScreen,
        val position: Int,
    )
}
