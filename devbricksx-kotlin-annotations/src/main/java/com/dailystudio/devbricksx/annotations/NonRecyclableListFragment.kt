package com.dailystudio.devbricksx.annotations

import kotlin.reflect.KClass

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class NonRecyclableListFragment(
        val layout: Int = -1,
        val layoutByName: String = "",
        val fillParent: Boolean = true,
        val dataSource: DataSource = DataSource.LiveData,
        val superClass: KClass<*> = Void::class,
)