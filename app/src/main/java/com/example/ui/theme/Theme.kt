package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.example.programme.ProgrammeTheme

data class BerlinDesignTokens(
    val bg: Color,
    val surface: Color,
    val surface2: Color,
    val ink: Color,
    val ink2: Color,
    val line: Color,
    val hero: Color,
    val onHero: Color,
    val onHero2: Color,
    val accent: Color,
    val accentInk: Color,
    val accentSoft: Color,
    val onAccent: Color,
    val violet: Color,
    val violetSoft: Color,
    val sky: Color,
    val skySoft: Color,
    val coral: Color,
    val coralSoft: Color,
    val ok: Color,
    val okBg: Color
)

val LightBerlinTokens = BerlinDesignTokens(
    bg = BerlinLightBg,
    surface = BerlinLightSurface,
    surface2 = BerlinLightSurface2,
    ink = BerlinLightInk,
    ink2 = BerlinLightInk2,
    line = BerlinLightLine,
    hero = BerlinLightHero,
    onHero = BerlinLightOnHero,
    onHero2 = BerlinLightOnHero2,
    accent = BerlinLightAccent,
    accentInk = BerlinLightAccentInk,
    accentSoft = BerlinLightAccentSoft,
    onAccent = BerlinLightOnAccent,
    violet = BerlinLightViolet,
    violetSoft = BerlinLightVioletSoft,
    sky = BerlinLightSky,
    skySoft = BerlinLightSkySoft,
    coral = BerlinLightCoral,
    coralSoft = BerlinLightCoralSoft,
    ok = BerlinLightOk,
    okBg = BerlinLightOkBg
)

val DarkBerlinTokens = BerlinDesignTokens(
    bg = BerlinDarkBg,
    surface = BerlinDarkSurface,
    surface2 = BerlinDarkSurface2,
    ink = BerlinDarkInk,
    ink2 = BerlinDarkInk2,
    line = BerlinDarkLine,
    hero = BerlinDarkHero,
    onHero = BerlinDarkOnHero,
    onHero2 = BerlinDarkOnHero2,
    accent = BerlinDarkAccent,
    accentInk = BerlinDarkAccentInk,
    accentSoft = BerlinDarkAccentSoft,
    onAccent = BerlinDarkOnAccent,
    violet = BerlinDarkViolet,
    violetSoft = BerlinDarkVioletSoft,
    sky = BerlinDarkSky,
    skySoft = BerlinDarkSkySoft,
    coral = BerlinDarkCoral,
    coralSoft = BerlinDarkCoralSoft,
    ok = BerlinDarkOk,
    okBg = BerlinDarkOkBg
)

val LocalBerlinTokens = staticCompositionLocalOf { DarkBerlinTokens }

val BerlinTheme: BerlinDesignTokens
    @Composable
    @ReadOnlyComposable
    get() = LocalBerlinTokens.current

private val LightColorScheme = lightColorScheme(
    primary = BerlinLightAccent,
    onPrimary = BerlinLightOnAccent,
    primaryContainer = BerlinLightAccentSoft,
    onPrimaryContainer = BerlinLightAccentInk,
    secondary = BerlinLightViolet,
    onSecondary = Color.White,
    secondaryContainer = BerlinLightVioletSoft,
    onSecondaryContainer = BerlinLightInk,
    tertiary = BerlinLightSky,
    onTertiary = Color.White,
    tertiaryContainer = BerlinLightSkySoft,
    onTertiaryContainer = BerlinLightInk,
    error = BerlinLightCoral,
    onError = Color.White,
    errorContainer = BerlinLightCoralSoft,
    onErrorContainer = BerlinLightCoral,
    background = BerlinLightBg,
    onBackground = BerlinLightInk,
    surface = BerlinLightSurface,
    onSurface = BerlinLightInk,
    surfaceVariant = BerlinLightSurface2,
    onSurfaceVariant = BerlinLightInk2,
    outline = BerlinLightLine,
    outlineVariant = BerlinLightLine
)

private val DarkColorScheme = darkColorScheme(
    primary = BerlinDarkAccent,
    onPrimary = BerlinDarkOnAccent,
    primaryContainer = BerlinDarkAccentSoft,
    onPrimaryContainer = BerlinDarkAccentInk,
    secondary = BerlinDarkViolet,
    onSecondary = Color(0xFF0E1730),
    secondaryContainer = BerlinDarkVioletSoft,
    onSecondaryContainer = BerlinDarkInk,
    tertiary = BerlinDarkSky,
    onTertiary = Color(0xFF08172B),
    tertiaryContainer = DarkSkySoft,
    onTertiaryContainer = BerlinDarkInk,
    error = BerlinDarkCoral,
    onError = Color.White,
    errorContainer = BerlinDarkCoralSoft,
    onErrorContainer = BerlinDarkCoral,
    background = BerlinDarkBg,
    onBackground = BerlinDarkInk,
    surface = BerlinDarkSurface,
    onSurface = BerlinDarkInk,
    surfaceVariant = BerlinDarkSurface2,
    onSurfaceVariant = BerlinDarkInk2,
    outline = BerlinDarkLine,
    outlineVariant = BerlinDarkLine
)

@Composable
fun C1DeutschTheme(
    darkTheme: Boolean = true,
    palette: ProgrammeTheme = ProgrammeTheme.TRACKSIDE,
    content: @Composable () -> Unit
) {
    val baseTokens = if (darkTheme) DarkBerlinTokens else LightBerlinTokens
    val tokens = when (palette) {
        ProgrammeTheme.TRACKSIDE -> baseTokens
        ProgrammeTheme.SILVER -> if (darkTheme) {
            baseTokens.copy(
                accent = Color(0xFFD0D8E3),
                accentInk = Color(0xFFE7EDF4),
                accentSoft = Color(0xFF344252),
                onAccent = Color(0xFF15202B)
            )
        } else {
            baseTokens.copy(
                accent = Color(0xFF44556B),
                accentInk = Color(0xFF243247),
                accentSoft = Color(0xFFE2E9F1),
                onAccent = Color.White
            )
        }
        ProgrammeTheme.PITCH -> if (darkTheme) {
            baseTokens.copy(
                accent = Color(0xFFA8DB8C),
                accentInk = Color(0xFFD9F0CD),
                accentSoft = Color(0xFF2D4724),
                onAccent = Color(0xFF16270F)
            )
        } else {
            baseTokens.copy(
                accent = Color(0xFF3E6B2F),
                accentInk = Color(0xFF254219),
                accentSoft = Color(0xFFE1F0DA),
                onAccent = Color.White
            )
        }
    }
    val baseColorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val colorScheme = baseColorScheme.copy(
        primary = tokens.accent,
        onPrimary = tokens.onAccent,
        primaryContainer = tokens.accentSoft,
        onPrimaryContainer = tokens.accentInk
    )

    CompositionLocalProvider(LocalBerlinTokens provides tokens) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
