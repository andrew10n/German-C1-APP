package com.example.data.curriculum

import com.example.domain.model.C1Pillar
import com.example.domain.model.SlideCheckpoint
import com.example.domain.model.VisualDiagramType
import com.example.domain.model.VisualLessonModule
import com.example.domain.model.VisualSlide

object C1VisualModulesData {
    val modules: List<VisualLessonModule> = listOf(
        VisualLessonModule(
            id = "mod_nominalisierung",
            pillar = C1Pillar.NOMINALISIERUNG,
            title = "Nominalstil & Präzisions-Genitiv",
            germanSubtitle = "Nebensätze in kompakte C1-Signalblöcke verdichten",
            estimatedMinutes = 5,
            keyVisualDiagram = VisualDiagramType.NOMINAL_TRANSFORM_FLOW,
            slides = listOf(
                VisualSlide(
                    id = "sl_nom_1",
                    pillar = C1Pillar.NOMINALISIERUNG,
                    slideIndex = 1,
                    totalSlides = 2,
                    headline = "Kausal-Verdichtung auf einen Blick",
                    subheadline = "Aus ‚weil‘ wird ‚infolge / angesichts + Genitiv‘",
                    diagramType = VisualDiagramType.NOMINAL_TRANSFORM_FLOW,
                    formulaBefore = "weil die Batterietemperatur steigt",
                    formulaAfter = "infolge steigender Batterietemperatur",
                    highlightRule = "infolge + Genitiv",
                    keyInsight = "Spart ein ganzes Verb-Ende und bündelt die Ursache am Satzanfang.",
                    microExampleDe = "Infolge steigender Lastanforderungen regelt das Steuergerät nach.",
                    microExampleGloss = "Due to rising load demands, the ECU adjusts.",
                    commonTrap = "Kein Dativ nach ‚infolge‘ oder ‚angesichts‘ im Prüfungsregister.",
                    checkpoint = SlideCheckpoint(
                        prompt = "Welche Form verdichtet ‚obwohl die Messdaten schwanken‘ korrekt?",
                        options = listOf(
                            "Ungeachtet schwankender Messdaten",
                            "Trotz dass die Messdaten schwanken",
                            "Wegen schwankenden Messdaten"
                        ),
                        correctIndex = 0,
                        feedback = "‚ungeachtet + Genitiv‘ drückt konzessive Verdichtung auf C1-Niveau aus."
                    )
                ),
                VisualSlide(
                    id = "sl_nom_2",
                    pillar = C1Pillar.NOMINALISIERUNG,
                    slideIndex = 2,
                    totalSlides = 2,
                    headline = "Final- & Konditional-Blöcke",
                    subheadline = "‚damit / um … zu‘ → ‚zwecks / zur + -ung‘",
                    diagramType = VisualDiagramType.NOMINAL_TRANSFORM_FLOW,
                    formulaBefore = "um die Drehmomentlücke zu glätten",
                    formulaAfter = "zur Glättung der Drehmomentlücke",
                    highlightRule = "zur + Nominalisierung + Genitiv",
                    keyInsight = "Macht technische Berichte und Präsentationen sofort prägnant.",
                    microExampleDe = "Zur Absicherung der Diagnose fahren wir zwei Gegenversuche.",
                    microExampleGloss = "To validate the diagnostics, we run two counter-tests.",
                    commonTrap = "Artikelverschmelzung beachten: zu + der = zur."
                )
            )
        ),
        VisualLessonModule(
            id = "mod_konjunktiv",
            pillar = C1Pillar.KONJUNKTIV,
            title = "Konjunktiv-Spektrum & Distanz",
            germanSubtitle = "Referieren (Konj. I) vs. Abwägen & Höflichkeit (Konj. II)",
            estimatedMinutes = 5,
            keyVisualDiagram = VisualDiagramType.KONJUNKTIV_SPECTRUM,
            slides = listOf(
                VisualSlide(
                    id = "sl_konj_1",
                    pillar = C1Pillar.KONJUNKTIV,
                    slideIndex = 1,
                    totalSlides = 2,
                    headline = "Neutrale Quellen-Distanz",
                    subheadline = "Konjunktiv I zeigt fremde Aussage ohne Zustimmung",
                    diagramType = VisualDiagramType.KONJUNKTIV_SPECTRUM,
                    formulaBefore = "Der Zulieferer sagt, das Signal ist stabil.",
                    formulaAfter = "Laut Zulieferer sei das Signal stabil.",
                    highlightRule = "sei / habe / liege vor",
                    keyInsight = "Im Statusbericht trennen Sie Messung und Behauptung glasklar.",
                    microExampleDe = "Der Prüfbericht hält fest, die Abweichung trete nur kalt auf.",
                    microExampleGloss = "The test report states the deviation only occurs cold.",
                    commonTrap = "Bei Formgleichheit mit dem Indikativ (Pl. ‚haben‘) auf ‚hätten‘ ausweichen.",
                    checkpoint = SlideCheckpoint(
                        prompt = "Welche Form wahrt neutrale indirekte Rede im Plural?",
                        options = listOf(
                            "Die Sensoren hätten keine Aussetzer gezeigt.",
                            "Die Sensoren haben keine Aussetzer gezeigt.",
                            "Die Sensoren zeigen wohl keine Aussetzer."
                        ),
                        correctIndex = 0,
                        feedback = "Da ‚haben‘ mit dem Indikativ zusammenfällt, springt Konjunktiv II (‚hätten‘) ein."
                    )
                ),
                VisualSlide(
                    id = "sl_konj_2",
                    pillar = C1Pillar.KONJUNKTIV,
                    slideIndex = 2,
                    totalSlides = 2,
                    headline = "Diplomatischer Konjunktiv II",
                    subheadline = "Annahmen höflich hinterfragen statt frontal widersprechen",
                    diagramType = VisualDiagramType.KONJUNKTIV_SPECTRUM,
                    formulaBefore = "Der Schwellenwert ist zu niedrig.",
                    formulaAfter = "Wäre hier nicht etwas mehr Hysterese sinnvoll?",
                    highlightRule = "wäre / ließe sich / könnte",
                    keyInsight = "Öffnet im Engineering-Review den Raum für gemeinsame Korrektur.",
                    microExampleDe = "Könnten wir den Übergang im Teillastbereich noch einmal gegenprüfen?",
                    microExampleGloss = "Could we cross-check the transition at part load once more?",
                    commonTrap = "Vermeiden Sie doppeltes ‚würde‘ im selben Nebensatz."
                )
            )
        ),
        VisualLessonModule(
            id = "mod_satzgefuege",
            pillar = C1Pillar.SATZGEFUEGE,
            title = "Satzklammer & Partizipial-Bäume",
            germanSubtitle = "Linksattribute entpacken & Satzspannung sauber halten",
            estimatedMinutes = 5,
            keyVisualDiagram = VisualDiagramType.PARTICIPLE_UNPACK_TREE,
            slides = listOf(
                VisualSlide(
                    id = "sl_satz_1",
                    pillar = C1Pillar.SATZGEFUEGE,
                    slideIndex = 1,
                    totalSlides = 2,
                    headline = "Partizipialattribut im Schnellscan",
                    subheadline = "Artikel → Kernnomen finden → Modifikatoren dazwischen lesen",
                    diagramType = VisualDiagramType.PARTICIPLE_UNPACK_TREE,
                    formulaBefore = "die Kennlinie, die gestern am Prüfstand bedatet wurde",
                    formulaAfter = "die gestern am Prüfstand bedatete Kennlinie",
                    highlightRule = "[Artikel] + [Kontext + Partizip] + [Nomen]",
                    keyInsight = "Beim Hören und Lesen springen Sie zuerst vom Artikel zum Nomen.",
                    microExampleDe = "Das im Warmlauf auftretende Ruckeln ließ sich reproduzieren.",
                    microExampleGloss = "The judder occurring during warm-up was reproducible.",
                    commonTrap = "Partizip I (-end) = aktiv/gleichzeitig; Partizip II = passiv/abgeschlossen.",
                    checkpoint = SlideCheckpoint(
                        prompt = "Welche Form beschreibt eine laufende aktive Messung korrekt?",
                        options = listOf(
                            "die aktuell laufende Messung",
                            "die aktuell gelaufene Messung",
                            "die aktuell laufen Messung"
                        ),
                        correctIndex = 0,
                        feedback = "Partizip I + Adjektivendung (-ende) markiert den aktiven Vorgang."
                    )
                ),
                VisualSlide(
                    id = "sl_satz_2",
                    pillar = C1Pillar.SATZGEFUEGE,
                    slideIndex = 2,
                    totalSlides = 2,
                    headline = "Satzklammer unter Sprechdruck",
                    subheadline = "Vorfeld für das Thema nutzen, Verb-Klammer früh schließen",
                    diagramType = VisualDiagramType.SENTENCE_BRACKET_ARCH,
                    formulaBefore = "Wir müssen heute im Team wegen der Freigabe sprechen.",
                    formulaAfter = "Über die Freigabe sprechen wir gleich kurz im Team.",
                    highlightRule = "[Fokus im Vorfeld] + [Verb 2] + [Mittelfeld]",
                    keyInsight = "Im gesprochenen C1 hält eine kurze Klammer den Zuhörer sofort im Bild.",
                    microExampleDe = "Im Halbraum drehst du mit dem ersten Kontakt sofort auf!",
                    microExampleGloss = "In the half-space, turn open immediately with your first touch!",
                    commonTrap = "Im Hauptsatz bleibt das konjugierte Verb strikt auf Position 2."
                )
            )
        ),
        VisualLessonModule(
            id = "mod_nomen_verb",
            pillar = C1Pillar.NOMEN_VERB,
            title = "Funktionsverbgefüge & Kollokationen",
            germanSubtitle = "Feste Verb-Nomen-Anker statt blasser Einzelverben",
            estimatedMinutes = 5,
            keyVisualDiagram = VisualDiagramType.COLLOCATION_NETWORK,
            slides = listOf(
                VisualSlide(
                    id = "sl_nvv_1",
                    pillar = C1Pillar.NOMEN_VERB,
                    slideIndex = 1,
                    totalSlides = 2,
                    headline = "Präzise Aktionsart durch NVV",
                    subheadline = "In Gang setzen · in Frage stellen · Rechnung tragen",
                    diagramType = VisualDiagramType.COLLOCATION_NETWORK,
                    formulaBefore = "Wir berücksichtigen die Toleranzen.",
                    formulaAfter = "Wir tragen den Toleranzen Rechnung.",
                    highlightRule = "Dativ + Rechnung tragen",
                    keyInsight = "NVV liefern feste Satzrahmen, die beim freien Sprechen Zeit kaufen.",
                    microExampleDe = "Wir sollten eine gestufte Freigabe in Betracht ziehen.",
                    microExampleGloss = "We should consider a staged release.",
                    commonTrap = "‚in Betracht ziehen‘ (nicht: nehmen) · ‚außer Frage stehen‘ (nicht: liegen).",
                    checkpoint = SlideCheckpoint(
                        prompt = "Ergänzen Sie das passende Funktionsverb: ‚Kritik an der Methodik ___‘",
                        options = listOf("üben", "machen", "tun"),
                        correctIndex = 0,
                        feedback = "Die feste C1-Kollokation lautet ‚Kritik üben an (+ Dat.)‘."
                    )
                ),
                VisualSlide(
                    id = "sl_nvv_2",
                    pillar = C1Pillar.NOMEN_VERB,
                    slideIndex = 2,
                    totalSlides = 2,
                    headline = "Alltags- & Technik-Anker",
                    subheadline = "Auf den Punkt bringen · Bescheid geben · unter Druck geraten",
                    diagramType = VisualDiagramType.COLLOCATION_NETWORK,
                    formulaBefore = "Sag mir kurz, wenn du fertig bist.",
                    formulaAfter = "Gib mir kurz Bescheid, sobald du durch bist.",
                    highlightRule = "jdm. kurz Bescheid geben",
                    keyInsight = "Natürliche Chunks verhindern wörtliches Übersetzen aus dem Englischen.",
                    microExampleDe = "Im Zentrum geraten wir nach Ballverlust zu schnell unter Druck.",
                    microExampleGloss = "In the center we come under pressure too quickly after losing the ball.",
                    commonTrap = "‚Bescheid geben/sagen‘ immer ohne Artikel."
                )
            )
        ),
        VisualLessonModule(
            id = "mod_modalpartikeln",
            pillar = C1Pillar.MODALPARTIKELN,
            title = "Modalpartikeln & Zwischentöne",
            germanSubtitle = "doch, halt, eben, mal, wohl, eigentlich & schwäbisch ‚fei‘",
            estimatedMinutes = 5,
            keyVisualDiagram = VisualDiagramType.MODAL_PARTICLE_MATRIX,
            slides = listOf(
                VisualSlide(
                    id = "sl_mp_1",
                    pillar = C1Pillar.MODALPARTIKELN,
                    slideIndex = 1,
                    totalSlides = 2,
                    headline = "Ton-Weichzeichner im Alltag",
                    subheadline = "Wie ‚mal‘, ‚doch‘ und ‚wohl‘ Schärfe aus Bitten nehmen",
                    diagramType = VisualDiagramType.MODAL_PARTICLE_MATRIX,
                    formulaBefore = "Prüfen Sie den Kassenbon!",
                    formulaAfter = "Könnten Sie da bitte kurz mal drüberschauen?",
                    highlightRule = "kurz + mal = freundlich & entspannt",
                    keyInsight = "Ohne Abtönung wirken grammatisch korrekte Sätze im Deutschen oft schroff.",
                    microExampleDe = "Da ist der Artikel wohl zweimal drübergegangen.",
                    microExampleGloss = "Looks like the item probably got scanned twice.",
                    commonTrap = "‚wohl‘ markiert eine höfliche Vermutung ohne direkte Schuldzuweisung.",
                    checkpoint = SlideCheckpoint(
                        prompt = "Welche Partikel mildert eine Vermutung an der Kasse diplomatisch ab?",
                        options = listOf(
                            "Der Joghurt wurde wohl doppelt gescannt.",
                            "Der Joghurt wurde bloß doppelt gescannt!",
                            "Der Joghurt wurde sowieso doppelt gescannt."
                        ),
                        correctIndex = 0,
                        feedback = "‚wohl‘ signalisiert eine ruhige, nicht vorwurfsvolle Annahme."
                    )
                ),
                VisualSlide(
                    id = "sl_mp_2",
                    pillar = C1Pillar.MODALPARTIKELN,
                    slideIndex = 2,
                    totalSlides = 2,
                    headline = "Süddeutsch & Esslingen verstehen",
                    subheadline = "‚fei‘, ‚gell‘ und ‚halt‘ richtig einordnen",
                    diagramType = VisualDiagramType.MODAL_PARTICLE_MATRIX,
                    formulaBefore = "Unterschätz die Glätte heute nicht!",
                    formulaAfter = "Des isch heut fei glatt draußen, gell!",
                    highlightRule = "fei = wichtiger Hinweis / Überraschung",
                    keyInsight = "Verstehen Sie regionale Färbung sicher, antworten Sie entspannt standardnah.",
                    microExampleDe = "Des hat fei schon noch e Gschmäckle bei der Freigabe.",
                    microExampleGloss = "That still leaves a dubious aftertaste regarding the sign-off.",
                    commonTrap = "‚Net gschimpft isch globt gnug‘ ist im Schwäbischen echtes Anerkennungslob!"
                )
            )
        ),
        VisualLessonModule(
            id = "mod_rhetorik",
            pillar = C1Pillar.RHETORIK,
            title = "Konzessive Rhetorik & Gegenwind",
            germanSubtitle = "Zwar … allerdings · Einwände aufgreifen & umlenken",
            estimatedMinutes = 5,
            keyVisualDiagram = VisualDiagramType.ARGUMENT_PYRAMID,
            slides = listOf(
                VisualSlide(
                    id = "sl_rhet_1",
                    pillar = C1Pillar.RHETORIK,
                    slideIndex = 1,
                    totalSlides = 2,
                    headline = "Die Zwar-Allerdings-Wippe",
                    subheadline = "Gegenargument anerkennen und das eigene Gewicht nach hinten legen",
                    diagramType = VisualDiagramType.ARGUMENT_PYRAMID,
                    formulaBefore = "Der Termin ist eng, aber die Sicherheit ist wichtiger.",
                    formulaAfter = "Zwar drängt der Termin, allerdings geht die Betriebssicherheit vor.",
                    highlightRule = "Zwar [Punkt A], allerdings [Kernpunkt B]",
                    keyInsight = "Der Nachsatz nach ‚allerdings‘ bleibt im Gedächtnis des Gesprächspartners.",
                    microExampleDe = "Zwar stimmt die Simulation stationär, im Übergang kippt jedoch das Moment.",
                    microExampleGloss = "While steady-state simulation matches, torque tips during transients.",
                    commonTrap = "Nach ‚allerdings‘ im Mittelfeld oder Vorfeld auf die Verbposition achten.",
                    checkpoint = SlideCheckpoint(
                        prompt = "Wie fangen Sie eine Unterbrechung im Meeting souverän ab?",
                        options = listOf(
                            "Guter Punkt – lassen Sie mich den Gedanken kurz zu Ende führen, dann gehe ich direkt darauf ein.",
                            "Unterbrechen Sie mich jetzt nicht, ich rede noch.",
                            "Okay, dann vergesse ich meinen Punkt."
                        ),
                        correctIndex = 0,
                        feedback = "Wertschätzen + Rederecht sichern + Rückbezug ankündigen = souveränes C1."
                    )
                ),
                VisualSlide(
                    id = "sl_rhet_2",
                    pillar = C1Pillar.RHETORIK,
                    slideIndex = 2,
                    totalSlides = 2,
                    headline = "Zwei-Register-Führung auf dem Platz",
                    subheadline = "Kindgerechtes Bild (F-Jugend) vs. Taktik-Analyse (Trainerteam)",
                    diagramType = VisualDiagramType.ARGUMENT_PYRAMID,
                    formulaBefore = "Ihr müsst die Tiefe belaufen!",
                    formulaAfter = "U9: ‚Such das freie Tor!‘ · Lizenz: ‚Vororientierung im Halbraum‘",
                    highlightRule = "Adressat steuert Wortwahl & Satzlänge",
                    keyInsight = "Echte C1-Kompetenz zeigt sich im mühelosen Umschalten zwischen beiden Welten.",
                    microExampleDe = "Kopf hoch, mutig geschossen – hol dir sofort den nächsten Ball!",
                    microExampleGloss = "Heads up, brave shot — grab the next ball right away!",
                    commonTrap = "Keine abstrakte Taktik-Terminologie gegenüber 8-jährigen F-Jugend-Kindern!"
                )
            )
        )
    )
}
