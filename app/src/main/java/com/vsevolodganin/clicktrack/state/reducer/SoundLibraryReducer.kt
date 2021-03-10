package com.vsevolodganin.clicktrack.state.reducer

import com.vsevolodganin.clicktrack.redux.Action
import com.vsevolodganin.clicktrack.state.Screen
import com.vsevolodganin.clicktrack.state.SoundLibraryState
import com.vsevolodganin.clicktrack.state.actions.SoundLibraryAction

fun Screen.SoundLibrary.reduceSoundLibraryScreen(action: Action): Screen {
    return Screen.SoundLibrary(
        state = state.reduce(action)
    )
}

private fun SoundLibraryState?.reduce(action: Action): SoundLibraryState? {
    return when (action) {
        is SoundLibraryAction.UpdateClickSoundsList -> SoundLibraryState(action.items)
        else -> this
    }
}
