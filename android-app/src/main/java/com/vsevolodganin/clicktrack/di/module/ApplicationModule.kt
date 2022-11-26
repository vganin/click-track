package com.vsevolodganin.clicktrack.di.module

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.media.AudioManager
import androidx.core.app.NotificationManagerCompat
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object ApplicationModule {

    @Provides
    @Singleton
    fun provideAudioManager(application: Application): AudioManager {
        return application.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    @Provides
    @Singleton
    fun provideContentResolver(application: Application): ContentResolver {
        return application.contentResolver
    }

    @Provides
    @Singleton
    fun provideWorkManager(application: Application): WorkManager {
        return WorkManager.getInstance(application)
    }

    @Provides
    @Singleton
    fun provideNotificationManager(application: Application): NotificationManagerCompat {
        return NotificationManagerCompat.from(application)
    }
}
