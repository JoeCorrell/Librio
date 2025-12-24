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
    contentType: CoverArtContentType = CoverArtContentType.AUDIOBOOK
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
        // Show placeholder with icon and extension
        if (showPlaceholderAlways || bitmap == null) {
            // Icon and file extension overlay
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
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
                        .size(72.dp)
                        .padding(8.dp),
                    tint = palette.shade2
                )

                if (fileExtension.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = fileExtension.uppercase(),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
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
    contentType: CoverArtContentType = CoverArtContentType.AUDIOBOOK
) {
    CoverArt(
        bitmap = bitmap,
        contentDescription = contentDescription,
        modifier = modifier.size(size),
        cornerRadiusSize = 8.dp,
        elevation = 4.dp,
        showPlaceholderAlways = showPlaceholderAlways,
        fileExtension = fileExtension,
        contentType = contentType
    )
}
