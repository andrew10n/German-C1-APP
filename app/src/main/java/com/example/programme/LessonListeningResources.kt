package com.example.programme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class LessonListeningRecommendation(
    val unitId: String,
    val title: String,
    val url: String,
    val task: String
)

data class BookBridgePractice(
    val unitId: String,
    val bookReference: String,
    val audioFilenameHint: String,
    val originalPrompt: String,
    val audioReference: String? = null
)

object LessonListeningLibrary {
    val episodes = listOf(
        LessonListeningRecommendation(
            unitId = "s2-data",
            title = "Easy German 709 · Unfreundlichkeitstrainings bei Aldi",
            url = "https://www.easygerman.org/podcast/episodes/709",
            task = "Höre den Abschnitt über KI-Rechenzentren. Trenne eine berichtete Behauptung, den dazu genannten Beleg und deine eigene Schlussfolgerung."
        ),
        LessonListeningRecommendation(
            unitId = "s4-argument",
            title = "Easy German 703 · In der Küche wird Politik gemacht",
            url = "https://www.easygerman.org/podcast/episodes/703",
            task = "Notiere je ein Argument für und gegen ein Parteiverbotsverfahren. Halte anschließend fest, welche Information für deine eigene Abwägung noch fehlt."
        ),
        LessonListeningRecommendation(
            unitId = "s4-ethics",
            title = "Easy German 701 · Aktivismus in Deutschland",
            url = "https://www.easygerman.org/podcast/episodes/701",
            task = "Fasse die Perspektive des Gastes und eine im Gespräch genannte gesellschaftliche Spannung getrennt zusammen."
        ),
        LessonListeningRecommendation(
            unitId = "s5-nuance",
            title = "Easy German 702 · Dialekt oder Akzent?",
            url = "https://www.easygerman.org/podcast/episodes/702",
            task = "Notiere, wie die Gesprächspartner Dialekt und Akzent unterscheiden. Prüfe, welche Beispiele sie tatsächlich nennen und was offenbleibt."
        )
    )

    val bookBridges = listOf(
        BookBridgePractice(
            unitId = "s3-negotiate",
            bookReference = "Sicher! C1/1 · Lektion 2 „Im Tourismus“ · Kursbuch S. 25–36, Arbeitsbuch AB 25–40",
            audioFilenameHint = "Im Ordner „audio AB“ tragen passende Dateien _L02_ im Namen.",
            originalPrompt = "Eine Stadt möchte Besucher auf weniger bekannte Viertel verteilen. Verhandeln Sie mit einem lokalen Anbieter über ein wetterfestes Wochenendangebot. Berücksichtigen Sie Erreichbarkeit, Kosten und die Belastung der Anwohner; nennen Sie eine Bedingung, unter der Sie den Vorschlag ändern würden."
        ),
        BookBridgePractice(
            unitId = "s4-argument",
            bookReference = "Sicher! C1/1 · Lektion 5 „Kunst“ · Kursbuch S. 61–72, Arbeitsbuch AB 73–88",
            audioFilenameHint = "Im Ordner „audio AB“ tragen passende Dateien _L05_ im Namen.",
            originalPrompt = "Ein Stadtteil kann entweder ein neues Wandbild finanzieren oder ein verwittertes historisches Schild restaurieren. Begründen Sie eine Empfehlung mit Zugänglichkeit, Erhalt und Kosten. Kennzeichnen Sie Annahmen, würdigen Sie das stärkste Gegenargument und schlagen Sie eine überprüfbare nächste Maßnahme vor."
        ),
        BookBridgePractice(
            unitId = "s2-mediate",
            bookReference = "Sicher! C1/1 · Lektion 6 „Studium“ · Kursbuch S. 73–84, Arbeitsbuch AB 89–104",
            audioFilenameHint = "Im Ordner „audio AB“ tragen passende Dateien _L06_ im Namen.",
            originalPrompt = "Erklären Sie einer berufstätigen Person die Anforderungen und den Wochenplan zweier Studienangebote, die Sie selbst recherchiert haben. Trennen Sie gesicherte Angaben von offenen Fragen und leiten Sie keine unbelegten Aussagen über Zulassungschancen ab."
        ),
        BookBridgePractice(
            unitId = "s5-nuance",
            bookReference = "Aspekte neu C1 · Kapitel 2 „Hast du Worte?“ · Kursbuch S. 24–39",
            audioFilenameHint = "Wähle die Lehrbuch-CD-Datei anhand der im Kursbuch beim Hörtext gedruckten Tracknummer.",
            audioReference = "Lehrbuch-CD 1 · Ordner „CD 1 K 1-3“; die Tracknummer bitte im Kursbuch abgleichen.",
            originalPrompt = "Höre eine selbst ausgewählte Passage über Sprachgebrauch oder Dialekt blind an. Notiere die wörtlich verstandene Aussage, eine mögliche Sprecherabsicht und eine offene Frage. Nach dem ersten Versuch darfst du das Buchtranskript zum Abgleich verwenden."
        ),
        BookBridgePractice(
            unitId = "s4-present",
            bookReference = "Aspekte neu C1 · Kapitel 3 „An die Arbeit!“ · Kursbuch S. 40–55",
            audioFilenameHint = "Wähle die Lehrbuch-CD-Datei anhand der im Kursbuch beim Hörtext gedruckten Tracknummer.",
            audioReference = "Lehrbuch-CD 1 · Ordner „CD 1 K 1-3“; die Tracknummer bitte im Kursbuch abgleichen.",
            originalPrompt = "Bereite einen kurzen freien Vortrag zu einer Arbeitswelt-Frage aus dem Kapitel vor. Formuliere These, zwei voneinander unabhängige Gründe, einen Vorbehalt und eine konkrete Schlussfolgerung. Trenne eigene Annahmen ausdrücklich von Informationen aus der Buchquelle."
        ),
        BookBridgePractice(
            unitId = "s3-agree",
            bookReference = "Aspekte neu C1 · Kapitel 4 „Wirtschaftsgipfel“ · Kursbuch S. 56–71",
            audioFilenameHint = "Wähle die Lehrbuch-CD-Datei anhand der im Kursbuch beim Hörtext gedruckten Tracknummer.",
            audioReference = "Lehrbuch-CD 2 · Ordner „CD 2 K 4-7“; die Tracknummer bitte im Kursbuch abgleichen.",
            originalPrompt = "Nutze einen Hörtext aus dem Kapitel als Grundlage für ein kurzes simuliertes Beratungsgespräch. Fasse zuerst das Anliegen neutral zusammen, stelle zwei präzise Rückfragen und schließe mit einer vorläufigen Vereinbarung samt ungeklärtem Punkt."
        ),
        BookBridgePractice(
            unitId = "s2-summarise",
            bookReference = "Aspekte neu C1 · Kapitel 10 „Erinnerungen“ · Kursbuch S. 152–167",
            audioFilenameHint = "Wähle die Lehrbuch-CD-Datei anhand der im Kursbuch beim Hörtext gedruckten Tracknummer.",
            audioReference = "Lehrbuch-CD 3 · Ordner „CD 3 K 8-10“; die Tracknummer bitte im Kursbuch abgleichen.",
            originalPrompt = "Fasse einen Hörtext des Kapitels in höchstens fünf Sätzen zusammen. Unterscheide die zentrale Aussage, einen genannten Beleg und eine Schlussfolgerung; markiere, wenn du eine Aussage nur als Vermutung wiedergibst."
        )
    )

    fun forUnit(unitId: String): Pair<List<LessonListeningRecommendation>, List<BookBridgePractice>> =
        episodes.filter { it.unitId == unitId } to bookBridges.filter { it.unitId == unitId }
}

@Composable
fun LessonListeningResources(
    unitId: String,
    onOpenListeningLab: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (episodes, bookBridges) = LessonListeningLibrary.forUnit(unitId)
    if (episodes.isEmpty() && bookBridges.isEmpty()) return
    val uriHandler = LocalUriHandler.current

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("Zusätzliche Originalquellen", fontWeight = FontWeight.SemiBold)
            episodes.forEach { episode ->
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(episode.title, fontWeight = FontWeight.Medium)
                    Text(episode.task, style = MaterialTheme.typography.bodySmall)
                    TextButton(onClick = { uriHandler.openUri(episode.url) }) {
                        Text("Folge beim Anbieter öffnen")
                    }
                }
            }
            bookBridges.forEach { bookBridge ->
                Text("Buch-Brücke · ${bookBridge.bookReference}", fontWeight = FontWeight.Medium)
                Text(bookBridge.originalPrompt)
                Text(bookBridge.audioFilenameHint, style = MaterialTheme.typography.bodySmall)
                bookBridge.audioReference?.let {
                    Text(it, style = MaterialTheme.typography.bodySmall)
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    Button(onClick = onOpenListeningLab) {
                        Text("Buchaufnahme im Hörlabor öffnen")
                    }
                }
                Text(
                    "Die Zusatzaufgabe ist neu formuliert. Buchtexte und -aufnahmen sind nicht in der App enthalten.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
