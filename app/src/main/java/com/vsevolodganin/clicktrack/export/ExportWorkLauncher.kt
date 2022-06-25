package com.vsevolodganin.clicktrack.export

import android.Manifest
import android.os.Build
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.vsevolodganin.clicktrack.model.ClickTrackId
import com.vsevolodganin.clicktrack.utils.android.PermissionsHelper
import dagger.Reusable
import javax.inject.Inject

@Reusable
class ExportWorkLauncher @Inject constructor(
    private val workManager: WorkManager,
    private val permissionsHelper: PermissionsHelper,
) {
    suspend fun launchExportToAudioFile(clickTrackId: ClickTrackId.Database) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P && !permissionsHelper.requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            return
        }

        workManager.enqueueUniqueWork(
            clickTrackId.toUniqueWorkName(),
            ExistingWorkPolicy.REPLACE,
            ExportWorker.createWorkRequest(clickTrackId)
        )
    }

    fun stopExportToAudioFile(clickTrackId: ClickTrackId.Database) {
        workManager.cancelUniqueWork(clickTrackId.toUniqueWorkName())
    }

    private fun ClickTrackId.Database.toUniqueWorkName() = toString()
}
