package com.vsevolodganin.clicktrack.di.module

import com.vsevolodganin.clicktrack.di.component.ActivityScope
import com.vsevolodganin.clicktrack.migration.CanMigrate
import com.vsevolodganin.clicktrack.storage.ClickTrackRepository
import me.tatarka.inject.annotations.IntoSet
import me.tatarka.inject.annotations.Provides

interface MigrationModule {

    @Provides
    @IntoSet
    @ActivityScope
    fun provideCanMigrate(clickTrackRepository: ClickTrackRepository): CanMigrate = clickTrackRepository
}
