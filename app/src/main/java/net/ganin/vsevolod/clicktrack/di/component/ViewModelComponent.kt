package net.ganin.vsevolod.clicktrack.di.component

import androidx.lifecycle.SavedStateHandle
import dagger.BindsInstance
import dagger.Subcomponent
import net.ganin.vsevolod.clicktrack.MainViewModel
import net.ganin.vsevolod.clicktrack.di.module.AppStateModule
import net.ganin.vsevolod.clicktrack.di.module.ViewModelScopedAppStateEpicModule
import net.ganin.vsevolod.clicktrack.di.module.ViewModelScopedCoroutineModule
import net.ganin.vsevolod.clicktrack.di.module.ViewModelScopedPlayerModule
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
