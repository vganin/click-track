package com.vsevolodganin.clicktrack.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ContactSupport
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import clicktrack.multiplatform.generated.resources.Res
import clicktrack.multiplatform.generated.resources.drawer_about
import clicktrack.multiplatform.generated.resources.drawer_metronome
import clicktrack.multiplatform.generated.resources.drawer_polyrhythms
import clicktrack.multiplatform.generated.resources.drawer_settings
import clicktrack.multiplatform.generated.resources.drawer_sound_library
import clicktrack.multiplatform.generated.resources.drawer_training
import clicktrack.multiplatform.generated.resources.metronome
import clicktrack.multiplatform.generated.resources.polyrhythm
import com.vsevolodganin.clicktrack.drawer.DrawerState
import com.vsevolodganin.clicktrack.drawer.DrawerViewModel
import com.vsevolodganin.clicktrack.ui.theme.ClickTrackTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun DrawerView(viewModel: DrawerViewModel) {
    val topInset = DrawerDefaults.windowInsets.only(WindowInsetsSides.Top)
    ModalDrawerSheet(
        windowInsets = DrawerDefaults.windowInsets.exclude(topInset),
    ) {
        val state by viewModel.state.collectAsState()

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsTopHeight(topInset)
                .consumeWindowInsets(topInset)
                .background(
                    if (isSystemInDarkTheme()) {
                        Color.Transparent
                    } else {
                        Color.Black.copy(alpha = 0.25f)
                    },
                ),
        )

        Spacer(modifier = Modifier.height(12.dp))

        DrawerButton(
            icon = painterResource(Res.drawable.metronome),
            label = stringResource(Res.string.drawer_metronome),
            isSelected = state.selectedItem == DrawerState.SelectedItem.METRONOME,
            action = viewModel::navigateToMetronome,
        )

        DrawerButton(
            icon = rememberVectorPainter(Icons.Filled.FitnessCenter),
            label = stringResource(Res.string.drawer_training),
            isSelected = state.selectedItem == DrawerState.SelectedItem.TRAINING,
            action = viewModel::navigateToTraining,
        )

        DrawerButton(
            icon = painterResource(Res.drawable.polyrhythm),
            label = stringResource(Res.string.drawer_polyrhythms),
            isSelected = state.selectedItem == DrawerState.SelectedItem.POLYRHYTHMS,
            action = viewModel::navigateToPolyrhythms,
        )

        DrawerButton(
            icon = rememberVectorPainter(Icons.Filled.LibraryMusic),
            label = stringResource(Res.string.drawer_sound_library),
            isSelected = state.selectedItem == DrawerState.SelectedItem.SOUND_LIBRARY,
            action = viewModel::navigateToSoundLibrary,
        )

        Spacer(modifier = Modifier.weight(1f))

        DrawerButton(
            icon = rememberVectorPainter(Icons.Filled.Settings),
            label = stringResource(Res.string.drawer_settings),
            isSelected = state.selectedItem == DrawerState.SelectedItem.SETTINGS,
            action = viewModel::navigateToSettings,
        )

        DrawerButton(
            icon = rememberVectorPainter(Icons.AutoMirrored.Filled.ContactSupport),
            label = stringResource(Res.string.drawer_about),
            isSelected = state.selectedItem == DrawerState.SelectedItem.ABOUT,
            action = viewModel::navigateToAbout,
        )

        Spacer(modifier = Modifier.height(4.dp))
    }
}

@Composable
private fun DrawerButton(icon: Painter, label: String, isSelected: Boolean, action: () -> Unit, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val textAndIconColor = if (isSelected) colors.secondary else colors.onSurface.copy(alpha = 0.9f)
    val backgroundColor = if (isSelected) colors.secondary.copy(alpha = 0.12f) else Color.Transparent

    Surface(
        modifier = modifier
            .padding(start = 8.dp, top = 4.dp, end = 8.dp)
            .fillMaxWidth()
            .height(48.dp),
        color = backgroundColor,
        shape = MaterialTheme.shapes.small,
    ) {
        TextButton(
            onClick = action,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(
                    painter = icon,
                    contentDescription = null,
                    tint = textAndIconColor,
                    modifier = Modifier.size(24.dp),
                )
                Spacer(Modifier.width(16.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = textAndIconColor,
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
                ),
            )

            override fun openDrawer() = Unit

            override fun closeDrawer() = Unit

            override fun navigateToMetronome() = Unit

            override fun navigateToTraining() = Unit

            override fun navigateToPolyrhythms() = Unit

            override fun navigateToSoundLibrary() = Unit

            override fun navigateToSettings() = Unit

            override fun navigateToAbout() = Unit
        },
    )
}
