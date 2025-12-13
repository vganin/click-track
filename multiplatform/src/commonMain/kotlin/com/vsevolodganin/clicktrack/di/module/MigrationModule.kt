package com.vsevolodganin.clicktrack.di.module

import com.vsevolodganin.clicktrack.di.component.MainControllerScope
import com.vsevolodganin.clicktrack.migration.CanMigrate
import com.vsevolodganin.clicktrack.storage.ClickTrackRepository
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@ContributesTo(MainControllerScope::class)
@BindingContainer
object MigrationModule {
    @Provides
    @IntoSet
    @SingleIn(MainControllerScope::class)
    fun provideCanMigrate(clickTrackRepository: ClickTrackRepository): CanMigrate = clickTrackRepository
}
