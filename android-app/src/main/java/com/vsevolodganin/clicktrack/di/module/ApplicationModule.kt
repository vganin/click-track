package com.vsevolodganin.clicktrack.di.module

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.media.AudioManager
import androidx.core.app.NotificationManagerCompat
import androidx.work.WorkManager
import com.vsevolodganin.clicktrack.di.component.ApplicationScope
import dev.zacsweers.metro.Provides

interface ApplicationModule {
    @Provides
    @ApplicationScope
    fun provideAudioManager(application: Application): AudioManager {
        return application.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    @Provides
    @ApplicationScope
    fun provideContentResolver(application: Application): ContentResolver {
        return application.contentResolver
    }

    @Provides
    @ApplicationScope
    fun provideWorkManager(application: Application): WorkManager {
        return WorkManager.getInstance(application)
    }

    @Provides
    @ApplicationScope
    fun provideNotificationManager(application: Application): NotificationManagerCompat {
        return NotificationManagerCompat.from(application)
    }
}
