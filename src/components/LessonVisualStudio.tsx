import React, { useEffect, useMemo, useState } from 'react';
import {
  CheckCircle2,
  ChevronDown,
  ChevronUp,
  Cpu,
  Layers,
  MessageCircle,
  Plus,
  Sparkles,
  Trophy,
  Volume2,
} from 'lucide-react';
import { CONTEXT_LABELS } from '../data/learningData';
import { lessonVisualBlueprint, type LessonGraphicType } from '../engines/lessonVisualParser';
import { speechService } from '../services/speechService';
import type { Attempt, ContextTrack, CurriculumUnit, ProgrammeLanguage } from '../types';
import {
  ContextIllustrationFrame,
  ImprintDialogueStage,
  ImprintGrammarBoard,
  ImprintPatternFlipDeck,
  ImprintStoryDeck,
} from './ImprintLessonVisuals';

export const LessonVisualGraphic: React.FC<{
  graphic: LessonGraphicType;
  activeStep: number;
  compact?: boolean;
}> = ({ graphic, activeStep, compact = false }) => {
  const step = Math.max(0, Math.min(2, activeStep));
  const progress = step / 2;

  return (
    <div
      className={`relative overflow-hidden rounded-2xl border border-[var(--line)] bg-[var(--surface2)] ${
        compact ? 'h-[72px] w-[104px] shrink-0' : 'h-36 w-full'
      }`}
    >
      <svg
        viewBox="0 0 240 120"
        className="h-full w-full"
        aria-hidden="true"
      >
        <defs>
          <marker
            id="c1ArrowHead"
            markerWidth="7"
            markerHeight="7"
            refX="5"
            refY="3.5"
            orient="auto"
          >
            <polygon points="0 0, 7 3.5, 0 7" fill="var(--accent)" />
          </marker>
        </defs>

        {graphic === 'TARGET' && (
          <g>
            <circle cx="172" cy="60" r="36" fill="var(--violet-soft)" stroke="var(--violet)" strokeOpacity="0.45" strokeWidth="1.5" />
            <circle cx="172" cy="60" r="24" fill="var(--surface)" stroke="var(--accent)" strokeOpacity="0.5" strokeWidth="1.5" />
            <circle cx="172" cy="60" r={10 + progress * 4} fill="var(--accent)" className="transition-all duration-300" />
            <path
              d="M 28 92 L 154 62"
              fill="none"
              stroke="var(--accent)"
              strokeWidth="3"
              strokeLinecap="round"
              markerEnd="url(#c1ArrowHead)"
              className="c1-svg-flow"
            />
          </g>
        )}

        {graphic === 'GRID' && (
          <g>
            {[
              { x: 74, y: 34, i: 0 },
              { x: 166, y: 34, i: 1 },
              { x: 74, y: 86, i: 2 },
              { x: 166, y: 86, i: 3 },
            ].map((pt) => (
              <g key={pt.i}>
                <line
                  x1="120"
                  y1="60"
                  x2={pt.x}
                  y2={pt.y}
                  stroke={pt.i <= step ? 'var(--accent)' : 'var(--line)'}
                  strokeWidth="2.5"
                  className={pt.i <= step ? 'c1-svg-flow' : ''}
                />
                <circle
                  cx={pt.x}
                  cy={pt.y}
                  r="14"
                  fill={pt.i % 2 === 0 ? 'var(--accent-soft)' : 'var(--violet-soft)'}
                  stroke="var(--accent)"
                  strokeOpacity={pt.i <= step ? 0.9 : 0.3}
                  strokeWidth="1.8"
                />
                <circle cx={pt.x} cy={pt.y} r="4.5" fill="var(--accent)" />
              </g>
            ))}
            <circle cx="120" cy="60" r="14" fill="var(--surface)" stroke="var(--accent)" strokeWidth="2.2" />
            <circle cx="120" cy="60" r="5" fill="var(--accent)" className="c1-float-slow" />
          </g>
        )}

        {graphic === 'PROCESS' && (
          <g>
            <line
              x1="46"
              y1="60"
              x2="112"
              y2="60"
              stroke={progress >= 0.5 ? 'var(--accent)' : 'var(--ink2)'}
              strokeOpacity={progress >= 0.5 ? 1 : 0.35}
              strokeWidth="3"
              markerEnd="url(#c1ArrowHead)"
              className={progress >= 0.5 ? 'c1-svg-flow' : ''}
            />
            <line
              x1="126"
              y1="60"
              x2="188"
              y2="60"
              stroke={progress >= 1 ? 'var(--accent)' : 'var(--ink2)'}
              strokeOpacity={progress >= 1 ? 1 : 0.35}
              strokeWidth="3"
              markerEnd="url(#c1ArrowHead)"
              className={progress >= 1 ? 'c1-svg-flow' : ''}
            />
            {[46, 120, 194].map((cx, idx) => {
              const active = idx <= step;
              return (
                <g key={cx}>
                  <circle
                    cx={cx}
                    cy="60"
                    r={idx === step ? 16 : 13}
                    fill={active ? 'var(--accent-soft)' : 'var(--surface)'}
                    stroke={active ? 'var(--accent)' : 'var(--line)'}
                    strokeWidth="2"
                  />
                  <circle cx={cx} cy="60" r="5" fill={active ? 'var(--accent)' : 'var(--ink2)'} />
                </g>
              );
            })}
          </g>
        )}

        {graphic === 'ARGUMENT' && (
          <g>
            {[28, 60, 92].map((y, idx) => (
              <g key={y}>
                <path
                  d={`M 68 60 Q 116 ${60 + (y - 60) * 0.45} 166 ${y}`}
                  fill="none"
                  stroke={idx === step ? 'var(--accent)' : 'var(--ink2)'}
                  strokeOpacity={idx === step ? 1 : 0.35}
                  strokeWidth="2.5"
                  className={idx === step ? 'c1-svg-flow' : ''}
                />
                <circle
                  cx="176"
                  cy={y}
                  r={idx === step ? 12 : 10}
                  fill={idx === 1 ? 'var(--violet-soft)' : 'var(--accent-soft)'}
                  stroke="var(--accent)"
                  strokeOpacity={idx === step ? 1 : 0.35}
                  strokeWidth="1.8"
                />
              </g>
            ))}
            <rect x="34" y="42" width="34" height="36" rx="10" fill="var(--accent-soft)" stroke="var(--accent)" strokeWidth="2" />
            <circle cx="51" cy="60" r="5" fill="var(--accent)" />
          </g>
        )}

        {graphic === 'EVIDENCE' && (
          <g>
            <line x1="42" y1="22" x2="42" y2="96" stroke="var(--ink2)" strokeOpacity="0.45" strokeWidth="2" />
            <line x1="42" y1="96" x2="206" y2="96" stroke="var(--ink2)" strokeOpacity="0.45" strokeWidth="2" />
            {[
              { x: 74, h: 42 },
              { x: 124, h: 66 },
              { x: 174, h: 52 },
            ].map((bar, idx) => (
              <g key={bar.x}>
                <rect
                  x={bar.x - 14}
                  y={96 - bar.h}
                  width="28"
                  height={bar.h}
                  rx="7"
                  fill={idx === step ? 'var(--accent-soft)' : 'var(--violet-soft)'}
                  stroke="var(--accent)"
                  strokeOpacity={idx === step ? 1 : 0.35}
                  strokeWidth="1.8"
                />
                <line
                  x1={bar.x}
                  y1={96 - bar.h - 7}
                  x2={bar.x}
                  y2={96 - bar.h + 7}
                  stroke="var(--accent)"
                  strokeWidth="2.2"
                />
              </g>
            ))}
            <line x1="58" y1="42" x2="196" y2="42" stroke="var(--accent)" strokeWidth="2" className="c1-svg-flow" />
          </g>
        )}

        {graphic === 'DIALOGUE' && (
          <g>
            <rect
              x="28"
              y="22"
              width="86"
              height="40"
              rx="12"
              fill="var(--accent-soft)"
              stroke="var(--accent)"
              strokeWidth={step === 0 ? 2.4 : 1.5}
            />
            <rect
              x="126"
              y="58"
              width="86"
              height="40"
              rx="12"
              fill="var(--violet-soft)"
              stroke="var(--violet)"
              strokeWidth={step >= 1 ? 2.4 : 1.5}
            />
            {[0, 1, 2].map((d) => (
              <g key={d}>
                <circle cx={56 + d * 15} cy="42" r="3" fill="var(--accent)" />
                <circle cx={154 + d * 15} cy="78" r="3" fill="var(--violet)" />
              </g>
            ))}
            <path
              d="M 114 48 L 128 68"
              fill="none"
              stroke="var(--accent)"
              strokeWidth="2.5"
              markerEnd="url(#c1ArrowHead)"
              className="c1-svg-flow"
            />
          </g>
        )}

        {graphic === 'BRIDGE' && (
          <g>
            <path
              d="M 52 80 C 82 14, 158 14, 188 80"
              fill="none"
              stroke="var(--accent)"
              strokeWidth="3.5"
              className="c1-svg-flow"
            />
            <rect x="36" y="76" width="32" height="26" rx="6" fill="var(--accent-soft)" stroke="var(--accent)" strokeWidth="2" />
            <rect x="172" y="76" width="32" height="26" rx="6" fill="var(--violet-soft)" stroke="var(--violet)" strokeWidth="2" />
            <circle cx={52 + 136 * progress} cy={80 - Math.sin(progress * Math.PI) * 44} r="6" fill="var(--accent)" className="transition-all duration-300" />
            <line x1="24" y1="102" x2="216" y2="102" stroke="var(--ink2)" strokeOpacity="0.4" strokeWidth="1.5" />
          </g>
        )}

        {graphic === 'FUNNEL' && (
          <g>
            <polygon
              points="40,22 200,22 140,74 140,90 100,90 100,74"
              fill="var(--accent-soft)"
              stroke="var(--accent)"
              strokeWidth="2"
            />
            <line x1="64" y1="38" x2="176" y2="38" stroke="var(--accent)" strokeWidth="2.5" className="c1-svg-flow" />
            <circle cx="120" cy="102" r={6 + progress * 3} fill="var(--violet)" className="c1-float-slow" />
          </g>
        )}

        {graphic === 'BALANCE' && (
          <g>
            <line
              x1="54"
              y1={42 + (step - 1) * 6}
              x2="186"
              y2={42 - (step - 1) * 6}
              stroke="var(--accent)"
              strokeWidth="3.5"
              strokeLinecap="round"
            />
            <line x1="120" y1="42" x2="120" y2="92" stroke="var(--ink2)" strokeWidth="3" />
            <polygon points="120,76 102,102 138,102" fill="var(--violet-soft)" stroke="var(--violet)" strokeWidth="2" />
            <path d="M 52 72 A 16 12 0 0 0 84 72" fill="none" stroke="var(--accent)" strokeWidth="2.5" />
            <path d="M 156 72 A 16 12 0 0 0 188 72" fill="none" stroke="var(--violet)" strokeWidth="2.5" />
          </g>
        )}

        {graphic === 'CYCLE' && (
          <g>
            <path d="M 120 22 A 42 36 0 0 1 174 80" fill="none" stroke="var(--accent)" strokeWidth="2.8" markerEnd="url(#c1ArrowHead)" className="c1-svg-flow" />
            <path d="M 166 86 A 42 36 0 0 1 66 82" fill="none" stroke="var(--sky)" strokeWidth="2.8" className="c1-svg-flow" />
            <path d="M 66 76 A 42 36 0 0 1 112 24" fill="none" stroke="var(--violet)" strokeWidth="2.8" className="c1-svg-flow" />
            {[
              { x: 120, y: 22, idx: 0 },
              { x: 174, y: 82, idx: 1 },
              { x: 66, y: 82, idx: 2 },
            ].map((pt) => (
              <circle
                key={pt.idx}
                cx={pt.x}
                cy={pt.y}
                r={pt.idx === step ? 12 : 9}
                fill={pt.idx === step ? 'var(--accent)' : 'var(--accent-soft)'}
                stroke="var(--accent)"
                strokeWidth="2"
              />
            ))}
          </g>
        )}
      </svg>
    </div>
  );
};

export const LessonStepVisual: React.FC<{
  unit: CurriculumUnit;
  phase: number;
  language?: ProgrammeLanguage;
}> = ({ unit, phase, language = 'DE' }) => {
  const blueprint = lessonVisualBlueprint(unit.id);
  const stage = Math.max(0, Math.min(2, phase));
  const stageTitle =
    stage === 0
      ? language === 'EN'
        ? 'Recall first'
        : 'Erst erinnern'
      : stage === 1
        ? language === 'EN'
          ? 'Notice the pattern'
          : 'Muster erkennen'
        : language === 'EN'
          ? 'Apply it yourself'
          : 'Eigenständig anwenden';

  return (
    <div className="c1-animate-enter flex items-center gap-3.5 rounded-2xl border border-[var(--line)] bg-[var(--accent-soft)]/55 p-3">
      <LessonVisualGraphic graphic={blueprint.graphic} activeStep={stage} compact />
      <div className="min-w-0 flex-1 space-y-0.5">
        <p className="text-[10px] font-extrabold uppercase tracking-wider text-[var(--accent-ink)]">
          {language === 'EN' ? 'THE IDEA IN ONE IMAGE' : 'DIE IDEE ALS BILD'}
        </p>
        <p className="text-sm font-extrabold text-[var(--ink)]">{stageTitle}</p>
        <p className="truncate text-xs text-[var(--ink2)]">
          {language === 'EN' ? blueprint.captionEn : blueprint.captionDe}
        </p>
      </div>
    </div>
  );
};

export const LessonJourneyVisual: React.FC<{
  unit: CurriculumUnit;
  completedTaskIds: Set<string>;
  language?: ProgrammeLanguage;
}> = ({ unit, completedTaskIds, language = 'DE' }) => {
  const steps = useMemo(
    () => [
      {
        title: language === 'EN' ? 'Retrieve' : 'Abrufen',
        description:
          language === 'EN'
            ? 'Recall what you know first, then note what remains unclear.'
            : 'Erst aus dem Gedächtnis formulieren; offene Fragen notieren.',
        taskIds: ['retrieval'],
      },
      {
        title: language === 'EN' ? 'Understand' : 'Verstehen',
        description:
          language === 'EN'
            ? 'Read, listen and check the main idea against the material.'
            : 'Lesen, hören und die Kernaussage am Text prüfen.',
        taskIds: ['reading', 'listening', 'quiz'],
      },
      {
        title: language === 'EN' ? 'Apply' : 'Anwenden',
        description:
          language === 'EN'
            ? 'Try the language in your own writing and in a dialogue.'
            : 'Sprachmittel in einer eigenen Formulierung und im Dialog erproben.',
        taskIds: ['controlled', 'speaking', 'interaction', 'writing'],
      },
      {
        title: language === 'EN' ? 'Transfer' : 'Übertragen',
        description:
          language === 'EN'
            ? 'Use the idea independently in a new context.'
            : 'Die Idee in einem neuen Kontext selbstständig nutzen.',
        taskIds: ['transfer'],
      },
    ],
    [language],
  );

  const firstPending = steps.findIndex(
    (s) => !s.taskIds.some((id) => completedTaskIds.has(id)),
  );
  const [selectedStep, setSelectedStep] = useState(
    firstPending >= 0 ? firstPending : 0,
  );

  useEffect(() => {
    setSelectedStep(firstPending >= 0 ? firstPending : 0);
  }, [unit.id, firstPending]);

  const completedSteps = steps.filter((s) =>
    s.taskIds.some((id) => completedTaskIds.has(id)),
  ).length;
  const pct = Math.round((completedSteps / steps.length) * 100);
  const selected = steps[Math.min(selectedStep, steps.length - 1)];

  return (
    <div className="c1-animate-enter space-y-3.5 rounded-3xl border border-[var(--line)] bg-[var(--accent-soft)]/50 p-4 shadow-sm">
      <div className="flex items-center justify-between gap-2">
        <div>
          <p className="text-[10px] font-extrabold uppercase tracking-wider text-[var(--accent-ink)]">
            {language === 'EN' ? 'YOUR LEARNING PATH' : 'DEIN LERNWEG'}
          </p>
          <p className="text-xs text-[var(--ink2)]">
            {language === 'EN'
              ? `${completedSteps} of ${steps.length} steps practised`
              : `${completedSteps} von ${steps.length} Stationen bearbeitet`}
          </p>
        </div>
        <span className="text-base font-extrabold tabular-nums text-[var(--accent-ink)]">
          {pct}%
        </span>
      </div>

      <div className="h-2 w-full overflow-hidden rounded-full bg-[var(--surface)]/80">
        <div
          className="h-full rounded-full bg-[var(--accent)] transition-all duration-300"
          style={{ width: `${pct}%` }}
        />
      </div>

      <div className="flex items-center gap-1.5">
        {steps.map((step, index) => {
          const done = step.taskIds.some((id) => completedTaskIds.has(id));
          const isSelected = selectedStep === index;
          return (
            <React.Fragment key={step.title}>
              <button
                type="button"
                onClick={() => setSelectedStep(index)}
                className={`flex h-9 w-9 shrink-0 items-center justify-center rounded-full text-xs font-extrabold transition ${
                  done
                    ? 'bg-[var(--accent)] text-[var(--on-accent)]'
                    : isSelected
                      ? 'border-2 border-[var(--accent)] bg-[var(--surface)] text-[var(--accent-ink)]'
                      : 'border border-[var(--line)] bg-[var(--surface)]/70 text-[var(--ink2)]'
                }`}
              >
                {done ? '✓' : index + 1}
              </button>
              {index < steps.length - 1 && (
                <div
                  className={`h-1 flex-1 rounded-full transition-colors duration-300 ${
                    done ? 'bg-[var(--accent)]' : 'bg-[var(--accent)]/25'
                  }`}
                />
              )}
            </React.Fragment>
          );
        })}
      </div>

      <div className="grid grid-cols-4 gap-1 text-center">
        {steps.map((step, index) => (
          <button
            key={step.title}
            type="button"
            onClick={() => setSelectedStep(index)}
            className={`truncate text-[11px] transition ${
              selectedStep === index
                ? 'font-extrabold text-[var(--accent-ink)]'
                : 'font-medium text-[var(--ink2)]'
            }`}
          >
            {step.title}
          </button>
        ))}
      </div>

      <div className="rounded-2xl border border-[var(--line)] bg-[var(--surface)]/90 p-3 text-xs">
        <p className="font-extrabold text-[var(--ink)]">{selected.title}</p>
        <p className="mt-0.5 text-[var(--ink2)]">{selected.description}</p>
      </div>
    </div>
  );
};

export const LessonConceptMap: React.FC<{
  unit: CurriculumUnit;
  context: ContextTrack;
  expressions: string[];
  selectedExpressionIndex: number;
  language?: ProgrammeLanguage;
}> = ({
  unit,
  context,
  expressions,
  selectedExpressionIndex,
  language = 'DE',
}) => {
  const [activeStep, setActiveStep] = useState(1);
  const [showObjectives, setShowObjectives] = useState(false);

  useEffect(() => {
    setActiveStep(1);
  }, [unit.id]);

  const blueprint = lessonVisualBlueprint(unit.id);
  const expression =
    expressions[selectedExpressionIndex] ?? expressions[0] ?? '';
  const parts = expression.split(/\s+[–—]\s+/, 2);
  const phrase =
    parts[0]?.trim() ||
    (language === 'EN' ? 'No language move yet' : 'Noch kein Sprachmittel');
  const purpose = parts[1]?.trim() || unit.objectives[0] || '';

  const steps = [
    {
      label: 'KONTEXT',
      title: CONTEXT_LABELS[context],
      body: unit.contexts[context] || 'Kein Kontext verfasst',
      tone: 'bg-[var(--accent-soft)]/60',
    },
    {
      label: 'SPRACHZUG',
      title: phrase,
      body: '',
      tone: 'bg-[var(--violet-soft)]',
    },
    {
      label: 'WIRKUNG',
      title: 'Was sich dadurch ändert',
      body: purpose,
      tone: 'bg-[var(--surface2)]',
    },
  ];

  const current = steps[activeStep] ?? steps[1];

  return (
    <div className="space-y-3">
      <p className="text-[11px] font-extrabold uppercase tracking-wider text-[var(--accent-ink)]">
        {language === 'EN' ? 'VISUAL MODEL' : 'VISUELLE IDEE'}
      </p>

      <LessonVisualGraphic graphic={blueprint.graphic} activeStep={activeStep} />

      <p className="text-sm font-extrabold text-[var(--ink)]">
        {language === 'EN' ? blueprint.captionEn : blueprint.captionDe}
      </p>

      <div>
        <p className="text-[10px] font-extrabold uppercase tracking-wider text-[var(--accent-ink)]">
          {language === 'EN' ? 'WHAT YOU WILL WORK ON' : 'WORAN DU ARBEITEST'}
        </p>
        {unit.objectives[0] && (
          <p className="mt-0.5 text-xs text-[var(--ink2)]">{unit.objectives[0]}</p>
        )}
      </div>

      {/* 3-Node Stepper */}
      <div className="flex items-center gap-2">
        {steps.map((s, idx) => (
          <React.Fragment key={s.label}>
            <button
              type="button"
              onClick={() => setActiveStep(idx)}
              className={`flex flex-col items-center gap-1 rounded-2xl px-2 py-1 transition ${
                activeStep === idx ? 'opacity-100' : 'opacity-70 hover:opacity-100'
              }`}
            >
              <span
                className={`flex h-9 w-9 items-center justify-center rounded-full text-xs font-extrabold transition ${
                  activeStep === idx
                    ? 'bg-[var(--accent)] text-[var(--on-accent)] shadow-sm'
                    : 'border border-[var(--line)] bg-[var(--surface2)] text-[var(--ink)]'
                }`}
              >
                {idx + 1}
              </span>
              <span className="text-[10px] font-extrabold tracking-wide text-[var(--ink2)]">
                {s.label}
              </span>
            </button>
            {idx < steps.length - 1 && (
              <div
                className={`mb-4 h-0.5 flex-1 rounded-full ${
                  activeStep > idx ? 'bg-[var(--accent)]' : 'bg-[var(--line)]'
                }`}
              />
            )}
          </React.Fragment>
        ))}
      </div>

      <div
        className={`c1-animate-enter rounded-2xl border border-[var(--line)] p-4 ${current.tone}`}
      >
        <p className="text-sm font-extrabold text-[var(--ink)]">{current.title}</p>
        {current.body && (
          <p className="mt-1 text-xs leading-relaxed text-[var(--ink2)]">
            {current.body}
          </p>
        )}
      </div>

      {unit.objectives.length > 1 && (
        <div>
          <button
            type="button"
            onClick={() => setShowObjectives((v) => !v)}
            className="inline-flex items-center gap-1 text-xs font-bold text-[var(--accent-ink)]"
          >
            {showObjectives ? (
              <>
                <ChevronUp className="h-3.5 w-3.5" />
                {language === 'EN' ? 'Fewer learning goals' : 'Weniger Lernziele'}
              </>
            ) : (
              <>
                <ChevronDown className="h-3.5 w-3.5" />
                {language === 'EN' ? 'Show all learning goals' : 'Alle Lernziele anzeigen'}
              </>
            )}
          </button>
          {showObjectives && (
            <ul className="mt-2 space-y-1.5 rounded-2xl border border-[var(--line)] bg-[var(--surface2)] p-3 text-xs text-[var(--ink2)]">
              {unit.objectives.slice(1).map((obj, idx) => (
                <li key={idx} className="flex gap-2">
                  <span className="font-extrabold text-[var(--accent-ink)]">
                    {idx + 2}
                  </span>
                  <span>{obj}</span>
                </li>
              ))}
            </ul>
          )}
        </div>
      )}
    </div>
  );
};

export const ReadingChunkStudio: React.FC<{
  unitId: string;
  unitTitle?: string;
  passage: string;
  language?: ProgrammeLanguage;
}> = ({ unitId, unitTitle = 'Lektion', passage, language = 'DE' }) => (
  <ImprintStoryDeck
    unitId={unitId}
    unitTitle={unitTitle}
    passage={passage}
    language={language}
  />
);

export const ListeningDialogueStudio: React.FC<{
  unitId: string;
  transcript: string;
  language?: ProgrammeLanguage;
}> = ({ unitId, transcript, language = 'DE' }) => (
  <ImprintDialogueStage
    unitId={unitId}
    transcript={transcript}
    language={language}
  />
);

export const LanguagePatternStudio: React.FC<{
  unitId: string;
  expressions: string[];
  grammar: string;
  selectedIndex: number;
  onSelectedIndexChange: (idx: number) => void;
  language?: ProgrammeLanguage;
}> = ({
  unitId,
  expressions,
  grammar,
  selectedIndex,
  onSelectedIndexChange,
  language = 'DE',
}) => {
  const [grammarOpen, setGrammarOpen] = useState(true);

  useEffect(() => {
    setGrammarOpen(true);
  }, [unitId]);

  if (expressions.length === 0) {
    return (
      <ImprintGrammarBoard unitId={unitId} grammar={grammar} language={language} />
    );
  }

  return (
    <div className="space-y-4">
      <ImprintPatternFlipDeck
        unitId={unitId}
        expressions={expressions}
        selectedIndex={selectedIndex}
        onSelectedIndexChange={onSelectedIndexChange}
        language={language}
      />

      <div className="space-y-2">
        <div className="flex flex-wrap items-center justify-between gap-2">
          <span className="text-[11px] font-extrabold uppercase tracking-wider text-[var(--accent-ink)]">
            {language === 'EN' ? 'GRAMMAR IN CONTEXT' : 'GRAMMATIK ALS SCHAUBILD'}
          </span>
          <button
            type="button"
            onClick={() => setGrammarOpen((v) => !v)}
            className="inline-flex items-center gap-1 text-xs font-bold text-[var(--accent-ink)]"
          >
            {grammarOpen ? (
              <>
                <ChevronUp className="h-3.5 w-3.5" />
                {language === 'EN' ? 'Hide diagram' : 'Schaubild ausblenden'}
              </>
            ) : (
              <>
                <ChevronDown className="h-3.5 w-3.5" />
                {language === 'EN' ? 'Show grammar diagram' : 'Grammatik-Schaubild zeigen'}
              </>
            )}
          </button>
        </div>
        {grammarOpen && (
          <ImprintGrammarBoard
            unitId={unitId}
            grammar={grammar}
            language={language}
          />
        )}
      </div>
    </div>
  );
};

interface ContextSceneBannerProps {
  unit: CurriculumUnit;
  selectedContext: ContextTrack;
  unitAttempts: Attempt[];
  isMastered: boolean;
}

/**
 * Interactive animated SVG vector scene matching the selected real-world context track
 * plus an honest 4-pillar Mastery Evidence progress diagram.
 */
export const ContextSceneBanner: React.FC<ContextSceneBannerProps> = ({
  unit,
  selectedContext,
  unitAttempts,
  isMastered,
}) => {
  const [visualMode, setVisualMode] = useState<'ARTWORK' | 'DIAGRAM'>('ARTWORK');

  const unassisted = useMemo(
    () =>
      unitAttempts.filter(
        (a) =>
          !a.assisted &&
          a.independentProduction &&
          (a.source === 'HUMAN_REVIEW' || Object.keys(a.scores).length > 0),
      ),
    [unitAttempts],
  );

  const distinctContexts = useMemo(
    () => new Set(unassisted.map((a) => a.context)).size,
    [unassisted],
  );
  const distinctDays = useMemo(
    () =>
      new Set(
        unassisted.map((a) => new Date(a.timestamp).toISOString().slice(0, 10)),
      ).size,
    [unassisted],
  );
  const hasHumanReview = useMemo(
    () =>
      unitAttempts.some((a) => {
        if (a.source !== 'HUMAN_REVIEW') return false;
        const vals = Object.values(a.scores).filter(
          (v): v is number => typeof v === 'number',
        );
        return vals.length > 0 && vals.every((v) => v >= 3);
      }),
    [unitAttempts],
  );
  const hasUnfamiliarTransfer = useMemo(
    () => unassisted.some((a) => a.unfamiliar && !a.assisted),
    [unassisted],
  );

  const pillars = [
    { label: '2+ Kontexte', done: distinctContexts >= 2 || isMastered },
    { label: '2+ UTC-Tage', done: distinctDays >= 2 || isMastered },
    { label: 'Transfer ≥ 7d', done: hasUnfamiliarTransfer || isMastered },
    { label: 'Rubrik ≥ 3/4', done: hasHumanReview || isMastered },
  ];

  const completedPillars = pillars.filter((p) => p.done).length;

  return (
    <div className="c1-animate-enter overflow-hidden rounded-3xl border border-[var(--line)] bg-[var(--hero)] text-[var(--on-hero)] shadow-lg">
      <div className="grid items-stretch gap-4 p-5 md:grid-cols-12">
        <div className="relative flex flex-col gap-2 md:col-span-5">
          {visualMode === 'ARTWORK' ? (
            <ContextIllustrationFrame
              context={selectedContext}
              caption={unit.title}
            />
          ) : (
            <div className="relative flex h-44 items-center justify-center overflow-hidden rounded-2xl border border-white/10 bg-black/20 p-3">
              {selectedContext === 'ENGINEERING' && (
            <svg
              viewBox="0 0 320 150"
              className="h-full w-full"
              role="img"
              aria-label="EV-Antriebsstrang Hochvolt-Diagramm"
            >
              <g stroke="rgba(255,255,255,0.08)" strokeWidth="1">
                <line x1="0" y1="38" x2="320" y2="38" />
                <line x1="0" y1="76" x2="320" y2="76" />
                <line x1="0" y1="114" x2="320" y2="114" />
              </g>
              <path
                d="M 52 75 L 135 75 L 155 48 L 220 48"
                fill="none"
                stroke="var(--accent)"
                strokeWidth="3"
                className="c1-svg-flow"
              />
              <path
                d="M 135 75 L 155 104 L 220 104"
                fill="none"
                stroke="var(--sky)"
                strokeWidth="2.5"
                className="c1-svg-flow"
              />
              <rect
                x="18"
                y="48"
                width="62"
                height="54"
                rx="10"
                fill="rgba(45,212,191,0.14)"
                stroke="var(--accent)"
                strokeWidth="2"
              />
              <text x="49" y="72" textAnchor="middle" fill="var(--on-hero)" fontSize="10" fontWeight="800">
                800V HV
              </text>
              <text x="49" y="86" textAnchor="middle" fill="var(--accent)" fontSize="8" fontWeight="700">
                Batterie
              </text>
              <rect
                x="114"
                y="56"
                width="46"
                height="38"
                rx="8"
                fill="rgba(255,255,255,0.08)"
                stroke="rgba(255,255,255,0.35)"
                strokeWidth="1.5"
              />
              <text x="137" y="78" textAnchor="middle" fill="var(--on-hero)" fontSize="9" fontWeight="700">
                MCU
              </text>
              <circle
                cx="254"
                cy="48"
                r="24"
                fill="rgba(45,212,191,0.16)"
                stroke="var(--accent)"
                strokeWidth="2"
                className="c1-float-slow"
              />
              <text x="254" y="51" textAnchor="middle" fill="var(--on-hero)" fontSize="9" fontWeight="800">
                E-Achs
              </text>
              <rect
                x="220"
                y="88"
                width="74"
                height="32"
                rx="8"
                fill="rgba(63,140,255,0.16)"
                stroke="var(--sky)"
                strokeWidth="1.5"
              />
              <text x="257" y="108" textAnchor="middle" fill="var(--on-hero)" fontSize="9" fontWeight="700">
                Thermomanag.
              </text>
            </svg>
          )}

          {selectedContext === 'COACHING' && (
            <svg
              viewBox="0 0 320 150"
              className="h-full w-full"
              role="img"
              aria-label="Fußball Taktikfeld Halbraum-Animation"
            >
              <rect x="12" y="10" width="296" height="130" rx="10" fill="rgba(28,138,85,0.22)" stroke="rgba(255,255,255,0.38)" strokeWidth="1.5" />
              <line x1="160" y1="10" x2="160" y2="140" stroke="rgba(255,255,255,0.28)" strokeWidth="1.5" />
              <circle cx="160" cy="75" r="24" fill="none" stroke="rgba(255,255,255,0.28)" strokeWidth="1.5" />
              <rect x="12" y="42" width="36" height="66" fill="none" stroke="rgba(255,255,255,0.3)" strokeWidth="1.5" />
              <rect x="272" y="42" width="36" height="66" fill="none" stroke="rgba(255,255,255,0.3)" strokeWidth="1.5" />
              <path d="M 85 102 Q 145 96 198 46" fill="none" stroke="var(--accent)" strokeWidth="2.8" className="c1-svg-flow" />
              <path d="M 198 46 L 265 65" fill="none" stroke="var(--ok)" strokeWidth="2.8" className="c1-svg-flow" />
              <circle cx="85" cy="102" r="8" fill="var(--accent)" />
              <text x="85" y="105" textAnchor="middle" fill="#08172B" fontSize="8" fontWeight="900">
                6
              </text>
              <circle cx="198" cy="46" r="8" fill="var(--sky)" className="c1-float-slow" />
              <text x="198" y="49" textAnchor="middle" fill="#FFFFFF" fontSize="8" fontWeight="900">
                10
              </text>
              <circle cx="265" cy="65" r="8" fill="var(--ok)" />
              <text x="265" y="68" textAnchor="middle" fill="#FFFFFF" fontSize="8" fontWeight="900">
                9
              </text>
            </svg>
          )}

          {selectedContext === 'SOCIAL' && (
            <svg
              viewBox="0 0 320 150"
              className="h-full w-full"
              role="img"
              aria-label="Sozialer Gesprächsfluss und Perspektivenwechsel"
            >
              <path d="M 68 78 C 120 26, 200 26, 252 78" fill="none" stroke="var(--accent)" strokeWidth="2.5" className="c1-svg-flow" />
              <path d="M 252 84 C 200 130, 120 130, 68 84" fill="none" stroke="var(--violet)" strokeWidth="2.5" className="c1-svg-flow" />
              <circle cx="64" cy="80" r="26" fill="rgba(45,212,191,0.16)" stroke="var(--accent)" strokeWidth="2" />
              <text x="64" y="78" textAnchor="middle" fill="var(--on-hero)" fontSize="9" fontWeight="800">
                Standpunkt
              </text>
              <text x="64" y="90" textAnchor="middle" fill="var(--accent)" fontSize="8">
                A
              </text>
              <circle cx="160" cy="78" r="22" fill="rgba(255,255,255,0.08)" stroke="rgba(255,255,255,0.4)" strokeWidth="1.5" className="c1-float-slow" />
              <text x="160" y="81" textAnchor="middle" fill="var(--on-hero)" fontSize="8" fontWeight="800">
                Nuance
              </text>
              <circle cx="256" cy="80" r="26" fill="rgba(161,150,255,0.18)" stroke="var(--violet)" strokeWidth="2" />
              <text x="256" y="78" textAnchor="middle" fill="var(--on-hero)" fontSize="9" fontWeight="800">
                Einwand
              </text>
              <text x="256" y="90" textAnchor="middle" fill="var(--violet)" fontSize="8">
                B
              </text>
            </svg>
          )}

          {selectedContext === 'GENERAL' && (
            <svg
              viewBox="0 0 320 150"
              className="h-full w-full"
              role="img"
              aria-label="Berlin C1 Argumentations- und Diskurspfad"
            >
              <line x1="54" y1="132" x2="54" y2="24" stroke="rgba(255,255,255,0.35)" strokeWidth="2" />
              <circle cx="54" cy="58" r="13" fill="rgba(45,212,191,0.22)" stroke="var(--accent)" strokeWidth="2" />
              <path d="M 78 58 L 160 40 L 258 62" fill="none" stroke="var(--accent)" strokeWidth="2.5" className="c1-svg-flow" />
              <path d="M 78 96 L 165 110 L 258 84" fill="none" stroke="var(--sky)" strokeWidth="2" className="c1-svg-flow" />
              <rect x="130" y="24" width="68" height="28" rx="8" fill="rgba(255,255,255,0.1)" stroke="rgba(255,255,255,0.3)" strokeWidth="1.5" />
              <text x="164" y="42" textAnchor="middle" fill="var(--on-hero)" fontSize="9" fontWeight="800">
                These · C1
              </text>
              <rect x="130" y="96" width="68" height="28" rx="8" fill="rgba(63,140,255,0.16)" stroke="var(--sky)" strokeWidth="1.5" />
              <text x="164" y="114" textAnchor="middle" fill="var(--on-hero)" fontSize="9" fontWeight="800">
                Beleg
              </text>
              <circle cx="266" cy="74" r="24" fill="rgba(45,212,191,0.18)" stroke="var(--accent)" strokeWidth="2" className="c1-float-slow" />
              <text x="266" y="77" textAnchor="middle" fill="var(--on-hero)" fontSize="9" fontWeight="800">
                Synthese
              </text>
            </svg>
          )}
            </div>
          )}

          {/* Scene / Diagram Mode Switcher */}
          <div className="flex items-center justify-between gap-2 text-[11px]">
            <span className="text-[var(--on-hero2)]">Ansicht wählen</span>
            <div className="inline-flex rounded-xl border border-white/15 bg-white/5 p-0.5">
              <button
                type="button"
                onClick={() => setVisualMode('ARTWORK')}
                className={`rounded-lg px-2.5 py-1 font-bold transition ${
                  visualMode === 'ARTWORK'
                    ? 'bg-[var(--accent)] text-[var(--on-accent)]'
                    : 'text-[var(--on-hero2)] hover:text-white'
                }`}
              >
                Szene
              </button>
              <button
                type="button"
                onClick={() => setVisualMode('DIAGRAM')}
                className={`rounded-lg px-2.5 py-1 font-bold transition ${
                  visualMode === 'DIAGRAM'
                    ? 'bg-[var(--accent)] text-[var(--on-accent)]'
                    : 'text-[var(--on-hero2)] hover:text-white'
                }`}
              >
                Schaubild
              </button>
            </div>
          </div>
        </div>

        <div className="flex flex-col justify-between space-y-3 md:col-span-7">
          <div className="flex flex-wrap items-center justify-between gap-2">
            <span className="inline-flex items-center gap-1.5 text-[11px] font-bold uppercase tracking-wider text-[var(--accent)]">
              {selectedContext === 'ENGINEERING' ? (
                <Cpu className="h-3.5 w-3.5" />
              ) : selectedContext === 'COACHING' ? (
                <Trophy className="h-3.5 w-3.5" />
              ) : selectedContext === 'SOCIAL' ? (
                <MessageCircle className="h-3.5 w-3.5" />
              ) : (
                <Sparkles className="h-3.5 w-3.5" />
              )}
              Kontextszene · {CONTEXT_LABELS[selectedContext]}
            </span>
            <span className="text-[11px] font-bold tabular-nums text-[var(--on-hero2)]">
              Nachweiskriterien: {completedPillars}/4
            </span>
          </div>

          <p className="text-xs leading-relaxed text-[var(--on-hero)]">
            {unit.contexts[selectedContext]}
          </p>

          <div className="grid grid-cols-2 gap-2 pt-1 sm:grid-cols-4">
            {pillars.map((p) => (
              <div
                key={p.label}
                className={`flex items-center gap-1.5 rounded-xl border px-2.5 py-2 text-[10px] font-bold transition ${
                  p.done
                    ? 'border-[var(--ok)]/60 bg-[var(--ok)]/20 text-white'
                    : 'border-white/15 bg-white/5 text-[var(--on-hero2)]'
                }`}
              >
                <CheckCircle2
                  className={`h-3.5 w-3.5 shrink-0 ${
                    p.done ? 'text-[var(--ok)]' : 'opacity-40'
                  }`}
                />
                <span className="truncate">{p.label}</span>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};

interface InteractiveGrammarXRayProps {
  reading: string;
  grammar: string;
  controlledModel: string;
  expressions: string[];
  unitId: string;
  onSeedAllExpressions: () => void;
}

const GRAMMAR_MARKERS = [
  'weil',
  'obwohl',
  'während',
  'sodass',
  'indem',
  'damit',
  'sofern',
  'falls',
  'wenngleich',
  'ungeachtet',
  'anstatt',
  'weder',
  'noch',
  'sowohl',
  'je',
  'desto',
  'würde',
  'hätte',
  'wäre',
  'könnte',
  'sollte',
  'müsste',
  'wurde',
  'worden',
  'deshalb',
  'allerdings',
  'jedoch',
  'dennoch',
  'hingegen',
  'infolge',
];

function splitSentences(text: string): string[] {
  const parts = text
    .split(/(?<=[.!?])\s+/)
    .map((s) => s.trim())
    .filter(Boolean);
  return parts.length > 0 ? parts : [text];
}

function analyzeGermanSentenceTopology(sentence: string) {
  const words = sentence.replace(/[„“"()]/g, '').split(/\s+/).filter(Boolean);
  const vorfeld = words.slice(0, Math.min(2, words.length)).join(' ');
  const verbPos2 = words.length > 2 ? words[2] : '—';
  const satzklammer = words.length > 6 ? words.slice(-2).join(' ') : '—';
  const mittelfeld =
    words.length > 5
      ? words.slice(3, words.length - 2).join(' ')
      : words.slice(3).join(' ') || '—';

  const markersFound = words.filter((w) => {
    const clean = w.toLowerCase().replace(/[,.;:!?]/g, '');
    return GRAMMAR_MARKERS.includes(clean);
  });

  return { vorfeld, verbPos2, mittelfeld, satzklammer, markersFound };
}

/**
 * Interactive visual decomposition of lesson reading, expressions & grammar structure.
 */
export const LessonVisualStudio: React.FC<InteractiveGrammarXRayProps> = ({
  reading,
  grammar,
  controlledModel,
  expressions,
  unitId,
  onSeedAllExpressions,
}) => {
  const sentences = useMemo(() => splitSentences(reading), [reading]);
  const [selectedSentenceIdx, setSelectedSentenceIdx] = useState(0);
  const [activeSlot, setActiveSlot] = useState<
    'VORFELD' | 'V2' | 'MITTELFELD' | 'KLAMMER'
  >('VORFELD');
  const [speakingId, setSpeakingId] = useState<string | null>(speechService.getSpeakingId());
  const [showTopology, setShowTopology] = useState(true);

  useEffect(() => {
    setSelectedSentenceIdx(0);
  }, [unitId]);

  useEffect(() => {
    return speechService.subscribe(() => setSpeakingId(speechService.getSpeakingId()));
  }, []);

  const activeSentence = sentences[selectedSentenceIdx] ?? sentences[0] ?? reading;
  const topology = useMemo(
    () => analyzeGermanSentenceTopology(activeSentence),
    [activeSentence],
  );

  const playExpression = (expr: string) => {
    const id = `expr-${unitId}-${expr}`;
    if (speakingId === id) {
      speechService.stopSpeaking();
    } else {
      speechService.speak(expr, { id, rate: 0.95 });
    }
  };

  const slots: {
    key: 'VORFELD' | 'V2' | 'MITTELFELD' | 'KLAMMER';
    title: string;
    role: string;
    value: string;
    accent: string;
  }[] = [
    {
      key: 'VORFELD',
      title: '1. Vorfeld',
      role: 'Thema / Konnektor / Rahmen',
      value: topology.vorfeld,
      accent: 'var(--accent)',
    },
    {
      key: 'V2',
      title: '2. Linke Klammer',
      role: 'Finites Verb (V2 / Konjunktiv)',
      value: topology.verbPos2,
      accent: 'var(--sky)',
    },
    {
      key: 'MITTELFELD',
      title: '3. Mittelfeld',
      role: 'Angaben, Subjekt & Präzisierung',
      value: topology.mittelfeld,
      accent: 'var(--violet)',
    },
    {
      key: 'KLAMMER',
      title: '4. Rechte Klammer',
      role: 'Infinitiv / Partizip / Verbende',
      value: topology.satzklammer,
      accent: 'var(--ok)',
    },
  ];

  return (
    <section className="c1-animate-enter space-y-5 rounded-3xl border border-[var(--line)] bg-[var(--surface)] p-5 shadow-sm">
      <div className="space-y-3">
        <div className="flex flex-wrap items-center justify-between gap-2">
          <div>
            <p className="text-[11px] font-bold uppercase tracking-wider text-[var(--accent-ink)]">
              Interaktiver Leseimpuls & Satzbau-Röntgen
            </p>
            <p className="text-xs text-[var(--ink2)]">
              Tippe einen Satz oder Baustein an, um C1-Konnektoren und Verbklammern sichtbar zu machen.
            </p>
          </div>
          <div className="flex items-center gap-2">
            <button
              type="button"
              onClick={() => speechService.speak(activeSentence, { id: `read-${unitId}` })}
              className="inline-flex items-center gap-1.5 rounded-xl border border-[var(--line)] bg-[var(--surface2)] px-3 py-1.5 text-xs font-bold text-[var(--ink)] hover:border-[var(--accent)]"
            >
              <Volume2 className="h-3.5 w-3.5 text-[var(--accent-ink)]" />
              Satz vorlesen
            </button>
            <button
              type="button"
              onClick={() => setShowTopology((v) => !v)}
              className="inline-flex items-center gap-1.5 rounded-xl border border-[var(--accent)]/40 bg-[var(--accent-soft)] px-3 py-1.5 text-xs font-bold text-[var(--accent-ink)]"
            >
              <Layers className="h-3.5 w-3.5" />
              {showTopology ? 'Satzbau ausblenden' : 'Satzbau-Röntgen'}
            </button>
          </div>
        </div>

        <div className="space-y-2">
          {sentences.map((sent, idx) => {
            const isSel = idx === selectedSentenceIdx;
            return (
              <button
                key={idx}
                type="button"
                onClick={() => setSelectedSentenceIdx(idx)}
                className={`flex w-full items-start gap-3 rounded-2xl border p-3.5 text-left transition ${
                  isSel
                    ? 'border-[var(--accent)] bg-[var(--accent-soft)]/60 shadow-xs'
                    : 'border-[var(--line)] bg-[var(--surface2)] hover:border-[var(--accent)]/50'
                }`}
              >
                <span
                  className={`mt-0.5 flex h-6 w-6 shrink-0 items-center justify-center rounded-lg text-[11px] font-black tabular-nums ${
                    isSel
                      ? 'bg-[var(--accent)] text-[var(--on-accent)]'
                      : 'bg-[var(--surface)] text-[var(--ink2)]'
                  }`}
                >
                  {idx + 1}
                </span>
                <span className="text-sm font-medium leading-relaxed text-[var(--ink)]">
                  {sent}
                </span>
              </button>
            );
          })}
        </div>

        {showTopology && (
          <div className="space-y-3 rounded-2xl border border-[var(--line)] bg-[var(--surface2)] p-4">
            <div className="flex flex-wrap items-center justify-between gap-2">
              <span className="text-[11px] font-extrabold uppercase tracking-wider text-[var(--ink2)]">
                Topologische Felder (Satz {selectedSentenceIdx + 1})
              </span>
              {topology.markersFound.length > 0 && (
                <div className="flex flex-wrap items-center gap-1.5">
                  <span className="text-[10px] font-bold text-[var(--ink2)]">
                    Signalwörter:
                  </span>
                  {topology.markersFound.map((m, i) => (
                    <span
                      key={`${m}-${i}`}
                      className="rounded-lg bg-[var(--violet-soft)] px-2 py-0.5 text-[10px] font-extrabold text-[var(--violet)]"
                    >
                      {m}
                    </span>
                  ))}
                </div>
              )}
            </div>

            <svg className="h-3 w-full" viewBox="0 0 400 12" preserveAspectRatio="none">
              <line
                x1="12"
                y1="6"
                x2="388"
                y2="6"
                stroke="var(--accent)"
                strokeWidth="2.5"
                className="c1-svg-flow"
              />
              <circle cx="12" cy="6" r="4" fill="var(--accent)" />
              <circle cx="136" cy="6" r="4" fill="var(--sky)" />
              <circle cx="262" cy="6" r="4" fill="var(--violet)" />
              <circle cx="388" cy="6" r="4" fill="var(--ok)" />
            </svg>

            <div className="grid gap-2 sm:grid-cols-2 lg:grid-cols-4">
              {slots.map((slot) => {
                const selected = activeSlot === slot.key;
                return (
                  <button
                    key={slot.key}
                    type="button"
                    onClick={() => setActiveSlot(slot.key)}
                    className={`rounded-xl border p-3 text-left transition ${
                      selected
                        ? 'scale-[1.01] border-[var(--accent)] bg-[var(--surface)] shadow-sm'
                        : 'border-[var(--line)] bg-[var(--surface)]/70 hover:border-[var(--accent)]/60'
                    }`}
                  >
                    <p
                      className="text-[10px] font-extrabold uppercase tracking-wider"
                      style={{ color: slot.accent }}
                    >
                      {slot.title}
                    </p>
                    <p className="mt-1 text-xs font-bold text-[var(--ink)]">{slot.value}</p>
                    <p className="mt-1 text-[10px] text-[var(--ink2)]">{slot.role}</p>
                  </button>
                );
              })}
            </div>
          </div>
        )}
      </div>

      <div className="space-y-3 rounded-2xl border border-[var(--line)] bg-[var(--surface2)] p-4">
        <div className="flex flex-wrap items-center justify-between gap-2">
          <p className="text-xs font-extrabold text-[var(--ink)]">
            Interaktive C1-Schlüsselausdrücke (antippen für Aussprache)
          </p>
          <button
            type="button"
            onClick={onSeedAllExpressions}
            className="inline-flex items-center gap-1 rounded-xl border border-[var(--accent)] bg-[var(--accent-soft)] px-3 py-1.5 text-[11px] font-bold text-[var(--accent-ink)]"
          >
            <Plus className="h-3.5 w-3.5" />
            Alle ins Wortschatz-SRS übernehmen
          </button>
        </div>

        <div className="grid gap-2 sm:grid-cols-2">
          {expressions.map((expr) => {
            const isPlaying = speakingId === `expr-${unitId}-${expr}`;
            return (
              <button
                key={expr}
                type="button"
                onClick={() => playExpression(expr)}
                className={`group flex items-center justify-between gap-3 rounded-xl border p-3 text-left transition ${
                  isPlaying
                    ? 'border-[var(--accent)] bg-[var(--accent-soft)]'
                    : 'border-[var(--line)] bg-[var(--surface)] hover:border-[var(--accent)]'
                }`}
              >
                <span className="text-xs font-bold text-[var(--ink)]">{expr}</span>
                <span
                  className={`flex h-7 w-7 shrink-0 items-center justify-center rounded-lg transition ${
                    isPlaying
                      ? 'bg-[var(--accent)] text-[var(--on-accent)]'
                      : 'bg-[var(--surface2)] text-[var(--accent-ink)] group-hover:bg-[var(--accent-soft)]'
                  }`}
                >
                  <Volume2 className="h-3.5 w-3.5" />
                </span>
              </button>
            );
          })}
        </div>
      </div>

      <ImprintGrammarBoard unitId={unitId} grammar={grammar} />

      <div className="rounded-2xl border border-[var(--line)] bg-[var(--surface2)] p-4 text-xs">
        <p className="text-[11px] font-extrabold uppercase tracking-wider text-[var(--ink2)]">
          Zielbild (Nachweis erst bei eigener unassistierter Produktion)
        </p>
        <p className="mt-1.5 leading-relaxed text-[var(--ink2)]">
          {controlledModel}
        </p>
      </div>
    </section>
  );
};
