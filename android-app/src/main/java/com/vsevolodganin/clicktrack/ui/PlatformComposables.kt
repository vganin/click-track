package com.vsevolodganin.clicktrack.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.vsevolodganin.clicktrack.edit.EditClickTrackViewModel
import com.vsevolodganin.clicktrack.ui.screen.EditClickTrackScreenView

object PlatformComposables : ComposableProvider {
    override val editClickTrack: @Composable (EditClickTrackViewModel, Modifier) -> Unit
        get() = @Composable { viewModel, modifier -> EditClickTrackScreenView(viewModel, modifier) }
}
