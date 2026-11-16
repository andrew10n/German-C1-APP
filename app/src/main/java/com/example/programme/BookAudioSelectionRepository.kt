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
    fun load(selectionKey: String? = null): BookAudioSelection {
        val suffix = selectionSuffix(selectionKey)
        return BookAudioSelection(
            uri = preferences.getString("$KEY_URI$suffix", null),
            displayName = preferences.getString("$KEY_NAME$suffix", null)
        )
    }

    fun select(uri: Uri, displayName: String?, selectionKey: String? = null) {
        val suffix = selectionSuffix(selectionKey)
        val previousUri = load(selectionKey).uri
        context.contentResolver.takePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
        val saved = preferences.edit()
            .putString("$KEY_URI$suffix", uri.toString())
            .putString("$KEY_NAME$suffix", displayName)
            .commit()
        if (!saved) {
            releaseIfPersisted(uri)
            throw IllegalStateException("Selected audio URI could not be saved.")
        }
        previousUri
            ?.takeIf { it != uri.toString() }
            ?.let(::releaseIfUnreferenced)
    }

    fun clear(selectionKey: String? = null) {
        if (selectionKey == null) {
            val selected = preferences.all
                .filterKeys { it.startsWith(KEY_URI) }
                .values
                .filterIsInstance<String>()
                .distinct()
            if (!preferences.edit().clear().commit()) {
                throw IllegalStateException("Selected audio URI could not be cleared.")
            }
            selected.forEach { releaseIfPersisted(Uri.parse(it)) }
            return
        }

        val suffix = selectionSuffix(selectionKey)
        val selected = load(selectionKey).uri
        if (!preferences.edit()
                .remove("$KEY_URI$suffix")
                .remove("$KEY_NAME$suffix")
                .commit()
        ) {
            throw IllegalStateException("Selected audio URI could not be cleared.")
        }
        selected?.let(::releaseIfUnreferenced)
    }

    private fun selectionSuffix(selectionKey: String?): String {
        if (selectionKey == null) return ""
        require(selectionKey.isNotBlank()) { "Audio selection key must not be blank." }
        return ":$selectionKey"
    }

    private fun releaseIfUnreferenced(uriString: String) {
        val stillSelected = preferences.all
            .filterKeys { it.startsWith(KEY_URI) }
            .values
            .any { it == uriString }
        if (!stillSelected) releaseIfPersisted(Uri.parse(uriString))
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
