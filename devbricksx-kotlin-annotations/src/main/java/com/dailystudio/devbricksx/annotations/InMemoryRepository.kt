package com.dailystudio.devbricksx.annotations

import kotlin.reflect.KClass

@Deprecated("InMemoryRepository is deprecated by InMemoryCompanion in package com.dailystudio.devbricksx.annotations.data")
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class InMemoryRepository(
        val key: KClass<*>,
        val pageSize: Int = 10)