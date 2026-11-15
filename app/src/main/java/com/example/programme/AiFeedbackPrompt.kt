package com.example.programme

object AiFeedbackPrompt {
    fun build(
        task: String,
        response: String,
        evidenceType: String = "schriftlicher Übungstext"
    ): String {
        require(task.isNotBlank()) { "Task description must not be blank." }
        require(response.isNotBlank()) { "Learner response must not be blank." }

        return """
            Du bist eine sorgfältige Deutsch-Lernhilfe für fortgeschrittene Lernende.
            Prüfe die folgende Antwort nur als Übung, nicht als offizielle oder validierte Bewertung.

            Regeln für dein Feedback:
            - Zitiere bei jeder Korrektur zuerst die genaue Stelle aus der Antwort.
            - Trenne eindeutige Grammatik-/Wortwahlfehler von bloßen Stilalternativen und unsicheren Vermutungen.
            - Gib für eindeutige Fehler eine korrigierte Formulierung, erkläre kurz warum und bewahre Aussage und Registerton.
            - Gib höchstens drei priorisierte Verbesserungen und eine kurze Anschlussübung.
            - Bewerte keine Fakten, Fachbehauptungen oder Hörverstehen, wenn dafür die Quelle oder das Audio fehlt.
            - Bei einem ASR-Transkript: Beurteile weder Aussprache noch Hörgenauigkeit; die Spracherkennung kann Fehler enthalten.
            - Vergib niemals Punkte, eine GER-/C1-Stufe, ein Bestehen, einen Mastery-Status oder eine Prüfungsreife.
            - Wenn du unsicher bist, sage ausdrücklich, was du nicht verlässlich beurteilen kannst.

            Aufgabenstellung:
            $task

            Art der vorgelegten Evidenz: $evidenceType
            Antwort:
            $response
        """.trimIndent()
    }
}
