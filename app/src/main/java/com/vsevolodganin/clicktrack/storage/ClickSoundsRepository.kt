package com.vsevolodganin.clicktrack.storage

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.vsevolodganin.clicktrack.Database
import com.vsevolodganin.clicktrack.di.component.ApplicationScoped
import com.vsevolodganin.clicktrack.sounds.model.ClickSoundSource
import com.vsevolodganin.clicktrack.sounds.model.ClickSoundType
import com.vsevolodganin.clicktrack.sounds.model.ClickSounds
import com.vsevolodganin.clicktrack.sounds.model.ClickSoundsId
import com.vsevolodganin.clicktrack.sounds.model.UserClickSounds
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import com.vsevolodganin.clicktrack.storage.ClickSounds as StorageClickSounds

@ApplicationScoped
class ClickSoundsRepository @Inject constructor(
    private val json: Json,
    private val database: Database,
) {
    fun getAll(): Flow<List<UserClickSounds>> {
        return database.sqlClickSoundsQueries.getAll().asFlow()
            .map { it.executeAsList().map { elem -> elem.toCommon() } }
    }

    fun getById(id: ClickSoundsId.Database): Flow<UserClickSounds?> {
        return database.sqlClickSoundsQueries.getById(id.value).asFlow()
            .map { it.executeAsOneOrNull()?.toCommon() }
    }

    fun insert(clickSounds: ClickSounds) {
        database.sqlClickSoundsQueries.insert(
            serializedValue = json.encodeToString(clickSounds)
        )
    }

    fun update(id: ClickSoundsId.Database, clickSounds: ClickSounds) {
        database.sqlClickSoundsQueries.update(
            id = id.value,
            serializedValue = clickSounds.serializeToString()
        )
    }

    fun update(id: ClickSoundsId.Database, type: ClickSoundType, source: ClickSoundSource) {
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
        value = serializedValue.deserializeToClickSounds()
    )

    private fun ClickSounds.serializeToString(): String = json.encodeToString(this)
    private fun String.deserializeToClickSounds(): ClickSounds = json.decodeFromString(this)
}
