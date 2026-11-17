import React, { useEffect, useMemo, useState } from 'react';
import {
  ArrowRight,
  CheckCircle2,
  ChevronLeft,
  ChevronRight,
  Eye,
  FlipHorizontal2,
  HelpCircle,
  LayoutGrid,
  Lightbulb,
  RotateCcw,
  ShieldAlert,
  Square,
  Volume2,
} from 'lucide-react';
import { CONTEXT_ARTWORK } from '../data/contextArtwork';
import {
  parseDialogueVisuals,
  parseGrammarVisuals,
  parseReadingSlides,
  parseTaskBlueprint,
  type ImprintSlide,
} from '../engines/lessonVisualParser';
import { speechService } from '../services/speechService';
import type { ContextTrack, ProgrammeLanguage } from '../types';
import { AcousticWaveform } from './AcousticWaveform';

const NODE_COLORS: Record<0 | 1 | 2, { border: string; bg: string; label: string; badge: string }> = {
  0: {
    border: 'border-[var(--accent)]/60',
    bg: 'bg-[var(--surface)]',
    label: 'text-[var(--accent-ink)]',
    badge: 'var(--accent)',
  },
  1: {
    border: 'border-[var(--violet)]/60',
    bg: 'bg-[var(--surface)]',
    label: 'text-[var(--violet)]',
    badge: 'var(--violet)',
  },
  2: {
    border: 'border-[var(--sky)]/60',
    bg: 'bg-[var(--surface)]',
    label: 'text-[var(--sky)]',
    badge: 'var(--sky)',
  },
};
function archetypeRoleLabels(archetype: ImprintSlide['archetype']): [string, string, string] {
  switch (archetype) {
    case 'CONTRAST':
      return ['Standpunkt / Vorteil', 'Gewichtung', 'Einwand / Grenze'];
    case 'CAUSE_EFFECT':
      return ['Grund / Auslöser', 'Verknüpfung', 'Folge / Entscheidung'];
    case 'EVIDENCE':
      return ['Beobachtung', 'Prüfgrenze', 'Offene Hypothese'];
    case 'CONDITION':
      return ['Wenn-Annahme', 'Prüfpunkt', 'Konsequenz / Revision'];
    case 'DIALOGUE':
      return ['Ausgangspunkt', 'Gesprächszug', 'Klärung'];
    case 'DISTINCTION':
      return ['Ebene A', 'Abgrenzung ≠', 'Ebene B'];
    default:
      return ['Schritt 1', 'Übergang', 'Nächster Schritt'];
  }
}

export const ImprintSlideInfographic: React.FC<{
  slide: ImprintSlide;
  focusedSlot: 0 | 1 | 2 | null;
  onSelectSlot: (slot: 0 | 1 | 2) => void;
}> = ({ slide, focusedSlot, onSelectSlot }) => {
  const roleLabels = archetypeRoleLabels(slide.archetype);

  const renderNodeCard = (
    idx: 0 | 1 | 2,
    x: number,
    y: number,
    w: number,
    h: number,
  ) => {
    const active = focusedSlot === idx;
    const tone = NODE_COLORS[idx];
    return (
      <foreignObject key={idx} x={x} y={y} width={w} height={h}>
        <button
          type="button"
          onClick={() => onSelectSlot(idx)}
          className={`flex h-full w-full flex-col justify-between rounded-2xl border p-2.5 text-left transition-transform duration-200 ${
            tone.bg
          } ${
            active
              ? 'scale-[1.03] border-[var(--accent)] shadow-md'
              : `${tone.border} hover:scale-[1.01]`
          }`}
        >
          <span
            className={`block truncate text-[9px] font-extrabold uppercase tracking-wider ${tone.label}`}
          >
            {roleLabels[idx]}
          </span>
          <span className="line-clamp-3 text-[11px] font-bold leading-snug text-[var(--ink)]">
            {slide.nodes[idx]}
          </span>
        </button>
      </foreignObject>
    );
  };

  return (
    <div className="relative overflow-hidden rounded-2xl border border-[var(--line)] bg-[var(--hero)] p-3 text-[var(--on-hero)]">
      <div className="mb-2 flex flex-wrap items-center justify-between gap-2 px-1">
        <span className="text-[10px] font-bold uppercase tracking-wider text-[var(--accent)]">
          {slide.archetypeLabel}
        </span>
        <span className="text-[11px] font-semibold text-[var(--on-hero2)]">
          {slide.headline}
        </span>
      </div>

      <svg
        viewBox="0 0 560 185"
        className="h-44 w-full sm:h-48"
        role="img"
        aria-label={slide.headline}
      >
        <defs>
          <pattern
            id={`grid-${slide.index}`}
            width="24"
            height="24"
            patternUnits="userSpaceOnUse"
          >
            <circle cx="2" cy="2" r="1" fill="rgba(255,255,255,0.12)" />
          </pattern>
          <marker
            id={`arrow-${slide.index}`}
            markerWidth="7"
            markerHeight="7"
            refX="5"
            refY="3.5"
            orient="auto"
          >
            <polygon points="0 0, 7 3.5, 0 7" fill="var(--accent)" />
          </marker>
        </defs>

        <rect width="560" height="185" rx="14" fill={`url(#grid-${slide.index})`} />

        {slide.archetype === 'CONTRAST' && (
          <g>
            {/* Balance Fulcrum */}
            <line
              x1="100"
              y1="98"
              x2="460"
              y2="98"
              stroke="var(--accent)"
              strokeWidth="2.5"
              className="c1-svg-flow"
            />
            <polygon
              points="280,104 258,136 302,136"
              fill="rgba(161,150,255,0.22)"
              stroke="var(--violet)"
              strokeWidth="2"
            />
            <text
              x="280"
              y="154"
              textAnchor="middle"
              fill="var(--on-hero2)"
              fontSize="10"
              fontWeight="700"
            >
              {slide.bridgeLabel}
            </text>
            {renderNodeCard(0, 14, 18, 166, 94)}
            {renderNodeCard(1, 200, 12, 160, 78)}
            {renderNodeCard(2, 380, 18, 166, 94)}
          </g>
        )}

        {slide.archetype === 'EVIDENCE' && (
          <g>
            {/* Certainty Split Line */}
            <line
              x1="280"
              y1="14"
              x2="280"
              y2="170"
              stroke="var(--coral)"
              strokeOpacity="0.6"
              strokeWidth="2"
              strokeDasharray="5 4"
            />
            <rect
              x="228"
              y="148"
              width="104"
              height="22"
              rx="6"
              fill="rgba(229,72,77,0.18)"
              stroke="var(--coral)"
              strokeOpacity="0.65"
            />
            <text
              x="280"
              y="163"
              textAnchor="middle"
              fill="var(--on-hero)"
              fontSize="9.5"
              fontWeight="800"
            >
              Beleggrenze
            </text>
            <path
              d="M 182 70 L 204 70"
              stroke="var(--accent)"
              strokeWidth="2.5"
              markerEnd={`url(#arrow-${slide.index})`}
            />
            <path
              d="M 358 70 L 380 70"
              stroke="var(--sky)"
              strokeWidth="2.5"
              className="c1-svg-flow"
            />
            {renderNodeCard(0, 16, 20, 164, 108)}
            {renderNodeCard(1, 204, 24, 152, 98)}
            {renderNodeCard(2, 382, 20, 162, 108)}
          </g>
        )}

        {slide.archetype === 'CONDITION' && (
          <g>
            {/* Decision Fork */}
            <path
              d="M 182 92 C 214 92, 214 52, 242 52"
              fill="none"
              stroke="var(--accent)"
              strokeWidth="2.8"
              markerEnd={`url(#arrow-${slide.index})`}
              className="c1-svg-flow"
            />
            <path
              d="M 182 92 C 214 92, 214 134, 242 134"
              fill="none"
              stroke="var(--sky)"
              strokeWidth="2.5"
              className="c1-svg-flow"
            />
            <circle cx="192" cy="92" r="7" fill="var(--accent)" className="c1-float-slow" />
            {renderNodeCard(0, 14, 42, 166, 100)}
            {renderNodeCard(1, 246, 14, 298, 74)}
            {renderNodeCard(2, 246, 100, 298, 74)}
          </g>
        )}

        {slide.archetype === 'DISTINCTION' && (
          <g>
            <circle
              cx="140"
              cy="92"
              r="76"
              fill="rgba(45,212,191,0.08)"
              stroke="var(--accent)"
              strokeOpacity="0.4"
            />
            <circle
              cx="420"
              cy="92"
              r="76"
              fill="rgba(63,140,255,0.08)"
              stroke="var(--sky)"
              strokeOpacity="0.4"
            />
            <line
              x1="280"
              y1="20"
              x2="280"
              y2="165"
              stroke="var(--violet)"
              strokeWidth="2"
              strokeDasharray="6 4"
            />
            {renderNodeCard(0, 16, 36, 164, 108)}
            {renderNodeCard(1, 200, 42, 160, 96)}
            {renderNodeCard(2, 380, 36, 164, 108)}
          </g>
        )}

        {(slide.archetype === 'SEQUENCE' ||
          slide.archetype === 'CAUSE_EFFECT' ||
          slide.archetype === 'DIALOGUE') && (
          <g>
            <path
              d="M 178 90 L 200 90"
              fill="none"
              stroke="var(--accent)"
              strokeWidth="3"
              markerEnd={`url(#arrow-${slide.index})`}
              className="c1-svg-flow"
            />
            <path
              d="M 358 90 L 380 90"
              fill="none"
              stroke="var(--sky)"
              strokeWidth="3"
              markerEnd={`url(#arrow-${slide.index})`}
              className="c1-svg-flow"
            />
            <text
              x="280"
              y="164"
              textAnchor="middle"
              fill="var(--on-hero2)"
              fontSize="10"
              fontWeight="700"
            >
              {slide.bridgeLabel}
            </text>
            {renderNodeCard(0, 14, 30, 162, 114)}
            {renderNodeCard(1, 202, 30, 156, 114)}
            {renderNodeCard(2, 382, 30, 164, 114)}
          </g>
        )}
      </svg>
    </div>
  );
};

export const ImprintStoryDeck: React.FC<{
  unitId: string;
  unitTitle: string;
  passage: string;
  language?: ProgrammeLanguage;
}> = ({ unitId, unitTitle, passage, language = 'DE' }) => {
  const slides = useMemo(
    () => parseReadingSlides(passage, unitTitle),
    [passage, unitTitle],
  );
  const [selectedSlideIdx, setSelectedSlideIdx] = useState(0);
  const [focusedSlot, setFocusedSlot] = useState<0 | 1 | 2 | null>(null);
  const [selectedHighlightIdx, setSelectedHighlightIdx] = useState<number | null>(null);
  const [revealInsight, setRevealInsight] = useState(false);
  const [showOverview, setShowOverview] = useState(false);
  const [speakingId, setSpeakingId] = useState<string | null>(
    speechService.getSpeakingId(),
  );

  useEffect(() => {
    setSelectedSlideIdx(0);
    setFocusedSlot(null);
    setSelectedHighlightIdx(null);
    setRevealInsight(false);
    setShowOverview(false);
  }, [unitId]);

  useEffect(() => {
    setFocusedSlot(null);
    setSelectedHighlightIdx(null);
    setRevealInsight(false);
  }, [selectedSlideIdx]);

  useEffect(() => {
    return speechService.subscribe(() => setSpeakingId(speechService.getSpeakingId()));
  }, []);

  const currentIdx = Math.min(selectedSlideIdx, Math.max(0, slides.length - 1));
  const slide = slides[currentIdx] ?? slides[0];
  const ttsId = `imprint-slide-${unitId}-${currentIdx}`;
  const isPlaying = speakingId === ttsId;

  const activeHighlight =
    selectedHighlightIdx !== null ? slide.highlights[selectedHighlightIdx] ?? null : null;

  return (
    <div className="space-y-4 rounded-3xl border border-[var(--line)] bg-[var(--surface2)] p-4">
      {/* Editorial Progress Header (Zero-Pill Metadata) */}
      <div className="flex flex-wrap items-center justify-between gap-2">
        <div className="flex items-center gap-2 text-xs font-semibold text-[var(--ink2)]">
          <span className="font-extrabold text-[var(--accent-ink)]">
            {language === 'EN' ? 'Visual Story' : 'Bild-Karte'}
          </span>
          <span aria-hidden="true">·</span>
          <span className="tabular-nums font-bold text-[var(--ink)]">
            {currentIdx + 1} / {slides.length}
          </span>
          <span aria-hidden="true">·</span>
          <span>{slide.archetypeLabel}</span>
        </div>

        <div className="flex items-center gap-2">
          {slides.length > 1 && (
            <button
              type="button"
              onClick={() => setShowOverview((v) => !v)}
              className={`inline-flex min-h-[36px] items-center gap-1.5 rounded-xl border px-3 py-1.5 text-xs font-bold transition ${
                showOverview
                  ? 'border-[var(--accent)] bg-[var(--accent-soft)] text-[var(--accent-ink)]'
                  : 'border-[var(--line)] bg-[var(--surface)] text-[var(--ink)] hover:border-[var(--accent)]'
              }`}
            >
              <LayoutGrid className="h-3.5 w-3.5" />
              {showOverview ? 'Einzelkarte' : 'Alle Szenen'}
            </button>
          )}
          <button
            type="button"
            onClick={() =>
              isPlaying
                ? speechService.stopSpeaking()
                : speechService.speak(slide.rawText, { id: ttsId, rate: 0.96 })
            }
            className="inline-flex min-h-[36px] items-center gap-1.5 rounded-xl border border-[var(--line)] bg-[var(--surface)] px-3 py-1.5 text-xs font-bold text-[var(--ink)] transition hover:border-[var(--accent)]"
          >
            {isPlaying ? (
              <Square className="h-3 w-3 fill-current text-[var(--coral)]" />
            ) : (
              <Volume2 className="h-3.5 w-3.5 text-[var(--accent-ink)]" />
            )}
            {isPlaying ? 'Stopp' : 'Vorlesen'}
          </button>
        </div>
      </div>

      {/* Interactive Storyboard Progress Bars */}
      {slides.length > 1 && (
        <div className="flex items-center gap-1.5">
          {slides.map((s, idx) => (
            <button
              key={s.index}
              type="button"
              onClick={() => {
                setSelectedSlideIdx(idx);
                setShowOverview(false);
              }}
              aria-label={`Karte ${idx + 1}: ${s.archetypeLabel}`}
              className={`h-2 flex-1 rounded-full transition-colors duration-200 ${
                idx === currentIdx
                  ? 'bg-[var(--accent)]'
                  : idx < currentIdx
                    ? 'bg-[var(--accent)]/45'
                    : 'bg-[var(--surface)]'
              }`}
            />
          ))}
        </div>
      )}

      {/* Optional Thumbnail Storyboard Grid Overview */}
      {showOverview && slides.length > 1 && (
        <div className="c1-animate-enter grid gap-2.5 sm:grid-cols-2 lg:grid-cols-3">
          {slides.map((s, idx) => (
            <button
              key={s.index}
              type="button"
              onClick={() => {
                setSelectedSlideIdx(idx);
                setShowOverview(false);
              }}
              className={`flex flex-col justify-between rounded-2xl border p-3 text-left transition ${
                idx === currentIdx
                  ? 'border-[var(--accent)] bg-[var(--accent-soft)]/45 shadow-sm'
                  : 'border-[var(--line)] bg-[var(--surface)] hover:border-[var(--accent)]'
              }`}
            >
              <div className="flex items-center justify-between text-[10px] font-extrabold uppercase tracking-wider text-[var(--accent-ink)]">
                <span>
                  {idx + 1} · {s.archetypeLabel}
                </span>
              </div>
              <p className="mt-1.5 line-clamp-2 text-xs font-extrabold text-[var(--ink)]">
                {s.headline}
              </p>
              <p className="mt-1 line-clamp-2 text-[11px] text-[var(--ink2)]">
                {s.nodes[0]} → {s.nodes[2]}
              </p>
            </button>
          ))}
        </div>
      )}

      {/* Main Imprint SVG Visual Diagram Stage */}
      <div key={`${unitId}-slide-${currentIdx}`} className="c1-animate-enter space-y-3">
        <ImprintSlideInfographic
          slide={slide}
          focusedSlot={focusedSlot}
          onSelectSlot={(slot) =>
            setFocusedSlot((prev) => (prev === slot ? null : slot))
          }
        />

        {isPlaying && (
          <AcousticWaveform active variant="accent" label="Sprachausgabe aktiv" />
        )}

        {/* Interactive German Prose with Discourse Highlights */}
        <div className="rounded-2xl border border-[var(--line)] bg-[var(--surface)] p-4">
          <p className="text-[15px] leading-relaxed text-[var(--ink)]">
            {slide.tokens.map((tok, i) => {
              if (tok.kind === 'plain') {
                return <React.Fragment key={i}>{tok.text}</React.Fragment>;
              }
              const hIdx = slide.highlights.findIndex(
                (h) =>
                  h.word.toLowerCase() === tok.text.toLowerCase() ||
                  tok.text.includes(h.word.replace(/…$/, '')),
              );
              const isSelected = hIdx >= 0 && selectedHighlightIdx === hIdx;
              const styleByKind =
                tok.kind === 'connector'
                  ? 'border-[var(--accent)]/50 bg-[var(--accent-soft)] text-[var(--accent-ink)]'
                  : tok.kind === 'modal'
                    ? 'border-[var(--violet)]/50 bg-[var(--violet-soft)] text-[var(--violet)]'
                    : tok.kind === 'number'
                      ? 'border-[var(--sky)]/50 bg-[var(--sky-soft)] text-[var(--sky)]'
                      : 'border-[var(--line)] bg-[var(--surface2)] text-[var(--ink)]';

              return (
                <button
                  key={i}
                  type="button"
                  onClick={() => {
                    if (tok.slotIndex !== undefined) {
                      setFocusedSlot(tok.slotIndex);
                    }
                    if (hIdx >= 0) {
                      setSelectedHighlightIdx((prev) => (prev === hIdx ? null : hIdx));
                    }
                  }}
                  className={`mx-0.5 inline rounded-md border px-1.5 py-0.5 font-bold transition ${styleByKind} ${
                    isSelected ? 'ring-2 ring-[var(--accent)]' : ''
                  }`}
                >
                  {tok.text}
                </button>
              );
            })}
          </p>

          {/* Interactive Signal Word Breakdown Strip */}
          {slide.highlights.length > 0 && (
            <div className="mt-3 border-t border-[var(--line)] pt-2.5">
              <div className="flex flex-wrap items-center gap-1.5">
                <span className="text-[11px] font-bold text-[var(--ink2)]">
                  Antippen für Funktion:
                </span>
                {slide.highlights.map((h, idx) => (
                  <button
                    key={`${h.word}-${idx}`}
                    type="button"
                    onClick={() => {
                      setSelectedHighlightIdx((prev) => (prev === idx ? null : idx));
                      setFocusedSlot(h.slotIndex);
                    }}
                    className={`rounded-lg border px-2 py-1 text-[11px] font-bold transition ${
                      selectedHighlightIdx === idx
                        ? 'border-[var(--accent)] bg-[var(--accent-soft)] text-[var(--accent-ink)]'
                        : 'border-[var(--line)] bg-[var(--surface2)] text-[var(--ink2)] hover:text-[var(--ink)]'
                    }`}
                  >
                    {h.word} · {h.role}
                  </button>
                ))}
              </div>

              {activeHighlight && (
                <div className="c1-animate-enter mt-2 rounded-xl border border-[var(--accent)]/40 bg-[var(--accent-soft)]/50 px-3 py-2 text-xs text-[var(--ink)]">
                  <span className="font-extrabold text-[var(--accent-ink)]">
                    {activeHighlight.word} ({activeHighlight.role}):
                  </span>{' '}
                  {activeHighlight.hint}
                </div>
              )}
            </div>
          )}
        </div>

        {/* Tap-to-Reveal Micro-Takeaway */}
        <div className="flex flex-wrap items-center justify-between gap-2">
          <button
            type="button"
            onClick={() => setRevealInsight((v) => !v)}
            className="inline-flex min-h-[38px] items-center gap-1.5 rounded-xl border border-[var(--line)] bg-[var(--surface)] px-3 py-1.5 text-xs font-bold text-[var(--accent-ink)] transition hover:border-[var(--accent)]"
          >
            <Lightbulb className="h-3.5 w-3.5" />
            {revealInsight
              ? language === 'EN'
                ? 'Hide takeaway'
                : 'Sprachidee ausblenden'
              : language === 'EN'
                ? 'Tap to reveal the idea'
                : 'Sprachidee aufdecken'}
          </button>

          {slides.length > 1 && (
            <div className="flex items-center gap-2">
              <button
                type="button"
                disabled={currentIdx === 0}
                onClick={() => setSelectedSlideIdx((c) => Math.max(0, c - 1))}
                className="inline-flex min-h-[38px] items-center gap-1 rounded-xl border border-[var(--line)] bg-[var(--surface)] px-3 py-1.5 text-xs font-bold text-[var(--ink)] disabled:opacity-40"
              >
                <ChevronLeft className="h-3.5 w-3.5" />
                Zurück
              </button>
              <button
                type="button"
                disabled={currentIdx >= slides.length - 1}
                onClick={() =>
                  setSelectedSlideIdx((c) => Math.min(slides.length - 1, c + 1))
                }
                className="inline-flex min-h-[38px] items-center gap-1 rounded-xl bg-[var(--accent)] px-3.5 py-1.5 text-xs font-extrabold text-[var(--on-accent)] disabled:opacity-40"
              >
                Weiter
                <ChevronRight className="h-3.5 w-3.5" />
              </button>
            </div>
          )}
        </div>

        {revealInsight && (
          <div className="c1-animate-enter rounded-2xl border border-[var(--violet)]/40 bg-[var(--violet-soft)] px-4 py-3 text-xs leading-relaxed text-[var(--ink)]">
            <span className="font-extrabold text-[var(--violet)]">
              {language === 'EN' ? 'Why this works: ' : 'Warum das wirkt: '}
            </span>
            {language === 'EN' ? slide.insightEn : slide.insightDe}
          </div>
        )}
      </div>
    </div>
  );
};

export const ImprintDialogueStage: React.FC<{
  unitId: string;
  transcript: string;
  language?: ProgrammeLanguage;
}> = ({ unitId, transcript, language = 'DE' }) => {
  const turns = useMemo(() => parseDialogueVisuals(transcript), [transcript]);
  const [selectedTurnIdx, setSelectedTurnIdx] = useState(0);
  const [speakingId, setSpeakingId] = useState<string | null>(
    speechService.getSpeakingId(),
  );

  useEffect(() => {
    setSelectedTurnIdx(0);
  }, [unitId]);

  useEffect(() => {
    return speechService.subscribe(() => setSpeakingId(speechService.getSpeakingId()));
  }, []);

  const fullId = `dialogue-full-${unitId}`;
  const isFullPlaying = speakingId === fullId;
  const activeTurn = turns[selectedTurnIdx] ?? turns[0];

  return (
    <div className="space-y-4 rounded-3xl border border-[var(--line)] bg-[var(--surface2)] p-4">
      <div className="flex flex-wrap items-center justify-between gap-2">
        <div className="space-y-0.5">
          <p className="text-xs font-extrabold text-[var(--sky)]">
            {language === 'EN' ? 'Visual Conversation Flow (TTS)' : 'Visueller Dialogverlauf (TTS)'}
          </p>
          <p className="text-[11px] text-[var(--ink2)]">
            Synthetische Gerätestimme als Lese-/Hörhilfe (ersetzt keine echte Aufnahme).
          </p>
        </div>
        <button
          type="button"
          onClick={() =>
            isFullPlaying
              ? speechService.stopSpeaking()
              : speechService.speak(transcript, { id: fullId })
          }
          className="inline-flex min-h-[38px] items-center gap-1.5 rounded-xl bg-[var(--accent)] px-3.5 py-1.5 text-xs font-extrabold text-[var(--on-accent)]"
        >
          {isFullPlaying ? (
            <Square className="h-3.5 w-3.5 fill-current" />
          ) : (
            <Volume2 className="h-3.5 w-3.5" />
          )}
          {isFullPlaying ? 'Stoppen' : 'Dialog vorlesen'}
        </button>
      </div>

      {/* Turn-by-Turn Visual Arc */}
      <div className="overflow-x-auto rounded-2xl border border-[var(--line)] bg-[var(--hero)] p-3.5 text-[var(--on-hero)]">
        <div className="flex min-w-max items-center gap-2">
          {turns.map((turn, idx) => {
            const active = idx === selectedTurnIdx;
            const toneColor =
              turn.moveTone === 'coral'
                ? 'var(--coral)'
                : turn.moveTone === 'violet'
                  ? 'var(--violet)'
                  : turn.moveTone === 'sky'
                    ? 'var(--sky)'
                    : 'var(--accent)';
            return (
              <React.Fragment key={idx}>
                <button
                  type="button"
                  onClick={() => setSelectedTurnIdx(idx)}
                  className={`flex flex-col items-center gap-1 rounded-2xl border px-3 py-2 text-center transition ${
                    active
                      ? 'scale-105 border-[var(--accent)] bg-white/15 shadow-sm'
                      : 'border-white/15 bg-white/5 opacity-80 hover:opacity-100'
                  }`}
                >
                  <span
                    className="flex h-7 w-7 items-center justify-center rounded-full text-[11px] font-black text-[var(--on-accent)]"
                    style={{ backgroundColor: toneColor }}
                  >
                    {idx + 1}
                  </span>
                  <span className="max-w-[92px] truncate text-[10px] font-extrabold text-[var(--on-hero)]">
                    {turn.speaker ?? 'Szene'}
                  </span>
                  <span className="text-[9px] font-semibold text-[var(--on-hero2)]">
                    {turn.moveLabel}
                  </span>
                </button>
                {idx < turns.length - 1 && (
                  <ArrowRight className="h-3.5 w-3.5 shrink-0 text-[var(--accent)] opacity-75" />
                )}
              </React.Fragment>
            );
          })}
        </div>
      </div>

      <AcousticWaveform
        active={Boolean(speakingId?.startsWith('dialogue-'))}
        variant="sky"
        label={speakingId?.startsWith('dialogue-') ? 'TTS aktiv' : 'Tippe eine Station oder einen Zug an'}
      />

      {/* Focused Turn Card + All Bubbles */}
      {activeTurn && (
        <div className="c1-animate-enter space-y-2">
          {turns.map((turn, idx) => {
            const isLeft = idx % 2 === 0;
            const turnId = `dialogue-turn-${unitId}-${idx}`;
            const isTurnPlaying = speakingId === turnId;
            const isFocused = idx === selectedTurnIdx;
            return (
              <div
                key={`${idx}-${turn.speaker ?? 'ctx'}`}
                className={`flex ${isLeft ? 'justify-start' : 'justify-end'}`}
              >
                <button
                  type="button"
                  onClick={() => {
                    setSelectedTurnIdx(idx);
                    if (isTurnPlaying) {
                      speechService.stopSpeaking();
                    } else {
                      speechService.speak(turn.text, { id: turnId });
                    }
                  }}
                  className={`w-[92%] rounded-2xl border p-3 text-left transition ${
                    isFocused
                      ? 'border-[var(--accent)] bg-[var(--surface)] shadow-sm'
                      : 'border-[var(--line)] bg-[var(--surface)]/75 hover:border-[var(--accent)]/60'
                  }`}
                >
                  <div className="flex items-center justify-between gap-2">
                    <span className="text-[11px] font-bold text-[var(--accent-ink)]">
                      {turn.speaker ?? 'Kontext'} · {turn.moveLabel}
                    </span>
                    <Volume2
                      className={`h-3.5 w-3.5 ${
                        isTurnPlaying ? 'text-[var(--accent)]' : 'text-[var(--ink2)]'
                      }`}
                    />
                  </div>
                  <p className="mt-1 text-xs leading-relaxed text-[var(--ink)]">
                    {turn.text}
                  </p>
                </button>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
};

export const ImprintGrammarBoard: React.FC<{
  unitId: string;
  grammar: string;
  language?: ProgrammeLanguage;
}> = ({ unitId, grammar, language = 'DE' }) => {
  const parsed = useMemo(() => parseGrammarVisuals(grammar), [grammar]);
  const [selectedClauseIdx, setSelectedClauseIdx] = useState(0);
  const [speakingId, setSpeakingId] = useState<string | null>(
    speechService.getSpeakingId(),
  );

  useEffect(() => {
    setSelectedClauseIdx(0);
  }, [unitId]);

  useEffect(() => {
    return speechService.subscribe(() => setSpeakingId(speechService.getSpeakingId()));
  }, []);

  const activeClause = parsed.clauses[selectedClauseIdx] ?? parsed.clauses[0] ?? null;

  return (
    <div className="space-y-4 rounded-3xl border border-[var(--line)] bg-[var(--surface2)] p-4">
      <div className="flex flex-wrap items-center justify-between gap-2">
        <div>
          <p className="text-xs font-extrabold text-[var(--violet)]">
            {language === 'EN' ? 'Visual Grammar Blueprint' : 'Visuelles Grammatik-Modell'}
          </p>
          <p className="text-[11px] text-[var(--ink2)]">
            {activeClause ? activeClause.architectureLabel : 'Satzbau & Sprachwirkung im Kontext'}
          </p>
        </div>

        {parsed.clauses.length > 1 && (
          <div className="flex items-center gap-1 rounded-xl border border-[var(--line)] bg-[var(--surface)] p-1">
            {parsed.clauses.map((c, idx) => (
              <button
                key={c.quote}
                type="button"
                onClick={() => setSelectedClauseIdx(idx)}
                className={`rounded-lg px-2.5 py-1 text-[11px] font-bold transition ${
                  idx === selectedClauseIdx
                    ? 'bg-[var(--accent)] text-[var(--on-accent)]'
                    : 'text-[var(--ink2)] hover:text-[var(--ink)]'
                }`}
              >
                Muster {idx + 1}
              </button>
            ))}
          </div>
        )}
      </div>

      {activeClause && (
        <div key={activeClause.quote} className="c1-animate-enter space-y-3">
          {/* Interactive Example Bar */}
          <div className="flex flex-wrap items-center justify-between gap-2 rounded-2xl border border-[var(--line)] bg-[var(--hero)] px-4 py-3 text-[var(--on-hero)]">
            <p className="text-sm font-bold text-[var(--on-hero)]">
              „{activeClause.quote}“
            </p>
            <button
              type="button"
              onClick={() => {
                const id = `grammar-${unitId}-${selectedClauseIdx}`;
                if (speakingId === id) {
                  speechService.stopSpeaking();
                } else {
                  speechService.speak(activeClause.quote, { id, rate: 0.94 });
                }
              }}
              className="inline-flex items-center gap-1 rounded-lg border border-white/20 bg-white/10 px-2.5 py-1 text-[11px] font-bold text-[var(--on-hero)] hover:border-[var(--accent)]"
            >
              <Volume2 className="h-3 w-3 text-[var(--accent)]" />
              Hören
            </button>
          </div>

          {/* Animated Slot Architecture Flow */}
          <div className="grid gap-2 sm:grid-cols-2 lg:grid-cols-4">
            {activeClause.slots.map((slot) => {
              const borderVar =
                slot.tone === 'violet'
                  ? 'border-[var(--violet)]/50'
                  : slot.tone === 'sky'
                    ? 'border-[var(--sky)]/50'
                    : slot.tone === 'ok'
                      ? 'border-[var(--ok)]/50'
                      : 'border-[var(--accent)]/50';
              const textVar =
                slot.tone === 'violet'
                  ? 'text-[var(--violet)]'
                  : slot.tone === 'sky'
                    ? 'text-[var(--sky)]'
                    : slot.tone === 'ok'
                      ? 'text-[var(--ok)]'
                      : 'text-[var(--accent-ink)]';

              return (
                <div
                  key={slot.label}
                  className={`rounded-2xl border bg-[var(--surface)] p-3 ${borderVar}`}
                >
                  <p className={`text-[10px] font-extrabold uppercase tracking-wider ${textVar}`}>
                    {slot.label}
                  </p>
                  <p className="mt-1 text-xs font-extrabold text-[var(--ink)]">
                    {slot.text}
                  </p>
                </div>
              );
            })}
          </div>

          <p className="rounded-2xl border border-[var(--line)] bg-[var(--surface)] px-3.5 py-2.5 text-xs leading-relaxed text-[var(--ink2)]">
            {activeClause.insight}
          </p>
        </div>
      )}

      {/* Epistemic Certainty Spectrum (if detected) */}
      {parsed.spectrum.length > 0 && (
        <div className="space-y-2 rounded-2xl border border-[var(--line)] bg-[var(--surface)] p-3.5">
          <p className="text-[11px] font-extrabold uppercase tracking-wider text-[var(--ink2)]">
            Aussagestärke · Gewissheits-Spektrum
          </p>
          <div className="space-y-2">
            {parsed.spectrum.map((item) => (
              <div key={item.marker} className="space-y-1">
                <div className="flex items-center justify-between text-xs">
                  <span className="font-extrabold text-[var(--ink)]">
                    {item.marker} · {item.label}
                  </span>
                  <span className="tabular-nums text-[11px] text-[var(--ink2)]">
                    {item.note}
                  </span>
                </div>
                <div className="h-2 w-full overflow-hidden rounded-full bg-[var(--surface2)]">
                  <div
                    className="h-full rounded-full bg-[var(--violet)] transition-all duration-300"
                    style={{ width: `${item.certainty}%` }}
                  />
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Compact Key Rules */}
      {parsed.rules.length > 0 && (
        <div className="grid gap-2 sm:grid-cols-2">
          {parsed.rules.map((rule, i) => (
            <div
              key={i}
              className="rounded-2xl border border-[var(--line)] bg-[var(--surface)] p-3 text-xs leading-relaxed text-[var(--ink)]"
            >
              {rule}
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export const ImprintPatternFlipDeck: React.FC<{
  unitId: string;
  expressions: string[];
  selectedIndex: number;
  onSelectedIndexChange: (idx: number) => void;
  language?: ProgrammeLanguage;
}> = ({
  unitId,
  expressions,
  selectedIndex,
  onSelectedIndexChange,
  language = 'DE',
}) => {
  const [flipped, setFlipped] = useState(false);
  const [speakingId, setSpeakingId] = useState<string | null>(
    speechService.getSpeakingId(),
  );

  useEffect(() => {
    setFlipped(false);
  }, [unitId, selectedIndex]);

  useEffect(() => {
    return speechService.subscribe(() => setSpeakingId(speechService.getSpeakingId()));
  }, []);

  if (expressions.length === 0) return null;

  const index = Math.min(Math.max(0, selectedIndex), expressions.length - 1);
  const activeExpr = expressions[index];
  const parts = activeExpr.split(/\s+[–—]\s+/, 2);
  const phrase = parts[0]?.trim() || activeExpr;
  const purpose = parts[1]?.trim() || 'Präziser C1-Gesprächszug';
  const ttsId = `pattern-flip-${unitId}-${index}`;
  const isPlaying = speakingId === ttsId;

  const hasPlaceholder = phrase.includes('…') || phrase.includes('...');
  const slots = phrase
    .split(/(…|\.\.\.)/)
    .map((s) => s.trim())
    .filter(Boolean);

  return (
    <div className="space-y-3">
      <div className="flex flex-wrap gap-1.5">
        {expressions.map((expr, i) => {
          const base = expr.split(/\s+[–—]\s+/)[0] ?? expr;
          const label = base.length > 26 ? `${base.slice(0, 26)}…` : base;
          const active = i === index;
          return (
            <button
              key={expr}
              type="button"
              onClick={() => onSelectedIndexChange(i)}
              className={`rounded-xl border px-2.5 py-1.5 text-xs font-bold transition ${
                active
                  ? 'border-[var(--accent)] bg-[var(--accent-soft)] text-[var(--accent-ink)]'
                  : 'border-[var(--line)] bg-[var(--surface2)] text-[var(--ink2)] hover:text-[var(--ink)]'
              }`}
            >
              {i + 1} · {label}
            </button>
          );
        })}
      </div>

      {/* 3D Flip Move Card */}
      <div className="c1-flip-scene">
        <div
          className={`c1-flip-card min-h-[210px] ${flipped ? 'is-flipped' : ''}`}
        >
          {/* Front Face */}
          <div className="c1-flip-face rounded-3xl border border-[var(--line)] bg-[var(--hero)] p-5 text-[var(--on-hero)] shadow-sm">
            <div className="flex items-center justify-between gap-2">
              <span className="text-[11px] font-bold text-[var(--accent)]">
                {language === 'EN' ? 'Expression Move' : 'Sprachzug'} · {index + 1}/{expressions.length}
              </span>
              <div className="flex items-center gap-2">
                <button
                  type="button"
                  onClick={() =>
                    isPlaying
                      ? speechService.stopSpeaking()
                      : speechService.speak(phrase, { id: ttsId })
                  }
                  className="inline-flex items-center gap-1 rounded-xl border border-white/20 bg-white/10 px-2.5 py-1 text-[11px] font-bold text-[var(--on-hero)]"
                >
                  <Volume2 className="h-3 w-3 text-[var(--accent)]" />
                  Hören
                </button>
                <button
                  type="button"
                  onClick={() => setFlipped(true)}
                  className="inline-flex items-center gap-1 rounded-xl bg-[var(--accent)] px-2.5 py-1 text-[11px] font-extrabold text-[var(--on-accent)]"
                >
                  <FlipHorizontal2 className="h-3 w-3" />
                  Zerlegen
                </button>
              </div>
            </div>

            <div className="mt-4 grid items-center gap-4 sm:grid-cols-12">
              <div className="sm:col-span-7">
                <p className="font-display text-xl font-extrabold leading-snug text-[var(--on-hero)]">
                  {phrase}
                </p>
                <div className="mt-3 flex items-center gap-2 text-xs text-[var(--on-hero2)]">
                  <ArrowRight className="h-4 w-4 shrink-0 text-[var(--accent)]" />
                  <span>{purpose}</span>
                </div>
              </div>

              <div className="sm:col-span-5">
                <svg viewBox="0 0 200 100" className="h-24 w-full" aria-hidden="true">
                  <rect
                    x="10"
                    y="16"
                    width="72"
                    height="68"
                    rx="12"
                    fill="rgba(45,212,191,0.16)"
                    stroke="var(--accent)"
                    strokeWidth="1.8"
                  />
                  <text x="46" y="47" textAnchor="middle" fill="var(--accent)" fontSize="9" fontWeight="800">
                    FORM
                  </text>
                  <text x="46" y="61" textAnchor="middle" fill="var(--on-hero)" fontSize="8">
                    Rahmen
                  </text>
                  <path
                    d="M 84 50 L 114 50"
                    stroke="var(--accent)"
                    strokeWidth="2.5"
                    className="c1-svg-flow"
                  />
                  <rect
                    x="118"
                    y="16"
                    width="72"
                    height="68"
                    rx="12"
                    fill="rgba(161,150,255,0.18)"
                    stroke="var(--violet)"
                    strokeWidth="1.8"
                  />
                  <text x="154" y="47" textAnchor="middle" fill="var(--violet)" fontSize="9" fontWeight="800">
                    WIRKUNG
                  </text>
                  <text x="154" y="61" textAnchor="middle" fill="var(--on-hero)" fontSize="8">
                    Funktion
                  </text>
                </svg>
              </div>
            </div>
          </div>

          {/* Back Face */}
          <div className="c1-flip-face c1-flip-face-back absolute inset-0 flex flex-col justify-between rounded-3xl border border-[var(--violet)]/45 bg-[var(--surface)] p-5 shadow-sm">
            <div className="flex items-center justify-between gap-2">
              <span className="text-[11px] font-extrabold uppercase tracking-wider text-[var(--violet)]">
                Bausteine & Anwendung
              </span>
              <button
                type="button"
                onClick={() => setFlipped(false)}
                className="inline-flex items-center gap-1 rounded-xl border border-[var(--line)] bg-[var(--surface2)] px-2.5 py-1 text-[11px] font-bold text-[var(--ink)]"
              >
                <RotateCcw className="h-3 w-3" />
                Zurückdrehen
              </button>
            </div>

            <div className="flex flex-wrap items-center gap-2 py-2">
              {slots.map((slot, idx) => {
                const isGap = slot === '…' || slot === '...';
                return (
                  <span
                    key={`${slot}-${idx}`}
                    className={`rounded-xl border px-3 py-1.5 text-xs font-extrabold ${
                      isGap
                        ? 'border-dashed border-[var(--accent)] bg-[var(--accent-soft)] text-[var(--accent-ink)]'
                        : 'border-[var(--line)] bg-[var(--surface2)] text-[var(--ink)]'
                    }`}
                  >
                    {isGap ? '[ eigene Aussage ]' : slot}
                  </span>
                );
              })}
            </div>

            <p className="text-xs leading-relaxed text-[var(--ink2)]">
              <strong className="text-[var(--ink)]">Wirkung:</strong> {purpose}.{' '}
              {hasPlaceholder
                ? 'Fülle die gestrichelte Lücke direkt mit deinem konkreten Kontext.'
                : 'Achte im Gespräch auf Tonfall und expliziten Bezug auf Vorhergesagtes.'}
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};

export const ImprintRetrievalCanvas: React.FC<{
  draftLength: number;
  hasQuestionMark: boolean;
}> = ({ draftLength, hasQuestionMark }) => {
  const nodes = [
    { label: '1. Kernaussage', sub: 'Erster Gedanke frei', active: draftLength > 10 },
    { label: '2. Ablauf / Beleg', sub: 'Ohne Vorlage ordnen', active: draftLength > 45 },
    { label: '3. Offene Frage', sub: 'Lücke sichtbar machen', active: hasQuestionMark },
  ];

  return (
    <div className="c1-animate-enter rounded-2xl border border-[var(--line)] bg-[var(--hero)] p-4 text-[var(--on-hero)]">
      <div className="mb-2.5 flex items-center justify-between text-[11px]">
        <span className="font-extrabold text-[var(--accent)]">
          Gedächtnis-Kompass · Erst abrufen, dann prüfen
        </span>
        <span className="text-[var(--on-hero2)]">
          {nodes.filter((n) => n.active).length}/3 Anker genutzt
        </span>
      </div>
      <div className="grid gap-2 sm:grid-cols-3">
        {nodes.map((node) => (
          <div
            key={node.label}
            className={`flex items-center gap-2.5 rounded-xl border p-2.5 transition ${
              node.active
                ? 'border-[var(--accent)] bg-[var(--accent)]/15'
                : 'border-white/15 bg-white/5'
            }`}
          >
            {node.active ? (
              <CheckCircle2 className="h-4 w-4 shrink-0 text-[var(--accent)]" />
            ) : (
              <HelpCircle className="h-4 w-4 shrink-0 text-[var(--on-hero2)]" />
            )}
            <div className="min-w-0">
              <p className="truncate text-xs font-extrabold text-[var(--on-hero)]">
                {node.label}
              </p>
              <p className="truncate text-[10px] text-[var(--on-hero2)]">{node.sub}</p>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export const ImprintTaskBlueprint: React.FC<{
  unitId: string;
  taskKind: string;
  title: string;
  promptText: string;
  limitationNote?: string;
}> = ({ unitId, taskKind, title, promptText, limitationNote }) => {
  const blueprint = useMemo(
    () => parseTaskBlueprint(promptText, taskKind),
    [promptText, taskKind],
  );
  const [checkedIds, setCheckedIds] = useState<Record<string, boolean>>({});
  const [showFullPrompt, setShowFullPrompt] = useState(false);

  useEffect(() => {
    setCheckedIds({});
    setShowFullPrompt(false);
  }, [unitId, taskKind]);

  const toggleCheck = (id: string) =>
    setCheckedIds((prev) => ({ ...prev, [id]: !prev[id] }));

  return (
    <div className="c1-animate-enter space-y-3 rounded-3xl border border-[var(--line)] bg-[var(--surface2)] p-4">
      {/* Visual Sender → Target Diagram Strip */}
      <div className="flex flex-wrap items-center justify-between gap-2 rounded-2xl border border-[var(--line)] bg-[var(--hero)] px-4 py-3 text-[var(--on-hero)]">
        <div className="flex flex-wrap items-center gap-2 text-xs font-extrabold">
          <span className="text-[var(--accent)]">{title}</span>
          <span aria-hidden="true" className="text-[var(--on-hero2)]">·</span>
          <span className="text-[var(--on-hero)]">{blueprint.roleFrom}</span>
          <ArrowRight className="h-3.5 w-3.5 text-[var(--accent)]" />
          <span className="text-[var(--sky)]">{blueprint.roleTo}</span>
        </div>
        {blueprint.wordTarget && (
          <span className="text-[11px] font-bold tabular-nums text-[var(--accent)]">
            Zielumfang: {blueprint.wordTarget}
          </span>
        )}
      </div>

      {/* Interactive Bausteine Checklist */}
      <div className="space-y-1.5">
        <p className="text-[11px] font-bold text-[var(--ink2)]">
          Bausteine zum Abhaken beim Formulieren:
        </p>
        <div className="grid gap-2 sm:grid-cols-2">
          {blueprint.checklist.map((item) => {
            const checked = Boolean(checkedIds[item.id]);
            return (
              <button
                key={item.id}
                type="button"
                onClick={() => toggleCheck(item.id)}
                className={`flex items-start gap-2.5 rounded-2xl border p-3 text-left text-xs transition ${
                  checked
                    ? 'border-[var(--ok)] bg-[var(--ok-bg)] text-[var(--ink)]'
                    : item.kind === 'constraint'
                      ? 'border-[var(--violet)]/35 bg-[var(--surface)] text-[var(--ink)] hover:border-[var(--accent)]'
                      : 'border-[var(--line)] bg-[var(--surface)] text-[var(--ink)] hover:border-[var(--accent)]'
                }`}
              >
                <span
                  className={`mt-0.5 flex h-4 w-4 shrink-0 items-center justify-center rounded border text-[10px] font-black ${
                    checked
                      ? 'border-[var(--ok)] bg-[var(--ok)] text-white'
                      : 'border-[var(--ink2)] text-transparent'
                  }`}
                >
                  ✓
                </span>
                <span className={checked ? 'line-through opacity-75' : ''}>
                  {item.text}
                </span>
              </button>
            );
          })}
        </div>
      </div>

      {/* Guardrails + Optional Raw Prompt Toggle */}
      <div className="flex flex-wrap items-center justify-between gap-2 pt-0.5">
        <div className="flex flex-wrap items-center gap-3 text-[11px] text-[var(--ink2)]">
          {blueprint.guards.map((g) => (
            <span key={g} className="inline-flex items-center gap-1 font-semibold text-[var(--coral)]">
              <ShieldAlert className="h-3.5 w-3.5" />
              {g}
            </span>
          ))}
        </div>

        <button
          type="button"
          onClick={() => setShowFullPrompt((v) => !v)}
          className="inline-flex items-center gap-1 text-[11px] font-bold text-[var(--accent-ink)]"
        >
          <Eye className="h-3.5 w-3.5" />
          {showFullPrompt ? 'Volltext ausblenden' : 'Ganze Aufgabenstellung'}
        </button>
      </div>

      {showFullPrompt && (
        <p className="c1-animate-enter rounded-2xl border border-[var(--line)] bg-[var(--surface)] p-3 text-xs leading-relaxed text-[var(--ink)]">
          {promptText}
        </p>
      )}

      {limitationNote && (
        <p className="text-[11px] text-[var(--ink2)]">{limitationNote}</p>
      )}
    </div>
  );
};

export const ContextIllustrationFrame: React.FC<{
  context: ContextTrack;
  compact?: boolean;
  caption?: string;
}> = ({ context, compact = false, caption }) => {
  const [imgError, setImgError] = useState(false);
  const art = CONTEXT_ARTWORK[context];

  return (
    <div
      className={`relative overflow-hidden rounded-2xl border border-white/15 bg-[var(--hero)] ${
        compact ? 'aspect-[16/9] w-full' : 'h-44 w-full'
      }`}
    >
      {!imgError ? (
        <img
          src={art.src}
          alt={art.alt}
          referrerPolicy="no-referrer"
          onError={() => setImgError(true)}
          className="h-full w-full object-cover"
        />
      ) : (
        <div className="flex h-full w-full items-center justify-center bg-gradient-to-br from-[var(--accent-soft)] via-[var(--hero)] to-[var(--violet-soft)] p-4 text-center">
          <span className="text-xs font-extrabold text-[var(--on-hero)]">{art.kicker}</span>
        </div>
      )}
      <div className="pointer-events-none absolute inset-0 bg-gradient-to-t from-black/85 via-black/30 to-transparent" />
      <div className="absolute inset-x-0 bottom-0 p-3">
        <p className="text-[10px] font-extrabold uppercase tracking-wider text-[var(--accent)]">
          {art.kicker}
        </p>
        <p className="truncate text-xs font-bold text-white">
          {caption ?? art.subtitle}
        </p>
      </div>
    </div>
  );
};

export const VocabMemoryCurveVisual: React.FC<{
  dueCount: number;
  masteredCount: number;
  totalCount: number;
  activeIntervalDays?: number;
  activeRepetitions?: number;
}> = ({
  dueCount,
  masteredCount,
  totalCount,
  activeIntervalDays = 1,
  activeRepetitions = 0,
}) => {
  const milestones = [
    { label: 'Tag 1', sub: 'Erst-Abruf', done: activeRepetitions >= 1 },
    { label: 'Tag 3', sub: 'Festigung', done: activeIntervalDays >= 3 },
    { label: 'Tag 7', sub: 'Transfer', done: activeIntervalDays >= 7 },
    { label: 'Langzeit', sub: 'Abrufbar', done: activeIntervalDays >= 14 },
  ];

  return (
    <div className="c1-animate-enter rounded-3xl border border-[var(--line)] bg-[var(--hero)] p-4 text-[var(--on-hero)]">
      <div className="flex flex-wrap items-center justify-between gap-2">
        <div>
          <p className="text-[10px] font-extrabold uppercase tracking-wider text-[var(--accent)]">
            Visuelle Erinnerungskurve (SM-2 Abstandsmodell)
          </p>
          <p className="text-xs text-[var(--on-hero2)]">
            Aktive Wiederholung im richtigen Abstand verhindert das Vergessen ohne stures Auswendiglernen.
          </p>
        </div>
        <div className="flex items-center gap-3 text-xs tabular-nums">
          <span className="font-bold text-[var(--accent)]">{dueCount} fällig</span>
          <span aria-hidden="true">·</span>
          <span className="font-bold text-[var(--ok)]">{masteredCount}/{totalCount} gefestigt</span>
        </div>
      </div>

      <svg
        viewBox="0 0 540 110"
        className="mt-3 h-24 w-full"
        role="img"
        aria-label="SM-2 Abrufkurve über mehrere Tage"
      >
        <line x1="20" y1="88" x2="520" y2="88" stroke="rgba(255,255,255,0.2)" strokeWidth="1.5" />
        {/* Passive forgetting decay curve */}
        <path
          d="M 30 24 Q 130 82, 270 86"
          fill="none"
          stroke="var(--coral)"
          strokeOpacity="0.45"
          strokeWidth="2"
          strokeDasharray="4 4"
        />
        {/* Active spaced retrieval boost curve */}
        <path
          d="M 30 24 Q 75 56, 130 62 L 130 22 Q 210 44, 290 48 L 290 20 Q 395 32, 500 22"
          fill="none"
          stroke="var(--accent)"
          strokeWidth="3"
          className="c1-svg-flow"
        />
        {[30, 130, 290, 500].map((cx, idx) => {
          const m = milestones[idx];
          return (
            <g key={m.label}>
              <circle
                cx={cx}
                cy={idx === 0 ? 24 : idx === 1 ? 22 : idx === 2 ? 20 : 22}
                r={m.done ? 8 : 6}
                fill={m.done ? 'var(--accent)' : 'var(--hero)'}
                stroke="var(--accent)"
                strokeWidth="2.2"
              />
              <text
                x={cx}
                y="102"
                textAnchor="middle"
                fill="var(--on-hero2)"
                fontSize="9.5"
                fontWeight="700"
              >
                {m.label} · {m.sub}
              </text>
            </g>
          );
        })}
      </svg>
    </div>
  );
};
