package com.vsevolodganin.clicktrack.di.module

import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import com.vsevolodganin.clicktrack.di.component.ApplicationScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.Dispatchers

@ContributesTo(ApplicationScope::class)
@BindingContainer
object UserPreferencesModule {

    @Provides
    @SingleIn(ApplicationScope::class)
    fun provideUserPreferences(): FlowSettings {
        return NSUserDefaultsSettings.Factory().create("user_preferences").toFlowSettings(Dispatchers.Default)
    }
}
