package com.vsevolodganin.clicktrack.lib

import com.vsevolodganin.clicktrack.lib.android.AndroidParcelable
import com.vsevolodganin.clicktrack.lib.android.AndroidParcelize
import kotlinx.serialization.Serializable

@Serializable
public sealed class ClickSoundSource : AndroidParcelable {

    @Serializable
    @AndroidParcelize
    public object BuiltinStrong : ClickSoundSource()

    @Serializable
    @AndroidParcelize
    public object BuiltinWeak : ClickSoundSource()

    @Serializable
    @AndroidParcelize
    public data class File(val path: String) : ClickSoundSource()
}
