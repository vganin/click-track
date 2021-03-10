package com.vsevolodganin.clicktrack.state.actions

import com.vsevolodganin.clicktrack.model.ClickTrackWithId
import com.vsevolodganin.clicktrack.redux.Action

/** Special action to exit app. Treated in activity. */
object FinishApp : Action

object OpenDrawer : Action

object CloseDrawer : Action

sealed class NavigationAction : Action {

    object Back : NavigationAction()

    class ToClickTrackListScreen(val clickTrack: List<ClickTrackWithId>) : NavigationAction()

    class ToClickTrackScreen(val clickTrack: ClickTrackWithId) : NavigationAction()

    class ToEditClickTrackScreen(val clickTrack: ClickTrackWithId) : NavigationAction()

    object ToMetronomeScreen : NavigationAction()

    object ToSettingsScreen : NavigationAction()

    object ToSoundLibraryScreen : NavigationAction()
}
