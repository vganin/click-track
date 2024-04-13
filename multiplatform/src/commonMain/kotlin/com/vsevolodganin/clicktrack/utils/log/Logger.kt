package com.vsevolodganin.clicktrack.utils.log

import com.vsevolodganin.clicktrack.utils.keep.Keep

@Keep
interface Logger {
    fun logError(tag: String, message: String)
    fun logError(tag: String, message: String, throwable: Throwable?)
}
