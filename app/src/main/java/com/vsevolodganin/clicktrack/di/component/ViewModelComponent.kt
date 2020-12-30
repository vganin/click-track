package com.vsevolodganin.clicktrack.di.component

import androidx.lifecycle.SavedStateHandle
import com.vsevolodganin.clicktrack.MainViewModel
import com.vsevolodganin.clicktrack.di.module.AppStateModule
import com.vsevolodganin.clicktrack.di.module.ViewModelScopedAppStateEpicModule
import com.vsevolodganin.clicktrack.di.module.ViewModelScopedCoroutineModule
import com.vsevolodganin.clicktrack.di.module.ViewModelScopedPlayerModule
import dagger.BindsInstance
import dagger.Subcomponent
import javax.inject.Scope

@Scope
annotation class ViewModelScoped

@ViewModelScoped
@Subcomponent(
    modules = [
        AppStateModule::class,
        ViewModelScopedAppStateEpicModule::class,
        ViewModelScopedCoroutineModule::class,
        ViewModelScopedPlayerModule::class
    ]
)
interface ViewModelComponent {
    fun activityComponentBuilder(): ActivityComponent.Builder
    fun inject(mainViewModel: MainViewModel)

    @Subcomponent.Builder
    interface Builder {
        @BindsInstance
        fun savedStateHandle(savedStateHandle: SavedStateHandle): Builder

        fun build(): ViewModelComponent
    }
}
