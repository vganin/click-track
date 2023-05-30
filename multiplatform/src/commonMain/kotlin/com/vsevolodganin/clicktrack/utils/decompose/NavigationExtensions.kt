package com.vsevolodganin.clicktrack.utils.decompose

import com.arkivanov.decompose.router.stack.navigate
import com.vsevolodganin.clicktrack.ScreenConfiguration
import com.vsevolodganin.clicktrack.ScreenStackNavigation

fun ScreenStackNavigation.resetTo(config: ScreenConfiguration) {
    navigate { stack -> listOf(stack.first(), config) }
}

fun ScreenStackNavigation.pushIfUnique(config: ScreenConfiguration) {
    navigate(transformer = { stack -> if (stack.last() == config) stack else stack + config })
}
