package com.vsevolodganin.clicktrack.di.module

import android.content.ContentResolver
import android.content.Context
import android.media.AudioManager
import androidx.core.app.NotificationManagerCompat
import androidx.work.WorkManager
import com.vsevolodganin.clicktrack.Application
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
interface AndroidModule {

    @Binds
    @Singleton
    fun provideContext(application: Application): Context

    companion object {

        @Provides
        @Singleton
        fun provideAudioManager(context: Context): AudioManager {
            return context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        }

        @Provides
        @Singleton
        fun provideContentResolver(context: Context): ContentResolver {
            return context.contentResolver
        }

        @Provides
        @Singleton
        fun provideWorkManager(context: Context): WorkManager {
            return WorkManager.getInstance(context)
        }

        @Provides
        @Singleton
        fun provideNotificationManager(context: Context): NotificationManagerCompat {
            return NotificationManagerCompat.from(context)
        }
    }
}
