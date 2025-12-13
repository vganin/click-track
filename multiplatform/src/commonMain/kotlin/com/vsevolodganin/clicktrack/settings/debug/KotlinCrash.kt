package com.vsevolodganin.clicktrack.settings.debug

import dev.zacsweers.metro.Inject

@Inject
class KotlinCrash {
    operator fun invoke() {
        throw RuntimeException("Test")
    }
}
