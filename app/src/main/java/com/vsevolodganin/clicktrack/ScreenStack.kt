package com.vsevolodganin.clicktrack

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.value.Value

typealias ScreenStack = @JvmSuppressWildcards ChildStack<ScreenConfiguration, ScreenViewModel>
typealias ScreenStackState = @JvmSuppressWildcards Value<ScreenStack>
typealias ScreenStackNavigation = @JvmSuppressWildcards StackNavigation<ScreenConfiguration>
