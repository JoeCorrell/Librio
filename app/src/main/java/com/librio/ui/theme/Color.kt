package com.librio.ui.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.compositeOver

/**
 * App Theme Color Palettes
 * Each theme uses a limited palette of 12 subtle shades derived from a base color
 */
enum class AppTheme(val displayName: String) {
    DARK_TEAL("Dark Teal"),
    // 35 curated themes for the app
    TEAL("Teal"),
    OCEAN("Ocean Blue"),
    PURPLE("Purple"),
    ROSE("Rose"),
    AMBER("Amber"),
    EMERALD("Emerald"),
    MIDNIGHT("Midnight"),
    CORAL("Coral"),
    FOREST("Forest"),
    SUNSET("Sunset"),
    LAVENDER("Lavender"),
    CRIMSON("Crimson"),
    CYAN("Cyan"),
    INDIGO("Indigo"),
    GOLD("Gold"),
    MINT("Mint"),
    SKY("Sky"),
    WINE("Wine"),
    SAPPHIRE("Sapphire"),
    PEACH("Peach"),
    // 15 new themes
    CHERRY("Cherry"),
    JADE("Jade"),
    COBALT("Cobalt"),
    BRONZE("Bronze"),
    ORCHID("Orchid"),
    RUST("Rust"),
    STEEL("Steel"),
    PLUM("Plum"),
    TURQUOISE("Turquoise"),
    SAND("Sand"),
    SLATE("Slate"),
    RASPBERRY("Raspberry"),
    HONEY("Honey"),
    OLIVE("Olive"),
    BLUSH("Blush"),
    PASTEL_MINT("Pastel Mint"),
    PASTEL_PEACH("Pastel Peach"),
    PASTEL_LILAC("Pastel Lilac"),
    PASTEL_SKY("Pastel Sky"),
    PASTEL_SAND("Pastel Sand"),
    PASTEL_LEMON("Pastel Lemon"),
    PASTEL_CORAL("Pastel Coral"),
    PASTEL_MAUVE("Pastel Mauve"),
    PASTEL_SAGE("Pastel Sage"),
    PASTEL_SEAFOAM("Pastel Seafoam"),
    PASTEL_PERIWINKLE("Pastel Periwinkle"),
    PASTEL_SUNSET("Pastel Sunset"),
    PASTEL_FOG("Pastel Fog"),
    PASTEL_CLAY("Pastel Clay"),
    PASTEL_BERRY("Pastel Berry"),
    OBSIDIAN("Obsidian"),
    DEEP_NAVY("Deep Navy"),
    INK("Ink"),
    GUNMETAL("Gunmetal"),
    STORM("Storm"),
    MIDNIGHT_PURPLE("Midnight Purple"),
    DEEP_FOREST("Deep Forest"),
    SHADOW("Shadow"),
    DEEP_PLUM("Deep Plum"),
    NIGHT_WINE("Night Wine"),
    DEEP_MAROON("Deep Maroon"),
    DEEP_INDIGO("Deep Indigo"),
    STEALTH_BLUE("Stealth Blue"),
    DIESEL_RED("Diesel Red"),
    DEEP_BRONZE("Deep Bronze"),
    DEEP_EMERALD("Deep Emerald"),
    DUSK("Dusk"),
    TWILIGHT("Twilight"),
    DEEP_SLATE("Deep Slate"),
    COBALT_NIGHT("Cobalt Night"),
    ASHEN_BLUE("Ashen Blue"),
    DEEP_OLIVE("Deep Olive"),
    MIDNIGHT_TEAL("Midnight Teal"),
    DEEP_CHARCOAL("Deep Charcoal"),
    DARK_PASTEL_MINT("Dark Pastel Mint"),
    DARK_PASTEL_PEACH("Dark Pastel Peach"),
    DARK_PASTEL_LILAC("Dark Pastel Lilac"),
    DARK_PASTEL_SKY("Dark Pastel Sky"),
    DARK_PASTEL_SAND("Dark Pastel Sand"),
    DARK_PASTEL_LEMON("Dark Pastel Lemon"),
    DARK_PASTEL_CORAL("Dark Pastel Coral"),
    DARK_PASTEL_MAUVE("Dark Pastel Mauve"),
    DARK_PASTEL_SAGE("Dark Pastel Sage"),
    DARK_PASTEL_SEAFOAM("Dark Pastel Seafoam"),
    DARK_PASTEL_PERIWINKLE("Dark Pastel Periwinkle"),
    DARK_PASTEL_SUNSET("Dark Pastel Sunset"),
    DARK_PASTEL_FOG("Dark Pastel Fog"),
    DARK_PASTEL_CLAY("Dark Pastel Clay"),
    DARK_PASTEL_BERRY("Dark Pastel Berry"),
    // Additional Teal & Aqua themes
    AQUAMARINE("Aquamarine"),
    CARIBBEAN("Caribbean"),
    PEACOCK("Peacock"),
    LAGOON("Lagoon"),
    ARCTIC_TEAL("Arctic Teal"),
    DEEP_CYAN("Deep Cyan"),
    OCEAN_TEAL("Ocean Teal"),
    TROPICAL_TEAL("Tropical Teal"),
    // Additional Purple & Violet themes
    AMETHYST("Amethyst"),
    GRAPE("Grape"),
    VIOLET("Violet"),
    WISTERIA("Wisteria"),
    // Additional Pink & Red themes
    MAGENTA("Magenta"),
    CARNATION("Carnation"),
    RUBY("Ruby"),
    SCARLET("Scarlet"),
    // Additional Green & Olive themes
    LIME("Lime"),
    PISTACHIO("Pistachio"),
    FERN("Fern"),
    // Additional Neutral & Gray themes
    SILVER("Silver"),
    GRAPHITE("Graphite"),
    PEWTER("Pewter"),
    TITANIUM("Titanium"),
    CHARCOAL("Charcoal"),
    // Additional themes for category completion
    CERULEAN("Cerulean"),
    DENIM("Denim"),
    AZURE("Azure"),
    MAUVE("Mauve"),
    IRIS("Iris"),
    FUCHSIA("Fuchsia"),
    SAGE("Sage"),
    MOSS("Moss"),
    CUSTOM("Custom");

    /**
     * Returns true if this is a dark-themed theme
     */
    fun isDark(): Boolean = when (this) {
        DARK_TEAL, OBSIDIAN, DEEP_NAVY, INK, GUNMETAL, STORM,
        MIDNIGHT_PURPLE, DEEP_FOREST, SHADOW, DEEP_PLUM, NIGHT_WINE,
        DEEP_MAROON, DEEP_INDIGO, STEALTH_BLUE, DIESEL_RED, DEEP_BRONZE,
        DEEP_EMERALD, DUSK, TWILIGHT, DEEP_SLATE, COBALT_NIGHT,
        ASHEN_BLUE, DEEP_OLIVE, MIDNIGHT_TEAL, DEEP_CHARCOAL,
        DARK_PASTEL_MINT, DARK_PASTEL_PEACH, DARK_PASTEL_LILAC,
        DARK_PASTEL_SKY, DARK_PASTEL_SAND, DARK_PASTEL_LEMON,
        DARK_PASTEL_CORAL, DARK_PASTEL_MAUVE, DARK_PASTEL_SAGE,
        DARK_PASTEL_SEAFOAM, DARK_PASTEL_PERIWINKLE, DARK_PASTEL_SUNSET,
        DARK_PASTEL_FOG, DARK_PASTEL_CLAY, DARK_PASTEL_BERRY -> true
        else -> false
    }

    /**
     * Get the corresponding light theme for a dark theme
     */
    fun toLightTheme(): AppTheme = when (this) {
        DARK_TEAL -> TEAL
        OBSIDIAN -> TEAL
        DEEP_NAVY -> OCEAN
        INK -> MIDNIGHT
        GUNMETAL -> STEEL
        STORM -> SLATE
        MIDNIGHT_PURPLE -> PURPLE
        DEEP_FOREST -> FOREST
        SHADOW -> STEEL
        DEEP_PLUM -> PLUM
        NIGHT_WINE -> WINE
        DEEP_MAROON -> CRIMSON
        DEEP_INDIGO -> INDIGO
        STEALTH_BLUE -> COBALT
        DIESEL_RED -> RUST
        DEEP_BRONZE -> BRONZE
        DEEP_EMERALD -> EMERALD
        DUSK -> SUNSET
        TWILIGHT -> LAVENDER
        DEEP_SLATE -> SLATE
        COBALT_NIGHT -> COBALT
        ASHEN_BLUE -> SKY
        DEEP_OLIVE -> OLIVE
        MIDNIGHT_TEAL -> TEAL
        DEEP_CHARCOAL -> STEEL
        DARK_PASTEL_MINT -> PASTEL_MINT
        DARK_PASTEL_PEACH -> PASTEL_PEACH
        DARK_PASTEL_LILAC -> PASTEL_LILAC
        DARK_PASTEL_SKY -> PASTEL_SKY
        DARK_PASTEL_SAND -> PASTEL_SAND
        DARK_PASTEL_LEMON -> PASTEL_LEMON
        DARK_PASTEL_CORAL -> PASTEL_CORAL
        DARK_PASTEL_MAUVE -> PASTEL_MAUVE
        DARK_PASTEL_SAGE -> PASTEL_SAGE
        DARK_PASTEL_SEAFOAM -> PASTEL_SEAFOAM
        DARK_PASTEL_PERIWINKLE -> PASTEL_PERIWINKLE
        DARK_PASTEL_SUNSET -> PASTEL_SUNSET
        DARK_PASTEL_FOG -> PASTEL_FOG
        DARK_PASTEL_CLAY -> PASTEL_CLAY
        DARK_PASTEL_BERRY -> PASTEL_BERRY
        else -> this // Already light
    }

    /**
     * Get the corresponding dark theme for a light theme
     */
    fun toDarkTheme(): AppTheme = when (this) {
        TEAL -> DARK_TEAL
        OCEAN -> DEEP_NAVY
        PURPLE -> MIDNIGHT_PURPLE
        ROSE -> DEEP_PLUM
        AMBER -> DEEP_BRONZE
        EMERALD -> DEEP_EMERALD
        MIDNIGHT -> INK
        CORAL -> DIESEL_RED
        FOREST -> DEEP_FOREST
        SUNSET -> DUSK
        LAVENDER -> TWILIGHT
        CRIMSON -> DEEP_MAROON
        CYAN -> MIDNIGHT_TEAL
        INDIGO -> DEEP_INDIGO
        GOLD -> DEEP_BRONZE
        MINT -> DARK_PASTEL_MINT
        SKY -> ASHEN_BLUE
        WINE -> NIGHT_WINE
        SAPPHIRE -> COBALT_NIGHT
        PEACH -> DARK_PASTEL_PEACH
        CHERRY -> DIESEL_RED
        JADE -> DEEP_EMERALD
        COBALT -> STEALTH_BLUE
        BRONZE -> DEEP_BRONZE
        ORCHID -> DARK_PASTEL_LILAC
        RUST -> DIESEL_RED
        STEEL -> GUNMETAL
        PLUM -> DEEP_PLUM
        TURQUOISE -> MIDNIGHT_TEAL
        SAND -> DARK_PASTEL_SAND
        SLATE -> DEEP_SLATE
        RASPBERRY -> DARK_PASTEL_BERRY
        HONEY -> DARK_PASTEL_LEMON
        OLIVE -> DEEP_OLIVE
        BLUSH -> DARK_PASTEL_CORAL
        PASTEL_MINT -> DARK_PASTEL_MINT
        PASTEL_PEACH -> DARK_PASTEL_PEACH
        PASTEL_LILAC -> DARK_PASTEL_LILAC
        PASTEL_SKY -> DARK_PASTEL_SKY
        PASTEL_SAND -> DARK_PASTEL_SAND
        PASTEL_LEMON -> DARK_PASTEL_LEMON
        PASTEL_CORAL -> DARK_PASTEL_CORAL
        PASTEL_MAUVE -> DARK_PASTEL_MAUVE
        PASTEL_SAGE -> DARK_PASTEL_SAGE
        PASTEL_SEAFOAM -> DARK_PASTEL_SEAFOAM
        PASTEL_PERIWINKLE -> DARK_PASTEL_PERIWINKLE
        PASTEL_SUNSET -> DARK_PASTEL_SUNSET
        PASTEL_FOG -> DARK_PASTEL_FOG
        PASTEL_CLAY -> DARK_PASTEL_CLAY
        PASTEL_BERRY -> DARK_PASTEL_BERRY
        CUSTOM -> OBSIDIAN // Default dark for custom
        else -> OBSIDIAN // Default fallback
    }
}

// Global custom color storage (set from SettingsRepository)
// Now uses single base color that generates all shades
object CustomThemeColors {
    var baseColor: Color = Color(0xFF00897B) // Single base color for entire theme
    // Legacy support - map to base color
    var primaryColor: Color
        get() = baseColor
        set(value) { baseColor = value }
    var accentColor: Color
        get() = baseColor
        set(value) { baseColor = value }
    var backgroundColor: Color = Color(0xFFFFFFFF)
}

/**
 * Creates a custom palette from a single base color
 * Automatically generates all 12 shades and derives all theme colors
 */
fun createCustomPalette(
    baseColor: Color = CustomThemeColors.baseColor
): ThemePalette {
    // Generate all shades from the base color
    val shades = createShadesFromColor(baseColor)
    val shade1 = shades[0] // Deepest
    val shade2 = shades[1]
    val shade3 = shades[2]
    val shade4 = shades[3] // Base
    val shade5 = shades[4]
    val shade6 = shades[5]
    val shade7 = shades[6]
    val shade8 = shades[7]
    val shade9 = shades[8]
    val shade10 = shades[9]
    val shade11 = shades[10]
    val shade12 = shades[11] // Softest tint

    // Derive divider color from shade
    val divider = Color(
        red = (shade5.red * 0.8f + 0.2f).coerceIn(0f, 1f),
        green = (shade5.green * 0.8f + 0.2f).coerceIn(0f, 1f),
        blue = (shade5.blue * 0.8f + 0.2f).coerceIn(0f, 1f)
    )

    val hasCustomBackground = CustomThemeColors.backgroundColor.alpha > 0f && CustomThemeColors.backgroundColor != Color.White
    val bg = if (hasCustomBackground) CustomThemeColors.backgroundColor else shade9
    val surfaceColor = if (hasCustomBackground) bg else shade10
    val bgLuminance = (bg.red * 0.299f + bg.green * 0.587f + bg.blue * 0.114f)
    val contrastTextPrimary = if (bgLuminance < 0.5f) Color.White else Color(0xFF111111)
    val contrastSecondary = if (bgLuminance < 0.5f) Color(0xFFD0D0D0) else Color(0xFF444444)
    val contrastMuted = if (bgLuminance < 0.5f) Color(0xFFB0B0B0) else Color(0xFF666666)

    return ThemePalette(
        primary = baseColor,
        primaryLight = shade5,
        primaryDark = shade2,
        accent = baseColor,
        accentGradientStart = shade2,
        accentGradientEnd = shade7,
        background = bg,
        surface = surfaceColor,
        surfaceDark = shade2,
        surfaceMedium = shade7,
        surfaceLight = shade10,
        surfaceCard = baseColor,
        headerBackground = shade2,
        textPrimary = contrastTextPrimary,
        textSecondary = contrastSecondary,
        textMuted = contrastMuted,
        onPrimary = Color.White,
        onBackground = contrastTextPrimary,
        onSurface = contrastTextPrimary,
        divider = divider,
        shade1 = shade1,
        shade2 = shade2,
        shade3 = shade3,
        shade4 = shade4,
        shade5 = shade5,
        shade6 = shade6,
        shade7 = shade7,
        shade8 = shade8,
        shade9 = shade9,
        shade10 = shade10,
        shade11 = shade11,
        shade12 = shade12
    )
}

// Extension function to composite colors
private fun Color.compositeOver(background: Color): Color {
    val fg = this
    val bg = background
    val a = fg.alpha + bg.alpha * (1f - fg.alpha)
    val r = (fg.red * fg.alpha + bg.red * bg.alpha * (1f - fg.alpha)) / a
    val g = (fg.green * fg.alpha + bg.green * bg.alpha * (1f - fg.alpha)) / a
    val b = (fg.blue * fg.alpha + bg.blue * bg.alpha * (1f - fg.alpha)) / a
    return Color(r, g, b, a)
}

/**
 * Background Theme Options - matches AppTheme with light/dark variants
 * Each option generates a subtle tinted background from its theme color
 */
enum class BackgroundTheme(val displayName: String, val baseTheme: AppTheme? = null) {
    // Neutral options
    DEFAULT("Default (Theme)", null),

    // Theme-based backgrounds (light tinted versions)
    TEAL("Teal", AppTheme.TEAL),
    OCEAN("Ocean Blue", AppTheme.OCEAN),
    PURPLE("Purple", AppTheme.PURPLE),
    ROSE("Rose", AppTheme.ROSE),
    AMBER("Amber", AppTheme.AMBER),
    EMERALD("Emerald", AppTheme.EMERALD),
    MIDNIGHT("Midnight", AppTheme.MIDNIGHT),
    CORAL("Coral", AppTheme.CORAL),
    FOREST("Forest", AppTheme.FOREST),
    SUNSET("Sunset", AppTheme.SUNSET),
    LAVENDER("Lavender", AppTheme.LAVENDER),
    CRIMSON("Crimson", AppTheme.CRIMSON),
    CYAN("Cyan", AppTheme.CYAN),
    INDIGO("Indigo", AppTheme.INDIGO),
    GOLD("Gold", AppTheme.GOLD),
    MINT("Mint", AppTheme.MINT),
    SKY("Sky", AppTheme.SKY),
    WINE("Wine", AppTheme.WINE),
    SAPPHIRE("Sapphire", AppTheme.SAPPHIRE),
    PEACH("Peach", AppTheme.PEACH),
    CHERRY("Cherry", AppTheme.CHERRY),
    JADE("Jade", AppTheme.JADE),
    COBALT("Cobalt", AppTheme.COBALT),
    BRONZE("Bronze", AppTheme.BRONZE),
    ORCHID("Orchid", AppTheme.ORCHID),
    RUST("Rust", AppTheme.RUST),
    STEEL("Steel", AppTheme.STEEL),
    PLUM("Plum", AppTheme.PLUM),
    TURQUOISE("Turquoise", AppTheme.TURQUOISE),
    SAND("Sand", AppTheme.SAND),
    SLATE("Slate", AppTheme.SLATE),
    RASPBERRY("Raspberry", AppTheme.RASPBERRY),
    HONEY("Honey", AppTheme.HONEY),
    OLIVE("Olive", AppTheme.OLIVE),
    BLUSH("Blush", AppTheme.BLUSH)
}

/**
 * Get background colors for each background theme
 * Returns (backgroundColor, surfaceColor) pair
 * For theme-based options, generates light tinted versions from the theme's palette
 */
fun getBackgroundColors(theme: BackgroundTheme, darkMode: Boolean = false): Pair<Color, Color> {
    // For DEFAULT, return transparent to use the theme's own background
    if (theme == BackgroundTheme.DEFAULT || theme.baseTheme == null) {
        return Pair(Color.Transparent, Color.Transparent)
    }

    // Get the palette for the base theme
    val palette = getThemePalette(theme.baseTheme, false)

    return if (darkMode) {
        // Dark mode: use darker shades with theme tint
        val darkBg = Color(
            red = (0.10f + palette.accent.red * 0.06f).coerceIn(0f, 0.2f),
            green = (0.10f + palette.accent.green * 0.06f).coerceIn(0f, 0.2f),
            blue = (0.12f + palette.accent.blue * 0.06f).coerceIn(0f, 0.22f)
        )
        val darkSurface = Color(
            red = (0.08f + palette.accent.red * 0.04f).coerceIn(0f, 0.15f),
            green = (0.08f + palette.accent.green * 0.04f).coerceIn(0f, 0.15f),
            blue = (0.10f + palette.accent.blue * 0.04f).coerceIn(0f, 0.18f)
        )
        Pair(darkBg, darkSurface)
    } else {
        // Light mode: use the palette's light shades (shade11, shade12)
        Pair(palette.shade11, palette.shade12)
    }
}

/**
 * Color palette for each theme - supports light mode
 * Uses a limited palette of 12 saturated shades derived from a base accent color
 * IMPORTANT: Light shades blend with a tinted base color, NOT pure white
 * This ensures gradients stay vibrant and never look washed out
 *
 * Shade levels (saturation-preserving):
 * - shade1: Deepest (50% brightness) - rich and dark
 * - shade2: Deep (65% brightness)
 * - shade3: Dark (80% brightness)
 * - shade4: Base accent color
 * - shade5: Bright (10% lighter, preserving saturation)
 * - shade6: Lighter (20% blend with tint)
 * - shade7: Soft (35% blend with tint)
 * - shade8: Gentle (50% blend with tint)
 * - shade9: Light tint (65% blend) - still visibly colored
 * - shade10: Soft tint (75% blend)
 * - shade11: Pale tint (85% blend) - subtle but colored
 * - shade12: Lightest tint (92% blend) - very light but NOT white
 */
data class ThemePalette(
    val primary: Color,
    val primaryLight: Color,
    val primaryDark: Color,
    val accent: Color,
    val accentGradientStart: Color,
    val accentGradientEnd: Color,
    val surfaceDark: Color,
    val surfaceMedium: Color,
    val surfaceLight: Color,
    val surfaceCard: Color,
    // Limited palette shades (12 saturated shades derived from accent - NO white)
    val shade1: Color = Color(0xFF126868), // Deepest - rich and dark
    val shade2: Color = Color(0xFF178888), // Deep
    val shade3: Color = Color(0xFF1E9E9E), // Dark
    val shade4: Color = Color(0xFF25B0B0), // Base accent
    val shade5: Color = Color(0xFF35BABA), // Bright
    val shade6: Color = Color(0xFF4AC4C4), // Lighter
    val shade7: Color = Color(0xFF68D0D0), // Soft
    val shade8: Color = Color(0xFF88DADA), // Gentle
    val shade9: Color = Color(0xFFA8E4E4), // Light tint - still saturated
    val shade10: Color = Color(0xFFC2EBEB), // Soft tint
    val shade11: Color = Color(0xFFDAF2F2), // Pale tint - subtle but colored
    val shade12: Color = Color(0xFFE8F6F6), // Lightest - still tinted, not white
    // Light mode colors
    val background: Color = Color(0xFFF8FAFA),
    val surface: Color = Color(0xFFFAFCFC),
    // Content-specific background (for library, e-reader, comic reader only)
    // Defaults to background, but can be overridden by BackgroundTheme
    val contentBackground: Color = Color(0xFFF8FAFA),
    val onPrimary: Color = Color.White,
    val onBackground: Color = Color(0xFF1A1A1A),
    val onSurface: Color = Color(0xFF1A1A1A),
    val textPrimary: Color = Color(0xFF1A1A1A),
    val textSecondary: Color = Color(0xFF455A64),
    val textMuted: Color = Color(0xFF78909C),
    val divider: Color = Color(0xFFB0BEC5),
    val headerBackground: Color = Color(0xFF178888)
)

/**
 * Creates a complete ThemePalette from a single accent color
 * Automatically generates all 12 shades and ensures proper text contrast
 */
fun createPaletteFromAccent(accent: Color): ThemePalette {
    val shades = createShadesFromColor(accent)

    // Calculate luminance to determine text contrast needs
    val luminance = accent.red * 0.299f + accent.green * 0.587f + accent.blue * 0.114f
    val isPastel = luminance > 0.65f

    // For buttons/headers on dark backgrounds, use white text
    // For pastel themes, use shade1 (which is now a proper dark color) for accents
    val accentForUI = if (isPastel) shades[0] else accent

    return ThemePalette(
        primary = accentForUI,
        primaryLight = shades[5],
        primaryDark = shades[0],
        accent = accentForUI,
        accentGradientStart = shades[0],
        accentGradientEnd = shades[3], // Use base color, not too light
        surfaceDark = shades[0],
        surfaceMedium = shades[7],
        surfaceLight = shades[9],
        surfaceCard = accentForUI,
        shade1 = shades[0],
        shade2 = shades[1],
        shade3 = shades[2],
        shade4 = shades[3],
        shade5 = shades[4],
        shade6 = shades[5],
        shade7 = shades[6],
        shade8 = shades[7],
        shade9 = shades[8],
        shade10 = shades[9],
        shade11 = shades[10],
        shade12 = shades[11],
        background = shades[11],
        surface = Color.White,
        onPrimary = Color.White,
        onBackground = Color(0xFF1A1A1A),
        onSurface = Color(0xFF1A1A1A),
        textPrimary = Color(0xFF1A1A1A),
        textSecondary = Color(0xFF37474F),
        textMuted = Color(0xFF607D8B),
        divider = Color(0xFFB0BEC5),
        headerBackground = shades[1]
    )
}

// Teal Theme (Default)
val TealPalette = createPaletteFromAccent(Color(0xFF0F8A8A))

// Ocean Blue Theme
val OceanPalette = createPaletteFromAccent(Color(0xFF0F4EA3))

// Purple Theme
val PurplePalette = createPaletteFromAccent(Color(0xFF5A1780))

// Rose Theme
val RosePalette = createPaletteFromAccent(Color(0xFFB0154C))

// Amber Theme
val AmberPalette = createPaletteFromAccent(Color(0xFFCC7400))

// Emerald Theme
val EmeraldPalette = createPaletteFromAccent(Color(0xFF2D6B2F))

// Midnight Theme
val MidnightPalette = createPaletteFromAccent(Color(0xFF27337F))

// Coral Theme
val CoralPalette = createPaletteFromAccent(Color(0xFFBF3914))

// Forest Theme
val ForestPalette = createPaletteFromAccent(Color(0xFF2D6B2F))

// Sunset Theme
val SunsetPalette = createPaletteFromAccent(Color(0xFFD45500))

// Lavender Theme
val LavenderPalette = createPaletteFromAccent(Color(0xFF6F5AA6))

// Charcoal Theme
val CharcoalPalette = createPaletteFromAccent(Color(0xFF757575))

// Crimson Theme
val CrimsonPalette = createPaletteFromAccent(Color(0xFFA52525))

// Cyan Theme
val CyanPalette = createPaletteFromAccent(Color(0xFF0087A3))

// Indigo Theme
val IndigoPalette = createPaletteFromAccent(Color(0xFF2C3A86))

// Pink Theme
val PinkPalette = createPaletteFromAccent(Color(0xFFE91E63))

// Lime Theme
val LimePalette = createPaletteFromAccent(Color(0xFF8BC34A))

// Brown Theme
val BrownPalette = createPaletteFromAccent(Color(0xFF795548))

// Slate Theme
val SlatePalette = createPaletteFromAccent(Color(0xFF607D8B))

// Navy Theme
val NavyPalette = createPaletteFromAccent(Color(0xFF1A237E))

// Olive Theme
val OlivePalette = createPaletteFromAccent(Color(0xFF827717))

// Maroon Theme
val MaroonPalette = createPaletteFromAccent(Color(0xFF880E4F))

// Tiffany Theme
val TiffanyPalette = createPaletteFromAccent(Color(0xFF0ABAB5))

// Gold Theme
val GoldPalette = createPaletteFromAccent(Color(0xFFC7AB00))

// Bronze Theme
val BronzePalette = createPaletteFromAccent(Color(0xFFA56525))

// Silver Theme
val SilverPalette = createPaletteFromAccent(Color(0xFF9E9E9E))

// Plum Theme
val PlumPalette = createPaletteFromAccent(Color(0xFF6B3464))

// Mint Theme
val MintPalette = createPaletteFromAccent(Color(0xFF2A8060))

// Peach Theme
val PeachPalette = createPaletteFromAccent(Color(0xFFD4876F))

// Sky Theme
val SkyPalette = createPaletteFromAccent(Color(0xFF0B79B5))

// Wine Theme
val WinePalette = createPaletteFromAccent(Color(0xFF541F25))

// Sage Theme
val SagePalette = createPaletteFromAccent(Color(0xFF87AE73))

// Rust Theme
val RustPalette = createPaletteFromAccent(Color(0xFF8F300A))

// Aqua Theme
val AquaPalette = createPaletteFromAccent(Color(0xFF00CCCC))

// Magenta Theme
val MagentaPalette = createPaletteFromAccent(Color(0xFFCC00CC))

// Turquoise Theme
val TurquoisePalette = createPaletteFromAccent(Color(0xFF2EAFA1))

// Cobalt Theme
val CobaltPalette = createPaletteFromAccent(Color(0xFF003274))

// Mauve Theme
val MauvePalette = createPaletteFromAccent(Color(0xFFB080CC))

// Cherry Theme
val CherryPalette = createPaletteFromAccent(Color(0xFFB4254D))

// Arctic Theme
val ArcticPalette = createPaletteFromAccent(Color(0xFF00ACC1))

// Moss Theme
val MossPalette = createPaletteFromAccent(Color(0xFF8A9A5B))

// Sapphire Theme
val SapphirePalette = createPaletteFromAccent(Color(0xFF0B3E8C))

// Tangerine Theme
val TangerinePalette = createPaletteFromAccent(Color(0xFFFF9966))

// Thistle Theme
val ThistlePalette = createPaletteFromAccent(Color(0xFF9370DB))

// Jade Theme
val JadePalette = createPaletteFromAccent(Color(0xFF007A50))

// Vermilion Theme
val VermilionPalette = createPaletteFromAccent(Color(0xFFE34234))

// Periwinkle Theme
val PeriwinklePalette = createPaletteFromAccent(Color(0xFF6666CC))

// Espresso Theme
val EspressoPalette = createPaletteFromAccent(Color(0xFF8B6B5C))

// Seafoam Theme
val SeafoamPalette = createPaletteFromAccent(Color(0xFF20B2AA))

// Mulberry Theme
val MulberryPalette = createPaletteFromAccent(Color(0xFFC54B8C))

// Sand Theme
val SandPalette = createPaletteFromAccent(Color(0xFF8A6F44))

// Cerulean Theme
val CeruleanPalette = createPaletteFromAccent(Color(0xFF007BA7))

// Fuchsia Theme
val FuchsiaPalette = createPaletteFromAccent(Color(0xFFFF00FF))

// Graphite Theme
val GraphitePalette = createPaletteFromAccent(Color(0xFF707070))

// Apricot Theme
val ApricotPalette = createPaletteFromAccent(Color(0xFFE07850))

// Steel Theme
val SteelPalette = createPaletteFromAccent(Color(0xFF345F83))

// Raspberry Theme
val RaspberryPalette = createPaletteFromAccent(Color(0xFFE30B5C))

// Ivory Theme
val IvoryPalette = createPaletteFromAccent(Color(0xFFD4AF37))

// Denim Theme
val DenimPalette = createPaletteFromAccent(Color(0xFF1560BD))

// Honey Theme
val HoneyPalette = createPaletteFromAccent(Color(0xFFEB9605))

// Orchid Theme
val OrchidPalette = createPaletteFromAccent(Color(0xFFB054B0))

// Ash Theme
val AshPalette = createPaletteFromAccent(Color(0xFF708078))

// Blush Theme
val BlushPalette = createPaletteFromAccent(Color(0xFFDE6FA1))

// Pastel Mint Theme
val PastelMintPalette = createPaletteFromAccent(Color(0xFF9FE6C3))

// Pastel Peach Theme
val PastelPeachPalette = createPaletteFromAccent(Color(0xFFFFC6A8))

// Pastel Lilac Theme
val PastelLilacPalette = createPaletteFromAccent(Color(0xFFD9C7FF))

// Pastel Sky Theme
val PastelSkyPalette = createPaletteFromAccent(Color(0xFFB9E4FF))

// Pastel Sand Theme
val PastelSandPalette = createPaletteFromAccent(Color(0xFFF1E2C6))

// Pastel Lemon Theme
val PastelLemonPalette = createPaletteFromAccent(Color(0xFFFFF3B0))

// Pastel Coral Theme
val PastelCoralPalette = createPaletteFromAccent(Color(0xFFFFC4C4))

// Pastel Mauve Theme
val PastelMauvePalette = createPaletteFromAccent(Color(0xFFE8D7F2))

// Pastel Sage Theme
val PastelSagePalette = createPaletteFromAccent(Color(0xFFCBDDC4))

// Pastel Seafoam Theme
val PastelSeafoamPalette = createPaletteFromAccent(Color(0xFFBEEBE0))

// Pastel Periwinkle Theme
val PastelPeriwinklePalette = createPaletteFromAccent(Color(0xFFC7D7FF))

// Pastel Sunset Theme
val PastelSunsetPalette = createPaletteFromAccent(Color(0xFFFFD8B5))

// Pastel Fog Theme
val PastelFogPalette = createPaletteFromAccent(Color(0xFFD9E1E8))

// Pastel Clay Theme
val PastelClayPalette = createPaletteFromAccent(Color(0xFFE7D0C0))

// Pastel Berry Theme
val PastelBerryPalette = createPaletteFromAccent(Color(0xFFF3C5E8))

// Dark core themes
val DarkTealPalette = createPaletteFromAccent(Color(0xFF006060))
val ObsidianPalette = createPaletteFromAccent(Color(0xFF111827))
val DeepNavyPalette = createPaletteFromAccent(Color(0xFF0A2342))
val InkPalette = createPaletteFromAccent(Color(0xFF0D0D26))
val GunmetalPalette = createPaletteFromAccent(Color(0xFF2A343E))
val StormPalette = createPaletteFromAccent(Color(0xFF2B3A67))
val MidnightPurplePalette = createPaletteFromAccent(Color(0xFF2B1B4C))
val DeepForestPalette = createPaletteFromAccent(Color(0xFF0F2A1D))
val ShadowPalette = createPaletteFromAccent(Color(0xFF1F2733))
val DeepPlumPalette = createPaletteFromAccent(Color(0xFF3B1D36))
val NightWinePalette = createPaletteFromAccent(Color(0xFF3A0E2A))
val DeepMaroonPalette = createPaletteFromAccent(Color(0xFF4A0F23))
val DeepIndigoPalette = createPaletteFromAccent(Color(0xFF1B254B))
val StealthBluePalette = createPaletteFromAccent(Color(0xFF14313E))
val DieselRedPalette = createPaletteFromAccent(Color(0xFF4C1410))
val DeepBronzePalette = createPaletteFromAccent(Color(0xFF3E2A16))
val DeepEmeraldPalette = createPaletteFromAccent(Color(0xFF0F3B2E))
val DuskPalette = createPaletteFromAccent(Color(0xFF263248))
val TwilightPalette = createPaletteFromAccent(Color(0xFF2C1E3D))
val DeepSlatePalette = createPaletteFromAccent(Color(0xFF2B2F36))
val CobaltNightPalette = createPaletteFromAccent(Color(0xFF102F5F))
val AshenBluePalette = createPaletteFromAccent(Color(0xFF2C3E50))
val DeepOlivePalette = createPaletteFromAccent(Color(0xFF2C3B2F))
val MidnightTealPalette = createPaletteFromAccent(Color(0xFF004D4D))
val DeepCharcoalPalette = createPaletteFromAccent(Color(0xFF141820))

// Dark pastel themes
val DarkPastelMintPalette = createPaletteFromAccent(Color(0xFF2D6F64))
val DarkPastelPeachPalette = createPaletteFromAccent(Color(0xFF7A5242))
val DarkPastelLilacPalette = createPaletteFromAccent(Color(0xFF5A4C7D))
val DarkPastelSkyPalette = createPaletteFromAccent(Color(0xFF3D5A73))
val DarkPastelSandPalette = createPaletteFromAccent(Color(0xFF6B5B45))
val DarkPastelLemonPalette = createPaletteFromAccent(Color(0xFF6B652E))
val DarkPastelCoralPalette = createPaletteFromAccent(Color(0xFF7A3E3C))
val DarkPastelMauvePalette = createPaletteFromAccent(Color(0xFF5C4A5C))
val DarkPastelSagePalette = createPaletteFromAccent(Color(0xFF4F5E4F))
val DarkPastelSeafoamPalette = createPaletteFromAccent(Color(0xFF3F6B63))
val DarkPastelPeriwinklePalette = createPaletteFromAccent(Color(0xFF4E5A8A))
val DarkPastelSunsetPalette = createPaletteFromAccent(Color(0xFF7A5048))
val DarkPastelFogPalette = createPaletteFromAccent(Color(0xFF4C5562))
val DarkPastelClayPalette = createPaletteFromAccent(Color(0xFF6A4F42))
val DarkPastelBerryPalette = createPaletteFromAccent(Color(0xFF6B3C55))

// New Teal & Aqua Palettes
val AquamarinePalette = createPaletteFromAccent(Color(0xFF7FFFD4))
val CaribbeanPalette = createPaletteFromAccent(Color(0xFF00CED1))
val PeacockPalette = createPaletteFromAccent(Color(0xFF005F6A))
val LagoonPalette = createPaletteFromAccent(Color(0xFF017F7F))
val ArcticTealPalette = createPaletteFromAccent(Color(0xFF5FB3B3))
val DeepCyanPalette = createPaletteFromAccent(Color(0xFF008B8B))
val OceanTealPalette = createPaletteFromAccent(Color(0xFF006D6D))
val TropicalTealPalette = createPaletteFromAccent(Color(0xFF20B2AA))

// New Purple & Violet Palettes
val AmethystPalette = createPaletteFromAccent(Color(0xFF9966CC))
val GrapePalette = createPaletteFromAccent(Color(0xFF6F2DA8))
val VioletPalette = createPaletteFromAccent(Color(0xFF8B00FF))
val WisteriaPalette = createPaletteFromAccent(Color(0xFFC9A0DC))

// New Pink & Red Palettes (Magenta already defined above)
val CarnationPalette = createPaletteFromAccent(Color(0xFFFFA6C9))
val RubyPalette = createPaletteFromAccent(Color(0xFFE0115F))
val ScarletPalette = createPaletteFromAccent(Color(0xFFFF2400))

// New Green Palettes (Lime already defined above)
val PistachioPalette = createPaletteFromAccent(Color(0xFF93C572))
val FernPalette = createPaletteFromAccent(Color(0xFF4F7942))

// New Neutral Palettes (Silver, Graphite already defined above)
val PewterPalette = createPaletteFromAccent(Color(0xFF8F8F8F))
val TitaniumPalette = createPaletteFromAccent(Color(0xFF878681))

// Additional palettes for category completion
val AzurePalette = createPaletteFromAccent(Color(0xFF007FFF))
val IrisPalette = createPaletteFromAccent(Color(0xFF5D3FD3))

/**
 * Adds shades to a palette based on its primary/accent color
 */
fun ThemePalette.withGeneratedShades(): ThemePalette {
    val shades = createShadesFromColor(accent)
    return copy(
        shade1 = shades[0],
        shade2 = shades[1],
        shade3 = shades[2],
        shade4 = shades[3],
        shade5 = shades[4],
        shade6 = shades[5],
        shade7 = shades[6],
        shade8 = shades[7],
        shade9 = shades[8],
        shade10 = shades[9],
        shade11 = shades[10],
        shade12 = shades[11]
    )
}

fun getThemePalette(
    theme: AppTheme,
    darkMode: Boolean = false
): ThemePalette {
    val basePalette = when (theme) {
        AppTheme.TEAL -> TealPalette
        AppTheme.OCEAN -> OceanPalette
        AppTheme.PURPLE -> PurplePalette
        AppTheme.ROSE -> RosePalette
        AppTheme.AMBER -> AmberPalette
        AppTheme.EMERALD -> EmeraldPalette
        AppTheme.MIDNIGHT -> MidnightPalette
        AppTheme.CORAL -> CoralPalette
        AppTheme.FOREST -> ForestPalette
        AppTheme.SUNSET -> SunsetPalette
        AppTheme.LAVENDER -> LavenderPalette
        AppTheme.CRIMSON -> CrimsonPalette
        AppTheme.CYAN -> CyanPalette
        AppTheme.INDIGO -> IndigoPalette
        AppTheme.GOLD -> GoldPalette
        AppTheme.MINT -> MintPalette
        AppTheme.SKY -> SkyPalette
        AppTheme.WINE -> WinePalette
        AppTheme.SAPPHIRE -> SapphirePalette
        AppTheme.PEACH -> PeachPalette
        // 15 new themes
        AppTheme.CHERRY -> CherryPalette
        AppTheme.JADE -> JadePalette
        AppTheme.COBALT -> CobaltPalette
        AppTheme.BRONZE -> BronzePalette
        AppTheme.ORCHID -> OrchidPalette
        AppTheme.RUST -> RustPalette
        AppTheme.STEEL -> SteelPalette
        AppTheme.PLUM -> PlumPalette
        AppTheme.TURQUOISE -> TurquoisePalette
        AppTheme.SAND -> SandPalette
        AppTheme.SLATE -> SlatePalette
        AppTheme.RASPBERRY -> RaspberryPalette
        AppTheme.HONEY -> HoneyPalette
        AppTheme.OLIVE -> OlivePalette
        AppTheme.BLUSH -> BlushPalette
        AppTheme.PASTEL_MINT -> PastelMintPalette
        AppTheme.PASTEL_PEACH -> PastelPeachPalette
        AppTheme.PASTEL_LILAC -> PastelLilacPalette
        AppTheme.PASTEL_SKY -> PastelSkyPalette
        AppTheme.PASTEL_SAND -> PastelSandPalette
        AppTheme.PASTEL_LEMON -> PastelLemonPalette
        AppTheme.PASTEL_CORAL -> PastelCoralPalette
        AppTheme.PASTEL_MAUVE -> PastelMauvePalette
        AppTheme.PASTEL_SAGE -> PastelSagePalette
        AppTheme.PASTEL_SEAFOAM -> PastelSeafoamPalette
        AppTheme.PASTEL_PERIWINKLE -> PastelPeriwinklePalette
        AppTheme.PASTEL_SUNSET -> PastelSunsetPalette
        AppTheme.PASTEL_FOG -> PastelFogPalette
        AppTheme.PASTEL_CLAY -> PastelClayPalette
        AppTheme.PASTEL_BERRY -> PastelBerryPalette
        AppTheme.DARK_TEAL -> DarkTealPalette
        AppTheme.OBSIDIAN -> ObsidianPalette
        AppTheme.DEEP_NAVY -> DeepNavyPalette
        AppTheme.INK -> InkPalette
        AppTheme.GUNMETAL -> GunmetalPalette
        AppTheme.STORM -> StormPalette
        AppTheme.MIDNIGHT_PURPLE -> MidnightPurplePalette
        AppTheme.DEEP_FOREST -> DeepForestPalette
        AppTheme.SHADOW -> ShadowPalette
        AppTheme.DEEP_PLUM -> DeepPlumPalette
        AppTheme.NIGHT_WINE -> NightWinePalette
        AppTheme.DEEP_MAROON -> DeepMaroonPalette
        AppTheme.DEEP_INDIGO -> DeepIndigoPalette
        AppTheme.STEALTH_BLUE -> StealthBluePalette
        AppTheme.DIESEL_RED -> DieselRedPalette
        AppTheme.DEEP_BRONZE -> DeepBronzePalette
        AppTheme.DEEP_EMERALD -> DeepEmeraldPalette
        AppTheme.DUSK -> DuskPalette
        AppTheme.TWILIGHT -> TwilightPalette
        AppTheme.DEEP_SLATE -> DeepSlatePalette
        AppTheme.COBALT_NIGHT -> CobaltNightPalette
        AppTheme.ASHEN_BLUE -> AshenBluePalette
        AppTheme.DEEP_OLIVE -> DeepOlivePalette
        AppTheme.MIDNIGHT_TEAL -> MidnightTealPalette
        AppTheme.DEEP_CHARCOAL -> DeepCharcoalPalette
        AppTheme.DARK_PASTEL_MINT -> DarkPastelMintPalette
        AppTheme.DARK_PASTEL_PEACH -> DarkPastelPeachPalette
        AppTheme.DARK_PASTEL_LILAC -> DarkPastelLilacPalette
        AppTheme.DARK_PASTEL_SKY -> DarkPastelSkyPalette
        AppTheme.DARK_PASTEL_SAND -> DarkPastelSandPalette
        AppTheme.DARK_PASTEL_LEMON -> DarkPastelLemonPalette
        AppTheme.DARK_PASTEL_CORAL -> DarkPastelCoralPalette
        AppTheme.DARK_PASTEL_MAUVE -> DarkPastelMauvePalette
        AppTheme.DARK_PASTEL_SAGE -> DarkPastelSagePalette
        AppTheme.DARK_PASTEL_SEAFOAM -> DarkPastelSeafoamPalette
        AppTheme.DARK_PASTEL_PERIWINKLE -> DarkPastelPeriwinklePalette
        AppTheme.DARK_PASTEL_SUNSET -> DarkPastelSunsetPalette
        AppTheme.DARK_PASTEL_FOG -> DarkPastelFogPalette
        AppTheme.DARK_PASTEL_CLAY -> DarkPastelClayPalette
        AppTheme.DARK_PASTEL_BERRY -> DarkPastelBerryPalette
        // Additional Teal & Aqua themes
        AppTheme.AQUAMARINE -> AquamarinePalette
        AppTheme.CARIBBEAN -> CaribbeanPalette
        AppTheme.PEACOCK -> PeacockPalette
        AppTheme.LAGOON -> LagoonPalette
        AppTheme.ARCTIC_TEAL -> ArcticTealPalette
        AppTheme.DEEP_CYAN -> DeepCyanPalette
        AppTheme.OCEAN_TEAL -> OceanTealPalette
        AppTheme.TROPICAL_TEAL -> TropicalTealPalette
        // Additional Purple & Violet themes
        AppTheme.AMETHYST -> AmethystPalette
        AppTheme.GRAPE -> GrapePalette
        AppTheme.VIOLET -> VioletPalette
        AppTheme.WISTERIA -> WisteriaPalette
        // Additional Pink & Red themes
        AppTheme.MAGENTA -> MagentaPalette
        AppTheme.CARNATION -> CarnationPalette
        AppTheme.RUBY -> RubyPalette
        AppTheme.SCARLET -> ScarletPalette
        // Additional Green themes
        AppTheme.LIME -> LimePalette
        AppTheme.PISTACHIO -> PistachioPalette
        AppTheme.FERN -> FernPalette
        // Additional Neutral themes
        AppTheme.SILVER -> SilverPalette
        AppTheme.GRAPHITE -> GraphitePalette
        AppTheme.PEWTER -> PewterPalette
        AppTheme.TITANIUM -> TitaniumPalette
        AppTheme.CHARCOAL -> CharcoalPalette
        // Additional themes for category completion
        AppTheme.CERULEAN -> CeruleanPalette
        AppTheme.DENIM -> DenimPalette
        AppTheme.AZURE -> AzurePalette
        AppTheme.MAUVE -> MauvePalette
        AppTheme.IRIS -> IrisPalette
        AppTheme.FUCHSIA -> FuchsiaPalette
        AppTheme.SAGE -> SagePalette
        AppTheme.MOSS -> MossPalette
        AppTheme.CUSTOM -> createCustomPalette()
    }

    // Generate shades for the palette
    val lightPalette = basePalette.withGeneratedShades()

    return if (darkMode) {
        // For custom theme in dark mode, use the custom background if it's dark
        if (theme == AppTheme.CUSTOM) {
            val customPalette = createCustomPalette().withGeneratedShades()
            val bgLuminance = (CustomThemeColors.backgroundColor.red * 0.299f +
                    CustomThemeColors.backgroundColor.green * 0.587f +
                    CustomThemeColors.backgroundColor.blue * 0.114f)
            // If custom background is dark enough, use it; otherwise use One Dark
            if (bgLuminance < 0.4f) {
                customPalette
            } else {
                lightPalette.toDarkMode()
            }
        } else {
            lightPalette.toDarkMode()
        }
    } else {
        // For CUSTOM theme, use the already properly configured palette
        if (theme == AppTheme.CUSTOM) {
            lightPalette
        } else {
            // Use light tints with subtle theme color
            lightPalette.copy(
                background = lightPalette.shade11,
                surface = lightPalette.shade12,
                surfaceMedium = lightPalette.shade10,
                surfaceLight = lightPalette.shade11,
                onBackground = Color(0xFF0F0F0F),
                onSurface = Color(0xFF0F0F0F),
                textPrimary = Color(0xFF0F0F0F),
                textSecondary = Color(0xFF2F3A41),
                textMuted = Color(0xFF5A6770)
            )
        }
    }
}

/**
 * Creates a mixed theme palette that combines primary colors from one theme
 * with accent colors from another theme.
 *
 * @param primaryTheme The theme to use for primary/background colors
 * @param accentTheme The theme to use for accent colors (buttons, toggles, etc.)
 * @param darkMode Whether to apply dark mode
 */
fun getMixedThemePalette(
    primaryTheme: AppTheme,
    accentTheme: AppTheme,
    darkMode: Boolean = false
): ThemePalette {
    // Get the primary theme palette (handles dark mode)
    val primaryPalette = getThemePalette(primaryTheme, darkMode)

    // If both themes are the same, just return the primary palette
    if (primaryTheme == accentTheme) {
        return primaryPalette
    }

    // Get the accent theme palette to extract accent colors
    val accentPalette = getThemePalette(accentTheme, darkMode)

    // Combine: use primary palette for backgrounds/surfaces, accent palette for accent colors
    // shade1-6 are accent-derived text/icon colors (from accent theme)
    // shade7-12 are background/surface tints (from primary theme)
    // This ensures consistent backgrounds while using accent colors for text/icons
    return primaryPalette.copy(
        // Accent colors from accent theme
        accent = accentPalette.accent,
        accentGradientStart = accentPalette.accentGradientStart,
        accentGradientEnd = accentPalette.accentGradientEnd,
        // Darker shades for text/icons from accent theme
        shade1 = accentPalette.shade1,
        shade2 = accentPalette.shade2,
        shade3 = accentPalette.shade3,
        shade4 = accentPalette.shade4,
        shade5 = accentPalette.shade5,
        shade6 = accentPalette.shade6
        // shade7-12 and surface colors remain from primaryPalette (backgrounds)
    )
}

// One Dark Pro color palette (used as reference)
val OneDarkBackground = Color(0xFF282C34)
val OneDarkSurface = Color(0xFF21252B)
val OneDarkSurfaceMedium = Color(0xFF3E4451)
val OneDarkSurfaceLight = Color(0xFF4B5263)
val OneDarkTextPrimary = Color(0xFFABB2BF)
val OneDarkTextSecondary = Color(0xFF848B98)
val OneDarkTextMuted = Color(0xFF5C6370)
val OneDarkDivider = Color(0xFF3E4451)

/**
 * Converts a light theme palette to dark mode using darker shades derived from the theme's colors
 * Similar to One Dark Pro style but maintaining the theme's color character
 */
fun ThemePalette.toDarkMode(): ThemePalette {
    // Create very dark backgrounds tinted with the theme's primary color
    val baseColor = primary

    // Very dark background with subtle theme tint (like One Dark Pro darkness level)
    val darkBg = Color(
        red = (0.16f + baseColor.red * 0.04f).coerceIn(0f, 1f),
        green = (0.17f + baseColor.green * 0.04f).coerceIn(0f, 1f),
        blue = (0.20f + baseColor.blue * 0.04f).coerceIn(0f, 1f)
    )

    val darkSurface = Color(
        red = (0.13f + baseColor.red * 0.03f).coerceIn(0f, 1f),
        green = (0.14f + baseColor.green * 0.03f).coerceIn(0f, 1f),
        blue = (0.17f + baseColor.blue * 0.03f).coerceIn(0f, 1f)
    )

    val darkSurfaceMedium = Color(
        red = (0.24f + baseColor.red * 0.05f).coerceIn(0f, 1f),
        green = (0.26f + baseColor.green * 0.05f).coerceIn(0f, 1f),
        blue = (0.31f + baseColor.blue * 0.05f).coerceIn(0f, 1f)
    )

    val darkSurfaceLight = Color(
        red = (0.29f + baseColor.red * 0.06f).coerceIn(0f, 1f),
        green = (0.32f + baseColor.green * 0.06f).coerceIn(0f, 1f),
        blue = (0.38f + baseColor.blue * 0.06f).coerceIn(0f, 1f)
    )

    val darkDivider = Color(
        red = (0.24f + baseColor.red * 0.08f).coerceIn(0f, 1f),
        green = (0.26f + baseColor.green * 0.08f).coerceIn(0f, 1f),
        blue = (0.31f + baseColor.blue * 0.08f).coerceIn(0f, 1f)
    )

    // Text colors with slight theme tint
    val textPrimary = Color(
        red = (0.67f + baseColor.red * 0.1f).coerceIn(0f, 1f),
        green = (0.70f + baseColor.green * 0.1f).coerceIn(0f, 1f),
        blue = (0.75f + baseColor.blue * 0.1f).coerceIn(0f, 1f)
    )

    val textSecondary = Color(
        red = (0.52f + baseColor.red * 0.08f).coerceIn(0f, 1f),
        green = (0.54f + baseColor.green * 0.08f).coerceIn(0f, 1f),
        blue = (0.59f + baseColor.blue * 0.08f).coerceIn(0f, 1f)
    )

    val textMuted = Color(
        red = (0.36f + baseColor.red * 0.06f).coerceIn(0f, 1f),
        green = (0.38f + baseColor.green * 0.06f).coerceIn(0f, 1f),
        blue = (0.44f + baseColor.blue * 0.06f).coerceIn(0f, 1f)
    )

    return copy(
        background = darkBg,
        surface = darkSurface,
        surfaceMedium = darkSurfaceMedium,
        surfaceLight = darkSurfaceLight,
        surfaceDark = darkSurface,
        onBackground = textPrimary,
        onSurface = textPrimary,
        textPrimary = textPrimary,
        textSecondary = textSecondary,
        textMuted = textMuted,
        divider = darkDivider,
        headerBackground = primaryDark,
        // Dark mode shades for nav bar and backgrounds
        shade5 = darkSurfaceLight,
        shade6 = darkSurfaceMedium,
        shade7 = darkSurface,
        shade8 = darkBg,
        shade9 = darkSurface,
        shade10 = darkSurfaceMedium,
        shade11 = darkSurfaceLight,
        shade12 = darkBg,
        // Keep accent colors vibrant for contrast
        primaryLight = primaryLight,
        primaryDark = primaryDark
    )
}

/**
 * Creates shades from a base color for the limited palette
 * Intelligently handles both saturated colors AND pastel/light colors
 * - For saturated colors: darkens by reducing brightness
 * - For pastel colors: creates darker anchor shades for contrast
 */
fun createShadesFromColor(baseColor: Color): List<Color> {
    // Calculate luminance to detect if this is a light/pastel color
    val luminance = baseColor.red * 0.299f + baseColor.green * 0.587f + baseColor.blue * 0.114f
    val isPastel = luminance > 0.65f

    // For pastel colors, we need to create much darker anchor shades
    // by moving toward a saturated/dark version of the color
    val darkAnchor = if (isPastel) {
        // Create a dark, more saturated version of the pastel
        val maxChannel = maxOf(baseColor.red, baseColor.green, baseColor.blue)
        val minChannel = minOf(baseColor.red, baseColor.green, baseColor.blue)
        val saturationBoost = if (maxChannel > 0f) 1f - (minChannel / maxChannel) else 0f

        Color(
            red = (baseColor.red * 0.3f * (1f + saturationBoost)).coerceIn(0f, 0.4f),
            green = (baseColor.green * 0.3f * (1f + saturationBoost)).coerceIn(0f, 0.4f),
            blue = (baseColor.blue * 0.3f * (1f + saturationBoost)).coerceIn(0f, 0.4f)
        )
    } else {
        // For saturated colors, just darken normally
        Color(
            red = (baseColor.red * 0.35f).coerceIn(0f, 1f),
            green = (baseColor.green * 0.35f).coerceIn(0f, 1f),
            blue = (baseColor.blue * 0.35f).coerceIn(0f, 1f)
        )
    }

    // Light tint for backgrounds (subtle, not white)
    val lightTint = Color(
        red = (baseColor.red * 0.2f + 0.80f).coerceIn(0f, 1f),
        green = (baseColor.green * 0.2f + 0.80f).coerceIn(0f, 1f),
        blue = (baseColor.blue * 0.2f + 0.80f).coerceIn(0f, 1f)
    )

    return listOf(
        // shade1: Darkest - for headers, buttons (must have good contrast)
        darkAnchor,
        // shade2: Dark - secondary dark elements
        Color(
            red = (darkAnchor.red * 0.6f + baseColor.red * 0.4f).coerceIn(0f, 1f),
            green = (darkAnchor.green * 0.6f + baseColor.green * 0.4f).coerceIn(0f, 1f),
            blue = (darkAnchor.blue * 0.6f + baseColor.blue * 0.4f).coerceIn(0f, 1f)
        ),
        // shade3: Medium-dark
        Color(
            red = (darkAnchor.red * 0.4f + baseColor.red * 0.6f).coerceIn(0f, 1f),
            green = (darkAnchor.green * 0.4f + baseColor.green * 0.6f).coerceIn(0f, 1f),
            blue = (darkAnchor.blue * 0.4f + baseColor.blue * 0.6f).coerceIn(0f, 1f)
        ),
        // shade4: Base color
        baseColor,
        // shade5: Slightly lighter than base
        Color(
            red = (baseColor.red * 0.85f + lightTint.red * 0.15f).coerceIn(0f, 1f),
            green = (baseColor.green * 0.85f + lightTint.green * 0.15f).coerceIn(0f, 1f),
            blue = (baseColor.blue * 0.85f + lightTint.blue * 0.15f).coerceIn(0f, 1f)
        ),
        // shade6: Light
        Color(
            red = (baseColor.red * 0.7f + lightTint.red * 0.3f).coerceIn(0f, 1f),
            green = (baseColor.green * 0.7f + lightTint.green * 0.3f).coerceIn(0f, 1f),
            blue = (baseColor.blue * 0.7f + lightTint.blue * 0.3f).coerceIn(0f, 1f)
        ),
        // shade7: Lighter
        Color(
            red = (baseColor.red * 0.55f + lightTint.red * 0.45f).coerceIn(0f, 1f),
            green = (baseColor.green * 0.55f + lightTint.green * 0.45f).coerceIn(0f, 1f),
            blue = (baseColor.blue * 0.55f + lightTint.blue * 0.45f).coerceIn(0f, 1f)
        ),
        // shade8: Soft background
        Color(
            red = (baseColor.red * 0.4f + lightTint.red * 0.6f).coerceIn(0f, 1f),
            green = (baseColor.green * 0.4f + lightTint.green * 0.6f).coerceIn(0f, 1f),
            blue = (baseColor.blue * 0.4f + lightTint.blue * 0.6f).coerceIn(0f, 1f)
        ),
        // shade9: Very light background
        Color(
            red = (baseColor.red * 0.25f + lightTint.red * 0.75f).coerceIn(0f, 1f),
            green = (baseColor.green * 0.25f + lightTint.green * 0.75f).coerceIn(0f, 1f),
            blue = (baseColor.blue * 0.25f + lightTint.blue * 0.75f).coerceIn(0f, 1f)
        ),
        // shade10: Ultra light background
        Color(
            red = (baseColor.red * 0.15f + lightTint.red * 0.85f).coerceIn(0f, 1f),
            green = (baseColor.green * 0.15f + lightTint.green * 0.85f).coerceIn(0f, 1f),
            blue = (baseColor.blue * 0.15f + lightTint.blue * 0.85f).coerceIn(0f, 1f)
        ),
        // shade11: Near-white with tint
        Color(
            red = (baseColor.red * 0.08f + lightTint.red * 0.92f).coerceIn(0f, 1f),
            green = (baseColor.green * 0.08f + lightTint.green * 0.92f).coerceIn(0f, 1f),
            blue = (baseColor.blue * 0.08f + lightTint.blue * 0.92f).coerceIn(0f, 1f)
        ),
        // shade12: Lightest
        lightTint
    )
}

/**
 * Returns a solid accent color - Material You style
 */
fun ThemePalette.accentGradient(): Brush {
    return SolidColor(shade3)
}

/**
 * Returns a solid accent color for vertical layouts
 */
fun ThemePalette.accentGradientVertical(): Brush {
    return SolidColor(shade3)
}

/**
 * Returns a solid accent color for radial layouts
 */
fun ThemePalette.accentGradientRadial(): Brush {
    return SolidColor(shade4)
}

/**
 * Returns a solid background color with subtle theme tint
 */
fun ThemePalette.backgroundGradient(): Brush {
    return SolidColor(shade11)
}

/**
 * Returns a solid header color (base theme color)
 */
fun ThemePalette.headerGradient(): Brush {
    return SolidColor(shade4)
}

/**
 * Returns a solid navigation bar color
 */
fun ThemePalette.navBarGradient(): Brush {
    return SolidColor(shade5)
}

/**
 * Returns a solid tinted color for cards and surfaces
 */
fun ThemePalette.cardGradient(): Brush {
    return SolidColor(shade9)
}

/**
 * Returns a solid color for placeholder cover art background
 */
fun ThemePalette.coverArtGradient(): Brush {
    return SolidColor(shade6)
}

/**
 * Returns a solid color for thumbnail backgrounds in list items
 */
fun ThemePalette.thumbnailGradient(): Brush {
    return SolidColor(shade6)
}

/**
 * Returns a solid color for buttons
 */
fun ThemePalette.buttonGradient(): Brush {
    return SolidColor(shade3)
}

/**
 * Returns a gradient for progress indicators (kept for visual feedback)
 */
fun ThemePalette.progressGradient(): Brush {
    return Brush.horizontalGradient(
        colors = listOf(shade2, shade3, shade4)
    )
}

/**
 * Returns a shimmer gradient for loading animations (kept for animation)
 */
fun ThemePalette.shimmerGradient(): Brush {
    return Brush.horizontalGradient(
        colors = listOf(shade4, shade5, shade6, shade5, shade4)
    )
}

/**
 * Returns a solid glass-like overlay color
 */
fun ThemePalette.glassGradient(): Brush {
    return SolidColor(shade8.copy(alpha = 0.85f))
}

// Legacy color names for compatibility - these will use the current theme
// Primary Teal Palette (default, kept for compatibility)
val TealPrimary = Color(0xFF008B8B)
val TealPrimaryLight = Color(0xFF4DB6AC)
val TealPrimaryDark = Color(0xFF00695C)
val TealAccent = Color(0xFF00BFA5)

// Surface Colors
val SurfaceDark = Color(0xFF0D1B1E)
val SurfaceMedium = Color(0xFF1A2C30)
val SurfaceLight = Color(0xFF243438)
val SurfaceCard = Color(0xFF2A3E42)

// Text Colors
val TextPrimary = Color(0xFFF5F5F5)
val TextSecondary = Color(0xFFB0BEC5)
val TextMuted = Color(0xFF78909C)

// Accent Colors
val AccentGold = Color(0xFFFFD54F)
val AccentCoral = Color(0xFFFF7043)

// Light Theme Colors
val TealPrimaryLightTheme = Color(0xFF00796B)
val BackgroundLight = Color(0xFFF5F9F9)
val SurfaceLightTheme = Color(0xFFFFFFFF)
val TextPrimaryLight = Color(0xFF1A1A1A)
val TextSecondaryLight = Color(0xFF546E7A)

// Player Specific
val ProgressTrack = Color(0xFF37474F)
val ProgressIndicator = Color(0xFF4DB6AC)
val PlayerControlsBackground = Color(0xFF1A2C30)
