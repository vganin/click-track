package com.vsevolodganin.clicktrack.settings

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.pop
import com.vsevolodganin.clicktrack.ScreenStackNavigation
import com.vsevolodganin.clicktrack.language.AppLanguage
import com.vsevolodganin.clicktrack.language.LanguageStore
import com.vsevolodganin.clicktrack.settings.debug.KotlinCrash
import com.vsevolodganin.clicktrack.settings.debug.NativeCrash
import com.vsevolodganin.clicktrack.storage.UserPreferencesRepository
import com.vsevolodganin.clicktrack.theme.Theme
import com.vsevolodganin.clicktrack.utils.decompose.coroutineScope
import com.vsevolodganin.clicktrack.utils.log.Logger
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class SettingsViewModelImpl(
    @Assisted componentContext: ComponentContext,
    private val navigation: ScreenStackNavigation,
    private val userPreferences: UserPreferencesRepository,
    private val languageStore: LanguageStore,
    private val kotlinCrashProvider: () -> KotlinCrash,
    private val nativeCrashProvider: Lazy<NativeCrash>,
    private val logger: Logger,
) : SettingsViewModel, ComponentContext by componentContext {
    private val scope = coroutineScope()

    override val state: StateFlow<SettingsState> = combine(
        userPreferences.theme.flow,
        userPreferences.ignoreAudioFocus.flow,
        languageStore.appLanguage,
    ) { theme, ignoreAudioFocus, language ->
        SettingsState(
            theme = theme,
            ignoreAudioFocus = ignoreAudioFocus,
            language = language,
        )
    }.stateIn(
        scope = scope,
        started = SharingStarted.Eagerly,
        initialValue = SettingsState(
            theme = userPreferences.theme.value,
            ignoreAudioFocus = userPreferences.ignoreAudioFocus.value,
            language = languageStore.appLanguage.value,
        ),
    )

    override fun onBackClick() = navigation.pop()

    override fun onThemeChange(theme: Theme) {
        userPreferences.theme.value = theme
    }

    override fun onLanguageChange(language: AppLanguage) {
        languageStore.appLanguage.value = language
    }

    override fun onIgnoreAudioFocusChange(ignoreAudioFocus: Boolean) {
        userPreferences.ignoreAudioFocus.value = ignoreAudioFocus
    }

    override fun onKotlinExceptionClick() {
        kotlinCrashProvider()()
    }

    override fun onNativeExceptionCrashClick() {
        nativeCrashProvider.value.exception()
    }

    override fun onNativeDanglingReferenceCrashClick() {
        nativeCrashProvider.value.danglingReference()
    }

    override fun onNonFatalClick() {
        logger.logError("TEST", "This is test non-fatal")
    }
}
