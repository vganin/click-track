package com.vsevolodganin.clicktrack

import com.vsevolodganin.clicktrack.di.component.DaggerApplicationComponent
import com.vsevolodganin.clicktrack.storage.UserPreferencesRepository
import com.vsevolodganin.clicktrack.theme.ThemeManager
import javax.inject.Inject

class Application : android.app.Application() {

    val daggerComponent = DaggerApplicationComponent.builder()
        .application(this)
        .build()

    @Inject
    lateinit var userPreferences: UserPreferencesRepository

    @Inject
    lateinit var themeManager: ThemeManager

    override fun onCreate() {
        super.onCreate()

        daggerComponent.inject(this)

        themeManager.setTheme(userPreferences.theme)
    }
}
