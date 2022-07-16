package com.vsevolodganin.clicktrack.ui.model

sealed interface UiScreen {

    val key: Any get() = javaClass

    data class ClickTrackList(val state: ClickTrackListUiState) : UiScreen

    data class PlayClickTrack(val state: PlayClickTrackUiState) : UiScreen {
        override val key: Any get() = super.key to state.clickTrack.id
    }

    data class EditClickTrack(val state: EditClickTrackUiState) : UiScreen {
        override val key: Any get() = super.key to state.id
    }

    data class Metronome(val state: MetronomeUiState) : UiScreen

    data class Training(val state: TrainingUiState) : UiScreen

    data class Settings(val state: SettingsUiState) : UiScreen

    data class SoundLibrary(val state: SoundLibraryUiState) : UiScreen

    data class About(val state: AboutUiState) : UiScreen

    data class Polyrhythms(val state: PolyrhythmsUiState) : UiScreen
}
