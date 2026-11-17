import type {
  Attempt,
  CurriculumUnit,
  DiagnosticCheckpoint,
  LearnerError,
} from '../types';
import { coreUnits, curriculumUnits } from '../data/curriculumCatalogue';
import { AdaptiveLearning } from './adaptiveLearning';
import { MasteryEngine } from './masteryEngine';
import { RetrievalPolicy } from './retrievalPolicy';

const PRODUCTION_TASKS = new Set([
  'retrieval',
  'controlled',
  'speaking',
  'interaction',
  'writing',
  'transfer',
]);

function plusDays(timestamp: number, days: number): number {
  return timestamp + days * MasteryEngine.DAY_MILLIS;
}

export const AdaptiveDiagnosticRoute = {
  evaluate(
    unit: CurriculumUnit,
    attempts: Attempt[],
    now: number,
  ): DiagnosticCheckpoint {
    if (unit.content === null || unit.stage < 0 || unit.stage > 6) {
      return { state: 'UNAVAILABLE', completedProductionTasks: 0, feedback: [] };
    }

    const history = attempts.filter(
      (a) =>
        a.unitId === unit.id && a.timestamp <= now && a.response.trim().length > 0,
    );
    const quizzes = history.filter((a) => a.taskId === 'quiz');
    const latestQuiz = quizzes.sort((a, b) => b.timestamp - a.timestamp)[0];

    if (!latestQuiz) {
      return {
        state: 'NEEDS_OBJECTIVE_CHECK',
        completedProductionTasks: 0,
        feedback: [],
      };
    }

    if (
      latestQuiz.source !== 'AUTOMATIC_QUIZ' ||
      latestQuiz.scores.COMPREHENSION !== 4
    ) {
      return {
        state: 'NEEDS_REMEDIATION',
        completedProductionTasks: 0,
        feedback: ['Die Verständnisfrage braucht einen erneuten Abruf.'],
      };
    }

    const latestByTask = new Map<string, Attempt>();
    for (const a of history) {
      if (
        a.source === 'SELF_REFLECTION' &&
        PRODUCTION_TASKS.has(a.taskId) &&
        !a.assisted
      ) {
        const prev = latestByTask.get(a.taskId);
        if (!prev || a.timestamp > prev.timestamp) {
          latestByTask.set(a.taskId, a);
        }
      }
    }
    const latestProduction = Array.from(latestByTask.values());

    const detectedByCode = new Map<string, LearnerError>();
    for (const a of latestProduction) {
      for (const err of AdaptiveLearning.detectResponseSignals(a.response, unit)) {
        if (!detectedByCode.has(err.code)) {
          detectedByCode.set(err.code, err);
        }
      }
    }
    const detectedErrors = Array.from(detectedByCode.values());

    if (detectedErrors.length > 0) {
      return {
        state: 'NEEDS_REMEDIATION',
        completedProductionTasks: latestProduction.length,
        feedback: detectedErrors.map((e) => `${e.label}: ${e.remediation}`),
      };
    }

    if (latestProduction.length < 2) {
      return {
        state: 'NEEDS_PRODUCTION',
        completedProductionTasks: latestProduction.length,
        feedback: [],
      };
    }

    return {
      state: 'COMPLETE',
      completedProductionTasks: latestProduction.length,
      feedback: [],
    };
  },

  isCheckpointComplete(
    unit: CurriculumUnit,
    attempts: Attempt[],
    now: number,
  ): boolean {
    return (
      AdaptiveDiagnosticRoute.evaluate(unit, attempts, now).state === 'COMPLETE'
    );
  },

  isUnlocked(
    unit: CurriculumUnit,
    attempts: Attempt[],
    now: number,
  ): boolean {
    const catalogue = new Map(curriculumUnits.map((u) => [u.id, u]));
    const ready = (candidate: CurriculumUnit, visiting: Set<string>): boolean => {
      if (
        candidate.content === null ||
        candidate.stage < 1 ||
        candidate.stage > 6 ||
        visiting.has(candidate.id)
      ) {
        return false;
      }
      const priorStages = coreUnits
        .filter((u) => u.stage >= 1 && u.stage < candidate.stage)
        .map((u) => u.id);
      const nextVisiting = new Set(visiting);
      nextVisiting.add(candidate.id);
      const allPrereqs = Array.from(
        new Set([...candidate.prerequisites, ...priorStages]),
      );
      return allPrereqs.every((id) => {
        const prereq = catalogue.get(id);
        return (
          prereq !== undefined &&
          AdaptiveDiagnosticRoute.isCheckpointComplete(prereq, attempts, now) &&
          ready(prereq, nextVisiting)
        );
      });
    };
    return ready(unit, new Set());
  },

  nextReview(
    attempts: Attempt[],
    unitId: string,
    now: number,
  ): number | null {
    const unit = curriculumUnits.find((u) => u.id === unitId);
    if (!unit) return null;
    const history = attempts.filter(
      (a) =>
        a.unitId === unitId && a.timestamp <= now && a.response.trim().length > 0,
    );

    const latestByTask = new Map<string, Attempt>();
    for (const a of history) {
      if (
        a.source === 'SELF_REFLECTION' &&
        PRODUCTION_TASKS.has(a.taskId) &&
        !a.assisted
      ) {
        const prev = latestByTask.get(a.taskId);
        if (!prev || a.timestamp > prev.timestamp) {
          latestByTask.set(a.taskId, a);
        }
      }
    }
    const latestProduction = Array.from(latestByTask.values());
    const unresolvedSignals = latestProduction
      .filter(
        (a) => AdaptiveLearning.detectResponseSignals(a.response, unit).length > 0,
      )
      .map((a) => a.timestamp);
    if (unresolvedSignals.length > 0) {
      return plusDays(
        Math.max(...unresolvedSignals),
        RetrievalPolicy.errorRetryDays,
      );
    }

    const quizzes = history.filter(
      (a) => a.taskId === 'quiz' && a.source === 'AUTOMATIC_QUIZ',
    );
    const latestQuiz = [...quizzes].sort((a, b) => b.timestamp - a.timestamp)[0];
    if (!latestQuiz) return null;
    if (latestQuiz.scores.COMPREHENSION !== 4) {
      return plusDays(latestQuiz.timestamp, RetrievalPolicy.errorRetryDays);
    }

    const retrievalAfterQuiz = history
      .filter(
        (a) =>
          a.taskId === 'retrieval' &&
          a.source === 'SELF_REFLECTION' &&
          !a.assisted &&
          a.timestamp > latestQuiz.timestamp,
      )
      .sort((a, b) => b.timestamp - a.timestamp)[0];

    const successfulDays = new Set(
      quizzes
        .filter((a) => a.scores.COMPREHENSION === 4)
        .map((a) => Math.floor(a.timestamp / MasteryEngine.DAY_MILLIS)),
    ).size;

    if (retrievalAfterQuiz) {
      if (
        AdaptiveLearning.detectResponseSignals(retrievalAfterQuiz.response, unit)
          .length > 0
      ) {
        return plusDays(
          retrievalAfterQuiz.timestamp,
          RetrievalPolicy.errorRetryDays,
        );
      }
      const successfulReviews = new Set(
        history
          .filter(
            (a) =>
              a.taskId === 'retrieval' &&
              a.source === 'SELF_REFLECTION' &&
              !a.assisted &&
              a.timestamp >= latestQuiz.timestamp &&
              AdaptiveLearning.detectResponseSignals(a.response, unit).length === 0,
          )
          .map((a) => Math.floor(a.timestamp / MasteryEngine.DAY_MILLIS)),
      ).size;

      const intervalIndex = Math.max(
        0,
        Math.min(
          RetrievalPolicy.intervalsDays.length - 1,
          successfulDays + successfulReviews - 1,
        ),
      );
      return plusDays(
        retrievalAfterQuiz.timestamp,
        RetrievalPolicy.intervalsDays[intervalIndex],
      );
    }

    const intervalIndex = Math.max(
      0,
      Math.min(RetrievalPolicy.intervalsDays.length - 1, successfulDays - 1),
    );
    return plusDays(
      latestQuiz.timestamp,
      RetrievalPolicy.intervalsDays[intervalIndex],
    );
  },

  completedCoreUnits(attempts: Attempt[], now: number): number {
    return coreUnits.filter((u) =>
      AdaptiveDiagnosticRoute.isCheckpointComplete(u, attempts, now),
    ).length;
  },

  completedAuthoredUnits(attempts: Attempt[], now: number): number {
    return curriculumUnits.filter(
      (u) =>
        u.content !== null &&
        AdaptiveDiagnosticRoute.isCheckpointComplete(u, attempts, now),
    ).length;
  },

  nextUnit(attempts: Attempt[], now: number): CurriculumUnit | null {
    const available = coreUnits.filter((u) =>
      AdaptiveDiagnosticRoute.isUnlocked(u, attempts, now),
    );
    const remediation = available.find(
      (unit) =>
        AdaptiveDiagnosticRoute.evaluate(unit, attempts, now).state ===
          'NEEDS_REMEDIATION' &&
        attempts.some((a) => a.unitId === unit.id && a.timestamp <= now),
    );
    if (remediation) return remediation;

    const due = available
      .map((unit) => {
        const nr = AdaptiveDiagnosticRoute.nextReview(attempts, unit.id, now);
        return nr !== null && nr <= now ? { unit, nr } : null;
      })
      .filter((x): x is { unit: CurriculumUnit; nr: number } => x !== null)
      .sort((a, b) => a.nr - b.nr || a.unit.stage - b.unit.stage);
    if (due.length > 0) return due[0].unit;

    return (
      available.find(
        (u) => !AdaptiveDiagnosticRoute.isCheckpointComplete(u, attempts, now),
      ) ?? null
    );
  },
};
