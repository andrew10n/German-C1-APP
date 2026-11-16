# Programme implementation and content readiness

## Scope and current status

### Implementation update — 2026-11-15

The next completion pass extended the existing foundations without claiming external work as finished:

- Assessment sessions are now Room-backed (`assessment_sessions`, schema migration 9→10), including task payloads, submitted responses, current task index, completion state and updated timestamps. The dashboard saves on start and after each answer and restores the latest unfinished session after screen recreation.
- Timed sessions remain familiar text practice. Separate local listening practice accepts audio selected by the learner and stores listening notes; speaking practice records an answer in app-private storage. Neither workflow is automatically scored, measures acoustic speaking quality, creates unfamiliar-task evidence, or contributes to mastery. Recorded audio and its filename are excluded from JSON export and removed by delete-all.
- Delayed-checkpoint scheduling now measures intervals from the earliest qualifying attempt, so later eligible attempts can satisfy the scheduled milestones. Roadmap-only outlines are retained in the active catalogue; any such outline in the graduation core is reported as a blocker, while an authored unit with the same stable ID replaces its outline.
- Practice retrieval uses shared scheduling rules; the mastery contract retains its explicit 1/3/7/21-day checkpoint intervals and the one-day error retry is centralized.
- Feedback challenges now have an explicit repository resolution operation with `ACCEPTED`, `REJECTED` and `NEEDS_REASSESSMENT` states. This is a local workflow foundation, not authenticated reviewer identity or a completed reassessment service.
- Settings provide a user-controlled JSON export of local learner records. Android cloud/device backup rules explicitly include the programme database and preferences when platform encryption is available.
- Lesson response drafts now autosave locally per unit, task and context, survive screen/process recreation, appear in JSON export and are removed by delete-all. Successfully saved responses clear their draft; failed saves retain it.
- The standalone audio-source destination and lesson links to external podcast episodes were removed. The lesson's authored listening script stays in the guided flow; learners can play the synthetic German version at normal or slower speed, compare the speaker-labelled transcript, or select their own course audio for in-app playback. Selected audio remains at its original URI and is not copied into the APK.
- Local source review found the Aspekte neu C1 Kursbuch/Arbeitsbuch and chapter audio, plus Sicher! C1/1 books, transcripts and chapter audio under `D:\German\C1\Sicher C1\Book\Sicher C11`. The app uses these only as private, learner-selected audio and high-level theme/skill references. It does not bundle, quote or reproduce publisher text, transcripts or recordings; source-file matching remains the learner's responsibility.

### Implementation update — 2026-11-16: visual learning dashboard

The programme dashboard received a visual microlearning-oriented redesign inspired by public visual-learning patterns such as short learning moments, a single clear next action, strong visual anchors and progressive disclosure. This is an original implementation and does not copy the reference app's branding or assets.

- Primary navigation is limited to Lessons, Progress and Settings. Lessons leads with the adaptive next lesson and keeps the staged course map and search secondary. Progress leads with automatically updated practice-coverage stars for Hören, Sprechen, Schreiben/Mediation and Interaktion, plus a local mistake log; stars are explicitly not proficiency ratings. Longer analytics and evidence details are tucked behind an expansion. Settings groups personal preferences and keeps legacy/reviewer controls in an advanced section.
- Listening stays inside the selected lesson: the original authored script can be played with the device's synthetic German voice at normal or slower speed, followed by transcript comparison and a saved response. The standalone source catalogue and lesson links to third-party audio were removed from this guided path; no podcast recordings or transcripts are copied into the app.
- Lessons starts with one adaptive recommendation and a staged roadmap; search is revealed only on request. There are no standalone skill destinations or raw curriculum metadata on the landing view.
- Progress shows automatically updated practice-coverage stars and a local mistake log first. Detailed checkpoints, practice signals, adaptive plans, review schedule and evidence boundaries are available under an expansion. Settings contains personal preferences and privacy controls; legacy/reviewer-related tools remain under Advanced.
- Dashboard UI tests cover the focused home view, on-demand course browsing/search, progress boundaries, advanced navigation and English UI labels. Full-suite validation is rerun for this change.

### Implementation update — 2026-11-16: adaptive lesson content

- The lesson controller now selects the unit, current task and recommended context from eligible saved evidence, then recalculates after each response. A fresh lesson guides the learner through retrieval, one reading-or-listening input, a comprehension check, an animated visual language map and an adaptive application activity. Due personal repairs and spaced lesson reviews can interrupt that sequence; completed lesson reviews reschedule from the saved retrieval response.
- The lesson view presents one activity at a time with a fixed Back/Next footer; advancing past response activities is gated on successful persistence, and drafts remain available if saving fails. The complete authored lesson is available only through the secondary “Show full lesson” control.
- The default lesson now carries the unit's original concept illustration through recall, input, comprehension and application, animating the highlighted stage as learners advance. Listening turns, reading chunks and the language map keep their separate visual treatments; diagrams explain the learning idea rather than decorating unrelated text.
- Recognized local response patterns add a targeted repair activity that refers to the learner's own saved text, the detected pattern and the unit objective. A due personal repair adds its own transfer prompt and advances its review schedule only after a response is successfully saved.
- A new transfer challenge is composed only after a correct objective check, at least three unassisted production responses across at least two contexts, and no active locally recognized text error. It rotates the unit objective and selects an unused context when available. This is a harder practice prompt, not proof of proficiency.
- Weakness evidence excludes future-dated records and decays with recency. A later clean response for a task clears that task's active local error from content selection; older repetitions remain available for spaced error-history counts but cannot keep forcing a stale repair.
- These are local, deterministic adaptations over authored curriculum and templates. The app does not generate arbitrary new reading passages or authentic audio, call a generative service, or claim a precise CEFR level. Those capabilities would require independently validated content and assessment, not just a smarter selector.

### Lesson studio and learning design

- The lesson opening now visualizes four concrete phases—retrieval, comprehension, application and transfer—with animated progress tied to saved task activity. It does not represent that activity as a language score.
- Original reading passages are divided at their authored paragraph boundaries and revealed one section at a time. Learners can move back and forth without losing the full source text used by the response exercise.
- Listening scripts reveal as speaker-labelled conversation cards after the first attempt, preserving the blind-first listening workflow while making turn changes visible.
- The lesson opening now leads with an original vector infographic selected for each authored unit (for example, argument structure, evidence limits, negotiation, summarising or retrieval). Its animated, tappable concept map connects the learner-selected context, an authored language move and its communicative effect; selecting another phrase updates the explanation. Secondary objectives and full grammar remain available on demand, while context examples show only the active selection.
- The learning sequence combines retrieval before review, comprehension checks, controlled production, freer production and a new-context transfer task, with spaced review handled separately. This draws on open retrieval-practice guidance from [The Learning Scientists](https://www.learningscientists.org/retrieval-practice); it does not reproduce their materials.
- Research review: [DW Learn German](https://learngerman.dw.com/de/deutsch-lernen/s-9095) advertises free materials from A1 through C1. [Langsam Gesprochene Nachrichten](https://learngerman.dw.com/de/langsam-gesprochene-nachrichten/s-60040332) is explicitly labelled B2 and includes audio plus text, so it is useful as a scaffold model, not as C1-level evidence. The [Council of Europe's CEFR Companion Volume](https://rm.coe.int/common-european-framework-of-reference-for-languages-learning-teaching/16809ea0d4) informs the separation of reception, production, interaction and mediation. These resources inform original task design only; no source passages or recordings are copied into the app and learners are not sent out of the lesson.
- Progress now covers all 28 authored units: 26 sequenced core lessons and 2 optional orientation lessons. Orientation checkpoints count when completed, but neither orientation lesson is required to unlock the core route.

### Current implementation notes

- The local JSON export is now version 2 and gathers all available learner records: attempts, assessment tasks/responses and timestamps, feedback challenges, local review assignments/audit records, real-life entries, source-listening notes, practice drafts, preferences/bookmarks, and retained legacy baseline, vocabulary, streak, and progress records. Audio bytes and the selected document URI are deliberately excluded. Export is asynchronous from the settings UI and reports failures.
- Room schema 14 adds due-date and source indexes for vocabulary, compound unit/timestamp and timestamp indexes for attempts, due/date indexes for real-life reports, and a completed/updated index for assessments. The in-memory attempt-history index groups records by unit, appends the common new-attempt case incrementally, and rebuilds on deletion or non-append changes.
- Vocabulary and course-expression cards now use a shared quality-adaptive SM-2 schedule: recall quality adjusts ease and the interval grows from that card's own successful history; a miss resets repetitions and schedules a one-day retry. The fixed 1/3/7/21-day intervals remain the separate protected mastery-checkpoint contract.
- Book-audio URI permissions are retained through Android's document picker and stored separately from app data. User-selected recordings now support seeking, 0.75×/1×/1.25× playback and A/B segment repetition; the app still does not bundle publisher audio.
- Long German speech responses request longer silence thresholds and API 33+ segmented sessions. Final segment results are accumulated into one editable transcript; recognition-provider support varies, and this remains transcript capture rather than acoustic speaking evaluation.
- Each authored unit's expressions can be added from its lesson to the existing vocabulary retrieval queue. Cards retain their source-unit ID, use stable IDs and insert-ignore seeding so reviewed progress is not reset.
- Course expressions are added only for the open unit when the learner taps the lesson action; the entire course is not dumped into the due queue at first launch. Repeated taps do not reset existing review history.
- Feedback challenges preserve their cited attempt/excerpt, use explicit statuses, and reject a second resolution. Schema 13 adds source snapshots while preserving pre-existing legacy rows through migration 12→13.
- Saved attempts can receive a local human-review note with all ten rubric dimensions. Reviewer identity and scores are explicitly unverified; notes do not rewrite attempt provenance or affect progression.
- Quiz choices expose grouped radio-button selection semantics to accessibility services. A single real-life diagnostic report now contributes at most one sample per diagnostic dimension, so duplicate observations cannot inflate confidence.
- There are two intentionally separate curriculum models: the active `programme.CurriculumCatalogue` has 28 authored lessons; `programme.core.CurriculumCatalog` is the older policy/planner model with three authored entries and 25 roadmap entries. The old model remains for legacy consumers and its stored records are not imported as active course evidence.
- The unused Firebase AI, App Check, Retrofit/OkHttp and Moshi runtime dependencies and related build plugins were removed. `metadata.json` no longer advertises a server-side Gemini capability. The app has no configured AI/backend call path.
- The Gradle SDK configuration uses compile SDK 34; `README.md` now reflects it.
- Release minification and resource shrinking are enabled and both Gradle tasks complete; R8 still emits Kotlin-metadata compatibility warnings. No upload keystore is present, so a signed release artifact is not verified.

Remaining beyond the private adaptive route: independently scored listening and acoustic speaking assessment, protected unfamiliar tasks, a verifiable record/review service for qualified curriculum and rubric calibration, broader validated language diagnostics, physical-device validation, crash/release operations, cross-device identity/sync and the four-week learner pilot. Structural curriculum tests check completeness and consistency only; they cannot establish C1 level, factual accuracy or scoring reliability.

### Sequential follow-up status

- **Build and dependency recovery:** completed. The project now uses the installed Android 34 SDK, Android Gradle Plugin 8.7.3, AndroidX-enabled Gradle settings and SDK-34-compatible Core/Activity versions. `assembleDebug`, `testDebugUnitTest` and `lintDebug` pass.
- **Reviewer UI removed:** this is a single-learner app. New route progression uses diagnostic outputs, not self-ratings or teacher review. Legacy review tables remain for migration/data compatibility only.
- **Privacy controls:** completed locally. Settings provide JSON export and a confirmed delete-all action for Room data and programme preferences. Exports and platform backups are correctly described as separate copies.
- **Curriculum audit:** completed. The catalogue exposes 28 distinct, fully authored units; existing tests verify content, task coverage, contexts, prerequisites and stable IDs.
- **Audio rights gate:** completed. Synthetic TTS remains labelled synthetic; no publisher recordings are bundled. The listening lab plays audio selected by the user through Android's document picker and retains only a local content URI. The owned-audio manifest rejects assets without application-use release, verified transcript and required speaker metadata.
- **External listening practice:** official links open Easy German's episode catalogue, DW Deutsch lernen, Deutschlandfunk's *Der Tag* feed and ARD Sounds. Selected lessons now recommend specific official Easy German episodes. Each provider offers a two-pass solo workflow: capture a blind first response, then compare against captions/transcripts only when the publisher provides them. Notes persist locally, are included in the user-controlled export and are removed by delete-all; they are not progression evidence. Media is not downloaded, copied or bundled.
- **Lesson drafts and book references:** written lesson drafts persist by unit/task/context and are included in export/delete. Optional Sicher! C1/1 references map tourism to negotiation, art criticism to argumentation, and university/study communication to mediation, each with a new prompt and workbook-audio filename hint.
- **Response feedback handoff:** written lesson responses, adaptive production/transfer responses and ASR transcripts can be shared through an explicit confirmation and Android's share sheet. The exact payload is visible and editable before sharing. Lesson reading and listening requests now include their authored source text for a more useful comparison. The prompt asks for quoted evidence, separates corrections from style suggestions, flags uncertainty and prohibits CEFR scores. The app has no provider integration, sends nothing automatically, receives no response and cannot validate third-party feedback.
- **Context depth:** each authored unit now turns the selected general, social, automotive or youth-football scenario into a concrete solo writing task with a self-check. These saved responses remain self-reflection, not a transfer pass or formal progression gate.
- **Physical-device check:** attempted. The debug APK is generated, but no usable ADB daemon or connected Android device is available in this environment, so microphone, TTS, audio-focus and process-death smoke tests remain pending.
- **Pilot readiness:** the executable four-week protocol is documented in `PILOT_EXECUTION_PLAN.md`; no pilot data or learning outcome is claimed. This is optional for personal practice and only needed to evaluate outcomes systematically.
- **Legacy reviewer records:** Room schema 11 reviewer assignments, audit events and reassessment links are retained for existing databases. They are not used by the private adaptive route and do not authenticate anyone.
- **Task 2 status corrected:** the weekly session kind and seven-day scheduler exist, but delivered tasks use familiar authored text prompts. Earlier mixed audio, multi-turn and unfamiliar-evidence labels overstated what the text-only UI collects; current tasks are explicitly `TEXT_RESPONSE`, `unfamiliar = false`, and saved as self-reflection. There is no protected unseen assessment, audio/speaking assessment or verified score.
- **Evidence-integrity correction:** the app exposes no local reviewer or self-rating path. Schema 12 downgrades legacy locally asserted human-review attempts to unscored self-reflection, clears old self-reflection assessment novelty flags and reviewer-verification flags, and preserves original responses and review notes. Resumed legacy assessment sessions are normalized to text-only, familiar practice.
- **Adaptive route:** a practice checkpoint requires a correct objective quiz, two different unassisted production-task responses and no currently recognized text-pattern error. The controller now unifies lesson and task selection, filters future evidence, rotates task/context practice, inserts local repair prompts and conditionally adds a varied transfer challenge. It hides non-current lesson sections by default while retaining the full authored lesson on request. This navigates private practice; it is not CEFR mastery or a reliable grade of open text.
- **Task 3 completed:** adaptive recommendations now produce objective-linked `ErrorRecyclingPlan` records with repeated-error counts, spaced next-review timestamps, support/core difficulty and remediation instructions. Repeated patterns affect practice difficulty only; they never create mastery, proficiency or CEFR evidence. Task 3 validation passed `assembleDebug`, `testDebugUnitTest` and `lintDebug`.
- **Task 4 completed:** added deeper curriculum QA for all 28 units: distinct reading/listening materials, minimum production depth, valid distractors, draft-marker checks, unsupported-certification checks and context-specific automotive/youth-football boundaries. Task 4 validation passed `assembleDebug`, `testDebugUnitTest` and `lintDebug`; independent teacher calibration is only needed for external validation claims.
- **Task 5 completed:** added persistent first-run onboarding with explicit practice-versus-evidence boundaries, synthetic-audio disclosure, local-data/privacy explanation and access to clearly labelled settings. Task 5 validation passed `assembleDebug`, `testDebugUnitTest` and `lintDebug`. Full TalkBack and physical-device accessibility validation still requires hardware.
- **Task 6 completed:** added `RELEASE_READINESS.md` plus provider-neutral account, sync and non-sensitive crash-reporting contracts with local-only no-op providers. No credentials or backend are claimed. Task 6 validation passed `assembleDebug`, `testDebugUnitTest` and `lintDebug`.

This is an implemented programme foundation, not a claim that the full C1 programme is independently validated. It provides a persistent, personalized curriculum dashboard, 28 fully authored playable units, saved attempts, an adaptive diagnostic practice route, familiar timed text sessions, separate unscored local audio-practice workflows and a separate formal-evidence policy. The content is original training material with complete lesson fields and four contextual tracks; independently scored authentic audio, acoustic speech assessment and broader diagnostic validation remain outstanding. Existing practice remains accessible without teacher review.

**28 authored communication units across 7 stages.** Every unit contains objectives, reading, listening transcript, expressions, grammar focus, comprehension check, controlled production, speaking, interaction, writing, transfer and feedback content. Every unit also carries general, social, automotive-engineering and youth-football-coaching context material. The units are complete as authored practice content; native audio and externally validated assessment remain separate gaps. External teacher review is optional for this private app.

### External C1 alignment audit

The curriculum was checked against the Council of Europe **CEFR Companion Volume (2020)** and the Goethe-Institut **Goethe-Zertifikat C1** module structure. The current catalogue covers the major activity types: complex reading, listening comprehension, structured writing, spoken presentation, discussion, interaction, mediation, register and transfer. The Goethe format additionally makes the required performance conditions explicit: sustained authentic listening, argument-focused reading, a structured opinion text/formal message, and a prepared presentation followed by discussion. The catalogue therefore provides a strong training base, not an automatic guarantee of C1 attainment.

For external C1-readiness claims, the remaining high-impact gap is repeated performance under exam-like and real-world conditions with reviewed multi-speaker audio, delayed unfamiliar tasks, and feedback evidence that can distinguish comprehension, formulation, interaction and naturalness. Device TTS and generated conversation variations now provide practice volume and unpredictability, but they are labelled synthetic and are not substitutes for authentic recordings or an external result.

Sources: [Council of Europe CEFR Companion Volume](https://www.coe.int/en/web/common-european-framework-reference-languages/cefr-companion-volume) and [Goethe-Zertifikat C1](https://www.goethe.de/ins/de/en/prf/gzc1.cfm).

### Validated-audio and mastery foundation

`AudioSourceRegistry` records the current source decision for FOLK, DGD, Pfeffer, Zwirner, GeWiss, Common Voice German and Goethe practice audio: corpus access is not treated as redistribution permission; Goethe material remains protected; Common Voice is reserved for short release-verified samples rather than coherent dialogue. No restricted recording is bundled.

`OwnedDialogueLibrary` contains recording briefs for supermarket/customer service, supervisor and automotive meetings, informal/regional conversations, and youth-football contexts. Each brief requires speaker metadata, verified transcripts, natural turn-taking, regional information where relevant, and explicit application-use release. It is a commissioning specification, not a claim that recordings already exist.

`ExternalListeningLibrary` provides official links to public listening sources with a two-pass blind-listening workflow and response notes. `ExternalListeningPracticeRepository` persists the learner's latest notes and optional episode title/link per source; they are included in local export and all-data deletion. Optional feedback sharing sends only the learner's typed notes after confirmation; the app does not share audio or transcript material. Availability and accuracy of captions/transcripts vary by item. The application opens each source externally and does not download or bundle it.

`LearnerDiagnosticEngine` extracts cautious findings from objectively scored quiz items, known grammar-pattern flags and learner-reported real-life difficulties. A clean free-text response is not marked demonstrated; text roleplay is not treated as spoken interaction, and text exercises do not count as listening. `AdaptiveDiagnosticRoute` drives the private practice sequence using one correct quiz item, two distinct unassisted text tasks and remediation for detected patterns. Its completed checkpoint is not a language score. `MasteryAssessmentContracts`, `GoetheC1TaskContracts` and `FourWeekPilotProtocol` describe future external assessment requirements, not features currently delivered.

`AdaptiveControlEngine` is the single daily-priority decision point. It uses fragile diagnostic findings, recurring error signals and real-life difficulties to reorder existing practice or request a conversation variation; it cannot invent proficiency evidence.

The controller now also selects an intervention sequence and redistributes the available daily minutes toward the active weakness. Listening signals begin with listening and comprehension before production; formulation signals begin with retrieval and controlled production; interaction signals begin with listening, interaction and speaking. This is still a deterministic local controller, but ordinary practice responses now pass through the existing grammar-signal detector before persistence, so detected evidence can influence later planning rather than being discarded.

Owned audio has an intake manifest in `OwnedAudioAssetManifest`. An asset cannot be considered playable until it has a valid dialogue brief, multiple speakers, an audio path, a verified transcript and explicit application-use release. No manifest entry bypasses licensing.

Free-form practice responses are persisted with deterministic response signals instead of an empty error list. `FeedbackEvidenceEngine` turns those signals into claims anchored to the learner's exact excerpt and rejects claims that cannot be found in the original response. Feedback challenges are stored in Room, shown with their cited response evidence, and can be resolved once with an explicit outcome. This local workflow is separate from route progression and does not validate the proposed feedback.

`AssessmentDelivery` creates timed familiar text-practice sessions from authored course prompts, with deterministic audience/pressure/constraint variations. The Goethe-oriented option is a short text exercise, not an exam simulation; weekly and pilot options also collect text only. Separate local listening practice uses learner-selected audio and notes; speaking practice can store a local audio recording. Neither scores performance or counts as unfamiliar or formal assessment evidence. Session state is Room-persisted and resumable after screen recreation.

`HumanReviewRepository` stores local review notes linked to an existing attempt. The UI does not authenticate reviewers, mark them verified, or convert the note into `HUMAN_REVIEW` attempt evidence. Migration 11→12 downgrades legacy client-asserted human-review evidence and novelty flags while retaining learner responses and review text; resumed legacy assessment sessions are normalized to text-only, familiar practice.

### Variation, calibration and model-provider status

The existing `ConversationVariationEngine` keeps authored conversation scenarios and adds two deterministic multi-turn variations with follow-up questions and interruption/disagreement objectives. Assessment delivery separately applies deterministic condition variations across audience, pressure and response constraint. Variation IDs and task/rubric versions are stored with delivered tasks and assessment attempts; default assessment variations rotate by UTC day. These variations change performance conditions, but they do not generate wholly new factual dialogue or authentic audio.

Calibration has not yet been performed: no qualified reviewer score set, inter-rater agreement or task-difficulty results are present. That is not a prerequisite for the personal practice route, but it is required before describing diagnostic findings as validated language assessment or CEFR evidence.

The unused Firebase AI, App Check, Retrofit/OkHttp and Moshi dependencies and related build plugins have been removed. `metadata.json` does not advertise server-side Gemini. `ProductionIntegrationContracts` supplies local no-op account, sync and crash-reporting providers; no backend or AI service is configured.

## Complete curriculum map

IDs are stable persistence keys; historical ID prefixes do not determine stage placement.

| Stage | Unit ID | Communication function | Readiness |
|---|---|---|---|
| 0 Diagnostic | s0-profile | Sprachbiografie und Zielprofil | Authored |
| 0 Diagnostic | s0-diagnose | Verstehen und Produzieren diagnostizieren | Authored |
| 1 B2 consolidation | s0-repair | Verständnisprobleme sichtbar machen | Authored |
| 1 B2 consolidation | s0-retrieve | Abrufen statt Wiederlesen | Authored |
| 1 B2 consolidation | s1-explain | Einen Ablauf verständlich erklären | Authored |
| 1 B2 consolidation | s1-justify | Eine Entscheidung nachvollziehbar begründen | Authored |
| 1 B2 consolidation | s1-uncertainty | Unsicherheit präzise ausdrücken | Authored |
| 1 B2 consolidation | s1-feedback | Konstruktiv Rückmeldung geben | Authored |
| 2 Advanced comprehension | s2-data | Daten und Einschränkungen verstehen | Authored |
| 2 Advanced comprehension | s4-challenge | Annahmen kritisch prüfen | Authored |
| 2 Advanced comprehension | s5-nuance | Bedeutungsnuancen verstehen | Authored |
| 3 Advanced production | s4-argument | Komplexe Argumentation strukturieren | Authored |
| 3 Advanced production | s4-present | Adressatengerecht präsentieren | Authored |
| 3 Advanced production | s5-revise | Texte stilistisch überarbeiten | Authored |
| 4 Interaction and pragmatics | s2-register | Register bewusst wechseln | Authored |
| 4 Interaction and pragmatics | s3-negotiate | Interessen aushandeln | Authored |
| 4 Interaction and pragmatics | s3-disagree | Differenziert widersprechen | Authored |
| 4 Interaction and pragmatics | s3-deescalate | Konflikte deeskalieren | Authored |
| 4 Interaction and pragmatics | s3-agree | Vereinbarungen absichern | Authored |
| 4 Interaction and pragmatics | s5-spontaneous | Spontan zusammenhängend reagieren | Authored |
| 5 Integration and mediation | s2-summarise | Kernaussagen verdichten | Authored |
| 5 Integration and mediation | s2-mediate | Zwischen Perspektiven vermitteln | Authored |
| 5 Integration and mediation | s4-ethics | Dilemmata erörtern | Authored |
| 5 Integration and mediation | s5-facilitate | Mehrparteiengespräche moderieren | Authored |
| 6 Independent transfer practice | s6-general | Unvorbereiteter allgemeiner Transfer | Authored |
| 6 Independent transfer practice | s6-specialist | Unvorbereiteter fachlicher Transfer | Authored |
| 6 Independent transfer practice | s6-retention | Langfristige Verfügbarkeit üben | Authored |
| 6 Independent transfer practice | s6-checkpoint | Übungsprofil auswerten und weiterlernen | Authored |

### Authored content inventory

Twenty-eight original German readings, dialogue listening scripts, comprehension MCQs with distractor explanations, controlled-production prompts and models, spontaneous-speaking tasks, interaction tasks, writing tasks, transfer prompts and illustrative feedback examples. Each also contains objectives, expressions/collocations, contextual grammar, four context prompts, retrieval and rubric guidance.

Automotive tasks explicitly include EV/hybrid, ECU/TCU interfaces, FMEA, safety evidence and root-cause uncertainty. Football tasks explicitly concern youth players, pass drills, shoulder checks, coaching feedback, parents and training organisation. Social prompts introduce modal particles and context-sensitive southern German/Swabian interpretation without treating regional speech as a stereotype.

This does **not** yet supply comprehensive independent review of every engineering/coaching subtopic, native multi-speaker recordings, or regional audio. Some source tasks are deliberately accessible B2-consolidation material, not a claim of C1-level complexity throughout.

## Data and progression

### Persistent evidence

Room schema version **14** adds programme attempts, feedback challenges, human reviews, resumable text-practice sessions, reviewer workflow records and targeted indexes without deleting legacy tables. Migration 11→12 downgrades locally self-asserted `HUMAN_REVIEW` records to unscored self-reflection and clears old novelty and reviewer-verification flags; migration 12→13 adds source snapshots to feedback challenges; migration 13→14 adds due/source indexes and course-expression provenance. Older migration paths remain registered. Destructive upgrade/downgrade fallback was removed.

Attempts store stable unit/task IDs, response, timestamp, source, criterion scores, context, assistance/novelty/independent-production flags, errors and task/rubric versions. Inserts abort on duplicate IDs rather than replacing evidence. JSON preserves response-related structured fields. UI/repository errors are exposed instead of assuming successful saves.

Sources are distinct:
- `AUTOMATIC_QUIZ`: correctness on a particular authored question only.
- `SELF_REFLECTION`: saved learner text or reflection; no proficiency score.
- `HUMAN_REVIEW`: reserved for trusted external evidence. The local review-note UI writes to a separate table and cannot assign this attempt source or establish mastery.

### Internal mastery policy

The policy is conservative and **not empirically calibrated to certify CEFR C1**. In-app tasks and automatic diagnostics cannot meet the independent-review or protected unfamiliar-transfer requirements, so they cannot advance this formal mastery policy. A permanent graduation gap also blocks completion until independent curriculum/rubric calibration and protected listening/speaking assessment are evidenced. The separate adaptive route advances practice checkpoints without calling them mastery:

1. A unit must have authored content.
2. At least three different successful task IDs must have human-review provenance, unassisted independent production, and scores of at least 3/4 in **every** one of the ten dimensions. No averaging hides a weak dimension.
3. Successes cover at least two contexts and two UTC days.
4. A successful unfamiliar task must occur at least seven days after the first successful performance.
5. Duplicate/conflicting evidence IDs cannot inflate independent evidence.

Dimensions: accuracy, fluency, range, naturalness/collocations, coherence, interaction, pragmatics, register, comprehension and mediation. The UI supplies dimension-specific descriptors and explains that text cannot establish pronunciation or real interaction.

Formal prerequisites: `justify` and `uncertainty` require `explain`; `feedback` requires both. Formal progression also requires mastery of authored earlier-stage units. The private adaptive route uses its practice-checkpoint prerequisites instead. All 28 authored units remain open for free practice.

Free practice is separate: all 28 authored lessons can be opened regardless of route status. The adaptive route advances after its limited checkpoint criteria, while the formal mastery policy remains unfulfilled and no C1 certification is claimed.

### Adaptation and retrieval

The dashboard selects due practice review and detected remediations before the next uncompleted practice checkpoint. Existing legacy training-priority and vocabulary-SRS systems remain available rather than being duplicated or replaced wholesale.

The adaptive practice route schedules review after objective quiz successes on distinct days; currently recognized text errors schedule a one-day repair. The separate vocabulary/expression cards use quality-adaptive SM-2 intervals, while the formal `MasteryEngine` retains the explicit 1/3/7/21-day checkpoints and stricter reviewed-evidence rules. Neither schedule certifies retention or language proficiency.

The private route prioritizes due practice review, detected pattern repair, and objective quiz failures; it does not require external review to continue. Diagnostic coverage remains narrow and deterministic, and choosing a new context does not by itself demonstrate transfer.

### Adaptive practice guidance

The programme now runs local, conservative pattern checks on saved learner responses. It can flag a small set of high-confidence revision prompts (for example subordinate-clause verb position, `seit/seid`, `dass` punctuation and sentence capitalization), link the prompt to a unit objective where possible, and recommend support, core practice or a challenge transfer. Repeated weak signals and incorrect comprehension checks move the learner toward remediation; several clean, recent attempts can move the learner toward a challenge.

These are revision suggestions, not automatic grades. They do not create rubric scores, human-review provenance, mastery, CEFR claims or pronunciation judgments. The current detector is intentionally narrow and deterministic; a broad automatic grammar/style evaluation and validated diagnostic remain future work.

The programme now also includes a persistent real-life difficulty loop. The learner can record what happened, what they heard, what they wanted to say, what they actually said, the context and the type of difficulty. Each entry receives a 1/3/7/21-day repair schedule, appears on the dashboard when due, and influences the next adaptive recommendation without creating a score or mastery evidence. A small deterministic repair engine provides concrete patterns for common cashier, engineering, coaching and conversational-repair situations; uncertain entries stay explicitly generic rather than being presented as automatic correction.

Due repair reviews also support a voice-first attempt. The learner can hear the German transfer prompt through device TTS, answer through the existing German speech-recognition service, and save the final transcription as the review response. Partial recognition text is not shown during the first attempt; model phrases and transfer guidance are revealed only after a response exists. Manual text entry remains available when microphone permission or device recognition is unavailable.

The `WeaknessEngine` derives granular practice signals from these reports and from existing attempts that contain explicit errors. It distinguishes listening vocabulary, situational inference, active retrieval, grammar under pressure, naturalness, register, conversational repair, interaction management and related signals. Each signal retains context, evidence excerpt, source and cautious confidence; repeated signals are prioritized using recurrence, cross-context spread and recency. The dashboard shows sample counts and confidence labels, while adaptive recommendations use recurring signals to select support mode. These signals guide practice only and do not create mastery or proficiency scores.

`LearnerDiagnosticEngine` extracts provisional findings from ordinary answers without adding a separate mandatory test. Only scored quiz items can be marked demonstrated. A known text-pattern flag can request repair; no detected error is not proof of correctness. Typed listening, speaking and interaction practice remain unobserved for acoustic, spontaneous or live-interaction abilities. Real-life reports can prioritize practice, but remain self-reported.

The conversation simulator now expands each authored scenario into a reproducible base version plus two variations. Variations add an extra follow-up, disagreement or information-gap turn, shorten the interruption window, and change the response objective without replacing the authored task. Partner, follow-up and interrupter turns use distinct device-TTS speaker profiles, and the UI labels this as a synthetic multi-speaker version. No native or licensed recording is claimed: real multi-speaker audio still requires reviewed audio files and distribution rights.

### Daily mode

Persisted 20/60/90-minute plans mix receptive and productive activities, including reading, synthetic listening, comprehension, controlled production, speaking, interaction and writing. Time allocations are budgets, not a running timer or proof of engagement. Weekly session types and scheduling exist, but their delivered tasks are familiar text practice, not an integrated C1 assessment.

The learner is no longer blocked by a one-time baseline assessment. The legacy diagnostic data and screen remain only for backward-compatible stored records and are not used to gate the current programme flow. The daily plan now receives current attempts and personal reports, maps the strongest recurring signal to a practice mode, and moves that block to the front while preserving the time-budget and receptive/productive balance. This is an evidence-driven adaptive controller, not a fabricated proficiency model or an opaque AI score.

### Graduation safeguards

All authored competencies must be mastered; advanced outlines remain explicit blockers. Separately, each checkpoint stream needs two different successful unfamiliar tasks on different UTC days:

| Stream | Context | Task ID family |
|---|---|---|
| General | GENERAL | unseen-general:<variant> |
| Colloquial/social interaction | SOCIAL | unseen-social:<variant> |
| Automotive engineering | ENGINEERING | unseen-specialist:<variant> |
| Youth-football coaching | COACHING | unseen-specialist:<variant> |
| Integrated general assessment | GENERAL | unseen-integrated:<variant> |

For external C1 certification only: the separate formal graduation contract uses `unitId = graduation` and requires all ten dimensions, distinct contexts, and unseen tasks. Engineering cannot substitute for football or social interaction. The app does not currently deliver a protected assessment battery, authenticate reviewers, or issue certification. Formal graduation is explicitly blocked until independent curriculum/rubric validation and protected listening/speaking assessment are evidenced; ordinary private practice remains available.

## UI and assessment integrity fixes

- New personalized programme home: graphite/teal Trackside, Silver and Pitch themes; saved learner name; compact/comfortable spacing.
- No invented initial measured skill scores, historical snapshots or streak seeds.
- Keystrokes, playback, model reveals and session completion do not earn proficiency points or error mastery.
- Existing diagnostic is explicitly a short MCQ practice screening. Untested dimensions are unassessed; timing cannot manufacture pronunciation or CEFR readiness; screening does not replace a saved baseline.
- Removed simulated microphone-response insertion. New runner speaking/dialogue practice is explicitly external, with typed reflection only.
- Existing speech feedback is labeled transcript agreement, not acoustic phoneme/prosody assessment. Demo mode cannot submit scores/activity.
- Synthetic TTS is labeled honestly, reports availability failures and does not fall back to English. Simulated playback duration/progress awards were removed.
- Replaced hardcoded dashboard/grammar/proficiency charts with unmeasured notices rather than invented evidence.
- Corrected the diagnostic rejection phrase to `aufgrund gravierender formeller Mängel` and retained corrected relative-clause/mediation examples.
- Legacy stored records are preserved, not silently deleted or retrospectively validated. Old baseline data must not be treated as newly measured proficiency.

## Files created and modified

Paths below are relative to the project root. This is a task inventory, not a Git diff: the recovered workspace has no `.git` repository.

### Created programme code

Under `app/src/main/java/com/example/programme/`:
- `ProgrammeModels.kt`
- `CurriculumCatalogue.kt`
- `MasteryEngine.kt`
- `ProgrammeRepository.kt` (entity, DAO, serialization and repository)
- `RealLifeExperienceRepository.kt` (Room-backed real-life difficulty records and spaced repair)
- `RealLifeRepairEngine.kt` (deterministic situation-specific repair prompts)
- `ProgrammeViewModel.kt`
- `ProgrammeScreen.kt`

### New tests

Under `app/src/test/java/com/example/programme/`:
- `CurriculumCatalogueTest.kt`
- `MasteryEngineTest.kt`
- `ProgrammeMigrationTest.kt`
- `RealLifeExperienceRepositoryTest.kt`
- `AdaptiveLearningTest.kt`
- `ProgrammeScreenTest.kt`
- `PracticeDraftRepositoryTest.kt`
- `LessonListeningLibraryTest.kt`

Also `app/src/test/java/com/example/DiagnosticValidityTest.kt`.

### Existing application files modified

Under `app/src/main/java/com/example/`:
- `MainActivity.kt`, `MainApplication.kt`
- `data/local/C1AppDatabase.kt`
- `data/repository/C1LearningRepository.kt`
- `data/repository/DailyStreakRepository.kt`
- `data/repository/ProficiencyProgressRepository.kt`
- `data/repository/DiagnosticRepository.kt`
- `data/repository/SpeakingPracticeRepository.kt`
- `service/AudioSpeechHelper.kt`
- `ui/C1ViewModel.kt`
- `ui/AdaptiveSessionScreen.kt`
- `ui/BaselineDiagnosticScreen.kt`
- `ui/SpeakingPracticeScreen.kt`
- `ui/diagnostic/DiagnosticEngine.kt`
- `ui/components/VisualPerformanceDashboard.kt`
- `ui/components/GrammarTopicsProgressChart.kt`
- `ui/components/C1ProficiencyProgressChart.kt`
- `ui/components/NativeListeningAudioPlayerComponent.kt`
- `ui/ListeningLabScreen.kt`
- `ui/components/BookAudioPracticeSection.kt`
- `programme/ProgrammeScreen.kt`
- `programme/ProgrammeViewModel.kt`
- `programme/AssessmentSessionRepository.kt`
- `programme/PracticeDraftRepository.kt`
- `programme/BookAudioSelectionRepository.kt`
- `programme/LessonListeningResources.kt`

Also modified `app/src/test/java/com/example/ExampleRobolectricTest.kt`, `app/build.gradle.kts`, `gradle/libs.versions.toml`, `README.md`; added this report. Build setup restored/generated the Gradle wrapper scripts/JAR plus ignored local SDK configuration/debug keystore. Java time desugaring was added for Android 7 compatibility.

## Validation

Final combined command:

```sh
./gradlew :app:assembleDebug :app:testDebugUnitTest :app:lintDebug --no-daemon
```

- **Build successful.** Debug APK: `app/build/outputs/apk/debug/app-debug.apk`.
- **223 tests across 35 classes, zero failures/errors/skips.** Also covers Room schema-14 indexes, incremental per-unit attempt indexing, adaptive SRS scheduling, course-expression seeding, long-turn transcript assembly, accessible quiz selection, local export payloads, feedback, lesson-resource mapping and all authored contexts.
- **Lint: passed with no errors; 81 warnings remain.**
- **Release shrinking:** `minifyReleaseWithR8` and `shrinkReleaseRes` passed with minification/resource shrinking enabled; R8 emitted 91 Kotlin-metadata compatibility warnings.
- No Firebase configuration or Gemini API key is required; those runtime dependencies and plugins are no longer part of the app build.
- Robolectric SDK 35 is pinned for JDK 17 compatibility; compile/target SDK remains unchanged.
- No connected device at final `adb devices` check; no AVD was available from the setup audit. Physical microphone permissions, audio quality, accessibility/device layouts and real-device persistence still need testing.

## Prioritized remaining work

1. Validate user-selected book-audio playback, URI retention and audio focus on a physical Android device. Bundled recordings would still require explicit app-use rights and verified transcripts.
2. Broaden automatic diagnostic coverage and add real listening/speaking assessment if exam-like readiness is desired. Current sessions and solo transfer prompts are practice, not protected or independently scored assessment.
3. Before any formal graduation or external C1-readiness claim, independently review and calibrate the units and rubrics with qualified German-language and domain experts, then record verifiable review evidence. Structural tests cannot substitute for this work.
4. Validate and broaden objective-linked error recycling and difficulty adaptation beyond the current narrow deterministic signals.
5. Complete production operations: authentication/sync, crash reporting, signed release, Play policy/privacy compliance, accessibility hardening and backup-restore testing.
6. Test on physical Android devices: microphone permissions, German ASR/TTS, audio focus, process death, migrations, layouts and battery/network behavior.
7. Optional: run the four-week learner pilot using `PILOT_EXECUTION_PLAN.md` to evaluate retention, transfer and usability. No causal CEFR outcome should be claimed.
