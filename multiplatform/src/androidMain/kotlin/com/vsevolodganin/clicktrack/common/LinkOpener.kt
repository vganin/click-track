package com.vsevolodganin.clicktrack.common

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.vsevolodganin.clicktrack.di.component.MainControllerScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@SingleIn(MainControllerScope::class)
@Inject
actual class LinkOpener(private val context: Activity) {
    actual fun url(url: String) {
        val uri: Uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivityIfAble(intent)
    }

    actual fun email(email: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        }
        context.startActivityIfAble(intent)
    }

    private fun Context.startActivityIfAble(intent: Intent) {
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // Ignoring
        }
    }
}
