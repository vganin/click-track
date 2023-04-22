package com.vsevolodganin.clicktrack.model

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize

@Parcelize
data class UserClickSounds(
    val id: ClickSoundsId.Database,
    val value: UriClickSounds,
) : Parcelable
