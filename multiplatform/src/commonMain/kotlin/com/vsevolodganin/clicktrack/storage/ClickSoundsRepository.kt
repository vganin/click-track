package com.vsevolodganin.clicktrack.storage

import app.cash.sqldelight.coroutines.asFlow
import com.vsevolodganin.clicktrack.Database
import com.vsevolodganin.clicktrack.di.component.ApplicationScope
import com.vsevolodganin.clicktrack.model.ClickSoundSource
import com.vsevolodganin.clicktrack.model.ClickSoundType
import com.vsevolodganin.clicktrack.model.ClickSoundsId
import com.vsevolodganin.clicktrack.model.UriClickSounds
import com.vsevolodganin.clicktrack.model.UserClickSounds
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Inject
import com.vsevolodganin.clicktrack.storage.ClickSounds as StorageClickSounds

@ApplicationScope
@Inject
class ClickSoundsRepository(
    private val database: Database,
    private val json: Json,
) {
    fun getAll(): Flow<List<UserClickSounds>> {
        return database.sqlClickSoundsQueries.getAll().asFlow()
            .map { it.executeAsList().map { elem -> elem.toCommon() } }
    }

    fun getById(id: ClickSoundsId.Database): Flow<UserClickSounds?> {
        return database.sqlClickSoundsQueries.getById(id.value).asFlow()
            .map { it.executeAsOneOrNull()?.toCommon() }
    }

    fun insert(clickSounds: UriClickSounds) {
        database.sqlClickSoundsQueries.insert(
            serializedValue = json.encodeToString(clickSounds),
        )
    }

    fun update(id: ClickSoundsId.Database, clickSounds: UriClickSounds) {
        database.sqlClickSoundsQueries.update(
            id = id.value,
            serializedValue = clickSounds.serializeToString(),
        )
    }

    fun update(id: ClickSoundsId.Database, type: ClickSoundType, source: ClickSoundSource.Uri) {
        database.sqlClickSoundsQueries.transaction {
            val current = database.sqlClickSoundsQueries.getById(id.value).executeAsOneOrNull()?.toCommon() ?: return@transaction
            val updated = when (type) {
                ClickSoundType.STRONG -> current.copy(value = current.value.copy(strongBeat = source))
                ClickSoundType.WEAK -> current.copy(value = current.value.copy(weakBeat = source))
            }
            update(updated)
        }
    }

    fun update(data: UserClickSounds) = update(data.id, data.value)

    fun remove(id: ClickSoundsId.Database) {
        database.sqlClickSoundsQueries.removeById(id.value)
    }

    private fun StorageClickSounds.toCommon() = UserClickSounds(
        id = ClickSoundsId.Database(id),
        value = serializedValue.deserializeToClickSounds(),
    )

    private fun UriClickSounds.serializeToString(): String = json.encodeToString(this)

    private fun String.deserializeToClickSounds(): UriClickSounds = json.decodeFromString(this)
}
