package com.example.programme

enum class DiagnosticDimension(val label: String) {
    WORD_RECOGNITION("Gesprochene Wörter erkennen"),
    CONNECTED_SPEECH("Redefluss und Wortgrenzen verstehen"),
    SITUATIONAL_MEANING("Situation und Absicht verstehen"),
    EXPRESSION_RETRIEVAL("Ausdrücke aktiv abrufen"),
    SENTENCE_FORMULATION("Sätze unter Druck formulieren"),
    NATURAL_SPEAKING("Natürlich und idiomatisch sprechen"),
    IMPLIED_MEANING("Andeutungen und Ironie einordnen"),
    MULTI_SPEAKER_TRACKING("Mehrere Sprecher verfolgen"),
    INFORMATION_RETENTION("Informationen behalten"),
    SPONTANEOUS_RESPONSE("Spontan reagieren"),
    RESPONSE_INITIATION("Antworten ohne lange Verzögerung beginnen"),
    INTERRUPTION_HANDLING("Unterbrechungen bewältigen"),
    CONVERSATIONAL_REPAIR("Missverständnisse reparieren")
}

enum class DiagnosticStatus(val label: String) {
    DEMONSTRATED("beobachtet"),
    FRAGILE("wiederholen"),
    NOT_OBSERVED("noch nicht beobachtet")
}

enum class DiagnosticConfidence(val label: String) {
    LOW("vorsichtiger Hinweis"),
    MEDIUM("mehrere Beobachtungen"),
    HIGH("wiederkehrendes Muster")
}

data class DiagnosticObservation(
    val dimension: DiagnosticDimension,
    val status: DiagnosticStatus,
    val confidence: Double,
    val evidence: String,
    val timestamp: Long
)

data class DiagnosticFinding(
    val dimension: DiagnosticDimension,
    val status: DiagnosticStatus,
    val confidence: DiagnosticConfidence,
    val sampleCount: Int,
    val evidence: List<String>
)

data class LearnerDiagnosticProfile(val findings: List<DiagnosticFinding>) {
    fun finding(dimension: DiagnosticDimension): DiagnosticFinding? =
        findings.firstOrNull { it.dimension == dimension }
}

/**
 * Extracts a provisional communication profile from ordinary learner input.
 * It never invents missing observations and never converts them into CEFR scores.
 */
object LearnerDiagnosticEngine {
    fun analyze(
        attempts: List<Attempt>,
        experiences: List<RealLifeExperience>,
        now: Long
    ): LearnerDiagnosticProfile {
        require(now >= 0)
        val observations = attempts.asSequence()
            .filter { it.timestamp <= now }
            .flatMap { fromAttempt(it).asSequence() }
            .toList() + experiences.asSequence()
            .filter { it.createdAt <= now }
            .flatMap { fromExperience(it).asSequence() }
            .toList()
        val findings = DiagnosticDimension.values().mapNotNull { dimension ->
            val matching = observations.filter { it.dimension == dimension }
            if (matching.isEmpty()) return@mapNotNull null
            val fragile = matching.count { it.status == DiagnosticStatus.FRAGILE }
            val demonstrated = matching.count { it.status == DiagnosticStatus.DEMONSTRATED }
            DiagnosticFinding(
                dimension = dimension,
                status = when {
                    fragile > demonstrated -> DiagnosticStatus.FRAGILE
                    demonstrated > 0 -> DiagnosticStatus.DEMONSTRATED
                    else -> DiagnosticStatus.NOT_OBSERVED
                },
                confidence = if (demonstrated == 0 && fragile == 0) DiagnosticConfidence.LOW
                else confidence(matching.size, matching.map { it.confidence }.average()),
                sampleCount = matching.size,
                evidence = matching.sortedByDescending { it.timestamp }
                    .take(2)
                    .map { it.evidence.take(120) }
            )
        }.sortedWith(compareBy<DiagnosticFinding> {
            if (it.status == DiagnosticStatus.FRAGILE) 0 else 1
        }.thenByDescending { it.sampleCount })
        return LearnerDiagnosticProfile(findings)
    }

    private fun fromAttempt(attempt: Attempt): List<DiagnosticObservation> {
        if (attempt.response.isBlank()) return emptyList()
        val response = attempt.response.trim()
        val evidence = attempt.errors.firstOrNull() ?: response
        val fragile = attempt.errors.isNotEmpty()
        val confidence = if (attempt.source == EvidenceSource.AUTOMATIC_QUIZ) 0.8 else 0.4
        val taskObservations = when {
            attempt.taskId == "quiz" && attempt.source == EvidenceSource.AUTOMATIC_QUIZ -> listOf(
                observation(
                    DiagnosticDimension.SITUATIONAL_MEANING,
                    if (attempt.scores[Dimension.COMPREHENSION] == 4) {
                        DiagnosticStatus.DEMONSTRATED
                    } else {
                        DiagnosticStatus.FRAGILE
                    },
                    confidence, evidence, attempt.timestamp
                )
            )
            attempt.taskId == "writing" || attempt.taskId == "controlled" ||
                attempt.taskId == "speaking" || attempt.taskId == "transfer" -> listOf(
                observation(
                    DiagnosticDimension.SENTENCE_FORMULATION,
                    if (fragile) DiagnosticStatus.FRAGILE else DiagnosticStatus.NOT_OBSERVED,
                    if (fragile) confidence else 0.2,
                    if (fragile) evidence else "Keine bekannte Regelverletzung gefunden; freier Text nicht vollständig bewertet.",
                    attempt.timestamp
                )
            )
            attempt.taskId == "listening" -> listOf(
                observation(
                    DiagnosticDimension.WORD_RECOGNITION,
                    DiagnosticStatus.NOT_OBSERVED,
                    0.1,
                    "Textantwort ohne kontrollierte Audioauswertung.",
                    attempt.timestamp
                )
            )
            attempt.taskId == "interaction" -> listOf(
                observation(
                    DiagnosticDimension.SPONTANEOUS_RESPONSE,
                    DiagnosticStatus.NOT_OBSERVED,
                    0.1,
                    "Getippte Antwort ohne Live-Interaktion oder Zeitmessung.",
                    attempt.timestamp
                )
            )
            else -> emptyList()
        }
        return taskObservations
    }

    private fun fromExperience(experience: RealLifeExperience): List<DiagnosticObservation> {
        val (dimension, status) = when (experience.issueType) {
            RealLifeIssueType.LISTENING -> DiagnosticDimension.WORD_RECOGNITION to DiagnosticStatus.FRAGILE
            RealLifeIssueType.RETRIEVAL -> DiagnosticDimension.EXPRESSION_RETRIEVAL to DiagnosticStatus.FRAGILE
            RealLifeIssueType.GRAMMAR_UNDER_PRESSURE -> DiagnosticDimension.SENTENCE_FORMULATION to DiagnosticStatus.FRAGILE
            RealLifeIssueType.NATURALNESS -> DiagnosticDimension.NATURAL_SPEAKING to DiagnosticStatus.FRAGILE
            RealLifeIssueType.REGISTER -> DiagnosticDimension.SITUATIONAL_MEANING to DiagnosticStatus.FRAGILE
            RealLifeIssueType.REPAIR -> DiagnosticDimension.CONVERSATIONAL_REPAIR to DiagnosticStatus.FRAGILE
        }
        val primaryEvidence = when (experience.issueType) {
            RealLifeIssueType.LISTENING -> experience.heard.ifBlank {
                "Gesprochenes wurde nicht sicher erkannt."
            }
            RealLifeIssueType.RETRIEVAL -> experience.said.ifBlank {
                "Der Ausdruck war in der Situation nicht abrufbar."
            }
            RealLifeIssueType.GRAMMAR_UNDER_PRESSURE,
            RealLifeIssueType.NATURALNESS,
            RealLifeIssueType.REGISTER,
            RealLifeIssueType.REPAIR -> experience.said.ifBlank {
                "Keine eigene Formulierung dokumentiert."
            }
        }
        val observations = mutableListOf(
            observation(dimension, status, 0.55, primaryEvidence, experience.createdAt)
        )
        if (experience.said.isBlank() && dimension != DiagnosticDimension.EXPRESSION_RETRIEVAL) {
            observations += observation(
                DiagnosticDimension.EXPRESSION_RETRIEVAL,
                DiagnosticStatus.FRAGILE,
                0.5,
                "Keine eigene Formulierung dokumentiert.",
                experience.createdAt
            )
        }
        if (experience.heard.isNotBlank() && experience.intended.isNotBlank() &&
            experience.heard != experience.intended
        ) {
            observations += observation(
                DiagnosticDimension.SITUATIONAL_MEANING,
                DiagnosticStatus.FRAGILE,
                0.45,
                experience.heard,
                experience.createdAt
            )
        }
        return observations
    }

    private fun observation(
        dimension: DiagnosticDimension,
        status: DiagnosticStatus,
        confidence: Double,
        evidence: String,
        timestamp: Long
    ) = DiagnosticObservation(dimension, status, confidence, evidence, timestamp)

    private fun confidence(count: Int, average: Double) = when {
        count >= 3 && average >= 0.55 -> DiagnosticConfidence.HIGH
        count >= 2 || average >= 0.7 -> DiagnosticConfidence.MEDIUM
        else -> DiagnosticConfidence.LOW
    }
}
