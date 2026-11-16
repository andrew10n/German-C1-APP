package com.example.programme

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

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
internal fun LessonConceptMap(
    unit: CurriculumUnit,
    context: ContextTrack,
    expressions: List<String>,
    selectedExpressionIndex: Int,
    language: ProgrammeLanguage
) {
    val expression = expressions.getOrNull(selectedExpressionIndex) ?: expressions.firstOrNull().orEmpty()
    val parts = expression.split(Regex("\\s+[–—]\\s+"), limit = 2)
    val phrase = parts.firstOrNull()?.takeIf { it.isNotBlank() }
        ?: language.text("Noch kein Sprachmittel", "No language move yet")
    val purpose = parts.getOrNull(1) ?: unit.objectives.firstOrNull().orEmpty()
    val visual = lessonVisualBlueprint(unit.id)
    var selectedStep by rememberSaveable(unit.id) { mutableIntStateOf(1) }
    var showObjectives by rememberSaveable(unit.id) { mutableStateOf(false) }
    val steps = listOf(
        Triple("KONTEXT", contextLabel(context), unit.contexts[context] ?: "Kein Kontext verfasst"),
        Triple("SPRACHZUG", phrase, phrase),
        Triple("WIRKUNG", "Was sich dadurch ändert", purpose)
    )
    val activeStep = selectedStep.coerceIn(steps.indices)

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            language.text("VISUELLE IDEE", "VISUAL MODEL"),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
        LessonVisualGraphic(visual.graphic, activeStep)
        Text(
            language.text(visual.caption, visual.englishCaption),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            language.text("WORAN DU ARBEITEST", "WHAT YOU WILL WORK ON"),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
        unit.objectives.firstOrNull()?.let { objective ->
            Text(
                objective,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Column {
            Row(
                modifier = Modifier.fillMaxWidth().selectableGroup(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                steps.forEachIndexed { index, _ ->
                    Surface(
                        modifier = Modifier
                            .size(42.dp)
                            .selectable(
                                selected = activeStep == index,
                                onClick = { selectedStep = index },
                                role = Role.Tab
                            )
                            .testTag("lesson_concept_step_$index"),
                        shape = CircleShape,
                        color = if (activeStep == index) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                "${index + 1}",
                                color = if (activeStep == index) MaterialTheme.colorScheme.onPrimary
                                else MaterialTheme.colorScheme.onSecondaryContainer,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (index < steps.lastIndex) {
                        Box(
                            modifier = Modifier.weight(1f).height(3.dp)
                                .background(
                                    if (activeStep > index) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.surfaceVariant
                                )
                        )
                    }
                }
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                steps.forEachIndexed { index, step ->
                    Text(
                        step.first,
                        modifier = Modifier.width(42.dp).padding(top = 6.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (activeStep == index) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    if (index < steps.lastIndex) Box(modifier = Modifier.weight(1f))
                }
            }
        }
        AnimatedContent(
            targetState = activeStep,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "lesson-concept-step"
        ) { target ->
            val step = steps[target]
            Card(
                modifier = Modifier.fillMaxWidth().animateContentSize(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = when (target) {
                        0 -> MaterialTheme.colorScheme.primaryContainer
                        1 -> MaterialTheme.colorScheme.tertiaryContainer
                        else -> MaterialTheme.colorScheme.secondaryContainer
                    }
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(step.second, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    if (target != 1) Text(step.third, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
        if (unit.objectives.size > 1) {
            TextButton(onClick = { showObjectives = !showObjectives }) {
                Text(
                    if (showObjectives) language.text("Weniger Lernziele", "Fewer learning goals")
                    else language.text("Alle Lernziele anzeigen", "Show all learning goals")
                )
            }
            AnimatedVisibility(visible = showObjectives) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    unit.objectives.drop(1).forEachIndexed { index, objective ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                "${index + 2}",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(objective, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}

private enum class LessonGraphicType {
    TARGET, GRID, PROCESS, ARGUMENT, EVIDENCE, DIALOGUE, BRIDGE, FUNNEL, BALANCE, CYCLE
}

private data class LessonVisualBlueprint(
    val graphic: LessonGraphicType,
    val caption: String,
    val englishCaption: String
)

@Composable
internal fun LessonStepVisual(
    unit: CurriculumUnit,
    phase: Int,
    language: ProgrammeLanguage
) {
    val visual = lessonVisualBlueprint(unit.id)
    val stage = phase.coerceIn(0, 2)
    val stageTitle = when (stage) {
        0 -> language.text("Erst erinnern", "Recall first")
        1 -> language.text("Muster erkennen", "Notice the pattern")
        else -> language.text("Eigenständig anwenden", "Apply it yourself")
    }

    Card(
        modifier = Modifier.fillMaxWidth().testTag("lesson_step_visual_$stage"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LessonVisualGraphic(
                graphic = visual.graphic,
                activeStep = stage,
                modifier = Modifier.width(90.dp),
                height = 66.dp,
                testTag = "lesson_step_art_$stage"
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    language.text("DIE IDEE ALS BILD", "THE IDEA IN ONE IMAGE"),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                AnimatedContent(
                    targetState = stageTitle,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "lesson-visual-stage"
                ) { title ->
                    Text(
                        title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Text(
                    language.text(visual.caption, visual.englishCaption),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

private fun lessonVisualBlueprint(unitId: String): LessonVisualBlueprint = when (unitId) {
    "s0-profile" -> LessonVisualBlueprint(LessonGraphicType.TARGET, "Wunsch → überprüfbares Ziel", "Wish → observable goal")
    "s0-diagnose" -> LessonVisualBlueprint(LessonGraphicType.GRID, "Vier Fertigkeiten, ein ehrlicher Ausgangspunkt", "Four skills, one honest starting point")
    "s0-repair" -> LessonVisualBlueprint(LessonGraphicType.DIALOGUE, "Störung → Klärung → gemeinsames Verständnis", "Breakdown → clarification → shared understanding")
    "s0-retrieve", "s5-revise", "s6-retention" ->
        LessonVisualBlueprint(LessonGraphicType.CYCLE, "Abrufen, prüfen und erneut anwenden", "Recall, check and apply again")
    "s1-explain" -> LessonVisualBlueprint(LessonGraphicType.PROCESS, "Schrittfolge mit sichtbaren Übergängen", "A sequence with visible transitions")
    "s1-justify", "s4-argument" ->
        LessonVisualBlueprint(LessonGraphicType.ARGUMENT, "These, Beleg und Einwand auseinanderhalten", "Separate claim, evidence and counterargument")
    "s1-uncertainty", "s2-data", "s4-challenge", "s6-specialist" ->
        LessonVisualBlueprint(LessonGraphicType.EVIDENCE, "Aussage, Beleg und Unsicherheitsgrenze", "Claim, evidence and uncertainty boundary")
    "s1-feedback" -> LessonVisualBlueprint(LessonGraphicType.DIALOGUE, "Beobachtung → Wirkung → nächster Schritt", "Observation → impact → next step")
    "s2-register" -> LessonVisualBlueprint(LessonGraphicType.BRIDGE, "Adressat und Situation steuern das Register", "Audience and situation shape register")
    "s2-summarise", "s4-present" ->
        LessonVisualBlueprint(LessonGraphicType.FUNNEL, "Wesentliches bündeln, Grenzen erhalten", "Distil the essential while preserving limits")
    "s2-mediate", "s3-negotiate" ->
        LessonVisualBlueprint(LessonGraphicType.BRIDGE, "Perspektiven verbinden, ohne sie gleichzusetzen", "Connect perspectives without conflating them")
    "s3-disagree" -> LessonVisualBlueprint(LessonGraphicType.BALANCE, "Klar widersprechen und das Gegenüber achten", "Disagree clearly while respecting the other person")
    "s3-deescalate" -> LessonVisualBlueprint(LessonGraphicType.CYCLE, "Spannung aufnehmen, neu rahmen, Lösung öffnen", "Acknowledge tension, reframe and open a way forward")
    "s3-agree" -> LessonVisualBlueprint(LessonGraphicType.PROCESS, "Verantwortung, Termin und Kriterium festhalten", "Record ownership, timing and a success criterion")
    "s4-ethics" -> LessonVisualBlueprint(LessonGraphicType.BALANCE, "Werte abwägen und die Entscheidung begrenzen", "Balance values and qualify the decision")
    "s5-spontaneous" -> LessonVisualBlueprint(LessonGraphicType.DIALOGUE, "Hören → reagieren → bei Bedarf reparieren", "Listen → respond → repair when needed")
    "s5-nuance" -> LessonVisualBlueprint(LessonGraphicType.EVIDENCE, "Bedeutung als Abstufung statt Entweder-oder", "Meaning as a spectrum, not a binary")
    "s5-facilitate" -> LessonVisualBlueprint(LessonGraphicType.GRID, "Mehrere Stimmen zu einem gemeinsamen Faden verknüpfen", "Weave multiple voices into a shared thread")
    "s6-general" -> LessonVisualBlueprint(LessonGraphicType.PROCESS, "Neue Aufgabe → Strategie → Selbstcheck", "New task → strategy → self-check")
    "s6-checkpoint" -> LessonVisualBlueprint(LessonGraphicType.TARGET, "Signale in ein begrenztes Übungsziel übersetzen", "Turn signals into a bounded practice goal")
    else -> LessonVisualBlueprint(LessonGraphicType.PROCESS, "Kontext → Sprachzug → Wirkung", "Context → language move → effect")
}

@Composable
private fun LessonVisualGraphic(
    graphic: LessonGraphicType,
    activeStep: Int,
    modifier: Modifier = Modifier.fillMaxWidth(),
    height: Dp = 154.dp,
    testTag: String = "lesson_visual_illustration"
) {
    val colors = MaterialTheme.colorScheme
    val progress by animateFloatAsState(activeStep / 2f, label = "lesson-graphic-progress")
    Canvas(
        modifier = modifier
            .height(height)
            .clip(RoundedCornerShape(24.dp))
            .background(colors.surfaceVariant)
            .testTag(testTag)
    ) {
        val unit = size.minDimension
        val stroke = unit * 0.018f
        val line = colors.onSurfaceVariant.copy(alpha = 0.58f)
        val accent = colors.primary
        val soft = colors.primaryContainer
        val warm = colors.tertiaryContainer
        val cool = colors.secondaryContainer
        val center = Offset(size.width / 2f, size.height / 2f)
        val nodeRadius = unit * (0.105f + 0.018f * progress)

        when (graphic) {
            LessonGraphicType.TARGET -> {
                val target = Offset(size.width * 0.72f, center.y)
                drawArrow(Offset(size.width * 0.12f, size.height * 0.78f), target, accent, stroke)
                drawCircle(warm, unit * 0.31f, target)
                drawCircle(colors.surface, unit * 0.22f, target)
                drawCircle(accent, unit * 0.11f, target)
            }
            LessonGraphicType.GRID -> {
                val points = listOf(
                    Offset(size.width * 0.32f, size.height * 0.34f),
                    Offset(size.width * 0.68f, size.height * 0.34f),
                    Offset(size.width * 0.32f, size.height * 0.68f),
                    Offset(size.width * 0.68f, size.height * 0.68f)
                )
                points.forEachIndexed { index, point ->
                    drawLine(line, center, point, strokeWidth = stroke)
                    drawCircle(if (index % 2 == 0) soft else warm, unit * 0.11f, point)
                    drawCircle(accent, unit * 0.035f, point)
                }
                drawCircle(colors.surface, unit * 0.105f, center)
                drawCircle(accent, unit * 0.045f, center)
            }
            LessonGraphicType.PROCESS -> {
                val points = listOf(
                    Offset(size.width * 0.18f, center.y),
                    center,
                    Offset(size.width * 0.82f, center.y)
                )
                points.zipWithNext().forEachIndexed { index, (start, end) ->
                    drawArrow(start, end, if (progress >= (index + 1) / 2f) accent else line, stroke)
                }
                points.forEachIndexed { index, point ->
                    drawCircle(if (progress >= index / 2f) soft else cool, nodeRadius, point)
                    drawCircle(if (progress >= index / 2f) accent else line, nodeRadius * 0.35f, point)
                }
            }
            LessonGraphicType.ARGUMENT -> {
                val claim = Offset(size.width * 0.22f, center.y)
                val evidence = listOf(
                    Offset(size.width * 0.72f, size.height * 0.25f),
                    Offset(size.width * 0.72f, center.y),
                    Offset(size.width * 0.72f, size.height * 0.75f)
                )
                evidence.forEachIndexed { index, point ->
                    drawArrow(claim, point, if (index == 1) accent else line, stroke)
                    drawCircle(if (index == 1) warm else cool, unit * 0.095f, point)
                }
                drawRoundRect(soft, Offset(claim.x - unit * 0.12f, claim.y - unit * 0.12f),
                    Size(unit * 0.24f, unit * 0.24f), CornerRadius(unit * 0.06f))
                drawCircle(accent, unit * 0.045f, claim)
            }
            LessonGraphicType.EVIDENCE -> {
                val left = size.width * 0.16f
                val right = size.width * 0.84f
                val bottom = size.height * 0.78f
                drawLine(line, Offset(left, size.height * 0.18f), Offset(left, bottom), strokeWidth = stroke)
                drawLine(line, Offset(left, bottom), Offset(right, bottom), strokeWidth = stroke)
                val bars = listOf(0.35f, 0.58f, 0.44f)
                bars.forEachIndexed { index, height ->
                    val x = size.width * (0.32f + index * 0.2f)
                    val top = bottom - size.height * height
                    drawRoundRect(
                        if (index == 1) warm else soft,
                        Offset(x - unit * 0.055f, top),
                        Size(unit * 0.11f, bottom - top),
                        CornerRadius(unit * 0.04f)
                    )
                    drawLine(accent, Offset(x, top - unit * 0.055f), Offset(x, top + unit * 0.055f), strokeWidth = stroke)
                }
                drawLine(accent, Offset(size.width * 0.28f, size.height * 0.35f),
                    Offset(size.width * 0.77f, size.height * 0.35f), strokeWidth = stroke * 0.8f)
            }
            LessonGraphicType.DIALOGUE -> {
                val bubbleWidth = size.width * 0.31f
                val bubbleHeight = size.height * 0.34f
                val first = Offset(size.width * 0.12f, size.height * 0.2f)
                val second = Offset(size.width * 0.57f, size.height * 0.48f)
                drawRoundRect(soft, first, Size(bubbleWidth, bubbleHeight), CornerRadius(unit * 0.09f))
                drawRoundRect(warm, second, Size(bubbleWidth, bubbleHeight), CornerRadius(unit * 0.09f))
                repeat(3) { index ->
                    drawCircle(accent, unit * 0.025f, Offset(first.x + bubbleWidth * (0.3f + index * 0.2f), first.y + bubbleHeight * 0.48f))
                    drawCircle(accent, unit * 0.025f, Offset(second.x + bubbleWidth * (0.3f + index * 0.2f), second.y + bubbleHeight * 0.48f))
                }
                drawArrow(Offset(first.x + bubbleWidth, first.y + bubbleHeight * 0.72f),
                    Offset(second.x, second.y + bubbleHeight * 0.28f), accent, stroke)
            }
            LessonGraphicType.BRIDGE -> {
                val left = Offset(size.width * 0.2f, size.height * 0.65f)
                val right = Offset(size.width * 0.8f, size.height * 0.65f)
                val arch = Path().apply {
                    moveTo(left.x, left.y)
                    cubicTo(size.width * 0.32f, size.height * 0.1f, size.width * 0.68f, size.height * 0.1f, right.x, right.y)
                }
                drawPath(arch, accent, style = Stroke(width = stroke * 2f))
                drawRoundRect(soft, Offset(left.x - unit * 0.08f, left.y), Size(unit * 0.16f, unit * 0.2f), CornerRadius(unit * 0.04f))
                drawRoundRect(warm, Offset(right.x - unit * 0.08f, right.y), Size(unit * 0.16f, unit * 0.2f), CornerRadius(unit * 0.04f))
                drawLine(line, Offset(left.x - unit * 0.12f, left.y + unit * 0.2f),
                    Offset(right.x + unit * 0.12f, right.y + unit * 0.2f), strokeWidth = stroke)
            }
            LessonGraphicType.FUNNEL -> {
                val funnel = Path().apply {
                    moveTo(size.width * 0.15f, size.height * 0.22f)
                    lineTo(size.width * 0.85f, size.height * 0.22f)
                    lineTo(size.width * 0.58f, size.height * 0.62f)
                    lineTo(size.width * 0.58f, size.height * 0.76f)
                    lineTo(size.width * 0.42f, size.height * 0.76f)
                    lineTo(size.width * 0.42f, size.height * 0.62f)
                    close()
                }
                drawPath(funnel, soft)
                drawLine(accent, Offset(size.width * 0.26f, size.height * 0.34f),
                    Offset(size.width * 0.74f, size.height * 0.34f), strokeWidth = stroke)
                drawCircle(warm, unit * 0.08f, Offset(center.x, size.height * 0.83f))
            }
            LessonGraphicType.BALANCE -> {
                val pivot = Offset(center.x, size.height * 0.76f)
                val beamY = size.height * (0.34f + 0.08f * progress)
                drawLine(accent, Offset(size.width * 0.23f, beamY), Offset(size.width * 0.77f, beamY), strokeWidth = stroke * 2f)
                drawLine(line, Offset(center.x, beamY), pivot, strokeWidth = stroke * 2f)
                drawTriangle(pivot, unit * 0.18f, warm)
                listOf(size.width * 0.31f, size.width * 0.69f).forEach { x ->
                    drawLine(line, Offset(x, beamY), Offset(x, size.height * 0.68f), strokeWidth = stroke)
                    drawArc(soft, 0f, 180f, false, Offset(x - unit * 0.13f, size.height * 0.62f),
                        Size(unit * 0.26f, unit * 0.16f), style = Stroke(width = stroke * 2f))
                }
            }
            LessonGraphicType.CYCLE -> {
                val points = listOf(
                    Offset(center.x, size.height * 0.2f),
                    Offset(size.width * 0.76f, size.height * 0.65f),
                    Offset(size.width * 0.24f, size.height * 0.65f)
                )
                points.indices.forEach { index ->
                    drawArrow(points[index], points[(index + 1) % points.size], accent, stroke)
                }
                points.forEachIndexed { index, point ->
                    drawCircle(if (index == activeStep) warm else soft, nodeRadius, point)
                }
            }
        }
    }
}

private fun DrawScope.drawArrow(start: Offset, end: Offset, color: Color, stroke: Float) {
    drawLine(color, start, end, strokeWidth = stroke, cap = StrokeCap.Round)
    val angle = atan2(end.y - start.y, end.x - start.x)
    val head = stroke * 3.2f
    val left = Offset(end.x - head * cos(angle - 0.55f), end.y - head * sin(angle - 0.55f))
    val right = Offset(end.x - head * cos(angle + 0.55f), end.y - head * sin(angle + 0.55f))
    val arrow = Path().apply {
        moveTo(end.x, end.y)
        lineTo(left.x, left.y)
        lineTo(right.x, right.y)
        close()
    }
    drawPath(arrow, color)
}

private fun DrawScope.drawTriangle(center: Offset, radius: Float, color: Color) {
    val triangle = Path().apply {
        moveTo(center.x, center.y)
        lineTo(center.x - radius, center.y - radius * 1.5f)
        lineTo(center.x + radius, center.y - radius * 1.5f)
        close()
    }
    drawPath(triangle, color)
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
internal fun LanguagePatternStudio(
    unitId: String,
    expressions: List<String>,
    grammar: String,
    language: ProgrammeLanguage,
    selectedIndex: Int,
    onSelectedIndexChange: (Int) -> Unit
) {
    if (expressions.isEmpty()) {
        GrammarDetails(unitId, grammar, language)
        return
    }
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
                    onClick = { onSelectedIndexChange(itemIndex) },
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                language.text("FORMULIERUNG", "PHRASE"),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(parts.first(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        }
                        if (parts.size > 1) {
                            Text("→", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    language.text("FUNKTION", "PURPOSE"),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(parts[1], style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }
        GrammarDetails(unitId, grammar, language)
    }
}

@Composable
private fun GrammarDetails(
    unitId: String,
    grammar: String,
    language: ProgrammeLanguage
) {
    var expanded by rememberSaveable(unitId) { mutableStateOf(false) }
    Text(
        language.text("GRAMMATIK IM KONTEXT", "GRAMMAR IN CONTEXT"),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary
    )
    TextButton(
        modifier = Modifier.testTag("lesson_grammar_toggle"),
        onClick = { expanded = !expanded }
    ) {
        Text(
            if (expanded) language.text("Grammatik ausblenden", "Hide grammar")
            else language.text("Grammatik im Detail anzeigen", "Show grammar details")
        )
    }
    AnimatedVisibility(visible = expanded) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Text(grammar, modifier = Modifier.padding(14.dp))
        }
    }
}
