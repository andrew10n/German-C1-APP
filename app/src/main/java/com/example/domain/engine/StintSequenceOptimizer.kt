package com.example.domain.engine

import com.example.data.local.MistakeLedgerEntity
import com.example.domain.model.C1Pillar
import com.example.domain.model.C1QuizItem
import com.example.domain.model.CognitiveLoadLevel
import com.example.domain.model.ContextTrack
import com.example.domain.model.ControllerStrategyMode
import com.example.domain.model.ErrorArchetype
import com.example.domain.model.FootballTacticalScenario
import com.example.domain.model.MediaImmersionMission
import com.example.domain.model.MediationScenarioItem
import com.example.domain.model.PrioritizedPersonalizedExercise
import com.example.domain.model.PrioritizedRepoUnit
import kotlin.math.roundToInt

data class OptimizedStintPlan(
    val orderedBeats: List<DailySessionBeat>,
    val decisionHeadlineDe: String,
    val modificationsCount: Int
)

/**
 * Decisive Expected-Value-Per-Minute (EVPM) Knapsack, Dynamic Reorderer,
 * and On-the-Fly Content Adaptation Engine (Zero Initial Assessment — Seeded from Profile).
 */
object StintSequenceOptimizer {

    fun buildOptimalStint(
        dailyGoalMinutes: Int,
        calibrated: Boolean,
        snapshots: List<PillarDiagnosticSnapshot>,
        unresolvedMistakes: List<MistakeLedgerEntity>,
        dominantError: ErrorArchetype?,
        cognitiveLoad: CognitiveLoadLevel,
        translationRiskDetected: Boolean,
        strategyMode: ControllerStrategyMode,
        prioritizedPersonalized: List<PrioritizedPersonalizedExercise>,
        prioritizedRepoUnits: List<PrioritizedRepoUnit>,
        orderedSrs: List<PrioritizedSrsCard>,
        activeQuizPool: List<C1QuizItem>,
        footballList: List<FootballTacticalScenario>,
        mediationList: List<MediationScenarioItem>,
        selectedTrack: ContextTrack,
        stintOffset: Int,
        mediaMissions: List<MediaImmersionMission> = emptyList(),
        completedMediaIds: Set<String> = emptySet(),
        commanderOverride: CommanderOverrideConfig = CommanderOverrideConfig()
    ): OptimizedStintPlan {
        val targetSectorCount = when {
            dailyGoalMinutes <= 8 -> 2
            dailyGoalMinutes <= 15 -> 4
            dailyGoalMinutes <= 26 -> 5
            else -> 6
        }

        val activePinnedPillar = if (commanderOverride.commanderEnabled) commanderOverride.pinnedFocusPillar else null
        val topVital = activePinnedPillar?.let { p -> snapshots.find { it.pillar == p } } ?: snapshots.first()
        val pillarMap = snapshots.associateBy { it.pillar }
        val candidates = mutableListOf<DailySessionBeat>()

        // --- CANDIDATE A: Surgical Mistake Repair / Targeted 3PL-IRT Probe ---
        if (activeQuizPool.isNotEmpty()) {
            val mistakeSourceIds = unresolvedMistakes.map { it.sourceItemId }.toSet()
            val mistakeArchetypeIds = unresolvedMistakes.map { it.archetypeId }.toSet()

            val selectedProbes = if (unresolvedMistakes.isNotEmpty()) {
                val exactMatches = activeQuizPool.filter { it.id in mistakeSourceIds }
                val siblingMatches = activeQuizPool.filter {
                    it.id !in mistakeSourceIds &&
                        (it.archetype.id in mistakeArchetypeIds || it.pillar.id == unresolvedMistakes.first().pillarId)
                }.sortedByDescending { if (it.isAiGenerated) 2 else 1 }
                (exactMatches + siblingMatches + activeQuizPool.filter { it.pillar == topVital.pillar })
                    .distinctBy { it.id }
                    .take(3)
            } else {
                val baseTier = if (cognitiveLoad == CognitiveLoadLevel.FATIGUED) {
                    (topVital.targetDifficultyTier - 1).coerceAtLeast(1)
                } else {
                    topVital.targetDifficultyTier
                }
                val effectiveTier = (baseTier + if (commanderOverride.commanderEnabled) commanderOverride.difficultyOffset else 0)
                    .coerceIn(1, 4)
                val theta = PsychometricMathEngine.masteryToTheta(topVital.mastery) +
                    (if (commanderOverride.commanderEnabled) commanderOverride.difficultyOffset * 0.45f else 0f)
                val ranked = activeQuizPool
                    .sortedByDescending { q ->
                        val a = PsychometricMathEngine.difficultyToDiscrimination(q.difficulty, q.isAiGenerated)
                        val b = PsychometricMathEngine.difficultyToBeta(q.difficulty)
                        val info = PsychometricMathEngine.fisherInformation3Pl(theta, a, b)
                        val pillarBoost = if (q.pillar == topVital.pillar) 2.4f else 0.85f
                        val tierBoost = if (q.difficulty == effectiveTier) 1.38f else 1.0f
                        val aiBoost = if (q.isAiGenerated) 1.30f else 1.0f
                        info * pillarBoost * tierBoost * aiBoost
                    }
                val offsetIdx = (stintOffset * 3) % ranked.size.coerceAtLeast(1)
                (ranked.drop(offsetIdx) + ranked.take(offsetIdx)).take(3)
            }

            val probeEvpm = when {
                unresolvedMistakes.isNotEmpty() -> 26.5f + unresolvedMistakes.size * 5.2f
                strategyMode == ControllerStrategyMode.INTENSIVE_REPAIR -> 19.5f
                strategyMode == ControllerStrategyMode.EXAM_SPRINT -> 17.6f
                else -> 13.8f + (topVital.roiScore * 0.14f)
            } * cognitiveFitMultiplier(DailyBeatType.ADAPTIVE_PROBE_BURST, cognitiveLoad)

            val probeAdaptation = craftProbeAdaptation(
                mistakesCount = unresolvedMistakes.size,
                topVital = topVital,
                cognitiveLoad = cognitiveLoad,
                translationRisk = translationRiskDetected
            )

            candidates.add(
                DailySessionBeat(
                    id = "beat_probe_${if (unresolvedMistakes.isNotEmpty()) "repair_${unresolvedMistakes.size}" else "cat"}_$stintOffset",
                    sectorNumber = 1,
                    beatType = DailyBeatType.ADAPTIVE_PROBE_BURST,
                    title = if (unresolvedMistakes.isNotEmpty()) {
                        "Präzisions-Reparatur: ${unresolvedMistakes.size} aktive Fehler"
                    } else {
                        "Goethe & C1 Präzisions-Impuls: ${topVital.pillar.shortLabel} (L${topVital.targetDifficultyTier})"
                    },
                    controllerReason = if (unresolvedMistakes.isNotEmpty()) {
                        "Höchster Grenz-ROI (${probeEvpm.roundToInt()} EV/m) · Eliminiert wiederkehrende Fehlermuster"
                    } else {
                        "80/20 Vital-Hebel · Deckt ${topVital.roiSharePercent}% Ihres nächsten C1-Sprungs ab"
                    },
                    estimatedMinutes = 3,
                    targetPillar = selectedProbes.firstOrNull()?.pillar ?: topVital.pillar,
                    evPerMinute = probeEvpm,
                    adaptation = probeAdaptation,
                    quizProbes = selectedProbes
                )
            )
        }

        // --- CANDIDATE B & C: Priority 1 Blind Listening (Gemini Studio Multi-Speaker) & Priority 2 Spontaneous Speaking ---
        if (prioritizedPersonalized.isNotEmpty()) {
            val rankedEx = prioritizedPersonalized[stintOffset % prioritizedPersonalized.size]
            val ex = rankedEx.exercise
            val exSnap = pillarMap[ex.mappedPillar] ?: topVital

            val listeningEvpm = (
                18.4f +
                    (if (!rankedEx.blindListeningDone) 4.8f else 0.8f) +
                    (if (ex.mappedContextTrack == selectedTrack) 3.4f else 0f) +
                    ((100f - exSnap.mastery) * 0.08f)
                ) * cognitiveFitMultiplier(DailyBeatType.BLIND_LISTENING_SCENE, cognitiveLoad)

            val listenAdaptation = craftListeningAdaptation(
                pillarMastery = exSnap.mastery,
                cognitiveLoad = cognitiveLoad,
                reductionCount = ex.connectedSpeechReductions.size
            )

            candidates.add(
                DailySessionBeat(
                    id = "beat_listen_${ex.id}_$stintOffset",
                    sectorNumber = 2,
                    beatType = DailyBeatType.BLIND_LISTENING_SCENE,
                    title = ex.title,
                    controllerReason = "Prio 1 Studio-Mehrsprecher (${listeningEvpm.roundToInt()} EV/m) · ${ex.connectedSpeechReductions.size} Verschleifungen",
                    estimatedMinutes = 4,
                    targetPillar = ex.mappedPillar,
                    evPerMinute = listeningEvpm,
                    adaptation = listenAdaptation,
                    personalizedExercise = ex
                )
            )

            val speakingEvpm = (
                17.8f +
                    (if (translationRiskDetected) 5.8f else 1.4f) +
                    (if (!rankedEx.spontaneousSpeakingDone) 4.2f else 0.8f) +
                    (if (ex.mappedContextTrack == selectedTrack) 2.8f else 0f)
                ) * cognitiveFitMultiplier(DailyBeatType.SPONTANEOUS_SPEAKING, cognitiveLoad)

            val speakAdaptation = craftSpeakingAdaptation(
                selectedTrack = selectedTrack,
                dominantError = dominantError,
                translationRisk = translationRiskDetected,
                cognitiveLoad = cognitiveLoad
            )

            candidates.add(
                DailySessionBeat(
                    id = "beat_speak_${ex.id}_$stintOffset",
                    sectorNumber = 3,
                    beatType = DailyBeatType.SPONTANEOUS_SPEAKING,
                    title = "Spontan-Reaktion: ${ex.subskill}",
                    controllerReason = if (translationRiskDetected) {
                        "Übersetzungs-Latenz erkannt (${speakingEvpm.roundToInt()} EV/m) · Erzwingt Direkt-Abruf"
                    } else {
                        "Prio 2 Spontanität (${speakingEvpm.roundToInt()} EV/m) · Frei reagieren + Register-Regler"
                    },
                    estimatedMinutes = 4,
                    targetPillar = ex.mappedPillar,
                    evPerMinute = speakingEvpm,
                    adaptation = speakAdaptation,
                    personalizedExercise = ex
                )
            )
        }

        // --- CANDIDATE D: Real German Broadcast / Podcast / Book Immersion (Zero TTS!) ---
        if (mediaMissions.isNotEmpty()) {
            val rankedMissions = mediaMissions.sortedByDescending { m ->
                val trackScore = if (m.contextTrack == selectedTrack) 18f else 4f
                val freshBonus = if (m.id !in completedMediaIds) 12f else 0f
                val pillarGap = 100f - (pillarMap[m.mappedPillar]?.mastery ?: 52f)
                trackScore + freshBonus + pillarGap * 0.18f
            }
            val pickedMission = rankedMissions[stintOffset % rankedMissions.size]
            val mSnap = pillarMap[pickedMission.mappedPillar] ?: topVital

            val mediaEvpm = (
                16.6f +
                    (if (pickedMission.id !in completedMediaIds) 3.4f else 0.8f) +
                    (if (pickedMission.contextTrack == selectedTrack) 2.6f else 0.5f) +
                    (if (strategyMode == ControllerStrategyMode.EXAM_SPRINT) 2.2f else 0f) +
                    ((100f - mSnap.mastery) * 0.06f)
                ) * cognitiveFitMultiplier(DailyBeatType.NATIVE_MEDIA_IMMERSION, cognitiveLoad)

            candidates.add(
                DailySessionBeat(
                    id = "beat_media_${pickedMission.id}_$stintOffset",
                    sectorNumber = 4,
                    beatType = DailyBeatType.NATIVE_MEDIA_IMMERSION,
                    title = pickedMission.title,
                    controllerReason = "Echt-Audio & ${pickedMission.goetheExamPartTag} (${mediaEvpm.roundToInt()} EV/m)",
                    estimatedMinutes = 4,
                    targetPillar = pickedMission.mappedPillar,
                    evPerMinute = mediaEvpm,
                    adaptation = BeatAdaptationSpec(
                        adaptationBadge = "ECHT-RUNDFUNK & PODCAST",
                        modifiedConstraintDe = "100% echtes Muttersprachler-Audio: Streamen Sie die aktuelle Folge direkt ohne Transkript und achten Sie auf die 4 Ziel-Kollokationen.",
                        targetLatencySeconds = 8,
                        recommendedAudioRate = 1.0f,
                        scaffoldingLevel = 1
                    ),
                    mediaMission = pickedMission
                )
            )
        }

        // --- CANDIDATE E: FSRS-4.5 Desirable-Difficulty Contextual Sentence Frames ---
        if (orderedSrs.isNotEmpty()) {
            val start = (stintOffset * 3) % orderedSrs.size
            val srsSlice = (0 until 3)
                .map { idx -> orderedSrs[(start + idx) % orderedSrs.size] }
                .distinctBy { it.card.id }

            val avgRet = srsSlice.map { it.retrievabilityPercent }.average().toFloat()
            val lapseCount = srsSlice.sumOf { it.state?.lapses ?: 0 }
            val aiCount = srsSlice.count { it.card.isAiGenerated }

            val srsEvpm = (
                14.0f +
                    ((92f - avgRet).coerceAtLeast(0f) * 0.24f) +
                    lapseCount * 3.4f +
                    aiCount * 2.5f
                ) * cognitiveFitMultiplier(DailyBeatType.SRS_VOCAB_BURST, cognitiveLoad)

            val srsAdaptation = BeatAdaptationSpec(
                adaptationBadge = if (avgRet < 80f) {
                    "AUTO-MOD: VERGESSENS-KANTE (${avgRet.roundToInt()}% R)"
                } else {
                    "AUTO-MOD: AKTIV-SATZRAHMEN"
                },
                modifiedConstraintDe = if (translationRiskDetected) {
                    "Direkt-Abruf (<5s): Bilden Sie sofort 1 eigenen Satz für ${selectedTrack.shortLabel} laut, bevor Sie die Karte umdrehen."
                } else {
                    "Satzrahmen statt Einzelwort: Achten Sie beim Umdrehen exakt auf Kasus-Regierung und Funktionsverb."
                },
                targetLatencySeconds = 5,
                recommendedAudioRate = 1.0f,
                scaffoldingLevel = if (cognitiveLoad == CognitiveLoadLevel.FATIGUED) 2 else 1
            )

            candidates.add(
                DailySessionBeat(
                    id = "beat_srs_$stintOffset",
                    sectorNumber = 5,
                    beatType = DailyBeatType.SRS_VOCAB_BURST,
                    title = "FSRS-4.5 Satzrahmen-Anker (${srsSlice.size} Kollokationen)",
                    controllerReason = "Abrufwahrscheinlichkeit Ø ${avgRet.roundToInt()}% (${srsEvpm.roundToInt()} EV/m) · Keine Wortlisten",
                    estimatedMinutes = 3,
                    targetPillar = srsSlice.first().card.pillar,
                    evPerMinute = srsEvpm,
                    adaptation = srsAdaptation,
                    srsCards = srsSlice
                )
            )
        }

        // --- CANDIDATE F: Textbook Visual Blueprint & Clause-Snap (Aspekte neu / Sicher! / Erkundungen) ---
        if (prioritizedRepoUnits.isNotEmpty()) {
            val pickedUnit = prioritizedRepoUnits[stintOffset % prioritizedRepoUnits.size]
            val uSnap = pillarMap[pickedUnit.unit.mappedPillar] ?: topVital
            val bookBadge = pickedUnit.unit.bookBridge?.shortBookBadge ?: "C1 Struktur"

            val unitEvpm = (
                14.5f +
                    (if (pickedUnit.isParetoVital20) 5.2f else 1.0f) +
                    ((4 - pickedUnit.completedStepsCount) * 1.4f) +
                    (uSnap.roiScore * 0.11f)
                ) * cognitiveFitMultiplier(DailyBeatType.TEXTBOOK_VISUAL_BLUEPRINT, cognitiveLoad)

            val unitAdaptation = BeatAdaptationSpec(
                adaptationBadge = "AUTO-MOD: ${selectedTrack.shortLabel.uppercase()}-TRANSFER",
                modifiedConstraintDe = "Struktur-Modifikation: Übertragen Sie das Vorher→Nachher-Muster mental auf ${
                    when (selectedTrack) {
                        ContextTrack.ENGINEERING -> "ein technisches AMG/911-Abstimmungsmeeting"
                        ContextTrack.COACHING -> "eine präzise BVB/F-Jugend-Spielanalyse"
                        ContextTrack.SOCIAL -> "eine spontane Diskussion im Freundeskreis in Esslingen"
                        ContextTrack.GENERAL -> "eine souveräne C1-Alltagsverhandlung"
                    }
                } und lösen Sie danach den Satzklammer-Snap fehlerfrei.",
                targetLatencySeconds = if (cognitiveLoad == CognitiveLoadLevel.FATIGUED) 12 else 8,
                recommendedAudioRate = 0.98f,
                scaffoldingLevel = if (cognitiveLoad == CognitiveLoadLevel.FATIGUED) 2 else 1
            )

            candidates.add(
                DailySessionBeat(
                    id = "beat_unit_${pickedUnit.unit.id}_$stintOffset",
                    sectorNumber = 6,
                    beatType = DailyBeatType.TEXTBOOK_VISUAL_BLUEPRINT,
                    title = pickedUnit.unit.title,
                    controllerReason = "$bookBadge (${unitEvpm.roundToInt()} EV/m) · 80/20 Satzbau-Transformation",
                    estimatedMinutes = 5,
                    targetPillar = pickedUnit.unit.mappedPillar,
                    evPerMinute = unitEvpm,
                    adaptation = unitAdaptation,
                    repoUnit = pickedUnit
                )
            )
        }

        // --- CANDIDATE G: Live Tactical Pitch or Mediation Transfer ---
        val useFootball = selectedTrack == ContextTrack.COACHING ||
            (selectedTrack != ContextTrack.ENGINEERING && stintOffset % 2 == 1 && footballList.isNotEmpty())
        if (useFootball && footballList.isNotEmpty()) {
            val fb = footballList[stintOffset % footballList.size]
            val tacEvpm = (
                13.8f +
                    (if (selectedTrack == ContextTrack.COACHING) 6.5f else 1.5f) +
                    (if (strategyMode == ControllerStrategyMode.EXAM_SPRINT) 2.5f else 0f)
                ) * cognitiveFitMultiplier(DailyBeatType.LIVE_TACTICAL_TRANSFER, cognitiveLoad)

            candidates.add(
                DailySessionBeat(
                    id = "beat_tactical_${fb.id}_$stintOffset",
                    sectorNumber = 7,
                    beatType = DailyBeatType.LIVE_TACTICAL_TRANSFER,
                    title = fb.title,
                    controllerReason = "Doppel-Register Transfer (${tacEvpm.roundToInt()} EV/m) · F-Jugend Ruf vs. Trainer-Analyse",
                    estimatedMinutes = 4,
                    targetPillar = C1Pillar.MODALPARTIKELN,
                    evPerMinute = tacEvpm,
                    adaptation = BeatAdaptationSpec(
                        adaptationBadge = "AUTO-MOD: DOPPEL-REGISTER",
                        modifiedConstraintDe = "Register-Wechsel unter Zeitdruck: Erst ultrakurzes Kinder-Kommando (max. 6 Wörter), dann präzise taktische Trainer-Begründung.",
                        targetLatencySeconds = 6,
                        recommendedAudioRate = 1.05f,
                        scaffoldingLevel = 0
                    ),
                    footballScenario = fb
                )
            )
        } else if (mediationList.isNotEmpty()) {
            val med = mediationList[stintOffset % mediationList.size]
            val medEvpm = (
                14.0f +
                    (if (selectedTrack == ContextTrack.ENGINEERING) 4.8f else 1.8f) +
                    (if (strategyMode == ControllerStrategyMode.EXAM_SPRINT) 3.2f else 0f)
                ) * cognitiveFitMultiplier(DailyBeatType.LIVE_TACTICAL_TRANSFER, cognitiveLoad)

            candidates.add(
                DailySessionBeat(
                    id = "beat_mediation_${med.id}_$stintOffset",
                    sectorNumber = 7,
                    beatType = DailyBeatType.LIVE_TACTICAL_TRANSFER,
                    title = med.title,
                    controllerReason = "Sprachmittlung (${medEvpm.roundToInt()} EV/m) · Kernaussagen adressatengerecht komprimieren",
                    estimatedMinutes = 4,
                    targetPillar = C1Pillar.KONJUNKTIV,
                    evPerMinute = medEvpm,
                    adaptation = BeatAdaptationSpec(
                        adaptationBadge = "AUTO-MOD: KONJUNKTIV-DISTANZ",
                        modifiedConstraintDe = "Präzisions-Filter: Komprimieren Sie die 3 Kernfakten ohne wörtliches Nachsprechen und nutzen Sie mindestens 1 indirekte Redewendung oder Nominalphrase.",
                        targetLatencySeconds = 9,
                        recommendedAudioRate = 1.0f,
                        scaffoldingLevel = 1
                    ),
                    mediationScenario = med
                )
            )
        }

        // --- STEP 2: Commander Filter + Decisive EVPM Selection ---
        val commanderFiltered = if (commanderOverride.commanderEnabled && commanderOverride.enabledBeatTypes.isNotEmpty()) {
            candidates.filter { it.beatType in commanderOverride.enabledBeatTypes }.ifEmpty { candidates }
        } else {
            candidates
        }

        val tunedCandidates = if (commanderOverride.commanderEnabled) {
            commanderFiltered.map { beat ->
                val pinBonus = if (activePinnedPillar != null && beat.targetPillar == activePinnedPillar) 16.0f else 0f
                val oldAdapt = beat.adaptation
                val speedBias = commanderOverride.audioSpeedBias.coerceIn(0.85f, 1.30f)
                val updatedAdapt = if (oldAdapt != null) {
                    val tunedRate = (oldAdapt.recommendedAudioRate * speedBias).coerceIn(0.85f, 1.30f)
                    val speedLabel = if (kotlin.math.abs(speedBias - 1.0f) > 0.04f) " · ${((speedBias * 100).roundToInt() / 100f)}×" else ""
                    val pinTag = if (activePinnedPillar != null && beat.targetPillar == activePinnedPillar) {
                        "CMD-LOCK: ${activePinnedPillar.shortLabel.uppercase()}$speedLabel"
                    } else {
                        oldAdapt.adaptationBadge + speedLabel
                    }
                    oldAdapt.copy(
                        adaptationBadge = pinTag,
                        recommendedAudioRate = tunedRate,
                        scaffoldingLevel = (oldAdapt.scaffoldingLevel - commanderOverride.difficultyOffset).coerceIn(0, 2)
                    )
                } else null
                beat.copy(
                    evPerMinute = beat.evPerMinute + pinBonus,
                    adaptation = updatedAdapt
                )
            }
        } else {
            commanderFiltered
        }

        val topSelected = tunedCandidates
            .sortedByDescending { it.evPerMinute }
            .take(targetSectorCount)
            .toMutableList()

        // --- STEP 3: Cognitive-Ergonomic Interleaving & Urgency Ordering ---
        val ordered = reorderForCognitiveErgonomics(
            selected = topSelected,
            cognitiveLoad = cognitiveLoad,
            hasUrgentMistakes = unresolvedMistakes.isNotEmpty()
        ).mapIndexed { idx, beat ->
            beat.copy(sectorNumber = idx + 1)
        }

        val modCount = ordered.count { it.adaptation != null }
        val topBeat = ordered.firstOrNull()
        val reasonDe = when {
            commanderOverride.commanderEnabled && activePinnedPillar != null ->
                "Commander Lock aktiv: ${activePinnedPillar.shortLabel} priorisiert (${topBeat?.evPerMinute?.roundToInt() ?: 25} EV/m)"
            commanderOverride.commanderEnabled ->
                "Commander-Mix aktiv (${ordered.size} Sektoren · Tempo ${commanderOverride.audioSpeedBias}×)"
            unresolvedMistakes.isNotEmpty() ->
                "Fehlerspeicher aktiv (${unresolvedMistakes.size}) → Reparatur auf Sektor 1 gezogen"
            cognitiveLoad == CognitiveLoadLevel.FATIGUED ->
                "Ermüdung erkannt → Akustik & Echt-Podcast vorgezogen, Schwierigkeit gedämpft"
            translationRiskDetected ->
                "Übersetzungs-Latenz erkannt → Spontan-Direktabruf (<7s) scharfgeschaltet"
            else ->
                "Profil-gesteuertes EV/min-Optimum (${topBeat?.evPerMinute?.roundToInt() ?: 19} EV/m) für ${selectedTrack.shortLabel}"
        }

        return OptimizedStintPlan(
            orderedBeats = ordered,
            decisionHeadlineDe = reasonDe,
            modificationsCount = modCount
        )
    }

    private fun cognitiveFitMultiplier(
        beatType: DailyBeatType,
        load: CognitiveLoadLevel
    ): Float = when (load) {
        CognitiveLoadLevel.FATIGUED -> when (beatType) {
            DailyBeatType.BLIND_LISTENING_SCENE -> 1.18f
            DailyBeatType.NATIVE_MEDIA_IMMERSION -> 1.16f
            DailyBeatType.SRS_VOCAB_BURST -> 1.15f
            DailyBeatType.SPONTANEOUS_SPEAKING -> 0.96f
            DailyBeatType.TEXTBOOK_VISUAL_BLUEPRINT -> 0.82f
            DailyBeatType.ADAPTIVE_PROBE_BURST -> 0.90f
            DailyBeatType.LIVE_TACTICAL_TRANSFER -> 0.88f
        }
        CognitiveLoadLevel.ELEVATED -> when (beatType) {
            DailyBeatType.BLIND_LISTENING_SCENE,
            DailyBeatType.NATIVE_MEDIA_IMMERSION,
            DailyBeatType.SRS_VOCAB_BURST -> 1.06f
            else -> 0.98f
        }
        CognitiveLoadLevel.FRESH -> when (beatType) {
            DailyBeatType.SPONTANEOUS_SPEAKING,
            DailyBeatType.LIVE_TACTICAL_TRANSFER,
            DailyBeatType.TEXTBOOK_VISUAL_BLUEPRINT -> 1.08f
            else -> 1.02f
        }
        CognitiveLoadLevel.OPTIMAL -> 1.0f
    }

    private fun reorderForCognitiveErgonomics(
        selected: List<DailySessionBeat>,
        cognitiveLoad: CognitiveLoadLevel,
        hasUrgentMistakes: Boolean
    ): List<DailySessionBeat> {
        if (selected.size <= 1) return selected

        val pool = selected.toMutableList()
        val result = mutableListOf<DailySessionBeat>()

        val anchorFirst = when {
            hasUrgentMistakes ->
                pool.firstOrNull { it.beatType == DailyBeatType.ADAPTIVE_PROBE_BURST }
            cognitiveLoad == CognitiveLoadLevel.FATIGUED ->
                pool.firstOrNull {
                    it.beatType == DailyBeatType.BLIND_LISTENING_SCENE ||
                        it.beatType == DailyBeatType.NATIVE_MEDIA_IMMERSION ||
                        it.beatType == DailyBeatType.SRS_VOCAB_BURST
                }
            else -> pool.maxByOrNull { it.evPerMinute }
        }
        if (anchorFirst != null) {
            result.add(anchorFirst)
            pool.remove(anchorFirst)
        }

        while (pool.isNotEmpty()) {
            val prev = result.lastOrNull()
            val pairedSpeaking = if (prev?.beatType == DailyBeatType.BLIND_LISTENING_SCENE) {
                pool.firstOrNull {
                    it.beatType == DailyBeatType.SPONTANEOUS_SPEAKING &&
                        it.personalizedExercise?.id == prev.personalizedExercise?.id
                }
            } else null

            val nextPick = pairedSpeaking ?: pool.maxByOrNull { cand ->
                val sameCategoryPenalty = if (prev != null && isHeavyAnalytical(prev.beatType) && isHeavyAnalytical(cand.beatType)) {
                    0.80f
                } else 1.0f
                val prematureSpeakPenalty = if (
                    cand.beatType == DailyBeatType.SPONTANEOUS_SPEAKING &&
                    pool.any {
                        it.beatType == DailyBeatType.BLIND_LISTENING_SCENE &&
                            it.personalizedExercise?.id == cand.personalizedExercise?.id
                    }
                ) 0.55f else 1.0f

                cand.evPerMinute * sameCategoryPenalty * prematureSpeakPenalty
            }!!

            result.add(nextPick)
            pool.remove(nextPick)
        }

        return result
    }

    private fun isHeavyAnalytical(type: DailyBeatType): Boolean =
        type == DailyBeatType.ADAPTIVE_PROBE_BURST || type == DailyBeatType.TEXTBOOK_VISUAL_BLUEPRINT

    private fun craftProbeAdaptation(
        mistakesCount: Int,
        topVital: PillarDiagnosticSnapshot,
        cognitiveLoad: CognitiveLoadLevel,
        translationRisk: Boolean
    ): BeatAdaptationSpec {
        val targetSec = when {
            cognitiveLoad == CognitiveLoadLevel.FATIGUED -> 11
            topVital.targetDifficultyTier >= 3 -> 6
            else -> 8
        }
        val badge = when {
            mistakesCount > 0 -> "AUTO-MOD: MINIMALPAAR-REPARATUR"
            translationRisk -> "AUTO-MOD: <${targetSec}s AUTOMATIK-DRUCK"
            cognitiveLoad == CognitiveLoadLevel.FATIGUED -> "AUTO-MOD: FOKUS-ENTLASTUNG"
            else -> "AUTO-MOD: TIER-L${topVital.targetDifficultyTier} PRÄZISION"
        }
        val note = when {
            mistakesCount > 0 ->
                "Controller hat Ihre letzten Fehlgriffe als Kontrast-Impulse geladen. Entscheiden Sie präzise nach Struktur-Signalwort."
            translationRisk ->
                "Zielzeit <${targetSec}s pro Impuls: Erkennen Sie das deutsche Strukturmuster direkt, ohne innerlich ins Englische zu übersetzen."
            cognitiveLoad == CognitiveLoadLevel.FATIGUED ->
                "Schwierigkeit um 1 Stufe stabilisiert für saubere Trefferquote trotz hoher kognitiver Last."
            else ->
                "Kalibriert auf 72%-Erfolgszone (${topVital.pillar.shortLabel} · ±${topVital.confidenceSigma.roundToInt()}% Sigma)."
        }
        return BeatAdaptationSpec(
            adaptationBadge = badge,
            modifiedConstraintDe = note,
            targetLatencySeconds = targetSec,
            recommendedAudioRate = 1.0f,
            scaffoldingLevel = if (cognitiveLoad == CognitiveLoadLevel.FATIGUED) 2 else if (topVital.targetDifficultyTier >= 3) 0 else 1
        )
    }

    private fun craftListeningAdaptation(
        pillarMastery: Float,
        cognitiveLoad: CognitiveLoadLevel,
        reductionCount: Int
    ): BeatAdaptationSpec {
        return when {
            cognitiveLoad == CognitiveLoadLevel.FATIGUED || pillarMastery < 52f -> BeatAdaptationSpec(
                adaptationBadge = "AUTO-MOD: 0.94× → 1.05× STUDIO-AUDIO",
                modifiedConstraintDe = "Gemini Mehrsprecher-Audio: Hören Sie zuerst klar strukturiert auf Sprecherabsicht, danach im echten Umgangstempo ($reductionCount Reduktionen).",
                targetLatencySeconds = 10,
                recommendedAudioRate = 0.94f,
                scaffoldingLevel = 2
            )
            pillarMastery >= 72f -> BeatAdaptationSpec(
                adaptationBadge = "AUTO-MOD: 1.15× BLIND-SCHNELLSPRECH",
                modifiedConstraintDe = "C1+ Echtzeit-Modus: Transkript gesperrt bei erhöhtem Sprechtempo — filtern Sie Subtext und Modalpartikeln beim ersten Hören.",
                targetLatencySeconds = 6,
                recommendedAudioRate = 1.15f,
                scaffoldingLevel = 0
            )
            else -> BeatAdaptationSpec(
                adaptationBadge = "AUTO-MOD: 1.05× STUDIO-DIALOG",
                modifiedConstraintDe = "Blind-First Fokus: Achten Sie gezielt auf verschliffene Endungen ($reductionCount Kurzformen) vor dem Öffnen der Sprechblasen.",
                targetLatencySeconds = 8,
                recommendedAudioRate = 1.05f,
                scaffoldingLevel = 1
            )
        }
    }

    private fun craftSpeakingAdaptation(
        selectedTrack: ContextTrack,
        dominantError: ErrorArchetype?,
        translationRisk: Boolean,
        cognitiveLoad: CognitiveLoadLevel
    ): BeatAdaptationSpec {
        val trackFlavor = when (selectedTrack) {
            ContextTrack.ENGINEERING -> "Mercedes-AMG / 911 Ingenieurs-Präzision"
            ContextTrack.COACHING -> "BVB / SV 1845 Trainer-Klarheit"
            ContextTrack.SOCIAL -> "natürlicher Esslinger Umgangston"
            ContextTrack.GENERAL -> "souveränes Alltags-C1"
        }
        val grammarConstraint = when {
            translationRisk ->
                "Sofort-Einstieg ohne Übersetzen: Beginnen Sie mit einem natürlichen Gesprächsanker (z. B. „Ich schätze mal …“ / „Unterm Strich …“) + $trackFlavor."
            dominantError == ErrorArchetype.WORD_ORDER_KLAMMER ->
                "Live-Vorgabe (Satzklammer): Nutzen Sie 1 konzessive Struktur („Zwar …, allerdings …“) mit stabiler Verb-Endstellung ($trackFlavor)."
            dominantError == ErrorArchetype.CASE_GOVERNMENT ->
                "Live-Vorgabe (Kasus): Bauen Sie 1 präzise Genitiv-/Präpositionalverbindung („angesichts“, „infolge“) ein ($trackFlavor)."
            dominantError == ErrorArchetype.MOOD_MODALITY ->
                "Live-Vorgabe (Modalität): Abtönung durch mindestens 1 Modalpartikel („doch“, „halt“, „eigentlich“, „wohl“) für $trackFlavor."
            else ->
                "Live-Vorgabe (Register): Reagieren Sie auf den Gegenwind ohne Lehnübersetzung — erst Verständnis signalisieren, dann präzise nachsteuern ($trackFlavor)."
        }
        return BeatAdaptationSpec(
            adaptationBadge = if (translationRisk) "AUTO-MOD: ANTI-ÜBERSETZUNG" else "AUTO-MOD: LIVE-VORGABE",
            modifiedConstraintDe = grammarConstraint,
            targetLatencySeconds = if (cognitiveLoad == CognitiveLoadLevel.FATIGUED) 10 else 7,
            recommendedAudioRate = 1.05f,
            scaffoldingLevel = if (cognitiveLoad == CognitiveLoadLevel.FATIGUED) 2 else 0
        )
    }
}
