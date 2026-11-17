import React, { useMemo } from 'react';
import { MasteryEngine } from '../engines/masteryEngine';
import { useApp } from '../state/AppContext';
import type { Dimension } from '../types';

const DIMENSION_SHORT_DE: Record<Dimension, string> = {
  ACCURACY: 'Korrektheit',
  FLUENCY: 'Flüssigkeit',
  RANGE: 'Ausdrucksspektrum',
  NATURALNESS: 'Natürlichkeit',
  COHERENCE: 'Kohärenz',
  INTERACTION: 'Interaktion',
  PRAGMATICS: 'Pragmatik',
  REGISTER: 'Register',
  COMPREHENSION: 'Verstehen',
  MEDIATION: 'Mediation',
};

export const C1ProficiencyProgressChart: React.FC = () => {
  const { humanReviews } = useApp();

  const stats = useMemo(() => {
    const totals = new Map<Dimension, { sum: number; count: number }>();
    for (const dim of MasteryEngine.ALL_DIMENSIONS) {
      totals.set(dim, { sum: 0, count: 0 });
    }
    for (const rev of humanReviews) {
      for (const dim of MasteryEngine.ALL_DIMENSIONS) {
        const val = rev.scores[dim];
        if (typeof val === 'number' && Number.isFinite(val)) {
          const entry = totals.get(dim)!;
          entry.sum += val;
          entry.count += 1;
        }
      }
    }
    return MasteryEngine.ALL_DIMENSIONS.map((dim) => {
      const entry = totals.get(dim)!;
      const avg = entry.count > 0 ? entry.sum / entry.count : null;
      return {
        dim,
        label: DIMENSION_SHORT_DE[dim],
        avg,
        count: entry.count,
      };
    });
  }, [humanReviews]);

  const measuredCount = stats.filter((s) => s.avg !== null).length;

  return (
    <div className="flex flex-col justify-between rounded-3xl border border-[var(--line)] bg-[var(--surface)] p-5">
      <div>
        <div className="flex items-start justify-between gap-2">
          <div>
            <p className="text-[10px] font-bold uppercase tracking-wider text-[var(--accent-ink)]">
              Rubrik-Profil · 0–4 Skala
            </p>
            <h3 className="mt-0.5 text-sm font-extrabold text-[var(--ink)]">
              {humanReviews.length > 0
                ? `Kompetenznachweis (${humanReviews.length} Bewertung${humanReviews.length === 1 ? '' : 'en'})`
                : 'Kompetenzverlauf · nicht gemessen'}
            </h3>
          </div>
          <span className="text-[11px] font-bold tabular-nums text-[var(--ink2)]">
            {measuredCount} / {MasteryEngine.ALL_DIMENSIONS.length}
          </span>
        </div>

        {humanReviews.length === 0 ? (
          <div className="mt-3 space-y-2.5">
            <svg
              viewBox="0 0 260 112"
              className="w-full rounded-2xl border border-dashed border-[var(--line)] bg-[var(--surface2)]/60 p-2"
              aria-hidden="true"
            >
              {/* Threshold line at 3.0 / 4.0 (75% width) */}
              <line
                x1="185"
                y1="10"
                x2="185"
                y2="100"
                stroke="var(--accent)"
                strokeDasharray="4 3"
                strokeWidth="1.5"
              />
              <text
                x="189"
                y="20"
                fill="var(--accent-ink)"
                fontSize="8.5"
                fontWeight="700"
              >
                Ziel ≥ 3,0
              </text>
              {[0, 1, 2, 3, 4].map((row) => (
                <g key={row} transform={`translate(10, ${18 + row * 17})`}>
                  <rect
                    x="0"
                    y="0"
                    width="64"
                    height="7"
                    rx="3.5"
                    fill="var(--line)"
                    opacity="0.55"
                  />
                  <rect
                    x="74"
                    y="0"
                    width="156"
                    height="7"
                    rx="3.5"
                    fill="var(--surface)"
                    stroke="var(--line)"
                    strokeWidth="0.75"
                  />
                </g>
              ))}
            </svg>
            <p className="text-[11px] leading-relaxed text-[var(--ink2)]">
              Noch keine menschliche Rubrikprüfung (HUMAN_REVIEW) vorhanden. Erst erfasste
              Prüferwertungen (0–4, Ziel ≥ 3,0) füllen diese 10 Dimensionen ohne Schätzwerte.
            </p>
          </div>
        ) : (
          <div className="mt-3">
            <svg
              viewBox="0 0 280 214"
              className="w-full"
              role="img"
              aria-label="Durchschnittswerte der 10 C1-Rubrikdimensionen"
            >
              {/* 3.0 / 4.0 C1 target guide line */}
              {(() => {
                const trackX = 96;
                const trackW = 144;
                const targetX = trackX + trackW * 0.75;
                return (
                  <>
                    <line
                      x1={targetX}
                      y1="4"
                      x2={targetX}
                      y2="206"
                      stroke="var(--accent)"
                      strokeDasharray="4 3"
                      strokeWidth="1.2"
                      opacity="0.85"
                    />
                    {stats.map((row, idx) => {
                      const y = 8 + idx * 20;
                      const pct = row.avg !== null ? Math.min(1, Math.max(0, row.avg / 4)) : 0;
                      const meetsC1 = row.avg !== null && row.avg >= 3;
                      return (
                        <g key={row.dim} transform={`translate(0, ${y})`}>
                          <text
                            x="0"
                            y="10"
                            fill="var(--ink2)"
                            fontSize="9.5"
                            fontWeight="600"
                          >
                            {row.label}
                          </text>
                          <rect
                            x={trackX}
                            y="2"
                            width={trackW}
                            height="10"
                            rx="5"
                            fill="var(--surface2)"
                          />
                          {pct > 0 && (
                            <rect
                              x={trackX}
                              y="2"
                              width={Math.max(4, trackW * pct)}
                              height="10"
                              rx="5"
                              fill={meetsC1 ? 'var(--ok)' : 'var(--accent)'}
                            />
                          )}
                          <text
                            x="278"
                            y="10"
                            textAnchor="end"
                            fill={meetsC1 ? 'var(--ok)' : 'var(--ink)'}
                            fontSize="9.5"
                            fontWeight="700"
                          >
                            {row.avg !== null ? row.avg.toFixed(1) : '–'}
                          </text>
                        </g>
                      );
                    })}
                  </>
                );
              })()}
            </svg>
          </div>
        )}
      </div>

      <p className="mt-3 border-t border-[var(--line)] pt-2 text-[10px] text-[var(--ink2)]">
        Freigabeschwelle: alle 10 Dimensionen ≥ 3,0 · Quelle: ausschließlich HUMAN_REVIEW
      </p>
    </div>
  );
};
