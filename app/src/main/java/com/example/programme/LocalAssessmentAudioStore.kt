package com.example.programme

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import java.io.File
import java.io.IOException
import java.util.UUID

const val ASSESSMENT_AUDIO_RESPONSE_PREFIX = "__audio__:"

class LocalAssessmentAudioStore(context: Context) {
    private val appContext = context.applicationContext
    private val directory = File(appContext.filesDir, "assessment_audio")
    private var recorder: MediaRecorder? = null
    private var activeFile: File? = null

    fun startRecording() {
        check(recorder == null) { "A recording is already active." }
        if (!directory.exists() && !directory.mkdirs()) {
            throw IOException("Could not create the private assessment-audio directory.")
        }
        val output = File(directory, "${UUID.randomUUID()}.m4a")
        val mediaRecorder = createRecorder()
        try {
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            mediaRecorder.setOutputFile(output.absolutePath)
            mediaRecorder.prepare()
            mediaRecorder.start()
            recorder = mediaRecorder
            activeFile = output
        } catch (error: IOException) {
            mediaRecorder.release()
            output.delete()
            throw error
        } catch (error: RuntimeException) {
            mediaRecorder.release()
            output.delete()
            throw error
        }
    }

    fun stopRecording(): String {
        val mediaRecorder = checkNotNull(recorder) { "No assessment recording is active." }
        val output = checkNotNull(activeFile) { "The active assessment recording has no output file." }
        recorder = null
        activeFile = null
        try {
            mediaRecorder.stop()
            return output.name
        } catch (error: RuntimeException) {
            output.delete()
            throw IllegalStateException("The audio recording could not be finalized.", error)
        } finally {
            mediaRecorder.release()
        }
    }

    fun cancelRecording() {
        val mediaRecorder = recorder ?: return
        val output = activeFile
        recorder = null
        activeFile = null
        try {
            mediaRecorder.reset()
        } finally {
            mediaRecorder.release()
            output?.delete()
        }
    }

    fun file(fileName: String): File {
        require(fileName.isNotBlank() && File(fileName).name == fileName && fileName.endsWith(".m4a")) {
            "Invalid assessment audio filename."
        }
        return File(directory, fileName)
    }

    fun hasRecording(fileName: String?): Boolean {
        if (fileName.isNullOrBlank() || File(fileName).name != fileName || !fileName.endsWith(".m4a")) {
            return false
        }
        return File(directory, fileName).isFile
    }

    fun delete(fileName: String) {
        val recording = file(fileName)
        if (recording.exists() && !recording.delete()) {
            throw IOException("Could not delete the local assessment recording.")
        }
    }

    fun deleteAll() {
        cancelRecording()
        if (!directory.exists()) return
        val recordings = directory.listFiles()
            ?: throw IOException("Could not list local assessment recordings.")
        recordings.filter(File::isFile).forEach { recording ->
            if (!recording.delete()) {
                throw IOException("Could not delete a local assessment recording.")
            }
        }
        if (directory.listFiles().isNullOrEmpty() && !directory.delete()) {
            throw IOException("Could not remove the empty assessment-audio directory.")
        }
    }

    private fun createRecorder(): MediaRecorder =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(appContext)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }
}
