import React, { useEffect, useState } from 'react';
import {
  CheckCircle2,
  ClipboardCheck,
  ShieldCheck,
  Trophy,
  Volume2,
  XCircle,
} from 'lucide-react';
import { FootballTacticalPitch } from '../components/FootballTacticalPitch';
import {
  COACH_TERM_CATEGORY_LABELS,
  footballCoachU15Data,
} from '../data/learningData';
import { speechService } from '../services/speechService';
import { useApp } from '../state/AppContext';
import type { CoachTermCategory } from '../types';

type TabKey = 'GLOSSARY' | 'SCENARIOS' | 'DRILLS';

export const FootballCoachTrackScreen: React.FC = () => {
  const { completedDrillIds, toggleDrillCompleted, recordPracticeActivity } = useApp();
  const [activeTab, setActiveTab] = useState<TabKey>('GLOSSARY');
  const [categoryFilter, setCategoryFilter] = useState<CoachTermCategory | 'ALL'>('ALL');
  const [scenarioAnswers, setScenarioAnswers] = useState<Record<string, number>>({});
  const [speakingId, setSpeakingId] = useState<string | null>(speechService.getSpeakingId());

  useEffect(() => {
    return speechService.subscribe(() => setSpeakingId(speechService.getSpeakingId()));
  }, []);

  const filteredTerms =
    categoryFilter === 'ALL'
      ? footballCoachU15Data.terms
      : footballCoachU15Data.terms.filter(
          (t) => t.category === categoryFilter,
        );

  const speakPhrase = (id: string, text: string) => {
    if (speakingId === id) {
      speechService.stopSpeaking();
    } else {
      speechService.speak(text, { rate: 1.0, id });
    }
  };

  return (
    <div className="space-y-5">
      <div className="rounded-3xl border border-[var(--ok)]/30 bg-[var(--ok-bg)] p-5">
        <div className="flex items-center gap-2 text-xs font-extrabold uppercase tracking-wider text-[var(--ok)]">
          <Trophy className="h-4 w-4" />
          Spezialtrack · E- bis C-Jugend (U10–U15)
        </div>
        <h1 className="mt-1 font-display text-3xl font-extrabold text-[var(--ink)]">
          Fußball-Trainer (U15)
        </h1>
        <p className="mt-1 text-xs leading-relaxed text-[var(--ink2)]">
          DFB-konforme Coaching-Sprache für Training, Kabine und Seitenlinie: kurze
          Handlungsziele statt langer Vorträge.
        </p>
      </div>

      {/* 3 Tabs */}
      <div className="grid grid-cols-3 gap-2 rounded-2xl border border-[var(--line)] bg-[var(--surface)] p-1.5">
        {(
          [
            { key: 'GLOSSARY', label: 'Taktik-Glossar' },
            { key: 'SCENARIOS', label: 'Spielszenarien' },
            { key: 'DRILLS', label: 'Drill-Kommandos' },
          ] as { key: TabKey; label: string }[]
        ).map((tab) => (
          <button
            key={tab.key}
            type="button"
            onClick={() => setActiveTab(tab.key)}
            className={`rounded-xl py-2.5 text-xs font-extrabold transition ${
              activeTab === tab.key
                ? 'bg-[var(--accent)] text-[var(--on-accent)]'
                : 'text-[var(--ink2)] hover:text-[var(--ink)]'
            }`}
          >
            {tab.label}
          </button>
        ))}
      </div>

      {/* TAB 1: GLOSSARY */}
      {activeTab === 'GLOSSARY' && (
        <div className="space-y-4">
          <div className="flex flex-wrap gap-1.5">
            <button
              type="button"
              onClick={() => setCategoryFilter('ALL')}
              className={`rounded-xl border px-3 py-1.5 text-xs font-bold ${
                categoryFilter === 'ALL'
                  ? 'border-[var(--accent)] bg-[var(--accent-soft)] text-[var(--accent-ink)]'
                  : 'border-[var(--line)] bg-[var(--surface)] text-[var(--ink2)]'
              }`}
            >
              Alle ({footballCoachU15Data.terms.length})
            </button>
            {(Object.keys(COACH_TERM_CATEGORY_LABELS) as CoachTermCategory[]).map((cat) => (
              <button
                key={cat}
                type="button"
                onClick={() => setCategoryFilter(cat)}
                className={`rounded-xl border px-3 py-1.5 text-xs font-bold ${
                  categoryFilter === cat
                    ? 'border-[var(--accent)] bg-[var(--accent-soft)] text-[var(--accent-ink)]'
                    : 'border-[var(--line)] bg-[var(--surface)] text-[var(--ink2)]'
                }`}
              >
                {COACH_TERM_CATEGORY_LABELS[cat]}
              </button>
            ))}
          </div>

          <FootballTacticalPitch activeCategory={categoryFilter} />

          <div className="grid gap-3 md:grid-cols-2">
            {filteredTerms.map((term) => {
              const ttsId = `coach-term-${term.id}`;
              return (
                <div
                  key={term.id}
                  className="flex flex-col justify-between space-y-3 rounded-2xl border border-[var(--line)] bg-[var(--surface)] p-4"
                >
                  <div>
                    <div className="flex items-start justify-between gap-2">
                      <h3 className="text-base font-extrabold text-[var(--ink)]">
                        {term.term}
                      </h3>
                      <span className="rounded-lg bg-[var(--surface2)] px-2 py-0.5 text-[10px] font-bold uppercase text-[var(--ink2)]">
                        {COACH_TERM_CATEGORY_LABELS[term.category]}
                      </span>
                    </div>
                    <p className="mt-1.5 text-xs leading-relaxed text-[var(--ink2)]">
                      {term.tacticalMeaning}
                    </p>
                  </div>

                  <div className="space-y-2 border-t border-[var(--line)] pt-2.5 text-xs">
                    <div className="flex items-center justify-between gap-2 rounded-xl bg-[var(--ok-bg)] p-2.5">
                      <div>
                        <p className="text-[10px] font-bold uppercase text-[var(--ok)]">
                          Platz-Kommando
                        </p>
                        <p className="font-bold text-[var(--ink)]">
                          „{term.pitchCommand}“
                        </p>
                      </div>
                      <button
                        type="button"
                        onClick={() => speakPhrase(ttsId, term.pitchCommand)}
                        className="rounded-xl bg-[var(--surface)] p-2 text-[var(--ok)]"
                        aria-label="Kommando anhören"
                      >
                        <Volume2 className="h-4 w-4" />
                      </button>
                    </div>
                    <p className="text-[11px] text-[var(--coral)]">
                      <strong>Vermeiden:</strong> {term.avoidInstead}
                    </p>
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      )}

      {/* TAB 2: SCENARIOS */}
      {activeTab === 'SCENARIOS' && (
        <div className="space-y-4">
          {footballCoachU15Data.scenarios.map((scenario) => {
            const chosen = scenarioAnswers[scenario.id];
            const answered = chosen !== undefined;
            const ttsId = `coach-scen-${scenario.id}`;
            return (
              <div
                key={scenario.id}
                className="space-y-3 rounded-3xl border border-[var(--line)] bg-[var(--surface)] p-5"
              >
                <div className="flex flex-wrap items-center justify-between gap-2">
                  <h3 className="text-base font-extrabold text-[var(--ink)]">
                    {scenario.title}
                  </h3>
                  <span className="rounded-lg bg-[var(--accent-soft)] px-2.5 py-1 text-[10px] font-bold text-[var(--accent-ink)]">
                    {scenario.matchPhase}
                  </span>
                </div>
                <p className="text-xs leading-relaxed text-[var(--ink2)]">
                  {scenario.situationDescription}
                </p>

                <div className="space-y-1.5">
                  {scenario.options.map((opt, idx) => {
                    const isSel = chosen === idx;
                    const isRight = idx === scenario.correctIndex;
                    return (
                      <button
                        key={idx}
                        type="button"
                        onClick={() => {
                          setScenarioAnswers((prev) => ({
                            ...prev,
                            [scenario.id]: idx,
                          }));
                          recordPracticeActivity('Fußball-Taktikszenario');
                        }}
                        className={`flex w-full items-center gap-2 rounded-xl border p-3 text-left text-xs transition ${
                          answered && isRight
                            ? 'border-[var(--ok)] bg-[var(--ok-bg)] text-[var(--ink)]'
                            : answered && isSel && !isRight
                              ? 'border-[var(--coral)] bg-[var(--coral-soft)] text-[var(--ink)]'
                              : 'border-[var(--line)] bg-[var(--surface2)] text-[var(--ink)] hover:border-[var(--accent)]'
                        }`}
                      >
                        {answered && isRight ? (
                          <CheckCircle2 className="h-4 w-4 shrink-0 text-[var(--ok)]" />
                        ) : answered && isSel && !isRight ? (
                          <XCircle className="h-4 w-4 shrink-0 text-[var(--coral)]" />
                        ) : (
                          <span className="h-3.5 w-3.5 shrink-0 rounded-full border border-[var(--ink2)]" />
                        )}
                        <span>{opt}</span>
                      </button>
                    );
                  })}
                </div>

                {answered && (
                  <div className="space-y-2 rounded-2xl border border-[var(--ok)]/30 bg-[var(--ok-bg)] p-3.5 text-xs">
                    <p className="leading-relaxed text-[var(--ink)]">
                      {scenario.coachingExplanation}
                    </p>
                    <div className="flex items-center justify-between gap-2 border-t border-[var(--ok)]/20 pt-2">
                      <span className="font-bold text-[var(--ok)]">
                        Ruf auf den Platz: „{scenario.keyCommandToShout}“
                      </span>
                      <button
                        type="button"
                        onClick={() => speakPhrase(ttsId, scenario.keyCommandToShout)}
                        className="rounded-lg bg-[var(--surface)] p-1.5 text-[var(--ok)]"
                        aria-label="Kommando vorsprechen"
                      >
                        <Volume2 className="h-4 w-4" />
                      </button>
                    </div>
                  </div>
                )}
              </div>
            );
          })}
        </div>
      )}

      {/* TAB 3: DRILL COMMANDS */}
      {activeTab === 'DRILLS' && (
        <div className="space-y-4">
          {footballCoachU15Data.drills.map((drill) => {
            const done = completedDrillIds.includes(drill.id);
            return (
              <div
                key={drill.id}
                className="space-y-3 rounded-3xl border border-[var(--line)] bg-[var(--surface)] p-5"
              >
                <div className="flex flex-wrap items-start justify-between gap-2">
                  <div>
                    <span className="text-[11px] font-bold uppercase tracking-wider text-[var(--accent-ink)]">
                      {drill.ageGroupFocus}
                    </span>
                    <h3 className="text-base font-extrabold text-[var(--ink)]">
                      {drill.drillTitle}
                    </h3>
                  </div>
                  <button
                    type="button"
                    onClick={() => toggleDrillCompleted(drill.id)}
                    className={`inline-flex items-center gap-1.5 rounded-xl border px-3 py-1.5 text-xs font-bold ${
                      done
                        ? 'border-[var(--ok)] bg-[var(--ok-bg)] text-[var(--ok)]'
                        : 'border-[var(--line)] bg-[var(--surface2)] text-[var(--ink2)]'
                    }`}
                  >
                    {done ? (
                      <ShieldCheck className="h-3.5 w-3.5" />
                    ) : (
                      <ClipboardCheck className="h-3.5 w-3.5" />
                    )}
                    {done ? 'Geübt' : 'Als geübt markieren'}
                  </button>
                </div>

                <p className="text-xs leading-relaxed text-[var(--ink2)]">
                  {drill.setupSummary}
                </p>

                <div className="space-y-1.5">
                  <p className="text-[11px] font-bold uppercase tracking-wider text-[var(--ink2)]">
                    Live-Kommandos (antippen zum Vorsprechen)
                  </p>
                  <div className="flex flex-wrap gap-2">
                    {drill.liveCommands.map((cmd, idx) => (
                      <button
                        key={idx}
                        type="button"
                        onClick={() => speakPhrase(`drill-${drill.id}-${idx}`, cmd)}
                        className="inline-flex items-center gap-1.5 rounded-xl border border-[var(--line)] bg-[var(--surface2)] px-3 py-2 text-xs font-bold text-[var(--ink)] hover:border-[var(--accent)]"
                      >
                        <Volume2 className="h-3.5 w-3.5 text-[var(--accent-ink)]" />
                        {cmd}
                      </button>
                    ))}
                  </div>
                </div>

                <div className="grid gap-2 border-t border-[var(--line)] pt-2.5 text-xs sm:grid-cols-2">
                  <div className="rounded-xl bg-[var(--coral-soft)] p-2.5 text-[var(--ink)]">
                    <strong className="text-[var(--coral)]">Vermeiden:</strong>{' '}
                    {drill.avoidPhrase}
                  </div>
                  <div className="rounded-xl bg-[var(--ok-bg)] p-2.5 text-[var(--ink)]">
                    <strong className="text-[var(--ok)]">Besser:</strong>{' '}
                    {drill.positiveCorrection}
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
};
