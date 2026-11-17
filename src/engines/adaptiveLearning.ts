import type {
  AdaptiveDifficulty,
  AdaptiveRecommendation,
  Attempt,
  CurriculumUnit,
  ErrorRecyclingPlan,
  FeedbackClaim,
  LearnerError,
  RealLifeExperience,
} from '../types';
import { RetrievalPolicy } from './retrievalPolicy';
import {
  REAL_LIFE_ISSUE_LABELS,
  WEAKNESS_SIGNAL_LABELS,
  WeaknessEngine,
} from './weaknessEngine';

const RESPONSE_TASKS = new Set([
  'retrieval',
  'controlled',
  'speaking',
  'interaction',
  'writing',
  'transfer',
]);

const CHALLENGE_PRODUCTION_TASKS = new Set([
  'speaking',
  'interaction',
  'writing',
  'transfer',
]);

function detectErrors(response: string, unit: CurriculumUnit): LearnerError[] {
  const text = response.trim();
  if (!text) return [];
  const lower = text.toLowerCase();
  const grammar = unit.content?.grammar ?? '';
  const objective =
    unit.objectives.find((objText) =>
      grammar
        ? objText
            .split(' ')
            .some(
              (word) =>
                word.length > 5 &&
                grammar.toLowerCase().includes(word.toLowerCase()),
            )
        : false,
    ) ?? null;

  const found: LearnerError[] = [];

  if (
    /\b(weil|dass|obwohl|während|damit)\b\s+[^.!?]*\b(ich|du|er|sie|wir|ihr|man)\s+\w+\s+\w+/.test(
      lower,
    )
  ) {
    found.push({
      code: 'subordinate-verb-position',
      label: 'Verb im Nebensatz prüfen',
      excerpt: text.slice(0, 120),
      explanation:
        'Nach weil, dass, obwohl, während und damit steht das konjugierte Verb im Nebensatz normalerweise am Ende.',
      remediation:
        'Formuliere den Satz mit dem finiten Verb am Ende neu und lies ihn langsam vor.',
      objective,
    });
  }

  if (
    /\b(seit|seid)\s+\w+/.test(lower) &&
    !/\bseit\s+ihr\b|\bihr\s+seit\b/.test(lower)
  ) {
    found.push({
      code: 'seit-seid',
      label: 'seit / seid unterscheiden',
      excerpt: text.slice(0, 120),
      explanation:
        'seit bezeichnet Zeit; seid ist die Verbform von sein bei ihr.',
      remediation:
        "Ersetze die Stelle probeweise durch 'schon damals' (seit) oder 'bin/sind' (seid).",
      objective,
    });
  }

  if (/,?\s+dass\s+[A-ZÄÖÜ]/.test(text)) {
    found.push({
      code: 'comma-dass',
      label: 'Komma vor dass prüfen',
      excerpt: text.slice(0, 120),
      explanation:
        'Ein dass-Nebensatz wird im Deutschen durch ein Komma vom Hauptsatz getrennt.',
      remediation:
        'Markiere Hauptsatz und dass-Nebensatz und setze das Komma an die Satzgrenze.',
      objective,
    });
  }

  if (
    text.length >= 80 &&
    text[0] !== text[0].toUpperCase() &&
    text[0] === text[0].toLowerCase()
  ) {
    found.push({
      code: 'sentence-capitalization',
      label: 'Satzanfang prüfen',
      excerpt: text.slice(0, 120),
      explanation:
        'Ein neuer deutscher Satz beginnt mit einem Großbuchstaben.',
      remediation:
        'Teile den Text in Sätze und korrigiere jeden Satzanfang einzeln.',
      objective,
    });
  }

  return found;
}

export function claimsForAttempt(
  attempt: Attempt,
  errors: LearnerError[],
): FeedbackClaim[] {
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
}

export const AdaptiveLearning = {
  detectResponseSignals(response: string, unit: CurriculumUnit): LearnerError[] {
    return detectErrors(response, unit);
  },

  recommend(
    unit: CurriculumUnit,
    attempts: Attempt[],
    now: number,
    realLifeExperiences: RealLifeExperience[] = [],
  ): AdaptiveRecommendation {
    const recent = attempts
      .filter(
        (a) =>
          a.unitId === unit.id && a.timestamp <= now && a.response.trim().length > 0,
      )
      .sort((a, b) => b.timestamp - a.timestamp);

    const latestQuiz = recent
      .filter((a) => a.taskId === 'quiz' && a.source === 'AUTOMATIC_QUIZ')
      .sort((a, b) => b.timestamp - a.timestamp)[0];

    const latestByTask = new Map<string, Attempt>();
    for (const a of recent) {
      if (
        a.source === 'SELF_REFLECTION' &&
        !a.assisted &&
        RESPONSE_TASKS.has(a.taskId)
      ) {
        const prev = latestByTask.get(a.taskId);
        if (!prev || a.timestamp > prev.timestamp) {
          latestByTask.set(a.taskId, a);
        }
      }
    }
    const latestResponses = Array.from(latestByTask.values());
    const latestProduction = latestResponses.filter((a) =>
      CHALLENGE_PRODUCTION_TASKS.has(a.taskId),
    );

    const activeErrorEvidence = latestResponses.flatMap((attempt) =>
      detectErrors(attempt.response, unit).map((err) => ({ attempt, err })),
    );

    const seenErrorKey = new Set<string>();
    const errors: LearnerError[] = [];
    for (const { err } of activeErrorEvidence) {
      const k = `${err.code}:${err.excerpt}`;
      if (!seenErrorKey.has(k)) {
        seenErrorKey.add(k);
        errors.push(err);
        if (errors.length >= 4) break;
      }
    }

    const byAttempt = new Map<string, { attempt: Attempt; errs: LearnerError[] }>();
    for (const { attempt, err } of activeErrorEvidence) {
      const entry = byAttempt.get(attempt.id) ?? { attempt, errs: [] };
      entry.errs.push(err);
      byAttempt.set(attempt.id, entry);
    }
    const seenClaims = new Set<string>();
    const feedbackClaims: FeedbackClaim[] = [];
    for (const { attempt, errs } of byAttempt.values()) {
      for (const claim of claimsForAttempt(attempt, errs)) {
        if (!seenClaims.has(claim.id)) {
          seenClaims.add(claim.id);
          feedbackClaims.push(claim);
          if (feedbackClaims.length >= 4) break;
        }
      }
      if (feedbackClaims.length >= 4) break;
    }

    const allErrors = recent.flatMap((a) => detectErrors(a.response, unit));
    const errorCounts = new Map<string, number>();
    for (const err of allErrors) {
      errorCounts.set(err.code, (errorCounts.get(err.code) ?? 0) + 1);
    }

    const recyclingPlans: ErrorRecyclingPlan[] = errors.map((error) => {
      const repeatedCount = errorCounts.get(error.code) ?? 1;
      const intervalIndex = Math.max(
        0,
        Math.min(RetrievalPolicy.intervalsDays.length - 1, repeatedCount - 1),
      );
      return {
        code: error.code,
        objective: error.objective,
        repeatedCount,
        nextReviewAt:
          now + RetrievalPolicy.intervalsDays[intervalIndex] * 86_400_000,
        difficulty: repeatedCount >= 2 ? 'SUPPORT' : 'CORE',
        instruction: error.remediation,
      };
    });

    const weaknesses = WeaknessEngine.top(
      WeaknessEngine.profile(attempts, realLifeExperiences, now),
    );
    const weakSignals = activeErrorEvidence.length;
    const incorrectQuizzes =
      latestQuiz && latestQuiz.scores.COMPREHENSION !== 4 ? 1 : 0;

    const challengeReady =
      latestQuiz?.scores.COMPREHENSION === 4 &&
      latestProduction.length >= 3 &&
      new Set(latestProduction.map((a) => a.context)).size >= 2 &&
      activeErrorEvidence.length === 0;

    const personalStruggles = realLifeExperiences.filter(
      (e) => e.nextReviewAt <= now,
    );

    let difficulty: AdaptiveDifficulty;
    if (
      personalStruggles.length > 0 ||
      incorrectQuizzes >= 2 ||
      weakSignals >= 3 ||
      recyclingPlans.some((p) => p.repeatedCount >= 2) ||
      weaknesses.some((w) => w.sampleCount >= 2)
    ) {
      difficulty = 'SUPPORT';
    } else if (challengeReady) {
      difficulty = 'CHALLENGE';
    } else {
      difficulty = 'CORE';
    }

    let reason: string;
    if (difficulty === 'SUPPORT') {
      if (personalStruggles.length > 0) {
        reason =
          'Eine echte Alltagssituation ist fällig. Repariere sie zuerst und übertrage sie anschließend in einen neuen Kontext.';
      } else if (weaknesses.some((w) => w.sampleCount >= 2)) {
        reason = `Ein wiederkehrendes Muster wird zuerst gezielt bearbeitet: ${WEAKNESS_SIGNAL_LABELS[weaknesses[0].signal]}.`;
      } else {
        reason =
          'Mehrere offene Hinweise sprechen für einen kurzen Reparaturschritt.';
      }
    } else if (difficulty === 'CHALLENGE') {
      reason =
        'Einzelne Übungsaufgaben waren unauffällig; der nächste Prompt wird anspruchsvoller. Das ist kein Kompetenznachweis.';
    } else {
      reason =
        'Weiter im Kernpfad; einzelne Hinweise werden gezielt wiederholt.';
    }

    let nextStep: string;
    if (personalStruggles.length > 0) {
      nextStep = `Formuliere deine Situation neu: ${REAL_LIFE_ISSUE_LABELS[personalStruggles[0].issueType].toLowerCase()} · danach eine ähnliche Situation ohne Vorlage.`;
    } else if (errors.length > 0) {
      nextStep = errors[0].remediation;
    } else if (difficulty === 'SUPPORT') {
      nextStep =
        'Bearbeite den kontrollierten Satzbau erneut und vergleiche ihn mit der Regel.';
    } else if (difficulty === 'CHALLENGE') {
      nextStep =
        'Bearbeite den anspruchsvolleren Prompt ohne Modell; bekannte Muster bleiben nur begrenzt prüfbar.';
    } else {
      nextStep =
        'Wiederhole den Abruf und formuliere anschließend einen eigenen Beispielsatz.';
    }

    return {
      difficulty,
      reason,
      errors,
      feedbackClaims,
      nextStep,
      weaknesses,
      recyclingPlans,
    };
  },
};
