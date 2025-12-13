package com.vsevolodganin.clicktrack.di.module

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.media.AudioManager
import androidx.core.app.NotificationManagerCompat
import androidx.work.WorkManager
import com.vsevolodganin.clicktrack.di.component.ApplicationScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@ContributesTo(ApplicationScope::class)
@BindingContainer
object ApplicationModule {

    @Provides
    @SingleIn(ApplicationScope::class)
    fun provideAudioManager(application: Application): AudioManager {
        return application.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    @Provides
    @SingleIn(ApplicationScope::class)
    fun provideContentResolver(application: Application): ContentResolver {
        return application.contentResolver
    }

    @Provides
    @SingleIn(ApplicationScope::class)
    fun provideWorkManager(application: Application): WorkManager {
        return WorkManager.getInstance(application)
    }

    @Provides
    @SingleIn(ApplicationScope::class)
    fun provideNotificationManager(application: Application): NotificationManagerCompat {
        return NotificationManagerCompat.from(application)
    }
}
