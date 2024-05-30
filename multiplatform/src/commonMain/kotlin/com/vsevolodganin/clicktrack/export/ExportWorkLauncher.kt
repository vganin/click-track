package com.vsevolodganin.clicktrack.export

import com.vsevolodganin.clicktrack.model.ClickTrackId

interface ExportWorkLauncher {
    suspend fun launchExportToAudioFile(clickTrackId: ClickTrackId.Database)

    fun stopExportToAudioFile(clickTrackId: ClickTrackId.Database)
}
