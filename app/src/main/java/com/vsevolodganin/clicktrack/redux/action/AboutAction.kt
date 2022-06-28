package com.vsevolodganin.clicktrack.redux.action

import com.vsevolodganin.clicktrack.redux.core.Action

sealed interface AboutAction : Action {

    object SendEmail : AboutAction

    object GoHomePage : AboutAction

    object GoTwitter : AboutAction

    object GoArtstation : AboutAction
}
