package com.vsevolodganin.clicktrack.language

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.arkivanov.essenty.lifecycle.LifecycleOwner
import com.arkivanov.essenty.lifecycle.doOnResume
import com.vsevolodganin.clicktrack.di.component.MainControllerScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@MainControllerScope
@Inject
class LanguageStoreImpl(lifecycleOwner: LifecycleOwner) : LanguageStore {

    override val appLanguage: MutableStateFlow<AppLanguage> = MutableStateFlow(storedAppLanguage)

    init {
        GlobalScope.launch(context = Dispatchers.Unconfined, start = CoroutineStart.UNDISPATCHED) {
            appLanguage.collect {
                storedAppLanguage = it
            }
        }

        lifecycleOwner.lifecycle.doOnResume {
            appLanguage.value = storedAppLanguage
        }
    }

    private var storedAppLanguage: AppLanguage
        set(value) {
            val appLocale = when (value) {
                AppLanguage.SYSTEM -> LocaleListCompat.getEmptyLocaleList()
                AppLanguage.ENGLISH -> LocaleListCompat.create(LOCALE_EN)
                AppLanguage.RUSSIAN -> LocaleListCompat.create(LOCALE_RU)
            }
            AppCompatDelegate.setApplicationLocales(appLocale)
        }
        get() {
            return when (AppCompatDelegate.getApplicationLocales().get(0)?.language) {
                "en" -> AppLanguage.ENGLISH
                "ru" -> AppLanguage.RUSSIAN
                else -> AppLanguage.SYSTEM
            }
        }
}
