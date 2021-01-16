package com.vsevolodganin.clicktrack.state.actions

import com.vsevolodganin.clicktrack.model.ClickTrackWithId
import com.vsevolodganin.clicktrack.redux.Action

sealed class NavigationAction : Action

/** Special action to exit app. Treated in activity. */
object FinishApp : Action

object NavigateBack : NavigationAction()

class NavigateToClickTrackListScreen(val clickTrack: List<ClickTrackWithId>) : NavigationAction()

class NavigateToClickTrackScreen(val clickTrack: ClickTrackWithId) : NavigationAction()

class NavigateToEditClickTrackScreen(val clickTrack: ClickTrackWithId) : NavigationAction()

object NavigateToMetronomeScreen : NavigationAction()