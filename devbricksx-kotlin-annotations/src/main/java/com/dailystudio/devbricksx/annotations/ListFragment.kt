package com.dailystudio.devbricksx.annotations

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class ListFragment(
        val layout: Int = -1,
        val layoutByName: String = "",
        val gridLayout: Boolean = false,
        val columns: Int = 2,
        val dataInterface: String = ""
)