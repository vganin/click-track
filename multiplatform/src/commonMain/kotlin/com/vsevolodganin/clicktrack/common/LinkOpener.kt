package com.vsevolodganin.clicktrack.common

import com.vsevolodganin.clicktrack.di.component.MainControllerScope
import dev.zacsweers.metro.Inject

@MainControllerScope
@Inject
expect class LinkOpener {
    fun url(url: String)

    fun email(email: String)
}
