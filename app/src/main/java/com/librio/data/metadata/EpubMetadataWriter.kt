package com.librio.data.metadata

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.zip.CRC32
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

/**
 * Writes metadata to EPUB files by modifying the OPF file inside the ZIP
 * EPUB is a ZIP archive containing:
 * - mimetype (first entry, uncompressed)
 * - META-INF/container.xml (points to OPF file)
 * - content.opf or similar (contains metadata)
 */
class EpubMetadataWriter : MetadataWriter {

    companion object {
        private const val TAG = "EpubMetadataWriter"
        private const val DC_NS = "http://purl.org/dc/elements/1.1/"
    }

    override fun supportsFormat(fileType: String): Boolean {
        return fileType.lowercase() == "epub"
    }

    override suspend fun writeMetadata(
        context: Context,
        uri: Uri,
        metadata: FileMetadata
    ): MetadataWriteResult = withContext(Dispatchers.IO) {
        try {
            when (uri.scheme) {
                "file" -> writeToFileUri(uri, metadata)
                "content" -> writeToContentUri(context, uri, metadata)
                else -> MetadataWriteResult.Error("Unsupported URI scheme: ${uri.scheme}")
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "Permission denied for URI: $uri", e)
            MetadataWriteResult.PermissionDenied
        } catch (e: Exception) {
            Log.e(TAG, "Error writing EPUB metadata to $uri", e)
            MetadataWriteResult.Error(e.message ?: "Unknown error")
        }
    }

    private fun writeToFileUri(uri: Uri, metadata: FileMetadata): MetadataWriteResult {
        val file = File(uri.path ?: return MetadataWriteResult.Error("Invalid file path"))
        if (!file.exists()) {
            return MetadataWriteResult.Error("File does not exist")
        }
        if (!file.canWrite()) {
            return MetadataWriteResult.PermissionDenied
        }

        return modifyEpub(FileInputStream(file), file, metadata)
    }

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

        // Create temp files for input and output
        val tempInput = File.createTempFile("epub_in_", ".epub", context.cacheDir)
        val tempOutput = File.createTempFile("epub_out_", ".epub", context.cacheDir)

        try {
            // Copy content URI to temp file
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(tempInput).use { output ->
                    input.copyTo(output)
                }
            } ?: return MetadataWriteResult.Error("Cannot open file for reading")

            // Modify the EPUB
            val result = modifyEpub(FileInputStream(tempInput), tempOutput, metadata)
            if (result != MetadataWriteResult.Success) {
                return result
            }

            // Write modified EPUB back to content URI
            context.contentResolver.openOutputStream(uri, "wt")?.use { output ->
                FileInputStream(tempOutput).use { input ->
                    input.copyTo(output)
                }
            } ?: return MetadataWriteResult.Error("Cannot open file for writing")

            return MetadataWriteResult.Success
        } finally {
            tempInput.delete()
            tempOutput.delete()
        }
    }

    /**
     * Modify an EPUB file's metadata
     * @param inputStream Input stream of the original EPUB
     * @param outputFile Output file to write the modified EPUB
     */
    private fun modifyEpub(
        inputStream: InputStream,
        outputFile: File,
        metadata: FileMetadata
    ): MetadataWriteResult {
        // Extract EPUB to temp directory
        val tempDir = File(outputFile.parentFile, "epub_temp_${System.currentTimeMillis()}")
        tempDir.mkdirs()

        try {
            // Extract all files
            var opfPath: String? = null
            ZipInputStream(inputStream).use { zip ->
                var entry = zip.nextEntry
                while (entry != null) {
                    val file = File(tempDir, entry.name)
                    if (entry.isDirectory) {
                        file.mkdirs()
                    } else {
                        file.parentFile?.mkdirs()
                        FileOutputStream(file).use { out ->
                            zip.copyTo(out)
                        }
                        // Find OPF file
                        if (entry.name.endsWith(".opf")) {
                            opfPath = entry.name
                        }
                    }
                    entry = zip.nextEntry
                }
            }

            if (opfPath == null) {
                // Try to find OPF from container.xml
                opfPath = findOpfFromContainer(tempDir)
            }

            if (opfPath == null) {
                return MetadataWriteResult.Error("Could not find OPF file in EPUB")
            }

            // Modify OPF metadata
            val opfFile = File(tempDir, opfPath)
            if (!opfFile.exists()) {
                return MetadataWriteResult.Error("OPF file not found: $opfPath")
            }

            modifyOpfMetadata(opfFile, metadata)

            // Repack EPUB
            repackEpub(tempDir, outputFile)

            Log.d(TAG, "Successfully wrote EPUB metadata")
            return MetadataWriteResult.Success
        } finally {
            // Clean up temp directory
            tempDir.deleteRecursively()
        }
    }

    /**
     * Find OPF file path from META-INF/container.xml
     */
    private fun findOpfFromContainer(tempDir: File): String? {
        val containerFile = File(tempDir, "META-INF/container.xml")
        if (!containerFile.exists()) return null

        return try {
            val factory = DocumentBuilderFactory.newInstance()
            val builder = factory.newDocumentBuilder()
            val doc = builder.parse(containerFile)

            val rootfiles = doc.getElementsByTagName("rootfile")
            if (rootfiles.length > 0) {
                val rootfile = rootfiles.item(0) as Element
                rootfile.getAttribute("full-path")
            } else null
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing container.xml", e)
            null
        }
    }

    /**
     * Modify the OPF file's Dublin Core metadata
     */
    private fun modifyOpfMetadata(opfFile: File, metadata: FileMetadata) {
        val factory = DocumentBuilderFactory.newInstance()
        factory.isNamespaceAware = true
        val builder = factory.newDocumentBuilder()
        val doc = builder.parse(opfFile)

        // Find metadata element
        val metadataElement = doc.getElementsByTagName("metadata").item(0) as? Element
            ?: doc.getElementsByTagNameNS("*", "metadata").item(0) as? Element
            ?: return

        // Update metadata fields
        metadata.title?.let {
            updateOrCreateDcElement(doc, metadataElement, "title", it)
        }
        metadata.author?.let {
            updateOrCreateDcElement(doc, metadataElement, "creator", it)
        }
        metadata.narrator?.let {
            updateOrCreateDcElement(doc, metadataElement, "contributor", it)
        }

        // Save modified OPF
        val transformer = TransformerFactory.newInstance().newTransformer()
        transformer.setOutputProperty(OutputKeys.INDENT, "yes")
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8")
        transformer.transform(DOMSource(doc), StreamResult(opfFile))
    }

    /**
     * Update or create a Dublin Core element
     */
    private fun updateOrCreateDcElement(
        doc: Document,
        parent: Element,
        localName: String,
        value: String
    ) {
        // Try to find existing element
        val existing = parent.getElementsByTagNameNS(DC_NS, localName).item(0)
            ?: parent.getElementsByTagName("dc:$localName").item(0)

        if (existing != null) {
            existing.textContent = value
        } else {
            // Create new element
            val newElement = doc.createElementNS(DC_NS, "dc:$localName")
            newElement.textContent = value
            parent.appendChild(newElement)
        }
    }

    /**
     * Repack the EPUB from the temp directory
     * EPUB requires mimetype to be first and uncompressed
     */
    private fun repackEpub(sourceDir: File, outputFile: File) {
        ZipOutputStream(FileOutputStream(outputFile)).use { zip ->
            // Write mimetype first, uncompressed (EPUB spec requirement)
            val mimetypeFile = File(sourceDir, "mimetype")
            if (mimetypeFile.exists()) {
                val entry = ZipEntry("mimetype")
                entry.method = ZipEntry.STORED
                entry.size = mimetypeFile.length()
                entry.compressedSize = mimetypeFile.length()
                entry.crc = calculateCrc(mimetypeFile)
                zip.putNextEntry(entry)
                mimetypeFile.inputStream().use { it.copyTo(zip) }
                zip.closeEntry()
            }

            // Write all other files compressed
            sourceDir.walkTopDown().forEach { file ->
                if (file.isFile && file.name != "mimetype") {
                    val relativePath = file.relativeTo(sourceDir).path.replace("\\", "/")
                    val entry = ZipEntry(relativePath)
                    zip.putNextEntry(entry)
                    file.inputStream().use { it.copyTo(zip) }
                    zip.closeEntry()
                }
            }
        }
    }

    /**
     * Calculate CRC32 for a file (required for STORED zip entries)
     */
    private fun calculateCrc(file: File): Long {
        val crc = CRC32()
        file.inputStream().use { input ->
            val buffer = ByteArray(8192)
            var read: Int
            while (input.read(buffer).also { read = it } != -1) {
                crc.update(buffer, 0, read)
            }
        }
        return crc.value
    }
}
