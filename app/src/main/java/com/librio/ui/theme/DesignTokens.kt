package com.librio.ui.theme

import androidx.compose.ui.unit.dp

/**
 * Design tokens for consistent spacing, elevation, and sizing
 * Use these throughout the app instead of hardcoded dp values
 */

/**
 * Standardized spacing scale
 */
object Spacing {
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 24.dp
    val xxl = 32.dp
}

/**
 * Standardized elevation scale for shadows
 */
object Elevation {
    val none = 0.dp
    val sm = 2.dp       // Subtle shadow (buttons)
    val md = 4.dp       // Standard cards
    val lg = 8.dp       // Prominent elements
    val xl = 12.dp      // Modals, overlays
}

/**
 * Standardized icon sizes
 */
object IconSize {
    val xs = 16.dp      // Inline, badges
    val sm = 20.dp      // Small buttons
    val md = 24.dp      // Standard icons
    val lg = 28.dp      // Large icons
    val xl = 32.dp      // Featured icons
    val xxl = 48.dp     // Empty states
}

/**
 * Standardized corner radius scale
 */
object CornerSize {
    val xs = 4.dp
    val sm = 6.dp       // Small elements (badges)
    val md = 8.dp       // Thumbnails
    val lg = 12.dp      // Cards, containers
    val xl = 16.dp      // Modal dialogs
    val pill = 50.dp    // Pill shapes
}

/**
 * Standardized button sizes
 */
object ButtonSize {
    val sm = 32.dp      // Compact buttons
    val md = 40.dp      // Standard buttons
    val lg = 48.dp      // Large, primary buttons
    val fab = 56.dp     // FAB standard size
}

/**
 * Standardized thumbnail sizes
 */
object ThumbnailSize {
    val xs = 40.dp      // Compact lists
    val sm = 48.dp      // Small lists
    val md = 64.dp      // Standard list items
    val lg = 72.dp      // Expanded list items
    val xl = 80.dp      // Featured items
}

/**
 * Standardized content widths for responsive layouts
 */
object ContentWidth {
    val compact = 360.dp
    val medium = 400.dp
    val expanded = 600.dp
}
