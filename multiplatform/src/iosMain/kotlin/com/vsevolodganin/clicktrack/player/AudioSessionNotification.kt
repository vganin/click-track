package com.vsevolodganin.clicktrack.player

interface AudioSessionNotification {
    fun show(title: String, contentText: String, isPaused: Boolean)
    fun hide()
    fun setCallbacks(onPause: () -> Unit, onResume: () -> Unit, onStop: () -> Unit)
}
