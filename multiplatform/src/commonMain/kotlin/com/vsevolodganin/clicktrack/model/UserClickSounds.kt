package com.vsevolodganin.clicktrack.model

import kotlinx.serialization.Serializable

@Serializable
data class UserClickSounds(
    val id: ClickSoundsId.Database,
    val value: ClickSounds,
)
