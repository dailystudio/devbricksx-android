package com.dailystudio.devbricksx.utils

import android.content.Context
import android.content.res.Resources.NotFoundException
import android.text.TextUtils
import com.dailystudio.devbricksx.development.Logger
import org.mozilla.universalchardet.UniversalDetector
import java.io.*
import java.math.BigInteger
import java.nio.charset.Charset
import java.security.MessageDigest
import java.util.*

/**
 * Utility class for file operations.
 *
 * Includes methods for directory creation, file encoding detection, content reading/writing,
 * hashing, and asset/raw resource copying.
 */
object FileUtils {

    private const val NO_MEDIA_TAG_FILE = ".nomedia"

    /**
     * Checks if a directory exists, creating it if necessary, and ensures a `.nomedia` file exists within it.
     *
     * @param directory The path to the directory.
     * @return True if successful, false otherwise.
     */
    fun checkOrCreateNoMediaDirectory(directory: String): Boolean {
        return checkOrCreateNoMediaDirectory(File(directory))
    }

    /**
     * Checks if a directory exists, creating it if necessary, and ensures a `.nomedia` file exists within it.
     *
     * @param directory The directory file.
     * @return True if successful, false otherwise.
     */
    fun checkOrCreateNoMediaDirectory(directory: File): Boolean {
        return checkOrCreateDirectory(directory, true)
    }

    /**
     * Checks if a directory exists, creating it if necessary.
     *
     * @param directory The path to the directory.
     * @return True if successful, false otherwise.
     */
    fun checkOrCreateDirectory(directory: String): Boolean {
        return checkOrCreateDirectory(File(directory))
    }

    /**
     * Checks if a directory exists, creating it if necessary.
     *
     * @param directory The directory file.
     * @return True if successful, false otherwise.
     */
    fun checkOrCreateDirectory(directory: File): Boolean {
        return checkOrCreateDirectory(directory, false)
    }

    /**
     * Checks if a directory exists, creating it if necessary.
     *
     * @param directory The directory file.
     * @param nomedia Whether to create a `.nomedia` file in the directory.
     * @return True if successful, false otherwise.
     */
    fun checkOrCreateDirectory(directory: File, nomedia: Boolean): Boolean {
        if (directory.exists()) {
            if (directory.isDirectory) {
                return true
            } else {
                Logger.warn("%s is NOT a directory", directory)
            }
        }

        val success = directory.mkdirs()
        if (!success) {
            return false
        }

        return if (!nomedia) {
            success
        } else checkOrCreateNoMediaTagInDirectory(directory)
    }

    /**
     * Checks for the existence of a `.nomedia` file in a directory, creating it if missing.
     *
     * @param directory The path to the directory.
     * @return True if successful, false otherwise.
     */
    fun checkOrCreateNoMediaTagInDirectory(directory: String): Boolean {
        return checkOrCreateNoMediaTagInDirectory(File(directory))
    }

    /**
     * Checks for the existence of a `.nomedia` file in a directory, creating it if missing.
     *
     * @param dir The directory file.
     * @return True if successful, false otherwise.
     */
    fun checkOrCreateNoMediaTagInDirectory(dir: File): Boolean {
        val tagFile = File(dir, NO_MEDIA_TAG_FILE)
        if (tagFile.exists()) {
            return true
        }

        return try {
            tagFile.createNewFile()
        } catch (e: IOException) {
            Logger.warn("could not create tag[%s] in dir[%s]: %s",
                    NO_MEDIA_TAG_FILE,
                    dir.absoluteFile,
                    e.toString())
            false
        }
    }

    /**
     * Checks if a file exists.
     *
     * @param filename The path to the file.
     * @return True if exists, false otherwise.
     */
    fun isFileExisted(filename: String): Boolean {
        return File(filename).exists()
    }

    /**
     * Detects the character encoding of a file.
     *
     * @param file The path to the file.
     * @return The detected encoding, or null if detection failed.
     */
    fun detectFileEncoding(file: String): String? {
        return detectFileEncoding(File(file))
    }

    /**
     * Detects the character encoding of a raw resource.
     *
     * @param context The context.
     * @param rawId The resource ID.
     * @return The detected encoding, or null if detection failed.
     */
    fun detectFileEncoding(context: Context, rawId: Int): String? {
        if (rawId <= 0) {
            Logger.warn("invalid resource id: $rawId")
            return null
        }

        val res = context.resources ?: return null

        val fStream = try {
            res.openRawResource(rawId)
        } catch (e: NotFoundException) {
            Logger.warn("failed to find raw file [$rawId]: $e")

            return null
        }

        return detectFileEncoding(fStream)
    }

    /**
     * Detects the character encoding of an asset file.
     *
     * @param context The context.
     * @param file The asset file path.
     * @return The detected encoding, or null if detection failed.
     */
    fun detectFileEncoding(context: Context, file: String): String? {
        val assetManager = context.assets ?: return null

        val fStream = try {
            assetManager.open(file) ?: return null
        } catch (e: IOException) {
            Logger.warn("failed to find asset file [$file]: $e")

            return null
        }

        return detectFileEncoding(fStream)
    }

    /**
     * Detects the character encoding of a file.
     *
     * @param file The file.
     * @return The detected encoding, or null if detection failed.
     */
    fun detectFileEncoding(file: File): String? {
        var fs: FileInputStream = try {
            FileInputStream(file)
        } catch (e: FileNotFoundException) {
            Logger.warn("get encoding failure: $e")
            return null
        }

        return detectFileEncoding(fs)
    }

    /**
     * Detects the character encoding of an input stream.
     *
     * @param iStream The input stream.
     * @return The detected encoding, or null if detection failed.
     */
    fun detectFileEncoding(iStream: InputStream): String? {
        val buf = ByteArray(4096)

        val detector = UniversalDetector(null)

        var nRead: Int
        try {
            while (iStream.read(buf).also { nRead = it } > 0 && !detector.isDone()) {
                detector.handleData(buf, 0, nRead)
            }
        } catch (e: IOException) {
            Logger.warn("get encoding failure: %s", e.toString())
        }

        detector.dataEnd()

        val encoding: String = detector.detectedCharset
        detector.reset()
        try {
            iStream.close()
        } catch (e: IOException) {
            Logger.warn("close stream failure: %s", e.toString())
        }

        return encoding
    }

    /**
     * Reads a file content into a String.
     *
     * @param file The path to the file.
     * @return The file content, or null if reading failed.
     */
    fun fileToString(file: String): String? {
        var fs: FileInputStream = try {
            FileInputStream(file)
        } catch (e: FileNotFoundException) {
            Logger.warn("get encoding failure: $e")
            return null
        }

        return fileToString(fs, detectFileEncoding(file))
    }

    /**
     * Reads an input stream content into a String.
     *
     * @param fStream The input stream.
     * @param encoding The encoding to use.
     * @return The content, or null if reading failed.
     */
    fun fileToString(fStream: InputStream, encoding: String?): String? {
        var reader: InputStreamReader? = null
        reader = encoding?.let {
            InputStreamReader(fStream, it)
        } ?: InputStreamReader(fStream)

        val writer = StringWriter()
        val buffer = CharArray(2048)
        var n = 0
        while (reader.read(buffer).also { n = it } != -1) {
            writer.write(buffer, 0, n)
        }

        writer.flush()

        fStream.close()

        return writer.toString()
    }

    /**
     * Reads an asset file content into a String.
     *
     * @param context The context.
     * @param file The asset file path.
     * @return The content, or null if reading failed.
     */
    fun assetToString(context: Context, file: String): String? {
        val assetManager = context.assets ?: return null

        val fStream = try {
            assetManager.open(file) ?: return null
        } catch (e: IOException) {
            Logger.warn("failed to find asset file [$file]: $e")

            return null
        }

        return fileToString(fStream, detectFileEncoding(context, file))
    }

    /**
     * Reads a raw resource content into a String.
     *
     * @param context The context.
     * @param rawId The resource ID.
     * @return The content, or null if reading failed.
     */
    fun rawToString(context: Context, rawId: Int): String? {
        if (rawId <= 0) {
            Logger.warn("invalid resource id: $rawId")
            return null
        }

        val res = context.resources ?: return null

        val fStream = try {
            res.openRawResource(rawId)
        } catch (e: NotFoundException) {
            Logger.warn("failed to find raw file [$rawId]: $e")

            return null
        }

        return fileToString(fStream,
                detectFileEncoding(context, rawId))
    }

    /**
     * Gets the base name of a file (with extension).
     *
     * @param filename The path to the file.
     * @return The base name.
     */
    fun getBaseName(filename: String): String {
        return File(filename).name
    }

    /**
     * Gets the file name without extension.
     *
     * @param filename The path to the file.
     * @return The file name without extension.
     */
    fun getFileName(filename: String): String {
        val baseName = getBaseName(filename)
        if (baseName.isNotEmpty()) {
            val i = baseName.lastIndexOf('.')
            if (i > -1 && i < baseName.length - 1) {
                return baseName.substring(0, i)
            }
        }
        return baseName
    }

    /**
     * Gets the file extension.
     *
     * @param filename The path to the file.
     * @return The file extension, or empty string if not found.
     */
    fun getFileExtension(filename: String): String {
        return getFileExtension(filename, "")
    }

    /**
     * Gets the file extension with a default value.
     *
     * @param filename The path to the file.
     * @param defExt The default extension if not found.
     * @return The file extension, or [defExt] if not found.
     */
    fun getFileExtension(filename: String, defExt: String): String {
        if (filename.isNotEmpty()) {
            val i = filename.lastIndexOf('.')
            if (i > -1 && i < filename.length - 1) {
                return filename.substring(i + 1)
            }
        }

        return defExt
    }

    /**
     * Writes a string to a file.
     *
     * @param file The path to the file.
     * @param fileContent The content to write.
     */
    fun stringToFile(file: String,
                     fileContent: String?) {
        stringToFile(file, fileContent, false)
    }

    /**
     * Writes a string to a file.
     *
     * @param file The path to the file.
     * @param fileContent The content to write.
     * @param append Whether to append to the file.
     */
    fun stringToFile(file: String, fileContent: String?, append: Boolean) {
        if (TextUtils.isEmpty(fileContent)) {
            return
        }

        val reader = StringReader(fileContent)
        val outputStream = FileWriter(file, append)

        try {
            val buffer = CharArray(2048)
            var n = 0
            while (reader.read(buffer).also { n = it } != -1) {
                outputStream.write(buffer, 0, n)
            }

            outputStream.flush()
            outputStream.close()

            reader.close()
        } catch (e: IOException) {
            Logger.error("write string [$fileContent] to file [$file] failed: $e")
        } finally {
            outputStream.close()
            reader.close()
        }
    }

    /**
     * Saves a byte array to a file.
     *
     * @param bytes The byte array.
     * @param filename The path to the file.
     * @return True if successful, false otherwise.
     */
    fun saveToFile(bytes: ByteArray?, filename: String): Boolean {
        return if (TextUtils.isEmpty(filename)) {
            false
        } else saveToFile(bytes, File(filename))
    }

    /**
     * Saves a byte array to a file.
     *
     * @param bytes The byte array.
     * @param file The file.
     * @return True if successful, false otherwise.
     */
    fun saveToFile(bytes: ByteArray?, file: File): Boolean {
        return saveToFile(bytes, FileOutputStream(file))
    }

    /**
     * Saves a byte array to an output stream.
     *
     * @param bytes The byte array.
     * @param out The output stream.
     * @return True if successful, false otherwise.
     */
    fun saveToFile(bytes: ByteArray?, out: OutputStream): Boolean {
        if (bytes == null) {
            return false
        }

        return try {
            out.write(bytes)
            out.flush()
            out.close()

            true
        } catch (e: IOException) {
            Logger.debug("save bytes to output stream failure: $e")

            false
        }
    }

    /**
     * Calculates the MD5 hash of a directory.
     *
     * @param dir The directory path.
     * @return The MD5 hash string.
     */
    fun md5Dir(dir: String): String {
        return md5Dir(dir, false)
    }

    /**
     * Calculates the MD5 hash of a directory.
     *
     * @param dir The directory file.
     * @return The MD5 hash string.
     */
    fun md5Dir(dir: File): String {
        return md5Dir(dir, false)
    }

    /**
     * Calculates the MD5 hash of a directory.
     *
     * @param dir The directory path.
     * @param hiddenFies Whether to include hidden files.
     * @return The MD5 hash string.
     */
    fun md5Dir(dir: String, hiddenFies: Boolean): String {
        return md5Dir(dir, hiddenFies, false)
    }

    /**
     * Calculates the MD5 hash of a directory.
     *
     * @param dir The directory file.
     * @param hiddenFiles Whether to include hidden files.
     * @return The MD5 hash string.
     */
    fun md5Dir(dir: File, hiddenFiles: Boolean): String {
        return md5Dir(dir, hiddenFiles, false)
    }

    /**
     * Calculates the MD5 hash of a directory.
     *
     * @param dir The directory path.
     * @param hiddenFiles Whether to include hidden files.
     * @param verbose Whether to log verbose output.
     * @return The MD5 hash string.
     */
    fun md5Dir(dir: String, hiddenFiles: Boolean, verbose: Boolean): String {
        return md5Dir(File(dir), hiddenFiles, verbose)
    }

    /**
     * Calculates the MD5 hash of a directory.
     *
     * @param dir The directory file.
     * @param hiddenFiles Whether to include hidden files.
     * @param verbose Whether to log verbose output.
     * @return The MD5 hash string.
     */
    fun md5Dir(dir: File, hiddenFiles: Boolean, verbose: Boolean): String {
        var md5 = ""
        if (!dir.exists() || !dir.isDirectory) {
            return md5
        }

        val files = dir.listFiles() ?: return md5
        Arrays.sort(files)
        var childMd5: String
        for (file in files) {
            if (!hiddenFiles && file.isHidden) {
                continue
            }
            childMd5 = if (file.isDirectory) {
                md5Dir(file, hiddenFiles)
            } else {
                md5File(file)
            }
            if (verbose) {
                Logger.debug("[%s] of (%s, %s)",
                        childMd5,
                        if (file.isDirectory) "D" else "F",
                        file.name)
            }
            md5 += childMd5
        }

        return md5HashOfString(md5)
    }

    /**
     * Calculates the MD5 hash of a file.
     *
     * @param file The file path.
     * @return The MD5 hash string.
     */
    fun md5File(file: String): String {
        return md5File(File(file))
    }

    /**
     * Calculates the MD5 hash of a file.
     *
     * @param file The file.
     * @return The MD5 hash string.
     */
    fun md5File(file: File): String {
        var md5 = ""
        if (!file.exists() || !file.isFile) {
            return md5
        }

        try {
            val input: InputStream = FileInputStream(file)
            val buffer = ByteArray(1024)
            val md5Hash = MessageDigest.getInstance("MD5")
            var numRead = 0
            while (numRead != -1) {
                numRead = input.read(buffer)
                if (numRead > 0) {
                    md5Hash.update(buffer, 0, numRead)
                }
            }
            input.close()
            val md5Bytes = md5Hash.digest()
            val bigInt = BigInteger(1, md5Bytes)
            val output = bigInt.toString(16)
            // Fill to 32 chars
            md5 = String.format("%32s", output).replace(' ', '0')
        } catch (e: Exception) {
            Logger.error("md5 calculation failed on file[%s]: %s",
                    file, e.toString())
        }

        return md5
    }

    private fun md5HashOfString(str: String): String {
        if (TextUtils.isEmpty(str)) {
            return str
        }

        val md5Hash: MessageDigest
        var md5 = ""
        try {
            md5Hash = MessageDigest.getInstance("MD5")
            md5Hash.reset()
            md5Hash.update(str.toByteArray(Charset.forName("UTF8")))
            val md5Bytes = md5Hash.digest()
            val bigInt = BigInteger(1, md5Bytes)
            val output = bigInt.toString(16)
            // Fill to 32 chars
            md5 = String.format("%32s", output).replace(' ', '0')
        } catch (e: Exception) {
            Logger.error("md5 hash failed on string[%s]: %s",
                    str, e.toString())
        }

        return md5
    }

    /**
     * Copies a raw resource to a file.
     *
     * @param context The context.
     * @param rawFile The name of the raw file (without extension).
     * @param dstFile The destination file path.
     * @return True if successful, false otherwise.
     */
    fun copyRawFile(context: Context,
                    rawFile: String,
                    dstFile: String
    ): Boolean {
        val res = context.resources ?: return false
        val resId = res.getIdentifier(
            rawFile,
            "raw", context.packageName
        )

        if (resId <= 0) {
            return false
        }
        val istream = res.openRawResource(resId) ?: return false
        val ostream = FileOutputStream(dstFile)
        return ResourcesUtils.copyToFile(istream, ostream)
    }

    /**
     * Checks if an asset file exists.
     *
     * @param context The context.
     * @param assetFile The asset file path.
     * @return True if exists, false otherwise.
     */
    fun isAssetFileExisted(context: Context, assetFile: String): Boolean {
        if (TextUtils.isEmpty(assetFile)) {
            return false
        }

        val assetManager = context.assets
        var inputStream: InputStream? = null
        try {
            inputStream = assetManager.open(assetFile)
            return true
        } catch (e: IOException) {
        } finally {
            try {
                inputStream?.close()
            } catch (e: IOException) {
                Logger.error("failed to close file [$assetFile]: $e")
            }
        }

        return false
    }

    /**
     * Copies an asset file to a destination file.
     *
     * @param context The context.
     * @param assetFile The asset file path.
     * @param dstFile The destination file path.
     * @return True if successful, false otherwise.
     */
    fun copyAssetFile(
        context: Context,
        assetFile: String,
        dstFile: String
    ): Boolean {
        val asstmgr = context.assets ?: return false

        val istream = try {
            asstmgr.open(assetFile)
        } catch (e: Exception) {
            Logger.error("failed to open asset file: $assetFile")

            null
        }

        val ostream = FileOutputStream(dstFile)

        return ResourcesUtils.copyToFile(istream, ostream)
    }

}