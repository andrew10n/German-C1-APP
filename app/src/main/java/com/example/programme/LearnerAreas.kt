package com.example.programme

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.DailyStreakState
import com.example.ui.theme.BerlinTheme
import java.text.DateFormat
import java.util.Date
import java.util.Locale

@Composable
internal fun LearnerBottomNav(
    selected: String,
    language: ProgrammeLanguage,
    onSelect: (String) -> Unit
) {
    val items = listOf(
        Triple("today", language.text("Heute", "Today"), Icons.Default.Home),
        Triple("learn", language.text("Lernen", "Learn"), Icons.AutoMirrored.Filled.MenuBook),
        Triple("progress", language.text("Fortschritt", "Progress"), Icons.Default.Insights),
        Triple("profile", language.text("Profil", "Profile"), Icons.Default.Person)
    )
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    ) {
        items.forEach { (id, label, icon) ->
            NavigationBarItem(
                selected = id == selected,
                onClick = { onSelect(id) },
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label) },
                alwaysShowLabel = true,
                modifier = Modifier.testTag("learner_nav_$id")
            )
        }
    }
}

@Composable
internal fun LearnArea(
    modifier: Modifier = Modifier,
    language: ProgrammeLanguage,
    onOpen: (CurriculumUnit, String) -> Unit,
    onOpenListening: () -> Unit,
    onOpenSpeaking: () -> Unit,
    onOpenConversation: () -> Unit,
    onOpenMediation: () -> Unit
) {
    val authoredUnits = CurriculumCatalogue.units.filter { it.content != null }
    var browsing by rememberSaveable { mutableStateOf(false) }
    var searchOpen by rememberSaveable { mutableStateOf(false) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var expandedStage by rememberSaveable { mutableStateOf<Int?>(null) }
    val matchingUnits = authoredUnits.filter { unit ->
        searchQuery.isBlank() ||
            unit.title.contains(searchQuery.trim(), ignoreCase = true) ||
            unit.objectives.any { it.contains(searchQuery.trim(), ignoreCase = true) } ||
            unit.contexts.values.any { it.contains(searchQuery.trim(), ignoreCase = true) }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize().testTag("learn_area"),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(language.text("LERNEN", "LEARN"), style = MaterialTheme.typography.labelMedium, color = BerlinTheme.accentInk)
                Text(language.text("Wähle eine Lernfläche.", "Choose a practice area."), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text(language.text(
                    "Kurze, konkrete Übungen – der Rest bleibt im Hintergrund.",
                    "Short, focused practice. Everything else stays out of the way."
                ), style = MaterialTheme.typography.bodyMedium)
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                LearnCard("H", language.text("Hören", "Listening"), language.text("Signale", "Signals"), Modifier.weight(1f), onOpenListening)
                LearnCard("S", language.text("Sprechen", "Speaking"), language.text("Klarheit", "Clarity"), Modifier.weight(1f), onOpenSpeaking)
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                LearnCard(
                    "W",
                    language.text("Schreiben / Mediation", "Writing / mediation"),
                    language.text("adressatengerecht erklären", "reframe for an audience"),
                    Modifier.weight(1f),
                    onOpenMediation
                )
                LearnCard("I", language.text("Interaktion", "Interaction"), language.text("Reagieren", "Respond"), Modifier.weight(1f), onOpenConversation)
            }
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Text(language.text("Kursweg", "Course path"), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                TextButton(onClick = { browsing = !browsing }) {
                    Text(
                        if (browsing) language.text("Schließen", "Close")
                        else language.text("Durchsuchen", "Browse")
                    )
                }
            }
        }
        if (browsing) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { searchOpen = !searchOpen }) {
                        Text(if (searchOpen) language.text("Suche schließen", "Hide search")
                        else language.text("Einheiten suchen", "Search units"))
                    }
                }
                if (searchOpen) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth().testTag("learn_course_search"),
                        label = { Text(language.text("Titel, Ziel oder Kontext", "Title, goal or context")) },
                        singleLine = true
                    )
                }
            }
            CurriculumCatalogue.stages.forEachIndexed { stage, stageTitle ->
                val stageUnits = matchingUnits.filter { it.stage == stage }
                if (stageUnits.isNotEmpty()) {
                    item(key = "learn-stage-$stage") {
                        Card(
                            modifier = Modifier.fillMaxWidth().clickable {
                                expandedStage = if (expandedStage == stage) null else stage
                            },
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        language.text("STUFE $stage", "STAGE $stage"),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = BerlinTheme.accentInk
                                    )
                                    Text(localizedStageTitle(stage, language, stageTitle), fontWeight = FontWeight.Bold)
                                    Text(
                                        language.text(
                                            "${stageUnits.size} Einheiten",
                                            "${stageUnits.size} units"
                                        ),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    if (stage == 0) {
                                        Text(
                                            language.text(
                                                "Optionale Orientierung · kein Zugangsschritt",
                                                "Optional orientation · not an access step"
                                            ),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Text(if (expandedStage == stage) "−" else "+", style = MaterialTheme.typography.titleLarge)
                            }
                        }
                    }
                    if (expandedStage == stage || searchQuery.isNotBlank()) {
                        items(stageUnits, key = { it.id }) { unit ->
                            Card(
                                modifier = Modifier.fillMaxWidth().clickable { onOpen(unit, "retrieval") },
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(unit.title, fontWeight = FontWeight.Bold)
                                    Text(unit.objectives.firstOrNull().orEmpty(), style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }
            if (matchingUnits.isEmpty()) {
                item {
                    Text(
                        language.text("Keine passenden Einheiten gefunden.", "No matching units found."),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun LearnCard(symbol: String, title: String, subtitle: String, modifier: Modifier, onClick: () -> Unit) {
    Card(modifier = modifier.clickable(onClick = onClick), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(symbol, style = MaterialTheme.typography.headlineSmall, color = BerlinTheme.accentInk)
            Text(title, fontWeight = FontWeight.Bold)
            Text(subtitle, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
internal fun ProgressArea(
    modifier: Modifier = Modifier,
    language: ProgrammeLanguage,
    attempts: List<Attempt>,
    now: Long,
    adaptiveDecision: AdaptiveControlDecision,
    weaknessProfile: WeaknessProfile,
    streakState: DailyStreakState,
    onOpenSettings: () -> Unit
) {
    val units = CurriculumCatalogue.units.filter { it.content != null }
    val completed = AdaptiveDiagnosticRoute.completedAuthoredUnits(attempts, now)
    val total = units.size
    val practiceCounts = mapOf(
        "listening" to attempts.count { it.taskId == "listening" },
        "speaking" to attempts.count { it.taskId == "speaking" },
        "writing" to attempts.count { it.taskId in setOf("writing", "controlled", "transfer") },
        "interaction" to attempts.count { it.taskId == "interaction" }
    )
    val maxPracticeCount = practiceCounts.values.maxOrNull()?.coerceAtLeast(1) ?: 1
    val scheduledReviews = units.mapNotNull { unit ->
        AdaptiveDiagnosticRoute.nextReview(attempts, unit.id, now)?.let { unit to it }
    }.sortedBy { it.second }
    val dueReviews = scheduledReviews.filter { it.second <= now }
    val nextReview = scheduledReviews.firstOrNull { it.second > now }
    val locale = if (language == ProgrammeLanguage.EN) Locale.ENGLISH else Locale.GERMAN
    val dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, locale)
    LazyColumn(
        modifier = modifier.fillMaxSize().testTag("progress_area"),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(language.text("FORTSCHRITT", "PROGRESS"), style = MaterialTheme.typography.labelMedium, color = BerlinTheme.accentInk)
                Text(language.text("Dein Fortschritt", "Your progress"), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text(language.text(
                    "Übungsfortschritt und Kompetenznachweise bleiben getrennt.",
                    "Practice progress and proficiency evidence are kept separate."
                ), style = MaterialTheme.typography.bodyMedium)
            }
        }
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text(language.text("Kursfortschritt", "Course progress"), fontWeight = FontWeight.Bold)
                        Text("$completed / $total", fontWeight = FontWeight.Bold, color = BerlinTheme.accentInk)
                    }
                    LinearProgressIndicator(
                        progress = { if (total == 0) 0f else completed.toFloat() / total },
                        modifier = Modifier.fillMaxWidth(),
                        color = BerlinTheme.accent,
                        trackColor = BerlinTheme.line
                    )
                    Text(
                        language.text(
                            "26 Kernlektionen und 2 optionale Orientierungseinheiten. Ein Checkpoint ist Übungsfortschritt, keine C1- oder Kompetenznote.",
                            "26 core lessons and 2 optional orientation units. A checkpoint is practice progress, not a C1 or proficiency score."
                        ),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        language.text(
                            "Orientierung bleibt optional und schaltet keine Kernlektion frei.",
                            "Orientation remains optional and does not unlock core lessons."
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        language.text(
                            "Aktuelle Serie: ${streakState.currentStreak} Tage",
                            "Current streak: ${streakState.currentStreak} days"
                        ),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
        item {
            ProgrammeAreaCard(language.text("Übungsbalance", "Practice balance")) {
                Text(
                    language.text(
                        "Häufigkeit gespeicherter Antworten · kein Fähigkeitsvergleich",
                        "Frequency of saved responses · not a skill comparison"
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                practiceCounts.forEach { (skill, count) ->
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text(skillLabel(skill, language))
                            Text("$count", style = MaterialTheme.typography.labelMedium)
                        }
                        LinearProgressIndicator(
                            progress = { count.toFloat() / maxPracticeCount },
                            modifier = Modifier.fillMaxWidth(),
                            color = BerlinTheme.accent,
                            trackColor = BerlinTheme.line
                        )
                    }
                }
            }
        }
        item {
            ProgrammeAreaCard(language.text("Was gut läuft", "What is going well")) {
                Text(
                    if (completed > 0) {
                        language.text(
                            "$completed Übungs-Checkpoint(s) abgeschlossen. Das ist ein belegter Lernfortschritt, keine Aussage über deine gesamte Sprachkompetenz.",
                            "$completed practice checkpoint(s) completed. This is a verified practice milestone, not a claim about overall language ability."
                        )
                    } else {
                        language.text(
                            "Noch kein Übungs-Checkpoint abgeschlossen. Das sagt nichts über dein Deutsch aus.",
                            "No practice checkpoints completed yet. This says nothing about your German ability."
                        )
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
                HorizontalDivider()
                Text(language.text("Am häufigsten geübt", "Most practised"), fontWeight = FontWeight.SemiBold)
                val mostPractised = practiceCounts.filterValues { it == practiceCounts.values.maxOrNull() && it > 0 }.keys
                Text(
                    mostPractised.takeIf { it.isNotEmpty() }?.joinToString { skillLabel(it, language) }
                        ?: language.text("Noch keine gespeicherten Antworten.", "No saved responses yet."),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        item {
            ProgrammeAreaCard(language.text("Aktuelle Fokusbereiche", "Current focus areas")) {
                if (weaknessProfile.summaries.isEmpty()) {
                    Text(
                        language.text(
                            "Noch kein klares Übungssignal. Das ist keine Bewertung und kein Mangel.",
                            "No clear practice signal yet. This is not a grade or a deficiency."
                        ),
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    weaknessProfile.top(3).forEach { signal ->
                        Text(
                            "• ${signalLabel(signal.signal, language)} · ${signal.sampleCount} " +
                                language.text("Übungshinweis(e)", "practice signal(s)"),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                Text(
                    language.text(
                        "Diese Hinweise sortieren die nächste Übung. Sie sind keine Diagnose deiner Gesamtkompetenz.",
                        "These signals help order the next practice step; they are not a diagnosis of overall ability."
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        item {
            ProgrammeAreaCard(language.text("Nächster adaptiver Plan", "Next adaptive plan")) {
                Text(
                    adaptiveDecision.primaryTaskId?.let { taskLabel(it, language) }
                        ?: language.text("Kernpfad", "Core path"),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = BerlinTheme.accentInk
                )
                Text(
                    language.text(
                        "Nächste Aufgabenreihenfolge",
                        "Next task order"
                    ),
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    adaptiveDecision.taskOrder.take(4).joinToString(" → ") { taskLabel(it, language) },
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    adaptiveExplanation(adaptiveDecision, weaknessProfile, language),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        item {
            ProgrammeAreaCard(language.text("Wiederholungsplan", "Review schedule")) {
                Text(
                    language.text(
                        "${dueReviews.size} Wiederholung(en) jetzt fällig",
                        "${dueReviews.size} review(s) due now"
                    ),
                    fontWeight = FontWeight.SemiBold
                )
                dueReviews.take(3).forEach { (unit, _) ->
                    Text("• ${unit.title}", style = MaterialTheme.typography.bodySmall)
                }
                Text(
                    nextReview?.let { (unit, timestamp) ->
                        language.text(
                            "Als Nächstes: ${unit.title} · ${dateFormat.format(Date(timestamp))}",
                            "Next: ${unit.title} · ${dateFormat.format(Date(timestamp))}"
                        )
                    } ?: language.text(
                        "Noch keine spätere Wiederholung geplant.",
                        "No future review scheduled yet."
                    ),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        item {
            ProgrammeAreaCard(language.text("Evidenzgrenzen", "Evidence boundaries")) {
                Text(
                    language.text(
                        "Übungsantworten und Checkpoints zeigen, was du bearbeitet hast. Sie belegen weder ein C1-Niveau noch Hörverstehen mit authentischem Audio, Aussprache oder unabhängig bewertete Sprachkompetenz.",
                        "Responses and checkpoints show what you practised. They do not prove C1 level, authentic-audio comprehension, pronunciation or independently assessed language ability."
                    ),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    language.text(
                        "Eine formale Einschätzung braucht geeignete Aufgaben und eine qualifizierte, unabhängige Beurteilung.",
                        "A formal assessment requires suitable tasks and qualified, independent evaluation."
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        item {
            OutlinedButton(onClick = onOpenSettings, modifier = Modifier.fillMaxWidth()) {
                Text(language.text("Einstellungen und Datenschutz", "Settings and privacy"))
            }
        }
    }
}

@Composable
private fun ProgrammeAreaCard(title: String, content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            content()
        }
    }
}

private fun skillLabel(skill: String, language: ProgrammeLanguage): String = when (skill) {
    "listening" -> language.text("Hören", "Listening")
    "speaking" -> language.text("Sprechen", "Speaking")
    "writing" -> language.text("Schreiben / Mediation", "Writing / mediation")
    else -> language.text("Interaktion", "Interaction")
}

@Composable
internal fun ProfileArea(
    modifier: Modifier = Modifier,
    language: ProgrammeLanguage,
    onOpenSettings: () -> Unit,
    onOpenOnboarding: () -> Unit,
    onOpenAudioPreferences: () -> Unit,
    onOpenPracticeTools: () -> Unit,
    onOpenIntelligenceProfile: () -> Unit
) {
    var advancedOpen by rememberSaveable { mutableStateOf(false) }
    LazyColumn(
        modifier = modifier.fillMaxSize().testTag("profile_area"),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(language.text("PROFIL", "PROFILE"), style = MaterialTheme.typography.labelMedium, color = BerlinTheme.accentInk)
                Text(language.text("Dein Raum", "Your space"), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text(language.text(
                    "Persönliche Einstellungen, Datenschutz und Lerngrenzen.",
                    "Personal settings, privacy and learning boundaries."
                ), style = MaterialTheme.typography.bodyMedium)
            }
        }
        item {
            OutlinedButton(onClick = onOpenSettings, modifier = Modifier.fillMaxWidth()) {
                Text(language.text("Einstellungen öffnen", "Open settings"))
            }
        }
        item {
            OutlinedButton(onClick = onOpenAudioPreferences, modifier = Modifier.fillMaxWidth()) {
                Text(language.text("Audio und Hörquellen", "Audio and listening sources"))
            }
        }
        item {
            TextButton(
                onClick = { advancedOpen = !advancedOpen },
                modifier = Modifier.fillMaxWidth().testTag("profile_advanced_toggle")
            ) {
                Text(
                    if (advancedOpen) language.text("Erweitert schließen", "Hide advanced")
                    else language.text("Erweiterte Bereiche", "Advanced")
                )
            }
        }
        if (advancedOpen) {
            item {
                OutlinedButton(onClick = onOpenPracticeTools, modifier = Modifier.fillMaxWidth()) {
                    Text(language.text("Weitere Übungstools", "More practice tools"))
                }
            }
            item {
                OutlinedButton(onClick = onOpenIntelligenceProfile, modifier = Modifier.fillMaxWidth()) {
                    Text(language.text("Übungsprofil öffnen", "Open practice profile"))
                }
            }
            item {
                TextButton(onClick = onOpenOnboarding, modifier = Modifier.fillMaxWidth()) {
                    Text(language.text("Lern- und Evidenzgrenzen ansehen", "Review learning and evidence boundaries"))
                }
            }
        }
    }
}

private fun localizedStageTitle(stage: Int, language: ProgrammeLanguage, fallback: String): String = when (stage) {
    1 -> language.text("B2 festigen", "B2 consolidation")
    2 -> language.text("Fortgeschrittenes Verstehen", "Advanced comprehension")
    3 -> language.text("Fortgeschrittenes Produzieren", "Advanced production")
    4 -> language.text("Pragmatik in Interaktion", "Interaction pragmatics")
    5 -> language.text("Integration und Sprachmittlung", "Integration and mediation")
    6 -> language.text("Selbstständiger Transfer", "Independent transfer practice")
    else -> fallback.replaceFirstChar { it.uppercase() }
}
