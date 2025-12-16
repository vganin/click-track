package com.vsevolodganin.clicktrack

import com.vsevolodganin.clicktrack.di.component.MainControllerScope
import com.vsevolodganin.clicktrack.export.ExportWorkLauncher
import com.vsevolodganin.clicktrack.model.ClickTrackId
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

@ContributesBinding(MainControllerScope::class)
@Inject
class DummyExportWorkLauncherImpl : ExportWorkLauncher {
    override suspend fun launchExportToAudioFile(clickTrackId: ClickTrackId.Database) = Unit

    override fun stopExportToAudioFile(clickTrackId: ClickTrackId.Database) = Unit
}
