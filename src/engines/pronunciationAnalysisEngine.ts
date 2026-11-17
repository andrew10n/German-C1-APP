import type {
  PhoneticWordTip,
  PronunciationAnalysisResult,
  PronunciationWordResult,
  SpeakingPrompt,
} from '../types';

function cleanPunctuation(word: string): string {
  return word.replace(/[.,!?:;"'„“()—]/g, '').trim();
}

function levenshteinDistance(s1: string, s2: string): number {
  const dp: number[][] = Array.from({ length: s1.length + 1 }, () =>
    Array<number>(s2.length + 1).fill(0),
  );
  for (let i = 0; i <= s1.length; i++) dp[i][0] = i;
  for (let j = 0; j <= s2.length; j++) dp[0][j] = j;

  for (let i = 1; i <= s1.length; i++) {
    for (let j = 1; j <= s2.length; j++) {
      const cost = s1[i - 1] === s2[j - 1] ? 0 : 1;
      dp[i][j] = Math.min(
        dp[i - 1][j] + 1,
        dp[i][j - 1] + 1,
        dp[i - 1][j - 1] + cost,
      );
    }
  }
  return dp[s1.length][s2.length];
}

function calculateSimilarity(s1: string, s2: string): number {
  if (s1 === s2) return 1;
  const maxLen = Math.max(s1.length, s2.length);
  if (maxLen === 0) return 1;
  const dist = levenshteinDistance(s1, s2);
  return Math.max(0, Math.min(1, 1 - dist / maxLen));
}

function clamp(n: number, min: number, max: number): number {
  return Math.max(min, Math.min(max, n));
}

export const PronunciationAnalysisEngine = {
  /**
   * Aligns targetSentence against transcribedSpeech using greedy Levenshtein alignment.
   * Epistemic honesty (matching SpeakingPracticeRepository.kt): acoustic fluency/prosody and CEFR ratings are NOT claimed.
   */
  analyzeSpokenResponse(
    prompt: SpeakingPrompt,
    transcribedSpeech: string,
    confidence = 0.85,
  ): PronunciationAnalysisResult {
    const cleanExpectedWords = prompt.targetSentence
      .split(/\s+/)
      .map(cleanPunctuation)
      .filter((w) => w.length > 0);
    const cleanSpokenWords = transcribedSpeech
      .split(/\s+/)
      .map(cleanPunctuation)
      .filter((w) => w.length > 0);

    const phoneticMap = new Map<string, PhoneticWordTip>();
    for (const tip of prompt.phoneticFocusWords) {
      phoneticMap.set(cleanPunctuation(tip.word).toLowerCase(), tip);
    }

    const wordResults: PronunciationWordResult[] = [];
    let correctWordsCount = 0;
    let acceptableWordsCount = 0;
    let spokenIdx = 0;

    for (const expected of cleanExpectedWords) {
      const normalizedExpected = expected.toLowerCase();
      const tip = phoneticMap.get(normalizedExpected) ?? null;

      if (spokenIdx < cleanSpokenWords.length) {
        const spoken = cleanSpokenWords[spokenIdx];
        const normalizedSpoken = spoken.toLowerCase();
        const similarity = calculateSimilarity(normalizedExpected, normalizedSpoken);

        if (normalizedExpected === normalizedSpoken) {
          correctWordsCount++;
          spokenIdx++;
          wordResults.push({
            expectedWord: expected,
            spokenWord: spoken,
            status: 'PERFECT',
            confidenceScore: confidence,
            ipa: tip?.ipa ?? null,
            phoneticTip: tip?.tipDe ?? null,
          });
        } else if (similarity >= 0.7) {
          acceptableWordsCount++;
          spokenIdx++;
          wordResults.push({
            expectedWord: expected,
            spokenWord: spoken,
            status: 'ACCEPTABLE',
            confidenceScore: confidence * similarity,
            ipa: tip?.ipa ?? null,
            phoneticTip: tip?.tipDe ?? null,
          });
        } else if (similarity >= 0.4) {
          spokenIdx++;
          wordResults.push({
            expectedWord: expected,
            spokenWord: spoken,
            status: 'MISPRONOUNCED',
            confidenceScore: confidence * similarity,
            ipa: tip?.ipa ?? null,
            phoneticTip: tip?.tipDe ?? null,
          });
        } else {
          const nextSpokenMatches = cleanSpokenWords
            .slice(spokenIdx + 1)
            .some((w) => w.toLowerCase() === normalizedExpected);
          if (nextSpokenMatches) {
            spokenIdx++;
            wordResults.push({
              expectedWord: expected,
              spokenWord: spoken,
              status: 'MISPRONOUNCED',
              confidenceScore: 0.4,
              ipa: tip?.ipa ?? null,
              phoneticTip: tip?.tipDe ?? null,
            });
          } else {
            wordResults.push({
              expectedWord: expected,
              spokenWord: null,
              status: 'MISSING',
              confidenceScore: 0,
              ipa: tip?.ipa ?? null,
              phoneticTip: tip?.tipDe ?? null,
            });
          }
        }
      } else {
        wordResults.push({
          expectedWord: expected,
          spokenWord: null,
          status: 'MISSING',
          confidenceScore: 0,
          ipa: tip?.ipa ?? null,
          phoneticTip: tip?.tipDe ?? null,
        });
      }
    }

    const totalExpected = Math.max(1, cleanExpectedWords.length);
    const rawAccuracy = clamp(
      Math.trunc(((correctWordsCount * 1.0 + acceptableWordsCount * 0.7) / totalExpected) * 100),
      0,
      100,
    );

    return {
      promptId: prompt.id,
      overallScore: rawAccuracy,
      accuracyScore: rawAccuracy,
      fluencyScore: 0,
      prosodyScore: 0,
      transcribedText:
        transcribedSpeech.trim().length > 0
          ? transcribedSpeech
          : 'Keine Spracheingabe erkannt.',
      wordResults,
      feedbackHeadlineDe: 'Transkript-Übereinstimmung',
      cefrBandDe: 'Keine Aussprachemessung oder GER-Einstufung',
      actionableTipsDe: [
        'Verglichen werden Zieltext und erkanntes Transkript. Erkennungsfehler sind möglich.',
        'Artikulation, Phoneme, Sprechtempo und Prosodie werden nicht gemessen. Allgemeine Lauttipps sind Lernmaterial, keine Fehlerdiagnose.',
        'Auch vollständige Textübereinstimmung ist kein Kompetenz- oder Prüfungsnachweis.',
      ],
      assessedAtTimestamp: Date.now(),
    };
  },
};
