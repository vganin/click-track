package com.vsevolodganin.clicktrack.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.vsevolodganin.clicktrack.edit.EditClickTrackViewModel
import com.vsevolodganin.clicktrack.play.PlayClickTrackViewModel
import com.vsevolodganin.clicktrack.ui.screen.EditClickTrackScreenView
import com.vsevolodganin.clicktrack.ui.screen.PlayClickTrackScreenView

object PlatformComposables : ComposableProvider {
    override val playClickTrack: @Composable (PlayClickTrackViewModel, Modifier) -> Unit
        get() = @Composable { viewModel, modifier -> PlayClickTrackScreenView(viewModel, modifier) }
    override val editClickTrack: @Composable (EditClickTrackViewModel, Modifier) -> Unit
        get() = @Composable { viewModel, modifier -> EditClickTrackScreenView(viewModel, modifier) }
}
