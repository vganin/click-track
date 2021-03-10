package com.vsevolodganin.clicktrack.sounds.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

sealed class ClickSoundsId : Parcelable {

    @Parcelize
    @Serializable
    data class Database(val value: Long) : ClickSoundsId()

    @Parcelize
    data class Builtin(val value: BuiltinClickSounds) : ClickSoundsId()
}
