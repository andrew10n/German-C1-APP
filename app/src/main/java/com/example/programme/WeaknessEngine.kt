package com.example.programme

import kotlin.math.ln
import kotlin.math.max
import kotlin.math.pow

enum class WeaknessSignal(val label: String) {
    SOUND_RECOGNITION("Laute in natürlicher Sprache erkennen"),
    WORD_SEGMENTATION("Wörter im Redefluss trennen"),
    LISTENING_VOCABULARY("Wortschatz beim Hören abrufen"),
    SITUATIONAL_INFERENCE("Situationen und Absichten deuten"),
    INFORMATION_RETENTION("Informationen über mehrere Äußerungen behalten"),
    ACTIVE_RETRIEVAL("Ausdrücke aktiv abrufen"),
    SENTENCE_FORMULATION("Sätze spontan formulieren"),
    GRAMMAR_UNDER_PRESSURE("Grammatik unter Druck"),
    NATURALNESS("Natürlich und idiomatisch formulieren"),
    REGISTER("Register passend wählen"),
    RESPONSE_INITIATION("Antworten ohne lange Verzögerung beginnen"),
    CONVERSATIONAL_REPAIR("Nachfragen und Missverständnisse reparieren"),
    INTERACTION_MANAGEMENT("Unerwartete Gesprächsbeiträge steuern"),
    IMPLIED_MEANING("Andeutungen und Ironie einordnen"),
    MULTI_SPEAKER_TRACKING("Mehrere Sprecher verfolgen")
}

enum class WeaknessConfidence(val label: String) {
    LOW("erste Hinweise"),
    MEDIUM("mehrere Hinweise"),
    HIGH("wiederkehrendes Muster")
}

data class WeaknessEvidence(
    val signal: WeaknessSignal,
    val context: ContextTrack,
    val confidence: Double,
    val timestamp: Long,
    val excerpt: String,
    val source: String
)

data class WeaknessSummary(
    val signal: WeaknessSignal,
    val contexts: Set<ContextTrack>,
    val sampleCount: Int,
    val confidence: WeaknessConfidence,
    val priority: Double,
    val latestEvidence: WeaknessEvidence
)

data class WeaknessProfile(val summaries: List<WeaknessSummary>) {
    fun top(limit: Int = 3): List<WeaknessSummary> = summaries.take(limit)
}

/**
 * Converts observations into cautious training signals. A single self-report is
 * evidence for a practice priority, never a permanent diagnosis or a score.
 */
object WeaknessEngine {
    fun profile(
        attempts: List<Attempt>,
        experiences: List<RealLifeExperience>,
        now: Long
    ): WeaknessProfile {
        val evidence = experiences.asSequence()
            .filter { it.createdAt <= now }
            .flatMap { classifyExperience(it).asSequence() }
            .toList() + attempts.asSequence()
            .filter { it.timestamp <= now }
            .flatMap { classifyAttempt(it).asSequence() }
            .toList()
        val grouped = evidence.groupBy { it.signal }
        val summaries = grouped.map { (signal, items) ->
            val ordered = items.sortedByDescending { it.timestamp }
            val count = items.size
            val contexts = items.map { it.context }.toSet()
            val recencyWeights = items.map { item ->
                val ageDays = (now - item.timestamp).coerceAtLeast(0L).toDouble() / DAY_MILLIS
                0.5.pow(ageDays / HALF_LIFE_DAYS)
            }
            val effectiveSamples = recencyWeights.sum()
            val recency = recencyWeights.maxOrNull() ?: 0.0
            val averageConfidence = items.map { it.confidence }.average()
            val priority = items.zip(recencyWeights).sumOf { (item, weight) ->
                item.confidence * weight
            } + ln(1.0 + count) * 0.35 +
                max(0, contexts.size - 1) * 0.25 + recency * 0.5
            WeaknessSummary(
                signal = signal,
                contexts = contexts,
                sampleCount = count,
                confidence = confidenceFor(effectiveSamples, averageConfidence),
                priority = priority,
                latestEvidence = ordered.first()
            )
        }.sortedByDescending { it.priority }
        return WeaknessProfile(summaries)
    }

    private fun classifyExperience(experience: RealLifeExperience): List<WeaknessEvidence> {
        val signal = when (experience.issueType) {
            RealLifeIssueType.LISTENING -> if (experience.heard.isBlank()) {
                WeaknessSignal.SITUATIONAL_INFERENCE
            } else {
                WeaknessSignal.LISTENING_VOCABULARY
            }
            RealLifeIssueType.RETRIEVAL -> WeaknessSignal.ACTIVE_RETRIEVAL
            RealLifeIssueType.GRAMMAR_UNDER_PRESSURE -> WeaknessSignal.GRAMMAR_UNDER_PRESSURE
            RealLifeIssueType.NATURALNESS -> WeaknessSignal.NATURALNESS
            RealLifeIssueType.REGISTER -> WeaknessSignal.REGISTER
            RealLifeIssueType.REPAIR -> WeaknessSignal.CONVERSATIONAL_REPAIR
        }
        return listOf(
            WeaknessEvidence(
                signal = signal,
                context = experience.context,
                confidence = 0.55,
                timestamp = experience.createdAt,
                excerpt = experience.said.take(120),
                source = "real-life"
            )
        )
    }

    private fun classifyAttempt(attempt: Attempt): List<WeaknessEvidence> {
        if (attempt.response.isBlank()) return emptyList()
        if (attempt.errors.isEmpty() && !attempt.response.trim().startsWithAny("äh", "ehm", "hm", "also", "na ja")) {
            return emptyList()
        }
        val signal = when {
            attempt.errors.isEmpty() -> WeaknessSignal.RESPONSE_INITIATION
            attempt.taskId.contains("listening", ignoreCase = true) ->
                WeaknessSignal.LISTENING_VOCABULARY
            attempt.taskId.contains("interaction", ignoreCase = true) ->
                WeaknessSignal.INTERACTION_MANAGEMENT
            attempt.taskId.contains("speaking", ignoreCase = true) ||
                attempt.taskId.contains("transfer", ignoreCase = true) ->
                WeaknessSignal.SENTENCE_FORMULATION
            else -> WeaknessSignal.GRAMMAR_UNDER_PRESSURE
        }
        return listOf(
            WeaknessEvidence(
                signal = signal,
                context = attempt.context,
                confidence = if (attempt.errors.isEmpty()) 0.3 else 0.5,
                timestamp = attempt.timestamp,
                excerpt = (attempt.errors.joinToString("; ").ifBlank { attempt.response }).take(120),
                source = "attempt"
            )
        )
    }

    private fun String.startsWithAny(vararg prefixes: String): Boolean =
        prefixes.any { startsWith(it, ignoreCase = true) }

    private fun confidenceFor(effectiveSamples: Double, average: Double): WeaknessConfidence = when {
        effectiveSamples >= 2.5 && average >= 0.5 -> WeaknessConfidence.HIGH
        effectiveSamples >= 1.7 || average >= 0.7 -> WeaknessConfidence.MEDIUM
        else -> WeaknessConfidence.LOW
    }

    private const val DAY_MILLIS = 86_400_000L
    private const val HALF_LIFE_DAYS = 45.0
}
