import React, { useState } from 'react';
import { Pause, Play, Radio } from 'lucide-react';
import { COACH_TERM_CATEGORY_LABELS } from '../data/learningData';
import type { CoachTermCategory } from '../types';

interface FootballTacticalPitchProps {
  activeCategory: CoachTermCategory | 'ALL';
}

interface PitchPlayConfig {
  title: string;
  principle: string;
  zoneRect?: { x: number; y: number; w: number; h: number; label: string };
  vectors: { d: string; color: string }[];
  attackers: { id: string; cx: number; cy: number; movedCx: number; movedCy: number }[];
  defenders: { id: string; cx: number; cy: number; movedCx: number; movedCy: number }[];
  ball: { x1: number; y1: number; x2: number; y2: number };
}

const PITCH_PLAYS: Record<CoachTermCategory | 'ALL', PitchPlayConfig> = {
  ALL: {
    title: 'DFB-Spielfeldzonen: Halbräume & Spielverlagerung',
    principle:
      'Gegner im Zentrum binden und den Ball über den Sechser gezielt in den offenen Halbraum verlagern.',
    zoneRect: { x: 165, y: 26, w: 90, h: 52, label: 'Ziel-Halbraum' },
    vectors: [
      { d: 'M 108 112 Q 152 104 185 88', color: '#FACC15' },
      { d: 'M 185 88 Q 214 58 242 52', color: '#2DD4BF' },
    ],
    attackers: [
      { id: '6', cx: 108, cy: 112, movedCx: 122, movedCy: 106 },
      { id: '8', cx: 175, cy: 96, movedCx: 190, movedCy: 84 },
      { id: '11', cx: 220, cy: 40, movedCx: 248, movedCy: 48 },
      { id: '9', cx: 244, cy: 96, movedCx: 265, movedCy: 92 },
    ],
    defenders: [
      { id: 'IV', cx: 225, cy: 86, movedCx: 234, movedCy: 78 },
      { id: 'AV', cx: 215, cy: 122, movedCx: 220, movedCy: 102 },
      { id: '6', cx: 185, cy: 112, movedCx: 192, movedCy: 96 },
    ],
    ball: { x1: 108, y1: 112, x2: 242, y2: 52 },
  },
  OFFENSIV: {
    title: 'Offensiv: Tiefenlauf & Klatschenlassen im dritten Drittel',
    principle:
      'Stürmer (9) startet in die Tiefe, bindet den IV und öffnet das Zuspiel auf den nachrückenden Achter.',
    zoneRect: { x: 220, y: 58, w: 68, h: 66, label: 'Tiefe / Box' },
    vectors: [
      { d: 'M 135 90 L 198 104', color: '#FACC15' },
      { d: 'M 198 104 L 182 72', color: '#2DD4BF' },
      { d: 'M 182 72 Q 230 62 272 78', color: '#38BDF8' },
    ],
    attackers: [
      { id: '6', cx: 135, cy: 90, movedCx: 146, movedCy: 88 },
      { id: '9', cx: 205, cy: 104, movedCx: 256, movedCy: 76 },
      { id: '10', cx: 172, cy: 76, movedCx: 194, movedCy: 68 },
      { id: '7', cx: 210, cy: 146, movedCx: 255, movedCy: 132 },
    ],
    defenders: [
      { id: 'IV1', cx: 228, cy: 98, movedCx: 248, movedCy: 88 },
      { id: 'IV2', cx: 232, cy: 62, movedCx: 246, movedCy: 56 },
    ],
    ball: { x1: 135, y1: 90, x2: 268, y2: 78 },
  },
  DEFENSIV: {
    title: 'Defensiv: Pressingfalle außen & Kompaktheit im Block',
    principle:
      'Ballnahen Außenverteidiger anlaufen, Passweg zurück zustellen („Deckungsschatten“) und kompakt verschieben.',
    zoneRect: { x: 155, y: 12, w: 96, h: 48, label: 'Pressingfalle' },
    vectors: [
      { d: 'M 176 62 L 204 34', color: '#FB7185' },
      { d: 'M 152 98 L 178 68', color: '#FACC15' },
      { d: 'M 136 122 L 154 96', color: '#FACC15' },
    ],
    attackers: [
      { id: 'AV', cx: 214, cy: 30, movedCx: 220, movedCy: 28 },
      { id: 'IV', cx: 248, cy: 72, movedCx: 252, movedCy: 74 },
      { id: '6', cx: 198, cy: 74, movedCx: 200, movedCy: 72 },
    ],
    defenders: [
      { id: '7', cx: 168, cy: 62, movedCx: 202, movedCy: 36 },
      { id: '9', cx: 185, cy: 84, movedCx: 214, movedCy: 66 },
      { id: '8', cx: 150, cy: 100, movedCx: 174, movedCy: 68 },
      { id: '6', cx: 130, cy: 124, movedCx: 150, movedCy: 94 },
    ],
    ball: { x1: 214, y1: 30, x2: 202, y2: 36 },
  },
  UMSCHALTEN: {
    title: 'Umschalten: Gegenpressing in den ersten 5 Sekunden',
    principle:
      'Nach Ballverlust sofort auf den ballführenden Gegner sprinten und vertikale Konterlinie sichern.',
    zoneRect: { x: 130, y: 54, w: 72, h: 72, label: 'Balljagd 5s' },
    vectors: [
      { d: 'M 150 118 L 170 92', color: '#FB7185' },
      { d: 'M 192 64 L 172 88', color: '#FB7185' },
      { d: 'M 170 88 L 245 78', color: '#2DD4BF' },
    ],
    attackers: [
      { id: '8', cx: 146, cy: 120, movedCx: 168, movedCy: 94 },
      { id: '10', cx: 194, cy: 60, movedCx: 174, movedCy: 84 },
      { id: '9', cx: 205, cy: 100, movedCx: 252, movedCy: 80 },
    ],
    defenders: [
      { id: '6', cx: 172, cy: 90, movedCx: 176, movedCy: 88 },
      { id: 'IV', cx: 218, cy: 88, movedCx: 228, movedCy: 92 },
    ],
    ball: { x1: 172, y1: 90, x2: 245, y2: 78 },
  },
  KOMMUNIKATION: {
    title: 'Coaching-Kommunikation: Kurze Kommandos mit Signalwirkung',
    principle:
      '„Vororientieren!“, „Klatschen!“, „Zeit!“ — maximal 2–3 Wörter vor der Ballannahme rufen.',
    zoneRect: { x: 118, y: 44, w: 90, h: 90, label: '360° Blickfeld' },
    vectors: [
      { d: 'M 72 90 L 146 90', color: '#2DD4BF' },
      { d: 'M 146 90 L 182 56', color: '#FACC15' },
      { d: 'M 146 90 L 194 120', color: '#38BDF8' },
    ],
    attackers: [
      { id: '4', cx: 72, cy: 90, movedCx: 82, movedCy: 90 },
      { id: '6', cx: 146, cy: 90, movedCx: 158, movedCy: 84 },
      { id: '8', cx: 182, cy: 56, movedCx: 204, movedCy: 50 },
      { id: '10', cx: 194, cy: 120, movedCx: 214, movedCy: 116 },
    ],
    defenders: [
      { id: 'G1', cx: 168, cy: 98, movedCx: 162, movedCy: 96 },
      { id: 'G2', cx: 198, cy: 80, movedCx: 194, movedCy: 78 },
    ],
    ball: { x1: 72, y1: 90, x2: 204, y2: 50 },
  },
};

export const FootballTacticalPitch: React.FC<FootballTacticalPitchProps> = ({
  activeCategory,
}) => {
  const [animatePhase, setAnimatePhase] = useState(true);
  const [phaseStep, setPhaseStep] = useState<0 | 1>(1);
  const play = PITCH_PLAYS[activeCategory] ?? PITCH_PLAYS.ALL;

  return (
    <section className="c1-animate-enter space-y-3 rounded-3xl border border-[var(--line)] bg-[var(--surface)] p-4 shadow-sm">
      <div className="flex flex-wrap items-center justify-between gap-2">
        <div>
          <p className="inline-flex items-center gap-1.5 text-[10px] font-extrabold uppercase tracking-wider text-[var(--ok)]">
            <Radio className="h-3.5 w-3.5" />
            Live-Taktiktafel ·{' '}
            {activeCategory === 'ALL'
              ? 'Alle Zonen'
              : COACH_TERM_CATEGORY_LABELS[activeCategory]}
          </p>
          <h2 className="text-sm font-extrabold text-[var(--ink)]">{play.title}</h2>
        </div>

        <div className="flex items-center gap-1.5">
          <button
            type="button"
            onClick={() => setPhaseStep((s) => (s === 0 ? 1 : 0))}
            className="rounded-xl border border-[var(--line)] bg-[var(--surface2)] px-3 py-1.5 text-[11px] font-bold text-[var(--ink)] hover:border-[var(--accent)]"
          >
            {phaseStep === 1 ? 'Ausgangsposition' : 'Spielzug auslösen'}
          </button>
          <button
            type="button"
            onClick={() => setAnimatePhase((a) => !a)}
            className="inline-flex items-center gap-1 rounded-xl bg-[var(--accent)] px-3 py-1.5 text-[11px] font-extrabold text-[var(--on-accent)]"
          >
            {animatePhase ? <Pause className="h-3 w-3" /> : <Play className="h-3 w-3" />}
            {animatePhase ? 'Pausieren' : 'Pfeile animieren'}
          </button>
        </div>
      </div>

      {/* SVG Soccer Pitch */}
      <div className="overflow-hidden rounded-2xl border border-[var(--line)] bg-[#143D29] p-2">
        <svg
          viewBox="0 0 320 180"
          className="h-52 w-full sm:h-60"
          role="img"
          aria-label={play.title}
        >
          {/* Alternating Pitch Mowing Stripes */}
          <rect x="0" y="0" width="320" height="180" fill="#164A31" rx="8" />
          <rect x="32" y="0" width="32" height="180" fill="#1A5438" />
          <rect x="96" y="0" width="32" height="180" fill="#1A5438" />
          <rect x="160" y="0" width="32" height="180" fill="#1A5438" />
          <rect x="224" y="0" width="32" height="180" fill="#1A5438" />
          <rect x="288" y="0" width="32" height="180" fill="#1A5438" />

          {/* Pitch Markings */}
          <g fill="none" stroke="rgba(255,255,255,0.52)" strokeWidth="1.5">
            <rect x="12" y="10" width="296" height="160" rx="2" />
            <line x1="160" y1="10" x2="160" y2="170" />
            <circle cx="160" cy="90" r="24" />
            <circle cx="160" cy="90" r="2" fill="rgba(255,255,255,0.65)" />
            {/* Left & Right Boxes */}
            <rect x="12" y="45" width="42" height="90" />
            <rect x="12" y="68" width="16" height="44" />
            <rect x="266" y="45" width="42" height="90" />
            <rect x="292" y="68" width="16" height="44" />
          </g>

          {/* Tactical Target Zone */}
          {play.zoneRect && (
            <g>
              <rect
                x={play.zoneRect.x}
                y={play.zoneRect.y}
                width={play.zoneRect.w}
                height={play.zoneRect.h}
                rx="6"
                fill="rgba(45,212,191,0.18)"
                stroke="#2DD4BF"
                strokeWidth="1.5"
                strokeDasharray="4 4"
              />
              <text
                x={play.zoneRect.x + play.zoneRect.w / 2}
                y={play.zoneRect.y + 13}
                textAnchor="middle"
                fill="#A7F3D0"
                fontSize="8"
                fontWeight="800"
              >
                {play.zoneRect.label}
              </text>
            </g>
          )}

          {/* Animated Passing / Running Vectors */}
          {play.vectors.map((v, i) => (
            <path
              key={i}
              d={v.d}
              fill="none"
              stroke={v.color}
              strokeWidth="2.8"
              strokeLinecap="round"
              className={animatePhase ? 'c1-svg-flow' : undefined}
              strokeDasharray={animatePhase ? undefined : '6 4'}
            />
          ))}

          {/* Attacking Team Tokens */}
          {play.attackers.map((p) => {
            const x = phaseStep === 1 ? p.movedCx : p.cx;
            const y = phaseStep === 1 ? p.movedCy : p.cy;
            return (
              <g
                key={`att-${p.id}`}
                style={{
                  transform: `translate(${x}px, ${y}px)`,
                  transition: 'transform 650ms cubic-bezier(0.22, 1, 0.36, 1)',
                }}
              >
                <circle r="9.5" fill="#2DD4BF" stroke="#08172B" strokeWidth="1.8" />
                <text
                  y="3"
                  textAnchor="middle"
                  fill="#08172B"
                  fontSize="8"
                  fontWeight="900"
                >
                  {p.id}
                </text>
              </g>
            );
          })}

          {/* Defending Team Tokens */}
          {play.defenders.map((d) => {
            const x = phaseStep === 1 ? d.movedCx : d.cx;
            const y = phaseStep === 1 ? d.movedCy : d.cy;
            return (
              <g
                key={`def-${d.id}`}
                style={{
                  transform: `translate(${x}px, ${y}px)`,
                  transition: 'transform 650ms cubic-bezier(0.22, 1, 0.36, 1)',
                }}
              >
                <circle r="8.5" fill="#E5484D" stroke="#FFFFFF" strokeWidth="1.5" />
                <text
                  y="3"
                  textAnchor="middle"
                  fill="#FFFFFF"
                  fontSize="7"
                  fontWeight="900"
                >
                  {d.id}
                </text>
              </g>
            );
          })}

          {/* Animated Ball */}
          <circle
            r="4.8"
            fill="#FFFFFF"
            stroke="#08172B"
            strokeWidth="1.5"
            style={{
              transform: `translate(${phaseStep === 1 ? play.ball.x2 : play.ball.x1}px, ${
                phaseStep === 1 ? play.ball.y2 : play.ball.y1
              }px)`,
              transition: 'transform 700ms cubic-bezier(0.22, 1, 0.36, 1)',
            }}
          />
        </svg>
      </div>

      <p className="text-xs leading-relaxed text-[var(--ink2)]">
        <strong className="text-[var(--ink)]">Leitprinzip:</strong> {play.principle}
      </p>
    </section>
  );
};
