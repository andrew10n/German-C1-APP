import React, { useMemo, useState } from 'react';
import { ArrowRight, CheckCircle2, Compass, Lock } from 'lucide-react';
import { CONTEXT_LABELS } from '../data/learningData';
import { getUnitVisualDeck } from '../data/visualLessonDecks';
import { lessonVisualBlueprint } from '../engines/lessonVisualParser';
import { MasteryEngine } from '../engines/masteryEngine';
import type { Attempt, ContextTrack, CurriculumUnit } from '../types';
import { LessonVisualGraphic } from './LessonVisualStudio';

interface StageMetroMapProps {
  coreUnits: CurriculumUnit[];
  stages: Record<number, string>;
  attempts: Attempt[];
  lastUnitId: string | null;
  selectedContext: ContextTrack;
  onOpenUnit: (unit: CurriculumUnit) => void;
}

const STAGE_ACCENTS = [
  'var(--accent)',
  'var(--sky)',
  'var(--violet)',
  'var(--ok)',
  'var(--coral)',
  'var(--accent)',
];

const STAGE_NUMS = [1, 2, 3, 4, 5, 6];

export const StageMetroMap: React.FC<StageMetroMapProps> = ({
  coreUnits,
  stages,
  attempts,
  lastUnitId,
  selectedContext,
  onOpenUnit,
}) => {
  const [focusedUnitId, setFocusedUnitId] = useState<string | null>(
    lastUnitId ?? coreUnits[0]?.id ?? null,
  );

  const focusedUnit = useMemo(
    () => coreUnits.find((u) => u.id === focusedUnitId) ?? coreUnits[0] ?? null,
    [coreUnits, focusedUnitId],
  );

  const stageGroups = useMemo(
    () =>
      STAGE_NUMS.map((num) => ({
        stage: num,
        title: stages[num] ?? `Stufe ${num}`,
        units: coreUnits.filter((u) => u.stage === num),
        color: STAGE_ACCENTS[(num - 1) % STAGE_ACCENTS.length],
      })),
    [coreUnits, stages],
  );

  const totalMastered = useMemo(
    () => coreUnits.filter((u) => MasteryEngine.isMastered(u, attempts)).length,
    [coreUnits, attempts],
  );

  const focusedDeck = focusedUnit ? getUnitVisualDeck(focusedUnit.id) : null;
  const focusedBlueprint = focusedUnit ? lessonVisualBlueprint(focusedUnit.id) : null;

  return (
    <section className="c1-animate-enter space-y-4 rounded-3xl border border-[var(--line)] bg-[var(--surface)] p-5 shadow-sm">
      <div className="flex flex-wrap items-center justify-between gap-2">
        <div>
          <p className="text-[11px] font-bold uppercase tracking-wider text-[var(--accent-ink)]">
            Interaktive C1-Linienkarte · Stufen 1–6
          </p>
          <h3 className="font-display text-xl font-extrabold text-[var(--ink)]">
            Lernpfad & Stationen im Überblick
          </h3>
        </div>
        <div className="flex items-center gap-3 text-[11px] font-bold text-[var(--ink2)]">
          <span className="inline-flex items-center gap-1 text-[var(--ok)]">
            <CheckCircle2 className="h-3.5 w-3.5" />
            {totalMastered} nachgewiesen
          </span>
          <span className="inline-flex items-center gap-1 text-[var(--accent-ink)]">
            <Compass className="h-3.5 w-3.5" />
            {coreUnits.length} Stationen
          </span>
        </div>
      </div>

      {/* Interactive Animated Metro Route Graph */}
      <div className="overflow-x-auto pb-2">
        <div className="min-w-[640px] space-y-3">
          {stageGroups.map((group) => {
            const masteredInStage = group.units.filter((u) =>
              MasteryEngine.isMastered(u, attempts),
            ).length;
            const stageRatio =
              group.units.length > 0 ? masteredInStage / group.units.length : 0;

            return (
              <div
                key={group.stage}
                className="rounded-2xl border border-[var(--line)] bg-[var(--surface2)] px-4 py-3"
              >
                <div className="mb-2.5 flex items-center justify-between gap-2">
                  <div className="flex items-center gap-2">
                    <span
                      className="flex h-6 w-6 items-center justify-center rounded-lg text-xs font-extrabold text-[var(--on-accent)]"
                      style={{ backgroundColor: group.color }}
                    >
                      {group.stage}
                    </span>
                    <span className="text-xs font-extrabold text-[var(--ink)]">
                      {group.title}
                    </span>
                  </div>
                  <div className="flex items-center gap-2">
                    <div className="h-1.5 w-16 overflow-hidden rounded-full bg-[var(--surface)]">
                      <div
                        className="h-full rounded-full transition-all duration-500"
                        style={{
                          width: `${Math.round(stageRatio * 100)}%`,
                          backgroundColor: group.color,
                        }}
                      />
                    </div>
                    <span className="text-[11px] font-bold tabular-nums text-[var(--ink2)]">
                      {masteredInStage} / {group.units.length}
                    </span>
                  </div>
                </div>

                {/* Connected Metro Nodes */}
                <div className="relative flex items-center justify-between gap-2 pt-1">
                  <div className="pointer-events-none absolute right-4 left-4 top-5 h-1 -translate-y-1/2 rounded-full bg-[var(--line)]" />
                  <svg
                    className="pointer-events-none absolute right-4 left-4 top-5 h-2 w-[calc(100%-2rem)] -translate-y-1/2 overflow-visible"
                    preserveAspectRatio="none"
                    viewBox="0 0 100 4"
                  >
                    <line
                      x1="0"
                      y1="2"
                      x2="100"
                      y2="2"
                      stroke={group.color}
                      strokeWidth="2.5"
                      strokeOpacity={stageRatio > 0 ? 0.85 : 0.35}
                      className="c1-svg-flow"
                    />
                  </svg>

                  {group.units.map((unit, unitIdx) => {
                    const mastered = MasteryEngine.isMastered(unit, attempts);
                    const unlocked = MasteryEngine.isUnlocked(unit, attempts);
                    const isFocused = focusedUnit?.id === unit.id;
                    const isLast = lastUnitId === unit.id;

                    return (
                      <button
                        key={unit.id}
                        type="button"
                        onClick={() => setFocusedUnitId(unit.id)}
                        onDoubleClick={() => onOpenUnit(unit)}
                        className="group relative z-10 flex flex-1 flex-col items-center text-center focus:outline-none"
                      >
                        <div
                          className={`flex h-8 w-8 items-center justify-center rounded-full border-2 text-[10px] font-black transition-transform duration-200 group-hover:scale-110 ${
                            mastered
                              ? 'border-[var(--ok)] bg-[var(--ok)] text-white shadow-sm'
                              : isFocused
                                ? 'scale-110 border-[var(--accent)] bg-[var(--surface)] text-[var(--accent-ink)] shadow-md'
                                : unlocked
                                  ? 'border-[var(--accent)]/70 bg-[var(--surface)] text-[var(--ink)]'
                                  : 'border-[var(--line)] bg-[var(--surface)] text-[var(--ink2)]'
                          }`}
                        >
                          {mastered ? (
                            <CheckCircle2 className="h-4 w-4" />
                          ) : unlocked ? (
                            `${group.stage}.${unitIdx + 1}`
                          ) : (
                            <Lock className="h-3 w-3" />
                          )}
                        </div>
                        <span
                          className={`mt-1.5 line-clamp-1 max-w-[118px] text-[10px] leading-tight ${
                            isFocused || isLast
                              ? 'font-extrabold text-[var(--accent-ink)]'
                              : 'font-semibold text-[var(--ink2)] group-hover:text-[var(--ink)]'
                          }`}
                        >
                          {unit.title}
                        </span>
                      </button>
                    );
                  })}
                </div>
              </div>
            );
          })}
        </div>
      </div>

      {/* Selected Station Live Visual Inspector */}
      {focusedUnit && focusedDeck && focusedBlueprint && (
        <div className="flex flex-wrap items-center justify-between gap-3 rounded-2xl border border-[var(--accent)]/40 bg-[var(--accent-soft)] p-4">
          <div className="flex min-w-0 flex-1 items-center gap-3.5">
            <LessonVisualGraphic graphic={focusedBlueprint.graphic} activeStep={1} compact />
            <div className="min-w-0 flex-1 space-y-1">
              <div className="flex flex-wrap items-center gap-2 text-[10px] font-bold uppercase tracking-wider text-[var(--accent-ink)]">
                <span>Station {focusedUnit.id}</span>
                <span>·</span>
                <span>Stufe {focusedUnit.stage}</span>
                <span>·</span>
                <span>{CONTEXT_LABELS[selectedContext]}</span>
              </div>
              <p className="text-sm font-extrabold text-[var(--ink)]">
                {focusedUnit.title}
              </p>
              <p className="text-xs font-semibold text-[var(--accent-ink)]">
                Denkmodell: {focusedDeck.modelTitle}
              </p>
              <p className="text-xs text-[var(--ink2)]">
                {focusedUnit.contexts[selectedContext]}
              </p>
            </div>
          </div>
          <button
            type="button"
            onClick={() => onOpenUnit(focusedUnit)}
            className="inline-flex shrink-0 items-center gap-1.5 rounded-xl bg-[var(--accent)] px-4 py-2.5 text-xs font-extrabold text-[var(--on-accent)] transition hover:opacity-95"
          >
            Kapitel starten
            <ArrowRight className="h-3.5 w-3.5" />
          </button>
        </div>
      )}
    </section>
  );
};
