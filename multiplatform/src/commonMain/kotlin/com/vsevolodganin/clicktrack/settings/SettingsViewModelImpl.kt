package com.vsevolodganin.clicktrack.settings

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.pop
import com.vsevolodganin.clicktrack.ScreenStackNavigation
import com.vsevolodganin.clicktrack.common.ApplicationBuildConfig
import com.vsevolodganin.clicktrack.language.AppLanguage
import com.vsevolodganin.clicktrack.language.LanguageStore
import com.vsevolodganin.clicktrack.settings.debug.KotlinCrash
import com.vsevolodganin.clicktrack.storage.UserPreferencesRepository
import com.vsevolodganin.clicktrack.theme.Theme
import com.vsevolodganin.clicktrack.utils.decompose.coroutineScope
import com.vsevolodganin.clicktrack.utils.log.Logger
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.Provider
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

@AssistedInject
class SettingsViewModelImpl(
    @Assisted componentContext: ComponentContext,
    private val navigation: ScreenStackNavigation,
    private val userPreferences: UserPreferencesRepository,
    private val languageStore: LanguageStore,
    private val kotlinCrashProvider: Provider<KotlinCrash>,
    private val logger: Logger,
    private val applicationBuildConfig: ApplicationBuildConfig,
) : SettingsViewModel, ComponentContext by componentContext {

    @AssistedFactory
    fun interface Factory {
        fun create(
            componentContext: ComponentContext,
        ): SettingsViewModelImpl
    }

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
            showCrashSimulationButtons = applicationBuildConfig.isDebug,
        )
    }.stateIn(
        scope = scope,
        started = SharingStarted.Eagerly,
        initialValue = SettingsState(
            theme = userPreferences.theme.value,
            ignoreAudioFocus = userPreferences.ignoreAudioFocus.value,
            language = languageStore.appLanguage.value,
            showCrashSimulationButtons = applicationBuildConfig.isDebug,
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

    override fun onNonFatalClick() {
        logger.logError("TEST", "This is test non-fatal")
    }
}
