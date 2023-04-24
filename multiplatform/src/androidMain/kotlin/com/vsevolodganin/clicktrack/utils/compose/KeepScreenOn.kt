package com.vsevolodganin.clicktrack.utils.compose

import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.ui.viewinterop.AndroidView

@Composable
@NonRestartableComposable
actual fun KeepScreenOn() = AndroidView({ View(it).apply { keepScreenOn = true } })
