package com.vsevolodganin.clicktrack

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.vsevolodganin.clicktrack.di.module.ViewModelScopedAppStateEpic
import com.vsevolodganin.clicktrack.player.PlayerServiceAccess
import com.vsevolodganin.clicktrack.redux.AppState
import com.vsevolodganin.clicktrack.redux.core.Epic
import com.vsevolodganin.clicktrack.redux.core.EpicMiddleware
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import javax.inject.Inject

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
