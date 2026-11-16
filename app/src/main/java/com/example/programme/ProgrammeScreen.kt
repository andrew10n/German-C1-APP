package com.example.programme

import android.Manifest
import android.content.ClipData
import android.content.Intent
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.text.DateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.delay
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import com.example.ui.theme.BerlinTheme
import com.example.ui.AiFeedbackShareButton
import com.example.MainApplication
import com.example.programme.LocalAssessmentAudioStore
import com.example.service.SpeechCaptureState
import com.example.service.SpeechRecognitionHelper
import com.example.service.AudioSpeechHelper
import com.example.ui.components.BookAudioPlaybackController
import com.example.ui.components.BookAudioPracticeSection
import java.io.IOException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgrammeScreen(
    viewModel: ProgrammeViewModel,
    onLegacyPractice: () -> Unit,
    onOpenListeningLab: () -> Unit = {},
    onOpenSpeakingLab: () -> Unit = {},
    onOpenConversation: () -> Unit = {},
    onOpenMediation: () -> Unit = {}
) {
    val preferences by viewModel.preferences.collectAsStateWithLifecycle()
    val attempts by viewModel.attempts.collectAsStateWithLifecycle()
    val attemptsByUnit by viewModel.attemptsByUnit.collectAsStateWithLifecycle()
    val loading by viewModel.loading.collectAsStateWithLifecycle()
    val historyAvailable by viewModel.historyAvailable.collectAsStateWithLifecycle()
    val saving by viewModel.saving.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val notice by viewModel.notice.collectAsStateWithLifecycle()
    val results by viewModel.quizResults.collectAsStateWithLifecycle()
    val assistedUnits by viewModel.assistedUnits.collectAsStateWithLifecycle()
    val bookmarkedUnitIds by viewModel.bookmarkedUnitIds.collectAsStateWithLifecycle()
    val realLifeExperiences by viewModel.realLifeExperiences.collectAsStateWithLifecycle()
    val feedbackChallenges by viewModel.feedbackChallenges.collectAsStateWithLifecycle()
    val humanReviews by viewModel.humanReviews.collectAsStateWithLifecycle()
    var selectedId by rememberSaveable { mutableStateOf(viewModel.lastUnitId) }
    var initialTask by rememberSaveable { mutableStateOf(viewModel.lastTaskId) }
    var settings by rememberSaveable { mutableStateOf(false) }
    var showOnboarding by rememberSaveable { mutableStateOf(!viewModel.onboardingComplete) }
    var showRealLifeDialog by rememberSaveable { mutableStateOf(false) }
    var reviewExperience by remember { mutableStateOf<RealLifeExperience?>(null) }
    val now by produceState(System.currentTimeMillis()) {
        while (true) {
            value = System.currentTimeMillis()
            delay(60_000)
        }
    }
    val evidence = remember(attempts, now) { attempts.filter { it.timestamp <= now } }
    val weaknessProfile = remember(attempts, realLifeExperiences, now) {
        WeaknessEngine.profile(attempts, realLifeExperiences, now)
    }
    val diagnosticProfile = remember(attempts, realLifeExperiences, now) {
        LearnerDiagnosticEngine.analyze(attempts, realLifeExperiences, now)
    }
    val adaptiveDecision = remember(attempts, realLifeExperiences, now) {
        AdaptiveControlEngine.decide(attempts, realLifeExperiences, now)
    }
    val selected = CurriculumCatalogue.units.find { it.id == selectedId }
    val accent = BerlinTheme.accent
    val colors = MaterialTheme.colorScheme.copy(
        primary = accent,
        onPrimary = BerlinTheme.onAccent,
        primaryContainer = BerlinTheme.accentSoft,
        onPrimaryContainer = BerlinTheme.accentInk,
        secondary = accent,
        onSecondary = BerlinTheme.onAccent,
        outline = BerlinTheme.line
    )
    BackHandler(enabled = selected != null && !settings) { selectedId = null }
    MaterialTheme(colorScheme = colors) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(preferences.language.text("DEUTSCH / TÄGLICH", "GERMAN / DAILY"),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary)
                            Text(
                                if (selected == null) preferences.language.text("Dein Programm", "Your program")
                                else preferences.language.text("Lernwerkstatt", "Lesson studio")
                            )
                        }
                    },
                    navigationIcon = {
                        if (selected != null) TextButton(onClick = { selectedId = null }) {
                            Text(preferences.language.text("Zurück", "Back"))
                        }
                    },
                    actions = {
                        TextButton(onClick = { settings = true }) {
                            Text(preferences.language.text("Anpassen · Einstellungen", "Settings"))
                        }
                    }
                )
            }
        ) { insets ->
            Box(Modifier.fillMaxSize().padding(insets)) {
                Column(
                    Modifier
                        .widthIn(max = 880.dp)
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .align(Alignment.TopCenter)
                ) {
                if (saving) {
                    Row(Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) {
                        CircularProgressIndicator(Modifier.sizeIn(maxWidth = 18.dp, maxHeight = 18.dp), strokeWidth = 2.dp)
                        Spacer(Modifier.width(10.dp))
                        Text("Wird gespeichert …", style = MaterialTheme.typography.labelMedium)
                    }
                }
                val feedback = error ?: notice
                if (feedback != null) {
                    Surface(color = if (error != null) colors.errorContainer else colors.primaryContainer) {
                        Row(Modifier.fillMaxWidth().padding(start = 16.dp)) {
                            Text(feedback, Modifier.weight(1f).padding(vertical = 12.dp),
                                color = if (error != null) colors.onErrorContainer else colors.onPrimaryContainer,
                                style = MaterialTheme.typography.bodySmall)
                            TextButton(onClick = viewModel::clearFeedback) { Text("OK") }
                        }
                    }
                }
                if (selected == null) {
                    ProgrammeDashboard(
                        preferences, evidence, realLifeExperiences, weaknessProfile, diagnosticProfile,
                        adaptiveDecision,
                        now, loading, historyAvailable,
                        onMinutes = viewModel::updateDailyMinutes,
                        plan = viewModel.plan(preferences.dailyMinutes, realLifeExperiences, now, attempts),
                        onOpen = {
                            unit, task ->
                            selectedId = unit.id
                            initialTask = task
                            viewModel.rememberLesson(unit.id, task)
                        },
                        onLegacyPractice = onLegacyPractice,
                        onLogRealLife = { showRealLifeDialog = true },
                        onReviewRealLife = { reviewExperience = it },
                        feedbackChallenges = feedbackChallenges,
                        onResolveChallenge = viewModel::resolveFeedbackChallenge,
                        onRecordAssessment = viewModel::recordAssessmentResponse,
                        bookmarkedUnitIds = bookmarkedUnitIds,
                        onToggleBookmark = viewModel::toggleBookmark,
                        onOpenListeningLab = onOpenListeningLab,
                        onOpenSpeakingLab = onOpenSpeakingLab,
                        onOpenConversation = onOpenConversation,
                        onOpenMediation = onOpenMediation
                    )
                } else {
                    UnitDetail(
                        selected, evidence, realLifeExperiences, preferences.compact, initialTask,
                        now, attemptsByUnit[selected.id].orEmpty().filter { it.timestamp <= now },
                        enabled = historyAvailable && selected.content != null,
                        saving = saving, result = results[selected.id],
                        assisted = selected.id in assistedUnits,
                        onReveal = { viewModel.revealModel(selected.id) },
                        onQuiz = { option, context -> viewModel.submitQuiz(selected.id, option, context) },
                        onSave = { task, response, context -> viewModel.recordPractice(selected.id, task, response, context) },
                        onAddExpressions = { viewModel.addCourseExpressionsToRetrieval(selected.id) },
                        onChallengeFeedback = viewModel::challengeFeedback,
                        humanReviews = humanReviews,
                        onRecordHumanReview = viewModel::recordHumanReview,
                        existingFeedbackClaimIds = feedbackChallenges.mapTo(mutableSetOf()) { it.claimId },
                        onOpenListeningLab = onOpenListeningLab,
                        onOpenSpeakingLab = onOpenSpeakingLab,
                        onOpenConversation = onOpenConversation
                    )
                    }
            }
        }
        }
        if (settings) PreferencesDialog(preferences, viewModel, onDismiss = { settings = false })
        if (showOnboarding) {
            ProgrammeOnboardingDialog(
                onComplete = {
                    viewModel.completeOnboarding()
                    showOnboarding = false
                }
            )
        }
        if (showRealLifeDialog) {
            RealLifeExperienceDialog(
                onDismiss = { showRealLifeDialog = false },
                onSave = { context, situation, heard, intended, said, issueType ->
                    viewModel.recordRealLifeExperience(context, situation, heard, intended, said, issueType)
                    showRealLifeDialog = false
                }
            )
        }
        reviewExperience?.let { experience ->
            RealLifeReviewDialog(
                experience = experience,
                onDismiss = { reviewExperience = null },
                onComplete = { response ->
                    viewModel.completeRealLifeReview(experience, response)
                    reviewExperience = null
                }
            )
        }
    }
}

@Composable
private fun AssessmentAudioCaptureControl(
    store: LocalAssessmentAudioStore,
    fileName: String?,
    language: ProgrammeLanguage,
    onSaved: (String) -> Unit,
    onDeleted: (String) -> Unit
) {
    val context = LocalContext.current
    var recording by remember { mutableStateOf(false) }
    var playing by remember { mutableStateOf(false) }
    var player by remember { mutableStateOf<MediaPlayer?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    fun startRecording() {
        try {
            store.startRecording()
            recording = true
            error = null
        } catch (_: IOException) {
            error = language.text("Die Aufnahme konnte nicht gestartet werden.", "Recording could not be started.")
        } catch (_: IllegalStateException) {
            error = language.text("Die Aufnahme ist nicht verfügbar.", "Recording is unavailable.")
        } catch (_: SecurityException) {
            error = language.text("Mikrofonzugriff wurde verweigert.", "Microphone access was denied.")
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) startRecording()
        else error = language.text(
            "Ohne Mikrofonberechtigung kann keine Aufnahme erstellt werden.",
            "Microphone permission is required to record an answer."
        )
    }

    DisposableEffect(store, player, recording) {
        onDispose {
            if (recording) store.cancelRecording()
            player?.pause()
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(language.text(
            "Die Sprachaufnahme bleibt lokal auf diesem Gerät. Sie wird nicht exportiert oder bewertet und beim Löschen aller lokalen Daten entfernt.",
            "The speech recording stays on this device. It is not exported or scored and is removed when you delete local data."
        ), style = MaterialTheme.typography.bodySmall)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    if (recording) {
                        try {
                            onSaved(store.stopRecording())
                            recording = false
                            error = null
                        } catch (_: IllegalStateException) {
                            recording = false
                            error = language.text(
                                "Die Aufnahme konnte nicht abgeschlossen werden. Bitte erneut aufnehmen.",
                                "Recording could not be finalized. Please record again."
                            )
                        }
                    } else if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.RECORD_AUDIO
                        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                    ) {
                        startRecording()
                    } else {
                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                }
            ) {
                Text(if (recording) language.text("Aufnahme stoppen", "Stop recording")
                else language.text("Antwort aufnehmen", "Record answer"))
            }
            if (recording) {
                OutlinedButton(onClick = {
                    store.cancelRecording()
                    recording = false
                }) { Text(language.text("Verwerfen", "Discard")) }
            }
        }
        if (store.hasRecording(fileName)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = {
                    try {
                        if (playing) {
                            player?.pause()
                            playing = false
                        } else {
                            player?.release()
                            val newPlayer = MediaPlayer().apply {
                                setDataSource(store.file(requireNotNull(fileName)).absolutePath)
                                setOnCompletionListener {
                                    playing = false
                                    it.seekTo(0)
                                }
                                prepare()
                                start()
                            }
                            player = newPlayer
                            playing = true
                            error = null
                        }
                    } catch (_: IOException) {
                        playing = false
                        error = language.text("Aufnahme kann nicht wiedergegeben werden.", "Recording could not be played.")
                    } catch (_: IllegalStateException) {
                        playing = false
                        error = language.text("Aufnahme kann nicht wiedergegeben werden.", "Recording could not be played.")
                    }
                }) {
                    Text(if (playing) language.text("Pause", "Pause")
                    else language.text("Aufnahme anhören", "Play recording"))
                }
                OutlinedButton(onClick = { fileName?.let(onDeleted) }) {
                    Text(language.text("Aufnahme löschen", "Delete recording"))
                }
            }
        }
        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ProgrammeDashboard(
    preferences: ProgrammePreferences,
    attempts: List<Attempt>,
    realLifeExperiences: List<RealLifeExperience>,
    weaknessProfile: WeaknessProfile,
    diagnosticProfile: LearnerDiagnosticProfile,
    adaptiveDecision: AdaptiveControlDecision,
    now: Long,
    loading: Boolean,
    historyAvailable: Boolean,
    onMinutes: (Int) -> Unit,
    plan: List<ProgrammePlanStep>,
    onOpen: (CurriculumUnit, String) -> Unit,
    onLegacyPractice: () -> Unit,
    onLogRealLife: () -> Unit,
        onReviewRealLife: (RealLifeExperience) -> Unit,
        feedbackChallenges: List<FeedbackChallenge>,
        onResolveChallenge: (String, FeedbackChallengeStatus, String?) -> Unit,
        onRecordAssessment: (DeliveredAssessmentTask, String) -> Unit,
        bookmarkedUnitIds: Set<String>,
        onToggleBookmark: (String) -> Unit,
        onOpenListeningLab: () -> Unit,
        onOpenSpeakingLab: () -> Unit,
        onOpenConversation: () -> Unit,
        onOpenMediation: () -> Unit
) {
    val next = if (historyAvailable) AdaptiveDiagnosticRoute.nextUnit(attempts, now) else null
    val authored = CurriculumCatalogue.coreUnits
    val orientation = CurriculumCatalogue.orientationUnits
    val due = if (historyAvailable) authored.mapNotNull { unit ->
        AdaptiveDiagnosticRoute.nextReview(attempts, unit.id, now)?.takeIf { it <= now }?.let { unit to it }
    }.sortedBy { it.second } else emptyList()
    val quickStartUnit = next ?: authored.firstOrNull { it.content != null }
    val gap = if (preferences.compact) 10.dp else 18.dp
    val dueRealLife = realLifeExperiences.filter { it.nextReviewAt <= now }
    val latestRealLife = realLifeExperiences.firstOrNull()
    val pilotComparison = attempts.minOfOrNull { it.timestamp }?.let { baselineAt ->
        AssessmentDeliveryEngine.comparePilot(attempts, baselineAt, now)
    }
    var showCurriculum by rememberSaveable { mutableStateOf(false) }
    var curriculumQuery by rememberSaveable { mutableStateOf("") }
    var bookmarkedOnly by rememberSaveable { mutableStateOf(false) }
    var assessmentKind by rememberSaveable { mutableStateOf<AssessmentSessionKind?>(null) }
    var assessmentSession by remember { mutableStateOf<AssessmentSession?>(null) }
    var assessmentIndex by rememberSaveable { mutableStateOf(0) }
    var assessmentResponse by rememberSaveable { mutableStateOf("") }
    var assessmentResponses by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    val assessmentScope = rememberCoroutineScope()
    val app = LocalContext.current.applicationContext as MainApplication
    val audioStore = remember(app) { LocalAssessmentAudioStore(app) }
    val bookAudioController = remember { BookAudioPlaybackController() }
    var selectedBookAudioUri by remember(app) {
        mutableStateOf(app.bookAudioSelectionRepository.load().uri)
    }
    val persistedAssessment by app.assessmentSessionRepository.active.collectAsState(initial = null)
    val normalizedQuery = curriculumQuery.trim()
    val matchingUnits = CurriculumCatalogue.units.filter { unit ->
        val matchesQuery = normalizedQuery.isBlank() ||
            unit.title.contains(normalizedQuery, ignoreCase = true) ||
            unit.id.contains(normalizedQuery, ignoreCase = true) ||
            unit.objectives.any { it.contains(normalizedQuery, ignoreCase = true) } ||
            unit.contexts.values.any { it.contains(normalizedQuery, ignoreCase = true) }
        matchesQuery && (!bookmarkedOnly || unit.id in bookmarkedUnitIds)
    }
    val routeExpanded = showCurriculum || normalizedQuery.isNotBlank() || bookmarkedOnly
    LaunchedEffect(persistedAssessment?.session?.id) {
        val persisted = persistedAssessment ?: return@LaunchedEffect
        if (assessmentSession == null) {
            assessmentSession = persisted.session
            assessmentKind = persisted.session.kind
            assessmentIndex = persisted.currentIndex
            assessmentResponses = persisted.responses
            assessmentResponse = persisted.session.tasks.getOrNull(persisted.currentIndex)
                ?.let { persisted.responses[it.id].orEmpty() }.orEmpty()
        }
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(gap)
    ) {
        item {
            Column(
                Modifier.fillMaxWidth().background(
                    Brush.linearGradient(listOf(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.surface)),
                    RoundedCornerShape(24.dp)
                ).padding(if (preferences.compact) 18.dp else 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(preferences.language.text("DEIN NÄCHSTER GUTER SCHRITT", "YOUR NEXT GOOD STEP"),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary)
                Text(
                    if (preferences.learnerName.isBlank()) {
                        preferences.language.text("Mehr ausdrücken.\nJeden Tag ein Stück.", "Express more.\nA little every day.")
                    } else {
                        preferences.language.text(
                            "Hallo, ${preferences.learnerName.trim()}.\nHeute zählt dein nächster Schritt.",
                            "Hello, ${preferences.learnerName.trim()}.\nTake your next step today."
                        )
                    },
                    style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold
                )
                Text(preferences.language.text(
                    "Verstehen und selbst formulieren – mit einem Plan, der in deinen Tag passt.",
                    "Understand and express ideas with a plan that fits your day."
                ))
                Text(preferences.language.text(
                    "${authored.size} Kerneinheiten bereit · ${orientation.size} optionale Einstiegseinheiten",
                    "${authored.size} core units ready · ${orientation.size} optional orientation units"
                ),
                    style = MaterialTheme.typography.labelLarge)
                Text(if (historyAvailable) preferences.language.text(
                    "${AdaptiveDiagnosticRoute.completedCoreUnits(attempts, now)} / ${authored.size} Übungs-Checkpoints abgeschlossen.",
                    "${AdaptiveDiagnosticRoute.completedCoreUnits(attempts, now)} / ${authored.size} practice checkpoints completed."
                ) else preferences.language.text("Übungsverlauf wird geladen.", "Loading practice history."),
                    style = MaterialTheme.typography.bodySmall)
                quickStartUnit?.let { unit ->
                    Button(
                        onClick = { onOpen(unit, "retrieval") },
                        modifier = Modifier.fillMaxWidth().testTag("dashboard_quick_start")
                    ) {
                        Column(horizontalAlignment = Alignment.Start) {
                            Text(
                                if (next != null) preferences.language.text("Weiterüben", "Continue practice")
                                else preferences.language.text("Lektion öffnen", "Open lesson")
                            )
                            Text(unit.title, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
        item {
            ProgrammeSection("C1-Kernfertigkeiten", preferences.compact) {
                Text(
                    "Trainiere jede Fertigkeit praktisch. Aufnahmen, Notizen und Texte bleiben auf diesem Gerät; die App vergibt dabei keine C1- oder Prüfungsnote.",
                    style = MaterialTheme.typography.bodySmall
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onOpenListeningLab,
                        modifier = Modifier.testTag("dashboard_skill_listening")
                    ) { Text("Hören") }
                    OutlinedButton(
                        onClick = onOpenSpeakingLab,
                        modifier = Modifier.testTag("dashboard_skill_speaking")
                    ) { Text("Sprechen") }
                    OutlinedButton(
                        onClick = onOpenMediation,
                        modifier = Modifier.testTag("dashboard_skill_mediation")
                    ) { Text("Sprachmittlung") }
                    OutlinedButton(
                        onClick = onOpenConversation,
                        modifier = Modifier.testTag("dashboard_skill_interaction")
                    ) { Text("Interaktion") }
                }
                Text(
                    "Für Schreiben wähle im Tagesplan eine Schreibaufgabe oder starte unten eine zeitgebundene Textübung.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        item {
        ProgrammeSection(preferences.language.text("Selbstständige Textübungen", "Independent writing practice"), preferences.compact) {
            Text(
                preferences.language.text(
                    "Wähle eine kurze Abruf-, Schreib- oder Formulierungsübung aus deinem Kurs.",
                    "Choose a short retrieval, writing, or formulation task from your course."
                ),
                style = MaterialTheme.typography.bodySmall
            )
            if (assessmentSession == null) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AssessmentSessionKind.values().forEach { kind ->
                        FilterChip(
                            selected = assessmentKind == kind,
                            onClick = { assessmentKind = kind },
                            label = { Text(
                                when (kind) {
                                    AssessmentSessionKind.DELAYED_CHECKPOINT ->
                                        preferences.language.text("Abrufübung", "Retrieval practice")
                                    AssessmentSessionKind.GOETHE_STYLE ->
                                        preferences.language.text("Goethe-orientierte Textübung", "Goethe-oriented text practice")
                                    AssessmentSessionKind.PILOT_WEEK ->
                                        preferences.language.text("Text-Pilot", "Text pilot")
                                    AssessmentSessionKind.WEEKLY_INTEGRATED ->
                                        preferences.language.text("Wöchentliches Textprofil", "Weekly text session")
                                    AssessmentSessionKind.LISTENING_PRACTICE ->
                                        preferences.language.text("Hörpraxis mit eigener Aufnahme", "Listening practice with your audio")
                                    AssessmentSessionKind.SPEAKING_PRACTICE ->
                                        preferences.language.text("Sprechaufnahme", "Recorded speaking practice")
                                }
                            ) }
                        )
                    }
                }
                Button(
                    onClick = {
                        val newSession = AssessmentDeliveryEngine.createSession(
                            assessmentKind ?: AssessmentSessionKind.DELAYED_CHECKPOINT,
                            now = now
                        )
                        assessmentSession = newSession
                        assessmentIndex = 0
                        assessmentResponse = ""
                        assessmentResponses = emptyMap()
                        assessmentScope.launch {
                            app.assessmentSessionRepository.save(
                                session = newSession,
                                responses = emptyMap(),
                                currentIndex = 0,
                                completed = false,
                                now = now
                            )
                        }
                    },
                    enabled = assessmentKind != null
                ) { Text(preferences.language.text("Übung starten", "Start practice")) }
            } else {
                val session = assessmentSession!!
                val task = session.tasks.getOrNull(assessmentIndex)
                val remainingMinutes = AssessmentDeliveryEngine.remainingMinutes(session, now)
                var listenedToAudio by remember(session.id, task?.id) { mutableStateOf(false) }
                if (task == null) {
                    Text(preferences.language.text(
                        "Übung abgeschlossen. Antworten wurden als Selbstreflexion gespeichert und nicht automatisch bewertet.",
                        "Practice complete. Responses were saved as self-reflection and were not automatically scored."
                    ))
                    TextButton(onClick = {
                        assessmentSession = null
                        assessmentKind = null
                        assessmentResponses = emptyMap()
                    }) { Text(preferences.language.text("Schließen", "Close")) }
                } else {
                    val audioResponseKey = ASSESSMENT_AUDIO_RESPONSE_PREFIX + task.id
                    val audioFileName = assessmentResponses[audioResponseKey]
                    Text(preferences.language.text(
                        "${assessmentIndex + 1} / ${session.tasks.size} · " +
                            if (task.evidence == AssessmentEvidence.AUDIO_RESPONSE) "Audioantwort" else "Textantwort",
                        "${assessmentIndex + 1} / ${session.tasks.size} · " +
                            if (task.evidence == AssessmentEvidence.AUDIO_RESPONSE) "Audio response" else "Text response"
                    ))
                    Text(
                        if (remainingMinutes > 0) preferences.language.text(
                            "Verbleibende Zeit: $remainingMinutes Min.",
                            "Time remaining: $remainingMinutes min."
                        ) else preferences.language.text(
                            "Zeit abgelaufen. Diese Assessment-Antwort kann nicht mehr gespeichert werden.",
                            "Time is up. This assessment response can no longer be saved."
                        ),
                        color = if (remainingMinutes > 0) MaterialTheme.colorScheme.onSurface
                        else MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(task.prompt.take(700), style = MaterialTheme.typography.bodySmall)
                    if (task.evidence == AssessmentEvidence.AUDIO_RESPONSE) {
                        if (session.kind == AssessmentSessionKind.LISTENING_PRACTICE) {
                            BookAudioPracticeSection(
                                controller = bookAudioController,
                                onPlaybackStart = { listenedToAudio = true },
                                language = preferences.language,
                                onSelectionChanged = { selectedBookAudioUri = it }
                            )
                            OutlinedTextField(
                                value = assessmentResponse,
                                onValueChange = { assessmentResponse = it },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 4,
                                label = {
                                    Text(preferences.language.text(
                                        "Deine Hörnotizen",
                                        "Your listening notes"
                                    ))
                                }
                            )
                        } else {
                            AssessmentAudioCaptureControl(
                                store = audioStore,
                                fileName = audioFileName,
                                language = preferences.language,
                                onSaved = { newFileName ->
                                    val previous = assessmentResponses[audioResponseKey]
                                    if (previous != null && previous != newFileName) {
                                        audioStore.delete(previous)
                                    }
                                    val updated = assessmentResponses +
                                        (audioResponseKey to newFileName)
                                    assessmentResponses = updated
                                    assessmentScope.launch {
                                        app.assessmentSessionRepository.save(
                                            session = session,
                                            responses = updated,
                                            currentIndex = assessmentIndex,
                                            completed = false,
                                            now = System.currentTimeMillis()
                                        )
                                    }
                                },
                                onDeleted = {
                                    audioStore.delete(it)
                                    assessmentResponses -= audioResponseKey
                                    assessmentScope.launch {
                                        app.assessmentSessionRepository.save(
                                            session = session,
                                            responses = assessmentResponses,
                                            currentIndex = assessmentIndex,
                                            completed = false,
                                            now = System.currentTimeMillis()
                                        )
                                    }
                                }
                            )
                            OutlinedTextField(
                                value = assessmentResponse,
                                onValueChange = { assessmentResponse = it },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 2,
                                label = {
                                    Text(preferences.language.text(
                                        "Notiz zur Aufnahme (optional)",
                                        "Recording note (optional)"
                                    ))
                                }
                            )
                        }
                    } else {
                        OutlinedTextField(
                            value = assessmentResponse,
                            onValueChange = { assessmentResponse = it },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 4,
                            label = {
                                Text(preferences.language.text("Deine Textantwort", "Your text response"))
                            }
                        )
                    }
                    val canSubmit = when {
                        task.evidence != AssessmentEvidence.AUDIO_RESPONSE ->
                            assessmentResponse.isNotBlank()
                        session.kind == AssessmentSessionKind.LISTENING_PRACTICE ->
                            selectedBookAudioUri != null && listenedToAudio && assessmentResponse.isNotBlank()
                        else -> audioStore.hasRecording(audioFileName)
                    }
                    Button(
                        onClick = {
                            if (assessmentResponse.isNotBlank()) {
                                onRecordAssessment(task, assessmentResponse)
                            }
                            val updatedResponses = if (assessmentResponse.isNotBlank()) {
                                assessmentResponses + (task.id to assessmentResponse.trim())
                            } else {
                                assessmentResponses
                            }
                            val nextIndex = assessmentIndex + 1
                            assessmentResponses = updatedResponses
                            assessmentScope.launch {
                                app.assessmentSessionRepository.save(
                                    session = session,
                                    responses = updatedResponses,
                                    currentIndex = nextIndex,
                                    completed = nextIndex >= session.tasks.size,
                                    now = now
                                )
                            }
                            assessmentResponse = ""
                            assessmentIndex = nextIndex
                        },
                        enabled = remainingMinutes > 0 && canSubmit
                    ) {
                        Text(if (assessmentIndex + 1 == session.tasks.size) {
                            preferences.language.text("Abschließen", "Finish")
                        } else {
                            preferences.language.text("Antwort speichern", "Save response")
                        })
                    }
                }
            }
            val openChallengeCount = feedbackChallenges.count { it.status == FeedbackChallengeStatus.OPEN }
            if (openChallengeCount > 0) {
                Text(
                    "$openChallengeCount Feedback-Herausforderung(en) gespeichert.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        }
        item {
            ProgrammeSection("Adaptive Rückmeldung prüfen", preferences.compact) {
                Text(
                    "Automatische Hinweise stammen aus wenigen lokalen Aufgaben- und Textmustern. Sie helfen bei der nächsten Übung, bewerten aber nicht zuverlässig dein gesamtes Deutsch.",
                    style = MaterialTheme.typography.bodySmall
                )
                Text("Feedback-Herausforderungen: ${feedbackChallenges.count { it.status == FeedbackChallengeStatus.OPEN }} offen",
                    style = MaterialTheme.typography.labelMedium)
                feedbackChallenges.filter { it.status == FeedbackChallengeStatus.OPEN }.take(5).forEach { challenge ->
                    Surface(color = MaterialTheme.colorScheme.secondaryContainer, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Offene Herausforderung · ${challenge.unitId.ifBlank { "Legacy" }} · ${challenge.claimType.ifBlank { challenge.claimId }}",
                                fontWeight = FontWeight.SemiBold)
                            Text(
                                if (challenge.responseExcerpt.isBlank()) {
                                    "Für diesen älteren Eintrag ist kein Antwortausschnitt gespeichert."
                                } else {
                                    "Beleg aus deiner Antwort: „${challenge.responseExcerpt}“"
                                },
                                style = MaterialTheme.typography.bodySmall
                            )
                            if (challenge.proposedFeedback.isNotBlank()) {
                                Text("Automatischer Hinweis: ${challenge.proposedFeedback}",
                                    style = MaterialTheme.typography.bodySmall)
                            }
                            Text("Entscheidung: ${challenge.learnerDecision ?: "keine"}", style = MaterialTheme.typography.bodySmall)
                            challenge.correctedFeedback?.takeIf { it.isNotBlank() }?.let {
                                Text("Deine Korrektur: $it", style = MaterialTheme.typography.bodySmall)
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                TextButton(onClick = {
                                    onResolveChallenge(challenge.id, FeedbackChallengeStatus.ACCEPTED, challenge.correctedFeedback)
                                }) { Text("Hinweis übernehmen") }
                                TextButton(onClick = {
                                    onResolveChallenge(challenge.id, FeedbackChallengeStatus.REJECTED, challenge.correctedFeedback)
                                }) { Text("Hinweis verwerfen") }
                                TextButton(onClick = {
                                    onResolveChallenge(challenge.id, FeedbackChallengeStatus.NEEDS_REASSESSMENT, challenge.correctedFeedback)
                                }) { Text("Weiter prüfen") }
                            }
                        }
                    }
                }
            }
        }
        item {
        ProgrammeSection("Vier-Wochen-Pilot", preferences.compact) {
            if (pilotComparison == null) {
                Text(
                    "Nach dem ersten gespeicherten Antwortversuch wird hier der Ausgangspunkt für den Pilotvergleich angelegt.",
                    style = MaterialTheme.typography.bodySmall
                )
            } else {
                Text(
                    "Der Vergleich zählt gespeicherte Evidenz, keine erfundenen Kompetenzpunkte.",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    "Antworten: ${pilotComparison.baselineResponses} Ausgangspunkt · " +
                        "${pilotComparison.finalResponses} danach"
                )
                Text(
                    "Unabhängig: ${pilotComparison.baselineIndependent} → " +
                        "${pilotComparison.finalIndependent}"
                )
                Text(
                    "Unbekannter Transfer: ${pilotComparison.baselineUnfamiliar} → " +
                        "${pilotComparison.finalUnfamiliar}"
                )
            }
        }
        }
        item {
            ProgrammeSection("Dein echter Alltag", preferences.compact) {
                Text(
                    when {
                        latestRealLife == null -> "Notiere eine Situation, in der dir Deutsch heute gefehlt hat. Daraus entsteht dein persönlicher Reparaturschritt."
                        dueRealLife.isNotEmpty() -> "${dueRealLife.size} persönlicher Reparaturschritt fällig · Kontext: ${contextLabel(dueRealLife.first().context)}"
                        else -> "Letzter Eintrag: ${contextLabel(latestRealLife.context)} · nächste Variation ist geplant."
                    },
                    style = MaterialTheme.typography.bodySmall
                )
                Button(onClick = onLogRealLife, modifier = Modifier.fillMaxWidth()) {
                    Text("Heute mit Deutsch Schwierigkeiten gehabt?")
                }
                dueRealLife.take(2).forEach { experience ->
                    val repair = RealLifeRepairEngine.create(experience)
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(experience.situation, fontWeight = FontWeight.SemiBold)
                                Text(
                                    "${contextLabel(experience.context)} · ${experience.issueType.label}",
                                    style = MaterialTheme.typography.labelSmall
                                )
                                Text(
                                    "Sage es jetzt neu und übertrage es morgen in eine andere Situation.",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(repair.primaryPhrase, fontWeight = FontWeight.SemiBold)
                                Text(repair.transferPrompt, style = MaterialTheme.typography.bodySmall)
                            }
                            TextButton(onClick = { onReviewRealLife(experience) }) {
                                Text("Jetzt üben")
                            }
                        }
                    }
                }
            }
        }
        if (weaknessProfile.summaries.isNotEmpty()) {
            item {
                ProgrammeSection("Deine wiederkehrenden Lernsignale", preferences.compact) {
                    Text(
                        "Diese Hinweise steuern deine nächsten Übungen. Sie sind keine Kompetenz- oder CEFR-Werte.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    weaknessProfile.top(4).forEach { weakness ->
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(weakness.signal.label, fontWeight = FontWeight.SemiBold)
                            Text(
                                "${weakness.sampleCount} Hinweis(e) · ${weakness.confidence.label} · " +
                                    weakness.contexts.joinToString { contextLabel(it) },
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                "Letzter Hinweis: ${weakness.latestEvidence.excerpt}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
        if (diagnosticProfile.findings.isNotEmpty()) {
            item {
                ProgrammeSection("Was deine Antworten zeigen", preferences.compact) {
                    Text(
                        "Quizfragen werden objektiv geprüft; wenige bekannte Muster können Text-Reparaturen auslösen. Saubere Texte beweisen keine Kompetenz, Sprechen und echtes Hören bleiben unbeurteilt.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    diagnosticProfile.findings.take(6).forEach { finding ->
                        Text(
                            "${finding.dimension.label}: ${finding.status.label}",
                            fontWeight = FontWeight.SemiBold,
                            color = if (finding.status == DiagnosticStatus.FRAGILE)
                                MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            "${finding.sampleCount} Beobachtung(en) · ${finding.confidence.label}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
        item {
            ProgrammeSection("Dein Tagesplan", preferences.compact) {
                Text("Coach-Entscheidung", style = MaterialTheme.typography.titleSmall)
                Text(adaptiveDecision.reason, fontWeight = FontWeight.SemiBold)
                Text(
                    "Priorität: ${adaptiveDecision.primaryTaskId ?: "Kernpfad"} · " +
                        "${adaptiveDecision.confidence.label}",
                    style = MaterialTheme.typography.bodySmall
                )
                adaptiveDecision.dialogueBriefId?.let { briefId ->
                    OwnedDialogueLibrary.briefs.firstOrNull { it.id == briefId }?.let { brief ->
                        Text(
                            "Zusatzfokus: ${brief.situation}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(20, 60, 90).forEach { minutes ->
                        FilterChip(selected = preferences.dailyMinutes == minutes,
                            onClick = { onMinutes(minutes) }, label = { Text("$minutes Min.") })
                    }
                }
                Text("${preferences.dailyMinutes / 2} Min. rezeptiv · ${preferences.dailyMinutes / 2} Min. produktiv",
                    color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge)
                Text("Zeitbudget, kein gemessener Timer. Freies Sprechen und Dialog übst du außerhalb der App; hier ist keine Aufnahme verfügbar.",
                    style = MaterialTheme.typography.bodySmall)
                if (loading) Text("Lernverlauf wird geladen …")
                else if (!historyAvailable) Text("Lernverlauf nicht verfügbar. Keine Empfehlung aus unbekannten Daten.")
                else if (next == null) Text("Alle aktuellen Übungs-Checkpoints sind abgeschlossen. Du kannst jede Einheit weiterhin frei öffnen.")
                else {
                    Text("${if (due.any { it.first.id == next.id }) "Fälliger Diagnose-Abruf" else "Nächster Diagnose-Schritt"}: ${next.title}",
                        fontWeight = FontWeight.SemiBold)
                    plan.forEach { step ->
                        Row(
                            Modifier.fillMaxWidth()
                                .padding(vertical = if (preferences.compact) 8.dp else 12.dp)
                                .clickable(role = Role.Button) { onOpen(next, step.taskId) }
                        ) {
                            Text(step.title, Modifier.weight(1f))
                            Text("${step.minutes} Min.  →", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                    Button(onClick = { onOpen(next, "retrieval") }, modifier = Modifier.fillMaxWidth()) {
                        Text("Mit Abruf starten")
                    }
                }
            }
        }
            item {
                val today = now / MasteryEngine.DAY_MILLIS
                val todaySteps = attempts.count { it.timestamp / MasteryEngine.DAY_MILLIS == today }
                    .coerceAtMost(3)
                ProgrammeSection("Heute schon geschafft", preferences.compact) {
                    Text("$todaySteps / 3 Lernschritte heute", fontWeight = FontWeight.SemiBold)
                    androidx.compose.material3.LinearProgressIndicator(
                        progress = { todaySteps / 3f },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        when (todaySteps) {
                            0 -> "Ein kleiner Start reicht: öffne deinen nächsten Schritt."
                            1, 2 -> "Du bist unterwegs. Noch ein kurzer Schritt hält den Rhythmus."
                            else -> "Tagesziel erreicht. Morgen wartet der nächste kleine Fortschritt."
                        },
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        item {
            ProgrammeSection("Wiederholen, was bleiben soll", preferences.compact) {
                if (!historyAvailable) Text("Fälligkeiten noch unbekannt.")
                else if (due.isEmpty()) Text("Heute kein Abruf fällig. Neue Antworten legen echte Wiederholungstermine an.")
                due.forEach { (unit, timestamp) ->
                    TextButton(onClick = { onOpen(unit, "retrieval") }) {
                        Text("${unit.title} · fällig seit ${dateLabel(timestamp)} →")
                    }
                }
                Text("Fälligkeiten folgen Wiederholungen und erkannten Mustern. Sie sind Lernplanung, kein Kompetenznachweis.",
                    style = MaterialTheme.typography.bodySmall)
            }
        }
        item {
            ProgrammeSection("Was die automatische Diagnose wirklich misst", preferences.compact) {
                Text(if (historyAvailable) "${attempts.count { it.source == EvidenceSource.AUTOMATIC_QUIZ }} objektive Quizantworten · " +
                    "${attempts.count { it.source == EvidenceSource.SELF_REFLECTION }} Selbstreflexionen"
                    else "Lernverlauf unbekannt – noch keine belastbare Übersicht.")
                Dimension.values().forEach { dimension ->
                    val measured = attempts.filter {
                        it.source == EvidenceSource.AUTOMATIC_QUIZ && it.scores[dimension] in 0..4
                    }
                        .maxByOrNull { it.timestamp }
                    Text("${dimensionLabel(dimension)}: " + when {
                        !historyAvailable -> "Nachweisstatus unbekannt"
                        measured == null && dimension == Dimension.COMPREHENSION -> "noch keine objektive Quizfrage beantwortet"
                        measured == null -> "nicht automatisch messbar"
                        else -> "${measured.scores[dimension]}/4 · einzelne Quizfrage, kein Gesamturteil"
                    },
                        style = MaterialTheme.typography.bodySmall)
                }
                Text("Bekannte Fehler können Hinweise auslösen. Keine erkannte Regelverletzung ist kein Beleg für korrekte oder C1-gerechte Sprache.",
                    style = MaterialTheme.typography.bodySmall)
            }
        }
        item {
            Text("Deine Stufenkarte", style = MaterialTheme.typography.headlineSmall)
            Text("28 Kompetenzen · 7 Stufen. Ausgearbeitet bedeutet nicht automatisch freigeschaltet.",
                style = MaterialTheme.typography.bodySmall)
        }
        item {
            ProgrammeSection(preferences.language.text("Deine Lernroute", "Your learning path"), preferences.compact) {
                Text(preferences.language.text(
                    "Suche nach Thema oder Kontext und speichere wichtige Einheiten für später.",
                    "Search by topic or context, and bookmark units to revisit."
                ),
                    style = MaterialTheme.typography.bodySmall)
                OutlinedTextField(
                    value = curriculumQuery,
                    onValueChange = { curriculumQuery = it },
                    modifier = Modifier.fillMaxWidth().testTag("curriculum_search"),
                    singleLine = true,
                    label = {
                        Text(preferences.language.text("Einheiten suchen", "Search units"))
                    },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                    trailingIcon = {
                        if (curriculumQuery.isNotEmpty()) {
                            IconButton(
                                onClick = { curriculumQuery = "" },
                                modifier = Modifier.testTag("curriculum_search_clear")
                            ) {
                                Icon(
                                    Icons.Filled.Clear,
                                    contentDescription = preferences.language.text("Suche löschen", "Clear search")
                                )
                            }
                        }
                    }
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = !bookmarkedOnly,
                        onClick = { bookmarkedOnly = false },
                        label = { Text(preferences.language.text("Alle", "All")) }
                    )
                    FilterChip(
                        selected = bookmarkedOnly,
                        onClick = { bookmarkedOnly = !bookmarkedOnly },
                        label = { Text(preferences.language.text("Gespeichert", "Bookmarked")) },
                        modifier = Modifier.testTag("curriculum_bookmarks_filter")
                    )
                }
                Text("${matchingUnits.size} / ${CurriculumCatalogue.units.size}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (normalizedQuery.isBlank() && !bookmarkedOnly) {
                    OutlinedButton(onClick = { showCurriculum = !showCurriculum }) {
                        Text(if (showCurriculum) {
                            preferences.language.text("Lernroute einklappen", "Collapse learning path")
                        } else {
                            preferences.language.text("Alle Einheiten anzeigen", "Show all units")
                        })
                    }
                }
            }
        }
        if (routeExpanded && matchingUnits.isEmpty()) {
            item(key = "empty-curriculum-search") {
                Text(
                    preferences.language.text(
                        "Keine passenden Einheiten. Ändere die Suche oder speichere eine Einheit.",
                        "No matching units. Try another search or bookmark a unit."
                    ),
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        if (routeExpanded) CurriculumCatalogue.stages.forEachIndexed { stage, title ->
            val stageUnits = matchingUnits.filter { it.stage == stage }
            if (stageUnits.isNotEmpty()) {
            item(key = "stage-$stage") {
                Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            }
            items(stageUnits, key = { it.id }) { unit ->
                val checkpoint = AdaptiveDiagnosticRoute.evaluate(unit, attempts, now)
                val unlocked = historyAvailable && AdaptiveDiagnosticRoute.isUnlocked(unit, attempts, now)
                val status = when {
                    unit.content == null -> "ENTWURF · keine Lektion"
                    !historyAvailable -> "AUSGEARBEITET · Diagnose noch unbekannt"
                    checkpoint.state == DiagnosticCheckpointState.COMPLETE -> "ÜBUNGS-CHECKPOINT ERLEDIGT"
                    checkpoint.state == DiagnosticCheckpointState.NEEDS_REMEDIATION -> "ADAPTIVE WIEDERHOLUNG · bekannte Hinweise"
                    unlocked -> "NÄCHSTER DIAGNOSE-SCHRITT"
                    else -> "FREI ÜBEN · Route wartet auf Checkpoints"
                }
                val objectivePreview = unit.objectives.firstOrNull() ?: "Zielbeschreibung noch offen."
                val isBookmarked = unit.id in bookmarkedUnitIds
                Card(
                    onClick = { onOpen(unit, "retrieval") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
                                        Color.Transparent,
                                        MaterialTheme.colorScheme.surface
                                    )
                                )
                            )
                            .padding(if (preferences.compact) 14.dp else 18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier
                                    .padding(end = 12.dp)
                                    .sizeIn(minWidth = 54.dp, minHeight = 54.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                    Text(
                                        "${unit.stage + 1}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                            Column(
                                Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    status,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (unlocked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(unit.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                                Text(
                                    objectivePreview,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    ContextTrack.values().forEach { track ->
                                        val text = unit.contexts[track]?.takeIf { it.isNotBlank() } ?: return@forEach
                                        Surface(
                                            shape = RoundedCornerShape(999.dp),
                                            color = MaterialTheme.colorScheme.surfaceVariant,
                                            tonalElevation = 0.dp
                                        ) {
                                            Text(
                                                text = contextLabel(track),
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                            IconButton(
                                onClick = { onToggleBookmark(unit.id) },
                                modifier = Modifier.testTag("bookmark_${unit.id}")
                            ) {
                                Icon(
                                    imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                    contentDescription = if (isBookmarked) {
                                        preferences.language.text("Lesezeichen entfernen", "Remove bookmark")
                                    } else {
                                        preferences.language.text("Einheit speichern", "Bookmark unit")
                                    },
                                    tint = if (isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
            }
        }
        item {
            ProgrammeSection("Formaler C1-Nachweis (getrennt vom Übungspfad)", preferences.compact) {
                if (historyAvailable) {
                                    MasteryEngine.graduationGaps(attempts).forEach { Text("• $it", style = MaterialTheme.typography.bodySmall) }
                                } else Text("Nachweislücken können erst mit verfügbarem Lernverlauf bestimmt werden.")
                Text("Der adaptive Übungspfad ist keine formale C1-Prüfung und erhebt keinen Anspruch auf Zertifizierung.",
                    fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall)
                OutlinedButton(onClick = onLegacyPractice) { Text("Freie Übungstools öffnen") }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun UnitDetail(
    unit: CurriculumUnit,
    attempts: List<Attempt>,
    realLifeExperiences: List<RealLifeExperience>,
    compact: Boolean,
    initialTask: String,
    now: Long,
    unitAttempts: List<Attempt>,
    enabled: Boolean,
    saving: Boolean,
    result: ProgrammeQuizResult?,
    assisted: Boolean,
    onReveal: () -> Unit,
    onQuiz: (Int?, ContextTrack) -> Unit,
    onSave: (String, String, ContextTrack) -> Unit,
    onChallengeFeedback: (FeedbackClaim, FeedbackChallengeDecision, String?) -> Unit,
    humanReviews: List<HumanReviewSubmission>,
    onRecordHumanReview: (String, String, String, Map<Dimension, Int>) -> Unit,
    existingFeedbackClaimIds: Set<String>,
    onOpenListeningLab: () -> Unit,
    onOpenSpeakingLab: () -> Unit,
    onOpenConversation: () -> Unit,
    onAddExpressions: () -> Unit
) {
    var contextName by rememberSaveable(unit.id) { mutableStateOf(ContextTrack.GENERAL.name) }
    val context = ContextTrack.values().first { it.name == contextName }
    val content = unit.content
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var showModel by rememberSaveable(unit.id) { mutableStateOf(false) }
    var showFeedback by rememberSaveable(unit.id) { mutableStateOf(false) }
    var selectedClaimId by rememberSaveable(unit.id) { mutableStateOf<String?>(null) }
    var challengeCorrection by rememberSaveable(unit.id) { mutableStateOf("") }
    var reviewAttemptId by rememberSaveable(unit.id) { mutableStateOf<String?>(null) }
    var reviewName by rememberSaveable(unit.id) { mutableStateOf("") }
    var reviewComment by rememberSaveable(unit.id) { mutableStateOf("") }
    var reviewScores by remember(unit.id) { mutableStateOf<Map<Dimension, Int>>(emptyMap()) }
    val practiceTasks = listOf(
        "retrieval" to "Abruf",
        "reading" to "Lesen",
        "listening" to "Hören",
        "quiz" to "Verständnischeck",
        "controlled" to "Sprachmittel",
        "speaking" to "Sprechen",
        "interaction" to "Dialog",
        "writing" to "Schreiben",
        "transfer" to "Transfer"
    )
    val completedTasks = unitAttempts.map { it.taskId }.toSet()
    val latestQuiz = unitAttempts.filter { it.taskId == "quiz" }.maxByOrNull { it.timestamp }
    val adaptive = remember(unit.id, attempts, realLifeExperiences, now) {
        AdaptiveLearning.recommend(unit, attempts, now, realLifeExperiences)
    }
    val selectedClaim = adaptive.feedbackClaims.firstOrNull { it.id == selectedClaimId }
    val taskIndexes = mapOf("retrieval" to 3, "reading" to 4, "listening" to 5, "quiz" to 7,
        "controlled" to 8, "speaking" to 9, "interaction" to 10, "writing" to 11, "transfer" to 12)
    LaunchedEffect(unit.id, initialTask) {
        if (content != null) listState.scrollToItem(taskIndexes[initialTask] ?: 0)
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(), state = listState,
        contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(if (compact) 10.dp else 18.dp)
    ) {
        item {
            ProgrammeSection(unit.title, compact) {
                Text(CurriculumCatalogue.stages[unit.stage], color = MaterialTheme.colorScheme.primary)
                Text(when {
                    content == null -> "Nur Entwurf: keine durchführbare Lektion und kein Kompetenznachweis."
                    enabled && AdaptiveDiagnosticRoute.isUnlocked(unit, attempts, now) -> "Als nächster Schritt im adaptiven Übungspfad bereit."
                    enabled -> "Freies Üben möglich. Der Diagnosepfad wartet auf frühere Übungs-Checkpoints."
                    else -> "Lernverlauf noch nicht verfügbar. Inhalte bleiben lesbar."
                })
                if (content != null && !AdaptiveDiagnosticRoute.isUnlocked(unit, attempts, now)) {
                    val required = (unit.prerequisites + CurriculumCatalogue.units.filter {
                        it.content != null && it.stage < unit.stage
                    }.map { it.id }).distinct()
                    Text("Voraussetzungen: " + if (required.isEmpty()) "Lernverlauf muss verfügbar sein."
                        else required.joinToString { id -> CurriculumCatalogue.units.find { it.id == id }?.title ?: id },
                        style = MaterialTheme.typography.bodySmall)
                }
                if (assisted) Text("Modellhilfe bereits geöffnet: Weitere Antworten dieser Einheit werden konservativ als assistiert markiert (auch nach Neustart).",
                    style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                TextButton(onClick = { scope.launch { listState.animateScrollToItem(0) } }) { Text("Einheitsübersicht") }
            }
        }
        item {
            ProgrammeSection("Das lernst du", compact) {
                unit.objectives.forEach { Text("• $it") }
            }
        }
        item {
            ProgrammeSection("Vier Kontexte · wähle deinen Fokus", compact) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ContextTrack.values().forEach { track ->
                        FilterChip(selected = context == track, onClick = { contextName = track.name },
                            label = { Text(contextLabel(track)) })
                    }
                }
                ContextTrack.values().forEach { track ->
                    Text("${contextLabel(track)}: ${unit.contexts[track] ?: "Kein Kontext verfasst"}")
                }
                Text("Gespeicherte Antworten tragen den gewählten Kontext. Ein Kontextwechsel allein ist kein Transfernachweis.",
                    style = MaterialTheme.typography.bodySmall)
                if (content != null) {
                    HorizontalDivider()
                    Text("Solo-Anwendung im gewählten Kontext", fontWeight = FontWeight.SemiBold)
                    Text(
                        "Nutze ein Lernziel aus dieser Einheit in diesem Szenario. Schreibe drei bis fünf Sätze; prüfe selbst Ziel, Adressat, Register und eine offene oder unsichere Stelle.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(unit.contexts.getValue(context), style = MaterialTheme.typography.bodySmall)
                    ResponseField(
                        unitId = unit.id,
                        task = "context-application",
                        label = "Neue Formulierung für ${contextLabel(context)}",
                        context = context,
                        enabled = enabled,
                        saving = saving,
                        onSave = onSave,
                        taskDescription = ContextApplicationTask.build(unit, context)
                    )
                }
            }
        }
        item {
            ProgrammeSection("Dein Übungsfortschritt", compact) {
                Text(
                    "${practiceTasks.count { it.first in completedTasks }} / ${practiceTasks.size} Übungsarten gespeichert",
                    fontWeight = FontWeight.SemiBold
                )
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    practiceTasks.forEach { (taskId, label) ->
                        FilterChip(
                            selected = taskId in completedTasks,
                            onClick = {},
                            enabled = false,
                            label = { Text(if (taskId in completedTasks) "✓ $label" else label) }
                        )
                    }
                }
                Text(
                    "Gespeichert bedeutet bearbeitet, nicht automatisch richtig oder menschlich beurteilt.",
                    style = MaterialTheme.typography.bodySmall
                )
                if (latestQuiz?.response?.startsWith("Option") == true &&
                    latestQuiz.errors.isNotEmpty()
                ) {
                    Text(
                        "Nacharbeit empfohlen: Bearbeite den Verständnischeck erneut und erkläre anschließend, warum die richtige Antwort zum Text passt.",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
        item {
            ProgrammeSection("Dein adaptiver nächster Schritt", compact) {
                Text(
                    when (adaptive.difficulty) {
                        AdaptiveDifficulty.SUPPORT -> "REPARATUR · erst Sicherheit aufbauen"
                        AdaptiveDifficulty.CORE -> "KERNPFAD · gezielt weiterüben"
                        AdaptiveDifficulty.CHALLENGE -> "TRANSFER · neue Anforderung versuchen"
                    },
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(adaptive.reason, style = MaterialTheme.typography.bodySmall)
                adaptive.errors.forEach { error ->
                    Text("${error.label}: ${error.explanation}", style = MaterialTheme.typography.bodySmall)
                    Text("Nächste Reparatur: ${error.remediation}", style = MaterialTheme.typography.bodySmall)
                }
                adaptive.feedbackClaims.forEach { claim ->
                    val sourceAttempt = attempts.firstOrNull { it.id == claim.attemptId }
                    val validation = sourceAttempt?.let {
                        FeedbackEvidenceEngine.validate(it.response, claim)
                    }
                    if (validation?.valid == true) {
                        Text("Beleg: „${claim.responseExcerpt}“", style = MaterialTheme.typography.bodySmall)
                        Text("Automatischer Hinweis: ${claim.proposedFeedback}",
                            style = MaterialTheme.typography.bodySmall)
                        if (claim.id in existingFeedbackClaimIds) {
                            Text("Dieser Hinweis ist bereits zur Prüfung vorgemerkt.",
                                style = MaterialTheme.typography.labelSmall)
                        } else {
                            OutlinedButton(onClick = {
                                challengeCorrection = ""
                                selectedClaimId = claim.id
                            }) { Text("Hinweis prüfen oder anfechten") }
                        }
                    }
                }
                Text("Empfehlung: ${adaptive.nextStep}", fontWeight = FontWeight.SemiBold)
                Text(
                    "Automatische Hinweise sind lokale Musterchecks und keine Benotung. Für Kompetenznachweise zählt weiterhin nur unabhängige menschliche Beurteilung.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        if (content != null) {
            item {
                PracticeSection(unit.id, "retrieval", "01 / Abruf vor dem Nachlesen",
                    "Schließe deine bisherigen Vorlagen. Erkläre aus dem Gedächtnis, was du zu diesem Lernziel bereits sagen kannst. Notiere eine offene Frage. Wenn es dein erster Versuch ist, halte fest, was du noch nicht weißt.",
                    context, enabled, saving, compact, onSave)
            }
            item {
                ProgrammeSection("02 / Lesen", compact) {
                    Text(content.reading)
                    ResponseField(
                        unit.id, "reading", "Gib die Kernaussagen in eigenen Worten wieder. Was bleibt unklar?",
                        context, enabled, saving, onSave,
                        taskDescription = "Lektion: ${unit.title}\nLesetext:\n${content.reading}\n" +
                            "Aufgabe: Fasse die Kernaussagen in eigenen Worten zusammen und benenne, was unklar bleibt."
                    )
                }
            }
            item {
                ProgrammeSection("03 / Hören · synthetische Stimme", compact) {
                    SyntheticListening(content.listening)
                    Text("Das sichtbare Transkript kann das Hörverstehen unterstützen; dies ist kein unabhängiger Hörtest.",
                        style = MaterialTheme.typography.bodySmall)
                    OutlinedButton(onClick = onOpenListeningLab) { Text("Hörlabor öffnen · eigene Buchaufnahmen") }
                    ResponseField(
                        unit.id, "listening", "Was hast du verstanden? Notiere auch, ob du das Transkript verwendet hast.",
                        context, enabled, saving, onSave,
                        taskDescription = "Lektion: ${unit.title}\nSynthetisches Hörskript (kein Audio-Beleg):\n${content.listening}\n" +
                            "Aufgabe: Vergleiche die Antwort nur mit diesem Skript. Behaupte nicht, dass dadurch die tatsächliche Hörleistung geprüft wurde."
                    )
                }
            }
            item {
                LessonListeningResources(unit.id, onOpenListeningLab)
            }
            item {
                ProgrammeSection("Sprachmittel & Grammatik", compact) {
                    content.expressions.forEach { Text("• $it") }
                    OutlinedButton(
                        onClick = onAddExpressions,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Kursausdrücke ins aktive Abruftraining übernehmen")
                    }
                    HorizontalDivider()
                    Text(content.grammar)
                }
            }
            item {
                QuizSection(unit, context, result, enabled, saving, onReveal, onQuiz, compact)
            }
            item {
                ProgrammeSection("05 / Kontrolliert formulieren", compact) {
                    Text(content.controlledPrompt)
                    ResponseField(
                        unit.id, "controlled", "Deine Formulierung",
                        context, enabled, saving, onSave,
                        taskDescription = "Lektion: ${unit.title}\nAufgabe: ${content.controlledPrompt}\n" +
                            "Lernziel: ${unit.objectives.joinToString("; ")}"
                    )
                    OutlinedButton(onClick = { onReveal(); showModel = true }) { Text("Modell öffnen · zählt als Hilfe") }
                    if (showModel) Text(content.controlledModel)
                }
            }
            item {
                PracticeSection(unit.id, "speaking", "06 / Frei sprechen", content.speaking,
                    context, enabled, saving, compact, onSave,
                    "Sprich laut außerhalb der App. Hier sind weder Aufnahme noch Spracherkennung implementiert. Speichere danach dein Skript oder deine Selbstreflexion, keinen vermeintlichen Audio-Nachweis.",
                    onOpenTool = onOpenSpeakingLab,
                    toolLabel = "Sprechlabor öffnen")
            }
            item {
                PracticeSection(unit.id, "interaction", "07 / Im Dialog handeln", content.interaction,
                    context, enabled, saving, compact, onSave,
                    "Übe mit einer realen Person oder wechsle selbst die Rollen. Kein KI-Gesprächspartner, keine Aufnahme und keine automatische Interaktionsbewertung. Notiere den Dialog oder reflektiere ihn ehrlich.",
                    onOpenTool = onOpenConversation,
                    toolLabel = "Dialogsimulator öffnen")
            }
            item {
                PracticeSection(unit.id, "writing", "08 / Schreiben", content.writing,
                    context, enabled, saving, compact, onSave)
            }
            item {
                PracticeSection(unit.id, "transfer", "Transfer · neuer Kontext", content.transfer,
                    context, enabled, saving, compact, onSave,
                    "Bearbeite die Situation ohne Modell und prüfe sie anschließend selbst. Optional kannst du den Text bewusst an eine externe KI teilen; dieses Feedback bleibt ungeprüft und ist kein unabhängiges Urteil. Weil der Impuls hier sichtbar und wiederholbar ist, gilt diese Eingabe nicht als verifizierter unbekannter Test.")
            }
            item {
                ProgrammeSection("Modellrückmeldung · kein Urteil über dich", compact) {
                    Text("Dieser Text ist ein verfasstes Feedbackbeispiel, keine Analyse deiner Eingabe.")
                    OutlinedButton(onClick = { onReveal(); showFeedback = true }) { Text("Beispiel öffnen · zählt als Hilfe") }
                    if (showFeedback) Text(content.feedback)
                }
            }
            item { RubricSection(compact) }
            item {
                ProgrammeSection("Gespeicherter Verlauf dieser Einheit", compact) {
                    val history = attempts.filter { it.unitId == unit.id }.sortedByDescending { it.timestamp }
                    if (history.isEmpty()) Text("Noch keine gespeicherte Antwort. Unbeurteilt ist nicht gleich null Punkte.")
                    history.forEach { attempt ->
                        Text("${dateLabel(attempt.timestamp)} · ${attempt.taskId} · ${contextLabel(attempt.context)}",
                            style = MaterialTheme.typography.labelLarge)
                        Text(when (attempt.source) {
                            EvidenceSource.SELF_REFLECTION -> "Selbstreflexion · kein Kompetenzscore"
                            EvidenceSource.AUTOMATIC_QUIZ -> "Automatisch ausgewertete Verständnisfrage · kein Gesamturteil"
                            EvidenceSource.HUMAN_REVIEW -> "Extern erfasstes menschliches Fremdurteil"
                        }, style = MaterialTheme.typography.bodySmall)
                        if (attempt.assisted) Text("Mit Modellhilfe / assistiert", style = MaterialTheme.typography.bodySmall)
                        Text(attempt.response)
                        attempt.scores.forEach { (dimension, score) ->
                            Text("${dimensionLabel(dimension)}: ${if (score in 0..4) "$score/4" else "ungültig / unbeurteilt"}",
                                style = MaterialTheme.typography.bodySmall)
                        }
                        TextButton(onClick = {
                            reviewAttemptId = attempt.id
                            reviewName = ""
                            reviewComment = ""
                            reviewScores = emptyMap()
                        }) { Text("Lokale Review-Notiz erfassen") }
                        humanReviews.filter { it.attemptId == attempt.id }.forEach { review ->
                            Text(
                                "Lokale Review-Notiz · ${review.reviewer.displayName} · nicht verifiziert",
                                style = MaterialTheme.typography.labelMedium
                            )
                            Text(review.scores.entries.joinToString(" · ") {
                                "${dimensionLabel(it.key)} ${it.value}/4"
                            }, style = MaterialTheme.typography.bodySmall)
                            review.comment.takeIf { it.isNotBlank() }?.let {
                                Text(it, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        HorizontalDivider()
                    }
                }
            }
        } else {
            item {
                ProgrammeSection("Noch nicht ausgearbeitet", compact) {
                    Text("Für diesen Karteneintrag fehlen Lesetext, Hörskript, Aufgaben und Bewertungsevidenz. Es werden weder Übungen noch Resultate erfunden.")
                }
            }
        }
    }
    selectedClaim?.let { claim ->
        FeedbackChallengeDialog(
            claim = claim,
            sourceAttempt = attempts.firstOrNull { it.id == claim.attemptId },
            correction = challengeCorrection,
            onCorrectionChange = { challengeCorrection = it },
            onDecision = { decision ->
                onChallengeFeedback(claim, decision, challengeCorrection)
                selectedClaimId = null
                challengeCorrection = ""
            },
            onDismiss = {
                selectedClaimId = null
                challengeCorrection = ""
            }
        )
    }
    reviewAttemptId?.let { attemptId ->
        attempts.firstOrNull { it.id == attemptId }?.let { sourceAttempt ->
            HumanReviewEntryDialog(
                attempt = sourceAttempt,
                reviewerName = reviewName,
                onReviewerNameChange = { reviewName = it },
                comment = reviewComment,
                onCommentChange = { reviewComment = it },
                scores = reviewScores,
                onScoreChange = { dimension, score ->
                    reviewScores = reviewScores + (dimension to score)
                },
                onSave = {
                    onRecordHumanReview(attemptId, reviewName, reviewComment, reviewScores)
                    reviewAttemptId = null
                },
                onDismiss = { reviewAttemptId = null }
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun HumanReviewEntryDialog(
    attempt: Attempt,
    reviewerName: String,
    onReviewerNameChange: (String) -> Unit,
    comment: String,
    onCommentChange: (String) -> Unit,
    scores: Map<Dimension, Int>,
    onScoreChange: (Dimension, Int) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    val completeRubric = Dimension.values().all { scores[it] in 0..4 }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Review-Notiz zu ${attempt.taskId}") },
        text = {
            Column(
                Modifier.fillMaxWidth().heightIn(max = 560.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    "Nur lokal dokumentiert. Name, Identität und Bewertung werden nicht verifiziert und ändern keinen Lernfortschritt.",
                    style = MaterialTheme.typography.bodySmall
                )
                OutlinedTextField(
                    value = reviewerName,
                    onValueChange = onReviewerNameChange,
                    label = { Text("Name oder lokales Kürzel") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("human_review_name")
                )
                Dimension.values().forEach { dimension ->
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(dimensionLabel(dimension), style = MaterialTheme.typography.labelLarge)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            (0..4).forEach { score ->
                                FilterChip(
                                    selected = scores[dimension] == score,
                                    onClick = { onScoreChange(dimension, score) },
                                    label = { Text("$score") },
                                    modifier = Modifier.testTag("human_review_${dimension.name}_$score")
                                )
                            }
                        }
                    }
                }
                OutlinedTextField(
                    value = comment,
                    onValueChange = onCommentChange,
                    label = { Text("Kommentar (optional)") },
                    minLines = 2,
                    maxLines = 5,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onSave,
                enabled = reviewerName.isNotBlank() && completeRubric,
                modifier = Modifier.testTag("human_review_save")
            ) { Text("Lokal speichern") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Abbrechen") } }
    )
}

@Composable
private fun FeedbackChallengeDialog(
    claim: FeedbackClaim,
    sourceAttempt: Attempt?,
    correction: String,
    onCorrectionChange: (String) -> Unit,
    onDecision: (FeedbackChallengeDecision) -> Unit,
    onDismiss: () -> Unit
) {
    val validation = sourceAttempt?.let { FeedbackEvidenceEngine.validate(it.response, claim) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Automatischen Hinweis prüfen") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (validation?.valid == true) {
                    Text("Dein Antwortausschnitt: „${claim.responseExcerpt}“")
                    Text("Vorgeschlagener Hinweis: ${claim.proposedFeedback}")
                    OutlinedTextField(
                        value = correction,
                        onValueChange = { onCorrectionChange(it.take(2_000)) },
                        label = { Text("Eigene Korrektur oder Begründung (optional)") },
                        minLines = 3
                    )
                } else {
                    Text("Der ursprüngliche Antwortbeleg ist nicht mehr verfügbar. Es wird keine Herausforderung gespeichert.")
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onDecision(FeedbackChallengeDecision.DISAGREE) },
                enabled = validation?.valid == true
            ) { Text("Hinweis passt nicht") }
        },
        dismissButton = {
            Row {
                TextButton(
                    onClick = { onDecision(FeedbackChallengeDecision.UNSURE) },
                    enabled = validation?.valid == true
                ) { Text("Unsicher") }
                TextButton(onClick = onDismiss) { Text("Abbrechen") }
            }
        }
    )
}

@Composable
private fun PracticeSection(
    unitId: String, task: String, title: String, prompt: String, context: ContextTrack,
    enabled: Boolean, saving: Boolean, compact: Boolean,
    onSave: (String, String, ContextTrack) -> Unit, limitation: String? = null,
    onOpenTool: (() -> Unit)? = null, toolLabel: String? = null
) {
    ProgrammeSection(title, compact) {
        Text(prompt)
        if (limitation != null) Text(limitation, style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary)
        if (onOpenTool != null && toolLabel != null) {
            OutlinedButton(onClick = onOpenTool) { Text(toolLabel) }
        }
        ResponseField(
            unitId, task, "Deine Antwort / ehrliche Selbstreflexion", context, enabled, saving, onSave,
            taskDescription = "$title\n$prompt"
        )
    }
}

@Composable
private fun ResponseField(
    unitId: String, task: String, label: String, context: ContextTrack,
    enabled: Boolean, saving: Boolean, onSave: (String, String, ContextTrack) -> Unit,
    taskDescription: String = "Deutschübung '$task' im Kontext ${contextLabel(context)}: $label"
) {
    val app = LocalContext.current.applicationContext as MainApplication
    val draftRepository = remember(app) { app.practiceDraftRepository }
    var draft by remember(unitId, task, context) {
        mutableStateOf(draftRepository.load(unitId, task, context))
    }
    OutlinedTextField(
        value = draft,
        onValueChange = {
            draft = it
            draftRepository.save(unitId, task, context, it)
        },
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(), minLines = 3, enabled = enabled && !saving)
    Text("Entwurf wird lokal automatisch gespeichert · nur Text · keine automatische Benotung",
        style = MaterialTheme.typography.labelSmall)
    Button(
        onClick = { onSave(task, draft, context) },
        enabled = enabled && !saving && draft.isNotBlank(),
        modifier = Modifier.testTag("save_practice_${unitId}_$task")
    ) {
        Text("Als Selbstreflexion speichern")
    }
    AiFeedbackShareButton(
        task = taskDescription,
        response = draft,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun QuizSection(
    unit: CurriculumUnit, context: ContextTrack, result: ProgrammeQuizResult?,
    enabled: Boolean, saving: Boolean, onReveal: () -> Unit,
    onQuiz: (Int?, ContextTrack) -> Unit, compact: Boolean
) {
    val content = unit.content ?: return
    var selection by rememberSaveable(unit.id) { mutableStateOf(-1) }
    var explanation by rememberSaveable(unit.id) { mutableStateOf(false) }
    ProgrammeSection("04 / Objektiver Verständnischeck", compact) {
        Text(content.question, fontWeight = FontWeight.SemiBold)
        Column(Modifier.selectableGroup()) {
            content.options.forEachIndexed { index, option ->
                Row(
                    Modifier.fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .selectable(
                            selected = selection == index,
                            onClick = { selection = index },
                            enabled = enabled && !saving,
                            role = Role.RadioButton
                        )
                        .testTag("quiz_option_$index")
                ) {
                    RadioButton(
                        selected = selection == index,
                        onClick = null,
                        enabled = enabled && !saving
                    )
                    Text(option, Modifier.weight(1f).padding(top = 12.dp))
                }
            }
        }
        Button(onClick = { onQuiz(selection, context) }, enabled = enabled && !saving && selection >= 0) {
            Text("Antwort abgeben & speichern")
        }
        TextButton(onClick = { onQuiz(null, context) }, enabled = enabled && !saving) {
            Text("Weiß ich noch nicht · ohne Score speichern")
        }
        if (result != null) {
            Text(when (result.correct) {
                true -> "Richtig · diese eine Verständnisfrage: 4/4"
                false -> "Noch nicht richtig · diese eine Verständnisfrage: 0/4"
                null -> "Als unbekannt gespeichert · kein Score"
            }, color = MaterialTheme.colorScheme.primary)
            Text("Gespeicherte Antwort: ${result.option?.let { content.options[it] } ?: "Weiß ich noch nicht"}" +
                if (result.assisted) " · assistiert" else " · ohne geöffnete Modellhilfe",
                style = MaterialTheme.typography.bodySmall)
        }
        Text("Keine Aussage über freie Produktion oder C1-Niveau. Weitere Versuche nach geöffneter Lösung werden als assistiert gespeichert.",
            style = MaterialTheme.typography.bodySmall)
        OutlinedButton(onClick = { onReveal(); explanation = true }) { Text("Lösung & Erklärung öffnen · Hilfe") }
        if (explanation) {
            Text("Lösung: ${content.options[content.correctOption]}", fontWeight = FontWeight.SemiBold)
            Text(content.explanation)
        }
    }
}

@Composable
private fun SyntheticListening(transcript: String) {
    val context = LocalContext.current.applicationContext
    var engine by remember { mutableStateOf<TextToSpeech?>(null) }
    var ready by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf("Sprachausgabe wird initialisiert …") }
    var showTranscript by rememberSaveable(transcript) { mutableStateOf(false) }
    DisposableEffect(context) {
        var disposed = false
        val handler = Handler(Looper.getMainLooper())
        var instance: TextToSpeech? = null
        try {
            instance = TextToSpeech(context) { result ->
                handler.post {
                    if (!disposed) {
                        val tts = instance
                        if (result == TextToSpeech.SUCCESS && tts != null) {
                            val language = tts.setLanguage(Locale.GERMAN)
                            ready = language != TextToSpeech.LANG_MISSING_DATA && language != TextToSpeech.LANG_NOT_SUPPORTED
                            status = if (ready) "Deutsche synthetische Stimme bereit."
                                else "Keine deutsche Stimme verfügbar. Installiere sie in den Android-Sprachausgabe-Einstellungen oder nutze das Transkript."
                        } else status = "Sprachausgabe nicht verfügbar. Das Transkript bleibt nutzbar."
                    }
                }
            }
            engine = instance
        } catch (_: Exception) {
            status = "Sprachausgabe konnte nicht gestartet werden. Nutze das Transkript."
        }
        onDispose {
            disposed = true
            handler.removeCallbacksAndMessages(null)
            instance?.stop()
            instance?.shutdown()
        }
    }
    Text("Android TextToSpeech · synthetisch, kein Audio von Muttersprachlern. Stimme und Qualität hängen vom Gerät ab; der gewählte Systemanbieter kann Netzwerkzugriff verwenden.",
        style = MaterialTheme.typography.bodySmall)
    Text(status, style = MaterialTheme.typography.bodySmall)
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(enabled = ready, onClick = {
            try {
                val parts = transcript.chunked((TextToSpeech.getMaxSpeechInputLength() - 1).coerceAtLeast(1))
                val failed = parts.mapIndexed { index, part ->
                    engine?.speak(part, if (index == 0) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD,
                        null, "programme-listening-$index") ?: TextToSpeech.ERROR
                }.any { it == TextToSpeech.ERROR }
                status = if (failed) "Wiedergabe fehlgeschlagen. Nutze das Transkript."
                    else "An die synthetische Sprachausgabe übergeben. Keine Aufnahme oder Bewertung."
            } catch (_: Exception) {
                status = "Wiedergabe nicht verfügbar. Nutze das Transkript."
            }
        }) { Text("Vorlesen") }
        OutlinedButton(enabled = ready, onClick = { engine?.stop(); status = "Wiedergabe gestoppt." }) { Text("Stopp") }
    }
    if (showTranscript) {
        Text("Transkript", style = MaterialTheme.typography.titleSmall)
        Text(transcript)
    } else {
        TextButton(onClick = { showTranscript = true }) {
            Text("Transkript nach dem ersten Versuch anzeigen")
        }
    }
}

@Composable
private fun RubricSection(compact: Boolean) {
    ProgrammeSection("Menschliches Beurteilungsraster", compact) {
        Text("Orientierung für eine qualifizierte beurteilende Person, keine automatische Bewertung deiner Texte oder Stimme.")
        listOf(
            "0 · Das Kriterium ist in einer beobachteten Leistung noch nicht erfüllt; nicht mit fehlender Beobachtung verwechseln.",
            "1 · Nur mit erheblicher Hilfe; häufige Brüche behindern die Verständigung.",
            "2 · Teilweise selbstständig; wiederkehrende Unsicherheiten erfordern Rückfragen oder Unterstützung.",
            "3 · Selbstständig, klar und situationsgerecht; einzelne Fehler beeinträchtigen die Aufgabe nicht wesentlich.",
            "4 · Auch unter wechselnden Anforderungen differenziert, flexibel und zuverlässig."
        ).forEach { Text(it, style = MaterialTheme.typography.bodySmall) }
        HorizontalDivider()
        Dimension.values().forEach { dimension ->
            Text(dimensionLabel(dimension), style = MaterialTheme.typography.titleSmall)
            Text(when (dimension) {
                Dimension.ACCURACY -> "Grammatik und Wortwahl tragen die beabsichtigte Bedeutung; Fehler werden sinnvoll repariert."
                Dimension.FLUENCY -> "Zusammenhängender Sprechfluss mit funktionalen Pausen statt kommunikationshemmender Suche. Nur an realer Sprechleistung beurteilbar."
                Dimension.RANGE -> "Ausreichend vielfältige sprachliche Mittel, um präzise zu erklären, einzuschränken und umzuformulieren."
                Dimension.NATURALNESS -> "Idiomatität, Kollokationen und prosodische Gestaltung passen zur Situation. Text allein belegt keine Aussprache."
                Dimension.COHERENCE -> "Gedanken sind nachvollziehbar gegliedert und logisch verbunden."
                Dimension.INTERACTION -> "Auf echte Beiträge eingehen, Sprecherwechsel gestalten und Verständnis gemeinsam sichern. Ein Monolog reicht nicht."
                Dimension.PRAGMATICS -> "Kommunikative Absicht, Höflichkeit und Wirkung werden angemessen aufeinander abgestimmt."
                Dimension.REGISTER -> "Ton, Formalität und Fachsprache passen zu Gegenüber, Medium und Anlass."
                Dimension.COMPREHENSION -> "Wesentliche Aussagen, Bedingungen und Implikationen korrekt verstehen und in eigenen Worten wiedergeben."
                Dimension.MEDIATION -> "Informationen und Perspektiven adressatengerecht vermitteln, ohne Fakten zu verfälschen."
            }, style = MaterialTheme.typography.bodySmall)
        }
        HorizontalDivider()
        Text("Nachweisregel: alle zehn Dimensionen mindestens 3/4, mindestens drei verschiedene Aufgaben, zwei Kontexte und zwei UTC-Lerntage; zusätzlich ein unbekannter Versuch nach mindestens sieben Tagen. Nur unabhängige, unassistierte, menschlich bewertete Produktion zählt.",
            style = MaterialTheme.typography.bodySmall)
        Text("Nicht beobachtet = unbeurteilt, nicht 0. Identität, Unabhängigkeit und unbekannte Aufgaben werden von dieser Oberfläche nicht verifiziert. Ein extern gespeichertes Fremdurteil allein ist kein geprüftes Zertifikat.",
            style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun ProgrammeSection(title: String, compact: Boolean, content: @Composable ColumnScope.() -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(if (compact) 14.dp else 20.dp), verticalArrangement = Arrangement.spacedBy(if (compact) 8.dp else 12.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            content()
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RealLifeReviewDialog(
    experience: RealLifeExperience,
    onDismiss: () -> Unit,
    onComplete: (String) -> Unit
) {
    val context = LocalContext.current
    val recognizer = remember { SpeechRecognitionHelper(context) }
    val speech = remember { AudioSpeechHelper(context) }
    val captureState by recognizer.captureState.collectAsState()
    val recognizerError by recognizer.errorMessage.collectAsState()
    var response by rememberSaveable(experience.id) { mutableStateOf("") }
    val repair = remember(experience.id) { RealLifeRepairEngine.create(experience) }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            recognizer.startListening { text, _ -> response = text }
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            recognizer.cancel()
            recognizer.destroy()
            speech.shutdown()
        }
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Persönliche Reparatur") },
        text = {
            Column(
                Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(experience.situation, fontWeight = FontWeight.SemiBold)
                Text(repair.explanation, style = MaterialTheme.typography.bodySmall)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = {
                        speech.speak(repair.transferPrompt, 0.95f, "real_life_repair")
                    }) {
                        Text("Situation hören")
                    }
                    Button(
                        onClick = {
                            if (captureState == SpeechCaptureState.LISTENING ||
                                captureState == SpeechCaptureState.PREPARING
                            ) {
                                recognizer.stopListening()
                            } else if (ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.RECORD_AUDIO
                                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                            ) {
                                recognizer.startListening { text, _ -> response = text }
                            } else {
                                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        }
                    ) {
                        Text(
                            when (captureState) {
                                SpeechCaptureState.LISTENING, SpeechCaptureState.PREPARING -> "Stopp"
                                else -> "Antwort sprechen"
                            }
                        )
                    }
                }
                if (captureState == SpeechCaptureState.PROCESSING) {
                    Text("Antwort wird verarbeitet …", style = MaterialTheme.typography.bodySmall)
                }
                if (recognizerError != null) {
                    Text(recognizerError.orEmpty(), style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error)
                }
                OutlinedTextField(
                    value = response,
                    onValueChange = { response = it },
                    label = { Text("Deine eigene Antwort ohne Abschreiben") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
                if (response.isNotBlank() && captureState != SpeechCaptureState.LISTENING) {
                    Text("Zielsatz", style = MaterialTheme.typography.titleSmall)
                    Text(repair.primaryPhrase)
                    Text("Varianten", style = MaterialTheme.typography.titleSmall)
                    repair.alternatives.forEach { Text("• $it", style = MaterialTheme.typography.bodySmall) }
                    Text("Transfer", style = MaterialTheme.typography.titleSmall)
                    Text(repair.transferPrompt, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(onClick = { onComplete(response) }, enabled = response.isNotBlank()) {
                Text("Reparatur abschließen")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Später") } }
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RealLifeExperienceDialog(
    onDismiss: () -> Unit,
    onSave: (ContextTrack, String, String, String, String, RealLifeIssueType) -> Unit
) {
    var contextName by rememberSaveable { mutableStateOf(ContextTrack.GENERAL.name) }
    var issueName by rememberSaveable { mutableStateOf(RealLifeIssueType.RETRIEVAL.name) }
    var situation by rememberSaveable { mutableStateOf("") }
    var heard by rememberSaveable { mutableStateOf("") }
    var intended by rememberSaveable { mutableStateOf("") }
    var said by rememberSaveable { mutableStateOf("") }
    val selectedContext = ContextTrack.valueOf(contextName)
    val selectedIssue = RealLifeIssueType.valueOf(issueName)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Heute mit Deutsch Schwierigkeiten gehabt?") },
        text = {
            Column(
                Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    "Aus einer echten Situation wird ein kurzer Reparatur- und Transfer-Schritt.",
                    style = MaterialTheme.typography.bodySmall
                )
                Text("Kontext", style = MaterialTheme.typography.titleSmall)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    ContextTrack.values().forEach { track ->
                        FilterChip(
                            selected = selectedContext == track,
                            onClick = { contextName = track.name },
                            label = { Text(contextLabel(track)) }
                        )
                    }
                }
                Text("Was war schwierig?", style = MaterialTheme.typography.titleSmall)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    RealLifeIssueType.values().forEach { issue ->
                        FilterChip(
                            selected = selectedIssue == issue,
                            onClick = { issueName = issue.name },
                            label = { Text(issue.label) }
                        )
                    }
                }
                OutlinedTextField(
                    value = situation,
                    onValueChange = { situation = it },
                    label = { Text("Was ist passiert?") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = heard,
                    onValueChange = { heard = it },
                    label = { Text("Was hat die andere Person gesagt? (optional)") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = intended,
                    onValueChange = { intended = it },
                    label = { Text("Was wolltest du sagen? (optional)") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = said,
                    onValueChange = { said = it },
                    label = { Text("Was hast du tatsächlich gesagt?") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(selectedContext, situation, heard, intended, said, selectedIssue)
                },
                enabled = situation.isNotBlank() && said.isNotBlank()
            ) {
                Text("Reparatur planen")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Abbrechen") } }
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ProgrammeOnboardingDialog(onComplete: () -> Unit) {
    AlertDialog(
        onDismissRequest = onComplete,
        title = { Text("Willkommen im Programm") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Starte mit der ersten Lektion. Dein Dashboard führt dich zu passenden Übungen und Wiederholungen.")
                Text("Antworten werden lokal gespeichert. Unter „Anpassen“ findest du Zeitbudget, Layout, Export und Löschen.")
                Text("Das Hör-Labor enthält vorgelesene Kursdialoge und Links zu öffentlichen Audioquellen.")
            }
        },
        confirmButton = { Button(onClick = onComplete) { Text("Verstanden") } },
        dismissButton = { TextButton(onClick = onComplete) { Text("Später") } }
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PreferencesDialog(preferences: ProgrammePreferences, viewModel: ProgrammeViewModel, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var confirmDelete by rememberSaveable { mutableStateOf(false) }
    var exporting by rememberSaveable { mutableStateOf(false) }
    var exportError by rememberSaveable { mutableStateOf<String?>(null) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(preferences.language.text("Dein Cockpit", "Settings")) },
        text = {
        Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(value = preferences.learnerName, onValueChange = viewModel::updateName,
                label = { Text(preferences.language.text("Dein Name (optional)", "Your name (optional)")) }, singleLine = true)
            Text(preferences.language.text("Bedienungssprache", "Interface language"),
                style = MaterialTheme.typography.titleSmall)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ProgrammeLanguage.values().forEach { language ->
                    FilterChip(
                        selected = preferences.language == language,
                        onClick = { viewModel.updateLanguage(language) },
                        label = { Text(language.label) }
                    )
                }
            }
            Text(
                preferences.language.text(
                    "Die Kurs- und Übungstexte bleiben auf Deutsch.",
                    "Course and practice content remains in German."
                ),
                style = MaterialTheme.typography.bodySmall
            )
            Text(preferences.language.text("Graphit als Basis. Dein Akzent.", "Graphite base. Your accent."),
                style = MaterialTheme.typography.titleSmall)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ProgrammeTheme.values().forEach { theme ->
                    FilterChip(selected = preferences.theme == theme, onClick = { viewModel.updateTheme(theme) },
                        label = { Text(theme.label) })
                }
            }
            Text(preferences.language.text(
                "Trackside: Teal / Graphit · Silver: Silber · Pitch green: Grün. Eigenständige Farbwelten, keine Markeninhalte.",
                "Trackside: teal / graphite · Silver: silver · Pitch green: green. Original palettes, no brand assets."
            ),
                style = MaterialTheme.typography.bodySmall)
            Row(Modifier.fillMaxWidth()) {
                Text(if (preferences.compact) {
                    preferences.language.text("Kompaktes Layout", "Compact layout")
                } else {
                    preferences.language.text("Komfortables Layout", "Comfortable layout")
                },
                    Modifier.weight(1f).padding(top = 12.dp))
                Switch(checked = preferences.compact, onCheckedChange = viewModel::updateCompact)
            }
            Text(preferences.language.text(
                "Name, Design, Layout und Zeitbudget werden lokal auf diesem Gerät gespeichert. Antworten werden über das Programm-Repository gespeichert; Entwürfe sind noch kein Verlauf.",
                "Name, theme, layout and daily time budget are stored on this device. Answers are saved in your program history; drafts are not completed attempts."
            ),
                style = MaterialTheme.typography.bodySmall)
            OutlinedButton(
                onClick = {
                    scope.launch {
                        exporting = true
                        exportError = null
                        try {
                            val exportDirectory = File(context.cacheDir, "exports")
                            if (!exportDirectory.exists() && !exportDirectory.mkdirs()) {
                                error("Could not create the temporary export directory.")
                            }
                            val exportFile = File(
                                exportDirectory,
                                "c1-deutsch-export-${System.currentTimeMillis()}.json"
                            )
                            exportFile.writeText(viewModel.exportLocalJson())
                            val exportUri = FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.fileprovider",
                                exportFile
                            )
                            val share = Intent(Intent.ACTION_SEND).apply {
                                type = "application/json"
                                putExtra(Intent.EXTRA_SUBJECT, "C1 Deutsch – lokaler Lernexport")
                                putExtra(Intent.EXTRA_STREAM, exportUri)
                                clipData = ClipData.newUri(
                                    context.contentResolver,
                                    exportFile.name,
                                    exportUri
                                )
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            context.startActivity(Intent.createChooser(share, "Lernexport teilen"))
                        } catch (cancelled: CancellationException) {
                            throw cancelled
                        } catch (exception: Exception) {
                            exportError = "Lernexport fehlgeschlagen: ${exception.localizedMessage ?: "Unbekannter Fehler"}"
                        } finally {
                            exporting = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !exporting
            ) {
                Text(if (exporting) "Export wird vorbereitet …" else "Lernverlauf als JSON-Datei exportieren")
            }
            exportError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            Text("Der Export enthält Lernversuche, Assessment-Antworten, Feedback-Herausforderungen, lokale Review-Daten, Alltagseinträge, Entwürfe, Einstellungen und ältere Lernaufzeichnungen. Audio, ausgewählte Datei-URIs und Zugangsdaten werden nicht exportiert.",
                style = MaterialTheme.typography.bodySmall)
            OutlinedButton(
                onClick = { confirmDelete = true },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Alle lokalen Lerndaten löschen", color = MaterialTheme.colorScheme.error) }
            Text("Löscht lokale Lerndaten und temporäre Exportdateien. Externe Kopien und Android-Backups bleiben erhalten.",
                style = MaterialTheme.typography.bodySmall)
        }
    }, confirmButton = { TextButton(onClick = onDismiss) { Text("Fertig") } })
    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text("Alle lokalen Daten löschen?") },
            text = { Text("Diese Aktion entfernt Lernversuche, Alltagseinträge, Reviews, Challenges, Assessment-Sitzungen, Vokabeln, Baseline und Verlauf von diesem Gerät. Sie kann nicht rückgängig gemacht werden.") },
            confirmButton = {
                Button(onClick = {
                    viewModel.deleteAllLocalData { confirmDelete = false; onDismiss() }
                }) { Text("Endgültig löschen") }
            },
            dismissButton = { TextButton(onClick = { confirmDelete = false }) { Text("Abbrechen") } }
        )
    }
}

private fun contextLabel(context: ContextTrack): String = when (context) {
    ContextTrack.GENERAL -> "Alltag"
    ContextTrack.SOCIAL -> "Soziales"
    ContextTrack.ENGINEERING -> "Engineering"
    ContextTrack.COACHING -> "Coaching"
}

private fun dimensionLabel(dimension: Dimension): String = when (dimension) {
    Dimension.ACCURACY -> "Korrektheit"
    Dimension.FLUENCY -> "Flüssigkeit"
    Dimension.RANGE -> "Sprachliche Bandbreite"
    Dimension.NATURALNESS -> "Natürlichkeit"
    Dimension.COHERENCE -> "Kohärenz"
    Dimension.INTERACTION -> "Interaktion"
    Dimension.PRAGMATICS -> "Pragmatik"
    Dimension.REGISTER -> "Register"
    Dimension.COMPREHENSION -> "Verstehen"
    Dimension.MEDIATION -> "Mediation"
}

private fun dateLabel(timestamp: Long): String =
    DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.GERMAN).format(Date(timestamp))
