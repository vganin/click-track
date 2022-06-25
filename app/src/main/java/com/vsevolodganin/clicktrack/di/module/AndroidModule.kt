package com.vsevolodganin.clicktrack.di.module

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.media.AudioManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.work.WorkManager
import com.vsevolodganin.clicktrack.Application
import com.vsevolodganin.clicktrack.MainActivity
import com.vsevolodganin.clicktrack.di.component.ActivityScoped
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
abstract class ApplicationScopedAndroidModule {

    @Binds
    @Singleton
    abstract fun provideApplicationContext(application: Application): Context

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

@Qualifier
annotation class ActivityContext

@Module
abstract class ActivityScopedAndroidModule {

    @Binds
    @ActivityScoped
    abstract fun provideBaseActivity(activity: MainActivity): Activity

    @Binds
    @ActivityScoped
    abstract fun provideAppCompatActivity(activity: MainActivity): AppCompatActivity

    @Binds
    @ActivityContext
    @ActivityScoped
    abstract fun provideContext(activity: Activity): Context
}
