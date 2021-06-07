package com.vsevolodganin.clicktrack.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed interface ClickTrackId : Parcelable {

    @Parcelize
    data class Database(val value: Long) : ClickTrackId

    @Parcelize
    enum class Builtin : ClickTrackId {
        METRONOME
    }
}
