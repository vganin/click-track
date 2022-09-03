package com.vsevolodganin.clicktrack

import android.app.Application
import android.os.StrictMode
import com.vsevolodganin.clicktrack.audio.SoundPreloader
import com.vsevolodganin.clicktrack.di.component.DaggerApplicationComponent
import com.vsevolodganin.clicktrack.storage.UserPreferencesRepository
import com.vsevolodganin.clicktrack.theme.ThemeManager
import timber.log.Timber
import javax.inject.Inject

class MainApplication : Application() {

    val daggerComponent = DaggerApplicationComponent.builder()
        .application(this)
        .build()

    @Inject
    lateinit var userPreferences: UserPreferencesRepository

    @Inject
    lateinit var themeManager: ThemeManager

    @Inject
    lateinit var soundsPreloader: SoundPreloader

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            strictMode()
        }

        NativeLibraries.init(this)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        daggerComponent.inject(this)

        themeManager.start()

        soundsPreloader.preload()
    }

    private fun strictMode() {
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build()
        )
        StrictMode.setVmPolicy(
            StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build()
        )
    }
}
