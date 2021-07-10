package com.vsevolodganin.clicktrack.di.component

import com.vsevolodganin.clicktrack.Application
import com.vsevolodganin.clicktrack.di.module.ApplicationScopedAndroidModule
import com.vsevolodganin.clicktrack.di.module.ApplicationScopedCoroutineModule
import com.vsevolodganin.clicktrack.di.module.DatabaseModule
import com.vsevolodganin.clicktrack.di.module.SerializationModule
import com.vsevolodganin.clicktrack.di.module.UserPreferencesModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

typealias ApplicationScoped = Singleton

@ApplicationScoped
@Component(
    modules = [
        ApplicationScopedCoroutineModule::class,
        ApplicationScopedAndroidModule::class,
        SerializationModule::class,
        DatabaseModule::class,
        UserPreferencesModule::class,
    ]
)
interface ApplicationComponent {
    fun inject(application: Application)

    fun viewModelComponentBuilder(): ViewModelComponent.Builder
    fun playerServiceComponentBuilder(): PlayerServiceComponent.Builder

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): ApplicationComponent
    }
}
