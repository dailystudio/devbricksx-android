package com.dailystudio.devbricksx.ksp.helper

import java.util.*


fun String.capitalizeName(): String {
    return if (isEmpty()) {
        this
    } else {
        replaceFirstChar {
            if (it.isLowerCase()) {
                it.titlecase(Locale.getDefault())
            } else {
                it.toString()
            }
        }
    }
}

fun String.lowerCamelCaseName(): String {
    if (isBlank()) {
        return this
    }

    val builder = StringBuilder(this)

    for (i in this.indices) {
        if (Character.isAlphabetic(this[i].code)) {
            builder.setCharAt(i, Character.toLowerCase(this[i]))
            break
        }
    }

    return builder.toString()
}

fun String.toVariableOrParamName(): String {
    return this@toVariableOrParamName.lowerCamelCaseName()
}

fun String.toVariableOrParamNameOfCollection(): String {
    return buildString {
        append(this@toVariableOrParamNameOfCollection.toVariableOrParamName())
        append('s')
    }
}

fun String.underscoreLowerCamelCaseName(): String {
    return buildString {
        append("_")
        append(this@underscoreLowerCamelCaseName.lowerCamelCaseName())
    }
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

        if (Character.isAlphabetic(c.code)
                && Character.isUpperCase(c)
                && i != 0) {

            builder.append("_")
        }

        builder.append(c.lowercase())
    }

    return builder.toString()
}
