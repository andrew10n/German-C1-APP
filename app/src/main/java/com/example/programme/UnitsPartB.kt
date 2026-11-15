package com.example.programme

private fun contextsB(general: String, social: String, engineering: String, coaching: String) =
    mapOf(
        ContextTrack.GENERAL to general,
        ContextTrack.SOCIAL to social,
        ContextTrack.ENGINEERING to engineering,
        ContextTrack.COACHING to coaching
    )

/**
 * Fully authored units, part B. All teams, names, signals and values are fictional
 * teaching material and must not be read as real data or real specifications.
 */
internal val unitsPartB: List<CurriculumUnit> = listOf(
    // ------------------------------------------------------------------ s4-argument
    CurriculumUnit(
        "s4-argument", "Komplexe Argumentation strukturieren", 3, emptyList(),
        listOf(
            "These, Beleg, Gegenargument und Einschränkung in einem längeren Beitrag kohärent verknüpfen.",
            "Beobachtung von Beleg unterscheiden und die Geltung einer Aussage ausdrücklich begrenzen.",
            "Einen Einwand fair darstellen und durch Bedingung oder Gegenbeleg einordnen, ohne die These aufzugeben."
        ),
        contextsB(
            "Eine Stellungnahme verfassen",
            "Einen Förderantrag sachlich begründen statt 'Das braucht man eben' zu behaupten",
            "Eine EV-Rekuperationsstrategie mit Fahrdynamikdaten und Safety-Grenzen begründen",
            "Ein U13-Trainingskonzept mit Kleinfeldspielen und gestuften Aufgaben schriftlich begründen"
        ),
        UnitContent(
            reading = """
                Stellungnahme zum U13-Training (fiktives Beispiel, SV Lindenau)

                Die U13 des SV Lindenau sollte im Training überwiegend in Kleinfeldspielen statt in langen Übungsreihen arbeiten. Diese These stützt sich auf drei Überlegungen.

                Erstens erhöhen kleine Spielformen die Zahl der Ballkontakte und Entscheidungssituationen je Kind, weil weniger Personen um denselben Ball konkurrieren. In den sechs Einheiten, die das Trainerteam im Herbst protokolliert hat (fiktive Beobachtung, keine Messreihe), standen Kinder in der Linienübung zeitweise in der Warteschlange, während im 4-gegen-4 alle ständig beteiligt waren. Zweitens verlangt das Kleinfeld Wahrnehmung: Wer sich nicht freiläuft oder die Position des Gegners nicht liest, verliert den Ball, und die Folge erlebt das Kind unmittelbar. Drittens lässt sich der Schwierigkeitsgrad durch Zusatzregeln stufenweise steigern, etwa durch ein Zwei-Kontakt-Limit oder eine Überzahl beim Spielaufbau gegen den ersten Pressingdruck.

                Dem lässt sich entgegenhalten, dass Kleinfeldspiele ohne Anleitung zu Zufallsfußball verkommen können; technisch schwächere Kinder erhielten dann kaum Hilfe. Dieser Einwand ist ernst zu nehmen, trifft aber nur zu, wenn das Spiel unstrukturiert bleibt. Gerade deshalb sollte jede Einheit mit einem klaren Schwerpunkt beginnen und gezielte Unterbrechungen enthalten. Ein Rondo oder eine kurze Technikreihe bleibt als Einstieg sinnvoll; sie darf jedoch nicht den Hauptteil dominieren.

                Offen ist, ob sich der Vorteil bei sehr großen Gruppen halten lässt. Dafür wären mehr Felder und mehr Betreuungspersonen nötig. Insgesamt spricht daher mehr für das Kleinfeld als dagegen, sofern Struktur und Betreuung stimmen. Die Entscheidung sollte nach einer Saisonhälfte anhand der Beobachtungsbögen überprüft werden.
            """.trimIndent(),
            listening = """
                Sabine: Gut, dann zum Förderantrag. Marc, du wolltest begründen, warum wir vier mobile Kleinfeldtore brauchen.
                Marc: Ja, also, meine These ist: Ohne die Tore steht unsere U13 weiter in Warteschlangen. Erstens ...
                Hasan: Moment, darf ich kurz dazwischen? Vier Tore für Warteschlangen, das klingt eher nach Wunsch als nach Begründung.
                Marc: Fair. Anders gesagt: Wir haben meistens sechzehn Kinder. Mit vier mobilen Toren bauen wir zwei Felder für 4-gegen-4 auf, und alle sechzehn sind gleichzeitig beschäftigt.
                Hasan: Rechnerisch stimmt das. Aber: Obwohl die Idee überzeugt, könnten wir das auch mit Hütchentoren machen. Die kosten fast nichts.
                Marc: Stimmt, das geht, zwar nur eingeschränkt. Ohne feste Torhöhe diskutieren die Kinder ständig, ob der Ball drin war, und eine Torwartrolle gibt es auch nicht.
                Sabine: Ist das gemessen oder beobachtet?
                Marc: Beobachtet, ehrlich gesagt. Das ist kein Beleg im strengen Sinn, eher ein Erfahrungswert aus dem Training.
                Sabine: Dann sollten wir das im Antrag auch so schreiben.
                Marc: Genau, deshalb mein Vorschlag: Wir leihen uns vier Wochen lang Tore, zählen in zwei Einheiten stichprobenartig die Ballkontakte und fragen die Kinder. Dann hat der Antrag etwas in der Hand.
                Hasan: Das würde mich überzeugen, sofern im Antrag steht, woran wir Erfolg messen. Ein Bauchgefühl reicht dem Fördergeber nicht.
                Marc: Äh, ja, Ballkontakte pro Kind und, na ja, die Rückmeldung der Kinder und Eltern. Mehr wollte ich nicht versprechen.
                Sabine: Das ist ehrlich, und das gefällt mir. Ich fasse zusammen: These, mobile Tore; Beleg, beobachtete Wartezeiten; Gegenargument, Hütchentore; Antwort, deren Grenzen; Bedingung, vierwöchiger Test.
                Marc: Und die Lagerung der Tore, die fehlt noch.
                Hasan: Stimmt, die hätte ich fast vergessen. Das nehme ich auf.
            """.trimIndent(),
            expressions = listOf(
                "Meine These lautet, dass … / Ich vertrete die Position, dass … – die Hauptaussage ausdrücklich markieren",
                "Dafür spricht erstens …, zweitens …; dagegen lässt sich einwenden, dass … – Argumente ordnen und gewichten",
                "Dem lässt sich entgegenhalten, dass … / Dem steht gegenüber, dass … – einen Einwand einführen",
                "Dieser Einwand ist ernst zu nehmen, trifft aber nur zu, wenn … – ein Gegenargument begrenzen, statt es wegzuwischen",
                "einen Einwand entkräften / ein Argument stützen / eine Behauptung belegen – feste Verbindungen der Argumentation",
                "Das ist eine Beobachtung, kein Beleg im strengen Sinn. – Evidenzgrenzen offenlegen",
                "Insgesamt spricht mehr für … als dagegen, sofern … – ein Fazit mit Bedingung formulieren",
                "etwas in Betracht ziehen / einen Gesichtspunkt gewichten / zu bedenken geben – Abwägung sprachlich sichtbar machen"
            ),
            grammar = "Konzessivsätze steuern die Gewichtung: 'Obwohl Kleinfeldspiele ohne Struktur zu Zufallsfußball führen können, überwiegen die Vorteile.' Im obwohl-Satz steht das Verb am Ende; er nennt das Zugestandene, der Hauptsatz die eigene Position. Bei 'zwar … aber/allerdings' bleiben beide Teile Hauptsätze: 'Zwar führt das Kleinfeld ohne Anleitung zu Zufallsfußball, allerdings lässt sich das durch Struktur vermeiden.' 'Sofern' und 'es sei denn, …' begrenzen eine Behauptung: 'Der Vorteil gilt, sofern die Einheit einen klaren Schwerpunkt hat.' Nominalstil verdichtet Begründungen ('die Zunahme der Ballkontakte' statt 'dass die Kinder mehr Ballkontakte haben'); er passt in schriftliche Stellungnahmen, macht aber handelnde Personen unsichtbar. Der Konjunktiv II markiert den Einwand als Möglichkeit: 'Technisch schwächere Kinder erhielten kaum Hilfe.'",
            question = "Welche Funktion hat der dritte Absatz (ab 'Dem lässt sich entgegenhalten …') der Stellungnahme?",
            options = listOf(
                "Er nimmt die These zurück, weil Kleinfeldspiele zu Zufallsfußball führen.",
                "Er nennt einen ernsthaften Einwand und begrenzt ihn durch eine Bedingung, ohne die These aufzugeben.",
                "Er liefert einen vierten Beleg dafür, dass Übungsreihen überflüssig sind.",
                "Er fordert, jede Technikübung vollständig aus dem Training zu streichen."
            ),
            correctOption = 1,
            explanation = "B beschreibt die Struktur: Einwand ernst nehmen, Geltungsbedingung nennen (nur bei unstrukturiertem Spiel), These behalten. A verwechselt Zugeständnis mit Rücknahme. C ist falsch, weil der Absatz keinen Beleg, sondern ein Gegenargument enthält. D widerspricht dem Text, der Rondo und kurze Technikreihen ausdrücklich als sinnvollen Einstieg bezeichnet.",
            controlledPrompt = "Formulieren Sie vier Sätze ohne neue Fakten: (1) These: Die U13 soll überwiegend auf dem Kleinfeld spielen. (2) Beleg mit 'weil': mehr Ballkontakte je Kind. (3) Einwand mit 'zwar … allerdings': Zufallsfußball ohne Anleitung. (4) Begrenzung mit 'sofern': klarer Schwerpunkt je Einheit.",
            controlledModel = "Die U13 sollte überwiegend auf dem Kleinfeld spielen, weil die Kinder dort mehr Ballkontakte und Entscheidungssituationen haben. Zwar kann ein Kleinfeldspiel ohne Anleitung zu Zufallsfußball werden, allerdings lässt sich dieses Risiko durch einen klaren Schwerpunkt begrenzen. Der Vorteil gilt daher, sofern jede Einheit ein erkennbares Ziel hat, etwa den Spielaufbau gegen den ersten Pressingdruck.",
            speaking = "Begründen Sie in zwei Minuten ohne Vorlage vor dem Trainerteam, warum die U13 überwiegend Kleinfeldspiele trainieren sollte. Nennen Sie These, zwei Belege, einen ernsthaften Einwand (zum Beispiel schwächere Kinder oder Zufallsfußball) und eine Geltungsbedingung. Erwähnen Sie ein Rondo als Einstieg und das Spiel gegen Pressing als möglichen Schwerpunkt. Kennzeichnen Sie Beobachtungen ausdrücklich als Beobachtungen.",
            interaction = "A ist Trainerin und vertritt das Kleinfeldkonzept; B ist ein Vater, der Linienübungen und Laufrunden aus der eigenen Jugend verteidigt ('Das hat uns auch nicht geschadet'). Twist: B unterbricht mehrfach und akzeptiert nur Aussagen, die überprüfbar sind. A bittet höflich darum, den Gedanken zu Ende führen zu dürfen, trennt Beobachtung von Beleg und bietet einen vierwöchigen Test mit Erfolgskriterium an. Tauschen Sie die Rollen.",
            writing = "Schreiben Sie eine Stellungnahme von 220–260 Wörtern für den Vorstand: Sollte die U13 überwiegend Kleinfeldspiele statt Übungsreihen trainieren? Verwenden Sie These, mindestens zwei Belege (nur Beobachtungen oder klar als fiktiv gekennzeichnete Angaben), einen ernsthaften Einwand mit Einschränkung, ein Fazit und eine Überprüfungsbedingung. Bauen Sie mindestens einen Konzessivsatz und einen Satz im Nominalstil ein.",
            transfer = "Persönliche Transferübung (nicht geschützter Test): Nehmen Sie ein anderes, selbst gewähltes fiktives EV-Beispiel, etwa höhere Rekuperation bei noch offenen Fragen zu Fahrstabilität oder Komfort. Verfassen Sie in 250–300 Wörtern oder drei Minuten eine Argumentation mit These, Belegen, Gegenargument und Geltungsgrenze. Erfinden Sie keine Sicherheitsgrenzen und behaupten Sie keine Serienfreigabe. Selbstcheck: Ist jede Schlussfolgerung durch die genannten Angaben gedeckt? Was würde Ihre Position ändern?",
            feedback = "Modellrückmeldung: Ihre These steht klar am Anfang, und die Belege sind getrennt nummeriert. Der Einwand 'Zufallsfußball' wird ernst genommen, was die Argumentation glaubwürdig macht. Verbesserungsfähig: 'Es ist bewiesen, dass Kleinfeld besser ist' geht über die Beobachtung hinaus; präziser wäre 'In unseren sechs protokollierten Einheiten waren die Kinder im Kleinfeld häufiger am Ball.' Prüfen Sie außerdem, ob Ihr Fazit die Bedingung (Struktur, Betreuung) wirklich wieder aufgreift. Im Automotive-Transfer gilt dieselbe Regel: Datenlage und Aussageumfang eng verknüpfen."
        )
    ),

    // ------------------------------------------------------------------ s4-present
    CurriculumUnit(
        "s4-present", "Adressatengerecht präsentieren", 3, emptyList(),
        listOf(
            "Ein komplexes Thema für fachfremde Zuhörende in wenigen Kernaussagen gliedern.",
            "Vereinfachungen, Bilder und Analogien einsetzen und ihre Grenzen ausdrücklich benennen.",
            "Verständnis während des Vortrags sichern und entscheidende Einschränkungen auch unter Zeitdruck nicht streichen."
        ),
        contextsB(
            "Einen Fachbeitrag für Laien halten",
            "Ein Vorhaben verständlich vorstellen; regionale Wörter bei Bedarf in Standardsprache erklären",
            "Ein EV-Sicherheitskonzept mit FMEA-Bezug für fachfremde Beteiligte präsentieren",
            "U12-Spielern und Eltern das Trainingsziel Freilaufen mit konkreten Spielszenen erläutern"
        ),
        UnitContent(
            reading = """
                Präsentationsmanuskript (fiktives Projekt Aurora, Information für einen Beirat ohne Technikhintergrund)

                Guten Morgen. In den nächsten zehn Minuten möchte ich drei Fragen beantworten: Was soll das Sicherheitskonzept leisten? Woran erkennen wir, dass es das tut? Und was wissen wir heute noch nicht?

                Zur ersten Frage: Ein Elektroauto setzt die Stellung des Fahrpedals in ein Antriebsmoment um. Die entscheidende Gefährdung besteht darin, dass das Fahrzeug stärker beschleunigt, als die Fahrerin es wünscht. Die Norm ISO 26262 zur funktionalen Sicherheit verlangt, solche Gefährdungen zu bewerten und in Stufen von ASIL A bis ASIL D einzuordnen; D ist die höchste Stufe. Unser Konzept sieht deshalb eine zweite, unabhängige Überwachung vor. Sie vergleicht das angeforderte mit dem tatsächlich abgegebenen Moment und leitet bei einer unzulässigen Abweichung einen definierten sicheren Zustand ein.

                Dazu ein Bild: Stellen Sie sich einen Rechnungsprüfer vor, der jede Überweisung gegenrechnet. Das Bild hilft, hat aber eine Grenze: Der Prüfer arbeitet nachträglich, unsere Überwachung muss dagegen sehr schnell reagieren, je nach Konzept im Millisekundenbereich.

                Zur zweiten Frage: Die FMEA, also die systematische Analyse möglicher Fehlerarten und ihrer Folgen, hat für die Überwachungsfunktion die kritischen Fehlermöglichkeiten benannt. Jede ist einer Gegenmaßnahme und einem Test zugeordnet. Für Ihre Entscheidung genügt eine Aussage: Die Zuordnung ist vollständig dokumentiert; der Wirksamkeitsnachweis ist bislang für zwei von fünf Fehlermöglichkeiten erbracht (fiktive Angabe).

                Zur dritten Frage: Offen sind noch die Fahrzeugtests bei niedriger Reibung und bei extremen Temperaturen. Bis dahin gilt: Das Konzept ist vollständig beschrieben, aber noch nicht vollständig nachgewiesen. Daraus lässt sich keine Serienfreigabe ableiten.

                Ich fasse zusammen: Ziel, Nachweisstand, offene Punkte. Gern beantworte ich jetzt Ihre Fragen.
            """.trimIndent(),
            listening = """
                Tobias: Also, ich fange so an: 'Guten Morgen, heute geht es um ...'
                Ines: Stopp, kurz. Du sagst gleich im ersten Satz 'Safety-Mechanismus mit ASIL-D-Anforderung'. Das verstehen drei von fünf Leuten im Beirat nicht.
                Tobias: Okay, ähm, dann so: 'Wir wollen verhindern, dass das Auto stärker beschleunigt, als die Fahrerin möchte.' Und ASIL erkläre ich später.
                Ines: Besser. Den Rechnungsprüfer-Vergleich finde ich übrigens gut, aber lass die Grenze nicht weg.
                Tobias: Welche Grenze meinst du?
                Ines: Dass der Prüfer nachträglich rechnet und eure Überwachung in Echtzeit läuft. Sonst glauben die Leute, es gäbe eine Kontrolle im Nachhinein.
                Dr. Brandt: Darf ich mich einklinken? Ich komme gerade vom Vorstandstermin. Tobias, wie viele Folien sind das?
                Tobias: Zwölf. Moment, nein, vierzehn, zwei habe ich gestern eingefügt.
                Dr. Brandt: Zu viele. Der Beirat will drei Dinge: Was soll es leisten, was ist nachgewiesen, was ist offen. Mehr nicht.
                Tobias: Und die FMEA? Da stecken ja Wochen Arbeit drin.
                Dr. Brandt: Eine Folie. Aussage: Jede Fehlermöglichkeit hat Gegenmaßnahme und Test, davon sind bisher zwei von fünf nachgewiesen. Die Details halten wir für Rückfragen bereit.
                Ines: Und sag das mit dem Nachweisstand ruhig deutlich. Wenn jemand fragt 'Ist es sicher?', antwortest du nicht mit Ja.
                Tobias: Sondern? Ich will ja nicht ausweichend wirken.
                Ines: Du sagst: 'Das Konzept ist beschrieben, der Nachweis ist teilweise erbracht, zwei Tests stehen noch aus.' Das ist klar und ehrlich.
                Dr. Brandt: Genau. Und wenn die Zeit knapp wird, streichst du Details, aber nie die Folie mit den offenen Punkten.
                Tobias: Verstanden. Dann übe ich es nochmal mit Stoppuhr, und ihr unterbrecht mich wieder.
            """.trimIndent(),
            expressions = listOf(
                "In den nächsten zehn Minuten beantworte ich drei Fragen: … – Orientierung geben",
                "Zur ersten Frage: … / Damit komme ich zur zweiten Frage: … – Gliederungssignale setzen",
                "Stellen Sie sich vor, … – ein anschauliches Bild einführen",
                "Das Bild hilft, hat aber eine Grenze: … – die Reichweite einer Analogie begrenzen",
                "Für Ihre Entscheidung genügt eine Aussage: … – auf Kernaussagen verdichten",
                "Daraus lässt sich keine … ableiten. – eine Schlussfolgerung ausdrücklich ausschließen",
                "Ich fasse zusammen: … / Haben Sie bis hierhin Fragen? – Zusammenfassung und Verständnissicherung",
                "Komplexität reduzieren / Einschränkungen nennen / den Nachweisstand offenlegen – feste Verbindungen für Fachvorträge"
            ),
            grammar = "Partizipialattribute verdichten Informationen, solange Zuhörende sie schriftlich vor sich haben: 'die von der Überwachung verglichenen Momente'. Im gesprochenen Vortrag sind kurze Relativsätze besser hörbar: 'die Momente, die die Überwachung vergleicht'. Das Passiv mit Modalverb markiert Grenzen von Aussagen: 'Daraus lässt sich keine Freigabe ableiten', 'Der Nachweis muss noch erbracht werden.' Der Konjunktiv II führt Bilder ein, ohne sie zu behaupten: 'Stellen Sie sich vor, ein Prüfer würde jede Überweisung gegenrechnen.' Gliederungssignale stehen im Vorfeld des Satzes und bewirken Inversion: 'Zur zweiten Frage komme ich jetzt.' Achten Sie auf konsequente Zahlenangaben und auf die Kennzeichnung fiktiver Beispiele.",
            question = "Warum nennt der Vortragende ausdrücklich die Grenze des Vergleichs mit dem Rechnungsprüfer?",
            options = listOf(
                "Damit das Publikum glaubt, die Überwachung arbeite nur nachträglich.",
                "Damit die Zuhörenden das Bild nicht auf die schnelle Reaktion der Überwachung übertragen.",
                "Weil das Bild falsch ist und deshalb nicht verwendet werden sollte.",
                "Damit der Vortrag länger wird und mehr Details enthält."
            ),
            correctOption = 1,
            explanation = "B ist richtig: Die Analogie erklärt das Gegenrechnen, aber nicht das Tempo; die Grenze verhindert einen Fehlschluss. A kehrt die Absicht um, denn gerade das nachträgliche Rechnen soll nicht übertragen werden. C ist falsch, weil das Bild ausdrücklich als hilfreich bezeichnet wird. D widerspricht dem Ziel, Komplexität zu reduzieren.",
            controlledPrompt = "Formulieren Sie für fachfremde Zuhörende drei Sätze: (1) Orientierung mit 'drei Fragen'; (2) ein Bild mit 'Stellen Sie sich vor, …'; (3) die Grenze des Bildes mit 'Das Bild hilft, hat aber eine Grenze:'.",
            controlledModel = "In den nächsten zehn Minuten beantworte ich drei Fragen: Was soll das Konzept leisten, was ist nachgewiesen, was ist offen? Stellen Sie sich vor, ein Prüfer würde jede Überweisung gegenrechnen. Das Bild hilft, hat aber eine Grenze: Der Prüfer arbeitet nachträglich, unsere Überwachung muss dagegen sehr schnell reagieren.",
            speaking = "Erläutern Sie in zwei Minuten ohne Vorlage U12-Spielern und deren Eltern gemeinsam das Trainingsziel Freilaufen. Nutzen Sie zwei konkrete Szenen: (1) Der Innenverteidiger hat den Ball, der defensive Mittelfeldspieler bietet sich im Halbraum an, nachdem er einen Schulterblick gemacht hat; (2) der Gegner presst und stellt den Passweg zu, also muss der Laufweg geändert werden. Erklären Sie Freilaufen kindgerecht, nennen Sie für die Eltern eine Grenze ('Freilaufen garantiert keinen Pass') und prüfen Sie das Verständnis mit einer Frage.",
            interaction = "A präsentiert dem Beirat das EV-Sicherheitskonzept; B ist ein Beiratsmitglied ohne Technikkenntnisse. Twist: B verlangt nach wenigen Sätzen 'Ja oder nein, ist es sicher?' und unterbricht, wenn A Fachwörter verwendet. A vereinfacht, ohne zu übertreiben, benennt den Nachweisstand und die offenen Tests und kündigt den nächsten Berichtstermin an. Erfinden Sie keine Freigabe. Tauschen Sie die Rollen.",
            writing = "Schreiben Sie ein Präsentationsmanuskript von 200–240 Wörtern (etwa 90 Sekunden Sprechzeit) für einen Elternabend der U12 zum Thema Freilaufen. Gliedern Sie in drei Fragen (Was ist Freilaufen? Woran erkennt man es im Spiel? Was kann es nicht leisten?), verwenden Sie ein Bild mit Grenze, beschreiben Sie zwei Spielszenen und schließen Sie mit einer Zusammenfassung und einer Verständnisfrage.",
            transfer = "Persönliche Transferübung (nicht geschützter Test): Erfinden Sie zwei klar als fiktiv markierte U15-Szenen nach Ballverlust: In einer ist Gegenpressing plausibel, in der anderen wegen offener Räume oder fehlender Abstimmung riskant. Erklären Sie Eltern ohne Fußballhintergrund in drei Minuten den Unterschied und die Grenzen. Selbstcheck: Haben Sie die Taktik verständlich erklärt, ohne eine allgemeine Erfolgsgarantie oder neue Fakten zu behaupten?",
            feedback = "Modellrückmeldung: Ihre Gliederung ist klar, und die Analogie passt zum Publikum. 'Das Konzept ist sicher' vermittelt jedoch mehr, als der Nachweisstand hergibt; besser: 'Das Konzept ist beschrieben, der Nachweis ist teilweise erbracht.' Achten Sie auf Satzlänge: Partizipialattribute, die auf Folien gut lesbar sind, überfordern im Hören. Bei den U12-Spielern war die Szenenbeschreibung konkret; ergänzen Sie nach jeder Erklärung eine kurze Verständnisfrage, die Kinder und Eltern in eigenen Worten beantworten können."
        )
    ),

    // ------------------------------------------------------------------ s5-revise
    CurriculumUnit(
        "s5-revise", "Texte stilistisch überarbeiten", 3, emptyList(),
        listOf(
            "Unklare Bezüge, Leerformeln und überladene Sätze erkennen und präzise ersetzen.",
            "Einen Text auf Leserbedürfnisse und Register abstimmen, ohne den Inhalt zu verändern.",
            "Eigene Eingriffe kurz begründen und offene fachliche Fragen an die Autorin oder den Autor zurückgeben."
        ),
        contextsB(
            "Einen Kommentar redigieren",
            "Eine Einladung inklusiv überarbeiten und ein drängendes 'Du kommst doch' durch eine offene Frage ersetzen",
            "Eine Automotive-Dokumentation zur ECU/TCU-Schnittstelle präzisieren",
            "Eine U13-Trainingsanleitung zu Passdreieck und Rollenwechsel für neue Co-Trainer überarbeiten"
        ),
        UnitContent(
            reading = """
                Redaktionsbeispiel (fiktive Schnittstellendokumentation, Projekt Aurora; alle Namen und Werte erfunden)

                Entwurf: „Es ist festzustellen, dass es bei der Übertragung der Momentanforderung manchmal zu Verzögerungen kommt, die dann dazu führen können, dass diese von ihr nicht rechtzeitig verarbeitet wird, was ein Rucken bewirken kann. Das sollte man beachten.“

                Dieser Satz ist grammatisch korrekt und trotzdem schwer zu gebrauchen. Wer ist „diese“, wer ist „ihr“, was ist „das“? Was heißt „manchmal“? Überarbeitung: „Die TCU sendet die Momentanforderung MomAnf_TCU (fiktiver Signalname) zyklisch alle 10 ms an die ECU. Trifft die Nachricht mehr als 30 ms nach der vorherigen ein, verwirft die ECU den Wert und hält das zuletzt gültige Moment. Beim Wechsel vom elektrischen zum kombinierten Antrieb kann das als Rucken spürbar werden. Die Zeitgrenze von 30 ms ist ein Entwurfswert und am Fahrzeug noch nicht validiert.“

                Vier Eingriffe lassen sich benennen. Erstens wurden unklare Pronomen durch konkrete Substantive ersetzt, damit jede Referenz eindeutig ist. Zweitens wurden Leerformeln („Es ist festzustellen, dass“, „manchmal“) durch eine Bedingung und einen Wert ersetzt, sodass der Satz überprüfbar wird. Drittens wurde der Schachtelsatz in einzelne Sätze mit je einem Gedanken zerlegt; die Folge Ursache, Reaktion, Wirkung ist jetzt chronologisch. Viertens wurde die Einschränkung ausdrücklich gemacht, damit niemand den Entwurfswert für einen verifizierten Wert hält.

                Beachten Sie die Grenze der Redaktion: Wer redigiert, verbessert die Form, nicht den Fachinhalt. Wenn unklar ist, ob die ECU den Wert verwirft oder nur markiert, wird diese Frage als Randnotiz an den Autor zurückgegeben, statt sie zu erraten. Eine gute Überarbeitung beginnt außerdem mit der Frage, wer liest: Ein neuer Testingenieur braucht die Zahlen, ein Projektleiter vor allem die Konsequenz.
            """.trimIndent(),
            listening = """
                Lena: Murat, du hast jetzt zwei Wochen mit der U13 trainiert. Lies bitte mal unsere Anleitung zum Passdreieck laut vor, so wie sie dasteht.
                Murat: 'Die Spieler machen dann halt das Dreieck und passen, und danach geht es los mit dem Wechsel.' Ähm, ehrlich gesagt, ich habe beim ersten Mal nicht verstanden, wer 'die' ist.
                Lena: Gut, dass du das sagst. Und was ist 'das Dreieck'?
                Murat: Keine Ahnung, wie groß. Und 'der Wechsel', was wechselt da? Die Rolle, die Seite, die Passrichtung?
                Frau Schulte: Darf ich als Mutter etwas ergänzen? Ich helfe beim Aufbau und hatte Mühe, weil nirgends steht, wie viele Hütchen ich hinlegen soll.
                Lena: Stimmt, das fehlt komplett. Dann streichen wir das 'halt' und das 'dann', die tragen ja nichts.
                Murat: Wie wäre es mit: 'Je drei Kinder bilden ein Dreieck mit etwa zehn Metern Seitenlänge, markiert durch drei Hütchen.'
                Lena: Das ist schon viel besser. Und der Wechsel: 'Nach zwei Minuten ändert die Trainerin die Passrichtung.'
                Murat: Moment, das sagen wir aber nicht immer. Manchmal wechseln wir nach einem Fehler. Oder ... nee, nach einem Fehler startet man nur neu.
                Lena: Richtig, das müssen wir trennen. Zwei Regeln, zwei Sätze. Sonst verwechselt jemand Neustart und Richtungswechsel.
                Frau Schulte: Und könnte man 'Freilaufen' vielleicht einmal kurz erklären? Ich war mir nicht sicher, ob damit Rennen gemeint ist.
                Lena: Gute Frage. Wir schreiben: 'Freilaufen heißt, sich ohne Ball so anzubieten, dass ein Pass möglich wird.'
                Murat: Soll ich noch die Reihenfolge nummerieren? Eins, Aufbau, zwei, Passfolge, drei, Fehlerregel, vier, Richtungswechsel.
                Lena: Ja, mach das. Neue Co-Trainer lesen so etwas ja meist zwischen Tür und Angel.
                Frau Schulte: Dann lese ich Ihnen die neue Fassung nächste Woche vor, und Sie sagen mir, ob ich sie ohne Rückfragen verstehe.
                Lena: Abgemacht. Das ist ein guter Test.
            """.trimIndent(),
            expressions = listOf(
                "Worauf bezieht sich 'diese' / 'das' hier? – einen unklaren Bezug aufdecken",
                "Das ist mir zu vage. Was genau meinst du mit …? – eine Leerformel konkretisieren",
                "einen Text straffen / präzisieren / entflechten / auf den Punkt bringen – typische Verben des Redigierens",
                "Pro Satz ein Gedanke. – eine einfache Regel für Lesbarkeit",
                "Wie wäre es mit der Formulierung: …? / Könnte man das aktiv formulieren? – Verbesserungen vorschlagen",
                "Eine Frage an den Autor: Ist gemeint, dass …? – Fachfragen zurückgeben, statt zu raten",
                "Kohärenz herstellen / Verständlichkeit erhöhen / das Register anpassen – feste Verbindungen",
                "für wen schreiben wir? / Was braucht die Leserin als Erstes? – vom Leserbedürfnis her denken"
            ),
            grammar = "Referenz: Pronomen und Demonstrativa ('diese', 'das', 'dies') müssen eindeutig auf einen Bezugsausdruck verweisen; bei zwei möglichen Bezügen wiederholen Sie das Substantiv. Nominalstil vs. Verbalstil: 'Die Durchführung der Überprüfung erfolgt durch das Team' wird klarer als 'Das Team überprüft'. Funktionsverbgefüge wie 'zur Anwendung kommen' oder 'in Betracht ziehen' dürfen bleiben, wenn sie Register oder Rhythmus tragen, lassen sich sonst aber oft durch ein Vollverb ersetzen ('anwenden', 'erwägen'). Konnektoren müssen logisch passen: 'deshalb' nennt eine Folge, 'dadurch' ein Mittel oder einen Weg, 'trotzdem' einen Gegensatz. Ein Partizipialattribut verdichtet ('der am Fahrzeug noch nicht validierte Wert'), ein Relativsatz entlastet den Satz ('der Wert, der am Fahrzeug noch nicht validiert ist'). Modalpartikeln wie 'halt' oder 'dann' nicht pauschal streichen: Prüfen Sie, ob sie Haltung, Reihenfolge oder Gesprächsbezug ausdrücken; entfernen Sie sie, wenn sie für die Anleitung nichts beitragen.",
            question = "Welche Änderung beseitigt das Hauptproblem des ursprünglichen Entwurfs am besten?",
            options = listOf(
                "Der Entwurf wird kürzer, indem die Hälfte der Wörter gestrichen wird.",
                "Unklare Pronomen werden durch konkrete Substantive ersetzt, und 'manchmal' wird durch Bedingung und Wert ersetzt.",
                "Alle Fachbegriffe werden vermieden, damit der Text für alle verständlich ist.",
                "Jeder Satz wird ins Passiv gesetzt, damit er sachlicher klingt."
            ),
            correctOption = 1,
            explanation = "B nennt die zwei tatsächlichen Schwächen: mehrdeutige Bezüge und nicht überprüfbare Aussagen. A kann Information zerstören, ohne Klarheit zu schaffen. C ist falsch, weil Leser einer Schnittstellendokumentation die Fachbegriffe brauchen. D verschärft das Problem, da Passiv handelnde Instanzen verbirgt.",
            controlledPrompt = "Überarbeiten Sie den Entwurfssatz: 'Manchmal passiert es, dass die Spieler nicht wissen, was zu tun ist, was dann zu Wartezeiten führt.' Verwenden Sie ein konkretes Subjekt, eine Bedingung mit 'wenn', eine konkrete Folge und einen zweiten Satz mit einer Handlungsanweisung für die Trainerin.",
            controlledModel = "Wenn die Kinder die Passfolge nicht kennen, bleiben sie an den Hütchen stehen, und die Übung gerät ins Stocken. Die Trainerin lässt deshalb vor dem Start eine Gruppe die Passfolge vormachen.",
            speaking = "Sprechen Sie zwei Minuten ohne Vorlage: Erklären Sie einer Kollegin, wie Sie den folgenden Entwurf überarbeiten würden und warum: 'Die Übung wird dann so gemacht, dass die Spieler halt passen und anschließend sich was suchen, wo sie stehen sollen.' Benennen Sie mindestens drei Eingriffe (Bezug, Leerformel, Satzbau) und nennen Sie eine Rückfrage an den Autor.",
            interaction = "A hat einen Text geschrieben (Trainingsanleitung oder ECU/TCU-Dokumentation), B redigiert ihn live. Twist: A reagiert gekränkt ('Das habe ich doch genau so gemeint') und beharrt auf einem unklaren Satz. B würdigt zuerst, was funktioniert, begründet jeden Eingriff mit einem konkreten Leserbedürfnis und gibt Fachfragen an A zurück, statt sie zu entscheiden. Tauschen Sie die Rollen.",
            writing = "Überarbeiten Sie diesen Entwurf zu einer Anleitung von 120–150 Wörtern und fügen Sie ein Änderungsprotokoll mit drei Eingriffen hinzu: 'Es wird dann irgendwie so gespielt, dass die Kinder sich halt freilaufen und das Zuspiel ankommt, wenn nichts dazwischenkommt, und dann geht man weiter, weil das ja wichtig ist und auch Sinn hat, wobei man darauf achten sollte, dass das auch alle verstehen.' Klären Sie Bezüge, ersetzen Sie Leerformeln, gliedern Sie nach Reihenfolge und kennzeichnen Sie offene Fragen an die Autorin.",
            transfer = "Persönliche Transferübung: Wählen Sie einen eigenen kurzen Text, den Sie überarbeiten dürfen, oder verfassen Sie selbst einen Rohtext mit einer Einladung und einer mehrdeutigen Wendung wie 'Du kommst doch, oder?'. Überarbeiten Sie ihn in 20 Minuten für eine genau benannte Leserschaft, behalten Sie die Fakten bei und notieren Sie drei Änderungen samt Zweck. Selbstcheck: Ist der Ton passend, die Aussage eindeutig und jede Änderung begründet? Eine eigene Überarbeitung ist kein unabhängiges Qualitätsurteil.",
            feedback = "Modellrückmeldung: Sie haben die unklaren Bezüge erkannt und konkret ersetzt; das Änderungsprotokoll macht Ihre Entscheidungen nachvollziehbar. Achten Sie darauf, nicht stillschweigend Fachinhalt zu ergänzen, etwa einen Zahlenwert, den der Entwurf nicht enthält; stellen Sie stattdessen eine Rückfrage. Ihre Sätze sind kürzer, aber 'dadurch' und 'deshalb' sollten Sie noch auf ihre logische Funktion prüfen. Bei der Einladung wirkt die offene Frage ('Hast du Lust und Zeit vorbeizukommen?') einladender als 'Du kommst doch', weil sie auch eine Absage zulässt."
        )
    ),

    // ------------------------------------------------------------------ s2-register
    CurriculumUnit(
        "s2-register", "Register bewusst wechseln", 4, emptyList(),
        listOf(
            "Dieselbe Botschaft in formeller und informeller Fassung vollständig und angemessen formulieren.",
            "Anrede, Satzbau, Wortwahl, Modalpartikeln und Höflichkeitsmittel gezielt dem Verhältnis und dem Medium anpassen.",
            "Beim Wechsel zwischen Du und Sie sowie zwischen Gespräch und Dokumentation den Inhalt unverändert und vollständig halten."
        ),
        contextsB(
            "Eine Anfrage an eine Behörde richten",
            "Eine sensible Nachricht mit oder ohne 'mal' formulieren; Du und Sie nicht aus regionaler Nähe ableiten",
            "Einen ECU/TCU-Schnittstellenfehler intern und gegenüber einem Automotive-Zulieferer melden",
            "Eine Trainingsabsage für U11-Spieler und deren Eltern unterschiedlich formulieren"
        ),
        UnitContent(
            reading = """
                Zwei Botschaften, je zwei Register (alle Namen und Versionsnummern fiktiv)

                A, formell, E-Mail an die Eltern: „Sehr geehrte Eltern, leider muss das Training der U11 am kommenden Donnerstag ausfallen, da der Platz aufgrund der Witterung gesperrt wurde. Wir bitten Sie, Ihre Kinder an diesem Tag nicht zum Sportplatz zu schicken. Ein Ersatztermin wird Ihnen bis Freitag mitgeteilt. Für Rückfragen stehen wir Ihnen gern zur Verfügung. Mit freundlichen Grüßen, das Trainerteam“

                B, informell, Gruppenchat der Kinder: „Hi Leute, Donnerstag fällt das Training aus, der Platz ist dicht, da steht alles unter Wasser. Bleibt also zu Hause und jongliert mal ein bisschen im Flur. Den Ersatztermin sag ich euch bis Freitag. Bis bald, euer Coach“

                C, informell, internes Ticket: „Moin Kai, seit dem neuen Stand verwirft die ECU ab und zu die Momentanforderung der TCU, die Logs hängen am Ticket. Kannst du mal nach den Zykluszeiten schauen? Ich vermute erst mal nichts, will nur wissen, ob es am Timing liegt.“

                D, formell, Meldung an den Zulieferer: „Sehr geehrter Herr Albrecht, bei der Erprobung des Softwarestands 2.4 haben wir festgestellt, dass die ECU in mehreren Messfahrten die Momentanforderung der TCU verworfen hat. Die Aufzeichnungen sind dem Ticket beigefügt. Wir bitten Sie, die Zykluszeit der betreffenden Nachricht zu prüfen und uns bis Mittwoch eine Ersteinschätzung zukommen zu lassen. Ob ein Fehler in Ihrer Software vorliegt, ist derzeit offen.“

                Was bleibt gleich? Anliegen, Grund, Bitte und Frist sind in beiden Fassungen vorhanden. Was ändert sich? Anrede (Sie/Du), Satzbau (Nebensätze und Nominalstil gegenüber kurzen Hauptsätzen), Höflichkeitsform (Wir bitten Sie gegenüber Imperativ mit mal), Wortwahl (Platz gesperrt gegenüber Platz ist dicht) und Medium. Ein Register ist nicht richtiger als das andere; es ist angemessen oder unangemessen für Beziehung und Zweck.
            """.trimIndent(),
            listening = """
                Nora: Nora Fischer, Entwicklung. Guten Tag, Herr Albrecht. Ich rufe wegen des Tickets zur Momentanforderung an.
                Albrecht: Guten Tag, Frau Fischer. Ja, das Ticket liegt vor mir. Ich habe die Logs gesehen, aber ich bräuchte noch etwas Kontext.
                Nora: Sehr gern. Wir haben bei mehreren Messfahrten festgestellt, dass die ECU Momentanforderungen der TCU verwirft. Ob die Ursache bei Ihrem Softwarestand liegt, können wir derzeit nicht sagen.
                Albrecht: Verstehe. Könnten Sie mir die Zykluszeiten Ihrer Aufzeichnung schicken?
                Nora: Moment, ich frage kurz nach. Kai, hast du die Zykluszeiten schon extrahiert? Ich hab sie gerade nicht im Kopf.
                Kai: Ja, liegen im Ordner. Ich schick sie dir gleich rüber, ist nur ein Klick.
                Nora: Danke dir. Entschuldigung, Herr Albrecht, ich musste kurz mit dem Kollegen sprechen. Sie erhalten die Daten noch heute per Ticket.
                Albrecht: Kein Problem. Sagen Sie, wir arbeiten ja schon eine Weile zusammen. Sollen wir nicht einfach beim Du bleiben?
                Nora: Gern, das freut mich, ich bin Nora. Im Ticket bleiben wir aber trotzdem bei der formellen Form, weil das Dokument später auch andere lesen.
                Albrecht: Völlig in Ordnung, das sehe ich genauso. Dann, Nora, eine Rückfrage zur Frist: Bis wann brauchst du die Ersteinschätzung?
                Nora: Bis Mittwoch wäre ideal, ein Zwischenstand reicht. Wenn es länger dauert, melde dich einfach kurz.
                Albrecht: Das schaffe ich. Ich würde allerdings vorschlagen, dass wir die Fehlerklassifizierung noch nicht festlegen.
                Nora: Genau, das sehe ich auch so. In der Meldung steht ausdrücklich, dass die Ursache offen ist.
                Albrecht: Gut. Dann schreibe ich im Ticket: Ersteinschätzung bis Mittwoch, Ursache offen. Einverstanden?
                Nora: Einverstanden. Vielen Dank für das Gespräch, Herr Albrecht, äh, Entschuldigung, Albrecht ... Thomas.
                Albrecht: Kein Problem, Nora, das braucht ein paar Wochen. Bis Mittwoch dann.
            """.trimIndent(),
            expressions = listOf(
                "Sehr geehrte Frau …, / Hallo Kai, – Anrede nach Beziehung, Medium und Dokumentationszweck wählen",
                "Wir bitten Sie, … zu prüfen. / Kannst du mal … schauen? – eine Bitte formell oder informell formulieren",
                "Für Rückfragen stehen wir Ihnen gern zur Verfügung. / Meld dich einfach, wenn was unklar ist. – Kontaktangebot",
                "Wir bitten um eine Rückmeldung bis … / Gib mir bitte bis … Bescheid. – eine Frist setzen",
                "leider muss … ausfallen / fällt aus – eine Absage formell und informell formulieren",
                "Bezug nehmen auf …, um Verständnis bitten, eine Rückmeldung erbitten – feste Verbindungen der formellen Sprache",
                "Sagen wir Du? – Im Ticket bleiben wir beim Sie. – Anrede wechseln und das Medium trennen",
                "Entschuldigung, ich musste kurz mit dem Kollegen sprechen. – eine Registerlücke im Gespräch sauber reparieren"
            ),
            grammar = "Formell tendiert die Sprache zu Nebensätzen und Nominalstil: 'aufgrund der Witterung', 'zur Verfügung stehen'; informell zu kurzen Hauptsätzen und Verbalstil: 'weil es regnet', 'meld dich'. Höflichkeit im Konjunktiv II: 'Könnten Sie …', 'Wären Sie so freundlich …'; informell genügt oft der Imperativ mit Abtönung: 'Schau mal …'. Modalpartikeln wie 'mal', 'halt', 'doch', 'ja' gehören vor allem in die informelle Rede und mildern oder betonen: 'Kannst du mal schauen?' ist weicher als 'Schau nach!', 'doch' kann aber drängend wirken. Schriftlich-formell stehen oft Präteritum und Passiv: 'wurde gesperrt', 'wird mitgeteilt'; mündlich-informell dominiert das Perfekt: 'ist gesperrt worden', 'sag ich euch'. Die Anrede ergibt sich aus Beziehung, Hierarchie und Medium, nicht aus regionaler Nähe: Ein Du im Gespräch heißt nicht, dass ein Dokument informell sein darf.",
            question = "Welche Fassung ist eine angemessene formelle Version von 'Kannst du mal nach den Zykluszeiten schauen?' in einem Zulieferer-Ticket?",
            options = listOf(
                "Wir bitten Sie, die Zykluszeit der betreffenden Nachricht zu prüfen.",
                "Sie müssen die Zykluszeiten prüfen.",
                "Könntest du bitte die Zykluszeiten prüfen?",
                "Schauen Sie mal schnell nach den Zykluszeiten, ja?"
            ),
            correctOption = 0,
            explanation = "A verbindet Sie-Anrede, Bitte statt Befehl und sachliche, präzise Formulierung. B ist formell, aber als Anweisung gegenüber einem Zulieferer unangemessen hart. C verwendet das Du und passt damit nicht zum formellen Ticket. D mischt Sie-Anrede mit 'mal schnell' und 'ja?' und wirkt drängend und unsauber.",
            controlledPrompt = "Übertragen Sie in die formelle Fassung: 'Hi Leute, Donnerstag fällt das Training aus, der Platz ist dicht. Den Ersatztermin sag ich euch bis Freitag.' Verwenden Sie 'leider', einen Grund mit 'aufgrund', eine Passivform und 'bis Freitag'.",
            controlledModel = "Sehr geehrte Eltern, leider muss das Training am Donnerstag aufgrund der Platzsperrung ausfallen. Der Ersatztermin wird Ihnen bis Freitag mitgeteilt.",
            speaking = "Sprechen Sie dieselbe Absage zweimal ein, je 30 Sekunden: einmal als Sprachnachricht an die U11-Kinder (Du/Ihr, kurze Sätze, Modalpartikel), einmal als Ansage an die versammelten Eltern auf dem Sportplatz (Sie, höflich, vollständig). Inhalt: Der Platz ist wegen Wasser gesperrt, Ersatztermin bis Freitag, Rückfragen an die Trainerin. Erklären Sie anschließend in zwei Sätzen, welche Mittel Sie verändert haben.",
            interaction = "A ruft als Ingenieurin bei einem Zulieferer an (Sie, ECU/TCU-Schnittstellenfehler, Ursache offen); B ist der Ansprechpartner. Twist: B bietet mitten im Gespräch das Du an und sagt später: 'Schick mir das halt formlos per Chat.' A nimmt das Du an, besteht aber darauf, dass die Meldung im Ticket formell bleibt, und repariert einen Registerfehler ('Sie ... äh, du ...') souverän. Tauschen Sie die Rollen.",
            writing = "Schreiben Sie dieselbe Botschaft in zwei Fassungen (je 70–90 Wörter, zusammen 140–180): Der Treffpunkt für das Hallenturnier der U11 ändert sich von 9.00 Uhr am Sportplatz auf 8.30 Uhr am Vereinsheim; Fahrgemeinschaften bitte bis Donnerstag melden. Fassung 1: E-Mail an die Eltern (Sie). Fassung 2: Chatnachricht an die Kinder (Du/Ihr). Ergänzen Sie drei Sätze, die erklären, welche Mittel Sie verändert haben (Anrede, Satzbau, Höflichkeit, Wortwahl).",
            transfer = "Persönliche Transferübung: Nehmen Sie denselben Sachverhalt – zum Beispiel eine Rückfrage zur Platznutzung – und verfassen Sie in 15 Minuten zwei Fassungen: eine formelle Nachricht an die Gemeinde und eine kurze Nachricht an eine befreundete Trainerin. Begründen Sie drei Registerentscheidungen. Selbstcheck: Bleiben Fakten und Anliegen gleich, während Anrede, Wortwahl und Direktheit zum jeweiligen Verhältnis passen?",
            feedback = "Modellrückmeldung: Ihre beiden Fassungen enthalten denselben Kerninhalt, und die Anrede ist durchgehend konsequent. In der formellen Version wirken 'Sie müssen' und 'bitte schnell' dagegen unhöflich; 'Wir bitten Sie, … zu prüfen' trägt dieselbe Bitte freundlicher. In der informellen Version ist 'mal' hilfreich, solange es nicht ersetzt, dass Sie Frist und Grund nennen. Beim Zulieferer ist wichtig: Ein Du im Telefonat ändert nichts am formellen Ticket. Prüfen Sie am Ende, ob in beiden Fassungen Grund, Bitte, Frist und Kontaktmöglichkeit vorkommen."
        )
    ),

    // ------------------------------------------------------------------ s3-negotiate
    CurriculumUnit(
        "s3-negotiate", "Interessen aushandeln", 4, emptyList(),
        listOf(
            "Positionen von dahinterliegenden Interessen trennen und beide Seiten ausdrücklich erfragen.",
            "Mehrere Optionen entwickeln, mit Kriterien bewerten und Bedingungen sowie Überprüfungstermine festhalten.",
            "Zugeständnisse, Vorbehalte und Vereinbarungen sprachlich präzise und gesichtswahrend formulieren."
        ),
        contextsB(
            "Einen Termin aushandeln",
            "Haushaltsaufgaben verhandeln, ohne mit 'Das machst du doch' Zustimmung zu unterstellen",
            "Prüfumfang für eine Hybrid-TCU-Änderung unter Safety-Vorgaben aushandeln",
            "Trainingszeiten und Platzhälften zwischen U13 und U15 aushandeln"
        ),
        UnitContent(
            reading = """
                Fallbeispiel (fiktiv): Platzverteilung beim SV Lindenau

                Im November wird es gegen fünf Uhr dunkel, und das Flutlicht beleuchtet nur eine Platzhälfte. Donnerstags wollen U13 und U15 gleichzeitig trainieren. Die U13-Trainerin Fatma Kaya fordert: „Wir brauchen die beleuchtete Hälfte von 17 bis 18.30 Uhr.“ Der U15-Trainer Dirk Roth hält dagegen: „Wir müssen die beleuchtete Hälfte haben, sonst können wir nicht ordentlich arbeiten.“ Beide nennen eine Position, und beide Positionen schließen sich scheinbar aus.

                Erst die Nachfrage „Wozu brauchst du das genau?“ legt die Interessen offen. Frau Kaya braucht Licht aus Sicherheitsgründen und Platz für drei Felder im Format 4-gegen-4, die quer nebeneinander passen. Herr Roth braucht Länge: Seine Spieler üben den Spielaufbau gegen Gegenpressing mit Viererkette und Besetzung der Halbräume, und das ist auf zu kleiner Fläche nicht realistisch. Außerdem möchte er, dass das Training nicht erst nach 19 Uhr endet, weil manche Jugendliche mit dem Bus fahren.

                Aus diesen Interessen entwickeln beide gemeinsam vier Optionen: (1) Zeitscheiben auf der beleuchteten Hälfte, erst U13, dann U15; (2) zusätzliche mobile Leuchten auf der zweiten Hälfte, finanziert vom Verein; (3) wöchentlicher Wechsel der Hälften; (4) die U15 weicht alle zwei Wochen in die Halle aus. Sie bewerten die Optionen nach vier Kriterien: Sicherheit, Entwicklungsziel, Verlässlichkeit für die Eltern und Kosten. Option 2 scheidet vorerst aus, weil kein Budget vorliegt. Vereinbart werden eine Kombination aus Option 1 und Option 4 sowie eine Probephase von vier Wochen. Danach sprechen beide erneut, wobei sich Frau Kaya und Herr Roth gegenseitig zusichern, Beschwerden der Eltern zunächst gemeinsam zu klären. Das Beispiel zeigt: Ein Kompromiss entsteht nicht durch Nachgeben um jeden Preis, sondern durch genaues Fragen.
            """.trimIndent(),
            listening = """
                Frank: Also, die TCU-Änderung muss zum Meilenstein in drei Wochen raus. Ich schlage vor, wir testen nur die geänderte Funktion am Prüfstand und sparen uns den Rest.
                Dr. Yilmaz: Den Rest sparen? Das ist mir zu pauschal. Ich brauche erst die Auswirkungsanalyse, bevor ich einem Umfang zustimmen kann.
                Frank: Die haben wir doch schon, oder, Paul?
                Paul: Na ja, halb. Die Änderung betrifft die Schaltstrategie im Komfortbereich. Die Schnittstelle zur Drehmomentüberwachung haben wir nicht angefasst, meines Wissens jedenfalls.
                Dr. Yilmaz: Meines Wissens reicht mir nicht. Was heißt 'nicht angefasst'? Gleicher Quellcode, gleiche Konfiguration, oder nur gleiche Signalnamen?
                Paul: Ähm, gleiche Signale, aber die Aufrufreihenfolge im Task hat sich geändert. Das hatte ich vorhin nicht erwähnt, tut mir leid.
                Dr. Yilmaz: Danke, dass du das sagst. Dann ist die Schnittstelle zur Überwachung potenziell betroffen.
                Frank: Wir sollten aber klären, worum es uns jeweils geht. Mir geht es nicht um den Test an sich, sondern um den Termin. Was brauchst du denn wirklich, Selin?
                Dr. Yilmaz: Ich brauche einen belastbaren Nachweis, dass die sicherheitsrelevanten Anforderungen weiterhin erfüllt sind. Wie viele Tests dafür nötig sind, ist verhandelbar.
                Paul: Wie wäre es, wenn wir die Regression nach Risiko staffeln? Volle Wiederholung für alles, was die Momentenüberwachung berührt, und Stichproben für die Komfortfunktionen.
                Dr. Yilmaz: Das könnte ich mittragen, sofern die Auswahl der Stichproben vorab dokumentiert wird und nicht erst nach den Ergebnissen.
                Frank: Einverstanden. Und die Fahrzeugerprobung? Wenn wir die auch noch komplett wiederholen, reißen wir den Meilenstein.
                Dr. Yilmaz: Fahrzeugtests hängen vom Prüfstandergebnis ab. Ich würde sagen: erst Prüfstand. Fällt dort nichts auf, reicht eine verkürzte Fahrzeugerprobung, andernfalls erweitern wir.
                Frank: Und wenn am Prüfstand etwas auffällt, verschiebt sich der Meilenstein?
                Dr. Yilmaz: Dann ja. Das ist meine Bedingung. Dafür bekommst du von mir ein Zwischenvotum direkt nach dem Prüfstand, nicht erst am Ende.
                Frank: Damit kann ich leben. Paul, schreibst du bitte die Aufrufreihenfolge in die Auswirkungsanalyse?
                Paul: Mach ich bis morgen Mittag. Ich ergänze auch, welche Signale unverändert sind, belegt durch einen Konfigurationsvergleich.
                Dr. Yilmaz: Gut. Dann halten wir fest: risikobasierter Umfang, Auswahl dokumentiert, erst Prüfstand, Zwischenvotum, Verschiebung nur bei Auffälligkeiten.
            """.trimIndent(),
            expressions = listOf(
                "Mir geht es nicht um …, sondern um … – ein Interesse hinter einer Position benennen",
                "Wozu brauchst du das genau? / Was ist dir dabei besonders wichtig? – Interessen erfragen",
                "Wie wäre es, wenn wir …? – eine Option vorschlagen, ohne sie aufzudrängen",
                "Das könnte ich mittragen, sofern … – ein bedingtes Einverständnis geben",
                "einen Kompromiss eingehen / eine Einigung erzielen / Zugeständnisse machen – feste Verbindungen des Verhandelns",
                "in Betracht ziehen / zur Verfügung stellen / Rücksicht nehmen auf … – Funktionsverbgefüge in Verhandlungen",
                "Dann halten wir fest: … / Wer macht was bis wann? – eine Vereinbarung absichern",
                "Wir überprüfen das nach vier Wochen. – eine Probephase und einen Revisionstermin festlegen"
            ),
            grammar = "Bedingungen formulieren Sie mit 'wenn', 'falls', 'sofern', 'vorausgesetzt, dass …' und 'es sei denn, …'; 'sofern' und 'vorausgesetzt' sind formeller und betonen die Voraussetzung. Der Konjunktiv II macht Vorschläge weicher: 'Wie wäre es, wenn wir …?', 'Ich könnte mir vorstellen, dass …'. Funktionsverbgefüge verleihen Verhandlungssprache Gewicht: 'einen Kompromiss eingehen', 'Rücksicht nehmen auf', 'in Erwägung ziehen'; im Alltag genügt oft das Vollverb ('erwägen'). Die Modalpartikel 'denn' mildert Fragen: 'Was brauchst du denn wirklich?' ist offener als 'Was brauchst du wirklich?'. Die Partikel 'ja' markiert Bekanntes: 'Das ist ja verhandelbar' setzt Zustimmung voraus und kann deshalb zu forsch wirken. Trennen Sie sprachlich Position ('Ich will …') von Interesse ('Mir ist wichtig, dass …') und verwenden Sie für Zusammenfassungen kurze, vollständige Sätze mit Verantwortlichen und Terminen.",
            question = "Welche Aussage aus dem Fallbeispiel nennt ein Interesse und keine Position?",
            options = listOf(
                "Wir brauchen die beleuchtete Hälfte von 17 bis 18.30 Uhr.",
                "Wir müssen die beleuchtete Hälfte haben, sonst können wir nicht ordentlich arbeiten.",
                "Die U15 braucht genügend Länge, um den Spielaufbau gegen Gegenpressing mit Viererkette und Halbräumen realistisch zu üben.",
                "Der Verein soll Flutlicht für beide Platzhälften installieren."
            ),
            correctOption = 2,
            explanation = "C beschreibt, wozu die U15 die Fläche braucht, und eröffnet damit mehrere Lösungen. A und B sind Forderungen (Positionen) nach einer konkreten Lösung. D ist ebenfalls eine Lösung, nämlich eine Option, kein Interesse.",
            controlledPrompt = "Formulieren Sie vier Sätze: (1) eine Position in ein Interesse übersetzen mit 'Mir geht es nicht um …, sondern um …'; (2) einen Vorschlag mit 'Wie wäre es, wenn …'; (3) ein bedingtes Einverständnis mit 'sofern'; (4) eine Probephase mit Überprüfungstermin.",
            controlledModel = "Mir geht es nicht um die gesamte beleuchtete Hälfte, sondern um genug Licht und Platz für drei Felder. Wie wäre es, wenn die U13 zuerst trainiert und die U15 danach folgt? Das könnte ich mittragen, sofern die U15 alle zwei Wochen in die Halle ausweichen kann. Wir probieren das vier Wochen aus und sprechen danach noch einmal darüber.",
            speaking = "Üben Sie zwei Minuten laut, indem Sie beide Rollen übernehmen und die Platznutzung zwischen U13 und U15 aushandeln. Beginnen Sie mit den zwei Positionen, erfragen Sie die Interessen, nennen Sie zwei Optionen, bewerten Sie diese nach Kriterien, formulieren Sie ein bedingtes Einverständnis und legen Sie eine Probephase mit Termin fest. Nutzen Sie mindestens zwei Fachbegriffe aus dem Fußball (zum Beispiel Kleinfeldspiel, Spielaufbau, Viererkette).",
            interaction = "A ist die U13-Trainerin, B der U15-Trainer. Twist: B hat gerade eine Beschwerde von Eltern über späte Trainingsenden erhalten, A eine über Dunkelheit; ein Platzwart, den beide als Dritter anrufen, sagt nur: 'Das machen wir schon.' A und B klären zuerst Interessen, rückversichern sich beim Platzwart ('Was genau übernehmen Sie bis wann?'), entwickeln mindestens drei Optionen und halten eine überprüfbare Vereinbarung fest. Tauschen Sie die Rollen.",
            writing = "Verfassen Sie ein Vereinbarungsprotokoll von 150–190 Wörtern zur Platznutzung zwischen U13 und U15: Ausgangslage, Positionen, Interessen, zwei verworfene und eine gewählte Option, Bedingungen, Verantwortliche, Probephase und Überprüfungstermin. Verwenden Sie mindestens ein Funktionsverbgefüge und einen sofern-Satz. Erfinden Sie keine Kostenzahlen.",
            transfer = "Persönliche Transferübung: Nutzen Sie ein WG-Szenario mit unterschiedlichen Arbeitszeiten (Schichtdienst, Homeoffice, Lernphase). Spielen Sie beide Rollen nacheinander laut oder schriftlich durch; trennen Sie Positionen von Interessen, vermeiden Sie Zustimmung unterstellende Sätze wie 'Das machst du doch' und halten Sie Zuständigkeit, Termin und Überprüfung fest. Selbstcheck: Ist das Ergebnis für beide Seiten konkret und überprüfbar, oder haben Sie einen Konflikt nur höflich zugedeckt?",
            feedback = "Modellrückmeldung: Sie fragen nach dem Wozu und finden dadurch mehrere Optionen. Ihre Vereinbarung enthält Verantwortliche, Fristen und eine Probephase. 'Das passt schon so' ist dagegen keine tragfähige Zusage, hier hilft die Nachfrage 'Was genau übernimmst du bis wann?'. Achten Sie darauf, Zugeständnisse mit Bedingungen zu verbinden ('sofern …') und Ihre eigenen Interessen genauso klar zu benennen wie die der anderen Seite. Im Prüfumfang-Gespräch gilt: Termin- und Safety-Interesse sind beide legitim; die Verhandlung betrifft den Umfang, nicht den Nachweis an sich."
        )
    ),

    // ------------------------------------------------------------------ s3-disagree
    CurriculumUnit(
        "s3-disagree", "Differenziert widersprechen", 4, emptyList(),
        listOf(
            "Einen Einwand klar in der Sache vertreten und die Person dabei respektieren.",
            "Die Position des Gegenübers korrekt wiedergeben (auch mit Konjunktiv I), bevor man ihr widerspricht.",
            "Widerspruch begründen, eine Alternative anbieten und die Tür für weitere Prüfung offen halten."
        ),
        contextsB(
            "Einer Empfehlung widersprechen",
            "Einer Gruppenentscheidung mit 'Da bin ich noch nicht ganz überzeugt' respektvoll widersprechen",
            "Einer unbelegten Root-Cause-Zuordnung zum EV-Inverter widersprechen",
            "Einem Elternwunsch nach fester U12-Stammaufstellung mit Entwicklungszielen widersprechen"
        ),
        UnitContent(
            reading = """
                Gesprächsbeispiel (fiktiv): Widerspruch im Elterngespräch, SG Lindenau U12

                Nach dem Spiel bittet Frau Weidner die Trainerin Anja Krüger um ein Gespräch. Ihr Sohn Ben solle in der U12 fest in der Startelf spielen, sonst verliere er die Freude am Fußball. Frau Krüger hört zuerst zu und fasst zusammen: „Wenn ich Sie richtig verstehe, wünschen Sie sich für Ben Verlässlichkeit und das Gefühl, dazuzugehören.“ Erst danach widerspricht sie, und zwar in drei Schritten.

                Erstens würdigt sie das Anliegen: „Dass Ben mit Begeisterung spielt, sieht man jedes Training.“ Zweitens benennt sie deutlich, wo sie anderer Meinung ist: „Eine feste Stammaufstellung halte ich in dieser Altersstufe nicht für sinnvoll.“ Drittens begründet sie mit dem Entwicklungskonzept der Mannschaft: „Bei uns sollen alle Kinder mehrere Positionen kennenlernen, etwa Außenverteidiger in der Viererkette, defensives Mittelfeld im Spielaufbau und Offensivrolle im Gegenpressing. Wer früh festgelegt wird, erlebt weniger Spielsituationen.“ Sie lässt es dabei nicht bei einem Nein: Ben erhält ein Entwicklungsgespräch, und die Einsatzzeiten werden für alle Eltern transparent nach Kriterien wie Trainingsbeteiligung und Positionswechseln geplant.

                Als Frau Weidner einwendet, andere Vereine machten das anders, bleibt die Trainerin sachlich: „Das kann sein; deren Konzepte kenne ich nicht im Einzelnen. Für unsere Mannschaft gilt das, was ich erläutert habe, und ich überprüfe es nach der Hinrunde.“

                Auffällig ist, was fehlt: Die Trainerin sagt nicht „Das sehen Sie falsch“, sie vertagt nicht mit „Mal sehen“, und sie macht Ben nicht zum Beispiel. Ein gelungener Widerspruch hat fünf Bestandteile: Anliegen anerkennen, die Position des Gegenübers korrekt wiedergeben, die eigene Position klar benennen, Gründe nennen und eine Alternative sowie einen Überprüfungstermin anbieten. Klarheit ist keine Härte; sie ist die Voraussetzung dafür, dass der andere weiß, woran er ist.
            """.trimIndent(),
            listening = """
                Marco: Dann halten wir fest: Das Drehmomentruckeln liegt am Inverter. Julia, du hattest das ja vorhin schon gesagt.
                Julia: Ja, es tritt erst seit dem neuen Inverter-Softwarestand auf, und die Phasenströme zeigen im Protokoll kleine Einbrüche. Für mich ist die Sache klar.
                Ömer: Entschuldige, wenn ich da kurz reingrätsche, aber da bin ich noch nicht ganz überzeugt.
                Marco: Okay, was stört dich?
                Ömer: Ich verstehe die Logik: Der Fehler erscheint nach dem Update, also liegt es am Update. Nur ist eine zeitliche Abfolge noch keine Ursache. Laut Änderungsprotokoll wurde mit dem Update auch die Kalibrierung des Rotorlagesensors angepasst.
                Julia: Das ist ein Punkt, aber die Anpassung ist doch minimal.
                Ömer: Minimal laut Beschreibung, gemessen haben wir das nicht. Ein Offset im Rotorlagewinkel würde ebenfalls zu schwankenden Phasenströmen führen, nur dass der Inverter dann korrekt auf falsche Eingangsdaten reagiert.
                Julia: Hm, okay, das ist plausibel. Aber wenn wir es nicht dem Inverter zuordnen, verlieren wir Zeit.
                Ömer: Das nehme ich ernst. Mein Vorschlag: Wir lassen die Zuordnung offen und trennen die Variablen, also alter Softwarestand mit neuer Kalibrierung und umgekehrt.
                Marco: Moment, ich will sichergehen, dass ich dich verstehe. Du sagst nicht, dass der Inverter unschuldig ist, sondern dass wir es noch nicht wissen?
                Ömer: Genau. Julia könnte durchaus recht haben. Ich widerspreche nur der Formulierung 'liegt am Inverter' im Protokoll.
                Julia: Gut, das kann ich mittragen. Im Protokoll steht dann: Hypothese Inverter, Gegenhypothese Kalibrierung, Prüfung am Prüfstand.
                Marco: Kurze Frage: Reicht ein Prüfstandlauf, oder brauchen wir das Fahrzeug?
                Ömer: Prüfstand zuerst, denn dort können wir die Kalibrierung gezielt variieren. Das Fahrzeug nur, wenn sich der Effekt am Prüfstand nicht zeigt.
                Marco: Gut. Dann schreibe ich: Ursache offen, Variantenvergleich bis Freitag, Entscheidung danach.
                Julia: Und ich bringe die Phasenstromdaten mit Zeitstempeln mit. Die sollten wir gegen die Rotorlagedaten legen.
            """.trimIndent(),
            expressions = listOf(
                "Da bin ich noch nicht ganz überzeugt. – einen Einwand abgestuft einleiten",
                "Ich sehe das etwas anders, weil … / Dem kann ich so nicht zustimmen, weil … – klar widersprechen und begründen",
                "Wenn ich dich richtig verstehe, meinst du …? – die Position des anderen prüfen, bevor man widerspricht",
                "Das ist ein berechtigter Punkt, aber … – ein Argument anerkennen und gewichten",
                "Ich widerspreche nicht deiner Beobachtung, sondern der Schlussfolgerung. – Sache und Person trennen",
                "Mein Gegenvorschlag lautet: … / Mir wäre lieber, wenn wir … – eine Alternative anbieten",
                "einen Einwand erheben / Bedenken äußern / der Argumentation nicht folgen können – feste Verbindungen des Widersprechens",
                "Wir lassen die Zuordnung offen, bis … – eine Entscheidung begründet vertagen"
            ),
            grammar = "Konjunktiv I gibt die Position des Gegenübers korrekt und distanziert wieder: 'Julia sagt, der Fehler liege am Inverter.' ('liege' = Konjunktiv I; wenn Konjunktiv I und Indikativ gleich lauten, wählt man Konjunktiv II: 'Sie sagen, die Eltern wollten …'.) Konzessive Konstruktionen zeigen Verständnis, ohne nachzugeben: 'So nachvollziehbar der Wunsch auch ist, eine feste Aufstellung passt nicht zum Entwicklungsziel.' Ebenso: 'Zwar … allerdings', 'trotz der Zeitnot', 'dennoch'. Der Konjunktiv II schwächt ab: 'Ich würde das anders sehen', 'Das könnte auch an der Kalibrierung liegen'; verwenden Sie ihn für Hypothesen, nicht, um klare Positionen zu verstecken. Modalpartikeln: 'doch' in 'Das ist doch minimal' behauptet Bekanntes und kann einen Widerspruch provozieren; 'schon' in 'Das ist schon richtig, aber …' signalisiert Zugeständnis; 'eigentlich' kann einen Widerspruch einleiten, aber auch abschwächen. Nominalisierungen wie 'meine Skepsis gegenüber der Zuordnung' machen den Widerspruch sachlich.",
            question = "Wodurch zeichnet sich der Widerspruch der Trainerin im Gespräch mit Frau Weidner aus?",
            options = listOf(
                "Sie weicht dem Wunsch aus und vertagt ihn mit 'Mal sehen'.",
                "Sie stellt klar, dass Frau Weidner die Entwicklung ihres Sohnes falsch einschätzt.",
                "Sie würdigt das Anliegen, benennt ihre Position klar, begründet mit Entwicklungszielen und bietet eine Alternative an.",
                "Sie gibt dem Wunsch nach, um das Gespräch zu beenden, und ändert die Planung später."
            ),
            correctOption = 2,
            explanation = "C fasst die fünf Bestandteile des Textes zusammen. A beschreibt ausdrücklich das, was die Trainerin nicht tut. B unterstellt einen persönlichen Vorwurf, den die Trainerin vermeidet. D widerspricht dem Gesprächsverlauf: Sie hält ihre Position fest und bietet stattdessen ein Entwicklungsgespräch an.",
            controlledPrompt = "Formulieren Sie vier Sätze: (1) die Position der Mutter korrekt wiedergeben, mit Konjunktiv I ('Sie sagt, …'); (2) Verständnis mit 'So nachvollziehbar … auch ist, …'; (3) eine Begründung mit Entwicklungsziel; (4) ein Angebot mit Überprüfungstermin.",
            controlledModel = "Frau Weidner sagt, ihr Sohn brauche einen festen Platz in der Startelf. So nachvollziehbar dieser Wunsch auch ist, halte ich eine feste Aufstellung in der U12 nicht für sinnvoll. Alle Kinder sollen in diesem Alter mehrere Positionen kennenlernen, etwa in der Viererkette und im Spielaufbau. Ich biete Ben ein Entwicklungsgespräch an und überprüfe die Planung nach der Hinrunde.",
            speaking = "Antworten Sie als U12-Trainerin in 90 Sekunden ohne Vorlage auf den Wunsch einer Mutter nach einer festen Stammaufstellung für ihren Sohn: Fassen Sie ihr Anliegen zusammen, widersprechen Sie klar mit einem Entwicklungsziel (mehrere Positionen kennenlernen, zum Beispiel Viererkette, Spielaufbau, Umschaltspiel), bieten Sie ein Entwicklungsgespräch an und nennen Sie einen Überprüfungstermin. Vermeiden Sie 'Das sehen Sie falsch' und 'Mal sehen'.",
            interaction = "A ist Entwicklerin und überzeugt, dass der EV-Inverter das Drehmomentruckeln verursacht; B ist Systemingenieur und widerspricht der Zuordnung, nicht der Beobachtung. Twist: A sagt 'Das sieht doch jeder' und verweist auf Lieferdruck; B fasst A's Position fair mit Konjunktiv I zusammen, erkennt die Zeitnot an, bleibt bei der Evidenzgrenze und schlägt einen Variantenvergleich am Prüfstand vor. Halten Sie ein überprüfbares Vorgehen fest. Tauschen Sie die Rollen.",
            writing = "Schreiben Sie eine E-Mail von 150–190 Wörtern an das Projektteam: Sie widersprechen der Protokollformulierung 'Die Ursache liegt am EV-Inverter', weil die Datenlage auch eine veränderte Kalibrierung des Rotorlagesensors zulässt. Geben Sie die Gegenposition mit Konjunktiv I korrekt wieder, würdigen Sie ihre Plausibilität, begründen Sie Ihren Einwand, schlagen Sie eine alternative Formulierung und einen Prüfschritt vor. Erfinden Sie keine Messwerte.",
            transfer = "Persönliche Transferübung: Wählen Sie einen Beschluss einer Hausgemeinschaft oder eines Vereins, dem Sie nicht zustimmen. Widersprechen Sie in drei Minuten mündlich oder schriftlich mit 'Da bin ich noch nicht ganz überzeugt', geben Sie die Gegenposition fair wieder, begründen Sie Ihre Sicht, bieten Sie eine Alternative an und reagieren Sie auf eine persönliche Spitze, ohne zurückzuschlagen. Selbstcheck: Ist die Gegenposition wiedererkennbar, Ihr Grund nachvollziehbar und die Alternative umsetzbar?",
            feedback = "Modellrückmeldung: Sie trennen Beobachtung und Schlussfolgerung und geben die Gegenposition korrekt wieder, bevor Sie widersprechen. 'Das stimmt einfach nicht' beendet allerdings das Gespräch; 'Dem kann ich so nicht zustimmen, weil …' lässt dieselbe Klarheit zu und lädt zum Prüfen ein. Prüfen Sie Ihren Konjunktiv I: 'Er sagt, der Fehler liege am Inverter' ist korrekt, 'liegt' würde Zustimmung andeuten. Beim Elternwunsch war Ihr Angebot (Entwicklungsgespräch, Überprüfungstermin) das Entscheidende; ohne es wäre der Widerspruch nur eine Absage."
        )
    )
)
