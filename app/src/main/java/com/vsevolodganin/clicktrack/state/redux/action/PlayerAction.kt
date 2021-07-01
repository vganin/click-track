package com.vsevolodganin.clicktrack.state.redux.action

import com.vsevolodganin.clicktrack.model.ClickTrackId
import com.vsevolodganin.clicktrack.state.redux.core.Action

sealed interface PlayerAction : Action {

    class StartPlay(val id: ClickTrackId, val progress: Double? = null) : PlayerAction

    object StopPlay : PlayerAction

    object PausePlay : PlayerAction
}
