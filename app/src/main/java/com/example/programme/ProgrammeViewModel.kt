package com.example.programme

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.MainApplication
import java.io.File
import java.util.UUID
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class ProgrammeTheme(val label: String) {
    TRACKSIDE("Trackside"), SILVER("Silver"), PITCH("Pitch green")
}

enum class ProgrammeLanguage(val label: String) {
    DE("Deutsch"), EN("English");

    fun text(german: String, english: String): String =
        if (this == DE) german else english
}

data class ProgrammePreferences(
    val theme: ProgrammeTheme = ProgrammeTheme.TRACKSIDE,
    val language: ProgrammeLanguage = ProgrammeLanguage.DE,
    val learnerName: String = "",
    val compact: Boolean = false,
    val dailyMinutes: Int = 20
)

data class ProgrammePlanStep(val taskId: String, val title: String, val minutes: Int)

data class ProgrammeQuizResult(val option: Int?, val correct: Boolean?, val assisted: Boolean)

class ProgrammeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = (application as MainApplication).programmeRepository
    private val realLifeRepository = (application as MainApplication).realLifeExperienceRepository
    private val feedbackRepository = (application as MainApplication).feedbackChallengeRepository
    private val humanReviewRepository = (application as MainApplication).humanReviewRepository
    private val assessmentSessionRepository = (application as MainApplication).assessmentSessionRepository
    private val externalListeningPracticeRepository = (application as MainApplication).externalListeningPracticeRepository
    private val practiceDraftRepository = (application as MainApplication).practiceDraftRepository
    private val bookAudioSelectionRepository = (application as MainApplication).bookAudioSelectionRepository
    private val reviewerWorkflowRepository = (application as MainApplication).reviewerWorkflowRepository
    private val storage = application.getSharedPreferences("programme_dashboard", Context.MODE_PRIVATE)
    val lastUnitId: String? = storage.getString("last_unit_id", null)
    val lastTaskId: String = storage.getString("last_task_id", "retrieval").orEmpty()
    val onboardingComplete: Boolean = storage.getBoolean("onboarding_complete", false)
    private val _preferences = MutableStateFlow(
        ProgrammePreferences(
            theme = ProgrammeTheme.values().firstOrNull {
                it.name == storage.getString("theme", null)
            } ?: ProgrammeTheme.TRACKSIDE,
            language = ProgrammeLanguage.values().firstOrNull {
                it.name == storage.getString("language", null)
            } ?: ProgrammeLanguage.DE,
            learnerName = storage.getString("learner_name", "").orEmpty(),
            compact = storage.getBoolean("compact", false),
            dailyMinutes = storage.getInt("daily_minutes", 20).takeIf { it in listOf(20, 60, 90) } ?: 20
        )
    )
    val preferences = _preferences.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()
    private val _saving = MutableStateFlow(false)
    val saving = _saving.asStateFlow()
    private val _loading = MutableStateFlow(true)
    val loading = _loading.asStateFlow()
    private val _historyAvailable = MutableStateFlow(false)
    val historyAvailable = _historyAvailable.asStateFlow()
    private val _notice = MutableStateFlow<String?>(null)
    val notice = _notice.asStateFlow()
    private val _quizResults = MutableStateFlow<Map<String, ProgrammeQuizResult>>(emptyMap())
    val quizResults = _quizResults.asStateFlow()
    private val _assistedUnits = MutableStateFlow(
        storage.getStringSet("assisted_units", emptySet()).orEmpty().toSet()
    )
    val assistedUnits = _assistedUnits.asStateFlow()
    private val _bookmarkedUnitIds = MutableStateFlow(
        storage.getStringSet("bookmarked_unit_ids", emptySet()).orEmpty().toSet()
    )
    val bookmarkedUnitIds = _bookmarkedUnitIds.asStateFlow()

    val realLifeExperiences: StateFlow<List<RealLifeExperience>> = realLifeRepository.experiences
        .catch {
            _error.value = "Persönliche Alltagseinträge konnten nicht geladen werden."
            emit(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val attemptIndex: StateFlow<AttemptHistoryIndex> = repository.attempts
        .onEach {
            _loading.value = false
            _historyAvailable.value = true
        }
        .catch {
            _loading.value = false
            _historyAvailable.value = false
            _error.value = "Lernverlauf konnte nicht geladen werden. Bitte die App neu starten. Es werden keine Fortschritte angenommen."
        }
        .scan(AttemptHistoryIndex.empty()) { index, rows -> index.update(rows) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, AttemptHistoryIndex.empty())

    val attempts: StateFlow<List<Attempt>> = attemptIndex
        .map { it.attempts }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val attemptsByUnit: StateFlow<Map<String, List<Attempt>>> = attemptIndex
        .map { it.byUnit }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyMap())

    val feedbackChallenges: StateFlow<List<FeedbackChallenge>> = feedbackRepository.challenges
        .catch {
            _error.value = "Feedback-Herausforderungen konnten nicht geladen werden."
            emit(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val humanReviews: StateFlow<List<HumanReviewSubmission>> = humanReviewRepository.reviews
        .catch {
            _error.value = "Lokale Review-Notizen konnten nicht geladen werden."
            emit(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val assessmentHistory: StateFlow<List<PersistedAssessment>> = assessmentSessionRepository.history
        .catch {
            _error.value = "Assessment-Verlauf konnte nicht geladen werden."
            emit(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val reviewAssignments: StateFlow<List<ReviewAssignment>> = reviewerWorkflowRepository.assignments
        .catch { _error.value = "Reviewer-Zuweisungen konnten nicht geladen werden."; emit(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val reviewAuditEvents: StateFlow<List<ReviewAuditEvent>> = reviewerWorkflowRepository.auditEvents
        .catch { _error.value = "Review-Audit konnte nicht geladen werden."; emit(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val reassessmentLinks: StateFlow<List<ReassessmentLink>> = reviewerWorkflowRepository.reassessments
        .catch { _error.value = "Reassessment-Verknüpfungen konnten nicht geladen werden."; emit(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    suspend fun exportLocalJson(): String {
        val app = getApplication<MainApplication>()
        val database = app.database
        return ProgrammeExport.create(
            attempts = repository.attempts.first(),
            experiences = realLifeRepository.experiences.first(),
            assessments = assessmentSessionRepository.history.first(),
            externalListeningNotes = externalListeningPracticeRepository.savedDrafts(),
            practiceDrafts = practiceDraftRepository.savedDrafts(),
            feedbackChallenges = feedbackRepository.challenges.first(),
            humanReviews = humanReviewRepository.reviews.first(),
            reviewAssignments = reviewerWorkflowRepository.assignments.first(),
            reviewAuditEvents = reviewerWorkflowRepository.auditEvents.first(),
            reassessmentLinks = reviewerWorkflowRepository.reassessments.first(),
            baselineProfile = database.baselineProfileDao().getBaselineProfileFlow().first(),
            vocabularyItems = database.vocabularyItemDao().getAllVocabularyFlow().first(),
            proficiencySnapshots = database.proficiencySnapshotDao().getAllSnapshotsDirect(),
            dailyStreak = database.dailyStreakDao().observeStreak().first(),
            preferences = _preferences.value,
            bookmarkedUnitIds = _bookmarkedUnitIds.value,
            assistedUnitIds = _assistedUnits.value,
            lastUnitId = storage.getString("last_unit_id", null),
            lastTaskId = storage.getString("last_task_id", "retrieval").orEmpty(),
            onboardingComplete = storage.getBoolean("onboarding_complete", false)
        )
    }

    fun assignReview(attemptId: String, reviewer: HumanReviewer, dueAt: Long? = null) {
        viewModelScope.launch {
            try {
                reviewerWorkflowRepository.assign(attemptId, reviewer, dueAt, System.currentTimeMillis())
                _notice.value = "Review-Aufgabe zugewiesen und protokolliert."
            } catch (cancelled: CancellationException) { throw cancelled }
            catch (_: Exception) { _error.value = "Die Review-Zuweisung konnte nicht gespeichert werden." }
        }
    }

    fun recordHumanReview(
        attemptId: String,
        reviewerName: String,
        comment: String,
        scores: Map<Dimension, Int>
    ) {
        val sourceAttempt = attempts.value.firstOrNull { it.id == attemptId }
        if (sourceAttempt == null) {
            _error.value = "Der zugehörige Lernversuch wurde nicht gefunden."
            return
        }
        val name = reviewerName.trim()
        if (name.isBlank()) {
            _error.value = "Bitte den lokalen Review-Namen eingeben."
            return
        }
        if (Dimension.values().any { scores[it] !in 0..4 }) {
            _error.value = "Bitte alle zehn Rubrikdimensionen mit 0 bis 4 bewerten."
            return
        }
        val review = HumanReviewSubmission(
            id = "local-review-${UUID.randomUUID()}",
            attemptId = sourceAttempt.id,
            reviewer = HumanReviewer(
                id = "local-reviewer-${UUID.randomUUID()}",
                displayName = name,
                verified = false
            ),
            rubricVersion = 1,
            scores = scores.toMap(),
            comment = comment.trim().take(2_000),
            createdAt = System.currentTimeMillis()
        )
        viewModelScope.launch {
            try {
                humanReviewRepository.record(review)
                _error.value = null
                _notice.value = "Lokale Review-Notiz gespeichert; Identität und Bewertung sind nicht verifiziert."
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                _error.value = "Die lokale Review-Notiz konnte nicht gespeichert werden."
            }
        }
    }

    fun linkReassessment(attemptId: String, reviewId: String?, unitId: String, taskId: String) {
        viewModelScope.launch {
            try {
                reviewerWorkflowRepository.linkReassessment(attemptId, reviewId, unitId, taskId, System.currentTimeMillis())
                _notice.value = "Reassessment mit dem ursprünglichen Versuch verknüpft."
            } catch (cancelled: CancellationException) { throw cancelled }
            catch (_: Exception) { _error.value = "Die Reassessment-Verknüpfung konnte nicht gespeichert werden." }
        }
    }

    fun deleteAllLocalData(onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val app = getApplication<MainApplication>()
                    app.database.clearAllTables()
                    reviewerWorkflowRepository.deleteAll()
                    externalListeningPracticeRepository.clearAll()
                    practiceDraftRepository.clearAll()
                    bookAudioSelectionRepository.clear()
                    LocalAssessmentAudioStore(app).deleteAll()
                    clearLocalExportCache()
                }
                storage.edit().clear().apply()
                _preferences.value = ProgrammePreferences()
                _assistedUnits.value = emptySet()
                _bookmarkedUnitIds.value = emptySet()
                _notice.value = "Alle lokalen Lerndaten wurden gelöscht."
                onComplete()
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (exception: Exception) {
                _error.value = "Die lokalen Lerndaten konnten nicht vollständig gelöscht werden: " +
                    (exception.localizedMessage ?: "Unbekannter Fehler.")
            }
        }
    }

    private fun clearLocalExportCache() {
        val directory = File(getApplication<Application>().cacheDir, "exports")
        if (!directory.exists()) return
        val exportFiles = directory.listFiles()
            ?: throw IllegalStateException("Could not inspect the local export cache.")
        exportFiles.filter {
            it.isFile && it.name.startsWith("c1-deutsch-export-") && it.extension == "json"
        }.forEach { file ->
            check(file.delete()) { "Could not delete a temporary learner export." }
        }
    }

    fun challengeFeedback(
        claim: FeedbackClaim,
        decision: FeedbackChallengeDecision,
        correction: String?
    ) {
        val sourceAttempt = attempts.value.firstOrNull { it.id == claim.attemptId }
        if (sourceAttempt == null || !FeedbackEvidenceEngine.validate(sourceAttempt.response, claim).valid) {
            _error.value = "Feedback-Herausforderung ohne belegbaren Antwortausschnitt abgelehnt."
            return
        }
        val challenge = FeedbackEvidenceEngine.challenge(
            claim,
            decision,
            correction,
            System.currentTimeMillis()
        )
        viewModelScope.launch {
            try {
                feedbackRepository.record(challenge)
                _notice.value = "Feedback markiert. Diese Korrektur wird nicht stillschweigend als richtig übernommen."
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                _error.value = "Die Feedback-Herausforderung konnte nicht gespeichert werden."
            }
        }
    }

    fun resolveFeedbackChallenge(
        challengeId: String,
        status: FeedbackChallengeStatus,
        correctedFeedback: String?
    ) {
        viewModelScope.launch {
            try {
                feedbackRepository.resolve(challengeId, status, correctedFeedback, System.currentTimeMillis())
                _notice.value = "Feedback-Herausforderung als ${status.name} gespeichert."
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                _error.value = "Die Auflösung der Feedback-Herausforderung konnte nicht gespeichert werden."
            }
        }
    }

    fun updateName(name: String) {
        val value = name.take(60)
        storage.edit().putString("learner_name", value).apply()
        _preferences.value = _preferences.value.copy(learnerName = value)
    }

    fun updateTheme(theme: ProgrammeTheme) {
        storage.edit().putString("theme", theme.name).apply()
        _preferences.value = _preferences.value.copy(theme = theme)
    }

    fun updateLanguage(language: ProgrammeLanguage) {
        storage.edit().putString("language", language.name).apply()
        _preferences.value = _preferences.value.copy(language = language)
    }

    fun updateCompact(compact: Boolean) {
        storage.edit().putBoolean("compact", compact).apply()
        _preferences.value = _preferences.value.copy(compact = compact)
    }

    fun toggleBookmark(unitId: String) {
        if (CurriculumCatalogue.units.none { it.id == unitId }) return
        val updated = _bookmarkedUnitIds.value.toMutableSet().apply {
            if (!add(unitId)) remove(unitId)
        }.toSet()
        storage.edit().putStringSet("bookmarked_unit_ids", updated).apply()
        _bookmarkedUnitIds.value = updated
    }

    fun updateDailyMinutes(minutes: Int) {
        if (minutes !in listOf(20, 60, 90)) return
        storage.edit().putInt("daily_minutes", minutes).apply()
        _preferences.value = _preferences.value.copy(dailyMinutes = minutes)
    }

    fun addCourseExpressionsToRetrieval(unitId: String) {
        viewModelScope.launch {
            try {
                val inserted = (getApplication<MainApplication>().vocabularySrsRepository)
                    .seedCurriculumExpressions(unitId = unitId)
                _notice.value = if (inserted > 0) {
                    "$inserted Kursausdrücke wurden zum aktiven Abruftraining hinzugefügt."
                } else {
                    "Diese Kursausdrücke sind bereits im aktiven Abruftraining."
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                _error.value = "Die Kursausdrücke konnten nicht ins Abruftraining übernommen werden."
            }
        }
    }

    fun completeOnboarding() {
        storage.edit().putBoolean("onboarding_complete", true).apply()
    }

    fun rememberLesson(unitId: String, taskId: String) {
        storage.edit()
            .putString("last_unit_id", unitId)
            .putString("last_task_id", taskId)
            .apply()
    }

    fun recordRealLifeExperience(
        context: ContextTrack,
        situation: String,
        heard: String,
        intended: String,
        said: String,
        issueType: RealLifeIssueType
    ) {
        val cleanedSituation = situation.trim()
        val cleanedSaid = said.trim()
        if (cleanedSituation.isBlank() || cleanedSaid.isBlank()) {
            _error.value = "Bitte beschreibe die Situation und was du gesagt hast."
            return
        }
        val now = System.currentTimeMillis()
        val experience = RealLifeExperience(
            id = UUID.randomUUID().toString(),
            createdAt = now,
            context = context,
            situation = cleanedSituation.take(500),
            heard = heard.trim().take(500),
            intended = intended.trim().take(500),
            said = cleanedSaid.take(500),
            issueType = issueType,
            reviewStage = 0,
            nextReviewAt = now + RealLifeExperienceRepository.DAY_MILLIS
        )
        viewModelScope.launch {
            try {
                realLifeRepository.record(experience)
                _notice.value = "Gespeichert. Morgen bekommst du einen kurzen Reparatur- und Transfer-Schritt."
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                _error.value = "Der Alltagseintrag konnte nicht gespeichert werden."
            }
        }
    }

    fun completeRealLifeReview(experience: RealLifeExperience, response: String) {
        viewModelScope.launch {
            try {
                realLifeRepository.completeReview(experience, response, System.currentTimeMillis())
                _notice.value = "Reparaturschritt verschoben. Die nächste Variation kommt später."
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                _error.value = "Der Reparaturschritt konnte nicht aktualisiert werden."
            }
        }
    }

    fun plan(
        minutes: Int,
        realLifeExperiences: List<RealLifeExperience> = emptyList(),
        now: Long = System.currentTimeMillis(),
        attempts: List<Attempt> = emptyList()
    ): List<ProgrammePlanStep> {
        // Receptive and productive halves stay equal; retrieval is productive, not rereading.
        val times = when (minutes) {
            60 -> listOf(5, 12, 12, 6, 5, 6, 6, 8)
            90 -> listOf(8, 18, 18, 9, 7, 10, 10, 10)
            else -> listOf(2, 3, 4, 3, 2, 2, 2, 2)
        }
        val steps = listOf(
            "retrieval" to "Abruf ohne Vorlage",
            "reading" to "Lesen & Kernaussagen",
            "listening" to "Hören mit synthetischer Stimme",
            "quiz" to "Verständnis prüfen",
            "controlled" to "Sprachmittel anwenden",
            "speaking" to "Frei sprechen",
            "interaction" to "Dialog / Rollenwechsel",
            "writing" to "Schreiben & reflektieren"
        )
        val focusTask = AdaptiveControlEngine.decide(
            attempts = attempts,
            experiences = realLifeExperiences,
            now = now
        )
        val ordered = focusTask.taskOrder.mapNotNull { taskId ->
            steps.firstOrNull { it.first == taskId }
        }.ifEmpty { steps }
        val timeByTask = steps.mapIndexed { index, (id, _) -> id to times[index] }.toMap()
        val adjustedTimes = focusTask.focusMinutes?.let { focusMinutes ->
            val id = focusTask.primaryTaskId
            if (id == null) timeByTask else {
                val delta = focusMinutes - (timeByTask[id] ?: focusMinutes)
                val donor = timeByTask.keys
                    .filter { it != id }
                    .maxByOrNull { timeByTask.getValue(it) }
                if (donor == null || delta <= 0) timeByTask
                else timeByTask + (id to focusMinutes) +
                    (donor to (timeByTask.getValue(donor) - delta).coerceAtLeast(1))
            }
        } ?: timeByTask
        return ordered.map { (id, title) -> ProgrammePlanStep(id, title, adjustedTimes.getValue(id)) }
    }

    fun revealModel(unitId: String) {
        if (CurriculumCatalogue.units.none { it.id == unitId && it.content != null }) return
        val updated = _assistedUnits.value + unitId
        storage.edit().putStringSet("assisted_units", updated).apply()
        _assistedUnits.value = updated
    }

    fun clearFeedback() {
        _error.value = null
        _notice.value = null
    }

    private fun availableUnit(unitId: String): CurriculumUnit? {
        if (!_historyAvailable.value) {
            _error.value = "Bitte warten, bis der Lernverlauf verfügbar ist."
            return null
        }
        val unit = CurriculumCatalogue.units.find { it.id == unitId }
        // Practice access is not a prerequisite waiver or a mastery decision.
        if (unit == null || unit.content == null) {
            _error.value = "Diese Einheit ist noch nicht ausgearbeitet."
            return null
        }
        return unit
    }

    fun submitQuiz(unitId: String, option: Int?, context: ContextTrack) {
        if (_saving.value) return
        val unit = availableUnit(unitId) ?: return
        val content = unit.content ?: return
        if (option != null && option !in content.options.indices) return
        val correct = option?.let { it == content.correctOption }
        val assisted = unitId in _assistedUnits.value
        val attempt = Attempt(
            id = UUID.randomUUID().toString(), unitId = unitId, taskId = "quiz",
            timestamp = System.currentTimeMillis(),
            response = option?.let { "Option ${it + 1}: ${content.options[it]}" } ?: "Weiß ich noch nicht",
            source = if (option == null) EvidenceSource.SELF_REFLECTION else EvidenceSource.AUTOMATIC_QUIZ,
            scores = if (correct == null) emptyMap() else mapOf(Dimension.COMPREHENSION to if (correct) 4 else 0),
            assisted = assisted, context = context,
            errors = if (correct == false) listOf("Verständnisfrage: ${content.question}") else emptyList()
        )
        save(attempt) {
            _quizResults.value = _quizResults.value + (unitId to ProgrammeQuizResult(option, correct, assisted))
        }
    }

    fun recordPractice(unitId: String, taskId: String, response: String, context: ContextTrack) {
        if (_saving.value) return
        if (taskId !in setOf(
                "retrieval", "reading", "listening", "controlled", "speaking",
                "interaction", "writing", "transfer", "context-application"
            ) &&
            !taskId.startsWith("assessment:")
        ) return
        val unit = availableUnit(unitId) ?: return
        if (response.isBlank()) {
            _error.value = "Bitte zuerst eine Antwort oder eine ehrliche Reflexion eintragen."
            return
        }
        val detectedErrors = AdaptiveLearning.detectResponseSignals(response, unit)
        save(
            Attempt(
                id = UUID.randomUUID().toString(), unitId = unit.id, taskId = taskId,
                timestamp = System.currentTimeMillis(), response = response.trim(),
                source = EvidenceSource.SELF_REFLECTION, assisted = unitId in _assistedUnits.value,
                context = context,
                errors = detectedErrors.map { "${it.code}: ${it.explanation}" }
            )
        ) {
            practiceDraftRepository.clear(unitId, taskId, context)
        }
    }

    fun recordAssessmentResponse(task: DeliveredAssessmentTask, response: String) {
        if (response.isBlank()) {
            _error.value = "Bitte zuerst die Assessment-Antwort eintragen."
            return
        }
        val unit = availableUnit(task.unitId) ?: return
        save(
            Attempt(
                id = UUID.randomUUID().toString(),
                unitId = unit.id,
                taskId = task.taskId,
                timestamp = System.currentTimeMillis(),
                response = response.trim(),
                source = EvidenceSource.SELF_REFLECTION,
                unfamiliar = task.unfamiliar,
                independentProduction = true,
                context = task.context,
                taskVersion = task.taskVersion,
                rubricVersion = task.rubricVersion
            )
        )
    }

    private fun save(attempt: Attempt, onSaved: () -> Unit = {}) {
        _saving.value = true
        _error.value = null
        _notice.value = null
        viewModelScope.launch {
            try {
                repository.record(attempt)
                onSaved()
                _notice.value = if (attempt.source == EvidenceSource.AUTOMATIC_QUIZ) {
                    "Quizantwort gespeichert. Das Ergebnis betrifft nur diese Verständnisfrage, nicht die Gesamtkompetenz."
                } else {
                    "Als Selbstreflexion gespeichert – keine automatische Bewertung und kein Kompetenznachweis."
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                _error.value = "Speichern fehlgeschlagen. Die Eingabe bleibt erhalten; bitte erneut versuchen."
            } finally {
                _saving.value = false
            }
        }
    }
}
