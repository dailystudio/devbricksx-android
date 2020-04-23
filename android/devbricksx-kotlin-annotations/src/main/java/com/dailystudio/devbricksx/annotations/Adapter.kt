package com.dailystudio.devbricksx.annotations

import kotlin.reflect.KClass

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class Adapter(
        val viewHolder: KClass<*>,
        val paged: Boolean = false
)