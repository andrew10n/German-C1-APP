package com.example.ui.theme

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.example.R

/**
 * User-selected personalized visual aesthetic across the entire app.
 */
enum class AppVibeTheme(
    val id: String,
    val displayName: String,
    val shortChip: String,
    val subtitle: String,
    val badgeTag: String,
    val isDark: Boolean
) {
    PORSCHE_911(
        id = "porsche_911",
        displayName = "Warmes Licht",
        shortChip = "Warm",
        subtitle = "Helle Galerie · Rotes Signal · Goldener Akzent",
        badgeTag = "KLAR",
        isDark = false
    ),
    AMG_RED(
        id = "amg_red",
        displayName = "Roter Fokus",
        shortChip = "Rot",
        subtitle = "Dunkler Grund · Kräftiges Rot · Aktive Akzente",
        badgeTag = "AKTIV",
        isDark = true
    ),
    MERCEDES_EQ_CYAN(
        id = "mercedes_eq_cyan",
        displayName = "Kühles Türkis",
        shortChip = "Türkis",
        subtitle = "Mitternachtsblau · Helles Türkis · Ruhiger Kontrast",
        badgeTag = "FOKUS",
        isDark = true
    ),
    BVB_DORTMUND(
        id = "bvb_dortmund",
        displayName = "Gelbe Energie",
        shortChip = "Gelb",
        subtitle = "Dunkler Grund · Signalgelb · Grüne Fortschrittspunkte",
        badgeTag = "WACH",
        isDark = true
    );

    companion object {
        fun fromId(id: String?): AppVibeTheme =
            entries.firstOrNull { it.id == id } ?: PORSCHE_911
    }
}

@Immutable
data class VibePalette(
    val theme: AppVibeTheme,
    val isDark: Boolean,
    val canvasBg: Color,
    val cardSurface: Color,
    val elevatedSurface: Color,
    val imprintStageBg: Color,
    val imprintStageSurface: Color,
    val imprintStageText: Color,
    val imprintStageSubtext: Color,
    val borderStroke: Color,
    val primaryAccent: Color,
    val onPrimaryAccent: Color,
    val secondaryAccent: Color,
    val onSecondaryAccent: Color,
    val tertiaryAccent: Color,
    val goldAccent: Color,
    val warningRed: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val heroTintTop: Color,
    val heroTintBottom: Color,
    @DrawableRes val heroDrawableRes: Int
)

fun paletteForVibe(theme: AppVibeTheme): VibePalette = when (theme) {
    AppVibeTheme.PORSCHE_911 -> VibePalette(
        theme = theme,
        isDark = false,
        canvasBg = Color(0xFFF5F3ED),           // Weissach Chalk Paper (Imprint crispness)
        cardSurface = Color(0xFFFFFFFF),        // Pure Gallery Card
        elevatedSurface = Color(0xFFECE9E0),    // Warm Architectural Slate
        imprintStageBg = Color(0xFF141821),     // Porsche Graphite Instrument Stage
        imprintStageSurface = Color(0xFF1E2430),
        imprintStageText = Color(0xFFF8FAFC),
        imprintStageSubtext = Color(0xFFCBD5E1),
        borderStroke = Color(0xFFDAD6CA),
        primaryAccent = Color(0xFFD5001C),      // Porsche Guards Red
        onPrimaryAccent = Color.White,
        secondaryAccent = Color(0xFF0F766E),    // Weissach Telemetry Teal
        onSecondaryAccent = Color.White,
        tertiaryAccent = Color(0xFF15803D),     // Track Sector Green
        goldAccent = Color(0xFFC98A00),         // Sport Chrono Aurum Gold
        warningRed = Color(0xFFC81E32),
        textPrimary = Color(0xFF11151E),
        textSecondary = Color(0xFF555E6F),
        heroTintTop = Color(0x28141821),
        heroTintBottom = Color(0xD9141821),
        heroDrawableRes = R.drawable.ctx_hero_studio
    )

    AppVibeTheme.AMG_RED -> VibePalette(
        theme = theme,
        isDark = true,
        canvasBg = Color(0xFF090A0E),           // Affalterbach Matte Carbon
        cardSurface = Color(0xFF13161E),        // Brushed Gunmetal
        elevatedSurface = Color(0xFF1D222E),    // Alcantara Slate
        imprintStageBg = Color(0xFF0B0D13),
        imprintStageSurface = Color(0xFF181C27),
        imprintStageText = Color(0xFFF8FAFC),
        imprintStageSubtext = Color(0xFFB4BFCE),
        borderStroke = Color(0xFF2D3342),
        primaryAccent = Color(0xFFFF1E38),      // AMG Performance Crimson
        onPrimaryAccent = Color.White,
        secondaryAccent = Color(0xFFE2E8F0),    // Brushed Titanium Alloy
        onSecondaryAccent = Color(0xFF090B0F),
        tertiaryAccent = Color(0xFF22C55E),     // Telemetry Emerald
        goldAccent = Color(0xFFFF8A00),         // Track Amber-Orange
        warningRed = Color(0xFFFF4D61),
        textPrimary = Color(0xFFF4F6FA),
        textSecondary = Color(0xFFA0ABBE),
        heroTintTop = Color(0x33090A0E),
        heroTintBottom = Color(0xE8090A0E),
        heroDrawableRes = R.drawable.ctx_ev_engineering
    )

    AppVibeTheme.MERCEDES_EQ_CYAN -> VibePalette(
        theme = theme,
        isDark = true,
        canvasBg = Color(0xFF060C16),           // Aero Midnight Indigo
        cardSurface = Color(0xFF0D1726),        // EQ Cockpit Surface
        elevatedSurface = Color(0xFF16253B),    // Luminous Navy Slate
        imprintStageBg = Color(0xFF07101C),
        imprintStageSurface = Color(0xFF122136),
        imprintStageText = Color(0xFFF1F7FC),
        imprintStageSubtext = Color(0xFFA7C0D8),
        borderStroke = Color(0xFF1F3654),
        primaryAccent = Color(0xFF00E5D1),      // Petronas Electric Cyan
        onPrimaryAccent = Color(0xFF051318),
        secondaryAccent = Color(0xFF38BDF8),    // High-Volt Sky Blue
        onSecondaryAccent = Color(0xFF051318),
        tertiaryAccent = Color(0xFF10B981),     // Recuperation Green
        goldAccent = Color(0xFFFBBF24),         // Amber Telemetry
        warningRed = Color(0xFFFF4D67),
        textPrimary = Color(0xFFF1F5FA),
        textSecondary = Color(0xFF96ADC6),
        heroTintTop = Color(0x30060C16),
        heroTintBottom = Color(0xEA060C16),
        heroDrawableRes = R.drawable.ctx_berlin_discourse
    )

    AppVibeTheme.BVB_DORTMUND -> VibePalette(
        theme = theme,
        isDark = true,
        canvasBg = Color(0xFF08090C),           // Borussia Pitch Black
        cardSurface = Color(0xFF12151B),        // Westfalen Anthracite
        elevatedSurface = Color(0xFF1C2029),    // Tunnel Slate
        imprintStageBg = Color(0xFF0A0C10),
        imprintStageSurface = Color(0xFF181C24),
        imprintStageText = Color(0xFFFAFAFA),
        imprintStageSubtext = Color(0xFFB5BCC9),
        borderStroke = Color(0xFF2B313E),
        primaryAccent = Color(0xFFFDE100),      // Signal Yellow 09
        onPrimaryAccent = Color(0xFF090B0F),
        secondaryAccent = Color(0xFF22C55E),    // Signal Iduna Turf Emerald
        onSecondaryAccent = Color(0xFF05160A),
        tertiaryAccent = Color(0xFF38BDF8),     // Tactical Chalk Blue
        goldAccent = Color(0xFFF59E0B),         // Floodlight Gold
        warningRed = Color(0xFFFF3B54),
        textPrimary = Color(0xFFF8F9FA),
        textSecondary = Color(0xFF9CA5B5),
        heroTintTop = Color(0x3008090C),
        heroTintBottom = Color(0xEC08090C),
        heroDrawableRes = R.drawable.ctx_football_coaching
    )
}

// Legacy compatibility color tokens (kept for untouched secondary views)
val ObsidianCanvas = Color(0xFF0A0C10)
val MidnightCard = Color(0xFF12161E)
val MidnightSurfaceVariant = Color(0xFF1B212D)
val MidnightBorder = Color(0xFF2A3346)

val IvoryText = Color(0xFFF4F6FA)
val CoolSlateText = Color(0xFF9BA6BA)

val BvbSignalYellow = Color(0xFFFDE100)
val RacingGold = Color(0xFFE5A000)
val AmgPetronasTeal = Color(0xFF00D2BE)
val TelemetryElectricBlue = Color(0xFF0EA5E9)
val TrackSectorGreen = Color(0xFF22C55E)
val PorscheRedline = Color(0xFFFF334B)
val UltravioletSector = Color(0xFFA855F7)

val DeepInk = Color(0xFF090B0F)
val MutedSlate = CoolSlateText

val IvoryPaper = Color(0xFFF5F3ED)
val PureCard = Color(0xFFFFFFFF)
val WarmSurfaceVariant = Color(0xFFECE9E0)
val WarmStoneBorder = Color(0xFFDAD6CA)

val CobaltInk = TelemetryElectricBlue
val CobaltBright = AmgPetronasTeal
val CobaltSoftContainer = Color(0xFF0B2E33)

val AmberGold = RacingGold
val AmberBright = BvbSignalYellow
val AmberSoftContainer = Color(0xFF332B05)

val EmeraldSage = TrackSectorGreen
val EmeraldBright = Color(0xFF4ADE80)
val EmeraldSoftContainer = Color(0xFF0A2E1C)

val CoralCrimson = PorscheRedline
val CoralSoftContainer = Color(0xFF380D16)

val PlumIndigo = UltravioletSector
val PlumSoftContainer = Color(0xFF27123B)

val CobaltLightDark = AmgPetronasTeal
val CobaltDarkContainer = Color(0xFF093236)
val AmberLightDark = BvbSignalYellow
val AmberDarkContainer = Color(0xFF3A3104)
val EmeraldLightDark = EmeraldBright
val EmeraldDarkContainer = Color(0xFF08331D)
val CoralLightDark = Color(0xFFFF6B7D)
val CoralDarkContainer = Color(0xFF420D18)

/**
 * Returns high-contrast foreground color (Carbon Black vs Crisp White) for any accent pill/button.
 */
fun contentColorOnAccent(accent: Color): Color {
    val luminance = 0.299f * accent.red + 0.587f * accent.green + 0.114f * accent.blue
    return if (luminance > 0.56f) Color(0xFF090B0F) else Color.White
}
