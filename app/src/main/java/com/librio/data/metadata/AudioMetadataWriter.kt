package com.librio.data.metadata

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import java.io.File
import java.io.FileOutputStream

/**
 * Writes metadata to audio files using JAudioTagger
 * Supports: MP3, M4A, M4B, OGG, FLAC, WAV, AIFF
 */
class AudioMetadataWriter : MetadataWriter {

    companion object {
        private const val TAG = "AudioMetadataWriter"
        private val SUPPORTED_FORMATS = setOf("mp3", "m4a", "m4b", "ogg", "flac", "wav", "aiff")
    }

    override fun supportsFormat(fileType: String): Boolean {
        return fileType.lowercase() in SUPPORTED_FORMATS
    }

    override suspend fun writeMetadata(
        context: Context,
        uri: Uri,
        metadata: FileMetadata
    ): MetadataWriteResult = withContext(Dispatchers.IO) {
        try {
            // Handle different URI schemes
            when (uri.scheme) {
                "file" -> writeToFileUri(uri, metadata)
                "content" -> writeToContentUri(context, uri, metadata)
                else -> MetadataWriteResult.Error("Unsupported URI scheme: ${uri.scheme}")
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "Permission denied for URI: $uri", e)
            MetadataWriteResult.PermissionDenied
        } catch (e: Exception) {
            Log.e(TAG, "Error writing metadata to $uri", e)
            MetadataWriteResult.Error(e.message ?: "Unknown error")
        }
    }

    /**
     * Write metadata to a file:// URI (direct file access)
     */
    private fun writeToFileUri(uri: Uri, metadata: FileMetadata): MetadataWriteResult {
        val file = File(uri.path ?: return MetadataWriteResult.Error("Invalid file path"))
        if (!file.exists()) {
            return MetadataWriteResult.Error("File does not exist")
        }
        if (!file.canWrite()) {
            return MetadataWriteResult.PermissionDenied
        }

        return writeToFile(file, metadata)
    }

    /**
     * Write metadata to a content:// URI (SAF)
     * This requires copying the file, modifying it, and writing it back
     */
    private fun writeToContentUri(
        context: Context,
        uri: Uri,
        metadata: FileMetadata
    ): MetadataWriteResult {
        val docFile = DocumentFile.fromSingleUri(context, uri)
        if (docFile == null || !docFile.exists()) {
            return MetadataWriteResult.Error("File does not exist")
        }
        if (!docFile.canWrite()) {
            return MetadataWriteResult.PermissionDenied
        }

        // Determine file extension
        val fileName = docFile.name ?: "audio"
        val extension = fileName.substringAfterLast('.', "mp3")

        // Create temp file
        val tempFile = File.createTempFile("metadata_edit_", ".$extension", context.cacheDir)
        try {
            // Copy content to temp file
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(tempFile).use { output ->
                    input.copyTo(output)
                }
            } ?: return MetadataWriteResult.Error("Cannot open file for reading")

            // Write metadata to temp file
            val result = writeToFile(tempFile, metadata)
            if (result != MetadataWriteResult.Success) {
                return result
            }

            // Write temp file back to original location
            context.contentResolver.openOutputStream(uri, "wt")?.use { output ->
                tempFile.inputStream().use { input ->
                    input.copyTo(output)
                }
            } ?: return MetadataWriteResult.Error("Cannot open file for writing")

            return MetadataWriteResult.Success
        } finally {
            // Clean up temp file
            tempFile.delete()
        }
    }

    /**
     * Write metadata to a File using JAudioTagger
     */
    private fun writeToFile(file: File, metadata: FileMetadata): MetadataWriteResult {
        return try {
            val audioFile = AudioFileIO.read(file)
            val tag = audioFile.tagOrCreateAndSetDefault

            // Write metadata fields if provided
            metadata.title?.let {
                tag.setField(FieldKey.TITLE, it)
            }
            metadata.author?.let {
                tag.setField(FieldKey.ARTIST, it)
            }
            metadata.album?.let {
                tag.setField(FieldKey.ALBUM, it)
            }
            metadata.track?.let {
                tag.setField(FieldKey.TRACK, it.toString())
            }
            metadata.narrator?.let {
                // Use COMPOSER for narrator (common convention for audiobooks)
                tag.setField(FieldKey.COMPOSER, it)
            }
            metadata.genre?.let {
                tag.setField(FieldKey.GENRE, it)
            }

            // Save the file
            audioFile.commit()

            Log.d(TAG, "Successfully wrote metadata to ${file.name}")
            MetadataWriteResult.Success
        } catch (e: Exception) {
            Log.e(TAG, "JAudioTagger error for ${file.name}", e)
            MetadataWriteResult.Error(e.message ?: "JAudioTagger error")
        }
    }
}
