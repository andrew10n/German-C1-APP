export interface ScheduleResult {
  intervalDays: number;
  easeFactor: number;
  repetitions: number;
}

export const RetrievalPolicy = {
  /** Fixed minimum spacing required by the mastery contract; not a card SRS algorithm. */
  intervalsDays: [1, 3, 7, 21] as const,
  errorRetryDays: 1,

  /** Adapt vocabulary and phrase-card intervals to recall quality and each card's history. */
  nextSchedule(
    quality: number,
    previousIntervalDays: number,
    previousEaseFactor: number,
    previousRepetitions: number,
  ): ScheduleResult {
    const score = Math.max(0, Math.min(3, Math.floor(quality)));
    const difference = 3 - score;
    const rawEase =
      previousEaseFactor + (0.1 - difference * (0.08 + difference * 0.02));
    const ease = Math.max(1.3, Math.min(2.8, rawEase));

    if (score < 2) {
      return { intervalDays: 1, easeFactor: ease, repetitions: 0 };
    }

    const repetitions = previousRepetitions + 1;
    let interval: number;
    if (repetitions === 1) {
      interval = score === 3 ? 2 : 1;
    } else if (repetitions === 2) {
      interval = score === 3 ? 4 : 3;
    } else {
      interval = Math.max(
        Math.floor(previousIntervalDays * ease + 0.5),
        previousIntervalDays + 1,
      );
    }
    return { intervalDays: interval, easeFactor: ease, repetitions };
  },
};
