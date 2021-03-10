package com.vsevolodganin.clicktrack.di.module

import com.vsevolodganin.clicktrack.di.component.ApplicationScoped
import dagger.Module
import dagger.Provides
import kotlinx.serialization.json.Json

@Module
class SerializationModule {

    @Provides
    @ApplicationScoped
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
    }
}
