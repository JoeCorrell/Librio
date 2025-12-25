package com.librio.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.librio.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = currentPalette()
    val shape4 = cornerRadius(4.dp)

    // Entry animation
    var startAnimation by remember { mutableStateOf(false) }

    // Loading progress
    var targetProgress by remember { mutableFloatStateOf(0f) }
    var loadingText by remember { mutableStateOf("Initializing...") }

    // Logo scale animation
    val logoScale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.5f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "logoScale"
    )

    // Logo alpha animation
    val logoAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(400, easing = EaseOutCubic),
        label = "logoAlpha"
    )

    // Smooth animated progress
    val loadingProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(
            durationMillis = 500,
            easing = FastOutSlowInEasing
        ),
        label = "loadingProgress"
    )

    // Loading bar fade in
    val barAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(600, delayMillis = 200, easing = EaseOutCubic),
        label = "barAlpha"
    )

    // Feature icons stagger animation
    val icon1Alpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(400, delayMillis = 100, easing = EaseOutCubic),
        label = "icon1Alpha"
    )
    val icon2Alpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(400, delayMillis = 200, easing = EaseOutCubic),
        label = "icon2Alpha"
    )
    val icon3Alpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(400, delayMillis = 300, easing = EaseOutCubic),
        label = "icon3Alpha"
    )
    val icon4Alpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(400, delayMillis = 400, easing = EaseOutCubic),
        label = "icon4Alpha"
    )
    val icon5Alpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(400, delayMillis = 500, easing = EaseOutCubic),
        label = "icon5Alpha"
    )

    // Shimmer effect across the loading bar
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerOffset"
    )

    // Pulse animation for logo
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    // Exit animation
    var exitAnimation by remember { mutableStateOf(false) }
    val exitAlpha by animateFloatAsState(
        targetValue = if (exitAnimation) 0f else 1f,
        animationSpec = tween(250, easing = EaseInCubic),
        label = "exitAlpha"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(100)

        // Stage 1: Quick initial progress
        targetProgress = 0.3f
        delay(250)

        // Stage 2: Loading library
        loadingText = "Loading library..."
        targetProgress = 0.6f
        delay(350)

        // Stage 3: Preparing
        loadingText = "Preparing your media..."
        targetProgress = 0.85f
        delay(300)

        // Stage 4: Complete
        loadingText = "Ready!"
        targetProgress = 1f
        delay(250)

        // Exit
        exitAnimation = true
        delay(150)
        onSplashComplete()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        palette.shade11,
                        palette.shade10,
                        palette.shade9
                    )
                )
            )
            .graphicsLayer { alpha = exitAlpha },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App logo with gradient background
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .scale(logoScale * pulseScale)
                    .alpha(logoAlpha)
                    .shadow(12.dp, CircleShape)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                palette.shade2,
                                palette.shade3,
                                palette.shade4
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = AppIcons.Library,
                    contentDescription = null,
                    modifier = Modifier.size(56.dp),
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // App name
            Text(
                text = "LIBRIO",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 4.sp
                ),
                color = palette.shade1,
                modifier = Modifier.alpha(logoAlpha)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tagline
            Text(
                text = "Your personal media library",
                style = MaterialTheme.typography.bodyLarge,
                color = palette.shade4,
                modifier = Modifier.alpha(logoAlpha)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Feature icons row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SplashFeatureIcon(
                    icon = AppIcons.Audiobook,
                    label = "Audiobooks",
                    alpha = icon1Alpha,
                    accentColor = palette.shade3
                )
                SplashFeatureIcon(
                    icon = AppIcons.Book,
                    label = "E-books",
                    alpha = icon2Alpha,
                    accentColor = palette.shade3
                )
                SplashFeatureIcon(
                    icon = AppIcons.Music,
                    label = "Music",
                    alpha = icon3Alpha,
                    accentColor = palette.shade3
                )
                SplashFeatureIcon(
                    icon = AppIcons.Comic,
                    label = "Comics",
                    alpha = icon4Alpha,
                    accentColor = palette.shade3
                )
                SplashFeatureIcon(
                    icon = AppIcons.Movie,
                    label = "Movies",
                    alpha = icon5Alpha,
                    accentColor = palette.shade3
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Loading bar section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(barAlpha),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Loading bar with visible track
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                ) {
                    // Track background
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(shape4)
                            .background(palette.shade7.copy(alpha = 0.5f))
                    )

                    // Progress fill with shimmer
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(loadingProgress)
                            .height(6.dp)
                            .clip(shape4)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        palette.accent,
                                        palette.accent.copy(alpha = 0.8f),
                                        palette.accent
                                    ),
                                    startX = shimmerOffset * 300f,
                                    endX = (shimmerOffset + 0.5f) * 300f
                                )
                            )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Loading text
                Text(
                    text = loadingText,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.5.sp
                    ),
                    color = palette.shade3
                )
            }
        }
    }
}

@Composable
private fun SplashFeatureIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    alpha: Float,
    accentColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.alpha(alpha)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(accentColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(22.dp),
                tint = accentColor
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = accentColor.copy(alpha = 0.8f)
        )
    }
}
