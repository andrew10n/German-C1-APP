package com.example.programme.core

/** Stable IDs are the repository/UI join keys. Roadmap units intentionally have no lesson payload. */
object CurriculumCatalog {
    private data class Mapping(val title: String, val goal: String)
    private val stageTitles = listOf(
        "Positionen begründen und klären", "Perspektiven verstehen und vermitteln",
        "Probleme analysieren und Entscheidungen aushandeln", "Komplexität präzise darstellen",
        "Kontroversen und Unsicherheit bewältigen", "Verantwortung und Wirkung reflektieren",
        "Selbstständiger Transfer und nachhaltige Handlungsfähigkeit"
    )
    private val mappings = listOf(
        listOf(
            Mapping("Öffnungszeiten mit Augenmaß", "Eine öffentliche Maßnahme abwägen und einen überprüfbaren Vorschlag begründen"),
            Mapping("Zusammenarbeit ohne stille Vorwürfe", "Einen sozialen Konflikt ansprechen, Interessen klären und Vereinbarungen treffen"),
            Mapping("Freigabe unter Vorbehalt", "Ein technisches Risiko verständlich erklären und eine bedingte Freigabe aushandeln"),
            Mapping("Ziele im Coaching klären", "Beobachtung und Bewertung trennen und ein überprüfbares Entwicklungsziel vereinbaren")
        ),
        listOf(
            Mapping("Daten und Schlagzeilen", "Eine öffentliche Behauptung anhand ihrer Belege und Grenzen einordnen"),
            Mapping("Zugehörigkeit und Sprache", "Unterschiedliche Erfahrungen respektvoll vergleichen und Missverständnisse vermitteln"),
            Mapping("Anforderungen übersetzen", "Nutzerbedürfnisse in prüfbare technische Anforderungen überführen"),
            Mapping("Aktives Zuhören statt Ratschläge", "Eine Perspektive paraphrasieren und mit offenen Fragen klären")
        ),
        listOf(
            Mapping("Wohnraum gerecht verteilen", "Konkurrierende Kriterien gewichten und eine begründete Entscheidung vertreten"),
            Mapping("Im Verein verhandeln", "Interessen bündeln und einen tragfähigen Kompromiss aushandeln"),
            Mapping("Fehleranalyse ohne Schuldzuweisung", "Ursachenhypothesen mit Evidenz abgleichen und Maßnahmen priorisieren"),
            Mapping("Handlungsoptionen entwickeln", "Optionen ergebnisoffen prüfen und einen selbstgewählten nächsten Schritt sichern")
        ),
        listOf(
            Mapping("Wissenschaft für die Öffentlichkeit", "Einen komplexen Befund adressatengerecht und ohne Übertreibung erklären"),
            Mapping("Sensibles Feedback geben", "Kritik differenziert formulieren und auf eine defensive Reaktion eingehen"),
            Mapping("Architekturentscheidungen dokumentieren", "Alternativen, Randbedingungen und Folgen nachvollziehbar dokumentieren"),
            Mapping("Ambivalenz aushalten", "Widersprüchliche Motive spiegeln, ohne der Person eine Lösung aufzudrängen")
        ),
        listOf(
            Mapping("Kontroverse moderieren", "Starke Gegenargumente fair aufnehmen und die Diskussion strukturieren"),
            Mapping("Grenzen und Erwartungen", "Eine Grenze wertschätzend setzen und unausgesprochene Erwartungen klären"),
            Mapping("Incident-Kommunikation", "Unter Zeitdruck gesicherte Fakten von Hypothesen trennen und Updates abstimmen"),
            Mapping("Widerstand und Autonomie", "Auf Zurückhaltung reagieren und die Entscheidungsfreiheit der Person sichern")
        ),
        listOf(
            Mapping("Digitale Verantwortung", "Gesellschaftliche Folgen einer Technologie differenziert bewerten"),
            Mapping("Macht und Teilhabe", "Ausschlussmechanismen ansprechen und Beteiligung konkret ermöglichen"),
            Mapping("Sicherheits- und Ethikreview", "Technische und ethische Risiken mit expliziten Entscheidungskriterien beurteilen"),
            Mapping("Grenzen des Coachings", "Vertraulichkeit und Zuständigkeit erläutern und gegebenenfalls weiterverweisen")
        ),
        listOf(
            Mapping("Unbekanntes Dossier vertreten", "Aus neuen Quellen spontan eine belastbare Position mit Einschränkungen entwickeln"),
            Mapping("Mehrparteienvermittlung", "Zwischen unvereinbar scheinenden Positionen vermitteln und Ergebnisse sichern"),
            Mapping("Fachübergreifende Entscheidung", "Eine neue technische Entscheidung gegenüber unterschiedlichen Zielgruppen verteidigen"),
            Mapping("Entwicklung nachhaltig überprüfen", "Wirkung mit zeitlichem Abstand prüfen und Ziele eigenständig revidieren")
        )
    )
    val units: List<CurriculumUnit> = frozen(mappings.flatMapIndexed { stageIndex, rows ->
        rows.mapIndexed { contextIndex, row ->
            val number = stageIndex * 4 + contextIndex + 1
            val prerequisites = when {
                number == 1 -> emptyList()
                stageIndex == 0 -> listOf(id(number - 1))
                else -> listOf(id(number - 4))
            }
            CurriculumUnit(id(number), stageIndex + 1, row.title, row.goal,
                CommunicationContext.values()[contextIndex], prerequisites,
                if (number <= 3) ContentStatus.AUTHORED else ContentStatus.ROADMAP,
                when (number) { 1 -> InitialContent.publicDecision; 2 -> InitialContent.socialAgreement
                    3 -> InitialContent.engineeringRelease; else -> null })
        }
    })
    val stages: List<CurriculumStage> = frozen(stageTitles.mapIndexed { index, title ->
        CurriculumStage(index + 1, title, units.filter { it.stage == index + 1 }.map { it.id })
    })
    fun unit(id: String): CurriculumUnit? = units.firstOrNull { it.id == id }
    private fun id(number: Int): String = "u" + number.toString().padStart(2, '0')
}
