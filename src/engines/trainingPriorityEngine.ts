import type {
  ErrorCategory,
  ErrorIntelligenceItem,
  SkillCategory,
  SkillScore,
  TrainingPriorityRecommendation,
} from '../types';
import { SKILL_CATEGORY_META } from '../data/learningData';

const WEAKNESS_WEIGHT = 0.3;
const IMPORTANCE_WEIGHT = 0.25;
const RECURRING_ERRORS_WEIGHT = 0.2;
const TIME_SINCE_LAST_TRAINING_WEIGHT = 0.15;
const RECENT_PERFORMANCE_WEIGHT = 0.1;

function clamp(v: number, min: number, max: number): number {
  return Math.max(min, Math.min(max, v));
}

function skillCategoryToErrorCategory(skill: SkillCategory): ErrorCategory {
  switch (skill) {
    case 'GRAMMATIK':
      return 'PRAEPOSITIONEN_KASUS';
    case 'WORTSCHATZ':
      return 'KOLLOKATIONEN';
    case 'SCHREIBEN':
      return 'REGISTER_MISMATCH';
    case 'SPONTANES_SPRECHEN':
    case 'INTERAKTION':
      return 'ZOEGERN_LATENZ';
    default:
      return 'VERBKLAMMER_SATZSTRUKTUR';
  }
}

export interface TrainingActivity {
  id: string;
  title: string;
  category: SkillCategory;
  weaknessSeverity: number;
  importanceForC1: number;
  recurringErrorsScore: number;
  timeSinceLastTraining: number;
  recentPerformanceNeed: number;
  recommendedTaskTitle: string;
  environment: string;
}

export interface PriorityEvaluation {
  activity: TrainingActivity;
  learningValueScore: number;
  normalizedPriorityPercent: number;
  rank: number;
  weaknessContribution: number;
  importanceContribution: number;
  errorsContribution: number;
  timeSinceTrainingContribution: number;
  recentPerformanceContribution: number;
  rationaleHeadline: string;
  detailedRationaleDe: string;
}

export const TrainingPriorityEngine = {
  createActivityFromSkill(
    skill: SkillScore,
    unresolvedErrorCount: number,
    importanceOverride: number | null = null,
    recentPerformanceScore = 30.0,
  ): TrainingActivity {
    let defaultImportance = 70;
    switch (skill.category) {
      case 'INTERAKTION':
      case 'SPONTANES_SPRECHEN':
        defaultImportance = 95;
        break;
      case 'HOERVERSTEHEN':
        defaultImportance = 90;
        break;
      case 'WORTSCHATZ':
      case 'GRAMMATIK':
        defaultImportance = 85;
        break;
      case 'MEDIATION':
        defaultImportance = 80;
        break;
      case 'LESEVERSTEHEN':
      case 'SCHREIBEN':
        defaultImportance = 75;
        break;
      case 'AUSSPRACHE_PROSODIE':
        defaultImportance = 70;
        break;
    }

    const weakness = clamp(100 - skill.score, 0, 100);
    const errorsScore = clamp(unresolvedErrorCount * 25.0, 0, 100);
    const timeScore = clamp(Math.max(0, skill.lastTestedDaysAgo) * 25.0, 0, 100);

    const taskTitles: Record<SkillCategory, string> = {
      INTERAKTION: 'Unterbrechungs-Stresstest im Führungskreis',
      HOERVERSTEHEN: 'Schwäbischer Werkstattdialog & Schnellsprech-Inferenz',
      SPONTANES_SPRECHEN: 'Spontane Gegendarstellung unter Zeitdruck',
      MEDIATION: 'Fachjuristisches Gutachten in Bürgersprache vermitteln',
      SCHREIBEN: 'Schlichtungsbericht mit präziser Registerabstufung',
      GRAMMATIK: 'Subtile Passiversatzformen & Zustandspassiv',
      WORTSCHATZ: 'Nomen-Verb-Verbindungen im Verhandlungsgespräch',
      LESEVERSTEHEN: 'Feuilleton-Glosse: Ironie & implizite Subtexte',
      AUSSPRACHE_PROSODIE: 'Emphatische Satzakzentuierung bei Einwänden',
    };

    const env =
      skill.category === 'INTERAKTION' || skill.category === 'SPONTANES_SPRECHEN'
        ? 'Beruf & Führung (35%)'
        : skill.category === 'HOERVERSTEHEN'
          ? 'Alltag & Medien (40%)'
          : skill.category === 'LESEVERSTEHEN' || skill.category === 'MEDIATION'
            ? 'Technik & Diskurs (25%)'
            : 'Alltag & Soziales (40%)';

    return {
      id: `act_${skill.category.toLowerCase()}`,
      title: SKILL_CATEGORY_META[skill.category].titleDe,
      category: skill.category,
      weaknessSeverity: weakness,
      importanceForC1: importanceOverride ?? defaultImportance,
      recurringErrorsScore: errorsScore,
      timeSinceLastTraining: timeScore,
      recentPerformanceNeed: recentPerformanceScore,
      recommendedTaskTitle: taskTitles[skill.category],
      environment: env,
    };
  },

  evaluate(activity: TrainingActivity, rank = 1): PriorityEvaluation {
    const cWeakness = clamp(activity.weaknessSeverity, 0, 100) * WEAKNESS_WEIGHT;
    const cImportance = clamp(activity.importanceForC1, 0, 100) * IMPORTANCE_WEIGHT;
    const cErrors = clamp(activity.recurringErrorsScore, 0, 100) * RECURRING_ERRORS_WEIGHT;
    const cTime =
      clamp(activity.timeSinceLastTraining, 0, 100) * TIME_SINCE_LAST_TRAINING_WEIGHT;
    const cPerformance =
      clamp(activity.recentPerformanceNeed, 0, 100) * RECENT_PERFORMANCE_WEIGHT;

    const totalScore = clamp(
      cWeakness + cImportance + cErrors + cTime + cPerformance,
      1,
      99,
    );
    const percent = Math.trunc(totalScore);
    const titleDe = SKILL_CATEGORY_META[activity.category].titleDe;
    const headline = `Fokus: ${titleDe} (${percent}% Lernwert)`;

    let rationale = `Prioritäts-Diagnose (${percent}%): `;
    if (activity.recurringErrorsScore >= 40) {
      rationale += 'Ungelöste Wiederholungsfehler verlangen Festigung. ';
    }
    if (activity.timeSinceLastTraining >= 40) {
      rationale += 'Längeres Übungsintervall verstrichen (Auffrischungsbedarf). ';
    } else if (activity.timeSinceLastTraining <= 10) {
      rationale += 'Kürzlich geübt; gezielte Konsolidierung. ';
    }
    if (activity.importanceForC1 >= 80) {
      rationale +=
        'Kritische Kernkompetenz für spontane C1-Handlungsfähigkeit im Alltag & Beruf.';
    } else {
      rationale += 'Spezifische Festigung der Präzision.';
    }

    return {
      activity,
      learningValueScore: totalScore,
      normalizedPriorityPercent: percent,
      rank,
      weaknessContribution: cWeakness,
      importanceContribution: cImportance,
      errorsContribution: cErrors,
      timeSinceTrainingContribution: cTime,
      recentPerformanceContribution: cPerformance,
      rationaleHeadline: headline,
      detailedRationaleDe: rationale,
    };
  },

  rankActivities(activities: TrainingActivity[]): PriorityEvaluation[] {
    return activities
      .map((a) => TrainingPriorityEngine.evaluate(a))
      .sort((a, b) => b.learningValueScore - a.learningValueScore)
      .map((ev, index) => ({ ...ev, rank: index + 1 }));
  },

  getTopTrainingPriority(
    isDiagnosticCompleted: boolean,
    skillScores: Record<SkillCategory, SkillScore>,
    errors: ErrorIntelligenceItem[],
  ): TrainingPriorityRecommendation {
    if (!isDiagnosticCompleted) {
      return {
        category: 'GRAMMATIK',
        calculatedPriority: 0,
        priorityRank: 0,
        reasonHeadline: 'Wähle deinen ersten Übungsfokus',
        detailedRationale:
          'Noch liegt keine persönliche Einstufung vor. Die Beispiel-Drills sind Lernmaterial, keine gespeicherten Fehler von dir.',
        recommendedTaskTitle: 'Starte mit einer Kernfertigkeit',
        sessionDurationMin: 18,
        targetEnvironment: 'Freie Auswahl',
        weightBreakdown: {
          severityOfWeaknessPct: 0,
          functionalImportancePct: 0,
          recurringErrorsPct: 0,
          daysSinceTrainedPct: 0,
          fatiguePerformancePct: 0,
        },
      };
    }

    const activities = Object.values(skillScores).map((skill) => {
      const unresolvedCount = errors.filter(
        (e) =>
          e.category === skillCategoryToErrorCategory(skill.category) &&
          e.masteryStage !== 'GEMEISTERT',
      ).length;
      return TrainingPriorityEngine.createActivityFromSkill(
        skill,
        unresolvedCount,
        null,
        30,
      );
    });

    const best = TrainingPriorityEngine.rankActivities(activities)[0];
    const activity = best.activity;

    return {
      category: activity.category,
      calculatedPriority: best.normalizedPriorityPercent,
      priorityRank: 1,
      reasonHeadline: best.rationaleHeadline,
      detailedRationale: best.detailedRationaleDe,
      recommendedTaskTitle: activity.recommendedTaskTitle,
      sessionDurationMin: 18,
      targetEnvironment: activity.environment,
      weightBreakdown: {
        severityOfWeaknessPct: Math.trunc(activity.weaknessSeverity),
        functionalImportancePct: Math.trunc(activity.importanceForC1),
        recurringErrorsPct: Math.trunc(activity.recurringErrorsScore),
        daysSinceTrainedPct: Math.trunc(activity.timeSinceLastTraining),
        fatiguePerformancePct: Math.trunc(activity.recentPerformanceNeed),
      },
    };
  },
};
