package com.vsevolodganin.clicktrack.utils.resources

import com.vsevolodganin.clicktrack.di.component.MainControllerScope
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.desc.desc
import me.tatarka.inject.annotations.Inject

@Inject
@MainControllerScope
actual class StringResolver() {
    actual suspend fun resolve(resource: StringResource): String {
        return resource.desc().localized()
    }
}
