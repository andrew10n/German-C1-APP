import type {
  AdaptiveAction,
  AdaptiveControlDecision,
  AdaptiveReasonCode,
  Attempt,
  ContextTrack,
  CurriculumUnit,
  DiagnosticConfidence,
  RealLifeExperience,
  RealLifeIssueType,
  WeaknessSignal,
} from '../types';
import { coreUnits } from '../data/curriculumCatalogue';
import { ownedDialogueBriefs } from '../data/listeningResources';
import { AdaptiveDiagnosticRoute } from './adaptiveDiagnosticRoute';
import { AdaptiveLearning } from './adaptiveLearning';
import { REAL_LIFE_ISSUE_LABELS, WEAKNESS_SIGNAL_LABELS, WeaknessEngine } from './weaknessEngine';

const THIRTY_DAYS_MILLIS = 30 * 86_400_000;
const TASK_SEQUENCE = [
  'retrieval',
  'reading',
  'listening',
  'quiz',
  'controlled',
  'speaking',
  'interaction',
  'writing',
];
const PRODUCTION_TASKS = ['speaking', 'interaction', 'writing', 'transfer'];
const RESPONSE_REPAIR_TASKS = new Set([
  'retrieval',
  'controlled',
  'speaking',
  'interaction',
  'writing',
  'transfer',
]);
const CONTEXT_ORDER: ContextTrack[] = ['GENERAL', 'SOCIAL', 'ENGINEERING', 'COACHING'];

interface Selection {
  task: string | null;
  reasonCode: AdaptiveReasonCode;
  confidence: DiagnosticConfidence;
  focusSignal: WeaknessSignal | null;
}

function taskForIssue(issue: RealLifeIssueType): string {
  switch (issue) {
    case 'LISTENING':
      return 'listening';
    case 'RETRIEVAL':
    case 'NATURALNESS':
    case 'REGISTER':
    case 'REPAIR':
      return 'interaction';
    case 'GRAMMAR_UNDER_PRESSURE':
      return 'controlled';
  }
}

function taskForSignal(signal: WeaknessSignal): string {
  switch (signal) {
    case 'SOUND_RECOGNITION':
    case 'WORD_SEGMENTATION':
    case 'LISTENING_VOCABULARY':
    case 'SITUATIONAL_INFERENCE':
    case 'INFORMATION_RETENTION':
      return 'listening';
    case 'ACTIVE_RETRIEVAL':
    case 'SENTENCE_FORMULATION':
    case 'GRAMMAR_UNDER_PRESSURE':
      return 'controlled';
    case 'NATURALNESS':
    case 'REGISTER':
    case 'RESPONSE_INITIATION':
    case 'CONVERSATIONAL_REPAIR':
    case 'INTERACTION_MANAGEMENT':
    case 'IMPLIED_MEANING':
    case 'MULTI_SPEAKER_TRACKING':
      return 'interaction';
  }
}

function productionTaskFor(signal: WeaknessSignal): string {
  switch (signal) {
    case 'NATURALNESS':
    case 'REGISTER':
    case 'RESPONSE_INITIATION':
    case 'CONVERSATIONAL_REPAIR':
    case 'INTERACTION_MANAGEMENT':
      return 'interaction';
    case 'SENTENCE_FORMULATION':
    case 'GRAMMAR_UNDER_PRESSURE':
      return 'writing';
    default:
      return 'speaking';
  }
}

function issueSignal(issue: RealLifeIssueType): WeaknessSignal {
  switch (issue) {
    case 'LISTENING':
      return 'LISTENING_VOCABULARY';
    case 'RETRIEVAL':
      return 'ACTIVE_RETRIEVAL';
    case 'GRAMMAR_UNDER_PRESSURE':
      return 'GRAMMAR_UNDER_PRESSURE';
    case 'NATURALNESS':
      return 'NATURALNESS';
    case 'REGISTER':
      return 'REGISTER';
    case 'REPAIR':
      return 'CONVERSATIONAL_REPAIR';
  }
}

function leastRecentlyPracticedUnit(attempts: Attempt[]): CurriculumUnit | null {
  const candidates = coreUnits.filter((u) => u.content !== null);
  if (candidates.length === 0) return null;
  return [...candidates].sort((a, b) => {
    const lastA = attempts
      .filter((att) => att.unitId === a.id)
      .reduce((max, att) => Math.max(max, att.timestamp), -Infinity);
    const lastB = attempts
      .filter((att) => att.unitId === b.id)
      .reduce((max, att) => Math.max(max, att.timestamp), -Infinity);
    if (lastA !== lastB) return lastA - lastB;
    return a.stage - b.stage;
  })[0];
}

function nextProductionTask(
  unitHistory: Attempt[],
  attempts: Attempt[],
  signal: WeaknessSignal | null,
): string {
  const practiced = new Set(unitHistory.map((a) => a.taskId));
  const preferred = signal ? productionTaskFor(signal) : null;
  if (preferred && !practiced.has(preferred)) return preferred;

  const recentCounts = new Map<string, number>();
  for (const a of attempts) {
    recentCounts.set(a.taskId, (recentCounts.get(a.taskId) ?? 0) + 1);
  }

  const remaining = PRODUCTION_TASKS.filter((t) => !practiced.has(t));
  if (remaining.length === 0) return 'transfer';
  return [...remaining].sort((a, b) => {
    const ca = recentCounts.get(a) ?? 0;
    const cb = recentCounts.get(b) ?? 0;
    if (ca !== cb) return ca - cb;
    return a.localeCompare(b);
  })[0];
}

function leastPracticedTask(
  unitHistory: Attempt[],
  attempts: Attempt[],
  now: number,
): string {
  const practiced = new Set(unitHistory.map((a) => a.taskId));
  const recentCounts = new Map<string, number>();
  for (const a of attempts) {
    if (a.timestamp <= now && now - a.timestamp <= THIRTY_DAYS_MILLIS) {
      recentCounts.set(a.taskId, (recentCounts.get(a.taskId) ?? 0) + 1);
    }
  }
  const remaining = TASK_SEQUENCE.filter((t) => !practiced.has(t));
  if (remaining.length === 0) return 'retrieval';
  return [...remaining].sort((a, b) => {
    const ca = recentCounts.get(a) ?? 0;
    const cb = recentCounts.get(b) ?? 0;
    if (ca !== cb) return ca - cb;
    return TASK_SEQUENCE.indexOf(a) - TASK_SEQUENCE.indexOf(b);
  })[0];
}

function selectContext(unitHistory: Attempt[], attempts: Attempt[]): ContextTrack {
  return [...CONTEXT_ORDER].sort((a, b) => {
    const scoreA =
      unitHistory.filter((att) => att.context === a).length * 2 +
      attempts.filter((att) => att.context === a).length;
    const scoreB =
      unitHistory.filter((att) => att.context === b).length * 2 +
      attempts.filter((att) => att.context === b).length;
    if (scoreA !== scoreB) return scoreA - scoreB;
    return CONTEXT_ORDER.indexOf(a) - CONTEXT_ORDER.indexOf(b);
  })[0];
}

function orderFor(
  primaryTask: string | null,
  unitHistory: Attempt[],
  attempts: Attempt[],
  now: number,
): string[] {
  const recentCounts = new Map<string, number>();
  for (const a of attempts) {
    if (a.timestamp <= now && now - a.timestamp <= THIRTY_DAYS_MILLIS) {
      recentCounts.set(a.taskId, (recentCounts.get(a.taskId) ?? 0) + 1);
    }
  }
  const lessonTasks = new Set(unitHistory.map((a) => a.taskId));
  const followUp = TASK_SEQUENCE.filter((t) => t !== primaryTask).sort((a, b) => {
    const la = lessonTasks.has(a) ? 1 : 0;
    const lb = lessonTasks.has(b) ? 1 : 0;
    if (la !== lb) return la - lb;
    const ca = recentCounts.get(a) ?? 0;
    const cb = recentCounts.get(b) ?? 0;
    if (ca !== cb) return ca - cb;
    return TASK_SEQUENCE.indexOf(a) - TASK_SEQUENCE.indexOf(b);
  });
  return primaryTask ? [primaryTask, ...followUp] : followUp;
}

function actionsFor(task: string | null, reviewDue: boolean): AdaptiveAction[] {
  const actions: AdaptiveAction[] = [];
  if (reviewDue) actions.push('SPACE_RETRIEVAL');
  switch (task) {
    case 'reading':
      actions.push('PRIORITIZE_READING');
      break;
    case 'listening':
      actions.push('PRIORITIZE_LISTENING', 'DEFER_GENERIC_READING');
      break;
    case 'quiz':
      actions.push('PRIORITIZE_COMPREHENSION');
      break;
    case 'controlled':
      actions.push('PRIORITIZE_FORMULATION');
      break;
    case 'interaction':
      actions.push('PRIORITIZE_INTERACTION', 'ADD_CONVERSATION_VARIATION');
      break;
    case 'retrieval':
      actions.push('PRIORITIZE_RETRIEVAL');
      break;
    case 'speaking':
      actions.push('PRIORITIZE_SPEAKING');
      break;
    case 'writing':
      actions.push('PRIORITIZE_WRITING');
      break;
  }
  return actions;
}

function reasonFor(
  reasonCode: AdaptiveReasonCode,
  signal: WeaknessSignal | null,
  experience: RealLifeExperience | null,
  unit: CurriculumUnit | null,
): string {
  switch (reasonCode) {
    case 'PERSONAL_REPAIR_DUE':
      return `Fälliger persönlicher Reparaturschritt: ${experience ? REAL_LIFE_ISSUE_LABELS[experience.issueType] : 'Alltag'}.`;
    case 'LESSON_REVIEW_DUE':
      return `Wiederholung fällig: ${unit?.title ?? 'Lektion'} wird mit aktivem Abruf gefestigt.`;
    case 'QUIZ_REPAIR':
      return 'Die Verständnisprüfung war noch nicht sicher; sie wird nach dem Lerninput erneut angeboten.';
    case 'RESPONSE_REPAIR':
      return 'Ein lokales Antwortmuster braucht gezielte Überarbeitung, bevor die Lektion weitergeht.';
    case 'FIRST_RETRIEVAL':
      return 'Beginne ohne Vorlage mit aktivem Abruf; danach führt der Plan zu Input, Hören und Anwendung.';
    case 'NEXT_INPUT':
      return 'Der erste Abruf ist gespeichert; als Nächstes folgt der Lektionsinput.';
    case 'NEXT_LISTENING':
      return 'Der Lektionsinput ist bearbeitet; als Nächstes folgt das integrierte Hörtraining.';
    case 'NEXT_COMPREHENSION_CHECK':
      return 'Input und Hören sind bearbeitet; prüfe jetzt das Textverständnis.';
    case 'NEXT_CONTROLLED_PRACTICE':
      return 'Das Verständnis ist gesichert; jetzt folgt angeleitete Formulierung.';
    case 'NEXT_PRODUCTION':
      return 'Jetzt folgt eine eigenständige Anwendung in einem weiteren Aufgabentyp.';
    case 'RECURRING_SIGNAL':
      return `Wiederkehrendes Übungssignal (${signal ? WEAKNESS_SIGNAL_LABELS[signal] : 'Übungsfokus'}); die nächste Aufgabe setzt dort an.`;
    case 'BALANCED_PRACTICE':
      return 'Der Kernpfad ist bearbeitet; als Nächstes wird ein zuletzt weniger geübter Bereich gewählt.';
    case 'COURSE_COMPLETE':
      return 'Der geführte Kursweg ist abgeschlossen; aktuell ist keine Lektion verfügbar.';
  }
}

function decideInternal(
  attempts: Attempt[],
  experiences: RealLifeExperience[],
  now: number,
  unitOverride: CurriculumUnit | null,
): AdaptiveControlDecision {
  const eligibleAttempts = attempts.filter((a) => a.timestamp <= now);
  const eligibleExperiences = experiences.filter((e) => e.createdAt <= now);
  const weaknessProfile = WeaknessEngine.profile(
    eligibleAttempts,
    eligibleExperiences,
    now,
  );
  const recurringWeakness =
    WeaknessEngine.top(weaknessProfile).find((s) => s.sampleCount >= 2) ?? null;

  const dueExperience =
    eligibleExperiences
      .filter((e) => e.nextReviewAt <= now)
      .sort((a, b) => a.nextReviewAt - b.nextReviewAt)[0] ?? null;

  const routedUnit =
    unitOverride ?? AdaptiveDiagnosticRoute.nextUnit(eligibleAttempts, now);
  const unit = routedUnit ?? leastRecentlyPracticedUnit(eligibleAttempts);

  const unitHistory = unit
    ? eligibleAttempts.filter(
        (a) => a.unitId === unit.id && a.response.trim().length > 0,
      )
    : [];

  const checkpoint = unit
    ? AdaptiveDiagnosticRoute.evaluate(unit, eligibleAttempts, now)
    : null;

  const latestQuiz =
    unitHistory
      .filter((a) => a.taskId === 'quiz')
      .sort((a, b) => b.timestamp - a.timestamp)[0] ?? null;

  const quizFailed =
    latestQuiz !== null &&
    (latestQuiz.source !== 'AUTOMATIC_QUIZ' ||
      latestQuiz.scores.COMPREHENSION !== 4);

  let productionNeedsRepair = false;
  if (unit) {
    const latestByTask = new Map<string, Attempt>();
    for (const a of unitHistory) {
      if (
        a.source === 'SELF_REFLECTION' &&
        RESPONSE_REPAIR_TASKS.has(a.taskId) &&
        !a.assisted
      ) {
        const prev = latestByTask.get(a.taskId);
        if (!prev || a.timestamp > prev.timestamp) latestByTask.set(a.taskId, a);
      }
    }
    productionNeedsRepair = Array.from(latestByTask.values()).some(
      (a) => AdaptiveLearning.detectResponseSignals(a.response, unit).length > 0,
    );
  }

  const nextRev = unit
    ? AdaptiveDiagnosticRoute.nextReview(eligibleAttempts, unit.id, now)
    : null;
  const reviewDue =
    unit !== null &&
    checkpoint?.state === 'COMPLETE' &&
    nextRev !== null &&
    nextRev <= now;

  let selected: Selection;
  if (dueExperience) {
    selected = {
      task: taskForIssue(dueExperience.issueType),
      reasonCode: 'PERSONAL_REPAIR_DUE',
      confidence: 'LOW',
      focusSignal: issueSignal(dueExperience.issueType),
    };
  } else if (reviewDue) {
    selected = {
      task: 'retrieval',
      reasonCode: 'LESSON_REVIEW_DUE',
      confidence: 'LOW',
      focusSignal: null,
    };
  } else if (!unit) {
    selected = {
      task: null,
      reasonCode: 'COURSE_COMPLETE',
      confidence: 'LOW',
      focusSignal: null,
    };
  } else if (quizFailed) {
    selected = {
      task: 'quiz',
      reasonCode: 'QUIZ_REPAIR',
      confidence: 'LOW',
      focusSignal: 'SITUATIONAL_INFERENCE',
    };
  } else if (productionNeedsRepair) {
    selected = {
      task: 'controlled',
      reasonCode: 'RESPONSE_REPAIR',
      confidence: 'LOW',
      focusSignal: recurringWeakness?.signal ?? null,
    };
  } else if (!unitHistory.some((a) => a.taskId === 'retrieval')) {
    selected = {
      task: 'retrieval',
      reasonCode: 'FIRST_RETRIEVAL',
      confidence: 'LOW',
      focusSignal: null,
    };
  } else if (!unitHistory.some((a) => a.taskId === 'reading')) {
    selected = {
      task: 'reading',
      reasonCode: 'NEXT_INPUT',
      confidence: 'LOW',
      focusSignal: null,
    };
  } else if (!unitHistory.some((a) => a.taskId === 'listening')) {
    selected = {
      task: 'listening',
      reasonCode: 'NEXT_LISTENING',
      confidence: 'LOW',
      focusSignal: 'LISTENING_VOCABULARY',
    };
  } else if (!latestQuiz) {
    selected = {
      task: 'quiz',
      reasonCode: 'NEXT_COMPREHENSION_CHECK',
      confidence: 'LOW',
      focusSignal: null,
    };
  } else if (!unitHistory.some((a) => a.taskId === 'controlled')) {
    selected = {
      task: 'controlled',
      reasonCode: 'NEXT_CONTROLLED_PRACTICE',
      confidence: 'LOW',
      focusSignal: null,
    };
  } else if (checkpoint?.state === 'NEEDS_PRODUCTION') {
    selected = {
      task: nextProductionTask(
        unitHistory,
        eligibleAttempts,
        recurringWeakness?.signal ?? null,
      ),
      reasonCode: 'NEXT_PRODUCTION',
      confidence: 'LOW',
      focusSignal: recurringWeakness?.signal ?? null,
    };
  } else if (recurringWeakness) {
    selected = {
      task: taskForSignal(recurringWeakness.signal),
      reasonCode: 'RECURRING_SIGNAL',
      confidence: recurringWeakness.confidence,
      focusSignal: recurringWeakness.signal,
    };
  } else {
    selected = {
      task: leastPracticedTask(unitHistory, eligibleAttempts, now),
      reasonCode: 'BALANCED_PRACTICE',
      confidence: 'LOW',
      focusSignal: null,
    };
  }

  const focusSignal = selected.focusSignal;
  const context: ContextTrack =
    dueExperience?.context ??
    (focusSignal
      ? WeaknessEngine.top(weaknessProfile).find((s) => s.signal === focusSignal)
          ?.latestEvidence.context
      : undefined) ??
    selectContext(unitHistory, eligibleAttempts);

  const dialogueBriefId =
    ownedDialogueBriefs.find(
      (b) =>
        b.context === context &&
        (focusSignal === null || b.targetSignals.includes(focusSignal)),
    )?.id ??
    ownedDialogueBriefs.find(
      (b) => focusSignal === null || b.targetSignals.includes(focusSignal),
    )?.id ??
    null;

  const taskOrder = orderFor(selected.task, unitHistory, eligibleAttempts, now);
  const reason = reasonFor(selected.reasonCode, focusSignal, dueExperience, unit);

  return {
    primaryTaskId: selected.task,
    actions: actionsFor(selected.task, reviewDue),
    reason,
    confidence: selected.confidence,
    dialogueBriefId,
    taskOrder,
    focusMinutes: selected.task
      ? dueExperience !== null || reviewDue || selected.task === 'interaction'
        ? 5
        : 4
      : null,
    recommendedUnitId: unit?.id ?? null,
    recommendedContext: context,
    reasonCode: selected.reasonCode,
    focusSignal,
  };
}

export const AdaptiveControlEngine = {
  decide(
    attempts: Attempt[],
    experiences: RealLifeExperience[],
    now: number,
  ): AdaptiveControlDecision {
    return decideInternal(attempts, experiences, now, null);
  },

  decideForUnit(
    unit: CurriculumUnit,
    attempts: Attempt[],
    experiences: RealLifeExperience[],
    now: number,
  ): AdaptiveControlDecision {
    return decideInternal(attempts, experiences, now, unit);
  },
};
