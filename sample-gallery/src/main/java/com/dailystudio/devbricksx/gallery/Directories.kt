package com.dailystudio.devbricksx.gallery

import android.os.Environment
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.utils.FileUtils
import java.io.File

object Directories {

    private const val SUB_DIR = "Gallery"
    private const val IMAGE_EXT = ".jpg"

    fun getImageDownloadPath(imageId: String): File {
        val downloadRoot = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        Logger.debug("download root: $downloadRoot")

        val downloadDir = File(downloadRoot, SUB_DIR)
        Logger.debug("download dir: $downloadDir")

        FileUtils.checkOrCreateDirectory(downloadDir)

        val fileName = imageId + IMAGE_EXT

        return File(downloadDir, fileName)
    }

}