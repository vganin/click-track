package com.vsevolodganin.clicktrack.utils.decompose

import com.arkivanov.essenty.statekeeper.StateKeeperOwner
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.serializer

inline fun <reified T> StateKeeperOwner.consumeSavedState(): T? =
    stateKeeper.consume(STATE_KEY, serializer())

inline fun <reified T> StateKeeperOwner.registerSaveStateFor(stateFlow: StateFlow<T>) =
    stateKeeper.register(STATE_KEY, serializer()) { stateFlow.value }

const val STATE_KEY = "state"
