package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.audio.rememberHybridC1AudioController
import com.example.domain.engine.DailyBeatType
import com.example.domain.engine.DailySessionBeat
import com.example.domain.engine.MasterControlState
import com.example.domain.model.C1Pillar
import com.example.domain.model.C1QuizItem
import com.example.domain.model.C1VocabCard
import com.example.domain.model.PersonalizedExercise
import com.example.domain.model.PostSessionAdaptationSummary
import com.example.ui.SituationEvalUiState
import com.example.ui.SpontaneousEvalUiState
import com.example.ui.components.MinimalDailyStintHeader
import com.example.ui.theme.AppVibeTheme
import com.example.ui.theme.LocalVibePalette

/**
 * Focused daily C1 lesson:
 * Illustrated scenarios, interactive flashcards, listening, speaking,
 * and smooth transitions from one learning step to the next.
 */
@Composable
fun DailySessionScreen(
    controlState: MasterControlState,
    selectedVibeTheme: AppVibeTheme = AppVibeTheme.PORSCHE_911,
    spontaneousEvalState: SpontaneousEvalUiState,
    situationEvalState: SituationEvalUiState,
    postSessionAdaptation: PostSessionAdaptationSummary? = null,
    onSelectVibeTheme: (AppVibeTheme) -> Unit = {},
    onBeatCompleted: (String, String) -> Unit,
    onEvaluateSpontaneous: (PersonalizedExercise, String) -> Unit,
    onResetSpontaneousEval: () -> Unit,
    onRateSrsCard: (C1VocabCard, Int) -> Unit,
    onRecordRepoStep: (String, String, Float) -> Unit,
    onRecordMediaMission: (String, Boolean) -> Unit = { _, _ -> },
    onSubmitQuizProbe: (C1QuizItem, Int, Long, Boolean) -> Unit,
    onEvaluateSituationReply: (String, String, String, String, C1Pillar) -> Unit,
    onResetSituationEval: () -> Unit,
    onConcludeStint: (Int) -> Unit = {},
    onAdvanceStintCycle: () -> Unit,
    onOpenEngineSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalVibePalette.current
    val queue = controlState.dailySessionQueue

    val cmd = controlState.commanderConfig
    val stintResetKey = "${controlState.dailyGoalMinutes}_${controlState.selectedContextTrack.id}_${cmd.commanderEnabled}_${cmd.pinnedFocusPillar?.id}_${cmd.enabledBeatTypes.size}_${cmd.difficultyOffset}"
    var manualLapCounter by remember { mutableIntStateOf(0) }
    val sessionKey = "${stintResetKey}_$manualLapCounter"

    val completedBeats = remember(sessionKey) { mutableStateListOf<DailySessionBeat>() }
    var pinnedBeat by remember(sessionKey) { mutableStateOf<DailySessionBeat?>(queue.firstOrNull()) }
    var stintCompleted by remember(sessionKey) { mutableStateOf(false) }

    // Dual-Engine Authentic German Audio (Gemini 3.1 Studio WAV + Live Broadcast MP3)
    val audioController = rememberHybridC1AudioController()
    var fastNaturalRate by remember { mutableStateOf(true) }

    LaunchedEffect(sessionKey, queue.size) {
        if (pinnedBeat == null && queue.isNotEmpty() && !stintCompleted) {
            pinnedBeat = queue.first()
        }
    }

    LaunchedEffect(pinnedBeat?.id) {
        val recRate = pinnedBeat?.adaptation?.recommendedAudioRate
        if (recRate != null) {
            fastNaturalRate = recRate >= 1.08f
        }
    }

    LaunchedEffect(stintCompleted, sessionKey) {
        if (stintCompleted && postSessionAdaptation == null) {
            onConcludeStint(completedBeats.size.coerceAtLeast(1))
        }
    }

    fun speakGerman(text: String, rateOverride: Float? = null) {
        val adaptedFast = pinnedBeat?.adaptation?.recommendedAudioRate?.coerceAtLeast(1.12f) ?: 1.15f
        val rate = rateOverride ?: if (fastNaturalRate) adaptedFast else 0.96f
        audioController.speakPhrase(text, rate)
    }

    fun stopAudio() {
        audioController.stop()
    }

    val targetTotalSectors = queue.size.coerceAtLeast(1)
    val liveDisplayBeats = remember(completedBeats.size, pinnedBeat?.id, queue) {
        val current = pinnedBeat
        val doneIds = completedBeats.map { it.beatType }.toSet()
        val remaining = queue.filter { cand ->
            cand.beatType !in doneIds && cand.beatType != current?.beatType
        }
        buildList {
            addAll(completedBeats)
            if (current != null && !stintCompleted) add(current)
            addAll(remaining)
        }.take(targetTotalSectors.coerceAtLeast(completedBeats.size + (if (current != null && !stintCompleted) 1 else 0)))
    }

    fun advanceToNextSector() {
        stopAudio()
        onResetSpontaneousEval()
        onResetSituationEval()
        pinnedBeat?.let { finished ->
            if (completedBeats.none { it.id == finished.id }) {
                completedBeats.add(finished)
            }
        }
        if (completedBeats.size >= targetTotalSectors) {
            stintCompleted = true
            pinnedBeat = null
        } else {
            val doneTypes = completedBeats.map { it.beatType }.toSet()
            val nextDynamicPick = queue.firstOrNull { it.beatType !in doneTypes }
                ?: queue.firstOrNull { cand -> completedBeats.none { it.id == cand.id } }
            if (nextDynamicPick != null) {
                pinnedBeat = nextDynamicPick
            } else {
                stintCompleted = true
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(palette.canvasBg),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 600.dp)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1. Minimalist Decluttered Header (Compact Theme Picker + Daily Summary + Streak + Integrated Sector Bars)
            val activeBeat = pinnedBeat ?: queue.firstOrNull()
            MinimalDailyStintHeader(
                selectedTheme = selectedVibeTheme,
                dailyGoalMinutes = controlState.dailyGoalMinutes,
                minutesToday = controlState.minutesToday,
                functionalC1Percent = controlState.functionalC1ReadinessPercent,
                streakDays = controlState.streakDays,
                commanderActive = controlState.commanderConfig.commanderEnabled,
                beats = if (!stintCompleted) liveDisplayBeats else emptyList(),
                activeBeat = activeBeat,
                completedBeatIds = completedBeats.map { it.id }.toSet(),
                onSelectTheme = onSelectVibeTheme,
                onSelectBeat = { clicked ->
                    stopAudio()
                    pinnedBeat = clicked
                },
                onOpenEngineSettings = onOpenEngineSettings
            )

            // 2. One focused visual learning step at a time
            if (stintCompleted || queue.isEmpty() || activeBeat == null) {
                StintCompletedCard(
                    completedCount = completedBeats.size.coerceAtLeast(queue.size),
                    functionalPercent = controlState.functionalC1ReadinessPercent,
                    examPercent = controlState.examReadinessPercent,
                    minutesToday = controlState.minutesToday,
                    dailyGoalMinutes = controlState.dailyGoalMinutes,
                    adaptationSummary = postSessionAdaptation,
                    onNextStint = {
                        onAdvanceStintCycle()
                        manualLapCounter++
                    },
                    onOpenEngine = onOpenEngineSettings
                )
            } else {
                AnimatedContent(
                    targetState = activeBeat.id,
                    transitionSpec = {
                        (fadeIn(tween(340, easing = FastOutSlowInEasing)) +
                            slideInHorizontally(
                                animationSpec = spring(dampingRatio = 0.84f, stiffness = Spring.StiffnessMediumLow)
                            ) { fullWidth -> fullWidth / 4 } +
                            scaleIn(initialScale = 0.96f, animationSpec = tween(320)))
                            .togetherWith(
                                fadeOut(tween(220)) +
                                    slideOutHorizontally(tween(240)) { fullWidth -> -fullWidth / 5 } +
                                    scaleOut(targetScale = 0.97f, animationSpec = tween(220))
                            )
                    },
                    label = "embeddedDailySector"
                ) { _ ->
                    when (activeBeat.beatType) {
                        DailyBeatType.ADAPTIVE_PROBE_BURST -> {
                            EmbeddedAdaptiveProbeBeat(
                                beat = activeBeat,
                                onSubmitProbe = onSubmitQuizProbe,
                                onSectorDone = { advanceToNextSector() }
                            )
                        }
                        DailyBeatType.BLIND_LISTENING_SCENE -> {
                            val ex = activeBeat.personalizedExercise
                            if (ex != null) {
                                val scenePlaying = audioController.isPlaying && audioController.activeKey == "dlg_${ex.id}"
                                val sceneLoading = audioController.isLoading && audioController.activeKey == "dlg_${ex.id}"
                                val adaptedFast = activeBeat.adaptation?.recommendedAudioRate?.coerceAtLeast(1.12f) ?: 1.15f
                                EmbeddedBlindListeningBeat(
                                    ex = ex,
                                    isPlayingAudio = scenePlaying,
                                    isLoadingAudio = sceneLoading,
                                    fastNaturalRate = fastNaturalRate,
                                    engineBadge = audioController.engineBadge,
                                    audioErrorMessage = audioController.errorMessage,
                                    onPlayDialogue = {
                                        val speed = if (fastNaturalRate) adaptedFast else 0.96f
                                        audioController.playDialogueScene(ex, speed)
                                    },
                                    onToggleSpeed = {
                                        fastNaturalRate = !fastNaturalRate
                                        audioController.updateSpeed(if (fastNaturalRate) adaptedFast else 0.96f)
                                    },
                                    onSpeakLine = { line -> speakGerman(line, 1.0f) },
                                    onSectorDone = {
                                        onBeatCompleted(ex.id, "BLIND_LISTEN")
                                        advanceToNextSector()
                                    }
                                )
                            }
                        }
                        DailyBeatType.SPONTANEOUS_SPEAKING -> {
                            val ex = activeBeat.personalizedExercise
                            if (ex != null) {
                                EmbeddedSpontaneousSpeakingBeat(
                                    ex = ex,
                                    evalState = spontaneousEvalState,
                                    onEvaluate = { reply -> onEvaluateSpontaneous(ex, reply) },
                                    onSpeakPhrase = { phrase -> speakGerman(phrase, 1.0f) },
                                    onSectorDone = {
                                        onBeatCompleted(ex.id, "TRANSFER")
                                        advanceToNextSector()
                                    }
                                )
                            }
                        }
                        DailyBeatType.NATIVE_MEDIA_IMMERSION -> {
                            val mission = activeBeat.mediaMission
                            if (mission != null) {
                                EmbeddedNativeMediaImmersionBeat(
                                    mission = mission,
                                    audioController = audioController,
                                    onSpeakPhrase = { phrase -> speakGerman(phrase, 1.0f) },
                                    onCompleteMission = { quizOk ->
                                        onRecordMediaMission(mission.id, quizOk)
                                        advanceToNextSector()
                                    }
                                )
                            } else {
                                OutlinedButton(onClick = { advanceToNextSector() }) {
                                    Text("Nächster Schritt →")
                                }
                            }
                        }
                        DailyBeatType.SRS_VOCAB_BURST -> {
                            EmbeddedSrsVocabBurstBeat(
                                beat = activeBeat,
                                onSpeakExpression = { expr -> speakGerman(expr, 0.98f) },
                                onRateCard = onRateSrsCard,
                                onSectorDone = { advanceToNextSector() }
                            )
                        }
                        DailyBeatType.TEXTBOOK_VISUAL_BLUEPRINT -> {
                            EmbeddedTextbookBlueprintBeat(
                                beat = activeBeat,
                                activeTrack = controlState.selectedContextTrack,
                                onSpeakPhrase = { phrase -> speakGerman(phrase, 0.98f) },
                                onRecordStep = onRecordRepoStep,
                                onSectorDone = { advanceToNextSector() }
                            )
                        }
                        DailyBeatType.LIVE_TACTICAL_TRANSFER -> {
                            EmbeddedLiveTransferBeat(
                                beat = activeBeat,
                                situationEvalState = situationEvalState,
                                onEvaluateMediation = onEvaluateSituationReply,
                                onSpeakCommand = { cmd -> speakGerman(cmd, 1.05f) },
                                onSectorDone = { advanceToNextSector() }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
        }
    }
}
