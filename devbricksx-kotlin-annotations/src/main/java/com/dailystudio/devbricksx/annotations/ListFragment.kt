package com.dailystudio.devbricksx.annotations

import kotlin.reflect.KClass

enum class DataSource {
    Flow,
    LiveData,
}

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class ListFragment(
        val layout: Int = -1,
        val layoutByName: String = "",
        val gridLayout: Boolean = false,
        val columns: Int = 2,
        val fillParent: Boolean = true,
        val dataSource: DataSource = DataSource.LiveData,
        val usingPaging3: Boolean = false,
        val superClass: KClass<*> = Void::class,
        val dataInterface: String = ""
)