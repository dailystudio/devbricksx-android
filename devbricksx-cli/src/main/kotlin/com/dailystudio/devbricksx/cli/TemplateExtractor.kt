package com.dailystudio.devbricksx.cli

import java.io.File
import java.io.InputStream
import java.util.zip.ZipInputStream

object TemplateExtractor {

    private const val TEMPLATE_ZIP_RESOURCE = "/template.zip"

    fun extractTemplate(destinationDir: File, localTemplateDir: File? = null) {
        destinationDir.mkdirs()

        // 1. Try classpath resource
        val resourceStream: InputStream? = TemplateExtractor::class.java.getResourceAsStream(TEMPLATE_ZIP_RESOURCE)
        if (resourceStream != null) {
            extractZipStream(resourceStream, destinationDir)
            return
        }

        // 2. Fallback to local template dir if provided or exists
        val localDir = localTemplateDir ?: File("template")
        if (localDir.isDirectory) {
            copyDirectory(localDir, destinationDir)
            return
        }

        throw IllegalStateException(
            "Could not locate bundled template resource ($TEMPLATE_ZIP_RESOURCE) or local template directory (${localDir.absolutePath})"
        )
    }

    private fun extractZipStream(inputStream: InputStream, destinationDir: File) {
        ZipInputStream(inputStream).use { zis ->
            var entry = zis.nextEntry
            while (entry != null) {
                val targetFile = File(destinationDir, entry.name)
                // Protect against Zip Slip vulnerability
                val canonicalDest = destinationDir.canonicalPath
                val canonicalTarget = targetFile.canonicalPath
                if (!canonicalTarget.startsWith(canonicalDest + File.separator) && canonicalTarget != canonicalDest) {
                    throw SecurityException("Zip entry is outside of target directory: ${entry.name}")
                }

                if (entry.isDirectory) {
                    targetFile.mkdirs()
                } else {
                    targetFile.parentFile?.mkdirs()
                    targetFile.outputStream().use { fos ->
                        zis.copyTo(fos)
                    }
                    if (targetFile.name == "gradlew") {
                        targetFile.setExecutable(true, false)
                    }
                }
                zis.closeEntry()
                entry = zis.nextEntry
            }
        }
    }

    private fun copyDirectory(source: File, target: File) {
        val excludedNames = setOf(".gradle", "build", ".cxx", ".idea", "local.properties")
        source.walkTopDown().forEach { file ->
            if (excludedNames.any { file.path.contains("${File.separator}$it") }) {
                return@forEach
            }
            val relativePath = file.relativeTo(source).path
            val targetFile = File(target, relativePath)
            if (file.isDirectory) {
                targetFile.mkdirs()
            } else {
                targetFile.parentFile?.mkdirs()
                file.copyTo(targetFile, overwrite = true)
                if (file.name == "gradlew") {
                    targetFile.setExecutable(true, false)
                }
            }
        }
    }
}
