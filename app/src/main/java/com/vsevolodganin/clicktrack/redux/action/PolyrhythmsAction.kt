package com.vsevolodganin.clicktrack.redux.action

import com.vsevolodganin.clicktrack.redux.core.Action

sealed interface PolyrhythmsAction : Action {

    class EditLayer1(val number: Int) : PolyrhythmsAction

    class EditLayer2(val number: Int) : PolyrhythmsAction
}
