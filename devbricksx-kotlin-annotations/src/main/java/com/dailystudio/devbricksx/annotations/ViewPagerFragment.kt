package com.dailystudio.devbricksx.annotations

import kotlin.reflect.KClass

@Deprecated("ViewPagerFragment is deprecated by ViewPagerFragment in package com.dailystudio.devbricksx.annotations.fragment")
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class ViewPagerFragment(
        val layout: Int = -1,
        val layoutByName: String = "",
        val fillParent: Boolean = true,
        val dataSource: DataSource = DataSource.LiveData,
        val superClass: KClass<*> = Void::class,
        val useFragment: Boolean = false,
        val offscreenPageLimit: Int = 1,
)