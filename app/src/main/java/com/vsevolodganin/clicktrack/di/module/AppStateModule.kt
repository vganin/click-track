package com.vsevolodganin.clicktrack.di.module

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import com.vsevolodganin.clicktrack.analytics.AnalyticsLogger
import com.vsevolodganin.clicktrack.di.component.ActivityScoped
import com.vsevolodganin.clicktrack.di.component.ViewModelScoped
import com.vsevolodganin.clicktrack.state.redux.AppState
import com.vsevolodganin.clicktrack.state.redux.core.AnalyticsMiddleware
import com.vsevolodganin.clicktrack.state.redux.core.DebugMiddleware
import com.vsevolodganin.clicktrack.state.redux.core.Epic
import com.vsevolodganin.clicktrack.state.redux.core.EpicMiddleware
import com.vsevolodganin.clicktrack.state.redux.core.Store
import com.vsevolodganin.clicktrack.state.redux.epic.AboutEpic
import com.vsevolodganin.clicktrack.state.redux.epic.ClickTrackEpic
import com.vsevolodganin.clicktrack.state.redux.epic.ExportEpic
import com.vsevolodganin.clicktrack.state.redux.epic.FinishAppEpic
import com.vsevolodganin.clicktrack.state.redux.epic.InAppReviewEpic
import com.vsevolodganin.clicktrack.state.redux.epic.MetronomeEpic
import com.vsevolodganin.clicktrack.state.redux.epic.NavigationEpic
import com.vsevolodganin.clicktrack.state.redux.epic.PlayerEpic
import com.vsevolodganin.clicktrack.state.redux.epic.PolyrhythmsEpic
import com.vsevolodganin.clicktrack.state.redux.epic.SettingsEpic
import com.vsevolodganin.clicktrack.state.redux.epic.SoundChooserEpic
import com.vsevolodganin.clicktrack.state.redux.epic.SoundLibraryEpic
import com.vsevolodganin.clicktrack.state.redux.epic.TrainingEpic
import com.vsevolodganin.clicktrack.state.redux.reducer.reduce
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
        coroutineScope: CoroutineScope,
        @SerialBackgroundDispatcher coroutineDispatcher: CoroutineDispatcher,
    ): EpicMiddleware<AppState> {
        return EpicMiddleware(CoroutineScope(coroutineScope.coroutineContext + coroutineDispatcher))
    }

    @Provides
    fun provideDebugMiddleware(): DebugMiddleware<AppState> = DebugMiddleware()

    @Provides
    fun provideAnalyticsMiddleware(analyticsLogger: AnalyticsLogger): AnalyticsMiddleware<AppState> = AnalyticsMiddleware(analyticsLogger)

    @Provides
    @ViewModelScoped
    fun provideStore(
        savedStateHandle: SavedStateHandle,
        coroutineScope: CoroutineScope,
        @SerialBackgroundDispatcher coroutineDispatcher: CoroutineDispatcher,
        epicMiddleware: EpicMiddleware<AppState>,
        debugMiddleware: DebugMiddleware<AppState>,
        analyticsMiddleware: AnalyticsMiddleware<AppState>,
    ): Store<AppState> {
        val initialState = savedStateHandle.get<Bundle?>(SavedStateConst.APP_STATE_BUNDLE_KEY)
            ?.getParcelable(SavedStateConst.APP_STATE_KEY)
            ?: AppState.INITIAL

        val store = Store(
            initialState,
            AppState::reduce,
            CoroutineScope(coroutineScope.coroutineContext + coroutineDispatcher),
            epicMiddleware,
            debugMiddleware,
            analyticsMiddleware,
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
    abstract fun bindLoadDataEpic(epic: ClickTrackEpic): Epic

    @ViewModelScoped
    @Binds
    @IntoSet
    @ViewModelScopedAppStateEpic
    abstract fun bindMetronomeEpic(epic: MetronomeEpic): Epic

    @ViewModelScoped
    @Binds
    @IntoSet
    @ViewModelScopedAppStateEpic
    abstract fun bindTrainingEpic(epic: TrainingEpic): Epic

    @ViewModelScoped
    @Binds
    @IntoSet
    @ViewModelScopedAppStateEpic
    abstract fun bindSettingsEpic(epic: SettingsEpic): Epic

    @ViewModelScoped
    @Binds
    @IntoSet
    @ViewModelScopedAppStateEpic
    abstract fun bindSoundLibraryEpic(epic: SoundLibraryEpic): Epic

    @ViewModelScoped
    @Binds
    @IntoSet
    @ViewModelScopedAppStateEpic
    abstract fun bindPolyrhythmsEpic(epic: PolyrhythmsEpic): Epic
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

    @ActivityScoped
    @Binds
    @IntoSet
    @ActivityScopedAppStateEpic
    abstract fun bindSoundChooserEpic(epic: SoundChooserEpic): Epic

    @ActivityScoped
    @Binds
    @IntoSet
    @ActivityScopedAppStateEpic
    abstract fun bindNavigationEpic(epic: NavigationEpic): Epic

    @ActivityScoped
    @Binds
    @IntoSet
    @ActivityScopedAppStateEpic
    abstract fun bindAboutEpic(epic: AboutEpic): Epic

    @ActivityScoped
    @Binds
    @IntoSet
    @ActivityScopedAppStateEpic
    abstract fun bindInAppReviewEpic(epic: InAppReviewEpic): Epic

    @ActivityScoped
    @Binds
    @IntoSet
    @ActivityScopedAppStateEpic
    abstract fun bindExportEpic(epic: ExportEpic): Epic
}
