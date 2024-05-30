package com.vsevolodganin.clicktrack.model

import com.vsevolodganin.clicktrack.utils.parcelable.Parcelable
import com.vsevolodganin.clicktrack.utils.parcelable.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
enum class ClickSoundType : Parcelable {
    STRONG,
    WEAK,
}
