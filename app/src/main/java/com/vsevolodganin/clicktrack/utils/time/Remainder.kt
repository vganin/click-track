package com.vsevolodganin.clicktrack.utils.time

import kotlin.time.Duration
import kotlin.time.nanoseconds

operator fun Duration.rem(duration: Duration): Duration {
    return (this.toLongNanoseconds() % duration.toLongNanoseconds()).nanoseconds
}
