package com.dailystudio.devbricksx.annotations

import kotlin.reflect.KClass

@Deprecated("Ordering is deprecated by Ordering in package com.dailystudio.devbricksx.annotations.data")
enum class Ordering {
    Ascending,
    Descending
}

@Deprecated("InMemoryManager is deprecated by InMemoryCompanion in package com.dailystudio.devbricksx.annotations.data")
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class InMemoryManager(
        val key: KClass<*>,
        val ordering: Ordering = Ordering.Ascending
)