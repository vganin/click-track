package com.vsevolodganin.clicktrack.di.component

import com.vsevolodganin.clicktrack.audio.SoundPreloader
import com.vsevolodganin.clicktrack.di.module.ApplicationModule
import com.vsevolodganin.clicktrack.di.module.DatabaseModule
import com.vsevolodganin.clicktrack.di.module.FirebaseModule
import com.vsevolodganin.clicktrack.di.module.SerializationModule
import com.vsevolodganin.clicktrack.di.module.UserPreferencesModule
import com.vsevolodganin.clicktrack.storage.UserPreferencesRepository
import com.vsevolodganin.clicktrack.theme.ThemeManager
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Inject
import me.tatarka.inject.annotations.Provides
import me.tatarka.inject.annotations.Scope

@Scope
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER)
annotation class ApplicationScope

@ApplicationScope
@Component
@Inject
abstract class ApplicationComponent(
    @get:Provides val application: android.app.Application
) : ApplicationModule,
    SerializationModule,
    DatabaseModule,
    UserPreferencesModule,
    FirebaseModule {

    abstract val userPreferences: UserPreferencesRepository
    abstract val themeManager: ThemeManager
    abstract val soundsPreloader: SoundPreloader
}
