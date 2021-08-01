package com.vsevolodganin.clicktrack.ui.model

sealed interface UiScreen {

    data class ClickTrackList(val state: ClickTrackListUiState) : UiScreen

    data class PlayClickTrack(val state: PlayClickTrackUiState) : UiScreen

    data class EditClickTrack(val state: EditClickTrackUiState) : UiScreen

    data class Metronome(val state: MetronomeUiState) : UiScreen

    data class Training(val state: TrainingUiState) : UiScreen

    data class Settings(val state: SettingsUiState) : UiScreen

    data class SoundLibrary(val state: SoundLibraryUiState) : UiScreen
}
