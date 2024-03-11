package com.dailystudio.devbricksx.annotations.fragment

import kotlin.reflect.KClass

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS)
annotation class ListFragment(
        val layout: Int = -1,
        val layoutByName: String = "",
        val fillParent: Boolean = true,
        val dataSource: DataSource = DataSource.LiveData,
        val superClass: KClass<*> = Unit::class,
        val pageSize: Int = 20,
        val gridLayout: Boolean = false,
        val columns: Int = 2,
        val adapter: KClass<*> = Unit::class,
        val dataCollectingRepeatOn: RepeatOnLifecycle = RepeatOnLifecycle.RESUMED
)