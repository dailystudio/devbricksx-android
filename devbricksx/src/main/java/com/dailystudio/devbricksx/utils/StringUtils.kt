package com.dailystudio.devbricksx.utils

import android.content.Context
import java.io.StringReader

/**
 * Utility class for String operations.
 */
object StringUtils {

    /**
     * Reads a string line by line into a list.
     *
     * @param str The input string.
     * @return A list of lines.
     */
    fun linesFromString(str: String): List<String> {
        val reader = StringReader(str)

        val lines = reader.readLines()
        reader.close()

        return lines
    }

    /**
     * Reads a file line by line into a list.
     *
     * @param file The file path.
     * @return A list of lines.
     */
    fun linesFromFile(file: String): List<String> {
        val content = FileUtils.fileToString(file) ?: ""

        return linesFromString(content)
    }

    /**
     * Reads an asset file line by line into a list.
     *
     * @param context The context.
     * @param file The asset file path.
     * @return A list of lines.
     */
    fun linesFromAsset(context: Context,
                       file: String): List<String> {
        val content = FileUtils.assetToString(context, file) ?: ""

        return linesFromString(content)
    }

    /**
     * Reads a raw resource line by line into a list.
     *
     * @param context The context.
     * @param resId The raw resource ID.
     * @return A list of lines.
     */
    fun linesFromRaw(context: Context,
                     resId: Int): List<String> {
        val content = FileUtils.rawToString(context, resId) ?: ""

        return linesFromString(content)
    }

}