package com.vsevolodganin.clicktrack.model

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize

sealed interface ClickSoundsId : Parcelable {

    @Parcelize
    data class Database(val value: Long) : ClickSoundsId

    @Parcelize
    data class Builtin(val value: BuiltinClickSounds) : ClickSoundsId
}
