package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Forum
import androidx.compose.material.icons.rounded.Headphones
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.PlayArrow
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.audio.rememberHybridC1AudioController
import com.example.domain.model.C1Pillar
import com.example.domain.model.ContextTrack
import com.example.domain.model.RepoCurriculumUnit
import com.example.domain.model.TranscriptTurn
import com.example.ui.SituationEvalUiState
import com.example.ui.components.ImprintMentalModelCanvas
import com.example.ui.components.InteractiveClauseSnapCard
import com.example.ui.components.SentenceSurgeryVisualCard
import com.example.ui.components.VisualComicDialogueStage
import com.example.ui.components.VisualStoryMapFlow
import com.example.ui.components.pillarAccentColor
import com.example.ui.components.visualIllustrationResFor
import com.example.ui.theme.AmberBright
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CobaltBright
import com.example.ui.theme.CoralCrimson
import com.example.ui.theme.DeepInk
import com.example.ui.theme.EmeraldSage

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RepoUnitStudioScreen(
    unit: RepoCurriculumUnit,
    activeContextTrack: ContextTrack,
    situationEvalState: SituationEvalUiState,
    onSelectContextTrack: (ContextTrack) -> Unit,
    onStepCompleted: (String, String, Float) -> Unit,
    onEvaluateSituationReply: (String, String, String, String, C1Pillar) -> Unit,
    onResetSituationEval: () -> Unit,
    onLaunchPillarDrill: (C1Pillar) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    // 4 Studio Sections: 0 = Visual Deck, 1 = Chirurgie & Quiz, 2 = Buch & Hören, 3 = Situation
    var studioTab by remember(unit.id) { mutableIntStateOf(0) }
    var activeStepIdx by remember(unit.id) { mutableIntStateOf(0) }
    var microCheckChoice by remember(unit.id) { mutableStateOf<Int?>(null) }
    var unitQuizChoice by remember(unit.id) { mutableStateOf<Int?>(null) }
    var roleplayTurn by remember(unit.id, activeContextTrack) { mutableIntStateOf(0) }
    var userReply by remember(unit.id, roleplayTurn, activeContextTrack) { mutableStateOf("") }

    val accent = pillarAccentColor(unit.mappedPillar)
    val deck = unit.visualDeck
    val audioController = rememberHybridC1AudioController()

    fun speakLine(line: String) {
        audioController.speakPhrase(line, 1.0f)
    }

    LaunchedEffect(unit.id) {
        onStepCompleted(unit.id, "VISUAL", 6f)
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 600.dp)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Top Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("unit_back_button")) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "LERNSTUFE ${unit.stage} · ${(unit.bookBridge?.shortBookBadge ?: unit.stageTitle).uppercase()}",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = accent
                    )
                    Text(
                        text = unit.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            // 4-Section Story Tab Switcher
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val tabLabels = listOf("1. Visual", "2. Puzzle", "3. Buch", "4. Situation")
                tabLabels.forEachIndexed { idx, label ->
                    val sel = studioTab == idx
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                studioTab = idx
                                if (idx == 2) onStepCompleted(unit.id, "BOOK", 5f)
                            },
                        shape = RoundedCornerShape(10.dp),
                        color = if (sel) accent else MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, if (sel) accent else MaterialTheme.colorScheme.outline)
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (sel) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }

            // Scrollable Section Body
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Context Track Strip (Alltag / Schwäbisch / EV-Technik / Fußball U15)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ContextTrack.entries.forEach { tr ->
                        val sel = tr == activeContextTrack
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(99.dp))
                                .clickable { onSelectContextTrack(tr) },
                            shape = RoundedCornerShape(99.dp),
                            color = if (sel) DeepInk else MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, if (sel) AmberBright else MaterialTheme.colorScheme.outline)
                        ) {
                            Text(
                                text = tr.shortLabel,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = if (sel) AmberBright else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                when (studioTab) {
                    0 -> {
                        // === TAB 0: IMPRINT VISUAL MENTAL MODEL & 4-CONTEXT TRANSFORMS ===
                        if (deck != null) {
                            val currentStep = deck.steps.getOrElse(activeStepIdx) { deck.steps.first() }
                            ImprintMentalModelCanvas(
                                diagramType = deck.diagramType,
                                modelTitle = deck.modelTitle,
                                visualHook = deck.visualHook,
                                nodes = currentStep.nodes,
                                highlightIndex = currentStep.highlightIndex,
                                accent = accent
                            )

                            // Interactive 3-Step Visual Cards
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                deck.steps.forEachIndexed { idx, st ->
                                    val sel = idx == activeStepIdx
                                    Surface(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(14.dp))
                                            .clickable { activeStepIdx = idx },
                                        shape = RoundedCornerShape(14.dp),
                                        color = if (sel) accent.copy(alpha = 0.14f) else MaterialTheme.colorScheme.surface,
                                        border = BorderStroke(if (sel) 1.5.dp else 1.dp, if (sel) accent else MaterialTheme.colorScheme.outline)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(10.dp),
                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(st.tag, style = MaterialTheme.typography.labelSmall, color = accent)
                                            Text(st.title, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), maxLines = 2)
                                        }
                                    }
                                }
                            }

                            // Visual Step Caption Pill + Illustrated Before -> After Split Diagram
                            val ctxPair = deck.contextTransforms[activeContextTrack] ?: deck.contextTransforms.values.first()
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = DeepInk,
                                border = BorderStroke(1.dp, accent.copy(alpha = 0.55f))
                            ) {
                                Column {
                                    Box(modifier = Modifier.fillMaxWidth().height(126.dp)) {
                                        Image(
                                            painter = painterResource(id = visualIllustrationResFor(track = activeContextTrack)),
                                            contentDescription = activeContextTrack.titleDe,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(Brush.verticalGradient(listOf(DeepInk.copy(alpha = 0.2f), DeepInk.copy(alpha = 0.92f))))
                                        )
                                        Column(
                                            modifier = Modifier
                                                .align(Alignment.BottomStart)
                                                .padding(12.dp),
                                            verticalArrangement = Arrangement.spacedBy(2.dp)
                                        ) {
                                            Text(
                                                text = "${activeContextTrack.shortLabel.uppercase()} · ${ctxPair.sceneLabel.uppercase()}",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = AmberBright
                                            )
                                            Text(
                                                text = currentStep.caption,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color.White,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Surface(
                                                modifier = Modifier.weight(1f),
                                                shape = RoundedCornerShape(12.dp),
                                                color = CoralCrimson.copy(alpha = 0.16f),
                                                border = BorderStroke(1.dp, CoralCrimson.copy(alpha = 0.55f))
                                            ) {
                                                Column(modifier = Modifier.padding(9.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                                    Text("✕ ${ctxPair.avoidLabel}", style = MaterialTheme.typography.labelSmall, color = CoralCrimson)
                                                    Text(ctxPair.avoidPhrase, style = MaterialTheme.typography.bodySmall, color = Color.White)
                                                }
                                            }
                                            Icon(Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = null, tint = AmberBright, modifier = Modifier.size(16.dp))
                                            Surface(
                                                modifier = Modifier
                                                    .weight(1.15f)
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .clickable { speakLine(ctxPair.c1Phrase) },
                                                shape = RoundedCornerShape(12.dp),
                                                color = EmeraldSage.copy(alpha = 0.2f),
                                                border = BorderStroke(1.dp, EmeraldSage)
                                            ) {
                                                Column(modifier = Modifier.padding(9.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                                    Text("🔊 ✓ SOUVERÄN C1", style = MaterialTheme.typography.labelSmall, color = EmeraldSage)
                                                    Text(ctxPair.c1Phrase, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                                                }
                                            }
                                        }
                                        Surface(
                                            shape = RoundedCornerShape(99.dp),
                                            color = Color.White.copy(alpha = 0.08f)
                                        ) {
                                            Text(
                                                text = "⚡ ${ctxPair.grammarHint}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = AmberBright,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            // Embedded Visual MicroCheck
                            val mc = deck.microCheck
                            Surface(
                                shape = RoundedCornerShape(18.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = BorderStroke(1.dp, accent.copy(alpha = 0.45f))
                            ) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text("VISUAL MICRO-CHECK", style = MaterialTheme.typography.labelSmall, color = accent)
                                    Text(mc.prompt, style = MaterialTheme.typography.titleSmall)
                                    mc.options.forEachIndexed { idx, opt ->
                                        val isCorr = idx == mc.correctIndex
                                        val chosen = microCheckChoice == idx
                                        val bg = when {
                                            microCheckChoice == null -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                            isCorr -> EmeraldSage.copy(alpha = 0.16f)
                                            chosen -> CoralCrimson.copy(alpha = 0.16f)
                                            else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
                                        }
                                        Surface(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(10.dp))
                                                .clickable(enabled = microCheckChoice == null) {
                                                    microCheckChoice = idx
                                                },
                                            shape = RoundedCornerShape(10.dp),
                                            color = bg
                                        ) {
                                            Text(opt, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(12.dp))
                                        }
                                    }
                                    if (microCheckChoice != null) {
                                        Text(mc.explanation, style = MaterialTheme.typography.bodySmall, color = EmeraldSage)
                                    }
                                }
                            }
                        }
                    }

                    1 -> {
                        // === TAB 1: CLAUSE SNAP PUZZLE, SENTENCE SURGERY & UNIT QUIZ ===
                        if (deck != null) {
                            InteractiveClauseSnapCard(
                                puzzle = deck.clausePuzzle,
                                onSolved = { onStepCompleted(unit.id, "PUZZLE", 8f) }
                            )
                            SentenceSurgeryVisualCard(surgery = deck.surgery)
                        }

                        // Authentic Unit Comprehension & Pragmatics Check
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("LEKTIONS-CHECK", style = MaterialTheme.typography.labelSmall, color = accent)
                                Text(unit.content.question, style = MaterialTheme.typography.titleSmall)
                                unit.content.options.forEachIndexed { idx, opt ->
                                    val isCorr = idx == unit.content.correctOption
                                    val chosen = unitQuizChoice == idx
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .clickable(enabled = unitQuizChoice == null) {
                                                unitQuizChoice = idx
                                                if (isCorr) onStepCompleted(unit.id, "PUZZLE", 6f)
                                            },
                                        shape = RoundedCornerShape(10.dp),
                                        color = when {
                                            unitQuizChoice == null -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                            isCorr -> EmeraldSage.copy(alpha = 0.16f)
                                            chosen -> CoralCrimson.copy(alpha = 0.16f)
                                            else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
                                        }
                                    ) {
                                        Text(opt, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(12.dp))
                                    }
                                }
                                if (unitQuizChoice != null) {
                                    Text(unit.content.explanation, style = MaterialTheme.typography.bodySmall, color = EmeraldSage)
                                }
                            }
                        }
                    }

                    2 -> {
                        // === TAB 2: ASPEKTE NEU / SICHER! BOOK BRIDGE, PODCAST & DIALOGUES ===
                        unit.bookBridge?.let { bb ->
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = AmberGold.copy(alpha = 0.10f),
                                border = BorderStroke(1.5.dp, AmberGold.copy(alpha = 0.55f))
                            ) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Icon(Icons.Rounded.MenuBook, contentDescription = null, tint = AmberGold)
                                        Text(
                                            text = "LEHRBUCH-BRÜCKE · ${bb.shortBookBadge.uppercase()}",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = AmberGold
                                        )
                                    }
                                    Text(bb.bookReference, style = MaterialTheme.typography.titleMedium)
                                    Text(
                                        text = "Audio-Hinweis: ${bb.audioFilenameHint}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = bb.originalPrompt,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }

                        unit.listeningEpisode?.let { ep ->
                            Surface(
                                shape = RoundedCornerShape(18.dp),
                                color = CobaltBright.copy(alpha = 0.09f),
                                border = BorderStroke(1.dp, CobaltBright.copy(alpha = 0.4f))
                            ) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Icon(Icons.Rounded.Headphones, contentDescription = null, tint = CobaltBright)
                                        Text("AUTHENTISCHES HÖR-LABOR", style = MaterialTheme.typography.labelSmall, color = CobaltBright)
                                    }
                                    Text(ep.title, style = MaterialTheme.typography.titleSmall)
                                    Text(ep.task, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }

                        // Visual 3-Node Story Map + Comic Bubble Audio Stage + Redemittel Pills
                        VisualStoryMapFlow(
                            title = "Szenen-Grafik · ${unit.title}",
                            rawText = unit.content.reading,
                            accent = accent
                        )

                        val comicTurns = remember(unit.content.listening) {
                            unit.content.listening
                                .split(Regex("(?<=[.!?])\\s+"))
                                .filter { it.isNotBlank() }
                                .mapIndexed { idx, line ->
                                    TranscriptTurn(
                                        speaker = if (idx % 2 == 0) "Sprecher A" else "Sprecher B",
                                        line = line.trim('„', '“', '"'),
                                        cue = if (idx == 0) "Impuls" else "Reaktion"
                                    )
                                }
                        }

                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(
                                    text = "HÖRDIALOG-BUBBLES & C1-BAUSTEINE (ANTIPPEN ZUM HÖREN)",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = CobaltBright
                                )
                                VisualComicDialogueStage(
                                    turns = comicTurns,
                                    onSpeakLine = { speakLine(it) }
                                )
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    unit.content.expressions.forEach { expr ->
                                        Surface(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(99.dp))
                                                .clickable { speakLine(expr) },
                                            shape = RoundedCornerShape(99.dp),
                                            color = EmeraldSage.copy(alpha = 0.14f),
                                            border = BorderStroke(1.dp, EmeraldSage.copy(alpha = 0.5f))
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Icon(Icons.AutoMirrored.Rounded.VolumeUp, contentDescription = null, tint = EmeraldSage, modifier = Modifier.size(14.dp))
                                                Text(expr, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    3 -> {
                        // === TAB 3: INTERACTIVE SITUATIONS-TRAINING (LESSON CONVERSATION STUDIO) ===
                        val partnerRole = when (activeContextTrack) {
                            ContextTrack.ENGINEERING -> "Dr. Kramer (Chefingenieur EV-Review)"
                            ContextTrack.COACHING -> "Jugendleiter / Spielervater (U15)"
                            ContextTrack.SOCIAL -> "Vorstand beim schwäbischen Vereinsabend"
                            ContextTrack.GENERAL -> "Gesprächspartner / Behördenleitung"
                        }
                        val turns = listOf(
                            Triple(
                                partnerRole,
                                "Wir sprechen über „${unit.contexts[activeContextTrack]}“. ${unit.content.speaking}",
                                "Eröffnen Sie souverän im passenden Register und bringen Sie Ihren Kernpunkt präzise auf den Punkt."
                            ),
                            Triple(
                                "$partnerRole (Gegenwind / Rückfrage)",
                                unit.content.interaction,
                                "Parieren Sie den Einwand höflich, sichern Sie das Verständnis und nennen Sie eine klare Bedingung."
                            ),
                            Triple(
                                "$partnerRole (Transfer)",
                                unit.content.transfer,
                                "Schließen Sie mit einer verbindlichen Vereinbarung unter Nutzung von mindestens einem C1-Redemittel."
                            )
                        )
                        val (speaker, partnerPrompt, turnGoal) = turns[roleplayTurn.coerceIn(0, 2)]

                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = DeepInk
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(Icons.Rounded.Forum, contentDescription = null, tint = AmberBright)
                                    Text(
                                        text = "SITUATIONS-SIMULATOR · RUNDE ${roleplayTurn + 1}/3",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = AmberBright
                                    )
                                }
                                Text(speaker, style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.75f))
                                Text("„$partnerPrompt“", style = MaterialTheme.typography.bodyLarge, color = Color.White)
                                Text("Ziel: $turnGoal", style = MaterialTheme.typography.bodySmall, color = AmberBright)
                            }
                        }

                        // Suggested Redemittel Chips to insert with 1 tap
                        Row(
                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            unit.content.expressions.take(4).forEach { expr ->
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable {
                                            userReply = if (userReply.isBlank()) expr else "$userReply $expr"
                                        },
                                    shape = RoundedCornerShape(8.dp),
                                    color = accent.copy(alpha = 0.12f)
                                ) {
                                    Text(
                                        text = "+ $expr",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = accent,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = userReply,
                            onValueChange = { userReply = it },
                            label = { Text("Ihre C1-Reaktion in der Situation") },
                            placeholder = { Text(unit.content.controlledModel) },
                            minLines = 3,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    onStepCompleted(unit.id, "SITUATION", 10f)
                                    onEvaluateSituationReply(
                                        "${unit.title} (${activeContextTrack.shortLabel})",
                                        partnerPrompt,
                                        turnGoal,
                                        userReply.ifBlank { unit.content.controlledModel },
                                        unit.mappedPillar
                                    )
                                },
                                enabled = situationEvalState !is SituationEvalUiState.Loading,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1.4f).height(48.dp)
                            ) {
                                if (situationEvalState is SituationEvalUiState.Loading) {
                                    CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text("Prüft...")
                                } else {
                                    Icon(Icons.AutoMirrored.Rounded.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text("AI C1-Feedback")
                                }
                            }
                            OutlinedButton(
                                onClick = {
                                    onResetSituationEval()
                                    roleplayTurn = (roleplayTurn + 1) % 3
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f).height(48.dp)
                            ) {
                                Text("Nächste Runde")
                            }
                        }

                        when (situationEvalState) {
                            is SituationEvalUiState.Success -> {
                                val fb = situationEvalState.feedback
                                Surface(
                                    shape = RoundedCornerShape(18.dp),
                                    color = EmeraldSage.copy(alpha = 0.10f),
                                    border = BorderStroke(1.5.dp, EmeraldSage)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = EmeraldSage, modifier = Modifier.size(18.dp))
                                            Text(
                                                "${fb.scorePercent}% · ${fb.registerVerdict}",
                                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                                color = EmeraldSage
                                            )
                                        }
                                        Text(fb.strengthsDe, style = MaterialTheme.typography.bodySmall)
                                        Text(
                                            "Souveräne C1-Musterlösung:\n„${fb.upgradedResponseDe}“",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                                        )
                                        Text("80/20 Takeaway: ${fb.keyTakeawayDe}", style = MaterialTheme.typography.labelSmall, color = AmberGold)
                                    }
                                }
                            }
                            is SituationEvalUiState.Error -> {
                                Text(situationEvalState.message, style = MaterialTheme.typography.bodySmall, color = CoralCrimson)
                            }
                            else -> Unit
                        }
                    }
                }
            }

            // Bottom Navigation Bar inside Unit Studio
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { if (studioTab > 0) studioTab-- else onBack() },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(1f).height(48.dp)
                ) {
                    Text(if (studioTab > 0) "Zurück" else "Roadmap")
                }
                Button(
                    onClick = {
                        if (studioTab < 3) {
                            studioTab++
                            if (studioTab == 2) onStepCompleted(unit.id, "BOOK", 5f)
                        } else {
                            onLaunchPillarDrill(unit.mappedPillar)
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(1.4f).height(48.dp)
                ) {
                    Icon(Icons.Rounded.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(if (studioTab < 3) "Weiter (${studioTab + 2}/4)" else "Adaptive Unit-Probe")
                }
            }
        }
    }
}
