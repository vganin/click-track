package com.vsevolodganin.clicktrack

import com.vsevolodganin.clicktrack.di.component.DaggerApplicationComponent
import com.vsevolodganin.clicktrack.sounds.AllClickSoundsPreloader
import com.vsevolodganin.clicktrack.storage.UserPreferencesRepository
import com.vsevolodganin.clicktrack.storage.blockingValue
import com.vsevolodganin.clicktrack.theme.ThemeManager
import javax.inject.Inject
import timber.log.Timber

class Application : android.app.Application() {

    val daggerComponent = DaggerApplicationComponent.builder()
        .application(this)
        .build()

    @Inject
    lateinit var userPreferences: UserPreferencesRepository

    @Inject
    lateinit var themeManager: ThemeManager

    @Inject
    lateinit var soundsPreloader: AllClickSoundsPreloader

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        daggerComponent.inject(this)

        themeManager.setTheme(userPreferences.theme.blockingValue)

        soundsPreloader.preload()
    }
}
