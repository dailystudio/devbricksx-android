package com.dailystudio.devbricksx.annotations

import kotlin.reflect.KClass

@Deprecated("DataSource is deprecated by DataSource in package com.dailystudio.devbricksx.annotations.fragment")
enum class DataSource {
    Flow,
    LiveData,
}

@Deprecated("ListFragment is deprecated by ListFragment in package com.dailystudio.devbricksx.annotations.fragment")
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class ListFragment(
        val layout: Int = -1,
        val layoutByName: String = "",
        val fillParent: Boolean = true,
        val dataSource: DataSource = DataSource.LiveData,
        val superClass: KClass<*> = Void::class,
        val pageSize: Int = 20,
        val gridLayout: Boolean = false,
        val columns: Int = 2,
)