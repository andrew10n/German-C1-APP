package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesomeMotion
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.Style
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.material.icons.rounded.WarningAmber
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.domain.engine.MasterControlState
import com.example.domain.model.C1Pillar
import com.example.domain.model.C1VocabCard
import com.example.ui.components.VisualConceptInfographic
import com.example.ui.components.VisualMemoryBatteryBar
import com.example.ui.components.pillarAccentColor
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CobaltBright
import com.example.ui.theme.CoralCrimson
import com.example.ui.theme.EmeraldSage
import com.example.ui.theme.contentColorOnAccent
import kotlin.math.roundToInt

private enum class VocabTabMode {
    ACTIVE_TRAINER,
    LEXIKON_BROWSER
}

@Composable
fun SrsVisualTrainerScreen(
    controlState: MasterControlState,
    onRateCard: (C1VocabCard, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var mode by remember { mutableStateOf(VocabTabMode.ACTIVE_TRAINER) }
    var selectedPillarFilter by remember { mutableStateOf<C1Pillar?>(null) }

    val filteredQueue = remember(controlState.orderedSrsCards, selectedPillarFilter) {
        if (selectedPillarFilter == null) {
            controlState.orderedSrsCards
        } else {
            controlState.orderedSrsCards.filter { it.card.pillar == selectedPillarFilter }
                .ifEmpty { controlState.orderedSrsCards }
        }
    }

    var cardIndex by remember(selectedPillarFilter) { mutableIntStateOf(0) }
    var flipped by remember(cardIndex, selectedPillarFilter) { mutableStateOf(false) }
    var reviewedCount by remember { mutableIntStateOf(0) }

    val safeIndex = if (filteredQueue.isNotEmpty()) cardIndex % filteredQueue.size else 0
    val current = filteredQueue.getOrNull(safeIndex)

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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "80/20 FSRS VOCAB TRAINER",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "C1 Vocab & NVV",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                Surface(
                    shape = RoundedCornerShape(99.dp),
                    color = AmberGold.copy(alpha = 0.14f)
                ) {
                    Text(
                        text = "${controlState.dueSrsCount} Due · $reviewedCount Done",
                        style = MaterialTheme.typography.labelMedium,
                        color = AmberGold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            // Mode Toggle: Active Trainer vs Lexikon Browser
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                VocabSubModePill(
                    label = "Active Recall",
                    selected = mode == VocabTabMode.ACTIVE_TRAINER,
                    onClick = { mode = VocabTabMode.ACTIVE_TRAINER },
                    icon = {
                        Icon(
                            Icons.Rounded.Style,
                            contentDescription = null,
                            tint = if (mode == VocabTabMode.ACTIVE_TRAINER) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    modifier = Modifier.weight(1f).testTag("vocab_mode_train")
                )
                VocabSubModePill(
                    label = "Lexikon (${filteredQueue.size})",
                    selected = mode == VocabTabMode.LEXIKON_BROWSER,
                    onClick = { mode = VocabTabMode.LEXIKON_BROWSER },
                    icon = {
                        Icon(
                            Icons.Rounded.MenuBook,
                            contentDescription = null,
                            tint = if (mode == VocabTabMode.LEXIKON_BROWSER) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    modifier = Modifier.weight(1f).testTag("vocab_mode_browse")
                )
            }

            // Unit / Pillar Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChipPill(
                    label = "All 80/20 Priority",
                    selected = selectedPillarFilter == null,
                    accent = AmberGold,
                    onClick = { selectedPillarFilter = null }
                )
                C1Pillar.entries.forEach { pillar ->
                    FilterChipPill(
                        label = pillar.shortLabel,
                        selected = selectedPillarFilter == pillar,
                        accent = pillarAccentColor(pillar),
                        onClick = { selectedPillarFilter = pillar }
                    )
                }
            }

            if (mode == VocabTabMode.ACTIVE_TRAINER && current != null) {
                val card = current.card
                val accent = pillarAccentColor(card.pillar)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = accent.copy(alpha = 0.14f)
                    ) {
                        Text(
                            text = "${card.pillar.shortLabel.uppercase()} · ${current.controllerTag.uppercase()}",
                            style = MaterialTheme.typography.labelSmall,
                            color = accent,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "${safeIndex + 1}/${filteredQueue.size}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        VisualMemoryBatteryBar(
                            stabilityDays = current.state?.stabilityDays ?: 0.5f,
                            accent = accent
                        )
                    }
                }

                // Visual Flip Flashcard
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("srs_flashcard")
                        .clip(RoundedCornerShape(24.dp))
                        .clickable { flipped = !flipped },
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.5.dp, accent.copy(alpha = 0.45f))
                ) {
                    AnimatedContent(
                        targetState = flipped,
                        label = "flashcardFlip"
                    ) { isBack ->
                        if (!isBack) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(22.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant
                                    ) {
                                        Text(
                                            card.register,
                                            style = MaterialTheme.typography.labelSmall,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer
                                    ) {
                                        Text(
                                            card.grammarTag,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = card.expression,
                                    style = MaterialTheme.typography.displaySmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "Visual Anchor: ${card.visualHint}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = accent,
                                    textAlign = TextAlign.Center
                                )

                                VisualConceptInfographic(
                                    diagramType = card.diagramType,
                                    formulaBefore = card.grammarTag,
                                    formulaAfter = card.expression,
                                    highlightRule = card.register
                                )

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        Icons.Rounded.TouchApp,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "Tap card to reveal C1 meaning, example & trap",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        } else {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(22.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = card.expression,
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = accent
                                )
                                Text(
                                    text = card.meaningEn,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
                                ) {
                                    Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
                                        Text(
                                            text = "KONTEXTBEISPIEL",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            text = "„${card.exampleDe}“",
                                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = CoralCrimson.copy(alpha = 0.08f),
                                    border = BorderStroke(1.dp, CoralCrimson.copy(alpha = 0.35f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Icon(
                                            Icons.Rounded.WarningAmber,
                                            contentDescription = "Trap warning",
                                            tint = CoralCrimson,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            text = card.trapWarning,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // FSRS Rating Buttons
                if (flipped) {
                    Text(
                        text = "Rate Recall (updates SRS stability, auto-pruning & Pillar Mastery)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SrsQualityButton("Again", CoralCrimson, {
                            onRateCard(card, 1)
                            reviewedCount++
                            cardIndex++
                        }, Modifier.weight(1f).testTag("srs_rate_1"))

                        SrsQualityButton("Hard", AmberGold, {
                            onRateCard(card, 2)
                            reviewedCount++
                            cardIndex++
                        }, Modifier.weight(1f).testTag("srs_rate_2"))

                        SrsQualityButton("Good", CobaltBright, {
                            onRateCard(card, 3)
                            reviewedCount++
                            cardIndex++
                        }, Modifier.weight(1f).testTag("srs_rate_3"))

                        SrsQualityButton("Easy", EmeraldSage, {
                            onRateCard(card, 4)
                            reviewedCount++
                            cardIndex++
                        }, Modifier.weight(1f).testTag("srs_rate_4"))
                    }
                } else {
                    Button(
                        onClick = { flipped = true },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("srs_reveal_button")
                    ) {
                        Icon(Icons.Rounded.AutoAwesomeMotion, contentDescription = null)
                        Spacer(Modifier.size(8.dp))
                        Text("Reveal Visual Context & Trap")
                    }
                }
            } else {
                // Lexikon Browser List (Tap any expression to jump right into practicing it!)
                filteredQueue.forEachIndexed { idx, item ->
                    val accent = pillarAccentColor(item.card.pillar)
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .clickable {
                                cardIndex = idx
                                flipped = false
                                mode = VocabTabMode.ACTIVE_TRAINER
                            },
                        shape = RoundedCornerShape(18.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${item.card.pillar.shortLabel.uppercase()} · ${item.card.register}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = accent
                                )
                                VisualMemoryBatteryBar(
                                    stabilityDays = item.state?.stabilityDays ?: 0.5f,
                                    accent = accent
                                )
                            }
                            Text(
                                text = item.card.expression,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = accent.copy(alpha = 0.10f)
                            ) {
                                Text(
                                    text = "⚡ ${item.card.visualHint}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = accent,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun VocabSubModePill(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            icon()
            Spacer(Modifier.size(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
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
            .height(36.dp)
            .clip(RoundedCornerShape(99.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(99.dp),
        color = if (selected) accent else MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, if (selected) accent else MaterialTheme.colorScheme.outline)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                color = if (selected) contentColorOnAccent(accent) else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SrsQualityButton(
    label: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        modifier = modifier.height(48.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = contentColorOnAccent(color)
        )
    }
}
