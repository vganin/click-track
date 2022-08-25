package com.vsevolodganin.clicktrack.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserClickSounds(
    val id: ClickSoundsId.Database,
    val value: ClickSounds,
) : Parcelable
