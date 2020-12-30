package com.vsevolodganin.clicktrack.lib

import com.vsevolodganin.clicktrack.lib.android.AndroidParcelable
import com.vsevolodganin.clicktrack.lib.android.AndroidParcelize
import kotlinx.serialization.Serializable

@Serializable
public sealed class ClickSoundSource : AndroidParcelable {

    @Serializable
    @AndroidParcelize
    public object Builtin : ClickSoundSource()

    @Serializable
    @AndroidParcelize
    public data class Uri(val value: String) : ClickSoundSource()
}
