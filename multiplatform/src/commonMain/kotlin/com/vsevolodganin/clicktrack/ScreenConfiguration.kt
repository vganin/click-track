package com.vsevolodganin.clicktrack

import com.vsevolodganin.clicktrack.model.ClickTrackId
import com.vsevolodganin.clicktrack.utils.parcelable.Parcelable
import com.vsevolodganin.clicktrack.utils.parcelable.Parcelize

sealed interface ScreenConfiguration : Parcelable {
    @Parcelize
    object ClickTrackList : ScreenConfiguration

    @Parcelize
    data class PlayClickTrack(val id: ClickTrackId.Database) : ScreenConfiguration

    @Parcelize
    data class EditClickTrack(val id: ClickTrackId.Database, val isInitialEdit: Boolean) : ScreenConfiguration

    @Parcelize
    object Metronome : ScreenConfiguration

    @Parcelize
    object Training : ScreenConfiguration

    @Parcelize
    object Settings : ScreenConfiguration

    @Parcelize
    object SoundLibrary : ScreenConfiguration

    @Parcelize
    object About : ScreenConfiguration

    @Parcelize
    object Polyrhythms : ScreenConfiguration
}
