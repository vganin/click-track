package com.vsevolodganin.clicktrack.di.module

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import kotlinx.coroutines.Dispatchers
import dev.zacsweers.metro.Provides

@OptIn(ExperimentalSettingsApi::class)
interface UserPreferencesModule {

    @Provides
    fun provideUserPreferences(): FlowSettings {
        return NSUserDefaultsSettings.Factory().create("user_preferences").toFlowSettings(Dispatchers.Default)
    }
}
