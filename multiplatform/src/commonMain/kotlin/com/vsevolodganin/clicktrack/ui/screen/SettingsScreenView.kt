package com.vsevolodganin.clicktrack.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import clicktrack.multiplatform.generated.resources.Res
import clicktrack.multiplatform.generated.resources.settings_ignore_audio_focus
import clicktrack.multiplatform.generated.resources.settings_ignore_audio_focus_description
import clicktrack.multiplatform.generated.resources.settings_language
import clicktrack.multiplatform.generated.resources.settings_language_system
import clicktrack.multiplatform.generated.resources.settings_language_system_english
import clicktrack.multiplatform.generated.resources.settings_language_system_russian
import clicktrack.multiplatform.generated.resources.settings_screen_title
import clicktrack.multiplatform.generated.resources.settings_theme
import clicktrack.multiplatform.generated.resources.settings_theme_auto
import clicktrack.multiplatform.generated.resources.settings_theme_auto_description
import clicktrack.multiplatform.generated.resources.settings_theme_dark
import clicktrack.multiplatform.generated.resources.settings_theme_light
import clicktrack.multiplatform.generated.resources.settings_theme_system
import clicktrack.multiplatform.generated.resources.settings_theme_system_description
import com.vsevolodganin.clicktrack.language.AppLanguage
import com.vsevolodganin.clicktrack.settings.SettingsState
import com.vsevolodganin.clicktrack.settings.SettingsViewModel
import com.vsevolodganin.clicktrack.theme.Theme
import com.vsevolodganin.clicktrack.ui.piece.DarkTopAppBarWithBack
import com.vsevolodganin.clicktrack.ui.piece.settings.BooleanChooser
import com.vsevolodganin.clicktrack.ui.piece.settings.ListChooser
import com.vsevolodganin.clicktrack.ui.piece.settings.ListChooserItem
import com.vsevolodganin.clicktrack.ui.theme.ClickTrackTheme
import com.vsevolodganin.clicktrack.utils.platform.isDebug
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SettingsScreenView(viewModel: SettingsViewModel, modifier: Modifier = Modifier) {
    Scaffold(
        topBar = {
            DarkTopAppBarWithBack(
                onBackClick = viewModel::onBackClick,
                title = { Text(stringResource(Res.string.settings_screen_title)) },
            )
        },
        modifier = modifier,
    ) { paddingValues ->
        Content(
            viewModel = viewModel,
            modifier = Modifier.padding(paddingValues),
        )
    }
}

@Composable
private fun Content(
    viewModel: SettingsViewModel,
    modifier: Modifier,
) {
    val state by viewModel.state.collectAsState()

    Column(modifier = modifier) {
        ListChooser(
            title = stringResource(Res.string.settings_theme),
            value = state.theme.displayValue(),
            variants = Theme.entries.map {
                ListChooserItem(
                    value = it,
                    displayValue = it.displayValue(),
                    description = it.description(),
                )
            },
            onChoose = viewModel::onThemeChange,
        )

        HorizontalDivider(modifier = Modifier.padding(start = 16.dp))

        ListChooser(
            title = stringResource(Res.string.settings_language),
            value = state.language.displayValue(),
            variants = AppLanguage.entries.map {
                ListChooserItem(
                    value = it,
                    displayValue = it.displayValue(),
                    description = null,
                )
            },
            onChoose = viewModel::onLanguageChange,
        )

        HorizontalDivider(modifier = Modifier.padding(start = 16.dp))

        BooleanChooser(
            title = stringResource(Res.string.settings_ignore_audio_focus),
            value = state.ignoreAudioFocus,
            onCheckedChange = viewModel::onIgnoreAudioFocusChange,
            description = stringResource(Res.string.settings_ignore_audio_focus_description),
        )

        if (isDebug()) {
            Spacer(modifier = Modifier.weight(1f))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Button(onClick = viewModel::onKotlinExceptionClick) {
                    Text("Kotlin exception crash")
                }
                Button(onClick = viewModel::onNativeExceptionCrashClick) {
                    Text("Native exception crash")
                }
                Button(onClick = viewModel::onNativeDanglingReferenceCrashClick) {
                    Text("Native dangling reference crash")
                }
                Button(onClick = viewModel::onNonFatalClick) {
                    Text("Non-fatal")
                }
            }
        }
    }
}

@Composable
private fun Theme.displayValue(): String = when (this) {
    Theme.LIGHT -> Res.string.settings_theme_light
    Theme.DARK -> Res.string.settings_theme_dark
    Theme.SYSTEM -> Res.string.settings_theme_system
    Theme.AUTO -> Res.string.settings_theme_auto
}.let { stringResource(it) }

@Composable
private fun Theme.description(): String? = when (this) {
    Theme.LIGHT -> null
    Theme.DARK -> null
    Theme.SYSTEM -> Res.string.settings_theme_system_description
    Theme.AUTO -> Res.string.settings_theme_auto_description
}?.let { stringResource(it) }

@Composable
private fun AppLanguage.displayValue(): String = when (this) {
    AppLanguage.SYSTEM -> Res.string.settings_language_system
    AppLanguage.ENGLISH -> Res.string.settings_language_system_english
    AppLanguage.RUSSIAN -> Res.string.settings_language_system_russian
}.let { stringResource(it) }

@Preview
@Composable
internal fun SettingsScreenPreview() = ClickTrackTheme {
    SettingsScreenView(
        viewModel = object : SettingsViewModel {
            override val state: StateFlow<SettingsState> = MutableStateFlow(
                SettingsState(
                    theme = Theme.SYSTEM,
                    ignoreAudioFocus = false,
                    language = AppLanguage.SYSTEM,
                ),
            )

            override fun onBackClick() = Unit

            override fun onThemeChange(theme: Theme) = Unit

            override fun onLanguageChange(language: AppLanguage) = Unit

            override fun onIgnoreAudioFocusChange(ignoreAudioFocus: Boolean) = Unit

            override fun onKotlinExceptionClick() = Unit

            override fun onNativeExceptionCrashClick() = Unit

            override fun onNativeDanglingReferenceCrashClick() = Unit

            override fun onNonFatalClick() = Unit
        },
    )
}
