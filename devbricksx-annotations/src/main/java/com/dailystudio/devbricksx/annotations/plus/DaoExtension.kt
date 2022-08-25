package com.dailystudio.devbricksx.annotations.plus

import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
annotation class DaoExtension (
    val entity: KClass<*>
)