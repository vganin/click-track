package com.vsevolodganin.clicktrack.utils.decompose

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.statekeeper.StateKeeper

fun createComponentContext(stateKeeper: StateKeeper): ComponentContext = DefaultComponentContext(
    lifecycle = LifecycleRegistry(),
    stateKeeper = stateKeeper,
)
