package com.vsevolodganin.clicktrack.di.module

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import com.vsevolodganin.clicktrack.MainActivity
import com.vsevolodganin.clicktrack.di.component.ActivityScope
import dagger.Binds
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

@Module
interface ActivityModule {

    @Binds
    @ActivityScope
    fun provideBaseActivity(activity: MainActivity): Activity

    @Binds
    @ActivityScope
    fun provideAppCompatActivity(activity: MainActivity): AppCompatActivity

    companion object {
        @Provides
        @ActivityScope
        fun provideActivityCoroutineScope(): CoroutineScope {
            return CoroutineScope(Dispatchers.Main)
        }
    }
}
