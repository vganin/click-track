package com.vsevolodganin.clicktrack.di.component

import com.vsevolodganin.clicktrack.Application
import com.vsevolodganin.clicktrack.di.module.AndroidModule
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
        AndroidModule::class,
        SerializationModule::class,
        DatabaseModule::class,
        UserPreferencesModule::class,
        FirebaseModule::class,
    ]
)
interface ApplicationComponent {
    fun inject(application: Application)
    fun inject(worker: ExportWorker)

    fun activityComponentBuilder(): ActivityComponent.Builder
    fun playerServiceComponentBuilder(): PlayerServiceComponent.Builder

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): ApplicationComponent
    }
}
