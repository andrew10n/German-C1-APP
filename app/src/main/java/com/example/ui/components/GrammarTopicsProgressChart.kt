package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

data class GrammarTopicProgress(
    val id: String,
    val title: String,
    val shortName: String,
    val category: String,
    val progressPercentage: Int,
    val formulaExample: String,
    val cefrTargetLevel: String,
    val completedExercises: Int,
    val totalExercises: Int,
    val commonMistake: String
)

@Suppress("UNUSED_PARAMETER")
@Composable
fun GrammarTopicsProgressChart(
    modifier: Modifier = Modifier,
    onTopicSelected: (GrammarTopicProgress) -> Unit = {}
) {
    Surface(
        modifier = modifier.fillMaxWidth().testTag("grammar_topics_progress_chart"),
        shape = RoundedCornerShape(22.dp),
        color = BerlinTheme.surface,
        border = BorderStroke(1.dp, BerlinTheme.line)
    ) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Grammatik · nicht gemessen", fontWeight = FontWeight.Bold, color = BerlinTheme.ink)
            Text(
                "Für einzelne Grammatikthemen liegen dieser Ansicht keine gemessenen Lernstände vor. Es werden keine Fortschrittsprozente oder abgeschlossenen Aufgaben behauptet.",
                fontSize = 13.sp,
                color = BerlinTheme.ink2
            )
        }
    }
}
