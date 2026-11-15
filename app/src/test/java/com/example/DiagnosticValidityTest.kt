package com.example

import android.app.Application
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.C1AppDatabase
import com.example.data.local.entity.BaselineProfileEntity
import com.example.data.model.SkillCategory
import com.example.data.repository.C1LearningRepository
import com.example.data.repository.DiagnosticRepository
import com.example.ui.diagnostic.DiagnosticEngine
import com.example.ui.diagnostic.DiagnosticUiState
import com.example.ui.diagnostic.DiagnosticUserAnswer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], application = Application::class)
class DiagnosticValidityTest {
    private lateinit var database: C1AppDatabase
    private lateinit var engine: DiagnosticEngine
    private lateinit var learningRepository: C1LearningRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        val application = ApplicationProvider.getApplicationContext<Application>()
        database = Room.inMemoryDatabaseBuilder(application, C1AppDatabase::class.java)
            .allowMainThreadQueries().build()
        learningRepository = C1LearningRepository()
        engine = DiagnosticEngine(application, database, learningRepository)
    }

    @After
    fun tearDown() {
        database.close()
        Dispatchers.resetMain()
    }

    private fun completed() = engine.uiState.value as DiagnosticUiState.Completed

    private fun correctAnswers(latency: Int = 0) = engine.currentTasks.mapIndexed { index, task ->
        index to DiagnosticUserAnswer(
            selectedOptionIndex = task.correctOptionIndex,
            recordedAudioLatencySeconds = latency,
            recordedDurationSeconds = 60,
            // Deliberately inconsistent metadata: only the actual selected option counts.
            isEvaluatedCorrect = false,
            earnedScore = 85
        )
    }.toMap()

    private fun priorBaseline() = BaselineProfileEntity(
        listeningScore = 81,
        speakingScore = 74,
        interactionScore = 73,
        readingScore = 82,
        writingScore = 75,
        grammarScore = 76,
        vocabularyScore = 77,
        mediationScore = 78,
        overallC1Score = 77,
        functionalReadinessPct = 76,
        primaryLimitingFactor = "Previous measured evidence",
        recommendedStartingEngine = "Previous programme",
        assessedAtTimestamp = 123L
    )

    @Test
    fun `empty answers leave every dimension unassessed and zero`() = runTest {
        engine.completeAssessment(emptyMap()).join()
        val state = completed()
        assertTrue(state.assessedSkills.isEmpty())
        assertTrue(state.nineDimensionalProfile.values.all { it.score == 0 && it.masteredAreas.isEmpty() })
        assertTrue(state.nineDimensionalProfile.values.all { it.criticalWeakness.contains("Nicht beurteilt") })
        assertEquals(0, state.answeredTaskCount)
        assertEquals(0, state.profile.overallC1Score)
        assertEquals(0, state.profile.functionalReadinessPct)
        assertEquals(0f, state.confidenceScore, 0f)
        assertTrue(state.isProvisional)
        assertFalse(state.profile.isCompleted)
        assertNull(database.baselineProfileDao().getBaselineProfile())
    }

    @Test
    fun `all correct MCQs cannot measure productive skills or CEFR readiness`() = runTest {
        engine.completeAssessment(correctAnswers()).join()
        val state = completed()
        assertEquals(8, state.answeredTaskCount)
        assertEquals(9, state.nineDimensionalProfile.size)
        assertEquals(setOf(
            SkillCategory.HOERVERSTEHEN, SkillCategory.LESEVERSTEHEN,
            SkillCategory.GRAMMATIK, SkillCategory.WORTSCHATZ
        ), state.assessedSkills)
        state.nineDimensionalProfile.forEach { (category, score) ->
            assertEquals(if (category in state.assessedSkills) 100 else 0, score.score)
            assertTrue(score.masteredAreas.isEmpty())
        }
        assertEquals(0, state.overallScore)
        assertEquals(0, state.profile.functionalReadinessPct)
        assertEquals(0f, state.confidenceScore, 0f)
        assertTrue(state.isProvisional)
        assertFalse(state.profile.isCompleted)
        assertTrue(state.personalizedRoadmap.isEmpty())
        assertTrue(state.summaryDe.contains("Keine GER-/CEFR-Einstufung"))
    }

    @Test
    fun `wrong choice earns zero even when caller supplies a high score`() = runTest {
        val grammarIndex = engine.tasks.indexOfFirst { it.skillCategory == SkillCategory.GRAMMATIK }
        val task = engine.tasks[grammarIndex]
        engine.completeAssessment(mapOf(grammarIndex to DiagnosticUserAnswer(
            selectedOptionIndex = (task.correctOptionIndex + 1) % task.multipleChoiceOptions.size,
            isEvaluatedCorrect = true,
            earnedScore = 100
        ))).join()
        val state = completed()
        assertEquals(0, state.grammarScore)
        assertEquals(setOf(SkillCategory.GRAMMATIK), state.assessedSkills)
        assertTrue(state.vocabTopicScores.isEmpty())
        assertEquals(1, state.answeredTaskCount)
    }

    @Test
    fun `invalid indices and freeform text do not count as assessed answers`() = runTest {
        engine.completeAssessment(mapOf(
            -1 to DiagnosticUserAnswer(selectedOptionIndex = 0, earnedScore = 100),
            99 to DiagnosticUserAnswer(selectedOptionIndex = 0, earnedScore = 100),
            0 to DiagnosticUserAnswer(selectedOptionIndex = 99, earnedScore = 100),
            5 to DiagnosticUserAnswer(freeformText = "Not rubric assessed", earnedScore = 100)
        )).join()
        assertEquals(0, completed().answeredTaskCount)
        assertTrue(completed().assessedSkills.isEmpty())
    }

    @Test
    fun `latency does not affect any score and pronunciation remains unassessed`() = runTest {
        engine.completeAssessment(correctAnswers(latency = 1)).join()
        val fast = completed().nineDimensionalProfile
        engine.completeAssessment(correctAnswers(latency = 90)).join()
        assertEquals(fast, completed().nineDimensionalProfile)
        assertEquals(0, fast.getValue(SkillCategory.AUSSPRACHE_PROSODIE).score)
        assertFalse(completed().assessedSkills.contains(SkillCategory.AUSSPRACHE_PROSODIE))
    }

    @Test
    fun `recording compatibility method never starts a fake recording timer`() = runTest {
        engine.startFullAssessment()
        engine.nextTask()
        engine.toggleVoiceRecording()
        advanceTimeBy(5_000)
        val state = engine.uiState.value as DiagnosticUiState.InProgress
        assertFalse(state.isRecordingVoice)
        assertEquals(0, state.recordingDurationSeconds)
        assertEquals(0, state.responseLatencySeconds)
        engine.recordAnswer(1, engine.tasks[1].correctOptionIndex)
        val answer = (engine.uiState.value as DiagnosticUiState.InProgress).answers.getValue(1)
        assertEquals(100, answer.earnedScore)
        assertEquals(0, answer.recordedAudioLatencySeconds)
    }

    @Test
    fun `grammar vocabulary screening leaves all other dimensions zero`() = runTest {
        engine.startGrammarVocabAssessment()
        engine.completeAssessment(correctAnswers()).join()
        val state = completed()
        assertEquals(setOf(SkillCategory.GRAMMATIK, SkillCategory.WORTSCHATZ), state.assessedSkills)
        assertEquals(100, state.grammarScore)
        assertEquals(100, state.vocabularyScore)
        state.nineDimensionalProfile.filterKeys { it !in state.assessedSkills }.values.forEach {
            assertEquals(0, it.score)
        }
        assertNull(database.baselineProfileDao().getBaselineProfile())
    }

    @Test
    fun `partial grammar screening does not fill missing topics or vocabulary`() = runTest {
        engine.startGrammarVocabAssessment()
        engine.completeAssessment(mapOf(0 to DiagnosticUserAnswer(selectedOptionIndex = 0))).join()
        val state = completed()
        assertEquals(20, state.grammarScore) // One correct choice out of the five offered grammar items.
        assertEquals(0, state.vocabularyScore)
        assertEquals(setOf(SkillCategory.GRAMMATIK), state.assessedSkills)
        assertEquals(1, state.grammarTopicScores.size)
        assertTrue(state.vocabTopicScores.isEmpty())
    }

    @Test
    fun `both screening modes preserve baseline and live learning state`() = runTest {
        val prior = priorBaseline()
        database.baselineProfileDao().insertOrUpdateProfile(prior)
        val skills = learningRepository.skillScores.value.toMap()
        val errors = learningRepository.errorDatabase.value.toList()
        val vocabulary = learningRepository.vocabularyList.value.toList()
        engine.startGrammarVocabAssessment()
        engine.completeAssessment(correctAnswers()).join()
        assertEquals(prior, database.baselineProfileDao().getBaselineProfile())
        engine.startFullAssessment()
        engine.completeAssessment(correctAnswers()).join()
        assertEquals(prior, database.baselineProfileDao().getBaselineProfile())
        assertEquals(skills, learningRepository.skillScores.value)
        assertEquals(errors, learningRepository.errorDatabase.value)
        assertEquals(vocabulary, learningRepository.vocabularyList.value)
    }

    @Test
    fun `repository rejects grammar only and practice baselines without breaking legacy writes`() = runTest {
        val repository = DiagnosticRepository(database.baselineProfileDao())
        val prior = priorBaseline()
        repository.saveBaselineProfile(prior)
        repository.saveBaselineProfile(prior.copy(grammarScore = 100), grammarOnly = true)
        assertEquals(prior, repository.getProfileDirect())
        repository.saveBaselineProfile(prior.copy(readingScore = 100), practiceScreening = true)
        assertEquals(prior, repository.getProfileDirect())
    }

    @Test
    fun `legacy profile has no inferred pronunciation or invented mastery`() {
        val scores = engine.buildNineDimensionalProfile(priorBaseline(), pronunciationOverride = 99)
        assertTrue(scores.values.all { it.score == 0 && it.masteredAreas.isEmpty() })
    }

    @Test
    fun `genitive item gives a cause not absence of defects`() {
        val task = engine.grammarAndVocabTasks.single { it.id == "gv_task_5" }
        assertEquals("aufgrund", task.multipleChoiceOptions[task.correctOptionIndex])
        assertTrue(task.analysisRationaleDe.contains("Fehlen"))
    }

    @Test
    fun `screening copy does not present synthetic regional text or model choices as performance evidence`() {
        val listening = engine.tasks.single { it.id == "task_listening" }
        val speaking = engine.tasks.single { it.id == "task_speaking" }
        val interaction = engine.tasks.single { it.id == "task_interaction" }

        assertTrue(listening.contextDe.contains("fiktive"))
        assertTrue(listening.dialectNote.orEmpty().contains("nicht regional validiert"))
        assertTrue(listening.analysisRationaleDe.contains("misst kein allgemeines Hörniveau"))
        assertTrue(speaking.analysisRationaleDe.contains("kein eigenes Sprechen"))
        assertTrue(interaction.analysisRationaleDe.contains("keine eigene Interaktionsleistung"))
    }
}
