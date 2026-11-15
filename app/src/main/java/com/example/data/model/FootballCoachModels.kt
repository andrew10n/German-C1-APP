package com.example.data.model

enum class CoachTermCategory(val label: String) {
    OFFENSIV("Offensivspiel"),
    DEFENSIV("Defensivverbund"),
    UMSCHALTEN("Umschaltspiel"),
    KOMMUNIKATION("Coaching-Sprache")
}

data class FootballCoachTerm(
    val id: String,
    val term: String,
    val category: CoachTermCategory,
    val tacticalMeaning: String,
    val pitchCommand: String,
    val avoidInstead: String
)

data class TacticalScenario(
    val id: String,
    val title: String,
    val matchPhase: String,
    val situationDescription: String,
    val options: List<String>,
    val correctIndex: Int,
    val coachingExplanation: String,
    val keyCommandToShout: String
)

data class DrillCommunicationPattern(
    val id: String,
    val drillTitle: String,
    val ageGroupFocus: String,
    val setupSummary: String,
    val liveCommands: List<String>,
    val avoidPhrase: String,
    val positiveCorrection: String
)

object FootballCoachU15Data {

    val terms: List<FootballCoachTerm> = listOf(
        FootballCoachTerm(
            id = "fc_halbraum",
            term = "den Halbraum besetzen",
            category = CoachTermCategory.OFFENSIV,
            tacticalMeaning = "Den vertikalen Korridor zwischen Zentrum und Außenbahn nutzen, um Diagonalpässe zwischen die gegnerischen Ketten zu ermöglichen.",
            pitchCommand = "Achter, schieb in den linken Halbraum — zwischen Innen- und Außenverteidiger!",
            avoidInstead = "Geh mal ein bisschen mehr nach halb-links!"
        ),
        FootballCoachTerm(
            id = "fc_abkippen",
            term = "abkippen (der Sechser)",
            category = CoachTermCategory.OFFENSIV,
            tacticalMeaning = "Der defensive Mittelfeldspieler lässt sich im Spielaufbau zwischen oder neben die Innenverteidiger fallen, um Überzahl gegen das gegnerische Pressing zu schaffen.",
            pitchCommand = "Jonas, kipp zwischen die Innenverteidiger ab, dann haben wir drei gegen zwei im Aufbau!",
            avoidInstead = "Lauf mal ganz nach hinten zum Torwart!"
        ),
        FootballCoachTerm(
            id = "fc_schnittstelle",
            term = "der Schnittstellenpass",
            category = CoachTermCategory.OFFENSIV,
            tacticalMeaning = "Ein präziser Pass genau in die Lücke zwischen zwei gegnerischen Abwehrspielern, idealerweise in den Lauf des startenden Stürmers.",
            pitchCommand = "Blick hoch, Timing abwarten und den Ball scharf in die Schnittstelle spielen!",
            avoidInstead = "Schieß einfach irgendwie durch die Mitte!"
        ),
        FootballCoachTerm(
            id = "fc_spielverlagerung",
            term = "die Spielverlagerung",
            category = CoachTermCategory.OFFENSIV,
            tacticalMeaning = "Schneller Seitenwechsel über zwei bis drei Stationen auf die ballferne Seite, wenn der Gegner das Zentrum oder eine Flanke komplett zugestellt hat.",
            pitchCommand = "Hier ist es eng — mit zwei Kontakten über die Sechs auf die andere Seite verlagern!",
            avoidInstead = "Spiel den Ball ganz weit rüber!"
        ),
        FootballCoachTerm(
            id = "fc_durchsichern",
            term = "herausrücken & durchsichern",
            category = CoachTermCategory.DEFENSIV,
            tacticalMeaning = "Sticht ein Verteidiger auf den ballführenden Gegner vor, rücken die übrigen Spieler der Viererkette sofort diagonal ein, um den Raum dahinter zu schließen.",
            pitchCommand = "Paul rückt raus — Kette schiebt sofort ballnah durch und sichert die Tiefe!",
            avoidInstead = "Bleibt alle hinten stehen und passt auf!"
        ),
        FootballCoachTerm(
            id = "fc_kompaktheit",
            term = "die Kompaktheit im Verbund",
            category = CoachTermCategory.DEFENSIV,
            tacticalMeaning = "Abstände zwischen Abwehr-, Mittelfeld- und Angriffslinie eng halten (ca. 25–30 Meter), sodass keine bespielbaren Zwischenräume entstehen.",
            pitchCommand = "Abstände verkürzen! Stürmer fallen mit zurück, wir halten den Block kompakt!",
            avoidInstead = "Alle nach hinten rennen!"
        ),
        FootballCoachTerm(
            id = "fc_gegenpressing",
            term = "das Gegenpressing (3-Sekunden-Regel)",
            category = CoachTermCategory.UMSCHALTEN,
            tacticalMeaning = "Unmittelbar nach eigenem Ballverlust greifen die ballnahen Spieler den Ballführenden an, um den Gegner zum Fehlpass zu zwingen, bevor er kontern kann.",
            pitchCommand = "Ball weg — sofort zu zweit anlaufen, Passwege zustellen, nicht fallen lassen!",
            avoidInstead = "Menno, schon wieder verloren — zurück!"
        ),
        FootballCoachTerm(
            id = "fc_tiefe_sichern",
            term = "die Tiefe sichern (Restverteidigung)",
            category = CoachTermCategory.UMSCHALTEN,
            tacticalMeaning = "Bei eigenem Angriff bleiben zwei Verteidiger und ein Sechser so gestaffelt, dass gegnerische lange Konterbälle sofort abgefangen werden.",
            pitchCommand = "Restverteidigung wach bleiben: Zwei Mann sichern hinter dem Ball die Tiefe!",
            avoidInstead = "Nicht alle nach vorne laufen!"
        ),
        FootballCoachTerm(
            id = "fc_vororientierung",
            term = "der Schulterblick (Vororientierung)",
            category = CoachTermCategory.KOMMUNIKATION,
            tacticalMeaning = "Noch bevor der Ball ankommt, schaut der Spieler kurz über beide Schultern, um Gegenspieler und freie Räume wahrzunehmen.",
            pitchCommand = "Schulterblick vor dem ersten Kontakt — weißt du schon, wo der freie Mann steht?",
            avoidInstead = "Pass auf, hinter dir ist einer!"
        ),
        FootballCoachTerm(
            id = "fc_positiv_coachen",
            term = "positives Handlungsziel setzen",
            category = CoachTermCategory.KOMMUNIKATION,
            tacticalMeaning = "Im Jugendfußball lösen positive Zielbilder ('Kopf hoch, Freilaufweg suchen') klare Aktionen aus, während Verbote ('Nicht bolzen') Spieler hemmen.",
            pitchCommand = "Mutig aufdrehen, ersten Kontakt nach vorne mitnehmen und Tiefe anbieten!",
            avoidInstead = "Mach nicht schon wieder so einen hektischen Fehlpass!"
        )
    )

    val scenarios: List<TacticalScenario> = listOf(
        TacticalScenario(
            id = "scen_1",
            title = "Hohes gegnerisches Pressing im Spielaufbau",
            matchPhase = "1. Halbzeit · Aufbauzone (C-Jugend 11v11)",
            situationDescription = "Der Gegner läuft Ihre beiden Innenverteidiger mit zwei Stürmern und einem Zehner hoch an. Ihr Torwart hat den Ball am Fuß, die Außenverteidiger sind zugestellt.",
            options = listOf(
                "Dem Torwart zurufen, den Ball ab sofort immer unkontrolliert hoch ins Aus zu schlagen.",
                "Den Sechser zwischen die Innenverteidiger abkippen lassen, um eine Dreierkette zu bilden und die Außenverteidiger hochzuschieben.",
                "Beide Innenverteidiger sollen noch enger zum Torwart rücken.",
                "Den Mittelstürmer zurück in den eigenen Strafraum beordern."
            ),
            correctIndex = 1,
            coachingExplanation = "Durch das Abkippen des Sechsers entsteht im Aufbau eine 3-gegen-2-Überzahl. Gleichzeitig ziehen die hochgeschobenen Außenverteidiger die gegnerischen Flügelspieler auseinander.",
            keyCommandToShout = "Sechs kippt ab! Außenverteidiger hoch und breit — Überzahl im Aufbau ausspielen!"
        ),
        TacticalScenario(
            id = "scen_2",
            title = "Ballverlust im gegnerischen Halbraum",
            matchPhase = "Umschaltmoment Offensive -> Defensive",
            situationDescription = "Ihr Zehner verliert den Ball am gegnerischen Strafraum. Drei Ihrer Offensivspieler stehen in unmittelbarer Ballnähe, während der Gegner zum Konter ansetzen will.",
            options = listOf(
                "Alle drei Offensivspieler sollen sofort bis an die eigene Eckfahne zurücksprinten.",
                "Sofortiges ballnahes Gegenpressing für 3–4 Sekunden auslösen, während die Restverteidigung das Zentrum schließt.",
                "Ein taktisches Foul an der Mittellinie in Kauf nehmen.",
                "Die Viererkette bis an die gegnerische Strafraumgrenze vorziehen."
            ),
            correctIndex = 1,
            coachingExplanation = "Wenn drei Spieler ballnah stehen, ist das Zeitfenster direkt nach Ballverlust optimal für Gegenpressing — zumindest um den schnellen Pass nach vorne zu unterbinden.",
            keyCommandToShout = "Sofort drauf, Passweg nach innen zu! Restverteidigung sichert die Tiefe!"
        ),
        TacticalScenario(
            id = "scen_3",
            title = "Durchbruch über den gegnerischen Flügel",
            matchPhase = "Abwehrverhalten im Verbund (Viererkette)",
            situationDescription = "Der gegnerische Rechtsaußen dribbelt an der Seitenlinie auf Ihren Linksverteidiger zu. Im Zentrum lauert ein Stürmer am Elfmeterpunkt.",
            options = listOf(
                "Der Linksverteidiger grätscht sofort mit vollem Risiko in den Zweikampf.",
                "Der Linksverteidiger stellt den Gegner, verzögert das Tempo und lenkt ihn nach außen, während die Kette diagonal in den Strafraum einrückt.",
                "Beide Innenverteidiger laufen gemeinsam zur Seitenlinie, um zu doppeln.",
                "Der Torwart verlässt den Strafraum bis zur Seitenlinie."
            ),
            correctIndex = 1,
            coachingExplanation = "In der U15-Viererkette gilt: 'Stellen statt blind grätschen'. Verzögern gibt dem ballnahen Sechser und der Kette Zeit, den Strafraum sauber zu besetzen.",
            keyCommandToShout = "Stellen und nach außen lenken! Kette schiebt durch und besetzt den Rückraum!"
        ),
        TacticalScenario(
            id = "scen_4",
            title = "Halbzeitansprache nach 0:2-Rückstand durch Konter",
            matchPhase = "Halbzeitpause · Kabinen-Coaching (U15)",
            situationDescription = "Ihre C-Jugend hat 65% Ballbesitz, liegt aber durch zwei lange Konter 0:2 hinten. Die Spieler wirken frustriert und suchen die Schuld beim Schiedsrichter.",
            options = listOf(
                "Die Mannschaft 10 Minuten lang lautstark kritisieren, um Kampfgeist zu erzwingen.",
                "Das starke eigene Ballbesitzspiel anerkennen, ruhig auf die fehlende Restverteidigung hinweisen und zwei klare Anpassungen für Halbzeit 2 vorgeben.",
                "Sofort alle fünf Auswechselspieler ohne taktischen Hinweis einwechseln.",
                "Die zweite Halbzeit komplett auf lange Bälle umstellen."
            ),
            correctIndex = 1,
            coachingExplanation = "14- bis 15-Jährige brauchen in der Pause emotionale Stabilisierung plus konkrete, umsetzbare Lösungen (z. B. Absicherung durch einen Sechser) statt Pauschalvorwürfe.",
            keyCommandToShout = "Kopf hoch, unser Aufbau war stark! In Halbzeit zwei sichert ein Sechser konsequent vor der Kette ab — dann drehen wir das."
        )
    )

    val drills: List<DrillCommunicationPattern> = listOf(
        DrillCommunicationPattern(
            id = "drill_1",
            drillTitle = "4+1 gegen 2 Rondo mit Anschlussaktion",
            ageGroupFocus = "C-Jugend (U14/U15) · Vororientierung & erster Kontakt",
            setupSummary = "10x10m Feld. 4 Außenspieler + 1 Zentrumsspieler gegen 2 Jäger. Nach 6 Kontakten erfolgt ein scharfer Flugball oder Schnittstellenpass auf ein kleines Zieltor.",
            liveCommands = listOf(
                "Vor dem Kontakt über die Schulter schauen — wo ist die offene Gasse?",
                "Körper offen stellen, damit du beide Seiten siehst!",
                "Scharf auf den vorderen Fuß passen, kein Hoppelball!",
                "Nach dem Pass sofort neuen Winkel anbieten!"
            ),
            avoidPhrase = "Nicht immer nur quer spielen! Schnell, schnell!",
            positiveCorrection = "Such nach zwei Kontakten den Blick in die Tiefe — sobald die Gasse aufgeht, spielst du durch!"
        ),
        DrillCommunicationPattern(
            id = "drill_2",
            drillTitle = "Viererkette vs. 5 Angreifer: Verschieben im Verbund",
            ageGroupFocus = "C-Jugend (U15) · Kettenverhalten & Durchsichern",
            setupSummary = "Halbfeld-Spielform. 4 Verteidiger + Torwart gegen 5 Angreifer. Das Trainerteam coacht ausschließlich Abstände, Herausstechen und Tiefe-Absichern.",
            liveCommands = listOf(
                "Ball fliegt — jetzt schieben alle vier gemeinsam auf die Ballseite!",
                "Ballnaher Innenverteidiger rückt raus, Rest bildet das Dreieck dahinter!",
                "Kommuniziert miteinander: 'Ich hab Ball — du sicherst!'",
                "Abstand zwischen den Ketten maximal zehn Meter!"
            ),
            avoidPhrase = "Du stehst völlig falsch! Bleib doch bei deinem Mann!",
            positiveCorrection = "Orientiere dich am Ball und an deinem Nebenmann: Wenn er rausgeht, rückst du zwei Meter nach innen ein!"
        ),
        DrillCommunicationPattern(
            id = "drill_3",
            drillTitle = "Umschaltspiel 6-gegen-6 auf vier Minitore",
            ageGroupFocus = "C-Jugend (U14/U15) · Gegenpressing & Konterabsicherung",
            setupSummary = "35x30m Spielfeld mit zwei Minitoren je Grundlinie. Nach Balleroberung zählt ein Treffer innerhalb von 6 Sekunden doppelt.",
            liveCommands = listOf(
                "Ballgewinn — erster Blick sofort nach vorne in die Tiefe!",
                "Ballverlust — drei Sekunden Vollgas ins Gegenpressing!",
                "Macht das Feld im Ballbesitz breit und tief!",
                "Zentrum dicht machen, lenkt sie auf die Außenbahn!"
            ),
            avoidPhrase = "Rennt nicht so faul zurück! Was soll das?",
            positiveCorrection = "Die ersten drei Schritte nach Ballverlust entscheiden — sofort Druck auf den Ballführenden, dann erobern wir ihn zurück!"
        ),
        DrillCommunicationPattern(
            id = "drill_4",
            drillTitle = "Flügelspiel & Boxbesetzung mit Timings",
            ageGroupFocus = "C-Jugend (U15) · Strafraumbesetzung & Rückraum",
            setupSummary = "Flügelspieler hinterläuft den Außenverteidiger und flankt flach. Drei Angreifer besetzen gestaffelt: erster Pfosten, zweiter Pfosten und Rückraum (Elfmeterpunkt).",
            liveCommands = listOf(
                "Timing beim Einlaufen: Nicht zu früh in die Box starten!",
                "Erster Pfosten, zweiter Pfosten und einer bleibt im Rückraum!",
                "Flanke scharf und flach zwischen Torwart und Abwehr!",
                "Nachsetzen auf den Abpraller — jeder Ball ist heiß!"
            ),
            avoidPhrase = "Warum steht ihr alle auf einem Haufen vor dem Tor?!",
            positiveCorrection = "Staffelt eure Laufwege: Einer zieht zum kurzen Pfosten, einer überläuft hinten und der Achter wartet am Elfmeterpunkt!"
        )
    )
}
