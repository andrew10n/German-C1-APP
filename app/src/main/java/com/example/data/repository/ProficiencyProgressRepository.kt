package com.example.data.repository

import com.example.data.local.dao.BaselineProfileDao
import com.example.data.local.dao.ProficiencySnapshotDao
import com.example.data.local.dao.VocabularyItemDao
import com.example.data.local.entity.BaselineProfileEntity
import com.example.data.local.entity.ProficiencySnapshotEntity
import kotlinx.coroutines.flow.Flow


/**
 * Repository that abstracts proficiency tracking data from the Room database,
 * combining baseline profiles, chronological snapshots, and vocabulary metrics.
 */
class ProficiencyProgressRepository(
    private val snapshotDao: ProficiencySnapshotDao,
    private val baselineDao: BaselineProfileDao,
    private val vocabularyDao: VocabularyItemDao
) {


    val allSnapshotsFlow: Flow<List<ProficiencySnapshotEntity>> =
        snapshotDao.getAllSnapshotsFlow()

    val baselineProfileFlow: Flow<BaselineProfileEntity?> =
        baselineDao.getBaselineProfileFlow()

    val masteredVocabularyCountFlow: Flow<Int> =
        vocabularyDao.getMasteredCountFlow()

    /** Compatibility entry point: history stays empty until actual snapshots are supplied. */
    @Suppress("UNUSED_PARAMETER")
    suspend fun seedHistoricalProficiencyIfEmpty(now: Long = System.currentTimeMillis()) = Unit

    suspend fun insertSnapshot(snapshot: ProficiencySnapshotEntity) {
        snapshotDao.insertSnapshot(snapshot)
    }

    suspend fun getLatestSnapshot(): ProficiencySnapshotEntity? {
        return snapshotDao.getLatestSnapshot()
    }
}
