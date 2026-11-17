import React from 'react';
import type { SkillCategory, SkillScore } from '../types';

interface BerlinRadarChartProps {
  skillScores: Record<SkillCategory, SkillScore>;
  size?: number;
}

const AXES: { category: SkillCategory; label: string }[] = [
  { category: 'HOERVERSTEHEN', label: 'HÖREN' },
  { category: 'SPONTANES_SPRECHEN', label: 'SPRECHEN' },
  { category: 'INTERAKTION', label: 'DIALOG' },
  { category: 'LESEVERSTEHEN', label: 'LESEN' },
  { category: 'SCHREIBEN', label: 'SCHREIBEN' },
  { category: 'GRAMMATIK', label: 'SYNTAX' },
  { category: 'WORTSCHATZ', label: 'LEXIK' },
  { category: 'MEDIATION', label: 'MEDIATION' },
];

export const BerlinRadarChart: React.FC<BerlinRadarChartProps> = ({
  skillScores,
  size = 250,
}) => {
  const cx = size / 2;
  const cy = size / 2;
  const maxR = size * 0.34;
  const ringFractions = [0.25, 0.5, 0.75, 1];
  const n = AXES.length;

  const angleFor = (idx: number) => (idx * 2 * Math.PI) / n - Math.PI / 2;

  const pointsForFraction = (fraction: number) =>
    AXES.map((_, idx) => {
      const a = angleFor(idx);
      const r = maxR * fraction;
      return `${(cx + r * Math.cos(a)).toFixed(1)},${(cy + r * Math.sin(a)).toFixed(1)}`;
    }).join(' ');

  const scorePoints = AXES.map((axis, idx) => {
    const score = Math.max(0, Math.min(100, skillScores[axis.category]?.score ?? 0));
    const a = angleFor(idx);
    const r = (maxR * Math.max(4, score)) / 100;
    return {
      x: cx + r * Math.cos(a),
      y: cy + r * Math.sin(a),
      score,
    };
  });

  const dataPolygon = scorePoints
    .map((p) => `${p.x.toFixed(1)},${p.y.toFixed(1)}`)
    .join(' ');

  return (
    <svg
      width={size}
      height={size}
      viewBox={`0 0 ${size} ${size}`}
      className="select-none overflow-visible"
      role="img"
      aria-label="8-Achsen Kompetenz-Radar"
    >
      <defs>
        <radialGradient id="berlinRadarFill" cx="50%" cy="50%" r="55%">
          <stop offset="0%" stopColor="var(--accent)" stopOpacity="0.42" />
          <stop offset="100%" stopColor="var(--sky)" stopOpacity="0.16" />
        </radialGradient>
      </defs>

      {ringFractions.map((frac) => (
        <polygon
          key={frac}
          points={pointsForFraction(frac)}
          fill="none"
          stroke="var(--line)"
          strokeWidth={frac === 1 ? 1.4 : 0.9}
          strokeDasharray={frac < 1 ? '3 3' : undefined}
          opacity={0.75}
        />
      ))}

      {AXES.map((axis, idx) => {
        const a = angleFor(idx);
        const xEnd = cx + maxR * Math.cos(a);
        const yEnd = cy + maxR * Math.sin(a);
        const labelR = maxR + 22;
        const lx = cx + labelR * Math.cos(a);
        const ly = cy + labelR * Math.sin(a);
        return (
          <g key={axis.category}>
            <line
              x1={cx}
              y1={cy}
              x2={xEnd}
              y2={yEnd}
              stroke="var(--line)"
              strokeWidth={1}
              opacity={0.7}
            />
            <text
              x={lx}
              y={ly}
              textAnchor="middle"
              dominantBaseline="central"
              fill="var(--on-hero2)"
              fontSize="9"
              fontWeight="700"
              letterSpacing="0.06em"
            >
              {axis.label}
            </text>
          </g>
        );
      })}

      <polygon
        points={dataPolygon}
        fill="url(#berlinRadarFill)"
        stroke="var(--accent)"
        strokeWidth={2}
      />

      {scorePoints.map((pt, idx) => (
        <circle
          key={AXES[idx].category}
          cx={pt.x}
          cy={pt.y}
          r={pt.score > 0 ? 3.5 : 2.5}
          fill="var(--accent)"
          stroke="var(--hero)"
          strokeWidth={1.5}
        />
      ))}
    </svg>
  );
};
