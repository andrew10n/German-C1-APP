import React, { useEffect, useState } from 'react';
import { ExternalLink, Headphones } from 'lucide-react';
import { externalListeningSources } from '../data/listeningResources';
import { useApp } from '../state/AppContext';
import { AiFeedbackShareButton } from './AiFeedbackShareButton';

export const ExternalListeningSourcesSection: React.FC = () => {
  const { externalListeningDrafts, upsertExternalListeningDraft } = useApp();
  const [selectedSourceId, setSelectedSourceId] = useState(
    externalListeningSources[0]?.id ?? 'easy-german-podcast',
  );
  const selectedSource =
    externalListeningSources.find((s) => s.id === selectedSourceId) ??
    externalListeningSources[0];

  const existingDraft = externalListeningDrafts.find(
    (d) => d.sourceId === selectedSource.id,
  );

  const [itemRef, setItemRef] = useState(existingDraft?.itemReference ?? '');
  const [firstPass, setFirstPass] = useState(existingDraft?.firstPass ?? '');
  const [secondPass, setSecondPass] = useState(existingDraft?.secondPass ?? '');
  const [comparisonOpened, setComparisonOpened] = useState(
    existingDraft?.comparisonOpened ?? false,
  );

  useEffect(() => {
    const found = externalListeningDrafts.find((d) => d.sourceId === selectedSource.id);
    setItemRef(found?.itemReference ?? '');
    setFirstPass(found?.firstPass ?? '');
    setSecondPass(found?.secondPass ?? '');
    setComparisonOpened(found?.comparisonOpened ?? false);
  }, [selectedSource.id, externalListeningDrafts]);

  const persistDraft = (
    nextRef = itemRef,
    nextFirst = firstPass,
    nextSecond = secondPass,
    nextCompare = comparisonOpened,
  ) => {
    upsertExternalListeningDraft({
      sourceId: selectedSource.id,
      itemReference: nextRef,
      firstPass: nextFirst,
      secondPass: nextSecond,
      comparisonOpened: nextCompare,
    });
  };

  return (
    <section className="rounded-3xl border border-[var(--line)] bg-[var(--surface)] p-5 shadow-sm">
      <div className="flex items-center gap-2 text-[var(--accent-ink)]">
        <Headphones className="h-4 w-4" />
        <p className="text-[11px] font-bold uppercase tracking-wider">
          Authentische externe Hörquellen
        </p>
      </div>
      <h3 className="mt-1 text-base font-bold text-[var(--ink)]">
        Frei zugängliches Original-Audio außerhalb der App
      </h3>
      <p className="mt-1 text-xs leading-relaxed text-[var(--ink2)]">
        Die App enthält keine gekauften Verlagstexte oder urheberrechtlich geschützten
        Audiodateien. Nutze diese öffentlichen Originalquellen für echtes Hörtraining im
        Zwei-Durchgangs-Protokoll.
      </p>

      <div className="mt-3 flex flex-wrap gap-2">
        {externalListeningSources.map((src) => {
          const active = src.id === selectedSource.id;
          return (
            <button
              key={src.id}
              type="button"
              onClick={() => setSelectedSourceId(src.id)}
              className={`rounded-xl border px-3 py-1.5 text-xs font-bold transition ${
                active
                  ? 'border-[var(--accent)] bg-[var(--accent-soft)] text-[var(--accent-ink)]'
                  : 'border-[var(--line)] bg-[var(--surface2)] text-[var(--ink2)] hover:text-[var(--ink)]'
              }`}
            >
              {src.title}
            </button>
          );
        })}
      </div>

      <div className="mt-4 rounded-2xl border border-[var(--line)] bg-[var(--surface2)] p-4">
        <div className="flex flex-wrap items-start justify-between gap-2">
          <div>
            <h4 className="text-sm font-bold text-[var(--ink)]">{selectedSource.title}</h4>
            <p className="text-xs font-semibold text-[var(--accent-ink)]">
              {selectedSource.formats}
            </p>
          </div>
          <a
            href={selectedSource.url}
            target="_blank"
            rel="noopener noreferrer"
            className="inline-flex items-center gap-1.5 rounded-xl bg-[var(--accent)] px-3 py-1.5 text-xs font-bold text-[var(--on-accent)]"
          >
            Beim Anbieter öffnen
            <ExternalLink className="h-3.5 w-3.5" />
          </a>
        </div>

        <p className="mt-2 text-xs text-[var(--ink)]">{selectedSource.focus}</p>
        <p className="mt-1 text-xs text-[var(--ink2)]">{selectedSource.transcriptNote}</p>

        <ul className="mt-3 space-y-1.5 border-t border-[var(--line)] pt-3 text-xs text-[var(--ink)]">
          {selectedSource.practiceTasks.map((task, idx) => (
            <li key={idx} className="flex gap-2">
              <span className="font-bold text-[var(--accent-ink)]">{idx + 1}.</span>
              <span>{task}</span>
            </li>
          ))}
        </ul>

        <div className="mt-4 space-y-3 border-t border-[var(--line)] pt-3">
          <div>
            <label className="block text-[11px] font-bold text-[var(--ink2)]">
              Folge / Beitrag (Titel oder URL-Notiz)
            </label>
            <input
              type="text"
              value={itemRef}
              onChange={(e) => {
                setItemRef(e.target.value);
                persistDraft(e.target.value, firstPass, secondPass, comparisonOpened);
              }}
              placeholder="z. B. Episode 709 · Min. 04:20–07:10"
              className="mt-1 w-full rounded-xl border border-[var(--line)] bg-[var(--surface)] px-3 py-2 text-xs text-[var(--ink)] focus:border-[var(--accent)] focus:outline-none"
            />
          </div>

          <div>
            <label className="block text-[11px] font-bold text-[var(--ink2)]">
              1. Hördurchgang (blind, ohne Transkript)
            </label>
            <textarea
              rows={3}
              value={firstPass}
              onChange={(e) => {
                setFirstPass(e.target.value);
                persistDraft(itemRef, e.target.value, secondPass, comparisonOpened);
              }}
              placeholder="Hauptaussage, Konfliktpunkt und unklare Stellen notieren..."
              className="mt-1 w-full rounded-xl border border-[var(--line)] bg-[var(--surface)] p-2.5 text-xs text-[var(--ink)] focus:border-[var(--accent)] focus:outline-none"
            />
          </div>

          <div>
            <label className="block text-[11px] font-bold text-[var(--ink2)]">
              2. Hördurchgang (Detailprüfung & fehlende Stellen)
            </label>
            <textarea
              rows={3}
              value={secondPass}
              onChange={(e) => {
                setSecondPass(e.target.value);
                persistDraft(itemRef, firstPass, e.target.value, comparisonOpened);
              }}
              placeholder="Belegte Beispiele, genaue Zahlen oder Sprecherabsicht ergänzen..."
              className="mt-1 w-full rounded-xl border border-[var(--line)] bg-[var(--surface)] p-2.5 text-xs text-[var(--ink)] focus:border-[var(--accent)] focus:outline-none"
            />
          </div>

          <label className="flex items-center gap-2 text-xs text-[var(--ink)]">
            <input
              type="checkbox"
              checked={comparisonOpened}
              onChange={(e) => {
                setComparisonOpened(e.target.checked);
                persistDraft(itemRef, firstPass, secondPass, e.target.checked);
              }}
              className="rounded border-[var(--line)] accent-[var(--accent)]"
            />
            Transkript / Untertitel beim Anbieter erst nach dem ersten Versuch abgeglichen
          </label>

          <div className="flex flex-wrap items-center justify-between gap-2 pt-1">
            <AiFeedbackShareButton
              task={`Externes Hörtraining (${selectedSource.title} · ${itemRef || 'ohne Episodenangabe'})`}
              response={`1. Durchgang:\n${firstPass}\n\n2. Durchgang:\n${secondPass}`}
              evidenceType="Hörprotokoll zu externer Originalquelle"
            />
            <button
              type="button"
              onClick={() => {
                setItemRef('');
                setFirstPass('');
                setSecondPass('');
                setComparisonOpened(false);
                persistDraft('', '', '', false);
              }}
              className="rounded-xl px-3 py-1.5 text-xs font-semibold text-[var(--ink2)] hover:text-[var(--coral)]"
            >
              Notizen leeren
            </button>
          </div>
        </div>
      </div>
    </section>
  );
};
