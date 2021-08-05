package com.vsevolodganin.clicktrack.di.module

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.vsevolodganin.clicktrack.di.component.ApplicationScoped
import dagger.Module
import dagger.Provides

@Module
object UserPreferencesModule {

    @Provides
    @ApplicationScoped
    fun provideUserPreferences(context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            migrations = listOf(SharedPreferencesMigration(context, "user_preferences")),
            produceFile = { context.preferencesDataStoreFile("user_preferences") },
        )
    }
}
