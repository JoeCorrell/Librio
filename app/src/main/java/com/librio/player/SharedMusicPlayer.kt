package com.librio.player

import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.exoplayer.ExoPlayer

/**
 * Shared ExoPlayer instance for music playback.
 * Keeps a single player alive across screens and the playback service,
 * with simple reference counting to avoid premature release.
 */
object SharedMusicPlayer {
    private var player: ExoPlayer? = null
    private var refCount = 0

    @Synchronized
    fun acquire(context: Context): ExoPlayer {
        if (player == null) {
            player = ExoPlayer.Builder(context.applicationContext)
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(C.USAGE_MEDIA)
                        .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                        .build(),
                    true
                )
                .setHandleAudioBecomingNoisy(true)
                .setWakeMode(C.WAKE_MODE_LOCAL)
                .build()
        }
        refCount++
        return player!!
    }

    @Synchronized
    fun release() {
        if (refCount > 0) {
            refCount--
            if (refCount == 0) {
                player?.release()
                player = null
            }
        }
    }
}
