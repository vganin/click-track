package net.ganin.vsevolod.clicktrack.view

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun ContentView(Screen: @Composable () -> Unit) {
    MaterialTheme {
        Screen()
    }
}
