package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Route
import androidx.compose.material.icons.rounded.Speed
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.engine.MasterControlState
import com.example.domain.model.C1Pillar
import com.example.domain.model.PrioritizedRepoUnit
import com.example.ui.components.RoadmapMilestoneGate
import com.example.ui.components.RoadmapRepoUnitStation
import com.example.ui.components.RoadmapSCurveConnector
import com.example.ui.components.RoadmapStageHeader
import com.example.ui.components.pillarAccentColor
import com.example.ui.components.roadmapSlotForIndex
import com.example.ui.theme.LocalVibePalette
import com.example.ui.theme.contentColorOnAccent
import kotlin.math.roundToInt

private enum class RoadmapFilterMode(val label: String) {
    ALL("Alle 28"),
    VITAL_80_20("80/20 Vital"),
    ACTIVE_OR_DONE("Aktiv / Fertig")
}

/**
 * Tab 2 — Visual C1 learning journey:
 * A winding 7-stage path with clear unit cards, filters, progress, and
 * one-tap access to the next visual lesson or targeted practice.
 */
@Composable
fun RoadmapScreen(
    controlState: MasterControlState,
    onOpenUnitStudio: (String) -> Unit,
    onStartUnitDrill: (C1Pillar) -> Unit,
    onPinPillarToEngine: (C1Pillar) -> Unit,
    onOpenCalibration: () -> Unit,
    onOpenAiStudio: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalVibePalette.current
    var filterMode by remember { mutableStateOf(RoadmapFilterMode.ALL) }
    var selectedStageFilter by remember { mutableIntStateOf(-1) }
    var expandedUnitId by remember {
        mutableStateOf(
            controlState.prioritizedRepoUnits.firstOrNull { it.isParetoVital20 }?.unit?.id
                ?: controlState.prioritizedRepoUnits.firstOrNull()?.unit?.id
        )
    }

    val stageByNumber = remember(controlState.stagesRoadmap) {
        controlState.stagesRoadmap.associateBy { it.stageIndex }
    }
    val unresolvedByPillar = remember(controlState.unresolvedMistakes) {
        controlState.unresolvedMistakes.groupingBy { it.pillarId }.eachCount()
    }
    val pinnedPillar = if (controlState.commanderConfig.commanderEnabled) {
        controlState.commanderConfig.pinnedFocusPillar
    } else null

    val orderedUnits = remember(controlState.prioritizedRepoUnits) {
        controlState.prioritizedRepoUnits.sortedWith(
            compareBy<PrioritizedRepoUnit> { it.unit.stage }.thenByDescending { it.roiScore }
        )
    }

    val filteredUnits = remember(orderedUnits, filterMode, selectedStageFilter) {
        orderedUnits.filter { item ->
            val modeMatch = when (filterMode) {
                RoadmapFilterMode.ALL -> true
                RoadmapFilterMode.VITAL_80_20 -> item.isParetoVital20
                RoadmapFilterMode.ACTIVE_OR_DONE -> item.completedStepsCount > 0 || item.masteryPercent >= 78
            }
            val stageMatch = selectedStageFilter == -1 || item.unit.stage == selectedStageFilter
            modeMatch && stageMatch
        }
    }

    val completedUnitsCount = orderedUnits.count { it.masteryPercent >= 80 || it.completedStepsCount >= 4 }
    val totalPhasesDone = orderedUnits.sumOf { it.completedStepsCount }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(palette.canvasBg),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 600.dp)
                .testTag("roadmap_list"),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 1. Visual 7-stage learning journey
            item(key = "circuit_hero") {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = palette.imprintStageBg,
                    border = BorderStroke(1.2.dp, palette.primaryAccent.copy(alpha = 0.55f)),
                    modifier = Modifier.fillMaxWidth()
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
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Rounded.Route, contentDescription = null, tint = palette.primaryAccent)
                                Text(
                                    text = "C1 LERNREISE",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = palette.primaryAccent
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(99.dp),
                                color = palette.secondaryAccent.copy(alpha = 0.2f),
                                border = BorderStroke(1.dp, palette.secondaryAccent.copy(alpha = 0.5f))
                            ) {
                                Text(
                                    text = "$completedUnitsCount/${orderedUnits.size} Units · ${controlState.selectedContextTrack.shortLabel}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = palette.secondaryAccent,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        // Custom visual path for learning stages 0..6
                        SevenStageCircuitSpline(
                            stagesMastery = (0..6).map { stage ->
                                val stageUnits = orderedUnits.filter { it.unit.stage == stage }
                                if (stageUnits.isEmpty()) 0 else stageUnits.map { it.masteryPercent }.average().roundToInt()
                            },
                            selectedStage = selectedStageFilter
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Aspekte neu C1 & Sicher! C1 · $totalPhasesDone Studio-Phasen erledigt",
                                style = MaterialTheme.typography.labelSmall,
                                color = palette.imprintStageSubtext
                            )
                            if (pinnedPillar != null) {
                                Surface(
                                    shape = RoundedCornerShape(99.dp),
                                    color = palette.primaryAccent
                                ) {
                                    Text(
                                        text = "⚡ Fokus: ${pinnedPillar.shortLabel}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = contentColorOnAccent(palette.primaryAccent),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 2. Filter Bar (Mode + Stage 0..6 pills)
            item(key = "filter_bar") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RoadmapFilterMode.entries.forEach { mode ->
                            val selected = filterMode == mode
                            Surface(
                                modifier = Modifier
                                    .testTag("roadmap_filter_${mode.name}")
                                    .clip(RoundedCornerShape(99.dp))
                                    .clickable { filterMode = mode },
                                shape = RoundedCornerShape(99.dp),
                                color = if (selected) palette.primaryAccent else palette.cardSurface,
                                border = BorderStroke(
                                    1.dp,
                                    if (selected) palette.primaryAccent else palette.borderStroke
                                )
                            ) {
                                Text(
                                    text = mode.label,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (selected) contentColorOnAccent(palette.primaryAccent) else palette.textPrimary,
                                    modifier = Modifier.padding(horizontal = 13.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val allStagesSelected = selectedStageFilter == -1
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(99.dp))
                                .clickable { selectedStageFilter = -1 },
                            shape = RoundedCornerShape(99.dp),
                            color = if (allStagesSelected) palette.secondaryAccent else palette.cardSurface,
                            border = BorderStroke(
                                1.dp,
                                if (allStagesSelected) palette.secondaryAccent else palette.borderStroke
                            )
                        ) {
                            Text(
                                text = "Alle Stufen",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (allStagesSelected) contentColorOnAccent(palette.secondaryAccent) else palette.textSecondary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                        (0..6).forEach { stageNum ->
                            val selected = selectedStageFilter == stageNum
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(99.dp))
                                    .clickable {
                                        selectedStageFilter = if (selected) -1 else stageNum
                                    },
                                shape = RoundedCornerShape(99.dp),
                                color = if (selected) palette.secondaryAccent else palette.cardSurface,
                                border = BorderStroke(
                                    1.dp,
                                    if (selected) palette.secondaryAccent else palette.borderStroke
                                )
                            ) {
                                Text(
                                    text = "S$stageNum",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (selected) contentColorOnAccent(palette.secondaryAccent) else palette.textSecondary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 3. Winding S-Curve Roadmap Stations grouped visually by Stage
            itemsIndexed(
                items = filteredUnits,
                key = { _, item -> item.unit.id }
            ) { idx, item ->
                val prevItem = filteredUnits.getOrNull(idx - 1)
                val showStageDivider = prevItem == null || prevItem.unit.stage != item.unit.stage

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (showStageDivider) {
                        val stageGroup = stageByNumber[item.unit.stage]
                        val stageAccent = pillarAccentColor(item.unit.mappedPillar)
                        val stageAvg = stageGroup?.averageMasteryPercent ?: run {
                            val stageUnits = orderedUnits.filter { it.unit.stage == item.unit.stage }
                            if (stageUnits.isEmpty()) 0 else stageUnits.map { it.masteryPercent }.average().roundToInt()
                        }

                        Spacer(Modifier.height(4.dp))
                        RoadmapStageHeader(
                            stageLabel = "STUFE ${item.unit.stage}",
                            title = stageGroup?.stageTitle ?: "C1 Ausbau",
                            subtitle = "${stageGroup?.units?.size ?: 4} Einheiten · ${controlState.selectedContextTrack.shortLabel}-Track",
                            accent = stageAccent,
                            masteryBadge = "$stageAvg% Ø"
                        )
                    } else if (prevItem != null) {
                        RoadmapSCurveConnector(
                            fromSlot = roadmapSlotForIndex(idx - 1),
                            toSlot = roadmapSlotForIndex(idx),
                            fromColor = pillarAccentColor(prevItem.unit.mappedPillar),
                            toColor = pillarAccentColor(item.unit.mappedPillar),
                            isHighlighted = prevItem.isParetoVital20 || item.isParetoVital20
                        )
                    }

                    RoadmapRepoUnitStation(
                        item = item,
                        unitOrderNumber = idx + 1,
                        activeContextTrack = controlState.selectedContextTrack,
                        slot = roadmapSlotForIndex(idx),
                        isExpanded = expandedUnitId == item.unit.id,
                        unresolvedMistakesCount = unresolvedByPillar[item.unit.mappedPillar.id] ?: 0,
                        isPinnedInCommander = pinnedPillar == item.unit.mappedPillar,
                        onToggleExpand = {
                            expandedUnitId = if (expandedUnitId == item.unit.id) null else item.unit.id
                        },
                        onOpenUnitStudio = { onOpenUnitStudio(item.unit.id) },
                        onStartUnitDrill = { onStartUnitDrill(item.unit.mappedPillar) },
                        onOpenSituationsLab = { onOpenUnitStudio(item.unit.id) },
                        onPinToEngine = { onPinPillarToEngine(item.unit.mappedPillar) }
                    )
                }
            }

            // 4. Summit Checkpoint Gates (CAT Probe & AI Coach Studio)
            item(key = "milestone_gates") {
                Spacer(Modifier.height(6.dp))
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    RoadmapMilestoneGate(
                        badge = "3PL-IRT CHECKPOINT",
                        title = "6-Säulen CAT Diagnose-Sweep",
                        subtitle = "Kalibriert Latenz (<12.5s) und Schärfe auf allen 6 Säulen.",
                        ctaLabel = "Probe",
                        accent = palette.secondaryAccent,
                        icon = Icons.Rounded.Speed,
                        onClick = onOpenCalibration,
                        testTag = "roadmap_cat_gate"
                    )
                    RoadmapMilestoneGate(
                        badge = "GEMINI C1 STUDIO",
                        title = "Schreib- & Konversations-Labor",
                        subtitle = "Prüfe freie C1-Texte oder trainiere mit dem sokratischen Coach.",
                        ctaLabel = "Öffnen",
                        accent = palette.primaryAccent,
                        isDarkHero = true,
                        icon = Icons.Rounded.AutoAwesome,
                        onClick = onOpenAiStudio,
                        testTag = "roadmap_ai_gate"
                    )
                }
                Spacer(Modifier.height(14.dp))
            }
        }
    }
}

@Composable
private fun SevenStageCircuitSpline(
    stagesMastery: List<Int>,
    selectedStage: Int
) {
    val palette = LocalVibePalette.current
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        val count = stagesMastery.size.coerceAtLeast(2)
        val padX = 20.dp.toPx()
        val usableW = (size.width - padX * 2).coerceAtLeast(1f)
        val points = stagesMastery.mapIndexed { idx, _ ->
            val x = padX + (usableW * idx / (count - 1).toFloat())
            val yWave = if (idx % 2 == 0) size.height * 0.34f else size.height * 0.68f
            Offset(x, yWave)
        }

        val path = Path().apply {
            moveTo(points.first().x, points.first().y)
            for (i in 1 until points.size) {
                val p0 = points[i - 1]
                val p1 = points[i]
                val midX = (p0.x + p1.x) / 2f
                cubicTo(midX, p0.y, midX, p1.y, p1.x, p1.y)
            }
        }

        drawPath(
            path = path,
            brush = Brush.horizontalGradient(
                listOf(palette.secondaryAccent, palette.primaryAccent, palette.goldAccent)
            ),
            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
        )

        points.forEachIndexed { idx, pt ->
            val pct = stagesMastery[idx]
            val isHighlighted = selectedStage == idx || pct >= 70
            drawCircle(
                color = if (isHighlighted) palette.primaryAccent else palette.imprintStageSurface,
                radius = if (selectedStage == idx) 10.dp.toPx() else 7.5.dp.toPx(),
                center = pt
            )
            drawCircle(
                color = if (pct >= 75) palette.tertiaryAccent else palette.goldAccent,
                radius = 4.dp.toPx(),
                center = pt
            )
        }
    }
}
