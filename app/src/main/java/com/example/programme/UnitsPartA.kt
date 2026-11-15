package com.example.programme

private fun contextsA(general: String, social: String, engineering: String, coaching: String) =
    mapOf(
        ContextTrack.GENERAL to general,
        ContextTrack.SOCIAL to social,
        ContextTrack.ENGINEERING to engineering,
        ContextTrack.COACHING to coaching
    )

/**
 * Fully authored units of part A. All teams, clubs, measurements and figures in these units are
 * fictional practice material and make no claim about real organisations or real data.
 */
internal val unitsPartA: List<CurriculumUnit> = listOf(
    CurriculumUnit(
        "s0-profile", "Sprachbiografie und Zielprofil", 0, emptyList(),
        listOf(
            "Kommunikative Ziele an konkreten Situationen festmachen statt allgemein 'besser sprechen' zu formulieren.",
            "Für jedes Ziel ein überprüfbares Kriterium und einen Termin für den eigenen Selbstcheck festlegen.",
            "Anrede, gewünschte Direktheit und eigene Prioritäten im Gespräch höflich klären."
        ),
        contextsA(
            "Behördengespräche planen",
            "Beim schwäbischen Vereinsabend Anrede und gewünschte Direktheit erfragen",
            "Sprachliche Aufgaben im EV-Fahrdynamikreview erfassen",
            "Sprachziele für U13-Trainingsansagen und Elterngespräche festlegen"
        ),
        UnitContent(
            reading = """
                Dilara Yıldız arbeitet als Fahrdynamik-Ingenieurin und trainiert nebenbei eine U13. Zu Beginn eines Sprachkurses schreibt sie nicht: „Ich möchte besser Deutsch sprechen.“ Ein solches Ziel lässt sich nicht überprüfen. Stattdessen sammelt sie vier Situationen aus den kommenden drei Monaten: das Fahrdynamikreview am Freitag, die Trainingsansage vor der U13, das Elterngespräch nach dem Turnier und einen Termin im Bürgeramt. Für jede Situation notiert sie, wer zuhört, was erreicht sein soll und woran sie den Erfolg erkennt. Im Review will sie fünf Minuten ohne Manuskript erklären, warum eine Messung nur eingeschränkt aussagekräftig ist; ihr Kollege Jan soll anschließend wiedergeben können, was offen geblieben ist. Für die Trainingsansage legt sie fest, dass die Jugendspieler die Übung nach höchstens zwei Erklärungen selbst zeigen können. Für den Vereinsabend plant sie zunächst nur, zu fragen, ob man sich duzt und wie direkt Kritik geäußert werden darf. Außerdem hält sie fest, was sie vorerst nicht übt: regionale Redeweisen will sie verstehen lernen, aber noch nicht selbst verwenden. Nach vier Wochen vergleicht sie ihre eigene Einschätzung mit dem Urteil ihrer Kursleiterin und ändert ihr Profil, wenn die Kriterien nicht passen.
            """.trimIndent(),
            listening = """
                Beraterin: Was möchten Sie denn in den nächsten Monaten erreichen?
                Dilara: Na ja, ich will einfach sicherer werden im Deutschen.
                Beraterin: „Sicherer“ ist ein Anfang. Woran würden Sie das merken – in welcher Situation?
                Dilara: Am Freitag beim Review. Da muss ich eine Messung erklären, und bisher lese ich meistens ab.
                Beraterin: Gut. Und was wäre ein überprüfbares Kriterium? Wie lange, für wen, mit welchem Ergebnis?
                Dilara: Fünf Minuten, frei gesprochen, und mein Kollege kann hinterher sagen, was noch offen ist.
                Beraterin: Das gefällt mir. Wer beurteilt das – Sie selbst oder jemand anderes?
                Dilara: Ich schätze mich meistens besser ein, als es dann ist. Also lieber Jan und Sie.
                Beraterin: Genau, ein Fremdurteil gehört dazu. Und was ist mit dem Vereinsabend?
                Dilara: Da will ich erst mal nur nachfragen können, ob wir uns duzen und wie direkt man Kritik äußern darf.
                Beraterin: Das ist realistisch. Dann halten wir fest: In vier Wochen schauen wir gemeinsam, was erreicht ist, und passen das Profil an.
            """.trimIndent(),
            expressions = listOf(
                "Mein Ziel ist es, … zu … – ein Ziel als beobachtbare Handlung formulieren, nicht als Gefühl ('sicherer werden')",
                "Woran erkenne ich, dass …? – ein Erfolgskriterium erfragen",
                "bis zum …, vor …, ohne … – ein Ziel nach Zeit, Publikum und Hilfsmitteln eingrenzen",
                "Das übe ich bewusst (noch) nicht. – Prioritäten setzen und Grenzen des Ziels benennen",
                "ein Ziel formulieren / überprüfen / anpassen / verfehlen / erreichen – typische Verbverbindungen",
                "die eigene Einschätzung mit einem Fremdurteil abgleichen – Selbst- und Fremdperspektive verbinden",
                "Sollen wir uns duzen? Wie direkt darf Kritik hier gemeint sein? – Anrede und Direktheit erfragen; im regionalen Vereinsumfeld lieber nachfragen als raten",
                "Für eine Behörde: 'Welche Unterlagen muss ich zum Termin mitbringen, und bis wann?' – ein Gespräch gezielt vorbereiten"
            ),
            grammar = "Zweckangaben: Bei gleichem Subjekt steht der Infinitivsatz mit 'um … zu': 'Ich übe die Ansage, um sie ohne Manuskript zu halten.' Bei verschiedenen Subjekten steht 'damit': 'Ich erkläre die Übung höchstens zweimal, damit die Jugendspieler sie selbst zeigen können.' Nach 'Mein Ziel ist es' folgt ein Infinitiv mit 'zu' (Komma setzen). Grenzwerte werden mit 'mindestens', 'höchstens' und 'spätestens' präzisiert. 'Woran erkenne ich, ob …' ist eine indirekte Frage mit dem Verb am Ende.",
            question = "Welches Ziel ist am besten überprüfbar?",
            options = listOf(
                "Ich möchte im Review insgesamt sicherer und besser sprechen.",
                "Ich erkläre im Freitagsreview fünf Minuten lang ohne Manuskript, warum eine Messung nur eingeschränkt aussagekräftig ist; anschließend prüfe ich anhand einer Liste, ob ich Befund, offene Punkte und Grenze getrennt habe.",
                "Ich lerne so viele neue Wörter wie möglich.",
                "Ich spreche ab sofort nur noch Deutsch, damit ich irgendwann fließend bin."
            ),
            correctOption = 1,
            explanation = "B nennt Situation, Dauer, Hilfsmittel, Publikum und ein eigenes beobachtbares Selbstcheck-Kriterium. A ist ein Wunsch ohne Kriterium. C nennt weder Situation noch einen Maßstab. D legt eine Methode fest, aber kein Ergebnis, und 'irgendwann fließend' ist nicht terminiert.",
            controlledPrompt = "Formulieren Sie zwei überprüfbare Ziele für die Trainingsansage vor der U13: eines mit 'um … zu' (Ihre Handlung), eines mit 'damit' (Wirkung auf die Jugendspieler). Kriterium: Die Jugendspieler sollen die Übung nach höchstens zwei Erklärungen zeigen können. Ergänzen Sie einen Selbstcheck, bei dem Sie notieren, welche Anweisung noch unklar war.",
            controlledModel = "Ich erkläre die Übung höchstens zweimal, damit die Jugendspieler sie anschließend selbst zeigen können. Um zu prüfen, ob meine Ansage verständlich war, notiere ich nach dem Training die Stelle, an der eine Rückfrage nötig war, und formuliere sie für das nächste Mal genauer.",
            speaking = "Stellen Sie in zwei Minuten ohne Vorlage Ihr Zielprofil vor: drei konkrete Situationen der nächsten Wochen, je ein beobachtbares Kriterium, einen Selbstcheck-Termin und einen Bereich, den Sie bewusst noch nicht üben. Bleiben Sie bei 'um … zu' und 'damit' sprachlich genau.",
            interaction = "A ist Sprachberater/in, B Lernende/r mit der Aussage: 'Ich will einfach fließend sprechen.' A fragt nach Situation, Kriterium und Fremdurteil. Twist: A schlägt Smalltalk im Verein als erstes Ziel vor, obwohl B zuerst das Fahrdynamikreview bewältigen muss. B widerspricht höflich, begründet die Priorität und fragt, wie direkt A Kritik am Zielprofil äußern darf. Tauschen Sie die Rollen.",
            writing = "Schreiben Sie ein Zielprofil von 150–190 Wörtern für die nächsten vier Wochen. Beschreiben Sie drei Situationen (mindestens eine aus Beruf oder Ehrenamt), je ein überprüfbares Kriterium, einen Selbstcheck und wann Sie das Profil überprüfen. Benennen Sie ausdrücklich ein Ziel, das Sie vorerst zurückstellen.",
            transfer = "Persönliche Transferübung (kein geschützter Test): Sie übernehmen ehrenamtlich die Elternvertretung einer Schulklasse und müssen beim ersten Elternabend ein Anliegen vortragen, Rückfragen beantworten und anschließend Aufgaben verteilen. Erstellen Sie ein Zielprofil mit Situation, beobachtbarem Kriterium und Überprüfungstermin. Bearbeiten Sie es nach einer Woche noch einmal ohne Vorlage; markieren Sie selbst eine klare Stärke, eine offene Stelle und eine konkrete nächste Übung. Das ist Lernreflexion, kein C1-Nachweis.",
            feedback = "Modellrückmeldung: Ihre Situationen sind konkret und das Kriterium 'ohne Manuskript, fünf Minuten' ist überprüfbar. 'Ich will sicherer werden' bleibt dagegen offen; ergänzen Sie eine beobachtbare Handlung, an der Sie selbst Ihren Fortschritt prüfen können. Für den Vereinsabend ist es klug, Anrede und Direktheit zu erfragen, statt sie zu erraten. Prüfen Sie noch, ob Ihr Profil nicht zu viele Ziele gleichzeitig verfolgt."
        )
    ),
    CurriculumUnit(
        "s0-diagnose", "Verstehen und Produzieren diagnostizieren", 0, emptyList(),
        listOf(
            "Verstehensleistung und Produktionsleistung getrennt beschreiben, statt nur 'alles gut' oder 'zu schwach' zu sagen.",
            "Eine Selbsteinschätzung mit den tatsächlich vorliegenden Belegen vergleichen und Aussagegrenzen benennen.",
            "Aus der Diagnose ein enges, überprüfbares Übungsziel ableiten."
        ),
        contextsA(
            "Radiobeitrag zusammenfassen",
            "Eine Einladung mit 'Komm halt vorbei' nach Ton und Beziehung deuten",
            "Einen Hybrid-TCU-Fehlerbericht verstehen und erläutern",
            "Eine U15-Taktikansage verstehen und eine Elternfrage zur Spielzeit paraphrasieren"
        ),
        UnitContent(
            reading = """
                Marek hört im Deutschkurs einen vierminütigen Radiobeitrag (Übungstext) über den Ausbau von Radwegen in einer Mittelstadt und fasst ihn anschließend schriftlich zusammen. Seine Kursleiterin wertet beides getrennt aus. Zum Verstehen: Die Hauptaussage – der Ausbau erfolge schrittweise und werde von Anwohnern teils kritisiert – hat er richtig erfasst. Zwei Zahlen hat er allerdings vertauscht, und die ironische Bemerkung des Moderators am Ende ist ihm entgangen. Zur Produktion: Seine Zusammenfassung ist gut verständlich, enthält aber dreimal einen Nebensatz, in dem das konjugierte Verb nicht am Ende steht. Außerdem beginnt sie mit Nebensächlichem; die Kernaussage folgt erst im fünften Satz. In seiner Selbsteinschätzung schreibt Marek: „Alles gut, ich habe fast alles verstanden.“ Seine Kollegin Ines, die den Text unabhängig liest, sagt, sie habe die Zusammenfassung ohne Mühe verstanden, die Kernaussage aber erst spät gefunden. Die Kursleiterin zieht daraus den Schluss: Verstehen und Produzieren sind unterschiedlich weit entwickelt, und Mareks Selbsteinschätzung deckt sich nur teilweise mit den Fremdurteilen. Als Übungsziel für zwei Wochen legt man fest: Die Kernaussage steht in den ersten zwei Sätzen, danach folgen höchstens drei Details, und die Verbstellung im Nebensatz wird vor der Abgabe gezielt kontrolliert.
            """.trimIndent(),
            listening = """
                Kursleiterin: Marek, Sie haben geschrieben: „Alles gut.“ Was meinen Sie damit genau – das Verstehen oder das Schreiben?
                Marek: Ehrlich gesagt beides. Ich hab doch fast alles verstanden.
                Kursleiterin: Beim Verstehen stimmt das im Kern. Die zwei Zahlen waren aber vertauscht. Haben Sie das bemerkt?
                Marek: Nein, das ist mir nicht aufgefallen. Ich dachte, ich hätte sie richtig notiert.
                Ines: Darf ich was ergänzen? Dein Text war gut zu lesen. Nur musste ich bis Satz fünf warten, bis ich wusste, worum es geht.
                Marek: Ach so. Ich wollte erst den Hintergrund erklären.
                Kursleiterin: Das ist nachvollziehbar, aber Leser suchen zuerst die Kernaussage. Und zu den Nebensätzen: Lesen Sie mal Ihren dritten Satz laut vor.
                Marek: „Der Moderator sagt, dass die Stadt hat mehr Geld bekommen.“ Oh – das Verb gehört ans Ende.
                Kursleiterin: Genau. Sie haben den Fehler selbst gefunden, sobald Sie langsam gelesen haben. Das ist ein gutes Zeichen: Das Wissen ist da, es greift nur im Schreibtempo noch nicht.
                Marek: Dann sollte ich also nicht mehr üben, wie ich etwas verstehe, sondern wie ich es zusammenfasse?
                Kursleiterin: Beides ein bisschen, aber der Schwerpunkt liegt auf der Zusammenfassung. Die Ironie am Ende des Beitrags schauen wir uns nächste Woche gemeinsam an.
                Ines: Und ich lese deine nächste Fassung wieder, ohne vorher zu wissen, was drinsteht.
            """.trimIndent(),
            expressions = listOf(
                "Die Hauptaussage habe ich verstanden, die Details nicht sicher. – Verstehensleistung differenziert beschreiben",
                "Beim Schreiben/Sprechen fällt mir auf, dass … – Produktionsleistung getrennt davon beschreiben",
                "Meine Einschätzung deckt sich mit / weicht ab von … – Selbst- und Fremdurteil vergleichen",
                "Laut Ines sei die Zusammenfassung verständlich gewesen. – ein Fremdurteil im Konjunktiv I wiedergeben",
                "Das ist mir entgangen / nicht aufgefallen. – eine Verstehenslücke benennen, ohne sie zu überspielen",
                "eine Leistung einschätzen / beurteilen / bestätigen; ein Fremdurteil einholen – typische Verbverbindungen",
                "'Komm halt vorbei!' – 'halt' kann die Einladung beiläufig oder als selbstverständlich klingen lassen; ob sie locker oder drängend wirkt und wie verbindlich sie ist, hängt von Ton und Beziehung ab. Nachfrage: 'Wann passt es dir denn?'",
                "Ein Fehlerspeichereintrag ist ein Befund, noch keine Ursache. – Bericht und Deutung auseinanderhalten"
            ),
            grammar = "Konjunktiv I in der Wiedergabe von Fremdurteilen: 'Ines sagt, die Zusammenfassung sei verständlich gewesen. Sie habe die Kernaussage erst spät gefunden.' Wenn die Konjunktiv-I-Form mit dem Indikativ identisch wäre, weicht man auf Konjunktiv II aus: 'Die Zahlen hätten nicht gestimmt' (nicht: 'haben'). 'Zwar … aber …' trennt Teilleistungen: 'Zwar versteht er die Hauptaussage, aber er fasst sie noch nicht klar zusammen.' 'Obwohl' leitet einen Nebensatz mit Verb am Ende ein.",
            question = "Welche Schlussfolgerung ist durch den Bericht gedeckt?",
            options = listOf(
                "Marek versteht Radiobeiträge insgesamt schlecht, weil er beim Schreiben Fehler macht.",
                "Das Verstehen der Hauptaussage ist tragfähig; die Produktion braucht eine klarere Gliederung und eine sichere Nebensatzstellung. Mareks 'Alles gut' deckt sich nur teilweise mit den Fremdurteilen.",
                "Weil Ines die Zusammenfassung gut verstanden hat, besteht kein Übungsbedarf.",
                "Die vertauschten Zahlen beweisen, dass Marek die Hauptaussage nicht verstanden hat."
            ),
            correctOption = 1,
            explanation = "B trennt Verstehen und Produzieren und ordnet das Fremdurteil richtig ein. A schließt fälschlich von Schreibfehlern auf mangelndes Verstehen. C verwechselt 'verständlich' mit 'wirksam strukturiert'; Ines nennt selbst die späte Kernaussage. D verallgemeinert: Die Hauptaussage war nachweislich richtig erfasst, nur Details waren vertauscht.",
            controlledPrompt = "Geben Sie das Urteil von Ines und der Kursleiterin im Konjunktiv I wieder (Ines: Text gut lesbar, Kernaussage spät; Kursleiterin: Verb im Nebensatz dreimal falsch gestellt, Hauptaussage richtig). Benutzen Sie bei 'haben' im Plural den Konjunktiv II und ein 'zwar … aber'.",
            controlledModel = "Ines sagt, der Text sei gut lesbar gewesen, sie habe die Kernaussage aber erst spät gefunden. Die Kursleiterin meint, Marek habe die Hauptaussage richtig erfasst; zwar seien die Zahlen vertauscht worden, aber vor allem hätten die Nebensätze dreimal die falsche Verbstellung gehabt.",
            speaking = "Hören oder lesen Sie eine kurze Taktikansage (zum Beispiel: 'Wir pressen heute früh, nach Ballgewinn spielen wir sofort in die Tiefe, und die Viererkette schiebt nach.'). Geben Sie in 90 Sekunden getrennt wieder, was Sie sicher verstanden haben, was Sie nicht sicher verstanden haben und was Sie bei der Wiedergabe sprachlich noch unsicher macht.",
            interaction = "A ist Elternteil und fragt: 'Warum spielt mein Sohn in der U15 nur zwanzig Minuten?' B ist Trainer/in und antwortet in zwei Sätzen mit sportlichen Kriterien. A paraphrasiert die Antwort im Konjunktiv I ('Sie sagen, er habe …'). Twist: A paraphrasiert absichtlich ein Detail falsch; B bemerkt die Abweichung und korrigiert freundlich. Danach schätzen beide ein, ob das Verstehen oder die Wiedergabe das Problem war.",
            writing = "Schreiben Sie einen Diagnosebericht von 140–180 Wörtern über Ihr Verstehen und Ihre Wiedergabe eines kurzen Hörtexts (Radiobeitrag oder Taktikansage). Trennen Sie Verstehen und Produktion, geben Sie Ines' fiktives Urteil aus dem Beispiel im Konjunktiv I wieder und leiten Sie ein Übungsziel für zwei Wochen ab. Kennzeichnen Sie, was Sie selbst prüfen können und was eine Textaufgabe nicht zeigt.",
            transfer = "Persönliche Transferübung: Ein fiktiver Prüfbericht hält fest, dass die TCU bei drei von elf Wiederholungen eines Schaltszenarios einen Fehlerspeichereintrag angelegt hat; auf der ECU-Seite fehlt ein Eintrag, die Ursache wurde nicht untersucht. Ein Kollege fasst zusammen: 'Die TCU ist kaputt.' Trennen Sie schriftlich, was der Bericht belegt, was offenbleibt und wie Sie es erklären würden. Prüfen Sie danach: Haben Sie Befund und Ursache getrennt, Details nicht erfunden und eine passende Rückfrage formuliert? Wiederholen Sie die Übung nach einer Woche mit einem anderen selbst gewählten Thema. Dies ist Übung, keine unabhängige Bewertung.",
            feedback = "Modellrückmeldung: Sie trennen Verstehen und Wiedergabe sauber und nennen konkrete Stellen. 'Alles gut' ist dagegen kein Befund; besser: 'Ich habe die Hauptaussage verstanden, bei den Zahlen war ich unsicher.' Beim Konjunktiv I achten Sie auf 'haben' im Plural, dort ist 'hätten' nötig. Ihre Deutung des Fehlerspeichereintrags bleibt vorsichtig: Ein Befund ist noch keine Ursache. Wiederholen Sie den Selbstcheck nach zwei Wochen mit einem neuen Text; eine einzelne eigene Einschätzung ist keine validierte Diagnose."
        )
    ),
    CurriculumUnit(
        "s0-repair", "Verständnisprobleme sichtbar machen", 1, emptyList(),
        listOf(
            "Gezielt nachfragen, sodass genau die unklare Stelle sichtbar wird (statt allgemein 'Wie bitte?').",
            "Das eigene Verständnis zur Bestätigung vorlegen und Verständnis anderer in eigenen Worten prüfen.",
            "Eine eigene missverständliche Aussage bemerken und selbst reparieren."
        ),
        contextsA(
            "Vertragsbedingung erfragen",
            "'Passt scho' im schwäbischen Gespräch nicht als sichere Zusage werten, sondern freundlich nachfragen",
            "Eine ECU/TCU-Drehmomentanforderung rückversichern",
            "Eine U11-Passübung von einem Jugendspieler in eigenen Worten wiedergeben lassen"
        ),
        UnitContent(
            reading = """
                In einem Hybrid-Projekt schreibt Paul an seine Kollegin Selin: „Die TCU soll beim Hochschalten das Moment ein bisschen zurücknehmen.“ Selin könnte einfach „Alles klar“ antworten. Stattdessen fragt sie gezielt: „Meinst du das Motormoment an der ECU-Schnittstelle oder das Moment am Getriebeeingang? Und ist ‚ein bisschen‘ ein fester Wert in Newtonmetern oder ein Anteil des Fahrerwunschmoments?“ Erst diese präzise Frage macht das Missverständnis sichtbar: Paul meinte einen Momenteingriff, den die TCU während des Schaltvorgangs anfordert, damit die ECU das Motormoment kurzzeitig absenkt. Selin fasst zusammen: „Habe ich richtig verstanden, dass die TCU beim Hochschalten eine Momentenreduzierung anfordert, die ECU sie umsetzt und das tatsächlich gestellte Moment zurückmeldet?“ Paul bestätigt den Ablauf und repariert seine eigene Aussage: „Ich hatte ‚zurücknehmen‘ unscharf benutzt. Genauer gesagt: Die ECU soll das Moment für die Dauer des Eingriffs absenken; die Größe steht in der Schnittstellenspezifikation.“ Beide halten fest, dass diese Klärung nur die Anforderung beschreibt und keinen Test oder keine Freigabe ersetzt. Der Gewinn liegt in der Gesprächstechnik: Die Frage nennt Alternativen, die Zusammenfassung macht das eigene Verständnis prüfbar, und die Korrektur stellt klar, was gemeint war.
            """.trimIndent(),
            listening = """
                Trainer: So, Leute, ich erklär's nur einmal, dann seid ihr dran. Der Torwart eröffnet flach auf den Außenverteidiger, der spielt diagonal zum Sechser, und der legt wieder zurück auf den Torwart. Danach wechselt ihr die Positionen.
                Trainer: Okay. Lena, sag du mal in deinen Worten, wie du das verstanden hast.
                Lena: Äh, also … der Torwart wirft den Ball zum Außenverteidiger?
                Trainer: Fast. Wirft oder spielt? Da hab ich mich vielleicht unklar ausgedrückt. Ich meinte: flach passen, mit dem Fuß.
                Lena: Ach so, flach mit dem Fuß. Und dann geht der Ball diagonal zum Sechser?
                Trainer: Genau. Und danach?
                Lena: Zurück zum Torwart. Aber wer wechselt dann?
                Trainer: Gute Frage, das hab ich noch nicht gesagt. Ben, was meinst du?
                Ben: Ich glaub, alle rücken eine Position weiter. Im Uhrzeigersinn, oder?
                Trainer: Richtig. Und merkt euch: Wenn etwas unklar ist, fragt nach. Das ist keine Schwäche, das hilft allen.
                Lena: Und wenn ich im Spiel was nicht verstehe, darf ich dann auch einfach rufen: Moment, wohin?
                Trainer: Ja klar. Am besten fragt ihr konkret: Soll ich zum Außen passen oder in die Mitte?
            """.trimIndent(),
            expressions = listOf(
                "Meinst du … oder …? – eine Alternative nennen, statt allgemein 'Wie bitte?' zu sagen",
                "Habe ich richtig verstanden, dass …? – das eigene Verständnis zur Bestätigung vorlegen",
                "Was genau verstehst du unter …? / Woran machst du … fest? – einen Begriff eingrenzen",
                "Ich meinte eigentlich … / Anders gesagt … / Genauer gesagt … – die eigene Aussage reparieren",
                "ein Missverständnis ausräumen / aufklären; eine Aussage präzisieren / konkretisieren – typische Verbverbindungen",
                "Könntest du mir das kurz in eigenen Worten wiedergeben? – Verständnis prüfen, ohne zu prüfen wirken",
                "'Ab wann gilt die Kündigungsfrist, und in welcher Form muss ich sie einreichen?' – eine Vertragsbedingung konkret erfragen",
                "'Passt scho' (regional: süddeutsch, auch schwäbisch) – kann zustimmen, beruhigen oder abwiegeln. Freundlich nachfragen: 'Heißt das, dass wir es so machen?'"
            ),
            grammar = "Indirekte Fragen mit 'ob' und W-Wort (Verb am Ende): 'Ich möchte wissen, ob die Größe absolut oder relativ gemeint ist.' Rückversicherung mit 'Habe ich richtig verstanden, dass …?' (dass-Satz, Verb am Ende). Reparatur mit Vorvergangenheit: 'Ich hatte ‚zurücknehmen‘ unscharf benutzt; gemeint war eine Absenkung.' Höfliche Bitte im Konjunktiv II: 'Könntest du das kurz zusammenfassen?' Das Wort 'bisschen' ist unscharf; nach einer Menge fragt man mit 'Wie viel genau …?'.",
            question = "Welche Nachfrage macht die Unklarheit in Pauls Aussage am gezieltesten sichtbar?",
            options = listOf(
                "Wie bitte?",
                "Meinst du das Motormoment an der ECU-Schnittstelle oder das Moment am Getriebeeingang, und ist 'ein bisschen' ein fester Wert oder ein Anteil?",
                "Kannst du mir bitte alles noch einmal von Anfang an erklären?",
                "Das ist doch klar, oder?"
            ),
            correctOption = 1,
            explanation = "B nennt zwei konkrete Bezugsgrößen und fragt nach der Art der Angabe; damit wird genau die mehrdeutige Stelle sichtbar. A zeigt nur, dass etwas unklar ist, nicht was. C verlangt eine vollständige Wiederholung statt gezielter Klärung. D fragt Zustimmung ab und verdeckt das Verständnisproblem eher.",
            controlledPrompt = "Formulieren Sie drei Sätze: eine Rückversicherung mit 'Habe ich richtig verstanden, dass …' (TCU fordert Momentenreduzierung an, ECU setzt um), eine gezielte Frage mit 'ob' (absolut oder relativ) und eine Selbstreparatur mit 'Ich hatte … gesagt, gemeint war …'.",
            controlledModel = "Habe ich richtig verstanden, dass die TCU beim Hochschalten eine Momentenreduzierung anfordert und die ECU sie umsetzt? Ich würde gern wissen, ob die Größe absolut in Newtonmetern oder als Anteil des Fahrerwunschmoments gemeint ist. Ich hatte ‚zurücknehmen‘ gesagt, gemeint war eine kurzzeitige Absenkung während des Schaltvorgangs.",
            speaking = "Sie haben von einem Vermieter oder Fitnessstudio die Aussage gehört: 'Das mit der Kündigung geht schon in Ordnung, passt scho.' Fragen Sie in 90 Sekunden nach, was genau gilt: Frist, Form, Beginn der Frist, Bestätigung. Geben Sie anschließend die Antwort mit 'Habe ich richtig verstanden, dass …' wieder.",
            interaction = "A erklärt B eine Passübung für die U11 in drei Sätzen, enthält absichtlich ein unscharfes Wort ('Dann spielst du ihn halt weiter'). B fragt gezielt nach, gibt die Übung in eigenen Worten wieder und lässt sich bestätigen. Twist: A merkt selbst, dass 'weiterspielen' unklar war, und repariert die Aussage mit 'Genauer gesagt …', bevor B fertig ist. Tauschen Sie die Rollen.",
            writing = "Schreiben Sie eine Nachricht von 140–180 Wörtern an eine Kollegin, in der Sie eine unklare Drehmomentanforderung zwischen ECU und TCU rückversichern: Fassen Sie Ihr Verständnis zusammen, stellen Sie zwei gezielte Fragen, benennen Sie, was noch ungeklärt ist, und schreiben Sie ausdrücklich, dass die Klärung keine Freigabe ersetzt. Erfinden Sie keine Zahlenwerte.",
            transfer = "Persönliche Transferübung: Sie beantragen als Vereinsmitglied eine Erstattung. Am Telefon heißt es: 'Des passt scho, schicken Sie halt die Unterlagen.' Sie wissen nicht, welche Unterlagen, bis wann und ob das eine Zusage ist. Formulieren Sie Ihre Rückfragen und eine abschließende Zusammenfassung. Prüfen Sie, ob Sie Unterlagen, Frist und Verbindlichkeit geklärt haben, ohne aus der freundlichen Wendung eine Zusage abzuleiten.",
            feedback = "Modellrückmeldung: Ihre Frage 'Meinst du A oder B?' macht die unklare Stelle sichtbar, und die Zusammenfassung ist als Bestätigungsfrage formuliert. 'Habt ihr das verstanden?' würde dagegen nur ein 'Ja' erzeugen; lassen Sie lieber wiedergeben. Bei 'Passt scho' haben Sie freundlich nachgefragt, ohne die Person zu verunsichern. Achten Sie darauf, dass die Rückversicherung bei technischen Anforderungen nicht mit einem Freigabeschritt verwechselt wird."
        )
    ),
    CurriculumUnit(
        "s0-retrieve", "Abrufen statt Wiederlesen", 1, emptyList(),
        listOf(
            "Gelerntes zuerst frei aus dem Gedächtnis erzeugen und erst danach mit der Vorlage vergleichen.",
            "Wiederkehrende Fehler in einem Fehlerprotokoll sammeln und nach 1, 3 und 7 Tagen ohne Vorlage neu bearbeiten.",
            "Eine Bitte mit 'Könntest du mal …?' freundlich und ohne drängenden Ton formulieren."
        ),
        contextsA(
            "Eine Nachricht frei wiedergeben",
            "'Könntest du mal …?' freundlich ohne drängenden Ton neu einsetzen",
            "Den Ablauf einer EV-FMEA ohne Vorlage erläutern",
            "Eine U13-Aufwärmübung ohne Vorlage erklären und einen früheren Ansagefehler korrigieren"
        ),
        UnitContent(
            reading = """
                Selim trainiert eine U13 und möchte seine Ansagen verbessern. Bisher hat er die Übungsbeschreibung vor jedem Training mehrmals gelesen und markiert; trotzdem blieb er auf dem Platz oft stecken. Nun arbeitet er anders: Er klappt die Beschreibung des Rondos – eines Positionsspiels, bei dem mehrere Spieler im Kreis den Ball gegen ein bis zwei Spieler in der Mitte halten – zu, nimmt ein leeres Blatt und schreibt auf, was er behalten hat: Aufbau, Regeln, Coachingpunkte und Abbruchregel. Erst danach vergleicht er seine Version mit der Vorlage und markiert, was gefehlt hat oder falsch war. Diese Lücken sind sein Übungsmaterial. Zusätzlich führt er ein Fehlerprotokoll. Zwei Fehler tauchen wiederholt auf: Erstens stellt er das Verb im dass-Satz falsch: „Ich habe gesagt, dass ihr müsst den Abstand halten.“ Zweitens verwechselt er den Artikel: „die Rondo“ statt „das Rondo“. Beide Fehler bearbeitet er nach einem Tag, nach drei Tagen und nach einer Woche, indem er neue Sätze ohne Vorlage bildet. Zum Schluss prüft er, ob sich der Fehler im freien Sprechen vor der Gruppe noch einmal einschleicht, denn nur dort zeigt sich, ob die Korrektur wirklich verfügbar ist. Wiederlesen erzeugt schnell ein Gefühl von Vertrautheit; ob man etwas selbst hervorbringen kann, zeigt es nicht.
            """.trimIndent(),
            listening = """
                Marie: Okay, Selim, Beschreibung zu, Blatt weg. Erklär mir das Kleinfeldspiel fünf gegen fünf von vorne.
                Selim: Gut. Also, erst das Feld: ungefähr dreißig mal zwanzig Meter, zwei kleine Tore. Dann teile ich zwei Teams ein.
                Marie: Und die Regeln?
                Selim: Ich habe gesagt, dass jeder muss einmal den Ball berühren, bevor man aufs Tor schießen darf. Äh …
                Marie: Hörst du's? Das ist genau der Fehler aus deinem Protokoll.
                Selim: Ja! Das Verb gehört ans Ende: „… dass jeder den Ball einmal berühren muss.“
                Marie: Super, das hast du selbst gemerkt. Das ist wichtig. Was fehlt noch?
                Selim: Die Coachingpunkte … Moment. Körperhaltung offen, Schulterblick, Pass in den Lauf.
                Marie: Gut. Und die Abbruchregel?
                Selim: Hm. Da bin ich komplett leer. Das schreib ich mir auf und übe es übermorgen noch mal.
                Marie: Könntest du mir übermorgen die Abbruchregel noch mal erklären? Ohne Zettel, bitte.
                Selim: Klar. Das ist wahrscheinlich besser, als wenn ich die Beschreibung heute zehnmal abschreibe, oder?
                Marie: Auf jeden Fall. Dann sehen wir, was wirklich hängen geblieben ist.
            """.trimIndent(),
            expressions = listOf(
                "ohne Vorlage / aus dem Gedächtnis erklären – freie Wiedergabe als Übungsform benennen",
                "Das habe ich behalten, das fehlt mir noch. – Lücken sichtbar machen",
                "einen Fehler protokollieren / bearbeiten / korrigieren; eine Korrektur überprüfen – typische Verbverbindungen",
                "nach einem Tag, nach drei Tagen, nach einer Woche – zeitversetzte Wiederholung planen",
                "Das Rondo, das Kleinfeldspiel, das Pressing, der Ballbesitz, die Viererkette – Artikel aus dem Fußball sicher abrufen",
                "Könntest du mal kurz zeigen, wie das geht? – eine Bitte mit 'mal' abmildern; Ton und Beziehung entscheiden, ob es freundlich oder drängend wirkt",
                "Erst die Phasen der FMEA aufzählen, dann Schwere, Auftreten, Entdeckung erklären – einen Ablauf ohne Vorlage gliedern",
                "Das hätte ich beim letzten Mal anders sagen sollen. – einen früheren Fehler benennen, ohne sich abzuwerten"
            ),
            grammar = "Im dass-Satz steht das konjugierte Verb am Ende: 'Ich habe gesagt, dass ihr den Abstand halten müsst.' (nicht: 'dass ihr müsst den Abstand halten'). Bei Modalverben steht das Modalverb als letztes Element. Artikel von Fachwörtern lernt man mit dem Nomen: das Rondo, das Kleinfeldspiel, das Pressing, die Viererkette, der Ballbesitz. 'Könntest du mal …?' ist eine Bitte im Konjunktiv II; 'mal' schwächt ab, ersetzt aber keine freundliche Betonung. Zeitliche Staffelung: 'nach einem Tag', 'danach', 'schließlich'.",
            question = "Welche Handlung ist ein Abruf aus dem Gedächtnis und keine reine Wiederholung?",
            options = listOf(
                "Die Beschreibung noch einmal lesen und die wichtigen Stellen markieren.",
                "Die Beschreibung schließen, den Ablauf aufschreiben oder erklären und erst danach mit der Vorlage vergleichen.",
                "Die Beschreibung sorgfältig abschreiben.",
                "Die Beschreibung beim Joggen als Audiodatei mehrmals anhören."
            ),
            correctOption = 1,
            explanation = "B verlangt, dass der Ablauf zuerst selbst hervorgebracht wird; der Vergleich danach zeigt Lücken. A, C und D bieten Information wieder an, ohne dass man sie vorher selbst erzeugen muss.",
            controlledPrompt = "Bilden Sie drei korrekte Sätze aus dem Fehlerprotokoll: einen dass-Satz mit 'müssen' (Abstand halten), einen Satz mit dem richtigen Artikel für Rondo und eine freundliche Bitte mit 'Könntest du mal …' (Abbruchregel erklären).",
            controlledModel = "Ich habe gesagt, dass ihr den Abstand halten müsst. Das Rondo beginnt als Vier-gegen-zwei im Kreis. Könntest du mal kurz erklären, wie die Abbruchregel funktioniert?",
            speaking = "Erklären Sie ohne Vorlage in zwei Minuten eine U13-Aufwärmübung (zum Beispiel ein Rondo oder ein Kleinfeldspiel): Aufbau, Regeln, Coachingpunkt, Abbruchregel. Bemerken Sie einen Verbstellungsfehler im dass-Satz, korrigieren Sie sich selbst, ohne die Erklärung abzubrechen.",
            interaction = "A ist Co-Trainer/in und bittet B mit 'Könntest du mal …?', ohne Vorlage die Aufwärmübung zu erklären. B macht absichtlich den bekannten Fehler im dass-Satz. Twist: A hilft nicht sofort, sondern wartet, ob B den Fehler selbst bemerkt; wenn nicht, fragt A freundlich nach. Danach tragen beide den Fehler ins Protokoll ein und vereinbaren den nächsten Abruftermin. Tauschen Sie die Rollen.",
            writing = "Schreiben Sie aus dem Gedächtnis, ohne Ihre Unterlagen anzusehen, eine Nachricht von 140–180 Wörtern an Ihr Trainerteam: Erklären Sie eine Aufwärmübung und fügen Sie einen Abschnitt hinzu, in dem Sie zwei frühere Ansagefehler benennen und beschreiben, wie Sie sie nach einem, drei und sieben Tagen wiederholen. Vergleichen Sie danach mit Ihren Unterlagen und markieren Sie Lücken.",
            transfer = "Persönliche Transferübung: Erklären Sie ohne Vorlage den Ablauf einer EV-FMEA für neue Kolleginnen: Planung, Struktur-, Funktions-, Fehler- und Risikoanalyse mit Schwere, Auftreten und Entdeckung, Optimierung und Dokumentation. Sagen Sie ausdrücklich, dass eine Aktionspriorität weder Freigabe noch Sicherheitsnachweis ersetzt. Vergleichen Sie Ihre Erklärung anschließend mit der Lesepassage und notieren Sie zwei Auslassungen oder unklare Stellen. Dies prüft den Abruf, nicht Ihre fachliche Qualifikation oder C1.",
            feedback = "Modellrückmeldung: Sie haben den Ablauf zuerst frei erklärt und erst dann verglichen; das ist der entscheidende Schritt. Ihre Korrektur des dass-Satzes kam selbstständig, das spricht für Verfügbarkeit des Wissens. Bei der Abbruchregel blieben Sie leer: Tragen Sie die Lücke ins Protokoll ein und fragen Sie sie erneut in drei Tagen ab. 'Könntest du mal …' war freundlich; achten Sie auf eine entspannte Betonung, damit es nicht wie ein Befehl klingt."
        )
    ),
    CurriculumUnit(
        "s2-data", "Daten und Einschränkungen verstehen", 2, emptyList(),
        listOf(
            "Befund, Stichprobe, Messunsicherheit und Schlussfolgerung in anspruchsvollen Berichten trennen.",
            "Erkennen, welche Ursachenbehauptung durch die Daten nicht gedeckt ist, und einen Prüfschritt benennen.",
            "Einschränkungen präzise sprachlich ausdrücken (Partizipialattribute, einschränkende Konnektoren)."
        ),
        contextsA(
            "Eine Umfrage samt Einschränkungen verstehen",
            "Mitgliederzahlen verstehen, ohne 'bloß ein paar' mit einer genauen Anzahl gleichzusetzen",
            "Einen EV-Bericht zu Querbeschleunigung und Messunsicherheit verstehen",
            "Einen U15-Spielbericht mit Passquoten lesen und Grenzen der Aussage erkennen"
        ),
        UnitContent(
            reading = """
                Passquote steigt – das Training wirkt? Ein Beispiel aus der Jugendförderung

                (Erfundener Übungstext: Verein, Liga und Zahlen sind fiktiv.)

                Der SV Rotbachtal hat seine U15 in der vergangenen Hinrunde systematisch beobachten lassen, und die Zahlen scheinen eindeutig: Von der vierten bis zur sechsten Runde lag die Passquote im Spielaufbau bei 84 Prozent, in den drei Spielen davor bei 79 Prozent. Der Jugendleiter führt den Anstieg auf das im Oktober eingeführte Rondo-Training zurück und kündigt an, das Konzept auf alle Altersklassen auszuweiten.

                Bei genauerem Hinsehen relativiert sich das Bild. Erstens beruht die Quote auf nur sechs Spielen; schon ein einzelnes Spiel gegen einen außergewöhnlich schwachen oder starken Gegner kann den Mittelwert merklich verschieben. Zweitens wurden die Pässe von zwei ehrenamtlichen Betreuern per Video von Hand erfasst; wie weit ihre Zählungen übereinstimmen, wurde nicht dokumentiert, und ein Passversuch im Gewühl lässt sich nicht immer eindeutig als angekommen werten. Drittens trafen die Rotbachtaler in den letzten drei Spielen auf Mannschaften der unteren Tabellenhälfte, die deutlich seltener früh pressten. Wer weniger unter Druck steht, spielt vermutlich genauere Pässe – unabhängig vom Training. Hinzu kommt, dass ein Teil der Mannschaft in diesen Wochen erstmals mit Viererkette statt mit Dreierkette spielte; auch das verändert, wo und unter welchem Druck der Ball gespielt wird und wie lange die Mannschaft im Ballbesitz bleibt.

                Aus den Daten folgt daher lediglich, dass die Quote in diesem Zeitraum höher lag. Dass das Rondo-Training dafür ursächlich war, ist möglich, aber nicht belegt. Besser prüfen ließe sich die Hypothese durch einen vorab geplanten Vergleich mit einer möglichst ähnlichen Mannschaft ohne Rondo-Training oder durch wiederholte Messungen vor und nach der Einführung, bei denen Gegnerstärke, Spielort und Erfassungsmethode berücksichtigt werden. Auch dann müsste die Unsicherheit benannt werden; ein einfacher Saisonvergleich beweist für sich allein keine Ursache. Zudem sagt eine hohe Passquote nichts darüber aus, ob die Pässe nach vorn führten: Wer überwiegend quer spielt, kann hohe Quoten erzielen, ohne Torchancen vorzubereiten. Der Jugendleiter kann die Maßnahme als Hypothese fortführen, sollte sie aber nicht als bewiesene Lösung darstellen.
            """.trimIndent(),
            listening = """
                Fiktive Beispielzahlen für die Übung.
                Brandt: So, dann zum Fahrdynamikergebnis. Frau Kaya, Variante B ist stabiler, oder?
                Kaya: Na ja, ich würd's so sagen: Im Mittel haben wir bei B 0,82 g Querbeschleunigung gemessen, bei A 0,80 g. Das steht so im Bericht.
                Brandt: Also ist B besser. Das reicht mir eigentlich schon.
                Kaya: Moment. Die Messunsicherheit des Sensors liegt bei plus minus 0,03 g.
                Tom: Und die gilt nur für den Sensor. Dazu kommt, dass wir A und B an verschiedenen Tagen gefahren sind. Asphalttemperatur, Reifenzustand – das hab ich nicht sauber dokumentiert.
                Brandt: Aber die Zahl ist doch klar höher.
                Kaya: Zahlenmäßig ja, um 0,02 g. Nur liegt dieser Unterschied innerhalb der Unsicherheit. Heißt: Wir können nicht sagen, dass B tatsächlich mehr erreicht. Es könnte genauso gut Messrauschen sein.
                Brandt: Und wie viele Fahrten waren das?
                Tom: Pro Variante fünf Durchgänge. Da streut's schon: Einzelne Durchgänge von A lagen höher als einzelne von B.
                Kaya: Das spricht zusätzlich gegen eine klare Aussage. Außerdem ist Querbeschleunigung nur eine Größe. Ob die Rekuperation bei niedriger Haftung das Fahrzeug stabil hält, sagt sie allein nicht.
                Brandt: Okay, was schlagt ihr vor? Ich muss ans Management was Konkretes melden.
                Tom: Wiederholen, am selben Tag, abwechselnd A, B, A, B, mit dokumentierten Randbedingungen.
                Brandt: Wie lange dauert das?
                Tom: Zwei Messtage, wenn das Wetter mitspielt.
                Kaya: Und bitte nicht melden, dass B besser ist, sondern: im bisherigen Messumfang kein belastbarer Unterschied.
                Brandt: Gut. Dann schreiben wir: nicht entscheidbar, Wiederholung geplant.
                Kaya: Genau. Und die Freigabefrage bleibt davon völlig unberührt; die hängt an den Safety-Nachweisen.
                Brandt: Klar. Dann hake ich das für heute ab.
            """.trimIndent(),
            expressions = listOf(
                "Die Quote lag in diesem Zeitraum höher, was aber nicht belegt, dass … – Befund und Ursache trennen",
                "Der Unterschied liegt innerhalb der Messunsicherheit. – einen Unterschied als nicht belastbar einordnen",
                "auf einer kleinen Stichprobe beruhen; eine Aussage relativieren / einschränken – typische Verbverbindungen",
                "Daraus lässt sich nicht ableiten, dass … – eine nicht gedeckte Schlussfolgerung zurückweisen",
                "Belegen ließe sich das etwa durch … – einen Prüfschritt konjunktivisch formulieren",
                "eine Hypothese aufstellen, überprüfen, bestätigen, verwerfen – Fachverben für den Umgang mit Annahmen",
                "'Bloß ein paar Mitglieder haben abgesagt' – 'bloß ein paar' ist keine genaue Anzahl; nachfragen: 'Wie viele genau, und von wie vielen insgesamt?'",
                "Befragt wurden …, die Ergebnisse sind nicht repräsentativ für … – eine Umfrage mit Einschränkung zusammenfassen",
                "die Passquote im Spielaufbau, im Ballbesitz, nach Gegenpressing – Kennzahlen immer mit ihrem Bezug nennen"
            ),
            grammar = "Partizipialattribute verdichten Einschränkungen: 'die in sechs Spielen erhobene Passquote', 'die von Hand erfassten Pässe'. Einschränkende Konnektoren: 'allerdings', 'zumal' (zusätzliches Argument), 'sofern' (Bedingung), 'wobei' (Nebeninformation), 'während' (Gegensatz). Passiv mit Modalverb: 'Der Unterschied ist nicht als Beleg zu werten' (sein + zu + Infinitiv). Konjunktiv II für Prüfbarkeit: 'Das ließe sich durch einen Vergleich belegen.' 'Dürfte' markiert eine begründete Vermutung: 'Der Gegnerdruck dürfte eine Rolle gespielt haben.' 'Lässt sich nicht ableiten' ist stärker als 'ist unklar'.",
            question = "Welche Aussage ist durch den Text gedeckt?",
            options = listOf(
                "Das Rondo-Training hat die Passquote nachweislich um fünf Prozentpunkte erhöht.",
                "In den ausgewerteten Spielen lag die Quote höher; ob das Training ursächlich war, bleibt offen, weil Gegnerstärke, Systemwechsel und kleine Stichprobe mitwirken können.",
                "Weil die Pässe von Hand erfasst wurden, sind die Daten wertlos.",
                "Eine hohe Passquote belegt, dass die Mannschaft mehr Torchancen vorbereitet."
            ),
            correctOption = 1,
            explanation = "B trennt Befund und Ursachenbehauptung und nennt die im Text genannten Einschränkungen. A macht aus einer Differenz von fünf Prozentpunkten einen Kausalbeleg, den der Text ausdrücklich verneint. C verallgemeinert: Der Text nennt die Handerfassung als Einschränkung, nicht als Wertlosigkeit. D behauptet genau das, was der Text als Fehlschluss beschreibt (quer spielen erzeugt hohe Quoten ohne Torchancen).",
            controlledPrompt = "Fassen Sie in vier Sätzen zusammen: Befund (mit Partizipialattribut: in sechs Spielen erhobene Quote), Einschränkung (allerdings oder zumal), Folgerung (lässt sich nicht ableiten) und Prüfschritt (sofern, Konjunktiv II).",
            controlledModel = "Die in sechs Spielen erhobene Passquote lag zuletzt höher als zuvor. Allerdings trafen die Rotbachtaler zuletzt auf Gegner, die seltener früh pressten, zumal sich durch die Viererkette auch die Passwege verändert haben. Eine ursächliche Wirkung des Rondo-Trainings lässt sich daraus nicht ableiten. Die Hypothese ließe sich besser prüfen, sofern man wiederholt vor und nach der Einführung misst, eine möglichst vergleichbare Mannschaft ohne Rondo-Training einbezieht und Gegnerstärke, Spielort sowie Erfassungsmethode berücksichtigt. Auch dann müsste die verbleibende Unsicherheit benannt werden.",
            speaking = "Berichten Sie in zwei Minuten ohne Vorlage dem Vorstand über den Text: Befund, drei Einschränkungen, welche Schlussfolgerung nicht gedeckt ist und welche Daten Sie zusätzlich bräuchten. Verwenden Sie mindestens ein Partizipialattribut und 'lässt sich nicht ableiten'. Nennen Sie keine echten Vereinsdaten.",
            interaction = "A ist Projektleiter/in unter Zeitdruck und möchte aus dem Fahrdynamikergebnis (0,82 g gegen 0,80 g, Unsicherheit plus minus 0,03 g, fiktiv) die Aussage 'B ist stabiler' an das Management melden. B erklärt die Messunsicherheit verständlich, widerspricht höflich und schlägt eine konkrete Wiederholungsmessung vor. Twist: A fragt zusätzlich, ob man 'dann wenigstens sagen kann, B dürfte besser sein'. B unterscheidet 'dürfte' von 'belegt'. Tauschen Sie die Rollen.",
            writing = "Schreiben Sie einen Kommentar von 250–300 Wörtern für das Vereinsmagazin zu einem fiktiven Spielbericht (Passquote im Spielaufbau, sechs Spiele, Gegnerstärke, Systemwechsel). Trennen Sie Befund, Einschränkungen und Schlussfolgerung, nennen Sie einen Prüfschritt und vermeiden Sie reale oder erfundene 'Beweise'. Verwenden Sie mindestens drei einschränkende Konnektoren.",
            transfer = "Persönliche Transferübung: Ein Verband veröffentlicht eine fiktive Umfrage: 'Eine deutliche Mehrheit wünscht längere Öffnungszeiten.' Rücklauf, Befragtengruppe, Fragestellung und Zeitraum sind nur teilweise genannt. Schreiben Sie, was die Aussage trägt, was nicht und welche Angaben fehlen. Selbstcheck: Markieren Sie jede Behauptung, die Sie direkt belegen können, und formulieren Sie mindestens zwei Rückfragen. Bearbeiten Sie nach einer Woche eine neue Umfrage aus einem anderen Kontext; beide Aufgaben bleiben Übung, keine Validierung.",
            feedback = "Modellrückmeldung: Sie trennen den Befund (höhere Quote in sechs Spielen) von der Ursache (Rondo-Training) und nennen Gegnerstärke und Systemwechsel als Einschränkungen. 'Die Zahlen beweisen, dass …' wäre zu stark; 'Die Zahlen sprechen nicht dagegen, belegen es aber nicht' trifft den Text besser. Beim EV-Beispiel haben Sie die Messunsicherheit richtig auf den Unterschied bezogen. Achten Sie darauf, 'dürfte' nicht als Beleg zu verwenden: Es markiert eine begründete Vermutung."
        )
    ),
    CurriculumUnit(
        "s4-challenge", "Annahmen kritisch prüfen", 2, emptyList(),
        listOf(
            "Beobachtungen, Deutungen und implizite Annahmen in einem Bericht sprachlich und inhaltlich trennen.",
            "Alternative Ursachen formulieren (Konjunktiv II der Vergangenheit) und einen Prüfschritt ableiten.",
            "Zuschreibungen wie 'Der ist halt so' als Deutung erkennen und respektvoll in eine überprüfbare Frage übersetzen."
        ),
        contextsA(
            "Die Prämissen einer Medienaussage erkennen",
            "Eine Zuschreibung hinter 'Der ist halt so' als Deutung statt Tatsache erkennen",
            "Annahmen einer Hybrid-Root-Cause-Analyse von Messbefunden unterscheiden",
            "In einem U15-Taktikbericht Beobachtungen zu Pressingfehlern von Spielerzuschreibungen trennen"
        ),
        UnitContent(
            reading = """
                Taktikbericht U15, TSV Lindenau gegen SG Hohenbach (erfundener Übungstext; Vereine und Spiel sind fiktiv)

                Beobachtung: In der zweiten Halbzeit kassierte die Mannschaft zwei Gegentore nach Ballverlusten im Mittelfeld. In beiden Szenen presste der Sechser den ballführenden Gegner allein an, während die Viererkette tief stehen blieb. Zwischen Abwehr und Mittelfeld entstand ein großer Raum, in dem die gegnerischen Achter über die Halbräume angespielt werden konnten. Nach dem Ballverlust wurde das Umschaltspiel des Gegners jeweils innerhalb weniger Sekunden gefährlich; ein Gegenpressing fand nicht statt. In der Halbzeitpause war die Pressinghöhe angehoben worden; ob die Anweisung bei allen Spielern der Viererkette angekommen war, wurde nicht abgefragt. Die erste Halbzeit endete torlos und wurde nicht per Video ausgewertet; die zweite Halbzeit liegt als Aufnahme vor.

                Einschätzung des Trainerteams: Das Pressing scheiterte am Sechser. Er ist offenbar nicht bereit, im Kollektiv zu arbeiten, und läuft nach der Pause einfach los, weil er eben so ist. Da die Mannschaft in der ersten Halbzeit gut verteidigt hat, kann es an der Grundordnung nicht liegen. Der Spieler sollte daher im nächsten Spiel auf der Bank beginnen, damit er den Ernst der Lage begreift. Der Fall zeigt außerdem, dass das Konzept des hohen Pressings in dieser Altersgruppe grundsätzlich funktioniert, wenn sich alle daran halten.

                Hinweis zur Quellenlage: Grundlage sind die Videoaufnahme der zweiten Halbzeit und die Erinnerung des Co-Trainers. Laufwege wurden nicht vermessen; die Abstände zwischen den Mannschaftsteilen sind geschätzt.
            """.trimIndent(),
            listening = """
                Demir: Gut, dann zur Root Cause, also zur eigentlichen Fehlerursache, beim Rucken im Hybrid-Prototyp. Herr Vogel, Ihre Einschätzung?
                Vogel: Ganz klar die neue Software. Seit Stand 4.2 ruckt es, vorher nicht. Mensch, das ist doch offensichtlich.
                Demir: Offensichtlich vielleicht. Aber was genau ist belegt?
                Vogel: Wir haben das Rucken auf dem Prototyp 2 nach dem Update beobachtet. Das ist belegt.
                Nico: Moment, ich bin die Strecken gefahren. Auf Prototyp 1, ebenfalls mit Stand 4.2, hab ich kein Rucken gemerkt.
                Vogel: Der hat ja auch 'ne andere Kupplungseinheit. Da fällt's vielleicht nicht auf.
                Demir: Dann ist die Annahme, dass die Software der einzige Unterschied ist, schon mal nicht haltbar, oder?
                Vogel: Na ja … beim Update wurde auch der Kalibrierdatensatz getauscht. Das stimmt. Ich hab das nur nicht als Unterschied mitgerechnet.
                Nico: Und die Öltemperatur lag bei den Fahrten mit Rucken höher. Das steht im Protokoll, ehrlich gesagt hab ich's nur nebenbei notiert.
                Demir: Dann haben wir mindestens drei Kandidaten: Software, Kalibrierdatensatz und Temperatur. Und zwei Annahmen im Hintergrund: dass die Fahrten vergleichbar waren und dass das Rucken immer gleich ausgelöst wird.
                Vogel: Okay, aber wenn's nicht die Software wäre, hätte es doch auch vorher schon ruckeln müssen.
                Demir: Nicht unbedingt. Wäre der Kalibrierdatensatz die Ursache, hätte sich das Rucken ebenfalls erst nach dem Update gezeigt. Dieselbe zeitliche Abfolge, andere Ursache.
                Vogel: Hm, da ist was dran.
                Nico: Wir könnten Stand 4.2 mit dem alten Kalibrierdatensatz fahren und umgekehrt. Bei gleicher Temperatur.
                Demir: Genau das ist der nächste Prüfschritt. Fünf Wiederholungen je Konfiguration, Randbedingungen dokumentiert.
                Vogel: Und bis dahin?
                Demir: Bis dahin schreiben wir: Rucken beobachtet, Ursache offen, drei Hypothesen, Prüfplan liegt vor. Und bitte nicht 'Software ist schuld' ins Protokoll.
                Vogel: Ist angekommen. Ich hätte die Konfigurationen früher gegenprüfen sollen.
            """.trimIndent(),
            expressions = listOf(
                "Beobachtet wurde …; daraus lässt sich nicht ableiten, dass … – Beobachtung und Schlussfolgerung trennen",
                "Das setzt voraus, dass … – eine implizite Annahme sichtbar machen",
                "Dahinter steht die Annahme, dass … – die Prämisse einer Aussage benennen",
                "eine Annahme hinterfragen / prüfen / belegen / widerlegen; eine Prämisse offenlegen – typische Verbverbindungen",
                "Dieselbe zeitliche Abfolge ist auch mit einer anderen Ursache vereinbar. – Korrelation nicht als Kausalität lesen",
                "Wäre …, hätte …; Hätte man …, wäre … – alternative Ursachen im Konjunktiv II der Vergangenheit formulieren",
                "'Der ist halt so.' – eine Zuschreibung, keine Beobachtung. Übersetzung: 'In diesen beiden Szenen habe ich … beobachtet; wie siehst du die Situation?'",
                "offenbar, anscheinend, angeblich, vermeintlich – Abstufungen der Verantwortung für eine Aussage",
                "Pressing, Gegenpressing, Umschaltspiel, Viererkette, Halbräume – die Beobachtung mit Fachbegriffen genau beschreiben"
            ),
            grammar = "Präsuppositionen stecken in Fragen und Behauptungen: 'Warum ist der Sechser so nachlässig?' setzt Nachlässigkeit schon voraus; neutral wäre: 'Was ist in diesen beiden Szenen passiert?' Alternativursachen formuliert man im Konjunktiv II der Vergangenheit: 'Wäre die Pressinghöhe allen erklärt worden, hätten Sechser und Viererkette womöglich gemeinsam aufgerückt.' Modalwörter markieren die Verantwortung für eine Aussage: 'offenbar' (es sieht so aus), 'angeblich' (andere behaupten es), 'vermeintlich' (ich bezweifle es). 'Halt' und 'eben' können eine Deutung als selbstverständlich darstellen, obwohl sie unbelegt ist; sie sind kein Argument.",
            question = "Welche Aussage im Taktikbericht ist eine nicht belegte Zuschreibung und keine Beobachtung?",
            options = listOf(
                "In beiden Szenen presste der Sechser den ballführenden Gegner allein an.",
                "Die Mannschaft kassierte zwei Gegentore nach Ballverlusten im Mittelfeld.",
                "Der Sechser ist nicht bereit, im Kollektiv zu arbeiten, und seine Haltung erklärt das Scheitern des Pressings.",
                "Die erste Halbzeit endete torlos."
            ),
            correctOption = 2,
            explanation = "C schreibt dem Spieler eine Haltung zu und macht sie zur alleinigen Ursache; im Bericht steht dafür kein Beleg, und alternative Ursachen (nicht abgefragte Anweisung, großer Raum zwischen Abwehr und Mittelfeld, fehlendes Gegenpressing) bleiben unberücksichtigt. A, B und D beschreiben Szenen oder Ergebnisse, die mit der Aufnahme überprüfbar sind.",
            controlledPrompt = "Formulieren Sie vier Sätze: eine neutrale Beobachtung, die Zuschreibung als Deutung markieren (offenbar oder angeblich), eine alternative Ursache im Konjunktiv II der Vergangenheit und eine Prüffrage an das Trainerteam.",
            controlledModel = "In beiden Szenen presste der Sechser allein, während die Viererkette tief stand. Die Annahme, er sei nicht bereit zur Zusammenarbeit, ist eine Deutung, die sich aus der Aufnahme nicht ablesen lässt. Wäre die geänderte Pressinghöhe allen Spielern ausdrücklich erklärt worden, hätten Sechser und Viererkette womöglich gemeinsam aufgerückt. Wir sollten deshalb prüfen, ob die Anweisung bei der Viererkette angekommen ist.",
            speaking = "Präsentieren Sie im Trainerteam in zwei Minuten ohne Vorlage Ihre Gegenrede zum Bericht: drei Beobachtungen, zwei implizite Annahmen, zwei alternative Ursachen, eine Prüffrage und eine respektvolle Formulierung für das Gespräch mit dem Spieler. Kritisieren Sie die Aussage, nicht die Person, die sie geschrieben hat.",
            interaction = "A ist Co-Trainer/in und vertritt: 'Der Sechser ist halt so, das haben wir schon oft gesehen.' B hinterfragt höflich, ohne zu belehren, und trennt Beobachtung und Deutung. Twist: A verweist auf 'viele Szenen', kann aber nur zwei benennen; B fragt nach den übrigen, bleibt fair und schlägt vor, gemeinsam das Video erneut anzusehen. Vereinbaren Sie einen Prüfschritt und einen Termin. Tauschen Sie die Rollen.",
            writing = "Verfassen Sie eine schriftliche Stellungnahme von 220–280 Wörtern zum Taktikbericht für das Trainerteam: Trennen Sie Beobachtungen, Deutungen und Annahmen, nennen Sie mindestens zwei Alternativursachen und einen Prüfschritt, und formulieren Sie eine Empfehlung für den Umgang mit dem Spieler, die ohne Charakterurteil auskommt.",
            transfer = "Persönliche Transferübung: Eine Zeitung titelt: 'Radwege machen Städte sicherer – seit dem Ausbau gibt es weniger Unfälle.' Im Text steht nur, dass die Zahl nach dem Ausbau niedriger war als im Vorjahr. Identifizieren Sie mindestens drei implizite Annahmen und zwei Alternativerklärungen. Selbstcheck: Haben Sie zeitliche Abfolge von Ursache getrennt und benannt, welche Daten zur Prüfung fehlen? Suchen Sie keine Bestätigung für die Schlagzeile, sondern prüfen Sie auch Gegenhypothesen.",
            feedback = "Modellrückmeldung: Sie haben die Beobachtungen (allein angepresst, tiefe Viererkette, kein Gegenpressing) sauber von der Zuschreibung (nicht bereit, im Kollektiv zu arbeiten) getrennt und die nicht abgefragte Pressinghöhe als Alternativursache genannt. 'Er ist halt so' sollten Sie nicht nur ablehnen, sondern in eine Frage übersetzen. Im Hybrid-Beispiel haben Sie die zeitliche Abfolge richtig nicht als Beleg gewertet; ergänzen Sie, welche Konfiguration Sie gegeneinander prüfen würden."
        )
    ),
    CurriculumUnit(
        "s5-nuance", "Bedeutungsnuancen verstehen", 2, emptyList(),
        listOf(
            "Abstufungen, Litotes und Modalpartikeln im Kontext zuverlässig deuten und bei Unsicherheit gezielt nachfragen.",
            "Regionale süddeutsche und schwäbische Formen als regional erkennen, verstehen und respektvoll einordnen, ohne sie zu verallgemeinern.",
            "'Möglich', 'plausibel' und 'nachgewiesen' in einer Safety-Bewertung unterscheiden und indirekte Kritik erkennen."
        ),
        contextsA(
            "Eine vorsichtig formulierte Einschätzung verstehen",
            "Schwäbisches 'net schlecht' und 'passt scho' im Kontext verstehen; keine pauschale Lob- oder Zusageregel annehmen",
            "'Möglich', 'plausibel' und 'nachgewiesen' in einer Automotive-Safety-Bewertung unterscheiden",
            "Indirekte Elternkritik an U11-Trainingsbelastung verstehen und offene Punkte erkennen"
        ),
        UnitContent(
            reading = """
                Zwischen den Zeilen: Wie man Abstufungen liest

                Wer in eine neue Region zieht, versteht die Wörter oft schneller als die Gewichte, die man ihnen gibt. Eine Journalistin aus Norddeutschland, die seit einem Jahr im Raum Stuttgart lebt, erzählt, ihr Vermieter habe ihre Renovierung mit „Net schlecht“ kommentiert. Sie sei zunächst gekränkt gewesen. Erst später stellte sich heraus, dass er sich anerkennend geäußert hatte. Ob „net schlecht“ jedoch stets so gemeint ist, lässt sich nicht pauschal sagen: Betonung, Mimik, der folgende Satz und die Beziehung der Beteiligten entscheiden, und ein gedehntes „net schlecht … aber“ signalisiert durchaus Zurückhaltung. Ähnliches gilt für „Des passt scho“, das man in Süddeutschland und auch im Schwäbischen hört. Es kann beruhigen („Keine Sorge“), eine Bitte annehmen („In Ordnung“) oder ein Angebot höflich abwehren („Danke, nicht nötig“). Wer Verbindlichkeit braucht, fragt freundlich nach: „Heißt das, dass Sie am Freitag kommen?“

                Auch Partikeln und Endungen tragen Bedeutung. Mit „Das war ein schöner Abend, gell?“ sucht die Sprecherin typischerweise Rückversicherung und Einbezug; die Äußerung ist keine offene Informationsfrage. Das Diminutiv „-le“ („Häusle“, „ein Viertele“) muss nichts Kleines meinen; es kann Zuneigung, Bescheidenheit oder Ironie ausdrücken. Wenn jemand sagt „Mir sind schon da“ (in diesem Beispiel standarddeutsch: Wir sind schon da) oder fragt „Wo schaffsch du?“ (neutral standardsprachlich: Wo arbeitest du?), ist das schlicht Alltagssprache und kein Zeichen mangelnder Bildung. Und „Hock di na“ (Setz dich hin) ist häufig eine Einladung statt eines scharfen Befehls; es bleibt eine Aufforderungsform, deren Wirkung auch von Ton und Beziehung abhängt.

                Wichtig ist dreierlei. Erstens sind diese Formen regional und nicht überall im deutschen Sprachraum üblich; auch in Baden-Württemberg unterscheiden sie sich nach Alter, Ort und Situation. Zweitens gibt es keine feste Übersetzungsregel von „net schlecht“ zu „gut“. Drittens genügt es für Lernende, solche Formen zu verstehen; wer sie nachahmt, um dazuzugehören, kann unbeabsichtigt spöttisch wirken. Ähnlich indirekt äußern sich manche Eltern im Jugendfußball: „Der Max ist abends halt ziemlich platt“ kann eine bloße Information sein – oder die vorsichtige Frage, ob die Trainingsbelastung zu hoch ist. Dann hilft eine offene Rückfrage: „Wie erleben Sie die Abende nach dem Training, und was würden Sie sich wünschen?“
            """.trimIndent(),
            listening = """
                Hartmann: Wir sind bei der FMEA-Zeile zum ungewollten Rekuperationsmoment an der Hinterachse bei niedriger Haftung. Herr Schwarz, wie ist der Stand?
                Schwarz: Also, die Simulation sieht net schlecht aus. Das Fahrzeug bleibt in allen gerechneten Szenarien stabil.
                Kowalski: Nicht schlecht – heißt das gut oder gerade so? Ich muss gleich ins Lenkungsgremium.
                Schwarz: Gute Frage, das hätte ich genauer sagen sollen. Alle Szenarien bestanden, aber es sind eben Simulationsfälle.
                Hartmann: Dann ist es plausibel, dass die Funktion stabil ist, aber noch nicht nachgewiesen.
                Schwarz: Genau. Plausibel, würde ich sagen. Nachgewiesen wäre es erst mit den Prüfstands- und Fahrversuchen aus dem Testplan.
                Kowalski: Und ein Fehler ist weiterhin möglich?
                Hartmann: Ja. Möglich heißt hier: Er ist nicht ausgeschlossen. Die Schwere der Fehlerfolge haben wir hoch bewertet; Auftreten und Entdeckung sind noch offen, deshalb lässt sich die Aktionspriorität noch nicht abschließend bestimmen.
                Kowalski: Gibt's denn schon eine ASIL-Einstufung?
                Hartmann: Vorläufig ASIL C aus der Gefährdungs- und Risikoanalyse. Das wird im Safety-Review bestätigt oder korrigiert.
                Kowalski: Dann kann ich dem Gremium sagen, die Funktion sei sicher?
                Hartmann: Nein, bitte nicht. Sagen Sie: Simulation positiv, Nachweis ausstehend, vorläufig ASIL C.
                Schwarz: Des passt scho, wenn wir das so formulieren. Wichtig ist, dass keiner 'plausibel' als 'nachgewiesen' weitergibt.
                Kowalski: Moment – 'passt scho': Heißt das, Sie sind mit dieser Formulierung einverstanden?
                Schwarz: Ja, ich bin einverstanden. Ich schick Ihnen den Wortlaut gleich schriftlich.
                Hartmann: Gut, dann halten wir das so im Protokoll fest. Fiktives Beispiel, wie gesagt, alle Angaben dienen nur der Übung.
                Kowalski: Danke. Dann habe ich für das Gremium eine klare Aussage und eine klare offene Frage.
            """.trimIndent(),
            expressions = listOf(
                "'Net schlecht' (regional: süddeutsch, auch schwäbisch, für 'nicht schlecht') – kann von zurückhaltend bis anerkennend reichen; Betonung und Folgesatz beachten. Nachfrage: 'Was hat Ihnen besonders gefallen?'",
                "'Des passt scho' (regional: süddeutsch, auch schwäbisch) – beruhigend, einverstanden oder höflich abwehrend; bei Verbindlichkeit nachfragen: 'Heißt das, dass …?'",
                "'Gell?' (regional, mit regionalen Varianten) – häufig eine Rückversicherung oder ein Einbezug des Gegenübers, keine offene W-Frage; die genaue Wirkung hängt von Kontext und Sprechweise ab",
                "'Mir sind …' (regional, schwäbisch: 'wir sind'); 'Du kriegsch …' (schwäbische Endung -sch: 'du kriegst/bekommst') – verstehen, nicht pauschal nachahmen",
                "Das Diminutiv '-le' (regional: 'Häusle', 'Viertele') – kann Zuneigung, Bescheidenheit oder Ironie ausdrücken, nicht nur Kleinheit",
                "'Schaffe' (hier regional, schwäbisch: arbeiten; 'Wo schaffsch du?') – neutral standardsprachlich: 'Wo arbeitest du?'; das standardsprachliche 'schaffen' hat je nach Kontext weitere Bedeutungen, etwa 'bewältigen'",
                "'Hock di na!' (regional, unter anderem süddeutsch: 'Setz dich hin!') – kann als Einladung statt als scharfer Befehl gemeint sein; es bleibt eine Aufforderungsform, und Ton sowie Beziehung entscheiden",
                "möglich – plausibel – nachgewiesen – drei Stufen der Evidenz: nicht ausgeschlossen, durch Argumente gestützt, durch Prüfung belegt",
                "nicht unerheblich, nicht ganz unplausibel, nicht schlecht – Litotes: Verneinung des Gegenteils, oft zurückhaltend gemeint",
                "'Der Max ist abends halt ziemlich platt.' – indirekte Kritik oder Information? Offen nachfragen: 'Wie erleben Sie die Abende nach dem Training?'"
            ),
            grammar = "Litotes: 'nicht schlecht', 'nicht unerheblich' sagen weniger, als sie meinen können, oder genau das, was sie sagen; der Kontext entscheidet. Epistemische Modalverben bilden eine Skala: 'Der Fehler kann auftreten' (möglich), 'Die Funktion dürfte stabil sein' (begründet vermutet), 'Die Funktion muss stabil sein' (schlussfolgernd, starker Schluss; ersetzt keinen Nachweis). Modalpartikeln wie 'halt', 'eben', 'doch', 'ja', 'wohl' färben eine Aussage: 'Er ist halt platt' stellt etwas als selbstverständlich dar. 'Gell' ist kein Fragewort im engen Sinn, sondern eine Rückversicherung. Dialektformen sind regional markiert; im Standarddeutschen schreibt man 'wir sind', 'du bekommst', 'arbeiten'.",
            question = "Was lässt sich über das schwäbische 'net schlecht' am besten sagen?",
            options = listOf(
                "Es bedeutet immer ein sehr großes Lob.",
                "Es bedeutet immer eine verdeckte Ablehnung.",
                "Seine Bedeutung hängt von Betonung, Folgesatz und Beziehung ab; bei Unsicherheit fragt man freundlich nach.",
                "Es wird im ganzen deutschen Sprachraum gleich verwendet."
            ),
            correctOption = 2,
            explanation = "C entspricht dem Text: Der Kontext entscheidet, eine feste Regel gibt es nicht. A und B ersetzen die Offenheit durch eine falsche Pauschalregel in entgegengesetzte Richtungen. D widerspricht dem Hinweis des Textes, dass diese Formen regional sind und auch in Baden-Württemberg unterschiedlich verbreitet.",
            controlledPrompt = "Erklären Sie in vier Sätzen: (1) 'Des passt scho' kann Zusage, Beruhigung oder höfliche Abwehr sein (regional markieren), (2) wie Sie nachfragen, (3) 'möglich', 'plausibel', 'nachgewiesen' in einer Safety-Aussage, (4) ein Wort zur respektvollen Haltung gegenüber regionaler Sprache.",
            controlledModel = "Das regionale 'Des passt scho', das man in Süddeutschland und auch im Schwäbischen hört, kann je nach Situation eine Zusage, eine Beruhigung oder eine höfliche Abwehr sein. Bei Unsicherheit frage ich freundlich nach: 'Heißt das, dass wir es so machen?' In einer Safety-Bewertung bedeutet 'möglich', dass ein Fehler nicht ausgeschlossen ist, 'plausibel', dass Argumente dafür sprechen, und 'nachgewiesen', dass eine Prüfung es belegt. Regionale Redeweisen verstehe ich als normale Sprache, die ich nicht verallgemeinere oder nachahme, um dazuzugehören.",
            speaking = "Erklären Sie in zwei Minuten ohne Vorlage einem Kollegen, der neu in Stuttgart ist, wie er 'net schlecht', 'Des passt scho' und 'gell' verstehen kann, ohne daraus feste Regeln zu machen. Nennen Sie zwei Rückfragen und ein Beispiel für indirekte Elternkritik im Jugendfußball. Vermeiden Sie Stereotype über Menschen aus der Region.",
            interaction = "A ist Elternteil einer U11-Spielerin und sagt: 'Die Lena ist abends halt ziemlich platt, gell?' B ist Trainer/in und fragt offen nach, ob A eine Änderung wünscht oder nur informieren möchte. Twist: A antwortet zunächst nur 'Des passt scho.' B darf das nicht als Zusage oder als Ablehnung deuten und fragt konkret nach Trainingsdauer, Wochenbelastung und Wunsch. Vereinbaren Sie einen überprüfbaren nächsten Schritt. Tauschen Sie die Rollen.",
            writing = "Schreiben Sie eine Gesprächsnotiz von 160–200 Wörtern für ein Safety-Protokoll (fiktives Beispiel: ungewolltes Rekuperationsmoment bei niedriger Haftung): Trennen Sie 'möglich', 'plausibel' und 'nachgewiesen', nennen Sie Schwere, Auftreten und Entdeckung als Bewertungsgrößen der Aktionspriorität und halten Sie fest, welche Aussage im Gremium nicht gemacht werden darf. Eine regionale Äußerung ('net schlecht') aus dem Gespräch soll in neutrale Standardsprache übertragen und mit ihrem tatsächlichen Gehalt festgehalten werden.",
            transfer = "Persönliche Transferübung: Eine Förderstelle schreibt: 'Ihr Konzept ist nicht uninteressant; die Kostenplanung dürfte noch geschärft werden. Wir sehen zunächst keinen Anlass für Bedenken.' Deuten Sie die Abstufungen vorsichtig, trennen Sie sichere Aussagen von Vermutungen und formulieren Sie zwei höfliche Rückfragen. Selbstcheck: Haben Sie keine Zusage unterstellt und die Kostenplanung konkret angesprochen? Die Aufgabe zeigt Übungsfortschritt, aber keine validierte Pragmatikkompetenz.",
            feedback = "Modellrückmeldung: Sie lesen 'net schlecht' und 'passt scho' als kontextabhängig und fragen freundlich nach, statt eine Regel zu unterstellen. In der Safety-Notiz halten Sie 'plausibel' und 'nachgewiesen' sauber auseinander und verwechseln Simulation nicht mit Prüfung. Bei der Elternkritik ist Ihre Rückfrage offen; 'Sie meinen also, wir trainieren zu hart' würde dagegen etwas unterstellen. Wenn Sie dialektale Formen erwähnen, kennzeichnen Sie sie als regional und beschreiben Sie Menschen nicht pauschal."
        )
    )
)
