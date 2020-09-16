package net.ganin.vsevolod.clicktrack.state.actions

import net.ganin.vsevolod.clicktrack.lib.ClickTrack
import net.ganin.vsevolod.clicktrack.redux.Action

object LoadDataAction : Action

class DataLoadedAction(val data: List<ClickTrack>) : Action
