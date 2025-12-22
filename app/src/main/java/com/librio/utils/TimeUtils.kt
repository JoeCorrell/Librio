package com.librio.utils

import java.util.concurrent.TimeUnit

/**
 * Format milliseconds to human-readable time string
 * Examples: "1:23:45", "45:30", "0:30"
 */
fun formatTime(milliseconds: Long): String {
    if (milliseconds < 0) return "0:00"
    
    val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds) % 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds) % 60
    
    return if (hours > 0) {
        String.format("%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%d:%02d", minutes, seconds)
    }
}

/**
 * Format milliseconds to compact time string for remaining time
 * Examples: "1h 23m left", "45m left", "30s left"
 */
fun formatTimeRemaining(milliseconds: Long): String {
    if (milliseconds < 0) return "0s left"
    
    val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds) % 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds) % 60
    
    return when {
        hours > 0 -> "${hours}h ${minutes}m left"
        minutes > 0 -> "${minutes}m left"
        else -> "${seconds}s left"
    }
}

/**
 * Format playback speed for display
 */
fun formatSpeed(speed: Float): String {
    return if (speed == speed.toLong().toFloat()) {
        "${speed.toLong()}x"
    } else {
        "${speed}x"
    }
}

/**
 * Format file size to human-readable string
 */
fun formatFileSize(bytes: Long): String {
    if (bytes < 1024) return "$bytes B"
    val kb = bytes / 1024.0
    if (kb < 1024) return String.format("%.1f KB", kb)
    val mb = kb / 1024.0
    if (mb < 1024) return String.format("%.1f MB", mb)
    val gb = mb / 1024.0
    return String.format("%.2f GB", gb)
}

/**
 * Get supported audio formats
 */
val SupportedAudioFormats = listOf(
    "m4b",  // Main audiobook format
    "m4a",  // AAC audio
    "mp3",  // MP3 audio
    "aac",  // AAC audio
    "ogg",  // Ogg Vorbis
    "opus", // Opus audio
    "flac", // FLAC lossless
    "wav",  // WAV audio
    "wma",  // Windows Media Audio
    "aiff", // Apple audio
    "mp4",  // Can contain audio
)

/**
 * Check if a file extension is a supported audio format
 */
fun isSupportedAudioFormat(fileName: String): Boolean {
    val extension = fileName.substringAfterLast('.', "").lowercase()
    return extension in SupportedAudioFormats
}
