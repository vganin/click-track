package com.vsevolodganin.clicktrack

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.vsevolodganin.clicktrack.di.module.ViewModelScopedAppStateEpic
import com.vsevolodganin.clicktrack.player.PlayerServiceAccess
import com.vsevolodganin.clicktrack.state.redux.AppState
import com.vsevolodganin.clicktrack.state.redux.core.Epic
import com.vsevolodganin.clicktrack.state.redux.core.EpicMiddleware
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel

class MainViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val daggerComponent = application.daggerComponent.viewModelComponentBuilder()
        .savedStateHandle(savedStateHandle)
        .build()

    @Inject
    lateinit var coroutineScope: CoroutineScope

    @Inject
    lateinit var epicMiddleware: EpicMiddleware<AppState>

    @Inject
    @ViewModelScopedAppStateEpic
    lateinit var epics: Set<@JvmSuppressWildcards Epic>

    @Inject
    lateinit var playerServiceAccess: PlayerServiceAccess

    init {
        daggerComponent.inject(this)
        epicMiddleware.register(*epics.toTypedArray())
        playerServiceAccess.connect()
    }

    override fun onCleared() {
        playerServiceAccess.disconnect()
        epicMiddleware.unregister(*epics.toTypedArray())
        coroutineScope.cancel()
    }
}
