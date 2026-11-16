package com.example.programme

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

private data class ListeningTurn(
    val speaker: String?,
    val text: String
)

@Composable
internal fun ListeningTranscriptStudio(
    transcript: String,
    language: ProgrammeLanguage
) {
    val turns = remember(transcript) { parseListeningTurns(transcript) }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            language.text("DIALOGVERLAUF", "CONVERSATION"),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
        turns.forEachIndexed { index, turn ->
            val background = when {
                turn.speaker == null -> MaterialTheme.colorScheme.tertiaryContainer
                index % 2 == 0 -> MaterialTheme.colorScheme.primaryContainer
                else -> MaterialTheme.colorScheme.secondaryContainer
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = if (index % 2 == 0) Alignment.Start else Alignment.End
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(0.96f),
                    shape = RoundedCornerShape(
                        topStart = 18.dp,
                        topEnd = 18.dp,
                        bottomEnd = if (index % 2 == 0) 18.dp else 6.dp,
                        bottomStart = if (index % 2 == 0) 6.dp else 18.dp
                    ),
                    color = background
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            turn.speaker ?: language.text("KONTEXT", "CONTEXT"),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(turn.text, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

private fun parseListeningTurns(transcript: String): List<ListeningTurn> {
    val speakerPrefix = Regex("^([^:\\n]{1,60}):\\s*(.*)$")
    val turns = mutableListOf<ListeningTurn>()
    var currentSpeaker: String? = null
    val currentLines = mutableListOf<String>()

    fun flush() {
        val text = currentLines.joinToString(" ").trim()
        if (text.isNotEmpty()) turns += ListeningTurn(currentSpeaker, text)
        currentLines.clear()
    }

    transcript.lineSequence().forEach { rawLine ->
        val line = rawLine.trim()
        if (line.isEmpty()) return@forEach
        val match = speakerPrefix.matchEntire(line)
        if (match != null) {
            flush()
            currentSpeaker = match.groupValues[1].trim()
            currentLines += match.groupValues[2].trim()
        } else if (currentSpeaker == null) {
            turns += ListeningTurn(null, line)
        } else {
            currentLines += line
        }
    }
    flush()
    return turns
}

@Composable
internal fun ReadingChunkStudio(
    unitId: String,
    passage: String,
    language: ProgrammeLanguage
) {
    val chunks = remember(passage) {
        passage.split(Regex("\\n\\s*\\n+"))
            .map(String::trim)
            .filter(String::isNotEmpty)
    }
    if (chunks.isEmpty()) {
        Text(passage)
        return
    }
    var selectedChunk by rememberSaveable(unitId) { mutableIntStateOf(0) }
    val current = selectedChunk.coerceIn(chunks.indices)
    val progress = (current + 1).toFloat() / chunks.size

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    language.text("LESEN · SINNABSCHNITTE", "READ · MEANINGFUL CHUNKS"),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    language.text("Teil ${current + 1} / ${chunks.size}", "Part ${current + 1} / ${chunks.size}"),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surface
            )
            AnimatedContent(
                targetState = current,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "reading-chunk"
            ) { chunkIndex ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Text(
                        chunks[chunkIndex],
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(
                    onClick = { selectedChunk = (current - 1).coerceAtLeast(0) },
                    enabled = current > 0
                ) {
                    Text(language.text("Zurück", "Previous"))
                }
                TextButton(
                    onClick = { selectedChunk = (current + 1).coerceAtMost(chunks.lastIndex) },
                    enabled = current < chunks.lastIndex
                ) {
                    Text(language.text("Weiter", "Next"))
                }
            }
        }
    }
}

private data class LessonPathStep(
    val title: String,
    val description: String,
    val taskIds: Set<String>
)

@Composable
internal fun LessonJourneyVisual(
    unit: CurriculumUnit,
    completedTaskIds: Set<String>,
    language: ProgrammeLanguage
) {
    val steps = listOf(
        LessonPathStep(
            language.text("Abrufen", "Retrieve"),
            language.text(
                "Erst aus dem Gedächtnis formulieren; offene Fragen notieren.",
                "Recall what you know first, then note what remains unclear."
            ),
            setOf("retrieval")
        ),
        LessonPathStep(
            language.text("Verstehen", "Understand"),
            language.text(
                "Lesen, hören und die Kernaussage am Text prüfen.",
                "Read, listen and check the main idea against the material."
            ),
            setOf("reading", "listening", "quiz")
        ),
        LessonPathStep(
            language.text("Anwenden", "Apply"),
            language.text(
                "Sprachmittel in einer eigenen Formulierung und im Dialog erproben.",
                "Try the language in your own writing and in a dialogue."
            ),
            setOf("controlled", "speaking", "interaction", "writing")
        ),
        LessonPathStep(
            language.text("Übertragen", "Transfer"),
            language.text(
                "Die Idee in einem neuen Kontext selbstständig nutzen.",
                "Use the idea independently in a new context."
            ),
            setOf("transfer")
        )
    )
    val completedSteps = steps.count { step -> step.taskIds.any { it in completedTaskIds } }
    val selectedDefault = steps.indexOfFirst { step -> step.taskIds.none { it in completedTaskIds } }
        .coerceAtLeast(0)
    var selectedStep by rememberSaveable(unit.id) { mutableIntStateOf(selectedDefault) }
    val progress by animateFloatAsState(
        targetValue = completedSteps.toFloat() / steps.size,
        label = "lesson-path-progress"
    )
    val selected = steps[selectedStep.coerceIn(steps.indices)]

    Card(
        modifier = Modifier.fillMaxWidth().animateContentSize(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        language.text("DEIN LERNWEG", "YOUR LEARNING PATH"),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        language.text(
                            "$completedSteps von ${steps.size} Stationen bearbeitet",
                            "$completedSteps of ${steps.size} steps practised"
                        ),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Text(
                    "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            androidx.compose.material3.LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                steps.forEachIndexed { index, step ->
                    val done = step.taskIds.any { it in completedTaskIds }
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    done -> MaterialTheme.colorScheme.primary
                                    selectedStep == index -> MaterialTheme.colorScheme.surface
                                    else -> MaterialTheme.colorScheme.surface.copy(alpha = 0.65f)
                                }
                            )
                            .clickable(role = Role.Button) { selectedStep = index },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            if (done) "✓" else "${index + 1}",
                            color = if (done) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    if (index < steps.lastIndex) {
                        val connectorProgress by animateFloatAsState(
                            targetValue = if (done) 1f else 0f,
                            label = "lesson-path-connector-$index"
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(3.dp)
                                .background(
                                    MaterialTheme.colorScheme.primary.copy(
                                        alpha = 0.2f + 0.65f * connectorProgress
                                    )
                                )
                        )
                    }
                }
            }
            Row(modifier = Modifier.fillMaxWidth().selectableGroup()) {
                steps.forEachIndexed { index, step ->
                    Text(
                        step.title,
                        modifier = Modifier
                            .weight(1f)
                            .selectable(
                                selected = selectedStep == index,
                                role = Role.Tab,
                                onClick = { selectedStep = index }
                            )
                            .testTag("lesson_path_step_$index"),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (selectedStep == index) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = if (selectedStep == index) FontWeight.Bold else FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            AnimatedContent(
                targetState = selected,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "lesson-path-description"
            ) { step ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.72f)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(step.title, fontWeight = FontWeight.SemiBold)
                        Text(step.description, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@Composable
internal fun LessonObjectiveMap(
    objectives: List<String>,
    language: ProgrammeLanguage
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            language.text("WORAN DU ARBEITEST", "WHAT YOU WILL WORK ON"),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
        objectives.forEachIndexed { index, objective ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                Surface(
                    modifier = Modifier.size(30.dp),
                    shape = CircleShape,
                    color = if (index == 0) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            "${index + 1}",
                            color = if (index == 0) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onSecondaryContainer,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Text(
                    objective,
                    modifier = Modifier.weight(1f).padding(top = 3.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
internal fun LanguagePatternStudio(
    unitId: String,
    expressions: List<String>,
    grammar: String,
    language: ProgrammeLanguage
) {
    if (expressions.isEmpty()) {
        Text(grammar)
        return
    }
    var selectedIndex by rememberSaveable(unitId) { mutableIntStateOf(0) }
    val index = selectedIndex.coerceIn(expressions.indices)

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            language.text(
                "Wähle ein Sprachmittel, um Form und Funktion zu verbinden.",
                "Choose a language tool to connect its form and function."
            ),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        androidx.compose.foundation.layout.FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            expressions.forEachIndexed { itemIndex, expression ->
                val label = expression.substringBefore(" – ")
                    .substringBefore(" — ")
                    .take(26)
                    .let { if (it.length < expression.substringBefore(" – ").length) "$it…" else it }
                FilterChip(
                    selected = itemIndex == index,
                    onClick = { selectedIndex = itemIndex },
                    label = { Text("${itemIndex + 1} · $label") }
                )
            }
        }
        AnimatedContent(
            targetState = index,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "language-pattern-selection"
        ) { selected ->
            val parts = expressions[selected].split(Regex("\\s+[–—]\\s+"), limit = 2)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        language.text("FORMULIERUNG", "PHRASE"),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        parts.first(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    parts.getOrNull(1)?.let { explanation ->
                        Text(
                            language.text("FUNKTION", "PURPOSE"),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(explanation, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
        Text(
            language.text("GRAMMATIK IM KONTEXT", "GRAMMAR IN CONTEXT"),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(grammar)
    }
}
