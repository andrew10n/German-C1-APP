// Web SpeechSynthesis (TTS) + Web SpeechRecognition (ASR) service for German (de-DE)

export interface SpeechRecognitionHandlers {
  onInterim?: (transcript: string) => void;
  onResult: (transcript: string, confidence: number) => void;
  onError?: (message: string) => void;
  onEnd?: () => void;
}

interface SpeechRecognitionAlternativeLike {
  transcript: string;
  confidence: number;
}

interface SpeechRecognitionResultLike {
  isFinal: boolean;
  length: number;
  [index: number]: SpeechRecognitionAlternativeLike;
}

interface SpeechRecognitionEventLike {
  resultIndex: number;
  results: {
    length: number;
    [index: number]: SpeechRecognitionResultLike;
  };
}

interface SpeechRecognitionErrorEventLike {
  error: string;
}

interface SpeechRecognitionLike {
  lang: string;
  continuous: boolean;
  interimResults: boolean;
  maxAlternatives: number;
  onresult: ((event: SpeechRecognitionEventLike) => void) | null;
  onerror: ((event: SpeechRecognitionErrorEventLike) => void) | null;
  onend: (() => void) | null;
  start: () => void;
  stop: () => void;
  abort: () => void;
}

type SpeechRecognitionCtor = new () => SpeechRecognitionLike;

function getSpeechRecognitionCtor(): SpeechRecognitionCtor | null {
  if (typeof window === 'undefined') return null;
  const win = window as unknown as {
    SpeechRecognition?: SpeechRecognitionCtor;
    webkitSpeechRecognition?: SpeechRecognitionCtor;
  };
  return win.SpeechRecognition ?? win.webkitSpeechRecognition ?? null;
}

class WebSpeechService {
  private activeRecognition: SpeechRecognitionLike | null = null;
  private listeners = new Set<() => void>();
  private currentSpeakingId: string | null = null;

  subscribe(listener: () => void): () => void {
    this.listeners.add(listener);
    return () => {
      this.listeners.delete(listener);
    };
  }

  private notify() {
    this.listeners.forEach((fn) => fn());
  }

  isTtsSupported(): boolean {
    return typeof window !== 'undefined' && 'speechSynthesis' in window;
  }

  isAsrSupported(): boolean {
    return getSpeechRecognitionCtor() !== null;
  }

  getSpeakingId(): string | null {
    return this.currentSpeakingId;
  }

  pickGermanVoice(): SpeechSynthesisVoice | null {
    if (!this.isTtsSupported()) return null;
    const voices = window.speechSynthesis.getVoices();
    if (!voices.length) return null;
    return (
      voices.find((v) => v.lang.toLowerCase() === 'de-de' && !v.localService) ??
      voices.find((v) => v.lang.toLowerCase() === 'de-de') ??
      voices.find((v) => v.lang.toLowerCase().startsWith('de')) ??
      null
    );
  }

  speak(
    text: string,
    options: { rate?: number; pitch?: number; id?: string; onEnd?: () => void } = {},
  ): boolean {
    if (!this.isTtsSupported() || !text.trim()) return false;
    try {
      window.speechSynthesis.cancel();
      const utterance = new SpeechSynthesisUtterance(text);
      utterance.lang = 'de-DE';
      utterance.rate = Math.min(2.0, Math.max(0.5, options.rate ?? 1.0));
      utterance.pitch = Math.min(2.0, Math.max(0.5, options.pitch ?? 1.0));
      const voice = this.pickGermanVoice();
      if (voice) utterance.voice = voice;

      this.currentSpeakingId = options.id ?? 'tts-active';
      this.notify();

      const clear = () => {
        this.currentSpeakingId = null;
        this.notify();
        options.onEnd?.();
      };
      utterance.onend = clear;
      utterance.onerror = clear;

      window.speechSynthesis.speak(utterance);
      return true;
    } catch {
      this.currentSpeakingId = null;
      this.notify();
      return false;
    }
  }

  stopSpeaking() {
    if (!this.isTtsSupported()) return;
    try {
      window.speechSynthesis.cancel();
    } catch {
      // ignore
    }
    if (this.currentSpeakingId !== null) {
      this.currentSpeakingId = null;
      this.notify();
    }
  }

  startRecognition(handlers: SpeechRecognitionHandlers): boolean {
    const Ctor = getSpeechRecognitionCtor();
    if (!Ctor) {
      handlers.onError?.(
        'Spracherkennung (Web Speech API) wird in diesem Browser nicht unterstützt. Bitte nutze die manuelle Transkript-Eingabe.',
      );
      return false;
    }

    this.stopRecognition();

    try {
      const recognition = new Ctor();
      recognition.lang = 'de-DE';
      recognition.continuous = false;
      recognition.interimResults = true;
      recognition.maxAlternatives = 1;

      let finalTranscript = '';
      let finalConfidence = 0.85;

      recognition.onresult = (event) => {
        let interim = '';
        for (let i = event.resultIndex; i < event.results.length; i++) {
          const result = event.results[i];
          const alt = result[0];
          if (!alt) continue;
          if (result.isFinal) {
            finalTranscript += (finalTranscript ? ' ' : '') + alt.transcript.trim();
            if (typeof alt.confidence === 'number' && alt.confidence > 0) {
              finalConfidence = alt.confidence;
            }
          } else {
            interim += (interim ? ' ' : '') + alt.transcript;
          }
        }
        const combined = [finalTranscript, interim].filter(Boolean).join(' ').trim();
        if (combined) handlers.onInterim?.(combined);
      };

      recognition.onerror = (event) => {
        const map: Record<string, string> = {
          'not-allowed': 'Mikrofonzugriff wurde nicht erlaubt.',
          'no-speech': 'Keine Sprache erkannt. Bitte erneut versuchen oder Text eingeben.',
          network: 'Spracherkennung benötigt eine Netzwerkverbindung.',
          'audio-capture': 'Kein Mikrofon gefunden.',
        };
        handlers.onError?.(map[event.error] ?? `Spracherkennungsfehler: ${event.error}`);
      };

      recognition.onend = () => {
        this.activeRecognition = null;
        if (finalTranscript.trim()) {
          handlers.onResult(finalTranscript.trim(), finalConfidence);
        }
        handlers.onEnd?.();
      };

      this.activeRecognition = recognition;
      recognition.start();
      return true;
    } catch {
      this.activeRecognition = null;
      handlers.onError?.(
        'Mikrofonstart fehlgeschlagen. Bitte manuelle Transkript-Eingabe verwenden.',
      );
      return false;
    }
  }

  stopRecognition() {
    if (this.activeRecognition) {
      try {
        this.activeRecognition.stop();
      } catch {
        // ignore
      }
      this.activeRecognition = null;
    }
  }
}

export const speechService = new WebSpeechService();
