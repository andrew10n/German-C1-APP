package com.example.programme

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.Index
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

@Entity(
    tableName = "programme_attempts",
    indices = [
        Index(value = ["unitId", "timestamp"]),
        Index(value = ["timestamp"])
    ]
)
data class ProgrammeAttemptEntity(
    @PrimaryKey val id: String,
    val unitId: String,
    val taskId: String,
    val timestamp: Long,
    val response: String,
    val source: String,
    val scoresJson: String,
    val assisted: Boolean,
    val unfamiliar: Boolean,
    val independentProduction: Boolean,
    val context: String,
    val errorsJson: String,
    @ColumnInfo(defaultValue = "1") val taskVersion: Int = 1,
    @ColumnInfo(defaultValue = "1") val rubricVersion: Int = 1
)

@Dao
interface ProgrammeAttemptDao {
    @Query("SELECT * FROM programme_attempts ORDER BY timestamp ASC, id ASC")
    fun observeAttempts(): Flow<List<ProgrammeAttemptEntity>>

    // An ID identifies immutable evidence: a retry must never replace its provenance or scores.
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(attempt: ProgrammeAttemptEntity)

    @Query("DELETE FROM programme_attempts")
    suspend fun deleteAll()
}

class ProgrammeRepository(private val dao: ProgrammeAttemptDao) {
    val attempts: Flow<List<Attempt>> = dao.observeAttempts().map { rows ->
        rows.map { it.toAttempt() }
    }

    suspend fun record(attempt: Attempt) {
        dao.insert(attempt.toEntity())
    }

    suspend fun deleteAll() = dao.deleteAll()
}

internal fun Attempt.toEntity(): ProgrammeAttemptEntity {
    val scoresObject = JSONObject()
    scores.forEach { (dimension, score) -> scoresObject.put(dimension.name, score) }
    val errorsArray = JSONArray()
    errors.forEach { errorsArray.put(it) }
    return ProgrammeAttemptEntity(
        id = id,
        unitId = unitId,
        taskId = taskId,
        timestamp = timestamp,
        response = response,
        source = source.name,
        scoresJson = scoresObject.toString(),
        assisted = assisted,
        unfamiliar = unfamiliar,
        independentProduction = independentProduction,
        context = context.name,
        errorsJson = errorsArray.toString(),
        taskVersion = taskVersion,
        rubricVersion = rubricVersion
    )
}

internal fun ProgrammeAttemptEntity.toAttempt(): Attempt {
    // Do not silently turn corrupt/unknown evidence into valid mastery evidence.
    val scoresObject = JSONObject(scoresJson)
    val scores = mutableMapOf<Dimension, Int>()
    val keys = scoresObject.keys()
    while (keys.hasNext()) {
        val key = keys.next()
        val score = scoresObject.get(key)
        require(score is Int) { "Invalid score for $key in attempt $id" }
        scores[Dimension.valueOf(key)] = score
    }
    val errorsArray = JSONArray(errorsJson)
    val errors = List(errorsArray.length()) { index ->
        val error = errorsArray.get(index)
        require(error is String) { "Invalid error at $index in attempt $id" }
        error
    }
    return Attempt(
        id = id,
        unitId = unitId,
        taskId = taskId,
        timestamp = timestamp,
        response = response,
        source = EvidenceSource.valueOf(source),
        scores = scores,
        assisted = assisted,
        unfamiliar = unfamiliar,
        independentProduction = independentProduction,
        context = ContextTrack.valueOf(context),
        errors = errors,
        taskVersion = taskVersion,
        rubricVersion = rubricVersion
    )
}
