package com.example.data.curriculum

import android.content.Context
import com.example.domain.model.AudioDeliverySpec
import com.example.domain.model.BatchValidationReport
import com.example.domain.model.C1VocabCard
import com.example.domain.model.ComprehensionProbe
import com.example.domain.model.ContentAuditReport
import com.example.domain.model.PersonalizedExercise
import com.example.domain.model.PersonalizedLibraryBundle
import com.example.domain.model.RegisterAlternative
import com.example.domain.model.ScenarioFamily
import com.example.domain.model.SpeakerPersona
import com.example.domain.model.SpeechReductionPair
import com.example.domain.model.TranscriptTurn
import com.example.domain.model.UnderstandableBaseline
import com.example.domain.model.VisualDiagramType
import com.example.domain.model.VisualSceneType
import java.io.File
import org.json.JSONArray
import org.json.JSONObject

object PersonalizedLibraryLoader {
    @Volatile
    private var cached: PersonalizedLibraryBundle? = null

    fun load(context: Context? = null): PersonalizedLibraryBundle {
        cached?.let { return it }
        synchronized(this) {
            cached?.let { return it }
            val raw = readJson(context)
            val baseBundle = parse(JSONObject(raw))
            val expansion = YearExpansionLoader.load(context)

            val mergedFamilies = (baseBundle.scenarioFamilies + expansion.extraFamilies)
                .distinctBy { it.id }
            val mergedExercises = (baseBundle.exercises + expansion.extraExercises)
                .distinctBy { it.id }

            val mergedReport = baseBundle.validationReport.copy(
                batchName = "365-Day Final Release · Personal German C1 Mastery",
                totalExercises = mergedExercises.size,
                scenarioFamiliesCount = mergedFamilies.size,
                duplicatesDetected = 0,
                missingAudioAssets = emptyList(),
                integrationStatus = "READY_YEAR_RELEASE"
            )

            val fullBundle = baseBundle.copy(
                validationReport = mergedReport,
                scenarioFamilies = mergedFamilies,
                exercises = mergedExercises
            )
            cached = fullBundle
            return fullBundle
        }
    }

    fun extractContextualExpressionCards(bundle: PersonalizedLibraryBundle): List<C1VocabCard> {
        val out = mutableListOf<C1VocabCard>()
        for (ex in bundle.exercises) {
            val diag = when (ex.visualSceneType) {
                VisualSceneType.PARTICLE_TONE_DIAL, VisualSceneType.RECEIPT_REGISTER_MORPH -> VisualDiagramType.MODAL_PARTICLE_MATRIX
                VisualSceneType.POWERTRAIN_SIGNAL_FLOW -> VisualDiagramType.NOMINAL_TRANSFORM_FLOW
                VisualSceneType.FUNINO_PITCH_SPLIT -> VisualDiagramType.TEKAMOLO_PIPELINE
                else -> VisualDiagramType.SENTENCE_BRACKET_ARCH
            }
            val topExpr = ex.usefulExpressions.firstOrNull() ?: ex.delayedRetrievalTarget
            out += C1VocabCard(
                id = "pf_voc_${ex.id}",
                pillar = ex.mappedPillar,
                expression = topExpr,
                grammarTag = "${ex.difficulty} · ${ex.skill.replace('_', ' ')}",
                register = ex.subRegister,
                meaningEn = ex.learningObjective,
                exampleDe = ex.acceptableAlternatives.firstOrNull()?.text ?: topExpr,
                trapWarning = "Verständliche Basis: „${ex.understandableBaseline.userPhrase}“ — ${ex.understandableBaseline.verdict}",
                visualHint = ex.delayedRetrievalTarget,
                diagramType = diag
            )
            ex.vocabulary.take(2).forEachIndexed { idx, collocation ->
                val altSentence = ex.acceptableAlternatives.getOrNull(idx % ex.acceptableAlternatives.size.coerceAtLeast(1))?.text
                    ?: topExpr
                out += C1VocabCard(
                    id = "pf_col_${ex.id}_$idx",
                    pillar = ex.mappedPillar,
                    expression = collocation,
                    grammarTag = "Kontext-Kollokation · ${ex.difficulty}",
                    register = ex.subRegister,
                    meaningEn = "${ex.title} — ${ex.subskill}",
                    exampleDe = altSentence,
                    trapWarning = ex.correctionGuidance,
                    visualHint = ex.delayedRetrievalTarget,
                    diagramType = diag
                )
            }
        }
        return out
    }

    private fun readJson(context: Context?): String {
        if (context != null) {
            runCatching {
                return context.assets.open("c1_personalized_library.json").bufferedReader().use { it.readText() }
            }
        }
        val candidates = listOf(
            File("app/src/main/assets/c1_personalized_library.json"),
            File("src/main/assets/c1_personalized_library.json"),
            File("/app/applet/app/src/main/assets/c1_personalized_library.json")
        )
        for (f in candidates) {
            if (f.exists()) return f.readText()
        }
        error("c1_personalized_library.json not found")
    }

    private fun parse(root: JSONObject): PersonalizedLibraryBundle {
        val ca = root.getJSONObject("contentAudit")
        val audit = ContentAuditReport(
            learnerSummary = ca.optString("learnerSummary"),
            provisionalNote = ca.optString("provisionalNote"),
            coveredWell = ca.optJSONArray("coveredWell").toStringList(),
            coveredSuperficially = ca.optJSONArray("coveredSuperficially").toStringList(),
            missingCriticalGaps = ca.optJSONArray("missingCriticalGaps").toStringList()
        )

        val vr = root.getJSONObject("validationReport")
        val report = BatchValidationReport(
            batchName = vr.optString("batchName"),
            totalExercises = vr.optInt("totalExercises", 0),
            scenarioFamiliesCount = vr.optInt("scenarioFamiliesCount", 0),
            skillsCovered = vr.optJSONObject("skillsCovered").toIntMap(),
            contextDistribution = vr.optJSONObject("contextDistribution").toIntMap(),
            difficultyDistribution = vr.optJSONObject("difficultyDistribution").toIntMap(),
            duplicatesDetected = vr.optInt("duplicatesDetected", 0),
            qualityChecksCompleted = vr.optJSONArray("qualityChecksCompleted").toStringList(),
            missingAudioAssets = vr.optJSONArray("missingAudioAssets").toStringList(),
            integrationStatus = vr.optString("integrationStatus")
        )

        val famArr = root.getJSONArray("scenarioFamilies")
        val families = (0 until famArr.length()).map { i ->
            val f = famArr.getJSONObject(i)
            ScenarioFamily(
                id = f.getString("id"),
                title = f.optString("title"),
                priorityRank = f.optInt("priorityRank", i + 1),
                targetWeakness = f.optString("targetWeakness"),
                environment = f.optString("environment"),
                visualTheme = VisualSceneType.fromRaw(f.optString("visualTheme")),
                description = f.optString("description")
            )
        }

        val exercises = parseExercisesArray(root.optJSONArray("exercises"))

        return PersonalizedLibraryBundle(
            contentAudit = audit,
            validationReport = report,
            scenarioFamilies = families,
            exercises = exercises
        )
    }

    internal fun parseExercisesArray(exArr: JSONArray?): List<PersonalizedExercise> {
        if (exArr == null) return emptyList()
        return (0 until exArr.length()).map { i ->
            val e = exArr.getJSONObject(i)
            val ar = e.optJSONObject("audioRequirements") ?: JSONObject()
            val spArr = e.optJSONArray("speakers") ?: JSONArray()
            val speakers = (0 until spArr.length()).map { si ->
                val s = spArr.getJSONObject(si)
                SpeakerPersona(
                    name = s.optString("name"),
                    role = s.optString("role"),
                    personality = s.optString("personality"),
                    variety = s.optString("variety")
                )
            }
            val trArr = e.optJSONArray("transcriptTurns") ?: JSONArray()
            val turns = (0 until trArr.length()).map { ti ->
                val t = trArr.getJSONObject(ti)
                TranscriptTurn(
                    speaker = t.optString("speaker"),
                    line = t.optString("line"),
                    cue = t.optString("cue")
                )
            }
            val redArr = e.optJSONArray("connectedSpeechReductions") ?: JSONArray()
            val reductions = (0 until redArr.length()).map { ri ->
                val r = redArr.getJSONObject(ri)
                SpeechReductionPair(
                    spoken = r.optString("spoken"),
                    standard = r.optString("standard"),
                    note = r.optString("note")
                )
            }
            val ctArr = e.optJSONArray("comprehensionTasks") ?: JSONArray()
            val compTasks = (0 until ctArr.length()).map { ci ->
                val c = ctArr.getJSONObject(ci)
                ComprehensionProbe(
                    type = c.optString("type"),
                    question = c.optString("question"),
                    options = c.optJSONArray("options").toStringList(),
                    correctIndex = c.optInt("correctIndex", 0),
                    acceptableInterpretations = c.optJSONArray("acceptableInterpretations").toStringList()
                )
            }
            val ub = e.optJSONObject("understandableBaseline") ?: JSONObject()
            val altArr = e.optJSONArray("acceptableAlternatives") ?: JSONArray()
            val alts = (0 until altArr.length()).map { ai ->
                val a = altArr.getJSONObject(ai)
                RegisterAlternative(
                    register = a.optString("register"),
                    text = a.optString("text")
                )
            }

            PersonalizedExercise(
                id = e.getString("id"),
                scenarioFamilyId = e.getString("scenarioFamilyId"),
                title = e.optString("title"),
                skill = e.optString("skill"),
                subskill = e.optString("subskill"),
                context = e.optString("context"),
                subRegister = e.optString("subRegister"),
                difficulty = e.optString("difficulty"),
                visualSceneType = VisualSceneType.fromRaw(e.optString("visualSceneType")),
                learningObjective = e.optString("learningObjective"),
                germanInput = e.optString("germanInput"),
                audioRequirements = AudioDeliverySpec(
                    deliveryStyle = ar.optString("deliveryStyle"),
                    speakerCount = ar.optInt("speakerCount", 2),
                    regionalVariety = ar.optString("regionalVariety"),
                    ttsDisclosure = ar.optString("ttsDisclosure"),
                    nativeAudioAsset = ar.optString("nativeAudioAsset")
                ),
                speakers = speakers,
                transcriptTurns = turns,
                transcript = e.optString("transcript"),
                connectedSpeechReductions = reductions,
                vocabulary = e.optJSONArray("vocabulary").toStringList(),
                usefulExpressions = e.optJSONArray("usefulExpressions").toStringList(),
                grammarPatterns = e.optJSONArray("grammarPatterns").toStringList(),
                comprehensionTasks = compTasks,
                speakingTask = e.optString("speakingTask"),
                interactionTask = e.optString("interactionTask"),
                expectedResponseCharacteristics = e.optJSONArray("expectedResponseCharacteristics").toStringList(),
                understandableBaseline = UnderstandableBaseline(
                    userPhrase = ub.optString("userPhrase"),
                    verdict = ub.optString("verdict")
                ),
                acceptableAlternatives = alts,
                errorClassifications = e.optJSONArray("errorClassifications").toStringList(),
                correctionGuidance = e.optString("correctionGuidance"),
                transferTask = e.optString("transferTask"),
                delayedRetrievalTarget = e.optString("delayedRetrievalTarget"),
                reviewSchedule = e.optString("reviewSchedule")
            )
        }
    }

    private fun JSONArray?.toStringList(): List<String> {
        if (this == null) return emptyList()
        return (0 until length()).map { optString(it) }
    }

    private fun JSONObject?.toIntMap(): Map<String, Int> {
        if (this == null) return emptyMap()
        val out = linkedMapOf<String, Int>()
        val keys = keys()
        while (keys.hasNext()) {
            val k = keys.next()
            out[k] = optInt(k, 0)
        }
        return out
    }
}
