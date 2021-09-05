package com.vsevolodganin.clicktrack.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.with
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.DrawerValue
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import com.google.accompanist.insets.ProvideWindowInsets
import com.vsevolodganin.clicktrack.state.redux.action.CloseDrawer
import com.vsevolodganin.clicktrack.state.redux.action.InAppReviewAction
import com.vsevolodganin.clicktrack.state.redux.action.OpenDrawer
import com.vsevolodganin.clicktrack.state.redux.core.Dispatch
import com.vsevolodganin.clicktrack.ui.model.AppUiState
import com.vsevolodganin.clicktrack.ui.model.UiScreen
import com.vsevolodganin.clicktrack.ui.screen.AboutScreenView
import com.vsevolodganin.clicktrack.ui.screen.ClickTrackListScreenView
import com.vsevolodganin.clicktrack.ui.screen.DrawerView
import com.vsevolodganin.clicktrack.ui.screen.EditClickTrackScreenView
import com.vsevolodganin.clicktrack.ui.screen.MetronomeScreenView
import com.vsevolodganin.clicktrack.ui.screen.PlayClickTrackScreenView
import com.vsevolodganin.clicktrack.ui.screen.PolyrhythmsScreenView
import com.vsevolodganin.clicktrack.ui.screen.SettingsScreenView
import com.vsevolodganin.clicktrack.ui.screen.SoundLibraryScreenView
import com.vsevolodganin.clicktrack.ui.screen.TrainingScreenView
import androidx.compose.material.DrawerState as ComposeDrawerState

@Composable
fun ContentView(
    appUiState: AppUiState,
    dispatch: Dispatch,
) {
    val screen = appUiState.screen
    val position = appUiState.screenPosition
    val drawerState = appUiState.drawerState

    // TODO: For testing purposes
    DisposableEffect(Unit) {
        dispatch(InAppReviewAction.RequestReview)
        onDispose {}
    }

    ClickTrackTheme {
        ProvideWindowInsets {
            Scaffold(
                scaffoldState = rememberScaffoldState(drawerState = drawerState(drawerState.isOpened, dispatch)),
                drawerContent = { DrawerView(drawerState, dispatch) },
                drawerGesturesEnabled = drawerState.gesturesEnabled,
            ) {
                AnimationScreen(screen, position) { targetScreen ->
                    val modifier = Modifier.fillMaxSize()
                    when (targetScreen) {
                        is UiScreen.ClickTrackList -> ClickTrackListScreenView(targetScreen.state, modifier, dispatch)
                        is UiScreen.PlayClickTrack -> PlayClickTrackScreenView(targetScreen.state, modifier, dispatch)
                        is UiScreen.EditClickTrack -> EditClickTrackScreenView(targetScreen.state, modifier, dispatch)
                        is UiScreen.Metronome -> MetronomeScreenView(targetScreen.state, modifier, dispatch)
                        is UiScreen.Settings -> SettingsScreenView(targetScreen.state, modifier, dispatch)
                        is UiScreen.SoundLibrary -> SoundLibraryScreenView(targetScreen.state, modifier, dispatch)
                        is UiScreen.Training -> TrainingScreenView(targetScreen.state, modifier, dispatch)
                        is UiScreen.About -> AboutScreenView(targetScreen.state, modifier, dispatch)
                        is UiScreen.Polyrhythms -> PolyrhythmsScreenView(targetScreen.state, modifier, dispatch)
                    }
                }
            }
        }
    }
}

@Composable
private fun AnimationScreen(
    screen: UiScreen,
    position: Int,
    content: @Composable (screen: UiScreen) -> Unit,
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
        keyToScreen.value[targetState]?.let { content(it) }
    }
}

@Composable
private fun drawerState(isOpened: Boolean, dispatch: Dispatch): ComposeDrawerState {
    val drawerValue = if (isOpened) DrawerValue.Open else DrawerValue.Closed
    return rememberDrawerState(
        initialValue = drawerValue,
        confirmStateChange = remember {
            { newDrawerValue ->
                when (newDrawerValue) {
                    DrawerValue.Closed -> dispatch(CloseDrawer)
                    DrawerValue.Open -> dispatch(OpenDrawer)
                }
                true
            }
        }
    ).apply {
        LaunchedEffect(drawerValue) {
            when (drawerValue) {
                DrawerValue.Closed -> close()
                DrawerValue.Open -> open()
            }
        }
    }
}

private const val DEBUG_TRANSITIONS = false
