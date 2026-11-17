import React, { useState } from 'react';
import { Check, Copy, Download, ExternalLink, X } from 'lucide-react';
import { AiFeedbackPrompt } from '../engines/aiFeedbackPrompt';

interface AiFeedbackShareButtonProps {
  task: string;
  response: string;
  evidenceType?: string;
  className?: string;
}

export const AiFeedbackShareButton: React.FC<AiFeedbackShareButtonProps> = ({
  task,
  response,
  evidenceType = 'schriftlicher Übungstext',
  className = '',
}) => {
  const [open, setOpen] = useState(false);
  const [shareText, setShareText] = useState('');
  const [copied, setCopied] = useState(false);
  const [statusMessage, setStatusMessage] = useState<string | null>(null);

  const handleOpen = () => {
    setShareText(AiFeedbackPrompt.build(task, response, evidenceType));
    setCopied(false);
    setStatusMessage(null);
    setOpen(true);
  };

  const handleCopy = async () => {
    try {
      await navigator.clipboard.writeText(shareText);
      setCopied(true);
      setStatusMessage('Prompt in die Zwischenablage kopiert.');
    } catch {
      setStatusMessage('Zwischenablage blockiert – Text manuell aus dem Feld kopieren.');
    }
  };

  const handleNativeShare = async () => {
    if (typeof navigator !== 'undefined' && typeof navigator.share === 'function') {
      try {
        await navigator.share({
          title: 'C1 Deutsch · Externe KI-Rückmeldung',
          text: shareText,
        });
        setOpen(false);
        return;
      } catch {
        setStatusMessage('Teilen abgebrochen oder nicht verfügbar. Nutze „Kopieren“ oder „Als .txt laden“.');
      }
    } else {
      setStatusMessage('Web-Share wird hier nicht unterstützt. Nutze „Kopieren“ oder „Als .txt laden“.');
    }
  };

  const handleDownloadTxt = () => {
    const blob = new Blob([shareText], { type: 'text/plain;charset=utf-8' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `c1-feedback-prompt-${Date.now()}.txt`;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    setTimeout(() => URL.revokeObjectURL(url), 1000);
    setStatusMessage('Textdatei heruntergeladen.');
  };

  return (
    <>
      <button
        type="button"
        disabled={!response.trim()}
        onClick={handleOpen}
        className={`inline-flex items-center gap-1.5 rounded-xl border border-[var(--line)] bg-[var(--surface2)] px-3 py-2 text-xs font-semibold text-[var(--ink)] transition hover:border-[var(--accent)] disabled:cursor-not-allowed disabled:opacity-45 ${className}`}
      >
        <ExternalLink className="h-3.5 w-3.5" />
        Externe KI-Rückmeldung anfragen
      </button>

      {open && (
        <div
          className="fixed inset-0 z-50 flex items-center justify-center bg-black/65 p-4 backdrop-blur-xs"
          role="dialog"
          aria-modal="true"
        >
          <div className="w-full max-w-lg rounded-3xl border border-[var(--line)] bg-[var(--surface)] p-5 text-[var(--ink)] shadow-2xl">
            <div className="flex items-start justify-between gap-2">
              <h3 className="text-base font-bold">Antwort extern teilen?</h3>
              <button
                type="button"
                onClick={() => setOpen(false)}
                className="rounded-xl p-1 text-[var(--ink2)] hover:bg-[var(--surface2)]"
                aria-label="Schließen"
              >
                <X className="h-5 w-5" />
              </button>
            </div>

            <p className="mt-2 text-xs leading-relaxed text-[var(--ink2)]">
              Prüfe oder bearbeite den genauen Text unten. Erst nach deinem aktiven
              Kopieren oder Teilen wird er übergeben. Teile keine vertraulichen Daten.
            </p>

            <label className="mt-3 block text-xs font-semibold text-[var(--ink2)]">
              Zu teilender Text
            </label>
            <textarea
              value={shareText}
              onChange={(e) => setShareText(e.target.value)}
              rows={9}
              className="mt-1 w-full rounded-2xl border border-[var(--line)] bg-[var(--bg)] p-3 text-xs leading-relaxed text-[var(--ink)] focus:border-[var(--accent)] focus:outline-none"
            />

            {statusMessage && (
              <p className="mt-2 text-xs font-medium text-[var(--accent-ink)]">
                {statusMessage}
              </p>
            )}

            <div className="mt-4 flex flex-wrap items-center justify-end gap-2">
              <button
                type="button"
                onClick={() => setOpen(false)}
                className="rounded-xl px-3 py-2 text-xs font-semibold text-[var(--ink2)] hover:bg-[var(--surface2)]"
              >
                Abbrechen
              </button>
              <button
                type="button"
                disabled={!shareText.trim()}
                onClick={handleDownloadTxt}
                className="inline-flex items-center gap-1.5 rounded-xl border border-[var(--line)] bg-[var(--surface2)] px-3 py-2 text-xs font-semibold text-[var(--ink)] hover:border-[var(--accent)] disabled:opacity-45"
              >
                <Download className="h-3.5 w-3.5" />
                Als .txt
              </button>
              <button
                type="button"
                disabled={!shareText.trim()}
                onClick={handleCopy}
                className="inline-flex items-center gap-1.5 rounded-xl bg-[var(--accent)] px-3.5 py-2 text-xs font-bold text-[var(--on-accent)] disabled:opacity-45"
              >
                {copied ? <Check className="h-3.5 w-3.5" /> : <Copy className="h-3.5 w-3.5" />}
                {copied ? 'Kopiert' : 'Prompt kopieren'}
              </button>
              {typeof navigator !== 'undefined' && typeof navigator.share === 'function' && (
                <button
                  type="button"
                  disabled={!shareText.trim()}
                  onClick={handleNativeShare}
                  className="inline-flex items-center gap-1.5 rounded-xl border border-[var(--accent)] bg-[var(--accent-soft)] px-3 py-2 text-xs font-bold text-[var(--accent-ink)] disabled:opacity-45"
                >
                  Teilen
                </button>
              )}
            </div>
          </div>
        </div>
      )}
    </>
  );
};
