package com.vsevolodganin.clicktrack.state.screen

import android.os.Parcelable
import com.vsevolodganin.clicktrack.theme.Theme
import kotlinx.parcelize.Parcelize

@Parcelize
data class SettingsScreenState(
    val theme: Theme,
) : Parcelable

