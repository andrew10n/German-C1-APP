package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

data class MetricProgressPoint(
    val label: String,
    val listeningScore: Float,
    val speakingScore: Float,
    val vocabularyRetention: Float
)

data class RetentionStageInfo(
    val stageName: String,
    val count: Int,
    val percentage: Float,
    val stageColor: Color
)

@Suppress("UNUSED_PARAMETER")
@Composable
fun VisualPerformanceDashboard(
    modifier: Modifier = Modifier,
    initialSelectedMetric: String = "ALL"
) {
    Surface(
        modifier = modifier.fillMaxWidth().testTag("visual_performance_dashboard"),
        shape = RoundedCornerShape(22.dp),
        color = BerlinTheme.surface,
        border = BorderStroke(1.dp, BerlinTheme.line)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("Übungsübersicht · nicht gemessen", fontWeight = FontWeight.Bold, color = BerlinTheme.ink)
            Text(
                "Für Hörverstehen, Sprechen und Wortschatz-Retention liegen dieser Ansicht keine verifizierten Messdaten vor.",
                fontSize = 13.sp,
                color = BerlinTheme.ink2
            )
            Text(
                "Legacy-Übungen bleiben verfügbar. Anhören, Musterlösungen und Übungsabschlüsse belegen weder Kompetenzzuwachs noch langfristiges Behalten.",
                fontSize = 13.sp,
                color = BerlinTheme.ink2
            )
        }
    }
}
