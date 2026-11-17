import type {
  BookBridgePractice,
  ExternalListeningSource,
  LessonListeningRecommendation,
  OwnedDialogueBrief,
  RecordingRequirement,
} from '../types';

export const externalListeningSources: ExternalListeningSource[] = [
  {
    id: 'easy-german',
    title: 'Easy German · Podcast-Folgen',
    url: 'https://www.easygerman.org/podcast/episodes',
    formats: 'Gespräche, Straßeninterviews und Erklärungen',
    focus: 'Natürliches Deutsch, spontane Reaktionen, Sprecherwechsel und alltagsnahe Themen.',
    transcriptNote:
      'Folgen und Zusatzmaterial öffnen beim Anbieter; interaktive Transkripte oder Untertitel können eingeschränkt oder kostenpflichtig sein.',
    practiceTasks: [
      'Wählen Sie eine Folge und hören Sie zunächst zwei bis fünf Minuten ohne Text mit. Notieren Sie Thema, Haltung und einen Beleg.',
      'Hören Sie denselben Abschnitt erneut. Notieren Sie zwei tatsächlich gehörte Gesprächsmarker und prüfen Sie erst jetzt verfügbare Untertitel.',
    ],
  },
  {
    id: 'dw-deutsch-lernen',
    title: 'DW · Deutsch lernen',
    url: 'https://learngerman.dw.com/de/deutsch-lernen/s-9095',
    formats: 'Lernbeiträge, Nachrichten und Audio-/Videokurse',
    focus: 'Strukturiertes Hörverstehen mit Angeboten bis C1.',
    transcriptNote: 'Lernhilfen und Transkripte unterscheiden sich je nach Beitrag.',
    practiceTasks: [
      'Wählen Sie einen Beitrag nahe C1 und notieren Sie Hauptaussage, Belege und Einschränkungen.',
      'Prüfen Sie erst nach dem ersten Hörversuch, welche Wörter oder Zahlen Sie verwechselt haben.',
    ],
  },
  {
    id: 'deutschlandfunk-podcasts',
    title: 'Deutschlandfunk · Der Tag',
    url: 'https://www.deutschlandfunk.de/podcast-104.html',
    formats: 'Interviews, Diskussionen, Reportagen und Nachrichten',
    focus: 'Längere zusammenhängende Beiträge, Argumente und unterschiedliche Positionen.',
    transcriptNote: 'Begleittexte oder Transkripte sind nicht für jede Sendung verfügbar.',
    practiceTasks: [
      'Fassen Sie die Positionen von zwei Sprechenden getrennt zusammen.',
      'Halten Sie eine offene Frage, eine Einschränkung und eine implizite Schlussfolgerung fest.',
    ],
  },
  {
    id: 'ard-sounds',
    title: 'ARD Sounds',
    url: 'https://www.ardsounds.de/',
    formats: 'Podcasts, Gespräche, Reportagen und Dokumentationen',
    focus: 'Authentische Sprecherwechsel und längere Alltags- und Sachthemen; die ARD-Mediathek führt direkt zu einzelnen Folgen.',
    transcriptNote: 'Transkripte und Zusatzmaterial variieren nach Sendung.',
    practiceTasks: [
      'Wählen Sie einen Beitrag und einen Abschnitt von zwei bis fünf Minuten. Skizzieren Sie ohne Transkript den Gesprächsverlauf.',
      'Hören Sie erneut und trennen Sie wörtliche Aussage, hörbaren Beleg, Haltung und eigene Vermutung.',
    ],
  },
  {
    id: 'swr-aktuell',
    title: 'SWR Aktuell & SWR Kultur · Wissen',
    url: 'https://www.swr.de/swrkultur/wissen/index.html',
    formats: 'Südwestdeutsche Regionalberichte, Reportagen und Wissens-Features',
    focus: 'Alltags- und Industrienähe im Südwesten (Mobilität, Verein, Handwerk) mit feinen regionalen Sprachfärbungen.',
    transcriptNote: 'Begleitartikel enthalten oft Kernaussagen, ersetzen aber kein wörtliches Transkript.',
    practiceTasks: [
      'Notieren Sie im ersten Durchgang drei Beobachtungen aus einem O-Ton und markieren Sie, ob ein Sprecher vermutet oder belegt.',
      'Vergleichen Sie süddeutsche Gesprächswendungen (z. B. „halt“, „eben“, „eigentlich“) mit deren Funktion im Kontext.',
    ],
  },
  {
    id: 'dlf-kultur',
    title: 'Deutschlandfunk Kultur · Wortwechsel & Thema',
    url: 'https://www.deutschlandfunkkultur.de/',
    formats: 'Feuilleton-Debatten, Streitgespräche und gesellschaftliche Analysen',
    focus: 'Differenzierte Meinungsäußerung, Ironie, implizite Voraussetzungen und höflicher Widerspruch.',
    transcriptNote: 'Einzelne Sendungen bieten Zusammenfassungen oder Manuskripte.',
    practiceTasks: [
      'Identifizieren Sie zwei ungenannte Prämissen, auf denen das Argument eines Gastes aufbaut.',
      'Formen Sie eine kontroverse Position mündlich neutral für Außenstehende um (Mediation).',
    ],
  },
];

export const lessonListeningEpisodes: LessonListeningRecommendation[] = [
  {
    unitId: 's0-diagnose',
    title: 'DW · Deutsch lernen · Aktuelle Kurzreportagen (C1)',
    url: 'https://learngerman.dw.com/de/deutsch-lernen/s-9095',
    task: 'Höre einen 3-Minuten-Beitrag einmal ohne Text. Notiere zuerst nur Hauptaussage und Zahlen; prüfe beim zweiten Hören, wo Verstehen sicher war und wo Lücken blieben.',
  },
  {
    unitId: 's1-explain',
    title: 'SWR Wissen · Wie komplexe Abläufe verständlich werden',
    url: 'https://www.swr.de/swrkultur/wissen/index.html',
    task: 'Achte bei einer Erklärpassage darauf, wie Zeitfolge („zunächst“, „erst wenn“) von Zweck und Bedingung sprachlich getrennt wird.',
  },
  {
    unitId: 's1-justify',
    title: 'Deutschlandfunk · Der Tag · Abwägung unter Zielkonflikten',
    url: 'https://www.deutschlandfunk.de/podcast-104.html',
    task: 'Notiere zu einer Kontroverse die Kriterien beider Seiten, einen echten Einwand und die Bedingung, unter der eine Empfehlung geändert werden müsste.',
  },
  {
    unitId: 's1-uncertainty',
    title: 'Deutschlandfunk · Forschung aktuell · Offene Ursachen',
    url: 'https://www.deutschlandfunk.de/podcast-104.html',
    task: 'Filtere drei Marker für Unsicherheit („dürfte“, „lässt sich noch nicht ableiten“, „deutet darauf hin“) und prüfe, ob ein Prüfschritt genannt wird.',
  },
  {
    unitId: 's1-feedback',
    title: 'Easy German · Arbeitskultur & Rückmeldung im Alltag',
    url: 'https://www.easygerman.org/podcast/episodes',
    task: 'Beobachte in einem Straßeninterview oder Gespräch, wann Kritik konkretes Verhalten beschreibt und wann pauschale Urteile mitschwingen.',
  },
  {
    unitId: 's2-data',
    title: 'Easy German 709 · Unfreundlichkeitstrainings bei Aldi',
    url: 'https://www.easygerman.org/podcast/episodes/709',
    task: 'Höre den Abschnitt über KI-Rechenzentren. Trenne eine berichtete Behauptung, den dazu genannten Beleg und deine eigene Schlussfolgerung.',
  },
  {
    unitId: 's2-register',
    title: 'ARD Sounds · Zwischen Alltagston und Amts- bzw. Fachton',
    url: 'https://www.ardsounds.de/',
    task: 'Vergleiche einen lockeren Moderationseinstieg mit einem offiziellen O-Ton: Welche Reduktionen verschwinden im formellen Register?',
  },
  {
    unitId: 's3-negotiate',
    title: 'Deutschlandfunk Kultur · Kompromiss & Interessenlage',
    url: 'https://www.deutschlandfunkkultur.de/',
    task: 'Unterscheide bei zwei Streitparteien die harte Forderung (Position) vom dahinterliegenden Bedürfnis (Interesse).',
  },
  {
    unitId: 's3-disagree',
    title: 'SWR Aktuell · Höflicher Widerspruch in Regionaldebatten',
    url: 'https://www.swr.de/swrkultur/wissen/index.html',
    task: 'Notiere drei Einstiegsformeln, mit denen Gesprächspartner widersprechen, ohne die Beziehung zu beschädigen.',
  },
  {
    unitId: 's4-argument',
    title: 'Easy German 703 · In der Küche wird Politik gemacht',
    url: 'https://www.easygerman.org/podcast/episodes/703',
    task: 'Notiere je ein Argument für und gegen ein Parteiverbotsverfahren. Halte anschließend fest, welche Information für deine eigene Abwägung noch fehlt.',
  },
  {
    unitId: 's4-ethics',
    title: 'Easy German 701 · Aktivismus in Deutschland',
    url: 'https://www.easygerman.org/podcast/episodes/701',
    task: 'Fasse die Perspektive des Gastes und eine im Gespräch genannte gesellschaftliche Spannung getrennt zusammen.',
  },
  {
    unitId: 's5-nuance',
    title: 'Easy German 702 · Dialekt oder Akzent?',
    url: 'https://www.easygerman.org/podcast/episodes/702',
    task: 'Notiere, wie die Gesprächspartner Dialekt und Akzent unterscheiden. Prüfe, welche Beispiele sie tatsächlich nennen und was offenbleibt.',
  },
  {
    unitId: 's5-facilitate',
    title: 'Deutschlandfunk · Streitkultur & Gesprächsleitung',
    url: 'https://www.deutschlandfunk.de/podcast-104.html',
    task: 'Beobachte, wie die Moderation unterbricht, Positionen spiegelt und leise Stimmen oder überhörte Einwände zurückholt.',
  },
  {
    unitId: 's0-profile',
    title: 'DW · Alltagsdeutsch · Sprachziele und Gesprächserwartungen',
    url: 'https://learngerman.dw.com/de/deutsch-lernen/s-9095',
    task: 'Höre einen kurzen Berufs- oder Vereinsbeitrag und formuliere daraus zwei konkrete eigene Sprachziele mit überprüfbarem Erfolgskriterium.',
  },
  {
    unitId: 's0-repair',
    title: 'Easy German · Missverständnisse höflich aufklären',
    url: 'https://www.easygerman.org/podcast/episodes',
    task: 'Achte im Gespräch auf spontane Rückfragen („Meinst du damit…?“, „Verstehe ich richtig…?“). Notiere drei Formen, die ohne Vorwurf klären.',
  },
  {
    unitId: 's0-retrieve',
    title: 'Deutschlandfunk · Kurz nachgefragt (Freie Wiedergabe)',
    url: 'https://www.deutschlandfunk.de/podcast-104.html',
    task: 'Höre zwei Minuten einmal an, pausiere und gib die Kernpunkte ohne Notizen mündlich wieder; vergleiche erst danach mit dem zweiten Durchgang.',
  },
  {
    unitId: 's2-summarise',
    title: 'Deutschlandfunk · Hintergrund · Mehrere Quellen bündeln',
    url: 'https://www.deutschlandfunk.de/podcast-104.html',
    task: 'Verdichte zwei unterschiedliche Positionen eines Beitrags auf höchstens vier Sätze, ohne Vorbehalte oder Zahlenbereiche zu glätten.',
  },
  {
    unitId: 's2-mediate',
    title: 'DLF Kultur · Wortwechsel · Zwischen Lagern vermitteln',
    url: 'https://www.deutschlandfunkkultur.de/',
    task: 'Gib eine zugespitzte Aussage beider Seiten neutral in indirekter Rede (Konjunktiv I) für eine außenstehende Person wieder.',
  },
  {
    unitId: 's3-deescalate',
    title: 'SWR Aktuell · Hitzige Debatten sachlich einfangen',
    url: 'https://www.swr.de/swrkultur/wissen/index.html',
    task: 'Achte darauf, wie ein Moderator emotionale Zuspitzung anerkennt, ohne Vorwürfe als Tatsachen zu übernehmen.',
  },
  {
    unitId: 's3-agree',
    title: 'Easy German · Verbindliche Absprachen im Alltag',
    url: 'https://www.easygerman.org/podcast/episodes',
    task: 'Notiere, wann ein „Schauen wir mal“ vage bleibt und durch welche Nachfrage Zuständigkeit und Termin konkret werden.',
  },
  {
    unitId: 's4-challenge',
    title: 'DLF Kultur · Medienkritik & unausgesprochene Prämissen',
    url: 'https://www.deutschlandfunkkultur.de/',
    task: 'Identifiziere bei einer Kommentatorin zwei implizite Voraussetzungen, die nicht explizit belegt wurden.',
  },
  {
    unitId: 's4-present',
    title: 'SWR Kultur Wissen · Komplexes verständlich vortragen',
    url: 'https://www.swr.de/swrkultur/wissen/index.html',
    task: 'Analysiere den Aufbau eines 4-Minuten-Vortrags: Einstiegsthese, zwei Begründungslinien, ausdrückliche Einschränkung.',
  },
  {
    unitId: 's5-spontaneous',
    title: 'Easy German · Spontane Reaktionen ohne Vorbereitung',
    url: 'https://www.easygerman.org/podcast/episodes',
    task: 'Pausiere nach einer unerwarteten Straßenfrage und antworte selbst 20 Sekunden laut, bevor du die echte Reaktion weiterhörst.',
  },
  {
    unitId: 's5-revise',
    title: 'DLF Kultur · Stil, Präzision & Adressatenbezug',
    url: 'https://www.deutschlandfunkkultur.de/',
    task: 'Wähle drei umgangssprachliche oder vage Wendungen aus einem O-Ton und überarbeite sie für ein formelles Protokoll.',
  },
  {
    unitId: 's6-specialist',
    title: 'Deutschlandfunk · Forschung aktuell · Fachthema für Laien',
    url: 'https://www.deutschlandfunk.de/podcast-104.html',
    task: 'Übersetze einen dreiminütigen Fachbericht in klare Alltagssprache, inklusive expliziter Nennung dessen, was die Studie nicht zeigt.',
  },
  {
    unitId: 's6-retention',
    title: 'ARD Sounds · Zweithören nach Abstand ohne Skript',
    url: 'https://www.ardsounds.de/',
    task: 'Höre einen Themenkomplex zu Mobilität oder Vereinsleben ohne Vorbereitung und baue deine eigene Stellungnahme frei mit C1-Konnektoren auf.',
  },
  {
    unitId: 's6-checkpoint',
    title: 'DW C1 & DLF · Selbsteinschätzung am Originalton prüfen',
    url: 'https://learngerman.dw.com/de/deutsch-lernen/s-9095',
    task: 'Höre eine anspruchsvolle Passage einmal blind und markiere ehrlich zwei Stellen, an denen Detailverstehen sicher war und zwei, die vage blieben.',
  },
  {
    unitId: 's6-general',
    title: 'ARD Sounds · Unvorbereitete Langform-Reportage (C1)',
    url: 'https://www.ardsounds.de/',
    task: 'Höre fünf Minuten zu einem neuen Thema ohne Vorwissen. Rekonstruiere danach frei Kernaussage, zwei Belege und eine offene Frage.',
  },
];

export const lessonBookBridges: BookBridgePractice[] = [
  {
    unitId: 's1-explain',
    bookReference: 'Sicher! C1/1 · Lektion 1 „Reisen und Mobilität“ · Arbeitsbuch AB 10–18',
    audioFilenameHint: 'Im Ordner „audio AB“ tragen passende Dateien _L01_ im Namen.',
    originalPrompt:
      'Erklären Sie einer neuen Kollegin einen mehrstufigen Organisationsablauf aus Ihrem Alltag in klarer Reihenfolge („zunächst“, „erst wenn“, „anschließend“). Prüfen Sie am Ende mit einer konkreten Rückfrage, ob die Fehlerregel verstanden wurde.',
  },
  {
    unitId: 's1-feedback',
    bookReference: 'Aspekte neu C1 · Kapitel 1 „Wohnwelten & Miteinander“ · Kursbuch S. 8–23',
    audioFilenameHint:
      'Wähle die Lehrbuch-CD-Datei anhand der im Kursbuch beim Hörtext gedruckten Tracknummer.',
    audioReference: 'Lehrbuch-CD 1 · Ordner „CD 1 K 1-3“; Tracknummer im Kursbuch abgleichen.',
    originalPrompt:
      'Hören Sie ein Konflikt- oder Alltagsgespräch aus dem Kapitel an. Formulieren Sie danach eine eigene Rückmeldung nach dem Muster Beobachtung → beobachtete Wirkung → verhandelbare Bitte ohne Charakterurteil.',
  },
  {
    unitId: 's2-data',
    bookReference: 'Sicher! C1/1 · Lektion 3 „Wissenschaft und Technik“ · Kursbuch S. 37–48',
    audioFilenameHint: 'Im Ordner „audio KB/AB“ tragen passende Dateien _L03_ im Namen.',
    originalPrompt:
      'Entnehmen Sie einer Grafik oder einem Kurzbericht des Kapitels zwei konkrete Befunde. Formulieren Sie anschließend schriftlich, welche Ursache aus den Zahlen allein noch nicht abgeleitet werden darf.',
  },
  {
    unitId: 's3-negotiate',
    bookReference: 'Sicher! C1/1 · Lektion 2 „Im Tourismus“ · Kursbuch S. 25–36, Arbeitsbuch AB 25–40',
    audioFilenameHint: 'Im Ordner „audio AB“ tragen passende Dateien _L02_ im Namen.',
    originalPrompt:
      'Eine Stadt möchte Besucher auf weniger bekannte Viertel verteilen. Verhandeln Sie mit einem lokalen Anbieter über ein wetterfestes Wochenendangebot. Berücksichtigen Sie Erreichbarkeit, Kosten und die Belastung der Anwohner; nennen Sie eine Bedingung, unter der Sie den Vorschlag ändern würden.',
  },
  {
    unitId: 's3-disagree',
    bookReference: 'Sicher! C1/1 · Lektion 4 „Arbeitswelt & Team“ · Kursbuch S. 49–60',
    audioFilenameHint: 'Im Ordner „audio AB“ tragen passende Dateien _L04_ im Namen.',
    originalPrompt:
      'Greifen Sie eine Position aus dem Kapitel auf, die Ihnen zu einseitig erscheint. Würdigen Sie zunächst den berechtigten Kern und formulieren Sie dann mit zwei Kriterien einen gesichtswahrenden Widerspruch.',
  },
  {
    unitId: 's4-argument',
    bookReference: 'Sicher! C1/1 · Lektion 5 „Kunst“ · Kursbuch S. 61–72, Arbeitsbuch AB 73–88',
    audioFilenameHint: 'Im Ordner „audio AB“ tragen passende Dateien _L05_ im Namen.',
    originalPrompt:
      'Ein Stadtteil kann entweder ein neues Wandbild finanzieren oder ein verwittertes historisches Schild restaurieren. Begründen Sie eine Empfehlung mit Zugänglichkeit, Erhalt und Kosten. Kennzeichnen Sie Annahmen, würdigen Sie das stärkste Gegenargument und schlagen Sie eine überprüfbare nächste Maßnahme vor.',
  },
  {
    unitId: 's2-mediate',
    bookReference: 'Sicher! C1/1 · Lektion 6 „Studium“ · Kursbuch S. 73–84, Arbeitsbuch AB 89–104',
    audioFilenameHint: 'Im Ordner „audio AB“ tragen passende Dateien _L06_ im Namen.',
    originalPrompt:
      'Erklären Sie einer berufstätigen Person die Anforderungen und den Wochenplan zweier Studienangebote, die Sie selbst recherchiert haben. Trennen Sie gesicherte Angaben von offenen Fragen und leiten Sie keine unbelegten Aussagen über Zulassungschancen ab.',
  },
  {
    unitId: 's5-nuance',
    bookReference: 'Aspekte neu C1 · Kapitel 2 „Hast du Worte?“ · Kursbuch S. 24–39',
    audioFilenameHint:
      'Wähle die Lehrbuch-CD-Datei anhand der im Kursbuch beim Hörtext gedruckten Tracknummer.',
    audioReference: 'Lehrbuch-CD 1 · Ordner „CD 1 K 1-3“; die Tracknummer bitte im Kursbuch abgleichen.',
    originalPrompt:
      'Höre eine selbst ausgewählte Passage über Sprachgebrauch oder Dialekt blind an. Notiere die wörtlich verstandene Aussage, eine mögliche Sprecherabsicht und eine offene Frage. Nach dem ersten Versuch darfst du das Buchtranskript zum Abgleich verwenden.',
  },
  {
    unitId: 's4-present',
    bookReference: 'Aspekte neu C1 · Kapitel 3 „An die Arbeit!“ · Kursbuch S. 40–55',
    audioFilenameHint:
      'Wähle die Lehrbuch-CD-Datei anhand der im Kursbuch beim Hörtext gedruckten Tracknummer.',
    audioReference: 'Lehrbuch-CD 1 · Ordner „CD 1 K 1-3“; die Tracknummer bitte im Kursbuch abgleichen.',
    originalPrompt:
      'Bereite einen kurzen freien Vortrag zu einer Arbeitswelt-Frage aus dem Kapitel vor. Formuliere These, zwei voneinander unabhängige Gründe, einen Vorbehalt und eine konkrete Schlussfolgerung. Trenne eigene Annahmen ausdrücklich von Informationen aus der Buchquelle.',
  },
  {
    unitId: 's3-agree',
    bookReference: 'Aspekte neu C1 · Kapitel 4 „Wirtschaftsgipfel“ · Kursbuch S. 56–71',
    audioFilenameHint:
      'Wähle die Lehrbuch-CD-Datei anhand der im Kursbuch beim Hörtext gedruckten Tracknummer.',
    audioReference: 'Lehrbuch-CD 2 · Ordner „CD 2 K 4-7“; die Tracknummer bitte im Kursbuch abgleichen.',
    originalPrompt:
      'Nutze einen Hörtext aus dem Kapitel als Grundlage für ein kurzes simuliertes Beratungsgespräch. Fasse zuerst das Anliegen neutral zusammen, stelle zwei präzise Rückfragen und schließe mit einer vorläufigen Vereinbarung samt ungeklärtem Punkt.',
  },
  {
    unitId: 's4-challenge',
    bookReference: 'Aspekte neu C1 · Kapitel 5 „Medien & Meinung“ · Kursbuch S. 72–87',
    audioFilenameHint:
      'Wähle die Lehrbuch-CD-Datei anhand der im Kursbuch beim Hörtext gedruckten Tracknummer.',
    audioReference: 'Lehrbuch-CD 2 · Ordner „CD 2 K 4-7“; Tracknummer im Kursbuch abgleichen.',
    originalPrompt:
      'Analysieren Sie einen Kommentar aus dem Kapitel: Welche unausgesprochene Annahme trägt die Hauptthese, und welche alternative Erklärung lässt der Text unerwähnt?',
  },
  {
    unitId: 's4-ethics',
    bookReference: 'Aspekte neu C1 · Kapitel 7 „Verantwortung & Forschung“ · Kursbuch S. 104–119',
    audioFilenameHint:
      'Wähle die Lehrbuch-CD-Datei anhand der im Kursbuch beim Hörtext gedruckten Tracknummer.',
    audioReference: 'Lehrbuch-CD 2 · Ordner „CD 2 K 4-7“; Tracknummer im Kursbuch abgleichen.',
    originalPrompt:
      'Wählen Sie ein Dilemma aus dem Kapitel und wägen Sie Nutzen, Risiko und fehlende Nachweise in höchstens 180 Wörtern ab, ohne eine Seite zu karikieren.',
  },
  {
    unitId: 's2-summarise',
    bookReference: 'Aspekte neu C1 · Kapitel 10 „Erinnerungen“ · Kursbuch S. 152–167',
    audioFilenameHint:
      'Wähle die Lehrbuch-CD-Datei anhand der im Kursbuch beim Hörtext gedruckten Tracknummer.',
    audioReference: 'Lehrbuch-CD 3 · Ordner „CD 3 K 8-10“; die Tracknummer bitte im Kursbuch abgleichen.',
    originalPrompt:
      'Fasse einen Hörtext des Kapitels in höchstens fünf Sätzen zusammen. Unterscheide die zentrale Aussage, einen genannten Beleg und eine Schlussfolgerung; markiere, wenn du eine Aussage nur als Vermutung wiedergibst.',
  },
  {
    unitId: 's0-profile',
    bookReference: 'Sicher! C1/1 · Einstieg & Lernplanung · Kursbuch S. 6–9',
    audioFilenameHint: 'Passende Einstiegs-Hörtexte über das eigene Lernziel im Kursbuch auswählen.',
    originalPrompt:
      'Höre ein kurzes Vorstellungs- oder Arbeitsgespräch aus dem Einstiegskapitel und formuliere anschließend zwei konkrete eigene C1-Ziele mit überprüfbarem Kontext.',
  },
  {
    unitId: 's0-diagnose',
    bookReference: 'Aspekte neu C1 · Auftaktmodul „Selbsteinschätzung & Hörverstehen“ · Kursbuch S. 6–7',
    audioFilenameHint: 'Lehrbuch-CD 1 · Einstiegstrack im Kursbuch abgleichen.',
    originalPrompt:
      'Höre einen unbekannten Kurztext einmal ohne mitzulesen. Trenne nach dem Hören ehrlich, was wörtlich verstanden wurde und wo Vermutungen Lücken füllten.',
  },
  {
    unitId: 's0-repair',
    bookReference: 'Sicher! C1/1 · Lektion 1 „Nachfragen & Klären“ · Arbeitsbuch AB 12–16',
    audioFilenameHint: 'Im Ordner „audio AB“ nach Dialogen mit _L01_ suchen.',
    originalPrompt:
      'Wähle einen Dialog mit Missverständnis und formuliere drei gesichtswahrende Klärungsfragen („Meinen Sie mit …?“, „Verstehe ich es richtig, dass …?“).',
  },
  {
    unitId: 's0-retrieve',
    bookReference: 'Aspekte neu C1 · Wiederholung & Aktivierung · Arbeitsbuch S. 18–21',
    audioFilenameHint: 'Audio nach einmaligem Hören pausieren und erst aus dem Gedächtnis notieren.',
    originalPrompt:
      'Schließe nach einem kurzen Hörtext das Buch und rekonstruiere Kernaussage und zwei Sprachmittel frei aus dem Gedächtnis, bevor du abgleichst.',
  },
  {
    unitId: 's1-justify',
    bookReference: 'Sicher! C1/1 · Lektion 2 „Argumentieren & Abwägen“ · Kursbuch S. 28–33',
    audioFilenameHint: 'Im Ordner „audio KB/AB“ tragen passende Dateien _L02_ im Namen.',
    originalPrompt:
      'Entnehmen Sie einer Diskussionsaufgabe des Kapitels zwei Kriterien, wägen Sie einen ernsthaften Einwand ab („zwar …, allerdings …“) und nennen Sie eine Revisionsbedingung.',
  },
  {
    unitId: 's1-uncertainty',
    bookReference: 'Aspekte neu C1 · Kapitel 6 „Vermutungen & Evidenz“ · Kursbuch S. 88–103',
    audioFilenameHint: 'Lehrbuch-CD 2 · Ordner „CD 2 K 4-7“; Tracknummer im Kursbuch abgleichen.',
    originalPrompt:
      'Hören Sie einen Bericht zu einer ungeklärten Ursache und trennen Sie gesicherte Beobachtung („fest steht“) von vorsichtiger Hypothese („könnte darauf hindeuten“).',
  },
  {
    unitId: 's2-register',
    bookReference: 'Aspekte neu C1 · Kapitel 2 „Sprachebenen & Register“ · Arbeitsbuch S. 26–35',
    audioFilenameHint: 'Lehrbuch-CD 1 · Ordner „CD 1 K 1-3“; Tracknummer im Arbeitsbuch abgleichen.',
    originalPrompt:
      'Übertragen Sie eine lockere mündliche Äußerung aus dem Kapitel in eine präzise, formelle Mitteilung ohne Informationsverlust.',
  },
  {
    unitId: 's3-deescalate',
    bookReference: 'Sicher! C1/1 · Lektion 4 „Konflikte sachlich lösen“ · Kursbuch S. 52–59',
    audioFilenameHint: 'Im Ordner „audio KB/AB“ tragen passende Dateien _L04_ im Namen.',
    originalPrompt:
      'Hören Sie ein kontroverses Team- oder Kundengespräch und formulieren Sie einen deeskalierenden Zwischenschritt, der Emotion anerkennt und zum Sachanliegen zurückführt.',
  },
  {
    unitId: 's5-spontaneous',
    bookReference: 'Aspekte neu C1 · Kapitel 9 „Spontan reagieren“ · Kursbuch S. 136–151',
    audioFilenameHint: 'Lehrbuch-CD 3 · Ordner „CD 3 K 8-10“; Tracknummer im Kursbuch abgleichen.',
    originalPrompt:
      'Pausieren Sie einen Gesprächstrack nach einer unerwarteten Rückfrage und sprechen Sie 60 Sekunden frei als direkte Erwiderung mit hörergerechter Selbstreparatur.',
  },
  {
    unitId: 's5-facilitate',
    bookReference: 'Sicher! C1/2 · Lektion 8 „Diskussionen moderieren“ · Kursbuch S. 97–108',
    audioFilenameHint: 'Im Ordner „audio KB“ tragen passende Dateien _L08_ im Namen.',
    originalPrompt:
      'Fassen Sie nach einer Gruppenaufnahme zwei gegensätzliche Positionen neutral zusammen und formulieren Sie eine Übergangsfrage für eine bislang stille Person.',
  },
  {
    unitId: 's5-revise',
    bookReference: 'Aspekte neu C1 · Schreibwerkstatt & Textrevision · Arbeitsbuch S. 122–131',
    audioFilenameHint: 'Begleittext im Arbeitsbuch als Revisionsvorlage nutzen.',
    originalPrompt:
      'Überarbeiten Sie einen ungenauen Ausgangstext aus dem Kapitel hinsichtlich Kohärenz, Registerkonstanz und präzisem Konnektoreneinsatz.',
  },
  {
    unitId: 's6-general',
    bookReference: 'Sicher! C1/2 · Lektion 10 „Gesellschaft & Debatte“ · Kursbuch S. 121–132',
    audioFilenameHint: 'Im Ordner „audio KB/AB“ tragen passende Dateien _L10_ im Namen.',
    originalPrompt:
      'Hören Sie einen unvorbereiteten Sachbeitrag zu einer gesellschaftlichen Frage und prüfen Sie Ihre spontane Stellungnahme gegen die Checkliste.',
  },
  {
    unitId: 's6-retention',
    bookReference: 'Aspekte neu C1 · Abschlusswiederholung & Transfer · Kursbuch S. 168–175',
    audioFilenameHint: 'Lehrbuch-CD 3 · Abschlusstracks im Kursbuch abgleichen.',
    originalPrompt:
      'Rufen Sie nach mehreren Tagen Abstand zu einer bekannten Einheit die zentralen C1-Sprachmittel frei in einem völlig neuen Kontext ab.',
  },
  {
    unitId: 's6-checkpoint',
    bookReference: 'Goethe-Zertifikat C1 / Telc C1 Hochschule · Modellsatz Prüfungstraining',
    audioFilenameHint: 'Audio-Dateien des gewählten Modellsatzes streng zeitbegrenzt durchlaufen.',
    originalPrompt:
      'Werten Sie eine selbst aufgenommene Antwort ehrlich auf Lücken aus und formulieren Sie zwei konkrete, überprüfbare Übungsziele.',
  },
  {
    unitId: 's6-specialist',
    bookReference: 'Aspekte neu C1 · Kapitel 8 „Technik & Zukunft“ · Kursbuch S. 120–135',
    audioFilenameHint:
      'Wähle die Lehrbuch-CD-Datei anhand der im Kursbuch beim Hörtext gedruckten Tracknummer.',
    audioReference: 'Lehrbuch-CD 3 · Ordner „CD 3 K 8-10“; Tracknummer im Kursbuch abgleichen.',
    originalPrompt:
      'Bereiten Sie aus einem Fachtext des Kapitels eine dreiminütige freie Vermittlung für fachfremde Beteiligte vor, inklusive expliziter Grenzen dessen, was der Text nicht belegt.',
  },
];

export function lessonListeningForUnit(unitId: string): {
  episodes: LessonListeningRecommendation[];
  bookBridges: BookBridgePractice[];
} {
  return {
    episodes: lessonListeningEpisodes.filter((e) => e.unitId === unitId),
    bookBridges: lessonBookBridges.filter((b) => b.unitId === unitId),
  };
}

const ALL_REQUIREMENTS: RecordingRequirement[] = [
  'NATIVE_OR_HIGHLY_PROFICIENT_SPEAKERS',
  'NATURAL_TURN_TAKING',
  'VERIFIED_TRANSCRIPT',
  'SPEAKER_METADATA',
  'REGIONAL_BACKGROUND',
  'APPLICATION_USE_RELEASE',
  'EDITED_AND_RAW_DELIVERABLES',
];

export const ownedDialogueBriefs: OwnedDialogueBrief[] = [
  {
    id: 'supermarket',
    situation: 'Doppelte Abbuchung an der Kasse',
    context: 'GENERAL',
    speakers: 2,
    targetSignals: ['CONVERSATIONAL_REPAIR', 'REGISTER'],
    requirements: ALL_REQUIREMENTS,
  },
  {
    id: 'customer-service',
    situation: 'Reklamation mit missverständlicher Rückfrage',
    context: 'SOCIAL',
    speakers: 2,
    targetSignals: ['SITUATIONAL_INFERENCE', 'RESPONSE_INITIATION'],
    requirements: ALL_REQUIREMENTS,
  },
  {
    id: 'supervisor',
    situation: 'Messwertfrage und unerwarteter Einwand der Leitung',
    context: 'ENGINEERING',
    speakers: 2,
    targetSignals: ['INFORMATION_RETENTION', 'SENTENCE_FORMULATION'],
    requirements: ALL_REQUIREMENTS,
  },
  {
    id: 'automotive-review',
    situation: 'ECU/TCU-Review mit Safety- und Entwicklungsrolle',
    context: 'ENGINEERING',
    speakers: 3,
    targetSignals: ['MULTI_SPEAKER_TRACKING', 'INTERACTION_MANAGEMENT'],
    requirements: ALL_REQUIREMENTS,
  },
  {
    id: 'football-children',
    situation: 'U13-Übung erklären und spontan vereinfachen',
    context: 'COACHING',
    speakers: 2,
    targetSignals: ['REGISTER', 'SENTENCE_FORMULATION'],
    requirements: ALL_REQUIREMENTS,
  },
  {
    id: 'football-parent',
    situation: 'Elterngespräch mit widersprüchlichem Einwand',
    context: 'COACHING',
    speakers: 2,
    targetSignals: ['CONVERSATIONAL_REPAIR', 'NATURALNESS'],
    requirements: ALL_REQUIREMENTS,
  },
  {
    id: 'informal-regional',
    situation: 'Informelles Gespräch mit regionalem Hintergrund',
    context: 'SOCIAL',
    speakers: 3,
    targetSignals: ['WORD_SEGMENTATION', 'IMPLIED_MEANING'],
    requirements: ALL_REQUIREMENTS,
  },
];
