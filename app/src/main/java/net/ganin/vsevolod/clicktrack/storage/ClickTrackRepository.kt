package net.ganin.vsevolod.clicktrack.storage

import android.content.Context
import com.squareup.sqldelight.android.AndroidSqliteDriver
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.ganin.vsevolod.clicktrack.Database
import net.ganin.vsevolod.clicktrack.lib.ClickTrack

class ClickTrackRepository(
    context: Context,
    private val database: Database = Database(AndroidSqliteDriver(Database.Schema, context, "click_track.db")),
    private val json: Json = Json
) {
    fun insert(clickTrack: ClickTrack) {
        val serialized = json.encodeToString(clickTrack)
        database.sqlClickTrackQueries.insert(serialized)
    }

    fun all(): List<ClickTrack> {
        return database.sqlClickTrackQueries.all().executeAsList()
            .map { json.decodeFromString(it) }
    }
}