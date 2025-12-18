package com.vsevolodganin.clicktrack.di.component

import com.arkivanov.decompose.ComponentContext
import com.vsevolodganin.clicktrack.RootViewModel
import com.vsevolodganin.clicktrack.migration.MigrationManager
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides

@GraphExtension(
    scope = MainControllerScope::class,
    additionalScopes = [PlayerServiceScope::class],
)
interface MainViewControllerComponent {

    val rootViewModel: RootViewModel
    val migrationManager: MigrationManager

    @GraphExtension.Factory
    fun interface Factory {
        fun create(@Provides componentContext: ComponentContext): MainViewControllerComponent
    }
}
