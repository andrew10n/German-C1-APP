package com.example.ui

import android.content.Intent
import android.content.ActivityNotFoundException
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.MainApplication
import com.example.programme.ExternalListeningLibrary
import com.example.ui.theme.BarlowCondensedFontFamily
import com.example.ui.theme.BerlinTheme

@Composable
internal fun ExternalListeningSourcesSection() {
    val context = LocalContext.current
    val practiceRepository = remember(context.applicationContext) {
        (context.applicationContext as MainApplication).externalListeningPracticeRepository
    }
    var launchUnavailable by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Öffentliche Originalquellen",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = BarlowCondensedFontFamily,
            color = BerlinTheme.ink
        )
        Text(
            text = "Die Links öffnen ausschließlich offizielle Anbieter. Wähle einen Beitrag, höre zuerst ohne Text und notiere deine Antwort, bevor du Untertitel oder Transkript prüfst. Die App lädt keine Medien herunter; Verfügbarkeit und Rechte bleiben beim Anbieter.",
            fontSize = 12.sp,
            color = BerlinTheme.ink2,
            lineHeight = 17.sp
        )
        Text(
            "Deine Notizen werden automatisch lokal gespeichert und sind im Export enthalten. Sie zählen nicht als Lernfortschritt.",
            fontSize = 11.sp,
            color = BerlinTheme.ink2
        )
        ExternalListeningLibrary.sources.forEach { source ->
            val savedDraft = remember(source.id) { practiceRepository.load(source.id) }
            var itemReference by remember(source.id) { mutableStateOf(savedDraft.itemReference) }
            var firstPass by remember(source.id) { mutableStateOf(savedDraft.firstPass) }
            var secondPass by remember(source.id) { mutableStateOf(savedDraft.secondPass) }
            var showComparison by remember(source.id) { mutableStateOf(savedDraft.comparisonOpened) }
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = BerlinTheme.surface),
                border = BorderStroke(1.dp, BerlinTheme.line)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = source.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = BerlinTheme.ink
                    )
                    Text(source.formats, fontSize = 12.sp, color = BerlinTheme.accentInk)
                    Text(source.focus, fontSize = 12.sp, color = BerlinTheme.ink2)
                    Text(source.transcriptNote, fontSize = 11.sp, color = BerlinTheme.ink2)
                    source.practiceTasks.forEachIndexed { index, task ->
                        Text(
                            text = "${index + 1}. $task",
                            fontSize = 12.sp,
                            color = BerlinTheme.ink,
                            lineHeight = 17.sp
                        )
                    }
                    OutlinedButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(source.url))
                            try {
                                context.startActivity(intent)
                                launchUnavailable = false
                            } catch (_: ActivityNotFoundException) {
                                launchUnavailable = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth().testTag("external_audio_${source.id}")
                    ) {
                        Text("Offizielle Beiträge öffnen")
                    }
                    OutlinedTextField(
                        value = itemReference,
                        onValueChange = {
                            itemReference = it
                            practiceRepository.save(source.id, it, firstPass, secondPass, showComparison)
                        },
                        label = { Text("Folgentitel oder Link (optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Text(
                        "1. Hörversuch · erst ohne Untertitel",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = BerlinTheme.ink
                    )
                    OutlinedTextField(
                        value = firstPass,
                        onValueChange = {
                            firstPass = it
                            practiceRepository.save(source.id, itemReference, it, secondPass, showComparison)
                        },
                        label = { Text("Kernaussage, Haltung und gehörten Beleg") },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 88.dp),
                        minLines = 3
                    )
                    Button(
                        onClick = {
                            showComparison = true
                            practiceRepository.save(source.id, itemReference, firstPass, secondPass, true)
                        },
                        enabled = firstPass.isNotBlank(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Ersten Hörversuch festhalten")
                    }
                    if (showComparison) {
                        Text(
                            "2. Hörversuch · spiele denselben Abschnitt erneut. Erst jetzt darfst du verfügbare Untertitel oder ein Transkript prüfen. Markiere, was du korrigierst; automatische Untertitel sind keine verlässliche Quelle.",
                            fontSize = 12.sp,
                            color = BerlinTheme.ink2,
                            lineHeight = 17.sp
                        )
                        OutlinedTextField(
                            value = secondPass,
                            onValueChange = {
                                secondPass = it
                                practiceRepository.save(source.id, itemReference, firstPass, it, showComparison)
                            },
                            label = { Text("Korrekturen, Unsicherheiten und neue Belege") },
                            modifier = Modifier.fillMaxWidth().heightIn(min = 88.dp),
                            minLines = 3
                        )
                        AiFeedbackShareButton(
                            task = "Hörnotizen zu ${source.title}, Beitrag: $itemReference. Prüfe nur Klarheit, Begründung und die Trennung von Gehörtem und Vermutung. Ohne Originalton oder Transkript kannst du die inhaltliche Richtigkeit nicht bestätigen.",
                            response = "Beitrag: $itemReference\n\n1. Hörversuch:\n$firstPass\n\n2. Hörversuch / Vergleich:\n$secondPass",
                            evidenceType = "Hörnotizen; Originalton und Transkript werden nicht mitgeteilt"
                        )
                    }
                    if (itemReference.isNotBlank() || firstPass.isNotBlank() || secondPass.isNotBlank() || showComparison) {
                        TextButton(
                            onClick = {
                                firstPass = ""
                                secondPass = ""
                                itemReference = ""
                                showComparison = false
                                practiceRepository.clear(source.id)
                            }
                        ) {
                            Text("Notizen löschen")
                        }
                    }
                }
            }
        }
        if (launchUnavailable) {
            Text(
                text = "Kein passender Browser oder Player ist auf diesem Gerät verfügbar.",
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp
            )
        }
    }
}
