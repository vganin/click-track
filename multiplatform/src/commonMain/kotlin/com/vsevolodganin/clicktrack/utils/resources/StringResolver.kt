package com.vsevolodganin.clicktrack.utils.resources

import com.vsevolodganin.clicktrack.di.component.MainControllerScope
import dev.icerock.moko.resources.StringResource
import me.tatarka.inject.annotations.Inject

@Inject
@MainControllerScope
expect class StringResolver {
    suspend fun resolve(resource: StringResource): String
}
