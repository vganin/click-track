package com.vsevolodganin.clicktrack.state

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DrawerScreenState(
    val isOpened: Boolean,
    val currentScreen: Screen?,
) : Parcelable
