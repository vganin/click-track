package com.vsevolodganin.clicktrack.utils.compose

import android.os.Build
import android.view.HapticFeedbackConstants
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.hapticfeedback.HapticFeedbackType.Companion.LongPress
import androidx.compose.ui.hapticfeedback.HapticFeedbackType.Companion.TextHandleMove
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView

@Composable
actual fun ForcedHapticFeedback(content: @Composable () -> Unit) {
    val view = LocalView.current
    CompositionLocalProvider(
        LocalHapticFeedback provides object : HapticFeedback {
            override fun performHapticFeedback(hapticFeedbackType: HapticFeedbackType) {
                view.performHapticFeedback(
                    when (hapticFeedbackType) {
                        LongPress -> HapticFeedbackConstants.LONG_PRESS
                        TextHandleMove -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                            HapticFeedbackConstants.TEXT_HANDLE_MOVE
                        } else {
                            return
                        }

                        else -> return
                    },
                    @Suppress("DEPRECATION") // TODO: Fix deprecation
                    HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING,
                )
            }
        },
    ) {
        content()
    }
}
