import type {
  Attempt,
  FeedbackChallenge,
  FeedbackChallengeDecision,
  FeedbackClaim,
  FeedbackValidation,
  LearnerError,
} from '../types';

export const FeedbackEvidenceEngine = {
  claimsFor(attempt: Attempt, errors: LearnerError[]): FeedbackClaim[] {
    return errors.map((error, index) => ({
      id: `claim-${attempt.id}-${error.code}-${index}`,
      responseExcerpt: error.excerpt,
      claimType: error.code,
      proposedFeedback: `${error.explanation} ${error.remediation}`,
      alternativeAccepted: null,
      attemptId: attempt.id,
      unitId: attempt.unitId,
      taskId: attempt.taskId,
      context: attempt.context,
    }));
  },

  validate(response: string, claim: FeedbackClaim): FeedbackValidation {
    if (!claim.responseExcerpt.trim()) {
      return {
        valid: false,
        reason: 'Die Rückmeldung enthält keinen Antwortausschnitt.',
      };
    }
    if (!response.toLowerCase().includes(claim.responseExcerpt.toLowerCase())) {
      return {
        valid: false,
        reason: 'Der Antwortausschnitt ist im Original nicht belegt.',
      };
    }
    if (!claim.proposedFeedback.trim()) {
      return {
        valid: false,
        reason: 'Die Rückmeldung enthält keine begründete Erklärung.',
      };
    }
    return {
      valid: true,
      reason: 'Die Rückmeldung ist am Originalausschnitt verankert.',
    };
  },

  challenge(
    claim: FeedbackClaim,
    learnerDecision: FeedbackChallengeDecision,
    correctedFeedback: string | null,
    now: number,
  ): FeedbackChallenge {
    if (!claim.attemptId.trim() || !claim.unitId.trim() || !claim.taskId.trim()) {
      throw new Error('Feedback claim must link to its source attempt.');
    }
    if (!claim.responseExcerpt.trim() || !claim.proposedFeedback.trim()) {
      throw new Error('Feedback claim must contain the quoted evidence and proposed feedback.');
    }
    const trimmed = correctedFeedback?.trim();
    const correction = trimmed && trimmed.length > 0 ? trimmed.slice(0, 2000) : null;
    return {
      id: `challenge-${claim.id}`,
      claimId: claim.id,
      learnerDecision,
      correctedFeedback: correction,
      status: 'OPEN',
      attemptId: claim.attemptId,
      unitId: claim.unitId,
      taskId: claim.taskId,
      context: claim.context,
      responseExcerpt: claim.responseExcerpt,
      claimType: claim.claimType,
      proposedFeedback: claim.proposedFeedback,
      createdAt: now,
      updatedAt: now,
    };
  },
};
