package com.example.programme

import android.content.Context

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
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
    fun firstUseShowsOneFocusedSessionRatherThanAnAnalyticsDashboard() {
        showDashboard()
        compose.onNodeWithText("Verstanden").performClick()

        compose.onNodeWithTag("today_screen").assertIsDisplayed()
        compose.onNodeWithText("Heute nur ein guter Schritt.").assertIsDisplayed()
        compose.onNodeWithTag("dashboard_quick_start").performScrollTo().assertIsDisplayed()
        compose.runOnIdle {
            assertTrue(viewModel.attempts.value.isEmpty())
            assertFalse(viewModel.loading.value)
            assertTrue(viewModel.historyAvailable.value)
        }
    }

    @Test
    fun firstUseCanOpenALessonDirectlyFromTheDashboardHero() {
        showDashboard()

        compose.onNodeWithText("Verstanden").performClick()
        val quickStart = compose.onNodeWithTag("dashboard_quick_start").performScrollTo()
        quickStart.assertIsDisplayed()
        quickStart.performClick()

        compose.onNodeWithText("Lernwerkstatt").assertIsDisplayed()
        scrollTo("Das lernst du")
        compose.onNodeWithText("Das lernst du").assertIsDisplayed()
    }

    @Test
    fun quizOptionsExposeRadioSelectionSemantics() {
        showDashboard()

        compose.onNodeWithText("Verstanden").performClick()
        compose.onNodeWithTag("dashboard_quick_start").performScrollTo().performClick()
        scrollTo("04 / Objektiver Verständnischeck")
        val firstOption = compose.onNodeWithTag("quiz_option_0")

        firstOption.performClick()

        firstOption.assertIsSelected()
    }

    @Test
    fun unfinishedLessonResponseAutosavesAcrossNavigationWithoutCreatingEvidence() {
        showDashboard()

        compose.onNodeWithText("Verstanden").performClick()
        compose.onNodeWithTag("dashboard_quick_start").performScrollTo().performClick()
        scrollTo("Deine Antwort / ehrliche Selbstreflexion")
        compose.onNodeWithText("Deine Antwort / ehrliche Selbstreflexion")
            .performTextReplacement("Unfertiger Entwurf bleibt erhalten")

        val savedDraft = application.practiceDraftRepository.savedDrafts().single()
        assertEquals("Unfertiger Entwurf bleibt erhalten", savedDraft.response)
        compose.runOnIdle { assertTrue(viewModel.attempts.value.isEmpty()) }

        compose.onNodeWithText("Zurück").performClick()
        compose.onNodeWithTag("dashboard_quick_start").performScrollTo().performClick()
        scrollTo("Deine Antwort / ehrliche Selbstreflexion")
        compose.onNodeWithText("Unfertiger Entwurf bleibt erhalten").assertIsDisplayed()
        compose.runOnIdle { assertTrue(viewModel.attempts.value.isEmpty()) }

        compose.onNodeWithTag("save_practice_${savedDraft.unitId}_${savedDraft.taskId}").performClick()
        compose.waitUntil(timeoutMillis = 10_000) { !viewModel.saving.value }
        compose.runOnIdle {
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

        compose.onNodeWithText("Anpassen · Einstellungen").performClick()
        compose.onNodeWithText("Dein Cockpit").assertIsDisplayed()
        compose.onNodeWithText("Dein Name (optional)").performTextReplacement("Mira")
        compose.onNodeWithText("Pitch green").performScrollTo().performClick()
        compose.waitUntil(5_000) { viewModel.preferences.value.theme == ProgrammeTheme.PITCH }
        compose.onNodeWithText("Pitch green").assertIsSelected()
        compose.onNodeWithText("Fertig").performClick()

        compose.onNodeWithText("Dein Cockpit").assertDoesNotExist()
        compose.onNodeWithText("Heute genügt ein guter Schritt, Mira.").assertIsDisplayed()
        val restored = newViewModel()
        compose.runOnIdle {
            assertEquals("Mira", viewModel.preferences.value.learnerName)
            assertEquals(ProgrammeTheme.PITCH, viewModel.preferences.value.theme)
            assertEquals(viewModel.preferences.value, restored.preferences.value)
        }
        compose.onNodeWithText("Anpassen · Einstellungen").performClick()
        compose.onNodeWithText("Pitch green").assertIsSelected()
        compose.onNodeWithText("Mira").assertIsDisplayed()
    }

    @Test
    fun englishPreferencePersistsWithoutAddingNavigationChrome() {
        showDashboard()
        compose.onNodeWithText("Verstanden").performClick()
        viewModel.updateLanguage(ProgrammeLanguage.EN)
        compose.onNodeWithTag("today_screen").assertIsDisplayed()
        compose.onNodeWithText("Take one good step today.").assertIsDisplayed()
        compose.onNodeWithTag("learner_nav_learn").performClick()
        compose.onNodeWithText("Choose a practice area.").assertIsDisplayed()
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
        compose.onNodeWithTag("learner_nav_profile").performClick()

        compose.onNodeWithText("Einstellungen öffnen").assertIsDisplayed()
        compose.onNodeWithText("Weitere Übungstools").assertDoesNotExist()
        compose.onNodeWithText("Übungsprofil öffnen").assertDoesNotExist()
        compose.onNodeWithTag("profile_advanced_toggle").performScrollTo().performClick()
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
    fun learnShowsFourAreasAndOnlyBrowsesCourseWhenRequested() {
        showDashboard()
        compose.onNodeWithText("Verstanden").performClick()
        compose.onNodeWithTag("learner_nav_learn").performClick()

        compose.onNodeWithText("Hören").assertIsDisplayed()
        compose.onNodeWithText("Sprechen").assertIsDisplayed()
        compose.onNodeWithText("Schreiben / Mediation").assertIsDisplayed()
        compose.onNodeWithText("Interaktion").assertIsDisplayed()
        compose.onNodeWithTag("learn_course_search").assertDoesNotExist()

        val unit = CurriculumCatalogue.coreUnits.first()
        scrollTo("Durchsuchen")
        compose.onNodeWithText("Durchsuchen").performClick()
        scrollTo("Einheiten suchen")
        compose.onNodeWithText("Einheiten suchen").performClick()
        compose.onNodeWithTag("learn_course_search").performTextReplacement(unit.title)
        compose.onNodeWithText(unit.title).assertIsDisplayed()
        val orientation = CurriculumCatalogue.orientationUnits.first()
        compose.onNodeWithTag("learn_course_search").performTextReplacement(orientation.title)
        compose.onNodeWithText(orientation.title).assertIsDisplayed()
    }

    @Test
    fun progressSeparatesPracticeCompletionFromEvidenceBoundaries() {
        showDashboard()
        compose.onNodeWithText("Verstanden").performClick()
        compose.onNodeWithTag("learner_nav_progress").performClick()

        compose.onNodeWithText("0 / ${CurriculumCatalogue.units.size}").performScrollTo().assertIsDisplayed()
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
    fun lessonShowsAnAnimatedLearningPathAndInteractiveLanguagePatterns() {
        showDashboard()
        compose.onNodeWithText("Verstanden").performClick()
        compose.onNodeWithTag("dashboard_quick_start").performScrollTo().performClick()

        scrollTo("DEIN LERNWEG")
        compose.onNodeWithText("DEIN LERNWEG").assertIsDisplayed()
        compose.onNodeWithTag("lesson_path_step_0").assertIsDisplayed()
        compose.onNodeWithTag("lesson_path_step_3").performClick()
        compose.onNodeWithTag("lesson_path_step_3").assertIsSelected()
        scrollTo("WORAN DU ARBEITEST")
        compose.onNodeWithText("WORAN DU ARBEITEST").assertIsDisplayed()
        scrollTo("LESEN · SINNABSCHNITTE")
        compose.onNodeWithText("LESEN · SINNABSCHNITTE").assertIsDisplayed()
        scrollTo("Transkript nach dem ersten Versuch anzeigen")
        compose.onNodeWithText("Transkript nach dem ersten Versuch anzeigen").performClick()
        compose.onNodeWithText("DIALOGVERLAUF").assertIsDisplayed()
        scrollTo("Sprachmittel & Grammatik")
        compose.onNodeWithText(
            "Wähle ein Sprachmittel, um Form und Funktion zu verbinden."
        ).assertIsDisplayed()
    }

    @Test
    fun visualDashboardKeepsTheFullCourseAvailableThroughTheFocusedLessonPath() {
        showDashboard()
        compose.onNodeWithText("Verstanden").performClick()
        compose.onNodeWithTag("dashboard_quick_start").performScrollTo().performClick()
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

        compose.onNodeWithTag("dashboard_quick_start").performScrollTo().performClick()
        compose.onNodeWithText("Lernwerkstatt").assertIsDisplayed()
        compose.onNodeWithText("Dein Programm").assertDoesNotExist()
        scrollTo("Das lernst du")
        compose.onNodeWithText("Das lernst du").assertIsDisplayed()
        compose.onNodeWithText("Zurück").assertIsDisplayed().performClick()

        compose.onNodeWithTag("today_screen").assertIsDisplayed()
        compose.onNodeWithText("Lernwerkstatt").assertDoesNotExist()
        compose.onNodeWithText("Heute nur ein guter Schritt.").assertIsDisplayed()
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

    private fun scrollTo(text: String) {
        compose.onNode(hasScrollAction()).performScrollToNode(hasText(text))
    }
}
