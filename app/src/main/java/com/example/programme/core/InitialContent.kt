package com.example.programme.core

/** Original German instructional content. Model responses are examples, never learner evidence. */
internal object InitialContent {
    val publicDecision = AuthoredContent(
        objectives = listOf(
            "Eine Position mit Belegen, Gegenargument und Einschränkung in zwei Minuten begründen.",
            "Kosten, Reichweite und Aussagekraft eines Pilotversuchs auseinanderhalten.",
            "In einer Rückfrage ein Missverständnis klären und einen überprüfbaren Kompromiss anbieten.",
            "Eine Verwaltungsvorlage für eine andere Zielgruppe in verständlichem Deutsch zusammenfassen."
        ),
        sources = listOf(
            LearningSource("u01-read", "Eine Bibliothek für alle – aber wann?", SourceKind.READING,
                """Die Stadt Lindenau erwägt, ihre Zentralbibliothek an zwei Wochentagen bis 21 Uhr statt bis 18 Uhr zu öffnen. In einer freiwilligen Onlinebefragung befürworteten 68 Prozent der 420 Teilnehmenden längere Öffnungszeiten. Daraus lässt sich jedoch nicht ableiten, dass zwei Drittel aller Einwohnerinnen und Einwohner das Angebot nutzen würden: Wer bereits eine Bibliothekskarte besitzt, wurde über den Newsletter besonders leicht erreicht. Menschen ohne Internetzugang waren dagegen kaum vertreten.

Für einen zwölfwöchigen Versuch veranschlagt die Verwaltung zusätzliche Personalkosten von 18.000 Euro. Die regulären Vormittagszeiten sollen erhalten bleiben. Die Summe betrifft ausschließlich den Versuch; eine jährliche Finanzierung ist noch nicht beschlossen. Ein Teil des Personals befürchtet, dass zusätzliche Abenddienste die ohnehin knappe Besetzung belasten könnten. Der Personalrat fordert deshalb einen Dienstplan, der freiwillige Meldungen bevorzugt und ausreichende Ruhezeiten sicherstellt.

Studierende begrüßen den Vorschlag. Eine Vertreterin pflegender Angehöriger weist allerdings darauf hin, dass ein später Abend nicht für alle eine Entlastung bedeutet. Sie regt ergänzend einen verlässlichen Samstagsdienst an. Dieser ist im bisherigen Budget nicht enthalten. Die Leiterin der Bibliothek schlägt vor, während des Versuchs nicht nur Besuche zu zählen, sondern auch neue Nutzergruppen und die Belastung des Personals zu erfassen. Die Besucherzahlen allein könnten nämlich steigen, obwohl dieselben Personen lediglich später kommen.

Der Stadtrat muss daher zunächst entscheiden, welche Frage der Versuch beantworten soll. Geht es um mehr Besuche insgesamt oder um einen gerechteren Zugang? Erst nach einer solchen Klärung lässt sich beurteilen, ob die zusätzlichen Mittel sinnvoll eingesetzt wären. Ein Versuch ersetzt diese Zielentscheidung nicht, kann aber eine belastbarere Grundlage für die spätere Finanzierung schaffen."""),
            LearningSource("u01-listen", "Stellungnahme im Bürgerforum", SourceKind.LISTENING_TTS,
                """Ich arbeite im Schichtdienst und kann die Bibliothek oft nicht vor achtzehn Uhr erreichen. Deshalb halte ich den Abendversuch grundsätzlich für sinnvoll. Mir ist aber wichtig, dass daraus kein Gegensatz zwischen Berufstätigen und älteren Menschen entsteht. Die Vormittagszeiten dürfen nicht gekürzt werden. In der Vorlage steht, dass sie erhalten bleiben sollen; das ist für mich eine notwendige Bedingung.

Mit der Befragung wäre ich vorsichtig. Achtundsechzig Prozent klingt eindeutig, sagt aber wenig über diejenigen aus, die den Newsletter gar nicht erhalten. Man könnte während des Versuchs zusätzlich im Stadtteilzentrum nachfragen. Ich würde nach sechs Wochen einen Zwischenstand veröffentlichen: Wer kommt neu, und wie geht es den Beschäftigten mit dem Dienstplan? Falls fast nur bisherige Gäste ihre Besuche verschieben, müssten wir das offen benennen. Mein Vorschlag ist also kein uneingeschränktes Ja, sondern ein befristetes Ja mit klaren Prüfkriterien. Einen Samstagsdienst finde ich ebenfalls interessant, doch darüber sollten wir mit einem eigenen Kostenvorschlag entscheiden.""")
        ),
        collocations = listOf(
            Collocation("etwas in Erwägung ziehen", "Die Stadt zieht längere Öffnungszeiten in Erwägung."),
            Collocation("Mittel bereitstellen", "Für den Versuch werden befristet Mittel bereitgestellt."),
            Collocation("einen Vorbehalt äußern", "Der Personalrat äußert einen Vorbehalt hinsichtlich der Belastung."),
            Collocation("eine Schlussfolgerung ziehen", "Aus der Befragung lässt sich diese Schlussfolgerung nicht ziehen."),
            Collocation("einer Belastung Rechnung tragen", "Der Dienstplan muss der zusätzlichen Belastung Rechnung tragen."),
            Collocation("eine Entscheidung an Kriterien knüpfen", "Wir knüpfen die Fortsetzung an überprüfbare Kriterien.")
        ),
        grammar = listOf(
            GrammarFocus("Konzessive Verknüpfung", "Obwohl leitet einen Nebensatz mit Verb am Ende ein; dennoch steht im Hauptsatz mit Verb an zweiter Position.", "Obwohl die Zustimmung hoch ist, bleibt die Repräsentativität unklar. Die Zustimmung ist hoch; dennoch fehlen bestimmte Gruppen."),
            GrammarFocus("Vorsichtige Schlussfolgerung", "Konjunktiv II und Modalverben markieren Möglichkeiten statt gesicherter Folgen.", "Längere Öffnungszeiten könnten den Zugang erleichtern; sie würden aber zusätzliche Dienste erfordern."),
            GrammarFocus("Indirekte Positionen", "Mit zufolge plus Dativ oder laut plus Dativ eine Quelle benennen, ohne die Aussage als eigene Gewissheit auszugeben.", "Dem Personalrat zufolge muss die Belastung begrenzt werden.")
        ),
        comprehension = listOf(
            ComprehensionItem("u01-read", "Worauf beziehen sich die 68 Prozent, und welche Schlussfolgerung wäre unzulässig?", "Auf 420 freiwillig Teilnehmende, nicht auf alle Einwohner. Eine repräsentative Zustimmung von 68 Prozent lässt sich daraus nicht ableiten."),
            ComprehensionItem("u01-read", "Wofür sind die 18.000 Euro vorgesehen?", "Für zusätzliche Personalkosten eines zwölfwöchigen Versuchs, nicht für eine beschlossene Jahresfinanzierung oder den Samstagsdienst."),
            ComprehensionItem("u01-read", "Warum reichen steigende Besuchszahlen als Erfolgskriterium nicht aus?", "Bisherige Gäste könnten nur zu einer anderen Uhrzeit kommen; neue Gruppen und Personalbelastung müssen getrennt erfasst werden."),
            ComprehensionItem("u01-listen", "Welche Bedingung nennt die sprechende Person für ihre Zustimmung?", "Die Vormittagszeiten müssen erhalten bleiben; Zustimmung gilt befristet mit klaren Prüfkriterien."),
            ComprehensionItem("u01-listen", "Wann und mit welchen Inhalten soll ein Zwischenstand erscheinen?", "Nach sechs Wochen; zu neu erreichten Gruppen und zur Belastung der Beschäftigten."),
            ComprehensionItem("u01-listen", "Lehnt die Person den Samstagsdienst ab? Begründe.", "Nein. Sie findet ihn interessant, verlangt aber eine getrennte Entscheidung mit eigenem Kostenvorschlag.")
        ),
        tasks = listOf(
            PracticeTask("u01-controlled", ActivityKind.CONTROLLED,
                "Verbinde: Die Zustimmung ist hoch. Die Befragung ist nicht repräsentativ. Verwende obwohl. Formuliere danach vorsichtig: Längere Öffnungszeiten erleichtern den Zugang. Korrigiere zuletzt: Die 18.000 Euro finanzieren dauerhaft auch den Samstag.",
                "Verbendstellung; Möglichkeit statt Gewissheit; Budgetgrenze vollständig wiedergeben.",
                "Obwohl die Zustimmung hoch ist, ist die Befragung nicht repräsentativ. Längere Öffnungszeiten könnten den Zugang erleichtern. Die 18.000 Euro decken zusätzliche Personalkosten des zwölfwöchigen Abendversuchs; der Samstag ist nicht enthalten.",
                "Die Einschränkung zur Stichprobe steht jetzt ausdrücklich im Satz. Prüfe bei obwohl die Endstellung von ist. Könnten schwächt die Aussage sachgerecht ab, statt einen Erfolg zu garantieren."),
            PracticeTask("u01-speaking", ActivityKind.SPEAKING,
                "Sprich nach höchstens 30 Sekunden Denkzeit zwei Minuten im Bürgerforum. Beziehe Position, nutze zwei konkrete Angaben, nenne einen Einwand und schlage ein überprüfbares Erfolgskriterium vor. Keine ausformulierten Notizen.",
                "Klare Position; 12 Wochen und Budget korrekt; Gegenposition fair; Kriterium von bloßer Besuchszahl abgrenzen.",
                "Ich befürworte den zwölfwöchigen Versuch unter Vorbehalt. Die 18.000 Euro sind ein befristeter Aufwand, keine Zusage für den Dauerbetrieb. Zwar gibt es hohe Zustimmung unter den Befragten, doch die Auswahl ist verzerrt. Entscheidend wäre für mich, ob bislang ausgeschlossene Gruppen erreicht werden, ohne das Personal zu überlasten. Deshalb sollten wir nach sechs Wochen neue Nutzergruppen und Dienstbelastung prüfen und erst danach über eine Fortsetzung sprechen.",
                "Der Beitrag trennt Zustimmung und gesicherte Nutzung. Gut ist die überprüfbare Zwischenprüfung. Noch präziser wäre eine vorab vereinbarte Messmethode; erfinde keinen angeblich beschlossenen Grenzwert."),
            PracticeTask("u01-interaction", ActivityKind.INTERACTION,
                "Bitte einen realen Gesprächspartner, gegen den Abendversuch einzuwenden: Dafür wird doch der Vormittag gestrichen! Reagiere, frage nach dem eigentlichen Anliegen und handle eine gemeinsame Prüffrage aus. Der Partner soll anschließend einen unerwarteten Einwand wählen. Eine KI-Rolle ist nur Simulation.",
                "Missverständnis höflich korrigieren; echte Rückfrage und Anschlussreaktion; keine Zustimmung unterstellen.",
                "Wenn ich Sie richtig verstehe, befürchten Sie weniger Angebote am Vormittag. Laut Vorlage sollen diese Zeiten erhalten bleiben. Geht es Ihnen vor allem um diese Zusicherung oder auch um die Besetzung? Wir könnten gemeinsam verlangen, dass der Zwischenbericht beide Punkte ausweist. Wäre das für Sie eine Grundlage, den Versuch zu prüfen?",
                "Die Paraphrase nimmt die Sorge ernst, ohne die falsche Annahme zu übernehmen. Der nächste eigene Satz muss auf die tatsächliche Antwort reagieren; das Muster allein belegt keine Interaktionsfähigkeit."),
            PracticeTask("u01-writing", ActivityKind.WRITING,
                "Verfasse eine Stellungnahme von 180–220 Wörtern an den Stadtrat. Erkläre Nutzen und Grenzen der Daten, berücksichtige zwei Interessengruppen und schlage eine bedingte Entscheidung vor. Bezeichne unbelegte Erwartungen als solche.",
                "Adressatengerechte Anrede; nachvollziehbare Absätze; kein Repräsentativitätsfehlschluss; überprüfbarer Vorschlag.",
                "Sehr geehrte Mitglieder des Stadtrats,\n\nich empfehle den zwölfwöchigen Abendversuch, sofern die Vormittagszeiten erhalten bleiben und die Belastung des Personals überprüft wird. Für Menschen im Schichtdienst könnte ein späteres Angebot den Zugang erleichtern. Die Onlinebefragung liefert dafür einen Hinweis, aber keinen repräsentativen Nachweis: Die 68 Prozent beziehen sich auf freiwillig Teilnehmende, nicht auf die gesamte Stadtbevölkerung.\n\nDie veranschlagten 18.000 Euro betreffen zusätzliche Personalkosten während des Versuchs. Daraus folgt weder eine dauerhafte Finanzierung noch ein finanziertes Samstagsangebot. Gerade pflegende Angehörige könnten jedoch andere Zeiten benötigen. Diesen Einwand sollten wir ernst nehmen und einen Samstagsdienst gegebenenfalls getrennt kalkulieren. Zugleich muss der Dienstplan ausreichende Ruhezeiten ermöglichen; bessere Zugänglichkeit darf nicht auf Kosten der Beschäftigten gehen.\n\nIch rege an, nach sechs Wochen einen Zwischenstand zu veröffentlichen. Dabei sollten freiwillige kurze Befragungen vor Ort erfassen, ob neue Gruppen erreicht werden oder bisherige Gäste lediglich später kommen. Ergänzend wäre eine Rückmeldung des Personals zur Dienstbelastung sinnvoll. Die Erhebung sollte so gestaltet sein, dass auch Menschen ohne Internetzugang mitreden können.\n\nVor Beginn sollten wir vereinbaren, wie diese Ergebnisse in die Entscheidung über eine Fortsetzung eingehen. Ein Versuch schafft eine bessere Entscheidungsgrundlage, garantiert aber noch keinen gerechteren Zugang.\n\nMit freundlichen Grüßen", 
                "Die Stellungnahme verbindet eine bedingte Empfehlung mit einer konkreten Erhebungsmethode und einem fairen Einwand. Vor-Ort-Befragungen sind ein eigener Vorschlag, kein bereits gefasster Beschluss. Vermeide die Behauptung, der Versuch beweise bereits bessere Teilhabe."),
            PracticeTask("u01-mediation", ActivityKind.MEDIATION,
                "Eine Nachbarin liest Verwaltungstexte ungern. Erkläre ihr in 90–120 einfachen deutschen Wörtern, was geplant ist, was noch offenbleibt und wie ihre Sorge um Vormittagstermine berücksichtigt wird. Übersetze nicht Satz für Satz.",
                "Kernaussagen korrekt; keine Fachwortkette; Plan und Beschluss unterscheiden; keine neue Zusage.",
                "Die Bibliothek könnte an zwei Tagen länger offen bleiben. Zuerst soll das zwölf Wochen lang ausprobiert werden. Dafür sind zusätzliche Personalkosten von 18.000 Euro vorgesehen. Morgens soll sie weiter wie bisher öffnen. Ob das dauerhaft so bleibt, ist noch nicht entschieden. Viele Teilnehmende einer Onlinebefragung waren dafür, aber nicht alle Bevölkerungsgruppen wurden gut erreicht. Deshalb soll auch geprüft werden, ob neue Menschen kommen und ob die Beschäftigten die Dienste gut bewältigen können. Ein zusätzlicher Samstag ist eine andere Idee; dafür gibt es bisher kein Geld im Versuchsbudget.",
                "Die Vereinfachung erhält die Unsicherheit und die Budgetgrenze. Frage nach, ob der Unterschied zwischen Versuch und Dauerbetrieb verständlich geworden ist; ersetze bei Bedarf Personalkosten durch Geld für zusätzliche Arbeitszeit."),
            PracticeTask("u01-transfer", ActivityKind.TRANSFER,
                "Übungswechsel in einen Betrieb: Ein Team erwägt eine zwölfwöchige spätere Servicezeit. Nur Newsletter-Abonnenten wurden befragt. Entwickle ohne Bibliotheksformulierungen eine Empfehlung für die Teamleitung und benenne fehlende Informationen. Für einen Leistungsnachweis muss ein Mensch einen neuen, vorher ungesehenen Fall bereitstellen.",
                "Stichprobenproblem übertragen; Belastung und Nutzen abwägen; mindestens zwei neue Klärungsfragen.",
                "Die Befragung erreicht vermutlich vor allem bestehende Kundschaft. Ich würde zunächst klären, welche bislang nicht erreichten Gruppen einen späteren Service benötigen und wie Zusatzdienste verteilt werden können. Ein befristeter Versuch wäre vertretbar, wenn wir neue Anfragen getrennt von verschobenen Kontakten erfassen und Abbruchkriterien vereinbaren.",
                "Du überträgst das Prinzip statt nur Bibliothek durch Betrieb zu ersetzen. Dieser sichtbare Übungsfall ist nach Einsicht nicht mehr unbekannt und darf nicht als unfamiliarTask verbucht werden."),
            PracticeTask("u01-review", ActivityKind.DELAYED_REVIEW,
                "Frühestens sieben Tage nach dem ersten unabhängigen Versuch: Erkläre ohne Quellen die Grenze einer freiwilligen Befragung, bilde zwei passende Kollokationen und begründe einen befristeten Versuch in einem neuen sozialen Kontext. Danach mit den Quellen abgleichen und einen eigenen Fehler in einem neuen Satz korrigieren.",
                "Kernaussage ohne Vorlage rekonstruieren; eigene Formulierungen; Datengrenze bewahren; neue Aufgabe durch Prüfer wählen lassen.",
                "Aus einer freiwilligen Befragung lässt sich keine repräsentative Zustimmung ableiten. Ein Verein könnte dennoch Mittel für einen begrenzten Versuch bereitstellen und die Fortsetzung an Kriterien knüpfen. Vorher wäre zu klären, welche Mitglieder bisher nicht erreicht wurden.",
                "Wörtliches Erinnern ist nicht das Ziel. Prüfe, ob du Schlussfolgerung ziehen korrekt benutzt und ob dein neuer Fall tatsächlich eine andere Zielgruppe enthält. Der Zeitabstand allein genügt nicht für Beherrschung.", 7)
        )
    )

    val socialAgreement = AuthoredContent(
        objectives = listOf(
            "Beobachtung, Deutung und Bedürfnis in einem Konflikt sprachlich trennen.",
            "Eine strittige Position neutral paraphrasieren und mit einer offenen Frage prüfen.",
            "Eine konkrete Vereinbarung mit Zuständigkeit, Frist und Rückmeldung aushandeln.",
            "Eine Konfliktzusammenfassung ohne Schuldzuweisung für abwesende Beteiligte verfassen."
        ),
        sources = listOf(
            LearningSource("u02-read", "Wer räumt nach dem Nachbarschaftscafé auf?", SourceKind.READING,
                """Seit drei Monaten organisiert ein Nachbarschaftsverein jeden Freitag ein offenes Café. Nach den letzten beiden Treffen blieben benutzte Tassen und volle Abfallsäcke im Raum stehen. Am Samstagmorgen räumte jeweils Leyla auf, die den Schlüssel für die nächste Veranstaltung abholen wollte. Im Gruppenchat schrieb sie: Offenbar ist euch meine Zeit egal. Darauf antwortete Jonas, er habe am Freitag wegen seines letzten Busses früher gehen müssen. Eine feste Aufräumverteilung sei ihm nicht bekannt gewesen.

Die Koordinatorin prüfte die bisherige Absprache. Im Protokoll stand lediglich, dass alle gemeinsam Verantwortung für den Raum übernehmen. Weder ein Zeitfenster noch eine Vertretung war festgelegt. Leyla hatte daraus geschlossen, dass alle bis zum Ende bleiben würden. Jonas verstand darunter, während des Cafés bei Bedarf mitzuhelfen. Beide beriefen sich also auf dieselbe Formulierung, verbanden damit aber unterschiedliche Erwartungen.

Für das nächste Treffen schlägt die Koordinatorin vor, den Ablauf statt die persönliche Einstellung zu besprechen. Zwei Personen könnten sich jeweils bis Donnerstag für den Abschlussdienst eintragen. Wer früher gehen müsse, solle das vorher mitteilen und eine Vertretung anfragen. Noch offen ist, ob für alle genügend passende Termine verfügbar sind. Eine Teilnehmerin mit Betreuungspflichten könnte stattdessen vor Beginn aufbauen. Diese Möglichkeit soll nicht automatisch als weniger wertvoll gelten.

Leyla wünscht sich vor allem Verlässlichkeit und eine kurze Nachricht, falls etwas liegen bleibt. Jonas möchte sich beteiligen, ohne den letzten Bus zu verpassen. Eine Lösung müsste beide Anliegen berücksichtigen. Ob die neue Verteilung funktioniert, soll nach zwei weiteren Caféterminen gemeinsam geprüft werden. Die Koordinatorin betont, dass ein Eintrag im Plan allein noch keine gelungene Zusammenarbeit belegt: Entscheidend seien die tatsächliche Übergabe und die Möglichkeit, rechtzeitig um Hilfe zu bitten."""),
            LearningSource("u02-listen", "Ein Gespräch vor dem nächsten Café", SourceKind.LISTENING_TTS,
                """Leyla: Ich möchte meinen Satz aus dem Chat zurücknehmen. Ich weiß nicht, ob euch meine Zeit egal ist. Was ich weiß: An zwei Samstagen habe ich jeweils fast eine halbe Stunde aufgeräumt, obwohl ich nur den Schlüssel holen wollte. Das hat mich geärgert.
Jonas: Das verstehe ich. Ich dachte, mit meinem Aufbau am Nachmittag hätte ich meinen Teil übernommen. Freitags muss ich um zwanzig Uhr zehn los, sonst fährt kein Bus mehr. Ich hätte das sagen sollen.
Leyla: Mir geht es nicht darum, dass alle gleich lange bleiben. Ich möchte vorher wissen, wer den Raum übergibt. Könntest du an Tagen mit frühem Aufbruch den Aufbau verbindlich übernehmen?
Jonas: Ja, wenn ich mich bis Donnerstag eintragen kann. Für den Abschluss kann ich niemanden versprechen. Vielleicht sollten die beiden Eingetragenen kurz bestätigen, dass der Raum fertig ist.
Leyla: Das wäre hilfreich. Lass uns nach zwei Terminen schauen, ob die Aufteilung funktioniert. Wenn jemand ausfällt, brauchen wir außerdem eine Nachricht, bevor alle weg sind.""")
        ),
        collocations = listOf(
            Collocation("eine Erwartung offenlegen", "Wir sollten unsere Erwartungen offenlegen, statt sie vorauszusetzen."),
            Collocation("Verantwortung übernehmen", "Ich übernehme Verantwortung für den Aufbau."),
            Collocation("eine Absprache einhalten", "Bei Krankheit kann ich die Absprache nicht einhalten und melde mich rechtzeitig."),
            Collocation("ein Missverständnis ausräumen", "Eine Rückfrage kann das Missverständnis ausräumen."),
            Collocation("auf ein Anliegen eingehen", "Der Vorschlag geht auf Jonas' Anliegen ein."),
            Collocation("eine Vertretung organisieren", "Wer ausfällt, fragt rechtzeitig an, ob sich eine Vertretung organisieren lässt.")
        ),
        grammar = listOf(
            GrammarFocus("Beobachtung statt Unterstellung", "Vergangenes mit konkretem Subjekt und Zeitbezug benennen; Vermutungen ausdrücklich markieren.", "An zwei Samstagen blieben Tassen stehen. Ich hatte angenommen, dass alle bis zum Ende bleiben würden."),
            GrammarFocus("Höfliche Klärung im Konjunktiv II", "Könntest du plus Infinitiv eröffnet eine verhandelbare Bitte; eine offene Frage lässt die Antwort frei.", "Könntest du den Aufbau übernehmen? Was wäre für dich zeitlich möglich?"),
            GrammarFocus("Bedingung und Zuständigkeit", "Falls markiert einen möglichen Fall; wer leitet eine allgemeine Zuständigkeitsregel ein.", "Falls jemand ausfällt, informiert die Person die Gruppe. Wer früher geht, klärt vorher die Übergabe.")
        ),
        comprehension = listOf(
            ComprehensionItem("u02-read", "Welche Beobachtung ist belegt, welche Deutung nicht?", "Tassen und Abfallsäcke blieben nach zwei Treffen stehen, Leyla räumte auf. Dass den anderen ihre Zeit egal sei, ist eine Deutung, keine belegte Absicht."),
            ComprehensionItem("u02-read", "Worin unterscheiden sich die beiden Lesarten der alten Absprache?", "Leyla erwartete gemeinsames Bleiben bis zum Ende; Jonas verstand Verantwortung als Hilfe während des Cafés, etwa beim Aufbau."),
            ComprehensionItem("u02-read", "Was ist im Vorschlag noch nicht geklärt?", "Ob alle passende Dienste finden, wie Vertretung tatsächlich gesichert wird und ob die neue Verteilung in der Praxis funktioniert."),
            ComprehensionItem("u02-listen", "Warum nimmt Leyla ihre Chat-Aussage zurück?", "Sie kann die unterstellte Gleichgültigkeit nicht wissen und trennt sie deshalb von ihrer konkreten Beobachtung und ihrem Ärger."),
            ComprehensionItem("u02-listen", "Was sagt Jonas verbindlich zu und was ausdrücklich nicht?", "Er kann den Aufbau übernehmen, wenn er sich bis Donnerstag eintragen kann. Eine Person für den Abschluss kann er nicht versprechen."),
            ComprehensionItem("u02-listen", "Wann wird die Vereinbarung überprüft?", "Nach zwei Caféterminen; zusätzlich wird eine rechtzeitige Nachricht bei Ausfällen benötigt.")
        ),
        tasks = listOf(
            PracticeTask("u02-controlled", ActivityKind.CONTROLLED,
                "Ersetze die Unterstellung Du drückst dich immer durch eine Beobachtung aus dem Text. Formuliere eine höfliche Bitte um Aufbauhilfe. Ergänze eine Falls-Regel für Ausfälle und nenne, was Jonas nicht zugesagt hat.",
                "Keine immer-Verallgemeinerung; konkrete Beobachtung; Könntest du mit Infinitiv; Verbendstellung im Falls-Satz; Zusage nicht ausweiten.",
                "Nach den letzten beiden Treffen blieben Tassen und Abfallsäcke stehen. Könntest du am Freitag den Aufbau übernehmen? Falls du ausfällst, informiere bitte vorher die Gruppe. Jonas hat keine Besetzung für den Abschluss zugesagt.",
                "Die Beobachtung enthält keine Vermutung über Motive. Verwechsle bitten nicht mit befehlen: Die tatsächliche Antwort auf die Bitte muss noch ausgehandelt werden."),
            PracticeTask("u02-speaking", ActivityKind.SPEAKING,
                "Eröffne ohne Manuskript eine zweiminütige Vereinsbesprechung. Beschreibe den Konflikt fair, erkläre zwei unterschiedliche Bedürfnisse und schlage einen nächsten Schritt vor. Lass Raum für Widerspruch.",
                "Beide Lesarten; keine Schuldzuweisung; konkrete Verteilung mit Prüfzeitpunkt; Unsicherheit benennen.",
                "Unsere bisherige Absprache war offenbar zu ungenau. Leyla brauchte eine verlässliche Übergabe, während Jonas seinen Aufbau als Beitrag verstanden hat und den letzten Bus erreichen musste. Ich schlage vor, Aufbau und Abschluss getrennt einzutragen. Bis Donnerstag sollte sichtbar sein, wer welchen Dienst übernimmt. Damit ist noch nicht geklärt, ob alle einen passenden Termin finden. Können wir das heute prüfen und die Verteilung nach zwei Caféterminen gemeinsam auswerten?",
                "Beide Anliegen erscheinen als legitim. Der Vorschlag benennt einen Prüfpunkt, statt Harmonie zu behaupten. Ergänze im Gespräch, wie bei Ausfall rechtzeitig Hilfe angefragt wird."),
            PracticeTask("u02-interaction", ActivityKind.INTERACTION,
                "Führe mit einem realen Partner ein Gespräch über eine unklare Aufgabenverteilung. Er beginnt mit: Am Ende bleibt doch wieder alles an mir hängen. Paraphrasiere, frage nach einer konkreten Situation und verhandle eine überprüfbare Absprache. Der Partner darf einen Vorschlag ablehnen.",
                "Auf tatsächliche Antworten reagieren; Emotion anerkennen ohne Motive zu erfinden; Zuständigkeit und Rückmeldung gemeinsam vereinbaren.",
                "Du befürchtest also, dass du wieder ohne Absprache einspringen musst. An welche Situation denkst du besonders? Wenn es um die Übergabe geht, könnten wir vor jedem Termin zwei Personen benennen. Welche Aufgabe wäre für dich realistisch? Wenn du den Abschluss nicht übernehmen kannst, suchen wir eine andere Verteilung und halten fest, wer den Raum bestätigt.",
                "Die offene Frage verhindert, dass du eine fertige Lösung überstülpst. Eine Partnerablehnung ist kein Fehler; entscheidend ist, ob du darauf eingehst. Ein vorgeführter Dialog ersetzt keine beobachtete Interaktion."),
            PracticeTask("u02-writing", ActivityKind.WRITING,
                "Schreibe eine neutrale Ergebnisnachricht von 160–200 Wörtern an den Verein. Trenne bestehende Fakten, vorgeschlagene Regeln und offene Fragen. Halte fest, wer noch zustimmen muss und wann geprüft wird.",
                "Keine erfundene Einigung; klare Statuswörter; Verantwortlichkeit, Frist und Überprüfung; respektvoller Ton.",
                "Liebe Vereinsmitglieder,\n\nunsere bisherige Regel zur gemeinsamen Verantwortung wurde unterschiedlich verstanden. Nach zwei Treffen blieben Aufgaben offen, die Leyla am nächsten Morgen übernahm. Jonas hatte bereits aufgebaut und musste seinen letzten Bus erreichen. Daraus sollten wir keine pauschalen Urteile über die Einsatzbereitschaft einzelner Personen ableiten.\n\nAls Vorschlag liegt nun vor, Aufbau und Abschluss getrennt zu planen. Jeweils zwei Personen könnten sich bis Donnerstag für den Abschluss eintragen und anschließend kurz bestätigen, dass der Raum übergeben werden kann. Wer früher gehen muss, sollte dies vorher mitteilen. Bei einem Ausfall wäre rechtzeitig in der Gruppe nach Vertretung zu fragen; eine Anfrage bedeutet allerdings noch keine gesicherte Zusage.\n\nBitte meldet zurück, welche Zeiten und Aufgaben für euch realistisch sind. Können Personen mit Betreuungspflichten stattdessen verbindlich beim Aufbau helfen? Wir müssen noch gemeinsam klären, ob diese Verteilung alle angemessen berücksichtigt. Die betroffenen Mitglieder sollen den konkreten Diensten ausdrücklich zustimmen.\n\nNach zwei weiteren Caféterminen soll geprüft werden, ob die Übergabe tatsächlich funktioniert und ob die Aufgaben ausgewogen verteilt sind. Eine endgültige Zustimmung aller zur neuen Regel liegt noch nicht vor.\n\nViele Grüße", 
                "Die Nachricht trennt Beobachtung, Vorschlag und offene Zustimmung. Die Rückmeldefrage macht unterschiedliche Beiträge sichtbar. Gut ist die Grenze zwischen Vertretung anfragen und Vertretung zusagen. Schreibe nicht Wir haben beschlossen, solange die Zustimmung fehlt."),
            PracticeTask("u02-mediation", ActivityKind.MEDIATION,
                "Erkläre einem neu hinzugekommenen Mitglied in 90–120 Wörtern, warum es Streit gab und was jetzt geklärt werden soll. Vermeide die abwertenden Chat-Formulierungen. Stelle beide Perspektiven gleich sorgfältig dar.",
                "Ursache als unklare Erwartung; Bus und zusätzliche Arbeit korrekt; vorgeschlagene Lösung nicht als fertigen Beschluss ausgeben.",
                "Die Gruppe hatte nur allgemein vereinbart, gemeinsam für den Raum verantwortlich zu sein. Leyla dachte, dass alle bis zum Schluss bleiben. Zweimal musste sie am nächsten Morgen zusätzlich aufräumen. Jonas hatte vorher aufgebaut und musste wegen seines letzten Busses früher gehen. Er dachte, damit sei sein Beitrag geleistet. Jetzt wird vorgeschlagen, Aufbau und Abschluss getrennt zu planen. Noch ist zu klären, wer passende Zeiten hat und wie bei Ausfällen Hilfe organisiert wird. Nach zwei weiteren Treffen soll geprüft werden, ob das besser funktioniert. Es geht also um verlässliche Absprachen, nicht darum, jemandem Gleichgültigkeit zu unterstellen.",
                "Die Zusammenfassung bewahrt beide Interessen und lässt die Schuldfrage offen. Das neue Mitglied braucht den Ablauf, nicht jedes Detail des Konflikts. Frage, welche Aufgabe es sich vorstellen könnte, ohne eine Pflicht zu unterstellen."),
            PracticeTask("u02-transfer", ActivityKind.TRANSFER,
                "Übertrage das Prinzip auf ein Engineering-Team: Eine Kollegin prüft ständig fremde Änderungen, obwohl gemeinsam Verantwortung vereinbart wurde. Andere rechnen ihre Testarbeit bereits als Beitrag an. Formuliere eine neutrale Gesprächseröffnung und eine überprüfbare Übergaberegel. Ein echter Transfernachweis benötigt einen separat erstellten ungesehenen Fall.",
                "Beobachtung von Absicht trennen; unterschiedliche Beiträge sichtbar machen; klare Zuständigkeit ohne fachliche Schuldbehauptung.",
                "In den letzten beiden Durchläufen hat nur eine Person die Änderungen geprüft. Ich vermute, dass wir Verantwortung unterschiedlich verstehen: Manche meinen Tests, andere die abschließende Prüfung. Können wir vor dem nächsten Durchlauf festlegen, wer Tests und wer Prüfung übernimmt und wie eine offene Prüfung sichtbar übergeben wird?",
                "Die soziale Strategie passt nun zu einem technischen Arbeitsablauf. Erfinde keinen Qualitätsmangel als Ursache. Weil dieser Fall sichtbar ist, ist er Übung und kein ungesehener Leistungsnachweis."),
            PracticeTask("u02-review", ActivityKind.DELAYED_REVIEW,
                "Nach mindestens sieben Tagen: Rekonstruiere ohne Text den Unterschied zwischen Beobachtung und Deutung. Reagiere spontan auf einen neuen Vorwurf aus einem Coaching-Kontext, paraphrasiere das Anliegen und vereinbare einen prüfbaren nächsten Schritt. Ein Prüfer liefert den neuen Vorwurf erst beim Versuch.",
                "Kein auswendig gelernter Dialog; Rückfrage passend zur Antwort; Zuständigkeit und Prüfzeitpunkt; mindestens zwei Kollokationen korrekt.",
                "Wenn du sagst, niemand unterstütze dich, möchte ich zuerst verstehen, was konkret passiert ist. Welche Unterstützung hattest du erwartet? Wir könnten diese Erwartung offenlegen und eine Absprache treffen, die du beim nächsten Termin überprüfen kannst. Den Schritt bestimmst du selbst.",
                "Die Frage wahrt Autonomie statt einen Rat aufzudrängen. Prüfe, ob du eine Absprache einhalten und auf ein Anliegen eingehen korrekt abrufen kannst. Vergleiche erst danach mit den Quellen.", 7)
        )
    )

    val engineeringRelease = AuthoredContent(
        objectives = listOf(
            "Messwert, Interpretation und ungetestete Annahme in einer Freigabeentscheidung trennen.",
            "Eine bedingte Empfehlung mit Schwelle, Verantwortlichkeit und Rückfalloption formulieren.",
            "Einen technischen Einwand spontan klären, ohne Unsicherheit zu verschleiern.",
            "Ein Risiko adressatengerecht an nichttechnische Beteiligte vermitteln, ohne Sicherheit zu garantieren."
        ),
        sources = listOf(
            LearningSource("u03-read", "Ein Update vor dem Wartungsfenster", SourceKind.READING,
                """Ein Betrieb möchte die Steuerungssoftware einer Verpackungslinie aktualisieren. Das Update soll seltene Verzögerungen bei der Erfassung von Sensordaten verringern. Im Labortest mit 10.000 Zyklen lag die gemessene Fehlerquote bei 0,2 Prozent; mit der bisherigen Version waren es unter denselben Laborbedingungen 0,8 Prozent. Der Test wurde allerdings nur bei Raumtemperatur durchgeführt. Für die höhere Temperatur im Sommerbetrieb liegen noch keine Daten vor. Aus dem Ergebnis lässt sich daher eine Verbesserung im getesteten Bereich, aber keine allgemeine Betriebssicherheit ableiten.

Das geplante Wartungsfenster dauert am Freitag von 18 bis 20 Uhr. Laut Testprotokoll benötigt die Installation etwa 25 Minuten. Eine Rückkehr zur bisherigen Version würde weitere 40 Minuten beanspruchen. Diese Angaben sind Schätzungen aus dem Labor, keine garantierten Höchstzeiten. Die Produktionsleitung möchte vermeiden, dass die Linie am Montag verspätet startet. Die Qualitätssicherung hält einen begrenzten Pilotbetrieb für vertretbar, sofern die alte Konfiguration gesichert und eine verantwortliche Person für den Rückbau benannt wird.

Als vorläufiges Abbruchkriterium wird vorgeschlagen, den Piloten bei mehr als drei Erfassungsfehlern innerhalb von 1.000 Zyklen zu stoppen. Dieser Wert ist ein Vorschlag für die Pilotüberwachung und weder eine bereits genehmigte Grenze noch eine gesetzliche Vorgabe. Ungeklärt ist außerdem, wer den Pilotlauf am Wochenende beobachtet und ob ein realistischer Temperaturtest vor Freitag möglich ist.

Die technische Leiterin verlangt deshalb eine Entscheidungsvorlage mit drei getrennten Punkten: Was wurde tatsächlich nachgewiesen, welche Unsicherheit bleibt und unter welchen Bedingungen wäre ein begrenzter Versuch verantwortbar? Falls die Überwachung nicht gesichert werden kann, soll die Freigabe verschoben werden. Ein besserer Laborwert allein reicht aus ihrer Sicht nicht aus, um die Verantwortung für den realen Betrieb zu übernehmen."""),
            LearningSource("u03-listen", "Rückfrage der Produktionsleitung", SourceKind.LISTENING_TTS,
                """Ich habe verstanden, dass die neue Version im Labor weniger Erfassungsfehler gezeigt hat. Bedeutet das auch, dass wir am Montag sicher ohne Unterbrechung starten können? Genau diese Zusage brauche ich für die Planung. Wenn wir sie nicht geben können, möchte ich wissen, welche Alternative es gibt.

Die Installation klingt mit fünfundzwanzig Minuten kurz. Zusammen mit vierzig Minuten für einen möglichen Rückbau sind das rechnerisch fünfundsechzig Minuten. Aber ich höre auch, dass die Zeiten nicht garantiert sind. Bitte behandelt den verbleibenden Teil des zweistündigen Wartungsfensters nicht automatisch als ausreichende Reserve; Prüfung und Entscheidung brauchen ebenfalls Zeit.

Für einen Piloten wäre ich offen, wenn eine verantwortliche Person die Überwachung verbindlich übernimmt und vorher klar ist, wann zurückgebaut wird. Wer ist am Wochenende erreichbar? Und wer darf den Stopp auslösen? Solange diese Fragen offen sind, plane ich lieber mit der bisherigen Version weiter. Eine Verschiebung wäre unangenehm, aber eine unbegründete Sicherheitszusage wäre für mich schlimmer.""")
        ),
        collocations = listOf(
            Collocation("eine Freigabe erteilen", "Wir können die Freigabe erst nach Klärung der Überwachung erteilen."),
            Collocation("ein Risiko eingrenzen", "Ein Pilot kann das Risiko eingrenzen, aber nicht vollständig beseitigen."),
            Collocation("einen Grenzwert überschreiten", "Wenn die Fehlerzahl den vereinbarten Grenzwert überschreitet, wird gestoppt."),
            Collocation("eine Annahme überprüfen", "Der Temperaturtest soll eine bisher ungetestete Annahme überprüfen."),
            Collocation("einen Rückbau vorsehen", "Für den Piloten müssen wir einen Rückbau vorsehen."),
            Collocation("unter Vorbehalt zustimmen", "Die Qualitätssicherung stimmt einem Pilotbetrieb nur unter Vorbehalt zu.")
        ),
        grammar = listOf(
            GrammarFocus("Passiv und Verantwortlichkeit", "Passiv hebt den Vorgang hervor. In einer Handlungsregel muss zusätzlich klar sein, wer handelt.", "Die Konfiguration wird gesichert. Die benannte Fachperson sichert sie vor der Installation."),
            GrammarFocus("Bedingung mit sofern und falls", "Sofern bezeichnet eine notwendige Voraussetzung; falls einen möglichen Ereignisfall. Beide verlangen Verbendstellung.", "Ein Pilot wäre vertretbar, sofern die Überwachung gesichert ist. Falls der Grenzwert überschritten wird, stoppt die verantwortliche Person den Lauf."),
            GrammarFocus("Evidenz sprachlich begrenzen", "Unter den getesteten Bedingungen und bisher verhindern eine unzulässige Verallgemeinerung; Konjunktiv II markiert Empfehlungen.", "Unter den getesteten Bedingungen sank die Fehlerquote. Ohne Temperaturtest würde ich keine allgemeine Freigabe empfehlen.")
        ),
        comprehension = listOf(
            ComprehensionItem("u03-read", "Welche Verbesserung ist belegt und welche nicht?", "Bei Raumtemperatur sank die Laborfehlerquote von 0,8 auf 0,2 Prozent. Eine Verbesserung bei sommerlichen Temperaturen oder allgemeine Betriebssicherheit ist nicht nachgewiesen."),
            ComprehensionItem("u03-read", "Was bedeutet das vorgeschlagene Abbruchkriterium genau?", "Mehr als drei Erfassungsfehler innerhalb von 1.000 Zyklen sollen zum Stopp führen; drei allein erfüllen mehr als drei nicht. Es ist nur ein Vorschlag, keine genehmigte oder gesetzliche Grenze."),
            ComprehensionItem("u03-read", "Welche organisatorischen Fragen sind offen?", "Wochenendüberwachung und Verantwortlichkeit, Stoppbefugnis beziehungsweise verbindlicher Rückbau sowie die Möglichkeit eines Temperaturtests müssen geklärt werden."),
            ComprehensionItem("u03-listen", "Warum garantieren 25 plus 40 Minuten keinen rechtzeitigen Abschluss?", "Die 65 Minuten sind Schätzungen; Prüfung und Entscheidung benötigen ebenfalls Zeit. Eine ausreichende Reserve ist nicht nachgewiesen."),
            ComprehensionItem("u03-listen", "Welche Zusage fordert die Produktionsleitung, und kann der Laborwert sie tragen?", "Sie fragt nach einem sicheren unterbrechungsfreien Montagstart. Der begrenzte Laborwert kann diese Zusage nicht tragen."),
            ComprehensionItem("u03-listen", "Was ist die vorläufige Alternative zum Pilotbetrieb?", "Mit der bisherigen Version weiterplanen und die Freigabe verschieben, solange Zuständigkeit und Stoppregel offenbleiben.")
        ),
        tasks = listOf(
            PracticeTask("u03-controlled", ActivityKind.CONTROLLED,
                "Begrenze die Aussage Das Update ist sicher auf die tatsächliche Evidenz. Verbinde Ein Pilot wäre vertretbar und Die Überwachung ist gesichert mit sofern. Ersetze Es wird bei Problemen gestoppt durch eine Regel mit benannter Rolle und dem vorgeschlagenen Zahlenkriterium.",
                "Keine Sicherheitsgarantie; sofern mit Verbendstellung; mehr als drei präzise; Vorschlagsstatus und zuständige Rolle erhalten.",
                "Unter den getesteten Laborbedingungen bei Raumtemperatur war die Fehlerquote geringer. Ein Pilot wäre vertretbar, sofern die Überwachung gesichert ist. Als Regel wird vorgeschlagen: Die vorab benannte verantwortliche Fachperson stoppt den Pilotlauf bei mehr als drei Erfassungsfehlern innerhalb von 1.000 Zyklen.",
                "Die Grenze ist sprachlich exakt und die Verantwortung sichtbar. Benannte Fachperson ist noch keine tatsächlich erfolgte Benennung; der Vorschlag muss vor einer Freigabe verbindlich werden."),
            PracticeTask("u03-speaking", ActivityKind.SPEAKING,
                "Gib ohne Manuskript eine zweiminütige Empfehlung an die technische Leiterin. Trenne Befund, Unsicherheit und Bedingungen. Nenne eine Alternative, falls eine Bedingung bis Freitag unerfüllt bleibt.",
                "Beide Laborquoten korrekt; Temperaturgrenze; Überwachung und Rückbau; keine Garantie; klare bedingte Entscheidung.",
                "Im Labor sank die Fehlerquote bei Raumtemperatur von 0,8 auf 0,2 Prozent. Das ist ein positives Signal, sagt aber noch nichts über den Sommerbetrieb aus. Ich würde daher nur einen begrenzten Piloten empfehlen, sofern Überwachung, Stoppbefugnis und gesicherte Rückkehr zur bisherigen Version verbindlich geklärt sind. Den vorgeschlagenen Grenzwert müssten wir vorher genehmigen. Können wir die Überwachung bis Freitag nicht besetzen, sollten wir verschieben und die bisherige Version beibehalten. Einen störungsfreien Montag kann ich aus den vorliegenden Daten nicht zusagen.",
                "Die Empfehlung verbindet Nutzen mit einer echten Ausweichentscheidung. Ergänze, wer die offenen Punkte bis wann klärt; passe nicht aus Zeitdruck die Bedeutung von sicher an."),
            PracticeTask("u03-interaction", ActivityKind.INTERACTION,
                "Ein realer Partner übernimmt die Produktionsleitung und fordert: Dann garantieren Sie mir bitte den Montagstart. Antworte verständlich, kläre das Planungsbedürfnis und verhandle eine Alternative. Der Partner stellt eine unvorbereitete Frage zu Zeitreserve oder Stoppbefugnis.",
                "Unbelegbare Zusage ausdrücklich ablehnen, ohne auszuweichen; Rückfrage; bedingte Alternative; echte Anschlussreaktion.",
                "Eine Garantie kann ich aus diesem Test nicht ableiten. Sie brauchen für Montag vor allem einen belastbaren Plan, richtig? Wenn die Überwachung und der Rückbau nicht verbindlich geklärt sind, wäre die bisherige Version die planbarere Alternative. Welche späteste Entscheidungszeit brauchen Sie? Die geschätzten 65 Minuten allein belegen keine ausreichende Reserve, weil Prüfung und Entscheidung hinzukommen.",
                "Die Grenze wird klar statt beschwichtigend benannt. Gehe auf die Antwort zur Entscheidungszeit ein. Eine simulierte Rolle kann das üben, zählt aber nicht als beobachtete unabhängige Interaktion."),
            PracticeTask("u03-writing", ActivityKind.WRITING,
                "Verfasse eine Entscheidungsvorlage von 200–250 Wörtern mit den Abschnitten Befund, Grenzen, Bedingungen und Empfehlung. Halte Vorschlag und genehmigte Regel auseinander. Weise offene Verantwortlichkeiten ausdrücklich aus.",
                "Adressatengerechte Struktur; präzise Zahlen und Testumfang; keine erfundene Genehmigung; nachvollziehbare Verschiebungsoption.",
                "Befund: In 10.000 Laborzyklen bei Raumtemperatur lag die Fehlerquote der neuen Version bei 0,2 statt 0,8 Prozent. Unter diesen Bedingungen wurde somit eine Verbesserung beobachtet. Ziel des Updates ist, seltene Verzögerungen bei der Erfassung von Sensordaten zu verringern.\n\nGrenzen: Für höhere Temperaturen fehlen Daten. Die geschätzten 25 Minuten für die Installation und 40 Minuten für einen möglichen Rückbau ergeben rechnerisch 65 Minuten, sind aber keine garantierten Höchstzeiten. Auch Prüfung und Entscheidung benötigen Zeit. Das zweistündige Wartungsfenster allein belegt deshalb keine ausreichende Reserve.\n\nBedingungen: Vor einem Piloten müssen Sicherung der bisherigen Konfiguration, Wochenendüberwachung, Stoppbefugnis und Rückbauverantwortung verbindlich zugeordnet werden. Mehr als drei Erfassungsfehler innerhalb von 1.000 Zyklen sind als Abbruchkriterium vorgeschlagen, aber noch nicht genehmigt. Vor der Installation sollte die technische Leitung die Zuständigkeiten und den genehmigten Grenzwert dokumentieren lassen. Zusätzlich ist zu klären, ob ein realistischer Temperaturtest noch vor Freitag möglich ist. Eine positive Antwort darf nicht vorweggenommen werden.\n\nEmpfehlung: Ein begrenzter Pilot kommt nur nach Klärung dieser Punkte infrage. Ich schlage eine verbindliche Entscheidung vor Beginn des Wartungsfensters vor, damit die Produktionsleitung mit einer eindeutigen Alternative planen kann. Bleibt die Überwachung unbesetzt oder ist die Rückkehr zur bisherigen Version nicht gesichert, sollte die Freigabe verschoben werden. Der Laborbefund rechtfertigt keine Zusage eines störungsfreien Montagstarts.", 
                "Die Vorlage begrenzt den Befund auf die Testbedingungen und weist die vorgeschlagene Entscheidungsfrist als eigenen Vorschlag aus. Der Temperaturtest bleibt offen. Verwandle geschätzte Laufzeiten nicht in garantierte Höchstwerte und den Stoppvorschlag nicht in eine bereits genehmigte Regel."),
            PracticeTask("u03-mediation", ActivityKind.MEDIATION,
                "Erkläre einer nichttechnischen Kollegin in 100–140 Wörtern, warum ein besseres Testergebnis noch keine Freigabe bedeutet. Erhalte die wichtigen Zahlen und erkläre Pilot sowie Rückbau ohne Fachwortketten.",
                "Nutzen nicht verschweigen; begrenzte Testbedingungen; Vorschlagsstatus; keine Sicherheitsgarantie; klare Alternative.",
                "Die neue Software hat im Labor weniger Fehler gemacht: 0,2 statt 0,8 Prozent. Getestet wurde aber nur bei Raumtemperatur. Wir wissen noch nicht, wie sie bei größerer Hitze arbeitet. Deshalb geht es höchstens um einen begrenzten Probelauf, nicht um eine allgemeine Zusage. Vorher muss feststehen, wer den Lauf beobachtet und bei Problemen zur bisherigen Software zurückkehrt. Dafür gibt es einen vorgeschlagenen Stoppwert, der noch genehmigt werden muss. Die Zeitangaben für Installation und Rückkehr sind nur Schätzungen. Wenn wir die Betreuung nicht zuverlässig organisieren können, verschieben wir das Update. Ein besseres Laborergebnis allein garantiert also keinen störungsfreien Start am Montag.",
                "Die Erklärung hält die Einschränkungen ohne unnötigen Jargon fest. Rückkehr ist hier verständlicher als Rollback. Frage nach, ob der Unterschied zwischen Laborbefund und betrieblicher Zusage klar ist."),
            PracticeTask("u03-transfer", ActivityKind.TRANSFER,
                "Neuer Übungskontext Coaching: Ein Team berichtet nach zwei Workshops weniger Konflikte, befragt wurden aber nur Teilnehmende unmittelbar danach. Formuliere, was daraus folgt, was offenbleibt und wie eine spätere Überprüfung aussehen könnte. Ein Prüfer muss für den Nachweis einen anderen ungesehenen Fall bereitstellen.",
                "Keine technische Stoppregel unpassend übertragen; Evidenzgrenze und zeitliche Nachhaltigkeit trennen; Autonomie der Beteiligten erhalten.",
                "Die unmittelbare Rückmeldung der Teilnehmenden ist positiv. Sie belegt noch keine dauerhafte Verbesserung im gesamten Team. Ich würde mit den Beteiligten vereinbaren, nach einigen Wochen konkrete Konfliktsituationen und die Sicht nicht teilnehmender Personen einzubeziehen. Welche Veränderung sie prüfen möchten, sollte das Team selbst mitbestimmen.",
                "Übertragen wird die Grenze der Evidenz, nicht der technische Wortschatz. Vermeide eine garantierte Wirkung. Der offen gezeigte Fall ist keine ungesehene Transferprüfung."),
            PracticeTask("u03-review", ActivityKind.DELAYED_REVIEW,
                "Mindestens sieben Tage nach dem ersten unabhängigen Versuch: Erläutere ohne Vorlage Befund, Einschränkung und bedingte Entscheidung. Ein Mensch legt einen neuen Fall aus einem sozialen Projekt mit anderen Zahlen und Bedingungen vor. Reagiere auf eine Rückfrage und korrigiere anschließend einen früheren Präzisionsfehler in einem neuen Beispiel.",
                "Neue Angaben statt alte Zahlen reproduzieren; mindestens zwei passende Kollokationen; Verantwortung und Alternative explizit; unabhängige Produktion vor Feedback.",
                "Das positive Ergebnis gilt nur für die untersuchte Gruppe. Bevor wir eine breitere Freigabe erteilen, sollten wir die Annahme überprüfen, dass andere Gruppen ähnlich profitieren. Ein begrenzter Versuch könnte das Risiko eingrenzen, sofern Betreuung und Auswertung gesichert sind. Andernfalls wäre eine Verschiebung begründbar.",
                "Der Abstand prüft nachhaltigen Abruf, nicht die Erinnerung an 0,2 Prozent. Bewerte alle zehn Dimensionen am neuen Artefakt. Ein fehlender oder schwacher Bereich darf nicht durch gute Zahlenpräzision ausgeglichen werden.", 7)
        )
    )
}
