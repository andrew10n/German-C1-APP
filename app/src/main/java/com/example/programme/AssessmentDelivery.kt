package com.example.programme

enum class AssessmentSessionKind {
    DELAYED_CHECKPOINT,
    GOETHE_STYLE,
    PILOT_WEEK,
    WEEKLY_INTEGRATED,
    LISTENING_PRACTICE,
    SPEAKING_PRACTICE
}

data class DeliveredAssessmentTask(
    val id: String,
    val unitId: String,
    val taskId: String,
    val prompt: String,
    val evidence: AssessmentEvidence,
    val context: ContextTrack,
    val unfamiliar: Boolean,
    val variationId: String,
    val taskVersion: Int,
    val rubricVersion: Int
)

data class TaskVariation(
    val id: String,
    val label: String,
    val audience: String,
    val pressure: String,
    val constraint: String,
    val taskVersion: Int = 2,
    val rubricVersion: Int = 1
) {
    fun apply(prompt: String): String =
        "$prompt\n\nVariation $label: Publikum: $audience. Gesprächsbedingung: $pressure. " +
            "Zusatzbedingung: $constraint"
}

data class AssessmentSession(
    val id: String,
    val kind: AssessmentSessionKind,
    val createdAt: Long,
    val durationMinutes: Int,
    val tasks: List<DeliveredAssessmentTask>
)

data class PilotComparison(
    val baselineResponses: Int,
    val finalResponses: Int,
    val baselineIndependent: Int,
    val finalIndependent: Int,
    val baselineUnfamiliar: Int,
    val finalUnfamiliar: Int
)

object AssessmentDeliveryEngine {
    private const val DAY_MILLIS = 86_400_000L

    val variations: List<TaskVariation> = listOf(
        TaskVariation("peer-review", "Kollegiales Review", "eine fachkundige Kollegin",
            "eine Rückfrage unterbricht Ihre Erklärung", "greifen Sie die Rückfrage auf und führen Sie zum Hauptpunkt zurück"),
        TaskVariation("manager-briefing", "Entscheidungsvorlage", "eine Projektleitung ohne Detailwissen",
            "es wird eine klare Empfehlung verlangt", "trennen Sie gesicherte Fakten, Annahmen und nächsten Prüfschritt"),
        TaskVariation("safety-escalation", "Sicherheitseskalation", "eine Safety-Verantwortliche",
            "Lieferdruck wird ausdrücklich genannt", "geben Sie keine Freigabe ohne belegte Voraussetzung"),
        TaskVariation("parent-conversation", "Elterngespräch", "ein besorgtes Elternteil",
            "die Person formuliert einen Vorwurf", "anerkennen Sie das Anliegen, ohne eine unbelegte Absicht zu bestätigen"),
        TaskVariation("youth-explanation", "Jugendliche Erklärung", "eine U13- oder U15-Gruppe",
            "ein Kind versteht den ersten Erklärungsversuch nicht", "vereinfachen Sie die Erklärung und lassen Sie das Verständnis prüfen"),
        TaskVariation("regional-informal", "Informelles Regionalgespräch", "eine vertraute Person im Vereinsumfeld",
            "eine umgangssprachliche oder regionale Wendung bleibt mehrdeutig", "fragen Sie die gemeinte Verbindlichkeit freundlich nach"),
        TaskVariation("mediation", "Mehrparteien-Mediation", "drei Rollen mit unterschiedlichen Interessen",
            "zwei Personen sprechen gleichzeitig oder widersprechen einander", "fassen Sie beide Positionen fair zusammen und sichern Sie eine nächste Handlung"),
        TaskVariation("delayed-retrieval", "Verzögerter Abruf", "eine neue Gesprächspartnerin",
            "Sie erhalten keine Vorlage und nur kurze Vorbereitungszeit", "verwenden Sie keine auswendig gelernte Modellformulierung")
    )

    fun remainingMinutes(session: AssessmentSession, now: Long): Int {
        val deadline = session.createdAt + session.durationMinutes * 60_000L
        return ((deadline - now + 59_999L) / 60_000L).coerceAtLeast(0L).toInt()
    }

    fun createSession(
        kind: AssessmentSessionKind,
        now: Long,
        seed: Int = Math.floorDiv(now, DAY_MILLIS).toInt()
    ): AssessmentSession {
        val authored = CurriculumCatalogue.units.filter { it.content != null }
        require(authored.isNotEmpty()) { "Assessment requires authored curriculum content." }
        val ordered = authored.drop(seed.floorMod(authored.size)) + authored.take(seed.floorMod(authored.size))
        val tasks = when (kind) {
            AssessmentSessionKind.DELAYED_CHECKPOINT -> ordered.take(4).mapIndexed { index, unit ->
                task(unit, "checkpoint:$index", unit.content!!.transfer, index, seed)
            }
            AssessmentSessionKind.GOETHE_STYLE -> listOf(
                task(ordered[0], "goethe:listening", ordered[0].content!!.listening,
                    0, seed),
                task(ordered[1 % ordered.size], "goethe:reading", ordered[1 % ordered.size].content!!.reading,
                    1, seed),
                task(ordered[2 % ordered.size], "goethe:writing", ordered[2 % ordered.size].content!!.writing,
                    2, seed),
                task(ordered[3 % ordered.size], "goethe:speaking", ordered[3 % ordered.size].content!!.speaking,
                    3, seed)
            )
            AssessmentSessionKind.PILOT_WEEK -> ordered.take(3).mapIndexed { index, unit ->
                task(unit, "pilot:$index", unit.content!!.transfer, index, seed)
            }
            AssessmentSessionKind.WEEKLY_INTEGRATED -> ordered.take(5).mapIndexed { index, unit ->
                task(unit, "weekly:${seed}:$index", unit.content!!.transfer, index, seed)
            }
            AssessmentSessionKind.LISTENING_PRACTICE -> listOf(
                audioTask(
                    ordered[0],
                    "listening:owned-audio",
                    "Hören Sie eine selbst ausgewählte, rechtmäßig zugängliche deutsche Aufnahme. " +
                        "Notieren Sie die Hauptaussage, zwei konkrete Einzelheiten und einen offenen Punkt.",
                    seed
                )
            )
            AssessmentSessionKind.SPEAKING_PRACTICE -> listOf(
                audioTask(
                    ordered[0],
                    "speaking:recorded-response",
                    ordered[0].content!!.speaking,
                    seed
                )
            )
        }
        return AssessmentSession(
            id = "assessment-${kind.name.lowercase()}-$now-$seed",
            kind = kind,
            createdAt = now,
            durationMinutes = when (kind) {
                AssessmentSessionKind.GOETHE_STYLE -> 30
                AssessmentSessionKind.WEEKLY_INTEGRATED -> 60
                AssessmentSessionKind.LISTENING_PRACTICE,
                AssessmentSessionKind.SPEAKING_PRACTICE -> 20
                else -> 30
            },
            tasks = tasks
        )
    }

    fun nextDelayedCheckpoint(
        attempts: List<Attempt>,
        skill: DiagnosticDimension,
        now: Long
    ): Long? {
        val relevant = attempts.filter {
            it.timestamp <= now &&
                it.unfamiliar &&
                it.independentProduction &&
                it.response.isNotBlank()
        }.sortedByDescending { it.timestamp }
        val baseline = relevant.minByOrNull { it.timestamp } ?: return now
        val intervals = MasteryAssessmentContracts.checkpoints
            .first { it.skill == skill }
            .requiredIntervalsDays
        val completedIntervals = intervals.indexOfFirst { interval ->
            val dueAt = plusDays(baseline.timestamp, interval.toLong())
            relevant.none { it.timestamp >= dueAt }
        }
        return if (completedIntervals == -1) null
        else plusDays(baseline.timestamp, intervals[completedIntervals].toLong())
    }

    fun comparePilot(
        attempts: List<Attempt>,
        baselineAt: Long,
        finalAt: Long
    ): PilotComparison {
        fun window(start: Long, end: Long) = attempts.filter {
            it.timestamp in start..end && it.response.isNotBlank()
        }
        val baseline = window(0, baselineAt)
        val final = window(baselineAt + 1, finalAt)
        return PilotComparison(
            baselineResponses = baseline.size,
            finalResponses = final.size,
            baselineIndependent = baseline.count { it.independentProduction },
            finalIndependent = final.count { it.independentProduction },
            baselineUnfamiliar = baseline.count { it.unfamiliar },
            finalUnfamiliar = final.count { it.unfamiliar }
        )
    }

    private fun task(
        unit: CurriculumUnit,
        taskId: String,
        prompt: String,
        index: Int,
        seed: Int = 0
    ): DeliveredAssessmentTask {
        val variation = variations[Math.floorMod(seed + index, variations.size)]
        return DeliveredAssessmentTask(
            id = "task-${unit.id}-$taskId-${variation.id}",
            unitId = unit.id,
            taskId = "assessment:$taskId:${variation.id}",
            prompt = variation.apply(prompt),
            evidence = AssessmentEvidence.TEXT_RESPONSE,
            context = ContextTrack.values()[index % ContextTrack.values().size],
            unfamiliar = false,
            variationId = variation.id,
            taskVersion = variation.taskVersion,
            rubricVersion = variation.rubricVersion
        )
    }

    private fun audioTask(
        unit: CurriculumUnit,
        taskId: String,
        prompt: String,
        seed: Int
    ) = DeliveredAssessmentTask(
        id = "task-${unit.id}-$taskId-$seed",
        unitId = unit.id,
        taskId = "assessment:$taskId:$seed",
        prompt = prompt,
        evidence = AssessmentEvidence.AUDIO_RESPONSE,
        context = ContextTrack.GENERAL,
        unfamiliar = false,
        variationId = "local-audio",
        taskVersion = 3,
        rubricVersion = 1
    )

    private fun Int.floorMod(modulus: Int): Int = Math.floorMod(this, modulus)

    private fun plusDays(timestamp: Long, days: Long): Long {
        val offset = days * MasteryEngine.DAY_MILLIS
        return if (timestamp > Long.MAX_VALUE - offset) Long.MAX_VALUE else timestamp + offset
    }
}
