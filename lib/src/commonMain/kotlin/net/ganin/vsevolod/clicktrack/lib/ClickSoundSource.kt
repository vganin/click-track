package net.ganin.vsevolod.clicktrack.lib

import kotlinx.serialization.Serializable
import net.ganin.vsevolod.clicktrack.lib.android.AndroidParcelable
import net.ganin.vsevolod.clicktrack.lib.android.AndroidParcelize

@Serializable
public sealed class ClickSoundSource : AndroidParcelable {

    @Serializable
    @AndroidParcelize
    public object Builtin : ClickSoundSource()

    @Serializable
    @AndroidParcelize
    public data class Uri(val value: String) : ClickSoundSource()
}
