package com.vsevolodganin.clicktrack.di.component

import androidx.core.app.NotificationManagerCompat
import androidx.work.WorkManager
import com.vsevolodganin.clicktrack.IntentFactory
import com.vsevolodganin.clicktrack.common.MediaStoreAccess
import com.vsevolodganin.clicktrack.export.ExportToAudioFile
import com.vsevolodganin.clicktrack.notification.NotificationChannels
import com.vsevolodganin.clicktrack.storage.ClickTrackRepository
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Includes

@DependencyGraph
abstract class ExportWorkerComponent(
    @Includes protected val applicationComponent: ApplicationComponent,
) {
    abstract val workManager: WorkManager
    abstract val clickTrackRepository: ClickTrackRepository
    abstract val exportToAudioFile: ExportToAudioFile
    abstract val mediaStoreAccess: MediaStoreAccess
    abstract val notificationManager: NotificationManagerCompat
    abstract val notificationChannels: NotificationChannels
    abstract val intentFactory: IntentFactory
}
