package com.vsevolodganin.clicktrack.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vsevolodganin.clicktrack.R
import com.vsevolodganin.clicktrack.redux.action.SettingsAction
import com.vsevolodganin.clicktrack.redux.core.Dispatch
import com.vsevolodganin.clicktrack.theme.Theme
import com.vsevolodganin.clicktrack.ui.ClickTrackTheme
import com.vsevolodganin.clicktrack.ui.model.SettingsUiState
import com.vsevolodganin.clicktrack.ui.piece.GenericTopBarWithBack
import com.vsevolodganin.clicktrack.ui.piece.settings.BooleanChooser
import com.vsevolodganin.clicktrack.ui.piece.settings.ListChooser
import com.vsevolodganin.clicktrack.ui.piece.settings.ListChooserItem

@Composable
fun SettingsScreenView(
    state: SettingsUiState,
    modifier: Modifier = Modifier,
    dispatch: Dispatch = Dispatch {},
) {
    Scaffold(
        topBar = { GenericTopBarWithBack(R.string.settings, dispatch) },
        modifier = modifier,
    ) {
        Content(state, dispatch)
    }
}

@Composable
private fun Content(
    state: SettingsUiState,
    dispatch: Dispatch,
) {
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
            onChoose = { theme -> dispatch(SettingsAction.ChangeTheme(theme)) },
        )
        Divider(modifier = Modifier.padding(start = 16.dp))
        BooleanChooser(
            title = stringResource(R.string.settings_ignore_audio_focus),
            value = state.ignoreAudioFocus,
            onCheckedChange = { ignoreAudioFocus -> dispatch(SettingsAction.ChangeIgnoreAudioFocus(ignoreAudioFocus)) },
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
        SettingsUiState(
            theme = Theme.SYSTEM,
            ignoreAudioFocus = false,
        )
    )
}
