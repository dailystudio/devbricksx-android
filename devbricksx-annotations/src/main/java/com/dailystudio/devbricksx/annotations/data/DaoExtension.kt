package com.dailystudio.devbricksx.annotations.data

import kotlin.reflect.KClass

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
annotation class DaoExtension (
    val entity: KClass<*>
)