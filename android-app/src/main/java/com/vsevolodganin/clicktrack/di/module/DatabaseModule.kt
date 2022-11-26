package com.vsevolodganin.clicktrack.di.module

import android.app.Application
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.vsevolodganin.clicktrack.Database
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(application: Application): Database {
        return Database(
            AndroidSqliteDriver(
                schema = Database.Schema,
                context = application,
                name = "click_track.db",
            )
        )
    }
}
