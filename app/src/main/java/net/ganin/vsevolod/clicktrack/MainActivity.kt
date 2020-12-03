package net.ganin.vsevolod.clicktrack

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.setContent
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import net.ganin.vsevolod.clicktrack.di.module.ActivityScopedAppStateEpic
import net.ganin.vsevolod.clicktrack.redux.Action
import net.ganin.vsevolod.clicktrack.redux.Epic
import net.ganin.vsevolod.clicktrack.redux.EpicMiddleware
import net.ganin.vsevolod.clicktrack.redux.Store
import net.ganin.vsevolod.clicktrack.state.AppState
import net.ganin.vsevolod.clicktrack.state.actions.NavigateBack
import net.ganin.vsevolod.clicktrack.state.frontScreen
import net.ganin.vsevolod.clicktrack.view.ContentView
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
        setContent {
            ContentView(frontScreen, ::dispatch)
        }
    }

    private fun dispatch(action: Action) = appStateStore.dispatch(action)
}
