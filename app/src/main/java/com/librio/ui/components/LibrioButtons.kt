package com.librio.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.librio.ui.theme.ButtonSize
import com.librio.ui.theme.CornerSize
import com.librio.ui.theme.Elevation
import com.librio.ui.theme.IconSize
import com.librio.ui.theme.Spacing
import com.librio.ui.theme.cornerRadius
import com.librio.ui.theme.currentPalette

/**
 * Button size variants
 */
enum class LibrioButtonSize(val buttonSize: Dp, val iconSize: Dp) {
    Small(ButtonSize.sm, IconSize.sm),
    Medium(ButtonSize.md, IconSize.md),
    Large(ButtonSize.lg, IconSize.lg)
}

/**
 * Primary filled button with accent color
 * Use for main actions like "Play", "Add", "Save"
 */
@Composable
fun LibrioFilledButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    text: String
) {
    val palette = currentPalette()

    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = cornerRadius(CornerSize.lg),
        colors = ButtonDefaults.buttonColors(
            containerColor = palette.accent,
            contentColor = Color.White,
            disabledContainerColor = palette.shade6,
            disabledContentColor = palette.shade8
        ),
        contentPadding = PaddingValues(
            horizontal = Spacing.lg,
            vertical = Spacing.md
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = Elevation.sm,
            pressedElevation = Elevation.md
        )
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(IconSize.sm)
            )
            Spacer(modifier = Modifier.width(Spacing.sm))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/**
 * Secondary tonal button with surface color
 * Use for secondary actions
 */
@Composable
fun LibrioTonalButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    text: String
) {
    val palette = currentPalette()

    FilledTonalButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = cornerRadius(CornerSize.lg),
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = palette.shade9,
            contentColor = palette.shade2,
            disabledContainerColor = palette.shade10,
            disabledContentColor = palette.shade6
        ),
        contentPadding = PaddingValues(
            horizontal = Spacing.lg,
            vertical = Spacing.md
        )
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(IconSize.sm)
            )
            Spacer(modifier = Modifier.width(Spacing.sm))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Icon-only tonal button
 * Use for toolbar actions, navigation icons
 */
@Composable
fun LibrioIconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: LibrioButtonSize = LibrioButtonSize.Medium,
    tint: Color? = null
) {
    val palette = currentPalette()
    val iconTint = tint ?: palette.shade2
    val backgroundColor = palette.shade9

    Box(
        modifier = modifier
            .size(size.buttonSize)
            .shadow(
                elevation = Elevation.sm,
                shape = CircleShape,
                clip = false
            )
            .clip(CircleShape)
            .background(if (enabled) backgroundColor else palette.shade10)
            .clickable(
                enabled = enabled,
                onClick = onClick,
                role = Role.Button,
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = true)
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (enabled) iconTint else palette.shade6,
            modifier = Modifier.size(size.iconSize)
        )
    }
}

/**
 * Icon button with accent/primary color background
 * Use for primary icon actions
 */
@Composable
fun LibrioAccentIconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: LibrioButtonSize = LibrioButtonSize.Medium
) {
    val palette = currentPalette()

    Box(
        modifier = modifier
            .size(size.buttonSize)
            .shadow(
                elevation = Elevation.md,
                shape = CircleShape,
                clip = false
            )
            .clip(CircleShape)
            .background(if (enabled) palette.accent else palette.shade6)
            .clickable(
                enabled = enabled,
                onClick = onClick,
                role = Role.Button,
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = true, color = Color.White)
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = Color.White,
            modifier = Modifier.size(size.iconSize)
        )
    }
}

/**
 * Material 3 Floating Action Button
 * Use for primary floating actions
 */
@Composable
fun LibrioFab(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    val palette = currentPalette()

    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        shape = CircleShape,
        containerColor = palette.accent,
        contentColor = Color.White,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = Elevation.lg,
            pressedElevation = Elevation.xl
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(IconSize.md)
        )
    }
}

/**
 * Small FAB variant
 */
@Composable
fun LibrioSmallFab(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    val palette = currentPalette()

    FloatingActionButton(
        onClick = onClick,
        modifier = modifier.size(ButtonSize.lg),
        shape = CircleShape,
        containerColor = palette.accent,
        contentColor = Color.White,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = Elevation.md,
            pressedElevation = Elevation.lg
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(IconSize.sm)
        )
    }
}
