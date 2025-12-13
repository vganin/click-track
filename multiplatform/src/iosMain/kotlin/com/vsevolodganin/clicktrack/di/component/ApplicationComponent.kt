package com.vsevolodganin.clicktrack.di.component

import com.vsevolodganin.clicktrack.common.BuildConfig
import com.vsevolodganin.clicktrack.common.BuildConfigImpl
import com.vsevolodganin.clicktrack.di.module.DatabaseModule
import com.vsevolodganin.clicktrack.di.module.SerializationModule
import com.vsevolodganin.clicktrack.di.module.UserPreferencesModule
import com.vsevolodganin.clicktrack.utils.log.Logger
import com.vsevolodganin.clicktrack.utils.log.LoggerImpl
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import kotlin.reflect.KClass

@ApplicationScope
@DependencyGraph
abstract class ApplicationComponent :
    SerializationModule,
    DatabaseModule,
    UserPreferencesModule {
    @get:Provides
    val BuildConfigImpl.binding: BuildConfig get() = this

    @get:Provides
    val LoggerImpl.binding: Logger get() = this

    @DependencyGraph.Factory
    fun interface Factory {
        fun create(): ApplicationComponent
    }
}

expect fun KClass<ApplicationComponent>.createKmp(): ApplicationComponent
