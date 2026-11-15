package com.example

import com.example.data.model.ConversationScenario
import com.example.data.model.ConversationTurn
import com.example.data.model.ConversationVariationEngine
import com.example.data.model.SpeakerProfile
import com.example.data.model.UnexpectedInterruption
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ConversationVariationEngineTest {
    private val base = ConversationScenario(
        id = "technical",
        title = "Technisches Review",
        rolePartner = "Leitung Entwicklung",
        environment = "Beruf",
        openingStatement = "Die Messwerte sind widersprüchlich.",
        turns = listOf(
            ConversationTurn(
                partnerSpeech = "Was schließen Sie daraus?",
                expectedUserRolePrompt = "Begründen Sie Ihre Einschätzung.",
                suggestedC1Connectors = listOf("allerdings"),
                targetRegister = "fachlich"
            )
        ),
        unexpectedInterruption = UnexpectedInterruption(
            triggerAfterSecond = 6,
            partnerInterruptSpeech = "Moment.",
            reactionObjective = "Einordnen."
        ),
        evaluationFocus = "Unsicherheit"
    )

    @Test
    fun createsUnfamiliarFollowUpAndKeepsAuthoredTurn() {
        val variation = ConversationVariationEngine.vary(base, 1)

        assertEquals("technical_v1", variation.id)
        assertEquals(2, variation.turns.size)
        assertTrue(variation.turns.last().partnerSpeech.contains("Messung"))
        assertEquals(SpeakerProfile.FOLLOW_UP, variation.turns.last().speakerProfile)
        assertTrue(variation.variationLabel.contains("Variation"))
    }

    @Test
    fun expansionKeepsBaseAndAddsTwoReproducibleVariants() {
        val expanded = ConversationVariationEngine.expand(listOf(base))

        assertEquals(listOf("technical", "technical_v1", "technical_v2"), expanded.map { it.id })
        assertEquals(3, expanded.size)
    }
}
