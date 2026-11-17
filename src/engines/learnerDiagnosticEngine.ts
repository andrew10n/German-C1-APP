import type {
  Attempt,
  DiagnosticConfidence,
  DiagnosticDimension,
  DiagnosticFinding,
  DiagnosticObservation,
  DiagnosticStatus,
  LearnerDiagnosticProfile,
  RealLifeExperience,
} from '../types';

export const DIAGNOSTIC_DIMENSION_LABELS: Record<DiagnosticDimension, string> = {
  WORD_RECOGNITION: 'Gesprochene Wörter erkennen',
  CONNECTED_SPEECH: 'Redefluss und Wortgrenzen verstehen',
  SITUATIONAL_MEANING: 'Situation und Absicht verstehen',
  EXPRESSION_RETRIEVAL: 'Ausdrücke aktiv abrufen',
  SENTENCE_FORMULATION: 'Sätze unter Druck formulieren',
  NATURAL_SPEAKING: 'Natürlich und idiomatisch sprechen',
  IMPLIED_MEANING: 'Andeutungen und Ironie einordnen',
  MULTI_SPEAKER_TRACKING: 'Mehrere Sprecher verfolgen',
  INFORMATION_RETENTION: 'Informationen behalten',
  SPONTANEOUS_RESPONSE: 'Spontan reagieren',
  RESPONSE_INITIATION: 'Antworten ohne lange Verzögerung beginnen',
  INTERRUPTION_HANDLING: 'Unterbrechungen bewältigen',
  CONVERSATIONAL_REPAIR: 'Missverständnisse reparieren',
};

export const DIAGNOSTIC_STATUS_LABELS: Record<DiagnosticStatus, string> = {
  DEMONSTRATED: 'beobachtet',
  FRAGILE: 'wiederholen',
  NOT_OBSERVED: 'noch nicht beobachtet',
};

export const DIAGNOSTIC_CONFIDENCE_LABELS: Record<DiagnosticConfidence, string> = {
  LOW: 'vorsichtiger Hinweis',
  MEDIUM: 'mehrere Beobachtungen',
  HIGH: 'wiederkehrendes Muster',
};

const ALL_DIAGNOSTIC_DIMENSIONS = Object.keys(
  DIAGNOSTIC_DIMENSION_LABELS,
) as DiagnosticDimension[];

function fromAttempt(attempt: Attempt): DiagnosticObservation[] {
  const response = attempt.response.trim();
  if (!response) return [];
  const evidence = attempt.errors[0] ?? response;
  const fragile = attempt.errors.length > 0;
  const confidence = attempt.source === 'AUTOMATIC_QUIZ' ? 0.8 : 0.4;

  if (attempt.taskId === 'quiz' && attempt.source === 'AUTOMATIC_QUIZ') {
    return [
      {
        dimension: 'SITUATIONAL_MEANING',
        status:
          attempt.scores.COMPREHENSION === 4 ? 'DEMONSTRATED' : 'FRAGILE',
        confidence,
        evidence,
        timestamp: attempt.timestamp,
      },
    ];
  }

  if (
    attempt.taskId === 'writing' ||
    attempt.taskId === 'controlled' ||
    attempt.taskId === 'speaking' ||
    attempt.taskId === 'transfer'
  ) {
    return [
      {
        dimension: 'SENTENCE_FORMULATION',
        status: fragile ? 'FRAGILE' : 'NOT_OBSERVED',
        confidence: fragile ? confidence : 0.2,
        evidence: fragile
          ? evidence
          : 'Keine bekannte Regelverletzung gefunden; freier Text nicht vollständig bewertet.',
        timestamp: attempt.timestamp,
      },
    ];
  }

  if (attempt.taskId === 'listening') {
    return [
      {
        dimension: 'WORD_RECOGNITION',
        status: 'NOT_OBSERVED',
        confidence: 0.1,
        evidence: 'Textantwort ohne kontrollierte Audioauswertung.',
        timestamp: attempt.timestamp,
      },
    ];
  }

  if (attempt.taskId === 'interaction') {
    return [
      {
        dimension: 'SPONTANEOUS_RESPONSE',
        status: 'NOT_OBSERVED',
        confidence: 0.1,
        evidence: 'Getippte Antwort ohne Live-Interaktion oder Zeitmessung.',
        timestamp: attempt.timestamp,
      },
    ];
  }

  return [];
}

function fromExperience(experience: RealLifeExperience): DiagnosticObservation[] {
  let dimension: DiagnosticDimension;
  switch (experience.issueType) {
    case 'LISTENING':
      dimension = 'WORD_RECOGNITION';
      break;
    case 'RETRIEVAL':
      dimension = 'EXPRESSION_RETRIEVAL';
      break;
    case 'GRAMMAR_UNDER_PRESSURE':
      dimension = 'SENTENCE_FORMULATION';
      break;
    case 'NATURALNESS':
      dimension = 'NATURAL_SPEAKING';
      break;
    case 'REGISTER':
      dimension = 'SITUATIONAL_MEANING';
      break;
    case 'REPAIR':
      dimension = 'CONVERSATIONAL_REPAIR';
      break;
  }

  let primaryEvidence: string;
  if (experience.issueType === 'LISTENING') {
    primaryEvidence =
      experience.heard.trim() || 'Gesprochenes wurde nicht sicher erkannt.';
  } else if (experience.issueType === 'RETRIEVAL') {
    primaryEvidence =
      experience.said.trim() ||
      'Der Ausdruck war in der Situation nicht abrufbar.';
  } else {
    primaryEvidence =
      experience.said.trim() || 'Keine eigene Formulierung dokumentiert.';
  }

  const observations: DiagnosticObservation[] = [
    {
      dimension,
      status: 'FRAGILE',
      confidence: 0.55,
      evidence: primaryEvidence,
      timestamp: experience.createdAt,
    },
  ];

  if (!experience.said.trim() && dimension !== 'EXPRESSION_RETRIEVAL') {
    observations.push({
      dimension: 'EXPRESSION_RETRIEVAL',
      status: 'FRAGILE',
      confidence: 0.5,
      evidence: 'Keine eigene Formulierung dokumentiert.',
      timestamp: experience.createdAt,
    });
  }

  if (
    experience.heard.trim() &&
    experience.intended.trim() &&
    experience.heard !== experience.intended
  ) {
    observations.push({
      dimension: 'SITUATIONAL_MEANING',
      status: 'FRAGILE',
      confidence: 0.45,
      evidence: experience.heard,
      timestamp: experience.createdAt,
    });
  }

  return observations;
}

function confidenceFor(count: number, average: number): DiagnosticConfidence {
  if (count >= 3 && average >= 0.55) return 'HIGH';
  if (count >= 2 || average >= 0.7) return 'MEDIUM';
  return 'LOW';
}

export const LearnerDiagnosticEngine = {
  analyze(
    attempts: Attempt[],
    experiences: RealLifeExperience[],
    now: number,
  ): LearnerDiagnosticProfile {
    const observations: DiagnosticObservation[] = [
      ...attempts.filter((a) => a.timestamp <= now).flatMap(fromAttempt),
      ...experiences
        .filter((e) => e.createdAt <= now)
        .flatMap(fromExperience),
    ];

    const findings: DiagnosticFinding[] = [];
    for (const dimension of ALL_DIAGNOSTIC_DIMENSIONS) {
      const matching = observations.filter((o) => o.dimension === dimension);
      if (matching.length === 0) continue;
      const fragile = matching.filter((o) => o.status === 'FRAGILE').length;
      const demonstrated = matching.filter(
        (o) => o.status === 'DEMONSTRATED',
      ).length;
      const avgConf =
        matching.reduce((s, o) => s + o.confidence, 0) / matching.length;
      const status: DiagnosticStatus =
        fragile > demonstrated
          ? 'FRAGILE'
          : demonstrated > 0
            ? 'DEMONSTRATED'
            : 'NOT_OBSERVED';

      findings.push({
        dimension,
        status,
        confidence:
          demonstrated === 0 && fragile === 0
            ? 'LOW'
            : confidenceFor(matching.length, avgConf),
        sampleCount: matching.length,
        evidence: [...matching]
          .sort((a, b) => b.timestamp - a.timestamp)
          .slice(0, 2)
          .map((o) => o.evidence.slice(0, 120)),
      });
    }

    findings.sort((a, b) => {
      const aFrag = a.status === 'FRAGILE' ? 0 : 1;
      const bFrag = b.status === 'FRAGILE' ? 0 : 1;
      return aFrag - bFrag || b.sampleCount - a.sampleCount;
    });

    return { findings };
  },
};
