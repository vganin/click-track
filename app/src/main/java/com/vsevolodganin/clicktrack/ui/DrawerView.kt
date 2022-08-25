package com.vsevolodganin.clicktrack.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContactSupport
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vsevolodganin.clicktrack.R
import com.vsevolodganin.clicktrack.drawer.DrawerState
import com.vsevolodganin.clicktrack.drawer.DrawerViewModel
import com.vsevolodganin.clicktrack.icons.ClickTrackIcons
import com.vsevolodganin.clicktrack.icons.clicktrackicons.Metronome
import com.vsevolodganin.clicktrack.icons.clicktrackicons.Polyrhythm
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun DrawerView(viewModel: DrawerViewModel) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .systemBarsPadding()
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        DrawerButton(
            icon = ClickTrackIcons.Metronome,
            label = stringResource(R.string.drawer_item_metronome),
            isSelected = state.selectedItem == DrawerState.SelectedItem.METRONOME,
            action = viewModel::navigateToMetronome
        )

        DrawerButton(
            icon = Icons.Filled.FitnessCenter,
            label = stringResource(R.string.drawer_item_training),
            isSelected = state.selectedItem == DrawerState.SelectedItem.TRAINING,
            action = viewModel::navigateToTraining
        )

        DrawerButton(
            icon = ClickTrackIcons.Polyrhythm,
            label = stringResource(R.string.drawer_item_polyrhythm),
            isSelected = state.selectedItem == DrawerState.SelectedItem.POLYRHYTHMS,
            action = viewModel::navigateToPolyrhythms
        )

        DrawerButton(
            icon = Icons.Filled.LibraryMusic,
            label = stringResource(R.string.drawer_item_sound_library),
            isSelected = state.selectedItem == DrawerState.SelectedItem.SOUND_LIBRARY,
            action = viewModel::navigateToSoundLibrary
        )

        Spacer(modifier = Modifier.weight(1f))

        DrawerButton(
            icon = Icons.Filled.Settings,
            label = stringResource(R.string.drawer_item_settings),
            isSelected = state.selectedItem == DrawerState.SelectedItem.SETTINGS,
            action = viewModel::navigateToSettings
        )

        DrawerButton(
            icon = Icons.Filled.ContactSupport,
            label = stringResource(R.string.drawer_item_about),
            isSelected = state.selectedItem == DrawerState.SelectedItem.ABOUT,
            action = viewModel::navigateToAbout
        )

        Spacer(modifier = Modifier.height(16.dp))
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
    val textAndIconColor = if (isSelected) colors.secondary else colors.onSurface.copy(alpha = 0.9f)
    val backgroundColor = if (isSelected) colors.secondary.copy(alpha = 0.12f) else Color.Transparent

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
private fun Preview() = ClickTrackTheme {
    DrawerView(
        viewModel = object : DrawerViewModel {
            override val state: StateFlow<DrawerState> = MutableStateFlow(
                DrawerState(
                    isOpened = true,
                    selectedItem = DrawerState.SelectedItem.METRONOME,
                )
            )

            override fun openDrawer() = Unit
            override fun closeDrawer() = Unit
            override fun navigateToMetronome() = Unit
            override fun navigateToTraining() = Unit
            override fun navigateToPolyrhythms() = Unit
            override fun navigateToSoundLibrary() = Unit
            override fun navigateToSettings() = Unit
            override fun navigateToAbout() = Unit
        }
    )
}
