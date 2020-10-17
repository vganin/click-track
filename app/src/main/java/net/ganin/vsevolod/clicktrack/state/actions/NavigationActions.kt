package net.ganin.vsevolod.clicktrack.state.actions

import net.ganin.vsevolod.clicktrack.model.ClickTrackWithId
import net.ganin.vsevolod.clicktrack.redux.Action

sealed class NavigationAction : Action

/** Special action to exit app. Treated in activity. */
object FinishApp : Action

object NavigateBack : NavigationAction()

class NavigateToClickTrackListScreen(val data: List<ClickTrackWithId>) : NavigationAction()

class NavigateToClickTrackScreen(val data: ClickTrackWithId) : NavigationAction()

class NavigateToEditClickTrackScreen(val data: ClickTrackWithId) : NavigationAction()
