import React, { useEffect, useState } from 'react';
import {
  ArrowRight,
  CheckCircle2,
  Play,
  Square,
  Trophy,
  Volume2,
  XCircle,
} from 'lucide-react';
import { AcousticWaveform } from '../components/AcousticWaveform';
import { AiFeedbackShareButton } from '../components/AiFeedbackShareButton';
import { ImprintSentenceSurgeryCard } from '../components/ImprintInteractiveDiagrams';
import { speechService } from '../services/speechService';
import { useApp } from '../state/AppContext';
import type { SessionPhase } from '../types';

const PHASES: { key: SessionPhase; label: string; step: number }[] = [
  { key: 'WARM_UP', label: '1. Warm-Up', step: 1 },
  { key: 'MAIN_CHALLENGE', label: '2. Szenario', step: 2 },
  { key: 'ACTIVE_PRODUCTION', label: '3. Produktion', step: 3 },
  { key: 'PRECISION_CORRECTION', label: '4. Korrektur', step: 4 },
  { key: 'TRANSFER_TEST', label: '5. Transfer', step: 5 },
];

export const AdaptiveSessionScreen: React.FC = () => {
  const {
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
    updateProductionInput,
    updateTransferInput,
    navigateTo,
  } = useApp();

  const [speakingId, setSpeakingId] = useState<string | null>(speechService.getSpeakingId());

  useEffect(() => {
    return speechService.subscribe(() => setSpeakingId(speechService.getSpeakingId()));
  }, []);

  if (!activeSession) {
    return (
      <div className="rounded-3xl border border-[var(--line)] bg-[var(--surface)] p-6 text-center">
        <h2 className="font-display text-2xl font-extrabold text-[var(--ink)]">
          Keine aktive Session geladen
        </h2>
        <p className="mt-2 text-xs text-[var(--ink2)]">
          Starte eine rotierende 5-Phasen-Übungsstation (unbewertete Praxis).
        </p>
        <button
          type="button"
          onClick={startDailySession}
          className="mt-4 inline-flex items-center gap-2 rounded-2xl bg-[var(--accent)] px-5 py-3 text-sm font-bold text-[var(--on-accent)]"
        >
          <Play className="h-4 w-4 fill-current" />
          Tagesstation starten
        </button>
      </div>
    );
  }

  if (sessionCompleted) {
    return (
      <div className="space-y-4 rounded-3xl border border-[var(--accent)]/40 bg-[var(--surface)] p-6 text-center shadow-lg">
        <div className="mx-auto flex h-14 w-14 items-center justify-center rounded-2xl bg-[var(--accent-soft)] text-[var(--accent-ink)]">
          <Trophy className="h-7 w-7" />
        </div>
        <h2 className="font-display text-3xl font-extrabold text-[var(--ink)]">
          Station abgeschlossen
        </h2>
        <p className="mx-auto max-w-md text-xs leading-relaxed text-[var(--ink2)]">
          Dein heutiger Trainingsimpuls ({activeSession.title}) wurde in deiner
          Lern-Streak verbucht. Hinweis: Diese 5-Phasen-Übung vergibt keine
          GER-/C1-Kompetenzbewertung.
        </p>

        <div className="flex flex-wrap justify-center gap-3 pt-2">
          <button
            type="button"
            onClick={() => navigateTo('VocabTraining')}
            className="rounded-2xl border border-[var(--line)] bg-[var(--surface2)] px-4 py-2.5 text-xs font-bold text-[var(--ink)]"
          >
            Zum Wortschatz-Training
          </button>
          <button
            type="button"
            onClick={() => navigateTo('Programme')}
            className="rounded-2xl bg-[var(--accent)] px-4 py-2.5 text-xs font-bold text-[var(--on-accent)]"
          >
            Zurück zu Lektionen & Roadmap
          </button>
        </div>
      </div>
    );
  }

  const currentIdx = PHASES.findIndex((p) => p.key === currentPhase);

  return (
    <div className="space-y-5">
      <div>
        <div className="flex flex-wrap items-center justify-between gap-2">
          <span className="rounded-lg bg-[var(--accent-soft)] px-2.5 py-1 text-[10px] font-bold uppercase tracking-wider text-[var(--accent-ink)]">
            {activeSession.environment}
          </span>
          <span className="text-xs font-bold text-[var(--ink2)]">
            Phase {currentIdx + 1} / {PHASES.length} · ~{activeSession.totalMinutes} Min.
          </span>
        </div>
        <h1 className="mt-2 font-display text-2xl font-extrabold text-[var(--ink)]">
          {activeSession.title}
        </h1>
      </div>

      {/* Animated 5-Phase Journey Stepper */}
      <div className="relative rounded-2xl border border-[var(--line)] bg-[var(--surface)] p-3.5">
        <div className="relative grid grid-cols-5 gap-2">
          <div className="pointer-events-none absolute right-6 left-6 top-4 h-1 -translate-y-1/2 rounded-full bg-[var(--surface2)]" />
          <div
            className="pointer-events-none absolute left-6 top-4 h-1 -translate-y-1/2 rounded-full bg-[var(--accent)] transition-all duration-500"
            style={{ width: `calc(${(currentIdx / (PHASES.length - 1)) * 100}% - 3rem)` }}
          />
          {PHASES.map((p, i) => {
            const active = i === currentIdx;
            const done = i < currentIdx;
            return (
              <div key={p.key} className="relative z-10 flex flex-col items-center text-center">
                <span
                  className={`flex h-8 w-8 items-center justify-center rounded-full border-2 text-xs font-black transition-transform duration-300 ${
                    active
                      ? 'scale-110 border-[var(--accent)] bg-[var(--accent)] text-[var(--on-accent)] shadow-md'
                      : done
                        ? 'border-[var(--ok)] bg-[var(--ok)] text-white'
                        : 'border-[var(--line)] bg-[var(--surface2)] text-[var(--ink2)]'
                  }`}
                >
                  {done ? '✓' : p.step}
                </span>
                <span
                  className={`mt-1.5 text-[10px] font-bold leading-tight ${
                    active
                      ? 'text-[var(--accent-ink)]'
                      : done
                        ? 'text-[var(--ok)]'
                        : 'text-[var(--ink2)]'
                  }`}
                >
                  {p.label.replace(/^\d+\.\s*/, '')}
                </span>
              </div>
            );
          })}
        </div>
      </div>

      {/* Phase 1: WARM_UP */}
      {currentPhase === 'WARM_UP' && (
        <section className="c1-animate-enter space-y-4 rounded-3xl border border-[var(--line)] bg-[var(--surface)] p-5">
          <div className="flex flex-wrap items-center justify-between gap-2">
            <div>
              <p className="text-[11px] font-bold uppercase tracking-wider text-[var(--accent-ink)]">
                Phase 1 · Akustischer Einstieg (TTS)
              </p>
              <p className="text-xs text-[var(--ink2)]">
                {activeSession.warmUpPrompt.accentInfo}
              </p>
            </div>
            <button
              type="button"
              onClick={() =>
                speakingId === 'daily-warmup'
                  ? speechService.stopSpeaking()
                  : speechService.speak(activeSession.warmUpPrompt.audioSpeakerText, {
                      rate: 1.1,
                      id: 'daily-warmup',
                    })
              }
              className="inline-flex items-center gap-1.5 rounded-xl bg-[var(--accent)] px-3.5 py-2 text-xs font-bold text-[var(--on-accent)]"
            >
              {speakingId === 'daily-warmup' ? (
                <Square className="h-3.5 w-3.5 fill-current" />
              ) : (
                <Volume2 className="h-3.5 w-3.5" />
              )}
              {speakingId === 'daily-warmup' ? 'Stoppen' : 'Vorlesen (TTS)'}
            </button>
          </div>

          <AcousticWaveform
            active={speakingId === 'daily-warmup'}
            variant="accent"
            label={speakingId === 'daily-warmup' ? 'Spricht…' : 'Hörimpuls'}
          />

          <blockquote className="rounded-2xl border border-[var(--line)] bg-[var(--surface2)] p-4 text-sm font-medium italic text-[var(--ink)]">
            „{activeSession.warmUpPrompt.audioSpeakerText}“
          </blockquote>

          <p className="text-sm font-bold text-[var(--ink)]">
            {activeSession.warmUpPrompt.questionPrompt}
          </p>

          <div className="space-y-2">
            {activeSession.warmUpPrompt.options.map((opt, idx) => {
              const selected = warmUpSelectedOption === idx;
              const isCorrect = idx === activeSession.warmUpPrompt.correctIndex;
              return (
                <button
                  key={idx}
                  type="button"
                  onClick={() => selectWarmUpOption(idx)}
                  className={`flex w-full items-center gap-2.5 rounded-2xl border p-3.5 text-left text-xs transition ${
                    warmUpAnswerRevealed && isCorrect
                      ? 'border-[var(--ok)] bg-[var(--ok-bg)] text-[var(--ink)]'
                      : warmUpAnswerRevealed && selected && !isCorrect
                        ? 'border-[var(--coral)] bg-[var(--coral-soft)] text-[var(--ink)]'
                        : selected
                          ? 'border-[var(--accent)] bg-[var(--accent-soft)] text-[var(--ink)]'
                          : 'border-[var(--line)] bg-[var(--surface2)] text-[var(--ink)] hover:border-[var(--accent)]'
                  }`}
                >
                  {warmUpAnswerRevealed && isCorrect ? (
                    <CheckCircle2 className="h-4 w-4 shrink-0 text-[var(--ok)]" />
                  ) : warmUpAnswerRevealed && selected && !isCorrect ? (
                    <XCircle className="h-4 w-4 shrink-0 text-[var(--coral)]" />
                  ) : (
                    <span
                      className={`h-3.5 w-3.5 shrink-0 rounded-full border ${
                        selected
                          ? 'border-[var(--accent)] bg-[var(--accent)]'
                          : 'border-[var(--ink2)]'
                      }`}
                    />
                  )}
                  <span>{opt}</span>
                </button>
              );
            })}
          </div>

          {!warmUpAnswerRevealed ? (
            <button
              type="button"
              disabled={warmUpSelectedOption === null}
              onClick={revealWarmUpAnswer}
              className="rounded-2xl border border-[var(--accent)] bg-[var(--accent-soft)] px-4 py-2 text-xs font-bold text-[var(--accent-ink)] disabled:opacity-45"
            >
              Auflösung & Erklärung anzeigen
            </button>
          ) : (
            <div className="rounded-2xl border border-[var(--ok)]/30 bg-[var(--ok-bg)] p-3.5 text-xs leading-relaxed text-[var(--ink)]">
              {activeSession.warmUpPrompt.diagnosticExplanation}
            </div>
          )}
        </section>
      )}

      {/* Phase 2: MAIN_CHALLENGE */}
      {currentPhase === 'MAIN_CHALLENGE' && (
        <section className="c1-animate-enter space-y-4 rounded-3xl border border-[var(--line)] bg-[var(--surface)] p-5">
          <p className="text-[11px] font-bold uppercase tracking-wider text-[var(--violet)]">
            Phase 2 · Kern-Herausforderung
          </p>
          <h2 className="text-lg font-extrabold text-[var(--ink)]">
            {activeSession.mainChallenge.title}
          </h2>
          <p className="text-xs leading-relaxed text-[var(--ink2)]">
            {activeSession.mainChallenge.scenarioContext}
          </p>

          <div className="rounded-2xl border border-[var(--line)] bg-[var(--surface2)] p-4">
            <p className="text-[11px] font-bold uppercase tracking-wider text-[var(--ink2)]">
              Aussage der Gesprächsperson
            </p>
            <p className="mt-1 text-sm font-medium italic text-[var(--ink)]">
              „{activeSession.mainChallenge.partnerStatement}“
            </p>
          </div>

          {/* Visual 3-Tier Escalation Flow Ladder */}
          <div className="relative space-y-2.5 pl-4">
            <div className="pointer-events-none absolute top-4 bottom-4 left-2 w-0.5 bg-gradient-to-b from-[var(--accent)] via-[var(--violet)] to-[var(--coral)]" />
            {[
              {
                label: 'Stufe 1 · Einordnung',
                text: activeSession.mainChallenge.stage1Prompt,
                dot: 'bg-[var(--accent)]',
              },
              {
                label: 'Stufe 2 · Vertiefung',
                text: activeSession.mainChallenge.stage2Deepening,
                dot: 'bg-[var(--violet)]',
              },
              {
                label: 'Stufe 3 · Druck & Subtext',
                text: activeSession.mainChallenge.stage3StressInference,
                dot: 'bg-[var(--coral)]',
              },
            ].map((stage, idx) => (
              <div
                key={idx}
                className="relative rounded-2xl border border-[var(--line)] bg-[var(--surface2)] p-3.5 text-xs leading-relaxed text-[var(--ink)]"
              >
                <span
                  className={`absolute top-4 -left-[13px] h-2.5 w-2.5 rounded-full ring-2 ring-[var(--surface)] ${stage.dot}`}
                />
                <p className="mb-1 text-[10px] font-extrabold uppercase tracking-wider text-[var(--ink2)]">
                  {stage.label}
                </p>
                {stage.text}
              </div>
            ))}
          </div>

          <div className="rounded-2xl border border-[var(--accent)]/30 bg-[var(--accent-soft)] p-3.5">
            <p className="text-xs font-bold text-[var(--accent-ink)]">Erwartete Merkmale:</p>
            <ul className="mt-1.5 list-disc space-y-1 pl-4 text-xs text-[var(--ink)]">
              {activeSession.mainChallenge.keyPointsExpected.map((kp, i) => (
                <li key={i}>{kp}</li>
              ))}
            </ul>
          </div>
        </section>
      )}

      {/* Phase 3: ACTIVE_PRODUCTION */}
      {currentPhase === 'ACTIVE_PRODUCTION' && (
        <section className="c1-animate-enter space-y-4 rounded-3xl border border-[var(--line)] bg-[var(--surface)] p-5">
          <div className="flex flex-wrap items-center justify-between gap-2">
            <p className="text-[11px] font-bold uppercase tracking-wider text-[var(--accent-ink)]">
              Phase 3 · Aktive Eigenproduktion (~{activeSession.productionTask.timeLimitSeconds}s)
            </p>
            <button
              type="button"
              onClick={() =>
                speakingId === 'daily-prod'
                  ? speechService.stopSpeaking()
                  : speechService.speak(activeSession.productionTask.simulatedAudioPrompt, {
                      id: 'daily-prod',
                    })
              }
              className="inline-flex items-center gap-1.5 rounded-xl border border-[var(--line)] bg-[var(--surface2)] px-3 py-1.5 text-xs font-bold text-[var(--ink)]"
            >
              <Volume2 className="h-3.5 w-3.5" />
              {speakingId === 'daily-prod' ? 'Stoppen' : 'Nachfrage anhören'}
            </button>
          </div>

          <AcousticWaveform
            active={speakingId === 'daily-prod'}
            variant="violet"
            height={28}
            label={speakingId === 'daily-prod' ? 'Nachfrage läuft' : 'Audio bereit'}
          />

          <p className="text-sm font-semibold text-[var(--ink)]">
            {activeSession.productionTask.situationBrief}
          </p>
          <div className="rounded-2xl border border-[var(--line)] bg-[var(--surface2)] p-3.5 text-xs italic text-[var(--ink)]">
            Nachfrage: „{activeSession.productionTask.simulatedAudioPrompt}“
          </div>

          <div>
            <label className="block text-xs font-bold text-[var(--ink2)]">
              Deine selbstständig formulierte Antwort
            </label>
            <textarea
              rows={5}
              value={productionUserInput}
              onChange={(e) => updateProductionInput(e.target.value)}
              placeholder="Formuliere deine komplette Reaktion auf C1-Niveau..."
              className="mt-1.5 w-full rounded-2xl border border-[var(--line)] bg-[var(--bg)] p-3.5 text-sm text-[var(--ink)] focus:border-[var(--accent)] focus:outline-none"
            />
          </div>

          <div className="rounded-2xl border border-[var(--line)] bg-[var(--surface2)] p-3.5">
            <p className="text-xs font-bold text-[var(--ink)]">Selbstprüfkriterien:</p>
            <ul className="mt-1 list-disc space-y-1 pl-4 text-xs text-[var(--ink2)]">
              {activeSession.productionTask.keyEvaluationCriteria.map((c, i) => (
                <li key={i}>{c}</li>
              ))}
            </ul>
          </div>

          <AiFeedbackShareButton
            task={activeSession.productionTask.situationBrief}
            response={productionUserInput}
            evidenceType="Spontane Reaktionsproduktion (Tagestraining)"
          />
        </section>
      )}

      {/* Phase 4: PRECISION_CORRECTION */}
      {currentPhase === 'PRECISION_CORRECTION' && (
        <section className="c1-animate-enter space-y-4 rounded-3xl border border-[var(--line)] bg-[var(--surface)] p-5">
          <p className="text-[11px] font-bold uppercase tracking-wider text-[var(--coral)]">
            Phase 4 · Präzisions- und Registerkorrektur
          </p>
          <p className="text-xs text-[var(--ink2)]">
            Vergleiche typische Schwachstellen aus den Beispiel-Drills mit idiomatischen
            C1-Alternativen:
          </p>

          <div className="space-y-3">
            {activeSession.correctionTask.errorItems.map((item, idx) => (
              <ImprintSentenceSurgeryCard
                key={item.id}
                label={`Korrektur ${idx + 1} · Register & Präzision`}
                before={item.userMistake}
                diagnosis={item.ruleOrNuance}
                after={item.nativeReformulation}
                steps={['Stolperfalle erkennen', 'Regel prüfen', 'C1-Formulierung übernehmen']}
              />
            ))}
          </div>
        </section>
      )}

      {/* Phase 5: TRANSFER_TEST */}
      {currentPhase === 'TRANSFER_TEST' && (
        <section className="space-y-4 rounded-3xl border border-[var(--line)] bg-[var(--surface)] p-5">
          <p className="text-[11px] font-bold uppercase tracking-wider text-[var(--accent-ink)]">
            Phase 5 · Kontext-Transfer ({activeSession.transferTask.transferDomain})
          </p>
          <h2 className="text-base font-extrabold text-[var(--ink)]">
            {activeSession.transferTask.scenarioPrompt}
          </h2>
          <p className="text-xs text-[var(--ink2)]">
            Erwartung: {activeSession.transferTask.benchmarkExpectation}
          </p>

          <textarea
            rows={5}
            value={transferUserInput}
            onChange={(e) => updateTransferInput(e.target.value)}
            placeholder="Übertrage die Struktur ohne Blick auf Notizen in diesen neuen Kontext..."
            className="w-full rounded-2xl border border-[var(--line)] bg-[var(--bg)] p-3.5 text-sm text-[var(--ink)] focus:border-[var(--accent)] focus:outline-none"
          />

          <AiFeedbackShareButton
            task={activeSession.transferTask.scenarioPrompt}
            response={transferUserInput}
            evidenceType="Transferaufgabe ohne Vorlage"
          />
        </section>
      )}

      {/* Bottom Advance Bar */}
      <div className="flex items-center justify-end">
        <button
          type="button"
          onClick={advanceSessionPhase}
          className="inline-flex items-center gap-2 rounded-2xl bg-[var(--accent)] px-5 py-3 text-sm font-extrabold text-[var(--on-accent)] transition hover:opacity-95"
        >
          {currentPhase === 'TRANSFER_TEST'
            ? 'Station abschließen'
            : 'Zur nächsten Phase'}
          <ArrowRight className="h-4 w-4" />
        </button>
      </div>
    </div>
  );
};
