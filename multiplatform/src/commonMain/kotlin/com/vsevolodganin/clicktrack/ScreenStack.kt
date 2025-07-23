package com.vsevolodganin.clicktrack

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.value.Value

typealias ScreenStack = ChildStack<ScreenConfiguration, ScreenViewModel>
typealias ScreenStackState = Value<ScreenStack>
typealias ScreenStackNavigation = StackNavigation<ScreenConfiguration>
