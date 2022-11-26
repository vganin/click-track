package com.vsevolodganin.clicktrack.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed interface ClickSoundsId : Parcelable {

    @Parcelize
    data class Database(val value: Long) : ClickSoundsId

    @Parcelize
    data class Builtin(val value: BuiltinClickSounds) : ClickSoundsId
}
