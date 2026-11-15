# Programme core API

Package: `com.example.programme.core`. Kotlin/JVM standard library and Java collection
snapshots only; no Android, Room, coroutine, TTS-engine, network, or persistence dependencies.
Dates are caller-supplied nonnegative UTC epoch-day `Long`s. No function reads a clock.

## Entry points

- `CurriculumCatalog.stages`: seven ordered stages, four context mappings per stage.
- `CurriculumCatalog.units`: 28 stable IDs (`u01`–`u28`), communication goals,
  context, prerequisite IDs and `ContentStatus`.
- `CurriculumCatalog.unit(id)`: nullable lookup.
- `ProgrammePolicy().evaluateUnit(id, today, evidence, verifications)` or
  `evaluateAll(...)`: immutable decisions with status, reasons, qualifying evidence
  IDs and weak dimensions. Constructors accept an alternative catalog for testing.
- `ProgrammePolicy().isProgrammeComplete(...)`: true only when **every** mapped
  unit has verified mastery. Intentionally false for the current partial curriculum.
- `DailyPlanner().plan(today, budget, evidence, verifications, reviews, errors)`:
  a `DailyPlan` with exact 20/60/90 minute budgets and stable resource IDs.
- `DailyPlanner().dueReviews(today, evidence, reviews, errors)`: ordered due queue.

```kotlin
val unit = requireNotNull(CurriculumCatalog.unit("u01"))
val lesson = requireNotNull(unit.content)
val decision = ProgrammePolicy().evaluateUnit(unit.id, today, evidence, verifications)
val plan = DailyPlanner().plan(
    today = today,
    budget = DailyBudget.STANDARD,
    evidence = evidence,
    verifications = verifications,
    reviews = reviewHistory,
    errors = errorHistory
)
// Render lesson.sources, collocations, grammar, comprehension and tasks.
// PlanBlock.resourceId references a LearningSource.id or PracticeTask.id.
```

## Content honesty and presentation

Only `u01`, `u02`, `u03` are `AUTHORED`; all other units are `ROADMAP` with **null**
content. A mapped goal is not a completed lesson or an unlocked achievement. Stages
are a pedagogical sequence, not seven officially certified CEFR levels. The local
rubric and mastery decisions are not a language certificate.

The first three units contain original German reading texts and listening scripts,
source-linked answer keys, grammar explanations, collocations, controlled practice,
spontaneous speaking, real-partner interaction, writing, mediation, transfer practice,
delayed review, models and model feedback. All texts are project-authored fictional
teaching scenarios, not external reporting. Listening is explicitly **synthetic TTS**,
not authentic human audio. The UI supplies its own `de-DE` TTS engine. Hide the
transcript on the first listening pass. An engine invocation is not provided here.

Hide answer keys, models and feedback until the learner submits an independent
attempt. Repeating a published prompt or viewing its model is practice, not unfamiliar
transfer. A trusted assessor must supply genuinely new prompts in another context;
never set `unfamiliarTask` just because the learner tapped a transfer button. Real
human dialogue is acceptable; scripted or AI-generated interaction evidence is not.

## Evidence and mastery contract

`ProductionEvidence` is an immutable snapshot; all exposed collection properties
(including rubric maps, catalog lists and plan results) are defensive unmodifiable
copies. The repository should retain immutable, append-only evidence IDs with the
original prompt, response artifact, assessment, assessor identity, day, mode, context,
assistance and provenance. `artifactReference` is an opaque locator, not an upload.
No data is sent anywhere by this package. Do not serialize model answers as evidence.

The deterministic policy requires:

1. Authored content and verified mastery of all prerequisites.
2. Human-assessed, observed, independent, **unassisted** production. Self-ratings,
   simulated results, automated grades, notes, translation, coaching and model-assisted
   attempts are excluded, though a repository may retain them as practice history.
3. At least four distinct task IDs on at least four days, covering speaking,
   interaction, writing and mediation, with at least two contexts including the unit's
   mapped context. A task ID must represent a different substantive prompt.
4. Every one of ten rubric dimensions at least **3/4 for every counted task**.
   Missing dimensions fail. There is no mean, weighted score or compensating strength.
   Each dimension must be observable in the task design: written responses can address
   a supplied objection; delivery means written conventions for writing. Do not invent
   observations when a task cannot elicit a dimension.
5. An unfamiliar, different-context transfer task explicitly linked via
   `delayedFromEvidenceId` to another qualifying task at least **seven days earlier**.
6. Separate `HumanVerification`, confirming independence, dated no earlier than the
   latest qualifying artifact and no later than today, listing the **exact** qualifying
   evidence IDs. Otherwise status is `AWAITING_HUMAN_VERIFICATION`, not `MASTERED`.

Latest eligible observed evidence per task supersedes older attempts, including later
weak attempts. Remaining weak latest tasks prevent mastery until repaired/reassessed;
older failures are not averaged away by strong unrelated work. A reassessment must
honestly record any assistance. A fresh weak unfamiliar task cannot be repaired by
calling a now-familiar retake unfamiliar. A new unseen transfer is then necessary.
Additional qualifying evidence invalidates an old exact-set sign-off. Evidence with
future days is ignored; duplicate evidence IDs, unknown requested units, cycles and
invalid rubric scores fail explicitly. Ordering of input evidence does not affect
results. Roadmap units never become mastered, even if artifacts are submitted.

`HumanVerification` is a trusted application-level attestation, **not a cryptographic
signature**. The repository must authenticate reviewers, enforce append-only IDs,
validate actual artifact retention and provenance, associate history with the current
learner, and prevent learner-supplied flags from masquerading as human assessments.
The core cannot independently prove that a recording is real or a task unseen.

## Daily work, reviews and error recycling

Budgets allocate reading / TTS listening / production / retrieval respectively:

| Total | Reading | Listening | Production | Review |
| --- | --- | --- | --- | --- |
| 20 | 3 | 3 | 9 | 5 |
| 60 | 9 | 9 | 27 | 15 |
| 90 | 12 | 13 | 45 | 20 |

Select the earliest accessible unmastered authored unit; all-authored-mastered falls
back to maintenance. Production modes rotate by epoch-day modulo four. A short block
may start a larger writing task rather than promise completion of the entire unit.
A roadmap-only catalog returns an empty plan instead of fabricating material.

A first eligible successful production schedules retrieval at +1 day. Eligible
`ReviewRecord`s reference actual retained evidence. Successive on-time successful
retrievals schedule +3, +7, +14, +30, then +30 days. Failure resets to +1 day. Early
reviews, duplicate evidence, repeated prompts and multiple reviews on the same day
cannot accelerate spacing. Self-rated, simulated, dependent, assisted or automated
records cannot postpone the due date. A review before seven days is practice and
cannot satisfy delayed mastery transfer. Review scheduling never unlocks mastery.

`ErrorRecord` stores observed form, correction, explanation and rubric dimension.
An error is due the day after its latest occurrence and stays in the queue until two
eligible fully passing repairs reference it through `ReviewRecord.errorIds`, on
separate later days with different task IDs. Update `lastSeenDay` when the error
recurs; earlier repairs will no longer close it. Review cards carry active error IDs
so the UI can show corrections **after** an unaided retrieval and ask for a new example.
The planner returns all due cards ordered by due day, error count and unit ID, but
budgets only the first accessible card. The remaining queue is backlog, not completed
work. The repository records actual attempts, not plan display or checkbox completion.

## Validation

New JUnit 4 tests are in `app/src/test/java/com/example/ProgrammePolicyTest.kt` and
`CurriculumContentTest.kt`. Run from the project root:

```text
gradlew.bat :app:testDebugUnitTest --tests com.example.ProgrammePolicyTest --tests com.example.CurriculumContentTest --offline --console=plain
```
