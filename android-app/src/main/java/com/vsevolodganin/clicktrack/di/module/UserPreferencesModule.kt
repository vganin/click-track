package com.vsevolodganin.clicktrack.di.module

import android.app.Application
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import com.russhwolf.settings.SharedPreferencesSettings
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import me.tatarka.inject.annotations.Provides

interface UserPreferencesModule {
    @Provides
    fun provideUserPreferences(application: Application): FlowSettings {
        return SharedPreferencesSettings.Factory(application).create("user_preferences")
            .toFlowSettings(Dispatchers.IO)
            .also { it.runDataStoreMigration(application) }
    }

    private fun FlowSettings.runDataStoreMigration(application: Application) {
        val legacyDataStoreFile = application.preferencesDataStoreFile("user_preferences")
        if (legacyDataStoreFile.exists()) {
            val legacyDataStore = PreferenceDataStoreFactory.create(
                migrations = listOf(SharedPreferencesMigration(application, "user_preferences")),
                produceFile = { application.preferencesDataStoreFile("user_preferences") },
            )
            runBlocking {
                val legacyData = legacyDataStore.data.first()
                legacyData.asMap().forEach { (key, value) ->
                    // We are inspecting only those types that were used at the time of writing.
                    when (value) {
                        is Boolean -> putBoolean(key.name, value)
                        is Int -> putInt(key.name, value)
                        is Long -> putLong(key.name, value)
                        is String -> putString(key.name, value)
                    }
                }
            }
            legacyDataStoreFile.delete()
        }
    }
}
