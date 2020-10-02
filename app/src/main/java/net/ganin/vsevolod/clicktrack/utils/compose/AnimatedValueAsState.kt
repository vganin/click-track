package net.ganin.vsevolod.clicktrack.utils.compose

import androidx.compose.animation.core.AnimationVector
import androidx.compose.animation.core.BaseAnimatedValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Composable
fun <T, V : AnimationVector> BaseAnimatedValue<T, V>.asState(): State<T> {
    val state = remember { mutableStateOf(value) }
    state.value = value
    return state
}
