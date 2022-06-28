package com.vsevolodganin.clicktrack.export

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.vsevolodganin.clicktrack.Application
import com.vsevolodganin.clicktrack.BuildConfig
import com.vsevolodganin.clicktrack.IntentFactory
import com.vsevolodganin.clicktrack.R
import com.vsevolodganin.clicktrack.common.MediaStoreAccess
import com.vsevolodganin.clicktrack.model.ClickTrack
import com.vsevolodganin.clicktrack.model.ClickTrackId
import com.vsevolodganin.clicktrack.model.ClickTrackWithDatabaseId
import com.vsevolodganin.clicktrack.notification.NotificationChannels
import com.vsevolodganin.clicktrack.storage.ClickTrackRepository
import com.vsevolodganin.clicktrack.utils.cast
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import kotlin.math.roundToInt

class ExportWorker(private val appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {

    companion object {
        fun createWorkRequest(clickTrackId: ClickTrackId.Database): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<ExportWorker>()
                .setInputData(
                    workDataOf(
                        InputKeys.CLICK_TRACK_ID to clickTrackId.value
                    )
                )
                .build()
        }
    }

    @Inject
    lateinit var workManager: WorkManager

    @Inject
    lateinit var clickTrackRepository: ClickTrackRepository

    @Inject
    lateinit var exportToAudioFile: ExportToAudioFile

    @Inject
    lateinit var mediaStoreAccess: MediaStoreAccess

    @Inject
    lateinit var notificationManager: NotificationManagerCompat

    @Inject
    lateinit var notificationChannels: NotificationChannels

    @Inject
    lateinit var intentFactory: IntentFactory

    // Need unique id to show multiple progress notifications and we can't use string tags
    private val foregroundNotificationId = id.leastSignificantBits.toInt()

    init {
        appContext.cast<Application>().daggerComponent.inject(this)
    }

    override suspend fun doWork(): Result {
        val clickTrackId = inputData.getLong(InputKeys.CLICK_TRACK_ID, -1)
            .takeIf { it >= 0 }
            ?.let(ClickTrackId::Database)
            ?: return Result.failure()

        val clickTrack = clickTrackRepository.getById(clickTrackId).firstOrNull()
            ?: return Result.failure()

        setForeground(foregroundInfo(clickTrack, 0f))

        val temporaryFile = exportToAudioFile.export(
            clickTrack = clickTrack.value,
            onProgress = {
                setForeground(foregroundInfo(clickTrack, it))
            }
        ) ?: return Result.failure()

        val accessUri = mediaStoreAccess.addAudioFile(temporaryFile)
            ?: return Result.failure()

        // Just speeding things up, this file will be deleted by system anyway
        temporaryFile.delete()

        notificationManager.cancel(foregroundNotificationId)
        notifyFinished(clickTrack.value, accessUri)

        return Result.success()
    }

    private fun foregroundInfo(clickTrack: ClickTrackWithDatabaseId, progress: Float): ForegroundInfo {
        val tapIntent = intentFactory.openClickTrack(clickTrack.id)

        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val progressResolution = 100

        return ForegroundInfo(
            foregroundNotificationId,
            NotificationCompat.Builder(appContext, notificationChannels.export)
                .setContentTitle(appContext.getString(R.string.exporting, clickTrack.value.name))
                .setSmallIcon(R.drawable.ic_notification)
                .setOngoing(true)
                .setContentIntent(PendingIntent.getActivity(appContext, 0, tapIntent, pendingIntentFlags))
                .addAction(
                    android.R.drawable.ic_delete,
                    appContext.getString(android.R.string.cancel),
                    workManager.createCancelPendingIntent(id)
                )
                .setGroup(NotificationGroups.EXPORTING)
                .setProgress(progressResolution, (progress * progressResolution).roundToInt(), false)
                .build(),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            } else {
                0
            }
        )
    }

    private fun notifyFinished(clickTrack: ClickTrack, accessUri: Uri) {
        val tapIntent = Intent.createChooser(
            Intent(Intent.ACTION_VIEW).apply {
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
                setDataAndType(accessUri, "audio/*")
            }, null
        )

        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        notificationManager.notify(
            clickTrack.name,
            R.id.notification_export_finished,
            NotificationCompat.Builder(appContext, notificationChannels.export)
                .setContentTitle(appContext.getString(R.string.export_finished, clickTrack.name))
                .setContentText(appContext.getString(R.string.export_open))
                .setSmallIcon(R.drawable.ic_notification)
                .setContentIntent(PendingIntent.getActivity(appContext, 0, tapIntent, pendingIntentFlags))
                .setGroup(NotificationGroups.EXPORT_FINISHED)
                .build()
        )
    }

    private object InputKeys {
        const val CLICK_TRACK_ID = "click_track_id"
    }

    private object NotificationGroups {
        const val EXPORTING = "${BuildConfig.APPLICATION_ID}.exporting"
        const val EXPORT_FINISHED = "${BuildConfig.APPLICATION_ID}.export_finished"
    }
}
