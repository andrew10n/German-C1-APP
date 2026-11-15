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
    fun emptyDashboardShowsUnknownSkillsRatherThanFabricatedC1Progress() {
        showDashboard()

        compose.onNodeWithText("Dein Programm").assertIsDisplayed()
        compose.onNodeWithText("Mehr ausdrücken.\nJeden Tag ein Stück.").assertIsDisplayed()
        val authored = CurriculumCatalogue.coreUnits.size
        compose.onNodeWithText(
            "0 / $authored Übungs-Checkpoints abgeschlossen."
        ).assertIsDisplayed()

        scrollTo("Was die automatische Diagnose wirklich misst")
        compose.onNodeWithText("0 objektive Quizantworten · 0 Selbstreflexionen").assertIsDisplayed()
        scrollTo("Korrektheit: nicht automatisch messbar")
        compose.onNodeWithText("Korrektheit: nicht automatisch messbar").assertIsDisplayed()
        scrollTo("Natürlichkeit: nicht automatisch messbar")
        compose.onNodeWithText("Natürlichkeit: nicht automatisch messbar").assertIsDisplayed()
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
        val quickStart = compose.onNodeWithTag("dashboard_quick_start")
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
        compose.onNodeWithTag("dashboard_quick_start").performClick()
        scrollTo("04 / Objektiver Verständnischeck")
        val firstOption = compose.onNodeWithTag("quiz_option_0")

        firstOption.performClick()

        firstOption.assertIsSelected()
    }

    @Test
    fun unfinishedLessonResponseAutosavesAcrossNavigationWithoutCreatingEvidence() {
        showDashboard()

        compose.onNodeWithText("Verstanden").performClick()
        compose.onNodeWithTag("dashboard_quick_start").performClick()
        scrollTo("Deine Antwort / ehrliche Selbstreflexion")
        compose.onNodeWithText("Deine Antwort / ehrliche Selbstreflexion")
            .performTextReplacement("Unfertiger Entwurf bleibt erhalten")

        val savedDraft = application.practiceDraftRepository.savedDrafts().single()
        assertEquals("Unfertiger Entwurf bleibt erhalten", savedDraft.response)
        compose.runOnIdle { assertTrue(viewModel.attempts.value.isEmpty()) }

        compose.onNodeWithText("Zurück").performClick()
        compose.onNodeWithTag("dashboard_quick_start").performClick()
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

        showDashboard()
        compose.onNodeWithText("Verstanden").performClick()
        compose.onNodeWithTag("dashboard_quick_start").performClick()
        compose.waitForIdle()
        scrollTo("Gespeicherter Verlauf dieser Einheit")
        compose.onNodeWithText("Lokale Review-Notiz erfassen").performScrollTo().performClick()
        compose.onNodeWithTag("human_review_name").performTextReplacement("Coach")
        Dimension.values().forEach { dimension ->
            compose.onNodeWithTag("human_review_${dimension.name}_3")
                .performScrollTo()
                .performClick()
        }
        compose.onNodeWithTag("human_review_save").performClick()
        compose.waitUntil(10_000) {
            viewModel.humanReviews.value.any { it.attemptId == sourceAttempt.id }
        }

        val review = viewModel.humanReviews.value.single { it.attemptId == sourceAttempt.id }
        assertEquals(false, review.reviewer.verified)
        assertEquals("Coach", review.reviewer.displayName)
        assertEquals(Dimension.values().toSet(), review.scores.keys)
        assertTrue(review.scores.values.all { it == 3 })
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

        compose.onNodeWithText("Anpassen · Einstellungen").performClick()
        compose.onNodeWithText("Dein Cockpit").assertIsDisplayed()
        compose.onNodeWithText("Dein Name (optional)").performTextReplacement("Mira")
        compose.onNodeWithText("Pitch green").performScrollTo().performClick()
        compose.waitUntil(5_000) { viewModel.preferences.value.theme == ProgrammeTheme.PITCH }
        compose.onNodeWithText("Pitch green").assertIsSelected()
        compose.onNodeWithText("Fertig").performClick()

        compose.onNodeWithText("Dein Cockpit").assertDoesNotExist()
        compose.onNodeWithText("Hallo, Mira.\nHeute zählt dein nächster Schritt.").assertIsDisplayed()
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
    fun englishModeLocalizesAssessmentControls() {
        showDashboard()
        compose.onNodeWithText("Verstanden").performClick()
        viewModel.updateLanguage(ProgrammeLanguage.EN)
        scrollTo("Independent writing practice")

        compose.onNodeWithText("Choose a short retrieval, writing, or formulation task from your course.")
            .assertIsDisplayed()
        compose.onNodeWithText("Listening practice with your audio").performClick()
        compose.onNodeWithText("Start practice").assertIsDisplayed()
            .performClick()
        compose.onNodeWithText("1 / 1 · Audio response").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Your listening notes").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Finish").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun curriculumSearchAndBookmarksPersistAndCanFilterTheLearningPath() {
        showDashboard()
        compose.onNodeWithText("Verstanden").performClick()
        scrollTo("Deine Lernroute")

        val unit = CurriculumCatalogue.coreUnits.first()
        compose.onNodeWithTag("curriculum_search").performTextReplacement(unit.title)
        compose.onNode(hasScrollAction()).performScrollToNode(hasTestTag("bookmark_${unit.id}"))
        compose.onNodeWithTag("bookmark_${unit.id}").assertIsDisplayed().performClick()
        compose.onNodeWithTag("curriculum_bookmarks_filter").performClick()

        compose.runOnIdle {
            assertTrue(unit.id in viewModel.bookmarkedUnitIds.value)
        }
        val restored = newViewModel()
        compose.runOnIdle {
            assertTrue(unit.id in restored.bookmarkedUnitIds.value)
        }
        compose.onNodeWithTag("bookmark_${unit.id}").assertIsDisplayed()
    }

    @Test
    fun dailyPlanChoicesUpdateSelectedBudgetBalanceAndPersist() {
        showDashboard()

        listOf(20, 60, 90).forEach { minutes ->
            scrollTo("$minutes Min.")
            compose.onNodeWithText("$minutes Min.").performClick().assertIsSelected()
            val balance = "${minutes / 2} Min. rezeptiv · ${minutes / 2} Min. produktiv"
            scrollTo(balance)
            compose.onNodeWithText(balance).assertIsDisplayed()
            compose.runOnIdle { assertEquals(minutes, viewModel.preferences.value.dailyMinutes) }
            val restored = newViewModel()
            compose.runOnIdle { assertEquals(minutes, restored.preferences.value.dailyMinutes) }
        }
    }

    @Test
    fun startUnitOpensWorkshopAndBackReturnsToDashboardWithoutCreatingEvidence() {
        showDashboard()

        scrollTo("Mit Abruf starten")
        compose.onNodeWithText("Mit Abruf starten").performScrollTo().assertIsDisplayed().performClick()
        compose.onNodeWithText("Lernwerkstatt").assertIsDisplayed()
        compose.onNodeWithText("Dein Programm").assertDoesNotExist()
        scrollTo("Das lernst du")
        compose.onNodeWithText("Das lernst du").assertIsDisplayed()
        compose.onNodeWithText("Zurück").assertIsDisplayed().performClick()

        compose.onNodeWithText("Dein Programm").assertIsDisplayed()
        compose.onNodeWithText("Lernwerkstatt").assertDoesNotExist()
        scrollTo("Mehr ausdrücken.\nJeden Tag ein Stück.")
        compose.onNodeWithText("Mehr ausdrücken.\nJeden Tag ein Stück.").assertIsDisplayed()
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

    private fun showDashboard() {
        compose.setContent {
            ProgrammeScreen(viewModel = viewModel, onLegacyPractice = {
                throw AssertionError("Smoke tests must stay inside the programme")
            })
        }
        compose.waitUntil(timeoutMillis = 10_000) { !viewModel.loading.value }
        compose.runOnIdle { assertTrue("History must load before asserting empty progress", viewModel.historyAvailable.value) }
        compose.waitForIdle()
    }

    private fun scrollTo(text: String) {
        compose.onNode(hasScrollAction()).performScrollToNode(hasText(text))
    }
}
