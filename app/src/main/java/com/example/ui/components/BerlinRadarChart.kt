package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SkillCategory
import com.example.data.model.SkillScore
import com.example.ui.theme.BarlowCondensedFontFamily
import com.example.ui.theme.BerlinTheme
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * High-fidelity 8-Axis Radar Chart component designed for the Berlin U-Bahn Wayfinding theme.
 * Displays user proficiency across the 8 core C1 skills with glowing polygon fill and DIN typography.
 */
@Composable
fun BerlinRadarChart(
    skillScores: Map<SkillCategory, SkillScore>,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    val accentColor = BerlinTheme.accent
    val onHeroColor = BerlinTheme.onHero
    val gridColor = Color.White.copy(alpha = 0.16f)

    // The 8 axes in clockwise order
    val orderedCategories = listOf(
        SkillCategory.HOERVERSTEHEN to "HÖREN",
        SkillCategory.SPONTANES_SPRECHEN to "SPRECHEN",
        SkillCategory.INTERAKTION to "DIALOG",
        SkillCategory.LESEVERSTEHEN to "LESEN",
        SkillCategory.SCHREIBEN to "SCHREIBEN",
        SkillCategory.GRAMMATIK to "SYNTAX",
        SkillCategory.WORTSCHATZ to "LEXIK",
        SkillCategory.MEDIATION to "MEDIATION"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = min(size.width, size.height) / 2f - 38.dp.toPx()
        val numAxes = orderedCategories.size
        val angleStep = (2 * Math.PI / numAxes).toFloat()

        // 1. Concentric Web Rings (25%, 50%, 75%, 100%)
        val ringLevels = listOf(0.25f, 0.5f, 0.75f, 1.0f)
        ringLevels.forEach { level ->
            val ringPath = Path()
            for (i in 0 until numAxes) {
                val angle = i * angleStep - (Math.PI / 2).toFloat()
                val r = radius * level
                val x = center.x + r * cos(angle)
                val y = center.y + r * sin(angle)
                if (i == 0) ringPath.moveTo(x, y) else ringPath.lineTo(x, y)
            }
            ringPath.close()
            drawPath(
                path = ringPath,
                color = gridColor,
                style = Stroke(width = 1.dp.toPx())
            )
        }

        // 2. Axis Spokes radiating from center
        for (i in 0 until numAxes) {
            val angle = i * angleStep - (Math.PI / 2).toFloat()
            val endX = center.x + radius * cos(angle)
            val endY = center.y + radius * sin(angle)
            drawLine(
                color = gridColor,
                start = center,
                end = Offset(endX, endY),
                strokeWidth = 1.dp.toPx()
            )

            // Category Axis Labels
            val labelText = orderedCategories[i].second
            val labelRadius = radius + 22.dp.toPx()
            val labelX = center.x + labelRadius * cos(angle)
            val labelY = center.y + labelRadius * sin(angle)

            val textLayout = textMeasurer.measure(
                text = labelText,
                style = TextStyle(
                    fontFamily = BarlowCondensedFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = onHeroColor.copy(alpha = 0.9f)
                )
            )

            drawText(
                textLayoutResult = textLayout,
                topLeft = Offset(
                    labelX - textLayout.size.width / 2f,
                    labelY - textLayout.size.height / 2f
                )
            )
        }

        // 3. User Data Polygon Path
        val dataPath = Path()
        val points = mutableListOf<Offset>()

        orderedCategories.forEachIndexed { i, (category, _) ->
            val score = skillScores[category]?.score ?: 65
            val normalized = (score / 100f).coerceIn(0.15f, 1.0f)
            val angle = i * angleStep - (Math.PI / 2).toFloat()
            val r = radius * normalized
            val x = center.x + r * cos(angle)
            val y = center.y + r * sin(angle)
            val pt = Offset(x, y)
            points.add(pt)
            if (i == 0) dataPath.moveTo(x, y) else dataPath.lineTo(x, y)
        }
        dataPath.close()

        // Draw translucent teal polygon fill
        drawPath(
            path = dataPath,
            color = accentColor.copy(alpha = 0.28f)
        )

        // Draw crisp 2.5dp teal polygon outline stroke
        drawPath(
            path = dataPath,
            color = accentColor,
            style = Stroke(width = 2.5.dp.toPx())
        )

        // 4. Data Points & Glowing Nodes
        points.forEach { pt ->
            drawCircle(
                color = Color(0xFF08172B),
                radius = 5.dp.toPx(),
                center = pt
            )
            drawCircle(
                color = accentColor,
                radius = 3.5.dp.toPx(),
                center = pt
            )
        }
    }
}
