package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Forum
import androidx.compose.material.icons.rounded.SportsSoccer
import androidx.compose.material.icons.rounded.Translate
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.ui.SituationEvalUiState
import com.example.ui.components.InteractiveTacticalPitchCanvas
import com.example.ui.components.VisualMediationFunnelCard
import com.example.ui.theme.AmberBright
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CobaltBright
import com.example.ui.theme.CoralCrimson
import com.example.ui.theme.DeepInk
import com.example.ui.theme.EmeraldSage

/**
 * Full-screen Interactive Situations-Training Arena from andrew10n/German-C1-APP:
 * 1. Gesprächs-Simulator (10 Multi-Turn Debates + Unexpected Interruption Challenge!)
 * 2. Sprachmittlung / Mediation (9 Real-World Fachsprache -> Bürgernah Scenarios)
 * 3. Fußball-Coaching U15 (6 Live Match Tactical Scenarios & Pitch Commands)
 */
@Composable
fun SituationsSimulatorScreen(
    controlState: MasterControlState,
    situationEvalState: SituationEvalUiState,
    onEvaluateReply: (String, String, String, String, C1Pillar) -> Unit,
    onResetEval: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    // 0 = Gesprächs-Simulator, 1 = Mediation, 2 = Fußball U15 Taktik
    var selectedMode by remember { mutableIntStateOf(0) }
    var convIdx by remember { mutableIntStateOf(0) }
    var turnIdx by remember(convIdx) { mutableIntStateOf(0) }
    var interruptionTriggered by remember(convIdx) { mutableStateOf(false) }
    var userResponse by remember(selectedMode, convIdx, turnIdx, interruptionTriggered) { mutableStateOf("") }

    var medIdx by remember { mutableIntStateOf(0) }
    var showMediationModel by remember(medIdx) { mutableStateOf(false) }

    var fbIdx by remember { mutableIntStateOf(0) }
    var fbChoice by remember(fbIdx) { mutableStateOf<Int?>(null) }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 600.dp)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("situations_back_button")) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "LIVE SITUATIONS-TRAINING · GEGENWIND",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Sprech- & Mediations-Labor",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            // 3 Mode Switcher Pills
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SimModePill("Debatten (${controlState.conversations.size})", selectedMode == 0, {
                    onResetEval()
                    selectedMode = 0
                }, Modifier.weight(1f))
                SimModePill("Mediation (${controlState.mediations.size})", selectedMode == 1, {
                    onResetEval()
                    selectedMode = 1
                }, Modifier.weight(1f))
                SimModePill("U15 Taktik (${controlState.footballScenarios.size})", selectedMode == 2, {
                    onResetEval()
                    selectedMode = 2
                }, Modifier.weight(1f))
            }

            when (selectedMode) {
                0 -> {
                    val convs = controlState.conversations
                    val currentConv = convs.getOrNull(convIdx % convs.size.coerceAtLeast(1))
                    if (currentConv != null) {
                        // Scenario Selector Strip
                        Row(
                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            convs.forEachIndexed { idx, c ->
                                val sel = idx == convIdx
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(99.dp))
                                        .clickable {
                                            onResetEval()
                                            convIdx = idx
                                        },
                                    shape = RoundedCornerShape(99.dp),
                                    color = if (sel) DeepInk else MaterialTheme.colorScheme.surface,
                                    border = BorderStroke(1.dp, if (sel) AmberBright else MaterialTheme.colorScheme.outline)
                                ) {
                                    Text(
                                        text = "${idx + 1}. ${c.title.substringBefore(":")}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (sel) AmberBright else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        val currentTurn = currentConv.turns.getOrElse(turnIdx.coerceAtMost(currentConv.turns.lastIndex.coerceAtLeast(0))) {
                            currentConv.turns.first()
                        }
                        val activePrompt = if (interruptionTriggered && currentConv.unexpectedInterruption != null) {
                            currentConv.unexpectedInterruption.partnerInterruptSpeech
                        } else if (turnIdx == 0) {
                            "${currentConv.openingStatement}\n\n${currentTurn.partnerSpeech}"
                        } else {
                            currentTurn.partnerSpeech
                        }
                        val activeObjective = if (interruptionTriggered && currentConv.unexpectedInterruption != null) {
                            currentConv.unexpectedInterruption.reactionObjective
                        } else {
                            currentTurn.expectedUserRolePrompt
                        }

                        // Partner Dialogue Card
                        Surface(
                            shape = RoundedCornerShape(22.dp),
                            color = if (interruptionTriggered) Color(0xFF450A0A) else DeepInk
                        ) {
                            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (interruptionTriggered) "⚡ UNERWARTETER EINWURF!" else currentConv.environment.uppercase(),
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = AmberBright
                                    )
                                    Text(
                                        text = currentConv.rolePartner,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White.copy(alpha = 0.78f)
                                    )
                                }
                                Text(currentConv.title, style = MaterialTheme.typography.titleMedium, color = Color.White)
                                Text("„$activePrompt“", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.92f))

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color.White.copy(alpha = 0.09f)
                                ) {
                                    Column(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text("IHR GESPRÄCHSZIEL (${currentTurn.targetRegister})", style = MaterialTheme.typography.labelSmall, color = AmberBright)
                                        Text(activeObjective, style = MaterialTheme.typography.bodySmall, color = Color.White)
                                    }
                                }

                                if (currentConv.unexpectedInterruption != null && !interruptionTriggered) {
                                    OutlinedButton(
                                        onClick = {
                                            onResetEval()
                                            interruptionTriggered = true
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        border = BorderStroke(1.dp, CoralCrimson),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(Icons.Rounded.Bolt, contentDescription = null, tint = AmberBright, modifier = Modifier.size(16.dp))
                                        Spacer(Modifier.width(6.dp))
                                        Text("Unerwartete Unterbrechung provozieren!", color = AmberBright)
                                    }
                                }
                            }
                        }

                        // Suggested C1 Connectors (1-tap insert)
                        Row(
                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            currentTurn.suggestedC1Connectors.forEach { conn ->
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable {
                                            userResponse = if (userResponse.isBlank()) "$conn " else "$userResponse $conn "
                                        },
                                    shape = RoundedCornerShape(8.dp),
                                    color = CobaltBright.copy(alpha = 0.12f)
                                ) {
                                    Text(
                                        text = "+ $conn",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                        color = CobaltBright,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = userResponse,
                            onValueChange = { userResponse = it },
                            label = { Text("Ihre spontane C1-Erwiderung") },
                            placeholder = { Text("Formulieren Sie Ihre Antwort diplomatisch und präzise …") },
                            minLines = 3,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    if (userResponse.isNotBlank()) {
                                        onEvaluateReply(
                                            currentConv.title,
                                            activePrompt,
                                            activeObjective,
                                            userResponse,
                                            C1Pillar.MODALPARTIKELN
                                        )
                                    }
                                },
                                enabled = userResponse.isNotBlank() && situationEvalState !is SituationEvalUiState.Loading,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1.4f).height(48.dp)
                            ) {
                                if (situationEvalState is SituationEvalUiState.Loading) {
                                    CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text("Analysiert...")
                                } else {
                                    Icon(Icons.AutoMirrored.Rounded.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text("AI Prüfer-Feedback")
                                }
                            }
                            OutlinedButton(
                                onClick = {
                                    onResetEval()
                                    interruptionTriggered = false
                                    if (turnIdx < currentConv.turns.lastIndex) {
                                        turnIdx++
                                    } else {
                                        convIdx = (convIdx + 1) % convs.size
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f).height(48.dp)
                            ) {
                                Text("Weiter")
                            }
                        }
                    }
                }

                1 -> {
                    // === MEDIATION / SPRACHMITTLUNG VISUAL FUNNEL ===
                    val meds = controlState.mediations
                    val currentMed = meds.getOrNull(medIdx % meds.size.coerceAtLeast(1))
                    if (currentMed != null) {
                        VisualMediationFunnelCard(
                            mediationType = currentMed.mediationType,
                            title = currentMed.title,
                            sourceMaterial = currentMed.sourceMaterial,
                            recipientContext = currentMed.recipientContext,
                            pitfalls = currentMed.criticalPitfallsToAvoid
                        )

                        OutlinedTextField(
                            value = userResponse,
                            onValueChange = { userResponse = it },
                            label = { Text("Ihre adressatengerechte Übertragung") },
                            minLines = 3,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    showMediationModel = true
                                    if (userResponse.isNotBlank()) {
                                        onEvaluateReply(
                                            currentMed.title,
                                            currentMed.sourceMaterial,
                                            currentMed.recipientContext,
                                            userResponse,
                                            C1Pillar.KONJUNKTIV
                                        )
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1.4f).height(48.dp)
                            ) {
                                Text("Prüfen & Muster zeigen")
                            }
                            OutlinedButton(
                                onClick = {
                                    onResetEval()
                                    showMediationModel = false
                                    medIdx = (medIdx + 1) % meds.size
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f).height(48.dp)
                            ) {
                                Text("Nächster Fall")
                            }
                        }

                        if (showMediationModel) {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = AmberGold.copy(alpha = 0.10f),
                                border = BorderStroke(1.dp, AmberGold.copy(alpha = 0.45f))
                            ) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text("REFERENZ-ÜBERTRAGUNG", style = MaterialTheme.typography.labelSmall, color = AmberGold)
                                    Text(currentMed.targetReformulationModel, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // === U15 FUSSBALL-COACHING LIVE ANIMATED TAKTIK-BOARD ===
                    val scens = controlState.footballScenarios
                    val sc = scens.getOrNull(fbIdx % scens.size.coerceAtLeast(1))
                    if (sc != null) {
                        InteractiveTacticalPitchCanvas(
                            matchPhase = sc.matchPhase,
                            title = sc.title,
                            isSolved = fbChoice != null
                        )
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                        ) {
                            Text(
                                text = sc.situationDescription,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(12.dp)
                            )
                        }

                        sc.options.forEachIndexed { idx, opt ->
                            val isCorr = idx == sc.correctIndex
                            val chosen = fbChoice == idx
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable(enabled = fbChoice == null) { fbChoice = idx },
                                shape = RoundedCornerShape(14.dp),
                                color = when {
                                    fbChoice == null -> MaterialTheme.colorScheme.surface
                                    isCorr -> EmeraldSage.copy(alpha = 0.16f)
                                    chosen -> CoralCrimson.copy(alpha = 0.16f)
                                    else -> MaterialTheme.colorScheme.surface
                                },
                                border = BorderStroke(
                                    1.dp,
                                    when {
                                        fbChoice != null && isCorr -> EmeraldSage
                                        fbChoice != null && chosen -> CoralCrimson
                                        else -> MaterialTheme.colorScheme.outline
                                    }
                                )
                            ) {
                                Text(opt, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(14.dp))
                            }
                        }

                        if (fbChoice != null) {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = EmeraldSage.copy(alpha = 0.10f),
                                border = BorderStroke(1.dp, EmeraldSage)
                            ) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text("PLATZKOMMANDO ZUM RUFEN:", style = MaterialTheme.typography.labelSmall, color = EmeraldSage)
                                    Text("„${sc.keyCommandToShout}“", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                                    Text(sc.coachingExplanation, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }

                        Button(
                            onClick = { fbIdx = (fbIdx + 1) % scens.size },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Text("Nächste Spielsituation (${(fbIdx + 1)}/${scens.size})")
                        }
                    }
                }
            }

            // Shared AI Evaluation Feedback Card for Modes 0 & 1
            if (selectedMode != 2) {
                when (situationEvalState) {
                    is SituationEvalUiState.Success -> {
                        val fb = situationEvalState.feedback
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = EmeraldSage.copy(alpha = 0.10f),
                            border = BorderStroke(1.5.dp, EmeraldSage)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = EmeraldSage)
                                    Text("${fb.scorePercent}% · ${fb.registerVerdict}", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = EmeraldSage)
                                }
                                Text(fb.strengthsDe, style = MaterialTheme.typography.bodySmall)
                                Text("Souveräne C1-Formulierung:\n„${fb.upgradedResponseDe}“", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                                Text("80/20 Takeaway: ${fb.keyTakeawayDe}", style = MaterialTheme.typography.labelSmall, color = AmberGold)
                            }
                        }
                    }
                    is SituationEvalUiState.Error -> {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Rounded.WarningAmber, contentDescription = null, tint = CoralCrimson)
                            Text(situationEvalState.message, style = MaterialTheme.typography.bodySmall, color = CoralCrimson)
                        }
                    }
                    else -> Unit
                }
            }
        }
    }
}

@Composable
private fun SimModePill(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 6.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
