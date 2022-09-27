package com.dailystudio.devbricksx.annotations

import kotlin.reflect.KClass

@Deprecated("NonRecyclableListFragment is deprecated by NonRecyclableListFragment in package com.dailystudio.devbricksx.annotations.fragment")
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class NonRecyclableListFragment(
        val layout: Int = -1,
        val layoutByName: String = "",
        val fillParent: Boolean = true,
        val dataSource: DataSource = DataSource.LiveData,
        val superClass: KClass<*> = Void::class,
)