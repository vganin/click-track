package com.vsevolodganin.clicktrack.utils.compose

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.currentRecomposeScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.vsevolodganin.clicktrack.utils.compose.ComposableTransitionState.ENTERING
import com.vsevolodganin.clicktrack.utils.compose.ComposableTransitionState.EXITING
import com.vsevolodganin.clicktrack.utils.compose.ComposableTransitionState.VISIBLE

enum class ComposableTransitionState {
    VISIBLE, ENTERING, EXITING,
}

@Composable
fun <Key, State> ComposableSwitcher(
    currentKey: Key,
    currentState: State,
    snapOnInitialComposition: Boolean = true,
    content: @Composable (Key, State, Transition<ComposableTransitionState>) -> Unit,
) {
    val state = remember {
        if (snapOnInitialComposition) {
            ComposableSwitcherState(
                currentKey = currentKey,
                items = mutableListOf(TransitionItem(
                    key = currentKey,
                    state = currentState,
                    content = { children ->
                        val transition = updateTransition(VISIBLE)
                        children(transition)
                    }
                )),
            )
        } else {
            ComposableSwitcherState(
                // we use Any here as something which will not be equals to the real initial value
                currentKey = Any(),
            )
        }
    }

    val switcherRecomposeScope = currentRecomposeScope

    if (currentKey != state.currentKey) {
        state.currentKey = currentKey

        val keysAndStates = state.items.map { it.key to it.state }.toMutableList()
        if (!keysAndStates.any { (key, _) -> key == currentKey }) {
            keysAndStates.add(currentKey to currentState)
        }

        state.items.clear()

        keysAndStates.mapTo(state.items) { (itemKey, itemState) ->
            TransitionItem(key = itemKey, state = itemState, content = { children ->
                val isVisible = itemKey == currentKey
                val transitionState = remember {
                    MutableTransitionState(when (isVisible) {
                        true -> ENTERING
                        false -> VISIBLE
                    })
                }
                transitionState.targetState = when (isVisible) {
                    true -> VISIBLE
                    false -> EXITING
                }
                val transition = updateTransition(transitionState)

                if (!isVisible && transitionState.currentState == transitionState.targetState) {
                    SideEffect {
                        state.items.removeAll { it.key == itemKey }
                        switcherRecomposeScope.invalidate()
                    }
                }

                children(transition)
            })
        }
    } else {
        state.items.find { it.key == currentKey }?.state = currentState
    }

    Box {
        state.items.forEach { (itemKey, itemState, content) ->
            key(itemKey) {
                content { transition ->
                    content(itemKey, itemState, transition)
                }
            }
        }
    }
}

private class ComposableSwitcherState<Key, State>(
    var currentKey: Any?,
    val items: MutableList<TransitionItem<Key, State>> = mutableListOf(),
)

private data class TransitionItem<Key, State>(
    val key: Key,
    var state: State,
    val content: @Composable (children: @Composable (Transition<ComposableTransitionState>) -> Unit) -> Unit,
)

@Preview
@Composable
fun ComposableSwitcherPreview() {
    val screens = remember {
        listOf(
            "Screen A",
            "Screen B",
            "Screen C",
        )
    }

    var screenIndex by remember { mutableStateOf(0) }
    var slideLeft by remember { mutableStateOf(true) }

    fun navigateNextScreen() {
        screenIndex = (screenIndex + 1).coerceIn(0, screens.lastIndex)
        slideLeft = true
    }

    fun navigatePreviousScreen() {
        screenIndex = (screenIndex - 1).coerceIn(0, screens.lastIndex)
        slideLeft = false
    }

    ComposableSwitcher(
        currentKey = screenIndex,
        currentState = screens[screenIndex],
    ) { _, screenData, transition ->
        val offset by transition.animateFloat(transitionSpec = { tween(durationMillis = 2000) }) { state ->
            when (state) {
                VISIBLE -> 0.0f
                ENTERING -> if (slideLeft) 1.0f else -1.0f
                EXITING -> if (slideLeft) -1.0f else 1.0f
            }
        }

        BoxWithConstraints {
            val width = maxWidth

            Box {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .offset(x = width * offset)
                ) {
                    Text(text = screenData, modifier = Modifier.align(Alignment.Center))
                }

                IconButton(modifier = Modifier.align(Alignment.CenterStart), onClick = ::navigatePreviousScreen) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                }

                IconButton(modifier = Modifier.align(Alignment.CenterEnd), onClick = ::navigateNextScreen) {
                    Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null)
                }
            }
        }
    }
}
