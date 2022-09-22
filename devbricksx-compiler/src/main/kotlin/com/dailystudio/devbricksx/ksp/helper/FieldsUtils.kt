package com.dailystudio.devbricksx.ksp.helper

import com.dailystudio.devbricksx.annotations.data.*
import com.dailystudio.devbricksx.ksp.utils.TypeNameUtils
import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.toTypeName


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
        is LongField -> fieldAnnotation.defaultValue.toString() + "l"
        is FloatField -> fieldAnnotation.defaultValue.toString() + "f"
        is DoubleField -> fieldAnnotation.defaultValue.toString()
        is BooleanField -> fieldAnnotation.defaultValue.toString()
        is StringField -> fieldAnnotation.defaultValue
        else -> ""
    }
}

fun KSPropertyDeclaration.isNullable(): Boolean {
    if (type.resolve().isMarkedNullable) {
        return true
    }

    val fieldAnnotation = findFieldAnnotation()
        ?: false

    return when (fieldAnnotation) {
        is IntegerField -> fieldAnnotation.nullable
        is LongField -> fieldAnnotation.nullable
        is FloatField -> fieldAnnotation.nullable
        is DoubleField -> fieldAnnotation.nullable
        is BooleanField -> fieldAnnotation.nullable
        is StringField -> fieldAnnotation.nullable
        else -> false
    }
}
fun KSPropertyDeclaration.getAlias(): String? {
    val fieldAnnotation = findFieldAnnotation()
        ?: return null

    return when (fieldAnnotation) {
        is IntegerField -> fieldAnnotation.alias
        is LongField -> fieldAnnotation.alias
        is FloatField -> fieldAnnotation.alias
        is DoubleField -> fieldAnnotation.alias
        is BooleanField -> fieldAnnotation.alias
        is StringField -> fieldAnnotation.alias
        else -> null
    }
}