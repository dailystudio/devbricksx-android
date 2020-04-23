package com.dailystudio.devbricksx.annotations

import kotlin.reflect.KClass

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class Adapter(
        val layout: Int,
        val viewHolder: KClass<*>,
        val paged: Boolean = false
)