package com.librio.data.metadata

import android.content.Context
import android.net.Uri

/**
 * Common metadata that can be written to files
 */
data class FileMetadata(
    val title: String? = null,
    val author: String? = null,      // Author/Artist
    val narrator: String? = null,    // Composer field for audio, dc:contributor for EPUB
    val album: String? = null,
    val track: Int? = null,
    val genre: String? = null
)

/**
 * Result of metadata write operation
 */
sealed class MetadataWriteResult {
    object Success : MetadataWriteResult()
    data class Error(val message: String) : MetadataWriteResult()
    object UnsupportedFormat : MetadataWriteResult()
    object PermissionDenied : MetadataWriteResult()
}

/**
 * Interface for writing metadata to files
 */
interface MetadataWriter {
    /**
     * Check if this writer supports the given file type
     */
    fun supportsFormat(fileType: String): Boolean

    /**
     * Write metadata to the file
     * @param context Android context for content resolver access
     * @param uri URI of the file to modify
     * @param metadata Metadata to write
     * @return Result of the operation
     */
    suspend fun writeMetadata(
        context: Context,
        uri: Uri,
        metadata: FileMetadata
    ): MetadataWriteResult
}
