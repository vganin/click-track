package com.vsevolodganin.clicktrack

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import com.vsevolodganin.clicktrack.about.AboutState
import com.vsevolodganin.clicktrack.about.AboutViewModel
import com.vsevolodganin.clicktrack.ui.screen.AboutScreenView
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

fun MainViewController() = ComposeUIViewController {
    AboutScreenView(
        viewModel = object : AboutViewModel {
            override val state: StateFlow<AboutState> = MutableStateFlow(
                AboutState(
                    displayVersion = "6.6.6"
                )
            )

            override fun onBackClick() = Unit
            override fun onHomeClick() = Unit
            override fun onTwitterClick() = Unit
            override fun onEmailClick() = Unit
            override fun onArtstationClick() = Unit
            override fun onProjectLinkClick() = Unit
        },
        modifier = Modifier.fillMaxSize()
    )
}
