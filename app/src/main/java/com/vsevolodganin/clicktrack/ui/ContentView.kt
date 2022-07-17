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
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DrawerValue
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import com.vsevolodganin.clicktrack.redux.action.DrawerAction
import com.vsevolodganin.clicktrack.redux.action.InAppReviewAction
import com.vsevolodganin.clicktrack.redux.core.Dispatch
import com.vsevolodganin.clicktrack.ui.model.AppUiState
import com.vsevolodganin.clicktrack.ui.model.UiScreen
import com.vsevolodganin.clicktrack.ui.piece.DrawerView
import com.vsevolodganin.clicktrack.ui.screen.AboutScreenView
import com.vsevolodganin.clicktrack.ui.screen.ClickTrackListScreenView
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
    val drawerState = appUiState.drawerState

    DisposableEffect(Unit) {
        dispatch(InAppReviewAction.TryRequestReview)
        onDispose {}
    }

    ClickTrackTheme {
        Scaffold(
            scaffoldState = rememberScaffoldState(drawerState = drawerState(drawerState.isOpened, dispatch)),
            drawerContent = { DrawerView(drawerState, dispatch) },
        ) {
            AnimatedScreen(appUiState, Modifier.padding(it)) { targetScreen ->
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

@Composable
private fun AnimatedScreen(
    state: AppUiState,
    modifier: Modifier,
    content: @Composable (screen: UiScreen) -> Unit,
) {
    updateTransition(targetState = state, label = "AnimatedScreen").AnimatedContent(
        modifier = modifier,
        transitionSpec = {
            val animationSpec = if (DEBUG_TRANSITIONS) {
                tween(durationMillis = 5000, easing = LinearEasing)
            } else {
                spring(visibilityThreshold = IntOffset.VisibilityThreshold)
            }

            val isPush = targetState.screenPosition >= initialState.screenPosition

            if (isPush) {
                slideIntoContainer(towards = AnimatedContentScope.SlideDirection.Left, animationSpec = animationSpec) with
                        slideOutOfContainer(towards = AnimatedContentScope.SlideDirection.Left, animationSpec = animationSpec)
            } else {
                slideIntoContainer(towards = AnimatedContentScope.SlideDirection.Right, animationSpec = animationSpec) with
                        slideOutOfContainer(towards = AnimatedContentScope.SlideDirection.Right, animationSpec = animationSpec)
            }
        },
        contentKey = { it.screenPosition to it.screen.key }
    ) { transitionState ->
        content(transitionState.screen)
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
                    DrawerValue.Closed -> dispatch(DrawerAction.Close)
                    DrawerValue.Open -> dispatch(DrawerAction.Open)
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
