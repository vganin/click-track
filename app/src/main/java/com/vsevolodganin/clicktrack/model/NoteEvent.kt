package com.vsevolodganin.clicktrack.model

import android.os.Parcelable
import com.vsevolodganin.clicktrack.utils.math.Rational
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class NoteEvent(val length: Rational, val type: Type) : Parcelable {
    enum class Type { REST, NOTE }
}
