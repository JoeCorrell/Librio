package com.librio.data

import android.os.Environment
import com.librio.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/**
 * Manages playlist metadata JSON files in Profiles/{ProfileName}/Playlists/
 * Each playlist is stored as a separate JSON file: {PlaylistID}.json
 * Handles two-way sync between JSON files and folder structure
 */
class PlaylistMetadataManager {

    private val librioRoot = File(Environment.getExternalStorageDirectory(), "Librio")
    private val profilesRoot = File(librioRoot, "Profiles")

    /**
     * Get the Playlists folder for a profile
     */
    fun getPlaylistsFolder(profileName: String): File {
        val profileFolder = File(profilesRoot, sanitizeFolderName(profileName))
        val playlistsFolder = File(profileFolder, "Playlists")
        if (!playlistsFolder.exists()) {
            playlistsFolder.mkdirs()
        }
        return playlistsFolder
    }

    /**
     * Save playlist metadata to JSON file
     */
    suspend fun savePlaylist(profileName: String, playlist: PlaylistMetadata): Boolean = withContext(Dispatchers.IO) {
        try {
            val playlistsFolder = getPlaylistsFolder(profileName)
            val playlistFile = File(playlistsFolder, "${playlist.id}.json")

            val jsonObject = JSONObject().apply {
                put("version", playlist.version)
                put("id", playlist.id)
                put("name", playlist.name)
                put("contentType", playlist.contentType.name)
                playlist.categoryId?.let { put("categoryId", it) }
                put("order", playlist.order)
                put("dateCreated", playlist.dateCreated)
                put("lastModified", System.currentTimeMillis())
                put("folderPath", playlist.folderPath)

                // Items array
                val itemsArray = JSONArray()
                playlist.items.forEach { item ->
                    val itemObj = JSONObject().apply {
                        put("uri", item.uri)
                        put("title", item.title)
                        put("order", item.order)
                        put("dateAdded", item.dateAdded)
                    }
                    itemsArray.put(itemObj)
                }
                put("items", itemsArray)

                // Metadata
                val metadataObj = JSONObject().apply {
                    put("itemCount", playlist.metadata.itemCount)
                    put("totalDuration", playlist.metadata.totalDuration)
                    put("lastScanned", playlist.metadata.lastScanned)
                }
                put("metadata", metadataObj)
            }

            // Atomic write with backup
            saveWithBackup(playlistFile, jsonObject.toString(2))
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Load a single playlist from JSON file
     */
    suspend fun loadPlaylist(profileName: String, playlistId: String): PlaylistMetadata? = withContext(Dispatchers.IO) {
        try {
            val playlistsFolder = getPlaylistsFolder(profileName)
            val playlistFile = File(playlistsFolder, "$playlistId.json")

            if (!playlistFile.exists()) return@withContext null

            val jsonString = playlistFile.readText()
            val jsonObject = JSONObject(jsonString)

            // Parse items
            val items = mutableListOf<PlaylistItem>()
            if (jsonObject.has("items")) {
                val itemsArray = jsonObject.getJSONArray("items")
                for (i in 0 until itemsArray.length()) {
                    val itemObj = itemsArray.getJSONObject(i)
                    items.add(
                        PlaylistItem(
                            uri = itemObj.getString("uri"),
                            title = itemObj.getString("title"),
                            order = itemObj.optInt("order", i),
                            dateAdded = itemObj.optLong("dateAdded", System.currentTimeMillis())
                        )
                    )
                }
            }

            // Parse metadata
            val metadata = if (jsonObject.has("metadata")) {
                val metadataObj = jsonObject.getJSONObject("metadata")
                PlaylistStats(
                    itemCount = metadataObj.optInt("itemCount", items.size),
                    totalDuration = metadataObj.optLong("totalDuration", 0),
                    lastScanned = metadataObj.optLong("lastScanned", System.currentTimeMillis())
                )
            } else {
                PlaylistStats(itemCount = items.size)
            }

            PlaylistMetadata(
                version = jsonObject.optInt("version", 1),
                id = jsonObject.getString("id"),
                name = jsonObject.getString("name"),
                contentType = ContentType.valueOf(jsonObject.getString("contentType")),
                categoryId = jsonObject.optString("categoryId").takeIf { it.isNotEmpty() },
                order = jsonObject.optInt("order", 0),
                dateCreated = jsonObject.optLong("dateCreated", System.currentTimeMillis()),
                lastModified = jsonObject.optLong("lastModified", System.currentTimeMillis()),
                folderPath = jsonObject.getString("folderPath"),
                items = items,
                metadata = metadata
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Load all playlists for a profile
     */
    suspend fun loadAllPlaylists(profileName: String): List<PlaylistMetadata> = withContext(Dispatchers.IO) {
        try {
            val playlistsFolder = getPlaylistsFolder(profileName)
            if (!playlistsFolder.exists()) return@withContext emptyList()

            val playlists = mutableListOf<PlaylistMetadata>()
            playlistsFolder.listFiles { file -> file.extension == "json" }?.forEach { file ->
                val playlistId = file.nameWithoutExtension
                loadPlaylist(profileName, playlistId)?.let { playlists.add(it) }
            }

            playlists.sortedBy { it.order }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Delete a playlist JSON file
     */
    suspend fun deletePlaylist(profileName: String, playlistId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val playlistsFolder = getPlaylistsFolder(profileName)
            val playlistFile = File(playlistsFolder, "$playlistId.json")

            if (playlistFile.exists()) {
                playlistFile.delete()
            } else {
                true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Scan playlist folder for media files and return list of items
     */
    suspend fun scanPlaylistItems(
        profileName: String,
        contentType: ContentType,
        playlistFolderPath: String
    ): List<PlaylistItem> = withContext(Dispatchers.IO) {
        try {
            val profileFolder = File(profilesRoot, sanitizeFolderName(profileName))
            val playlistFolder = File(profileFolder, playlistFolderPath)

            if (!playlistFolder.exists()) return@withContext emptyList()

            val items = mutableListOf<PlaylistItem>()
            val supportedExtensions = getSupportedExtensions(contentType)

            playlistFolder.listFiles()?.filter { file ->
                file.isFile && file.extension.lowercase() in supportedExtensions
            }?.sortedBy { it.name }?.forEachIndexed { index, file ->
                items.add(
                    PlaylistItem(
                        uri = file.toURI().toString(),
                        title = file.nameWithoutExtension,
                        order = index,
                        dateAdded = file.lastModified()
                    )
                )
            }

            items
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Two-way sync between playlist JSON files and folder structure
     * Returns sync result with added, removed, and modified playlists
     */
    suspend fun syncPlaylistsWithFolders(
        profileName: String,
        existingPlaylists: List<PlaylistMetadata>,
        playlistFolderManager: PlaylistFolderManager
    ): SyncResult = withContext(Dispatchers.IO) {
        try {
            val added = mutableListOf<PlaylistMetadata>()
            val removed = mutableListOf<String>()
            val modified = mutableListOf<PlaylistMetadata>()

            // Map existing playlists by folder path
            val playlistsByPath = existingPlaylists.associateBy { it.folderPath }

            // Discover all playlist folders
            val discoveredPlaylists = ContentType.entries.flatMap { contentType ->
                playlistFolderManager.discoverPlaylistFolders(profileName, contentType)
            }

            // Check each discovered folder
            discoveredPlaylists.forEach { discovered ->
                val folderPath = "${discovered.contentType.name}/${discovered.folderName}"
                val existing = playlistsByPath[folderPath]

                if (existing == null) {
                    // New folder without JSON - create playlist
                    val items = scanPlaylistItems(profileName, discovered.contentType, folderPath)
                    val newPlaylist = PlaylistMetadata(
                        version = 1,
                        id = java.util.UUID.randomUUID().toString(),
                        name = discovered.folderName,
                        contentType = discovered.contentType,
                        categoryId = null,
                        order = added.size,
                        dateCreated = discovered.dateCreated,
                        lastModified = System.currentTimeMillis(),
                        folderPath = folderPath,
                        items = items,
                        metadata = PlaylistStats(
                            itemCount = items.size,
                            totalDuration = 0,
                            lastScanned = System.currentTimeMillis()
                        )
                    )
                    added.add(newPlaylist)
                } else {
                    // Folder exists - check if items changed
                    val currentItems = scanPlaylistItems(profileName, discovered.contentType, folderPath)
                    if (currentItems != existing.items) {
                        val updatedPlaylist = existing.copy(
                            items = currentItems,
                            lastModified = System.currentTimeMillis(),
                            metadata = existing.metadata.copy(
                                itemCount = currentItems.size,
                                lastScanned = System.currentTimeMillis()
                            )
                        )
                        modified.add(updatedPlaylist)
                    }
                }
            }

            // Check for playlists without folders (deleted folders)
            val discoveredPaths = discoveredPlaylists.map { "${it.contentType.name}/${it.folderName}" }.toSet()
            existingPlaylists.forEach { playlist ->
                if (playlist.folderPath !in discoveredPaths) {
                    removed.add(playlist.id)
                }
            }

            SyncResult(
                added = added,
                removed = removed,
                modified = modified
            )
        } catch (e: Exception) {
            e.printStackTrace()
            SyncResult()
        }
    }

    /**
     * Get supported file extensions for content type
     */
    private fun getSupportedExtensions(contentType: ContentType): Set<String> {
        return when (contentType) {
            ContentType.AUDIOBOOK, ContentType.MUSIC, ContentType.CREEPYPASTA -> setOf("mp3", "m4a", "m4b", "aac", "ogg", "opus", "flac", "wav", "webm", "mp4", "mkv", "m4v")
            ContentType.EBOOK -> setOf("epub", "pdf", "txt", "mobi", "azw", "azw3")
            ContentType.COMICS -> setOf("cbz", "cbr", "zip", "rar", "pdf")
            ContentType.MOVIE -> setOf("mp4", "mkv", "avi", "mov", "wmv", "flv", "webm", "m4v")
        }
    }

    /**
     * Sanitize folder name to be filesystem safe
     */
    private fun sanitizeFolderName(name: String): String {
        return name.replace(Regex("[\\\\/:*?\"<>|]"), "_").trim()
    }

    /**
     * Atomic write with backup for data integrity
     */
    private fun saveWithBackup(file: File, content: String): Boolean {
        try {
            val backupFile = File(file.parentFile, "${file.name}.backup")

            // 1. Write to backup file first
            backupFile.writeText(content)

            // 2. Verify backup is valid JSON
            try {
                JSONObject(content)
            } catch (e: Exception) {
                backupFile.delete()
                return false
            }

            // 3. If original exists, keep as .old
            if (file.exists()) {
                val oldBackup = File(file.parentFile, "${file.name}.old")
                file.renameTo(oldBackup)
            }

            // 4. Rename backup to actual file (atomic on most filesystems)
            backupFile.renameTo(file)

            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
}
