package com.example.programme

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log

data class BookAudioSelection(
    val uri: String?,
    val displayName: String?
)

class BookAudioSelectionRepository(
    private val context: Context,
    private val preferences: SharedPreferences
) {
    fun load(): BookAudioSelection = BookAudioSelection(
        uri = preferences.getString(KEY_URI, null),
        displayName = preferences.getString(KEY_NAME, null)
    )

    fun select(uri: Uri, displayName: String?) {
        val previousUri = load().uri
        context.contentResolver.takePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
        val saved = preferences.edit()
            .putString(KEY_URI, uri.toString())
            .putString(KEY_NAME, displayName)
            .commit()
        if (!saved) {
            releaseIfPersisted(uri)
            throw IllegalStateException("Selected audio URI could not be saved.")
        }
        previousUri
            ?.takeIf { it != uri.toString() }
            ?.let { releaseIfPersisted(Uri.parse(it)) }
    }

    fun clear() {
        val selected = load().uri
        if (!preferences.edit().clear().commit()) {
            throw IllegalStateException("Selected audio URI could not be cleared.")
        }
        selected?.let { releaseIfPersisted(Uri.parse(it)) }
    }

    private fun releaseIfPersisted(uri: Uri) {
        val permissionExists = context.contentResolver.persistedUriPermissions
            .any { it.uri == uri && it.isReadPermission }
        if (permissionExists) {
            try {
                context.contentResolver.releasePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (error: SecurityException) {
                Log.w("BookAudioSelection", "Persisted access to an old audio URI was already revoked.", error)
            }
        }
    }

    companion object {
        private const val PREFERENCES_NAME = "book_audio_selection"
        private const val KEY_URI = "selected_uri"
        private const val KEY_NAME = "selected_name"

        fun create(context: Context) = BookAudioSelectionRepository(
            context = context,
            preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        )
    }
}
