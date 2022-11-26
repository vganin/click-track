package com.vsevolodganin.clicktrack.drawer

data class DrawerState(
    val isOpened: Boolean,
    val selectedItem: SelectedItem?,
) {
    enum class SelectedItem {
        METRONOME, TRAINING, SETTINGS, SOUND_LIBRARY, ABOUT, POLYRHYTHMS
    }
}
