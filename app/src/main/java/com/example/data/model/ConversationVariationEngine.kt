package com.example.data.model

/**
 * Produces bounded, reproducible variations from authored scenarios.
 * It changes the conversational demand without replacing the authored base task.
 */
object ConversationVariationEngine {
    fun expand(scenarios: List<ConversationScenario>): List<ConversationScenario> =
        scenarios.flatMap { scenario ->
            listOf(
                scenario,
                vary(scenario, 1),
                vary(scenario, 2)
            )
        }

    fun vary(scenario: ConversationScenario, variation: Int): ConversationScenario {
        require(variation in 1..2)
        val followUp = followUpFor(scenario, variation)
        val turn = ConversationTurn(
            partnerSpeech = followUp.first,
            expectedUserRolePrompt = followUp.second,
            suggestedC1Connectors = when (variation) {
                1 -> listOf("Wenn ich Sie richtig verstehe", "Allerdings", "Daraus folgt")
                else -> listOf("Das ist ein berechtigter Einwand", "Gleichzeitig", "Konkret bedeutet das")
            },
            targetRegister = scenario.turns.lastOrNull()?.targetRegister.orEmpty(),
            speakerProfile = SpeakerProfile.FOLLOW_UP
        )
        return scenario.copy(
            id = "${scenario.id}_v$variation",
            title = "${scenario.title} · Variation $variation",
            openingStatement = when (variation) {
                1 -> "${scenario.openingStatement} Ich möchte außerdem wissen, wie Sie mit einem unerwarteten Einwand umgehen."
                else -> "${scenario.openingStatement} Bitte antworten Sie zunächst kurz und fragen Sie anschließend gezielt nach."
            },
            turns = scenario.turns + turn,
            unexpectedInterruption = scenario.unexpectedInterruption.copy(
                triggerAfterSecond = (scenario.unexpectedInterruption.triggerAfterSecond - variation).coerceAtLeast(3),
                partnerInterruptSpeech = when (variation) {
                    1 -> "Entschuldigung, ich muss kurz nachhaken: Was wäre denn Ihr konkreter nächster Schritt?"
                    else -> "Moment, so einfach ist das nicht. Welche Information fehlt Ihnen noch für eine belastbare Entscheidung?"
                },
                reactionObjective = when (variation) {
                    1 -> "Greifen Sie den Einwand auf, strukturieren Sie Ihre Antwort und bleiben Sie beim Ziel."
                    else -> "Bitten Sie gezielt um Klärung und formulieren Sie eine vorläufige, begründete Antwort."
                }
            ),
            variationLabel = "Unvorhersehbare Variation $variation"
        )
    }

    private fun followUpFor(
        scenario: ConversationScenario,
        variation: Int
    ): Pair<String, String> {
        val text = "${scenario.title.lowercase()} ${scenario.rolePartner.lowercase()}"
        return when {
            "sport" in text || "trainer" in text || "jugend" in text -> if (variation == 1) {
                "Und wie erklären Sie das jetzt den Kindern, ohne sie zu verunsichern?" to
                    "Passen Sie die Erklärung an Kinder an und geben Sie eine klare nächste Handlung."
            } else {
                "Ein anderer Trainer schlägt gerade das Gegenteil vor. Wie beziehen Sie ihn ein?" to
                    "Würdigen Sie den Einwand und sichern Sie eine gemeinsame, praktikable Entscheidung."
            }
            "technik" in text || "entwicklung" in text || "ki-" in text || "ingenieur" in text -> if (variation == 1) {
                "Welche Messung würde Ihre Annahme am schnellsten bestätigen oder widerlegen?" to
                    "Benennen Sie eine konkrete Prüfung und trennen Sie Beobachtung von Schlussfolgerung."
            } else {
                "Die Datenlage ist noch lückenhaft. Was kommunizieren Sie jetzt an die Leitung?" to
                    "Formulieren Sie eine belastbare Zwischenmeldung mit Unsicherheit und nächstem Schritt."
            }
            else -> if (variation == 1) {
                "Was genau erwarten Sie jetzt von mir, damit wir weiterkommen?" to
                    "Klären Sie das Ziel und antworten Sie höflich, aber konkret."
            } else {
                "Ich sehe das anders. Welche Ihrer Annahmen ist für Sie am wenigsten sicher?" to
                    "Reagieren Sie auf den Widerspruch, ohne abzublocken, und markieren Sie Unsicherheit."
            }
        }
    }
}
