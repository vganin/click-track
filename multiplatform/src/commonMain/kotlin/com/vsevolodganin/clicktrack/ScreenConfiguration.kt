package com.vsevolodganin.clicktrack

import com.vsevolodganin.clicktrack.model.ClickTrackId
import kotlinx.serialization.Serializable

@Serializable
sealed interface ScreenConfiguration {
    @Serializable
    object ClickTrackList : ScreenConfiguration

    @Serializable
    data class PlayClickTrack(val id: ClickTrackId.Database) : ScreenConfiguration

    @Serializable
    data class EditClickTrack(val id: ClickTrackId.Database, val isInitialEdit: Boolean) : ScreenConfiguration

    @Serializable
    object Metronome : ScreenConfiguration

    @Serializable
    object Training : ScreenConfiguration

    @Serializable
    object Settings : ScreenConfiguration

    @Serializable
    object SoundLibrary : ScreenConfiguration

    @Serializable
    object About : ScreenConfiguration

    @Serializable
    object Polyrhythms : ScreenConfiguration
}
