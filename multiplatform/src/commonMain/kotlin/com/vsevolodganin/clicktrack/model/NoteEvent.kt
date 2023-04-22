package com.vsevolodganin.clicktrack.model

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.vsevolodganin.clicktrack.utils.math.Rational
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class NoteEvent(val length: Rational, val type: Type) : Parcelable {
    enum class Type { REST, NOTE }
}
