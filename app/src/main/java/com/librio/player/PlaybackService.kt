package com.librio.player

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.os.Build
import android.os.Bundle
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.compose.ui.graphics.toArgb
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.MediaStyleNotificationHelper
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.librio.MainActivity
import com.librio.R
import com.librio.ui.theme.AppTheme
import com.librio.ui.theme.getThemePalette
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture

/**
 * Service for background audio playback with media session support
 * Provides notification controls for play/pause, skip, etc.
 * Shows notification in the notification drawer (like YouTube)
 */
class PlaybackService : MediaSessionService() {

    private var player: ExoPlayer? = null
    private var mediaSession: MediaSession? = null
    private var notificationManager: NotificationManager? = null
    private val actionReceiver = PlaybackActionReceiver()
    private var isForegroundStarted = false
    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            if (isPlaying && !isForegroundStarted) {
                startForegroundNotification()
            }
            updateNotification()
        }

        override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
            updateNotification()
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            val activePlayer = mediaSession?.player ?: player
            when (playbackState) {
                Player.STATE_READY -> {
                    if (activePlayer?.playWhenReady == true && !isForegroundStarted) {
                        startForegroundNotification()
                    }
                }
                Player.STATE_ENDED -> {
                    updateNotification()
                }
            }
        }
    }

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "librio_playback_channel"
        const val NOTIFICATION_ID = 1001

        // Broadcast actions for notification buttons
        const val ACTION_PLAY = "com.librio.ACTION_PLAY"
        const val ACTION_PAUSE = "com.librio.ACTION_PAUSE"
        const val ACTION_PREVIOUS = "com.librio.ACTION_PREVIOUS"
        const val ACTION_NEXT = "com.librio.ACTION_NEXT"
        const val ACTION_SKIP_BACK = "com.librio.ACTION_SKIP_BACK"
        const val ACTION_SKIP_FORWARD = "com.librio.ACTION_SKIP_FORWARD"
        const val ACTION_FAST_FORWARD = "com.librio.ACTION_FAST_FORWARD"
        const val ACTION_STOP = "com.librio.ACTION_STOP"

        // Custom session commands
        const val COMMAND_SKIP_BACK = "SKIP_BACK"
        const val COMMAND_SKIP_FORWARD = "SKIP_FORWARD"
        const val COMMAND_FAST_FORWARD = "FAST_FORWARD"

        fun start(context: Context) {
            val appContext = context.applicationContext
            val intent = Intent(appContext, PlaybackService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startForegroundService(appContext, intent)
            } else {
                appContext.startService(intent)
            }
        }
    }

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()

        notificationManager = getSystemService(NotificationManager::class.java)

        // Create notification channel for Android O+
        createNotificationChannel()

        // Register broadcast receiver for notification actions
        val filter = IntentFilter().apply {
            addAction(ACTION_PLAY)
            addAction(ACTION_PAUSE)
            addAction(ACTION_PREVIOUS)
            addAction(ACTION_NEXT)
            addAction(ACTION_SKIP_BACK)
            addAction(ACTION_SKIP_FORWARD)
            addAction(ACTION_FAST_FORWARD)
            addAction(ACTION_STOP)
        }
        ContextCompat.registerReceiver(this, actionReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED)

        // Grab shared ExoPlayer with background-friendly configuration
        player = SharedMusicPlayer.acquire(this)
        player?.addListener(playerListener)

        // Create pending intent for notification tap
        val sessionActivityIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build media session with custom callback
        val activePlayer = player ?: return
        mediaSession = MediaSession.Builder(this, activePlayer)
            .setSessionActivity(sessionActivityIntent)
            .setCallback(MediaSessionCallback())
            .build()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Start as foreground service immediately to comply with Android requirements
        if (!isForegroundStarted) {
            startForegroundNotification()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startForegroundNotification() {
        if (isForegroundStarted) return

        val notification = buildNotification(
            title = mediaSession?.player?.mediaMetadata?.title?.toString() ?: "Librio",
            artist = mediaSession?.player?.mediaMetadata?.artist?.toString() ?: "Ready to play",
            isPlaying = mediaSession?.player?.isPlaying ?: false
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, notification, android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
        isForegroundStarted = true
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Media Playback",
                NotificationManager.IMPORTANCE_LOW // LOW importance keeps it in notification shade without sound
            ).apply {
                description = "Shows media playback controls"
                setShowBadge(true)
                setSound(null, null)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }

            notificationManager?.createNotificationChannel(channel)
        }
    }

    private fun updateNotification() {
        val session = mediaSession ?: return
        val player = session.player
        val metadata = player.mediaMetadata

        val notification = buildNotification(
            title = metadata.title?.toString() ?: "Unknown",
            artist = metadata.artist?.toString() ?: "Unknown Artist",
            isPlaying = player.isPlaying
        )

        notificationManager?.notify(NOTIFICATION_ID, notification)
    }

    private fun buildNotification(title: String, artist: String, isPlaying: Boolean): Notification {
        // Create pending intents for notification actions
        val playIntent = PendingIntent.getBroadcast(
            this, 0,
            Intent(ACTION_PLAY).setPackage(packageName),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val pauseIntent = PendingIntent.getBroadcast(
            this, 1,
            Intent(ACTION_PAUSE).setPackage(packageName),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val skipBackIntent = PendingIntent.getBroadcast(
            this, 2,
            Intent(ACTION_SKIP_BACK).setPackage(packageName),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val skipForwardIntent = PendingIntent.getBroadcast(
            this, 3,
            Intent(ACTION_SKIP_FORWARD).setPackage(packageName),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val fastForwardIntent = PendingIntent.getBroadcast(
            this, 4,
            Intent(ACTION_FAST_FORWARD).setPackage(packageName),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val previousIntent = PendingIntent.getBroadcast(
            this, 5,
            Intent(ACTION_PREVIOUS).setPackage(packageName),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val nextIntent = PendingIntent.getBroadcast(
            this, 6,
            Intent(ACTION_NEXT).setPackage(packageName),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val stopIntent = PendingIntent.getBroadcast(
            this, 7,
            Intent(ACTION_STOP).setPackage(packageName),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Content intent - opens app when notification is tapped
        val contentIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val accentColor = loadAccentColor()
        val metadata = mediaSession?.player?.mediaMetadata
        val largeIcon = metadata?.artworkData?.let { bytes ->
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } ?: buildPlaceholderArt(accentColor)

        val builderStyle = mediaSession?.let {
            MediaStyleNotificationHelper.MediaStyle(it)
                // Show previous, play/pause, and next in compact view
                .setShowActionsInCompactView(0, 2, 5)
        }

        // Build notification with media style
        val builder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(artist)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setLargeIcon(largeIcon)
            .setContentIntent(contentIntent)
            .setDeleteIntent(stopIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(isPlaying)
            .setColorized(true)
            .setColor(accentColor)
            .setShowWhen(false)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_TRANSPORT)
            .setOnlyAlertOnce(true)
        builderStyle?.let { builder.setStyle(it) }

        builder
            .addAction(
                NotificationCompat.Action.Builder(
                    android.R.drawable.ic_media_previous,
                    "Previous",
                    previousIntent
                ).build()
            )
            .addAction(
                NotificationCompat.Action.Builder(
                    R.drawable.ic_skip_back,
                    "Rewind",
                    skipBackIntent
                ).build()
            )
            // Play/Pause in the middle for easy thumb reach
            .addAction(
                if (isPlaying) {
                    NotificationCompat.Action.Builder(
                        R.drawable.ic_pause,
                        "Pause",
                        pauseIntent
                    ).build()
                } else {
                    NotificationCompat.Action.Builder(
                        R.drawable.ic_play,
                        "Play",
                        playIntent
                    ).build()
                }
            )
            // Fast forward is compact-visible
            .addAction(
                NotificationCompat.Action.Builder(
                    R.drawable.ic_skip_forward,
                    "Fast Forward 60s",
                    fastForwardIntent
                ).build()
            )
            .addAction(
                NotificationCompat.Action.Builder(
                    R.drawable.ic_skip_forward,
                    "Forward 30s",
                    skipForwardIntent
                ).build()
            )
            .addAction(
                NotificationCompat.Action.Builder(
                    android.R.drawable.ic_media_next,
                    "Next",
                    nextIntent
                ).build()
            )
            .addAction(
                NotificationCompat.Action.Builder(
                    android.R.drawable.ic_menu_close_clear_cancel,
                    "Stop",
                    stopIntent
                ).build()
            )
        return builder.build()
    }

    private fun loadAccentColor(): Int {
        val prefs = getSharedPreferences("audible_library_settings", Context.MODE_PRIVATE)
        val themeName = prefs.getString("app_theme", AppTheme.TEAL.name) ?: AppTheme.TEAL.name
        val darkMode = prefs.getBoolean("dark_mode", false)
        val theme = runCatching { AppTheme.valueOf(themeName) }.getOrDefault(AppTheme.TEAL)
        // Accent theme may differ; default to primary theme if missing
        val accentThemeName = prefs.getString("accent_theme", themeName) ?: themeName
        val accentTheme = runCatching { AppTheme.valueOf(accentThemeName) }.getOrDefault(theme)
        val palette = getThemePalette(accentTheme, darkMode)
        return palette.accent.toArgb()
    }

    private fun buildPlaceholderArt(accentColor: Int): Bitmap {
        val size = 320
        val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.shader = LinearGradient(
            0f, 0f, size.toFloat(), size.toFloat(),
            accentColor,
            adjustAlpha(accentColor, 0.65f),
            Shader.TileMode.CLAMP
        )
        canvas.drawRect(0f, 0f, size.toFloat(), size.toFloat(), paint)
        paint.shader = LinearGradient(
            0f, size.toFloat(), size.toFloat(), 0f,
            adjustAlpha(accentColor, 0.85f),
            adjustAlpha(accentColor, 0.35f),
            Shader.TileMode.CLAMP
        )
        paint.alpha = 180
        canvas.drawRect(0f, 0f, size.toFloat(), size.toFloat(), paint)
        return bmp
    }

    private fun adjustAlpha(color: Int, factor: Float): Int {
        val alpha = (android.graphics.Color.alpha(color) * factor).toInt().coerceIn(0, 255)
        val r = android.graphics.Color.red(color)
        val g = android.graphics.Color.green(color)
        val b = android.graphics.Color.blue(color)
        return android.graphics.Color.argb(alpha, r, g, b)
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        val player = mediaSession?.player
        // Only stop if not playing - allows background playback to continue
        if (player != null && !player.playWhenReady) {
            stopSelf()
        }
    }

    override fun onDestroy() {
        try {
            unregisterReceiver(actionReceiver)
        } catch (e: Exception) {
            // Receiver might not be registered
        }
        player?.removeListener(playerListener)
        mediaSession?.release()
        mediaSession = null
        SharedMusicPlayer.release()
        player = null
        super.onDestroy()
    }

    // Broadcast receiver to handle notification button clicks
    inner class PlaybackActionReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val player = mediaSession?.player ?: return

            when (intent?.action) {
                ACTION_PLAY -> player.play()
                ACTION_PAUSE -> player.pause()
                ACTION_PREVIOUS -> player.seekToPrevious()
                ACTION_NEXT -> player.seekToNext()
                ACTION_SKIP_BACK -> player.seekTo((player.currentPosition - 10000).coerceAtLeast(0))
                ACTION_SKIP_FORWARD -> {
                    val newPosition = player.currentPosition + 30000
                    val maxPosition = if (player.duration > 0) player.duration else Long.MAX_VALUE
                    player.seekTo(newPosition.coerceAtMost(maxPosition))
                }
                ACTION_FAST_FORWARD -> {
                    val newPosition = player.currentPosition + 60000
                    val maxPosition = if (player.duration > 0) player.duration else Long.MAX_VALUE
                    player.seekTo(newPosition.coerceAtMost(maxPosition))
                }
                ACTION_STOP -> {
                    player.stop()
                    stopSelf()
                }
            }
        }
    }

    // Custom session callback for handling commands
    private inner class MediaSessionCallback : MediaSession.Callback {
        override fun onConnect(
            session: MediaSession,
            controller: MediaSession.ControllerInfo
        ): MediaSession.ConnectionResult {
            val sessionCommands = MediaSession.ConnectionResult.DEFAULT_SESSION_COMMANDS.buildUpon()
                .add(SessionCommand(COMMAND_SKIP_BACK, Bundle.EMPTY))
                .add(SessionCommand(COMMAND_SKIP_FORWARD, Bundle.EMPTY))
                .add(SessionCommand(COMMAND_FAST_FORWARD, Bundle.EMPTY))
                .build()

            return MediaSession.ConnectionResult.AcceptedResultBuilder(session)
                .setAvailableSessionCommands(sessionCommands)
                .build()
        }

        override fun onCustomCommand(
            session: MediaSession,
            controller: MediaSession.ControllerInfo,
            customCommand: SessionCommand,
            args: Bundle
        ): ListenableFuture<SessionResult> {
            val player = session.player
            when (customCommand.customAction) {
                COMMAND_SKIP_BACK -> player.seekTo((player.currentPosition - 10000).coerceAtLeast(0))
                COMMAND_SKIP_FORWARD -> {
                    val newPosition = player.currentPosition + 30000
                    val maxPosition = if (player.duration > 0) player.duration else Long.MAX_VALUE
                    player.seekTo(newPosition.coerceAtMost(maxPosition))
                }
                COMMAND_FAST_FORWARD -> {
                    val newPosition = player.currentPosition + 60000
                    val maxPosition = if (player.duration > 0) player.duration else Long.MAX_VALUE
                    player.seekTo(newPosition.coerceAtMost(maxPosition))
                }
            }
            return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
        }
    }
}
