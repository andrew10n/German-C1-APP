import React, { useEffect, useMemo, useRef, useState } from 'react';
import {
  ArrowLeft,
  ArrowRight,
  Bookmark,
  BookmarkCheck,
  BookOpen,
  CheckCircle2,
  ChevronDown,
  ChevronUp,
  ClipboardCheck,
  Clock,
  Compass,
  ExternalLink,
  Eye,
  Flag,
  Headphones,
  LayoutGrid,
  ListChecks,
  Lock,
  Mic,
  MicOff,
  Moon,
  Plus,
  Search,
  Settings,
  ShieldCheck,
  Sun,
  UserCheck,
  X,
} from 'lucide-react';
import { AcousticWaveform } from '../components/AcousticWaveform';
import { AiFeedbackShareButton } from '../components/AiFeedbackShareButton';
import { BookAudioPracticeSection } from '../components/BookAudioPracticeSection';
import { DailyStreakBadge } from '../components/DailyStreakBadge';
import { ImprintVisualChapterDeck } from '../components/ImprintInteractiveDiagrams';
import { LessonConversationStudio } from '../components/LessonConversationStudio';
import {
  ContextIllustrationFrame,
  ImprintRetrievalCanvas,
  ImprintTaskBlueprint,
} from '../components/ImprintLessonVisuals';
import {
  ContextSceneBanner,
  LanguagePatternStudio,
  LessonConceptMap,
  LessonJourneyVisual,
  LessonStepVisual,
  LessonVisualGraphic,
  LessonVisualStudio,
  ListeningDialogueStudio,
  ReadingChunkStudio,
} from '../components/LessonVisualStudio';
import {
  FeedbackChallengeModal,
  HumanReviewModal,
  OnboardingModal,
  PreferencesModal,
  RealLifeAddModal,
  RealLifeReviewModal,
} from '../components/ProgrammeModals';
import { StageMetroMap } from '../components/StageMetroMap';
import {
  coreUnits,
  curriculumUnits,
  orientationUnits,
  stages,
} from '../data/curriculumCatalogue';
import { CONTEXT_LABELS } from '../data/learningData';
import { lessonListeningForUnit, ownedDialogueBriefs } from '../data/listeningResources';
import { AdaptiveControlEngine } from '../engines/adaptiveControlEngine';
import { AdaptiveLessonContentEngine } from '../engines/adaptiveLessonContentEngine';
import { adaptiveExplanation } from '../engines/adaptivePracticeCopy';
import { AssessmentDeliveryEngine } from '../engines/assessmentDelivery';
import { lessonVisualBlueprint } from '../engines/lessonVisualParser';
import { MasteryEngine } from '../engines/masteryEngine';
import { REAL_LIFE_ISSUE_LABELS, WeaknessEngine } from '../engines/weaknessEngine';
import { speechService } from '../services/speechService';
import { useApp } from '../state/AppContext';
import type {
  AssessmentSessionKind,
  ContextTrack,
  CurriculumUnit,
  FeedbackClaim,
  RealLifeExperience,
} from '../types';

const PRACTICE_TASKS: {
  id: string;
  label: string;
  field: keyof NonNullable<CurriculumUnit['content']>;
}[] = [
  { id: 'retrieval', label: 'Aktiver Abruf', field: 'expressions' },
  { id: 'controlled', label: 'Kontrollierter Satzbau', field: 'controlledPrompt' },
  { id: 'speaking', label: 'Freies Sprechen', field: 'speaking' },
  { id: 'interaction', label: 'Interaktion & Reparatur', field: 'interaction' },
  { id: 'writing', label: 'Schreiben & Register', field: 'writing' },
  { id: 'transfer', label: 'Unbekannter Transfer', field: 'transfer' },
];

const SESSION_KINDS: { kind: AssessmentSessionKind; label: string; desc: string }[] = [
  { kind: 'DELAYED_CHECKPOINT', label: 'Verzögerter Transfer-Check', desc: '4 Transferaufgaben · 30 Min.' },
  { kind: 'GOETHE_STYLE', label: 'Goethe-C1 Übungsformat', desc: 'Hören, Lesen, Schreiben, Sprechen · 30 Min.' },
  { kind: 'WEEKLY_INTEGRATED', label: 'Wöchentlicher Integrativ-Lauf', desc: '5 Transferaufgaben · 60 Min.' },
  { kind: 'PILOT_WEEK', label: 'Pilotwochen-Vergleich', desc: '3 Aufgaben · 30 Min.' },
  { kind: 'LISTENING_PRACTICE', label: 'Hörpraxis-Protokoll', desc: 'Eigene Audioquelle · 20 Min.' },
  { kind: 'SPEAKING_PRACTICE', label: 'Sprechpraxis-Aufnahme', desc: 'Freie Mikrofonantwort · 20 Min.' },
];

const GUIDED_OUTPUT_LIMITATIONS: Record<string, string> = {
  speaking:
    'Sprich laut und speichere danach dein Skript oder eine ehrliche Selbstreflexion. Die App bewertet hier keine Aussprache.',
  interaction:
    'Übe mit einer realen Person oder wechsle selbst die Rollen. Notiere den Dialog oder reflektiere ihn ehrlich.',
  writing:
    'Formuliere deinen Text selbstständig und prüfe Register sowie Argumentstruktur.',
  transfer:
    'Bearbeite die Situation ohne Modell und prüfe sie anschließend selbst. Das bleibt Übung, kein verifizierter Kompetenznachweis.',
};

export const ProgrammeScreen: React.FC = () => {
  const {
    isDark,
    toggleTheme,
    preferences,
    attempts,
    realLifeExperiences,
    assessments,
    activeAssessmentId,
    setActiveAssessmentId,
    feedbackChallenges,
    humanReviews,
    practiceDrafts,
    bookmarkedUnitIds,
    lastUnitId,
    onboardingComplete,
    quizResults,
    dailyStreak,
    programmeNotice,
    programmeError,
    clearProgrammeNotice,
    clearProgrammeError,
    startDailySession,
    navigateTo,
    pendingUnitOpen,
    clearPendingUnitOpen,
    setLastLocation,
    toggleBookmark,
    markUnitAssisted,
    submitUnitQuiz,
    recordPracticeAttempt,
    savePracticeDraft,
    saveAdaptiveAddition,
    startAssessmentSession,
    updateAssessmentResponse,
    setAssessmentIndex,
    completeAssessmentSession,
    seedCurriculumExpressions,
  } = useApp();

  const [selectedUnitId, setSelectedUnitId] = useState<string | null>(lastUnitId);
  const [selectedContext, setSelectedContext] = useState<ContextTrack>('GENERAL');
  const [bookmarkFilter, setBookmarkFilter] = useState(false);
  const [curriculumQuery, setCurriculumQuery] = useState('');
  const [expandedStages, setExpandedStages] = useState<Record<number, boolean>>({
    0: true,
    1: true,
    2: false,
    3: false,
    4: false,
    5: false,
    6: false,
  });
  const [dashboardTab, setDashboardTab] = useState<'ROADMAP' | 'CATALOGUE'>('ROADMAP');
  const [showOnboarding, setShowOnboarding] = useState(false);
  const [showPreferences, setShowPreferences] = useState(false);
  const [showRealLifeAdd, setShowRealLifeAdd] = useState(false);
  const [reviewingRealLife, setReviewingRealLife] =
    useState<RealLifeExperience | null>(null);
  const [reviewingAttemptId, setReviewingAttemptId] = useState<string | null>(null);
  const [challengingClaim, setChallengingClaim] = useState<FeedbackClaim | null>(null);
  const [showAssessmentPicker, setShowAssessmentPicker] = useState(false);

  // UnitDetail local states
  const [showFullLessonContent, setShowFullLessonContent] = useState(false);
  const [fullViewTab, setFullViewTab] = useState<
    'VISUAL' | 'INPUT' | 'QUIZ' | 'PRACTICE' | 'CONVERSATION'
  >('VISUAL');
  const [guidedInputTab, setGuidedInputTab] = useState<'MODEL' | 'SOURCE'>('MODEL');
  const [guidedLangTab, setGuidedLangTab] = useState<'IMPRINT' | 'PATTERNS'>('IMPRINT');
  const [guidedStepIndex, setGuidedStepIndex] = useState(0);
  const [completedStepKeys, setCompletedStepKeys] = useState<string[]>([]);
  const [selectedExpressionIdx, setSelectedExpressionIdx] = useState(0);
  const [activePracticeTaskId, setActivePracticeTaskId] = useState<string>('controlled');
  const [showControlledModel, setShowControlledModel] = useState(false);

  // Local Browser Audio Capture (in-memory MediaRecorder for assessments & lesson speaking)
  const [isRecordingAudio, setIsRecordingAudio] = useState(false);
  const [audioClipUrl, setAudioClipUrl] = useState<string | null>(null);
  const [audioCaptureError, setAudioCaptureError] = useState<string | null>(null);
  const recorderRef = useRef<MediaRecorder | null>(null);
  const chunksRef = useRef<Blob[]>([]);
  const recordingTargetRef = useRef<((kbSize: number) => void) | null>(null);

  useEffect(() => {
    return () => {
      if (audioClipUrl) URL.revokeObjectURL(audioClipUrl);
    };
  }, [audioClipUrl]);

  useEffect(() => {
    if (!pendingUnitOpen) return;
    const target = curriculumUnits.find((u) => u.id === pendingUnitOpen.unitId);
    if (target) {
      speechService.stopSpeaking();
      setSelectedUnitId(target.id);
      if (pendingUnitOpen.context) {
        setSelectedContext(pendingUnitOpen.context);
      }
      setGuidedStepIndex(0);
      setCompletedStepKeys([]);
      setSelectedExpressionIdx(0);
      setShowControlledModel(false);
      setShowFullLessonContent(false);
      setAudioCaptureError(null);
      setAudioClipUrl((prev) => {
        if (prev) URL.revokeObjectURL(prev);
        return null;
      });
      setLastLocation(target.id, activePracticeTaskId);
    }
    clearPendingUnitOpen();
  }, [pendingUnitOpen, clearPendingUnitOpen, setLastLocation, activePracticeTaskId]);

  const startMicRecording = async (onRecorded?: (kbSize: number) => void) => {
    setAudioCaptureError(null);
    if (
      typeof navigator === 'undefined' ||
      !navigator.mediaDevices?.getUserMedia ||
      typeof MediaRecorder === 'undefined'
    ) {
      setAudioCaptureError(
        'Browser-Mikrofonaufnahme (MediaRecorder) wird nicht unterstützt. Bitte nutze das Textfeld für dein Sprechskript.',
      );
      return;
    }
    try {
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      const recorder = new MediaRecorder(stream);
      chunksRef.current = [];
      recordingTargetRef.current = onRecorded ?? null;
      recorder.ondataavailable = (ev) => {
        if (ev.data.size > 0) chunksRef.current.push(ev.data);
      };
      recorder.onstop = () => {
        stream.getTracks().forEach((t) => t.stop());
        const blob = new Blob(chunksRef.current, { type: recorder.mimeType || 'audio/webm' });
        setAudioClipUrl((prev) => {
          if (prev) URL.revokeObjectURL(prev);
          return URL.createObjectURL(blob);
        });
        const kb = Math.max(1, Math.round(blob.size / 1024));
        recordingTargetRef.current?.(kb);
        recordingTargetRef.current = null;
      };
      recorderRef.current = recorder;
      recorder.start();
      setIsRecordingAudio(true);
    } catch {
      setAudioCaptureError('Mikrofonzugriff abgelehnt oder kein Mikrofon verfügbar.');
    }
  };

  const stopMicRecording = () => {
    recorderRef.current?.stop();
    setIsRecordingAudio(false);
  };

  const now = Date.now();
  const lang = preferences.language;

  const controlDecision = useMemo(
    () => AdaptiveControlEngine.decide(attempts, realLifeExperiences, now),
    [attempts, realLifeExperiences, now],
  );
  const weaknessProfile = useMemo(
    () => WeaknessEngine.profile(attempts, realLifeExperiences, now),
    [attempts, realLifeExperiences, now],
  );
  const dueExperiences = realLifeExperiences.filter((e) => e.nextReviewAt <= now);
  const masteredCount = coreUnits.filter((u) =>
    MasteryEngine.isMastered(u, attempts),
  ).length;

  const activePersistedAssessment = useMemo(
    () =>
      assessments.find((a) => a.session.id === activeAssessmentId && !a.completed) ?? null,
    [assessments, activeAssessmentId],
  );

  const selectedUnit = useMemo(
    () => curriculumUnits.find((u) => u.id === selectedUnitId) ?? null,
    [selectedUnitId],
  );

  const openUnit = (unit: CurriculumUnit, initialFocusTask?: string) => {
    speechService.stopSpeaking();
    setSelectedUnitId(unit.id);
    setGuidedStepIndex(0);
    setCompletedStepKeys([]);
    setSelectedExpressionIdx(0);
    setShowControlledModel(false);
    setShowFullLessonContent(false);
    setAudioCaptureError(null);
    setAudioClipUrl((prev) => {
      if (prev) URL.revokeObjectURL(prev);
      return null;
    });
    if (initialFocusTask) {
      setActivePracticeTaskId(initialFocusTask);
    }
    setLastLocation(unit.id, initialFocusTask ?? activePracticeTaskId);
  };

  // ---------- 1. TIMED ASSESSMENT RUNNER VIEW ----------
  if (activePersistedAssessment) {
    const { session, responses, currentIndex } = activePersistedAssessment;
    const task = session.tasks[currentIndex] ?? session.tasks[0];
    const remainingMin = AssessmentDeliveryEngine.remainingMinutes(session, Date.now());
    const responseVal = responses[task.id] ?? '';
    const audioNoteKey = `assessment-audio:${task.id}`;
    const audioNote = responses[audioNoteKey] ?? '';

    const handleAssessmentMicToggle = () => {
      if (isRecordingAudio) {
        stopMicRecording();
      } else {
        void startMicRecording((kb) => {
          updateAssessmentResponse(
            session.id,
            audioNoteKey,
            `Lokale Sprachaufnahme erstellt (${kb} KB, nur in dieser Sitzung).`,
          );
        });
      }
    };

    return (
      <div className="space-y-5">
        <div className="flex flex-wrap items-center justify-between gap-2">
          <button
            type="button"
            onClick={() => setActiveAssessmentId(null)}
            className="inline-flex items-center gap-1.5 rounded-xl border border-[var(--line)] bg-[var(--surface2)] px-3 py-1.5 text-xs font-bold text-[var(--ink)]"
          >
            <ArrowLeft className="h-3.5 w-3.5" />
            Pausieren & zurück
          </button>
          <span className="inline-flex items-center gap-1.5 rounded-xl border border-[var(--accent)]/40 bg-[var(--accent-soft)] px-3 py-1.5 text-xs font-extrabold tabular-nums text-[var(--accent-ink)]">
            <Clock className="h-3.5 w-3.5" />
            Restzeit: {remainingMin} Min.
          </span>
        </div>

        <section className="space-y-4 rounded-3xl border border-[var(--line)] bg-[var(--surface)] p-5 shadow-sm">
          <div className="flex flex-wrap items-center justify-between gap-2">
            <span className="rounded-lg bg-[var(--violet-soft)] px-2.5 py-1 text-[10px] font-bold uppercase text-[var(--violet)]">
              {session.kind} · Aufgabe {currentIndex + 1} / {session.tasks.length}
            </span>
            <span className="text-xs font-semibold text-[var(--ink2)]">
              Kontext: {CONTEXT_LABELS[task.context]}
            </span>
          </div>

          <p className="whitespace-pre-line text-sm font-medium leading-relaxed text-[var(--ink)]">
            {task.prompt}
          </p>

          {task.evidence === 'AUDIO_RESPONSE' && (
            <div className="space-y-2 rounded-2xl border border-[var(--line)] bg-[var(--surface2)] p-4">
              <div className="flex flex-wrap items-center gap-2">
                <button
                  type="button"
                  onClick={handleAssessmentMicToggle}
                  className={`inline-flex items-center gap-1.5 rounded-xl px-3.5 py-2 text-xs font-extrabold ${
                    isRecordingAudio
                      ? 'bg-[var(--coral)] text-white'
                      : 'bg-[var(--accent)] text-[var(--on-accent)]'
                  }`}
                >
                  {isRecordingAudio ? (
                    <MicOff className="h-4 w-4" />
                  ) : (
                    <Mic className="h-4 w-4" />
                  )}
                  {isRecordingAudio ? 'Aufnahme beenden' : 'Lokal aufnehmen'}
                </button>
                {audioClipUrl && (
                  <audio controls src={audioClipUrl} className="h-9 max-w-full" />
                )}
              </div>
              <AcousticWaveform
                active={isRecordingAudio}
                variant="coral"
                label={isRecordingAudio ? 'Mikrofon aktiv' : 'Bereit'}
              />
              {audioCaptureError && (
                <p className="text-xs text-[var(--coral)]">{audioCaptureError}</p>
              )}
              {audioNote && (
                <p className="text-xs text-[var(--accent-ink)]">{audioNote}</p>
              )}
            </div>
          )}

          <div>
            <label className="block text-xs font-bold text-[var(--ink2)]">
              Deine selbstständige Antwort
            </label>
            <textarea
              rows={6}
              value={responseVal}
              onChange={(e) => updateAssessmentResponse(session.id, task.id, e.target.value)}
              placeholder="Formuliere deine Antwort ohne externe Hilfe oder Vorlage..."
              className="mt-1.5 w-full rounded-2xl border border-[var(--line)] bg-[var(--bg)] p-3.5 text-sm text-[var(--ink)] focus:border-[var(--accent)] focus:outline-none"
            />
          </div>

          <div className="flex flex-wrap items-center justify-between gap-2 pt-2">
            <button
              type="button"
              disabled={currentIndex === 0}
              onClick={() => setAssessmentIndex(session.id, currentIndex - 1)}
              className="inline-flex items-center gap-1.5 rounded-xl border border-[var(--line)] bg-[var(--surface2)] px-4 py-2 text-xs font-bold text-[var(--ink)] disabled:opacity-40"
            >
              <ArrowLeft className="h-3.5 w-3.5" />
              Vorherige
            </button>

            <div className="flex gap-2">
              {currentIndex < session.tasks.length - 1 ? (
                <button
                  type="button"
                  onClick={() => setAssessmentIndex(session.id, currentIndex + 1)}
                  className="inline-flex items-center gap-1.5 rounded-xl bg-[var(--accent)] px-4 py-2 text-xs font-extrabold text-[var(--on-accent)]"
                >
                  Nächste Aufgabe
                  <ArrowRight className="h-3.5 w-3.5" />
                </button>
              ) : (
                <button
                  type="button"
                  onClick={() => completeAssessmentSession(session.id)}
                  className="inline-flex items-center gap-1.5 rounded-xl bg-[var(--ok)] px-4 py-2 text-xs font-extrabold text-white"
                >
                  <CheckCircle2 className="h-4 w-4" />
                  Übungsprüfung speichern
                </button>
              )}
            </div>
          </div>
        </section>
      </div>
    );
  }

  // ---------- 2. UNIT DETAIL STUDIO VIEW ----------
  if (selectedUnit) {
    const content = selectedUnit.content;
    const isMastered = MasteryEngine.isMastered(selectedUnit, attempts);
    const isBookmarked = bookmarkedUnitIds.includes(selectedUnit.id);
    const unitListening = lessonListeningForUnit(selectedUnit.id);
    const unitAttempts = attempts
      .filter((a) => a.unitId === selectedUnit.id)
      .sort((a, b) => b.timestamp - a.timestamp);
    const completedTasksSet = new Set(unitAttempts.map((a) => a.taskId));
    const adaptivePlan = AdaptiveLessonContentEngine.create(
      selectedUnit,
      attempts,
      realLifeExperiences,
      selectedContext,
      now,
    );
    const adaptiveRec = adaptivePlan.recommendation;
    const quizKey = `${selectedUnit.id}:${selectedContext}`;
    const quizState = quizResults[quizKey];
    const currentTaskDef =
      PRACTICE_TASKS.find((t) => t.id === activePracticeTaskId) ?? PRACTICE_TASKS[1];
    const draft =
      practiceDrafts.find(
        (d) =>
          d.unitId === selectedUnit.id &&
          d.taskId === currentTaskDef.id &&
          d.context === selectedContext,
      )?.response ?? '';

    const taskPromptText = content
      ? currentTaskDef.id === 'retrieval'
        ? `Verwende diese Ausdrücke frei im Kontext „${CONTEXT_LABELS[selectedContext]}“: ${content.expressions.join(' · ')}`
        : String(content[currentTaskDef.field] ?? '')
      : '';

    // Guided 5-step lesson configuration (matches upstream GuidedLessonFlow)
    const inputTask = adaptivePlan.focusTaskId === 'listening' ? 'listening' : 'reading';
    const addition = adaptivePlan.addition;
    const outputTask =
      addition?.taskId ??
      (['controlled', 'speaking', 'interaction', 'writing', 'transfer'].includes(
        adaptivePlan.focusTaskId,
      )
        ? adaptivePlan.focusTaskId
        : 'controlled');

    const outputTitle =
      addition?.title ??
      (outputTask === 'speaking'
        ? '05 / Frei sprechen'
        : outputTask === 'interaction'
          ? '05 / Im Dialog handeln'
          : outputTask === 'writing'
            ? '05 / Schreiben'
            : outputTask === 'transfer'
              ? '05 / Transfer in einen neuen Kontext'
              : '05 / Kontrolliert formulieren');

    const guidedSteps = [
      { key: 'retrieval', title: '01 / Abruf vor dem Nachlesen' },
      { key: 'input', title: inputTask === 'listening' ? '02 / Hören' : '02 / Lesen' },
      { key: 'quiz', title: '03 / Verständnis prüfen' },
      { key: 'language', title: '04 / Sprachmittel in Aktion' },
      { key: 'application', title: outputTitle },
    ];

    const activeGuidedIndex = Math.min(guidedStepIndex, guidedSteps.length - 1);
    const activeGuidedStep = guidedSteps[activeGuidedIndex];
    const guidedTaskId =
      activeGuidedStep.key === 'retrieval'
        ? 'retrieval'
        : activeGuidedStep.key === 'input'
          ? inputTask
          : activeGuidedStep.key === 'quiz'
            ? 'quiz'
            : activeGuidedStep.key === 'application'
              ? outputTask
              : null;

    const guidedContext =
      activeGuidedStep.key === 'application' && addition ? addition.context : selectedContext;

    const guidedDraft =
      guidedTaskId && guidedTaskId !== 'quiz'
        ? practiceDrafts.find(
            (d) =>
              d.unitId === selectedUnit.id &&
              d.taskId === guidedTaskId &&
              d.context === guidedContext,
          )?.response ?? ''
        : '';

    const guidedStepDone = completedStepKeys.includes(activeGuidedStep.key);
    const guidedNeedsSave =
      guidedTaskId !== null && activeGuidedStep.key !== 'quiz' && !guidedStepDone;
    const guidedCanContinue = guidedTaskId === null || guidedStepDone;

    const markGuidedComplete = (key: string) => {
      setCompletedStepKeys((prev) => (prev.includes(key) ? prev : [...prev, key]));
    };

    const handleGuidedPrimaryAction = () => {
      if (guidedNeedsSave && guidedTaskId) {
        if (!guidedDraft.trim()) return;
        if (activeGuidedStep.key === 'application' && addition) {
          saveAdaptiveAddition(selectedUnit.id, addition, guidedDraft);
        } else {
          recordPracticeAttempt({
            unitId: selectedUnit.id,
            taskId: guidedTaskId,
            response: guidedDraft,
            context: guidedContext,
            unfamiliar: guidedTaskId === 'transfer',
            independentProduction: !showControlledModel,
          });
          savePracticeDraft(selectedUnit.id, guidedTaskId, guidedContext, '');
        }
        markGuidedComplete(activeGuidedStep.key);
        return;
      }

      if (!guidedCanContinue) return;
      if (activeGuidedIndex === guidedSteps.length - 1) {
        speechService.stopSpeaking();
        setSelectedUnitId(null);
      } else {
        setGuidedStepIndex(activeGuidedIndex + 1);
      }
    };

    return (
      <div className="space-y-5">
        {/* Top Navigation Bar */}
        <div className="flex flex-wrap items-center justify-between gap-2">
          <button
            type="button"
            onClick={() => {
              speechService.stopSpeaking();
              setSelectedUnitId(null);
            }}
            className="inline-flex items-center gap-1.5 rounded-xl border border-[var(--line)] bg-[var(--surface2)] px-3 py-2 text-xs font-bold text-[var(--ink)]"
          >
            <ArrowLeft className="h-4 w-4" />
            Zur Kursübersicht
          </button>

          <div className="flex flex-wrap items-center gap-2">
            {content !== null && (
              <button
                type="button"
                onClick={() => setShowFullLessonContent((v) => !v)}
                className={`inline-flex items-center gap-1.5 rounded-xl border px-3 py-2 text-xs font-bold transition ${
                  showFullLessonContent
                    ? 'border-[var(--accent)] bg-[var(--accent-soft)] text-[var(--accent-ink)]'
                    : 'border-[var(--line)] bg-[var(--surface2)] text-[var(--ink)]'
                }`}
              >
                {showFullLessonContent ? (
                  <>
                    <ListChecks className="h-3.5 w-3.5" />
                    Geführter Lernfluss (5 Schritte)
                  </>
                ) : (
                  <>
                    <LayoutGrid className="h-3.5 w-3.5" />
                    Alle Aufgaben & Röntgen
                  </>
                )}
              </button>
            )}

            <button
              type="button"
              onClick={() => toggleBookmark(selectedUnit.id)}
              className={`inline-flex items-center gap-1.5 rounded-xl border px-3 py-2 text-xs font-bold ${
                isBookmarked
                  ? 'border-[var(--accent)] bg-[var(--accent-soft)] text-[var(--accent-ink)]'
                  : 'border-[var(--line)] bg-[var(--surface2)] text-[var(--ink2)]'
              }`}
            >
              {isBookmarked ? (
                <BookmarkCheck className="h-4 w-4" />
              ) : (
                <Bookmark className="h-4 w-4" />
              )}
              {isBookmarked ? 'Gemerkt' : 'Merken'}
            </button>
          </div>
        </div>

        {/* Notice / Error Banners */}
        {programmeNotice && (
          <div className="flex items-center justify-between gap-2 rounded-2xl border border-[var(--accent)]/40 bg-[var(--accent-soft)] px-4 py-3 text-xs font-semibold text-[var(--ink)]">
            <span>{programmeNotice}</span>
            <button type="button" onClick={clearProgrammeNotice} className="rounded-lg p-1 hover:bg-black/10">
              <X className="h-4 w-4" />
            </button>
          </div>
        )}
        {programmeError && (
          <div className="flex items-center justify-between gap-2 rounded-2xl border border-[var(--coral)] bg-[var(--coral-soft)] px-4 py-3 text-xs font-semibold text-[var(--coral)]">
            <span>{programmeError}</span>
            <button type="button" onClick={clearProgrammeError} className="rounded-lg p-1 hover:bg-black/10">
              <X className="h-4 w-4" />
            </button>
          </div>
        )}

        {/* Unit Header + Context Switcher */}
        <section className="rounded-3xl border border-[var(--line)] bg-[var(--surface)] p-5 shadow-sm">
          <div className="flex flex-wrap items-center justify-between gap-2">
            <span className="rounded-lg bg-[var(--accent-soft)] px-2.5 py-1 text-[10px] font-bold uppercase tracking-wider text-[var(--accent-ink)]">
              Stufe {selectedUnit.stage} · {selectedUnit.id}
            </span>
            {isMastered && (
              <span className="inline-flex items-center gap-1 rounded-lg bg-[var(--ok-bg)] px-2.5 py-1 text-[10px] font-bold text-[var(--ok)]">
                <CheckCircle2 className="h-3.5 w-3.5" />
                Nachgewiesen
              </span>
            )}
          </div>
          <h1 className="mt-2 font-display text-3xl font-extrabold text-[var(--ink)]">
            {selectedUnit.title}
          </h1>

          <div className="mt-4 flex flex-wrap gap-1.5">
            {(Object.keys(CONTEXT_LABELS) as ContextTrack[]).map((ctx) => (
              <button
                key={ctx}
                type="button"
                onClick={() => setSelectedContext(ctx)}
                className={`rounded-xl border px-3 py-1.5 text-xs font-bold transition ${
                  selectedContext === ctx
                    ? 'border-[var(--accent)] bg-[var(--accent-soft)] text-[var(--accent-ink)]'
                    : 'border-[var(--line)] bg-[var(--surface2)] text-[var(--ink2)] hover:text-[var(--ink)]'
                }`}
              >
                {CONTEXT_LABELS[ctx]}
              </button>
            ))}
          </div>

          <div className="mt-3 rounded-2xl border border-[var(--line)] bg-[var(--surface2)] px-3.5 py-2.5 text-xs leading-relaxed">
            <span className="font-extrabold text-[var(--accent-ink)]">
              Kontextfokus ({CONTEXT_LABELS[selectedContext]}):{' '}
            </span>
            <span className="text-[var(--ink)]">{selectedUnit.contexts[selectedContext]}</span>
          </div>
        </section>

        {/* Visual Animated Context Scene + 4-Pillar Mastery Evidence Progress */}
        <ContextSceneBanner
          unit={selectedUnit}
          selectedContext={selectedContext}
          unitAttempts={unitAttempts}
          isMastered={isMastered}
        />

        {content === null ? (
          <div className="rounded-3xl border border-[var(--line)] bg-[var(--surface)] p-5 text-xs leading-relaxed text-[var(--ink2)]">
            Diese Einheit ist ein Orientierungs- oder Struktur-Entwurf (`content = null`).
            Ausgearbeitete Aufgaben, Quizze und Nachweise befinden sich in den Kerneinheiten
            der Stufen 1 bis 6.
          </div>
        ) : !showFullLessonContent ? (
          /* ---------- GUIDED 5-STEP LESSON FLOW (UPSTREAM DEFAULT) ---------- */
          <section className="c1-animate-enter space-y-5 rounded-3xl border border-[var(--line)] bg-[var(--surface)] p-5 shadow-sm">
            {/* Step Progress Header */}
            <div className="space-y-2.5">
              <div className="flex flex-wrap items-center justify-between gap-2">
                <span className="text-[11px] font-extrabold uppercase tracking-wider text-[var(--accent-ink)]">
                  Geführter Lernfluss · {activeGuidedStep.title}
                </span>
                <span className="text-xs font-extrabold tabular-nums text-[var(--ink)]">
                  {activeGuidedIndex + 1} / {guidedSteps.length}
                </span>
              </div>

              <div className="flex items-center gap-1.5">
                {guidedSteps.map((s, idx) => {
                  const done = completedStepKeys.includes(s.key);
                  const active = idx === activeGuidedIndex;
                  return (
                    <button
                      key={s.key}
                      type="button"
                      onClick={() => setGuidedStepIndex(idx)}
                      className={`h-2 flex-1 rounded-full transition-all duration-300 ${
                        done
                          ? 'bg-[var(--ok)]'
                          : active
                            ? 'bg-[var(--accent)]'
                            : 'bg-[var(--surface2)]'
                      }`}
                      aria-label={s.title}
                    />
                  );
                })}
              </div>
            </div>

            {/* Guided Step Content */}
            <div key={`${selectedUnit.id}-${activeGuidedStep.key}`} className="c1-animate-enter space-y-4">
              {activeGuidedStep.key === 'retrieval' && (
                <>
                  <LessonStepVisual unit={selectedUnit} phase={0} language={lang} />
                  <ImprintRetrievalCanvas
                    draftLength={guidedDraft.trim().length}
                    hasQuestionMark={guidedDraft.includes('?')}
                  />
                  <p className="text-xs leading-relaxed text-[var(--ink2)]">
                    Was kannst du aus dem Gedächtnis zum Lernziel erklären? Notiere zuerst deine
                    Idee und eine offene Frage, bevor du den Input liest.
                  </p>
                  <textarea
                    rows={4}
                    value={guidedDraft}
                    onChange={(e) =>
                      savePracticeDraft(selectedUnit.id, 'retrieval', guidedContext, e.target.value)
                    }
                    placeholder="Schreibe aus dem Gedächtnis (auch Stichpunkte oder offene Fragen)..."
                    className="w-full rounded-2xl border border-[var(--line)] bg-[var(--bg)] p-3.5 text-sm text-[var(--ink)] focus:border-[var(--accent)] focus:outline-none"
                  />
                </>
              )}

              {activeGuidedStep.key === 'input' && (
                <>
                  <div className="grid grid-cols-2 gap-1.5 rounded-2xl border border-[var(--line)] bg-[var(--surface2)] p-1">
                    <button
                      type="button"
                      onClick={() => setGuidedInputTab('MODEL')}
                      className={`rounded-xl py-2 text-xs font-extrabold transition ${
                        guidedInputTab === 'MODEL'
                          ? 'bg-[var(--accent)] text-[var(--on-accent)]'
                          : 'text-[var(--ink2)] hover:text-[var(--ink)]'
                      }`}
                    >
                      1. Visuelles Denkmodell
                    </button>
                    <button
                      type="button"
                      onClick={() => setGuidedInputTab('SOURCE')}
                      className={`rounded-xl py-2 text-xs font-extrabold transition ${
                        guidedInputTab === 'SOURCE'
                          ? 'bg-[var(--accent)] text-[var(--on-accent)]'
                          : 'text-[var(--ink2)] hover:text-[var(--ink)]'
                      }`}
                    >
                      2. {inputTask === 'reading' ? 'Lesetext' : 'Hörtext'} & Audio
                    </button>
                  </div>

                  {guidedInputTab === 'MODEL' ? (
                    <ImprintVisualChapterDeck
                      unitId={selectedUnit.id}
                      selectedContext={selectedContext}
                      onSelectContext={setSelectedContext}
                      defaultTab="MODEL"
                    />
                  ) : inputTask === 'reading' ? (
                    <ReadingChunkStudio
                      unitId={selectedUnit.id}
                      unitTitle={selectedUnit.title}
                      passage={content.reading}
                      language={lang}
                    />
                  ) : (
                    <div className="space-y-3">
                      <ListeningDialogueStudio
                        unitId={selectedUnit.id}
                        transcript={content.listening}
                        language={lang}
                      />
                      {(unitListening.episodes.length > 0 || unitListening.bookBridges.length > 0) && (
                        <details className="rounded-2xl border border-[var(--line)] bg-[var(--surface2)] p-3.5 text-xs">
                          <summary className="flex cursor-pointer items-center gap-1.5 font-extrabold text-[var(--accent-ink)]">
                            <Headphones className="h-3.5 w-3.5" />
                            Ergänzende Audioquellen & Kursbuch-Audio
                          </summary>
                          <div className="mt-3 space-y-3">
                            <BookAudioPracticeSection
                              selectionKey={selectedUnit.id}
                              onPlaybackStart={() => speechService.stopSpeaking()}
                            />
                            {unitListening.episodes.map((ep) => (
                              <div
                                key={ep.title}
                                className="rounded-xl border border-[var(--line)] bg-[var(--surface)] p-3"
                              >
                                <div className="flex flex-wrap items-center justify-between gap-2">
                                  <span className="font-extrabold text-[var(--ink)]">{ep.title}</span>
                                  <a
                                    href={ep.url}
                                    target="_blank"
                                    rel="noopener noreferrer"
                                    className="inline-flex items-center gap-1 font-bold text-[var(--accent-ink)] hover:underline"
                                  >
                                    Quelle öffnen
                                    <ExternalLink className="h-3 w-3" />
                                  </a>
                                </div>
                                <p className="mt-1 leading-relaxed text-[var(--ink2)]">{ep.task}</p>
                              </div>
                            ))}
                            {unitListening.bookBridges.map((bridge) => (
                              <div
                                key={bridge.bookReference}
                                className="rounded-xl border border-[var(--line)] bg-[var(--surface)] p-3"
                              >
                                <p className="font-extrabold text-[var(--ink)]">{bridge.bookReference}</p>
                                <p className="mt-0.5 text-[11px] text-[var(--ink2)]">
                                  {bridge.audioReference ?? bridge.audioFilenameHint}
                                </p>
                                <p className="mt-1.5 leading-relaxed text-[var(--ink)]">
                                  {bridge.originalPrompt}
                                </p>
                              </div>
                            ))}
                          </div>
                        </details>
                      )}
                    </div>
                  )}

                  <div>
                    <label className="block text-xs font-bold text-[var(--ink2)]">
                      {inputTask === 'reading'
                        ? 'Kernaussage in eigenen Worten'
                        : 'Was hast du verstanden?'}
                    </label>
                    <textarea
                      rows={3}
                      value={guidedDraft}
                      onChange={(e) =>
                        savePracticeDraft(selectedUnit.id, inputTask, guidedContext, e.target.value)
                      }
                      placeholder="Fasse das Wichtigste knapp in eigenen Worten zusammen..."
                      className="mt-1.5 w-full rounded-2xl border border-[var(--line)] bg-[var(--bg)] p-3.5 text-sm text-[var(--ink)] focus:border-[var(--accent)] focus:outline-none"
                    />
                  </div>
                </>
              )}

              {activeGuidedStep.key === 'quiz' && (
                <>
                  <LessonStepVisual unit={selectedUnit} phase={1} language={lang} />
                  <div className="rounded-2xl border border-[var(--line)] bg-[var(--hero)] p-4 text-[var(--on-hero)]">
                    <p className="text-[11px] font-bold text-[var(--accent)]">
                      Verständnischeck · Kernaussage prüfen
                    </p>
                    <p className="mt-1 text-sm font-bold text-[var(--on-hero)]">
                      {content.question}
                    </p>
                  </div>
                  <div className="space-y-2">
                    {content.options.map((opt, idx) => {
                      const chosen = quizState?.option === idx;
                      const isRight = idx === content.correctOption;
                      const submitted = quizState !== undefined;
                      const letter = String.fromCharCode(65 + idx);
                      return (
                        <button
                          key={idx}
                          type="button"
                          onClick={() => {
                            submitUnitQuiz(
                              selectedUnit.id,
                              selectedContext,
                              idx,
                              content.correctOption,
                            );
                            markGuidedComplete('quiz');
                          }}
                          className={`flex w-full items-center gap-3 rounded-2xl border p-3.5 text-left text-xs transition ${
                            submitted && isRight
                              ? 'border-[var(--ok)] bg-[var(--ok-bg)] text-[var(--ink)]'
                              : submitted && chosen && !isRight
                                ? 'border-[var(--coral)] bg-[var(--coral-soft)] text-[var(--ink)]'
                                : 'border-[var(--line)] bg-[var(--surface2)] text-[var(--ink)] hover:border-[var(--accent)]'
                          }`}
                        >
                          <span
                            className={`flex h-7 w-7 shrink-0 items-center justify-center rounded-xl text-xs font-black ${
                              submitted && isRight
                                ? 'bg-[var(--ok)] text-white'
                                : submitted && chosen && !isRight
                                  ? 'bg-[var(--coral)] text-white'
                                  : chosen
                                    ? 'bg-[var(--accent)] text-[var(--on-accent)]'
                                    : 'bg-[var(--surface)] text-[var(--ink2)]'
                            }`}
                          >
                            {submitted && isRight ? '✓' : submitted && chosen ? '✕' : letter}
                          </span>
                          <span className="font-semibold leading-relaxed">{opt}</span>
                        </button>
                      );
                    })}
                    <button
                      type="button"
                      onClick={() => {
                        recordPracticeAttempt({
                          unitId: selectedUnit.id,
                          taskId: 'quiz',
                          response: 'Weiß ich noch nicht',
                          context: selectedContext,
                          source: 'SELF_REFLECTION',
                          scores: {},
                          independentProduction: false,
                        });
                        markGuidedComplete('quiz');
                      }}
                      className="w-full rounded-2xl border border-dashed border-[var(--line)] bg-[var(--surface2)] px-3.5 py-2.5 text-left text-xs font-semibold text-[var(--ink2)] hover:border-[var(--accent)]"
                    >
                      Weiß ich noch nicht (als offen speichern & weiter)
                    </button>
                  </div>
                  {(quizState || guidedStepDone) && (
                    <div className="c1-animate-enter rounded-2xl border border-[var(--accent)]/40 bg-[var(--accent-soft)]/50 p-4 text-xs leading-relaxed text-[var(--ink)]">
                      <p className="font-extrabold text-[var(--accent-ink)]">
                        Beleg aus dem Text
                      </p>
                      <p className="mt-1">{content.explanation}</p>
                    </div>
                  )}
                </>
              )}

              {activeGuidedStep.key === 'language' && (
                <div className="space-y-4">
                  <div className="grid grid-cols-2 gap-1.5 rounded-2xl border border-[var(--line)] bg-[var(--surface2)] p-1">
                    <button
                      type="button"
                      onClick={() => setGuidedLangTab('IMPRINT')}
                      className={`rounded-xl py-2 text-xs font-extrabold transition ${
                        guidedLangTab === 'IMPRINT'
                          ? 'bg-[var(--accent)] text-[var(--on-accent)]'
                          : 'text-[var(--ink2)] hover:text-[var(--ink)]'
                      }`}
                    >
                      1. Baukasten & Chirurgie
                    </button>
                    <button
                      type="button"
                      onClick={() => setGuidedLangTab('PATTERNS')}
                      className={`rounded-xl py-2 text-xs font-extrabold transition ${
                        guidedLangTab === 'PATTERNS'
                          ? 'bg-[var(--accent)] text-[var(--on-accent)]'
                          : 'text-[var(--ink2)] hover:text-[var(--ink)]'
                      }`}
                    >
                      2. Redemittel & Grammatik
                    </button>
                  </div>

                  {guidedLangTab === 'IMPRINT' ? (
                    <ImprintVisualChapterDeck
                      unitId={selectedUnit.id}
                      selectedContext={selectedContext}
                      onSelectContext={setSelectedContext}
                      defaultTab="PUZZLE"
                    />
                  ) : (
                    <>
                      <LessonConceptMap
                        unit={selectedUnit}
                        context={selectedContext}
                        expressions={content.expressions}
                        selectedExpressionIndex={selectedExpressionIdx}
                        language={lang}
                      />
                      <LanguagePatternStudio
                        unitId={selectedUnit.id}
                        expressions={content.expressions}
                        grammar={content.grammar}
                        selectedIndex={selectedExpressionIdx}
                        onSelectedIndexChange={setSelectedExpressionIdx}
                        language={lang}
                      />
                    </>
                  )}

                  <div className="flex flex-wrap gap-2">
                    {guidedLangTab === 'PATTERNS' && content.expressions.length > 1 && (
                      <button
                        type="button"
                        onClick={() =>
                          setSelectedExpressionIdx(
                            (prev) => (prev + 1) % content.expressions.length,
                          )
                        }
                        className="rounded-xl border border-[var(--line)] bg-[var(--surface2)] px-3.5 py-2 text-xs font-bold text-[var(--ink)] hover:border-[var(--accent)]"
                      >
                        Nächste Sprachhandlung
                      </button>
                    )}
                    <button
                      type="button"
                      onClick={() =>
                        seedCurriculumExpressions(selectedUnit.id, content.expressions)
                      }
                      className="inline-flex items-center gap-1.5 rounded-xl border border-[var(--accent)] bg-[var(--accent-soft)] px-3.5 py-2 text-xs font-bold text-[var(--accent-ink)]"
                    >
                      <Plus className="h-3.5 w-3.5" />
                      Ausdrücke ins Abruftraining
                    </button>
                  </div>
                </div>
              )}

              {activeGuidedStep.key === 'application' && (
                <>
                  <LessonStepVisual unit={selectedUnit} phase={2} language={lang} />
                  {addition ? (
                    <div className="space-y-3 rounded-2xl border border-[var(--accent)]/40 bg-[var(--accent-soft)]/45 p-4">
                      <span className="inline-block rounded-lg bg-[var(--accent)] px-2 py-0.5 text-[10px] font-extrabold text-[var(--on-accent)]">
                        {adaptivePlan.difficulty === 'SUPPORT'
                          ? 'GEZIELTE HILFE'
                          : 'NEUER TRANSFER'}
                      </span>
                      <p className="text-sm font-extrabold text-[var(--ink)]">{addition.title}</p>
                      <p className="text-xs text-[var(--ink2)]">{addition.instruction}</p>
                      <p className="text-xs font-semibold leading-relaxed text-[var(--ink)]">
                        {addition.prompt}
                      </p>
                      <textarea
                        rows={5}
                        value={guidedDraft}
                        onChange={(e) =>
                          savePracticeDraft(
                            selectedUnit.id,
                            addition.taskId,
                            addition.context,
                            e.target.value,
                          )
                        }
                        placeholder="Schreibe deine neue Antwort oder Überarbeitung..."
                        className="w-full rounded-2xl border border-[var(--line)] bg-[var(--bg)] p-3.5 text-sm text-[var(--ink)] focus:border-[var(--accent)] focus:outline-none"
                      />
                    </div>
                  ) : (
                    <div className="space-y-3">
                      <ImprintTaskBlueprint
                        unitId={selectedUnit.id}
                        taskKind={outputTask}
                        title={outputTitle}
                        promptText={
                          outputTask === 'speaking'
                            ? content.speaking
                            : outputTask === 'interaction'
                              ? content.interaction
                              : outputTask === 'writing'
                                ? content.writing
                                : outputTask === 'transfer'
                                  ? content.transfer
                                  : content.controlledPrompt
                        }
                        limitationNote={GUIDED_OUTPUT_LIMITATIONS[outputTask]}
                      />

                      {outputTask === 'controlled' && (
                        <div>
                          <button
                            type="button"
                            onClick={() => {
                              markUnitAssisted(selectedUnit.id);
                              setShowControlledModel((v) => !v);
                            }}
                            className="inline-flex items-center gap-1 text-xs font-bold text-[var(--accent-ink)]"
                          >
                            <Eye className="h-3.5 w-3.5" />
                            {showControlledModel
                              ? 'Modell ausblenden'
                              : 'Modell öffnen · zählt als Hilfe'}
                          </button>
                          {showControlledModel && (
                            <div className="mt-2 rounded-2xl border border-[var(--coral)]/30 bg-[var(--coral-soft)] p-3 text-xs leading-relaxed text-[var(--ink)]">
                              {content.controlledModel}
                            </div>
                          )}
                        </div>
                      )}

                      {(outputTask === 'speaking' || outputTask === 'interaction') && (
                        <div className="space-y-2 rounded-2xl border border-[var(--line)] bg-[var(--surface2)] p-3.5">
                          <div className="flex flex-wrap items-center justify-between gap-2">
                            <button
                              type="button"
                              onClick={() => {
                                if (isRecordingAudio) {
                                  stopMicRecording();
                                } else {
                                  void startMicRecording((kb) => {
                                    if (!guidedDraft.trim()) {
                                      savePracticeDraft(
                                        selectedUnit.id,
                                        outputTask,
                                        guidedContext,
                                        `[Lokale Sprachaufnahme ${kb} KB erstellt – Kernpunkte:] `,
                                      );
                                    }
                                  });
                                }
                              }}
                              className={`inline-flex items-center gap-1.5 rounded-xl px-3.5 py-2 text-xs font-extrabold ${
                                isRecordingAudio
                                  ? 'bg-[var(--coral)] text-white'
                                  : 'bg-[var(--accent)] text-[var(--on-accent)]'
                              }`}
                            >
                              {isRecordingAudio ? (
                                <MicOff className="h-3.5 w-3.5" />
                              ) : (
                                <Mic className="h-3.5 w-3.5" />
                              )}
                              {isRecordingAudio
                                ? 'Aufnahme beenden'
                                : 'Antwort lokal aufnehmen'}
                            </button>
                            {audioClipUrl && (
                              <audio controls src={audioClipUrl} className="h-9 max-w-full" />
                            )}
                          </div>
                          <AcousticWaveform
                            active={isRecordingAudio}
                            variant="coral"
                            label={
                              isRecordingAudio
                                ? 'Mikrofon aktiv · spricht frei'
                                : 'Lokale Selbstaufnahme (bleibt nur in dieser Sitzung im Browser)'
                            }
                          />
                          {audioCaptureError && (
                            <p className="text-xs text-[var(--coral)]">{audioCaptureError}</p>
                          )}
                        </div>
                      )}

                      <textarea
                        rows={5}
                        value={guidedDraft}
                        onChange={(e) =>
                          savePracticeDraft(
                            selectedUnit.id,
                            outputTask,
                            guidedContext,
                            e.target.value,
                          )
                        }
                        placeholder="Deine selbstständige Antwort (Entwurf wird automatisch gesichert)..."
                        className="w-full rounded-2xl border border-[var(--line)] bg-[var(--bg)] p-3.5 text-sm text-[var(--ink)] focus:border-[var(--accent)] focus:outline-none"
                      />

                      <AiFeedbackShareButton
                        task={`${selectedUnit.title} (${outputTitle})`}
                        response={guidedDraft}
                        evidenceType="Geführte C1-Lektion"
                      />
                    </div>
                  )}
                </>
              )}
            </div>

            {guidedTaskId && !guidedCanContinue && (
              <p className="text-[11px] text-[var(--ink2)]">
                Speichere deine Antwort, damit der nächste Schritt freigeschaltet wird.
              </p>
            )}

            {/* Guided Step Navigation Bar */}
            <div className="flex flex-wrap items-center justify-between gap-2 border-t border-[var(--line)] pt-4">
              <button
                type="button"
                disabled={activeGuidedIndex === 0}
                onClick={() => setGuidedStepIndex((i) => Math.max(0, i - 1))}
                className="inline-flex items-center gap-1 rounded-xl border border-[var(--line)] bg-[var(--surface2)] px-4 py-2 text-xs font-bold text-[var(--ink)] disabled:opacity-40"
              >
                <ArrowLeft className="h-3.5 w-3.5" />
                Voriger Schritt
              </button>

              <button
                type="button"
                disabled={guidedNeedsSave ? !guidedDraft.trim() : !guidedCanContinue}
                onClick={handleGuidedPrimaryAction}
                className="inline-flex items-center gap-1.5 rounded-xl bg-[var(--accent)] px-5 py-2.5 text-xs font-extrabold text-[var(--on-accent)] disabled:opacity-40"
              >
                {guidedNeedsSave ? (
                  <>
                    <CheckCircle2 className="h-3.5 w-3.5" />
                    Antwort speichern
                  </>
                ) : activeGuidedIndex === guidedSteps.length - 1 ? (
                  <>
                    <CheckCircle2 className="h-3.5 w-3.5" />
                    Lektion abschließen
                  </>
                ) : (
                  <>
                    Weiter
                    <ArrowRight className="h-3.5 w-3.5" />
                  </>
                )}
              </button>
            </div>
          </section>
        ) : (
          /* ---------- FULL UNIT OVERVIEW & TOPOLOGY X-RAY (TABBED FOR CLARITY) ---------- */
          <div className="space-y-4">
            <LessonJourneyVisual
              unit={selectedUnit}
              completedTaskIds={completedTasksSet}
              language={lang}
            />

            <div className="grid grid-cols-2 gap-1.5 rounded-2xl border border-[var(--line)] bg-[var(--surface)] p-1.5 sm:grid-cols-5">
              {(
                [
                  { id: 'VISUAL', label: 'Schaubild & Fokus' },
                  { id: 'INPUT', label: 'Text & Hören' },
                  { id: 'QUIZ', label: 'Verständnis-Quiz' },
                  { id: 'PRACTICE', label: 'Produktion & Üben' },
                  { id: 'CONVERSATION', label: 'Konversations-Studio' },
                ] as const
              ).map((tab) => (
                <button
                  key={tab.id}
                  type="button"
                  onClick={() => setFullViewTab(tab.id)}
                  className={`rounded-xl px-2 py-2 text-xs font-extrabold whitespace-nowrap transition ${
                    fullViewTab === tab.id
                      ? 'bg-[var(--accent)] text-[var(--on-accent)]'
                      : 'text-[var(--ink2)] hover:text-[var(--ink)]'
                  }`}
                >
                  {tab.label}
                </button>
              ))}
            </div>

            {fullViewTab === 'VISUAL' && (
              <div className="c1-animate-enter space-y-4">
                <ImprintVisualChapterDeck
                  unitId={selectedUnit.id}
                  selectedContext={selectedContext}
                  onSelectContext={setSelectedContext}
                  defaultTab="MODEL"
                />
                <LessonVisualStudio
                  reading={content.reading}
                  grammar={content.grammar}
                  controlledModel={content.controlledModel}
                  expressions={content.expressions}
                  unitId={selectedUnit.id}
                  onSeedAllExpressions={() =>
                    seedCurriculumExpressions(selectedUnit.id, content.expressions)
                  }
                />
              </div>
            )}

            {fullViewTab === 'CONVERSATION' && (
              <div className="c1-animate-enter space-y-4">
                <LessonConversationStudio
                  unitId={selectedUnit.id}
                  unitTitle={selectedUnit.title}
                  contextTrack={selectedContext}
                />
              </div>
            )}

            {fullViewTab === 'INPUT' && (
              <div className="c1-animate-enter space-y-4">
                <ReadingChunkStudio
                  unitId={selectedUnit.id}
                  unitTitle={selectedUnit.title}
                  passage={content.reading}
                  language={lang}
                />
                <ListeningDialogueStudio
                  unitId={selectedUnit.id}
                  transcript={content.listening}
                  language={lang}
                />
                {(unitListening.episodes.length > 0 || unitListening.bookBridges.length > 0) && (
                  <details className="rounded-3xl border border-[var(--line)] bg-[var(--surface)] p-5 text-xs">
                    <summary className="flex cursor-pointer items-center gap-1.5 text-[11px] font-extrabold uppercase tracking-wider text-[var(--accent-ink)]">
                      <Headphones className="h-3.5 w-3.5" />
                      Kuratierte Hörempfehlungen & Kursbuch-Audio öffnen
                    </summary>
                    <div className="mt-3 space-y-3">
                      <BookAudioPracticeSection
                        selectionKey={selectedUnit.id}
                        onPlaybackStart={() => speechService.stopSpeaking()}
                      />
                      <div className="grid gap-3 md:grid-cols-2">
                        {unitListening.episodes.map((ep) => (
                          <div
                            key={ep.title}
                            className="rounded-2xl border border-[var(--line)] bg-[var(--surface2)] p-3.5"
                          >
                            <div className="flex flex-wrap items-center justify-between gap-2">
                              <span className="font-extrabold text-[var(--ink)]">{ep.title}</span>
                              <a
                                href={ep.url}
                                target="_blank"
                                rel="noopener noreferrer"
                                className="inline-flex items-center gap-1 font-bold text-[var(--accent-ink)] hover:underline"
                              >
                                Quelle öffnen
                                <ExternalLink className="h-3 w-3" />
                              </a>
                            </div>
                            <p className="mt-1.5 leading-relaxed text-[var(--ink2)]">{ep.task}</p>
                          </div>
                        ))}
                        {unitListening.bookBridges.map((bridge) => (
                          <div
                            key={bridge.bookReference}
                            className="rounded-2xl border border-[var(--line)] bg-[var(--surface2)] p-3.5"
                          >
                            <p className="font-extrabold text-[var(--ink)]">{bridge.bookReference}</p>
                            <p className="mt-0.5 text-[11px] text-[var(--ink2)]">
                              {bridge.audioReference ?? bridge.audioFilenameHint}
                            </p>
                            <p className="mt-1.5 leading-relaxed text-[var(--ink)]">
                              {bridge.originalPrompt}
                            </p>
                          </div>
                        ))}
                      </div>
                    </div>
                  </details>
                )}
              </div>
            )}

            {/* Automatic Understanding Quiz */}
            {fullViewTab === 'QUIZ' && (
              <section className="c1-animate-enter space-y-3 rounded-3xl border border-[var(--line)] bg-[var(--surface)] p-5">
                <div className="rounded-2xl border border-[var(--line)] bg-[var(--hero)] p-4 text-[var(--on-hero)]">
                  <p className="text-[11px] font-bold text-[var(--accent)]">
                    Verständnischeck · Kernaussage prüfen
                  </p>
                  <p className="mt-1 text-sm font-bold text-[var(--on-hero)]">
                    {content.question}
                  </p>
                </div>
                <div className="space-y-2">
                  {content.options.map((opt, idx) => {
                    const chosen = quizState?.option === idx;
                    const isRight = idx === content.correctOption;
                    const submitted = quizState !== undefined;
                    const letter = String.fromCharCode(65 + idx);
                    return (
                      <button
                        key={idx}
                        type="button"
                        onClick={() =>
                          submitUnitQuiz(
                            selectedUnit.id,
                            selectedContext,
                            idx,
                            content.correctOption,
                          )
                        }
                        className={`flex w-full items-center gap-3 rounded-2xl border p-3.5 text-left text-xs transition ${
                          submitted && isRight
                            ? 'border-[var(--ok)] bg-[var(--ok-bg)] text-[var(--ink)]'
                            : submitted && chosen && !isRight
                              ? 'border-[var(--coral)] bg-[var(--coral-soft)] text-[var(--ink)]'
                              : 'border-[var(--line)] bg-[var(--surface2)] text-[var(--ink)] hover:border-[var(--accent)]'
                        }`}
                      >
                        <span
                          className={`flex h-7 w-7 shrink-0 items-center justify-center rounded-xl text-xs font-black ${
                            submitted && isRight
                              ? 'bg-[var(--ok)] text-white'
                              : submitted && chosen && !isRight
                                ? 'bg-[var(--coral)] text-white'
                                : chosen
                                  ? 'bg-[var(--accent)] text-[var(--on-accent)]'
                                  : 'bg-[var(--surface)] text-[var(--ink2)]'
                          }`}
                        >
                          {submitted && isRight ? '✓' : submitted && chosen ? '✕' : letter}
                        </span>
                        <span className="font-semibold leading-relaxed">{opt}</span>
                      </button>
                    );
                  })}
                </div>
                {quizState && (
                  <div className="c1-animate-enter rounded-2xl border border-[var(--accent)]/40 bg-[var(--accent-soft)]/50 p-4 text-xs leading-relaxed text-[var(--ink)]">
                    <p className="font-extrabold text-[var(--accent-ink)]">
                      Beleg aus dem Text
                    </p>
                    <p className="mt-1">{content.explanation}</p>
                  </div>
                )}
              </section>
            )}

            {/* Practice Section */}
            {fullViewTab === 'PRACTICE' && (
              <section className="c1-animate-enter space-y-4 rounded-3xl border border-[var(--line)] bg-[var(--surface)] p-5">
              <div className="flex flex-wrap items-center justify-between gap-2">
                <p className="text-[11px] font-bold uppercase tracking-wider text-[var(--accent-ink)]">
                  Eigenständige Produktion ({CONTEXT_LABELS[selectedContext]})
                </p>
                <span className="rounded-lg bg-[var(--surface2)] px-2.5 py-1 text-[10px] font-bold text-[var(--ink2)]">
                  Empfehlung: {adaptivePlan.difficulty} ({adaptivePlan.reason})
                </span>
              </div>

              <div className="flex flex-wrap gap-1.5">
                {PRACTICE_TASKS.map((t) => (
                  <button
                    key={t.id}
                    type="button"
                    onClick={() => setActivePracticeTaskId(t.id)}
                    className={`rounded-xl border px-3 py-1.5 text-xs font-bold ${
                      activePracticeTaskId === t.id
                        ? 'border-[var(--accent)] bg-[var(--accent-soft)] text-[var(--accent-ink)]'
                        : 'border-[var(--line)] bg-[var(--surface2)] text-[var(--ink2)]'
                    }`}
                  >
                    {t.label}
                  </button>
                ))}
              </div>

              <ImprintTaskBlueprint
                unitId={selectedUnit.id}
                taskKind={currentTaskDef.id}
                title={currentTaskDef.label}
                promptText={taskPromptText}
              />

              {(currentTaskDef.id === 'speaking' || currentTaskDef.id === 'interaction') && (
                <div className="space-y-2 rounded-2xl border border-[var(--line)] bg-[var(--surface2)] p-3.5">
                  <div className="flex flex-wrap items-center justify-between gap-2">
                    <button
                      type="button"
                      onClick={() => {
                        if (isRecordingAudio) {
                          stopMicRecording();
                        } else {
                          void startMicRecording((kb) => {
                            if (!draft.trim()) {
                              savePracticeDraft(
                                selectedUnit.id,
                                currentTaskDef.id,
                                selectedContext,
                                `[Lokale Sprachaufnahme ${kb} KB erstellt – Kernpunkte:] `,
                              );
                            }
                          });
                        }
                      }}
                      className={`inline-flex items-center gap-1.5 rounded-xl px-3.5 py-2 text-xs font-extrabold ${
                        isRecordingAudio
                          ? 'bg-[var(--coral)] text-white'
                          : 'bg-[var(--accent)] text-[var(--on-accent)]'
                      }`}
                    >
                      {isRecordingAudio ? (
                        <MicOff className="h-3.5 w-3.5" />
                      ) : (
                        <Mic className="h-3.5 w-3.5" />
                      )}
                      {isRecordingAudio ? 'Aufnahme beenden' : 'Antwort lokal aufnehmen'}
                    </button>
                    {audioClipUrl && (
                      <audio controls src={audioClipUrl} className="h-9 max-w-full" />
                    )}
                  </div>
                  <AcousticWaveform
                    active={isRecordingAudio}
                    variant="coral"
                    label={
                      isRecordingAudio
                        ? 'Mikrofon aktiv'
                        : 'Lokale Selbstaufnahme (nur in dieser Sitzung)'
                    }
                  />
                  {audioCaptureError && (
                    <p className="text-xs text-[var(--coral)]">{audioCaptureError}</p>
                  )}
                </div>
              )}

              <div>
                <div className="flex items-center justify-between">
                  <label className="text-xs font-bold text-[var(--ink2)]">
                    Deine Antwort (Entwurf wird automatisch lokal gesichert)
                  </label>
                  <button
                    type="button"
                    onClick={() => {
                      markUnitAssisted(selectedUnit.id);
                      setShowControlledModel((v) => !v);
                    }}
                    className="inline-flex items-center gap-1 text-xs font-bold text-[var(--accent-ink)]"
                  >
                    <Eye className="h-3.5 w-3.5" />
                    {showControlledModel ? 'Modell ausblenden' : 'Kontrolliertes Modell zeigen'}
                  </button>
                </div>

                {showControlledModel && (
                  <div className="mt-2 rounded-2xl border border-[var(--coral)]/30 bg-[var(--coral-soft)] p-3 text-xs leading-relaxed text-[var(--ink)]">
                    <p className="font-bold text-[var(--coral)]">
                      Hilfe verwendet (`assisted = true` für diese Einheit markiert):
                    </p>
                    <p className="mt-1">{content.controlledModel}</p>
                  </div>
                )}

                <textarea
                  rows={5}
                  value={draft}
                  onChange={(e) =>
                    savePracticeDraft(
                      selectedUnit.id,
                      currentTaskDef.id,
                      selectedContext,
                      e.target.value,
                    )
                  }
                  placeholder="Schreibe hier deine selbstständige Antwort..."
                  className="mt-2 w-full rounded-2xl border border-[var(--line)] bg-[var(--bg)] p-3.5 text-sm text-[var(--ink)] focus:border-[var(--accent)] focus:outline-none"
                />
              </div>

              <div className="flex flex-wrap items-center gap-2">
                <button
                  type="button"
                  disabled={!draft.trim()}
                  onClick={() => {
                    recordPracticeAttempt({
                      unitId: selectedUnit.id,
                      taskId: currentTaskDef.id,
                      response: draft,
                      context: selectedContext,
                      unfamiliar: currentTaskDef.id === 'transfer',
                      independentProduction: !showControlledModel,
                    });
                    savePracticeDraft(selectedUnit.id, currentTaskDef.id, selectedContext, '');
                  }}
                  className="inline-flex items-center gap-1.5 rounded-xl bg-[var(--accent)] px-4 py-2 text-xs font-extrabold text-[var(--on-accent)] disabled:opacity-45"
                >
                  <CheckCircle2 className="h-3.5 w-3.5" />
                  Versuch speichern (SELF_REFLECTION)
                </button>

                <AiFeedbackShareButton
                  task={`${selectedUnit.title} (${currentTaskDef.label}): ${taskPromptText}`}
                  response={draft}
                  evidenceType="Kurseinheit-Übungsproduktion"
                />
              </div>

              <p className="text-[11px] text-[var(--ink2)]">
                Selbstreflexion: {content.feedback}
              </p>

              {adaptiveRec.feedbackClaims.length > 0 && (
                <div className="space-y-2 border-t border-[var(--line)] pt-3">
                  <p className="text-xs font-extrabold text-[var(--ink)]">
                    Automatische Strukturhinweise (anfechtbar):
                  </p>
                  {adaptiveRec.feedbackClaims.map((claim) => {
                    const existingChallenge = feedbackChallenges.find(
                      (c) => c.claimId === claim.id,
                    );
                    return (
                      <div
                        key={claim.id}
                        className="flex flex-wrap items-start justify-between gap-2 rounded-2xl border border-[var(--line)] bg-[var(--surface2)] p-3 text-xs"
                      >
                        <div className="min-w-0 flex-1">
                          <p className="font-bold text-[var(--ink)]">
                            {claim.claimType}: „{claim.responseExcerpt}“
                          </p>
                          <p className="mt-0.5 text-[var(--ink2)]">
                            {claim.proposedFeedback}
                          </p>
                          {existingChallenge && (
                            <p className="mt-1 font-bold text-[var(--accent-ink)]">
                              Einspruch gespeichert ({existingChallenge.status})
                            </p>
                          )}
                        </div>
                        <button
                          type="button"
                          onClick={() => setChallengingClaim(claim)}
                          className="inline-flex items-center gap-1 rounded-xl border border-[var(--line)] bg-[var(--surface)] px-2.5 py-1.5 text-[11px] font-bold text-[var(--ink)] hover:border-[var(--coral)]"
                        >
                          <Flag className="h-3 w-3 text-[var(--coral)]" />
                          Anfechten
                        </button>
                      </div>
                    );
                  })}
                </div>
              )}
            </section>
            )}
          </div>
        )}

        {/* Saved Unit Attempts & Human Review Trigger */}
        <section className="space-y-3 rounded-3xl border border-[var(--line)] bg-[var(--surface)] p-5">
          <div className="flex items-center justify-between">
            <h3 className="text-sm font-extrabold text-[var(--ink)]">
              Gespeicherte Versuche ({unitAttempts.length})
            </h3>
            <span className="text-[11px] text-[var(--ink2)]">
              {humanReviews.length} Rubrikprüfungen gesamt
            </span>
          </div>

          {unitAttempts.length === 0 ? (
            <p className="text-xs text-[var(--ink2)]">
              Noch keine gespeicherten Versuche für diese Einheit.
            </p>
          ) : (
            <div className="space-y-2.5">
              {unitAttempts.map((att) => (
                <div
                  key={att.id}
                  className="space-y-2 rounded-2xl border border-[var(--line)] bg-[var(--surface2)] p-3.5 text-xs"
                >
                  <div className="flex flex-wrap items-center justify-between gap-2">
                    <span className="font-bold text-[var(--ink)]">
                      {att.taskId} · {CONTEXT_LABELS[att.context]} ·{' '}
                      <span
                        className={
                          att.source === 'HUMAN_REVIEW'
                            ? 'text-[var(--ok)]'
                            : 'text-[var(--ink2)]'
                        }
                      >
                        {att.source}
                      </span>
                    </span>
                    <span className="text-[11px] text-[var(--ink2)]">
                      {new Date(att.timestamp).toLocaleString('de-DE')}
                    </span>
                  </div>

                  <p className="text-[var(--ink)]">{att.response}</p>

                  {att.source !== 'HUMAN_REVIEW' && (
                    <div className="flex justify-end">
                      <button
                        type="button"
                        onClick={() => setReviewingAttemptId(att.id)}
                        className="inline-flex items-center gap-1 rounded-xl border border-[var(--accent)] bg-[var(--accent-soft)] px-3 py-1 text-[11px] font-bold text-[var(--accent-ink)]"
                      >
                        <UserCheck className="h-3.5 w-3.5" />
                        Rubrikwertung erfassen
                      </button>
                    </div>
                  )}
                </div>
              ))}
            </div>
          )}
        </section>

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
  }

  // ---------- 3. MAIN PROGRAMME DASHBOARD VIEW ----------
  const recommendedUnit =
    curriculumUnits.find((u) => u.id === controlDecision.recommendedUnitId) ??
    curriculumUnits.find((u) => u.id === lastUnitId) ??
    MasteryEngine.nextUnit(attempts, now) ??
    coreUnits[0];
  const recommendedBrief = ownedDialogueBriefs.find(
    (b) => b.id === controlDecision.dialogueBriefId,
  );
  const hasStartedSession = recommendedUnit
    ? attempts.some((a) => a.unitId === recommendedUnit.id)
    : false;

  const normalizedQuery = curriculumQuery.trim().toLowerCase();
  const filteredCoreUnits = coreUnits.filter((unit) => {
    const matchesBookmark = !bookmarkFilter || bookmarkedUnitIds.includes(unit.id);
    if (!normalizedQuery) return matchesBookmark;
    const matchesText =
      unit.title.toLowerCase().includes(normalizedQuery) ||
      unit.id.toLowerCase().includes(normalizedQuery) ||
      unit.objectives.some((o) => o.toLowerCase().includes(normalizedQuery)) ||
      Object.values(unit.contexts).some((c) => c.toLowerCase().includes(normalizedQuery));
    return matchesBookmark && matchesText;
  });

  return (
    <div className="space-y-6">
      {/* Header */}
      <header className="flex flex-wrap items-center justify-between gap-3">
        <div>
          <p className="text-[11px] font-bold uppercase tracking-wider text-[var(--accent-ink)]">
            {preferences.learnerName
              ? `Hallo ${preferences.learnerName} · C1-Lernprogramm`
              : 'Evidenzbasiertes C1-Lernprogramm'}
          </p>
          <h1 className="font-display text-3xl font-extrabold text-[var(--ink)]">
            Lektionen & Roadmap
          </h1>
        </div>

        <div className="flex flex-wrap items-center gap-2">
          <DailyStreakBadge
            streak={dailyStreak}
            onStartDailySession={startDailySession}
          />
          <button
            type="button"
            onClick={toggleTheme}
            className="flex h-9 w-9 items-center justify-center rounded-full border border-[var(--line)] bg-[var(--surface2)] text-[var(--ink)]"
            aria-label="Designmodus wechseln"
          >
            {isDark ? <Sun className="h-4 w-4" /> : <Moon className="h-4 w-4" />}
          </button>
          <button
            type="button"
            onClick={() => setShowPreferences(true)}
            className="flex h-9 w-9 items-center justify-center rounded-full border border-[var(--line)] bg-[var(--surface2)] text-[var(--ink)]"
            aria-label="Einstellungen öffnen"
          >
            <Settings className="h-4 w-4" />
          </button>
        </div>
      </header>

      {/* Notice / Error Banners */}
      {programmeNotice && (
        <div className="flex items-center justify-between gap-2 rounded-2xl border border-[var(--accent)]/40 bg-[var(--accent-soft)] px-4 py-3 text-xs font-semibold text-[var(--ink)]">
          <span>{programmeNotice}</span>
          <button
            type="button"
            onClick={clearProgrammeNotice}
            className="rounded-lg p-1 hover:bg-black/10"
          >
            <X className="h-4 w-4" />
          </button>
        </div>
      )}
      {programmeError && (
        <div className="flex items-center justify-between gap-2 rounded-2xl border border-[var(--coral)] bg-[var(--coral-soft)] px-4 py-3 text-xs font-semibold text-[var(--coral)]">
          <span>{programmeError}</span>
          <button
            type="button"
            onClick={clearProgrammeError}
            className="rounded-lg p-1 hover:bg-black/10"
          >
            <X className="h-4 w-4" />
          </button>
        </div>
      )}

      {/* Onboarding Banner */}
      {!onboardingComplete && (
        <div className="flex flex-wrap items-center justify-between gap-3 rounded-3xl border border-[var(--accent)]/40 bg-[var(--accent-soft)] p-4">
          <div className="flex items-start gap-3">
            <ShieldCheck className="mt-0.5 h-5 w-5 shrink-0 text-[var(--accent-ink)]" />
            <div className="text-xs leading-relaxed text-[var(--ink)]">
              <p className="font-extrabold">Erste Orientierung: Wie Nachweise zählen</p>
              <p className="text-[var(--ink2)]">
                Nur unassistierte Produktion mit gespeicherter Rubrikprüfung zählt als
                Nachweis. Sieh dir kurz die Programmregeln an.
              </p>
            </div>
          </div>
          <button
            type="button"
            onClick={() => setShowOnboarding(true)}
            className="rounded-xl bg-[var(--accent)] px-3.5 py-2 text-xs font-extrabold text-[var(--on-accent)]"
          >
            Regeln lesen
          </button>
        </div>
      )}

      {/* Next Adaptive Step Hero */}
      <section className="relative overflow-hidden rounded-3xl border border-[var(--line)] bg-[var(--hero)] p-6 text-[var(--on-hero)] shadow-lg">
        <div className="grid items-stretch gap-5 md:grid-cols-12">
          <div className="flex flex-col justify-between md:col-span-7">
            <div>
              <div className="flex items-start justify-between gap-4">
                <div className="space-y-2">
                  <p className="flex items-center gap-1.5 text-[11px] font-bold uppercase tracking-wider text-[var(--accent)]">
                    <Compass className="h-3.5 w-3.5" />
                    Nächster Schritt · {masteredCount}/{coreUnits.length} nachgewiesen
                  </p>
                  <h2 className="font-display text-2xl font-extrabold leading-tight">
                    {recommendedUnit?.title ?? 'Dein Einstieg'}
                  </h2>
                </div>

                <div className="c1-float-slow flex h-16 w-16 shrink-0 flex-col items-center justify-center rounded-full border-2 border-[var(--accent)] bg-black/30 text-center">
                  <span className="font-display text-xl font-extrabold tabular-nums text-[var(--on-hero)]">
                    {preferences.dailyMinutes}
                  </span>
                  <span className="text-[9px] font-bold tracking-widest text-[var(--accent)]">
                    MIN
                  </span>
                </div>
              </div>

              <p className="mt-3 text-xs leading-relaxed text-[var(--on-hero2)]">
                {adaptiveExplanation(controlDecision, weaknessProfile, lang)}
              </p>
              <p className="mt-1 text-[10px] text-[var(--on-hero2)] opacity-80">
                Heute:{' '}
                {dailyStreak.isPracticedToday ? 'Übung erfasst' : 'Noch keine Übung'}
              </p>

              {recommendedBrief && (
                <p className="mt-3 rounded-2xl border border-white/10 bg-white/5 px-3.5 py-2 text-xs text-[var(--on-hero)]">
                  Passendes Dialogszenario: {recommendedBrief.situation}
                </p>
              )}
            </div>

            <div className="mt-4 flex flex-wrap gap-2">
              {recommendedUnit && (
                <button
                  type="button"
                  onClick={() =>
                    openUnit(recommendedUnit, controlDecision.primaryTaskId ?? undefined)
                  }
                  className="inline-flex items-center gap-1.5 rounded-2xl bg-[var(--accent)] px-4 py-2.5 text-xs font-extrabold text-[var(--on-accent)]"
                >
                  {hasStartedSession ? 'Weiterüben' : 'Lektion starten'}
                  <ArrowRight className="h-3.5 w-3.5" />
                </button>
              )}
              <button
                type="button"
                onClick={() => setShowAssessmentPicker((v) => !v)}
                className="inline-flex items-center gap-1.5 rounded-2xl border border-white/15 bg-white/10 px-4 py-2.5 text-xs font-bold text-[var(--on-hero)] hover:bg-white/15"
              >
                <ClipboardCheck className="h-3.5 w-3.5" />
                Übungsprüfung
              </button>
              <button
                type="button"
                onClick={() => setShowRealLifeAdd(true)}
                className="inline-flex items-center gap-1.5 rounded-2xl border border-white/15 bg-white/10 px-4 py-2.5 text-xs font-bold text-[var(--on-hero)] hover:bg-white/15"
              >
                <Plus className="h-3.5 w-3.5" />
                Alltagssituation
              </button>
            </div>
          </div>

          <div className="flex flex-col justify-between gap-2 md:col-span-5">
            <ContextIllustrationFrame
              context={controlDecision.recommendedContext ?? selectedContext}
              caption={recommendedUnit?.title}
            />
          </div>
        </div>

        {showAssessmentPicker && (
          <div className="mt-4 grid gap-2 border-t border-white/10 pt-4 sm:grid-cols-2">
            {SESSION_KINDS.map((item) => (
              <button
                key={item.kind}
                type="button"
                onClick={() => {
                  setShowAssessmentPicker(false);
                  startAssessmentSession(item.kind, true);
                }}
                className="rounded-2xl border border-white/15 bg-white/5 p-3 text-left transition hover:border-[var(--accent)]"
              >
                <p className="text-xs font-extrabold text-[var(--on-hero)]">
                  {item.label}
                </p>
                <p className="mt-0.5 text-[11px] text-[var(--on-hero2)]">{item.desc}</p>
              </button>
            ))}
          </div>
        )}
      </section>

      {/* Due Real-Life Repairs (only if logged) */}
      {realLifeExperiences.length > 0 && (
        <section className="space-y-2.5 rounded-3xl border border-[var(--line)] bg-[var(--surface)] p-5">
          <div className="flex items-center justify-between">
            <h3 className="text-sm font-extrabold text-[var(--ink)]">
              Gemeldete Alltagssituationen ({dueExperiences.length} fällig)
            </h3>
            <button
              type="button"
              onClick={() => setShowRealLifeAdd(true)}
              className="text-xs font-bold text-[var(--accent-ink)]"
            >
              + Neue Situation
            </button>
          </div>
          <div className="space-y-2">
            {realLifeExperiences.map((exp) => {
              const isDue = exp.nextReviewAt <= now;
              return (
                <div
                  key={exp.id}
                  className="flex flex-wrap items-center justify-between gap-2 rounded-2xl border border-[var(--line)] bg-[var(--surface2)] px-4 py-3 text-xs"
                >
                  <div>
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
        </section>
      )}

      {/* Compact Context Selector + Roadmap / Catalogue Switcher */}
      <div className="flex flex-wrap items-center justify-between gap-3 rounded-3xl border border-[var(--line)] bg-[var(--surface)] p-3.5">
        <div className="flex flex-wrap gap-1.5">
          {(Object.keys(CONTEXT_LABELS) as ContextTrack[]).map((ctx) => {
            const active = selectedContext === ctx;
            return (
              <button
                key={ctx}
                type="button"
                onClick={() => setSelectedContext(ctx)}
                className={`rounded-xl border px-3 py-1.5 text-xs font-bold transition ${
                  active
                    ? 'border-[var(--accent)] bg-[var(--accent-soft)] text-[var(--accent-ink)]'
                    : 'border-[var(--line)] bg-[var(--surface2)] text-[var(--ink2)] hover:text-[var(--ink)]'
                }`}
              >
                {CONTEXT_LABELS[ctx]}
              </button>
            );
          })}
        </div>

        <div className="grid grid-cols-2 gap-1 rounded-xl border border-[var(--line)] bg-[var(--surface2)] p-1 text-xs">
          <button
            type="button"
            onClick={() => setDashboardTab('ROADMAP')}
            className={`rounded-lg px-3 py-1.5 font-extrabold transition ${
              dashboardTab === 'ROADMAP'
                ? 'bg-[var(--accent)] text-[var(--on-accent)]'
                : 'text-[var(--ink2)] hover:text-[var(--ink)]'
            }`}
          >
            Roadmap
          </button>
          <button
            type="button"
            onClick={() => setDashboardTab('CATALOGUE')}
            className={`rounded-lg px-3 py-1.5 font-extrabold transition ${
              dashboardTab === 'CATALOGUE'
                ? 'bg-[var(--accent)] text-[var(--on-accent)]'
                : 'text-[var(--ink2)] hover:text-[var(--ink)]'
            }`}
          >
            Alle Stufen ({coreUnits.length})
          </button>
        </div>
      </div>

      {/* TAB A: INTERACTIVE METRO ROADMAP + ORIENTATION */}
      {dashboardTab === 'ROADMAP' && (
        <div className="c1-animate-enter space-y-5">
          <StageMetroMap
            coreUnits={coreUnits}
            stages={stages}
            attempts={attempts}
            lastUnitId={lastUnitId}
            selectedContext={selectedContext}
            onOpenUnit={(u) => openUnit(u)}
          />

          {orientationUnits.length > 0 && (
            <section className="space-y-2">
              <h3 className="text-xs font-extrabold uppercase tracking-wider text-[var(--ink2)]">
                {stages[0]}
              </h3>
              <div className="grid gap-3 sm:grid-cols-2">
                {orientationUnits.map((unit) => {
                  const blueprint = lessonVisualBlueprint(unit.id);
                  return (
                    <button
                      key={unit.id}
                      type="button"
                      onClick={() => openUnit(unit)}
                      className="flex items-center gap-3.5 rounded-2xl border border-[var(--line)] bg-[var(--surface)] p-4 text-left transition hover:border-[var(--accent)]"
                    >
                      <LessonVisualGraphic
                        graphic={blueprint.graphic}
                        activeStep={1}
                        compact
                      />
                      <div className="min-w-0 flex-1">
                        <p className="text-[11px] font-bold text-[var(--accent-ink)]">
                          Orientierung
                        </p>
                        <p className="mt-0.5 truncate text-sm font-extrabold text-[var(--ink)]">
                          {unit.title}
                        </p>
                        <p className="mt-1 line-clamp-1 text-xs text-[var(--ink2)]">
                          {unit.contexts[selectedContext]}
                        </p>
                      </div>
                    </button>
                  );
                })}
              </div>
            </section>
          )}
        </div>
      )}

      {/* TAB B: SEARCH + STAGES 1–6 CATALOGUE */}
      {dashboardTab === 'CATALOGUE' && (
        <div className="c1-animate-enter space-y-4">
          <div className="flex flex-wrap items-center gap-2 rounded-3xl border border-[var(--line)] bg-[var(--surface)] p-3.5">
            <div className="relative min-w-[200px] flex-1">
              <Search className="pointer-events-none absolute top-1/2 left-3.5 h-4 w-4 -translate-y-1/2 text-[var(--ink2)]" />
              <input
                type="search"
                value={curriculumQuery}
                onChange={(e) => setCurriculumQuery(e.target.value)}
                placeholder="Lektion, Ziel oder Themenbereich suchen..."
                className="w-full rounded-2xl border border-[var(--line)] bg-[var(--surface2)] py-2 pr-4 pl-10 text-xs text-[var(--ink)] focus:border-[var(--accent)] focus:outline-none"
              />
            </div>

            <button
              type="button"
              onClick={() => setBookmarkFilter((b) => !b)}
              className={`inline-flex items-center gap-1.5 rounded-xl border px-3 py-2 text-xs font-bold ${
                bookmarkFilter
                  ? 'border-[var(--accent)] bg-[var(--accent-soft)] text-[var(--accent-ink)]'
                  : 'border-[var(--line)] bg-[var(--surface2)] text-[var(--ink2)]'
              }`}
            >
              <Bookmark className="h-3.5 w-3.5" />
              Merkliste ({bookmarkedUnitIds.length})
            </button>
          </div>

          {/* Stages 1–6 Collapsible Lesson Sections (only in Alle Stufen tab) */}
          {[1, 2, 3, 4, 5, 6].map((stageNum) => {
        const unitsInStage = filteredCoreUnits.filter((u) => u.stage === stageNum);
        if (unitsInStage.length === 0) return null;
        const masteredInStage = unitsInStage.filter((u) =>
          MasteryEngine.isMastered(u, attempts),
        ).length;
        const stageRatio = Math.round((masteredInStage / unitsInStage.length) * 100);
        const isExpanded =
          Boolean(normalizedQuery) || bookmarkFilter || (expandedStages[stageNum] ?? false);

        return (
          <section
            key={stageNum}
            className="overflow-hidden rounded-3xl border border-[var(--line)] bg-[var(--surface)]"
          >
            <button
              type="button"
              onClick={() =>
                setExpandedStages((prev) => ({
                  ...prev,
                  [stageNum]: !(prev[stageNum] ?? false),
                }))
              }
              className="flex w-full items-center justify-between gap-4 px-5 py-4 text-left hover:bg-[var(--surface2)]/60"
            >
              <div className="min-w-0 flex-1">
                <span className="text-[10px] font-extrabold uppercase tracking-wider text-[var(--accent-ink)]">
                  Stufe {stageNum} · {unitsInStage.length} Lektionen · {masteredInStage} nachgewiesen
                </span>
                <h3 className="text-sm font-extrabold text-[var(--ink)]">
                  {stages[stageNum] ?? `Stufe ${stageNum}`}
                </h3>
                <div className="mt-2 h-1.5 w-full max-w-xs overflow-hidden rounded-full bg-[var(--surface2)]">
                  <div
                    className="h-full rounded-full bg-[var(--accent)] transition-all duration-300"
                    style={{ width: `${stageRatio}%` }}
                  />
                </div>
              </div>
              {isExpanded ? (
                <ChevronUp className="h-4 w-4 shrink-0 text-[var(--ink2)]" />
              ) : (
                <ChevronDown className="h-4 w-4 shrink-0 text-[var(--ink2)]" />
              )}
            </button>

            {isExpanded && (
              <div className="grid gap-3 border-t border-[var(--line)] p-4 md:grid-cols-2">
                {unitsInStage.map((unit) => {
                  const mastered = MasteryEngine.isMastered(unit, attempts);
                  const unlocked = MasteryEngine.isUnlocked(unit, attempts);
                  const isLast = lastUnitId === unit.id;
                  const blueprint = lessonVisualBlueprint(unit.id);
                  return (
                    <button
                      key={unit.id}
                      type="button"
                      onClick={() => openUnit(unit)}
                      className={`flex items-start gap-3.5 rounded-2xl border bg-[var(--surface2)] text-left transition hover:border-[var(--accent)] ${
                        preferences.compact ? 'p-3.5' : 'p-4'
                      } ${isLast ? 'border-[var(--accent)]' : 'border-[var(--line)]'}`}
                    >
                      <LessonVisualGraphic
                        graphic={blueprint.graphic}
                        activeStep={mastered ? 2 : unlocked ? 1 : 0}
                        compact
                      />
                      <div className="min-w-0 flex-1">
                        <div className="flex flex-wrap items-center justify-between gap-2 text-[11px]">
                          <span className="font-bold text-[var(--ink2)]">{unit.id}</span>
                          {mastered ? (
                            <span className="inline-flex items-center gap-1 font-bold text-[var(--ok)]">
                              <CheckCircle2 className="h-3.5 w-3.5" />
                              Nachgewiesen
                            </span>
                          ) : unlocked ? (
                            <span className="inline-flex items-center gap-1 font-bold text-[var(--accent-ink)]">
                              <BookOpen className="h-3.5 w-3.5" />
                              Aktiv
                            </span>
                          ) : (
                            <span className="inline-flex items-center gap-1 font-semibold text-[var(--ink2)]">
                              <Lock className="h-3.5 w-3.5" />
                              Offen · übbar
                            </span>
                          )}
                        </div>
                        <h4 className="mt-0.5 text-sm font-extrabold text-[var(--ink)]">
                          {unit.title}
                        </h4>
                        <p className="mt-0.5 truncate text-[11px] font-semibold text-[var(--accent-ink)]">
                          {blueprint.captionDe}
                        </p>
                      </div>
                    </button>
                  );
                })}
              </div>
            )}
          </section>
        );
      })}
        </div>
      )}

      {/* Quiet Optional Specialty Labs Drawer (no top-level menu clutter) */}
      <details className="rounded-2xl border border-[var(--line)] bg-[var(--surface)] px-4 py-3 text-xs">
        <summary className="cursor-pointer font-bold text-[var(--ink2)] hover:text-[var(--ink)]">
          Optionale Zusatzlabore (Hören, Sprechen, Mediation, Fußball-U15)
        </summary>
        <div className="mt-3 flex flex-wrap gap-2">
          {(
            [
              { screen: 'ListeningLab', label: 'Hör-Labor' },
              { screen: 'SpeakingLab', label: 'Aussprache-Labor' },
              { screen: 'ConversationSimulator', label: 'Sprech-Simulator' },
              { screen: 'Mediation', label: 'Sprachmittlung' },
              { screen: 'FootballCoachTrack', label: 'Fußball-Trainer (U15)' },
            ] as const
          ).map((item) => (
            <button
              key={item.screen}
              type="button"
              onClick={() => navigateTo(item.screen)}
              className="rounded-xl border border-[var(--line)] bg-[var(--surface2)] px-3 py-1.5 text-xs font-bold text-[var(--ink)] hover:border-[var(--accent)]"
            >
              {item.label}
            </button>
          ))}
        </div>
      </details>

      {showOnboarding && <OnboardingModal onClose={() => setShowOnboarding(false)} />}
      {showPreferences && <PreferencesModal onClose={() => setShowPreferences(false)} />}
      {showRealLifeAdd && (
        <RealLifeAddModal
          defaultContext={selectedContext}
          onClose={() => setShowRealLifeAdd(false)}
        />
      )}
      {reviewingRealLife && (
        <RealLifeReviewModal
          experience={reviewingRealLife}
          onClose={() => setReviewingRealLife(null)}
        />
      )}
    </div>
  );
};
