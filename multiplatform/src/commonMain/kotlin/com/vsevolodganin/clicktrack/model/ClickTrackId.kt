package com.vsevolodganin.clicktrack.model

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize

sealed interface ClickTrackId : Parcelable, PlayableId {

    @Parcelize
    data class Database(val value: Long) : ClickTrackId

    sealed interface Builtin : ClickTrackId {

        @Parcelize
        object Metronome : Builtin

        @Parcelize
        class ClickSoundsTest(val soundsId: ClickSoundsId) : Builtin
    }
}
