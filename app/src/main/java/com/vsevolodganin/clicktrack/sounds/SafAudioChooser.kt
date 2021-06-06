package com.vsevolodganin.clicktrack.sounds

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import com.vsevolodganin.clicktrack.di.component.ActivityScoped
import javax.inject.Inject
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope

@ActivityScoped
class SafAudioChooser @Inject constructor(
    private val activity: AppCompatActivity,
) {
    private val resultChannel = Channel<Uri?>()
    private val intentLauncher = activity.registerForActivityResult(OpenAudio(), resultChannel::trySend)

    suspend fun chooseAudio(initialUri: String?): Uri? = coroutineScope {
        val resultAsync = async(start = CoroutineStart.UNDISPATCHED) {
            resultChannel.receive()
        }
        intentLauncher.launch(initialUri)
        resultAsync.await()?.also {
            obtainPersistentPermissions(it)
        }
    }

    private fun obtainPersistentPermissions(uri: Uri) {
        val contentResolver = activity.contentResolver
        contentResolver.takePersistableUriPermission(uri, FLAG_GRANT_READ_URI_PERMISSION)
    }

    private class OpenAudio : ActivityResultContract<String?, Uri?>() {

        override fun createIntent(context: Context, input: String?): Intent {
            return Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && input != null) {
                    putExtra(DocumentsContract.EXTRA_INITIAL_URI, Uri.parse(input))
                }
                type = "audio/*"
            }
        }

        override fun getSynchronousResult(context: Context, input: String?): SynchronousResult<Uri?>? = null

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return if (intent == null || resultCode != Activity.RESULT_OK) null else intent.data
        }
    }
}
