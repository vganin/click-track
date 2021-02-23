package com.vsevolodganin.clicktrack.view.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.vsevolodganin.clicktrack.R
import com.vsevolodganin.clicktrack.redux.Dispatch
import com.vsevolodganin.clicktrack.state.SettingsScreenState
import com.vsevolodganin.clicktrack.state.actions.NavigateBack
import com.vsevolodganin.clicktrack.state.actions.SettingsActions
import com.vsevolodganin.clicktrack.theme.Theme
import com.vsevolodganin.clicktrack.view.widget.settings.ListChooser
import com.vsevolodganin.clicktrack.view.widget.settings.ListChooserItem

@Composable
fun SettingsScreenView(
    state: SettingsScreenState?,
    modifier: Modifier = Modifier,
    dispatch: Dispatch = Dispatch {},
) {
    Scaffold(
        topBar = { SettingsScreenViewTopBar(dispatch) },
        modifier = modifier,
    ) {
        if (state != null) {
            SettingsScreenViewContent(state, dispatch)
        }
    }
}

@Composable
private fun SettingsScreenViewTopBar(dispatch: Dispatch) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = { dispatch(NavigateBack) }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
            }
        },
        title = { Text(text = stringResource(id = R.string.settings)) }
    )
}

@Composable
private fun SettingsScreenViewContent(
    state: SettingsScreenState,
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
            onChoose = { theme -> dispatch(SettingsActions.ChangeTheme(theme)) },
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
