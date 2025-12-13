package com.vsevolodganin.clicktrack.di.module

import com.vsevolodganin.clicktrack.di.component.ApplicationScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlinx.serialization.json.Json

@ContributesTo(ApplicationScope::class)
@BindingContainer
object SerializationModule {
    @Provides
    @SingleIn(ApplicationScope::class)
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
    }
}
