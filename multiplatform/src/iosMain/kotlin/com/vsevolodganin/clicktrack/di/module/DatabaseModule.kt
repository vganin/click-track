package com.vsevolodganin.clicktrack.di.module

import com.squareup.sqldelight.drivers.native.NativeSqliteDriver
import com.vsevolodganin.clicktrack.Database
import com.vsevolodganin.clicktrack.di.component.ApplicationScope
import me.tatarka.inject.annotations.Provides

interface DatabaseModule {

    @Provides
    @ApplicationScope
    fun provideDatabase(): Database {
        return Database(
            NativeSqliteDriver(
                schema = Database.Schema,
                name = "click_track.db",
            )
        )
    }
}
