package com.vsevolodganin.clicktrack.model

import com.vsevolodganin.clicktrack.utils.math.Rational
import com.vsevolodganin.clicktrack.utils.parcelable.Parcelable
import com.vsevolodganin.clicktrack.utils.parcelable.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class NoteEvent(val length: Rational, val type: Type) : Parcelable {
    enum class Type { REST, NOTE }
}
