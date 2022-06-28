package com.vsevolodganin.clicktrack.redux

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MetronomeState(
    val areOptionsExpanded: Boolean,
) : Parcelable
