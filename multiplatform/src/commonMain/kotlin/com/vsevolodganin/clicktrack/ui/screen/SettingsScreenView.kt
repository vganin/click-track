package com.vsevolodganin.clicktrack.ui.screen

import ClickTrack.multiplatform.MR
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vsevolodganin.clicktrack.language.AppLanguage
import com.vsevolodganin.clicktrack.settings.SettingsState
import com.vsevolodganin.clicktrack.settings.SettingsViewModel
import com.vsevolodganin.clicktrack.theme.Theme
import com.vsevolodganin.clicktrack.ui.ClickTrackTheme
import com.vsevolodganin.clicktrack.ui.piece.TopAppBarWithBack
import com.vsevolodganin.clicktrack.ui.piece.settings.BooleanChooser
import com.vsevolodganin.clicktrack.ui.piece.settings.ListChooser
import com.vsevolodganin.clicktrack.ui.piece.settings.ListChooserItem
import com.vsevolodganin.clicktrack.utils.compose.navigationBarsPadding
import com.vsevolodganin.clicktrack.utils.platform.isDebug
import com.vsevolodganin.clicktrack.utils.platform.nativeCrash
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun SettingsScreenView(
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            TopAppBarWithBack(
                onBackClick = viewModel::onBackClick,
                title = { Text(stringResource(MR.strings.settings_screen_title)) },
            )
        },
        modifier = modifier,
    ) {
        Content(viewModel)
    }
}

@Composable
private fun Content(viewModel: SettingsViewModel) {
    val state by viewModel.state.collectAsState()
    Column {
        ListChooser(
            title = stringResource(MR.strings.settings_theme),
            value = state.theme.displayValue(),
            variants = Theme.values().map {
                ListChooserItem(
                    value = it, displayValue = it.displayValue(), description = it.description()
                )
            },
            onChoose = viewModel::onThemeChange,
        )

        Divider(modifier = Modifier.padding(start = 16.dp))

        ListChooser(
            title = stringResource(MR.strings.settings_language),
            value = state.language.displayValue(),
            variants = AppLanguage.values().map {
                ListChooserItem(
                    value = it, displayValue = it.displayValue(), description = null
                )
            },
            onChoose = viewModel::onLanguageChange,
        )

        Divider(modifier = Modifier.padding(start = 16.dp))

        BooleanChooser(
            title = stringResource(MR.strings.settings_ignore_audio_focus),
            value = state.ignoreAudioFocus,
            onCheckedChange = viewModel::onIgnoreAudioFocusChange,
            description = stringResource(MR.strings.settings_ignore_audio_focus_description)
        )

        if (isDebug()) {
            Spacer(modifier = Modifier.weight(1f))

            Column(
                modifier = Modifier
                    .navigationBarsPadding()
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(onClick = { throw RuntimeException("Test") }) {
                    Text("Kotlin crash")
                }
                Button(onClick = ::nativeCrash) {
                    Text("Native crash")
                }
            }
        }
    }
}

@Composable
private fun Theme.displayValue(): String = when (this) {
    Theme.LIGHT -> MR.strings.settings_theme_light
    Theme.DARK -> MR.strings.settings_theme_dark
    Theme.SYSTEM -> MR.strings.settings_theme_system
    Theme.AUTO -> MR.strings.settings_theme_auto
}.let { stringResource(it) }

@Composable
private fun Theme.description(): String? = when (this) {
    Theme.LIGHT -> null
    Theme.DARK -> null
    Theme.SYSTEM -> MR.strings.settings_theme_system_description
    Theme.AUTO -> MR.strings.settings_theme_auto_description
}?.let { stringResource(it) }

@Composable
private fun AppLanguage.displayValue(): String = when (this) {
    AppLanguage.SYSTEM -> MR.strings.settings_language_system
    AppLanguage.ENGLISH -> MR.strings.settings_language_system_english
    AppLanguage.RUSSIAN -> MR.strings.settings_language_system_russian
}.let { stringResource(it) }

@ScreenPreview
@Composable
private fun Preview() = ClickTrackTheme {
    SettingsScreenView(
        viewModel = object : SettingsViewModel {
            override val state: StateFlow<SettingsState> = MutableStateFlow(
                SettingsState(
                    theme = Theme.SYSTEM,
                    ignoreAudioFocus = false,
                    language = AppLanguage.SYSTEM,
                )
            )

            override fun onBackClick() = Unit
            override fun onThemeChange(theme: Theme) = Unit
            override fun onLanguageChange(language: AppLanguage) = Unit
            override fun onIgnoreAudioFocusChange(ignoreAudioFocus: Boolean) = Unit
        }
    )
}
