package com.vsevolodganin.clicktrack.export

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.net.Uri
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.res.ResourcesCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.vsevolodganin.clicktrack.BuildConfig
import com.vsevolodganin.clicktrack.R
import com.vsevolodganin.clicktrack.applicationComponent
import com.vsevolodganin.clicktrack.di.component.ExportWorkerComponent
import com.vsevolodganin.clicktrack.di.component.create
import com.vsevolodganin.clicktrack.model.ClickTrack
import com.vsevolodganin.clicktrack.model.ClickTrackId
import com.vsevolodganin.clicktrack.model.ClickTrackWithDatabaseId
import kotlinx.coroutines.flow.firstOrNull
import kotlin.math.roundToInt

class ExportWorker(private val appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {
    companion object {
        fun createWorkRequest(clickTrackId: ClickTrackId.Database): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<ExportWorker>()
                .setInputData(
                    workDataOf(
                        InputKeys.CLICK_TRACK_ID to clickTrackId.value,
                    ),
                )
                .build()
        }
    }

    private val component = ExportWorkerComponent::class.create(appContext.applicationComponent)

    // Need unique id to show multiple progress notifications and we can't use string tags
    private val foregroundNotificationId = id.leastSignificantBits.toInt()

    override suspend fun doWork(): Result {
        val clickTrackId = inputData.getLong(InputKeys.CLICK_TRACK_ID, -1)
            .takeIf { it >= 0 }
            ?.let(ClickTrackId::Database)
            ?: return Result.failure()

        val clickTrack = component.clickTrackRepository.getById(clickTrackId).firstOrNull()
            ?: return Result.failure()

        setForeground(foregroundInfo(clickTrack, 0f))

        val temporaryFile = component.exportToAudioFile.export(
            clickTrack = clickTrack.value,
            reportProgress = {
                setForeground(foregroundInfo(clickTrack, it))
            },
        ) ?: return Result.failure()

        val accessUri = component.mediaStoreAccess.addAudioFile(temporaryFile)
            ?: return Result.failure()

        // Just speeding things up, this file will be deleted by system anyway
        temporaryFile.delete()

        component.notificationManager.cancel(foregroundNotificationId)
        notifyFinished(clickTrack.value, accessUri)

        return Result.success()
    }

    private fun foregroundInfo(clickTrack: ClickTrackWithDatabaseId, progress: Float): ForegroundInfo {
        val tapIntent = component.intentFactory.navigateClickTrack(clickTrack.id)

        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val progressResolution = 100

        return ForegroundInfo(
            foregroundNotificationId,
            NotificationCompat.Builder(appContext, component.notificationChannels.export)
                .setContentTitle(appContext.getString(R.string.export_worker_notification_in_process, clickTrack.value.name))
                .setSmallIcon(R.drawable.ic_notification)
                .setColor(ResourcesCompat.getColor(appContext.resources, R.color.blood_red, null))
                .setOngoing(true)
                .setContentIntent(PendingIntent.getActivity(appContext, 0, tapIntent, pendingIntentFlags))
                .addAction(
                    android.R.drawable.ic_delete,
                    appContext.getString(android.R.string.cancel),
                    component.workManager.createCancelPendingIntent(id),
                )
                .setGroup(NotificationGroups.EXPORTING)
                .setProgress(progressResolution, (progress * progressResolution).roundToInt(), false)
                .build(),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            } else {
                0
            },
        )
    }

    private fun notifyFinished(clickTrack: ClickTrack, accessUri: Uri) {
        val tapIntent = Intent.createChooser(
            Intent(Intent.ACTION_VIEW).apply {
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
                setDataAndType(accessUri, "audio/*")
            },
            null,
        )

        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        if (ActivityCompat.checkSelfPermission(appContext, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            component.notificationManager.notify(
                clickTrack.name,
                R.id.notification_export_finished,
                NotificationCompat.Builder(appContext, component.notificationChannels.export)
                    .setContentTitle(appContext.getString(R.string.export_worker_notification_finished, clickTrack.name))
                    .setContentText(appContext.getString(R.string.export_worker_notification_open))
                    .setSmallIcon(R.drawable.ic_notification)
                    .setColor(ResourcesCompat.getColor(appContext.resources, R.color.blood_red, null))
                    .setContentIntent(PendingIntent.getActivity(appContext, 0, tapIntent, pendingIntentFlags))
                    .setGroup(NotificationGroups.EXPORT_FINISHED)
                    .build(),
            )
        }
    }

    private object InputKeys {
        const val CLICK_TRACK_ID = "click_track_id"
    }

    private object NotificationGroups {
        const val EXPORTING = "${BuildConfig.APPLICATION_ID}.exporting"
        const val EXPORT_FINISHED = "${BuildConfig.APPLICATION_ID}.export_finished"
    }
}
