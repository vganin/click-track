package net.ganin.vsevolod.clicktrack.di.module

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import net.ganin.vsevolod.clicktrack.di.component.ActivityScoped
import net.ganin.vsevolod.clicktrack.di.component.ViewModelScoped
import net.ganin.vsevolod.clicktrack.redux.Epic
import net.ganin.vsevolod.clicktrack.redux.EpicMiddleware
import net.ganin.vsevolod.clicktrack.redux.Store
import net.ganin.vsevolod.clicktrack.state.AppState
import net.ganin.vsevolod.clicktrack.state.epic.ClickTrackPlayerEpic
import net.ganin.vsevolod.clicktrack.state.epic.FinishAppEpic
import net.ganin.vsevolod.clicktrack.state.epic.LoadDataEpic
import net.ganin.vsevolod.clicktrack.state.epic.RemoveClickTrackEpic
import net.ganin.vsevolod.clicktrack.state.epic.SaveClickTrackEpic
import net.ganin.vsevolod.clicktrack.state.reducer.reduce
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
        @ComputationDispatcher scope: CoroutineScope,
        epicMiddleware: EpicMiddleware<AppState>,
    ): Store<AppState> {
        val initialState = savedStateHandle.get<Bundle?>(SavedStateConst.APP_STATE_BUNDLE_KEY)
            ?.getParcelable(SavedStateConst.APP_STATE_KEY)
            ?: AppState.INITIAL

        val store = Store(
            initialState,
            AppState::reduce,
            scope,
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
    abstract fun bindClickTrackPlayerEpic(epic: ClickTrackPlayerEpic): Epic

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
