import type {
  DailyTrainingSession,
  ErrorIntelligenceItem,
  TrainingPriorityRecommendation,
} from '../types';

export function generateDailyTrainingSession(
  rotationIndex: number,
  topPriority: TrainingPriorityRecommendation,
  errors: ErrorIntelligenceItem[],
): DailyTrainingSession {
  const unresolved = errors.filter((e) => e.masteryStage !== 'GEMEISTERT');
  const pool = unresolved.length > 0 ? unresolved : errors;
  const errorsForCorrection = pool.slice(0, 2);
  const idx = ((rotationIndex % 6) + 6) % 6;
  const now = Date.now();

  switch (idx) {
    case 0:
      return {
        id: `session_${now}`,
        title: `Tagestraining: ${topPriority.reasonHeadline}`,
        targetCategory: topPriority.category,
        totalMinutes: 18,
        environment: topPriority.targetEnvironment,
        warmUpPrompt: {
          audioSpeakerText:
            'Da bin ich ganz anderer Meinung. Wir müssen das Vorhaben unverzüglich stoppen, sonst laufen uns die Kosten davon!',
          accentInfo:
            'Schnelles Sprechtempo (1.15x), Berlin/Brandenburg, emotionale Dringlichkeit',
          questionPrompt:
            'Welche unausgesprochene Haltung signalisiert der Sprecher primär?',
          options: [
            'Konstruktive Verhandlungsbereitschaft mit Budgetvorbehalt',
            'Kategorische Ablehnung und Druckaufbau durch Dramatisierung',
            'Zustimmung unter der Bedingung personeller Aufstockung',
            'Unsicherheit bezüglich der genauen Zahlen',
          ],
          correctIndex: 1,
          diagnosticExplanation:
            "Inferenz-Diagnostik: Die Schärfe von 'unverzüglich stoppen' und 'sonst laufen uns die Kosten davon' demonstriert kompromisslose Blockadehaltung, nicht konstruktive Vorbehalte.",
        },
        mainChallenge: {
          title: 'Stresstest: Spontane Gegendarstellung im Führungskreis',
          scenarioContext:
            'Im Budget-Meeting unterbricht Sie Ihr Kollege Herr Brandt und stellt die Wirtschaftlichkeit Ihres Konzepts öffentlich in Abrede.',
          partnerStatement:
            'Moment mal, das klingt ja auf dem Papier ganz nett, aber seien wir doch ehrlich: Bei den aktuellen Rohstoffpreisen ist Ihr Entwurf schlichtweg utopisch. Da verbrennen wir nur Geld!',
          stage1Prompt:
            "Stufe 1 (Souveränes Auffangen): Weisen Sie den Vorwurf 'utopisch' diplomatisch, aber bestimmt zurück, indem Sie die Risikoberechnung erwähnen.",
          stage2Deepening:
            "Stufe 2 (Konter unter Druck): Nutzen Sie eine Nomen-Verb-Verbindung ('zur Sprache bringen' / 'in Erwägung ziehen'), um das Gespräch wieder auf die Fakten zu lenken.",
          stage3StressInference:
            'Stufe 3 (Absicherung): Fordern Sie Herrn Brandt auf, eine konkrete Gegenrechnung vorzulegen, ohne aggressiv zu wirken.',
          keyPointsExpected: [
            'Kein Rechtfertigungston, sondern strategische Deeskalation',
            "Verwendung von gehobenen Konnektoren ('Ganz im Gegenteil', 'In Anbetracht der Tatsache')",
            'Reaktionslatenz unter 3 Sekunden',
          ],
        },
        productionTask: {
          isSpeaking: true,
          situationBrief:
            'Reagieren Sie jetzt direkt per Spracheingabe oder formulierter Antwort auf den Einwand von Herrn Brandt.',
          timeLimitSeconds: 90,
          keyEvaluationCriteria: [
            'Flüssigkeit & Sprechrhythmus (unter 2s Latenz)',
            'Angemessenes Führungsregister (weder defensiv noch beleidigt)',
            'Kohärente Argumentationskette',
          ],
          simulatedAudioPrompt:
            'Und was sagen Sie dazu? Können Sie das überhaupt stichhaltig belegen?',
        },
        correctionTask: { errorItems: errorsForCorrection },
        transferTask: {
          transferDomain:
            'Privates Umfeld: Mietpreisverhandlung oder WEG-Versammlung',
          scenarioPrompt:
            'Übertragen Sie die soeben geübte Deeskalationsstruktur auf eine Diskussion mit Ihrem Vermieter über eine geplante Modernisierungsumlage.',
          benchmarkExpectation:
            'Verwenden Sie die Zielstrukturen ohne Blick auf die vorherigen Notizen.',
        },
      };

    case 1:
      return {
        id: `session_${now}`,
        title: 'Nominalstil & Passiversatz in der Fachkommunikation',
        targetCategory: 'GRAMMATIK',
        totalMinutes: 18,
        environment: 'Beruf & Akademie (35%)',
        warmUpPrompt: {
          audioSpeakerText:
            'Die Anlage ist bis Freitag einer umfassenden Sicherheitsprüfung zu unterziehen; etwaige Mängel sind unverzüglich abzustellen.',
          accentInfo:
            'Kühles, präzises Standarddeutsch (1.10x), technisches Prüfregister',
          questionPrompt:
            "Welche grammatische Bedeutung hat 'ist zu unterziehen' in diesem Kontext?",
          options: [
            'Eine rein freiwillige Empfehlung ohne Zeitdruck',
            "Eine zwingende Notwendigkeit / Verpflichtung ('muss unterzogen werden')",
            'Eine abgeschlossene Handlung in der Vergangenheit',
            'Eine persönliche Vermutung des Sprechers',
          ],
          correctIndex: 1,
          diagnosticExplanation:
            "Passiversatzform 'sein + zu + Infinitiv' drückt hier eine klare modale Verpflichtung aus (muss geprüft werden).",
        },
        mainChallenge: {
          title: 'Register-Transformation: Vom Verbal- zum Nominalstil',
          scenarioContext:
            'Sie verfassen ein offizielles Status-Update für den technischen Vorstand nach einem Server-Zwischenfall.',
          partnerStatement:
            "Unser Team muss die Protokolle kürzer und förmlicher schreiben. Wie fassen Sie zusammen: 'Weil das Backup ausgefallen ist, konnten wir die Daten nicht rechtzeitig wiederherstellen'?",
          stage1Prompt:
            "Stufe 1 (Präpositionale Kausalität): Nutzen Sie 'Infolge + Genitiv' oder 'Aufgrund + Genitiv'.",
          stage2Deepening:
            "Stufe 2 (Nominalisierung): Wandeln Sie 'wiederherstellen' in ein präzises Substantiv um ('Wiederherstellung').",
          stage3StressInference:
            "Stufe 3 (Passiversatz): Ergänzen Sie eine Handlungsempfehlung mit 'lässt sich vermeiden' oder 'bleibt abzuwarten'.",
          keyPointsExpected: [
            "Korrekte Genitiv-Rektion ('infolge des Backup-Ausfalls')",
            "Vermeidung von 'weil'-Sätzen im formellen Berichtsstil",
            'Präzise Fachterminologie ohne Anglizismen-Überladung',
          ],
        },
        productionTask: {
          isSpeaking: false,
          situationBrief:
            'Formulieren Sie eine 2-sätzige Vorstandsnotiz im gehobenen Nominalstil über eine Lieferkettenverzögerung.',
          timeLimitSeconds: 90,
          keyEvaluationCriteria: [
            "Mindestens zwei Genitiv-Präpositionen ('ungeachtet', 'infolge', 'binnen')",
            'Korrekte Passiversatzform',
            'Objektiver, sachlicher Tonfall',
          ],
          simulatedAudioPrompt:
            'Fassen Sie die Ursache und die Konsequenz für den Quartalsbericht in zwei Sätzen zusammen.',
        },
        correctionTask: { errorItems: errorsForCorrection },
        transferTask: {
          transferDomain: 'Behördenkorrespondenz & Vertragswesen',
          scenarioPrompt:
            "Schreiben Sie einen kurzen Widerspruch gegen eine fehlerhafte Nebenkostenabrechnung unter Verwendung von 'unter Bezugnahme auf' und 'ungeachtet'.",
          benchmarkExpectation:
            'Klare juristische Sachlichkeit ohne emotionale Wertungen.',
        },
      };

    case 2:
      return {
        id: `session_${now}`,
        title: 'Schnellsprech-Dekodierung & Ironie im Alltag',
        targetCategory: 'HOERVERSTEHEN',
        totalMinutes: 18,
        environment: 'Alltag & Soziales (40%)',
        warmUpPrompt: {
          audioSpeakerText:
            'Na toll, schon wieder Schienenersatzverkehr! Da kannste die Pünktlichkeit heute echt in der Pfeife rauchen.',
          accentInfo:
            'Berliner Umgangssprache (1.25x), sarkastischer Unterton, Elisionen',
          questionPrompt:
            "Was drückt die Redewendung 'etwas in der Pfeife rauchen können' aus?",
          options: [
            'Man kann sich über die Situation entspannt freuen',
            'Etwas ist völlig wertlos, unbrauchbar oder endgültig gescheitert',
            'Man sollte eine Pause einlegen',
            'Die Bahn fährt heute besonders schnell',
          ],
          correctIndex: 1,
          diagnosticExplanation:
            "Sarkastisches Idiom: 'in der Pfeife rauchen können' signalisiert Frustration darüber, dass Pünktlichkeit heute völlig unmöglich ist.",
        },
        mainChallenge: {
          title: 'Subtext-Analyse: Zwischen den Zeilen hören',
          scenarioContext:
            'Ein Kollege kommentiert Ihren Vorschlag mit einem scheinbar freundlichen Lächeln.',
          partnerStatement:
            'Ach, das ist ja ein überaus ambitionierter Ansatz! Ich frage mich nur, ob die Geschäftsführung für derlei Experimente momentan den nötigen Spielraum sieht.',
          stage1Prompt:
            "Stufe 1 (Subtext erkennen): Was meint der Kollege wirklich mit 'überaus ambitioniert' und 'derlei Experimente'?",
          stage2Deepening:
            "Stufe 2 (Schlagfertig kontern): Reagieren Sie freundlich, aber nehmen Sie dem Wort 'Experiment' sofort den Wind aus den Segeln.",
          stage3StressInference:
            'Stufe 3 (Verbündete gewinnen): Stellen Sie eine offene Rückfrage, die den Kollegen zwingt, konkrete Einwände zu benennen.',
          keyPointsExpected: [
            "Erkennen von höflich verpackter Skepsis ('Experiment')",
            "Souveränes Umdeuten ('pilotierter Praxistest' statt 'Experiment')",
            'Ruhige, gelassene Stimmlage',
          ],
        },
        productionTask: {
          isSpeaking: true,
          situationBrief:
            'Parieren Sie den verdeckten Zweifel Ihres Kollegen souverän und charmant.',
          timeLimitSeconds: 75,
          keyEvaluationCriteria: [
            'Direkte Adressierung des Subtexts ohne Konfrontation',
            "Einsatz einer Modalpartikel ('doch', 'ja', 'eben') für Natürlichkeit",
            'Klare Rückführung auf messbare Pilot-Daten',
          ],
          simulatedAudioPrompt:
            'Na ja, Sie werden ja sehen, ob das oben auf Gegenliebe stößt.',
        },
        correctionTask: { errorItems: errorsForCorrection },
        transferTask: {
          transferDomain: 'Nachbarschaft & Freundeskreis',
          scenarioPrompt:
            "Reagieren Sie humorvoll, wenn jemand Ihre Urlaubsplanung als 'ziemlich gewagt' bezeichnet.",
          benchmarkExpectation:
            'Gelassene, idiomatische Schlagfertigkeit auf Augenhöhe.',
        },
      };

    case 3:
      return {
        id: `session_${now}`,
        title: 'Präzise Konnektoren & Nomen-Verb-Verbindungen',
        targetCategory: 'WORTSCHATZ',
        totalMinutes: 18,
        environment: 'Debatte & Gutachten (35%)',
        warmUpPrompt: {
          audioSpeakerText:
            'Die Auswertung fällt zwar ernüchternd aus; gleichwohl bietet sie uns einen belastbaren Ansatzpunkt für die Neuausrichtung.',
          accentInfo: 'Gehobenes Norddeutsches Standarddeutsch (1.05x)',
          questionPrompt: "Welche logische Relation stellt 'gleichwohl' hier her?",
          options: [
            'Eine zeitliche Gleichzeitigkeit',
            "Einen konzessiven Gegensatz ('trotzdem / dennoch')",
            "Eine kausale Begründung ('weil')",
            "Eine bedingte Einschränkung ('falls')",
          ],
          correctIndex: 1,
          diagnosticExplanation:
            "'Gleichwohl' ist ein gehobener konzessiver Konnektor (Synonym zu 'dennoch' oder 'nichtsdestotrotz').",
        },
        mainChallenge: {
          title: 'Kollokations-Präzision statt Allerweltsverben',
          scenarioContext:
            "Sie überarbeiten eine Projektpräsentation, die bisher zu viele B1-Verben ('machen', 'haben', 'sagen') enthält.",
          partnerStatement:
            "Ersetzen Sie in freier Rede: 'Wir haben viele Probleme gehabt, aber am Ende haben wir eine gute Lösung gemacht.'",
          stage1Prompt:
            "Stufe 1 (Nomen-Verb-Upgrade): Nutzen Sie 'auf Widerstand stoßen' oder 'mit Herausforderungen konfrontiert sein'.",
          stage2Deepening:
            "Stufe 2 (Präzises Zielverb): Ersetzen Sie 'eine Lösung machen' durch 'einen tragfähigen Kompromiss erzielen' oder 'eine Lösung herbeiführen'.",
          stage3StressInference:
            "Stufe 3 (Konnektor-Verknüpfung): Verbinden Sie beide Gedanken mit 'nichtsdestotrotz' oder 'schließlich'.",
          keyPointsExpected: [
            "Kein 'machen / tun / haben' als Vollverb-Ersatz",
            "Treffsichere Adjektiv-Nomen-Kollokation ('tragfähig', 'nachhaltig')",
            'Flüssige Satzklammer',
          ],
        },
        productionTask: {
          isSpeaking: true,
          situationBrief:
            'Sprechen Sie die auf C1-Niveau veredelte Version frei und überzeugend ein.',
          timeLimitSeconds: 80,
          keyEvaluationCriteria: [
            'Verwendung von mindestens zwei C1-Kollokationen',
            'Korrekte Verbzweit- bzw. Verbendstellung nach Konnektoren',
            'Überzeugende Betonung der Schlüsselwörter',
          ],
          simulatedAudioPrompt:
            'Wie würden Sie diesen Projektabschluss vor dem Beirat formulieren?',
        },
        correctionTask: { errorItems: errorsForCorrection },
        transferTask: {
          transferDomain: 'Bewerbungsgespräch / Jahresgespräch',
          scenarioPrompt:
            "Beschreiben Sie eine schwierige Situation aus Ihrem Arbeitsalltag mithilfe von 'in Kauf nehmen' und 'unter Beweis stellen'.",
          benchmarkExpectation:
            'Natürliche Integration der Nomen-Verb-Verbindungen.',
        },
      };

    case 4:
      return {
        id: `session_${now}`,
        title: 'Sprachmittlung & Konjunktiv I in der Berichterstattung',
        targetCategory: 'MEDIATION',
        totalMinutes: 18,
        environment: 'Mediation & Presse (25%)',
        warmUpPrompt: {
          audioSpeakerText:
            'Der Minister betonte, man habe die Warnungen der Gutachter keineswegs ignoriert; vielmehr sei eine Prüfung bereits eingeleitet worden.',
          accentInfo: 'Nachrichtensprecher-Tempo (1.15x), distanzierte Wiedergabe',
          questionPrompt:
            "Warum verwendet der Sprecher hier 'habe ignoriert' und 'sei eingeleitet worden'?",
          options: [
            'Um eine eigene Wunschvorstellung auszudrücken',
            'Zur neutralen, distanzierten Kennzeichnung indirekter Rede (Konjunktiv I)',
            'Weil die Handlung in der Zukunft liegt',
            'Um Höflichkeit gegenüber dem Zuhörer zu zeigen',
          ],
          correctIndex: 1,
          diagnosticExplanation:
            "Der Konjunktiv I signalisiert in der deutschen Presse- und Rechtssprache strikte Quellen-Distanz ('laut Aussage des Ministers').",
        },
        mainChallenge: {
          title: 'Komplexe Sachverhalte adressatengerecht vermitteln',
          scenarioContext:
            'Ein internationaler Partner fragt Sie nach der Kernaussage einer deutschen Behördenauflage.',
          partnerStatement:
            '„Die Erteilung der Betriebserlaubnis steht unter dem Vorbehalt der fristgerechten Nachreichung des Brandschutzkonzepts.“',
          stage1Prompt:
            "Stufe 1 (Entschlüsseln): Was bedeutet 'unter dem Vorbehalt' konkret für den Projektstart?",
          stage2Deepening:
            'Stufe 2 (Adressatengerecht erklären): Formulieren Sie die Bedingung klar für einen Projektleiter ohne Juristen-Deutsch.',
          stage3StressInference:
            'Stufe 3 (Handlungsschritt): Benennen Sie das Risiko bei Fristversäumnis präzise im Konjunktiv oder Konditionalsatz.',
          keyPointsExpected: [
            "Klare Übersetzung von 'Vorbehalt' (Bedingung / vorläufige Zusage)",
            'Vermeidung von Schachtelsätzen',
            'Verbindlicher, handlungsorientierter Ton',
          ],
        },
        productionTask: {
          isSpeaking: false,
          situationBrief:
            'Fassen Sie die Behördenauflage in 2–3 klaren Sätzen für Ihr Team zusammen.',
          timeLimitSeconds: 90,
          keyEvaluationCriteria: [
            'Vollständige inhaltliche Korrektheit ohne Juristen-Jargon',
            'Klare Handlungsaufforderung mit Fristbezug',
            'Stilsicheres Deutsch',
          ],
          simulatedAudioPrompt:
            'Dürfen wir also morgen schon mit dem Betrieb beginnen oder nicht?',
        },
        correctionTask: { errorItems: errorsForCorrection },
        transferTask: {
          transferDomain: 'Kundenservice / B2B-Eskalation',
          scenarioPrompt:
            "Erklären Sie einem Kunden verständlich, warum eine Rückerstattung 'vorbehaltlich einer technischen Prüfung' erfolgt.",
          benchmarkExpectation: 'Empathisch, transparent und dennoch rechtssicher.',
        },
      };

    default:
      return {
        id: `session_${now}`,
        title: 'Fußball-Trainer (E- & C-Jugend): Coaching-Präsenz & Taktikdeutsch',
        targetCategory: 'SPONTANES_SPRECHEN',
        totalMinutes: 18,
        environment: 'Sportplatz & Verein (E- & C-Jugend)',
        warmUpPrompt: {
          audioSpeakerText:
            'Trainer, warum spielt mein Sohn in der E-Jugend heute im Sturm und nicht hinten? Wir müssen doch die Tabelle halten!',
          accentInfo:
            'Emotionaler Elternton an der Seitenlinie, direktes Ansprechen (1.15x)',
          questionPrompt:
            'Welches pädagogische Grundprinzip des DFB-Kinderfußballs steht hier auf dem Spiel?',
          options: [
            'Positionsspezialisierung ab 9 Jahren für maximale Punktzahl',
            'Ganzheitliche Ausbildung durch Rotation und Spielfreude statt Ergebnisdruck',
            'Ausschluss von Eltern vom gesamten Trainingsgelände',
            'Sofortige Einführung der Abseitsfalle in der E-Jugend',
          ],
          correctIndex: 1,
          diagnosticExplanation:
            'In der E-Jugend (U10/U11) gilt laut DFB-Ausbildungskonzept Rotation auf allen Positionen und Entwicklung vor Tabellenergebnis.',
        },
        mainChallenge: {
          title:
            'Kabinen- & Platz-Coaching: Vom E-Jugend-Spaß zur C-Jugend-Taktik',
          scenarioContext:
            'Sie trainieren sowohl eine E-Jugend (9–11 J.) als auch eine C-Jugend (13–15 J.). Formulieren Sie altersgerechte, aber sprachlich präzise Ansagen.',
          partnerStatement:
            'In der C-Jugend-Halbzeit liegen Sie 0:2 hinten, weil Ihre Sechser bei Ballverlust zu weit aufgerückt waren und die Halbräume offen standen.',
          stage1Prompt:
            "Stufe 1 (E-Jugend-Tonfall): Motivieren Sie einen 10-jährigen Spieler nach einem Fehlpass, ohne das Wort 'nicht' als Vorwurf zu nutzen.",
          stage2Deepening:
            'Stufe 2 (C-Jugend-Taktik): Erklären Sie Ihrer C-Jugend in zwei klaren Sätzen, wie sie nach Ballverlust die Tiefe sichern und ins Gegenpressing gehen.',
          stage3StressInference:
            'Stufe 3 (Seitenlinien-Diplomatie): Bitten Sie einen übereifrigen Vater ruhig, das Coaching Ihnen zu überlassen und ausschließlich positiv anzufeuern.',
          keyPointsExpected: [
            'Altersgerechter Registerwechsel (spielerisch-ermutigend vs. taktisch-strukturiert)',
            "Präzise DFB-Fachbegriffe ('Umschaltspiel', 'Halbraum', 'Spielintelligenz')",
            'Souveräne, wertschätzende Abgrenzung gegenüber Eltern',
          ],
        },
        productionTask: {
          isSpeaking: true,
          situationBrief:
            'Sprechen Sie eine 45-sekündige Halbzeitansprache für Ihre C-Jugend: Ruhig, fokussiert, mit klarem taktischem Auftrag für die zweite Hälfte.',
          timeLimitSeconds: 60,
          keyEvaluationCriteria: [
            'Kurze, bildhafte Kommandos statt langer Vorträge',
            "Positive Handlungsziele ('Kopf hoch, Freilaufweg suchen') statt Verboten",
            'Klare Betonung und Präsenz in der Stimme',
          ],
          simulatedAudioPrompt:
            'Trainer, wir laufen uns tot! Sollen wir jetzt höher anlaufen oder erst ab der Mittellinie pressen?',
        },
        correctionTask: { errorItems: errorsForCorrection },
        transferTask: {
          transferDomain: 'Vereinskommunikation / Jugendvorstand',
          scenarioPrompt:
            'Begründen Sie gegenüber der Jugendleitung, warum Sie in der E-Jugend auf Minifußball-Spielformen (3-gegen-3 / Funino) setzen.',
          benchmarkExpectation:
            'Mehr Ballkontakte, schnellere Entscheidungsfindung und höhere Spielintelligenz pro Kind.',
        },
      };
  }
}
