package com.vsevolodganin.clicktrack.settings.debug

import me.tatarka.inject.annotations.Inject

@Inject
class KotlinCrash {
    operator fun invoke() {
        throw RuntimeException("Test")
    }
}
