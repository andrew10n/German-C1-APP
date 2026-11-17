import React, { useMemo, useState } from 'react';
import {
  BookOpen,
  Eye,
  Plus,
  RotateCcw,
  Search,
  Trash2,
  Volume2,
} from 'lucide-react';
import { VocabCollocationWebSvg } from '../components/ImprintInteractiveDiagrams';
import { VocabMemoryCurveVisual } from '../components/ImprintLessonVisuals';
import { speechService } from '../services/speechService';
import { useApp } from '../state/AppContext';

type InventoryFilter = 'DUE' | 'ALL' | 'MASTERED' | 'CUSTOM';
type VocabTab = 'TRAIN' | 'INVENTORY';

export const VocabTrainingScreen: React.FC = () => {
  const {
    srsVocabulary,
    recordSrsReview,
    addCustomSrsVocabulary,
    deleteSrsVocabulary,
  } = useApp();

  const [activeTab, setActiveTab] = useState<VocabTab>('TRAIN');
  const [srsRevealed, setSrsRevealed] = useState(false);
  const [sentenceDraft, setSentenceDraft] = useState('');
  const [showAddVocab, setShowAddVocab] = useState(false);
  const [newCollocation, setNewCollocation] = useState('');
  const [newMeaning, setNewMeaning] = useState('');
  const [newExample, setNewExample] = useState('');
  const [inventoryFilter, setInventoryFilter] = useState<InventoryFilter>('ALL');
  const [inventoryQuery, setInventoryQuery] = useState('');

  const now = Date.now();
  const dueCards = useMemo(
    () =>
      srsVocabulary
        .filter((v) => v.nextReviewTimestamp <= now)
        .sort((a, b) => a.nextReviewTimestamp - b.nextReviewTimestamp),
    [srsVocabulary, now],
  );
  const masteredCount = srsVocabulary.filter((v) => v.isMastered).length;
  const customCount = srsVocabulary.filter((v) => v.sourceUnitId === 'custom').length;
  const activeSrsCard = dueCards[0] ?? null;

  const filteredInventory = useMemo(() => {
    const q = inventoryQuery.trim().toLowerCase();
    return srsVocabulary.filter((card) => {
      if (inventoryFilter === 'DUE' && card.nextReviewTimestamp > now) return false;
      if (inventoryFilter === 'MASTERED' && !card.isMastered) return false;
      if (inventoryFilter === 'CUSTOM' && card.sourceUnitId !== 'custom') return false;
      if (!q) return true;
      return (
        card.germanCollocation.toLowerCase().includes(q) ||
        card.meaningDe.toLowerCase().includes(q) ||
        card.category.toLowerCase().includes(q)
      );
    });
  }, [srsVocabulary, inventoryFilter, inventoryQuery, now]);

  const handleCreateCard = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newCollocation.trim() || !newMeaning.trim()) return;
    addCustomSrsVocabulary({
      germanCollocation: newCollocation,
      meaningDe: newMeaning,
      exampleSentence: newExample || `Beispiel: ${newCollocation}`,
    });
    setNewCollocation('');
    setNewMeaning('');
    setNewExample('');
    setShowAddVocab(false);
  };

  return (
    <div className="space-y-5">
      <div className="flex flex-wrap items-end justify-between gap-3">
        <div>
          <p className="text-[11px] font-bold uppercase tracking-wider text-[var(--accent-ink)]">
            {dueCards.length} fällig · {masteredCount}/{srsVocabulary.length} gefestigt
          </p>
          <h1 className="font-display text-3xl font-extrabold text-[var(--ink)]">
            Wortschatz-Training
          </h1>
        </div>

        <button
          type="button"
          onClick={() => setShowAddVocab((v) => !v)}
          className="inline-flex items-center gap-1.5 rounded-xl bg-[var(--accent)] px-3.5 py-2 text-xs font-bold text-[var(--on-accent)]"
        >
          <Plus className="h-3.5 w-3.5" />
          Eigene Kollokation
        </button>
      </div>

      {/* 2 Clean Tabs: Focused Flashcard Training vs Full Card Inventory */}
      <div className="grid grid-cols-2 gap-1.5 rounded-2xl border border-[var(--line)] bg-[var(--surface)] p-1.5">
        <button
          type="button"
          onClick={() => setActiveTab('TRAIN')}
          className={`rounded-xl py-2 text-xs font-extrabold transition ${
            activeTab === 'TRAIN'
              ? 'bg-[var(--accent)] text-[var(--on-accent)]'
              : 'text-[var(--ink2)] hover:text-[var(--ink)]'
          }`}
        >
          Aktives Abruf-Training ({dueCards.length})
        </button>
        <button
          type="button"
          onClick={() => setActiveTab('INVENTORY')}
          className={`rounded-xl py-2 text-xs font-extrabold transition ${
            activeTab === 'INVENTORY'
              ? 'bg-[var(--accent)] text-[var(--on-accent)]'
              : 'text-[var(--ink2)] hover:text-[var(--ink)]'
          }`}
        >
          Kartenbestand & Verlauf ({srsVocabulary.length})
        </button>
      </div>

      {showAddVocab && (
        <form
          onSubmit={handleCreateCard}
          className="space-y-3 rounded-3xl border border-[var(--accent)] bg-[var(--surface)] p-4"
        >
          <h2 className="text-sm font-extrabold text-[var(--ink)]">
            Neue Wortschatzkarte hinzufügen
          </h2>
          <input
            type="text"
            required
            value={newCollocation}
            onChange={(e) => setNewCollocation(e.target.value)}
            placeholder="C1-Ausdruck / Nomen-Verb-Verbindung"
            className="w-full rounded-xl border border-[var(--line)] bg-[var(--bg)] px-3 py-2 text-xs text-[var(--ink)]"
          />
          <input
            type="text"
            required
            value={newMeaning}
            onChange={(e) => setNewMeaning(e.target.value)}
            placeholder="Bedeutung / Verwendungshinweis"
            className="w-full rounded-xl border border-[var(--line)] bg-[var(--bg)] px-3 py-2 text-xs text-[var(--ink)]"
          />
          <input
            type="text"
            value={newExample}
            onChange={(e) => setNewExample(e.target.value)}
            placeholder="Kontextsatz (optional)"
            className="w-full rounded-xl border border-[var(--line)] bg-[var(--bg)] px-3 py-2 text-xs text-[var(--ink)]"
          />
          <div className="flex justify-end gap-2">
            <button
              type="button"
              onClick={() => setShowAddVocab(false)}
              className="rounded-xl px-3 py-1.5 text-xs font-semibold text-[var(--ink2)]"
            >
              Abbrechen
            </button>
            <button
              type="submit"
              className="rounded-xl bg-[var(--accent)] px-4 py-1.5 text-xs font-bold text-[var(--on-accent)]"
            >
              Speichern
            </button>
          </div>
        </form>
      )}

      {/* TAB 1: FOCUSED FLASHCARD WORKSPACE */}
      {activeTab === 'TRAIN' &&
        (activeSrsCard ? (
          <section className="c1-animate-enter space-y-4 rounded-3xl border border-[var(--line)] bg-[var(--surface)] p-5 shadow-sm">
            <div className="flex flex-wrap items-center justify-between gap-2 text-xs">
              <span className="font-bold text-[var(--violet)]">
                {activeSrsCard.category} · {activeSrsCard.register}
              </span>
              <button
                type="button"
                onClick={() =>
                  speechService.speak(activeSrsCard.germanCollocation, {
                    id: `srs-${activeSrsCard.id}`,
                  })
                }
                className="inline-flex items-center gap-1 rounded-xl border border-[var(--line)] bg-[var(--surface2)] px-2.5 py-1 text-xs font-bold text-[var(--ink)]"
              >
                <Volume2 className="h-3.5 w-3.5" />
                Anhören
              </button>
            </div>

            <VocabCollocationWebSvg card={activeSrsCard} draftText={sentenceDraft} />

            <div className="c1-flip-scene">
              <div
                className={`c1-flip-card min-h-[172px] ${
                  srsRevealed ? 'is-flipped' : ''
                }`}
              >
                <div className="c1-flip-face absolute inset-0 flex flex-col items-center justify-center rounded-2xl border border-[var(--line)] bg-[var(--surface2)] p-6 text-center">
                  <p className="text-[10px] font-bold uppercase tracking-wider text-[var(--accent-ink)]">
                    C1-Kollokation · Aktiver Abruf
                  </p>
                  <h2 className="mt-2 font-display text-2xl font-extrabold text-[var(--ink)]">
                    {activeSrsCard.germanCollocation}
                  </h2>
                  <button
                    type="button"
                    onClick={() => setSrsRevealed(true)}
                    className="mt-4 inline-flex items-center gap-1.5 rounded-2xl bg-[var(--accent)] px-5 py-2.5 text-xs font-extrabold text-[var(--on-accent)]"
                  >
                    <Eye className="h-4 w-4" />
                    Karte wenden & prüfen
                  </button>
                </div>

                <div className="c1-flip-face c1-flip-face-back absolute inset-0 flex flex-col justify-between rounded-2xl border border-[var(--accent)]/50 bg-[var(--surface2)] p-5 text-left">
                  <div>
                    <div className="flex items-center justify-between gap-2">
                      <span className="text-[10px] font-extrabold uppercase tracking-wider text-[var(--ok)]">
                        Bedeutung & Kontextsatz
                      </span>
                      <button
                        type="button"
                        onClick={() => setSrsRevealed(false)}
                        className="text-[11px] font-bold text-[var(--ink2)] hover:text-[var(--ink)]"
                      >
                        Vorderseite
                      </button>
                    </div>
                    <p className="mt-2 text-sm font-bold text-[var(--ink)]">
                      {activeSrsCard.meaningDe}
                    </p>
                    <p className="mt-2 rounded-xl border border-[var(--line)] bg-[var(--surface)] p-3 text-xs italic text-[var(--ink2)]">
                      „{activeSrsCard.exampleSentence}“
                    </p>
                  </div>
                </div>
              </div>
            </div>

            <div className="space-y-1.5">
              <label
                htmlFor="srs-sentence-draft"
                className="block text-[11px] font-bold text-[var(--ink2)]"
              >
                Eigener C1-Beispielsatz (optional aktiv formulieren)
              </label>
              <textarea
                id="srs-sentence-draft"
                rows={2}
                value={sentenceDraft}
                onChange={(e) => setSentenceDraft(e.target.value)}
                placeholder={`Bilde einen Satz mit „${activeSrsCard.germanCollocation}“...`}
                className="w-full rounded-2xl border border-[var(--line)] bg-[var(--bg)] px-3.5 py-2.5 text-xs text-[var(--ink)] focus:border-[var(--accent)] focus:outline-none"
              />
            </div>

            {srsRevealed && (
              <div className="grid grid-cols-2 gap-2 sm:grid-cols-4">
                {(
                  [
                    {
                      q: 0,
                      label: 'Blackout (0)',
                      style:
                        'border-[var(--coral)] bg-[var(--coral-soft)] text-[var(--coral)]',
                    },
                    {
                      q: 1,
                      label: 'Schwer (1)',
                      style: 'border-amber-500/40 bg-amber-500/15 text-amber-400',
                    },
                    {
                      q: 2,
                      label: 'Gut (2)',
                      style:
                        'border-[var(--accent)] bg-[var(--accent-soft)] text-[var(--accent-ink)]',
                    },
                    {
                      q: 3,
                      label: 'Perfekt (3)',
                      style: 'border-[var(--ok)] bg-[var(--ok-bg)] text-[var(--ok)]',
                    },
                  ] as const
                ).map((btn) => (
                  <button
                    key={btn.q}
                    type="button"
                    onClick={() => {
                      recordSrsReview(activeSrsCard.id, btn.q);
                      setSrsRevealed(false);
                      setSentenceDraft('');
                    }}
                    className={`rounded-xl border px-3 py-2.5 text-xs font-extrabold transition hover:opacity-90 ${btn.style}`}
                  >
                    {btn.label}
                  </button>
                ))}
              </div>
            )}
          </section>
        ) : (
          <div className="rounded-3xl border border-[var(--ok)]/30 bg-[var(--ok-bg)] p-6 text-center">
            <BookOpen className="mx-auto h-8 w-8 text-[var(--ok)]" />
            <h2 className="mt-2 text-base font-extrabold text-[var(--ink)]">
              Aktuell keine fälligen Wortschatz-Karten
            </h2>
            <p className="mt-1 text-xs text-[var(--ink2)]">
              Alle Karten sind gemäß SM-2-Intervall eingeplant.
            </p>
          </div>
        ))}

      {/* TAB 2: MEMORY CURVE & CARD INVENTORY */}
      {activeTab === 'INVENTORY' && (
        <div className="c1-animate-enter space-y-4">
          <VocabMemoryCurveVisual
            dueCount={dueCards.length}
            masteredCount={masteredCount}
            totalCount={srsVocabulary.length}
            activeIntervalDays={activeSrsCard?.intervalDays}
            activeRepetitions={activeSrsCard?.repetitions}
          />

          <div className="flex flex-wrap items-center justify-between gap-2">
            <div className="flex flex-wrap gap-1.5">
              {(
                [
                  { id: 'ALL', label: `Alle (${srsVocabulary.length})` },
                  { id: 'DUE', label: `Fällig (${dueCards.length})` },
                  { id: 'MASTERED', label: `Gefestigt (${masteredCount})` },
                  { id: 'CUSTOM', label: `Eigene (${customCount})` },
                ] as const
              ).map((tab) => {
                const active = inventoryFilter === tab.id;
                return (
                  <button
                    key={tab.id}
                    type="button"
                    onClick={() => setInventoryFilter(tab.id)}
                    className={`rounded-xl border px-2.5 py-1 text-[11px] font-bold transition ${
                      active
                        ? 'border-[var(--accent)] bg-[var(--accent-soft)] text-[var(--accent-ink)]'
                        : 'border-[var(--line)] bg-[var(--surface)] text-[var(--ink2)] hover:text-[var(--ink)]'
                    }`}
                  >
                    {tab.label}
                  </button>
                );
              })}
            </div>
          </div>

          <div className="relative">
            <Search className="pointer-events-none absolute top-1/2 left-3.5 h-3.5 w-3.5 -translate-y-1/2 text-[var(--ink2)]" />
            <input
              type="search"
              value={inventoryQuery}
              onChange={(e) => setInventoryQuery(e.target.value)}
              placeholder="Kollokation, Bedeutung oder Kategorie suchen..."
              className="w-full rounded-2xl border border-[var(--line)] bg-[var(--surface)] py-2 pr-4 pl-9 text-xs text-[var(--ink)] focus:border-[var(--accent)] focus:outline-none"
            />
          </div>

          <div className="space-y-2">
            {filteredInventory.length === 0 ? (
              <p className="rounded-2xl border border-[var(--line)] bg-[var(--surface)] p-4 text-xs text-[var(--ink2)]">
                Keine Karten passend zum gewählten Filter gefunden.
              </p>
            ) : (
              filteredInventory.map((card) => {
                const isDue = card.nextReviewTimestamp <= now;
                return (
                  <div
                    key={card.id}
                    className="flex flex-wrap items-center justify-between gap-2 rounded-2xl border border-[var(--line)] bg-[var(--surface)] px-4 py-3 text-xs"
                  >
                    <div className="min-w-0 flex-1">
                      <p className="truncate font-bold text-[var(--ink)]">
                        {card.germanCollocation}
                      </p>
                      <p className="truncate text-[11px] text-[var(--ink2)]">
                        Intervall: {card.intervalDays}d · Wdh.: {card.repetitions} ·{' '}
                        {isDue
                          ? 'Jetzt fällig'
                          : `Fällig am ${new Date(card.nextReviewTimestamp).toLocaleDateString('de-DE')}`}
                      </p>
                    </div>
                    <div className="flex items-center gap-1.5">
                      <button
                        type="button"
                        onClick={() => recordSrsReview(card.id, 0)}
                        title="Zurücksetzen (Wiederholen)"
                        className="rounded-lg border border-[var(--line)] bg-[var(--surface2)] p-1.5 text-[var(--ink2)] hover:text-[var(--ink)]"
                      >
                        <RotateCcw className="h-3.5 w-3.5" />
                      </button>
                      <button
                        type="button"
                        onClick={() => deleteSrsVocabulary(card.id)}
                        title="Karte entfernen"
                        className="rounded-lg border border-[var(--line)] bg-[var(--surface2)] p-1.5 text-[var(--ink2)] hover:text-[var(--coral)]"
                      >
                        <Trash2 className="h-3.5 w-3.5" />
                      </button>
                    </div>
                  </div>
                );
              })
            )}
          </div>
        </div>
      )}
    </div>
  );
};
