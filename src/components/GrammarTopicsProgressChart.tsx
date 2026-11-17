import React, { useMemo } from 'react';
import { coreUnits, stages } from '../data/curriculumCatalogue';
import { useApp } from '../state/AppContext';

const STAGE_SHORT_DE: Record<number, string> = {
  1: 'B2-Festigung & Präzision',
  2: 'Komplexes Verstehen',
  3: 'Differenzierte Produktion',
  4: 'Pragmatik & Verhandlung',
  5: 'Integration & Mediation',
  6: 'Freier Transfer',
};

export const GrammarTopicsProgressChart: React.FC = () => {
  const { attempts, quizResults } = useApp();

  const stageRows = useMemo(() => {
    const practicedUnits = new Set(attempts.map((a) => a.unitId));

    return [1, 2, 3, 4, 5, 6].map((stageNum) => {
      const units = coreUnits.filter((u) => u.stage === stageNum);
      const practicedCount = units.filter((u) => practicedUnits.has(u.id)).length;

      let quizTotal = 0;
      let quizCorrect = 0;
      for (const [key, res] of Object.entries(quizResults)) {
        const unitId = key.split(':')[0];
        if (units.some((u) => u.id === unitId)) {
          quizTotal += 1;
          if (res.correct) quizCorrect += 1;
        }
      }

      return {
        stageNum,
        label: STAGE_SHORT_DE[stageNum] ?? stages[stageNum] ?? `Stufe ${stageNum}`,
        total: units.length,
        practicedCount,
        quizTotal,
        quizCorrect,
      };
    });
  }, [attempts, quizResults]);

  const totalPracticed = stageRows.reduce((sum, r) => sum + r.practicedCount, 0);

  return (
    <div className="flex flex-col justify-between rounded-3xl border border-[var(--line)] bg-[var(--surface)] p-5">
      <div>
        <div className="flex items-start justify-between gap-2">
          <div>
            <p className="text-[10px] font-bold uppercase tracking-wider text-[var(--accent-ink)]">
              Lehrplan-Abdeckung · Übungsstand
            </p>
            <h3 className="mt-0.5 text-sm font-extrabold text-[var(--ink)]">
              Stufen 1–6 ({totalPracticed} / {coreUnits.length} berührt)
            </h3>
          </div>
          <span className="text-[11px] font-bold tabular-nums text-[var(--ink2)]">
            {Object.keys(quizResults).length} Quizze
          </span>
        </div>

        <div className="mt-3 space-y-2.5">
          {stageRows.map((row) => {
            const pct = row.total > 0 ? Math.round((row.practicedCount / row.total) * 100) : 0;
            return (
              <div key={row.stageNum} className="space-y-1">
                <div className="flex items-center justify-between gap-2 text-[11px]">
                  <span className="truncate font-semibold text-[var(--ink)]">
                    Stufe {row.stageNum} · {row.label}
                  </span>
                  <span className="shrink-0 font-bold tabular-nums text-[var(--ink2)]">
                    {row.practicedCount}/{row.total}
                    {row.quizTotal > 0 ? ` · ${row.quizCorrect}/${row.quizTotal} Quiz` : ''}
                  </span>
                </div>
                <div className="h-2 overflow-hidden rounded-full bg-[var(--surface2)]">
                  <div
                    className="h-full rounded-full bg-[var(--accent)] transition-all duration-300"
                    style={{ width: `${pct}%` }}
                  />
                </div>
              </div>
            );
          })}
        </div>
      </div>

      <p className="mt-3 border-t border-[var(--line)] pt-2 text-[10px] text-[var(--ink2)]">
        Zeigt berührte Einheiten und Quiz-Treffer pro Stufe; keine Grammatiknote.
      </p>
    </div>
  );
};
