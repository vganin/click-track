package com.vsevolodganin.clicktrack

import android.app.Application
import android.content.Context
import android.os.StrictMode
import com.vsevolodganin.clicktrack.di.component.ApplicationComponent
import com.vsevolodganin.clicktrack.utils.cast
import dev.zacsweers.metro.createGraphFactory

class MainApplication : Application() {
    lateinit var component: ApplicationComponent
        private set

    override fun onCreate() {
        super.onCreate()

        component = createGraphFactory<ApplicationComponent.Factory>().create(this)

        if (BuildConfig.DEBUG) {
            strictMode()
        }

        component.apply {
            nativeLibraries.init()
            themeManager.start()
        }
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
