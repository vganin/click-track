package com.vsevolodganin.clicktrack.utils.android

import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.appcompat.app.AppCompatActivity
import com.vsevolodganin.clicktrack.di.component.MainControllerScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import me.tatarka.inject.annotations.Inject

@MainControllerScope
@Inject
class PermissionsHelper(
    activity: AppCompatActivity
) {
    // FIXME: Fix process restoration case

    private val resultChannel = Channel<Map<String, Boolean>>()
    private val launcher = activity.registerForActivityResult(RequestMultiplePermissions(), resultChannel::trySend)

    suspend fun requestPermission(permission: String): Boolean? = coroutineScope {
        requestPermissions(arrayOf(permission))[permission]
    }

    suspend fun requestPermissions(permissions: Array<String>): Map<String, Boolean> = coroutineScope {
        val resultAsync = async(start = CoroutineStart.UNDISPATCHED) {
            resultChannel.receive()
        }
        launcher.launch(permissions)
        resultAsync.await()
    }
}
