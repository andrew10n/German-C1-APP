package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Transform
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MediationTask
import com.example.data.model.SkillCategory
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediationScreen(
    viewModel: C1ViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val tasks by viewModel.repository.mediationTasks.collectAsState()
    var selectedIndex by remember { mutableStateOf(0) }
    val currentTask = tasks.getOrNull(selectedIndex) ?: return

    var userMediationText by remember { mutableStateOf("") }
    var showModelSolution by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BerlinTheme.bg,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Mediation & Sprachmittlung",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = BarlowCondensedFontFamily,
                            color = BerlinTheme.ink
                        )
                        Text(
                            text = "Komplexe Fachtexte laienverständlich vermitteln",
                            fontSize = 12.sp,
                            color = BerlinTheme.ink2
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("mediation_back")
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
                .testTag("mediation_scroll"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Task Tabs
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    tasks.forEachIndexed { index, task ->
                        val isSelected = index == selectedIndex
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    selectedIndex = index
                                    userMediationText = ""
                                    showModelSolution = false
                                },
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) BerlinTheme.hero else BerlinTheme.surface,
                            border = BorderStroke(1.dp, if (isSelected) BerlinTheme.accent else BerlinTheme.line)
                        ) {
                            Text(
                                text = "Aufgabe ${index + 1}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) BerlinTheme.onHero else BerlinTheme.ink,
                                modifier = Modifier.padding(vertical = 12.dp, horizontal = 14.dp)
                            )
                        }
                    }
                }
            }

            // Task Content Card (24dp radius, Berlin tokens)
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
                                text = currentTask.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                fontFamily = BarlowCondensedFontFamily,
                                color = BerlinTheme.ink,
                                modifier = Modifier.weight(1f)
                            )
                            Surface(
                                shape = RoundedCornerShape(99.dp),
                                color = BerlinTheme.violetSoft
                            ) {
                                Text(
                                    text = currentTask.mediationType,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BerlinTheme.violet,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Text(
                            text = "Adressat: ${currentTask.recipientContext}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = BerlinTheme.accentInk
                        )

                        // Source Material
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = BerlinTheme.surface2,
                            border = BorderStroke(1.dp, BerlinTheme.line),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "AUSGANGSTEXT:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = BarlowCondensedFontFamily,
                                    color = BerlinTheme.ink2
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = currentTask.sourceMaterial,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    lineHeight = 18.sp,
                                    color = BerlinTheme.ink
                                )
                            }
                        }

                        // Critical Pitfalls
                        Text(
                            text = "ZU VERMEIDENDE FEHLER (FALLSTRICKE):",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            fontFamily = BarlowCondensedFontFamily,
                            color = BerlinTheme.ink
                        )
                        currentTask.criticalPitfallsToAvoid.forEach { pit ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = BerlinTheme.accentInk,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(text = pit, fontSize = 12.sp, color = BerlinTheme.ink2)
                            }
                        }

                        OutlinedTextField(
                            value = userMediationText,
                            onValueChange = { userMediationText = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 110.dp)
                                .testTag("mediation_input_field"),
                            placeholder = { Text("Hier adressatengerechte Sprachmittlung verfassen...") },
                            shape = RoundedCornerShape(16.dp)
                        )

                        Button(
                            onClick = {
                                showModelSolution = true
                                viewModel.repository.recordSessionCompleted(SkillCategory.MEDIATION, 2)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BerlinTheme.accent,
                                contentColor = BerlinTheme.onAccent
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("Mediation bewerten & Musterlösung aufdecken", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }

                        if (showModelSolution) {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = BerlinTheme.okBg,
                                border = BorderStroke(1.5.dp, BerlinTheme.ok),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = "C1 MUSTER-SPRACHMITTLUNG:",
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = BarlowCondensedFontFamily,
                                        fontSize = 12.sp,
                                        color = BerlinTheme.ok
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = currentTask.targetReformulationModel,
                                        fontSize = 13.sp,
                                        lineHeight = 18.sp,
                                        color = BerlinTheme.ink
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
