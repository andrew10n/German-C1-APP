package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room database entity storing the user's initial baseline C1 skill profile,
 * covering all 8 assessed core competencies.
 */
@Entity(tableName = "baseline_profiles")
data class BaselineProfileEntity(
    @PrimaryKey
    val id: String = "primary_user_profile",
    val listeningScore: Int,      // Hörverstehen (0-100)
    val speakingScore: Int,       // Spontanes Sprechen (0-100)
    val interactionScore: Int,    // Interaktion (0-100)
    val readingScore: Int,        // Leseverstehen (0-100)
    val writingScore: Int,        // Schreiben (0-100)
    val grammarScore: Int,        // Grammatik (0-100)
    val vocabularyScore: Int,     // Wortschatz (0-100)
    val mediationScore: Int,      // Mediation & Sprachmittlung (0-100)
    val overallC1Score: Int,      // Aggregated C1 Score (0-100)
    val functionalReadinessPct: Int, // Real-world communicative capability (0-100)
    val primaryLimitingFactor: String,
    val recommendedStartingEngine: String,
    val isCompleted: Boolean = true,
    val assessedAtTimestamp: Long = System.currentTimeMillis()
)
