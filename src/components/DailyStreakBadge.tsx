import React, { useState } from 'react';
import { Flame, Shield, Play, X, Check } from 'lucide-react';
import type { DailyStreakState } from '../types';

interface DailyStreakBadgeProps {
  streak: DailyStreakState;
  onStartDailySession: () => void;
}

export const DailyStreakBadge: React.FC<DailyStreakBadgeProps> = ({
  streak,
  onStartDailySession,
}) => {
  const [open, setOpen] = useState(false);
  const pct = Math.round(streak.nextMilestone.progressFraction * 100);

  return (
    <>
      <button
        type="button"
        onClick={() => setOpen(true)}
        className="inline-flex items-center gap-1.5 rounded-full border border-[var(--line)] bg-[var(--surface2)] px-3 py-1.5 text-xs font-bold text-[var(--ink)] transition hover:border-[var(--accent)]"
        aria-label="Lern-Streak anzeigen"
      >
        <Flame
          className={`h-4 w-4 ${
            streak.isPracticedToday
              ? 'text-amber-400 fill-amber-400'
              : 'text-[var(--ink2)]'
          }`}
        />
        <span className="tabular-nums">{streak.currentStreak} Tage</span>
      </button>

      {open && (
        <div
          className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 p-4 backdrop-blur-xs"
          role="dialog"
          aria-modal="true"
        >
          <div className="w-full max-w-md rounded-3xl border border-[var(--line)] bg-[var(--surface)] p-6 text-[var(--ink)] shadow-2xl">
            <div className="flex items-start justify-between gap-3">
              <div className="flex items-center gap-3">
                <div className="flex h-11 w-11 items-center justify-center rounded-2xl bg-amber-500/15 text-amber-400">
                  <Flame className="h-6 w-6 fill-amber-400" />
                </div>
                <div>
                  <p className="text-[11px] font-bold uppercase tracking-wider text-[var(--ink2)]">
                    Lern-Kontinuität
                  </p>
                  <h3 className="font-display text-2xl font-extrabold">
                    {streak.currentStreak} Tage Serie
                  </h3>
                </div>
              </div>
              <button
                type="button"
                onClick={() => setOpen(false)}
                className="rounded-xl p-1.5 text-[var(--ink2)] hover:bg-[var(--surface2)]"
                aria-label="Schließen"
              >
                <X className="h-5 w-5" />
              </button>
            </div>

            <div className="mt-5 grid grid-cols-3 gap-2 rounded-2xl border border-[var(--line)] bg-[var(--surface2)] p-3 text-center">
              <div>
                <div className="text-[11px] text-[var(--ink2)]">Aktuell</div>
                <div className="font-display text-xl font-bold tabular-nums">
                  {streak.currentStreak}d
                </div>
              </div>
              <div>
                <div className="text-[11px] text-[var(--ink2)]">Rekord</div>
                <div className="font-display text-xl font-bold tabular-nums">
                  {streak.longestStreak}d
                </div>
              </div>
              <div>
                <div className="text-[11px] text-[var(--ink2)]">Einheiten</div>
                <div className="font-display text-xl font-bold tabular-nums">
                  {streak.totalSessionsCompleted}
                </div>
              </div>
            </div>

            <div className="mt-4">
              <div className="mb-2 flex items-center justify-between text-xs font-semibold text-[var(--ink2)]">
                <span>Wochenrhythmus (Mo–So)</span>
                <span className="inline-flex items-center gap-1 rounded-full bg-[var(--sky-soft)] px-2 py-0.5 text-[11px] font-bold text-[var(--sky)]">
                  <Shield className="h-3 w-3" />
                  {streak.streakFreezesAvailable} Streak-Freeze
                </span>
              </div>
              <div className="grid grid-cols-7 gap-1.5">
                {streak.weeklyDays.map((day) => (
                  <div
                    key={day.dayIndex}
                    className={`flex flex-col items-center justify-center rounded-xl border py-2 text-xs font-bold ${
                      day.isCompleted
                        ? 'border-[var(--accent)] bg-[var(--accent-soft)] text-[var(--accent-ink)]'
                        : day.isToday
                          ? 'border-[var(--ink2)] bg-[var(--surface2)] text-[var(--ink)]'
                          : 'border-[var(--line)] bg-[var(--surface)] text-[var(--ink2)]'
                    }`}
                  >
                    <span>{day.shortLabel}</span>
                    <span className="mt-1">
                      {day.isCompleted ? <Check className="h-3.5 w-3.5" /> : '·'}
                    </span>
                  </div>
                ))}
              </div>
            </div>

            <div className="mt-4 rounded-2xl border border-[var(--line)] bg-[var(--surface2)] p-3.5">
              <div className="flex items-center justify-between text-xs font-bold">
                <span>Nächster Meilenstein: {streak.nextMilestone.titleDe}</span>
                <span className="tabular-nums text-[var(--accent-ink)]">{pct}%</span>
              </div>
              <div className="mt-2 h-2 overflow-hidden rounded-full bg-[var(--line)]">
                <div
                  className="h-full rounded-full bg-[var(--accent)] transition-all"
                  style={{ width: `${pct}%` }}
                />
              </div>
              <p className="mt-2 text-xs text-[var(--ink2)]">
                {streak.nextMilestone.rewardDescriptionDe}
              </p>
            </div>

            <p className="mt-3 text-xs leading-relaxed text-[var(--ink2)]">
              {streak.motivationalQuoteDe}
            </p>

            <div className="mt-5 flex gap-2">
              <button
                type="button"
                onClick={() => {
                  setOpen(false);
                  onStartDailySession();
                }}
                className="flex flex-1 items-center justify-center gap-2 rounded-2xl bg-[var(--accent)] px-4 py-3 text-sm font-bold text-[var(--on-accent)] transition hover:opacity-95"
              >
                <Play className="h-4 w-4 fill-current" />
                18-Min. Training starten
              </button>
            </div>
          </div>
        </div>
      )}
    </>
  );
};
