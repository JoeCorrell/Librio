package com.librio.data.metadata

/**
 * Factory for getting the appropriate MetadataWriter for a file type
 */
object MetadataWriterFactory {

    private val writers: List<MetadataWriter> = listOf(
        AudioMetadataWriter(),
        EpubMetadataWriter()
    )

    /**
     * Get the appropriate writer for the given file type
     * @param fileType The file extension (e.g., "mp3", "epub")
     * @return MetadataWriter if supported, null otherwise
     */
    fun getWriter(fileType: String): MetadataWriter? {
        return writers.find { it.supportsFormat(fileType) }
    }

    /**
     * Check if a file type has a writer available
     * @param fileType The file extension (e.g., "mp3", "epub")
     * @return true if a writer is available
     */
    fun hasWriter(fileType: String): Boolean {
        return writers.any { it.supportsFormat(fileType) }
    }

    /**
     * Check if meaningful metadata can actually be written for this file type
     * Some formats may have writers but with limitations
     */
    fun canWriteMetadata(fileType: String): Boolean {
        return fileType.lowercase() in WRITABLE_FORMATS
    }

    /**
     * Get a list of all supported formats
     */
    fun getSupportedFormats(): Set<String> {
        return WRITABLE_FORMATS
    }

    private val WRITABLE_FORMATS = setOf(
        // Audio formats
        "mp3",
        "m4a",
        "m4b",
        "ogg",
        "flac",
        "wav",
        "aiff",
        // Ebook formats
        "epub"
    )

    /**
     * Formats that are NOT supported for metadata writing
     * PDF requires Apache PDFBox which adds ~8MB to APK size
     */
    val UNSUPPORTED_FORMATS = setOf(
        "pdf",
        "txt",
        "mobi",
        "azw",
        "azw3",
        "cbz",
        "cbr",
        "cb7"
    )
}
