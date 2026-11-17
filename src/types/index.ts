export type ProgrammeTheme = 'TRACKSIDE' | 'SILVER' | 'PITCH';

export type ProgrammeLanguage = 'DE' | 'EN';

export type Screen =
  | 'Programme'
  | 'VocabTraining'
  | 'DailySession'
  | 'ListeningLab'
  | 'SpeakingLab'
  | 'ConversationSimulator'
  | 'ErrorIntelligence'
  | 'Mediation'
  | 'IntelligenceProfile'
  | 'BaselineDiagnostic'
  | 'FootballCoachTrack';

export type ContextTrack = 'GENERAL' | 'SOCIAL' | 'ENGINEERING' | 'COACHING';

export type Dimension =
  | 'ACCURACY'
  | 'FLUENCY'
  | 'RANGE'
  | 'NATURALNESS'
  | 'COHERENCE'
  | 'INTERACTION'
  | 'PRAGMATICS'
  | 'REGISTER'
  | 'COMPREHENSION'
  | 'MEDIATION';

export type EvidenceSource =
  | 'SELF_REFLECTION'
  | 'AUTOMATIC_QUIZ'
  | 'HUMAN_REVIEW';

export interface UnitContent {
  reading: string;
  listening: string;
  expressions: string[];
  grammar: string;
  question: string;
  options: string[];
  correctOption: number;
  explanation: string;
  controlledPrompt: string;
  controlledModel: string;
  speaking: string;
  interaction: string;
  writing: string;
  transfer: string;
  feedback: string;
}

export interface CurriculumUnit {
  id: string;
  stage: number;
  title: string;
  objectives: string[];
  contexts: Record<ContextTrack, string>;
  prerequisites: string[];
  content: UnitContent | null;
}

export interface Attempt {
  id: string;
  unitId: string;
  taskId: string;
  timestamp: number;
  response: string;
  source: EvidenceSource;
  scores: Partial<Record<Dimension, number>>;
  assisted: boolean;
  unfamiliar: boolean;
  independentProduction: boolean;
  context: ContextTrack;
  errors: string[];
  taskVersion?: number;
  rubricVersion?: number;
}

export interface ProgrammePreferences {
  learnerName: string;
  theme: ProgrammeTheme;
  language: ProgrammeLanguage;
  dailyMinutes: number;
  compact: boolean;
}

export interface ProgrammePlanStep {
  taskId: string;
  title: string;
  minutes: number;
}

export interface ProgrammeQuizResult {
  option: number | null;
  correct: boolean | null;
  assisted: boolean;
}

// ---------- Weakness & Learner Diagnostic Engines ----------

export type WeaknessSignal =
  | 'SOUND_RECOGNITION'
  | 'WORD_SEGMENTATION'
  | 'LISTENING_VOCABULARY'
  | 'SITUATIONAL_INFERENCE'
  | 'INFORMATION_RETENTION'
  | 'ACTIVE_RETRIEVAL'
  | 'SENTENCE_FORMULATION'
  | 'GRAMMAR_UNDER_PRESSURE'
  | 'NATURALNESS'
  | 'REGISTER'
  | 'RESPONSE_INITIATION'
  | 'CONVERSATIONAL_REPAIR'
  | 'INTERACTION_MANAGEMENT'
  | 'IMPLIED_MEANING'
  | 'MULTI_SPEAKER_TRACKING';

export type WeaknessConfidence = 'LOW' | 'MEDIUM' | 'HIGH';

export interface WeaknessEvidence {
  signal: WeaknessSignal;
  context: ContextTrack;
  confidence: number;
  timestamp: number;
  excerpt: string;
  source: string;
}

export interface WeaknessSummary {
  signal: WeaknessSignal;
  contexts: ContextTrack[];
  sampleCount: number;
  confidence: WeaknessConfidence;
  priority: number;
  latestEvidence: WeaknessEvidence;
}

export interface WeaknessProfile {
  summaries: WeaknessSummary[];
}

export type DiagnosticDimension =
  | 'WORD_RECOGNITION'
  | 'CONNECTED_SPEECH'
  | 'SITUATIONAL_MEANING'
  | 'EXPRESSION_RETRIEVAL'
  | 'SENTENCE_FORMULATION'
  | 'NATURAL_SPEAKING'
  | 'IMPLIED_MEANING'
  | 'MULTI_SPEAKER_TRACKING'
  | 'INFORMATION_RETENTION'
  | 'SPONTANEOUS_RESPONSE'
  | 'RESPONSE_INITIATION'
  | 'INTERRUPTION_HANDLING'
  | 'CONVERSATIONAL_REPAIR';

export type DiagnosticStatus = 'DEMONSTRATED' | 'FRAGILE' | 'NOT_OBSERVED';

export type DiagnosticConfidence = 'LOW' | 'MEDIUM' | 'HIGH';

export interface DiagnosticObservation {
  dimension: DiagnosticDimension;
  status: DiagnosticStatus;
  confidence: number;
  evidence: string;
  timestamp: number;
}

export interface DiagnosticFinding {
  dimension: DiagnosticDimension;
  status: DiagnosticStatus;
  confidence: DiagnosticConfidence;
  sampleCount: number;
  evidence: string[];
}

export interface LearnerDiagnosticProfile {
  findings: DiagnosticFinding[];
}

export type AdaptiveAction =
  | 'PRIORITIZE_LISTENING'
  | 'PRIORITIZE_COMPREHENSION'
  | 'PRIORITIZE_RETRIEVAL'
  | 'PRIORITIZE_FORMULATION'
  | 'PRIORITIZE_INTERACTION'
  | 'PRIORITIZE_READING'
  | 'PRIORITIZE_SPEAKING'
  | 'PRIORITIZE_WRITING'
  | 'ADD_CONVERSATION_VARIATION'
  | 'SPACE_RETRIEVAL'
  | 'DEFER_GENERIC_READING';

export type AdaptiveReasonCode =
  | 'PERSONAL_REPAIR_DUE'
  | 'LESSON_REVIEW_DUE'
  | 'QUIZ_REPAIR'
  | 'RESPONSE_REPAIR'
  | 'FIRST_RETRIEVAL'
  | 'NEXT_INPUT'
  | 'NEXT_LISTENING'
  | 'NEXT_COMPREHENSION_CHECK'
  | 'NEXT_CONTROLLED_PRACTICE'
  | 'NEXT_PRODUCTION'
  | 'RECURRING_SIGNAL'
  | 'BALANCED_PRACTICE'
  | 'COURSE_COMPLETE';

export interface AdaptiveControlDecision {
  primaryTaskId: string | null;
  actions: AdaptiveAction[];
  reason: string;
  confidence: DiagnosticConfidence;
  dialogueBriefId: string | null;
  taskOrder: string[];
  focusMinutes: number | null;
  recommendedUnitId: string | null;
  recommendedContext: ContextTrack | null;
  reasonCode: AdaptiveReasonCode;
  focusSignal: WeaknessSignal | null;
}

export type AdaptiveDifficulty = 'SUPPORT' | 'CORE' | 'CHALLENGE';

export interface AdaptiveLessonAddition {
  taskId: string;
  title: string;
  instruction: string;
  prompt: string;
  context: ContextTrack;
  reviewExperienceId?: string | null;
}

export interface LearnerError {
  code: string;
  label: string;
  excerpt: string;
  explanation: string;
  remediation: string;
  objective: string | null;
}

export interface ErrorRecyclingPlan {
  code: string;
  objective: string | null;
  repeatedCount: number;
  nextReviewAt: number;
  difficulty: AdaptiveDifficulty;
  instruction: string;
}

export interface AdaptiveRecommendation {
  difficulty: AdaptiveDifficulty;
  reason: string;
  errors: LearnerError[];
  feedbackClaims: FeedbackClaim[];
  nextStep: string;
  weaknesses: WeaknessSummary[];
  recyclingPlans: ErrorRecyclingPlan[];
}

export interface AdaptiveLessonContentPlan {
  difficulty: AdaptiveDifficulty;
  focusTaskId: string;
  reason: string;
  addition: AdaptiveLessonAddition | null;
  recommendation: AdaptiveRecommendation;
}

export type DiagnosticCheckpointState =
  | 'NEEDS_OBJECTIVE_CHECK'
  | 'NEEDS_PRODUCTION'
  | 'NEEDS_REMEDIATION'
  | 'COMPLETE'
  | 'UNAVAILABLE';

export interface DiagnosticCheckpoint {
  state: DiagnosticCheckpointState;
  completedProductionTasks: number;
  feedback: string[];
}

// ---------- Real-Life Experience & Repair ----------

export type RealLifeIssueType =
  | 'LISTENING'
  | 'RETRIEVAL'
  | 'GRAMMAR_UNDER_PRESSURE'
  | 'NATURALNESS'
  | 'REGISTER'
  | 'REPAIR';

export interface RealLifeExperience {
  id: string;
  createdAt: number;
  context: ContextTrack;
  situation: string;
  heard: string;
  intended: string;
  said: string;
  issueType: RealLifeIssueType;
  reviewStage: number;
  nextReviewAt: number;
  reviewResponse: string;
}

export interface RealLifeRepair {
  primaryPhrase: string;
  alternatives: string[];
  explanation: string;
  transferPrompt: string;
}

// ---------- Assessments & Evidence Contracts ----------

export type AssessmentEvidence =
  | 'AUDIO_RESPONSE'
  | 'TEXT_RESPONSE'
  | 'MULTI_TURN_RESPONSE'
  | 'DELAYED_RETRIEVAL'
  | 'UNFAMILIAR_TRANSFER'
  | 'REAL_LIFE_REFLECTION';

export type AssessmentSessionKind =
  | 'DELAYED_CHECKPOINT'
  | 'GOETHE_STYLE'
  | 'PILOT_WEEK'
  | 'WEEKLY_INTEGRATED'
  | 'LISTENING_PRACTICE'
  | 'SPEAKING_PRACTICE';

export interface DeliveredAssessmentTask {
  id: string;
  unitId: string;
  taskId: string;
  prompt: string;
  evidence: AssessmentEvidence;
  context: ContextTrack;
  unfamiliar: boolean;
  variationId: string;
  taskVersion: number;
  rubricVersion: number;
}

export interface TaskVariation {
  id: string;
  label: string;
  audience: string;
  pressure: string;
  constraint: string;
  taskVersion: number;
  rubricVersion: number;
}

export interface AssessmentSession {
  id: string;
  kind: AssessmentSessionKind;
  createdAt: number;
  durationMinutes: number;
  tasks: DeliveredAssessmentTask[];
}

export interface PersistedAssessment {
  session: AssessmentSession;
  responses: Record<string, string>;
  currentIndex: number;
  completed: boolean;
  updatedAt: number;
}

export interface PilotComparison {
  baselineResponses: number;
  finalResponses: number;
  baselineIndependent: number;
  finalIndependent: number;
  baselineUnfamiliar: number;
  finalUnfamiliar: number;
}

export interface AssessmentPart {
  skill: string;
  prompt: string;
  minutes: number;
}

export interface AssessmentTask {
  taskId: string;
  stream: string;
  context: ContextTrack;
  title: string;
  sourceReading: string;
  sourceListening: string;
  parts: AssessmentPart[];
  reviewerChecklist: string[];
}

// ---------- Feedback Challenges & Human Reviews ----------

export interface FeedbackClaim {
  id: string;
  responseExcerpt: string;
  claimType: string;
  proposedFeedback: string;
  alternativeAccepted: boolean | null;
  attemptId: string;
  unitId: string;
  taskId: string;
  context: ContextTrack;
}

export type FeedbackChallengeDecision = 'DISAGREE' | 'UNSURE';

export type FeedbackChallengeStatus =
  | 'OPEN'
  | 'ACCEPTED'
  | 'REJECTED'
  | 'NEEDS_REASSESSMENT';

export interface FeedbackChallenge {
  id: string;
  claimId: string;
  learnerDecision: FeedbackChallengeDecision | null;
  correctedFeedback: string | null;
  status: FeedbackChallengeStatus;
  attemptId: string;
  unitId: string;
  taskId: string;
  context: ContextTrack;
  responseExcerpt: string;
  claimType: string;
  proposedFeedback: string;
  createdAt: number;
  updatedAt: number;
}

export interface FeedbackValidation {
  valid: boolean;
  reason: string;
}

export interface HumanReviewer {
  id: string;
  displayName: string;
  verified: boolean;
}

export interface HumanReviewSubmission {
  id: string;
  attemptId: string;
  reviewer: HumanReviewer;
  rubricVersion: number;
  scores: Partial<Record<Dimension, number>>;
  comment: string;
  createdAt: number;
}

export interface ReviewAssignment {
  id: string;
  attemptId: string;
  reviewerId: string;
  reviewerName: string;
  status: string;
  createdAt: number;
  dueAt: number | null;
  completedAt: number | null;
}

export interface ReviewAuditEvent {
  id: string;
  assignmentId: string;
  reviewId: string | null;
  actorId: string;
  action: string;
  details: string;
  createdAt: number;
}

export interface ReassessmentLink {
  id: string;
  sourceAttemptId: string;
  sourceReviewId: string | null;
  reassessmentUnitId: string;
  reassessmentTaskId: string;
  status: string;
  createdAt: number;
  completedAt: number | null;
}

// ---------- External & Book Listening Resources ----------

export interface ExternalListeningSource {
  id: string;
  title: string;
  url: string;
  formats: string;
  focus: string;
  transcriptNote: string;
  practiceTasks: string[];
}

export interface ExternalListeningDraft {
  sourceId: string;
  itemReference: string;
  firstPass: string;
  secondPass: string;
  comparisonOpened: boolean;
  updatedAt: number;
}

export interface BookAudioSelection {
  displayName: string | null;
  objectUrl: string | null;
}

export interface LessonListeningRecommendation {
  unitId: string;
  title: string;
  url: string;
  task: string;
}

export interface BookBridgePractice {
  unitId: string;
  bookReference: string;
  audioFilenameHint: string;
  originalPrompt: string;
  audioReference?: string;
}

export type RecordingRequirement =
  | 'NATIVE_OR_HIGHLY_PROFICIENT_SPEAKERS'
  | 'NATURAL_TURN_TAKING'
  | 'VERIFIED_TRANSCRIPT'
  | 'SPEAKER_METADATA'
  | 'REGIONAL_BACKGROUND'
  | 'APPLICATION_USE_RELEASE'
  | 'EDITED_AND_RAW_DELIVERABLES';

export interface OwnedDialogueBrief {
  id: string;
  situation: string;
  context: ContextTrack;
  speakers: number;
  targetSignals: WeaknessSignal[];
  requirements: RecordingRequirement[];
}

export interface PracticeDraft {
  unitId: string;
  taskId: string;
  context: ContextTrack;
  response: string;
  updatedAt: number;
}

// ---------- Legacy / C1 Lab Domain Types ----------

export type SkillCategory =
  | 'HOERVERSTEHEN'
  | 'SPONTANES_SPRECHEN'
  | 'INTERAKTION'
  | 'LESEVERSTEHEN'
  | 'SCHREIBEN'
  | 'GRAMMATIK'
  | 'WORTSCHATZ'
  | 'MEDIATION'
  | 'AUSSPRACHE_PROSODIE';

export interface SkillScore {
  category: SkillCategory;
  score: number;
  lastTestedDaysAgo: number;
  trend: number;
  criticalWeakness: string;
  masteredAreas: string[];
}

export interface CrossCuttingCompetence {
  title: string;
  description: string;
  score: number;
  levelLabel: string;
}

export interface DualBenchmark {
  functionalC1Score: number;
  functionalReadinessSummary: string;
  examC1Score: number;
  goetheC1Readiness: number;
  testDafReadiness: number;
  telcC1Readiness: number;
}

export interface BaselineDiagnosticReport {
  isCompleted: boolean;
  overallC1Score: number;
  functionalReadinessPct: number;
  primaryLimitingFactor: string;
  recommendedStartingPath: string;
  skillRatings: Partial<Record<SkillCategory, number>>;
}

export type SessionPhase =
  | 'WARM_UP'
  | 'MAIN_CHALLENGE'
  | 'ACTIVE_PRODUCTION'
  | 'PRECISION_CORRECTION'
  | 'TRANSFER_TEST';

export type ErrorCategory =
  | 'PRAEPOSITIONEN_KASUS'
  | 'KOLLOKATIONEN'
  | 'REGISTER_MISMATCH'
  | 'FALSE_FRIENDS_FEINHEITEN'
  | 'VERBKLAMMER_SATZSTRUKTUR'
  | 'ZOEGERN_LATENZ';

export type MasteryStage =
  | 'NEU'
  | 'WIEDERHOLUNG_TAG_1'
  | 'TRANSFER_TAG_3'
  | 'GEFESTIGT_TAG_7'
  | 'GEMEISTERT';

export interface ErrorIntelligenceItem {
  id: string;
  category: ErrorCategory;
  userMistake: string;
  nativeReformulation: string;
  whyItSoundsForeign: string;
  ruleOrNuance: string;
  contextSentence: string;
  transferChallengePrompt: string;
  transferTargetAnswer: string;
  masteryStage: MasteryStage;
  occurrencesCount: number;
  lastOccurredDaysAgo: number;
}

export interface ActiveVocabularyItem {
  id: string;
  expression: string;
  type: string;
  meaningDe: string;
  naturalExample: string;
  informalOrClunkyEquivalent: string;
  domain: string;
  isActivelyMastered: boolean;
  requiredInContext: string;
}

export interface WarmUpTask {
  audioSpeakerText: string;
  accentInfo: string;
  questionPrompt: string;
  options: string[];
  correctIndex: number;
  diagnosticExplanation: string;
}

export interface MainChallengeTask {
  title: string;
  scenarioContext: string;
  partnerStatement: string;
  stage1Prompt: string;
  stage2Deepening: string;
  stage3StressInference: string;
  keyPointsExpected: string[];
}

export interface ProductionTask {
  isSpeaking: boolean;
  situationBrief: string;
  timeLimitSeconds: number;
  keyEvaluationCriteria: string[];
  simulatedAudioPrompt: string;
}

export interface CorrectionTask {
  errorItems: ErrorIntelligenceItem[];
}

export interface TransferTask {
  transferDomain: string;
  scenarioPrompt: string;
  benchmarkExpectation: string;
}

export interface DailyTrainingSession {
  id: string;
  title: string;
  targetCategory: SkillCategory;
  totalMinutes: number;
  environment: string;
  warmUpPrompt: WarmUpTask;
  mainChallenge: MainChallengeTask;
  productionTask: ProductionTask;
  correctionTask: CorrectionTask;
  transferTask: TransferTask;
}

export interface InferenceQuestion {
  question: string;
  options: string[];
  correctIndex: number;
  rationaleWhyOthersAreWrong: string;
}

export interface ReductionAnnotation {
  spokenForm: string;
  standardWrittenForm: string;
  registerExplanation: string;
}

export interface ListeningLaboratoryItem {
  id: string;
  title: string;
  environment: string;
  accentOrDialect: string;
  nativeSpeechSpeed: number;
  durationSeconds: number;
  audioSpeakerText: string;
  blindComprehensionQuestions: InferenceQuestion[];
  transcriptWithReductions: string;
  reductionNotes: ReductionAnnotation[];
  contextAndSubtextAnalysis: string;
}

export type SpeakerProfile = 'PARTNER' | 'INTERRUPTER' | 'FOLLOW_UP';

export interface ConversationTurn {
  partnerSpeech: string;
  expectedUserRolePrompt: string;
  suggestedC1Connectors: string[];
  targetRegister: string;
  speakerProfile: SpeakerProfile;
}

export interface UnexpectedInterruption {
  triggerAfterSecond: number;
  partnerInterruptSpeech: string;
  reactionObjective: string;
  speakerProfile: SpeakerProfile;
}

export interface ConversationScenario {
  id: string;
  title: string;
  rolePartner: string;
  environment: string;
  openingStatement: string;
  turns: ConversationTurn[];
  unexpectedInterruption: UnexpectedInterruption;
  evaluationFocus: string;
  variationLabel: string;
}

export interface MediationTask {
  id: string;
  title: string;
  mediationType: string;
  sourceMaterial: string;
  recipientContext: string;
  criticalPitfallsToAvoid: string[];
  targetReformulationModel: string;
}

export type WordPronunciationStatus =
  | 'PERFECT'
  | 'ACCEPTABLE'
  | 'MISPRONOUNCED'
  | 'MISSING';

export interface PhoneticWordTip {
  word: string;
  ipa: string;
  focusArea: string;
  tipDe: string;
}

export interface SpeakingPrompt {
  id: string;
  title: string;
  targetSentence: string;
  category: string;
  contextBrief: string;
  difficultyLevel: string;
  phoneticFocusWords: PhoneticWordTip[];
  audioPromptSpeed: number;
}

export interface PronunciationWordResult {
  expectedWord: string;
  spokenWord: string | null;
  status: WordPronunciationStatus;
  confidenceScore: number;
  ipa: string | null;
  phoneticTip: string | null;
}

export interface PronunciationAnalysisResult {
  promptId: string;
  overallScore: number;
  accuracyScore: number;
  fluencyScore: number;
  prosodyScore: number;
  transcribedText: string;
  wordResults: PronunciationWordResult[];
  feedbackHeadlineDe: string;
  cefrBandDe: string;
  actionableTipsDe: string[];
  assessedAtTimestamp: number;
}

export interface VocabularySrsItem {
  id: string;
  germanCollocation: string;
  meaningDe: string;
  exampleSentence: string;
  register: string;
  category: string;
  stage: number;
  easeFactor: number;
  intervalDays: number;
  repetitions: number;
  nextReviewTimestamp: number;
  lastReviewedTimestamp: number | null;
  createdTimestamp: number;
  consecutiveErrors: number;
  isMastered: boolean;
  sourceUnitId: string;
}

export interface StreakDayStatus {
  dayIndex: number; // 1 (Mon) to 7 (Sun)
  shortLabel: string; // "Mo", "Di", "Mi", "Do", "Fr", "Sa", "So"
  isToday: boolean;
  isCompleted: boolean;
}

export interface StreakMilestone {
  targetDays: number;
  titleDe: string;
  rewardDescriptionDe: string;
  progressFraction: number;
}

export interface DailyStreakState {
  currentStreak: number;
  longestStreak: number;
  isPracticedToday: boolean;
  streakFreezesAvailable: number;
  totalSessionsCompleted: number;
  lastActiveEpochDay: number;
  activeDaysBitmask: number;
  lastActivityTimestamp: number;
  weeklyDays: StreakDayStatus[];
  nextMilestone: StreakMilestone;
  motivationalQuoteDe: string;
}

export interface TrainingPriorityRecommendation {
  category: SkillCategory;
  calculatedPriority: number;
  priorityRank: number;
  reasonHeadline: string;
  detailedRationale: string;
  recommendedTaskTitle: string;
  sessionDurationMin: number;
  targetEnvironment: string;
  weightBreakdown: {
    severityOfWeaknessPct: number;
    functionalImportancePct: number;
    recurringErrorsPct: number;
    daysSinceTrainedPct: number;
    fatiguePerformancePct: number;
  };
}

export type DiagnosticMode = 'FULL_COMPETENCY' | 'GRAMMAR_AND_VOCABULARY';

export type DiagnosticTaskType =
  | 'AUDIO_LISTENING'
  | 'RECORDED_SPEAKING'
  | 'INTERACTION_DIALOGUE'
  | 'READING_COMPREHENSION'
  | 'WRITING_PRODUCTION'
  | 'GRAMMAR_PRECISION'
  | 'VOCABULARY_COLLOCATION'
  | 'MEDIATION_TRANSFER';

export interface DiagnosticTask {
  id: string;
  taskType: DiagnosticTaskType;
  skillCategory: SkillCategory;
  headlineDe: string;
  contextDe: string;
  promptText: string;
  spokenAudioText: string | null;
  speechSpeed: number;
  dialectNote: string | null;
  multipleChoiceOptions: string[];
  correctOptionIndex: number;
  analysisRationaleDe: string;
  maxLatencyBenchmarkSeconds: number;
  subTopicName: string;
}

export interface DiagnosticUserAnswer {
  selectedOptionIndex: number | null;
  freeformText?: string;
  isEvaluatedCorrect: boolean;
  earnedScore: number;
}

export interface BaselineProfileRecord {
  id: string;
  listeningScore: number;
  speakingScore: number;
  interactionScore: number;
  readingScore: number;
  writingScore: number;
  grammarScore: number;
  vocabularyScore: number;
  mediationScore: number;
  overallC1Score: number;
  functionalReadinessPct: number;
  primaryLimitingFactor: string;
  recommendedStartingEngine: string;
  isCompleted: boolean;
  assessedAtTimestamp: number;
}

export interface ProficiencySnapshot {
  id: string;
  timestamp: number;
  periodLabel: string;
  overallScore: number;
  functionalScore: number;
  listeningScore: number;
  speakingScore: number;
  readingScore: number;
  writingScore: number;
  grammarScore: number;
  vocabularyScore: number;
}

export interface LearningPathMilestone {
  stage: number;
  title: string;
  targetArea: string;
  focusDescription: string;
  estimatedDurationDays: number;
  isPrimaryFocus: boolean;
}

export type CoachTermCategory =
  | 'OFFENSIV'
  | 'DEFENSIV'
  | 'UMSCHALTEN'
  | 'KOMMUNIKATION';

export interface FootballCoachTerm {
  id: string;
  term: string;
  category: CoachTermCategory;
  tacticalMeaning: string;
  pitchCommand: string;
  avoidInstead: string;
}

export interface TacticalScenario {
  id: string;
  title: string;
  matchPhase: string;
  situationDescription: string;
  options: string[];
  correctIndex: number;
  coachingExplanation: string;
  keyCommandToShout: string;
}

export interface DrillCommunicationPattern {
  id: string;
  drillTitle: string;
  ageGroupFocus: string;
  setupSummary: string;
  liveCommands: string[];
  avoidPhrase: string;
  positiveCorrection: string;
}
