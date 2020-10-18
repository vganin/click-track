package net.ganin.vsevolod.clicktrack.di.component

import android.app.Activity
import dagger.BindsInstance
import dagger.Subcomponent
import net.ganin.vsevolod.clicktrack.MainActivity
import net.ganin.vsevolod.clicktrack.di.module.ActivityScopedAndroidModule
import net.ganin.vsevolod.clicktrack.di.module.ActivityScopedAppStateEpicModule
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
