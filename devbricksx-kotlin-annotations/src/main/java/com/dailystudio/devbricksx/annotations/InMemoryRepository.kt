package com.dailystudio.devbricksx.annotations

import kotlin.reflect.KClass

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class InMemoryRepository(
        val key: KClass<*>,
        val pageSize: Int = 10)