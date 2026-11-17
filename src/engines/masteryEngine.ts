import type { Attempt, ContextTrack, CurriculumUnit, Dimension } from '../types';
import { coreUnits, curriculumUnits } from '../data/curriculumCatalogue';
import { RetrievalPolicy } from './retrievalPolicy';

const ALL_DIMENSIONS: Dimension[] = [
  'ACCURACY',
  'FLUENCY',
  'RANGE',
  'NATURALNESS',
  'COHERENCE',
  'INTERACTION',
  'PRAGMATICS',
  'REGISTER',
  'COMPREHENSION',
  'MEDIATION',
];

function plusDays(timestamp: number, days: number): number {
  const offset = days * MasteryEngine.DAY_MILLIS;
  return timestamp + offset;
}

function isCoreUnit(unit: CurriculumUnit): boolean {
  return unit.content !== null && unit.stage >= 1 && unit.stage <= 6;
}

function successful(attempt: Attempt): boolean {
  return (
    attempt.source === 'HUMAN_REVIEW' &&
    !attempt.assisted &&
    attempt.independentProduction &&
    ALL_DIMENSIONS.every((dim) => {
      const score = attempt.scores[dim];
      return typeof score === 'number' && score >= 3 && score <= 4;
    })
  );
}

function attemptKey(attempt: Attempt): string {
  return JSON.stringify({
    id: attempt.id,
    unitId: attempt.unitId,
    taskId: attempt.taskId,
    timestamp: attempt.timestamp,
    response: attempt.response,
    source: attempt.source,
    scores: attempt.scores,
    assisted: attempt.assisted,
    unfamiliar: attempt.unfamiliar,
    independentProduction: attempt.independentProduction,
    context: attempt.context,
    errors: attempt.errors,
  });
}

function history(attempts: Attempt[], unitId: string): Attempt[] {
  const byId = new Map<string, Attempt[]>();
  for (const a of attempts) {
    const list = byId.get(a.id) ?? [];
    list.push(a);
    byId.set(a.id, list);
  }
  const deduplicated: Attempt[] = [];
  for (const records of byId.values()) {
    const uniqueKeys = new Set(records.map(attemptKey));
    if (uniqueKeys.size === 1) {
      deduplicated.push(records[0]);
    }
  }
  return deduplicated
    .filter(
      (a) =>
        a.unitId === unitId &&
        a.id.trim().length > 0 &&
        a.taskId.trim().length > 0 &&
        a.response.trim().length > 0 &&
        a.timestamp >= 0,
    )
    .sort((a, b) => a.timestamp - b.timestamp || a.id.localeCompare(b.id));
}

function checkpointComplete(
  checkpoints: Attempt[],
  prefix: string,
  context: ContextTrack,
): boolean {
  const matching = checkpoints.filter(
    (attempt) =>
      attempt.context === context &&
      (attempt.taskId === prefix ||
        (attempt.taskId.startsWith(`${prefix}:`) &&
          attempt.taskId.slice(prefix.length + 1).trim().length > 0)),
  );
  return matching.some((first) =>
    matching.some(
      (second) =>
        first.taskId !== second.taskId &&
        Math.floor(first.timestamp / MasteryEngine.DAY_MILLIS) !==
          Math.floor(second.timestamp / MasteryEngine.DAY_MILLIS),
    ),
  );
}

export const MasteryEngine = {
  DAY_MILLIS: 86_400_000,
  GRADUATION_UNIT_ID: 'graduation',
  GENERAL_CHECKPOINT_TASK_ID: 'unseen-general',
  SPECIALIST_CHECKPOINT_TASK_ID: 'unseen-specialist',
  SOCIAL_CHECKPOINT_TASK_ID: 'unseen-social',
  INTEGRATED_CHECKPOINT_TASK_ID: 'unseen-integrated',
  FORMAL_VALIDATION_GAP:
    'Formale Abschlussfreigabe gesperrt: unabhängige Kalibrierung von Kursinhalten und Rubriken sowie geschützte Hör-/Sprechprüfungen sind nicht belegt.',
  ALL_DIMENSIONS,

  isMastered(unit: CurriculumUnit, attempts: Attempt[]): boolean {
    if (!isCoreUnit(unit)) return false;
    const successes = history(attempts, unit.id).filter(successful);
    if (new Set(successes.map((a) => a.taskId)).size < 3) return false;
    if (new Set(successes.map((a) => a.context)).size < 2) return false;
    if (
      new Set(successes.map((a) => Math.floor(a.timestamp / MasteryEngine.DAY_MILLIS))).size < 2
    ) {
      return false;
    }
    const first = successes[0].timestamp;
    return successes.some(
      (a) => a.unfamiliar && a.timestamp - first >= 7 * MasteryEngine.DAY_MILLIS,
    );
  },

  isUnlocked(unit: CurriculumUnit, attempts: Attempt[]): boolean {
    const catalogue = new Map(curriculumUnits.map((u) => [u.id, u]));
    const ready = (candidate: CurriculumUnit, visiting: Set<string>): boolean => {
      if (!isCoreUnit(candidate) || visiting.has(candidate.id)) return false;
      const priorStage = coreUnits
        .filter((u) => u.stage >= 1 && u.stage < candidate.stage)
        .map((u) => u.id);
      const nextVisiting = new Set(visiting);
      nextVisiting.add(candidate.id);
      const allPrereqs = Array.from(new Set([...candidate.prerequisites, ...priorStage]));
      return allPrereqs.every((id) => {
        const prereq = catalogue.get(id);
        return (
          prereq !== undefined &&
          MasteryEngine.isMastered(prereq, attempts) &&
          ready(prereq, nextVisiting)
        );
      });
    };
    return ready(unit, new Set());
  },

  nextReview(attempts: Attempt[], unitId: string): number | null {
    const unit = coreUnits.find((u) => u.id === unitId);
    if (!unit) return null;
    const records = history(attempts, unitId);
    if (records.length === 0) return null;

    const productions = records.filter(
      (r) =>
        r.source === 'HUMAN_REVIEW' && !r.assisted && r.independentProduction,
    );
    const latest = productions[productions.length - 1];
    let streak = 0;
    for (let i = productions.length - 1; i >= 0; i--) {
      if (successful(productions[i])) streak++;
      else break;
    }
    const intervalIndex = Math.min(
      RetrievalPolicy.intervalsDays.length - 1,
      Math.max(0, streak - 1),
    );
    const interval = RetrievalPolicy.intervalsDays[intervalIndex];
    const scheduled = plusDays(
      latest?.timestamp ?? records[0].timestamp,
      interval,
    );
    const lastSuccess = [...productions].reverse().find(successful)?.timestamp;
    const unresolvedError = [...records]
      .reverse()
      .find(
        (record) =>
          record.errors.some((e) => e.trim().length > 0) &&
          (lastSuccess === undefined || record.timestamp >= lastSuccess),
      );
    if (!unresolvedError) return scheduled;
    return Math.min(
      scheduled,
      plusDays(unresolvedError.timestamp, RetrievalPolicy.errorRetryDays),
    );
  },

  nextUnit(attempts: Attempt[], now: number): CurriculumUnit | null {
    const availableEvidence = attempts.filter((a) => a.timestamp <= now);
    const available = coreUnits.filter((u) =>
      MasteryEngine.isUnlocked(u, availableEvidence),
    );
    const due = available
      .map((unit) => {
        const nr = MasteryEngine.nextReview(availableEvidence, unit.id);
        return nr !== null && nr <= now ? { unit, nr } : null;
      })
      .filter((x): x is { unit: CurriculumUnit; nr: number } => x !== null)
      .sort(
        (a, b) =>
          a.nr - b.nr ||
          a.unit.stage - b.unit.stage ||
          a.unit.id.localeCompare(b.unit.id),
      );
    if (due.length > 0) return due[0].unit;
    return (
      available.find((u) => !MasteryEngine.isMastered(u, availableEvidence)) ??
      null
    );
  },

  graduationGaps(
    attempts: Attempt[],
    units: CurriculumUnit[] = coreUnits,
  ): string[] {
    const gaps: string[] = [MasteryEngine.FORMAL_VALIDATION_GAP];
    for (const unit of units) {
      if (!MasteryEngine.isMastered(unit, attempts)) {
        gaps.push(`Noch nicht nachgewiesen: ${unit.title} (${unit.id}).`);
      }
    }
    const outlineByStage = new Map<number, number>();
    for (const unit of units) {
      if (unit.content === null) {
        outlineByStage.set(unit.stage, (outlineByStage.get(unit.stage) ?? 0) + 1);
      }
    }
    for (const [stage, count] of Array.from(outlineByStage.entries()).sort(
      (a, b) => a[0] - b[0],
    )) {
      gaps.push(
        `Stufe ${stage}: ${count} Kompetenzen sind nur Entwürfe; ausgearbeitete Aufgaben und Leistungsnachweise fehlen.`,
      );
    }

    const checkpoints = history(attempts, MasteryEngine.GRADUATION_UNIT_ID).filter(
      (a) => successful(a) && a.unfamiliar,
    );
    const requireCheckpoint = (
      prefix: string,
      context: ContextTrack,
      label: string,
    ) => {
      if (!checkpointComplete(checkpoints, prefix, context)) {
        gaps.push(
          `${label} Abschluss-Checkpoint fehlt: mindestens zwei verschiedene unbekannte Aufgaben an verschiedenen UTC-Tagen; jeweils unabhängige, unassistierte Produktion mit gespeicherter Quelle HUMAN_REVIEW und allen zehn Dimensionen mindestens 3/4. Aufgaben-ID: ${prefix}:<Variante>, Kontext: ${context}. Die Quellenangabe ist keine externe Identitätsprüfung.`,
        );
      }
    };

    requireCheckpoint(
      MasteryEngine.GENERAL_CHECKPOINT_TASK_ID,
      'GENERAL',
      'Allgemeiner (GENERAL)',
    );
    requireCheckpoint(
      MasteryEngine.SOCIAL_CHECKPOINT_TASK_ID,
      'SOCIAL',
      'Sozialer (SOCIAL, Alltag und situationsgerechte Pragmatik)',
    );
    requireCheckpoint(
      MasteryEngine.SPECIALIST_CHECKPOINT_TASK_ID,
      'ENGINEERING',
      'Automotive-fachlicher (ENGINEERING, EV/Hybrid)',
    );
    requireCheckpoint(
      MasteryEngine.SPECIALIST_CHECKPOINT_TASK_ID,
      'COACHING',
      'Jugendfußball-fachlicher (COACHING, Spieler und Eltern)',
    );
    requireCheckpoint(
      MasteryEngine.INTEGRATED_CHECKPOINT_TASK_ID,
      'GENERAL',
      'Integrierter allgemeiner (GENERAL: Verstehen, Produktion, Interaktion und Mediation)',
    );

    return gaps;
  },
};
