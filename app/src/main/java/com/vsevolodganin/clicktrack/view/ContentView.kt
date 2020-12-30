package com.vsevolodganin.clicktrack.view

import androidx.compose.animation.core.FloatPropKey
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.transitionDefinition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.WithConstraints
import com.vsevolodganin.clicktrack.redux.Dispatch
import com.vsevolodganin.clicktrack.state.Screen
import com.vsevolodganin.clicktrack.utils.compose.ComposableSwitcher
import com.vsevolodganin.clicktrack.utils.compose.ComposableTransitionState
import com.vsevolodganin.clicktrack.view.screen.ClickTrackListScreenView
import com.vsevolodganin.clicktrack.view.screen.EditClickTrackScreenView
import com.vsevolodganin.clicktrack.view.screen.MetronomeScreenView
import com.vsevolodganin.clicktrack.view.screen.PlayClickTrackScreenView

@Composable
fun ContentView(screen: Screen, positionInBackstack: Int, dispatch: Dispatch) {
    ClickTrackTheme {
        val modifier = Modifier.fillMaxSize()

        val previousPosition = remember { mutableStateOf(positionInBackstack) }
        val isPush = positionInBackstack > previousPosition.value
        previousPosition.value = positionInBackstack

        ComposableSwitcher(
            currentKey = positionInBackstack,
            currentState = screen,
            transitionDefinition = screenTransitionDefinition(isPush),
        ) { _, screen, transitionState ->
            val alpha = transitionState[AlphaProp]
            val offset = transitionState[OffsetProp]

            WithConstraints {
                val width = maxWidth

                val modifierUnderTransition = modifier
                    .alpha(alpha)
                    .offset(width * offset)

                when (screen) {
                    is Screen.ClickTrackList -> ClickTrackListScreenView(screen.state, modifierUnderTransition, dispatch)
                    is Screen.PlayClickTrack -> PlayClickTrackScreenView(screen.state, modifierUnderTransition, dispatch)
                    is Screen.EditClickTrack -> EditClickTrackScreenView(screen.state, modifierUnderTransition, dispatch)
                    is Screen.Metronome -> MetronomeScreenView(screen.state, modifierUnderTransition, dispatch)
                }
            }
        }
    }
}

private const val ScreenTransitionAnimationDuration = 250

private val AlphaProp = FloatPropKey("alpha")
private val OffsetProp = FloatPropKey("offset")

private fun screenTransitionDefinition(isPush: Boolean) = transitionDefinition<ComposableTransitionState> {
    state(ComposableTransitionState.VISIBLE) {
        this[AlphaProp] = 1.0f
        this[OffsetProp] = 0.0f
    }
    state(ComposableTransitionState.ENTERING) {
        this[AlphaProp] = 0.7f
        this[OffsetProp] = if (isPush) 1.0f else -1.0f
    }
    state(ComposableTransitionState.EXITING) {
        this[AlphaProp] = 0.5f
        this[OffsetProp] = if (isPush) -1.0f else 1.0f
    }

    transition(
        fromState = ComposableTransitionState.ENTERING,
        toState = ComposableTransitionState.VISIBLE
    ) {
        AlphaProp using tween(
            durationMillis = ScreenTransitionAnimationDuration,
            easing = LinearEasing
        )
        OffsetProp using spring()
    }

    transition(
        fromState = ComposableTransitionState.VISIBLE,
        toState = ComposableTransitionState.EXITING
    ) {
        AlphaProp using tween(
            durationMillis = ScreenTransitionAnimationDuration,
            easing = LinearEasing
        )
        OffsetProp using spring()
    }
}
