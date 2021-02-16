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
import androidx.compose.runtime.setValue
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

@Composable
fun ContentView(
    screen: Screen,
    positionInBackstack: Int,
    drawerScreenState: DrawerScreenState,
    dispatch: Dispatch,
) {
    ClickTrackTheme {
        val modifier = Modifier.fillMaxSize()

        var previousPosition by remember { mutableStateOf(positionInBackstack) }
        val isPush = remember(positionInBackstack) { positionInBackstack > previousPosition }
        previousPosition = positionInBackstack

        Scaffold(
            scaffoldState = rememberScaffoldState(drawerState = drawerState(drawerScreenState, dispatch)),
            drawerContent = { DrawerScreenView(drawerScreenState, dispatch) },
        ) {
            ComposableSwitcher(
                currentKey = positionInBackstack,
                currentState = screen,
            ) { _, screen, transition ->
                if (transition.currentState == VISIBLE) {
                    previousPosition = positionInBackstack
                }

                val offset by transition.animateFloat(transitionSpec = { spring() }) { state ->
                    when (state) {
                        VISIBLE -> 0.0f
                        ENTERING -> if (isPush) 1.0f else -1.0f
                        EXITING -> if (isPush) -1.0f else 1.0f
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
                        is Screen.Settings -> { /*TODO*/ }
                    }
                }
            }
        }
    }
}

@Composable
private fun drawerState(drawerScreenState: DrawerScreenState, dispatch: Dispatch): DrawerState {
    val showDrawer = drawerScreenState.isOpened
    return rememberDrawerState(DrawerValue.Closed).apply {
        LaunchedEffect(showDrawer) {
            if (showDrawer) open() else close()
        }
        LaunchedEffect(value) {
            when (value) {
                DrawerValue.Closed -> dispatch(CloseDrawer)
                DrawerValue.Open -> dispatch(OpenDrawer)
            }
        }
    }
}
