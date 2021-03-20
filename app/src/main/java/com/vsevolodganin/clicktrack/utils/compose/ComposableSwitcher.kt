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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
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
    key: Key,
    state: State,
    snapOnInitialComposition: Boolean = true,
    content: @Composable (Key, State, Transition<ComposableTransitionState>) -> Unit,
) {
    val items: MutableList<TransitionItem<Key, State>> = remember {
        mutableStateListOf<TransitionItem<Key, State>>().apply {
            if (snapOnInitialComposition) {
                this += TransitionItem(
                    key = key,
                    state = state,
                    content = { children ->
                        val transition = updateTransition(VISIBLE)
                        children(transition)
                    }
                )
            }
        }
    }

    val currentKey = remember {
        mutableStateOf(if (snapOnInitialComposition) key else Any())
    }

    if (key != currentKey.value) {
        currentKey.value = key

        val keysAndStates = items.map { it.key to it.state }.toMutableList()

        val currentKeyIndex = keysAndStates.indexOfFirst { (it, _) -> it == key }
        if (currentKeyIndex == -1) {
            keysAndStates += key to state
        } else {
            keysAndStates[currentKeyIndex] = key to state
        }

        items.clear()

        keysAndStates.mapTo(items) { (itemKey, itemState) ->
            TransitionItem(key = itemKey, state = itemState, content = @Composable { children ->
                val isVisible = itemKey == key
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
                    items.removeAll { it.key == itemKey }
                }

                children(transition)
            })
        }
    } else {
        // Updating current key's state if it changed. Don't want to update every time since it triggers recomposition
        items.indexOfFirst { it.key == key }.takeIf { it >= 0 }?.let { indexOfKey ->
            val newState = items[indexOfKey].copy(state = state)
            if (items[indexOfKey] != newState) {
                items[indexOfKey] = newState
            }
        }
    }

    Box {
        items.forEach { (itemKey, itemState, content) ->
            key(itemKey) {
                content { transition ->
                    content(itemKey, itemState, transition)
                }
            }
        }
    }
}

private data class TransitionItem<Key, State>(
    val key: Key,
    val state: State,
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
        key = screenIndex,
        state = screens[screenIndex],
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
