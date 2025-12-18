package com.vsevolodganin.clicktrack

import android.app.Application
import android.content.Context
import android.os.StrictMode
import com.vsevolodganin.clicktrack.common.ApplicationBuildConfig
import com.vsevolodganin.clicktrack.di.component.ApplicationComponent
import com.vsevolodganin.clicktrack.theme.ThemeManager
import com.vsevolodganin.clicktrack.utils.cast
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.createGraphFactory

class MainApplication : Application() {

    lateinit var component: ApplicationComponent
        private set

    @Inject
    private lateinit var themeManager: ThemeManager

    @Inject
    private lateinit var applicationBuildConfig: ApplicationBuildConfig

    override fun onCreate() {
        super.onCreate()

        component = createGraphFactory<ApplicationComponent.Factory>()
            .create(this)
            .also {
                it.inject(this)
            }

        if (applicationBuildConfig.isDebug) {
            strictMode()
        }

        themeManager.start()
    }

    private fun strictMode() {
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build(),
        )
        StrictMode.setVmPolicy(
            StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build(),
        )
    }
}

val Context.applicationComponent: ApplicationComponent
    get() = applicationContext.cast<MainApplication>().component
