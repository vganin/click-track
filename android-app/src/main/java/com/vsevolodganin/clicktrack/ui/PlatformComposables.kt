package com.vsevolodganin.clicktrack.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.vsevolodganin.clicktrack.edit.EditClickTrackViewModel
import com.vsevolodganin.clicktrack.play.PlayClickTrackViewModel
import com.vsevolodganin.clicktrack.soundlibrary.SoundLibraryViewModel
import com.vsevolodganin.clicktrack.training.TrainingViewModel
import com.vsevolodganin.clicktrack.ui.screen.EditClickTrackScreenView
import com.vsevolodganin.clicktrack.ui.screen.PlayClickTrackScreenView
import com.vsevolodganin.clicktrack.ui.screen.SoundLibraryScreenView
import com.vsevolodganin.clicktrack.ui.screen.TrainingScreenView

object PlatformComposables : ComposableProvider {
    override val playClickTrack: @Composable (PlayClickTrackViewModel, Modifier) -> Unit
        get() = @Composable { viewModel, modifier -> PlayClickTrackScreenView(viewModel, modifier) }
    override val editClickTrack: @Composable (EditClickTrackViewModel, Modifier) -> Unit
        get() = @Composable { viewModel, modifier -> EditClickTrackScreenView(viewModel, modifier) }
    override val soundLibrary: @Composable (SoundLibraryViewModel, Modifier) -> Unit
        get() = @Composable { viewModel, modifier -> SoundLibraryScreenView(viewModel, modifier) }
    override val training: @Composable (TrainingViewModel, Modifier) -> Unit
        get() = @Composable { viewModel, modifier -> TrainingScreenView(viewModel, modifier) }
}
