package com.vsevolodganin.clicktrack.drawer

import kotlinx.coroutines.flow.StateFlow

interface DrawerViewModel {
    val state: StateFlow<DrawerState>

    fun navigateToMetronome()

    fun navigateToTraining()

    fun navigateToPolyrhythms()

    fun navigateToSoundLibrary()

    fun navigateToSettings()

    fun navigateToAbout()

    fun openDrawer()

    fun closeDrawer()
}
