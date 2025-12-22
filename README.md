# 🎧 Audiobook Player

A beautiful, modern Android audiobook player with a clean teal-blue UI design.

![Minimum SDK](https://img.shields.io/badge/minSdk-26-blue)
![Target SDK](https://img.shields.io/badge/targetSdk-34-green)
![Kotlin](https://img.shields.io/badge/Kotlin-1.9.20-purple)
![Jetpack Compose](https://img.shields.io/badge/Compose-Material3-teal)

## ✨ Features

- **Beautiful Teal UI** - Clean, modern design with a stunning teal color palette
- **Cover Art Display** - Automatically extracts and displays embedded cover art
- **Wide Format Support** - Plays M4B, MP3, M4A, AAC, OGG, OPUS, FLAC, WAV, and more
- **Playback Controls**:
  - Play/Pause with animated button
  - Skip forward/backward 30 seconds
  - Chapter navigation (for supported formats)
  - Adjustable playback speed (0.5x - 3x)
  - Seek bar with time display
- **Chapter Support** - Navigate between chapters in M4B and other chaptered formats
- **Background Playback** - Continue listening while using other apps
- **Media Session** - Control playback from notifications and lock screen

## 📱 Screenshots

The app features:
- Dark theme with teal accent colors
- Large cover art display with gradient background
- Intuitive playback controls
- Speed selector with visual feedback
- Progress slider with time remaining

## 🛠️ Building the App

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17
- Android SDK 34

### Build Steps

1. **Open in Android Studio**
   ```bash
   # Clone or copy the project
   # Open Android Studio
   # File → Open → Select the audiobook-player directory
   ```

2. **Sync Gradle**
   - Android Studio will automatically prompt to sync
   - Click "Sync Now" if prompted

3. **Build the APK**
   - Build → Build Bundle(s) / APK(s) → Build APK(s)
   - Or from terminal:
   ```bash
   ./gradlew assembleDebug
   ```

4. **Install on Device**
   - Connect your Android device via USB
   - Enable USB debugging in Developer Options
   - Run → Run 'app' (or press Shift+F10)

### Build Variants

- **Debug**: For development and testing
- **Release**: Optimized build for distribution

## 📁 Project Structure

```
audiobook-player/
├── app/
│   ├── src/main/
│   │   ├── java/com/audiobook/player/
│   │   │   ├── MainActivity.kt          # Main entry point
│   │   │   ├── model/
│   │   │   │   └── Audiobook.kt         # Data models
│   │   │   ├── player/
│   │   │   │   ├── AudiobookPlayer.kt   # ExoPlayer wrapper
│   │   │   │   └── PlaybackService.kt   # Background playback
│   │   │   ├── ui/
│   │   │   │   ├── components/          # Reusable UI components
│   │   │   │   │   ├── CoverArt.kt
│   │   │   │   │   └── PlaybackControls.kt
│   │   │   │   ├── screens/
│   │   │   │   │   └── PlayerScreen.kt  # Main player UI
│   │   │   │   └── theme/
│   │   │   │       ├── Color.kt         # Teal color palette
│   │   │   │       ├── Theme.kt         # Material3 theming
│   │   │   │       └── Type.kt          # Typography
│   │   │   └── utils/
│   │   │       └── TimeUtils.kt         # Formatting helpers
│   │   ├── res/
│   │   │   ├── drawable/                # App icons
│   │   │   ├── values/                  # Strings, themes
│   │   │   └── xml/                     # Backup rules
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

## 🎵 Supported Formats

| Format | Extension | Notes |
|--------|-----------|-------|
| AAC Audiobook | `.m4b` | Full chapter support |
| AAC Audio | `.m4a` | Primary audiobook format |
| MP3 | `.mp3` | Universal support |
| AAC | `.aac` | Raw AAC audio |
| Ogg Vorbis | `.ogg` | Open format |
| Opus | `.opus` | Modern codec |
| FLAC | `.flac` | Lossless audio |
| WAV | `.wav` | Uncompressed |
| Windows Media | `.wma` | Legacy format |

## 🎨 Color Palette

The app uses a carefully crafted teal color scheme:

```kotlin
// Primary colors
TealPrimary = #008B8B      // Dark Cyan
TealPrimaryLight = #4DB6AC // Light Teal
TealPrimaryDark = #00695C  // Deep Teal
TealAccent = #00BFA5       // Vibrant Accent

// Surface colors
SurfaceDark = #0D1B1E      // Background
SurfaceMedium = #1A2C30    // Cards
```

## 📋 Permissions

The app requires:

- **READ_MEDIA_AUDIO** (Android 13+) - Access audio files
- **READ_EXTERNAL_STORAGE** (Android 12-) - Access audio files
- **FOREGROUND_SERVICE** - Background playback
- **POST_NOTIFICATIONS** - Playback notifications

## 🚀 Future Enhancements

Potential features for future versions:

- [ ] Library management with folder scanning
- [ ] Bookmarks and sleep timer
- [ ] Equalizer and audio effects
- [ ] Car mode / Android Auto support
- [ ] Import/export progress
- [ ] Cloud sync
- [ ] Widgets

## 📄 License

This project is provided as-is for personal use.

## 🙏 Acknowledgments

Built with:
- [Jetpack Compose](https://developer.android.com/jetpack/compose) - Modern UI toolkit
- [Media3 ExoPlayer](https://developer.android.com/guide/topics/media/exoplayer) - Audio playback
- [Material Design 3](https://m3.material.io/) - Design system
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) - Async programming

---

Made with ❤️ for audiobook lovers
