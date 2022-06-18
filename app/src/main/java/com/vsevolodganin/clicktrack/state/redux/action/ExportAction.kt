package com.vsevolodganin.clicktrack.state.redux.action

import com.vsevolodganin.clicktrack.lib.ClickTrack
import com.vsevolodganin.clicktrack.state.redux.core.Action

sealed interface ExportAction {

    class Start(val clickTrack: ClickTrack) : Action

    object Stop : Action
}
