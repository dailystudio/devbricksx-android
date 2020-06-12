package com.dailystudio.devbricksx.annotations

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class ViewPagerFragment(
        val layout: Int = -1,
        val offscreenPageLimit: Int = 1,
        val dataInterface: String = ""
)