package com.vsevolodganin.clicktrack.model

import kotlinx.serialization.Serializable

@Serializable
sealed interface ClickSoundsId {
    @Serializable
    data class Database(val value: Long) : ClickSoundsId

    @Serializable
    data class Builtin(val value: BuiltinClickSounds) : ClickSoundsId
}
