package com.vsevolodganin.clicktrack.di.component

import com.vsevolodganin.clicktrack.common.BuildConfig
import com.vsevolodganin.clicktrack.common.BuildConfigImpl
import com.vsevolodganin.clicktrack.di.module.DatabaseModule
import com.vsevolodganin.clicktrack.di.module.SerializationModule
import com.vsevolodganin.clicktrack.di.module.UserPreferencesModule
import com.vsevolodganin.clicktrack.utils.log.Logger
import com.vsevolodganin.clicktrack.utils.log.LoggerImpl
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@ApplicationScope
@Component
abstract class ApplicationComponent :
    SerializationModule,
    DatabaseModule,
    UserPreferencesModule {
    @get:Provides
    val BuildConfigImpl.binding: BuildConfig get() = this

    @get:Provides
    val LoggerImpl.binding: Logger get() = this
}
