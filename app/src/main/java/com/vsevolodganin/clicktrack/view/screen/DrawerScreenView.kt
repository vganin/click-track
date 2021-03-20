package com.vsevolodganin.clicktrack.view.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import com.vsevolodganin.clicktrack.redux.Action
import com.vsevolodganin.clicktrack.redux.Dispatch
import com.vsevolodganin.clicktrack.state.DrawerScreenState
import com.vsevolodganin.clicktrack.state.actions.CloseDrawer
import com.vsevolodganin.clicktrack.state.actions.NavigationAction
import com.vsevolodganin.clicktrack.view.icon.ClickTrackIcons
import com.vsevolodganin.clicktrack.view.icon.Metronome

@Composable
fun DrawerScreenView(
    state: DrawerScreenState,
    dispatch: Dispatch = Dispatch {},
) {
    Box(modifier = Modifier.fillMaxHeight()) {
        Column {
            val dispatchClosingDrawer = { action: Action ->
                dispatch(CloseDrawer)
                dispatch(action)
            }

            Spacer(modifier = Modifier.height(12.dp))

            DrawerButton(
                icon = ClickTrackIcons.Metronome,
                label = stringResource(R.string.drawer_item_metronome),
                isSelected = state.selectedItem == DrawerScreenState.SelectedItem.METRONOME,
                action = { dispatchClosingDrawer(NavigationAction.ToMetronomeScreen) }
            )

            DrawerButton(
                icon = Icons.Filled.Settings,
                label = stringResource(R.string.drawer_item_settings),
                isSelected = state.selectedItem == DrawerScreenState.SelectedItem.SETTINGS,
                action = { dispatchClosingDrawer(NavigationAction.ToSettingsScreen) }
            )

            DrawerButton(
                icon = Icons.Filled.LibraryMusic,
                label = stringResource(R.string.drawer_item_sound_library),
                isSelected = state.selectedItem == DrawerScreenState.SelectedItem.SOUND_LIBRARY,
                action = { dispatchClosingDrawer(NavigationAction.ToSoundLibraryScreen) }
            )
        }

        Text(
            text = stringResource(R.string.drawer_version, state.displayVersion),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .alpha(0.7f),
            style = MaterialTheme.typography.caption
        )
    }
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
    val textAndIconColor = if (isSelected) colors.primary else colors.onSurface.copy(alpha = 0.9f)
    val backgroundColor = if (isSelected) colors.primary.copy(alpha = 0.12f) else Color.Transparent

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
                    tint = textAndIconColor,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(16.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.body2,
                    color = textAndIconColor
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
            state = DrawerScreenState(
                isOpened = true,
                gesturesEnabled = true,
                selectedItem = DrawerScreenState.SelectedItem.METRONOME,
                displayVersion = "6.6.6"
            ),
            dispatch = {}
        )
    }
}
