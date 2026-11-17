import type {
  AdaptiveDifficulty,
  AdaptiveLessonAddition,
  AdaptiveLessonContentPlan,
  AdaptiveRecommendation,
  Attempt,
  ContextTrack,
  CurriculumUnit,
  DiagnosticCheckpoint,
  RealLifeExperience,
} from '../types';
import { AdaptiveControlEngine } from './adaptiveControlEngine';
import { AdaptiveDiagnosticRoute } from './adaptiveDiagnosticRoute';
import { AdaptiveLearning } from './adaptiveLearning';
import { RealLifeRepairEngine } from './realLifeRepairEngine';
import { REAL_LIFE_ISSUE_LABELS } from './weaknessEngine';

const PRODUCTION_TASKS = new Set(['speaking', 'interaction', 'writing', 'transfer']);
const ALL_CONTEXTS: ContextTrack[] = ['GENERAL', 'SOCIAL', 'ENGINEERING', 'COACHING'];

function shortContextLabel(context: ContextTrack): string {
  switch (context) {
    case 'GENERAL':
      return 'Alltag';
    case 'SOCIAL':
      return 'soziale Gespräche';
    case 'ENGINEERING':
      return 'Ingenieurwesen';
    case 'COACHING':
      return 'Jugendtraining';
  }
}

function supportAddition(
  unit: CurriculumUnit,
  context: ContextTrack,
  recommendation: AdaptiveRecommendation,
  dueExperience: RealLifeExperience | null,
  checkpoint: DiagnosticCheckpoint,
): AdaptiveLessonAddition | null {
  const content = unit.content;
  if (!content) return null;

  if (dueExperience) {
    const repair = RealLifeRepairEngine.create(dueExperience);
    let taskId = 'interaction';
    if (dueExperience.issueType === 'LISTENING') taskId = 'listening';
    else if (dueExperience.issueType === 'GRAMMAR_UNDER_PRESSURE') taskId = 'controlled';

    return {
      taskId,
      title: `Fällige Alltagsreparatur · ${REAL_LIFE_ISSUE_LABELS[dueExperience.issueType]}`,
      instruction: repair.explanation,
      prompt: repair.transferPrompt,
      context: dueExperience.context,
      reviewExperienceId: dueExperience.id,
    };
  }

  const error = recommendation.errors[0];
  if (error) {
    return {
      taskId: 'controlled',
      title: `Gezielte Reparatur · ${error.label}`,
      instruction: error.explanation,
      prompt: `Prüfe zuerst deine gespeicherte Formulierung: „${error.excerpt}“. ${error.label}: ${error.remediation} Formuliere danach einen neuen Beispielsatz zum Lernziel: ${unit.objectives[0] ?? unit.title}`,
      context,
    };
  }

  if (checkpoint.state === 'NEEDS_REMEDIATION') {
    return {
      taskId: 'controlled',
      title: 'Verständnis neu aufbauen',
      instruction: content.explanation,
      prompt: `Erkläre die Frage ohne Antwortoptionen in eigenen Worten: ${content.question} Nenne den entscheidenden Hinweis aus dem Lektionsinput und formuliere anschließend, welche Information dir noch fehlt.`,
      context,
    };
  }

  return {
    taskId: 'controlled',
    title: 'Zusätzliche angeleitete Formulierung',
    instruction: recommendation.nextStep,
    prompt: `Bearbeite die Kernaufgabe ohne Modellhilfe: ${content.controlledPrompt} Prüfe danach dein Lernziel: ${unit.objectives[0] ?? unit.title}`,
    context,
  };
}

function challengeAddition(
  unit: CurriculumUnit,
  unitHistory: Attempt[],
  context: ContextTrack,
): AdaptiveLessonAddition | null {
  if (!unit.content) return null;

  const production = unitHistory.filter(
    (a) =>
      PRODUCTION_TASKS.has(a.taskId) &&
      a.source === 'SELF_REFLECTION' &&
      !a.assisted,
  );
  const unusedContext = ALL_CONTEXTS.find(
    (candidate) => !production.some((a) => a.context === candidate),
  );
  const challengeContext = unusedContext ?? context;
  const challengeIndex = unitHistory.filter((a) => a.taskId === 'transfer').length;
  const objective =
    unit.objectives.length === 0
      ? unit.title
      : unit.objectives[challengeIndex % unit.objectives.length];
  const scenario = unit.contexts[challengeContext] ?? '';

  return {
    taskId: 'transfer',
    title: `Neue Transferaufgabe · ${shortContextLabel(challengeContext)}`,
    instruction:
      'Diese Zusatzaufgabe wird angeboten, weil der Verständnischeck richtig war und mehrere unassistierte Antworten in unterschiedlichen Kontexten vorliegen. Das ist keine C1-Einstufung.',
    prompt: `Übertrage das Lernziel „${objective}“ auf diesen neuen Kontext: ${scenario} Formuliere eine klare Aussage, begründe sie mit einem konkreten Beispiel und benenne eine Einschränkung oder mögliche Rückfrage. Passe dein Register an; verwende keine Vorlage.`,
    context: challengeContext,
  };
}

export const AdaptiveLessonContentEngine = {
  create(
    unit: CurriculumUnit,
    attempts: Attempt[],
    experiences: RealLifeExperience[],
    context: ContextTrack,
    now: number,
  ): AdaptiveLessonContentPlan {
    const unitHistory = attempts.filter(
      (a) =>
        a.unitId === unit.id &&
        a.timestamp <= now &&
        a.response.trim().length > 0,
    );
    const eligibleExperiences = experiences.filter((e) => e.createdAt <= now);
    const decision = AdaptiveControlEngine.decideForUnit(
      unit,
      attempts,
      eligibleExperiences,
      now,
    );
    const recommendation = AdaptiveLearning.recommend(
      unit,
      unitHistory,
      now,
      eligibleExperiences,
    );
    const checkpoint = AdaptiveDiagnosticRoute.evaluate(unit, unitHistory, now);
    const dueExperience =
      eligibleExperiences
        .filter((e) => e.nextReviewAt <= now)
        .sort((a, b) => a.nextReviewAt - b.nextReviewAt)[0] ?? null;

    let difficulty: AdaptiveDifficulty;
    if (
      checkpoint.state === 'NEEDS_REMEDIATION' ||
      dueExperience !== null ||
      recommendation.errors.length > 0 ||
      recommendation.difficulty === 'SUPPORT'
    ) {
      difficulty = 'SUPPORT';
    } else if (recommendation.difficulty === 'CHALLENGE') {
      difficulty = 'CHALLENGE';
    } else {
      difficulty = 'CORE';
    }

    let addition: AdaptiveLessonAddition | null = null;
    if (difficulty === 'SUPPORT') {
      addition = supportAddition(
        unit,
        context,
        recommendation,
        dueExperience,
        checkpoint,
      );
    } else if (difficulty === 'CHALLENGE') {
      addition = challengeAddition(unit, unitHistory, context);
    }

    let reason: string;
    if (difficulty === 'SUPPORT') {
      reason =
        'Gezielte Zusatzhilfe aus einer fälligen Wiederholung oder einem lokalen Antwortsignal.';
    } else if (difficulty === 'CHALLENGE') {
      reason =
        'Zusatztransfer erst nach richtigem Check und vielfältigen, unassistierten Antworten.';
    } else {
      reason = 'Der Kerninhalt bleibt im üblichen Schrittumfang.';
    }

    return {
      difficulty,
      focusTaskId: decision.primaryTaskId ?? 'retrieval',
      reason,
      addition,
      recommendation,
    };
  },
};
