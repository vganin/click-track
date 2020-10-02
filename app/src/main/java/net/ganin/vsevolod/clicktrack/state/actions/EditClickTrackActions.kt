package net.ganin.vsevolod.clicktrack.state.actions

import net.ganin.vsevolod.clicktrack.lib.ClickTrackWithMeta
import net.ganin.vsevolod.clicktrack.redux.Action

class SaveClickTrack(val clickTrack: ClickTrackWithMeta) : Action
