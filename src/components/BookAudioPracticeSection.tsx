import React, { useEffect, useRef, useState } from 'react';
import { FolderOpen, Pause, Play, Repeat, RotateCcw, Trash2 } from 'lucide-react';
import { useApp } from '../state/AppContext';
import { AcousticWaveform } from './AcousticWaveform';

function formatMs(ms: number): string {
  const totalSec = Math.max(0, Math.floor(ms / 1000));
  const m = Math.floor(totalSec / 60);
  const s = totalSec % 60;
  return `${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`;
}

interface BookAudioPracticeSectionProps {
  selectionKey?: string;
  onPlaybackStart?: () => void;
}

export const BookAudioPracticeSection: React.FC<BookAudioPracticeSectionProps> = ({
  selectionKey,
  onPlaybackStart,
}) => {
  const { getBookAudio, setBookAudioFile, clearBookAudio } = useApp();
  const bookAudio = getBookAudio(selectionKey);
  const audioRef = useRef<HTMLAudioElement | null>(null);
  const fileInputRef = useRef<HTMLInputElement | null>(null);

  const [isPlaying, setIsPlaying] = useState(false);
  const [speed, setSpeed] = useState(1.0);
  const [positionMs, setPositionMs] = useState(0);
  const [durationMs, setDurationMs] = useState(0);
  const [loopStartMs, setLoopStartMs] = useState<number | null>(null);
  const [loopEndMs, setLoopEndMs] = useState<number | null>(null);
  const [playbackError, setPlaybackError] = useState<string | null>(null);

  useEffect(() => {
    setIsPlaying(false);
    setPositionMs(0);
    setDurationMs(0);
    setLoopStartMs(null);
    setLoopEndMs(null);
    setPlaybackError(null);
    if (audioRef.current) {
      audioRef.current.pause();
      audioRef.current.load();
    }
  }, [bookAudio.objectUrl]);

  useEffect(() => {
    if (audioRef.current) {
      audioRef.current.playbackRate = speed;
    }
  }, [speed]);

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0] ?? null;
    if (file) {
      setBookAudioFile(file, selectionKey);
    }
    e.target.value = '';
  };

  const togglePlay = async () => {
    const el = audioRef.current;
    if (!el || !bookAudio.objectUrl) return;
    setPlaybackError(null);
    try {
      if (isPlaying) {
        el.pause();
        setIsPlaying(false);
      } else {
        onPlaybackStart?.();
        if (
          loopStartMs !== null &&
          loopEndMs !== null &&
          loopEndMs > loopStartMs &&
          (el.currentTime * 1000 < loopStartMs || el.currentTime * 1000 >= loopEndMs)
        ) {
          el.currentTime = loopStartMs / 1000;
        }
        el.playbackRate = speed;
        await el.play();
        setIsPlaying(true);
      }
    } catch {
      setPlaybackError('Diese Audiodatei konnte im Browser nicht abgespielt werden.');
      setIsPlaying(false);
    }
  };

  const seekBy = (deltaMs: number) => {
    const el = audioRef.current;
    if (!el || !bookAudio.objectUrl) return;
    const nextMs = Math.max(0, Math.min(durationMs || 0, el.currentTime * 1000 + deltaMs));
    el.currentTime = nextMs / 1000;
    setPositionMs(nextMs);
  };

  const handleTimeUpdate = () => {
    const el = audioRef.current;
    if (!el) return;
    const currentMs = Math.floor(el.currentTime * 1000);
    setPositionMs(currentMs);
    if (
      loopStartMs !== null &&
      loopEndMs !== null &&
      loopEndMs > loopStartMs &&
      currentMs >= loopEndMs
    ) {
      el.currentTime = loopStartMs / 1000;
    }
  };

  return (
    <section className="rounded-3xl border border-[var(--line)] bg-[var(--surface)] p-5 shadow-sm">
      <div className="flex flex-wrap items-start justify-between gap-3">
        <div>
          <p className="text-[11px] font-bold uppercase tracking-wider text-[var(--accent-ink)]">
            Eigene Kursbuch-Audio-Übung
          </p>
          <h3 className="mt-1 text-base font-bold text-[var(--ink)]">
            Lokale Kursbuch- oder Hördatei abspielen
          </h3>
        </div>
        {bookAudio.displayName && (
          <button
            type="button"
            onClick={() => clearBookAudio(selectionKey)}
            className="inline-flex items-center gap-1 rounded-xl border border-[var(--line)] bg-[var(--surface2)] px-2.5 py-1.5 text-xs font-semibold text-[var(--ink2)] hover:text-[var(--coral)]"
          >
            <Trash2 className="h-3.5 w-3.5" />
            Entfernen
          </button>
        )}
      </div>

      <p className="mt-2 text-xs leading-relaxed text-[var(--ink2)]">
        Wähle eine Audio-Datei aus deinen eigenen rechtmäßig erworbenen Lehrwerken
        (z. B. MP3/M4A von Sicher! C1 oder Aspekte neu C1). Dateien bleiben ausschließlich
        lokal in dieser Browser-Sitzung und werden nie exportiert oder hochgeladen.
      </p>

      <input
        ref={fileInputRef}
        type="file"
        accept="audio/*"
        onChange={handleFileChange}
        className="hidden"
      />

      <div className="mt-3 flex flex-wrap items-center gap-2">
        <button
          type="button"
          onClick={() => fileInputRef.current?.click()}
          className="inline-flex items-center gap-2 rounded-2xl bg-[var(--accent)] px-4 py-2.5 text-xs font-bold text-[var(--on-accent)] transition hover:opacity-95"
        >
          <FolderOpen className="h-4 w-4" />
          Eigene Audiodatei wählen
        </button>
        {bookAudio.displayName && (
          <span className="rounded-xl border border-[var(--line)] bg-[var(--surface2)] px-3 py-1.5 text-xs font-semibold text-[var(--ink)]">
            {bookAudio.displayName}
          </span>
        )}
      </div>

      {bookAudio.objectUrl && (
        <div className="mt-4 space-y-3 rounded-2xl border border-[var(--line)] bg-[var(--surface2)] p-4">
          <audio
            ref={audioRef}
            src={bookAudio.objectUrl}
            onLoadedMetadata={() => {
              if (audioRef.current && Number.isFinite(audioRef.current.duration)) {
                setDurationMs(Math.floor(audioRef.current.duration * 1000));
              }
            }}
            onTimeUpdate={handleTimeUpdate}
            onEnded={() => setIsPlaying(false)}
            onError={() => {
              setPlaybackError('Fehler beim Dekodieren der lokalen Audiodatei.');
              setIsPlaying(false);
            }}
          />

          <AcousticWaveform
            active={isPlaying}
            variant="accent"
            label={isPlaying ? 'Kursbuch-Audio aktiv' : 'Bereit'}
          />

          <div className="flex items-center gap-3">
            <button
              type="button"
              onClick={togglePlay}
              className="flex h-10 w-10 items-center justify-center rounded-2xl bg-[var(--accent)] text-[var(--on-accent)]"
              aria-label={isPlaying ? 'Pause' : 'Abspielen'}
            >
              {isPlaying ? <Pause className="h-5 w-5" /> : <Play className="h-5 w-5 fill-current" />}
            </button>
            <button
              type="button"
              onClick={() => seekBy(-5000)}
              className="rounded-xl border border-[var(--line)] bg-[var(--surface)] px-2.5 py-1.5 text-xs font-bold text-[var(--ink)]"
            >
              −5s
            </button>
            <button
              type="button"
              onClick={() => seekBy(5000)}
              className="rounded-xl border border-[var(--line)] bg-[var(--surface)] px-2.5 py-1.5 text-xs font-bold text-[var(--ink)]"
            >
              +5s
            </button>

            <div className="ml-auto text-xs font-bold tabular-nums text-[var(--ink2)]">
              {formatMs(positionMs)} / {formatMs(durationMs)}
            </div>
          </div>

          <input
            type="range"
            min={0}
            max={Math.max(100, durationMs)}
            value={Math.min(positionMs, Math.max(100, durationMs))}
            onChange={(e) => {
              const next = Number(e.target.value);
              setPositionMs(next);
              if (audioRef.current) {
                audioRef.current.currentTime = next / 1000;
              }
            }}
            className="w-full accent-[var(--accent)]"
          />

          <div className="flex flex-wrap items-center justify-between gap-2 pt-1">
            <div className="flex items-center gap-1.5">
              <span className="text-[11px] font-semibold text-[var(--ink2)]">Tempo:</span>
              {[0.75, 1.0, 1.25].map((r) => (
                <button
                  key={r}
                  type="button"
                  onClick={() => setSpeed(r)}
                  className={`rounded-lg border px-2 py-1 text-xs font-bold tabular-nums ${
                    speed === r
                      ? 'border-[var(--accent)] bg-[var(--accent-soft)] text-[var(--accent-ink)]'
                      : 'border-[var(--line)] bg-[var(--surface)] text-[var(--ink2)]'
                  }`}
                >
                  {r}×
                </button>
              ))}
            </div>

            <div className="flex flex-wrap items-center gap-1.5">
              <button
                type="button"
                onClick={() => setLoopStartMs(positionMs)}
                className="rounded-lg border border-[var(--line)] bg-[var(--surface)] px-2 py-1 text-[11px] font-bold text-[var(--ink)]"
              >
                Start A {loopStartMs !== null ? `(${formatMs(loopStartMs)})` : ''}
              </button>
              <button
                type="button"
                onClick={() => setLoopEndMs(positionMs)}
                className="rounded-lg border border-[var(--line)] bg-[var(--surface)] px-2 py-1 text-[11px] font-bold text-[var(--ink)]"
              >
                Ende B {loopEndMs !== null ? `(${formatMs(loopEndMs)})` : ''}
              </button>
              {(loopStartMs !== null || loopEndMs !== null) && (
                <button
                  type="button"
                  onClick={() => {
                    setLoopStartMs(null);
                    setLoopEndMs(null);
                  }}
                  className="inline-flex items-center gap-1 rounded-lg border border-[var(--line)] bg-[var(--surface)] px-2 py-1 text-[11px] font-bold text-[var(--ink2)]"
                >
                  <RotateCcw className="h-3 w-3" />
                  Loop aus
                </button>
              )}
              {loopStartMs !== null && loopEndMs !== null && loopEndMs > loopStartMs && (
                <span className="inline-flex items-center gap-1 rounded-lg bg-[var(--accent-soft)] px-2 py-1 text-[11px] font-bold text-[var(--accent-ink)]">
                  <Repeat className="h-3 w-3" />
                  A–B aktiv
                </span>
              )}
            </div>
          </div>

          {playbackError && (
            <p className="text-xs font-medium text-[var(--coral)]">{playbackError}</p>
          )}
        </div>
      )}
    </section>
  );
};
