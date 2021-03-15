package com.vsevolodganin.clicktrack.view.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vsevolodganin.clicktrack.R
import com.vsevolodganin.clicktrack.lib.bpm
import com.vsevolodganin.clicktrack.redux.Action
import com.vsevolodganin.clicktrack.redux.Dispatch
import com.vsevolodganin.clicktrack.state.MetronomeScreenState
import com.vsevolodganin.clicktrack.state.Screen
import com.vsevolodganin.clicktrack.state.actions.CloseDrawer
import com.vsevolodganin.clicktrack.state.actions.NavigationAction
import com.vsevolodganin.clicktrack.view.icon.ClickTrackIcons
import com.vsevolodganin.clicktrack.view.icon.Metronome

@Composable
fun DrawerScreenView(
    currentScreen: Screen,
    dispatch: Dispatch = Dispatch {},
) {
    val dispatchClosingDrawer = { action: Action ->
        dispatch(CloseDrawer)
        dispatch(action)
    }

    DrawerButton(
        icon = ClickTrackIcons.Metronome,
        label = stringResource(R.string.drawer_item_metronome),
        isSelected = currentScreen is Screen.Metronome,
        action = { dispatchClosingDrawer(NavigationAction.ToMetronomeScreen) }
    )

    DrawerButton(
        icon = Icons.Filled.Settings,
        label = stringResource(R.string.drawer_item_settings),
        isSelected = currentScreen is Screen.Settings,
        action = { dispatchClosingDrawer(NavigationAction.ToSettingsScreen) }
    )

    DrawerButton(
        icon = Icons.Filled.LibraryMusic,
        label = stringResource(R.string.drawer_item_sound_library),
        isSelected = currentScreen is Screen.Settings,
        action = { dispatchClosingDrawer(NavigationAction.ToSoundLibraryScreen) }
    )
}

@Composable
private fun DrawerButton(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    action: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colors
    val imageAlpha = if (isSelected) {
        1f
    } else {
        0.6f
    }
    val textIconColor = if (isSelected) {
        colors.primary
    } else {
        colors.onSurface.copy(alpha = 0.6f)
    }
    val backgroundColor = if (isSelected) {
        colors.primary.copy(alpha = 0.12f)
    } else {
        Color.Transparent
    }

    Surface(
        modifier = modifier
            .padding(start = 8.dp, top = 4.dp, end = 8.dp)
            .fillMaxWidth()
            .height(48.dp),
        color = backgroundColor,
        shape = MaterialTheme.shapes.small
    ) {
        TextButton(
            onClick = action,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null, // decorative
                    tint = textIconColor,
                    modifier = Modifier
                        .size(24.dp)
                        .alpha(imageAlpha)
                )
                Spacer(Modifier.width(16.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.body2,
                    color = textIconColor
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    Column {
        DrawerScreenView(
            currentScreen = Screen.Metronome(MetronomeScreenState(120.bpm, 0.0, false))
        )
    }
}
