package com.vsevolodganin.clicktrack.di.module

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.vsevolodganin.clicktrack.Application
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

    @Module
    companion object {

        @JvmStatic
        @Provides
        @UserPreferences
        @ApplicationScoped
        fun provideUserPreferences(@ApplicationContext context: Context): SharedPreferences {
            return context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
        }
    }
}

@Qualifier
annotation class ActivityContext

@Module
abstract class ActivityScopedAndroidModule {

    @Binds
    @ApplicationContext
    @ActivityScoped
    abstract fun provideContext(activity: Activity): Context
}
