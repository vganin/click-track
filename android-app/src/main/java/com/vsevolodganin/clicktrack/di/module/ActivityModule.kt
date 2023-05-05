package com.vsevolodganin.clicktrack.di.module

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import com.vsevolodganin.clicktrack.MainActivity
import com.vsevolodganin.clicktrack.di.component.MainControllerScope
import me.tatarka.inject.annotations.Provides

interface ActivityModule {

    @Provides
    @MainControllerScope
    fun provideBaseActivity(activity: MainActivity): Activity = activity

    @Provides
    @MainControllerScope
    fun provideAppCompatActivity(activity: MainActivity): AppCompatActivity = activity
}
