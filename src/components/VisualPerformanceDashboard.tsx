import React, { useMemo } from 'react';
import { MasteryEngine } from '../engines/masteryEngine';
import { useApp } from '../state/AppContext';

export const VisualPerformanceDashboard: React.FC = () => {
  const { attempts, srsVocabulary, dailyStreak } = useApp();

  const metrics = useMemo(() => {
    const now = Date.now();
    const todayStart = new Date(now);
    todayStart.setHours(0, 0, 0, 0);
    const todayMs = todayStart.getTime();

    const days = Array.from({ length: 7 }, (_, i) => {
      const offset = 6 - i;
      const dayStart = todayMs - offset * MasteryEngine.DAY_MILLIS;
      const dayEnd = dayStart + MasteryEngine.DAY_MILLIS;
      const date = new Date(dayStart);
      const label = date.toLocaleDateString('de-DE', { weekday: 'short' }).slice(0, 2);
      const count = attempts.filter(
        (a) => a.timestamp >= dayStart && a.timestamp < dayEnd,
      ).length;
      return { key: dayStart, label, count, isToday: offset === 0 };
    });

    const maxDay = Math.max(1, ...days.map((d) => d.count));
    const unassistedCount = attempts.filter(
      (a) => a.independentProduction && !a.assisted,
    ).length;
    const assistedCount = attempts.filter((a) => a.assisted).length;
    const masteredVocab = srsVocabulary.filter((v) => v.isMastered).length;
    const dueVocab = srsVocabulary.filter((v) => v.nextReviewTimestamp <= now).length;

    return {
      days,
      maxDay,
      unassistedCount,
      assistedCount,
      masteredVocab,
      dueVocab,
    };
  }, [attempts, srsVocabulary]);

  return (
    <div className="flex flex-col justify-between rounded-3xl border border-[var(--line)] bg-[var(--surface)] p-5">
      <div>
        <div className="flex items-start justify-between gap-2">
          <div>
            <p className="text-[10px] font-bold uppercase tracking-wider text-[var(--accent-ink)]">
              Übungsaktivität · keine Kompetenznote
            </p>
            <h3 className="mt-0.5 text-sm font-extrabold text-[var(--ink)]">
              7-Tage-Übungspuls & Abruf
            </h3>
          </div>
          <span className="text-[11px] font-bold tabular-nums text-[var(--ink2)]">
            {dailyStreak.currentStreak}d Serie
          </span>
        </div>

        {/* 7-day SVG Bar Chart */}
        <svg
          viewBox="0 0 260 98"
          className="mt-3 w-full"
          role="img"
          aria-label="Lernversuche der letzten 7 Tage"
        >
          <line
            x1="8"
            y1="74"
            x2="252"
            y2="74"
            stroke="var(--line)"
            strokeWidth="1"
          />
          {metrics.days.map((day, i) => {
            const x = 14 + i * 34;
            const barMaxH = 54;
            const barH = day.count > 0 ? Math.max(8, (day.count / metrics.maxDay) * barMaxH) : 4;
            const y = 74 - barH;
            return (
              <g key={day.key}>
                <rect
                  x={x}
                  y={y}
                  width="20"
                  height={barH}
                  rx="4"
                  fill={
                    day.count > 0
                      ? day.isToday
                        ? 'var(--accent)'
                        : 'var(--violet)'
                      : 'var(--surface2)'
                  }
                />
                {day.count > 0 && (
                  <text
                    x={x + 10}
                    y={Math.max(10, y - 4)}
                    textAnchor="middle"
                    fill="var(--ink)"
                    fontSize="9"
                    fontWeight="700"
                  >
                    {day.count}
                  </text>
                )}
                <text
                  x={x + 10}
                  y="90"
                  textAnchor="middle"
                  fill={day.isToday ? 'var(--accent-ink)' : 'var(--ink2)'}
                  fontSize="9.5"
                  fontWeight={day.isToday ? '800' : '600'}
                >
                  {day.label}
                </text>
              </g>
            );
          })}
        </svg>

        <div className="mt-3 grid grid-cols-2 gap-2 text-xs">
          <div className="rounded-xl border border-[var(--line)] bg-[var(--surface2)] px-3 py-2">
            <p className="text-[10px] font-bold text-[var(--ink2)]">Eigenständig vs. Hilfe</p>
            <p className="mt-0.5 font-extrabold tabular-nums text-[var(--ink)]">
              {metrics.unassistedCount} frei · {metrics.assistedCount} mit Hilfe
            </p>
          </div>
          <div className="rounded-xl border border-[var(--line)] bg-[var(--surface2)] px-3 py-2">
            <p className="text-[10px] font-bold text-[var(--ink2)]">Wortschatz-Abruf</p>
            <p className="mt-0.5 font-extrabold tabular-nums text-[var(--ink)]">
              {metrics.masteredVocab}/{srsVocabulary.length} fest · {metrics.dueVocab} fällig
            </p>
          </div>
        </div>
      </div>

      <p className="mt-3 border-t border-[var(--line)] pt-2 text-[10px] text-[var(--ink2)]">
        Misst ausschließlich gespeicherte Übungshäufigkeit, nicht Sprachniveau.
      </p>
    </div>
  );
};
