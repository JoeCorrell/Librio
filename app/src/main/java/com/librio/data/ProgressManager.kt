package com.librio.data

import android.content.Context
import android.content.SharedPreferences

/**
 * Bulletproof progress manager that saves playback progress immediately and synchronously.
 * Uses SharedPreferences.commit() for guaranteed synchronous disk writes.
 *
 * Design principles:
 * 1. IMMEDIATE saves using SharedPreferences.commit() (synchronous)
 * 2. SIMPLE direct logic with no coroutines or async operations
 * 3. library.json is the primary storage; this provides fast redundant backup
 */
class ProgressManager(private val context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var currentProfileName: String = "Default"

    companion object {
        private const val PREFS_NAME = "librio_progress"

        private const val KEY_AUDIOBOOK_PREFIX = "ab_pos_"
        private const val KEY_MUSIC_PREFIX = "mu_pos_"
        private const val KEY_MOVIE_PREFIX = "mv_pos_"
        private const val KEY_BOOK_PREFIX = "bk_page_"
        private const val KEY_COMIC_PREFIX = "cm_page_"

        private const val KEY_LAST_AUDIOBOOK_ID = "last_ab_id_"
        private const val KEY_LAST_AUDIOBOOK_POSITION = "last_ab_pos_"
        private const val KEY_LAST_AUDIOBOOK_PLAYING = "last_ab_play_"

        private const val KEY_LAST_MUSIC_ID = "last_mu_id_"
        private const val KEY_LAST_MUSIC_POSITION = "last_mu_pos_"
        private const val KEY_LAST_MUSIC_PLAYING = "last_mu_play_"

        private const val KEY_LAST_ACTIVE_TYPE = "last_type_"

        @Volatile
        private var instance: ProgressManager? = null

        fun getInstance(context: Context): ProgressManager {
            return instance ?: synchronized(this) {
                instance ?: ProgressManager(context.applicationContext).also { instance = it }
            }
        }
    }

    private fun getProfileKey(): String = currentProfileName.replace(Regex("[^a-zA-Z0-9]"), "_")

    // ==================== AUDIOBOOK PROGRESS ====================

    fun saveAudiobookProgress(audiobookId: String, position: Long, duration: Long) {
        if (position < 0 || audiobookId.isBlank()) return
        prefs.edit()
            .putLong(KEY_AUDIOBOOK_PREFIX + getProfileKey() + "_" + audiobookId, position)
            .commit()
    }

    fun getAudiobookProgress(audiobookId: String): Long {
        return prefs.getLong(KEY_AUDIOBOOK_PREFIX + getProfileKey() + "_" + audiobookId, 0L)
    }

    // ==================== MUSIC PROGRESS ====================

    fun saveMusicProgress(musicId: String, position: Long, duration: Long) {
        if (position < 0 || musicId.isBlank()) return
        prefs.edit()
            .putLong(KEY_MUSIC_PREFIX + getProfileKey() + "_" + musicId, position)
            .commit()
    }

    fun getMusicProgress(musicId: String): Long {
        return prefs.getLong(KEY_MUSIC_PREFIX + getProfileKey() + "_" + musicId, 0L)
    }

    // ==================== MOVIE PROGRESS ====================

    fun saveMovieProgress(movieId: String, position: Long, duration: Long) {
        if (position < 0 || movieId.isBlank()) return
        prefs.edit()
            .putLong(KEY_MOVIE_PREFIX + getProfileKey() + "_" + movieId, position)
            .commit()
    }

    fun getMovieProgress(movieId: String): Long {
        return prefs.getLong(KEY_MOVIE_PREFIX + getProfileKey() + "_" + movieId, 0L)
    }

    // ==================== BOOK PROGRESS ====================

    fun saveBookProgress(bookId: String, currentPage: Int, totalPages: Int) {
        if (currentPage < 0 || bookId.isBlank()) return
        prefs.edit()
            .putInt(KEY_BOOK_PREFIX + getProfileKey() + "_" + bookId, currentPage)
            .commit()
    }

    fun getBookProgress(bookId: String): Int {
        return prefs.getInt(KEY_BOOK_PREFIX + getProfileKey() + "_" + bookId, 0)
    }

    // ==================== COMIC PROGRESS ====================

    fun saveComicProgress(comicId: String, currentPage: Int, totalPages: Int) {
        if (currentPage < 0 || comicId.isBlank()) return
        prefs.edit()
            .putInt(KEY_COMIC_PREFIX + getProfileKey() + "_" + comicId, currentPage)
            .commit()
    }

    fun getComicProgress(comicId: String): Int {
        return prefs.getInt(KEY_COMIC_PREFIX + getProfileKey() + "_" + comicId, 0)
    }

    // ==================== LAST PLAYED STATE ====================

    fun saveLastAudiobookState(audiobookId: String?, position: Long, isPlaying: Boolean) {
        prefs.edit()
            .putString(KEY_LAST_AUDIOBOOK_ID + getProfileKey(), audiobookId)
            .putLong(KEY_LAST_AUDIOBOOK_POSITION + getProfileKey(), position)
            .putBoolean(KEY_LAST_AUDIOBOOK_PLAYING + getProfileKey(), isPlaying)
            .commit()
    }

    fun getLastAudiobookId(): String? = prefs.getString(KEY_LAST_AUDIOBOOK_ID + getProfileKey(), null)
    fun getLastAudiobookPosition(): Long = prefs.getLong(KEY_LAST_AUDIOBOOK_POSITION + getProfileKey(), 0L)
    fun getLastAudiobookPlaying(): Boolean = prefs.getBoolean(KEY_LAST_AUDIOBOOK_PLAYING + getProfileKey(), false)

    fun saveLastMusicState(musicId: String?, position: Long, isPlaying: Boolean) {
        prefs.edit()
            .putString(KEY_LAST_MUSIC_ID + getProfileKey(), musicId)
            .putLong(KEY_LAST_MUSIC_POSITION + getProfileKey(), position)
            .putBoolean(KEY_LAST_MUSIC_PLAYING + getProfileKey(), isPlaying)
            .commit()
    }

    fun getLastMusicId(): String? = prefs.getString(KEY_LAST_MUSIC_ID + getProfileKey(), null)
    fun getLastMusicPosition(): Long = prefs.getLong(KEY_LAST_MUSIC_POSITION + getProfileKey(), 0L)
    fun getLastMusicPlaying(): Boolean = prefs.getBoolean(KEY_LAST_MUSIC_PLAYING + getProfileKey(), false)

    fun saveLastActiveType(type: String?) {
        if (type != null) {
            prefs.edit().putString(KEY_LAST_ACTIVE_TYPE + getProfileKey(), type).commit()
        } else {
            prefs.edit().remove(KEY_LAST_ACTIVE_TYPE + getProfileKey()).commit()
        }
    }

    fun getLastActiveType(): String? = prefs.getString(KEY_LAST_ACTIVE_TYPE + getProfileKey(), null)

    /**
     * No-op - library.json is the primary storage, SharedPreferences is already committed
     */
    fun forceSaveToFile() {
        // SharedPreferences.commit() already ensures data is written to disk
        // library.json save is handled by LibraryViewModel.saveLibrary()
    }

    /**
     * Debug function to dump all saved progress data
     * Returns a map of all keys and their values for the current profile
     */
    fun debugDumpAllProgress(): Map<String, Any?> {
        val result = mutableMapOf<String, Any?>()
        val profileKey = getProfileKey()

        result["_currentProfileName"] = currentProfileName
        result["_profileKey"] = profileKey

        // Get all entries from SharedPreferences
        prefs.all.forEach { (key, value) ->
            // Only include entries for current profile
            if (key.contains(profileKey)) {
                result[key] = value
            }
        }

        return result
    }

    /**
     * Debug function to get total count of saved progress entries
     */
    fun debugGetProgressCount(): Int {
        val profileKey = getProfileKey()
        return prefs.all.count { (key, _) ->
            key.startsWith(KEY_AUDIOBOOK_PREFIX) && key.contains(profileKey)
        }
    }
}
