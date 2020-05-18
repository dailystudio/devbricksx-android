package com.dailystudio.devbricksx.annotations

import kotlin.reflect.KClass

enum class Ordering {
    Ascending,
    Descending
}

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class InMemoryManager(
        val key: KClass<*>,
        val ordering: Ordering = Ordering.Ascending
)