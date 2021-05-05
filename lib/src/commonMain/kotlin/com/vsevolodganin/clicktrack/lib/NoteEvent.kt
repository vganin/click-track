package com.vsevolodganin.clicktrack.lib

import com.vsevolodganin.clicktrack.lib.android.AndroidParcelable
import com.vsevolodganin.clicktrack.lib.android.AndroidParcelize
import com.vsevolodganin.clicktrack.lib.math.Rational
import kotlinx.serialization.Serializable

@Serializable
@AndroidParcelize
public data class NoteEvent(val length: Rational, val type: Type) : AndroidParcelable {
    public enum class Type { REST, NOTE }
}
