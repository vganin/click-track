package com.vsevolodganin.clicktrack.di.module

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.media.AudioManager
import androidx.appcompat.app.AppCompatActivity
import com.vsevolodganin.clicktrack.Application
import com.vsevolodganin.clicktrack.MainActivity
import com.vsevolodganin.clicktrack.di.component.ActivityScoped
import com.vsevolodganin.clicktrack.di.component.ApplicationScoped
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Qualifier

@Module
abstract class ApplicationScopedAndroidModule {

    @Binds
    @ApplicationScoped
    abstract fun provideApplicationContext(application: Application): Context

    companion object {

        @Provides
        @ApplicationScoped
        fun provideAudioManager(context: Context): AudioManager {
            return context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        }

        @Provides
        @ApplicationScoped
        fun provideContentResolver(context: Context): ContentResolver {
            return context.contentResolver
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
