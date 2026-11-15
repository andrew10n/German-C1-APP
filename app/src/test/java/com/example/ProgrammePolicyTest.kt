package com.example

import com.example.programme.core.*
import org.junit.Assert.*
import org.junit.Test

class ProgrammePolicyTest {
    private val policy = ProgrammePolicy()
    private val planner = DailyPlanner()
    private fun rubric(weak: RubricDimension? = null) = RubricAssessment(
        RubricDimension.values().associateWith { if (it == weak) 2 else 4 })

    private fun sample(
        id: String = "e1", day: Long = 10, task: String = "task-$id",
        mode: ProductionMode = ProductionMode.SPEAKING,
        context: CommunicationContext = CommunicationContext.GENERAL,
        weak: RubricDimension? = null
    ) = ProductionEvidence(id, "u01", task, day, context, mode,
        EvidenceOrigin.OBSERVED_PRODUCTION, AssessmentAuthority.HUMAN, "teacher-1",
        Assistance.NONE, true, false, rubric(weak), "artifact://$id")

    private fun complete() = listOf(
        sample(),
        sample("e2", 11, mode = ProductionMode.INTERACTION, context = CommunicationContext.SOCIAL),
        sample("e3", 12, mode = ProductionMode.WRITING),
        sample("e4", 17, mode = ProductionMode.MEDIATION, context = CommunicationContext.ENGINEERING)
            .copy(unfamiliarTask = true, delayedFromEvidenceId = "e1")
    )
    private fun sign(samples: List<ProductionEvidence> = complete(), day: Long = 17,
                     confirmed: Boolean = true) = HumanVerification("u01", "reviewer", day,
        samples.map { it.id }, confirmed)
    private fun status(samples: List<ProductionEvidence>, today: Long = 30,
                       signs: List<HumanVerification> = emptyList()) =
        policy.evaluateUnit("u01", today, samples, signs).status

    @Test fun `four independent modes and seven day transfer still need human signoff`() {
        assertEquals(MasteryStatus.AWAITING_HUMAN_VERIFICATION, status(complete()))
        assertEquals(MasteryStatus.MASTERED, status(complete(), signs = listOf(sign())))
    }

    @Test fun `six day transfer fails seven day transfer passes`() {
        val six = complete().map { if (it.id == "e4") it.copy(day = 16) else it }
        assertEquals(MasteryStatus.DEVELOPING, status(six))
        assertEquals(MasteryStatus.AWAITING_HUMAN_VERIFICATION, status(complete()))
    }

    @Test fun `transfer must be unfamiliar linked qualifying and cross context`() {
        val last = complete().last()
        val invalid = listOf(
            last.copy(unfamiliarTask = false),
            last.copy(delayedFromEvidenceId = null),
            last.copy(delayedFromEvidenceId = "missing"),
            last.copy(delayedFromEvidenceId = "e3"),
            last.copy(context = CommunicationContext.GENERAL)
        )
        invalid.forEach { assertEquals(MasteryStatus.DEVELOPING, status(complete().dropLast(1) + it)) }
        val weakBaseline = complete().map { if (it.id == "e1") it.copy(rubric = rubric(RubricDimension.COHERENCE)) else it }
        assertEquals(MasteryStatus.DEVELOPING, status(weakBaseline))
    }

    @Test fun `self ratings simulations machine scores assistance and dependent work never count`() {
        val transforms = listOf<(ProductionEvidence) -> ProductionEvidence>(
            { it.copy(origin = EvidenceOrigin.SELF_RATING) },
            { it.copy(origin = EvidenceOrigin.SIMULATED) },
            { it.copy(authority = AssessmentAuthority.AUTOMATED) },
            { it.copy(independent = false) }
        ) + Assistance.values().filter { it != Assistance.NONE }.map { assistance ->
            { e: ProductionEvidence -> e.copy(assistance = assistance) }
        }
        transforms.forEach { transform ->
            val evidence = complete().map(transform)
            assertEquals(MasteryStatus.DEVELOPING, status(evidence, signs = listOf(sign(evidence))))
        }
    }

    @Test fun `every weak dimension blocks mastery despite nine excellent dimensions`() {
        assertEquals(10, RubricDimension.values().size)
        RubricDimension.values().forEach { dimension ->
            val evidence = complete().map { if (it.id == "e3") it.copy(rubric = rubric(dimension)) else it }
            val decision = policy.evaluateUnit("u01", 30, evidence, listOf(sign(evidence)))
            assertEquals(MasteryStatus.DEVELOPING, decision.status)
            assertTrue(dimension in decision.weakDimensions)
        }
    }

    @Test fun `missing rubric dimensions fail rather than defaulting to passing`() {
        val scores = RubricDimension.values().filter { it != RubricDimension.RESPONSIVENESS }.associateWith { 4 }
        val incomplete = complete().map { it.copy(rubric = RubricAssessment(scores)) }
        assertEquals(MasteryStatus.DEVELOPING, status(incomplete))
    }

    @Test fun `scores at three pass but scores outside zero to four rejected`() {
        assertTrue(RubricAssessment(RubricDimension.values().associateWith { 3 }).passes())
        assertThrows(IllegalArgumentException::class.java) {
            RubricAssessment(mapOf(RubricDimension.COHERENCE to 5))
        }
    }

    @Test fun `repeated days tasks modes and contexts cannot manufacture mastery`() {
        val variants = listOf(
            complete().map { it.copy(day = 10) },
            complete().map { it.copy(taskId = "same-prompt") },
            complete().map { it.copy(mode = ProductionMode.SPEAKING) },
            complete().map { it.copy(context = CommunicationContext.GENERAL) },
            complete().map { if (it.context == CommunicationContext.GENERAL) it.copy(context = CommunicationContext.COACHING) else it }
        )
        variants.forEach { assertEquals(MasteryStatus.DEVELOPING, status(it)) }
    }

    @Test fun `future evidence cannot unlock and ordering is deterministic`() {
        assertEquals(MasteryStatus.DEVELOPING, status(complete(), today = 16))
        val a = policy.evaluateUnit("u01", 30, complete())
        val b = policy.evaluateUnit("u01", 30, complete().reversed())
        assertEquals(a.status, b.status)
        assertEquals(a.evidenceIds, b.evidenceIds)
        assertEquals(a.reasons, b.reasons)
    }

    @Test fun `duplicate evidence ids rejected instead of inflating counts`() {
        assertThrows(IllegalArgumentException::class.java) { status(complete() + complete().first()) }
    }

    @Test fun `latest weak retake supersedes older passing prompt`() {
        val retake = complete().first().copy(id = "retake", day = 18,
            rubric = rubric(RubricDimension.GRAMMATICAL_ACCURACY))
        assertEquals(MasteryStatus.DEVELOPING, status(complete() + retake))
    }

    @Test fun `human signoff must be confirmed current exact and not future dated`() {
        val invalid = listOf(
            sign(day = 16), sign(day = 31), sign(confirmed = false),
            HumanVerification("u01", "teacher", 20, listOf("e1"), true),
            HumanVerification("u01", "teacher", 20, listOf("e1", "e2", "e3", "e4", "e4"), true),
            HumanVerification("u02", "teacher", 20, complete().map { it.id }, true)
        )
        invalid.forEach {
            assertEquals(MasteryStatus.AWAITING_HUMAN_VERIFICATION, status(complete(), signs = listOf(it)))
        }
    }

    @Test fun `new evidence invalidates stale attestation`() {
        val newer = sample("e5", 20, mode = ProductionMode.WRITING)
        assertEquals(MasteryStatus.AWAITING_HUMAN_VERIFICATION,
            status(complete() + newer, signs = listOf(sign())))
    }

    @Test fun `prerequisites require verified mastery roadmap never claims completion`() {
        assertEquals(MasteryStatus.LOCKED, policy.evaluateUnit("u02", 30, complete()).status)
        val decisions = policy.evaluateAll(30, complete(), listOf(sign()))
        assertEquals(MasteryStatus.DEVELOPING, decisions.getValue("u02").status)
        assertEquals(MasteryStatus.LOCKED, decisions.getValue("u03").status)
        assertEquals(MasteryStatus.ROADMAP, decisions.getValue("u04").status)
        assertFalse(policy.isProgrammeComplete(30, complete(), listOf(sign())))
    }

    @Test fun `unknown units and cycles fail explicitly`() {
        assertThrows(IllegalArgumentException::class.java) { policy.evaluateUnit("unknown", 30, emptyList()) }
        val unit = CurriculumCatalog.units.first()
        val cyclic = CurriculumUnit("cycle", 1, "Cycle", "Goal", CommunicationContext.GENERAL,
            listOf("cycle"), ContentStatus.AUTHORED, unit.content)
        assertThrows(IllegalArgumentException::class.java) { ProgrammePolicy(listOf(cyclic)) }
    }

    @Test fun `rubric and verification copy caller collections and reject mutation`() {
        val scores = RubricDimension.values().associateWith { 4 }.toMutableMap()
        val assessment = RubricAssessment(scores)
        scores[RubricDimension.COHERENCE] = 0
        assertTrue(assessment.passes())
        assertThrows(UnsupportedOperationException::class.java) {
            (assessment.scores as MutableMap)[RubricDimension.COHERENCE] = 0
        }
        val ids = mutableListOf("e1")
        val verification = HumanVerification("u01", "human", 20, ids, true)
        ids.clear()
        assertEquals(listOf("e1"), verification.evidenceIds)
        assertThrows(UnsupportedOperationException::class.java) {
            (verification.evidenceIds as MutableList).clear()
        }
    }

    @Test fun `each daily budget includes reading TTS production and review with exact minutes`() {
        DailyBudget.values().forEach { budget ->
            val plan = planner.plan(10, budget)
            assertEquals(budget.minutes, plan.plannedMinutes)
            assertEquals(PlanBlockKind.values().toSet(), plan.blocks.map { it.kind }.toSet())
            assertTrue(plan.blocks.all { it.minutes > 0 && it.unitId == "u01" })
            assertTrue(plan.blocks.first { it.kind == PlanBlockKind.LISTENING_TTS }.instruction.contains("TTS"))
        }
    }

    @Test fun `planner advances only after verified prerequisite and rotates production modes`() {
        val next = planner.plan(30, DailyBudget.STANDARD, complete(), listOf(sign()))
        assertEquals("u02", next.blocks.first { it.kind == PlanBlockKind.PRODUCTION }.unitId)
        val resources = (10L..13L).map { day ->
            planner.plan(day, DailyBudget.SHORT).blocks.first { it.kind == PlanBlockKind.PRODUCTION }.resourceId
        }
        assertEquals(4, resources.distinct().size)
    }

    @Test fun `review intervals grow one three seven fourteen thirty days`() {
        val baseline = sample()
        assertTrue(planner.dueReviews(10, listOf(baseline)).isEmpty())
        assertEquals(11L, planner.dueReviews(11, listOf(baseline)).single().dueDay)
        val evidence = mutableListOf(baseline)
        val records = mutableListOf<ReviewRecord>()
        listOf(11L to 14L, 14L to 21L, 21L to 35L, 35L to 65L, 65L to 95L).forEachIndexed { i, pair ->
            val e = sample("r$i", pair.first)
            evidence.add(e)
            records.add(ReviewRecord("u01", e.id))
            assertTrue(planner.dueReviews(pair.second - 1, evidence, records).isEmpty())
            assertEquals(pair.second, planner.dueReviews(pair.second, evidence, records).single().dueDay)
        }
    }

    @Test fun `failed review resets interval and duplicate or early retrieval cannot accelerate`() {
        val e = listOf(sample(), sample("r1", 11), sample("early", 12),
            sample("fail", 14, weak = RubricDimension.LEXICAL_RANGE))
        val reviews = e.drop(1).map { ReviewRecord("u01", it.id) }
        assertEquals(15L, planner.dueReviews(15, e, reviews + reviews).single().dueDay)
        val sameTask = sample("repeat", 14, task = "task-r1")
        assertEquals(14L, planner.dueReviews(14, e.take(2) + sameTask,
            reviews.take(1) + ReviewRecord("u01", "repeat")).single().dueDay)
    }

    @Test fun `self rated and assisted reviews cannot postpone due retrieval`() {
        val baseline = sample()
        val invalid = listOf(
            sample("self", 11).copy(origin = EvidenceOrigin.SELF_RATING),
            sample("sim", 11).copy(origin = EvidenceOrigin.SIMULATED),
            sample("notes", 11).copy(assistance = Assistance.NOTES),
            sample("auto", 11).copy(authority = AssessmentAuthority.AUTOMATED)
        )
        val due = planner.dueReviews(12, listOf(baseline) + invalid,
            invalid.map { ReviewRecord("u01", it.id) })
        assertEquals(11L, due.single().dueDay)
    }

    @Test fun `errors recycle until two independent repairs on different days and tasks`() {
        val error = ErrorRecord("err", "u01", RubricDimension.COLLOCATION_CONTROL,
            "eine Entscheidung machen", "eine Entscheidung treffen", "Feste Wortverbindung", 10)
        assertEquals(listOf("err"), planner.dueReviews(11, emptyList(), errors = listOf(error)).single().errorIds)
        val repair1 = sample("repair1", 11)
        val record1 = ReviewRecord("u01", "repair1", listOf("err"))
        assertEquals(listOf("err"), planner.dueReviews(12, listOf(repair1), listOf(record1), listOf(error)).single().errorIds)
        val sameDay = sample("same-day", 11)
        val records = listOf(record1, ReviewRecord("u01", "same-day", listOf("err")))
        assertEquals(listOf("err"), planner.dueReviews(12, listOf(repair1, sameDay), records, listOf(error)).single().errorIds)
        val repair2 = sample("repair2", 12)
        val repaired = planner.dueReviews(30, listOf(repair1, repair2),
            listOf(record1, ReviewRecord("u01", "repair2", listOf("err"))), listOf(error))
        assertTrue(repaired.all { it.errorIds.isEmpty() })
    }

    @Test fun `future errors and unknown review references are ignored deterministically`() {
        val future = ErrorRecord("future", "u01", RubricDimension.COHERENCE,
            "x", "y", "z", 40)
        assertTrue(planner.dueReviews(30, emptyList(),
            listOf(ReviewRecord("u01", "missing")), listOf(future)).isEmpty())
        val a = planner.plan(30, DailyBudget.SHORT, complete())
        val b = planner.plan(30, DailyBudget.SHORT, complete().reversed())
        assertEquals(a.blocks, b.blocks)
        assertEquals(a.dueReviews.map { it.dueDay }, b.dueReviews.map { it.dueDay })
    }
}
