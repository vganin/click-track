package com.vsevolodganin.clicktrack.utils.time

import kotlin.time.Duration

operator fun Duration.rem(duration: Duration): Duration {
    return Duration.nanoseconds((this.inWholeNanoseconds % duration.inWholeNanoseconds))
}
