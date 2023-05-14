package com.vsevolodganin.clicktrack.di.component

import com.vsevolodganin.clicktrack.common.BuildConfig
import com.vsevolodganin.clicktrack.common.BuildConfigImpl
import com.vsevolodganin.clicktrack.di.module.DatabaseModule
import com.vsevolodganin.clicktrack.di.module.SerializationModule
import com.vsevolodganin.clicktrack.di.module.UserPreferencesModule
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@ApplicationScope
@Component
abstract class ApplicationComponent :
    SerializationModule,
    DatabaseModule,
    UserPreferencesModule {

    @get:Provides
    val BuildConfigImpl.buildConfig: BuildConfig get() = this
}
