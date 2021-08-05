package com.vsevolodganin.clicktrack.di.module

import android.content.Context
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.vsevolodganin.clicktrack.Database
import com.vsevolodganin.clicktrack.di.component.ApplicationScoped
import dagger.Module
import dagger.Provides

@Module
class DatabaseModule {

    @Provides
    @ApplicationScoped
    fun provideDatabase(context: Context): Database {
        return Database(
            AndroidSqliteDriver(
                schema = Database.Schema,
                context = context,
                name = "click_track.db",
            )
        )
    }
}
