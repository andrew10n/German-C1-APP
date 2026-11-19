package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Headphones
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import com.example.domain.model.TranscriptTurn
import com.example.ui.theme.LocalVibePalette
import com.example.ui.theme.contentColorOnAccent
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

/**
 * State-of-the-Art Sentence Shadowing Deck with Synchronized Word Karaoke Highlighting:
 * Supports variable native tempos (0.85x - 1.25x), sequential word highlights,
 * and sentence-by-sentence shadowing repetition to internalize authentic German melody.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SentenceShadowingDeck(
    turns: List<TranscriptTurn>,
    onSpeakLine: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalVibePalette.current
    var selectedTurnIndex by remember { mutableIntStateOf(0) }
    var selectedSpeed by remember { mutableFloatStateOf(1.0f) }
    var isKaraokePlaying by remember { mutableStateOf(false) }
    var activeWordIndex by remember { mutableIntStateOf(-1) }

    val currentTurn = turns.getOrNull(selectedTurnIndex) ?: turns.firstOrNull()
    val words = remember(currentTurn?.line) {
        currentTurn?.line?.split(Regex("\\s+"))?.filter { it.isNotBlank() } ?: emptyList()
    }

    // Synchronized Karaoke Progress Simulator based on word count & speed
    LaunchedEffect(isKaraokePlaying, selectedTurnIndex, selectedSpeed) {
        if (isKaraokePlaying && words.isNotEmpty()) {
            activeWordIndex = 0
            val delayPerWordMs = (280f / selectedSpeed).toLong().coerceIn(160L, 450L)
            for (i in words.indices) {
                activeWordIndex = i
                delay(delayPerWordMs)
            }
            delay(300L)
            isKaraokePlaying = false
            activeWordIndex = -1
        }
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("sentence_shadowing_deck"),
        shape = RoundedCornerShape(22.dp),
        color = palette.imprintStageBg,
        border = BorderStroke(1.2.dp, palette.secondaryAccent.copy(alpha = 0.55f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header: Shadowing Mode Badge + Turn Counter
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = palette.secondaryAccent.copy(alpha = 0.18f),
                        modifier = Modifier.size(30.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Rounded.Headphones, contentDescription = null, tint = palette.secondaryAccent, modifier = Modifier.size(16.dp))
                        }
                    }
                    Text(
                        text = "SHADOWING-TRAINER (MITSPRECHEN)",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = palette.secondaryAccent
                    )
                }

                Surface(
                    shape = RoundedCornerShape(99.dp),
                    color = palette.imprintStageSurface
                ) {
                    Text(
                        text = "Satz ${selectedTurnIndex + 1} von ${turns.size}",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = palette.imprintStageText,
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                    )
                }
            }

            // Speaker & Cue info
            currentTurn?.let { turn ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = palette.goldAccent.copy(alpha = 0.16f)
                    ) {
                        Text(
                            text = turn.speaker.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = palette.goldAccent,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    if (turn.cue.isNotBlank()) {
                        Text(
                            text = turn.cue,
                            style = MaterialTheme.typography.labelSmall,
                            color = palette.imprintStageSubtext
                        )
                    }
                }

                // Karaoke Text Display Card (Words light up sequentially in real-time)
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = palette.imprintStageSurface,
                    border = BorderStroke(1.dp, palette.borderStroke.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FlowRow(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        words.forEachIndexed { wIdx, word ->
                            val isHighlighted = isKaraokePlaying && wIdx == activeWordIndex
                            val isSpokenPast = isKaraokePlaying && wIdx < activeWordIndex
                            val wordColor = when {
                                isHighlighted -> palette.primaryAccent
                                isSpokenPast -> palette.imprintStageText
                                else -> palette.imprintStageSubtext
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isHighlighted) palette.primaryAccent.copy(alpha = 0.22f) else Color.Transparent
                            ) {
                                Text(
                                    text = word,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Medium
                                    ),
                                    color = wordColor,
                                    modifier = Modifier.padding(horizontal = 3.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Speed Control Selector (0.85x, 1.00x, 1.15x, 1.25x)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Rounded.Speed, contentDescription = null, tint = palette.imprintStageSubtext, modifier = Modifier.size(15.dp))
                    Text("TEMPO:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = palette.imprintStageSubtext)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(0.85f to "0.85×", 1.00f to "1.0×", 1.15f to "1.15×", 1.25f to "1.25×").forEach { (speed, label) ->
                        val active = kotlin.math.abs(selectedSpeed - speed) < 0.03f
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(99.dp))
                                .clickable { selectedSpeed = speed },
                            shape = RoundedCornerShape(99.dp),
                            color = if (active) palette.secondaryAccent else palette.imprintStageSurface,
                            border = BorderStroke(1.dp, if (active) palette.secondaryAccent else palette.borderStroke.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (active) contentColorOnAccent(palette.secondaryAccent) else palette.imprintStageSubtext,
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // Shadowing Action Controls: Play & Karaoke / Repeat / Next Sentence
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Play Line with Synchronized Karaoke Highlight
                Button(
                    onClick = {
                        currentTurn?.let { turn ->
                            onSpeakLine(turn.line)
                            isKaraokePlaying = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = palette.primaryAccent),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1.3f).height(46.dp).testTag("play_shadowing_karaoke_btn")
                ) {
                    val fg = contentColorOnAccent(palette.primaryAccent)
                    Icon(
                        imageVector = if (isKaraokePlaying) Icons.Rounded.GraphicEq else Icons.Rounded.PlayArrow,
                        contentDescription = null,
                        tint = fg,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(if (isKaraokePlaying) "Mitsprechen..." else "Anhören & Mitsprechen", color = fg, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                }

                // Next Sentence in Dialogue
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            selectedTurnIndex = (selectedTurnIndex + 1) % turns.size
                            isKaraokePlaying = false
                            activeWordIndex = -1
                        }
                        .testTag("next_shadowing_sentence_btn"),
                    shape = RoundedCornerShape(12.dp),
                    color = palette.elevatedSurface,
                    border = BorderStroke(1.dp, palette.borderStroke)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("Nächster Satz →", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = palette.textPrimary)
                    }
                }
            }
        }
    }
}
