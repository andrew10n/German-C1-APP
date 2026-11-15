package com.example.programme

private fun contextsD(general: String, social: String, engineering: String, coaching: String) =
    mapOf(
        ContextTrack.GENERAL to general,
        ContextTrack.SOCIAL to social,
        ContextTrack.ENGINEERING to engineering,
        ContextTrack.COACHING to coaching
    )

/**
 * Stage 5/6 units. Stage 6 units contain practice variants of integrated tasks only;
 * genuine unseen verification items belong to the separate assessment bank.
 */
internal val unitsPartD: List<CurriculumUnit> = listOf(
    CurriculumUnit(
        "s4-ethics", "Dilemmata erörtern", 5, emptyList(),
        listOf(
            "Zwei berechtigte Werte oder Interessen benennen, die in einem Entscheidungsdilemma kollidieren, und die betroffenen Perspektiven fair wiedergeben.",
            "Eine begrenzte, überprüfbare Entscheidung mit Bedingungen und Revisionstermin begründen, ohne Tradition oder Termindruck als alleinige Begründung zu übernehmen."
        ),
        contextsD(
            "Datenschutz und Informationsbedarf abwägen",
            "Teilhabe und Aufwand diskutieren, ohne 'Das war schon immer so' als Begründung zu übernehmen",
            "Safety-Nachweise und Lieferdruck bei einer EV-Steuergerätefreigabe abwägen",
            "U15-Leistungsziele, faire Spielzeit und Schutz persönlicher Spielerdaten abwägen"
        ),
        UnitContent(
            reading = """
                Nach der Hinrunde steht im Jugendfußballverein eine Grundsatzfrage an. Die U15 spielt in einer leistungsorientierten Liga, und das Trainerteam möchte künftig mit Trainingsdaten arbeiten: Mit Brustgurten ließen sich Belastung und Laufwege erfassen, und eine Videoanalyse könnte zeigen, ob die Viererkette beim Gegenpressing und im Umschaltspiel zu weit auseinanderrückt. Der Sportliche Leiter erhofft sich davon eine gezieltere Trainingssteuerung und eine nachvollziehbarere Grundlage für Gespräche über Spielzeit.

                Einerseits ist dieses Interesse legitim, denn Entscheidungen über Einsatzzeiten wirken weniger willkürlich, wenn sie sich belegen lassen. Andererseits handelt es sich um personenbezogene Daten Minderjähriger. Gesundheitsnahe Werte wie die Herzfrequenz verdienen besondere Vorsicht. Zudem können Daten unbeabsichtigt Druck erzeugen: Wer im Vergleich schlechter aussieht, fühlt sich womöglich bloßgestellt, obwohl die Zahlen nur einen Ausschnitt des Spiels zeigen. Hinzu kommt, dass einzelne Eltern möglichst viel Spielzeit für ihr Kind wünschen, weil ein Wechsel in ein Leistungszentrum denkbar sei.

                Der Vorstand will deshalb keine Pauschalentscheidung treffen. Als Diskussionsvorschlag gilt: Zuerst wird geprüft, welche Daten für einen klaren Trainingszweck wirklich nötig sind und ob sich das Ziel mit weniger personenbezogenen Daten erreichen lässt. Denkbare Schutzmaßnahmen wären verständliche Informationen für Spieler und Eltern, eine möglichst geringe Datenerhebung, begrenzter Zugriff und eine festgelegte Löschfrist. Ob und auf welcher Rechtsgrundlage überhaupt Daten erhoben werden dürfen, wie mit gesundheitsbezogenen Werten umzugehen ist und welche Beteiligung oder Wahlmöglichkeiten nötig sind, muss vorab fachkundig geprüft werden. Eine Zustimmung allein macht die Verarbeitung nicht automatisch rechtmäßig. Auch darf eine Ablehnung zusätzlicher Trainingsmessungen nicht ohne sachlichen Grund zu Nachteilen bei der Aufstellung führen. Die rechtliche Prüfung dieses Vorschlags steht noch aus.

                Für faire Spielzeit soll unabhängig von der Datenlage ein eigener Grundsatz gelten: Alle Spieler des Kaders erhalten in der Hinrunde regelmäßig Einsatzzeiten, und Abweichungen werden begründet und besprochen. Ob dieser Kompromiss genügt, ist offen. Er löst den Zielkonflikt nicht vollständig auf, macht ihn aber bearbeitbar und überprüfbar: Nach einer Probephase von drei Monaten entscheidet die Mitgliederversammlung erneut, und zwar auf der Grundlage der Rückmeldungen von Spielern, Eltern und Trainerteam.
            """.trimIndent(),
            listening = """
                Vogel: Gut, dann zur Freigabe der Rekuperationsfunktion für die nächste Erprobungsphase. Frau Demir, wo stehen wir beim Safety-Nachweis?
                Demir: Also, die Gefahren- und Risikoanalyse ist abgeschlossen, und die Überwachungsfunktion ist spezifiziert. Offen ist der Nachweis, dass die Fehlerreaktionszeit am Prüfstand wirklich eingehalten wird. Der Testlauf ist für Donnerstag geplant.
                Haas: Moment, Donnerstag? Der Kunde will die Fahrzeuge aber schon am Montag sehen. Das ist, äh, ziemlich knapp.
                Demir: Das verstehe ich. Nur: Ein Termin ist kein Nachweis.
                Kowalski: Ich muss kurz was ergänzen. Wir haben die Funktion in der Simulation durchgespielt, und da war nichts Auffälliges. Des passt scho, würd ich sagen.
                Demir: Herr Kowalski, „nichts Auffälliges in der Simulation“ ist ein guter Hinweis, aber kein Ersatz für den Prüfstandsnachweis. Und „passt schon“ kann ich so nicht ins Protokoll schreiben.
                Kowalski: Stimmt, das war jetzt eher ein Bauchgefühl.
                Vogel: Okay, dann sortieren wir mal. Herr Haas, was genau braucht der Kunde am Montag?
                Haas: Eigentlich nur eine Vorführung. Fahren muss da keiner damit, glaube ich ... also, na ja, im Idealfall schon eine kurze Runde.
                Vogel: „Im Idealfall“ reicht mir nicht. Ist es eine statische Vorführung oder eine Fahrt?
                Haas: Statisch geht, wenn ich es vorher so ankündige.
                Demir: Dann sehe ich kein Problem, sofern die neue Funktion nicht im Fahrbetrieb aktiviert wird und das Fahrzeug im Rahmen der bestehenden Freigabe bleibt. Eine Fahrerprobung mit der neuen Funktion würde ich erst nach dem Prüfstandsnachweis empfehlen.
                Kowalski: Dann hätten wir den Test am Donnerstag und könnten die Fahrerprobung danach starten.
                Vogel: Gut. Ich halte fest: Statische Vorführung am Montag, keine Fahrt mit der neuen Funktion, Fahrerprobung erst nach bestandenem Nachweis. Frau Demir, dokumentieren Sie die Begründung?
                Demir: Ja, mit Bedingungen und offenen Punkten. Und falls der Test am Donnerstag scheitert, verschiebt sich die Fahrerprobung entsprechend, ohne neue Diskussion.
                Haas: Das kann ich dem Kunden so sagen. Besser, als etwas zu versprechen, das wir nicht halten können.

                Hinweis (fiktive Dialektannäherung, nicht regional validiert): „Des passt scho“ ist schwäbisch gefärbt und bedeutet hier etwa „Das passt schon“. Als Nachweis taugt der Satz nicht, auch wenn er freundlich gemeint ist. Die Besprechung ist fiktiv; eine echte Freigabe folgt dem vorgesehenen Safety-Prozess.
            """.trimIndent(),
            expressions = listOf(
                "Einerseits …, andererseits … – zwei berechtigte Ansprüche nebeneinanderstellen, ohne einen vorschnell abzuwerten",
                "Hier kollidieren zwei Werte: … und … – den Kern eines Dilemmas benennen",
                "Eine Abwägung zugunsten von … ist vertretbar, sofern … – eine Entscheidung mit Bedingungen verbinden",
                "Angesichts des ausstehenden Nachweises … – einen Entscheidungsgrund im Nominalstil formulieren",
                "Dass es schon immer so gemacht wurde, erklärt die Gewohnheit, begründet aber nicht, warum es so bleiben soll – Tradition von Begründung trennen",
                "Ich sehe das größere Risiko darin, dass … – die eigene Gewichtung offenlegen",
                "Kollokationen: einen Zielkonflikt bearbeitbar machen, Bedenken ernst nehmen, Verantwortung tragen, eine Entscheidung revidieren, Risiken gegeneinander abwägen",
                "Regional (schwäbisch): 'Des hent mir scho allaweil so gmacht' – als Hinweis auf Gewohnheit verstehen und freundlich nach dem heutigen Nutzen fragen; den Dialekt weder korrigieren noch nachahmen"
            ),
            grammar = "Zielkonflikte lassen sich mit konzessiven und konditionalen Strukturen genau darstellen. 'Obwohl der Termin drängt, fehlt der Nachweis': Im obwohl-Satz steht das finite Verb am Ende, der Hauptsatz folgt mit Verb an erster Stelle. 'Obwohl' markiert einen Gegensatz zur Erwartung, nicht einfach ein weiteres Argument. 'Zwar … , jedoch …' stellt Zugeständnis und Einschränkung gegenüber. 'Sofern' und 'solange' begrenzen die Geltung: 'Eine statische Vorführung ist vertretbar, sofern die Funktion nicht aktiviert wird.' 'Angesichts' verlangt den Genitiv: 'angesichts des ausstehenden Nachweises'. Der Konjunktiv II kennzeichnet Abwägungen und Vorschläge: 'Man könnte die Daten erheben, müsste aber zuerst Zweck, Erforderlichkeit und Rechtsgrundlage prüfen lassen.' Erhoben würden nur … ist eine Planung, keine beschlossene Praxis; weder Einwilligung noch Widerspruchsmöglichkeit ersetzen für sich die rechtliche Prüfung.",
            question = "Welche Entscheidung trifft die Runde im Hörtext zur Rekuperationsfunktion?",
            options = listOf(
                "Die Fahrerprobung beginnt am Montag, weil die Simulation unauffällig war.",
                "Am Montag gibt es nur eine statische Vorführung; die Fahrerprobung folgt erst nach bestandenem Prüfstandsnachweis.",
                "Der Kundentermin wird abgesagt, bis alle Safety-Nachweise vollständig vorliegen.",
                "Die Funktion wird für die Serie freigegeben, sobald die Safety-Managerin zustimmt."
            ),
            correctOption = 1,
            explanation = "B gibt die vereinbarte begrenzte Entscheidung mit ihren Bedingungen wieder. A behandelt einen Simulationshinweis als Nachweis, was Frau Demir ausdrücklich ablehnt. C geht über den Text hinaus: Der Kundentermin bleibt bestehen, nur in anderer Form. D verwechselt eine Erprobungsentscheidung mit einer Serienfreigabe; davon ist im Gespräch nicht die Rede.",
            controlledPrompt = "Bilden Sie vier Sätze ohne neue Fakten zum U15-Dilemma: (1) einerseits … andererseits (Trainingssteuerung / Daten Minderjähriger); (2) sofern (Datenminimierung und fachkundige rechtliche Prüfung); (3) obwohl (Zahlen wirken objektiv / nur ein Ausschnitt); (4) angesichts + Genitiv (Folge für Spielzeitentscheidungen).",
            controlledModel = "Einerseits können Trainingsdaten die Belastungssteuerung unterstützen, andererseits betreffen sie personenbezogene Daten Minderjähriger. Eine Erhebung kommt nur infrage, sofern Zweck, erforderliche Daten und Rechtsgrundlage fachkundig geprüft wurden. Obwohl Zahlen objektiv wirken, zeigen sie nur einen Ausschnitt des Spiels. Angesichts dieses Ausschnitts sollten Einzelwerte nicht die alleinige Grundlage für Entscheidungen über Spielzeit sein.",
            speaking = "Erörtern Sie ohne Vorlage in zwei Minuten das U15-Dilemma aus dem Lesetext: Nennen Sie die zwei kollidierenden Werte, die Betroffenen, eine begrenzte Entscheidung mit Bedingungen und einen Termin, an dem sie überprüft wird. Beziehen Sie die Elternperspektive fair ein, ohne den Eltern ein Motiv zu unterstellen. Erfinden Sie keine Rechtsauskunft und keine Messwerte.",
            interaction = "A moderiert die Vorstandssitzung zur Datenerhebung bei der U15. B ist ein Elternteil, das mehr Spielzeit für den Sohn wünscht und zuerst sagt: 'Des hent mir scho allaweil so gmacht' (regional: Das haben wir schon immer so gemacht). Twist: Nach A's offener Rückfrage nennt B ein neues, berechtigtes Anliegen, nämlich die Sorge, dass Einzelwerte des Sohnes für andere Eltern sichtbar werden. A trennt Gewohnheit von Begründung, nimmt das neue Argument ernst und schlägt eine begrenzte Probephase mit Revisionstermin vor. Weder Dialekt korrigieren noch nachahmen. Tauschen Sie die Rollen.",
            writing = "Schreiben Sie eine Stellungnahme von 200–240 Wörtern: Ein Verein erwägt, eine Telefonliste aller Mitglieder an alle Mitglieder zu verteilen, damit Fahrgemeinschaften leichter entstehen. Benennen Sie den Informationsbedarf und die Datenschutzinteressen, gewichten Sie beides begründet und schlagen Sie eine begrenzte Lösung mit Bedingungen vor, zum Beispiel freiwillige Eintragung. Verwenden Sie mindestens zwei konzessive Strukturen und eine Revisionsbedingung.",
            transfer = "Persönliche Transferübung: Ein Verein feiert sein Sommerfest bisher abends in der Halle. Familien mit kleinen Kindern und Mitglieder im Schichtdienst nehmen kaum teil. Ein Mittagsfest könnte mehr Teilhabe ermöglichen, bindet aber zusätzliche Helfende; einige sagen: 'Das war schon immer so.' Erörtern Sie beide Seiten und schlagen Sie einen begrenzten Versuch mit Auswertungstermin vor. Selbstcheck: Haben Sie Gewohnheit von Begründung getrennt, keine Zahlen erfunden und eine Bedingung für die Revision genannt? Wiederholen Sie nach einer Woche mit einem selbst gewählten Dilemma. Das bleibt Übung, keine unabhängige Bewertung.",
            feedback = "Modellrückmeldung: Sie benennen beide Werte und begrenzen die Entscheidung durch Bedingungen, das macht Ihre Position überprüfbar. 'Datenschutz ist einfach wichtiger' bleibt dagegen eine Behauptung; besser: 'Ich gewichte den Schutz der Daten Minderjähriger höher, weil einmal geteilte Einzelwerte später nur schwer wieder vollständig zurückgeholt werden können.' Im Hörtext gelingt es, 'passt schon' als Stimmungsäußerung zu erkennen und trotzdem einen Nachweis einzufordern. Achten Sie darauf, dass 'angesichts des Termindrucks' kein Argument für eine Freigabe ist, sondern ein Grund, den Prüfumfang bewusst zu begrenzen. Nennen Sie am Schluss immer, was die Entscheidung ändern würde."
        )
    ),
    CurriculumUnit(
        "s5-facilitate", "Mehrparteiengespräche moderieren", 5, emptyList(),
        listOf(
            "Beiträge verschiedener Rollen paraphrasieren, bündeln und in ein Zwischenergebnis überführen.",
            "Zurückhaltende Personen einladen, ohne sie zu drängen, und Dissens sichtbar festhalten.",
            "Ergebnisse mit Verantwortlichen und Terminen sichern und im Protokoll von Aussagen anderer unterscheiden."
        ),
        contextsD(
            "Ein Bürgergespräch moderieren",
            "Eine schwäbische Vereinssitzung moderieren, regionale Wendungen bei Bedarf erklären und niemanden zum Dialekt drängen",
            "Ein interdisziplinäres EV-FMEA-Review mit ECU-, TCU- und Safety-Team moderieren",
            "Ein U15-Saisongespräch mit Jugendspielern, Eltern und Trainerteam moderieren"
        ),
        UnitContent(
            reading = """
                Moderationsleitfaden für das EV-FMEA-Review

                Das Review führt Fachleute aus drei Bereichen zusammen: Das ECU-Team verantwortet die Drehmomentbereitstellung, das TCU-Team die Schaltstrategie, und das Safety-Team prüft, ob Fehlermöglichkeiten mit den Sicherheitszielen nach ISO 26262 vereinbar sind. Alle drei verwenden dieselben Wörter unterschiedlich. Eine „Drehmomentanforderung“ kann für die TCU ein Eingriffswunsch während des Schaltvorgangs sein, für die ECU ein Signal mit Prioritätslogik und für das Safety-Team eine Schnittstelle, deren Ausfall oder Verzögerung bewertet werden muss. Die Moderation lässt deshalb zu Beginn zentrale Begriffe klären und nennt das Ziel des Termins: Es werden Fehlermöglichkeiten bewertet, keine Schuldigen gesucht.

                Pro Fehlermöglichkeit werden Bedeutung (S, Severity), Auftreten (O, Occurrence) und Entdeckung (D, Detection) besprochen. Nach dem FMEA-Handbuch von AIAG und VDA ergibt sich daraus eine Aktionspriorität (hoch, mittel, niedrig) und nicht mehr allein die früher übliche Risikoprioritätszahl, das Produkt aus S, O und D. Das ist mehr als eine Formalie: Ein einfaches Produkt kann sehr unterschiedliche Konstellationen gleich bewerten, sodass bei hoher Bedeutung eine Rückfrage nötig bleibt, auch wenn Auftreten und Entdeckung günstig erscheinen.

                Moderierende schlagen keine Bewertungen vor, sondern fragen nach Annahmen: Worauf stützt sich die Einschätzung zum Auftreten? Welche Prüfung würde den Fehler wirklich entdecken? Leise Stimmen werden gezielt einbezogen, etwa durch die Frage an Personen, die bisher nichts gesagt haben, ob sie aus ihrer Sicht ein Risiko sehen. Zwischendurch fasst die Moderation zusammen: Was ist entschieden, was bleibt offen, wer klärt was bis wann? Uneinigkeit wird dokumentiert und nicht übertüncht. Bei hoher Aktionspriorität müssen Maßnahmen festgelegt oder eine nachvollziehbare Begründung dokumentiert werden. Über Freigaben entscheidet nicht das Review allein, sondern der dafür vorgesehene Prozess.
            """.trimIndent(),
            listening = """
                Roth: Schön, dass alle da sind. Ich schlage einen Ablauf vor: erst ein Rückblick auf die Hinrunde, dann Spielzeit und Rollen, zum Schluss Ziele für die Rückrunde. Wir haben neunzig Minuten. Kann jeder damit leben?
                Berger: Passt für mich.
                Brandl: Ja, aber eins vorab: Des isch net, wie mir's b'sproche hen. Mei Bua spielt bloß noch zwanzig Minuten.
                Roth: Danke, Herr Brandl, dass Sie das gleich ansprechen. Wenn ich Sie richtig verstehe, geht es Ihnen darum, dass die Einsatzzeit Ihres Sohnes kürzer geworden ist, als Sie es zu Saisonbeginn erwartet hatten. Stimmt das so?
                Brandl: Genau. Und keiner hat mir gesagt, warum.
                Roth: Das nehme ich auf. Ich schlage vor: Herr Berger erklärt zuerst, wie er die Rollen in der Viererkette und im Spielaufbau verteilt hat, danach sprechen wir über die Spielzeit. Herr Berger?
                Berger: Also, wir haben nach den ersten Spielen gemerkt, dass wir im Gegenpressing zu weit auseinanderstehen. Deshalb habe ich die Halbräume enger besetzt, und dafür brauchte ich im Mittelfeld Spieler, die nach Ballverlust sofort umschalten. Luca hat da im Training ... ähm, Luca, ich rede gerade über dich, ist das okay?
                Luca: Ja, schon.
                Roth: Luca, du hast bisher noch nichts gesagt. Magst du erzählen, wie du die Hinrunde erlebt hast? Du musst nicht, wenn du nicht willst.
                Luca: Also ... ich hab gemerkt, dass ich weniger spiele, aber ich wusste nicht genau, was ich besser machen soll. Im Training mag ich die Rondos, aber im Spiel war ich beim Umschaltspiel oft zu spät.
                Yilmaz: Das hat Luca mir zu Hause auch erzählt. Mir wäre wichtig, dass er konkrete Aufgaben bekommt, nicht nur ein „Arbeite an dir“.
                Roth: Gut, ich fasse kurz zusammen. Erstens: Die Spielzeit ist gesunken, und die Gründe waren nicht transparent. Zweitens: Luca wünscht sich konkrete Trainingsaufgaben. Frau Yilmaz, Herr Brandl, sehen Sie das auch so, oder fehlt etwas?
                Brandl: Mir fehlt noch: Wird das wieder besser, oder bleibt's so?
                Berger: Das kann ich nicht garantieren. Ich kann aber zusagen, dass es bis zur Winterpause einmal pro Woche ein Kleinfeldspiel gibt, in dem Luca das Umschalten gezielt übt, und nach drei Wochen ein kurzes Gespräch.
                Roth: Herr Brandl, ist das für Sie ein Anfang, oder bräuchten Sie mehr?
                Brandl: Ein Anfang, ja. Aber ich will in drei Wochen auch hören, was sich geändert hat.
                Roth: Dann halte ich fest: Termin in drei Wochen mit Herrn Berger und Luca, Rückmeldung an die Eltern. Das Protokoll übernehme ich und schicke es bis Freitag.

                Hinweis (fiktive Dialektannäherung, nicht regional validiert): „Des isch net, wie mir's b'sproche hen“ ist schwäbisch gefärbt und bedeutet „Das ist nicht, wie wir es besprochen haben“. Die Moderatorin spiegelt den Kern in Standardsprache zurück, ohne die Sprechweise zu korrigieren. Das Gespräch ist fiktiv.
            """.trimIndent(),
            expressions = listOf(
                "Wenn ich Sie richtig verstehe, geht es Ihnen darum, dass … – paraphrasieren und Bestätigung einholen",
                "Das nehme ich auf und schlage vor, es nach … zu behandeln – einen Beitrag würdigen und einordnen",
                "Ich fasse kurz zusammen: … Fehlt aus Ihrer Sicht etwas? – ein Zwischenergebnis sichern",
                "Magst du erzählen, wie du es erlebt hast? Du musst nicht, wenn du nicht willst. – eine zurückhaltende Person einladen, ohne Druck aufzubauen",
                "Ich sehe hier unterschiedliche Annahmen: … – Dissens benennen, statt ihn aufzulösen",
                "Wer klärt was bis wann? – Verantwortung und Termin festhalten",
                "Kollokationen: das Wort erteilen, Redezeit begrenzen, Beiträge bündeln, Ergebnisse sichern, einen Dissens dokumentieren",
                "Regional (schwäbisch): 'Des isch net, wie mir's b'sproche hen' – den Kern in Standardsprache zurückspiegeln, statt die Sprechweise zu korrigieren; Dialekt niemals einfordern"
            ),
            grammar = "Zusammenfassungen und Protokolle trennen eigene Aussagen von fremden. Dafür dient der Konjunktiv I in der indirekten Rede: 'Herr Brandl sagt, er sei mit der Einsatzzeit unzufrieden', 'Luca erklärt, er wünsche sich konkrete Aufgaben', 'Herr Berger betont, er könne nichts garantieren.' Stimmt die Konjunktiv-I-Form mit dem Indikativ überein, weicht man auf den Konjunktiv II aus: 'Die Eltern sagen, sie hätten die Gründe nicht erfahren.' Indirekte Fragen schließen mit Verb am Ende: 'Worauf stützt sich die Einschätzung, ob der Fehler auftritt?' Das Passiv mit Modalverb sichert Verbindlichkeit: 'Maßnahmen müssen festgelegt werden.' Wer moderiert, formuliert Aufforderungen oft als Frage: 'Könnten Sie die Signalverläufe bis Mittwoch bereitstellen?'",
            question = "Wie reagiert Frau Roth auf die erste Beschwerde von Herrn Brandl?",
            options = listOf(
                "Sie verweist auf die Tagesordnung und vertagt das Thema, ohne darauf einzugehen.",
                "Sie gibt den Kern in Standardsprache wieder, lässt ihn bestätigen und legt eine Reihenfolge fest, in der das Anliegen behandelt wird.",
                "Sie bittet Herrn Berger sofort, sich für die geringe Einsatzzeit zu rechtfertigen.",
                "Sie bittet Herrn Brandl, künftig in Standarddeutsch zu sprechen, damit alle ihn verstehen."
            ),
            correctOption = 1,
            explanation = "B beschreibt Paraphrase, Bestätigung und Einordnung. A ignoriert das Anliegen, dabei nimmt Frau Roth es ausdrücklich auf. C würde das Gespräch sofort zu einem Rechtfertigungsduell machen. D ist falsch, weil Frau Roth die Sprechweise gerade nicht thematisiert; sie klärt nur den Inhalt.",
            controlledPrompt = "Fassen Sie im Protokollstil mit Konjunktiv I zusammen, ohne neue Fakten: Herr Brandl: mit Einsatzzeit unzufrieden; Luca: wünscht konkrete Trainingsaufgaben; Herr Berger: kann nichts garantieren, will einen wöchentlichen Kleinfeldspiel-Block anbieten.",
            controlledModel = "Herr Brandl erklärt, er sei mit der Einsatzzeit seines Sohnes unzufrieden. Luca sagt, er wünsche sich konkrete Trainingsaufgaben. Herr Berger betont, er könne keine Einsatzzeit garantieren, wolle aber einen wöchentlichen Kleinfeldspiel-Block anbieten.",
            speaking = "Eröffnen Sie in zwei Minuten ohne Vorlage ein EV-FMEA-Review mit ECU-, TCU- und Safety-Team: Nennen Sie das Ziel, den Ablauf, klären Sie den Begriff 'Drehmomentanforderung' für alle drei Gruppen, erklären Sie S, O und D sowie die Aktionspriorität in einem Satz und laden Sie die Safety-Person ein, ihre Sicht zuerst zu schildern. Betonen Sie, dass keine Schuldigen gesucht werden.",
            interaction = "Vier Rollen: A moderiert, B vertritt das ECU-Team, C das TCU-Team, D das Safety-Team. B und C schieben die Verantwortung für einen verzögerten Momenteneingriff einander zu. Twist: D schweigt, hat aber Bedenken, ob die Entdeckung (D) realistisch bewertet ist. A fasst beide Positionen fair zusammen, lädt D ohne Druck ein, hält den Dissens fest und sichert Aufgaben mit Verantwortlichen und Terminen. Erfinden Sie keine Freigabeentscheidung. Wechseln Sie die Rollen.",
            writing = "Verfassen Sie ein Ergebnisprotokoll von 180–220 Wörtern zum U15-Saisongespräch aus dem Hörtext. Gliedern Sie nach Beschlüssen, offenen Punkten und Verantwortlichen mit Terminen. Geben Sie die Positionen der Beteiligten im Konjunktiv I wieder und trennen Sie sie von den vereinbarten Maßnahmen. Keine Wertung der Personen.",
            transfer = "Persönliche Transferübung: Eine Gemeinde diskutiert eine mögliche Verkehrsberuhigung vor einer Grundschule. Beteiligt sind Anwohnende, ein Gewerbetreibender, eine Elternvertretung, die Verwaltung und eine Jugendliche, die noch nichts gesagt hat. Moderieren Sie die Eröffnung und eine Zwischenzusammenfassung; laden Sie die Jugendliche ein, ohne sie zu drängen, und trennen Sie Beschluss von offenem Punkt. Selbstcheck: Haben Sie alle Positionen fair gebündelt und keine Zahlen oder Beschlüsse erfunden? Spielen Sie nach einer Woche eine neue Moderation mit einem anderen Thema durch.",
            feedback = "Modellrückmeldung: Ihre Paraphrase mit 'Wenn ich Sie richtig verstehe' gibt Herrn Brandl Gelegenheit zur Korrektur, und die Einladung an Luca lässt ihm die Wahl. 'Wir sind uns also alle einig' ist jedoch zu früh, wenn nur zwei Personen gesprochen haben; besser: 'Bisher habe ich zwei Punkte gehört, fehlt Ihnen etwas?' Im FMEA-Review gilt: Die Moderation fragt nach der Grundlage einer Bewertung, bewertet aber nicht selbst. Im Protokoll hilft der Konjunktiv I, Aussagen Dritter nicht als Tatsachen zu übernehmen. Halten Sie am Ende jeder Runde Verantwortliche und Termine fest."
        )
    ),
    CurriculumUnit(
        "s6-general", "Unvorbereiteter allgemeiner Transfer", 6, emptyList(),
        listOf(
            "Ein unbekanntes gesellschaftliches oder alltagsnahes Thema ohne Vorlage verstehen, erklären und erörtern.",
            "Unklare umgangssprachliche Zusagen erkennen und freundlich rückversichern.",
            "Fachwissen für Laien vermitteln, ohne Fachwissen mit Sprachleistung zu verwechseln."
        ),
        contextsD(
            "Ein neues gesellschaftliches Thema erörtern",
            "Eine unbekannte Gruppensituation klären und unklare umgangssprachliche Zusagen rückversichern",
            "Ein unbekanntes EV-Mobilitätsthema für Laien vermitteln, ohne Fachwissen mit Sprachleistung gleichzusetzen",
            "Eine unbekannte allgemeine Elternfrage zum U11-Vereinsalltag verständlich beantworten"
        ),
        UnitContent(
            reading = """
                Übungsvariante – echte ungesehene Aufgaben stehen im Prüfungsmodus. Dieses Set zeigt nur den Aufgabentyp; ein erfolgreiches Bearbeiten hier ist kein Nachweis unabhängiger Beherrschung.

                Laden im Mehrfamilienhaus

                Wer in einer Wohnung lebt, hat oft keinen eigenen Stellplatz mit Steckdose. Genau dort stellt sich beim Umstieg auf ein Elektrofahrzeug die Frage, wie Ladepunkte in Tiefgaragen und auf Hofflächen organisiert werden sollen. Für Mieter regelt § 554 Absatz 1 BGB, dass sie vom Vermieter grundsätzlich die Erlaubnis für bauliche Veränderungen zum Laden elektrisch betriebener Fahrzeuge verlangen können; der Anspruch entfällt, wenn die Maßnahme dem Vermieter auch unter Würdigung der Mieterinteressen nicht zumutbar ist. Wohnungseigentümer können nach § 20 Absatz 2 Nummer 2 WEG angemessene bauliche Veränderungen zum Laden verlangen; über die Durchführung muss im Rahmen ordnungsmäßiger Verwaltung beschlossen werden. Daraus folgt weder eine sofortige Installationsfreigabe noch eine pauschale Kostenregel. Technische Ausführung, Zuständigkeiten und Kosten sind im Einzelfall zu klären. Der Text ist eine Sprachübung, keine Rechtsberatung.

                Technisch ist weniger die einzelne Wallbox das Problem als die Summe: Der Hausanschluss kann nur eine begrenzte elektrische Leistung liefern, und diese Leistung wird auch für Aufzug, Beleuchtung und Haushalte gebraucht. Ein Lastmanagement verteilt die verfügbare Leistung auf die gleichzeitig ladenden Fahrzeuge. Das hat eine Folge, die man vorher ansprechen sollte: Laden mehrere Fahrzeuge zugleich, dauert der einzelne Ladevorgang länger. Über Nacht ist das oft unproblematisch, für Menschen mit wechselnden Schichten kann es lästig sein. Ob ein Netzausbau nötig ist, hängt von Anschluss, Gebäude und Zahl der Ladepunkte ab; pauschale Antworten gibt es nicht.

                Streit entsteht deshalb häufig nicht über die Technik, sondern über Gerechtigkeit: Sollen frühe Interessenten Vorrang haben? Zahlen alle mit, obwohl nur einige laden? Eine Lösung, die allein auf Freiwilligkeit beruht, kann schwer planbar sein; eine zentrale Pflichtlösung wiederum belastet Menschen, die kein Elektrofahrzeug planen.

                Aufgaben (Übungsvariante): 1) Markieren Sie, was der Text als sicher, als vom Einzelfall abhängig und als offen darstellt. 2) Erklären Sie einer Nachbarin in drei Sätzen, warum ein Lastmanagement nötig sein kann. 3) Fassen Sie die beiden Gerechtigkeitsfragen neutral zusammen.
            """.trimIndent(),
            listening = """
                Maier: Dann zum letzten Punkt: Ladepunkte in der Tiefgarage. Frau Okafor hat den Antrag gestellt. Frau Okafor, möchten Sie kurz sagen, worum es geht?
                Okafor: Ja, gern. Ich bekomme im Frühjahr ein Elektroauto und hätte gern eine Wallbox an meinem Stellplatz. Ich wollte vorher wissen, ob das überhaupt geht, also technisch.
                Schwab: Des isch doch a Riesenaufwand! Und am End zahlet mir alle mit.
                Maier: Herr Schwab, Sie sorgen sich also, dass die Kosten auf alle verteilt werden könnten. Das nehme ich als Frage auf. Herr Lindner, Sie sind Elektroniker. Wo liegt das technische Problem?
                Lindner: Klar. Stellen Sie sich den Hausanschluss wie ein Rohr vor, durch das nur eine bestimmte Menge Strom fließen kann. Wenn viele gleichzeitig laden, wird's zu viel. Ein Lastmanagement verteilt die Leistung. Dann lädt jedes Auto halt langsamer, wenn mehrere dranhängen.
                Okafor: Das wäre für mich in Ordnung. Ich lade ohnehin hauptsächlich nachts.
                Lindner: Eben. Ob der Anschluss insgesamt reicht, kann ich von hier aus nicht sagen. Das muss jemand vor Ort messen.
                Schwab: Ja, aber des kriege mer scho hi, ha?
                Maier: Moment, Herr Schwab. „Das kriegen wir schon hin“ – meinen Sie, dass Sie einer Prüfung zustimmen würden, oder dass Sie schon etwas beschließen wollen?
                Schwab: Nein, beschließen nicht. Erst mal prüfen, was es kostet.
                Maier: Gut. Dann schlage ich vor: Wir beauftragen eine Fachfirma mit einer Bestandsaufnahme, und erst danach wird über Kosten und Verteilung abgestimmt. Gibt es Einwände? ... Dann halten wir das so fest.
                Okafor: Ich würde noch gern wissen, bis wann die Firma kommt.
                Maier: Ich frage drei Firmen an und melde mich bis Ende nächster Woche mit Terminen. Ich schicke sie Ihnen per Mail.
                Lindner: Und schreiben Sie bitte rein, dass der Beschluss noch nichts über die Kostenverteilung sagt.
                Maier: Das kommt ins Protokoll.

                Hinweis (fiktive Dialektannäherung, nicht regional validiert): „Des isch doch a Riesenaufwand“ und „des kriege mer scho hi“ sind schwäbisch gefärbt (Das ist doch ein Riesenaufwand / Das kriegen wir schon hin). Die zweite Wendung klingt nach Zusage, ist hier aber keine; deshalb fragt Frau Maier nach. Der Hörtext ist eine Übungsvariante, keine echte Prüfungsaufgabe.
            """.trimIndent(),
            expressions = listOf(
                "Das hängt vom Einzelfall ab; sicher ist nur, dass … – Gesichertes von Offenem trennen",
                "Stellen Sie sich … wie … vor – ein Fachkonzept für Laien bildhaft erklären",
                "Meinen Sie damit, dass …, oder eher, dass …? – eine unklare Zusage rückversichern",
                "Das nehme ich als Frage auf. – einen Einwand würdigen, ohne ihn zu bewerten",
                "Wer macht was bis wann? – eine Vereinbarung konkretisieren",
                "Das lässt sich pauschal nicht beantworten, aber … – eine ehrliche Teilantwort geben",
                "Kollokationen: einen Antrag stellen, einen Beschluss fassen, Kosten verteilen, eine Bestandsaufnahme beauftragen, Vorrang einräumen",
                "Regional (schwäbisch): 'Des kriege mer scho hi' kann Zuversicht oder Zusage bedeuten; freundlich nachfragen, was genau gemeint ist, und den Dialekt nicht nachahmen"
            ),
            grammar = "Passiversatzformen halten Erklärungen sachlich: 'Die Leistung lässt sich verteilen' (statt 'kann verteilt werden'), 'Das ist nicht zu unterschätzen', 'Es bleibt zu klären, wer zahlt.' Relativadverbien verbinden Sätze: 'Das Lastmanagement verteilt die Leistung, wodurch eine Überlastung vermieden wird.' Zur Rückversicherung dienen indirekte Fragen mit Verb am Ende: 'Könnten Sie noch einmal sagen, wer sich bis wann um die Termine kümmert?' Die Modalpartikel 'ja' markiert Bekanntes ('Das ist ja ein bekanntes Problem'), 'doch' kann dagegen Widerspruch oder Drängen ausdrücken; beide können je nach Betonung und Beziehung freundlich oder scharf klingen. Konjunktiv II mildert Wünsche: 'Ich hätte gern eine Wallbox.'",
            question = "Welche Funktion hat laut Text ein Lastmanagement?",
            options = listOf(
                "Es erhöht die Leistung, die der Hausanschluss insgesamt liefern kann.",
                "Es verteilt die vorhandene Leistung auf die gleichzeitig ladenden Fahrzeuge.",
                "Es ersetzt die rechtliche Zustimmung der Eigentümergemeinschaft.",
                "Es schaltet alle Ladepunkte nachts automatisch ab."
            ),
            correctOption = 1,
            explanation = "B gibt die im Text beschriebene Funktion wieder und nennt die Folge längerer Ladezeiten. A verwechselt Verteilen mit Erhöhen; die Leistung des Anschlusses bleibt begrenzt. C vermischt Technik und Rechtslage. D wird im Text nicht erwähnt; nachts zu laden wird nur als häufig unproblematisch beschrieben.",
            controlledPrompt = "Erklären Sie einer Laiin in drei Sätzen, ohne neue Fakten: begrenzte Anschlussleistung; Lastmanagement verteilt sie (verwenden Sie 'lässt sich' und 'wodurch'); Folge für die Ladedauer (verwenden Sie 'sofern').",
            controlledModel = "Der Hausanschluss kann nur eine begrenzte Leistung liefern. Mit einem Lastmanagement lässt sich die verfügbare Leistung auf die gleichzeitig ladenden Fahrzeuge verteilen, wodurch eine Überlastung des Anschlusses vermieden wird. Der einzelne Ladevorgang kann dadurch länger dauern, sofern mehrere Fahrzeuge zugleich angeschlossen sind.",
            speaking = "Ohne Vorbereitung außer 30 Sekunden Notizen: Erklären Sie in 90 Sekunden einer Nachbarin, die kein technisches Wissen hat, warum Ladepunkte in einem Mehrfamilienhaus ein Lastmanagement brauchen können, und nennen Sie, was noch geklärt werden muss. Beantworten Sie danach in 60 Sekunden die Elternfrage 'Fällt das U11-Training bei Regen aus?': Sagen Sie, was Sie wissen und was nicht, wer entscheidet und wo die Information steht; erfinden Sie keine Vereinsregel.",
            interaction = "A organisiert ein kleines Straßenfest, B soll die Getränke übernehmen und sagt: 'Des kriege mer scho hi' (regional: Das kriegen wir schon hin). A ist unsicher, ob das eine Zusage ist. Twist: B ist tatsächlich zuständig, wollte aber nur beruhigen und hat die Zusage noch nicht geplant. A fragt freundlich nach Menge, Termin und Rückmeldung, ohne zu drängen oder B bloßzustellen, und fasst die Vereinbarung zusammen. Wechseln Sie die Rollen.",
            writing = "Schreiben Sie einen Leserbrief von 220–260 Wörtern zur Frage: Sollen Mehrfamilienhäuser Ladepunkte für alle Bewohner vorbereiten? Stellen Sie Ihre Position klar dar, würdigen Sie ein Gegenargument fair, trennen Sie Gesichertes von Offenem und erfinden Sie weder Zahlen noch Rechtsauskünfte. Verwenden Sie mindestens eine Passiversatzform und eine Revisionsbedingung.",
            transfer = "Persönliche Transferübung: Eine Gemeinde erwägt, die Bibliothek sonntags zu öffnen; Besucherzahlen und Kosten fehlen. Erörtern Sie die Frage für Laien und schlagen Sie einen begrenzten Probezeitraum mit Auswertungskriterien vor. Selbstcheck: Haben Sie fehlende Informationen klar benannt und einen Vorschlag gemacht, der revidierbar bleibt? Nach mindestens sieben Tagen können Sie die Aufgabe ohne Vorlage erneut beantworten. Dies ist keine geschützte oder unabhängig bewertete Prüfungsaufgabe.",
            feedback = "Modellrückmeldung: Ihre Erklärung ist für Laien verständlich, weil Sie das Bild des Rohres nutzen und trotzdem sagen, was offen bleibt. 'Das ist ganz einfach' wertet die Frage der Nachbarin ab; besser: 'Das klingt kompliziert, ist aber im Kern eine Verteilungsfrage.' Bei der Zusage 'Des kriege mer scho hi' war Ihre Nachfrage freundlich und konkret. Prüfen Sie, ob Sie Wissen über das Thema und sprachliche Leistung getrennt betrachten: Ein sachlich richtiger Satz mit einem Kasusfehler ist ein Sprachbefund, kein Wissensmangel. Diese Einheit ist eine Übungsvariante; die Bewertung unabhängiger Leistung geschieht im Prüfungsmodus."
        )
    ),
    CurriculumUnit(
        "s6-specialist", "Unvorbereiteter fachlicher Transfer", 6, emptyList(),
        listOf(
            "Fachinformationen aus Bericht und Gespräch trennen in Befund, Vermutung und Entscheidung.",
            "In einem Fachkontext Rückfragen stellen, Maßnahmen sichern und vorläufige Bewertungen als vorläufig kennzeichnen.",
            "Fachliche Inhalte für Betroffene verständlich vermitteln und Zustimmung nicht aus Höflichkeitsfloskeln ableiten."
        ),
        contextsD(
            "Neue fachliche Informationen für Betroffene vermitteln",
            "Ein unbekanntes Ehrenamtsprojekt abstimmen und Zustimmung nicht aus Höflichkeitsfloskeln ableiten",
            "Ein unbekanntes Automotive-Review zu Hybrid-Fahrdynamik, ECU/TCU und FMEA durchführen",
            "Eine unbekannte U15-Taktikaufgabe erklären und ein anschließendes Elterngespräch führen"
        ),
        UnitContent(
            reading = """
                Übungsvariante – echte ungesehene Aufgaben stehen im Prüfungsmodus. Der folgende Auszug ist ein fiktives Beispiel für den Aufgabentyp; er enthält keine realen Messwerte und kein reales Projekt.

                Review-Unterlage (fiktiv): Lastwechselreaktion im Hybridbetrieb

                Beobachtung: Bei einem Prototyp mit Hybridantrieb wurde auf niedrig haftender Fahrbahn beim abrupten Gaswegnehmen in der Kurve eine stärkere Gierbewegung beschrieben als beim Vergleichsfahrzeug. In dieser Situation kann das rekuperative Bremsmoment der E-Maschine an der angetriebenen Achse ansteigen. Die ECU koordiniert die Momentenanforderungen von Fahrer, Fahrdynamikregelung und Getriebe; die TCU fordert beim Zurückschalten zusätzlich einen Momenteneingriff an. Die Messung stammt aus einem Testlauf mit zwei Softwareständen, und es fuhren zwei verschiedene Personen.

                Bewertung im FMEA-Entwurf: Die Fehlermöglichkeit „Rekuperationsmoment wird bei geringer Haftung nicht rechtzeitig begrenzt“ wurde vom Safety-Team mit hoher Bedeutung (S) bewertet, weil eine Instabilität des Fahrzeugs möglich ist. Auftreten (O) und Entdeckung (D) sind noch nicht begründet; für die Entdeckung liegt bisher nur ein Simulationsergebnis vor. Eine formale Aktionspriorität ist deshalb noch nicht bestimmt: Im AIAG/VDA-Verfahren richtet sie sich nach der Kombination aus S, O und D. Für die weitere Bewertung wurde lediglich eine vorläufige interne Dringlichkeit markiert; die Maßnahmen stehen aus. Eine ASIL-Einstufung nach ISO 26262 ergibt sich aus der Gefahren- und Risikoanalyse, also aus Schadensschwere (Severity), Auftretenshäufigkeit der Betriebssituation (Exposure) und Beherrschbarkeit (Controllability); sie wird in dieser Unterlage weder behauptet noch ersetzt.

                Offene Punkte: Ob die Gierbewegung vom Rekuperationsmoment, vom Schaltvorgang oder von Fahrer und Messaufbau abhängt, ist nicht entschieden. Gefordert wird ein Vergleich beider Softwarestände mit demselben Fahrer oder einem reproduzierbaren Fahrmanöver im Rahmen des genehmigten Prüfplans.

                Aufgaben (Übungsvariante): 1) Trennen Sie Befund, Vermutung und Entscheidung. 2) Formulieren Sie je eine Rückfrage an die ECU- und die TCU-Verantwortlichen. 3) Erläutern Sie einer Laiin in vier Sätzen, warum die vorläufige interne Dringlichkeit weder eine formale Aktionspriorität noch eine Freigabe oder Sperre des Fahrzeugs ist.
            """.trimIndent(),
            listening = """
                Hartmann: Wir starten mit der Lastwechselreaktion. Kurz zum Stand: Was ist gemessen, was ist vermutet? Frau Yıldız, Sie haben die Daten.
                Yıldız: Gemessen ist eine stärkere Gierrate beim Gaswegnehmen in der Kurve, auf nasser Fahrbahn, mit zwei Softwareständen. Allerdings, äh, waren es zwei verschiedene Fahrer. Das müssen wir ehrlicherweise dazusagen.
                Pavlović: Dann ist es doch gar nicht klar, ob es an der ECU liegt. In der Simulation begrenzt die Funktion das Rekuperationsmoment rechtzeitig.
                Ebner: Die Simulation ist ein Hinweis, mehr nicht. Für die Entdeckung in der FMEA bräuchte ich einen Test am Prüfstand oder im Fahrversuch, der den Fehler auch wirklich provoziert. Sonst kann ich D nicht niedrig ansetzen.
                Nowak: Und bei uns? Die TCU fordert beim Zurückschalten einen Momenteneingriff an. Möglicherweise überlagert sich das mit der Rekuperation. Das müssten wir in den Signalen prüfen. Aber ich kann nicht versprechen, dass wir das noch diese Woche schaffen.
                Hartmann: Das ist ehrlich, danke. Herr Pavlović, könnten Sie die Signalverläufe der ECU-Seite bereitstellen?
                Pavlović: Des goht scho, kriegt ihr.
                Hartmann: Bis wann genau?
                Pavlović: Äh ... Mittwoch. Mittwochmittag.
                Hartmann: Gut, Mittwoch, zwölf Uhr, an Frau Nowak und Frau Yıldız. Herr Ebner, Sie wollten noch zur Aktionspriorität etwas sagen.
                Ebner: Die formale Aktionspriorität können wir noch nicht bestimmen, weil Auftreten und Entdeckung nicht bewertet sind. Wegen der hohen Bedeutung markieren wir die weitere Bewertung intern vorläufig als dringend. Das ist kein berechneter AP-Wert und bedeutet weder automatisch eine Fahrzeugsperre noch eine Freigabe.
                Yıldız: Und die Fahrversuche?
                Ebner: Nur im genehmigten Prüfplan. Wenn das Manöver geändert wird, geht das zurück an die Prüfleitung.
                Nowak: Dann wäre mein Vorschlag: gleicher Fahrer, gleiches Manöver, beide Softwarestände, und wir schauen auch auf unseren Schalteingriff.
                Hartmann: Einverstanden. Ich fasse zusammen: Befund ist eine stärkere Gierrate, die Ursache ist nicht geklärt. Offen sind ECU, TCU und Fahrer. Nächste Schritte: Signalverläufe bis Mittwoch zwölf Uhr, Vergleichsmessung nach Prüfplan, FMEA-Einträge zu O und D nach den Ergebnissen und danach die formale AP-Bestimmung. Fehlt jemandem etwas?
                Ebner: Nur: Die interne Dringlichkeit bitte nicht als formale Aktionspriorität protokollieren.
                Hartmann: Kommt rein.

                Hinweis (fiktive Dialektannäherung, nicht regional validiert): „Des goht scho, kriegt ihr“ ist schwäbisch gefärbt (Das geht schon, ihr bekommt es). Die Zusage wird erst durch die Nachfrage nach Termin und Empfängern verbindlich. Das Gespräch ist fiktiv und eine Übungsvariante.
            """.trimIndent(),
            expressions = listOf(
                "Gemessen ist …; vermutet wird …; entschieden ist noch nichts. – Befund, Vermutung und Entscheidung trennen",
                "Das ist ein Hinweis, aber noch kein Nachweis. – Evidenzgrenzen markieren",
                "Die formale Aktionspriorität folgt aus S, O und D; eine interne Dringlichkeit ist davon zu unterscheiden. – Fachbegriff ohne Fehlschluss erklären",
                "Könnten Sie bis wann genau … bereitstellen? – eine Zusage konkretisieren",
                "Aufgrund der unterschiedlichen Fahrer lässt sich … noch nicht ableiten. – eine Einschränkung begründen",
                "Im Protokoll bitte mit dem Zusatz 'vorläufig'. – Dokumentation präzise halten",
                "Kollokationen: eine Fehlermöglichkeit bewerten, Maßnahmen festlegen, einen Prüfplan einhalten, Signalverläufe bereitstellen, eine Hypothese stützen oder verwerfen",
                "Regional (schwäbisch): 'Des goht scho' kann ein Versprechen oder eine Beruhigung sein; nach Termin und Umfang fragen, den Dialekt nicht imitieren"
            ),
            grammar = "Partizipialattribute verdichten Fachtexte: 'die vom Safety-Team als hoch bewertete Bedeutung', 'die noch nicht begründete Entdeckung'. Für Laien lösen Sie sie auf: 'die Bedeutung, die das Safety-Team als hoch bewertet hat'. Kausale Präpositionen stehen in formeller Schriftsprache typischerweise mit Genitiv: 'aufgrund der unterschiedlichen Fahrer', 'infolge des Softwarewechsels', 'wegen der fehlenden Prüfstandsdaten'; verwenden Sie den Genitiv in formellen Texten. Der Dativ kommt regional und umgangssprachlich vor und sollte nicht pauschal als ungrammatisch bezeichnet werden. 'Trotz' mit Genitiv entspricht einem obwohl-Satz: 'Trotz der hohen Bedeutung ist keine Maßnahme festgelegt.' Indirekte Fragen mit Verb am Ende: 'Ob das Rekuperationsmoment die Gierbewegung auslöst, ist offen.' Das Passiv mit Modalverb drückt Pflichten aus: 'Die Begründung muss dokumentiert werden.'",
            question = "Was lässt sich aus der fiktiven Review-Unterlage sicher ableiten?",
            options = listOf(
                "Das Rekuperationsmoment verursacht nachweislich die Instabilität auf nasser Fahrbahn.",
                "Die Bedeutung ist hoch bewertet; Auftreten und Entdeckung sind noch zu begründen, eine formale Aktionspriorität ist daher noch offen.",
                "Die Aktionspriorität ist niedrig, weil eine Simulation die Entdeckung belegt.",
                "Das Fahrzeug ist bereits nach ISO 26262 mit ASIL D eingestuft."
            ),
            correctOption = 1,
            explanation = "B entspricht dem Bewertungsstand im Text. A macht aus einer offenen Vermutung einen Kausalnachweis; die Unterlage nennt ausdrücklich weitere mögliche Einflüsse. C ist falsch: Eine Simulation ersetzt weder die ausstehende Begründung von O und D noch erlaubt sie eine formale Aktionspriorität. D erfindet eine Einstufung; der Text sagt ausdrücklich, dass er keine ASIL-Einstufung behauptet.",
            controlledPrompt = "Formulieren Sie vier Sätze ohne neue Fakten: (1) Befund mit 'Gemessen ist'; (2) Einschränkung mit 'aufgrund' + Genitiv (zwei Fahrer); (3) Partizipialattribut für die Bedeutung (vom Safety-Team als hoch bewertet); (4) indirekte Frage mit 'ob' für die offene Ursache.",
            controlledModel = "Gemessen ist eine stärkere Gierbewegung beim Gaswegnehmen in der Kurve. Aufgrund der zwei verschiedenen Fahrer lässt sich daraus noch keine Ursache ableiten. Die vom Safety-Team als hoch bewertete Bedeutung der Fehlermöglichkeit macht eine Begründung für Auftreten und Entdeckung erforderlich; eine formale Aktionspriorität bleibt bis dahin offen. Ob das Rekuperationsmoment, der Schalteingriff oder der Fahrer die Gierbewegung beeinflusst, ist ebenfalls offen.",
            speaking = "Wählen Sie einen Kontext. Automotive: Führen Sie ein 2-minütiges Review-Eröffnungsstatement ohne Vorlage zur Lastwechselreaktion: Befund, drei offene Ursachen, vorläufige FMEA-Bewertung, nächster Prüfschritt, Protokollhinweis. Jugendfußball: Erklären Sie einer U15 in 2 Minuten eine unbekannte Taktikaufgabe, zum Beispiel Spielaufbau gegen hohes Pressing mit Besetzung der Halbräume und Gegenpressing nach Ballverlust; prüfen Sie das Verständnis, indem zwei Spieler den nächsten Schritt zeigen.",
            interaction = "A ist U15-Trainer und führt ein Elterngespräch, B ist ein Vater, dessen Sohn jetzt als Außenverteidiger in der Viererkette spielt. B sagt: 'Des passt scho, i sag nix' (regional: Das passt schon, ich sage nichts), wirkt aber sichtlich unzufrieden. Twist: Erst auf A's offene Rückfrage nennt B den eigentlichen Wunsch, dass sein Sohn im Mittelfeld Spielaufbau lernen soll. A erklärt Rolle und Entwicklungsziel konkret, ohne eine Zusage zu erfinden, und vereinbart, wann und woran beide die Entwicklung prüfen. Wechseln Sie die Rollen.",
            writing = "Schreiben Sie ein Review-Protokoll von 180–220 Wörtern zur fiktiven Lastwechselreaktion: Befund, offene Ursachen, Bewertung von S sowie den noch unbegründeten Werten O und D, den ausstehenden formalen AP und Maßnahmen mit Verantwortlichen und Terminen. Kennzeichnen Sie Vorläufiges als vorläufig und erfinden Sie keine Zahlen, keine ASIL-Einstufung und keine Freigabe.",
            transfer = "Persönliche Transferübung: Ein Verein plant ein Repair-Café. Auf die Frage nach Räumen und Aufsicht sagt die Gemeindevertreterin: 'Ja, schauen wir mal.' Ein Mitglied antwortet: 'Klingt gut.' Schreiben Sie ein kurzes Gespräch, das Zusage, Prüfung und Höflichkeit unterscheidet und Verantwortung, Termin sowie offene Punkte klärt. Erfinden Sie keine Förderungen oder Regeln. Selbstcheck: Haben Sie die unverbindliche Wendung nicht als Zusage ausgelegt und eine konkrete Rückmeldung vereinbart?",
            feedback = "Modellrückmeldung: Sie trennen Befund und Vermutung und markieren Vorläufiges. 'Die ECU begrenzt das Moment nicht rechtzeitig' behauptet dagegen eine Ursache; besser: 'Es ist offen, ob die Begrenzung rechtzeitig greift.' Bei 'Des goht scho' haben Sie Termin und Empfänger erfragt, so wird aus einer Beruhigung eine überprüfbare Zusage. Im Elterngespräch war 'Dein Sohn ist halt hinten besser' eine unbelegte Zuschreibung; konkreter ist: 'In den letzten Spielen hat er als Außenverteidiger die Absicherung beim Umschaltspiel gut übernommen.' Prüfen Sie Ihre Texte immer darauf, ob Fachwissen, Sprachrichtigkeit und Wirkung getrennt bewertet werden."
        )
    ),
    CurriculumUnit(
        "s6-retention", "Langfristige Verfügbarkeit nachweisen", 6, emptyList(),
        listOf(
            "Sprachliche Mittel nach einer Pause von mindestens sieben Tagen ohne Vorlage in einer neuen Aufgabe abrufen.",
            "Beobachtung, Hypothese und Prüfschritt in einem neuen Problem sauber trennen und für Laien erklären.",
            "Modalpartikeln und Rückmeldungen in einem Konfliktgespräch situationsgerecht einsetzen, statt auswendig gelernte Schablonen zu wiederholen."
        ),
        contextsD(
            "Ein neues Problem nach einer Lernpause erklären",
            "Ein neues Konfliktgespräch führen und Modalpartikeln ohne auswendig gelernte Schablonen einsetzen",
            "Eine neue EV-Root-Cause-Hypothese nach einer Pause verständlich und vorsichtig erläutern",
            "Nach einer Pause eine neue U12-Fußballübung anleiten und einer Spielerin konkretes Feedback geben"
        ),
        UnitContent(
            reading = """
                Übungsvariante – echte ungesehene Aufgaben stehen im Prüfungsmodus. Diese Einheit sollte erst nach einer Pause von mindestens sieben Tagen und ohne Vorlage bearbeitet werden.

                Verfügbarkeit nach der Pause

                Nach vier Wochen ohne Deutschübung wirkt vieles, was gut gelernt schien, plötzlich zäh. Das ist kein Zeichen von Unfähigkeit, sondern eine gewöhnliche Erfahrung: Wiedererkennen und Abrufen sind zwei verschiedene Leistungen. Einen Satz in einem Text als richtig zu erkennen ist leichter, als ihn in einer neuen Situation selbst zu bilden. Eine Prüfung nach einer Pause zeigt deshalb nicht, was einmal verstanden wurde, sondern was ohne Vorlage verfügbar ist. Lernforschung deutet darauf hin, dass aktives Abrufen das spätere Behalten besser stützt als erneutes Lesen; als Garantie für den Einzelfall darf man das aber nicht verstehen.

                Ein Beispiel: Tarek, Entwicklungsingenieur, beobachtet bei einem EV-Prototyp nach einem Softwareupdate eine geringere Ladeleistung bei Kälte. Nach einer Lernpause soll er dem Team auf Deutsch erklären, was er weiß. Unter Druck greift er auf eine Schablone zurück: „Es könnte sein, dass die Software schuld ist.“ Der Satz klingt vorsichtig, vermischt aber Beobachtung und Hypothese und nennt keine Prüfung. Besser wäre: „Beobachtet wurde eine geringere Ladeleistung bei Kälte. Möglich ist, dass geänderte Temperaturgrenzen im Batteriemanagement eine Rolle spielen; ebenso kann die Temperaturmessung abweichen. Um das zu prüfen, vergleichen wir beide Softwarestände bei gleicher Batterietemperatur.“

                Für die Selbstkontrolle gelten drei Fragen: Habe ich es in eigenen Worten ohne Vorlage gesagt? Habe ich es in einem neuen Zusammenhang getan? Tauchen frühere Fehler wieder auf, und sind sie in meiner Fehlerliste dokumentiert? Wiederkehrende Fehler sind kein Misserfolg, sondern der nützlichste Hinweis für die nächste Übung. Ein menschliches Urteil bleibt trotzdem nötig, weil man eigene Fehler leicht überhört.
            """.trimIndent(),
            listening = """
                Selin: Bevor wir starten: Heute neu, ein Rondo vier gegen zwei, aber mit einer Zusatzregel. Wer den Ball verliert, geht in die Mitte. Marc, magst du das kurz mit zwei Spielerinnen vormachen?
                Marc: Klar. Ich passe zu dir, ihr zwei geht drauf, und wenn ihr den Ball erobert, wechseln wir. So, siehst du, Emma?
                Emma: Und wenn der Ball nur abgefälscht wird, zählt das auch schon?
                Selin: Gute Frage. Nein, nur wenn ihr ihn wirklich kontrolliert. Ich will sichergehen, dass es alle verstanden haben. Wer sagt's mir in eigenen Worten? Emma?
                Emma: Also ... vier spielen sich den Ball zu, zwei in der Mitte jagen ihn, und wer den Ball verliert, geht in die Mitte. Aber nur, wenn die in der Mitte ihn wirklich haben. Stimmt das?
                Selin: Genau so. Dann los, drei Minuten.
                Selin: Stopp, kurz zu dir, Emma. Beim zweiten Durchgang hast du vor jedem Pass geschaut, wo die beiden in der Mitte stehen. Das hat man gesehen.
                Marc: Des war scho guet. Der Blick vor dem Pass, genau. Aber ein, zwei Pässe waren, ähm, ziemlich scharf.
                Emma: Moment, war das jetzt gut oder nicht?
                Marc: Entschuldige, das war missverständlich. Gut war der Blick vor dem Pass. Ändern würde ich die Passschärfe: Spiel den Ball in den Fuß, der weiter vom Gegner weg ist.
                Emma: Ah, okay. Also auf den ballfernen Fuß.
                Keller: Entschuldigung, darf ich kurz stören? Es fängt an zu regnen. Muss das denn sein, dass die Kinder weitermachen? Das war doch nicht abgemacht, dass sie nass werden!
                Selin: Ich verstehe, dass Sie sich Sorgen machen. Es ist leichter Regen, und wir haben ja besprochen, dass wir bei Gewitter abbrechen. Wenn Emma friert, sagt sie mir das bitte, ja, Emma?
                Emma: Mach ich.
                Keller: Na gut, aber ich schau halt weiter zu.
                Selin: Gern. Und wenn Sie danach noch einmal darüber reden möchten, nehme ich mir nach dem Training zehn Minuten.

                Hinweis (fiktive Dialektannäherung, nicht regional validiert): „Des war scho guet“ ist schwäbisch gefärbt (Das war schon gut). Hier ist es konkrete Anerkennung, weil Marc die Szene benennt; ohne Begründung kann „schon gut“ auch zurückhaltend gemeint sein. Das Gespräch ist fiktiv; die Absprache zum Gewitter ist nur ein Beispiel im Text.
            """.trimIndent(),
            expressions = listOf(
                "Beobachtet wurde …; möglich ist, dass …; um das zu prüfen, … – Befund, Hypothese und Prüfschritt trennen",
                "Wer sagt mir das in eigenen Worten? – Verständnis ohne Vorlage prüfen",
                "Gut war …; ändern würde ich … – konkretes Feedback mit Szene und Vorschlag",
                "Entschuldige, das war missverständlich. – eine eigene Äußerung reparieren",
                "Ich verstehe, dass Sie sich Sorgen machen. – ein Gefühl anerkennen, ohne den Vorwurf zu übernehmen",
                "Wie kam es denn dazu? – 'denn' macht eine Frage weniger verhörartig",
                "Kollokationen: einen Fehler dokumentieren, eine Hypothese prüfen, eine Pause einlegen, Verständnis sichern, eine Regel einführen",
                "Regional (schwäbisch): 'Des war scho guet' – ohne Begründung nachfragen: 'Was war konkret gut?'; den Dialekt weder verspotten noch nachahmen"
            ),
            grammar = "Modalpartikeln gehören zur gesprochenen Sprache und steuern Beziehung und Ton: 'Das ist ja nicht schlimm' (verweist auf Gemeinsames), 'Das war doch abgemacht' (kann als Vorwurf wirken), 'Kannst du mal kurz zuhören?' (mildert eine Bitte), 'Wie kam es denn dazu?' (macht eine Frage freundlicher), 'Das ist halt so' oder 'eben so' (kann als Erklärung oder als Ausweichen verstanden werden). Ihre Wirkung hängt von Betonung und Beziehung ab; wiederholte feste Schablonen klingen auswendig gelernt. In schriftlichen Berichten sind sie meist unpassend. Hypothesen brauchen Modalität: 'Möglich ist, dass …', 'Das könnte …', 'Das wird wohl …' (Vermutung); Fakten stehen im Indikativ. Reparaturen ohne Schuldumkehr: 'Das war missverständlich', nicht 'Du hast das falsch verstanden'.",
            question = "Wie ist Marcs Äußerung „Des war scho guet“ im Gespräch zu verstehen?",
            options = listOf(
                "Als ausweichende, unverbindliche Bemerkung ohne Information.",
                "Als konkrete Anerkennung des Blicks vor dem Pass, auch wenn Emma sie zunächst unklar findet.",
                "Als versteckte Kritik an Emmas Spielweise.",
                "Als Zusage, dass Emma künftig mehr Spielzeit erhält."
            ),
            correctOption = 1,
            explanation = "B stützt sich auf die Szene: Marc nennt den Blick vor dem Pass. A trifft nicht zu, denn der Satz hat Inhalt, auch wenn er knapp ist. C verwechselt die anschließende Verbesserung der Passschärfe mit Abwertung. D erfindet eine Zusage; über Spielzeit wird im Gespräch nicht gesprochen. Wichtig: Dieselbe Wendung könnte in einer anderen Situation zurückhaltender gemeint sein, deshalb zählt der Kontext, keine Pauschalregel.",
            controlledPrompt = "Formulieren Sie ohne Vorlage vier Sätze zum Training: (1) Beobachtung der Szene; (2) Hypothese mit 'möglich ist, dass' für eine zu scharfe Passschärfe; (3) Prüfschritt mit 'um … zu'; (4) eine höfliche Reparatur mit 'missverständlich'. Verwenden Sie genau eine Modalpartikel passend zur Situation.",
            controlledModel = "Beim zweiten Durchgang hast du vor jedem Pass nach den beiden Spielerinnen in der Mitte geschaut. Möglich ist, dass einige Pässe zu scharf waren, weil der Ball zum nahen Fuß gespielt wurde. Um das zu prüfen, spielen wir zwei Minuten lang auf den ballfernen Fuß. Entschuldige, mein Satz war missverständlich; gut war der Blick, ändern würde ich nur die Passschärfe.",
            speaking = "Erklären Sie nach einer Pause von mindestens sieben Tagen ohne Vorlage in 90 Sekunden eine neue EV-Root-Cause-Hypothese: Eine geringere Ladeleistung bei Kälte nach einem Softwarewechsel (oder ein anderes neues Problem aus Ihrem Fachgebiet). Trennen Sie Beobachtung, zwei Hypothesen und einen Prüfschritt, und sagen Sie klar, was nicht bewiesen ist. Danach geben Sie in 60 Sekunden einer U12-Spielerin Feedback zu einer konkreten Szene.",
            interaction = "A und B sind Nachbarn. Ein Paket wurde bei B abgegeben, aber nie an A weitergegeben; A ist verärgert und sagt: 'Das war doch klar, dass du das sagst.' Twist: B hatte A bereits eine Nachricht geschrieben, die nie angekommen ist. A erkennt B's Perspektive an, ohne die Beschwerde fallen zu lassen, und B erklärt ohne Rechtfertigungston; beide vereinbaren einen verlässlichen Kanal. Achten Sie auf Modalpartikeln als Beziehungssignal, nicht als Schablone. Wechseln Sie die Rollen.",
            writing = "Schreiben Sie nach einer Pause ohne Vorlage 160–200 Wörter: Erklären Sie einem Kollegen ein neues Problem Ihrer Wahl (nicht aus früheren Einheiten), etwa einen wiederkehrenden Terminkonflikt im Verein oder eine unerwartete Messabweichung. Trennen Sie Beobachtung, Vermutung und nächsten Prüfschritt und fügen Sie am Ende drei Fehler aus Ihrer eigenen Fehlerliste hinzu, die Sie nun bewusst vermieden haben.",
            transfer = "Persönliche Transferübung: Bei einer Vereinssitzung ist unklar, wer den Schlüssel für das Vereinsheim übergibt. Spielen Sie beide Seiten nacheinander durch, erkennen Sie beide Wahrnehmungen an, klären Sie die Zuständigkeit und vereinbaren Sie Übergabe und Rückmeldung. Verwenden Sie Modalpartikeln nur, wenn sie passen. Selbstcheck: Ist am Ende eindeutig, wer bis wann handelt und was bei einer Verspätung gilt? Notieren Sie ein wiederkehrendes eigenes Fehlermuster, falls Sie eines bemerken; erfinden Sie kein Urteil über Ihr gesamtes Sprachniveau.",
            feedback = "Modellrückmeldung: Sie trennen Beobachtung und Hypothese und nennen einen Prüfschritt. Die Wendung 'Es könnte sein, dass …' kommt jedoch fast in jedem Satz vor und wirkt wie eine Schablone; wechseln Sie mit 'Möglich ist', 'Das spricht dafür' oder 'Offen bleibt'. Beim Feedback an die Spielerin war 'Gut war der Blick vor dem Pass' konkret und hilfreich, 'Du bist halt manchmal zu hektisch' ist dagegen eine Zuschreibung. Im Konfliktgespräch wirkte 'Das war doch abgemacht' wie ein Vorwurf; 'Wie war das denn bei dir angekommen?' lädt ein. Tragen Sie wiederkehrende Fehler in die Fehlerliste ein und prüfen Sie nach der nächsten Pause, ob sie noch auftreten."
        )
    ),
    CurriculumUnit(
        "s6-checkpoint", "Übungsprofil auswerten und weiterlernen", 6, emptyList(),
        listOf(
            "Einen fiktiven Beispielbericht über zehn Dimensionen lesen und Werte, Belegstellen und Aussagegrenzen unterscheiden.",
            "Zwei bis drei Übungsprioritäten aus konkreten Belegstellen ableiten und als überprüfbare Ziele formulieren.",
            "Einen persönlichen Übungsplan mit Aufgabe, Selbstcheck und Termin schreiben, ohne daraus ein Niveau abzuleiten."
        ),
        contextsD(
            "Ein fiktives Beispielprofil zu einem allgemeinen Gespräch auswerten",
            "Die Wirkung von Direktheit und Dialektverständnis anhand konkreter Gesprächsstellen auswerten",
            "Ein fiktives Beispielprofil zum Automotive-Safety-Review auswerten",
            "Ein fiktives Beispielprofil zur U13-Trainingsansage und zum Elterngespräch auswerten"
        ),
        UnitContent(
            reading = """
                Der folgende Beispielbericht ist vollständig fiktiv: keine echte Person, keine echten Daten, kein Zertifikat und keine Einstufung nach einem Referenzrahmen. Die Aufgabe trainiert, konkrete Belege und Aussagegrenzen zu lesen; sie bewertet nicht Ihre eigenen Sprachleistungen.

                Fremdurteil zum unabhängigen Gespräch (Automotive-Safety-Review, ca. 12 Minuten, ohne Vorlage). Beobachterin: M. Berger (fiktiv).
                Skala im Beispielbericht: 0 nicht erkennbar, 1 nur punktuell, 2 teilweise tragfähig, 3 überwiegend verlässlich, 4 durchgehend sicher. Jeder Wert beschreibt diese Aufgabe, nicht die ganze Person.

                Genauigkeit 3: Kasus und Wortstellung meist korrekt; wiederkehrend 'aufgrund' mit Dativ statt Genitiv.
                Flüssigkeit 3: Tempo gleichmäßig, kurze Suchpausen bei Fachbegriffen.
                Spektrum 2: Häufige Wiederholung von „wichtig“ und „machen“ statt präziser Verben und Adjektive.
                Natürlichkeit 2: Schriftsprachliche Nebensatzketten im Gespräch; Modalpartikeln und knappe Reaktionen fehlen.
                Kohärenz 3: Gedankengang nachvollziehbar, Verknüpfungen teils mechanisch.
                Interaktion 3: Reagiert auf Einwürfe, lässt andere meist ausreden.
                Pragmatik 2: Bei „Des passt scho“ (Minute 7) keine Rückversicherung, ob eine Zusage gemeint war; „Das stimmt nicht“ (Minute 9) ohne Abschwächung wirkte schroff.
                Register 3: Angemessen formell, einmal zu umgangssprachlich gegenüber der Leitung.
                Verstehen 4: Auch indirekte Hinweise und Dialektfärbung inhaltlich erfasst.
                Mediation 2: In der Zusammenfassung für die Laiin fiel die Einschränkung „noch nicht nachgewiesen“ weg.

                Hinweis der Beobachterin: Dies ist eine Stichprobe. Aus einem Wert lässt sich nur ableiten, was in dieser Aufgabe sichtbar war. Priorisieren Sie wenige Dimensionen mit konkreten Belegstellen und planen Sie passende Übungen, statt daraus ein Gesamturteil abzuleiten.

                Aufgaben (Übungsvariante): 1) Ordnen Sie jeder Dimension Wert und Belegstelle zu. 2) Wählen Sie zwei Prioritäten und begründen Sie die Auswahl. 3) Formulieren Sie für eine Priorität Ziel, Übung, Nachweis und Termin.
            """.trimIndent(),
            listening = """
                Kerem: Ich bin ehrlich, ich bin ein bisschen enttäuscht. Ich verstehe doch alles, und trotzdem nur eine Zwei bei Pragmatik?
                Berger: Bei Verstehen steht ja eine Vier. Das heißt: Sie haben das Gespräch zuverlässig verstanden. Pragmatik betrifft etwas anderes, nämlich die Wirkung Ihrer Äußerungen. Darf ich die Stelle in Minute neun zeigen?
                Kerem: Bitte.
                Berger: Als Herr Schulz seinen Vorschlag gemacht hat, haben Sie gesagt: „Das stimmt nicht.“ Inhaltlich war Ihr Einwand berechtigt. Aber im Raum wurde es kurz still, und Herr Schulz hat danach nichts mehr beigetragen.
                Kerem: Ich wollte klar sein. In der Safety muss man doch direkt sein!
                Weiß: Direkt in der Sache, ja. Aber dieselbe Klarheit geht auch so: „Dem Argument folge ich noch nicht, weil der Nachweis fehlt.“ Das ist in der Sache nicht weicher, nur im Ton.
                Kerem: Okay, das sehe ich ein. Und die Sache mit „passt scho“?
                Berger: Minute sieben. Frau Haag sagte „Des passt scho“, und Sie haben geantwortet: „Danke, dann gilt der Termin.“ Aus dem Gespräch geht nicht hervor, ob sie einen Termin zugesagt hat.
                Kerem: Ich dachte, das heißt: ja.
                Berger: Es kann das heißen. Es kann auch heißen: „Ich mache mir keine Sorgen.“ Ein Satz wie „Heißt das, die Messdaten kommen bis Freitag?“ hätte es geklärt.
                Weiß: Wichtig ist, dass wir jetzt nicht zehn Dinge gleichzeitig üben. Welche zwei würden Sie priorisieren?
                Kerem: Mediation und Pragmatik, glaube ich, weil da die konkreten Stellen sind.
                Weiß: Gute Wahl. Und woran erkennen wir in vier Wochen, dass es besser geworden ist?
                Kerem: Bei der nächsten Zusammenfassung nenne ich die Einschränkung ausdrücklich und vergleiche meinen Text anschließend mit dem Ausgangstext.
                Berger: Das ist überprüfbar. Ich würde als Ziel nicht einfach „Pragmatik drei“ formulieren, sondern beschreibbare Stellen: etwa „bei unklaren Zusagen frage ich nach“.
                Weiß: Dann machen wir daraus einen Plan mit Übung und Termin. Nach der Übungsphase vergleichen Sie eine neue Antwort mit denselben Kriterien.
                Kerem: Das nehme ich mit. Eigentlich ist das beruhigender als gedacht, weil ich weiß, woran ich arbeiten kann.

                Hinweis (fiktive Dialektannäherung, nicht regional validiert): „Des passt scho“ ist schwäbisch gefärbt (Das passt schon) und kann beruhigen, zusagen oder abwinken. Das Gespräch und alle Personen sind fiktiv; es handelt sich um eine Übungsvariante.
            """.trimIndent(),
            expressions = listOf(
                "Der Wert beschreibt diese Aufgabe, nicht die ganze Person. – Aussagegrenzen eines Fremdurteils benennen",
                "An der Stelle in Minute … zeigt sich, dass … – eine Einschätzung an einer Belegstelle festmachen",
                "Das ist überprüfbar, weil … – ein Ziel durch beobachtbare Handlungen definieren",
                "Ich priorisiere …, weil dort konkrete Belegstellen vorliegen. – eine Auswahl begründen",
                "Als Selbstcheck dient …; nach … vergleiche ich eine neue Antwort. – Übung und Termin festlegen",
                "Direkt in der Sache, wertschätzend im Ton. – Klarheit und Beziehungsgestaltung verbinden",
                "Kollokationen: ein Beispielprofil auswerten, Prioritäten setzen, einen Übungsplan aufstellen, eine Belegstelle benennen, einen Fortschritt dokumentieren",
                "Regional (schwäbisch): 'Des passt scho' kann Beruhigung, Zusage oder Abwinken bedeuten; im Zweifel nachfragen: 'Heißt das, dass …?'"
            ),
            grammar = "Maßnahmenpläne nutzen instrumentale und finale Strukturen: 'Ich verbessere die Mediation, indem ich die Einschränkung nenne' (indem-Satz mit Verb am Ende; das Subjekt kann gleich oder verschieden sein), 'Um unklare Zusagen zu klären, frage ich nach' (um … zu bei gleichem Subjekt). Vergleiche und Abstufungen: 'Verstehen liegt zwei Punkte über Pragmatik', 'Die Werte unterscheiden sich deutlich'. 'Zwar … , aber …' hilft, Stärken und Lücken zusammen zu nennen. Berichte beschreiben Beobachtungen im Präteritum oder Perfekt und Bedeutungen im Präsens: 'Als er den Vorschlag machte, entstand eine Pause; das wirkt schroff.' Der Konjunktiv II mildert Rückmeldungen: 'Ein Satz wie … hätte es geklärt.' Wörter wie 'immer' oder 'nie' verlangen Belege.",
            question = "Welche Schlussfolgerung ist durch den fiktiven Bericht begründet?",
            options = listOf(
                "Weil Verstehen den Wert 4 hat, ist auch die Mediation gesichert.",
                "Mediation und Pragmatik zeigen konkrete Lücken, nämlich eine ausgelassene Einschränkung und eine nicht rückversicherte Zusage, und eignen sich als erste Ziele.",
                "Alle zehn Dimensionen sollen gleich intensiv geübt werden, damit kein Wert zurückbleibt.",
                "Der Bericht bescheinigt dem Lernenden ein bestimmtes Referenzniveau."
            ),
            correctOption = 1,
            explanation = "B stützt sich auf die genannten Belegstellen und die Empfehlung, zu priorisieren. A verwechselt Verstehen und Mediation; der Bericht trennt sie. C ignoriert den ausdrücklichen Hinweis, wenige Dimensionen mit Belegen zu wählen. D behauptet etwas, das der Bericht ausschließt: Er vergibt kein Zertifikat und keine Einstufung.",
            controlledPrompt = "Formulieren Sie für die Dimension Mediation einen Maßnahmenplan in fünf Teilen: Befund mit Belegstelle, Ziel als beobachtbare Handlung (verwenden Sie 'indem'), Übung, Nachweis, Termin. Erfinden Sie keine Messwerte.",
            controlledModel = "Belegstelle im Beispiel: In der Zusammenfassung für die Laiin fehlte die Einschränkung 'noch nicht nachgewiesen'. Übungsziel: Ich nenne in jeder Zusammenfassung ausdrücklich, was noch offen ist. Übung: Dreimal pro Woche fasse ich einen kurzen Fachtext für Laien in drei Sätzen zusammen und markiere die Einschränkung. Selbstcheck: Ich vergleiche die Zusammenfassung mit dem Ausgangstext und prüfe, ob die Einschränkung enthalten ist. Termin: Nach vier Wochen wiederhole ich die Aufgabe mit einem neuen Text.",
            speaking = "Sprechen Sie zwei Minuten ohne Vorlage zum fiktiven Bericht: Nennen Sie, was er belegt und was nicht, wählen Sie zwei Übungsprioritäten mit Belegstellen und beschreiben Sie, woran Sie in vier Wochen bei einer neuen Zusammenfassung selbst prüfen, ob eine Einschränkung erhalten blieb. Verwenden Sie 'indem' mindestens einmal und vermeiden Sie Selbstabwertung wie 'Ich bin einfach schlecht in Pragmatik'.",
            interaction = "A ist Beobachterin und erläutert den fiktiven Bericht, B ist der Lernende und widerspricht der Zwei in Pragmatik mit 'Meine Kunden verstehen mich doch.' Twist: B hat mit seinem Einwand teilweise recht, denn die Beobachtung betrifft nur eine Stichprobe, und beide müssen klären, was daraus folgt. A bleibt bei den Belegstellen, erkennt die Grenzen des Urteils an und B formuliert aus Minute 7 und Minute 9 ein überprüfbares Ziel. Wechseln Sie die Rollen.",
            writing = "Schreiben Sie einen Maßnahmenplan von 200–240 Wörtern zum fiktiven Bericht. Wählen Sie zwei Prioritäten, nennen Sie je Belegstelle, Ziel als beobachtbare Handlung, Übung, Nachweis und Termin, und begründen Sie, warum Sie die übrigen Dimensionen vorerst nicht vertiefen. Trennen Sie Beobachtung des Berichts, Ihre Deutung und Ihre Vorhaben und verwenden Sie 'indem' und 'um … zu'.",
            transfer = "Persönliche Transferübung, kein geschützter Test: Ein fiktives Beispielprofil zur U13-Trainingsansage nennt Verstehen 4, Genauigkeit 3, Interaktion 3, Register 2 (Wechsel zwischen Du und Sie) und Pragmatik 2 (eine unklare Zusage wurde nicht rückversichert). Werten Sie nur diese Angaben aus, wählen Sie zwei Übungsprioritäten und schreiben Sie einen Plan mit Aufgabe, Selbstcheck und Termin. Erfinden Sie keine weiteren Befunde und leiten Sie kein Niveau ab. Nach mindestens sieben Tagen führen Sie eine neue Ansage durch und vergleichen sie mit Ihrem eigenen Kriterienraster; das Ergebnis bleibt eine Lernnotiz, keine unabhängige Bewertung.",
            feedback = "Modellrückmeldung: Sie unterscheiden die Beispielwerte von den Belegstellen und setzen nur zwei Prioritäten, damit der Plan umsetzbar bleibt. 'Ich muss alles verbessern' ist kein Ziel; 'Bei einer unklaren Zusage frage ich nach Termin und Verantwortlichem' ist beobachtbar. Prüfen Sie Ihre nächste Antwort anhand dieses Verhaltensrasters. Ein Selbstcheck und ein einzelnes Quiz belegen keine allgemeine Sprachkompetenz oder ein CEFR-Niveau."
        )
    )
)
