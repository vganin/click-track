package com.vsevolodganin.clicktrack.view

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.Modifier
import com.vsevolodganin.clicktrack.redux.Dispatch
import com.vsevolodganin.clicktrack.state.DrawerScreenState
import com.vsevolodganin.clicktrack.state.Screen
import com.vsevolodganin.clicktrack.state.actions.CloseDrawer
import com.vsevolodganin.clicktrack.state.actions.OpenDrawer
import com.vsevolodganin.clicktrack.utils.compose.ComposableSwitcher
import com.vsevolodganin.clicktrack.utils.compose.ComposableTransitionState.ENTERING
import com.vsevolodganin.clicktrack.utils.compose.ComposableTransitionState.EXITING
import com.vsevolodganin.clicktrack.utils.compose.ComposableTransitionState.VISIBLE
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
    positionInBackstack: Int,
    drawerScreenState: DrawerScreenState,
    dispatch: Dispatch,
) {
    ClickTrackTheme {
        val modifier = Modifier.fillMaxSize()

        val previousPosition = remember { mutableStateOf(positionInBackstack) }
        val isPush = remember { mutableStateOf(true) }

        if (positionInBackstack != previousPosition.value) {
            isPush.value = positionInBackstack > previousPosition.value
            previousPosition.value = positionInBackstack
        }

        Scaffold(
            scaffoldState = rememberScaffoldState(drawerState = drawerState(drawerScreenState, dispatch)),
            drawerContent = { DrawerScreenView(drawerScreenState, dispatch) },
            drawerGesturesEnabled = drawerScreenState.gesturesEnabled,
        ) {
            ComposableSwitcher(
                key = previousPosition.value,
                state = screen,
            ) { _, screen, transition ->
                val offset by transition.animateFloat(transitionSpec = { spring() }) { state ->
                    when (state) {
                        VISIBLE -> 0.0f
                        ENTERING -> if (isPush.value) 1.0f else -1.0f
                        EXITING -> if (isPush.value) -1.0f else 1.0f
                    }
                }

                BoxWithConstraints {
                    val width = maxWidth

                    val modifierUnderTransition = modifier
                        .offset(x = width * offset)

                    when (screen) {
                        is Screen.ClickTrackList -> ClickTrackListScreenView(screen.state, modifierUnderTransition, dispatch)
                        is Screen.PlayClickTrack -> PlayClickTrackScreenView(screen.state, modifierUnderTransition, dispatch)
                        is Screen.EditClickTrack -> EditClickTrackScreenView(screen.state, modifierUnderTransition, dispatch)
                        is Screen.Metronome -> MetronomeScreenView(screen.state, modifierUnderTransition, dispatch)
                        is Screen.Settings -> SettingsScreenView(screen.state, modifierUnderTransition, dispatch)
                        is Screen.SoundLibrary -> SoundLibraryScreenView(screen.state, modifierUnderTransition, dispatch)
                    }
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
