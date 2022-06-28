package com.vsevolodganin.clicktrack.redux.action

import com.vsevolodganin.clicktrack.model.ClickTrackId
import com.vsevolodganin.clicktrack.redux.core.Action

sealed interface PlayerAction : Action {

    class StartPlayClickTrack(val id: ClickTrackId, val progress: Double? = null) : PlayerAction

    object StartPlayPolyrhythm : PlayerAction

    object StopPlay : PlayerAction

    object PausePlay : PlayerAction
}
