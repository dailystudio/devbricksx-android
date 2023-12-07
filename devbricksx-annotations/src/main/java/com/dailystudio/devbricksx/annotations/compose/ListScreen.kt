package com.dailystudio.devbricksx.annotations.compose

import com.dailystudio.devbricksx.annotations.fragment.DataSource
import kotlin.reflect.KClass

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
annotation class ListScreen(
    val dataSource: DataSource = DataSource.LiveData,
    val paged: Boolean = false,
    val pageSize: Int = 20,
    val gridLayout: Boolean = false,
    val columns: Int = 2,
    val selectable: Boolean = false,
)

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class ItemContent(
    val entity: KClass<*>
) {
}