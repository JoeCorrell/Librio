package com.librio.player

import android.animation.ValueAnimator
import android.content.Context
import android.view.animation.DecelerateInterpolator
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.audio.SilenceSkippingAudioProcessor
import androidx.media3.exoplayer.audio.DefaultAudioSink
import androidx.media3.common.AudioAttributes

/**
 * Manages audio settings for ExoPlayer including:
 * - Trim silence (skip silent parts)
 * - Fade on pause/resume
 * - Mono audio mixing
 * - Channel balance (L/R)
 * - Gapless playback
 */
@OptIn(UnstableApi::class)
class AudioSettingsManager(private val context: Context) {

    // Current settings state
    var trimSilence: Boolean = false
        private set
    var monoAudio: Boolean = false
        private set
    var channelBalance: Float = 0f  // -1 = left, 0 = center, 1 = right
        private set
    var fadeOnPauseResume: Boolean = false
        private set
    var fadeDurationMs: Long = 300L
        private set
    var gaplessPlayback: Boolean = true
        private set

    private var currentPlayer: ExoPlayer? = null
    private var fadeAnimator: ValueAnimator? = null

    // Audio processors
    private var silenceProcessor: SilenceSkippingAudioProcessor? = null
    private var channelProcessor: ChannelMixingAudioProcessor? = null

    /**
     * Create an ExoPlayer configured with current audio settings
     */
    fun createConfiguredPlayer(): ExoPlayer {
        // Create silence skipping processor
        silenceProcessor = SilenceSkippingAudioProcessor(
            /* minimumSilenceDurationUs = */ 150_000L,  // 150ms minimum silence
            /* paddingSilenceUs = */ 20_000L,           // 20ms padding
            /* silenceThresholdLevel = */ 1024.toShort()  // Threshold level
        )

        // Create channel mixing processor for mono/balance
        channelProcessor = ChannelMixingAudioProcessor()

        // Create custom audio sink with our processors
        val audioSink = DefaultAudioSink.Builder(context)
            .setAudioProcessors(arrayOf(channelProcessor!!, silenceProcessor!!))
            .build()

        // Create renderers factory with custom audio sink
        val renderersFactory = object : DefaultRenderersFactory(context) {
            override fun buildAudioSink(
                context: Context,
                enableFloatOutput: Boolean,
                enableAudioTrackPlaybackParams: Boolean
            ): androidx.media3.exoplayer.audio.AudioSink {
                return audioSink
            }
        }

        val player = ExoPlayer.Builder(context, renderersFactory)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(C.USAGE_MEDIA)
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .build(),
                true
            )
            .setHandleAudioBecomingNoisy(true)
            .setWakeMode(C.WAKE_MODE_LOCAL)
            .setPauseAtEndOfMediaItems(!gaplessPlayback)
            .build()

        currentPlayer = player

        // Apply initial settings
        updateSilenceSkipping()
        updateChannelMixing()

        return player
    }

    /**
     * Update trim silence setting
     */
    fun setTrimSilence(enabled: Boolean) {
        trimSilence = enabled
        updateSilenceSkipping()
    }

    private fun updateSilenceSkipping() {
        silenceProcessor?.setEnabled(trimSilence)
    }

    /**
     * Update mono audio setting
     */
    fun setMonoAudio(enabled: Boolean) {
        monoAudio = enabled
        updateChannelMixing()
    }

    /**
     * Update channel balance (-1 = left, 0 = center, 1 = right)
     */
    fun setChannelBalance(balance: Float) {
        channelBalance = balance.coerceIn(-1f, 1f)
        updateChannelMixing()
    }

    private fun updateChannelMixing() {
        channelProcessor?.setMono(monoAudio)
        channelProcessor?.setBalance(channelBalance)
    }

    /**
     * Update fade on pause/resume setting
     */
    fun setFadeOnPauseResume(enabled: Boolean, durationMs: Long = 300L) {
        fadeOnPauseResume = enabled
        fadeDurationMs = durationMs
    }

    /**
     * Play with optional fade in
     */
    fun playWithFade(player: ExoPlayer) {
        fadeAnimator?.cancel()

        if (fadeOnPauseResume) {
            player.volume = 0f
            player.play()

            fadeAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
                duration = fadeDurationMs
                interpolator = DecelerateInterpolator()
                addUpdateListener { animator ->
                    val value = animator.animatedValue as Float
                    player.volume = value
                }
                start()
            }
        } else {
            player.volume = 1f
            player.play()
        }
    }

    /**
     * Pause with optional fade out
     */
    fun pauseWithFade(player: ExoPlayer, onComplete: (() -> Unit)? = null) {
        fadeAnimator?.cancel()

        if (fadeOnPauseResume) {
            val startVolume = player.volume

            fadeAnimator = ValueAnimator.ofFloat(startVolume, 0f).apply {
                duration = fadeDurationMs
                interpolator = DecelerateInterpolator()
                addUpdateListener { animator ->
                    val value = animator.animatedValue as Float
                    player.volume = value
                }
                addListener(object : android.animation.AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: android.animation.Animator) {
                        player.pause()
                        player.volume = 1f  // Reset volume for next play
                        onComplete?.invoke()
                    }
                })
                start()
            }
        } else {
            player.pause()
            onComplete?.invoke()
        }
    }

    /**
     * Update gapless playback setting
     */
    fun setGaplessPlayback(enabled: Boolean) {
        gaplessPlayback = enabled
        currentPlayer?.pauseAtEndOfMediaItems = !enabled
    }

    /**
     * Update all settings at once
     */
    fun updateAllSettings(
        trimSilence: Boolean,
        monoAudio: Boolean,
        channelBalance: Float,
        fadeOnPauseResume: Boolean,
        gaplessPlayback: Boolean
    ) {
        this.trimSilence = trimSilence
        this.monoAudio = monoAudio
        this.channelBalance = channelBalance.coerceIn(-1f, 1f)
        this.fadeOnPauseResume = fadeOnPauseResume
        this.gaplessPlayback = gaplessPlayback

        updateSilenceSkipping()
        updateChannelMixing()
        currentPlayer?.pauseAtEndOfMediaItems = !gaplessPlayback
    }

    /**
     * Release resources
     */
    fun release() {
        fadeAnimator?.cancel()
        fadeAnimator = null
        currentPlayer = null
        silenceProcessor = null
        channelProcessor = null
    }
}
