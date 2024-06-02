package com.vsevolodganin.clicktrack.model

import com.vsevolodganin.clicktrack.utils.math.Rational
import kotlinx.serialization.Serializable

@Serializable
data class NoteEvent(val length: Rational, val type: Type) {
    enum class Type { REST, NOTE }
}
