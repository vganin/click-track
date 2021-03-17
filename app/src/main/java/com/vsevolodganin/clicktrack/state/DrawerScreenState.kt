package com.vsevolodganin.clicktrack.state

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DrawerScreenState(
    val isOpened: Boolean,
    val gesturesEnabled: Boolean,
    val selectedItem: SelectedItem?,
    val displayVersion: String,
) : Parcelable {
    enum class SelectedItem {
        METRONOME, SETTINGS, SOUND_LIBRARY
    }
}
