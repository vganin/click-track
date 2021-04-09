package com.vsevolodganin.clicktrack

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.vsevolodganin.clicktrack.di.module.ActivityScopedAppStateEpic
import com.vsevolodganin.clicktrack.migration.MigrationManager
import com.vsevolodganin.clicktrack.player.PlayerSoundPool
import com.vsevolodganin.clicktrack.redux.Action
import com.vsevolodganin.clicktrack.redux.Epic
import com.vsevolodganin.clicktrack.redux.EpicMiddleware
import com.vsevolodganin.clicktrack.redux.Store
import com.vsevolodganin.clicktrack.state.AppState
import com.vsevolodganin.clicktrack.state.actions.NavigationAction
import com.vsevolodganin.clicktrack.state.frontScreen
import com.vsevolodganin.clicktrack.state.frontScreenPosition
import com.vsevolodganin.clicktrack.view.ContentView
import javax.inject.Inject
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels {
        object : AbstractSavedStateViewModelFactory(this@MainActivity, null) {
            override fun <T : ViewModel?> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T {
                require(modelClass == MainViewModel::class.java)
                @Suppress("UNCHECKED_CAST") // Checked earlier
                return MainViewModel(application as Application, handle) as T
            }
        }
    }

    @Inject
    lateinit var appStateStore: Store<AppState>

    @Inject
    lateinit var epicMiddleware: EpicMiddleware<AppState>

    @Inject
    @ActivityScopedAppStateEpic
    lateinit var epics: Set<@JvmSuppressWildcards Epic>

    @Inject
    lateinit var intentProcessor: IntentProcessor

    @Inject
    lateinit var migrationManager: MigrationManager

    private val renderScope = MainScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inject()

        migrationManager.tryMigrate()

        epicMiddleware.register(*epics.toTypedArray())

        renderScope.launch {
            appStateStore.state.collect(::render)
        }

        intentProcessor.process(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intentProcessor.process(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        epicMiddleware.unregister(*epics.toTypedArray())
        renderScope.cancel()
    }

    override fun onResume() {
        super.onResume()
        volumeControlStream = PlayerSoundPool.AUDIO_ATTRIBUTES.volumeControlStream
    }

    override fun onBackPressed() {
        appStateStore.dispatch(NavigationAction.Back)
    }

    private fun inject() {
        viewModel.daggerComponent.activityComponentBuilder()
            .activity(this)
            .build()
            .inject(this)
    }

    private fun render(appState: AppState) {
        val frontScreen = appState.backstack.screens.frontScreen() ?: return
        val frontScreenPosition = appState.backstack.screens.frontScreenPosition()
        val drawerState = appState.backstack.drawerState

        setContent {
            ContentView(
                screen = frontScreen,
                positionInBackstack = frontScreenPosition,
                drawerScreenState = drawerState,
                dispatch = ::dispatch
            )
        }
    }

    private fun dispatch(action: Action) = appStateStore.dispatch(action)
}
