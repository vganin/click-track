package net.ganin.vsevolod.clicktrack.storage

import android.content.Context
import com.squareup.sqldelight.android.AndroidSqliteDriver
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.ganin.vsevolod.clicktrack.Database
import net.ganin.vsevolod.clicktrack.lib.ClickTrackWithMeta
import net.ganin.vsevolod.clicktrack.storage.ClickTrack as StorageClickTrack

class ClickTrackRepository(
    context: Context,
    private val database: Database = Database(AndroidSqliteDriver(Database.Schema, context, "click_track.db")),
    private val json: Json = Json
) {
    fun put(clickTrack: ClickTrackWithMeta) {
        database.sqlClickTrackQueries.put(clickTrack.toStorage())
    }

    fun remove(clickTrack: ClickTrackWithMeta) {
        database.sqlClickTrackQueries.remove(clickTrack.name)
    }

    fun all(): List<ClickTrackWithMeta> {
        return database.sqlClickTrackQueries.getAll().executeAsList()
            .map { it.toCommon() }
    }

    fun allNames(): List<String> {
        return database.sqlClickTrackQueries.getAllNames().executeAsList()
    }

    private fun ClickTrackWithMeta.toStorage(): StorageClickTrack {
        return StorageClickTrack(
            name = name,
            serialized = json.encodeToString(clickTrack)
        )
    }

    private fun StorageClickTrack.toCommon(): ClickTrackWithMeta {
        return ClickTrackWithMeta(
            name = name,
            clickTrack = json.decodeFromString(serialized)
        )
    }
}
