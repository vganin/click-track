package com.vsevolodganin.clicktrack.state.redux.action

import com.vsevolodganin.clicktrack.model.ClickTrackId
import com.vsevolodganin.clicktrack.state.redux.core.Action

sealed interface ExportAction {
    class Start(val clickTrackId: ClickTrackId.Database) : Action
    class Stop(val clickTrackId: ClickTrackId.Database) : Action
}
