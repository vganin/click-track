package com.vsevolodganin.clicktrack.storage

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.vsevolodganin.clicktrack.Database
import com.vsevolodganin.clicktrack.migration.CanMigrate
import com.vsevolodganin.clicktrack.model.ClickTrack
import com.vsevolodganin.clicktrack.model.ClickTrackId
import com.vsevolodganin.clicktrack.model.ClickTrackWithDatabaseId
import com.vsevolodganin.clicktrack.premade.PreMadeClickTracks
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton
import com.vsevolodganin.clicktrack.storage.ClickTrack as StorageClickTrack

@Singleton
class ClickTrackRepository @Inject constructor(
    private val database: Database,
    private val json: Json,
) : CanMigrate {

    override fun migrate(fromVersion: Int, toVersion: Int) {
        if (fromVersion == UserPreferencesRepository.Const.NO_APP_VERSION_CODE) {
            for (clickTrack in PreMadeClickTracks.DATA) {
                insertIfHasNoSuchName(clickTrack)
            }
        }
    }

    fun getAll(): Flow<List<ClickTrackWithDatabaseId>> {
        return database.sqlClickTrackQueries.getAll().asFlow()
            .map { it.executeAsList().map { elem -> elem.toCommon() } }
    }

    fun getAllNames(): List<String> {
        return database.sqlClickTrackQueries.getAllNames().executeAsList()
    }

    fun getById(id: ClickTrackId.Database): Flow<ClickTrackWithDatabaseId?> {
        return database.sqlClickTrackQueries.getById(id.value).asFlow()
            .map { it.executeAsOneOrNull()?.toCommon() }
    }

    fun insert(clickTrack: ClickTrack): ClickTrackId.Database {
        return database.sqlClickTrackQueries.transactionWithResult<Long> {
            database.sqlClickTrackQueries.insert(
                name = clickTrack.name,
                serializedValue = json.encodeToString(clickTrack)
            )
            database.sqlClickTrackQueries.lastRowId().executeAsOne()
        }.let(ClickTrackId::Database)
    }

    private fun insertIfHasNoSuchName(clickTrack: ClickTrack) {
        database.sqlClickTrackQueries.transaction {
            val allNames = database.sqlClickTrackQueries.getAllNames().executeAsList()
            if (clickTrack.name in allNames) return@transaction
            database.sqlClickTrackQueries.insert(
                name = clickTrack.name,
                serializedValue = json.encodeToString(clickTrack)
            )
        }
    }

    fun update(id: ClickTrackId.Database, clickTrack: ClickTrack) {
        database.sqlClickTrackQueries.update(
            id = id.value,
            name = clickTrack.name,
            serializedValue = clickTrack.serializeToString()
        )
    }

    fun remove(id: ClickTrackId.Database) {
        database.sqlClickTrackQueries.removeById(id.value)
    }

    private fun StorageClickTrack.toCommon(): ClickTrackWithDatabaseId {
        return ClickTrackWithDatabaseId(
            id = ClickTrackId.Database(id),
            value = serializedValue.deserializeToClickTrack()
        )
    }

    private fun ClickTrack.serializeToString(): String = json.encodeToString(this)
    private fun String.deserializeToClickTrack(): ClickTrack = json.decodeFromString(this)
}
