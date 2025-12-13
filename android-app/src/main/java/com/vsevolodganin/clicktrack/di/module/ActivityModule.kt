package com.vsevolodganin.clicktrack.di.module

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import com.vsevolodganin.clicktrack.MainActivity
import com.vsevolodganin.clicktrack.di.component.MainControllerScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo

@ContributesTo(MainControllerScope::class)
@BindingContainer
interface ActivityModule {

    @Binds
    fun provideBaseActivity(activity: MainActivity): Activity

    @Binds
    fun provideAppCompatActivity(activity: MainActivity): AppCompatActivity
}
