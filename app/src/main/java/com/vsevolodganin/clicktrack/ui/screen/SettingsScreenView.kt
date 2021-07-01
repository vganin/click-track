package com.vsevolodganin.clicktrack.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.vsevolodganin.clicktrack.R
import com.vsevolodganin.clicktrack.state.redux.action.SettingsAction
import com.vsevolodganin.clicktrack.state.redux.core.Dispatch
import com.vsevolodganin.clicktrack.theme.Theme
import com.vsevolodganin.clicktrack.ui.model.SettingsUiState
import com.vsevolodganin.clicktrack.ui.widget.GenericTopBarWithBack
import com.vsevolodganin.clicktrack.ui.widget.settings.ListChooser
import com.vsevolodganin.clicktrack.ui.widget.settings.ListChooserItem

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
    AnimatedVisibility(
        visibleState = remember { MutableTransitionState(initialState = false).apply { targetState = true } },
        enter = remember { fadeIn() },
        exit = remember { fadeOut() },
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
        }
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

@Preview
@Composable
private fun Preview() {
    SettingsScreenView(
        SettingsUiState(
            theme = Theme.SYSTEM
        )
    )
}
