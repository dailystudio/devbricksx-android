package com.dailystudio.devbricksx.ksp.utils

import com.dailystudio.devbricksx.ksp.Constants
import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration

fun KSClassDeclaration.fromShadowClass(resolver: Resolver): KSClassDeclaration? {
    val packageName = packageName.asString()
    val name = simpleName.asString()

    val shadowName = if (name.startsWith(Constants.SHADOW_CLASS_PREFIX)) {
        name.replaceFirst(Constants.SHADOW_CLASS_PREFIX, "")
    } else {
        name
    }

    return resolver.getClassDeclarationByName(
        "$packageName.$shadowName"
    )
}

fun KSClassDeclaration.toShadowClass(resolver: Resolver): KSClassDeclaration? {
    val packageName = packageName.asString()
    val name = simpleName.asString()
    val shadowSymbolName = "${packageName}.__${name}"

    return resolver.getClassDeclarationByName(
        shadowSymbolName
    )
}

fun Sequence<KSClassDeclaration>.mapToShadowClass(resolver: Resolver): Sequence<KSClassDeclaration> {
    return map {
        val originalClass = it.fromShadowClass(resolver)

        if (originalClass != null) {
            val old = "${it.qualifiedName}"
            val new = "${originalClass.qualifiedName}"

            println("mapping to shadow class: [${old}] -> [${new}]")

            originalClass
        } else {
            it
        }
    }
}