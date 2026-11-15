package com.example.programme.core

import java.util.Collections

internal fun <T> frozen(values: Collection<T>): List<T> =
    Collections.unmodifiableList(ArrayList(values))
internal fun <K, V> frozenMap(values: Map<K, V>): Map<K, V> =
    Collections.unmodifiableMap(LinkedHashMap(values))

enum class CommunicationContext { GENERAL, SOCIAL, ENGINEERING, COACHING }
enum class ContentStatus { AUTHORED, ROADMAP }
enum class ProductionMode { SPEAKING, INTERACTION, WRITING, MEDIATION }
enum class SourceKind { READING, LISTENING_TTS }
enum class ActivityKind { CONTROLLED, SPEAKING, INTERACTION, WRITING, MEDIATION, TRANSFER, DELAYED_REVIEW }

/** Project-authored teaching material, not a claim of authentic recorded speech. */
data class LearningSource(
    val id: String,
    val title: String,
    val kind: SourceKind,
    val text: String,
    val attribution: String = "Originaltext der Redaktion; didaktisch verfasst, keine externe Quelle",
    val delivery: String = if (kind == SourceKind.LISTENING_TTS)
        "TTS (de-DE), synthetische Stimme; kein authentisches Audio. Erst hören, danach Transkript öffnen."
    else "Lesetext; vor der Bearbeitung vollständig lesen."
)

data class Collocation(val expression: String, val example: String)
data class GrammarFocus(val topic: String, val explanation: String, val example: String)
data class ComprehensionItem(val sourceId: String, val question: String, val keyedAnswer: String)
data class PracticeTask(
    val id: String,
    val kind: ActivityKind,
    val prompt: String,
    val successCriteria: String,
    val modelAnswer: String,
    val modelFeedback: String,
    val minimumDelayDays: Int = 0
)

class AuthoredContent(
    objectives: List<String>,
    sources: List<LearningSource>,
    collocations: List<Collocation>,
    grammar: List<GrammarFocus>,
    comprehension: List<ComprehensionItem>,
    tasks: List<PracticeTask>
) {
    val objectives = frozen(objectives)
    val sources = frozen(sources)
    val collocations = frozen(collocations)
    val grammar = frozen(grammar)
    val comprehension = frozen(comprehension)
    val tasks = frozen(tasks)

    init {
        require(objectives.size >= 3)
        require(sources.map { it.id }.distinct().size == sources.size)
        require(sources.any { it.kind == SourceKind.READING })
        require(sources.any { it.kind == SourceKind.LISTENING_TTS })
        require(collocations.size >= 4 && grammar.size >= 2)
        require(comprehension.size >= 4 && comprehension.all { q ->
            sources.any { it.id == q.sourceId } && q.keyedAnswer.isNotBlank()
        })
        require(ActivityKind.values().all { kind -> tasks.any { it.kind == kind } })
        require(tasks.map { it.id }.distinct().size == tasks.size)
        require(tasks.all { it.prompt.isNotBlank() && it.successCriteria.isNotBlank() &&
            it.modelAnswer.isNotBlank() && it.modelFeedback.isNotBlank() })
        require(tasks.filter { it.kind == ActivityKind.DELAYED_REVIEW }.all { it.minimumDelayDays >= 7 })
    }
}

class CurriculumUnit(
    val id: String,
    val stage: Int,
    val title: String,
    val communicationGoal: String,
    val context: CommunicationContext,
    prerequisites: List<String>,
    val status: ContentStatus,
    val content: AuthoredContent? = null
) {
    val prerequisites = frozen(prerequisites)
    init {
        require(id.isNotBlank() && stage in 1..7 && communicationGoal.isNotBlank())
        require((status == ContentStatus.AUTHORED) == (content != null))
    }
}

class CurriculumStage(val number: Int, val title: String, unitIds: List<String>) {
    val unitIds = frozen(unitIds)
}

/** Local instructional rubric, not an official CEFR certification instrument. Every dimension must pass. */
enum class RubricDimension(val criterion: String) {
    TASK_FULFILMENT("Auftrag und alle wesentlichen Inhalte adressieren"),
    COHERENCE("Gedanken nachvollziehbar gliedern und verknüpfen"),
    LEXICAL_RANGE("Präzise und variabel formulieren, Umschreibungen nutzen"),
    COLLOCATION_CONTROL("Wortverbindungen und Fachbegriffe passend verwenden"),
    GRAMMATICAL_RANGE("Komplexe Strukturen funktional einsetzen"),
    GRAMMATICAL_ACCURACY("Bedeutung trotz vereinzelter Fehler eindeutig halten"),
    FLUENCY_AND_DELIVERY("Mündlich verständlich und flüssig; schriftlich lesbar und konventionsgerecht"),
    REGISTER_AND_PRAGMATICS("Adressat, Höflichkeit und Situation berücksichtigen"),
    RESPONSIVENESS("Einwände oder alternative Perspektiven aufnehmen und klären"),
    MEDIATION_AND_PRECISION("Fremde Positionen, Einschränkungen und Unsicherheit unverfälscht vermitteln")
}

/** 0 absent, 1 heavily dependent, 2 inconsistent, 3 independent and effective, 4 flexible and precise.
 * Missing dimensions fail; adapters must not fabricate a score for an unobserved dimension.
 */
class RubricAssessment(scores: Map<RubricDimension, Int>) {
    val scores = frozenMap(scores)
    init { require(scores.values.all { it in 0..4 }) }
    fun weakDimensions(minimum: Int = 3): List<RubricDimension> =
        frozen(RubricDimension.values().filter { (scores[it] ?: -1) < minimum })
    fun passes(): Boolean = weakDimensions().isEmpty()
}

enum class EvidenceOrigin { OBSERVED_PRODUCTION, SELF_RATING, SIMULATED }
enum class AssessmentAuthority { HUMAN, AUTOMATED }
enum class Assistance { NONE, NOTES, MODEL_ANSWER, TRANSLATION, COACHING }

/** Immutable snapshot. Day is a UTC epoch-day supplied by the caller, never the system clock.
 * artifactReference points to retained text/audio plus task prompt; the core does not store artifacts.
 * Task IDs identify genuinely different prompts, not repeat attempts relabelled by the UI.
 */
data class ProductionEvidence(
    val id: String,
    val unitId: String,
    val taskId: String,
    val day: Long,
    val context: CommunicationContext,
    val mode: ProductionMode,
    val origin: EvidenceOrigin,
    val authority: AssessmentAuthority,
    val assessorId: String,
    val assistance: Assistance,
    val independent: Boolean,
    val unfamiliarTask: Boolean,
    val rubric: RubricAssessment,
    val artifactReference: String,
    val delayedFromEvidenceId: String? = null
) {
    init {
        require(listOf(id, unitId, taskId, artifactReference, assessorId).all { it.isNotBlank() })
        require(day >= 0)
    }
}

/** A trusted human attestation, not a cryptographic signature. Validate identity in the repository.
 * The exact qualifying evidence set must be attested; a stale sign-off cannot unlock new evidence.
 */
class HumanVerification(
    val unitId: String,
    val reviewerId: String,
    val day: Long,
    evidenceIds: List<String>,
    val confirmsIndependentPerformance: Boolean
) {
    val evidenceIds = frozen(evidenceIds)
    init { require(unitId.isNotBlank() && reviewerId.isNotBlank() && day >= 0) }
}

enum class MasteryStatus { ROADMAP, LOCKED, DEVELOPING, AWAITING_HUMAN_VERIFICATION, MASTERED }
class MasteryDecision(
    val status: MasteryStatus,
    reasons: List<String>,
    evidenceIds: List<String>,
    weakDimensions: List<RubricDimension>
) {
    val reasons = frozen(reasons)
    val evidenceIds = frozen(evidenceIds)
    val weakDimensions = frozen(weakDimensions)
}
