package com.vsevolodganin.clicktrack.soundlibrary

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Parcelable
import android.provider.DocumentsContract
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import com.arkivanov.essenty.statekeeper.StateKeeperOwner
import com.arkivanov.essenty.statekeeper.consume
import com.vsevolodganin.clicktrack.di.component.MainControllerScope
import com.vsevolodganin.clicktrack.model.ClickSoundSource
import com.vsevolodganin.clicktrack.model.ClickSoundType
import com.vsevolodganin.clicktrack.model.ClickSoundsId
import com.vsevolodganin.clicktrack.storage.ClickSoundsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.parcelize.Parcelize
import me.tatarka.inject.annotations.Inject

@MainControllerScope
@Inject
class SoundChooserImpl(
    private val activity: AppCompatActivity,
    private val clickSoundsRepository: ClickSoundsRepository,
    stateKeeperOwner: StateKeeperOwner,
) : SoundChooser {
    private val pendingRequestState = MutableStateFlow<OpenAudioRequest?>(
        stateKeeperOwner.stateKeeper.consume(OpenAudioRequest.SAVED_STATE_KEY)
    )

    init {
        stateKeeperOwner.stateKeeper.register(OpenAudioRequest.SAVED_STATE_KEY) {
            pendingRequestState.value
        }
    }

    private val launcher = activity.registerForActivityResult(OpenAudio()) { uri ->
        val request = pendingRequestState.value
        if (uri != null && request != null) {
            obtainPersistentPermissions(uri)
            pendingRequestState.value = null
            clickSoundsRepository.update(request.id, request.type, ClickSoundSource.Uri(uri.toString()))
        }
    }

    override suspend fun launchFor(id: ClickSoundsId.Database, type: ClickSoundType) {
        val initialUri = getInitialUri(id, type)
        pendingRequestState.value = OpenAudioRequest(id, type)
        launcher.launch(initialUri)
    }

    private suspend fun getInitialUri(id: ClickSoundsId, type: ClickSoundType): Uri? {
        return when (id) {
            is ClickSoundsId.Builtin -> null
            is ClickSoundsId.Database -> clickSoundsRepository.getById(id).firstOrNull()?.value?.beatByType(type)?.value
        }?.let(Uri::parse)
    }

    private fun obtainPersistentPermissions(uri: Uri) {
        val contentResolver = activity.contentResolver
        contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    @Parcelize
    private class OpenAudioRequest(
        val id: ClickSoundsId.Database,
        val type: ClickSoundType,
    ) : Parcelable {

        companion object {
            const val SAVED_STATE_KEY = "open_audio_request"
        }
    }

    private class OpenAudio : ActivityResultContract<Uri?, Uri?>() {

        override fun createIntent(context: Context, input: Uri?): Intent {
            return Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                type = "audio/*"
                addCategory(Intent.CATEGORY_OPENABLE)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    putExtra(DocumentsContract.EXTRA_INITIAL_URI, input)
                }
            }
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return intent?.takeIf { resultCode == Activity.RESULT_OK }?.data
        }

        override fun getSynchronousResult(context: Context, input: Uri?): SynchronousResult<Uri?>? = null
    }
}
