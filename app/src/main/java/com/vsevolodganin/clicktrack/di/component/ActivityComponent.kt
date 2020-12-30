package com.vsevolodganin.clicktrack.di.component

import android.app.Activity
import com.vsevolodganin.clicktrack.MainActivity
import com.vsevolodganin.clicktrack.di.module.ActivityScopedAndroidModule
import com.vsevolodganin.clicktrack.di.module.ActivityScopedAppStateEpicModule
import dagger.BindsInstance
import dagger.Subcomponent
import javax.inject.Scope

@Scope
annotation class ActivityScoped

@ActivityScoped
@Subcomponent(
    modules = [
        ActivityScopedAppStateEpicModule::class,
        ActivityScopedAndroidModule::class
    ]
)
interface ActivityComponent {
    fun inject(mainActivity: MainActivity)

    @Subcomponent.Builder
    interface Builder {
        @BindsInstance
        fun activity(activity: Activity): Builder

        fun build(): ActivityComponent
    }
}
