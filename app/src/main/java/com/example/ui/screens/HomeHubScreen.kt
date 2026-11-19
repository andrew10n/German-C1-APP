package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Radar
import androidx.compose.material.icons.rounded.RecordVoiceOver
import androidx.compose.material.icons.rounded.Route
import androidx.compose.material.icons.rounded.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.domain.engine.ControllerDirective
import com.example.domain.engine.MasterControlState
import com.example.domain.model.C1Pillar
import com.example.domain.model.ContextTrack
import com.example.ui.components.BlueprintOverviewRepoUnitCard
import com.example.ui.components.NodeHorizontalSlot
import com.example.ui.components.RoadmapMilestoneGate
import com.example.ui.components.RoadmapRepoUnitStation
import com.example.ui.components.RoadmapSCurveConnector
import com.example.ui.components.RoadmapStageHeader
import com.example.ui.components.pillarAccentColor
import com.example.ui.components.roadmapSlotForIndex
import com.example.ui.components.visualIllustrationResFor
import com.example.ui.theme.AmberGold
import com.example.ui.theme.AmgPetronasTeal
import com.example.ui.theme.BvbSignalYellow
import com.example.ui.theme.CobaltBright
import com.example.ui.theme.CoralCrimson
import com.example.ui.theme.DeepInk
import com.example.ui.theme.EmeraldSage
import com.example.ui.theme.PorscheRedline
import com.example.ui.theme.contentColorOnAccent
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private enum class HomeViewMode {
    ROADMAP_PATH,
    OVERVIEW_BLUEPRINT
}

@Composable
fun HomeHubScreen(
    controlState: MasterControlState,
    onSelectContextTrack: (ContextTrack) -> Unit,
    onExecuteDirective: (ControllerDirective) -> Unit,
    onOpenRepoUnit: (String) -> Unit,
    onOpenPersonalizedLab: (String?) -> Unit,
    onOpenSituationsSimulator: () -> Unit,
    onStartPillarDrill: (C1Pillar) -> Unit,
    onOpenCalibration: () -> Unit,
    onOpenMistakeRepair: () -> Unit,
    onOpenControllerCenter: () -> Unit,
    onOpenAiStudio: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pareto = controlState.paretoSummary
    val activeTrack = controlState.selectedContextTrack
    val stages = controlState.stagesRoadmap
    val allRepoUnitsOrdered = remember(stages) { stages.flatMap { it.units } }

    var viewMode by remember { mutableStateOf(HomeViewMode.ROADMAP_PATH) }
    var stageFilter by remember { mutableIntStateOf(-2) }
    var bookFilter by remember { mutableStateOf("ALL") }

    val defaultExpandedId = controlState.prioritizedRepoUnits.firstOrNull()?.unit?.id
        ?: allRepoUnitsOrdered.firstOrNull()?.unit?.id
    var expandedUnitId by remember(defaultExpandedId) { mutableStateOf(defaultExpandedId) }

    val mistakesByPillar = remember(controlState.unresolvedMistakes) {
        controlState.unresolvedMistakes.groupBy { it.pillarId }
    }

    val unitGlobalOrderMap = remember(allRepoUnitsOrdered) {
        allRepoUnitsOrdered.mapIndexed { index, item -> item.unit.id to (index + 1) }.toMap()
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 600.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1. Minimalist Cockpit Header + Drive-Mode Track Strip
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(BvbSignalYellow)
                                )
                                Text(
                                    text = "STUTTGART · BVB 09 EDITION",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = BvbSignalYellow
                                )
                            }
                            Text(
                                text = "C1 Cockpit",
                                style = MaterialTheme.typography.headlineLarge,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(99.dp),
                                color = BvbSignalYellow.copy(alpha = 0.14f),
                                border = BorderStroke(1.dp, BvbSignalYellow.copy(alpha = 0.45f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        Icons.Rounded.LocalFireDepartment,
                                        contentDescription = "Streak",
                                        tint = BvbSignalYellow,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Text(
                                        "${controlState.streakDays}d",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = BvbSignalYellow
                                    )
                                }
                            }
                            Surface(
                                modifier = Modifier
                                    .testTag("open_controller_pill")
                                    .clip(RoundedCornerShape(99.dp))
                                    .clickable { onOpenControllerCenter() },
                                shape = RoundedCornerShape(99.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                border = BorderStroke(1.dp, AmgPetronasTeal.copy(alpha = 0.55f))
                            ) {
                                Text(
                                    text = "${controlState.minutesToday}/${controlState.dailyGoalMinutes}m",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = AmgPetronasTeal,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    // Slim Drive-Mode Selector (replaces bulky 4-card image carousel)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ContextTrack.entries.forEach { track ->
                            val selected = track == activeTrack
                            val modeTag = when (track) {
                                ContextTrack.ENGINEERING -> "⚡ AMG · 911"
                                ContextTrack.COACHING -> "⚽ BVB · Taktik"
                                ContextTrack.SOCIAL -> "🥨 Esslingen"
                                ContextTrack.GENERAL -> "🏛 Alltag"
                            }
                            Surface(
                                modifier = Modifier
                                    .height(36.dp)
                                    .testTag("track_chip_${track.id}")
                                    .clip(RoundedCornerShape(99.dp))
                                    .clickable { onSelectContextTrack(track) },
                                shape = RoundedCornerShape(99.dp),
                                color = if (selected) BvbSignalYellow else MaterialTheme.colorScheme.surface,
                                border = BorderStroke(
                                    1.dp,
                                    if (selected) BvbSignalYellow else MaterialTheme.colorScheme.outline
                                )
                            ) {
                                Box(
                                    modifier = Modifier.padding(horizontal = 13.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = modeTag,
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = if (selected) DeepInk else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 2. ONE Essential Cockpit Pod: Porsche 911 Sport Chrono Central Tachometer + Next High-ROI Stint
            // Replaces 3 separate stacked dashboards (Hero Directive + DualReadinessSplitCard + DailyProgressRing)
            item {
                val directive = controlState.directive
                val topExId = controlState.prioritizedPersonalizedExercises.firstOrNull()?.exercise?.id
                SportChronoStintPod(
                    artworkRes = activeTrack.artworkRes,
                    functionalPercent = controlState.functionalC1ReadinessPercent,
                    examPercent = controlState.examReadinessPercent,
                    cefrLevel = controlState.cefrLevel,
                    directive = directive,
                    onExecuteDirective = { onExecuteDirective(directive) },
                    onOpenVisualLab = { onOpenPersonalizedLab(topExId) },
                    onOpenSituations = onOpenSituationsSimulator
                )
            }

            // 3. Illustrated Visual Worlds Strip (6 Real-World Scenario Families)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Szenen-Welten (20 Visual-Labs)",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Hören · Spontan · Transfer",
                            style = MaterialTheme.typography.labelSmall,
                            color = AmgPetronasTeal
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        controlState.personalizedLibrary.scenarioFamilies.forEachIndexed { idx, fam ->
                            val famExercises = controlState.prioritizedPersonalizedExercises
                                .filter { it.exercise.scenarioFamilyId == fam.id }
                            val famEx = famExercises.firstOrNull()
                            val accent = when (idx % 3) {
                                0 -> BvbSignalYellow
                                1 -> AmgPetronasTeal
                                else -> EmeraldSage
                            }
                            val artRes = visualIllustrationResFor(sceneType = fam.visualTheme, familyId = fam.id)
                            Surface(
                                modifier = Modifier
                                    .width(196.dp)
                                    .height(112.dp)
                                    .testTag("scenario_family_${fam.id}")
                                    .clip(RoundedCornerShape(18.dp))
                                    .clickable { onOpenPersonalizedLab(famEx?.exercise?.id) },
                                shape = RoundedCornerShape(18.dp),
                                color = DeepInk,
                                border = BorderStroke(1.2.dp, accent.copy(alpha = 0.7f))
                            ) {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    Image(
                                        painter = painterResource(id = artRes),
                                        contentDescription = fam.title,
                                        contentScale = ContentScale.Crop,
                                        alpha = 0.62f,
                                        modifier = Modifier.matchParentSize()
                                    )
                                    Box(
                                        modifier = Modifier
                                            .matchParentSize()
                                            .background(
                                                Brush.verticalGradient(
                                                    listOf(
                                                        DeepInk.copy(alpha = 0.18f),
                                                        DeepInk.copy(alpha = 0.90f)
                                                    )
                                                )
                                            )
                                    )
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(11.dp),
                                        verticalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = accent
                                            ) {
                                                Text(
                                                    text = famEx?.exercise?.difficulty ?: "C1",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                    color = contentColorOnAccent(accent),
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                famExercises.take(4).forEach { pEx ->
                                                    val done = pEx.completedBeatsCount > 0
                                                    Box(
                                                        modifier = Modifier
                                                            .size(7.dp)
                                                            .clip(CircleShape)
                                                            .background(
                                                                if (done) BvbSignalYellow else Color.White.copy(alpha = 0.30f)
                                                            )
                                                    )
                                                }
                                            }
                                        }
                                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                            Text(
                                                text = fam.title.substringBefore(":"),
                                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                                color = Color.White,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = "${famExercises.size} Szenen →",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = accent
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 4. Clean Track Switcher (Strecke vs Lehrwerke) + Stage Quick-Jump
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ModeTogglePill(
                        label = "C1 Strecke (28)",
                        icon = { Icon(Icons.Rounded.Route, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        selected = viewMode == HomeViewMode.ROADMAP_PATH,
                        onClick = { viewMode = HomeViewMode.ROADMAP_PATH },
                        modifier = Modifier.weight(1f).testTag("toggle_roadmap_view")
                    )
                    ModeTogglePill(
                        label = "Buch-Index",
                        icon = { Icon(Icons.Rounded.GridView, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        selected = viewMode == HomeViewMode.OVERVIEW_BLUEPRINT,
                        onClick = { viewMode = HomeViewMode.OVERVIEW_BLUEPRINT },
                        modifier = Modifier.weight(1f).testTag("toggle_overview_view")
                    )
                }
            }

            if (viewMode == HomeViewMode.ROADMAP_PATH) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilterChipPill(
                            label = "Alle (28)",
                            selected = stageFilter == -2,
                            accent = AmgPetronasTeal,
                            onClick = { stageFilter = -2 }
                        )
                        FilterChipPill(
                            label = "★ Vital 20% (6)",
                            selected = stageFilter == -1,
                            accent = BvbSignalYellow,
                            onClick = { stageFilter = -1 }
                        )
                        stages.forEach { st ->
                            FilterChipPill(
                                label = "S${st.stageIndex}",
                                selected = stageFilter == st.stageIndex,
                                accent = stageAccentColor(st.stageIndex),
                                onClick = { stageFilter = st.stageIndex }
                            )
                        }
                    }
                }

                if (stageFilter == -1) {
                    val vitalUnits = controlState.prioritizedRepoUnits.filter { it.isParetoVital20 }
                    item {
                        RoadmapStageHeader(
                            stageLabel = "Qualifying Fast-Track · Vital 20%",
                            title = "Ihre 6 höchsten C1-Hebel-Units",
                            subtitle = "Maximaler Ertrag aus allen 28 Aspekte neu & Sicher! Units",
                            accent = BvbSignalYellow,
                            masteryBadge = "80% ROI"
                        )
                    }
                    items(vitalUnits.size, key = { idx -> "vital_${vitalUnits[idx].unit.id}" }) { idx ->
                        val uItem = vitalUnits[idx]
                        val slot = roadmapSlotForIndex(idx)
                        val mistakeCnt = mistakesByPillar[uItem.unit.mappedPillar.id]?.size ?: 0
                        Column {
                            RoadmapRepoUnitStation(
                                item = uItem,
                                unitOrderNumber = unitGlobalOrderMap[uItem.unit.id] ?: (idx + 1),
                                activeContextTrack = activeTrack,
                                slot = slot,
                                isExpanded = expandedUnitId == uItem.unit.id,
                                unresolvedMistakesCount = mistakeCnt,
                                onToggleExpand = {
                                    expandedUnitId = if (expandedUnitId == uItem.unit.id) null else uItem.unit.id
                                },
                                onOpenUnitStudio = { onOpenRepoUnit(uItem.unit.id) },
                                onStartUnitDrill = { onStartPillarDrill(uItem.unit.mappedPillar) },
                                onOpenSituationsLab = onOpenSituationsSimulator
                            )
                            if (idx < vitalUnits.lastIndex) {
                                RoadmapSCurveConnector(
                                    fromSlot = slot,
                                    toSlot = roadmapSlotForIndex(idx + 1),
                                    fromColor = BvbSignalYellow,
                                    toColor = AmgPetronasTeal,
                                    isHighlighted = true
                                )
                            }
                        }
                    }
                } else {
                    val visibleStages = if (stageFilter >= 0) {
                        stages.filter { it.stageIndex == stageFilter }
                    } else {
                        stages
                    }

                    visibleStages.forEach { stageGroup ->
                        val stageAccent = stageAccentColor(stageGroup.stageIndex)
                        val cleanTitle = stageGroup.stageTitle.substringAfter(": ").ifBlank { stageGroup.stageTitle }

                        item(key = "stage_hdr_${stageGroup.stageIndex}") {
                            RoadmapStageHeader(
                                stageLabel = "Sektor ${stageGroup.stageIndex} · ${stageGroup.units.size} Units",
                                title = cleanTitle,
                                subtitle = stageSubtitleDe(stageGroup.stageIndex, activeTrack),
                                accent = stageAccent,
                                masteryBadge = "${stageGroup.averageMasteryPercent}%"
                            )
                        }

                        items(
                            count = stageGroup.units.size,
                            key = { idx -> "st_${stageGroup.stageIndex}_${stageGroup.units[idx].unit.id}" }
                        ) { idx ->
                            val uItem = stageGroup.units[idx]
                            val globalOrder = unitGlobalOrderMap[uItem.unit.id] ?: (idx + 1)
                            val slot = roadmapSlotForIndex(globalOrder - 1)
                            val nextSlot = if (idx < stageGroup.units.lastIndex) {
                                roadmapSlotForIndex(globalOrder)
                            } else {
                                NodeHorizontalSlot.CENTER
                            }
                            val mistakeCnt = mistakesByPillar[uItem.unit.mappedPillar.id]?.size ?: 0
                            Column {
                                RoadmapRepoUnitStation(
                                    item = uItem,
                                    unitOrderNumber = globalOrder,
                                    activeContextTrack = activeTrack,
                                    slot = slot,
                                    isExpanded = expandedUnitId == uItem.unit.id,
                                    unresolvedMistakesCount = mistakeCnt,
                                    onToggleExpand = {
                                        expandedUnitId = if (expandedUnitId == uItem.unit.id) null else uItem.unit.id
                                    },
                                    onOpenUnitStudio = { onOpenRepoUnit(uItem.unit.id) },
                                    onStartUnitDrill = { onStartPillarDrill(uItem.unit.mappedPillar) },
                                    onOpenSituationsLab = onOpenSituationsSimulator
                                )
                                RoadmapSCurveConnector(
                                    fromSlot = slot,
                                    toSlot = nextSlot,
                                    fromColor = if (uItem.isParetoVital20) BvbSignalYellow else pillarAccentColor(uItem.unit.mappedPillar),
                                    toColor = stageAccent,
                                    isHighlighted = uItem.isParetoVital20 || uItem.completedStepsCount > 0
                                )
                            }
                        }

                        if (stageGroup.stageIndex == 0 && stageFilter == -2) {
                            item(key = "gate_personalized_visual_lab") {
                                val topExId = controlState.prioritizedPersonalizedExercises.firstOrNull()?.exercise?.id
                                Column {
                                    RoadmapMilestoneGate(
                                        badge = "AMG Prüfstand · Esslingen · BVB Taktik (${controlState.prioritizedPersonalizedExercises.size})",
                                        title = "Blind-Hören & Spontan-Cockpit",
                                        subtitle = "Echte Reduktionen hören & spontan reagieren, bevor Alternativen erscheinen.",
                                        ctaLabel = "Start",
                                        accent = BvbSignalYellow,
                                        icon = Icons.Rounded.AutoAwesome,
                                        onClick = { onOpenPersonalizedLab(topExId) },
                                        testTag = "roadmap_personalized_lab_gate"
                                    )
                                    RoadmapSCurveConnector(
                                        fromSlot = NodeHorizontalSlot.CENTER,
                                        toSlot = roadmapSlotForIndex(1),
                                        fromColor = BvbSignalYellow,
                                        toColor = AmgPetronasTeal,
                                        isHighlighted = true
                                    )
                                }
                            }
                        }

                        if (stageGroup.stageIndex == 2 && stageFilter == -2) {
                            item(key = "gate_situations_sim") {
                                Column {
                                    RoadmapMilestoneGate(
                                        badge = "Gegenpressing · ${controlState.conversations.size} Debatten · ${controlState.mediations.size} Mediationen",
                                        title = "Situations-Simulator & Taktiktafel",
                                        subtitle = "Unterbrechungen im Meeting kontern, Sprachmittlung & Coaching-Kommandos.",
                                        ctaLabel = "Match",
                                        accent = AmgPetronasTeal,
                                        icon = Icons.Rounded.RecordVoiceOver,
                                        onClick = onOpenSituationsSimulator,
                                        testTag = "roadmap_situations_gate"
                                    )
                                    RoadmapSCurveConnector(
                                        fromSlot = NodeHorizontalSlot.CENTER,
                                        toSlot = roadmapSlotForIndex(12),
                                        fromColor = AmgPetronasTeal,
                                        toColor = EmeraldSage,
                                        isHighlighted = true
                                    )
                                }
                            }
                        }

                        if (stageGroup.stageIndex == 4 && stageFilter == -2) {
                            item(key = "gate_cat_calibration") {
                                val hasMistakes = pareto.activeMistakesCount > 0
                                Column {
                                    RoadmapMilestoneGate(
                                        badge = if (hasMistakes) "Boxenstopp · ${pareto.activeMistakesCount} Fehler offen" else "Telemetrie · L1–L4 Scan",
                                        title = if (hasMistakes) "Pit-Stop Fehler-Reparatur" else "Adaptive C1 Diagnose-Schranke",
                                        subtitle = if (hasMistakes) "Gespeicherte Stolpersteine gezielt beheben."
                                        else "Prüft Präzision über alle 70+ C1-Items.",
                                        ctaLabel = if (hasMistakes) "Fix" else "Scan",
                                        accent = if (hasMistakes) CoralCrimson else EmeraldSage,
                                        icon = Icons.Rounded.Radar,
                                        onClick = { if (hasMistakes) onOpenMistakeRepair() else onOpenCalibration() },
                                        testTag = "roadmap_checkpoint_gate"
                                    )
                                    RoadmapSCurveConnector(
                                        fromSlot = NodeHorizontalSlot.CENTER,
                                        toSlot = roadmapSlotForIndex(20),
                                        fromColor = EmeraldSage,
                                        toColor = BvbSignalYellow,
                                        isHighlighted = controlState.calibrated
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    RoadmapMilestoneGate(
                        badge = "Zielgerade · Goethe C1 & Stil-Feinschliff",
                        title = "AI Stil-Upgrade & Sokratischer Coach",
                        subtitle = "Verwandelt Entwürfe in präzises C1-Deutsch & baut maßgeschneiderte KI-Drills.",
                        ctaLabel = "AI Lab",
                        accent = BvbSignalYellow,
                        isDarkHero = true,
                        icon = Icons.Rounded.WorkspacePremium,
                        onClick = onOpenAiStudio,
                        testTag = "roadmap_summit_ai_gate"
                    )
                }
            } else {
                // Clean, Zero-Dashboard Textbook Index (just filter chips + 28 unit cards)
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            "ALL" to "Alle 28 Units",
                            "ASPEKTE" to "Aspekte neu C1",
                            "SICHER" to "Sicher! C1",
                            "GOETHE" to "Goethe / Telc C1"
                        ).forEach { (key, label) ->
                            FilterChipPill(
                                label = label,
                                selected = bookFilter == key,
                                accent = BvbSignalYellow,
                                onClick = { bookFilter = key }
                            )
                        }
                    }
                }

                val filteredOverviewUnits = controlState.prioritizedRepoUnits.filter { item ->
                    val ref = item.unit.bookBridge?.bookReference.orEmpty()
                    when (bookFilter) {
                        "ASPEKTE" -> ref.contains("Aspekte", ignoreCase = true)
                        "SICHER" -> ref.contains("Sicher", ignoreCase = true)
                        "GOETHE" -> ref.contains("Goethe", ignoreCase = true) || ref.contains("Telc", ignoreCase = true)
                        else -> true
                    }
                }

                items(filteredOverviewUnits, key = { "ov_repo_${it.unit.id}" }) { uItem ->
                    BlueprintOverviewRepoUnitCard(
                        item = uItem,
                        activeContextTrack = activeTrack,
                        onOpenUnitStudio = { onOpenRepoUnit(uItem.unit.id) },
                        onOpenUnitDrill = { onStartPillarDrill(uItem.unit.mappedPillar) }
                    )
                }
            }

            item { Spacer(Modifier.height(12.dp)) }
        }
    }
}

/**
 * Single Unified Cockpit Instrument inspired by the Porsche 911 (992) Central Sport Chrono Tachometer,
 * Mercedes-AMG Petronas Carbon Telemetry, and BVB 09 Signal Yellow (#FDE100).
 * Replaces 3 separate dashboard boxes with 1 clean, sculptural launch pod.
 */
@Composable
private fun SportChronoStintPod(
    artworkRes: Int,
    functionalPercent: Int,
    examPercent: Int,
    cefrLevel: String,
    directive: ControllerDirective,
    onExecuteDirective: () -> Unit,
    onOpenVisualLab: () -> Unit,
    onOpenSituations: () -> Unit,
    modifier: Modifier = Modifier
) {
    val animFunc by animateFloatAsState(
        targetValue = (functionalPercent / 100f).coerceIn(0.06f, 1f),
        animationSpec = tween(750, easing = FastOutSlowInEasing),
        label = "tachFunc"
    )
    val animExam by animateFloatAsState(
        targetValue = (examPercent / 100f).coerceIn(0.06f, 1f),
        animationSpec = tween(850, easing = FastOutSlowInEasing),
        label = "tachExam"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("dual_readiness_split_card"),
        shape = RoundedCornerShape(24.dp),
        color = DeepInk,
        border = BorderStroke(1.2.dp, BvbSignalYellow.copy(alpha = 0.48f))
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(id = artworkRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                alpha = 0.34f,
                modifier = Modifier.matchParentSize()
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                DeepInk.copy(alpha = 0.96f),
                                DeepInk.copy(alpha = 0.86f),
                                DeepInk.copy(alpha = 0.72f)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Porsche 911 Sport Chrono Central Tachometer Dial
                    Box(
                        modifier = Modifier.size(106.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val center = Offset(size.width / 2f, size.height / 2f)
                            val outerPad = 5.dp.toPx()
                            val innerPad = 14.dp.toPx()

                            // Subtle carbon dial face
                            drawCircle(
                                color = Color(0xFF10141C).copy(alpha = 0.92f),
                                radius = size.minDimension / 2f - 2.dp.toPx(),
                                center = center
                            )

                            // 911 Tachometer Tick Marks (150° to 390°, 240° sweep)
                            val tickCount = 16
                            for (i in 0..tickCount) {
                                val deg = 150f + (240f * i / tickCount)
                                val rad = (deg * PI / 180.0).toFloat()
                                val rOuter = size.minDimension / 2f - 3.dp.toPx()
                                val rInner = rOuter - if (i % 4 == 0) 6.dp.toPx() else 3.dp.toPx()
                                val tickColor = if (i >= 13) PorscheRedline else Color.White.copy(alpha = 0.32f)
                                drawLine(
                                    color = tickColor,
                                    start = Offset(center.x + rInner * cos(rad), center.y + rInner * sin(rad)),
                                    end = Offset(center.x + rOuter * cos(rad), center.y + rOuter * sin(rad)),
                                    strokeWidth = if (i % 4 == 0) 2.dp.toPx() else 1.dp.toPx(),
                                    cap = StrokeCap.Round
                                )
                            }

                            // Outer AMG Petronas Cyan Ring = Goethe Exam Readiness
                            val outerSize = Size(size.width - outerPad * 2, size.height - outerPad * 2)
                            drawArc(
                                color = AmgPetronasTeal.copy(alpha = 0.16f),
                                startAngle = 150f,
                                sweepAngle = 240f,
                                useCenter = false,
                                topLeft = Offset(outerPad, outerPad),
                                size = outerSize,
                                style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
                            )
                            drawArc(
                                color = AmgPetronasTeal,
                                startAngle = 150f,
                                sweepAngle = 240f * animExam,
                                useCenter = false,
                                topLeft = Offset(outerPad, outerPad),
                                size = outerSize,
                                style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
                            )

                            // Inner BVB 09 / 911 Signal Yellow Arc = Live Functional C1 RPM
                            val innerSize = Size(size.width - innerPad * 2, size.height - innerPad * 2)
                            drawArc(
                                color = BvbSignalYellow.copy(alpha = 0.16f),
                                startAngle = 150f,
                                sweepAngle = 240f,
                                useCenter = false,
                                topLeft = Offset(innerPad, innerPad),
                                size = innerSize,
                                style = Stroke(width = 6.5.dp.toPx(), cap = StrokeCap.Round)
                            )
                            drawArc(
                                color = BvbSignalYellow,
                                startAngle = 150f,
                                sweepAngle = 240f * animFunc,
                                useCenter = false,
                                topLeft = Offset(innerPad, innerPad),
                                size = innerSize,
                                style = Stroke(width = 6.5.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = cefrLevel,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = AmgPetronasTeal
                            )
                            Text(
                                text = "$functionalPercent%",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = BvbSignalYellow
                            )
                            Text(
                                text = "PRÜF $examPercent%",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.72f)
                            )
                        }
                    }

                    // Right Stint Focus Summary
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = BvbSignalYellow.copy(alpha = 0.20f)
                            ) {
                                Text(
                                    text = "NÄCHSTER STINT",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = BvbSignalYellow,
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                )
                            }
                            Text(
                                text = directive.roiBadgeText,
                                style = MaterialTheme.typography.labelSmall,
                                color = AmgPetronasTeal
                            )
                        }
                        Text(
                            text = directive.headline,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = directive.rationale,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.76f),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Primary BVB Signal Yellow Launch Button + 2 Compact Mode Triggers
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onExecuteDirective,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BvbSignalYellow),
                        modifier = Modifier
                            .weight(1.4f)
                            .height(48.dp)
                            .testTag("execute_directive_button")
                    ) {
                        Icon(Icons.Rounded.Bolt, contentDescription = null, tint = DeepInk, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Start Stint (${directive.estimatedMinutes}m)",
                            color = DeepInk,
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.weight(1f))
                        Icon(Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = null, tint = DeepInk)
                    }

                    Surface(
                        modifier = Modifier
                            .height(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { onOpenVisualLab() },
                        shape = RoundedCornerShape(14.dp),
                        color = AmgPetronasTeal.copy(alpha = 0.16f),
                        border = BorderStroke(1.dp, AmgPetronasTeal.copy(alpha = 0.6f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 11.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(Icons.Rounded.AutoAwesome, contentDescription = "Visual Lab", tint = AmgPetronasTeal, modifier = Modifier.size(16.dp))
                            Text("Labor", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = AmgPetronasTeal)
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("open_situations_hero_button")
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { onOpenSituations() },
                        shape = RoundedCornerShape(14.dp),
                        color = Color.White.copy(alpha = 0.08f),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.28f))
                    ) {
                        Box(
                            modifier = Modifier.padding(horizontal = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Rounded.RecordVoiceOver,
                                contentDescription = "Situations-Simulator",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun stageAccentColor(stageIndex: Int): Color = when (stageIndex) {
    0 -> BvbSignalYellow
    1 -> AmgPetronasTeal
    2 -> PorscheRedline
    3 -> EmeraldSage
    4 -> AmgPetronasTeal
    5 -> AmberGold
    else -> BvbSignalYellow
}

private fun stageSubtitleDe(stageIndex: Int, track: ContextTrack): String = when (stageIndex) {
    0 -> "Standortbestimmung & Fokus (${track.shortLabel})"
    1 -> "Satzklammer, Kasus-Präzision & flüssiger Anschluss"
    2 -> "Gegenwind kontern, Konzessivsätze & Kausalketten"
    3 -> "Registerwechsel, Konjunktiv I & souverän verhandeln"
    4 -> "Nominalstil, Funktionsverbgefüge & schnelle Hörtexte"
    5 -> "Spontane Unterbrechungen, Mediation & Prüfstands-Berichte"
    else -> "Goethe C1 Feinschliff & Transfer auf Profiniveau"
}

@Composable
private fun FilterChipPill(
    label: String,
    selected: Boolean,
    accent: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(99.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(99.dp),
        color = if (selected) accent else MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, if (selected) accent else MaterialTheme.colorScheme.outline)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = if (selected) contentColorOnAccent(accent) else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
        )
    }
}

@Composable
private fun ModeTogglePill(
    label: String,
    icon: @Composable () -> Unit,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val fg = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
    Surface(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            1.dp,
            if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.padding(end = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.runtime.CompositionLocalProvider(
                    androidx.compose.material3.LocalContentColor provides fg
                ) {
                    icon()
                }
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = fg,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
