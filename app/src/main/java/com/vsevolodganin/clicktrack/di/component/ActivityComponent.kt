package com.vsevolodganin.clicktrack.di.component

import com.arkivanov.decompose.ComponentContext
import com.vsevolodganin.clicktrack.MainActivity
import com.vsevolodganin.clicktrack.di.module.ActivityModule
import com.vsevolodganin.clicktrack.di.module.GooglePlayModule
import com.vsevolodganin.clicktrack.di.module.MigrationModule
import com.vsevolodganin.clicktrack.di.module.ViewModelModule
import dagger.BindsInstance
import dagger.Subcomponent
import javax.inject.Scope

@Scope
annotation class ActivityScope

@ActivityScope
@Subcomponent(
    modules = [
        ActivityModule::class,
        ViewModelModule::class,
        MigrationModule::class,
        GooglePlayModule::class,
    ]
)
interface ActivityComponent {
    fun inject(mainActivity: MainActivity)

    @Subcomponent.Builder
    interface Builder {
        @BindsInstance
        fun activity(activity: MainActivity): Builder

        @BindsInstance
        fun rootComponentContext(componentContext: ComponentContext): Builder

        fun build(): ActivityComponent
    }
}
