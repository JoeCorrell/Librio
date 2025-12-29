package com.librio.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.librio.ui.theme.currentPalette
import kotlin.math.abs

/**
 * A custom super-thin slider with minimal design.
 * Uses Canvas for complete control over appearance.
 */
@Composable
fun MinimalSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    onValueChangeFinished: (() -> Unit)? = null,
    thumbSize: Dp = 6.dp,
    trackHeight: Dp = 1.5.dp,
    activeColor: Color? = null,
    inactiveColor: Color? = null
) {
    val palette = currentPalette()
    val thumbColor = activeColor ?: palette.accent
    val activeTrackColor = activeColor ?: palette.accent
    val inactiveTrackColor = inactiveColor ?: palette.surfaceLight.copy(alpha = 0.3f)

    var isDragging by remember { mutableStateOf(false) }
    val normalizedValue = ((value - valueRange.start) / (valueRange.endInclusive - valueRange.start)).coerceIn(0f, 1f)

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(24.dp)
            .pointerInput(enabled) {
                if (!enabled) return@pointerInput

                detectTapGestures { offset ->
                    val newValue = (offset.x / size.width).coerceIn(0f, 1f)
                    val mappedValue = valueRange.start + newValue * (valueRange.endInclusive - valueRange.start)
                    onValueChange(mappedValue)
                    onValueChangeFinished?.invoke()
                }
            }
            .pointerInput(enabled) {
                if (!enabled) return@pointerInput

                detectDragGestures(
                    onDragStart = { isDragging = true },
                    onDragEnd = {
                        isDragging = false
                        onValueChangeFinished?.invoke()
                    },
                    onDragCancel = {
                        isDragging = false
                        onValueChangeFinished?.invoke()
                    }
                ) { change, _ ->
                    change.consume()
                    val newValue = (change.position.x / size.width).coerceIn(0f, 1f)
                    val mappedValue = valueRange.start + newValue * (valueRange.endInclusive - valueRange.start)
                    onValueChange(mappedValue)
                }
            }
    ) {
        val trackY = size.height / 2f
        val trackHeightPx = trackHeight.toPx()
        val thumbSizePx = if (isDragging) thumbSize.toPx() * 1.5f else thumbSize.toPx()

        // Draw inactive track
        drawLine(
            color = inactiveTrackColor,
            start = Offset(0f, trackY),
            end = Offset(size.width, trackY),
            strokeWidth = trackHeightPx,
            cap = StrokeCap.Round
        )

        // Draw active track
        val activeEndX = size.width * normalizedValue
        drawLine(
            color = activeTrackColor,
            start = Offset(0f, trackY),
            end = Offset(activeEndX, trackY),
            strokeWidth = trackHeightPx,
            cap = StrokeCap.Round
        )

        // Draw thumb
        val thumbX = size.width * normalizedValue
        drawCircle(
            color = thumbColor,
            radius = thumbSizePx,
            center = Offset(thumbX, trackY)
        )

        // Draw subtle thumb shadow when dragging
        if (isDragging) {
            drawCircle(
                color = thumbColor.copy(alpha = 0.2f),
                radius = thumbSizePx * 1.8f,
                center = Offset(thumbX, trackY)
            )
        }
    }
}

/**
 * A custom super-thin progress slider for media playback.
 * Even thinner than MinimalSlider for a cleaner look.
 */
@Composable
fun MinimalProgressSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onValueChangeFinished: (() -> Unit)? = null,
    thumbSize: Dp = 5.dp,
    trackHeight: Dp = 1.dp,
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
