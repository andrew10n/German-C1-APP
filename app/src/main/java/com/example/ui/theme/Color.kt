package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// =========================================================================
// Berlin U-Bahn Wayfinding Design Tokens
// =========================================================================

// Light Tokens
val BerlinLightBg = Color(0xFFEEF2F7)
val BerlinLightSurface = Color(0xFFFFFFFF)
val BerlinLightSurface2 = Color(0xFFE4EAF2)
val BerlinLightInk = Color(0xFF0E1730)          // Navy, main text
val BerlinLightInk2 = Color(0xFF55607A)        // Secondary text
val BerlinLightLine = Color(0xFFDAE1EB)        // Borders, inactive track
val BerlinLightHero = Color(0xFF0B1530)        // Dark hero card holding radar chart
val BerlinLightOnHero = Color(0xFFF1F5FC)
val BerlinLightOnHero2 = Color(0xFF9FB0D0)
val BerlinLightAccent = Color(0xFF2DD4BF)      // Bright teal: only action color
val BerlinLightAccentInk = Color(0xFF0B7F72)
val BerlinLightAccentSoft = Color(0xFFD5F5F0)
val BerlinLightOnAccent = Color(0xFF08172B)    // Dark navy text on teal
val BerlinLightViolet = Color(0xFF7C6CF0)      // Category separator
val BerlinLightVioletSoft = Color(0xFFE6E2FF)
val BerlinLightSky = Color(0xFF3F8CFF)         // Category separator
val BerlinLightSkySoft = Color(0xFFDCEAFF)
val BerlinLightCoral = Color(0xFFE5484D)       // Errors and alerts only
val BerlinLightCoralSoft = Color(0xFFFDE8E9)
val BerlinLightOk = Color(0xFF1C8A55)          // Success green
val BerlinLightOkBg = Color(0xFFE3F4EB)

// Dark Tokens (Deep Obsidian-Navy + Electric Teal matching reference design)
val BerlinDarkBg = Color(0xFF050B18)
val BerlinDarkSurface = Color(0xFF0A1428)
val BerlinDarkSurface2 = Color(0xFF111F3A)
val BerlinDarkInk = Color(0xFFF1F5FC)
val BerlinDarkInk2 = Color(0xFF9FB0D0)
val BerlinDarkLine = Color(0xFF1C2D4F)
val BerlinDarkHero = Color(0xFF071124)
val BerlinDarkOnHero = Color(0xFFF1F5FC)
val BerlinDarkOnHero2 = Color(0xFF9FB0D0)
val BerlinDarkAccent = Color(0xFF1CE5C8)
val BerlinDarkAccentInk = Color(0xFF1CE5C8)
val BerlinDarkAccentSoft = Color(0xFF0D2E36)
val BerlinDarkOnAccent = Color(0xFF04131E)
val BerlinDarkViolet = Color(0xFFA196FF)
val BerlinDarkVioletSoft = Color(0xFF1E1A42)
val BerlinDarkSky = Color(0xFF6AA7FF)
val BerlinDarkSkySoft = Color(0xFF10254A)
val BerlinDarkCoral = Color(0xFFFF7075)
val BerlinDarkCoralSoft = Color(0xFF34161E)
val BerlinDarkOk = Color(0xFF4CC38A)
val BerlinDarkOkBg = Color(0xFF0F2E25)

// Primary Active Tokens (defaults to teal & berlin palette)
val BerlinTeal = BerlinLightAccent
val BerlinOnTeal = BerlinLightOnAccent
val BerlinHeroNavy = BerlinLightHero

// Hero radial glow brush: radial gradient at 50% 38%, rgba(45,212,191,.16) to transparent
val BerlinHeroGlowBrush = Brush.radialGradient(
    colors = listOf(
        Color(0xFF2DD4BF).copy(alpha = 0.18f),
        Color.Transparent
    )
)

// Legacy Aliases for seamless compilation across existing code
val ModernCobalt = BerlinLightAccent
val ModernAzure = BerlinLightAccent
val ModernAzureDark = BerlinLightAccentInk
val ModernAzureLight = BerlinLightAccentSoft
val ElectricCyan = BerlinLightAccent
val ElectricCyanDark = BerlinLightAccentInk
val ElectricCyanLight = BerlinLightAccentSoft
val GermanGold = BerlinLightAccent
val GermanGoldDark = BerlinLightAccentInk
val GermanGoldLight = BerlinLightAccentSoft

val SlateNavyDark = BerlinDarkBg
val SlateNavySurface = BerlinDarkSurface
val SlateNavyCard = BerlinDarkSurface2
val SlateNavyCardElevated = BerlinDarkSurface2
val SlateNavyBorder = BerlinDarkLine

val ElectricViolet = BerlinLightViolet
val ElectricVioletLight = BerlinLightViolet
val ElectricVioletGlow = BerlinLightViolet

val NeonCoral = BerlinLightCoral
val NeonCoralDark = BerlinLightCoral
val GermanCrimson = BerlinLightCoral
val GermanCrimsonDark = BerlinLightCoral

val NeonMint = BerlinLightOk
val NeonMintDark = BerlinLightOk
val GermanEmerald = BerlinLightOk
val GermanEmeraldDark = BerlinLightOk

val SlateLightBg = BerlinLightBg
val SlateLightSurface = BerlinLightSurface
val SlateLightCard = BerlinLightSurface2
val SlateLightBorder = BerlinLightLine
val SlateTextPrimary = BerlinLightInk
val SlateTextSecondary = BerlinLightInk2

val DarkSkySoft = BerlinDarkSkySoft

val GlassCardGradient = Brush.verticalGradient(
    colors = listOf(
        Color.Transparent,
        Color.Transparent
    )
)

val EmeraldNeonGradient = Brush.horizontalGradient(
    colors = listOf(BerlinLightOk, BerlinLightOk)
)
val CyanAuroraGradient = Brush.horizontalGradient(
    colors = listOf(BerlinLightAccent, BerlinLightAccent)
)
val VioletAuroraGradient = Brush.horizontalGradient(
    colors = listOf(BerlinLightViolet, BerlinLightViolet)
)
val CoralNeonGradient = Brush.horizontalGradient(
    colors = listOf(BerlinLightCoral, BerlinLightCoral)
)
