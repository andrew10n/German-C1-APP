import React, { useEffect, useState } from 'react';
import {
  AlertTriangle,
  ArrowRight,
  MessageSquare,
  Send,
  Volume2,
} from 'lucide-react';
import { AcousticWaveform } from '../components/AcousticWaveform';
import { AiFeedbackShareButton } from '../components/AiFeedbackShareButton';
import { speechService } from '../services/speechService';
import { useApp } from '../state/AppContext';

export const ConversationSimulatorScreen: React.FC = () => {
  const {
    conversationScenarios,
    activeConversationIndex,
    currentConversationTurnIndex,
    interruptionTriggered,
    conversationUserResponse,
    responseLatencySeconds,
    conversationSubmitted,
    selectConversation,
    playOpeningStatement,
    updateConversationResponse,
    submitConversationResponse,
    nextConversationTurn,
  } = useApp();

  const [, setSpeakingId] = useState<string | null>(speechService.getSpeakingId());
  const speakingNow = speechService.getSpeakingId();

  useEffect(() => {
    return speechService.subscribe(() => setSpeakingId(speechService.getSpeakingId()));
  }, []);

  const conv =
    conversationScenarios[activeConversationIndex] ?? conversationScenarios[0];
  if (!conv) return null;

  const currentTurn = conv.turns[currentConversationTurnIndex];
  const hasNextTurn = currentConversationTurnIndex < conv.turns.length - 1;
  const threshold = Math.max(1, conv.unexpectedInterruption.triggerAfterSecond);
  const tensionRatio = Math.min(1, responseLatencySeconds / threshold);
  const ringColor = interruptionTriggered
    ? 'var(--coral)'
    : tensionRatio > 0.7
      ? '#F59E0B'
      : 'var(--accent)';

  return (
    <div className="space-y-5">
      <div>
        <p className="text-[11px] font-bold uppercase tracking-wider text-[var(--violet)]">
          {conversationScenarios.length} Szenarien-Variationen · Interaktionsdruck & Interrupts
        </p>
        <h1 className="font-display text-3xl font-extrabold text-[var(--ink)]">
          Gesprächssimulator
        </h1>
        <p className="mt-1 text-xs leading-relaxed text-[var(--ink2)]">
          Unbewertetes Interaktionstraining unter Zeitdruck. Nach Erreichen der
          Unterbrechungsschwelle wird ein Einwand eingeblendet und vorgelesen.
        </p>
      </div>

      {/* Scenario Picker */}
      <div className="flex gap-2 overflow-x-auto pb-1">
        {conversationScenarios.map((item, idx) => {
          const active = idx === activeConversationIndex;
          return (
            <button
              key={item.id}
              type="button"
              onClick={() => selectConversation(idx)}
              className={`shrink-0 rounded-xl border px-3 py-2 text-left text-xs transition ${
                active
                  ? 'border-[var(--accent)] bg-[var(--accent-soft)] font-extrabold text-[var(--accent-ink)]'
                  : 'border-[var(--line)] bg-[var(--surface)] font-semibold text-[var(--ink2)] hover:text-[var(--ink)]'
              }`}
            >
              <div>{item.title}</div>
              <div className="text-[10px] opacity-80">{item.variationLabel}</div>
            </button>
          );
        })}
      </div>

      {/* Main Dialogue Panel */}
      <section className="c1-animate-enter space-y-4 rounded-3xl border border-[var(--line)] bg-[var(--surface)] p-5 shadow-sm">
        <div className="flex flex-wrap items-center justify-between gap-3">
          <div>
            <span className="rounded-lg bg-[var(--violet-soft)] px-2.5 py-1 text-[10px] font-bold text-[var(--violet)]">
              {conv.environment} · {conv.variationLabel}
            </span>
            <h2 className="mt-1.5 text-lg font-extrabold text-[var(--ink)]">
              {conv.title}
            </h2>
            <p className="text-xs text-[var(--ink2)]">Rolle: {conv.rolePartner}</p>
          </div>

          <div className="flex items-center gap-3">
            {/* Animated Radial Latency & Interruption Ring */}
            <div className="flex items-center gap-2 rounded-2xl border border-[var(--line)] bg-[var(--surface2)] px-3 py-1.5">
              <div className="relative flex h-10 w-10 items-center justify-center">
                <svg className="h-10 w-10 -rotate-90" viewBox="0 0 40 40">
                  <circle
                    cx="20"
                    cy="20"
                    r="16"
                    fill="none"
                    stroke="var(--line)"
                    strokeWidth="3.5"
                  />
                  <circle
                    cx="20"
                    cy="20"
                    r="16"
                    fill="none"
                    stroke={ringColor}
                    strokeWidth="3.5"
                    strokeLinecap="round"
                    strokeDasharray={100.53}
                    strokeDashoffset={100.53 * (1 - tensionRatio)}
                    className="transition-all duration-300"
                  />
                </svg>
                <span className="absolute text-[10px] font-black tabular-nums text-[var(--ink)]">
                  {responseLatencySeconds}s
                </span>
              </div>
              <div className="text-[10px] leading-tight">
                <p className="font-extrabold text-[var(--ink)]">
                  {interruptionTriggered ? 'Einwand aktiv' : `Schwelle ${threshold}s`}
                </p>
                <p className="text-[var(--ink2)]">Reaktionsdruck</p>
              </div>
            </div>

            <button
              type="button"
              onClick={playOpeningStatement}
              className="inline-flex items-center gap-1.5 rounded-xl bg-[var(--accent)] px-3 py-2 text-xs font-bold text-[var(--on-accent)]"
            >
              <Volume2 className="h-3.5 w-3.5" />
              Einstieg anhören
            </button>
          </div>
        </div>

        <AcousticWaveform
          active={Boolean(speakingNow)}
          variant={interruptionTriggered ? 'coral' : 'violet'}
          height={28}
          label={speakingNow ? 'Gesprächspartner spricht' : `Zug ${currentConversationTurnIndex + 1}/${conv.turns.length}`}
        />

        {/* Dialogue Timeline Dots */}
        <div className="flex items-center gap-2">
          {conv.turns.map((_, i) => (
            <div
              key={i}
              className={`h-1.5 flex-1 rounded-full transition-all duration-300 ${
                i < currentConversationTurnIndex
                  ? 'bg-[var(--ok)]'
                  : i === currentConversationTurnIndex
                    ? 'bg-[var(--accent)]'
                    : 'bg-[var(--surface2)]'
              }`}
            />
          ))}
        </div>

        {/* Visual Pragmatik- & Eskalationskompass */}
        <div className="rounded-2xl border border-white/10 bg-[var(--hero)] p-3.5 text-[var(--on-hero)]">
          <div className="flex flex-wrap items-center justify-between gap-2 text-[10px] font-extrabold uppercase tracking-wider">
            <span className="text-[var(--accent)]">
              Pragmatik-Kompass · {currentTurn?.targetRegister ?? 'C1 Sachlich'}
            </span>
            <span
              className={
                interruptionTriggered
                  ? 'text-[var(--coral)]'
                  : tensionRatio > 0.7
                    ? 'text-amber-300'
                    : 'text-[var(--ok)]'
              }
            >
              {interruptionTriggered
                ? 'Status: Einwand abfangen'
                : tensionRatio > 0.7
                  ? 'Status: Zeitdruck steigt'
                  : 'Status: Souveräne Führung'}
            </span>
          </div>
          <div className="mt-2.5 grid grid-cols-3 gap-2 text-[11px]">
            {[
              {
                step: '1. Anerkennen',
                cue: 'Einwand spiegeln',
                active: interruptionTriggered || currentConversationTurnIndex === 0,
              },
              {
                step: '2. Abgrenzen',
                cue: 'Präzise einordnen',
                active: currentConversationTurnIndex >= 1,
              },
              {
                step: '3. Abschließen',
                cue: 'Nächsten Schritt setzen',
                active: Boolean(conversationSubmitted || currentConversationTurnIndex === conv.turns.length - 1),
              },
            ].map((node) => (
              <div
                key={node.step}
                className={`rounded-xl border px-2.5 py-2 transition ${
                  node.active
                    ? 'border-[var(--accent)] bg-[var(--accent)]/15'
                    : 'border-white/15 bg-white/5 opacity-80'
                }`}
              >
                <p className="font-extrabold text-[var(--accent)]">{node.step}</p>
                <p className="mt-0.5 text-[10px] text-[var(--on-hero2)]">{node.cue}</p>
              </div>
            ))}
          </div>
        </div>

        {/* Opening Statement */}
        <div className="rounded-2xl rounded-tl-sm border border-[var(--line)] bg-[var(--surface2)] p-4">
          <p className="text-[11px] font-bold uppercase tracking-wider text-[var(--ink2)]">
            Gesprächseröffnung ({conv.rolePartner})
          </p>
          <p className="mt-1 text-sm font-medium text-[var(--ink)]">
            „{conv.openingStatement}“
          </p>
        </div>

        {/* Active Turn Prompt */}
        {currentTurn && (
          <div className="space-y-3 rounded-2xl border border-[var(--accent)]/30 bg-[var(--accent-soft)]/50 p-4">
            <div className="flex flex-wrap items-center justify-between gap-2">
              <span className="inline-flex items-center gap-1.5 text-xs font-extrabold text-[var(--accent-ink)]">
                <MessageSquare className="h-3.5 w-3.5" />
                Zug {currentConversationTurnIndex + 1} / {conv.turns.length} ·{' '}
                {currentTurn.targetRegister}
              </span>
              <button
                type="button"
                onClick={() =>
                  speechService.speak(currentTurn.partnerSpeech, {
                    id: `turn-speech-${currentConversationTurnIndex}`,
                  })
                }
                className="inline-flex items-center gap-1 text-xs font-bold text-[var(--accent-ink)]"
              >
                <Volume2 className="h-3.5 w-3.5" />
                Vorlesen
              </button>
            </div>

            <p className="text-sm font-semibold text-[var(--ink)]">
              „{currentTurn.partnerSpeech}“
            </p>
            <p className="text-xs leading-relaxed text-[var(--ink2)]">
              <strong>Dein Auftrag:</strong> {currentTurn.expectedUserRolePrompt}
            </p>

            {/* Suggested C1 Connectors */}
            <div className="flex flex-wrap items-center gap-1.5 pt-1">
              <span className="text-[11px] font-bold text-[var(--ink2)]">
                Nützliche C1-Redemittel:
              </span>
              {currentTurn.suggestedC1Connectors.map((conn) => (
                <button
                  key={conn}
                  type="button"
                  onClick={() =>
                    updateConversationResponse(
                      conversationUserResponse
                        ? `${conversationUserResponse} ${conn}`
                        : conn,
                    )
                  }
                  className="rounded-lg border border-[var(--line)] bg-[var(--surface)] px-2.5 py-1 text-[11px] font-semibold text-[var(--ink)] hover:border-[var(--accent)]"
                >
                  + {conn}
                </button>
              ))}
            </div>
          </div>
        )}

        {/* Sudden Interruption Banner */}
        {interruptionTriggered && (
          <div className="space-y-1.5 rounded-2xl border border-[var(--coral)] bg-[var(--coral-soft)] p-4">
            <div className="flex items-center gap-2 text-xs font-extrabold uppercase tracking-wider text-[var(--coral)]">
              <AlertTriangle className="h-4 w-4" />
              Spontane Unterbrechung!
            </div>
            <p className="text-sm font-bold text-[var(--ink)]">
              „{conv.unexpectedInterruption.partnerInterruptSpeech}“
            </p>
            <p className="text-xs text-[var(--ink2)]">
              <strong>Reaktionsziel:</strong> {conv.unexpectedInterruption.reactionObjective}
            </p>
          </div>
        )}

        {/* Response Form */}
        <div>
          <label className="block text-xs font-bold text-[var(--ink2)]">
            Deine spontane Reaktion
          </label>
          <textarea
            rows={4}
            value={conversationUserResponse}
            onChange={(e) => updateConversationResponse(e.target.value)}
            placeholder="Antworte präzise im passenden Register (Fokus: nicht rechtfertigen, Argument strukturieren)..."
            className="mt-1.5 w-full rounded-2xl border border-[var(--line)] bg-[var(--bg)] p-3.5 text-sm text-[var(--ink)] focus:border-[var(--accent)] focus:outline-none"
          />
          <p className="mt-1 text-[11px] text-[var(--ink2)]">
            Trainingsfokus: {conv.evaluationFocus}
          </p>
        </div>

        <div className="flex flex-wrap items-center justify-between gap-2">
          <div className="flex flex-wrap items-center gap-2">
            <button
              type="button"
              disabled={!conversationUserResponse.trim() || conversationSubmitted}
              onClick={() => submitConversationResponse(conversationUserResponse)}
              className="inline-flex items-center gap-1.5 rounded-xl bg-[var(--accent)] px-4 py-2 text-xs font-extrabold text-[var(--on-accent)] disabled:opacity-45"
            >
              <Send className="h-3.5 w-3.5" />
              {conversationSubmitted ? 'Antwort erfasst' : 'Reaktion abschließen'}
            </button>

            <AiFeedbackShareButton
              task={`${conv.title} (${conv.variationLabel}): ${currentTurn?.expectedUserRolePrompt ?? conv.openingStatement}`}
              response={conversationUserResponse}
              evidenceType="Gesprächssimulation / Interaktionsreaktion"
            />
          </div>

          {hasNextTurn && (
            <button
              type="button"
              onClick={nextConversationTurn}
              className="inline-flex items-center gap-1.5 rounded-xl border border-[var(--accent)] bg-[var(--accent-soft)] px-4 py-2 text-xs font-bold text-[var(--accent-ink)]"
            >
              Nächster Zug
              <ArrowRight className="h-3.5 w-3.5" />
            </button>
          )}
        </div>
      </section>
    </div>
  );
};
