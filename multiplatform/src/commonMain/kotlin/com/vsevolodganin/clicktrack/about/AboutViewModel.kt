package com.vsevolodganin.clicktrack.about

import kotlinx.coroutines.flow.StateFlow

interface AboutViewModel {
    val state: StateFlow<AboutState>

    fun onBackClick()

    fun onHomeClick()

    fun onTwitterClick()

    fun onEmailClick()

    fun onArtstationClick()

    fun onProjectLinkClick()
}
