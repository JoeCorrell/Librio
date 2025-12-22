package com.librio.data

import android.os.Environment
import com.librio.model.ChangedFile
import com.librio.model.FileType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Tracks file modification timestamps and detects external changes
 * Used to reload JSON files when they've been modified outside the app
 */
class FileReloadManager {

    private val librioRoot = File(Environment.getExternalStorageDirectory(), "Librio")
    private val profilesRoot = File(librioRoot, "Profiles")
    private val profilesFile = File(librioRoot, "profiles.json")

    // In-memory cache of file timestamps
    private val fileTimestamps = mutableMapOf<String, Long>()

    /**
     * Check for changes in all tracked files for a profile
     * Returns list of files that have been modified externally
     */
    suspend fun checkForChanges(profileName: String): List<ChangedFile> = withContext(Dispatchers.IO) {
        try {
            val changes = mutableListOf<ChangedFile>()

            // Check master profiles.json
            checkFileChange(
                file = profilesFile,
                fileType = FileType.MASTER_PROFILES
            )?.let { changes.add(it) }

            val profileFolder = File(profilesRoot, sanitizeFolderName(profileName))
            if (!profileFolder.exists()) return@withContext changes

            // Check profile settings files
            checkFileChange(
                file = File(profileFolder, "profile_settings.json"),
                fileType = FileType.PROFILE_SETTINGS
            )?.let { changes.add(it) }

            checkFileChange(
                file = File(profileFolder, "audio_settings.json"),
                fileType = FileType.AUDIO_SETTINGS
            )?.let { changes.add(it) }

            checkFileChange(
                file = File(profileFolder, "reader_settings.json"),
                fileType = FileType.READER_SETTINGS
            )?.let { changes.add(it) }

            checkFileChange(
                file = File(profileFolder, "comic_settings.json"),
                fileType = FileType.COMIC_SETTINGS
            )?.let { changes.add(it) }

            checkFileChange(
                file = File(profileFolder, "movie_settings.json"),
                fileType = FileType.MOVIE_SETTINGS
            )?.let { changes.add(it) }

            checkFileChange(
                file = File(profileFolder, "progress.json"),
                fileType = FileType.PROGRESS
            )?.let { changes.add(it) }

            // Check playlist files
            val playlistsFolder = File(profileFolder, "Playlists")
            if (playlistsFolder.exists()) {
                playlistsFolder.listFiles { file -> file.extension == "json" }?.forEach { file ->
                    checkFileChange(
                        file = file,
                        fileType = FileType.PLAYLIST
                    )?.let { changes.add(it) }
                }
            }

            changes
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Check if a specific file has changed
     * Returns ChangedFile if modified, null otherwise
     */
    private fun checkFileChange(file: File, fileType: FileType): ChangedFile? {
        if (!file.exists()) {
            // File was deleted - remove from tracking
            fileTimestamps.remove(file.absolutePath)
            return null
        }

        val currentTimestamp = file.lastModified()
        val cachedTimestamp = fileTimestamps[file.absolutePath]

        return if (cachedTimestamp == null || currentTimestamp > cachedTimestamp) {
            // File is new or has been modified
            fileTimestamps[file.absolutePath] = currentTimestamp
            ChangedFile(
                path = file.absolutePath,
                type = fileType,
                lastModified = currentTimestamp
            )
        } else {
            null
        }
    }

    /**
     * Record timestamp for a file (called after write operations)
     */
    fun recordTimestamp(filePath: String, timestamp: Long) {
        fileTimestamps[filePath] = timestamp
    }

    /**
     * Record timestamp for a file object
     */
    fun recordTimestamp(file: File) {
        if (file.exists()) {
            fileTimestamps[file.absolutePath] = file.lastModified()
        }
    }

    /**
     * Get cached timestamp for a file
     */
    fun getTimestamp(filePath: String): Long? {
        return fileTimestamps[filePath]
    }

    /**
     * Clear all cached timestamps
     */
    fun clearTimestamps() {
        fileTimestamps.clear()
    }

    /**
     * Clear timestamps for a specific profile
     */
    fun clearProfileTimestamps(profileName: String) {
        val profileFolder = File(profilesRoot, sanitizeFolderName(profileName))
        val profilePath = profileFolder.absolutePath

        fileTimestamps.keys.removeAll { path ->
            path.startsWith(profilePath)
        }
    }

    /**
     * Initialize timestamp tracking for a profile
     * Call this when app starts or profile is switched
     */
    suspend fun initializeTimestamps(profileName: String) = withContext(Dispatchers.IO) {
        try {
            // Record current state of all files
            checkForChanges(profileName)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Sanitize folder name to be filesystem safe
     */
    private fun sanitizeFolderName(name: String): String {
        return name.replace(Regex("[\\\\/:*?\"<>|]"), "_").trim()
    }
}
