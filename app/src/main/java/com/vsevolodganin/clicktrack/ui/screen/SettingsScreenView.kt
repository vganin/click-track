package com.vsevolodganin.clicktrack.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vsevolodganin.clicktrack.R
import com.vsevolodganin.clicktrack.settings.SettingsState
import com.vsevolodganin.clicktrack.settings.SettingsViewModel
import com.vsevolodganin.clicktrack.theme.Theme
import com.vsevolodganin.clicktrack.ui.ClickTrackTheme
import com.vsevolodganin.clicktrack.ui.piece.TopAppBarWithBack
import com.vsevolodganin.clicktrack.ui.piece.settings.BooleanChooser
import com.vsevolodganin.clicktrack.ui.piece.settings.ListChooser
import com.vsevolodganin.clicktrack.ui.piece.settings.ListChooserItem
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
                title = { Text(stringResource(R.string.settings)) },
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
            title = stringResource(R.string.settings_theme),
            value = state.theme.displayValue(),
            variants = Theme.values().map {
                ListChooserItem(
                    value = it,
                    displayValue = it.displayValue(),
                    description = it.description()
                )
            },
            onChoose = viewModel::onThemeChange,
        )
        Divider(modifier = Modifier.padding(start = 16.dp))
        BooleanChooser(
            title = stringResource(R.string.settings_ignore_audio_focus),
            value = state.ignoreAudioFocus,
            onCheckedChange = viewModel::onIgnoreAudioFocusChange,
            description = stringResource(R.string.settings_ignore_audio_focus_description)
        )
    }
}

@Composable
private fun Theme.displayValue(): String = when (this) {
    Theme.LIGHT -> R.string.settings_theme_light
    Theme.DARK -> R.string.settings_theme_dark
    Theme.SYSTEM -> R.string.settings_theme_system
    Theme.AUTO -> R.string.settings_theme_auto
}.let { stringResource(it) }

@Composable
private fun Theme.description(): String? = when (this) {
    Theme.LIGHT -> null
    Theme.DARK -> null
    Theme.SYSTEM -> R.string.settings_theme_system_description
    Theme.AUTO -> R.string.settings_theme_auto_description
}?.let { stringResource(it) }

@ScreenPreviews
@Composable
private fun Preview() = ClickTrackTheme {
    SettingsScreenView(
        viewModel = object : SettingsViewModel {
            override val state: StateFlow<SettingsState> = MutableStateFlow(
                SettingsState(
                    theme = Theme.SYSTEM,
                    ignoreAudioFocus = false,
                )
            )

            override fun onBackClick() = Unit
            override fun onThemeChange(theme: Theme) = Unit
            override fun onIgnoreAudioFocusChange(ignoreAudioFocus: Boolean) = Unit
        }

    )
}
