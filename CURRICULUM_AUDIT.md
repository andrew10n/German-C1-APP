# Curriculum and adaptivity audit

## Scope and conclusion

The active programme contains 28 authored lesson payloads. Two are optional stage-0 orientation lessons; the sequenced core contains 26 lessons across stages 1–6. Every core lesson has a reading text, a synthetic listening transcript, expressions, grammar guidance, a comprehension check, controlled practice, speaking, interaction, writing, transfer, and feedback.

The course is a substantial personal practice curriculum with clear evidence boundaries. It is not a validated C1 placement test and it cannot certify C1 attainment. Objective quiz answers can steer the practice route, while a narrow set of deterministic text patterns can request revision; neither evaluates open German reliably. The app does not require a teacher or external reviewer to move through its practice checkpoints.

## Course map

| Stage | Units | Main communicative work |
| --- | ---: | --- |
| 0 · orientation | 2 optional | goals and evidence literacy |
| 1 · B2 consolidation | 6 | explaining, justifying, uncertainty, feedback, clarification, retrieval habits |
| 2 · advanced comprehension | 3 | evidence, assumptions, nuance |
| 3 · advanced production | 3 | argument, presentation, revision |
| 4 · interaction pragmatics | 6 | register, negotiation, disagreement, de-escalation, agreements, spontaneous response |
| 5 · integration and mediation | 4 | summarising, mediation, ethics, facilitation |
| 6 · independent transfer practice | 4 | general and specialist transfer, delayed retrieval, personal practice planning |

## Adaptive contract

There are three separate mechanisms, each with a deliberately limited job.

1. `LearnerDiagnosticEngine` treats objectively scored quiz answers as evidence only for the item answered. Known text-pattern flags can request repair; no detected error is not proof of correct language. Typed work does not establish listening, pronunciation, spontaneous speech, or live interaction.
2. `AdaptiveControlEngine` uses quiz outcomes, those narrow pattern flags, and learner-reported real-life difficulties to reorder existing practice. It does not invent a proficiency score.
3. `AdaptiveDiagnosticRoute` moves through the 26 core units using a correct objective quiz and two distinct unassisted text-production tasks without a currently recognized error. These are personal practice checkpoints, not C1 mastery. All units remain open for free practice. `MasteryEngine` retains stricter formal-evidence rules, which are separate from and not required by the private practice route. The two optional stage-0 lessons remain preparation, not gates.

This preserves useful adaptation without treating a few multiple-choice answers as evidence of productive competence.

## Content review findings and corrections

- Rechecked all course fields for presence, uniqueness, prompt completeness, production coverage, unsupported certification claims, and clear fictional framing.
- Corrected grammar and register overstatements, including spoken *weil*, the formal use of *aufgrund*, indirect speech, and the relation between *um … zu* and *indem*.
- Corrected overclaims about causation, FMEA action priority, automotive safety evidence, youth-sports data handling, and synthetic dialect examples.
- Labeled regional forms as fictional, simplified practice approximations. They teach clarification behaviour, not a uniform regional grammar.
- Kept automotive, legal and sports examples fictional and separated a test result, a hypothesis, an FMEA result, and a release decision.

See [CONTENT_REVIEW.md](CONTENT_REVIEW.md) for individual copy corrections and source notes.

## Alignment limits

The CEFR uses communicative action scales, including mediation and interaction. The course covers those areas, but no internal rubric has been validated against CEFR cut scores. The current Goethe-Zertifikat C1 has listening, reading, writing and speaking modules; the speaking module involves paired interaction. This course supports related practice but does not reproduce official conditions or scoring.

The listening transcripts are delivered by TTS. They are useful for controlled comprehension practice, but they do not provide the range of voices, accents, sound quality or speech styles needed for an authentic listening claim. No synthetic dialect spelling can establish regional listening competence.

## Regression safeguards

`CurriculumStructureAuditTest` verifies the 2/26 split, stage distribution, complete learning cycle in each core unit, and the separation between adaptive practice checkpoints and formal mastery. Existing catalogue and quality tests also check uniqueness, prerequisites, input length, response options, unsupported certification wording, and that personal transfer tasks do not require an external human reviewer.

## Remaining evidence needed for a stronger public claim

- Native-speaker and specialist review of the regional and technical scenarios.
- Learner pilot data: completion, comprehension, production samples, and retention after delayed retrieval.
- Calibrated human ratings against a published C1 rubric for writing and paired speaking.
- Licensed multi-speaker audio and a comparison of TTS-only versus authentic listening outcomes.
