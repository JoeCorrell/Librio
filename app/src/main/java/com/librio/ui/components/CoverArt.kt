package com.librio.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.librio.ui.theme.AppIcons
import com.librio.ui.theme.cornerRadius
import com.librio.ui.theme.coverArtGradient
import com.librio.ui.theme.currentPalette

/**
 * Displays audiobook cover art with fallback placeholder
 * @param showPlaceholderAlways If true, always shows the placeholder with file extension instead of cover art
 * @param fileExtension The file extension to display (e.g., "M4B", "MP3")
 * @param contentType Type of content for icon selection (AUDIOBOOK, MUSIC, MOVIE)
 * @param title Optional title to display on placeholder to make each one unique
 */
@Composable
fun CoverArt(
    bitmap: Bitmap?,
    contentDescription: String,
    modifier: Modifier = Modifier,
    cornerRadiusSize: Dp = 16.dp,
    elevation: Dp = 12.dp,
    showPlaceholderAlways: Boolean = false,
    fileExtension: String = "",
    contentType: CoverArtContentType = CoverArtContentType.AUDIOBOOK,
    title: String = ""
) {
    val palette = currentPalette()
    val shape = cornerRadius(cornerRadiusSize)

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .shadow(elevation, shape)
            .clip(shape)
            .background(palette.coverArtGradient()),
        contentAlignment = Alignment.Center
    ) {
        // Show placeholder with icon, title, and extension
        if (showPlaceholderAlways || bitmap == null) {
            // Icon, title, and file extension overlay
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = when (contentType) {
                        CoverArtContentType.AUDIOBOOK -> AppIcons.Audiobook
                        CoverArtContentType.MUSIC -> AppIcons.Music
                        CoverArtContentType.MOVIE -> AppIcons.Movie
                        CoverArtContentType.EBOOK -> AppIcons.Book
                        CoverArtContentType.COMICS -> AppIcons.Comic
                    },
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .padding(4.dp),
                    tint = palette.shade2
                )

                // Title text (truncated to 2 lines)
                if (title.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Medium,
                            lineHeight = 14.sp
                        ),
                        color = palette.textSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }

                if (fileExtension.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = fileExtension.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = palette.accent
                    )
                }
            }
        } else {
            val imageBitmap = remember(bitmap) { bitmap.asImageBitmap() }
            Image(
                bitmap = imageBitmap,
                contentDescription = contentDescription,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    }
}

enum class CoverArtContentType {
    AUDIOBOOK,
    MUSIC,
    MOVIE,
    EBOOK,
    COMICS
}

/**
 * Small cover art thumbnail for lists
 */
@Composable
fun CoverArtThumbnail(
    bitmap: Bitmap?,
    contentDescription: String,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    showPlaceholderAlways: Boolean = false,
    fileExtension: String = "",
    contentType: CoverArtContentType = CoverArtContentType.AUDIOBOOK,
    title: String = ""
) {
    CoverArt(
        bitmap = bitmap,
        contentDescription = contentDescription,
        modifier = modifier.size(size),
        cornerRadiusSize = 8.dp,
        elevation = 4.dp,
        showPlaceholderAlways = showPlaceholderAlways,
        fileExtension = fileExtension,
        contentType = contentType,
        title = title
    )
}
