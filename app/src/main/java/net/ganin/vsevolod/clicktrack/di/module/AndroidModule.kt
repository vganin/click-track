package net.ganin.vsevolod.clicktrack.di.module

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import dagger.Binds
import dagger.Module
import dagger.Provides
import net.ganin.vsevolod.clicktrack.Application
import net.ganin.vsevolod.clicktrack.di.component.ActivityScoped
import net.ganin.vsevolod.clicktrack.di.component.ApplicationScoped
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
