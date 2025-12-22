package com.librio.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.librio.R
import com.librio.ui.theme.*
import com.librio.ui.theme.AppIcons

/**
 * App header with logo, title, and action buttons
 */
@Composable
fun AppHeader(
    audiobookCount: Int,
    onSettingsClick: () -> Unit,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dimens = rememberResponsiveDimens()
    val palette = currentPalette()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = dimens.horizontalPadding),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App logo
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .shadow(4.dp, CircleShape)
                    .clip(CircleShape)
                    .background(
                        brush = palette.accentGradient()
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.app_logo),
                    contentDescription = "Librio",
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "My Library",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = (dimens.titleTextSize + 8).sp
                    ),
                    fontWeight = FontWeight.Bold,
                    color = palette.primary
                )
                Text(
                    text = "$audiobookCount audiobook${if (audiobookCount != 1) "s" else ""}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = dimens.bodyTextSize.sp
                    ),
                    color = palette.primary.copy(alpha = 0.5f)
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            IconButton(onClick = onSearchClick) {
                Icon(
                    imageVector = AppIcons.Search,
                    contentDescription = "Search",
                    tint = palette.primaryLight,
                    modifier = Modifier.size(dimens.iconSize)
                )
            }

            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = AppIcons.Settings,
                    contentDescription = "Settings",
                    tint = palette.primaryLight,
                    modifier = Modifier.size(dimens.iconSize)
                )
            }
        }
    }
}

/**
 * Section header with optional action
 */
@Composable
fun SectionHeader(
    title: String,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val dimens = rememberResponsiveDimens()
    val palette = currentPalette()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = dimens.horizontalPadding),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = dimens.titleTextSize.sp
            ),
            fontWeight = FontWeight.SemiBold,
            color = palette.primaryLight
        )

        if (actionText != null && onActionClick != null) {
            TextButton(onClick = onActionClick) {
                Text(
                    text = actionText,
                    style = MaterialTheme.typography.labelMedium,
                    color = palette.accent
                )
            }
        }
    }
}
