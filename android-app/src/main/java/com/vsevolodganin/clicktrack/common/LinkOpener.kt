package com.vsevolodganin.clicktrack.common

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.vsevolodganin.clicktrack.di.component.ActivityScope
import javax.inject.Inject

@ActivityScope
class LinkOpener @Inject constructor(
    private val context: Activity,
) {
    fun url(url: String) {
        val uri: Uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivityIfAble(intent)
    }

    fun email(email: String) {
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
