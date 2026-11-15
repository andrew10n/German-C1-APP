package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.ColumnInfo
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room Entity representing a German C1 Vocabulary or Collocation item
 * tracked by the Spaced Repetition System (SRS).
 */
@Entity(
    tableName = "vocabulary_items",
    indices = [
        Index(value = ["nextReviewTimestamp"]),
        Index(value = ["sourceUnitId"])
    ]
)
data class VocabularyItemEntity(
    @PrimaryKey
    val id: String,
    val germanCollocation: String,
    val meaningDe: String,
    val exampleSentence: String,
    val register: String = "Gehoben / C1",
    val category: String = "Nomen-Verb-Verbindung",
    
    // Spaced Repetition System (SRS) Metrics
    val stage: Int = 0,                         // 0: New, 1: 1d, 2: 3d, 3: 7d, 4: 14d, 5: 30d (Mastered)
    val easeFactor: Float = 2.5f,               // SuperMemo SM-2 ease factor (min 1.3f)
    val intervalDays: Int = 0,                  // Current interval in days
    val repetitions: Int = 0,                   // Consecutive successful recalls
    val lastReviewedTimestamp: Long? = null,    // Timestamp of most recent review
    val nextReviewTimestamp: Long = 0L,         // Timestamp when item becomes due for review
    val consecutiveErrors: Int = 0,             // Number of recent failures triggering transfer drill
    val isMastered: Boolean = false,            // Whether item graduated to permanent long-term memory
    @ColumnInfo(defaultValue = "''")
    val sourceUnitId: String = ""
)
