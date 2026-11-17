import React, { useEffect, useState } from 'react';
import {
  Activity,
  ArrowLeft,
  ArrowRight,
  Check,
  Filter,
  Play,
  Square,
  Volume2,
} from 'lucide-react';
import { AcousticWaveform } from '../components/AcousticWaveform';
import { LessonVisualGraphic } from '../components/LessonVisualStudio';
import {
  diagnosticTasksFull,
  diagnosticTasksGrammarVocab,
  SKILL_CATEGORY_META,
} from '../data/learningData';
import { speechService } from '../services/speechService';
import { useApp } from '../state/AppContext';

export const BaselineDiagnosticScreen: React.FC = () => {
  const {
    diagnosticUiState,
    startDiagnosticAssessment,
    recordDiagnosticAnswer,
    nextDiagnosticTask,
    previousDiagnosticTask,
    resetDiagnosticAssessment,
    navigateTo,
  } = useApp();

  const [speakingId, setSpeakingId] = useState<string | null>(
    speechService.getSpeakingId(),
  );

  useEffect(() => {
    return speechService.subscribe(() => setSpeakingId(speechService.getSpeakingId()));
  }, []);

  if (diagnosticUiState.status === 'IDLE') {
    return (
      <div className="space-y-5">
        <div>
          <p className="text-[11px] font-bold uppercase tracking-wider text-[var(--accent-ink)]">
            Unbewertetes Übungsscreening
          </p>
          <h1 className="font-display text-3xl font-extrabold text-[var(--ink)]">
            MCQ-Stichprobe & Profilcheck
          </h1>
          <p className="mt-1 text-xs leading-relaxed text-[var(--ink2)]">
            Diese kurzen Multiple-Choice-Aufgaben prüfen punktuell Auswahlwissen zu
            Grammatik, Wortschatz und Leseverstehen (bzw. TTS-Hören). Sie ersetzen
            weder eine GER-/C1-Einstufung noch eine menschliche Leistungsprüfung und
            verändern deinen Programmfortschritt nicht.
          </p>
        </div>

        <div className="grid gap-4 sm:grid-cols-2">
          <div className="flex flex-col justify-between gap-4 rounded-3xl border border-[var(--line)] bg-[var(--surface)] p-5 shadow-sm">
            <div className="space-y-3">
              <p className="text-[11px] font-bold uppercase tracking-wider text-[var(--accent-ink)]">
                {diagnosticTasksGrammarVocab.length} Aufgaben · Fokus Präzision
              </p>
              <LessonVisualGraphic graphic="TARGET" activeStep={1} />
              <div>
                <h2 className="text-lg font-extrabold text-[var(--ink)]">
                  Grammatik & Kollokationen
                </h2>
                <p className="mt-1.5 text-xs leading-relaxed text-[var(--ink2)]">
                  Prüft gezielt Nebensatz-Inversion, Nomen-Verb-Verbindungen, Konjunktiv I,
                  Nominalstil und Partizipialattribute per Mehrfachauswahl.
                </p>
              </div>
            </div>
            <button
              type="button"
              onClick={() => startDiagnosticAssessment('GRAMMAR_AND_VOCABULARY')}
              className="inline-flex items-center justify-center gap-2 rounded-2xl bg-[var(--accent)] px-4 py-3 text-xs font-extrabold text-[var(--on-accent)]"
            >
              <Play className="h-3.5 w-3.5 fill-current" />
              Grammatik/Lexik-Check starten
            </button>
          </div>

          <div className="flex flex-col justify-between gap-4 rounded-3xl border border-[var(--line)] bg-[var(--surface)] p-5 shadow-sm">
            <div className="space-y-3">
              <p className="text-[11px] font-bold uppercase tracking-wider text-[var(--violet)]">
                {diagnosticTasksFull.length} Aufgaben · Gemischtes Screening
              </p>
              <LessonVisualGraphic graphic="GRID" activeStep={2} />
              <div>
                <h2 className="text-lg font-extrabold text-[var(--ink)]">
                  Gemischte Stichprobe
                </h2>
                <p className="mt-1.5 text-xs leading-relaxed text-[var(--ink2)]">
                  Kombiniert Hör-, Lese-, Grammatik- und Wortschatz-Items (produktive
                  Fertigkeiten werden nicht per MCQ bewertet).
                </p>
              </div>
            </div>
            <button
              type="button"
              onClick={() => startDiagnosticAssessment('FULL_COMPETENCY')}
              className="inline-flex items-center justify-center gap-2 rounded-2xl border border-[var(--accent)] bg-[var(--accent-soft)] px-4 py-3 text-xs font-extrabold text-[var(--accent-ink)]"
            >
              <Activity className="h-3.5 w-3.5" />
              Gemischten Check starten
            </button>
          </div>
        </div>
      </div>
    );
  }

  if (diagnosticUiState.status === 'IN_PROGRESS') {
    const task = diagnosticUiState.tasks[diagnosticUiState.currentTaskIndex];
    if (!task) return null;
    const answer = diagnosticUiState.answers[diagnosticUiState.currentTaskIndex];
    const progressPct = Math.round(
      ((diagnosticUiState.currentTaskIndex + 1) / diagnosticUiState.tasks.length) * 100,
    );
    const ttsId = `diag-task-${task.id}`;
    const isPlaying = speakingId === ttsId;

    return (
      <div className="space-y-5">
        <div className="flex flex-wrap items-center justify-between gap-2">
          <div>
            <span className="text-[11px] font-bold uppercase tracking-wider text-[var(--accent-ink)]">
              Aufgabe {diagnosticUiState.currentTaskIndex + 1} /{' '}
              {diagnosticUiState.tasks.length} ·{' '}
              {SKILL_CATEGORY_META[task.skillCategory].titleDe}
            </span>
            <h1 className="font-display text-2xl font-extrabold text-[var(--ink)]">
              {task.headlineDe}
            </h1>
          </div>
          <span className="rounded-xl border border-[var(--line)] bg-[var(--surface2)] px-3 py-1 text-xs font-bold tabular-nums text-[var(--ink)]">
            {progressPct}%
          </span>
        </div>

        <div className="h-2 overflow-hidden rounded-full bg-[var(--surface2)]">
          <div
            className="h-full rounded-full bg-[var(--accent)] transition-all"
            style={{ width: `${progressPct}%` }}
          />
        </div>

        <section className="space-y-4 rounded-3xl border border-[var(--line)] bg-[var(--surface)] p-5 shadow-sm">
          <div className="flex flex-wrap items-center justify-between gap-2">
            <span className="rounded-lg bg-[var(--surface2)] px-2.5 py-1 text-[10px] font-bold uppercase text-[var(--ink2)]">
              {task.subTopicName} · {task.contextDe}
            </span>
            {task.spokenAudioText && (
              <button
                type="button"
                onClick={() =>
                  isPlaying
                    ? speechService.stopSpeaking()
                    : speechService.speak(task.spokenAudioText!, {
                        rate: task.speechSpeed,
                        id: ttsId,
                      })
                }
                className="inline-flex items-center gap-1.5 rounded-xl bg-[var(--accent)] px-3 py-1.5 text-xs font-bold text-[var(--on-accent)]"
              >
                {isPlaying ? (
                  <Square className="h-3.5 w-3.5 fill-current" />
                ) : (
                  <Volume2 className="h-3.5 w-3.5" />
                )}
                {isPlaying ? 'Stoppen' : 'Hörtext abspielen (TTS)'}
              </button>
            )}
          </div>

          {task.spokenAudioText && (
            <AcousticWaveform
              active={isPlaying}
              variant="sky"
              bars={20}
              height={28}
              label={isPlaying ? 'Audio spielt' : 'Hörimpuls bereit'}
            />
          )}

          <p className="text-sm font-semibold leading-relaxed text-[var(--ink)]">
            {task.promptText}
          </p>

          <div className="space-y-2">
            {task.multipleChoiceOptions.map((opt, idx) => {
              const selected = answer?.selectedOptionIndex === idx;
              const letter = String.fromCharCode(65 + idx);
              return (
                <button
                  key={idx}
                  type="button"
                  onClick={() => recordDiagnosticAnswer(idx)}
                  className={`flex w-full items-center gap-3 rounded-2xl border p-3.5 text-left text-xs transition ${
                    selected
                      ? 'border-[var(--accent)] bg-[var(--accent-soft)] font-bold text-[var(--ink)]'
                      : 'border-[var(--line)] bg-[var(--surface2)] text-[var(--ink)] hover:border-[var(--accent)]'
                  }`}
                >
                  <span
                    className={`flex h-6 w-6 shrink-0 items-center justify-center rounded-lg border text-[11px] font-black ${
                      selected
                        ? 'border-[var(--accent)] bg-[var(--accent)] text-[var(--on-accent)]'
                        : 'border-[var(--line)] bg-[var(--surface)] text-[var(--ink2)]'
                    }`}
                  >
                    {letter}
                  </span>
                  <span>{opt}</span>
                </button>
              );
            })}
          </div>

          <div className="flex items-center justify-between pt-2">
            <button
              type="button"
              disabled={diagnosticUiState.currentTaskIndex === 0}
              onClick={previousDiagnosticTask}
              className="inline-flex items-center gap-1.5 rounded-xl border border-[var(--line)] bg-[var(--surface2)] px-4 py-2 text-xs font-bold text-[var(--ink)] disabled:opacity-40"
            >
              <ArrowLeft className="h-3.5 w-3.5" />
              Zurück
            </button>

            <button
              type="button"
              onClick={nextDiagnosticTask}
              className="inline-flex items-center gap-1.5 rounded-xl bg-[var(--accent)] px-4 py-2 text-xs font-extrabold text-[var(--on-accent)]"
            >
              {diagnosticUiState.currentTaskIndex < diagnosticUiState.tasks.length - 1
                ? 'Weiter'
                : 'Auswertung anzeigen'}
              <ArrowRight className="h-3.5 w-3.5" />
            </button>
          </div>
        </section>
      </div>
    );
  }

  const res = diagnosticUiState.evaluationResult;
  if (!res) return null;

  return (
    <div className="space-y-5">
      <div className="flex flex-wrap items-center justify-between gap-2">
        <div>
          <p className="text-[11px] font-bold uppercase tracking-wider text-[var(--accent-ink)]">
            Vorläufiges MCQ-Ergebnis
          </p>
          <h1 className="font-display text-3xl font-extrabold text-[var(--ink)]">
            Auswertung der Stichprobe
          </h1>
        </div>
        <button
          type="button"
          onClick={resetDiagnosticAssessment}
          className="rounded-xl border border-[var(--line)] bg-[var(--surface2)] px-3 py-2 text-xs font-bold text-[var(--ink)]"
        >
          Neuen Check wählen
        </button>
      </div>

      {/* Grammar & Vocabulary Sample Cards */}
      <div className="grid gap-4 sm:grid-cols-2">
        <div className="rounded-3xl border border-[var(--line)] bg-[var(--surface)] p-5">
          <p className="text-xs font-bold text-[var(--ink2)]">Grammatik (MCQ-Stichprobe)</p>
          <p className="mt-1 font-display text-3xl font-extrabold tabular-nums text-[var(--accent-ink)]">
            {res.grammarScore} <span className="text-sm font-normal text-[var(--ink2)]">/ 100</span>
          </p>
          <div className="mt-2 h-1.5 overflow-hidden rounded-full bg-[var(--surface2)]">
            <div
              className="h-full rounded-full bg-[var(--accent)]"
              style={{ width: `${res.grammarScore}%` }}
            />
          </div>
          <p className="mt-2 text-[11px] font-bold text-[var(--accent-ink)]">
            {res.assessedSkills.includes('GRAMMATIK')
              ? 'Nur Auswahlantworten, kein GER-Niveau'
              : 'Nicht beurteilt (intern 0)'}
          </p>
        </div>

        <div className="rounded-3xl border border-[var(--line)] bg-[var(--surface)] p-5">
          <p className="text-xs font-bold text-[var(--ink2)]">Wortschatz (MCQ-Stichprobe)</p>
          <p className="mt-1 font-display text-3xl font-extrabold tabular-nums text-[var(--violet)]">
            {res.vocabularyScore} <span className="text-sm font-normal text-[var(--ink2)]">/ 100</span>
          </p>
          <div className="mt-2 h-1.5 overflow-hidden rounded-full bg-[var(--surface2)]">
            <div
              className="h-full rounded-full bg-[var(--violet)]"
              style={{ width: `${res.vocabularyScore}%` }}
            />
          </div>
          <p className="mt-2 text-[11px] font-bold text-[var(--violet)]">
            {res.assessedSkills.includes('WORTSCHATZ')
              ? 'Nur Auswahlantworten, kein GER-Niveau'
              : 'Nicht beurteilt (intern 0)'}
          </p>
        </div>
      </div>

      {/* Honest No-Bottleneck Callout */}
      <div className="flex items-start gap-3 rounded-2xl border border-[var(--coral)]/35 bg-[var(--coral-soft)] p-4">
        <Filter className="mt-0.5 h-5 w-5 shrink-0 text-[var(--coral)]" />
        <div>
          <p className="font-display text-xs font-extrabold uppercase tracking-wider text-[var(--coral)]">
            KEINE ENGPASSDIAGNOSE
          </p>
          <p className="mt-0.5 text-sm font-bold text-[var(--ink)]">
            {res.primaryLimitingFactor}
          </p>
          <p className="mt-1.5 text-xs leading-relaxed text-[var(--ink2)]">
            {res.summaryDe}
          </p>
        </div>
      </div>

      {/* 9-Dimension Honesty Breakdown */}
      <section className="space-y-2.5 rounded-3xl border border-[var(--line)] bg-[var(--surface)] p-5">
        <h2 className="text-sm font-extrabold text-[var(--ink)]">
          Bereiche dieser MCQ-Stichprobe
        </h2>
        <div className="space-y-2">
          {Object.entries(res.nineDimensionalProfile).map(([cat, scoreObj]) => {
            const skillCat = cat as keyof typeof SKILL_CATEGORY_META;
            const assessed = res.assessedSkills.includes(skillCat);
            if (!assessed) {
              return (
                <p key={cat} className="text-xs text-[var(--ink2)]">
                  {SKILL_CATEGORY_META[skillCat].titleDe}: nicht beurteilt (0 = keine Evidenz)
                </p>
              );
            }
            return (
              <div key={cat} className="flex items-center justify-between gap-3 text-xs">
                <span className="font-semibold text-[var(--ink)]">
                  {SKILL_CATEGORY_META[skillCat].titleDe} (nur MCQ)
                </span>
                <div className="flex items-center gap-2">
                  <div className="h-1.5 w-24 overflow-hidden rounded-full bg-[var(--surface2)]">
                    <div
                      className="h-full rounded-full bg-[var(--accent)]"
                      style={{ width: `${scoreObj.score}%` }}
                    />
                  </div>
                  <span className="w-9 text-right font-bold tabular-nums text-[var(--ink)]">
                    {scoreObj.score}%
                  </span>
                </div>
              </div>
            );
          })}
        </div>
      </section>

      {/* Topic Breakdown */}
      {(Object.keys(res.grammarTopicScores).length > 0 ||
        Object.keys(res.vocabTopicScores).length > 0) && (
        <section className="space-y-2.5 rounded-3xl border border-[var(--line)] bg-[var(--surface)] p-5">
          <h2 className="text-sm font-extrabold text-[var(--ink)]">
            Beantwortete MCQ-Stichproben nach Thema
          </h2>
          <div className="space-y-2">
            {Object.entries({
              ...res.grammarTopicScores,
              ...res.vocabTopicScores,
            }).map(([topic, score]) => (
              <div key={topic} className="flex items-center justify-between gap-3 text-xs">
                <span className="text-[var(--ink)]">{topic}</span>
                <span
                  className={`font-bold tabular-nums ${
                    score >= 75 ? 'text-[var(--ok)]' : 'text-[var(--coral)]'
                  }`}
                >
                  {score}%
                </span>
              </div>
            ))}
          </div>
        </section>
      )}

      <button
        type="button"
        onClick={() => navigateTo('Programme')}
        className="flex w-full items-center justify-center gap-2 rounded-2xl bg-[var(--accent)] px-5 py-3.5 text-sm font-extrabold text-[var(--on-accent)]"
      >
        <Check className="h-4 w-4" />
        Zum evidenzbasierten Lernprogramm ›
      </button>
    </div>
  );
};
