package com.example.ai

import android.util.Log
import com.example.domain.model.AiOrchestrationPlan
import com.example.domain.model.C1Pillar
import com.example.domain.model.C1QuizItem
import com.example.domain.model.C1VocabCard
import com.example.domain.model.ControllerStrategyMode
import com.example.domain.model.ErrorArchetype
import com.example.domain.model.PillarDifficultyAdjustment
import com.example.domain.model.SituationAiFeedback
import com.example.domain.model.VisualDiagramType
import com.example.domain.model.VisualSlide
import com.example.domain.model.WritingDiagnosticResult
import com.example.domain.model.WritingTransformation
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withTimeout
import org.json.JSONObject
import java.util.UUID

private const val TAG = "GeminiC1Service"
private const val MODEL_NAME = "gemini-3.8-flash"

class GeminiC1Service {

    private val drillSchema = Schema.obj(
        properties = mapOf(
            "items" to Schema.array(
                items = Schema.obj(
                    properties = mapOf(
                        "promptDe" to Schema.string(description = "German C1 task prompt"),
                        "contextSnippet" to Schema.string(description = "German sentence with ___ blank or clause to transform"),
                        "options" to Schema.array(
                            items = Schema.string(),
                            description = "Exactly 4 distinct German options"
                        ),
                        "correctIndex" to Schema.integer(description = "0-based index of the single correct option"),
                        "difficulty" to Schema.integer(description = "Difficulty from 1 to 4"),
                        "explanationDe" to Schema.string(description = "Concise German grammar explanation"),
                        "explanationEn" to Schema.string(description = "Clear English pedagogical takeaway"),
                        "trapNote" to Schema.string(description = "Common B2 fossilized mistake to avoid")
                    )
                ),
                description = "List of 3 diagnostic C1 quiz items"
            )
        )
    )

    private val writingSchema = Schema.obj(
        properties = mapOf(
            "estimatedLevel" to Schema.string(description = "CEFR estimate like B2.2, C1.1, C1.2, or C1+"),
            "c1Score" to Schema.integer(description = "Numeric C1 quality score from 30 to 100"),
            "upgradedText" to Schema.string(description = "Rewritten text in authentic C1 Bildungssprache"),
            "diagnosticFeedback" to Schema.string(description = "Actionable coaching note tailored to the learner profile"),
            "detectedArchetypes" to Schema.array(
                items = Schema.enumeration(
                    values = ErrorArchetype.entries.map { it.id },
                    description = "Detected error archetype IDs"
                ),
                description = "Error patterns detected in the user draft"
            ),
            "transformations" to Schema.array(
                items = Schema.obj(
                    properties = mapOf(
                        "originalPhrase" to Schema.string(description = "Phrase from user draft"),
                        "upgradedPhrase" to Schema.string(description = "Upgraded C1 equivalent"),
                        "ruleTag" to Schema.string(description = "Short tag like Nominalstil, NVV, Konjunktiv I"),
                        "explanation" to Schema.string(description = "Why this upgrade elevates register/precision")
                    )
                ),
                description = "Up to 4 visual before-to-after transformations"
            )
        )
    )

    private val orchestrationSchema = Schema.obj(
        properties = mapOf(
            "executiveSummary" to Schema.string(
                description = "Concise 2-sentence executive decision explaining what the DiagnosticController is adapting and why"
            ),
            "paretoInsight" to Schema.string(
                description = "80/20 Pareto ROI insight explaining which 20% focus unlocks 80% of C1 exam score gains"
            ),
            "recommendedStrategyId" to Schema.enumeration(
                values = ControllerStrategyMode.entries.map { it.id },
                description = "Optimal strategy mode ID"
            ),
            "nextFocusPillarId" to Schema.enumeration(
                values = C1Pillar.entries.map { it.id },
                description = "Highest-ROI pillar ID to trigger next"
            ),
            "difficultyAdjustments" to Schema.array(
                items = Schema.obj(
                    properties = mapOf(
                        "pillarId" to Schema.enumeration(values = C1Pillar.entries.map { it.id }),
                        "newDifficultyTier" to Schema.integer(description = "Calibrated tier 1..4"),
                        "roiReason" to Schema.string(description = "Why this difficulty maximizes ROI")
                    )
                ),
                description = "Difficulty calibrations for 2 to 6 pillars"
            ),
            "pruneCandidateIds" to Schema.array(
                items = Schema.string(description = "Item ID from the provided lowRoiCandidates list to prune"),
                description = "IDs of low-ROI or over-mastered content to remove from active rotation"
            ),
            "newVisualSlide" to Schema.obj(
                properties = mapOf(
                    "headline" to Schema.string(description = "Punchy German C1 visual concept headline"),
                    "subheadline" to Schema.string(description = "Crisp German/English subtitle"),
                    "formulaBefore" to Schema.string(description = "B2 or error-prone German phrase"),
                    "formulaAfter" to Schema.string(description = "Upgraded C1 German structure"),
                    "highlightRule" to Schema.string(description = "Compact visual grammar formula"),
                    "keyInsight" to Schema.string(description = "1-sentence high-ROI visual takeaway"),
                    "microExampleDe" to Schema.string(description = "Authentic C1 example sentence in German"),
                    "microExampleGloss" to Schema.string(description = "English translation of microExampleDe"),
                    "commonTrap" to Schema.string(description = "Specific B2->C1 Stolperstein trap to avoid")
                )
            ),
            "newVocabCard" to Schema.obj(
                properties = mapOf(
                    "expression" to Schema.string(description = "High-ROI C1 Nomen-Verb-Verbindung, preposition, or connector"),
                    "grammarTag" to Schema.string(description = "Case/syntax tag e.g. Funktionsverb + Akk."),
                    "register" to Schema.string(description = "e.g. Bildungssprache / Goethe C1"),
                    "meaningEn" to Schema.string(description = "Precise English meaning"),
                    "exampleDe" to Schema.string(description = "Authentic German C1 sentence"),
                    "trapWarning" to Schema.string(description = "Case or collocation mistake to avoid"),
                    "visualHint" to Schema.string(description = "Mental visual image cue")
                )
            ),
            "newRemediationProbes" to Schema.array(
                items = Schema.obj(
                    properties = mapOf(
                        "promptDe" to Schema.string(description = "German C1 task prompt"),
                        "contextSnippet" to Schema.string(description = "German sentence with ___ blank or clause"),
                        "options" to Schema.array(items = Schema.string(), description = "Exactly 4 options"),
                        "correctIndex" to Schema.integer(description = "0-based correct index"),
                        "difficulty" to Schema.integer(description = "Difficulty 2..4"),
                        "explanationDe" to Schema.string(description = "German explanation"),
                        "explanationEn" to Schema.string(description = "English explanation"),
                        "trapNote" to Schema.string(description = "Trap note")
                    )
                ),
                description = "2 surgical C1 probes fixing the user's active mistakes & weak pillar"
            )
        )
    )

    private val drillModel by lazy {
        Firebase.ai.generativeModel(
            modelName = MODEL_NAME,
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = drillSchema
                temperature = 0.45f
            }
        )
    }

    private val writingModel by lazy {
        Firebase.ai.generativeModel(
            modelName = MODEL_NAME,
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = writingSchema
                temperature = 0.35f
            }
        )
    }

    private val orchestratorModel by lazy {
        Firebase.ai.generativeModel(
            modelName = MODEL_NAME,
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = orchestrationSchema
                temperature = 0.35f
            }
        )
    }

    private val tutorModel by lazy {
        Firebase.ai.generativeModel(
            modelName = MODEL_NAME,
            systemInstruction = content {
                text(
                    "You are an elite German C1 (Goethe-Zertifikat C1 / TestDaF) visual & diagnostic coach applying the 80/20 Pareto rule. " +
                        "Always respond clearly with high-ROI micro-insights, visual Before -> After transformations, " +
                        "and exact grammatical explanations tailored to the learner's diagnostic briefing. Keep responses concise and scannable."
                )
            }
        )
    }

    suspend fun orchestrateCurriculumWithAi(
        telemetryPayload: String,
        fallbackFocusPillar: C1Pillar,
        dominantArchetype: ErrorArchetype?
    ): Result<AiOrchestrationPlan> {
        return try {
            val prompt = """
                You are the state-of-the-art psychometric DiagnosticController AI engine for a personalized German C1 Mastery Programme.
                Apply Bayesian 3PL Item Response Theory, Response-Time Automaticity (eliminating mental translation from English/Arabic), FSRS-4.5 retention, and the 80/20 Pareto Principle:
                1. Analyze the learner's 6-pillar Bayesian mastery, posterior uncertainty (±sigma), response latency / TranslationRisk, FSRS retrievability, and active Mistake Ledger.
                2. Decisively calibrate difficulty tiers (1=B2+ Bridge, 2=C1.1 Core, 3=C1.2 Exam, 4=C1+ Sovereign Mastery) per pillar.
                3. Prune IDs from 'LowRoiMasteredCandidates' that yield low Fisher Information so the learner never wastes time on mastered material.
                4. Synthesize 1 vivid Before->After VisualSlide, 1 contextual C1VocabCard, and 2 surgical minimal-pair C1QuizItem remediation probes tailored directly to the active Context Track (Mercedes-AMG / Porsche 911 Powertrain, BVB 09 / SV 1845 Football Coaching, or Esslingen Everyday Spoken German) and exact logged errors.

                LIVE PSYCHOMETRIC TELEMETRY:
                $telemetryPayload
            """.trimIndent()

            val response = withTimeout(60_000L) {
                orchestratorModel.generateContent(prompt)
            }
            val rawJson = response.text ?: return Result.failure(
                IllegalStateException("Gemini returned an empty orchestration plan.")
            )
            val root = JSONObject(rawJson)

            val focusPillarId = root.optString("nextFocusPillarId", fallbackFocusPillar.id)
            val focusPillar = C1Pillar.entries.find { it.id == focusPillarId } ?: fallbackFocusPillar

            val strategyId = root.optString("recommendedStrategyId", ControllerStrategyMode.PARETO_80_20.id)
            val strategy = ControllerStrategyMode.entries.find { it.id == strategyId }
                ?: ControllerStrategyMode.PARETO_80_20

            val adjArray = root.optJSONArray("difficultyAdjustments")
            val adjustments = if (adjArray != null) {
                (0 until adjArray.length()).mapNotNull { idx ->
                    val o = adjArray.getJSONObject(idx)
                    val p = C1Pillar.entries.find { it.id == o.optString("pillarId") } ?: return@mapNotNull null
                    PillarDifficultyAdjustment(
                        pillar = p,
                        newDifficultyTier = o.optInt("newDifficultyTier", 2).coerceIn(1, 4),
                        roiReason = o.optString("roiReason", "Optimized for 80/20 ROI")
                    )
                }
            } else {
                emptyList()
            }

            val pruneJson = root.optJSONArray("pruneCandidateIds")
            val pruneIds = if (pruneJson != null) {
                (0 until pruneJson.length()).map { pruneJson.getString(it) }.filter { it.isNotBlank() }
            } else {
                emptyList()
            }

            val slideObj = root.getJSONObject("newVisualSlide")
            val newSlide = VisualSlide(
                id = "ai_slide_${focusPillar.id}_${UUID.randomUUID().toString().take(6)}",
                pillar = focusPillar,
                slideIndex = 5,
                totalSlides = 5,
                headline = slideObj.getString("headline"),
                subheadline = slideObj.getString("subheadline"),
                diagramType = diagramForPillar(focusPillar),
                formulaBefore = slideObj.getString("formulaBefore"),
                formulaAfter = slideObj.getString("formulaAfter"),
                highlightRule = slideObj.getString("highlightRule"),
                keyInsight = slideObj.getString("keyInsight"),
                microExampleDe = slideObj.getString("microExampleDe"),
                microExampleGloss = slideObj.getString("microExampleGloss"),
                commonTrap = slideObj.getString("commonTrap"),
                checkpoint = null,
                isAiSynthesized = true
            )

            val vocabObj = root.getJSONObject("newVocabCard")
            val newVocab = C1VocabCard(
                id = "ai_voc_${focusPillar.id}_${UUID.randomUUID().toString().take(6)}",
                pillar = focusPillar,
                expression = vocabObj.getString("expression"),
                grammarTag = vocabObj.getString("grammarTag"),
                register = vocabObj.getString("register"),
                meaningEn = vocabObj.getString("meaningEn"),
                exampleDe = vocabObj.getString("exampleDe"),
                trapWarning = vocabObj.getString("trapWarning"),
                visualHint = vocabObj.getString("visualHint"),
                diagramType = diagramForPillar(focusPillar),
                isAiGenerated = true
            )

            val probesArray = root.optJSONArray("newRemediationProbes")
            val newProbes = if (probesArray != null) {
                (0 until probesArray.length()).mapNotNull { idx ->
                    val obj = probesArray.getJSONObject(idx)
                    val optsJson = obj.getJSONArray("options")
                    val opts = (0 until optsJson.length()).map { optsJson.getString(it) }
                    if (opts.size < 4) return@mapNotNull null
                    C1QuizItem(
                        id = "ai_q_${focusPillar.id}_${UUID.randomUUID().toString().take(6)}",
                        pillar = focusPillar,
                        difficulty = obj.optInt("difficulty", 3).coerceIn(1, 4),
                        archetype = dominantArchetype ?: ErrorArchetype.REGISTER_COLLOCATION,
                        promptDe = obj.getString("promptDe"),
                        contextSnippet = obj.getString("contextSnippet"),
                        options = opts.take(4),
                        correctIndex = obj.optInt("correctIndex", 0).coerceIn(0, 3),
                        visualDiagram = diagramForPillar(focusPillar),
                        explanationDe = obj.getString("explanationDe"),
                        explanationEn = obj.getString("explanationEn"),
                        trapNote = obj.getString("trapNote"),
                        isAiGenerated = true
                    )
                }
            } else {
                emptyList()
            }

            Result.success(
                AiOrchestrationPlan(
                    executiveSummary = root.getString("executiveSummary"),
                    paretoInsight = root.getString("paretoInsight"),
                    recommendedStrategy = strategy,
                    nextFocusPillar = focusPillar,
                    difficultyAdjustments = adjustments,
                    pruneCandidateIds = pruneIds,
                    newVisualSlide = newSlide,
                    newVocabCard = newVocab,
                    newRemediationProbes = newProbes
                )
            )
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "AI curriculum orchestration timed out", e)
            Result.failure(Exception("AI curriculum optimization timed out after 60s. Please retry."))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "AI curriculum orchestration failed", e)
            Result.failure(e)
        }
    }

    suspend fun generateAdaptiveDrills(
        diagnosticBriefing: String,
        targetPillar: C1Pillar,
        targetArchetype: ErrorArchetype?
    ): Result<List<C1QuizItem>> {
        return try {
            val archetypeFocus = targetArchetype?.labelDe ?: "allgemeine C1-Präzision"
            val prompt = """
                Learner Diagnostic Profile: $diagnosticBriefing
                Target Pillar: ${targetPillar.titleDe} (${targetPillar.subtitleEn})
                Target Error Archetype: $archetypeFocus

                Generate exactly 3 brand-new, high-ROI German C1 multiple-choice diagnostic items specifically targeting this learner's weak spot (80/20 rule).
                Each item MUST have:
                - exactly 4 options (index 0..3) where only 1 is completely idiomatic and grammatically accurate at C1 level
                - realistic distractors reflecting typical B2 fossilizations
                - concise German & English explanations and a trapNote.
            """.trimIndent()

            val response = withTimeout(60_000L) {
                drillModel.generateContent(prompt)
            }
            val rawJson = response.text ?: return Result.failure(
                IllegalStateException("Gemini returned an empty drill response.")
            )
            val root = JSONObject(rawJson)
            val itemsArray = root.getJSONArray("items")
            val parsed = (0 until itemsArray.length()).mapNotNull { idx ->
                val obj = itemsArray.getJSONObject(idx)
                val optsJson = obj.getJSONArray("options")
                val options = (0 until optsJson.length()).map { optsJson.getString(it) }
                if (options.size < 2) return@mapNotNull null
                val correctIdx = obj.optInt("correctIndex", 0).coerceIn(0, options.lastIndex)
                val diff = obj.optInt("difficulty", 3).coerceIn(1, 4)
                C1QuizItem(
                    id = "ai_${targetPillar.id}_${UUID.randomUUID().toString().take(6)}",
                    pillar = targetPillar,
                    difficulty = diff,
                    archetype = targetArchetype ?: ErrorArchetype.REGISTER_COLLOCATION,
                    promptDe = obj.getString("promptDe"),
                    contextSnippet = obj.getString("contextSnippet"),
                    options = options,
                    correctIndex = correctIdx,
                    visualDiagram = diagramForPillar(targetPillar),
                    explanationDe = obj.getString("explanationDe"),
                    explanationEn = obj.getString("explanationEn"),
                    trapNote = obj.getString("trapNote"),
                    isAiGenerated = true
                )
            }
            if (parsed.isEmpty()) {
                Result.failure(IllegalStateException("No valid C1 items were parsed from AI response."))
            } else {
                Result.success(parsed)
            }
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Adaptive drill generation timed out", e)
            Result.failure(Exception("Drill generation timed out after 60 seconds. Please try again."))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Adaptive drill generation failed", e)
            Result.failure(e)
        }
    }

    suspend fun evaluateAndUpgradeWriting(
        userText: String,
        topicPrompt: String,
        diagnosticBriefing: String
    ): Result<WritingDiagnosticResult> {
        return try {
            val prompt = """
                Learner Diagnostic Profile: $diagnosticBriefing
                Writing Topic: $topicPrompt
                User German Draft:
                "$userText"

                Evaluate this German text against Goethe C1 / TestDaF standards using high-ROI 80/20 feedback.
                1. Estimate CEFR sub-level and numeric C1 score (30-100).
                2. Produce a polished 'upgradedText' in natural C1 Bildungssprache using Nominalisierung, Nomen-Verb-Verbindungen, and complex syntax while preserving the user's meaning.
                3. Provide up to 4 sharp before->after transformations highlighting exact C1 upgrades.
                4. Identify any error archetypes present from the allowed enum IDs.
            """.trimIndent()

            val response = withTimeout(60_000L) {
                writingModel.generateContent(prompt)
            }
            val rawJson = response.text ?: return Result.failure(
                IllegalStateException("Gemini returned an empty writing evaluation.")
            )
            val json = JSONObject(rawJson)
            val archArray = json.optJSONArray("detectedArchetypes")
            val archetypes = if (archArray != null) {
                (0 until archArray.length()).mapNotNull { i ->
                    val id = archArray.getString(i)
                    ErrorArchetype.entries.find { it.id == id }
                }.distinct()
            } else {
                emptyList()
            }
            val transArray = json.optJSONArray("transformations")
            val transformations = if (transArray != null) {
                (0 until transArray.length()).map { i ->
                    val t = transArray.getJSONObject(i)
                    WritingTransformation(
                        originalPhrase = t.getString("originalPhrase"),
                        upgradedPhrase = t.getString("upgradedPhrase"),
                        ruleTag = t.getString("ruleTag"),
                        explanation = t.getString("explanation")
                    )
                }
            } else {
                emptyList()
            }

            Result.success(
                WritingDiagnosticResult(
                    estimatedLevel = json.getString("estimatedLevel"),
                    c1Score = json.optInt("c1Score", 65).coerceIn(30, 100),
                    upgradedText = json.getString("upgradedText"),
                    diagnosticFeedback = json.getString("diagnosticFeedback"),
                    detectedArchetypes = archetypes,
                    transformations = transformations
                )
            )
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Writing evaluation timed out", e)
            Result.failure(Exception("Writing evaluation timed out after 60 seconds. Please try again."))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Writing evaluation failed", e)
            Result.failure(e)
        }
    }

    fun streamSocraticCoach(question: String, diagnosticBriefing: String): Flow<String> {
        val prompt = """
            [Diagnostic Context: $diagnosticBriefing]
            Learner Question: $question

            Answer using visual Before -> After German examples, pinpoint high-ROI C1 patterns (80/20 rule), and give 1 mini check question at the end.
        """.trimIndent()
        return tutorModel.generateContentStream(prompt)
            .map { chunk -> chunk.text.orEmpty() }
            .catch { throwable ->
                if (throwable is CancellationException) throw throwable
                Log.e(TAG, "Streaming tutor error", throwable)
                throw throwable
            }
    }

    private val situationSchema = Schema.obj(
        properties = mapOf(
            "scorePercent" to Schema.integer(description = "C1 pragmatic & register score from 35 to 100"),
            "registerVerdict" to Schema.string(description = "Short German verdict on tone, register, and interruption handling"),
            "strengthsDe" to Schema.string(description = "What worked well in the learner response"),
            "upgradedResponseDe" to Schema.string(description = "Native sovereign C1 model response for this exact situation"),
            "keyTakeawayDe" to Schema.string(description = "One concrete 80/20 C1 Redemittel or pragmatic takeaway")
        )
    )

    private val situationModel by lazy {
        Firebase.ai.generativeModel(
            modelName = MODEL_NAME,
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = situationSchema
                temperature = 0.4f
            }
        )
    }

    suspend fun evaluateSituationTurn(
        scenarioTitle: String,
        partnerPrompt: String,
        objective: String,
        userReply: String,
        contextTrackLabel: String
    ): Result<SituationAiFeedback> {
        return try {
            withTimeout(60_000L) {
                val prompt = """
                    Du bist ein strenger, konstruktiver C1-Prüfer und Kommunikationstrainer.
                    Kontext-Spur: $contextTrackLabel
                    Situation: $scenarioTitle
                    Gesprächspartner sagte: "$partnerPrompt"
                    Kommunikatives Ziel: $objective
                    Antwort des Lernenden: "$userReply"

                    Bewerte Pragmatik, Registertreffsicherheit, Souveränität (auch bei Unterbrechung/Gegenwind) und liefere eine idiomatische C1-Musterformulierung.
                """.trimIndent()
                val response = situationModel.generateContent(content { text(prompt) })
                val obj = JSONObject(response.text ?: "{}")
                Result.success(
                    SituationAiFeedback(
                        scorePercent = obj.optInt("scorePercent", 75).coerceIn(30, 100),
                        registerVerdict = obj.optString("registerVerdict", "Solide Reaktion mit C1-Ausbaupotenzial"),
                        strengthsDe = obj.optString("strengthsDe", "Klarer Bezug auf das Gesprächsziel."),
                        upgradedResponseDe = obj.optString("upgradedResponseDe", userReply),
                        keyTakeawayDe = obj.optString("keyTakeawayDe", "Nutzen Sie konzessive Einstiege („Zwar …, allerdings …“) für souveräne Abwägung.")
                    )
                )
            }
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Situation evaluation timed out", e)
            Result.failure(Exception("AI-Bewertung hat das Zeitlimit überschritten. Bitte erneut versuchen."))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Situation evaluation failed", e)
            Result.failure(e)
        }
    }

    private val spontaneousSchema = Schema.obj(
        properties = mapOf(
            "meaningCommunicatedWell" to Schema.boolean(description = "True if the learner's German was understandable and addressed the communicative goal"),
            "meaningFeedbackDe" to Schema.string(description = "Constructive German assessment of communicative meaning without marking understandable sentences as completely wrong"),
            "naturalnessPercent" to Schema.integer(description = "Idiomatic C1 naturalness & situational fit from 40 to 100"),
            "primaryErrorCategory" to Schema.enumeration(
                values = listOf(
                    "LISTENING_RECOGNITION",
                    "CONNECTED_SPEECH_DECODING",
                    "VOCABULARY",
                    "INTERPRETATION",
                    "SITUATIONAL_UNDERSTANDING",
                    "ACTIVE_RETRIEVAL",
                    "SENTENCE_FORMULATION",
                    "GRAMMAR_UNDER_PRESSURE",
                    "NATURALNESS",
                    "REGISTER",
                    "INTERACTION"
                ),
                description = "Exact primary personalization category to target next"
            ),
            "detectedTranslationArtifactDe" to Schema.string(description = "Specific note on any literal translation from English/Arabic or unnatural stiffness, or praise if none"),
            "registerAnalysisDe" to Schema.string(description = "Register analysis (Everyday / Swabian-Standard / Automotive Engineering / F-Jugend Kid vs. Coach)"),
            "naturalAlternatives" to Schema.array(
                items = Schema.string(description = "Natural idiomatic German C1 formulation for this exact moment")
            ),
            "targetedRepetitionPromptDe" to Schema.string(description = "1-sentence immediate active repetition drill fixing the exact weak spot"),
            "liveTransferChallengeDe" to Schema.string(description = "A brand-new unfamiliar transfer situation testing the same underlying communicative skill")
        )
    )

    private val spontaneousModel by lazy {
        Firebase.ai.generativeModel(
            modelName = MODEL_NAME,
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = spontaneousSchema
                temperature = 0.45f
            }
        )
    }

    suspend fun evaluateSpontaneousCounterpart(
        exerciseTitle: String,
        subRegister: String,
        speakingPrompt: String,
        interactionTwist: String,
        userReply: String
    ): Result<com.example.domain.model.SpontaneousCoachingEvaluation> {
        return try {
            withTimeout(60_000L) {
                val prompt = """
                    Learner Profile: Arabic L1, English C2, Spanish C1, German B2+ -> C1.
                    Lives in Esslingen, Automotive Systems M.Eng. student, Powertrain intern at Mercedes-Benz, F-Jugend Co-Trainer at SV 1845 Esslingen.
                    Scenario: $exerciseTitle
                    Target Register: $subRegister
                    Speaking Prompt: $speakingPrompt
                    Unexpected Counterpart Twist: $interactionTwist
                    Learner Independent Response: "$userReply"

                    Rules:
                    1. Do NOT mark understandable German as completely wrong — acknowledge communicative success first, then upgrade precision, naturalness, and register.
                    2. Classify the primary opportunity into the exact 11-category enum.
                    3. Flag any literal English/Arabic calque if present.
                    4. Provide 2-3 natural German alternatives, 1 targeted repetition cue, and 1 fresh unfamiliar transfer situation.
                """.trimIndent()
                val response = spontaneousModel.generateContent(content { text(prompt) })
                val obj = JSONObject(response.text ?: "{}")
                val altsArr = obj.optJSONArray("naturalAlternatives")
                val alts = if (altsArr != null) {
                    (0 until altsArr.length()).map { altsArr.optString(it) }.filter { it.isNotBlank() }
                } else emptyList()

                Result.success(
                    com.example.domain.model.SpontaneousCoachingEvaluation(
                        meaningCommunicatedWell = obj.optBoolean("meaningCommunicatedWell", true),
                        meaningFeedbackDe = obj.optString("meaningFeedbackDe", "Ihre Aussage war inhaltlich verständlich und zielgerichtet."),
                        naturalnessPercent = obj.optInt("naturalnessPercent", 76).coerceIn(35, 100),
                        primaryErrorCategory = com.example.domain.model.PersonalizationErrorCategory11.fromId(
                            obj.optString("primaryErrorCategory")
                        ),
                        detectedTranslationArtifactDe = obj.optString("detectedTranslationArtifactDe", "Kein direkter Übersetzungsbruch erkannt; Fokus liegt auf natürlicher Abtönung."),
                        registerAnalysisDe = obj.optString("registerAnalysisDe", "Passend für $subRegister mit Feinschliff-Potenzial."),
                        naturalAlternatives = alts,
                        targetedRepetitionPromptDe = obj.optString("targetedRepetitionPromptDe", "Formulieren Sie denselben Gedanken noch einmal mit einer natürlichen Modalpartikel."),
                        liveTransferChallengeDe = obj.optString("liveTransferChallengeDe", "Übertragen Sie dieselbe Reaktion auf ein kurzes Flurgespräch unter Zeitdruck.")
                    )
                )
            }
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Spontaneous counterpart evaluation timed out", e)
            Result.failure(Exception("AI-Analyse hat das Zeitlimit überschritten. Bitte erneut senden."))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Spontaneous counterpart evaluation failed", e)
            Result.failure(e)
        }
    }

    fun diagramForPillar(pillar: C1Pillar): VisualDiagramType = when (pillar) {
        C1Pillar.NOMINALISIERUNG -> VisualDiagramType.NOMINAL_TRANSFORM_FLOW
        C1Pillar.KONJUNKTIV -> VisualDiagramType.KONJUNKTIV_SPECTRUM
        C1Pillar.SATZGEFUEGE -> VisualDiagramType.PARTICIPLE_UNPACK_TREE
        C1Pillar.NOMEN_VERB -> VisualDiagramType.COLLOCATION_NETWORK
        C1Pillar.MODALPARTIKELN -> VisualDiagramType.MODAL_PARTICLE_MATRIX
        C1Pillar.RHETORIK -> VisualDiagramType.ARGUMENT_PYRAMID
    }
}
