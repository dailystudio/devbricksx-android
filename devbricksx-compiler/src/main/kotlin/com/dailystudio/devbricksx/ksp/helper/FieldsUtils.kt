package com.dailystudio.devbricksx.ksp.helper

import com.dailystudio.devbricksx.annotations.data.*
import com.dailystudio.devbricksx.ksp.utils.TypeNameUtils
import com.dailystudio.devbricksx.ksp.utils.hasAnnotation
import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.toTypeName
import org.jetbrains.annotations.Nullable


fun KSPropertyDeclaration.toAbsPrefsFuncType(): Pair<String, Boolean>? {
    return when (type.resolve().declaration.simpleName.getShortName()) {
        "Int" -> Pair("Integer", false)
        "String" -> Pair("String", true)
        "Float" -> Pair("Float", false)
        "Long" -> Pair("Long", false)
        "Boolean" -> Pair("Boolean", false)
        else -> null
    }
}

@OptIn(KspExperimental::class)
fun KSPropertyDeclaration.findFieldAnnotation(): Annotation? {
    val clazzOfAnnotation = when (type.resolve().toTypeName()) {
        INT -> IntegerField::class
        LONG -> LongField::class
        FLOAT -> FloatField::class
        DOUBLE -> DoubleField::class
        BOOLEAN -> BooleanField::class
        STRING -> StringField::class
        else -> return null
    }

    return getAnnotationsByType(clazzOfAnnotation)
        .firstOrNull()
}

fun KSPropertyDeclaration.getDefaultValue(): String {
    val typeOfProp = type.resolve().toTypeName()

    val fieldAnnotation = findFieldAnnotation()
        ?: return TypeNameUtils.defaultValOfType(typeOfProp)

    return when (fieldAnnotation) {
        is IntegerField -> fieldAnnotation.defaultValue.toString()
        is LongField -> fieldAnnotation.defaultValue.toString() + "L"
        is FloatField -> fieldAnnotation.defaultValue.toString() + "f"
        is DoubleField -> fieldAnnotation.defaultValue.toString()
        is BooleanField -> fieldAnnotation.defaultValue.toString()
        is StringField -> "\"" + fieldAnnotation.defaultValue + "\""
        else -> ""
    }
}

fun KSPropertyDeclaration.isNullable(): Boolean {
    return if (type.resolve().isMarkedNullable) {
        true
    } else {
        hasAnnotation(Nullable::class)
    }
}

@OptIn(KspExperimental::class)
fun KSPropertyDeclaration.getAlias(): String? {
    val aliasAnnotation = getAnnotationsByType(FieldAlias::class).firstOrNull()

    return aliasAnnotation?.alias
}