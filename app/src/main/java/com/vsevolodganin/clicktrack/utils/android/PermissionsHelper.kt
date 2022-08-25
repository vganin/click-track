package com.vsevolodganin.clicktrack.utils.android

import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.appcompat.app.AppCompatActivity
import com.vsevolodganin.clicktrack.di.component.ActivityScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

@ActivityScope
class PermissionsHelper @Inject constructor(activity: AppCompatActivity) {
    // FIXME: Fix process restoration case

    private val resultChannel = Channel<Map<String, Boolean>>()
    private val launcher = activity.registerForActivityResult(RequestMultiplePermissions(), resultChannel::trySend)

    suspend fun requestPermission(permission: String) = coroutineScope {
        requestPermissions(arrayOf(permission))[permission]!!
    }

    suspend fun requestPermissions(permissions: Array<String>) = coroutineScope {
        val resultAsync = async(start = CoroutineStart.UNDISPATCHED) {
            resultChannel.receive()
        }
        launcher.launch(permissions)
        resultAsync.await()
    }
}
