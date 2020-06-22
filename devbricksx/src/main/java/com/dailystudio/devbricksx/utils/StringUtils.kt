package com.dailystudio.devbricksx.utils

import android.content.Context
import java.io.StringReader

object StringUtils {

    fun linesFromString(str: String): List<String> {
        val reader = StringReader(str)

        val lines = reader.readLines()
        reader.close()

        return lines
    }

    fun linesFromFile(file: String): List<String> {
        val content = FileUtils.fileToString(file) ?: ""

        return linesFromString(content)
    }

    fun linesFromAsset(context: Context,
                       file: String): List<String> {
        val content = FileUtils.assetToString(context, file) ?: ""

        return linesFromString(content)
    }

    fun linesFromRaw(context: Context,
                     resId: Int): List<String> {
        val content = FileUtils.rawToString(context, resId) ?: ""

        return linesFromString(content)
    }

}