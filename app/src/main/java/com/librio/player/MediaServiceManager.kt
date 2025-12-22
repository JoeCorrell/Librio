package com.librio.player

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.PlaybackParameters
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Manager for connecting to the PlaybackService and controlling media playback
 * This enables background playback with notification controls
 */
class MediaServiceManager private constructor(private val context: Context) {

    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var mediaController: MediaController? = null

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration.asStateFlow()

    private val _currentMediaTitle = MutableStateFlow<String?>(null)
    val currentMediaTitle: StateFlow<String?> = _currentMediaTitle.asStateFlow()

    private var playerListener: Player.Listener? = null

    /**
     * Start the PlaybackService and connect to it
     */
    fun connect() {
        if (controllerFuture != null) return // Already connecting/connected

        // Start the service first
        val serviceIntent = Intent(context, PlaybackService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(context, serviceIntent)
        } else {
            context.startService(serviceIntent)
        }

        // Connect via MediaController
        val sessionToken = SessionToken(context, ComponentName(context, PlaybackService::class.java))
        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()

        controllerFuture?.addListener({
            try {
                mediaController = controllerFuture?.get()
                _isConnected.value = true

                // Add listener to track playback state
                playerListener = object : Player.Listener {
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        _isPlaying.value = isPlaying
                    }

                    override fun onPlaybackStateChanged(playbackState: Int) {
                        mediaController?.let { controller ->
                            _currentPosition.value = controller.currentPosition
                            _duration.value = controller.duration.coerceAtLeast(0)
                        }
                    }

                    override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                        _currentMediaTitle.value = mediaMetadata.title?.toString()
                    }
                }
                mediaController?.addListener(playerListener!!)

            } catch (e: Exception) {
                e.printStackTrace()
                _isConnected.value = false
            }
        }, MoreExecutors.directExecutor())
    }

    /**
     * Disconnect from the service
     */
    fun disconnect() {
        playerListener?.let { mediaController?.removeListener(it) }
        controllerFuture?.let { MediaController.releaseFuture(it) }
        controllerFuture = null
        mediaController = null
        _isConnected.value = false
    }

    /**
     * Load and play media
     */
    fun playMedia(
        uri: Uri,
        title: String,
        artist: String = "",
        startPosition: Long = 0L,
        artworkUri: Uri? = null
    ) {
        val controller = mediaController ?: return

        val mediaItem = MediaItem.Builder()
            .setUri(uri)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(title)
                    .setArtist(artist)
                    .setArtworkUri(artworkUri)
                    .build()
            )
            .build()

        controller.setMediaItem(mediaItem)
        controller.prepare()
        if (startPosition > 0) {
            controller.seekTo(startPosition)
        }
        controller.play()
    }

    /**
     * Play
     */
    fun play() {
        mediaController?.play()
    }

    /**
     * Pause
     */
    fun pause() {
        mediaController?.pause()
    }

    /**
     * Toggle play/pause
     */
    fun togglePlayPause() {
        mediaController?.let {
            if (it.isPlaying) it.pause() else it.play()
        }
    }

    /**
     * Seek to position
     */
    fun seekTo(position: Long) {
        mediaController?.seekTo(position)
    }

    /**
     * Seek forward by seconds
     */
    fun seekForward(seconds: Int = 30) {
        mediaController?.let {
            val newPosition = (it.currentPosition + seconds * 1000).coerceAtMost(it.duration)
            it.seekTo(newPosition)
        }
    }

    /**
     * Seek backward by seconds
     */
    fun seekBackward(seconds: Int = 10) {
        mediaController?.let {
            val newPosition = (it.currentPosition - seconds * 1000).coerceAtLeast(0)
            it.seekTo(newPosition)
        }
    }

    /**
     * Set playback speed
     */
    fun setPlaybackSpeed(speed: Float) {
        val safeSpeed = speed.coerceIn(0.5f, 2f)
        mediaController?.setPlaybackParameters(PlaybackParameters(safeSpeed, 1f))
    }

    /**
     * Get current position
     */
    fun getCurrentPosition(): Long {
        return mediaController?.currentPosition ?: 0L
    }

    /**
     * Get duration
     */
    fun getDuration(): Long {
        return mediaController?.duration?.coerceAtLeast(0) ?: 0L
    }

    /**
     * Check if playing
     */
    fun isCurrentlyPlaying(): Boolean {
        return mediaController?.isPlaying ?: false
    }

    /**
     * Get the underlying player for direct access (use with caution)
     */
    fun getPlayer(): Player? = mediaController

    /**
     * Stop playback and release
     */
    fun stop() {
        mediaController?.stop()
    }

    companion object {
        @Volatile
        private var instance: MediaServiceManager? = null

        fun getInstance(context: Context): MediaServiceManager {
            return instance ?: synchronized(this) {
                instance ?: MediaServiceManager(context.applicationContext).also { instance = it }
            }
        }
    }
}
