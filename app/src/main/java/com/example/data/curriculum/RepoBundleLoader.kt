package com.example.data.curriculum

import android.content.Context
import com.example.domain.model.BookBridgeInfo
import com.example.domain.model.C1Pillar
import com.example.domain.model.C1QuizItem
import com.example.domain.model.C1VocabCard
import com.example.domain.model.ClauseSnapPuzzle
import com.example.domain.model.ContextTrack
import com.example.domain.model.ContextTransformPair
import com.example.domain.model.ConversationScenarioItem
import com.example.domain.model.ConversationTurnItem
import com.example.domain.model.EmbeddedMicroCheck
import com.example.domain.model.ErrorArchetype
import com.example.domain.model.FootballTacticalScenario
import com.example.domain.model.ListeningEpisodeInfo
import com.example.domain.model.MediationScenarioItem
import com.example.domain.model.MentalDiagramArchetype
import com.example.domain.model.RepoCurriculumUnit
import com.example.domain.model.RepoUnitContent
import com.example.domain.model.SentenceSurgeryItem
import com.example.domain.model.UnexpectedInterruptionItem
import com.example.domain.model.UnitVisualDeck
import com.example.domain.model.VisualDiagramType
import com.example.domain.model.VisualStepCard
import java.io.File
import org.json.JSONArray
import org.json.JSONObject

data class LoadedRepoBundle(
    val stages: List<String>,
    val units: List<RepoCurriculumUnit>,
    val conversations: List<ConversationScenarioItem>,
    val mediations: List<MediationScenarioItem>,
    val footballScenarios: List<FootballTacticalScenario>,
    val repoVocabCards: List<C1VocabCard>,
    val repoQuizItems: List<C1QuizItem>
)

object RepoBundleLoader {
    @Volatile
    private var cached: LoadedRepoBundle? = null

    fun load(context: Context? = null): LoadedRepoBundle {
        cached?.let { return it }
        synchronized(this) {
            cached?.let { return it }
            val rawJson = readBundleJson(context)
            val parsed = parseBundle(JSONObject(rawJson))
            cached = parsed
            return parsed
        }
    }

    private fun readBundleJson(context: Context?): String {
        if (context != null) {
            runCatching {
                return context.assets.open("c1_repo_bundle.json").bufferedReader().use { it.readText() }
            }
        }
        val candidates = listOf(
            File("app/src/main/assets/c1_repo_bundle.json"),
            File("src/main/assets/c1_repo_bundle.json"),
            File("/app/applet/app/src/main/assets/c1_repo_bundle.json")
        )
        for (file in candidates) {
            if (file.exists()) {
                return file.readText()
            }
        }
        error("c1_repo_bundle.json not found in assets or project path")
    }

    private fun parseBundle(root: JSONObject): LoadedRepoBundle {
        val stages = root.getJSONArray("stages").toStringList()

        // 1. Book Bridges (*Aspekte neu C1*, *Sicher! C1*, *Goethe/Telc C1*)
        val bookBridgesByUnit = mutableMapOf<String, BookBridgeInfo>()
        val bbArr = root.optJSONArray("lessonBookBridges") ?: JSONArray()
        for (i in 0 until bbArr.length()) {
            val obj = bbArr.getJSONObject(i)
            val unitId = obj.getString("unitId")
            bookBridgesByUnit[unitId] = BookBridgeInfo(
                unitId = unitId,
                bookReference = obj.optString("bookReference"),
                audioFilenameHint = obj.optString("audioFilenameHint"),
                originalPrompt = obj.optString("originalPrompt")
            )
        }

        // 2. Podcast / Broadcast Listening Episodes
        val episodesByUnit = mutableMapOf<String, ListeningEpisodeInfo>()
        val epArr = root.optJSONArray("lessonListeningEpisodes") ?: JSONArray()
        for (i in 0 until epArr.length()) {
            val obj = epArr.getJSONObject(i)
            val unitId = obj.getString("unitId")
            episodesByUnit[unitId] = ListeningEpisodeInfo(
                unitId = unitId,
                title = obj.optString("title"),
                url = obj.optString("url"),
                task = obj.optString("task")
            )
        }

        // 3. Imprint Visual Lesson Decks (all 28 units)
        val visualDecksByUnit = mutableMapOf<String, UnitVisualDeck>()
        val decksObj = root.optJSONObject("visualLessonDecks")
        if (decksObj != null) {
            val keys = decksObj.keys()
            while (keys.hasNext()) {
                val uId = keys.next()
                val d = decksObj.getJSONObject(uId)
                val stepsArr = d.getJSONArray("steps")
                val steps = (0 until stepsArr.length()).map { idx ->
                    val s = stepsArr.getJSONObject(idx)
                    VisualStepCard(
                        tag = s.optString("tag"),
                        title = s.optString("title"),
                        caption = s.optString("caption"),
                        nodes = s.getJSONArray("nodes").toStringList(),
                        highlightIndex = s.optInt("highlightIndex", 0)
                    )
                }
                val ctxObj = d.getJSONObject("contextTransforms")
                val transforms = ContextTrack.entries.associateWith { track ->
                    val tr = ctxObj.optJSONObject(track.id) ?: ctxObj.getJSONObject("GENERAL")
                    ContextTransformPair(
                        sceneLabel = tr.optString("sceneLabel"),
                        avoidLabel = tr.optString("avoidLabel"),
                        avoidPhrase = tr.optString("avoidPhrase"),
                        c1Phrase = tr.optString("c1Phrase"),
                        grammarHint = tr.optString("grammarHint")
                    )
                }
                val mc = d.getJSONObject("microCheck")
                val cp = d.getJSONObject("clausePuzzle")
                val sg = d.getJSONObject("surgery")
                visualDecksByUnit[uId] = UnitVisualDeck(
                    unitId = uId,
                    modelTitle = d.optString("modelTitle"),
                    diagramType = MentalDiagramArchetype.fromName(d.optString("diagramType")),
                    visualHook = d.optString("visualHook"),
                    steps = steps,
                    contextTransforms = transforms,
                    microCheck = EmbeddedMicroCheck(
                        prompt = mc.optString("prompt"),
                        options = mc.getJSONArray("options").toStringList(),
                        correctIndex = mc.optInt("correctIndex", 0),
                        explanation = mc.optString("explanation")
                    ),
                    clausePuzzle = ClauseSnapPuzzle(
                        instruction = cp.optString("instruction"),
                        tiles = cp.getJSONArray("tiles").toStringList(),
                        targetOrder = cp.getJSONArray("targetOrder").toStringList(),
                        syntaxRule = cp.optString("syntaxRule")
                    ),
                    surgery = SentenceSurgeryItem(
                        label = sg.optString("label"),
                        before = sg.optString("before"),
                        diagnosis = sg.optString("diagnosis"),
                        after = sg.optString("after"),
                        steps = sg.getJSONArray("steps").toStringList()
                    )
                )
            }
        }

        // 4. All 28 Communicative Curriculum Units across Stages 0..6
        val unitsArr = root.getJSONArray("curriculumUnits")
        val units = mutableListOf<RepoCurriculumUnit>()
        val unitVocabCards = mutableListOf<C1VocabCard>()
        val unitQuizItems = mutableListOf<C1QuizItem>()

        for (i in 0 until unitsArr.length()) {
            val u = unitsArr.getJSONObject(i)
            val id = u.getString("id")
            val stage = u.optInt("stage", 0)
            val title = u.getString("title")
            val stageTitle = stages.getOrElse(stage) { "Stufe $stage" }
            val mappedPillar = mapUnitToPillar(id, stage)

            val ctxJson = u.getJSONObject("contexts")
            val contextsMap = ContextTrack.entries.associateWith { tr ->
                ctxJson.optString(tr.id, "")
            }

            val cObj = u.optJSONObject("content") ?: continue
            val content = RepoUnitContent(
                reading = cObj.optString("reading"),
                listening = cObj.optString("listening"),
                expressions = cObj.optJSONArray("expressions").toStringList(),
                grammar = cObj.optString("grammar"),
                question = cObj.optString("question"),
                options = cObj.optJSONArray("options").toStringList(),
                correctOption = cObj.optInt("correctOption", 0),
                explanation = cObj.optString("explanation"),
                controlledPrompt = cObj.optString("controlledPrompt"),
                controlledModel = cObj.optString("controlledModel"),
                speaking = cObj.optString("speaking"),
                interaction = cObj.optString("interaction"),
                writing = cObj.optString("writing"),
                transfer = cObj.optString("transfer"),
                feedback = cObj.optString("feedback")
            )

            val deck = visualDecksByUnit[id]
            val bookBridge = bookBridgesByUnit[id]
            val episode = episodesByUnit[id]

            val unit = RepoCurriculumUnit(
                id = id,
                stage = stage,
                stageTitle = stageTitle,
                title = title,
                objectives = u.optJSONArray("objectives").toStringList(),
                contexts = contextsMap,
                prerequisites = u.optJSONArray("prerequisites").toStringList(),
                mappedPillar = mappedPillar,
                content = content,
                visualDeck = deck,
                bookBridge = bookBridge,
                listeningEpisode = episode
            )
            units.add(unit)

            // Convert each unit's core comprehension/situation question into an adaptive CAT probe
            if (content.options.size >= 4) {
                unitQuizItems.add(
                    C1QuizItem(
                        id = "uquiz_$id",
                        pillar = mappedPillar,
                        difficulty = (stage / 2 + 1).coerceIn(1, 4),
                        archetype = mapStageToArchetype(stage),
                        promptDe = content.question,
                        contextSnippet = "[$title${bookBridge?.let { " · ${it.shortBookBadge}" } ?: ""}] ${contextsMap[ContextTrack.GENERAL]}",
                        options = content.options.take(4),
                        correctIndex = content.correctOption.coerceIn(0, 3),
                        visualDiagram = mapPillarDiagram(mappedPillar),
                        explanationDe = content.explanation,
                        explanationEn = content.grammar,
                        trapNote = content.feedback
                    )
                )
            }

            // Convert top C1 Redemittel from each unit into FSRS Vocab Cards
            content.expressions.take(2).forEachIndexed { expIdx, expr ->
                val generalTransform = deck?.contextTransforms?.get(ContextTrack.GENERAL)
                unitVocabCards.add(
                    C1VocabCard(
                        id = "uvoc_${id}_$expIdx",
                        pillar = mappedPillar,
                        expression = expr,
                        grammarTag = bookBridge?.shortBookBadge ?: "Stufe $stage Redemittel",
                        register = title,
                        meaningEn = unit.objectives.firstOrNull() ?: stageTitle,
                        exampleDe = generalTransform?.c1Phrase ?: content.controlledModel,
                        trapWarning = generalTransform?.let { "Vermeiden (${it.avoidLabel}): „${it.avoidPhrase}“" } ?: content.grammar,
                        visualHint = deck?.visualHook ?: stageTitle,
                        diagramType = mapPillarDiagram(mappedPillar)
                    )
                )
            }
        }

        // 5. Active Vocabulary from learningData.ts + Football U15 Coach Terms
        val repoVocab = mutableListOf<C1VocabCard>()
        val vocArr = root.optJSONArray("activeVocabulary") ?: JSONArray()
        for (i in 0 until vocArr.length()) {
            val v = vocArr.getJSONObject(i)
            repoVocab.add(
                C1VocabCard(
                    id = v.getString("id"),
                    pillar = C1Pillar.NOMEN_VERB,
                    expression = v.optString("expression"),
                    grammarTag = v.optString("type"),
                    register = v.optString("domain"),
                    meaningEn = v.optString("meaningDe"),
                    exampleDe = v.optString("naturalExample"),
                    trapWarning = "Statt B2/umgangssprachlich: „${v.optString("informalOrClunkyEquivalent")}“ · Einsatz: ${v.optString("requiredInContext")}",
                    visualHint = v.optString("requiredInContext"),
                    diagramType = VisualDiagramType.COLLOCATION_NETWORK
                )
            )
        }

        val fcObj = root.optJSONObject("footballCoachU15Data")
        val footballScenarios = mutableListOf<FootballTacticalScenario>()
        if (fcObj != null) {
            val termsArr = fcObj.optJSONArray("terms") ?: JSONArray()
            for (i in 0 until termsArr.length()) {
                val t = termsArr.getJSONObject(i)
                repoVocab.add(
                    C1VocabCard(
                        id = t.getString("id"),
                        pillar = C1Pillar.MODALPARTIKELN,
                        expression = t.optString("term"),
                        grammarTag = "Fußball U15 · ${t.optString("category")}",
                        register = "Platzkommando & Taktik",
                        meaningEn = t.optString("tacticalMeaning"),
                        exampleDe = t.optString("pitchCommand"),
                        trapWarning = "Nicht unpräzise rufen: „${t.optString("avoidInstead")}“",
                        visualHint = "U15 Coaching-Ruf auf dem Platz",
                        diagramType = VisualDiagramType.TEKAMOLO_PIPELINE
                    )
                )
            }
            val scenArr = fcObj.optJSONArray("scenarios") ?: JSONArray()
            for (i in 0 until scenArr.length()) {
                val sc = scenArr.getJSONObject(i)
                footballScenarios.add(
                    FootballTacticalScenario(
                        id = sc.getString("id"),
                        title = sc.optString("title"),
                        matchPhase = sc.optString("matchPhase"),
                        situationDescription = sc.optString("situationDescription"),
                        options = sc.getJSONArray("options").toStringList(),
                        correctIndex = sc.optInt("correctIndex", 0),
                        coachingExplanation = sc.optString("coachingExplanation"),
                        keyCommandToShout = sc.optString("keyCommandToShout")
                    )
                )
            }
        }

        // 6. Conversation Simulator Scenarios (with Unexpected Interruptions)
        val conversations = mutableListOf<ConversationScenarioItem>()
        val convArr = root.optJSONArray("conversations") ?: JSONArray()
        for (i in 0 until convArr.length()) {
            val c = convArr.getJSONObject(i)
            val turnsArr = c.optJSONArray("turns") ?: JSONArray()
            val turns = (0 until turnsArr.length()).map { idx ->
                val tr = turnsArr.getJSONObject(idx)
                ConversationTurnItem(
                    partnerSpeech = tr.optString("partnerSpeech"),
                    expectedUserRolePrompt = tr.optString("expectedUserRolePrompt"),
                    suggestedC1Connectors = tr.optJSONArray("suggestedC1Connectors").toStringList(),
                    targetRegister = tr.optString("targetRegister")
                )
            }
            val intrObj = c.optJSONObject("unexpectedInterruption")
            val intr = intrObj?.let {
                UnexpectedInterruptionItem(
                    triggerAfterSecond = it.optInt("triggerAfterSecond", 5),
                    partnerInterruptSpeech = it.optString("partnerInterruptSpeech"),
                    reactionObjective = it.optString("reactionObjective")
                )
            }
            conversations.add(
                ConversationScenarioItem(
                    id = c.getString("id"),
                    title = c.optString("title"),
                    rolePartner = c.optString("rolePartner"),
                    environment = c.optString("environment"),
                    openingStatement = c.optString("openingStatement"),
                    turns = turns,
                    unexpectedInterruption = intr,
                    evaluationFocus = c.optString("evaluationFocus")
                )
            )
        }

        // 7. Mediation Scenarios
        val mediations = mutableListOf<MediationScenarioItem>()
        val medArr = root.optJSONArray("mediationTasks") ?: JSONArray()
        for (i in 0 until medArr.length()) {
            val m = medArr.getJSONObject(i)
            mediations.add(
                MediationScenarioItem(
                    id = m.getString("id"),
                    title = m.optString("title"),
                    mediationType = m.optString("mediationType"),
                    sourceMaterial = m.optString("sourceMaterial"),
                    recipientContext = m.optString("recipientContext"),
                    criticalPitfallsToAvoid = m.optJSONArray("criticalPitfallsToAvoid").toStringList(),
                    targetReformulationModel = m.optString("targetReformulationModel")
                )
            )
        }

        // 8. Repo Diagnostic Tasks (Full + Grammar/Vocab)
        val diagQuiz = mutableListOf<C1QuizItem>()
        val allDiagArrays = listOf(
            root.optJSONArray("diagnosticTasksFull"),
            root.optJSONArray("diagnosticTasksGrammarVocab")
        )
        allDiagArrays.filterNotNull().forEach { arr ->
            for (i in 0 until arr.length()) {
                val d = arr.getJSONObject(i)
                val opts = d.optJSONArray("multipleChoiceOptions").toStringList()
                if (opts.size >= 4) {
                    val skillCat = d.optString("skillCategory")
                    val pillar = mapSkillCategoryToPillar(skillCat)
                    val spoken = d.optString("spokenAudioText").takeIf { it.isNotBlank() && it != "null" }
                    val snippet = buildString {
                        append(d.optString("contextDe"))
                        if (spoken != null) {
                            append("\n„")
                            append(spoken)
                            append("“")
                        }
                    }
                    diagQuiz.add(
                        C1QuizItem(
                            id = "repo_${d.getString("id")}",
                            pillar = pillar,
                            difficulty = if (skillCat == "GRAMMATIK" || skillCat == "MEDIATION") 3 else 2,
                            archetype = mapSkillCategoryToArchetype(skillCat),
                            promptDe = d.optString("promptText"),
                            contextSnippet = snippet,
                            options = opts.take(4),
                            correctIndex = d.optInt("correctOptionIndex", 0).coerceIn(0, 3),
                            visualDiagram = mapPillarDiagram(pillar),
                            explanationDe = d.optString("analysisRationaleDe"),
                            explanationEn = d.optString("headlineDe"),
                            trapNote = d.optString("dialectNote").takeIf { it.isNotBlank() && it != "null" }
                                ?: "Ziel-Antwortlatenz: < ${d.optInt("maxLatencyBenchmarkSeconds", 4)}s"
                        )
                    )
                }
            }
        }

        return LoadedRepoBundle(
            stages = stages,
            units = units,
            conversations = conversations,
            mediations = mediations,
            footballScenarios = footballScenarios,
            repoVocabCards = repoVocab + unitVocabCards,
            repoQuizItems = diagQuiz + unitQuizItems
        )
    }

    private fun mapUnitToPillar(unitId: String, stage: Int): C1Pillar = when {
        unitId.contains("register") || unitId.contains("data") || unitId.contains("revise") -> C1Pillar.NOMINALISIERUNG
        unitId.contains("mediate") || unitId.contains("uncertainty") || unitId.contains("summarise") -> C1Pillar.KONJUNKTIV
        unitId.contains("explain") || unitId.contains("present") || unitId.contains("retrieve") -> C1Pillar.SATZGEFUEGE
        unitId.contains("agree") || unitId.contains("negotiate") || unitId.contains("specialist") -> C1Pillar.NOMEN_VERB
        unitId.contains("repair") || unitId.contains("deescalate") || unitId.contains("nuance") || unitId.contains("spontaneous") -> C1Pillar.MODALPARTIKELN
        stage >= 4 || unitId.contains("argument") || unitId.contains("justify") || unitId.contains("disagree") -> C1Pillar.RHETORIK
        else -> C1Pillar.entries[stage % C1Pillar.entries.size]
    }

    private fun mapStageToArchetype(stage: Int): ErrorArchetype = when (stage) {
        0, 1 -> ErrorArchetype.WORD_ORDER_KLAMMER
        2 -> ErrorArchetype.CASE_GOVERNMENT
        3 -> ErrorArchetype.REGISTER_COLLOCATION
        4 -> ErrorArchetype.MOOD_MODALITY
        else -> ErrorArchetype.PARTICIPLE_AGREEMENT
    }

    private fun mapSkillCategoryToPillar(skill: String): C1Pillar = when (skill) {
        "GRAMMATIK" -> C1Pillar.NOMINALISIERUNG
        "MEDIATION" -> C1Pillar.KONJUNKTIV
        "LESEVERSTEHEN", "HOERVERSTEHEN" -> C1Pillar.SATZGEFUEGE
        "WORTSCHATZ" -> C1Pillar.NOMEN_VERB
        "INTERAKTION", "SPONTANES_SPRECHEN", "AUSSPRACHE_PROSODIE" -> C1Pillar.MODALPARTIKELN
        else -> C1Pillar.RHETORIK
    }

    private fun mapSkillCategoryToArchetype(skill: String): ErrorArchetype = when (skill) {
        "GRAMMATIK" -> ErrorArchetype.CASE_GOVERNMENT
        "WORTSCHATZ" -> ErrorArchetype.REGISTER_COLLOCATION
        "MEDIATION" -> ErrorArchetype.MOOD_MODALITY
        "INTERAKTION", "SPONTANES_SPRECHEN" -> ErrorArchetype.WORD_ORDER_KLAMMER
        else -> ErrorArchetype.PARTICIPLE_AGREEMENT
    }

    private fun mapPillarDiagram(pillar: C1Pillar): VisualDiagramType = when (pillar) {
        C1Pillar.NOMINALISIERUNG -> VisualDiagramType.NOMINAL_TRANSFORM_FLOW
        C1Pillar.KONJUNKTIV -> VisualDiagramType.KONJUNKTIV_SPECTRUM
        C1Pillar.SATZGEFUEGE -> VisualDiagramType.SENTENCE_BRACKET_ARCH
        C1Pillar.NOMEN_VERB -> VisualDiagramType.COLLOCATION_NETWORK
        C1Pillar.MODALPARTIKELN -> VisualDiagramType.MODAL_PARTICLE_MATRIX
        C1Pillar.RHETORIK -> VisualDiagramType.ARGUMENT_PYRAMID
    }

    private fun JSONArray?.toStringList(): List<String> {
        if (this == null) return emptyList()
        return (0 until length()).map { optString(it) }
    }
}
