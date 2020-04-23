package com.dailystudio.devbricksx.compiler.kotlin.utils

import java.lang.StringBuilder

fun String.lowerCamelCaseName(): String {
    if (isBlank()) {
        return this
    }

    val builder = StringBuilder(this)

    for (i in this.indices) {
        if (Character.isAlphabetic(this[i].toInt())) {
            builder.setCharAt(i, Character.toLowerCase(this[i]))
            break
        }
    }

    return builder.toString()
}