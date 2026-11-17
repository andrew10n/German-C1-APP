import React, { useMemo, useState } from 'react';
import {
  AlertCircle,
  ArrowRight,
  CheckCircle2,
  ChevronDown,
  ChevronUp,
  Compass,
  Flag,
  Layers,
  Star,
  UserCheck,
} from 'lucide-react';
import { C1ProficiencyProgressChart } from '../components/C1ProficiencyProgressChart';
import { GrammarTopicsProgressChart } from '../components/GrammarTopicsProgressChart';
import {
  FeedbackChallengeModal,
  HumanReviewModal,
} from '../components/ProgrammeModals';
import { SummaryDashboard } from '../components/SummaryDashboard';
import { VisualPerformanceDashboard } from '../components/VisualPerformanceDashboard';
import { assessmentBank, REVIEW_REMINDER } from '../data/assessmentBank';
import { coreUnits, curriculumUnits } from '../data/curriculumCatalogue';
import {
  ALL_SKILL_CATEGORIES,
  CONTEXT_LABELS,
  SKILL_CATEGORY_META,
} from '../data/learningData';
import { AdaptiveControlEngine } from '../engines/adaptiveControlEngine';
import { AdaptiveDiagnosticRoute } from '../engines/adaptiveDiagnosticRoute';
import { adaptiveExplanation, taskLabel } from '../engines/adaptivePracticeCopy';
import { MasteryEngine } from '../engines/masteryEngine';
import { WEAKNESS_SIGNAL_LABELS, WeaknessEngine } from '../engines/weaknessEngine';
import { useApp } from '../state/AppContext';
import type {
  ContextTrack,
  CurriculumUnit,
  Dimension,
  FeedbackClaim,
  SkillCategory,
} from '../types';

const CATEGORY_TO_DIMENSIONS: Record<SkillCategory, Dimension[]> = {
  HOERVERSTEHEN: ['COMPREHENSION'],
  SPONTANES_SPRECHEN: ['FLUENCY', 'RANGE'],
  INTERAKTION: ['INTERACTION', 'PRAGMATICS'],
  LESEVERSTEHEN: ['COMPREHENSION', 'COHERENCE'],
  SCHREIBEN: ['COHERENCE', 'ACCURACY', 'REGISTER'],
  GRAMMATIK: ['ACCURACY'],
  WORTSCHATZ: ['RANGE', 'NATURALNESS'],
  MEDIATION: ['MEDIATION', 'REGISTER'],
  AUSSPRACHE_PROSODIE: ['NATURALNESS', 'FLUENCY'],
};

type EvaluationTab = 'OVERVIEW' | 'SKILLS' | 'CHECKPOINTS';
type ExamPartTab = 'PARTS' | 'READING' | 'LISTENING' | 'CHECKLIST';

export const IntelligenceProfileScreen: React.FC = () => {
  const {
    preferences,
    attempts,
    realLifeExperiences,
    humanReviews,
    feedbackChallenges,
    skillScores,
    crossCutting,
    dualBenchmark,
    navigateTo,
    openUnitInProgramme,
  } = useApp();

  const [activeTab, setActiveTab] = useState<EvaluationTab>('OVERVIEW');
  const [showAllAttempts, setShowAllAttempts] = useState(false);
  const [showGapsList, setShowGapsList] = useState(false);
  const [showExamPaper, setShowExamPaper] = useState(false);
  const [examPartTab, setExamPartTab] = useState<ExamPartTab>('PARTS');
  const [assessmentContextFilter, setAssessmentContextFilter] = useState<
    ContextTrack | 'ALL'
  >('ALL');
  const [selectedAssessmentTaskId, setSelectedAssessmentTaskId] = useState<string>(
    assessmentBank[0]?.taskId ?? '',
  );
  const [reviewingAttemptId, setReviewingAttemptId] = useState<string | null>(null);
  const [challengingClaim, setChallengingClaim] = useState<FeedbackClaim | null>(null);

  const now = Date.now();
  const lang = preferences.language;

  const masteredUnits = coreUnits.filter((u) => MasteryEngine.isMastered(u, attempts));
  const gaps = useMemo(() => MasteryEngine.graduationGaps(attempts), [attempts]);
  const authoredUnits = curriculumUnits.filter((u) => u.content !== null);
  const completedCheckpoints = AdaptiveDiagnosticRoute.completedAuthoredUnits(attempts, now);

  const liveCategoryEvidence = useMemo(() => {
    const out = {} as Record<
      SkillCategory,
      { scorePct: number; avgFour: number | null; reviewCount: number; statusNote: string }
    >;
    for (const cat of ALL_SKILL_CATEGORIES) {
      const dims = CATEGORY_TO_DIMENSIONS[cat];
      let sum = 0;
      let count = 0;
      let reviewsWithDim = 0;
      for (const rev of humanReviews) {
        let hitInRev = false;
        for (const dim of dims) {
          const val = rev.scores[dim];
          if (typeof val === 'number' && Number.isFinite(val)) {
            sum += val;
            count += 1;
            hitInRev = true;
          }
        }
        if (hitInRev) reviewsWithDim += 1;
      }
      if (count > 0) {
        const avgFour = sum / count;
        out[cat] = {
          scorePct: Math.round((avgFour / 4) * 100),
          avgFour,
          reviewCount: reviewsWithDim,
          statusNote: `Ø ${avgFour.toFixed(1)} / 4,0 (${reviewsWithDim}×)`,
        };
      } else {
        out[cat] = {
          scorePct: skillScores[cat].score,
          avgFour: null,
          reviewCount: 0,
          statusNote: 'Noch nicht rubrikgeprüft',
        };
      }
    }
    return out;
  }, [humanReviews, skillScores]);

  const liveCrossCutting = useMemo(() => {
    const computeAvg = (dim: Dimension): { pct: number; label: string } | null => {
      const vals = humanReviews
        .map((r) => r.scores[dim])
        .filter((v): v is number => typeof v === 'number' && Number.isFinite(v));
      if (vals.length === 0) return null;
      const avg = vals.reduce((a, b) => a + b, 0) / vals.length;
      return {
        pct: Math.round((avg / 4) * 100),
        label: `Ø ${avg.toFixed(1)} / 4 (${vals.length}×)`,
      };
    };
    return crossCutting.map((item) => {
      const dim: Dimension = item.title.toLowerCase().includes('pragmat')
        ? 'PRAGMATICS'
        : 'REGISTER';
      const live = computeAvg(dim);
      return live ? { ...item, score: live.pct, levelLabel: live.label } : item;
    });
  }, [crossCutting, humanReviews]);

  const controlDecision = useMemo(
    () => AdaptiveControlEngine.decide(attempts, realLifeExperiences, now),
    [attempts, realLifeExperiences, now],
  );
  const weaknessProfile = useMemo(
    () => WeaknessEngine.profile(attempts, realLifeExperiences, now),
    [attempts, realLifeExperiences, now],
  );

  const scheduledReviews = authoredUnits
    .map((unit) => {
      const nextAt = AdaptiveDiagnosticRoute.nextReview(attempts, unit.id, now);
      return nextAt !== null ? { unit, nextAt } : null;
    })
    .filter((x): x is { unit: CurriculumUnit; nextAt: number } => x !== null)
    .sort((a, b) => a.nextAt - b.nextAt);
  const dueReviews = scheduledReviews.filter((r) => r.nextAt <= now);

  const sortedAttempts = useMemo(
    () => [...attempts].sort((a, b) => b.timestamp - a.timestamp),
    [attempts],
  );
  const visibleAttempts = showAllAttempts
    ? sortedAttempts.slice(0, 10)
    : sortedAttempts.slice(0, 3);

  const skillStarRows = [
    { id: 'listening', label: 'Hören', tasks: new Set(['listening']) },
    { id: 'speaking', label: 'Sprechen', tasks: new Set(['speaking']) },
    {
      id: 'writing',
      label: 'Schreiben',
      tasks: new Set(['writing', 'controlled', 'transfer', 'context-application']),
    },
    { id: 'interaction', label: 'Interaktion', tasks: new Set(['interaction']) },
  ];

  const filteredAssessments =
    assessmentContextFilter === 'ALL'
      ? assessmentBank
      : assessmentBank.filter((t) => t.context === assessmentContextFilter);
  const activeTask =
    filteredAssessments.find((t) => t.taskId === selectedAssessmentTaskId) ??
    filteredAssessments[0];

  return (
    <div className="space-y-5">
      {/* Compact Header */}
      <div className="flex flex-wrap items-end justify-between gap-3">
        <div>
          <p className="text-[11px] font-bold uppercase tracking-wider text-[var(--accent-ink)]">
            {completedCheckpoints}/{authoredUnits.length} Checkpoints · {humanReviews.length}{' '}
            Rubrikprüfungen
          </p>
          <h1 className="font-display text-3xl font-extrabold text-[var(--ink)]">
            Auswertung & Engine
          </h1>
        </div>

        <button
          type="button"
          onClick={() => navigateTo('BaselineDiagnostic')}
          className="inline-flex items-center gap-1.5 rounded-xl border border-[var(--line)] bg-[var(--surface)] px-3.5 py-2 text-xs font-bold text-[var(--ink)] hover:border-[var(--accent)]"
        >
          MCQ-Stichprobe
          <ArrowRight className="h-3.5 w-3.5" />
        </button>
      </div>

      {/* Focused 3-Tab Switcher */}
      <div className="grid grid-cols-3 gap-1.5 rounded-2xl border border-[var(--line)] bg-[var(--surface)] p-1.5">
        {(
          [
            { id: 'OVERVIEW', label: 'Fokus & Feedback' },
            { id: 'SKILLS', label: 'Kompetenzen (9)' },
            { id: 'CHECKPOINTS', label: `Prüfungen (${assessmentBank.length})` },
          ] as const
        ).map((tab) => {
          const active = activeTab === tab.id;
          return (
            <button
              key={tab.id}
              type="button"
              onClick={() => setActiveTab(tab.id)}
              className={`rounded-xl px-2 py-2.5 text-xs font-extrabold whitespace-nowrap transition ${
                active
                  ? 'bg-[var(--accent)] text-[var(--on-accent)]'
                  : 'text-[var(--ink2)] hover:text-[var(--ink)]'
              }`}
            >
              {tab.label}
            </button>
          );
        })}
      </div>

      {/* TAB 1: FOCUSED ENGINE OVERVIEW & LATEST FEEDBACK */}
      {activeTab === 'OVERVIEW' && (
        <div className="c1-animate-enter space-y-4">
          <SummaryDashboard
            masteredCount={masteredUnits.length}
            totalUnitsCount={coreUnits.length}
            completedCheckpoints={completedCheckpoints}
            totalCheckpoints={authoredUnits.length}
            humanReviewsCount={humanReviews.length}
            dueReviewsCount={dueReviews.length}
            liveCategoryEvidence={liveCategoryEvidence}
          />

          <div className="grid gap-4 md:grid-cols-2">
            {/* Engine Focus */}
            <section className="flex flex-col justify-between space-y-3 rounded-3xl border border-[var(--line)] bg-[var(--surface)] p-5 shadow-sm">
              <div className="space-y-2">
                <div className="flex items-center justify-between gap-2 text-[11px] font-bold">
                  <span className="uppercase tracking-wider text-[var(--accent-ink)]">
                    Nächster Fokus · Konfidenz {controlDecision.confidence}
                  </span>
                  <span className="tabular-nums text-[var(--ink2)]">
                    {dueReviews.length} fällig
                  </span>
                </div>
                <h2 className="text-lg font-extrabold text-[var(--ink)]">
                  {controlDecision.primaryTaskId
                    ? taskLabel(controlDecision.primaryTaskId, lang)
                    : 'Kernpfad fortsetzen'}
                </h2>
                <p className="text-xs font-semibold text-[var(--accent-ink)]">
                  {controlDecision.taskOrder
                    .slice(0, 4)
                    .map((t) => taskLabel(t, lang))
                    .join(' → ')}
                </p>
                <p className="text-xs leading-relaxed text-[var(--ink2)]">
                  {adaptiveExplanation(controlDecision, weaknessProfile, lang)}
                </p>
              </div>

              <button
                type="button"
                onClick={() => navigateTo('Programme')}
                className="inline-flex items-center justify-center gap-1.5 rounded-xl bg-[var(--accent)] px-4 py-2.5 text-xs font-extrabold text-[var(--on-accent)]"
              >
                Zur nächsten Lektion
                <ArrowRight className="h-3.5 w-3.5" />
              </button>
            </section>

            {/* Compact Readiness & 4 Pillars */}
            <section className="space-y-3 rounded-3xl border border-[var(--line)] bg-[var(--surface)] p-5 shadow-sm">
              <div className="flex items-center justify-between gap-2">
                <h2 className="text-sm font-extrabold text-[var(--ink)]">
                  Nachweis-Stand ({masteredUnits.length}/{coreUnits.length} Einheiten)
                </h2>
                <span className="text-[11px] font-bold text-[var(--coral)]">
                  {gaps.length} Lücken
                </span>
              </div>

              <div className="grid grid-cols-2 gap-2">
                {skillStarRows.map((row) => {
                  const practicedCount = new Set(
                    attempts.filter((a) => row.tasks.has(a.taskId)).map((a) => a.unitId),
                  ).size;
                  const stars = Math.min(5, practicedCount);
                  return (
                    <div
                      key={row.id}
                      className="rounded-xl border border-[var(--line)] bg-[var(--surface2)] px-3 py-2"
                    >
                      <div className="flex items-center justify-between text-xs">
                        <span className="font-bold text-[var(--ink)]">{row.label}</span>
                        <span className="font-extrabold tabular-nums text-[var(--accent-ink)]">
                          {stars}/5
                        </span>
                      </div>
                      <div className="mt-1 flex items-center gap-0.5">
                        {Array.from({ length: 5 }).map((_, i) => (
                          <Star
                            key={i}
                            className={`h-3 w-3 ${
                              i < stars
                                ? 'fill-[var(--accent)] text-[var(--accent)]'
                                : 'text-[var(--ink2)] opacity-30'
                            }`}
                          />
                        ))}
                      </div>
                    </div>
                  );
                })}
              </div>

              <div className="rounded-xl border border-[var(--coral)]/30 bg-[var(--coral-soft)] p-3 text-xs leading-relaxed text-[var(--ink)]">
                <span className="font-bold text-[var(--coral)]">Evidenz-Hinweis: </span>
                {dualBenchmark.functionalReadinessSummary}
              </div>
            </section>
          </div>

          {/* Latest Feedback & Attempts */}
          <section className="space-y-3 rounded-3xl border border-[var(--line)] bg-[var(--surface)] p-5 shadow-sm">
            <div className="flex flex-wrap items-center justify-between gap-2">
              <h2 className="text-sm font-extrabold text-[var(--ink)]">
                Letzte Versuche & Rückmeldungen ({attempts.length})
              </h2>
              {sortedAttempts.length > 3 && (
                <button
                  type="button"
                  onClick={() => setShowAllAttempts((v) => !v)}
                  className="text-xs font-bold text-[var(--accent-ink)]"
                >
                  {showAllAttempts ? 'Weniger' : `Alle (${sortedAttempts.length})`}
                </button>
              )}
            </div>

            {WeaknessEngine.top(weaknessProfile).length > 0 && (
              <div className="flex flex-wrap gap-2 text-[11px] text-[var(--coral)]">
                {WeaknessEngine.top(weaknessProfile)
                  .slice(0, 3)
                  .map((w) => (
                    <span key={w.signal} className="font-semibold">
                      • {WEAKNESS_SIGNAL_LABELS[w.signal]} ({w.sampleCount}×)
                    </span>
                  ))}
              </div>
            )}

            {visibleAttempts.length === 0 ? (
              <p className="rounded-2xl border border-[var(--line)] bg-[var(--surface2)] p-4 text-xs text-[var(--ink2)]">
                Noch keine gespeicherten Lernversuche. Starte eine Lektion unter „Lektionen“.
              </p>
            ) : (
              <div className="space-y-2">
                {visibleAttempts.map((att) => {
                  const uObj = curriculumUnits.find((u) => u.id === att.unitId);
                  return (
                    <div
                      key={att.id}
                      className="flex flex-wrap items-center justify-between gap-2 rounded-2xl border border-[var(--line)] bg-[var(--surface2)] p-3 text-xs"
                    >
                      <div className="min-w-0 flex-1">
                        <p className="truncate font-bold text-[var(--ink)]">
                          {uObj?.title ?? att.unitId} · {att.taskId}
                        </p>
                        <p className="mt-0.5 line-clamp-1 text-[var(--ink2)]">
                          {att.response}
                        </p>
                      </div>
                      <div className="flex items-center gap-2">
                        {uObj && (
                          <button
                            type="button"
                            onClick={() => openUnitInProgramme(att.unitId, att.context)}
                            className="rounded-xl border border-[var(--line)] bg-[var(--surface)] px-2.5 py-1 text-[11px] font-bold text-[var(--ink)] hover:border-[var(--accent)]"
                          >
                            Lektion
                          </button>
                        )}
                        {att.source !== 'HUMAN_REVIEW' && (
                          <button
                            type="button"
                            onClick={() => setReviewingAttemptId(att.id)}
                            className="inline-flex items-center gap-1 rounded-xl border border-[var(--accent)] bg-[var(--accent-soft)] px-2.5 py-1 text-[11px] font-bold text-[var(--accent-ink)]"
                          >
                            <UserCheck className="h-3 w-3" />
                            Rubrik
                          </button>
                        )}
                      </div>
                    </div>
                  );
                })}
              </div>
            )}
          </section>
        </div>
      )}

      {/* TAB 2: CHARTS + 9 CORE COMPETENCIES */}
      {activeTab === 'SKILLS' && (
        <div className="c1-animate-enter space-y-5">
          <div className="grid gap-4 md:grid-cols-3">
            <C1ProficiencyProgressChart />
            <VisualPerformanceDashboard />
            <GrammarTopicsProgressChart />
          </div>

          <section className="space-y-3">
            <div className="flex items-center gap-2">
              <Layers className="h-4 w-4 text-[var(--accent-ink)]" />
              <h2 className="text-sm font-extrabold uppercase tracking-wider text-[var(--ink)]">
                9 C1-Kompetenzdimensionen (Nullwert = noch nicht rubrikgeprüft)
              </h2>
            </div>

            <div className="grid gap-2.5 sm:grid-cols-2 lg:grid-cols-3">
              {ALL_SKILL_CATEGORIES.map((category) => {
                const meta = SKILL_CATEGORY_META[category];
                const ev = liveCategoryEvidence[category];
                const hasEvidence = ev.avgFour !== null;
                return (
                  <div
                    key={category}
                    className="flex flex-col justify-between rounded-2xl border border-[var(--line)] bg-[var(--surface)] p-3.5"
                  >
                    <div className="flex items-center justify-between gap-2">
                      <h3 className="text-xs font-extrabold text-[var(--ink)]">
                        {meta.titleDe}
                      </h3>
                      <span
                        className={`text-xs font-extrabold tabular-nums ${
                          hasEvidence ? 'text-[var(--accent-ink)]' : 'text-[var(--ink2)]'
                        }`}
                      >
                        {ev.scorePct}%
                      </span>
                    </div>

                    <div className="mt-2.5 h-1.5 overflow-hidden rounded-full bg-[var(--surface2)]">
                      <div
                        className={`h-full rounded-full ${
                          hasEvidence && (ev.avgFour ?? 0) >= 3
                            ? 'bg-[var(--ok)]'
                            : 'bg-[var(--accent)]'
                        }`}
                        style={{ width: `${ev.scorePct}%` }}
                      />
                    </div>
                    <p className="mt-1.5 flex items-center gap-1 text-[11px] text-[var(--ink2)]">
                      {hasEvidence ? (
                        <CheckCircle2 className="h-3 w-3 shrink-0 text-[var(--ok)]" />
                      ) : (
                        <AlertCircle className="h-3 w-3 shrink-0 text-[var(--ink2)]" />
                      )}
                      {ev.statusNote}
                    </p>
                  </div>
                );
              })}
            </div>
          </section>

          <section className="space-y-2.5">
            <div className="flex items-center gap-2">
              <Compass className="h-4 w-4 text-[var(--violet)]" />
              <h2 className="text-sm font-extrabold uppercase tracking-wider text-[var(--ink)]">
                Querschnittskompetenzen
              </h2>
            </div>
            <div className="grid gap-3 md:grid-cols-2">
              {liveCrossCutting.map((item) => (
                <div
                  key={item.title}
                  className="rounded-2xl border border-[var(--line)] bg-[var(--surface)] p-3.5"
                >
                  <div className="flex items-center justify-between gap-2">
                    <h3 className="text-xs font-extrabold text-[var(--ink)]">
                      {item.title}
                    </h3>
                    <span className="text-[11px] font-bold tabular-nums text-[var(--ink2)]">
                      {item.levelLabel} · {item.score}%
                    </span>
                  </div>
                  <p className="mt-1 text-xs text-[var(--ink2)]">{item.description}</p>
                </div>
              ))}
            </div>
          </section>
        </div>
      )}

      {/* TAB 3: UNSEEN CHECKPOINTS & GAPS (PROGRESSIVE DISCLOSURE) */}
      {activeTab === 'CHECKPOINTS' && (
        <div className="c1-animate-enter space-y-4">
          <div className="flex flex-wrap items-center justify-between gap-2 rounded-2xl border border-[var(--line)] bg-[var(--surface)] px-4 py-3">
            <span className="text-xs font-bold text-[var(--ink)]">
              Offene Nachweislücken im Programm:{' '}
              <strong className="tabular-nums text-[var(--coral)]">{gaps.length}</strong>
            </span>
            <button
              type="button"
              onClick={() => setShowGapsList((v) => !v)}
              className="inline-flex items-center gap-1 rounded-xl border border-[var(--line)] bg-[var(--surface2)] px-3 py-1.5 text-xs font-bold text-[var(--ink)]"
            >
              {showGapsList ? 'Lücken ausblenden' : 'Lückenliste öffnen'}
              {showGapsList ? (
                <ChevronUp className="h-3.5 w-3.5" />
              ) : (
                <ChevronDown className="h-3.5 w-3.5" />
              )}
            </button>
          </div>

          {showGapsList && (
            <ul className="max-h-56 space-y-1.5 overflow-y-auto rounded-2xl border border-[var(--line)] bg-[var(--surface)] p-4 text-xs text-[var(--ink2)]">
              {gaps.map((gap, i) => (
                <li key={i}>• {gap}</li>
              ))}
            </ul>
          )}

          <div className="flex flex-wrap gap-1.5">
            {(['ALL', 'GENERAL', 'SOCIAL', 'ENGINEERING', 'COACHING'] as const).map(
              (ctx) => {
                const count =
                  ctx === 'ALL'
                    ? assessmentBank.length
                    : assessmentBank.filter((t) => t.context === ctx).length;
                const active = assessmentContextFilter === ctx;
                return (
                  <button
                    key={ctx}
                    type="button"
                    onClick={() => {
                      setAssessmentContextFilter(ctx);
                      const firstMatch =
                        ctx === 'ALL'
                          ? assessmentBank[0]
                          : assessmentBank.find((t) => t.context === ctx);
                      if (firstMatch) setSelectedAssessmentTaskId(firstMatch.taskId);
                    }}
                    className={`rounded-xl border px-3 py-1.5 text-xs font-bold transition ${
                      active
                        ? 'border-[var(--accent)] bg-[var(--accent-soft)] text-[var(--accent-ink)]'
                        : 'border-[var(--line)] bg-[var(--surface)] text-[var(--ink2)] hover:text-[var(--ink)]'
                    }`}
                  >
                    {ctx === 'ALL' ? 'Alle' : CONTEXT_LABELS[ctx]} ({count})
                  </button>
                );
              },
            )}
          </div>

          {/* Clean, Scannable Checkpoint Selector Cards */}
          <div className="grid gap-2.5 sm:grid-cols-2">
            {filteredAssessments.map((task) => {
              const isSelected = activeTask?.taskId === task.taskId;
              return (
                <button
                  key={task.taskId}
                  type="button"
                  onClick={() => {
                    setSelectedAssessmentTaskId(task.taskId);
                    setShowExamPaper(false);
                  }}
                  className={`rounded-2xl border p-3.5 text-left text-xs transition ${
                    isSelected
                      ? 'border-[var(--accent)] bg-[var(--accent-soft)]/45 shadow-sm'
                      : 'border-[var(--line)] bg-[var(--surface)] hover:border-[var(--accent)]/60'
                  }`}
                >
                  <div className="flex items-center justify-between gap-2 text-[11px]">
                    <span className="font-bold text-[var(--accent-ink)]">
                      {CONTEXT_LABELS[task.context]}
                    </span>
                    <span className="tabular-nums text-[var(--ink2)]">
                      {task.parts.reduce((sum, p) => sum + p.minutes, 0)} Min.
                    </span>
                  </div>
                  <p className="mt-1 font-extrabold text-[var(--ink)]">{task.title}</p>
                </button>
              );
            })}
          </div>

          {/* Focused Active Checkpoint Card with On-Demand Drawer */}
          {activeTask && (
            <section className="space-y-3 rounded-3xl border border-[var(--line)] bg-[var(--surface)] p-5">
              <div className="flex flex-wrap items-center justify-between gap-3">
                <div>
                  <p className="text-[11px] font-bold uppercase tracking-wider text-[var(--accent-ink)]">
                    Ausgewählter Abschluss-Checkpoint · {activeTask.stream}
                  </p>
                  <h3 className="text-base font-extrabold text-[var(--ink)]">
                    {activeTask.title}
                  </h3>
                </div>
                <button
                  type="button"
                  onClick={() => setShowExamPaper((v) => !v)}
                  className="inline-flex items-center gap-1.5 rounded-xl bg-[var(--accent)] px-3.5 py-2 text-xs font-extrabold text-[var(--on-accent)]"
                >
                  {showExamPaper ? 'Prüfungsbogen schließen' : 'Prüfungsbogen öffnen'}
                  {showExamPaper ? (
                    <ChevronUp className="h-3.5 w-3.5" />
                  ) : (
                    <ChevronDown className="h-3.5 w-3.5" />
                  )}
                </button>
              </div>

              {showExamPaper && (
                <div className="c1-animate-enter space-y-3 border-t border-[var(--line)] pt-3">
                  <div className="flex flex-wrap gap-1.5">
                    {(
                      [
                        { id: 'PARTS', label: `6 Aufgaben (${activeTask.parts.length})` },
                        { id: 'READING', label: 'Lesequelle' },
                        { id: 'LISTENING', label: 'Hörimpuls' },
                        { id: 'CHECKLIST', label: 'Prüfer-Checkliste' },
                      ] as const
                    ).map((sub) => (
                      <button
                        key={sub.id}
                        type="button"
                        onClick={() => setExamPartTab(sub.id)}
                        className={`rounded-xl border px-3 py-1.5 text-xs font-bold transition ${
                          examPartTab === sub.id
                            ? 'border-[var(--accent)] bg-[var(--accent-soft)] text-[var(--accent-ink)]'
                            : 'border-[var(--line)] bg-[var(--surface2)] text-[var(--ink2)]'
                        }`}
                      >
                        {sub.label}
                      </button>
                    ))}
                  </div>

                  {examPartTab === 'PARTS' && (
                    <div className="grid gap-2.5 sm:grid-cols-2">
                      {activeTask.parts.map((part) => (
                        <div
                          key={part.skill}
                          className="rounded-xl border border-[var(--line)] bg-[var(--surface2)] p-3 text-xs"
                        >
                          <p className="font-extrabold text-[var(--accent-ink)]">
                            {part.skill} · {part.minutes} Min.
                          </p>
                          <p className="mt-1 leading-relaxed text-[var(--ink2)]">
                            {part.prompt}
                          </p>
                        </div>
                      ))}
                    </div>
                  )}

                  {examPartTab === 'READING' && (
                    <div className="rounded-xl border border-[var(--line)] bg-[var(--surface2)] p-4 text-xs leading-relaxed text-[var(--ink)]">
                      {activeTask.sourceReading}
                    </div>
                  )}

                  {examPartTab === 'LISTENING' && (
                    <div className="rounded-xl border border-[var(--line)] bg-[var(--surface2)] p-4 text-xs leading-relaxed text-[var(--ink)]">
                      {activeTask.sourceListening}
                    </div>
                  )}

                  {examPartTab === 'CHECKLIST' && (
                    <ul className="list-disc space-y-1 rounded-xl border border-[var(--line)] bg-[var(--surface2)] p-4 pl-7 text-xs leading-relaxed text-[var(--ink2)]">
                      {(activeTask.reviewerChecklist.length > 0
                        ? activeTask.reviewerChecklist
                        : [REVIEW_REMINDER]
                      ).map((item, i) => (
                        <li key={i}>{item}</li>
                      ))}
                    </ul>
                  )}
                </div>
              )}
            </section>
          )}
        </div>
      )}

      {feedbackChallenges.length > 0 && (
        <div className="flex items-center gap-2 text-xs text-[var(--ink2)]">
          <Flag className="h-3.5 w-3.5 text-[var(--coral)]" />
          <span>{feedbackChallenges.length} Feedback-Einspruch/-Einsprüche erfasst.</span>
        </div>
      )}

      {reviewingAttemptId && (
        <HumanReviewModal
          attemptId={reviewingAttemptId}
          onClose={() => setReviewingAttemptId(null)}
        />
      )}
      {challengingClaim && (
        <FeedbackChallengeModal
          claim={challengingClaim}
          onClose={() => setChallengingClaim(null)}
        />
      )}
    </div>
  );
};
