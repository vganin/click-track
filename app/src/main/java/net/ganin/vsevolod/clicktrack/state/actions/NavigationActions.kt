package net.ganin.vsevolod.clicktrack.state.actions

import net.ganin.vsevolod.clicktrack.lib.ClickTrack
import net.ganin.vsevolod.clicktrack.lib.ClickTrackWithMeta
import net.ganin.vsevolod.clicktrack.redux.Action

sealed class NavigationAction : Action

/** Special action to exit app. Treated in activity. */
object FinishApp : Action

object NavigateBack : NavigationAction()

class NavigateToClickTrackListScreen(val data: List<ClickTrackWithMeta>) : NavigationAction()

class NavigateToClickTrackScreen(val data: ClickTrack) : NavigationAction()

class NavigateToAddNewClickTrackScreen(val initialClickTrack: ClickTrackWithMeta) : NavigationAction()
