package com.vsevolodganin.clicktrack.model

import kotlinx.serialization.Serializable

sealed interface ClickTrackId : PlayableId {
    @Serializable
    data class Database(val value: Long) : ClickTrackId

    sealed interface Builtin : ClickTrackId {
        @Serializable
        object Metronome : Builtin

        @Serializable
        class ClickSoundsTest(val soundsId: ClickSoundsId) : Builtin
    }
}
