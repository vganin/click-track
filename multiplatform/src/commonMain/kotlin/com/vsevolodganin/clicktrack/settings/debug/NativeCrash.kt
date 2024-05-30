package com.vsevolodganin.clicktrack.settings.debug

interface NativeCrash {
    fun exception()

    fun danglingReference()
}
