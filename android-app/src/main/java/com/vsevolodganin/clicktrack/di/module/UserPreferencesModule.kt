package com.vsevolodganin.clicktrack.di.module

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import me.tatarka.inject.annotations.Provides

interface UserPreferencesModule {

    @Provides
    fun provideUserPreferences(application: Application): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            migrations = listOf(SharedPreferencesMigration(application, "user_preferences")),
            produceFile = { application.preferencesDataStoreFile("user_preferences") },
        )
    }
}
