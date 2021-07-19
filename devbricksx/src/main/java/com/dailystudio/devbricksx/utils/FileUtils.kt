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

object FileUtils {

    private const val NO_MEDIA_TAG_FILE = ".nomedia"

    fun checkOrCreateNoMediaDirectory(directory: String): Boolean {
        return checkOrCreateNoMediaDirectory(File(directory))
    }

    fun checkOrCreateNoMediaDirectory(directory: File): Boolean {
        return checkOrCreateDirectory(directory, true)
    }

    fun checkOrCreateDirectory(directory: String): Boolean {
        return checkOrCreateDirectory(File(directory))
    }

    fun checkOrCreateDirectory(directory: File): Boolean {
        return checkOrCreateDirectory(directory, false)
    }

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

    fun checkOrCreateNoMediaTagInDirectory(directory: String): Boolean {
        return checkOrCreateNoMediaTagInDirectory(File(directory))
    }

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

    fun isFileExisted(filename: String): Boolean {
        return File(filename).exists()
    }

    fun detectFileEncoding(file: String): String? {
        return detectFileEncoding(File(file))
    }

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

    fun detectFileEncoding(file: File): String? {
        var fs: FileInputStream = try {
            FileInputStream(file)
        } catch (e: FileNotFoundException) {
            Logger.warn("get encoding failure: $e")
            return null
        }

        return detectFileEncoding(fs)
    }

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

    fun fileToString(file: String): String? {
        var fs: FileInputStream = try {
            FileInputStream(file)
        } catch (e: FileNotFoundException) {
            Logger.warn("get encoding failure: $e")
            return null
        }

        return fileToString(fs, detectFileEncoding(file))
    }

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

    fun getBaseName(filename: String): String {
        return File(filename).name
    }

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

    fun getFileExtension(filename: String): String {
        return getFileExtension(filename, "")
    }

    fun getFileExtension(filename: String, defExt: String): String {
        if (filename.isNotEmpty()) {
            val i = filename.lastIndexOf('.')
            if (i > -1 && i < filename.length - 1) {
                return filename.substring(i + 1)
            }
        }

        return defExt
    }

    fun stringToFile(file: String,
                     fileContent: String?) {
        stringToFile(file, fileContent, false)
    }

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

    fun saveToFile(bytes: ByteArray?, filename: String): Boolean {
        return if (TextUtils.isEmpty(filename)) {
            false
        } else saveToFile(bytes, File(filename))
    }

    fun saveToFile(bytes: ByteArray?, file: File): Boolean {
        return saveToFile(bytes, FileOutputStream(file))
    }

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

    fun md5Dir(dir: String): String {
        return md5Dir(dir, false)
    }

    fun md5Dir(dir: File): String {
        return md5Dir(dir, false)
    }

    fun md5Dir(dir: String, hiddenFies: Boolean): String {
        return md5Dir(dir, hiddenFies, false)
    }

    fun md5Dir(dir: File, hiddenFiles: Boolean): String {
        return md5Dir(dir, hiddenFiles, false)
    }

    fun md5Dir(dir: String, hiddenFiles: Boolean, verbose: Boolean): String {
        return md5Dir(File(dir), hiddenFiles, verbose)
    }

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

    fun md5File(file: String): String {
        return md5File(File(file))
    }

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