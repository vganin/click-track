package net.ganin.vsevolod.clicktrack.storage

import android.content.Context
import com.squareup.sqldelight.android.AndroidSqliteDriver
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.ganin.vsevolod.clicktrack.Database
import net.ganin.vsevolod.clicktrack.di.component.ViewModelScoped
import net.ganin.vsevolod.clicktrack.di.module.ApplicationContext
import net.ganin.vsevolod.clicktrack.lib.ClickTrack
import net.ganin.vsevolod.clicktrack.model.ClickTrackWithId
import javax.inject.Inject
import net.ganin.vsevolod.clicktrack.storage.ClickTrack as StorageClickTrack

@ViewModelScoped
class ClickTrackRepository @Inject constructor(
    @ApplicationContext context: Context
) {
    private val database: Database = Database(AndroidSqliteDriver(Database.Schema, context, "click_track.db"))
    private val json: Json = Json

    fun getAll(): List<ClickTrackWithId> {
        return database.sqlClickTrackQueries.getAll().executeAsList()
            .map { it.toCommon() }
    }

    fun getAllNames(): List<String> {
        return database.sqlClickTrackQueries.getAllNames().executeAsList()
    }

    fun get(id: Long): ClickTrackWithId? {
        return database.sqlClickTrackQueries.getById(id).executeAsOneOrNull()
            ?.toCommon()
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
            id = insertedRowId,
            value = clickTrack
        )
    }

    fun update(id: Long, clickTrack: ClickTrack) {
        database.sqlClickTrackQueries.update(
            id = id,
            name = clickTrack.name,
            serializedValue = clickTrack.serializeToString()
        )
    }

    fun update(clickTrack: ClickTrackWithId) = update(clickTrack.id, clickTrack.value)

    fun remove(id: Long) {
        database.sqlClickTrackQueries.removeById(id)
    }

    private fun StorageClickTrack.toCommon(): ClickTrackWithId {
        return ClickTrackWithId(
            id = id,
            value = serializedValue.deserializeToClickTrack()
        )
    }

    private fun ClickTrack.serializeToString(): String = json.encodeToString(this)
    private fun String.deserializeToClickTrack(): ClickTrack = json.decodeFromString(this)
}
