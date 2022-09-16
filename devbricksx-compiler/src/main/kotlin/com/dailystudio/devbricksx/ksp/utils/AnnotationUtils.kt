package com.dailystudio.devbricksx.ksp.utils

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.ksp.toClassName
import kotlin.reflect.KClass

fun KSDeclaration.getKSAnnotation(
    annotationClass: KClass<out Annotation>,
    resolver: Resolver
): KSAnnotation? {
    var found: KSAnnotation? = null
    for (annotation in this.annotations) {
        if (annotation.annotationType.resolve() == annotationClass.asAnnotationType(resolver)) {
            found = annotation
            break
        }
    }

    return found
}

inline fun <reified R> KSAnnotation.findArgument(argName: String): R {
    return arguments.first {
        it.name?.getShortName() == argName
    }.value as R
}

fun KSClassDeclaration.collectTypesInAnnotationArguments(
    annotationClass: KClass<out Annotation>,
    nameOfArgument: String,
    resolver: Resolver): Set<KSType> {
    val companion = getKSAnnotation(annotationClass, resolver)

    val converters = mutableSetOf<KSType>()

    if (superClassType() != TypeNameUtils.typeOfKotlinAny(resolver)) {
        val convertersInSuperType =
            superClassType().collectTypesInAnnotationArguments(annotationClass,
                nameOfArgument, resolver)

        if (convertersInSuperType.isNotEmpty()) {
            converters.addAll(convertersInSuperType)
        }
    }

    companion?.findArgument<ArrayList<KSType>>(nameOfArgument)?.let { it ->
        converters.addAll(it)
    }

    return converters
}

fun KSClassDeclaration.packageName(): String {
    return this.toClassName().packageName
}

fun KSClassDeclaration.typeName(): String {
    return this.toClassName().simpleName
}

fun KSClassDeclaration.superClassType(): KSClassDeclaration {
    return superTypes
        .map { it.resolve().declaration }
        .filterIsInstance<KSClassDeclaration>()
//        .filter { it.classKind == ClassKind.CLASS }
        .first()
}

fun KClass<out Annotation>.asAnnotationType(resolver: Resolver): KSType? {
    val clazzName = this.qualifiedName ?: return null
    val ksName = resolver.getKSNameFromString(clazzName)

    return resolver.getClassDeclarationByName(ksName)?.asType(emptyList())
}
