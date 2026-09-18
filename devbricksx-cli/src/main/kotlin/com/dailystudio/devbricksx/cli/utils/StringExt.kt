package com.dailystudio.devbricksx.cli.utils

fun String.squeezeAndCapitalize(): String {
    val cleaned = this.replace(Regex("[^a-zA-Z0-9_\\-\\s]"), " ")
    val words = cleaned.split(Regex("[_\\-\\s]+")).filter { it.isNotEmpty() }
    if (words.isEmpty()) return "App"
    return words.joinToString("") { word ->
        word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }
}

fun parseThemeColor(color: String): String {
    val clean = color.trim().removePrefix("#")
    val rgb = when (clean.length) {
        3 -> "${clean[0]}${clean[0]}${clean[1]}${clean[1]}${clean[2]}${clean[2]}"
        6 -> clean
        8 -> clean.substring(2) // strip alpha if provided like FF008577
        else -> clean
    }
    return "0xff$rgb"
}
