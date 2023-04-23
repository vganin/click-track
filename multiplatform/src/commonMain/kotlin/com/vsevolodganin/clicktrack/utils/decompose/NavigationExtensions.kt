package com.vsevolodganin.clicktrack.utils.decompose

import com.arkivanov.decompose.router.stack.navigate
import com.vsevolodganin.clicktrack.Navigation
import com.vsevolodganin.clicktrack.ScreenConfiguration

fun Navigation.resetTo(config: ScreenConfiguration) {
    closeDrawer()
    navigate { stack -> listOf(stack.first(), config) }
}

fun Navigation.pushIfUnique(config: ScreenConfiguration) {
    navigate(transformer = { stack -> if (stack.last() == config) stack else stack + config })
}
