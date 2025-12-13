package com.vsevolodganin.clicktrack.common

expect class LinkOpener {
    fun url(url: String)
    fun email(email: String)
}
