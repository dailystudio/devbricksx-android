package com.dailystudio.devbricksx.utils

import android.content.Context
import com.dailystudio.devbricksx.development.Logger
import java.io.*

object ResourcesUtils {

    private const val COPY_BUFFER_SIZE = 1024 * 16

    fun copyRawToFile(context: Context, resId: Int, filename: String): Boolean {
        return copyRawToFile(context, resId, null, filename)
    }

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