import React from 'react';
import { Compass, ShieldCheck, Trophy } from 'lucide-react';
import { ALL_SKILL_CATEGORIES, SKILL_CATEGORY_META } from '../data/learningData';
import type { SkillCategory } from '../types';

interface SummaryDashboardProps {
  masteredCount: number;
  totalUnitsCount: number;
  completedCheckpoints: number;
  totalCheckpoints: number;
  humanReviewsCount: number;
  dueReviewsCount: number;
  liveCategoryEvidence: Record<
    SkillCategory,
    { scorePct: number; avgFour: number | null; reviewCount: number; statusNote: string }
  >;
}

export const SummaryDashboard: React.FC<SummaryDashboardProps> = ({
  masteredCount,
  totalUnitsCount,
  completedCheckpoints,
  totalCheckpoints,
  humanReviewsCount,
  dueReviewsCount,
  liveCategoryEvidence,
}) => {
  const progressRatio = Math.min(1, Math.max(0, masteredCount / Math.max(1, totalUnitsCount)));
  // SVG gauge arc math for a semi-circle or arc (radius 36, circumference ~226)
  const arcRadius = 36;
  const arcCircumference = Math.PI * arcRadius; // ~113.1
  const arcOffset = arcCircumference * (1 - progressRatio);

  return (
    <div className="grid gap-4 md:grid-cols-3">
      {/* 1. Mastered Units Gauge Arc Card */}
      <section className="flex flex-col justify-between rounded-3xl border border-[var(--line)] bg-[var(--surface)] p-5 shadow-sm">
        <div className="flex items-center justify-between gap-2">
          <span className="text-[10px] font-extrabold uppercase tracking-wider text-[var(--accent-ink)]">
            C1-Nachweisstatus
          </span>
          <Compass className="h-4 w-4 text-[var(--accent-ink)]" />
        </div>

        <div className="my-3 flex items-center justify-center">
          <div className="relative flex h-24 w-28 items-center justify-center">
            <svg className="h-28 w-28 -rotate-90 overflow-visible" viewBox="0 0 96 96">
              <circle
                cx="48"
                cy="48"
                r={arcRadius}
                fill="none"
                stroke="var(--surface2)"
                strokeWidth="8"
              />
              <circle
                cx="48"
                cy="48"
                r={arcRadius}
                fill="none"
                stroke="var(--accent)"
                strokeWidth="8"
                strokeLinecap="round"
                strokeDasharray={arcCircumference}
                strokeDashoffset={arcOffset}
                className="transition-all duration-700"
              />
            </svg>
            <div className="absolute inset-0 flex flex-col items-center justify-center pt-2 text-center">
              <span className="font-display text-xl font-black tabular-nums text-[var(--ink)]">
                {masteredCount} / {totalUnitsCount}
              </span>
              <span className="text-[9px] font-bold text-[var(--ink2)]">Nachgewiesen</span>
            </div>
          </div>
        </div>

        <div className="flex items-center justify-between border-t border-[var(--line)] pt-2.5 text-[11px] text-[var(--ink2)]">
          <span>{dueReviewsCount} Wiederholung(en) fällig</span>
          <span className="font-bold text-[var(--ok)]">{humanReviewsCount} Rubrikprüfungen</span>
        </div>
      </section>

      {/* 2. Checkpoint & Review Dot-Matrix Card */}
      <section className="flex flex-col justify-between rounded-3xl border border-[var(--line)] bg-[var(--surface)] p-5 shadow-sm">
        <div className="flex items-center justify-between gap-2">
          <span className="text-[10px] font-extrabold uppercase tracking-wider text-[var(--violet)]">
            Evidenz & Checkpoints
          </span>
          <ShieldCheck className="h-4 w-4 text-[var(--violet)]" />
        </div>

        <div className="my-2 space-y-2">
          <div>
            <div className="flex items-center justify-between text-xs">
              <span className="font-bold text-[var(--ink)]">Checkpoints bestritten</span>
              <span className="font-extrabold tabular-nums text-[var(--accent-ink)]">
                {completedCheckpoints} / {totalCheckpoints}
              </span>
            </div>
            {/* Dot-Matrix row */}
            <div className="mt-1.5 flex flex-wrap gap-1">
              {Array.from({ length: Math.min(16, totalCheckpoints) }).map((_, i) => {
                const filled = i < completedCheckpoints;
                return (
                  <span
                    key={i}
                    className={`h-2.5 w-2.5 rounded-full transition ${
                      filled ? 'bg-[var(--accent)]' : 'bg-[var(--surface2)]'
                    }`}
                    title={`Checkpoint ${i + 1}: ${filled ? 'Erfasst' : 'Ausstehend'}`}
                  />
                );
              })}
            </div>
          </div>
        </div>

        <div className="border-t border-[var(--line)] pt-2.5 text-[11px] text-[var(--ink2)]">
          Keine künstlichen Fortschrittsbalken – rein evidenzbasiert.
        </div>
      </section>

      {/* 3. Core Competency Qualitative Dot-Matrix Summary Card */}
      <section className="flex flex-col justify-between rounded-3xl border border-[var(--line)] bg-[var(--surface)] p-5 shadow-sm">
        <div className="flex items-center justify-between gap-2">
          <span className="text-[10px] font-extrabold uppercase tracking-wider text-[var(--ok)]">
            Kompetenz-Matrix (9 Dimensionen)
          </span>
          <Trophy className="h-4 w-4 text-[var(--ok)]" />
        </div>

        <div className="my-2 space-y-1.5">
          {ALL_SKILL_CATEGORIES.slice(0, 4).map((cat) => {
            const meta = SKILL_CATEGORY_META[cat];
            const ev = liveCategoryEvidence[cat];
            const hasEvidence = ev.avgFour !== null;
            const levelDots = hasEvidence ? Math.min(5, Math.round((ev.avgFour ?? 0) / 0.8)) : 0;
            return (
              <div key={cat} className="flex items-center justify-between gap-2 text-xs">
                <span className="truncate font-semibold text-[var(--ink)]">{meta.shortDe}</span>
                {/* 5-dot matrix for qualitative level instead of percentage numbers */}
                <div className="flex items-center gap-1">
                  {Array.from({ length: 5 }).map((_, dIdx) => (
                    <span
                      key={dIdx}
                      className={`h-2 w-2 rounded-full transition ${
                        dIdx < levelDots
                          ? 'bg-[var(--ok)]'
                          : hasEvidence
                            ? 'bg-[var(--accent)]/40'
                            : 'bg-[var(--surface2)]'
                      }`}
                    />
                  ))}
                </div>
              </div>
            );
          })}
        </div>

        <div className="border-t border-[var(--line)] pt-2.5 text-[11px] text-[var(--ink2)]">
          Qualitative Niveaustufen ohne numerische Prozent-Glättung.
        </div>
      </section>
    </div>
  );
};
