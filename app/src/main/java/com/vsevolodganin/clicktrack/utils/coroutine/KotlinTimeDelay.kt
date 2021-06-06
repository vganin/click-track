package com.vsevolodganin.clicktrack.utils.coroutine

import kotlin.time.Duration

suspend fun delay(duration: Duration) = kotlinx.coroutines.delay(duration.inWholeMilliseconds)
