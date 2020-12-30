package com.vsevolodganin.clicktrack

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.setContent
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.vsevolodganin.clicktrack.di.module.ActivityScopedAppStateEpic
import com.vsevolodganin.clicktrack.redux.Action
import com.vsevolodganin.clicktrack.redux.Epic
import com.vsevolodganin.clicktrack.redux.EpicMiddleware
import com.vsevolodganin.clicktrack.redux.Store
import com.vsevolodganin.clicktrack.state.AppState
import com.vsevolodganin.clicktrack.state.actions.NavigateBack
import com.vsevolodganin.clicktrack.state.frontScreen
import com.vsevolodganin.clicktrack.state.frontScreenPosition
import com.vsevolodganin.clicktrack.view.ContentView
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

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

    private val renderScope = MainScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inject()

        epicMiddleware.register(*epics.toTypedArray())

        renderScope.launch {
            // FIXME: Don't use method reference here because of compiler crash for now
            appStateStore.state.collect {
                render(it)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        epicMiddleware.unregister(*epics.toTypedArray())
        renderScope.cancel()
    }

    override fun onBackPressed() {
        appStateStore.dispatch(NavigateBack)
    }

    private fun inject() {
        viewModel.daggerComponent.activityComponentBuilder()
            .activity(this)
            .build()
            .inject(this)
    }

    private fun render(appState: AppState) {
        val frontScreen = appState.backstack.frontScreen() ?: return
        val frontScreenPosition = appState.backstack.frontScreenPosition()
        setContent {
            ContentView(frontScreen, frontScreenPosition, ::dispatch)
        }
    }

    private fun dispatch(action: Action) = appStateStore.dispatch(action)
}
