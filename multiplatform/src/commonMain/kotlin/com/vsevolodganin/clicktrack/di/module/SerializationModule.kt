package com.vsevolodganin.clicktrack.di.module

import com.vsevolodganin.clicktrack.di.component.ApplicationScope
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Provides

interface SerializationModule {
    @Provides
    @ApplicationScope
    fun provideJson(): Json =
        Json {
            ignoreUnknownKeys = true
        }
}
