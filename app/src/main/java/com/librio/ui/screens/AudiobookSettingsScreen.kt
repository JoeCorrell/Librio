package com.librio.ui.screens

import androidx.compose.runtime.Composable
import com.librio.ui.theme.AppIcons

@Composable
fun AudiobookSettingsScreen(
    onBack: () -> Unit = {},
    playbackSpeed: Float,
    onPlaybackSpeedChange: (Float) -> Unit,
    skipForward: Int,
    onSkipForwardChange: (Int) -> Unit,
    skipBack: Int,
    onSkipBackChange: (Int) -> Unit,
    autoRewind: Int,
    onAutoRewindChange: (Int) -> Unit,
    autoPlayNext: Boolean,
    onAutoPlayNextChange: (Boolean) -> Unit,
    resumePlayback: Boolean,
    onResumePlaybackChange: (Boolean) -> Unit,
    sleepTimerMinutes: Int,
    onSleepTimerChange: (Int) -> Unit,
    crossfadeDuration: Int,
    onCrossfadeDurationChange: (Int) -> Unit,
    volumeBoostEnabled: Boolean,
    onVolumeBoostEnabledChange: (Boolean) -> Unit,
    volumeBoostLevel: Float,
    onVolumeBoostLevelChange: (Float) -> Unit,
    normalizeAudio: Boolean,
    onNormalizeAudioChange: (Boolean) -> Unit,
    bassBoostLevel: Float,
    onBassBoostLevelChange: (Float) -> Unit,
    equalizerPreset: String,
    onEqualizerPresetChange: (String) -> Unit
) {
    MusicSettingsScreen(
        title = "Audiobook settings",
        icon = AppIcons.Audiobook,
        onBack = onBack,
        playbackSpeed = playbackSpeed,
        onPlaybackSpeedChange = onPlaybackSpeedChange,
        skipForward = skipForward,
        onSkipForwardChange = onSkipForwardChange,
        skipBack = skipBack,
        onSkipBackChange = onSkipBackChange,
        autoRewind = autoRewind,
        onAutoRewindChange = onAutoRewindChange,
        autoPlayNext = autoPlayNext,
        onAutoPlayNextChange = onAutoPlayNextChange,
        resumePlayback = resumePlayback,
        onResumePlaybackChange = onResumePlaybackChange,
        sleepTimerMinutes = sleepTimerMinutes,
        onSleepTimerChange = onSleepTimerChange,
        crossfadeDuration = crossfadeDuration,
        onCrossfadeDurationChange = onCrossfadeDurationChange,
        volumeBoostEnabled = volumeBoostEnabled,
        onVolumeBoostEnabledChange = onVolumeBoostEnabledChange,
        volumeBoostLevel = volumeBoostLevel,
        onVolumeBoostLevelChange = onVolumeBoostLevelChange,
        normalizeAudio = normalizeAudio,
        onNormalizeAudioChange = onNormalizeAudioChange,
        bassBoostLevel = bassBoostLevel,
        onBassBoostLevelChange = onBassBoostLevelChange,
        equalizerPreset = equalizerPreset,
        onEqualizerPresetChange = onEqualizerPresetChange
    )
}
