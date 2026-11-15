package com.example.programme.core

/** Pure deterministic policy. No clock, network, persistence, random selection, or Android types.
 * Readiness is deliberately conservative: four modes, four tasks on four days, two contexts,
 * including the unit's mapped context, plus unfamiliar transfer at least seven days after
 * a qualifying baseline. All ten rubric scores must be >= 3 in each counted sample.
 */
class ProgrammePolicy(units: List<CurriculumUnit> = CurriculumCatalog.units) {
    private val units = frozen(units)
    private val byId = this.units.associateBy { it.id }

    init {
        require(byId.size == units.size) { "Duplicate unit IDs" }
        val visited = mutableSetOf<String>()
        val visiting = mutableSetOf<String>()
        fun visit(id: String) {
            require(id in byId) { "Unknown prerequisite: $id" }
            require(id !in visiting) { "Cyclic prerequisites: $id" }
            if (id in visited) return
            visiting.add(id)
            byId.getValue(id).prerequisites.forEach { visit(it) }
            visiting.remove(id)
            visited.add(id)
        }
        units.forEach { visit(it.id) }
    }

    fun evaluateAll(
        today: Long,
        evidence: List<ProductionEvidence>,
        verifications: List<HumanVerification> = emptyList()
    ): Map<String, MasteryDecision> {
        require(today >= 0)
        require(evidence.map { it.id }.distinct().size == evidence.size) { "Duplicate evidence IDs" }
        val decisions = linkedMapOf<String, MasteryDecision>()
        fun evaluate(id: String): MasteryDecision {
            decisions[id]?.let { return it }
            val unit = byId.getValue(id)
            val prerequisites = unit.prerequisites.map { evaluate(it).status }
            val decision = when {
                unit.status == ContentStatus.ROADMAP -> decision(MasteryStatus.ROADMAP, "Content is not authored; cannot claim completion")
                prerequisites.any { it != MasteryStatus.MASTERED } ->
                    decision(MasteryStatus.LOCKED, "Prerequisites require verified mastery")
                else -> assess(unit, today, evidence, verifications)
            }
            decisions[id] = decision
            return decision
        }
        units.forEach { evaluate(it.id) }
        return frozenMap(decisions)
    }

    fun evaluateUnit(
        unitId: String,
        today: Long,
        evidence: List<ProductionEvidence>,
        verifications: List<HumanVerification> = emptyList()
    ): MasteryDecision {
        require(unitId in byId) { "Unknown unit: $unitId" }
        return evaluateAll(today, evidence, verifications).getValue(unitId)
    }

    /** False while any mapped unit is roadmap or lacks human-verified mastery. Not a C1 certificate. */
    fun isProgrammeComplete(
        today: Long,
        evidence: List<ProductionEvidence>,
        verifications: List<HumanVerification> = emptyList()
    ): Boolean = units.isNotEmpty() && evaluateAll(today, evidence, verifications).values
        .all { it.status == MasteryStatus.MASTERED }

    private fun assess(
        unit: CurriculumUnit,
        today: Long,
        evidence: List<ProductionEvidence>,
        verifications: List<HumanVerification>
    ): MasteryDecision {
        val observed = evidence.filter {
            it.unitId == unit.id && it.day <= today &&
                it.origin == EvidenceOrigin.OBSERVED_PRODUCTION &&
                it.authority == AssessmentAuthority.HUMAN &&
                it.assistance == Assistance.NONE && it.independent
        }
        // Repeating a prompt cannot multiply evidence. A later weak attempt supersedes its old score.
        val latest = observed.groupBy { it.taskId }.values.map { attempts ->
            attempts.sortedWith(compareBy<ProductionEvidence> { it.day }.thenBy { it.id }).last()
        }.sortedWith(compareBy<ProductionEvidence> { it.day }.thenBy { it.id })
        val passing = latest.filter { it.rubric.passes() }
        val weak = latest.flatMap { it.rubric.weakDimensions() }.distinct().sortedBy { it.ordinal }
        val reasons = mutableListOf<String>()
        if (passing.size < 4) reasons.add("Need four independently assessed tasks")
        if (passing.map { it.day }.distinct().size < 4) reasons.add("Need production on four different days")
        if (passing.map { it.context }.distinct().size < 2 || passing.none { it.context == unit.context })
            reasons.add("Need different contexts including the mapped unit context")
        if (!ProductionMode.values().all { mode -> passing.any { it.mode == mode } })
            reasons.add("Need speaking, interaction, writing and mediation evidence")
        val delayedTransfer = passing.any { transfer ->
            transfer.unfamiliarTask && passing.any { baseline ->
                baseline.id == transfer.delayedFromEvidenceId && baseline.taskId != transfer.taskId &&
                    transfer.day - baseline.day >= 7 && baseline.context != transfer.context
            }
        }
        if (!delayedTransfer) reasons.add("Need unfamiliar cross-context transfer at least seven days after a qualifying baseline")
        if (weak.isNotEmpty()) reasons.add("Latest observed tasks contain weak or unobserved rubric dimensions; no averaging")
        val ids = passing.map { it.id }.sorted()
        if (reasons.isNotEmpty()) return MasteryDecision(MasteryStatus.DEVELOPING, reasons, ids, weak)
        val verified = verifications.any {
            it.unitId == unit.id && it.confirmsIndependentPerformance &&
                it.day <= today && it.day >= passing.maxOf { sample -> sample.day } &&
                it.evidenceIds.size == ids.size && it.evidenceIds.toSet() == ids.toSet()
        }
        return if (verified) MasteryDecision(MasteryStatus.MASTERED, emptyList(), ids, emptyList())
        else MasteryDecision(MasteryStatus.AWAITING_HUMAN_VERIFICATION,
            listOf("A human must verify these exact artifacts and their independence"), ids, emptyList())
    }

    private fun decision(status: MasteryStatus, reason: String) =
        MasteryDecision(status, listOf(reason), emptyList(), emptyList())
}
