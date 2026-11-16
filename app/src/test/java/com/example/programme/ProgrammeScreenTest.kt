package com.example.programme

import android.content.Context

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextReplacement
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.test.core.app.ApplicationProvider
import com.example.MainApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], application = MainApplication::class)
class ProgrammeScreenTest {
    @get:Rule
    val compose = createComposeRule()

    private lateinit var application: MainApplication
    private lateinit var viewModel: ProgrammeViewModel
    private val stores = mutableListOf<ViewModelStore>()

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        application = ApplicationProvider.getApplicationContext()
        application.getSharedPreferences("programme_dashboard", Context.MODE_PRIVATE)
            .edit().clear().commit()
        application.practiceDraftRepository.clearAll()
        runBlocking(Dispatchers.IO) { application.database.clearAllTables() }
        viewModel = newViewModel()
    }

    @After
    fun tearDown() {
        compose.runOnIdle { stores.forEach { it.clear() } }
        Dispatchers.resetMain()
    }

    @Test
    fun firstUseShowsOneAdaptiveLessonAndKeepsTheCourseMapSecondary() {
        showDashboard()
        compose.onNodeWithText("Verstanden").performClick()

        compose.onNodeWithTag("learn_area").assertIsDisplayed()
        compose.onNodeWithTag("lesson_roadmap_recommendation").assertIsDisplayed()
        compose.onNodeWithTag("lesson_roadmap_continue").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Eine Lektion nach der anderen.").assertIsDisplayed()
        compose.runOnIdle {
            assertTrue(viewModel.attempts.value.isEmpty())
            assertFalse(viewModel.loading.value)
            assertTrue(viewModel.historyAvailable.value)
        }
    }

    @Test
    fun firstUseCanOpenTheRecommendedLessonDirectly() {
        showDashboard()

        compose.onNodeWithText("Verstanden").performClick()
        compose.onNodeWithTag("lesson_roadmap_continue").performScrollTo().performClick()

        compose.onNodeWithText("Lernwerkstatt").assertIsDisplayed()
        compose.onNodeWithText("01 / Abruf vor dem Nachlesen").assertIsDisplayed()
        compose.onNodeWithText("02 / Lesen").assertDoesNotExist()
        compose.onNodeWithTag("lesson_expand_all_content").performClick()
        scrollTo("02 / Lesen")
        compose.onNodeWithText("02 / Lesen").assertIsDisplayed()
        scrollTo("Das lernst du")
        compose.onNodeWithText("Das lernst du").assertIsDisplayed()
    }

    @Test
    fun guidedLessonRequiresPersistedPracticeBeforeAdvancing() {
        showDashboard()
        compose.onNodeWithText("Verstanden").performClick()
        openRecommendedLesson()

        compose.onNodeWithTag("lesson_guided_flow").assertIsDisplayed()
        compose.onNodeWithTag("lesson_step_visual_0").assertIsDisplayed()
        compose.onNodeWithText("02 / Lesen").assertDoesNotExist()
        val unitId = CurriculumCatalogue.coreUnits.first().id
        compose.onNodeWithTag("save_practice_${unitId}_retrieval").assertIsNotEnabled()
        compose.onNodeWithText("Deine Antwort / ehrliche Selbstreflexion")
            .performTextReplacement("Ich erinnere mich an den zentralen Ablauf.")

        val draft = application.practiceDraftRepository.savedDrafts().single()
        val saveButton = compose.onNodeWithTag("save_practice_${draft.unitId}_${draft.taskId}")
        saveButton.assertIsDisplayed().assertIsEnabled().performClick()
        compose.waitUntil(10_000) { viewModel.notice.value != null || viewModel.error.value != null }
        compose.runOnIdle {
            assertTrue("Save failed: ${viewModel.error.value}", viewModel.error.value == null)
            assertTrue(viewModel.attempts.value.any { it.unitId == draft.unitId && it.taskId == draft.taskId })
        }

        compose.onNodeWithTag("lesson_next").assertIsEnabled().performClick()
        compose.onNodeWithText("02 / Lesen").assertIsDisplayed()
        compose.onNodeWithTag("lesson_step_visual_1").assertIsDisplayed()
        compose.onNodeWithText("01 / Abruf vor dem Nachlesen").assertDoesNotExist()
    }

    @Test
    fun quizOptionsExposeRadioSelectionSemantics() {
        showDashboard()
        compose.onNodeWithText("Verstanden").performClick()
        openRecommendedLesson()
        compose.onNodeWithTag("lesson_expand_all_content").performClick()

        scrollTo("04 / Objektiver Verständnischeck")
        val firstOption = compose.onNodeWithTag("quiz_option_0")

        firstOption.performClick()

        firstOption.assertIsSelected()
    }

    @Test
    fun unfinishedLessonResponseAutosavesAcrossNavigationWithoutCreatingEvidence() {
        showDashboard()

        compose.onNodeWithText("Verstanden").performClick()
        openRecommendedLesson()
        compose.onNodeWithText("Deine Antwort / ehrliche Selbstreflexion").assertIsDisplayed()
        compose.onNodeWithText("Deine Antwort / ehrliche Selbstreflexion")
            .performTextReplacement("Unfertiger Entwurf bleibt erhalten")

        val savedDraft = application.practiceDraftRepository.savedDrafts().single()
        assertEquals("Unfertiger Entwurf bleibt erhalten", savedDraft.response)
        compose.runOnIdle { assertTrue(viewModel.attempts.value.isEmpty()) }

        compose.onNodeWithText("Zurück").performClick()
        openRecommendedLesson()
        compose.onNodeWithText("Deine Antwort / ehrliche Selbstreflexion").assertIsDisplayed()
        compose.onNodeWithText("Unfertiger Entwurf bleibt erhalten").assertIsDisplayed()
        compose.runOnIdle { assertTrue(viewModel.attempts.value.isEmpty()) }

        val saveButton = compose.onNodeWithTag("save_practice_${savedDraft.unitId}_${savedDraft.taskId}")
        saveButton.assertIsEnabled().performClick()
        compose.waitUntil(timeoutMillis = 10_000) {
            viewModel.notice.value != null || viewModel.error.value != null
        }
        compose.runOnIdle {
            assertTrue("Save failed: ${viewModel.error.value}", viewModel.error.value == null)
            assertTrue(viewModel.attempts.value.any { it.response == "Unfertiger Entwurf bleibt erhalten" })
            assertTrue(application.practiceDraftRepository.savedDrafts().isEmpty())
        }
    }

    @Test
    fun localHumanReviewEntryIsClearlyUnverifiedAndDoesNotRewriteAttemptEvidence() {
        val unit = CurriculumCatalogue.coreUnits.first { it.id == "s1-explain" }
        compose.waitUntil(10_000) { viewModel.historyAvailable.value }
        viewModel.recordPractice(
            unitId = unit.id,
            taskId = "writing",
            response = "Ich erläutere die Entscheidung und nenne einen nächsten Schritt.",
            context = ContextTrack.GENERAL
        )
        compose.waitUntil(10_000) {
            !viewModel.saving.value && viewModel.attempts.value.any { it.unitId == unit.id }
        }
        compose.waitUntil(10_000) {
            viewModel.attemptsByUnit.value[unit.id]?.any { it.unitId == unit.id } == true
        }
        val sourceAttempt = viewModel.attempts.value.first { it.unitId == unit.id }

        // The full local-review workflow is covered in HumanReviewRepositoryTest.
        // Home navigation must not silently rewrite an attempt into human evidence.
        assertEquals(EvidenceSource.SELF_REFLECTION, sourceAttempt.source)
        assertFalse(viewModel.attempts.value.first { it.id == sourceAttempt.id }.source == EvidenceSource.HUMAN_REVIEW)
    }

    @Test
    fun deleteAllRemovesTemporaryExportsWithoutDeletingUnrelatedCacheFiles() {
        val directory = File(application.cacheDir, "exports").apply { mkdirs() }
        val exportFile = File(directory, "c1-deutsch-export-test.json").apply { writeText("{}") }
        val unrelatedFile = File(directory, "keep.txt").apply { writeText("keep") }
        var completed = false

        try {
            viewModel.deleteAllLocalData { completed = true }
            compose.waitUntil(10_000) { completed || viewModel.error.value != null }

            assertTrue("Delete-all failed: ${viewModel.error.value}", completed)
            assertFalse(exportFile.exists())
            assertTrue(unrelatedFile.exists())
        } finally {
            exportFile.delete()
            unrelatedFile.delete()
        }
    }

    @Test
    fun personalizationChangesGreetingAndPersistsThemeAndNameAcrossViewModels() {
        showDashboard()
        compose.onNodeWithText("Verstanden").performClick()

        compose.onNodeWithTag("learner_nav_settings").performClick()
        compose.onNodeWithText("Einstellungen öffnen").performClick()
        compose.onNodeWithText("Dein Cockpit").assertIsDisplayed()
        compose.onNodeWithText("Dein Name (optional)").performTextReplacement("Mira")
        compose.onNodeWithText("Pitch green").performScrollTo().performClick()
        compose.waitUntil(5_000) { viewModel.preferences.value.theme == ProgrammeTheme.PITCH }
        compose.onNodeWithText("Pitch green").assertIsSelected()
        compose.onNodeWithText("Fertig").performClick()

        compose.onNodeWithText("Dein Cockpit").assertDoesNotExist()
        compose.onNodeWithText("Einstellungen öffnen").assertIsDisplayed()
        val restored = newViewModel()
        compose.runOnIdle {
            assertEquals("Mira", viewModel.preferences.value.learnerName)
            assertEquals(ProgrammeTheme.PITCH, viewModel.preferences.value.theme)
            assertEquals(viewModel.preferences.value, restored.preferences.value)
        }
        compose.onNodeWithTag("learner_nav_settings").performClick()
        compose.onNodeWithText("Einstellungen öffnen").performClick()
        compose.onNodeWithText("Pitch green").assertIsSelected()
        compose.onNodeWithText("Mira").assertIsDisplayed()
    }

    @Test
    fun englishPreferencePersistsWithoutAddingNavigationChrome() {
        showDashboard()
        compose.onNodeWithText("Verstanden").performClick()
        viewModel.updateLanguage(ProgrammeLanguage.EN)
        compose.onNodeWithTag("learn_area").assertIsDisplayed()
        compose.onNodeWithText("One lesson at a time.").assertIsDisplayed()
        compose.onNodeWithTag("learner_nav_progress").performClick()
        compose.onNodeWithText("Skills and mistakes").assertIsDisplayed()
        compose.runOnIdle { assertEquals(ProgrammeLanguage.EN, viewModel.preferences.value.language) }
    }

    @Test
    fun profileKeepsSettingsOnboardingAndLegacyLearningViewsReachable() {
        var practiceToolsOpened = false
        var intelligenceProfileOpened = false
        showDashboard(
            onLegacyPractice = { practiceToolsOpened = true },
            onOpenIntelligenceProfile = { intelligenceProfileOpened = true }
        )
        compose.onNodeWithText("Verstanden").performClick()
        compose.onNodeWithTag("learner_nav_settings").performClick()

        compose.onNodeWithText("Einstellungen öffnen").assertIsDisplayed()
        compose.onNodeWithText("Weitere Übungstools").assertDoesNotExist()
        compose.onNodeWithText("Übungsprofil öffnen").assertDoesNotExist()
        compose.onNodeWithTag("profile_advanced_toggle").performScrollTo().performClick()
        compose.onNodeWithText("Audio und Hörquellen").assertDoesNotExist()
        scrollTo("Weitere Übungstools")
        compose.onNodeWithText("Weitere Übungstools").performClick()
        scrollTo("Übungsprofil öffnen")
        compose.onNodeWithText("Übungsprofil öffnen").performClick()
        scrollTo("Lern- und Evidenzgrenzen ansehen")
        compose.onNodeWithText("Lern- und Evidenzgrenzen ansehen").assertIsDisplayed()
        compose.runOnIdle {
            assertTrue(practiceToolsOpened)
            assertTrue(intelligenceProfileOpened)
        }
    }

    @Test
    fun lessonsPrioritizeAdaptiveRecommendationAndHideSearchUntilRequested() {
        showDashboard()
        compose.onNodeWithText("Verstanden").performClick()

        compose.onNodeWithTag("lesson_roadmap_recommendation").assertIsDisplayed()
        scrollTo("Kurskarte")
        compose.onNodeWithText("Kurskarte").assertIsDisplayed()
        compose.onNodeWithTag("learn_course_search").assertDoesNotExist()

        val unit = CurriculumCatalogue.coreUnits.first()
        scrollTo("Lektionen suchen")
        compose.onNodeWithText("Lektionen suchen").performClick()
        compose.onNodeWithTag("learn_course_search").assertIsDisplayed()
        compose.onNodeWithTag("learn_course_search").performTextReplacement(unit.title)
        compose.onNodeWithTag("course_roadmap_unit_${unit.id}").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun progressShowsAutomaticPracticeStarsAndMistakeLogBeforeAdvancedDetails() {
        showDashboard()
        compose.onNodeWithText("Verstanden").performClick()
        compose.onNodeWithTag("learner_nav_progress").performClick()

        compose.onNodeWithText("Übungssterne je Fertigkeit").assertIsDisplayed()
        compose.onNodeWithTag("skill_rating_listening").performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("skill_rating_speaking").performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("skill_rating_writing").performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("skill_rating_interaction").performScrollTo().assertIsDisplayed()
        scrollTo("Fehlerprotokoll")
        compose.onNodeWithText("Fehlerprotokoll").assertIsDisplayed()
        compose.onNodeWithTag("mistake_log_empty").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Evidenzgrenzen").assertDoesNotExist()
        compose.onNodeWithTag("progress_details_toggle").performScrollTo().performClick()
        scrollTo("Kursfortschritt")
        compose.onNodeWithText("Kursfortschritt").assertIsDisplayed()
        scrollTo(
            "26 Kernlektionen und 2 optionale Orientierungseinheiten. Ein Checkpoint ist Übungsfortschritt, keine C1- oder Kompetenznote."
        )
        compose.onNodeWithText(
            "26 Kernlektionen und 2 optionale Orientierungseinheiten. Ein Checkpoint ist Übungsfortschritt, keine C1- oder Kompetenznote."
        ).assertIsDisplayed()
        scrollTo("Übungsbalance")
        compose.onNodeWithText("Übungsbalance").assertIsDisplayed()
        scrollTo("Aktuelle Fokusbereiche")
        compose.onNodeWithText("Aktuelle Fokusbereiche").assertIsDisplayed()
        scrollTo("Wiederholungsplan")
        compose.onNodeWithText("Wiederholungsplan").assertIsDisplayed()
        scrollTo("Evidenzgrenzen")
        compose.onNodeWithText("Evidenzgrenzen").assertIsDisplayed()
    }

    @Test
    fun practiceUpdatesSkillStarsAndAddsDetectedWritingSignalToMistakeLog() {
        showDashboard()
        compose.onNodeWithText("Verstanden").performClick()
        compose.onNodeWithTag("learner_nav_progress").performClick()
        compose.onNodeWithTag("skill_rating_writing").assertTextEquals("0 / 5")

        val unit = CurriculumCatalogue.coreUnits.first { it.id == "s1-explain" }
        viewModel.recordPractice(
            unitId = unit.id,
            taskId = "writing",
            response = "Ich denke dass Sie kommen.",
            context = ContextTrack.GENERAL
        )
        compose.waitUntil(10_000) {
            !viewModel.saving.value && viewModel.attempts.value.any { it.unitId == unit.id }
        }

        compose.onNodeWithTag("skill_rating_writing").assertTextEquals("1 / 5")
        scrollTo("Fehlerprotokoll")
        compose.onNodeWithTag("mistake_log_entry").assertIsDisplayed()
        compose.onNodeWithText(
            "Ein dass-Nebensatz wird im Deutschen durch ein Komma vom Hauptsatz getrennt."
        ).assertIsDisplayed()
    }

    @Test
    fun lessonShowsAnAnimatedLearningPathAndInteractiveLanguagePatterns() {
        showDashboard()
        compose.onNodeWithText("Verstanden").performClick()
        openRecommendedLesson()

        compose.onNodeWithTag("lesson_expand_all_content").performClick()
        scrollTo("DEIN LERNWEG")
        compose.onNodeWithText("DEIN LERNWEG").assertIsDisplayed()
        compose.onNodeWithTag("lesson_path_step_0").assertIsDisplayed()
        compose.onNodeWithTag("lesson_path_step_3").performClick()
        compose.onNodeWithTag("lesson_path_step_3").assertIsSelected()
        scrollTo("WORAN DU ARBEITEST")
        compose.onNodeWithText("WORAN DU ARBEITEST").assertIsDisplayed()
        compose.onNodeWithTag("lesson_visual_illustration").performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("lesson_concept_step_2").performScrollTo().performClick().assertIsSelected()
        compose.onNodeWithTag("lesson_concept_step_0").performScrollTo().performClick().assertIsSelected()
        scrollTo("LESEN · SINNABSCHNITTE")
        compose.onNodeWithText("LESEN · SINNABSCHNITTE").assertIsDisplayed()
        scrollTo("03 / Hören · synthetische Stimme")
        compose.onNodeWithText("Normal anhören").assertIsDisplayed()
        compose.onNodeWithText("Nochmal · langsamer").assertIsDisplayed()
        compose.onNodeWithTag("lesson_book_audio_${CurriculumCatalogue.coreUnits.first().id}")
            .performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Folge beim Anbieter öffnen").assertDoesNotExist()
        compose.onNodeWithText("Hörlabor öffnen · eigene Buchaufnahmen").assertDoesNotExist()
        scrollTo("Transkript nach dem ersten Versuch anzeigen")
        compose.onNodeWithText("Transkript nach dem ersten Versuch anzeigen").performClick()
        compose.onNodeWithText("DIALOGVERLAUF").assertIsDisplayed()
        scrollTo("Sprachmittel & Grammatik")
        compose.onNodeWithText(
            "Wähle ein Sprachmittel, um Form und Funktion zu verbinden."
        ).assertIsDisplayed()
        scrollTo("Grammatik im Detail anzeigen")
        compose.onNodeWithTag("lesson_grammar_toggle").performScrollTo().performClick()
        compose.waitForIdle()
        compose.onNodeWithText("Grammatik ausblenden").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun visualDashboardKeepsTheFullCourseAvailableThroughTheFocusedLessonPath() {
        showDashboard()
        compose.onNodeWithText("Verstanden").performClick()
        openRecommendedLesson()
        compose.onNodeWithText("Lernwerkstatt").assertIsDisplayed()
    }

    @Test
    fun dailyPlanPreferencesPersistWithoutShowingAPlanningMenuOnHome() {
        showDashboard()

        listOf(20, 60, 90).forEach { minutes ->
            viewModel.updateDailyMinutes(minutes)
            compose.runOnIdle { assertEquals(minutes, viewModel.preferences.value.dailyMinutes) }
            val restored = newViewModel()
            compose.runOnIdle { assertEquals(minutes, restored.preferences.value.dailyMinutes) }
        }
    }

    @Test
    fun startUnitOpensWorkshopAndBackReturnsToDashboardWithoutCreatingEvidence() {
        showDashboard()

        compose.onNodeWithText("Verstanden").performClick()
        openRecommendedLesson()
        compose.onNodeWithText("Lernwerkstatt").assertIsDisplayed()
        compose.onNodeWithText("Dein Programm").assertDoesNotExist()
        compose.onNodeWithTag("lesson_guided_flow").assertIsDisplayed()
        compose.onNodeWithText("02 / Lesen").assertDoesNotExist()
        compose.onNodeWithText("Zurück").assertIsDisplayed().performClick()

        compose.onNodeWithTag("learn_area").assertIsDisplayed()
        compose.onNodeWithText("Lernwerkstatt").assertDoesNotExist()
        compose.onNodeWithText("Eine Lektion nach der anderen.").assertIsDisplayed()
        compose.runOnIdle { assertTrue(viewModel.attempts.value.isEmpty()) }
    }

    @Test
    fun plansTotalRequestedMinutesWithEqualReceptiveAndProductivePractice() {
        val receptive = setOf("reading", "listening", "quiz")
        val productive = setOf("retrieval", "controlled", "speaking", "interaction", "writing")
        listOf(20, 60, 90).forEach { minutes ->
            val plan = viewModel.plan(minutes)
            assertEquals("Every task must occur exactly once", receptive + productive, plan.map { it.taskId }.toSet())
            assertEquals(8, plan.size)
            assertTrue(plan.all { it.minutes > 0 && it.title.isNotBlank() })
            assertEquals("Total for $minutes minute plan", minutes, plan.sumOf { it.minutes })
            assertEquals("Receptive half for $minutes", minutes / 2,
                plan.filter { it.taskId in receptive }.sumOf { it.minutes })
            assertEquals("Productive half for $minutes", minutes / 2,
                plan.filter { it.taskId in productive }.sumOf { it.minutes })
        }
    }

    @Test
    fun preferencesPersistLocallyAndRejectUnsupportedDailyBudgets() {
        compose.runOnIdle {
            assertEquals(ProgrammePreferences(), viewModel.preferences.value)
            viewModel.updateName("N".repeat(75))
            viewModel.updateTheme(ProgrammeTheme.SILVER)
            viewModel.updateLanguage(ProgrammeLanguage.EN)
            viewModel.updateCompact(true)
            viewModel.updateDailyMinutes(60)
            listOf(-1, 0, 21, 120).forEach { viewModel.updateDailyMinutes(it) }
        }
        val expected = ProgrammePreferences(
            theme = ProgrammeTheme.SILVER,
            language = ProgrammeLanguage.EN,
            learnerName = "N".repeat(60),
            compact = true,
            dailyMinutes = 60
        )
        val restored = newViewModel()
        compose.runOnIdle {
            assertEquals(expected, viewModel.preferences.value)
            assertEquals(expected, restored.preferences.value)
        }
    }

    private fun newViewModel(): ProgrammeViewModel = compose.runOnIdle {
        val store = ViewModelStore().also { stores.add(it) }
        ViewModelProvider(store, ViewModelProvider.AndroidViewModelFactory(application))[
            ProgrammeViewModel::class.java
        ]
    }

    private fun showDashboard(
        onLegacyPractice: () -> Unit = {},
        onOpenIntelligenceProfile: () -> Unit = {}
    ) {
        compose.setContent {
            ProgrammeScreen(
                viewModel = viewModel,
                onLegacyPractice = onLegacyPractice,
                onOpenIntelligenceProfile = onOpenIntelligenceProfile
            )
        }

        compose.waitUntil(timeoutMillis = 10_000) { !viewModel.loading.value }
        compose.runOnIdle { assertTrue("History must load before asserting empty progress", viewModel.historyAvailable.value) }
        compose.waitForIdle()
    }

    private fun openRecommendedLesson() {
        compose.onNodeWithTag("lesson_roadmap_continue").performScrollTo().performClick()
    }

    private fun scrollTo(text: String) {
        compose.onNode(hasScrollAction()).performScrollToNode(hasText(text))
    }
}
