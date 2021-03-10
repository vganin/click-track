package com.vsevolodganin.clicktrack.sounds.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class UserClickSounds(
    val id: ClickSoundsId.Database,
    val value: ClickSounds,
) : Parcelable
