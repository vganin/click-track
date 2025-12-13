package com.vsevolodganin.clicktrack.di.component

import com.arkivanov.decompose.ComponentContext
import com.vsevolodganin.clicktrack.DummyDocumentMetadataHelperImpl
import com.vsevolodganin.clicktrack.DummyExportWorkLauncherImpl
import com.vsevolodganin.clicktrack.DummyLanguageStoreImpl
import com.vsevolodganin.clicktrack.DummyPlayerServiceAccessImpl
import com.vsevolodganin.clicktrack.DummySoundChooserImpl
import com.vsevolodganin.clicktrack.RootViewModel
import com.vsevolodganin.clicktrack.di.module.MigrationModule
import com.vsevolodganin.clicktrack.di.module.ViewModelModule
import com.vsevolodganin.clicktrack.export.ExportWorkLauncher
import com.vsevolodganin.clicktrack.language.LanguageStore
import com.vsevolodganin.clicktrack.migration.MigrationManager
import com.vsevolodganin.clicktrack.player.PlayerServiceAccess
import com.vsevolodganin.clicktrack.settings.debug.NativeCrash
import com.vsevolodganin.clicktrack.settings.debug.NativeCrashImpl
import com.vsevolodganin.clicktrack.soundlibrary.DocumentMetadataHelper
import com.vsevolodganin.clicktrack.soundlibrary.SoundChooser
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Includes
import dev.zacsweers.metro.Provides
import kotlin.reflect.KClass

@MainControllerScope
@DependencyGraph
abstract class MainViewControllerComponent(
    @Includes protected val applicationComponent: ApplicationComponent,
    @get:Provides protected val componentContext: ComponentContext,
) : ViewModelModule,
    MigrationModule {
    abstract val rootViewModel: RootViewModel
    abstract val migrationManager: MigrationManager

    protected val DummyPlayerServiceAccessImpl.binding: PlayerServiceAccess @Provides get() = this
    protected val DummyExportWorkLauncherImpl.binding: ExportWorkLauncher @Provides get() = this
    protected val DummyLanguageStoreImpl.binding: LanguageStore @Provides get() = this
    protected val DummySoundChooserImpl.binding: SoundChooser @Provides get() = this
    protected val DummyDocumentMetadataHelperImpl.binding: DocumentMetadataHelper @Provides get() = this
    protected val NativeCrashImpl.binding: NativeCrash @Provides get() = this
}

expect fun KClass<MainViewControllerComponent>.createKmp(
    applicationComponent: ApplicationComponent,
    componentContext: ComponentContext,
): MainViewControllerComponent
