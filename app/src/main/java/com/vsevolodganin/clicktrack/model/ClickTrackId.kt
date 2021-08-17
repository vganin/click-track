package com.vsevolodganin.clicktrack.model

import android.os.Parcelable
import com.vsevolodganin.clicktrack.sounds.model.ClickSoundsId
import kotlinx.parcelize.Parcelize

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
