package com.vsevolodganin.clicktrack.di.component

import com.arkivanov.decompose.ComponentContext
import com.vsevolodganin.clicktrack.IntentProcessor
import com.vsevolodganin.clicktrack.MainActivity
import com.vsevolodganin.clicktrack.RootViewModel
import com.vsevolodganin.clicktrack.common.InAppReview
import com.vsevolodganin.clicktrack.di.module.ActivityModule
import com.vsevolodganin.clicktrack.di.module.GooglePlayModule
import com.vsevolodganin.clicktrack.di.module.MigrationModule
import com.vsevolodganin.clicktrack.di.module.ViewModelModule
import com.vsevolodganin.clicktrack.export.ExportWorkLauncher
import com.vsevolodganin.clicktrack.export.ExportWorkLauncherImpl
import com.vsevolodganin.clicktrack.language.LanguageStore
import com.vsevolodganin.clicktrack.language.LanguageStoreImpl
import com.vsevolodganin.clicktrack.migration.MigrationManager
import com.vsevolodganin.clicktrack.player.PlayerServiceAccess
import com.vsevolodganin.clicktrack.player.PlayerServiceAccessImpl
import com.vsevolodganin.clicktrack.settings.debug.NativeCrash
import com.vsevolodganin.clicktrack.settings.debug.NativeCrashImpl
import com.vsevolodganin.clicktrack.soundlibrary.DocumentMetadataHelper
import com.vsevolodganin.clicktrack.soundlibrary.DocumentMetadataHelperImpl
import com.vsevolodganin.clicktrack.soundlibrary.SoundChooser
import com.vsevolodganin.clicktrack.soundlibrary.SoundChooserImpl
import com.vsevolodganin.clicktrack.utils.android.PermissionsHelper
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Includes
import dev.zacsweers.metro.Provides

@MainControllerScope
@DependencyGraph
abstract class MainActivityComponent(
    @Includes protected val applicationComponent: ApplicationComponent,
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
    abstract val soundChooser: SoundChooserImpl
    abstract val permissionsHelper: PermissionsHelper

    protected val PlayerServiceAccessImpl.binding: PlayerServiceAccess @Provides get() = this
    protected val ExportWorkLauncherImpl.binding: ExportWorkLauncher @Provides get() = this
    protected val LanguageStoreImpl.binding: LanguageStore @Provides get() = this
    protected val SoundChooserImpl.binding: SoundChooser @Provides get() = this
    protected val DocumentMetadataHelperImpl.binding: DocumentMetadataHelper @Provides get() = this
    protected val NativeCrashImpl.binding: NativeCrash @Provides get() = this
}
