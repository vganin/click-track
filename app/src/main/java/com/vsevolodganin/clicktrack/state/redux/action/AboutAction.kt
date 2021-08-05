package com.vsevolodganin.clicktrack.state.redux.action

import com.vsevolodganin.clicktrack.state.redux.core.Action

sealed interface AboutAction : Action {

    object SendEmail : AboutAction

    object GoHomePage : AboutAction

    object GoTwitter : AboutAction

    object GoArtstation : AboutAction
}
