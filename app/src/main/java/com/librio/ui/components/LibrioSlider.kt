package com.librio.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.librio.ui.theme.currentPalette
import kotlin.math.roundToInt

/**
 * Custom stepped slider with tick marks for settings screens.
 * Values snap to defined steps (e.g., increments of 5 or 25).
 *
 * @param value Current value
 * @param onValueChange Callback when value changes
 * @param valueRange Range of allowed values
 * @param stepSize Increment size (e.g., 5 for 0, 5, 10, 15...)
 * @param valueLabel Optional label showing current value
 * @param showTickMarks Whether to display tick marks
 */
@Composable
fun LibrioSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    stepSize: Float,
    modifier: Modifier = Modifier,
    valueLabel: String? = null,
    showTickMarks: Boolean = true,
    trackHeight: Dp = 6.dp,
    thumbSize: Dp = 22.dp
) {
    val palette = currentPalette()
    val density = LocalDensity.current

    var sliderWidth by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }

    // Calculate number of steps
    val range = valueRange.endInclusive - valueRange.start
    val numSteps = (range / stepSize).roundToInt()

    // Snap value to nearest step
    fun snapToStep(rawValue: Float): Float {
        val snapped = ((rawValue - valueRange.start) / stepSize).roundToInt() * stepSize + valueRange.start
        return snapped.coerceIn(valueRange.start, valueRange.endInclusive)
    }

    // Calculate progress (0-1)
    val progress = ((value - valueRange.start) / range).coerceIn(0f, 1f)

    // Animated progress for smooth transitions
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "sliderProgress"
    )

    // Thumb size animation when dragging
    val animatedThumbSize by animateDpAsState(
        targetValue = if (isDragging) thumbSize + 4.dp else thumbSize,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "thumbSize"
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(thumbSize + 16.dp)
                .onGloballyPositioned { coordinates ->
                    sliderWidth = coordinates.size.width.toFloat()
                }
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        if (sliderWidth > 0) {
                            val tapProgress = (offset.x / sliderWidth).coerceIn(0f, 1f)
                            val newValue = valueRange.start + tapProgress * range
                            onValueChange(snapToStep(newValue))
                        }
                    }
                }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { isDragging = true },
                        onDragEnd = { isDragging = false },
                        onDragCancel = { isDragging = false }
                    ) { change, _ ->
                        change.consume()
                        if (sliderWidth > 0) {
                            val dragProgress = (change.position.x / sliderWidth).coerceIn(0f, 1f)
                            val newValue = valueRange.start + dragProgress * range
                            onValueChange(snapToStep(newValue))
                        }
                    }
                },
            contentAlignment = Alignment.CenterStart
        ) {
            // Track background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(trackHeight)
                    .clip(RoundedCornerShape(trackHeight / 2))
                    .background(palette.surfaceLight)
            )

            // Active track
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .height(trackHeight)
                    .clip(RoundedCornerShape(trackHeight / 2))
                    .background(palette.accent)
            )

            // Tick marks
            if (showTickMarks && numSteps <= 20) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = thumbSize / 2),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    for (i in 0..numSteps) {
                        val tickProgress = i.toFloat() / numSteps
                        val isActive = tickProgress <= progress
                        Box(
                            modifier = Modifier
                                .size(width = 2.dp, height = 10.dp)
                                .clip(RoundedCornerShape(1.dp))
                                .background(
                                    if (isActive) palette.accent.copy(alpha = 0.6f)
                                    else palette.surfaceLight.copy(alpha = 0.8f)
                                )
                        )
                    }
                }
            }

            // Thumb
            Box(
                modifier = Modifier
                    .offset {
                        val thumbOffset = (animatedProgress * (sliderWidth - with(density) { thumbSize.toPx() }))
                            .roundToInt()
                            .coerceAtLeast(0)
                        IntOffset(thumbOffset, 0)
                    }
                    .size(animatedThumbSize)
                    .shadow(if (isDragging) 8.dp else 4.dp, CircleShape)
                    .clip(CircleShape)
                    .background(palette.accent),
                contentAlignment = Alignment.Center
            ) {
                // Inner circle for depth effect
                Box(
                    modifier = Modifier
                        .size(animatedThumbSize * 0.4f)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.3f))
                )
            }
        }

        // Value label
        if (valueLabel != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = valueLabel,
                style = MaterialTheme.typography.labelSmall,
                color = palette.textMuted,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * Progress slider for media playback (smooth, no stepping).
 * Designed for seek controls where continuous values are needed.
 */
@Composable
fun LibrioProgressSlider(
    progress: Float,
    onProgressChange: (Float) -> Unit,
    onProgressChangeFinished: () -> Unit,
    modifier: Modifier = Modifier,
    trackHeight: Dp = 4.dp,
    thumbSize: Dp = 16.dp,
    showThumbOnlyWhenDragging: Boolean = false
) {
    val palette = currentPalette()
    val density = LocalDensity.current

    var sliderWidth by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    var localProgress by remember { mutableFloatStateOf(progress) }

    // Use local progress when dragging, otherwise use provided progress
    val displayProgress = if (isDragging) localProgress else progress

    // Animated progress
    val animatedProgress by animateFloatAsState(
        targetValue = displayProgress.coerceIn(0f, 1f),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "progressSlider"
    )

    // Thumb visibility and size animation
    val showThumb = !showThumbOnlyWhenDragging || isDragging
    val animatedThumbSize by animateDpAsState(
        targetValue = when {
            !showThumb -> 0.dp
            isDragging -> thumbSize + 6.dp
            else -> thumbSize
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "progressThumbSize"
    )

    // Track height animation when dragging
    val animatedTrackHeight by animateDpAsState(
        targetValue = if (isDragging) trackHeight + 2.dp else trackHeight,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "trackHeight"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(thumbSize + 16.dp)
            .onGloballyPositioned { coordinates ->
                sliderWidth = coordinates.size.width.toFloat()
            }
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    if (sliderWidth > 0) {
                        val tapProgress = (offset.x / sliderWidth).coerceIn(0f, 1f)
                        localProgress = tapProgress
                        onProgressChange(tapProgress)
                        onProgressChangeFinished()
                    }
                }
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        isDragging = true
                        if (sliderWidth > 0) {
                            localProgress = (offset.x / sliderWidth).coerceIn(0f, 1f)
                            onProgressChange(localProgress)
                        }
                    },
                    onDragEnd = {
                        isDragging = false
                        onProgressChangeFinished()
                    },
                    onDragCancel = {
                        isDragging = false
                        onProgressChangeFinished()
                    }
                ) { change, _ ->
                    change.consume()
                    if (sliderWidth > 0) {
                        localProgress = (change.position.x / sliderWidth).coerceIn(0f, 1f)
                        onProgressChange(localProgress)
                    }
                }
            },
        contentAlignment = Alignment.CenterStart
    ) {
        // Track background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(animatedTrackHeight)
                .clip(RoundedCornerShape(animatedTrackHeight / 2))
                .background(palette.accent.copy(alpha = 0.2f))
        )

        // Active track
        Box(
            modifier = Modifier
                .fillMaxWidth(animatedProgress)
                .height(animatedTrackHeight)
                .clip(RoundedCornerShape(animatedTrackHeight / 2))
                .background(palette.accent)
        )

        // Thumb
        if (animatedThumbSize > 0.dp) {
            Box(
                modifier = Modifier
                    .offset {
                        val thumbOffset = (animatedProgress * (sliderWidth - with(density) { thumbSize.toPx() }))
                            .roundToInt()
                            .coerceAtLeast(0)
                        IntOffset(thumbOffset, 0)
                    }
                    .size(animatedThumbSize)
                    .shadow(if (isDragging) 6.dp else 3.dp, CircleShape)
                    .clip(CircleShape)
                    .background(palette.accent),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(animatedThumbSize * 0.35f)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.4f))
                )
            }
        }
    }
}
