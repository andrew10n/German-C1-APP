package com.example.ui.components

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.MainApplication
import com.example.programme.ProgrammeLanguage
import java.io.IOException
import kotlinx.coroutines.delay

class BookAudioPlaybackController {
    internal var stopPlayback: () -> Unit = {}

    fun stop() = stopPlayback()
}

@Composable
fun BookAudioPracticeSection(
    controller: BookAudioPlaybackController,
    onPlaybackStart: () -> Unit,
    modifier: Modifier = Modifier,
    language: ProgrammeLanguage = ProgrammeLanguage.DE,
    onSelectionChanged: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val selectionRepository = remember(context) {
        (context.applicationContext as MainApplication).bookAudioSelectionRepository
    }
    val initialSelection = remember(selectionRepository) { selectionRepository.load() }
    var selectedUriString by remember {
        mutableStateOf(initialSelection.uri)
    }
    var selectedName by remember {
        mutableStateOf(initialSelection.displayName)
    }
    var player by remember { mutableStateOf<MediaPlayer?>(null) }
    var prepared by remember { mutableStateOf(false) }
    var playing by remember { mutableStateOf(false) }
    var completed by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var playheadMs by remember { mutableStateOf(0f) }
    var durationMs by remember { mutableStateOf(0) }
    var playbackSpeed by remember { mutableStateOf(1f) }
    var isSeeking by remember { mutableStateOf(false) }
    var repeatSegment by remember { mutableStateOf(false) }
    var segmentStartMs by remember { mutableStateOf<Int?>(null) }
    var segmentEndMs by remember { mutableStateOf<Int?>(null) }
    val selectedUri = remember(selectedUriString) {
        selectedUriString?.let(Uri::parse)
    }

    val audioPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            try {
                val name = displayName(context, uri)
                selectionRepository.select(uri, name)
                selectedUriString = uri.toString()
                selectedName = name
                error = null
                onSelectionChanged(uri.toString())
            } catch (_: SecurityException) {
                error = language.text(
                    "Die Audiodatei konnte nicht dauerhaft geöffnet werden. Bitte wähle sie erneut aus.",
                    "The audio file could not be opened persistently. Please select it again."
                )
            } catch (_: IllegalArgumentException) {
                error = language.text(
                    "Der Dateianbieter erlaubt keinen dauerhaften Zugriff. Bitte wähle die Aufnahme erneut aus.",
                    "This file provider does not allow persistent access. Please select the recording again."
                )
            } catch (_: IllegalStateException) {
                error = language.text(
                    "Die Auswahl konnte nicht gespeichert werden. Bitte versuche es erneut.",
                    "The selection could not be saved. Please try again."
                )
            }
        }
    }

    DisposableEffect(selectedUri, controller) {
        prepared = false
        playing = false
        completed = false
        error = null
        playheadMs = 0f
        durationMs = 0
        playbackSpeed = 1f
        repeatSegment = false
        segmentStartMs = null
        segmentEndMs = null
        var createdPlayer: MediaPlayer? = null

        if (selectedUri != null) {
            try {
                val mediaPlayer = MediaPlayer()
                createdPlayer = mediaPlayer
                mediaPlayer.setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .build()
                )
                mediaPlayer.setDataSource(context, selectedUri)
                mediaPlayer.setOnPreparedListener {
                    if (player === mediaPlayer) {
                        durationMs = mediaPlayer.duration.coerceAtLeast(0)
                        prepared = true
                    }
                }
                mediaPlayer.setOnCompletionListener {
                    if (player === mediaPlayer) {
                        playing = false
                        completed = true
                        playheadMs = durationMs.toFloat()
                    }
                }
                mediaPlayer.setOnErrorListener { _, _, _ ->
                    if (player === mediaPlayer) {
                        error = language.text(
                            "Dieses Audioformat kann auf dem Gerät nicht abgespielt werden.",
                            "This audio format cannot be played on this device."
                        )
                        prepared = false
                        playing = false
                    }
                    true
                }
                player = mediaPlayer
                mediaPlayer.prepareAsync()
            } catch (_: IOException) {
                error = language.text("Die ausgewählte Audiodatei ist nicht lesbar.", "The selected audio file cannot be read.")
                player = null
                createdPlayer?.release()
            } catch (_: SecurityException) {
                error = language.text(
                    "Der Zugriff auf die ausgewählte Datei ist abgelaufen. Bitte wähle sie erneut aus.",
                    "Access to the selected file has expired. Please select it again."
                )
                player = null
                createdPlayer?.release()
            } catch (_: IllegalArgumentException) {
                error = language.text("Die ausgewählte Datei ist kein unterstütztes Audio.", "The selected file is not supported audio.")
                player = null
                createdPlayer?.release()
            } catch (_: IllegalStateException) {
                error = language.text("Die Audiodatei konnte nicht vorbereitet werden.", "The audio file could not be prepared.")
                player = null
                createdPlayer?.release()
            }
        } else {
            player = null
        }

        controller.stopPlayback = {
            val active = player
            if (active === createdPlayer && active?.isPlaying == true) {
                active.pause()
                playing = false
            }
        }

        onDispose {
            controller.stopPlayback = {}
            if (player === createdPlayer) player = null
            createdPlayer?.release()
        }
    }

    LaunchedEffect(player, prepared, playing, repeatSegment, segmentStartMs, segmentEndMs) {
        val active = player
        while (active != null && prepared && playing) {
            try {
                if (!isSeeking) playheadMs = active.currentPosition.toFloat()
                val end = segmentEndMs
                val start = segmentStartMs
                if (repeatSegment && start != null && end != null && active.currentPosition >= end) {
                    active.seekTo(start)
                    playheadMs = start.toFloat()
                }
            } catch (_: IllegalStateException) {
                error = language.text("Der Wiedergabestatus konnte nicht gelesen werden.", "Playback status could not be read.")
                playing = false
            }
            delay(250)
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(language.text("Deine Buchaufnahmen", "Your book audio"), fontWeight = FontWeight.SemiBold)
            Text(language.text(
                "Wähle eine Aufnahme aus deinem Gerät oder einem Cloud-Speicher. Die Datei bleibt dort und wird nicht in die App kopiert.",
                "Choose a recording from your device or cloud storage. It stays there and is not copied into the app."
            ))
            Button(onClick = { audioPicker.launch(arrayOf("audio/*")) }) {
                Text(if (selectedUri == null) language.text("Audio auswählen", "Choose audio")
                else language.text("Andere Aufnahme auswählen", "Choose another recording"))
            }
            if (selectedUri != null) {
                Text(selectedName ?: language.text("Ausgewählte Audioaufnahme", "Selected audio recording"),
                    fontWeight = FontWeight.Medium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            val active = player ?: return@Button
                            try {
                                if (playing) {
                                    active.pause()
                                    playing = false
                                } else {
                                    onPlaybackStart()
                                    if (completed) {
                                        active.seekTo(0)
                                        completed = false
                                    }
                                    active.start()
                                    playing = true
                                    error = null
                                }
                            } catch (_: IllegalStateException) {
                                error = language.text(
                                    "Die Aufnahme konnte nicht gestartet werden. Wähle sie erneut aus.",
                                    "The recording could not be started. Please select it again."
                                )
                                prepared = false
                                playing = false
                            }
                        },
                        enabled = prepared
                    ) {
                        Icon(
                            imageVector = if (playing) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = null
                        )
                        Text(if (playing) "Pause" else "Abspielen")
                    }
                    if (error != null) {
                        OutlinedButton(onClick = { audioPicker.launch(arrayOf("audio/*")) }) {
                            Text(language.text("Erneut auswählen", "Select again"))
                        }
                    }
                }
                if (prepared && durationMs > 0) {
                        Slider(
                            value = playheadMs.coerceIn(0f, durationMs.toFloat()),
                            onValueChange = {
                                isSeeking = true
                                playheadMs = it
                            },
                            onValueChangeFinished = {
                                try {
                                    player?.seekTo(playheadMs.toInt())
                                } catch (_: IllegalStateException) {
                                    error = language.text(
                                        "Die Aufnahme konnte nicht an diese Stelle gesetzt werden.",
                                        "Could not seek to that position."
                                    )
                                }
                                isSeeking = false
                            },
                            valueRange = 0f..durationMs.toFloat(),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(formatPosition(playheadMs.toInt()), style = MaterialTheme.typography.labelSmall)
                            Text(formatPosition(durationMs), style = MaterialTheme.typography.labelSmall)
                        }
                        Text(language.text("Wiedergabegeschwindigkeit", "Playback speed"),
                            style = MaterialTheme.typography.labelLarge)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(0.75f, 1f, 1.25f).forEach { speed ->
                                androidx.compose.material3.FilterChip(
                                    selected = playbackSpeed == speed,
                                    onClick = {
                                        try {
                                            player?.setPlaybackParams(
                                                android.media.PlaybackParams().setSpeed(speed)
                                            )
                                            playbackSpeed = speed
                                        } catch (_: IllegalArgumentException) {
                                            error = language.text(
                                                "Diese Wiedergabegeschwindigkeit wird nicht unterstützt.",
                                                "This playback speed is not supported."
                                            )
                                        } catch (_: IllegalStateException) {
                                            error = language.text(
                                                "Die Geschwindigkeit kann erst nach dem Laden geändert werden.",
                                                "Playback speed can only be changed after loading."
                                            )
                                        }
                                    },
                                    label = { Text("${speed}×") }
                                )
                            }
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(language.text("Abschnitt wiederholen", "Repeat segment"),
                                modifier = Modifier.weight(1f))
                            Switch(
                                checked = repeatSegment,
                                onCheckedChange = { repeatSegment = it },
                                enabled = segmentStartMs != null && segmentEndMs != null
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(onClick = {
                                segmentStartMs = player?.currentPosition
                                segmentEndMs = null
                                repeatSegment = false
                            }) { Text(language.text("A setzen", "Set A")) }
                            OutlinedButton(onClick = {
                                val start = segmentStartMs
                                val end = player?.currentPosition
                                if (start == null || end == null || end <= start) {
                                    error = language.text(
                                        "Setze A und danach B an einer späteren Stelle.",
                                        "Set A, then set B at a later point."
                                    )
                                } else {
                                    segmentEndMs = end
                                    error = null
                                }
                            }) { Text(language.text("B setzen", "Set B")) }
                            TextButton(onClick = {
                                repeatSegment = false
                                segmentStartMs = null
                                segmentEndMs = null
                            }) { Text(language.text("Zurücksetzen", "Reset")) }
                        }
                        if (segmentStartMs != null && segmentEndMs != null) {
                            Text(
                                language.text(
                                    "Abschnitt ${formatPosition(segmentStartMs!!)}–${formatPosition(segmentEndMs!!)}",
                                    "Segment ${formatPosition(segmentStartMs!!)}–${formatPosition(segmentEndMs!!)}"
                                ),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
            }
            Text(language.text(
                "Sicher! C1/1: Passende Arbeitsbuchdateien tragen oft _L02_, _L05_ oder _L06_. Aspekte neu C1: Wähle im CD-Ordner (Kapitelbereich) den im Buch angegebenen Track; generische Titelnummern allein reichen nicht zur Zuordnung.",
                "Sicher! C1/1: Matching workbook files often contain _L02_, _L05_, or _L06_. Aspekte neu C1: Choose the track listed in the book from the matching chapter CD folder; generic track names alone are not enough to identify it."
            ), style = MaterialTheme.typography.bodySmall)
            error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        }
    }
}

private fun formatPosition(milliseconds: Int): String {
    val seconds = (milliseconds / 1_000).coerceAtLeast(0)
    return "%d:%02d".format(seconds / 60, seconds % 60)
}

private fun displayName(context: Context, uri: Uri): String? {
    val cursor = context.contentResolver.query(
        uri,
        arrayOf(OpenableColumns.DISPLAY_NAME),
        null,
        null,
        null
    ) ?: return null
    return try {
        if (!cursor.moveToFirst()) return null
        val nameColumn = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (nameColumn >= 0) cursor.getString(nameColumn) else null
    } finally {
        cursor.close()
    }
}

