package com.vsevolodganin.clicktrack.di.module

import android.app.Application
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.vsevolodganin.clicktrack.Database
import com.vsevolodganin.clicktrack.di.component.ApplicationScope
import me.tatarka.inject.annotations.Provides

interface DatabaseModule {

    @Provides
    @ApplicationScope
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
