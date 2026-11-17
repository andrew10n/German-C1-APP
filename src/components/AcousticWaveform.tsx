import React from 'react';

interface AcousticWaveformProps {
  active: boolean;
  variant?: 'accent' | 'coral' | 'sky' | 'violet';
  bars?: number;
  height?: number;
  label?: string;
}

const BAR_WEIGHTS = [
  0.42, 0.78, 0.56, 0.96, 0.65, 0.88, 0.48, 0.94, 0.72, 0.6,
  0.86, 0.45, 0.98, 0.62, 0.82, 0.52, 0.9, 0.68, 0.76, 0.44,
  0.84, 0.58, 0.92, 0.5,
];

const VARIANT_COLORS: Record<NonNullable<AcousticWaveformProps['variant']>, string> = {
  accent: 'var(--accent)',
  coral: 'var(--coral)',
  sky: 'var(--sky)',
  violet: 'var(--violet)',
};

export const AcousticWaveform: React.FC<AcousticWaveformProps> = ({
  active,
  variant = 'accent',
  bars = 20,
  height = 36,
  label,
}) => {
  const color = VARIANT_COLORS[variant];

  return (
    <div
      className="flex items-center gap-3 rounded-2xl border border-[var(--line)] bg-[var(--surface)] px-3.5 py-2"
      aria-label={label ?? (active ? 'Audio aktiv' : 'Audio bereit')}
    >
      <div
        className="flex flex-1 items-end justify-between gap-1"
        style={{ height: `${height}px` }}
      >
        {Array.from({ length: bars }).map((_, idx) => {
          const weight = BAR_WEIGHTS[idx % BAR_WEIGHTS.length];
          const duration = 560 + ((idx * 97) % 440);
          const delay = (idx * 55) % 480;
          return (
            <span
              key={idx}
              className={`c1-wave-bar block flex-1 rounded-full ${
                active ? 'c1-wave-bar-active opacity-100' : 'opacity-40'
              }`}
              style={
                {
                  height: `${Math.round(height * weight)}px`,
                  backgroundColor: color,
                  '--wave-dur': `${duration}ms`,
                  '--wave-delay': `${delay}ms`,
                } as React.CSSProperties
              }
            />
          );
        })}
      </div>
      {label && (
        <span
          className="shrink-0 text-[10px] font-extrabold uppercase tracking-wider tabular-nums"
          style={{ color }}
        >
          {label}
        </span>
      )}
    </div>
  );
};
