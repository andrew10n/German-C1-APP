package com.example.data.repository

import com.example.data.local.dao.BaselineProfileDao
import com.example.data.local.entity.BaselineProfileEntity
import kotlinx.coroutines.flow.Flow

class DiagnosticRepository(
    private val baselineProfileDao: BaselineProfileDao
) {
    val baselineProfile: Flow<BaselineProfileEntity?> =
        baselineProfileDao.getBaselineProfileFlow()

    suspend fun getProfileDirect(): BaselineProfileEntity? {
        return baselineProfileDao.getBaselineProfile()
    }

    suspend fun saveBaselineProfile(
        profile: BaselineProfileEntity,
        grammarOnly: Boolean = false,
        practiceScreening: Boolean = false
    ) {
        // The legacy table has no assessment provenance or per-skill unassessed state.
        // A short MCQ screening must not replace a previously measured baseline.
        if (grammarOnly || practiceScreening) return
        baselineProfileDao.insertOrUpdateProfile(profile)
    }

    suspend fun resetBaselineProfile() {
        baselineProfileDao.deleteProfile()
    }
}
