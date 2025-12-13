package com.vsevolodganin.clicktrack.export

import android.Manifest
import android.os.Build
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.vsevolodganin.clicktrack.model.ClickTrackId
import com.vsevolodganin.clicktrack.utils.android.PermissionsHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import dev.zacsweers.metro.Inject

@Inject
class ExportWorkLauncherImpl(
    private val workManager: WorkManager,
    private val permissionsHelper: PermissionsHelper,
) : ExportWorkLauncher {
    override suspend fun launchExportToAudioFile(clickTrackId: ClickTrackId.Database) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P && !requestStoragePermission()) {
            return
        }

        workManager.enqueueUniqueWork(
            clickTrackId.toUniqueWorkName(),
            ExistingWorkPolicy.REPLACE,
            ExportWorker.createWorkRequest(clickTrackId),
        )
    }

    override fun stopExportToAudioFile(clickTrackId: ClickTrackId.Database) {
        workManager.cancelUniqueWork(clickTrackId.toUniqueWorkName())
    }

    private suspend fun requestStoragePermission(): Boolean {
        return withContext(Dispatchers.Main) {
            permissionsHelper.requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!!
        }
    }

    private fun ClickTrackId.Database.toUniqueWorkName() = toString()
}
