package com.vsevolodganin.clicktrack.di.component

import com.arkivanov.decompose.ComponentContext
import com.vsevolodganin.clicktrack.MainActivity
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides

@GraphExtension(MainControllerScope::class)
interface MainActivityComponent {

    fun inject(activity: MainActivity)

    @GraphExtension.Factory
    fun interface Factory {
        fun create(
            @Provides activity: MainActivity,
            @Provides componentContext: ComponentContext,
        ): MainActivityComponent
    }
}
