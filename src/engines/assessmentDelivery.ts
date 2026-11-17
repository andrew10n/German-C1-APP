import type {
  AssessmentSession,
  AssessmentSessionKind,
  Attempt,
  ContextTrack,
  CurriculumUnit,
  DeliveredAssessmentTask,
  DiagnosticDimension,
  PilotComparison,
  TaskVariation,
} from '../types';
import { curriculumUnits } from '../data/curriculumCatalogue';
import { RetrievalPolicy } from './retrievalPolicy';
import { MasteryEngine } from './masteryEngine';

const DAY_MILLIS = 86_400_000;
const WEEK_MILLIS = 7 * DAY_MILLIS;

const CONTEXT_TRACKS: ContextTrack[] = ['GENERAL', 'SOCIAL', 'ENGINEERING', 'COACHING'];

function floorMod(n: number, m: number): number {
  return ((n % m) + m) % m;
}

export const ASSESSMENT_VARIATIONS: TaskVariation[] = [
  {
    id: 'peer-review',
    label: 'Kollegiales Review',
    audience: 'eine fachkundige Kollegin',
    pressure: 'eine Rückfrage unterbricht Ihre Erklärung',
    constraint: 'greifen Sie die Rückfrage auf und führen Sie zum Hauptpunkt zurück',
    taskVersion: 2,
    rubricVersion: 1,
  },
  {
    id: 'manager-briefing',
    label: 'Entscheidungsvorlage',
    audience: 'eine Projektleitung ohne Detailwissen',
    pressure: 'es wird eine klare Empfehlung verlangt',
    constraint: 'trennen Sie gesicherte Fakten, Annahmen und nächsten Prüfschritt',
    taskVersion: 2,
    rubricVersion: 1,
  },
  {
    id: 'safety-escalation',
    label: 'Sicherheitseskalation',
    audience: 'eine Safety-Verantwortliche',
    pressure: 'Lieferdruck wird ausdrücklich genannt',
    constraint: 'geben Sie keine Freigabe ohne belegte Voraussetzung',
    taskVersion: 2,
    rubricVersion: 1,
  },
  {
    id: 'parent-conversation',
    label: 'Elterngespräch',
    audience: 'ein besorgtes Elternteil',
    pressure: 'die Person formuliert einen Vorwurf',
    constraint: 'anerkennen Sie das Anliegen, ohne eine unbelegte Absicht zu bestätigen',
    taskVersion: 2,
    rubricVersion: 1,
  },
  {
    id: 'youth-explanation',
    label: 'Jugendliche Erklärung',
    audience: 'eine U13- oder U15-Gruppe',
    pressure: 'ein Kind versteht den ersten Erklärungsversuch nicht',
    constraint: 'vereinfachen Sie die Erklärung und lassen Sie das Verständnis prüfen',
    taskVersion: 2,
    rubricVersion: 1,
  },
  {
    id: 'regional-informal',
    label: 'Informelles Regionalgespräch',
    audience: 'eine vertraute Person im Vereinsumfeld',
    pressure: 'eine umgangssprachliche oder regionale Wendung bleibt mehrdeutig',
    constraint: 'fragen Sie die gemeinte Verbindlichkeit freundlich nach',
    taskVersion: 2,
    rubricVersion: 1,
  },
  {
    id: 'mediation',
    label: 'Mehrparteien-Mediation',
    audience: 'drei Rollen mit unterschiedlichen Interessen',
    pressure: 'zwei Personen sprechen gleichzeitig oder widersprechen einander',
    constraint: 'fassen Sie beide Positionen fair zusammen und sichern Sie eine nächste Handlung',
    taskVersion: 2,
    rubricVersion: 1,
  },
  {
    id: 'delayed-retrieval',
    label: 'Verzögerter Abruf',
    audience: 'eine neue Gesprächspartnerin',
    pressure: 'Sie erhalten keine Vorlage und nur kurze Vorbereitungszeit',
    constraint: 'verwenden Sie keine auswendig gelernte Modellformulierung',
    taskVersion: 2,
    rubricVersion: 1,
  },
];

export function applyVariation(variation: TaskVariation, prompt: string): string {
  return `${prompt}\n\nVariation ${variation.label}: Publikum: ${variation.audience}. Gesprächsbedingung: ${variation.pressure}. Zusatzbedingung: ${variation.constraint}`;
}

function buildTask(
  unit: CurriculumUnit,
  taskId: string,
  prompt: string,
  index: number,
  seed = 0,
): DeliveredAssessmentTask {
  const variation =
    ASSESSMENT_VARIATIONS[floorMod(seed + index, ASSESSMENT_VARIATIONS.length)];
  return {
    id: `task-${unit.id}-${taskId}-${variation.id}`,
    unitId: unit.id,
    taskId: `assessment:${taskId}:${variation.id}`,
    prompt: applyVariation(variation, prompt),
    evidence: 'TEXT_RESPONSE',
    context: CONTEXT_TRACKS[index % CONTEXT_TRACKS.length],
    unfamiliar: false,
    variationId: variation.id,
    taskVersion: variation.taskVersion,
    rubricVersion: variation.rubricVersion,
  };
}

function buildAudioTask(
  unit: CurriculumUnit,
  taskId: string,
  prompt: string,
  seed: number,
): DeliveredAssessmentTask {
  return {
    id: `task-${unit.id}-${taskId}-${seed}`,
    unitId: unit.id,
    taskId: `assessment:${taskId}:${seed}`,
    prompt,
    evidence: 'AUDIO_RESPONSE',
    context: 'GENERAL',
    unfamiliar: false,
    variationId: 'local-audio',
    taskVersion: 3,
    rubricVersion: 1,
  };
}

export const AssessmentDeliveryEngine = {
  variations: ASSESSMENT_VARIATIONS,

  remainingMinutes(session: AssessmentSession, now: number): number {
    const deadline = session.createdAt + session.durationMinutes * 60_000;
    return Math.max(0, Math.floor((deadline - now + 59_999) / 60_000));
  },

  createSession(
    kind: AssessmentSessionKind,
    now: number,
    seed = Math.floor(now / DAY_MILLIS),
  ): AssessmentSession {
    const authored = curriculumUnits.filter((u) => u.content !== null);
    if (authored.length === 0) {
      throw new Error('Assessment requires authored curriculum content.');
    }
    const shift = floorMod(seed, authored.length);
    const ordered = [...authored.slice(shift), ...authored.slice(0, shift)];

    let tasks: DeliveredAssessmentTask[] = [];
    switch (kind) {
      case 'DELAYED_CHECKPOINT':
        tasks = ordered
          .slice(0, 4)
          .map((unit, index) =>
            buildTask(unit, `checkpoint:${index}`, unit.content!.transfer, index, seed),
          );
        break;
      case 'GOETHE_STYLE':
        tasks = [
          buildTask(ordered[0], 'goethe:listening', ordered[0].content!.listening, 0, seed),
          buildTask(
            ordered[1 % ordered.length],
            'goethe:reading',
            ordered[1 % ordered.length].content!.reading,
            1,
            seed,
          ),
          buildTask(
            ordered[2 % ordered.length],
            'goethe:writing',
            ordered[2 % ordered.length].content!.writing,
            2,
            seed,
          ),
          buildTask(
            ordered[3 % ordered.length],
            'goethe:speaking',
            ordered[3 % ordered.length].content!.speaking,
            3,
            seed,
          ),
        ];
        break;
      case 'PILOT_WEEK':
        tasks = ordered
          .slice(0, 3)
          .map((unit, index) =>
            buildTask(unit, `pilot:${index}`, unit.content!.transfer, index, seed),
          );
        break;
      case 'WEEKLY_INTEGRATED':
        tasks = ordered
          .slice(0, 5)
          .map((unit, index) =>
            buildTask(unit, `weekly:${seed}:${index}`, unit.content!.transfer, index, seed),
          );
        break;
      case 'LISTENING_PRACTICE':
        tasks = [
          buildAudioTask(
            ordered[0],
            'listening:owned-audio',
            'Hören Sie eine selbst ausgewählte, rechtmäßig zugängliche deutsche Aufnahme. Notieren Sie die Hauptaussage, zwei konkrete Einzelheiten und einen offenen Punkt.',
            seed,
          ),
        ];
        break;
      case 'SPEAKING_PRACTICE':
        tasks = [
          buildAudioTask(
            ordered[0],
            'speaking:recorded-response',
            ordered[0].content!.speaking,
            seed,
          ),
        ];
        break;
    }

    const durationMinutes =
      kind === 'WEEKLY_INTEGRATED'
        ? 60
        : kind === 'LISTENING_PRACTICE' || kind === 'SPEAKING_PRACTICE'
          ? 20
          : 30;

    return {
      id: `assessment-${kind.toLowerCase()}-${now}-${seed}`,
      kind,
      createdAt: now,
      durationMinutes,
      tasks,
    };
  },

  nextDelayedCheckpoint(
    attempts: Attempt[],
    _skill: DiagnosticDimension,
    now: number,
  ): number | null {
    const relevant = attempts
      .filter(
        (a) =>
          a.timestamp <= now &&
          a.unfamiliar &&
          a.independentProduction &&
          a.response.trim().length > 0,
      )
      .sort((a, b) => b.timestamp - a.timestamp);
    if (relevant.length === 0) return now;
    const baseline = relevant[relevant.length - 1];
    const intervals = RetrievalPolicy.intervalsDays;
    const missingIndex = intervals.findIndex((interval) => {
      const dueAt = baseline.timestamp + interval * MasteryEngine.DAY_MILLIS;
      return relevant.every((a) => a.timestamp < dueAt);
    });
    if (missingIndex === -1) return null;
    return baseline.timestamp + intervals[missingIndex] * MasteryEngine.DAY_MILLIS;
  },

  comparePilot(attempts: Attempt[], baselineAt: number, finalAt: number): PilotComparison {
    const window = (start: number, end: number) =>
      attempts.filter(
        (a) => a.timestamp >= start && a.timestamp <= end && a.response.trim().length > 0,
      );
    const baseline = window(0, baselineAt);
    const final = window(baselineAt + 1, finalAt);
    return {
      baselineResponses: baseline.length,
      finalResponses: final.length,
      baselineIndependent: baseline.filter((a) => a.independentProduction).length,
      finalIndependent: final.filter((a) => a.independentProduction).length,
      baselineUnfamiliar: baseline.filter((a) => a.unfamiliar).length,
      finalUnfamiliar: final.filter((a) => a.unfamiliar).length,
    };
  },
};

export const IntegratedAssessmentScheduler = {
  WEEK_MILLIS,

  schedule(now: number, lastCompletedAt: number | null = null) {
    const weekStart = Math.floor(now / WEEK_MILLIS) * WEEK_MILLIS;
    const dueAt = lastCompletedAt !== null ? lastCompletedAt + WEEK_MILLIS : weekStart;
    const dueWeek = Math.floor(dueAt / WEEK_MILLIS);
    return {
      weekStartUtc: weekStart,
      dueAt,
      seed: floorMod(dueWeek, curriculumUnits.length),
      sessionKind: 'WEEKLY_INTEGRATED' as AssessmentSessionKind,
    };
  },

  isDue(now: number, lastCompletedAt: number | null = null): boolean {
    return now >= IntegratedAssessmentScheduler.schedule(now, lastCompletedAt).dueAt;
  },

  createDueSession(
    now: number,
    lastCompletedAt: number | null = null,
  ): AssessmentSession | null {
    const planned = IntegratedAssessmentScheduler.schedule(now, lastCompletedAt);
    if (now < planned.dueAt) return null;
    return AssessmentDeliveryEngine.createSession(planned.sessionKind, now, planned.seed);
  },
};
