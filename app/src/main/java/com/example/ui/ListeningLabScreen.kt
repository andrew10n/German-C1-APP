package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*
import com.example.ui.components.BookAudioPlaybackController
import com.example.ui.components.BookAudioPracticeSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListeningLabScreen(
    viewModel: C1ViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.stopAudio()
        viewModel.navigateBack()
    }

    val labItems by viewModel.repository.listeningItems.collectAsState()
    val selectedIndex by viewModel.selectedLabItemIndex.collectAsState()
    val speed by viewModel.playbackSpeed.collectAsState()
    val isPlaying by viewModel.speechHelper.isPlaying.collectAsState()
    val transcriptRevealed by viewModel.isBlindTranscriptRevealed.collectAsState()
    val userAnswers by viewModel.selectedLabQuizAnswers.collectAsState()
    val bookAudioController = remember { BookAudioPlaybackController() }

    val currentItem = labItems.getOrNull(selectedIndex) ?: return

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BerlinTheme.bg,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Hör-Intelligenz Labor",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = BarlowCondensedFontFamily,
                            color = BerlinTheme.ink
                        )
                        Text(
                            text = "Schnellsprech & regionale Reduktionen",
                            fontSize = 12.sp,
                            color = BerlinTheme.ink2
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            viewModel.stopAudio()
                            viewModel.navigateBack()
                        },
                        modifier = Modifier.testTag("listening_lab_back")
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
                .testTag("listening_lab_scroll"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Banner Image (Dark hero card style)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            painter = painterResource(id = R.drawable.img_hero_studio),
                            contentDescription = "Studio",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(BerlinTheme.hero.copy(alpha = 0.72f))
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(18.dp)
                        ) {
                            Text(
                                text = "AUTHENTISCHES DEUTSCH LABOR",
                                color = BerlinTheme.onHero,
                                fontWeight = FontWeight.Bold,
                                fontFamily = BarlowCondensedFontFamily,
                                fontSize = 18.sp
                            )
                            Text(
                                text = "Keine sauberen Lehrbuch-Monologe. Natürlicher Redefluss & Dialekte.",
                                color = BerlinTheme.onHero2,
                                fontSize = 12.sp
                            )
                        }

                    }
                }
            }

            item {
                BookAudioPracticeSection(
                    controller = bookAudioController,
                    onPlaybackStart = viewModel::stopAudio
                )
            }

            item {
                ExternalListeningSourcesSection()
            }

            // Scenario Selector Row
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    itemsIndexed(labItems) { index, item ->
                        val isSelected = index == selectedIndex
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) BerlinTheme.hero else BerlinTheme.surface,
                            border = BorderStroke(1.dp, if (isSelected) BerlinTheme.accent else BerlinTheme.line),
                            modifier = Modifier
                                .clickable { viewModel.selectLabItem(index) }
                                .testTag("lab_item_$index")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.GraphicEq,
                                    contentDescription = null,
                                    tint = if (isSelected) BerlinTheme.accent else BerlinTheme.ink2,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = item.title.take(24) + "...",
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) BerlinTheme.onHero else BerlinTheme.ink
                                )
                            }
                        }

                    }
                }
            }

            // Selected Audio Player Card (24dp radius, Berlin tokens)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = BerlinTheme.surface),
                    border = BorderStroke(1.dp, BerlinTheme.line)
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
                                text = currentItem.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                fontFamily = BarlowCondensedFontFamily,
                                color = BerlinTheme.ink,
                                modifier = Modifier.weight(1f)
                            )
                            Surface(
                                shape = RoundedCornerShape(99.dp),
                                color = BerlinTheme.skySoft
                            ) {
                                Text(
                                    text = currentItem.environment,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BerlinTheme.sky,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Text(
                            text = "Dialekt & Duktus: ${currentItem.accentOrDialect}",
                            fontSize = 12.sp,
                            color = BerlinTheme.ink2
                        )

                        // Speed Controls: 1.0x, 1.15x, 1.25x, 1.35x
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Sprechtempo:",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = BerlinTheme.ink
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf(1.0f, 1.15f, 1.25f, 1.35f).forEach { s ->
                                    val isCurrentSpeed = speed == s
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (isCurrentSpeed) BerlinTheme.hero else BerlinTheme.surface2,
                                        border = BorderStroke(1.dp, if (isCurrentSpeed) BerlinTheme.accent else BerlinTheme.line),
                                        modifier = Modifier
                                            .clickable { viewModel.setPlaybackSpeed(s) }
                                            .testTag("speed_button_${s}x")
                                    ) {
                                        Text(
                                            text = "${s}x",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isCurrentSpeed) BerlinTheme.accent else BerlinTheme.ink2,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Big Play / Stop Button (54dp height, 16dp radius, teal container)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Button(
                                onClick = {
                                    if (isPlaying) {
                                        viewModel.stopAudio()
                                    } else {
                                        bookAudioController.stop()
                                        viewModel.playCurrentLabAudio()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isPlaying) BerlinTheme.coral else BerlinTheme.accent,
                                    contentColor = if (isPlaying) Color.White else BerlinTheme.onAccent
                                ),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp)
                                    .testTag("button_play_lab_audio")
                            ) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = if (isPlaying) Color.White else BerlinTheme.onAccent
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isPlaying) "Wiedergabe stoppen" else "Blind anhören (${speed}x)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
            }

            // Inference Questions (Testing intent, subtext & tone BEFORE transcript)
            item {
                Text(
                    text = "1. INFERENZ- & INTENTIONSTEST (VOR TRANSKRIPT-EINSICHT)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = BarlowCondensedFontFamily,
                    color = BerlinTheme.ink
                )
            }

            itemsIndexed(currentItem.blindComprehensionQuestions) { qIndex, question ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = BerlinTheme.surface),
                    border = BorderStroke(1.dp, BerlinTheme.line)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = question.question,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = BerlinTheme.ink
                        )

                        // Multiple Choice Option Rows (.opt style: 2dp border, 16dp radius, min-height 56dp)
                        question.options.forEachIndexed { optIndex, option ->
                            val isSelected = userAnswers[qIndex] == optIndex
                            val isCorrect = optIndex == question.correctIndex
                            val answered = userAnswers.containsKey(qIndex)

                            val (borderCol, bgCol) = when {
                                answered && isCorrect -> Pair(BerlinTheme.ok, BerlinTheme.okBg)
                                answered && isSelected && !isCorrect -> Pair(BerlinTheme.coral, BerlinTheme.coralSoft)
                                isSelected -> Pair(BerlinTheme.accent, BerlinTheme.accentSoft)
                                else -> Pair(BerlinTheme.line, BerlinTheme.surface)
                            }

                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = bgCol,
                                border = BorderStroke(2.dp, borderCol),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .defaultMinSize(minHeight = 56.dp)
                                    .clickable { viewModel.answerLabQuestion(qIndex, optIndex) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { viewModel.answerLabQuestion(qIndex, optIndex) },
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = if (answered && isCorrect) BerlinTheme.ok else BerlinTheme.accent,
                                            unselectedColor = BerlinTheme.ink2
                                        )
                                    )
                                    Text(text = option, fontSize = 13.sp, color = BerlinTheme.ink, lineHeight = 18.sp)
                                }
                            }
                        }

                        if (userAnswers.containsKey(qIndex)) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = BerlinTheme.surface2,
                                border = BorderStroke(1.dp, BerlinTheme.line),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Analyse: ${question.rationaleWhyOthersAreWrong}",
                                    fontSize = 12.sp,
                                    color = BerlinTheme.ink2,
                                    lineHeight = 16.sp,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Step 2: Reveal Transcript with Reductions
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = BerlinTheme.surface),
                    border = BorderStroke(1.dp, BerlinTheme.line)
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
                            Text(
                                text = "2. TRANSKRIPT & REDUKTIONS-ANALYSE",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                fontFamily = BarlowCondensedFontFamily,
                                color = BerlinTheme.ink
                            )
                            if (!transcriptRevealed) {
                                Button(
                                    onClick = { viewModel.revealBlindTranscript() },
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = BerlinTheme.accent,
                                        contentColor = BerlinTheme.onAccent
                                    ),
                                    modifier = Modifier.height(44.dp).testTag("button_reveal_transcript")
                                ) {
                                    Text("Aufdecken", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        if (!transcriptRevealed) {
                            Text(
                                text = "Hören Sie zuerst blind und beantworten Sie die Inferenz-Fragen oben, um Ihr echtes C1-Hörverstehen zu schärfen.",
                                fontSize = 13.sp,
                                color = BerlinTheme.ink2
                            )
                        } else {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = BerlinTheme.surface2,
                                border = BorderStroke(1.dp, BerlinTheme.line),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Text(
                                        text = currentItem.transcriptWithReductions,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        lineHeight = 19.sp,
                                        color = BerlinTheme.ink
                                    )
                                }
                            }

                            Text(
                                text = "HERAUSGEFILTERTE REDUKTIONEN:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                fontFamily = BarlowCondensedFontFamily,
                                color = BerlinTheme.ink
                            )

                            currentItem.reductionNotes.forEach { red ->
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = BerlinTheme.surface2,
                                    border = BorderStroke(1.dp, BerlinTheme.line),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = BerlinTheme.skySoft
                                        ) {
                                            Text(
                                                text = red.spokenForm,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = BerlinTheme.sky,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }
                                        Icon(
                                            imageVector = Icons.Default.ArrowForward,
                                            contentDescription = null,
                                            tint = BerlinTheme.ink2,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Column {
                                            Text(
                                                text = red.standardWrittenForm,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = BerlinTheme.ink
                                            )
                                            Text(
                                                text = red.registerExplanation,
                                                fontSize = 11.sp,
                                                color = BerlinTheme.ink2
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
