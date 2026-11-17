import type {
  BaselineProfileRecord,
  DiagnosticMode,
  DiagnosticTask,
  DiagnosticUserAnswer,
  SkillCategory,
  SkillScore,
} from '../types';
import { ALL_SKILL_CATEGORIES } from '../data/learningData';

const SCREENING_SKILLS = new Set<SkillCategory>([
  'HOERVERSTEHEN',
  'LESEVERSTEHEN',
  'GRAMMATIK',
  'WORTSCHATZ',
]);

export interface DiagnosticEvaluationResult {
  profile: BaselineProfileRecord;
  nineDimensionalProfile: Record<SkillCategory, SkillScore>;
  isProvisional: boolean;
  confidenceScore: number;
  summaryDe: string;
  grammarScore: number;
  vocabularyScore: number;
  overallScore: number;
  grammarTopicScores: Record<string, number>;
  vocabTopicScores: Record<string, number>;
  primaryLimitingFactor: string;
  testMode: DiagnosticMode;
  assessedSkills: SkillCategory[];
  answeredTaskCount: number;
  totalTaskCount: number;
}

export function buildNineDimensionalProfile(
  profile: BaselineProfileRecord,
  assessedSkills: SkillCategory[] = [],
): Record<SkillCategory, SkillScore> {
  const scores: Partial<Record<SkillCategory, number>> = {
    HOERVERSTEHEN: profile.listeningScore,
    LESEVERSTEHEN: profile.readingScore,
    GRAMMATIK: profile.grammarScore,
    WORTSCHATZ: profile.vocabularyScore,
  };
  const assessedSet = new Set(assessedSkills);
  const result = {} as Record<SkillCategory, SkillScore>;
  for (const category of ALL_SKILL_CATEGORIES) {
    const assessed = assessedSet.has(category) && SCREENING_SKILLS.has(category);
    const val = scores[category] ?? 0;
    result[category] = {
      category,
      score: assessed ? Math.max(0, Math.min(100, val)) : 0,
      lastTestedDaysAgo: assessed ? 0 : -1,
      trend: 0,
      criticalWeakness: assessed
        ? 'Nur MCQ-Stichprobe; keine Kompetenzdiagnose'
        : 'Nicht beurteilt (0 = keine Evidenz)',
      masteredAreas: [],
    };
  }
  return result;
}

export function evaluateDiagnosticScreening(
  tasks: DiagnosticTask[],
  answers: Record<number, DiagnosticUserAnswer>,
  mode: DiagnosticMode,
  now = Date.now(),
): DiagnosticEvaluationResult {
  const validIndices: number[] = [];
  for (const [key, answer] of Object.entries(answers)) {
    const idx = Number(key);
    const task = tasks[idx];
    if (
      task &&
      answer.selectedOptionIndex !== null &&
      answer.selectedOptionIndex >= 0 &&
      answer.selectedOptionIndex < task.multipleChoiceOptions.length
    ) {
      validIndices.push(idx);
    }
  }
  const validSet = new Set(validIndices);

  const itemScore = (idx: number): number =>
    answers[idx]?.selectedOptionIndex === tasks[idx]?.correctOptionIndex ? 100 : 0;

  const scoredIndices = tasks
    .map((task, idx) => ({ task, idx }))
    .filter(({ task }) => SCREENING_SKILLS.has(task.skillCategory))
    .map(({ idx }) => idx);

  const assessedSet = new Set<SkillCategory>(
    scoredIndices.filter((idx) => validSet.has(idx)).map((idx) => tasks[idx].skillCategory),
  );

  const scoreFor = (category: SkillCategory): number => {
    if (!assessedSet.has(category)) return 0;
    const catIndices = scoredIndices.filter((idx) => tasks[idx].skillCategory === category);
    if (catIndices.length === 0) return 0;
    const sum = catIndices.reduce((acc, idx) => acc + itemScore(idx), 0);
    return Math.trunc(sum / catIndices.length);
  };

  const topicScores = (category: SkillCategory): Record<string, number> => {
    const grouped = new Map<string, number[]>();
    for (const idx of scoredIndices) {
      if (validSet.has(idx) && tasks[idx].skillCategory === category) {
        const topic = tasks[idx].subTopicName;
        const arr = grouped.get(topic) ?? [];
        arr.push(itemScore(idx));
        grouped.set(topic, arr);
      }
    }
    const out: Record<string, number> = {};
    for (const [topic, scoresArr] of grouped.entries()) {
      out[topic] = Math.trunc(scoresArr.reduce((a, b) => a + b, 0) / scoresArr.length);
    }
    return out;
  };

  const grammarScore = scoreFor('GRAMMATIK');
  const vocabularyScore = scoreFor('WORTSCHATZ');

  const profile: BaselineProfileRecord = {
    id: `screening-${now}`,
    listeningScore: scoreFor('HOERVERSTEHEN'),
    speakingScore: 0,
    interactionScore: 0,
    readingScore: scoreFor('LESEVERSTEHEN'),
    writingScore: 0,
    grammarScore,
    vocabularyScore,
    mediationScore: 0,
    overallC1Score: 0,
    functionalReadinessPct: 0,
    primaryLimitingFactor: 'Nicht bestimmt – kurze MCQ-Stichprobe',
    recommendedStartingEngine: 'Evidenzbasiertes Lernprogramm auf der Startseite',
    isCompleted: false,
    assessedAtTimestamp: now,
  };

  const assessedSkills = Array.from(assessedSet);
  const summaryDe =
    `Vorläufiges Profil: kurzes MCQ-Übungsscreening. ` +
    `${validSet.size}/${tasks.length} Aufgaben beantwortet. ` +
    'Prozentwerte zeigen nur richtige Auswahlantworten in dieser Stichprobe; ' +
    'unbeantwortete Aufgaben zählen nicht als richtig. ' +
    'Keine GER-/CEFR-Einstufung, keine Messung produktiver Fertigkeiten. ' +
    'Sprechen, Interaktion, Schreiben, Mediation und Aussprache/Prosodie: nicht beurteilt. ' +
    '0 bei nicht beurteilten Bereichen bedeutet fehlende Evidenz, nicht fehlende Fähigkeit. ' +
    'Keine validierte Konfidenz oder C1-/Alltagsbereitschaft ableitbar. ' +
    'Ergebnis nur für diese Sitzung; ein vorhandenes Baseline-Profil bleibt unverändert.';

  return {
    profile,
    nineDimensionalProfile: buildNineDimensionalProfile(profile, assessedSkills),
    isProvisional: true,
    confidenceScore: 0,
    summaryDe,
    grammarScore,
    vocabularyScore,
    overallScore: profile.overallC1Score,
    grammarTopicScores: topicScores('GRAMMATIK'),
    vocabTopicScores: topicScores('WORTSCHATZ'),
    primaryLimitingFactor: profile.primaryLimitingFactor,
    testMode: mode,
    assessedSkills,
    answeredTaskCount: validSet.size,
    totalTaskCount: tasks.length,
  };
}
