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

fun String.kotlinGetterName(): String {
    if (!startsWith("get")) {
        return this
    }

    return this.removePrefix("get").lowerCamelCaseName()
}

fun String.kebabCaseName(): String {
    if (isBlank()) {
        return this
    }

    val builder = StringBuilder()

    for (i in this.indices) {
        val c = this[i]

        if (Character.isAlphabetic(c.toInt())
                && Character.isUpperCase(c)
                && i != 0) {

            builder.append("-")
        }

        builder.append(c.toLowerCase())
    }

    return builder.toString()
}

fun String.underlineCaseName(): String {
    if (isBlank()) {
        return this
    }

    val builder = StringBuilder()

    for (i in this.indices) {
        val c = this[i]

        if (Character.isAlphabetic(c.toInt())
                && Character.isUpperCase(c)
                && i != 0) {

            builder.append("_")
        }

        builder.append(c.toLowerCase())
    }

    return builder.toString()
}
