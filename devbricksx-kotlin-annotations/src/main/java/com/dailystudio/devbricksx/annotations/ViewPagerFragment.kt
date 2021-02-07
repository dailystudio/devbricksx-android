package com.dailystudio.devbricksx.annotations

import kotlin.reflect.KClass

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class ViewPagerFragment(
        val useFragment: Boolean = false,
        val layout: Int = -1,
        val layoutByName: String = "",
        val offscreenPageLimit: Int = 1,
        val dataSource: DataSource = DataSource.LiveData,
        val usingPaging3: Boolean = false,
        val superClass: KClass<*> = Void::class,
)