package com.vsevolodganin.clicktrack.di.module

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object UserPreferencesModule {

    @Provides
    @Singleton
    fun provideUserPreferences(application: Application): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            migrations = listOf(SharedPreferencesMigration(application, "user_preferences")),
            produceFile = { application.preferencesDataStoreFile("user_preferences") },
        )
    }
}
