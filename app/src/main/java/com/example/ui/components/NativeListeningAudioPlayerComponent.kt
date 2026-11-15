package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.InferenceQuestion
import com.example.data.model.ListeningLaboratoryItem
import com.example.data.model.ReductionAnnotation


import com.example.ui.C1ViewModel
import com.example.ui.theme.*


/** German TTS listening practice with written register examples and a local comprehension quiz. */
@Composable
fun NativeListeningAudioPlayerComponent(
    viewModel: C1ViewModel,
    modifier: Modifier = Modifier
) {
    val speechHelper = viewModel.speechHelper
    val helperIsPlaying by speechHelper.isPlaying.collectAsState()
    val utteranceId by speechHelper.currentUtteranceId.collectAsState()
    val ttsError by speechHelper.errorMessage.collectAsState()

    DisposableEffect(speechHelper) {
        onDispose { speechHelper.stop() }
    }

    // Written examples read by device TTS; spelling does not guarantee synthesized dialect.
    val clips = remember {
        listOf(
            ListeningLaboratoryItem(
                id = "native_clip_1",
                title = "Werkstatt & Süddeutschland",
                environment = "Technik & Montage",
                accentOrDialect = "Schwäbischer Dialekt (1.25x)",
                nativeSpeechSpeed = 1.25f,
                durationSeconds = 14,
                audioSpeakerText = "Also pass amol uff, Kerle! Wenn mer die Toleranze bei dera Nockawell net grad jetzt akkurat eistelle, no fliegt uns d'ganze Serie beim Prüfstand um d'Ohre! Des hamma doch geschtern scho g'habt!",
                blindComprehensionQuestions = listOf(
                    InferenceQuestion(
                        question = "Welche implizite Emotion und Handlungsabsicht dominiert die Aussage des Werkstattleiters?",
                        options = listOf(
                            "Frustration über einen wiederholten Fehler mit dringender Korrekturaufforderung",
                            "Freundliches Lob für die rasche Montage",
                            "Gelassene Verschiebung der Qualitätsprüfung auf nächste Woche"
                        ),
                        correctIndex = 0,
                        rationaleWhyOthersAreWrong = "Ausdrücke wie 'Kerle!', 'fliegt um d'Ohre' und 'des hamma scho g'habt' signalisieren akuten Ärger über Wiederholungsfehler."
                    )
                ),
                transcriptWithReductions = "Also pass [amol uff] (einmal auf), Kerle! Wenn [mer] (wir) die Toleranzen bei [dera] (dieser) Nockenwelle [net] (nicht) grad jetzt akkurat einstellen, [no] (dann) fliegt uns [d'ganze] (die ganze) Serie beim Prüfstand um [d'Ohre] (die Ohren)! Des [hamma] (haben wir) doch gestern schon gehabt!",
                reductionNotes = listOf(
                    ReductionAnnotation("amol uff", "einmal auf", "Schwäbische Partikelreduktion"),
                    ReductionAnnotation("hamma", "haben wir", "Umgangssprachliche Lautverschmelzung"),
                    ReductionAnnotation("fliegt um d'Ohre", "fliegt um die Ohren", "Metapher für kapitalen Maschinenausschuss")
                ),
                contextAndSubtextAnalysis = "Echtes C1-Hörverstehen im deutschen Mittelstand verlangt das sofortige Decodieren emotionaler Dringlichkeit trotz mundartlicher Lautverschmelzung."
            ),
            ListeningLaboratoryItem(
                id = "native_clip_2",
                title = "Vorstandssitzung: Krisendialog",
                environment = "Beruf & Führung",
                accentOrDialect = "Gehobenes Wirtschaftsdeutsch (1.15x)",
                nativeSpeechSpeed = 1.15f,
                durationSeconds = 16,
                audioSpeakerText = "In Anbetracht der unvorhergesehenen Lieferengpässe mussten wir eine strategische Neupriorisierung vornehmen, um unsere Qualitätsstandards nicht zu gefährden. Das steht völlig außer Frage.",
                blindComprehensionQuestions = listOf(
                    InferenceQuestion(
                        question = "Welches rhetorische Manöver wird durch 'strategische Neupriorisierung' vollzogen?",
                        options = listOf(
                            "Euphemistische Umschreibung für eine schmerzhafte Verzögerung oder Kürzung",
                            "Ein formeller Antrag auf Erhöhung der Mitarbeiterzahl",
                            "Eine Bestätigung, dass alle Termine vorfristig eingehalten werden"
                        ),
                        correctIndex = 0,
                        rationaleWhyOthersAreWrong = "'Strategische Neupriorisierung' dient in Führungskreisen der diplomatischen Kaschierung von Leistungsrückständen."
                    )
                ),
                transcriptWithReductions = "[In Anbetracht] (Wegen) der unvorhergesehenen Lieferengpässe mussten wir eine [strategische Neupriorisierung vornehmen] (umplanen), um unsere Qualitätsstandards nicht zu gefährden. Das [steht völlig außer Frage] (ist unbestreitbar).",
                reductionNotes = listOf(
                    ReductionAnnotation("In Anbetracht", "aufgrund / angesichts", "Kausalkonnektor im gehobenen Genitiv"),
                    ReductionAnnotation("Neupriorisierung vornehmen", "umplanen", "Feste Nomen-Verb-Verbindung (Funktionsverbgefüge)")
                ),
                contextAndSubtextAnalysis = "Souveräne Abwehr von Vorwürfen im C1-Führungsregister ohne defensive Füllwörter."
            ),
            ListeningLaboratoryItem(
                id = "native_clip_3",
                title = "Berliner Kiez-Debatte",
                environment = "Alltag & Diskurs",
                accentOrDialect = "Berliner Umgangssprache (1.25x)",
                nativeSpeechSpeed = 1.25f,
                durationSeconds = 15,
                audioSpeakerText = "Dit is doch reine Augenwischerei! Da klatschen se n bisschen Farbe an die Fassade und schwupps wird dit Ding als Luxussanierung deklariert. Wer soll n dit bitteschön noch berappen?",
                blindComprehensionQuestions = listOf(
                    InferenceQuestion(
                        question = "Was kritisiert der Sprecher mit dem Idiom 'reine Augenwischerei'?",
                        options = listOf(
                            "Eine oberflächliche Täuschung ohne substanziellen Mehrwert",
                            "Einen mangelhaften Farbanstrich an Fenstern",
                            "Zu hohe Gebühren der Denkmalschutzbehörde"
                        ),
                        correctIndex = 0,
                        rationaleWhyOthersAreWrong = "'Augenwischerei' bezeichnet stets das Vortäuschen falscher Tatsachen."
                    )
                ),
                transcriptWithReductions = "[Dit] (Das) is doch reine Augenwischerei! Da klatschen [se] (sie) ['n] (ein) bisschen Farbe an die Fassade und schwupps wird [dit Ding] (die Sache) als Luxussanierung deklariert. Wer soll ['n dit] (denn das) bitteschön noch [berappen] (bezahlen)?",
                reductionNotes = listOf(
                    ReductionAnnotation("dit", "das", "Berlinerischer Wandel von s zu t"),
                    ReductionAnnotation("berappen", "bezahlen", "Umgangssprachliches C1-Idiom für hohe Kosten")
                ),
                contextAndSubtextAnalysis = "Erfassung von schlagfertigem Berliner Alltagstonfall und gesellschaftlicher Frustration."
            ),
            ListeningLaboratoryItem(
                id = "native_clip_4",
                title = "Feuilleton: Medienkritik",
                environment = "Kultur & Medien",
                accentOrDialect = "Akademisches Schnellsprech (1.20x)",
                nativeSpeechSpeed = 1.20f,
                durationSeconds = 17,
                audioSpeakerText = "Man könnte fast meinen, die jüngste Debatte sei weniger einem aufrichtigen Erkenntnisgewinn als vielmehr dem kollektiven Bedürfnis nach medialer Selbstinszenierung geschuldet.",
                blindComprehensionQuestions = listOf(
                    InferenceQuestion(
                        question = "Welche Haltung drückt der Konjunktiv I 'sei geschuldet' hier aus?",
                        options = listOf(
                            "Ironisch-distanzierte Kritik an der Eitelkeit des Diskurses",
                            "Eine unumstößliche naturwissenschaftliche Tatsache",
                            "Eine rechtlich bindende Zeugenaussage"
                        ),
                        correctIndex = 0,
                        rationaleWhyOthersAreWrong = "Konjunktiv I in der Kulturkritik signalisiert feine Ironie und Distanzierung."
                    )
                ),
                transcriptWithReductions = "Man könnte fast meinen, die jüngste Debatte [sei] (wäre) weniger einem aufrichtigen Erkenntnisgewinn als vielmehr dem kollektiven Bedürfnis nach medialer Selbstinszenierung [geschuldet] (zuzuschreiben).",
                reductionNotes = listOf(
                    ReductionAnnotation("sei geschuldet", "ist begründet in", "Gehobener Dativ mit Kausalbedeutung"),
                    ReductionAnnotation("Erkenntnisgewinn", "Lerneffekt", "Typisches C1-Abstraktum")
                ),
                contextAndSubtextAnalysis = "Feuilleton-Deutsch: Subtile Ironie, hypothetischer Konjunktiv und anspruchsvolle Syntax."
            ),
            ListeningLaboratoryItem(
                id = "native_clip_5",
                title = "Tech-Pitch: Deployment-Sprint",
                environment = "Start-up & IT",
                accentOrDialect = "Modernes Tempo-Umgangsdeutsch (1.35x)",
                nativeSpeechSpeed = 1.35f,
                durationSeconds = 13,
                audioSpeakerText = "Ganz ehrlich: Wenn wir das Deployment jetzt übers Knie brechen, zerschießt es uns die gesamte Datenbankmigration. Lasst uns die Skalierung sauber aufsetzen, sonst zahlen wir später doppelt drauf!",
                blindComprehensionQuestions = listOf(
                    InferenceQuestion(
                        question = "Was bedeutet 'etwas übers Knie brechen' im Projektkontext?",
                        options = listOf(
                            "Eine voreilige, überhastete Entscheidung treffen",
                            "Ein Server-Kabel mechanisch beschädigen",
                            "Ein Projekt endgültig einstellen"
                        ),
                        correctIndex = 0,
                        rationaleWhyOthersAreWrong = "'Übers Knie brechen' ist die feste Redewendung für erzwungene Eile mit hohem Fehlerrisiko."
                    )
                ),
                transcriptWithReductions = "Ganz ehrlich: Wenn wir das Deployment jetzt [übers Knie brechen] (überstürzen), [zerschießt es uns] (zerstört es) die gesamte Datenbankmigration. Lasst uns die Skalierung sauber aufsetzen, sonst [zahlen wir doppelt drauf] (haben wir hohe Mehrkosten)!",
                reductionNotes = listOf(
                    ReductionAnnotation("übers Knie brechen", "überstürzen", "Klassische deutsche Redewendung"),
                    ReductionAnnotation("zerschießt es uns", "ruiniert es", "Umgangssprachlicher IT-Jargon")
                ),
                contextAndSubtextAnalysis = "Spontanes, schnelles Projektdeutsch unter Zeitdruck."
            )
        )
    }

    var selectedClipIndex by remember { mutableStateOf(0) }
    val currentClip = clips[selectedClipIndex]

    val isPlaying = helperIsPlaying && utteranceId == currentClip.id

    // TTS rate setting, not a measured native-speaker speed.
    var currentSpeed by remember(selectedClipIndex) { mutableStateOf(currentClip.nativeSpeechSpeed) }


    var showTranscript by remember { mutableStateOf(false) }
    var selectedQuizOption by remember(selectedClipIndex) { mutableStateOf<Int?>(null) }
    var quizSubmitted by remember(selectedClipIndex) { mutableStateOf(false) }



    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("native_listening_component"),
        shape = RoundedCornerShape(24.dp),
        color = SlateNavyCard,
        border = BorderStroke(1.dp, SlateNavyBorder.copy(alpha = 0.8f)),
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .background(GlassCardGradient)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header: Component Badge, Title, and Dialect Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = NeonMint.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, NeonMint.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = "DEUTSCHE TTS-ÜBUNG",
                                color = NeonMint,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = "UNBEWERTETE HÖRPRAXIS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            letterSpacing = 1.sp
                        )
                    }

                    Text(
                        text = "Sprachausgabe & Textverständnis",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }

                // Dialect / Speed Badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SlateNavySurface,
                    border = BorderStroke(1.dp, NeonMint.copy(alpha = 0.35f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = null,
                            tint = NeonMint,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "TTS ${currentSpeed}x",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonMint
                        )
                    }
                }
            }

            Text(
                text = "Synthetische deutsche Gerätestimme (TTS), keine muttersprachliche Aufnahme. Dialektangaben beschreiben nur die Textvorlage; die Stimme bildet Dialekt, Betonung und Lautverschmelzungen nicht verlässlich ab. Animation ist dekorativ. Kein C1- oder Prüfungsnachweis.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            ttsError?.let { message ->
                Text(text = message, fontSize = 12.sp, color = NeonCoral, modifier = Modifier.testTag("listening_tts_error"))
            }

            // Carousel: Selectable TTS texts
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(clips) { index, clip ->
                    val isSelected = index == selectedClipIndex
                    Surface(
                        onClick = {
                            if (isSelected && isPlaying) {
                                speechHelper.stop()
                            } else {
                                speechHelper.stop()
                                selectedClipIndex = index
                                showTranscript = false
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) ElectricCyan.copy(alpha = 0.16f) else SlateNavySurface,
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) ElectricCyan.copy(alpha = 0.8f) else SlateNavyBorder.copy(alpha = 0.6f)
                        ),
                        modifier = Modifier.testTag("listening_clip_tab_$index")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = if (isSelected && isPlaying) Icons.Default.GraphicEq else Icons.Default.Headphones,
                                contentDescription = null,
                                tint = if (isSelected) ElectricCyan else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = clip.title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Active Audio Player Card with Waveform & Controls
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = SlateNavySurface,
                border = BorderStroke(1.dp, SlateNavyBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Title and Register Subtext
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = currentClip.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Textvorlage: ${currentClip.accentOrDialect}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ElectricViolet.copy(alpha = 0.18f),
                            border = BorderStroke(0.5.dp, ElectricVioletLight.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = currentClip.environment,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = ElectricVioletLight,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    // Dynamic Animated Waveform Equalizer (16 animated bars)
                    AnimatedEqualizerWaveform(
                        isPlaying = isPlaying,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    )

                    Text(
                        text = if (isPlaying) "TTS-Wiedergabe läuft · Dauer nicht ermittelt" else "TTS bereit zum Startversuch · keine gemessene Audiodauer",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // TTS supports restarting and stopping here, not seeking.
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Restart the complete text.
                        IconButton(
                            onClick = {
                                speechHelper.stop()

                                speechHelper.speak(currentClip.audioSpeakerText, currentSpeed, currentClip.id)
                            },
                            modifier = Modifier
                                .size(44.dp)
                                .testTag("listening_rewind_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Replay,
                                contentDescription = "Erneut abspielen",
                                tint = ElectricCyan,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // Master Play / Stop Button
                        Button(
                            onClick = {
                                if (isPlaying) {
                                    speechHelper.stop()
                                } else {
                                    speechHelper.speak(currentClip.audioSpeakerText, currentSpeed, currentClip.id)

                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isPlaying) NeonCoral else NeonMint,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier
                                .height(48.dp)
                                .padding(horizontal = 8.dp)
                                .testTag("listening_play_button")
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Stop" else "Play",
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isPlaying) "Wiedergabe stoppen" else "TTS anhören (${currentSpeed}x)",
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // Stop / Reset Button
                        IconButton(
                            onClick = {
                                speechHelper.stop()
                            },
                            modifier = Modifier
                                .size(44.dp)
                                .testTag("listening_stop_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.StopCircle,
                                contentDescription = "Stoppen",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    // Speed Selector Chips (0.9x, 1.0x, 1.15x, 1.25x, 1.35x)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Sprechtempo:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(0.9f, 1.0f, 1.15f, 1.25f, 1.35f).forEach { speed ->
                                val isSelected = currentSpeed == speed
                                Surface(
                                    onClick = {
                                        currentSpeed = speed
                                        if (isPlaying) {
                                            speechHelper.stop()
                                            speechHelper.speak(currentClip.audioSpeakerText, speed, currentClip.id)
                                        }
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) NeonMint.copy(alpha = 0.2f) else SlateNavyCard,
                                    border = BorderStroke(
                                        1.dp,
                                        if (isSelected) NeonMint else SlateNavyBorder
                                    ),
                                    modifier = Modifier.testTag("speed_chip_${speed}x")
                                ) {
                                    Text(
                                        text = "${speed}x",
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                                        color = if (isSelected) NeonMint else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Training Actions: Blind-Hören vs. Transkript enthüllen
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (showTranscript) "Transkript & Lautverschmelzungen:" else "Blind-Hören (Gehör decodieren)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                TextButton(
                    onClick = { showTranscript = !showTranscript },
                    modifier = Modifier.testTag("listening_transcript_toggle")
                ) {
                    Icon(
                        imageVector = if (showTranscript) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null,
                        tint = ElectricCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (showTranscript) "Verbergen" else "Transkript anzeigen",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElectricCyan
                    )
                }
            }

            // Revealed Transcript Accordion with Reduction Annotations
            AnimatedVisibility(
                visible = showTranscript,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = SlateNavySurface,
                    border = BorderStroke(1.dp, SlateNavyBorder.copy(alpha = 0.7f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = currentClip.transcriptWithReductions,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White,
                            lineHeight = 18.sp
                        )

                        Divider(color = SlateNavyBorder, thickness = 0.5.dp)

                        Text(
                            text = "Erläuterungen zur Textvorlage (nicht aus Audio gemessen):",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonMint
                        )

                        currentClip.reductionNotes.forEach { note ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    text = "•",
                                    color = NeonMint,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Column {
                                    Text(
                                        text = "„${note.spokenForm}“ = „${note.standardWrittenForm}“",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = note.registerExplanation,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Quick Comprehension Question Check
            currentClip.blindComprehensionQuestions.firstOrNull()?.let { quiz ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = SlateNavySurface,
                    border = BorderStroke(1.dp, SlateNavyBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                tint = ElectricCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Verständnis-Check:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = ElectricCyan
                            )
                        }

                        Text(
                            text = quiz.question,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )

                        quiz.options.forEachIndexed { optIndex, optionText ->
                            val isSelected = selectedQuizOption == optIndex
                            val isCorrect = optIndex == quiz.correctIndex

                            val borderColor = when {
                                quizSubmitted && isCorrect -> NeonMint
                                quizSubmitted && isSelected && !isCorrect -> NeonCoral
                                isSelected -> ElectricCyan
                                else -> SlateNavyBorder
                            }

                            val bgColor = when {
                                quizSubmitted && isCorrect -> NeonMint.copy(alpha = 0.12f)
                                quizSubmitted && isSelected && !isCorrect -> NeonCoral.copy(alpha = 0.12f)
                                isSelected -> ElectricCyan.copy(alpha = 0.1f)
                                else -> SlateNavyDark
                            }

                            Surface(
                                onClick = {
                                    if (!quizSubmitted) {
                                        selectedQuizOption = optIndex
                                        quizSubmitted = true
                                        viewModel.recordPracticeActivity("TTS-Textverständnis (unbewertete Übung)")
                                    }
                                },
                                shape = RoundedCornerShape(10.dp),
                                color = bgColor,
                                border = BorderStroke(1.dp, borderColor),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("listening_quiz_option_$optIndex")
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = when {
                                            quizSubmitted && isCorrect -> Icons.Default.CheckCircle
                                            quizSubmitted && isSelected && !isCorrect -> Icons.Default.Cancel
                                            isSelected -> Icons.Default.RadioButtonChecked
                                            else -> Icons.Default.RadioButtonUnchecked
                                        },
                                        contentDescription = null,
                                        tint = borderColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = optionText,
                                        fontSize = 11.sp,
                                        color = if (isSelected || (quizSubmitted && isCorrect)) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 15.sp
                                    )
                                }
                            }
                        }

                        if (quizSubmitted) {
                            Text(
                                text = quiz.rationaleWhyOthersAreWrong,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Animated 16-bar Equalizer Waveform that dynamically pulses when audio is active.
 */
@Composable
private fun AnimatedEqualizerWaveform(
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform_anim")

    // Decorative animation only; these bars are not measured audio frequencies.
    val heights = (0 until 18).map { i ->
        val duration = 400 + (i % 5) * 80
        val minHeight = 4.dp
        val maxHeight = (16 + (i * 7) % 24).dp

        val animatedHeight by infiniteTransition.animateValue(
            initialValue = if (isPlaying) minHeight else 4.dp,
            targetValue = if (isPlaying) maxHeight else 4.dp,
            typeConverter = androidx.compose.ui.unit.Dp.VectorConverter,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = duration, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "wave_bar_$i"
        )
        animatedHeight
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(SlateNavyDark.copy(alpha = 0.5f))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            heights.forEachIndexed { idx, barHeight ->
                val barColor = when {
                    idx % 3 == 0 -> NeonMint
                    idx % 3 == 1 -> ElectricCyan
                    else -> ElectricVioletLight
                }
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(barHeight)
                        .clip(RoundedCornerShape(2.dp))
                        .background(if (isPlaying) barColor else SlateNavyBorder)
                )
            }
        }
    }
}
