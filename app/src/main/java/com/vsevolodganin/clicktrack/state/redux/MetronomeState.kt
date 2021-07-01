package com.vsevolodganin.clicktrack.state.redux

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MetronomeState(
    val areOptionsExpanded: Boolean,
) : Parcelable
