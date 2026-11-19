package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.GeminiC1Service
import com.example.data.curriculum.C1CurriculumRepository
import com.example.data.local.AppDatabase
import com.example.ui.theme.AppVibeTheme
import com.example.domain.engine.CommanderOverrideConfig
import com.example.domain.engine.DailyBeatType
import com.example.domain.engine.DiagnosticController
import com.example.domain.engine.MasterControlState
import com.example.domain.model.AiMutationReport
import com.example.domain.model.C1Pillar
import com.example.domain.model.C1QuizItem
import com.example.domain.model.C1VocabCard
import com.example.domain.model.ContextTrack
import com.example.domain.model.ControllerStrategyMode
import com.example.domain.model.PersonalizedExercise
import com.example.domain.model.PostSessionAdaptationSummary
import com.example.domain.model.RepoCurriculumUnit
import com.example.domain.model.SituationAiFeedback
import com.example.domain.model.SpontaneousCoachingEvaluation
import com.example.domain.model.VisualLessonModule
import com.example.domain.model.VisualSlide
import com.example.domain.model.WritingDiagnosticResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class ArenaMode(val title: String) {
    ADAPTIVE_CAT("80/20 Adaptive CAT"),
    MISTAKE_REPAIR("Surgical Mistake Repair"),
    CALIBRATION_SWEEP("6-Pillar Diagnostic Sweep"),
    AI_CUSTOM_DRILLS("Gemini AI Custom Drills"),
    PINNED_PILLAR("Targeted Pillar Drill")
}

data class ArenaUiState(
    val mode: ArenaMode = ArenaMode.ADAPTIVE_CAT,
    val currentItem: C1QuizItem? = null,
    val selectedOptionIndex: Int? = null,
    val isSubmitted: Boolean = false,
    val questionNumber: Int = 1,
    val totalQuestions: Int = 6,
    val correctInSession: Int = 0,
    val consecutiveErrors: Int = 0,
    val askedIds: Set<String> = emptySet(),
    val coveredPillars: Set<C1Pillar> = emptySet(),
    val interventionSlide: VisualSlide? = null,
    val sessionFinished: Boolean = false,
    val pinnedPillar: C1Pillar? = null,
    val questionStartMs: Long = System.currentTimeMillis()
)

sealed interface WritingEvalUiState {
    data object Idle : WritingEvalUiState
    data object Loading : WritingEvalUiState
    data class Success(val result: WritingDiagnosticResult) : WritingEvalUiState
    data class Error(val message: String) : WritingEvalUiState
}

sealed interface TutorStreamUiState {
    data object Idle : TutorStreamUiState
    data object Loading : TutorStreamUiState
    data class Streaming(val text: String) : TutorStreamUiState
    data class Success(val text: String) : TutorStreamUiState
    data class Error(val message: String) : TutorStreamUiState
}

sealed interface DrillGenUiState {
    data object Idle : DrillGenUiState
    data object Loading : DrillGenUiState
    data class Success(val items: List<C1QuizItem>) : DrillGenUiState
    data class Error(val message: String) : DrillGenUiState
}

sealed interface AiOrchestrationUiState {
    data object Idle : AiOrchestrationUiState
    data object Running : AiOrchestrationUiState
    data class Success(val report: AiMutationReport) : AiOrchestrationUiState
    data class Error(val message: String) : AiOrchestrationUiState
}

sealed interface SituationEvalUiState {
    data object Idle : SituationEvalUiState
    data object Loading : SituationEvalUiState
    data class Success(val feedback: SituationAiFeedback) : SituationEvalUiState
    data class Error(val message: String) : SituationEvalUiState
}

sealed interface SpontaneousEvalUiState {
    data object Idle : SpontaneousEvalUiState
    data object Loading : SpontaneousEvalUiState
    data class Success(val evaluation: SpontaneousCoachingEvaluation) : SpontaneousEvalUiState
    data class Error(val message: String) : SpontaneousEvalUiState
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getInstance(application)
    private val dao = database.diagnosticDao()
    private val curriculum = C1CurriculumRepository(application)
    private val geminiService = GeminiC1Service()
    val diagnosticController = DiagnosticController(dao, curriculum, geminiService)

    private val coreTelemetryFlow = combine(
        dao.observeAllPillarMastery(),
        dao.observeErrorPatterns(),
        dao.observeSrsStates(),
        dao.observeRecentLogs(),
        dao.observeControllerConfig()
    ) { pillars, errors, srs, logs, config ->
        CoreTelemetryBundle(pillars, errors, srs, logs, config)
    }

    private val curriculumMutationFlow = combine(
        dao.observeMistakeLedger(),
        dao.observePrunedContent(),
        dao.observeAiSlides(),
        dao.observeAiVocab(),
        dao.observeAiQuiz()
    ) { mistakes, pruned, aiSlides, aiVocab, aiQuiz ->
        CurriculumMutationBundle(mistakes, pruned, aiSlides, aiVocab, aiQuiz)
    }

    private val stintOffsetFlow = MutableStateFlow(0)
    private val vibePrefs = application.getSharedPreferences("german_c1_vibe_prefs", Context.MODE_PRIVATE)
    private val commanderPrefs = application.getSharedPreferences("german_c1_commander_prefs", Context.MODE_PRIVATE)
    private val _commanderConfig = MutableStateFlow(loadCommanderConfig())

    val controlState: StateFlow<MasterControlState?> = combine(
        coreTelemetryFlow,
        curriculumMutationFlow,
        dao.observeRepoUnitProgress(),
        stintOffsetFlow,
        _commanderConfig
    ) { core, mut, unitProgress, offset, cmdConfig ->
        if (core.pillars.isEmpty()) null
        else diagnosticController.buildMasterControlState(
            pillars = core.pillars,
            errorPatterns = core.errors,
            srsStates = core.srs,
            recentLogs = core.logs,
            configEntity = core.config,
            mistakes = mut.mistakes,
            prunedItems = mut.pruned,
            aiSlides = mut.aiSlides,
            aiVocab = mut.aiVocab,
            aiQuiz = mut.aiQuiz,
            unitProgress = unitProgress,
            stintOffset = offset,
            commanderOverride = cmdConfig
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    private val _arenaState = MutableStateFlow(ArenaUiState())
    val arenaState: StateFlow<ArenaUiState> = _arenaState.asStateFlow()

    private val _writingState = MutableStateFlow<WritingEvalUiState>(WritingEvalUiState.Idle)
    val writingState: StateFlow<WritingEvalUiState> = _writingState.asStateFlow()

    private val _tutorState = MutableStateFlow<TutorStreamUiState>(TutorStreamUiState.Idle)
    val tutorState: StateFlow<TutorStreamUiState> = _tutorState.asStateFlow()

    private val _drillGenState = MutableStateFlow<DrillGenUiState>(DrillGenUiState.Idle)
    val drillGenState: StateFlow<DrillGenUiState> = _drillGenState.asStateFlow()

    private val _aiOrchestrationState = MutableStateFlow<AiOrchestrationUiState>(AiOrchestrationUiState.Idle)
    val aiOrchestrationState: StateFlow<AiOrchestrationUiState> = _aiOrchestrationState.asStateFlow()

    private val _situationEvalState = MutableStateFlow<SituationEvalUiState>(SituationEvalUiState.Idle)
    val situationEvalState: StateFlow<SituationEvalUiState> = _situationEvalState.asStateFlow()

    private val _spontaneousEvalState = MutableStateFlow<SpontaneousEvalUiState>(SpontaneousEvalUiState.Idle)
    val spontaneousEvalState: StateFlow<SpontaneousEvalUiState> = _spontaneousEvalState.asStateFlow()

    private val _lastPruneCount = MutableStateFlow<Int?>(null)
    val lastPruneCount: StateFlow<Int?> = _lastPruneCount.asStateFlow()

    private val _postSessionAdaptation = MutableStateFlow<PostSessionAdaptationSummary?>(null)
    val postSessionAdaptation: StateFlow<PostSessionAdaptationSummary?> = _postSessionAdaptation.asStateFlow()

    private val _vibeTheme = MutableStateFlow(
        AppVibeTheme.fromId(vibePrefs.getString("selected_vibe_theme", AppVibeTheme.PORSCHE_911.id))
    )
    val vibeTheme: StateFlow<AppVibeTheme> = _vibeTheme.asStateFlow()

    private var activeAiDrills: List<C1QuizItem> = emptyList()
    private var lastAutoAiMutationMs: Long = 0L

    init {
        viewModelScope.launch {
            diagnosticController.ensureInitialized()
        }
    }

    private fun loadCommanderConfig(): CommanderOverrideConfig {
        val enabled = commanderPrefs.getBoolean("cmd_enabled", false)
        val pillarId = commanderPrefs.getString("cmd_pillar", null)
        val pinnedPillar = C1Pillar.entries.firstOrNull { it.id == pillarId }
        val savedBeats = commanderPrefs.getStringSet("cmd_beats", null)
        val beats = if (savedBeats.isNullOrEmpty()) {
            DailyBeatType.entries.toSet()
        } else {
            savedBeats.mapNotNull { name -> runCatching { DailyBeatType.valueOf(name) }.getOrNull() }
                .toSet()
                .ifEmpty { DailyBeatType.entries.toSet() }
        }
        val speed = commanderPrefs.getFloat("cmd_speed", 1.0f).coerceIn(0.85f, 1.30f)
        val diff = commanderPrefs.getInt("cmd_diff", 0).coerceIn(-1, 1)
        return CommanderOverrideConfig(
            commanderEnabled = enabled,
            pinnedFocusPillar = pinnedPillar,
            enabledBeatTypes = beats,
            audioSpeedBias = speed,
            difficultyOffset = diff
        )
    }

    private fun persistCommanderConfig(cfg: CommanderOverrideConfig) {
        _commanderConfig.value = cfg
        commanderPrefs.edit()
            .putBoolean("cmd_enabled", cfg.commanderEnabled)
            .putString("cmd_pillar", cfg.pinnedFocusPillar?.id)
            .putStringSet("cmd_beats", cfg.enabledBeatTypes.map { it.name }.toSet())
            .putFloat("cmd_speed", cfg.audioSpeedBias)
            .putInt("cmd_diff", cfg.difficultyOffset)
            .apply()
    }

    fun setCommanderEnabled(enabled: Boolean) {
        persistCommanderConfig(_commanderConfig.value.copy(commanderEnabled = enabled))
    }

    fun pinFocusPillarInCommander(pillar: C1Pillar?) {
        val current = _commanderConfig.value
        persistCommanderConfig(
            current.copy(
                commanderEnabled = pillar != null || current.commanderEnabled,
                pinnedFocusPillar = pillar
            )
        )
    }

    fun toggleBeatTypeInCommander(beatType: DailyBeatType) {
        val current = _commanderConfig.value
        val nextSet = if (beatType in current.enabledBeatTypes) {
            val reduced = current.enabledBeatTypes - beatType
            if (reduced.isEmpty()) current.enabledBeatTypes else reduced
        } else {
            current.enabledBeatTypes + beatType
        }
        persistCommanderConfig(
            current.copy(
                commanderEnabled = true,
                enabledBeatTypes = nextSet
            )
        )
    }

    fun setCommanderAudioSpeed(speed: Float) {
        persistCommanderConfig(
            _commanderConfig.value.copy(
                commanderEnabled = true,
                audioSpeedBias = speed.coerceIn(0.85f, 1.30f)
            )
        )
    }

    fun setCommanderDifficultyOffset(offset: Int) {
        persistCommanderConfig(
            _commanderConfig.value.copy(
                commanderEnabled = true,
                difficultyOffset = offset.coerceIn(-1, 1)
            )
        )
    }

    fun resetCommanderToAutopilot() {
        persistCommanderConfig(CommanderOverrideConfig())
    }

    fun selectVibeTheme(theme: AppVibeTheme) {
        _vibeTheme.value = theme
        vibePrefs.edit().putString("selected_vibe_theme", theme.id).apply()
    }

    fun cycleVibeTheme() {
        val all = AppVibeTheme.entries
        val next = all[(all.indexOf(_vibeTheme.value) + 1) % all.size]
        selectVibeTheme(next)
    }

    fun getModuleById(id: String): VisualLessonModule? {
        return controlState.value?.orderedModules?.find { it.module.id == id }?.module
            ?: curriculum.getModuleById(id)
    }

    fun getRepoUnitById(unitId: String): RepoCurriculumUnit? {
        return curriculum.getRepoUnitById(unitId)
            ?: curriculum.getAllRepoUnits().firstOrNull()
    }

    fun selectContextTrack(track: ContextTrack) {
        viewModelScope.launch {
            diagnosticController.updateContextTrack(track)
        }
    }

    fun recordRepoUnitStep(unitId: String, stepType: String, scoreBoost: Float = 8f) {
        viewModelScope.launch {
            diagnosticController.recordRepoUnitStep(unitId, stepType, scoreBoost)
        }
    }

    fun evaluateSituationReply(
        scenarioTitle: String,
        partnerPrompt: String,
        objective: String,
        userReply: String,
        pillar: C1Pillar
    ) {
        if (userReply.isBlank()) return
        val trackLabel = controlState.value?.selectedContextTrack?.titleDe ?: ContextTrack.GENERAL.titleDe
        viewModelScope.launch {
            _situationEvalState.value = SituationEvalUiState.Loading
            val res = geminiService.evaluateSituationTurn(
                scenarioTitle = scenarioTitle,
                partnerPrompt = partnerPrompt,
                objective = objective,
                userReply = userReply.trim(),
                contextTrackLabel = trackLabel
            )
            res.fold(
                onSuccess = { feedback ->
                    diagnosticController.recordSituationEvaluation(
                        scenarioTitle = scenarioTitle,
                        userReply = userReply.trim(),
                        feedback = feedback,
                        pillar = pillar
                    )
                    _situationEvalState.value = SituationEvalUiState.Success(feedback)
                },
                onFailure = { err ->
                    _situationEvalState.value = SituationEvalUiState.Error(
                        err.localizedMessage ?: "AI-Bewertung der Situation fehlgeschlagen."
                    )
                }
            )
        }
    }

    fun resetSituationEval() {
        _situationEvalState.value = SituationEvalUiState.Idle
    }

    fun recordPersonalizedBeat(exerciseId: String, beatType: String) {
        viewModelScope.launch {
            diagnosticController.recordPersonalizedBeat(exerciseId, beatType)
        }
    }

    fun evaluateSpontaneousExercise(exercise: PersonalizedExercise, userReply: String) {
        if (userReply.isBlank()) return
        viewModelScope.launch {
            _spontaneousEvalState.value = SpontaneousEvalUiState.Loading
            val res = geminiService.evaluateSpontaneousCounterpart(
                exerciseTitle = exercise.title,
                subRegister = exercise.subRegister,
                speakingPrompt = exercise.speakingTask,
                interactionTwist = exercise.interactionTask,
                userReply = userReply.trim()
            )
            res.fold(
                onSuccess = { evaluation ->
                    diagnosticController.recordSpontaneousCoachingEval(
                        exercise = exercise,
                        userReply = userReply.trim(),
                        eval = evaluation
                    )
                    _spontaneousEvalState.value = SpontaneousEvalUiState.Success(evaluation)
                },
                onFailure = { err ->
                    _spontaneousEvalState.value = SpontaneousEvalUiState.Error(
                        err.localizedMessage ?: "AI-Gegenpart-Analyse fehlgeschlagen."
                    )
                }
            )
        }
    }

    fun resetSpontaneousEval() {
        _spontaneousEvalState.value = SpontaneousEvalUiState.Idle
    }

    fun startArenaSession(
        mode: ArenaMode = ArenaMode.ADAPTIVE_CAT,
        pinnedPillar: C1Pillar? = null,
        customAiItems: List<C1QuizItem> = emptyList()
    ) {
        val state = controlState.value ?: return
        if (customAiItems.isNotEmpty()) {
            activeAiDrills = customAiItems
        }
        viewModelScope.launch {
            val activePool = diagnosticController.getActiveQuizPool()
            val pool = if (mode == ArenaMode.AI_CUSTOM_DRILLS && activeAiDrills.isNotEmpty()) {
                activeAiDrills
            } else {
                activePool
            }
            val total = when (mode) {
                ArenaMode.CALIBRATION_SWEEP -> 8
                ArenaMode.MISTAKE_REPAIR -> state.unresolvedMistakes.size.coerceIn(4, 6)
                ArenaMode.AI_CUSTOM_DRILLS -> pool.size.coerceAtMost(6)
                else -> 6
            }
            val firstItem = diagnosticController.selectNextAdaptiveItem(
                pool = pool,
                state = state,
                alreadyAskedIds = emptySet(),
                recentConsecutiveErrors = 0,
                calibrationMode = mode == ArenaMode.CALIBRATION_SWEEP,
                coveredPillarsInSession = emptySet(),
                pinnedPillar = pinnedPillar,
                mistakeRepairMode = mode == ArenaMode.MISTAKE_REPAIR
            )
            _arenaState.value = ArenaUiState(
                mode = mode,
                currentItem = firstItem,
                totalQuestions = total,
                questionNumber = 1,
                pinnedPillar = pinnedPillar,
                questionStartMs = System.currentTimeMillis()
            )
        }
    }

    fun selectArenaOption(index: Int) {
        if (_arenaState.value.isSubmitted) return
        _arenaState.update { it.copy(selectedOptionIndex = index) }
    }

    fun submitArenaAnswer() {
        val current = _arenaState.value
        val item = current.currentItem ?: return
        val selected = current.selectedOptionIndex ?: return
        if (current.isSubmitted) return

        val isCorrect = selected == item.correctIndex
        val elapsedMs = (System.currentTimeMillis() - current.questionStartMs).coerceIn(500L, 60_000L)
        val newConsecutiveErrors = if (isCorrect) 0 else current.consecutiveErrors + 1

        val enrichedModule = controlState.value?.orderedModules
            ?.firstOrNull { it.module.pillar == item.pillar }?.module
            ?: curriculum.getModuleForPillar(item.pillar)

        val intervention = if (!isCorrect && newConsecutiveErrors >= 2) {
            enrichedModule.slides.lastOrNull { it.isAiSynthesized } ?: enrichedModule.slides.firstOrNull()
        } else {
            null
        }

        _arenaState.update {
            it.copy(
                isSubmitted = true,
                correctInSession = it.correctInSession + if (isCorrect) 1 else 0,
                consecutiveErrors = newConsecutiveErrors,
                askedIds = it.askedIds + item.id,
                coveredPillars = it.coveredPillars + item.pillar,
                interventionSlide = intervention
            )
        }

        viewModelScope.launch {
            val isLastCalibration = current.mode == ArenaMode.CALIBRATION_SWEEP &&
                current.questionNumber >= current.totalQuestions
            val shouldMutate = diagnosticController.recordQuizAnswer(
                item = item,
                selectedIndex = selected,
                isCorrect = isCorrect,
                responseTimeMs = elapsedMs,
                markCalibrated = isLastCalibration || current.coveredPillars.size >= 4
            )
            if (shouldMutate) {
                maybeTriggerAutonomousAiMutation()
            }
        }
    }

    fun dismissIntervention() {
        _arenaState.update { it.copy(interventionSlide = null) }
    }

    fun advanceArenaQuestion() {
        val current = _arenaState.value
        val state = controlState.value ?: return
        if (current.questionNumber >= current.totalQuestions) {
            _arenaState.update { it.copy(sessionFinished = true) }
            return
        }
        viewModelScope.launch {
            val activePool = diagnosticController.getActiveQuizPool()
            val pool = if (current.mode == ArenaMode.AI_CUSTOM_DRILLS && activeAiDrills.isNotEmpty()) {
                activeAiDrills
            } else {
                activePool
            }
            val nextItem = diagnosticController.selectNextAdaptiveItem(
                pool = pool,
                state = state,
                alreadyAskedIds = current.askedIds,
                recentConsecutiveErrors = current.consecutiveErrors,
                calibrationMode = current.mode == ArenaMode.CALIBRATION_SWEEP,
                coveredPillarsInSession = current.coveredPillars,
                pinnedPillar = current.pinnedPillar,
                mistakeRepairMode = current.mode == ArenaMode.MISTAKE_REPAIR
            )
            _arenaState.update {
                it.copy(
                    currentItem = nextItem,
                    selectedOptionIndex = null,
                    isSubmitted = false,
                    questionNumber = it.questionNumber + 1,
                    interventionSlide = null,
                    questionStartMs = System.currentTimeMillis()
                )
            }
        }
    }

    fun runAiCurriculumOptimizer() {
        val state = controlState.value ?: return
        viewModelScope.launch {
            _aiOrchestrationState.value = AiOrchestrationUiState.Running
            val result = diagnosticController.orchestrateWithFirebaseAi(state)
            result.fold(
                onSuccess = { report ->
                    _aiOrchestrationState.value = AiOrchestrationUiState.Success(report)
                },
                onFailure = { err ->
                    _aiOrchestrationState.value = AiOrchestrationUiState.Error(
                        err.localizedMessage ?: "Firebase AI curriculum optimization failed."
                    )
                }
            )
        }
    }

    fun runParetoPruneSweep() {
        viewModelScope.launch {
            val count = diagnosticController.runParetoPruneSweep()
            _lastPruneCount.value = count
        }
    }

    fun restorePrunedItem(contentId: String) {
        viewModelScope.launch {
            diagnosticController.restorePrunedItem(contentId)
        }
    }

    fun recordSlideSeen(pillar: C1Pillar, slideIndex: Int, checkpointCorrect: Boolean?) {
        viewModelScope.launch {
            diagnosticController.recordVisualSlideProgress(pillar, slideIndex, checkpointCorrect)
        }
    }

    fun rateSrsCard(card: C1VocabCard, quality: Int) {
        viewModelScope.launch {
            diagnosticController.recordSrsReview(card, quality)
        }
    }

    fun setStrategyMode(mode: ControllerStrategyMode) {
        viewModelScope.launch {
            diagnosticController.updateStrategyMode(mode)
        }
    }

    fun setDailyGoal(minutes: Int) {
        viewModelScope.launch {
            diagnosticController.updateDailyGoal(minutes)
        }
    }

    fun resetDiagnostics() {
        viewModelScope.launch {
            diagnosticController.resetDiagnosticTelemetry()
            _aiOrchestrationState.value = AiOrchestrationUiState.Idle
            _situationEvalState.value = SituationEvalUiState.Idle
            _spontaneousEvalState.value = SpontaneousEvalUiState.Idle
            _lastPruneCount.value = null
        }
    }

    fun evaluateWriting(userText: String, topic: String) {
        val state = controlState.value ?: return
        if (userText.isBlank()) {
            _writingState.value = WritingEvalUiState.Error("Please enter a German paragraph to diagnose.")
            return
        }
        viewModelScope.launch {
            _writingState.value = WritingEvalUiState.Loading
            val result = geminiService.evaluateAndUpgradeWriting(
                userText = userText.trim(),
                topicPrompt = topic,
                diagnosticBriefing = state.aiDiagnosticBriefing
            )
            result.fold(
                onSuccess = { diag ->
                    diagnosticController.recordWritingDiagnostic(diag)
                    _writingState.value = WritingEvalUiState.Success(diag)
                },
                onFailure = { err ->
                    _writingState.value = WritingEvalUiState.Error(
                        err.localizedMessage ?: "Failed to evaluate writing with Gemini."
                    )
                }
            )
        }
    }

    fun generateAdaptiveAiDrills(targetPillar: C1Pillar? = null) {
        val state = controlState.value ?: return
        val pillar = targetPillar ?: state.pillarSnapshots.first().pillar
        val archetype = state.dominantErrorArchetype
        viewModelScope.launch {
            _drillGenState.value = DrillGenUiState.Loading
            val result = geminiService.generateAdaptiveDrills(
                diagnosticBriefing = state.aiDiagnosticBriefing,
                targetPillar = pillar,
                targetArchetype = archetype
            )
            result.fold(
                onSuccess = { items ->
                    activeAiDrills = items
                    _drillGenState.value = DrillGenUiState.Success(items)
                },
                onFailure = { err ->
                    _drillGenState.value = DrillGenUiState.Error(
                        err.localizedMessage ?: "Failed to generate adaptive C1 drills."
                    )
                }
            )
        }
    }

    fun askSocraticCoach(question: String) {
        val state = controlState.value ?: return
        if (question.isBlank()) return
        viewModelScope.launch {
            _tutorState.value = TutorStreamUiState.Loading
            val buffer = StringBuilder()
            try {
                geminiService.streamSocraticCoach(question.trim(), state.aiDiagnosticBriefing)
                    .collect { chunk ->
                        buffer.append(chunk)
                        _tutorState.value = TutorStreamUiState.Streaming(buffer.toString())
                    }
                if (buffer.isNotBlank()) {
                    _tutorState.value = TutorStreamUiState.Success(buffer.toString())
                } else {
                    _tutorState.value = TutorStreamUiState.Error("Coach returned an empty response.")
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _tutorState.value = TutorStreamUiState.Error(
                    e.localizedMessage ?: "Streaming coach request failed."
                )
            }
        }
    }

    fun submitEmbeddedQuizProbe(
        item: C1QuizItem,
        selectedIndex: Int,
        responseTimeMs: Long = 4500L,
        markCalibrated: Boolean = false
    ) {
        viewModelScope.launch {
            val shouldMutate = diagnosticController.recordQuizAnswer(
                item = item,
                selectedIndex = selectedIndex,
                isCorrect = selectedIndex == item.correctIndex,
                responseTimeMs = responseTimeMs.coerceIn(500L, 60_000L),
                markCalibrated = markCalibrated
            )
            if (shouldMutate) {
                maybeTriggerAutonomousAiMutation()
            }
        }
    }

    private fun maybeTriggerAutonomousAiMutation() {
        val now = System.currentTimeMillis()
        if (now - lastAutoAiMutationMs < 180_000L) return
        if (_aiOrchestrationState.value is AiOrchestrationUiState.Running) return
        lastAutoAiMutationMs = now
        runAiCurriculumOptimizer()
    }

    fun recordMediaMissionCompleted(missionId: String, quizCorrect: Boolean) {
        viewModelScope.launch {
            diagnosticController.recordMediaMissionCompleted(missionId, quizCorrect)
        }
    }

    fun concludeStintAndAdapt(completedSectorsCount: Int) {
        viewModelScope.launch {
            val summary = diagnosticController.concludeDailyStintAndAdapt(
                completedSectorsCount = completedSectorsCount,
                nextStintOffset = stintOffsetFlow.value + 1
            )
            _postSessionAdaptation.value = summary
            maybeTriggerAutonomousAiMutation()
        }
    }

    fun advanceStintCycle() {
        resetSpontaneousEval()
        resetSituationEval()
        _postSessionAdaptation.value = null
        stintOffsetFlow.update { it + 1 }
    }

    private data class CoreTelemetryBundle(
        val pillars: List<com.example.data.local.PillarMasteryEntity>,
        val errors: List<com.example.data.local.ErrorPatternEntity>,
        val srs: List<com.example.data.local.SrsCardStateEntity>,
        val logs: List<com.example.data.local.DiagnosticSessionLogEntity>,
        val config: com.example.data.local.ControllerConfigEntity?
    )

    private data class CurriculumMutationBundle(
        val mistakes: List<com.example.data.local.MistakeLedgerEntity>,
        val pruned: List<com.example.data.local.PrunedContentEntity>,
        val aiSlides: List<com.example.data.local.AiGeneratedSlideEntity>,
        val aiVocab: List<com.example.data.local.AiGeneratedVocabEntity>,
        val aiQuiz: List<com.example.data.local.AiGeneratedQuizEntity>
    )
}
