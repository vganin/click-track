package com.vsevolodganin.clicktrack.view

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.with
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.DrawerState
import androidx.compose.material.DrawerValue
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.vsevolodganin.clicktrack.redux.Dispatch
import com.vsevolodganin.clicktrack.state.DrawerScreenState
import com.vsevolodganin.clicktrack.state.Screen
import com.vsevolodganin.clicktrack.state.actions.CloseDrawer
import com.vsevolodganin.clicktrack.state.actions.OpenDrawer
import com.vsevolodganin.clicktrack.view.screen.ClickTrackListScreenView
import com.vsevolodganin.clicktrack.view.screen.DrawerScreenView
import com.vsevolodganin.clicktrack.view.screen.EditClickTrackScreenView
import com.vsevolodganin.clicktrack.view.screen.MetronomeScreenView
import com.vsevolodganin.clicktrack.view.screen.PlayClickTrackScreenView
import com.vsevolodganin.clicktrack.view.screen.SettingsScreenView
import com.vsevolodganin.clicktrack.view.screen.SoundLibraryScreenView

@Composable
fun ContentView(
    screen: Screen,
    position: Int,
    drawerScreenState: DrawerScreenState,
    dispatch: Dispatch,
) {
    ClickTrackTheme {
        val modifier = Modifier.fillMaxSize()

        val previousPosition = remember { mutableStateOf(position) }
        val isPush = remember { mutableStateOf(true) }

        if (position != previousPosition.value) {
            isPush.value = position > previousPosition.value
            previousPosition.value = position
        }

        Scaffold(
            scaffoldState = rememberScaffoldState(drawerState = drawerState(drawerScreenState, dispatch)),
            drawerContent = { DrawerScreenView(drawerScreenState, dispatch) },
            drawerGesturesEnabled = drawerScreenState.gesturesEnabled,
        ) {
            AnimatedContent(
                targetState = position,
                transitionSpec = {
                    if (isPush.value) {
                        slideIntoContainer(towards = AnimatedContentScope.SlideDirection.Left) with
                                slideOutOfContainer(towards = AnimatedContentScope.SlideDirection.Left)
                    } else {
                        slideIntoContainer(towards = AnimatedContentScope.SlideDirection.Right) with
                                slideOutOfContainer(towards = AnimatedContentScope.SlideDirection.Right)
                    }
                }
            ) { targetPosition ->
                var targetScreen by remember { mutableStateOf(screen) }
                if (position == targetPosition) {
                    targetScreen = screen
                }

                when (@Suppress("NAME_SHADOWING") val screen = targetScreen) {
                    is Screen.ClickTrackList -> ClickTrackListScreenView(screen.state, modifier, dispatch)
                    is Screen.PlayClickTrack -> PlayClickTrackScreenView(screen.state, modifier, dispatch)
                    is Screen.EditClickTrack -> EditClickTrackScreenView(screen.state, modifier, dispatch)
                    is Screen.Metronome -> MetronomeScreenView(screen.state, modifier, dispatch)
                    is Screen.Settings -> SettingsScreenView(screen.state, modifier, dispatch)
                    is Screen.SoundLibrary -> SoundLibraryScreenView(screen.state, modifier, dispatch)
                }
            }
        }
    }
}

@Composable
private fun drawerState(drawerScreenState: DrawerScreenState, dispatch: Dispatch): DrawerState {
    val drawerValue = if (drawerScreenState.isOpened) DrawerValue.Open else DrawerValue.Closed
    return rememberDrawerState(drawerValue) { newDrawerValue ->
        when (newDrawerValue) {
            DrawerValue.Closed -> dispatch(CloseDrawer)
            DrawerValue.Open -> dispatch(OpenDrawer)
        }
        true
    }.apply {
        LaunchedEffect(drawerValue) {
            try {
                when (drawerValue) {
                    DrawerValue.Closed -> close()
                    DrawerValue.Open -> open()
                }
            } catch (e: IllegalArgumentException) {
                // FIXME: Ignoring spurious exception `java.lang.IllegalArgumentException: State androidx.compose.material.DrawerState@6294caf is not attached to a component. Have you passed state object to a component?`
            }
        }

        // FIXME(https://issuetracker.google.com/issues/181387076): Synchronizing manually internal drawer state with external for now
        LaunchedEffect(currentValue) {
            if (drawerValue != currentValue && !isAnimationRunning) {
                when (currentValue) {
                    DrawerValue.Closed -> dispatch(CloseDrawer)
                    DrawerValue.Open -> dispatch(OpenDrawer)
                }
            }
        }
    }
}
