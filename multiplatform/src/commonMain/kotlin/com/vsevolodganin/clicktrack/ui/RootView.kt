package com.vsevolodganin.clicktrack.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.vsevolodganin.clicktrack.RootViewModel
import com.vsevolodganin.clicktrack.ScreenConfiguration
import com.vsevolodganin.clicktrack.ScreenViewModel
import com.vsevolodganin.clicktrack.drawer.DrawerViewModel
import com.vsevolodganin.clicktrack.ui.screen.AboutScreenView
import com.vsevolodganin.clicktrack.ui.screen.ClickTrackListScreenView
import com.vsevolodganin.clicktrack.ui.screen.EditClickTrackScreenView
import com.vsevolodganin.clicktrack.ui.screen.MetronomeScreenView
import com.vsevolodganin.clicktrack.ui.screen.PlayClickTrackScreenView
import com.vsevolodganin.clicktrack.ui.screen.PolyrhythmsScreenView
import com.vsevolodganin.clicktrack.ui.screen.SettingsScreenView
import com.vsevolodganin.clicktrack.ui.screen.SoundLibraryScreenView
import com.vsevolodganin.clicktrack.ui.screen.TrainingScreenView
import com.vsevolodganin.clicktrack.ui.theme.ClickTrackTheme
import com.vsevolodganin.clicktrack.utils.compose.ForcedHapticFeedback
import androidx.compose.material3.DrawerState as ComposeDrawerState

@Composable
fun RootView(viewModel: RootViewModel) {
    ClickTrackTheme {
        ForcedHapticFeedback {
            ModalNavigationDrawer(
                drawerState = drawerState(viewModel.drawer),
                drawerContent = { DrawerView(viewModel.drawer) },
            ) {
                Scaffold {
                    RootViewContent(viewModel)
                }
            }
        }
    }
}

@Composable
private fun RootViewContent(viewModel: RootViewModel) {
    val screens by viewModel.screens.subscribeAsState()

    val activeScreen by remember {
        derivedStateOf {
            ActiveScreen(screens.active.configuration, screens.active.instance, screens.backStack.size)
        }
    }

    updateTransition(targetState = activeScreen, label = "ContentView").AnimatedContent(
        transitionSpec = {
            val animationSpec = spring(visibilityThreshold = IntOffset.VisibilityThreshold)
            val isPush = targetState.position >= initialState.position

            if (isPush) {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = animationSpec,
                ) togetherWith slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = animationSpec,
                )
            } else {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = animationSpec,
                ) togetherWith slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = animationSpec,
                )
            }
        },
        contentKey = ActiveScreen::config,
    ) { screen ->
        RootViewContent(screen.viewModel)
    }
}

private data class ActiveScreen(
    val config: ScreenConfiguration,
    val viewModel: ScreenViewModel,
    val position: Int,
)

@Composable
private fun RootViewContent(viewModel: ScreenViewModel) {
    val modifier = Modifier.fillMaxSize()
    when (viewModel) {
        is ScreenViewModel.ClickTrackList -> ClickTrackListScreenView(viewModel.value, modifier)
        is ScreenViewModel.PlayClickTrack -> PlayClickTrackScreenView(viewModel.value, modifier)
        is ScreenViewModel.EditClickTrack -> EditClickTrackScreenView(viewModel.value, modifier)
        is ScreenViewModel.Metronome -> MetronomeScreenView(viewModel.value, modifier)
        is ScreenViewModel.Settings -> SettingsScreenView(viewModel.value, modifier)
        is ScreenViewModel.SoundLibrary -> SoundLibraryScreenView(viewModel.value, modifier)
        is ScreenViewModel.Training -> TrainingScreenView(viewModel.value, modifier)
        is ScreenViewModel.About -> AboutScreenView(viewModel.value, modifier)
        is ScreenViewModel.Polyrhythms -> PolyrhythmsScreenView(viewModel.value, modifier)
    }
}

@Composable
private fun drawerState(drawerViewModel: DrawerViewModel): ComposeDrawerState {
    val externalDrawerState by drawerViewModel.state.collectAsState()
    val localDrawerState = rememberDrawerState(
        initialValue = if (externalDrawerState.isOpened) DrawerValue.Open else DrawerValue.Closed,
    )

    // Sync the external state with the local state
    LaunchedEffect(externalDrawerState.isOpened) {
        when (externalDrawerState.isOpened) {
            true -> localDrawerState.open()
            false -> localDrawerState.close()
        }
    }
    LaunchedEffect(localDrawerState.currentValue) {
        when (localDrawerState.currentValue) {
            DrawerValue.Closed -> drawerViewModel.closeDrawer()
            DrawerValue.Open -> drawerViewModel.openDrawer()
        }
    }

    return localDrawerState
}
