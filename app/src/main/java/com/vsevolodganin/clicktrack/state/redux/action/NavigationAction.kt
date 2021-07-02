package com.vsevolodganin.clicktrack.state.redux.action

import com.vsevolodganin.clicktrack.model.ClickTrackId
import com.vsevolodganin.clicktrack.model.ClickTrackWithDatabaseId
import com.vsevolodganin.clicktrack.state.redux.core.Action

/** Special action to exit app. Treated in activity. */
object FinishApp : Action

object OpenDrawer : Action

object CloseDrawer : Action

sealed interface NavigationAction : Action {

    object Back : NavigationAction

    object ToClickTrackListScreen : NavigationAction

    class ToClickTrackScreen(val id: ClickTrackId.Database) : NavigationAction

    class ToEditClickTrackScreen(val clickTrack: ClickTrackWithDatabaseId) : NavigationAction

    object ToMetronomeScreen : NavigationAction

    object ToSettingsScreen : NavigationAction

    object ToSoundLibraryScreen : NavigationAction
}