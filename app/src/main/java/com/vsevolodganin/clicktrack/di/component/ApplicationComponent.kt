package com.vsevolodganin.clicktrack.di.component

import com.vsevolodganin.clicktrack.Application
import com.vsevolodganin.clicktrack.di.module.ApplicationScopedAndroidModule
import com.vsevolodganin.clicktrack.di.module.ApplicationScopedCoroutineModule
import com.vsevolodganin.clicktrack.di.module.DatabaseModule
import com.vsevolodganin.clicktrack.di.module.FirebaseModule
import com.vsevolodganin.clicktrack.di.module.SerializationModule
import com.vsevolodganin.clicktrack.di.module.UserPreferencesModule
import com.vsevolodganin.clicktrack.export.ExportWorker
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        ApplicationScopedCoroutineModule::class,
        ApplicationScopedAndroidModule::class,
        SerializationModule::class,
        DatabaseModule::class,
        UserPreferencesModule::class,
        FirebaseModule::class,
    ]
)
interface ApplicationComponent {
    fun inject(application: Application)
    fun inject(worker: ExportWorker)

    fun viewModelComponentBuilder(): ViewModelComponent.Builder
    fun playerServiceComponentBuilder(): PlayerServiceComponent.Builder

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): ApplicationComponent
    }
}
