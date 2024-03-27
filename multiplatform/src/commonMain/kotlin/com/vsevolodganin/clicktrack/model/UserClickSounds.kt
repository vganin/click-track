package com.vsevolodganin.clicktrack.model

import com.vsevolodganin.clicktrack.utils.parcelable.Parcelable
import com.vsevolodganin.clicktrack.utils.parcelable.Parcelize

@Parcelize
data class UserClickSounds(
    val id: ClickSoundsId.Database,
    val value: UriClickSounds,
) : Parcelable
