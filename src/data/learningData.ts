import type {
  ActiveVocabularyItem,
  CoachTermCategory,
  ConversationScenario,
  CrossCuttingCompetence,
  DiagnosticTask,
  DrillCommunicationPattern,
  DualBenchmark,
  ErrorCategory,
  ErrorIntelligenceItem,
  FootballCoachTerm,
  ListeningLaboratoryItem,
  MasteryStage,
  MediationTask,
  SkillCategory,
  SkillScore,
  SpeakingPrompt,
  TacticalScenario,
  VocabularySrsItem,
} from '../types';

export const SKILL_CATEGORY_META: Record<
  SkillCategory,
  { titleDe: string; descriptionDe: string; shortDe: string }
> = {
  HOERVERSTEHEN: {
    titleDe: 'Hörverstehen',
    descriptionDe: 'Verstehen bei natürlichem Sprechtempo, Reduktionen & Dialekten',
    shortDe: 'Hören',
  },
  SPONTANES_SPRECHEN: {
    titleDe: 'Spontanes Sprechen',
    descriptionDe: 'Flüssigkeit, geringe Latenz, spontane Argumentation & Präzision',
    shortDe: 'Sprechen',
  },
  INTERAKTION: {
    titleDe: 'Interaktions-Intelligenz',
    descriptionDe: 'Diskussionen, Unterbrechungen parieren, Nachfragen & Reaktionen',
    shortDe: 'Interaktion',
  },
  LESEVERSTEHEN: {
    titleDe: 'Leseverstehen',
    descriptionDe: 'Komplexe Fachtexte, Feuilleton, implizite Bedeutung & Diskursstruktur',
    shortDe: 'Lesen',
  },
  SCHREIBEN: {
    titleDe: 'Schreib-Präzision',
    descriptionDe: 'Berichte, differenzierte Argumentation, Gutachten & Registerkontrolle',
    shortDe: 'Schreiben',
  },
  GRAMMATIK: {
    titleDe: 'Grammatik-Kontrolle',
    descriptionDe: 'Passiversatzformen, Konjunktiv I/II, Partizipialattribute, Subtilität',
    shortDe: 'Grammatik',
  },
  WORTSCHATZ: {
    titleDe: 'Wortschatz & Kollokationen',
    descriptionDe: 'Nomen-Verb-Verbindungen, idiomatische Treffsicherheit, Nuancen',
    shortDe: 'Wortschatz',
  },
  MEDIATION: {
    titleDe: 'Mediation & Sprachmittlung',
    descriptionDe: 'Komplexe Sachverhalte laienverständlich zusammenfassen & übertragen',
    shortDe: 'Mediation',
  },
  AUSSPRACHE_PROSODIE: {
    titleDe: 'Aussprache & Prosodie',
    descriptionDe: 'Satzmelodie, sinntragende Betonung, Rhythmus & Lautverschmelzungen',
    shortDe: 'Aussprache',
  },
};
export const ALL_SKILL_CATEGORIES: SkillCategory[] = [
  'HOERVERSTEHEN',
  'SPONTANES_SPRECHEN',
  'INTERAKTION',
  'LESEVERSTEHEN',
  'SCHREIBEN',
  'GRAMMATIK',
  'WORTSCHATZ',
  'MEDIATION',
  'AUSSPRACHE_PROSODIE',
];

export const ERROR_CATEGORY_LABELS: Record<ErrorCategory, string> = {
  PRAEPOSITIONEN_KASUS: 'Präpositionen & Kasus',
  KOLLOKATIONEN: 'Kollokationen & Nomen-Verb',
  REGISTER_MISMATCH: 'Register & Tonalität',
  FALSE_FRIENDS_FEINHEITEN: 'False Friends & Feinheiten',
  VERBKLAMMER_SATZSTRUKTUR: 'Satzbau & Verbklammer',
  ZOEGERN_LATENZ: 'Zögern & Füllwörter',
};

export const MASTERY_STAGE_LABELS: Record<MasteryStage, string> = {
  NEU: 'Neu erkannt',
  WIEDERHOLUNG_TAG_1: 'Tag 1 Abruf',
  TRANSFER_TAG_3: 'Tag 3 Transfer',
  GEFESTIGT_TAG_7: 'Tag 7 Festigung',
  GEMEISTERT: 'Gemeistert',
};

export const COACH_TERM_CATEGORY_LABELS: Record<CoachTermCategory, string> = {
  OFFENSIV: 'Offensivspiel',
  DEFENSIV: 'Defensivverbund',
  UMSCHALTEN: 'Umschaltspiel',
  KOMMUNIKATION: 'Coaching-Sprache',
};

export function createInitialSkillProfile(): Record<SkillCategory, SkillScore> {
  return ALL_SKILL_CATEGORIES.reduce(
    (acc, category) => {
      acc[category] = {
        category,
        score: 0,
        lastTestedDaysAgo: 0,
        trend: 0,
        criticalWeakness: 'Noch nicht gemessen; Nullwert ist ein Platzhalter.',
        masteredAreas: [],
      };
      return acc;
    },
    {} as Record<SkillCategory, SkillScore>,
  );
}

export const initialCrossCutting: CrossCuttingCompetence[] = [
  {
    title: 'Pragmatische Kompetenz',
    description: 'Situationsadäquate Tonalität & Diplomatie im Berufs- und Streitgespräch',
    score: 0,
    levelLabel: 'Nicht gemessen',
  },
  {
    title: 'Registerkontrolle',
    description: 'Mühelose Verschiebung zwischen Fachterminologie, Höflichkeit & Umgangssprache',
    score: 0,
    levelLabel: 'Nicht gemessen',
  },
];

export const initialDualBenchmark: DualBenchmark = {
  functionalC1Score: 0,
  functionalReadinessSummary:
    'Noch keine Einstufung. Nullwerte sind Platzhalter, keine gemessene Leistung oder Prüfungsprognose.',
  examC1Score: 0,
  goetheC1Readiness: 0,
  testDafReadiness: 0,
  telcC1Readiness: 0,
};

export const initialErrors: ErrorIntelligenceItem[] = [
  {
    id: 'err_1',
    category: 'KOLLOKATIONEN',
    userMistake: 'Wir müssen eine Entscheidung machen.',
    nativeReformulation: 'Wir müssen eine Entscheidung treffen / herbeiführen.',
    whyItSoundsForeign:
      "'Entscheidung machen' ist eine wörtliche Übersetzung aus dem Englischen ('make a decision'). Im gehobenen Deutsch kollokiert 'Entscheidung' fest mit 'treffen' oder 'fällen'.",
    ruleOrNuance:
      'Feste Nomen-Verb-Verbindung: eine Entscheidung treffen (Standard) / fällen (Gericht/Autorität).',
    contextSentence:
      'In Anbetracht der Frist müssen wir bis morgen eine verbindliche Entscheidung treffen.',
    transferChallengePrompt:
      'Formulieren Sie im neuen Kontext (Investitionsausschuss): Sagen Sie, dass das Gremium heute die endgültige Wahl bezüglich des Standorts beschließen muss.',
    transferTargetAnswer:
      'Das Gremium muss heute eine Richtungsentscheidung bezüglich des Standorts treffen.',
    masteryStage: 'WIEDERHOLUNG_TAG_1',
    occurrencesCount: 4,
    lastOccurredDaysAgo: 1,
  },
  {
    id: 'err_2',
    category: 'PRAEPOSITIONEN_KASUS',
    userMistake: 'Ich beziehe mich über das Schreiben vom 12. Mai.',
    nativeReformulation: 'Ich beziehe mich auf das Schreiben vom 12. Mai (Akkusativ).',
    whyItSoundsForeign:
      "Falsche Präposition. 'Sich beziehen' verlangt obligatorisch 'auf + Akkusativ'. 'Über' wird fälschlich von 'sprechen über' übertragen.",
    ruleOrNuance: 'Sich beziehen AUF (+ Akk.) / verweisen AUF (+ Akk.).',
    contextSentence:
      'Bezugnehmend auf Ihre Anfrage möchten wir Ihnen folgendes Angebot unterbreiten.',
    transferChallengePrompt:
      'Formulieren Sie im neuen Kontext (Rückfrage bei Behörde): Verweisen Sie auf den ursprünglichen Antrag Ihres Mandanten.',
    transferTargetAnswer:
      'Ich beziehe mich hierbei auf den Erstantrag meines Mandanten vom Januar.',
    masteryStage: 'TRANSFER_TAG_3',
    occurrencesCount: 3,
    lastOccurredDaysAgo: 3,
  },
  {
    id: 'err_3',
    category: 'REGISTER_MISMATCH',
    userMistake: 'Die Maschine ist kaputtgegangen und das ist voll blöd für uns.',
    nativeReformulation:
      'Aufgrund eines technischen Defekts an der Anlage ist es zu einem gravierenden Produktionsstillstand gekommen.',
    whyItSoundsForeign:
      "'voll blöd' ist jugendliche Umgangssprache und im geschäftlichen oder technischen Kontext grob deplatziert. C1 verlangt sachliche Präzision.",
    ruleOrNuance:
      "Verbalstil durch präzise Ursache-Wirkung-Relation ersetzen: 'aufgrund eines Defekts kam es zu...'",
    contextSentence:
      'Der Ausfall der Fräsmaschine führte bedauerlicherweise zu unvorhergesehenen Lieferverzögerungen.',
    transferChallengePrompt:
      'Formulieren Sie im neuen Kontext (Lieferverzug beim Kunden): Teilen Sie mit, dass der Server abgestürzt ist, im gehobenen B2B-Register.',
    transferTargetAnswer:
      'Infolge eines unerwarteten Systemausfalls verzögert sich die Auftragsabwicklung um wenige Stunden.',
    masteryStage: 'NEU',
    occurrencesCount: 2,
    lastOccurredDaysAgo: 0,
  },
  {
    id: 'err_4',
    category: 'FALSE_FRIENDS_FEINHEITEN',
    userMistake: 'Eventuell komme ich um 15 Uhr, das ist ganz sicher.',
    nativeReformulation:
      'Möglicherweise komme ich um 15 Uhr / Ich werde voraussichtlich um 15 Uhr eintreffen.',
    whyItSoundsForeign:
      "'Eventuell' bedeutet 'vielleicht / unter Umständen', niemals 'schließlich / at the end' (False Friend zu engl. 'eventually'). Widerspruch zu 'das ist ganz sicher'.",
    ruleOrNuance:
      'eventuell = maybe / possibly. Schließlich / letztendlich = eventually.',
    contextSentence: 'Wir werden das Gutachten voraussichtlich morgen abschließen.',
    transferChallengePrompt:
      "Sagen Sie auf Deutsch: 'After many delays, we eventually signed the contract' – ohne das Wort 'eventuell'!",
    transferTargetAnswer:
      'Nach zahlreichen Verzögerungen haben wir den Vertrag letztendlich unterzeichnet.',
    masteryStage: 'GEFESTIGT_TAG_7',
    occurrencesCount: 5,
    lastOccurredDaysAgo: 7,
  },
  {
    id: 'err_5',
    category: 'FALSE_FRIENDS_FEINHEITEN',
    userMistake: 'Scheinbar hat er den Zug verpasst, denn er steht nicht am Gleis.',
    nativeReformulation:
      'Anscheinend hat er den Zug verpasst, denn er steht nicht am Gleis.',
    whyItSoundsForeign:
      "'Scheinbar' bedeutet, dass etwas nur zum Schein so ist (in Wahrheit aber anders!). Wenn Sie eine wahrscheinliche Vermutung ausdrücken, brauchen Sie 'anscheinend'.",
    ruleOrNuance:
      'anscheinend = allem Anschein nach (wahrscheinlich wahr); scheinbar = nur vorgetäuscht (in Wirklichkeit falsch).',
    contextSentence:
      'Anscheinend wurde der Termin kurzfristig in den Konferenzraum B verlegt.',
    transferChallengePrompt:
      "Korrigieren Sie: 'Die Zahlen sehen scheinbar gut aus, also haben wir Gewinn gemacht.'",
    transferTargetAnswer:
      'Die Zahlen sehen anscheinend gut aus, also haben wir Gewinn gemacht.',
    masteryStage: 'NEU',
    occurrencesCount: 3,
    lastOccurredDaysAgo: 1,
  },
  {
    id: 'err_6',
    category: 'VERBKLAMMER_SATZSTRUKTUR',
    userMistake: 'Obwohl wir hatten wenig Zeit, das Ergebnis ist überzeugend.',
    nativeReformulation: 'Obwohl wir wenig Zeit hatten, ist das Ergebnis überzeugend.',
    whyItSoundsForeign:
      "Im schriftlichen und formellen C1-Deutsch verlangt 'obwohl' zwingend Verbendstellung im Nebensatz und Inversion (Verb-Subjekt) im anschließenden Hauptsatz.",
    ruleOrNuance:
      'Nebensatz (Verb am Ende) + Hauptsatz (finites Verb direkt nach dem Komma auf Position 1).',
    contextSentence:
      'Obwohl die Finanzierung noch aussteht, schreiten die Planungen planmäßig voran.',
    transferChallengePrompt:
      "Verbinden Sie mit 'obwohl': 'Die Frist war knapp. Wir haben pünktlich geliefert.'",
    transferTargetAnswer: 'Obwohl die Frist knapp war, haben wir pünktlich geliefert.',
    masteryStage: 'WIEDERHOLUNG_TAG_1',
    occurrencesCount: 4,
    lastOccurredDaysAgo: 2,
  },
  {
    id: 'err_7',
    category: 'PRAEPOSITIONEN_KASUS',
    userMistake: 'Wegen dem schlechten Wetter und trotz den Problemen kamen alle.',
    nativeReformulation:
      'Wegen des schlechten Wetters und trotz der Probleme kamen alle.',
    whyItSoundsForeign:
      "Im gesprochenen Alltag hört man oft den Dativ, doch in C1-Prüfungen und Business-Texten verlangen 'wegen', 'trotz', 'während' und 'aufgrund' konsequent den Genitiv.",
    ruleOrNuance:
      'Präpositionen mit Genitiv: wegen, trotz, aufgrund, anhand, ungeachtet, binnen.',
    contextSentence:
      'Ungeachtet der heftigen Kritik hielt der Vorstand an seiner Strategie fest.',
    transferChallengePrompt:
      "Setzen Sie in den Genitiv: 'Aufgrund (die neuen Vorschriften) müssen wir umplanen.'",
    transferTargetAnswer: 'Aufgrund der neuen Vorschriften müssen wir umplanen.',
    masteryStage: 'NEU',
    occurrencesCount: 6,
    lastOccurredDaysAgo: 0,
  },
  {
    id: 'err_8',
    category: 'VERBKLAMMER_SATZSTRUKTUR',
    userMistake: 'Die Kollegin, dessen Namen ich vergessen habe, hat angerufen.',
    nativeReformulation:
      'Die Kollegin, deren Namen ich vergessen habe, hat angerufen.',
    whyItSoundsForeign:
      "Viele Lernende verwechseln im Relativsatz 'deren' (feminin Singular & Plural) mit 'dessen' (maskulin/neutrum Singular).",
    ruleOrNuance:
      'mask./neutr. Sg. -> dessen | fem. Sg. & alle Plurale -> deren (ohne Endung vor dem Nomen).',
    contextSentence:
      'Das Unternehmen, dessen Umsatz stark gestiegen ist, expandiert nach Wien.',
    transferChallengePrompt:
      "Ergänzen Sie das Relativpronomen: 'Die Abteilung, ___ Budget gekürzt wurde, protestiert.'",
    transferTargetAnswer: 'Die Abteilung, deren Budget gekürzt wurde, protestiert.',
    masteryStage: 'TRANSFER_TAG_3',
    occurrencesCount: 2,
    lastOccurredDaysAgo: 3,
  },
  {
    id: 'err_9',
    category: 'REGISTER_MISMATCH',
    userMistake:
      'Der Chef hat gesagt, dass wir machen müssen das Projekt besser als wie letztes Jahr.',
    nativeReformulation:
      'Die Abteilungsleitung betonte, wir müssten das Projekt effizienter als im Vorjahr umsetzen.',
    whyItSoundsForeign:
      "Doppelter Fehler: 'als wie' ist regional-umgangssprachlich falsch (Komparativ immer nur 'als'), und in der indirekten Wiedergabe wirkt der Konjunktiv I/II deutlich souveräner.",
    ruleOrNuance:
      'Ungleichheit (Komparativ): größer ALS | Gleichheit: genauso groß WIE.',
    contextSentence:
      'Die diesjährige Bilanz fällt deutlich erfreulicher aus als die des Vorjahres.',
    transferChallengePrompt:
      "Korrigieren Sie: 'Dieses Verfahren ist doppelt so schnell als wie das alte.'",
    transferTargetAnswer: 'Dieses Verfahren ist doppelt so schnell wie das alte.',
    masteryStage: 'NEU',
    occurrencesCount: 3,
    lastOccurredDaysAgo: 1,
  },
  {
    id: 'err_10',
    category: 'KOLLOKATIONEN',
    userMistake: 'Wir müssen diese Frage jetzt in Betracht nehmen.',
    nativeReformulation:
      'Wir müssen diese Option jetzt in Betracht ziehen / in Erwägung ziehen.',
    whyItSoundsForeign:
      "Kontamination zweier Nomen-Verb-Verbindungen ('in Kauf nehmen' + 'in Betracht ziehen'). Deutsche Muttersprachler bemerken verwechselte Funktionsverben sofort.",
    ruleOrNuance:
      'in Betracht ZIEHEN | in Kauf NEHMEN | zur Sprache BRINGEN | Rechnung TRAGEN.',
    contextSentence:
      'Wir sollten auch eine schrittweise Einführung in Betracht ziehen.',
    transferChallengePrompt:
      "Wählen Sie das passende Verb: 'Bei der Qualität dürfen wir keine Abstriche ___.'",
    transferTargetAnswer: 'Bei der Qualität dürfen wir keine Abstriche machen.',
    masteryStage: 'WIEDERHOLUNG_TAG_1',
    occurrencesCount: 4,
    lastOccurredDaysAgo: 2,
  },
  {
    id: 'err_11',
    category: 'ZOEGERN_LATENZ',
    userMistake:
      'Ähm... also... ich denke, vielleicht könnten wir eventuell sagen, dass...',
    nativeReformulation:
      'Meines Erachtens lässt sich festhalten, dass... / Aus meiner Sicht spricht vieles dafür, dass...',
    whyItSoundsForeign:
      "Mehrfache Heckenausdrücke ('ich denke, vielleicht, eventuell') + Pausenfüller ('ähm') signalisieren mangelnde Überzeugungskraft im C1-Gespräch.",
    ruleOrNuance:
      "Einstiegsanker fest automatisieren: 'Lassen Sie mich dazu Folgendes anmerken:' verschafft 2 Sekunden Denkzeit ohne 'ähm'.",
    contextSentence:
      'Hierbei gilt es jedoch zu bedenken, dass die Folgekosten noch nicht beziffert sind.',
    transferChallengePrompt:
      "Starten Sie eine spontane Erwiderung ohne 'Ich denke' oder 'vielleicht': Thema Klimaneutralität.",
    transferTargetAnswer:
      'In Anbetracht der aktuellen Datenlage steht außer Frage, dass wir schneller handeln müssen.',
    masteryStage: 'NEU',
    occurrencesCount: 5,
    lastOccurredDaysAgo: 0,
  },
  {
    id: 'err_12',
    category: 'PRAEPOSITIONEN_KASUS',
    userMistake:
      'Ich interessiere mich nicht für Politik, sondern ich beschäftige mich über Wirtschaft.',
    nativeReformulation:
      'Ich interessiere mich weniger für Politik, sondern befasse mich vorrangig mit Wirtschaftsfragen.',
    whyItSoundsForeign:
      "'Sich beschäftigen / befassen' verlangt zwingend 'mit + Dativ', niemals 'über'.",
    ruleOrNuance:
      'sich beschäftigen MIT (+ Dat.) | sich befassen MIT (+ Dat.) | sich auseinandersetzen MIT (+ Dat.).',
    contextSentence:
      'Der Ausschuss befasst sich morgen eingehend mit den neuen Compliance-Richtlinien.',
    transferChallengePrompt:
      "Ergänzen Sie: 'Wir müssen uns dringend ___ diesem Engpass auseinandersetzen.'",
    transferTargetAnswer:
      'Wir müssen uns dringend mit diesem Engpass auseinandersetzen.',
    masteryStage: 'GEFESTIGT_TAG_7',
    occurrencesCount: 2,
    lastOccurredDaysAgo: 5,
  },
  {
    id: 'err_13',
    category: 'REGISTER_MISMATCH',
    userMistake:
      'Spiel nicht so hektisch! Schieß nicht einfach blind nach vorne!',
    nativeReformulation:
      'Nimm dir ruhig die Sekunde am Ball, schau hoch und such die nächste Anspielstation!',
    whyItSoundsForeign:
      "Negative Imperative ('mach nicht...') erzeugen bei E- und C-Jugendlichen Stress und Blockaden, statt ein klares Bewegungsbild zu liefern.",
    ruleOrNuance:
      "DFB-Coaching-Prinzip: Positive Handlungsaufforderung statt Verbot ('Kopf hoch, Tiefe suchen' statt 'Nicht blind bolzen').",
    contextSentence:
      'Bleib mutig im Eins-gegen-Eins und biete dich nach dem Pass sofort wieder im freien Raum an.',
    transferChallengePrompt:
      "Formen Sie um: 'Verlier den Ball nicht im Dribbling!' -> positives E-Jugend-Kommando.",
    transferTargetAnswer:
      'Schütze den Ball mit dem Körper und halte den Blick für deinen Mitspieler offen!',
    masteryStage: 'NEU',
    occurrencesCount: 3,
    lastOccurredDaysAgo: 1,
  },
  {
    id: 'err_14',
    category: 'KOLLOKATIONEN',
    userMistake:
      'Wir müssen jetzt mehr Speed machen und vorne die Defender attackieren.',
    nativeReformulation:
      'Wir müssen jetzt das Tempo anziehen und die gegnerische Abwehrreihe geschlossen anlaufen.',
    whyItSoundsForeign:
      "Unreflektierte Anglizismen ('Speed machen', 'Defender') wirken in der deutschen Trainerausbildung (DFB-C-Lizenz) unsauber.",
    ruleOrNuance:
      'Echte deutsche Fußballfachsprache nutzen: anlaufen, verschieben, umschalten, Halbraum besetzen, Tiefe sichern.',
    contextSentence:
      'Bei Ballverlust schalten wir geschlossen um und setzen den ballführenden Gegenspieler sofort unter Druck.',
    transferChallengePrompt:
      "Ersetzen Sie 'high pressing & drop back' durch präzises C-Jugend-Trainerdeutsch.",
    transferTargetAnswer:
      'Wir laufen den Gegner hoch an und fallen bei Überspielen der ersten Kette geschlossen ins Mittelfeldpressing zurück.',
    masteryStage: 'NEU',
    occurrencesCount: 2,
    lastOccurredDaysAgo: 2,
  },
  {
    id: 'err_15',
    category: 'FALSE_FRIENDS_FEINHEITEN',
    userMistake: 'Aktuell habe ich das nicht so gemeint, sondern wollte nur nachfragen.',
    nativeReformulation:
      'Eigentlich habe ich das nicht so gemeint, sondern wollte lediglich rückversichern.',
    whyItSoundsForeign:
      "'Aktuell' bedeutet auf Deutsch 'gegenwärtig / derzeit' (current), niemals 'tatsächlich / eigentlich' (engl. 'actually').",
    ruleOrNuance:
      'aktuell = currently / at present | eigentlich / tatsächlich = actually / in fact.',
    contextSentence:
      'Tatsächlich handelt es sich hierbei um eine vorläufige Arbeitshypothese, nicht um einen Befund.',
    transferChallengePrompt:
      "Korrigieren Sie im Gespräch: 'Aktuell ist die ECU-Software doch gar nicht freigegeben!' -> im Sinn von 'in fact'.",
    transferTargetAnswer:
      'Tatsächlich ist die ECU-Software für diesen Prüfschritt noch gar nicht freigegeben.',
    masteryStage: 'NEU',
    occurrencesCount: 4,
    lastOccurredDaysAgo: 1,
  },
  {
    id: 'err_16',
    category: 'PRAEPOSITIONEN_KASUS',
    userMistake: 'Mangels den belastbaren Messwerten können wir noch nicht entscheiden.',
    nativeReformulation:
      'Mangels belastbarer Messwerte lässt sich derzeit noch keine Entscheidung treffen.',
    whyItSoundsForeign:
      "'Mangels' verlangt zwingend den Genitiv (ohne Artikel im Plural über die starke Adjektivendung '-er' sichtbar, mit Artikel: 'mangels der Messwerte').",
    ruleOrNuance:
      'mangels (+ Genitiv) = wegen Fehlens von | anhand (+ Genitiv) = mithilfe von.',
    contextSentence:
      'Anhand der protokollierten Zeitstempel lässt sich der Versatz bislang nicht erklären.',
    transferChallengePrompt:
      "Ergänzen Sie im Genitiv: 'Mangels (ausreichende Belege) bleibt die Freigabe ausgesetzt.'",
    transferTargetAnswer:
      'Mangels ausreichender Belege bleibt die Freigabe ausgesetzt.',
    masteryStage: 'WIEDERHOLUNG_TAG_1',
    occurrencesCount: 3,
    lastOccurredDaysAgo: 2,
  },
  {
    id: 'err_17',
    category: 'VERBKLAMMER_SATZSTRUKTUR',
    userMistake: 'Er berichtete, dass die Pumpe musste vorher neu kalibriert worden sein.',
    nativeReformulation:
      'Er berichtete, dass die Pumpe zuvor neu hätte kalibriert werden müssen.',
    whyItSoundsForeign:
      'Bei drei Verbformen am Nebensatzende mit Modalverb und Passiv/Perfekt rückt das finite Hilfsverb (hätte) VOR den Doppelinfinitiv (Oberfeldstellung).',
    ruleOrNuance:
      'Nebensatz-Oberfeld bei Ersatzinfinitiv: ..., dass das Prüffeld hätte gesichert werden müssen.',
    contextSentence:
      'Im Audit wurde bemängelt, dass die Annahmenfolie auch bei Zeitdruck hätte gezeigt werden müssen.',
    transferChallengePrompt:
      "Stellen Sie um in einen dass-Satz: 'Die Eltern hätten früher informiert werden müssen.' -> 'Wir merken an, dass...'",
    transferTargetAnswer:
      'Wir merken an, dass die Eltern früher hätten informiert werden müssen.',
    masteryStage: 'NEU',
    occurrencesCount: 2,
    lastOccurredDaysAgo: 3,
  },
  {
    id: 'err_18',
    category: 'ZOEGERN_LATENZ',
    userMistake: 'Entschuldigung, ich weiß nicht genau, vielleicht verstehe ich das falsch, aber...',
    nativeReformulation:
      'Dürfte ich an dieser Stelle kurz rückversichern, ob Sie damit auch den zweiten Prüfschritt meinen?',
    whyItSoundsForeign:
      'Übermäßige Selbstabwertung vor einer fachlichen Rückfrage schwächt die eigene Glaubwürdigkeit in C1-Reviews und Elternabenden.',
    ruleOrNuance:
      'Präzises Rückversichern statt Entschuldigen: konkret die unklare Stelle benennen und bestätigen lassen.',
    contextSentence:
      'Verstehe ich Ihren Einwand richtig, dass sich die Frist ausschließlich auf den Erstantrag bezieht?',
    transferChallengePrompt:
      'Reagieren Sie auf ein vages „Passt scho, wir machen das irgendwie“ beim Vereinsfest mit einer höflichen Konkretisierung.',
    transferTargetAnswer:
      'Freut mich, dass es grundsätzlich passt — bis wann und über welchen Kanal soll ich die Rückmeldung schicken?',
    masteryStage: 'NEU',
    occurrencesCount: 2,
    lastOccurredDaysAgo: 0,
  },
];

export const initialActiveVocabulary: ActiveVocabularyItem[] = [
  {
    id: 'voc_1',
    expression: 'etwas in Betracht ziehen',
    type: 'Nomen-Verb-Verbindung',
    meaningDe: 'Etwas erwägen, berücksichtigen, als Möglichkeit prüfen',
    naturalExample:
      'Wir sollten auch alternative Antriebstechnologien ernsthaft in Betracht ziehen.',
    informalOrClunkyEquivalent: 'über etwas nachdenken / etwas vielleicht machen',
    domain: 'Beruf & Strategie',
    isActivelyMastered: true,
    requiredInContext:
      'Schlagen Sie Ihrem Team vor, eine hybride Arbeitsweise als Option zu prüfen.',
  },
  {
    id: 'voc_2',
    expression: 'außer Zweifel stehen',
    type: 'Nomen-Verb-Verbindung',
    meaningDe: 'Unbestreitbar sein, völlig sicher feststehen',
    naturalExample:
      'Dass die Umstellung erhebliche Ressourcen erfordert, steht außer Zweifel.',
    informalOrClunkyEquivalent: 'das ist 100% sicher / niemand zweifelt daran',
    domain: 'Debatte & Argumentation',
    isActivelyMastered: false,
    requiredInContext:
      'Betonen Sie in einer Diskussion, dass der Klimawandel unbestreitbar ist.',
  },
  {
    id: 'voc_3',
    expression: 'zur Sprache bringen',
    type: 'Nomen-Verb-Verbindung',
    meaningDe: 'Ein Thema ansprechen, die Diskussion darüber eröffnen',
    naturalExample:
      'Ich möchte die wiederholten Lieferengpässe in der morgigen Sitzung zur Sprache bringen.',
    informalOrClunkyEquivalent: 'über ein Problem reden wollen',
    domain: 'Beruf & Meetings',
    isActivelyMastered: false,
    requiredInContext:
      'Sagen Sie Ihrem Vorgesetzten, dass Sie die Gehälter im Jahresgespräch ansprechen möchten.',
  },
  {
    id: 'voc_4',
    expression: 'nachdrücklich darauf hinweisen',
    type: 'Kollokation',
    meaningDe: 'Mit großem Nachdruck und Entschiedenheit betonen',
    naturalExample:
      'Die Sicherheitsbeauftragte wies nachdrücklich auf die Einhaltung der Schutzvorschriften hin.',
    informalOrClunkyEquivalent: 'sehr streng sagen, dass...',
    domain: 'Technik & Sicherheit',
    isActivelyMastered: false,
    requiredInContext:
      'Weisen Sie einen Mitarbeiter formell und unmissverständlich auf eine Frist hin.',
  },
  {
    id: 'voc_5',
    expression: 'stichhaltig begründen',
    type: 'Kollokation',
    meaningDe: 'Überzeugend, wasserdicht und fundiert argumentieren',
    naturalExample:
      'Können Sie die kalkulierten Mehrkosten gegenüber dem Kunden stichhaltig begründen?',
    informalOrClunkyEquivalent: 'gut erklären mit Beweisen',
    domain: 'Debatte & Gutachten',
    isActivelyMastered: true,
    requiredInContext:
      'Fordern Sie von einem Lieferanten eine fundierte Rechtfertigung für die Verzögerung.',
  },
  {
    id: 'voc_6',
    expression: 'in Abrede stellen',
    type: 'Nomen-Verb-Verbindung',
    meaningDe: 'Etwas bestreiten, leugnen oder anzweifeln',
    naturalExample:
      'Niemand stellt die fachliche Kompetenz des Teams in Abrede.',
    informalOrClunkyEquivalent: 'nicht glauben / abstreiten',
    domain: 'Debatte & Kritik',
    isActivelyMastered: false,
    requiredInContext:
      'Reagieren Sie auf Kritik und betonen Sie, dass Sie das Engagement der Kollegen keineswegs abstreiten.',
  },
  {
    id: 'voc_7',
    expression: 'in Kauf nehmen',
    type: 'Nomen-Verb-Verbindung',
    meaningDe:
      'Nachteile oder Risiken bewusst akzeptieren, um ein größeres Ziel zu erreichen',
    naturalExample:
      'Für eine fehlerfreie Produkteinführung müssen wir moderate Verzögerungen in Kauf nehmen.',
    informalOrClunkyEquivalent: 'ein schlechtes Ding akzeptieren',
    domain: 'Projektmanagement',
    isActivelyMastered: true,
    requiredInContext:
      'Erklären Sie dem Kunden, dass höhere Anfangskosten für langfristige Stabilität unvermeidbar sind.',
  },
  {
    id: 'voc_8',
    expression: 'den Vorzug geben',
    type: 'Nomen-Verb-Verbindung',
    meaningDe: 'Eine Option oder Person einer anderen vorziehen',
    naturalExample:
      'Bei gleicher Qualifikation sollten wir dem internen Bewerber den Vorzug geben.',
    informalOrClunkyEquivalent: 'etwas lieber wollen / wählen',
    domain: 'Führung & HR',
    isActivelyMastered: false,
    requiredInContext:
      'Begründen Sie, warum Sie der nachhaltigen Zulieferkette den Vorzug geben.',
  },
  {
    id: 'voc_9',
    expression: 'Abstriche machen',
    type: 'Nomen-Verb-Verbindung',
    meaningDe:
      'Ansprüche, Anforderungen oder Erwartungen reduzieren; Einbußen hinnehmen',
    naturalExample:
      'Bei der Datensicherheit dürfen wir unter keinen Umständen Abstriche machen.',
    informalOrClunkyEquivalent: 'weniger verlangen / Kompromiss machen',
    domain: 'Governance & Qualität',
    isActivelyMastered: false,
    requiredInContext:
      'Bestehen Sie unmissverständlich auf Einhaltung der Qualitätsstandards.',
  },
  {
    id: 'voc_10',
    expression: 'ins Benehmen setzen',
    type: 'Nomen-Verb-Verbindung',
    meaningDe: 'Sich formell und einvernehmlich mit einer anderen Stelle abstimmen',
    naturalExample:
      'Bevor die Richtlinie verabschiedet wird, müssen wir uns mit der Revision ins Benehmen setzen.',
    informalOrClunkyEquivalent: 'mit jemandem reden und fragen',
    domain: 'Bürokratie & Recht',
    isActivelyMastered: false,
    requiredInContext:
      'Informieren Sie den Vorstand über die anstehende behördliche Abstimmung.',
  },
];

export const initialListeningLab: ListeningLaboratoryItem[] = [
  {
    id: 'lab_1',
    title: 'Schwäbischer Werkstatt-Dialog: Toleranzen bei der Nockenwelle',
    environment: 'Technik & Werkstatt (25%)',
    accentOrDialect: 'Schwäbischer Zungenschlag & schnelle Fachreduktionen',
    nativeSpeechSpeed: 1.2,
    durationSeconds: 48,
    audioSpeakerText:
      "Also pass amol uff, Kerle! Wenn mer die Toleranze bei dera Nockawell net grad jetzt akkurat eistelle, no fliegt uns d'ganze Serie beim Prüfstand um d'Ohre! Do brauchsch gar net so bleed gugge, des hamma doch geschtern scho g'habt!",
    blindComprehensionQuestions: [
      {
        question:
          'Welches unausgesprochene Gefühl treibt den Werkstattleiter primär an?',
        options: [
          'Frustration über wiederholte Schlampigkeit und Furcht vor Ausschuss',
          'Freude über einen geglückten Belastungstest',
          'Gleichgültigkeit gegenüber den Hersteller-Vorgaben',
          'Wunsch nach sofortigem Feierabend',
        ],
        correctIndex: 0,
        rationaleWhyOthersAreWrong:
          "'des hamma doch geschtern scho g'habt' verweist auf einen wiederholten Fehler; 'fliegt uns um d'Ohre' unterstreicht die drohende Havarie.",
      },
      {
        question: "Was bedeutet die Reduktion 'hamma' im Kontext?",
        options: [
          'haben wir (kontrahiert im schnellen süddeutschen Sprechfluss)',
          'Hammer (Werkzeug)',
          'haben man',
          'haben sie',
        ],
        correctIndex: 0,
        rationaleWhyOthersAreWrong:
          "Verschmelzung von Hilfsverb 'haben' und Personalpronomen 'wir' zu 'hamma'.",
      },
    ],
    transcriptWithReductions:
      "Also pass [amol uff] (einmal auf), Kerle! Wenn [mer] (wir) die Toleranzen bei [dera] (dieser) Nockenwelle [net] (nicht) grad jetzt akkurat einstellen, [no] (dann) fliegt uns [d'ganze] (die ganze) Serie beim Prüfstand um [d'Ohre] (die Ohren)! Da [brauchsch] (brauchst du) gar net so blöd [gugge] (schauen), des [hamma] (haben wir) doch gestern schon gehabt!",
    reductionNotes: [
      {
        spokenForm: 'amol uff',
        standardWrittenForm: 'einmal auf',
        registerExplanation: 'Schwäbische Partikelreduktion',
      },
      {
        spokenForm: 'hamma',
        standardWrittenForm: 'haben wir',
        registerExplanation: 'Umgangssprachliche Verschmelzung im süddeutschen Raum',
      },
      {
        spokenForm: "d'ganze Serie",
        standardWrittenForm: 'die ganze Serie',
        registerExplanation: 'Elision des Vokals vor Konsonant',
      },
    ],
    contextAndSubtextAnalysis:
      'Dieser Dialog trainiert das Verstehen von authentischem Fachpersonal im Automobilbau. Es geht nicht um Standardhochdeutsch, sondern um das blitzschnelle Decodieren emotionaler Dringlichkeit unter Werkstattbedingungen.',
  },
  {
    id: 'lab_2',
    title: 'Berliner Kiez-Debatte: Mietpreisbremse & Milieuschutz',
    environment: 'Alltag & Soziales (40%)',
    accentOrDialect: 'Berliner Umgangssprache mit Ironie & schnellen Elisionen',
    nativeSpeechSpeed: 1.25,
    durationSeconds: 55,
    audioSpeakerText:
      'Ick mein ja nur: Dit is doch reine Augenwischerei! Da klatschen se n bisschen Farbe an die Fassade und schwupps wird dit Ding als Luxussanierung deklariert. Wer soll n dit bitteschön noch berappen? Da bleibste doch uff der Strecke!',
    blindComprehensionQuestions: [
      {
        question:
          "Was signalisiert der Ausdruck 'reine Augenwischerei' im Hinblick auf die Haltung des Sprechers?",
        options: [
          'Er hält die Maßnahmen für eine betrügerische Täuschung ohne echten Nutzen',
          'Er lobt die optische Verbesserung des Hauses',
          'Er befürwortet Luxussanierungen im Viertel',
          'Er fordert mehr Fassadenfarben',
        ],
        correctIndex: 0,
        rationaleWhyOthersAreWrong:
          "'Augenwischerei' ist ein festes Idiom für Täuschung oder Vorspiegelung falscher Tatsachen.",
      },
    ],
    transcriptWithReductions:
      "[Dit] (Das) is doch reine Augenwischerei! Da klatschen [se] (sie) ['n] (ein) bisschen Farbe an die Fassade und schwupps wird [dit Ding] (die Sache) als Luxussanierung deklariert. Wer soll ['n dit] (denn das) bitteschön noch [berappen] (bezahlen)? Da [bleibste] (bleibst du) doch [uff der Strecke] (auf der Strecke)!",
    reductionNotes: [
      {
        spokenForm: 'dit',
        standardWrittenForm: 'das',
        registerExplanation: 'Berlinerischer Wandel von s zu t',
      },
      {
        spokenForm: 'berappen',
        standardWrittenForm: 'bezahlen',
        registerExplanation: 'Umgangssprachliches Idiom für hohe Zahlungen',
      },
      {
        spokenForm: 'bleibste',
        standardWrittenForm: 'bleibst du',
        registerExplanation: 'Enklitische Verschmelzung',
      },
    ],
    contextAndSubtextAnalysis:
      "Alltägliche soziale Debatten in Deutschland sind voller bildhafter Metaphern ('Augenwischerei', 'auf der Strecke bleiben'). Ein C1-Sprecher muss die bittere Frustration hinter dem lockeren Tonfall erfassen.",
  },
  {
    id: 'lab_3',
    title: 'Krisensitzung: Strategiewechsel im Vorstandsbüro',
    environment: 'Beruf & Führung (35%)',
    accentOrDialect: 'Gehobenes Norddeutsches Hochdeutsch, kühle Distanz',
    nativeSpeechSpeed: 1.15,
    durationSeconds: 62,
    audioSpeakerText:
      'Lassen Sie uns die Dinge beim Namen nennen. Wenn wir das Segment nicht binnen dreier Monate restrukturieren, wird der Aufsichtsrat personelle Konsequenzen ziehen. Ich erwarte von Ihnen keine Ausflüchte, sondern bis Freitag ein belastbares Sanierungskonzept.',
    blindComprehensionQuestions: [
      {
        question: 'Welche subtile Drohung formuliert der Vorstandsvorsitzende?',
        options: [
          'Entlassung des Führungsteams bei Nichterfüllung der Frist',
          'Eine sofortige Kürzung der Forschungsbudgets',
          'Die Verlegung der Firmenzentrale nach Hamburg',
          'Eine Verschiebung des Termins auf nächste Woche',
        ],
        correctIndex: 0,
        rationaleWhyOthersAreWrong:
          "'personelle Konsequenzen ziehen' ist die klassische gehobene Chiffre für Kündigungen im Top-Management.",
      },
    ],
    transcriptWithReductions:
      'Lassen Sie uns die Dinge beim Namen nennen. Wenn wir das Segment nicht binnen dreier Monate restrukturieren, wird der Aufsichtsrat personelle Konsequenzen ziehen. Ich erwarte von Ihnen keine Ausflüchte, sondern bis Freitag ein belastbares Sanierungskonzept.',
    reductionNotes: [
      {
        spokenForm: 'binnen dreier Monate',
        standardWrittenForm: 'innerhalb von 3 Monaten',
        registerExplanation: 'Gehobener Genitiv nach der Präposition binnen',
      },
      {
        spokenForm: 'belastbar',
        standardWrittenForm: 'fundiert / stichhaltig',
        registerExplanation: 'Management-Jargon für verlässliche Daten',
      },
    ],
    contextAndSubtextAnalysis:
      'Subtile, eiskalte Führungssprache. Kein Schreien, sondern präzise bürokratische Konsequenzen mit gehobenen Temporalangaben.',
  },
  {
    id: 'lab_4',
    title: 'Zürcher Banken-Finanzierungsdebatte: Konsortialkredit',
    environment: 'Beruf & Akademie (35%)',
    accentOrDialect:
      'Schweizer Hochdeutsch mit Helvetismen & präziser Bankterminologie',
    nativeSpeechSpeed: 1.15,
    durationSeconds: 52,
    audioSpeakerText:
      'Grüezi mitenand. Wir haben das Traktandum Konsortialkredit vorgezogen. Wenn der Zinsdeckungsgrad weiter absinkt, müssen wir eine Sistierung der Tranche ins Auge fassen. Bitte klären Sie allfällige Rückfragen noch heute vor Börsenschluss.',
    blindComprehensionQuestions: [
      {
        question: "Was bedeutet das Wort 'Traktandum' im Schweizer Hochdeutsch?",
        options: [
          'Tagesordnungspunkt in einer Sitzung',
          'Finanzvertrag',
          'Protokollant',
          'Darlehenszins',
        ],
        correctIndex: 0,
        rationaleWhyOthersAreWrong:
          "'Traktandum' (Plural: Traktanden) ist der offizielle schweizerische Standardausdruck für Tagesordnungspunkt.",
      },
      {
        question:
          'Welche Maßnahme droht der Sprecher bei sinkendem Zinsdeckungsgrad an?',
        options: [
          'Eine vorübergehende Aussetzung (Sistierung) der Kreditauszahlung',
          'Eine sofortige Kündigung aller Mitarbeiter',
          'Die Übernahme durch eine deutsche Großbank',
          'Eine Erhöhung der Dividende',
        ],
        correctIndex: 0,
        rationaleWhyOthersAreWrong:
          "'Sistierung' bedeutet im Schweizer Sprachgebrauch das Aussetzen oder Einfrieren eines Verfahrens oder einer Zahlung.",
      },
    ],
    transcriptWithReductions:
      '[Grüezi mitenand] (Guten Tag zusammen). Wir haben das [Traktandum] (den Tagesordnungspunkt) Konsortialkredit vorgezogen. Wenn der Zinsdeckungsgrad weiter absinkt, müssen wir eine [Sistierung] (vorübergehende Aussetzung/Stoppung) der Tranche ins Auge fassen. Bitte klären Sie [allfällige] (etwaige/eventuelle) Rückfragen noch heute vor Börsenschluss.',
    reductionNotes: [
      {
        spokenForm: 'Traktandum',
        standardWrittenForm: 'Tagesordnungspunkt',
        registerExplanation: 'Offizieller schweizerischer Standardausdruck (Helvetismus)',
      },
      {
        spokenForm: 'Sistierung',
        standardWrittenForm: 'Aussetzung / Unterbrechung',
        registerExplanation: 'Schweizer Amts- und Rechtssprache',
      },
      {
        spokenForm: 'allfällig',
        standardWrittenForm: 'eventuell / etwaig',
        registerExplanation: 'Fester Bestandteil des schweizerischen Schrift- und Geschäftsdeutschs',
      },
    ],
    contextAndSubtextAnalysis:
      'Schweizer Hochdeutsch auf C1-Niveau zeichnet sich durch formelle Helvetismen und eine charakteristische Satzmelodie aus. Für geschäftliche Verhandlungen in Zürich oder Basel unverzichtbar.',
  },
  {
    id: 'lab_5',
    title:
      'Wiener Ministerialbürokratie: Verwaltungsbeschwerde & Fristerstreckung',
    environment: 'Alltag & Soziales (40%)',
    accentOrDialect:
      'Österreichisches Hochdeutsch (Amtssprache) mit Wiener Tonfall',
    nativeSpeechSpeed: 1.2,
    durationSeconds: 58,
    audioSpeakerText:
      'Guten Tag. Bezüglich der anhängigen Beschwerde: Die Gegenseite hat eine Fristerstreckung urgiert. Wir gewähren Parteiengehör bis kommenden Dienstag. Erst danach ist der Bescheid spruchreif.',
    blindComprehensionQuestions: [
      {
        question:
          "Was hat die Gegenseite im Behördenverfahren unternommen ('urgiert')?",
        options: [
          'Sie hat eine Fristverlängerung mit Nachdruck angemahnt bzw. beantragt',
          'Sie hat die Klage vollständig zurückgezogen',
          'Sie hat die Akten vernichtet',
          'Sie hat den Sachbearbeiter angezeigt',
        ],
        correctIndex: 0,
        rationaleWhyOthersAreWrong:
          "'Urgieren' ist ein in Österreich gebräuchlicher juristischer Begriff für nachdrückliches Mahnen oder Betreiben.",
      },
      {
        question:
          "Wann gilt ein behördlicher Bescheid in Österreich als 'spruchreif'?",
        options: [
          'Wenn der Sachverhalt vollständig geklärt und bereit zur Entscheidung ist',
          'Wenn er im Radio vorgelesen wurde',
          'Wenn die Widerspruchsfrist abgelaufen ist',
          'Wenn der Minister persönlich unterschrieben hat',
        ],
        correctIndex: 0,
        rationaleWhyOthersAreWrong:
          "'Spruchreif' bedeutet entscheidungsreif nach Abschluss des Ermittlungsverfahrens und Parteiengehörs.",
      },
    ],
    transcriptWithReductions:
      'Guten Tag. Bezüglich der anhängigen Beschwerde: Die Gegenseite hat eine [Fristerstreckung urgiert] (Fristverlängerung mit Nachdruck beantragt). Wir gewähren [Parteiengehör] (rechtliches Gehör) bis kommenden Dienstag. Erst danach ist der Bescheid [spruchreif] (fertig zur Entscheidung).',
    reductionNotes: [
      {
        spokenForm: 'Fristerstreckung',
        standardWrittenForm: 'Fristverlängerung',
        registerExplanation: 'Österreichischer Rechts- und Verwaltungsterminus (Austriazismus)',
      },
      {
        spokenForm: 'urgieren',
        standardWrittenForm: 'nachdrücklich mahnen',
        registerExplanation: 'Gehobenes österreichisches Amtsdeutsch',
      },
      {
        spokenForm: 'spruchreif',
        standardWrittenForm: 'entscheidungsfertig',
        registerExplanation: 'Rechtsbegriff für den Abschluss des Ermittlungsverfahrens',
      },
    ],
    contextAndSubtextAnalysis:
      'Österreichisches Standarddeutsch im öffentlichen Sektor und in Behörden. Höfliche, aber unerbittliche bürokratische Präzision mit traditionellen lateinstämmigen Fachausdrücken.',
  },
  {
    id: 'lab_6',
    title: 'Münchner Ingenieurs-Stammtisch: Lieferketten & Fertigungstoleranzen',
    environment: 'Beruf & Alltag (35%)',
    accentOrDialect:
      'Bairisch gefärbtes Hochdeutsch mit zügigem Gesprächsfluss',
    nativeSpeechSpeed: 1.25,
    durationSeconds: 54,
    audioSpeakerText:
      "Schau her, des is doch g'hupft wie g'sprungen, ob mir des Bauteil aus Augsburg oder aus Brno beziehen. Solang die Toleranz unter drei Mikrometer bleibt und der Zulieferer ned wieder mit Ausreden daherkommt, wink i des durch. Aber wehe, die Charge hat wieder Haarrisse!",
    blindComprehensionQuestions: [
      {
        question:
          "Was meint der Ingenieur mit der Redewendung 'des is doch g'hupft wie g'sprungen'?",
        options: [
          'Es macht im Ergebnis überhaupt keinen Unterschied',
          'Die Lieferung wird sich um mehrere Wochen verzögern',
          'Die Bauteile müssen zweimal geprüft werden',
          'Die Transportkosten sind in Augsburg doppelt so hoch',
        ],
        correctIndex: 0,
        rationaleWhyOthersAreWrong:
          "'Gehupft wie gesprungen' ist ein süddeutsches Idiom für 'völlig egal / gleichgültig im Resultat'.",
      },
    ],
    transcriptWithReductions:
      "Schau her, [des is] (das ist) doch [g'hupft wie g'sprungen] (völlig gleichgültig), ob [mir] (wir) des Bauteil aus Augsburg oder aus Brno beziehen. Solang die Toleranz unter drei Mikrometer bleibt und der Zulieferer [ned] (nicht) wieder mit Ausreden daherkommt, [wink i des durch] (genehmige ich das). Aber wehe, die Charge hat wieder Haarrisse!",
    reductionNotes: [
      {
        spokenForm: 'mir',
        standardWrittenForm: 'wir',
        registerExplanation: 'Bairisch-süddeutsche Pronomenverschiebung in der 1. Person Plural',
      },
      {
        spokenForm: 'durchwinken',
        standardWrittenForm: 'ohne Einspruch genehmigen',
        registerExplanation: 'Umgangssprachlich-berufliches Idiom für zügige Freigabe',
      },
    ],
    contextAndSubtextAnalysis:
      "In süddeutschen Industrieunternehmen mischen erfahrene Fachkräfte pragmatische Präzision ('Mikrometer', 'Charge') mit regionaler Kolloquialsprache. Wer nur Lehrbuch-Hochdeutsch versteht, überhört die klare Warnung am Ende.",
  },
  {
    id: 'lab_7',
    title: 'Akademisches Kolloquium: Algorithmische Transparenz & Haftungsfragen',
    environment: 'Beruf & Akademie (35%)',
    accentOrDialect:
      'Präzises Wissenschaftsdeutsch, hohes Sprechtempo, dichte Nominalketten',
    nativeSpeechSpeed: 1.22,
    durationSeconds: 66,
    audioSpeakerText:
      'Meine sehr geehrten Damen und Herren, die bloße Forderung nach Erklärbarkeit neuronaler Netze greift meines Erachtens zu kurz. Solange die Kausalitätszurechnung im Schadensfall rechtlich im Ungefähren verbleibt, verlagern wir die Haftungslücke lediglich auf den Endanwender – ein Zustand, der mit dem Vorsorgeprinzip unvereinbar ist.',
    blindComprehensionQuestions: [
      {
        question:
          "Welchen zentralen Einwand erhebt der Vortragende gegen die reine Forderung nach 'Erklärbarkeit'?",
        options: [
          'Sie löst das rechtliche Problem der Haftungszuweisung im Schadensfall nicht',
          'Neuronale Netze sind technisch grundsätzlich nicht dokumentierbar',
          'Endanwender interessieren sich nicht für Datenschutz',
          'Das Vorsorgeprinzip gilt ausschließlich im Umweltrecht',
        ],
        correctIndex: 0,
        rationaleWhyOthersAreWrong:
          "Der Sprecher kritisiert, dass ohne klare 'Kausalitätszurechnung' die rechtliche Verantwortung unfair auf den Nutzer abgewälzt wird.",
      },
    ],
    transcriptWithReductions:
      'Meine sehr geehrten Damen und Herren, die bloße Forderung nach Erklärbarkeit neuronaler Netze [greift zu kurz] (ist unzureichend). Solange die [Kausalitätszurechnung] (rechtliche Ursachenzuordnung) im Schadensfall rechtlich [im Ungefähren verbleibt] (unklar bleibt), verlagern wir die Haftungslücke lediglich auf den Endanwender – ein Zustand, der mit dem Vorsorgeprinzip unvereinbar ist.',
    reductionNotes: [
      {
        spokenForm: 'zu kurz greifen',
        standardWrittenForm: 'nicht ausreichen / unvollständig sein',
        registerExplanation: 'Feste akademisch-publizistische Wendung',
      },
      {
        spokenForm: 'im Ungefähren verbleiben',
        standardWrittenForm: 'vage / unpräzise bleiben',
        registerExplanation: 'Gehobener Nominalstil in Rechts- und Ethikdebatten',
      },
    ],
    contextAndSubtextAnalysis:
      'Typischer Duktus universitärer Vorträge und Podiumsstatements: Lange, hochverdichtete Nominalsyntagmen erfordern sofortiges Mitdenken bei Subjekt-Prädikat-Klammern.',
  },
  {
    id: 'lab_8',
    title: 'Trainingsplatz E-Jugend: Funino-Turnier & Coaching-Kommandos',
    environment: 'Sportplatz & Verein (E-Jugend U10/U11)',
    accentOrDialect: 'Lebendiges Vereinsdeutsch am Spielfeldrand mit kurzen Zurufen',
    nativeSpeechSpeed: 1.15,
    durationSeconds: 48,
    audioSpeakerText:
      'Super mutig aufgedreht, Leo! Und jetzt alle mitgedacht: Wenn wir auf die vier Minitore spielen, wollen wir nicht alle auf einen Haufen rennen, sondern das Spielfeld breit machen! Nach jedem Tor wird durchrotiert, jeder kommt dran!',
    blindComprehensionQuestions: [
      {
        question:
          "Welches taktische Ziel verfolgt der E-Jugend-Trainer mit dem Hinweis 'das Spielfeld breit machen'?",
        options: [
          'Verhindern, dass alle Kinder als Traube um den Ball stehen, um Passräume zu öffnen',
          'Die Kinder sollen ausschließlich lange hohe Bälle schlagen',
          'Nur die zwei schnellsten Spieler sollen Tore schießen',
          'Das Training wegen Müdigkeit sofort beenden',
        ],
        correctIndex: 0,
        rationaleWhyOthersAreWrong:
          "In der E-Jugend neigen Kinder zur 'Traubenbildung'; 'breit machen' schafft Raum für Dribbling und einfache Pässe auf vier Minitore.",
      },
    ],
    transcriptWithReductions:
      'Super mutig [aufgedreht] (mit dem Ball zum Tor gedreht), Leo! Und jetzt alle mitgedacht: Wenn wir auf die vier Minitore spielen, wollen wir [nicht alle auf einen Haufen rennen] (keine Traubenbildung um den Ball), sondern das Spielfeld breit machen! Nach jedem Tor wird [durchrotiert] (gewechselt), jeder kommt dran!',
    reductionNotes: [
      {
        spokenForm: 'aufdrehen',
        standardWrittenForm: 'sich mit Ball nach vorne orientieren',
        registerExplanation: 'Zentrales Coaching-Verb für Offensivaktionen',
      },
      {
        spokenForm: 'auf einen Haufen rennen',
        standardWrittenForm: 'Traubenbildung / fehlende Raumaufteilung',
        registerExplanation: 'Typisches Phänomen im Kinderfußball',
      },
      {
        spokenForm: 'durchrotieren',
        standardWrittenForm: 'gleichmäßig wechseln',
        registerExplanation: 'Fairplay-Prinzip im E-Jugend-Spielbetrieb',
      },
    ],
    contextAndSubtextAnalysis:
      "Erfolgreiches E-Jugend-Coaching verbindet direktes Lob ('Super mutig aufgedreht') mit bildhaften, sofort umsetzbaren Raumhinweisen ohne komplizierten Taktikjargon.",
  },
  {
    id: 'lab_9',
    title: 'C-Jugend-Taktikbesprechung: Viererkette & Umschaltmoment',
    environment: 'Sportplatz & Verein (C-Jugend U14/U15)',
    accentOrDialect: 'Klares, dynamisches Trainerdeutsch in der Kabine',
    nativeSpeechSpeed: 1.2,
    durationSeconds: 56,
    audioSpeakerText:
      'Jungs, in der ersten Viertelstunde waren unsere Abstände zwischen Abwehrkette und Sechserraum zu groß. Wenn der Gegner in den Halbraum spielt, muss der ballnahe Innenverteidiger herausrücken, während der Außenverteidiger einrückt und die Tiefe absichert. Sobald wir den Ball erobern, suchen wir sofort den ersten Kontakt nach vorne!',
    blindComprehensionQuestions: [
      {
        question:
          'Wie soll sich die Abwehr verhalten, wenn der Innenverteidiger herausrückt?',
        options: [
          'Der Außenverteidiger rückt nach innen ein und sichert den Raum dahinter ab',
          'Alle Verteidiger laufen gleichzeitig auf den gegnerischen Torwart zu',
          'Der Torwart übernimmt die Position des Mittelstürmers',
          'Die Mannschaft bleibt komplett stehen und wartet auf den Schiedsrichter',
        ],
        correctIndex: 0,
        rationaleWhyOthersAreWrong:
          "Das Prinzip des 'Durchsicherns' in der C-Jugend bedeutet, dass beim Herausstechen eines Spielers der Nebenmann einrückt ('einrücken & Tiefe absichern').",
      },
    ],
    transcriptWithReductions:
      'Jungs, in der ersten Viertelstunde waren unsere Abstände zwischen Abwehrkette und [Sechserraum] (defensives Mittelfeld) zu groß. Wenn der Gegner in den [Halbraum] (Zone zwischen Zentrum und Flügel) spielt, muss der ballnahe Innenverteidiger [herausrücken] (entgegengehen), während der Außenverteidiger [einrückt] (nach innen zieht) und die Tiefe absichert.',
    reductionNotes: [
      {
        spokenForm: 'Sechserraum',
        standardWrittenForm: 'Zone vor der Abwehr',
        registerExplanation: 'Benannt nach der klassischen Rückennummer 6',
      },
      {
        spokenForm: 'herausrücken / einrücken',
        standardWrittenForm: 'kompaktes Verschieben in der Kette',
        registerExplanation: 'Grundvokabular für C-Jugend-Gruppentaktik',
      },
      {
        spokenForm: 'Tiefe absichern',
        standardWrittenForm: 'Räume hinter der Abwehr schützen',
        registerExplanation: 'Verhindert Steilpässe in den Rücken der Kette',
      },
    ],
    contextAndSubtextAnalysis:
      "Ab der C-Jugend (Großfeld 11-gegen-11) wechseln Spieler vom Kleinfeld auf Raumdeckung und Kettenverhalten. Klare räumliche Begriffe ('Halbraum', 'ballnah', 'Tiefe') sind entscheidend.",
  },
  {
    id: 'lab_10',
    title: 'EV-Prüfstand Stuttgart: Thermisches Derating & Software-Versatz',
    environment: 'Technik & EV-Entwicklung (25%)',
    accentOrDialect: 'Südwestdeutsch geprägtes Ingenieursdeutsch im Prüfstands-Review',
    nativeSpeechSpeed: 1.2,
    durationSeconds: 56,
    audioSpeakerText:
      "Hör zu, bei Modul vier hatten wir nach zwölf Minuten Schnellladen knapp achtundvierzig Grad, und das Derating hat sofort runtergeregelt. Bloß: Solang der Durchflusssensor noch auf dem alten Stand war, wissen wir halt net, ob wirklich die Kühlung stockt oder nur das Software-Update den Volumenstrom falsch loggt.",
    blindComprehensionQuestions: [
      {
        question: 'Was schließt der Sprecher ausdrücklich noch NICHT als Fehlerursache aus?',
        options: [
          'Dass entweder eine echte Kühlungsstockung oder ein Fehler durch das Software-Update bzw. den Sensor vorliegt',
          'Dass das Batteriepaket zwingend mechanisch zerstört ist',
          'Dass die Schnellladesäule abgeschaltet war',
          'Dass Modul vier überhaupt keinen Sensor besitzt',
        ],
        correctIndex: 0,
        rationaleWhyOthersAreWrong:
          "Der Sprecher benennt explizit die Doppeldeutigkeit ('net, ob wirklich die Kühlung stockt oder nur das Software-Update falsch loggt') bis zur Neukalibrierung.",
      },
      {
        question: "Welche Funktion hat die Partikel 'halt' in 'wissen wir halt net'?",
        options: [
          'Sie markiert eine nüchterne, pragmatische Einschränkung angesichts fehlender Daten',
          'Sie fordert einen sofortigen Maschinenstopp ("Halt!")',
          'Sie drückt Begeisterung über die Temperatur aus',
          'Sie beweist einen Herstellerfehler beim Zulieferer',
        ],
        correctIndex: 0,
        rationaleWhyOthersAreWrong:
          "Im süddeutschen Fachgespräch rahmt unbetontes 'halt' hier eine gegebene Grenze ('leider nun einmal nicht') sachlich ein.",
      },
    ],
    transcriptWithReductions:
      "Hör zu, bei Modul vier hatten [wir] (wir) nach zwölf Minuten Schnellladen knapp achtundvierzig Grad, und das Derating hat sofort runtergeregelt. [Bloß] (Allerdings): Solang der Durchflusssensor noch auf dem alten Stand war, wissen [wir halt net] (wir eben nicht), ob wirklich die Kühlung stockt oder nur das Software-Update den Volumenstrom falsch loggt.",
    reductionNotes: [
      {
        spokenForm: 'net',
        standardWrittenForm: 'nicht',
        registerExplanation: 'Südwestdeutsche Verneinungsreduktion im lockeren Fachgespräch',
      },
      {
        spokenForm: 'runtergeregelt',
        standardWrittenForm: 'heruntergeregelt / gedrosselt',
        registerExplanation: 'Umgangssprachliche Verbpartikel für technisches Derating',
      },
    ],
    contextAndSubtextAnalysis:
      'Authentische Prüfstandskommunikation mischt präzise Fachtermini („Derating“, „Volumenstrom“) mit süddeutschen Gesprächspartikeln („bloß“, „halt net“). Entscheidend ist das sichere Mithören der Beweisgrenze.',
  },
  {
    id: 'lab_11',
    title: 'Vereinsheim im Winter: Hallenzeiten, Elternanspruch & Ehrenamt',
    environment: 'Sportplatz & Verein (Alltag & Soziales)',
    accentOrDialect: 'Lebhaftes schwäbisch-gefärbtes Vereinsgespräch mit indirekten Spitzen',
    nativeSpeechSpeed: 1.18,
    durationSeconds: 58,
    audioSpeakerText:
      "Schau, wir können halt net alle fünf Jugendmannschaften gleichzeitig am Mittwoch um fünf in die kleine Halle stecken. Wenn mir dann manche Eltern schreiben, des sei ja ganz net, aber die Kleinen bräuchten halt Vorrang — ja mei, irgendwo muss ich die Hallenuhr ja aufteilen, sonst steht unsere U15 im Januar im Schneeregen.",
    blindComprehensionQuestions: [
      {
        question: 'Was meint die Sprecherin mit dem Einschub „des sei ja ganz net, aber …“?',
        options: [
          'Sie zitiert eine scheinbar freundliche Elternformulierung, die im Kern einen Widerspruch bzw. eine Forderung enthält',
          'Sie bestätigt, dass die kleine Halle für alle fünf Teams ausreicht',
          'Sie schlägt vor, das Wintertraining komplett abzusagen',
          'Sie freut sich über zusätzliches Budget für eine zweite Sporthalle',
        ],
        correctIndex: 0,
        rationaleWhyOthersAreWrong:
          "Die Konjunktiv-I-Wiedergabe ('sei') plus 'aber' deckt den höflich verpackten Anspruch der Eltern auf.",
      },
    ],
    transcriptWithReductions:
      "Schau, wir können [halt net] (eben nicht) alle fünf Jugendmannschaften gleichzeitig am Mittwoch um fünf in die kleine Halle stecken. Wenn [mir] (mir) dann manche Eltern schreiben, [des sei] (das sei) ja ganz nett, aber die Kleinen bräuchten [halt] (nun einmal) Vorrang — ja mei, irgendwo muss ich die Hallenuhr ja aufteilen.",
    reductionNotes: [
      {
        spokenForm: 'des sei',
        standardWrittenForm: 'das sei',
        registerExplanation: 'Dialektale Lautung verbunden mit gehobener indirekter Rede (Konjunktiv I)',
      },
      {
        spokenForm: 'ja mei',
        standardWrittenForm: 'nun ja / ach je',
        registerExplanation: 'Süddeutscher Seufzer der pragmatischen Ratlosigkeit',
      },
    ],
    contextAndSubtextAnalysis:
      'Im Ehrenamt treffen indirekte Höflichkeitsfloskeln und direkte Zielkonflikte aufeinander. C1-Hörverstehen erkennt die ironisch-distanzierte Zitierung von Elternforderungen.',
  },
  {
    id: 'lab_12',
    title: 'Wirtschafts-Debatte: Bürokratieabbau vs. Lieferkettenkontrolle',
    environment: 'Akademie & Öffentlichkeit (30%)',
    accentOrDialect: 'Zügiges bundesdeutsches Radio-Interview mit Unterbrechung',
    nativeSpeechSpeed: 1.24,
    durationSeconds: 60,
    audioSpeakerText:
      'Moment, Frau Moderation, so lässt sich das nicht gegeneinander ausspielen. Dass Dokumentationspflichten kleine Zulieferer belasten, bestreite ich nicht im Geringsten. Gleichwohl greift die Forderung nach pauschaler Abschaffung zu kurz, solange wir keine schlankeren digitalen Prüfverfahren etabliert haben.',
    blindComprehensionQuestions: [
      {
        question: 'Wie positioniert sich der Interviewgast zur Belastung kleiner Zulieferer?',
        options: [
          'Er räumt die Belastung ausdrücklich ein, lehnt aber eine pauschale Abschaffung ohne digitale Alternative ab',
          'Er behauptet, kleine Betriebe hätten überhaupt keinen Dokumentationsaufwand',
          'Er fordert eine Verdopplung der Berichtspflichten ohne Ausnahmen',
          'Er bricht das Interview wegen der Unterbrechung ab',
        ],
        correctIndex: 0,
        rationaleWhyOthersAreWrong:
          "'bestreite ich nicht im Geringsten' + 'Gleichwohl greift … zu kurz' bildet eine klassische C1-Konzession mit Gegenposition.",
      },
    ],
    transcriptWithReductions:
      'Moment, Frau Moderation, so lässt sich das nicht [gegeneinander ausspielen] (künstlich als Gegensatz darstellen). Dass Dokumentationspflichten kleine Zulieferer belasten, bestreite ich nicht im Geringsten. [Gleichwohl] (Dennoch) greift die Forderung nach pauschaler Abschaffung zu kurz, solange wir keine schlankeren digitalen Prüfverfahren etabliert haben.',
    reductionNotes: [
      {
        spokenForm: 'nicht im Geringsten',
        standardWrittenForm: 'überhaupt nicht',
        registerExplanation: 'Betonte bildungssprachliche Verneinung',
      },
      {
        spokenForm: 'Gleichwohl',
        standardWrittenForm: 'Dennoch / Trotzdem',
        registerExplanation: 'Gehobener konzessiver Konnektor im Medien- und Politikdiskurs',
      },
    ],
    contextAndSubtextAnalysis:
      'Typische Rundfunk-Rhetorik: Wer Konzessionen („bestreite ich nicht“) schnell überhört, missversteht das nachfolgende „Gleichwohl“ als Zustimmung statt als differenzierten Widerspruch.',
  },
];

export const baseConversations: ConversationScenario[] = [
  {
    id: 'conv_1',
    title: 'Meeting-Debatte: Investition in KI-Infrastruktur',
    rolePartner: 'Dr. Marianne Weber (CFO)',
    environment: 'Beruf & Akademie (35%)',
    openingStatement:
      'Guten Morgen. Wir haben Ihren Antrag über 2,5 Millionen Euro für das On-Premise-Cluster erhalten. Aus kaufmännischer Sicht sehe ich hier vor allem unkalkulierbare Betriebskosten. Überzeugen Sie mich, warum Cloud-Lösungen nicht ausreichen.',
    turns: [
      {
        partnerSpeech:
          'Verstehen Sie mich nicht falsch: Wir wollen Innovation nicht ausbremsen. Aber ein ROI nach drei Jahren ist mir schlicht zu spät bei der derzeitigen Marktdynamik.',
        expectedUserRolePrompt:
          'Argumentieren Sie für Datenschutz & IP-Schutz, ohne belehrend zu wirken.',
        suggestedC1Connectors: [
          'In Anbetracht der Tatsache',
          'Hierbei gilt es zu bedenken',
          'Nichtsdestotrotz',
        ],
        targetRegister: 'Diplomatisches Vorstandsniveau',
        speakerProfile: 'PARTNER',
      },
      {
        partnerSpeech:
          'Gut, die Datenschutzbedenken sind legitim. Aber wie federn wir den personellen Mehraufwand für die Wartung ab?',
        expectedUserRolePrompt:
          'Legen Sie dar, dass das bestehende DevOps-Team durch Automatisierungskapazitäten entlastet wird.',
        suggestedC1Connectors: [
          'Diesbezüglich',
          'Es steht außer Frage',
          'Ganz im Gegenteil',
        ],
        targetRegister: 'Faktensicheres Führungsdeutsch',
        speakerProfile: 'PARTNER',
      },
    ],
    unexpectedInterruption: {
      triggerAfterSecond: 6,
      partnerInterruptSpeech:
        'Moment mal! Da muss ich kurz einhaken: Sie tun so, als ob Cloud-Anbieter keine DSGVO-Garantien böten. Das ist doch ein Scheinargument!',
      reactionObjective:
        "Parieren Sie den Vorwurf 'Scheinargument' augenblicklich und differenzieren Sie zwischen Standard-DSGVO und Geschäftsgeheimnisschutz nach GeschGehG.",
      speakerProfile: 'INTERRUPTER',
    },
    evaluationFocus:
      "Sprechlatenz < 2.5s, keine 'ähm'-Füllwörter, diplomatisches Durchsetzungsvermögen.",
    variationLabel: 'Standard-Szenario',
  },
  {
    id: 'conv_2',
    title: 'Mittagspause mit Kollegen: Meinungsverschiedenheit über Remotearbeit',
    rolePartner: 'Felix & Katja (Teamkollegen)',
    environment: 'Alltag & Soziales (40%)',
    openingStatement:
      'Hey! Setz dich doch zu uns. Felix regt sich gerade furchtbar darüber auf, dass die Geschäftsleitung drei Tage Büro-Präsenz anordnen will. Wie siehst du das?',
    turns: [
      {
        partnerSpeech:
          'Ich finde das total rückschrittlich. Wer im Homeoffice sitzt, arbeitet doch genauso konzentriert – oft sogar mehr!',
        expectedUserRolePrompt:
          'Wägen Sie beide Seiten ab: Spontaner Wissensaustausch auf dem Flur vs. ungestörte Deep-Work-Phasen zu Hause.',
        suggestedC1Connectors: [
          'Einerseits ... andererseits',
          'Man darf jedoch nicht verkennen',
          'Meines Erachtens',
        ],
        targetRegister: 'Natürliches, kollegiales Niveau mit gedanklicher Tiefe',
        speakerProfile: 'PARTNER',
      },
    ],
    unexpectedInterruption: {
      triggerAfterSecond: 5,
      partnerInterruptSpeech:
        'Ja, aber findest du nicht, dass das ein bisschen zu kurz gedacht ist? Wer den Flurfunk braucht, organisiert einfach seine Meetings schlecht!',
      reactionObjective:
        'Reagieren Sie spontan mit einer persönlichen Beobachtung und widerlegen Sie die These mit feiner Selbstironie.',
      speakerProfile: 'INTERRUPTER',
    },
    evaluationFocus:
      'Spontanität, Vermeidung von Monologen, natürliche Einbindung von Gesprächspartnern.',
    variationLabel: 'Standard-Szenario',
  },
  {
    id: 'conv_3',
    title: 'Gehaltsverhandlung: Leistungsbeurteilung mit Gegenwind',
    rolePartner: 'Dr. Harald Winter (Bereichsleiter)',
    environment: 'Beruf & Führung (35%)',
    openingStatement:
      'Herr/Frau Kollege, Ihre Projektleistung im vergangenen Quartal war tadellos. Allerdings sind die Budgets für das kommende Geschäftsjahr extrem eingefroren. Eine Gehaltsanpassung um 12 Prozent sehe ich beim besten Willen nicht.',
    turns: [
      {
        partnerSpeech:
          'Wir haben konzernweit einen Einstellungsstopp. Warum sollte ich ausgerechnet in Ihrem Bereich eine Ausnahme beim Vorstand durchboxen?',
        expectedUserRolePrompt:
          'Untermauern Sie Ihren Mehrwert mit konkreten Kosteneinsparungen und Kundenakquise, ohne defensiv zu wirken.',
        suggestedC1Connectors: [
          'Maßgeblich hierfür ist',
          'Ungeachtet der Gesamtlage',
          'Erlauben Sie mir darauf hinzuweisen',
        ],
        targetRegister: 'Selbstbewusstes, verhandlungssicheres Management-Deutsch',
        speakerProfile: 'PARTNER',
      },
    ],
    unexpectedInterruption: {
      triggerAfterSecond: 6,
      partnerInterruptSpeech:
        'Moment mal! Sie wissen genauso gut wie ich, dass der Betriebsrat den Gehaltsbändern enge Grenzen setzt. Wollen Sie etwa das gesamte Betriebsgefüge gefährden?',
      reactionObjective:
        'Parieren Sie den Vorwurf mit Verweis auf leistungsorientierte Sondervergütungen oder variable Zielvereinbarungen.',
      speakerProfile: 'INTERRUPTER',
    },
    evaluationFocus:
      'Souveräne Diplomatie unter Druck, Vermeidung von Trotzreaktionen, argumentative Stringenz.',
    variationLabel: 'Standard-Szenario',
  },
  {
    id: 'conv_4',
    title: 'Eigentümerversammlung: Energetische Sanierung vs. Sonderumlage',
    rolePartner: 'Herr Grube (Beiratsvorsitzender)',
    environment: 'Alltag & Soziales (40%)',
    openingStatement:
      'Meine Damen und Herren, wir haben die Angebote für die Fassadendämmung und Wärmepumpen vorliegen. Das bedeutet eine Sonderumlage von knapp 35.000 Euro pro Wohneinheit. Wer soll das bitte noch berappen?',
    turns: [
      {
        partnerSpeech:
          'Ich lasse mir mein Erspartes nicht für ideologische Träumereien abknöpfen! Eine Reparatur der alten Gastherme tut es doch auch noch zehn Jahre!',
        expectedUserRolePrompt:
          'Bringen Sie langfristige Wertsteigerung und staatliche KfW-Förderungen ein, um den emotionalen Widerstand zu versachlichen.',
        suggestedC1Connectors: [
          'Es liegt auf der Hand, dass',
          'In Anbetracht der Sanierungspflichten',
          'Ganz im Gegenteil',
        ],
        targetRegister: 'Moderierendes, deeskalierendes Versammlungsdeutsch',
        speakerProfile: 'PARTNER',
      },
    ],
    unexpectedInterruption: {
      triggerAfterSecond: 5,
      partnerInterruptSpeech:
        'Ach kommen Sie doch nicht mit Förderungen! Bis die bewilligt sind, ist das Haus pleite!',
      reactionObjective:
        'Beweisen Sie Sachkenntnis über die Rechtslage bei Sanierungsbeschlüssen und fordern Sie ein unabhängiges Energieberater-Gutachten.',
      speakerProfile: 'INTERRUPTER',
    },
    evaluationFocus:
      'Empathische Entschärfung von Konflikten, klare juristisch fundierte Argumentationslinie.',
    variationLabel: 'Standard-Szenario',
  },
  {
    id: 'conv_5',
    title: 'Klinik-Alltag: Aufklärungsgespräch vor einer Operation',
    rolePartner: 'Frau Lindner (besorgte Patientin)',
    environment: 'Beruf & Soziales (35%)',
    openingStatement:
      'Herr/Frau Doktor, ich habe mir den Aufklärungsbogen durchgelesen und jetzt ehrlich gesagt panische Angst vor den Narkoserisiken. Muss dieser Eingriff denn wirklich schon nächste Woche stattfinden?',
    turns: [
      {
        partnerSpeech:
          'Aber meine Nachbarin hatte nach einer Vollnarkose wochenlang Kreislaufprobleme! Können wir nicht erst noch ein halbes Jahr abwarten und Physiotherapie machen?',
        expectedUserRolePrompt:
          'Nehmen Sie die Angst ernst, erklären Sie medizinische Notwendigkeit verständlich und benennen Sie Alternativen ehrlich.',
        suggestedC1Connectors: [
          'Ich kann Ihre Sorge gut nachvollziehen',
          'Aus medizinischer Sicht gilt es abzuwägen',
          'Gleichwohl',
        ],
        targetRegister: 'Empathisches, beruhigendes Fachdeutsch ohne Arroganz',
        speakerProfile: 'PARTNER',
      },
    ],
    unexpectedInterruption: {
      triggerAfterSecond: 5,
      partnerInterruptSpeech:
        'Warten Sie kurz – heißt das jetzt, wenn ich nicht unterschreibe, lassen Sie mich einfach mit den Schmerzen nach Hause gehen?!',
      reactionObjective:
        'Deeskalieren Sie sofort, stellen Sie die Entscheidungsfreiheit der Patientin klar und schlagen Sie eine Zweitmeinung oder Bedenkzeit vor.',
      speakerProfile: 'INTERRUPTER',
    },
    evaluationFocus:
      'Empathische Wortwahl, Übersetzung von Fachjargon in Alltagssprache, ruhige Deeskalation.',
    variationLabel: 'Standard-Szenario',
  },
  {
    id: 'conv_6',
    title: 'Podiumsdiskussion: Industriestandort vs. Klimaneutralität',
    rolePartner: 'Moderator & Verbandsvertreter (Wirtschaftsforum)',
    environment: 'Akademie & Öffentlichkeit (30%)',
    openingStatement:
      'Willkommen auf dem Podium! Die Energiepreise setzen dem Mittelstand zu, gleichzeitig drängt die EU auf strikte Dekarbonisierung. Droht Deutschland die schleichende Deindustrialisierung?',
    turns: [
      {
        partnerSpeech:
          'Schöne grüne Visionen bezahlen aber keine Löhne! Wenn unsere Betriebe abwandern, ist dem Weltklima überhaupt nicht geholfen.',
        expectedUserRolePrompt:
          'Differenzieren Sie zwischen kurzfristigen Standortkosten und langfristigen Transformationschancen durch grüne Leitmärkte.',
        suggestedC1Connectors: [
          'Dem ist entgegenzuhalten',
          'Unbeschadet dessen',
          'Langfristig betrachtet',
        ],
        targetRegister: 'Prägnantes, druckvolles Debatten-Deutsch',
        speakerProfile: 'PARTNER',
      },
    ],
    unexpectedInterruption: {
      triggerAfterSecond: 6,
      partnerInterruptSpeech:
        'Da muss ich Ihnen ins Wort fallen: Das klingt verdächtig nach Subventionspolitik auf Kosten der Steuerzahler!',
      reactionObjective:
        'Parieren Sie den Zwischenruf schlagfertig mit dem Verweis auf Planungssicherheit statt Dauersubventionen.',
      speakerProfile: 'INTERRUPTER',
    },
    evaluationFocus:
      'Rhetorische Schlagfertigkeit, präzise Konnektoren, Abgrenzung ohne Polemik.',
    variationLabel: 'Standard-Szenario',
  },
  {
    id: 'conv_7',
    title: 'E-Jugend Seitenlinie: Elterngespräch über Spielzeit & Fairplay',
    rolePartner: 'Herr Becker (ehrgeiziger Fußballvater)',
    environment: 'Sportplatz & Verein (E-Jugend)',
    openingStatement:
      'Trainer, mal unter uns: Warum wechselt du meinen Timmy beim Stand von 2:1 schon wieder aus? Wenn wir die Schwächeren einwechseln, verschenken wir doch noch den Turniersieg!',
    turns: [
      {
        partnerSpeech:
          'Aber die Jungs wollen doch auch gewinnen! In anderen Vereinen spielen die besten sieben Kinder durch!',
        expectedUserRolePrompt:
          'Erklären Sie ruhig die DFB-Ausbildungsphilosophie in der E-Jugend: Entwicklung aller Kinder, Spielzeitgarantie und langfristige Motivation statt kurzfristiger Pokale.',
        suggestedC1Connectors: [
          'Mir ist wichtig zu betonen',
          'Langfristig betrachtet',
          'Aus diesem Grund',
        ],
        targetRegister: 'Herzliches, aber standfestes Trainerdeutsch auf Augenhöhe',
        speakerProfile: 'PARTNER',
      },
    ],
    unexpectedInterruption: {
      triggerAfterSecond: 5,
      partnerInterruptSpeech:
        'Ganz ehrlich, wenn Timmy hier nicht gefördert wird, melden wir ihn nächste Saison beim Leistungszentrum an!',
      reactionObjective:
        'Bleiben Sie gelassen, würdigen Sie Timmys Talent und erklären Sie sachlich, warum Geduld, Vielseitigkeit und Spielfreude im E-Jugend-Alter die beste Basis sind.',
      speakerProfile: 'INTERRUPTER',
    },
    evaluationFocus:
      'Empathische Konfliktlösung am Spielfeldrand, pädagogische Klarheit ohne Rechthaberei.',
    variationLabel: 'Standard-Szenario',
  },
  {
    id: 'conv_8',
    title: 'C-Jugend-Derby: Halbzeitansprache & Schiedsrichter-Dialog',
    rolePartner: 'Kapitän Lukas & Jungschiedsrichter',
    environment: 'Sportplatz & Verein (C-Jugend)',
    openingStatement:
      'Trainer, der Schiri pfeift jeden Zweikampf gegen uns ab! Die Jungs sind komplett auf 180 und schreien sich auf dem Platz nur noch an!',
    turns: [
      {
        partnerSpeech:
          'Aber wenn der Gegner ständig unsere Außenbahn umhaut und keine Gelbe Karte sieht, müssen wir doch irgendwie Zeichen setzen!',
        expectedUserRolePrompt:
          'Beruhigen Sie die C-Jugend-Mannschaft, lenken Sie den Fokus zurück auf das eigene Positionsspiel und verbieten Sie Frustfouls.',
        suggestedC1Connectors: [
          'Ungeachtet der Schiedsrichterleistung',
          'Worauf es jetzt ankommt',
          'In aller Deutlichkeit',
        ],
        targetRegister: 'Autoritative, fokussierte Mannschaftsführung ohne Eskalation',
        speakerProfile: 'PARTNER',
      },
    ],
    unexpectedInterruption: {
      triggerAfterSecond: 5,
      partnerInterruptSpeech:
        "Der Jungschiedsrichter kommt zur Kabinentür: 'Herr Trainer, Ihre Nummer 8 hat mich gerade beleidigt – noch ein Wort und es gibt Rot!'",
      reactionObjective:
        'Übernehmen Sie sofort Verantwortung, entschuldigen Sie sich respektvoll beim Schiedsrichter und kündigen Sie eine interne Klärung mit dem Spieler an.',
      speakerProfile: 'INTERRUPTER',
    },
    evaluationFocus:
      'Vorbildfunktion als Jugendtrainer, Deeskalation gegenüber Unparteiischen, klare Führung.',
    variationLabel: 'Standard-Szenario',
  },
  {
    id: 'conv_9',
    title: 'EV-Prüfstand-Eskalation: Thermische Derating-Abweichung vor Erprobung',
    rolePartner: 'Herr Kramer (Projektleiter Wintererprobung)',
    environment: 'Technik & EV-Entwicklung (25%)',
    openingStatement:
      'Frau/Herr Kollege, der Tieflader nach Nordschweden steht Montag bereit. Das Derating hat die Batterie doch geschützt — geben Sie den Softwarestand für Modul 4 jetzt frei oder nicht?',
    turns: [
      {
        partnerSpeech:
          'Jeder Tag Verzug kostet uns sechzigtausend Euro Standgebühr! Unser Zulieferer sagt übrigens, seine Kühlplatte sei hundertprozentig maßhaltig.',
        expectedUserRolePrompt:
          'Trennen Sie Kosten- und Lieferdruck sauber von fehlenden FMEA-Nachweisen, und schlagen Sie konkrete Prüfschritte bis Donnerstag vor.',
        suggestedC1Connectors: [
          'Ich nehme den Termin- und Kostendruck ernst, gleichwohl',
          'Aus den vorliegenden Protokollen lässt sich noch nicht ableiten',
          'Unter der Voraussetzung, dass',
        ],
        targetRegister: 'Sachliches, evidential-präzises Ingenieursdeutsch',
        speakerProfile: 'PARTNER',
      },
    ],
    unexpectedInterruption: {
      triggerAfterSecond: 5,
      partnerInterruptSpeech:
        'Warten Sie! Wollen Sie mir jetzt ernsthaft sagen, wir sollen die komplette Wintererprobung abblasen, nur weil ein Sensor vielleicht falsch kalibriert war?!',
      reactionObjective:
        'Räumen Sie das Missverständnis (Prüfprogramm vs. Komplettabsage) ruhig aus und behalten Sie die Entscheidungsbefugnis des Safety-Boards konsequent bei.',
      speakerProfile: 'INTERRUPTER',
    },
    evaluationFocus:
      'Trennung von Befund, Hypothese und Freigabe; ruhiges Parieren unter Kosten- und Terminzwang.',
    variationLabel: 'Standard-Szenario',
  },
  {
    id: 'conv_10',
    title: 'Schwäbisches Vereinsfest: Mehrdeutige Zusage freundlich konkretisieren',
    rolePartner: 'Manfred (langjähriger Vereinswart)',
    environment: 'Alltag & Soziales (Schwaben)',
    openingStatement:
      'Du, wegen dem Turnieraufbau am Samstag — das passt scho irgendwie, mir schauen halt am Freitagabend mal, wer grad Zeit hat, gell?',
    turns: [
      {
        partnerSpeech:
          'Mach dir doch keinen Stress, wir machen das hier schon seit zwanzig Jahren so und es hat noch immer geklappt.',
        expectedUserRolePrompt:
          'Würdigen Sie die Erfahrung ohne Herablassung und konkretisieren Sie freundlich Uhrzeit, Schlüsselübergabe und Rückmeldeweg.',
        suggestedC1Connectors: [
          'Vielen Dank für eure Erfahrung, mir hilft zur Planung noch kurz',
          'Damit am Samstag niemand vor verschlossener Tür steht',
          'Könnten wir kurz festhalten, bis wann',
        ],
        targetRegister: 'Warmherziges, verbindliches Vereinsdeutsch ohne Besserwisserei',
        speakerProfile: 'PARTNER',
      },
    ],
    unexpectedInterruption: {
      triggerAfterSecond: 5,
      partnerInterruptSpeech:
        'Jetzt hör doch auf mit deinen Checklisten! Traust du mir etwa nicht zu, dass ich den Platz aufschließe?',
      reactionObjective:
        'Deeskalieren Sie sofort mit einer Ich-Botschaft, vermeiden Sie jedes Misstrauenssignal und sichern Sie eine einfache Bestätigung per Kurznachricht.',
      speakerProfile: 'INTERRUPTER',
    },
    evaluationFocus:
      'Sensible Deutung von „passt scho“ / „schauen wir mal“; Beziehungspflege bei klaren Absprachen.',
    variationLabel: 'Standard-Szenario',
  },
];

export const initialMediationTasks: MediationTask[] = [
  {
    id: 'med_1',
    title: 'Bauordnungsamt: Paragraph 34 BauGB bürgernah erklären',
    mediationType: 'Fachsprache -> Bürgernah',
    sourceMaterial:
      '„Die Zulässigkeit eines Vorhabens im unbeplanten Innenbereich richtet sich nach § 34 BauGB. Ein solches Vorhaben ist zulässig, wenn es sich nach Art und Maß der baulichen Nutzung, der Bauweise und der Grundstücksfläche, die überbaut werden soll, in die Eigenart der näheren Umgebung einfügt und die Erschließung gesichert ist. Die Anforderungen an gesunde Wohn- und Arbeitsverhältnisse müssen gewahrt bleiben; das Ortsbild darf nicht beeinträchtigt werden.“',
    recipientContext:
      'Ihre Nachbarin Frau Müller versteht den Brief des Bauamts nicht und befürchtet, sie dürfe ihre Garage nicht aufstocken.',
    criticalPitfallsToAvoid: [
      "Juristische Floskeln unreflektiert übernehmen ('Art und Maß der baulichen Nutzung')",
      'Falsche juristische Zusicherungen geben',
      'Zu komplizierte Schachtelsätze verwenden',
    ],
    targetReformulationModel:
      'Frau Müller, nach diesem Text muss sich das Vorhaben unter anderem in Nutzung, Größe, Bauweise und bebauter Fläche in die nähere Umgebung einfügen. Auch die Erschließung, etwa die Anbindung an Wege und Versorgungsleitungen, muss gesichert sein. Gesunde Wohn- und Arbeitsbedingungen sowie das Ortsbild sind ebenfalls zu beachten. Ob Ihre Aufstockung zulässig ist, lässt sich aus dem Auszug allein nicht entscheiden; das muss das Bauamt im Einzelfall prüfen.',
  },
  {
    id: 'med_2',
    title: "Englischer Fachtext: 'Technical Debt & Architectural Drift' vermitteln",
    mediationType: 'Englisch -> Idiomatisches Deutsch',
    sourceMaterial:
      '„Accumulated technical debt has led to severe architectural drift, rendering rapid feature deployment unsustainable without a comprehensive refactoring cycle.“',
    recipientContext:
      'Ein deutscher Geschäftsführer ohne Software-Hintergrund fragt, warum das nächste Feature 6 Wochen statt 1 Woche dauert.',
    criticalPitfallsToAvoid: [
      "Anglizismen wörtlich übernehmen ('wir haben technische Schulden und architektonischen Drift')",
      'Zu abstrakt bleiben ohne greifbare Metapher',
    ],
    targetReformulationModel:
      'Vereinfacht gesagt: Aufgeschobene technische Verbesserungen haben dazu geführt, dass sich der Aufbau unserer Software stark vom vorgesehenen Entwurf entfernt hat. Deshalb können wir neue Funktionen nicht dauerhaft schnell bereitstellen, ohne den bestehenden Code umfassend zu überarbeiten. Warum dafür genau sechs Wochen nötig sind, erklärt der Auszug nicht.',
  },
  {
    id: 'med_3',
    title: 'Medizinischer Fachbericht: Epikrise für Angehörige erklären',
    mediationType: 'Medizinische Fachsprache -> Empathische Alltagssprache',
    sourceMaterial:
      '„Der Patient wies eine transitorische ischämische Attacke im Stromgebiet der A. cerebri media links auf. Kardioembolische Genese bei paroxysmalem Vorhofflimmern wahrscheinlich. Initiale therapeutische Antikoagulation eingeleitet, bildmorphologisch kein Nachweis eines hämorrhagischen Infarktareals.“',
    recipientContext:
      'Die besorgte Ehefrau des Patienten versteht den Arztbrief nicht und befürchtet eine dauerhafte Lähmung.',
    criticalPitfallsToAvoid: [
      "Medizinische Fachbegriffe wie 'Antikoagulation' oder 'kardioembolisch' unübersetzt lassen",
      'Unbegründete Panik erzeugen oder voreilige Entwarnung ohne ärztlichen Vorbehalt geben',
    ],
    targetReformulationModel:
      'Frau Schneider, der Bericht beschreibt eine vorübergehende Durchblutungsstörung in einem Bereich der linken Gehirnhälfte. Als wahrscheinliche Ursache vermuten die Ärzte ein Blutgerinnsel aus dem Herzen bei zeitweise auftretendem unregelmäßigem Herzschlag. Sie haben eine Behandlung begonnen, die die Blutgerinnung hemmt. In den Bildern wurde kein blutiges Infarktgebiet nachgewiesen. Der Auszug allein erlaubt keine sichere Aussage zu bleibenden Folgen oder zur weiteren Entwicklung. Bitte besprechen Sie diese Fragen und die weitere Behandlung mit dem ärztlichen Team.',
  },
  {
    id: 'med_4',
    title: 'Arbeitsrecht: Änderungskündigung konstruktiv vermitteln',
    mediationType: 'Juristische Klausel -> Konstruktives Mitarbeitergespräch',
    sourceMaterial:
      'Fiktives Übungsschreiben: „Aufgrund betriebsorganisatorischer Umstrukturierungen entfällt Ihr bisheriger Aufgabenbereich. Wir sprechen hiermit eine ordentliche Änderungskündigung aus und bieten Ihnen zeitgleich die Fortsetzung des Arbeitsverhältnisses als Teamkoordinator am Standort Nord zu geänderten Vergütungskonditionen an. Einen Vorbehalt nach § 2 KSchG müssen Sie innerhalb der Kündigungsfrist, spätestens jedoch innerhalb von drei Wochen nach Zugang der Kündigung erklären.“',
    recipientContext:
      'Ein langjähriger Mitarbeiter fühlt sich durch den juristischen Brief vor den Kopf gestoßen und denkt an sofortige Klage.',
    criticalPitfallsToAvoid: [
      'Kalte Paragrafenreiter-Sprache wiederholen',
      'Die Kündigungsfrist und die Höchstfrist von drei Wochen für den Vorbehalt verschweigen oder Nachteilsfreiheit versprechen',
    ],
    targetReformulationModel:
      'Herr Becker, das Schreiben kündigt Ihr bisheriges Arbeitsverhältnis und bietet zugleich eine Fortsetzung als Teamkoordinator am Standort Nord mit geänderten Vergütungskonditionen an. Sie können das Angebot unter dem Vorbehalt annehmen, dass die Änderungen sozial gerechtfertigt sind. Laut Text muss dieser Vorbehalt innerhalb der Kündigungsfrist, spätestens innerhalb von drei Wochen nach Zugang erklärt werden. Das bedeutet nicht, dass Ihnen sicher keine Nachteile entstehen. Lassen Sie Ihre Möglichkeiten und die geltenden Fristen zeitnah fachkundig prüfen; diese Sprachübung ersetzt keine Rechtsberatung.',
  },
  {
    id: 'med_5',
    title: 'ESG-Nachhaltigkeitsbericht: Lieferketten-Audit für Kunden zusammenfassen',
    mediationType: 'Technischer Compliance-Bericht -> Überzeugendes Kundenanschreiben',
    sourceMaterial:
      '„Im Berichtszeitraum wurden 94,2 % der Tier-1-Zulieferer gemäß ISO 14001 auditiert. Zwei nicht-konforme Vorlieferanten im Bereich Verpackungslogistik wurden einem verbindlichen Corrective Action Plan (CAP) unterzogen; bei Nichterfüllung binnen Q3 erfolgt die Auslistung.“',
    recipientContext:
      'Ein umweltbewusster Großkunde fragt kritisch nach, ob Ihre Lieferkette wirklich nachhaltig kontrolliert wird.',
    criticalPitfallsToAvoid: [
      "Unverständliche Abkürzungen wie 'Tier-1' oder 'CAP' unkommentiert übernehmen",
      'Die zwei festgestellten Mängel vertuschen statt transparent als aktives Qualitätsmanagement darzustellen',
    ],
    targetReformulationModel:
      'Sehr geehrte Frau Dr. König, im Berichtszeitraum wurden 94,2 Prozent unserer direkten Zulieferer nach ISO 14001 geprüft. Bei zwei Vorlieferanten in der Verpackungslogistik wurden Abweichungen festgestellt und verbindliche Nachbesserungspläne vereinbart. Werden diese nicht bis zum Ende des dritten Quartals erfüllt, werden die betroffenen Lieferanten ausgelistet. Der Bericht belegt damit weder eine lückenlose Kontrolle noch die Nachhaltigkeit der gesamten Lieferkette.',
  },
  {
    id: 'med_6',
    title: 'Fiktives Vereinstraining: Kleine Spielformen Eltern erklären',
    mediationType: 'Übungstext -> Verständlicher Elternabend-Impuls',
    sourceMaterial:
      'Fiktiver Übungstext, kein DFB-Konzeptpapier: „In unserem E-Jugend-Training spielen die Kinder in kleinen Teams auf reduzierten Feldern mit vier Minitoren. Ziel sind viele Ballaktionen und eigene Entscheidungen. Altersgeretes Lernen steht vor frühzeitiger taktischer Spezialisierung und ergebnisorientiertem Tabellendruck.“'.replace(
        'Altersgeretes',
        'Altersgerechtes',
      ),
    recipientContext:
      'Beim Elternabend der E-Jugend fragen kritische Eltern, warum die Kinder auf vier kleine Tore statt auf ein großes Tor mit festem Torwart spielen.',
    criticalPitfallsToAvoid: [
      "Bürokratische Begriffe wie 'Aktionsdichte' oder 'kognitive Entscheidungsräume' unübersetzt lassen",
      'Den Eindruck erwecken, Leistung oder Wettkampf seien nicht mehr erwünscht',
    ],
    targetReformulationModel:
      'Liebe Eltern, wir nutzen im E-Jugend-Training kleine Teams, kleinere Felder und vier Minitore, damit die Kinder oft am Ball sind und selbst entscheiden können, ob sie dribbeln oder abspielen. Im Mittelpunkt steht altersgerechtes Lernen, nicht frühe taktische Spezialisierung oder der Tabellenplatz. Der Text nennt ein Trainingsziel, aber keine gemessene Zahl zusätzlicher Ballkontakte und keine verbindliche Wettkampfregel.',
  },
  {
    id: 'med_7',
    title: 'EV-FMEA-Risikoanalyse: Thermische Nachweisgrenze für den Einkauf erklären',
    mediationType: 'Technische Risikoanalyse -> Verständliche Management-Vorlage',
    sourceMaterial:
      '„Gemäß vorläufiger FMEA-Bewertung (AP hoch) ist bei Modul 4 ein temperaturbedingtes Derating unter Schnelllade-Last beobachtet worden. Solange die Durchflusssensorik nicht rekalibriert und die Pumpenansteuerung V2.3 nicht im 5-fach-Wiederholungstest verifiziert ist, bleibt eine eingeschränkte Erprobungsfreigabe ausgesetzt.“',
    recipientContext:
      'Die kaufmännische Einkaufsleitung fragt, warum Sie eine dringende Speditionsbuchung für Prototypen noch nicht freigeben können.',
    criticalPitfallsToAvoid: [
      'Ungeklärte Kürzel wie „AP hoch“ oder „V2.3“ ohne funktionale Erklärung übernehmen',
      'Ein noch nicht bewiesenes Bauteilversagen als sicher behaupten',
    ],
    targetReformulationModel:
      'Für unsere Transportplanung bedeutet der aktuelle Prüfbericht: Bei schnellem Laden stieg an einem Batteriemodul die Temperatur an, sodass die Elektronik die Ladeleistung vorsorglich gedrosselt hat. Bislang ist offen, ob ein Messsensor oder die Kühlmittelpumpe nach dem letzten Software-Update den Effekt auslöst. Bevor wir Testfahrzeuge verschicken, fordert das Sicherheitsteam fünf wiederholbare Vergleichsmessungen mit neu eingestelltem Sensor. Eine endgültige Absage der Erprobung ist das ausdrücklich noch nicht.',
  },
  {
    id: 'med_8',
    title: 'Englischer Safety-Report: „Thermal Propagation Containment“ vermitteln',
    mediationType: 'Englisch -> Verständliches Betriebsrats-Deutsch',
    sourceMaterial:
      '„Cell-level thermal runaway containment passed the 30-minute egress benchmark without adjacent module ignition; however, gas-venting valve telemetry exhibited a 140 ms latency spike under sub-zero preconditioning.“',
    recipientContext:
      'Ein Mitglied des Betriebsrats möchte auf einer Mitarbeiterversammlung ehrlich wissen, ob die neuen Prototypen im Testbetrieb sicher sind.',
    criticalPitfallsToAvoid: [
      'Englische Fachwörter wie „egress benchmark“ oder „telemetry latency spike“ wörtlich stehen lassen',
      'Den beobachteten Ventil-Zeitversatz bei Kälte verschweigen oder zur akuten Explosionsgefahr aufbauschen',
    ],
    targetReformulationModel:
      'Im Sicherheitsversuch hat sich ein einzelner Zellfehler innerhalb von 30 Minuten nicht auf Nachbarmodule ausgebreitet, sodass die vorgegebene Ausstiegszeit eingehalten wurde. Gleichzeitig zeigte das Überdruckventil bei extremen Minustemperaturen eine messbare Verzögerung der Messdatenübertragung um 140 Millisekunden. Was dieser Zeitversatz für den Winterbetrieb genau bedeutet, klärt die laufende Nachprüfung; der Auszug allein erteilt noch keine generelle Betriebs- oder Serienfreigabe.',
  },
  {
    id: 'med_9',
    title: 'Schiedsrichter-Sonderbericht: Vorfall sachlich für die U15-Eltern aufbereiten',
    mediationType: 'Sportgerichts-Protokoll -> Deeskalierende Elterninformation',
    sourceMaterial:
      'Fiktiver Auszug: „In der 68. Spielminute wurde das C-Jugend-Punktspiel nach wiederholten unsportlichen Zwischenrufen aus dem Zuschauerbereich des Gastvereins für drei Minuten unterbrochen. Nach Ansprache beider Spielführer und räumlicher Entfernung der rufenden Person konnte die Partie ordnungsgemäß zu Ende geführt werden; ein Sonderbericht an den Jugendstaffelleiter erfolgt fristgerecht.“',
    recipientContext:
      'Mehrere Eltern haben nur Gerüchte auf dem Parkplatz gehört („Wir werden wohl vom Spielbetrieb ausgeschlossen!“) und bitten um Einordnung.',
    criticalPitfallsToAvoid: [
      'Den betroffenen Zuschauer öffentlich an den Pranger stellen oder Namen nennen',
      'Strafmaße des Staffelleiters vorwegnehmen oder verharmlosen',
    ],
    targetReformulationModel:
      'Liebe Eltern, im gestrigen C-Jugend-Spiel hat der Schiedsrichter die Partie in der Schlussphase für drei Minuten unterbrochen, nachdem es wiederholte Zwischenrufe von außerhalb des Feldes gegeben hatte. Nach einer kurzen Klärung über die Spielführer wurde das Spiel regulär zu Ende gespielt. Der Vorfall wird nun sachlich an den Jugendstaffelleiter gemeldet; ob und welche Auflagen daraus folgen, liegt allein beim Verband und ist offen. Für den kommenden Samstag gilt unsere vereinbarte Fairplay-Zone entlang der Seitenlinie.',
  },
];

export const speakingPrompts: SpeakingPrompt[] = [
  {
    id: 'sp_1',
    title: 'Nomen-Verb-Verbindung: In Betracht ziehen',
    targetSentence:
      'Wir sollten alternative Antriebstechnologien ernsthaft in Betracht ziehen.',
    category: 'Nomen-Verb-Verbindungen',
    contextBrief: 'Strategiemeeting in der Automobilindustrie',
    difficultyLevel: 'C1.1',
    phoneticFocusWords: [
      {
        word: 'Betracht',
        ipa: '[bəˈtʁaxt]',
        focusArea: 'Dunkler Ach-Laut [x]',
        tipDe:
          "Nach dem Vokal 'a' tief am weichen Gaumensegel reiben. Nicht wie ein hartes 'k' abstoßen.",
      },
      {
        word: 'ernsthaft',
        ipa: '[ˈɛʁnsthaft]',
        focusArea: 'Konsonantencluster (-rnst-)',
        tipDe:
          'Alle vier Konsonanten präzise artikulieren, ohne Zwischenvokale einzuschieben.',
      },
    ],
    audioPromptSpeed: 1.05,
  },
  {
    id: 'sp_2',
    title: 'Gewissheit & Argumentation: Außer Zweifel stehen',
    targetSentence:
      'Dass die Umstellung erhebliche Ressourcen erfordert, steht außer Zweifel.',
    category: 'Debatte & Argumentation',
    contextBrief: 'Finanzplanung & Transformationsdialog',
    difficultyLevel: 'C1.1',
    phoneticFocusWords: [
      {
        word: 'Zweifel',
        ipa: '[ˈtsvaɪ̯fl̩]',
        focusArea: 'Affrikate [ts]',
        tipDe:
          "Das 'Z' als explosive Lautverbindung aus [t] und [s] sprechen, niemals wie ein weiches englisches 'z'.",
      },
      {
        word: 'Ressourcen',
        ipa: '[ʁɛˈsʊʁsn̩]',
        focusArea: 'Silbenreduktion & Reibe-S',
        tipDe:
          "Stimmloses, scharfes 'ss' mit zügigem Übergang in den vokalisierten Endsilbenlaut.",
      },
    ],
    audioPromptSpeed: 1.1,
  },
  {
    id: 'sp_3',
    title: 'Gehobene Temporalangabe: Binnen dreier Monate',
    targetSentence:
      'Binnen dreier Monate müssen wir ein belastbares Sanierungskonzept vorlegen.',
    category: 'Gehobene Syntax & Genitiv',
    contextBrief: 'Krisensitzung der Geschäftsleitung',
    difficultyLevel: 'C1.2',
    phoneticFocusWords: [
      {
        word: 'binnen',
        ipa: '[ˈbɪnən]',
        focusArea: 'Kurzer geschlossener I-Vokal [ɪ]',
        tipDe:
          "Das 'i' kurz und hell ansetzen, gefolgt von stimmhaftem Nasalkonsonanten.",
      },
      {
        word: 'Sanierungskonzept',
        ipa: '[zaˈniːʁʊŋs kɔnˌtsɛpt]',
        focusArea: 'Stimmhaftes Anlaut-S [z]',
        tipDe:
          "Das Anfangs-S in 'Sanierung' wie ein sanftes Summen stimmhaft anstimmen.",
      },
    ],
    audioPromptSpeed: 1.0,
  },
  {
    id: 'sp_4',
    title: 'Diplomatische Einschränkung: Abstriche machen',
    targetSentence:
      'Bei der Datenintegrität dürfen wir unter keinen Umständen Abstriche machen.',
    category: 'Verhandlung & Compliance',
    contextBrief: 'Audit & IT-Sicherheitsausschuss',
    difficultyLevel: 'C1.2',
    phoneticFocusWords: [
      {
        word: 'Abstriche',
        ipa: '[ˈapˌʃtʁɪçə]',
        focusArea: 'Weicher Ich-Laut [ç]',
        tipDe:
          'Den Zungenrücken an den harten Vordergaumen anlegen und einen weichen Hauchlaut erzeugen.',
      },
      {
        word: 'Integrität',
        ipa: '[ɪnteɡʁiˈtɛːt]',
        focusArea: 'Endsilbenbetonung & langes Ä [ɛː]',
        tipDe: "Der Hauptakzent liegt unmissverständlich auf der letzten Silbe '-tät'.",
      },
    ],
    audioPromptSpeed: 1.05,
  },
  {
    id: 'sp_5',
    title: 'Diplomatischer Einwand: Erlauben Sie mir',
    targetSentence:
      'Erlauben Sie mir darauf hinzuweisen, dass diese Maßnahme alternativlos ist.',
    category: 'Diplomatisches Führungsdeutsch',
    contextBrief: 'Aufsichtsratssitzung',
    difficultyLevel: 'C1.2',
    phoneticFocusWords: [
      {
        word: 'hinzuweisen',
        ipa: '[hɪnˈtsuːˌvaɪ̯zn̩]',
        focusArea: 'Langes U [uː] und Diphthong [aɪ̯]',
        tipDe: "Lippen für das 'u' stark runden, danach sanfter Übergang zum Diphthong.",
      },
      {
        word: 'alternativlos',
        ipa: '[altɛʁnaˈtiːfˌloːs]',
        focusArea: "Auslautverhärtung [f] bei '-tiv'",
        tipDe:
          "Das 'v' in '-tiv' wird im Deutschen stimmlos wie ein deutsches 'f' artikuliert.",
      },
    ],
    audioPromptSpeed: 1.1,
  },
  {
    id: 'sp_6',
    title: 'Formelle Konsultation: Ins Benehmen setzen',
    targetSentence:
      'Bevor die Richtlinie verabschiedet wird, müssen wir uns mit der Rechtsabteilung ins Benehmen setzen.',
    category: 'Amts- & Rechtsdeutsch',
    contextBrief: 'Behördenabstimmung & Governance',
    difficultyLevel: 'C2',
    phoneticFocusWords: [
      {
        word: 'Benehmen',
        ipa: '[bəˈneːmən]',
        focusArea: 'Langes geschlossenes E [eː]',
        tipDe:
          'Mundspalt schmal halten, Zungenränder an die oberen Backenzähne drücken, Vokal lang halten.',
      },
      {
        word: 'verabschiedet',
        ipa: '[fɛɐ̯ˈʔapˌʃiːdət]',
        focusArea: 'Knacklaut (Glottal Stop) [ʔ]',
        tipDe:
          "Kurze Stimmlippensperre vor der Vorsilbe 'ab-', wie ein winziger Atemstopp.",
      },
    ],
    audioPromptSpeed: 1.15,
  },
  {
    id: 'sp_7',
    title: 'E-Jugend Coaching: Ermutigung & Spielintelligenz',
    targetSentence:
      'Lass den Kopf oben, such den freien Raum und trau dich mutig ins Eins-gegen-Eins!',
    category: 'Fußball-Trainer (E-Jugend)',
    contextBrief: 'Anfeuerung & positives Coaching am Spielfeldrand (U10/U11)',
    difficultyLevel: 'C1.1',
    phoneticFocusWords: [
      {
        word: 'freien Raum',
        ipa: '[ˈfʁaɪ̯ən ʁaʊ̯m]',
        focusArea: 'Zäpfchen-R [ʁ] und Diphthonge [aɪ̯] / [aʊ̯]',
        tipDe:
          'Das R weich im Rachen ansetzen und die Diphthonge klar ausformen, damit der Zuruf über den Platz trägt.',
      },
      {
        word: 'trau dich',
        ipa: '[ˈtʁaʊ̯ dɪç]',
        focusArea: 'Ich-Laut [ç] nach hellem Vokal',
        tipDe:
          'Die Zungenspitze bleibt hinter den unteren Schneidezähnen; ein weicher Luftstrom entsteht.',
      },
    ],
    audioPromptSpeed: 1.05,
  },
  {
    id: 'sp_8',
    title: 'C-Jugend Taktik: Umschaltspiel & Kompaktheit',
    targetSentence:
      'Sobald wir den Ball verlieren, schließen wir sofort die Halbräume und rücken geschlossen nach!',
    category: 'Fußball-Trainer (C-Jugend)',
    contextBrief: 'Taktische Halbzeitansage für Großfeld-Umschaltspiel (U14/U15)',
    difficultyLevel: 'C1.2',
    phoneticFocusWords: [
      {
        word: 'Halbräume',
        ipa: '[ˈhalpˌʁɔɪ̯mə]',
        focusArea: 'Auslautverhärtung [p] + Umlaut-Diphthong [ɔɪ̯]',
        tipDe:
          "Das 'b' in 'Halb-' wird vor 'r' wie ein kurzes, klares 'p' gesprochen; 'äu' rund als [ɔɪ̯].",
      },
      {
        word: 'geschlossen',
        ipa: '[ɡəˈʃlɔsn̩]',
        focusArea: 'Kurzes offenes O [ɔ] vor Doppel-S',
        tipDe:
          "Das 'o' bleibt kurz und knackig, die Endung '-en' verschmilzt weich mit dem 's'.",
      },
    ],
    audioPromptSpeed: 1.12,
  },
  {
    id: 'sp_9',
    title: 'Partizipialattribut im Prüfstandsbericht',
    targetSentence:
      'Die am Prüfstand unter Volllast gemessenen Temperaturwerte erlauben noch keine abschließende Serienfreigabe.',
    category: 'Technik & Wissenschaft (EV)',
    contextBrief: 'Safety-Review nach Schnelllade-Versuchen',
    difficultyLevel: 'C1.2',
    phoneticFocusWords: [
      {
        word: 'Prüfstand',
        ipa: '[ˈpʁyːfˌʃtant]',
        focusArea: 'Langes gerundetes Ü [yː] & Auslautverhärtung [t]',
        tipDe: 'Lippen beim Ü eng vorstülpen und den Stammvokal lang halten; das auslautende -d wird hart als [t] gesprochen.',
      },
      {
        word: 'abschließende',
        ipa: '[ˈapˌʃliːsn̩də]',
        focusArea: 'Stimmloses ß [s] nach langem I [iː]',
        tipDe: 'Das lange I sauber vor dem scharfen, stimmlosen S halten.',
      },
    ],
    audioPromptSpeed: 1.05,
  },
  {
    id: 'sp_10',
    title: 'Konjunktiv I & Evidenzdistanz',
    targetSentence:
      'Dem Prüfbericht zufolge habe die Steuereinheit das Signal verspätet verarbeitet, wobei die eigentliche Ursache offenbleibe.',
    category: 'Indirekte Rede & Evidenz',
    contextBrief: 'Fachübergreifende Statusmeldung (ECU/TCU)',
    difficultyLevel: 'C2',
    phoneticFocusWords: [
      {
        word: 'zufolge',
        ipa: '[tsuˈfɔlɡə]',
        focusArea: 'Affrikate [ts] und kurzes offenes O [ɔ]',
        tipDe: 'Wortakzent liegt auf der zweiten Silbe (-folge), nicht auf zu-.',
      },
      {
        word: 'offenbleibe',
        ipa: '[ˈɔfn̩ˌblaɪ̯bə]',
        focusArea: 'Silbisches N [n̩] & Diphthong [aɪ̯]',
        tipDe: 'Silbentrennung nach „offen-“ deutlich halten, damit der Konjunktiv-I-Auslaut „-e“ gut hörbar bleibt.',
      },
    ],
    audioPromptSpeed: 1.02,
  },
  {
    id: 'sp_11',
    title: 'Deeskalierende Elternkommunikation (U15)',
    targetSentence:
      'Mir liegt am Herzen, dass jedes Kind verlässliche Spielpraxis sammelt, ohne dass unsere mannschaftliche Ordnung darunter leidet.',
    category: 'Fußball-Trainer (Elterngespräch)',
    contextBrief: 'Vertrauliches Gespräch an der Seitenlinie nach dem Punktspiel',
    difficultyLevel: 'C1.1',
    phoneticFocusWords: [
      {
        word: 'Herzen',
        ipa: '[ˈhɛʁtsn̩]',
        focusArea: 'Vokalisches R [ɐ̯/ʁ] vor Affrikate [ts]',
        tipDe: 'Das R nach kurzem E weich mitschwingen lassen und klar auf [ts] schließen.',
      },
      {
        word: 'Ordnung',
        ipa: '[ˈɔʁdnʊŋ]',
        focusArea: 'Velarer Nasal [ŋ] im Auslaut',
        tipDe: 'Am Ende kein hörbares hartes „-gk“ anhängen; den Nasal weich am Hintergaumen ausklingen lassen.',
      },
    ],
    audioPromptSpeed: 1.0,
  },
  {
    id: 'sp_12',
    title: 'Regionalpragmatik: Höfliche Termin-Konkretisierung',
    targetSentence:
      'Sehr gern schauen wir gemeinsam darauf — bis wann bräuchtest du spätestens eine verbindliche Rückmeldung von mir?',
    category: 'Alltag & Soziales (Pragmatik)',
    contextBrief: 'Klärung nach einem vagen „Schauen wir mal“ im Vereinsumfeld',
    difficultyLevel: 'C1.1',
    phoneticFocusWords: [
      {
        word: 'bräuchtest',
        ipa: '[ˈbʁɔɪ̯çtəst]',
        focusArea: 'Umlaut-Diphthong [ɔɪ̯] + Ich-Laut [ç]',
        tipDe: 'Nach dem runden [ɔɪ̯] die Zunge rasch für das weiche [ç] heben — kein harter Ach-Laut.',
      },
      {
        word: 'verbindliche',
        ipa: '[fɛɐ̯ˈbɪntlɪçə]',
        focusArea: 'Unbetonte Vorsilbe ver- [fɛɐ̯] & Auslautverhärtung',
        tipDe: 'Vorsilbe locker vokalisiert ansprechen; Hauptbetonung liegt klar auf „-bind-“.',
      },
    ],
    audioPromptSpeed: 1.04,
  },
];

export function createInitialSrsVocabulary(now: number = Date.now()): VocabularySrsItem[] {
  const DAY = 86_400_000;
  const item = (
    id: string,
    germanCollocation: string,
    meaningDe: string,
    exampleSentence: string,
    register: string,
    category: string,
    overrides: Partial<VocabularySrsItem> = {},
  ): VocabularySrsItem => ({
    id,
    germanCollocation,
    meaningDe,
    exampleSentence,
    register,
    category,
    stage: 0,
    easeFactor: 2.5,
    intervalDays: 0,
    repetitions: 0,
    nextReviewTimestamp: now,
    lastReviewedTimestamp: null,
    createdTimestamp: now,
    consecutiveErrors: 0,
    isMastered: false,
    sourceUnitId: '',
    ...overrides,
  });

  return [
    item(
      'voc_zur_sprache_bringen',
      'etw. zur Sprache bringen',
      'Ein heikles oder wichtiges Thema in einer Sitzung ansprechen',
      'In der Klausurtagung brachte sie die ungelösten Personalengpässe mutig zur Sprache.',
      'Gehoben / B2B & Führung',
      'Nomen-Verb-Verbindung',
    ),
    item(
      'voc_in_erwaegung_ziehen',
      'etw. in Erwägung ziehen',
      'Eine Option sorgfältig und rational abwägen / prüfen',
      'Wir sollten eine strategische Neupriorisierung der Ressourcen ernsthaft in Erwägung ziehen.',
      'Professionell / Strategie',
      'Nomen-Verb-Verbindung',
    ),
    item(
      'voc_auf_die_lange_bank_schieben',
      'etw. auf die lange Bank schieben',
      'Eine unangenehme Entscheidung unnötig aufschieben / verzögern',
      'Diesen heiklen Compliance-Fall dürfen wir keinesfalls auf die lange Bank schieben.',
      'Idiomatisch / Verhandlung',
      'Redewendung & Idiom',
      { stage: 1, intervalDays: 1, repetitions: 1, lastReviewedTimestamp: now - DAY },
    ),
    item(
      'voc_abstrich_machen',
      'Abstriche machen bei etw.',
      'Qualitätseinbußen oder Kompromisse hinnehmen müssen',
      'Bei der Produktsicherheit dürfen wir unter keinen Umständen Abstriche machen.',
      'Wirtschaft & Technik',
      'Nomen-Verb-Verbindung',
      {
        stage: 2,
        intervalDays: 3,
        repetitions: 2,
        lastReviewedTimestamp: now - 4 * DAY,
        nextReviewTimestamp: now - DAY,
      },
    ),
    item(
      'voc_ins_gewicht_fallen',
      'schwer ins Gewicht fallen',
      'Ausschlaggebende oder gravierende Bedeutung haben',
      'Die gestiegenen Energiekosten fallen bei der Gesamtrechnung schwer ins Gewicht.',
      'Analytisch / Feuilleton',
      'Nomen-Verb-Verbindung',
      {
        stage: 3,
        intervalDays: 7,
        repetitions: 3,
        lastReviewedTimestamp: now - 2 * DAY,
        nextReviewTimestamp: now + 5 * DAY,
      },
    ),
    item(
      'voc_in_abrede_stellen',
      'etw. in Abrede stellen',
      'Etwas bestreiten, leugnen oder öffentlich anzweifeln',
      'Niemand stellt die fachliche Kompetenz des Projektteams ernsthaft in Abrede.',
      'Gehoben / Debatte',
      'Nomen-Verb-Verbindung',
    ),
    item(
      'voc_den_vorzug_geben',
      'jmdm./etw. den Vorzug geben',
      'Eine Option oder Person gegenüber einer anderen bewusst bevorzugen',
      'Bei gleicher Qualifikation geben wir dem internen Bewerber den Vorzug.',
      'Formell / HR & Führung',
      'Nomen-Verb-Verbindung',
    ),
    item(
      'voc_in_kauf_nehmen',
      'etw. in Kauf nehmen',
      'Nachteile oder Risiken bewusst akzeptieren, um ein höheres Ziel zu erreichen',
      'Für maximale Datensicherheit nehmen wir längere Ladezeiten in Kauf.',
      'Standard / Business',
      'Nomen-Verb-Verbindung',
      { stage: 1, intervalDays: 1, repetitions: 1, lastReviewedTimestamp: now - DAY },
    ),
    item(
      'voc_ins_benehmen_setzen',
      'sich mit jmdm. ins Benehmen setzen',
      'Sich formell und einvernehmlich mit einer zuständigen Stelle abstimmen',
      'Vor Erlass der Richtlinie setzen wir uns mit dem Betriebsrat ins Benehmen.',
      'Amts- & Rechtsdeutsch',
      'Nomen-Verb-Verbindung',
    ),
    item(
      'voc_ausser_zweifel_stehen',
      'außer Zweifel stehen',
      'Unbestreitbar sein, völlig sicher feststehen',
      'Dass die Transformation erhebliche Investitionen erfordert, steht außer Zweifel.',
      'Gehoben / Argumentation',
      'Nomen-Verb-Verbindung',
    ),
    item(
      'voc_rechnung_tragen',
      'etw. (Dat.) Rechnung tragen',
      'Einen Umstand oder ein Bedürfnis angemessen berücksichtigen',
      'Der neue Tarifvertrag trägt der gestiegenen Inflation angemessen Rechnung.',
      'Gehoben / Wirtschaft',
      'Nomen-Verb-Verbindung',
    ),
    item(
      'voc_folge_leisten',
      'einer Aufforderung Folge leisten',
      'Eine offizielle Anweisung oder Einladung befolgen',
      'Alle Abteilungen leisteten der Einladung zur Krisensitzung unverzüglich Folge.',
      'Formell / Verwaltung',
      'Nomen-Verb-Verbindung',
    ),
    item(
      'voc_in_anspruch_nehmen',
      'etw. in Anspruch nehmen',
      'Eine Leistung, Zeit oder Hilfe nutzen bzw. beanspruchen',
      'Die Prüfung des Gutachtens wird mindestens drei Wochen in Anspruch nehmen.',
      'Standard / Beruf',
      'Nomen-Verb-Verbindung',
      { stage: 1, intervalDays: 1, repetitions: 1 },
    ),
    item(
      'voc_unter_beweis_stellen',
      'etw. unter Beweis stellen',
      'Eine Fähigkeit oder Qualität praktisch belegen / demonstrieren',
      'In der Verhandlung stellte sie ihr diplomatisches Geschick eindrucksvoll unter Beweis.',
      'Gehoben / Karriere',
      'Nomen-Verb-Verbindung',
    ),
    item(
      'voc_anbetracht_tatsache',
      'in Anbetracht der Tatsache, dass',
      'Angesichts des Umstands, unter Berücksichtigung von',
      'In Anbetracht der Tatsache, dass die Frist morgen endet, handeln wir sofort.',
      'Gehoben / Konnektor',
      'Konnektor (Kausal)',
    ),
    item(
      'voc_nichtsdestotrotz',
      'nichtsdestotrotz / gleichwohl',
      'Trotzdem, dennoch (gehobener konzessiver Anschluss)',
      'Die Risiken sind erheblich; gleichwohl halten wir am Zeitplan fest.',
      'Gehoben / Schriftsprache',
      'Konnektor (Konzessiv)',
    ),
    item(
      'voc_vorausgesetzt_dass',
      'vorausgesetzt, dass / sofern',
      'Unter der Bedingung, dass (konditionale Einschränkung)',
      'Wir genehmigen das Budget, sofern ein monatliches Controlling erfolgt.',
      'Präzise / Vertragssprache',
      'Konnektor (Konditional)',
    ),
    item(
      'voc_geschweige_denn',
      'geschweige denn',
      'Und noch viel weniger (Steigerung einer Verneinung)',
      'Er hat die Präsentation nicht einmal gelesen, geschweige denn korrigiert.',
      'Idiomatisch / C1',
      'Konnektor (Restriktiv)',
    ),
    item(
      'voc_zumal',
      'zumal (+ Nebensatz)',
      'Besonders weil / erst recht da (verstärkende Begründung)',
      'Ein Aufschub wäre fatal, zumal die Konkurrenz bereits testet.',
      'Gehoben / Debatte',
      'Konnektor (Kausal)',
    ),
    item(
      'voc_augenwischerei',
      'reine Augenwischerei sein',
      'Eine bewusste Täuschung oder Schönfärberei ohne echte Substanz',
      'Die angebliche Mietsenkung entpuppte sich bei näherem Hinsehen als Augenwischerei.',
      'Idiomatisch / Kritik',
      'Redewendung & Idiom',
    ),
    item(
      'voc_auf_der_strecke_bleiben',
      'auf der Strecke bleiben',
      'Benachteiligt werden, scheitern oder nicht mithalten können',
      'Ohne gezielte Weiterbildung bleiben ältere Fachkräfte leicht auf der Strecke.',
      'Idiomatisch / Soziales',
      'Redewendung & Idiom',
    ),
    item(
      'voc_ueber_den_tellerrand',
      'über den Tellerrand hinausblicken',
      'Über das eigene Fachgebiet oder den gewohnten Horizont hinausdenken',
      'Interdisziplinäre Teams zwingen uns dazu, über den eigenen Tellerrand hinauszublicken.',
      'Gehobene Umgangssprache',
      'Redewendung & Idiom',
    ),
    item(
      'voc_stichhaltig_begruenden',
      'etw. stichhaltig begründen',
      'Mit wasserdichten, logisch unangreifbaren Argumenten belegen',
      'Können Sie den Mehraufwand gegenüber dem Aufsichtsrat stichhaltig begründen?',
      'Akademisch & Management',
      'Kollokation',
    ),
    item(
      'voc_modalpartikel_doch',
      'Das liegt doch auf der Hand!',
      "Modalpartikel 'doch': Appell an offensichtliches, geteiltes Vorwissen",
      'Wir können das Projekt doch nicht ohne Sicherheitsaudit freigeben!',
      'Pragmatisch / Mündlich',
      'Modalpartikel & Pragmatik',
    ),
    item(
      'voc_coach_spielintelligenz',
      'die Spielintelligenz fördern',
      'Kindern und Jugendlichen helfen, auf dem Platz selbstständig kluge Entscheidungen zu treffen',
      'Durch kleine Spielformen im Vier-gegen-Vier fördern wir die Spielintelligenz unserer E-Jugend.',
      'DFB-Ausbildungssprache',
      'Fußball-Trainer (E-/C-Jugend)',
    ),
    item(
      'voc_coach_umschaltspiel',
      'schnell umschalten / das Umschaltspiel',
      'Gedanklich und läuferisch sofort zwischen Ballbesitz und Ballverlust wechseln',
      'Nach Ballverlust müssen wir in der C-Jugend geschlossen umschalten und die Tiefe sichern.',
      'Taktik & Vereinsfußball',
      'Fußball-Trainer (E-/C-Jugend)',
    ),
    item(
      'voc_coach_halbraum',
      'die Halbräume schließen / besetzen',
      'Den strategischen Raum zwischen Zentrum und Außenbahn taktisch abdecken oder anspielen',
      'Wenn der Außenverteidiger einrückt, schließen wir den gefährlichen Halbraum.',
      'Taktik & Gruppenspiel',
      'Fußball-Trainer (E-/C-Jugend)',
    ),
    item(
      'voc_coach_auf_augenhoehe',
      'auf Augenhöhe coachen',
      'Spieler und Eltern respektvoll, konstruktiv und ohne Herablassung anleiten',
      'Gerade in der C-Jugend ist es entscheidend, Jugendliche auf Augenhöhe zu coachen und einzubinden.',
      'Pädagogik & Führung',
      'Fußball-Trainer (E-/C-Jugend)',
    ),
    item(
      'voc_coach_altersgerecht',
      'altersgerecht ausbilden',
      'Trainingsinhalte an den körperlichen und geistigen Entwicklungsstand (E- vs. C-Jugend) anpassen',
      'Statt sturer Konditionsläufe bilden wir unsere E-Jugend altersgerecht mit Ball und Spielformen aus.',
      'DFB-Rahmenkonzept',
      'Fußball-Trainer (E-/C-Jugend)',
    ),
    item(
      'voc_aufschluss_geben',
      'Aufschluss geben über (+ Akk.)',
      'Erkenntnisse liefern, etwas erklären oder aufklären',
      'Erst die Vergleichsmessung gibt Aufschluss über die Ursache des Temperaturanstiegs.',
      'Analytisch / Technik & Forschung',
      'Nomen-Verb-Verbindung',
    ),
    item(
      'voc_ausser_acht_lassen',
      'etw. außer Acht lassen',
      'Einen wichtigen Faktor übersehen oder bewusst nicht berücksichtigen',
      'Bei der Bewertung des Rückgangs dürfen wir das neue Terminsystem nicht außer Acht lassen.',
      'Gehoben / Argumentation',
      'Nomen-Verb-Verbindung',
    ),
    item(
      'voc_reibungsverluste',
      'Reibungsverluste vermeiden',
      'Konflikte, Missverständnisse oder unnötige Abstimmungshürden im Team gering halten',
      'Klare Übergaberegeln helfen uns, unnötige Reibungsverluste zwischen den Teams zu vermeiden.',
      'Führung & Projektarbeit',
      'Kollokation',
    ),
    item(
      'voc_spagat_schaffen',
      'den Spagat schaffen zwischen … und …',
      'Zwei scheinbar gegensätzliche Anforderungen gleichzeitig ausbalancieren',
      'In der U15 muss das Trainerteam den Spagat zwischen Fairplay-Rotation und taktischer Stabilität schaffen.',
      'Publizistisch / Vereinsführung',
      'Redewendung & Idiom',
    ),
    item(
      'voc_auf_den_pruefstand',
      'etw. auf den Prüfstand stellen',
      'Eine Regel oder Annahme kritisch und systematisch überprüfen',
      'Nach sechs Monaten sollten wir das Mietmodell anhand der tatsächlichen Fahrten erneut auf den Prüfstand stellen.',
      'Strategie & Evaluation',
      'Nomen-Verb-Verbindung',
    ),
    item(
      'voc_unter_vorbehalt',
      'etw. unter Vorbehalt zusagen',
      'Eine Zusage ausdrücklich an überprüfbare Bedingungen knüpfen',
      'Wir können die Lieferung nur unter dem Vorbehalt freigegebener FMEA-Nachweise zusagen.',
      'Vertrag & Technik',
      'Kollokation',
    ),
    item(
      'voc_vom_tisch_wischen',
      'Einwände nicht vom Tisch wischen',
      'Berechtigte Bedenken ernst nehmen, statt sie pauschal abzutun',
      'Wir sollten die kritischen Rückfragen aus der Belegschaft keinesfalls vorschnell vom Tisch wischen.',
      'Diskurs & Deeskalation',
      'Redewendung & Idiom',
    ),
  ];
}

export const diagnosticTasksFull: DiagnosticTask[] = [
  {
    id: 'task_listening',
    taskType: 'AUDIO_LISTENING',
    skillCategory: 'HOERVERSTEHEN',
    headlineDe: '1. Hörverstehen: reduziertes Übungsdeutsch',
    contextDe:
      'Synthetisch vorgelesenes Übungsbeispiel im Werkstatt-Umfeld; fiktive, vereinfachte regionale Schreibannäherung, keine authentische Dialektaufnahme.',
    promptText:
      'Hören Sie die Äußerung des Werkstattleiters an: Welche implizite Botschaft und welcher Tonfall liegen primär vor?',
    spokenAudioText:
      "Also pass amol uff, Kerle! Wenn mer die Toleranze bei dera Nockawell net grad jetzt akkurat eistelle, no fliegt uns d'ganze Serie beim Prüfstand um d'Ohre! Des hamma doch geschtern scho g'habt!",
    speechSpeed: 1.2,
    dialectNote:
      'Fiktive, vereinfachte südwestdeutsche Schreibannäherung mit Lautverschmelzungen; nicht regional validiert und keine Norm.',
    multipleChoiceOptions: [
      'Sachliche Bitte um Überprüfung von Berechnungen',
      'Ungeduldige Frustration über wiederholten Schlampigkeitsfehler & Furcht vor Ausschuss',
      'Lob für einen geglückten Belastungstest',
      'Freude über vorzeitigen Feierabend',
    ],
    correctOptionIndex: 1,
    analysisRationaleDe:
      "Die Auswahlaufgabe prüft, ob Reduktionen wie 'des hamma' und der emotional-vorwurfsvolle Subtext im gegebenen Übungstext erkannt werden. Sie misst kein allgemeines Hörniveau und keine regionale Hörkompetenz.",
    maxLatencyBenchmarkSeconds: 3,
    subTopicName: 'Reduktion & Subtext im Übungstext',
  },
  {
    id: 'task_speaking',
    taskType: 'RECORDED_SPEAKING',
    skillCategory: 'SPONTANES_SPRECHEN',
    headlineDe: '2. Formulierungen erkennen (kein Sprechtest)',
    contextDe: 'Unerwartete Frage in der Geschäftsführungssitzung.',
    promptText:
      "Ihr Vorstand fragt ungeduldig: 'Können Sie die Verzögerung bei der Auslieferung stichhaltig begründen?' Welche vorgegebene Entgegnung passt am besten?",
    spokenAudioText:
      'Können Sie die Verzögerung bei der Auslieferung stichhaltig begründen?',
    speechSpeed: 1.15,
    dialectNote: null,
    multipleChoiceOptions: [
      'Ähm, ja also, der Zulieferer war schuld und wir konnten da echt gar nichts machen.',
      'In Anbetracht der unvorhergesehenen Lieferengpässe mussten wir eine strategische Neupriorisierung vornehmen, um unsere Qualitätsstandards nicht zu gefährden.',
      'Ich habe eine Entscheidung gemacht und das ist so in Ordnung.',
      'Wir haben schließlich keine Lust mehr auf Überstunden gehabt.',
    ],
    correctOptionIndex: 1,
    analysisRationaleDe:
      'Die Auswahlaufgabe erkennt eine formelle, begründende Antwort mit passendem Register und einer etablierten Kollokation. Sie beobachtet kein eigenes Sprechen, keine Latenz und keine Eloquenz der lernenden Person.',
    maxLatencyBenchmarkSeconds: 3,
    subTopicName: 'Formelles Register & Begründung',
  },
  {
    id: 'task_interaction',
    taskType: 'INTERACTION_DIALOGUE',
    skillCategory: 'INTERAKTION',
    headlineDe: '3. Gesprächsreaktion erkennen',
    contextDe: 'Konfrontativer Einwand im Projektmeeting.',
    promptText:
      "Ein Kollege unterbricht Sie plötzlich: 'Das ist doch ein reines Scheinargument!' Wie parieren Sie den Einwand deeskalierend, aber bestimmt?",
    spokenAudioText: 'Moment mal! Das ist doch ein reines Scheinargument!',
    speechSpeed: 1.15,
    dialectNote: null,
    multipleChoiceOptions: [
      'Sie haben doch selbst keine Ahnung von den Zahlen!',
      'Lassen Sie uns diesen Punkt sachlich differenzieren: Es geht hierbei keineswegs um Scheinargumente, sondern um gesetzlich bindende Vorgaben.',
      'Ich rede jetzt, Sie müssen schweigen.',
      'Eventuell haben Sie recht, aber ich weiß auch nicht.',
    ],
    correctOptionIndex: 1,
    analysisRationaleDe:
      'Die Auswahlaufgabe erkennt eine deeskalierende Reaktion mit inhaltlicher Grenzziehung. Sie misst keine eigene Interaktionsleistung.',
    maxLatencyBenchmarkSeconds: 3,
    subTopicName: 'Unterbrechungen parieren',
  },
  {
    id: 'task_reading',
    taskType: 'READING_COMPREHENSION',
    skillCategory: 'LESEVERSTEHEN',
    headlineDe: '4. Leseverstehen (Implizite Bedeutung & Stilmittel)',
    contextDe: 'Auszug aus einem deutschen Leitartikel (Feuilleton).',
    promptText:
      '„Die vielbeschworene Digitalisierungsoffensive des Ministeriums entpuppte sich letztlich als Potemkinsches Dorf auf perforiertem Pergament.“ Was intendiert der Autor?',
    spokenAudioText: null,
    speechSpeed: 1.15,
    dialectNote: null,
    multipleChoiceOptions: [
      'Ein Lob auf die umweltfreundliche Papierverwendung des Ministeriums',
      'Scharfe satirische Kritik an einer substanzlosen Fassade ohne reale digitale Umsetzung',
      'Die Ankündigung eines historischen Museumsbaus',
      'Eine technische Dokumentation über Behördennetzwerke',
    ],
    correctOptionIndex: 1,
    analysisRationaleDe:
      "Decodierung von Metaphern ('Potemkinsches Dorf') und alliterierender Ironie ('perforiertes Pergament') auf fortgeschrittenem C1-Niveau.",
    maxLatencyBenchmarkSeconds: 3,
    subTopicName: 'Feuilleton-Ironie & Metaphern',
  },
  {
    id: 'task_writing',
    taskType: 'WRITING_PRODUCTION',
    skillCategory: 'SCHREIBEN',
    headlineDe: '5. Schreib-Präzision (Register & Textkohärenz)',
    contextDe: 'Formeller Schlichtungsbericht im geschäftlichen Streitfall.',
    promptText:
      'Welche Formulierung wahrt das formelle B2B-Schlichtungsregister am treffendsten?',
    spokenAudioText: null,
    speechSpeed: 1.15,
    dialectNote: null,
    multipleChoiceOptions: [
      'Der Streit zwischen den beiden Abteilungen war total nervig und muss aufhören.',
      'Zur Beilegung der Divergenzen empfiehlt die Schlichtungskommission eine verbindliche Modifikation der Zuständigkeitsordnung.',
      'Wir machen jetzt eine Vereinbarung weil das kaputt ist.',
      'Die Kollegen sollen sich mal nicht so anstellen.',
    ],
    correctOptionIndex: 1,
    analysisRationaleDe:
      'Die Auswahlaufgabe erkennt ein formelles Register und sachliche Wortwahl. Sie misst keine eigene Schreibproduktion.',
    maxLatencyBenchmarkSeconds: 3,
    subTopicName: 'Formelles B2B-Register',
  },
  {
    id: 'task_grammar',
    taskType: 'GRAMMAR_PRECISION',
    skillCategory: 'GRAMMATIK',
    headlineDe: '6. Grammatik-Kontrolle (Passiversatzformen)',
    contextDe: 'Technische Dokumentation & Vorschriften.',
    promptText:
      "Welcher Satz drückt die Aussage 'Diese Anforderung kann nicht erfüllt werden' mit einer korrekten C1-Passiversatzform aus?",
    spokenAudioText: null,
    speechSpeed: 1.15,
    dialectNote: null,
    multipleChoiceOptions: [
      'Diese Anforderung ist nicht zu erfüllen.',
      'Diese Anforderung wird nicht erfüllt können.',
      'Man kann diese Anforderung nicht machen.',
      'Diese Anforderung tut sich nicht erfüllen lassen.',
    ],
    correctOptionIndex: 0,
    analysisRationaleDe:
      "Beherrschung von Passiversatzformen: 'sein + zu + Infinitiv' mit modaler Bedeutung von Unmöglichkeit.",
    maxLatencyBenchmarkSeconds: 3,
    subTopicName: 'Passiversatzformen',
  },
  {
    id: 'task_vocabulary',
    taskType: 'VOCABULARY_COLLOCATION',
    skillCategory: 'WORTSCHATZ',
    headlineDe: '7. Wortschatz & Kollokationen (Nomen-Verb-Verbindungen)',
    contextDe: 'Verhandlungs- und Tagungssprache.',
    promptText:
      "Welche Nomen-Verb-Verbindung drückt 'ein heikles Thema ausdrücklich ansprechen' muttersprachlich präzise aus?",
    spokenAudioText: null,
    speechSpeed: 1.15,
    dialectNote: null,
    multipleChoiceOptions: [
      'ein Thema in die Sprache bringen',
      'ein Thema zur Sprache bringen',
      'ein Thema nach Reden tun',
      'ein Thema zum Diskutieren machen',
    ],
    correctOptionIndex: 1,
    analysisRationaleDe:
      "Feste Nomen-Verb-Verbindung: 'zur Sprache bringen' (Standard für professionelle Themeninitiierung).",
    maxLatencyBenchmarkSeconds: 3,
    subTopicName: 'Nomen-Verb-Verbindungen',
  },
  {
    id: 'task_mediation',
    taskType: 'MEDIATION_TRANSFER',
    skillCategory: 'MEDIATION',
    headlineDe: '8. Mediation & Sprachmittlung (Bürgernah erklären)',
    contextDe: 'Behördlicher Bescheid der Stadtverwaltung.',
    promptText:
      '„Die Inanspruchnahme der Sondernutzungserlaubnis setzt die vorherige Entrichtung der kommunalen Gebührenordnung voraus.“ Wie vermitteln Sie dies bürgernah an einen Marktstandbetreiber?',
    spokenAudioText: null,
    speechSpeed: 1.15,
    dialectNote: null,
    multipleChoiceOptions: [
      'Die Entrichtung der Ordnung muss von Ihnen in Anspruch genommen werden.',
      'Bevor Sie Ihren Stand auf dem Marktplatz aufbauen dürfen, muss die Standgebühr bei der Stadt bezahlt sein.',
      'Sie dürfen gar nichts machen ohne Perforation.',
      'Der Paragraph verbietet jede Marktaktivität grundsätzlich.',
    ],
    correctOptionIndex: 1,
    analysisRationaleDe:
      'Die Auswahlaufgabe erkennt eine handlungsorientierte Umschreibung von Verwaltungssprache. Sie misst keine eigene Mediation.',
    maxLatencyBenchmarkSeconds: 3,
    subTopicName: 'Bürgernahe Sprachmittlung',
  },
  {
    id: 'task_prosody_pragmatics',
    taskType: 'AUDIO_LISTENING',
    skillCategory: 'AUSSPRACHE_PROSODIE',
    headlineDe: '9. Prosodie & Partikeldeutung (Übungsbeispiel)',
    contextDe: 'Mündliche Rücksprache beim schwäbischen Vereinsabend (TTS-Übungssatz).',
    promptText:
      'Hören Sie die Einladung an: „Komm halt einfach mal am Samstag vorbei!“ Welche pragmatische Haltung drückt der unbetonte Einsatz von „halt“ und „mal“ typischerweise aus?',
    spokenAudioText: 'Komm halt einfach mal am Samstag vorbei!',
    speechSpeed: 1.08,
    dialectNote: 'Übungsbeispiel zur Partikelwirkung; Intonation und Beziehungskontext sind entscheidend.',
    multipleChoiceOptions: [
      'Eine juristisch verbindliche Vorladung mit Anwesenheitspflicht',
      'Eine lockere, niedrigschwellige Einladung, deren genaue Uhrzeit bei Bedarf noch freundlich geklärt werden sollte',
      'Eine versteckte Ausladung für neue Mitglieder',
      'Eine Verpflichtung, ein ganzes Wochenende zu arbeiten',
    ],
    correctOptionIndex: 1,
    analysisRationaleDe:
      'Die Auswahlaufgabe prüft das Erkennen abmildernder Modalpartikeln im Übungsbeispiel. Sie bewertet keine reale interkulturelle Interaktion.',
    maxLatencyBenchmarkSeconds: 3,
    subTopicName: 'Modalpartikeln & Verbindlichkeit',
  },
  {
    id: 'task_evidence_engineering',
    taskType: 'READING_COMPREHENSION',
    skillCategory: 'LESEVERSTEHEN',
    headlineDe: '10. Evidenzgrenzen in einem Technikbericht',
    contextDe: 'Interner EV-Prüfstandsbericht nach Software-Update.',
    promptText:
      '„Nach einem Software-Update wurde an Modul 4 eine erhöhte Temperatur gemessen; gleichzeitig wurde jedoch die Sensor-Kontaktpaste erneuert.“ Welche Aussage ist allein durch diesen Satz gedeckt?',
    spokenAudioText: null,
    speechSpeed: 1.1,
    dialectNote: null,
    multipleChoiceOptions: [
      'Das Software-Update ist zweifelsfrei die alleinige Fehlerursache.',
      'Beide Faktoren fielen zeitlich zusammen; die Ursache lässt sich ohne gezielten Vergleich noch nicht eindeutig zuordnen.',
      'Die Batterie ist irreparabel beschädigt.',
      'Messfehler sind bei neuen Sensoren grundsätzlich ausgeschlossen.',
    ],
    correctOptionIndex: 1,
    analysisRationaleDe:
      'Erkennt die saubere Unterscheidung zwischen zeitlicher Koinzidenz und kausalem Nachweis (C1-Textkritik).',
    maxLatencyBenchmarkSeconds: 3,
    subTopicName: 'Befund vs. Kausalbehauptung',
  },
  {
    id: 'task_coaching_tone',
    taskType: 'INTERACTION_DIALOGUE',
    skillCategory: 'INTERAKTION',
    headlineDe: '11. Deeskalation & Rollenschutz am Spielfeldrand',
    contextDe: 'Elterngespräch nach einem C-Jugend-Punktspiel.',
    promptText:
      'Ein aufgebrachter Vater fragt: „Warum spielt der Müller durch, obwohl mein Sohn viel stärker trainiert?“ Welche Antwort wahrt Rollenschutz und Sachlichkeit?',
    spokenAudioText: null,
    speechSpeed: 1.1,
    dialectNote: null,
    multipleChoiceOptions: [
      'Stimmt, der Müller war heute tatsächlich schwächer, das spreche ich sofort vor der Gruppe an.',
      'Über einzelne Mitspieler sprechen wir nicht; gern erläutere ich Ihnen jedoch nach dem Training vertraulich, welche sportlichen Kriterien für die Position Ihres Sohnes ausschlaggebend waren.',
      'Wenn es Ihnen nicht passt, wechseln Sie bitte den Verein.',
      'Ihr Sohn darf ab nächster Woche garantiert jede Partie durchspielen.',
    ],
    correctOptionIndex: 1,
    analysisRationaleDe:
      'Wahrt Vertraulichkeit über Dritte, vermeidet falsche Einsatzversprechen und bietet einen konkreten Gesprächsrahmen an.',
    maxLatencyBenchmarkSeconds: 3,
    subTopicName: 'Vertraulichkeit & Deeskalation',
  },
];

export const diagnosticTasksGrammarVocab: DiagnosticTask[] = [
  {
    id: 'gv_task_1',
    taskType: 'GRAMMAR_PRECISION',
    skillCategory: 'GRAMMATIK',
    subTopicName: 'Passiversatzformen (Modale Bedeutung)',
    headlineDe: '1. Passiversatzformen (Modale Notwendigkeit)',
    contextDe: 'Rechtliche & behördliche Vorgaben im Sicherheitsbericht.',
    promptText:
      "Welcher Satz drückt die Aussage 'Diese Sicherheitsrichtlinie kann keinesfalls umgangen werden' mit einer grammatisch einwandfreien C1-Passiversatzform aus?",
    spokenAudioText: null,
    speechSpeed: 1.15,
    dialectNote: null,
    multipleChoiceOptions: [
      'Diese Sicherheitsrichtlinie lässt sich keinesfalls umgehen.',
      'Diese Sicherheitsrichtlinie wird keinesfalls umgangen können.',
      'Man tut diese Sicherheitsrichtlinie nicht umgehen können.',
      'Diese Sicherheitsrichtlinie ist ohne Umgehen zu sein.',
    ],
    correctOptionIndex: 0,
    analysisRationaleDe:
      "C1-Passiversatz: 'sich lassen + Infinitiv' drückt die modale Möglichkeit bzw. Notwendigkeit ('kann umgangen werden') präzise und elegant aus.",
    maxLatencyBenchmarkSeconds: 3,
  },
  {
    id: 'gv_task_2',
    taskType: 'GRAMMAR_PRECISION',
    skillCategory: 'GRAMMATIK',
    subTopicName: 'Erweiterte Partizipialkonstruktionen',
    headlineDe: '2. Partizipialkonstruktionen (Nominalstil)',
    contextDe: 'Wissenschaftlicher Fachaufsatz der Wirtschaftsforschung.',
    promptText:
      "Welche Umformung des Relativsatzes 'die Gutachten, die von den unabhängigen Sachverständigen erst kürzlich vorgelegt worden sind' in ein erweitertes Partizipialattribut ist grammatisch fehlerfrei?",
    spokenAudioText: null,
    speechSpeed: 1.15,
    dialectNote: null,
    multipleChoiceOptions: [
      'die von den unabhängigen Sachverständigen erst kürzlich vorgelegten Gutachten',
      'die erst kürzlich vorgelegte von den unabhängigen Sachverständigen Gutachten',
      'die vorgelegt gewordenen Gutachten von Sachverständigen',
      'die von Sachverständigen vorzulegenden Gutachten vor Kurzem',
    ],
    correctOptionIndex: 0,
    analysisRationaleDe:
      "Erweitertes Partizip II als Linksattribut: Die Attribute müssen vollständig zwischen Artikel ('die') und Nomen ('Gutachten') eingebettet sein.",
    maxLatencyBenchmarkSeconds: 3,
  },
  {
    id: 'gv_task_3',
    taskType: 'GRAMMAR_PRECISION',
    skillCategory: 'GRAMMATIK',
    subTopicName: 'Konjunktiv I & Indirekte Rede',
    headlineDe: '3. Konjunktiv I (Journalistische Distanzierung)',
    contextDe: 'Pressemitteilung & Feuilleton-Berichterstattung.',
    promptText:
      "Welcher Satz gibt die Äußerung 'Wir haben die Zielvorgaben erfüllt und wir fordern neue Verhandlungen' korrekt im gehobenen Konjunktiv I der indirekten Rede wieder?",
    spokenAudioText: null,
    speechSpeed: 1.15,
    dialectNote: null,
    multipleChoiceOptions: [
      'Die Sprecherin betonte, sie hätten die Zielvorgaben erfüllt und fordern Verhandlungen.',
      'Die Sprecherin betonte, man habe die Zielvorgaben erfüllt und fordere neue Verhandlungen.',
      'Die Sprecherin betonte, man hat die Zielvorgaben erfüllt und würde fordern.',
      'Die Sprecherin betonte, sie haben die Zielvorgaben erfüllt und verlangen Verhandlungen.',
    ],
    correctOptionIndex: 1,
    analysisRationaleDe:
      "Im journalistischen C1-Standard verlangt die indirekte Rede Konjunktiv I: 'habe erfüllt' (3. P. Sg.) und 'fordere' (Distanzierung).",
    maxLatencyBenchmarkSeconds: 3,
  },
  {
    id: 'gv_task_4',
    taskType: 'GRAMMAR_PRECISION',
    skillCategory: 'GRAMMATIK',
    subTopicName: 'Gehobene Konnektoren & Satzgefüge',
    headlineDe: '4. Gehobene Subjunktionen (Konzessiver Diskurs)',
    contextDe: 'Wirtschaftspolitischer Leitartikel.',
    promptText:
      "Welche Subjunktion verbindet die beiden Aussagen auf authentischem C1-Niveau konzessiv: '[...] die Konjunkturindikatoren spürbar schwächeln, bleibt die Investitionsbereitschaft stabil'?",
    spokenAudioText: null,
    speechSpeed: 1.15,
    dialectNote: null,
    multipleChoiceOptions: [
      'Wenngleich',
      'Insofern als',
      'Trotz dass',
      'Weil doch',
    ],
    correctOptionIndex: 0,
    analysisRationaleDe:
      "'Wenngleich' leitet einen konzessiven Nebensatz im gehobenen Register ein. 'Trotz dass' gilt als stilistischer Fehler.",
    maxLatencyBenchmarkSeconds: 3,
  },
  {
    id: 'gv_task_5',
    taskType: 'GRAMMAR_PRECISION',
    skillCategory: 'GRAMMATIK',
    subTopicName: 'Feste Genitiv-Präpositionen',
    headlineDe: '5. Gehobene Präpositionen mit Genitiv',
    contextDe: 'Juristischer Schiedsspruch & B2B-Vertragsrecht.',
    promptText:
      "Welche Genitiv-Präposition komplettiert die Feststellung stilgerecht: 'Der Antrag wurde [...] gravierender formeller Mängel abgewiesen'?",
    spokenAudioText: null,
    speechSpeed: 1.15,
    dialectNote: null,
    multipleChoiceOptions: [
      'aufgrund',
      'wegen von',
      'dank mit',
      'anstelle über',
    ],
    correctOptionIndex: 0,
    analysisRationaleDe:
      "'aufgrund + Genitiv' nennt den Ablehnungsgrund: Es liegen gravierende formelle Mängel vor. 'Mangels' würde deren Fehlen ausdrücken und die beabsichtigte Bedeutung umkehren.",
    maxLatencyBenchmarkSeconds: 3,
  },
  {
    id: 'gv_task_6',
    taskType: 'VOCABULARY_COLLOCATION',
    skillCategory: 'WORTSCHATZ',
    subTopicName: 'Nomen-Verb-Verbindungen (Initiierung)',
    headlineDe: '6. Funktionsverbgefüge (Themeninitiierung)',
    contextDe: 'Moderation einer kontroversen Podiumsdiskussion.',
    promptText:
      "Welche Nomen-Verb-Verbindung drückt 'ein heikles Thema ausdrücklich vor allen ansprechen' muttersprachlich präzise aus?",
    spokenAudioText: null,
    speechSpeed: 1.15,
    dialectNote: null,
    multipleChoiceOptions: [
      'ein Thema zur Sprache bringen',
      'ein Thema in die Sprache setzen',
      'ein Thema nach Rede stellen',
      'ein Thema zum Sagen machen',
    ],
    correctOptionIndex: 0,
    analysisRationaleDe:
      "Feste Nomen-Verb-Verbindung: 'zur Sprache bringen' bedeutet das gezielte Ansprechen eines Gegenstands im formellen Rahmen.",
    maxLatencyBenchmarkSeconds: 3,
  },
  {
    id: 'gv_task_7',
    taskType: 'VOCABULARY_COLLOCATION',
    skillCategory: 'WORTSCHATZ',
    subTopicName: 'Nomen-Verb-Verbindungen (Strategische Prüfung)',
    headlineDe: '7. Funktionsverbgefüge (Entscheidungsfindung)',
    contextDe: 'Strategieklausur der Geschäftsleitung.',
    promptText:
      "Welches Funktionsverb vervollständigt den gehobenen Satz korrekt: 'Wir müssen eine Neuausrichtung unseres Portfolios ernsthaft in Erwägung [...]'?",
    spokenAudioText: null,
    speechSpeed: 1.15,
    dialectNote: null,
    multipleChoiceOptions: ['ziehen', 'nehmen', 'bringen', 'stellen'],
    correctOptionIndex: 0,
    analysisRationaleDe:
      "Das feste Funktionsverbgefüge lautet 'in Erwägung ziehen' (= prüfen / erwägen).",
    maxLatencyBenchmarkSeconds: 3,
  },
  {
    id: 'gv_task_8',
    taskType: 'VOCABULARY_COLLOCATION',
    skillCategory: 'WORTSCHATZ',
    subTopicName: 'Bildungssprachliche Synonymik',
    headlineDe: '8. Bildungssprachliche Präzision & Synonymik',
    contextDe: 'Wissenschaftlicher Methodendiskurs.',
    promptText:
      "Welches bildungssprachliche Adjektiv charakterisiert eine Argumentation am treffendsten als 'in sich schlüssig, nachvollziehbar und überzeugend fundiert'?",
    spokenAudioText: null,
    speechSpeed: 1.15,
    dialectNote: null,
    multipleChoiceOptions: ['plausibel', 'populär', 'arbiträr', 'dekadent'],
    correctOptionIndex: 0,
    analysisRationaleDe:
      "'plausibel' bezeichnet methodische Einleuchtendheit und Schlüssigkeit auf akademischem C1-Niveau.",
    maxLatencyBenchmarkSeconds: 3,
  },
  {
    id: 'gv_task_9',
    taskType: 'VOCABULARY_COLLOCATION',
    skillCategory: 'WORTSCHATZ',
    subTopicName: 'C1-Metaphorik & Idiome',
    headlineDe: '9. C1-Metaphorik & Idiomatische Kollokationen',
    contextDe: 'Finanz- und Budgetanalyse.',
    promptText:
      "Was bedeutet die Wendung: 'Die geringfügigen Wechselkursschwankungen fallen im Jahresabschluss kaum ins Gewicht'?",
    spokenAudioText: null,
    speechSpeed: 1.15,
    dialectNote: null,
    multipleChoiceOptions: [
      'Sie spielen für das Gesamtergebnis eine vernachlässigbare, unbedeutende Rolle.',
      'Sie sind viel zu schwer, um gewogen zu werden.',
      'Sie führen unmittelbar zu einer Bilanzkrise.',
      'Sie müssen im Lager auf die Waage gelegt werden.',
    ],
    correctOptionIndex: 0,
    analysisRationaleDe:
      "Die Redewendung 'ins Gewicht fallen' bedeutet 'von ausschlaggebender Relevanz sein'. 'Kaum ins Gewicht fallen' = vernachlässigbar.",
    maxLatencyBenchmarkSeconds: 3,
  },
  {
    id: 'gv_task_10',
    taskType: 'VOCABULARY_COLLOCATION',
    skillCategory: 'WORTSCHATZ',
    subTopicName: 'Registerkontrolle & Diplomatischer Ton',
    headlineDe: '10. Registerkontrolle & Diplomatischer B2B-Ton',
    contextDe: 'Schlichtungsverhandlung bei Vertragsstreitigkeiten.',
    promptText:
      'Welche Formulierung wahrt die diplomatische Sachlichkeit und das formelle Schlichtungsregister am treffendsten?',
    spokenAudioText: null,
    speechSpeed: 1.15,
    dialectNote: null,
    multipleChoiceOptions: [
      'Zur Beilegung der Divergenzen schlagen wir eine verbindliche Modifikation der Lieferfristen vor.',
      'Damit der nervige Streit aufhört, müssen wir jetzt die Lieferfristen anpassen.',
      'Wir haben absolut keine Lust auf eure Beschwerden und machen jetzt neue Fristen.',
      'Die Gegenseite soll mal eine Entscheidung machen.',
    ],
    correctOptionIndex: 0,
    analysisRationaleDe:
      "Klassisches C1-Register: 'Divergenzen beilegen' und 'Modifikation vorschlagen' wahren Sachlichkeit ohne unprofessionelle Emotionalität.",
    maxLatencyBenchmarkSeconds: 3,
  },
  {
    id: 'gv_task_11',
    taskType: 'GRAMMAR_PRECISION',
    skillCategory: 'GRAMMATIK',
    subTopicName: 'Konjunktiv II Vergangenheit (Passiv)',
    headlineDe: '11. Irrealer Vorwurf & Oberfeldstellung',
    contextDe: 'Nachbesprechung einer verpassten Kundenfrist.',
    promptText:
      'Welche Formulierung stellt den Nebensatz mit Modalverb im Konjunktiv II der Vergangenheit standardsprachlich korrekt?',
    spokenAudioText: null,
    speechSpeed: 1.15,
    dialectNote: null,
    multipleChoiceOptions: [
      'Der Bericht bemängelt, dass die Annahmenfolie früher hätte gezeigt werden müssen.',
      'Der Bericht bemängelt, dass die Annahmenfolie früher gezeigt werden müssen hätte.',
      'Der Bericht bemängelt, dass hätte die Annahmenfolie früher gezeigt geworden müssen.',
      'Der Bericht bemängelt, dass die Annahmenfolie früher gemusst hätte gezeigt werden.',
    ],
    correctOptionIndex: 0,
    analysisRationaleDe:
      'Bei drei Verbteilen im Nebensatz steht das konjugierte Hilfsverb („hätte“) unmittelbar vor dem Doppelinfinitiv („gezeigt werden müssen“).',
    maxLatencyBenchmarkSeconds: 3,
  },
  {
    id: 'gv_task_12',
    taskType: 'GRAMMAR_PRECISION',
    skillCategory: 'GRAMMATIK',
    subTopicName: 'Präpositionaladverbien im Relativanschluss',
    contextDe: 'Technisches Protokoll & FMEA-Review.',
    headlineDe: '12. Präpositionalanschluss (worauf vs. darauf)',
    promptText:
      'Welcher Satzanschluss verweist grammatisch präzise auf den gesamten vorherigen Sachverhalt?',
    spokenAudioText: null,
    speechSpeed: 1.15,
    dialectNote: null,
    multipleChoiceOptions: [
      'Der Durchflusssensor wurde erst nach Lauf 2 kalibriert, worauf das Team die Vergleichsmessung wiederholte.',
      'Der Durchflusssensor wurde erst nach Lauf 2 kalibriert, auf was das Team die Vergleichsmessung wiederholte.',
      'Der Durchflusssensor wurde erst nach Lauf 2 kalibriert, darauf das Team die Vergleichsmessung wiederholte.',
      'Der Durchflusssensor wurde erst nach Lauf 2 kalibriert, über was das Team die Messung wiederholte.',
    ],
    correctOptionIndex: 0,
    analysisRationaleDe:
      'Satzbezogene Relativanschlüsse verlangen im gehobenen Deutsch „wo(r) + Präposition“ mit Verbendstellung („worauf … wiederholte“).',
    maxLatencyBenchmarkSeconds: 3,
  },
  {
    id: 'gv_task_13',
    taskType: 'VOCABULARY_COLLOCATION',
    skillCategory: 'WORTSCHATZ',
    subTopicName: 'Funktionsverbgefüge (Untersuchung & Erkenntnis)',
    headlineDe: '13. Funktionsverbgefüge (Erkenntnisgewinn)',
    contextDe: 'Wissenschaftliche oder technische Auswertung.',
    promptText:
      'Welche Verbindung drückt „klare Erkenntnisse über eine Ursache liefern“ idiomatisch korrekt aus?',
    spokenAudioText: null,
    speechSpeed: 1.15,
    dialectNote: null,
    multipleChoiceOptions: [
      'Aufschluss über die Ursache geben',
      'Aufschluss über die Ursache machen',
      'Einsicht über die Ursache tun',
      'Erklärung auf die Ursache tragen',
    ],
    correctOptionIndex: 0,
    analysisRationaleDe:
      'Die feste Verbindung lautet „Aufschluss über (+ Akkusativ) geben“ (= etwas aufklären / erkennen lassen).',
    maxLatencyBenchmarkSeconds: 3,
  },
  {
    id: 'gv_task_14',
    taskType: 'VOCABULARY_COLLOCATION',
    skillCategory: 'WORTSCHATZ',
    subTopicName: 'Modalpartikeln & Evidenzton',
    headlineDe: '14. Modalpartikeln („dürfte“ vs. „zweifellos“)',
    contextDe: 'Abwägende Projektkommunikation ohne Übertreibung.',
    promptText:
      'Welcher Satz formuliert eine begründete Vermutung bei noch unvollständigen Daten am präzisesten?',
    spokenAudioText: null,
    speechSpeed: 1.15,
    dialectNote: null,
    multipleChoiceOptions: [
      'Der Zeitversatz dürfte auf die neue Diagnosebotschaft zurückzuführen sein; eine Gegenmessung steht jedoch noch aus.',
      'Der Zeitversatz ist hundertprozentig sicher und wohl vielleicht durch die Botschaft verursacht.',
      'Der Zeitversatz kann überhaupt gar nicht anders entstanden sein.',
      'Eventuell ist das sicher die Ursache.',
    ],
    correctOptionIndex: 0,
    analysisRationaleDe:
      '„dürfte + Infinitiv“ drückt eine plausible, noch zu prüfende Annahme aus und widerspricht keinem nachgestellten Prüfvorbehalt.',
    maxLatencyBenchmarkSeconds: 3,
  },
];

export const footballCoachU15Data: {
  terms: FootballCoachTerm[];
  scenarios: TacticalScenario[];
  drills: DrillCommunicationPattern[];
} = {
  terms: [
    {
      id: 'fc_halbraum',
      term: 'den Halbraum besetzen',
      category: 'OFFENSIV',
      tacticalMeaning:
        'Den vertikalen Korridor zwischen Zentrum und Außenbahn nutzen, um Diagonalpässe zwischen die gegnerischen Ketten zu ermöglichen.',
      pitchCommand:
        'Achter, schieb in den linken Halbraum — zwischen Innen- und Außenverteidiger!',
      avoidInstead: 'Geh mal ein bisschen mehr nach halb-links!',
    },
    {
      id: 'fc_abkippen',
      term: 'abkippen (der Sechser)',
      category: 'OFFENSIV',
      tacticalMeaning:
        'Der defensive Mittelfeldspieler lässt sich im Spielaufbau zwischen oder neben die Innenverteidiger fallen, um Überzahl gegen das gegnerische Pressing zu schaffen.',
      pitchCommand:
        'Jonas, kipp zwischen die Innenverteidiger ab, dann haben wir drei gegen zwei im Aufbau!',
      avoidInstead: 'Lauf mal ganz nach hinten zum Torwart!',
    },
    {
      id: 'fc_schnittstelle',
      term: 'der Schnittstellenpass',
      category: 'OFFENSIV',
      tacticalMeaning:
        'Ein präziser Pass genau in die Lücke zwischen zwei gegnerischen Abwehrspielern, idealerweise in den Lauf des startenden Stürmers.',
      pitchCommand:
        'Blick hoch, Timing abwarten und den Ball scharf in die Schnittstelle spielen!',
      avoidInstead: 'Schieß einfach irgendwie durch die Mitte!',
    },
    {
      id: 'fc_spielverlagerung',
      term: 'die Spielverlagerung',
      category: 'OFFENSIV',
      tacticalMeaning:
        'Schneller Seitenwechsel über zwei bis drei Stationen auf die ballferne Seite, wenn der Gegner das Zentrum oder eine Flanke komplett zugestellt hat.',
      pitchCommand:
        'Hier ist es eng — mit zwei Kontakten über die Sechs auf die andere Seite verlagern!',
      avoidInstead: 'Spiel den Ball ganz weit rüber!',
    },
    {
      id: 'fc_durchsichern',
      term: 'herausrücken & durchsichern',
      category: 'DEFENSIV',
      tacticalMeaning:
        'Sticht ein Verteidiger auf den ballführenden Gegner vor, rücken die übrigen Spieler der Viererkette sofort diagonal ein, um den Raum dahinter zu schließen.',
      pitchCommand:
        'Paul rückt raus — Kette schiebt sofort ballnah durch und sichert die Tiefe!',
      avoidInstead: 'Bleibt alle hinten stehen und passt auf!',
    },
    {
      id: 'fc_kompaktheit',
      term: 'die Kompaktheit im Verbund',
      category: 'DEFENSIV',
      tacticalMeaning:
        'Abstände zwischen Abwehr-, Mittelfeld- und Angriffslinie eng halten (ca. 25–30 Meter), sodass keine bespielbaren Zwischenräume entstehen.',
      pitchCommand:
        'Abstände verkürzen! Stürmer fallen mit zurück, wir halten den Block kompakt!',
      avoidInstead: 'Alle nach hinten rennen!',
    },
    {
      id: 'fc_gegenpressing',
      term: 'das Gegenpressing (3-Sekunden-Regel)',
      category: 'UMSCHALTEN',
      tacticalMeaning:
        'Unmittelbar nach eigenem Ballverlust greifen die ballnahen Spieler den Ballführenden an, um den Gegner zum Fehlpass zu zwingen, bevor er kontern kann.',
      pitchCommand:
        'Ball weg — sofort zu zweit anlaufen, Passwege zustellen, nicht fallen lassen!',
      avoidInstead: 'Menno, schon wieder verloren — zurück!',
    },
    {
      id: 'fc_tiefe_sichern',
      term: 'die Tiefe sichern (Restverteidigung)',
      category: 'UMSCHALTEN',
      tacticalMeaning:
        'Bei eigenem Angriff bleiben zwei Verteidiger und ein Sechser so gestaffelt, dass gegnerische lange Konterbälle sofort abgefangen werden.',
      pitchCommand:
        'Restverteidigung wach bleiben: Zwei Mann sichern hinter dem Ball die Tiefe!',
      avoidInstead: 'Nicht alle nach vorne laufen!',
    },
    {
      id: 'fc_vororientierung',
      term: 'der Schulterblick (Vororientierung)',
      category: 'KOMMUNIKATION',
      tacticalMeaning:
        'Noch bevor der Ball ankommt, schaut der Spieler kurz über beide Schultern, um Gegenspieler und freie Räume wahrzunehmen.',
      pitchCommand:
        'Schulterblick vor dem ersten Kontakt — weißt du schon, wo der freie Mann steht?',
      avoidInstead: 'Pass auf, hinter dir ist einer!',
    },
    {
      id: 'fc_positiv_coachen',
      term: 'positives Handlungsziel setzen',
      category: 'KOMMUNIKATION',
      tacticalMeaning:
        "Im Jugendfußball lösen positive Zielbilder ('Kopf hoch, Freilaufweg suchen') klare Aktionen aus, während Verbote ('Nicht bolzen') Spieler hemmen.",
      pitchCommand:
        'Mutig aufdrehen, ersten Kontakt nach vorne mitnehmen und Tiefe anbieten!',
      avoidInstead: 'Mach nicht schon wieder so einen hektischen Fehlpass!',
    },
    {
      id: 'fc_deckungsschatten',
      term: 'im Deckungsschatten anlaufen',
      category: 'DEFENSIV',
      tacticalMeaning:
        'Der anlaufende Spieler wählt seinen Laufbogen so, dass er mit dem eigenen Körper den direkten Passweg ins Zentrum hinter sich abdeckt.',
      pitchCommand:
        'Laufbogen bogenförmig zum Sechser wählen — nimm den Passweg in deinen Deckungsschatten!',
      avoidInstead: 'Renn einfach geradewegs auf den Torwart zu!',
    },
    {
      id: 'fc_dritter_mann',
      term: 'klatschen lassen (Dritter Mann)',
      category: 'OFFENSIV',
      tacticalMeaning:
        'Ein Spieler unter Druck legt den Ball direkt auf einen zugewandten Mitspieler ab, der sofort in den freien Raum auf einen dritten startenden Spieler weiterleitet.',
      pitchCommand:
        'Klatschen lassen auf die Sechs und sofort tief auf den einlaufenden Achter durchstecken!',
      avoidInstead: 'Dreh dich nicht um, pass einfach irgendwie zurück!',
    },
    {
      id: 'fc_tempo_aufnehmen',
      term: 'das 1-gegen-1 stellen & verzögern',
      category: 'DEFENSIV',
      tacticalMeaning:
        'Der Verteidiger stürzt nicht blind in die Grätsche, sondern verlangsamt das Dribbling in leicht seitlicher Stellung und lenkt den Angreifer zur Auslinie.',
      pitchCommand:
        'Tempo rausnehmen, seitlich stellen und den Angreifer konsequent auf seinen schwachen Fuß lenken!',
      avoidInstead: 'Hau ihn endlich um oder grätsch rein!',
    },
    {
      id: 'fc_coaching_pause',
      term: 'Spielform einfrieren (Coaching-Impuls)',
      category: 'KOMMUNIKATION',
      tacticalMeaning:
        'Statt im laufenden Spiel pausenlos dazwischenzurufen, hält der Trainer eine kurze Szene für 20 Sekunden an und stellt den Spielern eine konkrete Wahrnehmungsfrage.',
      pitchCommand:
        'Stopp, kurz einfrieren! Schau dich um: Wo war gerade unser freier Mann auf der ballfernen Seite?',
      avoidInstead: 'Warum sieht das denn wieder niemand?! Ihr hört mir nie zu!',
    },
  ],
  scenarios: [
    {
      id: 'scen_1',
      title: 'Hohes gegnerisches Pressing im Spielaufbau',
      matchPhase: '1. Halbzeit · Aufbauzone (C-Jugend 11v11)',
      situationDescription:
        'Der Gegner läuft Ihre beiden Innenverteidiger mit zwei Stürmern und einem Zehner hoch an. Ihr Torwart hat den Ball am Fuß, die Außenverteidiger sind zugestellt.',
      options: [
        'Dem Torwart zurufen, den Ball ab sofort immer unkontrolliert hoch ins Aus zu schlagen.',
        'Den Sechser zwischen die Innenverteidiger abkippen lassen, um eine Dreierkette zu bilden und die Außenverteidiger hochzuschieben.',
        'Beide Innenverteidiger sollen noch enger zum Torwart rücken.',
        'Den Mittelstürmer zurück in den eigenen Strafraum beordern.',
      ],
      correctIndex: 1,
      coachingExplanation:
        'Durch das Abkippen des Sechsers entsteht im Aufbau eine 3-gegen-2-Überzahl. Gleichzeitig ziehen die hochgeschobenen Außenverteidiger die gegnerischen Flügelspieler auseinander.',
      keyCommandToShout:
        'Sechs kippt ab! Außenverteidiger hoch und breit — Überzahl im Aufbau ausspielen!',
    },
    {
      id: 'scen_2',
      title: 'Ballverlust im gegnerischen Halbraum',
      matchPhase: 'Umschaltmoment Offensive -> Defensive',
      situationDescription:
        'Ihr Zehner verliert den Ball am gegnerischen Strafraum. Drei Ihrer Offensivspieler stehen in unmittelbarer Ballnähe, während der Gegner zum Konter ansetzen will.',
      options: [
        'Alle drei Offensivspieler sollen sofort bis an die eigene Eckfahne zurücksprinten.',
        'Sofortiges ballnahes Gegenpressing für 3–4 Sekunden auslösen, während die Restverteidigung das Zentrum schließt.',
        'Ein taktisches Foul an der Mittellinie in Kauf nehmen.',
        'Die Viererkette bis an die gegnerische Strafraumgrenze vorziehen.',
      ],
      correctIndex: 1,
      coachingExplanation:
        'Wenn drei Spieler ballnah stehen, ist das Zeitfenster direkt nach Ballverlust optimal für Gegenpressing — zumindest um den schnellen Pass nach vorne zu unterbinden.',
      keyCommandToShout:
        'Sofort drauf, Passweg nach innen zu! Restverteidigung sichert die Tiefe!',
    },
    {
      id: 'scen_3',
      title: 'Durchbruch über den gegnerischen Flügel',
      matchPhase: 'Abwehrverhalten im Verbund (Viererkette)',
      situationDescription:
        'Der gegnerische Rechtsaußen dribbelt an der Seitenlinie auf Ihren Linksverteidiger zu. Im Zentrum lauert ein Stürmer am Elfmeterpunkt.',
      options: [
        'Der Linksverteidiger grätscht sofort mit vollem Risiko in den Zweikampf.',
        'Der Linksverteidiger stellt den Gegner, verzögert das Tempo und lenkt ihn nach außen, während die Kette diagonal in den Strafraum einrückt.',
        'Beide Innenverteidiger laufen gemeinsam zur Seitenlinie, um zu doppeln.',
        'Der Torwart verlässt den Strafraum bis zur Seitenlinie.',
      ],
      correctIndex: 1,
      coachingExplanation:
        "In der U15-Viererkette gilt: 'Stellen statt blind grätschen'. Verzögern gibt dem ballnahen Sechser und der Kette Zeit, den Strafraum sauber zu besetzen.",
      keyCommandToShout:
        'Stellen und nach außen lenken! Kette schiebt durch und besetzt den Rückraum!',
    },
    {
      id: 'scen_4',
      title: 'Halbzeitansprache nach 0:2-Rückstand durch Konter',
      matchPhase: 'Halbzeitpause · Kabinen-Coaching (U15)',
      situationDescription:
        'Ihre C-Jugend hat 65% Ballbesitz, liegt aber durch zwei lange Konter 0:2 hinten. Die Spieler wirken frustriert und suchen die Schuld beim Schiedsrichter.',
      options: [
        'Die Mannschaft 10 Minuten lang lautstark kritisieren, um Kampfgeist zu erzwingen.',
        'Das starke eigene Ballbesitzspiel anerkennen, ruhig auf die fehlende Restverteidigung hinweisen und zwei klare Anpassungen für Halbzeit 2 vorgeben.',
        'Sofort alle fünf Auswechselspieler ohne taktischen Hinweis einwechseln.',
        'Die zweite Halbzeit komplett auf lange Bälle umstellen.',
      ],
      correctIndex: 1,
      coachingExplanation:
        '14- bis 15-Jährige brauchen in der Pause emotionale Stabilisierung plus konkrete, umsetzbare Lösungen (z. B. Absicherung durch einen Sechser) statt Pauschalvorwürfe.',
      keyCommandToShout:
        'Kopf hoch, unser Aufbau war stark! In Halbzeit zwei sichert ein Sechser konsequent vor der Kette ab — dann drehen wir das.',
    },
    {
      id: 'scen_5',
      title: 'Eckball-Verteidigung gegen körperlich überlegenen Gegner',
      matchPhase: 'Standardsituation Defensive · U15',
      situationDescription:
        'Der Gegner bringt drei großgewachsene Kopfballspieler in Ihren Fünfmeterraum. In den ersten beiden Ecken kam zweimal ein frei einlaufender Spieler am kurzen Pfosten zum Abschluss.',
      options: [
        'Den gesamten Elfer komplett leer lassen und alle zehn Feldspieler auf der Torlinie aufstellen.',
        'Kurzen Pfosten und zentralen Fünfmeterraum im Raum fest besetzen, dazu drei manndeckende Zuordnungen für die einlaufenden Kopfballspieler und einen Spieler im Rückraum für den Konter.',
        'Den Torwart anweisen, niemals die Torlinie zu verlassen.',
        'Dem Schiedsrichter vorab mitteilen, jeder Körperkontakt sei ein Foul.',
      ],
      correctIndex: 1,
      coachingExplanation:
        'Eine gemischte Raum- und Manndeckung schützt die gefährlichste Zone am kurzen Pfosten verlässlich und verhindert, dass reine Blockbewegungen die Zuordnung sprengen.',
      keyCommandToShout:
        'Zone am kurzen Pfosten besetzen! Rückraum wach bleiben — zweiter Ball gehört uns!',
    },
    {
      id: 'scen_6',
      title: 'U13-Torwart nach zwei Aufbaufehlern stabilisieren',
      matchPhase: 'Spielfeldrand · Mentales Jugend-Coaching',
      situationDescription:
        'Ihr zwölfjähriger Torwart hat zweimal den kurzen Flachpass zum Sechser zu kurz gespielt, woraus ein Gegentor fiel. Beim nächsten Abstoß wirkt er verunsichert und will blind weit abschlagen.',
      options: [
        'Ihn sofort vor der Mannschaft als Hauptschuldigen auswechseln.',
        'Ihm ruhig Mut zusprechen, das kurzfristige Risiko durch breitere Innenverteidiger reduzieren und mit einer klaren Anschlussoption Vertrauen schenken.',
        'Ihm laut zurufen, er dürfe ab jetzt nie wieder kurz spielen.',
        'Den Fehler ignorieren und das gesamte Spiel schweigen.',
      ],
      correctIndex: 1,
      coachingExplanation:
        'Fehler im mutigen Spielaufbau gehören zur Ausbildung. Strukturhilfe (Innenverteidiger breiter stellen) plus ruhige Bestärkung verhindert Blockaden.',
      keyCommandToShout:
        'Mutig bleiben, Tom! Innenverteidiger ganz breit an die Strafraumecke — du hast zwei saubere Optionen!',
    },
  ],
  drills: [
    {
      id: 'drill_1',
      drillTitle: '4+1 gegen 2 Rondo mit Anschlussaktion',
      ageGroupFocus: 'C-Jugend (U14/U15) · Vororientierung & erster Kontakt',
      setupSummary:
        '10x10m Feld. 4 Außenspieler + 1 Zentrumsspieler gegen 2 Jäger. Nach 6 Kontakten erfolgt ein scharfer Flugball oder Schnittstellenpass auf ein kleines Zieltor.',
      liveCommands: [
        'Vor dem Kontakt über die Schulter schauen — wo ist die offene Gasse?',
        'Körper offen stellen, damit du beide Seiten siehst!',
        'Scharf auf den vorderen Fuß passen, kein Hoppelball!',
        'Nach dem Pass sofort neuen Winkel anbieten!',
      ],
      avoidPhrase: 'Nicht immer nur quer spielen! Schnell, schnell!',
      positiveCorrection:
        'Such nach zwei Kontakten den Blick in die Tiefe — sobald die Gasse aufgeht, spielst du durch!',
    },
    {
      id: 'drill_2',
      drillTitle: 'Viererkette vs. 5 Angreifer: Verschieben im Verbund',
      ageGroupFocus: 'C-Jugend (U15) · Kettenverhalten & Durchsichern',
      setupSummary:
        'Halbfeld-Spielform. 4 Verteidiger + Torwart gegen 5 Angreifer. Das Trainerteam coacht ausschließlich Abstände, Herausstechen und Tiefe-Absichern.',
      liveCommands: [
        'Ball fliegt — jetzt schieben alle vier gemeinsam auf die Ballseite!',
        'Ballnaher Innenverteidiger rückt raus, Rest bildet das Dreieck dahinter!',
        "Kommuniziert miteinander: 'Ich hab Ball — du sicherst!'",
        'Abstand zwischen den Ketten maximal zehn Meter!',
      ],
      avoidPhrase: 'Du stehst völlig falsch! Bleib doch bei deinem Mann!',
      positiveCorrection:
        'Orientiere dich am Ball und an deinem Nebenmann: Wenn er rausgeht, rückst du zwei Meter nach innen ein!',
    },
    {
      id: 'drill_3',
      drillTitle: 'Umschaltspiel 6-gegen-6 auf vier Minitore',
      ageGroupFocus: 'C-Jugend (U14/U15) · Gegenpressing & Konterabsicherung',
      setupSummary:
        '35x30m Spielfeld mit zwei Minitoren je Grundlinie. Nach Balleroberung zählt ein Treffer innerhalb von 6 Sekunden doppelt.',
      liveCommands: [
        'Ballgewinn — erster Blick sofort nach vorne in die Tiefe!',
        'Ballverlust — drei Sekunden Vollgas ins Gegenpressing!',
        'Macht das Feld im Ballbesitz breit und tief!',
        'Zentrum dicht machen, lenkt sie auf die Außenbahn!',
      ],
      avoidPhrase: 'Rennt nicht so faul zurück! Was soll das?',
      positiveCorrection:
        'Die ersten drei Schritte nach Ballverlust entscheiden — sofort Druck auf den Ballführenden, dann erobern wir ihn zurück!',
    },
    {
      id: 'drill_4',
      drillTitle: 'Flügelspiel & Boxbesetzung mit Timings',
      ageGroupFocus: 'C-Jugend (U15) · Strafraumbesetzung & Rückraum',
      setupSummary:
        'Flügelspieler hinterläuft den Außenverteidiger und flankt flach. Drei Angreifer besetzen gestaffelt: erster Pfosten, zweiter Pfosten und Rückraum (Elfmeterpunkt).',
      liveCommands: [
        'Timing beim Einlaufen: Nicht zu früh in die Box starten!',
        'Erster Pfosten, zweiter Pfosten und einer bleibt im Rückraum!',
        'Flanke scharf und flach zwischen Torwart und Abwehr!',
        'Nachsetzen auf den Abpraller — jeder Ball ist heiß!',
      ],
      avoidPhrase: 'Warum steht ihr alle auf einem Haufen vor dem Tor?!',
      positiveCorrection:
        'Staffelt eure Laufwege: Einer zieht zum kurzen Pfosten, einer überläuft hinten und der Achter wartet am Elfmeterpunkt!',
    },
    {
      id: 'drill_5',
      drillTitle: 'Spielform 4-gegen-4 + 3 neutrale Spieler: Dritter Mann',
      ageGroupFocus: 'Übergang E-/D-/C-Jugend (U12–U15) · Passwinkel & Klatschenlassen',
      setupSummary:
        '25x20m Feld mit zwei Zielspielern an den Grundlinien und einem zentralen neutralen Spieler. Punktgewinn bei Verbindung von Grundlinie zu Grundlinie über den dritten Mann.',
      liveCommands: [
        'Vor dem Pass schon die nächste Station im Kopf haben!',
        'Klatschen lassen — und der Dritte startet sofort in den freien Halbraum!',
        'Nach dem Zuspiel nicht stehen bleiben, neuen Dreieckswinkel bilden!',
        'Sauber auf den hinteren Fuß spielen, damit er aufdrehen kann!',
      ],
      avoidPhrase: 'Warum drehst du dich wieder in die Gegner rein?!',
      positiveCorrection:
        'Wenn Druck im Rücken kommt, spiel direkt klatschen auf den Neutralen und starte durch die nächste Lücke!',
    },
    {
      id: 'drill_6',
      drillTitle: '1-gegen-1 Defensiv-Duell: Stellen & nach außen lenken',
      ageGroupFocus: 'C-Jugend (U14/U15) · Individualtaktik Defensive',
      setupSummary:
        '18x14m Kanal mit einem dribbelnden Angreifer gegen einen Verteidiger. Wertungspunkte für das Lenken über die äußeren Hütchen statt bloßes Foulen.',
      liveCommands: [
        'Schnell heranlaufen, aber drei Meter vor dem Gegner Tempo drosseln!',
        'Leicht seitliche Körperstellung — Zentrum zumachen, Außenbahn anbieten!',
        'Füße aktiv halten, auf die Hüfte und den Ballkontakt des Gegners achten!',
        'Erst zustoßen, wenn der Ball zu weit vorgelegt wird!',
      ],
      avoidPhrase: 'Fall doch nicht auf jede Finte rein! Grätsch halt!',
      positiveCorrection:
        'Verzögere mit kleinen schnellen Schritten, bis der Angreifer den Kopf senkt — dann greifst du sauber zu!',
    },
  ],
};

export const CONTEXT_LABELS: Record<
  'GENERAL' | 'SOCIAL' | 'ENGINEERING' | 'COACHING',
  string
> = {
  GENERAL: 'Allgemein',
  SOCIAL: 'Alltag & Soziales',
  ENGINEERING: 'Technik (EV/Hybrid)',
  COACHING: 'Jugendfußball',
};
