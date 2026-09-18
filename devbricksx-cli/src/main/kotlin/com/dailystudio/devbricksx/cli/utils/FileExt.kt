package com.dailystudio.devbricksx.cli.utils

import java.io.File

private val BINARY_EXTENSIONS = setOf(
    "png", "webp", "jpg", "jpeg", "gif", "ico",
    "jar", "zip", "so", "dylib", "dll", "a",
    "class", "dex", "aar", "apk", "keystore", "jks"
)

fun File.isBinaryFile(): Boolean {
    val ext = this.extension.lowercase()
    return BINARY_EXTENSIONS.contains(ext)
}

fun File.replaceInFile(find: String, replaceWith: String) {
    if (!this.isFile || this.isBinaryFile()) return
    val content = this.readText(Charsets.UTF_8)
    if (content.contains(find)) {
        val updated = content.replace(find, replaceWith)
        this.writeText(updated, Charsets.UTF_8)
    }
}

fun File.replaceInFile(regex: Regex, replaceWith: String) {
    if (!this.isFile || this.isBinaryFile()) return
    val content = this.readText(Charsets.UTF_8)
    if (regex.containsMatchIn(content)) {
        val updated = content.replace(regex, replaceWith)
        this.writeText(updated, Charsets.UTF_8)
    }
}
