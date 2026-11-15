package com.example.programme

import android.content.Context
import android.content.SharedPreferences

data class ExternalListeningDraft(
    val sourceId: String,
    val itemReference: String,
    val firstPass: String,
    val secondPass: String,
    val comparisonOpened: Boolean,
    val updatedAt: Long
) {
    val hasNotes: Boolean
        get() = itemReference.isNotBlank() || firstPass.isNotBlank() || secondPass.isNotBlank()
}

class ExternalListeningPracticeRepository(
    private val preferences: SharedPreferences
) {
    fun load(sourceId: String): ExternalListeningDraft {
        requireKnownSource(sourceId)
        return ExternalListeningDraft(
            sourceId = sourceId,
            itemReference = preferences.getString(key(sourceId, "reference"), "").orEmpty(),
            firstPass = preferences.getString(key(sourceId, "first"), "").orEmpty(),
            secondPass = preferences.getString(key(sourceId, "second"), "").orEmpty(),
            comparisonOpened = preferences.getBoolean(key(sourceId, "comparison"), false),
            updatedAt = preferences.getLong(key(sourceId, "updatedAt"), 0L)
        )
    }

    fun save(
        sourceId: String,
        itemReference: String,
        firstPass: String,
        secondPass: String,
        comparisonOpened: Boolean
    ) {
        requireKnownSource(sourceId)
        if (itemReference.isBlank() && firstPass.isBlank() && secondPass.isBlank() && !comparisonOpened) {
            clear(sourceId)
            return
        }
        preferences.edit()
            .putString(key(sourceId, "reference"), itemReference)
            .putString(key(sourceId, "first"), firstPass)
            .putString(key(sourceId, "second"), secondPass)
            .putBoolean(key(sourceId, "comparison"), comparisonOpened)
            .putLong(key(sourceId, "updatedAt"), System.currentTimeMillis())
            .apply()
    }

    fun savedDrafts(): List<ExternalListeningDraft> =
        ExternalListeningLibrary.sources.map { load(it.id) }.filter { it.hasNotes }

    fun clear(sourceId: String) {
        requireKnownSource(sourceId)
        preferences.edit()
            .remove(key(sourceId, "reference"))
            .remove(key(sourceId, "first"))
            .remove(key(sourceId, "second"))
            .remove(key(sourceId, "comparison"))
            .remove(key(sourceId, "updatedAt"))
            .apply()
    }

    fun clearAll() {
        preferences.edit().clear().apply()
    }

    private fun requireKnownSource(sourceId: String) {
        require(ExternalListeningLibrary.sources.any { it.id == sourceId }) {
            "Unknown external listening source: $sourceId"
        }
    }

    private fun key(sourceId: String, field: String) = "$sourceId:$field"

    companion object {
        private const val PREFERENCES_NAME = "external_listening_practice"

        fun create(context: Context) = ExternalListeningPracticeRepository(
            context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        )
    }
}
