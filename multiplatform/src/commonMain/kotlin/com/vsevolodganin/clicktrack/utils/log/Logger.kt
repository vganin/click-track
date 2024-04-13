package com.vsevolodganin.clicktrack.utils.log

interface Logger {
    fun logError(tag: String, message: String)
    fun logError(tag: String, message: String, throwable: Throwable?)
}
