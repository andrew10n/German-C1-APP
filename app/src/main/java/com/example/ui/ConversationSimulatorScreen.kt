package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationSimulatorScreen(
    viewModel: C1ViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.stopAudio()
        viewModel.navigateBack()
    }

    val scenarios by viewModel.repository.conversationScenarios.collectAsState()
    val activeIdx by viewModel.activeConversationIndex.collectAsState()
    val turnIdx by viewModel.currentConversationTurnIndex.collectAsState()
    val interruptionTriggered by viewModel.interruptionTriggered.collectAsState()
    val responseLatency by viewModel.responseLatencySeconds.collectAsState()
    val userResponse by viewModel.conversationUserResponse.collectAsState()
    val isPlaying by viewModel.speechHelper.isPlaying.collectAsState()

    val currentScenario = scenarios.getOrNull(activeIdx) ?: return
    val currentTurn = currentScenario.turns.getOrNull(turnIdx)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BerlinTheme.bg,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Sprech- & Interaktions-Simulator",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = BarlowCondensedFontFamily,
                            color = BerlinTheme.ink
                        )
                        Text(
                            text = "Unvorhersehbare Unterbrechungen & Latenz-Training",
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
                        modifier = Modifier.testTag("conversation_back")
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück", tint = BerlinTheme.ink)
                    }
                },
                actions = {
                    Surface(
                        onClick = { viewModel.navigateTo(Screen.SpeakingLab) },
                        shape = RoundedCornerShape(99.dp),
                        color = BerlinTheme.accentSoft,
                        border = BorderStroke(1.dp, BerlinTheme.accent.copy(alpha = 0.5f)),
                        modifier = Modifier.padding(end = 8.dp).testTag("btn_switch_to_speaking_lab")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Mic, contentDescription = null, tint = BerlinTheme.accentInk, modifier = Modifier.size(15.dp))
                            Text("Aussprache-Labor", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = BerlinTheme.accentInk)
                        }
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
                .testTag("conversation_scroll"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Scenario Picker
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    itemsIndexed(scenarios) { idx, sc ->
                        val isSelected = idx == activeIdx
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) BerlinTheme.hero else BerlinTheme.surface,
                            border = BorderStroke(1.dp, if (isSelected) BerlinTheme.accent else BerlinTheme.line),
                            modifier = Modifier.clickable { viewModel.selectConversation(idx) }
                        ) {
                            Text(
                                text = sc.title.take(28) + "...",
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) BerlinTheme.onHero else BerlinTheme.ink,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                            )
                        }
                    }
                }
            }

            // Scenario Brief Card (24dp radius, Berlin tokens)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = BerlinTheme.surface),
                    border = BorderStroke(1.dp, BerlinTheme.line)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = currentScenario.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                fontFamily = BarlowCondensedFontFamily,
                                color = BerlinTheme.ink
                            )
                            Surface(
                                shape = RoundedCornerShape(99.dp),
                                color = BerlinTheme.accentSoft
                            ) {
                                Text(
                                    text = currentScenario.environment,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BerlinTheme.accentInk,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Text(
                            text = "Gesprächspartner: ${currentScenario.rolePartner}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = BerlinTheme.accentInk
                        )
                        Text(
                            text = "${currentScenario.variationLabel} · synthetische Mehrstimmen-Version",
                            fontSize = 11.sp,
                            color = BerlinTheme.ink2
                        )

                        // Opening Statement
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = BerlinTheme.surface2,
                            border = BorderStroke(1.dp, BerlinTheme.line),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "ERÖFFNUNG:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = BarlowCondensedFontFamily,
                                    color = BerlinTheme.ink2
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "„${currentScenario.openingStatement}“",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = BerlinTheme.ink
                                )
                            }
                        }
                    }
                }
            }

            // Sudden Interruption Banner if Triggered (.alert style: Coral soft background, coral border, 24dp radius)
            if (interruptionTriggered) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().testTag("interruption_alert_card"),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = BerlinTheme.coralSoft),
                        border = BorderStroke(1.5.dp, BerlinTheme.coral)
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = BerlinTheme.coral
                                )
                                Text(
                                    text = "⚡ PLÖTZLICHE UNTERBRECHUNG!",
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = BarlowCondensedFontFamily,
                                    fontSize = 16.sp,
                                    color = BerlinTheme.coral
                                )
                            }

                            Text(
                                text = "„${currentScenario.unexpectedInterruption.partnerInterruptSpeech}“",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = BerlinTheme.ink
                            )

                            Text(
                                text = "Ziel: ${currentScenario.unexpectedInterruption.reactionObjective}",
                                fontSize = 12.sp,
                                color = BerlinTheme.ink2
                            )
                        }
                    }
                }
            }

            // Current Turn Partner Speech (24dp radius, Berlin tokens)
            if (currentTurn != null) {
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
                                    text = "GESPRÄCHSRUNDE ${turnIdx + 1} VON ${currentScenario.turns.size}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    fontFamily = BarlowCondensedFontFamily,
                                    color = BerlinTheme.ink
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Schedule,
                                        contentDescription = null,
                                        tint = if (responseLatency > 4) BerlinTheme.coral else BerlinTheme.ok,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${responseLatency}s Latenz",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (responseLatency > 4) BerlinTheme.coral else BerlinTheme.ok
                                    )
                                }
                            }

                            // Partner Speech Bubble
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = BerlinTheme.surface2,
                                border = BorderStroke(1.dp, BerlinTheme.line),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "„${currentTurn.partnerSpeech}“",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = BerlinTheme.ink,
                                    modifier = Modifier.padding(14.dp)
                                )
                            }

                            Text(
                                text = "Ihre Aufgabe: ${currentTurn.expectedUserRolePrompt}",
                                fontSize = 12.sp,
                                color = BerlinTheme.ink2
                            )

                            // Suggested C1 Connectors
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                currentTurn.suggestedC1Connectors.forEach { conn ->
                                    Surface(
                                        shape = RoundedCornerShape(99.dp),
                                        color = BerlinTheme.accentSoft,
                                        border = BorderStroke(1.dp, BerlinTheme.accent.copy(alpha = 0.4f))
                                    ) {
                                        Text(
                                            text = conn,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = BerlinTheme.accentInk,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = userResponse,
                                onValueChange = { viewModel.submitConversationResponse(it) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 90.dp)
                                    .testTag("conversation_user_input"),
                                placeholder = { Text("Spontane Erwiderung formulieren...") },
                                shape = RoundedCornerShape(16.dp)
                            )

                            Button(
                                onClick = { viewModel.nextConversationTurn() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp)
                                    .testTag("button_next_turn"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = BerlinTheme.accent,
                                    contentColor = BerlinTheme.onAccent
                                ),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text("Erwiderung absenden & Fortfahren", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
