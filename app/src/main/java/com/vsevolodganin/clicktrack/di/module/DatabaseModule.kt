package com.vsevolodganin.clicktrack.di.module

import android.content.Context
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.vsevolodganin.clicktrack.Database
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object DatabaseModule {

    @Provides
    @Singleton
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
