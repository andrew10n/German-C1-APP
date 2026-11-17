import React, { useMemo, useState } from 'react';
import {
  AlertCircle,
  ArrowRight,
  CheckCircle2,
  Plus,
  X,
} from 'lucide-react';
import { ImprintSentenceSurgeryCard } from '../components/ImprintInteractiveDiagrams';
import {
  RealLifeAddModal,
  RealLifeReviewModal,
} from '../components/ProgrammeModals';
import { curriculumUnits } from '../data/curriculumCatalogue';
import {
  ERROR_CATEGORY_LABELS,
  MASTERY_STAGE_LABELS,
} from '../data/learningData';
import {
  REAL_LIFE_ISSUE_LABELS,
  WEAKNESS_SIGNAL_LABELS,
  WeaknessEngine,
} from '../engines/weaknessEngine';
import { useApp } from '../state/AppContext';
import type { ErrorCategory, RealLifeExperience } from '../types';

type ErrorTab = 'PERSONAL' | 'DRILLS';

export const ErrorIntelligenceScreen: React.FC = () => {
  const {
    attempts,
    realLifeExperiences,
    errorDatabase,
    selectedErrorForDrill,
    errorDrillUserAnswer,
    errorDrillFeedback,
    selectErrorForDrill,
    clearSelectedErrorForDrill,
    updateErrorDrillAnswer,
    submitErrorDrillAnswer,
    advanceErrorStage,
    openUnitInProgramme,
  } = useApp();

  const [activeTab, setActiveTab] = useState<ErrorTab>('PERSONAL');
  const [showAllMistakes, setShowAllMistakes] = useState(false);
  const [categoryFilter, setCategoryFilter] = useState<ErrorCategory | 'ALL'>('ALL');
  const [showRealLifeAdd, setShowRealLifeAdd] = useState(false);
  const [reviewingRealLife, setReviewingRealLife] = useState<RealLifeExperience | null>(null);

  const now = Date.now();

  const mistakes = useMemo(
    () =>
      attempts
        .filter((a) => a.errors.length > 0)
        .flatMap((a) => a.errors.map((err) => ({ attempt: a, errorText: err })))
        .sort((a, b) => b.attempt.timestamp - a.attempt.timestamp),
    [attempts],
  );

  const weaknessProfile = useMemo(
    () => WeaknessEngine.profile(attempts, realLifeExperiences, now),
    [attempts, realLifeExperiences, now],
  );
  const topWeaknesses = WeaknessEngine.top(weaknessProfile);

  const filteredDrills =
    categoryFilter === 'ALL'
      ? errorDatabase
      : errorDatabase.filter((e) => e.category === categoryFilter);

  return (
    <div className="space-y-5">
      <div className="flex flex-wrap items-end justify-between gap-3">
        <div>
          <p className="text-[11px] font-bold uppercase tracking-wider text-[var(--coral)]">
            {mistakes.length} Fehlerhinweise · {realLifeExperiences.length} Alltagssituationen
          </p>
          <h1 className="font-display text-3xl font-extrabold text-[var(--ink)]">
            Fehlerlog & Reparatur
          </h1>
        </div>

        <button
          type="button"
          onClick={() => setShowRealLifeAdd(true)}
          className="inline-flex items-center gap-1.5 rounded-xl bg-[var(--accent)] px-3.5 py-2 text-xs font-extrabold text-[var(--on-accent)]"
        >
          <Plus className="h-3.5 w-3.5" />
          Alltagssituation
        </button>
      </div>

      {/* 2 Clean Tabs: Personal Mistakes vs Curated C1 Transfer Drills */}
      <div className="grid grid-cols-2 gap-1.5 rounded-2xl border border-[var(--line)] bg-[var(--surface)] p-1.5">
        <button
          type="button"
          onClick={() => setActiveTab('PERSONAL')}
          className={`rounded-xl py-2 text-xs font-extrabold transition ${
            activeTab === 'PERSONAL'
              ? 'bg-[var(--accent)] text-[var(--on-accent)]'
              : 'text-[var(--ink2)] hover:text-[var(--ink)]'
          }`}
        >
          Mein Fehlerlog & Alltag ({mistakes.length + realLifeExperiences.length})
        </button>
        <button
          type="button"
          onClick={() => setActiveTab('DRILLS')}
          className={`rounded-xl py-2 text-xs font-extrabold transition ${
            activeTab === 'DRILLS'
              ? 'bg-[var(--accent)] text-[var(--on-accent)]'
              : 'text-[var(--ink2)] hover:text-[var(--ink)]'
          }`}
        >
          C1-Transfer-Drills ({errorDatabase.length})
        </button>
      </div>

      {/* TAB 1: PERSONAL MISTAKES & REAL-LIFE REPAIRS */}
      {activeTab === 'PERSONAL' && (
        <div className="c1-animate-enter grid gap-4 md:grid-cols-2">
          <section className="space-y-3 rounded-3xl border border-[var(--line)] bg-[var(--surface)] p-5">
            <div className="flex items-center justify-between gap-2">
              <h2 className="text-sm font-extrabold text-[var(--ink)]">
                Gespeicherte Fehlerhinweise ({mistakes.length})
              </h2>
              {mistakes.length > 5 && (
                <button
                  type="button"
                  onClick={() => setShowAllMistakes((v) => !v)}
                  className="text-xs font-bold text-[var(--accent-ink)]"
                >
                  {showAllMistakes ? 'Weniger' : 'Alle anzeigen'}
                </button>
              )}
            </div>
            {mistakes.length === 0 ? (
              <p className="rounded-2xl border border-[var(--line)] bg-[var(--surface2)] p-3.5 text-xs text-[var(--ink2)]">
                Noch keine gespeicherten Fehlerhinweise aus Lektionen.
              </p>
            ) : (
              <div className="space-y-2">
                {(showAllMistakes ? mistakes : mistakes.slice(0, 5)).map(
                  ({ attempt, errorText }, idx) => {
                    const unitObj = curriculumUnits.find((u) => u.id === attempt.unitId);
                    return (
                      <div
                        key={`${attempt.id}-${idx}`}
                        className="rounded-2xl border border-[var(--line)] bg-[var(--surface2)] p-3 text-xs"
                      >
                        <div className="flex items-center justify-between gap-2">
                          <p className="font-bold text-[var(--ink)]">
                            {unitObj?.title ?? attempt.unitId} · {attempt.taskId}
                          </p>
                          <span className="text-[11px] text-[var(--ink2)]">
                            {new Date(attempt.timestamp).toLocaleDateString('de-DE')}
                          </span>
                        </div>
                        <p className="mt-1 text-[var(--coral)]">{errorText}</p>
                        {unitObj && (
                          <div className="mt-2 flex justify-end">
                            <button
                              type="button"
                              onClick={() => openUnitInProgramme(attempt.unitId, attempt.context)}
                              className="inline-flex items-center gap-1 rounded-xl border border-[var(--line)] bg-[var(--surface)] px-2.5 py-1 text-[11px] font-bold text-[var(--ink)] hover:border-[var(--accent)]"
                            >
                              Zur Lektion
                              <ArrowRight className="h-3 w-3" />
                            </button>
                          </div>
                        )}
                      </div>
                    );
                  },
                )}
              </div>
            )}
          </section>

          <section className="space-y-3 rounded-3xl border border-[var(--line)] bg-[var(--surface)] p-5">
            <h2 className="text-sm font-extrabold text-[var(--ink)]">
              Wiederkehrende Signale & Alltagssituationen
            </h2>

            {topWeaknesses.length === 0 && realLifeExperiences.length === 0 ? (
              <p className="rounded-2xl border border-[var(--line)] bg-[var(--surface2)] p-3.5 text-xs text-[var(--ink2)]">
                Noch keine Signale oder Alltagssituationen aufgezeichnet.
              </p>
            ) : (
              <div className="space-y-3">
                {topWeaknesses.length > 0 && (
                  <ul className="space-y-1.5 text-xs text-[var(--ink)]">
                    {topWeaknesses.map((w) => (
                      <li
                        key={w.signal}
                        className="flex items-center justify-between rounded-xl border border-[var(--line)] bg-[var(--surface2)] px-3 py-2"
                      >
                        <span className="font-bold">{WEAKNESS_SIGNAL_LABELS[w.signal]}</span>
                        <span className="tabular-nums text-[var(--ink2)]">{w.sampleCount}×</span>
                      </li>
                    ))}
                  </ul>
                )}

                {realLifeExperiences.map((exp) => {
                  const isDue = exp.nextReviewAt <= now;
                  return (
                    <div
                      key={exp.id}
                      className="flex flex-wrap items-center justify-between gap-2 rounded-2xl border border-[var(--line)] bg-[var(--surface2)] px-3.5 py-2.5 text-xs"
                    >
                      <div className="min-w-0 flex-1">
                        <p className="font-bold text-[var(--ink)]">{exp.situation}</p>
                        <p className="text-[11px] text-[var(--ink2)]">
                          {REAL_LIFE_ISSUE_LABELS[exp.issueType]} · Stufe {exp.reviewStage} ·{' '}
                          {isDue ? 'Reparatur fällig' : 'Wiedervorlage geplant'}
                        </p>
                      </div>
                      <button
                        type="button"
                        onClick={() => setReviewingRealLife(exp)}
                        className="rounded-xl bg-[var(--accent)] px-3 py-1.5 font-bold text-[var(--on-accent)]"
                      >
                        Reparieren
                      </button>
                    </div>
                  );
                })}
              </div>
            )}
          </section>
        </div>
      )}

      {/* TAB 2: COMPACT DRILLS DIRECTORY + INTERACTIVE SURGERY */}
      {activeTab === 'DRILLS' && (
        <div className="c1-animate-enter space-y-4">
          <div className="flex flex-wrap gap-1.5">
            <button
              type="button"
              onClick={() => setCategoryFilter('ALL')}
              className={`rounded-xl border px-3 py-1.5 text-xs font-bold transition ${
                categoryFilter === 'ALL'
                  ? 'border-[var(--accent)] bg-[var(--accent-soft)] text-[var(--accent-ink)]'
                  : 'border-[var(--line)] bg-[var(--surface)] text-[var(--ink2)] hover:text-[var(--ink)]'
              }`}
            >
              Alle ({errorDatabase.length})
            </button>
            {(Object.keys(ERROR_CATEGORY_LABELS) as ErrorCategory[]).map((cat) => {
              const count = errorDatabase.filter((e) => e.category === cat).length;
              if (count === 0) return null;
              const active = categoryFilter === cat;
              return (
                <button
                  key={cat}
                  type="button"
                  onClick={() => setCategoryFilter(cat)}
                  className={`rounded-xl border px-3 py-1.5 text-xs font-bold transition ${
                    active
                      ? 'border-[var(--accent)] bg-[var(--accent-soft)] text-[var(--accent-ink)]'
                      : 'border-[var(--line)] bg-[var(--surface)] text-[var(--ink2)] hover:text-[var(--ink)]'
                  }`}
                >
                  {ERROR_CATEGORY_LABELS[cat]} ({count})
                </button>
              );
            })}
          </div>

          {selectedErrorForDrill && (
            <section className="space-y-3 rounded-3xl border border-[var(--accent)] bg-[var(--surface)] p-5 shadow-md">
              <div className="flex items-start justify-between gap-2">
                <div>
                  <span className="text-[10px] font-bold uppercase tracking-wider text-[var(--accent-ink)]">
                    Aktiver Transfer-Drill ·{' '}
                    {ERROR_CATEGORY_LABELS[selectedErrorForDrill.category]}
                  </span>
                  <h3 className="mt-1 text-base font-extrabold text-[var(--ink)]">
                    {selectedErrorForDrill.transferChallengePrompt}
                  </h3>
                </div>
                <button
                  type="button"
                  onClick={clearSelectedErrorForDrill}
                  className="rounded-xl p-1 text-[var(--ink2)] hover:bg-[var(--surface2)]"
                  aria-label="Drill schließen"
                >
                  <X className="h-5 w-5" />
                </button>
              </div>

              <ImprintSentenceSurgeryCard
                label={ERROR_CATEGORY_LABELS[selectedErrorForDrill.category]}
                before={selectedErrorForDrill.userMistake}
                diagnosis={selectedErrorForDrill.ruleOrNuance}
                after={selectedErrorForDrill.nativeReformulation}
                steps={['Stolperfalle erkennen', 'Regel prüfen', 'C1-Transfer formulieren']}
              />

              <textarea
                rows={3}
                value={errorDrillUserAnswer}
                onChange={(e) => updateErrorDrillAnswer(e.target.value)}
                placeholder="Formuliere deine C1-Lösung frei..."
                className="w-full rounded-2xl border border-[var(--line)] bg-[var(--bg)] p-3 text-sm text-[var(--ink)] focus:border-[var(--accent)] focus:outline-none"
              />

              <div className="flex flex-wrap items-center gap-2">
                <button
                  type="button"
                  onClick={submitErrorDrillAnswer}
                  className="rounded-xl bg-[var(--accent)] px-4 py-2 text-xs font-extrabold text-[var(--on-accent)]"
                >
                  Musterlösung vergleichen
                </button>
                <button
                  type="button"
                  onClick={() => advanceErrorStage(selectedErrorForDrill.id)}
                  className="inline-flex items-center gap-1 rounded-xl border border-[var(--ok)] bg-[var(--ok-bg)] px-3.5 py-2 text-xs font-bold text-[var(--ok)]"
                >
                  <CheckCircle2 className="h-3.5 w-3.5" />
                  Stufe weiter ({MASTERY_STAGE_LABELS[selectedErrorForDrill.masteryStage]})
                </button>
              </div>

              {errorDrillFeedback && (
                <div className="rounded-2xl border border-[var(--ok)]/30 bg-[var(--ok-bg)] p-3 text-xs leading-relaxed text-[var(--ink)]">
                  {errorDrillFeedback}
                </div>
              )}
            </section>
          )}

          {/* Compact Scannable Drill Rows */}
          <div className="space-y-2">
            {filteredDrills.map((err) => {
              const isSelected = selectedErrorForDrill?.id === err.id;
              return (
                <div
                  key={err.id}
                  className={`flex flex-wrap items-center justify-between gap-3 rounded-2xl border p-3.5 text-xs transition ${
                    isSelected
                      ? 'border-[var(--accent)] bg-[var(--accent-soft)]/35'
                      : 'border-[var(--line)] bg-[var(--surface)]'
                  }`}
                >
                  <div className="min-w-0 flex-1">
                    <p className="text-[11px] font-bold text-[var(--ink2)]">
                      {ERROR_CATEGORY_LABELS[err.category]} ·{' '}
                      <span className="text-[var(--accent-ink)]">
                        {MASTERY_STAGE_LABELS[err.masteryStage]}
                      </span>
                    </p>
                    <p className="mt-0.5 truncate font-extrabold text-[var(--ink)]">
                      „{err.userMistake}“ → „{err.nativeReformulation}“
                    </p>
                  </div>
                  <button
                    type="button"
                    onClick={() => selectErrorForDrill(err)}
                    className="inline-flex shrink-0 items-center gap-1 rounded-xl border border-[var(--line)] bg-[var(--surface2)] px-3 py-1.5 text-xs font-bold text-[var(--ink)] hover:border-[var(--accent)]"
                  >
                    Üben
                    <ArrowRight className="h-3.5 w-3.5" />
                  </button>
                </div>
              );
            })}
          </div>
        </div>
      )}

      {showRealLifeAdd && (
        <RealLifeAddModal
          defaultContext="GENERAL"
          onClose={() => setShowRealLifeAdd(false)}
        />
      )}
      {reviewingRealLife && (
        <RealLifeReviewModal
          experience={reviewingRealLife}
          onClose={() => setReviewingRealLife(null)}
        />
      )}
      {topWeaknesses.length > 0 && mistakes.length === 0 && (
        <div className="flex items-center gap-2 text-[11px] text-[var(--ink2)]">
          <AlertCircle className="h-3.5 w-3.5" />
          <span>Signale basieren auf gespeicherten Lernversuchen.</span>
        </div>
      )}
    </div>
  );
};
