import type {
  Attempt,
  BaselineProfileRecord,
  DailyStreakState,
  ExternalListeningDraft,
  FeedbackChallenge,
  HumanReviewSubmission,
  PersistedAssessment,
  PracticeDraft,
  ProficiencySnapshot,
  ProgrammePreferences,
  RealLifeExperience,
  ReassessmentLink,
  ReviewAssignment,
  ReviewAuditEvent,
  VocabularySrsItem,
} from '../types';

export interface ProgrammeExportInput {
  attempts: Attempt[];
  experiences: RealLifeExperience[];
  assessments: PersistedAssessment[];
  externalListeningNotes: ExternalListeningDraft[];
  practiceDrafts: PracticeDraft[];
  feedbackChallenges: FeedbackChallenge[];
  humanReviews: HumanReviewSubmission[];
  reviewAssignments: ReviewAssignment[];
  reviewAuditEvents: ReviewAuditEvent[];
  reassessmentLinks: ReassessmentLink[];
  baselineProfile: BaselineProfileRecord | null;
  vocabularyItems: VocabularySrsItem[];
  proficiencySnapshots: ProficiencySnapshot[];
  dailyStreak: DailyStreakState | null;
  preferences: ProgrammePreferences;
  bookmarkedUnitIds: string[];
  assistedUnitIds: string[];
  lastUnitId: string | null;
  lastTaskId: string;
  onboardingComplete: boolean;
}

export const ProgrammeExport = {
  create(input: ProgrammeExportInput): string {
    const payload = {
      format: 'c1-deutsch-local-export-v2',
      createdAt: Date.now(),
      scope: 'User-initiated export of local learner records.',
      media: {
        audioBytesIncluded: false,
        bookAudioContentUriIncluded: false,
        credentialsIncluded: false,
      },
      preferences: {
        theme: input.preferences.theme,
        language: input.preferences.language,
        learnerName: input.preferences.learnerName,
        compact: input.preferences.compact,
        dailyMinutes: input.preferences.dailyMinutes,
        lastUnitId: input.lastUnitId,
        lastTaskId: input.lastTaskId,
        onboardingComplete: input.onboardingComplete,
        bookmarkedUnitIds: [...input.bookmarkedUnitIds].sort(),
        assistedUnitIds: [...input.assistedUnitIds].sort(),
      },
      attempts: input.attempts.map((attempt) => ({
        id: attempt.id,
        unitId: attempt.unitId,
        taskId: attempt.taskId,
        timestamp: attempt.timestamp,
        response: attempt.response,
        source: attempt.source,
        context: attempt.context,
        assisted: attempt.assisted,
        unfamiliar: attempt.unfamiliar,
        independentProduction: attempt.independentProduction,
        scores: attempt.scores,
        errors: attempt.errors,
        taskVersion: attempt.taskVersion ?? 1,
        rubricVersion: attempt.rubricVersion ?? 1,
      })),
      realLifeEntries: input.experiences,
      assessmentSessions: input.assessments.map((persisted) => ({
        id: persisted.session.id,
        kind: persisted.session.kind,
        createdAt: persisted.session.createdAt,
        durationMinutes: persisted.session.durationMinutes,
        currentIndex: persisted.currentIndex,
        completed: persisted.completed,
        updatedAt: persisted.updatedAt,
        tasks: persisted.session.tasks,
        responses: Object.fromEntries(
          Object.entries(persisted.responses).filter(
            ([key]) => !key.startsWith('assessment-audio:'),
          ),
        ),
      })),
      feedbackChallenges: input.feedbackChallenges,
      humanReviews: input.humanReviews.map((review) => ({
        id: review.id,
        attemptId: review.attemptId,
        reviewerId: review.reviewer.id,
        reviewerName: review.reviewer.displayName,
        reviewerVerified: review.reviewer.verified,
        rubricVersion: review.rubricVersion,
        scores: review.scores,
        comment: review.comment,
        createdAt: review.createdAt,
      })),
      reviewAssignments: input.reviewAssignments,
      reviewAuditEvents: input.reviewAuditEvents,
      reassessmentLinks: input.reassessmentLinks,
      legacyData: {
        baselineProfileUnvalidated: input.baselineProfile
          ? {
              ...input.baselineProfile,
              interpretation: 'Legacy record; not validated or a certification result.',
            }
          : null,
        vocabularyItems: input.vocabularyItems,
        proficiencySnapshots: input.proficiencySnapshots.map((snapshot) => ({
          ...snapshot,
          interpretation: 'Legacy record; not validated or a certification result.',
        })),
        dailyStreak: input.dailyStreak
          ? {
              currentStreak: input.dailyStreak.currentStreak,
              longestStreak: input.dailyStreak.longestStreak,
              lastActiveEpochDay: input.dailyStreak.lastActiveEpochDay,
              streakFreezesAvailable: input.dailyStreak.streakFreezesAvailable,
              totalSessionsCompleted: input.dailyStreak.totalSessionsCompleted,
              activeDaysBitmask: input.dailyStreak.activeDaysBitmask,
              lastActivityTimestamp: input.dailyStreak.lastActivityTimestamp,
            }
          : null,
      },
      externalListeningNotes: input.externalListeningNotes,
      practiceDrafts: input.practiceDrafts,
    };

    return JSON.stringify(payload, null, 2);
  },

  download(jsonString: string, timestamp = Date.now()): void {
    const blob = new Blob([jsonString], { type: 'application/json;charset=utf-8' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `c1-deutsch-export-${timestamp}.json`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    setTimeout(() => URL.revokeObjectURL(url), 1000);
  },
};
