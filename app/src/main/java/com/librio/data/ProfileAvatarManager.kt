package com.librio.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Environment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

/**
 * Manages profile avatar images by copying them into profile folders
 * Handles image optimization, format conversion, and cleanup
 */
class ProfileAvatarManager {

    private val librioRoot = File(Environment.getExternalStorageDirectory(), "Librio")
    private val profilesRoot = File(librioRoot, "Profiles")

    companion object {
        private const val MAX_AVATAR_SIZE = 512 // Max width/height in pixels
        private const val JPEG_QUALITY = 85 // JPEG compression quality
    }

    /**
     * Save avatar from URI to profile folder
     * Copies and optimizes image, returns filename (avatar.jpg or avatar.png)
     */
    suspend fun saveAvatar(profileName: String, sourceUri: Uri, context: Context): String? = withContext(Dispatchers.IO) {
        try {
            val profileFolder = File(profilesRoot, sanitizeFolderName(profileName))
            if (!profileFolder.exists()) {
                profileFolder.mkdirs()
            }

            // Delete any existing avatar files first
            deleteAvatar(profileName)

            // Load bitmap from URI
            val inputStream = context.contentResolver.openInputStream(sourceUri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (originalBitmap == null) {
                return@withContext null
            }

            // Handle EXIF rotation
            val rotatedBitmap = handleExifRotation(sourceUri, originalBitmap, context)

            // Resize if needed
            val resizedBitmap = resizeIfNeeded(rotatedBitmap)

            // Determine format and save
            val filename = "avatar.jpg" // Use JPEG for better compression
            val avatarFile = File(profileFolder, filename)

            FileOutputStream(avatarFile).use { out ->
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, out)
            }

            // Clean up bitmaps
            if (rotatedBitmap != originalBitmap) {
                originalBitmap.recycle()
            }
            resizedBitmap.recycle()

            filename
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Delete avatar file from profile folder
     */
    suspend fun deleteAvatar(profileName: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val profileFolder = File(profilesRoot, sanitizeFolderName(profileName))
            var deleted = false

            // Delete both possible avatar files
            listOf("avatar.jpg", "avatar.png", "avatar.jpeg").forEach { filename ->
                val avatarFile = File(profileFolder, filename)
                if (avatarFile.exists()) {
                    avatarFile.delete()
                    deleted = true
                }
            }

            deleted
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Get avatar file for a profile
     */
    fun getAvatarFile(profileName: String): File? {
        val profileFolder = File(profilesRoot, sanitizeFolderName(profileName))

        // Check for avatar files in order of preference
        listOf("avatar.jpg", "avatar.png", "avatar.jpeg").forEach { filename ->
            val avatarFile = File(profileFolder, filename)
            if (avatarFile.exists()) {
                return avatarFile
            }
        }

        return null
    }

    /**
     * Check if profile has an avatar
     */
    fun hasAvatar(profileName: String): Boolean {
        return getAvatarFile(profileName) != null
    }

    /**
     * Handle EXIF rotation for images
     */
    private fun handleExifRotation(uri: Uri, bitmap: Bitmap, context: Context): Bitmap {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val exif = inputStream?.let { ExifInterface(it) }
            inputStream?.close()

            val orientation = exif?.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            ) ?: ExifInterface.ORIENTATION_NORMAL

            val matrix = Matrix()
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
                ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1f, 1f)
                ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1f, -1f)
                else -> return bitmap
            }

            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        } catch (e: Exception) {
            e.printStackTrace()
            return bitmap
        }
    }

    /**
     * Resize bitmap if it exceeds maximum size
     */
    private fun resizeIfNeeded(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        if (width <= MAX_AVATAR_SIZE && height <= MAX_AVATAR_SIZE) {
            return bitmap
        }

        val scale = if (width > height) {
            MAX_AVATAR_SIZE.toFloat() / width
        } else {
            MAX_AVATAR_SIZE.toFloat() / height
        }

        val scaledWidth = (width * scale).toInt()
        val scaledHeight = (height * scale).toInt()

        return Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true)
    }

    /**
     * Sanitize folder name to be filesystem safe
     */
    private fun sanitizeFolderName(name: String): String {
        return name.replace(Regex("[\\\\/:*?\"<>|]"), "_").trim()
    }
}
