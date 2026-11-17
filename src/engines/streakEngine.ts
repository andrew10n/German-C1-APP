import type { DailyStreakState, StreakDayStatus, StreakMilestone } from '../types';

const MOTIVATIONAL_QUOTE_DE =
  'Kontinuität schlägt Intensität: 18 Minuten täglich verankern C1-Strukturen dauerhaft im Sprachzentrum.';

const DAY_LABELS = ['Mo', 'Di', 'Mi', 'Do', 'Fr', 'Sa', 'So'] as const;

function localEpochDay(now = Date.now()): { epochDay: number; isoDayOfWeek: number } {
  const date = new Date(now);
  const utcMid = Date.UTC(date.getFullYear(), date.getMonth(), date.getDate());
  const epochDay = Math.floor(utcMid / 86_400_000);
  // JS getDay(): 0 = Sun, 1 = Mon ... 6 = Sat -> convert to 1 (Mon) .. 7 (Sun)
  const jsDay = date.getDay();
  const isoDayOfWeek = jsDay === 0 ? 7 : jsDay;
  return { epochDay, isoDayOfWeek };
}

export function defaultMilestone(currentStreak: number): StreakMilestone {
  const clamp01 = (v: number) => Math.max(0, Math.min(1, v));
  if (currentStreak < 3) {
    return {
      targetDays: 3,
      titleDe: '3 Tage Berliner Takt',
      rewardDescriptionDe: '+150 XP & Bronze Flammen-Status',
      progressFraction: clamp01(currentStreak / 3),
    };
  }
  if (currentStreak < 7) {
    return {
      targetDays: 7,
      titleDe: '1 Woche U-Bahn Souveränität',
      rewardDescriptionDe: '+1 Streak-Freeze Schutz & Silber-Abzeichen',
      progressFraction: clamp01((currentStreak - 3) / 4),
    };
  }
  if (currentStreak < 14) {
    return {
      targetDays: 14,
      titleDe: '2 Wochen C1 Kontinuität',
      rewardDescriptionDe: 'Goldenes C1-Sprint Abzeichen & Diplomatie-Bonus',
      progressFraction: clamp01((currentStreak - 7) / 7),
    };
  }
  if (currentStreak < 30) {
    return {
      targetDays: 30,
      titleDe: '30 Tage C1 Meisterschaft',
      rewardDescriptionDe: 'Platin-Status & Unbegrenzte Latenz-Analysen',
      progressFraction: clamp01((currentStreak - 14) / 16),
    };
  }
  return {
    targetDays: 100,
    titleDe: '100 Tage Muttersprachler-Niveau',
    rewardDescriptionDe: 'Diamant C1 Legenden-Status',
    progressFraction: clamp01((currentStreak - 30) / 70),
  };
}

export interface PersistedStreakFields {
  currentStreak: number;
  longestStreak: number;
  lastActiveEpochDay: number;
  streakFreezesAvailable: number;
  totalSessionsCompleted: number;
  activeDaysBitmask: number;
  lastActivityTimestamp: number;
}

export function hydrateStreakState(
  raw: PersistedStreakFields,
  now = Date.now(),
): DailyStreakState {
  const { epochDay, isoDayOfWeek } = localEpochDay(now);
  const isPracticedToday =
    raw.totalSessionsCompleted > 0 && raw.lastActiveEpochDay === epochDay;

  const weeklyDays: StreakDayStatus[] = DAY_LABELS.map((shortLabel, idx) => {
    const dayIndex = idx + 1;
    const isCompleted = (raw.activeDaysBitmask & (1 << idx)) !== 0;
    return {
      dayIndex,
      shortLabel,
      isToday: dayIndex === isoDayOfWeek,
      isCompleted,
    };
  });

  return {
    ...raw,
    isPracticedToday,
    weeklyDays,
    nextMilestone: defaultMilestone(raw.currentStreak),
    motivationalQuoteDe: MOTIVATIONAL_QUOTE_DE,
  };
}

export function createEmptyStreakState(now = Date.now()): DailyStreakState {
  return hydrateStreakState(
    {
      currentStreak: 0,
      longestStreak: 0,
      lastActiveEpochDay: 0,
      streakFreezesAvailable: 0,
      totalSessionsCompleted: 0,
      activeDaysBitmask: 0,
      lastActivityTimestamp: 0,
    },
    now,
  );
}

export function recordStreakActivity(
  existing: DailyStreakState,
  now = Date.now(),
): DailyStreakState {
  const { epochDay, isoDayOfWeek } = localEpochDay(now);
  const currentDayBit = 1 << (isoDayOfWeek - 1);
  const daysDifference = epochDay - existing.lastActiveEpochDay;

  let newStreak: number;
  let newLongest: number;
  let newFreezes = existing.streakFreezesAvailable;

  if (existing.totalSessionsCompleted === 0) {
    newStreak = 1;
    newLongest = 1;
  } else if (daysDifference === 0) {
    newStreak = existing.currentStreak;
    newLongest = existing.longestStreak;
  } else if (daysDifference === 1) {
    newStreak = existing.currentStreak + 1;
    newLongest = Math.max(newStreak, existing.longestStreak);
    if (newStreak % 7 === 0) {
      newFreezes += 1;
    }
  } else if (daysDifference === 2 && existing.streakFreezesAvailable > 0) {
    newStreak = existing.currentStreak + 1;
    newLongest = Math.max(newStreak, existing.longestStreak);
    newFreezes -= 1;
  } else {
    newStreak = 1;
    newLongest = Math.max(1, existing.longestStreak);
  }

  const updatedBitmask =
    isoDayOfWeek === 1 && daysDifference > 0
      ? currentDayBit
      : existing.activeDaysBitmask | currentDayBit;

  return hydrateStreakState(
    {
      currentStreak: newStreak,
      longestStreak: newLongest,
      lastActiveEpochDay: epochDay,
      streakFreezesAvailable: newFreezes,
      totalSessionsCompleted: existing.totalSessionsCompleted + 1,
      activeDaysBitmask: updatedBitmask,
      lastActivityTimestamp: now,
    },
    now,
  );
}
