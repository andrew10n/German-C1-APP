import React, { useState } from 'react';
import {
  AlertTriangle,
  Download,
  ShieldCheck,
  Trash2,
  X,
} from 'lucide-react';
import { RealLifeRepairEngine } from '../engines/realLifeRepairEngine';
import { REAL_LIFE_ISSUE_LABELS } from '../engines/weaknessEngine';
import { useApp } from '../state/AppContext';
import type {
  ContextTrack,
  Dimension,
  FeedbackChallengeDecision,
  FeedbackClaim,
  ProgrammeLanguage,
  ProgrammeTheme,
  RealLifeExperience,
  RealLifeIssueType,
} from '../types';

const DIMENSIONS: Dimension[] = [
  'ACCURACY',
  'FLUENCY',
  'RANGE',
  'NATURALNESS',
  'COHERENCE',
  'INTERACTION',
  'PRAGMATICS',
  'REGISTER',
  'COMPREHENSION',
  'MEDIATION',
];

const CONTEXTS: ContextTrack[] = ['GENERAL', 'SOCIAL', 'ENGINEERING', 'COACHING'];

const ISSUE_TYPES: RealLifeIssueType[] = [
  'LISTENING',
  'RETRIEVAL',
  'GRAMMAR_UNDER_PRESSURE',
  'NATURALNESS',
  'REGISTER',
  'REPAIR',
];

export const OnboardingModal: React.FC<{ onClose: () => void }> = ({ onClose }) => {
  const { completeOnboarding } = useApp();
  return (
    <div
      className="fixed inset-0 z-50 flex items-center justify-center bg-black/65 p-4 backdrop-blur-xs"
      role="dialog"
      aria-modal="true"
    >
      <div className="w-full max-w-lg rounded-3xl border border-[var(--line)] bg-[var(--surface)] p-6 text-[var(--ink)] shadow-2xl">
        <div className="flex items-start justify-between gap-2">
          <div className="flex items-center gap-2 text-[var(--accent-ink)]">
            <ShieldCheck className="h-5 w-5" />
            <span className="text-xs font-extrabold uppercase tracking-wider">
              Evidenz- & Ehrlichkeitshinweis
            </span>
          </div>
          <button
            type="button"
            onClick={onClose}
            className="rounded-xl p-1 text-[var(--ink2)] hover:bg-[var(--surface2)]"
          >
            <X className="h-5 w-5" />
          </button>
        </div>

        <h2 className="mt-2 font-display text-2xl font-extrabold">
          Willkommen im C1-Deutsch-Programm
        </h2>

        <div className="mt-3 space-y-2.5 text-xs leading-relaxed text-[var(--ink2)]">
          <p>
            <strong className="text-[var(--ink)]">1. Keine künstlichen Punkte:</strong>{' '}
            Lesen, synthetische TTS-Audio-Übungen und eigene Notizen sind Trainingsimpulse.
            Sie schalten Einheiten freiwillig zum Üben offen, behaupten aber niemals C1-Reife.
          </p>
          <p>
            <strong className="text-[var(--ink)]">2. Strenge Beherrschungsregel:</strong>{' '}
            Eine Einheit gilt erst nach unassistierter, eigenständiger Produktion über
            mindestens zwei Kontexte, zwei UTC-Tage und eine Transferaufgabe nach ≥ 7 Tagen
            mit lokaler Rubrikwertung (HUMAN_REVIEW ≥ 3/4 in allen 10 Dimensionen) als
            nachgewiesen.
          </p>
          <p>
            <strong className="text-[var(--ink)]">3. Lokal & Privat:</strong> Alle
            Notizen, Versuche und Wortschatzkarten bleiben im Browser (localStorage). Du
            kannst sie jederzeit als JSON exportieren oder vollständig löschen.
          </p>
        </div>

        <div className="mt-5 flex justify-end">
          <button
            type="button"
            onClick={() => {
              completeOnboarding();
              onClose();
            }}
            className="rounded-2xl bg-[var(--accent)] px-5 py-2.5 text-xs font-extrabold text-[var(--on-accent)]"
          >
            Verstanden & starten
          </button>
        </div>
      </div>
    </div>
  );
};

export const PreferencesModal: React.FC<{ onClose: () => void }> = ({ onClose }) => {
  const {
    preferences,
    updatePreferences,
    exportLocalJson,
    deleteAllLocalData,
  } = useApp();
  const [confirmDelete, setConfirmDelete] = useState(false);

  return (
    <div
      className="fixed inset-0 z-50 flex items-center justify-center bg-black/65 p-4 backdrop-blur-xs"
      role="dialog"
      aria-modal="true"
    >
      <div className="max-h-[90vh] w-full max-w-lg overflow-y-auto rounded-3xl border border-[var(--line)] bg-[var(--surface)] p-6 text-[var(--ink)] shadow-2xl">
        <div className="flex items-center justify-between">
          <h2 className="font-display text-2xl font-extrabold">Einstellungen & Daten</h2>
          <button
            type="button"
            onClick={onClose}
            className="rounded-xl p-1 text-[var(--ink2)] hover:bg-[var(--surface2)]"
          >
            <X className="h-5 w-5" />
          </button>
        </div>

        <div className="mt-4 space-y-4 text-xs">
          <div>
            <label className="block font-bold text-[var(--ink2)]">Lernname (lokal)</label>
            <input
              type="text"
              value={preferences.learnerName}
              onChange={(e) => updatePreferences({ learnerName: e.target.value })}
              placeholder="Optionaler Anzeigename"
              className="mt-1 w-full rounded-xl border border-[var(--line)] bg-[var(--bg)] px-3 py-2 text-sm text-[var(--ink)]"
            />
          </div>

          <div>
            <label className="block font-bold text-[var(--ink2)]">Farbpalette</label>
            <div className="mt-1.5 grid grid-cols-3 gap-2">
              {(
                [
                  { id: 'TRACKSIDE', label: 'Trackside' },
                  { id: 'SILVER', label: 'Silver' },
                  { id: 'PITCH', label: 'Pitch' },
                ] as { id: ProgrammeTheme; label: string }[]
              ).map((p) => (
                <button
                  key={p.id}
                  type="button"
                  onClick={() => updatePreferences({ theme: p.id })}
                  className={`rounded-xl border py-2 font-bold ${
                    preferences.theme === p.id
                      ? 'border-[var(--accent)] bg-[var(--accent-soft)] text-[var(--accent-ink)]'
                      : 'border-[var(--line)] bg-[var(--surface2)] text-[var(--ink2)]'
                  }`}
                >
                  {p.label}
                </button>
              ))}
            </div>
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block font-bold text-[var(--ink2)]">
                Hilfssprache (Hinweise)
              </label>
              <div className="mt-1.5 grid grid-cols-2 gap-1.5">
                {(['DE', 'EN'] as ProgrammeLanguage[]).map((lang) => (
                  <button
                    key={lang}
                    type="button"
                    onClick={() => updatePreferences({ language: lang })}
                    className={`rounded-xl border py-2 font-bold ${
                      preferences.language === lang
                        ? 'border-[var(--accent)] bg-[var(--accent-soft)] text-[var(--accent-ink)]'
                        : 'border-[var(--line)] bg-[var(--surface2)] text-[var(--ink2)]'
                    }`}
                  >
                    {lang}
                  </button>
                ))}
              </div>
            </div>

            <div>
              <label className="block font-bold text-[var(--ink2)]">
                Tagesziel (Minuten)
              </label>
              <div className="mt-1.5 flex flex-wrap gap-1.5">
                {[10, 15, 20, 30, 45].map((m) => (
                  <button
                    key={m}
                    type="button"
                    onClick={() => updatePreferences({ dailyMinutes: m })}
                    className={`rounded-xl border px-2.5 py-2 font-bold tabular-nums ${
                      preferences.dailyMinutes === m
                        ? 'border-[var(--accent)] bg-[var(--accent-soft)] text-[var(--accent-ink)]'
                        : 'border-[var(--line)] bg-[var(--surface2)] text-[var(--ink2)]'
                    }`}
                  >
                    {m}
                  </button>
                ))}
              </div>
            </div>
          </div>

          <label className="flex items-center gap-2 rounded-2xl border border-[var(--line)] bg-[var(--surface2)] p-3 font-semibold text-[var(--ink)]">
            <input
              type="checkbox"
              checked={preferences.compact}
              onChange={(e) => updatePreferences({ compact: e.target.checked })}
              className="rounded border-[var(--line)] accent-[var(--accent)]"
            />
            Kompakte Kartenansicht aktivieren
          </label>

          {/* Local Data Export & Deletion */}
          <div className="space-y-2 border-t border-[var(--line)] pt-4">
            <p className="font-bold text-[var(--ink)]">Datensouveränität</p>
            <button
              type="button"
              onClick={exportLocalJson}
              className="flex w-full items-center justify-center gap-2 rounded-2xl border border-[var(--accent)] bg-[var(--accent-soft)] py-2.5 font-extrabold text-[var(--accent-ink)]"
            >
              <Download className="h-4 w-4" />
              Alle Lerndaten als JSON exportieren
            </button>

            {!confirmDelete ? (
              <button
                type="button"
                onClick={() => setConfirmDelete(true)}
                className="flex w-full items-center justify-center gap-2 rounded-2xl border border-[var(--coral)]/40 bg-[var(--coral-soft)] py-2.5 font-bold text-[var(--coral)]"
              >
                <Trash2 className="h-4 w-4" />
                Alle lokalen Lerndaten löschen
              </button>
            ) : (
              <div className="space-y-2 rounded-2xl border border-[var(--coral)] bg-[var(--coral-soft)] p-3">
                <p className="flex items-center gap-1.5 font-bold text-[var(--coral)]">
                  <AlertTriangle className="h-4 w-4" />
                  Wirklich alle lokalen Versuche, Notizen und SRS-Karten zurücksetzen?
                </p>
                <div className="flex justify-end gap-2">
                  <button
                    type="button"
                    onClick={() => setConfirmDelete(false)}
                    className="rounded-xl bg-[var(--surface)] px-3 py-1.5 font-bold text-[var(--ink)]"
                  >
                    Abbrechen
                  </button>
                  <button
                    type="button"
                    onClick={() => {
                      deleteAllLocalData();
                      setConfirmDelete(false);
                      onClose();
                    }}
                    className="rounded-xl bg-[var(--coral)] px-3 py-1.5 font-bold text-white"
                  >
                    Endgültig löschen
                  </button>
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export const RealLifeAddModal: React.FC<{
  defaultContext: ContextTrack;
  onClose: () => void;
}> = ({ defaultContext, onClose }) => {
  const { addRealLifeExperience } = useApp();
  const [context, setContext] = useState<ContextTrack>(defaultContext);
  const [issueType, setIssueType] = useState<RealLifeIssueType>('REPAIR');
  const [situation, setSituation] = useState('');
  const [heard, setHeard] = useState('');
  const [intended, setIntended] = useState('');
  const [said, setSaid] = useState('');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!situation.trim() || !intended.trim()) return;
    addRealLifeExperience({ context, issueType, situation, heard, intended, said });
    onClose();
  };

  return (
    <div
      className="fixed inset-0 z-50 flex items-center justify-center bg-black/65 p-4 backdrop-blur-xs"
      role="dialog"
      aria-modal="true"
    >
      <form
        onSubmit={handleSubmit}
        className="max-h-[90vh] w-full max-w-lg space-y-3 overflow-y-auto rounded-3xl border border-[var(--line)] bg-[var(--surface)] p-6 text-xs text-[var(--ink)] shadow-2xl"
      >
        <div className="flex items-center justify-between">
          <h2 className="font-display text-2xl font-extrabold">
            Echte Alltagssituation erfassen
          </h2>
          <button
            type="button"
            onClick={onClose}
            className="rounded-xl p-1 text-[var(--ink2)]"
          >
            <X className="h-5 w-5" />
          </button>
        </div>

        <div className="grid grid-cols-2 gap-2">
          <div>
            <label className="block font-bold text-[var(--ink2)]">Kontext</label>
            <select
              value={context}
              onChange={(e) => setContext(e.target.value as ContextTrack)}
              className="mt-1 w-full rounded-xl border border-[var(--line)] bg-[var(--bg)] px-3 py-2 text-[var(--ink)]"
            >
              {CONTEXTS.map((c) => (
                <option key={c} value={c}>
                  {c}
                </option>
              ))}
            </select>
          </div>
          <div>
            <label className="block font-bold text-[var(--ink2)]">Schwierigkeitstyp</label>
            <select
              value={issueType}
              onChange={(e) => setIssueType(e.target.value as RealLifeIssueType)}
              className="mt-1 w-full rounded-xl border border-[var(--line)] bg-[var(--bg)] px-3 py-2 text-[var(--ink)]"
            >
              {ISSUE_TYPES.map((t) => (
                <option key={t} value={t}>
                  {REAL_LIFE_ISSUE_LABELS[t]}
                </option>
              ))}
            </select>
          </div>
        </div>

        <div>
          <label className="block font-bold text-[var(--ink2)]">
            Situation (Wo / Mit wem?) *
          </label>
          <input
            type="text"
            required
            value={situation}
            onChange={(e) => setSituation(e.target.value)}
            placeholder="z. B. An der Supermarktkasse / Elternabend auf dem Sportplatz"
            className="mt-1 w-full rounded-xl border border-[var(--line)] bg-[var(--bg)] px-3 py-2 text-[var(--ink)]"
          />
        </div>

        <div>
          <label className="block font-bold text-[var(--ink2)]">Was hast du gehört?</label>
          <input
            type="text"
            value={heard}
            onChange={(e) => setHeard(e.target.value)}
            placeholder="Wortlaut oder Stichwort des Gegenübers"
            className="mt-1 w-full rounded-xl border border-[var(--line)] bg-[var(--bg)] px-3 py-2 text-[var(--ink)]"
          />
        </div>

        <div>
          <label className="block font-bold text-[var(--ink2)]">
            Was wolltest du ausdrücken? *
          </label>
          <input
            type="text"
            required
            value={intended}
            onChange={(e) => setIntended(e.target.value)}
            placeholder="Deine eigentliche Kommunikationsabsicht"
            className="mt-1 w-full rounded-xl border border-[var(--line)] bg-[var(--bg)] px-3 py-2 text-[var(--ink)]"
          />
        </div>

        <div>
          <label className="block font-bold text-[var(--ink2)]">
            Was hast du tatsächlich gesagt?
          </label>
          <input
            type="text"
            value={said}
            onChange={(e) => setSaid(e.target.value)}
            placeholder="Tatsächliche Formulierung oder Blockade"
            className="mt-1 w-full rounded-xl border border-[var(--line)] bg-[var(--bg)] px-3 py-2 text-[var(--ink)]"
          />
        </div>

        <div className="flex justify-end gap-2 pt-2">
          <button
            type="button"
            onClick={onClose}
            className="rounded-xl px-3 py-2 font-semibold text-[var(--ink2)]"
          >
            Abbrechen
          </button>
          <button
            type="submit"
            className="rounded-xl bg-[var(--accent)] px-4 py-2 font-extrabold text-[var(--on-accent)]"
          >
            Speichern & Reparatur starten
          </button>
        </div>
      </form>
    </div>
  );
};

export const RealLifeReviewModal: React.FC<{
  experience: RealLifeExperience;
  onClose: () => void;
}> = ({ experience, onClose }) => {
  const { completeRealLifeReview } = useApp();
  const repair = RealLifeRepairEngine.create(experience);
  const [response, setResponse] = useState(experience.reviewResponse || '');

  return (
    <div
      className="fixed inset-0 z-50 flex items-center justify-center bg-black/65 p-4 backdrop-blur-xs"
      role="dialog"
      aria-modal="true"
    >
      <div className="max-h-[90vh] w-full max-w-lg space-y-3 overflow-y-auto rounded-3xl border border-[var(--line)] bg-[var(--surface)] p-6 text-xs text-[var(--ink)] shadow-2xl">
        <div className="flex items-center justify-between">
          <h2 className="font-display text-2xl font-extrabold">
            Situations-Reparatur & Transfer
          </h2>
          <button
            type="button"
            onClick={onClose}
            className="rounded-xl p-1 text-[var(--ink2)]"
          >
            <X className="h-5 w-5" />
          </button>
        </div>

        <div className="rounded-2xl border border-[var(--line)] bg-[var(--surface2)] p-3.5">
          <p className="font-bold">Situation: {experience.situation}</p>
          {experience.said && (
            <p className="mt-1 text-[var(--ink2)]">Gesagt: „{experience.said}“</p>
          )}
          <p className="mt-1 text-[var(--ink2)]">Absicht: „{experience.intended}“</p>
        </div>

        <div className="space-y-2 rounded-2xl border border-[var(--accent)]/40 bg-[var(--accent-soft)] p-3.5">
          <p className="text-[11px] font-bold uppercase tracking-wider text-[var(--accent-ink)]">
            Vorgeschlagene Reparaturformulierung
          </p>
          <p className="text-sm font-bold text-[var(--ink)]">{repair.primaryPhrase}</p>
          <p className="text-[var(--ink2)]">{repair.explanation}</p>
          <ul className="list-disc space-y-0.5 pl-4 text-[var(--ink)]">
            {repair.alternatives.map((alt, i) => (
              <li key={i}>{alt}</li>
            ))}
          </ul>
        </div>

        <div className="space-y-1.5">
          <p className="font-bold text-[var(--ink)]">
            Transfer-Aufgabe: {repair.transferPrompt}
          </p>
          <textarea
            rows={4}
            value={response}
            onChange={(e) => setResponse(e.target.value)}
            placeholder="Formuliere deine eigene Antwort für diese Transfer-Situation..."
            className="w-full rounded-2xl border border-[var(--line)] bg-[var(--bg)] p-3 text-sm text-[var(--ink)]"
          />
        </div>

        <div className="flex justify-end gap-2">
          <button
            type="button"
            onClick={onClose}
            className="rounded-xl px-3 py-2 font-semibold text-[var(--ink2)]"
          >
            Schließen
          </button>
          <button
            type="button"
            disabled={!response.trim()}
            onClick={() => {
              completeRealLifeReview(experience.id, response);
              onClose();
            }}
            className="rounded-xl bg-[var(--accent)] px-4 py-2 font-extrabold text-[var(--on-accent)] disabled:opacity-45"
          >
            Reparatur speichern
          </button>
        </div>
      </div>
    </div>
  );
};

export const FeedbackChallengeModal: React.FC<{
  claim: FeedbackClaim;
  onClose: () => void;
}> = ({ claim, onClose }) => {
  const { createFeedbackChallenge } = useApp();
  const [decision, setDecision] = useState<FeedbackChallengeDecision>('DISAGREE');
  const [correction, setCorrection] = useState('');

  return (
    <div
      className="fixed inset-0 z-50 flex items-center justify-center bg-black/65 p-4 backdrop-blur-xs"
      role="dialog"
      aria-modal="true"
    >
      <div className="w-full max-w-md space-y-3 rounded-3xl border border-[var(--line)] bg-[var(--surface)] p-5 text-xs text-[var(--ink)] shadow-2xl">
        <div className="flex items-center justify-between">
          <h3 className="text-base font-extrabold">Rückmeldung prüfen / anfechten</h3>
          <button
            type="button"
            onClick={onClose}
            className="rounded-xl p-1 text-[var(--ink2)]"
          >
            <X className="h-5 w-5" />
          </button>
        </div>

        <div className="rounded-2xl border border-[var(--line)] bg-[var(--surface2)] p-3">
          <p className="font-bold text-[var(--ink2)]">Bezug im Text:</p>
          <p className="mt-0.5 italic">„{claim.responseExcerpt}“</p>
          <p className="mt-2 font-bold text-[var(--ink2)]">Automatischer Hinweis:</p>
          <p className="mt-0.5">{claim.proposedFeedback}</p>
        </div>

        <div className="grid grid-cols-2 gap-2">
          {(
            [
              { id: 'DISAGREE', label: 'Hinweis unzutreffend' },
              { id: 'UNSURE', label: 'Unklar / prüfen lassen' },
            ] as { id: FeedbackChallengeDecision; label: string }[]
          ).map((d) => (
            <button
              key={d.id}
              type="button"
              onClick={() => setDecision(d.id)}
              className={`rounded-xl border py-2 font-bold ${
                decision === d.id
                  ? 'border-[var(--accent)] bg-[var(--accent-soft)] text-[var(--accent-ink)]'
                  : 'border-[var(--line)] bg-[var(--surface2)] text-[var(--ink2)]'
              }`}
            >
              {d.label}
            </button>
          ))}
        </div>

        <div>
          <label className="block font-bold text-[var(--ink2)]">
            Begründung / eigene Korrektur (optional)
          </label>
          <textarea
            rows={3}
            value={correction}
            onChange={(e) => setCorrection(e.target.value)}
            placeholder="Warum passt die Formulierung im Kontext?"
            className="mt-1 w-full rounded-xl border border-[var(--line)] bg-[var(--bg)] p-2.5 text-[var(--ink)]"
          />
        </div>

        <div className="flex justify-end gap-2">
          <button
            type="button"
            onClick={onClose}
            className="rounded-xl px-3 py-2 font-semibold text-[var(--ink2)]"
          >
            Abbrechen
          </button>
          <button
            type="button"
            onClick={() => {
              createFeedbackChallenge(claim, decision, correction || null);
              onClose();
            }}
            className="rounded-xl bg-[var(--accent)] px-4 py-2 font-extrabold text-[var(--on-accent)]"
          >
            Einspruch speichern
          </button>
        </div>
      </div>
    </div>
  );
};

export const HumanReviewModal: React.FC<{
  attemptId: string;
  onClose: () => void;
}> = ({ attemptId, onClose }) => {
  const { submitHumanReview } = useApp();
  const [reviewerName, setReviewerName] = useState('');
  const [verified, setVerified] = useState(false);
  const [comment, setComment] = useState('');
  const [scores, setScores] = useState<Record<Dimension, number>>(() =>
    DIMENSIONS.reduce(
      (acc, dim) => {
        acc[dim] = 3;
        return acc;
      },
      {} as Record<Dimension, number>,
    ),
  );

  return (
    <div
      className="fixed inset-0 z-50 flex items-center justify-center bg-black/65 p-4 backdrop-blur-xs"
      role="dialog"
      aria-modal="true"
    >
      <div className="max-h-[90vh] w-full max-w-lg space-y-3 overflow-y-auto rounded-3xl border border-[var(--line)] bg-[var(--surface)] p-6 text-xs text-[var(--ink)] shadow-2xl">
        <div className="flex items-center justify-between">
          <h2 className="font-display text-2xl font-extrabold">
            Menschliche Rubrikprüfung (1–4)
          </h2>
          <button
            type="button"
            onClick={onClose}
            className="rounded-xl p-1 text-[var(--ink2)]"
          >
            <X className="h-5 w-5" />
          </button>
        </div>

        <p className="text-[var(--ink2)]">
          Eine gespeicherte Rubrikwertung setzt lokal die Quelle HUMAN_REVIEW (ohne
          externe Identitätsprüfung). Beherrschung verlangt mindestens 3/4 in allen 10
          Dimensionen über ≥ 2 Kontexte und ≥ 2 UTC-Tage (Transfer ≥ 7 Tage).
        </p>

        <input
          type="text"
          value={reviewerName}
          onChange={(e) => setReviewerName(e.target.value)}
          placeholder="Name / Kürzel der prüfenden Person"
          className="w-full rounded-xl border border-[var(--line)] bg-[var(--bg)] px-3 py-2 text-[var(--ink)]"
        />

        <label className="flex items-center gap-2 font-semibold">
          <input
            type="checkbox"
            checked={verified}
            onChange={(e) => setVerified(e.target.checked)}
            className="accent-[var(--accent)]"
          />
          Unabhängige Lehrkraft / Tandempartner bestätigt (lokale Angabe)
        </label>

        <div className="grid grid-cols-2 gap-2">
          {DIMENSIONS.map((dim) => (
            <div
              key={dim}
              className="flex items-center justify-between rounded-xl border border-[var(--line)] bg-[var(--surface2)] px-2.5 py-2"
            >
              <span className="text-[11px] font-bold">{dim}</span>
              <div className="flex gap-1">
                {[1, 2, 3, 4].map((val) => (
                  <button
                    key={val}
                    type="button"
                    onClick={() => setScores((prev) => ({ ...prev, [dim]: val }))}
                    className={`h-6 w-6 rounded-lg font-bold ${
                      scores[dim] === val
                        ? 'bg-[var(--accent)] text-[var(--on-accent)]'
                        : 'bg-[var(--surface)] text-[var(--ink2)]'
                    }`}
                  >
                    {val}
                  </button>
                ))}
              </div>
            </div>
          ))}
        </div>

        <textarea
          rows={3}
          value={comment}
          onChange={(e) => setComment(e.target.value)}
          placeholder="Konkrete Rückmeldung zu Register, Präzision, Pragmatik und Transfer..."
          className="w-full rounded-xl border border-[var(--line)] bg-[var(--bg)] p-2.5 text-[var(--ink)]"
        />

        <div className="flex justify-end gap-2">
          <button
            type="button"
            onClick={onClose}
            className="rounded-xl px-3 py-2 font-semibold text-[var(--ink2)]"
          >
            Abbrechen
          </button>
          <button
            type="button"
            onClick={() => {
              submitHumanReview({
                attemptId,
                reviewerName: reviewerName || 'Menschliche Prüfung',
                verified,
                scores,
                comment: comment || 'Rubrikwertung gespeichert.',
              });
              onClose();
            }}
            className="rounded-xl bg-[var(--accent)] px-4 py-2 font-extrabold text-[var(--on-accent)]"
          >
            Rubrikwertung speichern
          </button>
        </div>
      </div>
    </div>
  );
};
