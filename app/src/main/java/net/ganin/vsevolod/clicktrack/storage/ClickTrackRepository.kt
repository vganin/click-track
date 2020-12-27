package net.ganin.vsevolod.clicktrack.storage

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.runtime.coroutines.asFlow
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.ganin.vsevolod.clicktrack.Database
import net.ganin.vsevolod.clicktrack.di.component.ViewModelScoped
import net.ganin.vsevolod.clicktrack.di.module.ApplicationContext
import net.ganin.vsevolod.clicktrack.di.module.MainDispatcher
import net.ganin.vsevolod.clicktrack.lib.ClickTrack
import net.ganin.vsevolod.clicktrack.lib.premade.PreMadeClickTracks
import net.ganin.vsevolod.clicktrack.model.ClickTrackWithId
import javax.inject.Inject
import net.ganin.vsevolod.clicktrack.storage.ClickTrack as StorageClickTrack

@ViewModelScoped
class ClickTrackRepository @Inject constructor(
    @ApplicationContext context: Context,
    @MainDispatcher mainDispatcher: CoroutineDispatcher,
) {
    private val database: Database = Database(AndroidSqliteDriver(
        schema = Database.Schema,
        context = context,
        name = "click_track.db",
        callback = object : AndroidSqliteDriver.Callback(Database.Schema) {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)

                GlobalScope.launch(mainDispatcher) {
                    for (clickTrack in PreMadeClickTracks.DATA) {
                        insert(clickTrack)
                    }
                }
            }
        }
    ))
    private val json: Json = Json

    fun getAll(): Flow<List<ClickTrackWithId>> {
        return database.sqlClickTrackQueries.getAll().asFlow()
            .map { it.executeAsList().map { elem -> elem.toCommon() } }
    }

    fun getAllNames(): List<String> {
        return database.sqlClickTrackQueries.getAllNames().executeAsList()
    }

    fun getById(id: Long): Flow<ClickTrackWithId?> {
        return database.sqlClickTrackQueries.getById(id).asFlow()
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
