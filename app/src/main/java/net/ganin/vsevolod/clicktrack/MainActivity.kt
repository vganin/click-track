package net.ganin.vsevolod.clicktrack

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.setContent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import net.ganin.vsevolod.clicktrack.audio.ClickTrackPlayer
import net.ganin.vsevolod.clicktrack.redux.EpicMiddleware
import net.ganin.vsevolod.clicktrack.redux.Store
import net.ganin.vsevolod.clicktrack.state.AppState
import net.ganin.vsevolod.clicktrack.state.Screen
import net.ganin.vsevolod.clicktrack.state.actions.LoadDataAction
import net.ganin.vsevolod.clicktrack.state.actions.NavigateBack
import net.ganin.vsevolod.clicktrack.state.epic.ClickTrackPlayerEpic
import net.ganin.vsevolod.clicktrack.state.epic.FinishAppEpic
import net.ganin.vsevolod.clicktrack.state.epic.LoadDataEpic
import net.ganin.vsevolod.clicktrack.state.frontScreen
import net.ganin.vsevolod.clicktrack.state.reducer.reduce
import net.ganin.vsevolod.clicktrack.storage.ClickTrackRepository
import net.ganin.vsevolod.clicktrack.view.ContentView
import net.ganin.vsevolod.clicktrack.view.screen.ClickTrackListScreenView
import net.ganin.vsevolod.clicktrack.view.screen.ClickTrackScreenView
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private val mainScope = MainScope()
    private val backgroundScope = CoroutineScope(Dispatchers.Default)

    private lateinit var store: Store<AppState>

    private val clickTrackPlayerDispatcher = Executors.newSingleThreadExecutor { runnable ->
        Thread(runnable, "click_track").apply {
            priority = Thread.MAX_PRIORITY
        }
    }.asCoroutineDispatcher()

    private val clickTrackRepository = ClickTrackRepository(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val initialState = savedInstanceState?.getParcelable(SavedStateConst.APP_STATE_KEY)
            ?: AppState.INITIAL

        val epicMiddleware = EpicMiddleware<AppState>(Dispatchers.Default)

        store = Store(
            initialState,
            AppState::reduce,
            backgroundScope,
            epicMiddleware
        )

        epicMiddleware.register(
            FinishAppEpic(this, store, Dispatchers.Main),
            LoadDataEpic(clickTrackRepository),
            ClickTrackPlayerEpic(
                store,
                ClickTrackPlayer(
                    context = this@MainActivity,
                    mainCoroutineScope = mainScope,
                    playerCoroutineContext = clickTrackPlayerDispatcher,
                    dispatch = store::dispatch,
                )
            ),
        )

        mainScope.launch {
            // FIXME: Don't use method reference here because of compiler crash for now
            store.state.collect { render(it) }
        }

        store.dispatch(LoadDataAction)
    }

    override fun onDestroy() {
        super.onDestroy()
        backgroundScope.cancel()
        mainScope.cancel()
        clickTrackPlayerDispatcher.close()
    }

    override fun onBackPressed() {
        store.dispatch(NavigateBack)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(SavedStateConst.APP_STATE_KEY, store.state.value)
    }

    private fun render(appState: AppState) {
        val frontScreen = appState.backstack.frontScreen() ?: return

        setContent {
            ContentView {
                when (frontScreen) {
                    is Screen.ClickTrackList -> ClickTrackListScreenView(frontScreen.state, store::dispatch)
                    is Screen.ClickTrack -> ClickTrackScreenView(frontScreen.state, store::dispatch)
                }
            }
        }
    }

    private object SavedStateConst {
        const val APP_STATE_KEY = "app_state"
    }
}
