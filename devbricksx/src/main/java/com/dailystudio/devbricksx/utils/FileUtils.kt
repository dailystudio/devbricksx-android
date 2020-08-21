package com.dailystudio.devbricksx.utils

import android.content.Context
import android.content.res.Resources.NotFoundException
import com.dailystudio.devbricksx.development.Logger
import org.mozilla.universalchardet.UniversalDetector
import java.io.*

object FileUtils {

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

}