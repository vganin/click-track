package com.vsevolodganin.clicktrack.redux

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ScreenBackstack(
    val frontScreen: Screen,
    val restScreens: List<Screen>,
) : Parcelable
