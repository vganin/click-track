package com.vsevolodganin.clicktrack.storage

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.vsevolodganin.clicktrack.Database
import com.vsevolodganin.clicktrack.di.component.ApplicationScope
import com.vsevolodganin.clicktrack.migration.CanMigrate
import com.vsevolodganin.clicktrack.model.ClickTrack
import com.vsevolodganin.clicktrack.model.ClickTrackId
import com.vsevolodganin.clicktrack.model.ClickTrackWithDatabaseId
import com.vsevolodganin.clicktrack.premade.PreMadeClickTracks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Inject
import com.vsevolodganin.clicktrack.storage.ClickTrack as StorageClickTrack

@ApplicationScope
@Inject
class ClickTrackRepository(
    private val database: Database,
    private val json: Json,
) : CanMigrate {

    override fun migrate(fromVersion: Int, toVersion: Int) {
        if (fromVersion == UserPreferencesRepository.Const.NO_APP_VERSION_CODE) {
            for (clickTrack in PreMadeClickTracks.DATA) {
                insert(clickTrack)
            }
        }
    }

    fun getAll(): Flow<List<ClickTrackWithDatabaseId>> {
        return database.sqlClickTrackQueries.getAll().asFlow()
            .mapToList(Dispatchers.IO)
            .map { listOfCLickTracks ->
                listOfCLickTracks
                    .sortedBy { it.ordinal }
                    .map { it.toCommon() }
            }
    }

    fun getAllNames(): List<String> {
        return database.sqlClickTrackQueries.getAllNames().executeAsList()
    }

    fun getById(id: ClickTrackId.Database): Flow<ClickTrackWithDatabaseId?> {
        return database.sqlClickTrackQueries.getById(id.value).asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { it?.toCommon() }
    }

    fun insert(clickTrack: ClickTrack): ClickTrackId.Database {
        return database.sqlClickTrackQueries.run {
            transactionWithResult {
                val count = getCount().executeAsOne()
                insert(
                    name = clickTrack.name,
                    serializedValue = json.encodeToString(clickTrack),
                    ordinal = count
                )
                lastRowId().executeAsOne()
            }
        }.let(ClickTrackId::Database)
    }

    fun update(id: ClickTrackId.Database, clickTrack: ClickTrack) {
        database.sqlClickTrackQueries.update(
            id = id.value,
            name = clickTrack.name,
            serializedValue = clickTrack.serializeToString()
        )
    }

    fun updateOrdering(ordering: List<ClickTrackId.Database>) {
        database.sqlClickTrackQueries.apply {
            transaction {
                ordering.forEachIndexed { index, id ->
                    updateOrdering(index.toLong(), id.value)
                }
            }
        }
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
