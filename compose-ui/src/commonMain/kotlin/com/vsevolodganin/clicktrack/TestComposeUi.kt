package com.vsevolodganin.clicktrack

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
internal fun TestComposeUi() {
    Box(modifier = Modifier.size(100.dp).background(Color.Red)) {
        Text("Hello test!")
    }
}
