package net.ganin.vsevolod.clicktrack.utils.compose

import androidx.compose.animation.asDisposableClock
import androidx.compose.animation.core.TransitionDefinition
import androidx.compose.animation.core.TransitionState
import androidx.compose.animation.core.createAnimation
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.currentComposer
import androidx.compose.runtime.invalidate
import androidx.compose.runtime.key
import androidx.compose.runtime.onCommit
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.AmbientAnimationClock

enum class ComposableTransitionState {
    VISIBLE, ENTERING, EXITING,
}

@Composable
fun <Key, State> ComposableSwitcher(
    currentKey: Key,
    currentState: State,
    transitionDefinition: TransitionDefinition<ComposableTransitionState>,
    snapOnInitialComposition: Boolean = true,
    content: @Composable (Key, State, TransitionState) -> Unit,
) {
    val isInitialComposition = currentComposer.inserting
    val state = remember { ItemTransitionInnerState<Key, State>() }

    if (currentKey != state.currentKey) {
        state.currentKey = currentKey

        val keysAndStates = state.items.map { it.key to it.state }.toMutableList()
        if (!keysAndStates.any { (key, _) -> key == currentKey }) {
            keysAndStates.add(currentKey to currentState)
        }

        state.items.clear()

        keysAndStates.mapTo(state.items) { (itemKey, itemState) ->
            ItemTransitionItem(itemKey, itemState) { children ->
                val clock = AmbientAnimationClock.current.asDisposableClock()
                val visible = itemKey == currentKey

                val anim = remember(clock, transitionDefinition) {
                    transitionDefinition.createAnimation(
                        clock = clock,
                        initState = when {
                            visible -> ComposableTransitionState.ENTERING
                            else -> ComposableTransitionState.VISIBLE
                        }
                    )
                }

                onCommit(visible) {
                    anim.onStateChangeFinished = { _ ->
                        if (itemKey == state.currentKey) {
                            // leave only the current in the list
                            state.items.removeAll { it.key != state.currentKey }
                            state.invalidate()
                        }
                    }
                    anim.onUpdate = { state.invalidate() }

                    val targetState = when {
                        visible -> ComposableTransitionState.VISIBLE
                        else -> ComposableTransitionState.EXITING
                    }

                    if (snapOnInitialComposition && isInitialComposition) {
                        anim.snapToState(targetState)
                        // FIXME(https://issuetracker.google.com/issues/174837340)
                        anim.onStateChangeFinished?.invoke(targetState)
                    } else {
                        anim.toState(targetState)
                    }
                }

                children(anim)
            }
        }
    }

    Box {
        state.invalidate = invalidate
        state.items.forEach { (itemKey, itemState, itemTransition) ->
            key(itemKey) {
                itemTransition { transitionState ->
                    content(itemKey, itemState, transitionState)
                }
            }
        }
    }
}

private class ItemTransitionInnerState<Key, State> {
    // we use Any here as something which will not be equals to the real initial value
    var currentKey: Any? = Any()
    var items = mutableListOf<ItemTransitionItem<Key, State>>()
    var invalidate: () -> Unit = { }
}

private data class ItemTransitionItem<Key, State>(
    val key: Key,
    val state: State,
    val content: ItemTransitionContent,
)

private typealias ItemTransitionContent = @Composable (children: @Composable (TransitionState) -> Unit) -> Unit
