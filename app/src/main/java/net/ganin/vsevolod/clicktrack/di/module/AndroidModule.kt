package net.ganin.vsevolod.clicktrack.di.module

import android.app.Activity
import android.content.Context
import dagger.Binds
import dagger.Module
import net.ganin.vsevolod.clicktrack.Application
import net.ganin.vsevolod.clicktrack.di.component.ActivityScoped
import net.ganin.vsevolod.clicktrack.di.component.ApplicationScoped
import javax.inject.Qualifier

@Qualifier
annotation class ApplicationContext

@Module
abstract class ApplicationScopedAndroidModule {

    @Binds
    @ApplicationContext
    @ApplicationScoped
    abstract fun provideContext(application: Application): Context
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
