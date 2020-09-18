package com.dailystudio.devbricksx.annotations

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class NonRecyclableListFragment(
        val layout: Int = -1,
        val layoutByName: String = "",
        val fillParent: Boolean = true,
        val dataInterface: String = ""
)