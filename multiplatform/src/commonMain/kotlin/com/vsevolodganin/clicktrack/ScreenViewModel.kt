package com.vsevolodganin.clicktrack

import com.vsevolodganin.clicktrack.about.AboutViewModel
import com.vsevolodganin.clicktrack.edit.EditClickTrackViewModel
import com.vsevolodganin.clicktrack.list.ClickTrackListViewModel
import com.vsevolodganin.clicktrack.metronome.MetronomeViewModel
import com.vsevolodganin.clicktrack.play.PlayClickTrackViewModel
import com.vsevolodganin.clicktrack.polyrhythm.PolyrhythmsViewModel
import com.vsevolodganin.clicktrack.settings.SettingsViewModel
import com.vsevolodganin.clicktrack.soundlibrary.SoundLibraryViewModel
import com.vsevolodganin.clicktrack.training.TrainingViewModel

sealed interface ScreenViewModel {
    class ClickTrackList(val value: ClickTrackListViewModel) : ScreenViewModel

    class PlayClickTrack(val value: PlayClickTrackViewModel) : ScreenViewModel

    class EditClickTrack(val value: EditClickTrackViewModel) : ScreenViewModel

    class Metronome(val value: MetronomeViewModel) : ScreenViewModel

    class Training(val value: TrainingViewModel) : ScreenViewModel

    class Settings(val value: SettingsViewModel) : ScreenViewModel

    class SoundLibrary(val value: SoundLibraryViewModel) : ScreenViewModel

    class About(val value: AboutViewModel) : ScreenViewModel

    class Polyrhythms(val value: PolyrhythmsViewModel) : ScreenViewModel
}
