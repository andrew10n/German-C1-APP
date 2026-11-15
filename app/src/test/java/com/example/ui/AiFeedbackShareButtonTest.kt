package com.example.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class AiFeedbackShareButtonTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun externalFeedbackRequiresAnExplicitConfirmationStep() {
        compose.setContent {
            MaterialTheme {
                AiFeedbackShareButton(
                    task = "Erkläre die Kernaussage.",
                    response = "Die Kernaussage ist ..."
                )
            }
        }

        compose.onNodeWithText("Externe KI-Rückmeldung anfragen").performClick()
        compose.onNodeWithText("Antwort extern teilen?").assertIsDisplayed()
        val shareText = compose.onNodeWithTag("external_feedback_share_text")
        shareText.assertIsDisplayed()
        shareText.performTextReplacement("Nur dieser gekürzte Text wird geteilt.")
        compose.onNodeWithText("Teilen-Auswahl öffnen").assertIsDisplayed()
    }
}
