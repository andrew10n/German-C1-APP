package com.example.programme

import android.content.Context
import android.content.SharedPreferences

data class PracticeDraft(
    val unitId: String,
    val taskId: String,
    val context: ContextTrack,
    val response: String,
    val updatedAt: Long
)

class PracticeDraftRepository(
    private val preferences: SharedPreferences
) {
    fun load(unitId: String, taskId: String, context: ContextTrack): String =
        preferences.getString(responseKey(unitId, taskId, context), "").orEmpty()

    fun save(unitId: String, taskId: String, context: ContextTrack, response: String) {
        val responseKey = responseKey(unitId, taskId, context)
        if (response.isBlank()) {
            preferences.edit()
                .remove(responseKey)
                .remove(updatedAtKey(responseKey))
                .apply()
            return
        }
        preferences.edit()
            .putString(responseKey, response)
            .putLong(updatedAtKey(responseKey), System.currentTimeMillis())
            .apply()
    }

    fun savedDrafts(): List<PracticeDraft> =
        preferences.all.keys
            .filter { it.startsWith(DRAFT_PREFIX) && !it.endsWith(UPDATED_AT_SUFFIX) }
            .map { key ->
                val parts = key.removePrefix(DRAFT_PREFIX).split(KEY_SEPARATOR)
                require(parts.size == 3) { "Invalid saved practice draft key: $key" }
                val context = ContextTrack.values().firstOrNull { it.name == parts[2] }
                    ?: error("Invalid saved practice draft context: ${parts[2]}")
                PracticeDraft(
                    unitId = parts[0],
                    taskId = parts[1],
                    context = context,
                    response = preferences.getString(key, "").orEmpty(),
                    updatedAt = preferences.getLong(updatedAtKey(key), 0L)
                )
            }
            .filter { it.response.isNotBlank() }
            .sortedByDescending { it.updatedAt }

    fun clear(unitId: String, taskId: String, context: ContextTrack) {
        val responseKey = responseKey(unitId, taskId, context)
        preferences.edit()
            .remove(responseKey)
            .remove(updatedAtKey(responseKey))
            .apply()
    }

    fun clearAll() {
        preferences.edit().clear().apply()
    }

    private fun responseKey(unitId: String, taskId: String, context: ContextTrack) =
        "$DRAFT_PREFIX$unitId$KEY_SEPARATOR$taskId$KEY_SEPARATOR${context.name}"

    private fun updatedAtKey(responseKey: String) = "$responseKey$UPDATED_AT_SUFFIX"

    companion object {
        private const val PREFERENCES_NAME = "practice_drafts"
        private const val DRAFT_PREFIX = "draft:"
        private const val KEY_SEPARATOR = ":"
        private const val UPDATED_AT_SUFFIX = ":updatedAt"

        fun create(context: Context) = PracticeDraftRepository(
            context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        )
    }
}
