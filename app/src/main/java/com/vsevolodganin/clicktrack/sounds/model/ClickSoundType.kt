package com.vsevolodganin.clicktrack.sounds.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
enum class ClickSoundType : Parcelable {
    STRONG, WEAK
}
