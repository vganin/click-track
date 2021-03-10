package com.vsevolodganin.clicktrack.sounds.model

import com.vsevolodganin.clicktrack.lib.android.AndroidParcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class ClickSounds(
    val strongBeat: ClickSoundSource?,
    val weakBeat: ClickSoundSource?,
) : AndroidParcelable {

    val asIterable: Iterable<ClickSoundSource> get() = listOfNotNull(strongBeat, weakBeat)

    fun beatByType(type: ClickSoundType) = when (type) {
        ClickSoundType.STRONG -> strongBeat
        ClickSoundType.WEAK -> weakBeat
    }
}
