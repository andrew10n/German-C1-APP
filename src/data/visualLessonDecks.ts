import type { ContextTrack } from '../types';

export type MentalDiagramArchetype =
  | 'SPECTRUM'
  | 'BRACKET'
  | 'MATRIX_2X2'
  | 'PIPELINE'
  | 'SCALE'
  | 'BRIDGE'
  | 'FUNNEL'
  | 'LOOP';

export interface VisualStepCard {
  tag: string;
  title: string;
  caption: string;
  nodes: [string, string, string];
  highlightIndex: 0 | 1 | 2;
}

export interface ContextTransformPair {
  sceneLabel: string;
  avoidLabel: string;
  avoidPhrase: string;
  c1Phrase: string;
  grammarHint: string;
}

export interface EmbeddedMicroCheck {
  prompt: string;
  options: string[];
  correctIndex: number;
  explanation: string;
}

export interface ClauseSnapPuzzle {
  instruction: string;
  tiles: string[];
  targetOrder: string[];
  syntaxRule: string;
}

export interface SentenceSurgeryItem {
  label: string;
  before: string;
  diagnosis: string;
  after: string;
  steps: string[];
}

export interface UnitVisualDeck {
  unitId: string;
  modelTitle: string;
  diagramType: MentalDiagramArchetype;
  visualHook: string;
  steps: [VisualStepCard, VisualStepCard, VisualStepCard];
  contextTransforms: Record<ContextTrack, ContextTransformPair>;
  microCheck: EmbeddedMicroCheck;
  clausePuzzle: ClauseSnapPuzzle;
  surgery: SentenceSurgeryItem;
}

const makeDeck = (
  unitId: string,
  modelTitle: string,
  diagramType: MentalDiagramArchetype,
  visualHook: string,
  steps: [VisualStepCard, VisualStepCard, VisualStepCard],
  contextTransforms: Record<ContextTrack, ContextTransformPair>,
  microCheck: EmbeddedMicroCheck,
  clausePuzzle: ClauseSnapPuzzle,
  surgery: SentenceSurgeryItem,
): UnitVisualDeck => ({
  unitId,
  modelTitle,
  diagramType,
  visualHook,
  steps,
  contextTransforms,
  microCheck,
  clausePuzzle,
  surgery,
});

export const VISUAL_LESSON_DECKS: Record<string, UnitVisualDeck> = {
  's0-profile': makeDeck(
    's0-profile',
    'Vom Wunsch zum überprüfbaren Ziel',
    'PIPELINE',
    'Zielklarheit entsteht, wenn Anlass, Gesprächsrolle und Kriterium feststehen.',
    [
      {
        tag: '1 · ANLASS',
        title: 'Situation konkret eingrenzen',
        caption: 'Statt „besser Deutsch sprechen“ genau eine reale Gesprächssituation benennen.',
        nodes: ['Vager Wunsch', 'Konkrete Szene', 'Kriterium'],
        highlightIndex: 0,
      },
      {
        tag: '2 · ROLLE',
        title: 'Adressat & Register festlegen',
        caption: 'Wer hört zu? Danach richten sich Ton, Fachwortschatz und Direktheit.',
        nodes: ['Situation', 'Adressat & Ton', 'Nachweis'],
        highlightIndex: 1,
      },
      {
        tag: '3 · CHECK',
        title: 'Beobachtbares Ergebnis definieren',
        caption: 'Woran erkennt ein Zuhörer nach dem Gespräch, dass dein Ziel erreicht wurde?',
        nodes: ['Kontext', 'Handlung', 'Überprüfbar'],
        highlightIndex: 2,
      },
    ],
    {
      GENERAL: {
        sceneLabel: 'Behörde / Termin',
        avoidLabel: 'Zu vage',
        avoidPhrase: 'Ich möchte im Amt einfach besser reden können.',
        c1Phrase: 'Ich möchte im Bürgeramt eine Fristverlängerung sachlich begründen und schriftlich bestätigen lassen.',
        grammarHint: 'Infinitivkonstruktion mit konkretisiertem Objekt + Ergebnisverb.',
      },
      SOCIAL: {
        sceneLabel: 'Schwäbischer Verein',
        avoidLabel: 'Pauschal',
        avoidPhrase: 'Ich will im Verein halt mitreden.',
        c1Phrase: 'Ich möchte beim Vereinsabend die gewünschte Anrede erfragen und bei Dialektausdrücken freundlich nachhaken.',
        grammarHint: 'Verben „erfragen“ & „nachhaken“ statt unscharfem „mitreden“.',
      },
      ENGINEERING: {
        sceneLabel: 'EV-Fahrdynamikreview',
        avoidLabel: 'Unkonkret',
        avoidPhrase: 'Ich muss über Hybrid-Bremsen berichten.',
        c1Phrase: 'Ich möchte beim Fahrdynamikreview offene Rekuperationsdaten benennen, ohne eine Serienfreigabe vorwegzunehmen.',
        grammarHint: 'Negierte Folge mit „ohne … zu + Infinitiv“ präzisiert den Rahmen.',
      },
      COACHING: {
        sceneLabel: 'U13-Platz & Eltern',
        avoidLabel: 'Allgemein',
        avoidPhrase: 'Meine Spieler sollen mich verstehen.',
        c1Phrase: 'Ich möchte U13-Übungen in drei klaren Schritten erklären und eine Spieler-Rückmeldung einholen.',
        grammarHint: 'Kriterium über hörbares Verhalten der Zielgruppe verankert.',
      },
    },
    {
      prompt: 'Welches Lernziel ist sprachlich überprüfbar formuliert?',
      options: [
        'Ich will flüssiger und sicherer klingen.',
        'Ich erkläre eine U13-Passübung in 60 Sekunden und lasse den Ablauf kurz wiedergeben.',
        'Ich lerne diese Woche fünfzig schwierige C1-Wörter.',
      ],
      correctIndex: 1,
      explanation: 'Option B nennt konkrete Situation, Zeitrahmen und eine sichtbare Verständnisprüfung.',
    },
    {
      instruction: 'Bringe das C1-Ziel in die richtige Satzklammer-Ordnung:',
      tiles: ['Um Missverständnisse', 'zu vermeiden,', 'halte ich', 'die Vereinbarung', 'schriftlich fest.'],
      targetOrder: ['Um Missverständnisse', 'zu vermeiden,', 'halte ich', 'die Vereinbarung', 'schriftlich fest.'],
      syntaxRule: 'Finalsatz im Vorfeld („Um … zu vermeiden“) → finites Verb direkt auf Position 2 („halte“) → Verbpartikel „fest“ am Ende.',
    },
    {
      label: 'Zielschärfung',
      before: 'Ich will bei Problemen auf Deutsch souverän bleiben.',
      diagnosis: '„Souverän“ ist nicht messbar; Adressat und Handlung fehlen.',
      after: 'Wenn im Review ein Einwand kommt, fasse ich ihn zunächst zusammen, bevor ich meine Gegenposition begründe.',
      steps: ['Auslöser benennen (Wenn…)', 'Konkrete Strategie (zusammenfassen)', 'Reihenfolge mit „bevor“ fixieren'],
    },
  ),

  's0-diagnose': makeDeck(
    's0-diagnose',
    'Rezeption vs. Produktion entkoppeln',
    'MATRIX_2X2',
    'Verstehen und selbst Produzieren sind getrennte Achsen – erst der Abgleich zeigt blinde Flecken.',
    [
      {
        tag: '1 · INPUT',
        title: 'Verstehen ohne Nachfragen',
        caption: 'Beim Lesen/Hören helfen Kontext und Weltwissen; das verdeckt oft Grammatiklücken.',
        nodes: ['Kontext-Hilfe', 'Verstehen hoch', 'Lücke verdeckt'],
        highlightIndex: 0,
      },
      {
        tag: '2 · OUTPUT',
        title: 'Freie Produktion prüft Abruf',
        caption: 'Erst beim Sprechen und Schreiben zeigt sich, welche Strukturen aktiv abrufbar sind.',
        nodes: ['Passiv bekannt', 'Aktiver Abruf', 'Präzision'],
        highlightIndex: 1,
      },
      {
        tag: '3 · ABGLEICH',
        title: 'Selbstbild mit Fremdurteil spiegeln',
        caption: 'Subjektives Gefühl ≠ Wirkung auf Zuhörende. Konkrete Kriterien schließen diese Lücke.',
        nodes: ['Selbstbild', 'Fremdeindruck', 'Trainingsfokus'],
        highlightIndex: 2,
      },
    ],
    {
      GENERAL: {
        sceneLabel: 'Radiobeitrag',
        avoidLabel: 'Vermischt',
        avoidPhrase: 'Ich habe den Beitrag verstanden, also kann ich C1.',
        c1Phrase: 'Den Hauptgedanken habe ich erfasst, doch beim freien Zusammenfassen fehlten mir präzise Konnektoren.',
        grammarHint: 'Konzessiver Kontrast mit „doch“ trennt Rezeption von Produktion.',
      },
      SOCIAL: {
        sceneLabel: 'Einladung deuten',
        avoidLabel: 'Wörtlich',
        avoidPhrase: 'Er hat „Komm halt vorbei“ gesagt, also erwartet er mich sicher.',
        c1Phrase: 'Die Wendung „Komm halt vorbei“ klang unverbindlich; ich frage lieber nach, ob ein konkreter Zeitpunkt passt.',
        grammarHint: 'Subjektive Deutung („klang“) + klärende indirekte Frage („ob“).',
      },
      ENGINEERING: {
        sceneLabel: 'TCU-Fehlerbericht',
        avoidLabel: 'Passiv',
        avoidPhrase: 'Der Bericht war leicht zu lesen.',
        c1Phrase: 'Zwar konnte ich den TCU-Bericht problemlos nachvollziehen, aber die mündliche Einordnung geriet noch stockend.',
        grammarHint: '„Zwar …, aber …“ macht die Lücke zwischen Lesen und Sprechen sichtbar.',
      },
      COACHING: {
        sceneLabel: 'Elternfrage Spielzeit',
        avoidLabel: 'Globalurteil',
        avoidPhrase: 'Das Elterngespräch lief irgendwie komisch.',
        c1Phrase: 'Ich habe den Ärger sofort verstanden, konnte meine Rotationskriterien aber erst im zweiten Anlauf ruhig paraphrasieren.',
        grammarHint: 'Präzise Trennung von Hörverstehen (Ärger) und Reaktionsfähigkeit.',
      },
    },
    {
      prompt: 'Warum ersetzt gutes Hörverstehen keine Sprechdiagnose?',
      options: [
        'Weil beim Hören der Kontext Lücken füllt, die im aktiven Satzbau offenbleiben.',
        'Weil Hörtexte im Deutschen immer langsamer sind als Gespräche.',
        'Weil beim Sprechen keine Grammatik nötig ist.',
      ],
      correctIndex: 0,
      explanation: 'Rezeptives Wiedererkennen nutzt Stützen aus dem Kontext; aktive Produktion verlangt selbstständigen Strukturabruf.',
    },
    {
      instruction: 'Verknüpfe Rezeption und eigene Einschätzung präzise:',
      tiles: ['Obwohl ich den Text', 'mühelos verstand,', 'fiel mir die', 'freie Zusammenfassung', 'spürbar schwer.'],
      targetOrder: ['Obwohl ich den Text', 'mühelos verstand,', 'fiel mir die', 'freie Zusammenfassung', 'spürbar schwer.'],
      syntaxRule: 'Nebensatz („Obwohl … verstand“) im Vorfeld → konjugiertes Verb „fiel“ direkt danach auf Position 2.',
    },
    {
      label: 'Diagnose-Differenzierung',
      before: 'Mein Deutsch ist eigentlich gut, nur beim Reden hakt es manchmal.',
      diagnosis: 'Keine Unterscheidung zwischen Wortschatz, Syntax unter Zeitdruck und Register.',
      after: 'Während ich Fachtexte flüssig lese, verliere ich bei spontanen Einwänden die Verbendstellung im Nebensatz.',
      steps: ['Stärke benennen (Während…)', 'Auslöser markieren (bei spontanen Einwänden)', 'Konkretes Symptom nennen (Verbendstellung)'],
    },
  ),

  's0-repair': makeDeck(
    's0-repair',
    'Reparatur-Schleife im Gespräch',
    'LOOP',
    'Nicht „blind weiternicken“: Verständnisproblem benennen, nachfragen und gesichert bestätigen.',
    [
      {
        tag: '1 · STOPP',
        title: 'Unklarheit sofort markieren',
        caption: 'Besser früh höflich bremsen als auf einer falschen Annahme weiterdiskutieren.',
        nodes: ['Unklarheit', 'Höflicher Stopp', 'Bestätigung'],
        highlightIndex: 0,
      },
      {
        tag: '2 · PARAPHRASE',
        title: 'Eigene Deutung anbieten',
        caption: 'Mit „Verstehe ich Sie richtig, dass …?“ gibst du eine prüfbare Hypothese vor.',
        nodes: ['Signalwort', 'Paraphrase', 'Prüf-Check'],
        highlightIndex: 1,
      },
      {
        tag: '3 · REPARATUR',
        title: 'Selbstkorrektur statt Rechtfertigung',
        caption: 'Eigene unklare Formulierung offen korrigieren („Genauer gesagt meine ich …“).',
        nodes: ['Einwand', 'Umformulierung', 'Einigkeit'],
        highlightIndex: 2,
      },
    ],
    {
      GENERAL: {
        sceneLabel: 'Vertragsfrage',
        avoidLabel: 'Unsicher',
        avoidPhrase: 'Was meinen Sie damit? Das ist komisch.',
        c1Phrase: 'Darf ich kurz nachfragen: Bedeutet diese Klausel, dass die Kündigungsfrist bereits ab Monatsende läuft?',
        grammarHint: 'Entscheidungsfrage nach Doppelpunkt prüft eine konkrete Lesart.',
      },
      SOCIAL: {
        sceneLabel: 'Schwäbisch „Passt scho“',
        avoidLabel: 'Fehlgedeutet',
        avoidPhrase: 'Super, „passt scho“ heißt also definitiv Ja.',
        c1Phrase: 'Nur zur Sicherheit, damit wir nicht aneinander vorbeireden: Soll ich die Unterlagen morgen fest einplanen?',
        grammarHint: 'Vorangestellter Zweck „damit wir nicht…“ entschärft das Nachhaken.',
      },
      ENGINEERING: {
        sceneLabel: 'Drehmoment-Schnittstelle',
        avoidLabel: 'Mehrdeutig',
        avoidPhrase: 'Meinen Sie jetzt den Wert oben oder unten?',
        c1Phrase: 'Um Missverständnisse auszuschließen: Bezieht sich das Limit auf das angeforderte oder das gemeldete Moment?',
        grammarHint: 'Doppelalternative („oder“) erzwingt präzise technische Klärung.',
      },
      COACHING: {
        sceneLabel: 'U11-Übung prüfen',
        avoidLabel: 'Scheinkontrolle',
        avoidPhrase: 'Habt ihr alle alles verstanden? Gut, los!',
        c1Phrase: 'Bevor wir starten: Kann mir jemand kurz in eigenen Worten sagen, wohin der erste Pass geht?',
        grammarHint: 'Aktive Wiedergabe durch den Spieler deckt Lücken vor Übungsbeginn auf.',
      },
    },
    {
      prompt: 'Welche Rückfrage prüft eine Zusage zuverlässig, ohne misstrauisch zu klingen?',
      options: [
        'Stimmt das jetzt wirklich oder sagen Sie das nur so?',
        'Damit ich es richtig notiere: Gilt der Termin damit als fest eingeplant?',
        'Alles klar, dann passt es ja.',
      ],
      correctIndex: 1,
      explanation: '„Damit ich es richtig notiere…“ nennt ein sachliches Motiv und fragt nach konkreter Verbindlichkeit.',
    },
    {
      instruction: 'Baue eine höfliche Gesprächsreparatur zusammen:',
      tiles: ['Genauer gesagt', 'meinte ich nicht', 'den Zeitplan,', 'sondern die', 'fehlende Freigabe.'],
      targetOrder: ['Genauer gesagt', 'meinte ich nicht', 'den Zeitplan,', 'sondern die', 'fehlende Freigabe.'],
      syntaxRule: 'Satzadverbial im Vorfeld („Genauer gesagt“) → finites Verb „meinte“ → „nicht X, sondern Y“ als scharfer Kontrast.',
    },
    {
      label: 'Selbstreparatur',
      before: 'Nein, falsch verstanden, ich hab doch Test gesagt!',
      diagnosis: 'Vorwurfsvoller Ton („falsch verstanden“, „doch“) blockiert Kooperation.',
      after: 'Da habe ich mich eben unpräzise ausgedrückt: Ich bezog mich nicht auf die Simulation, sondern auf den Prüfstandslauf.',
      steps: ['Verantwortung übernehmen (unpräzise ausgedrückt)', 'Fokus schärfen (nicht A, sondern B)', 'Ruhiger Ton ohne Partikel „doch“'],
    },
  ),

  's0-retrieve': makeDeck(
    's0-retrieve',
    'Abruf schlägt Wiederlesen',
    'LOOP',
    'Passives Durchlesen erzeugt Vertrautheits-Illusion; erst das Rekonstruieren ohne Vorlage festigt Synapsen.',
    [
      {
        tag: '1 · ILLUSION',
        title: 'Wiederlesen täuscht Sicherheit vor',
        caption: 'Wenn der Text vor dir liegt, erkennst du Wörter wieder – das ist noch kein eigener Abruf.',
        nodes: ['Text vor Augen', 'Vertrautheit', 'Scheinwissen'],
        highlightIndex: 0,
      },
      {
        tag: '2 · ABRUF',
        title: 'Aus dem Gedächtnis rekonstruieren',
        caption: 'Vorlage schließen, Kernaussagen frei formulieren, gezielt Lücken sichtbar machen.',
        nodes: ['Leeres Blatt', 'Freier Abruf', 'Echte Lücke'],
        highlightIndex: 1,
      },
      {
        tag: '3 · TRANSFER',
        title: 'Zeitversetzt im neuen Fall testen',
        caption: 'Nach Tagen Pause in einem anderen Kontext erneut abrufen – erst das beweist Verfügbarkeit.',
        nodes: ['Pause (Abstand)', 'Neuer Kontext', 'Langzeitfest'],
        highlightIndex: 2,
      },
    ],
    {
      GENERAL: {
        sceneLabel: 'Nachricht wiedergeben',
        avoidLabel: 'Abgelesen',
        avoidPhrase: 'Ich lese die E-Mail einfach nochmal ab.',
        c1Phrase: 'Aus dem Gedächtnis zusammengefasst verlangt die Behörde bis Freitag zwei Nachweise sowie eine kurze Begründung.',
        grammarHint: 'Partizipialer Einstieg („Aus dem Gedächtnis zusammengefasst“) als Vorfeld.',
      },
      SOCIAL: {
        sceneLabel: 'Höflichkeitsbitte variieren',
        avoidLabel: 'Schablone',
        avoidPhrase: 'Machst du das mal schnell?',
        c1Phrase: 'Könntest du mir bis heute Abend kurz rückmelden, ob die Zeiten für dich passen?',
        grammarHint: 'Konjunktiv II („Könntest“) + „ob“-Nebensatz statt Imperativdruck.',
      },
      ENGINEERING: {
        sceneLabel: 'EV-FMEA frei erklären',
        avoidLabel: 'Folien-Ablesen',
        avoidPhrase: 'Auf Folie 4 steht Severity mal Occurrence…',
        c1Phrase: 'Entscheidend bei der neuen FMEA-Logik ist, dass eine hohe Bedeutung auch bei geringer Entdeckungswahrscheinlichkeit Rückfragen auslöst.',
        grammarHint: 'Voranstellung des Prädikatsadjektivs „Entscheidend“ lenkt Aufmerksamkeit.',
      },
      COACHING: {
        sceneLabel: 'Aufwärmübung korrigiert',
        avoidLabel: 'Unreflektiert',
        avoidPhrase: 'Macht einfach wie letztes Mal.',
        c1Phrase: 'Letzte Woche fehlte mir in der Ansage der Blickkontakt-Hinweis; heute starten wir erst nach kurzem Schulterblick.',
        grammarHint: 'Eigene frühere Lücke benannt → direkte Verhaltensanpassung.',
      },
    },
    {
      prompt: 'Wann zeigt sich, dass eine C1-Struktur wirklich im Langzeitgedächtnis verankert ist?',
      options: [
        'Wenn ich den Grammatikkasten dreimal hintereinander markiert habe.',
        'Wenn ich sie nach einigen Tagen Pause ohne Vorlage in einer neuen Situation richtig anwende.',
        'Wenn ich sie im Lesetext sofort wiedererkenne.',
      ],
      correctIndex: 1,
      explanation: 'Zeitversetzter Transfer ohne Vorlage trennt echtes Beherrschen von kurzfristigem Wiedererkennen.',
    },
    {
      instruction: 'Ordne die Begründung für aktives Erinnern:',
      tiles: ['Erst wenn man', 'die Struktur ohne Vorlage', 'abrufen muss,', 'werden sprachliche Lücken', 'wirklich sichtbar.'],
      targetOrder: ['Erst wenn man', 'die Struktur ohne Vorlage', 'abrufen muss,', 'werden sprachliche Lücken', 'wirklich sichtbar.'],
      syntaxRule: 'Vorangestellter Temporalsatz („Erst wenn … muss“) → finites Verb „werden“ direkt nach dem Komma.',
    },
    {
      label: 'Vom Passivlesen zur Eigenformulierung',
      before: 'Ich weiß grob, was im Text über FMEA stand.',
      diagnosis: 'Vages Gefühl ohne überprüfbare Satzstruktur.',
      after: 'Ohne in die Notizen zu schauen, kann ich drei Kriterien nennen, nach denen eine FMEA-Priorität bewertet wird.',
      steps: ['Bedingung setzen (Ohne…)', 'Konkrete Zahl/Kategorie (drei Kriterien)', 'Präpositionalen Relativsatz nutzen (nach denen)'],
    },
  ),

  's1-explain': makeDeck(
    's1-explain',
    'Schrittfolge vs. Fehlerregel',
    'PIPELINE',
    'Ein guter Erklärtext trennt die normale Reihenfolge („Zunächst → Anschließend“) sauber von Bedingungen („Erst wenn“) und Ausnahmen.',
    [
      {
        tag: '1 · ABFOLGE',
        title: 'Schritte zeitlich gliedern',
        caption: '„Zunächst“, „anschließend“ und „daraufhin“ zeigen den Normalablauf ohne Verwirrung.',
        nodes: ['Zunächst', 'Anschließend', 'Abschluss'],
        highlightIndex: 0,
      },
      {
        tag: '2 · ABGRENZUNG',
        title: 'Begriff definieren & eingrenzen',
        caption: 'Mit „Unter X versteht man …; gemeint ist nicht …“ verhinderst du Fehlinterpretationen sofort.',
        nodes: ['Begriff', 'Definition', 'Nicht gemeint'],
        highlightIndex: 1,
      },
      {
        tag: '3 · REGEL-CHECK',
        title: 'Bedingung & Neustart trennen',
        caption: '„Erst wenn X erfüllt ist, passiert Y“ + explizite Fehlerregel + Verständnisabfrage.',
        nodes: ['Bedingung', 'Ausnahme', 'Verständnis'],
        highlightIndex: 2,
      },
    ],
    {
      GENERAL: {
        sceneLabel: 'Bibliotheks-Ausleihe',
        avoidLabel: 'Ungeordnet',
        avoidPhrase: 'Du gehst zum Automaten, aber manchmal geht die Karte nicht, und vorher musst du sie freischalten.',
        c1Phrase: 'Zunächst aktivieren Sie die Karte am Schalter; erst dann scannt das Terminal die Medien. Sollte ein Buch gesperrt sein, bleibt das Konto unberührt.',
        grammarHint: 'Klare Konnektoren + „Sollte…“ für den Ausnahmefall.',
      },
      SOCIAL: {
        sceneLabel: 'Schwäbischer Verein',
        avoidLabel: 'Direkt/Ungeschützt',
        avoidPhrase: 'Mach das einfach so, und frag nicht lang.',
        c1Phrase: 'Schau mal: Erst klären wir die Einteilung, anschließend tragen wir die Namen ein. Wenn etwas unklar ist, frag einfach kurz nach.',
        grammarHint: 'Partikel „mal“ mildert Einladung ab; Nachfragen wird explizit geöffnet.',
      },
      ENGINEERING: {
        sceneLabel: 'ECU/TCU-Prüfstand',
        avoidLabel: 'Vermischt',
        avoidPhrase: 'Wir testen das Drehmoment, und wenn es nicht passt, ist das Auto nicht fertig.',
        c1Phrase: 'Zunächst wird die Softwareversion protokolliert. Erst bei Abweichung zwischen Soll- und Ist-Moment greift der Abbruch; daraus folgt noch keine Gesamtaussage zur Freigabe.',
        grammarHint: 'Trennung von Testabbruch-Regel und Systemfreigabe.',
      },
      COACHING: {
        sceneLabel: 'U13-Passdreieck',
        avoidLabel: 'Unpräzise',
        avoidPhrase: 'Passen und dann ganz schnell wegrennen! Bei Fehler seid ihr raus.',
        c1Phrase: 'Unter Freilaufen verstehen wir hier, wieder anspielbar zu werden. Erst wenn Blickkontakt besteht, spielt ihr weiter; bei Fehlern startet ihr ruhig neu.',
        grammarHint: 'Definition („Unter X verstehen wir“) + „Erst wenn“ mit Verbzweitstellung.',
      },
    },
    {
      prompt: 'Welche Verknüpfung grenzt einen Fachbegriff sauber gegen Missverständnisse ab?',
      options: [
        'Freilaufen heißt einfach, so schnell wie möglich wegzurennen.',
        'Unter Freilaufen versteht man hier, sich anspielbar zu machen; gemeint ist nicht planloses Sprinten.',
        'Freilaufen ist halt wichtig im modernen Fußball.',
      ],
      correctIndex: 1,
      explanation: 'Positive Definition plus expliziter Ausschluss („gemeint ist nicht…“) verhindert Fehlinterpretationen sofort.',
    },
    {
      instruction: 'Verknüpfe Bedingung und Hauptsatz korrekt:',
      tiles: ['Erst wenn', 'die nächste Station', 'Blickkontakt aufnimmt,', 'wird der Ball', 'weitergespielt.'],
      targetOrder: ['Erst wenn', 'die nächste Station', 'Blickkontakt aufnimmt,', 'wird der Ball', 'weitergespielt.'],
      syntaxRule: 'Nebensatz („Erst wenn … aufnimmt“) belegt Position 1 → Passiv-Hilfsverb „wird“ direkt auf Position 2.',
    },
    {
      label: 'Ablauf & Fehlerregel trennen',
      before: 'Ihr passt und rennt, und wenn der Ball weg ist, wechselt die Richtung oder ihr fangt neu an.',
      diagnosis: 'Zeittakt (Richtungswechsel) und Fehlerregel (Neustart) werden vermengt.',
      after: 'Alle zwei Minuten wechselt ihr regulär die Richtung. Misslingt zwischendurch eine Annahme, stoppt ihr lediglich kurz und beginnt neu.',
      steps: ['Zeitintervall in eigenen Satz', 'Fehlerfall mit vorangestelltem Verb („Misslingt…“)', 'Abschwächer „lediglich“ klärt geringe Konsequenz'],
    },
  ),

  's1-justify': makeDeck(
    's1-justify',
    'Kriterium-Beleg-Abwägung',
    'SCALE',
    'Eine überzeugende Begründung wägt Vorteile gegen Gegenargumente ab und nennt die Bedingung, unter der sich das Urteil ändert.',
    [
      {
        tag: '1 · KRITERIUM',
        title: 'Maßstab offenlegen',
        caption: '„Ausschlaggebend sind …“ zeigt dem Gegenüber, nach welcher Regel du entscheidest.',
        nodes: ['Behauptung', 'Maßstab', 'Gewichtung'],
        highlightIndex: 0,
      },
      {
        tag: '2 · WAAGE',
        title: 'Pro & Contra fair abwägen',
        caption: 'Mit „Zwar …, allerdings …“ zeigst du, dass du das Gegenargument gesehen hast.',
        nodes: ['Vorteil (Zwar)', 'Gewicht (Da)', 'Einwand (Allerdings)'],
        highlightIndex: 1,
      },
      {
        tag: '3 · REVISION',
        title: 'Geltungsgrenze setzen',
        caption: '„Sollten sich die Rahmenbedingungen ändern, müsste man …“ macht das Urteil überprüfbar.',
        nodes: ['Empfehlung', 'Bedingung', 'Neubewertung'],
        highlightIndex: 2,
      },
    ],
    {
      GENERAL: {
        sceneLabel: 'Mobilitätswahl',
        avoidLabel: 'Unbelegt',
        avoidPhrase: 'Ein Abo ist immer besser als Einzeltickets.',
        c1Phrase: 'Bei weniger als zehn Fahrten im Monat fallen die Fixkosten stärker ins Gewicht; deshalb lohnt sich ein Einzeltarif vorerst eher.',
        grammarHint: '„Deshalb“ mit Inversion leitet begründete Folgerung ein.',
      },
      SOCIAL: {
        sceneLabel: 'Budgetvorschlag Verein',
        avoidLabel: 'Drängend',
        avoidPhrase: 'Wir kaufen das jetzt, das ist doch völlig klar!',
        c1Phrase: 'Zwar wäre ein eigenes Gerät bequem; angesichts von zwölf Einsätzen jährlich sprechen die laufenden Kosten aber für die Miete.',
        grammarHint: 'Präpositionalphrase „angesichts“ + Genitiv trägt sachlichen Grund.',
      },
      ENGINEERING: {
        sceneLabel: 'EV-Rekuperation',
        avoidLabel: 'Überzogen',
        avoidPhrase: 'Variante A gewinnt klar, weil sie mehr Energie spart.',
        c1Phrase: 'Obwohl Variante A mehr Energie rückgewinnt, fehlen noch Daten bei niedriger Haftung; daher empfehle ich zunächst weitere Prüfstandsläufe.',
        grammarHint: 'Konzessiver Vorspann begrenzt die Reichweite der Empfehlung.',
      },
      COACHING: {
        sceneLabel: 'U13-Kleinfeldspiel',
        avoidLabel: 'Pauschal',
        avoidPhrase: 'Linienübungen sind schlecht, Kleinfeld ist besser.',
        c1Phrase: 'Zwar erleichtern Linienübungen das Zählen, allerdings erhöhen zwei parallele 4-gegen-4-Felder die Ballkontakte pro Kind erheblich – sofern genügend Bälle bereitliegen.',
        grammarHint: 'Abwägung + Einschränkung („sofern“) schützt vor Übertreibung.',
      },
    },
    {
      prompt: 'Welcher Zusatz macht eine Entscheidungsempfehlung revisionsfähig?',
      options: [
        'Diese Lösung ist alternativlos und spart garantiert Geld.',
        'Sollten wir künftig jede Woche auswärts fahren, müssten wir die Gesamtkosten neu bewerten.',
        'Alle im Vorstand finden das gut.',
      ],
      correctIndex: 1,
      explanation: 'Ein expliziter Auslöser („Sollten wir…“) definiert exakt, wann die Entscheidung überprüft wird.',
    },
    {
      instruction: 'Verknüpfe Zugeständnis und Einschränkung richtig:',
      tiles: ['Zwar wäre ein', 'eigener Bus', 'jederzeit verfügbar,', 'allerdings fallen auch', 'ohne Fahrten Fixkosten an.'],
      targetOrder: ['Zwar wäre ein', 'eigener Bus', 'jederzeit verfügbar,', 'allerdings fallen auch', 'ohne Fahrten Fixkosten an.'],
      syntaxRule: '„Zwar“ + Konjunktiv II für die gedachte Option; nach dem Komma folgt „allerdings“ direkt vor dem Verb „fallen“.',
    },
    {
      label: 'Von pauschaler Meinung zu Abwägung',
      before: 'Mieten ist immer billiger, deshalb kaufen wir keinen Bus.',
      diagnosis: 'Unzulässige Verallgemeinerung („immer“); Gegenargument fehlt.',
      after: 'Bei unseren bisherigen zwölf Fahrten spricht die Vermeidung laufender Fixkosten für eine Miete; bei wöchentlichem Einsatz wäre neu zu rechnen.',
      steps: ['Geltungsbereich auf bisherige Daten einengen', 'Konkreten Kostentreiber nennen', 'Revisionspunkt ergänzen'],
    },
  ),

  's1-uncertainty': makeDeck(
    's1-uncertainty',
    'Epistemisches Spektrum: Fakt vs. Indiz vs. Hypothese',
    'SPECTRUM',
    'Zwischen „bewiesen“ und „völlig unbekannt“ liegen präzise C1-Stufen: Befund → Indiz → Hypothese → Prüfschritt.',
    [
      {
        tag: '1 · BEFUND',
        title: 'Was ist sicher beobachtet?',
        caption: '„Fest steht bislang nur, dass …“ trennt Messdaten sauber von jeder Interpretation.',
        nodes: ['Messbefund', 'Indiz', 'Vermutung'],
        highlightIndex: 0,
      },
      {
        tag: '2 · HYPOTHESE',
        title: 'Ursachen als möglich markieren',
        caption: 'Konjunktiv II („könnte“) und „ob“-Fragen lassen konkurrierende Erklärungen offen.',
        nodes: ['Erklärung A', 'Erklärung B', 'Offener Status'],
        highlightIndex: 1,
      },
      {
        tag: '3 · PRÜFSCHRITT',
        title: 'Vorsorge & Test benennen',
        caption: 'Keine falsche Sicherheit durch „sicher vielleicht“ – stattdessen konkretisieren, was als Nächstes geprüft wird.',
        nodes: ['Evidenzgrenze', 'Prüfplan', 'Vorsorgestatus'],
        highlightIndex: 2,
      },
    ],
    {
      GENERAL: {
        sceneLabel: 'Widersprüchliche Meldung',
        avoidLabel: 'Spekulativ',
        avoidPhrase: 'Die Bahn streikt bestimmt wieder.',
        c1Phrase: 'Gesichert ist bislang nur, dass die Verbindung ausfällt; ob eine technische Störung vorliegt, wird derzeit noch geprüft.',
        grammarHint: '„Gesichert ist …; ob …“ setzt klare Grenze zwischen Fakt und Grund.',
      },
      SOCIAL: {
        sceneLabel: 'Ausgebliebene Antwort',
        avoidLabel: 'Unterstellung',
        avoidPhrase: 'Er antwortet nicht, also hat er keine Lust.',
        c1Phrase: 'Da bisher keine Rückmeldung einging, lässt sich der Grund nicht ablesen; möglicherweise hat meine Nachricht ihn gar nicht erreicht.',
        grammarHint: 'Kausalsatz + neutrales Modaladverb „möglicherweise“ vermeidet Unterstellungen.',
      },
      ENGINEERING: {
        sceneLabel: 'Hybrid ECU/TCU-Rucken',
        avoidLabel: 'Kausalsprung',
        avoidPhrase: 'Die neue TCU-Software ist eindeutig schuld am Rucken.',
        c1Phrase: 'Beobachtet wurde ein Zeitversatz im Protokoll; ob die Software oder die geänderte Messkonfiguration ursächlich ist, bleibt bis zur Messkettenprüfung offen.',
        grammarHint: 'Passiv für den Befund + indirekte Doppelfrage („ob A oder B“).',
      },
      COACHING: {
        sceneLabel: 'U14-Trainingsabsenzen',
        avoidLabel: 'Charakterurteil',
        avoidPhrase: 'Er fehlt zweimal, ihm ist das Team wohl egal.',
        c1Phrase: 'Fest steht, dass Leo zweimal gefehlt hat; die Ursache ist offen. Denkbar ist eine Terminüberschneidung – wir fragen freundlich nach.',
        grammarHint: 'Trennung von Teilnahme-Fakt und unbewiesenen Motiven.',
      },
    },
    {
      prompt: 'Welcher Satz vermischt Sicherheit und Unsicherheit unzulässig?',
      options: [
        'Die Ursache bleibt offen; eine geänderte Messkonfiguration könnte den Effekt erklären.',
        'Das liegt wohl eindeutig sicher an der TCU-Software.',
        'Fest steht bislang nur, dass zwei Prototypen beim Übergang geruckelt haben.',
      ],
      correctIndex: 1,
      explanation: '„wohl“ (Vermutung) und „eindeutig sicher“ (Gewissheit) widersprechen sich im selben Satz.',
    },
    {
      instruction: 'Formuliere eine saubere Evidenzgrenze mit Verb-Endstellung:',
      tiles: ['Ob die neue Software', 'das Rucken tatsächlich', 'verursacht,', 'lässt sich aus diesen Daten', 'noch nicht ableiten.'],
      targetOrder: ['Ob die neue Software', 'das Rucken tatsächlich', 'verursacht,', 'lässt sich aus diesen Daten', 'noch nicht ableiten.'],
      syntaxRule: 'Subjektsatz mit „ob“ im Vorfeld (Verb „verursacht“ am Ende) → finites Verb „lässt“ auf Position 2.',
    },
    {
      label: 'Befund von Vermutung lösen',
      before: 'Die Messzeitstempel sind kaputt, deshalb ruckelt die Software sicher.',
      diagnosis: 'Unbewiesene Kausalität und Widerspruch („kaputt“ vs. „Software“).',
      after: 'Fest steht bislang ein Zeitversatz im Protokoll. Denkbar wäre entweder eine Abweichung der Zeitstempel oder eine reale Verzögerung.',
      steps: ['Beobachtung isolieren (Zeitversatz)', 'Konjunktiv II („wäre“) für Alternativen', '„entweder … oder“ offen halten'],
    },
  ),

  's1-feedback': makeDeck(
    's1-feedback',
    'WWW-Schleife ohne Abwertung',
    'BRIDGE',
    'Konstruktive Rückmeldung schlägt eine Brücke: Wahrnehmung (konkret) → Wirkung (Ich-Ebene) → Wunsch (verhandelbar).',
    [
      {
        tag: '1 · SZENE',
        title: 'Einzelbeobachtung statt „immer“',
        caption: 'Mit „Als …“ greifst du eine konkrete Situation heraus, statt den Charakter zu bewerten.',
        nodes: ['Einzel-Szene', 'Wirkung', 'Bitte'],
        highlightIndex: 0,
      },
      {
        tag: '2 · WIRKUNG',
        title: 'Folge sachlich beschreiben',
        caption: '„Für mich war schwer erkennbar …“ beschreibt Wirkung, ohne dem Gegenüber böse Absicht zu unterstellen.',
        nodes: ['Kein Charakterurteil', 'Auswirkung', 'Offenheit'],
        highlightIndex: 1,
      },
      {
        tag: '3 · DIALOG',
        title: 'Sicht erfragen & Abmachung treffen',
        caption: '„Wie hast du es erlebt?“ + konkrete nächste Vereinbarung verwandelt Kritik in Zusammenarbeit.',
        nodes: ['Rückfrage', 'Vereinbarung', 'Nachprüfung'],
        highlightIndex: 2,
      },
    ],
    {
      GENERAL: {
        sceneLabel: 'Dienstleistung reklamieren',
        avoidLabel: 'Abwertend',
        avoidPhrase: 'Sie arbeiten schlampig und vergessen immer die Hälfte!',
        c1Phrase: 'Bei der gestrigen Lieferung fehlten zwei Positionen auf dem Lieferschein; könnten wir vereinbaren, die Nachsendung bis Donnerstag abzustimmen?',
        grammarHint: 'Konkreter Zeitpunkt + Konjunktiv-II-Bitte vermeidet Pauschalangriff.',
      },
      SOCIAL: {
        sceneLabel: 'Vereins-Rückmeldung',
        avoidLabel: 'Pseudoprobleme',
        avoidPhrase: 'Du sagst „nicht schlecht“, also fandest du es eigentlich mies.',
        c1Phrase: 'Danke für die Rückmeldung! Welche zwei Punkte haben gut gepasst, und wo sollten wir beim nächsten Treffen nachsteuern?',
        grammarHint: 'Konkrete Doppelfrage übersetzt knappes Lob in nutzbare Infos.',
      },
      ENGINEERING: {
        sceneLabel: 'FMEA-Übergabe',
        avoidLabel: 'Unterstellung',
        avoidPhrase: 'Du lässt immer die Annahmen weg, um schneller fertig zu sein.',
        c1Phrase: 'Als die Annahmenfolie entfiel, kamen Rückfragen zur Datengrundlage. Könnten wir ein kurzes Zeitsignal vereinbaren, damit sie künftig drinbleibt?',
        grammarHint: '„Als“ (einmaliges Ereignis) statt „immer wenn“ entschärft sofort.',
      },
      COACHING: {
        sceneLabel: 'U12-Schulterblick',
        avoidLabel: 'Etikettierend',
        avoidPhrase: 'Du passt nie auf, was hinter dir passiert!',
        c1Phrase: 'In zwei Szenen vorhin kam der Pass direkt nach dem Stoppen, während eine Mitspielerin frei hinter dir stand. Wie hast du die Szene gesehen?',
        grammarHint: 'Präzise Szenenangabe + offene Frage stärkt Selbstwahrnehmung.',
      },
    },
    {
      prompt: 'Warum wirkt „Ich finde, du bist unzuverlässig“ trotz Ich-Form verletzend?',
      options: [
        'Weil „Ich“ am Satzanfang grammatisch falsch ist.',
        'Weil es trotz „ich finde“ ein pauschales Charakterurteil ohne konkrete Verhaltensszene fällt.',
        'Weil es zu höflich formuliert ist.',
      ],
      correctIndex: 1,
      explanation: 'Echte Ich-Botschaften beschreiben konkrete Beobachtung und eigene Wirkung – nicht pauschale Eigenschaften der anderen Person.',
    },
    {
      instruction: 'Setze Beobachtung, Wirkung und Bitte sauber zusammen:',
      tiles: ['Als die Annahmenfolie', 'ausblieb,', 'war die Datengrundlage', 'für das Publikum', 'nicht mehr erkennbar.'],
      targetOrder: ['Als die Annahmenfolie', 'ausblieb,', 'war die Datengrundlage', 'für das Publikum', 'nicht mehr erkennbar.'],
      syntaxRule: 'Einmaliges Ereignis in der Vergangenheit mit „Als“ → direkt anschließendes Verb „war“ auf Position 2.',
    },
    {
      label: 'Charakterurteil in Verhaltensbitte umbauen',
      before: 'Du ignorierst immer die Elternnachrichten!',
      diagnosis: 'Unterstellt Absicht („ignorierst“) und verallgemeinert („immer“).',
      after: 'Bei der Platzänderung am Dienstag erreichte die Information zwei Elternfamilien nicht; lasst uns kurz festlegen, wer bis wann schreibt.',
      steps: ['Subjekt auf die Information verlagern', 'Konkreten Fall nennen (am Dienstag)', 'Verantwortung & Frist klären'],
    },
  ),

  's2-data': makeDeck(
    's2-data',
    'Zahl-vs.-Aussagekraft-Filter',
    'FUNNEL',
    'Zahlen sind nie roh „wahr“: Erst Stichprobe, Messmethode und Vergleichsbasis zeigen, was eine Kennzahl tatsächlich trägt.',
    [
      {
        tag: '1 · BASIS',
        title: 'Grundgesamtheit & n prüfen',
        caption: '„+50 %“ klingt gewaltig – doch bei n = 4 Personen reicht ein einziger Wechsel für die Quote.',
        nodes: ['Rohzahl / %', 'Stichprobe & Basis', 'Tragweite'],
        highlightIndex: 0,
      },
      {
        tag: '2 · METHODE',
        title: 'Messgrenze & Selbstbericht trennen',
        caption: 'Gemessene Sensorwerte ≠ Umfrage-Einschätzung; Messrauschen begrenzt den Vergleich.',
        nodes: ['Messart', 'Unsicherheit', 'Vergleichbar?'],
        highlightIndex: 1,
      },
      {
        tag: '3 · SCHLUSS',
        title: 'Korrelation ≠ Kausalität',
        caption: 'Zwei Entwicklungen im selben Zeitraum beweisen noch lange nicht, dass A die Ursache von B ist.',
        nodes: ['Zusammenhang', 'Störfaktor', 'Begrenztes Fazit'],
        highlightIndex: 2,
      },
    ],
    {
      GENERAL: {
        sceneLabel: 'Umfrage einordnen',
        avoidLabel: 'Überzogen',
        avoidPhrase: 'Die Studie beweist, dass alle Bürger unzufrieden sind.',
        c1Phrase: 'Die Befragung deutet zwar auf Unzufriedenheit hin, beschränkt sich jedoch auf 120 Online-Teilnehmende und ist somit nicht repräsentativ.',
        grammarHint: 'Partizip/Relativsatz grenzt Reichweite der Stichprobe ein.',
      },
      SOCIAL: {
        sceneLabel: 'Vereinszahlen lesen',
        avoidLabel: 'Schwammig',
        avoidPhrase: 'Bloß ein paar Leute fehlen immer.',
        c1Phrase: 'Die Angabe „bloß ein paar“ ersetzt keine Teilnehmerliste; von 18 gemeldeten Personen waren in den letzten drei Terminen im Schnitt zwölf vor Ort.',
        grammarHint: 'Konkreter Bezug auf Referenzgröße (von 18) macht Trend prüfbar.',
      },
      ENGINEERING: {
        sceneLabel: 'Querbeschleunigung',
        avoidLabel: 'Scheingenau',
        avoidPhrase: 'Aufbau B ist exakt 4 % besser.',
        c1Phrase: 'Der Unterschied von vier Prozentpunkten liegt innerhalb der ausgewiesenen Messunsicherheit und erlaubt keine belastbare Aussage zur Überlegenheit.',
        grammarHint: 'Prozent vs. Prozentpunkte + Hinweis auf Messunsicherheit.',
      },
      COACHING: {
        sceneLabel: 'U15-Passquote',
        avoidLabel: 'Blind für Kontext',
        avoidPhrase: '85 % Passquote heißt, unser Spielaufbau war super.',
        c1Phrase: 'Eine hohe Passquote spiegelt oft viele Sicherheitspässe in der eigenen Hälfte wider, sagt aber wenig über Raumgewinn im letzten Drittel.',
        grammarHint: 'Kontrast „spiegelt X wider, sagt aber wenig über Y“ deckt blinde Flecken auf.',
      },
    },
    {
      prompt: 'Worauf weist der Unterschied zwischen „4 %“ und „4 Prozentpunkten“ bei Quoten hin?',
      options: [
        'Prozentpunkte messen den absoluten Abstand zwischen zwei Prozentwerten, nicht eine relative Steigerung.',
        'Es gibt keinen Unterschied, beide Begriffe sind Synonyme.',
        'Prozentpunkte gelten nur im Fußball.',
      ],
      correctIndex: 0,
      explanation: 'Von 50 % auf 54 % sind +4 Prozentpunkte, aber relativ +8 %. Präzision verhindert Fehlinterpretationen.',
    },
    {
      instruction: 'Begrenze die Aussagekraft einer Datenreihe präzise:',
      tiles: ['Da sich die Stichprobe', 'auf eine Gruppe', 'beschränkt,', 'lässt die Zahl', 'keine allgemeine Regel zu.'],
      targetOrder: ['Da sich die Stichprobe', 'auf eine Gruppe', 'beschränkt,', 'lässt die Zahl', 'keine allgemeine Regel zu.'],
      syntaxRule: 'Kausaler Vorfeld-Nebensatz („Da … beschränkt“) → „lässt“ auf Position 2 + „zu“ am Satzende.',
    },
    {
      label: 'Korrelation nicht als Ursache verkaufen',
      before: 'Seit der Änderung haben wir mehr Pässe, also funktioniert die Taktik zu 100 %.',
      diagnosis: 'Setzt zeitliche Koinzidenz mit Kausalbeweis gleich und übertreibt.',
      after: 'Seit der Umstellung stieg zwar die Anzahl kurzer Pässe, doch lässt sich nicht belegen, ob das allein auf die neue Ordnung oder den Gegner zurückgeht.',
      steps: ['Beobachtung benennen (stieg zwar)', 'Einschränkung mit „doch“', 'Alternative Ursache nennen („oder…“)'],
    },
  ),

  's4-challenge': makeDeck(
    's4-challenge',
    'Prämissen-Röntgen: Tatsache vs. Deutung',
    'MATRIX_2X2',
    'Hinter fast jedem starken Urteil versteckt sich eine unausgesprochene Annahme (Prämisse). Wer sie sichtbar macht, entkräftet Pauschalurteile.',
    [
      {
        tag: '1 · DETEKTION',
        title: 'Verdeckte Vorannahme aufspüren',
        caption: 'Sätze wie „Der ist halt so“ oder „Das lag eindeutig an X“ setzen Regeln voraus, die niemand belegt hat.',
        nodes: ['Behauptung', 'Verdeckte Prämisse', 'Alternativen'],
        highlightIndex: 0,
      },
      {
        tag: '2 · GEGENPROBE',
        title: 'Was müsste alternativ gelten?',
        caption: '„Eine andere Erklärung wäre …“ prüft, ob dieselben Fakten ganz anders zustande kommen könnten.',
        nodes: ['Beobachtung', 'Prämisse prüfen', 'Gegenhypothese'],
        highlightIndex: 1,
      },
      {
        tag: '3 · FRAGE',
        title: 'Mit ruhiger Rückfrage entlarven',
        caption: 'Statt frontal zu widersprechen: „Worauf stützt sich die Annahme, dass …?“ öffnet die Diskussion.',
        nodes: ['Rückfrage', 'Belegprüfung', 'Neubewertung'],
        highlightIndex: 2,
      },
    ],
    {
      GENERAL: {
        sceneLabel: 'Medienaussage prüfen',
        avoidLabel: 'Unreflektiert',
        avoidPhrase: 'In der Zeitung stand das so, also stimmt der Zusammenhang.',
        c1Phrase: 'Der Kommentar setzt stillschweigend voraus, dass geringere Nutzung automatisch auf Desinteresse beruht; fehlende Angebote bleiben ausgeblendet.',
        grammarHint: '„setzt stillschweigend voraus, dass …“ benennt die implizite Prämisse.',
      },
      SOCIAL: {
        sceneLabel: '„Der ist halt so“',
        avoidLabel: 'Abgestempelt',
        avoidPhrase: 'Mit Tim brauchst du gar nicht reden, der ist halt stur.',
        c1Phrase: 'Die Aussage „Der ist halt so“ beschreibt keine unveränderliche Tatsache, sondern eine Deutung. Vielleicht fehlten ihm schlicht die Vorabinfos?',
        grammarHint: '„nicht X, sondern Y“ trennt Charakterzuschreibung von Deutung.',
      },
      ENGINEERING: {
        sceneLabel: 'Root-Cause-Annahme',
        avoidLabel: 'Voreilig',
        avoidPhrase: 'Der Sensorwert ist falsch, also muss die Verkabelung defekt sein.',
        c1Phrase: 'Diese Folgerung setzt voraus, dass die Auswertelogik fehlerfrei rechnet; ein Firmware-Skalierungsfehler käme als alternative Ursache ebenso infrage.',
        grammarHint: 'Konjunktiv II („käme … infrage“) führt Gegenhypothese sauber ein.',
      },
      COACHING: {
        sceneLabel: 'U15-Pressingfehler',
        avoidLabel: 'Zuschreibung',
        avoidPhrase: 'Die Abwehr ist einfach zu faul zum Nachrücken.',
        c1Phrase: 'Statt mangelnden Willen zu unterstellen: Möglicherweise fehlte nach dem Ballverlust ein klares Kommando zum gemeinsamen Vorschieben.',
        grammarHint: '„Statt … zu + Infinitiv“ ersetzt Motiv-Unterstellung durch Taktikanalyse.',
      },
    },
    {
      prompt: 'Welche Frage deckt eine verborgene Prämisse sachlich auf?',
      options: [
        'Warum behaupten Sie einfach etwas ohne Ahnung?',
        'Welche Voraussetzung nehmen wir hier eigentlich an, damit dieser Schluss gilt?',
        'Das stimmt doch sowieso nicht.',
      ],
      correctIndex: 1,
      explanation: 'Die Frage nach der notwendigen Voraussetzung („damit … gilt“) lenkt den Blick vom Schlagabtausch auf die Logik.',
    },
    {
      instruction: 'Formuliere eine präzise Prämissen-Prüfung:',
      tiles: ['Diese Schlussfolgerung', 'setzt stillschweigend voraus,', 'dass alle anderen', 'Einflussgrößen', 'konstant geblieben sind.'],
      targetOrder: ['Diese Schlussfolgerung', 'setzt stillschweigend voraus,', 'dass alle anderen', 'Einflussgrößen', 'konstant geblieben sind.'],
      syntaxRule: 'Hauptsatz („setzt … voraus“) + Objekt-Nebensatz mit „dass“ und Verb-Endstellung („geblieben sind“).',
    },
    {
      label: 'Deutung von Beobachtung abkoppeln',
      before: 'Die Mannschaft war unkonzentriert, deshalb haben wir verloren.',
      diagnosis: '„Unkonzentriert“ ist eine psychologische Deutung, keine messbare Spielszene.',
      after: 'In der zweiten Hälfte häuften sich Fehlpässe im Zentrum; ob dies auf Müdigkeit oder stärkeres gegnerisches Pressing zurückgeht, bleibt offen.',
      steps: ['Konkrete Spielszene benennen (Fehlpässe im Zentrum)', 'Zwei konkurrierende Ursachen gegenüberstellen', 'Offenheit markieren'],
    },
  ),

  's5-nuance': makeDeck(
    's5-nuance',
    'Modalpartikel- & Gewissheits-Regler',
    'SPECTRUM',
    'Kleine Wörter wie „doch“, „halt“, „eben“, „wohl“ oder „möglich vs. plausibel vs. belegt“ verschieben den Subtext dramatisch.',
    [
      {
        tag: '1 · PARTIKELN',
        title: 'Ton & Beziehung statt Wortlexikon',
        caption: '„Das weißt du doch“ appelliert oder drängt; „Komm mal her“ mildert ab – Kontext und Betonung entscheiden.',
        nodes: ['Ton / Kontext', 'Modalpartikel', 'Soziale Wirkung'],
        highlightIndex: 0,
      },
      {
        tag: '2 · ABSTUFUNG',
        title: 'Möglich ↔ Plausibel ↔ Belegt',
        caption: 'Zwischen „denkbar“ und „nachgewiesen“ darf im Fachkontext niemals verwischt werden.',
        nodes: ['Möglich', 'Plausibel', 'Nachgewiesen'],
        highlightIndex: 1,
      },
      {
        tag: '3 · INDIREKT',
        title: 'Untertöne lesen & rückfragen',
        caption: 'Regionales Understatement („net schlecht“) oder indirekte Kritik weder überlesen noch überinterpretieren.',
        nodes: ['Wortlaut', 'Subtext-Signal', 'Klärungsfrage'],
        highlightIndex: 2,
      },
    ],
    {
      GENERAL: {
        sceneLabel: 'Vorsichtige Einschätzung',
        avoidLabel: 'Vergröbert',
        avoidPhrase: 'Der Bericht sagt, das Projekt klappt sicher.',
        c1Phrase: 'Die Formulierung „dürfte umsetzbar sein“ drückt eine begründete Zuversicht aus, enthält aber noch keine verbindliche Zusage.',
        grammarHint: 'Modalverb „dürfte“ = wahrscheinliche Einschätzung unter Vorbehalt.',
      },
      SOCIAL: {
        sceneLabel: 'Schwäbisch „net schlecht“',
        avoidLabel: 'Klischeeregel',
        avoidPhrase: 'Im Schwabenland heißt „net schlecht“ immer höchstes Lob.',
        c1Phrase: '„Net schlecht“ kann Anerkennung oder nüchterne Zufriedenheit ausdrücken; am besten fragt man kurz nach, was konkret gut ankam.',
        grammarHint: 'Keine starre Stereotyp-Regel; Funktionsspektrum + Nachfragen.',
      },
      ENGINEERING: {
        sceneLabel: 'Safety-Bewertung',
        avoidLabel: 'Vermischt',
        avoidPhrase: 'Die Hypothese ist plausibel, damit ist der Fehler bewiesen.',
        c1Phrase: 'Eine Hypothese kann technisch plausibel erscheinen und dennoch nicht nachgewiesen sein; für Safety zählt allein der geprüfte Nachweis.',
        grammarHint: 'Konzessiver Abgleich („kann zwar …, und dennoch“) trennt Stufen scharf.',
      },
      COACHING: {
        sceneLabel: 'Indirekte Elternkritik',
        avoidLabel: 'Überhört',
        avoidPhrase: 'Die Mutter hat nur gefragt, wie lang wir noch laufen.',
        c1Phrase: 'Die Frage „Ist das nicht etwas viel für die Jüngeren?“ enthält möglicherweise Sorge vor Überlastung – hier hilft eine ruhige Rückfrage zur Wahrnehmung des Kindes.',
        grammarHint: 'Negative Entscheidungsfrage als indirekte Kritik erkennen.',
      },
    },
    {
      prompt: 'Welcher Begriff beschreibt im Safety-Bericht die stärkste technische Aussage?',
      options: [
        'möglich',
        'plausibel',
        'prüfstandsnachgewiesen',
      ],
      correctIndex: 2,
      explanation: '„Möglich“ schließt nur Logikfehler aus; „plausibel“ passt ins Modell; erst „nachgewiesen“ stützt sich auf verifizierte Evidenz.',
    },
    {
      instruction: 'Ordne den Satz zur Rolle von Modalpartikeln:',
      tiles: ['Die Partikel „doch“', 'ersetzt keinen Beleg,', 'sondern unterstellt meist,', 'dass das Gegenüber', 'bereits zustimmen müsste.'],
      targetOrder: ['Die Partikel „doch“', 'ersetzt keinen Beleg,', 'sondern unterstellt meist,', 'dass das Gegenüber', 'bereits zustimmen müsste.'],
      syntaxRule: 'Kontrast „keinen X, sondern Y“ + Konjunktiv II „müsste“ im untergeordneten dass-Satz.',
    },
    {
      label: 'Abstufung statt Entweder-oder',
      before: 'Die Ergebnisse sind gut, es gab kaum Ausfälle.',
      diagnosis: '„Gut“ und „kaum“ verschleiern die genaue Evidenzstufe.',
      after: 'Die ersten drei Prüfläufe verliefen auffälligkeitsfrei, was die Hypothese stützt; ein Dauerlaufnachweis steht gleichwohl noch aus.',
      steps: ['Konkrete Anzahl nennen (ersten drei)', 'Konjunktionaladverb „gleichwohl“ ergänzt Grenze', 'Präzise Abstufung herstellen'],
    },
  ),

  's4-argument': makeDeck(
    's4-argument',
    '4-Säulen-Argumentationsarchitektur',
    'SCALE',
    'C1-Argumentation hat vier feste Stockwerke: These → Belege (mit Quelle) → Konzessiver Einwand + Antwort → Revisionsbedingung.',
    [
      {
        tag: '1 · THESE & BELEG',
        title: 'Behauptung + überprüfbares Gewicht',
        caption: '„Erstens …, zweitens …“ ordnen Argumente; Beobachtungen werden offen von Messwerten getrennt.',
        nodes: ['Klare These', 'Beleg-Stufen', 'Evidenzgrenze'],
        highlightIndex: 0,
      },
      {
        tag: '2 · GEGENWIND',
        title: 'Stärksten Einwand selbst einladen',
        caption: '„Dem lässt sich entgegenhalten, dass …“ macht deinen Text unangreifbar, weil du Schwächen zuerst selbst begrenzt.',
        nodes: ['Einwand nennen', 'Ernst nehmen', 'Geltung begrenzen'],
        highlightIndex: 1,
      },
      {
        tag: '3 · FAZIT',
        title: 'Bedingtes Urteil mit „sofern“',
        caption: 'Nicht „immer besser“, sondern „spricht mehr dafür, sofern Struktur und Kontrolle gesichert sind“.',
        nodes: ['Abwägung', 'Sofern-Klausel', 'Testtermin'],
        highlightIndex: 2,
      },
    ],
    {
      GENERAL: {
        sceneLabel: 'Stellungnahme',
        avoidLabel: 'Einseitig',
        avoidPhrase: 'Homeoffice ist super und wer dagegen ist, hat unrecht.',
        c1Phrase: 'Zwar reduziert Homeoffice Pendelzeiten spürbar, allerdings erschwert es bei neuen Mitarbeitenden die spontane Einarbeitung; sinnvoll ist daher ein hybrides Modell, sofern feste Teamtage gelten.',
        grammarHint: 'Konzessiv + Folgerung + „sofern“-Bedingung in einem schlüssigen Bogen.',
      },
      SOCIAL: {
        sceneLabel: 'Förderantrag Verein',
        avoidLabel: 'Bauchgefühl',
        avoidPhrase: 'Wir brauchen mobile Tore, das braucht man eben.',
        c1Phrase: 'In sechs protokollierten Einheiten bildeten sich vor großen Toren Warteschlangen; vier mobile Tore würden paralleles 4-gegen-4 ermöglichen – vorbehaltlich einer vierwöchigen Testphase.',
        grammarHint: 'Quelle („in sechs protokollierten Einheiten“) + Konjunktiv II + Vorbehalt.',
      },
      ENGINEERING: {
        sceneLabel: 'EV-Rekuperationsstrategie',
        avoidLabel: 'Lückenhaft',
        avoidPhrase: 'Mehr Rekuperation ist besser für die Reichweite.',
        c1Phrase: 'Obwohl höhere Rekuperation den Energieverbrauch senkt, erfordert sie bei niedriger Fahrbahnhaftung eine Begrenzung über das Stabilitätsprogramm.',
        grammarHint: '„Obwohl … erfordert sie …“ stellt Energiegewinn unter Safety-Vorbehalt.',
      },
      COACHING: {
        sceneLabel: 'U13-Kleinfeld-Konzept',
        avoidLabel: 'Dogmatisch',
        avoidPhrase: 'Linienübungen sind völlig veraltet.',
        c1Phrase: 'Ein kurzes Rondo zum Einstieg bleibt sinnvoll; im Hauptteil überwiegt jedoch das 4-gegen-4, weil es Wahrnehmung und Freilaufen unter Gegnerdruck erzwingt.',
        grammarHint: 'Differenzierte Konzession schützt vor Übertreibung.',
      },
    },
    {
      prompt: 'Wozu dient ein konzessiver Absatz in einer C1-Stellungnahme?',
      options: [
        'Um die eigene These am Ende doch wieder zurückzunehmen.',
        'Um den stärksten Einwand fair einzubeziehen und seine Reichweite sachlich zu begrenzen.',
        'Um möglichst viele schwierige Konjunktionen zu sammeln.',
      ],
      correctIndex: 1,
      explanation: 'Wer Gegenargumente selbst benennt und einordnet, erhöht die Überzeugungskraft enorm.',
    },
    {
      instruction: 'Verknüpfe Konzessivsatz und Hauptsatz in C1-Syntax:',
      tiles: ['Obwohl Kleinfeldspiele', 'ohne Struktur', 'chaotisch wirken können,', 'überwiegen die', 'Lernvorteile deutlich.'],
      targetOrder: ['Obwohl Kleinfeldspiele', 'ohne Struktur', 'chaotisch wirken können,', 'überwiegen die', 'Lernvorteile deutlich.'],
      syntaxRule: 'Konzessivsatz voran (Modalverb „können“ ganz am Nebensatz-Ende) → finites Verb „überwiegen“ direkt nach dem Komma.',
    },
    {
      label: 'Stellungnahme mit Bedingung verankern',
      before: 'Kleinfeldtraining bringt immer bessere Spieler hervor.',
      diagnosis: 'Absolutheitsanspruch ohne Einwand und ohne Qualitätsbedingung.',
      after: 'Das Kleinfeld erhöht zwar die Entscheidungsdichte je Kind, entfaltet seinen Vorteil jedoch nur, sofern jede Einheit einen klaren Schwerpunkt verfolgt.',
      steps: ['Nominalstil („Entscheidungsdichte“)', 'Konzessives „zwar … jedoch“', 'Geltungsbedingung mit „sofern“'],
    },
  ),

  's4-present': makeDeck(
    's4-present',
    '3-Trichter: Komplexität für Laien übersetzen',
    'FUNNEL',
    'Adressatengerecht heißt: Jargon durch ein klares Bild ersetzen – UND die Grenze des Bildes selbst mitliefern.',
    [
      {
        tag: '1 · STRUKTUR',
        title: 'Drei Leitfragen statt Folienflut',
        caption: 'Was leistet es? Woran erkennen wir das? Was ist noch offen? – Zuhörende wissen sofort, wo sie sind.',
        nodes: ['1 · Aufgabe', '2 · Nachweis', '3 · Grenze'],
        highlightIndex: 0,
      },
      {
        tag: '2 · ANALOGIE',
        title: 'Bild + explizite Analogie-Grenze',
        caption: '„Stellen Sie sich X vor – das Bild hat allerdings eine Grenze: …“ verhindert falsche Vereinfachungen.',
        nodes: ['Fachbegriff', 'Alltagsbild', 'Grenze des Bildes'],
        highlightIndex: 1,
      },
      {
        tag: '3 · SCHUTZ',
        title: 'Vorbehalte auch bei Zeitdruck halten',
        caption: 'Lieber eine Detailfolie kürzen als die Einschränkung „Freigabe steht noch aus“ streichen.',
        nodes: ['Kürzungsdruck', 'Kern & Risiko', 'Verständnischeck'],
        highlightIndex: 2,
      },
    ],
    {
      GENERAL: {
        sceneLabel: 'Fachbeitrag für Laien',
        avoidLabel: 'Abgehoben',
        avoidPhrase: 'Die stochastische Varianz der Kohorte erfordert Korrekturfaktoren.',
        c1Phrase: 'Da unsere Befragten überwiegend aus einer Großstadt stammen, dürfen wir das Ergebnis nicht einfach auf ländliche Regionen übertragen.',
        grammarHint: 'Ursache + konkretisierte Schlussfolgerung ohne Jargon.',
      },
      SOCIAL: {
        sceneLabel: 'Vorhaben im Verein',
        avoidLabel: 'Regional verschlossen',
        avoidPhrase: 'Des Bäschtle machen mir mit dene Kicker halt so.',
        c1Phrase: 'Wir starten mit einem kleinen Probelauf: Für alle, die unseren Vereinsausdruck „Kicker“ nicht kennen – gemeint sind die Jugendspieler der E-Jugend.',
        grammarHint: 'Kurze Einschub-Erklärung macht Beteiligung ohne Ausgrenzung möglich.',
      },
      ENGINEERING: {
        sceneLabel: 'ISO 26262 für Beirat',
        avoidLabel: 'Blindes Bild',
        avoidPhrase: 'Die ECU-Überwachung ist einfach wie ein Finanzprüfer.',
        c1Phrase: 'Die zweite Ebene arbeitet ähnlich wie ein Rechnungsprüfer – mit einem entscheidenden Unterschied: Sie muss innerhalb von Millisekunden eingreifen.',
        grammarHint: 'Vergleich („ähnlich wie“) + sofortige Grenze („mit einem Unterschied:“).',
      },
      COACHING: {
        sceneLabel: 'Freilaufen für U12 & Eltern',
        avoidLabel: 'Abstrakt',
        avoidPhrase: 'Wir trainieren heute Raumaufteilung im Halbraum.',
        c1Phrase: 'Stellt euch vor, eure Mitspielerin hat den Ball, aber zwei Gegner stehen vor ihr: Freilaufen heißt, einen offenen Kanal zu schaffen, damit sie euch anspielen kann.',
        grammarHint: 'Konkrete Spielszene + Zweck mit „damit“ statt Taktik-Kauderwelsch.',
      },
    },
    {
      prompt: 'Was darf beim Kürzen eines Fachvortrags unter Zeitdruck NICHT entfallen?',
      options: [
        'Die historische Einführung.',
        'Die entscheidenden Einschränkungen und offenen Sicherheits- oder Evidenzfragen.',
        'Alle Analogien.',
      ],
      correctIndex: 1,
      explanation: 'Wer Vorbehalte streicht, erzeugt bei fachfremden Zuhörenden ein falsches Gefühl absoluter Sicherheit.',
    },
    {
      instruction: 'Verbinde Fachbild und notwendige Grenze:',
      tiles: ['Dieses Bild hilft', 'für das Grundprinzip,', 'hat allerdings', 'eine klare Grenze:', 'Unsere Überwachung reagiert sofort.'],
      targetOrder: ['Dieses Bild hilft', 'für das Grundprinzip,', 'hat allerdings', 'eine klare Grenze:', 'Unsere Überwachung reagiert sofort.'],
      syntaxRule: 'Doppelte Prädikatsklammer („hilft …, hat allerdings …“) vor Doppelpunkt-Auflösung.',
    },
    {
      label: 'Analogie durch Grenzmarkierung schärfen',
      before: 'Die Software-Überwachung funktioniert genau wie ein Schutzengel.',
      diagnosis: 'Verharmlost technische Fehlergrenzen und weckt unrealistische Erwartungen.',
      after: 'Die Überwachung prüft das Signal unabhängig wie ein zweites Augenpaar; im Fehlerfall versetzt sie das Fahrzeug in einen definierten sicheren Zustand, verhindert aber keine mechanischen Schäden.',
      steps: ['Sachliches Bild wählen (zweites Augenpaar)', 'Wirkmechanismus beschreiben', 'Leistungsgrenze mit „verhindert aber keine“ ziehen'],
    },
  ),

  's5-revise': makeDeck(
    's5-revise',
    'Stil-Redigatur: Klarheit, Bezug & Ton',
    'BRACKET',
    'Gute Überarbeitung prüft drei Achsen: eindeutige Pronomen-Bezüge, konsistentes Register und Vermeidung von verstecktem Druck.',
    [
      {
        tag: '1 · BEZUG',
        title: 'Satzbezüge eindeutig klären',
        caption: 'Worauf bezieht sich „sie“ oder „dies“? Eindeutige Nomen verhindern Rätselraten beim Lesen.',
        nodes: ['Unklarer Bezug', 'Präzises Nomen', 'Lesefluss'],
        highlightIndex: 0,
      },
      {
        tag: '2 · KOMPRESSION',
        title: 'Nominalstil dosiert einsetzen',
        caption: '„Nach Prüfung der Daten“ spart Platz, verschleiert aber schnell, WER prüft – aktiv zurückholen, wo Verantwortung zählt.',
        nodes: ['Nominalstil', 'Akteur sichtbar?', 'Passende Balance'],
        highlightIndex: 1,
      },
      {
        tag: '3 · REGISTER',
        title: 'Versteckten Druck herausstreichen',
        caption: '„Du kommst doch sicher“ durch echte Wahlfreiheit ersetzen; Fachdokumentation von Höflichkeitsfloskeln befreien.',
        nodes: ['Drucksignal', 'Offene Frage', 'Ziel-Ton'],
        highlightIndex: 2,
      },
    ],
    {
      GENERAL: {
        sceneLabel: 'Kommentar redigieren',
        avoidLabel: 'Holprig',
        avoidPhrase: 'Die Stadt baute Radwege, was Autofahrer kritisieren, und sie meinen, es staut sich.',
        c1Phrase: 'Obwohl der Ausbau der Radwege vielerorts zu Kritik wegen längerer Staus führt, belegen Zählungen eine steigende Nutzung.',
        grammarHint: 'Saubere Zuordnung von Subjekt („der Ausbau“, „Zählungen“) ohne mehrdeutiges „sie“.',
      },
      SOCIAL: {
        sceneLabel: 'Einladung überarbeiten',
        avoidLabel: 'Drängend',
        avoidPhrase: 'Du kommst doch am Samstag bestimmt auch vorbei, oder?',
        c1Phrase: 'Wir freuen uns, wenn es am Samstag bei dir klappt – gib uns einfach bis Donnerstag kurz Bescheid, ob du dabei bist.',
        grammarHint: 'Statt suggestivem „doch bestimmt“ wird echte Entscheidungsfreiheit formuliert.',
      },
      ENGINEERING: {
        sceneLabel: 'ECU/TCU-Spezifikation',
        avoidLabel: 'Passiv-Verschleiert',
        avoidPhrase: 'Bei Abweichungen wird dann entsprechend reagiert und geklärt.',
        c1Phrase: 'Überschreitet die Differenz die definierte Schwelle, unterbricht der Prüfstand den Lauf und dokumentiert den Fehlercode für die Prüfleitung.',
        grammarHint: 'Konkrete Subjekte („Prüfstand“) statt unklarem „es wird reagiert“.',
      },
      COACHING: {
        sceneLabel: 'U13-Trainingsblatt',
        avoidLabel: 'Überladen',
        avoidPhrase: 'Die Durchführung der Passfolgen-Kontrollübung erfolgt im Dreieck.',
        c1Phrase: 'Drei Spielerinnen bilden ein Dreieck und passen im Uhrzeigersinn; nach jedem Zuspiel läuft die Passgeberin zum freien Hütchen.',
        grammarHint: 'Aktive Verben statt schwerfälliger „-ung“-Nominalisierung für den Platz.',
      },
    },
    {
      prompt: 'Wann schadet übermäßiger Nominalstil einem Text?',
      options: [
        'Wenn dadurch nicht mehr erkennbar ist, wer verantwortlich handelt oder wem etwas obliegt.',
        'Weil Nomen im Deutschen keinen Artikel haben.',
        'Nominalstil ist im C1 grundsätzlich verboten.',
      ],
      correctIndex: 0,
      explanation: 'Nominalisierungen sind kompakt, aber verdecken Akteure („Bei Nichteinhaltung wird reagiert“ → Wer genau reagiert?).',
    },
    {
      instruction: 'Löse eine schwerfällige Passiv-Nominalisierung aktiv auf:',
      tiles: ['Sobald die Differenz', 'den Schwellenwert übersteigt,', 'bricht das System', 'den aktuellen Prüflauf', 'automatisch ab.'],
      targetOrder: ['Sobald die Differenz', 'den Schwellenwert übersteigt,', 'bricht das System', 'den aktuellen Prüflauf', 'automatisch ab.'],
      syntaxRule: 'Temporaler Nebensatz voran → trennbares Verb „abbrechen“ teilt sich: „bricht“ auf Pos. 2, „ab“ am Satzende.',
    },
    {
      label: 'Mehrdeutigkeit & Nominalballast tilgen',
      before: 'Die Prüfung der Daten der Spieler seitens der Trainer führte zu deren Unmut.',
      diagnosis: 'Genitivkette + mehrdeutiges „deren“ (Daten? Spieler? Trainer?).',
      after: 'Als das Trainerteam die Belastungsdaten ohne Vorankündigung auswertete, reagierten mehrere Spieler verunsichert.',
      steps: ['Genitivkette in aktiven „Als“-Satz auflösen', 'Handelnde Subjekte klar trennen', 'Gefühl präzisieren („verunsichert“)'],
    },
  ),

  's2-register': makeDeck(
    's2-register',
    'Register-Regler: Adressat & Medium',
    'SPECTRUM',
    'Dieselbe Information braucht drei völlig verschiedene Sprachkleider: Teamchat (kurz) ↔ Fachbericht (sachlich) ↔ Behörde/Kunde (formell).',
    [
      {
        tag: '1 · ANREDE & DISTANZ',
        title: 'Du/Sie nicht aus Dialekt ableiten',
        caption: 'Soziale Nähe oder regionale Färbung entscheidet nicht automatisch über das „Du“ – Situation und Vereinbarung zählen.',
        nodes: ['Informell (Du)', 'Sachlich-Neutral', 'Formell (Sie)'],
        highlightIndex: 0,
      },
      {
        tag: '2 · WORTWAHL',
        title: 'Umgangssprache vs. Schriftsprache',
        caption: '„läuft nicht rund“ → „weist eine Verzögerung auf“. Im Registerwechsel ändert sich der Ton, nicht die Fakten!',
        nodes: ['Alltagston', 'Fakttreue', 'C1-Präzision'],
        highlightIndex: 1,
      },
      {
        tag: '3 · STRUKTUR',
        title: 'Satzbau ans Zielmedium anpassen',
        caption: 'Elternnachricht: kurz + handlungsorientiert; Zulieferermeldung: präzise Konditionale + Frist.',
        nodes: ['Medium prüfen', 'Satzbau wählen', 'Fakten erhalten'],
        highlightIndex: 2,
      },
    ],
    {
      GENERAL: {
        sceneLabel: 'Behördenanfrage',
        avoidLabel: 'Zu salopp',
        avoidPhrase: 'Hallo, ich wollte mal nachfragen, wo mein Antrag eigentlich hängt.',
        c1Phrase: 'Sehr geehrte Damen und Herren, ich beziehe mich auf meinen Antrag vom 12. Mai und bitte um eine kurze Auskunft zum aktuellen Bearbeitungsstand.',
        grammarHint: 'Formelle Eröffnung + Aktenbezug + höfliche Nominalphrase.',
      },
      SOCIAL: {
        sceneLabel: 'Mit oder ohne „mal“',
        avoidLabel: 'Missverständlich',
        avoidPhrase: 'Kannst du endlich mal das Protokoll schicken?',
        c1Phrase: 'Könntest du mir bei Gelegenheit das Protokoll zusenden? Ohne Eile – ich benötige es erst am Donnerstag.',
        grammarHint: 'Statt gereiztem „endlich mal“ setzt die Zeitangabe Ruhe in den Ton.',
      },
      ENGINEERING: {
        sceneLabel: 'Intern vs. Zulieferer',
        avoidLabel: 'Mischmasch',
        avoidPhrase: 'Hey, die ECU spinnt wieder beim Schalten, schaut euch das mal an.',
        c1Phrase: 'Im aktuellen Prüfstandslauf trat beim Übergang ein reproduzierbarer Momentensprung auf; die Logfiles fügen wir zur gemeinsamen Analyse bei.',
        grammarHint: 'Präzises Fachsubstantiv + sachliches Passiv/Perfekt ohne Schuldzuweisung.',
      },
      COACHING: {
        sceneLabel: 'Absage U11 vs. Eltern',
        avoidLabel: 'Einheitsbrei',
        avoidPhrase: 'Wegen Unwetter entfällt die heutige Sporteinheit ersatzlos.',
        c1Phrase: 'An die Eltern: Aufgrund der Unwetterwarnung sagen wir das Training heute ab; eine Rückmeldung ist nicht nötig. An das Team: Heute leider Pause wegen Regen – am Donnerstag sehen wir uns wieder!',
        grammarHint: 'Zwei gezielte Register für denselben Sachverhalt.',
      },
    },
    {
      prompt: 'Was muss beim Wechsel vom informellen Chat in einen formellen Bericht UNVERÄNDERT bleiben?',
      options: [
        'Die Satzlänge und die Modalpartikeln.',
        'Der sachliche Kern und die nachweisbaren Fakten – ohne Dinge dazuzuerfinden oder wegzulassen.',
        'Die Anrede „Du“.',
      ],
      correctIndex: 1,
      explanation: 'Registerwechsel verändert Tonalität, Wortschatz und Grammatikdichte, niemals den Wahrheitsgehalt.',
    },
    {
      instruction: 'Übertrage eine Meldung ins formelle C1-Register:',
      tiles: ['Bezugnehmend auf', 'unser gestriges Telefonat', 'übermitteln wir Ihnen', 'anbei die', 'überarbeiteten Messdaten.'],
      targetOrder: ['Bezugnehmend auf', 'unser gestriges Telefonat', 'übermitteln wir Ihnen', 'anbei die', 'überarbeiteten Messdaten.'],
      syntaxRule: 'Partizipialkonstruktion im Vorfeld („Bezugnehmend auf …“) → finites Verb „übermitteln“ auf Position 2.',
    },
    {
      label: 'Register-Lift: Chat → Zulieferer',
      before: 'Hi, das Teil macht beim Hochschalten komische Sachen, wir brauchen bis morgen eine Lösung!',
      diagnosis: 'Umgangssprache („macht komische Sachen“) + unrealistisches Ultimatum.',
      after: 'Beim Schaltvorgang unter Teillast wurde am Prüfstand eine Abweichung protokolliert; wir bitten bis morgen um eine erste Einschätzung zum weiteren Prüfvorgehen.',
      steps: ['Vorgang lokalisieren (beim Schaltvorgang)', 'Beobachtung neutralisieren (Abweichung)', 'Machbare Frist setzen (erste Einschätzung)'],
    },
  ),

  's3-negotiate': makeDeck(
    's3-negotiate',
    'Interessen hinter Positionen aufdecken',
    'BRIDGE',
    'Verhandlungen scheitern, wenn Positionen („Ich will A!“) aufeinanderprallen. Erst die dahinterliegenden Interessen öffnen Optionen.',
    [
      {
        tag: '1 · TRENNUNG',
        title: 'Forderung ≠ Motiv',
        caption: 'Position = „Ich brauche den Prüfstand Dienstag“; Interesse = „Ich muss mein Review-Ergebnis rechtzeitig liefern“.',
        nodes: ['Starre Position', 'Dahinter: Interesse', 'Spielraum'],
        highlightIndex: 0,
      },
      {
        tag: '2 · OPTIONEN',
        title: 'Konditionale Angebote bauen',
        caption: '„Wenn wir X vorziehen, könnten wir Y auf Mittwoch legen“ schafft verhandelbare Pakete.',
        nodes: ['Interesse A', 'Konditional-Paket', 'Interesse B'],
        highlightIndex: 1,
      },
      {
        tag: '3 · KRITERIUM',
        title: 'Nicht verhandelbare Grenzen sichern',
        caption: 'Kompromiss heißt nicht „Regeln aufweichen“: Safety- oder Aufsichtsgrenzen bleiben fest verankert.',
        nodes: ['Option wählen', 'Grenze wahren', 'Abnahme'],
        highlightIndex: 2,
      },
    ],
    {
      GENERAL: {
        sceneLabel: 'Termin aushandeln',
        avoidLabel: 'Blockiert',
        avoidPhrase: 'Dienstag geht bei mir nicht, Punkt.',
        c1Phrase: 'Dienstag bin ich gebunden; da Ihnen die frühe Klärung wichtig ist, könnte ich Mittwoch um 8 Uhr oder bereits heute eine Vorabinfo schicken.',
        grammarHint: 'Anerkennung des fremden Interesses („da Ihnen … wichtig ist“) + Doppellösung.',
      },
      SOCIAL: {
        sceneLabel: 'Haushaltsaufgaben',
        avoidLabel: 'Unterstellend',
        avoidPhrase: 'Den Einkauf machst du doch sowieso viel lieber.',
        c1Phrase: 'Mir ist wichtig, dass das Wochenende frei bleibt; übernimmst du eher den Einkauf am Donnerstag, oder passt dir das Kochen am Freitag besser?',
        grammarHint: 'Eigenes Interesse transparent machen + echte Wahl statt „doch sowieso“.',
      },
      ENGINEERING: {
        sceneLabel: 'TCU-Prüfumfang & Safety',
        avoidLabel: 'Riskanter Kompromiss',
        avoidPhrase: 'Na gut, dann lassen wir den Safety-Test einfach weg.',
        c1Phrase: 'Auf den Basis-Safety-Nachweis können wir nicht verzichten; allerdings ließe sich die Komfort-Kalibrierung in ein nachgelagertes Teilpaket auslagern.',
        grammarHint: 'Klare rote Linie + „allerdings ließe sich …“ im Konjunktiv II.',
      },
      COACHING: {
        sceneLabel: 'Platzhälften U13 & U15',
        avoidLabel: 'Machtkampf',
        avoidPhrase: 'Wir sind die Älteren, also gehört uns das ganze Feld.',
        c1Phrase: 'Ihr braucht Länge für das U15-Umschaltspiel, wir benötigen zwei kleine 4-gegen-4-Felder: Teilen wir quer, sodass die U15 die längere Diagonale nutzt?',
        grammarHint: 'Beide Interessen nebeneinanderstellen → räumliche Win-Win-Option.',
      },
    },
    {
      prompt: 'Wodurch unterscheidet sich ein „Interesse“ von einer „Position“?',
      options: [
        'Ein Interesse ist laut, eine Position leise.',
        'Die Position ist die konkrete Forderung, das Interesse das Bedürfnis oder Ziel dahinter.',
        'Interessen spielen nur bei Verträgen eine Rolle.',
      ],
      correctIndex: 1,
      explanation: 'Wer Interessen kennt, findet mehrere Wege, beide Seiten zufriedenzustellen.',
    },
    {
      instruction: 'Verknüpfe gegenseitiges Entgegenkommen mit „sofern“:',
      tiles: ['Wir könnten den', 'Prüfumfang vorziehen,', 'sofern uns die', 'Messdaten bereits', 'am Montag vorliegen.'],
      targetOrder: ['Wir könnten den', 'Prüfumfang vorziehen,', 'sofern uns die', 'Messdaten bereits', 'am Montag vorliegen.'],
      syntaxRule: 'Verhandelbares Angebot im Konjunktiv II („könnten“) + Bedingung mit Verb-Endstellung („vorliegen“).',
    },
    {
      label: 'Von starrer Forderung zur Paketlösung',
      before: 'Ich brauche den ganzen Platz, sonst kann ich nicht trainieren.',
      diagnosis: 'Maximalforderung blockiert Alternative.',
      after: 'Für das geplante Übergangsspiel benötige ich rund sechzig Meter Tiefe; wenn wir die beiden Jugendfelder um zehn Minuten versetzen, passt das für beide Teams.',
      steps: ['Kernbedarf präzisieren (sechzig Meter Tiefe)', 'Zeitversatz als Variable einführen', 'Konjunktiv/Konditional als Angebot'],
    },
  ),

  's3-disagree': makeDeck(
    's3-disagree',
    'Schild des gesichtswahrenden Widerspruchs',
    'SCALE',
    'C1-Widerspruch greift keine Personen an: erst Wertschätzung des Punkts, dann genaue Eingrenzung des Dissenses + Sachgrund.',
    [
      {
        tag: '1 · BRÜCKE',
        title: 'Verstehen vor Gegenrede zeigen',
        caption: '„Ich sehe den Punkt, dass …“ nimmt das Gegenüber ernst, bevor du widersprichst.',
        nodes: ['Anerkennen', 'Abgrenzen', 'Begründen'],
        highlightIndex: 0,
      },
      {
        tag: '2 · KERN',
        title: 'Genau benennen, WAS nicht trägt',
        caption: 'Nicht „alles falsch“, sondern „bei der Schlussfolgerung auf den Inverter gehe ich nicht mit“.',
        nodes: ['Teilbereich', 'Widerspruch', 'Beleglücke'],
        highlightIndex: 1,
      },
      {
        tag: '3 · WEG',
        title: 'Prüffrage statt Rechthaberei',
        caption: '„Welche zusätzliche Messung würde uns beide überzeugen?“ lenkt den Streit auf Kriterien.',
        nodes: ['Alternative', 'Prüf-Vorschlag', 'Gesicht gewahrt'],
        highlightIndex: 2,
      },
    ],
    {
      GENERAL: {
        sceneLabel: 'Empfehlung ablehnen',
        avoidLabel: 'Schroff',
        avoidPhrase: 'Das ist Unsinn, so machen wir das auf keinen Fall.',
        c1Phrase: 'Der Kostenvorteil leuchtet mir ein; allerdings überzeugen mich die Annahmen zur Haltbarkeit noch nicht, sodass ich eine Alternative vorschlage.',
        grammarHint: 'Anerkennung + „allerdings“ + konsekutive Folgerung („sodass“).',
      },
      SOCIAL: {
        sceneLabel: 'Gruppenentscheidung',
        avoidLabel: 'Blockierend',
        avoidPhrase: 'Macht was ihr wollt, ich finde das doof.',
        c1Phrase: 'Ich merke, dass die Mehrheit für den frühen Termin ist – ganz überzeugt bin ich wegen der Anfahrtszeit noch nicht. Können wir einen Puffer einplanen?',
        grammarHint: 'Subjektive Gewichtung („bin ich noch nicht“) + konstruktive Anpassung.',
      },
      ENGINEERING: {
        sceneLabel: 'Inverter-Root-Cause',
        avoidLabel: 'Konfrontativ',
        avoidPhrase: 'Das liegt niemals am Inverter, da liegen Sie völlig falsch!',
        c1Phrase: 'Ich teile Ihre Sorge um den Ausfall; für eine eindeutige Zuordnung zum Inverter fehlen aus meiner Sicht bislang jedoch die Gegenmessungen am Kabelbaum.',
        grammarHint: '„Aus meiner Sicht“ + „bislang jedoch“ lokalisiert den fehlenden Nachweis.',
      },
      COACHING: {
        sceneLabel: 'U12-Stammaufstellung',
        avoidLabel: 'Abwehrend',
        avoidPhrase: 'Ich bin der Trainer und bestimme, wer spielt.',
        c1Phrase: 'Ich verstehe, dass Sie Ihrem Sohn Stabilität wünschen; in der U12 steht für uns allerdings die Rotation auf mehreren Positionen im Vordergrund.',
        grammarHint: 'Elternwunsch anerkennen + Prinzip des Altersbereichs ruhig dagegensetzen.',
      },
    },
    {
      prompt: 'Welcher Einstieg widerspricht klar, ohne die Gesprächsatmosphäre zu vergiften?',
      options: [
        'Das stimmt doch hinten und vorne nicht.',
        'Ich sehe, warum dieser Schluss naheliegt; allerdings tragen unsere Messdaten ihn bisher noch nicht.',
        'Okay, dann machen wir es halt so, auch wenn es falsch ist.',
      ],
      correctIndex: 1,
      explanation: 'Plausibilität anerkennen + konkrete Beleglücke benennen hält den Sachfokus stabil.',
    },
    {
      instruction: 'Formuliere einen sachlichen Einwand:',
      tiles: ['Bei diesem Punkt', 'bin ich noch nicht', 'ganz überzeugt,', 'weil die Gegenprobe', 'am Prüfstand fehlt.'],
      targetOrder: ['Bei diesem Punkt', 'bin ich noch nicht', 'ganz überzeugt,', 'weil die Gegenprobe', 'am Prüfstand fehlt.'],
      syntaxRule: 'Präpositionalobjekt im Vorfeld + ruhige Negation + Kausalsatz mit „weil“ (Verb „fehlt“ am Ende).',
    },
    {
      label: 'Frontalangriff in sachlichen Dissens verwandeln',
      before: 'Sie reden Quatsch, der Inverter hat damit null zu tun!',
      diagnosis: 'Persönliche Abwertung und unbelegte Gegenbehauptung („null zu tun“).',
      after: 'Für eine Festlegung auf den Inverter liegen uns momentan noch keine ausreichenden Messdaten vor; deshalb plädiere ich dafür, zunächst die Eingangsspannung zu prüfen.',
      steps: ['Ich/Sach-Bezug ohne Beleidigung', 'Gegenwärtige Datenlage benennen (momentan noch keine)', 'Nächsten Prüfschritt vorschlagen'],
    },
  ),

  's3-deescalate': makeDeck(
    's3-deescalate',
    'Eskalations-Bremse: Gefühl ≠ Tatsache',
    'LOOP',
    'Anerkennung eines Gefühls („Ich sehe Ihren Ärger“) ist KEIN Schuldeingeständnis für den Vorwurf („Das war Absicht“).',
    [
      {
        tag: '1 · TRENNEN',
        title: 'Emotion würdigen, Behauptung offenlassen',
        caption: '„Dass der Stillstand Sie belastet, nehme ich ernst. Ob es am Bauteil liegt, prüfe ich.“',
        nodes: ['Gefühl würdigen', 'Behauptung offen', 'Grenze / Termin'],
        highlightIndex: 0,
      },
      {
        tag: '2 · EINGESTEHEN',
        title: 'Berechtigte Teilkritik offen zugeben',
        caption: 'Ein klares „Da haben wir Sie nicht rechtzeitig informiert“ nimmt sofort Luft aus dem Streit.',
        nodes: ['Eigener Fehler?', 'Konkret zugeben', 'Kein „immer/nie“'],
        highlightIndex: 1,
      },
      {
        tag: '3 · SCHÜTZEN',
        title: 'Ton ansprechen & Rahmen wechseln',
        caption: 'Bei Beschimpfung oder Vorwürfen im Teamkreis ruhig vertagen und Gespräch unter vier Augen anbieten.',
        nodes: ['Ton markieren', 'Rahmen wählen', 'Verbindliche Frist'],
        highlightIndex: 2,
      },
    ],
    {
      GENERAL: {
        sceneLabel: 'Beschwerde klären',
        avoidLabel: 'Defensiv',
        avoidPhrase: 'Da können wir doch nichts dafür, Sie haben falsch bestellt!',
        c1Phrase: 'Ich höre, wie ärgerlich die Verzögerung für Sie ist. Lassen Sie uns kurz prüfen, welche Positionen fehlen, und bis heute 15 Uhr einen Ersatztermin finden.',
        grammarHint: 'Gefühl ansprechen + kooperative Verben („Lassen Sie uns…“).',
      },
      SOCIAL: {
        sceneLabel: '„Jetzt hör halt zu“',
        avoidLabel: 'Gegendruck',
        avoidPhrase: 'Schrei mich nicht so an, dann hör ich auch zu!',
        c1Phrase: 'Ich möchte dir wirklich zuhören; wenn wir beide in ruhigerem Ton sprechen, verstehe ich dein Anliegen sofort besser.',
        grammarHint: 'Bereitschaft signalisieren + Bedingung für Gesprächsatmosphäre nennen.',
      },
      ENGINEERING: {
        sceneLabel: 'Zulieferer unter Druck',
        avoidLabel: 'Schuldabwälzung',
        avoidPhrase: 'Ihr Prüfstand ist wahrscheinlich kaputt.',
        c1Phrase: 'Dass drei Stillstände extrem belasten, verstehe ich. Ob Steuergerät oder Prüfstands-Spannung ursächlich ist, klären wir gemeinsam anhand der Logfiles bis Freitag.',
        grammarHint: 'Vorangestellter dass-/ob-Satz trennt Verständnis von technischer Ursache.',
      },
      COACHING: {
        sceneLabel: 'Elterngespräch U14',
        avoidLabel: 'Öffentlicher Streit',
        avoidPhrase: 'Ben strengt sich im Training halt nicht an!',
        c1Phrase: 'Sie wünschen sich mehr Spielzeit für Ben und sind enttäuscht – das verstehe ich. Lassen Sie uns die Einsatzkriterien morgen in Ruhe unter vier Augen besprechen.',
        grammarHint: 'Gefühl benennen + Orts-/Zeitwechsel schützt Spieler und Gruppe.',
      },
    },
    {
      prompt: 'Warum darf man einen Satz wie „Sie haben Ben absichtlich benachteiligt“ nicht pauschal mit „Das verstehe ich“ beantworten?',
      options: [
        'Weil „Das verstehe ich“ so klingen kann, als würde man dem Vorwurf der Absicht zustimmen.',
        'Weil man das Wort „verstehen“ im C1 vermeiden muss.',
        'Weil Eltern grundsätzlich kein Mitspracherecht haben.',
      ],
      correctIndex: 0,
      explanation: 'Benenne explizit WAS du verstehst (Enttäuschung/Ärger) und WAS offen bleibt (Vorwurf der Absicht).',
    },
    {
      instruction: 'Trenne Empathie und Sachstatus grammatisch sauber:',
      tiles: ['Dass der Stillstand', 'Sie belastet,', 'nehme ich ernst;', 'ob das Gerät', 'ursächlich ist, bleibt offen.'],
      targetOrder: ['Dass der Stillstand', 'Sie belastet,', 'nehme ich ernst;', 'ob das Gerät', 'ursächlich ist, bleibt offen.'],
      syntaxRule: 'Parallele Vorfeldsätze („Dass…“ vs. „Ob…“) ziehen jeweils direkt das finite Verb hinter das Komma!',
    },
    {
      label: 'Teilkritik ohne Verteidigung annehmen',
      before: 'Sie haben uns nicht informiert, das stimmt ja gar nicht – eine Mail ging raus!',
      diagnosis: 'Rechthaberei über Details verschärft Eskalation, wenn Info beim Empfänger nicht ankam.',
      after: 'Hätten wir die neue Regel direkt vor Trainingsbeginn angekündigt, wäre die Verunsicherung vermeidbar gewesen; das übernehme ich bis morgen.',
      steps: ['Konjunktiv II Vergangenheit („Hätten wir…“) für Selbstkritik', 'Wirkung beim anderen anerkennen', 'Nächste Handlung mit Frist setzen'],
    },
  ),

  's3-agree': makeDeck(
    's3-agree',
    '4-Eckpfeiler einer belastbaren Vereinbarung',
    'MATRIX_2X2',
    'Freundliche Floskeln („Schauen wir mal“) werden erst zur Vereinbarung, wenn Wer, Was, Bis wann und Prüfkriterium stehen.',
    [
      {
        tag: '1 · WER & WAS',
        title: 'Verantwortung namentlich klären',
        caption: '„Jemand“ erledigt nichts. Name + konkrete Aufgabe + Vertretung verhindern das Verantwortungs-Vakuum.',
        nodes: ['Wer? (Name)', 'Was genau?', 'Bis wann?'],
        highlightIndex: 0,
      },
      {
        tag: '2 · KONKRET',
        title: 'Aus „Passt scho“ Termin machen',
        caption: '„Damit ich es richtig einplanen kann: Steht Dienstag fest?“ wandelt Wohlwollen höflich in Klarheit.',
        nodes: ['Floskel hören', 'Freundlich prüfen', 'Festes Datum'],
        highlightIndex: 1,
      },
      {
        tag: '3 · CHECK',
        title: 'Erfolgskriterium & Ausnahme schriftlich',
        caption: 'Kurze Bestätigungsnotiz + Regelung für Ausnahmen + Termin zur Überprüfung nach 4 Wochen.',
        nodes: ['Schriftnotiz', 'Ausnahmefall', 'Prüf-Termin'],
        highlightIndex: 2,
      },
    ],
    {
      GENERAL: {
        sceneLabel: 'Reparatur vereinbaren',
        avoidLabel: 'Unverbindlich',
        avoidPhrase: 'Also bis irgendwann nächste Woche dann!',
        c1Phrase: 'Ich halte kurz fest: Herr Berg tauscht das Ventil bis Donnerstag, 14 Uhr; falls ein Ersatzteil fehlt, informieren Sie mich bitte bis Mittwochvormittag.',
        grammarHint: 'Zusammenfassung mit Uhrzeit + explizite Ausnahme-Bedingung („falls“).',
      },
      SOCIAL: {
        sceneLabel: '„Schauen wir mal“',
        avoidLabel: 'Scheinzusage',
        avoidPhrase: 'Prima, dann kommst du am Dienstag!',
        c1Phrase: 'Danke dir! Damit ich planen kann: Heißt „schauen wir mal“, dass du es noch prüfen musst, und wann gibst du mir kurz Bescheid?',
        grammarHint: 'Neutrales Nachfragen übersetzt Höflichkeit ohne Konfrontation.',
      },
      ENGINEERING: {
        sceneLabel: 'TCU-Prüfplan',
        avoidLabel: 'Lückenhaft',
        avoidPhrase: 'Testfälle machen wir fertig, wenn der Prüfstand frei ist.',
        c1Phrase: 'Frau Demir liefert die Testfälle bis Montag 12 Uhr; Herr Haug klärt heute bis 16 Uhr die Prüfstandsbuchung für Dienstag.',
        grammarHint: 'Beidseitige Verantwortlichkeit mit Namen und exakten Fristen.',
      },
      COACHING: {
        sceneLabel: 'U11-Abholregel',
        avoidLabel: 'Vage',
        avoidPhrase: 'Alle Eltern passen bitte besser auf.',
        c1Phrase: 'Abholung ist dienstags bis 18:45 Uhr am Kabineneingang; bei Verspätung bitte bis 18:30 Uhr Nachricht senden, nach vier Wochen prüfen wir gemeinsam.',
        grammarHint: 'Ort + Zeit + Meldefrist + Revisionsdatum in einem Absatz.',
      },
    },
    {
      prompt: 'Welcher Bestandteil macht aus einer guten Absicht erst eine überprüfbare Vereinbarung?',
      options: [
        'Dass alle freundlich nicken.',
        'Benannte Person, konkretes Ergebnis, fester Termin und vereinbartes Erfolgskriterium.',
        'Dass niemand mehr Ausnahmen ansprechen darf.',
      ],
      correctIndex: 1,
      explanation: 'Erst Wer-Was-Bis-wann plus schriftliche Bestätigung verhindert, dass sich jede Seite etwas anderes merkt.',
    },
    {
      instruction: 'Verfasse eine verbindliche Rückversicherungs-Formel:',
      tiles: ['Ich fasse unsere', 'Vereinbarung kurz zusammen:', 'Frau Demir sendet', 'die Daten bis', 'morgen um 16 Uhr.'],
      targetOrder: ['Ich fasse unsere', 'Vereinbarung kurz zusammen:', 'Frau Demir sendet', 'die Daten bis', 'morgen um 16 Uhr.'],
      syntaxRule: 'Doppelpunkt als visueller Strukturanker; Zeitangabe mit Präposition „bis“ definiert klare Deadline.',
    },
    {
      label: 'Floskel in klare Vereinbarung umgießen',
      before: 'Ja, passt scho, wir machen das mit dem Elternabend irgendwie.',
      diagnosis: 'Keine Person, kein Kanal, keine Rückmeldung, keine Kontrollfrist.',
      after: 'Ich schicke die vier Punkte bis morgen früh in die Elterngruppe und bitte um Rückmeldung „Gelesen“ bis Sonntag; bei Einwänden klären wir eine Vertretung.',
      steps: ['Handelnden festlegen (Ich)', 'Kanal & Rückmeldewort festlegen', 'Bedingung für Ausnahmen ergänzen'],
    },
  ),

  's5-spontaneous': makeDeck(
    's5-spontaneous',
    'Spontan-Anker: Zeit gewinnen & umbauen',
    'PIPELINE',
    'Spontane C1-Flüssigkeit bedeutet nicht „nie stocken“, sondern elegant Zeit kaufen, den Punkt setzen und sich notfalls hörergerecht korrigieren.',
    [
      {
        tag: '1 · ANKERN',
        title: 'Denkpause sprachlich füllen',
        caption: 'Statt „ähm…“ oder Schweigen: „Das berührt zwei Ebenen – lassen Sie mich mit X beginnen.“',
        nodes: ['Unerwartet', 'Zeit-Anker', 'Klare Struktur'],
        highlightIndex: 0,
      },
      {
        tag: '2 · REAGIEREN',
        title: 'Erst Kern, dann Einordnung',
        caption: 'Bei Überraschungsfragen direkt antworten („Kurz gesagt: …“), erst danach die Bedingung nachreichen.',
        nodes: ['Direktantwort', 'Begründung', 'Grenze'],
        highlightIndex: 1,
      },
      {
        tag: '3 · REPARATUR',
        title: 'Laufende Selbstkorrektur',
        caption: 'Wer sich mitten im Satz präzisiert („Anders formuliert:“), wirkt kompetenter als jemand, der einen Fehler versteckt.',
        nodes: ['Fehlgriff', 'Umschalten', 'Präzision'],
        highlightIndex: 2,
      },
    ],
    {
      GENERAL: {
        sceneLabel: 'Unerwartete Nachfrage',
        avoidLabel: 'Ausweichend',
        avoidPhrase: 'Äh, gute Frage, da müsste ich jetzt lange überlegen…',
        c1Phrase: 'Ein wichtiger Punkt – kurz vorweg: Gesichert ist der Zeitrahmen, während über das Budget erst am Donnerstag entschieden wird.',
        grammarHint: 'Strukturmarker („kurz vorweg“) + „während“-Gegenüberstellung.',
      },
      SOCIAL: {
        sceneLabel: 'Partikeln im Gespräch',
        avoidLabel: 'Gereizt',
        avoidPhrase: 'Das macht man eben so, fertig!',
        c1Phrase: 'Ich verstehe, warum dir der Ablauf vertraut ist; allerdings hat sich unsere Gruppengröße verdoppelt – was wäre aus deiner Sicht jetzt praktikabel?',
        grammarHint: 'Statt abwürgendem „eben“ den veränderten Faktor benennen.',
      },
      ENGINEERING: {
        sceneLabel: 'ECU/TCU-Zwischenfrage',
        avoidLabel: 'Unstrukturiert',
        avoidPhrase: 'Also beim Drehmoment, da war halt gestern noch was mit der Software…',
        c1Phrase: 'Dazu gibt es zwei Ebenen: Auf Signalebene liegt die Anforderung vor; auf Prüfstandsebene fehlt uns noch die Validierung bei Kälte.',
        grammarHint: 'Zweiteilung („Auf X-Ebene …; auf Y-Ebene …“) ordnet spontan unter Druck.',
      },
      COACHING: {
        sceneLabel: 'Elternfrage am Spielfeldrand',
        avoidLabel: 'Bloßstellend',
        avoidPhrase: 'Ben spielt wenig, weil er gerade nicht gut verteidigt.',
        c1Phrase: 'Zur konkreten Spielidee heute: Wir wollen gegen das hohe Pressing rechts überladen; nach Abpfiff erkläre ich Ihnen gern in Ruhe unsere Einsatzrotation.',
        grammarHint: 'Sachfokus aufs Teamkonzept + Schutz einzelner Jugendspieler.',
      },
    },
    {
      prompt: 'Welcher Satz hilft bei einer überraschenden Fachfrage am effektivsten, Struktur zu gewinnen?',
      options: [
        'Dazu kann ich spontan leider gar nichts sagen.',
        'Lassen Sie mich zwei Aspekte trennen: Erstens den aktuellen Messstand, zweitens die noch offene Prüfung.',
        'Das haben wir doch gestern schon ausführlich besprochen.',
      ],
      correctIndex: 1,
      explanation: 'Durch explizites Zweiteilen („zwei Aspekte trennen“) schaffst du sofort Überblick für dich und die Zuhörenden.',
    },
    {
      instruction: 'Verbinde spontane Selbstreparatur und klare Aussage:',
      tiles: ['Genauer gesagt', 'meine ich nicht', 'einen Verzicht,', 'sondern eine', 'schrittweise Umstellung.'],
      targetOrder: ['Genauer gesagt', 'meine ich nicht', 'einen Verzicht,', 'sondern eine', 'schrittweise Umstellung.'],
      syntaxRule: 'Korrektur-Signal („Genauer gesagt“) leitet nahtlos in die „nicht A, sondern B“-Präzisierung über.',
    },
    {
      label: 'Spontan-Antwort unter Druck strukturieren',
      before: 'Also ich weiß nicht genau, das Drehmoment kommt manchmal später, vielleicht.',
      diagnosis: 'Unsortiert, doppelte Abschwächung („weiß nicht“ + „vielleicht“).',
      after: 'Kurz gesagt: Die Verzögerung ist beobachtet, ihre Ursache prüfen wir bis Freitag – genauer gesagt vergleichen wir zunächst beide Zeitstempel.',
      steps: ['Signalkopf „Kurz gesagt:“ setzen', 'Status in zwei Teilen benennen', 'Mit „genauer gesagt“ nachschärfen'],
    },
  ),

  's2-summarise': makeDeck(
    's2-summarise',
    'Verdichtungs-Trichter ohne Informationsverlust',
    'FUNNEL',
    'Zusammenfassen heißt: Beispiele weglassen, Kernargumente bündeln – aber Bedingungen und Gegenstimmen zwingend mitnehmen!',
    [
      {
        tag: '1 · HERAUSFILTERN',
        title: 'Kern vs. schmückendes Beispiel',
        caption: 'Anekdoten und Wiederholungen fallen weg; Zahlen, Positionen und Kriterien bleiben.',
        nodes: ['Lange Quellen', 'Kernpunkte', 'Einschränkungen'],
        highlightIndex: 0,
      },
      {
        tag: '2 · SYNTHESE',
        title: 'Zwei Quellen vergleichend koppeln',
        caption: 'Statt „Quelle A sagt… Dann Quelle B…“: „Während Quelle A X betont, verweist B auf Y.“',
        nodes: ['Quelle A', 'Verknüpfung', 'Quelle B'],
        highlightIndex: 1,
      },
      {
        tag: '3 · SCHUTZ',
        title: 'Konjunktiv I / Quellenbezug halten',
        caption: '„Laut Bericht …“ markiert, dass es sich um eine wiedergegebene Einschätzung handelt, nicht um dein eigenes Urteil.',
        nodes: ['Indirekt', 'Vorbehalte', 'Zielumfang'],
        highlightIndex: 2,
      },
    ],
    {
      GENERAL: {
        sceneLabel: 'Zwei Sachbeiträge',
        avoidLabel: 'Nacherzählt',
        avoidPhrase: 'Im ersten Text geht es um Strom, und im zweiten Text geht es auch um Strom, nur teurer.',
        c1Phrase: 'Während Beitrag A den Netzausbau als Voraussetzung betont, warnt Beitrag B vor steigenden Kosten für Haushalte – beide fordern jedoch transparente Planung.',
        grammarHint: 'Adversatives „Während“ + gemeinsamer Nenner („beide fordern jedoch“).',
      },
      SOCIAL: {
        sceneLabel: 'Vereinsprotokoll',
        avoidLabel: 'Parteiisch',
        avoidPhrase: 'Alle waren für den Ausflug, nur Max hat genervt.',
        c1Phrase: 'Die Mehrheit befürwortet den Wochenendtermin; Max gab jedoch zu bedenken, dass Schichtarbeitende dann nicht teilnehmen könnten.',
        grammarHint: 'Konjunktiv II („könnten“) + neutrales Verb „gab zu bedenken“.',
      },
      ENGINEERING: {
        sceneLabel: 'FMEA & Fahrdynamik',
        avoidLabel: 'Verkürzt ohne Grenze',
        avoidPhrase: 'Die Fahrtests liefen gut, FMEA ist damit auch grün.',
        c1Phrase: 'Die Fahrdynamikwerte auf trockener Strecke fielen stabil aus; gleichwohl verweist der FMEA-Bericht auf noch ausstehende Nässe-Nachweise.',
        grammarHint: '„gleichwohl“ verhindert unzulässigen Optimismus bei der Verdichtung.',
      },
      COACHING: {
        sceneLabel: 'U15-Beobachtung & Spielerfragen',
        avoidLabel: 'Ungefiltert',
        avoidPhrase: 'Die Spieler haben viel gefragt und beim Spiel haben wir zwei Tore kassiert.',
        c1Phrase: 'Aus Spielbeobachtung und Teamrunde ergeben sich zwei Trainingsschwerpunkte: schnelleres Verschieben nach Ballverlust sowie klarere Zuordnung bei Standards.',
        grammarHint: 'Nominal-Synthese verdichtet zwei Quellen zu konkreten Handlungszielen.',
      },
    },
    {
      prompt: 'Welcher Fehler passiert beim Verdichten komplexer Berichte am häufigsten?',
      options: [
        'Man kürzt Anekdoten weg.',
        'Man streicht auch die Einschränkungen und stellt unsichere Annahmen als glatte Fakten dar.',
        'Man verwendet Konjunktiv I.',
      ],
      correctIndex: 1,
      explanation: 'Gerade für Entscheider sind die Einschränkungen („gilt nur unter Bedingung X“) mindestens so wichtig wie das Hauptresultat.',
    },
    {
      instruction: 'Verknüpfe zwei Quellen in einem vergleichenden Satz:',
      tiles: ['Während die', 'Fahrdynamik-Messung', 'stabil ausfiel,', 'verweist die FMEA auf', 'offene Nässetests.'],
      targetOrder: ['Während die', 'Fahrdynamik-Messung', 'stabil ausfiel,', 'verweist die FMEA auf', 'offene Nässetests.'],
      syntaxRule: 'Adversatives „Während“ im Vorfeld (Verb „ausfiel“ am Nebensatz-Ende) → Verb „verweist“ auf Position 2.',
    },
    {
      label: 'Quellenvergleich statt Aufzählung',
      before: 'Text 1 sagt, dass wir mehr Daten brauchen. Text 2 sagt, dass Daten viel kosten.',
      diagnosis: 'Einfache Reihung ohne logische Spannung zwischen beiden Aussagen.',
      after: 'Obwohl Quelle 1 eine breitere Datenerhebung empfiehlt, mahnt Quelle 2 die entstehenden Erhebungskosten als Begrenzungsfaktor an.',
      steps: ['Konzessives „Obwohl“ spannt Bogen', 'Verbale Redevariation („mahnt … an“)', 'Abhängigkeit sichtbar machen'],
    },
  ),

  's2-mediate': makeDeck(
    's2-mediate',
    'Allparteiliche Vermittlungs-Brücke',
    'BRIDGE',
    'Mediation bedeutet: Beide Perspektiven so fair übersetzen, dass die jeweils andere Seite ihren berechtigten Kern erkennen kann.',
    [
      {
        tag: '1 · ÜBERSETZEN',
        title: 'Schärfe herausnehmen, Anliegen bewahren',
        caption: 'Statt Beleidigungen wörtlich zu wiederholen: „Auf Seite A besteht der Wunsch nach Planbarkeit.“',
        nodes: ['Position A', 'Neutraler Kern', 'Position B'],
        highlightIndex: 0,
      },
      {
        tag: '2 · BRÜCKE',
        title: 'Geteilten Nenner sichtbar machen',
        caption: 'Selbst bei Streit gibt es fast immer ein gemeinsames Ziel (z. B. Sicherheit oder faire Förderung).',
        nodes: ['Anliegen A', 'Gemeinsamkeit', 'Anliegen B'],
        highlightIndex: 1,
      },
      {
        tag: '3 · CHECK',
        title: 'Rückbestätigung beider Seiten',
        caption: '„Habe ich Ihre Punkte so richtig getroffen?“ verhindert, dass eine Seite sich übergangen fühlt.',
        nodes: ['Spiegelung', 'Rückfrage', 'Nächster Schritt'],
        highlightIndex: 2,
      },
    ],
    {
      GENERAL: {
        sceneLabel: 'Öffentliche Debatte',
        avoidLabel: 'Wertung',
        avoidPhrase: 'Die einen sind rückständig, die anderen denken nicht an Kosten.',
        c1Phrase: 'Während eine Seite auf raschen Klimaschutz dringt, stellt die andere Seite die soziale Bezahlbarkeit für Pendler in den Mittelpunkt.',
        grammarHint: 'Symmetrische Gewichtung ohne wertende Adjektive.',
      },
      SOCIAL: {
        sceneLabel: 'Nachbarschaftskonflikt',
        avoidLabel: 'Dialekt-Unsicherheit',
        avoidPhrase: 'Er hat „mir egal“ gebrummelt, also interessiert es ihn nicht.',
        c1Phrase: 'Herr Klein signalisierte knapp, das Thema sei für ihn erledigt; damit wir nichts übersehen, frage ich kurz nach, ob die Lärmzeiten damit für Sie passen.',
        grammarHint: 'Deutung („signalisierte“) + direkte Vergewisserung.',
      },
      ENGINEERING: {
        sceneLabel: 'ECU vs. TCU vermitteln',
        avoidLabel: 'Lagerbildung',
        avoidPhrase: 'Die TCU nervt mit zu vielen Anfragen.',
        c1Phrase: 'Die TCU strebt weichere Schaltübergänge an, während die ECU das thermische Momentenlimit absichern muss – beide verfolgen damit sichere Fahrbarkeit.',
        grammarHint: 'Interessen beider Teams neutral darstellen + gemeinsames Oberziel.',
      },
      COACHING: {
        sceneLabel: 'Spieler · Eltern · Trainer',
        avoidLabel: 'Parteiisch',
        avoidPhrase: 'Die Eltern verstehen halt nichts von Taktik.',
        c1Phrase: 'Eltern wünschen sich sichtbare Förderung und Spielpraxis; das Trainerteam muss zugleich die Balance aller vierzehn Kinder im Blick behalten.',
        grammarHint: '„zugleich“ verbindet berechtigte Bedürfnisse ohne Abwertung.',
      },
    },
    {
      prompt: 'Woran erkennt man gute Sprachmittlung zwischen zwei streitenden Parteien?',
      options: [
        'Man schlägt sich auf die Seite des lauteren Arguments.',
        'Jede Seite erkennt ihr Kernanliegen in der neutralen Wiedergabe des Vermittlers wieder.',
        'Man erfindet schnell einen Kompromiss, ohne die Gründe zu nennen.',
      ],
      correctIndex: 1,
      explanation: 'Allparteilichkeit heißt: Fairness in der Übersetzung, bevor verhandelt wird.',
    },
    {
      instruction: 'Verbinde beide Perspektiven symmetrisch:',
      tiles: ['Während das TCU-Team', 'weiche Übergänge anstrebt,', 'muss das ECU-Team', 'die thermischen Grenzen', 'zuverlässig einhalten.'],
      targetOrder: ['Während das TCU-Team', 'weiche Übergänge anstrebt,', 'muss das ECU-Team', 'die thermischen Grenzen', 'zuverlässig einhalten.'],
      syntaxRule: 'Symmetrische Gegenüberstellung mit „Während“ → Modalverb „muss“ auf Position 2.',
    },
    {
      label: 'Vorwurf in vermitteltes Anliegen übersetzen',
      before: 'Die Kollegen sagen, eure Vorgaben machen alles kaputt und verzögern den Launch.',
      diagnosis: 'Eskalierende Weiterleitung statt neutraler Mediation.',
      after: 'Auf Entwicklerseite besteht die Sorge, dass kurzfristige Änderungen den Zeitplan gefährden; gleichzeitig betont das Prüfteam die notwendige Verifikation.',
      steps: ['Emotionale Zuspitzung neutralisieren', 'Sorge hinter dem Vorwurf benennen', 'Gegenseite mit „gleichzeitig“ einbinden'],
    },
  ),

  's4-ethics': makeDeck(
    's4-ethics',
    'Dilemma-Waage: Wertekonflikt begrenzen',
    'SCALE',
    'Echte Dilemmata haben keine perfekte Lösung: Zwei legitime Werte kollidieren – gefragt ist eine begründete, überprüfbare Begrenzung.',
    [
      {
        tag: '1 · KOLLISION',
        title: 'Beide legitimen Werte benennen',
        caption: 'Nicht „Gut gegen Böse“, sondern z. B. Datenschutz gegen gezielte Förderung oder Lieferdruck gegen Safety.',
        nodes: ['Wert A (legitim)', 'Spannungsfeld', 'Wert B (legitim)'],
        highlightIndex: 0,
      },
      {
        tag: '2 · GEWICHTUNG',
        title: 'Entscheidung durch Regeln einhegen',
        caption: '„Eine Abwägung zugunsten von X ist nur vertretbar, sofern …“ + Tradition („schon immer so“) entlarven.',
        nodes: ['Keine Gewohnheit', 'Schutzmaßnahmen', 'Rechtsvorbehalt'],
        highlightIndex: 1,
      },
      {
        tag: '3 · PROBEPHASE',
        title: 'Reversible Entscheidung mit Termin',
        caption: 'Statt Dauerbeschluss: begrenzte Probephase mit klarem Auswertungsdatum und Beteiligtenstimme.',
        nodes: ['Übergangslösung', 'Prüf-Frist', 'Revision'],
        highlightIndex: 2,
      },
    ],
    {
      GENERAL: {
        sceneLabel: 'Datenschutz vs. Transparenz',
        avoidLabel: 'Einseitig',
        avoidPhrase: 'Datenschutz ist wichtiger, also machen wir gar keine Liste.',
        c1Phrase: 'Einerseits erleichtert eine Kontaktliste Fahrgemeinschaften, andererseits betrifft sie private Nummern; eine rein freiwillige Eintragung schützt beide Interessen.',
        grammarHint: '„Einerseits …, andererseits …“ führt in einen begrenzten Kompromiss.',
      },
      SOCIAL: {
        sceneLabel: 'Teilhabe vs. Tradition',
        avoidLabel: 'Gewohnheits-Falle',
        avoidPhrase: 'Wir feiern abends, des hent mir scho allaweil so gmacht!',
        c1Phrase: 'Dass wir abends gefeiert haben, erklärt die Gewohnheit, begründet aber nicht, warum Schichtarbeitende weiter ausgeschlossen bleiben sollten.',
        grammarHint: 'Vorangestellter „Dass“-Satz entkoppelt Tradition von sachlicher Begründung.',
      },
      ENGINEERING: {
        sceneLabel: 'Safety vs. Kundentermin',
        avoidLabel: 'Termin über Evidenz',
        avoidPhrase: 'In der Simulation lief alles, das passt schon für Montag.',
        c1Phrase: 'Angesichts des ausstehenden Prüfstandsnachweises ist am Montag ausschließlich eine statische Vorführung vertretbar; eine Fahrerprobung folgt erst nach bestandenem Test.',
        grammarHint: '„Angesichts“ + Genitiv markiert das unübergehbare Prüfhindernis.',
      },
      COACHING: {
        sceneLabel: 'U15-Brustgurt-Daten',
        avoidLabel: 'Naiv',
        avoidPhrase: 'Wenn die Eltern unterschreiben, können wir alle Herzdaten sammeln.',
        c1Phrase: 'Selbst bei Einwilligung muss zunächst fachkundig geprüft werden, welche Daten für den Trainingszweck wirklich erforderlich sind.',
        grammarHint: '„Selbst bei … muss …“ grenzt rechtliche Prüfung von bloßer Zustimmung ab.',
      },
    },
    {
      prompt: 'Warum taugt „Das haben wir schon immer so gemacht“ nicht als moralisches oder sachliches Argument?',
      options: [
        'Weil es nur die Vergangenheit beschreibt, aber weder aktuellen Nutzen noch faire Folgen begründet.',
        'Weil Traditionen im C1 verboten sind.',
        'Weil es kein vollständiger deutscher Satz ist.',
      ],
      correctIndex: 0,
      explanation: 'Sein (Gewohnheit) begründet nicht Sollen (richtige Entscheidung unter heutigen Bedingungen).',
    },
    {
      instruction: 'Verknüpfe Genitivpräposition und Geltungsgrenze:',
      tiles: ['Angesichts des', 'ausstehenden Nachweises', 'ist eine Erprobung', 'erst vertretbar,', 'wenn der Test vorliegt.'],
      targetOrder: ['Angesichts des', 'ausstehenden Nachweises', 'ist eine Erprobung', 'erst vertretbar,', 'wenn der Test vorliegt.'],
      syntaxRule: 'Präposition „Angesichts“ verlangt Genitiv („des ausstehenden Nachweises“) → finites Verb „ist“ direkt dahinter.',
    },
    {
      label: 'Wertekonflikt bearbeitbar machen',
      before: 'Sicherheit geht halt immer vor, egal was der Kunde braucht.',
      diagnosis: 'Ignoriert berechtigte Seite (Kundentermin) und bietet keine kreative Lösung.',
      after: 'Um den Kundentermin einzuhalten, ohne Sicherheitsnachweise zu umgehen, begrenzen wir den Montag auf eine statische Präsentation und holen den Fahrtest nach.',
      steps: ['Beide Ziele im „Um … zu / ohne … zu“-Bogen fassen', 'Begrenzte Option formulieren', 'Reihenfolge klären'],
    },
  ),

  's5-facilitate': makeDeck(
    's5-facilitate',
    'Moderation: 3 Stimmen zu 1 Faden verweben',
    'MATRIX_2X2',
    'Moderierende bewerten nicht selbst – sie klären Begriffe, holen leise Stimmen rein und halten Beschlüsse sowie offenen Dissens fest.',
    [
      {
        tag: '1 · KLÄRUNG',
        title: 'Schlüsselbegriffe abgleichen',
        caption: 'ECU, TCU und Safety nutzen oft dasselbe Wort unterschiedlich – erst Bedeutung klären, dann bewerten.',
        nodes: ['Begriffs-Check', 'Rollenblick', 'Ziel des Termins'],
        highlightIndex: 0,
      },
      {
        tag: '2 · EINBINDUNG',
        title: 'Leise Stimmen gezielt öffnen',
        caption: '„Wir haben noch nichts aus Ihrer Perspektive gehört – welches Risiko sehen Sie?“ statt Monologe laufen lassen.',
        nodes: ['Laute Stimme', 'Gezielte Einladung', 'Vollbild'],
        highlightIndex: 1,
      },
      {
        tag: '3 · SICHERUNG',
        title: 'Dissens & Ergebnis im Protokoll trennen',
        caption: 'Nicht übertünchen: „Entschieden ist A; offen bleibt B zwischen Team X und Y bis Freitag.“',
        nodes: ['Zwischenfazit', 'Offener Dissens', 'Verantwortung'],
        highlightIndex: 2,
      },
    ],
    {
      GENERAL: {
        sceneLabel: 'Bürgergespräch',
        avoidLabel: 'Parteiisch',
        avoidPhrase: 'Herr Müller redet viel, hören wir lieber Frau Weber zu.',
        c1Phrase: 'Danke für den Punkt, Herr Müller; damit alle Blickwinkel vorkommen, möchte ich nun auch diejenigen fragen, die sich bisher zurückgehalten haben.',
        grammarHint: 'Dank + Zielangabe („damit alle…“) steuert Redezeit diplomatisch.',
      },
      SOCIAL: {
        sceneLabel: 'Schwäbische Sitzung',
        avoidLabel: 'Bloßstellend',
        avoidPhrase: 'Redet bitte mal Hochdeutsch, sonst versteht man euch nicht.',
        c1Phrase: 'Kurze Frage für unser Protokoll: Mit „mir hent’s b’sproche“ meinten Sie die Abstimmung von letzter Woche, richtig?',
        grammarHint: 'Respektvolle Klärung für das gemeinsame Protokoll ohne Maßregelung.',
      },
      ENGINEERING: {
        sceneLabel: 'FMEA-Review ECU/TCU',
        avoidLabel: 'Unmoderiert',
        avoidPhrase: 'Macht einfach mal eine RPZ von 120 und fertig.',
        c1Phrase: 'Bevor wir die Aktionspriorität festlegen: Auf welche Annahme stützt das TCU-Team die niedrige Auftretensbewertung?',
        grammarHint: 'Offene Prüffrage lenkt Moderation von Bauchgefühl zu Nachweis.',
      },
      COACHING: {
        sceneLabel: 'U15-Saisongespräch',
        avoidLabel: 'Übergehend',
        avoidPhrase: 'Die Jugendlichen sollen jetzt auch mal kurz nicken.',
        c1Phrase: 'Wir haben die Sicht der Eltern gehört; jetzt interessiert mich besonders, wie ihr Spieler die Belastung in den englischen Wochen wahrnehmt.',
        grammarHint: 'Aktive Einladung auf Augenhöhe an jugendliche Stimmen.',
      },
    },
    {
      prompt: 'Wie geht gute Moderation mit einer bleibenden Meinungsverschiedenheit zwischen zwei Fachteams um?',
      options: [
        'Sie stimmt einfach selbst für eine Seite ab.',
        'Sie hält sowohl den erreichten Zwischenstand als auch den offenen Dissens samt Prüfauftrag sauber fest.',
        'Sie streicht das Thema aus dem Protokoll.',
      ],
      correctIndex: 1,
      explanation: 'Sichtbarer Dissens verhindert Scheinharmonie und macht gezielte Nacharbeit möglich.',
    },
    {
      instruction: 'Bündle verschiedene Redebeiträge in ein Zwischenfazit:',
      tiles: ['Fassen wir zusammen:', 'Während Punkt eins', 'einvernehmlich geklärt ist,', 'bleibt die Messfrage', 'bis Freitag offen.'],
      targetOrder: ['Fassen wir zusammen:', 'Während Punkt eins', 'einvernehmlich geklärt ist,', 'bleibt die Messfrage', 'bis Freitag offen.'],
      syntaxRule: 'Imperativ-Formel + Doppelpunkt → „Während“ kontrastiert Geklärtes mit Offenem.',
    },
    {
      label: 'Moderationseingriff bei Dominanz',
      before: 'Sie haben jetzt schon zehn Minuten geredet, lassen Sie endlich andere dran!',
      diagnosis: 'Abrupter Abbruch erzeugt Gegenwehr.',
      after: 'Vielen Dank für die Einordnung; damit wir auch noch die Perspektive der anderen Gruppen einholen, gebe ich das Wort an das Prüfstandsteam weiter.',
      steps: ['Beitrag kurz würdigen', 'Zeit-/Gruppenziel mit „damit“ benennen', 'Übergabe sachlich aussprechen'],
    },
  ),

  's6-general': makeDeck(
    's6-general',
    'Unvorbereiteter Alltags-Transfer',
    'PIPELINE',
    'Neues Thema ohne Musterblatt: 30-Sekunden-Gerüst aufbauen → Position + Abwägung + Konkretisierung liefern → Selbstcheck.',
    [
      {
        tag: '1 · ORIENTIERUNG',
        title: 'Situation & Rolle in 10 Sek. scannen',
        caption: 'Wer ist mein Gegenüber? Was ist meine kommunikative Hauptaufgabe (erklären, abwägen, beruhigen)?',
        nodes: ['Rolle & Ziel', 'Registerwahl', 'Kein Muster'],
        highlightIndex: 0,
      },
      {
        tag: '2 · KERNBAU',
        title: 'Mit Universalgerüsten antworten',
        caption: 'Einordnung → Abwägung („zwar … allerdings“) → konkretes Handlungsangebot tragen in jedem unbekannten Feld.',
        nodes: ['Einordnung', 'Abwägung', 'Nächster Schritt'],
        highlightIndex: 1,
      },
      {
        tag: '3 · KONTROLLE',
        title: 'Selbstcheck nach der Produktion',
        caption: 'Waren Fakten von Vermutungen getrennt? Gab es unklare Zusagen, die rückversichert werden mussten?',
        nodes: ['Fakt vs. Annahme', 'Register gehalten', 'Grenze ehrlich'],
        highlightIndex: 2,
      },
    ],
    {
      GENERAL: {
        sceneLabel: 'Neues Gesellschaftsthema',
        avoidLabel: 'Ungeordnet',
        avoidPhrase: 'Das Thema ist sehr kompliziert, man kann das so oder so sehen.',
        c1Phrase: 'Der Vorschlag berührt zwei Spannungsfelder: Einerseits verbessert er die Erreichbarkeit, andererseits wirft er Fragen der Finanzierung auf.',
        grammarHint: 'Sofortige Strukturierung über zwei benannte Pole statt Allgemeinplatz.',
      },
      SOCIAL: {
        sceneLabel: 'Neue Gruppensituation',
        avoidLabel: 'Unsichere Deutung',
        avoidPhrase: 'Alle schienen einverstanden zu sein.',
        c1Phrase: 'Damit wir wirklich auf demselben Stand sind: Wer übernimmt konkret die Anmeldung, falls es regnet?',
        grammarHint: 'Klärung von Zuständigkeit und Bedingung trotz harmonischer Runde.',
      },
      ENGINEERING: {
        sceneLabel: 'EV-Thema für Laien',
        avoidLabel: 'Fachtermini-Überlast',
        avoidPhrase: 'Die Ladekurve hängt vom SoC und Thermomanagement ab.',
        c1Phrase: 'Je voller eine Batterie bereits ist, desto langsamer nimmt sie weiteren Strom auf – ähnlich wie bei einem Parkplatz, auf dem die letzten Lücken schwerer zu finden sind.',
        grammarHint: '„Je …, desto …“ macht technische Abhängigkeit sofort anschaulich.',
      },
      COACHING: {
        sceneLabel: 'Neue U11-Elternfrage',
        avoidLabel: 'Überfahren',
        avoidPhrase: 'Das machen alle Vereine so.',
        c1Phrase: 'Gute Frage zur Aufsicht: Bis 18:45 Uhr liegt die Verantwortung bei uns am Kabineneingang; danach übernehmen die jeweils benannten Abholpersonen.',
        grammarHint: 'Klare Zeit- und Verantwortungsabgrenzung schafft Vertrauen.',
      },
    },
    {
      prompt: 'Worauf deutet die Struktur „Je …, desto …“ im Erklärtransfer besonders wirksam hin?',
      options: [
        'Auf einen rein zeitlichen Ablauf.',
        'Auf eine proportionale Veränderung („je mehr X, desto weniger/mehr Y“).',
        'Auf eine Vergangenheitshandlung.',
      ],
      correctIndex: 1,
      explanation: '„Je …, desto …“ macht Zusammenhänge sofort logisch und anschaulich.',
    },
    {
      instruction: 'Verknüpfe den Zusammenhang mit „Je …, desto …“:',
      tiles: ['Je voller', 'der Akku bereits ist,', 'desto stärker drosselt', 'das System die', 'aktuelle Ladeleistung.'],
      targetOrder: ['Je voller', 'der Akku bereits ist,', 'desto stärker drosselt', 'das System die', 'aktuelle Ladeleistung.'],
      syntaxRule: 'Nebensatz („Je + Komparativ … ist“) voran → „desto + Komparativ“ + direktes finites Verb („drosselt“).',
    },
    {
      label: 'Spontaner Alltags-Transfer mit Proportionale',
      before: 'Wenn man spät lädt, dauert das Laden eben viel länger.',
      diagnosis: 'Umgangssprachlich („eben“), unpräzise Ursache.',
      after: 'Je höher der aktuelle Ladestand liegt, desto stärker reduziert das Batteriemanagement aus Schutzgründen die Ladegeschwindigkeit.',
      steps: ['Proportional-Bogen „Je …, desto …“', 'Fachbegriff „Ladestand“ statt „spät“', 'Ursache mit „aus Schutzgründen“ einfügen'],
    },
  ),

  's6-specialist': makeDeck(
    's6-specialist',
    'Integrierter Fachtransfer (Verstehen + Produktion)',
    'MATRIX_2X2',
    'Im Fachtransfer greifen Analyse, Fachpräzision und Adressatengerechtigkeit nahtlos ineinander – ohne Fachwissen mit Sprachniveau zu verwechseln.',
    [
      {
        tag: '1 · ANALYSE',
        title: 'Ausgangslage scharf zerlegen',
        caption: 'Welche Fakten stehen fest? Welche Safety-, Fairness- oder Terminannahmen fehlen noch?',
        nodes: ['Faktenbasis', 'Lücken', 'Zielgruppe'],
        highlightIndex: 0,
      },
      {
        tag: '2 · SYNTHESE',
        title: 'Fachlich korrekt & doch verständlich',
        caption: 'Begriffe wie FMEA oder Gegenpressing kurz verorten, sodass Laien wie Experten folgen können.',
        nodes: ['Begriff verorten', 'Abwägen', 'Bedingung'],
        highlightIndex: 1,
      },
      {
        tag: '3 · EVIDENZ',
        title: 'Wissenserhalt ohne Blindflug',
        caption: 'Die Übung dient dem Training; ein echtes C1-Urteil setzt eine unabhängige Rubrikprüfung voraus.',
        nodes: ['Selbstcheck', 'Fremdurteil', 'Transfer-Nachweis'],
        highlightIndex: 2,
      },
    ],
    {
      GENERAL: {
        sceneLabel: 'Fachinfo für Betroffene',
        avoidLabel: 'Unvermittelt',
        avoidPhrase: 'Das neue Gesetz hat Ausnahmen nach Paragraph 4.',
        c1Phrase: 'Die neue Regelung greift ab Herbst; für Haushalte mit bestehendem Vertrag gilt jedoch eine zweijährige Übergangsfrist.',
        grammarHint: 'Direkter Nutzwert für Betroffene nach Semikolon hervorgehoben.',
      },
      SOCIAL: {
        sceneLabel: 'Ehrenamtsprojekt',
        avoidLabel: 'Scheinkonsens',
        avoidPhrase: 'Alle fanden den Plan gut.',
        c1Phrase: 'Auch wenn niemand laut widersprach, klären wir besser vor dem Beschluss, wer die Materialkosten vorstreckt.',
        grammarHint: '„Auch wenn“ deckt stilles Schweigen auf.',
      },
      ENGINEERING: {
        sceneLabel: 'Komplettes Hybrid-Review',
        avoidLabel: 'Vermengt',
        avoidPhrase: 'Die ECU und TCU passen nach FMEA jetzt zur Freigabe.',
        c1Phrase: 'Die bisherigen Prüfstandsläufe zeigen ein stabiles Momentenverhalten; da jedoch die Kälte-Validierung nach FMEA noch aussteht, bleibt die Fahrerprobung begrenzt.',
        grammarHint: 'Kausale Verknüpfung zwischen offenem FMEA-Punkt und begrenztem Scope.',
      },
      COACHING: {
        sceneLabel: 'Taktik & Elterngespräch',
        avoidLabel: 'Getrennte Welten',
        avoidPhrase: 'Beim Training machen wir Rondo, und den Eltern sagen wir nichts.',
        c1Phrase: 'Auf dem Platz setzen wir auf Überzahl-Rondos für schnellere Entscheidungen; gegenüber den Eltern erläutern wir transparent, dass Spielzeit nach Engagement und Entwicklungszielen verteilt wird.',
        grammarHint: 'Adressatenwechsel zwischen Platzansage und Elternkommunikation.',
      },
    },
    {
      prompt: 'Warum darf eine selbstständige Fachübung in der App nicht automatisch als „bestandenes C1-Zertifikat“ gewertet werden?',
      options: [
        'Weil Fachthemen nicht zu C1 gehören.',
        'Weil echte Sprachkompetenz auf C1 erst durch unabhängige menschliche Prüfung an unbekannten Aufgaben belegt wird.',
        'Weil die App nur A1-Wörter kennt.',
      ],
      correctIndex: 1,
      explanation: 'Epistemische Ehrlichkeit trennt internes Selbsttraining strikt von kalibrierter Fremdbeurteilung.',
    },
    {
      instruction: 'Verbinde Fachterminus und Erkläreinschub:',
      tiles: ['Die dreistufige FMEA,', 'also die systematische', 'Fehler- und Risikoanalyse,', 'verlangt bei hoher', 'Bedeutung konkrete Maßnahmen.'],
      targetOrder: ['Die dreistufige FMEA,', 'also die systematische', 'Fehler- und Risikoanalyse,', 'verlangt bei hoher', 'Bedeutung konkrete Maßnahmen.'],
      syntaxRule: 'Eingeschobene Apposition zwischen Kommas („, also … ,“) erklärt den Fachbegriff nahtlos im Satzfluss.',
    },
    {
      label: 'Fachwissen mit adressatengerechtem Einschub',
      before: 'Die Priorität der FMEA triggert direkt das Gate.',
      diagnosis: 'Denglisch-Jargon ohne Einordnung für fachfremde Beteiligte.',
      after: 'Erreicht ein Fehlerrisiko in der FMEA eine hohe Aktionspriorität, löst dies verbindlich eine vorherige Überprüfung vor dem nächsten Projektschritt aus.',
      steps: ['Uneingeleiteten Konditionalsatz („Erreicht…“) einsetzen', 'Deutschen Fachbegriff „Aktionspriorität“ nutzen', 'Prozessfolge klar benennen'],
    },
  ),

  's6-retention': makeDeck(
    's6-retention',
    'Langzeit-Abruf nach Lernpause',
    'LOOP',
    'Erst wenn zwischen Übung und Abruf Tage vergehen und kein Muster vor dir liegt, trennst du echtes Können von Kurzzeitgedächtnis.',
    [
      {
        tag: '1 · ABSTAND',
        title: 'Bewusste Pause zulassen',
        caption: 'Leichtes Vergessen macht den nächsten Abruf anstrengender – genau diese Anstrengung stärkt das Behalten.',
        nodes: ['Erste Übung', 'Pause ≥ 7 Tage', 'Kalter Abruf'],
        highlightIndex: 0,
      },
      {
        tag: '2 · OHNE STÜTZE',
        title: 'Struktur aus dem Kopf abrufen',
        caption: 'Keine Spickzettel: Kriterien, Konnektoren und Registerentscheidung frei rekonstruieren.',
        nodes: ['Keine Vorlage', 'Freie Konstruktion', 'Selbstprüfung'],
        highlightIndex: 1,
      },
      {
        tag: '3 · FEINSCHLIFF',
        title: 'Alte Fehler gezielt abgleichen',
        caption: 'Danach gezielt schauen: Sind typische Stolperfallen (Verbklammer, Partikeln, Übertreibung) verschwunden?',
        nodes: ['Abgleich', 'Fehler weg?', 'Nächster Zyklus'],
        highlightIndex: 2,
      },
    ],
    {
      GENERAL: {
        sceneLabel: 'Nach Pause erklären',
        avoidLabel: 'Abgehackt',
        avoidPhrase: 'Es gibt da ein Problem. Wir müssen das erst testen. Dann sehen wir.',
        c1Phrase: 'Nach dem aktuellen Stand tritt das Problem bislang nur bei hoher Auslastung auf; bevor wir eine Änderung empfehlen, prüfen wir zunächst die Protokolle.',
        grammarHint: 'Flüssige Verknüpfung über Semikolon + Temporalsatz „bevor“.',
      },
      SOCIAL: {
        sceneLabel: 'Konflikt ohne Schablone',
        avoidLabel: 'Aufgesetzt',
        avoidPhrase: 'Ich höre, dass Sie verärgert sind. Ob das stimmt, sage ich nicht.',
        c1Phrase: 'Ich spüre, wie wichtig Ihnen eine schnelle Klärung ist – lassen Sie uns direkt die konkreten Punkte durchgehen, die wir heute festlegen können.',
        grammarHint: 'Natürlicher Ton statt auswendig gelernter Lehrbuchformel.',
      },
      ENGINEERING: {
        sceneLabel: 'Hypothese nach Pause',
        avoidLabel: 'Unvorsichtig',
        avoidPhrase: 'Die neue Hypothese ist: Der Sensor hat eine Macke.',
        c1Phrase: 'Eine denkbare Erklärung wäre eine Drift des Temperatursensors; ob diese Vermutung trägt, lässt sich durch einen Vergleichstest am Referenzfahrzeug prüfen.',
        grammarHint: 'Konjunktiv II + indirekte Frage hält Hypothesenstatus nach Pause präzise.',
      },
      COACHING: {
        sceneLabel: 'Neue U12-Übung & Feedback',
        avoidLabel: 'Unkonkret',
        avoidPhrase: 'Gute Übung heute, macht weiter so.',
        c1Phrase: 'Bei den beiden letzten Ballannahmen fehlte noch der kurze Blick über die Schulter; probiert beim nächsten Durchgang bewusst, schon vor dem Pass zu schauen.',
        grammarHint: 'Konkrete Szene + umsetzbarer nächster Versuch ohne Etikett.',
      },
    },
    {
      prompt: 'Warum fühlt sich ein Abruf nach einer Woche Pause oft schwerer an als direkt nach der Lektion?',
      options: [
        'Weil man dann alles wieder verlernt hat.',
        'Weil das Kurzzeitgedächtnis nicht mehr aushilft und das Gehirn die Struktur aktiv neu aufbauen muss.',
        'Weil Pausen beim Sprachenlernen grundsätzlich schädlich sind.',
      ],
      correctIndex: 1,
      explanation: 'Gerade diese „erwünschte Schwierigkeit“ nach Pausen verankert Sprachmuster dauerhaft.',
    },
    {
      instruction: 'Formuliere den Abrufgrundsatz in C1-Syntax:',
      tiles: ['Gerade weil', 'der zeitversetzte Abruf', 'mehr Anstrengung erfordert,', 'verankert er die', 'Strukturen dauerhafter.'],
      targetOrder: ['Gerade weil', 'der zeitversetzte Abruf', 'mehr Anstrengung erfordert,', 'verankert er die', 'Strukturen dauerhafter.'],
      syntaxRule: 'Verstärkter Kausalsatz („Gerade weil … erfordert“) → finites Verb „verankert“ direkt danach.',
    },
    {
      label: 'Natürlichkeit statt Schablonen-Ton',
      before: 'Ich anerkenne Ihr Gefühl. Ob Ihre Behauptung wahr ist, ist offen. Nennen wir einen Termin.',
      diagnosis: 'Mechanische Aneinanderreihung gelernter Satzbausteine.',
      after: 'Ich verstehe Ihren Ärger sehr gut; gleichzeitig möchte ich die Ursache erst anhand der Daten prüfen, bevor wir über weitere Schritte entscheiden.',
      steps: ['Bausteine mit „gleichzeitig“ verbinden', 'Natürliche Verben wählen („prüfen“) statt Floskeln', 'Zeitbogen mit „bevor“ schließen'],
    },
  ),

  's6-checkpoint': makeDeck(
    's6-checkpoint',
    'Profil-Spiegel: Von Signalen zum Übungsziel',
    'PIPELINE',
    'Ein Diagnoseprofil ist kein Etikett, sondern ein Kompass: Was ist belegt, was ist unbekannt – und welcher eine Schritt folgt daraus?',
    [
      {
        tag: '1 · TRENNEN',
        title: 'Evidenz von blinden Flecken lösen',
        caption: 'Ein starker Lesewert beweist noch keine spontane Sprechsicherheit – jeder Kanal braucht eigene Belege.',
        nodes: ['Belegter Bereich', 'Ungeprüfter Bereich', 'Ehrliches Bild'],
        highlightIndex: 0,
      },
      {
        tag: '2 · ZIELEN',
        title: 'Einen konkreten Engpass wählen',
        caption: 'Nicht zehn Baustellen gleichzeitig: Ein spezifischer Schwerpunkt (z. B. Verbklammer unter Zeitdruck) bringt den größten Hebel.',
        nodes: ['Alle Signale', 'Größter Hebel', 'Übungsplan'],
        highlightIndex: 1,
      },
      {
        tag: '3 · PRÜFEN',
        title: 'Erfolgskriterium vorab festlegen',
        caption: 'In zwei Wochen anhand von zwei neuen, unvorbereiteten Aufgaben prüfen lassen.',
        nodes: ['Übungsfokus', 'Pause', 'Fremd-Check'],
        highlightIndex: 2,
      },
    ],
    {
      GENERAL: {
        sceneLabel: 'Profil auswerten',
        avoidLabel: 'Überinterpretiert',
        avoidPhrase: 'Mein Profil zeigt 80 %, also bin ich auf C1-Niveau.',
        c1Phrase: 'Die Ergebnisse stützen zwar ein solides Textverständnis, lassen über meine spontane Gesprächsführung unter Zeitdruck jedoch noch keine Aussage zu.',
        grammarHint: 'Präzise Abgrenzung der Aussagekraft („lassen … keine Aussage zu“).',
      },
      SOCIAL: {
        sceneLabel: 'Direktheit & Dialekt',
        avoidLabel: 'Pauschal',
        avoidPhrase: 'Ich habe ein Problem mit Schwäbisch und Direktheit.',
        c1Phrase: 'Während ich gesprochene Einladungen gut verstehe, möchte ich gezielt üben, unklare Zusagen wie „schauen wir mal“ freundlich zu konkretisieren.',
        grammarHint: 'Stärke und konkretes Handlungsziel in einem „Während“-Satz gekoppelt.',
      },
      ENGINEERING: {
        sceneLabel: 'Automotive-Safety-Profil',
        avoidLabel: 'Fach- mit Sprachurteil verwechselt',
        avoidPhrase: 'Ich kenne die ISO 26262, also ist mein Fachdeutsch perfekt.',
        c1Phrase: 'Mein technisches Wissen ist gesichert; sprachlich liegt mein nächster Hebel darin, offene Safety-Grenzen unter Zeitdruck ohne Weichmacher zu benennen.',
        grammarHint: 'Saubere Trennung zwischen Sachkompetenz und sprachlicher Präzision.',
      },
      COACHING: {
        sceneLabel: 'Trainer-Profil',
        avoidLabel: 'Undifferenziert',
        avoidPhrase: 'Ich muss insgesamt besser mit Eltern und Spielern reden.',
        c1Phrase: 'Auf dem Platz gelingen knappe Ansagen bereits zuverlässig; im nächsten Schritt trainiere ich ruhiges Paraphrasieren bei schwierigen Elterngesprächen.',
        grammarHint: 'Konkrete Erfolgssituation gegenüber klarem Transferziel.',
      },
    },
    {
      prompt: 'Was ist das wichtigste Prinzip beim Auswerten eines Lernprofils?',
      options: [
        'Ungeprüfte Bereiche mit einem Durchschnittswert auffüllen.',
        'Belegte Leistungen strikt von noch nicht geprüften Bereichen trennen und ein begrenztes Ziel ableiten.',
        'Alle Bereiche gleichzeitig üben, auch wenn sie schon sicher sind.',
      ],
      correctIndex: 1,
      explanation: 'Ehrliche Lückenanalyse verhindert Selbsttäuschung und fokussiert die Lernzeit auf echte Wirkung.',
    },
    {
      instruction: 'Formuliere ein ehrliches, begrenztes Lernfazit:',
      tiles: ['Zwar liegen für', 'das Leseverstehen bereits', 'gute Belege vor,', 'über das spontane Sprechen', 'fehlt jedoch noch ein Urteil.'],
      targetOrder: ['Zwar liegen für', 'das Leseverstehen bereits', 'gute Belege vor,', 'über das spontane Sprechen', 'fehlt jedoch noch ein Urteil.'],
      syntaxRule: '„Zwar … , … jedoch“ stellt gesicherte Evidenz dem noch offenen Bereich klar gegenüber.',
    },
    {
      label: 'Von globalem Lob zu präzisem Übungsplan',
      before: 'Mein Check war gut, ich mache einfach so weiter.',
      diagnosis: 'Kein konkreter Engpass und kein Prüfdatum benannt.',
      after: 'Mein Lese- und Grammatikverständnis ist stabil; in den kommenden zehn Tagen übe ich daher gezielt das freie Einwand-Handling mit anschließender Rubrikprüfung.',
      steps: ['Stärke knapp benennen', 'Zeitlich begrenzten Fokus wählen (zehn Tage)', 'Prüfinstanz festlegen'],
    },
  ),
};

export function getUnitVisualDeck(unitId: string): UnitVisualDeck {
  return VISUAL_LESSON_DECKS[unitId] ?? VISUAL_LESSON_DECKS['s1-explain'];
}
