import React, { useEffect, useMemo, useState } from 'react';
import {
  ArrowRight,
  CheckCircle2,
  GitCompare,
  Layers,
  Puzzle,
  RotateCcw,
  Scissors,
  Sparkles,
  Volume2,
  XCircle,
} from 'lucide-react';
import { CONTEXT_LABELS } from '../data/learningData';
import {
  getUnitVisualDeck,
  type MentalDiagramArchetype,
} from '../data/visualLessonDecks';
import { speechService } from '../services/speechService';
import type { ContextTrack, VocabularySrsItem } from '../types';

function deterministicScramble(items: string[], seedStr: string): string[] {
  if (items.length <= 1) return items;
  let hash = 0;
  for (let i = 0; i < seedStr.length; i++) {
    hash = (hash * 31 + seedStr.charCodeAt(i)) >>> 0;
  }
  const arr = [...items];
  for (let i = arr.length - 1; i > 0; i--) {
    hash = (hash * 1664525 + 1013904223) >>> 0;
    const j = hash % (i + 1);
    const tmp = arr[i];
    arr[i] = arr[j];
    arr[j] = tmp;
  }
  // Guarantee not trivially identical if possible
  if (arr.every((v, idx) => v === items[idx]) && arr.length > 1) {
    const first = arr[0];
    arr[0] = arr[arr.length - 1];
    arr[arr.length - 1] = first;
  }
  return arr;
}

function clampLabel(text: string, maxLen = 20): string {
  const t = text.trim();
  if (t.length <= maxLen) return t;
  return `${t.slice(0, maxLen - 1)}…`;
}

export const MentalModelDiagramSvg: React.FC<{
  archetype: MentalDiagramArchetype;
  nodes: [string, string, string];
  activeStep: 0 | 1 | 2;
  onSelectStep: (idx: 0 | 1 | 2) => void;
}> = ({ archetype, nodes, activeStep, onSelectStep }) => {
  const n0 = clampLabel(nodes[0]);
  const n1 = clampLabel(nodes[1]);
  const n2 = clampLabel(nodes[2]);

  const nodeColor = (idx: 0 | 1 | 2) =>
    activeStep === idx
      ? 'var(--accent)'
      : idx === 1
        ? 'var(--sky)'
        : 'var(--violet)';

  return (
    <div className="rounded-2xl border border-white/15 bg-[var(--hero)] p-3.5 text-[var(--on-hero)]">
      <svg
        viewBox="0 0 560 175"
        className="h-44 w-full"
        role="img"
        aria-label={`Interaktives Denkmodell: ${nodes.join(' – ')}`}
      >
        <defs>
          <marker
            id="imprintDiagramArrow"
            markerWidth="8"
            markerHeight="8"
            refX="6"
            refY="4"
            orient="auto"
          >
            <polygon points="0 0, 8 4, 0 8" fill="var(--accent)" />
          </marker>
        </defs>

        {/* Subtle structural grid lines */}
        <g stroke="rgba(255,255,255,0.06)" strokeWidth="1">
          <line x1="20" y1="45" x2="540" y2="45" />
          <line x1="20" y1="90" x2="540" y2="90" />
          <line x1="20" y1="135" x2="540" y2="135" />
        </g>

        {archetype === 'SPECTRUM' && (
          <g>
            <linearGradient id="specGrad" x1="0%" y1="0%" x2="100%" y2="0%">
              <stop offset="0%" stopColor="var(--coral)" />
              <stop offset="50%" stopColor="var(--sky)" />
              <stop offset="100%" stopColor="var(--accent)" />
            </linearGradient>
            <rect
              x="60"
              y="76"
              width="440"
              height="10"
              rx="5"
              fill="url(#specGrad)"
              opacity="0.75"
            />
            {[95, 280, 465].map((cx, idx) => {
              const i = idx as 0 | 1 | 2;
              const isAct = activeStep === i;
              return (
                <g
                  key={i}
                  onClick={() => onSelectStep(i)}
                  className="cursor-pointer"
                >
                  <circle
                    cx={cx}
                    cy="81"
                    r={isAct ? 18 : 13}
                    fill={isAct ? 'var(--accent)' : 'var(--hero)'}
                    stroke={isAct ? '#FFFFFF' : nodeColor(i)}
                    strokeWidth={isAct ? 3 : 2}
                    className="transition-all duration-300"
                  />
                  <text
                    x={cx}
                    y="85"
                    textAnchor="middle"
                    fill={isAct ? 'var(--on-accent)' : '#FFFFFF'}
                    fontSize="11"
                    fontWeight="900"
                  >
                    {idx + 1}
                  </text>
                  <rect
                    x={cx - 72}
                    y="112"
                    width="144"
                    height="36"
                    rx="10"
                    fill={isAct ? 'rgba(255,255,255,0.14)' : 'rgba(255,255,255,0.05)'}
                    stroke={isAct ? 'var(--accent)' : 'rgba(255,255,255,0.16)'}
                    strokeWidth="1.5"
                  />
                  <text
                    x={cx}
                    y="134"
                    textAnchor="middle"
                    fill="#FFFFFF"
                    fontSize="11"
                    fontWeight={isAct ? '800' : '600'}
                  >
                    {[n0, n1, n2][i]}
                  </text>
                </g>
              );
            })}
            {/* Sliding Indicator Triangle */}
            <polygon
              points="0,0 14,0 7,10"
              fill="var(--accent)"
              style={{
                transform: `translate(${[88, 273, 458][activeStep]}px, 42px)`,
                transition: 'transform 380ms cubic-bezier(0.22, 1, 0.36, 1)',
              }}
            />
          </g>
        )}

        {archetype === 'SCALE' && (
          <g>
            {/* Fulcrum */}
            <polygon
              points="280,96 254,152 306,152"
              fill="rgba(255,255,255,0.08)"
              stroke="rgba(255,255,255,0.3)"
              strokeWidth="2"
            />
            {/* Tilting Beam */}
            <g
              style={{
                transform: `rotate(${activeStep === 0 ? -7 : activeStep === 1 ? 0 : 7}deg)`,
                transformOrigin: '280px 88px',
                transition: 'transform 450ms cubic-bezier(0.22, 1, 0.36, 1)',
              }}
            >
              <line
                x1="100"
                y1="88"
                x2="460"
                y2="88"
                stroke="var(--accent)"
                strokeWidth="4"
                strokeLinecap="round"
              />
              {/* Left Pan */}
              <g onClick={() => onSelectStep(0)} className="cursor-pointer">
                <line x1="115" y1="88" x2="115" y2="115" stroke="rgba(255,255,255,0.45)" strokeWidth="2" />
                <rect
                  x="42"
                  y="40"
                  width="146"
                  height="36"
                  rx="10"
                  fill={activeStep === 0 ? 'var(--accent)' : 'rgba(255,255,255,0.1)'}
                  stroke="var(--accent)"
                  strokeWidth="1.8"
                />
                <text
                  x="115"
                  y="62"
                  textAnchor="middle"
                  fill={activeStep === 0 ? 'var(--on-accent)' : '#FFFFFF'}
                  fontSize="11"
                  fontWeight="800"
                >
                  {n0}
                </text>
              </g>

              {/* Right Pan */}
              <g onClick={() => onSelectStep(2)} className="cursor-pointer">
                <line x1="445" y1="88" x2="445" y2="115" stroke="rgba(255,255,255,0.45)" strokeWidth="2" />
                <rect
                  x="372"
                  y="40"
                  width="146"
                  height="36"
                  rx="10"
                  fill={activeStep === 2 ? 'var(--accent)' : 'rgba(255,255,255,0.1)'}
                  stroke="var(--violet)"
                  strokeWidth="1.8"
                />
                <text
                  x="445"
                  y="62"
                  textAnchor="middle"
                  fill={activeStep === 2 ? 'var(--on-accent)' : '#FFFFFF'}
                  fontSize="11"
                  fontWeight="800"
                >
                  {n2}
                </text>
              </g>
            </g>

            {/* Center Criterion Pivot */}
            <g onClick={() => onSelectStep(1)} className="cursor-pointer">
              <rect
                x="208"
                y="18"
                width="144"
                height="34"
                rx="10"
                fill={activeStep === 1 ? 'var(--accent)' : 'var(--hero)'}
                stroke="var(--sky)"
                strokeWidth="2"
              />
              <text
                x="280"
                y="39"
                textAnchor="middle"
                fill={activeStep === 1 ? 'var(--on-accent)' : '#FFFFFF'}
                fontSize="11"
                fontWeight="800"
              >
                {n1}
              </text>
            </g>
          </g>
        )}

        {archetype === 'BRACKET' && (
          <g>
            {/* Satzklammer Arch */}
            <path
              d="M 105 94 C 165 20, 395 20, 455 94"
              fill="none"
              stroke="var(--accent)"
              strokeWidth="3.5"
              className="c1-svg-flow"
            />
            {[95, 280, 465].map((cx, idx) => {
              const i = idx as 0 | 1 | 2;
              const isAct = activeStep === i;
              const y = i === 1 ? 52 : 106;
              return (
                <g key={i} onClick={() => onSelectStep(i)} className="cursor-pointer">
                  <rect
                    x={cx - 78}
                    y={y}
                    width="156"
                    height="42"
                    rx="12"
                    fill={isAct ? 'var(--accent)' : 'rgba(255,255,255,0.09)'}
                    stroke={isAct ? '#FFFFFF' : nodeColor(i)}
                    strokeWidth="2"
                  />
                  <text
                    x={cx}
                    y={y + 17}
                    textAnchor="middle"
                    fill={isAct ? 'var(--on-accent)' : 'var(--accent)'}
                    fontSize="9"
                    fontWeight="900"
                  >
                    {i === 0 ? 'VORSITZ / POS. 2' : i === 1 ? 'SPANNBOGEN' : 'KLAMMER-ENDE'}
                  </text>
                  <text
                    x={cx}
                    y={y + 33}
                    textAnchor="middle"
                    fill={isAct ? 'var(--on-accent)' : '#FFFFFF'}
                    fontSize="11"
                    fontWeight="800"
                  >
                    {[n0, n1, n2][i]}
                  </text>
                </g>
              );
            })}
          </g>
        )}

        {archetype === 'MATRIX_2X2' && (
          <g>
            <line x1="280" y1="18" x2="280" y2="156" stroke="rgba(255,255,255,0.22)" strokeWidth="1.5" strokeDasharray="4 4" />
            <line x1="48" y1="88" x2="512" y2="88" stroke="rgba(255,255,255,0.22)" strokeWidth="1.5" strokeDasharray="4 4" />
            {[
              { x: 64, y: 24, idx: 0 as const, label: n0 },
              { x: 296, y: 24, idx: 1 as const, label: n1 },
              { x: 180, y: 100, idx: 2 as const, label: n2 },
            ].map((cell) => {
              const isAct = activeStep === cell.idx;
              return (
                <g
                  key={cell.idx}
                  onClick={() => onSelectStep(cell.idx)}
                  className="cursor-pointer"
                >
                  <rect
                    x={cell.x}
                    y={cell.y}
                    width="200"
                    height="50"
                    rx="12"
                    fill={isAct ? 'var(--accent)' : 'rgba(255,255,255,0.09)'}
                    stroke={isAct ? '#FFFFFF' : nodeColor(cell.idx)}
                    strokeWidth="2"
                  />
                  <text
                    x={cell.x + 100}
                    y={cell.y + 21}
                    textAnchor="middle"
                    fill={isAct ? 'var(--on-accent)' : 'var(--accent)'}
                    fontSize="9"
                    fontWeight="900"
                  >
                    ACHSE {cell.idx + 1}
                  </text>
                  <text
                    x={cell.x + 100}
                    y={cell.y + 38}
                    textAnchor="middle"
                    fill={isAct ? 'var(--on-accent)' : '#FFFFFF'}
                    fontSize="11.5"
                    fontWeight="800"
                  >
                    {cell.label}
                  </text>
                </g>
              );
            })}
          </g>
        )}

        {archetype === 'FUNNEL' && (
          <g>
            <polygon
              points="46,28 514,28 360,148 200,148"
              fill="rgba(45,212,191,0.09)"
              stroke="var(--accent)"
              strokeOpacity="0.45"
              strokeWidth="2"
            />
            {[
              { y: 26, w: 420, idx: 0 as const, label: n0 },
              { y: 72, w: 300, idx: 1 as const, label: n1 },
              { y: 118, w: 200, idx: 2 as const, label: n2 },
            ].map((layer) => {
              const isAct = activeStep === layer.idx;
              const x = 280 - layer.w / 2;
              return (
                <g
                  key={layer.idx}
                  onClick={() => onSelectStep(layer.idx)}
                  className="cursor-pointer"
                >
                  <rect
                    x={x}
                    y={layer.y}
                    width={layer.w}
                    height="34"
                    rx="10"
                    fill={isAct ? 'var(--accent)' : 'var(--hero)'}
                    stroke={isAct ? '#FFFFFF' : nodeColor(layer.idx)}
                    strokeWidth="2"
                  />
                  <text
                    x="280"
                    y={layer.y + 21}
                    textAnchor="middle"
                    fill={isAct ? 'var(--on-accent)' : '#FFFFFF'}
                    fontSize="11.5"
                    fontWeight="800"
                  >
                    {layer.idx + 1}. {layer.label}
                  </text>
                </g>
              );
            })}
          </g>
        )}

        {archetype === 'BRIDGE' && (
          <g>
            <path
              d="M 105 112 Q 280 18 455 112"
              fill="none"
              stroke="var(--accent)"
              strokeWidth="3.5"
              className="c1-svg-flow"
            />
            {[
              { cx: 100, cy: 112, idx: 0 as const, label: n0 },
              { cx: 280, cy: 48, idx: 1 as const, label: n1 },
              { cx: 460, cy: 112, idx: 2 as const, label: n2 },
            ].map((pt) => {
              const isAct = activeStep === pt.idx;
              return (
                <g
                  key={pt.idx}
                  onClick={() => onSelectStep(pt.idx)}
                  className="cursor-pointer"
                >
                  <rect
                    x={pt.cx - 78}
                    y={pt.cy - 22}
                    width="156"
                    height="44"
                    rx="12"
                    fill={isAct ? 'var(--accent)' : 'var(--hero)'}
                    stroke={isAct ? '#FFFFFF' : nodeColor(pt.idx)}
                    strokeWidth="2"
                  />
                  <text
                    x={pt.cx}
                    y={pt.cy + 4}
                    textAnchor="middle"
                    fill={isAct ? 'var(--on-accent)' : '#FFFFFF'}
                    fontSize="11.5"
                    fontWeight="800"
                  >
                    {pt.label}
                  </text>
                </g>
              );
            })}
          </g>
        )}

        {archetype === 'LOOP' && (
          <g>
            <path
              d="M 110 118 Q 110 38 280 38"
              fill="none"
              stroke="var(--accent)"
              strokeWidth="2.8"
              markerEnd="url(#imprintDiagramArrow)"
              className="c1-svg-flow"
            />
            <path
              d="M 280 38 Q 450 38 450 118"
              fill="none"
              stroke="var(--sky)"
              strokeWidth="2.8"
              markerEnd="url(#imprintDiagramArrow)"
              className="c1-svg-flow"
            />
            <path
              d="M 435 130 Q 280 162 125 130"
              fill="none"
              stroke="var(--violet)"
              strokeWidth="2.8"
              markerEnd="url(#imprintDiagramArrow)"
              className="c1-svg-flow"
            />
            {[
              { cx: 108, cy: 120, idx: 0 as const, label: n0 },
              { cx: 280, cy: 40, idx: 1 as const, label: n1 },
              { cx: 452, cy: 120, idx: 2 as const, label: n2 },
            ].map((pt) => {
              const isAct = activeStep === pt.idx;
              return (
                <g
                  key={pt.idx}
                  onClick={() => onSelectStep(pt.idx)}
                  className="cursor-pointer"
                >
                  <rect
                    x={pt.cx - 76}
                    y={pt.cy - 20}
                    width="152"
                    height="40"
                    rx="12"
                    fill={isAct ? 'var(--accent)' : 'var(--hero)'}
                    stroke={isAct ? '#FFFFFF' : nodeColor(pt.idx)}
                    strokeWidth="2"
                  />
                  <text
                    x={pt.cx}
                    y={pt.cy + 4}
                    textAnchor="middle"
                    fill={isAct ? 'var(--on-accent)' : '#FFFFFF'}
                    fontSize="11"
                    fontWeight="800"
                  >
                    {pt.idx + 1}. {pt.label}
                  </text>
                </g>
              );
            })}
          </g>
        )}

        {archetype === 'PIPELINE' && (
          <g>
            <line
              x1="150"
              y1="86"
              x2="210"
              y2="86"
              stroke="var(--accent)"
              strokeWidth="3"
              markerEnd="url(#imprintDiagramArrow)"
              className="c1-svg-flow"
            />
            <line
              x1="340"
              y1="86"
              x2="400"
              y2="86"
              stroke="var(--accent)"
              strokeWidth="3"
              markerEnd="url(#imprintDiagramArrow)"
              className="c1-svg-flow"
            />
            {[95, 280, 465].map((cx, idx) => {
              const i = idx as 0 | 1 | 2;
              const isAct = activeStep === i;
              return (
                <g key={i} onClick={() => onSelectStep(i)} className="cursor-pointer">
                  <rect
                    x={cx - 74}
                    y="52"
                    width="148"
                    height="68"
                    rx="14"
                    fill={isAct ? 'var(--accent)' : 'rgba(255,255,255,0.08)'}
                    stroke={isAct ? '#FFFFFF' : nodeColor(i)}
                    strokeWidth={isAct ? 2.4 : 1.6}
                  />
                  <text
                    x={cx}
                    y="76"
                    textAnchor="middle"
                    fill={isAct ? 'var(--on-accent)' : 'var(--accent)'}
                    fontSize="9.5"
                    fontWeight="900"
                  >
                    SCHRITT {idx + 1}
                  </text>
                  <text
                    x={cx}
                    y="98"
                    textAnchor="middle"
                    fill={isAct ? 'var(--on-accent)' : '#FFFFFF'}
                    fontSize="11.5"
                    fontWeight="800"
                  >
                    {[n0, n1, n2][i]}
                  </text>
                </g>
              );
            })}
          </g>
        )}
      </svg>
    </div>
  );
};

export const ImprintVisualChapterDeck: React.FC<{
  unitId: string;
  selectedContext: ContextTrack;
  onSelectContext?: (ctx: ContextTrack) => void;
  defaultTab?: 'MODEL' | 'CONTEXT' | 'PUZZLE' | 'SURGERY';
}> = ({ unitId, selectedContext, onSelectContext, defaultTab = 'MODEL' }) => {
  const deck = useMemo(() => getUnitVisualDeck(unitId), [unitId]);
  const [activeTab, setActiveTab] = useState<'MODEL' | 'CONTEXT' | 'PUZZLE' | 'SURGERY'>(
    defaultTab,
  );
  const [modelStep, setModelStep] = useState<0 | 1 | 2>(0);
  const [microChoice, setMicroChoice] = useState<number | null>(null);
  const [pickedTiles, setPickedTiles] = useState<number[]>([]);
  const [surgeryStep, setSurgeryStep] = useState<0 | 1 | 2>(0);

  const scrambledTiles = useMemo(
    () => deterministicScramble(deck.clausePuzzle.tiles, `${unitId}-puzzle`),
    [deck.clausePuzzle.tiles, unitId],
  );

  useEffect(() => {
    setModelStep(0);
    setMicroChoice(null);
    setPickedTiles([]);
    setSurgeryStep(0);
    setActiveTab(defaultTab);
  }, [unitId, defaultTab]);

  const currentStep = deck.steps[modelStep];
  const ctxPair = deck.contextTransforms[selectedContext];

  const assembledSentence = pickedTiles.map((idx) => scrambledTiles[idx]).join(' ');
  const isPuzzleComplete = pickedTiles.length === scrambledTiles.length;
  const isPuzzleCorrect =
    isPuzzleComplete &&
    pickedTiles.every(
      (tileIdx, orderIdx) =>
        scrambledTiles[tileIdx] === deck.clausePuzzle.targetOrder[orderIdx],
    );

  const speakPhrase = (text: string, id: string) => {
    speechService.speak(text, { rate: 0.96, id });
  };

  return (
    <section className="c1-animate-enter space-y-4 rounded-3xl border border-[var(--line)] bg-[var(--surface)] p-5 shadow-sm">
      {/* Imprint Chapter Deck Header */}
      <div className="flex flex-wrap items-center justify-between gap-2">
        <div>
          <p className="inline-flex items-center gap-1.5 text-[10px] font-extrabold uppercase tracking-wider text-[var(--accent-ink)]">
            <Sparkles className="h-3.5 w-3.5" />
            Visuelles Lernkapitel · Imprint-Modus
          </p>
          <h3 className="font-display text-lg font-extrabold text-[var(--ink)]">
            {deck.modelTitle}
          </h3>
        </div>

        {/* 4 Interactive Visual Modes */}
        <div className="flex flex-wrap gap-1 rounded-2xl border border-[var(--line)] bg-[var(--surface2)] p-1">
          {(
            [
              { id: 'MODEL', label: 'Schaubild', icon: Layers },
              { id: 'CONTEXT', label: 'Kontext-Morph', icon: GitCompare },
              { id: 'PUZZLE', label: 'Baukasten', icon: Puzzle },
              { id: 'SURGERY', label: 'Satz-Chirurgie', icon: Scissors },
            ] as const
          ).map((tab) => {
            const Icon = tab.icon;
            const active = activeTab === tab.id;
            return (
              <button
                key={tab.id}
                type="button"
                onClick={() => setActiveTab(tab.id)}
                className={`inline-flex items-center gap-1.5 rounded-xl px-2.5 py-1.5 text-[11px] font-extrabold transition ${
                  active
                    ? 'bg-[var(--accent)] text-[var(--on-accent)] shadow-xs'
                    : 'text-[var(--ink2)] hover:text-[var(--ink)]'
                }`}
              >
                <Icon className="h-3.5 w-3.5" />
                {tab.label}
              </button>
            );
          })}
        </div>
      </div>

      {/* TAB 1: ANIMATED MENTAL MODEL + STEP SCRUBBER */}
      {activeTab === 'MODEL' && (
        <div className="c1-animate-enter space-y-3.5">
          <MentalModelDiagramSvg
            archetype={deck.diagramType}
            nodes={currentStep.nodes}
            activeStep={modelStep}
            onSelectStep={setModelStep}
          />

          {/* Step Selector Buttons */}
          <div className="grid grid-cols-3 gap-2">
            {deck.steps.map((st, idx) => {
              const i = idx as 0 | 1 | 2;
              const active = modelStep === i;
              return (
                <button
                  key={st.tag}
                  type="button"
                  onClick={() => setModelStep(i)}
                  className={`rounded-2xl border p-2.5 text-left transition ${
                    active
                      ? 'border-[var(--accent)] bg-[var(--accent-soft)] text-[var(--ink)]'
                      : 'border-[var(--line)] bg-[var(--surface2)] text-[var(--ink2)] hover:border-[var(--accent)]/60'
                  }`}
                >
                  <p className="text-[10px] font-extrabold uppercase tracking-wider text-[var(--accent-ink)]">
                    {st.tag}
                  </p>
                  <p className="mt-0.5 line-clamp-1 text-xs font-bold text-[var(--ink)]">
                    {st.title}
                  </p>
                </button>
              );
            })}
          </div>

          {/* Bite-Sized Visual Takeaway Card */}
          <div className="flex flex-wrap items-start justify-between gap-3 rounded-2xl border border-[var(--line)] bg-[var(--surface2)] p-4">
            <div className="min-w-0 flex-1 space-y-1">
              <span className="text-[10px] font-extrabold uppercase tracking-wider text-[var(--violet)]">
                {currentStep.tag} · Kernidee
              </span>
              <h4 className="text-sm font-extrabold text-[var(--ink)]">
                {currentStep.title}
              </h4>
              <p className="text-xs leading-relaxed text-[var(--ink2)]">
                {currentStep.caption}
              </p>
            </div>
            <button
              type="button"
              onClick={() =>
                speakPhrase(
                  `${currentStep.title}. ${currentStep.caption}`,
                  `imprint-step-${unitId}-${modelStep}`,
                )
              }
              className="inline-flex shrink-0 items-center gap-1 rounded-xl border border-[var(--line)] bg-[var(--surface)] px-2.5 py-1.5 text-[11px] font-bold text-[var(--ink)] hover:border-[var(--accent)]"
            >
              <Volume2 className="h-3.5 w-3.5 text-[var(--accent-ink)]" />
              Anhören
            </button>
          </div>

          <div className="flex flex-wrap items-center justify-between gap-2 text-xs">
            <span className="text-[11px] text-[var(--ink2)]">{deck.visualHook}</span>
            <button
              type="button"
              onClick={() => {
                if (modelStep < 2) setModelStep((modelStep + 1) as 0 | 1 | 2);
                else setActiveTab('CONTEXT');
              }}
              className="inline-flex items-center gap-1 rounded-xl bg-[var(--accent)] px-3 py-1.5 text-[11px] font-extrabold text-[var(--on-accent)]"
            >
              {modelStep < 2 ? 'Nächster Schritt' : 'Zum Kontext-Vergleich'}
              <ArrowRight className="h-3.5 w-3.5" />
            </button>
          </div>
        </div>
      )}

      {/* TAB 2: CONTEXT-ADAPTIVE AVOID VS C1-PRECISION */}
      {activeTab === 'CONTEXT' && (
        <div className="c1-animate-enter space-y-3.5">
          {onSelectContext && (
            <div className="flex flex-wrap gap-1.5">
              {(Object.keys(CONTEXT_LABELS) as ContextTrack[]).map((ctx) => (
                <button
                  key={ctx}
                  type="button"
                  onClick={() => onSelectContext(ctx)}
                  className={`rounded-xl border px-3 py-1.5 text-[11px] font-bold transition ${
                    selectedContext === ctx
                      ? 'border-[var(--accent)] bg-[var(--accent-soft)] text-[var(--accent-ink)]'
                      : 'border-[var(--line)] bg-[var(--surface2)] text-[var(--ink2)] hover:text-[var(--ink)]'
                  }`}
                >
                  {CONTEXT_LABELS[ctx]}
                </button>
              ))}
            </div>
          )}

          <div className="rounded-2xl border border-[var(--line)] bg-[var(--hero)] px-4 py-2.5 text-xs text-[var(--on-hero)]">
            <span className="font-extrabold text-[var(--accent)]">
              Kontextszene: {ctxPair.sceneLabel}
            </span>
            <span aria-hidden="true" className="mx-2 text-[var(--on-hero2)]">
              ·
            </span>
            <span className="text-[var(--on-hero2)]">{CONTEXT_LABELS[selectedContext]}</span>
          </div>

          <div className="grid items-stretch gap-3 md:grid-cols-[1fr_auto_1fr]">
            <div className="rounded-2xl border border-[var(--coral)]/40 bg-[var(--coral-soft)] p-4 text-xs">
              <p className="text-[10px] font-extrabold uppercase tracking-wider text-[var(--coral)]">
                ✕ Vermeide · {ctxPair.avoidLabel}
              </p>
              <p className="mt-1.5 font-medium leading-relaxed text-[var(--ink)]">
                „{ctxPair.avoidPhrase}“
              </p>
            </div>

            <div className="flex items-center justify-center text-[var(--accent-ink)]">
              <ArrowRight className="h-5 w-5 rotate-90 md:rotate-0" />
            </div>

            <div className="flex flex-col justify-between rounded-2xl border border-[var(--ok)]/45 bg-[var(--ok-bg)] p-4 text-xs">
              <div>
                <p className="text-[10px] font-extrabold uppercase tracking-wider text-[var(--ok)]">
                  ✓ C1-Präzision
                </p>
                <p className="mt-1.5 font-bold leading-relaxed text-[var(--ink)]">
                  „{ctxPair.c1Phrase}“
                </p>
              </div>
              <div className="mt-3 flex justify-end">
                <button
                  type="button"
                  onClick={() =>
                    speakPhrase(
                      ctxPair.c1Phrase,
                      `imprint-ctx-${unitId}-${selectedContext}`,
                    )
                  }
                  className="inline-flex items-center gap-1 rounded-xl border border-[var(--ok)]/30 bg-[var(--surface)] px-2.5 py-1 text-[11px] font-bold text-[var(--ok)]"
                >
                  <Volume2 className="h-3.5 w-3.5" />
                  C1 anhören
                </button>
              </div>
            </div>
          </div>

          <div className="rounded-2xl border border-[var(--line)] bg-[var(--surface2)] px-4 py-3 text-xs text-[var(--ink2)]">
            <strong className="text-[var(--ink)]">Warum das wirkt: </strong>
            {ctxPair.grammarHint}
          </div>
        </div>
      )}

      {/* TAB 3: EMBEDDED MICRO-CHECK + TACTILE CLAUSE PUZZLE */}
      {activeTab === 'PUZZLE' && (
        <div className="c1-animate-enter space-y-4">
          {/* Quick Contrast Micro-Check */}
          <div className="space-y-2.5 rounded-2xl border border-[var(--line)] bg-[var(--surface2)] p-4">
            <p className="text-[10px] font-extrabold uppercase tracking-wider text-[var(--violet)]">
              Imprint-Impuls · Sofort-Check
            </p>
            <p className="text-xs font-extrabold text-[var(--ink)]">
              {deck.microCheck.prompt}
            </p>
            <div className="space-y-1.5">
              {deck.microCheck.options.map((opt, idx) => {
                const chosen = microChoice === idx;
                const isRight = idx === deck.microCheck.correctIndex;
                const answered = microChoice !== null;
                return (
                  <button
                    key={idx}
                    type="button"
                    onClick={() => setMicroChoice(idx)}
                    className={`flex w-full items-center gap-2 rounded-xl border p-2.5 text-left text-xs transition ${
                      answered && isRight
                        ? 'border-[var(--ok)] bg-[var(--ok-bg)] text-[var(--ink)]'
                        : answered && chosen && !isRight
                          ? 'border-[var(--coral)] bg-[var(--coral-soft)] text-[var(--ink)]'
                          : 'border-[var(--line)] bg-[var(--surface)] text-[var(--ink)] hover:border-[var(--accent)]'
                    }`}
                  >
                    {answered && isRight ? (
                      <CheckCircle2 className="h-4 w-4 shrink-0 text-[var(--ok)]" />
                    ) : answered && chosen && !isRight ? (
                      <XCircle className="h-4 w-4 shrink-0 text-[var(--coral)]" />
                    ) : (
                      <span className="h-3.5 w-3.5 shrink-0 rounded-full border border-[var(--ink2)]" />
                    )}
                    <span className="font-medium">{opt}</span>
                  </button>
                );
              })}
            </div>
            {microChoice !== null && (
              <p className="c1-animate-enter rounded-xl border border-[var(--accent)]/40 bg-[var(--accent-soft)]/50 p-2.5 text-[11px] leading-relaxed text-[var(--ink)]">
                {deck.microCheck.explanation}
              </p>
            )}
          </div>

          {/* Tactile Satz-Baukasten (Word Order Snap) */}
          <div className="space-y-3 rounded-2xl border border-[var(--accent)]/35 bg-[var(--surface2)] p-4">
            <div className="flex flex-wrap items-center justify-between gap-2">
              <div>
                <p className="text-[10px] font-extrabold uppercase tracking-wider text-[var(--accent-ink)]">
                  Taktiler Satz-Baukasten · Wortstellung
                </p>
                <p className="text-xs font-extrabold text-[var(--ink)]">
                  {deck.clausePuzzle.instruction}
                </p>
              </div>
              {pickedTiles.length > 0 && (
                <button
                  type="button"
                  onClick={() => setPickedTiles([])}
                  className="inline-flex items-center gap-1 rounded-xl border border-[var(--line)] bg-[var(--surface)] px-2.5 py-1 text-[11px] font-bold text-[var(--ink2)] hover:text-[var(--ink)]"
                >
                  <RotateCcw className="h-3 w-3" />
                  Zurücksetzen
                </button>
              )}
            </div>

            {/* Assembly Tray */}
            <div
              className={`min-h-[54px] rounded-xl border-2 border-dashed p-3 text-xs transition ${
                isPuzzleComplete
                  ? isPuzzleCorrect
                    ? 'border-[var(--ok)] bg-[var(--ok-bg)]'
                    : 'border-[var(--coral)] bg-[var(--coral-soft)]'
                  : 'border-[var(--line)] bg-[var(--surface)]'
              }`}
            >
              {pickedTiles.length === 0 ? (
                <span className="text-[var(--ink2)]">
                  Tippe die Bausteine unten in der richtigen Reihenfolge an…
                </span>
              ) : (
                <div className="flex flex-wrap items-center gap-1.5">
                  {pickedTiles.map((tileIdx, slotPos) => (
                    <button
                      key={`${tileIdx}-${slotPos}`}
                      type="button"
                      onClick={() =>
                        setPickedTiles((prev) => prev.filter((_, p) => p !== slotPos))
                      }
                      className="rounded-lg border border-[var(--accent)] bg-[var(--accent-soft)] px-2.5 py-1 text-xs font-extrabold text-[var(--accent-ink)]"
                    >
                      {scrambledTiles[tileIdx]}
                    </button>
                  ))}
                </div>
              )}
            </div>

            {/* Available Scrambled Tiles */}
            <div className="flex flex-wrap gap-2">
              {scrambledTiles.map((tile, idx) => {
                const used = pickedTiles.includes(idx);
                return (
                  <button
                    key={`${tile}-${idx}`}
                    type="button"
                    disabled={used}
                    onClick={() => setPickedTiles((prev) => [...prev, idx])}
                    className={`rounded-xl border px-3 py-1.5 text-xs font-bold transition ${
                      used
                        ? 'border-[var(--line)] bg-[var(--surface)] opacity-35'
                        : 'border-[var(--line)] bg-[var(--surface)] text-[var(--ink)] hover:border-[var(--accent)]'
                    }`}
                  >
                    {tile}
                  </button>
                );
              })}
            </div>

            {isPuzzleComplete && (
              <div className="c1-animate-enter space-y-1.5 rounded-xl border border-[var(--line)] bg-[var(--surface)] p-3 text-xs">
                <div className="flex items-center justify-between gap-2">
                  <span
                    className={`font-extrabold ${
                      isPuzzleCorrect ? 'text-[var(--ok)]' : 'text-[var(--coral)]'
                    }`}
                  >
                    {isPuzzleCorrect
                      ? '✓ Exakte C1-Verbklammer!'
                      : '✕ Fast – prüfe Verbposition nach dem Komma:'}
                  </span>
                  <button
                    type="button"
                    onClick={() =>
                      speakPhrase(
                        isPuzzleCorrect
                          ? assembledSentence
                          : deck.clausePuzzle.targetOrder.join(' '),
                        `puzzle-tts-${unitId}`,
                      )
                    }
                    className="inline-flex items-center gap-1 text-[11px] font-bold text-[var(--accent-ink)]"
                  >
                    <Volume2 className="h-3.5 w-3.5" />
                    Anhören
                  </button>
                </div>
                {!isPuzzleCorrect && (
                  <p className="font-bold text-[var(--ink)]">
                    Ziel: {deck.clausePuzzle.targetOrder.join(' ')}
                  </p>
                )}
                <p className="text-[11px] leading-relaxed text-[var(--ink2)]">
                  {deck.clausePuzzle.syntaxRule}
                </p>
              </div>
            )}
          </div>
        </div>
      )}

      {/* TAB 4: SENTENCE SURGERY */}
      {activeTab === 'SURGERY' && (
        <ImprintSentenceSurgeryCard
          label={deck.surgery.label}
          before={deck.surgery.before}
          diagnosis={deck.surgery.diagnosis}
          after={deck.surgery.after}
          steps={deck.surgery.steps}
          activeStep={surgeryStep}
          onStepChange={setSurgeryStep}
        />
      )}
    </section>
  );
};

export const ImprintSentenceSurgeryCard: React.FC<{
  label: string;
  before: string;
  diagnosis: string;
  after: string;
  steps?: string[];
  activeStep?: 0 | 1 | 2;
  onStepChange?: (s: 0 | 1 | 2) => void;
}> = ({ label, before, diagnosis, after, steps = [], activeStep, onStepChange }) => {
  const [internalStep, setInternalStep] = useState<0 | 1 | 2>(0);
  const step = activeStep ?? internalStep;
  const setStep = onStepChange ?? setInternalStep;

  return (
    <div className="c1-animate-enter space-y-3 rounded-2xl border border-[var(--line)] bg-[var(--surface2)] p-4">
      <div className="flex flex-wrap items-center justify-between gap-2">
        <span className="inline-flex items-center gap-1.5 text-[10px] font-extrabold uppercase tracking-wider text-[var(--accent-ink)]">
          <Scissors className="h-3.5 w-3.5" />
          Satz-Chirurgie · {label}
        </span>

        <div className="flex gap-1 rounded-xl border border-[var(--line)] bg-[var(--surface)] p-0.5 text-[11px]">
          {(
            [
              { idx: 0, text: '1. Rohsatz' },
              { idx: 1, text: '2. Diagnose' },
              { idx: 2, text: '3. C1-Umbau' },
            ] as const
          ).map((btn) => (
            <button
              key={btn.idx}
              type="button"
              onClick={() => setStep(btn.idx)}
              className={`rounded-lg px-2.5 py-1 font-bold transition ${
                step === btn.idx
                  ? 'bg-[var(--accent)] text-[var(--on-accent)]'
                  : 'text-[var(--ink2)] hover:text-[var(--ink)]'
              }`}
            >
              {btn.text}
            </button>
          ))}
        </div>
      </div>

      {/* Visual Morph Box */}
      <div className="grid gap-2.5 md:grid-cols-2">
        <div
          className={`rounded-xl border p-3 text-xs transition-all ${
            step === 0
              ? 'border-[var(--coral)] bg-[var(--coral-soft)]'
              : 'border-[var(--line)] bg-[var(--surface)] opacity-75'
          }`}
        >
          <p className="text-[10px] font-extrabold uppercase tracking-wider text-[var(--coral)]">
            Ausgangssatz (Stolperfalle)
          </p>
          <p className={`mt-1 leading-relaxed text-[var(--ink)] ${step === 2 ? 'line-through opacity-70' : 'font-semibold'}`}>
            „{before}“
          </p>
        </div>

        <div
          className={`rounded-xl border p-3 text-xs transition-all ${
            step === 2
              ? 'border-[var(--ok)] bg-[var(--ok-bg)] shadow-xs'
              : 'border-[var(--line)] bg-[var(--surface)]'
          }`}
        >
          <p className="text-[10px] font-extrabold uppercase tracking-wider text-[var(--ok)]">
            C1-Zielstruktur
          </p>
          <p className="mt-1 font-bold leading-relaxed text-[var(--ink)]">
            {step < 2 ? 'Tippe „3. C1-Umbau“, um die Umformung zu sehen…' : `„${after}“`}
          </p>
        </div>
      </div>

      {step >= 1 && (
        <div className="c1-animate-enter space-y-1.5 rounded-xl border border-[var(--line)] bg-[var(--surface)] p-3 text-xs">
          <p className="font-extrabold text-[var(--violet)]">
            Befund: <span className="font-semibold text-[var(--ink)]">{diagnosis}</span>
          </p>
          {steps.length > 0 && (
            <div className="flex flex-wrap gap-1.5 pt-1">
              {steps.map((s, idx) => (
                <span
                  key={idx}
                  className="rounded-lg border border-[var(--accent)]/35 bg-[var(--accent-soft)] px-2.5 py-1 text-[11px] font-bold text-[var(--accent-ink)]"
                >
                  {idx + 1}. {s}
                </span>
              ))}
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export const VocabCollocationWebSvg: React.FC<{
  card: VocabularySrsItem;
  draftText: string;
}> = ({ card, draftText }) => {
  const tokens = useMemo(
    () =>
      card.germanCollocation
        .replace(/[.,!?„“"]/g, '')
        .split(/\s+/)
        .filter((w) => w.length >= 3),
    [card.germanCollocation],
  );
  const lowerDraft = draftText.toLowerCase();
  const matchedTokens = tokens.filter((t) => lowerDraft.includes(t.toLowerCase()));

  const regLower = card.register.toLowerCase();
  const registerLevel = regLower.includes('form') || regLower.includes('schrift')
    ? 2
    : regLower.includes('alltag') || regLower.includes('umgang')
      ? 0
      : 1;

  const coreTitle = clampLabel(card.germanCollocation, 28);
  const catLabel = clampLabel(card.category, 18);
  const regLabel = clampLabel(card.register, 18);
  const intervalLabel = `Intervall: ${card.intervalDays}d`;

  return (
    <div className="c1-animate-enter space-y-3 rounded-2xl border border-white/15 bg-[var(--hero)] p-4 text-[var(--on-hero)]">
      <div className="flex flex-wrap items-center justify-between gap-2 text-[11px]">
        <span className="font-extrabold uppercase tracking-wider text-[var(--accent)]">
          Kollokations-Netz & Register
        </span>
        <span className="text-[var(--on-hero2)]">
          Aktive Treffer im Entwurf: {matchedTokens.length}/{Math.max(1, tokens.length)}
        </span>
      </div>

      <svg
        viewBox="0 0 520 130"
        className="h-32 w-full"
        role="img"
        aria-label={`Wortnetz für ${card.germanCollocation}`}
      >
        <line x1="260" y1="65" x2="96" y2="30" stroke="var(--sky)" strokeWidth="2" className="c1-svg-flow" />
        <line x1="260" y1="65" x2="424" y2="30" stroke="var(--violet)" strokeWidth="2" className="c1-svg-flow" />
        <line x1="260" y1="65" x2="96" y2="102" stroke="var(--accent)" strokeWidth="2" className="c1-svg-flow" />
        <line x1="260" y1="65" x2="424" y2="102" stroke="var(--ok)" strokeWidth="2" className="c1-svg-flow" />

        {/* Surrounding Nodes */}
        {[
          { cx: 96, cy: 30, tag: 'KATEGORIE', val: catLabel, stroke: 'var(--sky)' },
          { cx: 424, cy: 30, tag: 'REGISTER', val: regLabel, stroke: 'var(--violet)' },
          { cx: 96, cy: 102, tag: 'SM-2 STATUS', val: intervalLabel, stroke: 'var(--accent)' },
          {
            cx: 424,
            cy: 102,
            tag: 'ABGLEICH',
            val: matchedTokens.length > 0 ? 'Im Satz genutzt ✓' : 'Satz bilden…',
            stroke: matchedTokens.length > 0 ? 'var(--ok)' : 'rgba(255,255,255,0.35)',
          },
        ].map((node) => (
          <g key={node.tag}>
            <rect
              x={node.cx - 78}
              y={node.cy - 20}
              width="156"
              height="40"
              rx="10"
              fill="rgba(255,255,255,0.08)"
              stroke={node.stroke}
              strokeWidth="1.6"
            />
            <text x={node.cx} y={node.cy - 4} textAnchor="middle" fill={node.stroke} fontSize="8" fontWeight="900">
              {node.tag}
            </text>
            <text x={node.cx} y={node.cy + 11} textAnchor="middle" fill="#FFFFFF" fontSize="10.5" fontWeight="700">
              {node.val}
            </text>
          </g>
        ))}

        {/* Center Collocation Node */}
        <rect
          x="165"
          y="40"
          width="190"
          height="50"
          rx="14"
          fill="var(--accent)"
          stroke="#FFFFFF"
          strokeWidth="2"
        />
        <text x="260" y="61" textAnchor="middle" fill="var(--on-accent)" fontSize="8.5" fontWeight="900">
          KERN-KOLLOKATION
        </text>
        <text x="260" y="77" textAnchor="middle" fill="var(--on-accent)" fontSize="11.5" fontWeight="900">
          {coreTitle}
        </text>
      </svg>

      {/* Register Spectrum Indicator */}
      <div className="grid grid-cols-3 gap-1.5 text-center text-[10px] font-bold">
        {['Umgangssprachlich', 'Sachlich · Standard', 'C1 Formell / Schrift'].map(
          (lbl, idx) => (
            <div
              key={lbl}
              className={`rounded-lg border px-2 py-1 ${
                registerLevel === idx
                  ? 'border-[var(--accent)] bg-[var(--accent)]/20 text-[var(--accent)]'
                  : 'border-white/10 bg-white/5 text-[var(--on-hero2)]'
              }`}
            >
              {lbl}
            </div>
          ),
        )}
      </div>
    </div>
  );
};
