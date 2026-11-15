package com.example.programme

object ContextApplicationTask {
    fun build(unit: CurriculumUnit, context: ContextTrack): String {
        requireNotNull(unit.content) { "Context application requires an authored unit." }
        val scenario = requireNotNull(unit.contexts[context]) {
            "Unit ${unit.id} has no authored scenario for $context."
        }

        return """
            Einheit: ${unit.title}
            Lernziele: ${unit.objectives.joinToString("; ")}
            Gewählter Kontext (${context.name}): $scenario
            Aufgabe: Schreibe drei bis fünf neue Sätze und prüfe selbst Ziel, Adressat, Register sowie eine offene oder unsichere Stelle.
            Erfinde keine technischen oder sachlichen Angaben; kennzeichne Annahmen.
        """.trimIndent()
    }
}
