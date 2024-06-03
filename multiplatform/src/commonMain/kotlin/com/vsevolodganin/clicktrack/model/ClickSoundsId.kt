package com.vsevolodganin.clicktrack.model

import com.vsevolodganin.clicktrack.utils.parcelable.Parcelable
import com.vsevolodganin.clicktrack.utils.parcelable.Parcelize

sealed interface ClickSoundsId : Parcelable {
    @Parcelize
    data class Database(val value: Long) : ClickSoundsId

    @Parcelize
    data class Builtin(val value: BuiltinClickSounds) : ClickSoundsId
}
