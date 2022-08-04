package com.dailystudio.devbricksx.ksp.utils

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import kotlin.reflect.KClass

fun KSClassDeclaration.getAnnotation(
    clazz: KClass<out Annotation>,
    resolver: Resolver
): KSAnnotation? {
    var found: KSAnnotation? = null
    for (annotation in this.annotations) {
        if (annotation.annotationType.resolve() == clazz.asAnnotationType(resolver)) {
            found = annotation
            break
        }
    }

    return found
}

fun KClass<out Annotation>.asAnnotationType(resolver: Resolver): KSType? {
    val clazzName = this.qualifiedName ?: return null
    val ksName = resolver.getKSNameFromString(clazzName)

    return resolver.getClassDeclarationByName(ksName)?.asType(emptyList())
}
