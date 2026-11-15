package com.example.programme

private fun contextsC(general: String, social: String, engineering: String, coaching: String) =
    mapOf(
        ContextTrack.GENERAL to general,
        ContextTrack.SOCIAL to social,
        ContextTrack.ENGINEERING to engineering,
        ContextTrack.COACHING to coaching
    )

/**
 * Fully authored units s3-deescalate, s3-agree, s5-spontaneous, s2-summarise, s2-mediate.
 * All people, clubs, companies, measurements and data in these units are fictional.
 */
internal val unitsPartC: List<CurriculumUnit> = listOf(
    CurriculumUnit(
        "s3-deescalate", "Konflikte deeskalieren", 4, emptyList(),
        listOf(
            "Gefühle und Anliegen anerkennen, ohne unbelegte Vorwürfe als Tatsachen zu übernehmen.",
            "Berechtigte Teilkritik offen eingestehen und eine Gesprächsgrenze ruhig benennen.",
            "Das Gespräch auf eine überprüfbare nächste Handlung zurückführen."
        ),
        contextsC(
            "Eine Beschwerde klären",
            "Bei 'Jetzt hör halt zu' den Ton ansprechen und zum Anliegen zurückführen",
            "Ein Automotive-Zulieferergespräch über ungeklärte ECU-Ausfälle beruhigen",
            "Ein aufgebrachtes Elterngespräch über U14-Spielzeit ruhig und vertraulich führen"
        ),
        UnitContent(
            reading = """
                Konflikte eskalieren selten wegen eines einzelnen Satzes, sondern weil sich beide Seiten nicht gehört fühlen. Wer deeskalieren will, muss deshalb zwei Ebenen trennen: das Gefühl und die Behauptung. Ein Gefühl lässt sich anerkennen („Ich merke, dass Sie verärgert sind“), ohne dass man der Behauptung zustimmt („Mein Sohn wird systematisch benachteiligt“). Genau diese Unterscheidung fällt im Alltag schwer, weil Anerkennung schnell als Schuldeingeständnis missverstanden wird – von beiden Seiten.

                Ein erfundenes Beispiel aus dem Jugendfußball: Nach dem Heimspiel der U14 steht Herr Keller aufgebracht vor dem Trainer. Sein Sohn Ben habe in drei Spielen hintereinander nur zehn Minuten gespielt; das sei eine Frechheit und Absicht. Der Trainer könnte jetzt kontern, Ben zeige im Training halt zu wenig Einsatz. Damit würde er jedoch eine Deutung als Tatsache darstellen und den Streit vor den Augen der Mannschaft verschärfen. Stattdessen schlägt er ein Gespräch für den nächsten Tag vor, an einem ruhigen Ort und unter vier Augen. Dort beginnt er mit dem Anliegen: „Sie wünschen sich mehr Spielzeit für Ben und sind enttäuscht. Das kann ich nachvollziehen.“ Erst danach erläutert er, welche Kriterien im Team gelten, etwa die Rotation, die Trainingsbeteiligung und die Aufgaben in der Viererkette beim Spielaufbau, und was Ben konkret tun kann, um häufiger eingesetzt zu werden. Von „immer“ und „nie“ spricht er nicht.

                Für solche Gespräche haben sich vier Schritte bewährt: erstens Tempo und Lautstärke senken, zweitens das Gefühl benennen, drittens den Vorwurf in ein überprüfbares Anliegen übersetzen, viertens eine nächste Handlung mit Termin vereinbaren. Wichtig ist auch die Grenze: Wird jemand beleidigend, darf man das Gespräch unterbrechen und einen neuen Termin anbieten. Das ist keine Schwäche, sondern Selbstschutz. Und wenn ein Vorwurf teilweise berechtigt ist, sollte man das offen eingestehen, statt aus Prestigegründen alles abzuwehren. Ein klares „Da haben wir Sie nicht rechtzeitig informiert“ wirkt oft stärker als jede Rechtfertigung, kostet aber nichts, was nicht ohnehin schon sichtbar wäre.
            """.trimIndent(),
            listening = """
                (Fiktives Telefonat zwischen Hersteller und Zulieferer. Herr Schuster nutzt eine fiktive, vereinfachte schwäbische Schreibannäherung (nicht regional validiert, keine Norm).)
                Brandt: Frau Yilmaz, ich sag's ganz offen: Das ist jetzt der dritte Ausfall am Prüfstand, und von Ihnen kommen wieder nur Zwischenmeldungen. Das ist doch inakzeptabel!
                Yilmaz: Herr Brandt, ich höre, dass Sie verärgert sind, und das kann ich nachvollziehen. Ihr Prüfstand steht ja still.
                Brandt: Genau, und wir verlieren Wochen. Ihr Steuergerät ist einfach fehlerhaft, das ist doch offensichtlich.
                Yilmaz: Dass der Stillstand Sie belastet, nehme ich ernst. Ob es am Steuergerät liegt, kann ich heute noch nicht bestätigen. Da bin ich ehrlich. Die Rückläufer sind erst gestern bei uns angekommen.
                Brandt: Das hör ich seit zwei Wochen: erst gestern, erst nächste Woche …
                Schuster: Entschuldigung, wenn ich kurz dazwischengehe: Mir hend die Fehlerspeichereinträge aus zwei von den drei Läufen, gell?
                Brandt: Und?
                Schuster: Beide Male steht ein Reset durch den Watchdog drin. Des isch aber no koi Beweis für en Hardwarefehler – des ka au a Spannungseinbruch am Prüfstand sei.
                Brandt: Moment, jetzt wird's mir zu bunt. Wollen Sie mir sagen, das liegt an unserem Prüfstand?
                Yilmaz: Nein, das sagt Herr Schuster nicht. Er sagt, dass wir beide Möglichkeiten prüfen müssen. Habe ich das richtig verstanden, Herr Schuster?
                Schuster: Genau, so isch's gmeint.
                Brandt: Gut. Aber ich brauche etwas Verbindliches, nicht wieder Absichtserklärungen.
                Yilmaz: Verständlich. Ich schlage vor: Morgen bis zwölf Uhr schicken wir Ihnen die Logfiles beider Läufe, und bis Freitag liegt die Rückläuferanalyse vor, mit einer Zwischenbewertung, was gesichert und was noch offen ist.
                Brandt: Und die Versorgungsspannung messt ihr bei uns gegen?
                Schuster: Wenn Sie uns den Messaufbau schicket, dann ja.
                Brandt: Das schick ich heute noch. Und Frau Yilmaz – der Ton vorhin war zu scharf. Das tut mir leid.
                Yilmaz: Schon vergessen. Hauptsache, wir kommen jetzt gemeinsam weiter.
            """.trimIndent(),
            expressions = listOf(
                "Ich höre, dass Sie verärgert sind, und das kann ich nachvollziehen. – ein Gefühl anerkennen, ohne dem Vorwurf zuzustimmen",
                "Dass …, nehme ich ernst; ob …, kann ich heute noch nicht bestätigen. – das Anliegen würdigen und die Behauptung offenlassen",
                "Habe ich Sie richtig verstanden, dass …? – einen Vorwurf in ein überprüfbares Anliegen übersetzen",
                "Da haben wir einen Fehler gemacht / Das hätten wir früher sagen müssen. – berechtigte Teilkritik zugeben",
                "Bei mir kommt an, dass … / Ich möchte, dass wir beide ausreden können. – den Ton ansprechen, ohne zurückzuschlagen",
                "Ich schlage vor, wir unterbrechen kurz und sprechen morgen um … weiter. – eine Gesprächsgrenze ruhig setzen",
                "Kollokationen: Verständnis zeigen für …, die Wogen glätten, den Ton ansprechen, Vorwürfe zurückweisen, Öl ins Feuer gießen, das Gespräch vertagen",
                "'Jetzt hör halt zu' – 'halt' kann hier genervte Resignation tragen; Antwort: 'Ich höre dir zu, und mir ist wichtig, dass wir beide ausreden können. Was ist dir am wichtigsten?'"
            ),
            grammar = "Vorangestellte dass- und ob-Sätze trennen Anerkennung und Offenlassen: 'Dass der Stillstand Sie belastet, nehme ich ernst. Ob es am Steuergerät liegt, kann ich nicht bestätigen.' Nach dem Nebensatz steht das Verb des Hauptsatzes direkt an erster Stelle (Inversion). Ein Vorwurf wird mit Konjunktiv I oder 'laut' distanziert wiedergegeben: 'Sie sagen, Ihr Sohn werde benachteiligt' übernimmt die Behauptung nicht als Tatsache. Berechtigte Teilkritik passt in den Konjunktiv II der Vergangenheit: 'Hätten wir die Eltern früher informiert, wäre die Verunsicherung kleiner gewesen.' Pauschale Wörter wie 'immer' oder 'nie' belegen Vorwürfe nicht; ersetzen Sie sie durch konkrete Fälle ('in den letzten drei Spielen'). Die Modalpartikel 'halt' im Satz 'Jetzt hör halt zu' macht die Aufforderung nicht höflicher; sie kann Genervtheit anzeigen.",
            question = "Ein Vater sagt, sein Sohn werde 'systematisch benachteiligt'. Welche Reaktion entspricht dem im Text beschriebenen Vorgehen am besten?",
            options = listOf(
                "Das stimmt nicht. Ben zeigt im Training einfach zu wenig Einsatz.",
                "Sie sind enttäuscht über die wenige Spielzeit, das kann ich nachvollziehen. Welche Kriterien bei uns gelten, erkläre ich Ihnen morgen in Ruhe unter vier Augen.",
                "Sie haben recht, wir haben Ihren Sohn benachteiligt. Ab sofort spielt er mehr.",
                "Darüber diskutiere ich weder vor der Mannschaft noch in diesem Ton."
            ),
            correctOption = 1,
            explanation = "B erkennt das Gefühl an, übernimmt die Behauptung 'systematisch' nicht und verschiebt die Sachklärung auf einen ruhigen Rahmen. A wehrt ab und stellt eine Deutung über den Einsatz als Tatsache dar; das verschärft den Streit. C übernimmt den unbelegten Vorwurf und macht eine Zusage unter Druck. D setzt zwar eine Grenze, erkennt aber weder Gefühl noch Anliegen an und bietet keine nächste Handlung an.",
            controlledPrompt = "Zuliefergespräch: Der Prüfstand steht still, die Ursache ist ungeklärt, die Rückläufer kamen erst gestern an. Formulieren Sie vier Sätze: (1) Gefühl anerkennen ('Ich höre, dass …'), (2) Behauptung offenlassen ('Ob …, kann ich … nicht bestätigen'), (3) berechtigte Teilkritik im Konjunktiv II der Vergangenheit ('Hätten wir …, wären …'), (4) nächste Handlung mit Frist. Keine Schuldzuweisung, kein 'immer' oder 'nie'.",
            controlledModel = "Ich höre, dass Sie verärgert sind, weil Ihr Prüfstand stillsteht. Ob das Steuergerät die Ursache ist, kann ich heute noch nicht bestätigen. Hätten wir die Rückläufer früher angefordert, wären wir mit der Analyse jetzt weiter. Ich schicke Ihnen morgen bis zwölf Uhr die Logfiles und bis Freitag eine Zwischenbewertung.",
            speaking = "Führen Sie zwei Minuten lang ohne Vorlage ein vertrauliches Elterngespräch (fiktive Lage): Herr Keller, Vater eines U14-Spielers, sagt: 'Ben hat in drei Spielen nur je zehn Minuten gespielt – das ist doch Absicht!' Erkennen Sie seine Enttäuschung an, ohne 'Absicht' als Tatsache zu übernehmen. Erklären Sie in zwei Sätzen, wonach Einsatzzeiten im Team entschieden werden (zum Beispiel Rotation, Trainingsbeteiligung, Position in der Viererkette, Spielplan für Pressing und Umschaltspiel). Geben Sie eine berechtigte Teilkritik zu (etwa zur Kommunikation) und vereinbaren Sie einen konkreten Gesprächstermin.",
            interaction = "A ist Trainer oder Trainerin, B der aufgebrachte Vater. B steigt laut ein und wird persönlich ('Sie haben doch keine Ahnung'); bei 'Jetzt hör halt zu' spricht A den Ton an und führt zum Anliegen zurück. Wendung: Nach etwa einer Minute nennt B eine berechtigte Information: Die Elternnachricht zu den neuen Rotationsregeln wurde nie verschickt. A gesteht das ein, ohne die Entscheidungsrolle aufzugeben, zieht bei persönlichen Angriffen eine ruhige Grenze und vereinbart Termin, Verantwortliche und Kanal für die Information. Tauschen Sie die Rollen.",
            writing = "Im Elternchat der U14 schreibt ein Vater: 'Wieder nur zehn Minuten – das ist ein Witz, und das Trainerteam hat keine Ahnung.' Verfassen Sie eine private Antwort von 150–190 Wörtern an ihn (nicht in den Gruppenchat). Erkennen Sie die Enttäuschung an, übernehmen Sie die Wertung 'keine Ahnung' nicht, geben Sie eine konkrete Teilkritik zu (zum Beispiel die fehlende Information über die Rotation), nennen Sie die Kriterien in zwei Sätzen und schlagen Sie ein Gespräch mit Termin vor.",
            transfer = "Neuer Kontext, ausdrücklich ohne Vorbereitung: Eine Mitarbeiterin im Kundenservice eines Autohauses spricht mit einem wütenden Kunden, dessen Hybridfahrzeug nach einer Werkstattvorstellung beim Wechsel zwischen elektrischem und kombiniertem Antrieb weiterhin ruckelt. Die Ursache ist noch nicht gefunden; Kulanz kann sie nicht zusagen. Deeskalieren Sie, übernehmen Sie Vorwürfe wie 'Ihr betrügt mich' nicht als Tatsachen, erkennen Sie berechtigte Kritik an und vereinbaren Sie nächste Schritte mit Termin. Diese Übung dient nur dem ersten Training. Für die Prüfung sind frische, bisher unbekannte Situationen erforderlich; sie folgen frühestens sieben Tage später, werden ohne Hilfsmittel bearbeitet und von einer unabhängigen Person bewertet.",
            feedback = "Modellrückmeldung: Ihr Einstieg erkennt das Gefühl an, bevor Sie sachlich werden; das senkt das Tempo. Achten Sie darauf, dass 'Das verstehe ich' nicht als Zustimmung zum Vorwurf klingt: Sagen Sie ausdrücklich, was Sie anerkennen (Ärger, Wartezeit) und was noch offen ist (Ursache, Absicht). 'Da muss ich Ihnen widersprechen' direkt nach der Beschwerde eskaliert eher; besser ist eine Rückfrage wie 'Habe ich Sie richtig verstanden, dass …?'. Die Teilkritik war konkret und ohne Selbstbezichtigung. Prüfen Sie, ob Ihr Schluss eine Handlung mit Frist enthält. Bei persönlichen Angriffen darf das Gespräch freundlich, aber klar vertagt werden."
        )
    ),
    CurriculumUnit(
        "s3-agree", "Vereinbarungen absichern", 4, emptyList(),
        listOf(
            "Zusagen konkretisieren: Wer macht was bis wann, und woran erkennt man den Erfolg?",
            "Unverbindliche Höflichkeitsformeln freundlich in überprüfbare Absprachen überführen.",
            "Eine Vereinbarung schriftlich bestätigen lassen und Ausnahmen oder Ersatzregeln festhalten."
        ),
        contextsC(
            "Eine Reparatur vereinbaren",
            "Ein schwäbisches 'Schauen wir mal' nicht als Terminbestätigung behandeln und freundlich konkretisieren",
            "Verantwortung und Nachweise für einen TCU-Prüfplan festhalten",
            "Mit U11-Eltern Abholort, Aufsicht und Rückmeldung nach dem Fußballtraining vereinbaren"
        ),
        UnitContent(
            reading = """
                Elternabend der U11, Donnerstagabend, kurz vor Saisonbeginn. Die Trainerin Sabine Maurer, eine erfundene Figur, hat ein Problem, das vielen Vereinen vertraut ist: Nach dem Training warten manche Kinder allein am Parkplatz, weil unklar ist, wer sie abholt und wo. Auf ihre Frage, ob alle Eltern ihr Kind künftig am Kabineneingang abholen könnten, antworten mehrere Mütter und Väter freundlich: „Ja, passt scho“, „Schauen wir mal“ oder „Machen wir“. Frau Maurer weiß aus Erfahrung, dass diese Antworten Wohlwollen ausdrücken, aber noch keine Vereinbarung sind. Deshalb fragt sie nach, ohne misstrauisch zu klingen: „Danke für die Bereitschaft. Damit ich es richtig notieren kann: Wer holt Mia und Elias dienstags ab, und wo genau?“

                Aus dem Gespräch entsteht eine Vereinbarung mit vier Bestandteilen. Erstens die Verantwortung: Jede Familie nennt eine namentlich benannte Person, die abholt, und eine Vertretung. Zweitens Ort und Zeit: Abholung bis 18.45 Uhr am Kabineneingang, nicht am Parkplatz. Drittens die Aufsicht: Das Trainerteam beaufsichtigt die Kinder bis 18.45 Uhr; wer sich verspätet, schreibt spätestens um 18.30 Uhr eine Nachricht in die Gruppe. Viertens das Erfolgskriterium: Nach vier Wochen schaut das Trainerteam gemeinsam mit zwei Elternvertretern, ob kein Kind mehr unbeaufsichtigt wartete und ob die Nachrichtenregel funktioniert hat. Falls nicht, wird die Regelung angepasst.

                Am nächsten Morgen schickt Frau Maurer die Vereinbarung schriftlich in die Gruppe und bittet um Rückmeldung: „Bitte antwortet mit ‚Gelesen und einverstanden‘ oder nennt mir eure Einwände bis Sonntag.“ Das ist keine Förmelei, sondern verhindert, dass jede Familie sich etwas anderes gemerkt hat. Wichtig ist außerdem, was die Vereinbarung nicht regelt: Sie ersetzt keine vereinsinternen Regeln zur Aufsicht und keine Notfallnummer. Für Ausnahmen, etwa bei Krankheit der abholenden Person, gilt die benannte Vertretung oder der Anruf bei der Trainerin. Gute Vereinbarungen sind kurz, überprüfbar und lassen Platz für begründete Ausnahmen. Sie machen aus einem guten Willen eine gemeinsame Handlungsgrundlage.
            """.trimIndent(),
            listening = """
                (Fiktive Prüfplan-Besprechung zu einer TCU-Änderung. Herr Haug nutzt eine fiktive, vereinfachte schwäbische Schreibannäherung (nicht regional validiert, keine Norm).)
                Haug: So, zum Prüfplan für die TCU-Änderung: Bis wann könnet Sie die Testfälle für den Übergang von elektrisch auf hybrid fertig habe, Frau Demir?
                Demir: Die Testfälle selbst sind eigentlich fast fertig. Das Problem ist der Prüfstand, der ist nächste Woche halt noch belegt.
                Haug: Mir schauet mal, ob des klappt.
                Demir: Entschuldigung, Herr Haug – heißt 'Wir schauen mal', dass ich den Prüfstand für Dienstag fest einplanen kann, oder müssen Sie das erst klären?
                Haug: Ah, ja, stimmt, des war jetzt unpräzis. Ich klär des heut noch mit der Prüfstandsplanung und gib Ihne bis 16 Uhr Bescheid.
                Demir: Gut, dann trage ich ein: Haug klärt die Prüfstandsbelegung, Rückmeldung bis heute 16 Uhr.
                Albrecht: Darf ich was ergänzen? Der Testbericht allein reicht uns nicht. Wir brauchen die Rückverfolgbarkeit von der Anforderung zum Testfall, und die FMEA-Zeilen, die von der Änderung betroffen sind, müssen vorher aktualisiert sein.
                Demir: Wer aktualisiert die FMEA?
                Albrecht: Das muss das Entwicklungsteam machen. Ich prüfe es dann im Safety-Review.
                Haug: Dann hat das Entwicklungsteam die FMEA bis Donnerstag. Herr Wagner, des isch Ihr Thema, oder?
                Wagner: Ja, ich übernehm das. Donnerstag, Ende des Tages – nein, warte, ich bin Donnerstag beim Kundentermin. Sagen wir Mittwoch, 17 Uhr.
                Haug: Mittwoch, 17 Uhr, Herr Wagner. Sind alle damit einverstanden?
                Albrecht: Ja. Mein Review läuft dann Donnerstag und Freitag. Ich sag aber gleich: Sind die Zeilen unvollständig, geht's zurück.
                Demir: Und woran erkennen wir, dass der Prüfplan erfüllt ist? Alle Testfälle ausgeführt, Abweichungen dokumentiert, Rückverfolgbarkeit vollständig?
                Haug: Genau, des isch unser Erfolgskriterium. Wichtig: Bestandene Testfälle sind noch koi Fahrzeugfreigabe. Die Freigabe läuft über den Safety-Prozess, nicht über uns.
                Demir: Ich schick nachher ein Protokoll mit allen Punkten. Bitte kurz bestätigen.
                Haug: Machet mer so.
            """.trimIndent(),
            expressions = listOf(
                "Wer macht was bis wann? – Verantwortung, Aufgabe und Frist festhalten",
                "Damit ich es richtig notiere: … – eine freundliche Zusage konkretisieren",
                "Woran erkennen wir, dass … erfüllt ist? – ein Erfolgskriterium vereinbaren",
                "Ich fasse kurz zusammen und schicke es schriftlich; bitte bestätigen Sie es bis … – die Vereinbarung absichern",
                "Falls …, gilt … / Als Vertretung ist … benannt. – Ausnahmen und Ersatzregeln festlegen",
                "Kollokationen: eine Vereinbarung treffen, einen Termin verbindlich zusagen, Verantwortung übernehmen, eine Frist einhalten, Nachweise vorlegen, etwas schriftlich festhalten, den Termin bestätigen",
                "'Schauen wir mal' / 'Passt scho' / 'Machen wir halt so' – oft freundliche Zurückhaltung oder Wohlwollen, nicht automatisch eine Zusage; nachfragen: 'Heißt das, dass ich fest damit planen kann?'"
            ),
            grammar = "Passiv verdeckt Zuständigkeit: 'Die FMEA wird aktualisiert' sagt nicht, wer handelt. Im Aktiv wird die Absprache prüfbar: 'Herr Wagner aktualisiert die FMEA bis Mittwoch, 17 Uhr.' Das Präsens mit Zeitangabe ('Ich schicke es bis 16 Uhr') ist verbindlicher als 'Ich werde versuchen, …'. Das Zustandspassiv beschreibt ein Erfolgskriterium als erreichten Zustand: 'Alle Testfälle sind ausgeführt, die Abweichungen sind dokumentiert.' Das Passiv mit Modalverb nennt Anforderungen: 'Die Zeilen müssen aktualisiert werden.' Der Konditionalsatz mit 'falls' regelt Ausnahmen: 'Falls der Prüfstand nicht frei ist, gilt der Ausweichtermin am Donnerstag.' 'Bis' markiert die Frist, 'spätestens' verschärft sie, 'ab' markiert den Beginn.",
            question = "Warum bedankt sich die Trainerin nicht einfach für die freundlichen Antworten wie 'Passt scho' oder 'Schauen wir mal', sondern fragt nach?",
            options = listOf(
                "Weil sie den Eltern grundsätzlich misstraut und sie kontrollieren will.",
                "Weil solche Antworten Wohlwollen ausdrücken können, aber nicht klären, wer wann und wo abholt.",
                "Weil Zusagen im Verein nur gelten, wenn sie vorher notariell beglaubigt werden.",
                "Weil Wendungen wie 'Schauen wir mal' in Süddeutschland immer eine Absage bedeuten."
            ),
            correctOption = 1,
            explanation = "B trifft den Text: Frau Maurer fragt freundlich nach Person, Ort und Zeit, weil Wohlwollen noch keine Vereinbarung ist. A unterstellt Misstrauen, das der Text nicht nennt; sie fragt ausdrücklich ohne misstrauischen Ton. C erfindet eine Formalität; die schriftliche Bestätigung per Gruppennachricht ist kein notarieller Akt. D verallgemeinert regionale Wendungen: Sie können je nach Situation Zusage, Zurückhaltung oder Höflichkeit bedeuten, deshalb fragt man nach.",
            controlledPrompt = "Kontext Werkstatt, Hybridfahrzeug mit Ruckeln: Der Werkstattmitarbeiter sagt: 'Ja, wir schauen mal.' Formulieren Sie vier Sätze: (1) eine freundliche Konkretisierungsfrage ('Damit ich es richtig notiere: …'), (2) Verantwortung im Aktiv mit Person, Aufgabe und Frist, (3) ein Erfolgskriterium im Zustandspassiv, (4) eine Ausweichregel mit 'falls'.",
            controlledModel = "Damit ich es richtig notiere: Heißt 'Wir schauen mal', dass mein Auto am Dienstag fest eingeplant ist? Herr Ott ruft mich bis Montag, 12 Uhr, an und bestätigt den Termin. Die Reparatur gilt als erledigt, wenn der Fehler behoben und die Probefahrt dokumentiert ist. Falls ein Ersatzteil fehlt, ruft Herr Ott mich noch am selben Tag an und nennt einen neuen Termin.",
            speaking = "Leiten Sie in zwei Minuten ohne Vorlage den Elternabend einer U11 an und vereinbaren Sie die Abholung nach dem Fußballtraining. Klären Sie Abholort, benannte Person und Vertretung, die Aufsicht durch das Trainerteam, die Regel für Verspätungen und ein überprüfbares Erfolgskriterium nach vier Wochen. Reagieren Sie freundlich auf 'Schauen wir mal', indem Sie nach Person, Ort und Zeit fragen. Nennen Sie auch, was die Vereinbarung nicht regelt.",
            interaction = "A ist die U11-Trainerin, B ein Vater, der 'Passt scho, wir schauen mal' sagt. A konkretisiert freundlich. Wendung: B arbeitet dienstags bis 19 Uhr und die Mutter möchte, dass der Junge allein heimgeht. A nimmt beide Anliegen ernst, legt aber nur Regeln fest, die zur Aufsichtsverantwortung des Vereins passen und nicht erfunden sind; gemeinsam entwickeln Sie eine Vertretung oder Fahrgemeinschaft. Am Schluss fasst A Verantwortung, Frist, Ausnahme und schriftliche Bestätigung zusammen. Tauschen Sie die Rollen.",
            writing = "Schreiben Sie eine Bestätigungsmail von 140–180 Wörtern an eine Kfz-Werkstatt (fiktive Lage): Ihr Hybridfahrzeug ruckelt beim Wechsel zwischen elektrischem und kombiniertem Antrieb. Halten Sie schriftlich fest: Abgabetermin am Dienstag, Kostenvoranschlag vor Arbeitsbeginn, Rückruf bei Mehrkosten über 150 Euro, voraussichtliche Fertigstellung bis Freitag, 16 Uhr, Ersatzfahrzeug ja oder nein, Ansprechperson und Erfolgskriterium. Bitten Sie um eine Antwort bis Montagmittag, ob alle Punkte so gelten.",
            transfer = "Neuer Kontext, ohne Vorbereitung: Ein Freund sagt zu einem Samstagsumzug 'Ich schau mal, ob ich Zeit hab'. Klären Sie freundlich, ob er fest zusagt, wer das Auto mitbringt, wann Sie anfangen, was passiert, wenn er kurzfristig absagen muss, und bitten Sie um schriftliche Bestätigung. Diese Übung dient nur dem ersten Training. Für die Prüfung sind frische, bisher unbekannte Situationen erforderlich; sie folgen frühestens sieben Tage später, werden ohne Hilfsmittel bearbeitet und von einer unabhängigen Person bewertet.",
            feedback = "Modellrückmeldung: Ihre Nachfrage 'Damit ich es richtig notiere' ist höflich und deutlich genug. Prüfen Sie, ob in Ihrer Vereinbarung alle vier Bestandteile vorkommen: Person, Frist, Erfolgskriterium und Ausweichregel. Im Satz 'Der Termin wird noch geklärt' fehlt die handelnde Person; im Aktiv wäre er prüfbar. 'Ich versuche, bis Freitag …' ist weicher als 'Ich schicke es bis Freitag, 12 Uhr'. Im Automotive-Beispiel dürfen bestandene Testfälle nicht mit einer Fahrzeugfreigabe verwechselt werden. Vergessen Sie die schriftliche Bestätigung nicht: Sie schützt beide Seiten."
        )
    ),
    CurriculumUnit(
        "s5-spontaneous", "Spontan zusammenhängend reagieren", 4, emptyList(),
        listOf(
            "Auf unerwartete Fragen Zeit gewinnen und mit einem klaren Gerüst antworten (Kernaussage, Grund, Beispiel, Rückfrage).",
            "Sich hörergerecht reparieren, wenn eine Aussage ungenau oder missverständlich war.",
            "Modalpartikeln wie 'eben' und 'halt' nach Funktion und Ton einordnen und respektvoll nachfragen."
        ),
        contextsC(
            "Eine unerwartete Nachfrage beantworten",
            "Im freien Gespräch 'eben' und 'halt' nach Funktion und Ton einordnen und respektvoll reagieren",
            "Auf eine unerwartete Frage zur ECU/TCU-Drehmomentkoordination im Automotive-Review reagieren",
            "Auf eine unerwartete Elternfrage zur U13-Taktik reagieren, ohne Spieler öffentlich zu kritisieren"
        ),
        UnitContent(
            reading = """
                Wer in einer Besprechung oder an der Seitenlinie unerwartet angesprochen wird, merkt schnell: Fachwissen allein reicht nicht. Entscheidend ist, wie man in den ersten Sekunden reagiert. Erfahrene Sprecher versuchen nicht, sofort die perfekte Antwort zu liefern. Sie sichern sich zuerst Denkzeit, etwa mit einer Rückfrage („Meinen Sie den Moment des Schaltvorgangs oder das Anfahren?“) oder einer kurzen Einordnung („Das ist eine berechtigte Frage, die ich in zwei Schritten beantworten möchte“). Das ist kein Ausweichen, solange danach tatsächlich eine Antwort folgt.

                Hilfreich ist ein einfaches Grundgerüst: erst die Kernaussage, dann ein Grund, dann ein Beispiel, am Ende eine Rückfrage. Ein erfundenes Beispiel aus einem Hybrid-Review: Ein Kollege aus einer anderen Abteilung fragt unvermittelt, warum die TCU ihre Drehmomentanforderung nicht einfach direkt an den Antrieb weitergebe. Die Ingenieurin hält keinen Vortrag, sondern sagt: „Kurz gesagt: weil die ECU die Anforderungen koordiniert und gegen Bauteilgrenzen prüft. Der Grund ist, dass mehrere Funktionen gleichzeitig Drehmoment anfordern können. Ein Beispiel wäre ein Schaltvorgang bei hoher Last. Reicht das als Einordnung, oder soll ich tiefer gehen?“

                Ebenso wichtig ist die Reparatur. Wer merkt, dass er sich missverständlich ausgedrückt hat, sollte das offen sagen: „Das war ungenau – genauer gesagt …“. Hörer nehmen eine Korrektur selten als Schwäche wahr, sondern als Zeichen, dass jemand mitdenkt.

                Im Jugendfußball zeigt sich dasselbe, nur emotionaler. Eine Mutter fragt am Spielfeldrand der U13, warum ihr Sohn in der Viererkette zu weit herausgerückt sei und so das Gegentor möglich wurde. Die Versuchung ist groß, den Jungen zu benennen. Besser ist: „Gute Frage. Wir hatten heute im Spielaufbau und beim Pressing Abstimmungsprobleme; das betraf die ganze Kette, nicht eine einzelne Person. Das üben wir diese Woche im Kleinfeldspiel.“ So bleibt das Gespräch sachlich, und niemand wird vor anderen bloßgestellt.

                Schließlich lohnt ein Blick auf kleine Wörter. „Das ist eben so“ klingt nach einer Selbstverständlichkeit, „Das ist halt so“ eher nach Resignation; in vielen Situationen sind beide fast austauschbar, doch der Ton entscheidet. Wer sie hört, sollte nicht sofort gekränkt reagieren, sondern nachfragen: „Was meinst du damit konkret?“
            """.trimIndent(),
            listening = """
                (Fiktives Gespräch am Spielfeldrand nach dem U13-Training. Frau Brenner nutzt eine fiktive, vereinfachte schwäbische Schreibannäherung (nicht regional validiert, keine Norm).)
                Brenner: Herr Kaya, darf ich Sie was fragen? Warum macht ihr eigentlich so viel Rondo? Mei Sohn sagt, er hätt no nie so lang im Kreis gstande.
                Kaya: Ah, gute Frage, die hab ich so nich erwartet. Moment – meinen Sie, dass er zu wenig richtige Spielformen hat, oder dass er sich langweilt?
                Brenner: Beides eigentlich. Mir ham früher halt einfach gspielt, Ball aufs Tor.
                Kaya: Verstehe. Kurz gesagt: Das Rondo ist unser Einstieg, nicht das ganze Training. Der Grund ist, dass die Kinder dabei in kurzer Zeit sehr viele Ballkontakte haben und unter leichtem Druck entscheiden müssen: freilaufen, anspielen, Raum erkennen. Danach spielen wir immer ein Kleinfeldspiel, vier gegen vier.
                Öztürk: Aber ist das nicht zu technisch? Die Kinder wollen doch Tore schießen!
                Kaya: Tore fallen im Kleinfeld ja häufiger als – nein, Moment, das war so allgemein ungenau. Ich meine: Im Vier-gegen-vier hat jedes Kind mehr Ballaktionen und mehr Abschlussmöglichkeiten als im Spiel auf dem großen Feld, einfach weil weniger Spieler auf dem Platz stehen.
                Brenner: Des leucht mir ei. Und warum steht mei Sohn dann immer links hinte in der Viererkette?
                Kaya: Mhm, jetzt wird's persönlich. Über einzelne Kinder möchte ich hier am Spielfeldrand nich sprechen. Aber ich nehm die Frage gern mit: Wir wechseln die Positionen im Spielaufbau derzeit durch, damit jedes Kind alle Rollen kennenlernt.
                Öztürk: Das ist halt so beim Trainer. Der macht eben, was er will.
                Kaya: Hm, Herr Öztürk, 'was er will' – das klingt für mich nach Kritik. Was würden Sie sich denn anders wünschen?
                Öztürk: Nein, nein, so war's nich gemeint. Ich wollte nur verstehen, wie ihr plant.
                Kaya: Dann nehm ich das so mit, danke. Wissen Sie was? Beim nächsten Elternabend erkläre ich unseren Plan mit Beispielen. Einverstanden?
                Brenner: Des wär prima. Und wann isch der?
                Kaya: Ich schick den Termin heut Abend in die Gruppe.
                Brenner: Gell, des mit em Rondo isch also net bloß Spielerei?
                Kaya: Nein, keine Spielerei. Aber Sie haben recht: Wir hätten es früher besser erklären sollen.
            """.trimIndent(),
            expressions = listOf(
                "Gute Frage – da muss ich kurz überlegen. / Moment, ich ordne das kurz. – Denkzeit gewinnen",
                "Meinen Sie …, oder geht es Ihnen eher um …? – die Frage durch Rückfrage eingrenzen",
                "Kurz gesagt: …; der Grund ist …; ein Beispiel wäre … – ein Antwortgerüst aufbauen",
                "Das war ungenau – ich meine eigentlich … / Genauer gesagt … – sich selbst reparieren",
                "Über Einzelne möchte ich hier nicht sprechen, aber ich nehme die Frage gern mit. – eine Grenze setzen, ohne abzublocken",
                "Reicht Ihnen das als Antwort, oder soll ich noch auf … eingehen? – eine Rückfrage zum Abschluss stellen",
                "Kollokationen: eine Frage aufgreifen, Zeit gewinnen, auf den Punkt kommen, den Faden wiederaufnehmen, sich korrigieren, ins Detail gehen, eine Frage zurückstellen",
                "'Das ist eben so' (Selbstverständlichkeit) / 'Das ist halt so' (Hinnahme, oft Resignation): Ton prüfen und nachfragen – 'Was meinst du damit konkret?'"
            ),
            grammar = "Kohäsion in spontanen Antworten: 'Kurz gesagt', 'Der Grund dafür ist', 'Ein Beispiel wäre', 'Daher' gliedern die Antwort hörbar. Nach 'deshalb' und 'daher' steht das Verb an zweiter Stelle ('Daher prüfen wir das'), im prototypischen mit 'weil' eingeleiteten Nebensatz am Ende. In informeller gesprochener Sprache kann 'weil' nach einer abgeschlossenen Äußerung auch einen eigenständigen Verbzweitsatz anschließen, oft mit Pause oder Neustart: 'Komm kurz rüber, weil: Ich habe etwas für dich.' Das ist eine markierte Gesprächsvariante, keine allgemeine Wortstellungsregel; in formellen Texten und wenn der Grund als integrierter Nebensatz formuliert wird, steht das finite Verb am Ende. Selbstreparatur mit 'nicht A, sondern B' oder 'genauer gesagt': 'Das betraf nicht eine einzelne Person, sondern die ganze Kette.' Indirekte Fragen geben dem Gespräch Zeit: 'Ich wüsste gern, ob Sie den Moment des Schaltvorgangs meinen.' Die Modalpartikeln 'eben' und 'halt' stehen typischerweise unbetont im Mittelfeld ('Der macht eben, was er will') und nicht allein im Vorfeld. Sie können in einem verneinten Satz oder einer Frage stehen, tragen aber selbst nicht die Satznegation oder den Frageinhalt. Ihre Wirkung hängt von Betonung und Beziehung ab.",
            question = "Wie reagiert der Trainer auf die Frage nach dem Sohn in der Viererkette?",
            options = listOf(
                "Er erklärt, warum der Junge weniger Talent habe als die anderen.",
                "Er lehnt Aussagen über einzelne Kinder am Spielfeldrand ab, nennt das allgemeine Prinzip des Positionswechsels und nimmt die Frage mit.",
                "Er ignoriert die Frage und spricht wieder über das Rondo.",
                "Er verspricht, dass der Junge ab sofort im Mittelfeld spielt."
            ),
            correctOption = 1,
            explanation = "B beschreibt Grenze, Sachgrund und Mitnehmen der Frage. A wäre eine öffentliche Kritik am Kind, die der Trainer gerade vermeidet. C ist falsch, weil er die Frage durchaus aufgreift. D macht eine Zusage, die im Gespräch gar nicht vorkommt; er nennt nur das System der Positionswechsel.",
            controlledPrompt = "Frage im Hybrid-Review: 'Warum geht die Drehmomentanforderung der TCU nicht einfach direkt an den Antrieb?' Bauen Sie eine spontane Antwort in fünf Sätzen: (1) Rückfrage zur Eingrenzung, (2) Kernaussage mit 'Kurz gesagt', (3) Grund mit 'Der Grund ist', (4) Beispiel mit 'Ein Beispiel wäre', (5) Selbstreparatur mit 'Das war ungenau'.",
            controlledModel = "Meinen Sie den Moment des Schaltvorgangs oder das Anfahren? Kurz gesagt: Die ECU koordiniert die Drehmomentanforderungen und prüft sie gegen Bauteilgrenzen. Der Grund ist, dass mehrere Funktionen gleichzeitig Drehmoment anfordern können. Ein Beispiel wäre ein Schaltvorgang bei hoher Last. Das war ungenau: Die TCU wird nicht übergangen; sie fordert an, und die ECU koordiniert die Umsetzung.",
            speaking = "Ein Partner stellt Ihnen drei unerwartete Fragen, jeweils mit etwa 90 Sekunden Antwortzeit und ohne Vorbereitung. Beispiele nur zum Training: Elternfrage 'Warum presst die U13 nach Ballverlust sofort, obwohl es manchmal ein Gegentor gibt?' (Gegenpressing, Umschaltspiel, Halbräume; kein Kind öffentlich kritisieren); Review-Frage 'Warum brauchen wir überhaupt eine Rückverfolgbarkeit zwischen Anforderung und Testfall?'; Allgemeinfrage 'Warum lohnt sich ein Sprachkurs für ein Fachgespräch?'. Nutzen Sie Rückfrage, Kernaussage, Grund, Beispiel und mindestens eine Selbstreparatur.",
            interaction = "A stellt in einem Gespräch am Spielfeldrand nacheinander drei überraschende Fragen und unterbricht einmal; B antwortet kohärent, repariert sich bei Ungenauigkeit und schützt einzelne Kinder vor öffentlicher Kritik. Wendung: A sagt plötzlich 'Das ist doch eh halt so beim Training'. B ordnet den Ton ein, fragt freundlich nach ('Was meinst du damit konkret?') und führt zum Anliegen zurück, ohne gekränkt zu reagieren. Tauschen Sie die Rollen.",
            writing = "Nehmen Sie eine eigene spontane Antwort aus der Sprechübung kurz auf und schreiben Sie fünf Sätze davon als Transkript auf. Markieren Sie Füllwörter, Abbrüche und Reparaturen. Verfassen Sie dann einen Reflexionstext von 150–190 Wörtern: Was war gut gegliedert? Wo fehlte ein Grund oder Beispiel? Wie hätten Sie die Frage klarer eingegrenzt? Schreiben Sie anschließend eine verbesserte Fassung der Antwort in zwei bis drei Sätzen.",
            transfer = "Neuer Kontext, ohne Vorbereitung: Nach einem kurzen Vortrag in einer Vereinsversammlung zu einem Thema, das Sie selbst wählen, stellt eine Person aus dem Publikum drei überraschende Fragen, eine davon kritisch und im Ton ungeduldig ('Das ist halt wieder typisch'). Antworten Sie ohne Notizen, gliedern Sie hörbar und reparieren Sie sich bei Bedarf. Diese Übung dient nur dem ersten Training. Für die Prüfung sind frische, bisher unbekannte Fragen und ein anderer Kontext erforderlich; sie folgen frühestens sieben Tage später und werden von einer unabhängigen Person bewertet.",
            feedback = "Modellrückmeldung: Ihre Antwort beginnt mit einer Rückfrage und gewinnt dadurch Zeit; das ist gut. Prüfen Sie, ob nach der Rückfrage wirklich eine Kernaussage folgt, sonst wirkt sie wie Ausweichen. 'Ähm, also, ja, halt …' am Satzanfang verlängert die Pause, gliedert aber nicht; ersetzen Sie es durch 'Kurz gesagt' oder 'Der Grund ist'. Die Selbstreparatur 'Das war ungenau' ist klar; ergänzen Sie sofort die genauere Fassung. Bei der Elternfrage haben Sie das Team statt einer Person genannt und damit niemanden bloßgestellt. Bei 'eben' oder 'halt' im Satz Ihres Gegenübers sollten Sie erst nachfragen, bevor Sie beleidigt reagieren."
        )
    ),
    CurriculumUnit(
        "s2-summarise", "Kernaussagen verdichten", 5, listOf("s1-explain"),
        listOf(
            "Zwei Quellen vergleichen und gemeinsame Aussagen, Unterschiede und offene Punkte getrennt wiedergeben.",
            "Einschränkungen wie Datenbasis, Simulation oder Schätzung in der Verdichtung bewahren.",
            "Eine Zusammenfassung an Leser ohne Detailkenntnis anpassen und Fachbegriffe knapp erklären."
        ),
        contextsC(
            "Zwei Sachbeiträge für Laien zusammenfassen",
            "Eine Vereinsdiskussion trotz 'Das ist doch klar' neutral protokollieren",
            "EV-Fahrdynamikmessungen und FMEA-Befunde für die Projektleitung verdichten",
            "U15-Spielbeobachtungen und Spielerfragen für das nächste Fußballtraining bündeln"
        ),
        UnitContent(
            reading = """
                Aufgabe: Die Projektleitung entscheidet morgen über das Winterprogramm eines Elektro-Prototyps und möchte beide Berichte in einer kurzen Zusammenfassung lesen. Alle Daten sind fiktiv.

                Quelle A – Fahrdynamikbericht (Auszug): Verglichen wurden die Rekuperationsstufen 2 und 3. In fünf Bremsmanövern auf nasser Fahrbahn lieferte Stufe 3 in vier Manövern mehr zurückgewonnene Energie; die Testfahrer bewerteten sie als komfortabler. Auf Fahrbahn mit niedriger Haftung wurde nur ein Manöver gefahren: Das Fahrzeugheck drehte leicht ein, die Stabilitätsregelung (ESC) griff korrigierend ein. Der Reibwert wurde geschätzt, nicht gemessen. Für Stufe 2 liegen auf niedriger Haftung keine Streckenversuche vor.

                Quelle B – FMEA-Befund der Funktionalen Sicherheit (Auszug): Bewertet wurde der Fehlerfall „Rekuperationsmoment bleibt trotz Radschlupf zu hoch“. Mögliche Auswirkung ist eine Instabilität des Fahrzeughecks. Als Maßnahme begrenzt die ESC-Anforderung das Rekuperationsmoment. Die Wirksamkeit wurde in der Simulation für trockene und nasse Fahrbahn bestätigt; ein Nachweis für niedrige Haftung am Fahrzeug fehlt. Der Auszug nennt eine weiterhin hohe Handlungspriorität, enthält aber keine Einzelbewertungen für S, O und D; ihre Herleitung lässt sich aus diesem Ausschnitt nicht prüfen. Empfehlung: Stufe 3 nicht für Winterfahrten freigeben, bevor dieser Nachweis vorliegt.

                Hinweis zur Methode: Eine gute Zusammenfassung ist keine Kürzung nach Zufall. Sie nennt zuerst, was beide Quellen gemeinsam stützen, dann, worin sie sich unterscheiden, und behält die Bedingungen bei, die eine Aussage begrenzen („nur auf nasser Fahrbahn“, „in der Simulation“, „geschätzt“). Wer diese Einschränkungen streicht, macht aus einem Teilbefund eine Freigabeempfehlung. Zugleich muss der Text für Leser ohne Detailkenntnis verständlich bleiben: Begriffe wie ESC oder Handlungspriorität werden bei Bedarf in einem Halbsatz erklärt, Zahlen nur genannt, wenn sie die Aussage tragen. Ob die Quellen sich widersprechen oder lediglich Verschiedenes betrachten, sollte die Zusammenfassung ausdrücklich sagen, statt die Spannung zu glätten.
            """.trimIndent(),
            listening = """
                (Fiktive Besprechung vor dem Lenkungskreis. Herr Nagel nutzt eine fiktive, vereinfachte schwäbische Schreibannäherung (nicht regional validiert, keine Norm).)
                Roth: Ich hab gleich Lenkungskreis und brauch's in drei Sätzen: Können wir Rekuperationsstufe 3 für die Winterversuche nehmen, ja oder nein?
                Nagel: Aus Fahrdynamiksicht erst mal ja. Der Energierückgewinn war besser, die Fahrer fanden's komfortabler.
                Idrissi: Moment, Herr Nagel, so kann das nicht in den Lenkungskreis. Ihre Daten stammen von nasser Fahrbahn, nicht von niedriger Haftung.
                Nagel: Stimmt, mir hen uf Schnee halt bloß oi Manöver gmacht. Da isch des Heck leicht eigedreht, aber die Stabilitätsregelung hat's gfange.
                Idrissi: Eben. Ein Manöver ist kein Nachweis. Im FMEA-Auszug wird die Handlungspriorität weiter als hoch angegeben; die Einzelbewertungen für S, O und D sehen wir hier aber nicht.
                Roth: Okay, stopp, ich versuch's mal: Stufe 3 ist besser und fürs Winterprogramm freigegeben, nur die FMEA ist noch nicht fertig.
                Idrissi: Nein, nein, 'freigegeben' ist falsch. Nicht freigegeben, und nicht 'noch nicht fertig', sondern: Der Nachweis für niedrige Haftung fehlt.
                Roth: Ah, ich verstehe. Also: Stufe 3 zeigt auf nasser Fahrbahn mehr Energierückgewinn und wird als komfortabler bewertet, aber für niedrige Haftung fehlt der Nachweis, deshalb vorerst keine Freigabe für Winterfahrten. Stimmt das so?
                Nagel: Fast. Ich würd noch ergänzen: Die Aussage zum Energievorteil beruht auf fünf Manövern, also auf einer kleinen Stichprobe.
                Roth: Gut, dass Sie das sagen. 'Beruht auf fünf Manövern' nehm ich mit rein.
                Idrissi: Und für Stufe 2? Dazu hab ich bisher keine Aussage gesehen.
                Nagel: Für Stufe 2 liegt die Messung auf Schnee ebenfalls nicht vor. Da müssen wir ehrlich sein.
                Roth: Dann schreib ich: Beide Stufen brauchen einen Nachweis bei niedriger Haftung. Der Lenkungskreis soll das Prüfprogramm dafür freigeben, nicht die Stufe selbst.
                Idrissi: Genau so. Schicken Sie mir den Text bitte vor dem Lenkungskreis.
                Roth: Mach ich. In einer Viertelstunde habt ihr ihn.
                Nagel: Und wenn der Vorsitzende nach dem Komfort fragt?
                Roth: Dann sag ich: ein subjektives Urteil der Testfahrer, kein Messwert.
            """.trimIndent(),
            expressions = listOf(
                "Zusammengefasst lässt sich sagen, dass … – die Kernaussage einleiten",
                "Beide Quellen stimmen darin überein, dass …; sie unterscheiden sich in … – Quellen vergleichen",
                "Laut Quelle A …, während Quelle B … – Positionen zuordnen",
                "…, bislang nur auf … / im untersuchten Umfang / geschätzt – Einschränkungen bewahren",
                "Offen bleibt, ob … – einen offenen Punkt kennzeichnen",
                "Der Bericht stützt sich auf … – die Datenbasis nennen",
                "Kollokationen: Kernaussagen herausarbeiten, Ergebnisse bündeln, eine Einschränkung kenntlich machen, auf den Punkt bringen, Quellen gegenüberstellen, eine Zusammenfassung abstimmen",
                "'Das ist doch klar' im Protokoll nicht übernehmen, sondern neutral wiedergeben: 'X hält den Punkt für vorrangig und begründet das mit …'"
            ),
            grammar = "Verdichten durch Nominalisierung: 'Weil die Rücklage begrenzt ist' wird zu 'wegen der begrenzten Rücklage'. Dabei darf die Aussage nicht stärker werden; 'Messfahrten' sind nicht 'Messreihen'. Berichte zu fremden Aussagen stehen häufig im Konjunktiv I: 'Der Kassenwart führt an, die Rücklage reiche nur für ein Projekt.' Wenn Konjunktiv I und Indikativ gleich lauten, wird der Konjunktiv II genutzt: 'Sie sagten, sie hätten keine Daten.' Einschränkende Adverbien und Präpositionalgruppen ('bislang nur', 'in der Simulation', 'nach vorliegenden Angaben') müssen mit in die Zusammenfassung. Gegensätze markieren 'während', 'wohingegen' und 'dagegen': 'Quelle A betont den Komfort, während Quelle B den fehlenden Nachweis betont.' Das Partizipialattribut verdichtet: 'die in der Simulation bestätigte Maßnahme'.",
            question = "Welche Zusammenfassung gibt Quelle A und B am genauesten wieder?",
            options = listOf(
                "Stufe 3 ist besser und kann auch im Winter freigegeben werden.",
                "Stufe 3 brachte in der kleinen Stichprobe auf nasser Fahrbahn mehr Energie; für niedrige Haftung fehlt der Nachweis, daher wird sie dafür nicht freigegeben.",
                "Die FMEA beweist, dass Stufe 3 gefährlich ist.",
                "Die Quellen widersprechen sich völlig; deshalb lässt sich nichts aussagen."
            ),
            correctOption = 1,
            explanation = "B nennt den Vorteil mit seiner Einschränkung (nasse Fahrbahn, kleine Stichprobe) und den fehlenden Nachweis. A streicht die Einschränkung und macht aus einem Teilbefund eine Freigabe. C verwechselt einen fehlenden Nachweis mit einem Beweis der Gefährlichkeit. D übertreibt den Gegensatz: Die Quellen betrachten teils Verschiedenes und stützen gemeinsam die Aussage, dass für niedrige Haftung Nachweise fehlen.",
            controlledPrompt = "Fassen Sie Quelle A und B in drei Sätzen für Leser ohne Detailkenntnis zusammen: (1) gemeinsame Basis mit 'Beide Quellen …', (2) Unterschied mit 'während', (3) Konsequenz mit 'daher'. Verwenden Sie 'bislang nur' für die Einschränkung und erklären Sie ESC in einem Halbsatz.",
            controlledModel = "Beide Quellen stützen, dass Stufe 3 auf nasser Fahrbahn bislang nur in einer kleinen Stichprobe von fünf Manövern mehr Energie zurückgewann; Quelle A betont zudem den Komfort, während Quelle B darauf verweist, dass für niedrige Haftung ein Nachweis am Fahrzeug fehlt, obwohl die Stabilitätsregelung (ESC) das Rekuperationsmoment begrenzen soll. Quelle B nennt eine hohe Handlungspriorität, legt die Einzelbewertungen für S, O und D im Auszug aber nicht offen. Daher wird Stufe 3 für Winterfahrten vorerst nicht freigegeben, und auch für Stufe 2 fehlen Versuche auf niedriger Haftung.",
            speaking = "Fassen Sie in 90 Sekunden ohne Vorlage die Beobachtungen und Fragen einer fiktiven U15 für das nächste Training zusammen. Quelle Trainerbeobachtung: In zwei von vier beobachteten Spielen setzte das Gegenpressing nach Ballverlust im Spielaufbau spät ein, sodass die Halbräume offen blieben. Quelle Spielerfragen: 'Wenn wir vorn pressen, kommen wir hinten mit der Viererkette nicht hinterher. Müssen wir dann trotzdem sofort Druck machen?' Quelle Co-Trainer: Im Rondo funktionierte der erste Druck gut, im Kleinfeldspiel weniger. Nennen Sie gemeinsame Beobachtung, Spannung zwischen Quellen, Einschränkung und Trainingsfokus; erfinden Sie keine Statistiken.",
            interaction = "A ist die Projektleiterin und verlangt 'nur einen Satz'. B fasst Quelle A und B zusammen, ohne Einschränkungen zu streichen. Wendung: Der Vorsitzende fragt, was Quelle B zu Stufe 2 sage. B erklärt ehrlich, was nicht in den Quellen steht, statt zu raten, und nennt die nächste Prüfung. A will die Einschränkung 'wegen Terminlage' weglassen; B begründet, warum sie bleibt. Tauschen Sie die Rollen.",
            writing = "Schreiben Sie auf Grundlage der Quellen A und B aus dem Lesetext eine Zusammenfassung von 120–150 Wörtern für den Lenkungskreis (Leser ohne Fahrdynamikkenntnis). Gliedern Sie in Gemeinsames, Unterschied, Einschränkungen und nächster Schritt. Erklären Sie ESC und Handlungspriorität knapp, nennen Sie nur Zahlen, die die Aussage tragen, und erfinden Sie keine zusätzlichen Messwerte.",
            transfer = "Neuer Kontext (Vereinsdiskussion), zur Orientierung mit zwei Quellen. Beitrag 1 (Kassenwart, fiktiv): 'Die Rücklage reicht dieses Jahr nur für ein größeres Projekt. Neue Trikots sind schön, aber nicht dringend. Die Flutlichtanlage fällt im Winter öfter aus; das ist doch klar die Priorität.' Beitrag 2 (Jugendleiterin, fiktiv): 'Für zwei Jugendmannschaften fehlen komplette Trikotsätze; sie spielten schon in Trainingsleibchen. Das ist doch klar die Priorität. Beim Flutlicht könnte man übergangsweise den Trainingsplatz tauschen.' Schreiben Sie ein neutrales Protokoll von 100–140 Wörtern mit Positionen, gemeinsamer Basis, offenen Punkten und nächstem Schritt, ohne 'doch klar' zu übernehmen. Diese Übung dient nur dem ersten Training. Für die Prüfung sind frische, bisher unbekannte Quellen erforderlich; sie folgen frühestens sieben Tage später und werden von einer unabhängigen Person bewertet.",
            feedback = "Modellrückmeldung: Ihre Zusammenfassung nennt Vorteil und Einschränkung gemeinsam, das ist der wichtigste Punkt. Prüfen Sie, ob 'Stufe 3 ist besser' ohne 'auf nasser Fahrbahn' noch stehen bleibt; ohne diesen Zusatz würde der Leser eine Freigabeempfehlung herauslesen. 'Die Quellen sind sich uneinig' ist zu pauschal; genauer: 'Quelle A betrachtet Energie und Komfort, Quelle B den fehlenden Nachweis bei niedriger Haftung.' Im Vereinsprotokoll sollten 'doch klar' und Wertungen entfallen. Modellzusammenfassung: 'Für Rekuperationsstufe 3 liegen auf nasser Fahrbahn Hinweise auf höheren Energierückgewinn und besseren Komfort vor, die auf fünf Manövern beruhen. Für niedrige Haftung fehlt ein Nachweis am Fahrzeug. Quelle B nennt die Handlungspriorität hoch, doch die Einzelbewertungen für S, O und D sind im Auszug nicht abgedruckt. Empfohlen wird, das Prüfprogramm für niedrige Haftung freizugeben, nicht die Stufe für Winterfahrten.'"
        )
    ),
    CurriculumUnit(
        "s2-mediate", "Zwischen Perspektiven vermitteln", 5, emptyList(),
        listOf(
            "Zwei Positionen neutral und für beide Seiten akzeptabel wiedergeben.",
            "Gemeinsame Interessen und echte Unterschiede trennen und daraus einen überprüfbaren Vorschlag entwickeln.",
            "Unklare oder regionale Wendungen rückversichern, statt sie als Zustimmung oder Ablehnung zu deuten."
        ),
        contextsC(
            "Eine öffentliche Debatte erklären",
            "Zwischen Nachbarn vermitteln und bei regionalen Wendungen die gemeinte Aussage rückversichern",
            "Zwischen ECU-Drehmomentregelung und TCU-Schaltstrategie im Hybrid-Team vermitteln",
            "Zwischen U14-Spielern, Eltern und Trainerteam über Einsatzzeiten vermitteln"
        ),
        UnitContent(
            reading = """
                Mediation heißt hier nicht, eine Seite zu übersetzen oder zu überreden, sondern zwei Positionen so wiederzugeben, dass beide sich richtig verstanden fühlen, und Gemeinsames sichtbar zu machen. Das folgende Beispiel ist fiktiv. Im Hybrid-Team streiten die Getriebe- und die Motorsteuerungsentwicklung über den Drehmomenteingriff beim Hochschalten.

                Position A – Frau Lindner, TCU (Getriebesteuerung): „Beim Hochschalten muss das Antriebsmoment kurz und schnell zurückgenommen werden, sonst dauert die Schaltung zu lange. In den Messfahrten erreichen wir die Zielschaltzeit nicht, weil die ECU den Momentgradienten stark begrenzt. Testfahrer nennen die Schaltungen zäh. Wir brauchen einen steileren Gradienten.“

                Position B – Herr Mertens, ECU (Antriebssteuerung): „Die Begrenzung schützt Bauteile und Komfort. Ein sehr steiler Momenteingriff regt Schwingungen im Triebstrang an, die als Ruckeln spürbar werden. Außerdem überwacht die Safety-Funktion Abweichungen beim Drehmoment; ein steilerer Gradient müsste dagegen erst abgesichert werden. Ohne Nachweis ändern wir ihn nicht.“

                Eine gute Vermittlung beginnt nicht mit einem Urteil. Sie hält zuerst fest, worin beide übereinstimmen: Beide wollen eine kurze und zugleich ruckfreie Schaltung, und beide wollen keine Bauteilbelastung oder Safety-Verletzung riskieren. Der Unterschied liegt in der Gewichtung: Die TCU-Seite stellt die Schaltzeit in den Vordergrund, die ECU-Seite die Schwingungsanregung und die Absicherung. Offen ist, wie stark ein steilerer Gradient Schaltzeit und Längsruck tatsächlich verändert, denn dazu fehlt bisher eine gemeinsame Messreihe. Ein möglicher Vorschlag: drei Gradientenstufen im selben Fahrzeug, dieselbe Messkette, zwei gemeinsame Kennwerte (Schaltzeit und Längsruck) und vor jeder Änderung von Serienparametern die Absicherung durch die Safety-Überwachung. Die Vermittlerin entscheidet nicht, wer recht hat, sondern was als Nächstes gemessen wird.
            """.trimIndent(),
            listening = """
                (Fiktives Nachbarschaftsgespräch mit Vermittlerin. Herr Beck nutzt eine fiktive, vereinfachte schwäbische Schreibannäherung (nicht regional validiert, keine Norm).)
                Hofmann: Schön, dass Sie beide gekommen sind. Ich möchte zuerst zuhören und dann kurz zusammenfassen, was ich verstanden habe. Herr Beck, Sie fangen an?
                Beck: Also, des goht so it weiter. Jeden Nachmittag dr Ball an dr Wand, bum, bum, bum – des isch ja a Gschroi!
                Hofmann: Wenn ich Sie richtig verstehe, stört Sie vor allem das Geräusch am Nachmittag, nicht die Kinder an sich. Stimmt das?
                Beck: Ja, die Kinder sind scho recht, aber i han Nachtschicht gschafft, i muss mittags schlofe.
                Aydın: Entschuldigung, aber die Kinder spielen doch erst ab vier! Und nur ab und zu, nicht jeden Tag.
                Beck: Jeden Tag, sag i!
                Hofmann: Moment, Frau Aydın, einen Augenblick. Herr Beck sagt: Er hört den Ball fast täglich, auch vor vier. Sie sagen: Gespielt wird ab vier und nicht täglich. Das sind zwei Wahrnehmungen, die sich prüfen lassen. Hat jemand von Ihnen mal aufgeschrieben, wann gespielt wurde?
                Aydın: Nein. Aber unsere Kinder haben sonst keinen Platz. Der Sportplatz ist abends vom Verein belegt.
                Hofmann: Verstanden. Frau Aydın, Ihnen geht es darum, dass die Kinder einen Ort zum Bewegen haben. Und Ihnen, Herr Beck, um Ruhe in den Stunden, in denen Sie schlafen. Das sind keine gegensätzlichen Ziele, oder?
                Beck: Na ja … wenn mer's so sieht.
                Aydın: Wir könnten doch feste Zeiten ausmachen. Zum Beispiel erst ab halb fünf?
                Beck: Halb fünf, des wär scho möglich.
                Hofmann: Herr Beck, 'des wär scho möglich' – heißt das, Sie stimmen halb fünf zu, oder sind Sie noch skeptisch?
                Beck: Zustimmung unter Vorbehalt. Wenn's klappt, bin i zufrieden.
                Hofmann: Gut. Dann schlage ich vier Wochen Probe vor: ab halb fünf, und der Ballaufprall an der Wand wird gedämpft, zum Beispiel mit einer Matte. Danach treffen wir uns noch einmal. Frau Aydın, was sagen Sie?
                Aydın: Das können wir machen. Ich sag es den Eltern noch heute.
                Hofmann: Und Sie, Herr Beck: Passt es, wenn Sie in vier Wochen sagen, was sich verändert hat?
                Beck: Ja, des passt.
            """.trimIndent(),
            expressions = listOf(
                "Wenn ich Sie richtig verstehe, geht es Ihnen darum, dass … – eine Position rückversichern",
                "Beide Seiten möchten …; sie unterscheiden sich darin, dass … – Gemeinsames und Unterschied benennen",
                "Ich fasse beide Positionen kurz zusammen, bevor wir weitergehen. – neutrale Struktur schaffen",
                "Das sind zwei Wahrnehmungen, die sich prüfen lassen. – Tatsachen und Deutung trennen",
                "Heißt das, dass …, oder meinen Sie …? – indirekte und regionale Wendungen klären",
                "Könnten wir als Probe … vereinbaren und danach auswerten? – eine überprüfbare Option vorschlagen",
                "Kollokationen: zwischen den Positionen vermitteln, Interessen offenlegen, einen Kompromiss ausloten, Gemeinsamkeiten herausarbeiten, eine Probephase vereinbaren, niemanden übergehen",
                "'Des goht it' / 'Des wär scho möglich' (regional, schwäbisch): kann klare Ablehnung, zögerliche Zustimmung oder Höflichkeit bedeuten; deshalb freundlich rückfragen, nicht raten oder den Dialekt nachahmen"
            ),
            grammar = "Fremde Positionen werden mit Verben der Redewiedergabe oder Konjunktiv I neutral wiedergegeben: 'Frau Lindner fordert einen steileren Gradienten', 'Herr Mertens betont, die Begrenzung schütze Bauteile.' Gegensätze und Gemeinsames markieren 'während', 'wohingegen', 'sowohl … als auch', 'weder … noch': 'Beide Seiten wollen sowohl eine kurze als auch eine ruckfreie Schaltung.' 'Wert legen auf' verlangt den Akkusativ mit Präposition: 'Die TCU-Seite legt Wert auf kurze Schaltzeiten', als Frage 'Worauf legen Sie Wert?'. Relativsätze mit Präposition bündeln Interessen: 'die Sicherheit, auf die das ECU-Team Wert legt'. Konjunktiv II macht Vorschläge höflich: 'Könnten wir …?', 'Wir sollten …'. Modalpartikeln und Redewendungen ('scho', 'halt') tragen Ton, nicht Inhalt; ihre Bedeutung wird durch Rückfrage geklärt.",
            question = "Welche Vermittlung entspricht dem Beispiel zwischen TCU- und ECU-Team?",
            options = listOf(
                "Das ECU-Team hat recht, weil Bauteilschutz immer wichtiger als Schaltzeit ist.",
                "Beide wollen eine gute Schaltqualität; sie gewichten Schaltzeit und Schwingungs- beziehungsweise Bauteilschutz verschieden; eine gemeinsame Messreihe klärt den Zielkonflikt, und die Absicherung bleibt bestehen.",
                "Das TCU-Team soll seine Zielschaltzeit aufgeben, damit der Streit endet.",
                "Beide Teams sagen dasselbe; es gibt keinen Konflikt."
            ),
            correctOption = 1,
            explanation = "B benennt Gemeinsamkeit, Unterschied, offene Frage und überprüfbaren Vorschlag. A fällt ein Urteil, obwohl die Daten fehlen, und verallgemeinert ('immer'). C verlangt einseitigen Verzicht, ohne die Messdaten zu klären. D leugnet den echten Konflikt über die Gewichtung von Schaltzeit und Schwingungsschutz.",
            controlledPrompt = "Vermitteln Sie zwischen Frau Lindner (TCU) und Herrn Mertens (ECU) in vier Sätzen: (1) gemeinsames Ziel mit 'sowohl … als auch', (2) Unterschied mit 'während', (3) offene Frage mit 'Offen bleibt, ob …', (4) Vorschlag im Konjunktiv II ('Könnten wir …?'). Keine Seite wird bewertet.",
            controlledModel = "Beide Seiten wollen eine Schaltung, die sowohl kurz als auch ruckfrei ist. Die TCU-Seite legt Wert auf eine kurze Schaltzeit, während die ECU-Seite Schwingungsanregung und die Absicherung des Drehmomenteingriffs in den Vordergrund stellt. Offen bleibt, ob ein steilerer Momentgradient die Schaltzeit stark verbessert und den Längsruck nur gering verschlechtert. Könnten wir drei Gradientenstufen im selben Fahrzeug messen und vor jeder Parameteränderung die Safety-Absicherung klären?",
            speaking = "Vermitteln Sie in zwei Minuten ohne Vorlage mündlich zwischen Frau Lindner (TCU) und Herrn Mertens (ECU) aus dem Lesetext. Geben Sie beide Positionen so wieder, dass die Beteiligten zustimmen könnten, nennen Sie das gemeinsame Ziel, den Unterschied, die offene Frage und einen überprüfbaren Vorschlag. Reagieren Sie auf die Zwischenfrage 'Wer von uns hat denn nun recht?', ohne ein Urteil zu fällen, das die Daten nicht tragen.",
            interaction = "A ist die Vermittlerin im Nachbarschaftsgespräch, B Herr Beck, C (oder B in zweiter Rolle) Frau Aydın. Herr Beck sagt 'Des goht it' und später 'Des wär scho möglich'; A fragt höflich nach, was damit gemeint ist, ohne den Dialekt nachzuahmen. Wendung: Frau Aydıns Sohn möchte selbst sprechen und sagt, er habe den Ball nie vor vier Uhr gespielt; Herr Beck behauptet das Gegenteil. A trennt Wahrnehmung und Tatsache, schlägt eine vierwöchige Probe mit einem einfachen Notizzettel vor und fasst Vorbehalte beider Seiten zusammen. Tauschen Sie die Rollen.",
            writing = "Schreiben Sie für eine Lokalzeitung einen neutralen Erklärtext von 150–190 Wörtern zu einer fiktiven Debatte in einer erfundenen Stadt. Quelle 1 (Anwohnerinitiative): 'Das Flutlicht am Kunstrasenplatz brennt bis spät abends; wir wünschen uns feste Ruhezeiten.' Quelle 2 (Sportverein): 'Viele Jugendmannschaften brauchen die späten Zeiten, weil Schule und Arbeit früher nicht möglich sind; wir bieten ein Gespräch über Zeiten an.' Beschreiben Sie beide Positionen fair, nennen Sie das gemeinsame Ziel, den Streitpunkt und zwei mögliche Lösungsschritte, ohne Partei zu ergreifen.",
            transfer = "Neuer Kontext (U14-Fußball), zur Orientierung mit drei Quellen. Spieler (fiktiv): 'Ich trainiere immer voll mit und spiele trotzdem nur zehn Minuten. Wenn ich einen Fehler mache, werde ich gleich ausgewechselt.' Mutter (fiktiv): 'Wir wollen keine Stammplatzgarantie. Wir wünschen uns aber, dass uns jemand sagt, wonach die Einsatzzeit entschieden wird.' Trainerteam (fiktiv): 'Die Einsatzzeit richtet sich nach Trainingsbeteiligung, Position, Spielplan und Belastung; im Spielaufbau und beim Gegenpressing brauchen wir bestimmte Rollen. Rotation ist vorgesehen, wir haben sie aber nicht erklärt.' Vermitteln Sie in einem dreiminütigen Gespräch und halten Sie in einer Notiz von 100–130 Wörtern Gemeinsames, Unterschiede und eine Vereinbarung fest; niemand wird öffentlich kritisiert. Diese Übung dient nur dem ersten Training. Für die Prüfung sind frische, bisher unbekannte Quellen erforderlich; sie folgen frühestens sieben Tage später und werden von einer unabhängigen Person bewertet.",
            feedback = "Modellrückmeldung: Sie geben beide Positionen mit ähnlicher Ausführlichkeit wieder, und Ihr Vorschlag ist überprüfbar; das ist gute Vermittlung. Vermeiden Sie Urteile wie 'Die ECU-Seite blockiert nur', denn damit werten Sie. Die Wendung 'Des wär scho möglich' sollte nachgefragt, nicht als Zusage gelesen werden. Prüfen Sie außerdem, ob Sie das Gemeinsame wirklich benannt haben und nicht nur beide Seiten nacheinander zitieren. Modellvermittlung: 'Beide Seiten möchten eine Schaltung, die kurz und ruckfrei ist. Die TCU-Seite gewichtet die Schaltzeit stärker, die ECU-Seite Schwingungen und Absicherung. Ob ein steilerer Gradient den Längsruck stark verschlechtert, ist offen. Deshalb schlage ich eine gemeinsame Messreihe mit drei Stufen vor; vor einer Parameteränderung wird die Safety-Überwachung geprüft.'"
        )
    )
)
