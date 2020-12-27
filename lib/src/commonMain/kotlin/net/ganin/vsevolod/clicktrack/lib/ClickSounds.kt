package net.ganin.vsevolod.clicktrack.lib

import kotlinx.serialization.Serializable
import net.ganin.vsevolod.clicktrack.lib.android.AndroidParcelable
import net.ganin.vsevolod.clicktrack.lib.android.AndroidParcelize

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
