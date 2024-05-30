package com.vsevolodganin.clicktrack.common

import com.vsevolodganin.clicktrack.di.component.MainControllerScope
import me.tatarka.inject.annotations.Inject
import platform.Foundation.NSURL.Companion.URLWithString
import platform.UIKit.UIApplication

@MainControllerScope
@Inject
actual class LinkOpener {
    actual fun url(url: String) {
        UIApplication.sharedApplication.openURL(URLWithString(url)!!)
    }

    actual fun email(email: String) {
        UIApplication.sharedApplication.openURL(URLWithString("mailto:$email")!!)
    }
}
