package com.vsevolodganin.clicktrack.di.component

import com.arkivanov.decompose.ComponentContext
import com.vsevolodganin.clicktrack.IntentProcessor
import com.vsevolodganin.clicktrack.MainActivity
import com.vsevolodganin.clicktrack.RootViewModel
import com.vsevolodganin.clicktrack.ScreenViewModelFactory
import com.vsevolodganin.clicktrack.ScreenViewModelFactoryImpl
import com.vsevolodganin.clicktrack.common.InAppReview
import com.vsevolodganin.clicktrack.di.module.ActivityModule
import com.vsevolodganin.clicktrack.di.module.GooglePlayModule
import com.vsevolodganin.clicktrack.di.module.MigrationModule
import com.vsevolodganin.clicktrack.di.module.ViewModelModule
import com.vsevolodganin.clicktrack.migration.MigrationManager
import com.vsevolodganin.clicktrack.soundlibrary.SoundChooser
import com.vsevolodganin.clicktrack.utils.android.PermissionsHelper
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@MainControllerScope
@Component
abstract class MainActivityComponent(
    @Component protected val applicationComponent: ApplicationComponent,
    @get:Provides protected val activity: MainActivity,
    @get:Provides protected val componentContext: ComponentContext,
) : ActivityModule,
    ViewModelModule,
    MigrationModule,
    GooglePlayModule {

    abstract val intentProcessor: IntentProcessor
    abstract val migrationManager: MigrationManager
    abstract val rootViewModel: RootViewModel
    abstract val inAppReview: InAppReview
    abstract val soundChooser: SoundChooser
    abstract val permissionsHelper: PermissionsHelper

    // FIXME: Temp
    @get:Provides
    protected val ScreenViewModelFactoryImpl.screenViewModelFactory: ScreenViewModelFactory get() = this
}
