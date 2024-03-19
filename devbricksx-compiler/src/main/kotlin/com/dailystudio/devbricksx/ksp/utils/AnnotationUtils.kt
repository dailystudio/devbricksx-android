package com.dailystudio.devbricksx.ksp.utils

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.ksp.toClassName
import kotlin.reflect.KClass

fun <T: Annotation> KSDeclaration.hasAnnotation(
    annotationClass: KClass<T>,
    resolver: Resolver? = null,
): Boolean = (getAnnotation(annotationClass, resolver) != null)

/*
@OptIn(KspExperimental::class)
fun <T: Annotation> KSDeclaration.getAnnotation(
    annotationClass: KClass<T>,
    resolver: Resolver? = null,
): T? {
    if (resolver == null) {
        return getAnnotationsByType(annotationClass).firstOrNull()
    } else {
        val annotation = getAnnotationsByType(annotationClass).firstOrNull()
        if (annotation != null || this !is KSClassDeclaration) {
            return annotation
        }

        return toShadowClass(resolver)?.getAnnotation(annotationClass)
    }
}
*/

fun <T: Annotation> KSDeclaration.getAnnotation(
    annotationClass: KClass<T>,
    resolver: Resolver? = null,
): T? {
    return getAnnotation(annotationClass, 0, resolver)
}

fun <T: Annotation> KSDeclaration.getAnnotation(
    annotationClass: KClass<T>,
    position: Int = 0,
    resolver: Resolver? = null,
): T? {
    val annotations =  getAnnotations(annotationClass, resolver)

    return if (position < annotations.size) {
        annotations[position]
    } else {
        if (annotations.isNotEmpty()) {
            return annotations[0]
        } else {
            null
        }
    }
}

@OptIn(KspExperimental::class)
fun <T: Annotation> KSDeclaration.getAnnotations(
    annotationClass: KClass<T>,
    resolver: Resolver? = null,
): List<T> {
    if (resolver == null) {
        return getAnnotationsByType(annotationClass).toList()
    } else {
        val list = getAnnotationsByType(annotationClass).toList()
        if (list.isNotEmpty() || this !is KSClassDeclaration) {
            return list
        }

        return toShadowClass(resolver)?.getAnnotations(annotationClass) ?: emptyList()
    }
}

/*fun <T: Annotation> KSDeclaration.getKSAnnotation(
    annotationClass: KClass<T>,
    resolver: Resolver
): KSAnnotation? {
    var found: KSAnnotation? = null
    for (annotation in this.annotations) {
        if (annotation.annotationType.resolve() == annotationClass.asAnnotationType(resolver)) {
            found = annotation
            break
        }
    }

    if (found != null || this !is KSClassDeclaration) {
        return found
    }

    return toShadowClass(resolver)?.getKSAnnotation(
        annotationClass, resolver)
}*/

fun <T: Annotation> KSDeclaration.getKSAnnotation(
    annotationClass: KClass<T>,
    resolver: Resolver
): KSAnnotation? {
    return getKSAnnotation(annotationClass, 0, resolver)
}

fun <T: Annotation> KSDeclaration.getKSAnnotation(
    annotationClass: KClass<T>,
    position: Int = 0,
    resolver: Resolver
): KSAnnotation? {
    val annotations = getKSAnnotations(annotationClass, resolver)

    return if (position < annotations.size) {
        annotations[position]
    } else {
        if (annotations.isNotEmpty()) {
            return annotations[0]
        } else {
            null
        }
    }
}

fun <T: Annotation> KSDeclaration.getKSAnnotations(
    annotationClass: KClass<T>,
    resolver: Resolver
): List<KSAnnotation> {
    val found = mutableListOf<KSAnnotation>()
    for (annotation in this.annotations) {
        if (annotation.annotationType.resolve() == annotationClass.asAnnotationType(resolver)) {
            found.add(annotation)
        }
    }

    if (found.isNotEmpty() || this !is KSClassDeclaration) {
        return found
    }

    return toShadowClass(resolver)?.getKSAnnotations(
        annotationClass, resolver) ?: emptyList()
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

fun KSClassDeclaration.collectObjectsInAnnotationArguments(
    annotationClass: KClass<out Annotation>,
    nameOfArgument: String,
    resolver: Resolver): Set<Any> {
    val companion = getKSAnnotation(annotationClass, resolver)

    val objects = mutableSetOf<Any>()

    if (superClassType() != TypeNameUtils.typeOfKotlinAny(resolver)) {
        val convertersInSuperType =
            superClassType().collectObjectsInAnnotationArguments(annotationClass,
                nameOfArgument, resolver)

        if (convertersInSuperType.isNotEmpty()) {
            objects.addAll(convertersInSuperType)
        }
    }

    companion?.findArgument<ArrayList<Any>>(nameOfArgument)?.let { it ->
        objects.addAll(it)
    }

    return objects
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
