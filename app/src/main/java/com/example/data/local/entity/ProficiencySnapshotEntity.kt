package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity capturing chronological language proficiency progress checkpoints over time.
 * Allows tracking continuous C1 competency growth from baseline to advanced mastery.
 */
@Entity(tableName = "proficiency_snapshots")
data class ProficiencySnapshotEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long,
    val periodLabel: String,     // e.g., "Basis-Diagnostik", "Woche 2", "Woche 4", "Aktuell"
    val overallScore: Int,       // Aggregated C1 Score (0-100)
    val functionalScore: Int,    // Spontaneous real-world readiness (0-100)
    val listeningScore: Int,     // Hörverstehen (0-100)
    val speakingScore: Int,      // Spontanes Sprechen (0-100)
    val readingScore: Int,       // Leseverstehen (0-100)
    val writingScore: Int,       // Schreiben & Register (0-100)
    val grammarScore: Int,       // Grammatik-Präzision (0-100)
    val vocabularyScore: Int     // Wortschatz-Retention (0-100)
)
