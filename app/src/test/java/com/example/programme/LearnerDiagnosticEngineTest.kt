package com.example.programme

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LearnerDiagnosticEngineTest {
    @Test
    fun ordinaryAnswersCreateSeparateComprehensionAndFormulationFindings() {
        val attempts = listOf(
            Attempt(
                id = "quiz",
                unitId = "s1-explain",
                taskId = "quiz",
                timestamp = 1,
                response = "Option 1",
                source = EvidenceSource.AUTOMATIC_QUIZ,
                scores = mapOf(Dimension.COMPREHENSION to 4)
            ),
            Attempt(
                id = "speaking",
                unitId = "s1-explain",
                taskId = "speaking",
                timestamp = 2,
                response = "Ich erkläre den Ablauf und prüfe danach das Verständnis.",
                source = EvidenceSource.SELF_REFLECTION
            )
        )

        val profile = LearnerDiagnosticEngine.analyze(attempts, emptyList(), 3)

        assertEquals(
            DiagnosticStatus.DEMONSTRATED,
            profile.finding(DiagnosticDimension.SITUATIONAL_MEANING)?.status
        )
        assertEquals(
            DiagnosticStatus.NOT_OBSERVED,
            profile.finding(DiagnosticDimension.SENTENCE_FORMULATION)?.status
        )
    }

    @Test
    fun repeatedRealLifeRepairReportsBecomeAFragilePattern() {
        val experiences = listOf(
            experience("one", 1),
            experience("two", 2),
            experience("three", 3)
        )

        val finding = LearnerDiagnosticEngine.analyze(emptyList(), experiences, 4)
            .finding(DiagnosticDimension.CONVERSATIONAL_REPAIR)

        assertTrue(finding != null)
        assertEquals(DiagnosticStatus.FRAGILE, finding?.status)
        assertEquals(3, finding?.sampleCount)
    }

    @Test
    fun oneListeningExperienceCountsAsOneObservation() {
        val listening = experience("single-listening", 1).copy(
            heard = "",
            issueType = RealLifeIssueType.LISTENING
        )

        val finding = LearnerDiagnosticEngine.analyze(emptyList(), listOf(listening), 2)
            .finding(DiagnosticDimension.WORD_RECOGNITION)

        assertEquals(1, finding?.sampleCount)
        assertEquals(DiagnosticConfidence.LOW, finding?.confidence)
        assertEquals("Gesprochenes wurde nicht sicher erkannt.", finding?.evidence?.single())
    }

    @Test
    fun futureEvidenceCannotInfluenceTheCurrentDiagnosticProfile() {
        val futureAttempt = Attempt(
            id = "future-writing",
            unitId = "s1-explain",
            taskId = "writing",
            timestamp = 101,
            response = "Ich bleibe, weil ich gehe morgen.",
            source = EvidenceSource.SELF_REFLECTION,
            errors = listOf("subordinate-verb-position")
        )
        val futureExperience = experience("future-repair", 102)

        val profile = LearnerDiagnosticEngine.analyze(
            attempts = listOf(futureAttempt),
            experiences = listOf(futureExperience),
            now = 100
        )

        assertTrue(profile.findings.isEmpty())
    }

    @Test
    fun typedRoleplayDoesNotProveLiveInteractionOrResponseTiming() {
        val profile = LearnerDiagnosticEngine.analyze(
            attempts = listOf(
                Attempt(
                    id = "interaction",
                    unitId = "u1",
                    taskId = "interaction",
                    timestamp = 1,
                    response = "Äh, wie bitte? Anders gesagt, meinen Sie die Frist?",
                    source = EvidenceSource.SELF_REFLECTION
                )
            ),
            experiences = emptyList(),
            now = 2
        )

        assertEquals(
            DiagnosticStatus.NOT_OBSERVED,
            profile.finding(DiagnosticDimension.SPONTANEOUS_RESPONSE)?.status
        )
        assertTrue(profile.finding(DiagnosticDimension.CONVERSATIONAL_REPAIR) == null)
    }

    @Test
    fun recognizedGrammarErrorIsFragileButNoRecognizedPatternIsNotDemonstrated() {
        val unit = CurriculumCatalogue.coreUnits.first()
        val clean = Attempt(
            id = "clean",
            unitId = unit.id,
            taskId = "writing",
            timestamp = 1,
            response = "Ich erläutere die Ausgangslage und begründe den nächsten Schritt.",
            source = EvidenceSource.SELF_REFLECTION
        )
        val issue = clean.copy(
            id = "issue",
            timestamp = 2,
            response = "Ich bleibe, weil ich gehe morgen.",
            errors = listOf("subordinate-verb-position")
        )
        val cleanFinding = LearnerDiagnosticEngine.analyze(listOf(clean), emptyList(), 3)
            .finding(DiagnosticDimension.SENTENCE_FORMULATION)
        val issueFinding = LearnerDiagnosticEngine.analyze(listOf(issue), emptyList(), 3)
            .finding(DiagnosticDimension.SENTENCE_FORMULATION)

        assertEquals(DiagnosticStatus.NOT_OBSERVED, cleanFinding?.status)
        assertEquals(DiagnosticStatus.FRAGILE, issueFinding?.status)
    }

    @Test
    fun objectiveDiagnosticFindingChangesTheNextPracticePriority() {
        val quizFailure = Attempt(
            id = "quiz-failure",
            unitId = "s1-explain",
            taskId = "quiz",
            timestamp = 1,
            response = "Option 2",
            source = EvidenceSource.AUTOMATIC_QUIZ,
            scores = mapOf(Dimension.COMPREHENSION to 0)
        )

        val decision = AdaptiveControlEngine.decide(listOf(quizFailure), emptyList(), 2)

        assertEquals("quiz", decision.primaryTaskId)
        assertTrue(decision.reason.contains("Fragiles Signal"))
    }

    private fun experience(id: String, createdAt: Long) = RealLifeExperience(
        id = id,
        createdAt = createdAt,
        context = ContextTrack.GENERAL,
        situation = "Gespräch",
        heard = "",
        intended = "Nachfragen",
        said = "Ich habe nicht verstanden.",
        issueType = RealLifeIssueType.REPAIR,
        reviewStage = 0,
        nextReviewAt = createdAt + 1
    )
}
