package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.BaselineProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BaselineProfileDao {

    @Query("SELECT * FROM baseline_profiles WHERE id = :id LIMIT 1")
    fun getBaselineProfileFlow(id: String = "primary_user_profile"): Flow<BaselineProfileEntity?>

    @Query("SELECT * FROM baseline_profiles WHERE id = :id LIMIT 1")
    suspend fun getBaselineProfile(id: String = "primary_user_profile"): BaselineProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: BaselineProfileEntity)

    @Query("DELETE FROM baseline_profiles WHERE id = :id")
    suspend fun deleteProfile(id: String = "primary_user_profile")
}
