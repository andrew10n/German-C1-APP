package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.engine.MasterControlState
import com.example.domain.model.C1Pillar
import com.example.domain.model.C1QuizItem
import com.example.ui.DrillGenUiState
import com.example.ui.TutorStreamUiState
import com.example.ui.WritingEvalUiState
import com.example.ui.components.pillarAccentColor
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CoralCrimson
import com.example.ui.theme.EmeraldSage

@Composable
fun AiC1StudioScreen(
    controlState: MasterControlState,
    writingState: WritingEvalUiState,
    tutorState: TutorStreamUiState,
    drillGenState: DrillGenUiState,
    onEvaluateWriting: (String, String) -> Unit,
    onGenerateDrills: (C1Pillar) -> Unit,
    onLaunchGeneratedDrills: (List<C1QuizItem>) -> Unit,
    onAskCoach: (String) -> Unit,
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Stil-Upgrade, 1 = AI Drill Forge, 2 = Socratic Coach
    var draftText by remember {
        mutableStateOf(
            "Obwohl viele Leute glauben, dass Homeoffice die Produktivität senkt, zeigen neue Studien, dass die Mitarbeiter zufriedener sind und besser arbeiten."
        )
    }
    var topicText by remember { mutableStateOf("Digitalisierung der Arbeitswelt (Goethe C1 Erörterung)") }
    var selectedForgePillar by remember { mutableStateOf(controlState.pillarSnapshots.first().pillar) }
    var coachQuestion by remember { mutableStateOf("Erkläre mir den Unterschied zwischen 'infolge' und 'zufolge' mit visuellen Beispielen.") }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 600.dp)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("ai_studio_back_button")
                ) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "GEMINI × DIAGNOSTIC ENGINE",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "C1 AI Atelier",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            // Live Diagnostic Briefing Pill
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        Icons.Rounded.Psychology,
                        contentDescription = "Controller context",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Controller Focus: ${controlState.pillarSnapshots.first().pillar.titleDe} (${controlState.cefrLevel})",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // 3 Studio Mode Tabs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StudioTabPill("Stil-Upgrade", selectedTab == 0, { selectedTab = 0 }, Modifier.weight(1f).testTag("ai_tab_writing"))
                StudioTabPill("Drill Forge", selectedTab == 1, { selectedTab = 1 }, Modifier.weight(1f).testTag("ai_tab_forge"))
                StudioTabPill("C1 Coach", selectedTab == 2, { selectedTab = 2 }, Modifier.weight(1f).testTag("ai_tab_coach"))
            }

            when (selectedTab) {
                0 -> {
                    // STIL-UPGRADE & WRITING DIAGNOSTIC LAB
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "B2 → C1 Bildungssprache Transformer",
                                style = MaterialTheme.typography.titleLarge
                            )
                            OutlinedTextField(
                                value = topicText,
                                onValueChange = { topicText = it },
                                label = { Text("Essay / Forum Topic") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = draftText,
                                onValueChange = { draftText = it },
                                label = { Text("Your German Draft") },
                                minLines = 4,
                                modifier = Modifier.fillMaxWidth().testTag("writing_draft_input")
                            )
                            Button(
                                onClick = { onEvaluateWriting(draftText, topicText) },
                                enabled = writingState !is WritingEvalUiState.Loading,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth().height(50.dp).testTag("evaluate_writing_button")
                            ) {
                                if (writingState is WritingEvalUiState.Loading) {
                                    CircularProgressIndicator(
                                        color = Color.White,
                                        strokeWidth = 2.dp,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text("Diagnosing with Gemini...")
                                } else {
                                    Icon(Icons.Rounded.EditNote, contentDescription = null)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Diagnose & Upgrade to C1")
                                }
                            }
                        }
                    }

                    when (writingState) {
                        is WritingEvalUiState.Error -> {
                            ErrorCard(writingState.message)
                        }
                        is WritingEvalUiState.Success -> {
                            val res = writingState.result
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = BorderStroke(1.5.dp, EmeraldSage.copy(alpha = 0.55f))
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Estimated: ${res.estimatedLevel}",
                                            style = MaterialTheme.typography.headlineSmall,
                                            color = EmeraldSage
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(99.dp),
                                            color = EmeraldSage.copy(alpha = 0.14f)
                                        ) {
                                            Text(
                                                text = "C1 Score ${res.c1Score}/100",
                                                style = MaterialTheme.typography.labelMedium,
                                                color = EmeraldSage,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                            )
                                        }
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(14.dp),
                                        color = EmeraldSage.copy(alpha = 0.08f)
                                    ) {
                                        Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                                            Text(
                                                text = "C1 BILDUNGSSPRACHE UPGRADE",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = EmeraldSage
                                            )
                                            Spacer(Modifier.height(4.dp))
                                            Text(
                                                text = res.upgradedText,
                                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }

                                    Text(
                                        text = res.diagnosticFeedback,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    if (res.transformations.isNotEmpty()) {
                                        Text(
                                            text = "Visual Before → After Upgrades",
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        res.transformations.forEach { t ->
                                            Surface(
                                                shape = RoundedCornerShape(14.dp),
                                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
                                            ) {
                                                Column(
                                                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                                ) {
                                                    Text(
                                                        text = t.ruleTag.uppercase(),
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = MaterialTheme.colorScheme.primary
                                                    )
                                                    Text(
                                                        text = "✕ ${t.originalPhrase}",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = CoralCrimson
                                                    )
                                                    Text(
                                                        text = "✓ ${t.upgradedPhrase}",
                                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                                        color = EmeraldSage
                                                    )
                                                    Text(
                                                        text = t.explanation,
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        else -> Unit
                    }
                }

                1 -> {
                    // ADAPTIVE AI DRILL FORGE
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Synthesize Personalized C1 Probes",
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = "Gemini reads your live Diagnostic Controller state and builds 3 custom exam-grade probes targeting your selected pillar.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                C1Pillar.entries.chunked(2).forEach { pair ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        pair.forEach { pillar ->
                                            val selected = selectedForgePillar == pillar
                                            val accent = pillarAccentColor(pillar)
                                            Surface(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .clickable { selectedForgePillar = pillar },
                                                shape = RoundedCornerShape(12.dp),
                                                color = if (selected) accent.copy(alpha = 0.16f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                                                border = BorderStroke(1.dp, if (selected) accent else MaterialTheme.colorScheme.outline)
                                            ) {
                                                Text(
                                                    text = pillar.shortLabel,
                                                    style = MaterialTheme.typography.labelLarge,
                                                    color = if (selected) accent else MaterialTheme.colorScheme.onSurface,
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Button(
                                onClick = { onGenerateDrills(selectedForgePillar) },
                                enabled = drillGenState !is DrillGenUiState.Loading,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth().height(50.dp).testTag("forge_drills_button")
                            ) {
                                if (drillGenState is DrillGenUiState.Loading) {
                                    CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text("Forging Custom Probes...")
                                } else {
                                    Icon(Icons.Rounded.AutoAwesome, contentDescription = null)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Generate 3 Adaptive Probes")
                                }
                            }
                        }
                    }

                    when (drillGenState) {
                        is DrillGenUiState.Error -> ErrorCard(drillGenState.message)
                        is DrillGenUiState.Success -> {
                            Surface(
                                shape = RoundedCornerShape(18.dp),
                                color = EmeraldSage.copy(alpha = 0.10f),
                                border = BorderStroke(1.dp, EmeraldSage)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(
                                        text = "${drillGenState.items.size} Custom C1 Probes Ready",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = EmeraldSage
                                    )
                                    drillGenState.items.forEachIndexed { idx, item ->
                                        Text(
                                            text = "${idx + 1}. ${item.contextSnippet}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Button(
                                        onClick = { onLaunchGeneratedDrills(drillGenState.items) },
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth().height(48.dp).testTag("launch_forged_drills_button")
                                    ) {
                                        Icon(Icons.Rounded.Bolt, contentDescription = null)
                                        Spacer(Modifier.width(6.dp))
                                        Text("Launch in Adaptive Arena")
                                        Spacer(Modifier.weight(1f))
                                        Icon(Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = null)
                                    }
                                }
                            }
                        }
                        else -> Unit
                    }
                }

                else -> {
                    // STREAMING SOCRATIC COACH
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Ask Your C1 Visual Mentor",
                                style = MaterialTheme.typography.titleLarge
                            )
                            OutlinedTextField(
                                value = coachQuestion,
                                onValueChange = { coachQuestion = it },
                                label = { Text("Grammar, Redemittel or Nuance Question") },
                                minLines = 3,
                                modifier = Modifier.fillMaxWidth().testTag("coach_question_input")
                            )
                            Button(
                                onClick = { onAskCoach(coachQuestion) },
                                enabled = tutorState !is TutorStreamUiState.Loading,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth().height(50.dp).testTag("ask_coach_button")
                            ) {
                                if (tutorState is TutorStreamUiState.Loading) {
                                    CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text("Connecting...")
                                } else {
                                    Icon(Icons.AutoMirrored.Rounded.Send, contentDescription = null)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Stream Visual Explanation")
                                }
                            }
                        }
                    }

                    when (tutorState) {
                        is TutorStreamUiState.Error -> ErrorCard(tutorState.message)
                        is TutorStreamUiState.Streaming -> CoachResponseCard(tutorState.text, isStreaming = true)
                        is TutorStreamUiState.Success -> CoachResponseCard(tutorState.text, isStreaming = false)
                        else -> Unit
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun StudioTabPill(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline)
    ) {
        Box(
            modifier = Modifier.padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun CoachResponseCard(text: String, isStreaming: Boolean) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.45f))
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = if (isStreaming) "GEMINI C1 MENTOR (STREAMING...)" else "GEMINI C1 MENTOR",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun ErrorCard(message: String) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = CoralCrimson.copy(alpha = 0.10f),
        border = BorderStroke(1.dp, CoralCrimson)
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = CoralCrimson,
            modifier = Modifier.fillMaxWidth().padding(14.dp)
        )
    }
}
