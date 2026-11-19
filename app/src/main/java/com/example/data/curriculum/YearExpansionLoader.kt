package com.example.data.curriculum

import android.content.Context
import com.example.domain.model.AuditGapResolutionItem
import com.example.domain.model.C1Pillar
import com.example.domain.model.C1QuizItem
import com.example.domain.model.C1VocabCard
import com.example.domain.model.ContextTrack
import com.example.domain.model.ErrorArchetype
import com.example.domain.model.GoetheExamModuleAudit
import com.example.domain.model.MediaImmersionMission
import com.example.domain.model.PersonalizedExercise
import com.example.domain.model.ScenarioFamily
import com.example.domain.model.VisualDiagramType
import com.example.domain.model.VisualSceneType
import com.example.domain.model.YearReleaseAuditReport
import java.io.File
import org.json.JSONArray
import org.json.JSONObject

data class LoadedYearExpansion(
    val rawAuditReport: YearReleaseAuditReport,
    val extraFamilies: List<ScenarioFamily>,
    val extraExercises: List<PersonalizedExercise>,
    val mediaMissions: List<MediaImmersionMission>,
    val extraVocabCards: List<C1VocabCard>,
    val extraQuizProbes: List<C1QuizItem>
)

object YearExpansionLoader {
    @Volatile
    private var cached: LoadedYearExpansion? = null

    fun load(context: Context? = null): LoadedYearExpansion {
        cached?.let { return it }
        synchronized(this) {
            cached?.let { return it }

            val auditRoot = JSONObject(readAssetFile(context, "c1_year_media_audit.json"))
            val pack1Root = JSONObject(readAssetFile(context, "c1_year_scenarios_pack1.json"))
            val pack2Root = JSONObject(readAssetFile(context, "c1_year_scenarios_pack2.json"))
            val pack3Root = JSONObject(readAssetFile(context, "c1_year_scenarios_pack3.json"))
            val chunksRoot = JSONObject(readAssetFile(context, "c1_year_chunks_probes.json"))

            val extraFamilies = parseFamilies(auditRoot.optJSONArray("extraScenarioFamilies"))
            val extraExercises = (
                PersonalizedLibraryLoader.parseExercisesArray(pack1Root.optJSONArray("exercises")) +
                    PersonalizedLibraryLoader.parseExercisesArray(pack2Root.optJSONArray("exercises")) +
                    PersonalizedLibraryLoader.parseExercisesArray(pack3Root.optJSONArray("exercises"))
                ).distinctBy { it.id }

            val missions = (
                parseMediaMissions(auditRoot.optJSONArray("mediaImmersionMissions")) +
                    parseMediaMissions(pack2Root.optJSONArray("extraMediaMissions"))
                ).distinctBy { it.id }

            val extraVocab = parseVocabCards(chunksRoot.optJSONArray("vocabCards"))
            val explicitProbes = parseQuizProbes(chunksRoot.optJSONArray("quizProbes"))
            val scenarioDerivedProbes = deriveIrtProbesFromScenarios(extraExercises)

            val parsedAudit = parseAuditReport(auditRoot.getJSONObject("yearAuditReport"))

            val bundle = LoadedYearExpansion(
                rawAuditReport = parsedAudit,
                extraFamilies = extraFamilies,
                extraExercises = extraExercises,
                mediaMissions = missions,
                extraVocabCards = extraVocab,
                extraQuizProbes = (explicitProbes + scenarioDerivedProbes).distinctBy { it.id }
            )
            cached = bundle
            return bundle
        }
    }

    private fun readAssetFile(context: Context?, fileName: String): String {
        if (context != null) {
            runCatching {
                return context.assets.open(fileName).bufferedReader().use { it.readText() }
            }
        }
        val candidates = listOf(
            File("app/src/main/assets/$fileName"),
            File("src/main/assets/$fileName"),
            File("/app/applet/app/src/main/assets/$fileName")
        )
        for (f in candidates) {
            if (f.exists()) return f.readText()
        }
        error("$fileName not found")
    }

    private fun parseAuditReport(obj: JSONObject): YearReleaseAuditReport {
        val gapArr = obj.optJSONArray("gapResolutions") ?: JSONArray()
        val gaps = (0 until gapArr.length()).map { i ->
            val g = gapArr.getJSONObject(i)
            AuditGapResolutionItem(
                dimension = g.optString("dimension"),
                previousStatus = g.optString("previousStatus"),
                finalReleaseSolution = g.optString("finalReleaseSolution"),
                itemCountDelivered = g.optInt("itemCountDelivered", 0)
            )
        }
        val gmArr = obj.optJSONArray("goetheExamMatrix") ?: JSONArray()
        val matrix = (0 until gmArr.length()).map { i ->
            val m = gmArr.getJSONObject(i)
            GoetheExamModuleAudit(
                moduleCode = m.optString("moduleCode"),
                officialFormatDe = m.optString("officialFormatDe"),
                targetedSubskillsDe = m.optString("targetedSubskillsDe"),
                embeddedPracticeCount = m.optInt("embeddedPracticeCount", 0)
            )
        }
        return YearReleaseAuditReport(
            releaseVersion = obj.optString("releaseVersion", "2026.12 · 365-Day Release"),
            totalAnnualCapacityDays = obj.optInt("totalAnnualCapacityDays", 365),
            totalMultiSpeakerScenarios = 0,
            totalContextualVocabChunks = 0,
            totalAdaptiveIrtProbes = 0,
            totalTextbookUnits = 0,
            totalLiveMediaMissions = 0,
            audioArchitectureSummaryDe = obj.optString("audioArchitectureSummaryDe"),
            gapResolutions = gaps,
            goetheExamMatrix = matrix
        )
    }

    private fun parseFamilies(arr: JSONArray?): List<ScenarioFamily> {
        if (arr == null) return emptyList()
        return (0 until arr.length()).map { i ->
            val f = arr.getJSONObject(i)
            ScenarioFamily(
                id = f.getString("id"),
                title = f.optString("title"),
                priorityRank = f.optInt("priorityRank", i + 7),
                targetWeakness = f.optString("targetWeakness"),
                environment = f.optString("environment"),
                visualTheme = VisualSceneType.fromRaw(f.optString("visualTheme")),
                description = f.optString("description")
            )
        }
    }

    private fun parseMediaMissions(arr: JSONArray?): List<MediaImmersionMission> {
        if (arr == null) return emptyList()
        return (0 until arr.length()).map { i ->
            val m = arr.getJSONObject(i)
            val pillarRaw = m.optString("mappedPillar", "SATZGEFUEGE")
            val pillar = C1Pillar.entries.find { it.name.equals(pillarRaw, ignoreCase = true) }
                ?: C1Pillar.SATZGEFUEGE
            MediaImmersionMission(
                id = m.getString("id"),
                title = m.optString("title"),
                sourceName = m.optString("sourceName"),
                sourceType = m.optString("sourceType", "PODCAST_LIVE_STREAM"),
                podcastFeedId = m.optString("podcastFeedId", "ams_moove"),
                externalUrl = m.optString("externalUrl"),
                contextTrack = ContextTrack.fromId(m.optString("contextTrack")),
                mappedPillar = pillar,
                difficulty = m.optString("difficulty", "C1.1"),
                goetheExamPartTag = m.optString("goetheExamPartTag"),
                listeningMissionDe = m.optString("listeningMissionDe"),
                keyCollocationsToCatch = m.optJSONArray("keyCollocationsToCatch").toStringList(),
                spokenFeaturesNoteDe = m.optString("spokenFeaturesNoteDe"),
                checkQuestionDe = m.optString("checkQuestionDe"),
                checkOptions = m.optJSONArray("checkOptions").toStringList(),
                correctOptionIndex = m.optInt("correctOptionIndex", 0),
                checkExplanationDe = m.optString("checkExplanationDe"),
                activeReactionPromptDe = m.optString("activeReactionPromptDe"),
                sampleSovereignReactionDe = m.optString("sampleSovereignReactionDe")
            )
        }
    }

    private fun parseVocabCards(arr: JSONArray?): List<C1VocabCard> {
        if (arr == null) return emptyList()
        return (0 until arr.length()).map { i ->
            val v = arr.getJSONObject(i)
            val pillar = C1Pillar.entries.find { it.name.equals(v.optString("pillar"), ignoreCase = true) }
                ?: C1Pillar.NOMEN_VERB
            val diag = VisualDiagramType.entries.find { it.name.equals(v.optString("diagramType"), ignoreCase = true) }
                ?: VisualDiagramType.COLLOCATION_NETWORK
            C1VocabCard(
                id = v.getString("id"),
                pillar = pillar,
                expression = v.optString("expression"),
                grammarTag = v.optString("grammarTag"),
                register = v.optString("register"),
                meaningEn = v.optString("meaningEn"),
                exampleDe = v.optString("exampleDe"),
                trapWarning = v.optString("trapWarning"),
                visualHint = v.optString("visualHint"),
                diagramType = diag
            )
        }
    }

    private fun parseQuizProbes(arr: JSONArray?): List<C1QuizItem> {
        if (arr == null) return emptyList()
        return (0 until arr.length()).map { i ->
            val q = arr.getJSONObject(i)
            val pillar = C1Pillar.entries.find { it.name.equals(q.optString("pillar"), ignoreCase = true) }
                ?: C1Pillar.NOMEN_VERB
            val arch = ErrorArchetype.entries.find { it.name.equals(q.optString("archetype"), ignoreCase = true) }
                ?: ErrorArchetype.REGISTER_COLLOCATION
            val diag = VisualDiagramType.entries.find { it.name.equals(q.optString("visualDiagram"), ignoreCase = true) }
                ?: VisualDiagramType.COLLOCATION_NETWORK
            C1QuizItem(
                id = q.getString("id"),
                pillar = pillar,
                difficulty = q.optInt("difficulty", 3).coerceIn(1, 4),
                archetype = arch,
                promptDe = q.optString("promptDe"),
                contextSnippet = q.optString("contextSnippet"),
                options = q.optJSONArray("options").toStringList(),
                correctIndex = q.optInt("correctIndex", 0),
                visualDiagram = diag,
                explanationDe = q.optString("explanationDe"),
                explanationEn = q.optString("explanationEn"),
                trapNote = q.optString("trapNote")
            )
        }
    }

    private fun deriveIrtProbesFromScenarios(exercises: List<PersonalizedExercise>): List<C1QuizItem> {
        return exercises.mapNotNull { ex ->
            val comp = ex.comprehensionTasks.firstOrNull() ?: return@mapNotNull null
            if (comp.options.size < 3) return@mapNotNull null
            val diffTier = when {
                ex.difficulty.contains("Goethe", ignoreCase = true) -> 4
                ex.difficulty.contains("1.2") -> 3
                ex.difficulty.contains("1.1") -> 2
                else -> 2
            }
            val arch = when (ex.mappedPillar) {
                C1Pillar.MODALPARTIKELN -> ErrorArchetype.MOOD_MODALITY
                C1Pillar.SATZGEFUEGE -> ErrorArchetype.WORD_ORDER_KLAMMER
                C1Pillar.NOMINALISIERUNG -> ErrorArchetype.CASE_GOVERNMENT
                else -> ErrorArchetype.REGISTER_COLLOCATION
            }
            C1QuizItem(
                id = "sc_irt_${ex.id}",
                pillar = ex.mappedPillar,
                difficulty = diffTier,
                archetype = arch,
                promptDe = comp.question,
                contextSnippet = ex.transcriptTurns.joinToString(" · ") { "${it.speaker}: „${it.line}“" }.take(280),
                options = comp.options,
                correctIndex = comp.correctIndex,
                visualDiagram = VisualDiagramType.MODAL_PARTICLE_MATRIX,
                explanationDe = ex.correctionGuidance,
                explanationEn = ex.learningObjective,
                trapNote = "Ziel-Anker: ${ex.delayedRetrievalTarget}"
            )
        }
    }

    private fun JSONArray?.toStringList(): List<String> {
        if (this == null) return emptyList()
        return (0 until length()).map { optString(it) }
    }
}
