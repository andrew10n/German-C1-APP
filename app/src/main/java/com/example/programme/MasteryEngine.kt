package com.example.programme

/** Pure, deterministic rules. Timestamps are epoch milliseconds; learning days are UTC days. */
object MasteryEngine {
    const val DAY_MILLIS: Long = 86_400_000L
    const val GRADUATION_UNIT_ID = "graduation"
    // Task-family prefixes: append :<nonblank-variant-id> for each distinct unseen task.
    // A legacy bare ID remains one task, never two merely because it was attempted again.
    const val GENERAL_CHECKPOINT_TASK_ID = "unseen-general"
    const val SPECIALIST_CHECKPOINT_TASK_ID = "unseen-specialist"
    const val SOCIAL_CHECKPOINT_TASK_ID = "unseen-social"
    const val INTEGRATED_CHECKPOINT_TASK_ID = "unseen-integrated"
    const val FORMAL_VALIDATION_GAP =
        "Formale Abschlussfreigabe gesperrt: unabhängige Kalibrierung von Kursinhalten und Rubriken sowie geschützte Hör-/Sprechprüfungen sind nicht belegt."

    private fun isCoreUnit(unit: CurriculumUnit): Boolean =
        unit.content != null && unit.stage in 1..6

    /** All ten dimensions are required on each task; strong dimensions cannot offset weak ones. */
    private fun successful(attempt: Attempt): Boolean =
        attempt.source == EvidenceSource.HUMAN_REVIEW &&
            !attempt.assisted && attempt.independentProduction &&
            Dimension.values().all { (attempt.scores[it] ?: -1) in 3..4 }

    // Conflicting records sharing an evidence ID are not independent, trustworthy evidence.
    private fun history(attempts: List<Attempt>, unitId: String): List<Attempt> = attempts
        .groupBy { it.id }
        .values.mapNotNull { records -> records.distinct().singleOrNull() }
        .filter {
            it.unitId == unitId && it.id.isNotBlank() && it.taskId.isNotBlank() &&
                it.response.isNotBlank() && it.timestamp >= 0
        }
        .sortedWith(compareBy<Attempt> { it.timestamp }.thenBy { it.id })

    fun isMastered(unit: CurriculumUnit, attempts: List<Attempt>): Boolean {
        if (!isCoreUnit(unit)) return false
        val successes = history(attempts, unit.id).filter(::successful)
        if (successes.map { it.taskId }.distinct().size < 3) return false
        if (successes.map { it.context }.distinct().size < 2) return false
        if (successes.map { it.timestamp / DAY_MILLIS }.distinct().size < 2) return false
        val first = successes.first().timestamp
        return successes.any { it.unfamiliar && it.timestamp - first >= 7 * DAY_MILLIS }
    }

    /** Outlines do not block a stage gate, but explicit missing/outline prerequisites do. */
    fun isUnlocked(unit: CurriculumUnit, attempts: List<Attempt>): Boolean {
        val catalogue = CurriculumCatalogue.units.associateBy { it.id }
        fun ready(candidate: CurriculumUnit, visiting: Set<String>): Boolean {
            if (!isCoreUnit(candidate) ||
                candidate.id in visiting) return false
            // Diagnostic units establish a baseline but do not block the authored
            // learning sequence. Stages 1+ must still be mastered in order.
            val priorStage = CurriculumCatalogue.coreUnits.filter {
                it.stage in 1 until candidate.stage
            }.map { it.id }
            return (candidate.prerequisites + priorStage).distinct().all { id ->
                val prerequisite = catalogue[id]
                prerequisite != null && isMastered(prerequisite, attempts) &&
                    ready(prerequisite, visiting + candidate.id)
            }
        }
        return ready(unit, emptySet())
    }

    private fun plusDays(timestamp: Long, days: Long): Long {
        val offset = days * DAY_MILLIS
        return if (timestamp > Long.MAX_VALUE - offset) Long.MAX_VALUE else timestamp + offset
    }

    /**
     * Retrieval intervals use the shared 1/3/7/21-day policy after consecutive human successes.
     * Quiz/self ratings never lengthen an interval. Explicit errors recycle within a day,
     * until superseded by a later successful independent human-reviewed production.
     */
    fun nextReview(attempts: List<Attempt>, unitId: String): Long? {
        val unit = CurriculumCatalogue.coreUnits.find { it.id == unitId } ?: return null
        val records = history(attempts, unitId)
        if (records.isEmpty()) return null
        val productions = records.filter {
            it.source == EvidenceSource.HUMAN_REVIEW && !it.assisted && it.independentProduction
        }
        val latest = productions.lastOrNull()
        val streak = productions.asReversed().takeWhile(::successful).size
        val intervalIndex = (streak - 1).coerceAtLeast(0)
            .coerceAtMost(RetrievalPolicy.intervalsDays.lastIndex)
        val interval = RetrievalPolicy.intervalsDays[intervalIndex].toLong()
        val scheduled = plusDays(latest?.timestamp ?: records.first().timestamp, interval)
        val lastSuccess = productions.lastOrNull(::successful)?.timestamp
        val unresolvedError = records.lastOrNull { record ->
            record.errors.any { it.isNotBlank() } &&
                (lastSuccess == null || record.timestamp >= lastSuccess)
        }
        return if (unresolvedError == null) scheduled
        else minOf(scheduled, plusDays(unresolvedError.timestamp, RetrievalPolicy.errorRetryDays.toLong()))
    }

    /** Due retrieval precedes new learning, including already mastered units. Future data is ignored. */
    fun nextUnit(attempts: List<Attempt>, now: Long): CurriculumUnit? {
        val availableEvidence = attempts.filter { it.timestamp <= now }
        val available = CurriculumCatalogue.coreUnits.filter { isUnlocked(it, availableEvidence) }
        val due = available.mapNotNull { unit ->
            nextReview(availableEvidence, unit.id)?.takeIf { it <= now }?.let { unit to it }
        }.minWithOrNull(compareBy<Pair<CurriculumUnit, Long>> { it.second }.thenBy { it.first.stage }
            .thenBy { it.first.id })
        return due?.first ?: available.firstOrNull { !isMastered(it, availableEvidence) }
    }

    private fun checkpointComplete(
        checkpoints: List<Attempt>, prefix: String, context: ContextTrack
    ): Boolean {
        val matching = checkpoints.filter { attempt ->
            attempt.context == context && (attempt.taskId == prefix ||
                (attempt.taskId.startsWith("$prefix:") &&
                    attempt.taskId.removePrefix("$prefix:").isNotBlank()))
        }
        return matching.any { first ->
            matching.any { second ->
                first.taskId != second.taskId &&
                    first.timestamp / DAY_MILLIS != second.timestamp / DAY_MILLIS
            }
        }
    }

    /**
     * Five separate streams must each demonstrate two distinct unseen tasks on different UTC
     * days: GENERAL/unseen-general, SOCIAL/unseen-social, ENGINEERING/unseen-specialist,
     * COACHING/unseen-specialist, and GENERAL/unseen-integrated. Integrated assessments
     * combine comprehension, production, interaction and mediation; all ten dimensions must
     * meet 3/4 on every qualifying assessment, with no averaging or cross-stream substitution.
     *
     * Use graduation as unitId and <prefix>:<variant-id> as taskId (legacy bare IDs count once).
     * HUMAN_REVIEW is stored source provenance used by internal policy, not authentication
     * of reviewer identity or external certification. No source/identity verification occurs here.
     * Advanced outlines must still be authored and demonstrated; checkpoints do not bypass them.
     */
    fun graduationGaps(
        attempts: List<Attempt>,
        coreUnits: List<CurriculumUnit> = CurriculumCatalogue.coreUnits
    ): List<String> = buildList {
        add(FORMAL_VALIDATION_GAP)
        coreUnits.forEach { unit ->
            if (!isMastered(unit, attempts)) add("Noch nicht nachgewiesen: ${unit.title} (${unit.id}).")
        }
        coreUnits.filter { it.content == null }
            .groupBy { it.stage }.toSortedMap().forEach { (stage, outlines) ->
                add("Stufe $stage: ${outlines.size} Kompetenzen sind nur Entwürfe; ausgearbeitete Aufgaben und Leistungsnachweise fehlen.")
            }
        val checkpoints = history(attempts, GRADUATION_UNIT_ID).filter { successful(it) && it.unfamiliar }
        fun requireCheckpoint(prefix: String, context: ContextTrack, label: String) {
            if (!checkpointComplete(checkpoints, prefix, context)) {
                add("$label Abschluss-Checkpoint fehlt: mindestens zwei verschiedene unbekannte Aufgaben an verschiedenen UTC-Tagen; jeweils unabhängige, unassistierte Produktion mit gespeicherter Quelle HUMAN_REVIEW und allen zehn Dimensionen mindestens 3/4. Aufgaben-ID: $prefix:<Variante>, Kontext: ${context.name}. Die Quellenangabe ist keine externe Identitätsprüfung.")
            }
        }
        requireCheckpoint(GENERAL_CHECKPOINT_TASK_ID, ContextTrack.GENERAL,
            "Allgemeiner (GENERAL)")
        requireCheckpoint(SOCIAL_CHECKPOINT_TASK_ID, ContextTrack.SOCIAL,
            "Sozialer (SOCIAL, Alltag und situationsgerechte Pragmatik)")
        requireCheckpoint(SPECIALIST_CHECKPOINT_TASK_ID, ContextTrack.ENGINEERING,
            "Automotive-fachlicher (ENGINEERING, EV/Hybrid)")
        requireCheckpoint(SPECIALIST_CHECKPOINT_TASK_ID, ContextTrack.COACHING,
            "Jugendfußball-fachlicher (COACHING, Spieler und Eltern)")
        requireCheckpoint(INTEGRATED_CHECKPOINT_TASK_ID, ContextTrack.GENERAL,
            "Integrierter allgemeiner (GENERAL: Verstehen, Produktion, Interaktion und Mediation)")
    }
}
