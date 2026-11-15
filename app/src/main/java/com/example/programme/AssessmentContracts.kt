package com.example.programme

enum class AssessmentEvidence {
    AUDIO_RESPONSE,
    TEXT_RESPONSE,
    MULTI_TURN_RESPONSE,
    DELAYED_RETRIEVAL,
    UNFAMILIAR_TRANSFER,
    REAL_LIFE_REFLECTION
}

data class DiagnosticTaskContract(
    val id: String,
    val dimension: DiagnosticDimension,
    val evidence: AssessmentEvidence,
    val repetitions: Int,
    val independent: Boolean
)

object DiagnosticTaskBank {
    val contracts = DiagnosticDimension.values().flatMap { dimension ->
        listOf(
            DiagnosticTaskContract("${dimension.name.lowercase()}-controlled", dimension,
                AssessmentEvidence.TEXT_RESPONSE, 2, true),
            DiagnosticTaskContract("${dimension.name.lowercase()}-transfer", dimension,
                AssessmentEvidence.UNFAMILIAR_TRANSFER, 2, true)
        )
    }
}

data class FeedbackClaim(
    val id: String,
    val responseExcerpt: String,
    val claimType: String,
    val proposedFeedback: String,
    val alternativeAccepted: Boolean?,
    val attemptId: String = "",
    val unitId: String = "",
    val taskId: String = "",
    val context: ContextTrack = ContextTrack.GENERAL
)

enum class FeedbackChallengeStatus { OPEN, ACCEPTED, REJECTED, NEEDS_REASSESSMENT }

enum class FeedbackChallengeDecision { DISAGREE, UNSURE }

data class FeedbackChallenge(
    val claimId: String,
    val learnerDecision: String?,
    val correctedFeedback: String?,
    val status: FeedbackChallengeStatus,
    val id: String,
    val attemptId: String,
    val unitId: String,
    val taskId: String,
    val context: ContextTrack,
    val responseExcerpt: String,
    val claimType: String,
    val proposedFeedback: String,
    val createdAt: Long,
    val updatedAt: Long
)

data class FeedbackValidation(
    val valid: Boolean,
    val reason: String
)

object FeedbackEvidenceEngine {
    fun claimsFor(attempt: Attempt, errors: List<LearnerError>): List<FeedbackClaim> =
        errors.mapIndexed { index, error ->
            FeedbackClaim(
                id = "claim-${attempt.id}-${error.code}-$index",
                responseExcerpt = error.excerpt,
                claimType = error.code,
                proposedFeedback = "${error.explanation} ${error.remediation}",
                alternativeAccepted = null,
                attemptId = attempt.id,
                unitId = attempt.unitId,
                taskId = attempt.taskId,
                context = attempt.context
            )
        }

    fun validate(response: String, claim: FeedbackClaim): FeedbackValidation {
        if (claim.responseExcerpt.isBlank()) {
            return FeedbackValidation(false, "Die Rückmeldung enthält keinen Antwortausschnitt.")
        }
        if (!response.contains(claim.responseExcerpt, ignoreCase = true)) {
            return FeedbackValidation(false, "Der Antwortausschnitt ist im Original nicht belegt.")
        }
        if (claim.proposedFeedback.isBlank()) {
            return FeedbackValidation(false, "Die Rückmeldung enthält keine begründete Erklärung.")
        }
        return FeedbackValidation(true, "Die Rückmeldung ist am Originalausschnitt verankert.")
    }

    fun challenge(
        claim: FeedbackClaim,
        learnerDecision: FeedbackChallengeDecision,
        correctedFeedback: String?,
        now: Long
    ): FeedbackChallenge {
        require(claim.attemptId.isNotBlank() && claim.unitId.isNotBlank() && claim.taskId.isNotBlank()) {
            "Feedback claim must link to its source attempt."
        }
        require(claim.responseExcerpt.isNotBlank() && claim.proposedFeedback.isNotBlank()) {
            "Feedback claim must contain the quoted evidence and proposed feedback."
        }
        val correction = correctedFeedback?.trim()?.takeIf { it.isNotBlank() }?.take(2_000)
        return FeedbackChallenge(
            claimId = claim.id,
            learnerDecision = learnerDecision.name,
            correctedFeedback = correction,
            status = FeedbackChallengeStatus.OPEN,
            id = "challenge-${claim.id}",
            attemptId = claim.attemptId,
            unitId = claim.unitId,
            taskId = claim.taskId,
            context = claim.context,
            responseExcerpt = claim.responseExcerpt,
            claimType = claim.claimType,
            proposedFeedback = claim.proposedFeedback,
            createdAt = now,
            updatedAt = now
        )
    }
}

data class DelayedMasteryCheckpoint(
    val skill: DiagnosticDimension,
    val requiredIntervalsDays: List<Int> = RetrievalPolicy.intervalsDays,
    val requiredIndependentSuccesses: Int = 3,
    val requiresUnfamiliarTransfer: Boolean = true
)

object MasteryAssessmentContracts {
    val checkpoints = DiagnosticDimension.values().map {
        DelayedMasteryCheckpoint(it)
    }
}

enum class GoetheModule { LISTENING, READING, WRITING, SPEAKING }

data class GoetheTaskContract(
    val module: GoetheModule,
    val minutes: Int,
    val taskType: String,
    val requiredEvidence: Set<AssessmentEvidence>,
    val usesProtectedMaterial: Boolean = false
)

object GoetheC1TaskContracts {
    val modules = listOf(
        GoetheTaskContract(GoetheModule.LISTENING, 40, "Podcast, interview, discussion or lecture comprehension",
            setOf(AssessmentEvidence.AUDIO_RESPONSE)),
        GoetheTaskContract(GoetheModule.READING, 65, "Complex text, argument and implicit meaning comprehension",
            setOf(AssessmentEvidence.TEXT_RESPONSE)),
        GoetheTaskContract(GoetheModule.WRITING, 75, "Opinion/discussion text plus formal or semi-formal message",
            setOf(AssessmentEvidence.TEXT_RESPONSE)),
        GoetheTaskContract(GoetheModule.SPEAKING, 20, "Prepared presentation followed by discussion",
            setOf(AssessmentEvidence.MULTI_TURN_RESPONSE))
    )
}

data class PilotWeek(
    val week: Int,
    val requiredEvidence: Set<AssessmentEvidence>,
    val focus: String
)

object FourWeekPilotProtocol {
    val weeks = listOf(
        PilotWeek(0, setOf(AssessmentEvidence.TEXT_RESPONSE, AssessmentEvidence.AUDIO_RESPONSE),
            "Initial profile from ordinary answers and equivalent unfamiliar tasks"),
        PilotWeek(1, setOf(AssessmentEvidence.AUDIO_RESPONSE, AssessmentEvidence.MULTI_TURN_RESPONSE),
            "Listening and spontaneous interaction"),
        PilotWeek(2, setOf(AssessmentEvidence.DELAYED_RETRIEVAL, AssessmentEvidence.REAL_LIFE_REFLECTION),
            "Delayed retrieval and real-life transfer"),
        PilotWeek(3, setOf(AssessmentEvidence.UNFAMILIAR_TRANSFER, AssessmentEvidence.MULTI_TURN_RESPONSE),
            "Final equivalent unfamiliar tasks and change report")
    )
}
