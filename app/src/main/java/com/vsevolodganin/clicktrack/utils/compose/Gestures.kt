package com.vsevolodganin.clicktrack.utils.compose

import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.consumeDownChange
import androidx.compose.ui.unit.inMilliseconds
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout

suspend fun PointerInputScope.awaitLongTapOrCancellation(): PointerInputChange? {
    val down = awaitPointerEventScope {
        awaitFirstDown().also {
            it.consumeDownChange()
        }
    }

    return try {
        withTimeout(viewConfiguration.longPressTimeout.inMilliseconds()) {
            awaitPointerEventScope {
                waitForUpOrCancellation()?.also { it.consumeDownChange() }
            }
        }
        return null
    } catch (_: TimeoutCancellationException) {
        down
    }
}
