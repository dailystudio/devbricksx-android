package com.dailystudio.devbricksx.annotations.fragment

import kotlin.reflect.KClass

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class ViewPagerFragment(
        val layout: Int = -1,
        val layoutByName: String = "",
        val fillParent: Boolean = true,
        val dataSource: DataSource = DataSource.LiveData,
        val superClass: KClass<*> = Unit::class,
        val useFragment: Boolean = false,
        val offscreenPageLimit: Int = 1,
)