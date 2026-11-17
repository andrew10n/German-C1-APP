import React, { useEffect, useState } from 'react';
import {
  ArrowRight,
  CheckCircle2,
  Eye,
  EyeOff,
  Headphones,
  Play,
  Square,
  XCircle,
} from 'lucide-react';
import { AcousticWaveform } from '../components/AcousticWaveform';
import { BookAudioPracticeSection } from '../components/BookAudioPracticeSection';
import { ExternalListeningSourcesSection } from '../components/ExternalListeningSourcesSection';
import { NativeListeningAudioPlayerComponent } from '../components/NativeListeningAudioPlayerComponent';
import { speechService } from '../services/speechService';
import { useApp } from '../state/AppContext';

type LabTab = 'SCENARIOS' | 'BOOK' | 'SOURCES' | 'CLIPS';

export const ListeningLabScreen: React.FC = () => {
  const {
    listeningItems,
    selectedLabItemIndex,
    playbackSpeed,
    isBlindTranscriptRevealed,
    selectedLabQuizAnswers,
    selectLabItem,
    setPlaybackSpeed,
    revealBlindTranscript,
    answerLabQuestion,
  } = useApp();

  const [activeTab, setActiveTab] = useState<LabTab>('SCENARIOS');
  const [speakingId, setSpeakingId] = useState<string | null>(
    speechService.getSpeakingId(),
  );

  useEffect(() => {
    return speechService.subscribe(() => setSpeakingId(speechService.getSpeakingId()));
  }, []);

  const currentItem = listeningItems[selectedLabItemIndex] ?? listeningItems[0];
  const isPlaying = currentItem ? speakingId === `lab-${currentItem.id}` : false;

  const togglePlay = () => {
    if (!currentItem) return;
    if (isPlaying) {
      speechService.stopSpeaking();
    } else {
      speechService.speak(currentItem.audioSpeakerText, {
        rate: playbackSpeed,
        id: `lab-${currentItem.id}`,
      });
    }
  };

  return (
    <div className="space-y-5">
      {/* Studio Banner */}
      <div className="relative overflow-hidden rounded-3xl border border-[var(--line)] bg-[var(--hero)]">
        <img
          src="/images/img_hero_studio.jpg"
          alt="Hör-Labor Studio"
          className="h-32 w-full object-cover opacity-35"
        />
        <div className="absolute inset-0 flex flex-col justify-end bg-gradient-to-t from-[var(--hero)] via-[var(--hero)]/65 to-transparent p-5">
          <span className="text-[11px] font-bold uppercase tracking-wider text-[var(--accent)]">
            Hörverstehen · Reduktionen & Subtext
          </span>
          <h1 className="font-display text-3xl font-extrabold text-[var(--on-hero)]">
            C1 Hör-Labor
          </h1>
        </div>
      </div>

      {/* Focused 4-Tab Lab Selector (1 Trainer on Screen at a Time) */}
      <div className="grid grid-cols-2 gap-1.5 rounded-2xl border border-[var(--line)] bg-[var(--surface)] p-1.5 sm:grid-cols-4">
        {(
          [
            { id: 'SCENARIOS', label: 'TTS-Szenarien' },
            { id: 'BOOK', label: 'Kursbuch-Audio' },
            { id: 'SOURCES', label: 'Originalquellen' },
            { id: 'CLIPS', label: 'Clip-Bibliothek' },
          ] as const
        ).map((tab) => (
          <button
            key={tab.id}
            type="button"
            onClick={() => setActiveTab(tab.id)}
            className={`rounded-xl px-2 py-2 text-xs font-extrabold whitespace-nowrap transition ${
              activeTab === tab.id
                ? 'bg-[var(--accent)] text-[var(--on-accent)]'
                : 'text-[var(--ink2)] hover:text-[var(--ink)]'
            }`}
          >
            {tab.label}
          </button>
        ))}
      </div>

      {activeTab === 'BOOK' && <BookAudioPracticeSection />}
      {activeTab === 'SOURCES' && <ExternalListeningSourcesSection />}
      {activeTab === 'CLIPS' && <NativeListeningAudioPlayerComponent />}

      {activeTab === 'SCENARIOS' && currentItem && (
        <section className="c1-animate-enter space-y-4 rounded-3xl border border-[var(--line)] bg-[var(--surface)] p-5 shadow-sm">
          <div className="flex gap-2 overflow-x-auto pb-1">
            {listeningItems.map((item, idx) => {
              const active = idx === selectedLabItemIndex;
              return (
                <button
                  key={item.id}
                  type="button"
                  onClick={() => selectLabItem(idx)}
                  className={`inline-flex shrink-0 items-center gap-1.5 rounded-xl border px-3 py-2 text-xs font-bold transition ${
                    active
                      ? 'border-[var(--accent)] bg-[var(--accent-soft)] text-[var(--accent-ink)]'
                      : 'border-[var(--line)] bg-[var(--surface2)] text-[var(--ink2)] hover:text-[var(--ink)]'
                  }`}
                >
                  <Headphones className="h-3.5 w-3.5" />
                  {item.title}
                </button>
              );
            })}
          </div>

          <div className="rounded-2xl border border-[var(--line)] bg-[var(--surface2)] p-4">
            <div className="flex flex-wrap items-start justify-between gap-2">
              <div>
                <h2 className="text-base font-extrabold text-[var(--ink)]">
                  {currentItem.title}
                </h2>
                <p className="text-xs text-[var(--ink2)]">
                  {currentItem.environment} · Vorlage: {currentItem.accentOrDialect}
                </p>
              </div>
              <button
                type="button"
                onClick={togglePlay}
                className={`inline-flex items-center gap-2 rounded-2xl px-4 py-2 text-xs font-extrabold ${
                  isPlaying
                    ? 'bg-[var(--coral)] text-white'
                    : 'bg-[var(--accent)] text-[var(--on-accent)]'
                }`}
              >
                {isPlaying ? (
                  <Square className="h-3.5 w-3.5 fill-current" />
                ) : (
                  <Play className="h-3.5 w-3.5 fill-current" />
                )}
                {isPlaying ? 'Stoppen' : `Anhören (${playbackSpeed}x)`}
              </button>
            </div>

            <div className="mt-3">
              <AcousticWaveform
                active={isPlaying}
                variant="accent"
                height={34}
                label={isPlaying ? `Aktiv · ${playbackSpeed}x` : 'Hör-Szenario'}
              />
            </div>

            <div className="mt-3 flex flex-wrap items-center gap-1.5">
              <span className="text-[11px] font-semibold text-[var(--ink2)]">
                TTS-Tempo:
              </span>
              {[1.0, 1.15, 1.25, 1.35].map((r) => (
                <button
                  key={r}
                  type="button"
                  onClick={() => setPlaybackSpeed(r)}
                  className={`rounded-lg border px-2 py-1 text-[11px] font-bold tabular-nums ${
                    playbackSpeed === r
                      ? 'border-[var(--accent)] bg-[var(--accent-soft)] text-[var(--accent-ink)]'
                      : 'border-[var(--line)] bg-[var(--surface)] text-[var(--ink2)]'
                  }`}
                >
                  {r}x
                </button>
              ))}
            </div>
          </div>

          <div className="space-y-3">
            {currentItem.blindComprehensionQuestions.map((q, qIdx) => {
              const selected = selectedLabQuizAnswers[qIdx];
              const answered = selected !== undefined;
              return (
                <div
                  key={qIdx}
                  className="space-y-2 rounded-2xl border border-[var(--line)] bg-[var(--surface2)] p-4"
                >
                  <p className="text-xs font-bold text-[var(--ink)]">
                    Frage {qIdx + 1}: {q.question}
                  </p>
                  <div className="space-y-1.5">
                    {q.options.map((opt, oIdx) => {
                      const isSel = selected === oIdx;
                      const isRight = oIdx === q.correctIndex;
                      return (
                        <button
                          key={oIdx}
                          type="button"
                          disabled={answered}
                          onClick={() => answerLabQuestion(qIdx, oIdx)}
                          className={`flex w-full items-center gap-2 rounded-xl border p-2.5 text-left text-xs transition ${
                            answered && isRight
                              ? 'border-[var(--ok)] bg-[var(--ok-bg)] text-[var(--ink)]'
                              : answered && isSel && !isRight
                                ? 'border-[var(--coral)] bg-[var(--coral-soft)] text-[var(--ink)]'
                                : 'border-[var(--line)] bg-[var(--surface)] text-[var(--ink)] hover:border-[var(--accent)]'
                          }`}
                        >
                          {answered && isRight ? (
                            <CheckCircle2 className="h-4 w-4 shrink-0 text-[var(--ok)]" />
                          ) : answered && isSel && !isRight ? (
                            <XCircle className="h-4 w-4 shrink-0 text-[var(--coral)]" />
                          ) : (
                            <span className="h-3.5 w-3.5 shrink-0 rounded-full border border-[var(--ink2)]" />
                          )}
                          <span>{opt}</span>
                        </button>
                      );
                    })}
                  </div>
                  {answered && (
                    <p className="text-[11px] leading-relaxed text-[var(--ink2)]">
                      {q.rationaleWhyOthersAreWrong}
                    </p>
                  )}
                </div>
              );
            })}
          </div>

          <div>
            {!isBlindTranscriptRevealed ? (
              <button
                type="button"
                onClick={revealBlindTranscript}
                className="inline-flex items-center gap-1.5 rounded-xl border border-[var(--accent)] bg-[var(--accent-soft)] px-4 py-2 text-xs font-bold text-[var(--accent-ink)]"
              >
                <Eye className="h-3.5 w-3.5" />
                Transkript & Reduktionsformen enthüllen
              </button>
            ) : (
              <div className="c1-animate-enter space-y-3 rounded-2xl border border-[var(--line)] bg-[var(--surface2)] p-4">
                <div className="flex items-center justify-between">
                  <span className="text-xs font-bold text-[var(--accent-ink)]">
                    Markiertes Transkript & Subtext
                  </span>
                  <EyeOff className="h-4 w-4 text-[var(--ink2)]" />
                </div>
                <p className="text-xs leading-relaxed text-[var(--ink)]">
                  {currentItem.transcriptWithReductions}
                </p>
                <div className="grid gap-2 border-t border-[var(--line)] pt-3 sm:grid-cols-2">
                  {currentItem.reductionNotes.map((rn, idx) => (
                    <div
                      key={idx}
                      className="rounded-xl border border-[var(--line)] bg-[var(--surface)] p-3 text-xs"
                    >
                      <div className="flex flex-wrap items-center gap-1.5 font-extrabold">
                        <span className="rounded-md bg-[var(--coral-soft)] px-2 py-0.5 text-[var(--coral)]">
                          Gehört: „{rn.spokenForm}“
                        </span>
                        <ArrowRight className="h-3.5 w-3.5 text-[var(--accent-ink)]" />
                        <span className="rounded-md bg-[var(--ok-bg)] px-2 py-0.5 text-[var(--ok)]">
                          C1: „{rn.standardWrittenForm}“
                        </span>
                      </div>
                      <p className="mt-1.5 text-[11px] leading-relaxed text-[var(--ink2)]">
                        {rn.registerExplanation}
                      </p>
                    </div>
                  ))}
                </div>
                <p className="border-t border-[var(--line)] pt-2 text-xs text-[var(--ink)]">
                  <strong>Pragmatik & Subtext:</strong> {currentItem.contextAndSubtextAnalysis}
                </p>
              </div>
            )}
          </div>
        </section>
      )}
    </div>
  );
};
