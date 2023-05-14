package com.vsevolodganin.clicktrack.utils.resources

import android.app.Activity
import com.vsevolodganin.clicktrack.di.component.MainControllerScope
import dev.icerock.moko.resources.StringResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

@Inject
@MainControllerScope
actual class StringResolver(private val context: Activity) {
    actual suspend fun resolve(resource: StringResource): String {
        return withContext(Dispatchers.Main.immediate) {
            resource.getString(context)
        }
    }
}
