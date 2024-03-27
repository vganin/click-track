@file:Suppress("DEPRECATION") // FIXME: Look into Parcelize not working with K2

package com.vsevolodganin.clicktrack.utils.decompose

import com.arkivanov.essenty.statekeeper.StateKeeperOwner
import com.arkivanov.essenty.statekeeper.consume
import com.vsevolodganin.clicktrack.utils.parcelable.Parcelable
import kotlinx.coroutines.flow.StateFlow

inline fun <reified T : Parcelable> StateKeeperOwner.consumeSavedState(): T? = stateKeeper.consume(STATE_KEY)

fun <T : Parcelable?> StateKeeperOwner.registerSaveStateFor(stateFlow: StateFlow<T>) = stateKeeper.register(STATE_KEY) { stateFlow.value }

const val STATE_KEY = "state"
