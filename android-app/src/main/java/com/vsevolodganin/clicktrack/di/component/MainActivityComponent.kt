package com.vsevolodganin.clicktrack.di.component

import com.arkivanov.decompose.ComponentContext
import com.vsevolodganin.clicktrack.MainActivity
import com.vsevolodganin.clicktrack.export.ExportWorkLauncher
import com.vsevolodganin.clicktrack.export.ExportWorkLauncherImpl
import com.vsevolodganin.clicktrack.language.LanguageStore
import com.vsevolodganin.clicktrack.language.LanguageStoreImpl
import com.vsevolodganin.clicktrack.player.PlayerServiceAccess
import com.vsevolodganin.clicktrack.player.PlayerServiceAccessImpl
import com.vsevolodganin.clicktrack.settings.debug.NativeCrash
import com.vsevolodganin.clicktrack.settings.debug.NativeCrashImpl
import com.vsevolodganin.clicktrack.soundlibrary.DocumentMetadataHelper
import com.vsevolodganin.clicktrack.soundlibrary.DocumentMetadataHelperImpl
import com.vsevolodganin.clicktrack.soundlibrary.SoundChooser
import com.vsevolodganin.clicktrack.soundlibrary.SoundChooserImpl
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides

@GraphExtension(MainControllerScope::class)
interface MainActivityComponent {

    fun inject(activity: MainActivity)

    @Binds
    val PlayerServiceAccessImpl.binding: PlayerServiceAccess

    @Binds
    val ExportWorkLauncherImpl.binding: ExportWorkLauncher

    @Binds
    val LanguageStoreImpl.binding: LanguageStore

    @Binds
    val SoundChooserImpl.binding: SoundChooser

    @Binds
    val DocumentMetadataHelperImpl.binding: DocumentMetadataHelper

    @Binds
    val NativeCrashImpl.binding: NativeCrash

    @GraphExtension.Factory
    fun interface Factory {
        fun create(
            @Provides activity: MainActivity,
            @Provides componentContext: ComponentContext,
        ): MainActivityComponent
    }
}
