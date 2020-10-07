package net.ganin.vsevolod.clicktrack.state.actions

import net.ganin.vsevolod.clicktrack.lib.ClickTrackWithMeta
import net.ganin.vsevolod.clicktrack.redux.Action

object ClickTrackListLoadRequestAction : Action
class ClickTrackListDataLoadedAction(val data: List<ClickTrackWithMeta>) : Action

class ClickTrackLoadRequestAction(val name: String) : Action
class ClickTrackDataLoadedAction(val data: ClickTrackWithMeta) : Action
