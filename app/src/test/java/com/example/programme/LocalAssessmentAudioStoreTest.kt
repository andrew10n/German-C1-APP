package com.example.programme

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], application = Application::class)
class LocalAssessmentAudioStoreTest {
    private val store = LocalAssessmentAudioStore(ApplicationProvider.getApplicationContext())

    @Test
    fun localRecordingsAreScopedToTheAssessmentDirectoryAndDeleteAllRemovesThem() {
        val recording = store.file("recording.m4a")
        recording.parentFile?.mkdirs()
        recording.writeBytes(byteArrayOf(1, 2, 3))

        assertTrue(store.hasRecording(recording.name))
        store.deleteAll()
        assertFalse(recording.exists())
        assertFalse(store.hasRecording(recording.name))
    }

    @Test
    fun recordingNamesCannotEscapeThePrivateAssessmentDirectory() {
        assertThrows(IllegalArgumentException::class.java) {
            store.file("../outside.m4a")
        }
        assertFalse(store.hasRecording("../outside.m4a"))
    }
}
