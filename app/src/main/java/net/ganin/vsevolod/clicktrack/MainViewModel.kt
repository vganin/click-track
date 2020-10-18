package net.ganin.vsevolod.clicktrack

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import net.ganin.vsevolod.clicktrack.di.module.ComputationDispatcher
import net.ganin.vsevolod.clicktrack.di.module.MainDispatcher
import net.ganin.vsevolod.clicktrack.di.module.ViewModelScopedAppStateEpic
import net.ganin.vsevolod.clicktrack.redux.Epic
import net.ganin.vsevolod.clicktrack.redux.EpicMiddleware
import net.ganin.vsevolod.clicktrack.state.AppState
import javax.inject.Inject

class MainViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val daggerComponent = application.daggerComponent.viewModelComponentBuilder()
        .savedStateHandle(savedStateHandle)
        .build()

    @Inject
    @MainDispatcher
    lateinit var mainScope: CoroutineScope

    @Inject
    @ComputationDispatcher
    lateinit var computationScope: CoroutineScope

    @Inject
    lateinit var epicMiddleware: EpicMiddleware<AppState>

    @Inject
    @ViewModelScopedAppStateEpic
    lateinit var epics: Set<@JvmSuppressWildcards Epic>

    init {
        daggerComponent.inject(this)
        epicMiddleware.register(*epics.toTypedArray())
    }

    override fun onCleared() {
        epicMiddleware.unregister(*epics.toTypedArray())
        mainScope.cancel()
        computationScope.cancel()
    }
}
