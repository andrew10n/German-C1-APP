package com.example.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.programme.AiFeedbackPrompt

@Composable
fun AiFeedbackShareButton(
    task: String,
    response: String,
    evidenceType: String = "schriftlicher Übungstext",
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showConsent by remember { mutableStateOf(false) }
    var shareUnavailable by remember { mutableStateOf(false) }
    var shareText by remember(task, response, evidenceType) { mutableStateOf("") }

    OutlinedButton(
        onClick = {
            shareText = AiFeedbackPrompt.build(task, response, evidenceType)
            shareUnavailable = false
            showConsent = true
        },
        enabled = response.isNotBlank(),
        modifier = modifier
    ) {
        Text("Externe KI-Rückmeldung anfragen")
    }

    if (shareUnavailable) {
        Text("Die Android-Teilen-Funktion ist auf diesem Gerät nicht verfügbar.")
    }

    if (showConsent) {
        AlertDialog(
            onDismissRequest = { showConsent = false },
            title = { Text("Antwort extern teilen?") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Prüfe oder bearbeite den genauen Text unten. Erst nach deiner Auswahl einer App in Androids Teilen-Menü wird er übergeben. Teile keine vertraulichen Daten."
                    )
                    OutlinedTextField(
                        value = shareText,
                        onValueChange = { shareText = it },
                        label = { Text("Zu teilender Text") },
                        modifier = Modifier.fillMaxWidth()
                            .heightIn(min = 160.dp, max = 320.dp)
                            .testTag("external_feedback_share_text"),
                        minLines = 6,
                        maxLines = 10
                    )
                }
            },
            confirmButton = {
                TextButton(
                    enabled = shareText.isNotBlank(),
                    onClick = {
                        showConsent = false
                        val sendIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, shareText)
                        }
                        try {
                            context.startActivity(Intent.createChooser(sendIntent, "Feedback-App auswählen"))
                        } catch (_: ActivityNotFoundException) {
                            shareUnavailable = true
                        }
                    }
                ) {
                    Text("Teilen-Auswahl öffnen")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConsent = false }) {
                    Text("Abbrechen")
                }
            }
        )
    }
}
