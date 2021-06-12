package com.vsevolodganin.clicktrack.view

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.with
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.DrawerState
import androidx.compose.material.DrawerValue
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
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
        Scaffold(
            scaffoldState = rememberScaffoldState(drawerState = drawerState(drawerScreenState, dispatch)),
            drawerContent = { DrawerScreenView(drawerScreenState, dispatch) },
            drawerGesturesEnabled = drawerScreenState.gesturesEnabled,
        ) {
            AnimationScreen(screen, position) { targetScreen ->
                val modifier = Modifier.fillMaxSize()
                when (targetScreen) {
                    is Screen.ClickTrackList -> ClickTrackListScreenView(targetScreen.state, modifier, dispatch)
                    is Screen.PlayClickTrack -> PlayClickTrackScreenView(targetScreen.state, modifier, dispatch)
                    is Screen.EditClickTrack -> EditClickTrackScreenView(targetScreen.state, modifier, dispatch)
                    is Screen.Metronome -> MetronomeScreenView(targetScreen.state, modifier, dispatch)
                    is Screen.Settings -> SettingsScreenView(targetScreen.state, modifier, dispatch)
                    is Screen.SoundLibrary -> SoundLibraryScreenView(targetScreen.state, modifier, dispatch)
                }
            }
        }
    }
}

@Composable
private fun AnimationScreen(
    screen: Screen,
    position: Int,
    content: @Composable (screen: Screen) -> Unit,
) {
    val isPush = remember { mutableStateOf(true) }
    val previousPosition = remember { mutableStateOf(position) }
    val keyToScreen = remember { mutableStateOf(mapOf(position to screen)) }

    if (position != previousPosition.value) {
        isPush.value = position > previousPosition.value
        previousPosition.value = position
        keyToScreen.value += position to screen
    }

    val transition = updateTransition(targetState = position, label = "AnimatedScreen")

    if (transition.targetState == transition.currentState) {
        keyToScreen.value = mapOf(position to screen)
    }

    transition.AnimatedContent(
        transitionSpec = {
            val animationSpec = if (DEBUG_TRANSITIONS) {
                tween(durationMillis = 5000, easing = LinearEasing)
            } else {
                spring(visibilityThreshold = IntOffset.VisibilityThreshold)
            }

            if (isPush.value) {
                slideIntoContainer(towards = AnimatedContentScope.SlideDirection.Left, animationSpec = animationSpec) with
                        slideOutOfContainer(towards = AnimatedContentScope.SlideDirection.Left, animationSpec = animationSpec)
            } else {
                slideIntoContainer(towards = AnimatedContentScope.SlideDirection.Right, animationSpec = animationSpec) with
                        slideOutOfContainer(towards = AnimatedContentScope.SlideDirection.Right, animationSpec = animationSpec)
            }
        }
    ) { targetState ->
        content(keyToScreen.value[targetState]!!)
    }
}

@Composable
private fun drawerState(drawerScreenState: DrawerScreenState, dispatch: Dispatch): DrawerState {
    val drawerValue = if (drawerScreenState.isOpened) DrawerValue.Open else DrawerValue.Closed
    return rememberDrawerState(initialValue = drawerValue, confirmStateChange = { newDrawerValue ->
        when (newDrawerValue) {
            DrawerValue.Closed -> dispatch(CloseDrawer)
            DrawerValue.Open -> dispatch(OpenDrawer)
        }
        false
    }).apply {
        LaunchedEffect(drawerValue) {
            when (drawerValue) {
                DrawerValue.Closed -> close()
                DrawerValue.Open -> open()
            }
        }
    }
}

private const val DEBUG_TRANSITIONS = false
