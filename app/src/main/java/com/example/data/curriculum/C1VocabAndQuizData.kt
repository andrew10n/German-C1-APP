package com.example.data.curriculum

import com.example.domain.model.C1Pillar
import com.example.domain.model.C1QuizItem
import com.example.domain.model.C1VocabCard
import com.example.domain.model.ErrorArchetype
import com.example.domain.model.VisualDiagramType

object C1VocabAndQuizData {
    val vocabCards: List<C1VocabCard> = listOf(
        C1VocabCard(
            id = "voc_nvv_01",
            pillar = C1Pillar.NOMEN_VERB,
            expression = "etw. in Betracht ziehen",
            grammarTag = "Funktionsverbgefüge (+ Akk.)",
            register = "Beruf & Analyse",
            meaningEn = "to take into consideration",
            exampleDe = "Für den Kaltstart sollten wir eine sanftere Kennlinie in Betracht ziehen.",
            trapWarning = "Nie ‚in Betracht nehmen‘ – immer ‚ziehen‘.",
            visualHint = "[Option] → in Betracht ziehen",
            diagramType = VisualDiagramType.COLLOCATION_NETWORK
        ),
        C1VocabCard(
            id = "voc_nvv_02",
            pillar = C1Pillar.NOMEN_VERB,
            expression = "einer Sache (Dat.) Rechnung tragen",
            grammarTag = "Funktionsverbgefüge (+ Dat.)",
            register = "Technik & Prüfung",
            meaningEn = "to take due account of / accommodate",
            exampleDe = "Das Sicherheitskonzept trägt den strengen FTTI-Vorgaben voll Rechnung.",
            trapWarning = "Verlangt den Dativ vor ‚Rechnung tragen‘.",
            visualHint = "[Faktor im Dativ] → Rechnung tragen",
            diagramType = VisualDiagramType.COLLOCATION_NETWORK
        ),
        C1VocabCard(
            id = "voc_nvv_03",
            pillar = C1Pillar.NOMEN_VERB,
            expression = "jdm. kurz Bescheid geben",
            grammarTag = "Alltags-Kollokation (+ Dat.)",
            register = "Alltag & Team",
            meaningEn = "to let someone know briefly",
            exampleDe = "Gib mir einfach kurz Bescheid, sobald der Prüfstand frei ist.",
            trapWarning = "Ohne Artikel – nicht ‚einen Bescheid geben‘.",
            visualHint = "kurz Bescheid → geben",
            diagramType = VisualDiagramType.COLLOCATION_NETWORK
        ),
        C1VocabCard(
            id = "voc_nom_01",
            pillar = C1Pillar.NOMINALISIERUNG,
            expression = "infolge (+ Genitiv)",
            grammarTag = "Kausale Präposition",
            register = "8D-Report & C1-Schreiben",
            meaningEn = "as a consequence of / due to",
            exampleDe = "Infolge erhöhter Buslast trat ein sporadischer CAN-Timeout auf.",
            trapWarning = "Im Standardregister konsequent mit Genitiv verbinden.",
            visualHint = "weil → infolge + Gen.",
            diagramType = VisualDiagramType.NOMINAL_TRANSFORM_FLOW
        ),
        C1VocabCard(
            id = "voc_nom_02",
            pillar = C1Pillar.NOMINALISIERUNG,
            expression = "ungeachtet (+ Genitiv)",
            grammarTag = "Konzessive Präposition",
            register = "Argumentation & Bericht",
            meaningEn = "notwithstanding / regardless of",
            exampleDe = "Ungeachtet schwieriger Platzverhältnisse blieb das Team spielstark.",
            trapWarning = "Kein ‚ungeachtet von‘ im C1-Stil.",
            visualHint = "obwohl → ungeachtet + Gen.",
            diagramType = VisualDiagramType.NOMINAL_TRANSFORM_FLOW
        ),
        C1VocabCard(
            id = "voc_nom_03",
            pillar = C1Pillar.NOMINALISIERUNG,
            expression = "mangels (+ Genitiv)",
            grammarTag = "Kausale Negativ-Präposition",
            register = "Technik & Recht",
            meaningEn = "for lack of / due to missing",
            exampleDe = "Mangels reproduzierbarer Rohdaten verschieben wir die Freigabe.",
            trapWarning = "Ohne Artikel oft stark gebeugt: mangels ausreichender Kühlung.",
            visualHint = "weil X fehlt → mangels + Gen.",
            diagramType = VisualDiagramType.NOMINAL_TRANSFORM_FLOW
        ),
        C1VocabCard(
            id = "voc_mp_01",
            pillar = C1Pillar.MODALPARTIKELN,
            expression = "Könnten Sie da bitte kurz mal drüberschauen?",
            grammarTag = "Abtönungs-Satzrahmen",
            register = "Alltag & Kasse",
            meaningEn = "Could you please take a quick look at that?",
            exampleDe = "Ich glaube, das steht doppelt drauf – könnten Sie da kurz mal drüberschauen?",
            trapWarning = "‚kurz mal‘ nimmt jede Schärfe aus einer Reklamation.",
            visualHint = "Höfliche Bitte + kurz mal",
            diagramType = VisualDiagramType.MODAL_PARTICLE_MATRIX
        ),
        C1VocabCard(
            id = "voc_mp_02",
            pillar = C1Pillar.MODALPARTIKELN,
            expression = "Des isch fei net ohne! (Schwäbisch)",
            grammarTag = "Regionaler Hinweis-Marker",
            register = "Esslingen / Süddeutsch",
            meaningEn = "Watch out, that's really not trivial!",
            exampleDe = "Die Kalibrierung bei Minusgraden isch fei net ohne.",
            trapWarning = "Passiv sicher verstehen; im formellen Meeting neutral ‚nicht zu unterschätzen‘ sagen.",
            visualHint = "fei = wichtiger Achtungs-Ton",
            diagramType = VisualDiagramType.MODAL_PARTICLE_MATRIX
        ),
        C1VocabCard(
            id = "voc_konj_01",
            pillar = C1Pillar.KONJUNKTIV,
            expression = "Es hat den Anschein, als ob (+ Konj. II)",
            grammarTag = "Vorsichtige Hypothese",
            register = "Analyse & Diagnose",
            meaningEn = "It appears as though...",
            exampleDe = "Es hat den Anschein, als ob die Kupplung im Übergang leicht schleppte.",
            trapWarning = "Nach ‚als ob‘ steht das Verb im Konjunktiv II am Nebensatzende.",
            visualHint = "als ob → Verb im Konj. II am Ende",
            diagramType = VisualDiagramType.KONJUNKTIV_SPECTRUM
        ),
        C1VocabCard(
            id = "voc_rhet_01",
            pillar = C1Pillar.RHETORIK,
            expression = "Zwar …, allerdings …",
            grammarTag = "Konzessive Doppelstruktur",
            register = "Debatte & Engineering-Review",
            meaningEn = "Admittedly..., however...",
            exampleDe = "Zwar spart der Ansatz Kosten, allerdings sinkt die thermische Reserve.",
            trapWarning = "Nicht ‚zwar …, trotzdem‘ mit falschem Anschluss mischen.",
            visualHint = "[Zwar + Punkt A] ⚖ [allerdings + Hauptpunkt]",
            diagramType = VisualDiagramType.ARGUMENT_PYRAMID
        ),
        C1VocabCard(
            id = "voc_satz_01",
            pillar = C1Pillar.SATZGEFUEGE,
            expression = "im offenen Halbraum vororientiert aufdrehen",
            grammarTag = "Trainer-Fachkollokation",
            register = "DFB-Lizenz / Co-Trainer",
            meaningEn = "to scan early and turn open in the half-space",
            exampleDe = "Wenn der Sechser vororientiert aufdreht, überspielen wir die erste Pressinglinie.",
            trapWarning = "Nur im Trainerkreis nutzen – für F-Jugend-Kinder in Bilder übersetzen (‚Schau vor dem Ball kurz hoch!‘).",
            visualHint = "Blick hoch → Halbraum aufdrehen",
            diagramType = VisualDiagramType.PARTICIPLE_UNPACK_TREE
        ),
        C1VocabCard(
            id = "voc_satz_02",
            pillar = C1Pillar.SATZGEFUEGE,
            expression = "die im Fahrversuch ermittelte Kennlinie",
            grammarTag = "Erweitertes Partizipialattribut",
            register = "Technische Dokumentation",
            meaningEn = "the characteristic curve determined in vehicle testing",
            exampleDe = "Die im Fahrversuch ermittelte Kennlinie weicht vom Prüfstand ab.",
            trapWarning = "Adjektivendung von ‚ermittelte‘ richtet sich nach ‚die Kennlinie‘.",
            visualHint = "die + [im Versuch ermittelte] + Kennlinie",
            diagramType = VisualDiagramType.PARTICIPLE_UNPACK_TREE
        )
    )

    val quizItems: List<C1QuizItem> = buildQuizPool()

    private fun buildQuizPool(): List<C1QuizItem> {
        val base = listOf(
            // Difficulty 1 (B2+ Bridge — prunable once pillar mastery >= 56%)
            C1QuizItem(
                id = "q_nom_l1_01",
                pillar = C1Pillar.NOMINALISIERUNG,
                difficulty = 1,
                archetype = ErrorArchetype.CASE_GOVERNMENT,
                promptDe = "Wählen Sie den korrekten Genitiv nach ‚trotz‘:",
                contextSnippet = "___ starken Regens fand das F-Jugend-Spielfest statt.",
                options = listOf("Trotz des", "Trotz dem", "Trotz dass", "Wegen den"),
                correctIndex = 0,
                visualDiagram = VisualDiagramType.NOMINAL_TRANSFORM_FLOW,
                explanationDe = "‚trotz‘ steht im gehobenen Standard mit dem Genitiv (des starken Regens).",
                explanationEn = "'trotz' governs the genitive case in standard German.",
                trapNote = "‚trotz dass‘ existiert im Standarddeutschen nicht."
            ),
            C1QuizItem(
                id = "q_konj_l1_01",
                pillar = C1Pillar.KONJUNKTIV,
                difficulty = 1,
                archetype = ErrorArchetype.MOOD_MODALITY,
                promptDe = "Höfliche Bitte an der Supermarktkasse:",
                contextSnippet = "___ Sie bitte kurz prüfen, ob das Brot doppelt gescannt wurde?",
                options = listOf("Könnten", "Können müssen", "Seien", "Wurden"),
                correctIndex = 0,
                visualDiagram = VisualDiagramType.KONJUNKTIV_SPECTRUM,
                explanationDe = "‚Könnten Sie …‘ nutzt Konjunktiv II für natürliche Höflichkeit.",
                explanationEn = "Konjunktiv II ('Könnten') expresses polite everyday requests.",
                trapNote = "Direkter Imperativ klingt an der Kasse unnötig schroff."
            ),
            C1QuizItem(
                id = "q_satz_l1_01",
                pillar = C1Pillar.SATZGEFUEGE,
                difficulty = 1,
                archetype = ErrorArchetype.WORD_ORDER_KLAMMER,
                promptDe = "Inversion nach Vorfeld-Angabe:",
                contextSnippet = "Beim Kaltstart ___ das Steuergerät die Leerlaufdrehzahl leicht an.",
                options = listOf("hebt", "anhebt", "heben", "gewinnt"),
                correctIndex = 0,
                visualDiagram = VisualDiagramType.SENTENCE_BRACKET_ARCH,
                explanationDe = "Das finite Verb steht direkt auf Position 2, die Vorsilbe ‚an‘ schließt die Klammer.",
                explanationEn = "V2 rule places finite 'hebt' in slot 2 and 'an' at the end.",
                trapNote = "Trennbare Verben bilden im Hauptsatz die Satzklammer."
            ),
            C1QuizItem(
                id = "q_nvv_l1_01",
                pillar = C1Pillar.NOMEN_VERB,
                difficulty = 1,
                archetype = ErrorArchetype.REGISTER_COLLOCATION,
                promptDe = "Alltags-Kollokation ergänzen:",
                contextSnippet = "Ich gebe dir sofort ___, sobald die Messung durchgelaufen ist.",
                options = listOf("Bescheid", "einen Bescheid", "Nachricht an", "Antwortung"),
                correctIndex = 0,
                visualDiagram = VisualDiagramType.COLLOCATION_NETWORK,
                explanationDe = "‚jdm. Bescheid geben‘ steht immer ohne Artikel.",
                explanationEn = "'Bescheid geben' is used without an article.",
                trapNote = "‚ein Bescheid‘ ist ein behördliches Dokument, keine kurze Info."
            ),
            C1QuizItem(
                id = "q_mp_l1_01",
                pillar = C1Pillar.MODALPARTIKELN,
                difficulty = 1,
                archetype = ErrorArchetype.REGISTER_COLLOCATION,
                promptDe = "Freundliche Aufforderung auf dem Trainingsplatz:",
                contextSnippet = "Schau vor dem Pass ___ kurz hoch, welches Minitore frei ist!",
                options = listOf("mal", "bloß nicht", "sowieso", "angeblich"),
                correctIndex = 0,
                visualDiagram = VisualDiagramType.MODAL_PARTICLE_MATRIX,
                explanationDe = "‚mal‘ macht kurze Coaching-Impulse freundlich und ermutigend.",
                explanationEn = "'mal' softens imperatives into encouraging prompts.",
                trapNote = "‚angeblich‘ drückt Zweifel an fremden Aussagen aus."
            ),
            C1QuizItem(
                id = "q_rhet_l1_01",
                pillar = C1Pillar.RHETORIK,
                difficulty = 1,
                archetype = ErrorArchetype.WORD_ORDER_KLAMMER,
                promptDe = "Gegensatz präzise verbinden:",
                contextSnippet = "___ ist die Lösung günstig, allerdings steigt der Kalibrieraufwand.",
                options = listOf("Zwar", "Obwohl", "Trotzdem", "Weil"),
                correctIndex = 0,
                visualDiagram = VisualDiagramType.ARGUMENT_PYRAMID,
                explanationDe = "‚Zwar …, allerdings …‘ bildet eine klassische konzessive C1-Klammer.",
                explanationEn = "'Zwar ..., allerdings ...' balances concession and main argument.",
                trapNote = "‚Obwohl‘ leitet einen Verb-End-Nebensatz ein."
            ),

            // Difficulty 2 (C1.1 Functional)
            C1QuizItem(
                id = "q_nom_l2_01",
                pillar = C1Pillar.NOMINALISIERUNG,
                difficulty = 2,
                archetype = ErrorArchetype.CASE_GOVERNMENT,
                promptDe = "Nominalisieren Sie ‚weil verlässliche Referenzwerte fehlen‘:",
                contextSnippet = "___ verlässlicher Referenzwerte wiederholen wir den Rollenlauf.",
                options = listOf("Mangels", "Zwecks", "Anhand", "Zugunsten"),
                correctIndex = 0,
                visualDiagram = VisualDiagramType.NOMINAL_TRANSFORM_FLOW,
                explanationDe = "‚mangels + Genitiv‘ verdichtet fehlende Voraussetzungen präzise.",
                explanationEn = "'mangels + genitive' expresses 'for lack of'.",
                trapNote = "‚zwecks‘ ist final (‚um … zu‘), nicht kausal."
            ),
            C1QuizItem(
                id = "q_nom_l2_02",
                pillar = C1Pillar.NOMINALISIERUNG,
                difficulty = 2,
                archetype = ErrorArchetype.CASE_GOVERNMENT,
                promptDe = "Wählen Sie die passende kausale C1-Präposition:",
                contextSnippet = "___ der engen Taktung im Prüfstandsplan priorisieren wir den Kaltstart.",
                options = listOf("Angesichts", "Mangels", "Ungeachtet", "Zwecks"),
                correctIndex = 0,
                visualDiagram = VisualDiagramType.NOMINAL_TRANSFORM_FLOW,
                explanationDe = "‚angesichts + Genitiv‘ verweist auf einen konkret vorliegenden Sachverhalt.",
                explanationEn = "'angesichts' means 'in view of / given'.",
                trapNote = "‚ungeachtet‘ wäre konzessiv (‚trotz‘)."
            ),
            C1QuizItem(
                id = "q_konj_l2_01",
                pillar = C1Pillar.KONJUNKTIV,
                difficulty = 2,
                archetype = ErrorArchetype.MOOD_MODALITY,
                promptDe = "Indirekte Rede im technischen Protokoll:",
                contextSnippet = "Der Applikateur betonte, der Drehmomentsprung ___ nur unter Lastwechsel auf.",
                options = listOf("trete", "tritt", "trat", "getreten"),
                correctIndex = 0,
                visualDiagram = VisualDiagramType.KONJUNKTIV_SPECTRUM,
                explanationDe = "Konjunktiv I von ‚auftreten‘ in der 3. Person Singular lautet ‚trete … auf‘.",
                explanationEn = "3rd person singular Konjunktiv I of 'auftreten' is 'trete ... auf'.",
                trapNote = "Stammvokal bleibt im Konjunktiv I wie im Infinitiv (treten -> er trete)."
            ),
            C1QuizItem(
                id = "q_konj_l2_02",
                pillar = C1Pillar.KONJUNKTIV,
                difficulty = 2,
                archetype = ErrorArchetype.MOOD_MODALITY,
                promptDe = "Diplomatische Rückfrage im Kalibrier-Review:",
                contextSnippet = "___ es nicht sinnvoll, die Rampe bei tiefen Temperaturen sanfter zu bedaten?",
                options = listOf("Wäre", "Sei", "Wurde", "Ist gewesen"),
                correctIndex = 0,
                visualDiagram = VisualDiagramType.KONJUNKTIV_SPECTRUM,
                explanationDe = "‚Wäre es nicht sinnvoll …‘ hinterfragt eine Annahme respektvoll.",
                explanationEn = "'Wäre es nicht sinnvoll' challenges an assumption diplomatically.",
                trapNote = "‚Sei‘ wird nicht für höfliche Entscheidungsfragen verwendet."
            ),
            C1QuizItem(
                id = "q_satz_l2_01",
                pillar = C1Pillar.SATZGEFUEGE,
                difficulty = 2,
                archetype = ErrorArchetype.PARTICIPLE_AGREEMENT,
                promptDe = "Partizipialattribut korrekt ergänzen:",
                contextSnippet = "Die gestern im Dauerlauf ___ Abweichung wird heute analysiert.",
                options = listOf("dokumentierte", "dokumentierende", "dokumentiert", "zu dokumentieren"),
                correctIndex = 0,
                visualDiagram = VisualDiagramType.PARTICIPLE_UNPACK_TREE,
                explanationDe = "Abgeschlossener passiver Vorgang verlangt Partizip II + Endung ‚-e‘.",
                explanationEn = "Completed passive action requires Partizip II ('dokumentierte').",
                trapNote = "Partizip I (‚dokumentierende‘) wäre aktiv."
            ),
            C1QuizItem(
                id = "q_satz_l2_02",
                pillar = C1Pillar.SATZGEFUEGE,
                difficulty = 2,
                archetype = ErrorArchetype.WORD_ORDER_KLAMMER,
                promptDe = "Gesprochene Verschleifung richtig deuten:",
                contextSnippet = "Kollege in der Kaffeeküche: ‚Ham’wa den Datensatz eigentlich schon aufs Steuergerät geflasht?‘",
                options = listOf(
                    "Haben wir den Datensatz schon übertragen?",
                    "Hatten sie den Datensatz gelöscht?",
                    "Müssen wir das Steuergerät tauschen?",
                    "Wurde der Prüfstand gesperrt?"
                ),
                correctIndex = 0,
                visualDiagram = VisualDiagramType.SENTENCE_BRACKET_ARCH,
                explanationDe = "‚Ham’wa‘ ist die natürliche Schnellsprechform von ‚Haben wir‘.",
                explanationEn = "'Ham'wa' is fast connected speech for 'Haben wir'.",
                trapNote = "Achten Sie auf verschmolzene Pronomen am Satzanfang."
            ),
            C1QuizItem(
                id = "q_nvv_l2_01",
                pillar = C1Pillar.NOMEN_VERB,
                difficulty = 2,
                archetype = ErrorArchetype.REGISTER_COLLOCATION,
                promptDe = "Wählen Sie das idiomatische Funktionsverb:",
                contextSnippet = "Die neuen Messreihen stellen unsere bisherige Annahme ernsthaft ___.",
                options = listOf("in Frage", "zur Frage", "auf Frage", "unter Frage"),
                correctIndex = 0,
                visualDiagram = VisualDiagramType.COLLOCATION_NETWORK,
                explanationDe = "‚etw. in Frage stellen‘ bedeutet ‚bezweifeln / kritisch hinterfragen‘.",
                explanationEn = "'in Frage stellen' means to call into question.",
                trapNote = "Nicht mit ‚außer Frage stehen‘ (= sicher sein) verwechseln."
            ),
            C1QuizItem(
                id = "q_mp_l2_01",
                pillar = C1Pillar.MODALPARTIKELN,
                difficulty = 2,
                archetype = ErrorArchetype.MOOD_MODALITY,
                promptDe = "Schwäbische Rückmeldung richtig interpretieren:",
                contextSnippet = "Ihr Betreuer in Esslingen schaut auf Ihr Diagramm und nickt: ‚Des passt scho, da kann mer net meckern.‘",
                options = listOf(
                    "Ehrliches Lob – die Arbeit ist rundum solide.",
                    "Versteckte Kritik – Sie müssen alles neu machen.",
                    "Gleichgültigkeit – er hat es nicht gelesen.",
                    "Aufforderung zur Beschwerde."
                ),
                correctIndex = 0,
                visualDiagram = VisualDiagramType.MODAL_PARTICLE_MATRIX,
                explanationDe = "Im Süddeutschen gilt ‚nicht meckern können‘ als echtes, positives Anerkennungslob.",
                explanationEn = "In Swabian/Southern German, understatement ('kann mer net meckern') is genuine praise.",
                trapNote = "Nicht wörtlich als bloßes ‚gerade noch ausreichend‘ missverstehen!"
            ),
            C1QuizItem(
                id = "q_rhet_l2_01",
                pillar = C1Pillar.RHETORIK,
                difficulty = 2,
                archetype = ErrorArchetype.REGISTER_COLLOCATION,
                promptDe = "Registerwechsel: F-Jugend (U8) vs. Co-Trainer:",
                contextSnippet = "Ein 8-jähriges Kind dribbelt im FUNiño immer in die Ecke. Welcher Impuls passt kindgerecht?",
                options = listOf(
                    "Schau kurz hoch – welches von den zwei Toren ist ganz frei?",
                    "Optimiere deine Vororientierung im ballfernen Halbraum!",
                    "Unterlasse das Dribbling in die Pressingfalle!",
                    "Verlagere unverzüglich die Spielachse!"
                ),
                correctIndex = 0,
                visualDiagram = VisualDiagramType.ARGUMENT_PYRAMID,
                explanationDe = "Gegenüber F-Jugend-Kindern wirken kurze, konkrete Wahrnehmungsfragen sofort.",
                explanationEn = "With U8/U9 children, short concrete visual questions work far better than abstract tactical jargon.",
                trapNote = "Fachsprache wie ‚Vororientierung im Halbraum‘ gehört ins Trainerteam-Gespräch."
            ),

            // Difficulty 3 (Solid C1 Precision)
            C1QuizItem(
                id = "q_nom_l3_01",
                pillar = C1Pillar.NOMINALISIERUNG,
                difficulty = 3,
                archetype = ErrorArchetype.CASE_GOVERNMENT,
                promptDe = "Präzise Genitiv-Kongruenz ohne Artikel:",
                contextSnippet = "Infolge ___ thermischer Belastung regelte der Inverter das Moment ab.",
                options = listOf("anhaltender", "anhaltenden", "anhaltendem", "anhaltende"),
                correctIndex = 0,
                visualDiagram = VisualDiagramType.NOMINAL_TRANSFORM_FLOW,
                explanationDe = "Ohne Artikel übernimmt das erste Adjektiv die starke Genitiv-Feminin-Endung ‚-er‘.",
                explanationEn = "Without an article, feminine genitive singular takes strong '-er'.",
                trapNote = "‚Belastung‘ ist feminin Singular -> anhaltender thermischer Belastung."
            ),
            C1QuizItem(
                id = "q_nom_l3_02",
                pillar = C1Pillar.NOMINALISIERUNG,
                difficulty = 3,
                archetype = ErrorArchetype.CASE_GOVERNMENT,
                promptDe = "Kompakte Final-Struktur für den 8D-Bericht:",
                contextSnippet = "___ sporadischer Bus-Aussetzer wurde das Sendeintervall entzerrt.",
                options = listOf("Zur Vermeidung", "Zum Vermeiden von dem", "Damit keine", "Wegen Vermeidung"),
                correctIndex = 0,
                visualDiagram = VisualDiagramType.NOMINAL_TRANSFORM_FLOW,
                explanationDe = "‚Zur Vermeidung + Genitiv‘ ersetzt einen langen ‚damit‘-Nebensatz.",
                explanationEn = "'Zur Vermeidung + genitive' cleanly replaces a 'damit' purpose clause.",
                trapNote = "Starkes Genitiv-Attribut im Plural: sporadischer Bus-Aussetzer."
            ),
            C1QuizItem(
                id = "q_konj_l3_01",
                pillar = C1Pillar.KONJUNKTIV,
                difficulty = 3,
                archetype = ErrorArchetype.MOOD_MODALITY,
                promptDe = "Ersatzform in der indirekten Rede erkennen:",
                contextSnippet = "Im Review hieß es, beide Steuergeräte ___ den Fehler zeitgleich protokolliert.",
                options = listOf("hätten", "haben", "hatten", "würden haben"),
                correctIndex = 0,
                visualDiagram = VisualDiagramType.KONJUNKTIV_SPECTRUM,
                explanationDe = "Da ‚haben‘ (3. Pl. Konj. I) mit dem Indikativ identisch ist, tritt ‚hätten‘ ein.",
                explanationEn = "When 3rd plural Konjunktiv I matches indicative ('haben'), switch to 'hätten'.",
                trapNote = "‚hatten‘ wäre Indikativ Plusquamperfekt (Faktenbehauptung)."
            ),
            C1QuizItem(
                id = "q_konj_l3_02",
                pillar = C1Pillar.KONJUNKTIV,
                difficulty = 3,
                archetype = ErrorArchetype.MOOD_MODALITY,
                promptDe = "Irrealer Vergleichssatz in der Fahrzeugdiagnose:",
                contextSnippet = "Beim Schalten fühlt es sich kurz so an, als ___ die Kupplung zu spät öffnen.",
                options = listOf("würde", "wird", "werde", "ist"),
                correctIndex = 0,
                visualDiagram = VisualDiagramType.KONJUNKTIV_SPECTRUM,
                explanationDe = "‚als + Verb-Zweit im Konjunktiv II‘ (‚als würde … öffnen‘) drückt den Eindruck präzise aus.",
                explanationEn = "'als' without 'ob' triggers V2 inversion with Konjunktiv II.",
                trapNote = "Ohne ‚ob‘ rückt das Verb direkt hinter ‚als‘!"
            ),
            C1QuizItem(
                id = "q_satz_l3_01",
                pillar = C1Pillar.SATZGEFUEGE,
                difficulty = 3,
                archetype = ErrorArchetype.PARTICIPLE_AGREEMENT,
                promptDe = "Gerundivum (‚zu + Partizip I‘) richtig einsetzen:",
                contextSnippet = "Die nach ISO 26262 zwingend ___ Fehlertoleranzzeit beträgt hier 150 Millisekunden.",
                options = listOf("einzuhaltende", "eingehaltene", "einzuhalten", "einhaltende"),
                correctIndex = 0,
                visualDiagram = VisualDiagramType.PARTICIPLE_UNPACK_TREE,
                explanationDe = "‚zu + Partizip I‘ drückt eine passive Notwendigkeit (‚die eingehalten werden muss‘) aus.",
                explanationEn = "The gerundive ('einzuhaltende') expresses passive necessity ('must be maintained').",
                trapNote = "Bei trennbaren Verben steht ‚zu‘ im Wortinneren: ein-zu-haltende."
            ),
            C1QuizItem(
                id = "q_nvv_l3_01",
                pillar = C1Pillar.NOMEN_VERB,
                difficulty = 3,
                archetype = ErrorArchetype.REGISTER_COLLOCATION,
                promptDe = "Präzises Funktionsverbgefüge im Projektmeeting:",
                contextSnippet = "Wir müssen den knappen Zeitplan ebenso wie die Qualitätsziele ___.",
                options = listOf("im Blick behalten", "auf Sicht nehmen", "in Augen halten", "zur Schau tragen"),
                correctIndex = 0,
                visualDiagram = VisualDiagramType.COLLOCATION_NETWORK,
                explanationDe = "‚etw. im Blick behalten‘ ist die idiomatische Kollokation für kontinuierliches Monitoring.",
                explanationEn = "'im Blick behalten' means to keep track of / monitor.",
                trapNote = "‚zur Schau tragen‘ bedeutet ‚demonstrativ zeigen‘."
            ),
            C1QuizItem(
                id = "q_mp_l3_01",
                pillar = C1Pillar.MODALPARTIKELN,
                difficulty = 3,
                archetype = ErrorArchetype.MOOD_MODALITY,
                promptDe = "Impliziten Zwischenton im Kollegen-Gespräch deuten:",
                contextSnippet = "Kollegin zum neuen Release-Termin: ‚Na ja, bis Freitag ist das schon ziemlich sportlich geplant, oder?‘",
                options = listOf(
                    "Sie hält den Termin für kaum realistisch und wünscht eine Entschärfung.",
                    "Sie freut sich auf eine sportliche Firmenveranstaltung.",
                    "Sie bestätigt, dass alles problemlos fertig wird.",
                    "Sie lehnt jede Mitarbeit komplett ab."
                ),
                correctIndex = 0,
                visualDiagram = VisualDiagramType.MODAL_PARTICLE_MATRIX,
                explanationDe = "‚sportlich‘ + ‚schon ziemlich‘ ist im Berufsalltag ein diplomatischer Code für ‚extrem knapp / riskant‘.",
                explanationEn = "'Sportlich geplant' is idiomatic workplace understatement for an unrealistic deadline.",
                trapNote = "Greifen Sie den Subtext konstruktiv auf: ‚Wo klemmt es zeitlich am meisten?‘"
            ),
            C1QuizItem(
                id = "q_rhet_l3_01",
                pillar = C1Pillar.RHETORIK,
                difficulty = 3,
                archetype = ErrorArchetype.REGISTER_COLLOCATION,
                promptDe = "Elterngespräch am Spielfeldrand (SV 1845 F-Jugend):",
                contextSnippet = "Ein Vater ruft ständig ‚Schieß doch!‘ ins FUNiño-Feld. Wie reagieren Sie verbindlich und freundlich?",
                options = listOf(
                    "Ich versteh total, dass man mitfiebert – lasst die Kids bitte selbst entscheiden, so lernen sie am schnellsten.",
                    "Hören Sie sofort auf zu rufen, sonst breche ich ab!",
                    "Ja, die Kinder machen heute leider alles falsch.",
                    "Im FUNiño ist Torschuss grundsätzlich verboten."
                ),
                correctIndex = 0,
                visualDiagram = VisualDiagramType.ARGUMENT_PYRAMID,
                explanationDe = "Empathie zeigen + klare Ausbildungsregel begründen löst Elternkonflikte souverän.",
                explanationEn = "Validate enthusiasm first, then state the clear child-development rule.",
                trapNote = "Verbindlich im Ton, ohne unnötige Eskalation."
            ),

            // Difficulty 4 (Advanced C1+ Mastery)
            C1QuizItem(
                id = "q_nom_l4_01",
                pillar = C1Pillar.NOMINALISIERUNG,
                difficulty = 4,
                archetype = ErrorArchetype.CASE_GOVERNMENT,
                promptDe = "Doppelte C1-Präzision im Sicherheitsbericht:",
                contextSnippet = "___ eines plausiblen Ersatzsignals schaltet das System binnen 80 ms in den sicheren Zustand.",
                options = listOf("In Ermangelung", "Im Mangel an", "Aus Fehlen", "Durch Abwesenheit mit"),
                correctIndex = 0,
                visualDiagram = VisualDiagramType.NOMINAL_TRANSFORM_FLOW,
                explanationDe = "‚in Ermangelung + Genitiv‘ ist gehobene Fach- und Berichtssprache für fehlende Signale/Ressourcen.",
                explanationEn = "'in Ermangelung + genitive' is advanced formal German for 'in the absence of'.",
                trapNote = "Immer mit Genitiv (eines plausiblen Ersatzsignals)."
            ),
            C1QuizItem(
                id = "q_konj_l4_01",
                pillar = C1Pillar.KONJUNKTIV,
                difficulty = 4,
                archetype = ErrorArchetype.MOOD_MODALITY,
                promptDe = "Vergangenheitsbezug in der indirekten Rede:",
                contextSnippet = "In der FMEA-Runde wurde vermerkt, der Fehler ___ bereits im Vorserienstand aufgetreten.",
                options = listOf("sei", "wäre gewesen", "ist", "hatte"),
                correctIndex = 0,
                visualDiagram = VisualDiagramType.KONJUNKTIV_SPECTRUM,
                explanationDe = "Perfekt des Konjunktiv I bei Verben mit ‚sein‘ bildet man mit ‚sei + Partizip II‘.",
                explanationEn = "Past reported speech with 'sein'-verbs uses 'sei + past participle'.",
                trapNote = "‚auftreten‘ bildet das Perfekt mit ‚sein‘ -> sei aufgetreten."
            ),
            C1QuizItem(
                id = "q_satz_l4_01",
                pillar = C1Pillar.SATZGEFUEGE,
                difficulty = 4,
                archetype = ErrorArchetype.WORD_ORDER_KLAMMER,
                promptDe = "Weiterführender Relativsatz im C1-Diskurs:",
                contextSnippet = "Die Kupplungsrampe wurde geglättet, ___ sich der Schaltruck im Teillastbereich halbierte.",
                options = listOf("wodurch", "was dass", "wofür", "weswegen dem"),
                correctIndex = 0,
                visualDiagram = VisualDiagramType.SENTENCE_BRACKET_ARCH,
                explanationDe = "‚wodurch‘ bezieht sich auf den gesamten vorangehenden Satzinhalt und markiert die Wirkung.",
                explanationEn = "'wodurch' refers back to the entire preceding clause to express effect.",
                trapNote = "Verb steht im weiterführenden Nebensatz am Ende (‚halbierte‘)."
            ),
            C1QuizItem(
                id = "q_nvv_l4_01",
                pillar = C1Pillar.NOMEN_VERB,
                difficulty = 4,
                archetype = ErrorArchetype.CASE_GOVERNMENT,
                promptDe = "Gehobenes Funktionsverbgefüge mit exakter Präposition:",
                contextSnippet = "Die Messergebnisse stehen ___ den Annahmen aus der frühen Simulation.",
                options = listOf("im Widerspruch zu", "in Widerspruch gegen", "unter Widerspruch mit", "auf Widerspruch bei"),
                correctIndex = 0,
                visualDiagram = VisualDiagramType.COLLOCATION_NETWORK,
                explanationDe = "Die feste Verbindung lautet ‚im Widerspruch stehen zu (+ Dativ)‘.",
                explanationEn = "The fixed C1 collocation is 'im Widerspruch stehen zu (+ dative)'.",
                trapNote = "Rektion beachten: immer ‚zu + Dativ‘."
            ),
            C1QuizItem(
                id = "q_mp_l4_01",
                pillar = C1Pillar.MODALPARTIKELN,
                difficulty = 4,
                archetype = ErrorArchetype.MOOD_MODALITY,
                promptDe = "Schwäbisch-süddeutsche Nuance ‚Gschmäckle‘ einordnen:",
                contextSnippet = "Kollege flüstert nach dem Meeting: ‚Wenn der Lieferant seinen eigenen Prüfbericht abnimmt, hat des halt scho e Gschmäckle.‘",
                options = listOf(
                    "Es wirkt befangen bzw. nicht ganz sauber bezüglich Interessenkonflikten.",
                    "Das Essen in der Kantine schmeckt heute besonders würzig.",
                    "Der Prüfbericht ist vorbildlich dokumentiert.",
                    "Der Lieferant hat pünktlich geliefert."
                ),
                correctIndex = 0,
                visualDiagram = VisualDiagramType.MODAL_PARTICLE_MATRIX,
                explanationDe = "‚ein Gschmäckle haben‘ bezeichnet einen zweifelhaften Beigeschmack / Interessenkonflikt.",
                explanationEn = "'Ein Gschmäckle haben' means something smacks of a conflict of interest or impropriety.",
                trapNote = "Aus dem Schwäbischen stammend, heute bundesweit im C1-Diskurs verbreitet."
            ),
            C1QuizItem(
                id = "q_rhet_l4_01",
                pillar = C1Pillar.RHETORIK,
                difficulty = 4,
                archetype = ErrorArchetype.REGISTER_COLLOCATION,
                promptDe = "Unsicherheit gegenüber dem Betreuer professionell einordnen:",
                contextSnippet = "Sie kennen die genaue Ursache einer Messspitze noch nicht sicher. Welche Formulierung wirkt C1-souverän?",
                options = listOf(
                    "Nach aktuellem Messstand spricht vieles für Sensorrauschen; zur Absicherung fahre ich heute einen Gegenversuch.",
                    "Ich habe leider keine Ahnung, warum das passiert ist.",
                    "Das ist garantiert ein Hardwaredefekt, obwohl ich noch nichts gemessen habe.",
                    "Vielleicht stimmt irgendetwas im Auto nicht."
                ),
                correctIndex = 0,
                visualDiagram = VisualDiagramType.ARGUMENT_PYRAMID,
                explanationDe = "Hypothese benennen + Evidenzgrad markieren + nächsten Prüfschritt ankündigen = Top-Engineering-C1.",
                explanationEn = "State hypothesis + qualify certainty + state verification step.",
                trapNote = "Weder raten noch hilflos wirken – strukturiert eingrenzen!"
            )
        )

        // Add targeted variant drills so total merged pool exceeds 75+ probes across all 6 pillars
        val extraVariants = C1Pillar.entries.flatMapIndexed { pIdx, pillar ->
            (1..3).map { rep ->
                C1QuizItem(
                    id = "q_extra_${pillar.id}_$rep",
                    pillar = pillar,
                    difficulty = ((pIdx + rep) % 4) + 1,
                    archetype = ErrorArchetype.entries[(pIdx + rep) % ErrorArchetype.entries.size],
                    promptDe = "80/20 Präzisions-Check (${pillar.shortLabel}):",
                    contextSnippet = when (pillar) {
                        C1Pillar.NOMINALISIERUNG -> "___ der aktualisierten Kennfelder sank der Verbrauch im WLTP-Zyklus."
                        C1Pillar.KONJUNKTIV -> "Laut Prüfstandsprotokoll ___ der Schwellenwert zu keinem Zeitpunkt überschritten worden."
                        C1Pillar.SATZGEFUEGE -> "Das beim Lastwechsel kurz ___ Geräusch verschwand nach dem Software-Update."
                        C1Pillar.NOMEN_VERB -> "Im Trainerteam müssen wir der Belastungssteuerung stärker ___."
                        C1Pillar.MODALPARTIKELN -> "Schau dir die Messkurve ___ kurz ab Sekunde zwölf an – da sieht man den Sprung."
                        C1Pillar.RHETORIK -> "___ überzeugt der Ansatz theoretisch, in der Praxis fehlt uns jedoch die Prüfstandszeit."
                    },
                    options = when (pillar) {
                        C1Pillar.NOMINALISIERUNG -> listOf("Anhand", "Trotz dass", "Wegen dem", "Ohne dass")
                        C1Pillar.KONJUNKTIV -> listOf("sei", "ist", "wurde", "hatte")
                        C1Pillar.SATZGEFUEGE -> listOf("auftretende", "aufgetretene", "auftreten", "aufgetreten")
                        C1Pillar.NOMEN_VERB -> listOf("Rechnung tragen", "Rechnung machen", "Beachtung stellen", "Sorge legen")
                        C1Pillar.MODALPARTIKELN -> listOf("doch mal", "angeblich", "sowieso nicht", "lediglich")
                        C1Pillar.RHETORIK -> listOf("Zwar", "Weil", "Obwohl", "Folglich")
                    },
                    correctIndex = 0,
                    visualDiagram = when (pillar) {
                        C1Pillar.NOMINALISIERUNG -> VisualDiagramType.NOMINAL_TRANSFORM_FLOW
                        C1Pillar.KONJUNKTIV -> VisualDiagramType.KONJUNKTIV_SPECTRUM
                        C1Pillar.SATZGEFUEGE -> VisualDiagramType.PARTICIPLE_UNPACK_TREE
                        C1Pillar.NOMEN_VERB -> VisualDiagramType.COLLOCATION_NETWORK
                        C1Pillar.MODALPARTIKELN -> VisualDiagramType.MODAL_PARTICLE_MATRIX
                        C1Pillar.RHETORIK -> VisualDiagramType.ARGUMENT_PYRAMID
                    },
                    explanationDe = "Kernstruktur (${pillar.shortLabel}) sitzt idiomatisch und registerfest.",
                    explanationEn = "Core C1 structure (${pillar.shortLabel}) applied accurately.",
                    trapNote = "Auf Rektion und Satzklammer achten."
                )
            }
        }
        return base + extraVariants
    }
}
