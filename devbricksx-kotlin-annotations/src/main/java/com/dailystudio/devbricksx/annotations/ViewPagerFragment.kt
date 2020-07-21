package com.dailystudio.devbricksx.annotations

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class ViewPagerFragment(
        val useFragment: Boolean = false,
        val layout: Int = -1,
        val layoutByName: String = "",
        val offscreenPageLimit: Int = 1
)