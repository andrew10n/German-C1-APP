package com.example.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

val LocalVibePalette = staticCompositionLocalOf { paletteForVibe(AppVibeTheme.PORSCHE_911) }

val AppShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(30.dp)
)

@Composable
private fun animateThemeColor(target: Color, label: String): Color {
    val animated by animateColorAsState(
        targetValue = target,
        animationSpec = tween(durationMillis = 520, easing = FastOutSlowInEasing),
        label = label
    )
    return animated
}

@Composable
fun GermanC1Theme(
    vibeTheme: AppVibeTheme = AppVibeTheme.PORSCHE_911,
    content: @Composable () -> Unit
) {
    val target = paletteForVibe(vibeTheme)

    val animatedPalette = VibePalette(
        theme = target.theme,
        isDark = target.isDark,
        canvasBg = animateThemeColor(target.canvasBg, "canvasBg"),
        cardSurface = animateThemeColor(target.cardSurface, "cardSurface"),
        elevatedSurface = animateThemeColor(target.elevatedSurface, "elevatedSurface"),
        imprintStageBg = animateThemeColor(target.imprintStageBg, "imprintStageBg"),
        imprintStageSurface = animateThemeColor(target.imprintStageSurface, "imprintStageSurface"),
        imprintStageText = animateThemeColor(target.imprintStageText, "imprintStageText"),
        imprintStageSubtext = animateThemeColor(target.imprintStageSubtext, "imprintStageSubtext"),
        borderStroke = animateThemeColor(target.borderStroke, "borderStroke"),
        primaryAccent = animateThemeColor(target.primaryAccent, "primaryAccent"),
        onPrimaryAccent = animateThemeColor(target.onPrimaryAccent, "onPrimaryAccent"),
        secondaryAccent = animateThemeColor(target.secondaryAccent, "secondaryAccent"),
        onSecondaryAccent = animateThemeColor(target.onSecondaryAccent, "onSecondaryAccent"),
        tertiaryAccent = animateThemeColor(target.tertiaryAccent, "tertiaryAccent"),
        goldAccent = animateThemeColor(target.goldAccent, "goldAccent"),
        warningRed = animateThemeColor(target.warningRed, "warningRed"),
        textPrimary = animateThemeColor(target.textPrimary, "textPrimary"),
        textSecondary = animateThemeColor(target.textSecondary, "textSecondary"),
        heroTintTop = animateThemeColor(target.heroTintTop, "heroTintTop"),
        heroTintBottom = animateThemeColor(target.heroTintBottom, "heroTintBottom"),
        heroDrawableRes = target.heroDrawableRes
    )

    val colorScheme = if (target.isDark) {
        darkColorScheme(
            primary = animatedPalette.primaryAccent,
            onPrimary = animatedPalette.onPrimaryAccent,
            primaryContainer = animatedPalette.primaryAccent.copy(alpha = 0.22f),
            onPrimaryContainer = animatedPalette.primaryAccent,
            secondary = animatedPalette.secondaryAccent,
            onSecondary = animatedPalette.onSecondaryAccent,
            secondaryContainer = animatedPalette.secondaryAccent.copy(alpha = 0.18f),
            onSecondaryContainer = animatedPalette.textPrimary,
            tertiary = animatedPalette.tertiaryAccent,
            onTertiary = contentColorOnAccent(animatedPalette.tertiaryAccent),
            tertiaryContainer = animatedPalette.tertiaryAccent.copy(alpha = 0.20f),
            onTertiaryContainer = animatedPalette.textPrimary,
            error = animatedPalette.warningRed,
            onError = Color.White,
            errorContainer = animatedPalette.warningRed.copy(alpha = 0.18f),
            onErrorContainer = animatedPalette.textPrimary,
            background = animatedPalette.canvasBg,
            onBackground = animatedPalette.textPrimary,
            surface = animatedPalette.cardSurface,
            onSurface = animatedPalette.textPrimary,
            surfaceVariant = animatedPalette.elevatedSurface,
            onSurfaceVariant = animatedPalette.textSecondary,
            outline = animatedPalette.borderStroke
        )
    } else {
        lightColorScheme(
            primary = animatedPalette.primaryAccent,
            onPrimary = animatedPalette.onPrimaryAccent,
            primaryContainer = animatedPalette.primaryAccent.copy(alpha = 0.12f),
            onPrimaryContainer = animatedPalette.primaryAccent,
            secondary = animatedPalette.secondaryAccent,
            onSecondary = animatedPalette.onSecondaryAccent,
            secondaryContainer = animatedPalette.secondaryAccent.copy(alpha = 0.12f),
            onSecondaryContainer = animatedPalette.textPrimary,
            tertiary = animatedPalette.tertiaryAccent,
            onTertiary = Color.White,
            tertiaryContainer = animatedPalette.tertiaryAccent.copy(alpha = 0.12f),
            onTertiaryContainer = animatedPalette.textPrimary,
            error = animatedPalette.warningRed,
            onError = Color.White,
            errorContainer = animatedPalette.warningRed.copy(alpha = 0.12f),
            onErrorContainer = animatedPalette.warningRed,
            background = animatedPalette.canvasBg,
            onBackground = animatedPalette.textPrimary,
            surface = animatedPalette.cardSurface,
            onSurface = animatedPalette.textPrimary,
            surfaceVariant = animatedPalette.elevatedSurface,
            onSurfaceVariant = animatedPalette.textSecondary,
            outline = animatedPalette.borderStroke
        )
    }

    CompositionLocalProvider(LocalVibePalette provides animatedPalette) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = AppShapes,
            content = content
        )
    }
}
