package com.example.programme

data class ExternalListeningSource(
    val id: String,
    val title: String,
    val url: String,
    val formats: String,
    val focus: String,
    val transcriptNote: String,
    val practiceTasks: List<String>
)

object ExternalListeningLibrary {
    val sources = listOf(
        ExternalListeningSource(
            id = "easy-german-youtube",
            title = "Easy German · YouTube-Straßeninterviews",
            url = "https://www.youtube.com/@EasyGerman",
            formats = "Straßeninterviews und Gespräche mit deutschen und englischen Untertiteln",
            focus = "Alltagssprache, spontane Sprecherwechsel, unterschiedliche Stimmen und regionale Alltagsthemen; wähle ausdrücklich die fortgeschrittenen Videos.",
            transcriptNote = "Untertitel und verfügbare Transkripte gehören zum Anbieter. Nicht herunterladen oder in die App kopieren.",
            practiceTasks = listOf(
                "Wähle ein fortgeschrittenes Straßeninterview und höre zuerst 90 Sekunden ohne Untertitel. Notiere Thema, zwei Haltungen und eine offene Stelle.",
                "Sieh denselben Abschnitt erneut mit deutschen Untertiteln. Korrigiere nur das, was der Beitrag tatsächlich zeigt oder sagt, und notiere zwei Gesprächsmarker."
            )
        ),
        ExternalListeningSource(
            id = "easy-german",
            title = "Easy German · Podcast-Folgen",
            url = "https://www.easygerman.org/podcast/episodes",
            formats = "Gespräche, Straßeninterviews und Erklärungen",
            focus = "Natürliches Deutsch, spontane Reaktionen, Sprecherwechsel und alltagsnahe Themen.",
            transcriptNote = "Folgen und Zusatzmaterial öffnen beim Anbieter; interaktive Transkripte oder Untertitel können eingeschränkt oder kostenpflichtig sein.",
            practiceTasks = listOf(
                "Wählen Sie eine Folge und hören Sie zunächst zwei bis fünf Minuten ohne Text mit. Notieren Sie Thema, Haltung und einen Beleg.",
                "Hören Sie denselben Abschnitt erneut. Notieren Sie zwei tatsächlich gehörte Gesprächsmarker und prüfen Sie erst jetzt verfügbare Untertitel."
            )
        ),
        ExternalListeningSource(
            id = "dw-deutsch-lernen",
            title = "DW · Deutsch lernen",
            url = "https://learngerman.dw.com/de/deutsch-lernen/s-9095",
            formats = "Lernbeiträge, Nachrichten und Audio-/Videokurse",
            focus = "Strukturiertes Hörverstehen mit Angeboten bis C1.",
            transcriptNote = "Lernhilfen und Transkripte unterscheiden sich je nach Beitrag.",
            practiceTasks = listOf(
                "Wählen Sie einen Beitrag nahe C1 und notieren Sie Hauptaussage, Belege und Einschränkungen.",
                "Prüfen Sie erst nach dem ersten Hörversuch, welche Wörter oder Zahlen Sie verwechselt haben."
            )
        ),
        ExternalListeningSource(
            id = "deutschlandfunk-podcasts",
            title = "Deutschlandfunk · Der Tag",
            url = "https://www.deutschlandfunk.de/podcast-104.html",
            formats = "Interviews, Diskussionen, Reportagen und Nachrichten",
            focus = "Längere zusammenhängende Beiträge, Argumente und unterschiedliche Positionen.",
            transcriptNote = "Begleittexte oder Transkripte sind nicht für jede Sendung verfügbar.",
            practiceTasks = listOf(
                "Fassen Sie die Positionen von zwei Sprechenden getrennt zusammen.",
                "Halten Sie eine offene Frage, eine Einschränkung und eine implizite Schlussfolgerung fest."
            )
        ),
        ExternalListeningSource(
            id = "tagesschau-youtube",
            title = "tagesschau · YouTube",
            url = "https://www.youtube.com/@tagesschau",
            formats = "Nachrichten, Reportagen und Interviews des öffentlich-rechtlichen Nachrichtenangebots",
            focus = "Formelles Standarddeutsch, Nachrichtensprache, Tempo und verdichtete Sachinformationen.",
            transcriptNote = "Verfügbarkeit einzelner Videos und Rechte liegen beim Anbieter; die App speichert keine Medien oder Untertitel.",
            practiceTasks = listOf(
                "Wähle einen Beitrag von zwei bis fünf Minuten und notiere die Hauptaussage, zwei belegte Fakten und eine im Beitrag genannte Einschränkung.",
                "Höre oder sieh den Abschnitt erneut. Trenne klar zwischen Bericht, Zitat einer Quelle und deiner eigenen Schlussfolgerung."
            )
        ),
        ExternalListeningSource(
            id = "ard-sounds",
            title = "ARD Sounds",
            url = "https://www.ardsounds.de/",
            formats = "Podcasts, Gespräche, Reportagen und Dokumentationen",
            focus = "Authentische Sprecherwechsel und längere Alltags- und Sachthemen; die ARD-Mediathek führt direkt zu einzelnen Folgen.",
            transcriptNote = "Transkripte und Zusatzmaterial variieren nach Sendung.",
            practiceTasks = listOf(
                "Wählen Sie einen Beitrag und einen Abschnitt von zwei bis fünf Minuten. Skizzieren Sie ohne Transkript den Gesprächsverlauf.",
                "Hören Sie erneut und trennen Sie wörtliche Aussage, hörbaren Beleg, Haltung und eigene Vermutung."
            )
        )
    )

    fun isValid(source: ExternalListeningSource): Boolean =
        source.id.isNotBlank() &&
            source.title.isNotBlank() &&
            source.url.startsWith("https://") &&
            source.formats.isNotBlank() &&
            source.focus.isNotBlank() &&
            source.transcriptNote.isNotBlank() &&
            source.practiceTasks.size >= 2 &&
            source.practiceTasks.all(String::isNotBlank)
}
