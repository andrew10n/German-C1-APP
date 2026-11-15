package com.example.programme

data class RealLifeRepair(
    val primaryPhrase: String,
    val alternatives: List<String>,
    val explanation: String,
    val transferPrompt: String
)

object RealLifeRepairEngine {
    fun create(experience: RealLifeExperience): RealLifeRepair {
        val source = "${experience.situation} ${experience.said}".lowercase()
        if (experience.context == ContextTrack.GENERAL &&
            listOf("scan", "gescannt", "bon", "artikel", "kasse", "doppelt", "zweimal").any(source::contains)
        ) {
            return RealLifeRepair(
                primaryPhrase = "Entschuldigung, ich glaube, der Artikel wurde zweimal gescannt. Könnten Sie das bitte überprüfen?",
                alternatives = listOf(
                    "Der Artikel steht zweimal auf dem Bon.",
                    "Das wurde doppelt berechnet.",
                    "Ich glaube, da ist etwas doppelt erfasst worden."
                ),
                explanation = "Dein Satz war verständlich. Zweimal wird zusammengeschrieben; mit „gescannt“ oder „auf dem Bon“ wird die Situation präziser.",
                transferPrompt = "Ein Restaurantbon enthält einen Posten doppelt. Bitte sprich deine eigene höfliche Nachfrage."
            )
        }

        return when (experience.context) {
            ContextTrack.ENGINEERING -> RealLifeRepair(
                primaryPhrase = "Ich würde das gern noch einmal prüfen, bevor ich eine endgültige Aussage treffe.",
                alternatives = listOf(
                    "Mir ist eine Abweichung aufgefallen; ich vergleiche sie noch mit dem vorherigen Versuch.",
                    "Nach aktuellem Stand könnte die Ursache bei der Messung liegen, aber ich möchte das noch absichern."
                ),
                explanation = "Technische Gespräche brauchen eine klare Beobachtung, eine vorsichtige Bewertung und gegebenenfalls den nächsten Prüfschritt.",
                transferPrompt = "Dein Supervisor fragt nach einer unerwarteten Messwertabweichung. Antworte ohne diese Vorlage."
            )
            ContextTrack.COACHING -> RealLifeRepair(
                primaryPhrase = "Schau dich erst um, bevor du den Ball bekommst.",
                alternatives = listOf(
                    "Mach dich breit!",
                    "Spiel in den freien Raum.",
                    "Versuch's noch einmal."
                ),
                explanation = "Mit Kindern sind kurze, konkrete Verben und ein direktes Ziel hilfreicher als lange Erklärungen.",
                transferPrompt = "Ein Kind steht zu eng am Ball. Gib eine kurze, positive Anweisung."
            )
            ContextTrack.SOCIAL, ContextTrack.GENERAL -> RealLifeRepair(
                primaryPhrase = "Entschuldigung, ich glaube, ich habe das gerade nicht ganz verstanden. Könnten Sie das bitte anders formulieren?",
                alternatives = listOf(
                    "Meinst du damit, dass …?",
                    "Könntest du das kurz an einem Beispiel erklären?",
                    "Einen Moment, ich möchte sicher sein, dass ich dich richtig verstanden habe."
                ),
                explanation = "Wenn die Situation unklar ist, ist eine höfliche Reparaturstrategie oft wichtiger als ein sofort perfekter Satz.",
                transferPrompt = "Jemand erklärt dir etwas unerwartet schnell. Bitte frage nach, ohne ins Englische zu wechseln."
            )
        }
    }
}
