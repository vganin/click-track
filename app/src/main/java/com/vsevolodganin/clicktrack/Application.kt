package com.vsevolodganin.clicktrack

import com.vsevolodganin.clicktrack.di.component.DaggerApplicationComponent
import com.vsevolodganin.clicktrack.di.module.ComputationDispatcher
import com.vsevolodganin.clicktrack.sounds.AllClickSoundsLoader
import com.vsevolodganin.clicktrack.storage.UserPreferencesRepository
import com.vsevolodganin.clicktrack.theme.ThemeManager
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Application : android.app.Application() {

    val daggerComponent = DaggerApplicationComponent.builder()
        .application(this)
        .build()

    @Inject
    lateinit var userPreferences: UserPreferencesRepository

    @Inject
    lateinit var themeManager: ThemeManager

    @Inject
    lateinit var soundsLoader: AllClickSoundsLoader

    @Inject
    @ComputationDispatcher
    lateinit var computationDispatcher: CoroutineDispatcher

    override fun onCreate() {
        super.onCreate()

        daggerComponent.inject(this)

        themeManager.setTheme(userPreferences.theme)

        GlobalScope.launch(computationDispatcher) {
            soundsLoader.reload()
        }
    }
}
