package com.librio.ui.theme

data class ThemeCategory(
    val title: String,
    val themes: List<AppTheme>
)

fun themeCategoriesByColor(): List<ThemeCategory> {
    // Teal & Aqua - 15 themes
    val tealAqua = listOf(
        AppTheme.DARK_TEAL,
        AppTheme.MIDNIGHT_TEAL,
        AppTheme.TEAL,
        AppTheme.CYAN,
        AppTheme.TURQUOISE,
        AppTheme.PEACOCK,
        AppTheme.LAGOON,
        AppTheme.DEEP_CYAN,
        AppTheme.OCEAN_TEAL,
        AppTheme.TROPICAL_TEAL,
        AppTheme.ARCTIC_TEAL,
        AppTheme.CARIBBEAN,
        AppTheme.AQUAMARINE,
        AppTheme.CERULEAN,
        AppTheme.DARK_PASTEL_SEAFOAM
    )

    // Blue & Indigo - 15 themes
    val blueIndigo = listOf(
        AppTheme.MIDNIGHT,
        AppTheme.DEEP_NAVY,
        AppTheme.DEEP_INDIGO,
        AppTheme.STEALTH_BLUE,
        AppTheme.COBALT_NIGHT,
        AppTheme.ASHEN_BLUE,
        AppTheme.INDIGO,
        AppTheme.COBALT,
        AppTheme.SAPPHIRE,
        AppTheme.OCEAN,
        AppTheme.SKY,
        AppTheme.DENIM,
        AppTheme.AZURE,
        AppTheme.DARK_PASTEL_SKY,
        AppTheme.DARK_PASTEL_PERIWINKLE
    )

    // Purple & Violet - 15 themes
    val purpleViolet = listOf(
        AppTheme.MIDNIGHT_PURPLE,
        AppTheme.DEEP_PLUM,
        AppTheme.TWILIGHT,
        AppTheme.PLUM,
        AppTheme.PURPLE,
        AppTheme.ORCHID,
        AppTheme.LAVENDER,
        AppTheme.GRAPE,
        AppTheme.VIOLET,
        AppTheme.AMETHYST,
        AppTheme.WISTERIA,
        AppTheme.MAUVE,
        AppTheme.IRIS,
        AppTheme.DARK_PASTEL_LILAC,
        AppTheme.DARK_PASTEL_MAUVE
    )

    // Pink & Red - 15 themes
    val pinkRed = listOf(
        AppTheme.NIGHT_WINE,
        AppTheme.DEEP_MAROON,
        AppTheme.DIESEL_RED,
        AppTheme.WINE,
        AppTheme.CRIMSON,
        AppTheme.CHERRY,
        AppTheme.RASPBERRY,
        AppTheme.ROSE,
        AppTheme.RUBY,
        AppTheme.SCARLET,
        AppTheme.MAGENTA,
        AppTheme.FUCHSIA,
        AppTheme.BLUSH,
        AppTheme.CARNATION,
        AppTheme.DARK_PASTEL_BERRY
    )

    // Green & Olive - 15 themes
    val greenOlive = listOf(
        AppTheme.DEEP_FOREST,
        AppTheme.DEEP_EMERALD,
        AppTheme.DEEP_OLIVE,
        AppTheme.FOREST,
        AppTheme.EMERALD,
        AppTheme.JADE,
        AppTheme.OLIVE,
        AppTheme.MINT,
        AppTheme.FERN,
        AppTheme.LIME,
        AppTheme.PISTACHIO,
        AppTheme.SAGE,
        AppTheme.MOSS,
        AppTheme.DARK_PASTEL_MINT,
        AppTheme.DARK_PASTEL_SAGE
    )

    // Warm & Earthy - 15 themes
    val warmEarthy = listOf(
        AppTheme.DEEP_BRONZE,
        AppTheme.DUSK,
        AppTheme.RUST,
        AppTheme.BRONZE,
        AppTheme.AMBER,
        AppTheme.GOLD,
        AppTheme.HONEY,
        AppTheme.SUNSET,
        AppTheme.CORAL,
        AppTheme.PEACH,
        AppTheme.SAND,
        AppTheme.DARK_PASTEL_PEACH,
        AppTheme.DARK_PASTEL_CORAL,
        AppTheme.DARK_PASTEL_SUNSET,
        AppTheme.DARK_PASTEL_CLAY
    )

    // Neutral & Gray - 15 themes
    val neutralGray = listOf(
        AppTheme.OBSIDIAN,
        AppTheme.INK,
        AppTheme.SHADOW,
        AppTheme.DEEP_CHARCOAL,
        AppTheme.DEEP_SLATE,
        AppTheme.GUNMETAL,
        AppTheme.STORM,
        AppTheme.SLATE,
        AppTheme.STEEL,
        AppTheme.GRAPHITE,
        AppTheme.PEWTER,
        AppTheme.TITANIUM,
        AppTheme.SILVER,
        AppTheme.CHARCOAL,
        AppTheme.DARK_PASTEL_FOG
    )

    return listOf(
        ThemeCategory("Teal & Aqua", tealAqua),
        ThemeCategory("Blue & Indigo", blueIndigo),
        ThemeCategory("Purple & Violet", purpleViolet),
        ThemeCategory("Pink & Red", pinkRed),
        ThemeCategory("Green & Olive", greenOlive),
        ThemeCategory("Warm & Earthy", warmEarthy),
        ThemeCategory("Neutral & Gray", neutralGray),
        ThemeCategory("Custom", listOf(AppTheme.CUSTOM))
    )
}
