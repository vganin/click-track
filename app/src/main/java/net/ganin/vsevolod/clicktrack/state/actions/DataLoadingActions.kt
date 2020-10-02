package net.ganin.vsevolod.clicktrack.state.actions

import net.ganin.vsevolod.clicktrack.lib.ClickTrackWithMeta
import net.ganin.vsevolod.clicktrack.redux.Action

object LoadDataAction : Action

class DataLoadedAction(val data: List<ClickTrackWithMeta>) : Action
