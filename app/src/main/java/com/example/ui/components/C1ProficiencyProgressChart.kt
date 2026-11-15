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
import com.example.ui.progress.ProficiencyProgressViewModel
import com.example.ui.theme.*

@Suppress("UNUSED_PARAMETER")
@Composable
fun C1ProficiencyProgressChart(
    modifier: Modifier = Modifier,
    // Do not instantiate the legacy view model here: initialization seeds synthetic history.
    viewModel: ProficiencyProgressViewModel? = null
) {
    Surface(
        modifier = modifier.fillMaxWidth().testTag("c1_proficiency_chart_card"),
        shape = RoundedCornerShape(20.dp),
        color = BerlinTheme.surface,
        border = BorderStroke(1.dp, BerlinTheme.line)
    ) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Kompetenzverlauf · nicht gemessen", fontWeight = FontWeight.Bold, color = BerlinTheme.ink)
            Text(
                "Diese Legacy-Ansicht hat keinen verifizierten Kompetenzverlauf. Frühere Beispielwerte und manuelle Schätzungen sind keine Messungen und werden hier nicht als Fortschritt dargestellt.",
                fontSize = 13.sp,
                color = BerlinTheme.ink2
            )
            Text(
                "Ein Übungsabschluss ist kein C1- oder Prüfungsnachweis.",
                fontSize = 13.sp,
                color = BerlinTheme.ink2
            )
        }
    }
}
