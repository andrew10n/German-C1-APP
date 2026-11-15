package com.example.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.MainApplication
import com.example.data.local.C1AppDatabase
import com.example.data.local.entity.BaselineProfileEntity
import com.example.data.model.*
import com.example.data.repository.C1LearningRepository
import com.example.service.AudioSpeechHelper
import com.example.data.model.SpeakerProfile
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class Screen {
    object Home : Screen()
    object LegacyHome : Screen()
    object SessionRunner : Screen()
    object ListeningLab : Screen()
    object ConversationSimulator : Screen()
    object SpeakingLab : Screen()
    object ErrorIntelligence : Screen()
    object IntelligenceProfile : Screen()
    object Mediation : Screen()
    object BaselineDiagnostic : Screen()
    object FootballCoachTrack : Screen()
}

class C1ViewModel(application: Application) : AndroidViewModel(application) {
    private val uiPreferences = application.getSharedPreferences("c1_ui_preferences", Application.MODE_PRIVATE)

    val repository: C1LearningRepository = (application as? MainApplication)?.learningRepository ?: C1LearningRepository()
    val speechHelper = AudioSpeechHelper(application)
    val streakRepository: com.example.data.repository.DailyStreakRepository =
        (application as? MainApplication)?.dailyStreakRepository
            ?: com.example.data.repository.DailyStreakRepository(
                ((application as? MainApplication)?.database ?: C1AppDatabase.getDatabase(application)).dailyStreakDao()
            )

    val speakingRepository: com.example.data.repository.SpeakingPracticeRepository =
        (application as? MainApplication)?.speakingPracticeRepository
            ?: com.example.data.repository.SpeakingPracticeRepository()

    val streakState: StateFlow<DailyStreakState> = streakRepository.streakState

    val legacyProgressNote = "Unbewertete Legacy-Übung: Nullwerte bedeuten nicht gemessen. Vorhandene Einstufungswerte sind Legacy-Schätzungen, kein C1- oder Prüfungsnachweis. Tippen, Anhören, Musterlösungen und Sitzungsabschluss erhöhen keine Kompetenzwerte."

    fun recordPracticeActivity(activityName: String = "C1 Training") {
        viewModelScope.launch {
            try {
                streakRepository.recordActivity(activityName)
            } catch (e: Exception) {
                Log.e("C1ViewModel", "Error recording streak activity", e)
            }
        }
    }

    // Modern Theme Mode: true = Deep Obsidian-Navy Dark (default), false = Clean Light
    private val _isDarkTheme = MutableStateFlow(uiPreferences.getBoolean("dark_theme", true))
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
        uiPreferences.edit().putBoolean("dark_theme", _isDarkTheme.value).apply()
    }

    // Baseline Diagnostic State (First-use onboarding check)
    private val _hasCompletedBaseline = MutableStateFlow(false)
    val hasCompletedBaseline: StateFlow<Boolean> = _hasCompletedBaseline.asStateFlow()

    // Current Screen
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Home)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    // Screen navigation stack
    private val screenStack = mutableListOf<Screen>(Screen.Home)

    init {
        checkBaselineDiagnosticProfile()
    }

    fun checkBaselineDiagnosticProfile() {
        viewModelScope.launch {
            try {
                val db = (getApplication() as? MainApplication)?.database ?: C1AppDatabase.getDatabase(getApplication())
                val existing = db.baselineProfileDao().getBaselineProfile()
                if (existing != null && existing.isCompleted) {
                    _hasCompletedBaseline.value = true
                    val scoreMap = mapOf(
                        SkillCategory.HOERVERSTEHEN to existing.listeningScore,
                        SkillCategory.SPONTANES_SPRECHEN to existing.speakingScore,
                        SkillCategory.INTERAKTION to existing.interactionScore,
                        SkillCategory.LESEVERSTEHEN to existing.readingScore,
                        SkillCategory.SCHREIBEN to existing.writingScore,
                        SkillCategory.GRAMMATIK to existing.grammarScore,
                        SkillCategory.WORTSCHATZ to existing.vocabularyScore,
                        SkillCategory.MEDIATION to existing.mediationScore,
                        SkillCategory.AUSSPRACHE_PROSODIE to ((existing.speakingScore + existing.listeningScore) / 2)
                    )
                    repository.applyDiagnosticResults(existing, scoreMap)
                } else {
                    _hasCompletedBaseline.value = false
                    // Keep user on Screen.Home so they experience the modern dashboard and start diagnostic from the hero card
                }
            } catch (e: Exception) {
                Log.e("C1ViewModel", "Error checking baseline diagnostic status", e)
            }
        }
    }

    fun onBaselineAssessmentCompleted(profile: BaselineProfileEntity, scoreMap: Map<SkillCategory, Int>) {
        _hasCompletedBaseline.value = true
        repository.applyDiagnosticResults(profile, scoreMap)
        recordPracticeActivity("C1 Basis-Diagnostik")
        // Transition back to Home with personalized plan activated
        screenStack.clear()
        screenStack.add(Screen.Home)
        _currentScreen.value = Screen.Home
    }

    fun navigateTo(screen: Screen) {
        if (_currentScreen.value != screen) {
            screenStack.add(screen)
            _currentScreen.value = screen
        }
    }

    fun navigateBack(): Boolean {
        if (screenStack.size > 1) {
            screenStack.removeAt(screenStack.size - 1)
            _currentScreen.value = screenStack.last()
            speechHelper.stop()
            return true
        }
        return false
    }

    // ----------------------------------------------------
    // 18-Minute Daily Session State
    // ----------------------------------------------------
    private val _activeSession = MutableStateFlow<DailyTrainingSession?>(null)
    val activeSession: StateFlow<DailyTrainingSession?> = _activeSession.asStateFlow()

    private val _currentPhase = MutableStateFlow(SessionPhase.WARM_UP)
    val currentPhase: StateFlow<SessionPhase> = _currentPhase.asStateFlow()

    private val _warmUpSelectedOption = MutableStateFlow<Int?>(null)
    val warmUpSelectedOption: StateFlow<Int?> = _warmUpSelectedOption.asStateFlow()

    private val _warmUpAnswerRevealed = MutableStateFlow(false)
    val warmUpAnswerRevealed: StateFlow<Boolean> = _warmUpAnswerRevealed.asStateFlow()

    private val _productionUserInput = MutableStateFlow("")
    val productionUserInput: StateFlow<String> = _productionUserInput.asStateFlow()

    private val _productionRecordingActive = MutableStateFlow(false)
    val productionRecordingActive: StateFlow<Boolean> = _productionRecordingActive.asStateFlow()

    private val _transferUserInput = MutableStateFlow("")
    val transferUserInput: StateFlow<String> = _transferUserInput.asStateFlow()

    private val _sessionCompleted = MutableStateFlow(false)
    val sessionCompleted: StateFlow<Boolean> = _sessionCompleted.asStateFlow()

    fun startDailySession() {
        val session = repository.generateDailySession()
        _activeSession.value = session
        _currentPhase.value = SessionPhase.WARM_UP
        _warmUpSelectedOption.value = null
        _warmUpAnswerRevealed.value = false
        _productionUserInput.value = ""
        _transferUserInput.value = ""
        _sessionCompleted.value = false
        navigateTo(Screen.SessionRunner)
    }

    fun selectWarmUpOption(index: Int) {
        _warmUpSelectedOption.value = index
    }

    fun revealWarmUpAnswer() {
        _warmUpAnswerRevealed.value = true
    }

    fun advanceSessionPhase() {
        if (_activeSession.value == null || _sessionCompleted.value) return
        val next = when (_currentPhase.value) {
            SessionPhase.WARM_UP -> SessionPhase.MAIN_CHALLENGE
            SessionPhase.MAIN_CHALLENGE -> SessionPhase.ACTIVE_PRODUCTION
            SessionPhase.ACTIVE_PRODUCTION -> SessionPhase.PRECISION_CORRECTION
            SessionPhase.PRECISION_CORRECTION -> SessionPhase.TRANSFER_TEST
            SessionPhase.TRANSFER_TEST -> {
                // Complete session
                _sessionCompleted.value = true

                recordPracticeActivity("18-Min. Adaptiv-Session")
                return
            }
        }
        _currentPhase.value = next
    }

    fun updateProductionInput(text: String) {
        _productionUserInput.value = text
    }

    fun toggleProductionRecording() {
        // This session has no speech recognizer; never manufacture a learner response.
        _productionRecordingActive.value = false
    }

    fun updateTransferInput(text: String) {
        _transferUserInput.value = text
    }

    // ----------------------------------------------------
    // Listening Laboratory State
    // ----------------------------------------------------
    private val _selectedLabItemIndex = MutableStateFlow(0)
    val selectedLabItemIndex: StateFlow<Int> = _selectedLabItemIndex.asStateFlow()

    private val _playbackSpeed = MutableStateFlow(1.0f)
    val playbackSpeed: StateFlow<Float> = _playbackSpeed.asStateFlow()

    private val _isBlindTranscriptRevealed = MutableStateFlow(false)
    val isBlindTranscriptRevealed: StateFlow<Boolean> = _isBlindTranscriptRevealed.asStateFlow()

    private val _selectedLabQuizAnswers = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val selectedLabQuizAnswers: StateFlow<Map<Int, Int>> = _selectedLabQuizAnswers.asStateFlow()

    fun selectLabItem(index: Int) {
        speechHelper.stop()
        _selectedLabItemIndex.value = index
        _isBlindTranscriptRevealed.value = false
        _selectedLabQuizAnswers.value = emptyMap()
    }

    fun setPlaybackSpeed(speed: Float) {
        _playbackSpeed.value = speed
    }

    fun playCurrentLabAudio() {
        val items = repository.listeningItems.value
        val item = items.getOrNull(_selectedLabItemIndex.value) ?: return
        speechHelper.speak(item.audioSpeakerText, _playbackSpeed.value)
    }

    fun stopAudio() {
        speechHelper.stop()
    }

    fun revealBlindTranscript() {
        _isBlindTranscriptRevealed.value = true
    }

    fun answerLabQuestion(questionIndex: Int, answerIndex: Int) {
        _selectedLabQuizAnswers.update { it + (questionIndex to answerIndex) }
        recordPracticeActivity("Hör-Labor")
    }

    // ----------------------------------------------------
    // Conversation Simulator State
    // ----------------------------------------------------
    private val _activeConversationIndex = MutableStateFlow(0)
    val activeConversationIndex: StateFlow<Int> = _activeConversationIndex.asStateFlow()

    private val _currentConversationTurnIndex = MutableStateFlow(0)
    val currentConversationTurnIndex: StateFlow<Int> = _currentConversationTurnIndex.asStateFlow()

    private val _interruptionTriggered = MutableStateFlow(false)
    val interruptionTriggered: StateFlow<Boolean> = _interruptionTriggered.asStateFlow()

    private val _conversationUserResponse = MutableStateFlow("")
    val conversationUserResponse: StateFlow<String> = _conversationUserResponse.asStateFlow()

    private val _responseLatencySeconds = MutableStateFlow(0)
    val responseLatencySeconds: StateFlow<Int> = _responseLatencySeconds.asStateFlow()

    private var timerJob: Job? = null
    private var conversationResponseSubmitted = false

    fun selectConversation(index: Int) {
        conversationResponseSubmitted = false
        _activeConversationIndex.value = index
        _currentConversationTurnIndex.value = 0
        _interruptionTriggered.value = false
        _conversationUserResponse.value = ""
        _responseLatencySeconds.value = 0
        playOpeningStatement()
    }

    fun playOpeningStatement() {
        val conv = repository.conversationScenarios.value.getOrNull(_activeConversationIndex.value) ?: return
        speechHelper.speakAs(conv.openingStatement, SpeakerProfile.PARTNER, "conversation-opening")
        startLatencyTimer()
    }

    private fun startLatencyTimer() {
        timerJob?.cancel()
        _responseLatencySeconds.value = 0
        timerJob = viewModelScope.launch {
            try {
                val conv = repository.conversationScenarios.value.getOrNull(_activeConversationIndex.value)
                val interruptSec = conv?.unexpectedInterruption?.triggerAfterSecond ?: 6
                while (true) {
                    delay(1000)
                    _responseLatencySeconds.value += 1
                    if (_responseLatencySeconds.value == interruptSec && !_interruptionTriggered.value) {
                        _interruptionTriggered.value = true
                        val interruptText = conv?.unexpectedInterruption?.partnerInterruptSpeech ?: ""
                        speechHelper.speakAs(interruptText, SpeakerProfile.INTERRUPTER, "conversation-interruption")
                    }
                }
            } catch (e: Exception) {
                Log.d("C1ViewModel", "Latency timer finished or cancelled: ${e.message}")
            }
        }
    }

    fun updateConversationResponse(text: String) {
        _conversationUserResponse.value = text
    }

    fun submitConversationResponse(text: String) {
        if (text.isBlank() || conversationResponseSubmitted) return
        timerJob?.cancel()
        _conversationUserResponse.value = text
        conversationResponseSubmitted = true
        recordPracticeActivity("Gesprächssimulator (unbewertete Übung)")
    }

    fun nextConversationTurn() {
        val conv = repository.conversationScenarios.value.getOrNull(_activeConversationIndex.value) ?: return
        if (_currentConversationTurnIndex.value < conv.turns.size - 1) {
            conversationResponseSubmitted = false
            _currentConversationTurnIndex.value += 1
            _conversationUserResponse.value = ""
            _interruptionTriggered.value = false
            val turn = conv.turns[_currentConversationTurnIndex.value]
            speechHelper.speakAs(turn.partnerSpeech, turn.speakerProfile, "conversation-turn-${_currentConversationTurnIndex.value}")
            startLatencyTimer()
        }
    }

    // ----------------------------------------------------
    // Error Intelligence State
    // ----------------------------------------------------
    private val _selectedErrorForDrill = MutableStateFlow<ErrorIntelligenceItem?>(null)
    val selectedErrorForDrill: StateFlow<ErrorIntelligenceItem?> = _selectedErrorForDrill.asStateFlow()

    private val _errorDrillUserAnswer = MutableStateFlow("")
    val errorDrillUserAnswer: StateFlow<String> = _errorDrillUserAnswer.asStateFlow()

    private val _errorDrillFeedback = MutableStateFlow<String?>(null)
    val errorDrillFeedback: StateFlow<String?> = _errorDrillFeedback.asStateFlow()

    fun selectErrorForDrill(error: ErrorIntelligenceItem) {
        _selectedErrorForDrill.value = error
        _errorDrillUserAnswer.value = ""
        _errorDrillFeedback.value = null
    }

    fun clearSelectedErrorForDrill() {
        _selectedErrorForDrill.value = null
        _errorDrillUserAnswer.value = ""
        _errorDrillFeedback.value = null
    }

    fun updateErrorDrillAnswer(text: String) {
        _errorDrillUserAnswer.value = text
    }

    fun submitErrorDrillAnswer() {
        val error = _selectedErrorForDrill.value ?: return
        _errorDrillFeedback.value = "Musterlösung (unbewerteter Vergleich, kein Beherrschungsnachweis): ${error.transferTargetAnswer}"
    }

    fun advanceErrorStage(errorId: String) {
        repository.advanceErrorMastery(errorId)
    }

    // ----------------------------------------------------
    // Baseline Diagnostic State
    // ----------------------------------------------------
    private val _diagnosticStep = MutableStateFlow(0)
    val diagnosticStep: StateFlow<Int> = _diagnosticStep.asStateFlow()

    private val _diagnosticAnswers = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val diagnosticAnswers: StateFlow<Map<Int, Int>> = _diagnosticAnswers.asStateFlow()

    val diagnosticQuestions = listOf(
        DiagnosticQuestion(
            id = "diag_1",
            dimension = SkillCategory.HOERVERSTEHEN,
            promptText = "Hörprobe: 'Also pass amol uff, des hamma scho dreimol durchgekaut.' – Was signalisiert diese Aussage primär?",
            options = listOf(
                "Sachliche Bitte um Wiederholung",
                "Ungeduld und Hinweis auf redundante Diskussion im süddeutschen Dialekt",
                "Verständnislosigkeit über eine mathematische Berechnung",
                "Freude über ein vertrautes Gesprächsthema"
            ),
            correctIndex = 1,
            analysisRationale = "Erkenntnis von Dialektverschmelzung ('pass amol uff', 'des hamma') und idiomatischem Tonfall ('durchgekaut')."
        ),
        DiagnosticQuestion(
            id = "diag_2",
            dimension = SkillCategory.GRAMMATIK,
            promptText = "Welcher Satz drückt eine hypothetische Bedingung in gehobenem C1-Stil korrekt aus?",
            options = listOf(
                "Wenn wir das früher gewusst hätten, hätten wir anders reagiert.",
                "Hätten wir dies früher in Erfahrung gebracht, so wäre anders verfahren worden.",
                "Hätten wir das früher wissen tun, hätten wir anders gehandelt.",
                "Wenn wir es früher wissen würden, hätten wir reagiert."
            ),
            correctIndex = 1,
            analysisRationale = "Uneingeleiteter Konditionalsatz mit Inversion ('Hätten wir...') und unpersönlichem Passiv im Konjunktiv II."
        ),
        DiagnosticQuestion(
            id = "diag_3",
            dimension = SkillCategory.WORTSCHATZ,
            promptText = "Welche Kollokation drückt 'etwas offiziell bestreiten' am treffendsten aus?",
            options = listOf(
                "etwas in Zweifel machen",
                "etwas in Abrede stellen",
                "etwas aus der Hand weisen",
                "etwas nach Streit bringen"
            ),
            correctIndex = 1,
            analysisRationale = "'In Abrede stellen' ist die idiomatisch hochgradig präzise C1-Nomen-Verb-Verbindung."
        ),
        DiagnosticQuestion(
            id = "diag_4",
            dimension = SkillCategory.MEDIATION,
            promptText = "Wie fassen Sie den Satz: 'Die Evidenzbasis divergiert signifikant von den postulierten Axiomen' laienverständlich zusammen?",
            options = listOf(
                "Die Zahlen stimmen überhaupt nicht mit dem überein, was vorher behauptet wurde.",
                "Die Evidenz ist eine ganz andere als das Axiom.",
                "Wir müssen signifikant divergieren.",
                "Die Daten sind unvollständig."
            ),
            correctIndex = 0,
            analysisRationale = "Erfolgreiche Sprachmittlung: Abstrakte Fachbegriffe in kristallklare, bürgernahe deutsche Sprache transferiert."
        )
    )

    fun answerDiagnosticQuestion(qIndex: Int, answerIndex: Int) {
        _diagnosticAnswers.update { it + (qIndex to answerIndex) }
    }

    fun nextDiagnosticStep() {
        if (_diagnosticStep.value < diagnosticQuestions.size - 1) {
            _diagnosticStep.value += 1
        } else {
            // Finished diagnostic
            navigateTo(Screen.Home)
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        speechHelper.shutdown()
    }
}
