package net.ganin.vsevolod.clicktrack.storage

import android.content.Context
import com.squareup.sqldelight.Query
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.ganin.vsevolod.clicktrack.Database
import net.ganin.vsevolod.clicktrack.lib.ClickTrackWithMeta
import kotlin.coroutines.coroutineContext
import net.ganin.vsevolod.clicktrack.storage.ClickTrack as StorageClickTrack

class ClickTrackRepository(
    context: Context,
    private val database: Database = Database(AndroidSqliteDriver(Database.Schema, context, "click_track.db")),
    private val json: Json = Json
) {
    fun put(clickTrack: ClickTrackWithMeta) {
        database.sqlClickTrackQueries.put(clickTrack.toStorage())
    }

    fun all(): Flow<List<ClickTrackWithMeta>> {
        fun Query<StorageClickTrack>.pollAsFlow(): Flow<Query<StorageClickTrack>> {
            return flow {
                while (coroutineContext.isActive) {
                    emit(this@pollAsFlow)
                    delay(1000)
                }
            }
        }

        return database.sqlClickTrackQueries.getAll()
//            .asFlow() // FIXME: This crashes at runtime, stupid poll flow will do for now
            .pollAsFlow()
            .mapToList(Dispatchers.Main)
            .map { data -> data.map { it.toCommon() } }
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
