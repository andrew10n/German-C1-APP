package com.example.programme

import org.junit.Assert.assertTrue
import org.junit.Test

class AiFeedbackPromptTest {
    @Test
    fun promptRequiresEvidenceBasedCorrectionAndProhibitsCefrScoring() {
        val prompt = AiFeedbackPrompt.build(
            task = "Erkläre die offene Frage im Bericht.",
            response = "Die Messung zeigt alles."
        )

        assertTrue(prompt.contains("Zitiere bei jeder Korrektur zuerst die genaue Stelle"))
        assertTrue(prompt.contains("Trenne eindeutige Grammatik-/Wortwahlfehler"))
        assertTrue(prompt.contains("Vergib niemals Punkte, eine GER-/C1-Stufe"))
        assertTrue(prompt.contains("Die Messung zeigt alles."))
        assertTrue(prompt.contains("Erkläre die offene Frage im Bericht."))
    }

    @Test(expected = IllegalArgumentException::class)
    fun promptRejectsBlankLearnerResponse() {
        AiFeedbackPrompt.build(task = "Aufgabe", response = " ")
    }
}
