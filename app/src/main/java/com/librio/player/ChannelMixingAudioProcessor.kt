package com.librio.player

import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.util.UnstableApi
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Audio processor that handles:
 * - Mono audio mixing (combines L+R channels)
 * - Channel balance adjustment (pan left/right)
 *
 * Works with 16-bit PCM stereo audio.
 */
@OptIn(UnstableApi::class)
class ChannelMixingAudioProcessor : AudioProcessor {

    private var inputAudioFormat = AudioProcessor.AudioFormat.NOT_SET
    private var outputAudioFormat = AudioProcessor.AudioFormat.NOT_SET
    private var buffer = AudioProcessor.EMPTY_BUFFER
    private var inputEnded = false

    // Settings
    @Volatile
    private var monoEnabled = false

    @Volatile
    private var balance = 0f  // -1 = full left, 0 = center, 1 = full right

    fun setMono(enabled: Boolean) {
        monoEnabled = enabled
    }

    fun setBalance(value: Float) {
        balance = value.coerceIn(-1f, 1f)
    }

    override fun configure(inputAudioFormat: AudioProcessor.AudioFormat): AudioProcessor.AudioFormat {
        // Only process stereo 16-bit PCM audio
        if (inputAudioFormat.encoding != C.ENCODING_PCM_16BIT) {
            return AudioProcessor.AudioFormat.NOT_SET
        }
        if (inputAudioFormat.channelCount != 2) {
            // Not stereo, pass through unchanged
            return AudioProcessor.AudioFormat.NOT_SET
        }

        this.inputAudioFormat = inputAudioFormat
        // Output same format (we modify in-place)
        this.outputAudioFormat = inputAudioFormat
        return outputAudioFormat
    }

    override fun isActive(): Boolean {
        // Active when we have valid stereo input and mono or balance is set
        return outputAudioFormat != AudioProcessor.AudioFormat.NOT_SET &&
                (monoEnabled || balance != 0f)
    }

    override fun queueInput(inputBuffer: ByteBuffer) {
        if (!isActive()) {
            // Pass through unchanged
            buffer = inputBuffer
            return
        }

        val remaining = inputBuffer.remaining()
        if (remaining == 0) {
            buffer = AudioProcessor.EMPTY_BUFFER
            return
        }

        // Allocate output buffer if needed
        if (buffer.capacity() < remaining) {
            buffer = ByteBuffer.allocateDirect(remaining).order(ByteOrder.nativeOrder())
        } else {
            buffer.clear()
        }

        // Process samples (16-bit stereo = 4 bytes per sample pair)
        val samplePairs = remaining / 4

        for (i in 0 until samplePairs) {
            // Read left and right samples (16-bit signed)
            val left = inputBuffer.short.toInt()
            val right = inputBuffer.short.toInt()

            val (outLeft, outRight) = if (monoEnabled) {
                // Mono: average both channels
                val mono = ((left + right) / 2).coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                mono to mono
            } else if (balance != 0f) {
                // Apply balance
                // Balance < 0 means reduce right channel
                // Balance > 0 means reduce left channel
                val leftGain = if (balance > 0) 1f - balance else 1f
                val rightGain = if (balance < 0) 1f + balance else 1f

                val newLeft = (left * leftGain).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                val newRight = (right * rightGain).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                newLeft to newRight
            } else {
                // No change
                left to right
            }

            buffer.putShort(outLeft.toShort())
            buffer.putShort(outRight.toShort())
        }

        buffer.flip()
    }

    override fun queueEndOfStream() {
        inputEnded = true
    }

    override fun getOutput(): ByteBuffer {
        val output = buffer
        buffer = AudioProcessor.EMPTY_BUFFER
        return output
    }

    override fun isEnded(): Boolean {
        return inputEnded && buffer === AudioProcessor.EMPTY_BUFFER
    }

    override fun flush() {
        buffer = AudioProcessor.EMPTY_BUFFER
        inputEnded = false
    }

    override fun reset() {
        flush()
        inputAudioFormat = AudioProcessor.AudioFormat.NOT_SET
        outputAudioFormat = AudioProcessor.AudioFormat.NOT_SET
    }
}
