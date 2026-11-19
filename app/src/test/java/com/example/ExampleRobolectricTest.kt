package com.example

import android.app.Application
import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.audio.PodcastFeedSpec
import com.example.audio.PodcastRssClient
import com.example.audio.WavPcmEncoder
import com.example.data.curriculum.C1CurriculumRepository
import com.example.data.local.AiGeneratedQuizEntity
import com.example.data.local.AiGeneratedSlideEntity
import com.example.data.local.AiGeneratedVocabEntity
import com.example.data.local.AppDatabase
import com.example.data.local.SrsCardStateEntity
import com.example.domain.engine.AutomaticityClassification
import com.example.domain.engine.CommanderOverrideConfig
import com.example.domain.engine.DailyBeatType
import com.example.domain.engine.DiagnosticController
import com.example.domain.engine.DirectiveActionType
import com.example.domain.engine.PsychometricMathEngine
import com.example.domain.model.C1Pillar
import com.example.domain.model.ContextTrack
import com.example.audio.GermanSpeechRecognizerController
import com.example.domain.model.ControllerStrategyMode
import com.example.domain.model.ErrorArchetype
import com.example.domain.model.PersonalizationErrorCategory11
import com.example.domain.model.SpontaneousCoachingEvaluation
import com.example.domain.model.VisualDiagramType
import com.example.ui.MainViewModel
import com.example.ui.components.BranchQuality
import com.example.ui.components.SampleWorkplaceScenarios
import com.example.ui.theme.AppVibeTheme
import com.example.ui.theme.contentColorOnAccent
import com.example.ui.theme.paletteForVibe
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    private lateinit var db: AppDatabase
    private lateinit var controller: DiagnosticController
    private lateinit var curriculum: C1CurriculumRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        curriculum = C1CurriculumRepository(context)
        controller = DiagnosticController(db.diagnosticDao(), curriculum)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun `authentic repo curriculum book bridges visual decks and situations are loaded`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("German C1", appName)

        // 7 Communicative Stages & 28 Real Repo Units
        assertEquals(7, curriculum.getStages().size)
        val repoUnits = curriculum.getAllRepoUnits()
        assertEquals(28, repoUnits.size)

        // Verify Aspekte neu C1 & Sicher! C1 Textbook Bridges + Imprint Visual Decks
        assertTrue(repoUnits.all { it.bookBridge != null })
        assertTrue(repoUnits.any { it.bookBridge?.bookReference?.contains("Aspekte neu C1") == true })
        assertTrue(repoUnits.any { it.bookBridge?.bookReference?.contains("Sicher! C1") == true })
        assertTrue(repoUnits.all { it.visualDeck != null })

        // Verify 4 Context Tracks + Interactive Situations-Training Datasets
        assertTrue(repoUnits.first().contexts.containsKey(ContextTrack.ENGINEERING))
        assertTrue(repoUnits.first().contexts.containsKey(ContextTrack.COACHING))
        assertEquals(10, curriculum.getConversations().size)
        assertEquals(9, curriculum.getMediations().size)
        assertEquals(6, curriculum.getFootballScenarios().size)

        // Merged Vocab & Quiz Pools (365-Day Expansion)
        assertTrue(curriculum.getAllVocabCards().size >= 160)
        assertTrue(curriculum.getAllQuizItems().size >= 120)
        assertEquals(16, curriculum.getMediaImmersionMissions().size)
        val audit = curriculum.getYearAuditReport()
        assertEquals(42, audit.totalMultiSpeakerScenarios)
        assertEquals(16, audit.totalLiveMediaMissions)
        assertEquals(4, audit.goetheExamMatrix.size)
    }

    @Test
    fun `diagnostic controller orchestrates 28 repo units context tracks and zero initial assessment`() = runTest {
        controller.ensureInitialized()
        val dao = db.diagnosticDao()

        controller.updateContextTrack(ContextTrack.ENGINEERING)
        controller.recordRepoUnitStep("s1-explain", "VISUAL", 10f)
        controller.recordRepoUnitStep("s1-explain", "PUZZLE", 10f)

        val state = controller.buildMasterControlState(
            pillars = dao.getAllPillarMasteryOnce(),
            errorPatterns = dao.getErrorPatternsOnce(),
            srsStates = emptyList(),
            recentLogs = dao.getRecentLogsOnce(),
            configEntity = dao.getControllerConfigOnce(),
            unitProgress = dao.getRepoUnitProgressOnce()
        )

        assertEquals(ContextTrack.ENGINEERING, state.selectedContextTrack)
        assertEquals(7, state.stagesRoadmap.size)
        assertEquals(28, state.prioritizedRepoUnits.size)
        assertEquals(6, state.prioritizedRepoUnits.count { it.isParetoVital20 })

        val updatedSyntaxUnit = state.prioritizedRepoUnits.first { it.unit.id == "s1-explain" }
        assertTrue(updatedSyntaxUnit.visualDone)
        assertTrue(updatedSyntaxUnit.puzzleDone)
        assertEquals(2, updatedSyntaxUnit.completedStepsCount)

        // Day-1 profile seeding skips forced RUN_CALIBRATION_SWEEP on entry
        assertTrue(state.calibrated)
        assertEquals(ControllerStrategyMode.PARETO_80_20, state.strategyMode)
        assertNotEquals(DirectiveActionType.RUN_CALIBRATION_SWEEP, state.directive.actionType)
        assertEquals(DirectiveActionType.LAUNCH_REPO_UNIT, state.directive.actionType)

        val nextItem = controller.selectNextAdaptiveItem(
            pool = curriculum.getAllQuizItems(),
            state = state,
            alreadyAskedIds = emptySet(),
            recentConsecutiveErrors = 0,
            calibrationMode = false,
            coveredPillarsInSession = emptySet()
        )
        assertNotNull(nextItem)

        val targetItem = nextItem!!
        val beforeMastery = state.pillarSnapshots.first { it.pillar == targetItem.pillar }.mastery
        controller.recordQuizAnswer(
            item = targetItem,
            selectedIndex = targetItem.correctIndex,
            isCorrect = true,
            responseTimeMs = 3800L,
            markCalibrated = true
        )

        val calibratedState = controller.buildMasterControlState(
            pillars = dao.getAllPillarMasteryOnce(),
            errorPatterns = dao.getErrorPatternsOnce(),
            srsStates = emptyList(),
            recentLogs = dao.getRecentLogsOnce(),
            configEntity = dao.getControllerConfigOnce(),
            unitProgress = dao.getRepoUnitProgressOnce()
        )
        val afterMastery = calibratedState.pillarSnapshots.first { it.pillar == targetItem.pillar }.mastery
        assertTrue(afterMastery > beforeMastery)
        assertTrue(calibratedState.calibrated)
    }

    @Test
    fun `diagnostic controller tracks mistakes fixes them and prunes low roi content`() = runTest {
        controller.ensureInitialized()
        val dao = db.diagnosticDao()
        val sampleQuiz = curriculum.getAllQuizItems().first { it.difficulty >= 3 }
        val wrongIndex = (sampleQuiz.correctIndex + 1) % sampleQuiz.options.size

        controller.recordQuizAnswer(
            item = sampleQuiz,
            selectedIndex = wrongIndex,
            isCorrect = false,
            responseTimeMs = 9200L,
            markCalibrated = true
        )

        val afterMistake = dao.getMistakeLedgerOnce()
        assertEquals(1, afterMistake.size)
        assertFalse(afterMistake.first().isResolved)

        val stateWithMistake = controller.buildMasterControlState(
            pillars = dao.getAllPillarMasteryOnce(),
            errorPatterns = dao.getErrorPatternsOnce(),
            srsStates = emptyList(),
            recentLogs = dao.getRecentLogsOnce(),
            configEntity = dao.getControllerConfigOnce(),
            mistakes = afterMistake
        )
        val repairPick = controller.selectNextAdaptiveItem(
            pool = curriculum.getAllQuizItems(),
            state = stateWithMistake,
            alreadyAskedIds = emptySet(),
            recentConsecutiveErrors = 0,
            calibrationMode = false,
            coveredPillarsInSession = emptySet(),
            mistakeRepairMode = true
        )
        assertEquals(sampleQuiz.id, repairPick?.id)

        controller.recordQuizAnswer(
            item = sampleQuiz,
            selectedIndex = sampleQuiz.correctIndex,
            isCorrect = true,
            responseTimeMs = 4500L
        )
        val afterRepair = dao.getMistakeLedgerOnce()
        assertTrue(afterRepair.first().isResolved)

        val initialPoolSize = controller.getActiveQuizPool().size
        val prunedCount = controller.runParetoPruneSweep()
        assertTrue("Expected low-ROI items to be pruned", prunedCount > 0)
        val prunedPoolSize = controller.getActiveQuizPool().size
        assertEquals(initialPoolSize - prunedCount, prunedPoolSize)

        val firstPruned = dao.getPrunedContentOnce().first()
        controller.restorePrunedItem(firstPruned.contentId)
        assertEquals(prunedPoolSize + 1, controller.getActiveQuizPool().size)
    }

    @Test
    fun `diagnostic controller dynamically merges ai synthesized slides vocab and probes`() = runTest {
        controller.ensureInitialized()
        val dao = db.diagnosticDao()
        val now = System.currentTimeMillis()

        dao.insertAiSlide(
            AiGeneratedSlideEntity(
                id = "ai_slide_test_1",
                pillarId = C1Pillar.NOMINALISIERUNG.id,
                headline = "AI Genitiv-Transformation",
                subheadline = "Synthesized for your 80/20 gap",
                diagramTypeName = VisualDiagramType.NOMINAL_TRANSFORM_FLOW.name,
                formulaBefore = "weil die Kosten steigen",
                formulaAfter = "infolge steigender Kosten",
                highlightRule = "infolge + Genitiv",
                keyInsight = "Replaces causal Nebensatz with high-density C1 Nominalstil.",
                microExampleDe = "Infolge steigender Energiekosten wurden Maßnahmen ergriffen.",
                microExampleGloss = "Due to rising energy costs, measures were taken.",
                commonTrap = "Avoid Dativ after infolge in formal C1 exams.",
                createdAt = now
            )
        )
        dao.insertAiVocab(
            AiGeneratedVocabEntity(
                id = "ai_vocab_test_1",
                pillarId = C1Pillar.NOMEN_VERB.id,
                expression = "in Betracht ziehen (+ Akk.)",
                grammarTag = "Nomen-Verb-Gefüge",
                register = "Wissenschaftssprache",
                meaningEn = "to take into consideration",
                exampleDe = "Man sollte alternative Ansätze in Betracht ziehen.",
                trapWarning = "Never say 'in Betrachtung nehmen'.",
                visualHint = "[in Betracht] -> [ziehen]",
                diagramTypeName = VisualDiagramType.COLLOCATION_NETWORK.name,
                createdAt = now
            )
        )
        dao.insertAllAiQuiz(
            listOf(
                AiGeneratedQuizEntity(
                    id = "ai_quiz_test_1",
                    pillarId = C1Pillar.NOMINALISIERUNG.id,
                    difficulty = 3,
                    archetypeId = ErrorArchetype.CASE_GOVERNMENT.id,
                    promptDe = "Wählen Sie die korrekte C1-Form:",
                    contextSnippet = "___ wachsender Kritik hält der Vorstand am Kurs fest.",
                    option0 = "Ungeachtet",
                    option1 = "Wegen dem",
                    option2 = "Trotz dass",
                    option3 = "Weil",
                    correctIndex = 0,
                    explanationDe = "'ungeachtet' verlangt den Genitiv.",
                    explanationEn = "'ungeachtet' governs the genitive case.",
                    trapNote = "'trotz dass' ist ungrammatisch.",
                    createdAt = now
                )
            )
        )

        val state = controller.buildMasterControlState(
            pillars = dao.getAllPillarMasteryOnce(),
            errorPatterns = dao.getErrorPatternsOnce(),
            srsStates = emptyList(),
            recentLogs = dao.getRecentLogsOnce(),
            configEntity = dao.getControllerConfigOnce(),
            aiSlides = dao.getAiSlidesOnce(),
            aiVocab = dao.getAiVocabOnce(),
            aiQuiz = dao.getAiQuizOnce()
        )

        assertEquals(3, state.paretoSummary.aiAddedContentCount)
        val nomMod = state.orderedModules.first { it.module.pillar == C1Pillar.NOMINALISIERUNG }
        assertTrue(nomMod.module.slides.any { it.id == "ai_slide_test_1" && it.isAiSynthesized })
        assertTrue(state.orderedSrsCards.any { it.card.id == "ai_vocab_test_1" && it.card.isAiGenerated })
        assertTrue(controller.getActiveQuizPool().any { it.id == "ai_quiz_test_1" && it.isAiGenerated })
    }

    @Test
    fun `personalized visual library loads 42 exercises 10 scenario families zero duplicates and tracks dual readiness`() = runTest {
        controller.ensureInitialized()
        val dao = db.diagnosticDao()

        val lib = curriculum.getPersonalizedLibrary()
        assertEquals(10, lib.scenarioFamilies.size)
        assertEquals(42, lib.exercises.size)
        assertEquals(0, lib.validationReport.duplicatesDetected)
        assertEquals(42, lib.validationReport.totalExercises)
        assertEquals(0, lib.validationReport.missingAudioAssets.size)

        // Verify all mandatory content fields & Gemini 3.1 Studio Audio disclosures
        assertTrue(lib.exercises.all { it.audioRequirements.ttsDisclosure.isNotBlank() })
        assertTrue(lib.exercises.any { it.audioRequirements.ttsDisclosure.contains("Gemini 3.1 Studio") })
        assertTrue(lib.exercises.all { it.comprehensionTasks.isNotEmpty() })
        assertTrue(lib.exercises.all { it.acceptableAlternatives.size >= 2 })
        assertTrue(lib.exercises.all { it.connectedSpeechReductions.isNotEmpty() })

        // Verify contextual sentence-frame cards were merged into SRS vocabulary without isolated word lists
        assertTrue(curriculum.getAllVocabCards().any { it.id.startsWith("pf_voc_") })
        assertTrue(curriculum.getAllVocabCards().any { it.id.startsWith("yr_voc_") })

        val sampleEx = lib.exercises.first()
        controller.recordPersonalizedBeat(sampleEx.id, "BLIND_LISTEN")
        controller.recordSpontaneousCoachingEval(
            exercise = sampleEx,
            userReply = "Das steht zweimal auf dem Bon, schauen Sie bitte kurz.",
            eval = SpontaneousCoachingEvaluation(
                meaningCommunicatedWell = true,
                meaningFeedbackDe = "Verständlich und höflich formuliert.",
                naturalnessPercent = 74,
                primaryErrorCategory = PersonalizationErrorCategory11.NATURALNESS,
                detectedTranslationArtifactDe = "Leicht wörtliche Struktur.",
                registerAnalysisDe = "Alltagstauglich, mit Modalpartikel noch idiomatischer.",
                naturalAlternatives = sampleEx.acceptableAlternatives.map { it.text },
                targetedRepetitionPromptDe = "Nutzen Sie 'wohl' oder 'glaube ich' zur sanften Abtönung.",
                liveTransferChallengeDe = sampleEx.transferTask
            )
        )

        val state = controller.buildMasterControlState(
            pillars = dao.getAllPillarMasteryOnce(),
            errorPatterns = dao.getErrorPatternsOnce(),
            srsStates = emptyList(),
            recentLogs = dao.getRecentLogsOnce(),
            configEntity = dao.getControllerConfigOnce(),
            mistakes = dao.getMistakeLedgerOnce(),
            unitProgress = dao.getRepoUnitProgressOnce()
        )

        assertEquals(42, state.prioritizedPersonalizedExercises.size)
        assertTrue(state.functionalC1ReadinessPercent in 20..99)
        assertTrue(state.examReadinessPercent in 20..99)

        val trackedEx = state.prioritizedPersonalizedExercises.first { it.exercise.id == sampleEx.id }
        assertTrue(trackedEx.blindListeningDone)
        assertTrue(trackedEx.spontaneousSpeakingDone)
        assertTrue(state.unresolvedMistakes.any { it.mistakeKey.startsWith("pf_${sampleEx.id}") })
    }

    @Test
    fun `daily session queue embeds visual beats and scales automatically with engine session length`() = runTest {
        controller.ensureInitialized()
        val dao = db.diagnosticDao()

        suspend fun loadStateWithGoal(minutes: Int, stintOffset: Int = 0) = run {
            controller.updateDailyGoal(minutes)
            controller.buildMasterControlState(
                pillars = dao.getAllPillarMasteryOnce(),
                errorPatterns = dao.getErrorPatternsOnce(),
                srsStates = emptyList(),
                recentLogs = dao.getRecentLogsOnce(),
                configEntity = dao.getControllerConfigOnce(),
                mistakes = dao.getMistakeLedgerOnce(),
                unitProgress = dao.getRepoUnitProgressOnce(),
                stintOffset = stintOffset
            )
        }

        val quickStint = loadStateWithGoal(5)
        assertEquals(5, quickStint.dailyGoalMinutes)
        assertEquals(2, quickStint.dailySessionQueue.size)

        val standardStint = loadStateWithGoal(15)
        assertEquals(15, standardStint.dailyGoalMinutes)
        assertEquals(4, standardStint.dailySessionQueue.size)

        val enduranceStint = loadStateWithGoal(35, stintOffset = 0)
        assertEquals(35, enduranceStint.dailyGoalMinutes)
        assertEquals(6, enduranceStint.dailySessionQueue.size)

        val nextLapStint = loadStateWithGoal(35, stintOffset = 1)
        assertEquals(6, nextLapStint.dailySessionQueue.size)
        assertTrue(nextLapStint.dailySessionQueue.first().id != enduranceStint.dailySessionQueue.first().id)
    }

    @Test
    fun `state of the art 3pl rt irt fsrs 4 5 and evpm content modification reorder decisively`() = runTest {
        controller.ensureInitialized()
        val dao = db.diagnosticDao()

        // 1. Verify 3PL + Response-Time IRT distinguishes Automatic C1 (<5s) from Translation Latency (>15s)
        val fastPosterior = PsychometricMathEngine.computeBayesian3PlUpdate(
            priorMastery = 55f,
            priorSigma = 20f,
            currentTier = 2,
            recentStreak = 1,
            itemDifficulty = 3,
            isAiSurgical = false,
            isCorrect = true,
            responseTimeMs = 4_200L
        )
        val slowTranslatedPosterior = PsychometricMathEngine.computeBayesian3PlUpdate(
            priorMastery = 55f,
            priorSigma = 20f,
            currentTier = 2,
            recentStreak = 1,
            itemDifficulty = 3,
            isAiSurgical = false,
            isCorrect = true,
            responseTimeMs = 16_500L
        )
        assertEquals(AutomaticityClassification.AUTOMATIC_C1, fastPosterior.automaticity)
        assertEquals(AutomaticityClassification.TRANSLATION_LATENCY, slowTranslatedPosterior.automaticity)
        assertTrue(fastPosterior.updatedMastery > slowTranslatedPosterior.updatedMastery)
        assertTrue(fastPosterior.updatedSigma < slowTranslatedPosterior.updatedSigma)

        // 2. Verify FSRS-4.5 Power-Law Retrievability R(t = S, S) == 0.90 (90% target retention)
        val now = System.currentTimeMillis()
        val sampleStateAtStability = SrsCardStateEntity(
            cardId = "test_fsrs",
            stabilityDays = 10f,
            difficulty = 5f,
            repetitions = 3,
            lapses = 0,
            nextReviewAt = now, // elapsed == 10d == stabilityDays
            lastQuality = 3
        )
        val rAtStability = PsychometricMathEngine.computeFsrsRetrievability(sampleStateAtStability, now)
        assertEquals(0.90f, rAtStability, 0.01f)

        // 3. Verify slow >15s responses trigger Translation Risk & modify Spontaneous Speaking constraint
        val probes = curriculum.getAllQuizItems().take(3)
        probes.forEachIndexed { idx, q ->
            controller.recordQuizAnswer(
                item = q,
                selectedIndex = q.correctIndex,
                isCorrect = true,
                responseTimeMs = 15_800L,
                markCalibrated = idx == probes.lastIndex
            )
        }

        val stateWithTranslationRisk = controller.buildMasterControlState(
            pillars = dao.getAllPillarMasteryOnce(),
            errorPatterns = dao.getErrorPatternsOnce(),
            srsStates = dao.getAllSrsStatesOnce(),
            recentLogs = dao.getRecentLogsOnce(),
            configEntity = dao.getControllerConfigOnce(),
            mistakes = dao.getMistakeLedgerOnce(),
            unitProgress = dao.getRepoUnitProgressOnce()
        )
        assertTrue(stateWithTranslationRisk.precisionTelemetry.translationRiskDetected)
        assertTrue(stateWithTranslationRisk.dailySessionQueue.all { it.adaptation != null && it.evPerMinute > 0f })

        val speakBeat = stateWithTranslationRisk.dailySessionQueue.firstOrNull {
            it.beatType == DailyBeatType.SPONTANEOUS_SPEAKING
        }
        assertNotNull(speakBeat)
        assertEquals("AUTO-MOD: ANTI-ÜBERSETZUNG", speakBeat?.adaptation?.adaptationBadge)

        // 4. Verify logging an active mistake decisively reorders Sector 1 to Surgical Repair
        val hardItem = curriculum.getAllQuizItems().first { it.difficulty >= 3 }
        controller.recordQuizAnswer(
            item = hardItem,
            selectedIndex = (hardItem.correctIndex + 1) % hardItem.options.size,
            isCorrect = false,
            responseTimeMs = 6_000L
        )

        val stateAfterMistake = controller.buildMasterControlState(
            pillars = dao.getAllPillarMasteryOnce(),
            errorPatterns = dao.getErrorPatternsOnce(),
            srsStates = dao.getAllSrsStatesOnce(),
            recentLogs = dao.getRecentLogsOnce(),
            configEntity = dao.getControllerConfigOnce(),
            mistakes = dao.getMistakeLedgerOnce(),
            unitProgress = dao.getRepoUnitProgressOnce()
        )
        val firstBeat = stateAfterMistake.dailySessionQueue.first()
        assertEquals(DailyBeatType.ADAPTIVE_PROBE_BURST, firstBeat.beatType)
        assertEquals("AUTO-MOD: MINIMALPAAR-REPARATUR", firstBeat.adaptation?.adaptationBadge)
    }

    @Test
    fun `wav pcm encoder podcast rss parser and post session active adaptation work reliably`() = runTest {
        // 1. WavPcmEncoder RIFF/WAVE header verification
        val pcm = byteArrayOf(10, 20, 30, 40, 50, 60)
        val wav = WavPcmEncoder.wrapPcm16MonoToWav(pcm, sampleRate = 24_000)
        assertEquals(44 + pcm.size, wav.size)
        assertEquals('R'.code.toByte(), wav[0])
        assertEquals('I'.code.toByte(), wav[1])
        assertEquals('F'.code.toByte(), wav[2])
        assertEquals('F'.code.toByte(), wav[3])
        assertEquals('W'.code.toByte(), wav[8])
        assertEquals('A'.code.toByte(), wav[9])
        assertEquals('V'.code.toByte(), wav[10])
        assertEquals('E'.code.toByte(), wav[11])
        assertEquals(24_000, WavPcmEncoder.extractSampleRateFromMime("audio/L16;rate=24000"))

        // 2. PodcastRssClient XML enclosure parsing
        val sampleRss = """
            <?xml version="1.0" encoding="UTF-8"?>
            <rss version="2.0">
              <channel>
                <title>auto motor und sport moove</title>
                <item>
                  <title>AMG E-Performance Hybrid Analyse</title>
                  <pubDate>Tue, 18 Nov 2025 08:00:00 +0100</pubDate>
                  <enclosure url="https://cdn.example.de/amg_hybrid.mp3" type="audio/mpeg" length="123456"/>
                </item>
              </channel>
            </rss>
        """.trimIndent()
        val feed = PodcastFeedSpec("ams_moove", "ams moove", "Automotive", "https://example.de/rss", "https://example.de")
        val episodes = PodcastRssClient.parseRssXml(sampleRss, feed)
        assertEquals(1, episodes.size)
        assertEquals("AMG E-Performance Hybrid Analyse", episodes.first().title)
        assertEquals("https://cdn.example.de/amg_hybrid.mp3", episodes.first().audioUrl)

        // 3. Stint media immersion emission + Active Post-Session Controller Adaptation
        controller.ensureInitialized()
        val dao = db.diagnosticDao()
        controller.updateDailyGoal(25)
        val qItem = curriculum.getAllQuizItems().first()
        controller.recordQuizAnswer(qItem, qItem.correctIndex, isCorrect = true, responseTimeMs = 15_200L)
        controller.recordMediaMissionCompleted("mm_ams_01", quizCorrect = true)

        val state25 = controller.buildMasterControlState(
            pillars = dao.getAllPillarMasteryOnce(),
            errorPatterns = dao.getErrorPatternsOnce(),
            srsStates = dao.getAllSrsStatesOnce(),
            recentLogs = dao.getRecentLogsOnce(),
            configEntity = dao.getControllerConfigOnce(),
            mistakes = dao.getMistakeLedgerOnce(),
            unitProgress = dao.getRepoUnitProgressOnce()
        )
        assertTrue(state25.dailySessionQueue.any { it.beatType == DailyBeatType.NATIVE_MEDIA_IMMERSION })

        val adaptation = controller.concludeDailyStintAndAdapt(completedSectorsCount = 5, nextStintOffset = 1)
        assertEquals(5, adaptation.completedSectorsCount)
        assertEquals(100, adaptation.sessionAccuracyPercent)
        assertTrue(adaptation.nextStintPreviewTitles.isNotEmpty())
        assertTrue(adaptation.adjustedPillarsSummary.isNotEmpty())
    }

    @Test
    fun `personalized vibe themes provide 4 distinct motorsport and bvb aesthetics with contrast and persistence`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        app.getSharedPreferences("german_c1_vibe_prefs", Context.MODE_PRIVATE).edit().clear().commit()

        assertEquals(4, AppVibeTheme.entries.size)
        assertFalse(paletteForVibe(AppVibeTheme.PORSCHE_911).isDark)
        assertTrue(paletteForVibe(AppVibeTheme.AMG_RED).isDark)
        assertTrue(paletteForVibe(AppVibeTheme.MERCEDES_EQ_CYAN).isDark)
        assertTrue(paletteForVibe(AppVibeTheme.BVB_DORTMUND).isDark)

        val palettes = AppVibeTheme.entries.map { paletteForVibe(it) }
        assertEquals(4, palettes.map { it.primaryAccent }.toSet().size)
        assertEquals(4, palettes.map { it.heroDrawableRes }.toSet().size)
        assertTrue(palettes.all { it.heroDrawableRes != 0 })

        // Verify high-contrast foreground selection (dark ink on BVB Yellow / EQ Cyan; white on Porsche Red / AMG Red)
        assertEquals(Color(0xFF090B0F), contentColorOnAccent(paletteForVibe(AppVibeTheme.BVB_DORTMUND).primaryAccent))
        assertEquals(Color(0xFF090B0F), contentColorOnAccent(paletteForVibe(AppVibeTheme.MERCEDES_EQ_CYAN).primaryAccent))
        assertEquals(Color.White, contentColorOnAccent(paletteForVibe(AppVibeTheme.PORSCHE_911).primaryAccent))
        assertEquals(Color.White, contentColorOnAccent(paletteForVibe(AppVibeTheme.AMG_RED).primaryAccent))

        // Verify fallback and ViewModel SharedPreferences persistence
        assertEquals(AppVibeTheme.PORSCHE_911, AppVibeTheme.fromId("nonexistent"))
        assertEquals(AppVibeTheme.BVB_DORTMUND, AppVibeTheme.fromId(AppVibeTheme.BVB_DORTMUND.id))

        val vm1 = MainViewModel(app)
        assertEquals(AppVibeTheme.PORSCHE_911, vm1.vibeTheme.value)

        vm1.selectVibeTheme(AppVibeTheme.AMG_RED)
        assertEquals(AppVibeTheme.AMG_RED, vm1.vibeTheme.value)

        vm1.cycleVibeTheme()
        assertEquals(AppVibeTheme.MERCEDES_EQ_CYAN, vm1.vibeTheme.value)

        vm1.selectVibeTheme(AppVibeTheme.BVB_DORTMUND)
        val vmRestored = MainViewModel(app)
        assertEquals(AppVibeTheme.BVB_DORTMUND, vmRestored.vibeTheme.value)
    }

    @Test
    fun `commander overrides, visible decision feed, and 28-unit roadmap prioritization work end to end`() = runTest {
        controller.ensureInitialized()
        val dao = db.diagnosticDao()

        // 1. Verify Autopilot visible decision feed
        val autoState = controller.buildMasterControlState(
            pillars = dao.getAllPillarMasteryOnce(),
            errorPatterns = dao.getErrorPatternsOnce(),
            srsStates = dao.getAllSrsStatesOnce(),
            recentLogs = dao.getRecentLogsOnce(),
            configEntity = dao.getControllerConfigOnce(),
            mistakes = dao.getMistakeLedgerOnce(),
            unitProgress = dao.getRepoUnitProgressOnce()
        )
        assertEquals(4, autoState.decisionFeed.size)
        assertFalse(autoState.decisionFeed.first().isOverride)
        assertEquals(28, autoState.prioritizedRepoUnits.size)
        assertEquals(7, autoState.stagesRoadmap.size)

        // 2. Verify Commander override pinning Konjunktiv + restricting sector types + scaling audio speed
        controller.updateDailyGoal(25)
        val cmdOverride = CommanderOverrideConfig(
            commanderEnabled = true,
            pinnedFocusPillar = C1Pillar.KONJUNKTIV,
            enabledBeatTypes = setOf(
                DailyBeatType.BLIND_LISTENING_SCENE,
                DailyBeatType.LIVE_TACTICAL_TRANSFER
            ),
            audioSpeedBias = 0.90f,
            difficultyOffset = 1
        )
        val cmdState = controller.buildMasterControlState(
            pillars = dao.getAllPillarMasteryOnce(),
            errorPatterns = dao.getErrorPatternsOnce(),
            srsStates = dao.getAllSrsStatesOnce(),
            recentLogs = dao.getRecentLogsOnce(),
            configEntity = dao.getControllerConfigOnce(),
            mistakes = dao.getMistakeLedgerOnce(),
            unitProgress = dao.getRepoUnitProgressOnce(),
            commanderOverride = cmdOverride
        )
        assertTrue(cmdState.commanderConfig.commanderEnabled)
        assertTrue(cmdState.decisionFeed.first().isOverride)
        assertTrue(cmdState.dailySessionQueue.isNotEmpty())
        assertTrue(
            cmdState.dailySessionQueue.all {
                it.beatType == DailyBeatType.BLIND_LISTENING_SCENE ||
                    it.beatType == DailyBeatType.LIVE_TACTICAL_TRANSFER
            }
        )
        assertTrue(cmdState.dailySessionQueue.any { it.targetPillar == C1Pillar.KONJUNKTIV })

        // 3. Verify MainViewModel Commander persistence across recreations
        val app = ApplicationProvider.getApplicationContext<Application>()
        app.getSharedPreferences("german_c1_commander_prefs", Context.MODE_PRIVATE).edit().clear().commit()
        val vm = MainViewModel(app)
        vm.pinFocusPillarInCommander(C1Pillar.RHETORIK)
        vm.setCommanderAudioSpeed(1.15f)
        vm.setCommanderDifficultyOffset(-1)

        val vmRestored = MainViewModel(app)
        val restoredField = MainViewModel::class.java.getDeclaredField("_commanderConfig").apply {
            isAccessible = true
        }
        @Suppress("UNCHECKED_CAST")
        val restoredFlow = restoredField.get(vmRestored) as kotlinx.coroutines.flow.StateFlow<CommanderOverrideConfig>
        assertTrue(restoredFlow.value.commanderEnabled)
        assertEquals(C1Pillar.RHETORIK, restoredFlow.value.pinnedFocusPillar)
        assertEquals(1.15f, restoredFlow.value.audioSpeedBias, 0.01f)
        assertEquals(-1, restoredFlow.value.difficultyOffset)

        vmRestored.resetCommanderToAutopilot()
        assertFalse(restoredFlow.value.commanderEnabled)
        assertEquals(null, restoredFlow.value.pinnedFocusPillar)
    }

    @Test
    fun `german prosody analyzer computes wpm filler count and C1 speech flow score correctly`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val controller = GermanSpeechRecognizerController(context)

        // 1. Fluent C1 Nominalstil sentence without fillers in ~5 seconds (13 words -> ~156 WPM)
        val fluentC1Speech = "Infolge thermischer Belastungsspitzen am Wechselrichter empfiehlt das Ingenieursteam eine temporäre Spitzenleistungsbegrenzung zwecks Gewährleistung."
        controller.analyzeProsody(fluentC1Speech, customDurationSec = 6.0f)

        val metricsFluent = controller.prosodyMetrics
        assertNotNull(metricsFluent)
        assertEquals(0, metricsFluent!!.fillerWordCount)
        assertTrue(metricsFluent.detectedFillers.isEmpty())
        assertTrue("WPM should be in fluent range", metricsFluent.wordsPerMinute in 100..160)
        assertTrue("Flow score should be high without fillers", metricsFluent.flowScorePercent >= 85)

        // 2. Fragmented sentence with German fillers ("ähm", "halt", "sozusagen")
        val fillerSpeech = "Wir müssen ähm halt sozusagen das Auto drosseln weil ähm der Motor kocht."
        controller.analyzeProsody(fillerSpeech, customDurationSec = 7.0f)

        val metricsFiller = controller.prosodyMetrics
        assertNotNull(metricsFiller)
        assertTrue("Should detect at least 3 fillers", metricsFiller!!.fillerWordCount >= 3)
        assertTrue("Fillers should contain äh/ähm/halt/sozusagen", metricsFiller.detectedFillers.contains("ähm"))
        assertTrue(metricsFiller.detectedFillers.contains("halt"))
        assertTrue(metricsFiller.detectedFillers.contains("sozusagen"))
        assertTrue("Flow score should be penalized for fillers", metricsFiller.flowScorePercent < metricsFluent.flowScorePercent)
    }

    @Test
    fun `workplace roleplay branching simulator verifies Mercedes Porsche and BVB scenarios`() {
        // Verify all 3 personalized scenarios exist
        assertEquals(3, SampleWorkplaceScenarios.size)

        val mbScenario = SampleWorkplaceScenarios.first { it.id == "mb_sindelfingen_inverter" }
        assertEquals("Mercedes-Benz R&D · Sindelfingen", mbScenario.institutionBadge)
        assertEquals(3, mbScenario.options.size)
        assertTrue(mbScenario.options.any { it.quality == BranchQuality.EXECUTIVE_C1 && it.quality.score == 100 })
        assertTrue(mbScenario.options.any { it.quality == BranchQuality.INFORMAL_WEAK })
        assertTrue(mbScenario.options.any { it.quality == BranchQuality.DEFENSIVE })

        val porscheScenario = SampleWorkplaceScenarios.first { it.id == "porsche_weissach_aero" }
        assertEquals("Porsche Motorsport · Weissach", porscheScenario.institutionBadge)
        assertTrue(porscheScenario.initialPromptDe.contains("Schwedenkreuz"))
        val porscheOptimal = porscheScenario.options.first { it.quality == BranchQuality.EXECUTIVE_C1 }
        assertTrue(porscheOptimal.textDe.contains("böte sich eine stufenweise Erhöhung"))

        val bvbScenario = SampleWorkplaceScenarios.first { it.id == "bvb_press_conference" }
        assertEquals("BVB 09 · Signal Iduna Park Medienraum", bvbScenario.institutionBadge)
        val bvbOptimal = bvbScenario.options.first { it.quality == BranchQuality.EXECUTIVE_C1 }
        assertTrue(bvbOptimal.textDe.contains("Zwecks Verdichtung der Halbräume"))
    }
}
