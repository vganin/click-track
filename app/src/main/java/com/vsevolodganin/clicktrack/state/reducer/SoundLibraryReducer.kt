package com.vsevolodganin.clicktrack.state.reducer

import com.vsevolodganin.clicktrack.redux.Action
import com.vsevolodganin.clicktrack.state.actions.SoundLibraryAction
import com.vsevolodganin.clicktrack.state.screen.Screen
import com.vsevolodganin.clicktrack.state.screen.SoundLibraryScreenState

fun Screen.SoundLibrary.reduceSoundLibraryScreen(action: Action): Screen {
    return Screen.SoundLibrary(
        state = state.reduce(action)
    )
}

private fun SoundLibraryScreenState?.reduce(action: Action): SoundLibraryScreenState? {
    return when (action) {
        is SoundLibraryAction.UpdateClickSoundsList -> SoundLibraryScreenState(action.items)
        else -> this
    }
}
