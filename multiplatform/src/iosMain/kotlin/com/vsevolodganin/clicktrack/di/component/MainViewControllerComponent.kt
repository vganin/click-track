package com.vsevolodganin.clicktrack.di.component

import com.arkivanov.decompose.ComponentContext
import com.vsevolodganin.clicktrack.RootViewModel
import com.vsevolodganin.clicktrack.ScreenViewModelFactory
import com.vsevolodganin.clicktrack.ScreenViewModelFactoryImpl
import com.vsevolodganin.clicktrack.di.module.MigrationModule
import com.vsevolodganin.clicktrack.di.module.ViewModelModule
import com.vsevolodganin.clicktrack.migration.MigrationManager
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@MainControllerScope
@Component
abstract class MainViewControllerComponent(
    @Component protected val applicationComponent: ApplicationComponent,
    @get:Provides protected val componentContext: ComponentContext
) : ViewModelModule,
    MigrationModule {

    abstract val rootViewModel: RootViewModel
    abstract val migrationManager: MigrationManager

    @get:Provides
    protected val ScreenViewModelFactoryImpl.screenViewModelFactory: ScreenViewModelFactory get() = this
}
