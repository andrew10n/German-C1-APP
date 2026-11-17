# C1 Deutsch

Offline-first German C1 learning studio built with React, TypeScript, Vite, and Tailwind CSS.

## Features

- **28-Unit C1 Curriculum Programme (Stages 0–6)**: Context tracks (`GENERAL`, `SOCIAL`, `ENGINEERING`, `COACHING`), active recall expressions, syntax/register drills, comprehension checks, auto-saved practice drafts, and strict mastery tracking (`MasteryEngine`).
- **Evidence & Human Review Contracts**: Separates unvalidated self-reflection (`SELF_REFLECTION`), automatic checks (`AUTOMATIC_QUIZ`), and rubric-scored evaluation (`HUMAN_REVIEW`), with feedback claims, challenge workflows (`FeedbackEvidenceEngine`), and real-life repair logs (`RealLifeRepairEngine`).
- **8 Graduation Assessments & Timed Sessions**: Multi-part C1 benchmarks plus independent timed practice runs (`DELAYED_CHECKPOINT`, `GOETHE_STYLE`, `WEEKLY_INTEGRATED`, `PILOT_WEEK`, `LISTENING_PRACTICE`, `SPEAKING_PRACTICE`).
- **Training Labs**:
  - **Hör-Labor**: Local coursebook audio file player (`0.75×`/`1×`/`1.25×` speed, A–B segment loop), external authentic sources, and 9 synthetic German TTS scenarios with blind comprehension quizzes and reduction notes.
  - **Aussprache-Labor**: Web Speech API (`de-DE`) or manual transcript alignment with IPA focus hints.
  - **Sprech-Simulator**: 24 dialogue variations with latency timer, sudden interruptions, and C1 connector suggestions.
  - **Fehler-Intelligenz & Wortschatz-SRS**: 14 transfer drills and SM-2 spaced repetition flashcard deck synchronized with course expressions.
  - **Sprachmittlung**: 6 mediation tasks with pitfall warnings and reference formulations.
  - **Fußball-Trainer (U15)**: Youth football tactical glossary, scenarios, and live pitch commands.
  - **MCQ-Übungsscreening**: Unvalidated grammar/vocabulary (10-task) and mixed (8-task) multiple-choice sample checks.
- **Local Privacy**: All learner data persists locally in `localStorage` and can be exported as JSON (`c1-deutsch-local-export-v2`) or wiped cleanly.

## Development

```bash
npm install
npm run dev      # Starts Vite on http://0.0.0.0:3000
npm run build    # Type-checks and builds production bundle into dist/
npm run lint     # Runs ESLint across TypeScript/TSX sources
```
