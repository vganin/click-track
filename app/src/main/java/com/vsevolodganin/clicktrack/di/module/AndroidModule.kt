package com.vsevolodganin.clicktrack.di.module

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.SharedPreferences
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

@Qualifier
annotation class ApplicationContext

@Qualifier
annotation class UserPreferences

@Module
abstract class ApplicationScopedAndroidModule {

    @Binds
    @ApplicationContext
    @ApplicationScoped
    abstract fun provideContext(application: Application): Context

    companion object {

        @Provides
        @ApplicationScoped
        fun provideAudioManager(@ApplicationContext context: Context): AudioManager {
            return context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        }

        @Provides
        @UserPreferences
        @ApplicationScoped
        fun provideUserPreferences(@ApplicationContext context: Context): SharedPreferences {
            return context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
        }

        @Provides
        @ApplicationScoped
        fun provideContentResolver(@ApplicationContext context: Context): ContentResolver {
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
