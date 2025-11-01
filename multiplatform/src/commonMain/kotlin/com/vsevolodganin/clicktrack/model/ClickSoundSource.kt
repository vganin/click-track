package com.vsevolodganin.clicktrack.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class ClickSoundSource(
    @JsonNames("value") // Alias for backward compatibility
    val uri: String,
)
