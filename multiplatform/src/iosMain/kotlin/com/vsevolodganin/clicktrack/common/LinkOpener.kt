package com.vsevolodganin.clicktrack.common

import com.vsevolodganin.clicktrack.di.component.MainControllerScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import platform.Foundation.NSURL.Companion.URLWithString
import platform.UIKit.UIApplication

@SingleIn(MainControllerScope::class)
@Inject
actual class LinkOpener {
    actual fun url(url: String) {
        UIApplication.sharedApplication.openURL(URLWithString(url)!!)
    }

    actual fun email(email: String) {
        UIApplication.sharedApplication.openURL(URLWithString("mailto:$email")!!)
    }
}
