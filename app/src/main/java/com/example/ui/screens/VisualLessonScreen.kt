package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.domain.model.C1Pillar
import com.example.domain.model.VisualLessonModule
import com.example.ui.components.VisualConceptInfographic
import com.example.ui.components.pillarAccentColor
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CoralCrimson
import com.example.ui.theme.EmeraldSage

@Composable
fun VisualLessonScreen(
    module: VisualLessonModule,
    onSlideViewed: (C1Pillar, Int, Boolean?) -> Unit,
    onLaunchPillarDrill: (C1Pillar) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var slideIndex by remember(module.id) { mutableIntStateOf(0) }
    var checkpointAnswer by remember(module.id, slideIndex) { mutableStateOf<Int?>(null) }
    var revealTransform by remember(module.id, slideIndex) { mutableStateOf(false) }

    val slide = module.slides[slideIndex]
    val accent = pillarAccentColor(module.pillar)

    LaunchedEffect(module.id, slideIndex) {
        onSlideViewed(module.pillar, slideIndex + 1, null)
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 600.dp)
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            // Top bar + Segmented Story Progress
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("lesson_back_button")
                ) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = module.pillar.shortLabel.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = accent
                    )
                    Text(
                        text = module.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                Text(
                    text = "${slideIndex + 1}/${module.slides.size}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(8.dp))

            // Imprint/Headway Story Progress Segments
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                module.slides.forEachIndexed { idx, _ ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(5.dp)
                            .clip(RoundedCornerShape(99.dp))
                            .background(
                                if (idx <= slideIndex) accent else MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)
                            )
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            // Slide Content Body
            AnimatedContent(
                targetState = slide,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                modifier = Modifier.weight(1f),
                label = "visualSlideTransition"
            ) { currentSlide ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (currentSlide.isAiSynthesized) {
                        Surface(
                            shape = RoundedCornerShape(99.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.45f))
                        ) {
                            Text(
                                text = "AI SYNTHESIZED · 80/20 CUSTOM SLIDE",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                    Text(
                        text = currentSlide.headline,
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = currentSlide.subheadline,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    VisualTransformStage(
                        pillar = currentSlide.pillar,
                        before = currentSlide.formulaBefore,
                        after = currentSlide.formulaAfter,
                        rule = currentSlide.highlightRule,
                        revealed = revealTransform,
                        onToggleReveal = { revealTransform = !revealTransform }
                    )

                    // Bespoke Visual Infographic
                    VisualConceptInfographic(
                        diagramType = currentSlide.diagramType,
                        formulaBefore = currentSlide.formulaBefore,
                        formulaAfter = currentSlide.formulaAfter,
                        highlightRule = currentSlide.highlightRule
                    )

                    // Key Insight Callout (Headway style)
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = accent.copy(alpha = 0.10f),
                        border = BorderStroke(1.dp, accent.copy(alpha = 0.35f))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                Icons.Rounded.Lightbulb,
                                contentDescription = "Key Insight",
                                tint = accent,
                                modifier = Modifier.size(22.dp)
                            )
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "KEY INSIGHT",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = accent
                                )
                                Text(
                                    text = currentSlide.keyInsight,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    // C1 Micro Example Card
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "C1 BILDUNGSSPRACHE IN KONTEXT",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "„${currentSlide.microExampleDe}“",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = currentSlide.microExampleGloss,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Stolperstein Trap Card
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = AmberGold.copy(alpha = 0.10f),
                        border = BorderStroke(1.dp, AmberGold.copy(alpha = 0.35f))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                Icons.Rounded.WarningAmber,
                                contentDescription = "Common Trap",
                                tint = AmberGold,
                                modifier = Modifier.size(20.dp)
                            )
                            Column {
                                Text(
                                    text = "STOLPERSTEIN (B2 → C1 TRAP)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = AmberGold
                                )
                                Text(
                                    text = currentSlide.commonTrap,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    // Embedded Visual Micro-Checkpoint
                    currentSlide.checkpoint?.let { cp ->
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.45f))
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "VISUAL CHECKPOINT",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = cp.prompt,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                cp.options.forEachIndexed { idx, opt ->
                                    val chosen = checkpointAnswer == idx
                                    val isCorrect = idx == cp.correctIndex
                                    val bgColor = when {
                                        checkpointAnswer == null -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                                        isCorrect -> EmeraldSage.copy(alpha = 0.18f)
                                        chosen -> CoralCrimson.copy(alpha = 0.18f)
                                        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                    }
                                    val borderColor = when {
                                        checkpointAnswer == null -> MaterialTheme.colorScheme.outline
                                        isCorrect -> EmeraldSage
                                        chosen -> CoralCrimson
                                        else -> MaterialTheme.colorScheme.outline
                                    }
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .clickable(enabled = checkpointAnswer == null) {
                                                checkpointAnswer = idx
                                                onSlideViewed(module.pillar, slideIndex + 1, idx == cp.correctIndex)
                                            },
                                        shape = RoundedCornerShape(12.dp),
                                        color = bgColor,
                                        border = BorderStroke(1.dp, borderColor)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = if (checkpointAnswer == null) accent.copy(alpha = 0.12f)
                                                else if (isCorrect) EmeraldSage.copy(alpha = 0.24f)
                                                else if (chosen) CoralCrimson.copy(alpha = 0.24f)
                                                else accent.copy(alpha = 0.08f),
                                                modifier = Modifier.size(30.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Text(
                                                        text = ('A'.code + idx).toChar().toString(),
                                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                                        color = if (isCorrect && checkpointAnswer != null) EmeraldSage
                                                        else if (chosen && checkpointAnswer != null) CoralCrimson
                                                        else accent
                                                    )
                                                }
                                            }
                                            Spacer(Modifier.width(8.dp))
                                            Text(
                                                text = opt,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.weight(1f)
                                            )
                                            if (checkpointAnswer != null && isCorrect) {
                                                Icon(
                                                    Icons.Rounded.CheckCircle,
                                                    contentDescription = "Correct",
                                                    tint = EmeraldSage,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                                if (checkpointAnswer != null) {
                                    Text(
                                        text = cp.feedback,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = EmeraldSage
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                }
            }

            // Bottom Navigation Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = { if (slideIndex > 0) slideIndex-- else onBack() },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("lesson_prev_button")
                ) {
                    Text(if (slideIndex > 0) "Zurück" else "Schließen")
                }

                Button(
                    onClick = {
                        if (slideIndex < module.slides.lastIndex) {
                            slideIndex++
                        } else {
                            onLaunchPillarDrill(module.pillar)
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1.4f)
                        .height(50.dp)
                        .testTag("lesson_next_button")
                ) {
                    if (slideIndex < module.slides.lastIndex) {
                        Text("Nächste Visualisierung")
                        Spacer(Modifier.width(6.dp))
                        Icon(Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = null)
                    } else {
                        Icon(Icons.Rounded.PlayArrow, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("Test ${module.pillar.shortLabel}")
                    }
                }
            }
        }
    }
}

@Composable
private fun VisualTransformStage(
    pillar: C1Pillar,
    before: String,
    after: String,
    rule: String,
    revealed: Boolean,
    onToggleReveal: () -> Unit
) {
    val illustrationRes = when (pillar) {
        C1Pillar.NOMINALISIERUNG -> R.drawable.ctx_ev_engineering
        C1Pillar.KONJUNKTIV -> R.drawable.ctx_berlin_discourse
        C1Pillar.SATZGEFUEGE -> R.drawable.img_amg_911_cockpit
        C1Pillar.NOMEN_VERB -> R.drawable.ctx_social_swabia
        C1Pillar.MODALPARTIKELN -> R.drawable.img_visual_swabia_roundtable
        C1Pillar.RHETORIK -> R.drawable.ctx_hero_studio
    }
    val accent = pillarAccentColor(pillar)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(210.dp)
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onToggleReveal),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.2.dp, accent.copy(alpha = 0.65f))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = illustrationRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Black.copy(alpha = 0.08f),
                                Color.Black.copy(alpha = 0.72f),
                                Color.Black.copy(alpha = 0.94f)
                            )
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(99.dp),
                        color = accent
                    ) {
                        Text(
                            text = if (revealed) "C1 FORM" else "B2 BASIS",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(99.dp),
                        color = Color.Black.copy(alpha = 0.55f),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f))
                    ) {
                        Text(
                            text = "Tippen",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                        )
                    }
                }
                AnimatedContent(
                    targetState = revealed,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "visualTransformReveal"
                ) { showC1 ->
                    Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                        Text(
                            text = if (showC1) after else before,
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = accent.copy(alpha = if (showC1) 0.95f else 0.28f),
                            border = BorderStroke(1.dp, accent)
                        ) {
                            Text(
                                text = rule,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (showC1) Color.White else Color(0xFFE8EEF7),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
