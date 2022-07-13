package com.vsevolodganin.clicktrack

import android.content.Intent
import android.media.AudioManager
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.vsevolodganin.clicktrack.di.module.ActivityScopedAppStateEpic
import com.vsevolodganin.clicktrack.di.module.SerialBackgroundDispatcher
import com.vsevolodganin.clicktrack.migration.MigrationManager
import com.vsevolodganin.clicktrack.presenter.PresenterOrchestrator
import com.vsevolodganin.clicktrack.redux.AppState
import com.vsevolodganin.clicktrack.redux.action.BackAction
import com.vsevolodganin.clicktrack.redux.core.Action
import com.vsevolodganin.clicktrack.redux.core.Epic
import com.vsevolodganin.clicktrack.redux.core.EpicMiddleware
import com.vsevolodganin.clicktrack.redux.core.Store
import com.vsevolodganin.clicktrack.ui.ContentView
import com.vsevolodganin.clicktrack.ui.model.AppUiState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels {
        object : AbstractSavedStateViewModelFactory(this@MainActivity, null) {
            override fun <T : ViewModel> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T {
                require(modelClass == MainViewModel::class.java)
                @Suppress("UNCHECKED_CAST") // Checked earlier
                return MainViewModel(application as Application, handle) as T
            }
        }
    }

    @Inject
    lateinit var appStateStore: Store<AppState>

    @Inject
    lateinit var presenterOrchestrator: PresenterOrchestrator

    @Inject
    lateinit var epicMiddleware: EpicMiddleware<AppState>

    @Inject
    @ActivityScopedAppStateEpic
    lateinit var epics: Set<@JvmSuppressWildcards Epic>

    @Inject
    lateinit var intentProcessor: IntentProcessor

    @Inject
    lateinit var migrationManager: MigrationManager

    @Inject
    @SerialBackgroundDispatcher
    lateinit var backgroundDispatcher: CoroutineDispatcher

    private val renderScope = MainScope()

    private var shouldKeepSplash = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen().apply {
            setKeepOnScreenCondition { shouldKeepSplash }
        }

        inject()

        migrationManager.tryMigrate()

        epicMiddleware.register(*epics.toTypedArray())

        renderScope.launch {
            presenterOrchestrator.states()
                .flowOn(backgroundDispatcher)
                .onEach { shouldKeepSplash = false }
                .collect(::render)
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
        volumeControlStream = AudioManager.STREAM_MUSIC
    }

    override fun onBackPressed() {
        appStateStore.dispatch(BackAction)
    }

    private fun inject() {
        viewModel.daggerComponent.activityComponentBuilder()
            .activity(this)
            .build()
            .inject(this)
    }

    private fun render(appUiState: AppUiState) {
        setContent {
            ContentView(
                appUiState = appUiState,
                dispatch = ::dispatch
            )
        }
    }

    private fun dispatch(action: Action) = appStateStore.dispatch(action)
}
