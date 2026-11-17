import type {
  Attempt,
  RealLifeExperience,
  RealLifeIssueType,
  WeaknessConfidence,
  WeaknessEvidence,
  WeaknessProfile,
  WeaknessSignal,
  WeaknessSummary,
} from '../types';

export const WEAKNESS_SIGNAL_LABELS: Record<WeaknessSignal, string> = {
  SOUND_RECOGNITION: 'Laute in natürlicher Sprache erkennen',
  WORD_SEGMENTATION: 'Wörter im Redefluss trennen',
  LISTENING_VOCABULARY: 'Wortschatz beim Hören abrufen',
  SITUATIONAL_INFERENCE: 'Situationen und Absichten deuten',
  INFORMATION_RETENTION: 'Informationen über mehrere Äußerungen behalten',
  ACTIVE_RETRIEVAL: 'Ausdrücke aktiv abrufen',
  SENTENCE_FORMULATION: 'Sätze spontan formulieren',
  GRAMMAR_UNDER_PRESSURE: 'Grammatik unter Druck',
  NATURALNESS: 'Natürlich und idiomatisch formulieren',
  REGISTER: 'Register passend wählen',
  RESPONSE_INITIATION: 'Antworten ohne lange Verzögerung beginnen',
  CONVERSATIONAL_REPAIR: 'Nachfragen und Missverständnisse reparieren',
  INTERACTION_MANAGEMENT: 'Unerwartete Gesprächsbeiträge steuern',
  IMPLIED_MEANING: 'Andeutungen und Ironie einordnen',
  MULTI_SPEAKER_TRACKING: 'Mehrere Sprecher verfolgen',
};
export const WEAKNESS_CONFIDENCE_LABELS: Record<WeaknessConfidence, string> = {
  LOW: 'erste Hinweise',
  MEDIUM: 'mehrere Hinweise',
  HIGH: 'wiederkehrendes Muster',
};

export const REAL_LIFE_ISSUE_LABELS: Record<RealLifeIssueType, string> = {
  LISTENING: 'Nicht verstanden',
  RETRIEVAL: 'Wort oder Satz fehlte',
  GRAMMAR_UNDER_PRESSURE: 'Grammatik unter Druck',
  NATURALNESS: 'Klang unnatürlich',
  REGISTER: 'Formell oder informell unsicher',
  REPAIR: 'Nicht gut nachfragen oder reparieren',
};

const DAY_MILLIS = 86_400_000;
const HALF_LIFE_DAYS = 45.0;
const HESITATION_PREFIXES = ['äh', 'ehm', 'hm', 'also', 'na ja'];

function startsWithAnyPrefix(text: string): boolean {
  const lower = text.trim().toLowerCase();
  return HESITATION_PREFIXES.some((p) => lower.startsWith(p));
}

function classifyExperience(experience: RealLifeExperience): WeaknessEvidence[] {
  let signal: WeaknessSignal;
  switch (experience.issueType) {
    case 'LISTENING':
      signal =
        experience.heard.trim().length === 0
          ? 'SITUATIONAL_INFERENCE'
          : 'LISTENING_VOCABULARY';
      break;
    case 'RETRIEVAL':
      signal = 'ACTIVE_RETRIEVAL';
      break;
    case 'GRAMMAR_UNDER_PRESSURE':
      signal = 'GRAMMAR_UNDER_PRESSURE';
      break;
    case 'NATURALNESS':
      signal = 'NATURALNESS';
      break;
    case 'REGISTER':
      signal = 'REGISTER';
      break;
    case 'REPAIR':
      signal = 'CONVERSATIONAL_REPAIR';
      break;
  }

  return [
    {
      signal,
      context: experience.context,
      confidence: 0.55,
      timestamp: experience.createdAt,
      excerpt: experience.said.slice(0, 120),
      source: 'real-life',
    },
  ];
}

function classifyAttempt(attempt: Attempt): WeaknessEvidence[] {
  if (!attempt.response.trim()) return [];
  if (attempt.errors.length === 0 && !startsWithAnyPrefix(attempt.response)) {
    return [];
  }

  const lowerTask = attempt.taskId.toLowerCase();
  let signal: WeaknessSignal;
  if (attempt.errors.length === 0) {
    signal = 'RESPONSE_INITIATION';
  } else if (lowerTask.includes('listening')) {
    signal = 'LISTENING_VOCABULARY';
  } else if (lowerTask.includes('interaction')) {
    signal = 'INTERACTION_MANAGEMENT';
  } else if (lowerTask.includes('speaking') || lowerTask.includes('transfer')) {
    signal = 'SENTENCE_FORMULATION';
  } else {
    signal = 'GRAMMAR_UNDER_PRESSURE';
  }

  const excerptSource =
    attempt.errors.length > 0
      ? attempt.errors.join('; ')
      : attempt.response;

  return [
    {
      signal,
      context: attempt.context,
      confidence: attempt.errors.length === 0 ? 0.3 : 0.5,
      timestamp: attempt.timestamp,
      excerpt: excerptSource.slice(0, 120),
      source: 'attempt',
    },
  ];
}

function confidenceFor(
  effectiveSamples: number,
  average: number,
): WeaknessConfidence {
  if (effectiveSamples >= 2.5 && average >= 0.5) return 'HIGH';
  if (effectiveSamples >= 1.7 || average >= 0.7) return 'MEDIUM';
  return 'LOW';
}

export const WeaknessEngine = {
  profile(
    attempts: Attempt[],
    experiences: RealLifeExperience[],
    now: number,
  ): WeaknessProfile {
    const evidence: WeaknessEvidence[] = [
      ...experiences
        .filter((e) => e.createdAt <= now)
        .flatMap(classifyExperience),
      ...attempts
        .filter((a) => a.timestamp <= now)
        .flatMap(classifyAttempt),
    ];

    const grouped = new Map<WeaknessSignal, WeaknessEvidence[]>();
    for (const item of evidence) {
      const list = grouped.get(item.signal) ?? [];
      list.push(item);
      grouped.set(item.signal, list);
    }

    const summaries: WeaknessSummary[] = [];
    for (const [signal, items] of grouped.entries()) {
      const ordered = [...items].sort((a, b) => b.timestamp - a.timestamp);
      const count = items.length;
      const contexts = Array.from(new Set(items.map((i) => i.context)));
      const recencyWeights = items.map((item) => {
        const ageDays = Math.max(0, now - item.timestamp) / DAY_MILLIS;
        return Math.pow(0.5, ageDays / HALF_LIFE_DAYS);
      });
      const effectiveSamples = recencyWeights.reduce((s, w) => s + w, 0);
      const recency = recencyWeights.length > 0 ? Math.max(...recencyWeights) : 0;
      const avgConf =
        items.reduce((sum, i) => sum + i.confidence, 0) / items.length;
      const weightedConfidenceSum = items.reduce(
        (sum, item, idx) => sum + item.confidence * recencyWeights[idx],
        0,
      );
      const priority =
        weightedConfidenceSum +
        Math.log(1 + count) * 0.35 +
        Math.max(0, contexts.length - 1) * 0.25 +
        recency * 0.5;

      summaries.push({
        signal,
        contexts,
        sampleCount: count,
        confidence: confidenceFor(effectiveSamples, avgConf),
        priority,
        latestEvidence: ordered[0],
      });
    }

    summaries.sort((a, b) => b.priority - a.priority);
    return { summaries };
  },

  top(profile: WeaknessProfile, limit = 3): WeaknessSummary[] {
    return profile.summaries.slice(0, limit);
  },
};
