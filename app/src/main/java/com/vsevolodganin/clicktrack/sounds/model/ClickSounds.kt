package com.vsevolodganin.clicktrack.sounds.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class ClickSounds(
    val strongBeat: ClickSoundSource?,
    val weakBeat: ClickSoundSource?,
    val strongBeatAlternative: ClickSoundSource? = null,
) : Parcelable {

    val asIterable: Iterable<ClickSoundSource> get() = listOfNotNull(strongBeat, weakBeat, strongBeatAlternative)

    fun beatByType(type: ClickSoundType) = when (type) {
        ClickSoundType.STRONG -> strongBeat
        ClickSoundType.WEAK -> weakBeat
    }
}
