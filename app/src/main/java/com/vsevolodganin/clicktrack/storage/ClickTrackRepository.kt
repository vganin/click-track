package com.vsevolodganin.clicktrack.storage

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.vsevolodganin.clicktrack.Database
import com.vsevolodganin.clicktrack.di.component.ViewModelScoped
import com.vsevolodganin.clicktrack.lib.ClickTrack
import com.vsevolodganin.clicktrack.lib.premade.PreMadeClickTracks
import com.vsevolodganin.clicktrack.migration.CanMigrate
import com.vsevolodganin.clicktrack.model.ClickTrackId
import com.vsevolodganin.clicktrack.model.ClickTrackWithId
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import com.vsevolodganin.clicktrack.storage.ClickTrack as StorageClickTrack

@ViewModelScoped
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

    fun getAll(): Flow<List<ClickTrackWithId>> {
        return database.sqlClickTrackQueries.getAll().asFlow()
            .map { it.executeAsList().map { elem -> elem.toCommon() } }
    }

    fun getAllNames(): List<String> {
        return database.sqlClickTrackQueries.getAllNames().executeAsList()
    }

    fun getById(id: ClickTrackId.Database): Flow<ClickTrackWithId?> {
        return database.sqlClickTrackQueries.getById(id.value).asFlow()
            .map { it.executeAsOneOrNull()?.toCommon() }
    }

    fun insert(clickTrack: ClickTrack): ClickTrackWithId {
        val insertedRowId: Long = database.sqlClickTrackQueries.transactionWithResult {
            database.sqlClickTrackQueries.insert(
                name = clickTrack.name,
                serializedValue = json.encodeToString(clickTrack)
            )
            database.sqlClickTrackQueries.lastRowId().executeAsOne()
        }
        return ClickTrackWithId(
            id = ClickTrackId.Database(insertedRowId),
            value = clickTrack
        )
    }

    private fun insertIfHasNoSuchName(clickTrack: ClickTrack): ClickTrackWithId? {
        val insertedRowId: Long = database.sqlClickTrackQueries.transactionWithResult {
            val allNames = database.sqlClickTrackQueries.getAllNames().executeAsList()
            if (clickTrack.name in allNames) return@transactionWithResult null
            database.sqlClickTrackQueries.insert(
                name = clickTrack.name,
                serializedValue = json.encodeToString(clickTrack)
            )
            database.sqlClickTrackQueries.lastRowId().executeAsOne()
        } ?: return null
        return ClickTrackWithId(
            id = ClickTrackId.Database(insertedRowId),
            value = clickTrack
        )
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

    private fun StorageClickTrack.toCommon(): ClickTrackWithId {
        return ClickTrackWithId(
            id = ClickTrackId.Database(id),
            value = serializedValue.deserializeToClickTrack()
        )
    }

    private fun ClickTrack.serializeToString(): String = json.encodeToString(this)
    private fun String.deserializeToClickTrack(): ClickTrack = json.decodeFromString(this)
}
