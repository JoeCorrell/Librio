package com.librio.player

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.common.C
import com.librio.model.Audiobook
import com.librio.model.Chapter
import com.librio.model.PlaybackState
import com.librio.player.SharedMusicPlayer
import com.librio.player.PlaybackService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Manager class for audiobook playback using ExoPlayer
 * Supports M4B, MP3, M4A, AAC, OGG, FLAC, and other common formats
 */
@Suppress("UNUSED_PARAMETER", "UNUSED_VARIABLE")
class AudiobookPlayer(private val context: Context) {

    private var exoPlayer: androidx.media3.exoplayer.ExoPlayer? = null
    private var playerListener: Player.Listener? = null
    private var positionUpdateJob: Job? = null
    private var fadeOnPauseResume: Boolean = false
    private val scopeJob = Job()
    private val scope = CoroutineScope(scopeJob + Dispatchers.Main)
    
    private val _currentAudiobook = MutableStateFlow<Audiobook?>(null)
    val currentAudiobook: StateFlow<Audiobook?> = _currentAudiobook.asStateFlow()
    
    private val _playbackState = MutableStateFlow(PlaybackState())
    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()
    
    init {
        initializePlayer()
    }
    
    private fun initializePlayer() {
        val player = SharedMusicPlayer.acquire(context)
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                updatePlaybackState()
                when (state) {
                    Player.STATE_READY -> {
                        _playbackState.value = _playbackState.value.copy(
                            isLoading = false,
                            duration = player.duration
                        )
                        // Audio effects are managed by SharedMusicPlayer's AudioSettingsManager
                    }
                    Player.STATE_BUFFERING -> {
                        _playbackState.value = _playbackState.value.copy(isLoading = true)
                    }
                    Player.STATE_ENDED -> {
                        _playbackState.value = _playbackState.value.copy(isPlaying = false)
                    }
                    Player.STATE_IDLE -> {
                        _playbackState.value = _playbackState.value.copy(isLoading = false)
                    }
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                val currentUri = player.currentMediaItem?.localConfiguration?.uri
                val audiobookUri = _currentAudiobook.value?.uri
                // Only react to audiobook media items
                val isAudiobook = audiobookUri != null && currentUri == audiobookUri
                if (!isAudiobook) return

                _playbackState.value = _playbackState.value.copy(isPlaying = isPlaying)
                if (isPlaying) {
                    startPositionUpdates()
                    PlaybackService.start(context)
                } else {
                    stopPositionUpdates()
                }
            }

            override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                _playbackState.value = _playbackState.value.copy(
                    error = error.message ?: "Playback error occurred",
                    isLoading = false
                )
            }
        }
        player.addListener(listener)

        exoPlayer = player
        playerListener = listener
    }

    
    /**
     * Load an audiobook from a URI
     * @param uri The URI of the audiobook file
     * @param editedTitle Optional edited title to use instead of embedded metadata
     * @param editedAuthor Optional edited author to use instead of embedded metadata
     */
    suspend fun loadAudiobook(uri: Uri, editedTitle: String? = null, editedAuthor: String? = null) {
        _playbackState.value = _playbackState.value.copy(isLoading = true, error = null)

        try {
            // Extract metadata in background
            val extractedMetadata = withContext(Dispatchers.IO) {
                extractMetadata(uri)
            }

            // Use edited metadata if provided, otherwise use extracted
            val metadata = extractedMetadata.copy(
                title = editedTitle?.takeIf { it.isNotBlank() } ?: extractedMetadata.title,
                author = editedAuthor?.takeIf { it.isNotBlank() } ?: extractedMetadata.author
            )

            _currentAudiobook.value = metadata

            // Prepare the player
            val mediaItem = MediaItem.Builder()
                .setUri(uri)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(metadata.title)
                        .setArtist(metadata.author)
                        .build()
                )
                .build()

            exoPlayer?.apply {
                setMediaItem(mediaItem)
                prepare()
            }

            // Update chapters from player after preparation
            exoPlayer?.let { player ->
                val chapters = extractChaptersFromPlayer(player, metadata)
                _currentAudiobook.value = metadata.copy(chapters = chapters)
            }

        } catch (e: Exception) {
            _playbackState.value = _playbackState.value.copy(
                error = "Failed to load audiobook: ${e.message}",
                isLoading = false
            )
        }
    }
    
    /**
     * Extract metadata from audio file
     * Uses robust cover art extraction with fallback handling for device compatibility
     */
    private fun extractMetadata(uri: Uri): Audiobook {
        val retriever = MediaMetadataRetriever()
        var title = "Unknown Title"
        var author = "Unknown Author"
        var narrator: String? = null
        var duration = 0L
        var fileName = ""

        try {
            retriever.setDataSource(context, uri)

            title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
                ?: retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)
                ?: uri.lastPathSegment?.substringBeforeLast(".") ?: "Unknown Title"

            author = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
                ?: retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST)
                ?: retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_AUTHOR)
                ?: "Unknown Author"

            narrator = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COMPOSER)

            duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                ?.toLongOrNull() ?: 0L

            fileName = uri.lastPathSegment ?: ""

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                retriever.release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Extract cover art using robust method with multiple fallback approaches
        val coverArt = extractCoverArtRobust(context, uri, 512)

        return Audiobook(
            uri = uri,
            title = title,
            author = author,
            narrator = narrator,
            coverArt = coverArt,
            duration = duration,
            fileName = fileName,
        )
    }

    /**
     * Extract cover art from a media file with robust fallback handling.
     * Uses multiple approaches to handle device-specific codec issues.
     */
    private fun extractCoverArtRobust(context: android.content.Context, uri: Uri, targetSize: Int): Bitmap? {
        var coverArt: Bitmap? = null
        var retriever: MediaMetadataRetriever? = null

        try {
            retriever = MediaMetadataRetriever()
            retriever.setDataSource(context, uri)

            // Try 1: Extract embedded picture with ARGB_8888 (better color/alpha support)
            val artBytes = retriever.embeddedPicture
            if (artBytes != null && artBytes.isNotEmpty()) {
                coverArt = decodeBitmapSafely(artBytes, targetSize, Bitmap.Config.ARGB_8888)

                // Try 2: If ARGB_8888 failed, try RGB_565 (uses less memory)
                if (coverArt == null) {
                    coverArt = decodeBitmapSafely(artBytes, targetSize, Bitmap.Config.RGB_565)
                }
            }

            // Try 3: If embedded picture failed, try getting a video frame
            if (coverArt == null) {
                try {
                    val frame = retriever.getFrameAtTime(1000000L) // 1 second in
                    if (frame != null) {
                        coverArt = scaleBitmapIfNeeded(frame, targetSize)
                    }
                } catch (_: Exception) {
                    // Not a video file or frame extraction failed
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                retriever?.release()
            } catch (_: Exception) {}
        }

        return coverArt
    }

    /**
     * Decode bitmap bytes safely with proper error handling and memory management
     */
    private fun decodeBitmapSafely(bytes: ByteArray, targetSize: Int, config: Bitmap.Config): Bitmap? {
        return try {
            // First pass: get dimensions only
            val boundsOptions = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size, boundsOptions)

            // Calculate sample size
            var inSampleSize = 1
            val (height, width) = boundsOptions.run { outHeight to outWidth }
            if (height > targetSize || width > targetSize) {
                val halfHeight = height / 2
                val halfWidth = width / 2
                while (halfHeight / inSampleSize >= targetSize && halfWidth / inSampleSize >= targetSize) {
                    inSampleSize *= 2
                }
            }

            // Second pass: decode with calculated sample size
            val decodeOptions = BitmapFactory.Options().apply {
                inJustDecodeBounds = false
                this.inSampleSize = inSampleSize
                inPreferredConfig = config
                inScaled = false
            }

            BitmapFactory.decodeByteArray(bytes, 0, bytes.size, decodeOptions)
        } catch (e: OutOfMemoryError) {
            // Try with a larger sample size if we ran out of memory
            try {
                val fallbackOptions = BitmapFactory.Options().apply {
                    inSampleSize = 4
                    inPreferredConfig = Bitmap.Config.RGB_565
                    inScaled = false
                }
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size, fallbackOptions)
            } catch (_: Exception) {
                null
            }
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Scale bitmap if larger than target size
     */
    private fun scaleBitmapIfNeeded(bitmap: Bitmap, targetSize: Int): Bitmap {
        if (bitmap.width <= targetSize && bitmap.height <= targetSize) {
            return bitmap
        }
        val scale = targetSize.toFloat() / maxOf(bitmap.width, bitmap.height)
        val newWidth = (bitmap.width * scale).toInt()
        val newHeight = (bitmap.height * scale).toInt()
        return try {
            Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
        } catch (_: Exception) {
            bitmap
        }
    }
    
    /**
     * Extract chapters from ExoPlayer's chapter metadata
     */
    private fun extractChaptersFromPlayer(player: ExoPlayer, audiobook: Audiobook): List<Chapter> {
        val chapters = mutableListOf<Chapter>()
        
        // Try to get chapters from media metadata
        val mediaItem = player.currentMediaItem
        val metadata = mediaItem?.mediaMetadata
        
        // ExoPlayer may expose chapters through timeline
        val timeline = player.currentTimeline
        if (!timeline.isEmpty) {
            val window = androidx.media3.common.Timeline.Window()
            for (i in 0 until timeline.windowCount) {
                timeline.getWindow(i, window)
                // Check for chapter markers in window
            }
        }
        
        // If no chapters found, create a single chapter for the whole book
        if (chapters.isEmpty()) {
            chapters.add(
                Chapter(
                    index = 0,
                    title = audiobook.title,
                    startTime = 0L,
                    endTime = player.duration.coerceAtLeast(0L)
                )
            )
        }
        
        return chapters
    }
    
    fun play() {
        PlaybackService.start(context)
        if (fadeOnPauseResume) {
            SharedMusicPlayer.playWithFade(context)
        } else {
            exoPlayer?.play()
        }
    }

    fun pause() {
        if (fadeOnPauseResume) {
            SharedMusicPlayer.pauseWithFade(context)
        } else {
            exoPlayer?.pause()
        }
    }

    fun togglePlayPause() {
        exoPlayer?.let {
            if (it.isPlaying) pause() else play()
        }
    }

    fun setFadeOnPauseResume(enabled: Boolean) {
        fadeOnPauseResume = enabled
    }
    
    fun seekTo(position: Long) {
        exoPlayer?.seekTo(position)
        updatePlaybackState()
    }
    
    fun seekForward(seconds: Int = 30) {
        exoPlayer?.let {
            val newPosition = (it.currentPosition + seconds * 1000).coerceAtMost(it.duration)
            seekTo(newPosition)
        }
    }
    
    fun seekBackward(seconds: Int = 30) {
        exoPlayer?.let {
            val newPosition = (it.currentPosition - seconds * 1000).coerceAtLeast(0)
            seekTo(newPosition)
        }
    }
    
    fun setPlaybackSpeed(speed: Float) {
        val safeSpeed = speed.coerceIn(0.5f, 2f)
        exoPlayer?.playbackParameters = PlaybackParameters(safeSpeed, 1f)
        _playbackState.value = _playbackState.value.copy(playbackSpeed = safeSpeed)
    }

    fun setEqualizerPreset(preset: String) {
        SharedMusicPlayer.setEqualizerPreset(context, preset)
    }

    fun setVolumeBoost(enabled: Boolean, level: Float) {
        SharedMusicPlayer.setVolumeBoost(context, enabled, level)
    }

    fun setNormalizeAudio(enabled: Boolean) {
        SharedMusicPlayer.setNormalizeAudio(context, enabled)
    }

    fun setBassBoostLevel(level: Float) {
        SharedMusicPlayer.setBassBoostLevel(context, level.coerceIn(0f, 1f))
    }

    /**
     * Update all audio settings at once - useful when loading settings from JSON
     */
    fun updateAllAudioSettings(
        newVolumeBoostEnabled: Boolean,
        newVolumeBoostLevel: Float,
        newNormalizeAudio: Boolean,
        newBassBoostLevel: Float,
        newEqualizerPreset: String
    ) {
        SharedMusicPlayer.updateAudioSettings(
            context,
            equalizerPreset = newEqualizerPreset,
            volumeBoostEnabled = newVolumeBoostEnabled,
            volumeBoostLevel = newVolumeBoostLevel,
            normalizeAudio = newNormalizeAudio,
            bassBoostLevel = newBassBoostLevel.coerceIn(0f, 1f)
        )
    }

    /**
     * Refresh all audio effects - call this to ensure effects match current settings
     * Useful when resuming playback or after loading settings from JSON
     */
    fun refreshAudioEffects() {
        SharedMusicPlayer.refreshAudioEffects(context)
    }

    fun skipToChapter(chapterIndex: Int) {
        val chapters = _currentAudiobook.value?.chapters ?: return
        if (chapterIndex in chapters.indices) {
            val chapter = chapters[chapterIndex]
            seekTo(chapter.startTime)
            _playbackState.value = _playbackState.value.copy(currentChapterIndex = chapterIndex)
        }
    }
    
    fun nextChapter() {
        val currentIndex = _playbackState.value.currentChapterIndex
        val chapters = _currentAudiobook.value?.chapters ?: return
        if (currentIndex < chapters.size - 1) {
            skipToChapter(currentIndex + 1)
        }
    }
    
    fun previousChapter() {
        val currentIndex = _playbackState.value.currentChapterIndex
        val position = exoPlayer?.currentPosition ?: 0
        val chapters = _currentAudiobook.value?.chapters ?: return

        // Validate index bounds
        if (currentIndex < 0 || currentIndex >= chapters.size) return

        // If we're more than 3 seconds into the chapter, go to start of current chapter
        // Otherwise go to previous chapter
        if (currentIndex > 0 && position < 3000) {
            skipToChapter(currentIndex - 1)
        } else {
            seekTo(chapters[currentIndex].startTime)
        }
    }
    
    private fun updatePlaybackState() {
        exoPlayer?.let { player ->
            val currentUri = player.currentMediaItem?.localConfiguration?.uri
            val audiobookUri = _currentAudiobook.value?.uri
            // Ignore updates when the shared player is being used for music
            if (audiobookUri != null && currentUri != null && audiobookUri != currentUri) {
                return
            }

            val position = player.currentPosition.coerceAtLeast(0)
            val duration = player.duration.coerceAtLeast(0)
            
            // Find current chapter
            val chapters = _currentAudiobook.value?.chapters ?: emptyList()
            val currentChapterIndex = chapters.indexOfLast { it.startTime <= position }
                .coerceAtLeast(0)
            
            _playbackState.value = _playbackState.value.copy(
                currentPosition = position,
                duration = duration,
                currentChapterIndex = currentChapterIndex,
            )
        }
    }
    
    private fun startPositionUpdates() {
        positionUpdateJob?.cancel()
        positionUpdateJob = scope.launch {
            while (true) {
                updatePlaybackState()
                delay(500) // Update every 500ms to save battery
            }
        }
    }
    
    private fun stopPositionUpdates() {
        positionUpdateJob?.cancel()
        positionUpdateJob = null
    }
    
    fun release() {
        stopPositionUpdates()
        scopeJob.cancel()
        playerListener?.let { listener ->
            exoPlayer?.removeListener(listener)
        }
        playerListener = null
        // Audio effects are managed by SharedMusicPlayer and will be released there
        SharedMusicPlayer.release()
        exoPlayer = null
    }
    
    fun clearError() {
        _playbackState.value = _playbackState.value.copy(error = null)
    }
}
