package com.dailystudio.devbricksx.annotations

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class Fragment(
        val layout: Int = -1,
        val dataInterface: String = ""
)