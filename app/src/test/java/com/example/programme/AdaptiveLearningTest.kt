package com.example.programme

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AdaptiveLearningTest {
    @Test
    fun ordinaryPracticeResponseExposesDetectedSignalsToAdaptiveEvidence() {
        val unit = CurriculumCatalogue.units.first { it.id == "s1-explain" }
        val errors = AdaptiveLearning.detectResponseSignals(
            "Ich bleibe, weil ich gehe morgen.",
            unit
        )

        assertTrue(errors.any { it.code == "subordinate-verb-position" })
    }

    @Test
    fun feedbackClaimsMustReferenceTheOriginalResponse() {
        val claim = FeedbackClaim(
            id = "claim",
            responseExcerpt = "weil ich gehe morgen",
            claimType = "subordinate-verb-position",
            proposedFeedback = "Verb ans Ende setzen.",
            alternativeAccepted = null
        )

        assertTrue(
            FeedbackEvidenceEngine.validate(
                "Ich bleibe, weil ich gehe morgen.",
                claim
            ).valid
        )
        assertTrue(
            !FeedbackEvidenceEngine.validate("Alles war richtig.", claim).valid
        )
    }

    @Test
    fun generatedFeedbackClaimRetainsItsSourceAndCanBecomeAChallenge() {
        val attempt = Attempt(
            id = "attempt-17",
            unitId = unit.id,
            taskId = "writing",
            timestamp = 100,
            response = "Ich bleibe, weil ich gehe morgen.",
            source = EvidenceSource.SELF_REFLECTION
        )
        val claim = AdaptiveLearning.recommend(unit, listOf(attempt), 200)
            .feedbackClaims.single()

        assertEquals(attempt.id, claim.attemptId)
        assertTrue(FeedbackEvidenceEngine.validate(attempt.response, claim).valid)
        val challenge = FeedbackEvidenceEngine.challenge(
            claim,
            FeedbackChallengeDecision.DISAGREE,
            "Die Satzstellung soll hier anders beurteilt werden.",
            now = 300
        )
        assertEquals(attempt.id, challenge.attemptId)
        assertEquals(claim.responseExcerpt, challenge.responseExcerpt)
        assertEquals(FeedbackChallengeStatus.OPEN, challenge.status)
        assertEquals(300L, challenge.createdAt)
    }

    private val unit = CurriculumCatalogue.units.first { it.id == "s1-explain" }

    @Test fun detectsActionableGermanGrammarHintsWithoutScoringMastery() {
        val attempt = Attempt(
            id = "a", unitId = unit.id, taskId = "writing", timestamp = 1,
            response = "Ich bleibe, weil ich gehe morgen nach Hause. Es ist klar dass Das stimmt.",
            source = EvidenceSource.SELF_REFLECTION
        )
        val recommendation = AdaptiveLearning.recommend(unit, listOf(attempt), 2)
        assertTrue(recommendation.errors.any { it.code == "subordinate-verb-position" })
        assertTrue(recommendation.errors.any { it.code == "comma-dass" })
        assertEquals(AdaptiveDifficulty.CORE, recommendation.difficulty)
    }

    @Test fun repeatedWeakSignalsMoveLearnerToSupport() {
        val attempts = listOf("writing", "speaking", "interaction").mapIndexed { index, task ->
            Attempt(
                id = "a$index", unitId = unit.id, taskId = task, timestamp = index.toLong(),
                response = "Ich bleibe, weil ich gehe morgen.",
                source = EvidenceSource.SELF_REFLECTION
            )
        }
        assertEquals(
            AdaptiveDifficulty.SUPPORT,
            AdaptiveLearning.recommend(unit, attempts, 4).difficulty
        )
    }

    @Test fun noHistoryRecommendsCoreWithoutInventingEvidence() {
        val recommendation = AdaptiveLearning.recommend(unit, emptyList(), 0)
        assertEquals(AdaptiveDifficulty.CORE, recommendation.difficulty)
        assertTrue(recommendation.errors.isEmpty())
    }

    @Test fun repeatedUnflaggedTextAloneDoesNotTriggerChallengeDifficulty() {
        val attempts = (1..5).map {
            Attempt(
                id = "a$it",
                unitId = unit.id,
                taskId = "writing",
                timestamp = it.toLong(),
                response = "Ich begründe die Entscheidung und sichere den nächsten Schritt.",
                source = EvidenceSource.SELF_REFLECTION
            )
        }

        assertEquals(AdaptiveDifficulty.CORE, AdaptiveLearning.recommend(unit, attempts, 6).difficulty)
    }

    @Test fun challengeDifficultyRequiresObjectiveAndVariedPracticeEvidence() {
        val attempts = listOf(
            Attempt(
                id = "quiz",
                unitId = unit.id,
                taskId = "quiz",
                timestamp = 1,
                response = "Option 1",
                source = EvidenceSource.AUTOMATIC_QUIZ,
                scores = mapOf(Dimension.COMPREHENSION to 4)
            ),
            Attempt("writing", unit.id, "writing", 2, "Ich begründe die Entscheidung.",
                EvidenceSource.SELF_REFLECTION, independentProduction = true, context = ContextTrack.GENERAL),
            Attempt("speaking", unit.id, "speaking", 3, "Zunächst erläutere ich die Ausgangslage.",
                EvidenceSource.SELF_REFLECTION, independentProduction = true, context = ContextTrack.SOCIAL),
            Attempt("interaction", unit.id, "interaction", 4, "Ich frage nach und sichere den nächsten Schritt.",
                EvidenceSource.SELF_REFLECTION, independentProduction = true, context = ContextTrack.GENERAL)
        )

        assertEquals(AdaptiveDifficulty.CHALLENGE, AdaptiveLearning.recommend(unit, attempts, 5).difficulty)
    }

    @Test fun dueRealLifeStrugglePrioritizesSupportWithoutCreatingGrammarErrors() {
        val struggle = RealLifeExperience(
            id = "cashier",
            createdAt = 1,
            context = ContextTrack.GENERAL,
            situation = "Artikel doppelt gescannt",
            heard = "",
            intended = "Um Überprüfung bitten",
            said = "Das steht zwei mal hier",
            issueType = RealLifeIssueType.RETRIEVAL,
            reviewStage = 0,
            nextReviewAt = 2
        )

        val recommendation = AdaptiveLearning.recommend(
            unit = unit,
            attempts = emptyList(),
            now = 3,
            realLifeExperiences = listOf(struggle)
        )

        assertEquals(AdaptiveDifficulty.SUPPORT, recommendation.difficulty)
        assertTrue(recommendation.errors.isEmpty())
        assertTrue(recommendation.nextStep.contains("wort oder satz fehlte"))
    }
}
