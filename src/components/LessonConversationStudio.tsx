import React, { useState } from 'react';
import {
  ArrowRight,
  CheckCircle2,
  HelpCircle,
  MessageSquare,
  Mic,
  MicOff,
  Volume2,
} from 'lucide-react';
import { speechService } from '../services/speechService';
import { AiFeedbackShareButton } from './AiFeedbackShareButton';
import { AcousticWaveform } from './AcousticWaveform';

interface LessonConversationStudioProps {
  unitId: string;
  unitTitle: string;
  contextTrack: string;
  defaultScenario?: {
    partnerRole: string;
    openingLine: string;
    interruptionPrompt: string;
    expectedStructures: string[];
  };
}

export const LessonConversationStudio: React.FC<LessonConversationStudioProps> = ({
  unitTitle,
  contextTrack,
  defaultScenario,
}) => {
  const [turnIndex, setTurnIndex] = useState(0);
  const [userReply, setUserReply] = useState('');
  const [isRecording, setIsRecording] = useState(false);
  const [completedTurns, setCompletedTurns] = useState<number[]>([]);
  const [showHint, setShowHint] = useState(false);

  const scenario = defaultScenario ?? {
    partnerRole: contextTrack === 'ENGINEERING' ? 'Chefingenieur / Review-Leiter' : contextTrack === 'GENERAL' ? 'Sachbearbeiter im Amt' : 'Gesprächspartner',
    openingLine: `Guten Tag. Wir besprechen heute den Fall aus "${unitTitle}". Worin sehen Sie den Kernpunkt?`,
    interruptionPrompt: 'Moment bitte – greifen Sie diesen Einwand auf: Wie belegen Sie diese Einschätzung konkret?',
    expectedStructures: ['Konjunktiv II (Könnte / Wäre)', 'Konzessive Einschränkung (Zwar …, allerdings …)', 'Präzise Nominalstil-Wendung'],
  };

  const turns = [
    {
      speaker: scenario.partnerRole,
      prompt: scenario.openingLine,
      goal: 'Eröffnen Sie das Gespräch souverän und fassen Sie das Anliegen in einem präzisen Satz zusammen.',
    },
    {
      speaker: `${scenario.partnerRole} (Gegenargument)`,
      prompt: scenario.interruptionPrompt,
      goal: 'Parieren Sie den Einwand höflich und bringen Sie eine begründete Gegenposition ein.',
    },
    {
      speaker: `${scenario.partnerRole} (Abschluss)`,
      prompt: 'Gut, wir halten das so fest. Haben Sie noch einen offenen Punkt zu klären?',
      goal: 'Formulieren Sie eine klare vorläufige Vereinbarung mit einem verbleibenden Prüfpunkt.',
    },
  ];

  const currentTurn = turns[Math.min(turnIndex, turns.length - 1)];

  return (
    <div className="space-y-4 rounded-3xl border border-[var(--line)] bg-[var(--surface)] p-5 shadow-sm c1-animate-enter">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div>
          <span className="inline-flex items-center gap-1.5 rounded-lg bg-[var(--accent-soft)] px-2.5 py-1 text-[10px] font-extrabold uppercase tracking-wider text-[var(--accent-ink)]">
            <MessageSquare className="h-3 w-3" />
            Konversations-Studio · Interaktives Rollenspiel
          </span>
          <h3 className="mt-1.5 text-base font-extrabold text-[var(--ink)]">
            {unitTitle}
          </h3>
          <p className="text-xs text-[var(--ink2)]">
            Rolle des Gegenübers: <span className="font-semibold text-[var(--ink)]">{scenario.partnerRole}</span>
          </p>
        </div>

        <button
          type="button"
          onClick={() => speechService.speak(currentTurn.prompt)}
          className="inline-flex items-center gap-1.5 rounded-xl bg-[var(--surface2)] border border-[var(--line)] px-3.5 py-2 text-xs font-bold text-[var(--ink)] hover:border-[var(--accent)]"
        >
          <Volume2 className="h-3.5 w-3.5 text-[var(--accent)]" />
          Aussage anhören
        </button>
      </div>

      {/* Progress Track */}
      <div className="flex gap-1.5">
        {turns.map((_, idx) => {
          const isDone = completedTurns.includes(idx);
          const isCurrent = idx === turnIndex;
          return (
            <div
              key={idx}
              className={`h-2 flex-1 rounded-full transition-all ${
                isDone
                  ? 'bg-[var(--accent)]'
                  : isCurrent
                    ? 'bg-[var(--violet)]'
                    : 'bg-[var(--line)]'
              }`}
            />
          );
        })}
      </div>

      {/* Current Prompt Card */}
      <div className="rounded-2xl border border-[var(--line)] bg-[var(--hero)] p-4 text-[var(--on-hero)] space-y-2">
        <div className="flex items-center justify-between text-[11px] font-bold text-[var(--accent)]">
          <span>Runde {turnIndex + 1} von {turns.length} · {currentTurn.speaker}</span>
          <span>{Math.round(((turnIndex + 1) / turns.length) * 100)}% Dialogfortschritt</span>
        </div>
        <p className="text-sm font-bold leading-relaxed text-[var(--on-hero)]">
          „{currentTurn.prompt}“
        </p>
        <p className="text-xs text-[var(--on-hero)]/80">
          <strong>Ziel für diese Erwiderung:</strong> {currentTurn.goal}
        </p>
      </div>

      {/* Strategic Hints */}
      <div>
        <button
          type="button"
          onClick={() => setShowHint((v) => !v)}
          className="inline-flex items-center gap-1 text-xs font-bold text-[var(--accent-ink)]"
        >
          <HelpCircle className="h-3.5 w-3.5" />
          {showHint ? 'C1-Redemittel ausblenden' : 'C1-Redemittel & Strukturen einblenden'}
        </button>
        {showHint && (
          <div className="mt-2 flex flex-wrap gap-1.5">
            {scenario.expectedStructures.map((st, i) => (
              <span
                key={i}
                className="rounded-lg bg-[var(--accent-soft)] px-2.5 py-1 text-[11px] font-bold text-[var(--accent-ink)]"
              >
                ✓ {st}
              </span>
            ))}
          </div>
        )}
      </div>

      {/* User Response Area */}
      <div className="space-y-3">
        <div className="flex flex-wrap items-center justify-between gap-2">
          <button
            type="button"
            onClick={() => setIsRecording((v) => !v)}
            className={`inline-flex items-center gap-1.5 rounded-xl px-3.5 py-2 text-xs font-extrabold ${
              isRecording
                ? 'bg-[var(--coral)] text-white'
                : 'bg-[var(--surface2)] border border-[var(--line)] text-[var(--ink)]'
            }`}
          >
            {isRecording ? <MicOff className="h-3.5 w-3.5" /> : <Mic className="h-3.5 w-3.5" />}
            {isRecording ? 'Sprachaufnahme stoppen' : 'Mündliche Erwiderung aufnehmen'}
          </button>
          {isRecording && (
            <span className="text-xs font-bold text-[var(--coral)] animate-pulse">
              ● Mikrofon aktiv (Sprechen Sie frei)
            </span>
          )}
        </div>

        {isRecording && (
          <AcousticWaveform active={isRecording} variant="coral" label="Mündlicher Dialog-Modus aktiv" />
        )}

        <textarea
          rows={3}
          value={userReply}
          onChange={(e) => setUserReply(e.target.value)}
          placeholder="Schreibe deine souveräne C1-Erwiderung (oder nutze die Spracheingabe)..."
          className="w-full rounded-2xl border border-[var(--line)] bg-[var(--bg)] p-3.5 text-sm text-[var(--ink)] focus:border-[var(--accent)] focus:outline-none"
        />

        <div className="flex items-center justify-between pt-1">
          <AiFeedbackShareButton
            task={`Rollenspiel: ${unitTitle} (${currentTurn.speaker})`}
            response={userReply}
            evidenceType="Konversations-Studio"
          />

          <button
            type="button"
            disabled={!userReply.trim()}
            onClick={() => {
              if (!completedTurns.includes(turnIndex)) {
                setCompletedTurns([...completedTurns, turnIndex]);
              }
              if (turnIndex < turns.length - 1) {
                setTurnIndex(turnIndex + 1);
                setUserReply('');
              }
            }}
            className="inline-flex items-center gap-1.5 rounded-xl bg-[var(--accent)] px-5 py-2.5 text-xs font-extrabold text-[var(--on-accent)] disabled:opacity-40"
          >
            {turnIndex === turns.length - 1 ? (
              <>
                <CheckCircle2 className="h-3.5 w-3.5" />
                Rollenspiel abschließen
              </>
            ) : (
              <>
                Nächster Zug
                <ArrowRight className="h-3.5 w-3.5" />
              </>
            )}
          </button>
        </div>
      </div>
    </div>
  );
};
