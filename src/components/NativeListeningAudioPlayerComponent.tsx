import React, { useEffect, useState } from 'react';
import {
  ArrowRight,
  CheckCircle2,
  Eye,
  EyeOff,
  Gauge,
  Headphones,
  Play,
  RotateCcw,
  Square,
  XCircle,
} from 'lucide-react';
import { AcousticWaveform } from './AcousticWaveform';
import { useApp } from '../state/AppContext';
import { speechService } from '../services/speechService';

export const NativeListeningAudioPlayerComponent: React.FC = () => {
  const { listeningItems, recordPracticeActivity } = useApp();
  const clips = listeningItems.slice(0, 5);
  const [selectedIndex, setSelectedIndex] = useState(0);
  const clip = clips[selectedIndex] ?? clips[0];

  const [speed, setSpeed] = useState(clip?.nativeSpeechSpeed ?? 1.0);
  const [showTranscript, setShowTranscript] = useState(false);
  const [selectedQuizOption, setSelectedQuizOption] = useState<number | null>(null);
  const [speakingId, setSpeakingId] = useState<string | null>(speechService.getSpeakingId());
  const [ttsError, setTtsError] = useState<string | null>(null);

  useEffect(() => {
    return speechService.subscribe(() => {
      setSpeakingId(speechService.getSpeakingId());
    });
  }, []);

  useEffect(() => {
    if (clip) {
      setSpeed(clip.nativeSpeechSpeed);
      setShowTranscript(false);
      setSelectedQuizOption(null);
      setTtsError(null);
    }
  }, [selectedIndex, clip]);

  if (!clip) return null;
  const isPlaying = speakingId === clip.id;
  const quiz = clip.blindComprehensionQuestions[0];

  const startTts = (rate = speed) => {
    setTtsError(null);
    const ok = speechService.speak(clip.audioSpeakerText, {
      rate,
      id: clip.id,
    });
    if (!ok) {
      setTtsError('Web Speech Synthesis (de-DE) wird von diesem Browser nicht unterstützt.');
    }
  };

  return (
    <section className="rounded-3xl border border-[var(--line)] bg-[var(--surface)] p-5 shadow-sm">
      <div className="flex flex-wrap items-center justify-between gap-2">
        <div>
          <div className="flex items-center gap-2">
            <span className="rounded-md border border-[var(--accent)]/40 bg-[var(--accent-soft)] px-2 py-0.5 text-[10px] font-black tracking-wider text-[var(--accent-ink)]">
              DEUTSCHE TTS-ÜBUNG
            </span>
            <span className="text-[10px] font-bold uppercase tracking-wider text-[var(--ink2)]">
              Unbewertete Hörpraxis
            </span>
          </div>
          <h3 className="mt-1 text-base font-extrabold text-[var(--ink)]">
            Sprachausgabe & Textverständnis
          </h3>
        </div>
        <span className="inline-flex items-center gap-1 rounded-xl border border-[var(--line)] bg-[var(--surface2)] px-2.5 py-1 text-xs font-bold text-[var(--accent-ink)] tabular-nums">
          <Gauge className="h-3.5 w-3.5" />
          TTS {speed}x
        </span>
      </div>

      <p className="mt-2 text-xs leading-relaxed text-[var(--ink2)]">
        Synthetische deutsche Gerätestimme (TTS), keine muttersprachliche Aufnahme.
        Dialektangaben beschreiben nur die Textvorlage; die Stimme bildet Dialekt,
        Betonung und Lautverschmelzungen nicht verlässlich ab. Kein C1- oder Prüfungsnachweis.
      </p>

      {ttsError && <p className="mt-2 text-xs font-medium text-[var(--coral)]">{ttsError}</p>}

      <div className="mt-3 flex gap-2 overflow-x-auto pb-1">
        {clips.map((c, idx) => {
          const active = idx === selectedIndex;
          return (
            <button
              key={c.id}
              type="button"
              onClick={() => {
                speechService.stopSpeaking();
                setSelectedIndex(idx);
              }}
              className={`inline-flex shrink-0 items-center gap-1.5 rounded-xl border px-3 py-2 text-xs font-bold transition ${
                active
                  ? 'border-[var(--accent)] bg-[var(--accent-soft)] text-[var(--accent-ink)]'
                  : 'border-[var(--line)] bg-[var(--surface2)] text-[var(--ink2)] hover:text-[var(--ink)]'
              }`}
            >
              <Headphones className="h-3.5 w-3.5" />
              {c.title}
            </button>
          );
        })}
      </div>

      <div className="mt-4 space-y-3 rounded-2xl border border-[var(--line)] bg-[var(--surface2)] p-4">
        <div className="flex flex-wrap items-center justify-between gap-2">
          <div>
            <h4 className="text-sm font-bold text-[var(--ink)]">{clip.title}</h4>
            <p className="text-[11px] text-[var(--ink2)]">Textvorlage: {clip.accentOrDialect}</p>
          </div>
          <span className="rounded-lg bg-[var(--violet-soft)] px-2 py-0.5 text-[10px] font-bold text-[var(--violet)]">
            {clip.environment}
          </span>
        </div>

        <AcousticWaveform
          active={isPlaying}
          variant="accent"
          height={34}
          label={isPlaying ? `Spielt ${speed}x` : 'TTS bereit'}
        />

        <div className="flex flex-wrap items-center justify-center gap-3 py-1">
          <button
            type="button"
            onClick={() => startTts(speed)}
            className="rounded-xl border border-[var(--line)] bg-[var(--surface)] p-2.5 text-[var(--ink)] hover:border-[var(--accent)]"
            aria-label="Erneut abspielen"
          >
            <RotateCcw className="h-4 w-4" />
          </button>
          <button
            type="button"
            onClick={() => (isPlaying ? speechService.stopSpeaking() : startTts(speed))}
            className={`inline-flex items-center gap-2 rounded-2xl px-5 py-2.5 text-xs font-extrabold transition ${
              isPlaying
                ? 'bg-[var(--coral)] text-white'
                : 'bg-[var(--accent)] text-[var(--on-accent)]'
            }`}
          >
            {isPlaying ? <Square className="h-4 w-4 fill-current" /> : <Play className="h-4 w-4 fill-current" />}
            {isPlaying ? 'Wiedergabe stoppen' : `TTS anhören (${speed}x)`}
          </button>
        </div>

        <div className="flex flex-wrap items-center justify-between gap-2 border-t border-[var(--line)] pt-2">
          <span className="text-[11px] font-semibold text-[var(--ink2)]">Sprechtempo:</span>
          <div className="flex flex-wrap gap-1.5">
            {[0.9, 1.0, 1.15, 1.25, 1.35].map((r) => (
              <button
                key={r}
                type="button"
                onClick={() => {
                  setSpeed(r);
                  if (isPlaying) startTts(r);
                }}
                className={`rounded-lg border px-2 py-1 text-[11px] font-bold tabular-nums ${
                  speed === r
                    ? 'border-[var(--accent)] bg-[var(--accent-soft)] text-[var(--accent-ink)]'
                    : 'border-[var(--line)] bg-[var(--surface)] text-[var(--ink2)]'
                }`}
              >
                {r}x
              </button>
            ))}
          </div>
        </div>
      </div>

      <div className="mt-4 flex items-center justify-between">
        <span className="text-xs font-bold text-[var(--ink)]">
          {showTranscript ? 'Transkript & Lautverschmelzungen:' : 'Blind-Hören (Gehör decodieren)'}
        </span>
        <button
          type="button"
          onClick={() => setShowTranscript((v) => !v)}
          className="inline-flex items-center gap-1 text-xs font-bold text-[var(--accent-ink)]"
        >
          {showTranscript ? <EyeOff className="h-3.5 w-3.5" /> : <Eye className="h-3.5 w-3.5" />}
          {showTranscript ? 'Verbergen' : 'Transkript anzeigen'}
        </button>
      </div>

      {showTranscript && (
        <div className="c1-animate-enter mt-2 space-y-3 rounded-2xl border border-[var(--line)] bg-[var(--surface2)] p-4 text-xs">
          <p className="leading-relaxed text-[var(--ink)]">{clip.transcriptWithReductions}</p>
          <div className="border-t border-[var(--line)] pt-3">
            <p className="mb-2 font-bold text-[var(--accent-ink)]">
              Lautverschmelzungen visuell (nicht aus Audio gemessen):
            </p>
            <div className="grid gap-2 sm:grid-cols-2">
              {clip.reductionNotes.map((n, i) => (
                <div
                  key={i}
                  className="rounded-xl border border-[var(--line)] bg-[var(--surface)] p-2.5"
                >
                  <div className="flex flex-wrap items-center gap-1.5 font-extrabold">
                    <span className="rounded-md bg-[var(--coral-soft)] px-2 py-0.5 text-[var(--coral)]">
                      „{n.spokenForm}“
                    </span>
                    <ArrowRight className="h-3.5 w-3.5 text-[var(--accent-ink)]" />
                    <span className="rounded-md bg-[var(--ok-bg)] px-2 py-0.5 text-[var(--ok)]">
                      „{n.standardWrittenForm}“
                    </span>
                  </div>
                  <p className="mt-1.5 text-[11px] leading-relaxed text-[var(--ink2)]">
                    {n.registerExplanation}
                  </p>
                </div>
              ))}
            </div>
          </div>
        </div>
      )}

      {quiz && (
        <div className="mt-4 space-y-2 rounded-2xl border border-[var(--line)] bg-[var(--surface2)] p-4">
          <p className="text-xs font-bold text-[var(--accent-ink)]">Verständnis-Check:</p>
          <p className="text-xs font-semibold text-[var(--ink)]">{quiz.question}</p>
          <div className="space-y-1.5">
            {quiz.options.map((opt, idx) => {
              const selected = selectedQuizOption === idx;
              const isCorrect = idx === quiz.correctIndex;
              const submitted = selectedQuizOption !== null;
              return (
                <button
                  key={idx}
                  type="button"
                  disabled={submitted}
                  onClick={() => {
                    setSelectedQuizOption(idx);
                    recordPracticeActivity('TTS-Textverständnis (unbewertete Übung)');
                  }}
                  className={`flex w-full items-center gap-2 rounded-xl border p-2.5 text-left text-xs transition ${
                    submitted && isCorrect
                      ? 'border-[var(--ok)] bg-[var(--ok-bg)] text-[var(--ink)]'
                      : submitted && selected && !isCorrect
                        ? 'border-[var(--coral)] bg-[var(--coral-soft)] text-[var(--ink)]'
                        : 'border-[var(--line)] bg-[var(--surface)] text-[var(--ink)] hover:border-[var(--accent)]'
                  }`}
                >
                  {submitted && isCorrect ? (
                    <CheckCircle2 className="h-4 w-4 shrink-0 text-[var(--ok)]" />
                  ) : submitted && selected && !isCorrect ? (
                    <XCircle className="h-4 w-4 shrink-0 text-[var(--coral)]" />
                  ) : (
                    <span className="h-3.5 w-3.5 shrink-0 rounded-full border border-[var(--ink2)]" />
                  )}
                  <span>{opt}</span>
                </button>
              );
            })}
          </div>
          {selectedQuizOption !== null && (
            <p className="pt-1 text-[11px] leading-relaxed text-[var(--ink2)]">
              {quiz.rationaleWhyOthersAreWrong}
            </p>
          )}
        </div>
      )}
    </section>
  );
};
