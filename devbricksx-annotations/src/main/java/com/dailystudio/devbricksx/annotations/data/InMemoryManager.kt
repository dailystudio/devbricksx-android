package com.dailystudio.devbricksx.annotations.data

import kotlin.reflect.KClass

enum class Ordering {
    Ascending,
    Descending
}

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class InMemoryCompanion(
        val key: KClass<*>,
        val ordering: Ordering = Ordering.Ascending
)