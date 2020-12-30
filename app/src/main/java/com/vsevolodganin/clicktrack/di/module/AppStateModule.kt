package com.vsevolodganin.clicktrack.di.module

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import com.vsevolodganin.clicktrack.di.component.ActivityScoped
import com.vsevolodganin.clicktrack.di.component.ViewModelScoped
import com.vsevolodganin.clicktrack.redux.Epic
import com.vsevolodganin.clicktrack.redux.EpicMiddleware
import com.vsevolodganin.clicktrack.redux.Store
import com.vsevolodganin.clicktrack.state.AppState
import com.vsevolodganin.clicktrack.state.epic.FinishAppEpic
import com.vsevolodganin.clicktrack.state.epic.LoadDataEpic
import com.vsevolodganin.clicktrack.state.epic.MetronomeEpic
import com.vsevolodganin.clicktrack.state.epic.PlayerEpic
import com.vsevolodganin.clicktrack.state.epic.RemoveClickTrackEpic
import com.vsevolodganin.clicktrack.state.epic.SaveClickTrackEpic
import com.vsevolodganin.clicktrack.state.reducer.reduce
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import javax.inject.Qualifier

@Module
class AppStateModule {

    @Provides
    @ViewModelScoped
    fun provideEpicMiddleware(
        @ComputationDispatcher computationDispatcher: CoroutineDispatcher
    ) = EpicMiddleware<AppState>(computationDispatcher)

    @Provides
    @ViewModelScoped
    fun provideStore(
        savedStateHandle: SavedStateHandle,
        coroutineScope: CoroutineScope,
        @ComputationDispatcher coroutineDispatcher: CoroutineDispatcher,
        epicMiddleware: EpicMiddleware<AppState>,
    ): Store<AppState> {
        val initialState = savedStateHandle.get<Bundle?>(SavedStateConst.APP_STATE_BUNDLE_KEY)
            ?.getParcelable(SavedStateConst.APP_STATE_KEY)
            ?: AppState.INITIAL

        val store = Store(
            initialState,
            AppState::reduce,
            CoroutineScope(coroutineScope.coroutineContext + coroutineDispatcher),
            epicMiddleware
        )

        savedStateHandle.setSavedStateProvider(SavedStateConst.APP_STATE_BUNDLE_KEY) {
            Bundle().apply {
                putParcelable(SavedStateConst.APP_STATE_KEY, store.state.value)
            }
        }

        return store
    }

    private object SavedStateConst {
        const val APP_STATE_BUNDLE_KEY = "app_state_bundle"
        const val APP_STATE_KEY = "app_state"
    }
}

@Qualifier
annotation class ViewModelScopedAppStateEpic

@Module
abstract class ViewModelScopedAppStateEpicModule {

    @ViewModelScoped
    @Binds
    @IntoSet
    @ViewModelScopedAppStateEpic
    abstract fun bindPlayerEpic(epic: PlayerEpic): Epic

    @ViewModelScoped
    @Binds
    @IntoSet
    @ViewModelScopedAppStateEpic
    abstract fun bindLoadDataEpic(epic: LoadDataEpic): Epic

    @ViewModelScoped
    @Binds
    @IntoSet
    @ViewModelScopedAppStateEpic
    abstract fun bindRemoveClickTrackEpic(epic: RemoveClickTrackEpic): Epic

    @ViewModelScoped
    @Binds
    @IntoSet
    @ViewModelScopedAppStateEpic
    abstract fun bindSaveClickTrackEpic(epic: SaveClickTrackEpic): Epic

    @ViewModelScoped
    @Binds
    @IntoSet
    @ViewModelScopedAppStateEpic
    abstract fun bindMetronomeEpic(epic: MetronomeEpic): Epic
}

@Qualifier
annotation class ActivityScopedAppStateEpic

@Module
abstract class ActivityScopedAppStateEpicModule {

    @ActivityScoped
    @Binds
    @IntoSet
    @ActivityScopedAppStateEpic
    abstract fun bindFinishAppEpic(epic: FinishAppEpic): Epic
}
