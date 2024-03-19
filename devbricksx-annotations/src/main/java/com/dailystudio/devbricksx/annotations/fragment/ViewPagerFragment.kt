package com.dailystudio.devbricksx.annotations.fragment

import kotlin.reflect.KClass

@Repeatable
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS)
annotation class ViewPagerFragment(
        val name: String = "",
        val layout: Int = -1,
        val layoutByName: String = "",
        val fillParent: Boolean = true,
        val dataSource: DataSource = DataSource.LiveData,
        val superClass: KClass<*> = Unit::class,
        val adapter: KClass<*> = Unit::class,
        val useFragment: Boolean = false,
        val offscreenPageLimit: Int = 1,
        val dataCollectingRepeatOn: RepeatOnLifecycle = RepeatOnLifecycle.RESUMED
)