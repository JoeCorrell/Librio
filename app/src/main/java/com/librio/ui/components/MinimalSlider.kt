package com.librio.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.librio.ui.theme.currentPalette

/**
 * A minimal, thin slider with a clean design.
 * Thinner track (4dp) and smaller thumb (16dp) for a modern look.
 *
 * Note: thumbSize and trackHeight parameters are kept for API compatibility
 * but are not used in the current Material 3 Slider implementation.
 */
@Suppress("UNUSED_PARAMETER")
@Composable
fun MinimalSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    onValueChangeFinished: (() -> Unit)? = null,
    thumbSize: Dp = 16.dp,
    trackHeight: Dp = 4.dp,
    activeColor: Color? = null,
    inactiveColor: Color? = null
) {
    val palette = currentPalette()
    val thumbColor = activeColor ?: palette.accent
    val activeTrackColor = activeColor ?: palette.accent
    val inactiveTrackColor = inactiveColor ?: palette.surfaceLight

    val interactionSource = remember { MutableInteractionSource() }

    Slider(
        value = value.coerceIn(valueRange.start, valueRange.endInclusive),
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        valueRange = valueRange,
        steps = steps,
        onValueChangeFinished = onValueChangeFinished,
        colors = SliderDefaults.colors(
            thumbColor = thumbColor,
            activeTrackColor = activeTrackColor,
            inactiveTrackColor = inactiveTrackColor,
            activeTickColor = Color.Transparent,
            inactiveTickColor = Color.Transparent,
            disabledThumbColor = thumbColor.copy(alpha = 0.38f),
            disabledActiveTrackColor = activeTrackColor.copy(alpha = 0.38f),
            disabledInactiveTrackColor = inactiveTrackColor.copy(alpha = 0.38f)
        ),
        interactionSource = interactionSource
    )
}

/**
 * A minimal progress slider specifically for media playback.
 * Thinner and cleaner than the default Material 3 slider.
 */
@Composable
fun MinimalProgressSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onValueChangeFinished: (() -> Unit)? = null,
    thumbSize: Dp = 14.dp,
    trackHeight: Dp = 3.dp,
    activeColor: Color? = null,
    inactiveColor: Color? = null
) {
    MinimalSlider(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        valueRange = 0f..1f,
        steps = 0,
        onValueChangeFinished = onValueChangeFinished,
        thumbSize = thumbSize,
        trackHeight = trackHeight,
        activeColor = activeColor,
        inactiveColor = inactiveColor
    )
}
