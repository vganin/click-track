package com.vsevolodganin.clicktrack.di.module

import com.vsevolodganin.clicktrack.di.component.ActivityScoped
import com.vsevolodganin.clicktrack.migration.CanMigrate
import com.vsevolodganin.clicktrack.storage.ClickTrackRepository
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet

@Module
interface MigrationModule {

    @Binds
    @IntoSet
    @ActivityScoped
    fun bindClickTrackRepository(clickTrackRepository: ClickTrackRepository): CanMigrate
}
