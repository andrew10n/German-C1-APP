import type { AssessmentTask, AssessmentPart, ContextTrack } from '../types';

export const REVIEW_REMINDER =
  'Hinweis zur Bewertung: Die Wertung muss durch eine qualifizierte Person mit der 0–4-Rubrik ' +
  'der App erfolgen (alle zehn Dimensionen, mindestens 3 in jeder Dimension, keine Verrechnung ' +
  'starker mit schwachen Dimensionen). Selbsteinschätzung und automatische Quizze genügen nicht. ' +
  'Das Ergebnis ist kein offizieller Nachweis nach dem GER/CEFR und keine Zertifizierung.';

export const GRADUATION_MINUTES = [10, 15, 30, 8, 12, 15] as const;

function makeReviewerChecklist(params: {
  verstehen: string;
  register: string;
  spektrum: string;
  interaktion: string;
  mediation: string;
  extra: string;
}): string[] {
  return [
    'Verstehen: ' + params.verstehen,
    'Akkuratheit und Register: Kasus, Verbstellung, Konjunktiv und Satzbau bleiben in Schreiben und ' +
      'Sprechen weitgehend korrekt; Fehler stören das Verständnis nicht und werden bei Rückfrage selbst ' +
      'korrigiert. Register: ' +
      params.register,
    'Spektrum und Natürlichkeit/Kollokationen: ' + params.spektrum,
    'Kohärenz und Flüssigkeit: Der Text ist erkennbar gegliedert, nutzt unterschiedliche Konnektoren und ' +
      'hält die Wortzahl ein; im freien Sprechen gibt es längere Passagen ohne auffälliges Suchen, ' +
      'Abbrüche oder Rückgriff auf Notizen.',
    'Interaktion und Pragmatik: ' + params.interaktion,
    'Mediation: ' + params.mediation,
    params.extra,
    REVIEW_REMINDER,
  ];
}

function makeAssessment(params: {
  id: string;
  stream: string;
  context: ContextTrack;
  title: string;
  reading: string;
  listening: string;
  hoeren: string;
  lesen: string;
  schreiben: string;
  sprechen: string;
  interaktion: string;
  mediation: string;
  checklist: string[];
  minutes?: readonly number[];
}): AssessmentTask {
  const m = params.minutes ?? GRADUATION_MINUTES;
  const parts: AssessmentPart[] = [
    { skill: 'Hören', prompt: params.hoeren, minutes: m[0] },
    { skill: 'Lesen', prompt: params.lesen, minutes: m[1] },
    { skill: 'Schreiben', prompt: params.schreiben, minutes: m[2] },
    { skill: 'Sprechen', prompt: params.sprechen, minutes: m[3] },
    { skill: 'Interaktion', prompt: params.interaktion, minutes: m[4] },
    { skill: 'Mediation', prompt: params.mediation, minutes: m[5] },
  ];
  return {
    taskId: params.id,
    stream: params.stream,
    context: params.context,
    title: params.title,
    sourceReading: params.reading,
    sourceListening: params.listening,
    parts,
    reviewerChecklist: params.checklist,
  };
}

const generalGraduation: AssessmentTask[] = [
  makeAssessment({
    id: 'unseen-general:g1',
    stream: 'general',
    context: 'GENERAL',
    title: 'Vier-Tage-Woche in der Verwaltung: Kommentar und Interview',
    reading: `Seit dem Frühjahr testet die Stadtverwaltung von Lindenau in zwei Ämtern eine verkürzte Arbeitswoche: Die Beschäftigten arbeiten an vier Tagen, jeweils etwas länger, und behalten ihr Gehalt. Die Bilanz nach neun Monaten fällt, wie so oft bei solchen Pilotprojekten, uneinheitlich aus. Die Personalleitung spricht von einer „spürbaren Entlastung“ und verweist auf weniger kurzfristige Krankmeldungen im Bürgerbüro. Zugleich räumt sie ein, dass sich daraus allein noch keine Ursache ableiten lasse: Im selben Zeitraum wurde auch ein neues Terminsystem eingeführt, das die Wartezeiten am Schalter verändert hat.

Kritiker aus dem Gemeinderat halten dagegen, ein Versuch in nur zwei Ämtern sage wenig über die gesamte Verwaltung aus. Ein Bauamt, das Genehmigungen bearbeitet, funktioniere anders als ein Bürgerbüro, in dem die Öffnungszeiten ohnehin feststehen. Außerdem sei zu klären, wer am freien Tag erreichbar ist, wenn dringende Anfragen eintreffen. Die Personalvertretung wiederum betont, dass längere Arbeitstage nicht für alle gleich gut zu bewältigen seien, etwa für Alleinerziehende, deren Betreuungszeiten sich nicht beliebig verschieben ließen.

Bemerkenswert ist, dass die Beteiligten in einem Punkt übereinstimmen: Entscheidend sei nicht die Zahl der Tage, sondern die Frage, wie Arbeit organisiert wird. Wo Besprechungen gestrafft und Zuständigkeiten geklärt wurden, habe der Versuch funktioniert; wo lediglich dieselbe Arbeit in weniger Tage gepresst wurde, sei die Stimmung gekippt. Dieser Befund relativiert die gesamte Debatte. Wer die Vier-Tage-Woche als Wundermittel bewirbt, verspricht ebenso zu viel wie jemand, der sie als bloßen Luxus abtut.

Ob das Modell auf weitere Ämter ausgeweitet wird, soll nach einer zweiten Auswertung im kommenden Jahr entschieden werden. Bis dahin bleibt offen, ob hier ein tragfähiges Arbeitszeitmodell entsteht oder nur ein Experiment, das vor allem dort glückt, wo ohnehin schon gut organisiert wurde. Wer aus den bisherigen Erfahrungen vorschnell eine Regel für alle ableitet, übersieht, wie unterschiedlich Arbeit in einer Verwaltung aussieht.`,
    listening: `Moderatorin: Frau Brandl, in Ihrem Haus arbeiten manche Ämter jetzt vier Tage pro Woche. Hand aufs Herz: Ist das nicht vor allem ein Imagegewinn im Wettbewerb um Fachkräfte?
Brandl: Das spielt natürlich eine Rolle, das will ich gar nicht bestreiten. Aber wir hätten uns den Aufwand sparen können, wenn es nur um die Außenwirkung ginge. Uns interessiert, ob die Arbeit dadurch besser läuft.
Moderatorin: Und, läuft sie besser?
Brandl: Teilweise. Im Bürgerbüro sind die kurzfristigen Ausfälle zurückgegangen, aber ich würde mich hüten, das allein der Arbeitszeit zuzuschreiben. Wir haben gleichzeitig das Terminsystem umgestellt, und das verändert den Alltag am Schalter ganz erheblich.
Moderatorin: Und wer geht ans Telefon, wenn jemand am freien Tag anruft?
Brandl: Wir arbeiten mit versetzten freien Tagen. Das heißt, von Montag bis Freitag ist immer jemand da, nur eben nicht immer dieselben Personen. Das klingt einfacher, als es ist, denn die Vertretungsregeln müssen dann wirklich funktionieren, sonst bleibt am Ende alles an denselben zwei Leuten hängen.
Moderatorin: Der Personalrat sagt, für Alleinerziehende sei das schwierig.
Brandl: Da haben die Kolleginnen und Kollegen recht. Deshalb ist die Teilnahme freiwillig, und wer die längeren Tage nicht schafft, behält seine bisherige Regelung. Eine Pflicht haben wir bewusst nicht eingeführt.
Moderatorin: Gibt es Bereiche, in denen es gar nicht geht?
Brandl: Das Bauamt ist bewusst nicht dabei, weil Fristen und Termine mit Bauherren sich nicht verschieben lassen. Dort denken wir eher über eine flexible Kernzeit nach, aber das ist noch kein Beschluss.
Moderatorin: Also doch kein Patentrezept?
Brandl: Auf keinen Fall. Wenn jemand behauptet, die Vier-Tage-Woche löse alle Probleme, würde ich widersprechen. Sie bringt nur dann etwas, wenn man gleichzeitig Besprechungen kürzt und Zuständigkeiten klärt. Sonst hat man dieselbe Arbeit in weniger Tagen und gereizte Leute.
Moderatorin: Wann wird entschieden, wie es weitergeht?
Brandl: Nach der nächsten Auswertung. Ich rechne frühestens im Herbst damit, dass der Gemeinderat darüber berät, und bis dahin sammeln wir weiter Erfahrungen, auch die kritischen.`,
    hoeren:
      'Hören (zweimal, Notizen erlaubt). Beantworten Sie in ganzen Sätzen: (1) Warum bestreitet Frau Brandl den Imageaspekt nicht, hält ihn aber nicht für ausschlaggebend? (2) Weshalb zögert sie, den Rückgang der Ausfälle allein der Arbeitszeit zuzuschreiben? (3) Wie wird die Erreichbarkeit organisiert, und welche Schwierigkeit räumt sie dabei ein? (4) Wie steht sie zur Aussage, die Vier-Tage-Woche sei ein Patentrezept? Begründen Sie in eigenen Worten.',
    lesen:
      'Lesen Sie den Kommentar gründlich und beantworten Sie schriftlich oder mündlich: (1) Welche Haltung nimmt der Autor ein, und woran erkennen Sie sie, obwohl er sie nie direkt ausspricht? (2) Erklären Sie den Unterschied zwischen Zusammenhang und Ursache anhand des ersten Absatzes. (3) Was meinen „die Stimmung ist gekippt“ und „Wundermittel“ hier? (4) Nennen Sie zwei inhaltliche Übereinstimmungen und einen Unterschied zwischen Kommentar und Interview.',
    schreiben:
      'Schreiben Sie einen Leserbrief von 230–270 Wörtern an die Lokalzeitung. Beziehen Sie Stellung zur Frage, ob die Verwaltung die Vier-Tage-Woche ausweiten sollte. Nennen Sie Ihre Position, zwei Argumente, ein fair dargestelltes Gegenargument und eine Bedingung, unter der Sie Ihre Meinung ändern würden. Register: formell, sachlich, nicht belehrend.',
    sprechen:
      'Spontan, ohne schriftliche Vorbereitung (höchstens 60 Sekunden Gedankensammeln, keine Notizen beim Sprechen): Sprechen Sie etwa drei Minuten zu der Frage „Soll jeder Arbeitgeber eine Vier-Tage-Woche anbieten?“ Gewichten Sie Argumente, geben Sie ein Beispiel aus eigener Erfahrung und sagen Sie klar, was Sie nicht beurteilen können.',
    interaktion:
      'Die prüfende Person spielt ein Mitglied des Gemeinderats. Geplante Wendungen: (1) Sie unterbricht nach etwa 30 Sekunden mit „Das ist doch Augenwischerei!“. (2) Sie unterstellt, Sie hätten eine Pflicht für alle gefordert (Missverständnis, das Sie richtigstellen müssen). (3) Sie bringt ein neues Gegenargument: „Und wer zahlt die Notfalleinsätze am freien Tag?“. (4) Sie verlangt eine verbindliche Zusage, die Sie sachlich ablehnen müssen. Dauer ca. 10–12 Minuten.',
    mediation:
      'Fassen Sie mündlich in 2–3 Minuten (oder schriftlich in etwa 120 Wörtern) für die Belegschaft eines privaten Handwerksbetriebs zusammen, was Kommentar und Interview gemeinsam zeigen. Trennen Sie Befund, Meinung und offene Frage und erklären Sie Begriffe wie Personalrat und Kernzeit, ohne die Quellen zu zitieren.',
    checklist: makeReviewerChecklist({
      verstehen:
        'Hören und Lesen werden mit Details belegt (Terminsystem als zweite Ursache, freiwillige Teilnahme, Bauamt bewusst ausgenommen); Haltung des Autors wird aus Wortwahl erschlossen, nicht nur zitiert.',
      register:
        'Der Leserbrief ist formell-sachlich; das Sprechen bleibt höflich-bestimmt, auch bei der Unterbrechung.',
      spektrum:
        'Passende Kollokationen wie „eine Bilanz ziehen“, „eine Ursache ableiten“, „Zuständigkeiten klären“, „einer Debatte etwas entgegenhalten“ statt Umschreibungen; Abwägungsmittel wie „zwar … jedoch“, „sofern“, „es sei denn“.',
      interaktion:
        'Unterbrechung wird aufgefangen, ohne den Faden zu verlieren; das Missverständnis wird ruhig mit eigenen Worten richtiggestellt; die Forderung nach einer Zusage wird begründet abgelehnt, ohne Beziehung zu beschädigen.',
      mediation:
        'Zusammenfassung ist für Laien verständlich, trennt Fakten und Meinung, erklärt Fachwörter ohne Beschönigung und fügt keine neuen Fakten hinzu.',
      extra:
        'Argumentation: Zusammenhang und Ursache werden im Schreiben und Sprechen sauber getrennt; die Änderungsbedingung im Leserbrief ist konkret und überprüfbar.',
    }),
  }),
  makeAssessment({
    id: 'unseen-general:g2',
    stream: 'general',
    context: 'GENERAL',
    title: 'Stadtbäume, Hitze und Verantwortung: Artikel und Vortrag',
    reading: `Wer an einem Hochsommertag durch eine Stadt geht, bemerkt den Unterschied sofort: In einer baumlosen Straße strahlen Asphalt und Fassaden die gespeicherte Wärme noch Stunden nach Sonnenuntergang ab, während es unter einer dichten Baumkrone spürbar angenehmer ist. Eine Arbeitsgruppe mehrerer Hochschulen hat deshalb in der Mittelstadt Rotenau über zwei Sommer hinweg die Temperaturen an Straßenzügen mit unterschiedlichem Baumbestand verglichen. Die Forschenden betonen, dass ihre Messungen keine allgemeingültigen Zahlen liefern, sondern ein Muster zeigen: Straßen mit großen, gesunden Kronen blieben tagsüber merklich kühler als vergleichbare Straßen ohne Bäume.

Dafür gibt es zwei Gründe. Zum einen spendet eine Krone Schatten, sodass sich Boden und Hauswände weniger aufheizen. Zum anderen verdunsten Bäume Wasser über ihre Blätter, was die Umgebungsluft zusätzlich kühlt. Beide Effekte hängen allerdings davon ab, ob der Baum genügend Wasser bekommt. Leidet er unter Trockenheit, schließt er seine Spaltöffnungen, verdunstet weniger und kühlt entsprechend schlechter. Die Studie zeigt außerdem, dass ausgerechnet dort, wo Bäume am dringendsten gebraucht würden, nämlich in dicht bebauten Vierteln mit viel versiegelter Fläche, der Wurzelraum am knappsten ist. Leitungen, Tiefgaragen und verdichtete Böden lassen den Wurzeln wenig Platz.

Aus diesen Befunden leiten die Autorinnen und Autoren keine einfache Forderung nach „mehr Bäumen“ ab. Sie plädieren stattdessen dafür, bei jeder Pflanzung die Wasserversorgung mitzuplanen. Als vielversprechend gilt das sogenannte Schwammstadt-Prinzip, bei dem Regenwasser nicht in die Kanalisation geleitet, sondern unter der Straße zwischengespeichert wird, damit Wurzeln es später nutzen können. Ob sich der Mehraufwand rechnet, lasse sich erst nach mehreren Jahren beurteilen, weil junge Bäume zunächst kaum Schatten werfen und in den ersten Jahren besonders viel Pflege brauchen.

Kritiker weisen darauf hin, dass Bäume allein die Hitzebelastung nicht lösen: Fassadenbegrünung, helle Beläge und Frischluftschneisen seien gleichwertige Bausteine. Die Autorinnen und Autoren widersprechen dem nicht. Sie halten Bäume lediglich für den Baustein, der zugleich Schatten, Kühlung und Aufenthaltsqualität bietet, und fordern, die verschiedenen Maßnahmen nicht gegeneinander auszuspielen.`,
    listening: `Guten Abend zusammen. Ich bin Tobias Eder vom städtischen Grünflächenamt und möchte Ihnen erklären, warum bei uns manche Bäume im Sommer von Wassersäcken umgeben sind und warum wir nicht einfach mehr pflanzen, obwohl alle danach rufen.

Zunächst einmal: Wir pflanzen durchaus. Aber ein Baum, den wir in eine enge Baumscheibe setzen, hat unter der Oberfläche oft nur wenige Kubikmeter Platz. Das reicht in den ersten Jahren, später nicht mehr. Deshalb bauen wir bei Neuanlagen inzwischen größere Wurzelräume, und zwar mit einem Substrat, das Wasser speichert und trotzdem Luft an die Wurzeln lässt. Das ist teurer, aber immer noch billiger als ein Baum, der nach acht Jahren eingeht.

Zweiter Punkt: die Artenwahl. Früher haben wir vor allem Linden und Ahorn gepflanzt. Die mögen es aber weder besonders heiß noch besonders trocken. Wir probieren deshalb Arten aus, die Hitze besser vertragen, etwa Zürgelbaum oder Hopfenbuche. Ich sage ganz offen: Wir wissen heute nicht genau, wie sich diese Bäume in vierzig Jahren verhalten. Deshalb mischen wir bewusst, statt auf eine einzige Art zu setzen. Wenn eine Krankheit kommt, trifft sie dann nicht die ganze Straße.

Dritter Punkt, und der betrifft Sie: das Gießen. In den ersten Jahren braucht ein junger Baum vor allem im Sommer Wasser, und unsere Kolonnen schaffen das nicht überall. Wenn Sie in Ihrer Straße eine Patenschaft übernehmen möchten, melden Sie sich bei uns. Bitte aber nicht mit dem Gartenschlauch kurz über die Baumscheibe sprühen. Das Wasser läuft dann ab, bevor es in die Tiefe kommt. Besser ist ein langsames, durchdringendes Gießen, dafür reichen ein paar Eimer pro Woche.

Und noch ein Wort zu den Kosten: Ob sich das alles lohnt, kann ich Ihnen nicht in Euro beziffern. Aber ein ausgewachsener Straßenbaum lässt sich nicht in zehn Jahren ersetzen. Wer heute spart, zahlt später mit heißen Sommern. Vielen Dank, ich beantworte gern Ihre Fragen.`,
    hoeren:
      'Hören (zweimal, Notizen erlaubt). Beantworten Sie in ganzen Sätzen: (1) Warum pflanzt das Grünflächenamt nicht einfach mehr Bäume? (2) Welche Unsicherheit räumt der Referent bei der Artenwahl ein, und wie begegnet das Amt ihr? (3) Was können Anwohner beitragen, was sollen sie unterlassen, und warum? (4) Wie ist der letzte Gedanke zu den Kosten gemeint? Erklären Sie ihn in eigenen Worten.',
    lesen:
      'Lesen Sie den Artikel gründlich: (1) Unterscheiden Sie, was gemessen wurde, was erklärt wird und was nur gefordert oder vermutet wird. (2) Warum ist der Wurzelraum gerade in dicht bebauten Vierteln ein Problem? (3) Erklären Sie das Schwammstadt-Prinzip so, dass jemand ohne Vorwissen es versteht. (4) Wie positionieren sich die Autorinnen und Autoren zur Kritik am Schluss? Belegen Sie am Text. (5) Wo ergänzen sich Artikel und Vortrag, wo setzt der Referent andere Schwerpunkte?',
    schreiben:
      'Schreiben Sie 230–270 Wörter: Als Anwohnerin oder Anwohner einer Straße ohne Bäume wenden Sie sich an das Grünflächenamt. Formulieren Sie den Wunsch nach einer Pflanzung, bieten Sie eine Gießpatenschaft an, stellen Sie zwei konkrete Fragen (Wurzelraum, Artenwahl) und beziehen Sie sich auf den Vortrag. Ton: höflich und bestimmt, ohne Vorwürfe.',
    sprechen:
      'Spontan (höchstens 60 Sekunden Gedankensammeln, keine Notizen beim Sprechen), etwa drei Minuten: Wie sollten Städte begrenzte Mittel zwischen Neupflanzungen, Pflege vorhandener Bäume und anderen Hitzemaßnahmen verteilen? Gewichten Sie, nennen Sie ein Beispiel aus Ihrer Erfahrung und kennzeichnen Sie, was Sie nicht wissen.',
    interaktion:
      'Die prüfende Person spielt ein Mitglied einer Bürgerinitiative. Geplante Wendungen: (1) Sie unterbricht mit „Pflege, Pflege – wir brauchen endlich Bäume!“. (2) Sie versteht Ihre Aussage zur Artenwahl falsch, als sei die Stadt unvorbereitet. (3) Sie wendet ein, Gießpatenschaften schöben Verantwortung auf Bürger ab. (4) Sie bittet Sie, einen gemeinsamen Vorschlag für die nächste Sitzung zu formulieren. Dauer ca. 10–12 Minuten.',
    mediation:
      'Ihr Nachbar Herr Bauer (Mitte 70, aufmerksam, ohne Fachwissen, Deutsch sicher) war nicht dabei. Erklären Sie ihm in 2–3 Minuten, warum an manchen Straßenbäumen Wassersäcke hängen und was er selbst tun kann. Erklären Sie „Baumscheibe“ und „Wurzelraum“ ohne Fachjargon und prüfen Sie zwischendurch sein Verständnis.',
    checklist: makeReviewerChecklist({
      verstehen:
        'Unterscheidet belegte Befunde, Erklärungen und Forderungen; erfasst die offen eingeräumte Unsicherheit bei der Artenwahl und die Begründung gegen das Sprühen mit dem Schlauch.',
      register:
        'Brief an das Amt: höflich-bestimmt, ohne Vorwurfston; Gespräch mit dem Nachbarn: verständlich und respektvoll, nicht herablassend.',
      spektrum:
        'Natürliche Kollokationen wie „Flächen versiegeln“, „Wasser speichern“, „Schatten spenden“, „eine Patenschaft übernehmen“, „etwas gegeneinander ausspielen“; Wechsel zwischen Fachbegriff und Paraphrase.',
      interaktion:
        'Die Unterbrechung wird nicht übergangen, sondern konstruktiv aufgegriffen; das Missverständnis zur Artenwahl wird geklärt; der gemeinsame Vorschlag wird tatsächlich ausgehandelt (Zugeständnis und eigene Bedingung).',
      mediation:
        'Fachbegriffe werden in Alltagssprache übertragen, ein Beispiel veranschaulicht, das Verständnis wird aktiv geprüft; keine Zahlen oder Fakten werden erfunden.',
      extra:
        'Unsicherheit wird sprachlich markiert („nach bisherigem Stand“, „das lässt sich noch nicht sagen“) statt als Gewissheit dargestellt.',
    }),
  }),
  makeAssessment({
    id: 'unseen-general:g3',
    stream: 'general',
    context: 'GENERAL',
    title: 'Neues Bewohnerparken: Behördenschreiben und Telefonat',
    reading: `Stadt Hohenbach – Amt für öffentliche Ordnung
Betreff: Neuregelung des Bewohnerparkens im Quartier Altstadt-Süd

Sehr geehrte Anwohnerinnen und Anwohner,

der Gemeinderat hat am 14. Oktober beschlossen, das Bewohnerparken im Quartier Altstadt-Süd zum 1. Januar neu zu ordnen. Anlass sind zahlreiche Beschwerden über Fremdparker und über Ausweise, die nicht mehr den tatsächlichen Wohnverhältnissen entsprechen. Wir bitten Sie, die folgenden Hinweise sorgfältig zu lesen, da sich für Sie Pflichten und Fristen ergeben.

Bisherige Bewohnerparkausweise verlieren zum 31. Dezember ihre Gültigkeit. Ein neuer Ausweis wird nur auf Antrag ausgestellt. Der Antrag kann online oder persönlich im Bürgerservice gestellt werden; bei persönlicher Vorsprache ist eine Terminvereinbarung empfehlenswert, aber nicht zwingend. Voraussetzung ist, dass Sie mit Hauptwohnsitz im Quartier gemeldet sind und auf Ihren Namen ein Fahrzeug zugelassen ist. Ist das Fahrzeug auf einen Arbeitgeber oder ein Leasingunternehmen zugelassen, genügt eine schriftliche Bestätigung über die dauerhafte Nutzung durch Sie. Ein Zweitwohnsitz begründet keinen Anspruch.

Die Gebühr richtet sich künftig nach der Länge des Fahrzeugs. Die genaue Gebührenordnung liegt diesem Schreiben bei. Für Haushalte mit Schwerbehindertenausweis sowie für Pflegedienste mit Einsatz im Quartier gelten besondere Regelungen, über die wir auf Anfrage gesondert informieren.

Anträge, die bis zum 30. November vollständig eingehen, werden bis zum Jahreswechsel bearbeitet. Bei später eingehenden Anträgen kann es zu Lücken kommen, für die keine Übergangsregelung vorgesehen ist. Während einer solchen Lücke besteht kein Anspruch darauf, dass Verwarnungen unterbleiben. Ein bloßer Antrag ersetzt den Ausweis nicht.

Gegen den Gebührenbescheid kann innerhalb eines Monats nach Bekanntgabe Widerspruch eingelegt werden. Der Gemeinderatsbeschluss selbst ist davon nicht berührt; Einwände gegen die Neuregelung als solche können Sie schriftlich an die Fraktionen des Gemeinderats richten.

Wir danken für Ihr Verständnis und bitten um fristgerechte Antragstellung.

Mit freundlichen Grüßen
Im Auftrag
Katrin Mayerhöfer, Amtsleitung`,
    listening: `Frau Keller: Bürgerservice Hohenbach, Sie sprechen mit Frau Keller, guten Tag.
Herr Demir: Guten Tag, Demir hier. Ich habe gestern diesen Brief zum Bewohnerparken bekommen und, ehrlich gesagt, blicke ich bei einer Sache nicht durch. Mein Auto ist ein Firmenwagen.
Frau Keller: Ja, das ist ein Klassiker. Wer ist denn als Halter eingetragen?
Herr Demir: Mein Arbeitgeber, die Firma Kühnle. Ich fahre den Wagen aber privat und dienstlich, und er steht jede Nacht vor meiner Haustür.
Frau Keller: Dann brauchen wir von der Firma eine schriftliche Bestätigung, dass Sie das Fahrzeug dauerhaft nutzen. Die Form ist frei, aber mit Unterschrift oder Firmenstempel, und das Kennzeichen muss draufstehen.
Herr Demir: Und wie lange dauert das? Ich bin nämlich ab nächster Woche auf Dienstreise, bis Ende November.
Frau Keller: Dann bräuchten Sie den Antrag eigentlich vor der Abreise, denn er muss bis zum 30. November vollständig vorliegen. Sie können ihn aber auch online stellen, den Nachweis laden Sie dort als Datei hoch.
Herr Demir: Und wenn die Bestätigung erst Anfang Dezember kommt?
Frau Keller: Dann kann es eine Lücke geben. Und da muss ich Ihnen ehrlich sagen: In der Lücke können Sie ein Knöllchen bekommen. Der Antrag allein schützt Sie nicht. Ich kann Ihnen da nichts versprechen.
Herr Demir: Aber ich habe ja alles Nötige getan, nur eben etwas später.
Frau Keller: Das verstehe ich gut. Trotzdem gilt die Regel für alle gleich, und ich darf keine Ausnahme zusagen. Was Sie tun können: Bitten Sie Ihre Firma, die Bestätigung vorab per Mail zu schicken, dann laden Sie sie von unterwegs hoch.
Herr Demir: Und die Gebühr hängt von der Länge ab? Ich habe das Auto ja nie ausgemessen.
Frau Keller: Die Länge steht in den Zulassungsdaten. Bei einem Firmenwagen sollten Sie sich also eine Kopie des Fahrzeugscheins von der Firma geben lassen.
Herr Demir: Und wenn ich mit der Gebühr nicht einverstanden bin?
Frau Keller: Dann können Sie gegen den Gebührenbescheid innerhalb eines Monats Widerspruch einlegen. Gegen den Beschluss selbst nicht, da müssten Sie sich an die Fraktionen wenden.
Herr Demir: Gut, dann rufe ich heute noch bei der Firma an. Vielen Dank.
Frau Keller: Gern geschehen. Und denken Sie an die Frist.`,
    hoeren:
      'Hören (zweimal, Notizen erlaubt). Beantworten Sie in ganzen Sätzen: (1) Was ist Herrn Demirs Problem genau, und welche Lösung nennt Frau Keller? (2) Warum kann sie ihm keine Zusage zu möglichen Verwarnungen geben? (3) Welche Handlungsschritte empfiehlt sie, und in welcher Reihenfolge? (4) Wie klingt Frau Keller in der Frage der Ausnahme – was ist ihr Ton, und woran hören Sie das?',
    lesen:
      'Lesen Sie das Schreiben gründlich: (1) Welche Pflichten und Fristen ergeben sich, und was passiert bei Fristversäumnis? (2) Erklären Sie, was „ein bloßer Antrag ersetzt den Ausweis nicht“ praktisch bedeutet. (3) Welche Formulierungen im Schreiben sind verbindlich, welche lassen Spielraum („genügt“, „empfehlenswert, aber nicht zwingend“, „auf Anfrage“)? (4) Was lässt sich aus dem Schreiben nicht beantworten, und wo würden Sie nachfragen? (5) Vergleichen Sie Schreiben und Telefonat: Welche Information des Telefonats steht nicht im Brief?',
    schreiben:
      'Schreiben Sie 220–260 Wörter: Eine E-Mail an den Bürgerservice. Sie sind Pflegedienstleiterin oder -leiter mit Einsätzen im Quartier und bitten um die im Schreiben erwähnte gesonderte Information. Schildern Sie Ihre Lage, stellen Sie drei präzise Fragen (Fristen, Nachweise, Gebühr), bitten Sie um eine verbindliche schriftliche Antwort und bleiben Sie auch bei Unzufriedenheit sachlich-höflich.',
    sprechen:
      'Spontan (höchstens 60 Sekunden Gedankensammeln), etwa drei Minuten: Sollten Städte Parkraum in Wohnvierteln stärker regulieren? Nehmen Sie Position ein, gehen Sie auf mindestens zwei Betroffene mit gegensätzlichen Interessen ein (z. B. Familien, Handwerk, Pflegedienste) und nennen Sie eine Lösung, die Härten abfedert.',
    interaktion:
      'Die prüfende Person spielt einen verärgerten Anwohner am Telefon (Sie sind Sachbearbeiterin oder Sachbearbeiter im Bürgerservice). Geplante Wendungen: (1) Er unterbricht Sie bei der Fristerklärung. (2) Er versteht „Widerspruch“ falsch und glaubt, er könne den Ratsbeschluss anfechten. (3) Er verlangt eine Ausnahme von der Frist. (4) Er droht mit einer Beschwerde bei der Bürgermeisterin. Bleiben Sie sachlich, erklären Sie klar und beenden Sie das Gespräch mit einer Vereinbarung.',
    mediation:
      'Ihre Nachbarin, Frau Okafor, spricht fließend Deutsch, aber nur wenig Verwaltungssprache, und hat das Schreiben bekommen. Erklären Sie ihr mündlich in 2–3 Minuten, was sie bis wann tun muss, was passiert, wenn sie die Frist verpasst, und was „Widerspruch“ und „Gebührenbescheid“ bedeuten. Verwenden Sie Alltagssprache.',
    checklist: makeReviewerChecklist({
      verstehen:
        'Fristen, Voraussetzungen und Rechtsfolgen werden korrekt erfasst; der Unterschied zwischen Antrag und Ausweis sowie zwischen Widerspruch gegen den Bescheid und Einwänden gegen den Beschluss wird erkannt; die implizite Haltung der Sachbearbeiterin wird gedeutet.',
      register:
        'E-Mail an die Behörde: formell mit Anrede und Schlussformel, sachliche Konnektoren; Telefonat mit dem Anwohner: höflich-professionell, nicht unterwürfig, nicht barsch.',
      spektrum:
        'Verwaltungstypische Kollokationen wie „einen Antrag stellen/einreichen“, „Widerspruch einlegen“, „eine Frist einhalten/versäumen“, „einen Anspruch begründen“; Wechsel zwischen Amtssprache und Erklärung in Alltagssprache.',
      interaktion:
        'Unterbrechung wird höflich zurückgewiesen und das Anliegen zu Ende erklärt; das Missverständnis zum Widerspruch wird ohne Besserwisserton geklärt; die Ablehnung der Ausnahme wird begründet; das Gespräch endet mit konkreter Vereinbarung.',
      mediation:
        'Rechtliche Begriffe werden vereinfacht, ohne falsch zu werden; Fristen und Folgen sind eindeutig; keine Zusagen werden erfunden, die das Schreiben nicht enthält.',
      extra:
        'Pragmatik der Absage: Eine Ausnahme wird verweigert, ohne die Kooperationsbereitschaft zu verlieren (z. B. „Das kann ich Ihnen nicht zusagen, aber was ich tun kann, ist …“).',
    }),
  }),
  makeAssessment({
    id: 'unseen-general:g4',
    stream: 'general',
    context: 'GENERAL',
    title: 'Bibliothek als dritter Ort: Bericht und Bürgerversammlung',
    reading: `In der Stadtbibliothek Neckarburg ist seit dem Umbau vieles anders. Wer das Haus betritt, steht nicht mehr vor einer Ausleihtheke, sondern in einem offenen Foyer mit Café, Sitzgruppen und einer Spielecke. Die Bücherregale wurden niedriger und rücken näher an die Wände, in der ersten Etage gibt es Arbeitsplätze, Gruppenräume und ein kleines Tonstudio, das man stundenweise reservieren kann. Die Bibliotheksleiterin Sabine Ott spricht von einem „dritten Ort“, einem Treffpunkt neben Zuhause und Arbeitsplatz, an dem man sich aufhalten darf, ohne etwas konsumieren zu müssen.

Nicht alle sind begeistert. Ein langjähriger Nutzer, der anonym bleiben möchte, bemängelt, dass es an Lesetagen kaum noch möglich sei, in Ruhe zu arbeiten. Die Geräuschkulisse im Foyer dringe bis in die erste Etage. Ott räumt das Problem ein; man habe die Akustik unterschätzt und lasse derzeit Schallschutzelemente prüfen. Zugleich verweist sie darauf, dass es weiterhin einen ausgewiesenen Stillbereich gebe, der allerdings kleiner sei als früher.

Auch der Bestand ist Gegenstand der Debatte. Weil die Fläche anders verteilt wurde, hat die Bibliothek einen Teil der Sachbücher ins Magazin verlagert. Sie sind weiterhin bestellbar, aber nicht mehr frei zugänglich. Kritiker sehen darin einen Verlust des zufälligen Entdeckens. Die Bibliotheksleitung hält dagegen, dass gerade die verlagerten Bestände zuletzt kaum ausgeliehen worden seien, während der Bedarf an Räumen und Veranstaltungen stetig wachse. Wie belastbar diese Einschätzung ist, lässt sich von außen schwer beurteilen, denn die Ausleihzahlen wurden bislang nicht veröffentlicht.

Unstrittig ist, dass das Haus nun andere Menschen anzieht. Jugendliche lernen in Gruppen, Seniorinnen treffen sich zum Handarbeiten, ein Verein für Sprachtandems nutzt abends einen Raum. Die Frage, die der Gemeinderat im nächsten Jahr zu beantworten hat, lautet daher weniger, ob die Bibliothek moderner geworden ist, sondern welche ihrer Aufgaben künftig Vorrang haben soll: das Bewahren und Zugänglichmachen von Wissen oder das Schaffen eines Ortes der Begegnung. Beides gleichzeitig zu leisten, kostet Platz, Personal und Geld, und alle drei sind knapp.`,
    listening: `Vorsitzende: Wir kommen zu Tagesordnungspunkt drei, der Stadtbibliothek. Herr Albrecht, Sie hatten sich zu Wort gemeldet.
Albrecht: Danke. Ich bin seit über vierzig Jahren Mitglied der Bibliothek, und ich will nicht pauschal gegen den Umbau reden. Das Café ist schön, die Kinderecke auch. Aber ich muss sagen: Ich komme seit dem Umbau seltener, weil ich die Bücher, die mich interessieren, nicht mehr in die Hand nehmen kann. Früher bin ich vor einem Regal gestanden und habe etwas entdeckt, wonach ich gar nicht gesucht hatte. Heute muss ich im Katalog suchen und bestellen, und wer nicht weiß, was er sucht, findet auch nichts.
Zwischenruf: Dann müssen Sie halt ins Internet!
Albrecht: Mit Verlaub, ich habe nichts gegen das Internet, aber eine Bibliothek soll doch etwas anderes bieten als eine Suchmaschine. Und noch etwas: Man sagt uns, die Sachbücher im Magazin würden kaum ausgeliehen. Ja, natürlich! Wer sie nicht sieht, leiht sie nicht aus. Das ist doch ein Zirkelschluss.
Vorsitzende: Herr Albrecht, bitte kommen Sie zum Schluss.
Albrecht: Gleich. Mein Vorschlag lautet: Man sollte ein Jahr lang einen Teil der Bestände zurückholen und dann nachzählen, was tatsächlich ausgeliehen wird. Und die Ausleihzahlen sollten veröffentlicht werden, damit wir nicht über Eindrücke diskutieren. Ich gebe zu, dass ich die Zahlen selbst nicht kenne. Aber genau darum geht es ja.
Vorsitzende: Danke. Frau Ott, möchten Sie direkt antworten?
Ott: Kurz. Wir veröffentlichen die Zahlen gern, das ist kein Problem. Einen Probebetrieb halte ich für überlegenswert, aber er kostet Platz, den wir für die Gruppenräume brauchen. Ich schlage vor, dass wir das im Kulturausschuss besprechen. Und ich möchte betonen: Wir wollten niemandem etwas wegnehmen. Wir haben versucht, ein Haus für mehr Menschen zu öffnen, und dabei haben wir an manchen Stellen vermutlich zu viel auf einmal verändert.
Vorsitzende: Gut, dann nehmen wir das als Prüfauftrag mit und kommen im Frühjahr darauf zurück.`,
    hoeren:
      'Hören (zweimal, Notizen erlaubt). Beantworten Sie in ganzen Sätzen: (1) Was kritisiert Herr Albrecht, und was lobt er ausdrücklich? Warum ist dieses Lob für seine Glaubwürdigkeit wichtig? (2) Was meint er mit „Zirkelschluss“? (3) Welche konkreten Vorschläge macht er, und wie reagiert Frau Ott darauf? (4) Wie bewerten Sie die Haltung von Frau Ott am Schluss – woran erkennen Sie das?',
    lesen:
      'Lesen Sie den Bericht gründlich: (1) Welche drei Konfliktpunkte nennt der Text? (2) Welche Einschätzung der Bibliotheksleitung bezweifelt der Autor indirekt, und woran erkennen Sie das? (3) Erklären Sie die letzte Frage des Berichts in eigenen Worten. (4) Vergleichen Sie Bericht und Bürgerversammlung: Welche Argumente tauchen in beiden auf, welche nur in einem?',
    schreiben:
      'Schreiben Sie 230–270 Wörter: Sie sind Mitglied des Kulturausschusses und verfassen eine Beschlussvorlage-Empfehlung für den Prüfauftrag. Stellen Sie die Ausgangslage dar (Fakten getrennt von Behauptungen), nennen Sie zwei Handlungsoptionen mit je einem Vor- und Nachteil und empfehlen Sie eine Option mit überprüfbaren Kriterien. Stil: formell, neutral, klar gegliedert.',
    sprechen:
      'Spontan (höchstens 60 Sekunden Gedankensammeln), etwa drei Minuten: Welche Aufgaben sollten öffentliche Bibliotheken künftig vorrangig erfüllen? Setzen Sie Prioritäten, nennen Sie ein Beispiel aus eigener Erfahrung und gehen Sie auf mindestens eine Gruppe ein, die bei Ihrer Gewichtung verliert.',
    interaktion:
      'Die prüfende Person spielt Herrn Albrecht, Sie die Bibliotheksleitung. Geplante Wendungen: (1) Er unterbricht Sie mit „Das sind doch nur Ausreden!“. (2) Er behauptet, Sie hätten gesagt, Bücher seien überflüssig (Missverständnis). (3) Er stellt ein überzeugendes Gegenargument zur Auswertung der Ausleihzahlen. (4) Er fragt, ob Sie wirklich bereit seien, den Probebetrieb zu unterstützen. Reagieren Sie ehrlich, ohne Zusagen zu machen, die Sie nicht einhalten können.',
    mediation:
      'Fassen Sie für eine ältere Nachbarin, die selten die Bibliothek besucht und den Umbau nur vom Hörensagen kennt, in 2–3 Minuten zusammen, worum der Streit geht und welche Positionen es gibt. Bleiben Sie neutral, erklären Sie „dritter Ort“ und „Magazin“ und sagen Sie, was noch ungeklärt ist.',
    checklist: makeReviewerChecklist({
      verstehen:
        'Konfliktpunkte (Lärm, Stillbereich, verlagerte Sachbücher) und der unveröffentlichte Beleg werden erfasst; die Kritik am Zirkelschluss wird erklärt; Frau Otts selbstkritischer Schluss wird als Haltung gedeutet.',
      register:
        'Beschlussvorlage: neutral-formell, Passiv und Nominalstil sparsam und korrekt; Gespräch mit Herrn Albrecht: höflich-professionell, auch bei „Ausreden“.',
      spektrum:
        'Kollokationen wie „einen Bestand verlagern“, „einen Prüfauftrag erteilen“, „etwas zur Diskussion stellen“, „Prioritäten setzen“, „an Glaubwürdigkeit gewinnen/verlieren“; präzise Abwägungssprache.',
      interaktion:
        'Die Unterstellung („Ausreden“) wird ruhig aufgenommen; das Missverständnis wird geklärt; auf das Gegenargument wird inhaltlich eingegangen statt ausgewichen; die Antwort auf die Gretchenfrage ist ehrlich und überprüfbar.',
      mediation:
        'Der Streit wird neutral und für Laien verständlich wiedergegeben; Positionen werden fair zugeordnet; offene Punkte sind als offen markiert.',
      extra:
        'Trennung von Tatsachen und Behauptungen ist im Text und im Gespräch erkennbar („laut Bibliotheksleitung“, „belegt ist bisher nur …“).',
    }),
  }),
];

const socialGraduation: AssessmentTask[] = [
  makeAssessment({
    id: 'unseen-social:s1',
    stream: 'social',
    context: 'SOCIAL',
    title: 'WG-Chat und Küchengespräch: Geburtstag mit Hindernissen',
    reading: `Gruppenchat „WG Sonnenstraße 14“

Jule, 18:02: Leute, Mamas Geburtstag am Samstag, ich brauch die Wohnung ab 15 Uhr. Geht das für alle?
Marek, 18:05: Ab 15 Uhr? Ich wollte am Samstag eigentlich mal wieder Musik machen. Aber joa, wird schon irgendwie.
Tilda, 18:06: „Wird schon irgendwie“ heißt bei dir ja immer: „Ich bin angefressen, sag's aber nicht“ 😄
Marek, 18:07: Gar nicht! Mir ist's wirklich recht. Ich bräuchte nur zwei Stunden vorher das Wohnzimmer.
Jule, 18:09: Kein Ding, ich saug vorher durch.
Cem, 18:15: Ich kann um halb drei mit Kuchen anrücken, hab sowieso Backdienst bei Oma. Aber wie viele Leute sind das denn? Letztes Mal hieß es „ein paar“, und dann standen vierzehn in der Küche.
Jule, 18:16: Haha, stimmt. Diesmal wirklich nur acht. Oder neun. Zehn maximal.
Tilda, 18:16: Das ist die Definition von „wirklich nur“ 😂
Cem, 18:18: Ich rechne mit zwölf und backe zwei Kuchen.
Marek, 18:20: Ich schlage vor: Wer mehr als zehn einlädt, spült. Das ist nur fair.
Jule, 18:21: Deal. Aber nur, wenn du nicht wieder um Mitternacht „nur kurz“ die Gitarre rausholst, wenn alle gehen wollen.
Marek, 18:22: Das war ein einziges Mal!
Tilda, 18:22: Dreimal.
Marek, 18:23: Okay, DREI Mal. Aber alle drei Male war's schön.
Tilda, 18:30: Mal ernsthaft: Ich bin Samstag eh bei meinem Bruder und komm erst spät zurück. Wenn ihr mich braucht, schreibt mir. Wenn nicht, tut so, als hätte ich nie existiert 😅
Jule, 18:31: Hast du eigentlich noch meine große Schüssel mit dem Deckel?
Tilda, 18:33: Welche Schüssel? 🙈
Jule, 18:33: Tilda.
Tilda, 18:34: Gut, sie steht im Flurschrank, hinter dem Wok. Ich hab sie nicht versteckt, ich hab sie in Sicherheit gebracht.
Jule, 18:35: Ich nehm das als Entschuldigung. Danke euch, ihr seid die Besten. Und Marek: Kein Wort zum Wohnzimmer!
Marek, 18:36: Ich sag nix. Ich denk nur sehr laut.`,
    listening: `Cem: Ey, riecht ihr das? Hat jemand was anbrennen lassen?
Jule: Das bin ich, ich teste das Rezept für Samstag. Probier mal, aber sei ehrlich.
Cem: Hm. Also … ja. Schmeckt … mutig.
Jule: Mutig! Das heißt, es schmeckt nicht.
Cem: Es schmeckt, äh, nach einer Idee, die noch Zeit braucht. Aber die Füllung ist super.
Marek: Moin, was gibt's denn hier? Ach, Kuchen! Gib mal her. Hm. Weißt du was, Jule, ich hab ja gesagt, ich sag nix zum Boden. Aber das ist der Boden.
Jule: Haha, sehr witzig. Das ist der Boden vom Kuchen, nicht der vom Wohnzimmer.
Marek: Eben. Beide ein bisschen klebrig.
Jule: Raus hier! Nein, Moment, bleib. Ich brauch eure Hilfe, ehrlich. Meine Mama kommt mit ihrer Schwester, und die Tante hat ja immer was zu meckern.
Cem: Die Tante, die letztes Mal gefragt hat, ob der Salat „bio“ ist, und dann gesagt hat, er schmecke „irgendwie nach gar nichts“?
Jule: Genau die. Und diesmal soll alles perfekt sein, weil Mama sich so freut, dass sie mal in meine Wohnung darf.
Marek: Dann nimm doch den Kuchen von Cems Oma und sag, du hättest ihn selber gebacken.
Jule: Das kann ich nicht bringen. Ich hab's ja nicht mal geschafft, den Backofen richtig vorzuheizen.
Cem: Hör mal, im Ernst: Ich frag Oma, ob sie dir das Rezept gibt. Dann backen wir den gemeinsam, du und ich, Freitagabend. Und der gehört dann trotzdem dir.
Jule: Echt? Das würdest du machen? Nach dem „mutig“?
Cem: Ich hab ja nicht gesagt, dass du es nicht kannst. Ich hab gesagt, es braucht Zeit. Und Oma braucht übrigens auch Zeit. Sie liest mir das Rezept nie vor, sie sagt immer „ein bisschen Mehl, bis es sich richtig anfühlt“.
Marek: Das ist keine Mengenangabe, das ist Philosophie.
Jule: Dann nehm ich Philosophie. Hauptsache, die Tante isst.
Cem: Und wenn sie meckert, sagst du einfach: „Das ist ein Familienrezept.“
Jule: Ist es dann ja auch, irgendwie.`,
    hoeren:
      'Hören (zweimal, Notizen erlaubt). Beantworten Sie in ganzen Sätzen: (1) Was meint Cem mit „mutig“ und mit „einer Idee, die noch Zeit braucht“? Woran erkennen Sie, dass er höflich bleiben will? (2) Welche ironischen Bemerkungen machen Marek und Cem, und wie reagiert Jule jeweils? (3) Was ist Jules eigentliches Problem, und wie wird es gelöst? (4) Wie ist das Verhältnis der drei zueinander? Belegen Sie das an Sprechweise und Wortwahl.',
    lesen:
      'Lesen Sie den Gruppenchat: (1) Wie entwickelt sich die Stimmung, und woran erkennen Sie, wer wann leicht genervt oder amüsiert ist? (2) Was bedeuten „wird schon irgendwie“, „wirklich nur acht“, „in Sicherheit gebracht“ und „ich denk nur sehr laut“ – wörtlich und im Kontext? (3) Welche Abmachungen gelten ernsthaft, welche sind nur scherzhaft? (4) Mit welchen sprachlichen Mitteln (Partikeln, Auslassungen, Kurzsätze) entsteht der Ton? Wie würde dieselbe Information in einer Mail an die Hausverwaltung klingen?',
    schreiben:
      'Schreiben Sie zwei Texte, zusammen 240–280 Wörter: (a) eine lockere Chatnachricht (ca. 100–120 Wörter) an die WG, in der Sie einen kurzfristigen Besuch am Samstag ankündigen, humorvoll Bedingungen nennen und um Zustimmung bitten, ohne zu drängen; (b) eine höfliche E-Mail (ca. 140–160 Wörter) an die Hausverwaltung, in der Sie wegen der Feier mit etwa zwölf Gästen um Verständnis bitten, die Ruhezeiten ansprechen und Kontaktmöglichkeiten nennen. Wechseln Sie das Register bewusst.',
    sprechen:
      'Spontan, etwa drei Minuten, locker erzählt (höchstens 60 Sekunden Gedankensammeln): Erzählen Sie eine Geschichte, in der ein gut gemeinter Plan unerwartet schiefging (Feier, Essen, Umzug o. Ä.). Setzen Sie Partikeln wie halt, eben, mal, ja, doch natürlich ein, bringen Sie Selbstironie ein und enden Sie mit einer Pointe.',
    interaktion:
      'Gespräch zu dritt (die prüfende Person übernimmt zwei Rollen, ggf. mit einer zweiten Person). Sie sind Mitbewohnerin oder Mitbewohner und möchten am Samstag eine Feier mit zwölf Gästen. Geplante Wendungen: (1) Eine Mitbewohnerin ironisiert: „Klar, ‚nur ein paar Leute‘ …“ – reagieren Sie humorvoll, ohne beleidigt zu sein. (2) Ein Mitbewohner sagt „Passt schon“, meint aber erkennbar Nein – greifen Sie das freundlich auf. (3) Missverständnis über die Uhrzeit (Beginn der Feier vs. Ankunft der Gäste). (4) Jemand wechselt abrupt das Thema – holen Sie das Gespräch zurück. Dauer ca. 10–12 Minuten, informell.',
    mediation:
      'Eine neue Kollegin aus Chile (Deutsch gut, aber wenig Alltagsdeutsch) hat den Chat mitgelesen und fragt: „Ist Marek sauer? Was heißt ‚wird schon irgendwie‘?“ Erklären Sie ihr mündlich in 2–3 Minuten Tonfall, Ironie und Absprachen und geben Sie ihr einen Tipp, wann man bei Zweifeln besser nachfragt.',
    checklist: makeReviewerChecklist({
      verstehen:
        'Ironie und Untertöne werden erschlossen („mutig“ = höfliche Kritik, „Passt schon“ = unklar, „in Sicherheit gebracht“ = scherzhafte Ausrede); Kernabsprachen (Spülregel, Backen am Freitag) werden korrekt wiedergegeben.',
      register:
        'Chat und Gespräch locker-umgangssprachlich; die E-Mail an die Hausverwaltung wechselt erkennbar ins formelle Register (Anrede, Konjunktiv II der Höflichkeit, keine Reduktionsformen).',
      spektrum:
        'Natürliche Partikeln und Wendungen (halt, eben, mal, ja, doch; „Kein Ding“, „angefressen sein“, „etwas anrücken“, „auf die Schippe nehmen“), nicht gehäuft oder falsch platziert; Reduktionen wie „hab“, „\'ne“, „gibt\'s“ in passenden Situationen.',
      interaktion:
        'Ironie wird erkannt und mit Humor erwidert; bei „Passt schon“ wird nachgefragt, statt Zustimmung anzunehmen; Themenwechsel und Uhrzeit-Missverständnis werden elegant repariert.',
      mediation:
        'Die Erklärung für die Kollegin deckt Ton, Ironie und Absprachen ab, nennt Beispiele aus dem Chat und beschreibt, wann man nachfragen sollte; sie bleibt freundlich und wertet Marek nicht ab.',
      extra:
        'Humor: Selbstironie und Pointe in der Erzählung wirken natürlich und verletzen niemanden; Wortspiele werden nicht erklärt, sondern funktionieren.',
    }),
  }),
  makeAssessment({
    id: 'unseen-social:s2',
    stream: 'social',
    context: 'SOCIAL',
    title: 'Gartenfest und Pfandgläser: Vereinsglosse und Stammtisch',
    reading: `Vereinsblatt „Obst & Garten Echterbach e. V.“ – Glosse der Schriftführerin

Es gibt Vereinsfeste, und es gibt das Echterbacher Gartenfest. Letzteres folgt eigenen Gesetzen. Gesetz Nummer eins: Wenn der Vorstand fragt, wer beim Aufbau hilft, hat plötzlich jeder einen Termin. Gesetz Nummer zwei: Wer ein „kleines Kuchenbuffet“ ankündigt, bekommt am Ende elf Torten, drei Hefezöpfe und eine Schüssel Kartoffelsalat, die niemand bestellt hat, die aber, das muss man zugeben, als Erste leer ist.

Dieses Jahr wollte der Vorstand alles besser machen. Erstens hängt die Helferliste schon seit März im Vereinsheim. Zweitens gibt es einen Plan B für den Regen, nämlich die Scheune von Familie Reutter, sofern sich die Familie daran erinnert, dass sie das zugesagt hat. Drittens bleiben die Getränkepreise gleich, auch wenn der Kassenwart, Herr Haug, bei diesem Satz die Stirn in Falten legt. Er hat außerdem darum gebeten, das Wort „kostenlos“ nicht mehr ins Programm zu schreiben, weil am Ende ohnehin der Verein die Rechnung begleiche.

Die Reaktionen im Verein fielen, wie immer, gemischt aus. Ein langjähriges Mitglied erklärte, er habe nichts gegen Neuerungen, solange alles beim Alten bleibe. Eine jüngere Vorstandskollegin, erst seit zwei Jahren dabei und deshalb noch immer „die Neue“, schlug vor, auf Einweggeschirr zu verzichten und Gläser gegen Pfand auszugeben. Man hörte, wie einige tief Luft holten. Dann sagte Frau Reutter, die Spülmaschine im Bürgerhaus fasse ohnehin mehr, als sie je gebraucht habe, und damit war die Sache so gut wie entschieden. Wer in Schwaben „Des kann man scho mache“ sagt, meint nicht immer ein Ja – das ist eine regionale Redeweise, die man nicht verallgemeinern sollte. Hier meinte es eines, und alle waren überrascht.

Wir bitten deshalb alle Mitglieder, sich bis Ende des Monats auf der Liste einzutragen, nicht nur mit Kuchen, sondern auch mit Muskelkraft. Wer weder backen noch tragen kann, darf gern zuhören, loben oder kritisieren. Das tun ohnehin die meisten am liebsten. Wir freuen uns auf Sie, auf Sie alle, und auf den Kartoffelsalat, den niemand bestellt hat.`,
    listening: `Reinhold: Also, des mit de Pfandgläser, des hen mir no nie so gmacht. [regional, schwäbisch: Das mit den Pfandgläsern haben wir noch nie so gemacht.]
Sevda: Eben, Reinhold, und das ist ja genau das Problem. „Haben wir noch nie so gemacht“ ist kein Argument, das ist eher eine Gewohnheit mit Begründung.
Haug: Jetzt lass doch den Reinhold. Der meint's nicht bös. Der bruddelt halt gern. [regional, schwäbisch: bruddeln = meckern]
Reinhold: I bruddel net, i sag nur, wie's isch. [regional, schwäbisch: Ich meckere nicht, ich sage nur, wie es ist.]
Birgit: Das ist das Gleiche, nur mit besserem Gewissen. Aber mal im Ernst, Sevda, was kostet uns das?
Sevda: Ich war letzte Woche im Rathaus. Die Gemeinde hat Gläser, die verleiht sie gegen eine kleine Gebühr, und die Spülmaschine im Bürgerhaus ist eh frei.
Haug: Eine kleine Gebühr. Des sagt jeder. [regional: Das sagt jeder.] Und wenn dann die Hälfte der Gläser fehlt?
Sevda: Dann gibt's Pfand, und wer ein Glas einbehält, hat halt ein Glas gekauft. Dann sind alle zufrieden.
Reinhold: Wer klaut denn bei uns Gläser? Des isch doch kein Kaufhaus! [regional: Das ist doch kein Kaufhaus!]
Birgit: Reinhold, letztes Jahr fehlten vierzehn Gläser, und ich hab nach dem Fest zwei davon in deinem Kofferraum gefunden.
Reinhold: Des waret Leihgabe! [regional: Das waren Leihgaben!]
Haug: Hajo, aber net zurückgebracht. [regional: Na ja, aber nicht zurückgebracht.]
Sevda: Also, ich fasse zusammen: Gläser von der Gemeinde, ein kleiner Pfandbetrag, und Reinhold übernimmt die Pfandausgabe, weil er sich am besten mit dem Schwund auskennt.
Reinhold: Des isch jetzt aber gemein. Aber guat, i mach's. Unter einer Bedingung: Des Wort „kostenlos“ kommt net ins Programm. [regional: Das ist jetzt aber gemein. Aber gut, ich mache es. Das Wort „kostenlos“ kommt nicht ins Programm.]
Haug: Da bin ich dabei.
Sevda: Das versteh ich jetzt sogar auf Schwäbisch. Das Verständnis kostet nix.
Haug: Des koscht aber en Viertele. [regional: Das kostet aber ein Viertele (ein Viertelliter Wein).]`,
    hoeren:
      'Hören (zweimal, Notizen erlaubt; regionale Wörter sind im Transkript markiert). Beantworten Sie in ganzen Sätzen: (1) Welche Positionen vertreten Reinhold, Sevda und Haug, und wie unterscheiden sich ihre Argumentationsstile? (2) Wie ist „Haben wir noch nie so gemacht“ gemeint, und wie reagiert Sevda ohne Streit? (3) Welche Pointen beruhen auf den vierzehn Gläsern und dem Wort „kostenlos“? (4) Was wurde konkret beschlossen, und wer übernimmt was?',
    lesen:
      'Lesen Sie die Glosse: (1) Welche Haltung hat die Verfasserin zu ihrem Verein, und woran erkennen Sie die Ironie? (2) Erklären Sie die „Gesetze“ eins und zwei und den Schluss („den Kartoffelsalat, den niemand bestellt hat“). (3) Was sagt der Text über die Wendung „Des kann man scho mache“ aus, und warum ist der Hinweis zur Verallgemeinerung wichtig? (4) Welche Neuerungen wurden vorgeschlagen, und welche Widerstände zeigen sich? (5) Vergleichen Sie Glosse und Stammtischgespräch: Welche Informationen ergänzen sich?',
    schreiben:
      'Schreiben Sie zwei Texte, zusammen 240–280 Wörter: (a) eine humorvolle Kurzglosse (140–160 Wörter) für ein Vereinsblatt über ein „Gesetz“ des Vereinslebens, das Sie selbst erlebt haben, mit Ironie, ohne jemanden bloßzustellen; (b) eine formelle Anfrage (100–120 Wörter) an die Gemeinde zur Ausleihe von Gläsern: Zeitraum, Menge, Gebühr, Pfandregelung und Rückgabe erfragen und um schriftliche Bestätigung bitten.',
    sprechen:
      'Spontan (höchstens 60 Sekunden Gedankensammeln), etwa drei Minuten: Sie stellen sich beim Vereinsabend als Neumitglied vor. Erzählen Sie locker, wie Sie hierher gekommen sind, was Ihnen aufgefallen ist (Humor erlaubt, aber kein Spott über Dialekt oder Bräuche) und was Sie beitragen möchten. Beenden Sie mit einer Frage an die Runde.',
    interaktion:
      'Stammtischgespräch zu viert (die prüfende Person spielt Reinhold, ggf. weitere Personen). Sie schlagen vor, auf Einweggeschirr zu verzichten. Geplante Wendungen: (1) Reinhold sagt „Des kann man scho mache“ mit skeptischem Ton – klären Sie, ob es eine Zusage ist. (2) Er macht eine ironische Bemerkung über Zugezogene – reagieren Sie souverän und freundlich. (3) Missverständnis: Er versteht „Pfand“ als zusätzliche Mitgliedergebühr. (4) Er hält einen langen Monolog – unterbrechen Sie höflich und führen Sie zu einer Entscheidung. Dauer ca. 10–12 Minuten. Der Dialekt gehört zur Rolle; Sie sprechen Standarddeutsch, dürfen aber nachfragen.',
    mediation:
      'Ein Mitglied, das nicht am Stammtisch war und kaum Schwäbisch versteht, möchte wissen, was beschlossen wurde. Schreiben Sie in 100–130 Wörtern (oder sprechen Sie 2 Minuten) eine kurze Information für den Vereinsaushang in verständlichem Standarddeutsch: Beschlüsse, Zuständigkeiten, offene Punkte, ohne Dialektzitate und ohne die Beteiligten bloßzustellen.',
    checklist: makeReviewerChecklist({
      verstehen:
        'Regional markierte Äußerungen werden mit Hilfe des Kontexts verstanden; Ironie in Glosse und Stammtisch (Leihgabe, „kostenlos“, Kartoffelsalat) wird erkannt; Beschlüsse und Zuständigkeiten werden vollständig wiedergegeben.',
      register:
        'Glosse: pointiert, spielerisch, aber stilsicher; Gemeindeanfrage: formell und präzise; Stammtisch: freundlich-locker, Standardsprache ohne Imitation des Dialekts.',
      spektrum:
        'Umgangssprachliche Wendungen und Partikeln (halt, eben, mal, ja, doch) passend dosiert; Wortfeld Verein (Vorstand, Kassenwart, Mitgliedsbeitrag, Helferliste); Ironie durch Understatement oder Übertreibung statt durch Beschimpfung.',
      interaktion:
        'Skeptisches „Des kann man scho mache“ wird freundlich geprüft; der ironische Seitenhieb auf Zugezogene wird humorvoll pariert; das Pfand-Missverständnis wird sauber ausgeräumt; die Unterbrechung ist höflich und endet in einer Entscheidung.',
      mediation:
        'Aushang in verständlichem Standarddeutsch; Beschlüsse sind klar, Verantwortliche genannt, nichts erfunden; Dialekt wird übersetzt, nicht karikiert.',
      extra:
        'Regionale Sensibilität: Dialekt wird weder verspottet noch auf alle Schwaben verallgemeinert; Nachfragen sind höflich („Was genau meinen Sie damit?“).',
    }),
  }),
  makeAssessment({
    id: 'unseen-social:s3',
    stream: 'social',
    context: 'SOCIAL',
    title: 'Zugausfall am Wandertag: Blogbeitrag und Bahnsteiggespräch',
    reading: `Wer zum ersten Mal mit sieben Freunden verreist, lernt schnell: Eine Gruppe bewegt sich nicht mit der Geschwindigkeit ihres schnellsten Mitglieds, sondern mit der ihres unentschlossensten. Letzten Monat sind wir zu einer Wanderung auf die Schwäbische Alb aufgebrochen, geplant als entspannter Samstag mit zwei Gipfeln, einer Hütte und möglichst wenig Organisation. Die Betonung liegt auf „möglichst“.

Los ging es mit der Frage, wann wir uns am Bahnhof treffen. Die Vorschläge reichten von „so gegen acht“ bis „kurz nach neun, aber nur, wenn der Kaffee stimmt“. Wir einigten uns auf Viertel nach acht, was bedeutete, dass um halb neun immerhin fünf von uns da waren. Der sechste kam mit einem Rucksack, der aussah, als wolle er damit die Antarktis durchqueren, der siebte mit dem Satz „Ich hab nur kurz was vergessen“. Er hatte seine Wanderschuhe vergessen, und zwar sämtliche.

Der Regionalzug fuhr natürlich nicht. Auf der Anzeige stand erst „+5“, dann „+15“, dann nur noch ein ratloses Fragezeichen. Eine freundliche Lautsprecherstimme bat um „Verständnis für die Unannehmlichkeiten“. Wir haben Verständnis. Wir haben immer Verständnis. Es ist das Einzige, was wir in Deutschland zuverlässig im Überfluss haben. Statt zu schimpfen, beschlossen wir, den Ausfall als Chance zu betrachten – ein Satz, der in der Gruppe ungefähr so gut ankam, wie man es erwarten würde.

Am Ende wurde es doch noch ein schöner Tag. Eine ältere Dame am Bahnsteig, die solche Situationen offenbar kannte, erklärte uns, es gebe einen Bus, der zwar länger brauche, dafür aber direkt am Wanderparkplatz halte. Wir stiegen ein, verpassten nur einmal die richtige Haltestelle und kamen mit einer Stunde Verspätung, einem Gipfel statt zwei und überraschend guter Laune oben an. Die Hütte war zum Glück noch geöffnet. Der siebte Wanderer lief in den Ersatzschuhen einer Mitwanderin, die ihm zwei Nummern zu groß waren. Er behauptet bis heute, es sei sein bester Ausflug gewesen. Die anderen vermuten, er sage das nur, damit niemand fragt, wer beim nächsten Mal die Schuhe einpackt.`,
    listening: `Anja: Hat jemand die Durchsage verstanden? Bei mir hat's nur gerauscht.
Ben: Doch, doch, ganz klar: Der Zug fällt aus. Wegen „betrieblicher Gründe“. Das ist Bahnisch für „Wir wissen es selbst nicht“.
Dilara: Und der nächste?
Ben: Der nächste fährt in einer Stunde. Wenn er fährt. Und wenn nicht, dann halt der übernächste. Oder wir bauen uns hier eine Hütte.
Anja: Ben, bitte, ich bin gerade echt gestresst. Wir haben die Hütte für zwölf Uhr reserviert!
Hannes: Jetzt hock di mol na, Anja. Des wird scho. [regional, schwäbisch: Setz dich mal hin, Anja. Das wird schon.] Mir guckät mol, ob's en Bus gibt. [regional: Wir schauen mal, ob es einen Bus gibt.]
Dilara: Ich hab hier was in der App. Ein Bus fährt in zwanzig Minuten vom Vorplatz ab und hält am Wanderparkplatz, ist aber fast eine Stunde unterwegs.
Anja: Eine Stunde? Dann sind wir ja erst gegen elf dran.
Ben: Dann nehmen wir eben nur einen Gipfel. Oder wir laufen schneller. Oder wir nennen die Hütte einfach „Mittagessen mit Aussicht“.
Anja: Das ist nicht lustig.
Ben: Doch, ein bisschen schon.
Hannes: Gell, des isch scho a bissle lustig. [regional: Nicht wahr, das ist schon ein bisschen lustig.]
Anja: Na gut, ein bisschen. Aber ruf trotzdem in der Hütte an, Hannes, und sag Bescheid.
Hannes: Mach i, aber die Wirtin versteht mi net, die isch aus Hamburg! [regional: Mache ich, aber die Wirtin versteht mich nicht, die ist aus Hamburg!]
Dilara: Dann übersetz ich. Gib mir das Handy.
Ben: Ich schreibe schon mal die Beschwerde. Titel: „Warum wir trotzdem pünktlich sein wollten“.
Anja: Okay, Plan: Bus, ein Gipfel, Hütte ab halb zwei, Dilara telefoniert. Einverstanden?
Dilara: Einverstanden, aber nur, wenn ich vorher einen Kaffee kriege.
Ben: Ich bin dafür, dass wir trotzdem ein Gruppenfoto machen. Beweisstück A.
Anja: Gut. Aber lächeln, bitte, auch wenn es schwerfällt.`,
    hoeren:
      'Hören (zweimal, Notizen erlaubt; regionale Wörter sind markiert). Beantworten Sie in ganzen Sätzen: (1) Wie unterscheiden sich Anjas, Bens und Hannes\' Reaktionen auf den Ausfall? (2) Was meint Ben mit „Bahnisch“, und warum wirkt der Witz erst für Anja nicht, später doch? (3) Welcher Plan wird vereinbart, und wer übernimmt was? (4) Welche Rolle spielen Dilara und Hannes beim Beruhigen der Lage?',
    lesen:
      'Lesen Sie den Blogbeitrag: (1) Welche Haltung hat die Autorin zu ihrer Gruppe, und mit welchen Mitteln erzeugt sie Ironie (Übertreibung, Understatement, Wiederholung)? (2) Erklären Sie „Wir haben immer Verständnis“ und „und zwar sämtliche“. (3) Was ist am Schluss die eigentliche Pointe? (4) Vergleichen Sie Blog und Gespräch: Wo decken sich die Abläufe, wo unterscheiden sich Ton und Details?',
    schreiben:
      'Schreiben Sie zwei Texte, zusammen 240–280 Wörter: (a) eine lockere Nachricht (ca. 110–130 Wörter) an die Gruppe, in der Sie ankündigen, dass Sie sich verspäten, einen Alternativvorschlag machen und mit Humor um Geduld bitten; (b) eine sachliche Kundenbeschwerde (ca. 130–150 Wörter) an das Verkehrsunternehmen wegen des Ausfalls: Sachverhalt, Auswirkungen, konkrete Forderung, Frist. Register deutlich unterscheiden.',
    sprechen:
      'Spontan (höchstens 60 Sekunden Gedankensammeln), etwa drei Minuten: Erzählen Sie von einer Reise oder einem Ausflug, bei dem etwas Wichtiges schiefging und der trotzdem schön wurde. Erzählen Sie lebendig, mit Dialogwiedergabe, Selbstironie und mindestens einer passenden Partikelwendung (halt, eben, ja, doch).',
    interaktion:
      'Gespräch zu viert am Bahnsteig (Prüfende übernehmen mehrere Rollen). Sie sind für die Gruppe verantwortlich. Geplante Wendungen: (1) Ein Teilnehmer reagiert mit sarkastischem Humor („Wir bauen uns hier eine Hütte“) – reagieren Sie, ohne die Stimmung zu kippen. (2) Ein anderer schlägt ein Taxi vor, das Sie für zu teuer halten – lehnen Sie diplomatisch ab. (3) Ein Teilnehmer versteht „halb zwei“ als 13:30 Uhr, ein anderer als 14:30 Uhr – klären Sie das Missverständnis. (4) Ein Teilnehmer unterbricht Ihre Zusammenfassung mit einer neuen Idee – nehmen Sie sie auf oder lehnen Sie sie begründet ab. Dauer ca. 10–12 Minuten.',
    mediation:
      'Ihre Schwester wollte mitkommen, hat aber nur den Blogbeitrag gelesen und fragt: „Was war denn jetzt eigentlich schiefgegangen, und war es schlimm?“ Erzählen Sie ihr in 2–3 Minuten, was passierte, was die Gruppe daraus gemacht hat und welche Tipps Sie daraus für die nächste Reise ziehen. Erklären Sie ironische Stellen, ohne sie platt zu machen.',
    checklist: makeReviewerChecklist({
      verstehen:
        'Ironie im Blog (Verständnis als „Überfluss“, „möglichst wenig Organisation“) und im Gespräch („Bahnisch“, „Beweisstück A“) wird erkannt; der Plan mit Aufgabenverteilung wird korrekt zusammengefasst.',
      register:
        'Gruppennachricht locker mit Reduktionen und Partikeln; Beschwerde an das Verkehrsunternehmen formell, sachlich, mit Frist und konkreter Forderung.',
      spektrum:
        'Wendungen wie „einen Gang zurückschalten“, „es hapert an etwas“, „etwas auf die leichte Schulter nehmen“, „ein Ausfall“, „Anschluss verpassen“; Reduktionen (hab, gibt\'s, \'ne) und Partikeln in natürlicher Dosis.',
      interaktion:
        'Sarkasmus wird mit Humor gekontert; das Taxi wird diplomatisch abgelehnt; das Uhrzeit-Missverständnis wird durch Rückversicherung („Also 13:30, nicht 14:30?“) geklärt; die neue Idee wird fair behandelt.',
      mediation:
        'Die Schwester erhält einen klaren Ablauf, eine Einschätzung („nicht schlimm, weil …“) und Erklärungen zu den ironischen Stellen; es wird nichts hinzuerfunden.',
      extra:
        'Erzählkompetenz: Der Erzählbogen (Ausgangslage, Zwischenfall, Wendung, Pointe) ist im freien Sprechen erkennbar, direkte Rede wird natürlich eingesetzt.',
    }),
  }),
  makeAssessment({
    id: 'unseen-social:s4',
    stream: 'social',
    context: 'SOCIAL',
    title: 'Zettelkrieg im Treppenhaus: Aushänge und Hofgespräch',
    reading: `Pinnwand im Treppenhaus, Hauptstraße 7, über zwei Wochen hinweg

Zettel 1 (Frau Beck, 2. OG): „Liebe Nachbarn, in den letzten Wochen wurde mehrfach vergessen, die Treppe zu wischen. Die Kehrwoche ist keine Empfehlung, sondern eine Vereinbarung. Ich bitte um Beachtung. Gruß, B. Beck“

Zettel 2 (handschriftlich darunter, Jonas, 1. OG): „Vereinbarung, ja. Aber die wurde 1987 getroffen, als hier noch fast alle tagsüber zu Hause waren. Heute arbeiten wir alle. Vorschlag: ein Reinigungsdienst für alle. Das kostet pro Monat etwa so viel wie zwei Pizzen. LG Jonas“

Zettel 3 (Frau Beck): „Jonas, bei aller Wertschätzung: Es geht nicht um Pizza, es geht um Verantwortung. Außerdem steht Ihr Fahrrad schon wieder im Flur.“

Zettel 4 (Jonas): „Das Fahrrad steht im Flur, weil der Fahrradkeller seit Februar kein Licht hat. Das habe ich der Hausverwaltung dreimal gemeldet. Bitte nicht schon wieder das Fahrrad.“

Zettel 5 (Hausverwaltung, gedruckt): „Sehr geehrte Mieterinnen und Mieter, wir bitten um eine sachliche Kommunikation. Die Kehrwoche ist gemäß Hausordnung Teil des Mietvertrags. Die Beauftragung eines Reinigungsdienstes ist möglich, sofern mehr als zwei Drittel der Mietparteien zustimmen; die Kosten würden anteilig über die Nebenkosten umgelegt. Der Defekt im Fahrradkeller ist bekannt, ein Termin für den Elektriker steht noch nicht fest.“

Zettel 6 (Herr Aydın, 3. OG, neu eingezogen): „Ich bin neu hier. Ich habe keine Ahnung von 1987, aber ich putze gern, wenn mir jemand zeigt, wie. Und ich würde gern über den Reinigungsdienst abstimmen. Samstag, 17 Uhr, im Hof. Ich bringe Getränke mit.“

Zettel 7 (Frau Beck): „Ich komme. Aber ich bringe meine Hausordnung mit.“

Zettel 8 (Jonas): „Frau Beck, ich bringe Kuchen. Dann ist die Hausordnung wenigstens besser zu ertragen.“

Zettel 9 (Frau Yılmaz, EG): „Bitte nehmen Sie am Samstag Rücksicht: Mein Sohn schläft nachmittags. Ich komme trotzdem kurz runter, aber ohne Lautsprecher. Vielen Dank, die Nachbarin vom Erdgeschoss, die nur einen Wunsch hat: dass endlich jemand die Haustür leise schließt.“`,
    listening: `Beck: Also, Herr Aydın, des isch nett, dass Sie dabei sind. Hen Sie scho mal Kehrwoche gmacht? [regional, schwäbisch: Das ist nett, dass Sie dabei sind. Haben Sie schon mal Kehrwoche gemacht?]
Aydın: Ehrlich gesagt nicht. Ich kenne das nur vom Hörensagen. Ich dachte, das sei so eine Art Legende.
Jonas: Eine Legende mit Eimer.
Beck: Des isch kei Legende, des isch Ordnung. [regional: Das ist keine Legende, das ist Ordnung.] Sie werfen das Wort so leicht hin.
Jonas: Frau Beck, ich werfe nichts hin. Ich sage nur, dass ich um sieben Uhr morgens aus dem Haus muss und um acht Uhr abends komme. Wann soll ich wischen? Nachts?
Beck: Hajo, i hab ja nix gegen Sie persönlich. Aber wenn jeder sagt, i hab koi Zeit, dann hen mir ein dreckiges Treppenhaus. [regional: Na ja, ich habe nichts gegen Sie persönlich. Aber wenn jeder sagt, ich habe keine Zeit, dann haben wir ein dreckiges Treppenhaus.]
Aydın: Darf ich einen Vorschlag machen? Wir probieren es sechs Monate mit dem Reinigungsdienst. Wenn es teurer wird als besprochen oder schlecht gemacht ist, gehen wir zurück zur Kehrwoche.
Beck: Und wer prüft des? [regional: Und wer prüft das?]
Aydın: Sie, Frau Beck. Sie sind die Einzige, die das Treppenhaus wirklich kennt.
Jonas: Das ist ein ziemlich geschicktes Argument.
Aydın: Das war kein Argument, das war ein Kompliment.
Beck: Des isch beides. Aber … no guat. [regional: Das ist beides. Aber na gut.] Unter einer Bedingung: Das Licht im Fahrradkeller wird repariert, bevor wir abstimmen. Sonst bleibt des Rad im Flur, und i muss drüber steige. [regional: … und ich muss darübersteigen.]
Jonas: Das ist fair. Ich schreibe der Hausverwaltung heute noch, und diesmal mit Frist.
Beck: Und mit Kopie an mich. Net, dass die wieder sagen, sie hätten nix gwusst. [regional: Nicht, dass die wieder sagen, sie hätten nichts gewusst.]
Aydın: Dann heißt es: Abstimmung nächste Woche?
Beck: Nächste Woche, gleiche Zeit. Und Jonas: Der Kuchen war gut. Aber des sag i nur einmal. [regional: Das sage ich nur einmal.]
Jonas: Ich schreibe es auf einen Zettel.`,
    hoeren:
      'Hören (zweimal, Notizen erlaubt; regionale Wörter sind markiert). Beantworten Sie in ganzen Sätzen: (1) Wie entwickelt sich das Gespräch von der Konfrontation zur Annäherung? Nennen Sie zwei Wendepunkte. (2) Was ist an Herrn Aydıns „Kompliment“ pragmatisch geschickt, und wie reagiert Frau Beck? (3) Welche Bedingungen stellt Frau Beck, welche Zusagen macht Jonas? (4) Wie wirkt Frau Becks letzte Bemerkung zum Kuchen – wörtlich und gemeint?',
    lesen:
      'Lesen Sie die Aushänge: (1) Verfolgen Sie den Konflikt: Welche Sachfragen und welche Beziehungsebenen (Vorwürfe, Seitenhiebe) lassen sich trennen? (2) Wie unterscheiden sich Ton und Register der neun Zettel (z. B. Frau Beck, Hausverwaltung, Herr Aydın, Frau Yılmaz)? (3) Was ist der ironische Kern von Zettel 9? (4) Welche Regelung und welche Abstimmungsbedingung nennt die Hausverwaltung, und was folgt daraus für das Treffen?',
    schreiben:
      'Schreiben Sie zwei Texte, zusammen 240–280 Wörter: (a) einen freundlich-humorvollen Zettel für die Pinnwand (ca. 90–110 Wörter), in dem Sie als neu eingezogene Person zum gemeinsamen Hofgespräch einladen und einen Kompromiss vorschlagen, ohne jemanden bloßzustellen; (b) eine formelle E-Mail an die Hausverwaltung (ca. 150–170 Wörter) mit Fristsetzung zur Reparatur des Lichts im Fahrradkeller, mit Sachverhalt, Meldedaten (fiktiv, keine realen Daten) und gewünschter schriftlicher Rückmeldung.',
    sprechen:
      'Spontan (höchstens 60 Sekunden Gedankensammeln), etwa drei Minuten: Erzählen Sie von einem Nachbarschaftskonflikt (real oder erfunden), den Sie gelöst oder zumindest entschärft haben. Zeigen Sie dabei Humor, nennen Sie Ihre eigenen Anteile am Konflikt und verwenden Sie natürliche Partikeln und Reduktionen.',
    interaktion:
      'Hofgespräch zu dritt (Prüfende spielen Frau Beck und eine weitere Person). Sie vertreten den Reinigungsdienst. Geplante Wendungen: (1) Frau Beck sagt „Hajo, i hab ja nix gegen Sie persönlich“ – das „ja“ signalisiert ein „aber“; reagieren Sie vorausschauend. (2) Sie spielt auf Ihren Beruf und Ihre Arbeitszeiten spitz an – reagieren Sie ohne Gegenangriff. (3) Missverständnis: Sie versteht „Reinigungsdienst testen“ als „Kehrwoche abschaffen“. (4) Eine dritte Person unterbricht mit einer Beschwerde über ein ganz anderes Thema. Ziel: eine Vereinbarung für die Abstimmung. Dauer ca. 10–12 Minuten.',
    mediation:
      'Eine neue Mieterin aus Italien, die gerade einzieht, versteht die Zettel nicht und fragt: „Was ist eine Kehrwoche, und warum streiten alle?“ Erklären Sie ihr mündlich in 2–3 Minuten, was damit gemeint ist, wie ernst der Konflikt wirklich ist und wie sie sich neutral verhalten kann. Vermeiden Sie Klischees über Schwaben.',
    checklist: makeReviewerChecklist({
      verstehen:
        'Sachebene und Beziehungsebene werden getrennt; Register der Zettel wird gedeutet; Frau Becks „Hajo … ja“ wird als Einleitung eines „Aber“ erkannt; Bedingungen und Zusagen werden vollständig wiedergegeben.',
      register:
        'Zettel freundlich-humorvoll und nicht belehrend; E-Mail an die Hausverwaltung formell, mit Frist und Sachverhalt; im Gespräch respektvolle Anrede mit Sie.',
      spektrum:
        'Wendungen wie „etwas zur Sprache bringen“, „einen Kompromiss finden“, „Rücksicht nehmen“, „ein Machtwort sprechen“, „sich die Butter nicht vom Brot nehmen lassen“; Partikeln und Reduktionen natürlich.',
      interaktion:
        'Spitze Bemerkungen werden nicht erwidert, sondern entschärft; das Missverständnis (Test vs. Abschaffung) wird mit eigenen Worten richtiggestellt; die Unterbrechung durch die dritte Person wird freundlich vertagt; am Ende steht eine konkrete Vereinbarung.',
      mediation:
        'Die Erklärung der Kehrwoche ist sachlich, kulturell sensibel und frei von Klischees; die Neutralität der neuen Mieterin wird als praktischer Rat formuliert.',
      extra:
        'Soziale Pragmatik: Eigene Anteile am Konflikt werden benannt; Kritik wird als Ich-Botschaft formuliert; Dialekt wird als solcher markiert, nicht karikiert.',
    }),
  }),
];

const engineeringGraduation: AssessmentTask[] = [
  makeAssessment({
    id: 'unseen-engineering:e1',
    stream: 'engineering',
    context: 'ENGINEERING',
    title: 'EV-Thermomanagement unter Schnelllade-Last: Prüfstandsbericht und Review',
    reading: `Interner Entwicklungsbericht · Abteilung Hochvolt-Thermomanagement (Übungsdokument)

Bei drei Prüfstandsläufen eines 800-Volt-Batteriepakets unter wiederholter Schnelllade-Last stieg die Zelltemperatur an Modul 4 schneller als in der thermischen Simulation erwartet. Nach zwölf Minuten Ladezeit erreichte der Sensor am Kühlmitteleintritt 47,8 °C, während das Simulationsmodell für diesen Betriebsmodus 42,5 °C prognostiziert hatte. Parallel dazu löste die Leistungselektronik zweimal eine vorsorgliche Leistungsbegrenzung (Derating) aus, sodass die Ladeleistung für jeweils vier Minuten um rund dreißig Prozent reduziert wurde.

Fest steht bislang lediglich, dass die gemessenen Temperaturwerte an Modul 4 in diesen drei Läufen höher lagen als an den übrigen Modulen. Unklar ist jedoch, ob ein tatsächlicher Wärmestau durch ungleichmäßige Durchströmung der Kühlplatte vorliegt oder ob ein versetzter Temperatursensor bzw. eine veränderte Kontaktpaste am Modulgehäuse die Messwerte verfälscht hat. Eine mechanische Öffnung des Batteriegehäuses steht noch aus.

Die Kalibrierabteilung weist darauf hin, dass zwischen Lauf 1 und Lauf 2 ein Software-Update für die Pumpenansteuerung der TCU/BTM-Schnittstelle aufgespielt wurde. Ob dieses Update den Volumenstrom unbeabsichtigt gedrosselt hat, lässt sich aus den vorliegenden Protokollen nicht sicher ablesen, da der Durchflusssensor im sekundären Kühlkreis erst nach Lauf 2 neu kalibriert wurde.

Die Projektleitung drängt auf eine Freigabe für die Wintererprobung in Nordschweden in zwei Wochen, da Transportkapazitäten gebucht sind. Das Safety-Team betont hingegen, dass gemäß FMEA zunächst ein wiederholbarer Nachweis über mindestens fünf Prüfstandsläufe mit kalibriertem Durchflussprofil erforderlich ist, bevor eine eingeschränkte Erprobungsfreigabe überhaupt geprüft werden kann.`,
    listening: `Projektleiter Kramer: Frau Albers, wir haben am Montag die Verladung der Fahrzeuge nach Schweden. Reicht der aktuelle Befund an Modul 4 für eine bedingte Erprobungsfreigabe?
Ingenieurin Albers: Für eine Freigabe liegt uns aktuell kein belastbarer Nachweis vor. Wir haben einen Temperaturanstieg auf knapp achtundvierzig Grad beobachtet, wissen aber noch nicht, ob es ein echtes Durchströmungsproblem oder ein Messartefakt ist.
Kramer: Aber das Derating hat doch sauber gegriffen! Die Batterie wurde geschützt. Das zeigt doch, dass das System sicher reagiert.
Albers: Das Derating hat die Leistung wie vorgesehen gedrosselt, ja. Aber ein Schutzmechanismus ersetzt keinen Ursachennachweis. Wenn in Schweden zusätzlich hohe Lastwechsel auf kaltem Boden dazukommen, wissen wir ohne Durchflussmessung nicht, wie schnell sich Modul 4 tatsächlich aufheizt.
Kramer: Unser Zulieferer der Kühlplatte meint, an seiner Fertigung könne es nicht liegen, die Toleranzen seien laut Endkontrolle alle im grünen Bereich.
Albers: Das nehme ich zur Kenntnis, es schließt aber weder Montagefehler bei der Wärmeleitpaste noch eine reduzierte Pumpenleistung nach dem Software-Update aus.
Kramer: Was brauchen Sie konkret, und bis wann?
Albers: Erstens den Abgleich des Durchflusssensors mit der alten Softwareversion am Prüfstand bis Donnerstagabend. Zweitens eine Endoskopie- oder Wärmebildprüfung von Modul 4, ohne das Pack sofort komplett zu zerlegen.
Kramer: Wenn bis Donnerstag keine Abweichung mehr auftritt – fahren die Autos dann am Montag?
Albers: Dann legen wir die vollständigen Messdaten am Freitag dem Safety-Board vor. Ob eine eingeschränkte Freigabe unter Temperaturauflage vertretbar ist, entscheidet allein das Board anhand der FMEA-Kriterien, nicht mein Bauchgefühl.`,
    hoeren:
      'Hören (zweimal, Notizen erlaubt). Beantworten Sie in ganzen Sätzen: (1) Warum lässt Frau Albers das Argument des Projektleiters, das Derating habe die Batterie doch sicher geschützt, nicht für eine Freigabe gelten? (2) Wie reagiert sie auf die Behauptung des Zulieferers, seine Kühlplattentoleranzen seien einwandfrei? (3) Welche zwei konkreten Prüfschritte nennt sie bis Donnerstagabend? (4) Wer entscheidet am Ende über eine mögliche Freigabe, und unter welcher Voraussetzung?',
    lesen:
      'Lesen Sie den Bericht gründlich: (1) Trennen Sie eindeutig belegte Messbefunde von offenen Hypothesen und unbelegten Vermutungen. (2) Warum erschwert die zeitliche Überschneidung zweier Änderungen zwischen Lauf 1 und Lauf 2 die Ursachenzuordnung? (3) Erklären Sie die Begriffe „Derating“ und „Wärmestau“ so, dass eine kaufmännische Projektleitung sie ohne Ingenieurstudium versteht. (4) Wo weichen die Erwartungen von Projektleitung und Safety-Team voneinander ab?',
    schreiben:
      'Schreiben Sie eine technische Entscheidungsvorlage von 230–270 Wörtern für das freitägliche Safety-Board. Stellen Sie Beobachtung (Modul 4, Derating), offene Fehlerhypothesen (Durchfluss, Sensorik/Wärmeleitpaste, Software-Update), die Beweisgrenzen aus Lauf 1–2 und die beiden Prüfschritte bis Donnerstag klar dar. Sprechen Sie ausdrücklich keine Serien- oder Vorabfreigabe aus und benennen Sie die Bedingung für eine Neubewertung. Stil: sachlich, präzise, ohne Schuldzuweisungen.',
    sprechen:
      'Spontan (höchstens 60 Sekunden Gedankensammeln, keine Notizen), etwa drei Minuten: Wie geht man in technischen Projekten verantwortungsvoll mit starkem Termin- und Verladungsdruck um, wenn Messdaten noch nicht eindeutig sind? Strukturieren Sie nach Kriterien, geben Sie ein konkretes Beispiel aus Ihrer Praxis (oder dem Kontext) und benennen Sie ausdrücklich die Grenzen Ihrer Rolle.',
    interaktion:
      'Statusmeeting zu zweit (prüfende Person spielt Projektleiter Kramer). Geplante Wendungen: (1) Er unterbricht Sie mit „Das kostet uns sechzigtausend Euro Standgebühr am Tag!“. (2) Er unterstellt, Sie wollten die Wintererprobung komplett absagen (Missverständnis). (3) Er bringt ein neues Argument: „Die Konkurrenz testet längst unter Extrembedingungen!“ (4) Er verlangt eine telefonische Vorab-Zusage für die Spedition. Reagieren Sie ruhig, trennen Sie Kostenfolgen von Safety-Nachweisen und sichern Sie verbindlich den nächsten Prüftermin.',
    mediation:
      'Ein Mitglied des Betriebsrats und eine Einkäuferin ohne Hochvolt-Fachhintergrund fragen nach dem Meeting: „Warum bleiben die Prototypen vielleicht stehen, obwohl die Batterie doch gar nicht überhitzt ist?“ Erklären Sie in 2–3 Minuten verständlich den Zusammenhang zwischen Temperaturmessung, vorsorglichem Derating, der fehlenden Durchflussmessung und der Rolle der FMEA, ohne technische Abkürzungen unübersetzt stehen zu lassen.',
    checklist: makeReviewerChecklist({
      verstehen:
        'Beobachtung (47,8 °C, 3 Läufe, 30 % Derating), Störfaktoren (Software-Update und späte Sensorkalibrierung) sowie Prüfschritte bis Donnerstag werden vollständig und ohne Überinterpretation wiedergegeben.',
      register:
        'Entscheidungsvorlage durchgängig sachlich-analytisch im Nominal- und Verbalstil ohne Panik oder Verharmlosung; im Konfliktgespräch respektvoll und standfest.',
      spektrum:
        'Fach- und Präzisionskollokationen wie „einen Befund eingrenzen“, „einen Nachweis erbringen“, „vorsorglich drosseln“, „Messwerte verfälschen“, „eine Entscheidung dem Gremium vorbehalten“; sichere Nutzung von Konjunktiv I und Passiversatzformen.',
      interaktion:
        'Kostendruck wird anerkannt, ohne Sicherheitsgrenzen preiszugeben; das Missverständnis (Prüfprogramm vs. Komplettabsage) wird sachlich korrigiert; die Vorab-Zusage wird mit Verweis auf das Safety-Board klar abgelehnt.',
      mediation:
        'Fachbegriffe („Derating“, „FMEA“, „Volumenstrom“) werden anschaulich übersetzt; es werden keine nicht im Text belegten Ausfallwahrscheinlichkeiten erfunden.',
      extra:
        'Evidenzdisziplin: Befund, Arbeitshypothese und Freigabebefugnis bleiben durchgehend getrennt.',
    }),
  }),
  makeAssessment({
    id: 'unseen-engineering:e2',
    stream: 'engineering',
    context: 'ENGINEERING',
    title: 'Rekuperations-Softwarestand zwischen ECU, TCU und Zulieferer: Eskalationsgespräch',
    reading: `Auszug aus der gemeinsamen Fehleranalyse · Antriebsstrang & Rekuperation

Seit Integration des Softwarestandes V4.2 durch den Tier-1-Inverterzulieferer berichten Testfahrer bei feuchter Fahrbahn von einem verzögerten Abbau des Rekuperationsmoments kurz vor Stillstand. In vier von achtzehn protokollierten Bremsmanövern lag die zwischen TCU (Getriebesteuergerät) und ECU (Motor-/Antriebssteuergerät) ausgetauschte Momentenfreigabe rund 90 Millisekunden über dem definierten Schwellwert im Lastenheft.

Der Zulieferer verweist in seiner Stellungnahme darauf, dass sein Inverter im Komponentenprüfstand alle geforderten Antwortzeiten einhalte. Die beobachtete Verzögerung müsse folglich auf eine veränderte Botschaftspriorisierung auf dem internen Fahrzeug-CAN-Bus zurückzuführen sein. Eine eigene Nachmessung im Gesamtfahrzeug lehnt der Zulieferer derzeit unter Hinweis auf fehlende Verrechnungsfreigaben ab.

Die eigene Systemintegration widerspricht dieser pauschalen Zuweisung: In einer Vergleichsmessung mit dem alten Softwarestand V4.1 bei identischer CAN-Bus-Auslastung trat die 90-Millisekunden-Verzögerung nicht auf. Allerdings wurden in V4.2 auch neue Diagnosebotschaften aktiviert, die bislang nicht in der Buslast-Bilanz dokumentiert waren. Ein eindeutiger Verursacher lässt sich ohne gemeinsame Bus-Log-Auswertung nicht feststellen.

In der kommenden Woche steht ein Meilenstein-Review mit dem Gesamtfahrzeugvorstand an. Beide Seiten müssen klären, welche Messdaten bis dahin gemeinsam ausgewertet werden, ohne vertragliche Haftungsfragen vorschnell zu präjudizieren.`,
    listening: `Zulieferer Dr. Böttcher: Frau Lindemann, ich verstehe Ihren Ärger, aber unsere Komponentenprüfung ist eindeutig: Unser Inverter reagiert unter zwanzig Millisekunden. Wenn im Auto neunzig ankommen, liegt das Problem auf Ihrem Fahrzeugbus, nicht bei uns.
Systemingenieurin Lindemann: Herr Böttcher, unsere Einzelmessung widerspricht Ihrer Prüfstandszahl gar nicht. Wir beobachten den Versatz aber ausschließlich seit dem Update auf V4.2 – bei unveränderter Fahrzeug-Hardware.
Böttcher: Wollen Sie damit sagen, wir hätten fehlerhaften Code geliefert? Das weise ich zurück!
Lindemann: Nein, ich weise niemandem vorab eine Schuld zu. Ich stelle nur fest, dass in V4.2 zusätzliche Diagnosebotschaften aktiv sind, die in unserer gemeinsamen Buslast-Tabelle noch fehlen. Das kann das Timing verschoben haben.
Böttcher: Na gut, aber ohne schriftliche Kostenübernahme für einen Testwagen kann ich keine Ingenieure drei Tage auf Ihre Teststrecke schicken.
Lindemann: Für eine gemeinsame Log-Analyse brauchen wir heute noch keinen Testwagen auf der Strecke. Wenn Sie mir die genauen Sendefrequenzen der neuen Diagnosebotschaften freigeben, fahren wir die Vergleichsaufzeichnung morgen auf unserem Prüfstand und werten die Spuren gemeinsam per Videokonferenz aus.
Böttcher: Und wenn sich zeigt, dass die Botschaften harmlos sind?
Lindemann: Dann suchen wir gemeinsam im CAN-Gateway weiter und dokumentieren das sauber fürs Review. Entscheidend ist, dass wir bis Dienstag belastbare Fakten vorlegen, statt uns gegenseitig Papiere zuzuschieben.`,
    hoeren:
      'Hören (zweimal, Notizen erlaubt). Beantworten Sie in ganzen Sätzen: (1) Warum entkräftet Frau Lindemann Dr. Böttchers Einwand zum Komponentenprüfstand, ohne ihn der Lüge zu bezichtigen? (2) Welchen konkreten technischen Verdacht nennt sie statt einer Schuldzuweisung? (3) Wie umgeht sie pragmatisch die vertragliche Hürde der Testwagen-Kosten? (4) Welche gemeinsame Haltung schlägt sie für den Fall vor, dass sich ihr Verdacht nicht bestätigt?',
    lesen:
      'Lesen Sie den Auszug gründlich: (1) Warum sind sowohl der Verweis des Zulieferers auf den Komponentenprüfstand als auch eine vorschnelle Schuldzuweisung an den Zulieferer unzureichend? (2) Welche Rolle spielt die fehlende Dokumentation neuer Diagnosebotschaften? (3) Erklären Sie die Begriffe „Lastenheft“ und „Buslast-Bilanz“ laienverständlich. (4) Was meint der letzte Satz mit „vertragliche Haftungsfragen nicht vorschnell präjudizieren“?',
    schreiben:
      'Schreiben Sie eine formelle, kooperative E-Mail (220–260 Wörter) an Dr. Böttcher zur Vorbereitung der gemeinsamen Log-Auswertung. Fassen Sie die Ausgangslage (Unterschied V4.1 vs. V4.2, fehlende Buslast-Angaben) sachlich zusammen, stellen Sie zwei konkrete technische Fragen zu den Diagnosebotschaften und schlagen Sie einen verbindlichen Terminplan vor, ohne rechtliche Haftung oder Schuld zuzuweisen.',
    sprechen:
      'Spontan (höchstens 60 Sekunden Gedankensammeln), etwa drei Minuten: Wie gelingt eine sachliche Zusammenarbeit mit externen Zulieferern, wenn ein Fehler genau an der Schnittstelle zweier Systeme auftritt? Nennen Sie Prinzipien, typische sprachliche Fallstricke und ein konkretes Vorgehen.',
    interaktion:
      'Online-Krisencall mit dem Zulieferer (prüfende Person spielt Dr. Böttcher). Geplante Wendungen: (1) Er unterbricht zu Beginn: „Sie wollen doch nur die Verantwortung auf uns abwälzen!“ (2) Er behauptet fälschlich, Sie hätten eine sofortige Vertragsstrafe angedroht (Missverständnis). (3) Er wendet ein, sein Team habe vor Freitag keine Kapazität für eine Auswertung. (4) Er fordert vorab eine schriftliche Bestätigung, dass V4.2 freigegeben bleibt. Deeskalieren Sie, klären Sie das Missverständnis und vereinbaren Sie einen realistischen Arbeitsschritt.',
    mediation:
      'Ein kaufmännischer Leiter Ihres Hauses bereitet den Meilenstein-Termin vor und fragt: „Müssen wir den Zulieferer jetzt vertraglich abmahnen oder bremst nur eine Software-Einstellung?“ Vermitteln Sie in 2–3 Minuten sachlich den aktuellen Erkenntnisstand, den Unterschied zwischen Komponenten- und Gesamtsystemtest sowie warum eine gemeinsame Datenauswertung vor einer Eskalation sinnvoll ist.',
    checklist: makeReviewerChecklist({
      verstehen:
        'Unterschied zwischen Komponentenprüfung und Gesamtfahrzeugverhalten, Rolle der V4.2-Diagnosebotschaften und die Kostenschranke werden klar erfasst.',
      register:
        'E-Mail und Call wahren konsequent diplomatisch-juristische Zurückhaltung ohne Vorverurteilung („ohne Schuldzuweisung“, „zur gemeinsamen Eingrenzung“).',
      spektrum:
        'Präzisionsausdrücke wie „eine Verantwortung zurückweisen“, „einen Vorgang präjudizieren“, „eine Schnittstelle eingrenzen“, „auf eine Vergleichsmessung verweisen“; saubere Konjunktiv-I-Wiedergabe.',
      interaktion:
        'Schuldunterstellung wird sachlich entkräftet; das Missverständnis zur Vertragsstrafe wird freundlich ausgeräumt; fehlende Kapazität führt zu pragmatischer Arbeitsteilung; die vorzeitige Freigabebestätigung wird begründet abgelehnt.',
      mediation:
        'Der kaufmännische Leiter erhält eine klare Entscheidungsgrundlage ohne technischen Jargon und ohne unbelegte Vertragsbehauptungen.',
      extra:
        'Pragmatische Deeskalation: Gemeinsame Datenbasis wird konsequent vor formale Eskalation gestellt.',
    }),
  }),
];

const coachingGraduation: AssessmentTask[] = [
  makeAssessment({
    id: 'unseen-coaching:c1',
    stream: 'coaching',
    context: 'COACHING',
    title: 'U15-Spielzeit, Leistungsdruck und Elternabend: Vereinskonzept und Klärungsgespräch',
    reading: `Rundschreiben der Jugendleitung · SV Neckarwiese (Übungstext)

In den vergangenen Wochen haben sich mehrere Eltern unserer U15-Mannschaft (C-Jugend) nach den Einsatzzeiten in der Bezirksstaffel erkundigt. Anlass waren zwei enge Auswärtsspiele, bei denen drei Wechselspieler in der zweiten Halbzeit jeweils nur zehn bis fünfzehn Minuten eingesetzt wurden, während sieben Stammspieler nahezu durchspielten.

Unser Jugendkonzept hält zwei Grundsätze fest, die nicht immer leicht zu verbinden sind: Erstens sollen alle zuverlässig trainierenden Jugendlichen einer Altersklasse über die Saison hinweg verlässliche Spielpraxis sammeln, um sich sportlich und sozial weiterzuentwickeln. Zweitens verlangt die Bezirksstaffel im 11-gegen-11 taktische Stabilität, weshalb ein Trainer in der Schlussphase nicht beliebig viele Positionen gleichzeitig wechseln kann, ohne die Ordnung im Verbund zu gefährden.

Ein festes Minutenversprechen pro Einzelspiel gibt der Verein bewusst nicht. Stattdessen gilt eine Saison-Richtlinie: Wer regelmäßig am Training teilnimmt, soll über jeweils vier Spieltage betrachtet auf mindestens eine volle Spielhälfte im Durchschnitt kommen – entweder durch Startelf-Einsätze in Ausgleichsspielen oder durch strukturierte Wechselblöcke in der zweiten Halbzeit. Bei Rückfragen entscheiden die sportlichen Kriterien Trainingsbeteiligung, Trainingsverhalten und gesundheitliche Belastbarkeit, nicht jedoch Einzelkritik an Mitspielern.

Die Jugendleitung lädt deshalb am Donnerstag um 19:30 Uhr zu einem kurzen Elternaustausch ins Vereinsheim ein. Ziel ist es, den Unterschied zwischen Einzelspiel-Taktik und mittelfristiger Entwicklungsplanung transparent zu erläutern und klare Ansprechwege zu vereinbaren.`,
    listening: `Mutter Frau Seidel: Trainer, darf ich Sie nach dem Training kurz unter vier Augen sprechen? Mein Sohn Leon saß am Samstag fünfzig Minuten auf der Bank. Er überlegt schon, ob sich die Fahrerei dreimal die Woche noch lohnt.
Trainer Hofstetter: Gut, dass Sie das direkt ansprechen, Frau Seidel. Mir ist wichtig, dass Leon weiß, woran er ist. Am Samstag hatten wir ab der sechzigsten Minute eine enge 1:0-Führung zu verteidigen, da konnte ich in der Viererkette nicht zweimal gleichzeitig umstellen.
Seidel: Das verstehe ich ja fürs Ergebnis, aber andere Jungs spielen gefühlt immer durch, egal wie sie trainieren.
Hofstetter: Genau diesen Eindruck möchte ich ungern stehen lassen. Über die letzten vier Spiele gerechnet hatte Leon zwei Startelf-Einsätze und kam im Schnitt auf rund fünfzig Minuten pro Partie. Aber ich nehme ernst, dass sich das am Samstag für ihn anders angefühlt hat.
Seidel: Er war vor allem enttäuscht, weil niemand nach dem Spiel mit ihm gesprochen hat.
Hofstetter: Da haben Sie einen berechtigten Punkt – das war nach dem Abpfiff zu hektisch, da hätte ich am Dienstag direkt ein kurzes Feedback geben müssen. Konkret sehe ich bei Leon aktuell zwei Schwerpunkte: Sein Passspiel unter Druck ist richtig stark geworden, beim Nachrücken im Umschalten verliert er nach Ballverlust manchmal noch zwei Sekunden.
Seidel: Das sage ich ihm bestimmt nicht so, sonst denkt er, ich mische mich ein.
Hofstetter: Das übernehme selbstverständlich ich morgen vor der Übung persönlich mit Leon. Und beim Elternabend am Donnerstag legen wir für alle offen, wie wir die Einsatzzeiten über vier Wochen planen.`,
    hoeren:
      'Hören (zweimal, Notizen erlaubt). Beantworten Sie in ganzen Sätzen: (1) Wie erklärt der Trainer die geringe Einsatzzeit am Samstag, ohne Leons Enttäuschung abzutun? (2) Welchen eigenen Versäumnispunkt räumt er gegenüber Frau Seidel offen ein? (3) Wie trennt er Rückmeldung zur Leistung von Aufgaben der Eltern? (4) Welche zwei konkreten Schritte folgen aus dem Gespräch?',
    lesen:
      'Lesen Sie das Rundschreiben gründlich: (1) Welcher Zielkonflikt besteht in der C-Jugend zwischen Ausbildungsanspruch und Wettkampf? (2) Worin unterscheidet sich die „Saison-Richtlinie über vier Spieltage“ von einem festen Minutenversprechen pro Spiel? (3) Welche drei Kriterien sind für Einsatzzeiten maßgeblich, und was wird ausdrücklich ausgeschlossen? (4) Was lässt das Rundschreiben über konkrete Nachholtermine für weniger eingesetzte Spieler noch offen?',
    schreiben:
      'Schreiben Sie 220–260 Wörter: Als Co-Trainerin oder Co-Trainer verfassen Sie nach Rücksprache mit der Jugendleitung eine sachliche, wertschätzende Einladungsmail an die U15-Eltern für den Donnerstagabend. Erklären Sie kurz den Anlass (Rückfragen zu Einsatzzeiten), das Grundprinzip der Vier-Spiele-Betrachtung sowie verbindliche Gesprächsregeln (keine Diskussion über einzelne Mitspieler, vertrauliche Einzelgespräche nach Termin). Ton: respektvoll, transparent, auf Augenhöhe.',
    sprechen:
      'Spontan (höchstens 60 Sekunden Gedankensammeln), etwa drei Minuten: Wie erklärt man Jugendlichen und Eltern in einem leistungsorientierten Breitensportverein fair, warum Einsatzzeiten schwanken können, ohne Kinder zu demotivieren? Strukturieren Sie nach pädagogischer Haltung, konkreter Gesprächsführung und Grenzen der Trainerrolle.',
    interaktion:
      'Elterngespräch am Spielfeldrand (prüfende Person spielt einen aufgebrachten Vater). Geplante Wendungen: (1) Er unterbricht mit „Sie bevorzugen doch nur Ihre Lieblingsspieler!“. (2) Er behauptet, im Rundschreiben stehe eine garantierte Spielzeit von 50 % in jedem Spiel (Missverständnis). (3) Er vergleicht seinen Sohn namentlich abwertend mit einem Mitspieler. (4) Er droht mit sofortiger Abmeldung, falls sein Sohn am Wochenende nicht startet. Bleiben Sie ruhig, weisen Sie Vergleiche mit Dritten freundlich zurück und vereinbaren Sie ein Entwicklungsgespräch mit dem Spieler.',
    mediation:
      'Ein vierzehnjähriger Spieler der U15, dessen Eltern kaum Deutsch sprechen, hat Angst, wegen der Diskussion am Donnerstag aus der Mannschaft gestrichen zu werden. Erklären Sie ihm in 2–3 Minuten ruhig, verständlich und ohne Vorwürfe, worum es beim Elternabend geht, was das Konzept für ihn bedeutet und wie er selbst im Training aktiv zeigen kann, was er verbessern möchte.',
    checklist: makeReviewerChecklist({
      verstehen:
        'Spannungsfeld Entwicklungsprinzip vs. taktische Stabilität im 11-gegen-11 sowie die 4-Spieltage-Betrachtung werden präzise erfasst; der eigene Kommunikationsfehler des Trainers im Hörtext wird benannt.',
      register:
        'Elternmail respektvoll-klar und professionell ohne Bürokratendeutsch; Gespräch mit dem Jugendlichen altersgerecht, ermutigend und direkt.',
      spektrum:
        'Pädagogisch-taktische Wendungen wie „vertrauensvoll ansprechen“, „Spielpraxis sammeln“, „auf Augenhöhe rückmelden“, „den Verbund nicht gefährden“, „Kriterien offenlegen“; sichere Abgrenzung ohne Angriff.',
      interaktion:
        'Vorwurf der Bevorzugung wird ruhig zurückgewiesen; das Missverständnis (pro Einzelspiel vs. über 4 Spieltage) wird belegt geklärt; abwertende Vergleiche mit Mitspielern werden konsequent unterbunden; die Startelf-Forderung wird nicht versprochen.',
      mediation:
        'Dem Jugendlichen wird ohne Fachjargon die Angst genommen; die Kriterien werden handlungsorientiert erklärt; sein Verständnis wird geprüft.',
      extra:
        'Rollenschutz im Ehrenamt: Vertraulichkeit über Dritte wird konsequent gewahrt.',
    }),
  }),
  makeAssessment({
    id: 'unseen-coaching:c2',
    stream: 'coaching',
    context: 'COACHING',
    title: 'U13-Trainingsumstellung auf kleine Spielformen & Fairplay nach hitzigem Spiel',
    reading: `Notiz für das Jugendtrainer-Team · Übergang E- zur D-Jugend (Übungsfall)

Nach der Umstellung unserer U12/U13-Trainingseinheiten von langen Passschlangen auf parallele Drei-gegen-Drei- und Vier-gegen-Vier-Felder mit Provokationsregeln melden zwei Co-Trainer gemischte Rückmeldungen: Einerseits hätten deutlich mehr Kinder den Ball am Fuß und träfen eigenständige Entscheidungen unter Gegnerdruck. Andererseits wirkten einige Übungsphasen lauter und hektischer, weil Tore schnell fallen und Verlierer-Teams mitunter frustriert reagieren.

Zudem kam es beim letzten Turnier zu einer unnötigen Szene an der Seitenlinie: Nach einem strittigen Einwurf rief ein Zuschauer wiederholt in Richtung des zwölfjährigen Jungschiedsrichters, woraufhin zwei eigene Spieler das Spielen einstellten und lautstark reklamierten.

Das Trainerteam möchte für das nächste Training zweierlei schärfen: Erstens müssen die Spielformen sprachlich klarer eingeführt werden – ein kurzes Bewegungsziel vorab, eine klare Fehler- und Neustartregel sowie gezielte Coaching-Pausen statt Dauerrufen von außen. Zweitens braucht die Mannschaft eine verbindliche Fairplay-Routine, damit Jugendliche Konflikte nach Pfiffen nicht an der Seitenlinie austragen.`,
    listening: `Haupttrainerin Yasmin: Lukas, wie liefen denn gestern die vier Felder im Drei-gegen-Drei auf Minitore?
Co-Trainer Lukas: Ehrlich gesagt intensiv, aber nach zehn Minuten gab es beim zweiten Feld Geschrei, weil jeder alles selbst lösen wollte und drei Jungs nach Gegentoren stehen geblieben sind.
Yasmin: Woran lag das deiner Meinung nach?
Lukas: Ich glaube, meine Ansage vorab war zu ungenau. Ich hab gesagt „Macht Dampf und spielt schnell“, und danach war ich auf drei Feldern gleichzeitig und habe ständig reingerufen.
Yasmin: Genau da können wir ansetzen. „Macht Dampf“ gibt den Kindern kein klares Bild, was sie tun sollen. Wenn du stattdessen sagst: „Nach Pass sofort neuen Winkel öffnen – und bei Ballaus läuft der nächste Ball binnen drei Sekunden ein“, nimmst du die Diskussion raus.
Lukas: Und was machen wir mit dem Gemecker bei knappen Ausbällen? Das schaukelt sich seit dem Turnier mit dem Zuschauerruf hoch.
Yasmin: Wir üben nächste Woche zweimal bewusst eine 5-Sekunden-Regel nach Pfiffen oder strittigen Bällen: Kein Reklamieren, Entscheidung akzeptieren und sofort umschalten. Und beim nächsten Elternkurztreff vor Anpfiff bitte ich dich, die Fairplay-Zone freundlich, aber unmissverständlich anzusprechen – ohne Einzelne an den Pranger zu stellen.
Lukas: Einverstanden. Ich bereite bis Mittwoch die drei kurzen Ansagen für die Felder vor und lass sie die Kinder selbst wiederholen, bevor es losgeht.`,
    hoeren:
      'Hören (zweimal, Notizen erlaubt). Beantworten Sie in ganzen Sätzen: (1) Warum beurteilt Yasmin den Zuruf „Macht Dampf und spielt schnell“ kritisch? (2) Welche konkreten Veränderungen schlägt sie für Coaching-Ansagen und Spieltempo vor? (3) Wie will das Team auf das Reklamieren nach strittigen Entscheidungen reagieren? (4) Was vereinbaren Yasmin und Lukas für das nächste Elternkurztreff?',
    lesen:
      'Lesen Sie den Übungsfall gründlich: (1) Welche positiven Effekte und welche Nebeneffekte der kleinen Spielformen werden beobachtet? (2) Welcher Zusammenhang besteht zwischen dem Verhalten eines Zuschauers und der Reaktion auf dem Feld? (3) Erklären Sie den Unterschied zwischen „Dauerrufen von außen“ und „gezielten Coaching-Pausen“. (4) Welche drei Bestandteile soll jede Trainingsansage künftig enthalten?',
    schreiben:
      'Schreiben Sie einen strukturierten Trainingsleitfaden (220–260 Wörter) für neue ehrenamtliche Co-Trainerinnen und Co-Trainer Ihrer U13. Erklären Sie darin am Beispiel einer konkreten Drei-gegen-Drei-Spielform, wie man (a) ein klares positives Handlungsziel formuliert, (b) das Verständnis der Kinder kurz prüft, (c) Neustarts nach Ballaus organisiert und (d) in Coaching-Pausen ruhiges Feedback gibt, statt negativ dazwischenzurufen.',
    sprechen:
      'Spontan (höchstens 60 Sekunden Gedankensammeln), etwa drei Minuten: Warum wirken kurze, positive Handlungsziele auf dem Jugendfußballplatz nachhaltiger als Daueranweisungen oder negative Verbote? Geben Sie konkrete Sprachbeispiele für E- oder C-Jugend und schildern Sie, wie Sie mit eigenen hektischen Momenten am Spielfeldrand umgehen.',
    interaktion:
      'Gespräch direkt nach einem Turnierspiel (prüfende Person spielt einen engagierten Zuschauer, der meint, Sie würden die Kinder durch Schweigen am Rand zu wenig antreiben und der Schiedsrichter habe „das Spiel verpfiffen“). Geplante Wendungen: (1) Er unterbricht mit „Wenn Sie nichts reinrufen, schlafen die doch ein!“. (2) Er unterstellt, Kinder bräuchten ständige Kommandos wie bei Profis. (3) Er verlangt, dass Sie den Jungschiedsrichter beim Staffelleiter melden. Halten Sie professionell die Fairplay-Grenze, würdigen Sie sein Interesse am Team und schließen Sie mit einer konstruktiven Vereinbarung.',
    mediation:
      'Ein zehnjähriger neuer Spieler versteht beim ersten Training die Begriffe „Provokationsregel“, „Winkel öffnen“ und „Fairplay-Zone“ nicht und traut sich kaum mitzuspielen. Erklären Sie ihm in 2–3 Minuten kindgerecht, ermutigend und ohne Fachjargon, was beim Drei-gegen-Drei passiert, wann er losspielen darf und worauf er nach einem Gegentor achten soll.',
    checklist: makeReviewerChecklist({
      verstehen:
        'Vor- und Nachteile kleiner Spielformen, die Rolle präziser Vorab-Ansagen sowie die Eskalationskette beim Jungschiedsrichter-Vorfall werden vollständig erfasst.',
      register:
        'Leitfaden für Co-Trainer kooperativ und praxisnah; Zuschauergespräch höflich-bestimmt; Erklärung für den Zehnjährigen warmherzig und einfach.',
      spektrum:
        'Positive Handlungsformulierungen („Winkel öffnen“, „Verständnis prüfen“, „entscheidungsoffen anleiten“, „Deeskalation an der Seitenlinie“) statt negativer Imperative.',
      interaktion:
        'Forderung nach Dauerrufen wird fachlich-freundlich eingeordnet; die Schiedsrichterkritik wird sachlich abgegrenzt, ohne den Zuschauer bloßzustellen; der Vorbildgedanke bleibt leitend.',
      mediation:
        'Unbekannte Konzepte werden mit anschaulichen Spielbildern erklärt; aktives Verständnis wird abgefragt; der Ton nimmt Druck vom Kind.',
      extra:
        'Sprachpädagogische Konsistenz: Keine negativen Verbote in den Beispielkommandos für Jugendspieler.',
    }),
  }),
];

export const assessmentBank: AssessmentTask[] = [
  ...generalGraduation,
  ...socialGraduation,
  ...engineeringGraduation,
  ...coachingGraduation,
];
