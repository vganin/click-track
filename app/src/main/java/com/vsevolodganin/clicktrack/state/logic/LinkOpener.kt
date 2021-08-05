package com.vsevolodganin.clicktrack.state.logic

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.vsevolodganin.clicktrack.di.component.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class LinkOpener @Inject constructor(
    private val context: Activity,
) {
    fun url(url: String) {
        val uri: Uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        }
    }

    fun email(email: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        }
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        }
    }
}
