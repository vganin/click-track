package com.vsevolodganin.clicktrack.di.module

import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.vsevolodganin.clicktrack.Database
import com.vsevolodganin.clicktrack.di.component.ApplicationScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@ContributesTo(ApplicationScope::class)
@BindingContainer
object DatabaseModule {

    @Provides
    @SingleIn(ApplicationScope::class)
    fun provideDatabase(): Database {
        return Database(
            NativeSqliteDriver(
                schema = Database.Schema,
                name = "click_track.db",
            ),
        )
    }
}
