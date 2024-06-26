package com.vsevolodganin.clicktrack.di.component

import com.vsevolodganin.clicktrack.NativeLibraries
import com.vsevolodganin.clicktrack.common.BuildConfig
import com.vsevolodganin.clicktrack.common.BuildConfigImpl
import com.vsevolodganin.clicktrack.di.module.ApplicationModule
import com.vsevolodganin.clicktrack.di.module.DatabaseModule
import com.vsevolodganin.clicktrack.di.module.FirebaseModule
import com.vsevolodganin.clicktrack.di.module.SerializationModule
import com.vsevolodganin.clicktrack.di.module.UserPreferencesModule
import com.vsevolodganin.clicktrack.storage.UserPreferencesRepository
import com.vsevolodganin.clicktrack.theme.ThemeManager
import com.vsevolodganin.clicktrack.utils.log.Logger
import com.vsevolodganin.clicktrack.utils.log.LoggerImpl
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@ApplicationScope
@Component
abstract class ApplicationComponent(
    @get:Provides val application: android.app.Application,
) : ApplicationModule,
    SerializationModule,
    DatabaseModule,
    UserPreferencesModule,
    FirebaseModule {
    abstract val nativeLibraries: NativeLibraries
    abstract val userPreferences: UserPreferencesRepository
    abstract val themeManager: ThemeManager

    @get:Provides
    val BuildConfigImpl.binding: BuildConfig get() = this

    @get:Provides
    val LoggerImpl.binding: Logger get() = this
}
