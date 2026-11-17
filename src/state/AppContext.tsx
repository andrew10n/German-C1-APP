/* eslint-disable react-refresh/only-export-components */
import React, {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useRef,
  useState,
} from 'react';
import type {
  AdaptiveLessonAddition,
  Attempt,
  AssessmentSessionKind,
  BaselineProfileRecord,
  BookAudioSelection,
  ContextTrack,
  ConversationScenario,
  CrossCuttingCompetence,
  DailyStreakState,
  DailyTrainingSession,
  DiagnosticMode,
  DiagnosticTask,
  DiagnosticUserAnswer,
  Dimension,
  DualBenchmark,
  ErrorIntelligenceItem,
  EvidenceSource,
  ExternalListeningDraft,
  FeedbackChallenge,
  FeedbackChallengeDecision,
  FeedbackChallengeStatus,
  FeedbackClaim,
  HumanReviewSubmission,
  ListeningLaboratoryItem,
  MasteryStage,
  MediationTask,
  PersistedAssessment,
  PracticeDraft,
  ProficiencySnapshot,
  ProgrammePreferences,
  ProgrammeQuizResult,
  PronunciationAnalysisResult,
  RealLifeExperience,
  RealLifeIssueType,
  ReassessmentLink,
  ReviewAssignment,
  ReviewAuditEvent,
  Screen,
  SessionPhase,
  SkillCategory,
  SkillScore,
  SpeakingPrompt,
  TrainingPriorityRecommendation,
  VocabularySrsItem,
} from '../types';
import {
  baseConversations,
  createInitialSkillProfile,
  createInitialSrsVocabulary,
  diagnosticTasksFull,
  diagnosticTasksGrammarVocab,
  initialCrossCutting,
  initialDualBenchmark,
  initialErrors,
  initialListeningLab,
  initialMediationTasks,
  speakingPrompts as initialSpeakingPrompts,
} from '../data/learningData';
import { curriculumUnits } from '../data/curriculumCatalogue';
import { AdaptiveLearning } from '../engines/adaptiveLearning';
import { AssessmentDeliveryEngine } from '../engines/assessmentDelivery';
import { ConversationVariationEngine } from '../engines/conversationVariationEngine';
import { generateDailyTrainingSession } from '../engines/dailySessionGenerator';
import {
  DiagnosticEvaluationResult,
  evaluateDiagnosticScreening,
} from '../engines/diagnosticEvaluator';
import { FeedbackEvidenceEngine } from '../engines/feedbackEvidenceEngine';
import { MasteryEngine } from '../engines/masteryEngine';
import { ProgrammeExport } from '../engines/programmeExport';
import { PronunciationAnalysisEngine } from '../engines/pronunciationAnalysisEngine';
import { RetrievalPolicy } from '../engines/retrievalPolicy';
import {
  createEmptyStreakState,
  hydrateStreakState,
  recordStreakActivity,
} from '../engines/streakEngine';
import { TrainingPriorityEngine } from '../engines/trainingPriorityEngine';
import { speechService } from '../services/speechService';

const STORAGE_DARK_KEY = 'c1_is_dark';
const STORAGE_PROGRAMME_KEY = 'c1_programme_state_v2';
const STORAGE_LABS_KEY = 'c1_labs_state_v2';

const MASTERY_ORDER: MasteryStage[] = [
  'NEU',
  'WIEDERHOLUNG_TAG_1',
  'TRANSFER_TAG_3',
  'GEFESTIGT_TAG_7',
  'GEMEISTERT',
];

const DEFAULT_PREFERENCES: ProgrammePreferences = {
  learnerName: '',
  theme: 'TRACKSIDE',
  language: 'DE',
  dailyMinutes: 20,
  compact: false,
};

interface PersistedProgrammeData {
  preferences: ProgrammePreferences;
  attempts: Attempt[];
  realLifeExperiences: RealLifeExperience[];
  assessments: PersistedAssessment[];
  feedbackChallenges: FeedbackChallenge[];
  humanReviews: HumanReviewSubmission[];
  reviewAssignments: ReviewAssignment[];
  reviewAuditEvents: ReviewAuditEvent[];
  reassessmentLinks: ReassessmentLink[];
  externalListeningDrafts: ExternalListeningDraft[];
  practiceDrafts: PracticeDraft[];
  assistedUnitIds: string[];
  bookmarkedUnitIds: string[];
  lastUnitId: string | null;
  lastTaskId: string;
  onboardingComplete: boolean;
  quizResults: Record<string, ProgrammeQuizResult>;
}

function defaultProgrammeData(): PersistedProgrammeData {
  return {
    preferences: { ...DEFAULT_PREFERENCES },
    attempts: [],
    realLifeExperiences: [],
    assessments: [],
    feedbackChallenges: [],
    humanReviews: [],
    reviewAssignments: [],
    reviewAuditEvents: [],
    reassessmentLinks: [],
    externalListeningDrafts: [],
    practiceDrafts: [],
    assistedUnitIds: [],
    bookmarkedUnitIds: [],
    lastUnitId: null,
    lastTaskId: 'reading',
    onboardingComplete: false,
    quizResults: {},
  };
}

interface PersistedLabsData {
  dailyStreak: DailyStreakState;
  srsVocabulary: VocabularySrsItem[];
  errorMasteryOverrides: Record<string, MasteryStage>;
  completedDrillIds: string[];
  baselineProfile: BaselineProfileRecord | null;
  proficiencySnapshots: ProficiencySnapshot[];
  sessionRotationIndex: number;
}

function defaultLabsData(): PersistedLabsData {
  return {
    dailyStreak: createEmptyStreakState(),
    srsVocabulary: createInitialSrsVocabulary(),
    errorMasteryOverrides: {},
    completedDrillIds: [],
    baselineProfile: null,
    proficiencySnapshots: [],
    sessionRotationIndex: 0,
  };
}

function loadJsonStorage<T>(key: string, fallback: () => T): T {
  try {
    const raw = localStorage.getItem(key);
    if (!raw) return fallback();
    return JSON.parse(raw) as T;
  } catch {
    return fallback();
  }
}

export interface DiagnosticUiState {
  status: 'IDLE' | 'IN_PROGRESS' | 'EVALUATING' | 'COMPLETED';
  mode: DiagnosticMode;
  tasks: DiagnosticTask[];
  currentTaskIndex: number;
  answers: Record<number, DiagnosticUserAnswer>;
  evaluationResult: DiagnosticEvaluationResult | null;
}

interface AppContextValue {
  // Theme & Navigation
  isDark: boolean;
  toggleTheme: () => void;
  currentScreen: Screen;
  navigateTo: (screen: Screen) => void;
  navigateBack: () => boolean;
  canNavigateBack: boolean;
  pendingUnitOpen: { unitId: string; context?: ContextTrack } | null;
  openUnitInProgramme: (unitId: string, context?: ContextTrack) => void;
  clearPendingUnitOpen: () => void;

  // Programme State & Actions
  preferences: ProgrammePreferences;
  updatePreferences: (patch: Partial<ProgrammePreferences>) => void;
  attempts: Attempt[];
  realLifeExperiences: RealLifeExperience[];
  assessments: PersistedAssessment[];
  activeAssessmentId: string | null;
  setActiveAssessmentId: (id: string | null) => void;
  feedbackChallenges: FeedbackChallenge[];
  humanReviews: HumanReviewSubmission[];
  reviewAssignments: ReviewAssignment[];
  reviewAuditEvents: ReviewAuditEvent[];
  reassessmentLinks: ReassessmentLink[];
  externalListeningDrafts: ExternalListeningDraft[];
  practiceDrafts: PracticeDraft[];
  assistedUnitIds: string[];
  bookmarkedUnitIds: string[];
  lastUnitId: string | null;
  lastTaskId: string;
  onboardingComplete: boolean;
  quizResults: Record<string, ProgrammeQuizResult>;
  bookAudio: BookAudioSelection;
  getBookAudio: (selectionKey?: string) => BookAudioSelection;
  programmeNotice: string | null;
  programmeError: string | null;
  clearProgrammeNotice: () => void;
  clearProgrammeError: () => void;
  setProgrammeNotice: (msg: string | null) => void;
  setProgrammeError: (msg: string | null) => void;
  completeOnboarding: () => void;
  setLastLocation: (unitId: string | null, taskId?: string) => void;
  toggleBookmark: (unitId: string) => void;
  markUnitAssisted: (unitId: string) => void;
  submitUnitQuiz: (
    unitId: string,
    context: ContextTrack,
    selectedOption: number,
    correctOption: number,
  ) => void;
  recordPracticeAttempt: (input: {
    unitId: string;
    taskId: string;
    response: string;
    context: ContextTrack;
    assisted?: boolean;
    unfamiliar?: boolean;
    independentProduction?: boolean;
    errors?: string[];
    scores?: Partial<Record<Dimension, number>>;
    source?: EvidenceSource;
  }) => Attempt;
  savePracticeDraft: (
    unitId: string,
    taskId: string,
    context: ContextTrack,
    response: string,
  ) => void;
  saveAdaptiveAddition: (
    unitId: string,
    addition: AdaptiveLessonAddition,
    response: string,
  ) => Attempt | null;
  startAssessmentSession: (
    kind: AssessmentSessionKind,
    resumeExisting?: boolean,
  ) => PersistedAssessment;
  updateAssessmentResponse: (sessionId: string, key: string, value: string) => void;
  setAssessmentIndex: (sessionId: string, index: number) => void;
  completeAssessmentSession: (sessionId: string) => void;
  addRealLifeExperience: (input: {
    context: ContextTrack;
    situation: string;
    heard: string;
    intended: string;
    said: string;
    issueType: RealLifeIssueType;
  }) => RealLifeExperience;
  completeRealLifeReview: (experienceId: string, reviewResponse: string) => void;
  createFeedbackChallenge: (
    claim: FeedbackClaim,
    decision: FeedbackChallengeDecision,
    correctedFeedback: string | null,
  ) => void;
  resolveFeedbackChallenge: (
    challengeId: string,
    status: FeedbackChallengeStatus,
    correctedFeedback?: string | null,
  ) => void;
  submitHumanReview: (input: {
    attemptId: string;
    reviewerName: string;
    verified: boolean;
    scores: Partial<Record<Dimension, number>>;
    comment: string;
    assignmentId?: string;
  }) => void;
  createReviewAssignment: (attemptId: string, reviewerName: string, dueDays?: number) => void;
  createReassessmentLink: (
    sourceAttemptId: string,
    sourceReviewId: string | null,
    unitId: string,
    taskId: string,
  ) => void;
  upsertExternalListeningDraft: (draft: Omit<ExternalListeningDraft, 'updatedAt'>) => void;
  setBookAudioFile: (file: File | null, selectionKey?: string) => void;
  clearBookAudio: (selectionKey?: string) => void;
  exportLocalJson: () => void;
  deleteAllLocalData: () => void;

  // C1 Training Labs, SRS, Streak & Diagnostics
  dailyStreak: DailyStreakState;
  recordPracticeActivity: (activityName?: string) => void;
  srsVocabulary: VocabularySrsItem[];
  recordSrsReview: (itemId: string, qualityScore: number) => VocabularySrsItem | null;
  addCustomSrsVocabulary: (input: {
    germanCollocation: string;
    meaningDe: string;
    exampleSentence: string;
    register?: string;
    category?: string;
  }) => void;
  deleteSrsVocabulary: (id: string) => void;
  seedCurriculumExpressions: (unitId: string, expressions: string[]) => number;
  skillScores: Record<SkillCategory, SkillScore>;
  crossCutting: CrossCuttingCompetence[];
  dualBenchmark: DualBenchmark;
  topPriority: TrainingPriorityRecommendation;
  errorDatabase: ErrorIntelligenceItem[];
  advanceErrorStage: (errorId: string) => void;
  listeningItems: ListeningLaboratoryItem[];
  conversationScenarios: ConversationScenario[];
  mediationTasks: MediationTask[];
  speakingPrompts: SpeakingPrompt[];

  // DailySession Runner
  activeSession: DailyTrainingSession | null;
  currentPhase: SessionPhase;
  warmUpSelectedOption: number | null;
  warmUpAnswerRevealed: boolean;
  productionUserInput: string;
  transferUserInput: string;
  sessionCompleted: boolean;
  startDailySession: () => void;
  selectWarmUpOption: (idx: number) => void;
  revealWarmUpAnswer: () => void;
  advanceSessionPhase: () => void;
  updateProductionInput: (text: string) => void;
  updateTransferInput: (text: string) => void;

  // ListeningLab State
  selectedLabItemIndex: number;
  playbackSpeed: number;
  isBlindTranscriptRevealed: boolean;
  selectedLabQuizAnswers: Record<number, number>;
  selectLabItem: (idx: number) => void;
  setPlaybackSpeed: (speed: number) => void;
  revealBlindTranscript: () => void;
  answerLabQuestion: (questionIdx: number, answerIdx: number) => void;

  // SpeakingLab State
  currentSpeakingPromptIndex: number;
  setCurrentSpeakingPromptIndex: (idx: number) => void;
  latestPronunciationResult: PronunciationAnalysisResult | null;
  analyzeSpokenResponse: (
    prompt: SpeakingPrompt,
    transcribedText: string,
    confidence?: number,
  ) => PronunciationAnalysisResult;
  clearPronunciationResult: () => void;

  // Conversation Simulator State
  activeConversationIndex: number;
  currentConversationTurnIndex: number;
  interruptionTriggered: boolean;
  conversationUserResponse: string;
  responseLatencySeconds: number;
  conversationSubmitted: boolean;
  selectConversation: (idx: number) => void;
  playOpeningStatement: () => void;
  updateConversationResponse: (text: string) => void;
  submitConversationResponse: (text: string) => void;
  nextConversationTurn: () => void;

  // ErrorIntelligence Drill State
  selectedErrorForDrill: ErrorIntelligenceItem | null;
  errorDrillUserAnswer: string;
  errorDrillFeedback: string | null;
  selectErrorForDrill: (error: ErrorIntelligenceItem) => void;
  clearSelectedErrorForDrill: () => void;
  updateErrorDrillAnswer: (text: string) => void;
  submitErrorDrillAnswer: () => void;

  // Baseline Diagnostic State
  diagnosticUiState: DiagnosticUiState;
  startDiagnosticAssessment: (mode: DiagnosticMode) => void;
  recordDiagnosticAnswer: (selectedIndex: number) => void;
  nextDiagnosticTask: () => void;
  previousDiagnosticTask: () => void;
  resetDiagnosticAssessment: () => void;

  // Football Coach Track
  completedDrillIds: string[];
  toggleDrillCompleted: (drillId: string) => void;
}

const AppContext = createContext<AppContextValue | null>(null);

export const AppProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [isDark, setIsDark] = useState<boolean>(() => {
    try {
      const saved = localStorage.getItem(STORAGE_DARK_KEY);
      return saved === null ? true : saved === 'true';
    } catch {
      return true;
    }
  });

  const [programme, setProgramme] = useState<PersistedProgrammeData>(() => {
    const loaded = loadJsonStorage(STORAGE_PROGRAMME_KEY, defaultProgrammeData);
    return {
      ...defaultProgrammeData(),
      ...loaded,
      preferences: { ...DEFAULT_PREFERENCES, ...(loaded.preferences ?? {}) },
    };
  });

  const [labs, setLabs] = useState<PersistedLabsData>(() => {
    const loaded = loadJsonStorage(STORAGE_LABS_KEY, defaultLabsData);
    const hydratedStreak = loaded.dailyStreak
      ? hydrateStreakState({
          currentStreak: loaded.dailyStreak.currentStreak ?? 0,
          longestStreak: loaded.dailyStreak.longestStreak ?? 0,
          lastActiveEpochDay: loaded.dailyStreak.lastActiveEpochDay ?? 0,
          streakFreezesAvailable: loaded.dailyStreak.streakFreezesAvailable ?? 0,
          totalSessionsCompleted: loaded.dailyStreak.totalSessionsCompleted ?? 0,
          activeDaysBitmask: loaded.dailyStreak.activeDaysBitmask ?? 0,
          lastActivityTimestamp: loaded.dailyStreak.lastActivityTimestamp ?? 0,
        })
      : createEmptyStreakState();

    const defaultSrs = createInitialSrsVocabulary();
    const srs =
      Array.isArray(loaded.srsVocabulary) && loaded.srsVocabulary.length > 0
        ? [
            ...loaded.srsVocabulary,
            ...defaultSrs.filter(
              (item) =>
                !loaded.srsVocabulary!.some((existing) => existing.id === item.id),
            ),
          ]
        : defaultSrs;

    return {
      ...defaultLabsData(),
      ...loaded,
      dailyStreak: hydratedStreak,
      srsVocabulary: srs,
    };
  });

  // Navigation stack
  const [screenStack, setScreenStack] = useState<Screen[]>(['Programme']);
  const currentScreen = screenStack[screenStack.length - 1];

  const [activeAssessmentId, setActiveAssessmentId] = useState<string | null>(null);
  const [pendingUnitOpen, setPendingUnitOpen] = useState<{
    unitId: string;
    context?: ContextTrack;
  } | null>(null);
  const [bookAudioMap, setBookAudioMap] = useState<Record<string, BookAudioSelection>>({});
  const bookAudioUrlsRef = useRef<Record<string, string>>({});
  const bookAudio: BookAudioSelection = bookAudioMap.__default__ ?? {
    displayName: null,
    objectUrl: null,
  };
  const getBookAudio = useCallback(
    (selectionKey?: string): BookAudioSelection =>
      bookAudioMap[selectionKey ?? '__default__'] ?? {
        displayName: null,
        objectUrl: null,
      },
    [bookAudioMap],
  );

  const [programmeNotice, setProgrammeNotice] = useState<string | null>(null);
  const [programmeError, setProgrammeError] = useState<string | null>(null);

  const clearProgrammeNotice = useCallback(() => setProgrammeNotice(null), []);
  const clearProgrammeError = useCallback(() => setProgrammeError(null), []);

  // Sync dark mode & theme palette to <html>
  useEffect(() => {
    const root = document.documentElement;
    root.classList.toggle('dark', isDark);
    root.dataset.palette = programme.preferences.theme;
    try {
      localStorage.setItem(STORAGE_DARK_KEY, String(isDark));
      localStorage.setItem('c1_palette', programme.preferences.theme);
    } catch {
      // ignore storage quota
    }
  }, [isDark, programme.preferences.theme]);

  // Persist programme & labs data to localStorage
  useEffect(() => {
    try {
      localStorage.setItem(STORAGE_PROGRAMME_KEY, JSON.stringify(programme));
    } catch {
      // ignore
    }
  }, [programme]);

  useEffect(() => {
    try {
      localStorage.setItem(STORAGE_LABS_KEY, JSON.stringify(labs));
    } catch {
      // ignore
    }
  }, [labs]);

  // Cleanup book audio ObjectURLs on unmount
  useEffect(() => {
    const urlsRef = bookAudioUrlsRef;
    return () => {
      Object.values(urlsRef.current).forEach((u) => URL.revokeObjectURL(u));
    };
  }, []);

  const toggleTheme = useCallback(() => {
    setIsDark((prev) => !prev);
  }, []);

  const navigateTo = useCallback((screen: Screen) => {
    speechService.stopSpeaking();
    speechService.stopRecognition();
    setScreenStack((prev) => {
      if (prev[prev.length - 1] === screen) return prev;
      return [...prev, screen];
    });
  }, []);

  const navigateBack = useCallback((): boolean => {
    speechService.stopSpeaking();
    speechService.stopRecognition();
    let didNavigate = false;
    setScreenStack((prev) => {
      if (prev.length > 1) {
        didNavigate = true;
        return prev.slice(0, -1);
      }
      return prev;
    });
    return didNavigate;
  }, []);

  const openUnitInProgramme = useCallback(
    (unitId: string, context?: ContextTrack) => {
      setPendingUnitOpen({ unitId, context });
      setProgramme((prev) => ({ ...prev, lastUnitId: unitId }));
      navigateTo('Programme');
    },
    [navigateTo],
  );

  const clearPendingUnitOpen = useCallback(() => {
    setPendingUnitOpen(null);
  }, []);

  // Derived data
  const skillScores = useMemo(() => createInitialSkillProfile(), []);
  const crossCutting = useMemo(() => initialCrossCutting, []);
  const dualBenchmark = useMemo(() => initialDualBenchmark, []);
  const listeningItems = useMemo(() => initialListeningLab, []);
  const conversationScenarios = useMemo(
    () => ConversationVariationEngine.expand(baseConversations),
    [],
  );
  const mediationTasks = useMemo(() => initialMediationTasks, []);
  const speakingPrompts = useMemo(() => initialSpeakingPrompts, []);

  const errorDatabase = useMemo(
    () =>
      initialErrors.map((err) => ({
        ...err,
        masteryStage: labs.errorMasteryOverrides[err.id] ?? err.masteryStage,
      })),
    [labs.errorMasteryOverrides],
  );

  const topPriority = useMemo(
    () =>
      TrainingPriorityEngine.getTopTrainingPriority(
        false,
        skillScores,
        errorDatabase,
      ),
    [skillScores, errorDatabase],
  );

  const recordPracticeActivity = useCallback((activityName = 'C1 Training') => {
    void activityName;
    setLabs((prev) => ({
      ...prev,
      dailyStreak: recordStreakActivity(prev.dailyStreak, Date.now()),
    }));
  }, []);

  // Programme Actions
  const updatePreferences = useCallback((patch: Partial<ProgrammePreferences>) => {
    setProgramme((prev) => ({
      ...prev,
      preferences: { ...prev.preferences, ...patch },
    }));
  }, []);

  const completeOnboarding = useCallback(() => {
    setProgramme((prev) => ({ ...prev, onboardingComplete: true }));
  }, []);

  const setLastLocation = useCallback((unitId: string | null, taskId?: string) => {
    setProgramme((prev) => ({
      ...prev,
      lastUnitId: unitId,
      lastTaskId: taskId ?? prev.lastTaskId,
    }));
  }, []);

  const toggleBookmark = useCallback((unitId: string) => {
    setProgramme((prev) => {
      const exists = prev.bookmarkedUnitIds.includes(unitId);
      return {
        ...prev,
        bookmarkedUnitIds: exists
          ? prev.bookmarkedUnitIds.filter((id) => id !== unitId)
          : [...prev.bookmarkedUnitIds, unitId],
      };
    });
  }, []);

  const markUnitAssisted = useCallback((unitId: string) => {
    setProgramme((prev) =>
      prev.assistedUnitIds.includes(unitId)
        ? prev
        : { ...prev, assistedUnitIds: [...prev.assistedUnitIds, unitId] },
    );
  }, []);

  const submitUnitQuiz = useCallback(
    (
      unitId: string,
      context: ContextTrack,
      selectedOption: number,
      correctOption: number,
    ) => {
      const now = Date.now();
      const isCorrect = selectedOption === correctOption;
      const key = `${unitId}:${context}`;
      const assisted = programme.assistedUnitIds.includes(unitId);

      const attempt: Attempt = {
        id: `quiz-${unitId}-${now}-${Math.random().toString(36).slice(2, 7)}`,
        unitId,
        taskId: 'quiz',
        timestamp: now,
        response: `Option ${selectedOption + 1}`,
        source: 'AUTOMATIC_QUIZ',
        scores: { COMPREHENSION: isCorrect ? 4 : 1 },
        assisted,
        unfamiliar: false,
        independentProduction: false,
        context,
        errors: isCorrect ? [] : ['Verständnisprüfung wiederholen'],
      };

      setProgramme((prev) => ({
        ...prev,
        quizResults: {
          ...prev.quizResults,
          [key]: { option: selectedOption, correct: isCorrect, assisted },
        },
        attempts: [...prev.attempts, attempt],
        lastUnitId: unitId,
        lastTaskId: 'quiz',
      }));
      recordPracticeActivity('Verständnischeck');
    },
    [programme.assistedUnitIds, recordPracticeActivity],
  );

  const recordPracticeAttempt = useCallback(
    (input: {
      unitId: string;
      taskId: string;
      response: string;
      context: ContextTrack;
      assisted?: boolean;
      unfamiliar?: boolean;
      independentProduction?: boolean;
      errors?: string[];
      scores?: Partial<Record<Dimension, number>>;
      source?: EvidenceSource;
    }): Attempt => {
      const now = Date.now();
      const unitObj = curriculumUnits.find((u) => u.id === input.unitId);
      const detectedErrors =
        input.errors ??
        (unitObj
          ? AdaptiveLearning.detectResponseSignals(input.response, unitObj).map(
              (err) => `${err.code}: ${err.explanation}`,
            )
          : []);
      const attempt: Attempt = {
        id: `att-${now}-${Math.random().toString(36).slice(2, 8)}`,
        unitId: input.unitId,
        taskId: input.taskId,
        timestamp: now,
        response: input.response.trim(),
        source: input.source ?? 'SELF_REFLECTION',
        scores: input.scores ?? {},
        assisted: input.assisted ?? programme.assistedUnitIds.includes(input.unitId),
        unfamiliar: input.unfamiliar ?? false,
        independentProduction: input.independentProduction ?? true,
        context: input.context,
        errors: detectedErrors,
        taskVersion: 1,
        rubricVersion: 1,
      };
      setProgramme((prev) => ({
        ...prev,
        attempts: [...prev.attempts, attempt],
        lastUnitId: input.unitId === MasteryEngine.GRADUATION_UNIT_ID ? prev.lastUnitId : input.unitId,
        lastTaskId: input.taskId,
      }));
      recordPracticeActivity('Programme Practice');
      setProgrammeNotice(
        'Als Selbstreflexion gespeichert – keine automatische Bewertung und kein Kompetenznachweis.',
      );
      return attempt;
    },
    [programme.assistedUnitIds, recordPracticeActivity],
  );

  const savePracticeDraft = useCallback(
    (unitId: string, taskId: string, context: ContextTrack, response: string) => {
      const now = Date.now();
      setProgramme((prev) => {
        const filtered = prev.practiceDrafts.filter(
          (d) => !(d.unitId === unitId && d.taskId === taskId && d.context === context),
        );
        return {
          ...prev,
          practiceDrafts:
            response.length === 0
              ? filtered
              : [...filtered, { unitId, taskId, context, response, updatedAt: now }],
        };
      });
    },
    [],
  );

  const startAssessmentSession = useCallback(
    (kind: AssessmentSessionKind, resumeExisting = true): PersistedAssessment => {
      if (resumeExisting) {
        const existing = programme.assessments.find(
          (a) => a.session.kind === kind && !a.completed,
        );
        if (existing) {
          setActiveAssessmentId(existing.session.id);
          return existing;
        }
      }
      const now = Date.now();
      const session = AssessmentDeliveryEngine.createSession(kind, now);
      const persisted: PersistedAssessment = {
        session,
        responses: {},
        currentIndex: 0,
        completed: false,
        updatedAt: now,
      };
      setProgramme((prev) => ({
        ...prev,
        assessments: [persisted, ...prev.assessments],
      }));
      setActiveAssessmentId(persisted.session.id);
      return persisted;
    },
    [programme.assessments],
  );

  const updateAssessmentResponse = useCallback(
    (sessionId: string, key: string, value: string) => {
      const now = Date.now();
      setProgramme((prev) => ({
        ...prev,
        assessments: prev.assessments.map((item) =>
          item.session.id === sessionId
            ? {
                ...item,
                responses: { ...item.responses, [key]: value },
                updatedAt: now,
              }
            : item,
        ),
      }));
    },
    [],
  );

  const setAssessmentIndex = useCallback((sessionId: string, index: number) => {
    setProgramme((prev) => ({
      ...prev,
      assessments: prev.assessments.map((item) =>
        item.session.id === sessionId
          ? {
              ...item,
              currentIndex: Math.max(0, Math.min(item.session.tasks.length - 1, index)),
              updatedAt: Date.now(),
            }
          : item,
      ),
    }));
  }, []);

  const completeAssessmentSession = useCallback(
    (sessionId: string) => {
      const target = programme.assessments.find((a) => a.session.id === sessionId);
      if (!target) return;
      const now = Date.now();
      const newAttempts: Attempt[] = [];
      for (const task of target.session.tasks) {
        const resp = (target.responses[task.id] ?? '').trim();
        const audioNote = (target.responses[`assessment-audio:${task.id}`] ?? '').trim();
        const combined = [resp, audioNote ? `[Audio-Notiz: ${audioNote}]` : '']
          .filter(Boolean)
          .join('\n');
        if (combined.length > 0) {
          newAttempts.push({
            id: `assess-${sessionId}-${task.id}-${now}`,
            unitId: task.unitId,
            taskId: task.taskId,
            timestamp: now,
            response: combined,
            source: 'SELF_REFLECTION',
            scores: {},
            assisted: false,
            unfamiliar: task.unfamiliar,
            independentProduction: true,
            context: task.context,
            errors: [],
            taskVersion: task.taskVersion,
            rubricVersion: task.rubricVersion,
          });
        }
      }
      setProgramme((prev) => ({
        ...prev,
        assessments: prev.assessments.map((item) =>
          item.session.id === sessionId
            ? { ...item, completed: true, updatedAt: now }
            : item,
        ),
        attempts: [...prev.attempts, ...newAttempts],
      }));
      setActiveAssessmentId(null);
      setProgrammeNotice(
        'Übungsprüfung lokal gespeichert. Ohne menschliche Rubrikprüfung erfolgt keine Kompetenz- oder GER-Bewertung.',
      );
      recordPracticeActivity('Unabhängiger Übungslauf');
    },
    [programme.assessments, recordPracticeActivity],
  );

  const addRealLifeExperience = useCallback(
    (input: {
      context: ContextTrack;
      situation: string;
      heard: string;
      intended: string;
      said: string;
      issueType: RealLifeIssueType;
    }): RealLifeExperience => {
      const now = Date.now();
      const exp: RealLifeExperience = {
        id: `real-${now}-${Math.random().toString(36).slice(2, 7)}`,
        createdAt: now,
        context: input.context,
        situation: input.situation.trim(),
        heard: input.heard.trim(),
        intended: input.intended.trim(),
        said: input.said.trim(),
        issueType: input.issueType,
        reviewStage: 0,
        nextReviewAt: now,
        reviewResponse: '',
      };
      setProgramme((prev) => ({
        ...prev,
        realLifeExperiences: [exp, ...prev.realLifeExperiences],
      }));
      setProgrammeNotice('Alltagssituation gespeichert und sofort zur Reparatur eingereiht.');
      return exp;
    },
    [],
  );

  const completeRealLifeReview = useCallback(
    (experienceId: string, reviewResponse: string) => {
      const cleaned = reviewResponse.trim();
      if (!cleaned) return;
      const now = Date.now();
      setProgramme((prev) => ({
        ...prev,
        realLifeExperiences: prev.realLifeExperiences.map((exp) => {
          if (exp.id !== experienceId) return exp;
          const nextStage = exp.reviewStage + 1;
          const intervalIndex = Math.min(
            RetrievalPolicy.intervalsDays.length - 1,
            nextStage - 1,
          );
          const nextDays = RetrievalPolicy.intervalsDays[intervalIndex];
          return {
            ...exp,
            reviewStage: nextStage,
            nextReviewAt: now + nextDays * MasteryEngine.DAY_MILLIS,
            reviewResponse: cleaned,
          };
        }),
      }));
      recordPracticeActivity('Alltagsreparatur');
      setProgrammeNotice('Reparatur und Transferantwort gespeichert.');
    },
    [recordPracticeActivity],
  );

  const saveAdaptiveAddition = useCallback(
    (
      unitId: string,
      addition: AdaptiveLessonAddition,
      response: string,
    ): Attempt | null => {
      const cleaned = response.trim();
      if (!cleaned) return null;
      const attempt = recordPracticeAttempt({
        unitId,
        taskId: addition.taskId,
        response: cleaned,
        context: addition.context,
        unfamiliar: addition.taskId === 'transfer',
      });
      savePracticeDraft(unitId, addition.taskId, addition.context, '');
      if (addition.reviewExperienceId) {
        completeRealLifeReview(addition.reviewExperienceId, cleaned);
      }
      return attempt;
    },
    [completeRealLifeReview, recordPracticeAttempt, savePracticeDraft],
  );

  const createFeedbackChallenge = useCallback(
    (
      claim: FeedbackClaim,
      decision: FeedbackChallengeDecision,
      correctedFeedback: string | null,
    ) => {
      const now = Date.now();
      try {
        const challenge = FeedbackEvidenceEngine.challenge(
          claim,
          decision,
          correctedFeedback,
          now,
        );
        setProgramme((prev) => ({
          ...prev,
          feedbackChallenges: [
            challenge,
            ...prev.feedbackChallenges.filter((c) => c.id !== challenge.id),
          ],
        }));
        setProgrammeNotice('Rückmeldung als Einspruch protokolliert.');
      } catch (err) {
        setProgrammeError(err instanceof Error ? err.message : 'Einspruch fehlgeschlagen.');
      }
    },
    [],
  );

  const resolveFeedbackChallenge = useCallback(
    (
      challengeId: string,
      status: FeedbackChallengeStatus,
      correctedFeedback?: string | null,
    ) => {
      const now = Date.now();
      setProgramme((prev) => ({
        ...prev,
        feedbackChallenges: prev.feedbackChallenges.map((c) =>
          c.id === challengeId
            ? {
                ...c,
                status,
                correctedFeedback:
                  correctedFeedback !== undefined ? correctedFeedback : c.correctedFeedback,
                updatedAt: now,
              }
            : c,
        ),
      }));
    },
    [],
  );

  const createReviewAssignment = useCallback(
    (attemptId: string, reviewerName: string, dueDays = 3) => {
      const now = Date.now();
      const assignment: ReviewAssignment = {
        id: `assign-${now}-${Math.random().toString(36).slice(2, 6)}`,
        attemptId,
        reviewerId: `reviewer-${reviewerName.toLowerCase().replace(/\s+/g, '-') || 'local'}`,
        reviewerName: reviewerName.trim() || 'Menschliche Prüfung',
        status: 'ASSIGNED',
        createdAt: now,
        dueAt: now + dueDays * MasteryEngine.DAY_MILLIS,
        completedAt: null,
      };
      const audit: ReviewAuditEvent = {
        id: `audit-${now}-${Math.random().toString(36).slice(2, 6)}`,
        assignmentId: assignment.id,
        reviewId: null,
        actorId: 'learner',
        action: 'ASSIGNED',
        details: `Prüfauftrag an ${assignment.reviewerName} erstellt`,
        createdAt: now,
      };
      setProgramme((prev) => ({
        ...prev,
        reviewAssignments: [assignment, ...prev.reviewAssignments],
        reviewAuditEvents: [audit, ...prev.reviewAuditEvents],
      }));
      setProgrammeNotice('Prüfauftrag angelegt.');
    },
    [],
  );

  const submitHumanReview = useCallback(
    (input: {
      attemptId: string;
      reviewerName: string;
      verified: boolean;
      scores: Partial<Record<Dimension, number>>;
      comment: string;
      assignmentId?: string;
    }) => {
      const now = Date.now();
      const reviewId = `rev-${now}-${Math.random().toString(36).slice(2, 6)}`;
      const review: HumanReviewSubmission = {
        id: reviewId,
        attemptId: input.attemptId,
        reviewer: {
          id: `reviewer-${input.reviewerName.toLowerCase().replace(/\s+/g, '-') || 'local'}`,
          displayName: input.reviewerName.trim() || 'Prüfende Person',
          verified: input.verified,
        },
        rubricVersion: 1,
        scores: input.scores,
        comment: input.comment.trim(),
        createdAt: now,
      };

      const audit: ReviewAuditEvent = {
        id: `audit-${now}-${Math.random().toString(36).slice(2, 6)}`,
        assignmentId: input.assignmentId ?? 'direct-review',
        reviewId,
        actorId: review.reviewer.id,
        action: 'SUBMITTED',
        details: `Rubrikwertung gespeichert (${review.reviewer.displayName})`,
        createdAt: now,
      };

      setProgramme((prev) => ({
        ...prev,
        humanReviews: [review, ...prev.humanReviews],
        attempts: prev.attempts.map((a) =>
          a.id === input.attemptId
            ? {
                ...a,
                source: 'HUMAN_REVIEW',
                scores: input.scores,
              }
            : a,
        ),
        reviewAssignments: prev.reviewAssignments.map((assign) =>
          assign.id === input.assignmentId || assign.attemptId === input.attemptId
            ? { ...assign, status: 'COMPLETED', completedAt: now }
            : assign,
        ),
        reviewAuditEvents: [audit, ...prev.reviewAuditEvents],
      }));
      setProgrammeNotice(
        'Menschliche Bewertung gespeichert. Quelle ist lokal als HUMAN_REVIEW markiert (ohne externe Identitätsprüfung).',
      );
    },
    [],
  );

  const createReassessmentLink = useCallback(
    (
      sourceAttemptId: string,
      sourceReviewId: string | null,
      unitId: string,
      taskId: string,
    ) => {
      const now = Date.now();
      const link: ReassessmentLink = {
        id: `reassess-${now}-${Math.random().toString(36).slice(2, 6)}`,
        sourceAttemptId,
        sourceReviewId,
        reassessmentUnitId: unitId,
        reassessmentTaskId: taskId,
        status: 'PLANNED',
        createdAt: now,
        completedAt: null,
      };
      setProgramme((prev) => ({
        ...prev,
        reassessmentLinks: [link, ...prev.reassessmentLinks],
      }));
      setProgrammeNotice('Nachprüfung verknüpft.');
    },
    [],
  );

  const upsertExternalListeningDraft = useCallback(
    (draft: Omit<ExternalListeningDraft, 'updatedAt'>) => {
      const now = Date.now();
      setProgramme((prev) => {
        const filtered = prev.externalListeningDrafts.filter(
          (d) => d.sourceId !== draft.sourceId,
        );
        return {
          ...prev,
          externalListeningDrafts: [{ ...draft, updatedAt: now }, ...filtered],
        };
      });
    },
    [],
  );

  const setBookAudioFile = useCallback((file: File | null, selectionKey?: string) => {
    const key = selectionKey ?? '__default__';
    if (bookAudioUrlsRef.current[key]) {
      URL.revokeObjectURL(bookAudioUrlsRef.current[key]);
      delete bookAudioUrlsRef.current[key];
    }
    if (!file) {
      setBookAudioMap((prev) => {
        const next = { ...prev };
        delete next[key];
        return next;
      });
      return;
    }
    const url = URL.createObjectURL(file);
    bookAudioUrlsRef.current[key] = url;
    setBookAudioMap((prev) => ({
      ...prev,
      [key]: { displayName: file.name, objectUrl: url },
    }));
  }, []);

  const clearBookAudio = useCallback(
    (selectionKey?: string) => {
      if (!selectionKey) {
        Object.values(bookAudioUrlsRef.current).forEach((u) => URL.revokeObjectURL(u));
        bookAudioUrlsRef.current = {};
        setBookAudioMap({});
        return;
      }
      setBookAudioFile(null, selectionKey);
    },
    [setBookAudioFile],
  );

  const exportLocalJson = useCallback(() => {
    const json = ProgrammeExport.create({
      attempts: programme.attempts,
      experiences: programme.realLifeExperiences,
      assessments: programme.assessments,
      externalListeningNotes: programme.externalListeningDrafts,
      practiceDrafts: programme.practiceDrafts,
      feedbackChallenges: programme.feedbackChallenges,
      humanReviews: programme.humanReviews,
      reviewAssignments: programme.reviewAssignments,
      reviewAuditEvents: programme.reviewAuditEvents,
      reassessmentLinks: programme.reassessmentLinks,
      baselineProfile: labs.baselineProfile,
      vocabularyItems: labs.srsVocabulary,
      proficiencySnapshots: labs.proficiencySnapshots,
      dailyStreak: labs.dailyStreak,
      preferences: programme.preferences,
      bookmarkedUnitIds: programme.bookmarkedUnitIds,
      assistedUnitIds: programme.assistedUnitIds,
      lastUnitId: programme.lastUnitId,
      lastTaskId: programme.lastTaskId,
      onboardingComplete: programme.onboardingComplete,
    });
    ProgrammeExport.download(json);
    setProgrammeNotice('Lokaler JSON-Export erstellt (ohne Audiodateien).');
  }, [programme, labs]);

  const deleteAllLocalData = useCallback(() => {
    Object.values(bookAudioUrlsRef.current).forEach((u) => URL.revokeObjectURL(u));
    bookAudioUrlsRef.current = {};
    setBookAudioMap({});
    try {
      localStorage.removeItem(STORAGE_PROGRAMME_KEY);
      localStorage.removeItem(STORAGE_LABS_KEY);
    } catch {
      // ignore
    }
    setProgramme(defaultProgrammeData());
    setLabs(defaultLabsData());
    setActiveAssessmentId(null);
    setProgrammeNotice('Alle lokalen Lerndaten wurden vollständig gelöscht.');
  }, []);

  // SRS Actions
  const recordSrsReview = useCallback(
    (itemId: string, qualityScore: number): VocabularySrsItem | null => {
      const now = Date.now();
      let updatedCard: VocabularySrsItem | null = null;
      setLabs((prev) => {
        const nextVocab = prev.srsVocabulary.map((item) => {
          if (item.id !== itemId) return item;
          const next = RetrievalPolicy.nextSchedule(
            qualityScore,
            item.intervalDays,
            item.easeFactor,
            item.repetitions,
          );
          const consecutiveErrors = qualityScore < 2 ? item.consecutiveErrors + 1 : 0;
          const stage =
            qualityScore < 2
              ? Math.max(0, item.stage - 1)
              : Math.min(4, item.stage + 1);
          const updated: VocabularySrsItem = {
            ...item,
            stage,
            easeFactor: next.easeFactor,
            intervalDays: next.intervalDays,
            repetitions: next.repetitions,
            nextReviewTimestamp: now + next.intervalDays * MasteryEngine.DAY_MILLIS,
            lastReviewedTimestamp: now,
            consecutiveErrors,
            isMastered: next.repetitions >= 3 && next.intervalDays >= 7,
          };
          updatedCard = updated;
          return updated;
        });
        return {
          ...prev,
          srsVocabulary: nextVocab,
          dailyStreak: recordStreakActivity(prev.dailyStreak, now),
        };
      });
      return updatedCard;
    },
    [],
  );

  const addCustomSrsVocabulary = useCallback(
    (input: {
      germanCollocation: string;
      meaningDe: string;
      exampleSentence: string;
      register?: string;
      category?: string;
    }) => {
      const now = Date.now();
      const newItem: VocabularySrsItem = {
        id: `custom_voc_${now}_${Math.random().toString(36).slice(2, 6)}`,
        germanCollocation: input.germanCollocation.trim(),
        meaningDe: input.meaningDe.trim(),
        exampleSentence: input.exampleSentence.trim(),
        register: input.register?.trim() || 'Gehoben / C1',
        category: input.category?.trim() || 'Nomen-Verb-Verbindung',
        stage: 0,
        easeFactor: 2.5,
        intervalDays: 0,
        repetitions: 0,
        nextReviewTimestamp: now,
        lastReviewedTimestamp: null,
        createdTimestamp: now,
        consecutiveErrors: 0,
        isMastered: false,
        sourceUnitId: 'custom',
      };
      setLabs((prev) => ({
        ...prev,
        srsVocabulary: [newItem, ...prev.srsVocabulary],
      }));
    },
    [],
  );

  const deleteSrsVocabulary = useCallback((id: string) => {
    setLabs((prev) => ({
      ...prev,
      srsVocabulary: prev.srsVocabulary.filter((v) => v.id !== id),
    }));
  }, []);

  const seedCurriculumExpressions = useCallback(
    (unitId: string, expressions: string[]): number => {
      const now = Date.now();
      const unitTitle =
        curriculumUnits.find((u) => u.id === unitId)?.title ?? unitId;
      let addedCount = 0;
      setLabs((prev) => {
        const existingLower = new Set(
          prev.srsVocabulary.map((v) => v.germanCollocation.toLowerCase()),
        );
        const toAdd: VocabularySrsItem[] = [];
        expressions.forEach((expr, idx) => {
          const clean = expr.trim();
          if (!clean || existingLower.has(clean.toLowerCase())) return;
          existingLower.add(clean.toLowerCase());
          toAdd.push({
            id: `unit_${unitId}_${idx}_${now}`,
            germanCollocation: clean,
            meaningDe: `Ausdruck aus Kurseinheit „${unitTitle}“ – im Kontext aktiv verwenden.`,
            exampleSentence: `Lernaufgabe (${unitId}): „${clean}“ frei in einer eigenen Antwort einsetzen.`,
            register: 'C1 Kursausdruck',
            category: 'Lektionsexpression',
            stage: 0,
            easeFactor: 2.5,
            intervalDays: 0,
            repetitions: 0,
            nextReviewTimestamp: now,
            lastReviewedTimestamp: null,
            createdTimestamp: now,
            consecutiveErrors: 0,
            isMastered: false,
            sourceUnitId: unitId,
          });
        });
        addedCount = toAdd.length;
        if (toAdd.length === 0) return prev;
        return {
          ...prev,
          srsVocabulary: [...toAdd, ...prev.srsVocabulary],
        };
      });
      setProgrammeNotice(
        addedCount > 0
          ? `${addedCount} Ausdrücke ins aktive Abruftraining (Wortschatz-SRS) übernommen.`
          : 'Alle Ausdrücke dieser Einheit sind bereits im Abruftraining vorhanden.',
      );
      return addedCount;
    },
    [],
  );

  const advanceErrorStage = useCallback((errorId: string) => {
    setLabs((prev) => {
      const base = initialErrors.find((e) => e.id === errorId);
      const current = prev.errorMasteryOverrides[errorId] ?? base?.masteryStage ?? 'NEU';
      const idx = MASTERY_ORDER.indexOf(current);
      const nextStage =
        idx >= 0 && idx < MASTERY_ORDER.length - 1
          ? MASTERY_ORDER[idx + 1]
          : 'GEMEISTERT';
      return {
        ...prev,
        errorMasteryOverrides: {
          ...prev.errorMasteryOverrides,
          [errorId]: nextStage,
        },
      };
    });
  }, []);

  // DailySession Runner state
  const [activeSession, setActiveSession] = useState<DailyTrainingSession | null>(null);
  const [currentPhase, setCurrentPhase] = useState<SessionPhase>('WARM_UP');
  const [warmUpSelectedOption, setWarmUpSelectedOption] = useState<number | null>(null);
  const [warmUpAnswerRevealed, setWarmUpAnswerRevealed] = useState(false);
  const [productionUserInput, setProductionUserInput] = useState('');
  const [transferUserInput, setTransferUserInput] = useState('');
  const [sessionCompleted, setSessionCompleted] = useState(false);

  const startDailySession = useCallback(() => {
    const nextRotation = labs.sessionRotationIndex;
    const session = generateDailyTrainingSession(nextRotation, topPriority, errorDatabase);
    setLabs((prev) => ({
      ...prev,
      sessionRotationIndex: prev.sessionRotationIndex + 1,
    }));
    setActiveSession(session);
    setCurrentPhase('WARM_UP');
    setWarmUpSelectedOption(null);
    setWarmUpAnswerRevealed(false);
    setProductionUserInput('');
    setTransferUserInput('');
    setSessionCompleted(false);
    navigateTo('DailySession');
  }, [labs.sessionRotationIndex, topPriority, errorDatabase, navigateTo]);

  const selectWarmUpOption = useCallback((idx: number) => {
    setWarmUpSelectedOption(idx);
  }, []);

  const revealWarmUpAnswer = useCallback(() => {
    setWarmUpAnswerRevealed(true);
  }, []);

  const advanceSessionPhase = useCallback(() => {
    if (!activeSession || sessionCompleted) return;
    if (currentPhase === 'WARM_UP') setCurrentPhase('MAIN_CHALLENGE');
    else if (currentPhase === 'MAIN_CHALLENGE') setCurrentPhase('ACTIVE_PRODUCTION');
    else if (currentPhase === 'ACTIVE_PRODUCTION') setCurrentPhase('PRECISION_CORRECTION');
    else if (currentPhase === 'PRECISION_CORRECTION') setCurrentPhase('TRANSFER_TEST');
    else {
      setSessionCompleted(true);
      recordPracticeActivity('18-Min. Adaptiv-Session');
    }
  }, [activeSession, sessionCompleted, currentPhase, recordPracticeActivity]);

  // ListeningLab State
  const [selectedLabItemIndex, setSelectedLabItemIndex] = useState(0);
  const [playbackSpeed, setPlaybackSpeed] = useState(1.0);
  const [isBlindTranscriptRevealed, setIsBlindTranscriptRevealed] = useState(false);
  const [selectedLabQuizAnswers, setSelectedLabQuizAnswers] = useState<Record<number, number>>({});

  const selectLabItem = useCallback((idx: number) => {
    speechService.stopSpeaking();
    setSelectedLabItemIndex(idx);
    setIsBlindTranscriptRevealed(false);
    setSelectedLabQuizAnswers({});
  }, []);

  const revealBlindTranscript = useCallback(() => {
    setIsBlindTranscriptRevealed(true);
  }, []);

  const answerLabQuestion = useCallback(
    (questionIdx: number, answerIdx: number) => {
      setSelectedLabQuizAnswers((prev) => ({ ...prev, [questionIdx]: answerIdx }));
      recordPracticeActivity('Hör-Labor');
    },
    [recordPracticeActivity],
  );

  // SpeakingLab State
  const [currentSpeakingPromptIndex, setCurrentSpeakingPromptIndex] = useState(0);
  const [latestPronunciationResult, setLatestPronunciationResult] =
    useState<PronunciationAnalysisResult | null>(null);

  const analyzeSpokenResponse = useCallback(
    (
      prompt: SpeakingPrompt,
      transcribedText: string,
      confidence = 0.85,
    ): PronunciationAnalysisResult => {
      const result = PronunciationAnalysisEngine.analyzeSpokenResponse(
        prompt,
        transcribedText,
        confidence,
      );
      setLatestPronunciationResult(result);
      if (transcribedText.trim()) {
        recordPracticeActivity('Sprechübung (unbewerteter Transkript-Abgleich)');
      }
      return result;
    },
    [recordPracticeActivity],
  );

  const clearPronunciationResult = useCallback(() => {
    setLatestPronunciationResult(null);
  }, []);

  // Conversation Simulator State
  const [activeConversationIndex, setActiveConversationIndex] = useState(0);
  const [currentConversationTurnIndex, setCurrentConversationTurnIndex] = useState(0);
  const [interruptionTriggered, setInterruptionTriggered] = useState(false);
  const [conversationUserResponse, setConversationUserResponse] = useState('');
  const [responseLatencySeconds, setResponseLatencySeconds] = useState(0);
  const [conversationSubmitted, setConversationSubmitted] = useState(false);
  const [latencyTimerActive, setLatencyTimerActive] = useState(false);

  useEffect(() => {
    if (!latencyTimerActive || currentScreen !== 'ConversationSimulator') return;
    const conv = conversationScenarios[activeConversationIndex];
    const interruptSec = conv?.unexpectedInterruption.triggerAfterSecond ?? 6;
    const timer = window.setInterval(() => {
      setResponseLatencySeconds((sec) => {
        const next = sec + 1;
        if (next === interruptSec && !interruptionTriggered && conv) {
          setInterruptionTriggered(true);
          speechService.speak(conv.unexpectedInterruption.partnerInterruptSpeech, {
            rate: 1.05,
            pitch: 1.05,
            id: 'conversation-interruption',
          });
        }
        return next;
      });
    }, 1000);
    return () => window.clearInterval(timer);
  }, [
    latencyTimerActive,
    currentScreen,
    conversationScenarios,
    activeConversationIndex,
    interruptionTriggered,
  ]);

  const playOpeningStatement = useCallback(() => {
    const conv = conversationScenarios[activeConversationIndex];
    if (!conv) return;
    speechService.speak(conv.openingStatement, {
      rate: 0.98,
      pitch: 1.0,
      id: 'conversation-opening',
    });
    setResponseLatencySeconds(0);
    setLatencyTimerActive(true);
  }, [conversationScenarios, activeConversationIndex]);

  const selectConversation = useCallback(
    (idx: number) => {
      setConversationSubmitted(false);
      setActiveConversationIndex(idx);
      setCurrentConversationTurnIndex(0);
      setInterruptionTriggered(false);
      setConversationUserResponse('');
      setResponseLatencySeconds(0);
      const conv = conversationScenarios[idx];
      if (conv) {
        speechService.speak(conv.openingStatement, {
          rate: 0.98,
          pitch: 1.0,
          id: 'conversation-opening',
        });
        setLatencyTimerActive(true);
      }
    },
    [conversationScenarios],
  );

  const submitConversationResponse = useCallback(
    (text: string) => {
      if (!text.trim() || conversationSubmitted) return;
      setLatencyTimerActive(false);
      setConversationUserResponse(text);
      setConversationSubmitted(true);
      recordPracticeActivity('Gesprächssimulator (unbewertete Übung)');
    },
    [conversationSubmitted, recordPracticeActivity],
  );

  const nextConversationTurn = useCallback(() => {
    const conv = conversationScenarios[activeConversationIndex];
    if (!conv) return;
    if (currentConversationTurnIndex < conv.turns.length - 1) {
      const nextIdx = currentConversationTurnIndex + 1;
      setConversationSubmitted(false);
      setCurrentConversationTurnIndex(nextIdx);
      setConversationUserResponse('');
      setInterruptionTriggered(false);
      setResponseLatencySeconds(0);
      const turn = conv.turns[nextIdx];
      speechService.speak(turn.partnerSpeech, {
        rate: turn.speakerProfile === 'INTERRUPTER' ? 1.05 : 1.0,
        pitch: turn.speakerProfile === 'FOLLOW_UP' ? 0.94 : 1.0,
        id: `conversation-turn-${nextIdx}`,
      });
      setLatencyTimerActive(true);
    }
  }, [conversationScenarios, activeConversationIndex, currentConversationTurnIndex]);

  // ErrorIntelligence Drill State
  const [selectedErrorForDrill, setSelectedErrorForDrill] =
    useState<ErrorIntelligenceItem | null>(null);
  const [errorDrillUserAnswer, setErrorDrillUserAnswer] = useState('');
  const [errorDrillFeedback, setErrorDrillFeedback] = useState<string | null>(null);

  const selectErrorForDrill = useCallback((error: ErrorIntelligenceItem) => {
    setSelectedErrorForDrill(error);
    setErrorDrillUserAnswer('');
    setErrorDrillFeedback(null);
  }, []);

  const clearSelectedErrorForDrill = useCallback(() => {
    setSelectedErrorForDrill(null);
    setErrorDrillUserAnswer('');
    setErrorDrillFeedback(null);
  }, []);

  const submitErrorDrillAnswer = useCallback(() => {
    if (!selectedErrorForDrill) return;
    setErrorDrillFeedback(
      `Musterlösung (unbewerteter Vergleich, kein Beherrschungsnachweis): ${selectedErrorForDrill.transferTargetAnswer}`,
    );
  }, [selectedErrorForDrill]);

  // Baseline Diagnostic State
  const [diagnosticUiState, setDiagnosticUiState] = useState<DiagnosticUiState>({
    status: 'IDLE',
    mode: 'GRAMMAR_AND_VOCABULARY',
    tasks: diagnosticTasksGrammarVocab,
    currentTaskIndex: 0,
    answers: {},
    evaluationResult: null,
  });

  const startDiagnosticAssessment = useCallback((mode: DiagnosticMode) => {
    const tasks =
      mode === 'GRAMMAR_AND_VOCABULARY'
        ? diagnosticTasksGrammarVocab
        : diagnosticTasksFull;
    setDiagnosticUiState({
      status: 'IN_PROGRESS',
      mode,
      tasks,
      currentTaskIndex: 0,
      answers: {},
      evaluationResult: null,
    });
  }, []);

  const recordDiagnosticAnswer = useCallback((selectedIndex: number) => {
    setDiagnosticUiState((prev) => {
      const task = prev.tasks[prev.currentTaskIndex];
      if (!task) return prev;
      const isCorrect = selectedIndex === task.correctOptionIndex;
      return {
        ...prev,
        answers: {
          ...prev.answers,
          [prev.currentTaskIndex]: {
            selectedOptionIndex: selectedIndex,
            isEvaluatedCorrect: isCorrect,
            earnedScore: isCorrect ? 100 : 0,
          },
        },
      };
    });
  }, []);

  const nextDiagnosticTask = useCallback(() => {
    setDiagnosticUiState((prev) => {
      if (prev.currentTaskIndex < prev.tasks.length - 1) {
        return { ...prev, currentTaskIndex: prev.currentTaskIndex + 1 };
      }
      const evaluation = evaluateDiagnosticScreening(
        prev.tasks,
        prev.answers,
        prev.mode,
      );
      return {
        ...prev,
        status: 'COMPLETED',
        evaluationResult: evaluation,
      };
    });
    recordPracticeActivity('MCQ-Übungsscreening');
  }, [recordPracticeActivity]);

  const previousDiagnosticTask = useCallback(() => {
    setDiagnosticUiState((prev) => ({
      ...prev,
      currentTaskIndex: Math.max(0, prev.currentTaskIndex - 1),
    }));
  }, []);

  const resetDiagnosticAssessment = useCallback(() => {
    setDiagnosticUiState((prev) => ({
      ...prev,
      status: 'IDLE',
      currentTaskIndex: 0,
      answers: {},
      evaluationResult: null,
    }));
  }, []);

  // Football Coach Track drills
  const toggleDrillCompleted = useCallback((drillId: string) => {
    setLabs((prev) => {
      const exists = prev.completedDrillIds.includes(drillId);
      return {
        ...prev,
        completedDrillIds: exists
          ? prev.completedDrillIds.filter((id) => id !== drillId)
          : [...prev.completedDrillIds, drillId],
      };
    });
  }, []);

  const value: AppContextValue = {
    isDark,
    toggleTheme,
    currentScreen,
    navigateTo,
    navigateBack,
    canNavigateBack: screenStack.length > 1,
    pendingUnitOpen,
    openUnitInProgramme,
    clearPendingUnitOpen,

    preferences: programme.preferences,
    updatePreferences,
    attempts: programme.attempts,
    realLifeExperiences: programme.realLifeExperiences,
    assessments: programme.assessments,
    activeAssessmentId,
    setActiveAssessmentId,
    feedbackChallenges: programme.feedbackChallenges,
    humanReviews: programme.humanReviews,
    reviewAssignments: programme.reviewAssignments,
    reviewAuditEvents: programme.reviewAuditEvents,
    reassessmentLinks: programme.reassessmentLinks,
    externalListeningDrafts: programme.externalListeningDrafts,
    practiceDrafts: programme.practiceDrafts,
    assistedUnitIds: programme.assistedUnitIds,
    bookmarkedUnitIds: programme.bookmarkedUnitIds,
    lastUnitId: programme.lastUnitId,
    lastTaskId: programme.lastTaskId,
    onboardingComplete: programme.onboardingComplete,
    quizResults: programme.quizResults,
    bookAudio,
    getBookAudio,
    programmeNotice,
    programmeError,
    clearProgrammeNotice,
    clearProgrammeError,
    setProgrammeNotice,
    setProgrammeError,
    completeOnboarding,
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
    addRealLifeExperience,
    completeRealLifeReview,
    createFeedbackChallenge,
    resolveFeedbackChallenge,
    submitHumanReview,
    createReviewAssignment,
    createReassessmentLink,
    upsertExternalListeningDraft,
    setBookAudioFile,
    clearBookAudio,
    exportLocalJson,
    deleteAllLocalData,

    dailyStreak: labs.dailyStreak,
    recordPracticeActivity,
    srsVocabulary: labs.srsVocabulary,
    recordSrsReview,
    addCustomSrsVocabulary,
    deleteSrsVocabulary,
    seedCurriculumExpressions,
    skillScores,
    crossCutting,
    dualBenchmark,
    topPriority,
    errorDatabase,
    advanceErrorStage,
    listeningItems,
    conversationScenarios,
    mediationTasks,
    speakingPrompts,

    activeSession,
    currentPhase,
    warmUpSelectedOption,
    warmUpAnswerRevealed,
    productionUserInput,
    transferUserInput,
    sessionCompleted,
    startDailySession,
    selectWarmUpOption,
    revealWarmUpAnswer,
    advanceSessionPhase,
    updateProductionInput: setProductionUserInput,
    updateTransferInput: setTransferUserInput,

    selectedLabItemIndex,
    playbackSpeed,
    isBlindTranscriptRevealed,
    selectedLabQuizAnswers,
    selectLabItem,
    setPlaybackSpeed,
    revealBlindTranscript,
    answerLabQuestion,

    currentSpeakingPromptIndex,
    setCurrentSpeakingPromptIndex,
    latestPronunciationResult,
    analyzeSpokenResponse,
    clearPronunciationResult,

    activeConversationIndex,
    currentConversationTurnIndex,
    interruptionTriggered,
    conversationUserResponse,
    responseLatencySeconds,
    conversationSubmitted,
    selectConversation,
    playOpeningStatement,
    updateConversationResponse: setConversationUserResponse,
    submitConversationResponse,
    nextConversationTurn,

    selectedErrorForDrill,
    errorDrillUserAnswer,
    errorDrillFeedback,
    selectErrorForDrill,
    clearSelectedErrorForDrill,
    updateErrorDrillAnswer: setErrorDrillUserAnswer,
    submitErrorDrillAnswer,

    diagnosticUiState,
    startDiagnosticAssessment,
    recordDiagnosticAnswer,
    nextDiagnosticTask,
    previousDiagnosticTask,
    resetDiagnosticAssessment,

    completedDrillIds: labs.completedDrillIds,
    toggleDrillCompleted,
  };

  return <AppContext.Provider value={value}>{children}</AppContext.Provider>;
};

export function useApp(): AppContextValue {
  const ctx = useContext(AppContext);
  if (!ctx) throw new Error('useApp must be used inside <AppProvider>');
  return ctx;
}
