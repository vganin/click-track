package com.vsevolodganin.clicktrack.lib

import com.vsevolodganin.clicktrack.lib.android.AndroidParcelable
import com.vsevolodganin.clicktrack.lib.android.AndroidParcelize
import kotlinx.serialization.Serializable

@Serializable
@AndroidParcelize
public data class ClickSounds(
    val strongBeat: ClickSoundSource,
    val weakBeat: ClickSoundSource,
) : AndroidParcelable

public val BuiltinClickSounds: ClickSounds = ClickSounds(
    strongBeat = ClickSoundSource.Builtin,
    weakBeat = ClickSoundSource.Builtin,
)
