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
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
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
import androidx.compose.runtime.remember
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
        Triple("lessons", language.text("Lernweg", "Lessons"), Icons.AutoMirrored.Filled.MenuBook),
        Triple("progress", language.text("Fortschritt", "Progress"), Icons.Default.Insights),
        Triple("settings", language.text("Einstellungen", "Settings"), Icons.Default.Settings)
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
    attempts: List<Attempt>,
    now: Long,
    historyAvailable: Boolean,
    adaptiveDecision: AdaptiveControlDecision,
    weaknessProfile: WeaknessProfile,
    streakState: DailyStreakState,
    dailyMinutes: Int,
    onOpen: (CurriculumUnit, String) -> Unit
) {
    val authoredUnits = CurriculumCatalogue.coreUnits.filter { it.content != null }
    val nextUnit = adaptiveDecision.recommendedUnitId
        ?.let { recommendedId -> authoredUnits.firstOrNull { it.id == recommendedId } }
    val sessionTask = adaptiveDecision.primaryTaskId ?: "retrieval"
    val hasStarted = nextUnit != null && attempts.any {
        it.unitId == nextUnit.id && it.timestamp <= now
    }
    var searchOpen by rememberSaveable { mutableStateOf(false) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var expandedStage by rememberSaveable(nextUnit?.stage) { mutableStateOf(nextUnit?.stage) }
    fun matchesQuery(unit: CurriculumUnit, query: String): Boolean {
        val term = query.trim()
        return term.isBlank() ||
            unit.title.contains(term, ignoreCase = true) ||
            unit.objectives.any { it.contains(term, ignoreCase = true) } ||
            unit.contexts.values.any { it.contains(term, ignoreCase = true) }
    }
    val matchingUnits = authoredUnits.filter { unit ->
        matchesQuery(unit, searchQuery)
    }

    LazyColumn(
        modifier = modifier.fillMaxSize().testTag("learn_area"),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(language.text("DEIN KURSWEG", "YOUR COURSE PATH"), style = MaterialTheme.typography.labelMedium, color = BerlinTheme.accentInk)
                Text(language.text("Eine Lektion nach der anderen.", "One lesson at a time."), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text(language.text(
                    "Dein nächster Schritt wird aus deinem Lernverlauf gewählt. Du kannst direkt weitermachen oder den Kursweg ansehen.",
                    "Your next step is chosen from your practice history. Continue directly or explore the course map."
                ), style = MaterialTheme.typography.bodyMedium)
            }
        }
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("lesson_roadmap_recommendation"),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text(language.text("HEUTE · DEIN NÄCHSTER SCHRITT", "TODAY · YOUR NEXT STEP"),
                            style = MaterialTheme.typography.labelMedium, color = BerlinTheme.accentInk)
                        Text(language.text("${streakState.currentStreak} Tage Serie", "${streakState.currentStreak}-day streak"),
                            style = MaterialTheme.typography.labelMedium)
                    }
                    Text(
                        nextUnit?.title ?: language.text("Kursweg abgeschlossen", "Course path complete"),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        if (historyAvailable) adaptiveExplanation(adaptiveDecision, weaknessProfile, language)
                        else language.text(
                            "Dein adaptiver Lernweg startet, sobald dein lokaler Lernverlauf geladen ist.",
                            "Your adaptive path starts once your local practice history is ready."
                        ),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        language.text(
                            "${dailyMinutes} Min. Tagesziel · vorgeschlagene Übung: ${roadmapTaskLabel(sessionTask, language)}",
                            "${dailyMinutes}-minute daily goal · suggested practice: ${roadmapTaskLabel(sessionTask, language)}"
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    nextUnit?.let { unit ->
                        Button(
                            onClick = { onOpen(unit, sessionTask) },
                            enabled = historyAvailable,
                            modifier = Modifier.fillMaxWidth().testTag("lesson_roadmap_continue")
                        ) {
                            Text(if (hasStarted) language.text("Lektion fortsetzen", "Continue lesson")
                            else language.text("Adaptive Lektion starten", "Start adaptive lesson"))
                        }
                    }
                }
            }
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Text(language.text("Kurskarte", "Course map"),
                    style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                TextButton(onClick = { searchOpen = !searchOpen }) {
                    Text(if (searchOpen) language.text("Suche schließen", "Hide search")
                    else language.text("Lektionen suchen", "Search lessons"))
                }
            }
        }
        if (searchOpen) {
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { query ->
                        searchQuery = query
                        expandedStage = if (query.isBlank()) nextUnit?.stage
                        else authoredUnits.firstOrNull { matchesQuery(it, query) }?.stage ?: expandedStage
                    },
                    modifier = Modifier.fillMaxWidth().testTag("learn_course_search"),
                    label = { Text(language.text("Titel, Ziel oder Kontext", "Title, goal or context")) },
                    singleLine = true
                )
            }
        }
        if (searchQuery.isBlank()) {
            CurriculumCatalogue.stages.forEachIndexed { stage, stageTitle ->
                val stageUnits = matchingUnits.filter { it.stage == stage }
                if (stageUnits.isNotEmpty()) {
                    val stageCompleted = stageUnits.count { unit ->
                        AdaptiveDiagnosticRoute.isCheckpointComplete(unit, attempts, now)
                    }
                    item(key = "learn-stage-$stage") {
                        Card(
                            modifier = Modifier.fillMaxWidth().clickable {
                                expandedStage = if (expandedStage == stage) null else stage
                            },
                            colors = CardDefaults.cardColors(
                                containerColor = if (stage == nextUnit?.stage) MaterialTheme.colorScheme.secondaryContainer
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(language.text("STUFE $stage", "STAGE $stage"),
                                            style = MaterialTheme.typography.labelSmall, color = BerlinTheme.accentInk)
                                        Text(localizedStageTitle(stage, language, stageTitle), fontWeight = FontWeight.Bold)
                                        Text(
                                            language.text("$stageCompleted / ${stageUnits.size} Lektionen", "$stageCompleted / ${stageUnits.size} lessons"),
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                    Text(if (expandedStage == stage) "−" else "+", style = MaterialTheme.typography.titleLarge)
                                }
                                LinearProgressIndicator(
                                    progress = { if (stageUnits.isEmpty()) 0f else stageCompleted.toFloat() / stageUnits.size },
                                    modifier = Modifier.fillMaxWidth(),
                                    color = BerlinTheme.accent,
                                    trackColor = BerlinTheme.line
                                )
                            }
                        }
                    }
                    if (expandedStage == stage) {
                        items(stageUnits, key = { it.id }) { unit ->
                            val complete = AdaptiveDiagnosticRoute.isCheckpointComplete(unit, attempts, now)
                            Card(
                                modifier = Modifier.fillMaxWidth()
                                    .clickable { onOpen(unit, sessionTask) }
                                    .testTag("course_roadmap_unit_${unit.id}"),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                                ) {
                                    Text(if (complete) "✓" else "○", color = BerlinTheme.accentInk,
                                        style = MaterialTheme.typography.titleMedium)
                                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                        Text(unit.title, fontWeight = FontWeight.Bold)
                                        if (unit.id == nextUnit?.id) {
                                            Text(language.text("Nächste adaptive Lektion", "Next adaptive lesson"),
                                                style = MaterialTheme.typography.labelSmall, color = BerlinTheme.accentInk)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            items(matchingUnits, key = { it.id }) { unit ->
                val complete = AdaptiveDiagnosticRoute.isCheckpointComplete(unit, attempts, now)
                Card(
                    modifier = Modifier.fillMaxWidth()
                        .clickable { onOpen(unit, sessionTask) }
                        .testTag("course_roadmap_unit_${unit.id}"),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Text(if (complete) "✓" else "○", color = BerlinTheme.accentInk,
                            style = MaterialTheme.typography.titleMedium)
                        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            Text(unit.title, fontWeight = FontWeight.Bold)
                            Text(localizedStageTitle(unit.stage, language, CurriculumCatalogue.stages[unit.stage]),
                                style = MaterialTheme.typography.bodySmall)
                            if (unit.id == nextUnit?.id) {
                                Text(language.text("Nächste adaptive Lektion", "Next adaptive lesson"),
                                    style = MaterialTheme.typography.labelSmall, color = BerlinTheme.accentInk)
                            }
                        }
                    }
                }
            }
        }
        if (matchingUnits.isEmpty()) {
            item {
                Text(
                    language.text("Keine passenden Lektionen gefunden.", "No matching lessons found."),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun roadmapTaskLabel(task: String, language: ProgrammeLanguage): String = when (task) {
    "listening" -> language.text("Hörverstehen", "listening")
    "quiz" -> language.text("Verständnischeck", "comprehension check")
    "controlled" -> language.text("Sprachmittel", "language patterns")
    "speaking" -> language.text("Sprechen", "speaking")
    "interaction" -> language.text("Interaktion", "interaction")
    "writing" -> language.text("Schreiben", "writing")
    "transfer" -> language.text("Transfer", "transfer")
    else -> language.text("Abruf", "recall")
}

@Composable
internal fun ProgressArea(
    modifier: Modifier = Modifier,
    language: ProgrammeLanguage,
    attempts: List<Attempt>,
    now: Long,
    adaptiveDecision: AdaptiveControlDecision,
    weaknessProfile: WeaknessProfile,
    streakState: DailyStreakState
) {
    val units = CurriculumCatalogue.units.filter { it.content != null }
    val completed = AdaptiveDiagnosticRoute.completedAuthoredUnits(attempts, now)
    val total = units.size
    var showMoreDetails by rememberSaveable { mutableStateOf(false) }
    var showAllMistakes by rememberSaveable { mutableStateOf(false) }
    val skillTasks = listOf(
        Triple("listening", language.text("Hören", "Listening"), setOf("listening")),
        Triple("speaking", language.text("Sprechen", "Speaking"), setOf("speaking")),
        Triple("writing", language.text("Schreiben / Mediation", "Writing / mediation"),
            setOf("writing", "transfer", "controlled", "context-application")),
        Triple("interaction", language.text("Interaktion", "Interaction"), setOf("interaction"))
    )
    val mistakes = remember(attempts) {
        attempts.asSequence()
            .filter { it.errors.isNotEmpty() }
            .flatMap { attempt -> attempt.errors.asSequence().map { error -> attempt to error } }
            .sortedByDescending { (attempt, _) -> attempt.timestamp }
            .toList()
    }
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
                Text(language.text("Fertigkeiten und Fehler", "Skills and mistakes"), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text(language.text(
                    "Deine Sterne aktualisieren sich automatisch, sobald du Lektionen abschließt.",
                    "Your stars update automatically as you complete lesson practice."
                ), style = MaterialTheme.typography.bodyMedium)
            }
        }
        item {
            ProgrammeAreaCard(language.text("Übungssterne je Fertigkeit", "Practice stars by skill")) {
                Text(
                    language.text(
                        "Ein Stern steht für eine Lektion, in der du diese Fertigkeit geübt hast (höchstens fünf). Das ist Lernaktivität, keine Kompetenznote.",
                        "Each star represents a lesson where you practised that skill (up to five). This tracks practice, not proficiency."
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                skillTasks.forEach { (id, label, tasks) ->
                    val practicedLessons = attempts.asSequence()
                        .filter { it.taskId in tasks }
                        .map { it.unitId }
                        .distinct()
                        .count()
                    val stars = practicedLessons.coerceAtMost(5).toInt()
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(label, fontWeight = FontWeight.SemiBold)
                            Text("$stars / 5", style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.testTag("skill_rating_$id"))
                        }
                        Text(
                            buildString {
                                repeat(5) { append(if (it < stars) "★" else "☆") }
                            },
                            style = MaterialTheme.typography.titleLarge,
                            color = BerlinTheme.accentInk,
                            modifier = Modifier.testTag("skill_stars_$id")
                        )
                        Text(
                            language.text("$practicedLessons Lektionen geübt", "$practicedLessons lessons practised"),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
        item {
            ProgrammeAreaCard(language.text("Fehlerprotokoll", "Mistake log")) {
                Text(
                    language.text(
                        "Gespeicherte Quizfehler und lokale Formulierungshinweise. Automatische Hinweise können ungenau sein.",
                        "Saved quiz errors and local writing signals. Automatic signals can be imperfect."
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (mistakes.isEmpty()) {
                    Text(
                        language.text("Noch keine gespeicherten Fehlerhinweise.", "No saved mistake signals yet."),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.testTag("mistake_log_empty")
                    )
                } else {
                    mistakes.take(if (showAllMistakes) mistakes.size else 5).forEach { (attempt, errorText) ->
                        val unitTitle = CurriculumCatalogue.units.firstOrNull { it.id == attempt.unitId }?.title
                            ?: attempt.unitId
                        Card(
                            modifier = Modifier.fillMaxWidth().testTag("mistake_log_entry"),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    "$unitTitle · ${roadmapTaskLabel(attempt.taskId, language)}",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(errorText.substringAfter(": "), style = MaterialTheme.typography.bodySmall)
                                if (attempt.response.isNotBlank()) {
                                    Text(
                                        attempt.response.take(160),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                    if (mistakes.size > 5) {
                        TextButton(onClick = { showAllMistakes = !showAllMistakes }) {
                            Text(
                                if (showAllMistakes) language.text("Weniger anzeigen", "Show less")
                                else language.text("Alle ${mistakes.size} Hinweise anzeigen", "Show all ${mistakes.size} signals")
                            )
                        }
                    }
                }
            }
        }
        item {
            TextButton(
                onClick = { showMoreDetails = !showMoreDetails },
                modifier = Modifier.fillMaxWidth().testTag("progress_details_toggle")
            ) {
                Text(
                    if (showMoreDetails) language.text("Weitere Details ausblenden", "Hide extra details")
                    else language.text("Weitere Lernverlaufsdetails", "More learning-history details")
                )
            }
        }
        if (showMoreDetails) {
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
                Text(language.text("EINSTELLUNGEN", "SETTINGS"), style = MaterialTheme.typography.labelMedium, color = BerlinTheme.accentInk)
                Text(language.text("Dein Lernweg, deine Einstellungen.", "Your learning path, your settings."),
                    style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text(language.text(
                    "Name, Sprache, Tagesziel, Design, Audio, Export und Datenschutz.",
                    "Name, language, daily goal, theme, audio, export and privacy."
                ), style = MaterialTheme.typography.bodyMedium)
            }
        }
        item {
            OutlinedButton(onClick = onOpenSettings, modifier = Modifier.fillMaxWidth()) {
                Text(language.text("Einstellungen öffnen", "Open settings"))
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
