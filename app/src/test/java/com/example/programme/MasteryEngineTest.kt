package com.example.programme

import org.junit.Assert.*
import org.junit.Test

class MasteryEngineTest {
    private val day = MasteryEngine.DAY_MILLIS
    private val explain = CurriculumCatalogue.units.first { it.id == "s1-explain" }
    private val justify = CurriculumCatalogue.units.first { it.id == "s1-justify" }
    private val uncertainty = CurriculumCatalogue.units.first { it.id == "s1-uncertainty" }
    private val feedback = CurriculumCatalogue.units.first { it.id == "s1-feedback" }
    private val scores = Dimension.values().associateWith { 3 }

    private fun attempt(
        n: Int, at: Long = n * day, unit: CurriculumUnit = explain,
        context: ContextTrack = ContextTrack.GENERAL
    ) = Attempt("${unit.id}-$n", unit.id, "task-$n", at, "Eigenständige Antwort $n",
        EvidenceSource.HUMAN_REVIEW, scores, unfamiliar = n == 3,
        independentProduction = true, context = context)

    private fun mastery(unit: CurriculumUnit = explain) = listOf(
        attempt(1, 0, unit), attempt(2, day, unit, ContextTrack.SOCIAL),
        attempt(3, 7 * day, unit, ContextTrack.ENGINEERING)
    )

    private fun checkpointPrefix(context: ContextTrack) = when (context) {
        ContextTrack.GENERAL -> MasteryEngine.GENERAL_CHECKPOINT_TASK_ID
        ContextTrack.SOCIAL -> MasteryEngine.SOCIAL_CHECKPOINT_TASK_ID
        ContextTrack.ENGINEERING, ContextTrack.COACHING -> MasteryEngine.SPECIALIST_CHECKPOINT_TASK_ID
    }

    private fun checkpoint(
        context: ContextTrack = ContextTrack.GENERAL,
        prefix: String = checkpointPrefix(context), n: Int = 1
    ) = Attempt(
        id = "checkpoint-$context-$prefix-$n",
        unitId = MasteryEngine.GRADUATION_UNIT_ID,
        taskId = "$prefix:task-$n",
        timestamp = (29L + n) * day, response = "Unvorbereitete unabhängige Leistung $n",
        source = EvidenceSource.HUMAN_REVIEW, scores = scores, unfamiliar = true,
        independentProduction = true, context = context
    )

    private fun checkpointPair(
        context: ContextTrack = ContextTrack.GENERAL, prefix: String = checkpointPrefix(context)
    ) = (1..2).map { checkpoint(context, prefix, it) }

    private fun allCheckpoints() = ContextTrack.values().flatMap { checkpointPair(it) } +
        checkpointPair(prefix = MasteryEngine.INTEGRATED_CHECKPOINT_TASK_ID)

    private fun checkpointGaps(records: List<Attempt>) = MasteryEngine.graduationGaps(records)
        .filter { it.contains("Abschluss-Checkpoint fehlt") }

    private fun hasCheckpointGap(
        records: List<Attempt>, context: ContextTrack,
        prefix: String = checkpointPrefix(context)
    ) = checkpointGaps(records).any {
        it.contains("Aufgaben-ID: $prefix:<Variante>") && it.contains("Kontext: ${context.name}.")
    }

    @Test fun threeIndependentHumanTasksAcrossContextsAndDaysMasterAUnit() {
        assertTrue(MasteryEngine.isMastered(explain, mastery()))
        assertTrue(MasteryEngine.isMastered(explain, mastery().reversed()))
    }

    @Test fun fewerThanThreeDistinctTasksDoNotMaster() {
        assertFalse(MasteryEngine.isMastered(explain, mastery().take(2)))
        assertFalse(MasteryEngine.isMastered(explain, mastery().map { it.copy(taskId = "same-task") }))
    }

    @Test fun duplicateAttemptIdsCannotManufactureEvidence() {
        val one = mastery().first()
        assertFalse(MasteryEngine.isMastered(explain, List(5) { one }))
        val conflicting = mastery().map { it.copy(id = "same-id") }
        assertFalse(MasteryEngine.isMastered(explain, conflicting))
        assertTrue(MasteryEngine.isMastered(explain, mastery() + mastery()))
    }

    @Test fun quizAndSelfReflectionNeverEstablishProductionMastery() {
        listOf(EvidenceSource.AUTOMATIC_QUIZ, EvidenceSource.SELF_REFLECTION).forEach { source ->
            assertFalse(MasteryEngine.isMastered(explain, mastery().map { it.copy(source = source) }))
            assertFalse(MasteryEngine.isMastered(explain, mastery().take(2) + mastery().last().copy(source = source)))
        }
    }

    @Test fun assistanceAndNonProductionCannotEstablishMastery() {
        assertFalse(MasteryEngine.isMastered(explain, mastery().map { it.copy(assisted = true) }))
        assertFalse(MasteryEngine.isMastered(explain, mastery().map { it.copy(independentProduction = false) }))
    }

    @Test fun everyDimensionMustMeetThresholdWithoutAveraging() {
        Dimension.values().forEach { weak ->
            val strong = Dimension.values().associateWith { if (it == weak) 2 else 4 }
            assertFalse("Weak dimension $weak", MasteryEngine.isMastered(explain,
                mastery().map { it.copy(scores = strong) }))
        }
        assertTrue(MasteryEngine.isMastered(explain, mastery().map { it.copy(scores = scores.mapValues { 4 }) }))
    }

    @Test fun missingAndOutOfRangeScoresFailClosed() {
        listOf(scores - Dimension.MEDIATION, scores + (Dimension.ACCURACY to 5),
            scores + (Dimension.FLUENCY to -1), emptyMap()).forEach { invalid ->
            assertFalse(MasteryEngine.isMastered(explain, mastery().map { it.copy(scores = invalid) }))
        }
    }

    @Test fun oneBadAttemptCannotBorrowScoresFromAnother() {
        val records = mastery().mapIndexed { i, a ->
            a.copy(scores = scores + (Dimension.values()[i] to 2))
        }
        assertFalse(MasteryEngine.isMastered(explain, records))
    }

    @Test fun oneContextIsInsufficient() {
        assertFalse(MasteryEngine.isMastered(explain, mastery().map { it.copy(context = ContextTrack.GENERAL) }))
    }

    @Test fun repeatedSameDayTasksDoNotSatisfySpacing() {
        assertFalse(MasteryEngine.isMastered(explain, mastery().map { it.copy(timestamp = 0) }))
    }

    @Test fun delayedUnfamiliarSuccessRequiresFullSevenDays() {
        val early = mastery().dropLast(1) + mastery().last().copy(timestamp = 7 * day - 1)
        assertFalse(MasteryEngine.isMastered(explain, early))
        assertTrue(MasteryEngine.isMastered(explain, mastery()))
    }

    @Test fun unfamiliarityAndDelayMustCoincideInSuccessfulEvidence() {
        val familiarDelayed = mastery().map { it.copy(unfamiliar = it.timestamp == 0L) }
        assertFalse(MasteryEngine.isMastered(explain, familiarDelayed))
        val assistedDelayed = mastery().dropLast(1) + mastery().last().copy(assisted = true)
        assertFalse(MasteryEngine.isMastered(explain, assistedDelayed))
    }

    @Test fun delayStartsWithHumanSuccessNotEarlierQuiz() {
        val quiz = attempt(0, 0).copy(source = EvidenceSource.AUTOMATIC_QUIZ)
        val records = listOf(quiz, attempt(1, 6 * day), attempt(2, 7 * day, context = ContextTrack.SOCIAL),
            attempt(3, 8 * day))
        assertFalse(MasteryEngine.isMastered(explain, records))
    }

    @Test fun malformedIdentityResponseAndNegativeTimeAreNotEvidence() {
        listOf<(Attempt) -> Attempt>(
            { it.copy(id = " ") }, { it.copy(taskId = "") }, { it.copy(response = " ") },
            { it.copy(timestamp = -1) }, { it.copy(unitId = "other") }
        ).forEach { mutate -> assertFalse(MasteryEngine.isMastered(explain, mastery().map(mutate))) }
    }

    @Test fun outlinesNeverBecomeMasteredEvenWithExcellentEvidence() {
        val outline = CurriculumUnit("outline", "Entwurf", 1, emptyList(), listOf("Ziel"),
            emptyMap(), null)
        assertFalse(MasteryEngine.isMastered(outline, mastery(outline)))
        assertFalse(MasteryEngine.isUnlocked(outline, mastery(outline)))
        assertNull(MasteryEngine.nextReview(mastery(outline), outline.id))
    }

    @Test fun prerequisitesUnlockOnlyAfterMastery() {
        assertTrue(MasteryEngine.isUnlocked(explain, emptyList()))
        assertFalse(MasteryEngine.isUnlocked(justify, emptyList()))
        assertFalse(MasteryEngine.isUnlocked(justify, mastery().take(2)))
        assertTrue(MasteryEngine.isUnlocked(justify, mastery()))
        assertTrue(MasteryEngine.isUnlocked(uncertainty, mastery()))
        assertFalse(MasteryEngine.isUnlocked(feedback, mastery() + mastery(justify)))
        assertTrue(MasteryEngine.isUnlocked(feedback, mastery() + mastery(justify) + mastery(uncertainty)))
    }

    @Test fun laterStageGateRequiresAllEarlierAuthoredCompetenciesNotOutlines() {
        val later = explain.copy(id = "future-authored", stage = 2, prerequisites = emptyList())
        assertFalse(MasteryEngine.isUnlocked(later, mastery()))
        val all = CurriculumCatalogue.units.filter { it.stage == 1 }.flatMap { mastery(it) }
        assertTrue(MasteryEngine.isUnlocked(later, all))
    }

    @Test fun missingOutlineAndCyclicPrerequisitesFailClosed() {
        assertFalse(MasteryEngine.isUnlocked(explain.copy(prerequisites = listOf("missing")), emptyList()))
        assertFalse(MasteryEngine.isUnlocked(explain.copy(prerequisites = listOf("s0-profile")), emptyList()))
        assertFalse(MasteryEngine.isUnlocked(explain.copy(prerequisites = listOf(explain.id)), mastery()))
        assertFalse(MasteryEngine.isUnlocked(explain.copy(stage = 7), mastery()))
    }

    @Test fun retrievalIntervalsGrowWithHumanSuccesses() {
        val first = attempt(1, 0)
        val second = attempt(2, day)
        val third = attempt(3, 4 * day)
        val fourth = attempt(4, 11 * day)
        val fifth = attempt(5, 25 * day)
        assertNull(MasteryEngine.nextReview(emptyList(), explain.id))
        assertNull(MasteryEngine.nextReview(listOf(first), "missing"))
        assertEquals(day, MasteryEngine.nextReview(listOf(first), explain.id))
        assertEquals(4 * day, MasteryEngine.nextReview(listOf(first, second), explain.id))
        assertEquals(11 * day, MasteryEngine.nextReview(listOf(first, second, third), explain.id))
        assertEquals(32 * day, MasteryEngine.nextReview(listOf(first, second, third, fourth), explain.id))
        assertEquals(46 * day, MasteryEngine.nextReview(listOf(first, second, third, fourth, fifth), explain.id))
    }

    @Test fun quizSelfAndAssistedAttemptsDoNotPostponeRetrieval() {
        val baseline = mastery()
        val due = MasteryEngine.nextReview(baseline, explain.id)
        listOf(
            attempt(4, 13 * day).copy(source = EvidenceSource.AUTOMATIC_QUIZ),
            attempt(5, 13 * day).copy(source = EvidenceSource.SELF_REFLECTION),
            attempt(6, 13 * day).copy(assisted = true),
            attempt(7, 13 * day).copy(independentProduction = false)
        ).forEach { assertEquals(due, MasteryEngine.nextReview(baseline + it, explain.id)) }
    }

    @Test fun quizOnlyHistoryHasShortRetrievalNotMastery() {
        val quiz = attempt(1, 0).copy(source = EvidenceSource.AUTOMATIC_QUIZ)
        val repeat = quiz.copy(id = "quiz-2", timestamp = 10 * day)
        assertEquals(day, MasteryEngine.nextReview(listOf(quiz, repeat), explain.id))
        assertFalse(MasteryEngine.isMastered(explain, listOf(quiz, repeat)))
    }

    @Test fun humanFailureResetsRetrievalInterval() {
        val failed = attempt(4, 8 * day).copy(scores = scores + (Dimension.ACCURACY to 2))
        assertEquals(9 * day, MasteryEngine.nextReview(mastery() + failed, explain.id))
    }

    @Test fun errorsAreRecycledEvenWhenReportedBySelfOrQuiz() {
        listOf(EvidenceSource.SELF_REFLECTION, EvidenceSource.AUTOMATIC_QUIZ).forEach { source ->
            val error = attempt(4, 8 * day).copy(source = source, errors = listOf("Verbposition nach deshalb"))
            assertEquals(9 * day, MasteryEngine.nextReview(mastery() + error, explain.id))
        }
    }

    @Test fun subsequentIndependentHumanSuccessClearsOldErrorPriority() {
        val error = attempt(4, 8 * day).copy(source = EvidenceSource.SELF_REFLECTION, errors = listOf("Kasus"))
        val repaired = attempt(5, 9 * day)
        assertEquals(30 * day, MasteryEngine.nextReview(mastery() + error + repaired, explain.id))
    }

    @Test fun errorsInOtherwiseSuccessfulProductionStillTriggerRecycling() {
        val error = attempt(4, 8 * day).copy(errors = listOf("Kollokation"))
        assertEquals(9 * day, MasteryEngine.nextReview(mastery() + error, explain.id))
    }

    @Test fun dueRetrievalPrecedesNewLearningAndIncludesMasteredUnits() {
        assertEquals(justify.id, MasteryEngine.nextUnit(mastery(), 7 * day)?.id)
        assertEquals(explain.id, MasteryEngine.nextUnit(mastery(), 14 * day)?.id)
        val error = attempt(4, 8 * day, uncertainty).copy(
            source = EvidenceSource.SELF_REFLECTION, errors = listOf("Unbelegte Gewissheit"))
        assertEquals(uncertainty.id, MasteryEngine.nextUnit(mastery() + error, 9 * day)?.id)
    }

    @Test fun nextUnitIgnoresFutureEvidenceAndNeverSchedulesOrientationMaterial() {
        assertEquals(explain.id, MasteryEngine.nextUnit(mastery(), 0)?.id)
        val all = CurriculumCatalogue.units.filter { it.stage == 1 }.flatMap { mastery(it) }
        assertEquals("s2-data", MasteryEngine.nextUnit(all, 7 * day)?.id)
        assertEquals("s0-repair", MasteryEngine.nextUnit(all, 14 * day)?.id)
        val orientation = CurriculumCatalogue.units.first { it.id == "s0-profile" }
        assertFalse(MasteryEngine.isMastered(orientation, mastery(orientation)))
        assertNull(MasteryEngine.nextReview(mastery(orientation), orientation.id))
    }

    @Test fun reviewTimeSaturatesInsteadOfOverflowing() {
        assertEquals(Long.MAX_VALUE, MasteryEngine.nextReview(listOf(attempt(1, Long.MAX_VALUE)), explain.id))
    }

    @Test fun fourStarterUnitsAloneNeverGrantGraduation() {
        val all = listOf(explain, justify, uncertainty, feedback).flatMap { mastery(it) }
        val gaps = MasteryEngine.graduationGaps(all)
        assertEquals(22, gaps.count { it.startsWith("Noch nicht nachgewiesen:") })
        assertEquals(0, gaps.count { it.contains("nur Entwürfe") })
        assertEquals(5, gaps.count { it.contains("Abschluss-Checkpoint fehlt") })
    }

    @Test fun graduationNeedsRepeatedGeneralSocialEngineeringFootballAndIntegratedCheckpoints() {
        assertEquals(5, checkpointGaps(emptyList()).size)
        assertEquals(4, checkpointGaps(checkpointPair()).size)
        assertEquals(0, checkpointGaps(allCheckpoints()).size)
        assertEquals(0, checkpointGaps(allCheckpoints().reversed()).size)
        ContextTrack.values().forEach { context ->
            val missing = allCheckpoints().filterNot {
                it.context == context && it.taskId.startsWith("${checkpointPrefix(context)}:")
            }
            assertEquals(1, checkpointGaps(missing).size)
            assertTrue(hasCheckpointGap(missing, context))
        }
    }

    @Test fun formalGraduationRemainsBlockedWithoutIndependentCurriculumAndAssessmentValidation() {
        assertTrue(
            MasteryEngine.FORMAL_VALIDATION_GAP in
                MasteryEngine.graduationGaps(allCheckpoints())
        )
    }

    @Test fun engineeringCannotSubstituteForFootballOrSocial() {
        val engineering = checkpointPair(ContextTrack.ENGINEERING) +
            checkpoint(ContextTrack.ENGINEERING, n = 3) + checkpoint(ContextTrack.ENGINEERING, n = 4)
        val records = checkpointPair() + engineering +
            checkpointPair(prefix = MasteryEngine.INTEGRATED_CHECKPOINT_TASK_ID)
        assertEquals(2, checkpointGaps(records).size)
        assertFalse(hasCheckpointGap(records, ContextTrack.ENGINEERING))
        assertTrue(hasCheckpointGap(records, ContextTrack.COACHING))
        assertTrue(hasCheckpointGap(records, ContextTrack.SOCIAL))
    }

    @Test fun footballCannotSubstituteForEngineeringAndSpecialistTasksCannotCountAsSocial() {
        val football = checkpointPair(ContextTrack.COACHING)
        assertFalse(hasCheckpointGap(football, ContextTrack.COACHING))
        assertTrue(hasCheckpointGap(football, ContextTrack.ENGINEERING))
        val mislabelled = football.map { it.copy(context = ContextTrack.SOCIAL) }
        assertTrue(hasCheckpointGap(mislabelled, ContextTrack.SOCIAL))
        assertTrue(hasCheckpointGap(mislabelled, ContextTrack.COACHING))
    }

    @Test fun integratedGeneralAssessmentIsSeparateFromContextSpecificGeneralTasks() {
        val onlyContexts = ContextTrack.values().flatMap { checkpointPair(it) }
        assertEquals(1, checkpointGaps(onlyContexts).size)
        assertTrue(hasCheckpointGap(onlyContexts, ContextTrack.GENERAL, MasteryEngine.INTEGRATED_CHECKPOINT_TASK_ID))
        val onlyIntegrated = checkpointPair(prefix = MasteryEngine.INTEGRATED_CHECKPOINT_TASK_ID)
        assertTrue(hasCheckpointGap(onlyIntegrated, ContextTrack.GENERAL))
        assertFalse(hasCheckpointGap(onlyIntegrated, ContextTrack.GENERAL, MasteryEngine.INTEGRATED_CHECKPOINT_TASK_ID))
        val wrongContext = allCheckpoints().map {
            if (it.taskId.startsWith("${MasteryEngine.INTEGRATED_CHECKPOINT_TASK_ID}:"))
                it.copy(context = ContextTrack.ENGINEERING) else it
        }
        assertEquals(1, checkpointGaps(wrongContext).size)
    }

    @Test fun oneSuccessPerCheckpointStreamIsInsufficient() {
        val singles = allCheckpoints().filter { it.timestamp == 30 * day }
        assertEquals(5, singles.size)
        assertEquals(5, checkpointGaps(singles).size)
    }

    @Test fun sameDayRepeatedDistinctTasksCannotSatisfyAnyCheckpoint() {
        val sameDay = allCheckpoints().mapIndexed { index, a -> a.copy(timestamp = 30 * day + index * 1000L) }
        assertEquals(5, checkpointGaps(sameDay).size)
    }

    @Test fun distinctTaskAndDayMustBelongToTheSameCheckpointStream() {
        val general = listOf(checkpoint(), checkpoint(n = 2).copy(timestamp = 30 * day))
        val social = checkpointPair(ContextTrack.SOCIAL)
        assertTrue(hasCheckpointGap(general + social, ContextTrack.GENERAL))
        assertFalse(hasCheckpointGap(general + social, ContextTrack.SOCIAL))
    }

    @Test fun repeatedSameTaskOnDifferentDaysAndDuplicateEvidenceDoNotInflateCoverage() {
        val singles = allCheckpoints().filter { it.timestamp == 30 * day }
        val repeated = singles + singles.map { it.copy(id = "${it.id}-repeat", timestamp = 31 * day) }
        assertEquals(5, checkpointGaps(repeated).size)
        assertEquals(5, checkpointGaps(singles + singles).size)
        assertEquals(0, checkpointGaps(allCheckpoints() + allCheckpoints()).size)
        val conflictingIds = allCheckpoints().map { it.copy(id = "${it.context}-${it.taskId.substringBefore(':')}") }
        assertEquals(5, checkpointGaps(conflictingIds).size)
    }

    @Test fun legacyBareCheckpointIdsCountAsOneDistinctTaskOnly() {
        val mixed = allCheckpoints().map {
            if (it.timestamp == 30 * day) it.copy(taskId = it.taskId.substringBefore(':')) else it
        }
        assertEquals(0, checkpointGaps(mixed).size)
        val onlyBare = mixed.map { it.copy(taskId = it.taskId.substringBefore(':')) }
        assertEquals(5, checkpointGaps(onlyBare).size)
    }

    @Test fun taskPrefixesRequireAnExactFamilyAndNonblankVariant() {
        listOf<(String) -> String>(
            { it.substringBefore(':') + ":" },
            { it.substringBefore(':') + ":   " },
            { it.replace(":", "-other:") },
            { "unrelated-$it" }
        ).forEach { mutate ->
            assertEquals(5, checkpointGaps(allCheckpoints().map { it.copy(taskId = mutate(it.taskId)) }).size)
        }
    }

    @Test fun graduationRejectsQuizAssistedFamiliarAndWeakCheckpoints() {
        listOf<(Attempt) -> Attempt>(
            { it.copy(source = EvidenceSource.AUTOMATIC_QUIZ) },
            { it.copy(source = EvidenceSource.SELF_REFLECTION) },
            { it.copy(assisted = true) }, { it.copy(unfamiliar = false) },
            { it.copy(independentProduction = false) },
            { it.copy(scores = scores + (Dimension.REGISTER to 2)) },
            { it.copy(unitId = explain.id) }, { it.copy(taskId = "ordinary-lesson-task") },
            { it.copy(response = " ") }, { it.copy(id = "") }, { it.copy(timestamp = -1) }
        ).forEach { mutate ->
            assertEquals(5, checkpointGaps(allCheckpoints().map(mutate)).size)
        }
    }

    @Test fun selfAndQuizSuccessesCannotSupplyTheSecondHumanSuccess() {
        val singles = allCheckpoints().filter { it.timestamp == 30 * day }
        val seconds = allCheckpoints().filter { it.timestamp == 31 * day }
        val inflated = singles + listOf(EvidenceSource.AUTOMATIC_QUIZ, EvidenceSource.SELF_REFLECTION)
            .flatMap { source -> seconds.map { it.copy(id = "${it.id}-$source", source = source) } }
        assertEquals(5, checkpointGaps(inflated).size)
        assertEquals(0, checkpointGaps(inflated + seconds).size)
    }

    @Test fun everyDimensionMustPassOnEveryQualifyingIntegratedAssessment() {
        Dimension.values().forEach { weak ->
            val records = allCheckpoints().map {
                if (it.taskId.startsWith("${MasteryEngine.INTEGRATED_CHECKPOINT_TASK_ID}:") &&
                    it.timestamp == 31 * day)
                    it.copy(scores = Dimension.values().associateWith { d -> if (d == weak) 2 else 4 })
                else it
            }
            assertEquals("Integrated weak dimension $weak", 1, checkpointGaps(records).size)
            assertTrue(hasCheckpointGap(records, ContextTrack.GENERAL, MasteryEngine.INTEGRATED_CHECKPOINT_TASK_ID))
        }
        listOf(scores - Dimension.MEDIATION, scores + (Dimension.COMPREHENSION to 5)).forEach { invalid ->
            val records = allCheckpoints().map {
                if (it.taskId.startsWith("${MasteryEngine.INTEGRATED_CHECKPOINT_TASK_ID}:")) it.copy(scores = invalid) else it
            }
            assertEquals(1, checkpointGaps(records).size)
        }
    }

    @Test fun dimensionsCannotBePooledBetweenIntegratedTasks() {
        val records = allCheckpoints().map {
            if (it.taskId.startsWith("${MasteryEngine.INTEGRATED_CHECKPOINT_TASK_ID}:")) {
                val weak = if (it.timestamp == 30 * day) Dimension.COMPREHENSION else Dimension.INTERACTION
                it.copy(scores = Dimension.values().associateWith { d -> if (d == weak) 2 else 4 })
            } else it
        }
        assertTrue(hasCheckpointGap(records, ContextTrack.GENERAL, MasteryEngine.INTEGRATED_CHECKPOINT_TASK_ID))
    }

    @Test fun allCheckpointsStillCannotBypassOutlineBlockers() {
        val mastered = listOf(explain, justify, uncertainty, feedback).flatMap { mastery(it) }
        val gaps = MasteryEngine.graduationGaps(mastered + allCheckpoints())
        assertEquals(0, checkpointGaps(mastered + allCheckpoints()).size)
        assertEquals(22, gaps.count { it.startsWith("Noch nicht nachgewiesen:") })
        assertTrue(MasteryEngine.FORMAL_VALIDATION_GAP in gaps)
    }

    @Test fun gapsDescribeEveryRequiredContextRepetitionAndInternalSourceProvenance() {
        val gaps = checkpointGaps(emptyList())
        assertEquals(5, gaps.size)
        ContextTrack.values().forEach { context ->
            assertTrue(gaps.any { it.contains("Kontext: ${context.name}.") })
        }
        assertTrue(gaps.any { it.contains("Automotive") })
        assertTrue(gaps.any { it.contains("Jugendfußball") })
        assertTrue(gaps.any { it.contains("Verstehen, Produktion, Interaktion und Mediation") })
        assertTrue(gaps.all { it.contains("zwei verschiedene unbekannte Aufgaben an verschiedenen UTC-Tagen") })
        assertTrue(gaps.all { it.contains("allen zehn Dimensionen mindestens 3/4") })
        assertTrue(gaps.all { it.contains("gespeicherter Quelle HUMAN_REVIEW") && it.contains("keine externe Identitätsprüfung") })
    }
}
