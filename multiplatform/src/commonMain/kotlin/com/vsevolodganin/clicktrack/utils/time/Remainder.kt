package com.vsevolodganin.clicktrack.utils.time

import kotlin.time.Duration
import kotlin.time.Duration.Companion.nanoseconds

operator fun Duration.rem(duration: Duration): Duration {
    return (this.inWholeNanoseconds % duration.inWholeNanoseconds).nanoseconds
}
