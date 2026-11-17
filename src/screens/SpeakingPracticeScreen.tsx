import React, { useEffect, useState } from 'react';
import {
  CheckCircle2,
  Mic,
  MicOff,
  Play,
  RotateCcw,
  Square,
  Volume2,
  X,
} from 'lucide-react';
import { AcousticWaveform } from '../components/AcousticWaveform';
import { AiFeedbackShareButton } from '../components/AiFeedbackShareButton';
import { speechService } from '../services/speechService';
import { useApp } from '../state/AppContext';
import type { PronunciationWordResult } from '../types';

export const SpeakingPracticeScreen: React.FC = () => {
  const {
    speakingPrompts,
    currentSpeakingPromptIndex,
    setCurrentSpeakingPromptIndex,
    latestPronunciationResult,
    analyzeSpokenResponse,
    clearPronunciationResult,
  } = useApp();

  const prompt =
    speakingPrompts[currentSpeakingPromptIndex] ?? speakingPrompts[0];

  const [isRecording, setIsRecording] = useState(false);
  const [interimText, setInterimText] = useState('');
  const [manualText, setManualText] = useState('');
  const [asrError, setAsrError] = useState<string | null>(null);
  const [showDemoPreview, setShowDemoPreview] = useState(false);
  const [speakingId, setSpeakingId] = useState<string | null>(
    speechService.getSpeakingId(),
  );
  const [selectedWordDetail, setSelectedWordDetail] =
    useState<PronunciationWordResult | null>(null);

  useEffect(() => {
    return speechService.subscribe(() => setSpeakingId(speechService.getSpeakingId()));
  }, []);

  if (!prompt) return null;

  const isPlayingModel = speakingId === `speak-model-${prompt.id}`;

  const handleSelectPrompt = (idx: number) => {
    speechService.stopSpeaking();
    speechService.stopRecognition();
    setIsRecording(false);
    setInterimText('');
    setManualText('');
    setAsrError(null);
    setShowDemoPreview(false);
    clearPronunciationResult();
    setCurrentSpeakingPromptIndex(idx);
  };

  const toggleRecording = () => {
    if (isRecording) {
      speechService.stopRecognition();
      setIsRecording(false);
      return;
    }
    speechService.stopSpeaking();
    setAsrError(null);
    setInterimText('');
    const started = speechService.startRecognition({
      onInterim: (text) => setInterimText(text),
      onResult: (transcript, confidence) => {
        setManualText(transcript);
        analyzeSpokenResponse(prompt, transcript, confidence);
      },
      onError: (msg) => {
        setAsrError(msg);
        setIsRecording(false);
      },
      onEnd: () => {
        setIsRecording(false);
      },
    });
    if (started) setIsRecording(true);
  };

  const handleManualCompare = () => {
    if (!manualText.trim()) return;
    setAsrError(null);
    analyzeSpokenResponse(prompt, manualText.trim(), 0.9);
  };

  const wordStatusStyle = (status: PronunciationWordResult['status']) => {
    switch (status) {
      case 'PERFECT':
        return 'border-[var(--ok)] bg-[var(--ok-bg)] text-[var(--ok)]';
      case 'ACCEPTABLE':
        return 'border-[var(--accent)] bg-[var(--accent-soft)] text-[var(--accent-ink)]';
      case 'MISPRONOUNCED':
        return 'border-amber-500/50 bg-amber-500/15 text-amber-400';
      case 'MISSING':
        return 'border-[var(--coral)] bg-[var(--coral-soft)] text-[var(--coral)]';
    }
  };

  return (
    <div className="space-y-5">
      <div>
        <p className="text-[11px] font-bold uppercase tracking-wider text-[var(--accent-ink)]">
          Transkript-Abgleich & Phonetikhinweise
        </p>
        <h1 className="font-display text-3xl font-extrabold text-[var(--ink)]">
          Aussprache-Labor
        </h1>
        <p className="mt-1 text-xs leading-relaxed text-[var(--ink2)]">
          Spracherkennung vergleicht das erkannte Texttranskript mit dem Zielsatz.
          Artikulation, Phoneme, Prosodie und GER-Niveau werden technisch nicht gemessen.
        </p>
      </div>

      {/* Prompt Carousel */}
      <div className="flex gap-2 overflow-x-auto pb-1">
        {speakingPrompts.map((item, idx) => {
          const active = idx === currentSpeakingPromptIndex;
          return (
            <button
              key={item.id}
              type="button"
              onClick={() => handleSelectPrompt(idx)}
              className={`inline-flex shrink-0 items-center gap-1.5 rounded-xl border px-3 py-2 text-xs font-bold transition ${
                active
                  ? 'border-[var(--accent)] bg-[var(--accent-soft)] text-[var(--accent-ink)]'
                  : 'border-[var(--line)] bg-[var(--surface)] text-[var(--ink2)] hover:text-[var(--ink)]'
              }`}
            >
              <span>{item.title}</span>
            </button>
          );
        })}
      </div>

      {/* Prompt Card */}
      <section className="space-y-4 rounded-3xl border border-[var(--line)] bg-[var(--surface)] p-5 shadow-sm">
        <div className="flex flex-wrap items-center justify-between gap-2">
          <div className="flex items-center gap-2">
            <span className="rounded-lg bg-[var(--violet-soft)] px-2.5 py-1 text-[10px] font-bold text-[var(--violet)]">
              {prompt.category}
            </span>
            <span className="rounded-lg bg-[var(--surface2)] px-2.5 py-1 text-[10px] font-bold text-[var(--ink2)]">
              {prompt.difficultyLevel}
            </span>
          </div>

          <button
            type="button"
            onClick={() =>
              isPlayingModel
                ? speechService.stopSpeaking()
                : speechService.speak(prompt.targetSentence, {
                    rate: prompt.audioPromptSpeed,
                    id: `speak-model-${prompt.id}`,
                  })
            }
            className="inline-flex items-center gap-1.5 rounded-xl border border-[var(--accent)] bg-[var(--accent-soft)] px-3 py-1.5 text-xs font-bold text-[var(--accent-ink)]"
          >
            {isPlayingModel ? (
              <Square className="h-3.5 w-3.5 fill-current" />
            ) : (
              <Volume2 className="h-3.5 w-3.5" />
            )}
            {isPlayingModel ? 'Stoppen' : 'Mustersatz anhören (TTS)'}
          </button>
        </div>

        <p className="text-xs text-[var(--ink2)]">{prompt.contextBrief}</p>

        <AcousticWaveform
          active={isPlayingModel || isRecording}
          variant={isRecording ? 'coral' : 'accent'}
          height={34}
          label={
            isRecording
              ? 'Mikrofon aktiv'
              : isPlayingModel
                ? 'Mustersatz (TTS)'
                : 'Stimm-Studio'
          }
        />

        <div className="rounded-2xl border border-[var(--line)] bg-[var(--surface2)] p-4">
          <p className="text-base font-bold leading-relaxed text-[var(--ink)]">
            „{prompt.targetSentence}“
          </p>
        </div>

        {/* IPA Phonetic Focus Chips */}
        {prompt.phoneticFocusWords.length > 0 && (
          <div className="space-y-2">
            <p className="text-[11px] font-bold uppercase tracking-wider text-[var(--ink2)]">
              Phonetische Fokuswörter (allgemeine Lerntipps)
            </p>
            <div className="grid gap-2 sm:grid-cols-2">
              {prompt.phoneticFocusWords.map((tip) => (
                <div
                  key={tip.word}
                  className="rounded-2xl border border-[var(--line)] bg-[var(--surface2)] p-3 text-xs"
                >
                  <div className="flex items-center justify-between gap-2">
                    <strong className="text-[var(--ink)]">{tip.word}</strong>
                    <span className="font-mono text-[11px] text-[var(--accent-ink)]">
                      {tip.ipa}
                    </span>
                  </div>
                  <p className="mt-0.5 text-[11px] font-semibold text-[var(--violet)]">
                    {tip.focusArea}
                  </p>
                  <p className="mt-1 leading-relaxed text-[var(--ink2)]">{tip.tipDe}</p>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Controls: SpeechRecognition or Manual Input */}
        <div className="space-y-3 border-t border-[var(--line)] pt-4">
          <div className="flex flex-wrap items-center gap-2">
            <button
              type="button"
              onClick={toggleRecording}
              className={`inline-flex items-center gap-2 rounded-2xl px-4 py-2.5 text-xs font-extrabold transition ${
                isRecording
                  ? 'bg-[var(--coral)] text-white'
                  : 'bg-[var(--accent)] text-[var(--on-accent)]'
              }`}
            >
              {isRecording ? (
                <MicOff className="h-4 w-4" />
              ) : (
                <Mic className="h-4 w-4" />
              )}
              {isRecording
                ? 'Erkennung stoppen'
                : 'Deutsch einsprechen (Web Speech API)'}
            </button>

            <button
              type="button"
              onClick={() => setShowDemoPreview((v) => !v)}
              className="inline-flex items-center gap-1.5 rounded-2xl border border-[var(--line)] bg-[var(--surface2)] px-3 py-2.5 text-xs font-bold text-[var(--ink)]"
            >
              <Play className="h-3.5 w-3.5" />
              Demo anzeigen · keine Aufnahme
            </button>
          </div>

          {showDemoPreview && (
            <div className="rounded-2xl border border-[var(--line)] bg-[var(--surface2)] p-3 text-xs leading-relaxed text-[var(--ink2)]">
              Demo-Hinweis: Keine Aufnahme gestartet. Ein echter Transkript-Abgleich
              erfolgt erst nach eigener Spracheingabe oder Texteingabe.
            </div>
          )}

          {isRecording && (
            <div className="c1-animate-enter flex items-center gap-3 rounded-2xl border border-[var(--coral)]/50 bg-[var(--coral-soft)] p-3.5 text-xs text-[var(--ink)]">
              <div className="relative flex h-9 w-9 shrink-0 items-center justify-center">
                <span className="c1-mic-ring absolute inset-0 rounded-full border-2 border-[var(--coral)]" />
                <span className="relative flex h-7 w-7 items-center justify-center rounded-full bg-[var(--coral)] text-white">
                  <Mic className="h-3.5 w-3.5" />
                </span>
              </div>
              <div>
                <strong className="text-[var(--coral)]">Live-Spracherkennung (de-DE):</strong>{' '}
                <span>{interimText || 'Bitte den Zielsatz deutlich einsprechen...'}</span>
              </div>
            </div>
          )}

          {asrError && (
            <p className="text-xs font-medium text-[var(--coral)]">{asrError}</p>
          )}

          <div>
            <label className="block text-xs font-bold text-[var(--ink2)]">
              Erkanntes oder manuell eingegebenes Transkript
            </label>
            <div className="mt-1.5 flex flex-col gap-2 sm:flex-row">
              <input
                type="text"
                value={manualText}
                onChange={(e) => setManualText(e.target.value)}
                placeholder="Gesprochenen Satz hier prüfen oder eintippen..."
                className="flex-1 rounded-xl border border-[var(--line)] bg-[var(--bg)] px-3.5 py-2.5 text-sm text-[var(--ink)] focus:border-[var(--accent)] focus:outline-none"
              />
              <button
                type="button"
                disabled={!manualText.trim()}
                onClick={handleManualCompare}
                className="inline-flex items-center justify-center gap-1.5 rounded-xl bg-[var(--accent)] px-4 py-2.5 text-xs font-bold text-[var(--on-accent)] disabled:opacity-45"
              >
                <CheckCircle2 className="h-4 w-4" />
                Mit Zielsatz abgleichen
              </button>
            </div>
          </div>
        </div>
      </section>

      {/* Pronunciation Feedback Card */}
      {latestPronunciationResult && (
        <section className="c1-animate-enter space-y-4 rounded-3xl border border-[var(--line)] bg-[var(--surface)] p-5 shadow-sm">
          <div className="flex flex-wrap items-center justify-between gap-3">
            <div className="flex items-center gap-3.5">
              {/* Animated SVG Accuracy Gauge */}
              <div className="relative flex h-16 w-16 shrink-0 items-center justify-center">
                <svg className="h-16 w-16 -rotate-90" viewBox="0 0 64 64">
                  <circle
                    cx="32"
                    cy="32"
                    r="26"
                    fill="none"
                    stroke="var(--surface2)"
                    strokeWidth="6"
                  />
                  <circle
                    cx="32"
                    cy="32"
                    r="26"
                    fill="none"
                    stroke="var(--accent)"
                    strokeWidth="6"
                    strokeLinecap="round"
                    strokeDasharray={163.36}
                    strokeDashoffset={
                      163.36 * (1 - latestPronunciationResult.accuracyScore / 100)
                    }
                    className="transition-all duration-700"
                  />
                </svg>
                <span className="absolute text-xs font-black tabular-nums text-[var(--ink)]">
                  {latestPronunciationResult.accuracyScore}%
                </span>
              </div>
              <div>
                <p className="text-[11px] font-bold uppercase tracking-wider text-[var(--coral)]">
                  {latestPronunciationResult.cefrBandDe}
                </p>
                <h2 className="text-lg font-extrabold text-[var(--ink)]">
                  {latestPronunciationResult.feedbackHeadlineDe}
                </h2>
              </div>
            </div>
            <button
              type="button"
              onClick={clearPronunciationResult}
              className="inline-flex items-center gap-1 rounded-xl border border-[var(--line)] bg-[var(--surface2)] px-2.5 py-1.5 text-xs font-semibold text-[var(--ink2)]"
            >
              <RotateCcw className="h-3.5 w-3.5" />
              Zurücksetzen
            </button>
          </div>

          <div className="rounded-2xl border border-[var(--line)] bg-[var(--surface2)] p-3 text-xs">
            <span className="font-bold text-[var(--ink2)]">Erkanntes Transkript: </span>
            <span className="text-[var(--ink)]">
              {latestPronunciationResult.transcribedText}
            </span>
          </div>

          <div>
            <p className="mb-2 text-[11px] font-bold uppercase tracking-wider text-[var(--ink2)]">
              Wort-für-Wort Abgleich (Wort antippen für IPA/Tipp)
            </p>
            <div className="flex flex-wrap gap-1.5">
              {latestPronunciationResult.wordResults.map((wr, idx) => (
                <button
                  key={`${wr.expectedWord}-${idx}`}
                  type="button"
                  onClick={() => setSelectedWordDetail(wr)}
                  className={`rounded-xl border px-2.5 py-1 text-xs font-bold transition hover:opacity-90 ${wordStatusStyle(
                    wr.status,
                  )}`}
                >
                  {wr.expectedWord}
                </button>
              ))}
            </div>
          </div>

          <ul className="space-y-1 text-xs leading-relaxed text-[var(--ink2)]">
            {latestPronunciationResult.actionableTipsDe.map((tip, idx) => (
              <li key={idx}>• {tip}</li>
            ))}
          </ul>

          <AiFeedbackShareButton
            task={`Aussprache- & Formulierungstraining: ${prompt.targetSentence}`}
            response={latestPronunciationResult.transcribedText}
            evidenceType="ASR-Transkript aus Sprechübung"
          />
        </section>
      )}

      {/* Word Phonetic Detail Modal */}
      {selectedWordDetail && (
        <div
          className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 p-4 backdrop-blur-xs"
          role="dialog"
          aria-modal="true"
        >
          <div className="w-full max-w-sm rounded-3xl border border-[var(--line)] bg-[var(--surface)] p-5 text-[var(--ink)] shadow-2xl">
            <div className="flex items-start justify-between gap-2">
              <div>
                <h3 className="text-lg font-extrabold">{selectedWordDetail.expectedWord}</h3>
                {selectedWordDetail.ipa && (
                  <p className="font-mono text-xs text-[var(--accent-ink)]">
                    {selectedWordDetail.ipa}
                  </p>
                )}
              </div>
              <button
                type="button"
                onClick={() => setSelectedWordDetail(null)}
                className="rounded-xl p-1 text-[var(--ink2)] hover:bg-[var(--surface2)]"
              >
                <X className="h-5 w-5" />
              </button>
            </div>

            <div className="mt-3 space-y-2 text-xs">
              <p>
                <strong className="text-[var(--ink2)]">Status:</strong>{' '}
                {selectedWordDetail.status}
              </p>
              <p>
                <strong className="text-[var(--ink2)]">Erkanntes Wort:</strong>{' '}
                {selectedWordDetail.spokenWord ?? '— (nicht erkannt)'}
              </p>
              <p className="rounded-xl bg-[var(--surface2)] p-3 leading-relaxed text-[var(--ink)]">
                {selectedWordDetail.phoneticTip ??
                  'Kein gesonderter Lauttipp hinterlegt. Vergleiche Betonung und Vokallänge mit der TTS-Referenz.'}
              </p>
            </div>

            <div className="mt-4 flex justify-end gap-2">
              <button
                type="button"
                onClick={() =>
                  speechService.speak(selectedWordDetail.expectedWord, {
                    rate: 0.85,
                    id: 'word-detail',
                  })
                }
                className="inline-flex items-center gap-1 rounded-xl border border-[var(--line)] bg-[var(--surface2)] px-3 py-1.5 text-xs font-bold"
              >
                <Volume2 className="h-3.5 w-3.5" />
                Anhören
              </button>
              <button
                type="button"
                onClick={() => setSelectedWordDetail(null)}
                className="rounded-xl bg-[var(--accent)] px-3.5 py-1.5 text-xs font-bold text-[var(--on-accent)]"
              >
                Schließen
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
