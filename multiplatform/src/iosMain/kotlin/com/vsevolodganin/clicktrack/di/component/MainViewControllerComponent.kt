package com.vsevolodganin.clicktrack.di.component

import com.arkivanov.decompose.ComponentContext
import com.vsevolodganin.clicktrack.DummyDocumentMetadataHelperImpl
import com.vsevolodganin.clicktrack.DummyExportWorkLauncherImpl
import com.vsevolodganin.clicktrack.DummyLanguageStoreImpl
import com.vsevolodganin.clicktrack.DummyPlayerServiceAccessImpl
import com.vsevolodganin.clicktrack.DummySoundChooserImpl
import com.vsevolodganin.clicktrack.RootViewModel
import com.vsevolodganin.clicktrack.export.ExportWorkLauncher
import com.vsevolodganin.clicktrack.language.LanguageStore
import com.vsevolodganin.clicktrack.migration.MigrationManager
import com.vsevolodganin.clicktrack.player.PlayerServiceAccess
import com.vsevolodganin.clicktrack.settings.debug.NativeCrash
import com.vsevolodganin.clicktrack.settings.debug.NativeCrashImpl
import com.vsevolodganin.clicktrack.soundlibrary.DocumentMetadataHelper
import com.vsevolodganin.clicktrack.soundlibrary.SoundChooser
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides

@GraphExtension(MainControllerScope::class)
interface MainViewControllerComponent {

    val rootViewModel: RootViewModel
    val migrationManager: MigrationManager

    @Binds
    val DummyPlayerServiceAccessImpl.binding: PlayerServiceAccess

    @Binds
    val DummyExportWorkLauncherImpl.binding: ExportWorkLauncher

    @Binds
    val DummyLanguageStoreImpl.binding: LanguageStore

    @Binds
    val DummySoundChooserImpl.binding: SoundChooser

    @Binds
    val DummyDocumentMetadataHelperImpl.binding: DocumentMetadataHelper

    @Binds
    val NativeCrashImpl.binding: NativeCrash

    @GraphExtension.Factory
    fun interface Factory {
        fun create(@Provides componentContext: ComponentContext): MainViewControllerComponent
    }
}
