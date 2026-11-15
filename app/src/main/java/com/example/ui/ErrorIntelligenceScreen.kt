package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.entity.VocabularyItemEntity
import com.example.data.model.ErrorIntelligenceItem
import com.example.data.model.MasteryStage
import com.example.ui.theme.*
import com.example.ui.vocabulary.VocabularySrsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ErrorIntelligenceScreen(
    viewModel: C1ViewModel,
    srsViewModel: VocabularySrsViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val errors by viewModel.repository.errorDatabase.collectAsState()
    val activeDrill by viewModel.selectedErrorForDrill.collectAsState()
    val drillAnswer by viewModel.errorDrillUserAnswer.collectAsState()
    val drillFeedback by viewModel.errorDrillFeedback.collectAsState()

    // Room Spaced Repetition System (SRS) State
    val srsCardState by srsViewModel.cardState.collectAsState()
    val srsAllVocab by srsViewModel.allVocabulary.collectAsState()
    val srsDueCount by srsViewModel.dueCount.collectAsState()
    val srsMasteredCount by srsViewModel.masteredCount.collectAsState()

    var selectedTab by remember { mutableStateOf(0) } // 0 = Fehler-Recycling, 1 = Wortschatz-SRS (Room)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BerlinTheme.bg,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Ausdrucks- & Wortschatzwerkstatt",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = BarlowCondensedFontFamily,
                            color = BerlinTheme.ink
                        )
                        Text(
                            text = "Beispiel-Drills und persönliche Wiederholung",
                            fontSize = 12.sp,
                            color = BerlinTheme.ink2
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("error_intelligence_back")
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück", tint = BerlinTheme.ink)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BerlinTheme.surface)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .testTag("error_intelligence_scroll"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Tab Switcher (Floating style, Berlin tokens)
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = BerlinTheme.surface,
                    border = BorderStroke(1.dp, BerlinTheme.line),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = BerlinTheme.surface,
                        contentColor = BerlinTheme.accent,
                        divider = {}
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = {
                                Text(
                                    "Beispiel-Drills (${errors.size})",
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedTab == 0) BerlinTheme.accentInk else BerlinTheme.ink2
                                )
                            }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        "Wortschatz-SRS (${srsAllVocab.size})",
                                        fontWeight = FontWeight.Bold,
                                        color = if (selectedTab == 1) BerlinTheme.accentInk else BerlinTheme.ink2
                                    )
                                    if (srsDueCount > 0) {
                                        Surface(
                                            shape = CircleShape,
                                            color = BerlinTheme.coral,
                                            modifier = Modifier.size(18.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = "$srsDueCount",
                                                    color = Color.White,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Black
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }

            if (selectedTab == 0) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = BerlinTheme.surface2,
                        border = BorderStroke(1.dp, BerlinTheme.line)
                    ) {
                        Text(
                            text = "Diese Beispiele sind Trainingsmaterial, keine Analyse deiner persönlichen Fehler. Persönliche Hinweise entstehen nur aus gespeicherten Antworten im Programm.",
                            modifier = Modifier.padding(14.dp),
                            fontSize = 12.sp,
                            color = BerlinTheme.ink2,
                            lineHeight = 17.sp
                        )
                    }
                }
                // Section 1: Active Error Transfer Drill (if selected)
                if (activeDrill != null) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth().testTag("active_drill_card"),
                            shape = RoundedCornerShape(24.dp),
                            border = BorderStroke(2.dp, BerlinTheme.accent),
                            colors = CardDefaults.cardColors(containerColor = BerlinTheme.surface)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "AKTIVER TRANSFER-DRILL (SOFORT-KORREKTUR)",
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = BarlowCondensedFontFamily,
                                        fontSize = 15.sp,
                                        color = BerlinTheme.accentInk
                                    )
                                    IconButton(onClick = { viewModel.clearSelectedErrorForDrill() }) {
                                        Icon(imageVector = Icons.Default.Close, contentDescription = "Schließen", tint = BerlinTheme.ink2)
                                    }
                                }

                                Text(
                                    text = activeDrill!!.transferChallengePrompt,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = BerlinTheme.ink
                                )

                                OutlinedTextField(
                                    value = drillAnswer,
                                    onValueChange = { viewModel.updateErrorDrillAnswer(it) },
                                    label = { Text("Ihre C1-Reformulierung:") },
                                    modifier = Modifier.fillMaxWidth().testTag("drill_answer_input"),
                                    shape = RoundedCornerShape(16.dp)
                                )

                                Button(
                                    onClick = { viewModel.submitErrorDrillAnswer() },
                                    modifier = Modifier.fillMaxWidth().height(54.dp).testTag("button_submit_drill"),
                                    colors = ButtonDefaults.buttonColors(containerColor = BerlinTheme.accent, contentColor = BerlinTheme.onAccent),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Text("Überprüfen & Transfer festigen", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                }

                                if (drillFeedback != null) {
                                    val isSuccess = drillFeedback!!.contains("Hervorragend")
                                    Surface(
                                        shape = RoundedCornerShape(14.dp),
                                        color = if (isSuccess) BerlinTheme.okBg else BerlinTheme.coralSoft,
                                        border = BorderStroke(1.dp, if (isSuccess) BerlinTheme.ok else BerlinTheme.coral),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = drillFeedback!!,
                                            modifier = Modifier.padding(14.dp),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSuccess) BerlinTheme.ok else BerlinTheme.coral
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Error Items List (24dp radius surface cards)
                items(errors) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth().testTag("error_card_${item.id}"),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = BerlinTheme.surface),
                        border = BorderStroke(1.dp, BerlinTheme.line)
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item.category.displayName,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BerlinTheme.ink2
                                )
                                Surface(
                                    shape = RoundedCornerShape(99.dp),
                                    color = BerlinTheme.okBg
                                ) {
                                    Text(
                                        text = item.masteryStage.label,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = BerlinTheme.ok,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Text(
                                text = "Übungsbeispiel: ${item.userMistake}",
                                color = BerlinTheme.coral,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            Text(
                                text = "Natürlichere Formulierung: ${item.nativeReformulation}",
                                color = BerlinTheme.ok,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = item.whyItSoundsForeign,
                                fontSize = 12.sp,
                                color = BerlinTheme.ink2,
                                lineHeight = 16.sp
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(onClick = { viewModel.selectErrorForDrill(item) }) {
                                    Text("Transfer üben ›", color = BerlinTheme.accentInk, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            } else {
                // Section 2: Room-Backed Spaced Repetition System (SRS)
                // 1. SRS Status Metric Card (24dp radius, Berlin tokens)
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        color = BerlinTheme.surface,
                        border = BorderStroke(1.dp, BerlinTheme.line)
                    ) {
                        Row(
                            modifier = Modifier.padding(18.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(16.dp),
                                color = BerlinTheme.surface2,
                                border = BorderStroke(1.dp, BerlinTheme.line)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(text = "Fällige Reviews", fontSize = 11.sp, color = BerlinTheme.ink2)
                                    Text(
                                        text = "$srsDueCount",
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = BarlowCondensedFontFamily,
                                        color = if (srsDueCount > 0) BerlinTheme.coral else BerlinTheme.ok
                                    )
                                    Text(text = "Spaced Repetition", fontSize = 10.sp, color = BerlinTheme.ink2)
                                }
                            }

                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(16.dp),
                                color = BerlinTheme.surface2,
                                border = BorderStroke(1.dp, BerlinTheme.line)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(text = "Langzeit-Transfer", fontSize = 11.sp, color = BerlinTheme.ink2)
                                    Text(
                                        text = "$srsMasteredCount",
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = BarlowCondensedFontFamily,
                                        color = BerlinTheme.ok
                                    )
                                    Text(text = "Stufe 5 Gemeistert", fontSize = 10.sp, color = BerlinTheme.ok)
                                }
                            }
                        }
                    }
                }

                // 2. Active SRS Flashcard Drill Card (24dp radius)
                item {
                    val activeCard = srsCardState.currentCard
                    if (activeCard != null && !srsCardState.isSessionFinished) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("srs_active_flashcard"),
                            shape = RoundedCornerShape(24.dp),
                            color = BerlinTheme.surface,
                            border = BorderStroke(2.dp, BerlinTheme.accent)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(99.dp),
                                        color = BerlinTheme.accentSoft
                                    ) {
                                        Text(
                                            text = "FÄLLIGE KARTE (${srsCardState.currentIndex + 1}/${srsCardState.totalDue})",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = BarlowCondensedFontFamily,
                                            color = BerlinTheme.accentInk,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(99.dp),
                                        color = BerlinTheme.surface2
                                    ) {
                                        Text(
                                            text = "Stufe ${activeCard.stage} • Intervall ${activeCard.intervalDays}d",
                                            fontSize = 11.sp,
                                            color = BerlinTheme.ink2,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                Text(
                                    text = activeCard.germanCollocation,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = BarlowCondensedFontFamily,
                                    color = BerlinTheme.ink
                                )

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = BerlinTheme.violetSoft
                                ) {
                                    Text(
                                        text = "${activeCard.category} • ${activeCard.register}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = BerlinTheme.violet,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                if (!srsCardState.isRevealed) {
                                    Button(
                                        onClick = { srsViewModel.revealCard() },
                                        modifier = Modifier.fillMaxWidth().height(54.dp).testTag("button_reveal_srs_card"),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = BerlinTheme.accent, contentColor = BerlinTheme.onAccent)
                                    ) {
                                        Icon(imageVector = Icons.Default.Visibility, contentDescription = null, tint = BerlinTheme.onAccent)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            if (activeCard.sourceUnitId.isBlank()) {
                                                "Karte aufdecken (Bedeutung & Beispiel)"
                                            } else {
                                                "Kursausdruck abrufen"
                                            },
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        )
                                    }
                                } else {
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text(
                                            text = "Bedeutung: ${activeCard.meaningDe}",
                                            fontSize = 14.sp,
                                            color = BerlinTheme.ink,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = if (activeCard.sourceUnitId.isBlank()) {
                                                "Beispiel: „${activeCard.exampleSentence}“"
                                            } else {
                                                activeCard.exampleSentence
                                            },
                                            fontSize = 13.sp,
                                            color = BerlinTheme.ink2
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Wie sicher war der spontaneous Recall?",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = BerlinTheme.ink
                                        )

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Button(
                                                onClick = { srsViewModel.submitReview(0) },
                                                modifier = Modifier.weight(1f).height(46.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = BerlinTheme.coral, contentColor = Color.White),
                                                shape = RoundedCornerShape(12.dp)
                                            ) {
                                                Text("Blackout (0)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }

                                            Button(
                                                onClick = { srsViewModel.submitReview(1) },
                                                modifier = Modifier.weight(1f).height(46.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = BerlinTheme.surface2, contentColor = BerlinTheme.ink),
                                                shape = RoundedCornerShape(12.dp)
                                            ) {
                                                Text("Schwer (1)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }

                                            Button(
                                                onClick = { srsViewModel.submitReview(2) },
                                                modifier = Modifier.weight(1f).height(46.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = BerlinTheme.skySoft, contentColor = BerlinTheme.sky),
                                                shape = RoundedCornerShape(12.dp)
                                            ) {
                                                Text("Gut (2)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }

                                            Button(
                                                onClick = { srsViewModel.submitReview(3) },
                                                modifier = Modifier.weight(1f).height(46.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = BerlinTheme.ok, contentColor = Color.White),
                                                shape = RoundedCornerShape(12.dp)
                                            ) {
                                                Text("Perfekt (3)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            color = BerlinTheme.okBg,
                            border = BorderStroke(1.5.dp, BerlinTheme.ok)
                        ) {
                            Row(
                                modifier = Modifier.padding(18.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = BerlinTheme.ok)
                                Column {
                                    Text(
                                        text = "ALLE FÄLLIGEN WORTSCHATZ-KARTEN WIEDERHOLT!",
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = BarlowCondensedFontFamily,
                                        fontSize = 15.sp,
                                        color = BerlinTheme.ok
                                    )
                                    Text(
                                        text = "Das Spaced Repetition System erinnert Sie automatisch, sobald neue Items fällig sind.",
                                        fontSize = 12.sp,
                                        color = BerlinTheme.ink2
                                    )
                                }
                            }
                        }
                    }
                }

                // 3. Room-Persisted Vocabulary Items List with Timestamps (24dp cards)
                item {
                    Text(
                        text = "ALLE IM ROOM-SRS GESPEICHERTEN EINHEITEN (${srsAllVocab.size})",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = BarlowCondensedFontFamily,
                        color = BerlinTheme.ink
                    )
                }

                items(srsAllVocab) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = BerlinTheme.surface),
                        border = BorderStroke(1.dp, BerlinTheme.line)
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item.germanCollocation,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = BarlowCondensedFontFamily,
                                    color = BerlinTheme.ink
                                )
                                Surface(
                                    shape = RoundedCornerShape(99.dp),
                                    color = if (item.isMastered) BerlinTheme.okBg else BerlinTheme.surface2
                                ) {
                                    Text(
                                        text = if (item.isMastered) "Stufe 5 (Gemeistert)" else "Stufe ${item.stage} (${item.intervalDays}d)",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (item.isMastered) BerlinTheme.ok else BerlinTheme.ink2,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Text(
                                text = "Bedeutung: ${item.meaningDe}",
                                fontSize = 13.sp,
                                color = BerlinTheme.ink
                            )

                            Text(
                                text = "Beispiel: „${item.exampleSentence}“",
                                fontSize = 12.sp,
                                color = BerlinTheme.ink2
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            // Exact Timestamps Display
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Zuletzt: ${formatLastReviewed(item.lastReviewedTimestamp)}",
                                    fontSize = 11.sp,
                                    color = BerlinTheme.ink2
                                )
                                Text(
                                    text = "Nächste: ${formatReviewDate(item.nextReviewTimestamp)}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (item.nextReviewTimestamp <= System.currentTimeMillis()) BerlinTheme.coral else BerlinTheme.ok
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

fun formatReviewDate(timestamp: Long): String {
    if (timestamp == 0L) return "Heute fällig"
    val diff = timestamp - System.currentTimeMillis()
    val days = (diff / 86_400_000L).toInt()
    return when {
        days < 0 -> "Fällig (Überfällig seit ${-days} Tagen)"
        days == 0 -> "Heute fällig"
        days == 1 -> "Morgen fällig"
        else -> "In $days Tagen"
    }
}

fun formatLastReviewed(timestamp: Long?): String {
    if (timestamp == null || timestamp == 0L) return "Noch ungetestet"
    val diff = System.currentTimeMillis() - timestamp
    val days = (diff / 86_400_000L).toInt()
    return when {
        days <= 0 -> "Heute"
        days == 1 -> "Gestern"
        else -> "Vor $days Tagen"
    }
}
