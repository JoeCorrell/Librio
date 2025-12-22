package com.librio.data

import android.os.Environment
import com.librio.model.ProfileEntry
import com.librio.model.ProfileRegistry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/**
 * Manages the master profiles.json file at /Librio/profiles.json
 * Provides centralized profile registry with list of all profiles and active profile tracking
 */
class MasterProfileManager {

    private val librioRoot = File(Environment.getExternalStorageDirectory(), "Librio")
    private val profilesFile = File(librioRoot, "profiles.json")

    /**
     * Load profile registry from profiles.json
     * Creates default registry if file doesn't exist
     */
    suspend fun loadProfiles(): ProfileRegistry = withContext(Dispatchers.IO) {
        try {
            if (!profilesFile.exists()) {
                // Return default registry with single default profile
                return@withContext ProfileRegistry(
                    version = 1,
                    lastModified = System.currentTimeMillis(),
                    activeProfileId = "default",
                    profiles = listOf(
                        ProfileEntry(
                            id = "default",
                            name = "Default",
                            folderName = "Default",
                            avatarFile = null,
                            dateCreated = System.currentTimeMillis(),
                            lastAccessed = System.currentTimeMillis()
                        )
                    )
                )
            }

            val jsonString = profilesFile.readText()
            val jsonObject = JSONObject(jsonString)

            val profiles = mutableListOf<ProfileEntry>()
            val profilesArray = jsonObject.getJSONArray("profiles")
            for (i in 0 until profilesArray.length()) {
                val profileObj = profilesArray.getJSONObject(i)
                profiles.add(
                    ProfileEntry(
                        id = profileObj.getString("id"),
                        name = profileObj.getString("name"),
                        folderName = profileObj.getString("folderName"),
                        avatarFile = profileObj.optString("avatarFile").takeIf { it.isNotEmpty() },
                        dateCreated = profileObj.optLong("dateCreated", System.currentTimeMillis()),
                        lastAccessed = profileObj.optLong("lastAccessed", System.currentTimeMillis())
                    )
                )
            }

            ProfileRegistry(
                version = jsonObject.optInt("version", 1),
                lastModified = jsonObject.optLong("lastModified", System.currentTimeMillis()),
                activeProfileId = jsonObject.getString("activeProfileId"),
                profiles = profiles
            )
        } catch (e: Exception) {
            e.printStackTrace()
            // Return default on error
            ProfileRegistry(
                version = 1,
                lastModified = System.currentTimeMillis(),
                activeProfileId = "default",
                profiles = listOf(
                    ProfileEntry(
                        id = "default",
                        name = "Default",
                        folderName = "Default",
                        avatarFile = null,
                        dateCreated = System.currentTimeMillis(),
                        lastAccessed = System.currentTimeMillis()
                    )
                )
            )
        }
    }

    /**
     * Save profile registry to profiles.json with atomic write
     */
    suspend fun saveProfiles(registry: ProfileRegistry): Boolean = withContext(Dispatchers.IO) {
        try {
            // Ensure Librio root exists
            if (!librioRoot.exists()) {
                librioRoot.mkdirs()
            }

            val jsonObject = JSONObject().apply {
                put("version", registry.version)
                put("lastModified", System.currentTimeMillis())
                put("activeProfileId", registry.activeProfileId)

                val profilesArray = JSONArray()
                registry.profiles.forEach { profile ->
                    val profileObj = JSONObject().apply {
                        put("id", profile.id)
                        put("name", profile.name)
                        put("folderName", profile.folderName)
                        profile.avatarFile?.let { put("avatarFile", it) }
                        put("dateCreated", profile.dateCreated)
                        put("lastAccessed", profile.lastAccessed)
                    }
                    profilesArray.put(profileObj)
                }
                put("profiles", profilesArray)
            }

            // Atomic write with backup
            saveWithBackup(profilesFile, jsonObject.toString(2))
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Add a new profile to the registry
     */
    suspend fun addProfile(profile: ProfileEntry): Boolean = withContext(Dispatchers.IO) {
        try {
            val registry = loadProfiles()
            val updatedProfiles = registry.profiles.toMutableList()
            updatedProfiles.add(profile)

            saveProfiles(
                registry.copy(
                    profiles = updatedProfiles,
                    lastModified = System.currentTimeMillis()
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Remove a profile from the registry
     */
    suspend fun removeProfile(profileId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val registry = loadProfiles()
            val updatedProfiles = registry.profiles.filter { it.id != profileId }

            // If removing active profile, switch to first remaining profile
            val newActiveId = if (registry.activeProfileId == profileId && updatedProfiles.isNotEmpty()) {
                updatedProfiles.first().id
            } else {
                registry.activeProfileId
            }

            saveProfiles(
                registry.copy(
                    profiles = updatedProfiles,
                    activeProfileId = newActiveId,
                    lastModified = System.currentTimeMillis()
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Set the active profile
     */
    suspend fun setActiveProfile(profileId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val registry = loadProfiles()

            // Update last accessed time for the profile being activated
            val updatedProfiles = registry.profiles.map { profile ->
                if (profile.id == profileId) {
                    profile.copy(lastAccessed = System.currentTimeMillis())
                } else {
                    profile
                }
            }

            saveProfiles(
                registry.copy(
                    profiles = updatedProfiles,
                    activeProfileId = profileId,
                    lastModified = System.currentTimeMillis()
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Update a profile's information
     */
    suspend fun updateProfile(updatedProfile: ProfileEntry): Boolean = withContext(Dispatchers.IO) {
        try {
            val registry = loadProfiles()
            val updatedProfiles = registry.profiles.map { profile ->
                if (profile.id == updatedProfile.id) updatedProfile else profile
            }

            saveProfiles(
                registry.copy(
                    profiles = updatedProfiles,
                    lastModified = System.currentTimeMillis()
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Get the currently active profile
     */
    suspend fun getActiveProfile(): ProfileEntry? = withContext(Dispatchers.IO) {
        try {
            val registry = loadProfiles()
            registry.profiles.find { it.id == registry.activeProfileId }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Get the profiles.json file
     */
    fun getProfilesFile(): File = profilesFile

    /**
     * Check if profiles.json exists
     */
    fun hasProfilesFile(): Boolean = profilesFile.exists()

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
