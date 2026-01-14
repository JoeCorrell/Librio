package com.librio.data

import com.librio.LibrioApplication
import com.librio.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/**
 * Manages progress.json file for tracking playback/reading progress
 * Stores progress per profile in the profile folder
 */
class ProgressFileManager {

    private val librioRoot: File get() = LibrioApplication.getLibrioRoot()
    private val profilesRoot = File(librioRoot, "Profiles")

    /**
     * Get the progress.json file path for a profile
     */
    fun getProgressFile(profileName: String): File {
        val profileFolder = File(profilesRoot, sanitizeFolderName(profileName))
        if (!profileFolder.exists()) {
            profileFolder.mkdirs()
        }
        return File(profileFolder, "progress.json")
    }

    /**
     * Sanitize folder name to be filesystem safe
     */
    private fun sanitizeFolderName(name: String): String {
        return name.replace(Regex("[\\\\/:*?\"<>|]"), "_").trim()
    }

    /**
     * Data class representing progress entry in JSON
     */
    data class ProgressEntry(
        val id: String,
        val uri: String,
        val type: String, // AUDIOBOOK, EBOOK, MUSIC, COMICS, MOVIE
        val position: Long, // lastPosition for audio/video, currentPage for books/comics
        val total: Long, // duration for audio/video, totalPages for books/comics
        val lastUpdated: Long = System.currentTimeMillis()
    )

    /**
     * Save all progress to progress.json file
     */
    suspend fun saveProgress(
        profileName: String,
        audiobooks: List<LibraryAudiobook>,
        books: List<LibraryBook>,
        music: List<LibraryMusic>,
        comics: List<LibraryComic>,
        movies: List<LibraryMovie>
    ) = withContext(Dispatchers.IO) {
        try {
            val progressFile = getProgressFile(profileName)
            val jsonObject = JSONObject()

            // Metadata
            jsonObject.put("version", 1)
            jsonObject.put("lastUpdated", System.currentTimeMillis())
            jsonObject.put("profile", profileName)

            // Audiobooks progress
            val audiobooksArray = JSONArray()
            audiobooks.filter { it.lastPosition > 0 }.forEach { audiobook ->
                val entry = JSONObject().apply {
                    put("id", audiobook.id)
                    put("uri", audiobook.uri.toString())
                    put("title", audiobook.title)
                    put("position", audiobook.lastPosition)
                    put("duration", audiobook.duration)
                    put("progress", audiobook.progress)
                    put("isCompleted", audiobook.isCompleted)
                    put("lastPlayed", audiobook.lastPlayed)
                }
                audiobooksArray.put(entry)
            }
            jsonObject.put("audiobooks", audiobooksArray)

            // Books progress
            val booksArray = JSONArray()
            books.filter { it.currentPage > 0 }.forEach { book ->
                val entry = JSONObject().apply {
                    put("id", book.id)
                    put("uri", book.uri.toString())
                    put("title", book.title)
                    put("currentPage", book.currentPage)
                    put("totalPages", book.totalPages)
                    put("progress", book.progress)
                    put("isCompleted", book.isCompleted)
                    put("lastRead", book.lastRead)
                }
                booksArray.put(entry)
            }
            jsonObject.put("books", booksArray)

            // Music progress
            val musicArray = JSONArray()
            music.filter { it.lastPosition > 0 }.forEach { track ->
                val entry = JSONObject().apply {
                    put("id", track.id)
                    put("uri", track.uri.toString())
                    put("title", track.title)
                    put("position", track.lastPosition)
                    put("duration", track.duration)
                    put("progress", track.progress)
                    put("isCompleted", track.isCompleted)
                    put("lastPlayed", track.lastPlayed)
                    put("timesListened", track.timesListened)
                }
                musicArray.put(entry)
            }
            jsonObject.put("music", musicArray)

            // Comics progress
            val comicsArray = JSONArray()
            comics.filter { it.currentPage > 0 }.forEach { comic ->
                val entry = JSONObject().apply {
                    put("id", comic.id)
                    put("uri", comic.uri.toString())
                    put("title", comic.title)
                    put("currentPage", comic.currentPage)
                    put("totalPages", comic.totalPages)
                    put("progress", comic.progress)
                    put("isCompleted", comic.isCompleted)
                    put("lastRead", comic.lastRead)
                }
                comicsArray.put(entry)
            }
            jsonObject.put("comics", comicsArray)

            // Movies progress
            val moviesArray = JSONArray()
            movies.filter { it.lastPosition > 0 }.forEach { movie ->
                val entry = JSONObject().apply {
                    put("id", movie.id)
                    put("uri", movie.uri.toString())
                    put("title", movie.title)
                    put("position", movie.lastPosition)
                    put("duration", movie.duration)
                    put("progress", movie.progress)
                    put("isCompleted", movie.isCompleted)
                    put("lastPlayed", movie.lastPlayed)
                }
                moviesArray.put(entry)
            }
            jsonObject.put("movies", moviesArray)

            // Write to file with pretty printing
            progressFile.writeText(jsonObject.toString(2))
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Load progress from progress.json file
     * Returns a map of URI -> ProgressEntry
     */
    suspend fun loadProgress(profileName: String): Map<String, ProgressEntry> = withContext(Dispatchers.IO) {
        try {
            val progressFile = getProgressFile(profileName)
            if (!progressFile.exists()) return@withContext emptyMap()

            val jsonString = progressFile.readText()
            val jsonObject = JSONObject(jsonString)
            val progressMap = mutableMapOf<String, ProgressEntry>()

            // Load audiobooks
            if (jsonObject.has("audiobooks")) {
                val audiobooksArray = jsonObject.getJSONArray("audiobooks")
                for (i in 0 until audiobooksArray.length()) {
                    val entry = audiobooksArray.getJSONObject(i)
                    val uri = entry.getString("uri")
                    progressMap[uri] = ProgressEntry(
                        id = entry.getString("id"),
                        uri = uri,
                        type = "AUDIOBOOK",
                        position = entry.getLong("position"),
                        total = entry.optLong("duration", 0L),
                        lastUpdated = entry.optLong("lastPlayed", 0L)
                    )
                }
            }

            // Load books
            if (jsonObject.has("books")) {
                val booksArray = jsonObject.getJSONArray("books")
                for (i in 0 until booksArray.length()) {
                    val entry = booksArray.getJSONObject(i)
                    val uri = entry.getString("uri")
                    progressMap[uri] = ProgressEntry(
                        id = entry.getString("id"),
                        uri = uri,
                        type = "EBOOK",
                        position = entry.getInt("currentPage").toLong(),
                        total = entry.optInt("totalPages", 0).toLong(),
                        lastUpdated = entry.optLong("lastRead", 0L)
                    )
                }
            }

            // Load music
            if (jsonObject.has("music")) {
                val musicArray = jsonObject.getJSONArray("music")
                for (i in 0 until musicArray.length()) {
                    val entry = musicArray.getJSONObject(i)
                    val uri = entry.getString("uri")
                    progressMap[uri] = ProgressEntry(
                        id = entry.getString("id"),
                        uri = uri,
                        type = "MUSIC",
                        position = entry.getLong("position"),
                        total = entry.optLong("duration", 0L),
                        lastUpdated = entry.optLong("lastPlayed", 0L)
                    )
                }
            }

            // Load comics
            if (jsonObject.has("comics")) {
                val comicsArray = jsonObject.getJSONArray("comics")
                for (i in 0 until comicsArray.length()) {
                    val entry = comicsArray.getJSONObject(i)
                    val uri = entry.getString("uri")
                    progressMap[uri] = ProgressEntry(
                        id = entry.getString("id"),
                        uri = uri,
                        type = "COMICS",
                        position = entry.getInt("currentPage").toLong(),
                        total = entry.optInt("totalPages", 0).toLong(),
                        lastUpdated = entry.optLong("lastRead", 0L)
                    )
                }
            }

            // Load movies
            if (jsonObject.has("movies")) {
                val moviesArray = jsonObject.getJSONArray("movies")
                for (i in 0 until moviesArray.length()) {
                    val entry = moviesArray.getJSONObject(i)
                    val uri = entry.getString("uri")
                    progressMap[uri] = ProgressEntry(
                        id = entry.getString("id"),
                        uri = uri,
                        type = "MOVIE",
                        position = entry.getLong("position"),
                        total = entry.optLong("duration", 0L),
                        lastUpdated = entry.optLong("lastPlayed", 0L)
                    )
                }
            }

            progressMap
        } catch (e: Exception) {
            e.printStackTrace()
            emptyMap()
        }
    }

    /**
     * Update a single progress entry without rewriting the entire file
     * This is used for real-time progress updates
     */
    suspend fun updateSingleProgress(
        profileName: String,
        uri: String,
        type: String,
        position: Long,
        total: Long
    ) = withContext(Dispatchers.IO) {
        try {
            val progressFile = getProgressFile(profileName)
            val jsonObject = if (progressFile.exists()) {
                JSONObject(progressFile.readText())
            } else {
                JSONObject().apply {
                    put("version", 1)
                    put("profile", profileName)
                }
            }

            jsonObject.put("lastUpdated", System.currentTimeMillis())

            // Get or create the appropriate array
            val arrayKey = when (type) {
                "AUDIOBOOK" -> "audiobooks"
                "EBOOK" -> "books"
                "MUSIC" -> "music"
                "COMICS" -> "comics"
                "MOVIE" -> "movies"
                else -> return@withContext false
            }

            val array = if (jsonObject.has(arrayKey)) {
                jsonObject.getJSONArray(arrayKey)
            } else {
                JSONArray().also { jsonObject.put(arrayKey, it) }
            }

            // Find and update existing entry or add new one
            var found = false
            for (i in 0 until array.length()) {
                val entry = array.getJSONObject(i)
                if (entry.getString("uri") == uri) {
                    when (type) {
                        "AUDIOBOOK", "MUSIC", "MOVIE" -> {
                            entry.put("position", position)
                            entry.put("duration", total)
                            entry.put("lastPlayed", System.currentTimeMillis())
                            entry.put("progress", if (total > 0) position.toFloat() / total else 0f)
                        }
                        "EBOOK", "COMICS" -> {
                            entry.put("currentPage", position.toInt())
                            entry.put("totalPages", total.toInt())
                            entry.put("lastRead", System.currentTimeMillis())
                            entry.put("progress", if (total > 0) position.toFloat() / total else 0f)
                        }
                    }
                    found = true
                    break
                }
            }

            if (!found) {
                // Add new entry
                val newEntry = JSONObject().apply {
                    put("uri", uri)
                    when (type) {
                        "AUDIOBOOK", "MUSIC", "MOVIE" -> {
                            put("position", position)
                            put("duration", total)
                            put("lastPlayed", System.currentTimeMillis())
                            put("progress", if (total > 0) position.toFloat() / total else 0f)
                        }
                        "EBOOK", "COMICS" -> {
                            put("currentPage", position.toInt())
                            put("totalPages", total.toInt())
                            put("lastRead", System.currentTimeMillis())
                            put("progress", if (total > 0) position.toFloat() / total else 0f)
                        }
                    }
                }
                array.put(newEntry)
            }

            progressFile.writeText(jsonObject.toString(2))
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Check if progress file exists for a profile
     */
    fun hasProgressFile(profileName: String): Boolean {
        return getProgressFile(profileName).exists()
    }

    /**
     * Delete progress file for a profile
     */
    suspend fun deleteProgressFile(profileName: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val progressFile = getProgressFile(profileName)
            if (progressFile.exists()) {
                progressFile.delete()
            } else {
                true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
