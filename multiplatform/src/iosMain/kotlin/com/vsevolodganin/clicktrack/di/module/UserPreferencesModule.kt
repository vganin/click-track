package com.vsevolodganin.clicktrack.di.module

import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import kotlinx.coroutines.Dispatchers
import me.tatarka.inject.annotations.Provides

interface UserPreferencesModule {
    @Provides
    fun provideUserPreferences(): FlowSettings {
        return NSUserDefaultsSettings.Factory().create("user_preferences").toFlowSettings(Dispatchers.Default)
    }
}
