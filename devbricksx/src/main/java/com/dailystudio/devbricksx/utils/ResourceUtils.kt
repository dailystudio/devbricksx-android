package com.dailystudio.devbricksx.utils

import android.content.Context
import com.dailystudio.devbricksx.development.Logger
import java.io.*

/**
 * Utility class for low-level resource operations, like copying raw resources to files.
 */
object ResourcesUtils {

    private const val COPY_BUFFER_SIZE = 1024 * 16

    /**
     * Copies a raw resource to a file in the application's internal storage.
     *
     * @param context The context.
     * @param resId The raw resource ID.
     * @param filename The destination filename.
     * @return True if successful, false otherwise.
     */
    fun copyRawToFile(context: Context, resId: Int, filename: String): Boolean {
        return copyRawToFile(context, resId, null, filename)
    }

    /**
     * Copies a raw resource to a file in a destination context's internal storage.
     *
     * @param srcContext The source context containing the resource.
     * @param resId The raw resource ID.
     * @param dstContext The destination context (can be same as source).
     * @param fname The destination filename.
     * @return True if successful, false otherwise.
     */
    fun copyRawToFile(
        srcContext: Context,
        resId: Int,
        dstContext: Context?,
        fname: String
    ): Boolean {
        val dstContext = dstContext ?: srcContext

        val res = srcContext.resources ?: return false

        val input = res.openRawResource(resId)
        val output: FileOutputStream? = try {
            dstContext.openFileOutput(fname, Context.MODE_PRIVATE)
        } catch (e: FileNotFoundException) {
            Logger.error("failed to open file: $fname")
            null
        }

        return if (output == null) {
            false
        } else copyToFile(input, output)
    }

    /**
     * Copies data from an InputStream to a FileOutputStream.
     *
     * Closes both streams after operation.
     *
     * @param input The input stream.
     * @param output The output stream.
     * @return True if successful, false otherwise.
     */
    fun copyToFile(input: InputStream?, output: FileOutputStream?): Boolean {
        if (input == null || output == null) {
            return false
        }

        val istream = BufferedInputStream(input)
        val ostream = BufferedOutputStream(output)
        val buffer = ByteArray(COPY_BUFFER_SIZE)
        var count: Int
        try {
            while (istream.read(buffer).also { count = it } > 0) {
                ostream.write(buffer, 0, count)
            }
            ostream.flush()
        } catch (e: IOException) {
            Logger.error("fail to write to file: $e")
            return false
        }

        try {
            istream.close()
            ostream.close()
        } catch (e: IOException) {
            Logger.error("fail to close files: $e")

            return false
        }

        return true
    }
}