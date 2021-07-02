package com.vsevolodganin.clicktrack.state.redux

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DrawerState(
    val isOpened: Boolean,
    val gesturesEnabled: Boolean,
    val selectedItem: SelectedItem?,
) : Parcelable {
    enum class SelectedItem {
        METRONOME, SETTINGS, SOUND_LIBRARY
    }
}