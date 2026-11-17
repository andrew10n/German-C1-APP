export type LessonGraphicType =
  | 'TARGET'
  | 'GRID'
  | 'PROCESS'
  | 'ARGUMENT'
  | 'EVIDENCE'
  | 'DIALOGUE'
  | 'BRIDGE'
  | 'FUNNEL'
  | 'BALANCE'
  | 'CYCLE';

export interface LessonVisualBlueprint {
  graphic: LessonGraphicType;
  captionDe: string;
  captionEn: string;
}

export function lessonVisualBlueprint(unitId: string): LessonVisualBlueprint {
  switch (unitId) {
    case 's0-profile':
      return { graphic: 'TARGET', captionDe: 'Wunsch → überprüfbares Ziel', captionEn: 'Wish → observable goal' };
    case 's0-diagnose':
      return { graphic: 'GRID', captionDe: 'Vier Fertigkeiten, ein ehrlicher Ausgangspunkt', captionEn: 'Four skills, one honest starting point' };
    case 's0-repair':
      return { graphic: 'DIALOGUE', captionDe: 'Störung → Klärung → gemeinsames Verständnis', captionEn: 'Breakdown → clarification → shared understanding' };
    case 's0-retrieve':
    case 's5-revise':
    case 's6-retention':
      return { graphic: 'CYCLE', captionDe: 'Abrufen, prüfen und erneut anwenden', captionEn: 'Recall, check and apply again' };
    case 's1-explain':
      return { graphic: 'PROCESS', captionDe: 'Schrittfolge mit sichtbaren Übergängen', captionEn: 'A sequence with visible transitions' };
    case 's1-justify':
    case 's4-argument':
      return { graphic: 'ARGUMENT', captionDe: 'These, Beleg und Einwand auseinanderhalten', captionEn: 'Separate claim, evidence and counterargument' };
    case 's1-uncertainty':
    case 's2-data':
    case 's4-challenge':
    case 's6-specialist':
      return { graphic: 'EVIDENCE', captionDe: 'Aussage, Beleg und Unsicherheitsgrenze', captionEn: 'Claim, evidence and uncertainty boundary' };
    case 's1-feedback':
      return { graphic: 'DIALOGUE', captionDe: 'Beobachtung → Wirkung → nächster Schritt', captionEn: 'Observation → impact → next step' };
    case 's2-register':
      return { graphic: 'BRIDGE', captionDe: 'Adressat und Situation steuern das Register', captionEn: 'Audience and situation shape register' };
    case 's2-summarise':
    case 's4-present':
      return { graphic: 'FUNNEL', captionDe: 'Wesentliches bündeln, Grenzen erhalten', captionEn: 'Distil the essential while preserving limits' };
    case 's2-mediate':
    case 's3-negotiate':
      return { graphic: 'BRIDGE', captionDe: 'Perspektiven verbinden, ohne sie gleichzusetzen', captionEn: 'Connect perspectives without conflating them' };
    case 's3-disagree':
      return { graphic: 'BALANCE', captionDe: 'Klar widersprechen und das Gegenüber achten', captionEn: 'Disagree clearly while respecting the other person' };
    case 's3-deescalate':
      return { graphic: 'CYCLE', captionDe: 'Spannung aufnehmen, neu rahmen, Lösung öffnen', captionEn: 'Acknowledge tension, reframe and open a way forward' };
    case 's3-agree':
      return { graphic: 'PROCESS', captionDe: 'Verantwortung, Termin und Kriterium festhalten', captionEn: 'Record ownership, timing and a success criterion' };
    case 's4-ethics':
      return { graphic: 'BALANCE', captionDe: 'Werte abwägen und die Entscheidung begrenzen', captionEn: 'Balance values and qualify the decision' };
    case 's5-spontaneous':
      return { graphic: 'DIALOGUE', captionDe: 'Hören → reagieren → bei Bedarf reparieren', captionEn: 'Listen → respond → repair when needed' };
    case 's5-nuance':
      return { graphic: 'EVIDENCE', captionDe: 'Bedeutung als Abstufung statt Entweder-oder', captionEn: 'Meaning as a spectrum, not a binary' };
    case 's5-facilitate':
      return { graphic: 'GRID', captionDe: 'Mehrere Stimmen zu einem gemeinsamen Faden verknüpfen', captionEn: 'Weave multiple voices into a shared thread' };
    case 's6-general':
      return { graphic: 'PROCESS', captionDe: 'Neue Aufgabe → Strategie → Selbstcheck', captionEn: 'New task → strategy → self-check' };
    case 's6-checkpoint':
      return { graphic: 'TARGET', captionDe: 'Signale in ein begrenztes Übungsziel übersetzen', captionEn: 'Turn signals into a bounded practice goal' };
    default:
      return { graphic: 'PROCESS', captionDe: 'Kontext → Sprachzug → Wirkung', captionEn: 'Context → language move → effect' };
  }
}

export type SlideArchetype =
  | 'SEQUENCE'
  | 'CONTRAST'
  | 'CAUSE_EFFECT'
  | 'EVIDENCE'
  | 'CONDITION'
  | 'DIALOGUE'
  | 'DISTINCTION';

export interface DiscourseToken {
  text: string;
  kind: 'connector' | 'modal' | 'number' | 'quote' | 'plain';
  roleLabel?: string;
  roleHint?: string;
  slotIndex?: 0 | 1 | 2;
}

export interface SlideHighlight {
  word: string;
  role: string;
  hint: string;
  slotIndex: 0 | 1 | 2;
}

export interface ImprintSlide {
  index: number;
  rawText: string;
  archetype: SlideArchetype;
  archetypeLabel: string;
  archetypeLabelEn: string;
  headline: string;
  nodes: [string, string, string];
  bridgeLabel: string;
  tokens: DiscourseToken[];
  highlights: SlideHighlight[];
  insightDe: string;
  insightEn: string;
}

interface SignalRule {
  pattern: RegExp;
  kind: 'connector' | 'modal';
  role: string;
  hint: string;
  archetype?: SlideArchetype;
  slotIndex: 0 | 1 | 2;
}

const SIGNAL_RULES: SignalRule[] = [
  {
    pattern: /\b(zunächst|zuerst|vor dem start|anfangs)\b/i,
    kind: 'connector',
    role: 'Schritt 1',
    hint: 'Markiert den Ausgangspunkt einer geordneten Abfolge.',
    archetype: 'SEQUENCE',
    slotIndex: 0,
  },
  {
    pattern: /\b(anschließend|danach|erst dann|erst wenn|zum schluss|schließlich|nach [a-zäöüß]+ minuten?)\b/i,
    kind: 'connector',
    role: 'Folgeschritt',
    hint: 'Bindet den nächsten Schritt an Zeit oder Voraussetzung.',
    archetype: 'SEQUENCE',
    slotIndex: 2,
  },
  {
    pattern: /\b(zwar)\b/i,
    kind: 'connector',
    role: 'Zugeständnis',
    hint: 'Erkennt eine Seite an, bevor die Einschränkung folgt.',
    archetype: 'CONTRAST',
    slotIndex: 0,
  },
  {
    pattern: /\b(allerdings|jedoch|hingegen|dennoch|trotzdem|aber|gegen)\b/i,
    kind: 'connector',
    role: 'Einwand / Grenze',
    hint: 'Verschiebt das Gewicht auf einen Gegenpunkt oder eine Grenze.',
    archetype: 'CONTRAST',
    slotIndex: 2,
  },
  {
    pattern: /\b(nicht [^,.]+,\s*sondern|statt|weder [^,.]+ noch|entweder)\b/i,
    kind: 'connector',
    role: 'Abgrenzung',
    hint: 'Schließt eine Fehlinterpretation aus und schärft die Alternative.',
    archetype: 'DISTINCTION',
    slotIndex: 1,
  },
  {
    pattern: /\b(deshalb|daher|folglich|ausschlaggebend|sodass|damit|weil|da)\b/i,
    kind: 'connector',
    role: 'Ursache / Zweck',
    hint: 'Verknüpft Grund oder Absicht sichtbar mit der Konsequenz.',
    archetype: 'CAUSE_EFFECT',
    slotIndex: 1,
  },
  {
    pattern: /\b(sollten|falls|sofern|wenn|unter einer [^,.]+ annahme|voraussetzung)\b/i,
    kind: 'connector',
    role: 'Bedingung',
    hint: 'Macht die Aussage überprüfbar: Gilt nur, wenn die Bedingung erfüllt ist.',
    archetype: 'CONDITION',
    slotIndex: 1,
  },
  {
    pattern: /\b(fest steht|belegt|beobachtet|tatsächlich|gesichert)\b/i,
    kind: 'modal',
    role: 'Gesicherte Beobachtung',
    hint: 'Benennt nur das, wofür direkte Beobachtung oder Daten vorliegen.',
    archetype: 'EVIDENCE',
    slotIndex: 0,
  },
  {
    pattern: /\b(offen|möglicherweise|denkbar|könnte|könnten|dürfte|dürften|vielleicht|vermutung|vorsorglich|kein nachweis)\b/i,
    kind: 'modal',
    role: 'Hypothese / Offenheit',
    hint: 'Hält Vermutungen und fehlende Belege sauber vom Fakt getrennt.',
    archetype: 'EVIDENCE',
    slotIndex: 2,
  },
  {
    pattern: /\b(trennen|unterscheiden|unterschiedlich|ebenen|gemeint ist|versteht man)\b/i,
    kind: 'connector',
    role: 'Begriffsschärfe',
    hint: 'Zieht eine klare Grenze zwischen zwei leicht verwechselten Ebenen.',
    archetype: 'DISTINCTION',
    slotIndex: 1,
  },
];

function compactPhrase(raw: string, maxLen = 30): string {
  const cleaned = raw
    .replace(/[„“"']/g, '')
    .replace(/\s+/g, ' ')
    .trim();
  if (cleaned.length <= maxLen) return cleaned;
  const words = cleaned.split(' ');
  let acc = '';
  for (const w of words) {
    if ((acc + ' ' + w).trim().length > maxLen - 1) break;
    acc = (acc + ' ' + w).trim();
  }
  return acc ? `${acc}…` : `${cleaned.slice(0, maxLen - 1)}…`;
}

function splitChunkIntoVisualClauses(text: string): [string, string, string] {
  const quotes = text.match(/„[^“]{3,60}“/g);
  const clauses = text
    .split(/(?:;|:\s+|\.\s+|,\s+(?=(?:allerdings|jedoch|aber|sondern|damit|weil|da|obwohl|deshalb|während|ohne dass)\b))/i)
    .map((s) => s.trim())
    .filter((s) => s.length > 3);

  if (quotes && quotes.length >= 2) {
    return [
      compactPhrase(quotes[0], 26),
      compactPhrase(clauses[0] ?? 'Aussage vs. Deutung', 24),
      compactPhrase(quotes[1], 26),
    ];
  }

  if (clauses.length >= 3) {
    return [
      compactPhrase(clauses[0], 28),
      compactPhrase(clauses[1], 28),
      compactPhrase(clauses[clauses.length - 1], 28),
    ];
  }

  if (clauses.length === 2) {
    const words = clauses[1].split(' ');
    const mid = Math.ceil(words.length / 2);
    return [
      compactPhrase(clauses[0], 28),
      compactPhrase(words.slice(0, mid).join(' '), 26),
      compactPhrase(words.slice(mid).join(' ') || clauses[1], 28),
    ];
  }

  const words = text.split(/\s+/).filter(Boolean);
  const third = Math.max(1, Math.floor(words.length / 3));
  return [
    compactPhrase(words.slice(0, third).join(' '), 26),
    compactPhrase(words.slice(third, third * 2).join(' '), 26),
    compactPhrase(words.slice(third * 2).join(' '), 26),
  ];
}

function detectArchetype(text: string): {
  archetype: SlideArchetype;
  labelDe: string;
  labelEn: string;
  headline: string;
  bridgeLabel: string;
  insightDe: string;
  insightEn: string;
} {
  if (/„[^“]+“/.test(text) && /\b(fragt|antwortet|meint|sagt|schreibt|bestätigt)\b/i.test(text)) {
    return {
      archetype: 'DIALOGUE',
      labelDe: 'Gesprächszug',
      labelEn: 'Dialogue Move',
      headline: 'Aussage → präzise Rückfrage → Klärung',
      bridgeLabel: 'Rückfrage klärt',
      insightDe: 'Wörtliche Rede zeigt den genauen Moment, an dem Bedeutung ausgehandelt wird.',
      insightEn: 'Quoted speech highlights the exact turn where meaning is clarified.',
    };
  }
  if (/\b(fest steht|offen|könnte|könnten|denkbar|möglicherweise|root cause|vermutung|kein nachweis|belegt)\b/i.test(text)) {
    return {
      archetype: 'EVIDENCE',
      labelDe: 'Evidenzgrenze',
      labelEn: 'Evidence Boundary',
      headline: 'Beobachtung sauber von Hypothese trennen',
      bridgeLabel: 'Prüfgrenze',
      insightDe: 'C1-Präzision trennt, was direkt gemessen wurde, von dem, was erst noch geprüft werden muss.',
      insightEn: 'C1 precision separates what is observed from what still needs testing.',
    };
  }
  if (/\b(zwar|allerdings|jedoch|hingegen|gegen|aber|trotzdem|einwand)\b/i.test(text)) {
    return {
      archetype: 'CONTRAST',
      labelDe: 'Abwägung',
      labelEn: 'Trade-off',
      headline: 'Vorteil anerkennen · Gegenargument gewichten',
      bridgeLabel: 'Gewichtung',
      insightDe: 'Ein guter Standpunkt blendet den Gegenpunkt nicht aus, sondern setzt ihn sichtbar auf die Waage.',
      insightEn: 'A strong stance acknowledges the trade-off instead of hiding it.',
    };
  }
  if (/\b(trennen|unterscheiden|gemeint ist|nicht [^,.]+,\s*sondern|ebenen|verwechselt)\b/i.test(text)) {
    return {
      archetype: 'DISTINCTION',
      labelDe: 'Unterscheidung',
      labelEn: 'Distinction',
      headline: 'Zwei Ebenen scharf auseinanderhalten',
      bridgeLabel: '≠ Unterschied',
      insightDe: 'Durch explizite Abgrenzung verhinderst du Fehlschlüsse beim Gegenüber.',
      insightEn: 'Explicit contrast prevents the listener from conflating two distinct concepts.',
    };
  }
  if (/\b(sollten|falls|sofern|voraussetzung|annahme|wenn [^,.]+,\s*(?:müsste|wird|spielt|bleibt))\b/i.test(text)) {
    return {
      archetype: 'CONDITION',
      labelDe: 'Bedingung & Revision',
      labelEn: 'Condition & Revision',
      headline: 'Wann gilt die Aussage – und wann ändert sie sich?',
      bridgeLabel: 'Falls erfüllt →',
      insightDe: 'Bedingungssätze zeigen, unter welcher überprüfbaren Annahme eine Empfehlung hält.',
      insightEn: 'Conditionals make the recommendation testable by naming when it would change.',
    };
  }
  if (/\b(deshalb|daher|ausschlaggebend|weil|da\s|damit|sodass|daraus)\b/i.test(text)) {
    return {
      archetype: 'CAUSE_EFFECT',
      labelDe: 'Ursache & Wirkung',
      labelEn: 'Cause & Effect',
      headline: 'Grund oder Kriterium → begründete Folge',
      bridgeLabel: 'führt zu →',
      insightDe: 'Kausale Konnektoren zeigen, warum eine Entscheidung aus den Fakten folgt.',
      insightEn: 'Causal connectors make the link between criteria and outcome explicit.',
    };
  }
  return {
    archetype: 'SEQUENCE',
    labelDe: 'Ablauf & Struktur',
    labelEn: 'Step Sequence',
    headline: 'Geordnete Schritte mit klaren Übergängen',
    bridgeLabel: 'Übergang →',
    insightDe: 'Zeitliche und logische Marker orientieren dein Gegenüber Schritt für Schritt.',
    insightEn: 'Sequence markers guide your listener step by step without ambiguity.',
  };
}

function tokenizeSlideText(text: string): {
  tokens: DiscourseToken[];
  highlights: SlideHighlight[];
} {
  // Match quotes, signal words, or numbers cleanly
  const masterRegex =
    /(„[^“]{2,65}“|\b(?:zunächst|zuerst|anschließend|danach|erst dann|erst wenn|zum schluss|schließlich|zwar|allerdings|jedoch|hingegen|dennoch|trotzdem|aber|sondern|statt|deshalb|daher|ausschlaggebend|sodass|damit|weil|sollten|falls|sofern|wenn|fest steht|offen|möglicherweise|denkbar|könnte|könnten|dürfte|dürften|vielleicht|trennen|unterscheiden|gemeint ist|versteht man|voraussetzung|annahme)\b|\b\d+(?:[.,–-]\d+)?(?:\s*(?:%|minuten?|tagen?|wochen?|monaten?|jahren?|fahrten?|wörtern?))?\b)/gi;

  const tokens: DiscourseToken[] = [];
  const highlights: SlideHighlight[] = [];
  const seenWords = new Set<string>();
  let lastIndex = 0;

  let match: RegExpExecArray | null;
  while ((match = masterRegex.exec(text)) !== null) {
    if (match.index > lastIndex) {
      tokens.push({ text: text.slice(lastIndex, match.index), kind: 'plain' });
    }

    const raw = match[0];
    if (raw.startsWith('„')) {
      tokens.push({
        text: raw,
        kind: 'quote',
        roleLabel: 'Wörtliches Beispiel',
        roleHint: 'Direktes Zitat oder konkrete Beispielformulierung aus der Situation.',
        slotIndex: 1,
      });
      if (!seenWords.has(raw)) {
        seenWords.add(raw);
        highlights.push({
          word: compactPhrase(raw, 28),
          role: 'Beispiel',
          hint: 'Zeigt die konkrete Formulierung in der Situation.',
          slotIndex: 1,
        });
      }
    } else if (/^\d/.test(raw)) {
      tokens.push({
        text: raw,
        kind: 'number',
        roleLabel: 'Konkrete Größe',
        roleHint: 'Belegte Zahl, Dauer oder Grenze – nicht verallgemeinern.',
        slotIndex: 0,
      });
      if (!seenWords.has(raw)) {
        seenWords.add(raw);
        highlights.push({
          word: raw,
          role: 'Kennzahl',
          hint: 'Konkreter Umfang statt vager Schätzung.',
          slotIndex: 0,
        });
      }
    } else {
      const rule = SIGNAL_RULES.find((r) => r.pattern.test(raw));
      const kind = rule?.kind ?? 'connector';
      const role = rule?.role ?? 'Signalwort';
      const hint = rule?.hint ?? 'Steuert die Satzlogik im C1-Diskurs.';
      const slotIndex = rule?.slotIndex ?? 1;

      tokens.push({
        text: raw,
        kind,
        roleLabel: role,
        roleHint: hint,
        slotIndex,
      });
      const key = raw.toLowerCase();
      if (!seenWords.has(key)) {
        seenWords.add(key);
        highlights.push({ word: raw, role, hint, slotIndex });
      }
    }

    lastIndex = masterRegex.lastIndex;
  }

  if (lastIndex < text.length) {
    tokens.push({ text: text.slice(lastIndex), kind: 'plain' });
  }

  return { tokens, highlights: highlights.slice(0, 5) };
}

export function parseReadingSlides(passage: string, unitTitle: string): ImprintSlide[] {
  const byParagraph = passage
    .split(/\n\s*\n+/)
    .map((s) => s.trim())
    .filter(Boolean);

  let rawChunks: string[];
  if (byParagraph.length > 1) {
    rawChunks = byParagraph;
  } else {
    const sentences = passage
      .split(/(?<=[.!?])\s+(?=[A-ZÄÖÜ„])/)
      .map((s) => s.trim())
      .filter(Boolean);

    if (sentences.length <= 2) {
      rawChunks = [passage.trim()];
    } else {
      rawChunks = [];
      for (let i = 0; i < sentences.length; i += 2) {
        const pair = sentences.slice(i, i + 2).join(' ');
        // Keep slides bite-sized (Imprint cadence): if > 290 chars, split 1 sentence per slide
        if (pair.length > 290 && sentences[i + 1]) {
          rawChunks.push(sentences[i]);
          rawChunks.push(sentences[i + 1]);
        } else {
          rawChunks.push(pair);
        }
      }
    }
  }

  return rawChunks.map((rawText, index) => {
    const meta = detectArchetype(rawText);
    const nodes = splitChunkIntoVisualClauses(rawText);
    const { tokens, highlights } = tokenizeSlideText(rawText);
    const headline =
      index === 0 && rawChunks.length > 1 ? `${unitTitle.split(' ').slice(0, 4).join(' ')} · Einstieg` : meta.headline;

    return {
      index,
      rawText,
      archetype: meta.archetype,
      archetypeLabel: meta.labelDe,
      archetypeLabelEn: meta.labelEn,
      headline,
      nodes,
      bridgeLabel: meta.bridgeLabel,
      tokens,
      highlights,
      insightDe: meta.insightDe,
      insightEn: meta.insightEn,
    };
  });
}

export interface GrammarVisualClause {
  quote: string;
  connector: string | null;
  architecture: 'NEBENSATZ_END' | 'V2_INVERSION' | 'KONJUNKTIV_HYPOTHESIS' | 'PARTICLE_MODULATION';
  architectureLabel: string;
  slots: {
    label: string;
    text: string;
    tone: 'accent' | 'sky' | 'violet' | 'ok';
  }[];
  insight: string;
}

export interface GrammarSpectrumItem {
  marker: string;
  label: string;
  certainty: number; // 0..100
  note: string;
}

export interface ParsedGrammarVisual {
  clauses: GrammarVisualClause[];
  spectrum: GrammarSpectrumItem[];
  rules: string[];
}

function decomposeGermanQuote(quote: string): GrammarVisualClause {
  const clean = quote.trim();
  const hasComma = clean.includes(',');
  const subMatch = clean.match(/^(Erst wenn|Wenn|Weil|Da|Obwohl|Ob|Damit|Sodass|Sollten|Falls|Sofern|Zwar|Während)\b\s*(.*)/i);
  const conjMatch = clean.match(/\b(könnte|dürfte|müsste|wäre|hätte|sollten|würde)\b/i);
  const particleMatch = clean.match(/\b(mal|doch|wohl|halt|eben|schon)\b/i);

  if (subMatch && hasComma) {
    const [firstClause, secondClause] = clean.split(/,\s*(.+)/);
    const firstWords = firstClause.trim().split(/\s+/);
    const secondWords = (secondClause ?? '').replace(/[.!?]$/, '').trim().split(/\s+/);

    return {
      quote: clean,
      connector: subMatch[1],
      architecture: conjMatch ? 'KONJUNKTIV_HYPOTHESIS' : 'NEBENSATZ_END',
      architectureLabel: conjMatch
        ? 'Bedingung + Konjunktiv II'
        : 'Nebensatz (Verbende) → Hauptsatz (V2)',
      slots: [
        {
          label: '1. Konnektor',
          text: subMatch[1],
          tone: 'accent',
        },
        {
          label: '2. Nebensatz-Verb (Ende)',
          text: firstWords.slice(-1)[0] ?? firstClause,
          tone: 'violet',
        },
        {
          label: '3. Hauptsatz-V2 (direkt nach Komma)',
          text: secondWords[0] ?? '—',
          tone: 'sky',
        },
        {
          label: '4. Aussage-Kern',
          text: compactPhrase(secondWords.slice(1).join(' ') || secondClause, 32),
          tone: 'ok',
        },
      ],
      insight:
        'Vor dem Komma zieht der Nebensatz-Konnektor das finite Verb ans Ende; direkt nach dem Komma steht sofort das finite Verb des Hauptsatzes.',
    };
  }

  if (particleMatch) {
    return {
      quote: clean,
      connector: particleMatch[1],
      architecture: 'PARTICLE_MODULATION',
      architectureLabel: 'Ton & Beziehung modulieren',
      slots: [
        { label: 'Äußerung', text: compactPhrase(clean, 34), tone: 'accent' },
        { label: 'Partikel / Marker', text: `„${particleMatch[1]}“`, tone: 'violet' },
        { label: 'Wirkung', text: 'Färbt Haltung, ersetzt keinen Beleg', tone: 'sky' },
      ],
      insight: `Die Partikel „${particleMatch[1]}“ steuert Gesprächston oder Erwartung – ihre Wirkung hängt von Kontext und Intonation ab.`,
    };
  }

  if (conjMatch) {
    const words = clean.replace(/[.!?]$/, '').split(/\s+/);
    const modalIdx = words.findIndex((w) =>
      /^(könnte|dürfte|müsste|wäre|hätte|sollten|würde)$/i.test(w),
    );
    return {
      quote: clean,
      connector: conjMatch[1],
      architecture: 'KONJUNKTIV_HYPOTHESIS',
      architectureLabel: 'Konjunktiv II · Hypothese vs. Fakt',
      slots: [
        {
          label: '1. Thema / Vorfeld',
          text: compactPhrase(words.slice(0, Math.max(1, modalIdx)).join(' '), 26),
          tone: 'accent',
        },
        {
          label: '2. Konjunktiv-Anker',
          text: words[modalIdx] ?? conjMatch[1],
          tone: 'violet',
        },
        {
          label: '3. Offene / geprüfte Folge',
          text: compactPhrase(words.slice(modalIdx + 1).join(' '), 30),
          tone: 'sky',
        },
      ],
      insight: `„${conjMatch[1]}“ kennzeichnet bewusst eine Möglichkeit, Hypothese oder Revisionsbedingung – keinen gesicherten Nachweis.`,
    };
  }

  const words = clean.replace(/[.!?]$/, '').split(/\s+/);
  return {
    quote: clean,
    connector: words[0] ?? null,
    architecture: 'V2_INVERSION',
    architectureLabel: 'Vorfeld-Fokus & Verbzweitstellung (V2)',
    slots: [
      { label: '1. Vorfeld (Fokus)', text: words[0] ?? '—', tone: 'accent' },
      { label: '2. Finites Verb (V2)', text: words[1] ?? '—', tone: 'sky' },
      { label: '3. Mittelfeld & Klammer', text: compactPhrase(words.slice(2).join(' '), 34), tone: 'violet' },
    ],
    insight: 'Steht ein Konnektor oder Grund im Vorfeld, bleibt das finite Verb fest auf Position 2.',
  };
}

export function parseGrammarVisuals(grammar: string): ParsedGrammarVisual {
  const quotes: string[] = [];
  const quotePattern = /['„]([^'“]{8,140})['“]/g;
  let m: RegExpExecArray | null;
  while ((m = quotePattern.exec(grammar)) !== null) {
    const candidate = m[1].trim();
    if (candidate.split(/\s+/).length >= 3 && !quotes.includes(candidate)) {
      quotes.push(candidate);
    }
  }

  const clauses = quotes.slice(0, 3).map(decomposeGermanQuote);

  const spectrum: GrammarSpectrumItem[] = [];
  if (/\b(könnte|möglichkeit|vermutung|wohl|vielleicht)\b/i.test(grammar)) {
    spectrum.push({ marker: 'könnte / wohl', label: 'Vorsichtige Hypothese', certainty: 30, note: 'Offen für Alternativen' });
  }
  if (/\b(dürfte|wahrscheinlicher|stärkere vermutung)\b/i.test(grammar)) {
    spectrum.push({ marker: 'dürfte', label: 'Stärkere Annahme', certainty: 65, note: 'Braucht belastbare Hinweise' });
  }
  if (/\b(müsste|erwartung|beleg|fest steht)\b/i.test(grammar)) {
    spectrum.push({ marker: 'belegt / fest steht', label: 'Gesicherter Befund', certainty: 92, note: 'Direkt beobachtet oder gemessen' });
  }

  const rules = grammar
    .split(/(?<=[.!?])\s+(?=[A-ZÄÖÜ'„])/)
    .map((r) => r.trim())
    .filter((r) => r.length > 8 && !quotes.some((q) => r === `'${q}'` || r === `„${q}“`))
    .slice(0, 4);

  return { clauses, spectrum, rules };
}

export interface DialogueTurnVisual {
  index: number;
  speaker: string | null;
  text: string;
  moveLabel: string;
  moveTone: 'accent' | 'sky' | 'violet' | 'coral';
}

export function parseDialogueVisuals(transcript: string): DialogueTurnVisual[] {
  const speakerPrefix = /^([^:\n]{1,60}):\s*(.*)$/;
  const rawTurns: { speaker: string | null; text: string }[] = [];
  let currentSpeaker: string | null = null;
  let currentLines: string[] = [];

  const flush = () => {
    const text = currentLines.join(' ').trim();
    if (text) rawTurns.push({ speaker: currentSpeaker, text });
    currentLines = [];
  };

  for (const rawLine of transcript.split(/\r?\n/)) {
    const line = rawLine.trim();
    if (!line) continue;
    const match = speakerPrefix.exec(line);
    if (match) {
      flush();
      currentSpeaker = match[1].trim();
      currentLines.push(match[2].trim());
    } else if (currentSpeaker === null) {
      rawTurns.push({ speaker: null, text: line });
    } else {
      currentLines.push(line);
    }
  }
  flush();

  return rawTurns.map((turn, index) => {
    let moveLabel = 'Impuls';
    let moveTone: DialogueTurnVisual['moveTone'] = index % 2 === 0 ? 'accent' : 'sky';

    if (turn.speaker === null) {
      moveLabel = 'Szene';
      moveTone = 'violet';
    } else if (turn.text.includes('?')) {
      moveLabel = 'Rückfrage';
      moveTone = 'sky';
    } else if (/^(nein|nicht einfach|dafür liegt|aber)/i.test(turn.text) || /\b(einwand|falsch|risiko|ausgesetzt)\b/i.test(turn.text)) {
      moveLabel = 'Abgrenzung / Einwand';
      moveTone = 'coral';
    } else if (/\b(genau|richtig|verstanden|zusammen|also)\b/i.test(turn.text)) {
      moveLabel = 'Klärung';
      moveTone = 'accent';
    } else {
      moveLabel = 'Präzisierung';
      moveTone = 'violet';
    }

    return { index, speaker: turn.speaker, text: turn.text, moveLabel, moveTone };
  });
}

export interface TaskBlueprintVisual {
  roleFrom: string;
  roleTo: string;
  wordTarget: string | null;
  checklist: { id: string; text: string; kind: 'action' | 'constraint' }[];
  guards: string[];
}

export function parseTaskBlueprint(promptText: string, taskKind: string): TaskBlueprintVisual {
  const wordMatch = promptText.match(/\b(\d{2,3}\s*[–-]\s*\d{2,3}\s*Wörter|\d+\s*(?:Sekunden|Minuten|Sätze|Tage?n?))\b/i);
  const audienceMatch = promptText.match(/\b(?:für eine[rn]?|an eine[rn]?|mit einer|einer)\s+([A-ZÄÖÜ0-9][A-Za-zÄÖÜäöüß0-9\- ]{3,32})/i);

  const guards: string[] = [];
  if (/keine.*erfinden|erfinden Sie weder|ohne neue Fakten/i.test(promptText)) {
    guards.push('Keine Fakten/Zahlen erfinden');
  }
  if (/ohne Vorlage/i.test(promptText)) {
    guards.push('Frei ohne Vorlage produzieren');
  }
  if (/unterscheiden|trennen/i.test(promptText)) {
    guards.push('Ebenen sauber trennen');
  }

  const rawClauses = promptText
    .split(/(?<=[.!?])\s+|:\s+|\s*;\s*/)
    .map((s) => s.trim())
    .filter((s) => s.length > 8);

  const checklist = rawClauses.slice(0, 4).map((clause, i) => ({
    id: `${taskKind}-${i}`,
    text: clause.replace(/\.$/, ''),
    kind: (/\b(ohne|nicht|erfinden|trennen|unterscheiden|prüfen)\b/i.test(clause)
      ? 'constraint'
      : 'action') as 'action' | 'constraint',
  }));

  return {
    roleFrom: taskKind === 'retrieval' ? 'Dein Gedächtnis' : 'Deine Produktion',
    roleTo: audienceMatch ? compactPhrase(audienceMatch[1], 28) : 'Zielpublikum (C1)',
    wordTarget: wordMatch ? wordMatch[0] : null,
    checklist,
    guards,
  };
}
