import type { CurriculumUnit } from '../types';
import { unitsPartA } from './curriculumPartA';
import { unitsPartB } from './curriculumPartB';
import { unitsPartC } from './curriculumPartC';
import { unitsPartD } from './curriculumPartD';

const contexts = (general: string, social: string, engineering: string, coaching: string) => ({
  GENERAL: general,
  SOCIAL: social,
  ENGINEERING: engineering,
  COACHING: coaching,
});

const outline = (
  id: string,
  title: StringOrStr,
  stage: number,
  objective: string,
  general: string,
  social: string,
  engineering: string,
  coaching: string,
  prerequisites: string[] = [],
): CurriculumUnit => ({
  id,
  title,
  stage,
  prerequisites,
  objectives: [objective],
  contexts: contexts(general, social, engineering, coaching),
  content: null,
});

type StringOrStr = string;

export const stages: string[] = [
  'Standortbestimmung & Profil',
  'Fundament & Präzision (B2+ → C1)',
  'Komplexes Verstehen & Analyse',
  'Anspruchsvolle Text- & Redeproduktion',
  'Interaktionspragmatik & Verhandlung',
  'Integrative Sprachmittlung & Moderation',
  'Freier Transfer & Langzeitabruf',
];

const roadmapUnits: CurriculumUnit[] = [
  outline(
    's0-profile',
    'Sprachbiografie und Zielprofil',
    0,
    'Kommunikative Ziele anhand konkreter Situationen und überprüfbarer Kriterien formulieren.',
    'Behördengespräche planen',
    'Beim schwäbischen Vereinsabend Anrede und gewünschte Direktheit erfragen',
    'Sprachliche Aufgaben im EV-Fahrdynamikreview erfassen',
    'Sprachziele für U13-Trainingsansagen und Elterngespräche festlegen',
  ),
  outline(
    's0-diagnose',
    'Verstehen und Produzieren diagnostizieren',
    0,
    'Verstehens- und Produktionsleistung getrennt einschätzen und mit Fremdurteilen abgleichen.',
    'Radiobeitrag zusammenfassen',
    "Eine Einladung mit 'Komm halt vorbei' nach Ton und Beziehung deuten",
    'Einen Hybrid-TCU-Fehlerbericht verstehen und erläutern',
    'Eine U15-Taktikansage verstehen und eine Elternfrage zur Spielzeit paraphrasieren',
  ),
  outline(
    's0-repair',
    'Verständnisprobleme sichtbar machen',
    1,
    'Gezielt nachfragen und eine missverständliche Aussage selbst reparieren.',
    'Vertragsbedingung erfragen',
    "'Passt scho' im schwäbischen Gespräch nicht als sichere Zusage werten, sondern freundlich nachfragen",
    'Eine ECU/TCU-Drehmomentanforderung rückversichern',
    'Eine U11-Passübung von einem Jugendspieler in eigenen Worten wiedergeben lassen',
  ),
  outline(
    's0-retrieve',
    'Abrufen statt Wiederlesen',
    1,
    'Aus dem Gedächtnis produzieren und wiederkehrende Fehler zeitversetzt bearbeiten.',
    'Eine Nachricht frei wiedergeben',
    "'Könntest du mal …?' freundlich ohne drängenden Ton neu einsetzen",
    'Den Ablauf einer EV-FMEA ohne Vorlage erläutern',
    'Eine U13-Aufwärmübung ohne Vorlage erklären und einen früheren Ansagefehler korrigieren',
  ),
  {
    id: 's1-explain',
    title: 'Einen Ablauf verständlich erklären',
    stage: 1,
    prerequisites: [],
    objectives: [
      'Einen Ablauf logisch gliedern und Ursache von Reihenfolge unterscheiden.',
      'Fachbegriffe für fachfremde Gesprächspartner erklären und Verständnis prüfen.',
    ],
    contexts: contexts(
      'Die Ausleihe in einer Bibliothek erklären',
      "Einen Ablauf mit 'Schau mal' erläutern; im schwäbischen Verein bei unbekannten Wörtern höflich nachfragen",
      'Die Prüfschritte einer ECU/TCU-Drehmoment-Schnittstelle am Hybrid-Prüfstand erklären',
      'Eine U13-Passübung mit Freilaufen und Rollenwechsel anleiten',
    ),
    content: {
      reading: `Heute übt unsere U13 das Freilaufen nach einem Pass. Je drei Jugendspieler bilden an drei Hütchen ein Dreieck. Zunächst passt die Person mit Ball zur nächsten Station. Anschließend läuft sie zum freien Hütchen, damit wieder eine Anspielmöglichkeit entsteht. Mit „Freilaufen“ ist hier gemeint, sich ohne Ball so anzubieten, dass ein Pass möglich wird; gemeint ist nicht, möglichst schnell irgendwohin zu rennen. Erst wenn die nächste Person Blickkontakt aufnimmt, wird weitergespielt. Misslingt die Annahme, stoppen die drei kurz und beginnen ruhig neu; niemand scheidet aus. Nach zwei Minuten wechselt die Passrichtung. Vor dem Start lässt die Trainerin einen Spieler den Ablauf zeigen und eine andere Spielerin den Neustart erklären. Die Gruppe übt zunächst ohne Gegnerdruck.`,
      listening: `Trainerin: Schaut mal: Erst passen, dann zum freien Hütchen laufen.
Spieler: Soll ich einfach so schnell wie möglich losrennen?
Trainerin: Nicht einfach irgendwohin. Du bietest dich so an, dass du wieder anspielbar bist.
Spielerin: Und wenn mir der Ball verspringt, bin ich raus?
Trainerin: Nein, ihr stoppt kurz und startet gemeinsam neu. Zeigt mir doch mal, wie das aussieht.
Spieler: Also erst Blickkontakt, dann der nächste Pass; bei einem Fehler ruhig neu anfangen?
Trainerin: Genau. Nach zwei Minuten ändern wir die Richtung.`,
      expressions: [
        'Zunächst …, anschließend …, erst dann … – Schritte ordnen',
        'Unter Freilaufen versteht man hier … – einen Fußballbegriff eingrenzen',
        'Das bedeutet noch nicht, dass … – einen Fehlschluss verhindern',
        'Könnt ihr kurz zeigen, was als Nächstes passiert? – Verständnis altersgerecht prüfen',
        'Schau mal … / Zeig doch mal … – Modalpartikeln können eine Einladung abmildern; Ton und Beziehung entscheiden',
        "Im schwäbischen Vereinsgespräch: 'Was bedeutet das hier genau?' – respektvoll nachfragen, statt Dialekt nachzuahmen",
      ],
      grammar:
        "Temporalsätze: 'Erst wenn Blickkontakt besteht, spielt ihr weiter.' Im wenn-Satz steht das finite Verb am Ende, nach dem vorangestellten Nebensatz folgt das Verb des Hauptsatzes. 'Damit wieder eine Anspielmöglichkeit entsteht' nennt einen Zweck, nicht den nächsten Schritt. 'Mal' in einer freundlichen Aufforderung bezeichnet keine genaue Anzahl.",
      question: 'Was geschieht, wenn bei der U13-Passübung die Ballannahme misslingt?',
      options: [
        'Die betroffene Person scheidet aus.',
        'Die drei stoppen kurz und beginnen ruhig neu.',
        'Die Gruppe wechselt nach jedem Fehler sofort die Passrichtung.',
      ],
      correctOption: 1,
      explanation:
        'B entspricht der beschriebenen Fehlerregel. A verwechselt Üben mit einem Ausscheidungsspiel. C verwechselt den geplanten Richtungswechsel nach zwei Minuten mit dem Neustart nach einem Fehler.',
      controlledPrompt:
        "Verbinden Sie ohne neue Fakten: Blickkontakt bestehen → weiterspielen. Erklären Sie 'Freilaufen' mit 'Damit ist … gemeint' und nennen Sie die Fehlerregel.",
      controlledModel:
        'Erst wenn Blickkontakt besteht, spielt ihr weiter. Mit Freilaufen ist hier gemeint, sich ohne Ball für einen Pass anzubieten. Misslingt die Annahme, stoppt ihr kurz und beginnt gemeinsam neu.',
      speaking:
        'Erklären Sie einer U13-Gruppe die Passübung in 90 Sekunden ohne Vorlage. Definieren Sie Freilaufen, nennen Sie den Neustart und lassen Sie zwei Jugendspieler den nächsten Schritt zeigen.',
      interaction:
        "A trainiert die U13; B glaubt, nach einem Annahmefehler ausscheiden zu müssen. Klären Sie das Missverständnis ohne Bloßstellung. 'Zeig doch mal' soll einladen, nicht ungeduldig klingen. Tauschen Sie die Rollen.",
      writing:
        'Schreiben Sie eine Anleitung von 140–180 Wörtern für eine neue U13-Co-Trainerin: Aufbau, Passfolge, Blickkontakt, Neustart und Richtungswechsel. Erklären Sie Fußballbegriffe und trennen Sie Zeit- von Fehlerregeln.',
      transfer:
        'Unbekannter Automotive-Kontext, ohne Modell: Bei einem Hybrid-Prüfstandtest wird zuerst die ECU/TCU-Softwareversion dokumentiert, dann das freigegebene Testszenario ausgeführt und anschließend die angeforderte mit der gemeldeten Drehmomentgröße verglichen. Bei einer Abweichung wird der Test gemäß Prüfplan unterbrochen und die verantwortliche Prüfleitung informiert; daraus folgt keine Fahrzeugfreigabe. Erklären Sie den Ablauf für neue Mitarbeitende, ohne Bedien- oder Sicherheitsregeln zu erfinden. Nach mindestens sieben Tagen folgt ein neuer Ablauf in einem anderen Kontext mit menschlicher Bewertung.',
      feedback:
        "Modellrückmeldung: Pass und Laufweg sind klar getrennt. 'Dann schnell weg' erklärt aber nicht das Ziel des Freilaufens; nennen Sie die erneute Anspielbarkeit. Ihre freundliche Einladung 'Zeig mal' passt zur Gruppe, ersetzt jedoch keine Verständnisprüfung bei allen. Lassen Sie auch die Fehlerregel wiedergeben. Im Automotive-Transfer sind Testabschluss und Fahrzeugfreigabe ebenso sorgfältig zu unterscheiden.",
    },
  },
  {
    id: 's1-justify',
    title: 'Eine Entscheidung nachvollziehbar begründen',
    stage: 1,
    prerequisites: ['s1-explain'],
    objectives: [
      'Eine Empfehlung mit Kriterien, Belegen und Abwägung stützen.',
      'Eine Alternative fair darstellen und eine Bedingung für Revision nennen.',
    ],
    contexts: contexts(
      'Zwischen Mobilitätsangeboten wählen',
      "Im schwäbischen Verein einen Budgetvorschlag mit 'Das wäre doch …' machen, ohne Zustimmung vorauszusetzen",
      'Eine EV-Rekuperationsabstimmung anhand von Fahrdynamik und Safety-Nachweisen begründen',
      'Für U13-Spieler kleine Spielformen statt langer Warteschlangen begründet empfehlen',
    ),
    content: {
      reading: `Unser Jugendfußballverein kann entweder einen eigenen Kleinbus für Auswärtsspiele kaufen oder Fahrzeuge nach Bedarf mieten. Für einen Kauf spricht, dass der Bus jederzeit verfügbar wäre. Allerdings haben wir im vergangenen Jahr nur zwölf Fahrten organisiert. Versicherung, Wartung und Stellplatz würden auch in Monaten ohne Fahrt anfallen. Deshalb empfiehlt der Vorstand vorerst die Miete. Ausschlaggebend sind nicht allein die Anschaffungskosten, sondern die Gesamtkosten bei geringer Nutzung. Gegen die Miete spricht das Risiko, kurzfristig kein passendes Fahrzeug zu finden. Wir schlagen daher vor, Termine früh zu buchen und nach sechs Monaten erneut zu prüfen. Sollten wir künftig jede Woche fahren, müsste die Entscheidung neu bewertet werden. Die Empfehlung gilt also unter einer überprüfbaren Annahme.`,
      listening: `Jonas: Ein eigener Bus wäre doch viel praktischer.
Aylin: Das stimmt hinsichtlich der Verfügbarkeit. Bei zwölf Fahrten im Jahr wiegen die festen Kosten für mich aber schwerer.
Jonas: Du meinst also, Mieten ist immer billiger?
Aylin: Nein. Meine Empfehlung bezieht sich auf unsere bisherige Nutzung. Wenn wir jede Woche fahren, kann ein Kauf sinnvoller sein.
Jonas: Und spontane Fahrten?
Aylin: Das ist der stärkste Einwand. Dafür brauchen wir einen zweiten Anbieter als Ausweichmöglichkeit.`,
      expressions: [
        'Für … spricht, dass … – ein Argument einführen',
        'Ausschlaggebend ist … – ein Kriterium gewichten',
        'Zwar …, allerdings … – einen Vorteil gegen einen Nachteil abwägen',
        'Unter der Voraussetzung, dass … – die Geltung begrenzen',
        "Das wäre doch eine Möglichkeit – 'doch' wirbt für Zustimmung, kann bei Widerspruch aber drängend wirken",
        "Bei einem schwäbischen 'Des koscht halt' nach dem konkreten Kostenargument fragen; regionale Redeweisen weder verspotten noch allen zuschreiben",
      ],
      grammar:
        "'Zwar wäre ein Kauf praktisch, allerdings fallen feste Kosten an.' Konjunktiv II markiert eine hypothetische Option, nicht einen Beleg. 'Deshalb' leitet die Folgerung mit Verbzweitstellung ein: 'Deshalb empfehlen wir die Miete.' 'Sollten wir häufiger fahren, müssten wir neu rechnen' nennt eine Revisionsbedingung.",
      question: 'Welche Begründung entspricht der Empfehlung des Vorstands?',
      options: [
        'Mieten ist unabhängig von der Nutzung immer günstiger.',
        'Ein Kauf hat keinerlei Vorteile.',
        'Bei bisher geringer Nutzung wiegen feste Kosten schwerer als ständige Verfügbarkeit.',
      ],
      correctOption: 2,
      explanation:
        'C verknüpft das Kriterium Gesamtkosten mit der belegten geringen Nutzung und wägt Verfügbarkeit ab. A verallgemeinert unzulässig; B unterschlägt den ausdrücklich genannten Kaufvorteil.',
      controlledPrompt:
        'Formulieren Sie vier Sätze: Kaufvorteil anerkennen (zwar), geringe Nutzung als Grund nennen (da), Miete empfehlen (deshalb), häufigere Nutzung als Revisionsbedingung nennen (sollten).',
      controlledModel:
        'Zwar wäre ein eigener Bus jederzeit verfügbar. Da wir bisher nur zwölf Fahrten im Jahr organisieren, fallen die festen Kosten jedoch stark ins Gewicht. Deshalb empfehle ich zunächst die Miete. Sollten wir künftig jede Woche fahren, müssten wir die Gesamtkosten neu vergleichen.',
      speaking:
        'Empfehlen Sie dem Jugendfußballverein in zwei Minuten eine Fahrtoption für die U15. Verwenden Sie ein Kriterium, die zwölf bisherigen Fahrten, einen ernsthaften Einwand und eine Revisionsbedingung; erfinden Sie keine Kostenzahlen. Begründen Sie anschließend für eine U13-Trainingsgruppe zwei parallele Kleinfelder statt einer langen Warteschlange mit mehr Ballkontakten; prüfen Sie Platz und Betreuung als Voraussetzungen.',
      interaction:
        "A vertritt die Miete, B als Elternvertretung den Kauf und bringt ein kurzfristiges U15-Auswärtsspiel ins Spiel. Fassen Sie das stärkste Gegenargument fair zusammen und handeln Sie ein überprüfbares Vorgehen aus. Reagieren Sie auf 'Das ist doch klar' sachlich, ohne 'doch' mit einem Beleg zu verwechseln.",
      writing:
        'Verfassen Sie eine Entscheidungsvorlage von 170–210 Wörtern. Trennen Sie bekannte Fakten, Annahmen und Empfehlung. Erläutern Sie, welche zusätzliche Information Ihre Empfehlung ändern könnte.',
      transfer:
        'Unbekannter Automotive-Kontext: Ein EV-Team vergleicht zwei Rekuperationsabstimmungen. Variante A zeigt im vorliegenden Fahrzyklus höhere Energierückgewinnung; bei niedriger Haftung fehlen noch Fahrdynamikdaten. Für Variante B liegt der geforderte Nachweis im betrachteten Testumfang vor, ein Energievergleich fehlt. Empfehlen Sie das weitere Prüfprogramm, nicht eine unbelegte Serienfreigabe. Berücksichtigen Sie die FMEA, also die Fehlermöglichkeits- und -einflussanalyse, und reagieren Sie auf Lieferdruck. Erfinden Sie weder Sicherheitsgrenzen noch allgemeine Überlegenheit. Nach mindestens sieben Tagen folgt eine neue Entscheidung in einem anderen Kontext mit menschlicher Bewertung.',
      feedback:
        "Modellrückmeldung: Sie würdigen die Verfügbarkeit und legen Ihre Gewichtung offen. 'Miete ist billiger' geht aber über die Daten hinaus; präziser wäre 'Bei geringer Nutzung spricht die Vermeidung fester Kosten für die Miete; ein Kostenvergleich steht noch aus.' Behalten Sie Ihre klare Empfehlung bei, kennzeichnen Sie aber ihre Voraussetzungen.",
    },
  },
  {
    id: 's1-uncertainty',
    title: 'Unsicherheit präzise ausdrücken',
    stage: 1,
    prerequisites: ['s1-explain'],
    objectives: [
      'Beobachtung, Vermutung und gesichertes Wissen sprachlich trennen.',
      'Eine überprüfbare Hypothese mit nächstem Prüfschritt formulieren.',
    ],
    contexts: contexts(
      'Eine widersprüchliche Nachricht einordnen',
      "Eine ausgebliebene Antwort mit 'Vielleicht war einfach viel los' offenlassen; schwäbisches Understatement nicht als sichere Zustimmung deuten",
      'Die Root Cause eines Hybrid-Schaltruckens zwischen ECU und TCU eingrenzen',
      'Bei zwei U14-Trainingsabsenzen Spieler und Eltern offen nach organisatorischen Gründen fragen',
    ),
    content: {
      reading: `Bei zwei Hybrid-Prototypen wurde nach einem Softwarewechsel ein Rucken beim Übergang vom elektrischen zum kombinierten Antrieb beobachtet. Fest steht bisher nur, dass in diesen Versuchen die von der ECU, dem Motorsteuergerät, gemeldete Drehmomentgröße später als die Anforderung der TCU, des Getriebesteuergeräts, im Messprotokoll erscheint. Ob die Software den Effekt verursacht, ist offen: Gleichzeitig wurde die Messkonfiguration geändert. Möglicherweise stimmen die Zeitstempel der Aufzeichnung nicht überein. Denkbar ist auch eine tatsächliche Verzögerung in der Drehmomentkoordination. Das Team prüft zunächst die Messkette und vergleicht anschließend freigegebene Konfigurationen am Prüfstand nach Prüfplan. Bis zur Klärung bleibt die Freigabe für weitere Fahrzeugversuche ausgesetzt. Das ist eine vorsorgliche Entscheidung, kein Nachweis einer bestimmten Fehlerursache. Die FMEA wird auf betroffene Fehlermöglichkeiten geprüft.`,
      listening: `Leitung: Ist bewiesen, dass die neue TCU-Software das Rucken verursacht?
Ingenieurin: Nein. Das Rucken ist beobachtet; die Root Cause, also die eigentliche Fehlerursache, ist noch offen. Auch die Messzeitstempel könnten eine Rolle spielen.
Leitung: Das müsste doch mit einem Vergleich schnell geklärt sein?
Ingenieurin: Ein Vergleich kann Hinweise liefern. Zuerst müssen wir aber prüfen, ob die Messdaten zeitlich vergleichbar sind.
Leitung: Können wir die Fahrzeugversuche trotzdem freigeben?
Ingenieurin: Dafür liegt hier kein Nachweis vor. Die Freigabe bleibt bis zur Klärung ausgesetzt; wir testen nur im genehmigten Prüfstandumfang.
Leitung: Dann brauche ich beim nächsten Review die Ergebnisse und die noch offenen Punkte.`,
      expressions: [
        'Fest steht bislang nur, dass … – Wissen begrenzen',
        'Das könnte darauf hindeuten, dass … – vorsichtig folgern',
        'Daraus lässt sich noch nicht ableiten, dass … – Evidenzgrenzen markieren',
        'Um die Vermutung zu prüfen, … – einen Prüfschritt nennen',
        "Das ist wohl noch offen – 'wohl' markiert eine Einschätzung, keine gemessene Wahrscheinlichkeit",
        "'Wird scho passen' kann im schwäbischen Alltagsgespräch beruhigen; bei unklaren Zusagen freundlich konkretisieren, bei Safety-Fragen explizite Nachweise verlangen",
      ],
      grammar:
        "'Die Messkonfiguration könnte den Zeitversatz erklären' verwendet Konjunktiv II für Möglichkeit. 'Die Software dürfte beteiligt sein' signalisiert eine stärkere Vermutung und braucht stärkere Belege. 'Ob die Software ursächlich ist, bleibt offen' formuliert eine indirekte Entscheidungsfrage mit Verb am Ende. 'Das müsste doch …' äußert eine Erwartung, keinen technischen Nachweis. Vermeiden Sie zugleich 'sicher' und 'vielleicht' für dieselbe Aussage.",
      question: 'Welche Aussage ist durch den Bericht gedeckt?',
      options: [
        'Die Ursache ist offen; Messkettenprüfung und Vergleich sollen Vermutungen prüfen.',
        'Die neue TCU-Software ist als Ursache bewiesen.',
        'Die ausgesetzte Freigabe beweist, dass die ECU defekt ist.',
      ],
      correctOption: 0,
      explanation:
        'A trennt Beobachtung und Hypothese. B macht aus einer zeitlichen Nähe einen Kausalbeweis. C verwechselt eine vorsorglich ausgesetzte Freigabe mit dem Nachweis eines bestimmten Bauteilfehlers.',
      controlledPrompt:
        "Ordnen Sie sprachlich ein: beobachtet = Rucken bei zwei Hybrid-Prototypen; möglich = nicht vergleichbare Messzeitstempel; unbekannt = Root Cause; nächster Schritt = Messkette prüfen. Nutzen Sie 'Fest steht', 'könnte', 'ob' und 'um … zu'.",
      controlledModel:
        'Fest steht, dass bei zwei Hybrid-Prototypen ein Rucken beobachtet wurde. Eine Änderung der Messzeitstempel könnte den protokollierten Zeitversatz erklären. Ob die Software das Rucken verursacht, bleibt offen. Um die Messdaten einordnen zu können, prüfen wir zunächst die Messkette.',
      speaking:
        "Geben Sie im Automotive-Review ein 90-sekündiges Lagebild ohne Vorlage: beobachtetes Hybrid-Rucken, zwei Erklärungen für den protokollierten Zeitversatz, Evidenzgrenze, Prüfschritt und Freigabestatus. Reagieren Sie auf 'Können Sie die Root Cause garantieren?'.",
      interaction:
        'A verlangt wegen des Termindrucks eine eindeutige Zuordnung zu ECU oder TCU; B benennt Unsicherheit, Prüfplan und nächsten Berichtstermin. A paraphrasiert, was beobachtet und was unbewiesen ist. Erfinden Sie keine Safety-Freigabe.',
      writing:
        'Schreiben Sie eine Automotive-Statusmeldung von 140–180 Wörtern für eine fachübergreifende Runde. Erklären Sie ECU, TCU und Root Cause, vermeiden Sie erfundene Wahrscheinlichkeiten und trennen Sie Messbefund, Hypothese und Freigabestatus.',
      transfer:
        'Unbekannter Jugendfußball-Kontext: Ein U14-Spieler hat zweimal beim Training gefehlt; eine Nachricht der Eltern liegt noch nicht vor. Die Gründe sind unbekannt. Nennen Sie als ausdrücklich offene Möglichkeiten etwa einen Terminüberschneidungskonflikt oder einen nicht angekommenen Trainingsplan, ohne Motivation, Gesundheit oder Familienlage zu behaupten. Fragen Sie Spieler und Eltern respektvoll nach dem Informationsweg und vereinbaren Sie die Rückmeldung zum nächsten Training. Es geht um Teilnahmeorganisation, nicht um eine Bewertung der Persönlichkeit. Bearbeiten Sie nach mindestens sieben Tagen eine andere unvorbereitete Situation mit menschlicher Fremdbewertung.',
      feedback:
        "Modellrückmeldung: Sie trennen den Messbefund von der Root Cause. 'Das liegt wohl eindeutig an der TCU' hebt diese Trennung wieder auf; besser: 'Ein Zusammenhang ist möglich, die Messkette ist noch zu prüfen.' Im Jugendfußball-Transfer war Ihre Elternfrage offen und respektvoll. 'Er hat eben keine Lust' wäre dagegen eine unbelegte Zuschreibung; fragen Sie konkret, ob Trainingszeit und Nachricht angekommen sind.",
    },
  },
  {
    id: 's1-feedback',
    title: 'Konstruktiv Rückmeldung geben',
    stage: 1,
    prerequisites: ['s1-justify', 's1-uncertainty'],
    objectives: [
      'Beobachtung, Wirkung und Wunsch ohne persönliche Abwertung formulieren.',
      'Die Sicht des Gegenübers erfragen und eine überprüfbare Vereinbarung treffen.',
    ],
    contexts: contexts(
      'Eine Dienstleistung respektvoll beanstanden',
      "Im schwäbischen Ehrenamt 'Nicht schlecht' nicht als eindeutiges Urteil behandeln, sondern konkrete Rückmeldung erfragen",
      'Fehlende Annahmen in einer EV-FMEA-Übergabe konstruktiv ansprechen',
      'Einem U12-Spieler Rückmeldung zu Schulterblick und Passentscheidung geben',
    ),
    content: {
      reading: `Nach einer gemeinsamen Präsentation zur EV-FMEA, einer Analyse möglicher Fehler und ihrer Auswirkungen im Elektrofahrzeug, bittet Mara ihren Kollegen um ein kurzes Gespräch: „Passt es gerade für eine Rückmeldung zur Übergabe? Als du nach meiner Einleitung direkt zu den Ergebnissen gewechselt bist, blieb die Folie mit den Annahmen aus. Zwei Zuhörer fragten später nach unserer Datengrundlage. Für mich war dadurch schwer erkennbar, ob wir die Aussagekraft der Ergebnisse ausreichend eingegrenzt hatten. Mir ist wichtig, dass wir beim nächsten Mal erst die Annahmen nennen. Wie hast du die Situation erlebt?“ Ihr Kollege erklärt, er habe wegen eines verspäteten Beginns kürzen wollen. Beide vereinbaren ein sichtbares Zeitsignal und legen fest, welche Folie selbst bei Zeitdruck nicht entfällt. Die Rückmeldung beschreibt Verhalten, nicht den Charakter des Kollegen.`,
      listening: `Mara: Darf ich dir etwas zur EV-FMEA-Präsentation rückmelden?
Leon: Wenn es kurz geht. Du fandest es bestimmt wieder schlecht.
Mara: Mir geht es um eine konkrete Stelle, nicht um ein Gesamturteil. Die Annahmenfolie fiel weg; danach kamen Rückfragen zur Datengrundlage.
Leon: Wir hatten doch kaum noch Zeit.
Mara: Den Zeitdruck verstehe ich. Gleichzeitig brauchen wir diese Einordnung. Wie könnten wir beim nächsten Mal früher merken, dass wir kürzen müssen?
Leon: Gib mir zwei Minuten vor Schluss ein Zeichen. Die Annahmen lassen wir dann trotzdem drin.
Mara: Einverstanden. Nach der nächsten Präsentation prüfen wir kurz, ob das funktioniert hat.`,
      expressions: [
        'Passt es gerade für eine Rückmeldung zu …? – Einverständnis einholen',
        'Mir ist an der Stelle aufgefallen, dass … – Beobachtung eingrenzen',
        'Auf mich wirkte das …; wie hast du es erlebt? – Wirkung von Absicht trennen',
        'Könnten wir für das nächste Mal vereinbaren, dass …? – eine konkrete Bitte formulieren',
        "Lass uns doch kurz auf die Szene schauen – 'doch' kann einladen, ein scharfes 'doch!' aber widersprechen",
        "Im schwäbischen Vereinsgespräch bei knapper Rückmeldung nachfragen: 'Was war konkret hilfreich, was sollen wir ändern?' – keine vermeintliche Regionalregel voraussetzen",
      ],
      grammar:
        "'Als du zu den Ergebnissen gewechselt bist, blieb die Folie aus' bezeichnet ein einzelnes vergangenes Ereignis; 'immer wenn' würde eine belegpflichtige Verallgemeinerung ausdrücken. 'Könnten wir …?' ist eine höfliche Bitte im Konjunktiv II. Eine Ich-Botschaft wie 'Ich finde, du bist unfähig' bleibt trotz 'ich' eine persönliche Abwertung.",
      question: 'Welche Rückmeldung folgt dem beschriebenen Vorgehen am besten?',
      options: [
        'Du lässt immer die wichtigen Dinge weg; sei endlich professionell.',
        'Als die Annahmenfolie ausblieb, kamen Rückfragen. Können wir vereinbaren, sie künftig beizubehalten? Wie siehst du das?',
        'Ich finde dich unzuverlässig, aber nimm es bitte nicht persönlich.',
      ],
      correctOption: 1,
      explanation:
        'B benennt ein konkretes Verhalten, dessen beobachtete Folge und eine verhandelbare Bitte und öffnet den Dialog. A verallgemeinert und wertet ab. C versteckt ein Charakterurteil hinter einer Ich-Formulierung.',
      controlledPrompt:
        "Formulieren Sie Beobachtung → Wirkung → Bitte → offene Frage: Übergabe ohne Annahmenfolie; Rückfragen zur Datengrundlage; Folie künftig beizubehalten. Keine Wörter wie 'immer', 'nie' oder Charakterurteile.",
      controlledModel:
        'Als du zu den Ergebnissen übergegangen bist, blieb die Annahmenfolie aus. Danach kamen Rückfragen zur Datengrundlage. Könnten wir vereinbaren, diese Folie beim nächsten Mal auch unter Zeitdruck zu zeigen? Wie hast du die Situation erlebt?',
      speaking:
        'Geben Sie einem U12-Spieler nach der Übung 90 Sekunden altersgerechtes Feedback: In zwei beobachteten Szenen kam der Pass ohne vorherigen Schulterblick, obwohl eine freie Mitspielerin hinter ihm stand. Beschreiben Sie nur diese Szenen, fragen Sie nach seiner Wahrnehmung und vereinbaren Sie einen Schulterblick vor der nächsten Ballannahme; behaupten Sie nicht, er sei grundsätzlich unaufmerksam.',
      interaction:
        'A gibt Feedback zur EV-FMEA-Übergabe; B reagiert defensiv und nennt Zeitdruck. A erkennt den Grund an, besteht aber auf transparenten Annahmen und dokumentiert offenen Safety-Nachweisen. Vereinbaren Sie ein Signal und einen Auswertungstermin; die Gesprächsvereinbarung ersetzt keine technische Freigabe. Tauschen Sie die Rollen.',
      writing:
        'Schreiben Sie einer U12-Co-Trainerin eine vertrauliche Nachricht von 150–190 Wörtern: Bei zwei Passübungen blieben neue Jugendspieler ohne Rollenklärung am Rand stehen. Beschreiben Sie die Beobachtung ohne Absichtsunterstellung, fragen Sie nach ihrer Sicht und schlagen Sie klare Wechselrollen für das nächste Fußballtraining vor.',
      transfer:
        "Unbekannter Jugendfußball-Kontext: Zwei Eltern einer U11-Mannschaft haben eine kurzfristige Platzänderung nicht erfahren; die Zuständigkeit im Trainerteam war unklar. Führen Sie ein vertrauliches Feedbackgespräch mit der zuständigen erwachsenen Person, reagieren Sie auf 'Ich dachte halt, das macht jemand anders' und vereinbaren Sie, wer die Eltern bis wann über welchen Kanal informiert. 'Halt' kann hier eine Erklärung oder ein Ausweichen signalisieren; fragen Sie nach, statt Absichten zu unterstellen. Lassen Sie nach mindestens sieben Tagen ein neues Gespräch in einem weiteren Kontext menschlich beurteilen.",
      feedback:
        "Modellrückmeldung: Ihr Einstieg gibt dem Gegenüber Raum, und die Bitte ist umsetzbar. 'Du hast die Eltern ignoriert' unterstellt jedoch eine Absicht; benennen Sie die nicht angekommene Platzinformation. Beim U12-Spieler ist 'Vor diesen beiden Pässen fehlte der Schulterblick' hilfreicher als ein Charakterurteil. Im EV-FMEA-Gespräch erkennen Sie Zeitdruck an, ohne offene Nachweise zu verschweigen. Halten Sie in jedem Kontext fest, wer was bis wann überprüft.",
    },
  },
  outline(
    's2-summarise',
    'Kernaussagen verdichten',
    5,
    'Informationen aus mehreren Quellen adressatengerecht bündeln, ohne Einschränkungen zu verlieren.',
    'Zwei Sachbeiträge für Laien zusammenfassen',
    "Eine Vereinsdiskussion trotz 'Das ist doch klar' neutral protokollieren",
    'EV-Fahrdynamikmessungen und FMEA-Befunde für die Projektleitung verdichten',
    'U15-Spielbeobachtungen und Spielerfragen für das nächste Fußballtraining bündeln',
    ['s1-explain'],
  ),
  outline(
    's2-mediate',
    'Zwischen Perspektiven vermitteln',
    5,
    'Zwei Positionen fair wiedergeben und Gemeinsamkeiten sichtbar machen.',
    'Eine öffentliche Debatte erklären',
    'Zwischen Nachbarn vermitteln und bei regionalen Wendungen die gemeinte Aussage rückversichern',
    'Zwischen ECU-Drehmomentregelung und TCU-Schaltstrategie im Hybrid-Team vermitteln',
    'Zwischen U14-Spielern, Eltern und Trainerteam über Einsatzzeiten vermitteln',
  ),
  outline(
    's2-data',
    'Daten und Einschränkungen verstehen',
    2,
    'Trends, Messunsicherheiten und nicht belegte Schlussfolgerungen in anspruchsvollen Berichten erkennen.',
    'Eine Umfrage samt Einschränkungen verstehen',
    "Mitgliederzahlen verstehen, ohne 'bloß ein paar' mit einer genauen Anzahl gleichzusetzen",
    'Einen EV-Bericht zu Querbeschleunigung und Messunsicherheit verstehen',
    'Einen U15-Spielbericht mit Passquoten lesen und Grenzen der Aussage erkennen',
  ),
  outline(
    's2-register',
    'Register bewusst wechseln',
    4,
    'Dieselbe Botschaft formell und informell angemessen formulieren.',
    'Eine Anfrage an eine Behörde richten',
    "Eine sensible Nachricht mit oder ohne 'mal' formulieren; Du und Sie nicht aus regionaler Nähe ableiten",
    'Einen ECU/TCU-Schnittstellenfehler intern und gegenüber einem Automotive-Zulieferer melden',
    'Eine Trainingsabsage für U11-Spieler und deren Eltern unterschiedlich formulieren',
  ),
  outline(
    's3-negotiate',
    'Interessen aushandeln',
    4,
    'Positionen von Interessen trennen und Optionen entwickeln.',
    'Einen Termin aushandeln',
    "Haushaltsaufgaben verhandeln, ohne mit 'Das machst du doch' Zustimmung zu unterstellen",
    'Prüfumfang für eine Hybrid-TCU-Änderung unter Safety-Vorgaben aushandeln',
    'Trainingszeiten und Platzhälften zwischen U13 und U15 aushandeln',
  ),
  outline(
    's3-disagree',
    'Differenziert widersprechen',
    4,
    'Einen Einwand klar und gesichtswahrend vertreten.',
    'Einer Empfehlung widersprechen',
    "Einer Gruppenentscheidung mit 'Da bin ich noch nicht ganz überzeugt' respektvoll widersprechen",
    'Einer unbelegten Root-Cause-Zuordnung zum EV-Inverter widersprechen',
    'Einem Elternwunsch nach fester U12-Stammaufstellung mit Entwicklungszielen widersprechen',
  ),
  outline(
    's3-deescalate',
    'Konflikte deeskalieren',
    4,
    'Emotionen anerkennen, ohne Vorwürfe als Fakten zu übernehmen.',
    'Eine Beschwerde klären',
    "Bei 'Jetzt hör halt zu' den Ton ansprechen und zum Anliegen zurückführen",
    'Ein Automotive-Zulieferergespräch über ungeklärte ECU-Ausfälle beruhigen',
    'Ein aufgebrachtes Elterngespräch über U14-Spielzeit ruhig und vertraulich führen',
  ),
  outline(
    's3-agree',
    'Vereinbarungen absichern',
    4,
    'Verantwortung, Termin und Erfolgskriterium gemeinsam festhalten.',
    'Eine Reparatur vereinbaren',
    "Ein schwäbisches 'Schauen wir mal' nicht als Terminbestätigung behandeln und freundlich konkretisieren",
    'Verantwortung und Nachweise für einen TCU-Prüfplan festhalten',
    'Mit U11-Eltern Abholort, Aufsicht und Rückmeldung nach dem Fußballtraining vereinbaren',
  ),
  outline(
    's4-argument',
    'Komplexe Argumentation strukturieren',
    3,
    'These, Beleg und Gegenargument in längeren Beiträgen kohärent verknüpfen.',
    'Eine Stellungnahme verfassen',
    "Einen Förderantrag sachlich begründen statt 'Das braucht man eben' zu behaupten",
    'Eine EV-Rekuperationsstrategie mit Fahrdynamikdaten und Safety-Grenzen begründen',
    'Ein U13-Trainingskonzept mit Kleinfeldspielen und gestuften Aufgaben schriftlich begründen',
  ),
  outline(
    's4-challenge',
    'Annahmen kritisch prüfen',
    2,
    'Implizite Prämissen und alternative Ursachen in komplexen Texten und Gesprächen erkennen.',
    'Die Prämissen einer Medienaussage erkennen',
    "Eine Zuschreibung hinter 'Der ist halt so' als Deutung statt Tatsache erkennen",
    'Annahmen einer Hybrid-Root-Cause-Analyse von Messbefunden unterscheiden',
    'In einem U15-Taktikbericht Beobachtungen zu Pressingfehlern von Spielerzuschreibungen trennen',
  ),
  outline(
    's4-present',
    'Adressatengerecht präsentieren',
    3,
    'Komplexität reduzieren, ohne entscheidende Einschränkungen zu verlieren.',
    'Einen Fachbeitrag für Laien halten',
    'Ein Vorhaben verständlich vorstellen; regionale Wörter bei Bedarf in Standardsprache erklären',
    'Ein EV-Sicherheitskonzept mit FMEA-Bezug für fachfremde Beteiligte präsentieren',
    'U12-Spielern und Eltern das Trainingsziel Freilaufen mit konkreten Spielszenen erläutern',
  ),
  outline(
    's4-ethics',
    'Dilemmata erörtern',
    5,
    'Wertekonflikte aus mehreren Perspektiven zusammenführen und begrenzte Entscheidungen begründen.',
    'Datenschutz und Informationsbedarf abwägen',
    "Teilhabe und Aufwand diskutieren, ohne 'Das war schon immer so' als Begründung zu übernehmen",
    'Safety-Nachweise und Lieferdruck bei einer EV-Steuergerätefreigabe abwägen',
    'U15-Leistungsziele, faire Spielzeit und Schutz persönlicher Spielerdaten abwägen',
  ),
  outline(
    's5-spontaneous',
    'Spontan zusammenhängend reagieren',
    4,
    'Auf unerwartete Gesprächsbeiträge kohärent reagieren und sich hörergerecht reparieren.',
    'Eine unerwartete Nachfrage beantworten',
    "Im freien Gespräch 'eben' und 'halt' nach Funktion und Ton einordnen und respektvoll reagieren",
    'Auf eine unerwartete Frage zur ECU/TCU-Drehmomentkoordination im Automotive-Review reagieren',
    'Auf eine unerwartete Elternfrage zur U13-Taktik reagieren, ohne Spieler öffentlich zu kritisieren',
  ),
  outline(
    's5-nuance',
    'Bedeutungsnuancen verstehen',
    2,
    'Abstufungen, Modalpartikeln und indirekte Bewertungen im Kontext zuverlässig verstehen.',
    'Eine vorsichtig formulierte Einschätzung verstehen',
    "Schwäbisches 'net schlecht' und 'passt scho' im Kontext verstehen; keine pauschale Lob- oder Zusageregel annehmen",
    "'Möglich', 'plausibel' und 'nachgewiesen' in einer Automotive-Safety-Bewertung unterscheiden",
    'Indirekte Elternkritik an U11-Trainingsbelastung verstehen und offene Punkte erkennen',
  ),
  outline(
    's5-facilitate',
    'Mehrparteiengespräche moderieren',
    5,
    'Beiträge aus verschiedenen Rollen verbinden und leise Stimmen aktiv einbeziehen.',
    'Ein Bürgergespräch moderieren',
    'Eine schwäbische Vereinssitzung moderieren, regionale Wendungen bei Bedarf erklären und niemanden zum Dialekt drängen',
    'Ein interdisziplinäres EV-FMEA-Review mit ECU-, TCU- und Safety-Team moderieren',
    'Ein U15-Saisongespräch mit Jugendspielern, Eltern und Trainerteam moderieren',
  ),
  outline(
    's5-revise',
    'Texte stilistisch überarbeiten',
    3,
    'Kohärenz, Präzision und Register anhand von Leserbedürfnissen verbessern.',
    'Einen Kommentar redigieren',
    "Eine Einladung inklusiv überarbeiten und ein drängendes 'Du kommst doch' durch eine offene Frage ersetzen",
    'Eine Automotive-Dokumentation zur ECU/TCU-Schnittstelle präzisieren',
    'Eine U13-Trainingsanleitung zu Passdreieck und Rollenwechsel für neue Co-Trainer überarbeiten',
  ),
  outline(
    's6-general',
    'Unvorbereiteter allgemeiner Transfer',
    6,
    'Neue allgemeine Aufgaben selbstständig bearbeiten und die eigene Lösung anhand klarer Kriterien prüfen.',
    'Ein neues gesellschaftliches Thema erörtern',
    'Eine unbekannte Gruppensituation klären und unklare umgangssprachliche Zusagen rückversichern',
    'Ein unbekanntes EV-Mobilitätsthema für Laien vermitteln, ohne Fachwissen mit Sprachleistung gleichzusetzen',
    'Eine unbekannte allgemeine Elternfrage zum U11-Vereinsalltag verständlich beantworten',
  ),
  outline(
    's6-specialist',
    'Unvorbereiteter fachlicher Transfer',
    6,
    'In einem gewählten Fachkontext Verstehen und Produktion in einer anspruchsvollen Übung verbinden.',
    'Neue fachliche Informationen für Betroffene vermitteln',
    'Ein unbekanntes Ehrenamtsprojekt abstimmen und Zustimmung nicht aus Höflichkeitsfloskeln ableiten',
    'Ein unbekanntes Automotive-Review zu Hybrid-Fahrdynamik, ECU/TCU und FMEA durchführen',
    'Eine unbekannte U15-Taktikaufgabe erklären und ein anschließendes Elterngespräch führen',
  ),
  outline(
    's6-retention',
    'Langfristige Verfügbarkeit nachweisen',
    6,
    'Sprachliche Mittel nach einer Pause ohne Vorlage in neuen Aufgaben abrufen.',
    'Ein neues Problem nach einer Lernpause erklären',
    'Ein neues Konfliktgespräch führen und Modalpartikeln ohne auswendig gelernte Schablonen einsetzen',
    'Eine neue EV-Root-Cause-Hypothese nach einer Pause verständlich und vorsichtig erläutern',
    'Nach einer Pause eine neue U12-Fußballübung anleiten und einer Spielerin konkretes Feedback geben',
  ),
  outline(
    's6-checkpoint',
    'Übungsprofil auswerten und weiterlernen',
    6,
    'Ein fiktives Beispielprofil auswerten und daraus begrenzte, überprüfbare Übungsziele ableiten.',
    'Ein fiktives Beispielprofil zu einem allgemeinen Gespräch auswerten',
    'Direktheit und Dialektverständnis anhand fiktiver Gesprächsbelege reflektieren',
    'Ein fiktives Beispielprofil zu einem Automotive-Safety-Review auswerten',
    'Ein fiktives Beispielprofil zu Trainingsansage und Elterngespräch auswerten',
  ),
];

export function mergeRoadmapUnits(
  authoredUnits: CurriculumUnit[],
  outlineUnits: CurriculumUnit[],
): CurriculumUnit[] {
  const authoredIds = new Set(authoredUnits.map((u) => u.id));
  return [...authoredUnits, ...outlineUnits.filter((u) => !authoredIds.has(u.id))];
}

export const curriculumUnits: CurriculumUnit[] = mergeRoadmapUnits(
  [...unitsPartA, ...unitsPartB, ...unitsPartC, ...unitsPartD],
  roadmapUnits,
);

/** Optional goal-setting and evidence-literacy lessons, outside the mastery sequence. */
export const orientationUnits: CurriculumUnit[] = curriculumUnits.filter((u) => u.stage === 0);

/** Sequenced learning programme. The mastery and retrieval engines use this set only. */
export const coreUnits: CurriculumUnit[] = curriculumUnits
  .filter((u) => u.stage >= 1 && u.stage <= 6)
  .sort((a, b) => {
    if (a.stage !== b.stage) return a.stage - b.stage;
    const aSpecial = a.id === 's0-repair' || a.id === 's0-retrieve' ? 1 : 0;
    const bSpecial = b.id === 's0-repair' || b.id === 's0-retrieve' ? 1 : 0;
    return aSpecial - bSpecial;
  });
