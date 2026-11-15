package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SkillCategory
import com.example.data.model.SkillScore
import com.example.ui.components.C1ProficiencyProgressChart
import com.example.ui.components.VisualPerformanceDashboard
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntelligenceProfileScreen(
    viewModel: C1ViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val skillMap by viewModel.repository.skillScores.collectAsState()
    val crossCutting by viewModel.repository.crossCutting.collectAsState()
    val dualBenchmark by viewModel.repository.dualBenchmark.collectAsState()
    val diagnosticReport by viewModel.repository.diagnosticReport.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BerlinTheme.bg,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "C1 Intelligenz-Profil",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = BarlowCondensedFontFamily,
                            color = BerlinTheme.ink
                        )
                        Text(
                            text = "9 Dimensionen & Dual-Benchmark",
                            fontSize = 12.sp,
                            color = BerlinTheme.ink2
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("intelligence_back")
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
                .testTag("intelligence_profile_scroll"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // C1 Proficiency Progress Over Time (Room SQLite Live Chart)
            item {
                C1ProficiencyProgressChart()
            }

            // Visual Progress Dashboard (Listening, Speaking, Vocabulary Retention)
            item {
                VisualPerformanceDashboard()
            }

            // Dual Benchmark Overview Card (24dp radius, Berlin tokens)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("profile_benchmark_card"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = BerlinTheme.surface),
                    border = BorderStroke(1.dp, BerlinTheme.line)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "DUAL-BENCHMARK: FUNKTIONAL VS. ZERTIFIKAT",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = BarlowCondensedFontFamily,
                            color = BerlinTheme.ink
                        )

                        Text(
                            text = "Echtes C1 bedeutet souveräne Handlungsfähigkeit in fordernden Lebens- und Arbeitssituationen – nicht bloßes Beherrschen von Prüfungsmechaniken.",
                            fontSize = 12.sp,
                            color = BerlinTheme.ink2,
                            lineHeight = 17.sp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            BenchmarkScoreBox(
                                title = "Funktionales C1",
                                score = "${dualBenchmark.functionalC1Score}/100",
                                subtitle = "Reale Praxis-Tauglichkeit",
                                color = BerlinTheme.accent,
                                modifier = Modifier.weight(1f)
                            )
                            BenchmarkScoreBox(
                                title = "Prüfungs-C1",
                                score = "${dualBenchmark.examC1Score}%",
                                subtitle = "Goethe / TestDaF / telc",
                                color = BerlinTheme.sky,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Exam Specific Breakdown
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = BerlinTheme.surface2,
                            border = BorderStroke(1.dp, BerlinTheme.line),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "ZERTIFIKATS-REIFE IM DETAIL:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = BarlowCondensedFontFamily,
                                    color = BerlinTheme.ink2
                                )
                                ExamBar(name = "Goethe-Zertifikat C1", percent = dualBenchmark.goetheC1Readiness)
                                ExamBar(name = "TestDaF (TDN 5 Niveau)", percent = dualBenchmark.testDafReadiness)
                                ExamBar(name = "telc Deutsch C1 Hochschule", percent = dualBenchmark.telcC1Readiness)
                            }
                        }
                    }
                }
            }

            // Cross-Cutting Indicators
            item {
                Text(
                    text = "QUERSCHNITTS-KOMPETENZEN",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = BarlowCondensedFontFamily,
                    color = BerlinTheme.ink
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    crossCutting.forEach { comp ->
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = BerlinTheme.surface),
                            border = BorderStroke(1.dp, BerlinTheme.line)
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = comp.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    fontFamily = BarlowCondensedFontFamily,
                                    color = BerlinTheme.accentInk
                                )
                                Text(
                                    text = "${comp.score}/100 • ${comp.levelLabel}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = BerlinTheme.ink
                                )
                                Text(
                                    text = comp.description,
                                    fontSize = 11.sp,
                                    color = BerlinTheme.ink2,
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }
                }
            }

            // 9 Core Dimensions List
            item {
                Text(
                    text = "DIE 9 KERN-DIMENSIONEN DES C1-PROFILS",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = BarlowCondensedFontFamily,
                    color = BerlinTheme.ink
                )
            }

            skillMap.values.toList().forEach { skill ->
                item {
                    SkillDimensionCard(skill = skill)
                }
            }
        }
    }
}

@Composable
fun BenchmarkScoreBox(
    title: String,
    score: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = BerlinTheme.surface2,
        border = BorderStroke(1.dp, BerlinTheme.line)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BerlinTheme.ink)
            Text(
                text = score,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = BarlowCondensedFontFamily,
                color = color
            )
            Text(text = subtitle, fontSize = 11.sp, color = BerlinTheme.ink2)
        }
    }
}

@Composable
fun ExamBar(name: String, percent: Int) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = name, fontSize = 12.sp, color = BerlinTheme.ink)
            Text(text = "$percent%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BerlinTheme.sky)
        }
        LinearProgressIndicator(
            progress = { percent / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = BerlinTheme.sky,
            trackColor = BerlinTheme.line
        )
    }
}

@Composable
fun SkillDimensionCard(skill: SkillScore) {
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
                    text = skill.category.titleDe,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    fontFamily = BarlowCondensedFontFamily,
                    color = BerlinTheme.ink
                )

                Surface(
                    shape = RoundedCornerShape(99.dp),
                    color = BerlinTheme.accentSoft
                ) {
                    Text(
                        text = "${skill.score} / 100",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = BerlinTheme.accentInk,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                    )
                }
            }

            LinearProgressIndicator(
                progress = { skill.score / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (skill.score < 60) BerlinTheme.coral else BerlinTheme.accent,
                trackColor = BerlinTheme.line
            )

            // Critical Weakness Flag (.alert style: Coral)
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = BerlinTheme.coral,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Aktuelle Schwachstelle: ${skill.criticalWeakness}",
                    fontSize = 11.sp,
                    color = BerlinTheme.ink2,
                    lineHeight = 15.sp
                )
            }
        }
    }
}
