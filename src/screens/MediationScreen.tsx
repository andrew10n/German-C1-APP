import React, { useMemo, useState } from 'react';
import { AlertTriangle, CheckCircle2, Eye, EyeOff, Languages, Sparkles } from 'lucide-react';
import { AiFeedbackShareButton } from '../components/AiFeedbackShareButton';
import { useApp } from '../state/AppContext';

export const MediationScreen: React.FC = () => {
  const { mediationTasks, recordPracticeActivity } = useApp();
  const [selectedIndex, setSelectedIndex] = useState(0);
  const [userDraft, setUserDraft] = useState('');
  const [revealed, setRevealed] = useState(false);
  const [selectedFacts, setSelectedFacts] = useState<number[]>([]);
  const [checkedPitfalls, setCheckedPitfalls] = useState<number[]>([]);

  const task = mediationTasks[selectedIndex] ?? mediationTasks[0];

  const sourceSentences = useMemo(() => {
    if (!task) return [];
    return task.sourceMaterial
      .split(/(?<=[.!?])\s+/)
      .map((s) => s.trim())
      .filter(Boolean);
  }, [task]);

  if (!task) return null;

  const toggleFact = (idx: number) =>
    setSelectedFacts((prev) =>
      prev.includes(idx) ? prev.filter((i) => i !== idx) : [...prev, idx],
    );

  const togglePitfall = (idx: number) =>
    setCheckedPitfalls((prev) =>
      prev.includes(idx) ? prev.filter((i) => i !== idx) : [...prev, idx],
    );

  return (
    <div className="space-y-5">
      <div>
        <p className="text-[11px] font-bold uppercase tracking-wider text-[var(--accent-ink)]">
          Komplexe Inhalte zielgruppengerecht übertragen
        </p>
        <h1 className="font-display text-3xl font-extrabold text-[var(--ink)]">
          Sprachmittlung & Mediation
        </h1>
      </div>

      {/* Task Selector */}
      <div className="flex gap-2 overflow-x-auto pb-1">
        {mediationTasks.map((item, idx) => {
          const active = idx === selectedIndex;
          return (
            <button
              key={item.id}
              type="button"
              onClick={() => {
                setSelectedIndex(idx);
                setUserDraft('');
                setRevealed(false);
                setSelectedFacts([]);
                setCheckedPitfalls([]);
              }}
              className={`inline-flex shrink-0 items-center gap-1.5 rounded-xl border px-3 py-2 text-xs font-bold transition ${
                active
                  ? 'border-[var(--accent)] bg-[var(--accent-soft)] text-[var(--accent-ink)]'
                  : 'border-[var(--line)] bg-[var(--surface)] text-[var(--ink2)] hover:text-[var(--ink)]'
              }`}
            >
              <Languages className="h-3.5 w-3.5" />
              {item.title}
            </button>
          );
        })}
      </div>

      {/* Task Card */}
      <section className="c1-animate-enter space-y-4 rounded-3xl border border-[var(--line)] bg-[var(--surface)] p-5 shadow-sm">
        <div className="flex flex-wrap items-center justify-between gap-2">
          <h2 className="text-lg font-extrabold text-[var(--ink)]">{task.title}</h2>
          <span className="rounded-lg bg-[var(--violet-soft)] px-2.5 py-1 text-[11px] font-bold text-[var(--violet)]">
            {task.mediationType}
          </span>
        </div>

        {/* Visual 3-Step Mediation Pipeline Header */}
        <div className="grid grid-cols-3 gap-2 rounded-2xl border border-white/15 bg-[var(--hero)] p-3 text-center text-[11px] font-bold text-[var(--on-hero)]">
          <div className="rounded-xl bg-white/5 px-2 py-2">
            <span className="block text-[9px] uppercase tracking-wider text-[var(--accent)]">
              Schritt 1 · Quelle
            </span>
            Kernfakten markieren ({selectedFacts.length}/{Math.max(1, sourceSentences.length)})
          </div>
          <div className="rounded-xl bg-white/5 px-2 py-2">
            <span className="block text-[9px] uppercase tracking-wider text-[var(--sky)]">
              Schritt 2 · Filter
            </span>
            Stolperfallen prüfen ({checkedPitfalls.length}/{task.criticalPitfallsToAvoid.length})
          </div>
          <div className="rounded-xl bg-white/5 px-2 py-2">
            <span className="block text-[9px] uppercase tracking-wider text-[var(--ok)]">
              Schritt 3 · Ziel
            </span>
            Adressatengerecht formulieren
          </div>
        </div>

        {/* Visual Mediation Transformation Flow */}
        <div className="grid items-stretch gap-3 rounded-2xl border border-[var(--line)] bg-[var(--surface2)] p-4 md:grid-cols-[1.2fr_auto_1fr]">
          <div className="space-y-2 rounded-xl border border-[var(--line)] bg-[var(--surface)] p-3.5">
            <div className="flex items-center justify-between gap-2">
              <p className="text-[10px] font-extrabold uppercase tracking-wider text-[var(--ink2)]">
                1. Ausgangsmaterial (Sätze zum Übernehmen antippen)
              </p>
              <Sparkles className="h-3.5 w-3.5 text-[var(--accent-ink)]" />
            </div>
            <div className="space-y-1.5">
              {sourceSentences.map((sent, idx) => {
                const picked = selectedFacts.includes(idx);
                return (
                  <button
                    key={idx}
                    type="button"
                    onClick={() => toggleFact(idx)}
                    className={`flex w-full items-start gap-2 rounded-xl border p-2 text-left text-xs leading-relaxed transition ${
                      picked
                        ? 'border-[var(--accent)] bg-[var(--accent-soft)] font-semibold text-[var(--ink)]'
                        : 'border-[var(--line)] bg-[var(--surface2)] text-[var(--ink)] hover:border-[var(--accent)]/60'
                    }`}
                  >
                    <CheckCircle2
                      className={`mt-0.5 h-3.5 w-3.5 shrink-0 ${
                        picked ? 'text-[var(--accent-ink)]' : 'text-[var(--ink2)] opacity-35'
                      }`}
                    />
                    <span>{sent}</span>
                  </button>
                );
              })}
            </div>
          </div>

          <div className="flex flex-col items-center justify-center gap-1 px-2 text-center">
            <svg className="h-6 w-20" viewBox="0 0 80 24">
              <line
                x1="4"
                y1="12"
                x2="70"
                y2="12"
                stroke="var(--accent)"
                strokeWidth="2.5"
                className="c1-svg-flow"
              />
              <polygon points="68,7 77,12 68,17" fill="var(--accent)" />
            </svg>
            <span className="text-[10px] font-extrabold uppercase tracking-wider text-[var(--accent-ink)]">
              Register-Transfer
            </span>
          </div>

          <div className="flex flex-col justify-between rounded-xl border border-[var(--accent)]/40 bg-[var(--accent-soft)] p-3.5">
            <div>
              <p className="text-[10px] font-extrabold uppercase tracking-wider text-[var(--accent-ink)]">
                2. Zielgruppe & Kontext
              </p>
              <p className="mt-1.5 text-xs font-medium leading-relaxed text-[var(--ink)]">
                {task.recipientContext}
              </p>
            </div>
            {selectedFacts.length > 0 && (
              <div className="mt-3 rounded-lg border border-[var(--accent)]/30 bg-[var(--surface)] p-2.5 text-[11px] text-[var(--ink2)]">
                <strong className="text-[var(--accent-ink)]">
                  {selectedFacts.length} Kernaussage(n) vorgemerkt
                </strong>{' '}
                — übersetze sie im Ton der Zielgruppe, ohne Zusatzannahmen einzubauen.
              </div>
            )}
          </div>
        </div>

        {/* Interactive Pitfalls Guard Checklist */}
        <div className="space-y-2 rounded-2xl border border-[var(--coral)]/30 bg-[var(--coral-soft)] p-4">
          <div className="flex items-center gap-1.5 text-xs font-bold text-[var(--coral)]">
            <AlertTriangle className="h-4 w-4" />
            Checkliste: Typische Stolperfallen vermeiden (abhaken beim Schreiben)
          </div>
          <div className="grid gap-2 sm:grid-cols-2">
            {task.criticalPitfallsToAvoid.map((pitfall, idx) => {
              const checked = checkedPitfalls.includes(idx);
              return (
                <button
                  key={idx}
                  type="button"
                  onClick={() => togglePitfall(idx)}
                  className={`flex items-start gap-2 rounded-xl border p-2.5 text-left text-xs transition ${
                    checked
                      ? 'border-[var(--ok)] bg-[var(--ok-bg)] text-[var(--ink)]'
                      : 'border-[var(--coral)]/25 bg-[var(--surface)] text-[var(--ink)] hover:border-[var(--accent)]'
                  }`}
                >
                  <CheckCircle2
                    className={`mt-0.5 h-3.5 w-3.5 shrink-0 ${
                      checked ? 'text-[var(--ok)]' : 'text-[var(--coral)] opacity-60'
                    }`}
                  />
                  <span className={checked ? 'line-through opacity-75' : ''}>
                    {pitfall}
                  </span>
                </button>
              );
            })}
          </div>
        </div>

        <div>
          <label className="block text-xs font-bold text-[var(--ink2)]">
            Deine Sprachmittlung (erst frei formulieren, dann Muster prüfen)
          </label>
          <textarea
            rows={5}
            value={userDraft}
            onChange={(e) => setUserDraft(e.target.value)}
            placeholder="Übertrage die Kernaussage sachlich, vollständig und passend zum Zielpublikum..."
            className="mt-1.5 w-full rounded-2xl border border-[var(--line)] bg-[var(--bg)] p-3.5 text-sm text-[var(--ink)] focus:border-[var(--accent)] focus:outline-none"
          />
        </div>

        <div className="flex flex-wrap items-center gap-2">
          <button
            type="button"
            onClick={() => {
              setRevealed((r) => !r);
              if (!revealed) recordPracticeActivity('Sprachmittlung (unbewertete Übung)');
            }}
            className="inline-flex items-center gap-1.5 rounded-xl bg-[var(--accent)] px-4 py-2 text-xs font-bold text-[var(--on-accent)]"
          >
            {revealed ? <EyeOff className="h-3.5 w-3.5" /> : <Eye className="h-3.5 w-3.5" />}
            {revealed ? 'Musterlösung ausblenden' : 'Musterlösung vergleichen'}
          </button>

          <AiFeedbackShareButton
            task={`${task.title} (${task.recipientContext})`}
            response={userDraft}
            evidenceType="Sprachmittlung / Mediation"
          />
        </div>

        {revealed && (
          <div className="c1-animate-enter rounded-2xl border border-[var(--ok)]/30 bg-[var(--ok-bg)] p-4">
            <p className="text-[11px] font-bold uppercase tracking-wider text-[var(--ok)]">
              Zielformulierung (Referenzmodell)
            </p>
            <p className="mt-1.5 text-xs leading-relaxed text-[var(--ink)]">
              {task.targetReformulationModel}
            </p>
          </div>
        )}
      </section>
    </div>
  );
};
