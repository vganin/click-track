package com.vsevolodganin.clicktrack

import com.vsevolodganin.clicktrack.export.ExportWorkLauncher
import com.vsevolodganin.clicktrack.model.ClickTrackId
import me.tatarka.inject.annotations.Inject

@Inject
class DummyExportWorkLauncherImpl : ExportWorkLauncher {
    override suspend fun launchExportToAudioFile(clickTrackId: ClickTrackId.Database) = Unit
    override fun stopExportToAudioFile(clickTrackId: ClickTrackId.Database) = Unit
}
