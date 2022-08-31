package com.dailystudio.devbricksx.annotations.data

enum class Ordering {
    Ascending,
    Descending
}

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class InMemoryCompanion(
        val ordering: Ordering = Ordering.Ascending,
        val pageSize: Int = DEFAULT_PAGE_SIZE) {
    companion object {
        const val DEFAULT_PAGE_SIZE = InternalConstants.DEFAULT_PAGE_SIZE
    }
}