package com.vsevolodganin.clicktrack.notification

import android.app.Application
import androidx.annotation.StringRes
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationManagerCompat
import com.vsevolodganin.clicktrack.R
import com.vsevolodganin.clicktrack.di.component.ApplicationScope
import me.tatarka.inject.annotations.Inject

@ApplicationScope
@Inject
class NotificationChannels(
    private val application: Application,
    private val notificationManager: NotificationManagerCompat
) {
    val playingNow = "playing_now"
    val export = "export"

    init {
        createChannel(
            id = export,
            nameRes = R.string.notification_channel_export,
        )
        createChannel(
            id = playingNow,
            nameRes = R.string.notification_channel_playing_now,
        )
    }

    private fun createChannel(
        id: String,
        @StringRes nameRes: Int,
        importance: Int = NotificationManagerCompat.IMPORTANCE_DEFAULT,
    ) {
        val channel = NotificationChannelCompat.Builder(id, importance)
            .setName(application.getString(nameRes))
            .setVibrationEnabled(false)
            .setSound(null, null)
            .build()
        notificationManager.createNotificationChannel(channel)
    }
}
